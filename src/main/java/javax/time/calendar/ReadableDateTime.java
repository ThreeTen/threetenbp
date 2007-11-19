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
package javax.time.calendar;

/**
 * Provides read-only access to a date-time in the ISO calendar system.
 * <p>
 * ReadableDateTime is a simple interface that provides uniform access to any
 * object that can provide access to a date-time in the ISO calendar system.
 * <p>
 * NOTE: The implementation of <code>ReadableDateTime</code> may be mutable.
 * For example, {@link java.util.GregorianCalendar GregorianCalendar} is a
 * mutable implementation of this interface.
 * The result of {@link #toLocalDateTime()}, however, is immutable.
 * <p>
 * NOTE: The implementation of <code>ReadableDateTime</code> may provide more
 * information than just a local date-time. For example, {@link ZonedDateTime},
 * implements this interface and also provides a time zone.
 * <p>
 * ReadableDateTime makes no guarantees about the thread-safety or immutability
 * of implementations.
 *
 * @author Stephen Colebourne
 */
public interface ReadableDateTime extends ReadableDate, ReadableTime {

    /**
     * Returns an instance of <code>LocalDateTime</code> initialised from the
     * state of this object.
     * <p>
     * This method will take the date-time represented by this object and return
     * a {@link LocalDateTime} constructed using the year, month, day, hour,
     * minute, second and nanosecond. If this object is already a
     * <code>LocalDateTime</code> then it is simply returned.
     * <p>
     * If this object does not support nanosecond precision, then all fields
     * below the precision it does support must be set to zero. For example,
     * if this instance only stores hours, minutes and seconds, then the
     * nanoseconds part will be set to zero.
     * <p>
     * It is recommended that only classes that provide time information to
     * at least minute precision implement this interface.
     * For example, a class that only represents the date and hour of day
     * should not implement <code>ReadableDateTime</code>.
     * <p>
     * The result of this method is a <code>LocalDateTime</code> which represents
     * a date in the ISO calendar system. Implementors may perform conversion
     * when implementing this method to convert from alternate calendar systems.
     *
     * @return the <code>LocalDateTime</code> equivalent to this object, never null
     */
    LocalDateTime toLocalDateTime();

}
