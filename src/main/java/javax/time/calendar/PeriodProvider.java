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
package javax.time.calendar;

/**
 * Provides access to a period of time, such as '2 Years and 5 Months'.
 * <p>
 * PeriodProvider is a simple interface that provides uniform access to
 * any object that can provide access to a period.
 * <p>
 * The implementation of {@code PeriodProvider} may be mutable.
 * The result of {@link #toPeriodFields()}, however, is immutable.
 * <p>
 * When implementing an API that accepts a {@code PeriodProvider} as a parameter,
 * it is important to convert the input to a {@code PeriodFields} once and once only.
 * It is recommended that this is done at the top of the method before other processing.
 * This is necessary to handle the case where the implementation of the provider is
 * mutable and changes in value between two calls to {@code toPeriodFields()}.
 * <p>
 * The recommended way to convert a DateProvider to a LocalDate is using
 * {@link PeriodFields#of(PeriodProvider)} as this method provides additional null checking.
 * <p>
 * PeriodProvider makes no guarantees about the thread-safety or
 * immutability of implementations.
 *
 * @author Stephen Colebourne
 */
public interface PeriodProvider {

    /**
     * Returns an instance of {@code PeriodFields} initialized from the
     * state of this object.
     * <p>
     * This method will take the period represented by this object and return
     * an equivalent {@link PeriodFields}.
     * The amount stored for a unit in the result may be zero.
     * If this object is already a {@code PeriodFields} then it is simply returned.
     * <p>
     * Implementations must ensure that this method provides a thread-safe consistent
     * result. An immutable implementation will naturally provide this guarantee.
     *
     * @return the period equivalent to this one, never null
     */
    PeriodFields toPeriodFields();

}
