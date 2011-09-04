/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.Duration;
import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.MathUtils;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatters;

/**
 * A time with a zone offset from UTC in the ISO-8601 calendar system,
 * such as {@code 10:15:30+01:00}.
 * <p>
 * {@code OffsetTime} is an immutable calendrical that represents a time, often
 * viewed as hour-minute-second-offset.
 * This class stores all time fields, to a precision of nanoseconds,
 * as well as a zone offset.
 * Thus, for example, the value "13:45.30.123456789+02:00" can be stored
 * in a {@code OffsetTime}.
 * <p>
 * OffsetTime is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class OffsetTime
        implements Calendrical, CalendricalMatcher, TimeAdjuster, Comparable<OffsetTime>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -1751032571L;

    /**
     * The local time, not null.
     */
    private final LocalTime time;
    /**
     * The zone offset from UTC, not null.
     */
    private final ZoneOffset offset;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code OffsetTime}.
     *
     * @return the rule for the time, not null
     */
    public static CalendricalRule<OffsetTime> rule() {
        return ISOCalendricalRule.OFFSET_TIME;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the current time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current time using the system clock, not null
     */
    public static OffsetTime now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current time, not null
     */
    public static OffsetTime now(Clock clock) {
        ISOChronology.checkNotNull(clock, "Clock must not be null");
        final Instant now = clock.instant();  // called once
        return ofInstant(now, clock.getZone().getRules().getOffset(now));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from an hour and minute.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static OffsetTime of(int hourOfDay, int minuteOfHour, ZoneOffset offset) {
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of {@code OffsetTime} from an hour, minute and second.
     * <p>
     * The second field will be set to zero by this factory method.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static OffsetTime of(int hourOfDay, int minuteOfHour, int secondOfMinute, ZoneOffset offset) {
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of {@code OffsetTime} from an hour, minute, second and nanosecond.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return the offset time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static OffsetTime of(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, ZoneOffset offset) {
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new OffsetTime(time, offset);
    }

    /**
     * Obtains an instance of {@code OffsetTime} from a local time and an offset.
     *
     * @param time  the local time, not null
     * @param offset  the zone offset, not null
     * @return the offset time, not null
     */
    public static OffsetTime of(LocalTime time, ZoneOffset offset) {
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from an {@code InstantProvider}
     * using the UTC offset.
     * <p>
     * The date component of the instant is dropped during the conversion.
     * This means that the conversion can never fail due to the instant being
     * out of the valid range of dates.
     *
     * @param instantProvider  the instant to convert, not null
     * @return the offset time in UTC, not null
     */
    public static OffsetTime ofInstantUTC(InstantProvider instantProvider) {
        return ofInstant(instantProvider, ZoneOffset.UTC);
    }

    /**
     * Obtains an instance of {@code OffsetTime} from an {@code InstantProvider}.
     * <p>
     * The date component of the instant is dropped during the conversion.
     * This means that the conversion can never fail due to the instant being
     * out of the valid range of dates.
     *
     * @param instantProvider  the instant to convert, not null
     * @param offset  the zone offset, not null
     * @return the offset time, not null
     */
    public static OffsetTime ofInstant(InstantProvider instantProvider, ZoneOffset offset) {
        Instant instant = Instant.of(instantProvider);
        ISOChronology.checkNotNull(offset, "ZoneOffset must not be null");
        
        long secsOfDay = instant.getEpochSecond() % ISOChronology.SECONDS_PER_DAY;
        secsOfDay = (secsOfDay + offset.getAmountSeconds()) % ISOChronology.SECONDS_PER_DAY;
        if (secsOfDay < 0) {
            secsOfDay += ISOChronology.SECONDS_PER_DAY;
        }
        LocalTime time = LocalTime.ofSecondOfDay(secsOfDay, instant.getNanoOfSecond());
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from a set of calendricals.
     * <p>
     * A calendrical represents some form of date and time information.
     * This method combines the input calendricals into a time.
     *
     * @param calendricals  the calendricals to create a time from, no nulls, not null
     * @return the offset time, not null
     * @throws CalendricalException if unable to merge to an offset time
     */
    public static OffsetTime from(Calendrical... calendricals) {
        return CalendricalEngine.merge(calendricals).deriveChecked(rule());
    }

    /**
     * Obtains an instance of {@code OffsetTime} from the engine.
     * <p>
     * This internal method is used by the associated rule.
     *
     * @param engine  the engine to derive from, not null
     * @return the offset time, null if unable to obtain the time
     */
    static OffsetTime deriveFrom(CalendricalEngine engine) {
        LocalTime time = engine.derive(LocalTime.rule());
        ZoneOffset offset = engine.getOffset(true);
        if (time == null || offset == null) {
            return null;
        }
        return new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetTime} from a text string such as {@code 10:15:30+01:00}.
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
     * @param text  the text to parse such as "10:15:30+01:00", not null
     * @return the parsed local time, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static OffsetTime parse(CharSequence text) {
        return DateTimeFormatters.isoOffsetTime().parse(text, rule());
    }

    /**
     * Obtains an instance of {@code OffsetTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed offset time, not null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static OffsetTime parse(CharSequence text, DateTimeFormatter formatter) {
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
            throw new NullPointerException("LocalTime must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("ZoneOffset must not be null");
        }
        this.time = time;
        this.offset = offset;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this time then
     * {@code null} will be returned.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        if (ruleToDerive == rule()) {
            return (T) this;
        }
        return CalendricalEngine.derive(ruleToDerive, rule(), null, time, offset, null, ISOChronology.INSTANCE, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the time altered and the offset retained.
     * <p>
     * This method returns an object with the same {@code ZoneOffset} and the specified {@code LocalTime}.
     * No calculation is needed or performed.
     *
     * @param time  the local time to change to, not null
     * @return an {@code OffsetTime} based on this time with the requested time, not null
     */
    public OffsetTime withTime(LocalTime time) {
        return this.time.equals(time) ? this : new OffsetTime(time, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset representing how far ahead or behind UTC the time is.
     *
     * @return the zone offset, not null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified offset ensuring
     * that the result has the same local time.
     * <p>
     * This method returns an object with the same {@code LocalTime} and the specified {@code ZoneOffset}.
     * No calculation is needed or performed.
     * For example, if this time represents {@code 10:30+02:00} and the offset specified is
     * {@code +03:00}, then this method will return {@code 10:30+03:00}.
     * <p>
     * To take into account the difference between the offsets, and adjust the time fields,
     * use {@link #withOffsetSameInstant}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return an {@code OffsetTime} based on this time with the requested offset, not null
     */
    public OffsetTime withOffsetSameLocal(ZoneOffset offset) {
        return offset != null && offset.equals(this.offset) ? this : new OffsetTime(time, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified offset ensuring
     * that the result is at the same instant on an implied day.
     * <p>
     * This method returns an object with the specified {@code ZoneOffset} and a {@code LocalTime}
     * adjusted by the difference between the two offsets.
     * This will result in the old and new objects representing the same instant an an implied day.
     * This is useful for finding the local time in a different offset.
     * For example, if this time represents {@code 10:30+02:00} and the offset specified is
     * {@code +03:00}, then this method will return {@code 11:30+03:00}.
     * <p>
     * To change the offset without adjusting the local time use {@link #withOffsetSameLocal}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return an {@code OffsetTime} based on this time with the requested offset, not null
     */
    public OffsetTime withOffsetSameInstant(ZoneOffset offset) {
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
     * @return an {@code OffsetTime} based on this time adjusted as necessary, not null
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
     * @return an {@code OffsetTime} based on this time with the requested hour, not null
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
     * @return an {@code OffsetTime} based on this time with the requested minute, not null
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
     * @return an {@code OffsetTime} based on this time with the requested second, not null
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
     * @return an {@code OffsetTime} based on this time with the requested nanosecond, not null
     * @throws IllegalCalendarFieldValueException if the nanos value is invalid
     */
    public OffsetTime withNanoOfSecond(int nanoOfSecond) {
        LocalTime newTime = time.withNanoOfSecond(nanoOfSecond);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified period added.
     * <p>
     * This adds the specified period to this time, returning a new time.
     * The calculation wraps around midnight and ignores any date-based ISO fields.
     * <p>
     * The period is interpreted using rules equivalent to {@link Period#ofTimeFields(PeriodProvider)}.
     * Those rules ignore any date-based ISO fields, thus adding a date-based
     * period to this time will have no effect.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return an {@code OffsetTime} based on this time with the period added, not null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws ArithmeticException if the period overflows during conversion to hours/minutes/seconds/nanos
     */
    public OffsetTime plus(PeriodProvider periodProvider) {
        LocalTime newTime = time.plus(periodProvider);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified duration added.
     * <p>
     * This adds the specified duration to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * The calculation is equivalent to using {@link #plusSeconds(long)} and
     * {@link #plusNanos(long)} on the two parts of the duration.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return an {@code OffsetTime} based on this time with the duration added, not null
     */
    public OffsetTime plus(Duration duration) {
        LocalTime newTime = time.plus(duration);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in hours added.
     * <p>
     * This adds the specified number of hours to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return an {@code OffsetTime} based on this time with the hours added, not null
     */
    public OffsetTime plusHours(long hours) {
        LocalTime newTime = time.plusHours(hours);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in minutes added.
     * <p>
     * This adds the specified number of minutes to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return an {@code OffsetTime} based on this time with the minutes added, not null
     */
    public OffsetTime plusMinutes(long minutes) {
        LocalTime newTime = time.plusMinutes(minutes);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in seconds added.
     * <p>
     * This adds the specified number of seconds to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return an {@code OffsetTime} based on this time with the seconds added, not null
     */
    public OffsetTime plusSeconds(long seconds) {
        LocalTime newTime = time.plusSeconds(seconds);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in nanoseconds added.
     * <p>
     * This adds the specified number of nanoseconds to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return an {@code OffsetTime} based on this time with the nanoseconds added, not null
     */
    public OffsetTime plusNanos(long nanos) {
        LocalTime newTime = time.plusNanos(nanos);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified period subtracted.
     * <p>
     * This subtracts the specified period from this time, returning a new time.
     * The calculation wraps around midnight and ignores any date-based ISO fields.
     * <p>
     * The period is interpreted using rules equivalent to {@link Period#ofTimeFields(PeriodProvider)}.
     * Those rules ignore any date-based ISO fields, thus adding a date-based
     * period to this time will have no effect.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return an {@code OffsetTime} based on this time with the period subtracted, not null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws ArithmeticException if the period overflows during conversion to hours/minutes/seconds/nanos
     */
    public OffsetTime minus(PeriodProvider periodProvider) {
        LocalTime newTime = time.minus(periodProvider);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified duration subtracted.
     * <p>
     * This subtracts the specified duration to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * The calculation is equivalent to using {@link #minusSeconds(long)} and
     * {@link #minusNanos(long)} on the two parts of the duration.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return an {@code OffsetTime} based on this time with the duration subtracted, not null
     */
    public OffsetTime minus(Duration duration) {
        LocalTime newTime = time.minus(duration);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in hours subtracted.
     * <p>
     * This subtracts the specified number of hours from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the hours subtracted, not null
     */
    public OffsetTime minusHours(long hours) {
        LocalTime newTime = time.minusHours(hours);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in minutes subtracted.
     * <p>
     * This subtracts the specified number of minutes from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the minutes subtracted, not null
     */
    public OffsetTime minusMinutes(long minutes) {
        LocalTime newTime = time.minusMinutes(minutes);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in seconds subtracted.
     * <p>
     * This subtracts the specified number of seconds from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the seconds subtracted, not null
     */
    public OffsetTime minusSeconds(long seconds) {
        LocalTime newTime = time.minusSeconds(seconds);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetTime} with the specified period in nanoseconds subtracted.
     * <p>
     * This subtracts the specified number of nanoseconds from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return an {@code OffsetTime} based on this time with the nanoseconds subtracted, not null
     */
    public OffsetTime minusNanos(long nanos) {
        LocalTime newTime = time.minusNanos(nanos);
        return newTime == this.time ? this : new OffsetTime(newTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this {@code OffsetTime} matches the specified matcher.
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
     * <p>
     * This method implements the {@code CalendricalMatcher} interface.
     * It is intended that applications use {@link #matches} rather than this method.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        return this.equals(calendrical.get(rule()));
    }

    /**
     * Adjusts a time to have the value of the time part of this object.
     * <p>
     * This method implements the {@code TimeAdjuster} interface.
     * It is intended that applications use {@link #with(TimeAdjuster)} rather than this method.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, not null
     */
    public LocalTime adjustTime(LocalTime time) {
        return this.time.adjustTime(time);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this time to a {@code LocalTime}.
     *
     * @return a LocalTime with the same time as this instance, not null
     */
    public LocalTime toLocalTime() {
        return time;
    }

    /**
     * Converts this time to epoch nanos based on 1970-01-01Z.
     * 
     * @return the epoch nanos value
     */
    private long toEpochNano() {
        long nod = time.toNanoOfDay();
        long offsetNanos = offset.getAmountSeconds() * 1000000000L;
        return nod - offsetNanos;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code OffsetTime} to another time based on the UTC equivalent times
     * then local time.
     * <p>
     * This ordering is consistent with {@code equals()}.
     * For example, the following is the comparator order:
     * <ol>
     * <li>{@code 10:30+01:00}</li>
     * <li>{@code 11:00+01:00}</li>
     * <li>{@code 12:00+02:00}</li>
     * <li>{@code 11:30+01:00}</li>
     * <li>{@code 12:00+01:00}</li>
     * <li>{@code 12:30+01:00}</li>
     * </ol>
     * Values #2 and #3 represent the same instant on the time-line.
     * When two values represent the same instant, the local time is compared
     * to distinguish them. This step is needed to make the ordering
     * consistent with {@code equals()}.
     *
     * @param other  the other time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if {@code other} is null
     */
    public int compareTo(OffsetTime other) {
        if (offset.equals(other.offset)) {
            return time.compareTo(other.time);
        }
        int compare = MathUtils.safeCompare(toEpochNano(), other.toEpochNano());
        if (compare == 0) {
            compare = time.compareTo(other.time);
        }
        return compare;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the instant of this {@code OffsetTime} is after that of the
     * specified time applying both times to a common date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the time. This is equivalent to converting both
     * times to an instant using the same date and comparing the instants.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is after the instant of the specified time
     */
    public boolean isAfter(OffsetTime other) {
        return toEpochNano() > other.toEpochNano();
    }

    /**
     * Checks if the instant of this {@code OffsetTime} is before that of the
     * specified time applying both times to a common date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the time. This is equivalent to converting both
     * times to an instant using the same date and comparing the instants.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is before the instant of the specified time
     */
    public boolean isBefore(OffsetTime other) {
        return toEpochNano() < other.toEpochNano();
    }

    /**
     * Checks if the instant of this {@code OffsetTime} is equal to that of the
     * specified time applying both times to a common date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} and {@link #equals}
     * in that it only compares the instant of the time. This is equivalent to converting both
     * times to an instant using the same date and comparing the instants.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is equal to the instant of the specified time
     */
    public boolean equalInstant(OffsetTime other) {
        return toEpochNano() == other.toEpochNano();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this time is equal to another time.
     * <p>
     * The comparison is based on the local-time and the offset.
     * To compare for the same instant on the time-line, use {@link #equalInstant}.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other time
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof OffsetTime) {
            OffsetTime other = (OffsetTime) obj;
            return time.equals(other.time) && offset.equals(other.offset);
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
     * The output will be one of the following ISO-8601 formats:
     * <ul>
     * <li>{@code HH:mmXXXXX}</li>
     * <li>{@code HH:mm:ssXXXXX}</li>
     * <li>{@code HH:mm:ssfnnnXXXXX}</li>
     * <li>{@code HH:mm:ssfnnnnnnXXXXX}</li>
     * <li>{@code HH:mm:ssfnnnnnnnnnXXXXX}</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this time, not null
     */
    @Override
    public String toString() {
        return time.toString() + offset.toString();
    }

    /**
     * Outputs this time as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted time string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    public String toString(DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.print(this);
    }

}
