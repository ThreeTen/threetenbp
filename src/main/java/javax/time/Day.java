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
 * A moment of a day.
 * <p>
 * Day is an immutable moment that records time information to the precision
 * of a day. For example, the value "21st September 2007" can be stored in a Day.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * Day is thread-safe and immutable. 
 * 
 * @author Stephen Colebourne
 */
public final class Day implements Comparable<Day> {

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
     * Obtains an instance of <code>Day</code>.
     * 
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     */
    public static Day day(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
        return new Day(year, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>Day</code> representing today.
     * 
     * @return a Day object representing today
     */
    public static Day today() {
        return null;  // TODO
    }

    /**
     * Obtains an instance of <code>Day</code> representing yesterday.
     * 
     * @return a Day object representing yesterday
     */
    public static Day yesterday() {
        return null;  // TODO
    }

    /**
     * Obtains an instance of <code>Day</code> representing tomorrow.
     * 
     * @return a Day object representing tomorrow
     */
    public static Day tomorrow() {
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
    private Day(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
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
     * Compares this instance to another.
     * 
     * @param otherDay  the other day instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(Day otherDay) {
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
    public boolean isAfter(Day otherDay) {
        return compareTo(otherDay) > 0;
    }

    /**
     * Is this instance before the specified one.
     * 
     * @param otherDay  the other day instance, not null
     * @return true if this month of year is less
     * @throws NullPointerException if otherDay is null
     */
    public boolean isBefore(Day otherDay) {
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
        if (otherDay instanceof Day) {
            return monthOfYear == ((Day) otherDay).monthOfYear;
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
