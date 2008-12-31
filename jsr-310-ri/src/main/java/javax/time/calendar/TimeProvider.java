/*
 * Copyright (c) 2007, 2008, Stephen Colebourne & Michael Nascimento Santos
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
 * Provides read-only access to a time in the ISO-8601 calendar system.
 * <p>
 * TimeProvider is a simple interface that provides uniform access to any
 * object that can provide access to a time in the ISO-8601 calendar system.
 * <p>
 * NOTE: The implementation of <code>TimeProvider</code> may be mutable.
 * For example, {@link java.util.GregorianCalendar GregorianCalendar} is a
 * mutable implementation of this interface.
 * The result of {@link #toLocalTime()}, however, is immutable.
 * <p>
 * NOTE: The implementation of <code>TimeProvider</code> may provide more
 * information than just a local time. For example, {@link ZonedDateTime},
 * implements this interface and also provides a date and a time zone.
 * <p>
 * TimeProvider makes no guarantees about the thread-safety or immutability
 * of implementations.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public interface TimeProvider extends CalendricalProvider {

    /**
     * Returns an instance of <code>LocalTime</code> initialised from the
     * state of this object.
     * <p>
     * This method will take the time represented by this object and return
     * a {@link LocalTime} constructed using the hour, minute, second and
     * nanosecond. If this object is already a <code>LocalTime</code> then
     * it is simply returned.
     * <p>
     * If this object does not support nanosecond precision, then all fields
     * below the precision it does support must be set to zero. For example,
     * if this instance only stores hours, minutes and seconds, then the
     * nanoseconds part will be set to zero.
     * <p>
     * It is recommended that only classes that provide time information to
     * at least minute precision implement this interface.
     * For example, a class that only represents the
     * {@link javax.time.calendar.field.HourOfDay hour of day} should not
     * implement <code>TimeProvider</code>.
     * <p>
     * The result of this method is a <code>LocalTime</code> which represents
     * a time in the ISO calendar system. Implementors may perform conversion
     * when implementing this method to convert from alternate calendar systems.
     *
     * @return the <code>LocalTime</code> equivalent to this object, never null
     */
    LocalTime toLocalTime();

}
