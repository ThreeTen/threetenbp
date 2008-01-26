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

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.calendar.DateAdjustor;
import javax.time.calendar.DateResolver;
import javax.time.calendar.DateResolvers;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.TimeFieldRule;

/**
 * A calendrical representation of a year of era.
 * <p>
 * YearOfEra is an immutable time field that can only store a year of era.
 * It is a type-safe way of representing a year of era in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The year of era may be queried using getYearOfEra().
 * <p>
 * YearOfEra is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class YearOfEra
        implements Calendrical, Comparable<YearOfEra>, Serializable, DateAdjustor {

    /**
     * The rule implementation that defines how the year of era field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The year of era being represented.
     */
    private final int yearOfEra;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>YearOfEra</code>.
     *
     * @param yearOfEra  the year of era to represent
     * @return the created YearOfEra
     * @throws IllegalCalendarFieldValueException if the yearOfEra is invalid
     */
    public static YearOfEra yearOfEra(int yearOfEra) {
        RULE.checkValue(yearOfEra);
        return new YearOfEra(yearOfEra);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified year of era.
     *
     * @param yearOfEra  the year of era to represent
     */
    private YearOfEra(int yearOfEra) {
        this.yearOfEra = yearOfEra;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year of era value.
     *
     * @return the year of era
     */
    public int getValue() {
        return yearOfEra;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * YearOfEra instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year of era instance to another.
     *
     * @param otherYearOfEra  the other year of era instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherYearOfEra is null
     */
    public int compareTo(YearOfEra otherYearOfEra) {
        int thisValue = this.yearOfEra;
        int otherValue = otherYearOfEra.yearOfEra;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the year of era.
     *
     * @param otherYearOfEra  the other year of era instance, null returns false
     * @return true if the year of era is the same
     */
    @Override
    public boolean equals(Object otherYearOfEra) {
        if (this == otherYearOfEra) {
            return true;
        }
        if (otherYearOfEra instanceof YearOfEra) {
            return yearOfEra == ((YearOfEra) otherYearOfEra).yearOfEra;
        }
        return false;
    }

    /**
     * A hashcode for the year of era object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return yearOfEra;
    }

    /**
     * A string describing the year of era object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "YearOfEra=" + getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a date to have the year of era represented by this object,
     * returning a new date.
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
        if (this.getValue() == date.getYear().getYearOfEra()) {
            return date;
        }
        Year newYear = Year.year(date.getYear().getEra(), this.getValue());
        return resolver.resolveDate(newYear, date.getMonthOfYear(), date.getDayOfMonth());
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the year of era field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("YearOfEra", null, null, 1, Integer.MAX_VALUE);
        }
    }

}
