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

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

/**
 * Common implementations of {@code TemporalQuery}.
 * <p>
 * This class provides common implementations of {@link TemporalQuery}.
 * These queries are primarily used as optimizations, allowing the internals
 * of other objects to be extracted effectively. Note that application code
 * can also use the {@code from(TemporalAccessor)} method on most temporal
 * objects as a method reference matching the query interface, such as
 * {@code LocalDate::from} and {@code ZoneId::from}.
 * <p>
 * There are two equivalent ways of using a {@code TemporalQuery}.
 * The first is to invoke the method on the interface directly.
 * The second is to use {@link TemporalAccessor#query(TemporalQuery)}:
 * <pre>
 *   // these two lines are equivalent, but the second approach is recommended
 *   dateTime = query.queryFrom(dateTime);
 *   dateTime = dateTime.query(query);
 * </pre>
 * It is recommended to use the second approach, {@code query(TemporalQuery)},
 * as it is a lot clearer to read in code.
 *
 * <h3>Specification for implementors</h3>
 * This is a thread-safe utility class.
 * All returned adjusters are immutable and thread-safe.
 */
public final class TemporalQueries {
    // note that it is vital that each method supplies a constant, not a
    // calculated value, as they will be checked for using ==
    // it is also vital that each constant is different (due to the == checking)

    /**
     * Private constructor since this is a utility class.
     */
    private TemporalQueries() {
    }

    //-----------------------------------------------------------------------
    // special constants should be used to extract information from a DateTimeAccessor
    // that cannot be derived in other ways
    /**
     * The special constant for the query for {@code ZoneId}.
     * <p>
     * If the target {@code DateTimeAccessor} has a zone ID, then querying
     * it with this constant must return the chronology.
     */
    public static final TemporalQuery<ZoneId> ZONE_ID = new TemporalQuery<ZoneId>() {
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
    public static final TemporalQuery<Chrono<?>> CHRONO = new TemporalQuery<Chrono<?>>() {
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
    public static final TemporalQuery<ChronoUnit> TIME_PRECISION = new TemporalQuery<ChronoUnit>() {
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
    public static final TemporalQuery<ZoneOffset> OFFSET = new TemporalQuery<ZoneOffset>() {
        @Override
        public ZoneOffset queryFrom(TemporalAccessor temporal) {
            if (temporal.isSupported(OFFSET_SECONDS)) {
                return ZoneOffset.ofTotalSeconds(temporal.get(OFFSET_SECONDS));
            }
            return null;
        }
    };

}
