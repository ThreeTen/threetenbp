/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.temporal;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.Duration;
import org.threeten.bp.Period;
import org.threeten.bp.temporal.Temporal.MinusAdjuster;
import org.threeten.bp.temporal.Temporal.PlusAdjuster;

/**
 * A unit of date-time, such as Days or Hours.
 * <p>
 * Measurement of time is built on units, such as years, months, days, hours, minutes and seconds.
 * Implementations of this interface represent those units.
 * <p>
 * An instance of this interface represents the unit itself, rather than an amount of the unit.
 * See {@link Period} for a class that represents an amount in terms of the common units.
 * <p>
 * The most commonly used units are defined in {@link ChronoUnit}.
 * Additional units can be written by application code by implementing this interface.
 * <p>
 * The unit works using double dispatch. Client code calls methods on a date-time like
 * {@code LocalDateTime} which check if the unit is a {@code ChronoUnit}.
 * If it is, then the date-time must handle it.
 * Otherwise, the method call is re-dispatched to the matching method in this interface.
 *
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * It is recommended to use an enum where possible.
 */
public interface TemporalUnit {

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
     * To determine if the duration is an estimate, use {@link #isDurationEstimated()}.
     *
     * @return the duration of this unit, which may be an estimate, not null
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
     * Checks if this unit is supported by the specified date-time object.
     * <p>
     * This checks that the implementing date-time can add/subtract this unit.
     * This can be used to avoid throwing an exception.
     *
     * @param dateTime  the date-time object to check, not null
     * @return true if the unit is supported
     */
    boolean isSupported(Temporal dateTime);
    // JAVA 8
    // default {
    //     try {
    //         dateTime.plus(0, this);
    //         return true;
    //     } catch (RuntimeException ex) {
    //         return false;
    //     }
    // }

    /**
     * Implementation of the logic to add a period to the specified date-time.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use {@link Temporal#plus(long, TemporalUnit)} or the
     * equivalent {@code minus} method on the date-time object passing this as the argument.
     * <pre>
     *   updated = date.plus(amount, unit);
     * </pre>
     * <p>
     * The period added is a multiple of this unit. For example, this method
     * could be used to add "3 days" to a date by calling this method on the
     * instance representing "days", passing the date and the period "3".
     * The period to be added may be negative, which is equivalent to subtraction.
     * Implementations must be written using the units available in {@link ChronoUnit}
     * or the fields available in {@link ChronoField}.
     *
     * @param dateTime  the date-time object to adjust, not null
     * @param periodToAdd  the period of this unit to add, positive or negative
     * @return the adjusted date-time object, not null
     * @throws DateTimeException if the period cannot be added
     */
    <R extends Temporal> R doPlus(R dateTime, long periodToAdd);

    //-----------------------------------------------------------------------
    /**
     * Calculates the period in terms of this unit between two date-time objects of the same type.
     * <p>
     * The period will be positive if the second date-time is after the first, and
     * negative if the second date-time is before the first.
     * <p>
     * The result can be queried for the {@link PeriodBetween#getAmount() amount}, the
     * {@link PeriodBetween#getUnit() unit} and used directly in addition/subtraction:
     * <pre>
     *  date = date.minus(MONTHS.between(start, end));
     * </pre>
     *
     * @param <R>  the type of the date-time; the two date-times must be of the same type
     * @param dateTime1  the base date-time object, not null
     * @param dateTime2  the other date-time object, not null
     * @return the period between datetime1 and datetime2 in terms of this unit;
     *      positive if datetime2 is later than datetime1, not null
     */
    <R extends Temporal> PeriodBetween between(R dateTime1, R dateTime2);

    //-----------------------------------------------------------------------
    /**
     * Outputs this unit as a {@code String} using the name.
     *
     * @return the name of this unit, not null
     */
    @Override
    String toString();  // JAVA8 default interface method

    //-----------------------------------------------------------------------
    /**
     * Simple period representing the amount of time between two date-time objects.
     * <p>
     * This interface is the return type from {@link TemporalUnit#between}.
     * It represents an amount of time measured in a single unit.
     * It can be queried for the amount and unit, or added directly to another date-time:
     * <pre>
     *  date = date.minus(MONTHS.between(start, end));
     * </pre>
     *
     * <h4>Implementation notes</h4>
     * This interface must be implemented with care to ensure other classes operate correctly.
     * All implementations that can be instantiated must be final, immutable and thread-safe.
     */
    interface PeriodBetween extends PlusAdjuster, MinusAdjuster {
        /**
         * Gets the amount of the period.
         *
         * @return the amount
         */
        long getAmount();

        /**
         * Gets the unit of the period.
         *
         * @return the unit that the amount is measured in, not null
         */
        TemporalUnit getUnit();
    }

}
