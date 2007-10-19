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
package javax.time.calendar.view;

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.period.PeriodView;

/**
 * An immutable time point, with nanosecond precision, operating without a time zone.
 * <p>
 * CalendarPoint is an immutable time point that records time information to the
 * precision of a nanosecond. Although nanoseconds are supported, most uses of this
 * class will only involve second precision.
 * <p>
 * As an example, the value "21st September 2007 at 14:20:32.012343210" can be stored
 * in a CalendarPoint.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarPoint is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarPoint implements Calendrical, Comparable<CalendarPoint>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -1054345985648291006L;

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

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarPoint</code>.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @param secondOfDay  the second of day to represent
     * @param nanoOfSecond  the nano of second to represent
     * @return a CalendarPoint object
     */
    public static CalendarPoint calendarPoint(int year, int monthOfYear, int dayOfMonth, int secondOfDay, int nanoOfSecond) {
        return null;
    }

    /**
     * Obtains an instance of <code>CalendarPoint</code> from a set of moments.
     * <p>
     * This can be used to pass in any combination of moments that fully specify
     * a calendar day. For example, Year + MonthOfYear + DayOfMonth + TimeOfDay.
     *
     * @param moments  a set of moments that fully represent a calendar day
     * @return a CalendarPoint object
     */
    public static CalendarPoint calendarPoint(Calendrical... moments) {
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
    private CalendarPoint(int year, int monthOfYear, int dayOfMonth, int secondOfDay, int nanoOfSecond) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.secondOfDay = secondOfDay;
        this.nanoOfSecond = nanoOfSecond;
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
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getDayOfYear() {
        return dayOfMonth;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getDayOfWeek() {
        return dayOfMonth;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getHourOfDay() {
        return secondOfDay / 3600;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getMinuteOfHour() {
        return (secondOfDay / 60) % 60;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getSecondOfMinute() {
        return secondOfDay % 60;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getNanoOfSecond() {
        return nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarPoint with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated CalendarPoint
     */
    public CalendarPoint with(Calendrical moment) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moments  the moments to update to, not null
     * @return a new updated CalendarPoint
     */
    public CalendarPoint with(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarPoint with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withYear(int year) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withMonthOfYear(int monthOfYear) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withDayOfMonth(int dayOfMonth) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withLastDayOfMonth() {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withDayOfYear(int dayOfYear) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withLastDayOfYear() {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withDayOfWeek(int dayOfWeek) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withHourOfDay(int hourOfDay) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withMinuteOfHour(int minuteOfHour) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withSecondOfMinute(int secondOfMinute) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent
     * @return a new updated CalendarPoint
     */
    public CalendarPoint withNanoOfSecond(int nanoOfSecond) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarPoint with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarPoint with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plusMonths(int months) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plusDays(int days) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plusHours(int hours) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plusMinutes(int minutes) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plusSeconds(int seconds) {
        return null;
    }

    /**
     * Returns a copy of this CalendarPoint with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add
     * @return a new updated CalendarPoint
     */
    public CalendarPoint plusNanos(int nanos) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherPoint  the other point instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(CalendarPoint otherPoint) {
        int cmp = MathUtils.safeCompare(year, otherPoint.year);
        if (cmp != 0) {
            return cmp;
        }
        cmp = MathUtils.safeCompare(monthOfYear, otherPoint.monthOfYear);
        if (cmp != 0) {
            return cmp;
        }
        cmp = MathUtils.safeCompare(dayOfMonth, otherPoint.dayOfMonth);
        if (cmp != 0) {
            return cmp;
        }
        cmp = MathUtils.safeCompare(secondOfDay, otherPoint.secondOfDay);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(nanoOfSecond, otherPoint.nanoOfSecond);
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherPoint  the other day instance to compare to, not null
     * @return true if this point is after the specified point
     * @throws NullPointerException if otherPoint is null
     */
    public boolean isAfter(CalendarPoint otherPoint) {
        return compareTo(otherPoint) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherPoint  the other day instance to compare to, not null
     * @return true if this point is before the specified point
     * @throws NullPointerException if otherPoint is null
     */
    public boolean isBefore(CalendarPoint otherPoint) {
        return compareTo(otherPoint) < 0;
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
        if (otherPoint instanceof CalendarPoint) {
            CalendarPoint other = (CalendarPoint) otherPoint;
            return  year == other.year &&
                    monthOfYear == other.monthOfYear &&
                    dayOfMonth == other.dayOfMonth &&
                    secondOfDay == other.secondOfDay &&
                    nanoOfSecond == other.nanoOfSecond;
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
            + 37 * nanoOfSecond;
    }

}
