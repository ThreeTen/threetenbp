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

import static javax.time.calendrical.LocalDateTimeField.SECOND_OF_MINUTE;

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
 * A representation of a second-of-minute in the ISO-8601 calendar system.
 * <p>
 * SecondOfMinute is an immutable time field that can only store a second-of-minute.
 * It is a type-safe way of representing a second-of-minute in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The second-of-minute may be queried using getValue().
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class SecondOfMinute
        implements Comparable<SecondOfMinute>, WithAdjuster, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Cache of singleton instances.
     */
    private static final AtomicReferenceArray<SecondOfMinute> CACHE = new AtomicReferenceArray<SecondOfMinute>(60);

    /**
     * The second-of-minute being represented.
     */
    private final int secondOfMinute;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code SecondOfMinute}.
     *
     * @param secondOfMinute  the minute-of-hour to represent, from 0 to 59
     * @return the second-of-minute, not null
     * @throws DateTimeException if the second-of-minute is invalid
     */
    public static SecondOfMinute of(int secondOfMinute) {
        try {
            SecondOfMinute result = CACHE.get(secondOfMinute);
            if (result == null) {
                SecondOfMinute temp = new SecondOfMinute(secondOfMinute);
                CACHE.compareAndSet(secondOfMinute, null, temp);
                result = CACHE.get(secondOfMinute);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new DateTimeException("Invalid value for SecondOfMinute: " + secondOfMinute);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code SecondOfMinute} from a date-time object.
     * <p>
     * A {@code DateTimeAccessor} represents some form of date and time information.
     * This factory converts the arbitrary date-time object to an instance of {@code SecondOfMinute}.
     * 
     * @param dateTime  the date-time object to convert, not null
     * @return the year, not null
     * @throws DateTimeException if unable to convert to a {@code SecondOfMinute}
     */
    public static SecondOfMinute from(DateTimeAccessor dateTime) {
        LocalTime time = LocalTime.from(dateTime);
        return SecondOfMinute.of(time.getSecond());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified second-of-minute.
     *
     * @param secondOfMinute  the second-of-minute to represent
     */
    private SecondOfMinute(int secondOfMinute) {
        this.secondOfMinute = secondOfMinute;
    }

    /**
     * Resolve the singleton.
     *
     * @return the singleton, never null
     */
    private Object readResolve() {
        return of(secondOfMinute);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field that defines how the second-of-minute field operates.
     * <p>
     * The field provides access to the minimum and maximum values, and a
     * generic way to access values within a date-time.
     *
     * @return the second-of-minute field, never null
     */
    public DateTimeField getField() {
        return LocalDateTimeField.SECOND_OF_MINUTE;
    }

    /**
     * Gets the second-of-minute value.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getValue() {
        return secondOfMinute;
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a time to have the second-of-minute represented by this object,
     * returning a new time.
     * <p>
     * Only the second-of-minute field is adjusted in the result. The other time
     * fields are unaffected.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dateTime  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    @Override
    public DateTime doWithAdjustment(DateTime dateTime) {
        return dateTime.with(SECOND_OF_MINUTE, secondOfMinute);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this second-of-minute instance to another.
     *
     * @param otherSecondOfMinute  the other second-of-minute instance, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherSecondOfMinute is null
     */
    public int compareTo(SecondOfMinute otherSecondOfMinute) {
        int thisValue = this.secondOfMinute;
        int otherValue = otherSecondOfMinute.secondOfMinute;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the second-of-minute.
     *
     * @param otherSecondOfMinute  the other second-of-minute instance, null returns false
     * @return true if the second-of-minute is the same
     */
    @Override
    public boolean equals(Object otherSecondOfMinute) {
        return this == otherSecondOfMinute;
    }

    /**
     * A hash code for the second-of-minute object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return secondOfMinute;
    }

    /**
     * A string describing the second-of-minute object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "SecondOfMinute=" + getValue();
    }

}
