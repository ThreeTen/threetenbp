/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendrical;

import javax.time.DateTimeException;

/**
 * A date and/or time object.
 * <p>
 * This interface is implemented by all date-time classes.
 * It provides access to the state using the {@link #get(DateTimeField)} method that takes
 * a {@link DateTimeField}. Access is also provided to any additional state using a
 * simple lookup by {@code Class} through {@link #extract(Class)}. This is primarily
 * intended to provide access to the time-zone, offset and calendar system.
 * <p>
 * A sub-interface, {@link DateTime}, extends this definition to one that also
 * supports addition and subtraction of periods.
 * 
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 * See {@code DateTime} for a full description of whether to implement this
 * interface.
 */
public interface DateTimeAccessor {

    /**
     * Gets the range of valid values for the specified date-time field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * <p>
     * Note that the result only describes the minimum and maximum valid values
     * and it is important not to read too much into them. For example, there
     * could be values within the range that are invalid for the field.
     * <p>
     * This method will return a result whether or not the implementation supports the field.
     * 
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link LocalDateTimeField} before
     * delegating on to the {@link DateTimeField#doRange(DateTimeAccessor) doRange method} on the specified field.
     *
     * @param field  the field to get, not null
     * @return the range of valid values for the field, not null
     */
    DateTimeValueRange range(DateTimeField field);
    // JAVA8
    // default {
    //     if (field instanceof LocalDateTimeField) {
    //         return field.range();
    //     }
    //     return field.doRange(this);
    // }

    /**
     * Gets the value of the specified date-time field.
     * <p>
     * This queries the date-time for the value for the specified field.
     * If the date-time cannot return the value, it will throw an exception.
     * 
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link LocalDateTimeField} before
     * delegating on to the {@link DateTimeField#doGet(DateTimeAccessor) doGet method} on the specified field.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws DateTimeException if a value for the field cannot be obtained
     * @throws ArithmeticException if numeric overflow occurs
     */
    long get(DateTimeField field);

    /**
     * Returns an object of the same type as this object with the specified field altered.
     * <p>
     * This returns a new object based on this one with the value for the specified field changed.
     * For example, on a {@code LocalDate}, this could be used to set the year, month or day-of-month.
     * The returned object will have the same observable type as this object.
     * <p>
     * In some cases, changing a field is not fully defined. For example, if the target object is
     * a date representing the 31st January, then changing the month to February would be unclear.
     * In cases like this, the field is responsible for resolving the result. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * 
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link LocalDateTimeField} before
     * delegating on to the {@link DateTimeField#doSet(DateTimeAccessor, long) doSet method} on the specified field.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original and return it.
     *
     * @param field  the field to set in the returned date, not null
     * @param newValue  the new value of the field in the returned date, not null
     * @return an object of the same type with the specified field set, not null
     * @throws DateTimeException if the field cannot be set
     * @throws ArithmeticException if numeric overflow occurs
     */
    DateTimeAccessor with(DateTimeField field, long newValue);

    /**
     * Extracts an instance of the specified type.
     * <p>
     * This queries the date-time for an object that matches the requested type.
     * A selection of types, listed below, must be returned if they are available.
     * This is of most use to obtain the time-zone, offset and calendar system where the
     * type of the object is only defined as this interface.
     * 
     * <h4>Implementation notes</h4>
     * An implementation must return the following types if it contains sufficient information:
     * <ul>
     * <li>LocalDate
     * <li>LocalTime
     * <li>ZoneOffset
     * <li>ZoneId
     * <li>Chronology
     * </ul>
     * Other objects may be returned if appropriate.
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract an object of the requested type
     */
    <R> R extract(Class<R> type);

}
