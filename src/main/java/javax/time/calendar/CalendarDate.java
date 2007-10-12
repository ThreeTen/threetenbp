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
 * An immutable time point, with day precision, operating without a time zone.
 * <p>
 * CalendarDate is an immutable time point that records time information to the
 * precision of a day.
 * <p>
 * As an example, the value "21st September 2007" can be stored in a CalendarDate.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarDate is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarDate implements Calendrical, Comparable<CalendarDate>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -3005141082690272399L;

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

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarDate</code>.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @return a CalendarDate object
     */
    public static CalendarDate yearMonthDay(int year, int monthOfYear, int dayOfMonth) {
        return new CalendarDate(year, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>CalendarDate</code> from a set of moments.
     * <p>
     * This can be used to pass in any combination of moments that fully specify
     * a calendar day. For example, Year + MonthOfYear + DayOfMonth, or
     * Year + DayOfYear.
     *
     * @param moments  a set of moments that fully represent a calendar day
     * @return a CalendarDate object
     */
    public static CalendarDate calendarDate(Calendrical... moments) {
        return new CalendarDate(0, 0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     */
    private CalendarDate(int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
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

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDate with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated CalendarDate
     */
    public CalendarDate with(Calendrical moment) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDate with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moments  the moments to update to, not null
     * @return a new updated CalendarDate
     */
    public CalendarDate with(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDate with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CalendarDate
     */
    public CalendarDate withYear(int year) {
        return new CalendarDate(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDate with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent
     * @return a new updated CalendarDate
     */
    public CalendarDate withMonthOfYear(int monthOfYear) {
        return new CalendarDate(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDate with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent
     * @return a new updated CalendarDate
     */
    public CalendarDate withDayOfMonth(int dayOfMonth) {
        return new CalendarDate(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDate with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDate
     */
    public CalendarDate withLastDayOfMonth() {
        return new CalendarDate(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDate with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent
     * @return a new updated CalendarDate
     */
    public CalendarDate withDayOfYear(int dayOfYear) {
        return new CalendarDate(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDate with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDate
     */
    public CalendarDate withLastDayOfYear() {
        return new CalendarDate(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDate with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent
     * @return a new updated CalendarDate
     */
    public CalendarDate withDayOfWeek(int dayOfWeek) {
        return new CalendarDate(0, 0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDate with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CalendarDate
     */
    public CalendarDate plus(Durational period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarDate with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CalendarDate
     */
    public CalendarDate plus(Durational... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDate with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated CalendarDate
     */
    public CalendarDate plusYears(int years) {
        return new CalendarDate(year + years, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this CalendarDate with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated CalendarDate
     */
    public CalendarDate plusMonths(int months) {
        return new CalendarDate(year, monthOfYear + months, dayOfMonth);
    }

    /**
     * Returns a copy of this CalendarDate with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated CalendarDate
     */
    public CalendarDate plusWeeks(int weeks) {
        return new CalendarDate(year, monthOfYear, dayOfMonth + weeks * 7);
    }

    /**
     * Returns a copy of this CalendarDate with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated CalendarDate
     */
    public CalendarDate plusDays(int days) {
        return new CalendarDate(year, monthOfYear, dayOfMonth + days);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherDay  the other day instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(CalendarDate otherDay) {
        int cmp = MathUtils.safeCompare(year, otherDay.year);
        if (cmp != 0) {
            return cmp;
        }
        cmp = MathUtils.safeCompare(monthOfYear, otherDay.monthOfYear);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(dayOfMonth, otherDay.dayOfMonth);
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherDay  the other day instance to compare to, not null
     * @return true if this day is after the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isAfter(CalendarDate otherDay) {
        return compareTo(otherDay) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherDay  the other day instance to compare to, not null
     * @return true if this day is before the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isBefore(CalendarDate otherDay) {
        return compareTo(otherDay) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherDay  the other day instance to compare to, null returns false
     * @return true if this day is equal to the specified day
     */
    @Override
    public boolean equals(Object otherDay) {
        if (this == otherDay) {
            return true;
        }
        if (otherDay instanceof CalendarDate) {
            CalendarDate other = (CalendarDate) otherDay;
            return  dayOfMonth == other.dayOfMonth &&
                    monthOfYear == other.monthOfYear &&
                    year == other.year;
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
            + 37 * dayOfMonth;
    }

}
