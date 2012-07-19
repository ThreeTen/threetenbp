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

import java.util.Comparator;

import javax.time.CalendricalException;

/**
 * A field of date/time.
 * <p>
 * A date, as expressed by {@link javax.time.LocalDateTime}, is broken down into a number of fields,
 * such as year, month, day-of-month, hour, minute and second.
 * Implementations of this interface represent those fields.
 * The fields include their own calculations which are specific to one calendar system.
 * 
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * It is recommended to use an enum where possible.
 */
public interface DateTimeField extends Comparator<DateTime> {

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
     * Compares the value of this field in two calendricals.
     * <p>
     * All fields implement {@link Comparator} on {@link DateTime}.
     * This allows a list of calendricals to be compared using the value of a field.
     * For example, you could sort a list of arbitrary calendricals by the value of
     * the month-of-year field - {@code Collections.sort(list, MONTH_OF_YEAR)}
     *
     * @param calendrical1  the first calendrical to compare, not null
     * @param calendrical2  the second calendrical to compare, not null
     * @throws CalendricalException if unable to obtain the value for this field
     */
    int compare(DateTime calendrical1, DateTime calendrical2);  // JAVA8 default method

    //-----------------------------------------------------------------------
    /**
     * Gets the range of valid values for the field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * <p>
     * Note that the result only describes the minimum and maximum valid values
     * and it is important not to read too much into them. For example, there
     * could be values within the range that are invalid for the field.
     * 
     * @return the range of valid values for the field, not null
     */
    DateTimeValueRange range();

    /**
     * Gets the range of valid values for the associated field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * <p>
     * The date-time object is used to provide context to refine the valid value range.
     * 
     * @param dateTime  the context date-time object, not null
     * @return the range of valid values for the associated field, not null
     */
    DateTimeValueRange range(DateTime dateTime);

    /**
     * Implementation of the logic to get the value of the associated field.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use {@link DateTime#get(DateTimeField)} on the date-time
     * object passing this as the argument.
     * <pre>
     *   long value = date.get(field);
     * </pre>
     * <p>
     * The value of the associated field is expressed as a {@code long} integer
     * and is extracted from the specified date-time object.
     * Implementations must be written using the fields available in {@link LocalDateTimeField}.
     *
     * @param calendrical  the calendrical object, not null
     * @return the value of the associated field, not null
     * @throws CalendricalException if unable to get the field
     */
    long doGet(DateTime calendrical);

    /**
     * Implementation of the logic to set the value of the associated field.
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
     * Implementations must be written using the fields available in {@link LocalDateTimeField}.
     *
     * @param calendrical the date-time object to adjust, not null
     * @param newValue the new value of the field
     * @return the adjusted date-time object, not null
     * @throws CalendricalException if the value is invalid
     */
    <R extends DateTime> R doSet(R calendrical, long newValue);

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
     * @throws CalendricalException if unable to resolve
     */
    boolean resolve(DateTimeBuilder builder, long value);

}
