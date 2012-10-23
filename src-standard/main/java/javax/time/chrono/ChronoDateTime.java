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
package javax.time.chrono;

import javax.time.*;
import static javax.time.DateTimes.HOURS_PER_DAY;
import static javax.time.DateTimes.MICROS_PER_DAY;
import static javax.time.DateTimes.MILLIS_PER_DAY;
import static javax.time.DateTimes.MINUTES_PER_DAY;
import static javax.time.DateTimes.NANOS_PER_DAY;
import static javax.time.DateTimes.NANOS_PER_HOUR;
import static javax.time.DateTimes.NANOS_PER_MINUTE;
import static javax.time.DateTimes.NANOS_PER_SECOND;
import static javax.time.DateTimes.SECONDS_PER_DAY;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_DAY;

import java.io.Serializable;

import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjusters;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.CalendricalFormatter;
import javax.time.format.DateTimeFormatters;
import javax.time.format.DateTimeParseException;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;

/**
 * A date-time without a time-zone for the calendar neutral API.
 * <p>
 * {@code ChronoDateTime} is an immutable calendrical that represents a date-time, often
 * viewed as year-month-day-hour-minute-second. This object can also access other
 * fields such as day-of-year, day-of-week and week-of-year.
 * <p>
 * This class stores all date and time fields, to a precision of nanoseconds.
 * It does not store or represent a time-zone. For example, the value
 * "2nd October 2007 at 13:45.30.123456789" can be stored in an {@code ChronoDateTime}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 * 
 * @param C the Chronology of this date
 */
