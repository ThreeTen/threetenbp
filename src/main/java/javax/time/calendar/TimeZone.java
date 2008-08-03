/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.Instant;
import javax.time.calendar.field.DayOfWeek;

/**
 * A time zone representing the set of rules by which the zone offset
 * varies through the year and historically.
 * <p>
 * All supplied subclasses of TimeZone are thread-safe and immutable.
 * Other subclasses of TimeZone should be immutable but this cannot be enforced.
 *
 * @author Stephen Colebourne
 */
public abstract class TimeZone implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 93618758758127L;
    /**
     * Cache of time zones by id.
     */
    private static final ConcurrentMap<String, TimeZone> CACHE = new ConcurrentHashMap<String, TimeZone>();
    /**
     * The time zone offset for UTC, with an id of 'UTC'.
     */
    public static final TimeZone UTC = timeZone(ZoneOffset.UTC);

    /**
     * The time zone ID.
     */
    private final String timeZoneID;

    /**
     * Obtains an instance of <code>TimeZone</code> using its ID.
     *
     * @param timeZoneID  the time zone id, not null
     * @return the TimeZone, never null
     */
    public static TimeZone timeZone(String timeZoneID) {
        TimeZone zone = CACHE.get(timeZoneID);
        if (zone == null) {
            if (timeZoneID.startsWith("UTC") || timeZoneID.startsWith("GMT")) {  // not sure about GMT
                // 'UTC' will have been dealy with by the cache
                return timeZone(ZoneOffset.zoneOffset(timeZoneID.substring(3)));
            } else {
                Map<String, Integer> map = new HashMap<String, Integer>();
                map.put("Europe/Dublin", 0);
                map.put("Europe/Lisbon", 0);
                map.put("Europe/London", 0);
                map.put("Europe/Amsterdam", 1);
                map.put("Europe/Andorra", 1);
                map.put("Europe/Belgrade", 1);
                map.put("Europe/Ljubljana", 1);
                map.put("Europe/Podgorica", 1);
                map.put("Europe/Sarajevo", 1);
                map.put("Europe/Skopje", 1);
                map.put("Europe/Zagreb", 1);
                map.put("Europe/Berlin", 1);
                map.put("Europe/Brussels", 1);
                map.put("Europe/Budapest", 1);
                map.put("Europe/Copenhagen", 1);
                map.put("Europe/Gibraltar", 1);
                map.put("Europe/Luxembourg", 1);
                map.put("Europe/Madrid", 1);
                map.put("Europe/Malta", 1);
                map.put("Europe/Monaco", 1);
                map.put("Europe/Oslo", 1);
                map.put("Europe/Paris", 1);
                map.put("Europe/Prague", 1);
                map.put("Europe/Bratislava", 1);
                map.put("Europe/Rome", 1);
                map.put("Europe/San_Marino", 1);
                map.put("Europe/Vatican", 1);
                map.put("Europe/Stockholm", 1);
                map.put("Europe/Tirane", 1);
                map.put("Europe/Vaduz", 1);
                map.put("Europe/Vienna", 1);
                map.put("Europe/Warsaw", 1);
                map.put("Europe/Zurich", 1);
                map.put("Europe/Athens", 2);
                map.put("Europe/Bucharest", 2);
                map.put("Europe/Chisinau", 2);
                map.put("Europe/Helsinki", 2);
                map.put("Asia/Istanbul", 2);
                map.put("Europe/Istanbul", 2);
                map.put("Europe/Kaliningrad", 2);
                map.put("Europe/Kiev", 2);
                map.put("Europe/Minsk", 2);
                map.put("Europe/Riga", 2);
                map.put("Europe/Sofia", 2);
                map.put("Europe/Tallinn", 2);
                map.put("Europe/Vilnius", 2);
                Integer standardOffset = map.get(timeZoneID);
                if (standardOffset != null) {
                    zone = new EuropeZone(timeZoneID, standardOffset);
                } else {
                    zone = UTC;
                }
                TimeZone cached = CACHE.putIfAbsent(timeZoneID, zone);
                zone = (cached != null ? cached : zone);
            }
        }
        return zone;
    }

    /**
     * Obtains an instance of <code>TimeZone</code> using an offset.
     *
     * @param offset  the zone offset, not null
     * @return the TimeZone for the offset, never null
     */
    public static TimeZone timeZone(ZoneOffset offset) {
        String timeZoneID = (offset == ZoneOffset.UTC ? "UTC" : "UTC" + offset.getID());
        TimeZone zone = CACHE.get(timeZoneID);
        if (zone == null) {
            zone = new Fixed(timeZoneID, offset);
            TimeZone cached = CACHE.putIfAbsent(timeZoneID, zone);
            zone = (cached != null ? cached : zone);
        }
        return zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using the time zone ID.
     *
     * @param timeZoneID  the time zone id, not null
     */
    private TimeZone(String timeZoneID) {
        super();
        this.timeZoneID = timeZoneID;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time zone ID.
     *
     * @return the time zone ID, never null
     */
    public String getID() {
        return timeZoneID;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the time zone.
     *
     * @return the time zone name, never null
     */
    public String getName() {
        return timeZoneID;
    }

    /**
     * Gets the short name of the time zone.
     *
     * @return the time zone short name, never null
     */
    public String getShortName() {
        return timeZoneID;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the offset applicable at the specified instant.
     *
     * @param instant  the instant to find the offset for, not null
     * @return the offset, never null
     */
    public abstract ZoneOffset getOffset(Instant instant);

    /**
     * Gets the offset information for a local date-time.
     * <p>
     * This method can return one of two classes. It will return either
     * a <code>ZoneOffset</code> or a <code>TimeZone.Discontinuity</code>.
     * This has to be checked using instanceof.
     *
     * @param dateTime  the date-time to find the offset for, not null
     * @return the offset information, never null
     */
    public abstract OffsetInfo getOffsetInfo(LocalDateTime dateTime);

    /**
     * Is this time zone fixed, such that the offset never varies.
     *
     * @return true if the time zone is fixed and the offset never changes
     */
    public abstract boolean isFixed();

//    //-----------------------------------------------------------------------
//    /**
//     * Creates an offset info for the normal case where only one offset is valid.
//     *
//     * @param dateTime  the date-time that this info applies to, not null
//     * @param offset  the zone offset, not null
//     * @return the created offset info, never null
//     */
//    protected OffsetInfo createOffsetInfo(LocalDateTime dateTime, ZoneOffset offset) {
//        return new OffsetInfo(dateTime, 0, 0, Collections.singletonList(offset));
//    }
//
//    /**
//     * Constructor for a gap where there are no valid offsets.
//     *
//     * @param dateTime  the date-time that this info applies to, not null
//     * @param discontinuityPositionSeconds  the position in the discontinuity, seconds
//     * @param discontinuityTotalSeconds  the total size of the discontinuity, seconds
//     * @return the created offset info, never null
//     */
//    protected OffsetInfo createOffsetInfo(
//            LocalDateTime dateTime, int discontinuityPositionSeconds, int discontinuityTotalSeconds) {
//        return new OffsetInfo(dateTime, discontinuityPositionSeconds, discontinuityTotalSeconds,
//                Collections.<ZoneOffset>emptyList());
//    }
//
//    /**
//     * Constructor for an overlap, where there are two or more valid offsets.
//     * <p>
//     * It is very rare, but still feasible, for there to be more than two
//     * offsets. In this case, the discontinuity amount represents the total
//     * size of the discontinuity.
//     *
//     * @param dateTime  the date-time that this info applies to, not null
//     * @param discontinuityPositionSeconds  the position in the discontinuity, seconds
//     * @param discontinuityTotalSeconds  the total size of the discontinuity, seconds
//     * @param offsets  the zone offsets, not null
//     * @return the created offset info, never null
//     */
//    protected OffsetInfo createOffsetInfo(
//            LocalDateTime dateTime, int discontinuityPositionSeconds, int discontinuityTotalSeconds, ZoneOffset... offsets) {
//        return new OffsetInfo(dateTime, discontinuityPositionSeconds, discontinuityTotalSeconds,
//                Collections.<ZoneOffset>unmodifiableList(Arrays.asList(offsets)));
//    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified by comparing the ID.
     *
     * @param otherZone  the other zone, null returns false
     * @return true if this zone is the same as that specified
     */
    @Override
    public boolean equals(Object otherZone) {
        if (this == otherZone) {
           return true;
        }
        if (otherZone instanceof TimeZone) {
            return timeZoneID.equals(((TimeZone) otherZone).timeZoneID);
        }
        return false;
    }

    /**
     * A hashcode for the time zone object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return timeZoneID.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the time zone using the ID.
     *
     * @return the time zone ID, never null
     */
    @Override
    public String toString() {
        return timeZoneID;
    }

    //-----------------------------------------------------------------------
    /**
     * Information about a discontinuity in the local time-line.
     * This is typically caused by daylight savings cutover, either a gap in
     * spring, or an overlap in autumn.
     * <p>
     * Discontinuity is thread-safe and immutable.
     *
     * @author Stephen Colebourne
     */
    public static final class Discontinuity implements OffsetInfo {
        /** The transition instant. */
        private final Instant transition;
        /** The offset before the discontinuity. */
        private final ZoneOffset offsetBefore;
        /** The offset at and after the discontinuity. */
        private final ZoneOffset offsetAfter;
        /** The discontinuity in seconds. */
        private final int discontinuitySeconds;
        
        /**
         * Constructor for a gap where there are no valid offsets.
         *
         * @param transition  the transition instant, not null
         * @param offsetBefore  the offset before the discontinuity, not null
         * @param offsetAfter  the offset at and after the discontinuity, not null
         */
        private Discontinuity(Instant transition, ZoneOffset offsetBefore, ZoneOffset offsetAfter) {
            this.transition = transition;
            this.offsetBefore = offsetBefore;
            this.offsetAfter = offsetAfter;
            this.discontinuitySeconds = offsetAfter.getAmountSeconds() - offsetBefore.getAmountSeconds();
        }
        
        //-----------------------------------------------------------------------
        /**
         * Gets the transition instant.
         * This is the first instant after the gap.
         *
         * @return the transition instant, not null
         */
        public Instant getTransition() {
            return transition;
        }
        
        /**
         * Gets the offset before the gap.
         *
         * @return the offset before the gap, not null
         */
        public ZoneOffset getOffsetBefore() {
            return offsetBefore;
        }
        
        /**
         * Gets the offset after the gap.
         *
         * @return the offset after the gap, not null
         */
        public ZoneOffset getOffsetAfter() {
            return offsetAfter;
        }
        
        /**
         * Gets the size of the discontinuity in seconds.
         *
         * @return the size of the discontinuity in seconds, positive for gaps, negative for overlaps
         */
        public int getDiscontinuitySize() {
            return discontinuitySeconds;
        }
        
        /**
         * Does this discontinuity represent a gap in the local time-line.
         *
         * @return true if this discontinuity is a gap
         */
        public boolean isGap() {
            return getDiscontinuitySize() > 0;
        }
        
        /**
         * Does this discontinuity represent a gap in the local time-line.
         *
         * @return true if this discontinuity is an overlap
         */
        public boolean isOverlap() {
            return getDiscontinuitySize() < 0;
        }
        
        /**
         * Checks if the specified offset is one of those described by this
         * discontinuity.
         *
         * @param offset  the offset to check, null returns false
         * @return true if the offset is one of those described by this discontinuity
         */
        public boolean containsOffset(ZoneOffset offset) {
            return offsetBefore.equals(offset) || offsetAfter.equals(offset);
        }
        
        /**
         * Checks if this instance equals another.
         *
         * @param other  the other object to compare to, null returns false
         * @return true if equal
         */
        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof Discontinuity) {
                Discontinuity d = (Discontinuity) other;
                return transition.equals(d.transition) &&
                    offsetBefore.equals(d.offsetBefore) &&
                    offsetAfter.equals(d.offsetAfter);
            }
            return false;
        }
        
        /**
         * Gets the hash code.
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            return transition.hashCode() ^ offsetBefore.hashCode() ^ offsetAfter.hashCode();
        }
        
        /**
         * Gets a string describing this object.
         *
         * @return a string for debugging, never null
         */
        @Override
        public String toString() {
            return "Discontinuity from " + offsetBefore + " to " + offsetAfter;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Marker interface for classes that describe the local time-line.
     * <p>
     * OffsetInfo is thread-safe and immutable.
     *
     * @author Stephen Colebourne
     */
    public static interface OffsetInfo {
        // TODO: Perhaps need a BetweenDiscontinuity class to wrap the offset
        // and define the valid range
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Information about the offsets applicable for a local date-time.
//     * <p>
//     * OffsetInfo is thread-safe and immutable.
//     *
//     * @author Stephen Colebourne
//     */
//    public static class OffsetInfo {
//        /** The date-time that this info applies to. */
//        private final LocalDateTime dateTime;
//        /** The valid offsets. */
//        private final List<ZoneOffset> validOffsets;
//        /** The position in the discontinuity of the date-time in seconds. */
//        private final int discontinuityPositionSeconds;
//        /** The total size of the discontinuity in seconds. */
//        private final int discontinuitySizeSeconds;
//        
//        /**
//         * Constructor for a gap where there are no valid offsets.
//         *
//         * @param dateTime  the date-time that this info applies to, not null
//         * @param discontinuityPositionSeconds  the position in the discontinuity, seconds
//         * @param discontinuityTotalSeconds  the total size of the discontinuity, seconds
//         * @param offsetsUnmodifiable  the valid offsets, unmodifiable, not null
//         */
//        private OffsetInfo(
//                LocalDateTime dateTime,
//                int discontinuityPositionSeconds,
//                int discontinuityTotalSeconds,
//                List<ZoneOffset> offsetsUnmodifiable) {
//            this.dateTime = dateTime;
//            this.discontinuityPositionSeconds = discontinuityPositionSeconds;
//            this.discontinuitySizeSeconds = discontinuityTotalSeconds;
//            this.validOffsets = offsetsUnmodifiable;
//        }
//        
//        //-----------------------------------------------------------------------
//        /**
//         * Gets the local date-time that this info is applicable to.
//         *
//         * @return true if there is no valid offset
//         */
//        public LocalDateTime getLocalDateTime() {
//            return dateTime;
//        }
//        
//        /**
//         * Is there a gap for this local date-time.
//         *
//         * @return true if there is no valid offset
//         */
//        public boolean isGap() {
//            return validOffsets.isEmpty();
//        }
//        
//        /**
//         * Is there an overlap for this local date-time.
//         *
//         * @return true if there is more than one valid offset
//         */
//        public boolean isOverlap() {
//            return validOffsets.size() > 1;
//        }
//        
//        /**
//         * Checks if the zone offset is valid for this local date-time.
//         *
//         * @param offset  the zone offset to check, not null
//         * @return the list of offsets from earliest to latest, never null
//         */
//        public boolean isValidOffset(ZoneOffset offset) {
//            return validOffsets.contains(offset);
//        }
//        
//        /**
//         * Gets the list of offsets in order from earliest to latest.
//         * The returned list is unmodifiable.
//         *
//         * @return the list of offsets from earliest to latest, never null
//         */
//        public List<ZoneOffset> getOffsets() {
//            return validOffsets;
//        }
//        
//        /**
//         * Gets the amount in seconds that the local date-time is within the
//         * discontinuity.
//         *
//         * @return the discontinuity position in seconds
//         */
//        public int getDiscontinuityPositionSeconds() {
//            return discontinuityPositionSeconds;
//        }
//        
//        /**
//         * Gets the total size in seconds of the discontinuity.
//         *
//         * @return the discontinuity total size in seconds
//         */
//        public int getDiscontinuitySizeInSeconds() {
//            return discontinuitySizeSeconds;
//        }
//        
//        /**
//         * Gets the local date-time that this info is applicable to.
//         *
//         * @return true if there is no valid offset
//         */
//        public OffsetDateTime getEarliestValidDateTime() {
//            if (isOverlap()) {
//                return OffsetDateTime.dateTime(dateTime, validOffsets.get(0));
//            }
//            if (isGap()) {
//                return OffsetDateTime.dateTime(dateTime, validOffsets.get(0));
//            }
//            return ;
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of time zone for fixed offsets.
     */
    private static final class Fixed extends TimeZone {
        /** The fixed offset. */
        private final ZoneOffset offset;
        /**
         * Constructor.
         * @param id  the time zone id, not null
         * @param offset  the zone offset, not null
         */
        private Fixed(String id, ZoneOffset offset) {
            super(id);
            this.offset = offset;
        }
        /**
         * Resolves singletons.
         * @return the singleton instance
         */
        private Object readResolve() {
            return TimeZone.timeZone(getID());
        }
        /** {@inheritDoc} */
        @Override
        public ZoneOffset getOffset(Instant instant) {
            return offset;
        }
        /** {@inheritDoc} */
        @Override
        public OffsetInfo getOffsetInfo(LocalDateTime dateTime) {
            return offset;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isFixed() {
            return true;
        }
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Implementation of time zone based on java.util.TimeZone.
//     */
//    private static final class UtilZone extends TimeZone {
//        /** The fixed offset. */
//        private final java.util.TimeZone utilZone;
//        /**
//         * Constructor.
//         * @param id  the time zone id, not null
//         * @param utilZone  the java.util.TimeZone instance, not null
//         */
//        private UtilZone(String id, java.util.TimeZone utilZone) {
//            super(id);
//            this.utilZone = utilZone;
//        }
//        /**
//         * Resolves singletons.
//         * @return the singleton instance
//         */
//        private Object readResolve() {
//            return TimeZone.timeZone(getID());
//        }
//        /** {@inheritDoc} */
//        @Override
//        public ZoneOffset getOffset(Instant instant) {
//            int offsetMillis = utilZone.getOffset(instant.toEpochMillis());
//            return ZoneOffset.forTotalSeconds(offsetMillis / 1000);
//        }
//        /** {@inheritDoc} */
//        @Override
//        public OffsetInfo getOffsetInfo(LocalDateTime dateTime) {
////            // TODO: better algorithm / overflows
////            long wallMillis = dateTime.toLocalDate().toModifiedJulianDays() * 24 * 60 * 60 * 1000 +
////                    dateTime.toLocalTime().toMilliOfDay();
////            int offsetMillis = utilZone.getOffset(wallMillis);
////            long millis = wallMillis - offsetMillis;
////            return ZoneOffset.forTotalSeconds(offsetMillis / 1000);
//            int offsetMillis = utilZone.getOffset(
//                    GregorianCalendar.BC,
//                    dateTime.getYear().getValue(),
//                    dateTime.getMonthOfYear().getValue() - 1,
//                    dateTime.getDayOfMonth().getValue(),
//                    Calendar.SUNDAY,
//                    dateTime.toLocalTime().toMilliOfDay());
//            return ZoneOffset.forTotalSeconds(offsetMillis / 1000);
//        }
//        /** {@inheritDoc} */
//        @Override
//        public boolean isFixed() {
//            return utilZone.useDaylightTime();
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of time zone based on java.util.TimeZone.
     */
    private static final class EuropeZone extends TimeZone {
        /** The standard offset in hours. */
        private final int standardOffset;
        /**
         * Constructor.
         * @param id  the time zone id, not null
         * @param utilZone  the java.util.TimeZone instance, not null
         */
        private EuropeZone(String id, int standardOffset) {
            super(id);
            this.standardOffset = standardOffset;
        }
        /**
         * Resolves singletons.
         * @return the singleton instance
         */
        private Object readResolve() {
            return TimeZone.timeZone(getID());
        }
        /** {@inheritDoc} */
        @Override
        public ZoneOffset getOffset(Instant instant) {
            OffsetDateTime dt = OffsetDateTime.dateTime(instant, ZoneOffset.UTC);
            int offsetBefore = standardOffset;
            int offsetAfter = standardOffset;
            switch (dt.getMonthOfYear()) {
                case JANUARY:
                case FEBRUARY:
                case NOVEMBER:
                case DECEMBER:
                    return ZoneOffset.UTC;
                case MARCH:
                    offsetAfter += 1;
                    break;
                case APRIL:
                case MAY:
                case JUNE:
                case JULY:
                case AUGUST:
                case SEPTEMBER:
                    return ZoneOffset.zoneOffset("+01:00");
                case OCTOBER:
                    offsetBefore += 1;
                    break;
            }
            int dom = dt.getDayOfMonth().getValue();
            if (dom < 25) {
                return ZoneOffset.zoneOffset(offsetBefore);
            }
            if (dt.getDayOfWeek() == DayOfWeek.SUNDAY) {
                OffsetDateTime cutover = OffsetDateTime.dateTime(dt.toLocalDate(), LocalTime.time(1, 0), ZoneOffset.UTC);
                return dt.isBefore(cutover) ? ZoneOffset.zoneOffset(offsetBefore) : ZoneOffset.zoneOffset(offsetAfter);
            }
            int daysToSun = 7 - dt.getDayOfWeek().getValue();
            return dom + daysToSun <= 31 ? ZoneOffset.zoneOffset(offsetBefore) : ZoneOffset.zoneOffset(offsetAfter);
        }
        /** {@inheritDoc} */
        @Override
        public OffsetInfo getOffsetInfo(LocalDateTime dt) {
            int offsetBefore = standardOffset;
            int offsetAfter = standardOffset;
            switch (dt.getMonthOfYear()) {
                case JANUARY:
                case FEBRUARY:
                case NOVEMBER:
                case DECEMBER:
                    return ZoneOffset.UTC;
                case MARCH:
                    offsetAfter += 1;
                    break;
                case APRIL:
                case MAY:
                case JUNE:
                case JULY:
                case AUGUST:
                case SEPTEMBER:
                    return ZoneOffset.zoneOffset("+01:00");
                case OCTOBER:
                    offsetBefore += 1;
                    break;
            }
            int dom = dt.getDayOfMonth().getValue();
            if (dom < 25) {
                return ZoneOffset.zoneOffset(offsetBefore);
            }
            if (dt.getDayOfWeek() == DayOfWeek.SUNDAY) {
                if (dt.getHourOfDay().getValue() < 1 + standardOffset) {
                    return ZoneOffset.zoneOffset(offsetBefore);
                }
                if (dt.getHourOfDay().getValue() >= 2 + standardOffset) {
                    return ZoneOffset.zoneOffset(offsetAfter);
                }
                OffsetDateTime cutover = OffsetDateTime.dateTime(dt.toLocalDate(), LocalTime.time(1, 0), ZoneOffset.UTC);
                return new Discontinuity(cutover.toInstant(), ZoneOffset.zoneOffset(offsetBefore), ZoneOffset.zoneOffset(offsetAfter));
            }
            int daysToSun = 7 - dt.getDayOfWeek().getValue();
            return dom + daysToSun <= 31 ? ZoneOffset.zoneOffset(offsetBefore) : ZoneOffset.zoneOffset(offsetAfter);
        }
        /** {@inheritDoc} */
        @Override
        public boolean isFixed() {
            return false;
        }
    }

}
