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

import java.util.List;

import javax.time.Instant;
import javax.time.LocalDateTime;
import javax.time.OffsetDate;
import javax.time.OffsetDateTime;
import javax.time.OffsetTime;
import javax.time.Period;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.ZonedDateTime;

/**
 * The rules defining how the zone offset varies for a single time-zone.
 * <p>
 * The rules model all the historic and future transitions for a time-zone.
 * {@link ZoneOffsetTransition} is used for known transitions, typically historic.
 * {@link ZoneOffsetTransitionRule} is used for future transitions that are based
 * on the result of an algorithm.
 * <p>
 * The rules are loaded via {@link ZoneId} and {@link ZoneRulesGroup} and
 * are specific to a group, region and version. The same rules may be shared
 * between multiple versions, regions or even groups.
 * <p>
 * Serializing an instance of {@code ZoneRules} will store the entire set
 * of rules. It does not store the group, region or version as they are not
 * part of the state of this object.
 * <p>
 * A rule implementation may or may not store full information about historic
 * and future transitions, and the information stored is only as accurate as
 * that supplied to the implementation by the rules provider.
 * Applications should treat the data provided as representing the best information
 * available to the implementation of this rule.
 * 
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * Subclasses should be Serializable wherever possible.
 */
public interface ZoneRules {

    /**
     * Checks of the zone rules are fixed, such that the offset never varies.
     * <p>
     * It is intended that {@link OffsetDateTime}, {@link OffsetDate} and
     * {@link OffsetTime} are used in preference to fixed offset time-zones
     * in {@link ZonedDateTime}.
     *
     * @return true if the time-zone is fixed and the offset never changes
     */
    boolean isFixedOffset();

    //-----------------------------------------------------------------------
    /**
     * Gets the offset applicable at the specified instant in this zone.
     * <p>
     * For any given instant there can only ever be one valid offset, which
     * is returned by this method.
     *
     * @param instant  the instant to find the offset for,
     *   ignored for fixed offset rules, otherwise not null
     * @return the offset, not null
     */
    ZoneOffset getOffset(Instant instant);

    /**
     * Gets the offset information for a local date-time in this zone.
     * <p>
     * This provides access to full details as to the offset or offsets applicable
     * for the local date-time. The mapping from a local date-time to an offset
     * is not straightforward. There are three cases:
     * <ul>
     * <li>Normal. Where there is a single offset for the local date-time.</li>
     * <li>Gap. Where there is a gap in the local time-line normally caused by the
     * spring cutover to daylight savings. There are no valid offsets within the gap</li>
     * <li>Overlap. Where there is a gap in the local time-line normally caused by the
     * autumn cutover from daylight savings. There are two valid offsets during the overlap.</li>
     * </ul>
     * The returned object provides this information and it is vital to check
     * {@link ZoneOffsetInfo#isTransition()} to handle the gap or overlap.
     *
     * @param dateTime  the date-time to find the offset information for, not null
     * @return the offset information, not null
     */
    ZoneOffsetInfo getOffsetInfo(LocalDateTime dateTime);

    //-----------------------------------------------------------------------
    /**
     * Checks if the offset date-time is valid for these rules.
     * <p>
     * To be valid, the local date-time must not be in a gap and the offset
     * must match the valid offsets.
     *
     * @param dateTime  the date-time to check, not null
     * @return true if the offset date-time is valid for these rules
     */
    boolean isValidDateTime(OffsetDateTime dateTime);
//    default {
//        ZoneOffsetInfo info = getOffsetInfo(dateTime.toLocalDateTime());
//        return info.isValidOffset(dateTime.getOffset());
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the standard offset for the specified instant in this zone.
     * <p>
     * This provides access to historic information on how the standard offset
     * has changed over time.
     * The standard offset is the offset before any daylight saving time is applied.
     * This is typically the offset applicable during winter.
     *
     * @param instant  the instant to find the offset information for, not null
     * @return the standard offset, not null
     */
    ZoneOffset getStandardOffset(Instant instant);

