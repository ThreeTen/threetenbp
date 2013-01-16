/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.temporal;

import org.threeten.bp.DateTimeException;

/**
 * General low-level access to a date and/or time object.
 * <p>
 * This interface is implemented by all date-time classes.
 * It provides access to the state using the {@link #get(TemporalField)} and
 * {@link #getLong(TemporalField)} methods that takes a {@link TemporalField}.
 * Access is also provided to any additional state using a query interface
 * through {@link #query(TemporalQuery)}.
 * This provides access to the time-zone, precision and calendar system.
 * <p>
 * A sub-interface, {@link Temporal}, extends this definition to one that also
 * supports adjustment and manipulation on more complete date-time objects.
 *
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 * See {@code DateTime} for a full description of whether to implement this
 * interface.
 */
public interface TemporalAccessor {

    /**
     * Checks if the specified date-time field is supported.
     * <p>
     * This checks if the date-time can be queried for the specified field.
     * If false, then calling the {@link #range(TemporalField) range} and {@link #get(TemporalField) get}
     * methods will throw an exception.
     *
     * <h5>Implementation notes</h5>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link TemporalField#doRange(TemporalAccessor) doRange method} on the specified field.
     *
     * @param field  the field to check, null returns false
     * @return true if this date-time can be queried for the field, false if not
     */
    boolean isSupported(TemporalField field);

    /**
     * Gets the range of valid values for the specified date-time field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * If the date-time cannot return the range, because the field is unsupported or for
     * some other reason, an exception will be thrown.
     * <p>
     * Note that the result only describes the minimum and maximum valid values
     * and it is important not to read too much into them. For example, there
     * could be values within the range that are invalid for the field.
     *
     * <h5>Implementation notes</h5>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link TemporalField#doRange(TemporalAccessor) doRange method} on the specified field.
     *
     * @param field  the field to get, not null
     * @return the range of valid values for the field, not null
     * @throws DateTimeException if the range for the field cannot be obtained
     */
    ValueRange range(TemporalField field);

    /**
     * Gets the value of the specified date-time field as an {@code int}.
     * <p>
     * This queries the date-time for the value for the specified field.
     * The returned value will always be within the valid range of values for the field.
     * If the date-time cannot return the value, because the field is unsupported or for
     * some other reason, an exception will be thrown.
     *
     * <h5>Implementation notes</h5>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link TemporalField#doGet(TemporalAccessor) doGet method} on the specified field.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws DateTimeException if a value for the field cannot be obtained
     * @throws DateTimeException if the range of valid values for the field exceeds an {@code int}
     * @throws DateTimeException if the value is outside the range of valid values for the field
     * @throws ArithmeticException if numeric overflow occurs
     */
    int get(TemporalField field);

    /**
     * Gets the value of the specified date-time field as a {@code Long}.
     * <p>
     * This queries the date-time for the value for the specified field.
     * The returned value may be outside the valid range of values for the field.
     * If the date-time cannot return the value, because the field is unsupported or for
     * some other reason, an exception will be thrown.
     *
     * <h5>Implementation notes</h5>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link TemporalField#doGet(TemporalAccessor) doGet method} on the specified field.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws DateTimeException if a value for the field cannot be obtained
     * @throws ArithmeticException if numeric overflow occurs
     */
    long getLong(TemporalField field);

    /**
     * Queries this date-time.
     * <p>
     * This queries this date-time using the specified query strategy object.
     * The Query interface has three special predefined constants -
     * {@code Query.ZONE_ID}, {@code Query.CHRONO} and {@code Query.TIME_PRECISION}.
     * Other queries may be defined by applications.
     *
     * <h5>Implementation notes</h5>
     * Queries are used for two purposes - general application specific logic,
     * and providing a way to query those parts of a {@code DateTimeAccessor}
     * that cannot be returned as a {@code long} using a field.
     * <p>
     * In use, there is no difference between the two purposes.
     * However, there is a difference in implementation.
     * It is the responsibility of implementations of this method to return a
     * value for the three special constants if applicable.
     * Future JDKs are permitted to add further special constants.
     * <p>
     * The standard implementation of this method will be similar to the following:
     * <pre>
     *   public &lt;R&gt; R query(TemporalQuery&lt;R&gt; type) {
     *     // only include an if statement if the implementation can return it
     *     if (query == TemporalQuery.ZONE_ID)  return // the ZoneId
     *     if (query == TemporalQuery.CHRONO)  return // the Chrono
     *     if (query == TemporalQuery.PRECISION)  return // the precision
     *     // call default method
     *     return super.query(query);
     *   }
     * </pre>
     * If the implementation class has no zone, chronology or precision, then
     * the class can rely totally on the default implementation.
     *
     * @param <R> the type of the result
     * @param query  the query to invoke, not null
     * @return the query result, null may be returned (defined by the query)
     */
    <R> R query(TemporalQuery<R> query);

}
