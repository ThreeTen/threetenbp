/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.ZonedDateTime;
import javax.time.period.Period;

/**
 * A time zone representing the set of rules by which the zone offset
 * varies through the year and historically.
 * <p>
 * TimeZone is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 * It is only intended that the abstract methods are overridden.
 * Subclasses should be Serializable wherever possible.
 *
 * @author Stephen Colebourne
 */
public abstract class ZoneRules {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 93618758758127L;

    //-----------------------------------------------------------------------
    /**
     * Gets the rules for a specific offset.
     *
     * @param offset  the offset to get the fixed rules for, not null
     * @return the rules, never null
     */
    public static ZoneRules fixed(ZoneOffset offset) {
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
     * Gets the offset applicable at the specified instant in this zone.
     * <p>
     * For any given instant there can only ever be one valid offset, which
     * is returned by this method. To access more detailed information about
     * the offset at and around the instant use {@link #getOffsetInfo(Instant)}.
     *
     * @param instant  the instant to find the offset for, not null
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
     * is vital to check {@link OffsetInfo#isDiscontinuity()} to handle the overlap.
     *
     * @param instant  the instant to find the offset information for, not null
     * @return the offset information, never null
     */
    public OffsetInfo getOffsetInfo(Instant instant) {
        ZoneOffset offset = getOffset(instant);
        OffsetDateTime odt = OffsetDateTime.fromInstant(instant, offset);
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
     * {@link OffsetInfo#isDiscontinuity()} to handle the gap or overlap.
     *
     * @param dateTime  the date-time to find the offset information for, not null
     * @return the offset information, never null
     */
    public abstract OffsetInfo getOffsetInfo(LocalDateTime dateTime);

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
     * @return the standard offset, never null
     */
    public Period getDaylightSavings(InstantProvider instantProvider) {
        Instant instant = Instant.instant(instantProvider);
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
     * Is this time zone fixed, such that the offset never varies.
     * <p>
     * It is intended that {@link OffsetDateTime}, {@link OffsetDate} and
     * {@link OffsetTime} are used in preference to fixed offset time zones
     * in {@link ZonedDateTime}.
     * <p>
     * The default implementation returns false and it is not intended that
     * user-supplied subclasses override this.
     *
     * @return true if the time zone is fixed and the offset never changes
     */
    public boolean isFixed() {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the complete list of transitions.
     * <p>
     * This list normally contains a complete historical set of transitions
     * that have occurred. Some transitions may be in the future, although
     * generally the transition rules handle future years.
     *
     * @return true if the time zone is fixed and the offset never changes
     * @throws UnsupportedOperationException if the implementation cannot return this information -
     *  the default 'TZDB' can return this information
     */
    public abstract List<ZoneOffsetTransition> getTransitions();

    /**
     * Gets the list of transition rules for years beyond those defined in the transition list.
     * <p>
     * The list represents all the transitions that are expected in each year
     * beyond those in the transition list. The list size will normally be zero or two.
     * It will never be size one however it could theoretically be greater than two.
     * <p>
     * If the zone defines daylight savings into the future, then the list will normally
     * be of size two and hold information about entering and exiting daylight savings.
     * If the zone does not have daylight savings, or information about future changes
     * is uncertain, then the list will be empty.
     *
     * @return independent, modifiable copy of the list of transition rules, never null
     * @throws UnsupportedOperationException if the implementation cannot return this information -
     *  the default 'TZDB' can return this information
     */
    public abstract List<ZoneOffsetTransitionRule> getTransitionRules();

    //-----------------------------------------------------------------------
    /**
     * Creates an offset info for the normal case where only one offset is valid.
     * <p>
     * This protected method provides the means for subclasses to create instances
     * of {@link OffsetInfo}. This is the only way to create that class.
     *
     * @param dateTime  the date-time that this info applies to, not null
     * @param offset  the zone offset, not null
     * @return the created offset info, never null
     */
    protected OffsetInfo createOffsetInfo(LocalDateTime dateTime, ZoneOffset offset) {
        checkNotNull(dateTime, "LocalDateTime must not be null");
        checkNotNull(offset, "ZoneOffset must not be null");
        return new OffsetInfo(dateTime, offset);
    }

    /**
     * Creates an offset info for a gap, where there are no valid offsets,
     * or an overlap, where there are two valid offsets.
     *
     * @param dateTime  the date-time that this info applies to, not null
     * @param cutoverDateTime  the date-time of the discontinuity using the offset before, not null
     * @param offsetAfter  the offset after the discontinuity, not null
     * @return the created offset info, never null
     */
    protected OffsetInfo createOffsetInfo(
            LocalDateTime dateTime,
            OffsetDateTime cutoverDateTime,
            ZoneOffset offsetAfter) {
        
        checkNotNull(dateTime, "LocalDateTime must not be null");
        checkNotNull(cutoverDateTime, "OffsetDateTime must not be null");
        checkNotNull(offsetAfter, "ZoneOffset must not be null");
        return new OffsetInfo(dateTime, cutoverDateTime, offsetAfter);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this set of rules equals another.
     * <p>
     * Two rule sets are equal if they will always result in the same output
     * for any given input instant or date-time.
     * Rules from two different groups may return false even if they are in fact the same.
     *
     * @param otherRules  the other rules, null returns false
     * @return true if this rules is the same as that specified
     */
    @Override
    public abstract boolean equals(Object otherRules);

    /**
     * A hash code for the rules object.
     *
     * @return a suitable hash code
     */
    @Override
    public abstract int hashCode();

//    //-----------------------------------------------------------------------
//    /**
//     * Information about a discontinuity in the local time-line.
//     * <p>
//     * A discontinuity is normally the result of a daylight savings cutover,
//     * where a gap occurs in spring and an overlap occurs in autumn.
//     * <p>
//     * Discontinuity is immutable and thread-safe.
//     *
//     * @author Stephen Colebourne
//     */
//    public static final class Discontinuity {
//        /** The transition date-time with the offset before the discontinuity. */
//        private final OffsetDateTime transition;
//        /** The offset at and after the discontinuity. */
//        private final ZoneOffset offsetAfter;
//        
//        /**
//         * Constructor.
//         *
//         * @param transition  the transition date-time with the offset before the discontinuity, not null
//         * @param offsetAfter  the offset at and after the discontinuity, not null
//         */
//        private Discontinuity(OffsetDateTime transition, ZoneOffset offsetAfter) {
//            this.transition = transition;
//            this.offsetAfter = offsetAfter;
//        }
//        
//        //-----------------------------------------------------------------------
//        /**
//         * Gets the transition instant.
//         * This is the first instant after the discontinuity, when the new offset applies.
//         *
//         * @return the transition instant, not null
//         */
//        public Instant getTransitionInstant() {
//            return transition.toInstant();
//        }
//        
//        /**
//         * Gets the transition date-time expressed with the before offset.
//         * This is the date-time where the discontinuity begins, and as such it never
//         * actually occurs.
//         *
//         * @return the transition date-time expressed with the before offset, not null
//         */
//        public OffsetDateTime getTransitionDateTime() {
//            return transition;
//        }
//        
//        /**
//         * Gets the transition date-time expressed with the after offset.
//         * This is the first date-time after the discontinuity, when the new offset applies.
//         *
//         * @return the transition date-time expressed with the after offset, not null
//         */
//        public OffsetDateTime getTransitionDateTimeAfter() {
//            return transition.withOffsetSameInstant(offsetAfter);
//        }
//        
//        /**
//         * Gets the offset before the gap.
//         *
//         * @return the offset before the gap, not null
//         */
//        public ZoneOffset getOffsetBefore() {
//            return transition.getOffset();
//        }
//        
//        /**
//         * Gets the offset after the gap.
//         *
//         * @return the offset after the gap, not null
//         */
//        public ZoneOffset getOffsetAfter() {
//            return offsetAfter;
//        }
//        
//        /**
//         * Gets the size of the discontinuity in seconds.
//         *
//         * @return the size of the discontinuity in seconds, positive for gaps, negative for overlaps
//         */
//        public Period getDiscontinuitySize() {
//            int secs = getOffsetAfter().getAmountSeconds() - getOffsetBefore().getAmountSeconds();
//            return Period.seconds(secs).normalized();
//        }
//        
//        /**
//         * Does this discontinuity represent a gap in the local time-line.
//         *
//         * @return true if this discontinuity is a gap
//         */
//        public boolean isGap() {
//            return getOffsetAfter().getAmountSeconds() > getOffsetBefore().getAmountSeconds();
//        }
//        
//        /**
//         * Does this discontinuity represent a gap in the local time-line.
//         *
//         * @return true if this discontinuity is an overlap
//         */
//        public boolean isOverlap() {
//            return getOffsetAfter().getAmountSeconds() < getOffsetBefore().getAmountSeconds();
//        }
//        
////        /**
////         * Checks if the specified offset is one of those described by this discontinuity.
////         *
////         * @param offset  the offset to check, null returns false
////         * @return true if the offset is one of those described by this discontinuity
////         */
////        public boolean containsOffset(ZoneOffset offset) {
////            return offsetBefore.equals(offset) || offsetAfter.equals(offset);
////        }
//        
//        /**
//         * Checks if the specified offset is valid during this discontinuity.
//         * A gap will always return false.
//         * An overlap will return true if the offset is either the before or after offset.
//         *
//         * @param offset  the offset to check, null returns false
//         * @return true if the offset is valid during the discontinuity
//         */
//        public boolean isValidOffset(ZoneOffset offset) {
//            return isGap() ? false : (getOffsetBefore().equals(offset) || getOffsetAfter().equals(offset));
//        }
//        
//        //-----------------------------------------------------------------------
//        /**
//         * Checks if this instance equals another.
//         *
//         * @param other  the other object to compare to, null returns false
//         * @return true if equal
//         */
//        @Override
//        public boolean equals(Object other) {
//            if (other == this) {
//                return true;
//            }
//            if (other instanceof Discontinuity) {
//                Discontinuity d = (Discontinuity) other;
//                return transition.equals(d.transition) &&
//                    offsetAfter.equals(d.offsetAfter);
//            }
//            return false;
//        }
//        
//        /**
//         * Gets the hash code.
//         *
//         * @return the hash code
//         */
//        @Override
//        public int hashCode() {
//            return transition.hashCode() ^ offsetAfter.hashCode();
//        }
//        
//        /**
//         * Gets a string describing this object.
//         *
//         * @return a string for debugging, never null
//         */
//        @Override
//        public String toString() {
//            StringBuilder buf = new StringBuilder();
//            buf.append("Discontinuity[")
//                .append(isGap() ? "Gap" : "Overlap")
//                .append(" at ")
//                .append(transition)
//                .append(" to ")
//                .append(offsetAfter)
//                .append(']');
//            return buf.toString();
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Information about the valid offsets applicable for a local date-time.
     * <p>
     * The mapping from a local date-time to an offset is not straightforward.
     * There are three cases:
     * <ul>
     * <li>Normal. Where there is a single offset for the local date-time.</li>
     * <li>Gap. Where there is a gap in the local time-line normally caused by the
     * spring cutover to daylight savings. There are no valid offsets within the gap</li>
     * <li>Overlap. Where there is a gap in the local time-line normally caused by the
     * autumn cutover from daylight savings. There are two valid offsets during the overlap.</li>
     * </ul>
     * When using this class, it is vital to check the {@link #isDiscontinuity()}
     * method to handle the gap and overlap. Alternatively use one of the general
     * methods {@link #getEstimatedOffset()} or {@link #isValidOffset(ZoneOffset)}.
     * <p>
     * OffsetInfo is immutable and thread-safe.
     *
     * @author Stephen Colebourne
     */
    public static class OffsetInfo {
        /** The date-time that this info applies to. */
        private final LocalDateTime dateTime;
        /** The offset for the local time-line. */
        private final ZoneOffset offset;
        /** The discontinuity in the local time-line. */
        private final ZoneOffsetTransition discontinuity;
        
        /**
         * Constructor for handling a simple single offset.
         *
         * @param dateTime  the date-time that this info applies to, not null
         * @param offset  the offset applicable at the date-time, not null
         */
        OffsetInfo(
                LocalDateTime dateTime,
                ZoneOffset offset) {
            this.dateTime = dateTime;
            this.offset = offset;
            this.discontinuity = null;
        }
        
        /**
         * Constructor for handling a discontinuity.
         *
         * @param dateTime  the date-time that this info applies to, not null
         * @param cutoverDateTime  the date-time of the cutover with the offset before, not null
         * @param offsetAfter  the offset applicable after the cutover gap/overlap, not null
         */
        OffsetInfo(
                LocalDateTime dateTime,
                OffsetDateTime cutoverDateTime,
                ZoneOffset offsetAfter) {
            this.dateTime = dateTime;
            this.offset = null;
            this.discontinuity = new ZoneOffsetTransition(cutoverDateTime, offsetAfter);
        }
        
        //-----------------------------------------------------------------------
        /**
         * Gets the local date-time that this info is applicable to.
         *
         * @return true if there is no valid offset
         */
        public LocalDateTime getLocalDateTime() {
            return dateTime;
        }
        
        /**
         * Is the offset information for the local date-time a discontinuity.
         * A discontinuity may be a gap or overlap and is normally caused by
         * daylight savings cutover.
         *
         * @return true if there is a discontinuity in the local time-line
         */
        public boolean isDiscontinuity() {
            return discontinuity != null;
        }
        
        /**
         * Gets the offset applicable at this point on the local time-line.
         * This method is intended for use when {@link #isDiscontinuity()} returns false.
         *
         * @return the offset applicable when there is not a discontinuity in the
         *  local-time line, null if it is a discontinuity
         */
        public ZoneOffset getOffset() {
            return offset;
        }
        
        /**
         * Gets information about any discontinuity in the local time-line.
         * This method should only be called after calling {@link #isDiscontinuity()}.
         *
         * @return the discontinuity in the local-time line, null if not a discontinuity
         */
        public ZoneOffsetTransition getDiscontinuity() {
            return discontinuity;
        }
        
        //-----------------------------------------------------------------------
        /**
         * Gets an estimated offset for the local date-time.
         * <p>
         * The result will be the same as {@link #getOffset()} except during a discontinuity.
         * During a discontinuity, the value of {@link Discontinuity#getOffsetAfter()} will
         * be returned. How meaningful that offset is depends on your application.
         *
         * @return a suitable estimated offset, never null
         */
        public ZoneOffset getEstimatedOffset() {
            return isDiscontinuity() ? getDiscontinuity().getOffsetAfter() : offset;
        }
        
        /**
         * Checks if the specified offset is valid for this discontinuity.
         *
         * @param offset  the offset to check, null returns false
         * @return true if the offset is one of those described by this discontinuity
         */
        public boolean isValidOffset(ZoneOffset offset) {
            return isDiscontinuity() ? discontinuity.isValidOffset(offset) : this.offset.equals(offset);
        }
        
        //-----------------------------------------------------------------------
        /**
         * Gets a string describing this object.
         *
         * @return a string for debugging, never null
         */
        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("OffsetInfo[")
                .append(isDiscontinuity() ? discontinuity : offset)
                .append(']');
            return buf.toString();
        }
    }

}