    /**
     * Gets the amount of daylight savings in use for the specified instant in this zone.
     * <p>
     * This provides access to historic information on how the amount of daylight
     * savings has changed over time.
     * This is the difference between the standard offset and the actual offset.
     * It is expressed in seconds.
     * Typically the amount is zero during winter and one hour during summer.
     *
     * @param instant  the instant to find the offset information for, not null
     * @return the difference between the standard and actual offset, not null
     */
    Period getDaylightSavings(Instant instant);
//    default {
//        ZoneOffset standardOffset = getStandardOffset(instant);
//        ZoneOffset actualOffset = getOffset(instant);
//        return actualOffset.toPeriod().minus(standardOffset.toPeriod()).normalized();
//    }

    /**
     * Gets the standard offset for the specified instant in this zone.
     * <p>
     * This provides access to historic information on how the standard offset
     * has changed over time.
     * The standard offset is the offset before any daylight saving time is applied.
     * This is typically the offset applicable during winter.
     *
     * @param instant  the instant to find the offset information for, not null
     * @return the standard offset, not null
     */
    boolean isDaylightSavings(Instant instant);
//    default {
//        return (getStandardOffset(instant).equals(getOffset(instant)) == false);
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next transition after the specified transition.
     * <p>
     * This returns details of the next transition after the specified instant.
     * For example, if the instant represents a point where "Summer" daylight savings time
     * applies, then the method will return the transition to the next "Winter" time.
     *
     * @param instant  the instant to get the next transition after, not null
     * @return the next transition after the specified instant, null if this is after the last transition
     */
    ZoneOffsetTransition nextTransition(Instant instant);

    /**
     * Gets the previous transition after the specified transition.
     * <p>
     * This returns details of the previous transition after the specified instant.
     * For example, if the instant represents a point where "summer" daylight saving time
     * applies, then the method will return the transition from the previous "winter" time.
     *
     * @param instant  the instant to get the previous transition after, not null
     * @return the previous transition after the specified instant, null if this is before the first transition
     */
    ZoneOffsetTransition previousTransition(Instant instant);

    /**
     * Gets the complete list of fully defined transitions.
     * <p>
     * The complete set of transitions for this rules instance is defined by this method
     * and {@link #getTransitionRules()}. This method returns those transitions that have
     * been fully defined. These are typically historical, but may be in the future.
     * <p>
     * The list will be empty for fixed offset rules and for any time-zone where there has
     * only ever been a single offset. The list will also be empty if the transition rules are unknown.
     *
     * @return an immutable list of fully defined transitions, not null
     */
    List<ZoneOffsetTransition> getTransitions();

    /**
     * Gets the list of transition rules for years beyond those defined in the transition list.
     * <p>
     * The complete set of transitions for this rules instance is defined by this method
     * and {@link #getTransitions()}. This method returns instances of {@link ZoneOffsetTransitionRule}
     * that define an algorithm for when transitions will occur.
     * <p>
     * For any given {@code ZoneRules}, this list contains the transition rules for years
     * beyond those years that have been fully defined. These rules typically refer to future
     * daylight saving time rule changes.
     * <p>
     * If the zone defines daylight savings into the future, then the list will normally
     * be of size two and hold information about entering and exiting daylight savings.
     * If the zone does not have daylight savings, or information about future changes
     * is uncertain, then the list will be empty.
     * <p>
     * The list will be empty for fixed offset rules and for any time-zone where there is no
     * daylight saving time. The list will also be empty if the transition rules are unknown.
     *
     * @return an immutable list of transition rules, not null
     */
    List<ZoneOffsetTransitionRule> getTransitionRules();

    //-----------------------------------------------------------------------
    /**
     * Checks if this set of rules equals another.
     * <p>
     * Two rule sets are equal if they will always result in the same output
     * for any given input instant or local date-time.
     * Rules from two different groups may return false even if they are in fact the same.
     * <p>
     * This definition should result in implementations comparing their entire state.
     *
     * @param otherRules  the other rules, null returns false
     * @return true if this rules is the same as that specified
     */
    @Override
    boolean equals(Object otherRules);

    /**
     * Returns a suitable hash code given the definition of {@code #equals}.
     *
     * @return the hash code
     */
    @Override
    int hashCode();

}
