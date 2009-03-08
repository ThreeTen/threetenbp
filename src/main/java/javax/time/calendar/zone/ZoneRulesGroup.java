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
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.CalendricalException;

/**
 * A group of time zone rules wrapping a provider of multiple versions of the data.
 * <p>
 * Zone rule data is provided by organizations or groups.
 * To manage this data each group is given a unique ID.
 * Two IDs are provided as standard - 'Fixed' and 'TZDB'.
 * <p>
 * The 'Fixed' group represents simple time zones that have no rules.
 * The zone offset is fixed for all time.
 * As there are no versions of the 'Fixed' group data, a blank string should be used instead.
 * <p>
 * The 'TZDB' group represents that data provided by the
 * <a href="http://www.twinsun.com/tz/tz-link.htm">time zone database</a>
 * as used in older versions of Java and many operating systems.
 * <p>
 * Other groups of zone rules can be developed and registered.
 * Group IDs should be reverse domain names as with package names unless explicitly
 * approved by the JSR-310 expert group.
 * <p>
 * ZoneRulesGroup is a thread-safe map of provider versions.
 * New version providers may safely be added during the lifetime of the application.
 *
 * @author Stephen Colebourne
 */
public final class ZoneRulesGroup {

    /**
     * The zone rule groups.
     */
    private static final ConcurrentMap<String, ZoneRulesGroup> GROUPS =
            new ConcurrentHashMap<String, ZoneRulesGroup>();

    static {
        // TODO: better
       ZoneRulesGroup.registerProvider(new TZDBZoneRulesDataProvider("2008i"));
       ZoneRulesGroup.registerProvider(FixedZoneRulesDataProvider.INSTANCE);
    }

    /**
     * The zone rules group ID, such as 'TZDB'.
     */
    private final String id;
    /**
     * The zone rule versions.
     */
    private final ConcurrentMap<String, ZoneRulesGroupVersion> versions =
            new ConcurrentHashMap<String, ZoneRulesGroupVersion>();

    //-----------------------------------------------------------------------
    /**
     * Gets a group by ID, such as 'TZDB'.
     *
     * @param groupID  the group ID, not null
     * @return the zone rules group, never null
     * @throws IllegalArgumentException if the group ID is not found
     */
    public static ZoneRulesGroup getGroup(String groupID) {
        ZoneRules.checkNotNull(groupID, "Group ID must not be null");
        ZoneRulesGroup group = GROUPS.get(groupID);
        if (group == null) {
            throw new IllegalArgumentException("Unknown zone rules group: " + groupID);
        }
        return group;
    }

    /**
     * Gets the set of available zone rule groups.
     *
     * @return an unsorted, independent, modifiable list of available versions, never null
     */
    public static List<ZoneRulesGroup> getAvailableGroups() {
        return new ArrayList<ZoneRulesGroup>(GROUPS.values());
    }

    /**
     * Gets a group by group and version ID, such as 'TZDB/2009b'.
     * <p>
     * If the version is missing, the latest version will be returned.
     *
     * @param groupVersionID  the group and version ID, not null
     * @return the zone rules group version, never null
     * @throws IllegalArgumentException if the group or version ID is not found
     */
    public static ZoneRulesGroupVersion getGroupVersion(String groupVersionID) {
        int pos = groupVersionID.indexOf('/');
        if (pos >= 0) {
            return getGroup(groupVersionID.substring(0, pos)).getVersion(groupVersionID.substring(pos + 1));
        } else {
            return getGroup(groupVersionID).getLatestVersion();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Registers a zone rules provider with this group.
     *
     * @param provider  the provider to register, not null
     * @throws CalendricalException if the group ID is invalid
     * @throws CalendricalException if the provider is already registered
     */
    public static void registerProvider(ZoneRulesDataProvider provider) {
        ZoneRulesGroup group = GROUPS.get(provider.getGroupID());
        if (group == null) {
            group = new ZoneRulesGroup(provider.getGroupID());
            group.registerProvider0(provider);
            ZoneRulesGroup old = GROUPS.putIfAbsent(provider.getGroupID(), group);
            if (old != null) {
                group = old;  // another thread working in parallel
            } else {
                return;
            }
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
        if (groupID.contains(":")) {
            throw new CalendricalException("Group ID must not contain ':'");
        }
        if (groupID.contains("/")) {
            throw new CalendricalException("Group ID must not contain '/'");
        }
        this.id = groupID;
    }

    /**
     * Registers a zone rules provider with this group.
     *
     * @param provider  the provider to register, not null
     */
    private void registerProvider0(ZoneRulesDataProvider provider) {
        ZoneRulesGroupVersion version = new ZoneRulesGroupVersion(this, provider);
        ZoneRulesGroupVersion old = versions.putIfAbsent(provider.getVersion(), version);
        if (old != null) {
            throw new CalendricalException("ZoneRulesDataProvider already registered: ");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the source, such as 'TZDB'.
     *
     * @return the ID of the source, never null
     */
    public String getID() {
        return id;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the specified version of the group's data.
     *
     * @param version  the version, not null
     * @return the matched version, never null
     * @throws IllegalArgumentException if the version is not found
     */
    public ZoneRulesGroupVersion getVersion(String version) {
        ZoneRulesGroupVersion v = versions.get(version);
        if (v == null) {
            throw new IllegalArgumentException("Unknown zone rules version: " + id + ":" + version);
        }
        return v;
    }

    /**
     * Gets the latest available version of the group's data.
     * <p>
     * The latest available group is determined by a <code>String</code> based sort
     * of the versions.
     *
     * @return an independent, modifiable list of available versions from oldest to newest, never null
     * @throws IllegalStateException if there are no versions for this group
     */
    public ZoneRulesGroupVersion getLatestVersion() {
        List<ZoneRulesGroupVersion> versions = getAvailableVersions();
        if (versions.size() == 0) {
            throw new IllegalStateException("No zone rules version available for group: " + id);
        }
        return versions.get(versions.size() - 1);
    }

    /**
     * Gets the set of available zone rule versions for this group.
     * <p>
     * The available versions are returned sorted from oldest to newest using
     * an ordering determined by a <code>String</code> based sort.
     *
     * @return an independent, modifiable list of available versions from oldest to newest, never null
     */
    public List<ZoneRulesGroupVersion> getAvailableVersions() {
        TreeMap<String, ZoneRulesGroupVersion> sorted = new TreeMap<String, ZoneRulesGroupVersion>(versions);
        return new ArrayList<ZoneRulesGroupVersion>(sorted.values());
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified by comparing the ID.
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
            return id.equals(((ZoneRulesGroup) otherGroup).id);
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
        return id.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the group using the ID.
     *
     * @return the group ID, never null
     */
    @Override
    public String toString() {
        return id;
    }

}
