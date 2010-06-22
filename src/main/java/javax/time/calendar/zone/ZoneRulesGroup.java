/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar.zone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import javax.time.CalendricalException;
import javax.time.calendar.OffsetDateTime;

/**
 * A group of time-zone rules wrapping a provider of multiple versions of the data.
 * <p>
 * Zone rule data is provided by organizations or groups.
 * To manage this data each group is given a unique ID.
 * One group is provided as standard - 'TZDB' - and more may be added.
 * <p>
 * The 'TZDB' group represents that data provided by the
 * <a href="http://www.twinsun.com/tz/tz-link.htm">time-zone database</a>
 * as used in older versions of Java and many operating systems.
 * <p>
 * Other groups of zone rules can be developed and registered.
 * Group IDs should be reverse domain names as with package names unless explicitly
 * approved by the JSR-310 expert group.
 * <p>
 * Each group will provide versioned sets of data for a number of geographical regions.
 * Instances of {@code ZoneRulesGroup} manage the data via region and version IDs.
 * <p>
 * ZoneRulesGroup is thread-safe and immutable.
 * <p>
 * Rules may be registered at any time during the life of the application.
 * No rules may be removed however, thus any rules obtained will remain valid.
 * <p>
 * The static methods of ZoneRulesGroup wrap a thread-safe map of groups.
 * New groups and providers may safely be added during the lifetime of the application.
 * To maintain data integrity, providers may not be removed during the lifetime of the application.
 *
 * @author Stephen Colebourne
 */
public final class ZoneRulesGroup {

    /**
     * Group ID pattern.
     */
    private static final Pattern PATTERN_GROUP = Pattern.compile("[A-Za-z0-9._-]+");
    /**
     * Version ID pattern.
     */
    private static final Pattern PATTERN_VERSION = Pattern.compile("[A-Za-z0-9._-]+");
    /**
     * The zone IDs.
     * Should not be empty.
     */
    private static final Map<String, Object> IDS = new ConcurrentHashMap<String, Object>(100, 0.75f, 4);
    /**
     * The zone rule groups.
     * Should not be empty.
     */
    private static final ConcurrentMap<String, ZoneRulesGroup> GROUPS =
            new ConcurrentHashMap<String, ZoneRulesGroup>(16, 0.75f, 2);

    static {
        ResourceZoneRulesDataProvider.load();
    }

    /**
     * The zone rules group ID, such as 'TZDB'.
     */
    private final String groupID;
    /**
     * The versions and rules.
     */
    private AtomicReference<TreeMap<String, ZoneRulesVersion>> versions =
            new AtomicReference<TreeMap<String, ZoneRulesVersion>>(
                    new TreeMap<String, ZoneRulesVersion>(Collections.reverseOrder()));

    //-----------------------------------------------------------------------
    /**
     * Checks if the group ID is valid.
     * <p>
     * Which groups are available is dependent on the registered providers.
     *
     * @param groupID  the group ID, null returns false
     * @return true if the group ID is valid
     */
    public static boolean isValidGroupID(String groupID) {
        if (groupID == null) {
            return false;
        }
        return GROUPS.containsKey(groupID);
    }

    /**
     * Gets a group by ID, such as 'TZDB'.
     * <p>
     * Which groups are available is dependent on the registered providers.
     * <p>
     * This method relies on time-zone data provider files. These are often loaded as jar files.
     * If no providers have been {@link #registerProvider(ZoneRulesDataProvider) registered} or no
     * provider has been registered for the requested group then an exception is thrown.
     *
     * @param groupID  the group ID, not null
     * @return the zone rules group, never null
     * @throws CalendricalException if the group ID is not found
     */
    public static ZoneRulesGroup getGroup(String groupID) {
        ZoneRules.checkNotNull(groupID, "Group ID must not be null");
        ZoneRulesGroup group = GROUPS.get(groupID);
        if (group == null) {
            if (GROUPS.isEmpty()) {
                throw new CalendricalException("Unknown time-zone group '" + groupID + "', no time-zone data files registered");
            }
            throw new CalendricalException("Unknown time-zone group '" + groupID + '\'');
        }
        return group;
    }

