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
 * A calendrical representation of a second of minute.
 * <p>
 * SecondOfMinute is an immutable time field that can only store a second of minute.
 * It is a type-safe way of representing a second of minute in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The second of minute may be queried using getSecondOfMinute().
 * <p>
 * SecondOfMinute is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class SecondOfMinute implements Calendrical, Comparable<SecondOfMinute>, Serializable {

    /**
     * The rule implementation that defines how the second of minute field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The second of minute being represented.
     */
    private final int secondOfMinute;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>SecondOfMinute</code>.
     *
     * @param secondOfMinute  the second of minute to represent
     * @return the created SecondOfMinute
     */
    public static SecondOfMinute secondOfMinute(int secondOfMinute) {
        RULE.checkValue(secondOfMinute);
        return new SecondOfMinute(secondOfMinute);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified second of minute.
     *
     * @param secondOfMinute  the second of minute to represent
     */
    private SecondOfMinute(int secondOfMinute) {
        this.secondOfMinute = secondOfMinute;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the second of minute value.
     *
     * @return the second of minute
     */
    public int getValue() {
        return secondOfMinute;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * SecondOfMinute instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this second of minute instance to another.
     *
     * @param otherSecondOfMinute  the other second of minute instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherSecondOfMinute is null
     */
    public int compareTo(SecondOfMinute otherSecondOfMinute) {
        int thisValue = this.secondOfMinute;
        int otherValue = otherSecondOfMinute.secondOfMinute;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this second of minute instance greater than the specified second of minute.
     *
     * @param otherSecondOfMinute  the other second of minute instance, not null
     * @return true if this second of minute is greater
     * @throws NullPointerException if otherSecondOfMinute is null
     */
    public boolean isGreaterThan(SecondOfMinute otherSecondOfMinute) {
        return compareTo(otherSecondOfMinute) > 0;
    }

    /**
     * Is this second of minute instance less than the specified second of minute.
     *
     * @param otherSecondOfMinute  the other second of minute instance, not null
     * @return true if this second of minute is less
     * @throws NullPointerException if otherSecondOfMinute is null
     */
    public boolean isLessThan(SecondOfMinute otherSecondOfMinute) {
        return compareTo(otherSecondOfMinute) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the second of minute.
     *
     * @param otherSecondOfMinute  the other second of minute instance, null returns false
     * @return true if the second of minute is the same
     */
    @Override
    public boolean equals(Object otherSecondOfMinute) {
        if (this == otherSecondOfMinute) {
            return true;
        }
        if (otherSecondOfMinute instanceof SecondOfMinute) {
            return secondOfMinute == ((SecondOfMinute) otherSecondOfMinute).secondOfMinute;
        }
        return false;
    }

    /**
     * A hashcode for the second of minute object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return secondOfMinute;
    }

    /**
     * A string describing the second of minute object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "SecondOfMinute=" + getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the second of minute field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("SecondOfMinute", null, null, 0, 59);
        }
    }

}
