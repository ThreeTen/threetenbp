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

import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;
import javax.time.period.Periods;

/**
 * A calendrical representation of a year and month without a time zone.
 * <p>
 * YearMonth is an immutable calendrical that represents a year-month combination.
 * This class does not store or represent a day, time or time zone.
 * Thus, for example, the value "October 2007" can be stored in a YearMonth.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * YearMonth is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class YearMonth
        implements Calendrical, Comparable<YearMonth>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1507289123L;

    /**
     * The year being represented.
     */
    private final int year;
    /**
     * The month of year being represented.
     */
    private final int monthOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarMonth</code>.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a YearMonth object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static YearMonth yearMonth(int year, int monthOfYear) {
        return new YearMonth(year, monthOfYear);
    }

    /**
     * Obtains an instance of <code>CalendarMonth</code> from a set of calendricals.
     * <p>
     * This can be used to pass in any combination of calendricals that fully specify
     * a calendar month. For example, Year + MonthOfYear.
     *
     * @param calendricals  a set of calendricals that fully represent a calendar month
     * @return a YearMonth object, never null
     */
    public static YearMonth yearMonth(Calendrical... calendricals) {
        return new YearMonth(0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     */
    private YearMonth(int year, int monthOfYear) {
        this.year = year;
        this.monthOfYear = monthOfYear;
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

    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>YearMonth</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return field.isSupported(Periods.MONTHS, Periods.FOREVER);
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
            throw new UnsupportedCalendarFieldException("YearMonth does not support field " + field.getName());
        }
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ISO proleptic year value.
     * <p>
     * The year 1AD is represented by 1.<br />
     * The year 1BC is represented by 0.<br />
     * The year 2BC is represented by -1.<br />
     *
     * @return the year, from MIN_VALUE + 1 to MAX_VALUE
     */
    public int getYear() {
        return 0;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated YearMonth, never null
     */
    public YearMonth with(Calendrical calendrical) {
        return null;
    }

    /**
     * Returns a copy of this YearMonth with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated YearMonth, never null
     */
    public YearMonth with(Calendrical... calendricals) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @return a new updated YearMonth, never null
     */
    public YearMonth withYear(int year) {
        return null;
    }

    /**
     * Returns a copy of this YearMonth with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated YearMonth, never null
     */
    public YearMonth withMonthOfYear(int monthOfYear) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated YearMonth, never null
     */
    public YearMonth plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this YearMonth with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated YearMonth, never null
     */
    public YearMonth plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated YearMonth, never null
     */
    public YearMonth plusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this YearMonth with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated YearMonth, never null
     */
    public YearMonth plusMonths(int months) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year-month to another year-month.
     *
     * @param other  the other year-month to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(YearMonth other) {
        return 0;
    }

    /**
     * Is this year-month after the specified year-month.
     *
     * @param other  the other year-month to compare to, not null
     * @return true if this is after the specified year-month
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(YearMonth other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this year-month before the specified year-month.
     *
     * @param other  the other year-month to compare to, not null
     * @return true if this point is before the specified year-month
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(YearMonth other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this year-month equal to the specified year-month.
     *
     * @param other  the other year-month to compare to, null returns false
     * @return true if this point is equal to the specified year-month
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof YearMonth) {
            YearMonth yearMonth = (YearMonth) other;
            return  true;
        }
        return false;
    }

    /**
     * A hashcode for this year-month.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Outputs the string form of the year-month.
     *
     * @return the string form of the year-month
     */
    @Override
    public String toString() {
        return super.toString();
    }

}