    /**
     * Gets the set of available zone rule groups.
     * <p>
     * Which groups are available is dependent on the registered providers.
     * <p>
     * The returned groups will remain available and valid for the lifetime of the application as
     * there is no way to deregister time-zone information. More groups may be added during
     * the lifetime of the application, however the returned list will not be altered.
     *
     * @return an unsorted, independent, modifiable list of available groups, never null
     */
    public static List<ZoneRulesGroup> getAvailableGroups() {
        return new ArrayList<ZoneRulesGroup>(GROUPS.values());
    }

    /**
     * Gets a view of the complete set of parsable group:region IDs.
     * <p>
     * This returns the complete set of group:region IDs that can be parsed.
     * The version is not included in the set for performance reasons.
     * Each 'TZDB' ID will be included twice as the 'TZDB:' prefix is optional in parsing.
     * For more detailed control, use the instance methods on this class.
     * <p>
     * For example, for the single time-zone of 'Europe/London' would contain:
     * <ul>
     * <li>{@code Europe/London}</li>
     * <li>{@code TZDB:Europe/London}</li>
     * </ul>
     * <p>
     * The returned set is a view of underlying state that may be changed by another thread.
     * The underlying set is thread-safe, thus the view is thread-safe.
     * However, check-then-act operations are potentially unsafe.
     * <p>
     * Since IDs are never deregistered, the set can only get larger.
     * This means that it the caller can cache the set and its current size to use
     * as an indication as to whether the contents have changed.
     *
     * @return an unmodifiable set of parsable group:region IDs, never null
     */
    public static Set<String> getParsableIDs() {
        return Collections.unmodifiableSet(IDS.keySet());
    }

