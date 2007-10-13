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
import javax.time.period.PeriodUnit;
import javax.time.period.PeriodView;
import javax.time.period.Periods;

/**
 * Represnts a date and time without a time zone.
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
public final class CalendarDateTime
        implements Calendrical, Comparable<CalendarDateTime>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -137361873671176867L;

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
     * The second of days being represented.
     */
    private final int secondOfDay;
    /**
     * The nanos of second being represented.
     */
    private final int nanoOfSecond;
    /**
     * The precision being represented.
     */
    private final PeriodUnit precision;

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
    public static CalendarDateTime calendarDateTime(
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
    public static CalendarDateTime calendarDateTime(
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
    public static CalendarDateTime calendarDateTime(
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
    public static CalendarDateTime calendarDateTime(
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
    public static CalendarDateTime calendarDateTime(Calendrical... moments) {
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
    private CalendarDateTime(int year, int monthOfYear, int dayOfMonth, int secondOfDay, int nanoOfSecond) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.secondOfDay = secondOfDay;
        this.nanoOfSecond = nanoOfSecond;
        this.precision = null;
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
     * Gets the precision of this datetime instance.
     *
     * @return the precision of this instance
     */
    public PeriodUnit getPrecision() {
        return precision;
    }

    /**
     * Is the precision of this CalendarDateTime at least hours.
     *
     * @return true if the precision is at least hours
     */
    public boolean isPrecisionAtLeastHours() {
        return precision.compareTo(Periods.HOURS) <= 0;
    }

    /**
     * Is the precision of this CalendarDateTime at least minutes.
     *
     * @return true if the precision is at least minutes
     */
    public boolean isPrecisionAtLeastMinutes() {
        return precision.compareTo(Periods.MINUTES) <= 0;
    }

    /**
     * Is the precision of this CalendarDateTime at least seconds.
     *
     * @return true if the precision is at least seconds
     */
    public boolean isPrecisionAtLeastSeconds() {
        return precision.compareTo(Periods.SECONDS) <= 0;
    }

    /**
     * Is the precision of this CalendarDateTime equals to nanos.
     *
     * @return true if the precision is nanos
     */
    public boolean isPrecisionNanos() {
        return precision.compareTo(Periods.NANOS) <= 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDateTime with the precision altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param newPrecision  the new precision to use, not null
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withPrecision(PeriodUnit newPrecision) {
        if (newPrecision == precision) {
            return this;
        }
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the precision altered to hours.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withPrecisionHours() {
        return withPrecision(Periods.HOURS);
    }

    /**
     * Checks the precision and returns either this instance or a copy with
     * increased precision ensuring that the result has a precision of at
     * least hours. If the precision is expanded, then the time is set at the
     * earliest point in the original precision.
     * <p>
     * For example, if this instance represents AM (morning) with a precision
     * of half-day, then the returned object will represent hour 00 at the
     * start of day with a precision of hours.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withPrecisionAtLeastHours() {
        if (isPrecisionAtLeastHours()) {
            return this;
        }
        return withPrecision(Periods.HOURS);
    }

    /**
     * Returns a copy of this CalendarDateTime with the precision altered to minutes.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withPrecisionMinutes() {
        return withPrecision(Periods.MINUTES);
    }

    /**
     * Checks the precision and returns either this instance or a copy with
     * increased precision ensuring that the result has a precision of at
     * least minutes. If the precision is expanded, then the time is set at the
     * earliest point in the original precision.
     * <p>
     * For example, if this instance represents 12 with a precision of hours,
     * then the returned object will represent 12:00 with a precision of minutes.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withPrecisionAtLeastMinutes() {
        if (isPrecisionAtLeastMinutes()) {
            return this;
        }
        return withPrecision(Periods.MINUTES);
    }

    /**
     * Returns a copy of this CalendarDateTime with the precision altered to seconds.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withPrecisionSeconds() {
        return withPrecision(Periods.SECONDS);
    }

    /**
     * Checks the precision and returns either this instance or a copy with
     * increased precision ensuring that the result has a precision of at
     * least seconds. If the precision is expanded, then the time is set at the
     * earliest point in the original precision.
     * <p>
     * For example, if this instance represents 12:30 with a precision of minutes,
     * then the returned object will represent 12:30:00.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withPrecisionAtLeastSeconds() {
        if (isPrecisionAtLeastSeconds()) {
            return this;
        }
        return withPrecision(Periods.SECONDS);
    }

    /**
     * Returns a copy of this CalendarDateTime with the precision altered to nanos.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withPrecisionNanos() {
        return withPrecision(Periods.NANOS);
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
     * Gets the hours of day value.
     *
     * @return the hours of day
     * @throws IllegalStateException if this instance has insufficient precision to access hours
     */
    public int getHourOfDay() {
        if (!isPrecisionAtLeastHours()) {
            throw new IllegalStateException("Field HourOfDay cannot be accessed as the precision is " + precision.getName());
        }
        return secondOfDay / 3600;
    }

    /**
     * Gets the minute of hours value.
     *
     * @return the minute of hours
     * @throws IllegalStateException if this instance has insufficient precision to access minutes
     */
    public int getMinuteOfHour() {
        if (!isPrecisionAtLeastHours()) {
            throw new IllegalStateException("Field MinuteOfHour cannot be accessed as the precision is " + precision.getName());
        }
        return (secondOfDay / 60) % 60;
    }

    /**
     * Gets the second of minute value.
     *
     * @return the second of minute
     * @throws IllegalStateException if this instance has insufficient precision to access seconds
     */
    public int getSecondOfMinute() {
        if (!isPrecisionAtLeastHours()) {
            throw new IllegalStateException("Field SecondOfMinute cannot be accessed as the precision is " + precision.getName());
        }
        return secondOfDay % 60;
    }

    /**
     * Gets the nano of second value.
     *
     * @return the nano of second
     * @throws IllegalStateException if this instance has insufficient precision to access nanos
     */
    public int getNanoOfSecond() {
        if (!isPrecisionAtLeastHours()) {
            throw new IllegalStateException("Field NanoOfSecond cannot be accessed as the precision is " + precision.getName());
        }
        return nanoOfSecond;
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
    public CalendarDateTime with(Calendrical moment) {
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
    public CalendarDateTime with(Calendrical... moments) {
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
    public CalendarDateTime withYear(int year) {
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
    public CalendarDateTime withMonthOfYear(int monthOfYear) {
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
    public CalendarDateTime withDayOfMonth(int dayOfMonth) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withLastDayOfMonth() {
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
    public CalendarDateTime withDayOfYear(int dayOfYear) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDateTime
     */
    public CalendarDateTime withLastDayOfYear() {
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
    public CalendarDateTime withDayOfWeek(int dayOfWeek) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent
     * @return a new updated CalendarDateTime
     * @throws IllegalStateException if this instance has insufficient precision to update hours
     */
    public CalendarDateTime withHourOfDay(int hourOfDay) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent
     * @return a new updated CalendarDateTime
     * @throws IllegalStateException if this instance has insufficient precision to update minutes
     */
    public CalendarDateTime withMinuteOfHour(int minuteOfHour) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent
     * @return a new updated CalendarDateTime
     * @throws IllegalStateException if this instance has insufficient precision to update seconds
     */
    public CalendarDateTime withSecondOfMinute(int secondOfMinute) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent
     * @return a new updated CalendarDateTime
     * @throws IllegalStateException if this instance has insufficient precision to update nanos
     */
    public CalendarDateTime withNanoOfSecond(int nanoOfSecond) {
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
    public CalendarDateTime plus(PeriodView period) {
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
    public CalendarDateTime plus(PeriodView... periods) {
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
    public CalendarDateTime plusYears(int years) {
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
    public CalendarDateTime plusMonths(int months) {
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
    public CalendarDateTime plusWeeks(int weeks) {
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
    public CalendarDateTime plusDays(int days) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated CalendarDateTime
     * @throws IllegalStateException if this instance has insufficient precision to update hours
     */
    public CalendarDateTime plusHours(int hours) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated CalendarDateTime
     * @throws IllegalStateException if this instance has insufficient precision to update minutes
     */
    public CalendarDateTime plusMinutes(int minutes) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add
     * @return a new updated CalendarDateTime
     * @throws IllegalStateException if this instance has insufficient precision to update seconds
     */
    public CalendarDateTime plusSeconds(int seconds) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDateTime with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add
     * @return a new updated CalendarDateTime
     * @throws IllegalStateException if this instance has insufficient precision to update nanos
     */
    public CalendarDateTime plusNanos(int nanos) {
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
    public int compareTo(CalendarDateTime otherDateTime) {
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
        cmp = MathUtils.safeCompare(secondOfDay, otherDateTime.secondOfDay);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(nanoOfSecond, otherDateTime.nanoOfSecond);
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherDateTime  the other instance to compare to, not null
     * @return true if this point is after the specified point
     * @throws NullPointerException if otherDateTime is null
     */
    public boolean isAfter(CalendarDateTime otherDateTime) {
        return compareTo(otherDateTime) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherDateTime  the other instance to compare to, not null
     * @return true if this point is before the specified point
     * @throws NullPointerException if otherDateTime is null
     */
    public boolean isBefore(CalendarDateTime otherDateTime) {
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
        if (otherPoint instanceof CalendarDateTime) {
            CalendarDateTime other = (CalendarDateTime) otherPoint;
            return  year == other.year &&
                    monthOfYear == other.monthOfYear &&
                    dayOfMonth == other.dayOfMonth &&
                    secondOfDay == other.secondOfDay &&
                    nanoOfSecond == other.nanoOfSecond &&
                    precision.equals(other.precision);
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
            + 37 * secondOfDay
            + 37 * nanoOfSecond
            + 37 * precision.hashCode();
    }

}
