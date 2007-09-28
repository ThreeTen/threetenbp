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

import javax.time.calendar.CalendarDay;
import javax.time.calendar.CalendarMonth;
import javax.time.calendar.CalendarYear;
import javax.time.calendar.TimeOfDay;

/**
 * A facade for accessing the current time in the Java Time Framework.
 *
 * @author Stephen Colebourne
 */
public abstract class Now {

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>Now</code> that obtains the current datetime
     * using the system millisecond clock - {@link System#currentTimeMillis()}.
     *
     * @return an instance of now that uses the system clock
     */
    public static Now system() {
        return SystemMillis.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>Instant</code> representing the current
     * instant on the time line.
     *
     * @return a Instant represnting the current instant, never null
     */
    public abstract Instant instant();

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of CalendarYear representing the current year
     * using the system clock in the default time zone.
     *
     * @return a year object represnting the current year, never null
     */
    public CalendarYear currentYear() {
        return null; //Year.currentYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of CalendarMonth representing the current month
     * using the system clock in the default time zone.
     *
     * @return a month object represnting the current month, never null
     */
    public CalendarMonth currentMonth() {
        return CalendarMonth.yearMonth(2007, 6);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of CalendarDay representing today
     * using the system clock in the default time zone.
     *
     * @return a day object represnting today, never null
     */
    public CalendarDay today() {
        return CalendarDay.yearMonthDay(2007, 6, 1);
    }

    /**
     * Gets an instance of CalendarDay representing yesterday
     * using the system clock in the default time zone.
     *
     * @return a day object represnting yesterday, never null
     */
    public CalendarDay yesterday() {
        return CalendarDay.yearMonthDay(2007, 6, 1);
    }

    /**
     * Gets an instance of CalendarDay representing tomorrow
     * using the system clock in the default time zone.
     *
     * @return a day object represnting tommorrow, never null
     */
    public CalendarDay tomorrow() {
        return CalendarDay.yearMonthDay(2007, 6, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of TimeOfDay representing the current time of day
     * using the system clock in the default time zone.
     *
     * @return a time object represnting the current time of day, never null
     */
    public TimeOfDay currentTime() {
        return TimeOfDay.timeOfDay(12, 30);
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of Now that always returns the latest time from
     * {@link System#currentTimeMillis()}.
     */
    private static class SystemMillis extends Now {
        /** The singleton instance. */
        private static final SystemMillis INSTANCE = new SystemMillis();

        /** Restricted constructor. */
        private SystemMillis() {
            super();
        }

        /**
         * Gets the current instant.
         * @return the current instant
         */
        @Override
        public Instant instant() {
            return Instant.millisInstant(System.currentTimeMillis());
        }
    }

}
