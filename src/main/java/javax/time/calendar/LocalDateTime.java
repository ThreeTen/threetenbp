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

import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.period.PeriodView;
import javax.time.period.Periods;

/**
 * A calendrical representation of a date-time without a time zone,
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
 * LocalDateTime is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class LocalDateTime
        implements ReadableDateTime, Calendrical, Comparable<LocalDateTime>, Serializable {

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
     * Obtains an instance of <code>LocalDateTime</code> with the time set
     * to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @return a LocalDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalDateTime dateMidnight(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        return new LocalDateTime(date, LocalTime.MIDNIGHT);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> with the time set
     * to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a LocalDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalDateTime dateMidnight(int year, MonthOfYear monthOfYear, int dayOfMonth) {
        return dateMidnight(year, monthOfYear.getValue(), dayOfMonth);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> with the time set
     * to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a LocalDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalDateTime dateMidnight(int year, int monthOfYear, int dayOfMonth) {
        return dateMidnight(year, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code>.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a LocalDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalDateTime dateTime(int year, MonthOfYear monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        return dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, 0, 0);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code>.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a LocalDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalDateTime dateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        return dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, 0, 0);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code>.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a LocalDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalDateTime dateTime(int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, 0);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code>.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a LocalDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalDateTime dateTime(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, 0);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code>.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a LocalDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalDateTime dateTime(int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code>.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a LocalDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static LocalDateTime dateTime(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of <code>LocalDateTime</code> from a date and time.
     *
     * @param date  the date to represent, not null
     * @param time  the time to represent, not null
     * @return a LocalDateTime object, never null
     */
    public static LocalDateTime dateTime(LocalDate date, LocalTime time) {
        if (date == null) {
            throw new NullPointerException("The date must not be null");
        }
        if (time == null) {
            throw new NullPointerException("The time must not be null");
        }
        return new LocalDateTime(date, time);
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
     * Gets the calendrical state which provides internal access to
     * this date-time.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    /**
     * Gets the chronology that describes the calendar system rules for
     * this date-time.
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
     * This method queries whether this <code>LocalDateTime</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return field.isSupported(Periods.NANOS, Periods.FOREVER);
    }

    /**
     * Gets the value of the specified calendar field.
     * <p>
     * This method queries the value of the specified calendar field.
     * If the calendar field is not supported then an exception is thrown.
     *
     * @param field  the field to query, not null
     * @return the value for the field
     * @throws UnsupportedCalendarFieldException if the field is not supported
     */
    public int get(TimeFieldRule field) {
        if (!isSupported(field)) {
            throw new UnsupportedCalendarFieldException(field, "date-time");
        }
        if (date.isSupported(field)) {
            return date.get(field);
        } else if (time.isSupported(field)) {
            return time.get(field);
        } else {
            return field.getValue(getCalendricalState());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>YearMonth</code> initialised to the
     * year and month of this date-time.
     *
     * @return the year-month object, never null
     */
    public YearMonth yearMonth() {
        return date.yearMonth();
    }

    /**
     * Gets an instance of <code>MonthDay</code> initialised to the
     * month and day of month of this date-time.
     *
     * @return the month-day object, never null
     */
    public MonthDay monthDay() {
        return date.monthDay();
    }

    /**
     * Gets an instance of <code>LocalDate</code> initialised to the
     * date of this date-time.
     *
     * @return the date object, never null
     */
    public LocalDate date() {
        return date;
    }

    /**
     * Gets an instance of <code>LocalTime</code> initialised to the
     * time of this date-time.
     *
     * @return the time object, never null
     */
    public LocalTime time() {
        return time;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method provides access to an object representing the year field.
     * This can be used to access the {@link Year#getValue() int value}.
     *
     * @return the year, never null
     */
    public Year getYear() {
        return date.getYear();
    }

    /**
     * Gets the month of year field.
     * <p>
     * This method provides access to an object representing the month field.
     * This can be used to access the {@link MonthOfYear#getValue() int value}.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return date.getMonthOfYear();
    }

    /**
     * Gets the day of month field.
     * <p>
     * This method provides access to an object representing the day of month field.
     * This can be used to access the {@link DayOfMonth#getValue() int value}.
     *
     * @return the day of month, never null
     */
    public DayOfMonth getDayOfMonth() {
        return date.getDayOfMonth();
    }

    /**
     * Gets the day of year field.
     * <p>
     * This method provides access to an object representing the day of year field.
     * This can be used to access the {@link DayOfYear#getValue() int value}.
     *
     * @return the day of year, never null
     */
    public DayOfYear getDayOfYear() {
        return date.getDayOfYear();
    }

    /**
     * Gets the day of week field.
     * <p>
     * This method provides access to an object representing the day of week field.
     * This can be used to access the {@link DayOfWeek#getValue() int value}.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day field.
     * <p>
     * This method provides access to an object representing the hour of day field.
     * This can be used to access the {@link HourOfDay#getValue() int value}.
     *
     * @return the hour of day, never null
     */
    public HourOfDay getHourOfDay() {
        return time.getHourOfDay();
    }

    /**
     * Gets the minute of hour field.
     * <p>
     * This method provides access to an object representing the minute of hour field.
     * This can be used to access the {@link MinuteOfHour#getValue() int value}.
     *
     * @return the minute of hour, never null
     */
    public MinuteOfHour getMinuteOfHour() {
        return time.getMinuteOfHour();
    }

    /**
     * Gets the second of minute field.
     * <p>
     * This method provides access to an object representing the second of minute field.
     * This can be used to access the {@link SecondOfMinute#getValue() int value}.
     *
     * @return the second of minute, never null
     */
    public SecondOfMinute getSecondOfMinute() {
        return time.getSecondOfMinute();
    }

    /**
     * Gets the nano of second field.
     * <p>
     * This method provides access to an object representing the nano of second field.
     * This can be used to access the {@link NanoOfSecond#getValue() int value}.
     *
     * @return the nano of second, never null
     */
    public NanoOfSecond getNanoOfSecond() {
        return time.getNanoOfSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDateTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime with(Calendrical calendrical) {
        if (calendrical instanceof LocalDate) {
            return withDateTime((LocalDate) calendrical, time);
        }
        if (calendrical instanceof LocalTime) {
            return withDateTime(date, (LocalTime) calendrical);
        }
        // TODO
        return null;
    }

    /**
     * Returns a copy of this LocalDateTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime with(Calendrical... calendricals) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDateTime with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime withYear(int year) {
        LocalDate newDate = date.withYear(year);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime withMonthOfYear(int monthOfYear) {
        LocalDate newDate = date.withMonthOfYear(monthOfYear);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime withDayOfMonth(int dayOfMonth) {
        LocalDate newDate = date.withDayOfMonth(dayOfMonth);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime withLastDayOfMonth() {
        LocalDate newDate = date.withLastDayOfMonth();
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the day of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent, from 1 to 366
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime withDayOfYear(int dayOfYear) {
        LocalDate newDate = date.withDayOfYear(dayOfYear);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime withLastDayOfYear() {
        LocalDate newDate = date.withLastDayOfYear();
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent, from 1 (Monday) to 7 (Sunday)
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime withDayOfWeek(int dayOfWeek) {
        LocalDate newDate = date.withDayOfWeek(dayOfWeek);
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
     * @return a new updated ZonedDateTime
     */
    public LocalDateTime withDate(int year, int monthOfYear, int dayOfMonth) {
        if (year == getYear().getValue() &&
                monthOfYear == getMonthOfYear().getValue() &&
                dayOfMonth == getDayOfMonth().getValue()) {
            return this;
        }
        LocalDate newDate = LocalDate.date(year, monthOfYear, dayOfMonth);
        return withDateTime(newDate, time);
    }

    /**
     * Returns a copy of this LocalDateTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated LocalDateTime, never null
     * @throws IllegalCalendarFieldValueException if the value if invalid
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
     * @throws IllegalCalendarFieldValueException if the value if invalid
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
     * @throws IllegalCalendarFieldValueException if the value if invalid
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
     * @throws IllegalCalendarFieldValueException if the value if invalid
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
     */
    public LocalDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        if (hourOfDay == getHourOfDay().getValue() && minuteOfHour == getMinuteOfHour().getValue() &&
                secondOfMinute == getSecondOfMinute().getValue() && nanoOfSecond == getNanoOfSecond().getValue()) {
            return this;
        }
        LocalTime newTime = LocalTime.time(hourOfDay, minuteOfHour, secondOfMinute);
        return withDateTime(date, newTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDateTime with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this LocalDateTime with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated LocalDateTime, never null
     */
    public LocalDateTime plus(PeriodView... periods) {
        // TODO
        return null;
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
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public LocalDateTime plusYears(int years) {
        LocalDate newDate = date.plusYears(years);
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
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a new updated LocalDateTime, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public LocalDateTime plusMonths(int months) {
        LocalDate newDate = date.plusMonths(months);
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
     * @throws ArithmeticException if the calculation overflows
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
     */
    public LocalDateTime plusDays(int days) {
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
     */
    public LocalDateTime plusHours(int hours) {
        LocalTime.Overflow overflow = time.plusWithOverflow(Periods.hours(hours));
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
     */
    public LocalDateTime plusMinutes(int minutes) {
        LocalTime.Overflow overflow = time.plusWithOverflow(Periods.minutes(minutes));
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
     */
    public LocalDateTime plusSeconds(int seconds) {
        LocalTime.Overflow overflow = time.plusWithOverflow(Periods.seconds(seconds));
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
     */
    public LocalDateTime plusNanos(int nanos) {
        LocalTime.Overflow overflow = time.plusWithOverflow(Periods.nanos(nanos));
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return withDateTime(newDate, overflow.getResultTime());
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
     * @return the comparator value, negative if less, postive if greater
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
     * A hashcode for this date-time.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return date.hashCode() ^ time.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the string form of the date-time.
     *
     * @return the string form of the date-time
     */
    @Override
    public String toString() {
        return date + "T" + time;
    }

}
