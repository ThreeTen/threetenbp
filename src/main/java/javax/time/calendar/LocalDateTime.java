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

import javax.time.CalendricalException;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.calendar.format.DateTimeFormatters;
import javax.time.period.PeriodProvider;

/**
 * A date-time without a time zone in the ISO-8601 calendar system,
 * such as '2007-12-03T10:15:30'.
 * <p>
 * LocalDateTime is an immutable calendrical that represents a date-time, often
 * viewed as year-month-day-hour-minute-second. This object can also access other
 * fields such as day of year, day of week and week of year.
 * <p>
 * This class stores all date and time fields, to a precision of nanoseconds.
 * It does not store or represent a time zone. Thus, for example, the value
 * "2nd October 2007 at 13:45.30.123456789" can be stored in an LocalDateTime.
 * <p>
 * LocalDateTime is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class LocalDateTime
        implements Calendrical, DateTimeProvider, Comparable<LocalDateTime>,
                    CalendricalMatcher, DateAdjuster, TimeAdjuster, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1153828870L;

    /**
     * The date part.
     */
    private final LocalDate date;
    /**
     * The time part.
     */
    private final LocalTime time;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @return the local date-time, never null
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateMidnight(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        return new LocalDateTime(date, LocalTime.MIDNIGHT);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return the local date-time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateMidnight(int year, MonthOfYear monthOfYear, int dayOfMonth) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        return new LocalDateTime(date, LocalTime.MIDNIGHT);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return the local date-time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateMidnight(int year, int monthOfYear, int dayOfMonth) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        return new LocalDateTime(date, LocalTime.MIDNIGHT);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from a date with the
     * time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the local date-time, never null
     */
    public static LocalDateTime dateMidnight(DateProvider dateProvider) {
        LocalDate date = LocalDate.date(dateProvider);
        return new LocalDateTime(date, LocalTime.MIDNIGHT);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @return the local date-time, never null
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateTime(
            Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth,
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param secondOfMinute  the second of minute to represent, not null
     * @return the local date-time, never null
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateTime(
            Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth,
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour, SecondOfMinute secondOfMinute) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month,
     * day, hour, minute, second and nanosecond.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param secondOfMinute  the second of minute to represent, not null
     * @param nanoOfSecond  the nano of second to represent, not null
     * @return the local date-time, never null
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateTime(
            Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth,
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour,
            SecondOfMinute secondOfMinute, NanoOfSecond nanoOfSecond) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return the local date-time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return the local date-time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month,
     * day, hour, minute, second and nanosecond.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return the local date-time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return the local date-time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateTime(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return the local date-time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateTime(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from year, month,
     * day, hour, minute, second and nanosecond.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return the local date-time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime dateTime(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalDateTime</code> from a date and time.
     *
     * @param dateProvider  the date provider to use, not null
     * @param timeProvider  the time provider to use, not null
     * @return the local date-time, never null
     */
    public static LocalDateTime dateTime(DateProvider dateProvider, TimeProvider timeProvider) {
        LocalDate date = LocalDate.date(dateProvider);
        LocalTime time = LocalTime.time(timeProvider);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of <code>LocalTime</code> from a date-time provider.
     * <p>
     * The purpose of this method is to convert a <code>DateTimeProvider</code>
     * to a <code>LocalDateTime</code> in the safest possible way. Specifically,
     * the means checking whether the input parameter is null and
     * whether the result of the provider is null.
     *
     * @param dateTimeProvider  the date-time provider to use, not null
     * @return the local date-time, never null
     */
    public static LocalDateTime dateTime(DateTimeProvider dateTimeProvider) {
        ISOChronology.checkNotNull(dateTimeProvider, "DateTimeProvider must not be null");
        LocalDateTime result = dateTimeProvider.toLocalDateTime();
        ISOChronology.checkNotNull(result, "DateTimeProvider implementation must not return null");
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalDateTime</code> from a text string.
     * <p>
     * The following formats are accepted in ASCII:
     * <ul>
     * <li><code>{Year}-{MonthOfYear}-{DayOfMonth}T{Hour}:{Minute}</code>
     * <li><code>{Year}-{MonthOfYear}-{DayOfMonth}T{Hour}:{Minute}:{Second}</code>
     * <li><code>{Year}-{MonthOfYear}-{DayOfMonth}T{Hour}:{Minute}:{Second}.{NanosecondFraction}</code>
     * </ul>
     * <p>
     * The year has between 4 and 10 digits with values from MIN_YEAR to MAX_YEAR.
     * If there are more than 4 digits then the year must be prefixed with the plus symbol.
     * Negative years are allowed, but not negative zero.
     * <p>
     * The month of year has 2 digits with values from 1 to 12.
     * <p>
     * The day of month has 2 digits with values from 1 to 31 appropriate to the month.
     * <p>
     * The hour has 2 digits with values from 0 to 23.
     * The minute has 2 digits with values from 0 to 59.
     * The second has 2 digits with values from 0 to 59.
     * The nanosecond fraction has from 1 to 9 digits with values from 0 to 999,999,999.
     *
     * @param text  the text to parse such as '2007-12-03T10:15:30', not null
     * @return the parsed local date-time, never null
     * @throws CalendricalParseException if the text cannot be parsed
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDateTime parse(String text) {
        return DateTimeFormatters.isoLocalDateTime().parse(text, rule());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param date  the date part of the date-time, not null
     * @param time  the time part of the date-time, not null
     */
    private LocalDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Returns a copy of this date-time with the new date and time, checking
     * to see if a new object is in fact required.
     *
     * @param newDate  the date of the new date-time, not null
     * @param newTime  the time of the new date-time, not null
     * @return the date-time, never null
     */
    private LocalDateTime withDateTime(LocalDate newDate, LocalTime newTime) {
        if (date == newDate && time == newTime) {
            return this;
        }
        return new LocalDateTime(newDate, newTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that this date-time uses, which is the ISO calendar system.
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
     * If the value cannot be returned for the rule from this date-time then
     * <code>null</code> will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, this, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field as a <code>Year</code>.
     * <p>
     * This method provides access to an object representing the year field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the year, never null
     */
    public Year toYear() {
        return date.toYear();
    }

    /**
     * Gets the month of year field as a <code>MonthOfYear</code>.
     * <p>
     * This method provides access to an object representing the month of year field.
     * This allows operations to be performed on this field in a type-safe manner.
     * <p>
     * This method is the same as {@link #getMonthOfYear()}.
     *
     * @return the month of year, never null
     */
    public MonthOfYear toMonthOfYear() {
        return date.toMonthOfYear();
    }

    /**
     * Gets the day of month field as a <code>DayOfMonth</code>.
     * <p>
     * This method provides access to an object representing the day of month field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the day of month, never null
     */
    public DayOfMonth toDayOfMonth() {
        return date.toDayOfMonth();
    }

    /**
     * Gets the day of year field as a <code>DayOfYear</code>.
     * <p>
     * This method provides access to an object representing the day of year field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the day of year, never null
     */
    public DayOfYear toDayOfYear() {
        return date.toDayOfYear();
    }

    /**
     * Gets the day of week field as a <code>DayOfWeek</code>.
     * <p>
     * This method provides access to an object representing the day of week field.
     * This allows operations to be performed on this field in a type-safe manner.
     * <p>
     * This method is the same as {@link #getDayOfWeek()}.
     *
     * @return the day of week, never null
     */
    public DayOfWeek toDayOfWeek() {
        return date.toDayOfWeek();
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
     * Gets the year field.
     * <p>
     * This method returns the primitive <code>int</code> value for the year.
     * <p>
     * Additional information about the year can be obtained from via {@link #toYear()}.
     * This returns a <code>Year</code> object which includes information on whether
     * this is a leap year and its length in days. It can also be used as a {@link CalendricalMatcher}
     * and a {@link DateAdjuster}.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return date.getYear();
    }

    /**
     * Gets the month of year field, which is an enum <code>MonthOfYear</code>.
     * <p>
     * This method returns the enum {@link MonthOfYear} for the month.
     * This avoids confusion as to what <code>int</code> values mean.
     * If you need access to the primitive <code>int</code> value then the enum
     * provides the {@link MonthOfYear#getValue() int value}.
     * <p>
     * Additional information can be obtained from the <code>MonthOfYear</code>.
     * This includes month lengths, textual names and access to the quarter of year
     * and month of quarter values.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return date.getMonthOfYear();
    }

    /**
     * Gets the day of month field.
     * <p>
     * This method returns the primitive <code>int</code> value for the day of month.
     * <p>
     * Additional information about the day of month can be obtained from via {@link #toDayOfMonth()}.
     * This returns a <code>DayOfMonth</code> object which can be used as a {@link CalendricalMatcher}
     * and a {@link DateAdjuster}.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    /**
     * Gets the day of year field.
     * <p>
     * This method returns the primitive <code>int</code> value for the day of year.
     * <p>
     * Additional information about the day of year can be obtained from via {@link #toDayOfYear()}.
     * This returns a <code>DayOfYear</code> object which can be used as a {@link CalendricalMatcher}
     * and a {@link DateAdjuster}.
     *
     * @return the day of year, from 1 to 365, or 366 in a leap year
     */
    public int getDayOfYear() {
        return date.getDayOfYear();
    }

    /**
     * Gets the day of week field, which is an enum <code>DayOfWeek</code>.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day of week.
     * This avoids confusion as to what <code>int</code> values mean.
     * If you need access to the primitive <code>int</code> value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the <code>DayOfWeek</code>.
     * This includes textual names of the values.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
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
     * Returns a copy of this LocalDateTime with the date altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the date in various ways.
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date to the last day of the month.
     * <p>
     * The adjustment has no effect on the time.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime with(DateAdjuster adjuster) {
        return withDateTime(date.with(adjuster), time);
    }

    /**
     * Returns a copy of this LocalDateTime with the time altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the time in various ways.
     * A simple adjuster might simply set the one of the fields, such as the hour field.
     * A more complex adjuster might set the time to end of the working day.
     * <p>
     * The adjustment has no effect on the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime with(TimeAdjuster adjuster) {
        return withDateTime(date, time.with(adjuster));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDateTime with the year value altered.
     * If the resulting <code>LocalDateTime</code> is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * <p>
     * This method does the same as <code>withYear(year, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     * @see #withYear(int,DateResolver)
     */
    public LocalDateTime withYear(int year) {
        LocalDate newDate = date.withYear(year);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the year value altered.
     * If the resulting <code>LocalDateTime</code> is invalid, it will be resolved using <code>dateResolver</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     * @throws InvalidCalendarFieldException if the year is invalid for the day-month combination
     */
    public LocalDateTime withYear(int year, DateResolver dateResolver) {
        LocalDate newDate = date.withYear(year, dateResolver);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the month of year value altered.
     * If the resulting <code>LocalDateTime</code> is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * <p>
     * This method does the same as <code>withMonthOfYear(monthOfYear, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if the month of year value is invalid
     * @see #withMonthOfYear(int,DateResolver)
     */
    public LocalDateTime withMonthOfYear(int monthOfYear) {
        LocalDate newDate = date.withMonthOfYear(monthOfYear);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the month of year value altered.
     * If the resulting <code>LocalDateTime</code> is invalid, it will be resolved using <code>dateResolver</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if the month of year value is invalid
     */
    public LocalDateTime withMonthOfYear(int monthOfYear, DateResolver dateResolver) {
        LocalDate newDate = date.withMonthOfYear(monthOfYear, dateResolver);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if day of month value is invalid
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public LocalDateTime withDayOfMonth(int dayOfMonth) {
        LocalDate newDate = date.withDayOfMonth(dayOfMonth);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public LocalDateTime withDate(int year, MonthOfYear monthOfYear, int dayOfMonth) {
        if (year == getYear() &&
                monthOfYear == getMonthOfYear() &&
                dayOfMonth == getDayOfMonth()) {
            return this;
        }
        LocalDate newDate = LocalDate.date(year, monthOfYear, dayOfMonth);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public LocalDateTime withDate(int year, int monthOfYear, int dayOfMonth) {
        if (year == getYear() &&
                monthOfYear == getMonthOfYear().getValue() &&
                dayOfMonth == getDayOfMonth()) {
            return this;
        }
        LocalDate newDate = LocalDate.date(year, monthOfYear, dayOfMonth);
        return withDateTime(newDate, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDateTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if the hour value is invalid
     */
    public LocalDateTime withHourOfDay(int hourOfDay) {
        LocalTime newTime = time.withHourOfDay(hourOfDay);
        return withDateTime(date, newTime);
    }

    /**
     * Returns a copy of this LocalDateTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if the minute value is invalid
     */
    public LocalDateTime withMinuteOfHour(int minuteOfHour) {
        LocalTime newTime = time.withMinuteOfHour(minuteOfHour);
        return withDateTime(date, newTime);
    }

    /**
     * Returns a copy of this LocalDateTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if the second value is invalid
     */
    public LocalDateTime withSecondOfMinute(int secondOfMinute) {
        LocalTime newTime = time.withSecondOfMinute(secondOfMinute);
        return withDateTime(date, newTime);
    }

    /**
     * Returns a copy of this LocalDateTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if the nano value is invalid
     */
    public LocalDateTime withNanoOfSecond(int nanoOfSecond) {
        LocalTime newTime = time.withNanoOfSecond(nanoOfSecond);
        return withDateTime(date, newTime);
    }

    /**
     * Returns a copy of this LocalDateTime with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int,int)} and sets
     * the second and nanosecond fields to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public LocalDateTime withTime(int hourOfDay, int minuteOfHour) {
        return withTime(hourOfDay, minuteOfHour, 0, 0);
    }

    /**
     * Returns a copy of this LocalDateTime with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int,int)} and sets
     * the nanosecond fields to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public LocalDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return withTime(hourOfDay, minuteOfHour, secondOfMinute, 0);
    }

    /**
     * Returns a copy of this LocalDateTime with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public LocalDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        if (hourOfDay == getHourOfDay() && minuteOfHour == getMinuteOfHour() &&
                secondOfMinute == getSecondOfMinute() && nanoOfSecond == getNanoOfSecond()) {
            return this;
        }
        LocalTime newTime = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return withDateTime(date, newTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDateTime with the specified period added.
     * <p>
     * This adds the specified period to this date-time.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plus(PeriodProvider periodProvider) {
        LocalDate date = this.date.plus(periodProvider);
        LocalTime.Overflow overflow = this.time.plusWithOverflow(periodProvider);
        return withDateTime(date.plusDays(overflow.getOverflowDays()), overflow.getResultTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDateTime with the specified period in years added.
     * <p>
     * This method add the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) plus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This method does the same as <code>plusYears(years, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusYears(int, javax.time.calendar.DateResolver)
     */
    public LocalDateTime plusYears(int years) {
        LocalDate newDate = date.plusYears(years);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in years added.
     * <p>
     * This method add the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using <code>dateResolver</code> if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusYears(int years, DateResolver dateResolver) {
        LocalDate newDate = date.plusYears(years, dateResolver);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in months added.
     * <p>
     * This method add the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month of year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 plus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This method does the same as <code>plusMonths(months, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusMonths(int, javax.time.calendar.DateResolver)
     */
    public LocalDateTime plusMonths(int months) {
        LocalDate newDate = date.plusMonths(months);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in months added.
     * <p>
     * This method add the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month of year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using <code>dateResolver</code> if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusMonths(int months, DateResolver dateResolver) {
        LocalDate newDate = date.plusMonths(months, dateResolver);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in weeks added.
     * <p>
     * This method add the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusWeeks(int weeks) {
        LocalDate newDate = date.plusWeeks(weeks);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in days added.
     * <p>
     * This method add the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusDays(long days) {
        LocalDate newDate = date.plusDays(days);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusHours(int hours) {
        LocalTime.Overflow overflow = time.plusWithOverflow(hours, 0, 0, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return withDateTime(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusMinutes(int minutes) {
        LocalTime.Overflow overflow = time.plusWithOverflow(0, minutes, 0, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return withDateTime(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusSeconds(int seconds) {
        LocalTime.Overflow overflow = time.plusWithOverflow(0, 0, seconds, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return withDateTime(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusNanos(long nanos) {
        LocalTime.Overflow overflow = time.plusNanosWithOverflow(nanos);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return withDateTime(newDate, overflow.getResultTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDateTime with the specified period subtracted.
     * <p>
     * This subtracts the specified period from this date-time.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minus(PeriodProvider periodProvider) {
        LocalDate date = this.date.minus(periodProvider);
        LocalTime.Overflow overflow = this.time.minusWithOverflow(periodProvider);
        return withDateTime(date.plusDays(overflow.getOverflowDays()), overflow.getResultTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDateTime with the specified period in years subtracted.
     * <p>
     * This method subtract the specified amount to the years field in three steps:
     * <ol>
     * <li>subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This method does the same as <code>minusYears(years, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusYears(int, javax.time.calendar.DateResolver)
     */
    public LocalDateTime minusYears(int years) {
        LocalDate newDate = date.minusYears(years);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in years subtracted.
     * <p>
     * This method subtract the specified amount to the years field in three steps:
     * <ol>
     * <li>subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using <code>dateResolver</code> if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusYears(int years, DateResolver dateResolver) {
        LocalDate newDate = date.minusYears(years, dateResolver);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in months subtracted.
     * <p>
     * This method subtract the specified amount to the months field in three steps:
     * <ol>
     * <li>subtract the input months to the month of year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This method does the same as <code>minusMonts(months, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusMonths(int, javax.time.calendar.DateResolver)
     */
    public LocalDateTime minusMonths(int months) {
        LocalDate newDate = date.minusMonths(months);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in months subtracted.
     * <p>
     * This method subtract the specified amount to the months field in three steps:
     * <ol>
     * <li>subtract the input months to the month of year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using <code>dateResolver</code> if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusMonths(int months, DateResolver dateResolver) {
        LocalDate newDate = date.minusMonths(months, dateResolver);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in weeks subtracted.
     * <p>
     * This method subtract the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusWeeks(int weeks) {
        LocalDate newDate = date.minusWeeks(weeks);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in days subtracted.
     * <p>
     * This method subtract the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusDays(long days) {
        LocalDate newDate = date.minusDays(days);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusHours(int hours) {
        LocalTime.Overflow overflow = time.minusWithOverflow(hours, 0, 0, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return withDateTime(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusMinutes(int minutes) {
        LocalTime.Overflow overflow = time.minusWithOverflow(0, minutes, 0, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return withDateTime(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusSeconds(int seconds) {
        LocalTime.Overflow overflow = time.minusWithOverflow(0, 0, seconds, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return withDateTime(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this LocalDateTime with the specified period in nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusNanos(long nanos) {
        LocalTime.Overflow overflow = time.minusNanosWithOverflow(nanos);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return withDateTime(newDate, overflow.getResultTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this date-time matches the specified matcher.
     * <p>
     * Matchers can be used to query the date-time.
     * A simple matcher might simply compare one of the fields, such as the year field.
     * A more complex matcher might check if the date is the last day of the month.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this date-time matches the matcher, false otherwise
     */
    public boolean matches(CalendricalMatcher matcher) {
        return matcher.matchesCalendrical(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the date-time extracted from the calendrical matches this.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        return this.equals(calendrical.get(rule()));
    }

    /**
     * Adjusts a date to have the value of the date part of this object.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     */
    public LocalDate adjustDate(LocalDate date) {
        return this.date.adjustDate(date);
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
     * Returns an offset date-time formed from this date-time and the specified offset.
     * <p>
     * This merges the two objects - <code>this</code> and the specified offset -
     * to form an instance of <code>OffsetDateTime</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the offset to use, not null
     * @return the offset date-time formed from this date-time and the specified offset, never null
     */
    public OffsetDateTime atOffset(ZoneOffset offset) {
        return OffsetDateTime.dateTime(this, offset);
    }

    /**
     * Returns a zoned date-time formed from this date-time and the specified time-zone.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. When this method converts the date to a date-time it adjusts
     * the time and offset according to the {@link ZoneResolvers#postTransition()} rules.
     * This selects the date-time immediately after a gap and the later offset in overlaps.
     * <p>
     * Finer control over gaps and overlaps is available in two ways.
     * If you simply want to use the earlier offset at overlaps then call
     * {@link ZonedDateTime#withEarlierOffsetAtOverlap()} immediately after this method.
     * Alternately, pass a specific resolver to {@link #atZone(TimeZone, ZoneResolver)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date-time, never null
     */
    public ZonedDateTime atZone(TimeZone zone) {
        return ZonedDateTime.dateTime(this, zone, ZoneResolvers.postTransition());
    }

    /**
     * Returns a zoned date-time formed from this date-time and the specified time-zone
     * taking control of what occurs in time-line gaps and overlaps.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. When this method converts the date to a date-time it adjusts
     * the time and offset according to the specified zone resolver.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @param resolver  the zone resolver to use for gaps and overlaps, not null
     * @return the zoned date-time formed from this date-time, never null
     * @throws CalendricalException if the date-time cannot be resolved
     */
    public ZonedDateTime atZone(TimeZone zone, ZoneResolver resolver) {
        return ZonedDateTime.dateTime(this, zone, resolver);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to a <code>LocalDate</code>.
     *
     * @return a LocalDate representing the date fields of this date-time, never null
     */
    public LocalDate toLocalDate() {
        return date;
    }

    /**
     * Converts this date-time to a <code>LocalTime</code>.
     *
     * @return a LocalTime representing the time fields of this date-time, never null
     */
    public LocalTime toLocalTime() {
        return time;
    }

    /**
     * Converts this date-time to a <code>LocalDateTime</code>,
     * trivially returning <code>this</code>.
     *
     * @return <code>this</code>, never null
     */
    public LocalDateTime toLocalDateTime() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date-time to another date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(LocalDateTime other) {
        int cmp = date.compareTo(other.date);
        if (cmp == 0) {
            cmp = time.compareTo(other.time);
        }
        return cmp;
    }

    /**
     * Is this date-time after the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(LocalDateTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date-time before the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this point is before the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(LocalDateTime other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this date-time equal to the specified date-time.
     *
     * @param other  the other date-time to compare to, null returns false
     * @return true if this point is equal to the specified date-time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LocalDateTime) {
            LocalDateTime dt = (LocalDateTime) other;
            return date.equals(dt.date) && time.equals(dt.time);
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
     * Outputs the date-time as a <code>String</code>, such as
     * '2007-12-03T10:15:30'.
     * <p>
     * The output will be one of the following formats:
     * <ul>
     * <li>'yyyy-MM-ddThh:mm'</li>
     * <li>'yyyy-MM-ddThh:mm:ss'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSS'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSSSS'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSSSSSSS'</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return the formatted date-time string, never null
     */
    @Override
    public String toString() {
        return date + "T" + time;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field rule for <code>LocalDateTime</code>.
     *
     * @return the field rule for the date-time, never null
     */
    public static CalendricalRule<LocalDateTime> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<LocalDateTime> implements Serializable {
        private static final CalendricalRule<LocalDateTime> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(LocalDateTime.class, ISOChronology.INSTANCE, "LocalDateTime");
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected LocalDateTime derive(Calendrical calendrical) {
            OffsetDateTime odt = calendrical.get(OffsetDateTime.rule());
            return odt != null ? odt.toLocalDateTime() : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            ZoneOffset offset = merger.getValue(ZoneOffset.rule());
            if (offset != null) {
                LocalDateTime dateTime = merger.getValue(this);
                merger.storeMerged(OffsetDateTime.rule(), OffsetDateTime.dateTime(dateTime, offset));
                merger.removeProcessed(this);
                merger.removeProcessed(ZoneOffset.rule());
            }
        }
    }

}
