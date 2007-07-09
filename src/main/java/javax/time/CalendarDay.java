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
package javax.time;

/**
 * A calendar day which represents a moment with a precision of a day.
 * <p>
 * CalendarDay is an immutable moment that records time information to the precision
 * of a day. For example, the value "21st September 2007" can be stored in a CalendarDay.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarDay is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarDay implements SingleMoment, Comparable<CalendarDay> {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

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
     * Obtains an instance of <code>CalendarDay</code>.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @return a CalendarDay object
     */
    public static CalendarDay yearMonthDay(int year, int monthOfYear, int dayOfMonth) {
        return new CalendarDay(year, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>CalendarDay</code> from a set of moments.
     * <p>
     * This can be used to pass in any combination of moments that fully specify
     * a calendar day. For example, Year + MonthOfYear + DayOfMonth, or
     * CalendarMonth + DayOfMonth.
     *
     * @param moments  a set of moments that fully represent a calendar day
     * @return a CalendarDay object
     */
    public static CalendarDay calendarDay(Moment... moments) {
        return new CalendarDay(0, 0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     */
    private CalendarDay(int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
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

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated CalendarDay
     */
    public CalendarDay with(Moment moment) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDay with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moments  the moments to update to, not null
     * @return a new updated CalendarDay
     */
    public CalendarDay with(Moment... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withYear(int year) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withMonthOfYear(int monthOfYear) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withDayOfMonth(int dayOfMonth) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDay
     */
    public CalendarDay withLastDayOfMonth() {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withDayOfYear(int dayOfYear) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDay
     */
    public CalendarDay withLastDayOfYear() {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withDayOfWeek(int dayOfWeek) {
        return new CalendarDay(0, 0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CalendarDay
     */
    public CalendarDay plus(Period period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarDay with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CalendarDay
     */
    public CalendarDay plus(Period... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated CalendarDay
     */
    public CalendarDay plusYears(int years) {
        return new CalendarDay(year + years, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this CalendarDay with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated CalendarDay
     */
    public CalendarDay plusMonths(int months) {
        return new CalendarDay(year, monthOfYear + months, dayOfMonth);
    }

    /**
     * Returns a copy of this CalendarDay with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated CalendarDay
     */
    public CalendarDay plusWeeks(int weeks) {
        return new CalendarDay(year, monthOfYear, dayOfMonth + weeks * 7);
    }

    /**
     * Returns a copy of this CalendarDay with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated CalendarDay
     */
    public CalendarDay plusDays(int days) {
        return new CalendarDay(year, monthOfYear, dayOfMonth + days);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherDay  the other day instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(CalendarDay otherDay) {
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
    public boolean isAfter(CalendarDay otherDay) {
        return compareTo(otherDay) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherDay  the other day instance to compare to, not null
     * @return true if this day is before the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isBefore(CalendarDay otherDay) {
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
        if (otherDay instanceof CalendarDay) {
            CalendarDay other = (CalendarDay) otherDay;
            return  dayOfMonth == other.dayOfMonth &&
                    monthOfYear == other.monthOfYear &&
                    year == other.year;
        }
        return false;
    }

    /**
     * A hashcode for the day object.
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
