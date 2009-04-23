/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.time.CalendricalException;
import javax.time.calendar.OffsetDateTime;

/**
 * A group of time zone rules wrapping a provider of multiple versions of the data.
 * <p>
 * Zone rule data is provided by organizations or groups.
 * To manage this data each group is given a unique ID.
 * One group is provided as standard - 'TZDB' - and more may be added.
 * <p>
 * The 'TZDB' group represents that data provided by the
 * <a href="http://www.twinsun.com/tz/tz-link.htm">time zone database</a>
 * as used in older versions of Java and many operating systems.
 * <p>
 * Other groups of zone rules can be developed and registered.
 * Group IDs should be reverse domain names as with package names unless explicitly
 * approved by the JSR-310 expert group.
 * <p>
 * ZoneRulesGroup is thread-safe and immutable.
 * <p>
 * The static methods of ZoneRulesGroup wrap a thread-safe map of groups.
 * New groups and providers may safely be added during the lifetime of the application.
 *
 * @author Stephen Colebourne
 */
public final class ZoneRulesGroup {

    /**
     * The zone IDs.
     * Should not be empty.
     */
    private static final Set<String> IDS = new CopyOnWriteArraySet<String>();
    /**
     * The zone rule groups.
     * Should not be empty.
     */
    private static final ConcurrentMap<String, ZoneRulesGroup> GROUPS =
            new ConcurrentHashMap<String, ZoneRulesGroup>(16, 0.75f, 2);

    static {
        JarZoneRulesDataProvider.load();
    }

    /**
     * The zone rules group ID, such as 'TZDB'.
     */
    private final String groupID;
    /**
     * The zone rule data.
     * The TreeMap must never be visibly empty.
     */
    private final ConcurrentMap<String, TreeMap<String, ZoneRulesDataProvider>> regions =
            new ConcurrentHashMap<String, TreeMap<String, ZoneRulesDataProvider>>(100, 0.75f, 2);

    //-----------------------------------------------------------------------
    /**
     * Checks if the group ID is valid.
     * <p>
     * The 'TZDB' group will be always be available.
     * Any other groups are dependent on what has been installed.
     *
     * @param groupID  the group ID, null returns false
     * @return true if the group ID is valid
     */
    public static boolean isValidGroup(String groupID) {
        if (groupID == null) {
            return false;
        }
        return GROUPS.containsKey(groupID);
    }

    /**
     * Gets a group by ID, such as 'TZDB'.
     * <p>
     * The 'TZDB' group will be always be available.
     * Any other groups are dependent on what has been installed.
     *
     * @param groupID  the group ID, not null
     * @return the zone rules group, never null
     * @throws CalendricalException if the group ID is not found
     */
    public static ZoneRulesGroup getGroup(String groupID) {
        ZoneRules.checkNotNull(groupID, "Group ID must not be null");
        ZoneRulesGroup group = GROUPS.get(groupID);
        if (group == null) {
            throw new CalendricalException("Unknown time zone group ID: " + groupID);
        }
        return group;
    }

    /**
     * Gets the set of available zone rule groups.
     * <p>
     * The 'TZDB' group will be always be available.
     * Any other groups are dependent on what has been installed.
     *
     * @return an unsorted, independent, modifiable list of available groups, never null
     */
    public static List<ZoneRulesGroup> getAvailableGroups() {
        return new ArrayList<ZoneRulesGroup>(GROUPS.values());
    }

