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
import javax.time.period.Periods;

/**
 * A calendrical representation of a hour of meridiem.
 * <p>
 * HourOfMeridiem is an immutable time field that can only store a hour of meridiem.
 * It is a type-safe way of representing a hour of meridiem in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The hour of meridiem may be queried using getValue().
 * <p>
 * HourOfMeridiem is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class HourOfMeridiem implements Calendrical, Comparable<HourOfMeridiem>, Serializable {

    /**
     * The rule implementation that defines how the hour of meridiem field operates.
     */
    public static final DateTimeFieldRule RULE = Rule.INSTANCE;
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Cache of singleton instances.
     */
    private static final AtomicReferenceArray<HourOfMeridiem> cache = new AtomicReferenceArray<HourOfMeridiem>(12);

    /**
     * The hour of meridiem being represented.
     */
    private final int hourOfMeridiem;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>HourOfMeridiem</code>.
     *
     * @param hourOfMeridiem  the hour of meridiem to represent, from 0 to 11
     * @return the created HourOfMeridiem
     * @throws IllegalCalendarFieldValueException if the hourOfMeridiem is invalid
     */
    public static HourOfMeridiem hourOfMeridiem(int hourOfMeridiem) {
        try {
            HourOfMeridiem result = cache.get(hourOfMeridiem);
            if (result == null) {
                HourOfMeridiem temp = new HourOfMeridiem(hourOfMeridiem);
                cache.compareAndSet(hourOfMeridiem, null, temp);
                result = cache.get(hourOfMeridiem);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalCalendarFieldValueException(
                RULE.getName(), hourOfMeridiem, RULE.getMinimumValue(), RULE.getMaximumValue());
        }
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

    /**
     * Resolve the singleton.
     *
     * @return the singleton, never null
     */
    private Object readResolve() {
        return hourOfMeridiem(hourOfMeridiem);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of meridiem value.
     *
     * @return the hour of meridiem, from 0 to 11
     */
    public int getValue() {
        return hourOfMeridiem;
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

    /**
     * A string describing the hour of meridiem object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "HourOfMeridiem=" + getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the hour of meridiem field.
     */
    private static class Rule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new Rule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private Rule() {
            super("HourOfMeridiem", Periods.HOURS, Periods.DAYS, 0, 11);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public int getValue(FlexiDateTime dateTime) {
            if (dateTime.getTime() != null) {
                return dateTime.getTime().getHourOfDay().getHourOfAmPm();
            }
            return dateTime.getFieldValueMapValue(this);
        }
    }

}
