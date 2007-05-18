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
 * Day is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class CalendarDay implements SingleMoment, Comparable<CalendarDay> {

    /**
     * The year being represented.
     */
    private final Year year;
    /**
     * The month of year being represented.
     */
    private final MonthOfYear monthOfYear;
    /**
     * The day of month being represented.
     */
    private final DayOfMonth dayOfMonth;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarDay</code>.
     * 
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @return a CalendarDay object representing yesterday
     */
    public static CalendarDay day(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
        return new CalendarDay(year, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>CalendarDay</code> representing today.
     * 
     * @return a CalendarDay object representing today
     */
    public static CalendarDay today() {
        return null;  // TODO
    }

    /**
     * Obtains an instance of <code>CalendarDay</code> representing yesterday.
     * 
     * @return a CalendarDay object representing yesterday
     */
    public static CalendarDay yesterday() {
        return null;  // TODO
    }

    /**
     * Obtains an instance of <code>CalendarDay</code> representing tomorrow.
     * 
     * @return a CalendarDay object representing tomorrow
     */
    public static CalendarDay tomorrow() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     * 
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     */
    private CalendarDay(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
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
    public Year getYear() {
        return year;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public MonthOfYear getMonthOfYear() {
        return monthOfYear;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public DayOfMonth getDayOfMonth() {
        return dayOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated TimeOfDay
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
     * @return a new updated TimeOfDay
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
        return new CalendarDay(null, null, null);
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
        return new CalendarDay(null, null, null);
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
        return new CalendarDay(null, null, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     * 
     * @param otherDay  the other day instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(CalendarDay otherDay) {
        int cmp = year.compareTo(otherDay.year);
        if (cmp != 0) {
            return cmp;
        }
        return monthOfYear.compareTo(otherDay.monthOfYear);
    }

    /**
     * Is this instance after the specified one.
     * 
     * @param otherDay  the other day instance, not null
     * @return true if this month of year is greater
     * @throws NullPointerException if otherDay is null
     */
    public boolean isAfter(CalendarDay otherDay) {
        return compareTo(otherDay) > 0;
    }

    /**
     * Is this instance before the specified one.
     * 
     * @param otherDay  the other day instance, not null
     * @return true if this month of year is less
     * @throws NullPointerException if otherDay is null
     */
    public boolean isBefore(CalendarDay otherDay) {
        return compareTo(otherDay) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     * 
     * @param otherDay  the other month instance, null returns false
     * @return true if the month of year is the same
     */
    public boolean equals(Object otherDay) {
        if (this == otherDay) {
            return true;
        }
        if (otherDay instanceof CalendarDay) {
            return monthOfYear == ((CalendarDay) otherDay).monthOfYear;
        }
        return false;
    }

    /**
     * A hashcode for the day object.
     * 
     * @return a suitable hashcode
     */
    public int hashCode() {
        return year.hashCode()
            + 37 * monthOfYear.hashCode()
            + 37 * dayOfMonth.hashCode();
    }

}
