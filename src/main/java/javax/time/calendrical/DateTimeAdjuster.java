/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.CalendricalException;

/**
 * Strategy for adjusting a calendrical.
 * <p>
 * This interface provides a common way to access many different adjustments.
 * Examples might be an adjuster that sets the date avoiding weekends, or one that
 * sets the date to the last day of the month.
 * <p>
 * An adjuster is not normally used directly. Instead it should be used as follows:
 * <pre>
 *   date = date.with(adjuster);
 * </pre>
 * 
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 */
public interface DateTimeAdjuster {

    /**
     * Adjusts the input calendrical.
     * <p>
     * This method returns a new object based on this one with the specified adjustment made.
     * For example, on a {@code LocalDate}, this could be used to adjust to the "next Wednesday".
     * The returned object will have the same observable type as this object.
     * <p>
     * Implementations should use the methods on {@code DateTimeObject} to make the adjustment.
     * The input object will be mutated if it is mutable, or a new object returned if immutable.
     * <p>
     * This interface can be used by calendar systems other than ISO.
     * Implementations may choose to document compatibility with other calendar systems, or
     * validate for it by querying the calendar system from the input object.
     *
     * @param calendrical  the calendrical to adjust, not null
     * @return an object of the same type with the adjustment made, not null
     * @throws CalendricalException if the unable to make the adjustment
     * @throws RuntimeException if the result exceeds the supported range
     */
    DateTimeObject adjustCalendrical(DateTimeObject calendrical);

}
