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
package javax.time.builder;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;

/**
 * A unit of time, such as Days or Hours.
 * <p>
 * Measurement of time is built on units, such as years, months, days, hours, minutes and seconds.
 * An instance of this interface represents the unit itself, whereas {@link Period}
 * represents an amount of the unit.
 * <p>
 * Implementations of this interface may define a unit that is specific to one calendar system
 * or a unit that is descriptive such that it only has meaning when paired with a calendar system.
 * <p>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 */
public interface PeriodUnit {

    /**
     * Gets a descriptive name for the unit.
     * <p>
     * This should be in the plural and mixed case, such as 'Days' or 'Minutes'.
     * 
     * @return the name, not null
     */
    String getName();

    /**
     * Gets the low-level rules that the unit uses.
     * <p>
     * This method is intended for low-level use and frameworks rather than day-to-day coding.
     * Applications should typically use the API of the class that implements this interface.
     * 
     * @return the rules for the unit, not null
     */
    Rules getRules();

    //-----------------------------------------------------------------------
    /**
     * The set of rules that define define how a period unit works.
     * <p>
     * This interface defines the internal calculations necessary to manage a period unit.
     * Applications will primarily deal with {@link PeriodUnit}.
     * Each instance of this interface is implicitly associated with a single period unit.
     * <p>
     * The calculations must succeed leniently wherever possible.
     * Thus, it should be possible to add minutes to a date or months to a time.
     * For example, adding between 24 and 47 standard hours to a date should add 1 standard day.
     * Adding days to a time, or any multiple, such as months or years, should have no-effect.
     * If this lenient approach is not possible then an exception may be thrown.
     * <p>
     * This interface must be implemented with care to ensure other classes operate correctly.
     * All implementations that can be instantiated must be final, immutable and thread-safe.
     */
    public interface Rules {

        /**
         * Adds the amount of the associated unit to the specified date.
         * 
         * @param date  the date to add to, not null
         * @param period  the period of the associated unit to add, positive or negative
         * @return the adjusted date, not null
         * @throws CalendricalException if unable to add
         */
        LocalDate addToDate(LocalDate date, long period);

        /**
         * Adds the amount of the associated unit to the specified time.
         * 
         * @param time  the time to add to, not null
         * @param period  the period of the associated unit to add, positive or negative
         * @return the adjusted time, not null
         * @throws CalendricalException if unable to add
         */
        LocalTime addToTime(LocalTime time, long period);

        /**
         * Adds the amount of the associated unit to the specified date-time.
         * 
         * @param dateTime  the date-time to add to, not null
         * @param period  the period of the associated unit to add, positive or negative
         * @return the adjusted date-time, not null
         * @throws CalendricalException if unable to add
         */
        LocalDateTime addToDateTime(LocalDateTime dateTime, long period);

        //-----------------------------------------------------------------------
        /**
         * Calculates the period in the associated unit between specified dates.
         * <p>
         * The period will be positive if the second date is after the first, and
         * negative if the second date is before the first.
         * 
         * @param date1  the first date, not null
         * @param date2  the second date, not null
         * @return the period between the dates, positive or negative
         * @throws CalendricalException if unable to calculate
         */
        long getPeriodBetweenDates(LocalDate date1, LocalDate date2);

        /**
         * Calculates the period in the associated unit between specified times.
         * <p>
         * The period will be positive if the second time is after the first, and
         * negative if the second time is before the first.
         * 
         * @param time1  the first time, not null
         * @param time2  the second time, not null
         * @return the period between the times, positive or negative
         * @throws CalendricalException if unable to calculate
         */
        long getPeriodBetweenTimes(LocalTime time1, LocalTime time2);

        /**
         * Calculates the period in the associated unit between specified date-times.
         * <p>
         * The period will be positive if the second date-time is after the first, and
         * negative if the second date-time is before the first.
         * 
         * @param dateTime1  the first date-time, not null
         * @param dateTime2  the second date-time, not null
         * @return the period between the date-times, positive or negative
         * @throws CalendricalException if unable to calculate
         */
        long getPeriodBetweenDateTimes(LocalDateTime dateTime1, LocalDateTime dateTime2);

    }

}
