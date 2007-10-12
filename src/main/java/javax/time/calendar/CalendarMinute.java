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
import javax.time.duration.Durational;

/**
 * An immutable time point, with minute precision, operating without a time zone.
 * <p>
 * CalendarMinute is an immutable time point that records time information to the
 * precision of a minute.
 * <p>
 * As an example, the value "21st September 2007 at 14:20:32" can be stored
 * in a CalendarMinute.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarMinute is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarMinute implements Calendrical, Comparable<CalendarMinute>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -201274823589416112L;

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
     * The minute of day being represented.
     */
    private final int minuteOfDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarMinute</code>.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @param minuteOfDay  the minute of day to represent
     * @return a CalendarMinute object
     */
    public static CalendarMinute calendarMinute(int year, int monthOfYear, int dayOfMonth, int minuteOfDay) {
        return null;
    }

    /**
     * Obtains an instance of <code>CalendarMinute</code> from a set of moments.
     * <p>
     * This can be used to pass in any combination of moments that fully specify
     * a calendar day. For example, CalendarDate + HourOfDay + MinuteOfHour.
     *
     * @param moments  a set of moments that fully represent a calendar minute
     * @return a CalendarMinute object
     */
    public static CalendarMinute calendarMinute(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified date and time.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @param minuteOfDay  the minute of day to represent
     */
    private CalendarMinute(int year, int monthOfYear, int dayOfMonth, int minuteOfDay) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.minuteOfDay = minuteOfDay;
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
     * Gets the hour of day value.
     *
     * @return the hour of day
     */
    public int getHourOfDay() {
        return minuteOfDay / 60;
    }

    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour
     */
    public int getMinuteOfHour() {
        return minuteOfDay % 60;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarMinute with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated CalendarMinute
     */
    public CalendarMinute with(Calendrical moment) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moments  the moments to update to, not null
     * @return a new updated CalendarMinute
     */
    public CalendarMinute with(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarMinute with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withYear(int year) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withMonthOfYear(int monthOfYear) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withDayOfMonth(int dayOfMonth) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withLastDayOfMonth() {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withDayOfYear(int dayOfYear) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withLastDayOfYear() {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withDayOfWeek(int dayOfWeek) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withHourOfDay(int hourOfDay) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withMinuteOfHour(int minuteOfHour) {
        return null;
    }

    /**
     * Returns a copy of this CalendarSecond with the date values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @return a new updated CalendarSecond
     */
    public CalendarSecond withDate(int year, int monthOfYear, int dayOfMonth) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the time values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent
     * @param minuteOfHour  the minute of hour to represent
     * @return a new updated CalendarMinute
     */
    public CalendarMinute withTime(int hourOfDay, int minuteOfHour) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarMinute with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CalendarMinute
     */
    public CalendarMinute plus(Durational period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CalendarMinute
     */
    public CalendarMinute plus(Durational... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarMinute with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated CalendarMinute
     */
    public CalendarMinute plusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated CalendarMinute
     */
    public CalendarMinute plusMonths(int months) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated CalendarMinute
     */
    public CalendarMinute plusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated CalendarMinute
     */
    public CalendarMinute plusDays(int days) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated CalendarMinute
     */
    public CalendarMinute plusHours(int hours) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMinute with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated CalendarMinute
     */
    public CalendarMinute plusMinutes(int minutes) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param other  the other minute instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if other is null
     */
    public int compareTo(CalendarMinute other) {
        int cmp = MathUtils.safeCompare(year, other.year);
        if (cmp != 0) {
            return cmp;
        }
        cmp = MathUtils.safeCompare(monthOfYear, other.monthOfYear);
        if (cmp != 0) {
            return cmp;
        }
        cmp = MathUtils.safeCompare(dayOfMonth, other.dayOfMonth);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(minuteOfDay, other.minuteOfDay);
    }

    /**
     * Is this instance after the specified one.
     *
     * @param other  the other minute instance to compare to, not null
     * @return true if this point is after the specified point
     * @throws NullPointerException if other is null
     */
    public boolean isAfter(CalendarMinute other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param other  the other minute instance to compare to, not null
     * @return true if this point is before the specified point
     * @throws NullPointerException if other is null
     */
    public boolean isBefore(CalendarMinute other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param other  the other point instance to compare to, null returns false
     * @return true if this point is equal to the specified minute
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof CalendarMinute) {
            CalendarMinute otherMinute = (CalendarMinute) other;
            return  year == otherMinute.year &&
                    monthOfYear == otherMinute.monthOfYear &&
                    dayOfMonth == otherMinute.dayOfMonth &&
                    minuteOfDay == otherMinute.minuteOfDay;
        }
        return false;
    }

    /**
     * A suitable hashcode for this object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return year
            + 37 * monthOfYear
            + 37 * dayOfMonth
            + 37 * minuteOfDay;
    }

}
