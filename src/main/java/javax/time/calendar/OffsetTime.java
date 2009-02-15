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
package javax.time.calendar;

import java.io.Serializable;

import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.period.PeriodProvider;

/**
 * A time with a zone offset from UTC in the ISO-8601 calendar system,
 * such as '10:15:30+01:00'.
 * <p>
 * OffsetTime is an immutable calendrical that represents a time, often
 * viewed as hour-minute-second-offset.
 * This class stores all time fields, to a precision of nanoseconds,
 * as well as a zone offset.
 * Thus, for example, the value "13:45.30.123456789+02:00" can be stored
 * in a OffsetTime.
 * <p>
 * OffsetTime is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class OffsetTime
        implements TimeProvider, CalendricalProvider, Comparable<OffsetTime>, Serializable, TimeMatcher, TimeAdjuster {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -1751032571L;

    /**
     * The local time, never null.
     */
    private final LocalTime time;
    /**
     * The zone offset from UTC, never null.
     */
    private final ZoneOffset offset;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetTime</code> from an hour, minute,
     * and offset, setting the second and nanosecond to zero.
     *
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     */
    public static OffsetTime time(
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour, ZoneOffset offset) {
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of <code>OffsetTime</code> from an hour, minute,
     * second, and offset, setting the nanosecond to zero.
     *
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param secondOfMinute  the second of minute to represent, not null
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     */
    public static OffsetTime time(
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour,
            SecondOfMinute secondOfMinute, ZoneOffset offset) {
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of <code>OffsetTime</code> from an hour, minute,
     * second, nanosecond, and offset.
     *
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param secondOfMinute  the second of minute to represent, not null
     * @param nanoOfSecond  the nano of second to represent, not null
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     */
    public static OffsetTime time(
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour,
            SecondOfMinute secondOfMinute, NanoOfSecond nanoOfSecond, ZoneOffset offset) {
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of <code>OffsetTime</code>.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static OffsetTime time(int hourOfDay, int minuteOfHour, ZoneOffset offset) {
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of <code>OffsetTime</code>.
     * <p>
     * The second field will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static OffsetTime time(int hourOfDay, int minuteOfHour, int secondOfMinute, ZoneOffset offset) {
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of <code>OffsetTime</code>.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static OffsetTime time(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, ZoneOffset offset) {
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of <code>OffsetTime</code>.
     *
     * @param timeProvider  the time provider to use, not null
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     */
    public static OffsetTime time(TimeProvider timeProvider, ZoneOffset offset) {
        LocalTime time = LocalTime.time(timeProvider);
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts an instant to an offset time.
     * <p>
     * The date component of the instant is dropped during the conversion.
     * This means that the conversion can never fail due to the instant being
     * out of the valid range of dates.
     *
     * @param instantProvider  the instant to convert, not null
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     */
    public static OffsetTime fromInstant(InstantProvider instantProvider, ZoneOffset offset) {
        Instant instant = Instant.instant(instantProvider);
        ISOChronology.checkNotNull(offset, "ZoneOffset must not be null");
        
        long secsOfDay = instant.getEpochSeconds() % ISOChronology.SECONDS_PER_DAY;
        secsOfDay = (secsOfDay + offset.getAmountSeconds()) % ISOChronology.SECONDS_PER_DAY;
        if (secsOfDay < 0) {
            secsOfDay += ISOChronology.SECONDS_PER_DAY;
        }
        LocalTime time = LocalTime.fromSecondOfDay(secsOfDay, instant.getNanoOfSecond());
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param time  the time, validated as not null
     * @param offset  the zone offset, validated as not null
     */
    private OffsetTime(LocalTime time, ZoneOffset offset) {
        if (time == null) {
            throw new NullPointerException("The time must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("The zone offset must not be null");
        }
        this.time = time;
        this.offset = offset;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that describes the calendar system rules for
     * this time.
     *
     * @return the ISO chronology, never null
     */
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>OffsetTime</code> can
     * be queried using the specified calendar field.
     *
     * @param fieldRule  the field to query, null returns false
     * @return true if the field is supported, false otherwise
     */
    public boolean isSupported(DateTimeFieldRule fieldRule) {
        return time.isSupported(fieldRule);
    }

    /**
     * Gets the value of the specified calendar field.
     * <p>
     * This method queries the value of the specified calendar field.
     * If the calendar field is not supported then an exception is thrown.
     *
     * @param fieldRule  the field to query, not null
     * @return the value for the field
     * @throws UnsupportedCalendarFieldException if no value for the field is found
     */
    public int get(DateTimeFieldRule fieldRule) {
        return time.get(fieldRule);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the local time.
     * <p>
     * This returns the time without the zone offset.
     *
     * @return the local time, never null
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Returns a copy of this OffsetTime with a different local time.
     * <p>
     * This method changes the time stored to a different time.
     * No calculation is performed. The result simply represents the same
     * offset and the new time.
     *
     * @param timeProvider  the local time to change to, not null
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime withTime(TimeProvider timeProvider) {
        LocalTime localTime = LocalTime.time(timeProvider);
        return localTime.equals(this.time) ? this : new OffsetTime(localTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset representing how far ahead or behind UTC the time is.
     *
     * @return the zone offset, never null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Returns a copy of this OffsetTime with a different zone offset.
     * <p>
     * This method changes the zoned time to a different offset.
     * No calculation is performed - the result simply represents the same
     * time and the new offset.
     * <p>
     * To take into account the offsets and adjust the time fields,
     * use {@link #adjustLocalTime(ZoneOffset)}.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime withOffset(ZoneOffset offset) {
        return offset != null && offset.equals(this.offset) ? this : new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts the local time using the specified offset.
     * <p>
     * This method changes the offset time from one offset to another.
     * If this time represents 10:30+02:00 and the offset specified is
     * +03:00, then this method will return 11:30+03:00.
     * <p>
     * To change the offset whilst keeping the local time,
     * use {@link #withOffset(ZoneOffset)}.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime adjustLocalTime(ZoneOffset offset) {
        if (offset.equals(this.offset)) {
            return this;
        }
        int difference = offset.getAmountSeconds() - this.offset.getAmountSeconds();
        LocalTime adjusted = time.plusSeconds(difference);
        return new OffsetTime(adjusted, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day field as an <code>HourOfDay</code>.
     * <p>
     * This method provides access to an object representing the hour of day field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the hour of day, never null
     */
    public HourOfDay toHourOfDay() {
        return time.toHourOfDay();
    }

    /**
     * Gets the minute of hour field as a <code>MinuteOfHour</code>.
     * <p>
     * This method provides access to an object representing the minute of hour field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the minute of hour, never null
     */
    public MinuteOfHour toMinuteOfHour() {
        return time.toMinuteOfHour();
    }

    /**
     * Gets the second of minute field as a <code>SecondOfMinute</code>.
     * <p>
     * This method provides access to an object representing the second of minute field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the second of minute, never null
     */
    public SecondOfMinute toSecondOfMinute() {
        return time.toSecondOfMinute();
    }

    /**
     * Gets the nano of second field as a <code>NanoOfSecond</code>.
     * <p>
     * This method provides access to an object representing the nano of second field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the nano of second, never null
     */
    public NanoOfSecond toNanoOfSecond() {
        return time.toNanoOfSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day field.
     *
     * @return the hour of day, from 0 to 23
     */
    public int getHourOfDay() {
        return time.getHourOfDay();
    }

    /**
     * Gets the minute of hour field.
     *
     * @return the minute of hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return time.getMinuteOfHour();
    }

    /**
     * Gets the second of minute field.
     *
     * @return the second of minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return time.getSecondOfMinute();
    }

    /**
     * Gets the nano of second field.
     *
     * @return the nano of second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return time.getNanoOfSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the time altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the time in various ways.
     * A simple adjuster might simply set the one of the fields, such as the hour field.
     * A more complex adjuster might set the time to end of the working day.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a new updated OffsetTime, never null
     * @throws IllegalArgumentException if the adjuster returned null
     */
    public OffsetTime with(TimeAdjuster adjuster) {
        LocalTime newTime = time.with(adjuster);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated OffsetTime, never null
     * @throws IllegalCalendarFieldValueException if the hour value is invalid
     */
    public OffsetTime withHourOfDay(int hourOfDay) {
        LocalTime newTime = time.withHourOfDay(hourOfDay);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated OffsetTime, never null
     * @throws IllegalCalendarFieldValueException if the minute value is invalid
     */
    public OffsetTime withMinuteOfHour(int minuteOfHour) {
        LocalTime newTime = time.withMinuteOfHour(minuteOfHour);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated OffsetTime, never null
     * @throws IllegalCalendarFieldValueException if the second value is invalid
     */
    public OffsetTime withSecondOfMinute(int secondOfMinute) {
        LocalTime newTime = time.withSecondOfMinute(secondOfMinute);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated OffsetTime, never null
     * @throws IllegalCalendarFieldValueException if the nanos value is invalid
     */
    public OffsetTime withNanoOfSecond(int nanoOfSecond) {
        LocalTime newTime = time.withNanoOfSecond(nanoOfSecond);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the specified period added.
     * <p>
     * This adds the amount in hours, minutes and seconds from the specified period to this time.
     * Any date amounts, such as years, months or days are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plus(PeriodProvider periodProvider) {
        LocalTime newTime = time.plus(periodProvider);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the specified period in hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plusHours(int hours) {
        LocalTime newTime = time.plusHours(hours);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plusMinutes(int minutes) {
        LocalTime newTime = time.plusMinutes(minutes);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plusSeconds(int seconds) {
        LocalTime newTime = time.plusSeconds(seconds);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime plusNanos(int nanos) {
        LocalTime newTime = time.plusNanos(nanos);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the specified period subtracted.
     * <p>
     * This subtracts the amount in hours, minutes and seconds from the specified period to this time.
     * Any date amounts, such as years, months or days are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime minus(PeriodProvider periodProvider) {
        LocalTime newTime = time.minus(periodProvider);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetTime with the specified period in hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime minusHours(int hours) {
        LocalTime newTime = time.minusHours(hours);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified period in minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime minusMinutes(int minutes) {
        LocalTime newTime = time.minusMinutes(minutes);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified period in seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime minusSeconds(int seconds) {
        LocalTime newTime = time.minusSeconds(seconds);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this OffsetTime with the specified period in nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return a new updated OffsetTime, never null
     */
    public OffsetTime minusNanos(int nanos) {
        LocalTime newTime = time.minusNanos(nanos);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the time matches the specified matcher.
     * <p>
     * Matchers can be used to query the time.
     * A simple matcher might simply query one of the fields, such as the hour field.
     * A more complex matcher might query if the time is during opening hours.
     * <p>
     * The offset has no effect on the matching.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this time matches the matcher, false otherwise
     */
    public boolean matches(TimeMatcher matcher) {
        return time.matches(matcher);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this time part of this object is equal to the input time
     *
     * @param time the time to match, not null
     * @return true if the two times are equal, false otherwise
     */
    public boolean matchesTime(LocalTime time) {
        return this.time.matchesTime(time);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a time to have the value of the time part of this object.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    public LocalTime adjustTime(LocalTime time) {
        return matches(time) ? time : this.time;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this time to a <code>LocalTime</code>.
     *
     * @return a LocalTime with the same time as this instance, never null
     */
    public LocalTime toLocalTime() {
        return time;
    }

    /**
     * Converts this date to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return new Calendrical(null, time, offset, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this time to another time based on the UTC equivalent times
     * then local time.
     * <p>
     * This ordering is consistent with <code>equals()</code>.
     * For example, the following is the comparator order:
     * <ol>
     * <li>10:30+01:00</li>
     * <li>11:00+01:00</li>
     * <li>12:00+02:00</li>
     * <li>11:30+01:00</li>
     * <li>12:00+01:00</li>
     * <li>12:30+01:00</li>
     * </ol>
     * Values #2 and #3 represent the same instant on the time-line.
     * When two values represent the same instant, the local time is compared
     * to distinguish them. This step is needed to make the ordering
     * consistent with <code>equals()</code>.
     *
     * @param other  the other time to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(OffsetTime other) {
        if (offset.equals(other.offset)) {
            return time.compareTo(other.time);
        }
        LocalTime thisUTC = time.plusSeconds(-offset.getAmountSeconds());
        LocalTime otherUTC = other.time.plusSeconds(-other.offset.getAmountSeconds());
        int compare = thisUTC.compareTo(otherUTC);
        if (compare == 0) {
            compare = time.compareTo(other.time);
        }
        return compare;
    }

    /**
     * Is this time after the specified time.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is after the specified time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(OffsetTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this time before the specified time.
     *
     * @param other  the other time to compare to, not null
     * @return true if this point is before the specified time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(OffsetTime other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this time equal to the specified time.
     * <p>
     * This compares the time and the offset.
     *
     * @param other  the other time to compare to, null returns false
     * @return true if this point is equal to the specified time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof OffsetTime) {
            OffsetTime zonedTime = (OffsetTime) other;
            return time.equals(zonedTime.time) && offset.equals(zonedTime.offset);
        }
        return false;
    }

    /**
     * A hashcode for this time.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return time.hashCode() ^ offset.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the time as a <code>String</code>, such as '10:15:30+01:00'.
     * <p>
     * The output will be one of the following formats:
     * <ul>
     * <li>'hh:mmZ'</li>
     * <li>'hh:mm:ssZ'</li>
     * <li>'hh:mm:ss.SSSZ'</li>
     * <li>'hh:mm:ss.SSSSSSZ'</li>
     * <li>'hh:mm:ss.SSSSSSSSSZ'</li>
     * </ul>
     * where 'Z' is the id of the zone offset, such as '+02:30' or 'Z'.
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return the formatted time string, never null
     */
    @Override
    public String toString() {
        return time.toString() + offset.toString();
    }

}
