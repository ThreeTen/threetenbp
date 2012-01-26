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

import static javax.time.calendar.ISOChronology.HOURS_PER_DAY;
import static javax.time.calendar.ISOChronology.MINUTES_PER_DAY;
import static javax.time.calendar.ISOChronology.MINUTES_PER_HOUR;
import static javax.time.calendar.ISOChronology.NANOS_PER_DAY;
import static javax.time.calendar.ISOChronology.NANOS_PER_HOUR;
import static javax.time.calendar.ISOChronology.NANOS_PER_MINUTE;
import static javax.time.calendar.ISOChronology.NANOS_PER_SECOND;
import static javax.time.calendar.ISOChronology.SECONDS_PER_DAY;
import static javax.time.calendar.ISOChronology.SECONDS_PER_MINUTE;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_SECOND;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_MINUTE;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.Instant;
import javax.time.MathUtils;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatters;

/**
 * A time without time-zone in the ISO-8601 calendar system,
 * such as {@code 10:15:30}.
 * <p>
 * {@code LocalTime} is an immutable calendrical that represents a time, often
 * viewed as hour-minute-second.
 * <p>
 * This class stores all time fields, to a precision of nanoseconds.
 * It does not store or represent a date or time-zone.
 * For example, the value "13:45.30.123456789" can be stored in a {@code LocalTime}.
 * <p>
 * LocalTime is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class LocalTime
        implements Calendrical, CalendricalMatcher, TimeAdjuster, Comparable<LocalTime>, Serializable {

    /**
     * Constant for the local time of midnight, 00:00.
     */
    public static final LocalTime MIN_TIME;
    /**
     * Constant for the local time just before midnight, 23:59:59.999999999.
     */
    public static final LocalTime MAX_TIME;
    /**
     * Constant for the local time of midnight, 00:00.
     */
    public static final LocalTime MIDNIGHT;
    /**
     * Constant for the local time of midday, 12:00.
     */
    public static final LocalTime MIDDAY;
    /**
     * Constants for the local time of each hour.
     */
    private static final LocalTime[] HOURS = new LocalTime[24];
    static {
        for (int i = 0; i < HOURS.length; i++) {
            HOURS[i] = new LocalTime(i * NANOS_PER_HOUR);
        }
        MIDNIGHT = HOURS[0];
        MIDDAY = HOURS[12];
        MIN_TIME = HOURS[0];
        MAX_TIME = new LocalTime(NANOS_PER_DAY - 1);
    }

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The nano-of-day.
     */
    private final long nod;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code LocalTime}.
     *
     * @return the rule for the time, not null
     */
    public static CalendricalRule<LocalTime> rule() {
        return ISOCalendricalRule.LOCAL_TIME;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the current time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current time.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current time using the system clock, not null
     */
    public static LocalTime now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current time.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current time, not null
     */
    public static LocalTime now(Clock clock) {
        ISOChronology.checkNotNull(clock, "Clock must not be null");
        // inline OffsetTime factory to avoid creating object and InstantProvider checks
        final Instant now = clock.instant();  // called once
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long secsOfDay = now.getEpochSecond() % ISOChronology.SECONDS_PER_DAY;
        secsOfDay = (secsOfDay + offset.getTotalSeconds()) % ISOChronology.SECONDS_PER_DAY;
        if (secsOfDay < 0) {
            secsOfDay += ISOChronology.SECONDS_PER_DAY;
        }
        return LocalTime.ofSecondOfDay(secsOfDay, now.getNanoOfSecond());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalTime} from an hour and minute.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return the local time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static LocalTime of(int hourOfDay, int minuteOfHour) {
        HOUR_OF_DAY.checkValidValue(hourOfDay);
        if (minuteOfHour == 0) {
            return HOURS[hourOfDay];  // for performance
        }
        MINUTE_OF_HOUR.checkValidValue(minuteOfHour);
        return new LocalTime(ISODateTimeRule.packHmsn(hourOfDay, minuteOfHour, 0, 0));
    }

    /**
     * Obtains an instance of {@code LocalTime} from an hour, minute and second.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return the local time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static LocalTime of(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        HOUR_OF_DAY.checkValidValue(hourOfDay);
        if ((minuteOfHour | secondOfMinute) == 0) {
            return HOURS[hourOfDay];  // for performance
        }
        MINUTE_OF_HOUR.checkValidValue(minuteOfHour);
        SECOND_OF_MINUTE.checkValidValue(secondOfMinute);
        return new LocalTime(ISODateTimeRule.packHmsn(hourOfDay, minuteOfHour, secondOfMinute, 0));
    }

    /**
     * Obtains an instance of {@code LocalTime} from an hour, minute, second and nanosecond.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return the local time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static LocalTime of(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        HOUR_OF_DAY.checkValidValue(hourOfDay);
        if ((minuteOfHour | secondOfMinute | nanoOfSecond) == 0) {
            return HOURS[hourOfDay];  // for performance
        }
        MINUTE_OF_HOUR.checkValidValue(minuteOfHour);
        SECOND_OF_MINUTE.checkValidValue(secondOfMinute);
        NANO_OF_SECOND.checkValidValue(nanoOfSecond);
        return new LocalTime(ISODateTimeRule.packHmsn(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalTime} from a second-of-day value.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param secondOfDay  the second-of-day, from {@code 0} to {@code 24 * 60 * 60 - 1}
     * @return the local time, not null
     * @throws IllegalCalendarFieldValueException if the second-of-day value is invalid
     */
    public static LocalTime ofSecondOfDay(long secondOfDay) {
        SECOND_OF_DAY.checkValidValue(secondOfDay);
        return create(secondOfDay * NANOS_PER_SECOND);
    }

    /**
     * Obtains an instance of {@code LocalTime} from a second-of-day value, with
     * associated nanos of second.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param secondOfDay  the second-of-day, from {@code 0} to {@code 24 * 60 * 60 - 1}
     * @param nanoOfSecond  the nano-of-second, from 0 to 999,999,999
     * @return the local time, not null
     * @throws IllegalCalendarFieldValueException if the either input value is invalid
     */
    public static LocalTime ofSecondOfDay(long secondOfDay, int nanoOfSecond) {
        SECOND_OF_DAY.checkValidValue(secondOfDay);
        NANO_OF_SECOND.checkValidValue(nanoOfSecond);
        return create(secondOfDay * NANOS_PER_SECOND + nanoOfSecond);
    }

    /**
     * Obtains an instance of {@code LocalTime} from a nanos-of-day value.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param nanoOfDay  the nano of day, from {@code 0} to {@code 24 * 60 * 60 * 1,000,000,000 - 1}
     * @return the local time, not null
     * @throws CalendricalException if the nanos of day value is invalid
     */
    public static LocalTime ofNanoOfDay(long nanoOfDay) {
        NANO_OF_DAY.checkValidValue(nanoOfDay);
        return create(nanoOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalTime} from a set of calendricals.
     * <p>
     * A calendrical represents some form of date and time information.
     * This method combines the input calendricals into a time.
     *
     * @param calendricals  the calendricals to create a time from, no nulls, not null
     * @return the local time, not null
     * @throws CalendricalException if unable to merge to a local time
     */
    public static LocalTime from(Calendrical... calendricals) {
        return CalendricalEngine.merge(calendricals).deriveChecked(rule());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalTime} from a text string such as {@code 10:15}.
     * <p>
     * The string must represent a valid time and is parsed using
     * {@link DateTimeFormatters#isoLocalTime()}.
     * Hour and minute are required.
     * Seconds and fractional seconds are optional.
     *
     * @param text the text to parse such as "10:15:30", not null
     * @return the parsed local time, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static LocalTime parse(CharSequence text) {
        return DateTimeFormatters.isoLocalTime().parse(text, rule());
    }

    /**
     * Obtains an instance of {@code LocalTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed local time, not null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static LocalTime parse(CharSequence text, DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.parse(text, rule());
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a local time from the nano-of-day value.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param nod  the nano-of-day to represent, validated
     * @return the local time, not null
     */
    private static LocalTime create(long nod) {
        if ((nod % NANOS_PER_HOUR) == 0) {
            return HOURS[(int) (nod / NANOS_PER_HOUR)];
        }
        return new LocalTime(nod);
    }

    /**
     * Constructor, previously validated.
     *
     * @param nod  the nano-of-day to represent, validated
     */
    private LocalTime(long nod) {
        this.nod = nod;
    }

    /**
     * Handle singletons on deserialization.
     * @return the resolved object.
     */
    private Object readResolve() {
        NANO_OF_DAY.checkValidValue(nod);
        return create(nod);
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
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        return CalendricalEngine.derive(ruleToDerive, rule(), null, this, null, null, ISOChronology.INSTANCE, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    public int getHourOfDay() {
        return (int) (nod / NANOS_PER_HOUR);
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return (int) ((nod / NANOS_PER_MINUTE) % MINUTES_PER_HOUR);
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return (int) ((nod / NANOS_PER_SECOND) % SECONDS_PER_MINUTE);
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return (int) (nod % NANOS_PER_SECOND);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the time altered using the adjuster.
     * <p>
     * This adjusts the time according to the rules of the specified adjuster.
     * A simple adjuster might simply set the one of the fields, such as the hour field.
     * A more complex adjuster might set the time to end of the working day.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalTime} based on this time adjusted as necessary, not null
     */
    public LocalTime with(TimeAdjuster adjuster) {
        LocalTime time = adjuster.adjustTime(this);
        if (time == null) {
            throw new NullPointerException("TimeAdjuster implementation must not return null");
        }
        return time;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the hour-of-day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @return a {@code LocalTime} based on this time with the requested hour, not null
     * @throws IllegalCalendarFieldValueException if the hour value is invalid
     */
    public LocalTime withHourOfDay(int hourOfDay) {
        int hod = getHourOfDay();
        if (hourOfDay == hod) {
            return this;
        }
        HOUR_OF_DAY.checkValidValue(hourOfDay);
        return create(nod + (hourOfDay - hod) * NANOS_PER_HOUR);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the minute-of-hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return a {@code LocalTime} based on this time with the requested minute, not null
     * @throws IllegalCalendarFieldValueException if the minute value is invalid
     */
    public LocalTime withMinuteOfHour(int minuteOfHour) {
        int moh = getMinuteOfHour();
        if (minuteOfHour == moh) {
            return this;
        }
        MINUTE_OF_HOUR.checkValidValue(minuteOfHour);
        return create(nod + (minuteOfHour - moh) * NANOS_PER_MINUTE);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the second-of-minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return a {@code LocalTime} based on this time with the requested second, not null
     * @throws IllegalCalendarFieldValueException if the second value is invalid
     */
    public LocalTime withSecondOfMinute(int secondOfMinute) {
        int som = getSecondOfMinute();
        if (secondOfMinute == som) {
            return this;
        }
        SECOND_OF_MINUTE.checkValidValue(secondOfMinute);
        return create(nod + (secondOfMinute - som) * NANOS_PER_SECOND);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the nano-of-second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code LocalTime} based on this time with the requested nanosecond, not null
     * @throws IllegalCalendarFieldValueException if the nanos value is invalid
     */
    public LocalTime withNanoOfSecond(int nanoOfSecond) {
        int nos = getNanoOfSecond();
        if (nanoOfSecond == nos) {
            return this;
        }
        NANO_OF_SECOND.checkValidValue(nanoOfSecond);
        return create(nod + (nanoOfSecond - nos));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period added.
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
     * @return a {@code LocalTime} based on this time with the period added, not null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws ArithmeticException if the period overflows during conversion to hours/minutes/seconds/nanos
     */
    public LocalTime plus(PeriodProvider periodProvider) {
        Period period = Period.ofTimeFields(periodProvider).normalizedWith24HourDays();
        long periodHours = period.getHours();
        long periodMinutes = period.getMinutes();
        long periodSeconds = period.getSeconds();
        long periodNanos = period.getNanos();
        long totNanos = periodNanos % NANOS_PER_DAY +                    //   max  86400000000000
                (periodSeconds % SECONDS_PER_DAY) * NANOS_PER_SECOND +   //   max  86400000000000
                (periodMinutes % MINUTES_PER_DAY) * NANOS_PER_MINUTE +   //   max  86400000000000
                (periodHours % HOURS_PER_DAY) * NANOS_PER_HOUR;          //   max  86400000000000
        return plusNanos(totNanos);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified duration added.
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
     * @return a {@code LocalTime} based on this time with the duration added, not null
     */
    public LocalTime plus(Duration duration) {
        long seconds = duration.getSeconds() % SECONDS_PER_DAY;
        long nanos = seconds * NANOS_PER_SECOND + duration.getNanoOfSecond();
        return plusNanos(nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period in hours added.
     * <p>
     * This adds the specified number of hours to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a {@code LocalTime} based on this time with the hours added, not null
     */
    public LocalTime plusHours(long hours) {
        return plusNanos((hours % HOURS_PER_DAY) * NANOS_PER_HOUR);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in minutes added.
     * <p>
     * This adds the specified number of minutes to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a {@code LocalTime} based on this time with the minutes added, not null
     */
    public LocalTime plusMinutes(long minutes) {
        return plusNanos((minutes % MINUTES_PER_DAY) * NANOS_PER_MINUTE);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in seconds added.
     * <p>
     * This adds the specified number of seconds to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a {@code LocalTime} based on this time with the seconds added, not null
     */
    public LocalTime plusSeconds(long seconds) {
        return plusNanos((seconds % SECONDS_PER_DAY) * NANOS_PER_SECOND);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in nanoseconds added.
     * <p>
     * This adds the specified number of nanoseconds to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a {@code LocalTime} based on this time with the nanoseconds added, not null
     */
    public LocalTime plusNanos(long nanos) {
        if (nanos == 0) {
            return this;
        }
        long newNod = ((nanos % NANOS_PER_DAY) + nod + NANOS_PER_DAY) % NANOS_PER_DAY;
        if (nod == newNod) {
            return this;
        }
        return create(newNod);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period subtracted.
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
     * @return a {@code LocalTime} based on this time with the period subtracted, not null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws ArithmeticException if the period overflows during conversion to hours/minutes/seconds/nanos
     */
    public LocalTime minus(PeriodProvider periodProvider) {
        Period period = Period.ofTimeFields(periodProvider).normalizedWith24HourDays();
        long periodHours = period.getHours();
        long periodMinutes = period.getMinutes();
        long periodSeconds = period.getSeconds();
        long periodNanos = period.getNanos();
        long totNanos = periodNanos % NANOS_PER_DAY +                    //   max  86400000000000
                (periodSeconds % SECONDS_PER_DAY) * NANOS_PER_SECOND +   //   max  86400000000000
                (periodMinutes % MINUTES_PER_DAY) * NANOS_PER_MINUTE +   //   max  86400000000000
                (periodHours % HOURS_PER_DAY) * NANOS_PER_HOUR;          //   max  86400000000000
        return minusNanos(totNanos);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified duration subtracted.
     * <p>
     * This subtracts the specified duration from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * The calculation is equivalent to using {@link #minusSeconds(long)} and
     * {@link #minusNanos(long)} on the two parts of the duration.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a {@code LocalTime} based on this time with the duration subtracted, not null
     */
    public LocalTime minus(Duration duration) {
        long seconds = duration.getSeconds() % SECONDS_PER_DAY;
        long nanos = seconds * NANOS_PER_SECOND + duration.getNanoOfSecond();
        return minusNanos(nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period in hours subtracted.
     * <p>
     * This subtracts the specified number of hours from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return a {@code LocalTime} based on this time with the hours subtracted, not null
     */
    public LocalTime minusHours(long hours) {
        return plusHours(-(hours % HOURS_PER_DAY));
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in minutes subtracted.
     * <p>
     * This subtracts the specified number of minutes from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return a {@code LocalTime} based on this time with the minutes subtracted, not null
     */
    public LocalTime minusMinutes(long minutes) {
        return plusMinutes(-(minutes % MINUTES_PER_DAY));
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in seconds subtracted.
     * <p>
     * This subtracts the specified number of seconds from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return a {@code LocalTime} based on this time with the seconds subtracted, not null
     */
    public LocalTime minusSeconds(long seconds) {
        return plusSeconds(-(seconds % SECONDS_PER_DAY));
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in nanoseconds subtracted.
     * <p>
     * This subtracts the specified number of nanoseconds from this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return a {@code LocalTime} based on this time with the nanoseconds subtracted, not null
     */
    public LocalTime minusNanos(long nanos) {
        return plusNanos(-(nanos % NANOS_PER_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this {@code LocalTime} matches the specified matcher.
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
     * Adjusts a time to have the value of this time.
     * <p>
     * This method implements the {@code TimeAdjuster} interface.
     * It is intended that applications use {@link #with(TimeAdjuster)} rather than this method.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, not null
     */
    public LocalTime adjustTime(LocalTime time) {
        ISOChronology.checkNotNull(time, "LocalTime must not be null");
        return this.equals(time) ? time : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an offset time formed from this time and the specified offset.
     * <p>
     * This merges the two objects - {@code this} and the specified offset -
     * to form an instance of {@code OffsetTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the offset to use, not null
     * @return the offset time formed from this time and the specified offset, not null
     */
    public OffsetTime atOffset(ZoneOffset offset) {
        return OffsetTime.of(this, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this time to a {@code LocalTime}, trivially
     * returning {@code this}.
     *
     * @return {@code this}, not null
     */
    public LocalTime toLocalTime() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts the time as seconds of day,
     * from {@code 0} to {@code 24 * 60 * 60 - 1}.
     *
     * @return the second-of-day equivalent to this time
     */
    public int toSecondOfDay() {
        return (int) (nod / NANOS_PER_SECOND);
    }

    /**
     * Extracts the time as nanos of day,
     * from {@code 0} to {@code 24 * 60 * 60 * 1,000,000,000 - 1}.
     *
     * @return the nano of day equivalent to this time
     */
    public long toNanoOfDay() {
        return nod;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code LocalTime} to another time.
     * <p>
     * The comparison is based on the time-line position of the times within a day.
     *
     * @param other  the other time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if {@code other} is null
     */
    public int compareTo(LocalTime other) {
        return MathUtils.safeCompare(nod, other.nod);
    }

    /**
     * Checks if this {@code LocalTime} is after the specified time.
     * <p>
     * The comparison is based on the time-line position of the time within a day.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is after the specified time
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isAfter(LocalTime other) {
        return nod > other.nod;
    }

    /**
     * Checks if this {@code LocalTime} is before the specified time.
     * <p>
     * The comparison is based on the time-line position of the time within a day.
     *
     * @param other  the other time to compare to, not null
     * @return true if this point is before the specified time
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isBefore(LocalTime other) {
        return nod < other.nod;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this time is equal to another time.
     * <p>
     * The comparison is based on the time-line position of the time within a day.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other time
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LocalTime) {
            LocalTime other = (LocalTime) obj;
            return nod == other.nod;
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
        return (int) (nod ^ (nod >>> 32));
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this time as a {@code String}, such as {@code 10:15}.
     * <p>
     * The output will be one of the following ISO-8601 formats:
     * <ul>
     * <li>{@code HH:mm}</li>
     * <li>{@code HH:mm:ss}</li>
     * <li>{@code HH:mm:ssfnnn}</li>
     * <li>{@code HH:mm:ssfnnnnnn}</li>
     * <li>{@code HH:mm:ssfnnnnnnnnn}</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this time, not null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(18);
        int hourValue = getHourOfDay();
        int minuteValue = getMinuteOfHour();
        int secondValue = getSecondOfMinute();
        int nanoValue = getNanoOfSecond();
        buf.append(hourValue < 10 ? "0" : "").append(hourValue)
            .append(minuteValue < 10 ? ":0" : ":").append(minuteValue);
        if (secondValue > 0 || nanoValue > 0) {
            buf.append(secondValue < 10 ? ":0" : ":").append(secondValue);
            if (nanoValue > 0) {
                buf.append('.');
                if (nanoValue % 1000000 == 0) {
                    buf.append(Integer.toString((nanoValue / 1000000) + 1000).substring(1));
                } else if (nanoValue % 1000 == 0) {
                    buf.append(Integer.toString((nanoValue / 1000) + 1000000).substring(1));
                } else {
                    buf.append(Integer.toString((nanoValue) + 1000000000).substring(1));
                }
            }
        }
        return buf.toString();
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
