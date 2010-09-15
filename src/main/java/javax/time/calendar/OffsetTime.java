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

import javax.time.CalendricalException;
import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.calendar.format.CalendricalPrintException;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatters;

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
        implements TimeProvider, Calendrical, Comparable<OffsetTime>, Serializable, CalendricalMatcher, TimeAdjuster {

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
     * Obtains the current time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current time.
     * The offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current time, never null
     */
    public static OffsetTime now(Clock clock) {
        ISOChronology.checkNotNull(clock, "Clock must not be null");
        return ofInstant(clock.instant(), clock.getZone().getRules().getOffset(clock.instant()));
    }

    /**
     * Obtains the current time from the system clock in the default time-zone.
     * <p>
     * This will query the system clock in the default time-zone to obtain the current time.
     * The offset will be set based on the time-zone in the system clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current time using the system clock, never null
     */
    public static OffsetTime nowSystemClock() {
        return now(Clock.systemDefaultZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime}.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static OffsetTime of(int hourOfDay, int minuteOfHour, ZoneOffset offset) {
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of {@code OffsetTime}.
     * <p>
     * The second field will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static OffsetTime of(int hourOfDay, int minuteOfHour, int secondOfMinute, ZoneOffset offset) {
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of {@code OffsetTime}.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static OffsetTime of(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, ZoneOffset offset) {
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of {@code OffsetTime} from a {@code TimeProvider}.
     *
     * @param timeProvider  the time provider to use, not null
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     */
    public static OffsetTime of(TimeProvider timeProvider, ZoneOffset offset) {
        LocalTime time = LocalTime.of(timeProvider);
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from an {@code InstantProvider}.
     * <p>
     * The date component of the instant is dropped during the conversion.
     * This means that the conversion can never fail due to the instant being
     * out of the valid range of dates.
     *
     * @param instantProvider  the instant to convert, not null
     * @param offset  the zone offset, not null
     * @return the offset time, never null
     */
    public static OffsetTime ofInstant(InstantProvider instantProvider, ZoneOffset offset) {
        Instant instant = Instant.of(instantProvider);
        ISOChronology.checkNotNull(offset, "ZoneOffset must not be null");
        
        long secsOfDay = instant.getEpochSeconds() % ISOChronology.SECONDS_PER_DAY;
        secsOfDay = (secsOfDay + offset.getAmountSeconds()) % ISOChronology.SECONDS_PER_DAY;
        if (secsOfDay < 0) {
            secsOfDay += ISOChronology.SECONDS_PER_DAY;
        }
        LocalTime time = LocalTime.ofSecondOfDay(secsOfDay, instant.getNanoOfSecond());
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from a text string.
     * <p>
     * The following formats are accepted in ASCII:
     * <ul>
     * <li>{@code {Hour}:{Minute}{OffsetID}}
     * <li>{@code {Hour}:{Minute}:{Second}{OffsetID}}
     * <li>{@code {Hour}:{Minute}:{Second}.{NanosecondFraction}{OffsetID}}
     * </ul>
     * <p>
     * The hour has 2 digits with values from 0 to 23.
     * The minute has 2 digits with values from 0 to 59.
     * The second has 2 digits with values from 0 to 59.
     * The nanosecond fraction has from 1 to 9 digits with values from 0 to 999,999,999.
     * <p>
     * The offset ID is the normalized form as defined in {@link ZoneOffset}.
     *
     * @param text  the text to parse such as '10:15:30+01:00', not null
     * @return the parsed local time, never null
     * @throws CalendricalException if the text cannot be parsed
     */
    public static OffsetTime parse(String text) {
        return DateTimeFormatters.isoOffsetTime().parse(text, rule());
    }

    /**
     * Obtains an instance of {@code OffsetTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed offset time, never null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalException if the text cannot be parsed
     */
    public static OffsetTime parse(String text, DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.parse(text, rule());
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
     * Gets the chronology that this time uses, which is the ISO calendar system.
     *
     * @return the ISO chronology, never null
     */
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this time then
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
        LocalTime localTime = LocalTime.of(timeProvider);
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
        // TODO: Rename to withOffsetAdjustLocalTime?
        if (offset.equals(this.offset)) {
            return this;
        }
        int difference = offset.getAmountSeconds() - this.offset.getAmountSeconds();
        LocalTime adjusted = time.plusSeconds(difference);
        return new OffsetTime(adjusted, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    public int getHourOfDay() {
        return time.getHourOfDay();
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return time.getMinuteOfHour();
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return time.getSecondOfMinute();
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return time.getNanoOfSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the time altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the time in various ways.
     * A simple adjuster might simply set the one of the fields, such as the hour field.
     * A more complex adjuster might set the time to end of the working day.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return an {@code OffsetTime} based on this time adjusted as necessary, never null
     * @throws IllegalArgumentException if the adjuster returned null
     */
    public OffsetTime with(TimeAdjuster adjuster) {
        LocalTime newTime = time.with(adjuster);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the hour-of-day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @return an {@code OffsetTime} based on this time with the requested hour, never null
     * @throws IllegalCalendarFieldValueException if the hour value is invalid
     */
    public OffsetTime withHourOfDay(int hourOfDay) {
        LocalTime newTime = time.withHourOfDay(hourOfDay);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the minute-of-hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return an {@code OffsetTime} based on this time with the requested minute, never null
     * @throws IllegalCalendarFieldValueException if the minute value is invalid
     */
    public OffsetTime withMinuteOfHour(int minuteOfHour) {
        LocalTime newTime = time.withMinuteOfHour(minuteOfHour);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the second-of-minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return an {@code OffsetTime} based on this time with the requested second, never null
     * @throws IllegalCalendarFieldValueException if the second value is invalid
     */
    public OffsetTime withSecondOfMinute(int secondOfMinute) {
        LocalTime newTime = time.withSecondOfMinute(secondOfMinute);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the nano-of-second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return an {@code OffsetTime} based on this time with the requested nanosecond, never null
     * @throws IllegalCalendarFieldValueException if the nanos value is invalid
     */
    public OffsetTime withNanoOfSecond(int nanoOfSecond) {
        LocalTime newTime = time.withNanoOfSecond(nanoOfSecond);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified time period added.
     * <p>
     * This adds the specified period to this time, returning a new time.
     * Before addition, the period is converted to a time-based {@code Period} using
     * {@link Period#ofTimeFields(PeriodProvider)}.
     * That factory ignores any date-based ISO fields, thus adding a date-based
     * period to this time will have no effect.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return an {@code OffsetTime} based on this time with the period added, never null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     */
    public OffsetTime plus(PeriodProvider periodProvider) {
        LocalTime newTime = time.plus(periodProvider);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return an {@code OffsetTime} based on this time with the hours added, never null
     */
    public OffsetTime plusHours(int hours) {
        LocalTime newTime = time.plusHours(hours);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return an {@code OffsetTime} based on this time with the minutes added, never null
     */
    public OffsetTime plusMinutes(int minutes) {
        LocalTime newTime = time.plusMinutes(minutes);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return an {@code OffsetTime} based on this time with the seconds added, never null
     */
    public OffsetTime plusSeconds(int seconds) {
        LocalTime newTime = time.plusSeconds(seconds);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return an {@code OffsetTime} based on this time with the nanoseconds added, never null
     */
    public OffsetTime plusNanos(int nanos) {
        LocalTime newTime = time.plusNanos(nanos);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified time period subtracted.
     * <p>
     * This subtracts the specified period from this time, returning a new time.
     * Before subtraction, the period is converted to a time-based {@code Period} using
     * {@link Period#ofTimeFields(PeriodProvider)}.
     * That factory ignores any date-based ISO fields, thus subtracting a date-based
     * period to this time will have no effect.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return an {@code OffsetTime} based on this time with the period subtracted, never null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     */
    public OffsetTime minus(PeriodProvider periodProvider) {
        LocalTime newTime = time.minus(periodProvider);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the hours subtracted, never null
     */
    public OffsetTime minusHours(int hours) {
        LocalTime newTime = time.minusHours(hours);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the minutes subtracted, never null
     */
    public OffsetTime minusMinutes(int minutes) {
        LocalTime newTime = time.minusMinutes(minutes);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the seconds subtracted, never null
     */
    public OffsetTime minusSeconds(int seconds) {
        LocalTime newTime = time.minusSeconds(seconds);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the nanoseconds subtracted, never null
     */
    public OffsetTime minusNanos(int nanos) {
        LocalTime newTime = time.minusNanos(nanos);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this time matches the specified matcher.
     * <p>
     * Matchers can be used to query the time.
     * A simple matcher might simply compare one of the fields, such as the hour field.
     * A more complex matcher might check if the time is the last second of the day.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this time matches the matcher, false otherwise
     */
    public boolean matches(CalendricalMatcher matcher) {
        return matcher.matchesCalendrical(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the time extracted from the calendrical matches this.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        return this.equals(calendrical.get(rule()));
    }

    /**
     * Adjusts a time to have the value of the time part of this object.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    public LocalTime adjustTime(LocalTime time) {
        return this.time.adjustTime(time);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this time to a {@code LocalTime}.
     *
     * @return a LocalTime with the same time as this instance, never null
     */
    public LocalTime toLocalTime() {
        return time;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this time to another time based on the UTC equivalent times
     * then local time.
     * <p>
     * This ordering is consistent with {@code equals()}.
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
     * consistent with {@code equals()}.
     *
     * @param other  the other time to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if {@code other} is null
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
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isAfter(OffsetTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this time before the specified time.
     *
     * @param other  the other time to compare to, not null
     * @return true if this point is before the specified time
     * @throws NullPointerException if {@code other} is null
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
     * A hash code for this time.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return time.hashCode() ^ offset.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this time as a {@code String}, such as {@code 10:15:30+01:00}.
     * <p>
     * The output will be one of the following formats:
     * <ul>
     * <li>{@code HH:mmZZZZ}</li>
     * <li>{@code HH:mm:ssZZZZ}</li>
     * <li>{@code HH:mm:ssfnnnZZZZ}</li>
     * <li>{@code HH:mm:ssfnnnnnnZZZZ}</li>
     * <li>{@code HH:mm:ssfnnnnnnnnnZZZZ}</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return the formatted time, never null
     */
    @Override
    public String toString() {
        return time.toString() + offset.toString();
    }

    /**
     * Outputs this time as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted time string, never null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalPrintException if an error occurs during printing
     */
    public String toString(DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.print(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field rule for {@code OffsetTime}.
     *
     * @return the field rule for the time, never null
     */
    public static CalendricalRule<OffsetTime> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<OffsetTime> implements Serializable {
        private static final CalendricalRule<OffsetTime> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(OffsetTime.class, ISOChronology.INSTANCE, "OffsetTime", ISOChronology.periodNanos(), ISOChronology.periodDays());
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected OffsetTime derive(Calendrical calendrical) {
            OffsetDateTime odt = calendrical.get(OffsetDateTime.rule());
            return odt != null ? odt.toOffsetTime() : null;
        }
    }

}
