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
 * A calendrical representation of a hour of meridiem.
 * <p>
 * HourOfMeridiem is an immutable time field that can only store a hour of meridiem.
 * It is a type-safe way of representing a hour of meridiem in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The hour of meridiem may be queried using getHourOfMeridiem().
 * <p>
 * HourOfMeridiem is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class HourOfMeridiem implements Calendrical, Comparable<HourOfMeridiem>, Serializable {

    /**
     * The rule implementation that defines how the hour of meridiem field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The hour of meridiem being represented.
     */
    private final int hourOfMeridiem;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>HourOfMeridiem</code>.
     *
     * @param hourOfMeridiem  the hour of meridiem to represent
     * @return the created HourOfMeridiem
     */
    public static HourOfMeridiem hourOfMeridiem(int hourOfMeridiem) {
        return new HourOfMeridiem(hourOfMeridiem);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified hour of meridiem.
     *
     * @param hourOfMeridiem  the hour of meridiem to represent
     */
    private HourOfMeridiem(int hourOfMeridiem) {
        this.hourOfMeridiem = hourOfMeridiem;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of meridiem value.
     *
     * @return the hour of meridiem
     */
    public int getValue() {
        return hourOfMeridiem;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * HourOfMeridiem instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this hour of meridiem instance to another.
     *
     * @param otherHourOfMeridiem  the other hour of meridiem instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherHourOfMeridiem is null
     */
    public int compareTo(HourOfMeridiem otherHourOfMeridiem) {
        int thisValue = this.hourOfMeridiem;
        int otherValue = otherHourOfMeridiem.hourOfMeridiem;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this hour of meridiem instance greater than the specified hour of meridiem.
     *
     * @param otherHourOfMeridiem  the other hour of meridiem instance, not null
     * @return true if this hour of meridiem is greater
     * @throws NullPointerException if otherHourOfMeridiem is null
     */
    public boolean isGreaterThan(HourOfMeridiem otherHourOfMeridiem) {
        return compareTo(otherHourOfMeridiem) > 0;
    }

    /**
     * Is this hour of meridiem instance less than the specified hour of meridiem.
     *
     * @param otherHourOfMeridiem  the other hour of meridiem instance, not null
     * @return true if this hour of meridiem is less
     * @throws NullPointerException if otherHourOfMeridiem is null
     */
    public boolean isLessThan(HourOfMeridiem otherHourOfMeridiem) {
        return compareTo(otherHourOfMeridiem) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the hour of meridiem.
     *
     * @param otherHourOfMeridiem  the other hour of meridiem instance, null returns false
     * @return true if the hour of meridiem is the same
     */
    @Override
    public boolean equals(Object otherHourOfMeridiem) {
        if (this == otherHourOfMeridiem) {
            return true;
        }
        if (otherHourOfMeridiem instanceof HourOfMeridiem) {
            return hourOfMeridiem == ((HourOfMeridiem) otherHourOfMeridiem).hourOfMeridiem;
        }
        return false;
    }

    /**
     * A hashcode for the hour of meridiem object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return hourOfMeridiem;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the hour of meridiem field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("HourOfMeridiem", null, null, 0, 11);
        }
    }

}
