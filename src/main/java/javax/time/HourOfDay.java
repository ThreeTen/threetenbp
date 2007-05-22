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
package javax.time;

/**
 * A time field representing a hour of day.
 * <p>
 * HourOfDay is an immutable time field that can only store a hour of day.
 * It is a type-safe way of representing a hour of day in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The hour of day may be queried using getHourOfDay().
 * <p>
 * HourOfDay is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class HourOfDay implements Moment, Comparable<HourOfDay> {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The hour of day being represented.
     */
    private final int hourOfDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>HourOfDay</code>.
     *
     * @param hourOfDay  the hour of day to represent
     */
    public static HourOfDay hourOfDay(int hourOfDay) {
        return new HourOfDay(hourOfDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified hour of day.
     */
    private HourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day value.
     *
     * @return the hour of day
     */
    public int getHourOfDay() {
        return hourOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this hour of day instance to another.
     *
     * @param otherHourOfDay  the other hour of day instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherHourOfDay is null
     */
    public int compareTo(HourOfDay otherHourOfDay) {
        int thisValue = this.hourOfDay;
        int otherValue = otherHourOfDay.hourOfDay;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this hour of day instance greater than the specified hour of day.
     *
     * @param otherHourOfDay  the other hour of day instance, not null
     * @return true if this hour of day is greater
     * @throws NullPointerException if otherHourOfDay is null
     */
    public boolean isGreaterThan(HourOfDay otherHourOfDay) {
        return compareTo(otherHourOfDay) > 0;
    }

    /**
     * Is this hour of day instance less than the specified hour of day.
     *
     * @param otherHourOfDay  the other hour of day instance, not null
     * @return true if this hour of day is less
     * @throws NullPointerException if otherHourOfDay is null
     */
    public boolean isLessThan(HourOfDay otherHourOfDay) {
        return compareTo(otherHourOfDay) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the hour of day.
     *
     * @param otherHourOfDay  the other hour of day instance, null returns false
     * @return true if the hour of day is the same
     */
    public boolean equals(Object otherHourOfDay) {
        if (this == otherHourOfDay) {
            return true;
        }
        if (otherHourOfDay instanceof HourOfDay) {
            return hourOfDay == ((HourOfDay) otherHourOfDay).hourOfDay;
        }
        return false;
    }

    /**
     * A hashcode for the hour of day object.
     *
     * @return a suitable hashcode
     */
    public int hashCode() {
        return hourOfDay;
    }

}
