/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.zone;

import javax.time.CalendricalException;
import javax.time.LocalDateTime;
import javax.time.OffsetDateTime;
import javax.time.ZoneId;

/**
 * Strategy for resolving a {@code LocalDateTime} to an {@code OffsetDateTime}
 * using the rules of a time-zone.
 * <p>
 * A time-zone provides rules for when and by how much the offset changes for a given location.
 * These rules can result in gaps in the local time-line, such as at the spring daylight
 * savings cutover, and overlaps, such as at the autumn cutover.
 * <p>
 * Implementations of this resolver handles these missing and overlapping cases by either
 * throwing an exception, selecting the appropriate offset or changing the local date-time.
 * 
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 */
public interface ZoneResolver {

    /**
     * Resolves the new local date-time to an offset date-time using the zone.
     * <p>
     * This takes the local date-time and applies the rules of the time-zone to
     * create the resultant offset date-time.
     * The result must be a valid date-time for the time-zone.
     * <p>
     * The resolution may use the optional "old" date-time.
     * For example, this might be used to pick the closest offset.
     * <p>
     * The method caller supplies the {@link ZoneOffsetInfo} and {@link ZoneRules} to use.
     * These should be used in preference to methods on the supplied {@link ZoneId}.
     * Most implementations query the info to determine if the local date-time
     * is in a gap or overlap, adjusting accordingly.
     *
     * @param desiredLocalDateTime  the desired local date-time, not null
     * @param info  the zone-offset info from the rules for the local date-time, not null
     * @param rules  the time-zone rules to use, not null
     * @param zone  the target time-zone, not normally used by implementations, not null
     * @param oldDateTime  the old date-time before any adjustment, may be null
     * @return the resolved offset date-time, not null
     * @throws CalendricalException if the date-time cannot be resolved
     */
    OffsetDateTime resolve(
            LocalDateTime desiredLocalDateTime,
            ZoneOffsetInfo info,
            ZoneRules rules,
            ZoneId zone,
            OffsetDateTime oldDateTime);

}
