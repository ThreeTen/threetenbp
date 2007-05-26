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
 * A time field representing a day of year.
 * <p>
 * DayOfYear is an immutable time field that can only store a day of year.
 * It is a type-safe way of representing a day of year in an application.
 * <p>
 * Static factory methods allow you to constuct instances.
 * The day of year may be queried using getDayOfYear().
 * <p>
 * DayOfYear is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class DayOfYear implements Moment, Comparable<DayOfYear> {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The day of year being represented.
     */
    private final int dayOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>DayOfYear</code>.
     *
     * @param dayOfYear  the day of year to represent
     * @return the created DayOfYear
     */
    public static DayOfYear dayOfYear(int dayOfYear) {
        return new DayOfYear(dayOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified day of year.
     *
     * @param dayOfYear  the day of year to represent
     */
    private DayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day of year value.
     *
     * @return the day of year
     */
    public int getDayOfYear() {
        return dayOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this day of year instance to another.
     *
     * @param otherDayOfYear  the other day of year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDayOfYear is null
     */
    public int compareTo(DayOfYear otherDayOfYear) {
        int thisValue = this.dayOfYear;
        int otherValue = otherDayOfYear.dayOfYear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this day of year instance greater than the specified day of year.
     *
     * @param otherDayOfYear  the other day of year instance, not null
     * @return true if this day of year is greater
     * @throws NullPointerException if otherDayOfYear is null
     */
    public boolean isGreaterThan(DayOfYear otherDayOfYear) {
        return compareTo(otherDayOfYear) > 0;
    }

    /**
     * Is this day of year instance less than the specified day of year.
     *
     * @param otherDayOfYear  the other day of year instance, not null
     * @return true if this day of year is less
     * @throws NullPointerException if otherDayOfYear is null
     */
    public boolean isLessThan(DayOfYear otherDayOfYear) {
        return compareTo(otherDayOfYear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the day of year.
     *
     * @param otherDayOfYear  the other day of year instance, null returns false
     * @return true if the day of year is the same
     */
    public boolean equals(Object otherDayOfYear) {
        if (this == otherDayOfYear) {
            return true;
        }
        if (otherDayOfYear instanceof DayOfYear) {
            return dayOfYear == ((DayOfYear) otherDayOfYear).dayOfYear;
        }
        return false;
    }

    /**
     * A hashcode for the day of year object.
     *
     * @return a suitable hashcode
     */
    public int hashCode() {
        return dayOfYear;
    }

}
