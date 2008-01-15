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
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeAdjustor;
import javax.time.calendar.TimeFieldRule;
import javax.time.calendar.TimeMatcher;

/**
 * A calendrical representation of a nano of second.
 * <p>
 * NanoOfSecond is an immutable time field that can only store a nano of second.
 * It is a type-safe way of representing a nano of second in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The nano of second may be queried using getNanoOfSecond().
 * <p>
 * NanoOfSecond is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class NanoOfSecond
        implements Calendrical, Comparable<NanoOfSecond>, Serializable, TimeAdjustor, TimeMatcher {

    /**
     * The rule implementation that defines how the nano of second field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A singleton instance for zero nanoseconds.
     */
    public static final NanoOfSecond NANO_0 = new NanoOfSecond(0);
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The nano of second being represented.
     */
    private final int nanoOfSecond;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>NanoOfSecond</code>.
     *
     * @param nanoOfSecond  the nano of second to represent
     * @return the created NanoOfSecond
     */
    public static NanoOfSecond nanoOfSecond(int nanoOfSecond) {
        RULE.checkValue(nanoOfSecond);
        if (nanoOfSecond == 0) {
            return NANO_0;
        }
        return new NanoOfSecond(nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified nano of second.
     *
     * @param nanoOfSecond  the nano of second to represent
     */
    private NanoOfSecond(int nanoOfSecond) {
        this.nanoOfSecond = nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the nano of second value.
     *
     * @return the nano of second
     */
    public int getValue() {
        return nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * NanoOfSecond instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this nano of second instance to another.
     *
     * @param otherNanoOfSecond  the other nano of second instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherNanoOfSecond is null
     */
    public int compareTo(NanoOfSecond otherNanoOfSecond) {
        int thisValue = this.nanoOfSecond;
        int otherValue = otherNanoOfSecond.nanoOfSecond;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this nano of second instance greater than the specified nano of second.
     *
     * @param otherNanoOfSecond  the other nano of second instance, not null
     * @return true if this nano of second is greater
     * @throws NullPointerException if otherNanoOfSecond is null
     */
    public boolean isGreaterThan(NanoOfSecond otherNanoOfSecond) {
        return compareTo(otherNanoOfSecond) > 0;
    }

    /**
     * Is this nano of second instance less than the specified nano of second.
     *
     * @param otherNanoOfSecond  the other nano of second instance, not null
     * @return true if this nano of second is less
     * @throws NullPointerException if otherNanoOfSecond is null
     */
    public boolean isLessThan(NanoOfSecond otherNanoOfSecond) {
        return compareTo(otherNanoOfSecond) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the nano of second.
     *
     * @param otherNanoOfSecond  the other nano of second instance, null returns false
     * @return true if the nano of second is the same
     */
    @Override
    public boolean equals(Object otherNanoOfSecond) {
        if (this == otherNanoOfSecond) {
            return true;
        }
        if (otherNanoOfSecond instanceof NanoOfSecond) {
            return nanoOfSecond == ((NanoOfSecond) otherNanoOfSecond).nanoOfSecond;
        }
        return false;
    }

    /**
     * A hashcode for the nano of second object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return nanoOfSecond;
    }

    /**
     * A string describing the nano of second object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "NanoOfSecond=" + getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a time to have the the nano of second represented by this object,
     * returning a new time.
     * <p>
     * Only the nano of second field is adjusted in the result. The other time
     * fields are unaffected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    public LocalTime adjustTime(LocalTime time) {
        if (this == time.getNanoOfSecond()) {
            return time;
        }
        return LocalTime.time(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute(), this);
    }

    /**
     * Checks if the input time has the same nano of second that is represented
     * by this object.
     *
     * @param time  the time to match, not null
     * @return true if the time matches, false otherwise
     */
    public boolean matchesTime(LocalTime time) {
        return this == time.getNanoOfSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value as a fraction of a second expressed as a double.
     *
     * @return the nano of second, from 0 to 0.999,999,999
     */
    public double getFractionalValue() {
        return ((double) nanoOfSecond) / 1000000000d;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the nano of second field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("NanoOfSecond", null, null, 0, 999999999);
        }
    }

}
