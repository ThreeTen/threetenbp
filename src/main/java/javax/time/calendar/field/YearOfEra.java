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
package javax.time.calendar.field;

import java.io.Serializable;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.calendar.TimeFieldRule;

/**
 * A time field representing a year of era.
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
public final class YearOfEra implements Calendrical, Comparable<YearOfEra>, Serializable {

    /**
     * The rule implementation that defines how the year of era field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this class.
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
     */
    public static YearOfEra yearOfEra(int yearOfEra) {
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
    public int getYearOfEra() {
        return yearOfEra;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * YearOfEra instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
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

    /**
     * Is this year of era instance greater than the specified year of era.
     *
     * @param otherYearOfEra  the other year of era instance, not null
     * @return true if this year of era is greater
     * @throws NullPointerException if otherYearOfEra is null
     */
    public boolean isGreaterThan(YearOfEra otherYearOfEra) {
        return compareTo(otherYearOfEra) > 0;
    }

    /**
     * Is this year of era instance less than the specified year of era.
     *
     * @param otherYearOfEra  the other year of era instance, not null
     * @return true if this year of era is less
     * @throws NullPointerException if otherYearOfEra is null
     */
    public boolean isLessThan(YearOfEra otherYearOfEra) {
        return compareTo(otherYearOfEra) < 0;
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
