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
package javax.time.calendar.field;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.MathUtils;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateAdjustor;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DateResolver;
import javax.time.calendar.DateResolvers;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.UnsupportedCalendarFieldException;
import javax.time.period.PeriodView;

/**
 * A representation of a year without a time zone in the ISO-8601 calendar system.
 * <p>
 * Year is an immutable calendrical that represents a year.
 * This class does not store or represent a month, day, time or time zone.
 * Thus, for example, the value "2007" can be stored in a Year.
 * <p>
 * Year is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class Year
        implements CalendricalProvider, Comparable<Year>, Serializable, DateAdjustor, DateMatcher {

    /**
     * Constant for the minimum year on the proleptic ISO calendar system.
     */
    public static final int MIN_YEAR = Integer.MIN_VALUE + 2;
    /**
     * Constant for the maximum year on the proleptic ISO calendar system,
     * which is the same as the maximum for year of era.
     */
    public static final int MAX_YEAR = Integer.MAX_VALUE;
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 2751581L;

    /**
     * The year being represented.
     */
    private final int year;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the year field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the year rule, never null
     */
    public static DateTimeFieldRule rule() {
        return ISOChronology.yearRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Year</code>.
     * <p>
     * This method accepts a year value from the proleptic ISO calendar system.
     * <p>
     * The year 2AD/CE is represented by 2.<br />
     * The year 1AD/CE is represented by 1.<br />
     * The year 1BC/BCE is represented by 0.<br />
     * The year 2BC/BCE is represented by -1.<br />
     *
     * @param isoYear  the ISO proleptic year to represent, from MIN_YEAR to MAX_YEAR
     * @return the created Year, never null
     * @throws IllegalCalendarFieldValueException if the field is invalid
     */
    public static Year isoYear(int isoYear) {
        rule().checkValue(isoYear);
        return new Year(isoYear);
    }

    /**
     * Obtains an instance of <code>Year</code> from a date provider.
     * <p>
     * This can be used extract a year object directly from any implementation
     * of DateProvider, including those in other calendar systems.
     *
     * @param dateProvider  the date provider to use, not null
     * @return a Year object, never null
     */
    public static Year year(DateProvider dateProvider) {
        return LocalDate.date(dateProvider).getYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent
     */
    private Year(int year) {
        this.year = year;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year value.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getValue() {
        return year;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>Year</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(DateTimeFieldRule field) {
        return toCalendrical().getValueQuiet(field) != null;
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
        return toCalendrical().getValue(field);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the ISO proleptic
     * calendar system rules.
     * <p>
     * This method follows the current standard rules for leap years.
     * In general, a year is a leap year if it is divisible by four without
     * remainder. However, years divisible by 100, are not leap years, with
     * the exception of years divisible by 400 which are.
     * <p>
     * For example, 1904 is a leap year it is divisible by 4.
     * 1900 was not a leap year as it is divisible by 100, however 2000 was a
     * leap year as it is divisible by 400.
     * <p>
     * This calculation is proleptic - applying the same rules into prehistory.
     * This is historically inaccurate, but is correct for the ISO8601 standard.
     *
     * @return true if the year is leap, false otherwise
     */
    public boolean isLeap() {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the next year.
     *
     * @return the next year, never null
     * @throws CalendricalException if the maximum year is reached
     */
    public Year next() {
        if (year == MAX_YEAR) {
            throw new CalendricalException("Year is already at the maximum value");
        }
        return isoYear(year + 1);
    }

    /**
     * Returns the next leap year after the current year.
     * The definition of a leap year is specified in {@link #isLeap()}.
     *
     * @return the next leap year after this year
     * @throws CalendricalException if the maximum year is reached
     */
    public Year nextLeap() {
        Year temp = next();
        while (!temp.isLeap()) {
            temp = temp.next();
        }
        return temp;
    }

    /**
     * Returns the previous year.
     *
     * @return the previous year, never null
     * @throws CalendricalException if the maximum year is reached
     */
    public Year previous() {
        if (year == MIN_YEAR) {
            throw new CalendricalException("Year is already at the minimum value");
        }
        return isoYear(year - 1);
    }

    /**
     * Returns the previous leap year before the current year.
     * The definition of a leap year is specified in {@link #isLeap()}.
     *
     * @return the previous leap year after this year
     * @throws CalendricalException if the minimum year is reached
     */
    public Year previousLeap() {
        Year temp = previous();
        while (!temp.isLeap()) {
            temp = temp.previous();
        }
        return temp;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Year with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated Year, never null
     */
    public Year plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this Year with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated Year, never null
     */
    public Year plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Year with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated Year, never null
     * @throws CalendricalException if the result exceeds the supported year range
     */
    public Year plusYears(int years) {
        int newYear = 0;
        try {
            newYear = MathUtils.safeAdd(year, years);
            return isoYear(newYear);
        } catch (ArithmeticException ae) {
            throw new CalendricalException("Year " + (((long) year) + years) + " exceeds the supported year range");
        } catch (IllegalCalendarFieldValueException ae) {
            throw new CalendricalException("Year " + (((long) year) + years) + " exceeds the supported year range");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Year with the specified number of years subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract
     * @return a new updated Year, never null
     * @throws CalendricalException if the result exceeds the supported year range
     */
    public Year minusYears(int years) {
        int newYear = 0;
        try {
            newYear = MathUtils.safeSubtract(year, years);
            return isoYear(newYear);
        } catch (ArithmeticException ae) {
            throw new CalendricalException("Year " + (((long) year) - ((long) years)) + " exceeds the supported year range");
        } catch (IllegalCalendarFieldValueException ae) {
            throw new CalendricalException("Year " + (((long) year) - ((long) years)) + " exceeds the supported year range");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a date to have the value of this year, returning a new date.
     * <p>
     * If the day of month is invalid for the new year then the
     * {@link DateResolvers#previousValid()} resolver is used.
     * This occurs if the input date is 29th February in a leap year, and this
     * object represents a non-leap year.
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
     * Adjusts a date to have the value of this year, using a resolver to
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
        if (this.equals(date.getYear())) {
            return date;
        }
        LocalDate resolved = resolver.resolveDate(this, date.getMonthOfYear(), date.getDayOfMonth());
        if (resolved == null) {
            throw new NullPointerException("The implementation of DateResolver must not return null");
        }
        return resolved;
    }

    /**
     * Checks if the value of this year matches the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date matches, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return this.equals(date.getYear());
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the ISO proleptic year, from MIN_YEAR to MAX_YEAR.
//     * <p>
//     * The year 2AD/CE is represented by 2.<br />
//     * The year 1AD/CE is represented by 1.<br />
//     * The year 1BC/BCE is represented by 0.<br />
//     * The year 2BC/BCE is represented by -1.<br />
//     *
//     * @return the ISO proleptic year, from MIN_YEAR to MAX_YEAR
//     */
//    public int getISOYear() {
//        return year;
//    }
//
//    /**
//     * Returns a new <code>Year</code> instance with a different year.
//     * <p>
//     * The year 2AD/CE is represented by 2.<br />
//     * The year 1AD/CE is represented by 1.<br />
//     * The year 1BC/BCE is represented by 0.<br />
//     * The year 2BC/BCE is represented by -1.<br />
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param isoYear  the year to represent, from MIN_YEAR to MAX_YEAR
//     * @return a new updated Year, never null
//     */
//    public Year withISOYear(int isoYear) {
//        rule().checkValue(isoYear);
//        return null;
//    }

    /**
     * Gets the year of era, from 1 to MAX_YEAR, which is used in combination
     * with {@link #getEstimatedEra()}.
     * <p>
     * The year 2, estimated as 2AD/CE is represented by 2.<br />
     * The year 1, estimated as 1AD/CE is represented by 1.<br />
     * The year 0, estimated as 1BC/BCE is represented by 1.<br />
     * The year -1, estimated as 2BC/BCE is represented by 2.<br />
     *
     * @return the year of era, from 1 to MAX_YEAR
     */
    public int getYearOfEra() {
        // TODO: ISO Year doesn't have an era
        return (year > 0 ? year : -(year - 1));
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the century of era, from 0 to MAX_YEAR / 100.
//     * <p>
//     * This method uses a simple definition of century, being the
//     * ISO year divided by 100, which is the same as the printed ISO year
//     * without the last two digits.
//     * <p>
//     * The value 20 will be returned from 2000 to 2099.<br/>
//     * The value 19 will be returned from 1900 to 1999.<br/>
//     * The value 1 will be returned from 100 to 199.<br/>
//     * The value 0 will be returned from -99 to 99 (199 years in the century).<br/>
//     * The value -1 will be returned from -100 to 199.<br/>
//     *
//     * @return the century of era, from 0 to MAX_YEAR / 100
//     */
//    public int getISOCentury() {
//        return year / 100;
//    }
//
//    /**
//     * Gets the year of century, from 0 to 99, which is used in combination
//     * with {@link #getCenturyOfEra()}.
//     * This is the lower two digits of the ISO year.
//     *
//     * @return the year of era, from 0 to 99
//     */
//    public int getYearOfISOCentury() {
//        int yoc = year % 100;
//        return yoc < 0 ? yoc + 100 : yoc;
//    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return Calendrical.calendrical(rule(), getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year to another year.
     *
     * @param other  the other year to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(Year other) {
        return MathUtils.safeCompare(year, other.year);
    }

    /**
     * Is this year after the specified year.
     *
     * @param other  the other year to compare to, not null
     * @return true if this is after the specified year
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(Year other) {
        return year > other.year;
    }

    /**
     * Is this year before the specified year.
     *
     * @param other  the other year to compare to, not null
     * @return true if this point is before the specified year
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(Year other) {
        return year < other.year;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this year equal to the specified year.
     *
     * @param other  the other year to compare to, null returns false
     * @return true if this point is equal to the specified year
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Year) {
            return year == ((Year) other).year;
        }
        return false;
    }

    /**
     * A hashcode for this year.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return year;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the string form of the year.
     *
     * @return the string form of the year
     */
    @Override
    public String toString() {
        // TODO: prefix to 4 digits
        return Integer.toString(year);
    }

}
