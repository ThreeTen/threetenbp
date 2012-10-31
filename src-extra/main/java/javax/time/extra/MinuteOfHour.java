/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.extra;

import static javax.time.calendrical.LocalDateTimeField.MINUTE_OF_HOUR;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.time.DateTimeException;
import javax.time.LocalTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;

/**
 * A representation of a minute-of-hour in the ISO-8601 calendar system.
 * <p>
 * MinuteOfHour is an immutable time field that can only store a minute-of-hour.
 * It is a type-safe way of representing a minute-of-hour in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The minute-of-hour may be queried using getValue().
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class MinuteOfHour
        implements Comparable<MinuteOfHour>, WithAdjuster, Serializable {

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
     * Obtains an instance of {@code MinuteOfHour}.
     *
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return the minute-of-hour, not null
     * @throws DateTimeException if the minute-of-hour is invalid
     */
    public static MinuteOfHour of(int minuteOfHour) {
        try {
            MinuteOfHour result = CACHE.get(minuteOfHour);
            if (result == null) {
                MinuteOfHour temp = new MinuteOfHour(minuteOfHour);
                CACHE.compareAndSet(minuteOfHour, null, temp);
                result = CACHE.get(minuteOfHour);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new DateTimeException("Invalid value for MinuteOfHour: " + minuteOfHour);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code MinuteOfHour} from a date-time object.
     * <p>
     * A {@code DateTimeAccessor} represents some form of date and time information.
     * This factory converts the arbitrary date-time object to an instance of {@code MinuteOfHour}.
     * 
     * @param dateTime  the date-time object to convert, not null
     * @return the minute-of-hour, not null
     * @throws DateTimeException if unable to convert to a {@code MinuteOfHour}
     */
    public static MinuteOfHour from(DateTimeAccessor dateTime) {
        LocalTime time = LocalTime.from(dateTime);
        return MinuteOfHour.of(time.getMinute());
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
        return of(minuteOfHour);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field that defines how the minute-of-hour field operates.
     * <p>
     * The field provides access to the minimum and maximum values, and a
     * generic way to access values within a date-time.
     *
     * @return the minute-of-hour field, never null
     */
    public DateTimeField getField() {
        return LocalDateTimeField.MINUTE_OF_HOUR;
    }

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
     * Adjusts a time to have the minute-of-hour represented by this object,
     * returning a new time.
     * <p>
     * Only the minute-of-hour field is adjusted in the result. The other time
     * fields are unaffected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dateTime  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    @Override
    public DateTime doWithAdjustment(DateTime dateTime) {
        return dateTime.with(MINUTE_OF_HOUR, minuteOfHour);
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
