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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.time.CalendricalException;

/**
 * A time-zone offset from UTC, such as '+02:00'.
 * <p>
 * A time-zone offset is the period of time that a time-zone differs from UTC.
 * This is usually a fixed number of hours and minutes.
 * <p>
 * Different parts of the world have different time-zone offsets.
 * The rules for how offsets vary by place and time of year are captured in the
 * {@link TimeZone} class.
 * <p>
 * For example, Paris is one hours ahead of UTC in winter and two hours ahead in
 * summer. The {@code TimeZone} instance for Paris will reference two
 * {@code ZoneOffset} instances - a {@code +01:00} instance for winter,
 * and a {@code +02:00} instance for summer.
 * <p>
 * In 2008, time-zone offsets around the world extended from -12:00 to +14:00.
 * To prevent any problems with that range being extended, yet still provide
 * validation, the range of offsets is restricted to -18:00 to 18:00 inclusive.
 * <p>
 * This class is designed primarily for use with the {@link ISOChronology}.
 * The fields of hours, minutes and seconds make assumptions that are valid for the
 * standard ISO definitions of those fields. This class may be used with other
 * calendar systems providing the definition of the time fields matches those
 * of the ISO calendar system.
 * <p>
 * Instances of ZoneOffset must be compared using {@link #equals}.
 * Implementations may choose to cache certain common offsets, however
 * applications must not rely on such caching.
 * <p>
 * ZoneOffset is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ZoneOffset
        implements Calendrical, Comparable<ZoneOffset>, Serializable {

    /** Cache of time-zone offset by offset in seconds. */
    private static final ReadWriteLock CACHE_LOCK = new ReentrantReadWriteLock();
    /** Cache of time-zone offset by offset in seconds. */
    private static final Map<Integer, ZoneOffset> SECONDS_CACHE = new HashMap<Integer, ZoneOffset>();
    /** Cache of time-zone offset by id. */
    private static final Map<String, ZoneOffset> ID_CACHE = new HashMap<String, ZoneOffset>();

    /**
     * The time-zone offset for UTC, with an id of 'Z'.
     */
    public static final ZoneOffset UTC = ofHoursMinutesSeconds(0, 0, 0);
    /**
     * The number of seconds per hour.
     */
    private static final int SECONDS_PER_HOUR = 60 * 60;
    /**
     * The number of seconds per minute.
     */
    private static final int SECONDS_PER_MINUTE = 60;
    /**
     * The number of minutes per hour.
     */
    private static final int MINUTES_PER_HOUR = 60;
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The offset in seconds.
     */
    private final int amountSeconds;
    /**
     * The string form of the time-zone offset.
     */
    private final transient String id;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneOffset} using the id.
     * <p>
     * This method parses the string id of a {@code ZoneOffset} to
     * return an instance. The parsing accepts all the formats generated by
     * {@link #getID()}, plus some additional formats:
     * <ul>
     * <li>{@code Z} - for UTC
     * <li>{@code +hh:mm}
     * <li>{@code -hh:mm}
     * <li>{@code +hhmm}
     * <li>{@code -hhmm}
     * <li>{@code +hh:mm:ss}
     * <li>{@code -hh:mm:ss}
     * <li>{@code +hhmmss}
     * <li>{@code -hhmmss}
     * </ul>
     * Note that &plusmn; means either the plus or minus symbol.
     * <p>
     * The ID of the returned offset will be normalized to one of the formats
     * described by {@link #getID()}.
     * <p>
     * The maximum supported range is from +18:00 to -18:00 inclusive.
     *
     * @param offsetID  the offset id, not null
     * @return the ZoneOffset, never null
     * @throws IllegalArgumentException if the offset id is invalid
     */
    public static ZoneOffset of(String offsetID) {
        if (offsetID == null) {
            throw new NullPointerException("The offset ID must not be null");
        }
        CACHE_LOCK.readLock().lock();
        try {
            ZoneOffset offset = ID_CACHE.get(offsetID);
            if (offset != null) {
                return offset;
            }
        } finally {
            CACHE_LOCK.readLock().unlock();
        }
        
//        // parse - Z, +hh, +hhmm, +hh:mm, +hhmmss, +hh:mm:ss
//        if (offsetID.equals("Z")) {
//            return UTC;
//        }
        final int hours, minutes, seconds;
        int len = offsetID.length();
        switch (len) {
            case 3:
                hours = parseNumber(offsetID, 1, false);
                minutes = 0;
                seconds = 0;
                break;
            case 5:
                hours = parseNumber(offsetID, 1, false);
                minutes = parseNumber(offsetID, 3, false);
                seconds = 0;
                break;
            case 6:
                hours = parseNumber(offsetID, 1, false);
                minutes = parseNumber(offsetID, 4, true);
                seconds = 0;
                break;
            case 7:
                hours = parseNumber(offsetID, 1, false);
                minutes = parseNumber(offsetID, 3, false);
                seconds = parseNumber(offsetID, 5, false);
                break;
            case 9:
                hours = parseNumber(offsetID, 1, false);
                minutes = parseNumber(offsetID, 4, true);
                seconds = parseNumber(offsetID, 7, true);
                break;
            default:
                throw new IllegalArgumentException("Zone offset id '" + offsetID + "' is invalid");
        }
        char first = offsetID.charAt(0);
        if (first != '+' && first != '-') {
            throw new IllegalArgumentException("Zone offset id '" + offsetID + "' is invalid: Plus/minus not found when expected");
        }
        if (first == '-') {
            return ofHoursMinutesSeconds(-hours, -minutes, -seconds);
        } else {
            return ofHoursMinutesSeconds(hours, minutes, seconds);
        }
    }

    /**
     * Parse a two digit zero-prefixed number.
     *
     * @param offsetID  the offset id, not null
     * @param pos  the position to parse, valid
     * @param precededByColon  should this number be prefixed by a precededByColon
     * @return the parsed number, from 0 to 99
     */
    private static int parseNumber(String offsetID, int pos, boolean precededByColon) {
        if (precededByColon && offsetID.charAt(pos - 1) != ':') {
            throw new IllegalArgumentException("Zone offset id '" + offsetID + "' is invalid: Colon not found when expected");
        }
        char ch1 = offsetID.charAt(pos);
        char ch2 = offsetID.charAt(pos + 1);
        if (ch1 < '0' || ch1 > '9' || ch2 < '0' || ch2 > '9') {
            throw new IllegalArgumentException("Zone offset id '" + offsetID + "' is invalid: Non numeric characters found");
        }
        return (ch1 - 48) * 10 + (ch2 - 48);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneOffset} using an offset in hours.
     *
     * @param hours  the time-zone offset in hours, from -18 to +18
     * @return the ZoneOffset, never null
     * @throws IllegalArgumentException if the offset is not in the required range
     */
    public static ZoneOffset ofHours(int hours) {
        return ofHoursMinutesSeconds(hours, 0, 0);
    }

    /**
     * Obtains an instance of {@code ZoneOffset} using an offset in
     * hours and minutes.
     * <p>
     * The sign of the hours and minutes components must match.
     * Thus, if the hours is negative, the minutes must be negative or zero.
     * If the hours is zero, the minutes may be positive, negative or zero.
     *
     * @param hours  the time-zone offset in hours, from -18 to +18
     * @param minutes  the time-zone offset in minutes, from 0 to &plusmn;59, sign matches hours
     * @return the ZoneOffset, never null
     * @throws IllegalArgumentException if the offset is not in the required range
     */
    public static ZoneOffset ofHoursMinutes(int hours, int minutes) {
        return ofHoursMinutesSeconds(hours, minutes, 0);
    }

    /**
     * Obtains an instance of {@code ZoneOffset} using an offset in
     * hours, minutes and seconds.
     * <p>
     * The sign of the hours, minutes and seconds components must match.
     * Thus, if the hours is negative, the minutes and seconds must be negative or zero.
     *
     * @param hours  the time-zone offset in hours, from -18 to +18
     * @param minutes  the time-zone offset in minutes, from 0 to &plusmn;59, sign matches hours and seconds
     * @param seconds  the time-zone offset in seconds, from 0 to &plusmn;59, sign matches hours and minutes
     * @return the ZoneOffset, never null
     * @throws IllegalArgumentException if the offset is not in the required range
     */
    public static ZoneOffset ofHoursMinutesSeconds(int hours, int minutes, int seconds) {
        validate(hours, minutes, seconds);
        int totalSeconds = totalSeconds(hours, minutes, seconds);
        return ofTotalSeconds(totalSeconds);
    }

    /**
     * Obtains an instance of {@code ZoneOffset} from a period.
     * <p>
     * This creates an offset from the specified period, converting using
     * {@link Period#of(PeriodProvider)}.
     * Only the hour, minute and second fields from the period are used - other fields are ignored.
     * The sign of the hours, minutes and seconds components must match.
     * Thus, if the hours is negative, the minutes and seconds must be negative or zero.
     *
     * @param periodProvider  the period to use, not null
     * @return the ZoneOffset, never null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws IllegalArgumentException if the offset is not in the required range
     */
    public static ZoneOffset of(PeriodProvider periodProvider) {
        Period period = Period.of(periodProvider);
        return ofHoursMinutesSeconds(period.getHours(), period.getMinutes(), period.getSeconds());
    }

    //-----------------------------------------------------------------------
    /**
     * Validates the offset fields.
     *
     * @param hours  the time-zone offset in hours, from -18 to +18
     * @param minutes  the time-zone offset in minutes, from 0 to &plusmn;59
     * @param seconds  the time-zone offset in seconds, from 0 to &plusmn;59
     * @throws IllegalArgumentException if the offset is not in the required range
     */
    private static void validate(int hours, int minutes, int seconds) {
        if (hours < -18 || hours > 18) {
            throw new IllegalArgumentException("Zone offset hours not in valid range: value " + hours +
                    " is not in the range -18 to 18");
        }
        if (hours > 0) {
            if (minutes < 0 || seconds < 0) {
                throw new IllegalArgumentException("Zone offset minutes and seconds must be positive because hours is positive");
            }
        } else if (hours < 0) {
            if (minutes > 0 || seconds > 0) {
                throw new IllegalArgumentException("Zone offset minutes and seconds must be negative because hours is negative");
            }
        } else if ((minutes > 0 && seconds < 0) || (minutes < 0 && seconds > 0)) {
            throw new IllegalArgumentException("Zone offset minutes and seconds must have the same sign");
        }
        if (Math.abs(minutes) > 59) {
            throw new IllegalArgumentException("Zone offset minutes not in valid range: value " +
                    Math.abs(minutes) + " is not in the range 0 to 59");
        }
        if (Math.abs(seconds) > 59) {
            throw new IllegalArgumentException("Zone offset seconds not in valid range: value " +
                    Math.abs(seconds) + " is not in the range 0 to 59");
        }
        if (Math.abs(hours) == 18 && (Math.abs(minutes) > 0 || Math.abs(seconds) > 0)) {
            throw new IllegalArgumentException("Zone offset not in valid range: -18:00 to +18:00");
        }
    }

    /**
     * Calculates the total offset in seconds.
     *
     * @param hours  the time-zone offset in hours, from -18 to +18
     * @param minutes  the time-zone offset in minutes, from 0 to &plusmn;59, sign matches hours and seconds
     * @param seconds  the time-zone offset in seconds, from 0 to &plusmn;59, sign matches hours and minutes
     * @return the total in seconds
     */
    private static int totalSeconds(int hours, int minutes, int seconds) {
        return hours * SECONDS_PER_HOUR + minutes * SECONDS_PER_MINUTE + seconds;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneOffset} specifying the total offset in seconds
     * <p>
     * The offset must be in the range {@code -18:00} to {@code +18:00}, which corresponds to -64800 to +64800.
     *
     * @param totalSeconds  the total time-zone offset in seconds, from -64800 to +64800
     * @return the ZoneOffset, never null
     * @throws IllegalArgumentException if the offset is not in the required range
     */
    public static ZoneOffset ofTotalSeconds(int totalSeconds) {
        if (Math.abs(totalSeconds) > (18 * SECONDS_PER_HOUR)) {
            throw new IllegalArgumentException("Zone offset not in valid range: -18:00 to +18:00");
        }
        if (totalSeconds % (15 * SECONDS_PER_MINUTE) == 0) {
            Integer totalSecs = totalSeconds;
            CACHE_LOCK.readLock().lock();
            try {
                ZoneOffset result = SECONDS_CACHE.get(totalSecs);
                if (result != null) {
                    return result;
                }
            } finally {
                CACHE_LOCK.readLock().unlock();
            }
            CACHE_LOCK.writeLock().lock();
            try {
                ZoneOffset result = SECONDS_CACHE.get(totalSecs);
                if (result == null) {
                    result = new ZoneOffset(totalSeconds);
                    SECONDS_CACHE.put(totalSecs, result);
                    ID_CACHE.put(result.getID(), result);
                }
                return result;
            } finally {
                CACHE_LOCK.writeLock().unlock();
            }
        } else {
            return new ZoneOffset(totalSeconds);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param totalSeconds  the total time-zone offset in seconds, from -64800 to +64800
     */
    private ZoneOffset(int totalSeconds) {
        super();
        amountSeconds = totalSeconds;
        if (amountSeconds == 0) {
            id = "Z";
        } else {
            int absTotalSeconds = Math.abs(amountSeconds);
            StringBuilder buf = new StringBuilder();
            int absHours = absTotalSeconds / SECONDS_PER_HOUR;
            int absMinutes = (absTotalSeconds / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
            buf.append(amountSeconds < 0 ? "-" : "+")
                .append(absHours < 10 ? "0" : "").append(absHours)
                .append(absMinutes < 10 ? ":0" : ":").append(absMinutes);
            int absSeconds = absTotalSeconds % SECONDS_PER_MINUTE;
            if (absSeconds != 0) {
                buf.append(absSeconds < 10 ? ":0" : ":").append(absSeconds);
            }
            id = buf.toString();
        }
    }

    /**
     * Resolves singletons.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return ZoneOffset.ofTotalSeconds(amountSeconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the total zone offset in seconds.
     * <p>
     * This is the primary way to access the offset amount.
     * It returns the total of the hours, minutes and seconds fields as a
     * single offset that can be added to a time.
     *
     * @return the total zone offset amount in seconds
     */
    public int getAmountSeconds() {
        return amountSeconds;
    }

    /**
     * Gets the normalized zone offset id.
     * <p>
     * The id is minor variation to the standard ISO-8601 formatted string
     * for the offset. There are three formats:
     * <ul>
     * <li>{@code Z} - for UTC (ISO-8601)
     * <li>{@code +hh:mm} or {@code -hh:mm} - if the seconds are zero (ISO-8601)
     * <li>{@code +hh:mm:ss} or {@code -hh:mm:ss} - if the seconds are non-zero (not ISO-8601)
     * </ul>
     *
     * @return the zone offset ID, never null
     */
    public String getID() {
        return id;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hours field of the zone offset.
     * <p>
     * This method only has meaning when considered with the minutes and seconds
     * fields. Most applications are advised to use {@link #toPeriod()}
     * or {@link #getAmountSeconds()}.
     * <p>
     * The zone offset is divided into three fields - hours, minutes and seconds.
     * This method returns the value of the hours field.
     * The sign of the value returned by this method will match that of the
     * minutes and seconds fields.
     *
     * @return the hours field of the zone offset amount, from -18 to 18
     */
    public int getHoursField() {
        return amountSeconds / SECONDS_PER_HOUR;
    }

    /**
     * Gets the minutes field of the zone offset.
     * <p>
     * This method only has meaning when considered with the hours and minutes
     * fields. Most applications are advised to use {@link #toPeriod()}
     * or {@link #getAmountSeconds()}.
     * <p>
     * The zone offset is divided into three fields - hours, minutes and seconds.
     * This method returns the value of the minutes field.
     * The sign of the value returned by this method will match that of the
     * hours and seconds fields.
     *
     * @return the minutes field of the zone offset amount,
     *      from -59 to 59 where the sign matches the hours and seconds
     */
    public int getMinutesField() {
        return (amountSeconds / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
    }

    /**
     * Gets the seconds field of the zone offset.
     * <p>
     * This method only has meaning when considered with the hours and minutes
     * fields. Most applications are advised to use {@link #toPeriod()}
     * or {@link #getAmountSeconds()}.
     * <p>
     * The zone offset is divided into three fields - hours, minutes and seconds.
     * This method returns the value of the seconds field.
     * The sign of the value returned by this method will match that of the
     * hours and minutes fields.
     *
     * @return the seconds field of the zone offset amount,
     *      from -59 to 59 where the sign matches the hours and minutes
     */
    public int getSecondsField() {
        return amountSeconds % SECONDS_PER_MINUTE;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this offset with the specified period added.
     * <p>
     * This adds the amount in hours, minutes and seconds from the specified period to this offset.
     * This converts the period using {@link Period#of(PeriodProvider)}.
     * Only the hour, minute and second fields from the period are used - other fields are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a {@code ZoneOffset} based on this offset with the period added, never null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws IllegalArgumentException if the offset is not in the required range
     */
    public ZoneOffset plus(PeriodProvider periodProvider) {
        Period otherPeriod = Period.of(periodProvider).withTimeFieldsOnly().withNanos(0);
        Period thisPeriod = toPeriod();
        Period combined = thisPeriod.plus(otherPeriod).normalized();
        return of(combined);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this offset to a period.
     * <p>
     * The period returned will have fields for hour, minute and second.
     * For negative offsets, the values in the period will all be negative.
     * <p>
     * For example, {@code +02:45} will be converted to {@code P2H45M},
     * while {@code -01:15} will be converted to {@code P-1H-15M}.
     *
     * @return the period equivalent to the zone offset amount, never null
     */
    public Period toPeriod() {
        return Period.ofTimeFields(getHoursField(), getMinutesField(), getSecondsField());
    }

    /**
     * Converts this offset to a time-zone.
     * <p>
     * The returned time-zone will use this offset for all instants.
     *
     * @return the time-zone, never null
     */
    public TimeZone toTimeZone() {
        return TimeZone.of(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this offset to another offset in descending order.
     * <p>
     * The offsets are compared in the order that they occur for the same time
     * of day around the world. Thus, an offset of {@code +10:00} comes before an
     * offset of {@code +09:00} and so on down to {@code -18:00}.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if {@code other} is null
     */
    public int compareTo(ZoneOffset other) {
        return other.amountSeconds - amountSeconds;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instance is equal to the specified offset, comparing
     * the amount of the offset in seconds.
     *
     * @param other  the other zone offset, null returns false
     * @return true if this offset is the same as that specified
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
           return true;
        }
        if (other instanceof ZoneOffset) {
            return amountSeconds == ((ZoneOffset) other).amountSeconds;
        }
        return false;
    }

    /**
     * A hash code for the zone offset.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return amountSeconds;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the zone offset, which is the same
     * as the normalized id.
     *
     * @return the id
     */
    @Override
    public String toString() {
        return id;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this offset then
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, this, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field rule for the zone-offset.
     *
     * @return the field rule for the zone-offset, never null
     */
    public static CalendricalRule<ZoneOffset> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<ZoneOffset> implements Serializable {
        private static final CalendricalRule<ZoneOffset> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(ZoneOffset.class, ISOChronology.INSTANCE, "ZoneOffset", null, null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected ZoneOffset derive(Calendrical calendrical) {
            OffsetDateTime odt = calendrical.get(OffsetDateTime.rule());
            if (odt != null) {
                return odt.getOffset();
            }
            OffsetDate od = calendrical.get(OffsetDate.rule());
            if (od != null) {
                return od.getOffset();
            }
            OffsetTime ot = calendrical.get(OffsetTime.rule());
            if (ot != null) {
                return ot.getOffset();
            }
            return null;
        }
    }

}
