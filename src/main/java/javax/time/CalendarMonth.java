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
 * A moment of a month.
 * <p>
 * Month is an immutable moment that records time information to the precision
 * of a month. For example, the value "September 2007" can be stored in a Month.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarMonth is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarMonth implements SingleMoment, Comparable<CalendarMonth> {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The year being represented.
     */
    private final Year year;
    /**
     * The month of year being represented.
     */
    private final MonthOfYear monthOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Month</code>.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @return the created CalendarMonth
     */
    public static CalendarMonth month(Year year, MonthOfYear monthOfYear) {
        return new CalendarMonth(year, monthOfYear);
    }

    /**
     * Obtains an instance of <code>Month</code> representing this month.
     *
     * @return a Month object representing this month
     */
    public static CalendarMonth thisMonth() {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     */
    private CalendarMonth(Year year, MonthOfYear monthOfYear) {
        this.year = year;
        this.monthOfYear = monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of year value.
     *
     * @return the month of year
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

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherMonth  the other month instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMonth is null
     */
    public int compareTo(CalendarMonth otherMonth) {
        int cmp = year.compareTo(otherMonth.year);
        if (cmp != 0) {
            return cmp;
        }
        return monthOfYear.compareTo(otherMonth.monthOfYear);
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherMonth  the other month instance, not null
     * @return true if this month of year is greater
     * @throws NullPointerException if otherMonth is null
     */
    public boolean isAfter(CalendarMonth otherMonth) {
        return compareTo(otherMonth) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherMonth  the other month instance, not null
     * @return true if this month of year is less
     * @throws NullPointerException if otherMonth is null
     */
    public boolean isBefore(CalendarMonth otherMonth) {
        return compareTo(otherMonth) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherMonth  the other month instance, null returns false
     * @return true if the month of year is the same
     */
    public boolean equals(Object otherMonth) {
        if (this == otherMonth) {
            return true;
        }
        if (otherMonth instanceof CalendarMonth) {
            return monthOfYear == ((CalendarMonth) otherMonth).monthOfYear;
        }
        return false;
    }

    /**
     * A hashcode for the month object.
     *
     * @return a suitable hashcode
     */
    public int hashCode() {
        return year.hashCode() + 37 * monthOfYear.hashCode();
    }

}
