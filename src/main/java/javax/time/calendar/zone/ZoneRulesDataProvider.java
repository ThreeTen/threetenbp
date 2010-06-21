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

import java.util.Set;

/**
 * Provides access to a versioned set of time-zone rules from a single group.
 * <p>
 * Multiple providers of time-zone rules may be registered.
 * Each provider will supply one to many zone IDs.
 * No two providers may overlap in the set of zone IDs that they provide.
 * <p>
 * The values returned by the provider must never change over time.
 * A new provider must be returned to return new regions or versions.
 * <p>
 * Many systems would like to receive new time-zone rules dynamically.
 * This must be implemented separately from this interface, typically using a listener.
 * Whenever the listener detects new rules it should call
 * {@link ZoneRulesGroup#registerProvider(ZoneRulesDataProvider)} using a standard
 * immutable provider implementation.
 * <p>
 * ZoneRulesDataProvider is a service provider interface that can be called
 * by multiple threads. Implementations must be immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public interface ZoneRulesDataProvider {

    /**
     * Gets the time-zone group ID of the data available via this provider, such as 'TZDB'.
     * <p>
     * Group IDs must match regex {@code [A-Za-z0-9._-]+}.
     * Group IDs should use reverse domain name notation, like packages.
     * Group IDs without a dot are reserved for use by the JSR-310 expert group.
     *
     * @return the ID of the group, never null
     */
    String getGroupID();

    /**
     * Gets the provided rules, version by version.
     *
     * @return the provided rules, not to be modified, never null
     */
    Set<ZoneRulesVersion> getVersions();

    /**
     * Gets the provided region IDs.
     *
     * @return the provided region IDs, not to be modified, never null
     */
    Set<String> getRegionIDs();

}
