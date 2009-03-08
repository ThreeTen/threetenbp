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

import javax.time.CalendricalException;

/**
 * A version of the time zone rule data.
 * <p>
 * Zone rule data is provided by groups, and each group can produce multiple versions of the data.
 * This class captures a single version of all the zone rule data provided by a group.
 * <p>
 * ZoneRulesGroupVersion is a immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ZoneRulesGroupVersion {

    /**
     * The zone rules group, such as 'TZDB'.
     */
    private final ZoneRulesGroup group;
    /**
     * The zone rules version ID, such as '2009b'.
     */
    private final String versionID;
    /**
     * The zone rules provider.
     */
    private final ZoneRulesDataProvider provider;

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param group  the group, not null
     * @param provider  the version provider, not null
     * @throws CalendricalException if the version is invalid
     */
    ZoneRulesGroupVersion(ZoneRulesGroup group, ZoneRulesDataProvider provider) {
        if (group == null) {
            throw new NullPointerException("Group must not be null");
        }
        if (provider == null) {
            throw new NullPointerException("ZoneRulesDataProvider must not be null");
        }
        if (provider.getVersion().contains(":")) {
            throw new CalendricalException("Version must not contain ':'");
        }
        this.group = group;
        this.versionID = provider.getVersion();
        this.provider = provider;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the group, such as 'TZDB'.
     *
     * @return the group, never null
     */
    public ZoneRulesGroup getGroup() {
        return group;
    }

    /**
     * Gets the version ID of the zone rules, such as '2009b'.
     *
     * @return the version, never null
     */
    public String getID() {
        return versionID;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone rules for the specified time zone location ID.
     *
     * @param locationID  the time zone location ID, not null
     * @return the zone rules, never null
     * @throws IllegalArgumentException if the time zone ID is invalid
     */
    public ZoneRules getZoneRules(String locationID) {
        ZoneRules rules = provider.getZoneRules(locationID);
        if (rules == null) {
            throw new IllegalArgumentException("Unknown time zone location: " + locationID);
        }
        return rules;
    }

    /**
     * Gets the set of available time zone location IDs for this group and version.
     *
     * @return an unsorted, independent, modifiable list of available IDs, never null
     */
    public List<String> getAvailableLocationIDs() {
        // TODO: sort?
        return new ArrayList<String>(provider.getAvailableTimeZoneIDs());
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified by comparing the group ID and version.
     *
     * @param otherVersion  the other group, null returns false
     * @return true if this zone is the same as that specified
     */
    @Override
    public boolean equals(Object otherVersion) {
        if (this == otherVersion) {
           return true;
        }
        if (otherVersion instanceof ZoneRulesGroupVersion) {
            ZoneRulesGroupVersion v = (ZoneRulesGroupVersion) otherVersion;
            return group.equals(v.group) && versionID.equals(v.versionID);
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
        return group.hashCode() ^ versionID.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the group using the ID.
     *
     * @return the group ID, never null
     */
    @Override
    public String toString() {
        return group.getID() + ":" + versionID;
    }

}
