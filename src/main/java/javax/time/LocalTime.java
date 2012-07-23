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
package javax.time;

import static javax.time.DateTimes.HOURS_PER_DAY;
import static javax.time.DateTimes.MICROS_PER_DAY;
import static javax.time.DateTimes.MILLIS_PER_DAY;
import static javax.time.DateTimes.MINUTES_PER_DAY;
import static javax.time.DateTimes.MINUTES_PER_HOUR;
import static javax.time.DateTimes.NANOS_PER_DAY;
import static javax.time.DateTimes.NANOS_PER_HOUR;
import static javax.time.DateTimes.NANOS_PER_MINUTE;
import static javax.time.DateTimes.NANOS_PER_SECOND;
import static javax.time.DateTimes.SECONDS_PER_DAY;
import static javax.time.DateTimes.SECONDS_PER_HOUR;
import static javax.time.DateTimes.SECONDS_PER_MINUTE;
import static javax.time.calendrical.LocalDateTimeField.HOUR_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.MINUTE_OF_HOUR;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_SECOND;
import static javax.time.calendrical.LocalDateTimeField.SECOND_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.SECOND_OF_MINUTE;

import java.io.Serializable;

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.CalendricalFormatter;
import javax.time.format.DateTimeFormatters;

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
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class LocalTime
        implements AdjustableDateTime, DateTimeAdjuster, Comparable<LocalTime>, Serializable {

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
            HOURS[i] = new LocalTime(i, 0, 0, 0);
        }
        MIDNIGHT = HOURS[0];
        MIDDAY = HOURS[12];
        MIN_TIME = HOURS[0];
        MAX_TIME = new LocalTime(23, 59, 59, 999999999);
    }

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

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
        DateTimes.checkNotNull(clock, "Clock must not be null");
        // inline OffsetTime factory to avoid creating object and InstantProvider checks
        final Instant now = clock.instant();  // called once
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long secsOfDay = now.getEpochSecond() % SECONDS_PER_DAY;
        secsOfDay = (secsOfDay + offset.getTotalSeconds()) % SECONDS_PER_DAY;
        if (secsOfDay < 0) {
            secsOfDay += SECONDS_PER_DAY;
        }
        return LocalTime.ofSecondOfDay(secsOfDay, now.getNano());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalTime} from an hour and minute.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return the local time, not null
     * @throws CalendricalException if the value of any field is out of range
     */
    public static LocalTime of(int hour, int minute) {
        HOUR_OF_DAY.checkValidValue(hour);
        if (minute == 0) {
            return HOURS[hour];  // for performance
        }
        MINUTE_OF_HOUR.checkValidValue(minute);
        return new LocalTime(hour, minute, 0, 0);
    }

    /**
     * Obtains an instance of {@code LocalTime} from an hour, minute and second.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return the local time, not null
     * @throws CalendricalException if the value of any field is out of range
     */
    public static LocalTime of(int hour, int minute, int second) {
        HOUR_OF_DAY.checkValidValue(hour);
        if ((minute | second) == 0) {
            return HOURS[hour];  // for performance
        }
        MINUTE_OF_HOUR.checkValidValue(minute);
        SECOND_OF_MINUTE.checkValidValue(second);
        return new LocalTime(hour, minute, second, 0);
    }

    /**
     * Obtains an instance of {@code LocalTime} from an hour, minute, second and nanosecond.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return the local time, not null
     * @throws CalendricalException if the value of any field is out of range
     */
    public static LocalTime of(int hour, int minute, int second, int nanoOfSecond) {
        HOUR_OF_DAY.checkValidValue(hour);
        MINUTE_OF_HOUR.checkValidValue(minute);
        SECOND_OF_MINUTE.checkValidValue(second);
        NANO_OF_SECOND.checkValidValue(nanoOfSecond);
        return create(hour, minute, second, nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalTime} from a second-of-day value.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param secondOfDay  the second-of-day, from {@code 0} to {@code 24 * 60 * 60 - 1}
     * @return the local time, not null
     * @throws CalendricalException if the second-of-day value is invalid
     */
    public static LocalTime ofSecondOfDay(long secondOfDay) {
        SECOND_OF_DAY.checkValidValue(secondOfDay);
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
     * @return the local time, not null
     * @throws CalendricalException if the either input value is invalid
     */
    public static LocalTime ofSecondOfDay(long secondOfDay, int nanoOfSecond) {
        SECOND_OF_DAY.checkValidValue(secondOfDay);
        NANO_OF_SECOND.checkValidValue(nanoOfSecond);
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
     * @return the local time, not null
     * @throws CalendricalException if the nanos of day value is invalid
     */
    public static LocalTime ofNanoOfDay(long nanoOfDay) {
        NANO_OF_DAY.checkValidValue(nanoOfDay);
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
     * Obtains an instance of {@code LocalTime} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code LocalTime}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the local time, not null
     * @throws CalendricalException if unable to convert to a {@code LocalTime}
     */
    public static LocalTime from(DateTime calendrical) {
        LocalTime obj = calendrical.extract(LocalTime.class);
        return DateTimes.ensureNotNull(obj, "Unable to convert calendrical to LocalTime: ", calendrical.getClass());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalTime} from a text string such as {@code 10:15}.
     * <p>
     * The string must represent a valid time and is parsed using
     * {@link javax.time.format.DateTimeFormatters#isoLocalTime()}.
     *
     * @param text the text to parse such as "10:15:30", not null
     * @return the parsed local time, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static LocalTime parse(CharSequence text) {
        return parse(text, DateTimeFormatters.isoLocalTime());
    }

    /**
     * Obtains an instance of {@code LocalTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed local time, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static LocalTime parse(CharSequence text, CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.parse(text, LocalTime.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a local time from the hour, minute, second and nanosecond fields.
     * <p>
     * This factory may return a cached value, but applications must not rely on this.
     *
     * @param hour  the hour-of-day to represent, validated from 0 to 23
     * @param minute  the minute-of-hour to represent, validated from 0 to 59
     * @param second  the second-of-minute to represent, validated from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, validated from 0 to 999,999,999
     * @return the local time, not null
     */
    private static LocalTime create(int hour, int minute, int second, int nanoOfSecond) {
        if ((minute | second | nanoOfSecond) == 0) {
            return HOURS[hour];
        }
        return new LocalTime(hour, minute, second, nanoOfSecond);
    }

    /**
     * Constructor, previously validated.
     *
     * @param hour  the hour-of-day to represent, validated from 0 to 23
     * @param minute  the minute-of-hour to represent, validated from 0 to 59
     * @param second  the second-of-minute to represent, validated from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, validated from 0 to 999,999,999
     */
    private LocalTime(int hour, int minute, int second, int nanoOfSecond) {
        this.hour = (byte) hour;
        this.minute = (byte) minute;
        this.second = (byte) second;
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
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case NANO_OF_SECOND: return nano;
                case NANO_OF_DAY: return toNanoOfDay();
                case MICRO_OF_SECOND: return nano / 1000;
                case MICRO_OF_DAY: return toNanoOfDay() / 1000;
                case MILLI_OF_SECOND: return nano / 1000000;
                case MILLI_OF_DAY: return toNanoOfDay() / 1000000;
                case SECOND_OF_MINUTE: return second;
                case SECOND_OF_DAY: return toSecondOfDay();
                case MINUTE_OF_HOUR: return minute;
                case MINUTE_OF_DAY: return hour * 60 + minute;
                case HOUR_OF_AMPM: return hour % 12;
                case CLOCK_HOUR_OF_AMPM: int ham = hour % 12; return (ham % 12 == 0 ? 12 : ham);
                case HOUR_OF_DAY: return hour;
                case CLOCK_HOUR_OF_DAY: return (hour == 0 ? 24 : hour);
                case AMPM_OF_DAY: return hour / 12;
            }
            throw new CalendricalException(field.getName() + " not valid for LocalTime");
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    public int getHour() {
        return hour;
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecond() {
        return second;
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    public int getNano() {
        return nano;
    }

    //-----------------------------------------------------------------------
    public LocalTime with(DateTimeAdjuster adjuster) {
        return (LocalTime) adjuster.doAdjustment(this);
    }

    /**
     * Returns a copy of this time with the specified field altered.
     * <p>
     * This method returns a new time based on this time with a new value for the specified field.
     * This can be used to change any field, for example to set the hour-of-day.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the returned time, not null
     * @param newValue  the new value of the field in the returned time, not null
     * @return a {@code LocalTime} based on this time with the specified field set, not null
     * @throws CalendricalException if the value is invalid
     */
    public LocalTime with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case NANO_OF_SECOND: return withNano((int) newValue);
                case NANO_OF_DAY: return LocalTime.ofNanoOfDay(newValue);
                case MICRO_OF_SECOND: return withNano((int) newValue * 1000);
                case MICRO_OF_DAY: return plusNanos((newValue - toNanoOfDay() / 1000) * 1000);
                case MILLI_OF_SECOND: return withNano((int) newValue * 1000000);
                case MILLI_OF_DAY: return plusNanos((newValue - toNanoOfDay() / 1000000) * 1000000);
                case SECOND_OF_MINUTE: return withSecond((int) newValue);
                case SECOND_OF_DAY: return plusSeconds(newValue - toSecondOfDay());
                case MINUTE_OF_HOUR: return withMinute((int) newValue);
                case MINUTE_OF_DAY: return plusMinutes(newValue - (hour * 60 + minute));
                case HOUR_OF_AMPM: return plusHours(newValue - (hour % 12));
                case CLOCK_HOUR_OF_AMPM: return plusHours((newValue == 12 ? 0 : newValue) - (hour % 12));
                case HOUR_OF_DAY: return withHour((int) newValue);
                case CLOCK_HOUR_OF_DAY: return withHour((int) (newValue == 24 ? 0 : newValue));
                case AMPM_OF_DAY: return plusHours((newValue - (hour / 12)) * 12);
            }
            throw new CalendricalException(field.getName() + " not valid for LocalTime");
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalTime} with the hour-of-day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @return a {@code LocalTime} based on this time with the requested hour, not null
     * @throws CalendricalException if the hour value is invalid
     */
    public LocalTime withHour(int hour) {
        if (this.hour == hour) {
            return this;
        }
        HOUR_OF_DAY.checkValidValue(hour);
        return create(hour, minute, second, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the minute-of-hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return a {@code LocalTime} based on this time with the requested minute, not null
     * @throws CalendricalException if the minute value is invalid
     */
    public LocalTime withMinute(int minute) {
        if (this.minute == minute) {
            return this;
        }
        MINUTE_OF_HOUR.checkValidValue(minute);
        return create(hour, minute, second, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the second-of-minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return a {@code LocalTime} based on this time with the requested second, not null
     * @throws CalendricalException if the second value is invalid
     */
    public LocalTime withSecond(int second) {
        if (this.second == second) {
            return this;
        }
        SECOND_OF_MINUTE.checkValidValue(second);
        return create(hour, minute, second, nano);
    }

    /**
     * Returns a copy of this {@code LocalTime} with the nano-of-second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code LocalTime} based on this time with the requested nanosecond, not null
     * @throws CalendricalException if the nanos value is invalid
     */
    public LocalTime withNano(int nanoOfSecond) {
        if (this.nano == nanoOfSecond) {
            return this;
        }
        NANO_OF_SECOND.checkValidValue(nanoOfSecond);
        return create(hour, minute, second, nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this time with the specified duration added.
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
        return plusSeconds(duration.getSeconds()).plusNanos(duration.getNano());
    }

    /**
     * Returns a copy of this time with the specified period added.
     * <p>
     * This method returns a new time based on this time with the specified period added.
     * The calculation is delegated to the unit within the period.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a {@code LocalTime} based on this time with the period added, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public LocalTime plus(Period period) {
        return plus(period.getAmount(), period.getUnit());
    }

    /**
     * Returns a copy of this time with the specified period added.
     * <p>
     * This method returns a new time based on this time with the specified period added.
     * This can be used to add any period that is defined by a unit, for example to add hours, minutes or seconds.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to add to the returned time, not null
     * @param unit  the unit of the period to add, not null
     * @return a {@code LocalTime} based on this time with the specified period added, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public LocalTime plus(long periodAmount, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            LocalPeriodUnit f = (LocalPeriodUnit) unit;
            switch (f) {
                case NANOS: return plusNanos(periodAmount);
                case MICROS: return plusNanos((periodAmount % MICROS_PER_DAY) * 1000);
                case MILLIS: return plusNanos((periodAmount % MILLIS_PER_DAY) * 1000000);
                case SECONDS: return plusSeconds(periodAmount);
                case MINUTES: return plusMinutes(periodAmount);
                case HOURS: return plusHours(periodAmount);
                case HALF_DAYS: return plusHours((periodAmount % 2) * 12);
            }
            throw new CalendricalException(unit.getName() + " not valid for LocalTime");
        }
        return unit.doAdd(this, periodAmount);
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
        if (hours == 0) {
            return this;
        }
        int newHour = ((int) (hours % HOURS_PER_DAY) + hour + HOURS_PER_DAY) % HOURS_PER_DAY;
        return create(newHour, minute, second, nano);
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
     * This adds the specified number of seconds to this time, returning a new time.
     * The calculation wraps around midnight.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a {@code LocalTime} based on this time with the seconds added, not null
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
     * Returns a copy of this time with the specified duration subtracted.
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
        return minusSeconds(duration.getSeconds()).minusNanos(duration.getNano());
    }

    /**
     * Returns a copy of this time with the specified period subtracted.
     * <p>
     * This method returns a new time based on this time with the specified period subtracted.
     * The calculation is delegated to the unit within the period.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, not null
     * @return a {@code LocalTime} based on this time with the period subtracted, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public LocalTime minus(Period period) {
        return minus(period.getAmount(), period.getUnit());
    }

    /**
     * Returns a copy of this time with the specified period subtracted.
     * <p>
     * This method returns a new time based on this time with the specified period subtracted.
     * This can be used to subtract any period that is defined by a unit, for example to subtract hours, minutes or seconds.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to subtract from the returned time, not null
     * @param unit  the unit of the period to subtract, not null
     * @return a {@code LocalTime} based on this time with the specified period subtracted, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public LocalTime minus(long periodAmount, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(periodAmount), unit);
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
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTime} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>LocalTime
     * <li>DateTimeBuilder
     * <li>Class, returning {@code LocalTime}
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == LocalTime.class) {
            return (R) this;
        } else if (type == Class.class) {
            return (R) LocalTime.class;
        } else if (type == DateTimeBuilder.class) {
            return (R) new DateTimeBuilder(this);
        }
        return null;
    }

    @Override
    public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
        return calendrical.with(NANO_OF_DAY, toNanoOfDay());
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
        int cmp = DateTimes.safeCompare(hour, other.hour);
        if (cmp == 0) {
            cmp = DateTimes.safeCompare(minute, other.minute);
            if (cmp == 0) {
                cmp = DateTimes.safeCompare(second, other.second);
                if (cmp == 0) {
                    cmp = DateTimes.safeCompare(nano, other.nano);
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
            return hour == other.hour && minute == other.minute &&
                    second == other.second && nano == other.nano;
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
        long nod = toNanoOfDay();
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
     * @return the formatted time string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    public String toString(CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.print(this);
    }

}
