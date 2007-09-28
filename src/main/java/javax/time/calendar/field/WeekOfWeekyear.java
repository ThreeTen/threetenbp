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
 * A time field representing a week of week-based year.
 * <p>
 * WeekOfWeekyear is an immutable time field that can only store a week of week-based year.
 * It is a type-safe way of representing a week of week-based year in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The week of week-based year may be queried using getWeekOfWeekyear().
 * <p>
 * WeekOfWeekyear is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class WeekOfWeekyear implements Calendrical, Comparable<WeekOfWeekyear>, Serializable {

    /**
     * The rule implementation that defines how the week of week-based year field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The week of week-based year being represented.
     */
    private final int weekOfWeekyear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>WeekOfWeekyear</code>.
     *
     * @param weekOfWeekyear  the week of week-based year to represent
     * @return the created WeekOfWeekyear
     */
    public static WeekOfWeekyear weekOfWeekyear(int weekOfWeekyear) {
        return new WeekOfWeekyear(weekOfWeekyear);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified week of week-based year.
     *
     * @param weekOfWeekyear  the week of week-based year to represent
     */
    private WeekOfWeekyear(int weekOfWeekyear) {
        this.weekOfWeekyear = weekOfWeekyear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the week of week-based year value.
     *
     * @return the week of week-based year
     */
    public int getWeekOfWeekyear() {
        return weekOfWeekyear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * WeekOfWeekyear instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this week of week-based year instance to another.
     *
     * @param otherWeekOfWeekyear  the other week of week-based year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherWeekOfWeekyear is null
     */
    public int compareTo(WeekOfWeekyear otherWeekOfWeekyear) {
        int thisValue = this.weekOfWeekyear;
        int otherValue = otherWeekOfWeekyear.weekOfWeekyear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this week of week-based year instance greater than the specified week of week-based year.
     *
     * @param otherWeekOfWeekyear  the other week of week-based year instance, not null
     * @return true if this week of week-based year is greater
     * @throws NullPointerException if otherWeekOfWeekyear is null
     */
    public boolean isGreaterThan(WeekOfWeekyear otherWeekOfWeekyear) {
        return compareTo(otherWeekOfWeekyear) > 0;
    }

    /**
     * Is this week of week-based year instance less than the specified week of week-based year.
     *
     * @param otherWeekOfWeekyear  the other week of week-based year instance, not null
     * @return true if this week of week-based year is less
     * @throws NullPointerException if otherWeekOfWeekyear is null
     */
    public boolean isLessThan(WeekOfWeekyear otherWeekOfWeekyear) {
        return compareTo(otherWeekOfWeekyear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the week of week-based year.
     *
     * @param otherWeekOfWeekyear  the other week of week-based year instance, null returns false
     * @return true if the week of week-based year is the same
     */
    @Override
    public boolean equals(Object otherWeekOfWeekyear) {
        if (this == otherWeekOfWeekyear) {
            return true;
        }
        if (otherWeekOfWeekyear instanceof WeekOfWeekyear) {
            return weekOfWeekyear == ((WeekOfWeekyear) otherWeekOfWeekyear).weekOfWeekyear;
        }
        return false;
    }

    /**
     * A hashcode for the week of week-based year object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return weekOfWeekyear;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the week of week-based year field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("WeekOfWeekyear", null, null, 1, 53);
        }
    }

}
