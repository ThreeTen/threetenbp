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
package javax.time.calendar.zone;

import java.util.List;

import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDate;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.OffsetTime;
import javax.time.calendar.Period;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.ZonedDateTime;

/**
 * The rules defining how the zone offset varies for a single time-zone.
 * <p>
 * The rules model all the historic and future transitions for a time-zone.
 * The rules are loaded via {@link TimeZone} and {@link ZoneRulesGroup} and
 * are specific to a group, region and version. The same rules may be shared
 * between multiple versions, regions or even groups.
 * <p>
 * Serializing an instance of {@code ZoneRules} will store the entire set
 * of rules. It does not store the group, region or version as they are not
 * part of the state of this object.
 * <p>
 * ZoneRules is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 * It is only intended that the abstract methods are overridden.
 * Subclasses should be Serializable wherever possible.
 *
 * @author Stephen Colebourne
 */
public abstract class ZoneRules {

    /**
     * Obtains a rules instance for a specific offset.
     * <p>
     * The returned rules object will have no transitions and will use the
     * specified offset for all points on the time-line.
     *
     * @param offset  the offset to get the fixed rules for, not null
     * @return the rules, never null
     */
    public static ZoneRules ofFixed(ZoneOffset offset) {
        return new FixedZoneRules(offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor for subclasses.
     */
    protected ZoneRules() {
        super();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks of the zone rules are fixed, such that the offset never varies.
     * <p>
     * It is intended that {@link OffsetDateTime}, {@link OffsetDate} and
     * {@link OffsetTime} are used in preference to fixed offset time-zones
     * in {@link ZonedDateTime}.
     * <p>
     * The default implementation returns false.
     *
     * @return true if the time-zone is fixed and the offset never changes
     */
    public boolean isFixedOffset() {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the offset applicable at the specified instant in this zone.
     * <p>
     * For any given instant there can only ever be one valid offset, which
     * is returned by this method. To access more detailed information about
     * the offset at and around the instant use {@link #getOffsetInfo(Instant)}.
     *
     * @param instant  the instant to find the offset for,
     *   ignored for fixed offset rules, otherwise not null
     * @return the offset, never null
     */
    public abstract ZoneOffset getOffset(InstantProvider instant);

    /**
     * Gets the offset information for the specified instant in this zone.
     * <p>
     * This provides access to full details as to the offset or offsets applicable
     * for the local date-time. The mapping from an instant to an offset
     * is not straightforward. There are two cases:
     * <ul>
     * <li>Normal. Where there is a single offset for the local date-time.</li>
     * <li>Overlap. Where there is a gap in the local time-line normally caused by the
     * autumn cutover from daylight savings. There are two valid offsets during the overlap.</li>
     * </ul>
     * The third case, a gap in the local time-line, cannot be returned by this
     * method as an instant will always represent a valid point and cannot be in a gap.
     * The returned object provides information about the offset or overlap and it
     * is vital to check {@link ZoneOffsetInfo#isTransition()} to handle the overlap.
     *
     * @param instant  the instant to find the offset information for, not null
     * @return the offset information, never null
     */
    public ZoneOffsetInfo getOffsetInfo(Instant instant) {
        ZoneOffset offset = getOffset(instant);
        OffsetDateTime odt = OffsetDateTime.ofInstant(instant, offset);
        return getOffsetInfo(odt.toLocalDateTime());
    }

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
     * @return the offset information, never null
     */
    public abstract ZoneOffsetInfo getOffsetInfo(LocalDateTime dateTime);

    //-----------------------------------------------------------------------
    /**
     * Gets the standard offset for the specified instant in this zone.
     * <p>
     * This provides access to historic information on how the standard offset
     * has changed over time.
     * The standard offset is the offset before any daylight savings time is applied.
     * This is typically the offset applicable during winter.
     *
     * @param instantProvider  the instant to find the offset information for, not null
     * @return the standard offset, never null
     */
    public abstract ZoneOffset getStandardOffset(InstantProvider instantProvider);

    /**
     * Gets the amount of daylight savings in use for the specified instant in this zone.
     * <p>
     * This provides access to historic information on how the amount of daylight
     * savings has changed over time.
     * This is the difference between the standard offset and the actual offset.
     * It is expressed in hours, minutes and seconds.
     * Typically the amount is zero during winter and one hour during summer.
     *
     * @param instantProvider  the instant to find the offset information for, not null
     * @return the difference between the standard and actual offset, never null
     */
    public Period getDaylightSavings(InstantProvider instantProvider) {
        Instant instant = Instant.of(instantProvider);
        ZoneOffset standardOffset = getStandardOffset(instant);
        ZoneOffset actualOffset = getOffset(instant);
        return actualOffset.toPeriod().minus(standardOffset.toPeriod()).normalized();
    }

    /**
     * Gets the standard offset for the specified instant in this zone.
     * <p>
     * This provides access to historic information on how the standard offset
     * has changed over time.
     * The standard offset is the offset before any daylight savings time is applied.
     * This is typically the offset applicable during winter.
     *
     * @param instant  the instant to find the offset information for, not null
     * @return the standard offset, never null
     */
    public boolean isDaylightSavings(InstantProvider instant) {
        return (getStandardOffset(instant).equals(getOffset(instant)) == false);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next transition after the specified transition.
     * <p>
     * This returns details of the next transition after the specified instant.
     * <p>
     * Some providers of rules may not be able to return this information, thus
     * the method is defined to throw UnsupportedOperationException. The supplied
     * rules implementations do supply this information and don't throw the exception
     *
     * @param instantProvider  the instant to get the next transition after, not null
     * @return the next transition after the specified instant, null if this is after the last transition
     * @throws UnsupportedOperationException if the implementation cannot return this information -
     *  the default 'TZDB' can return this information
     */
    public abstract ZoneOffsetTransition nextTransition(InstantProvider instantProvider);

    /**
     * Gets the previous transition after the specified transition.
     * <p>
     * This returns details of the previous transition after the specified instant.
     * <p>
     * Some providers of rules may not be able to return this information, thus
     * the method is defined to throw UnsupportedOperationException. The supplied
     * rules implementations do supply this information and don't throw the exception
     *
     * @param instantProvider  the instant to get the previous transition after, not null
     * @return the previous transition after the specified instant, null if this is before the first transition
     * @throws UnsupportedOperationException if the implementation cannot return this information -
     *  the default 'TZDB' can return this information
     */
    public abstract ZoneOffsetTransition previousTransition(InstantProvider instantProvider);

    /**
     * Gets the complete list of fully defined transitions.
     * <p>
     * The complete set of transitions for this rules instance is defined by this method
     * and {@link #getTransitionRules()}. This method returns those transitions that have
     * been fully defined. These are typically historical, but may be in the future.
     * The list will be empty for fixed offset rules.
     * <p>
     * Some providers of rules cannot return this information, thus this method is defined
     * to throw UnsupportedOperationException. The supplied 'TZDB' implementation can supply
     * this information thus does not throw the exception.
     *
     * @return independent, modifiable copy of the list of fully defined transitions, never null
     * @throws UnsupportedOperationException if the implementation cannot return this information -
     *  the default 'TZDB' can return this information
     */
    public abstract List<ZoneOffsetTransition> getTransitions();

    /**
     * Gets the list of transition rules for years beyond those defined in the transition list.
     * <p>
     * The complete set of transitions for this rules instance is defined by this method
     * and {@link #getTransitions()}. This method returns instances of {@link ZoneOffsetTransitionRule}
     * that define an algorithm for when transitions will occur.
     * The list will be empty for fixed offset rules.
     * <p>
     * For any given {@code ZoneRules}, this list contains the transition rules for years
     * beyond those years that have been fully defined. These rules typically refer to future
     * daylight savings time rule changes.
     * <p>
     * If the zone defines daylight savings into the future, then the list will normally
     * be of size two and hold information about entering and exiting daylight savings.
     * If the zone does not have daylight savings, or information about future changes
     * is uncertain, then the list will be empty.
     * <p>
     * Some providers of rules cannot return this information, thus this method is defined
     * to throw UnsupportedOperationException. The supplied 'TZDB' implementation can supply
     * this information thus does not throw the exception.
     *
     * @return independent, modifiable copy of the list of transition rules, never null
     * @throws UnsupportedOperationException if the implementation cannot return this information -
     *  the default 'TZDB' can return this information
     */
    public abstract List<ZoneOffsetTransitionRule> getTransitionRules();

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
    public boolean isValidDateTime(OffsetDateTime dateTime) {
        ZoneOffsetInfo info = getOffsetInfo(dateTime.toLocalDateTime());
        return info.isValidOffset(dateTime.getOffset());
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this set of rules equals another.
     * <p>
     * Two rule sets are equal if they will always result in the same output
     * for any given input instant or date-time.
     * Rules from two different groups may return false even if they are in fact the same.
     * <p>
     * This definition should result in implementations comparing their entire state.
     *
     * @param otherRules  the other rules, null returns false
     * @return true if this rules is the same as that specified
     */
    @Override
    public abstract boolean equals(Object otherRules);

    /**
     * Returns a suitable hash code.
     *
     * @return the hash code
     */
    @Override
    public abstract int hashCode();

}
