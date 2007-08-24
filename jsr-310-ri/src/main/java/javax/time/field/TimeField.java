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
 * A specific field of time value, such as where DayOfMonth is 2 or DayOfWeek
 * is Wednesday.
 * <p>
 * A <code>TimeField</code> is used to store the value of a field of time
 * measured in terms of a human scale time part, such as years, months, days,
 * hours, minutes or seconds. An example is the DayOfWeek of Thursday, or the
 * HourOfMinute of 13.
 * <p>
 * TimeField is an abstract class and must be subclassed with care to ensure
 * other classes in the framework operate correctly.
 * All subclasses must be final, immutable and thread-safe.
 *
 * @param <U> the time part that the amount is measured in
 * @author Stephen Colebourne
 */


/**
 * A specific field of time value, such as where DayOfMonth is 2 or DayOfWeek
 * is Wednesday.
 * <p>
 * TimeField is an interface and must be implemented with care to ensure
 * other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @param <U> the unit time part
 * @param <R> the range time part
 * @author Stephen Colebourne
 */
public interface TimeField<U extends TimePart, R extends TimePart>
        extends TimeView<U, R> {

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the time field.
     * <p>
     * This method returns the time field value being represented.
     * For example, if this object is the field where DayOfMonth is 2 then
     * this method will return 2.
     *
     * @return the time field value, may be negative
     */
    int getValue();

    /**
     * Returns a new instance, of the same field type, with the specified value.
     * <p>
     * This method returns a new object with the value requested, assuming that
     * value is valid.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param newValue  the new value to store, must be valid
     * @return a new instance with the same type and new value, never null
     * @throws IllegalArgumentException if the new value is invalid
     */
    TimeField<U, R> withValue(int newValue);

    //-----------------------------------------------------------------------
    /**
     * Gets the type representing the time part stored in this object.
     *
     * @return the time field type, never null
     */
    TimeFieldType<U, R> getType();

}
