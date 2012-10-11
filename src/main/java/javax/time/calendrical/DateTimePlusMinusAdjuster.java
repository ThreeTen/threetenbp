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
import javax.time.Duration;

/**
 * Strategy for adjusting a date-time object by addition or subtraction.
 * <p>
 * This interface provides common access to different additions/subtractions.
 * Implementations of this interface are used to add to a date-time.
 * <p>
 * This adjuster should not normally used directly. Instead it should be used as follows:
 * <pre>
 *   date = date.plus(adjuster);
 *   date = date.minus(adjuster);
 * </pre>
 * <p>
 * Note that calling {@code plus} followed by {@code minus} is not guaranteed to
 * return the same date-time.
 * 
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 */
public interface DateTimePlusMinusAdjuster {

    /**
     * Implementation of the strategy to add to the specified date-time object.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use the {@code plus(DateTimePlusMinusAdjuster)} method on the date-time
     * object passing this as the argument.
     * 
     * <h4>Implementation notes</h4>
     * The implementation takes the input object and adds to it.
     * For example, the implementation {@link Duration} will add the length of the duration.
     * <p>
     * Implementations must use the methods on {@code DateTime} to make the adjustment.
     * The returned object must have the same observable type as this object.
     * The input object will be mutated if it is mutable, or a new object returned if immutable.
     * <p>
     * This interface can be used by calendar systems other than ISO.
     * Typically this requires no extra work, because the algorithm for adding/subtraing in
     * the calendar system is part of the {@code DateTime} implementation.
     *
     * @param dateTime  the date-time object to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws DateTimeException if unable to add
     * @throws ArithmeticException if numeric overflow occurs
     */
    DateTime doAdd(DateTime dateTime);

    /**
     * Implementation of the strategy to subtract from the specified date-time object.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use the {@code minus(DateTimePlusMinusAdjuster)} method on the date-time
     * object passing this as the argument.
     * 
     * <h4>Implementation notes</h4>
     * The implementation takes the input object and adds to it.
     * For example, the implementation {@link Duration} will subtract the length of the duration.
     * <p>
     * Implementations must use the methods on {@code DateTime} to make the adjustment.
     * The returned object must have the same observable type as this object.
     * The input object will be mutated if it is mutable, or a new object returned if immutable.
     * <p>
     * This interface can be used by calendar systems other than ISO.
     * Typically this requires no extra work, because the algorithm for subtracting in
     * the calendar system is part of the {@code DateTime} implementation.
     *
     * @param dateTime  the date-time object to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws DateTimeException if unable to subtract
     * @throws ArithmeticException if numeric overflow occurs
     */
    DateTime doSubtract(DateTime dateTime);

}
