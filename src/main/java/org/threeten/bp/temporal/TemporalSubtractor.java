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
 * Strategy for adjusting a date-time object by subtraction.
 * <p>
 * This interface allows different types of subtraction to be modeled.
 * Implementations of this interface are used to subtract from a date-time.
 * <p>
 * Implementations should not normally be used directly.
 * Instead, the {@link Temporal#minus(MinusAdjuster)} method should be used:
 * <pre>
 *   dateTime = dateTime.minus(adjuster);
 * </pre>
 *
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 */
public interface TemporalSubtractor {

	/**
     * Implementation of the strategy to subtract from the specified date-time object.
     * <p>
     * This method is not intended to be called by application code directly.
     * Instead, the {@link org.threeten.bp.temporal.Temporal#minus(MinusAdjuster)} method should be used:
     * <pre>
     *   dateTime = dateTime.minus(adjuster);
     * </pre>
     *
     * <h5>Implementation notes</h5>
     * The implementation takes the input object and subtracts from it.
     * For example, the implementation {@link org.threeten.bp.Duration} will subtract the length of the duration.
     * <p>
     * Implementations must use the methods on {@code DateTime} to make the adjustment.
     * The returned object must have the same observable type as this object.
     * The input object will be mutated if it is mutable, or a new object returned if immutable.
     * <p>
     * This interface can be used by calendar systems other than ISO.
     * Typically this requires no extra work, because the algorithm for subtracting in
     * the calendar system is part of the {@code DateTime} implementation.
     *
     * @param temporal  the date-time object to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws DateTimeException if unable to subtract
     * @throws ArithmeticException if numeric overflow occurs
     */
    Temporal doMinusAdjustment(Temporal temporal);

}
