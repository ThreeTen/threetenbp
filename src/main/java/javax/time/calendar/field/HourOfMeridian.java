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
 * A time field representing a hour of meridian.
 * <p>
 * HourOfMeridian is an immutable time field that can only store a hour of meridian.
 * It is a type-safe way of representing a hour of meridian in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The hour of meridian may be queried using getHourOfMeridian().
 * <p>
 * HourOfMeridian is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class HourOfMeridian implements Calendrical, Comparable<HourOfMeridian>, Serializable {

    /**
     * The rule implementation that defines how the hour of meridian field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The hour of meridian being represented.
     */
    private final int hourOfMeridian;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>HourOfMeridian</code>.
     *
     * @param hourOfMeridian  the hour of meridian to represent
     * @return the created HourOfMeridian
     */
    public static HourOfMeridian hourOfMeridian(int hourOfMeridian) {
        return new HourOfMeridian(hourOfMeridian);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified hour of meridian.
     *
     * @param hourOfMeridian  the hour of meridian to represent
     */
    private HourOfMeridian(int hourOfMeridian) {
        this.hourOfMeridian = hourOfMeridian;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of meridian value.
     *
     * @return the hour of meridian
     */
    public int getHourOfMeridian() {
        return hourOfMeridian;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * HourOfMeridian instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this hour of meridian instance to another.
     *
     * @param otherHourOfMeridian  the other hour of meridian instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherHourOfMeridian is null
     */
    public int compareTo(HourOfMeridian otherHourOfMeridian) {
        int thisValue = this.hourOfMeridian;
        int otherValue = otherHourOfMeridian.hourOfMeridian;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this hour of meridian instance greater than the specified hour of meridian.
     *
     * @param otherHourOfMeridian  the other hour of meridian instance, not null
     * @return true if this hour of meridian is greater
     * @throws NullPointerException if otherHourOfMeridian is null
     */
    public boolean isGreaterThan(HourOfMeridian otherHourOfMeridian) {
        return compareTo(otherHourOfMeridian) > 0;
    }

    /**
     * Is this hour of meridian instance less than the specified hour of meridian.
     *
     * @param otherHourOfMeridian  the other hour of meridian instance, not null
     * @return true if this hour of meridian is less
     * @throws NullPointerException if otherHourOfMeridian is null
     */
    public boolean isLessThan(HourOfMeridian otherHourOfMeridian) {
        return compareTo(otherHourOfMeridian) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the hour of meridian.
     *
     * @param otherHourOfMeridian  the other hour of meridian instance, null returns false
     * @return true if the hour of meridian is the same
     */
    @Override
    public boolean equals(Object otherHourOfMeridian) {
        if (this == otherHourOfMeridian) {
            return true;
        }
        if (otherHourOfMeridian instanceof HourOfMeridian) {
            return hourOfMeridian == ((HourOfMeridian) otherHourOfMeridian).hourOfMeridian;
        }
        return false;
    }

    /**
     * A hashcode for the hour of meridian object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return hourOfMeridian;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the hour of meridian field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("HourOfMeridian", null, null, 0, 11);
        }
    }

}
