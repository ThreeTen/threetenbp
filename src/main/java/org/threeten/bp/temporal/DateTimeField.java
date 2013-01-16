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

import java.util.Comparator;

import org.threeten.bp.DateTimeException;

/**
 * A field of date-time, such as month-of-year or hour-of-minute.
 * <p>
 * Date and time is expressed using fields which partition the time-line into something
 * meaningful for humans. Implementations of this interface represent those fields.
 * <p>
 * The most commonly used units are defined in {@link ChronoField}.
 * Additional fields can be written by application code by implementing this interface.
 * <p>
 * The field works using double dispatch. Client code calls methods on a date-time like
 * {@code LocalDateTime} which check if the field is a {@code ChronoField}.
 * If it is, then the date-time must handle it.
 * Otherwise, the method call is re-dispatched to the matching method in this interface.
 *
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * It is recommended to use an enum where possible.
 */
public interface DateTimeField extends Comparator<DateTimeAccessor> {

    /**
     * Gets a descriptive name for the field.
     * <p>
     * The should be of the format 'BaseOfRange', such as 'MonthOfYear',
     * unless the field is unbounded, such as 'Year' or 'Era', when only
     * the base unit is mentioned.
     *
     * @return the name, not null
     */
    String getName();

    /**
     * Gets the unit that the field is measured in.
     * <p>
     * The unit of the field is the period that varies within the range.
     * For example, in the field 'MonthOfYear', the unit is 'Months'.
     * See also {@link #getRangeUnit()}.
     *
     * @return the period unit defining the base unit of the field, not null
     */
    PeriodUnit getBaseUnit();

    /**
     * Gets the range that the field is bound by.
     * <p>
     * The range of the field is the period that the field varies within.
     * For example, in the field 'MonthOfYear', the range is 'Years'.
     * See also {@link #getBaseUnit()}.
     * <p>
     * The range is never null. For example, the 'Year' field is shorthand for
     * 'YearOfForever'. It therefore has a unit of 'Years' and a range of 'Forever'.
     *
     * @return the period unit defining the range of the field, not null
     */
    PeriodUnit getRangeUnit();

    /**
     * Compares the value of this field in two date-time objects.
     * <p>
     * All fields implement {@link Comparator} on {@link DateTimeAccessor}.
     * This allows a list of date-times to be compared using the value of a field.
     * For example, you could sort a list of arbitrary date-time objects by the value of
     * the month-of-year field - {@code Collections.sort(list, MONTH_OF_YEAR)}
     *
     * @param dateTime1  the first date-time object to compare, not null
     * @param dateTime2  the second date-time object to compare, not null
     * @throws DateTimeException if unable to obtain the value for this field
     */
    int compare(DateTimeAccessor dateTime1, DateTimeAccessor dateTime2);  // JAVA8 default method

    //-----------------------------------------------------------------------
    /**
     * Gets the range of valid values for the field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * This method is generally only applicable to the ISO-8601 calendar system.
     * <p>
     * Note that the result only describes the minimum and maximum valid values
     * and it is important not to read too much into them. For example, there
     * could be values within the range that are invalid for the field.
     *
     * @return the range of valid values for the field, not null
     */
    DateTimeValueRange range();

    /**
     * Implementation of the logic to check if this field is supported by the accessor.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use {@link DateTimeAccessor#isSupported(DateTimeField)} on the date-time
     * object passing this as the argument.
     * <pre>
     *   boolean supported = date.isSupported(field);
     * </pre>
     * <p>
     * Implementations should be written using the fields available in {@link ChronoField}.
     *
     * @param dateTime  the date-time object to query, not null
     * @return true if the date-time can be queried for this field, false if not
     */
    boolean doIsSupported(DateTimeAccessor dateTime);

    /**
     * Implementation of the logic to get the range of valid values for this field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * <p>
     * Note that the result only describes the minimum and maximum valid values
     * and it is important not to read too much into them. For example, there
     * could be values within the range that are invalid for the field.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use {@link DateTimeAccessor#range(DateTimeField)} on the date-time
     * object passing this as the argument.
     * <pre>
     *   DateTimeValueRange range = date.range(field);
     * </pre>
     * <p>
     * Implementations should be written using the fields available in {@link ChronoField}.
     *
     * @param dateTime  the date-time object used to refine the result, not null
     * @return the range of valid values for this field, not null
     */
    DateTimeValueRange doRange(DateTimeAccessor dateTime);

    /**
     * Implementation of the logic to get the value of this field.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use {@link DateTimeAccessor#get(DateTimeField)} or
     * {@link DateTimeAccessor#getLong(DateTimeField)} on the date-time
     * object passing this as the argument.
     * <pre>
     *   long value = date.get(field);
     * </pre>
     * <p>
     * The value of the associated field is expressed as a {@code long} integer
     * and is extracted from the specified date-time object.
     * <p>
     * Implementations should be written using the fields available in {@link ChronoField}.
     *
     * @param dateTime  the date-time object to query, not null
     * @return the value of this field, not null
     * @throws DateTimeException if unable to get the field
     */
    long doGet(DateTimeAccessor dateTime);

    /**
     * Implementation of the logic to set the value of this field.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use {@link DateTime#with(DateTimeField, long)} on the date-time
     * object passing this as the argument.
     * <pre>
     *   updated = date.with(field, newValue);
     * </pre>
     * <p>
     * The new value of the field is expressed as a {@code long} integer.
     * The result will be adjusted to set the value of the field.
     * <p>
     * Implementations should be written using the fields available in {@link ChronoField}.
     *
     * @param dateTime the date-time object to adjust, not null
     * @param newValue the new value of the field
     * @return the adjusted date-time object, not null
     * @throws DateTimeException if the value is invalid
     */
    <R extends DateTime> R doWith(R dateTime, long newValue);

    /**
     * Resolves the date/time information in the builder
     * <p>
     * This method is invoked during the resolve of the builder.
     * Implementations should combine the associated field with others to form
     * objects like {@code LocalDate}, {@code LocalTime} and {@code LocalDateTime}
     *
     * @param builder  the builder to resolve, not null
     * @param value  the value of the associated field
     * @return true if builder has been changed, false otherwise
     * @throws DateTimeException if unable to resolve
     */
    boolean resolve(DateTimeBuilder builder, long value);

}
