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

import java.util.Set;

/**
 * Provides access to a versioned set of time zone rules.
 * <p>
 * Multiple providers of time zone rules may be registered.
 * Each provider will supply one to many zone IDs.
 * No two providers may overlap in the set of zone IDs that they provide.
 * <p>
 * A typical approach is for each provider to supply all the data for a single version.
 * This approach removes the possibility of two providers supplying the same zone ID.
 * <p>
 * ZoneRulesDataProvider is a service provider interface that can be called
 * by multiple threads.
 *
 * @author Stephen Colebourne
 */
public interface ZoneRulesDataProvider {

    /**
     * Gets the time zone group ID of the data available via this provider, such as 'TZDB'.
     * <p>
     * Group IDs may consist of alphanumeric characters, the dot, underscore and dash.
     * Group IDs should use reverse domain name notation, like packages.
     * Group IDs without a dot are reserved.
     *
     * @return the ID of the group, never null
     */
    String getGroupID();

    /**
     * Gets the complete set of provided time zone IDs.
     * <p>
     * The returned IDs specify the region ID, optionally followed by the version ID.
     * If the provider supports versions, then versions must be included.
     * <p>
     * For example, if this provider supports versions '1.1' and '1.2' of 'France'
     * then the IDs returned might be 'France#1.1' and 'France#1.2'.
     *
     * @return the provided IDs, unmodifiable, never null
     */
    Set<String> getIDs();

    /**
     * Gets the zone rules for the specified time zone ID.
     * <p>
     * The combined time zone ID must be one of those returned by {@link #getZoneIDs()}.
     *
     * @param regionID  the time zone region ID, not null
     * @param versionID  the time zone version ID, not null
     * @return the matched zone rules, null if not found
     */
    ZoneRules getZoneRules(String regionID, String versionID);

}
