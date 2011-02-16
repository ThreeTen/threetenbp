/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_HOUR;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMatcher;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateTimeField;
import javax.time.calendar.DateTimeRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeAdjuster;
import javax.time.calendar.UnsupportedRuleException;

/**
 * A representation of a minute-of-hour in the ISO-8601 calendar system.
 * <p>
 * MinuteOfHour is an immutable time field that can only store a minute-of-hour.
 * It is a type-safe way of representing a minute-of-hour in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The minute-of-hour may be queried using getValue().
 * <p>
 * MinuteOfHour is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class MinuteOfHour
        implements Calendrical, Comparable<MinuteOfHour>, TimeAdjuster, CalendricalMatcher, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Cache of singleton instances.
     */
    private static final AtomicReferenceArray<MinuteOfHour> CACHE = new AtomicReferenceArray<MinuteOfHour>(60);

    /**
     * The minute-of-hour being represented.
     */
    private final int minuteOfHour;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the minute-of-hour field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the minute-of-hour rule, never null
     */
    public static DateTimeRule rule() {
        return MINUTE_OF_HOUR;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MinuteOfHour</code>.
     *
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return the created MinuteOfHour
     * @throws IllegalCalendarFieldValueException if the minuteOfHour is invalid
     */
    public static MinuteOfHour minuteOfHour(int minuteOfHour) {
        try {
            MinuteOfHour result = CACHE.get(minuteOfHour);
            if (result == null) {
                MinuteOfHour temp = new MinuteOfHour(minuteOfHour);
                CACHE.compareAndSet(minuteOfHour, null, temp);
                result = CACHE.get(minuteOfHour);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalCalendarFieldValueException(
                rule(), minuteOfHour, rule().getMinimumValue(), rule().getMaximumValue());
        }
    }

    /**
     * Obtains an instance of <code>MinuteOfHour</code> from a calendrical.
     * <p>
     * This can be used extract the minute-of-hour value directly from any implementation
     * of <code>Calendrical</code>, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the MinuteOfHour instance, never null
     * @throws UnsupportedRuleException if the minute-of-hour cannot be obtained
     */
    public static MinuteOfHour minuteOfHour(Calendrical calendrical) {
        return minuteOfHour(rule().getValueChecked(calendrical).getValidIntValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified minute-of-hour.
     *
     * @param minuteOfHour  the minute-of-hour to represent
     */
    private MinuteOfHour(int minuteOfHour) {
        this.minuteOfHour = minuteOfHour;
    }

    /**
     * Resolve the singleton.
     *
     * @return the singleton, never null
     */
    private Object readResolve() {
        return minuteOfHour(minuteOfHour);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this instance then
     * <code>null</code> will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, rule().field(minuteOfHour), this, ISOChronology.INSTANCE);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the minute-of-hour value.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getValue() {
        return minuteOfHour;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the minute-of-hour extracted from the calendrical matches this.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        DateTimeField calValue = calendrical.get(rule());
        return calValue != null && calValue.getValue() == getValue();
    }

    /**
     * Adjusts a time to have the the minute-of-hour represented by this object,
     * returning a new time.
     * <p>
     * Only the minute-of-hour field is adjusted in the result. The other time
     * fields are unaffected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    public LocalTime adjustTime(LocalTime time) {
        return time.withMinuteOfHour(minuteOfHour);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this minute-of-hour instance to another.
     *
     * @param otherMinuteOfHour  the other minute-of-hour instance, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherMinuteOfHour is null
     */
    public int compareTo(MinuteOfHour otherMinuteOfHour) {
        int thisValue = this.minuteOfHour;
        int otherValue = otherMinuteOfHour.minuteOfHour;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the minute-of-hour.
     *
     * @param otherMinuteOfHour  the other minute-of-hour instance, null returns false
     * @return true if the minute-of-hour is the same
     */
    @Override
    public boolean equals(Object otherMinuteOfHour) {
        return this == otherMinuteOfHour;
    }

    /**
     * A hash code for the minute-of-hour object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return minuteOfHour;
    }

    /**
     * A string describing the minute-of-hour object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "MinuteOfHour=" + getValue();
    }

}
