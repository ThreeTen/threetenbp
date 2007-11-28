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
 * A calendrical representation of a century of era.
 * <p>
 * CenturyOfEra is an immutable time field that can only store a century of era.
 * It is a type-safe way of representing a century of era in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The century of era may be queried using getCenturyOfEra().
 * <p>
 * CenturyOfEra is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CenturyOfEra implements Calendrical, Comparable<CenturyOfEra>, Serializable {

    /**
     * The rule implementation that defines how the century of era field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The century of era being represented.
     */
    private final int centuryOfEra;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CenturyOfEra</code>.
     *
     * @param centuryOfEra  the century of era to represent
     * @return the created CenturyOfEra
     */
    public static CenturyOfEra centuryOfEra(int centuryOfEra) {
        RULE.checkValue(centuryOfEra);
        return new CenturyOfEra(centuryOfEra);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified century of era.
     *
     * @param centuryOfEra  the century of era to represent
     */
    private CenturyOfEra(int centuryOfEra) {
        this.centuryOfEra = centuryOfEra;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the century of era value.
     *
     * @return the century of era
     */
    public int getValue() {
        return centuryOfEra;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * CenturyOfEra instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this century of era instance to another.
     *
     * @param otherCenturyOfEra  the other century of era instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherCenturyOfEra is null
     */
    public int compareTo(CenturyOfEra otherCenturyOfEra) {
        int thisValue = this.centuryOfEra;
        int otherValue = otherCenturyOfEra.centuryOfEra;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this century of era instance greater than the specified century of era.
     *
     * @param otherCenturyOfEra  the other century of era instance, not null
     * @return true if this century of era is greater
     * @throws NullPointerException if otherCenturyOfEra is null
     */
    public boolean isGreaterThan(CenturyOfEra otherCenturyOfEra) {
        return compareTo(otherCenturyOfEra) > 0;
    }

    /**
     * Is this century of era instance less than the specified century of era.
     *
     * @param otherCenturyOfEra  the other century of era instance, not null
     * @return true if this century of era is less
     * @throws NullPointerException if otherCenturyOfEra is null
     */
    public boolean isLessThan(CenturyOfEra otherCenturyOfEra) {
        return compareTo(otherCenturyOfEra) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the century of era.
     *
     * @param otherCenturyOfEra  the other century of era instance, null returns false
     * @return true if the century of era is the same
     */
    @Override
    public boolean equals(Object otherCenturyOfEra) {
        if (this == otherCenturyOfEra) {
            return true;
        }
        if (otherCenturyOfEra instanceof CenturyOfEra) {
            return centuryOfEra == ((CenturyOfEra) otherCenturyOfEra).centuryOfEra;
        }
        return false;
    }

    /**
     * A hashcode for the century of era object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return centuryOfEra;
    }

    /**
     * A string describing the century of era object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "CenturyOfEra=" + getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the century of era field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("CenturyOfEra", null, null, 0, Integer.MAX_VALUE / 100);
        }
    }

}
