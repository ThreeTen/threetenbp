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
import javax.time.duration.Durational;

/**
 * A time field representing a minute of hour.
 * <p>
 * MinuteOfHour is an immutable time field that can only store a minute of hour.
 * It is a type-safe way of representing a minute of hour in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The minute of hour may be queried using getMinuteOfHour().
 * <p>
 * MinuteOfHour is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class MinuteOfHour implements Calendrical, Comparable<MinuteOfHour>, Serializable {

    /**
     * The rule implementation that defines how the minute of hour field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The minute of hour being represented.
     */
    private final int minuteOfHour;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MinuteOfHour</code>.
     *
     * @param minuteOfHour  the minute of hour to represent
     * @return the created MinuteOfHour
     */
    public static MinuteOfHour minuteOfHour(int minuteOfHour) {
        return new MinuteOfHour(minuteOfHour);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified minute of hour.
     *
     * @param minuteOfHour  the minute of hour to represent
     */
    private MinuteOfHour(int minuteOfHour) {
        this.minuteOfHour = minuteOfHour;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour
     */
    public int getMinuteOfHour() {
        return minuteOfHour;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * MinuteOfHour instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this minute of hour instance to another.
     *
     * @param otherMinuteOfHour  the other minute of hour instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMinuteOfHour is null
     */
    public int compareTo(MinuteOfHour otherMinuteOfHour) {
        int thisValue = this.minuteOfHour;
        int otherValue = otherMinuteOfHour.minuteOfHour;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this minute of hour instance greater than the specified minute of hour.
     *
     * @param otherMinuteOfHour  the other minute of hour instance, not null
     * @return true if this minute of hour is greater
     * @throws NullPointerException if otherMinuteOfHour is null
     */
    public boolean isGreaterThan(MinuteOfHour otherMinuteOfHour) {
        return compareTo(otherMinuteOfHour) > 0;
    }

    /**
     * Is this minute of hour instance less than the specified minute of hour.
     *
     * @param otherMinuteOfHour  the other minute of hour instance, not null
     * @return true if this minute of hour is less
     * @throws NullPointerException if otherMinuteOfHour is null
     */
    public boolean isLessThan(MinuteOfHour otherMinuteOfHour) {
        return compareTo(otherMinuteOfHour) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the minute of hour.
     *
     * @param otherMinuteOfHour  the other minute of hour instance, null returns false
     * @return true if the minute of hour is the same
     */
    @Override
    public boolean equals(Object otherMinuteOfHour) {
        if (this == otherMinuteOfHour) {
            return true;
        }
        if (otherMinuteOfHour instanceof MinuteOfHour) {
            return minuteOfHour == ((MinuteOfHour) otherMinuteOfHour).minuteOfHour;
        }
        return false;
    }

    /**
     * A hashcode for the minute of hour object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return minuteOfHour;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the minute of hour field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("MinuteOfHour", null, null, 0, 59);
        }
    }

}
