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

import static javax.time.calendrical.ChronoField.OFFSET_SECONDS;

import javax.time.DateTimeException;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.chrono.Chrono;

/**
 * General low-level access to a date and/or time object.
 * <p>
 * This interface is implemented by all date-time classes.
 * It provides access to the state using the {@link #get(DateTimeField)} and
 * {@link #getLong(DateTimeField)} methods that takes a {@link DateTimeField}.
 * Access is also provided to any additional state using a query interface
 * through {@link #query(Query)}.
 * This provides access to the time-zone, precision and calendar system.
 * <p>
 * A sub-interface, {@link DateTime}, extends this definition to one that also
 * supports adjustment and manipulation on more complete date-time objects.
 *
 * <h4>Implementation notes</h4>
 * This interface places no restrictions on implementations and makes no guarantees
 * about their thread-safety.
 * See {@code DateTime} for a full description of whether to implement this
 * interface.
 */
public interface DateTimeAccessor {

    /**
     * Checks if the specified date-time field is supported.
     * <p>
     * This checks if the date-time can be queried for the specified field.
     * If false, then calling the {@link #range(DateTimeField) range} and {@link #get(DateTimeField) get}
     * methods will throw an exception.
     *
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link DateTimeField#doRange(DateTimeAccessor) doRange method} on the specified field.
     *
     * @param field  the field to check, null returns false
     * @return true if this date-time can be queried for the field, false if not
     */
    boolean isSupported(DateTimeField field);

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
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link DateTimeField#doRange(DateTimeAccessor) doRange method} on the specified field.
     *
     * @param field  the field to get, not null
     * @return the range of valid values for the field, not null
     * @throws DateTimeException if the range for the field cannot be obtained
     */
    DateTimeValueRange range(DateTimeField field);

    /**
     * Gets the value of the specified date-time field as an {@code int}.
     * <p>
     * This queries the date-time for the value for the specified field.
     * The returned value will always be within the valid range of values for the field.
     * If the date-time cannot return the value, because the field is unsupported or for
     * some other reason, an exception will be thrown.
     *
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link DateTimeField#doGet(DateTimeAccessor) doGet method} on the specified field.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws DateTimeException if a value for the field cannot be obtained
     * @throws DateTimeException if the range of valid values for the field exceeds an {@code int}
     * @throws DateTimeException if the value is outside the range of valid values for the field
     * @throws ArithmeticException if numeric overflow occurs
     */
    int get(DateTimeField field);

    /**
     * Gets the value of the specified date-time field as a {@code Long}.
     * <p>
     * This queries the date-time for the value for the specified field.
     * The returned value may be outside the valid range of values for the field.
     * If the date-time cannot return the value, because the field is unsupported or for
     * some other reason, an exception will be thrown.
     *
     * <h4>Implementation notes</h4>
     * Implementations must check and handle any fields defined in {@link ChronoField} before
     * delegating on to the {@link DateTimeField#doGet(DateTimeAccessor) doGet method} on the specified field.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws DateTimeException if a value for the field cannot be obtained
     * @throws ArithmeticException if numeric overflow occurs
     */
    long getLong(DateTimeField field);