    //-----------------------------------------------------------------------
    /**
     * Registers a zone rules provider with this group.
     * <p>
     * This adds a new provider to those currently available.
     * Each provider is specific to one group, but may provide any number of
     * regions and versions.
     * <p>
     * To ensure the integrity of time-zones already created, there is no way
     * to deregister providers.
     *
     * @param provider  the provider to register, not null
     * @return the rules group, never null
     * @throws CalendricalException if the group ID is invalid
     * @throws CalendricalException if the provider is already registered
     */
    public static synchronized ZoneRulesGroup registerProvider(ZoneRulesDataProvider provider) {
        ZoneRulesGroup group = GROUPS.get(provider.getGroupID());
        if (group == null) {
            group = new ZoneRulesGroup(provider.getGroupID());
            GROUPS.put(provider.getGroupID(), group);
        }
        group.registerProvider0(provider);
        return group;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param groupID  the group ID, not null
     * @throws CalendricalException if the group ID is invalid
     */
    private ZoneRulesGroup(String groupID) {
        ZoneRules.checkNotNull(groupID, "Group ID must not be null");
        if (PATTERN_GROUP.matcher(groupID).matches() == false) {
            throw new CalendricalException("Invalid group ID '" + groupID + "', must match regex [A-Za-z0-9._-]+");
        }
        this.groupID = groupID;
    }

    /**
     * Registers a zone rules provider with this group.
     *
     * @param provider  the provider to register, not null
     */
    @SuppressWarnings("unchecked")
    private void registerProvider0(ZoneRulesDataProvider provider) {
        // synchronized by caller
        TreeMap<String, ZoneRulesVersion> newVersions = (TreeMap<String, ZoneRulesVersion>) versions.get().clone();
        for (ZoneRulesVersion version : provider.getVersions()) {
            String versionID = version.getVersionID();
            ZoneRules.checkNotNull(versionID, "Version ID must not be null");
            if (PATTERN_VERSION.matcher(versionID).matches() == false) {
                throw new CalendricalException("Invalid version ID '" + versionID + "', must match regex [A-Za-z0-9._-]+");
            }
            if (newVersions.containsKey(versionID)) {
                throw new CalendricalException("Cannot register provider for group '" +
                        groupID + "' as version '" + versionID + "' is already registered");
            }
            newVersions.put(versionID, version);
        }
        versions.set(newVersions);
        
        Set<String> regionIDs = provider.getRegionIDs();
        for (String regionID : regionIDs) {
            IDS.put(groupID + ':' + regionID, "");
            if (groupID.equals("TZDB")) {
                IDS.put(regionID, "");
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the group, such as 'TZDB'.
     *
     * @return the ID of the group, never null
     */
    public String getID() {
        return groupID;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the region#version ID combination is valid.
     *
     * @param regionID  the time-zone region ID, null returns false
     * @param versionID  the time-zone version ID, null returns false
     * @return true if the version ID is valid
     */
    public boolean isValidRules(String regionID, String versionID) {
        if (regionID == null || versionID == null) {
            return false;
        }
        ZoneRulesVersion version = versions.get().get(versionID);
        return version != null && version.isRegionID(regionID);
    }

    /**
     * Gets the rules for the specified region#version ID combination.
     *
     * @param regionID  the time-zone region ID, not null
     * @param versionID  the time-zone version ID, not null
     * @return the matched zone rules, never null
     * @throws CalendricalException if the rules cannot be found
     */
    public ZoneRules getRules(String regionID, String versionID) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        ZoneRules.checkNotNull(versionID, "Version ID must not be null");
        ZoneRulesVersion version = versions.get().get(versionID);
        if (version == null) {
            throw new CalendricalException("Unknown version for group: " + groupID + ':' + regionID + '#' + versionID);
        }
        ZoneRules rules = version.getZoneRules(regionID);
        if (rules == null) {
            throw new CalendricalException("Unknown region for version: " + groupID + ':' + regionID + '#' + versionID);
        }
        return rules;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rules for the specified region and version ensuring that the rules
     * are valid for the date-time.
     * <p>
     * This method returns the rules matching the region and version providing that
     * the date-time is valid.
     * This checks both the region and version IDs for validity.
     * <p>
     * The returned rules will remain available and valid for the lifetime of the application as
     * there is no way to deregister time-zone information. More rules may be added during
     * the lifetime of the application, however the returned rules will not be altered.
     *
     * @param regionID  the time-zone region ID, not null
     * @param versionID  the time-zone version ID, empty means floating version, not null
     * @param dateTime  the date-time that must be valid, not null
     * @return the matched zone rules, never null
     * @throws CalendricalException if the rules cannot be found
     */
    public ZoneRules getRulesValidFor(String regionID, String versionID, OffsetDateTime dateTime) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        ZoneRules.checkNotNull(versionID, "Version ID must not be null");
        ZoneRules.checkNotNull(dateTime, "Valid date-time must not be null");
        ZoneRules rules = getRules(regionID, versionID);
        if (rules.isValidDateTime(dateTime) == false) {
            throw new CalendricalException("Rules in time-zone " + groupID + ':' + regionID +
                    '#' + versionID + " are invalid for date-time " + dateTime);
        }
        return rules;
    }

    /**
     * Finds the latest version ID that is valid for
     * <p>
     * This method returns the latest version of the region rules where the date-time is valid.
     * This checks the region for validity.
     * <p>
     * The returned version will remain valid for the lifetime of the application as
     * there is no way to deregister time-zone information. If more time-zone information
     * is added then a later version may become available.
     *
     * @param regionID  the time-zone region ID, not null
     * @param dateTime  the date-time that must be valid, not null
     * @return the matched zone rules, never null
     * @throws CalendricalException if the region is unknown
     * @throws CalendricalException if the rules cannot be found
     */
    public String getLatestVersionIDValidFor(String regionID, OffsetDateTime dateTime) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        ZoneRules.checkNotNull(dateTime, "OffsetDateTime must not be null");
        boolean foundRegion = false;
        for (ZoneRulesVersion version : versions.get().values()) {
            if (version.isRegionID(regionID)) {
                foundRegion = true;
                ZoneRules rules = version.getZoneRules(regionID);  // not null if registered properly
                if (rules.isValidDateTime(dateTime)) {
                    return version.getVersionID();
                }
            }
        }
        if (foundRegion) {
            throw new CalendricalException("No rules could be found for '" + groupID + ':' + regionID +
                    "' that are valid for date-time " + dateTime);
        }
        throw new CalendricalException("Unknown time-zone region: " + groupID + ':' + regionID);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the set of available versions for this group.
     * <p>
     * The available versions are returned sorted from newest to oldest using
     * an ordering determined by a {@code String} based sort.
     * <p>
     * The returned versions will remain available for the lifetime of the application as
     * there is no way to deregister time-zone information. More regions may be added during
     * the lifetime of the application, however the returned list will not be dynamically updated.
     *
     * @return the version IDs sorted from newest to oldest, unmodifiable, never null
     * @throws CalendricalException if the region ID is not found
     */
    public Set<String> getAvailableVersionIDs() {
        return Collections.unmodifiableSet(versions.get().keySet());
    }

    /**
     * Gets the latest available version of the group's data.
     * <p>
     * The latest available version is determined by a {@code String} based sort
     * of the versions.
     * <p>
     * The returned version will remain available for the lifetime of the application as
     * there is no way to deregister time-zone information. More information may be added during
     * the lifetime of the application, causing a later version to be available.
     *
     * @return the latest version ID for the group, never null
     * @throws CalendricalException if the region ID is not found
     */
    public String getLatestVersionID() {
        return versions.get().firstKey();  // TODO: test, use as cache in TimeZone???
    }

    /**
     * Gets the latest available version of the group's data for a region.
     * <p>
     * The latest available version is determined by a {@code String} based sort
     * of the versions.
     * <p>
     * The returned version will remain available for the lifetime of the application as
     * there is no way to deregister time-zone information. More information may be added during
     * the lifetime of the application, causing a later version to be available.
     *
     * @param regionID  the time-zone region ID, not null
     * @return the latest version ID for the region, never null
     * @throws CalendricalException if the region ID is not found
     */
    public String getLatestVersionID(String regionID) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        for (ZoneRulesVersion version : versions.get().values()) {
            if (version.isRegionID(regionID)) {
                return version.getVersionID();
            }
        }
        throw new CalendricalException("Unknown time-zone region: " + groupID + ':' + regionID);
    }

    /**
     * Checks if the region ID is valid.
     * <p>
     * The returned version will remain available for the lifetime of the application as
     * there is no way to deregister time-zone information. More information may be added during
     * the lifetime of the application, causing a later version to be available.
     *
     * @param regionID  the time-zone region ID, not null
     * @return true if the region ID is valid for at least one version
     * @throws CalendricalException if the region ID is not found
     */
    public boolean isValidRegionID(String regionID) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        for (ZoneRulesVersion version : versions.get().values()) {
            if (version.isRegionID(regionID)) {
                return true;
            }
        }
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the set of available region IDs for this group that are valid for the specified version.
     * <p>
     * The available versions are returned sorted from oldest to newest using
     * an ordering determined by a {@code String} based sort.
     * <p>
     * If the version is not found, an empty list is returned.
     * <p>
     * The returned regions will remain available for the lifetime of the application as
     * there is no way to deregister time-zone information. More regions may be added during
     * the lifetime of the application, however the returned list will not be altered.
     *
     * @param versionID  the time-zone version ID, empty means any version, not null
     * @return the region IDs, unmodifiable, never null
     */
    public Set<String> getRegionIDs(String versionID) {
        ZoneRules.checkNotNull(versionID, "Version ID must not be null");
        ZoneRulesVersion version = versions.get().get(versionID);
        if (version == null) {
            throw new CalendricalException("Unknown time-zone version: " + groupID + '#' + versionID);
        }
        return version.getRegionIDs();
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified by comparing the group ID.
     *
     * @param otherGroup  the other group, null returns false
     * @return true if this zone is the same as that specified
     */
    @Override
    public boolean equals(Object otherGroup) {
        if (this == otherGroup) {
           return true;
        }
        if (otherGroup instanceof ZoneRulesGroup) {
            return groupID.equals(((ZoneRulesGroup) otherGroup).groupID);
        }
        return false;
    }

    /**
     * A hash code for this object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return groupID.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the group using the group ID.
     *
     * @return the string representation, never null
     */
    @Override
    public String toString() {
        return groupID;
    }

}
