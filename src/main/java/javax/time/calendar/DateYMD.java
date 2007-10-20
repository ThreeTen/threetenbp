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

import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;
import javax.time.period.Periods;

/**
 * A calendrical representation of a date without a time zone.
 * <p>
 * DateYMD is an immutable calendrical that represents a date, often viewed as year-month-day.
 * This class does not store or represent a time or time zone.
 * Thus, for example, the value "2nd October 2007" can be stored in a DateYMD.
 * <p>
 * DateYMD is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class DateYMD
        implements Calendrical, Comparable<DateYMD>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -1187006174L;

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
     * Obtains an instance of <code>DateYMD</code>.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a DateYMD object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static DateYMD date(int year, int monthOfYear, int dayOfMonth) {
        return new DateYMD(year, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>DateYMD</code> from a set of calendricals.
     * <p>
     * This can be used to pass in any combination of calendricals that fully specify
     * a calendar day. For example, Year + MonthOfYear + DayOfMonth, or
     * Year + DayOfYear.
     *
     * @param calendricals  a set of calendricals that fully represent a calendar day
     * @return a DateYMD object, never null
     */
    public static DateYMD date(Calendrical... calendricals) {
        return new DateYMD(0, 0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     */
    private DateYMD(int year, int monthOfYear, int dayOfMonth) {
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

    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>DateYMD</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return field.isSupported(Periods.DAYS, Periods.FOREVER);
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
            throw new UnsupportedCalendarFieldException("DateYMD does not support field " + field.getName());
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

    /**
     * Gets the day of year value.
     *
     * @return the day of year, from 1 to 366
     */
    public int getDayOfYear() {
        return 0;
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return 0;
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateYMD with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated DateYMD, never null
     */
    public DateYMD with(Calendrical calendrical) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated DateYMD, never null
     */
    public DateYMD with(Calendrical... calendricals) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateYMD with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @return a new updated DateYMD, never null
     */
    public DateYMD withYear(int year) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated DateYMD, never null
     */
    public DateYMD withMonthOfYear(int monthOfYear) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent, from 1 to 366
     * @return a new updated DateYMD, never null
     */
    public DateYMD withDayOfYear(int dayOfYear) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated DateYMD, never null
     */
    public DateYMD withLastDayOfYear() {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated DateYMD, never null
     */
    public DateYMD withDayOfMonth(int dayOfMonth) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated DateYMD, never null
     */
    public DateYMD withLastDayOfMonth() {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent, from 1 (Monday) to 7 (Sunday)
     * @return a new updated DateYMD, never null
     */
    public DateYMD withDayOfWeek(int dayOfWeek) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateYMD with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated DateYMD, never null
     */
    public DateYMD plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated DateYMD, never null
     */
    public DateYMD plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateYMD with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated DateYMD, never null
     */
    public DateYMD plusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated DateYMD, never null
     */
    public DateYMD plusMonths(int months) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated DateYMD, never null
     */
    public DateYMD plusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated DateYMD, never null
     */
    public DateYMD plusDays(int days) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(DateYMD other) {
        return 0;
    }

    /**
     * Is this date after the specified date.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the specified date
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(DateYMD other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date before the specified date.
     *
     * @param other  the other date to compare to, not null
     * @return true if this point is before the specified date
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(DateYMD other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this date equal to the specified date.
     *
     * @param other  the other date to compare to, null returns false
     * @return true if this point is equal to the specified date
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof DateYMD) {
            DateYMD dateYMD = (DateYMD) other;
            return  true;
        }
        return false;
    }

    /**
     * A hashcode for this date.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Outputs the string form of the date.
     *
     * @return the string form of the date
     */
    @Override
    public String toString() {
        return super.toString();
    }

}
