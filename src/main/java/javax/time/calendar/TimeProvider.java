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
package javax.time.calendar;

import javax.time.CalendricalException;

/**
 * Provides access to a time in the ISO-8601 calendar system.
 * <p>
 * TimeProvider is a simple interface that provides uniform access to any
 * object that can provide access to a time in the ISO-8601 calendar system.
 * <p>
 * The implementation of {@code TimeProvider} may be mutable.
 * For example, {@link java.util.GregorianCalendar GregorianCalendar} is a
 * mutable implementation of this interface.
 * The result of {@link #toLocalTime()}, however, is immutable.
 * <p>
 * When implementing an API that accepts a TimeProvider as a parameter, it is
 * important to convert the input to a {@code LocalTime} once and once only.
 * It is recommended that this is done at the top of the method before other processing.
 * This is necessary to handle the case where the implementation of the provider is
 * mutable and changes in value between two calls to {@code toLocalTime()}.
 * <p>
 * The recommended way to convert a TimeProvider to a LocalTime is using
 * {@link LocalTime#of(TimeProvider)} as this method provides additional null checking.
 * <p>
 * It is recommended that this interface should only be implemented by classes
 * that provide time information to at least minute precision.
 * <p>
 * The implementation of {@code TimeProvider} may provide more
 * information than just a time. For example, {@link OffsetTime},
 * implements this interface and also provides a zone offset.
 * <p>
 * TimeProvider makes no guarantees about the thread-safety or immutability
 * of implementations.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public interface TimeProvider {

    /**
     * Returns an instance of {@code LocalTime} initialized from the
     * state of this object.
     * <p>
     * This method will take the time represented by this object and return
     * a {@link LocalTime} constructed using the hour, minute, second and
     * nanosecond. If this object is already a {@code LocalTime} then
     * it is simply returned.
     * <p>
     * If this object does not support nanosecond precision, then all fields
     * below the precision it does support must be set to zero. For example,
     * if this instance only stores hours, minutes and seconds, then the
     * nanoseconds part will be set to zero.
     * <p>
     * The result of this method is a {@code LocalTime} which represents
     * a time in the ISO calendar system. Implementors may perform conversion
     * when implementing this method to convert from alternate calendar systems.
     *
     * @return the {@code LocalTime} equivalent to this object, never null
     * @throws CalendricalException if the time cannot be converted
     */
    LocalTime toLocalTime();

}
