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
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.TimeFieldRule;

/**
 * A calendrical representation of a decade of century.
 * <p>
 * DecadeOfCentury is an immutable time field that can only store a decade of century.
 * It is a type-safe way of representing a decade of century in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The decade of century may be queried using getDecadeOfCentury().
 * <p>
 * DecadeOfCentury is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class DecadeOfCentury implements Calendrical, Comparable<DecadeOfCentury>, Serializable {

    /**
     * The rule implementation that defines how the decade of century field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The decade of century being represented.
     */
    private final int decadeOfCentury;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>DecadeOfCentury</code>.
     *
     * @param decadeOfCentury  the decade of century to represent
     * @return the created DecadeOfCentury
     * @throws IllegalCalendarFieldValueException if the decadeOfCentury is invalid
     */
    public static DecadeOfCentury decadeOfCentury(int decadeOfCentury) {
        RULE.checkValue(decadeOfCentury);
        return new DecadeOfCentury(decadeOfCentury);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified decade of century.
     *
     * @param decadeOfCentury  the decade of century to represent
     */
    private DecadeOfCentury(int decadeOfCentury) {
        this.decadeOfCentury = decadeOfCentury;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the decade of century value.
     *
     * @return the decade of century
     */
    public int getValue() {
        return decadeOfCentury;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * DecadeOfCentury instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this decade of century instance to another.
     *
     * @param otherDecadeOfCentury  the other decade of century instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDecadeOfCentury is null
     */
    public int compareTo(DecadeOfCentury otherDecadeOfCentury) {
        int thisValue = this.decadeOfCentury;
        int otherValue = otherDecadeOfCentury.decadeOfCentury;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the decade of century.
     *
     * @param otherDecadeOfCentury  the other decade of century instance, null returns false
     * @return true if the decade of century is the same
     */
    @Override
    public boolean equals(Object otherDecadeOfCentury) {
        if (this == otherDecadeOfCentury) {
            return true;
        }
        if (otherDecadeOfCentury instanceof DecadeOfCentury) {
            return decadeOfCentury == ((DecadeOfCentury) otherDecadeOfCentury).decadeOfCentury;
        }
        return false;
    }

    /**
     * A hashcode for the decade of century object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return decadeOfCentury;
    }

    /**
     * A string describing the decade of century object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "DecadeOfCentury=" + getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the decade of century field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("DecadeOfCentury", null, null, 0, 9);
        }
    }

}
