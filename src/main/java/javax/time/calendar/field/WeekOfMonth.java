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
 * A time field representing a week of month.
 * <p>
 * WeekOfMonth is an immutable time field that can only store a week of month.
 * It is a type-safe way of representing a week of month in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The week of month may be queried using getWeekOfMonth().
 * <p>
 * WeekOfMonth is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class WeekOfMonth implements Calendrical, Comparable<WeekOfMonth>, Serializable {

    /**
     * The rule implementation that defines how the week of month field operates.
     */
    public static final TimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The week of month being represented.
     */
    private final int weekOfMonth;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>WeekOfMonth</code>.
     *
     * @param weekOfMonth  the week of month to represent
     * @return the created WeekOfMonth
     */
    public static WeekOfMonth weekOfMonth(int weekOfMonth) {
        return new WeekOfMonth(weekOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified week of month.
     *
     * @param weekOfMonth  the week of month to represent
     */
    private WeekOfMonth(int weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the week of month value.
     *
     * @return the week of month
     */
    public int getWeekOfMonth() {
        return weekOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * WeekOfMonth instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this week of month instance to another.
     *
     * @param otherWeekOfMonth  the other week of month instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherWeekOfMonth is null
     */
    public int compareTo(WeekOfMonth otherWeekOfMonth) {
        int thisValue = this.weekOfMonth;
        int otherValue = otherWeekOfMonth.weekOfMonth;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this week of month instance greater than the specified week of month.
     *
     * @param otherWeekOfMonth  the other week of month instance, not null
     * @return true if this week of month is greater
     * @throws NullPointerException if otherWeekOfMonth is null
     */
    public boolean isGreaterThan(WeekOfMonth otherWeekOfMonth) {
        return compareTo(otherWeekOfMonth) > 0;
    }

    /**
     * Is this week of month instance less than the specified week of month.
     *
     * @param otherWeekOfMonth  the other week of month instance, not null
     * @return true if this week of month is less
     * @throws NullPointerException if otherWeekOfMonth is null
     */
    public boolean isLessThan(WeekOfMonth otherWeekOfMonth) {
        return compareTo(otherWeekOfMonth) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the week of month.
     *
     * @param otherWeekOfMonth  the other week of month instance, null returns false
     * @return true if the week of month is the same
     */
    @Override
    public boolean equals(Object otherWeekOfMonth) {
        if (this == otherWeekOfMonth) {
            return true;
        }
        if (otherWeekOfMonth instanceof WeekOfMonth) {
            return weekOfMonth == ((WeekOfMonth) otherWeekOfMonth).weekOfMonth;
        }
        return false;
    }

    /**
     * A hashcode for the week of month object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return weekOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the week of month field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("WeekOfMonth", null, null, 1, 5);
        }
    }

}
