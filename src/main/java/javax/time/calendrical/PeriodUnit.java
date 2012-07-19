/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.Duration;
import javax.time.Period;

/**
 * A unit of time, such as Days or Hours.
 * <p>
 * Measurement of time is built on units, such as years, months, days, hours, minutes and seconds.
 * An instance of this interface represents the unit itself, whereas {@link Period}
 * represents an amount of the unit.
 * <p>
 * Implementations of this interface define one or more units.
 * The units include their own calculations which are specific to one calendar system.
 * 
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * It is recommended to use an enum where possible.
 */
public interface PeriodUnit {

    /**
     * Gets a descriptive name for the unit.
     * <p>
     * This should be in the plural and upper-first camel case, such as 'Days' or 'Minutes'.
     * 
     * @return the name, not null
     */
    String getName();

    /**
     * Gets the duration of this unit, which may be an estimate.
     * <p>
     * All units return a duration measured in standard nanoseconds from this method.
     * For example, an hour has a duration of {@code 60 * 60 * 1,000,000,000ns}.
     * <p>
     * Some units may return an accurate duration while others return an estimate.
     * For example, days have an estimated duration due to the possibility of
     * daylight saving time changes.
     * Use {@link #isDurationEstimated()} to determine if the status of the duration.
     *
     * @return the estimated duration of this unit, not null
     */
    Duration getDuration();

    /**
     * Checks if the duration of the unit is an estimate.
     * <p>
     * All units have a duration, however the duration is not always accurate.
     * For example, days have an estimated duration due to the possibility of
     * daylight saving time changes.
     * This method returns true if the duration is an estimate and false if it is
     * accurate. Note that accurate/estimated ignores leap seconds.
     *
     * @return true if the duration is estimated, false if accurate
     */
    boolean isDurationEstimated();

    //-----------------------------------------------------------------------
    /**
     * Implementation of the logic to add a period to the specified date-time.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use {@link AdjustableDateTime#plus(long, PeriodUnit)} or the
     * equivalent {@code minus} method on the date-time object passing this as the argument.
     * <pre>
     *   updated = date.plus(amount, unit);
     * </pre>
     * <p>
     * The period added is a multiple of this unit. For example, this method
     * could be used to add "3 days" to a date by calling this method on the
     * instance representing "days", passing the date and the period "3".
     * The period to be added may be negative, which is equivalent to subtraction.
     * Implementations must be written using the units available in {@link LocalPeriodUnit}
     * or the fields available in {@link LocalDateTimeField}.
     *
     * @param dateTime  the date-time object to adjust, not null
     * @param periodToAdd  the period of this unit to add, positive or negative
     * @return the adjusted date-time object, not null
     * @throws CalendricalException if the period cannot be added
     */
    <R extends AdjustableDateTime> R doAdd(R dateTime, long periodToAdd);

    //-----------------------------------------------------------------------
    /**
     * Calculates the period in terms of this unit between two date-time objects of the same type.
     * <p>
     * The period will be positive if the second date is after the first, and
     * negative if the second date is before the first.
     *
     * @param <R>  the type of the date-time; the two date-times must be of the same type
     * @param dateTime1  the base date-time object, not null
     * @param dateTime2  the other date-time object, not null
     * @return the Period between datetime1 and datetime2; 
     *      positive if datetime2 is later than datetime1, not null
     */
    <R extends AdjustableDateTime> Period between(R dateTime1, R dateTime2);

    //-----------------------------------------------------------------------
    /**
     * Outputs this unit as a {@code String} using the name.
     *
     * @return the name of this unit, not null
     */
    @Override
    String toString();  // JAVA8 default interface method

}
