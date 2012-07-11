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

import java.util.List;

import javax.time.CalendricalException;

/**
 * A calendrical that can provide information on the fields of date and time.
 * <p>
 * Date and time fields are represented by {@link DateTimeField}, such as year,
 * month-of-year, day-of-month, hour-of-day and minute-of-hour.
 * This interface is implemented by those classes that allow the fields to be
 * read and altered. See also {@link DateTimeObject}, which extends the semantics
 * of this interface further.
 * 
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 */
public interface DateTimeCalendrical {

    /**
     * Gets a list of fields that fully represents the date-time represented.
     * <p>
     * This returns a list of fields that defines the date-time.
     * The list must be ordered from largest to smallest whenever possible for at
     * least those fields in {@link LocalDateTimeField}.
     * <p>
     * There may be multiple sets of fields that represent the date-time.
     * For example, a date might be represented by epoch-day, year/month/day, or
     * many other choices. The option chosen should be the most common representation
     * used by developers, year/month/day in this case. Preference should also
     * be given to the fields in {@code LocalDateTimeField}.
     * <p>
     * Implementations that always return the same result from this method should
     * store and return a single instance from a static variable.
     * 
     * @return the immutable list of fields, not null
     */
    List<DateTimeField> fieldList();

    /**
     * Gets the value of the specified date-time field.
     * <p>
     * This allows a single field of date-time to be queried.
     * All fields returned in {@link #fieldList()} will return a value.
     * Other fields will return a value if they can be calculated, or an exception if not.
     * <p>
     * Implementations must check and return any fields defined in {@code LocalDateTimeField}
     * before {@link DateTimeField#get(DateTimeCalendrical) delegating on} to the the specified field.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws CalendricalException if a value for the field cannot be obtained
     */
    long get(DateTimeField field);

    /**
     * Returns an object of the same type as this object with the specified field altered.
     * <p>
     * This method returns a new object based on this one with the value for the specified field changed.
     * For example, on a {@code LocalDate}, this could be used to set the year, month of day-of-month.
     * The returned object will have the same observable type as this object.
     * <p>
     * In some cases, changing a field is not fully defined. For example, if the target object is
     * a date representing the 31st January, then changing the month to February would be unclear.
     * In cases like this, the field is responsible for resolving the result. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * Implementations must check and return any fields defined in {@code LocalDateTimeField} before
     * delegating on to the method on the specified field.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original.
     *
     * @param field  the field to set in the returned date, not null
     * @param newValue  the new value of the field in the returned date, not null
     * @return an object of the same type with the specified field set, not null
     * @throws CalendricalException if the specified value is invalid
     * @throws CalendricalException if the field cannot be set on this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    DateTimeCalendrical with(DateTimeField field, long newValue);

    /**
     * Extracts an instance of the specified type.
     * <p>
     * This allows common objects to be easily extracted in a standard way.
     * It is intended for lower-level code rather than day-to-day business logic.
     * <p>
     * Implementations must return the following types if they contain sufficient information:
     * <ul>
     * <li>LocalDate
     * <li>LocalTime
     * <li>LocalDateTime
     * <li>OffsetDate
     * <li>OffsetTime
     * <li>OffsetDateTime
     * <li>ZoneOffset
     * <li>ZoneId
     * <li>Instant
     * <li>DateTimeBuilder
     * <liL>Class - like {@code getClass()} but returning the public type of the implementation
     * <li>this object if the specified type is passed in
     * </ul>
     * Other objects may be returned if appropriate.
     * 
     * @param <T> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    <T> T extract(Class<T> type);

}
