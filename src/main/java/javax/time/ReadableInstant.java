/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
 * Provides read-only access to an instant on the time-line.
 * <p>
 * ReadableInstant is a simple interface that provides uniform access to any
 * object that can provide access to an <code>Instant</code>.
 * <p>
 * NOTE: The implementation of <code>ReadableInstant</code> may be mutable.
 * For example, {@link java.util.Date Date} is a mutable implementation of
 * this interface.
 * The result of {@link #toInstant()}, however, is immutable.
 * <p>
 * NOTE: The implementation of <code>ReadableInstant</code> may provide more
 * information than just an instant. For example,
 * {@link javax.time.calendar.ZonedDateTime ZonedDateTime}, implements this
 * interface and also provides full date, time and time zone information.
 * <p>
 * ReadableInstant makes no guarantees about the thread-safety or immutability
 * of implementations.
 *
 * @author Stephen Colebourne
 */
public interface ReadableInstant {

    /**
     * Returns an instance of <code>Instant</code> initialised from the
     * state of this object.
     * <p>
     * This method will take the instant represented by this object and return
     * an {@link Instant}. If this object is already a <code>Instant</code>
     * then it is simply returned.
     * <p>
     * If this object does not support nanosecond precision, then all fields
     * below the precision it does support must be set to zero. For example,
     * if this instance only stores millisecond precision, then the
     * nanoseconds part of the <code>Instant</code> will be set to zero.
     * <p>
     * It is recommended that only classes that provide time information to
     * at least minute precision implement this interface.
     * For example, a class that only represents the date and hour should not
     * implement <code>ReadableInstant</code>.
     *
     * @return the <code>Instant</code> equivalent to this object, never null
     */
    Instant toInstant();

}
