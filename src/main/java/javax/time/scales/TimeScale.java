/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.scales;

import javax.time.Duration;
import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.calendar.CalendarConversionException;

/**
 * A time-scale that defines how an instant relates to the time-line.
 * <p>
 * Most of the Java Time Framework works on the assumption that the time-line is
 * simple, there are no leap-seconds and there are always 24 * 60 * 60 seconds in a day.
 * Sadly, the real-life time-line is not this simple.
 * <p>
 * This interface defines a time-scale that is used to represent different ways
 * of counting time from the standard idealized version used by {@link Instant}.
 * <p>
 * The interface is usually used internally from the {@link TimeScaleInstant} class.
 * Standard time-scale implementations are provided in {@link TimeScales}.
 * <p>
 * As API designers, we hope that that time-scales will be rarely used, and that the
 * "ideal world" represented by {@code Instant} will be sufficient.
 * <p>
 * TimeScale is an interface and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 * <p>
 * It is recommended that implementations are {@code Serializable} where possible.
 *
 * @author Stephen Colebourne
 * @author Mark Thornton
 */
public interface TimeScale {

    /**
     * Gets the internal name of the time-scale.
     * 
     * @return the name of the time-scale, never null
     */
    String getName();

    /**
     * Does the time scale support leap seconds.
     *
     * @return true if leap seconds are represented in this time scale
     */
    boolean supportsLeapSecond();

    /**
     * Converts a scaled instant to a standard instant.
     * <p>
     * A scaled instant may represent a leap-second, however a standard instant
     * cannot represent this. As such, the implementation may need to adopt a
     * strategy for handling the loss of information.
     * <p>
     * The specified instant must be defined in this time-scale.
     * 
     * @param tsi  the time-scale based instant to convert, not null
     * @return the standard instant, never null
     * @throws IllegalArgumentException if the time-scale isn't this scale
     * @throws CalendarConversionException if the conversion cannot be performed
     */
    Instant toInstant(TimeScaleInstant tsi);

    /**
     * Convert a scaled instant to TAI.
     *
     * @param tsi  the time-scale based instant to convert, not null
     * @return instant converted to TAI, never null
     */
    TimeScaleInstant toTAI(TimeScaleInstant tsi);

    /**
     * Converts a standard instant to a scaled instant.
     * <p>
     * The resulting instant will have this time-scale.
     * 
     * @param provider  the instant provider, not null
     * @return the scaled instant, never null
     * @throws CalendarConversionException if the conversion cannot be performed
     */
    TimeScaleInstant toTimeScaleInstant(InstantProvider provider);

    /**
     * Converts a scaled instant to this time scale.
     *
     * @param tsi  the time-scale based instant to convert, not null
     * @return corresponding instant on this time scale, never null
     */
    TimeScaleInstant toTimeScaleInstant(TimeScaleInstant tsi);

    /**
     * Checks the validity of the specified instant.
     * Time scales which are not continuous give rise to periods which do not correspond to a
     * genuine instant (invalid), or which have more than one corresponding real instant (ambiguous).
     *
     * @param tsi  the time-scale based instant to check, not null
     * @return validity of the instant, never null
     */
    TimeScaleInstant.Validity getValidity(TimeScaleInstant tsi);

    /**
     * Adds the duration to the specified instant.
     *
     * @param tsi  the time-scale based instant to add to, not null
     * @param dur  the duration to add, not null
     * @return a {@code TimeScaleInstant} equal to the instant plus the duration, never null
     */
    TimeScaleInstant add(TimeScaleInstant tsi, Duration dur);

    /**
     * Subtracts the duration from the specified instant.
     *
     * @param tsi  the time-scale based instant to subtract from, not null
     * @param dur  the duration to subtract, not null
     * @return a {@code TimeScaleInstant} equal to the instant minus the duration, never null
     */
    TimeScaleInstant subtract(TimeScaleInstant tsi, Duration dur);

    /**
     * Calculates the duration between two instants on this time scale.
     * <p>
     * If the two instants are not on this time scale they will be converted.
     *
     * @param start  the first time-scale based instant, not null
     * @param end  the second time-scale based instant, not null
     * @return the duration measured using this time scale, never null
     */
    Duration durationBetween(TimeScaleInstant start, TimeScaleInstant end);

}