    /**
     * Queries this date-time.
     * <p>
     * This queries this date-time using the specified query strategy object.
     * The Query interface has three special predefined constants -
     * {@code Query.ZONE_ID}, {@code Query.CHRONO} and {@code Query.TIME_PRECISION}.
     * Other queries may be defined by applications.
     *
     * <h4>Implementation notes</h4>
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
     *   public &lt;R&gt; R query(Query&lt;R&gt; type) {
     *     // only include an if statement if the implementation can return it
     *     if (query == Query.ZONE_ID)  return // the ZoneId
     *     if (query == Query.CHRONO)  return // the Chrono
     *     if (query == Query.PRECISION)  return // the precision
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
    <R> R query(Query<R> query);

    //-----------------------------------------------------------------------
    /**
     * Strategy for querying a date-time object.
     * <p>
     * This interface allows different kinds of query to be modeled.
     * Examples might be a query that checks if the date is the day before February 29th
     * in a leap year, or calculates the number of days to your next birthday.
     * <p>
     * Implementations should not normally be used directly.
     * Instead, the {@link DateTimeAccessor#query(Query)} method must be used:
     * <pre>
     *   dateTime = dateTime.query(query);
     * </pre>
     * <p>
     * See {@link DateTimeAdjusters} for a standard set of adjusters, including finding the
     * last day of the month.
     *
     * <h4>Implementation notes</h4>
     * This interface must be implemented with care to ensure other classes operate correctly.
     * All implementations that can be instantiated must be final, immutable and thread-safe.
     */
    public interface Query<R> {
        // special constants should be used to extract information from a DateTimeAccessor
        // that cannot be derived in other ways
        /**
         * The special constant for the query for {@code ZoneId}.
         * <p>
         * If the target {@code DateTimeAccessor} has a zone ID, then querying
         * it with this constant must return the chronology.
         */
        Query<ZoneId> ZONE_ID = new Query<ZoneId>() {
            @Override
            public ZoneId doQuery(DateTimeAccessor dateTime) {
                return null;
            }
        };
        /**
         * The special constant for the query for {@code Chrono}.
         * <p>
         * If the target {@code DateTimeAccessor} has a chronology, then querying
         * it with this constant must return the chronology.
         * Note that {@code LocalTime} returns null as it is valid for all chronologies.
         */
        Query<Chrono<?>> CHRONO = new Query<Chrono<?>>() {
            @Override
            public Chrono<?> doQuery(DateTimeAccessor dateTime) {
                return null;
            }
        };
        /**
         * The special constant for the query for the minimum supported time unit.
         * <p>
         * If the target {@code DateTimeAccessor} represents a consistent or complete
         * date-time, date or time then this must return the smallest precision actually
         * supported. Note that fields such as {@code NANO_OF_DAY} and {@code NANO_OF_SECOND}
         * are defined to always return ignoring the precision, thus this is the only
         * way to find the accurate minimum supported unit.
         * <p>
         * For example, {@code GregorianCalendar} has a precision of {@code MILLIS}, whereas
         * {@code LocalDate} and {@code ZoneOffset} have no time precision and thus returns null.
         */
        Query<ChronoUnit> TIME_PRECISION = new Query<ChronoUnit>() {
            @Override
            public ChronoUnit doQuery(DateTimeAccessor dateTime) {
                return null;
            }
        };
        /**
         * A query for the {@code ZoneOffset}.
         * <p>
         * This query examines the {@link ChronoField#OFFSET_SECONDS offset-seconds}
         * field and uses it to create a {@code ZoneOffset}.
         * Implementations of {@code DateTimeAccessor} may choose to check for this
         * constant and return a stored offset directly.
         */
        Query<ZoneOffset> OFFSET = new Query<ZoneOffset>() {
            @Override
            public ZoneOffset doQuery(DateTimeAccessor dateTime) {
                if (dateTime.isSupported(OFFSET_SECONDS)) {
                    return ZoneOffset.ofTotalSeconds(dateTime.get(OFFSET_SECONDS));
                }
                return null;
            }
        };

        /**
         * Implementation of the strategy to query the specified date-time object.
         * <p>
         * This method is not intended to be called by application code directly.
         * Instead, the {@link DateTimeAccessor#query(Query)} method must be used:
         * <pre>
         *   dateTime = dateTime.query(query);
         * </pre>
         *
         * <h4>Implementation notes</h4>
         * The implementation queries the input date-time object to return the result.
         * For example, an implementation might query the date and time, returning
         * the astronomical Julian day as a {@code BigDecimal}.
         * <p>
         * This interface can be used by calendar systems other than ISO.
         * Implementations may choose to document compatibility with other calendar systems, or
         * validate for it by querying the chronology from the input object.
         *
         * @param dateTime  the date-time object to query, not null
         * @return the queried value, avoid returning null
         * @throws DateTimeException if unable to query
         * @throws ArithmeticException if numeric overflow occurs
         */
        R doQuery(DateTimeAccessor dateTime);
    }

}
