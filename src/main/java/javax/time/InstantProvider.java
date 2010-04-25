/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

/**
 * Provides access to an instant on the time-line.
 * <p>
 * {@code InstantProvider} is a simple interface that provides uniform access to any
 * object that can provide access to an {@code Instant}.
 * <p>
 * The implementation of {@code InstantProvider} may be mutable.
 * For example, {@link java.util.Date Date} is a mutable implementation of this interface.
 * The result of calling {@link #toInstant()} is always immutable.
 * <p>
 * When implementing an API that accepts an {@code InstantProvider} as a parameter, it is
 * important to convert the input to a {@code Instant} once and once only.
 * It is recommended that this is done at the top of the method before other processing.
 * This is necessary to handle the case where the implementation of the provider is
 * mutable and changes in value between two calls to {@code toInstant()}.
 * <p>
 * The recommended way to convert an {@code InstantProvider} to a {@code Instant} is using
 * {@link Instant#of(InstantProvider)} as this method provides additional null checking.
 * <p>
 * The implementation of {@code InstantProvider} may provide more
 * information than just an instant. For example,
 * {@link javax.time.calendar.ZonedDateTime ZonedDateTime}, implements this
 * interface and also provides full date, time and time-zone information.
 * <p>
 * InstantProvider makes no overall guarantees about the thread-safety or immutability
 * of implementations. However, the method itself has a specific thread-safe guarantee.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public interface InstantProvider {

    /**
     * Returns an instance of {@code Instant} initialized from the
     * state of this object.
     * <p>
     * This method will take the instant represented by this object and return
     * an {@link Instant}. If this object is already a {@code Instant}
     * then it is simply returned.
     * <p>
     * If this object does not support nanosecond precision, then all fields
     * below the precision it does support must be set to zero. For example,
     * if this instance only stores millisecond precision, then the
     * nanoseconds part of the {@code Instant} will be set to zero.
     * It is recommended that this interface should only be implemented by classes
     * that provide time information to at least minute precision.
     * <p>
     * InstantProvider makes no overall guarantees about the thread-safety.
     * However, this method must return a fully consistent result.
     * For example, if the implementation is mutable and contains two fields,
     * then the result of this method must refer to a valid snapshot of both fields,
     * and not a snapshot where one field has been updated and not the other.
     *
     * @return the {@code Instant} equivalent to this object, never null
     * @throws CalendricalException if the time cannot be converted
     */
    Instant toInstant();

}
