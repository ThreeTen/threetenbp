/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.period;

import java.util.Set;

import javax.time.calendar.PeriodUnit;

/**
 * Provides access to a period of time, such as '2 Years and 5 Months'.
 * <p>
 * PeriodProvider is a simple interface that provides uniform access to
 * any object that can provide access to a period.
 * <p>
 * The implementation of <code>PeriodProvider</code> may be mutable.
 * <p>
 * PeriodProvider makes no guarantees about the thread-safety or
 * immutability of implementations.
 *
 * @author Stephen Colebourne
 */
public interface PeriodProvider {
    // TODO: alternate representation, for better thread safety?
    // PeriodFields
    // Set<PeriodField>
    // Map<PeriodRule, Long>

    /**
     * Gets the complete set of units which have amounts stored.
     * <p>
     * The amount stored for a unit may be zero.
     * <p>
     * Implementations must ensure that this method is thread-safe, and that
     * the returned set is either unmodifiable or independent from the implementation.
     *
     * @return the set of units for which an amount is stored in this period
     */
    Set<PeriodUnit> periodRules();

    /**
     * Gets the amount of time stored for the specified unit.
     * <p>
     * Zero is returned if no amount is stored for the unit.
     * <p>
     * Implementations must ensure that this method is thread-safe.
     *
     * @param unit  the unit to get, not null
     * @return the amount of time stored in this period for the unit
     */
    long periodAmount(PeriodUnit unit);

}
