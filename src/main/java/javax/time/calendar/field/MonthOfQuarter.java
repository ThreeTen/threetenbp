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
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.time.calendar.Calendrical;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.format.FlexiDateTime;

/**
 * A calendrical representation of a month of quarter.
 * <p>
 * MonthOfQuarter is an immutable time field that can only store a month of quarter.
 * It is a type-safe way of representing a month of quarter in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The month of quarter may be queried using getValue().
 * <p>
 * MonthOfQuarter is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class MonthOfQuarter implements Calendrical, Comparable<MonthOfQuarter>, Serializable {

    /**
     * The rule implementation that defines how the month of quarter field operates.
     */
    public static final DateTimeFieldRule RULE = new Rule();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Cache of singleton instances.
     */
    private static final AtomicReferenceArray<MonthOfQuarter> cache = new AtomicReferenceArray<MonthOfQuarter>(4);

    /**
     * The month of quarter being represented.
     */
    private final int monthOfQuarter;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MonthOfQuarter</code>.
     *
     * @param monthOfQuarter  the month of quarter to represent
     * @return the created MonthOfQuarter
     * @throws IllegalCalendarFieldValueException if the monthOfQuarter is invalid
     */
    public static MonthOfQuarter monthOfQuarter(int monthOfQuarter) {
        try {
            MonthOfQuarter result = cache.get(monthOfQuarter);
            if (result == null) {
                MonthOfQuarter temp = new MonthOfQuarter(monthOfQuarter);
                cache.compareAndSet(monthOfQuarter, null, temp);
                result = cache.get(monthOfQuarter);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalCalendarFieldValueException(
                RULE.getName(), monthOfQuarter, RULE.getMinimumValue(), RULE.getMaximumValue());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of quarter.
     *
     * @param monthOfQuarter  the month of quarter to represent
     */
    private MonthOfQuarter(int monthOfQuarter) {
        this.monthOfQuarter = monthOfQuarter;
    }

    /**
     * Resolve the singleton.
     *
     * @return the singleton, never null
     */
    private Object readResolve() {
        return monthOfQuarter(monthOfQuarter);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of quarter value.
     *
     * @return the month of quarter
     */
    public int getValue() {
        return monthOfQuarter;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a <code>FlexiDateTime</code>.
     *
     * @return the flexible date-time representation for this instance, never null
     */
    public FlexiDateTime toFlexiDateTime() {
        return new FlexiDateTime(RULE, getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this month of quarter instance to another.
     *
     * @param otherMonthOfQuarter  the other month of quarter instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMonthOfQuarter is null
     */
    public int compareTo(MonthOfQuarter otherMonthOfQuarter) {
        int thisValue = this.monthOfQuarter;
        int otherValue = otherMonthOfQuarter.monthOfQuarter;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the month of quarter.
     *
     * @param otherMonthOfQuarter  the other month of quarter instance, null returns false
     * @return true if the month of quarter is the same
     */
    @Override
    public boolean equals(Object otherMonthOfQuarter) {
        if (this == otherMonthOfQuarter) {
            return true;
        }
        if (otherMonthOfQuarter instanceof MonthOfQuarter) {
            return monthOfQuarter == ((MonthOfQuarter) otherMonthOfQuarter).monthOfQuarter;
        }
        return false;
    }

    /**
     * A hashcode for the month of quarter object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return monthOfQuarter;
    }

    /**
     * A string describing the month of quarter object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "MonthOfQuarter=" + getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the month of quarter field.
     */
    private static class Rule extends DateTimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("MonthOfQuarter", null, null, 1, 3);
        }

        /** {@inheritDoc} */
        @Override
        public int getValue(FlexiDateTime dateTime) {
            if (dateTime.getDate() != null) {
                return dateTime.getDate().getMonthOfYear().getMonthOfQuarter();
            }
            return dateTime.getFieldValueMapValue(this);
        }
    }

}
