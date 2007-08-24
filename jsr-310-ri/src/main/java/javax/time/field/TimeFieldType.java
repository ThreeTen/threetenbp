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
package javax.time.field;

import javax.time.part.TimePart;

/**
 * The type of a measurable field of time, such as DayOfWeek or MinuteOfHour.
 * <p>
 * TimeFieldType is an abstract class and must be subclassed with care to ensure
 * other classes in the framework operate correctly. All subclasses must be
 * final, immutable, thread-safe and be singletons.
 *
 * @param <U> the time part for the unit
 * @param <R> the time part for the range
 * @author Stephen Colebourne
 */
public abstract class TimeFieldType<U extends TimePart, R extends TimePart> {

    /**
     * Constructor.
     */
    protected TimeFieldType() {
        super();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unit time part, which the element which alters within the range.
     * <p>
     * In the phrase 'hour of day', the unit is the hour.
     *
     * @return the time part for the unit, never null
     */
    public abstract U getUnitPart();

    /**
     * Gets the range time part, which the field is bound by.
     * <p>
     * In the phrase 'hour of day', the range is the day.
     *
     * @return the time part, never null
     */
    public abstract R getRangePart();

    /**
     * Gets the name of the time field type.
     * <p>
     * Subclasses should use the form 'UnitOfRange' whenever possible.
     *
     * @return the name of the time field type, never null
     */
    public abstract String getName();

    /**
     * Creates a new instance of the associated time amount using the
     * specified value.
     *
     * @param value  the value of the field to represent
     * @return the time amount, never null
     */
    public abstract TimeField<U, R> createInstance(int value);

    //-----------------------------------------------------------------------
    /**
     * Compares this TimeFieldType to another based on the unit duration
     * followed by the range duration.
     * <p>
     * The unit duration is compared first, so MinuteOfHour will be less than
     * HourOfDay, which will be less than DayOfWeek. When the unit duration is
     * the same, the range duration is compared, so DayOfWeek is less than
     * DayOfMonth, which is less than DayOfYear.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, postive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(TimeFieldType<U, R> other) {
        int cmp = this.getUnitPart().compareTo(other.getUnitPart());
        if (cmp != 0) {
            return cmp;
        }
        return this.getRangePart().compareTo(other.getRangePart());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the time amount type.
     *
     * @return a description of the amount of time
     */
    @Override
    public String toString() {
        return getName();
    }

}