    /**
     * Gets a view of the complete set of parsable time zone IDs.
     * <p>
     * This returns the complete set of IDs that can be parsed.
     * For each group and region, all the valid versions and the 'floating'
     * version IDs will be included. Each 'TZDB' ID will be included twice
     * as the 'TZDB:' prefix is optional in parsing.
     * For more detailed control, use the instance methods on this class.
     * <p>
     * For example, for the single time zone of 'Europe/London' and two available
     * versions, the set would contain:
     * <ul>
     * <li>Europe/London</li>
     * <li>Europe/London#2009a</li>
     * <li>Europe/London#2009b</li>
     * <li>TZDB:Europe/London</li>
     * <li>TZDB:Europe/London#2009a</li>
     * <li>TZDB:Europe/London#2009b</li>
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
     * @return an unmodifiable set of parsable IDs, never null
     */
    public static Set<String> getParsableIDs() {
        return Collections.unmodifiableSet(IDS);
    }

    //-----------------------------------------------------------------------
    /**
     * Registers a zone rules provider with this group.
     * <p>
     * This adds a new provider to those currently available.
     * Each provider is specific to one group, but may provide any number of
     * regions and versions.
     * <p>
     * To ensure the integrity of time zones already created, there is no way
     * to deregister providers.
     *
     * @param provider  the provider to register, not null
     * @throws CalendricalException if the group ID is invalid
     * @throws CalendricalException if the provider is already registered
     */
    public static synchronized void registerProvider(ZoneRulesDataProvider provider) {
        ZoneRulesGroup group = GROUPS.get(provider.getGroupID());
        if (group == null) {
            group = new ZoneRulesGroup(provider.getGroupID());
            GROUPS.put(provider.getGroupID(), group);
        }
        group.registerProvider0(provider);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param groupID  the group ID, not null
     * @throws CalendricalException if the group ID is invalid
     */
    private ZoneRulesGroup(String groupID) {
        if (groupID == null) {
            throw new NullPointerException("Group ID must not be null");
        }
        if (groupID.matches("[A-Za-z0-9._-]+") == false) {
            throw new CalendricalException("Group ID must only contain alphanumerics, dot, underscore and dash");
        }
        this.groupID = groupID;
    }

    /**
     * Registers a zone rules provider with this group.
     *
     * @param provider  the provider to register, not null
     */
    private synchronized void registerProvider0(ZoneRulesDataProvider provider) {
        Set<String> ids = provider.getIDs();
        Set<String> fullIDs = new HashSet<String>(ids.size());
        Set<String[]> splits = new HashSet<String[]>(ids.size());
        for (String id : ids) {
            int pos = id.indexOf('#');
            String regionID = id;
            String versionID = "";
            if (pos >= 0) {
                regionID = id.substring(0, pos);
                versionID = id.substring(pos + 1);
            }
            TreeMap<String, ZoneRulesDataProvider> versions = regions.get(regionID);
            if (versions != null) {
                if (versionID.length() > 0 && versions.containsKey("")) {
                    throw new CalendricalException("Cannot register versioned provider '" +
                            groupID + ":" + regionID + "#" + versionID + "' as an unversioned provider '" +
                            groupID + ":" + regionID + "' is already registered");
                }
                if (versions.containsKey(versionID)) {
                    throw new CalendricalException("Cannot register provider '" +
                            groupID + ":" + regionID + "#" + versionID + "' as one is already registered with that ID");
                }
            }
            splits.add(new String[] {regionID, versionID});
            if (groupID.equals("TZDB")) {
                fullIDs.add(id);
                fullIDs.add(regionID);
            }
            fullIDs.add(groupID + ':' + id);
            fullIDs.add(groupID + ':' + regionID);
        }
        for (String[] split : splits) {
            // still need to be careful to ensure that regions never holds an empty map
            TreeMap<String, ZoneRulesDataProvider> map = new TreeMap<String, ZoneRulesDataProvider>();
            map.put(split[1], provider);
            map = regions.putIfAbsent(split[0], map);
            if (map != null) {
                regions.get(split[0]).put(split[1], provider);
            }
        }
        IDS.addAll(fullIDs);
    }

    /**
     * Gets the region from the ID.
     *
     * @param regionID  the time zone region ID, not null
     * @return the region map, never null
     * @throws CalendricalException if the region is unknown
     */
    private TreeMap<String, ZoneRulesDataProvider> getVersions(String regionID) {
        TreeMap<String, ZoneRulesDataProvider> versions = regions.get(regionID);
        if (versions == null) {
            throw new CalendricalException("Unknown time zone region: " + groupID + ":" + regionID);
        }
        return versions;
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
     * Checks if the time zone version ID is valid.
     *
     * @param regionID  the time zone region ID, null returns false
     * @param versionID  the time zone version ID, empty means floating version, null returns false
     * @return true if the version ID is valid
     */
    public boolean isValidRules(String regionID, String versionID) {
        if (regionID == null || versionID == null) {
            return false;
        }
        TreeMap<String, ZoneRulesDataProvider> versions = regions.get(regionID);
        return versions != null &&
            (versionID.length() == 0 || versions.containsKey(versionID));
    }

    /**
     * Gets the rules for the specified region and version.
     * <p>
     * If the version is an empty string, then the latest version of the rules
     * will be returned for the region.
     *
     * @param regionID  the time zone region ID, not null
     * @param versionID  the time zone version ID, empty means floating version, not null
     * @return the matched zone rules, never null
     * @throws CalendricalException if the rules cannot be found
     */
    public ZoneRules getRules(String regionID, String versionID) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        ZoneRules.checkNotNull(versionID, "Version ID must not be null");
        TreeMap<String, ZoneRulesDataProvider> versions = getVersions(regionID);
        ZoneRulesDataProvider provider;
        if (versionID.length() == 0) {
            versionID = versions.lastKey();
            provider = versions.get(versionID);
        } else {
            provider = versions.get(versionID);
            if (provider == null) {
                throw new CalendricalException("Unknown time zone version: " + groupID + ":" +
                        regionID + (versionID.length() == 0 ? "" : "#" + versionID));
            }
        }
        return provider.getZoneRules(regionID, versionID);  // not null if registered properly
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if rules are available for the specified region and version that
     * are valid for the date-time.
     * <p>
     * This method returns true if it is possible to obtain a set of rules for
     * the specified region and version that are valid for the date-time.
     * If the version is an empty string, which represents a floating version,
     * then the search will check if any rules are valid for the specified date-time.
     *
     * @param regionID  the time zone region ID, null returns false
     * @param versionID  the time zone version ID, empty means floating version, null returns false
     * @param dateTime  the date-time that must be valid, null returns false
     * @return true if the version ID is valid
     */
    public boolean isValidRulesFor(String regionID, String versionID, OffsetDateTime dateTime) {
        if (regionID == null || versionID == null || dateTime == null) {
            return false;
        }
        try {
            getRulesValidFor(regionID, versionID, dateTime);
            return true;
        } catch (CalendricalException ex) {
            return false;
        }
    }

    /**
     * Gets the rules for the specified region and version ensuring that the rules
     * are valid for the date-time.
     * <p>
     * This method returns the rules matching the region and version providing that
     * the date-time is valid. If the version is an empty string, which represents a
     * floating version, then the latest version of the rules which are valid for the
     * specified date-time will be returned.
     *
     * @param regionID  the time zone region ID, not null
     * @param versionID  the time zone version ID, empty means floating version, not null
     * @param dateTime  the date-time that must be valid, not null
     * @return the matched zone rules, never null
     * @throws CalendricalException if the rules cannot be found
     */
    public ZoneRules getRulesValidFor(String regionID, String versionID, OffsetDateTime dateTime) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        ZoneRules.checkNotNull(versionID, "Version ID must not be null");
        ZoneRules.checkNotNull(dateTime, "Valid date-time must not be null");
        if (versionID.length() > 0) {
            // specific version
            ZoneRules rules = getRules(regionID, versionID);
            if (rules.isValidDateTime(dateTime) == false) {
                throw new CalendricalException("Rules in time zone " + groupID + ":" + regionID +
                        "#" + versionID + " are invalid for date-time " + dateTime);
            }
            return rules;
        }
        
        // floating version - pick latest version that matches date-time
        versionID = getLatestVersionIDValidFor(regionID, dateTime);
        return getRules(regionID, versionID);
    }

    /**
     * Finds the latest version ID that is valid for
     *
     * @param regionID  the time zone region ID, not null
     * @param dateTime  the date-time that must be valid, not null
     * @return the matched zone rules, never null
     * @throws CalendricalException if the rules cannot be found
     */
    public String getLatestVersionIDValidFor(String regionID, OffsetDateTime dateTime) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        ZoneRules.checkNotNull(dateTime, "OffsetDateTime must not be null");
        TreeMap<String, ZoneRulesDataProvider> versions = new TreeMap<String, ZoneRulesDataProvider>(Collections.reverseOrder());
        versions.putAll(getVersions(regionID));
        for (Entry<String, ZoneRulesDataProvider> entry : versions.entrySet()) {
            ZoneRulesDataProvider provider = entry.getValue();
            ZoneRules rules = provider.getZoneRules(regionID, entry.getKey());  // not null if registered properly
            if (rules.isValidDateTime(dateTime)) {
                return entry.getKey();
            }
        }
        throw new CalendricalException("No rules could be found in time zone " + groupID + ":" + regionID +
                " that are valid for date-time " + dateTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the set of available region IDs for this group.
     * <p>
     * Each returned region will have at least one associated version.
     *
     * @return an independent, modifiable list of available regions sorted alphabetically, never null
     */
    public List<String> getAvailableRegionIDs() {
        List<String> list = new ArrayList<String>(regions.keySet());
        Collections.sort(list);
        return list;
    }

    /**
     * Gets the set of available region IDs for this group that are valid for the specified version.
     * <p>
     * The available versions are returned sorted from oldest to newest using
     * an ordering determined by a <code>String</code> based sort.
     * <p>
     * If the version is not found, an empty list is returned.
     *
     * @param versionID  the time zone version ID, empty means any version, not null
     * @return an independent, modifiable list of available regions sorted alphabetically, never null
     */
    public List<String> getAvailableRegionIDs(String versionID) {
        ZoneRules.checkNotNull(versionID, "Version ID must not be null");
        if (versionID.length() == 0) {
            return getAvailableRegionIDs();
        }
        Set<String> set = new HashSet<String>();
        for (Entry<String, TreeMap<String, ZoneRulesDataProvider>> entry : regions.entrySet()) {
            if (entry.getValue().containsKey(versionID)) {
                set.add(entry.getKey());
            }
        }
        List<String> list = new ArrayList<String>(set);
        Collections.sort(list);
        return list;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the latest available version of the group's data.
     * <p>
     * The latest available group is determined by a <code>String</code> based sort
     * of the versions.
     *
     * @param regionID  the time zone region ID, not null
     * @return the latest version ID for the region, never null
     * @throws CalendricalException if the region ID is not found
     */
    public String getLatestVersionID(String regionID) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        TreeMap<String, ZoneRulesDataProvider> versions = getVersions(regionID);
        return versions.lastKey();
    }

    /**
     * Gets the set of available time zone versions for this group and the specified region.
     * <p>
     * The available versions are returned sorted from oldest to newest using
     * an ordering determined by a <code>String</code> based sort.
     *
     * @param regionID  the time zone region ID, not null
     * @return an independent, modifiable list of available versions from oldest to newest, never null
     * @throws CalendricalException if the region ID is not found
     */
    public List<String> getAvailableVersionIDs(String regionID) {
        ZoneRules.checkNotNull(regionID, "Region ID must not be null");
        TreeMap<String, ZoneRulesDataProvider> versions = getVersions(regionID);
        return new ArrayList<String>(versions.keySet());
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
     * @return the group ID, never null
     */
    @Override
    public String toString() {
        return groupID;
    }

}
