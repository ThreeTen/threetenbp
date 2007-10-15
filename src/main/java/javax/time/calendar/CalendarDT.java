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

import javax.time.MathUtils;
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;

/**
 * A date and time without a time zone.
 * <p>
 * CalendarPoint is an immutable calendrical object that records a date and time
 * information to various possible precisions. As a user of the class, you can
 * choose to limit the precision to just hours, down to minutes, down to seconds or
 * even down to nanoseconds.
 * <p>
 * As an example, the value "21st September 2007 at 14:20" could be stored using
 * minute precision, or "21st September 2007 at 14:20:32.012343210" could be stored
 * using nanosecond precision.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarPoint is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarDT<T extends TimeCalendrical>
        implements Calendrical, Comparable<CalendarDT<T>>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -3878273871687168L;

    /**
     * The year being represented.
     */
    private final int year;
    /**
     * The month of year being represented.
     */
    private final int monthOfYear;
    /**
     * The day of month being represented.
     */
    private final int dayOfMonth;
    /**
     * The time being represented.
     */
    private final T time;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarDateTime</code>.
     * <p>
     * This factory will create an object with full precision.
     * All fields on the created instance can be queried.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @param secondOfMinute  the second of minute to represent
     * @param nanoOfSecond  the nano of second to represent
     * @return a CalendarDateTime object
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public static CalendarDT<TimeHMSN> calendarDateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        return null;
    }

    /**
     * Obtains an instance of <code>CalendarDateTime</code>.
     * <p>
     * This factory will create an object with full precision.
     * The nanosecond field on the created instance cannot be queried.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @param secondOfMinute  the second of minute to represent
     * @return a CalendarDateTime object
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public static CalendarDT<TimeHMS> calendarDateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return null;
    }

    /**
     * Obtains an instance of <code>CalendarDateTime</code>.
     * <p>
     * This factory will create an object with full precision.
     * The second and nanosecond fields on the created instance cannot be queried.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @return a CalendarDateTime object
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public static CalendarDT<TimeHM> calendarDateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour) {
        return null;
    }

    /**
     * Obtains an instance of <code>CalendarDateTime</code>.
     * <p>
     * This factory will create an object with full precision.
     * The minute, second and nanosecond fields on the created instance cannot be queried.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @param hourOfDay  the hour of day to represent
     * @return a CalendarDateTime object
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public static CalendarDT calendarDateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth, int hourOfDay) {
        return null;
    }

    /**
     * Obtains an instance of <code>CalendarDateTime</code> from a set of moments.
     * <p>
     * This can be used to pass in any combination of moments that fully specify
     * a calendar day. For example, Year + MonthOfYear + DayOfMonth + TimeOfDay.
     *
     * @param moments  a set of moments that fully represent a calendar day
     * @return a CalendarDateTime object
     */
    public static CalendarDT calendarDateTime(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @param secondOfDay  the second of day to represent
     * @param nanoOfSecond  the nano of second to represent
     */
    private CalendarDT(int year, int monthOfYear, int dayOfMonth, int secondOfDay, int nanoOfSecond) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.time = null;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year value.
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getMonthOfYear() {
        return monthOfYear;
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year
     */
    public int getDayOfYear() {
        return dayOfMonth;
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week
     */
    public int getDayOfWeek() {
        return dayOfMonth;
    }

    /**
     * Gets the time.
     *
     * @return the time, never null
     */
    public T time() {
        return time;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDateTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> with(Calendrical moment) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moments  the moments to update to, not null
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> with(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDateTime with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> withYear(int year) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> withMonthOfYear(int monthOfYear) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> withDayOfMonth(int dayOfMonth) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> withLastDayOfMonth() {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> withDayOfYear(int dayOfYear) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> withLastDayOfYear() {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> withDayOfWeek(int dayOfWeek) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the new time instance, not null
     * @return a new updated CalendarDateTime
     * @throws IllegalStateException if this instance has insufficient precision to update hours
     */
    public <R extends TimeCalendrical> CalendarDT<R> withTime(R time) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDateTime with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDateTime with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> plusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> plusMonths(int months) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> plusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated CalendarDateTime
     */
    public CalendarDT<T> plusDays(int days) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherDateTime  the other instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDateTime is null
     */
    public int compareTo(CalendarDT<T> otherDateTime) {
        int cmp = MathUtils.safeCompare(year, otherDateTime.year);
        if (cmp != 0) {
            return cmp;
        }
        cmp = MathUtils.safeCompare(monthOfYear, otherDateTime.monthOfYear);
        if (cmp != 0) {
            return cmp;
        }
        cmp = MathUtils.safeCompare(dayOfMonth, otherDateTime.dayOfMonth);
        if (cmp != 0) {
            return cmp;
        }
        return 0;
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherDateTime  the other instance to compare to, not null
     * @return true if this point is after the specified point
     * @throws NullPointerException if otherDateTime is null
     */
    public boolean isAfter(CalendarDT<T> otherDateTime) {
        return compareTo(otherDateTime) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherDateTime  the other instance to compare to, not null
     * @return true if this point is before the specified point
     * @throws NullPointerException if otherDateTime is null
     */
    public boolean isBefore(CalendarDT<T> otherDateTime) {
        return compareTo(otherDateTime) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherPoint  the other point instance to compare to, null returns false
     * @return true if this point is equal to the specified point
     */
    @Override
    public boolean equals(Object otherPoint) {
        if (this == otherPoint) {
            return true;
        }
        if (otherPoint instanceof CalendarDT) {
            CalendarDT other = (CalendarDT) otherPoint;
            return  year == other.year &&
                    monthOfYear == other.monthOfYear &&
                    dayOfMonth == other.dayOfMonth &&
                    time == other.time;
        }
        return false;
    }

    /**
     * A hashcode for the point object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return year
            + 37 * monthOfYear
            + 37 * dayOfMonth
            + 37 * time.hashCode();
    }

}