public /* final */ class ChronoDateTime<C extends Chronology<C>>
        implements  DateTime, WithAdjuster, Comparable<ChronoDateTime<C>>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The date part.
     */
    private final ChronoDate<C> date;

    /**
     * The time part.
     */
    private final LocalTime time;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date-time.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock and default time-zone, not null
     */
    public static ChronoDateTime now(String calendar) {
        return now(calendar, Clock.systemDefaultZone());
    }

    /**
     * Obtains the current date-time from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date-time.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock, not null
     */
    public static ChronoDateTime now(String calendar, ZoneId zone) {
        return now(calendar, Clock.system(zone));
    }

    /**
     * Obtains the current date-time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date-time.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date-time, not null
     */
    public static ChronoDateTime now(String calendar, Clock clock) {
        DateTimes.checkNotNull(clock, "Clock must not be null");
        // inline OffsetDateTime factory to avoid creating object and InstantProvider checks
        final Instant now = clock.instant();  // called once
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long localSeconds = now.getEpochSecond() + offset.getTotalSeconds();  // overflow caught later
        return create(Chronology.of(calendar), localSeconds, now.getNano());
    }

    /**
     * Obtains an instance of {@code ChronoDateTime} using seconds from the
     * local epoch of 1970-01-01T00:00:00.
     * <p>
     * The nanosecond field is set to zero.
     *
     * @param chrono the Chronology for which to create the ChronoDate
     * @param localSeconds  the number of seconds from the local epoch of 1970-01-01T00:00:00
     * @param nanoOfSecond  the nanosecond within the second, from 0 to 999,999,999
     * @return the local date-time, not null
     * @throws DateTimeException if the instant exceeds the supported date range
     */
    static ChronoDateTime create(Chronology chrono, long localSeconds, int nanoOfSecond) {
        long epochDays = DateTimes.floorDiv(localSeconds, SECONDS_PER_DAY);
        int secsOfDay = DateTimes.floorMod(localSeconds, SECONDS_PER_DAY);
        ChronoDate date = chrono.dateFromEpochDay(epochDays);
        LocalTime time = LocalTime.ofSecondOfDay(secsOfDay, nanoOfSecond);
        return ChronoDateTime.of(date, time);
    }

    /**
     * Obtains an instance of {@code ChronoDateTime} from a date and time.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @return the local date-time, not null
     */
    public static <R extends Chronology<R>> ChronoDateTime<R> of(ChronoDate<R> date, LocalTime time) {
        DateTimes.checkNotNull(date, "ChronoDate must not be null");
        DateTimes.checkNotNull(time, "LocalTime must not be null");
        return new ChronoDateTime(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ChronoDateTime} using its chronology
     * from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ChronoDateTime}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the local date-time, not null
     * @throws DateTimeException if unable to convert to a {@code ChronoDateTime}
     */
    public static ChronoDateTime<?> from(DateTime calendrical) {
        if (calendrical instanceof ChronoDateTime) {
            return (ChronoDateTime) calendrical;
        }
        ChronoDate<?> date = ChronoDate.from(calendrical);
        LocalTime time = LocalTime.from(calendrical);
        return new ChronoDateTime(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param date  the date part of the date-time, not null
     * @param time  the time part of the date-time, not null
     */
    protected /* private */ ChronoDateTime(ChronoDate<C> date, LocalTime time) {
        DateTimes.checkNotNull(date, "Date must not be null");
        DateTimes.checkNotNull(time, "Time must not be null");
        this.date = date;
        this.time = time;
    }

    /**
     * Returns a copy of this date-time with the new date and time, checking
     * to see if a new object is in fact required.
     * <p>
     * This method must be overridden so the subclass can create its own type
     * of ChronoDateTime.
     *
     * @param newDate  the date of the new date-time, not null
     * @param newTime  the time of the new date-time, not null
     * @return the date-time, not null
     */
    public <R extends Chronology<R>> ChronoDateTime<R> with(ChronoDate<R> newDate, LocalTime newTime) {
        if (date == newDate && time == newTime) {
            return (ChronoDateTime<R>)this;
        }
        return new ChronoDateTime(newDate, newTime);
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            return date.range(field);  // optimize as time fields are fixed
        }
        return field.doRange(this);
    }

    @Override
    public int get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            return (f.isTimeField() ? time.get(field) : date.get(field));
        }
        return field.range().checkValidIntValue(getLong(field), field);
    }

    @Override
    public long getLong(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            return (f.isTimeField() ? time.getLong(field) : date.getLong(field));
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    int getYear() {
        return date.getYear();
    }

    /**
     * Gets the month-of-year field from 1 to 12.
     * <p>
     * This method returns the month as an {@code int} from 1 to 12.
     * Application code is frequently clearer if the enum {@link Month}
     * is used by calling {@link #getMonth()}.
     *
     * @return the month-of-year, from 1 to 12
     * @see #getMonth()
     */
    int getMonthValue() {
        return date.getMonthValue();
    }

    /**
     * Gets the day-of-month field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 31
     */
    int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    /**
     * Gets the day-of-year field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-year.
     *
     * @return the day-of-year, from 1 to 365, or 366 in a leap year
     */
    int getDayOfYear() {
        return date.getDayOfYear();
    }

    /**
     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day-of-week.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the {@code DayOfWeek}.
     * This includes textual names of the values.
     *
     * @return the day-of-week, not null
     */
    DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    int getHour() {
        return time.getHour();
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    int getMinute() {
        return time.getMinute();
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    int getSecond() {
        return time.getSecond();
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    int getNano() {
        return time.getNano();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an adjusted date-time based on this date-time.
     * <p>
     * This adjusts the date-time according to the rules of the specified adjuster.
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date-time to the last day of the month.
     * A selection of common adjustments is provided in {@link DateTimeAdjusters}.
     * These include finding the "last day of the month" and "next Wednesday".
     * The adjuster is responsible for handling special cases, such as the varying
     * lengths of month and leap years.
     * <p>
     * In addition, all principal classes implement the {@link javax.time.calendrical.DateTime.WithAdjuster} interface,
     * including this one. For example, {@link ChronoDate} implements the adjuster interface.
     * As such, this code will compile and run:
     * <pre>
     *  dateTime.with(date);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return a {@code ChronoDateTime} based on this date-time with the adjustment made, not null
     * @throws DateTimeException if the adjustment cannot be made
     */
    @Override
    public ChronoDateTime<C> with(WithAdjuster adjuster) {
        if (adjuster instanceof ChronoDate) {
            return with((ChronoDate) adjuster, time);
        } else if (adjuster instanceof LocalTime) {
            return with(date, (LocalTime) adjuster);
        } else if (adjuster instanceof ChronoDateTime) {
            return (ChronoDateTime) adjuster;
        }
        return (ChronoDateTime) adjuster.doAdjustment(this);
    }

    /**
     * Returns a copy of this date-time with the specified field altered.
     * <p>
     * This method returns a new date-time based on this date-time with a new value for the specified field.
     * This can be used to change any field, for example to set the year, month of day-of-month.
     * <p>
     * In some cases, changing the specified field can cause the resulting date-time to become invalid,
     * such as changing the month from January to February would make the day-of-month 31 invalid.
     * In cases like this, the field is responsible for resolving the date. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the returned date-time, not null
     * @param newValue  the new value of the field in the returned date-time, not null
     * @return a {@code ChronoDateTime} based on this date-time with the specified field set, not null
     * @throws DateTimeException if the value is invalid
     */
    @Override
    public ChronoDateTime<C> with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (f.isTimeField()) {
                return with(date, time.with(field, newValue));
            } else {
                return with(date.with(field, newValue), time);
            }
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ChronoDateTime} with the year altered.
     * The time does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date, from MIN_YEAR to MAX_YEAR
     * @return a {@code ChronoDateTime} based on this date-time with the requested year, not null
     * @throws DateTimeException if the year value is invalid
     */
    ChronoDateTime<C> withYear(int year) {
        return with(date.withYear(year), time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the month-of-year altered.
     * The time does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to set in the returned date, from 1 (January) to 12 (December)
     * @return a {@code ChronoDateTime} based on this date-time with the requested month, not null
     * @throws DateTimeException if the month-of-year value is invalid
     */
    ChronoDateTime<C> withMonth(int month) {
        return with(date.withMonth(month), time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the day-of-month altered.
     * If the resulting {@code ChronoDateTime} is invalid, an exception is thrown.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 28-31
     * @return a {@code ChronoDateTime} based on this date-time with the requested day, not null
     * @throws DateTimeException if the day-of-month value is invalid
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    ChronoDateTime<C> withDayOfMonth(int dayOfMonth) {
        return with(date.withDayOfMonth(dayOfMonth), time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the day-of-year altered.
     * If the resulting {@code ChronoDateTime} is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return a {@code ChronoDateTime} based on this date with the requested day, not null
     * @throws DateTimeException if the day-of-year value is invalid
     * @throws DateTimeException if the day-of-year is invalid for the year
     */
    ChronoDateTime<C> withDayOfYear(int dayOfYear) {
        return with(date.withDayOfYear(dayOfYear), time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return a {@code ChronoDateTime} based on this date-time with the requested date, not null
     * @throws DateTimeException if any field value is invalid
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    ChronoDateTime<C> withDate(int year, int month, int dayOfMonth) {
        if (year == getYear() &&
                month == getMonthValue() &&
                dayOfMonth == getDayOfMonth()) {
            return this;
        }
        ChronoDate newDate = date.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth);
        return with(newDate, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ChronoDateTime} with the hour-of-day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @return a {@code ChronoDateTime} based on this date-time with the requested hour, not null
     * @throws DateTimeException if the hour value is invalid
     */
    ChronoDateTime<C> withHour(int hour) {
        LocalTime newTime = time.withHour(hour);
        return with(date, newTime);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the minute-of-hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return a {@code ChronoDateTime} based on this date-time with the requested minute, not null
     * @throws DateTimeException if the minute value is invalid
     */
    ChronoDateTime<C> withMinute(int minute) {
        LocalTime newTime = time.withMinute(minute);
        return with(date, newTime);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the second-of-minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return a {@code ChronoDateTime} based on this date-time with the requested second, not null
     * @throws DateTimeException if the second value is invalid
     */
    ChronoDateTime<C> withSecond(int second) {
        LocalTime newTime = time.withSecond(second);
        return with(date, newTime);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the nano-of-second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code ChronoDateTime} based on this date-time with the requested nanosecond, not null
     * @throws DateTimeException if the nano value is invalid
     */
    ChronoDateTime<C> withNano(int nanoOfSecond) {
        LocalTime newTime = time.withNano(nanoOfSecond);
        return with(date, newTime);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int,int)} and sets
     * the second and nanosecond fields to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return a {@code ChronoDateTime} based on this date-time with the requested time, not null
     * @throws DateTimeException if any field value is invalid
     */
    ChronoDateTime<C> withTime(int hour, int minute) {
        return withTime(hour, minute, 0, 0);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int,int)} and sets
     * the nanosecond fields to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return a {@code ChronoDateTime} based on this date-time with the requested time, not null
     * @throws DateTimeException if any field value is invalid
     */
    ChronoDateTime<C> withTime(int hour, int minute, int second) {
        return withTime(hour, minute, second, 0);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code ChronoDateTime} based on this date-time with the requested time, not null
     * @throws DateTimeException if any field value is invalid
     */
    ChronoDateTime<C> withTime(int hour, int minute, int second, int nanoOfSecond) {
        if (hour == getHour() && minute == getMinute() &&
                second == getSecond() && nanoOfSecond == getNano()) {
            return this;
        }
        LocalTime newTime = LocalTime.of(hour, minute, second, nanoOfSecond);
        return with(date, newTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified period added.
     * <p>
     * This method returns a new date-time based on this time with the specified period added.
     * The adjuster is typically {@link Period} but may be any other type implementing
     * the {@link javax.time.calendrical.DateTime.PlusAdjuster} interface.
     * The calculation is delegated to the specified adjuster, which typically calls
     * back to {@link #plus(long, PeriodUnit)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalDateTime} based on this date-time with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public LocalDateTime plus(PlusAdjuster adjuster) {
        return (LocalDateTime) adjuster.doAdd(this);
    }

    /**
     * Returns a copy of this date-time with the specified period added.
     * <p>
     * This method returns a new date-time based on this date-time with the specified period added.
     * This can be used to add any period that is defined by a unit, for example to add years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to add to the returned date-time, not null
     * @param unit  the unit of the period to add, not null
     * @return a {@code ChronoDateTime} based on this date-time with the specified period added, not null
     * @throws DateTimeException if the unit cannot be added to this type
     */
    @Override
    public ChronoDateTime<C> plus(long periodAmount, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            LocalPeriodUnit f = (LocalPeriodUnit) unit;
            switch (f) {
                case NANOS: return plusNanos(periodAmount);
                case MICROS: return plusDays(periodAmount / MICROS_PER_DAY).plusNanos((periodAmount % MICROS_PER_DAY) * 1000);
                case MILLIS: return plusDays(periodAmount / MILLIS_PER_DAY).plusNanos((periodAmount % MILLIS_PER_DAY) * 1000000);
                case SECONDS: return plusSeconds(periodAmount);
                case MINUTES: return plusMinutes(periodAmount);
                case HOURS: return plusHours(periodAmount);
                case HALF_DAYS: return plusDays(periodAmount / 256).plusHours((periodAmount % 256) * 12);  // no overflow (256 is multiple of 2)
            }
            return with(date.plus(periodAmount, unit), time);
        }
        return unit.doAdd(this, periodAmount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in years added.
     * <p>
     * This method adds the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) plus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime plusYears(long years) {
        ChronoDate newDate = date.plusYears(years);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 plus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the months added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> plusMonths(long months) {
        ChronoDate newDate = date.plusMonths(months);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in weeks added.
     * <p>
     * This method adds the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the weeks added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> plusWeeks(long weeks) {
        ChronoDate newDate = date.plusWeeks(weeks);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in days added.
     * <p>
     * This method adds the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the days added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> plusDays(long days) {
        ChronoDate newDate = date.plusDays(days);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the hours added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> plusHours(long hours) {
        return plusWithOverflow(date, hours, 0, 0, 0, 1);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the minutes added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> plusMinutes(long minutes) {
        return plusWithOverflow(date, 0, minutes, 0, 0, 1);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the seconds added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> plusSeconds(long seconds) {
        return plusWithOverflow(date, 0, 0, seconds, 0, 1);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the nanoseconds added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> plusNanos(long nanos) {
        return plusWithOverflow(date, 0, 0, 0, nanos, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified period subtracted.
     * <p>
     * This method returns a new date-time based on this time with the specified period subtracted.
     * The adjuster is typically {@link Period} but may be any other type implementing
     * the {@link javax.time.calendrical.DateTime.MinusAdjuster} interface.
     * The calculation is delegated to the specified adjuster, which typically calls
     * back to {@link #minus(long, PeriodUnit)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalDateTime} based on this date-time with the subtraction made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public LocalDateTime minus(MinusAdjuster adjuster) {
        return (LocalDateTime) adjuster.doSubtract(this);
    }

    /**
     * Returns a copy of this date-time with the specified period subtracted.
     * <p>
     * This method returns a new date-time based on this date-time with the specified period subtracted.
     * This can be used to subtract any period that is defined by a unit, for example to subtract years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to subtract from the returned date-time, not null
     * @param unit  the unit of the period to subtract, not null
     * @return a {@code ChronoDateTime} based on this date-time with the specified period subtracted, not null
     * @throws DateTimeException if the unit cannot be added to this type
     */
    @Override
    public ChronoDateTime<C> minus(long periodAmount, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(periodAmount), unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in years subtracted.
     * <p>
     * This method subtracts the specified amount from the years field in three steps:
     * <ol>
     * <li>Subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the years subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> minusYears(long years) {
        ChronoDate newDate = date.minusYears(years);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in months subtracted.
     * <p>
     * This method subtracts the specified amount from the months field in three steps:
     * <ol>
     * <li>Subtract the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the months subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> minusMonths(long months) {
        ChronoDate newDate = date.minusMonths(months);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks from the days field decrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the weeks subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> minusWeeks(long weeks) {
        ChronoDate newDate = date.minusWeeks(weeks);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in days subtracted.
     * <p>
     * This method subtracts the specified amount from the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the days subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> minusDays(long days) {
        ChronoDate newDate = date.minusDays(days);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the hours subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> minusHours(long hours) {
        return plusWithOverflow(date, hours, 0, 0, 0, -1);
   }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the minutes subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> minusMinutes(long minutes) {
        return plusWithOverflow(date, 0, minutes, 0, 0, -1);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the seconds subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> minusSeconds(long seconds) {
        return plusWithOverflow(date, 0, 0, seconds, 0, -1);
    }

    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period in nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return a {@code ChronoDateTime} based on this date-time with the nanoseconds subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateTime<C> minusNanos(long nanos) {
        return plusWithOverflow(date, 0, 0, 0, nanos, -1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ChronoDateTime} with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param newDate  the new date to base the calculation on, not null
     * @param hours  the hours to add, may be negative
     * @param minutes the minutes to add, may be negative
     * @param seconds the seconds to add, may be negative
     * @param nanos the nanos to add, may be negative
     * @param sign  the sign to determine add or subtract
     * @return a long nanos-from-midnight value, holding the nano-of-day time and days overflow
     */
    private ChronoDateTime<C> plusWithOverflow(ChronoDate newDate, long hours, long minutes, long seconds, long nanos, int sign) {
        // 9223372036854775808 long, 2147483648 int
        if ((hours | minutes | seconds | nanos) == 0) {
            return with(newDate, time);
        }
        long totDays = nanos / NANOS_PER_DAY +             //   max/24*60*60*1B
                seconds / SECONDS_PER_DAY +                //   max/24*60*60
                minutes / MINUTES_PER_DAY +                //   max/24*60
                hours / HOURS_PER_DAY;                     //   max/24
        totDays *= sign;                                   // total max*0.4237...
        long totNanos = nanos % NANOS_PER_DAY +                    //   max  86400000000000
                (seconds % SECONDS_PER_DAY) * NANOS_PER_SECOND +   //   max  86400000000000
                (minutes % MINUTES_PER_DAY) * NANOS_PER_MINUTE +   //   max  86400000000000
                (hours % HOURS_PER_DAY) * NANOS_PER_HOUR;          //   max  86400000000000
        long curNoD = time.toNanoOfDay();                       //   max  86400000000000
        totNanos = totNanos * sign + curNoD;                    // total 432000000000000
        totDays += DateTimes.floorDiv(totNanos, NANOS_PER_DAY);
        long newNoD = DateTimes.floorMod(totNanos, NANOS_PER_DAY);
        LocalTime newTime = (newNoD == curNoD ? time : LocalTime.ofNanoOfDay(newNoD));
        return with(newDate.plusDays(totDays), newTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an offset date-time formed from this date-time and the specified offset.
     * <p>
     * This merges the two objects - {@code this} and the specified offset -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the offset to use, not null
     * @return the offset date-time formed from this date-time and the specified offset, not null
     */
    public ChronoOffsetDateTime<C> atOffset(ZoneOffset offset) {
        return ChronoOffsetDateTime.of(this, offset);
    }

    /**
     * Returns a zoned date-time formed from this date-time and the specified time-zone.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. If the local date-time is in a gap or overlap according to
     * the rules then a resolver is used to determine the resultant local time and offset.
     * This method uses the {@link ZoneResolvers#postGapPreOverlap() post-gap pre-overlap} resolver.
     * This selects the date-time immediately after a gap and the earlier offset in overlaps.
     * <p>
     * Finer control over gaps and overlaps is available in two ways.
     * If you simply want to use the later offset at overlaps then call
     * {@link javax.time.ZonedDateTime#withLaterOffsetAtOverlap()} immediately after this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date-time, not null
     */
    ///public ChronoZonedDateTime<C> atZone(ZoneId zone) {
    ///    return ChronoZonedDateTime.of(this, zone, ZoneResolvers.postGapPreOverlap());
    ///}

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTime} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>ChronoDate
     * <li>LocalTime
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == ChronoDate.class) {
            return (R) date;
        } else if (type == LocalDate.class) {
            return (R) date.toLocalDate();
        } else if (type == LocalTime.class) {
            return (R) time;
        }
        return null;
    }

    @Override
    public DateTime doAdjustment(DateTime datetime) {
        return datetime.with(EPOCH_DAY, date.toEpochDay()).with(NANO_OF_DAY, time.toNanoOfDay());
    }

    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof ChronoDateTime == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
        ChronoDateTime end = (ChronoDateTime) endDateTime;
        if (unit instanceof LocalPeriodUnit) {
            LocalPeriodUnit f = (LocalPeriodUnit) unit;
            if (f.isTimeUnit()) {
                long amount = end.date.toEpochDay() - date.toEpochDay();
                switch (f) {
                    case NANOS: amount = DateTimes.safeMultiply(amount, NANOS_PER_DAY); break;
                    case MICROS: amount = DateTimes.safeMultiply(amount, MICROS_PER_DAY); break;
                    case MILLIS: amount = DateTimes.safeMultiply(amount, MILLIS_PER_DAY); break;
                    case SECONDS: amount = DateTimes.safeMultiply(amount, SECONDS_PER_DAY); break;
                    case MINUTES: amount = DateTimes.safeMultiply(amount, MINUTES_PER_DAY); break;
                    case HOURS: amount = DateTimes.safeMultiply(amount, HOURS_PER_DAY); break;
                    case HALF_DAYS: amount = DateTimes.safeMultiply(amount, 2); break;
                }
                return DateTimes.safeAdd(amount, time.periodUntil(end.time, unit));
            }
            ChronoDate endDate = end.date;
            if (end.time.isBefore(time)) {
                endDate = endDate.minusDays(1);
            }
            return date.periodUntil(endDate, unit);
        }
        return unit.between(this, endDateTime).getAmount();
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to a {@code ChronoDate}.
     *
     * @return a ChronoDate representing the date fields of this date-time, not null
     */
    public ChronoDate<C> getChronoDate() {
        return date;
    }
    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to a {@code LocalDate}.
     *
     * @return a ChronoDate representing the date fields of this date-time, not null
     */
    public LocalDate toLocalDate() {
        return LocalDate.from(date);
    }
 
    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to a {@code LocalDateTime}.
     *
     * @return a LocalDateTime representing the date fields of this date-time, not null
     */
    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(LocalDate.from(date), time);
    }


    /**
     * Converts this date-time to a {@code LocalTime}.
     *
     * @return a LocalTime representing the time fields of this date-time, not null
     */
    public LocalTime toLocalTime() {
        return time;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code ChronoDateTime} to another date-time.
     * <p>
     * The comparison is based on the time-line position of the date-times.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(ChronoDateTime other) {
        int cmp = date.compareTo(other.date);
        if (cmp == 0) {
            cmp = time.compareTo(other.time);
        }
        return cmp;
    }

    /**
     * Checks if this {@code ChronoDateTime} is after the specified date-time.
     * <p>
     * The comparison is based on the time-line position of the date-times.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the specified date-time
     */
    boolean isAfter(ChronoDateTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if this {@code ChronoDateTime} is before the specified date-time.
     * <p>
     * The comparison is based on the time-line position of the date-times.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is before the specified date-time
     */
    boolean isBefore(ChronoDateTime other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date-time is equal to another date-time.
     * <p>
     * The comparison is based on the time-line position of the date-times.
     * Only objects of type {@code ChronoDateTime} are compared, other types return false.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date-time
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChronoDateTime) {
            ChronoDateTime other = (ChronoDateTime) obj;
            return date.equals(other.date) && time.equals(other.time);
        }
        return false;
    }

    /**
     * A hash code for this date-time.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return date.hashCode() ^ time.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date-time as a {@code String}, such as {@code 2007-12-03T10:15:30}.
     * <p>
     * The output will be one of the following ISO-8601 formats:
     * <ul>
     * <li>{@code yyyy-MM-dd'T'HH:mm}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnn}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnnnn}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnnnnnnn}</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this date-time, not null
     */
    @Override
    public String toString() {
        return date.toString() + 'T' + time.toString();
    }

    /**
     * Outputs this date-time as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted date-time string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws DateTimeException if an error occurs during printing
     */
    public String toString(CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.print(this);
    }

}
