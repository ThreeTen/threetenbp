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
import javax.time.MathUtils;
import javax.time.calendar.format.CalendricalPrintException;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatters;

/**
 * A time without time-zone in the ISO-8601 calendar system,
 * such as '10:15:30'.
 * <p>
 * LocalTime is an immutable calendrical that represents a time, often
 * viewed as hour-minute-second.
 * <p>
 * This class stores all time fields, to a precision of nanoseconds.
 * It does not store or represent a date or time-zone. Thus, for example, the
 * value "13:45.30.123456789" can be stored in a LocalTime.
 * <p>
 * LocalTime is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class LocalTime
        implements Calendrical, TimeProvider, CalendricalMatcher, TimeAdjuster, Comparable<LocalTime>, Serializable {

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
            HOURS[i] = new LocalTime(i, 0, 0, 0);
        }
        MIDNIGHT = HOURS[0];
        MIDDAY = HOURS[12];
    }

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /** Hours per minute. */
    private static final int HOURS_PER_DAY = 24;
    /** Minutes per hour. */
    private static final int MINUTES_PER_HOUR = 60;
    /** Minutes per day. */
    private static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;
    /** Seconds per minute. */
    private static final int SECONDS_PER_MINUTE = 60;
    /** Seconds per hour. */
    private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    /** Seconds per day. */
    private static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    /** Nanos per second. */
    private static final long NANOS_PER_SECOND = 1000000000L;
    /** Nanos per minute. */
    private static final long NANOS_PER_MINUTE = NANOS_PER_SECOND * SECONDS_PER_MINUTE;
    /** Nanos per hour. */
    private static final long NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR;
    /** Nanos per day. */
    private static final long NANOS_PER_DAY = NANOS_PER_HOUR * HOURS_PER_DAY;

    /**
     * The hour.
     */
    private final byte hour;
    /**
     * The minute.
     */
    private final byte minute;
    /**
     * The second.
     */
    private final byte second;
    /**
     * The nanosecond.
     */
    private final int nano;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current time.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current time, never null
     */
    public static LocalTime now(Clock clock) {
        ISOChronology.checkNotNull(clock, "Clock must not be null");
        // inline OffsetTime factory to avoid creating object and InstantProvider checks
        Instant instant = clock.instant();
        ZoneOffset offset = clock.getZone().getRules().getOffset(instant);
        long secsOfDay = instant.getEpochSeconds() % ISOChronology.SECONDS_PER_DAY;
        secsOfDay = (secsOfDay + offset.getAmountSeconds()) % ISOChronology.SECONDS_PER_DAY;
        if (secsOfDay < 0) {
            secsOfDay += ISOChronology.SECONDS_PER_DAY;
        }
        return LocalTime.ofSecondOfDay(secsOfDay, instant.getNanoOfSecond());
    }

    /**
     * Obtains the current time from the system clock in the default time-zone.
     * <p>
     * This will query the system clock in the default time-zone to obtain the current time.
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current time using the system clock, never null
     */
    public static LocalTime nowSystemClock() {
        return now(Clock.systemDefaultZone());
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
     * @return the local time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static LocalTime of(int hourOfDay, int minuteOfHour) {
        ISOChronology.hourOfDayRule().checkValue(hourOfDay);
        if (minuteOfHour == 0) {
            return HOURS[hourOfDay];  // for performance
        }
        ISOChronology.minuteOfHourRule().checkValue(minuteOfHour);
        return new LocalTime(hourOfDay, minuteOfHour, 0, 0);
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
     * @return the local time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static LocalTime of(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        ISOChronology.hourOfDayRule().checkValue(hourOfDay);
        if ((minuteOfHour | secondOfMinute) == 0) {
            return HOURS[hourOfDay];  // for performance
        }
        ISOChronology.minuteOfHourRule().checkValue(minuteOfHour);
        ISOChronology.secondOfMinuteRule().checkValue(secondOfMinute);
        return new LocalTime(hourOfDay, minuteOfHour, secondOfMinute, 0);
    }

//    /**
//     * Obtains an instance of {@code LocalTime}.
//     *
//     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
//     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
//     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59.999,999,999
//     * @return a LocalTime object, never null
//     * @throws IllegalCalendarFieldValueException if any field is invalid
//     */
//    public static LocalTime time(int hourOfDay, int minuteOfHour, double secondOfMinute) {
//        // TODO: check maths and overflow
//        long nanos = Math.round(secondOfMinute * 1000000000);
//        long sec = nanos / 1000000000;
//        int nos = (int) (nanos % 1000000000);
//        if (nos < 0) {
//           nos += 1000000000;
//           sec--;
//        }
//        return time(hourOfDay, minuteOfHour, (int) sec, nos);
//    }

    /**
     * Obtains an instance of {@code LocalTime} from an hour, minute, second and nanosecond.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return the local time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public static LocalTime of(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        ISOChronology.hourOfDayRule().checkValue(hourOfDay);
        ISOChronology.minuteOfHourRule().checkValue(minuteOfHour);
        ISOChronology.secondOfMinuteRule().checkValue(secondOfMinute);
        ISOChronology.nanoOfSecondRule().checkValue(nanoOfSecond);
        return create(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
    }

    /**
     * Obtains an instance of {@code LocalTime} from a time provider.
     * <p>
     * The purpose of this method is to convert a {@code TimeProvider}
     * to a {@code LocalTime} in the safest possible way. Specifically,
     * the means checking whether the input parameter is null and
     * whether the result of the provider is null.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param timeProvider  the time provider to use, not null
     * @return the local time, never null
     */
    public static LocalTime of(TimeProvider timeProvider) {
        ISOChronology.checkNotNull(timeProvider, "TimeProvider must not be null");
        LocalTime result = timeProvider.toLocalTime();
        ISOChronology.checkNotNull(result, "TimeProvider implementation must not return null");
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalTime} from a second-of-day value.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param secondOfDay  the second-of-day, from {@code 0} to {@code 24 * 60 * 60 - 1}
     * @return the local time, never null
     * @throws IllegalCalendarFieldValueException if the second-of-day value is invalid
     */
    public static LocalTime ofSecondOfDay(long secondOfDay) {
        ISOChronology.secondOfDayRule().checkValue(secondOfDay);
        int hours = (int) (secondOfDay / SECONDS_PER_HOUR);
        secondOfDay -= hours * SECONDS_PER_HOUR;
        int minutes = (int) (secondOfDay / SECONDS_PER_MINUTE);
        secondOfDay -= minutes * SECONDS_PER_MINUTE;
        return create(hours, minutes, (int) secondOfDay, 0);
    }

    /**
     * Obtains an instance of {@code LocalTime} from a second-of-day value, with
     * associated nanos of second.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param secondOfDay  the second-of-day, from {@code 0} to {@code 24 * 60 * 60 - 1}
     * @param nanoOfSecond  the nano-of-second, from 0 to 999,999,999
     * @return the local time, never null
     * @throws IllegalCalendarFieldValueException if the either input value is invalid
     */
    public static LocalTime ofSecondOfDay(long secondOfDay, int nanoOfSecond) {
        ISOChronology.secondOfDayRule().checkValue(secondOfDay);
        ISOChronology.nanoOfSecondRule().checkValue(nanoOfSecond);
        int hours = (int) (secondOfDay / SECONDS_PER_HOUR);
        secondOfDay -= hours * SECONDS_PER_HOUR;
        int minutes = (int) (secondOfDay / SECONDS_PER_MINUTE);
        secondOfDay -= minutes * SECONDS_PER_MINUTE;
        return create(hours, minutes, (int) secondOfDay, nanoOfSecond);
    }

    /**
     * Obtains an instance of {@code LocalTime} from a nanos-of-day value.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param nanoOfDay  the nano of day, from {@code 0} to {@code 24 * 60 * 60 * 1,000,000,000 - 1}
     * @return the local time, never null
     * @throws CalendricalException if the nanos of day value is invalid
     */
    public static LocalTime ofNanoOfDay(long nanoOfDay) {
        if (nanoOfDay < 0) {
            throw new CalendricalException("Cannot create LocalTime from nanos of day as value " +
                    nanoOfDay + " must not be negative");
        }
        if (nanoOfDay >= NANOS_PER_DAY) {
            throw new CalendricalException("Cannot create LocalTime from nanos of day as value " +
                    nanoOfDay + " must be less than " + NANOS_PER_DAY);
        }
        int hours = (int) (nanoOfDay / NANOS_PER_HOUR);
        nanoOfDay -= hours * NANOS_PER_HOUR;
        int minutes = (int) (nanoOfDay / NANOS_PER_MINUTE);
        nanoOfDay -= minutes * NANOS_PER_MINUTE;
        int seconds = (int) (nanoOfDay / NANOS_PER_SECOND);
        nanoOfDay -= seconds * NANOS_PER_SECOND;
        return create(hours, minutes, seconds, (int) nanoOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalTime} from a string.
     * <p>
     * The following formats are accepted in ASCII:
     * <ul>
     * <li>{@code {Hour}:{Minute}}
     * <li>{@code {Hour}:{Minute}:{Second}}
     * <li>{@code {Hour}:{Minute}:{Second}.{NanosecondFraction}}
     * </ul>
     * <p>
     * The hour has 2 digits with values from 0 to 23.
     * The minute has 2 digits with values from 0 to 59.
     * The second has 2 digits with values from 0 to 59.
     * The nanosecond fraction has from 1 to 9 digits with values from 0 to 999,999,999.
     *
     * @param text  the text to parse such as '10:15:30', not null
     * @return the parsed local time, never null
     * @throws CalendricalException if the text cannot be parsed
     */
    public static LocalTime parse(String text) {
        return DateTimeFormatters.isoLocalTime().parse(text, rule());
    }

    /**
     * Obtains an instance of {@code LocalTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed local time, never null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalException if the text cannot be parsed
     */
    public static LocalTime parse(String text, DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.parse(text, rule());
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a local time from the hour, minute, second and nanosecond fields.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param hourOfDay  the hour-of-day to represent, validated from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, validated from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, validated from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, validated from 0 to 999,999,999
     * @return the local time, never null
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    private static LocalTime create(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        if ((minuteOfHour | secondOfMinute | nanoOfSecond) == 0) {
            return HOURS[hourOfDay];
        }
        return new LocalTime(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
    }

    /**
     * Constructor, previously validated.
     *
     * @param hourOfDay  the hour-of-day to represent, validated from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, validated from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, validated from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, validated from 0 to 999,999,999
     */
    private LocalTime(
            int hourOfDay, int minuteOfHour,
            int secondOfMinute, int nanoOfSecond) {
        this.hour = (byte) hourOfDay;
        this.minute = (byte) minuteOfHour;
        this.second = (byte) secondOfMinute;
        this.nano = nanoOfSecond;
    }

    /**
     * Handle singletons on deserialization.
     * @return the resolved object.
     */
    private Object readResolve() {
        return create(hour, minute, second, nano);
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
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    public int getHourOfDay() {
        return hour;
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return minute;
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return second;
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return nano;
    }

//    /**
//     * Gets the second and nanosecond, expressed as a double in seconds.
//     *
//     * @return the nano-of-second, from 0 to 59.999,999,999
//     */
//    public double getFractionalSecondOfMinute() {
//        // TODO: check maths and write tests
//        return (((double) nano.getValue()) / 1000000000d) + second.getValue();
//    }
//
//    /**
//     * Gets the time as a fraction of a day, expressed as a double in days.
//     *
//     * @return the nano-of-second, from 0 to &lt; 1
//     */
//    public double getFractionalDay() {
//        // TODO: check maths and write tests
//        return (((double) toNanoOfDay()) / ((double) NANOS_PER_DAY));
//    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the time altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the time in various ways.
     * A simple adjuster might simply set the one of the fields, such as the hour field.
     * A more complex adjuster might set the time to end of the working day.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalTime} based on this time adjusted as necessary, never null
     */
    public LocalTime with(TimeAdjuster adjuster) {
        LocalTime time = adjuster.adjustTime(this);
        if (time == null) {
            throw new NullPointerException("The implementation of TimeAdjuster must not return null");
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
     * @return a {@code LocalTime} based on this time with the requested hour, never null
     * @throws IllegalCalendarFieldValueException if the hour value is invalid
     */
    public LocalTime withHourOfDay(int hourOfDay) {
        if (hourOfDay == hour) {
            return this;
        }
        ISOChronology.hourOfDayRule().checkValue(hourOfDay);
        return create(hourOfDay, minute, second, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the minute-of-hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return a {@code LocalTime} based on this time with the requested minute, never null
     * @throws IllegalCalendarFieldValueException if the minute value is invalid
     */
    public LocalTime withMinuteOfHour(int minuteOfHour) {
        if (minuteOfHour == minute) {
            return this;
        }
        ISOChronology.minuteOfHourRule().checkValue(minuteOfHour);
        return create(hour, minuteOfHour, second, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the second-of-minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return a {@code LocalTime} based on this time with the requested second, never null
     * @throws IllegalCalendarFieldValueException if the second value is invalid
     */
    public LocalTime withSecondOfMinute(int secondOfMinute) {
        if (secondOfMinute == second) {
            return this;
        }
        ISOChronology.secondOfMinuteRule().checkValue(secondOfMinute);
        return create(hour, minute, secondOfMinute, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the nano-of-second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code LocalTime} based on this time with the requested nanosecond, never null
     * @throws IllegalCalendarFieldValueException if the nanos value is invalid
     */
    public LocalTime withNanoOfSecond(int nanoOfSecond) {
        if (nanoOfSecond == nano) {
            return this;
        }
        ISOChronology.nanoOfSecondRule().checkValue(nanoOfSecond);
        return create(hour, minute, second, nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period added.
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
     * @return a {@code LocalTime} based on this time with the period added, never null
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

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period in hours added.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a {@code LocalTime} based on this time with the hours added, never null
     */
    public LocalTime plusHours(long hours) {
        if (hours == 0) {
            return this;
        }
        int newHour = ((int) (hours % HOURS_PER_DAY) + hour + HOURS_PER_DAY) % HOURS_PER_DAY;
        return create(newHour, minute, second, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in minutes added.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a {@code LocalTime} based on this time with the minutes added, never null
     */
    public LocalTime plusMinutes(long minutes) {
        if (minutes == 0) {
            return this;
        }
        int mofd = hour * MINUTES_PER_HOUR + minute;
        int newMofd = ((int) (minutes % MINUTES_PER_DAY) + mofd + MINUTES_PER_DAY) % MINUTES_PER_DAY;
        if (mofd == newMofd) {
            return this;
        }
        int newHour = newMofd / MINUTES_PER_HOUR;
        int newMinute = newMofd % MINUTES_PER_HOUR;
        return create(newHour, newMinute, second, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in seconds added.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a {@code LocalTime} based on this time with the seconds added, never null
     */
    public LocalTime plusSeconds(long seconds) {
        if (seconds == 0) {
            return this;
        }
        int sofd = hour * SECONDS_PER_HOUR +
                    minute * SECONDS_PER_MINUTE + second;
        int newSofd = ((int) (seconds % SECONDS_PER_DAY) + sofd + SECONDS_PER_DAY) % SECONDS_PER_DAY;
        if (sofd == newSofd) {
            return this;
        }
        int newHour = newSofd / SECONDS_PER_HOUR;
        int newMinute = (newSofd / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
        int newSecond = newSofd % SECONDS_PER_MINUTE;
        return create(newHour, newMinute, newSecond, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in nanoseconds added.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a {@code LocalTime} based on this time with the nanoseconds added, never null
     */
    public LocalTime plusNanos(long nanos) {
        if (nanos == 0) {
            return this;
        }
        long nofd = toNanoOfDay();
        long newNofd = ((nanos % NANOS_PER_DAY) + nofd + NANOS_PER_DAY) % NANOS_PER_DAY;
        if (nofd == newNofd) {
            return this;
        }
        int newHour = (int) (newNofd / NANOS_PER_HOUR);
        int newMinute = (int) ((newNofd / NANOS_PER_MINUTE) % MINUTES_PER_HOUR);
        int newSecond = (int) ((newNofd / NANOS_PER_SECOND) % SECONDS_PER_MINUTE);
        int newNano = (int) (newNofd % NANOS_PER_SECOND);
        return create(newHour, newMinute, newSecond, newNano);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period added,
     * returning the new time with any overflow in days.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * addition and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @param minutes the minutes to add, may be negative
     * @param seconds the seconds to add, may be negative
     * @param nanos the nanos to add, may be negative
     * @return an {@code Overflow} instance with the resulting time and overflow, never null
     */
    public Overflow plusWithOverflow(int hours, int minutes, int seconds, long nanos) {
        return plusWithOverflow(hours, minutes, seconds, nanos, 1);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period added,
     * returning the new time with any overflow in days.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * addition and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @param minutes the minutes to add, may be negative
     * @param seconds the seconds to add, may be negative
     * @param nanos the nanos to add, may be negative
     * @return an {@code Overflow} instance with the resulting time and overflow, never null
     */
    private Overflow plusWithOverflow(int hours, int minutes, int seconds, long nanos, int sign) {
        // 9223372036854775808 long, 2147483648 int
        int totDays = (int) (nanos / NANOS_PER_DAY) +      //   max   106751
                seconds / SECONDS_PER_DAY +                //   max    24855
                minutes / MINUTES_PER_DAY +                //   max  1491308
                hours / HOURS_PER_DAY;                     //   max 89478485
        totDays *= sign;                                   // total 91101399
        long totNanos = nanos % NANOS_PER_DAY +                    //   max  86400000000000
                (seconds % SECONDS_PER_DAY) * NANOS_PER_SECOND +   //   max  86400000000000
                (minutes % MINUTES_PER_DAY) * NANOS_PER_MINUTE +   //   max  86400000000000
                (hours % HOURS_PER_DAY) * NANOS_PER_HOUR;          //   max  86400000000000
        if (totNanos == 0) {
            return new Overflow(this, totDays);
        }
        long thisNanos = toNanoOfDay();                            //   max  86400000000000
        totNanos = totNanos * sign + thisNanos;                    // total 432000000000000
        totDays += (int) MathUtils.floorDiv(totNanos, NANOS_PER_DAY);
        totNanos = MathUtils.floorMod(totNanos, NANOS_PER_DAY);
        LocalTime newTime = (totNanos == thisNanos ? this : ofNanoOfDay(totNanos));
        return new Overflow(newTime, totDays);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period subtracted.
     * <p>
     * This subtracts the specified period from this time, returning a new time.
     * Before subtraction, the period is converted to a time-based {@code Period} using
     * {@link Period#ofTimeFields(PeriodProvider)}.
     * That factory ignores any date-based ISO fields, thus subtracting a date-based
     * period from this time will have no effect.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a {@code LocalTime} based on this time with the period subtracted, never null
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

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period in hours subtracted.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return a {@code LocalTime} based on this time with the hours subtracted, never null
     */
    public LocalTime minusHours(long hours) {
        if (hours == 0) {
            return this;
        }
        int newHour = ((int) -(hours % HOURS_PER_DAY) + hour + HOURS_PER_DAY) % HOURS_PER_DAY;
        return create(newHour, minute, second, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in minutes subtracted.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return a {@code LocalTime} based on this time with the minutes subtracted, never null
     */
    public LocalTime minusMinutes(long minutes) {
        if (minutes == 0) {
            return this;
        }
        int mofd = hour * MINUTES_PER_HOUR + minute;
        int newMofd = ((int) -(minutes % MINUTES_PER_DAY) + mofd + MINUTES_PER_DAY) % MINUTES_PER_DAY;
        if (mofd == newMofd) {
            return this;
        }
        int newHour = newMofd / MINUTES_PER_HOUR;
        int newMinute = newMofd % MINUTES_PER_HOUR;
        return create(newHour, newMinute, second, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in seconds subtracted.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return a {@code LocalTime} based on this time with the seconds subtracted, never null
     */
    public LocalTime minusSeconds(long seconds) {
        if (seconds == 0) {
            return this;
        }
        int sofd = hour * SECONDS_PER_HOUR +
                    minute * SECONDS_PER_MINUTE + second;
        int newSofd = ((int) -(seconds % SECONDS_PER_DAY) + sofd + SECONDS_PER_DAY) % SECONDS_PER_DAY;
        if (sofd == newSofd) {
            return this;
        }
        int newHour = newSofd / SECONDS_PER_HOUR;
        int newMinute = (newSofd / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR;
        int newSecond = newSofd % SECONDS_PER_MINUTE;
        return create(newHour, newMinute, newSecond, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the specified period in nanoseconds subtracted.
     * <p>
     * If the resulting hour is lesser than 0 or greater than 23, the hour field <b>rolls</b>.
     * For instance, 24 becomes 0 and -1 becomes 23.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return a {@code LocalTime} based on this time with the nanoseconds subtracted, never null
     */
    public LocalTime minusNanos(long nanos) {
        if (nanos == 0) {
            return this;
        }
        long nofd = toNanoOfDay();
        long newNofd = (-(nanos % NANOS_PER_DAY) + nofd + NANOS_PER_DAY) % NANOS_PER_DAY;
        if (nofd == newNofd) {
            return this;
        }
        int newHour = (int) (newNofd / NANOS_PER_HOUR);
        int newMinute = (int) ((newNofd / NANOS_PER_MINUTE) % MINUTES_PER_HOUR);
        int newSecond = (int) ((newNofd / NANOS_PER_SECOND) % SECONDS_PER_MINUTE);
        int newNano = (int) (newNofd % NANOS_PER_SECOND);
        return create(newHour, newMinute, newSecond, newNano);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the specified period subtracted,
     * returning the new time with any overflow in days.
     * <p>
     * This method returns an {@link Overflow} instance with the result of the
     * subtraction and any overflow in days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @param minutes the minutes to subtract, may be negative
     * @param seconds the seconds to subtract, may be negative
     * @param nanos the nanos to subtract, may be negative
     * @return an {@code Overflow} instance with the resulting time and overflow, never null
     */
    public Overflow minusWithOverflow(int hours, int minutes, int seconds, long nanos) {
        return plusWithOverflow(hours, minutes, seconds, nanos, -1);
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
     * Adjusts a time to have the value of this time.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, never null
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
     * @return the offset time formed from this time and the specified offset, never null
     */
    public OffsetTime atOffset(ZoneOffset offset) {
        return OffsetTime.of(this, offset);
    }

    //-----------------------------------------------------------------------
//    /**
//     * Converts this time to a {@code DateTimeFields} containing the
//     * hour, minute, second and nano fields.
//     *
//     * @return the field set, never null
//     */
//    public DateTimeFields toDateTimeFields() {
//        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
//        map.put(ISOChronology.hourOfDayRule(), (int) hour);
//        map.put(ISOChronology.minuteOfHourRule(), (int) minute);
//        map.put(ISOChronology.secondOfMinuteRule(), (int) second);
//        map.put(ISOChronology.nanoOfSecondRule(), nano);
//        return DateTimeFields.fields(map);
//    }

    /**
     * Converts this time to a {@code LocalTime}, trivially
     * returning {@code this}.
     *
     * @return {@code this}, never null
     */
    public LocalTime toLocalTime() {
        return this;
    }

    /**
     * Returns this time wrapped as an days-overflow.
     * <p>
     * This method will generally only be needed by those writing low-level date
     * and time code that handles days-overflow. An overflow happens when adding
     * or subtracting to a time and the result overflows the range of a time.
     * The number of days later (or earlier) of the result is recorded in the overflow.
     *
     * @param daysOverflow  the number of days to store
     * @return the days-overflow, never null
     */
    public Overflow toOverflow(int daysOverflow) {
        return new Overflow(this, daysOverflow);
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts the time as seconds of day,
     * from {@code 0} to {@code 24 * 60 * 60 - 1}.
     *
     * @return the second-of-day equivalent to this time
     */
    public int toSecondOfDay() {
        int total = hour * SECONDS_PER_HOUR;
        total += minute * SECONDS_PER_MINUTE;
        total += second;
        return total;
    }

//    /**
//     * Extracts the time as millis of day,
//     * from {@code 0} to {@code 24 * 60 * 60 * 1000 - 1}.
//     *
//     * @return the milli of day equivalent to this time
//     */
//    int toMilliOfDay() {
//        long total = toNanoOfDay();
//        return (int) (total / 1000000);
//    }

    /**
     * Extracts the time as nanos of day,
     * from {@code 0} to {@code 24 * 60 * 60 * 1,000,000,000 - 1}.
     *
     * @return the nano of day equivalent to this time
     */
    public long toNanoOfDay() {
        long total = hour * NANOS_PER_HOUR;
        total += minute * NANOS_PER_MINUTE;
        total += second * NANOS_PER_SECOND;
        total += nano;
        return total;
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
        int cmp = MathUtils.safeCompare(hour, other.hour);
        if (cmp == 0) {
            cmp = MathUtils.safeCompare(minute, other.minute);
            if (cmp == 0) {
                cmp = MathUtils.safeCompare(second, other.second);
                if (cmp == 0) {
                    cmp = MathUtils.safeCompare(nano, other.nano);
                }
            }
        }
        return cmp;
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
        return compareTo(other) > 0;
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
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this {@code LocalTime} is equal to the specified time.
     * <p>
     * The comparison is based on the time-line position of the time within a day.
     *
     * @param other  the other time to compare to, null returns false
     * @return true if this point is equal to the specified time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LocalTime) {
            LocalTime localTime = (LocalTime) other;
            return hour == localTime.hour && minute == localTime.minute &&
                    second == localTime.second && nano == localTime.nano;
        }
        return false;
    }

    /**
     * A hash code for this {@code LocalTime}.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        long nod = toNanoOfDay();
        return (int) (nod ^ (nod >>> 32));
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this time as a {@code String}, such as {@code 10:15}.
     * <p>
     * The output will be one of the following formats:
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
     * @return the formatted time, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(18);
        int hourValue = hour;
        int minuteValue = minute;
        int secondValue = second;
        int nanoValue = nano;
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
     * @return the formatted time string, never null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalPrintException if an error occurs during printing
     */
    public String toString(DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.print(this);
    }

//    //-----------------------------------------------------------------------
//    /**
//     * The result of addition to a {@code LocalTime} allowing the expression of
//     * any overflow in days.
//     */
//    public static final class MaybeEndOfDay implements Calendrical, Comparable<MaybeEndOfDay> {
//        /** The time, null if end of day. */
//        private final LocalTime time;
//
//        @Override
//        public <T> T get(CalendricalRule<T> rule) {
//            if (rule == ISOChronology.hourOfDayRule() && time == null) {
//                return rule.reify(24);
//            }
//            return rule().deriveValueFor(rule, time, time);
//        }
//
//        /**
//         * Compares this object to another.
//         *
//         * @param obj  the object to compare to
//         * @return true if equal
//         */
//        @Override
//        public int compareTo(MaybeEndOfDay other) {
//            if (time == null) {
//                return other.time == null ? 0 : 1;
//            }
//            if (other.time == null) {
//                return -1;
//            }
//            return time.compareTo(other.time);
//        }
//
//        /**
//         * Compares this object to another.
//         *
//         * @param obj  the object to compare to
//         * @return true if equal
//         */
//        @Override
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            }
//            if (obj instanceof MaybeEndOfDay) {
//                MaybeEndOfDay other = (MaybeEndOfDay) obj;
//                return time == null ? other.time == null : time.equals(other.time);
//            }
//            return false;
//        }
//
//        /**
//         * Returns a suitable hash code.
//         *
//         * @return the hash code
//         */
//        @Override
//        public int hashCode() {
//            return time == null ? 0 : time.hashCode();
//        }
//
//        /**
//         * Returns a string description of this instance.
//         *
//         * @return the string, never null
//         */
//        @Override
//        public String toString() {
//            return time == null ? "24:00" : time.toString();
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * The result of addition to a {@code LocalTime} allowing the expression of
     * any overflow in days.
     */
    public static final class Overflow {
        /** The time after the addition. */
        private final LocalTime time;
        /** The overflow in days. */
        private final int days;

        /**
         * Constructor.
         *
         * @param time  the {@code LocalTime} after the addition, not null
         * @param days  the overflow in days
         */
        private Overflow(LocalTime time, int days) {
            this.time = time;
            this.days = days;
        }

        /**
         * Gets the time that was the result of the calculation.
         *
         * @return the time, never null
         */
        public LocalTime getResultTime() {
            return time;
        }

        /**
         * Gets the days overflowing from the calculation.
         *
         * @return the overflow days
         */
        public int getOverflowDays() {
            return days;
        }

        /**
         * Creates a {@code LocalDateTime} from the specified date and this instance.
         *
         * @param date  the date to use, not null
         * @return the combination of the date, time and overflow in days, never null
         */
        public LocalDateTime toLocalDateTime(LocalDate date) {
            return LocalDateTime.of(date.plusDays(getOverflowDays()), time);
        }

        /**
         * Compares this object to another.
         *
         * @param obj  the object to compare to
         * @return true if equal
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Overflow) {
                Overflow other = (Overflow) obj;
                return time.equals(other.time) && days == other.days;
            }
            return false;
        }

        /**
         * Returns a suitable hash code.
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            return time.hashCode() + days;
        }

        /**
         * Returns a string description of this instance.
         *
         * @return the string, never null
         */
        @Override
        public String toString() {
            return getResultTime().toString() + " + P" + days + "D";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code LocalTime}.
     *
     * @return the rule for the time, never null
     */
    public static CalendricalRule<LocalTime> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<LocalTime> implements Serializable {
        private static final CalendricalRule<LocalTime> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(LocalTime.class, ISOChronology.INSTANCE, "LocalTime", ISOChronology.periodNanos(), ISOChronology.periodDays());
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected LocalTime derive(Calendrical calendrical) {
            LocalDateTime ldt = calendrical.get(LocalDateTime.rule());
            if (ldt != null) {
                return ldt.toLocalTime();
            }
            OffsetTime ot = calendrical.get(OffsetTime.rule());
            if (ot != null) {
                return ot.toLocalTime();
            }
            return null;
        }
    }

}
