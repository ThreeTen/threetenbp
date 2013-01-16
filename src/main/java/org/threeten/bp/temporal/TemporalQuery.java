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

import static org.threeten.bp.temporal.ChronoField.OFFSET_SECONDS;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

/**
 * Strategy for querying a date-time object.
 * <p>
 * This interface allows different kinds of query to be modeled.
 * Examples might be a query that checks if the date is the day before February 29th
 * in a leap year, or calculates the number of days to your next birthday.
 * <p>
 * Implementations should not normally be used directly.
 * Instead, the {@link TemporalAccessor#query(TemporalQuery)} method must be used:
 * <pre>
 *   dateTime = dateTime.query(query);
 * </pre>
 * <p>
 * See {@link TemporalAdjusters} for a standard set of adjusters, including finding the
 * last day of the month.
 *
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 */
public interface TemporalQuery<R> {
    // special constants should be used to extract information from a DateTimeAccessor
    // that cannot be derived in other ways
    /**
     * The special constant for the query for {@code ZoneId}.
     * <p>
     * If the target {@code DateTimeAccessor} has a zone ID, then querying
     * it with this constant must return the chronology.
     */
    TemporalQuery<ZoneId> ZONE_ID = new TemporalQuery<ZoneId>() {
        @Override
        public ZoneId queryFrom(TemporalAccessor temporal) {
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
    TemporalQuery<Chrono<?>> CHRONO = new TemporalQuery<Chrono<?>>() {
        @Override
        public Chrono<?> queryFrom(TemporalAccessor temporal) {
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
    TemporalQuery<ChronoUnit> TIME_PRECISION = new TemporalQuery<ChronoUnit>() {
        @Override
        public ChronoUnit queryFrom(TemporalAccessor temporal) {
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
    TemporalQuery<ZoneOffset> OFFSET = new TemporalQuery<ZoneOffset>() {
        @Override
        public ZoneOffset queryFrom(TemporalAccessor temporal) {
            if (temporal.isSupported(OFFSET_SECONDS)) {
                return ZoneOffset.ofTotalSeconds(temporal.get(OFFSET_SECONDS));
            }
            return null;
        }
    };

    /**
     * Implementation of the strategy to query the specified date-time object.
     * <p>
     * This method is not intended to be called by application code directly.
     * Instead, the {@link TemporalAccessor#query(TemporalQuery)} method must be used:
     * <pre>
     *   dateTime = dateTime.query(query);
     * </pre>
     *
     * <h5>Implementation notes</h5>
     * The implementation queries the input date-time object to return the result.
     * For example, an implementation might query the date and time, returning
     * the astronomical Julian day as a {@code BigDecimal}.
     * <p>
     * This interface can be used by calendar systems other than ISO.
     * Implementations may choose to document compatibility with other calendar systems, or
     * validate for it by querying the chronology from the input object.
     *
     * @param temporal  the date-time object to query, not null
     * @return the queried value, avoid returning null
     * @throws DateTimeException if unable to query
     * @throws ArithmeticException if numeric overflow occurs
     */
    R queryFrom(TemporalAccessor temporal);

}
