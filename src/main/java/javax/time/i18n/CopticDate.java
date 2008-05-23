/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.i18n;

import java.io.Serializable;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.LocalDate;
import javax.time.calendar.UnsupportedCalendarFieldException;
import javax.time.calendar.format.FlexiDateTime;
import javax.time.period.PeriodView;

/**
 * A date in the Coptic calendar system.
 * <p>
 * CopticDate is an immutable class that represents a date in the Coptic calendar
 * system to the precision of a day. Although this class can be created from,
 * and converted to, the standard ISO calendar system, it is not directly linked.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CopticDate is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class CopticDate
        implements DateProvider, Calendrical, Comparable<CopticDate>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The date that this Coptic date wraps.
     */
    private final LocalDate date;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CopticDate</code>.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @return a CopticDate object
     */
    public static CopticDate copticDate(int year, int monthOfYear, int dayOfMonth) {
        CopticChronology.INSTANCE.year().checkValue(year);
        CopticChronology.INSTANCE.monthOfYear().checkValue(monthOfYear);
        CopticChronology.INSTANCE.dayOfMonth().checkValue(dayOfMonth);
        long mjDays = year * 365 + (year / 4) + 30 * (monthOfYear - 1) + dayOfMonth;
        return new CopticDate(LocalDate.fromMJDays(mjDays));
    }

    /**
     * Obtains an instance of <code>CopticDate</code> from a date provider.
     *
     * @param dateProvider  the date provider to use, not null
     * @return a CopticDate object, never null
     */
    public static CopticDate copticDate(DateProvider dateProvider) {
        if (dateProvider == null) {
            throw new NullPointerException("dateProvider must not be null");
        }
        LocalDate localDate = dateProvider.toLocalDate();
        if (localDate == null) {
            throw new NullPointerException("The DateProvider implementation must not return null");
        }

        return new CopticDate(localDate);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified date.
     *
     * @param date  the date to wrap, not null
     */
    private CopticDate(LocalDate date) {
        this.date = date;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that describes the Coptic calendar system.
     *
     * @return the Coptic chronology, never null
     */
    public CopticChronology getChronology() {
        return CopticChronology.INSTANCE;
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
    public int get(DateTimeFieldRule field) {
        return field.getValue(toFlexiDateTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year value.
     *
     * @return the year
     */
    public int getYear() {
        return get(CopticChronology.INSTANCE.year());
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year, never null
     */
    public int getMonthOfYear() {
        return get(CopticChronology.INSTANCE.monthOfYear());
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month
     */
    public int getDayOfMonth() {
        return get(CopticChronology.INSTANCE.dayOfMonth());
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year
     */
    public int getDayOfYear() {
        return get(CopticChronology.INSTANCE.dayOfYear());
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week
     */
    public int getDayOfWeek() {
        return get(CopticChronology.INSTANCE.dayOfWeek());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CopticDate with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CopticDate
     */
    public CopticDate withYear(int year) {
        return copticDate(year, getMonthOfYear(), getDayOfMonth());
    }

    /**
     * Returns a copy of this CopticDate with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent
     * @return a new updated CopticDate
     */
    public CopticDate withMonthOfYear(int monthOfYear) {
        return copticDate(getYear(), monthOfYear, getDayOfMonth());
    }

    /**
     * Returns a copy of this CopticDate with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent
     * @return a new updated CopticDate
     */
    public CopticDate withDayOfMonth(int dayOfMonth) {
        return copticDate(getYear(), getMonthOfYear(), dayOfMonth);
    }

    /**
     * Returns a copy of this CopticDate with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent
     * @return a new updated CopticDate
     */
    public CopticDate withDayOfYear(int dayOfYear) {
        dayOfYear--;
        return copticDate(getYear(), dayOfYear / 30 + 1, dayOfYear % 30 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CopticDate with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CopticDate
     */
    public CopticDate plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CopticDate with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CopticDate
     */
    public CopticDate plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CopticDate with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated CopticDate
     */
    public CopticDate plusYears(int years) {
        return null; //new CopticDate(year + years, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this CopticDate with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated CopticDate
     */
    public CopticDate plusMonths(int months) {
        return null; //new CopticDate(year, monthOfYear + months, dayOfMonth);
    }

    /**
     * Returns a copy of this CopticDate with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated CopticDate
     */
    public CopticDate plusWeeks(int weeks) {
        return null; //new CopticDate(year, monthOfYear, dayOfMonth + weeks * 7);
    }

    /**
     * Returns a copy of this CopticDate with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated CopticDate
     */
    public CopticDate plusDays(int days) {
        return null; //new CopticDate(year, monthOfYear, dayOfMonth + days);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to an ISO-8601 calendar system <code>LocalDate</code>.
     *
     * @return the equivalent date in the ISO-8601 calendar system, never null
     */
    public LocalDate toLocalDate() {
        return date;
    }

    /**
     * Converts this date to a <code>FlexiDateTime</code>.
     *
     * @return the flexible date-time representation for this instance, never null
     */
    public FlexiDateTime toFlexiDateTime() {
        return new FlexiDateTime(date, null, null, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherDate  the other date instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(CopticDate otherDate) {
        return date.compareTo(otherDate.date);
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherDate  the other date instance to compare to, not null
     * @return true if this day is after the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isAfter(CopticDate otherDate) {
        return compareTo(otherDate) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherDate  the other date instance to compare to, not null
     * @return true if this day is before the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isBefore(CopticDate otherDate) {
        return compareTo(otherDate) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherDate  the other date instance to compare to, null returns false
     * @return true if this day is equal to the specified day
     */
    @Override
    public boolean equals(Object otherDate) {
        if (this == otherDate) {
            return true;
        }
        if (otherDate instanceof CopticDate) {
            CopticDate other = (CopticDate) otherDate;
            return date.equals(other.date);
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
        return date.hashCode();
    }


    //-----------------------------------------------------------------------
    /**
     * Outputs the date as a <code>String</code>, such as '2007-13-01'.
     * <p>
     * The output will be in the format 'yyyy-MM-dd'.
     *
     * @return the formatted date string, never null
     */
    @Override
    public String toString() {
        int yearValue = getYear();
        int monthValue = getMonthOfYear();
        int dayValue = getDayOfMonth();
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(12);
        if (absYear < 1000) {
            if (yearValue < 0) {
                buf.append(yearValue - 10000).deleteCharAt(1);
            } else {
                buf.append(yearValue + 10000).deleteCharAt(0);
            }
        } else {
            buf.append(yearValue);
        }
        return buf.append(monthValue < 10 ? "-0" : "-")
            .append(monthValue)
            .append(dayValue < 10 ? "-0" : "-")
            .append(dayValue)
            .toString();
    }

}
