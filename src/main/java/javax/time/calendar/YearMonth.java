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
package javax.time.calendar;

import java.io.Serializable;

import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.FlexiDateTime;
import javax.time.period.PeriodView;
import javax.time.period.Periods;

/**
 * A year-month without a time zone in the ISO-8601 calendar system,
 * such as 'December 2007'.
 * <p>
 * YearMonth is an immutable calendrical that represents a year-month combination.
 * This class does not store or represent a day, time or time zone.
 * Thus, for example, the value "October 2007" can be stored in a YearMonth.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * YearMonth is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class YearMonth
        implements Calendrical, Comparable<YearMonth>, Serializable, DateAdjustor, DateMatcher {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1507289123L;

    /**
     * The year, not null.
     */
    private final Year year;
    /**
     * The month of year, not null.
     */
    private final MonthOfYear month;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>YearMonth</code>.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @return a YearMonth object, never null
     */
    public static YearMonth yearMonth(Year year, MonthOfYear monthOfYear) {
        if (year == null) {
            throw new NullPointerException("Year must not be null");
        }
        if (monthOfYear == null) {
            throw new NullPointerException("MonthOfYear must not be null");
        }
        return new YearMonth(year, monthOfYear);
    }

    /**
     * Obtains an instance of <code>YearMonth</code>.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @return a YearMonth object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static YearMonth yearMonth(int year, MonthOfYear monthOfYear) {
        return yearMonth(Year.isoYear(year), monthOfYear);
    }

    /**
     * Obtains an instance of <code>YearMonth</code>.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a YearMonth object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static YearMonth yearMonth(int year, int monthOfYear) {
        return yearMonth(Year.isoYear(year), MonthOfYear.monthOfYear(monthOfYear));
    }

    /**
     * Obtains an instance of <code>YearMonth</code> from a date provider.
     * <p>
     * This can be used extract a year-month object directly from any implementation
     * of DateProvider, including those in other calendar systems.
     *
     * @param dateProvider  the date provider to use, not null
     * @return a YearMonth object, never null
     */
    public static YearMonth yearMonth(DateProvider dateProvider) {
        LocalDate localDate = dateProvider.toLocalDate();
        return new YearMonth(localDate.getYear(), localDate.getMonthOfYear());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     */
    private YearMonth(Year year, MonthOfYear monthOfYear) {
        this.year = year;
        this.month = monthOfYear;
    }

    /**
     * Returns a copy of this year-month with the new year and month, checking
     * to see if a new object is in fact required.
     *
     * @param newYear  the year to represent, not null
     * @param newMonth  the month of year to represent, not null
     * @return the year-month, never null
     */
    private YearMonth withYearMonth(Year newYear, MonthOfYear newMonth) {
        if (year.equals(newYear) && month == newMonth) {
            return this;
        }
        return new YearMonth(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that describes the calendar system rules for
     * this year-month.
     *
     * @return the ISO chronology, never null
     */
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>YearMonth</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(DateTimeFieldRule field) {
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
    public int get(DateTimeFieldRule field) {
        return field.getValue(toFlexiDateTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method provides access to an object representing the year field.
     * This can be used to access the {@link Year#getValue() int value}.
     *
     * @return the year, never null
     */
    public Year getYear() {
        return year;
    }

    /**
     * Gets the month of year field.
     * <p>
     * This method provides access to an object representing the month field.
     * This can be used to access the {@link MonthOfYear#getValue() int value}.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return month;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth with the year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, not null
     * @return a new updated YearMonth, never null
     */
    public YearMonth with(Year year) {
        if (year == null) {
            throw new NullPointerException("Year must not be null");
        }
        return withYearMonth(year, month);
    }

    /**
     * Returns a copy of this YearMonth with the month of year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, not null
     * @return a new updated YearMonth, never null
     */
    public YearMonth with(MonthOfYear monthOfYear) {
        if (monthOfYear == null) {
            throw new NullPointerException("MonthOfYear must not be null");
        }
        return withYearMonth(year, monthOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated YearMonth, never null
     */
    public YearMonth withYear(int year) {
        if (this.year.getValue() == year) {
            return this;
        }
        return withYearMonth(Year.isoYear(year), month);
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
        if (this.month.getValue() == monthOfYear) {
            return this;
        }
        return withYearMonth(year, MonthOfYear.monthOfYear(monthOfYear));
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
     * Returns a copy of this YearMonth with the specified period in years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, positive or negative
     * @return a new updated YearMonth, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public YearMonth plusYears(int years) {
        if (years == 0) {
            return this;
        }
        Year newYear = year.plusYears(years);
        return withYearMonth(newYear, month);
    }

    /**
     * Returns a copy of this YearMonth with the specified period in months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a new updated YearMonth, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public YearMonth plusMonths(int months) {
        if (months == 0) {
            return this;
        }
        long newMonth0 = month.getValue() - 1;
        newMonth0 = newMonth0 + months;
        int years = (int) (newMonth0 / 12);
        newMonth0 = newMonth0 % 12;
        if (newMonth0 < 0) {
            newMonth0 += 12;
            years--;
        }
        Year newYear = year.plusYears(years);
        MonthOfYear newMonth = MonthOfYear.monthOfYear((int) ++newMonth0);
        return withYearMonth(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth rolling the month of year field by the
     * specified number of months.
     * <p>
     * This method will add the specified number of months to the month-day,
     * rolling from December back to January if necessary.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to roll by, positive or negative
     * @return a new updated YearMonth, never null
     */
    public YearMonth rollMonthOfYear(int months) {
        if (months == 0) {
            return this;
        }
        int newMonth0 = (months % 12) + (month.getValue() - 1);
        newMonth0 = (newMonth0 + 12) % 12;
        return withMonthOfYear(++newMonth0);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a date to have the value of this year-month, returning a new date.
     * <p>
     * If the day of month is invalid for the new year then the
     * {@link DateResolvers#previousValid()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     */
    public LocalDate adjustDate(LocalDate date) {
        return adjustDate(date, DateResolvers.previousValid());
    }

    /**
     * Adjusts a date to have the value of this year-month, using a resolver to
     * handle the case when the day of month becomes invalid.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @param resolver  the date resolver to use if the day of month becomes invalid, not null
     * @return the adjusted date, never null
     * @throws IllegalCalendarFieldValueException if the date cannot be resolved using the resolver
     */
    public LocalDate adjustDate(LocalDate date, DateResolver resolver) {
        if (year.equals(date.getYear()) && month == date.getMonthOfYear()) {
            return date;
        }
        return resolver.resolveDate(year, month, date.getDayOfMonth());
    }

    /**
     * Checks if the year-month represented by this object matches the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date matches, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return year.equals(date.getYear()) && month == date.getMonthOfYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to a <code>FlexiDateTime</code>.
     *
     * @return the flexible date-time representation for this instance, never null
     */
    public FlexiDateTime toFlexiDateTime() {
        return new FlexiDateTime(Year.rule(), year.getValue(), MonthOfYear.rule(), month.getValue());
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
        int cmp = year.compareTo(other.year);
        if (cmp == 0) {
            cmp = month.compareTo(other.month);
        }
        return cmp;
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
            YearMonth otherYM = (YearMonth) other;
            return year.equals(otherYM.year) && month == otherYM.month;
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
        return year.getValue() ^ (month.getValue() << 27);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the year-month as a <code>String</code>.
     * <p>
     * The output will be in the format 'yyyy-MM':
     *
     * @return the string form of the year-month
     */
    @Override
    public String toString() {
        int yearValue = year.getValue();
        int monthValue = month.getValue();
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(9);
        if (absYear < 1000) {
            if (yearValue < 0) {
                buf.append(yearValue - 10000).deleteCharAt(1);
            } else {
                buf.append(yearValue + 10000).deleteCharAt(0);
            }
        } else {
            buf.append(year);
        }
        return buf.append(monthValue < 10 ? "-0" : "-")
            .append(monthValue)
            .toString();
    }

}
