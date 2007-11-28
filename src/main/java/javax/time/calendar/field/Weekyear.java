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
 * A calendrical representation of a week-based year.
 * <p>
 * Weekyear is an immutable time field that can only store a week-based year.
 * It is a type-safe way of representing a week-based year in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The week-based year may be queried using getWeekyear().
 * <p>
 * Weekyear is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class Weekyear implements Calendrical, Comparable<Weekyear>, Serializable {

    /**
     * The rule implementation that defines how the week-based year field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The week-based year being represented.
     */
    private final int weekyear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Weekyear</code>.
     *
     * @param weekyear  the week-based year to represent
     * @return the created Weekyear
     */
    public static Weekyear weekyear(int weekyear) {
        RULE.checkValue(weekyear);
        return new Weekyear(weekyear);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified week-based year.
     *
     * @param weekyear  the week-based year to represent
     */
    private Weekyear(int weekyear) {
        this.weekyear = weekyear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the week-based year value.
     *
     * @return the week-based year
     */
    public int getValue() {
        return weekyear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * Weekyear instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this week-based year instance to another.
     *
     * @param otherWeekyear  the other week-based year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherWeekyear is null
     */
    public int compareTo(Weekyear otherWeekyear) {
        int thisValue = this.weekyear;
        int otherValue = otherWeekyear.weekyear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this week-based year instance greater than the specified week-based year.
     *
     * @param otherWeekyear  the other week-based year instance, not null
     * @return true if this week-based year is greater
     * @throws NullPointerException if otherWeekyear is null
     */
    public boolean isGreaterThan(Weekyear otherWeekyear) {
        return compareTo(otherWeekyear) > 0;
    }

    /**
     * Is this week-based year instance less than the specified week-based year.
     *
     * @param otherWeekyear  the other week-based year instance, not null
     * @return true if this week-based year is less
     * @throws NullPointerException if otherWeekyear is null
     */
    public boolean isLessThan(Weekyear otherWeekyear) {
        return compareTo(otherWeekyear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the week-based year.
     *
     * @param otherWeekyear  the other week-based year instance, null returns false
     * @return true if the week-based year is the same
     */
    @Override
    public boolean equals(Object otherWeekyear) {
        if (this == otherWeekyear) {
            return true;
        }
        if (otherWeekyear instanceof Weekyear) {
            return weekyear == ((Weekyear) otherWeekyear).weekyear;
        }
        return false;
    }

    /**
     * A hashcode for the week-based year object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return weekyear;
    }

    /**
     * A string describing the week-based year object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "Weekyear=" + getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the week-based year field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("Weekyear", null, null, Integer.MIN_VALUE + 1, Integer.MAX_VALUE -1);
        }
    }

}
