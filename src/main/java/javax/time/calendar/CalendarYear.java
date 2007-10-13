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
import javax.time.calendar.field.Era;
import javax.time.period.PeriodView;

/**
 * A time point of a year.
 * <p>
 * CalendarYear is an immutable time point that records time information to the
 * precision of a year. For example, the value "2007" can be stored in a CalendarYear.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarYear is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarYear implements Calendrical, Comparable<CalendarYear>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -6758311647959144592L;

    /**
     * The year being represented.
     */
    private final int year;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarYear</code>.
     * <p>
     * This method accepts a year value from the proleptic ISO calendar system.
     * <p>
     * The year 1AD is represented by 1.<br />
     * The year 1BC is represented by 0.<br />
     * The year 2BC is represented by -1.<br />
     *
     * @param isoYear  the ISO proleptic year to represent
     * @return the created CalendarYear
     */
    public static CalendarYear year(int isoYear) {
        return new CalendarYear(isoYear);
    }

    /**
     * Obtains an instance of <code>CalendarYear</code> using an era.
     * <p>
     * This method accepts a year and era to create a year object.
     *
     * @param era  the era to represent, either BC or AD, not null
     * @param yearOfEra  the year within the era to represent, from 1 to MAX_VALUE
     * @return the year object, never null
     */
    public static CalendarYear year(Era era, int yearOfEra) {
        if (yearOfEra < 1) {
            throw new IllegalCalendarFieldValueException("year of era", yearOfEra, 1, Integer.MAX_VALUE);
        }
        if (era == Era.AD) {
            return CalendarYear.year(yearOfEra);
        } else {
            return CalendarYear.year((-yearOfEra) + 1);
        }
    }

    /**
     * Obtains an instance of <code>CalendarYear</code> from a set of moments.
     * <p>
     * This can be used to pass in any combination of moments that fully specify
     * a calendar year. For example, Century + YearOfCentury.
     *
     * @param moments  a set of moments that fully represent a calendar year
     * @return a CalendarYear object
     */
    public static CalendarYear calendarYear(Calendrical... moments) {
        return new CalendarYear(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified year.
     *
     * @param year  the year to represent
     */
    private CalendarYear(int year) {
        this.year = year;
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

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarYear with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moment  the moment to update to, not null
     * @return a new updated CalendarYear
     */
    public CalendarYear with(Calendrical moment) {
        return null;
    }

    /**
     * Returns a copy of this CalendarYear with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param moments  the moments to update to, not null
     * @return a new updated CalendarYear
     */
    public CalendarYear with(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarYear with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CalendarYear
     */
    public CalendarYear withYear(int year) {
        return new CalendarYear(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarYear with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CalendarYear
     */
    public CalendarYear plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarYear with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CalendarYear
     */
    public CalendarYear plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarYear with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated CalendarYear
     */
    public CalendarYear plusYears(int years) {
        return new CalendarYear(year + years);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherYear  the other year instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherYear is null
     */
    public int compareTo(CalendarYear otherYear) {
        return MathUtils.safeCompare(year, otherYear.year);
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherYear  the other month instance to compare to, not null
     * @return true if this year is after the specified year
     * @throws NullPointerException if otherYear is null
     */
    public boolean isAfter(CalendarYear otherYear) {
        return compareTo(otherYear) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherYear  the other year instance to compare to, not null
     * @return true if this year is before the specified year
     * @throws NullPointerException if otherYear is null
     */
    public boolean isBefore(CalendarYear otherYear) {
        return compareTo(otherYear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherYear  the other month instance to compare to, null returns false
     * @return true if this month is equal to the specified month
     */
    @Override
    public boolean equals(Object otherYear) {
        if (this == otherYear) {
            return true;
        }
        if (otherYear instanceof CalendarYear) {
            return year == ((CalendarYear) otherYear).year;
        }
        return false;
    }

    /**
     * A hashcode for the month object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return year;
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
     * For example, 1904 is a leap year it is divisble by 4.
     * 1900 was not a leap year as it is divisble by 100, however 2000 was a
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

    /**
     * Returns the next year.
     *
     * @return the next year, never null
     * @throws IllegalCalendarFieldValueException if the maximum year is reached
     */
    public CalendarYear next() {
        if (year == Integer.MAX_VALUE) {
            throw new IllegalCalendarFieldValueException("year is already at the maximum value");
        }
        return year(year + 1);
    }

    /**
     * Returns the next leap year after the current year.
     * The definition of a leap year is specified in {@link #isLeap()}.
     *
     * @return the next leap year after this year
     * @throws IllegalCalendarFieldValueException if the maximum year is reached
     */
    public CalendarYear nextLeap() {
        CalendarYear temp = next();
        while (!temp.isLeap()) {
            temp = temp.next();
        }
        return temp;
    }

    /**
     * Returns the previous year.
     *
     * @return the previous year, never null
     * @throws IllegalCalendarFieldValueException if the maximum year is reached
     */
    public CalendarYear previous() {
        if (year == (Integer.MIN_VALUE + 1)) {
            throw new IllegalCalendarFieldValueException("year is already at the minimum value");
        }
        return year(year - 1);
    }

    /**
     * Returns the previous leap year before the current year.
     * The definition of a leap year is specified in {@link #isLeap()}.
     *
     * @return the previous leap year after this year
     * @throws IllegalCalendarFieldValueException if the minimum year is reached
     */
    public CalendarYear previousLeap() {
        CalendarYear temp = previous();
        while (!temp.isLeap()) {
            temp = temp.previous();
        }
        return temp;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ISO proleptic year, from MIN_VALUE+1 to MAX_VALUE.
     *
     * @return the ISO proleptic year, from MIN_VALUE+1 to MAX_VALUE
     */
    public int getISOYear() {
        return year;
    }

    /**
     * Gets the year of era, from 1 to MAX_VALUE.
     *
     * @return the year of era, from 1 to MAX_VALUE.
     */
    public int getYearOfEra() {
        return (year > 0 ? year : -(year - 1));
    }

    /**
     * Gets the century of era, from 0 to MAX_VALUE / 100.
     * <p>
     * This method uses a simple definition of century, being the
     * year of era divided by 100.
     * <p>
     * The value 20 will be returned from 2000AD to 2099AD.<br/>
     * The value 19 will be returned from 1900AD to 1999AD.<br/>
     * The value 0 will be returned from 1AD to 99AD.<br/>
     * The value 0 will be returned from 99BC to 1BC.<br/>
     * The value 1 will be returned from 1000BC to 1999BC.<br/>
     *
     * @return the century of era, from 0 to MAX_VALUE / 100.
     */
    public int getCenturyOfEra() {
        return getYearOfEra() / 100;
    }

    /**
     * Gets the millenium of era, from 0 to MAX_VALUE / 1000.
     * <p>
     * This method uses a simple definition of millenium, being the
     * year of era divided by 100.
     * <p>
     * The value 2 will be returned from 2000AD to 2999AD.<br/>
     * The value 1 will be returned from 1000AD to 1999AD.<br/>
     * The value 0 will be returned from 1AD to 999AD.<br/>
     * The value 0 will be returned from 999BC to 1BC.<br/>
     * The value 1 will be returned from 1000BC to 1999BC.<br/>
     *
     * @return the millenium of era, from 0 to MAX_VALUE / 1000.
     */
    public int getMilleniumOfEra() {
        return getYearOfEra() / 1000;
    }

    /**
     * Gets the era.
     *
     * @return the era, never null
     */
    public Era getEra() {
        return (year > 0 ? Era.AD : Era.BC);
    }

    /**
     * Gets the decade of century, from 0 to 9.
     * <p>
     * This method uses a simple definition of decade, being the
     * remainder of the year of era divided by 10.
     * <p>
     * The value 2 will be returned from 2020AD to 2029AD.<br/>
     * The value 1 will be returned from 2010AD to 2019AD.<br/>
     * The value 0 will be returned from 2000AD to 2009AD.<br/>
     * The value 9 will be returned from 1990AD to 1999AD.<br/>
     *
     * @return the decade of era, from 0 to 9.
     */
    public int getDecadeOfCentury() {
        return (Math.abs(getYearOfEra()) % 100) / 10;
    }

}
