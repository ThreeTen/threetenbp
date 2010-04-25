/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import javax.time.CalendricalException;
import javax.time.calendar.zone.ZoneOffsetInfo;
import javax.time.calendar.zone.ZoneOffsetTransition;
import javax.time.calendar.zone.ZoneRules;

/**
 * Strategy for resolving a {@code LocalDateTime} to an {@code OffsetDateTime}
 * using the rules of the time-zone.
 * <p>
 * A time-zone provides rules for when and by how much the offset changes for
 * a given location. These rules can result in 'missing hours', such as at the
 * spring daylight savings cutover, and 'overlapping hours', such as at the
 * autumn cutover.
 * <p>
 * Implementations of this resolver handles these missing and overlapping cases
 * by either throwing an exception, selecting the appropriate offset or changing
 * the local date-time.
 * <p>
 * ZoneResolver is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public abstract class ZoneResolver {

    /**
     * Restrictive constructor.
     */
    protected ZoneResolver() {
        super();
    }

    //-----------------------------------------------------------------------
    /**
     * Resolves the new local date-time to an offset date-time using the zone.
     * <p>
     * This method forwards to an internal method that calls {@link #handleGap}
     * or {@link #handleOverlap}. The internal method will validate the result
     * to ensure it is valid for the zone.
     *
     * @param zone  the time-zone, not null
     * @param newDateTime  the new date-time, not null
     * @param oldDateTime  the old date-time before the adjustment, may be null
     * @return the resolved values, returned as a (year,month,day) tuple, never null
     * @throws CalendricalException if the date-time cannot be resolved
     */
    public final OffsetDateTime resolve(
            TimeZone zone,
            LocalDateTime newDateTime,
            ZonedDateTime oldDateTime) {
        
        // ensure rules used are appropriate if zone has a floating version
        ZoneRules rules = (oldDateTime != null ? oldDateTime.getApplicableRules() : zone.getRules());
        
        ZoneOffsetInfo info = rules.getOffsetInfo(newDateTime);
        if (info.isTransition() == false) {
            return OffsetDateTime.of(newDateTime, info.getOffset());
        }
        ZoneOffsetTransition discontinuity = info.getTransition();
        OffsetDateTime result = discontinuity.isGap() ?
            handleGap(zone, rules, discontinuity, newDateTime, oldDateTime != null ? oldDateTime.toOffsetDateTime() : null) :
            handleOverlap(zone, rules, discontinuity, newDateTime, oldDateTime != null ? oldDateTime.toOffsetDateTime() : null);
        
        // validate the result
        if (result == null) {
            throw new CalendricalException(
                    "ZoneResolver implementation must not return null: " + getClass().getName());
        }
        if (zone.isValidFor(result) == false) {
            throw new CalendricalException(
                    "ZoneResolver implementation must return a valid date-time and offset for the zone: " + getClass().getName());
        }
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Defines the strategy for selecting an offset to use for a local date-time
     * when the local date-time is in a local time-line gap.
     * <p>
     * Implementations of method handles missing date-times by either throwing an
     * exception or changing the local date-time.
     * <p>
     * Information is provided to assist with the strategy.
     * This includes the zone rules, information about the gap and the old date-time.
     * <p>
     * The old date-time is provided if this strategy is called as a result of an
     * adjustment, such as changing a field, addition or subtraction.
     * This parameter will be null if there is no original date-time, such as
     * during construction of a {@code ZonedDateTime}.
     * <p>
     * After the completion of this method, the result will be validated.
     * <p>
     * A typical implementation might be:
     * <pre>
     *  return gapInfo.getDateTimeAfter();
     * </pre>
     * This implementation works by returning the first valid date-time after the gap.
     *
     * @param zone  the time-zone, not null
     * @param rules  the applicable zone rules, not null
     * @param gapInfo  the information about the gap for the newDateTime, not null
     * @param newDateTime  the new local date-time, not null
     * @param oldDateTime  the old offset date-time before the adjustment, may be null
     * @return the resolved offset date-time, never null
     * @throws IllegalCalendarFieldValueException if the offset cannot be calculated
     */
    protected abstract OffsetDateTime handleGap(
            TimeZone zone,
            ZoneRules rules,
            ZoneOffsetTransition gapInfo,
            LocalDateTime newDateTime,
            OffsetDateTime oldDateTime);

    /**
     * Defines the strategy for selecting an offset to use for a local date-time
     * when the local date-time is in a local time-line overlap.
     * <p>
     * Implementations of method handle overlapping date-times by throwing an
     * exception, selecting the appropriate offset or changing the local date-time.
     * Two additional parameters are available to help with the logic.
     * <p>
     * Firstly, the discontinuity, which represents the discontinuity in the local
     * time-line that needs to be resolved. This is the result from
     * {@code zone.getOffsetInfo(newDateTime)} and is provided to improve
     * performance.
     * <p>
     * Secondly, the old date-time, which is the original offset date-time that
     * any adjustment started from. Example adjustments are changing a field,
     * addition or subtraction. This parameter will be null if there is no
     * original date-time, such as during construction.
     * <p>
     * After the completion of this method, the result will be validated.
     * <p>
     * A typical implementation might be:
     * <pre>
     *  if (oldDateTime != null && discontinuity.containsOffset(oldDateTime.getOffset())) {
     *    return OffsetDateTime.dateTime(newDateTime, oldDateTime.getOffset());
     *  }
     *  return OffsetDateTime.dateTime(newDateTime, discontinuity.getOffsetBefore());
     * </pre>
     * This implementation handles the overlap by attempting to keep the result
     * offset in the same offset as the old date-time. Otherwise, it returns the
     * earlier of the two offsets.
     *
     * @param zone  the time-zone, not null
     * @param rules  the applicable zone rules, not null
     * @param overlapInfo  the information about the overlap for the newDateTime, not null
     * @param newDateTime  the new local date-time, not null
     * @param oldDateTime  the old offset date-time before the adjustment, may be null
     * @return the resolved offset date-time, never null
     * @throws IllegalCalendarFieldValueException if the offset cannot be calculated
     */
    protected abstract OffsetDateTime handleOverlap(
            TimeZone zone,
            ZoneRules rules,
            ZoneOffsetTransition overlapInfo,
            LocalDateTime newDateTime,
            OffsetDateTime oldDateTime);

}
