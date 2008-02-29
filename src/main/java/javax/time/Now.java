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
package javax.time;

import java.io.Serializable;

import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.YearMonth;
import javax.time.calendar.ZonedDateTime;
import javax.time.calendar.field.Year;

/**
 * A facade for accessing the current time in the Java Time Framework.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class Now {
    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>Now</code> that obtains the current datetime
     * using the system millisecond clock - {@link System#currentTimeMillis()}
     * and the default time zone - {@link #timeZone()}.
     * 
     * All objects produced by this implementation have at best millisecond 
     * precision, since this is the maximum precision supported by 
     * <code>System</code>.
     *
     * @return an instance of now that uses the system clock in the default time zone
     */
    public static Now system() {
        return SystemMillis.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>Instant</code> representing the current
     * instant on the time line.
     * 
     * The instant precision depends on the underlying implementation.
     * 
     * @return a <code>Instant</code> representing the current instant, never null
     */
    public abstract Instant instant();

    //-----------------------------------------------------------------------
    /**
     * Gets the <code>TimeZone</code> instance used to produce <code>ZonedDateTime</code> instances.
     * 
     * @return the <code>TimeZone</code>
     * @see #currentZonedDateTime()
     * @see #setTimeZone(TimeZone)
     */
    public abstract TimeZone timeZone();

    //-----------------------------------------------------------------------
    /**
     * Sets the <code>TimeZone</code> instance used to produce <code>ZonedDateTime</code> instances.
     * 
     * @param timeZone the new <code>TimeZone</code> instance, must not be null
     * @throws IllegalArgumentException if <code>timeZone</code> is null
     */
    //TODO: Create a permission class and require it
    public abstract void setTimeZone(TimeZone timeZone);

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>Year</code> representing the current year.
     *
     * @return a year object representing the current year, never null
     */
    public Year currentYear() {
        return Year.isoYear(2007);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>YearMonth</code> representing the current month.
     *
     * @return a month object representing the current month, never null
     */
    public YearMonth currentMonth() {
        return YearMonth.yearMonth(2007, 6);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>LocalDate</code> representing today.
     *
     * @return a day object representing today, never null
     */
    public LocalDate today() {
        return LocalDate.date(2007, 6, 1);
    }

    /**
     * Gets an instance of <code>LocalDate</code> representing yesterday.
     *
     * @return a day object representing yesterday, never null
     */
    public LocalDate yesterday() {
        return LocalDate.date(2007, 6, 1);
    }

    /**
     * Gets an instance of <code>LocalDate</code> representing tomorrow.
     *
     * @return a day object representing tommorrow, never null
     */
    public LocalDate tomorrow() {
        return LocalDate.date(2007, 6, 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>LocalTime</code> representing the current time of day.
     *
     * @return a time object representing the current time of day, never null
     */
    public LocalTime currentTime() {
        return LocalTime.time(12, 30);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>LocalDateTime</code> representing the current date and time.
     *
     * @return a date-time object representing the current date and time, never null
     */
    public LocalDateTime currentDateTime() {
        return LocalDateTime.dateTime(2007, 6, 1, 12, 30);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>ZonedDateTime</code> representing date and time in the default <code>TimeZone</code>.
     * 
     * It is a shortcut for <code>ZonedDateTime.dateTime(now.instant(), now.timeZone())</code> for this <code>Now</code> instance.
     *
     * @return a zoned date-time object representing date and time in the default <code>TimeZone</code>, never null
     * @see #timeZone()
     */
    public ZonedDateTime currentZonedDateTime() {
        return ZonedDateTime.dateTime(instant(), timeZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of Now that always returns the latest time from
     * {@link System#currentTimeMillis()}.
     */
    private static final class SystemMillis extends Now implements Serializable {
        /**
         * The singleton instance.
         */
        private static final SystemMillis INSTANCE = new SystemMillis();
        /**
         * A serialization identifier for this class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The default <code>TimeZone</code>.
         */
        //TODO: Proper initialization
        private transient TimeZone timeZone = TimeZone.UTC;

        /**
         * Restricted constructor.
         */
        private SystemMillis() {
            super();
        }

        /**
         * Resolves singleton.
         * @return the singleton instance
         */
        private Object readResolve() {
            return INSTANCE;
        }

        /**
         * Gets the current instant.
         * @return the current instant
         */
        @Override
        public Instant instant() {
            return Instant.millisInstant(System.currentTimeMillis());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TimeZone timeZone() {
            return timeZone;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
        }
    }
}
