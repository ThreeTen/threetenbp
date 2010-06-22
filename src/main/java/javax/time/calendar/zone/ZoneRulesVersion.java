/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Set;

/**
 * A version of time-zone rules from a single group.
 * <p>
 * Zone rule data is provided by organizations or groups.
 * Each group provides multiple versions of their data over time.
 * This interface models one version of data.
 * <p>
 * ZoneRulesVersion is a service provider interface that can be called
 * by multiple threads. Implementations must be immutable and thread-safe.
 * <p>
 * Implementations are responsible for caching.
 *
 * @author Stephen Colebourne
 */
public interface ZoneRulesVersion {

    /**
     * Gets the time-zone version ID of the data available via this provider, such as '2010e'.
     * <p>
     * Version IDs must match regex {@code [A-Za-z0-9._-]+}.
     *
     * @return the ID of the group, never null
     */
    String getVersionID();

    /**
     * Checks if the region ID is valid.
     *
     * @param regionID  the region ID, null returns false
     * @return true if the specified region ID is valid for this group and version
     */
    boolean isRegionID(String regionID);

    /**
     * Gets the complete set of provided region IDs, such as 'Europe/Paris'.
     * <p>
     * Region IDs must match regex {@code [A-Za-z0-9%@~/+._-]+}.
     *
     * @return the provided region IDs, unmodifiable, never null
     */
    Set<String> getRegionIDs();

    /**
     * Gets the zone rules for the specified region ID.
     * <p>
     * The region ID should be one of those returned by {@code #getRegionIDs()}.
     *
     * @param regionID  the region ID, not null
     * @return the matched zone rules, null if not found
     */
    ZoneRules getZoneRules(String regionID);

}
