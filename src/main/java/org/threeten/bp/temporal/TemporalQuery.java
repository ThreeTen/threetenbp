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
