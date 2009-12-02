/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.CalendarConversionException;

/**
 * A time-scale that defines the meaning of the count of time.
 * <p>
 * Most of the Java Time Framework works on the assumption that the time-line is
 * simple, there are no leap-seconds and there are always 24 * 60 * 60 seconds in a day.
 * Sadly, the real-life time-line is not this simple.
 * <p>
 * This interface defines a time-scale that is used to represent different ways
 * of counting time from the standard idealised version used by {@link Instant}.
 * <p>
 * The interface is usually used internally from the {@link TimeScaleInstant} class.
 * Standard time-scale implementations are provided in {@link TimeScales}.
 * <p>
 * As API designers, we hope that that time-scales will be rarely used, and that the
 * 'ideal world' represented by <code>Instant</code> will be sufficient.
 * <p>
 * TimeScale is an interface and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public interface TimeScale {
    // implementations should be a single enum
    // need methods to convert from one scale to another
    // duration between ?
    // plus(Duration) ?
    // isCompleteTimeLine() ?

    /**
     * Converts a scaled instant to a standard instant.
     * <p>
     * A scaled instant may represent a leap-second, however a standard instant
     * cannot represent this. As such, the implementation may need to adopt a
     * strategy for handling the loss of information.
     * <p>
     * The specified instant must be defined in this time-scale.
     * 
     * @param tsInstant  the time-scale based instant to convert, not null
     * @return the standard instant, never null
     * @throws IllegalArgumentException if the time-scale isn't this scale
     * @throws CalendarConversionException if the conversion cannot be performed
     */
    Instant toInstant(TimeScaleInstant tsInstant);

    /**
     * Converts a standard instant to a scaled instant.
     * <p>
     * The resulting instant will have this time-scale.
     * 
     * @param tsInstant  the time-scale based instant to convert, not null
     * @return the scaled instant, never null
     * @throws CalendarConversionException if the conversion cannot be performed
     */
    TimeScaleInstant toTimeScaleInstant(InstantProvider instantProvider);

    /**
     * Gets the internal name of the time-scale.
     * 
     * @return the name of the time-scale, never null
     */
    String getName();

}
