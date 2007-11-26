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
 * A calendrical representation of a millenium of era.
 * <p>
 * MilleniumOfEra is an immutable time field that can only store a millenium of era.
 * It is a type-safe way of representing a millenium of era in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The millenium of era may be queried using getMilleniumOfEra().
 * <p>
 * MilleniumOfEra is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class MilleniumOfEra implements Calendrical, Comparable<MilleniumOfEra>, Serializable {

    /**
     * The rule implementation that defines how the millenium of era field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The millenium of era being represented.
     */
    private final int milleniumOfEra;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MilleniumOfEra</code>.
     *
     * @param milleniumOfEra  the millenium of era to represent
     * @return the created MilleniumOfEra
     */
    public static MilleniumOfEra milleniumOfEra(int milleniumOfEra) {
        return new MilleniumOfEra(milleniumOfEra);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified millenium of era.
     *
     * @param milleniumOfEra  the millenium of era to represent
     */
    private MilleniumOfEra(int milleniumOfEra) {
        this.milleniumOfEra = milleniumOfEra;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the millenium of era value.
     *
     * @return the millenium of era
     */
    public int getValue() {
        return milleniumOfEra;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * MilleniumOfEra instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this millenium of era instance to another.
     *
     * @param otherMilleniumOfEra  the other millenium of era instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMilleniumOfEra is null
     */
    public int compareTo(MilleniumOfEra otherMilleniumOfEra) {
        int thisValue = this.milleniumOfEra;
        int otherValue = otherMilleniumOfEra.milleniumOfEra;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this millenium of era instance greater than the specified millenium of era.
     *
     * @param otherMilleniumOfEra  the other millenium of era instance, not null
     * @return true if this millenium of era is greater
     * @throws NullPointerException if otherMilleniumOfEra is null
     */
    public boolean isGreaterThan(MilleniumOfEra otherMilleniumOfEra) {
        return compareTo(otherMilleniumOfEra) > 0;
    }

    /**
     * Is this millenium of era instance less than the specified millenium of era.
     *
     * @param otherMilleniumOfEra  the other millenium of era instance, not null
     * @return true if this millenium of era is less
     * @throws NullPointerException if otherMilleniumOfEra is null
     */
    public boolean isLessThan(MilleniumOfEra otherMilleniumOfEra) {
        return compareTo(otherMilleniumOfEra) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the millenium of era.
     *
     * @param otherMilleniumOfEra  the other millenium of era instance, null returns false
     * @return true if the millenium of era is the same
     */
    @Override
    public boolean equals(Object otherMilleniumOfEra) {
        if (this == otherMilleniumOfEra) {
            return true;
        }
        if (otherMilleniumOfEra instanceof MilleniumOfEra) {
            return milleniumOfEra == ((MilleniumOfEra) otherMilleniumOfEra).milleniumOfEra;
        }
        return false;
    }

    /**
     * A hashcode for the millenium of era object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return milleniumOfEra;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the millenium of era field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("MilleniumOfEra", null, null, 0, Integer.MAX_VALUE / 1000);
        }
    }

}
