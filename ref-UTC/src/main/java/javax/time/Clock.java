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
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZonedDateTime;
import javax.time.calendar.field.Year;

/**
 * A facade for accessing the current time in the Java Time Framework.
 * <p>
 * Clock is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class Clock {

    /**
     * Gets an instance of <code>Clock</code> that obtains the current instant
     * using the system millisecond clock - {@link System#currentTimeMillis()}
     * and the default time zone.
     * <p>
     * All objects produced by this implementation have at best millisecond
     * precision, since this is the maximum precision supported by
     * <code>System</code>.
     *
     * @return an instance of <code>Clock</code> that uses the system clock in the default time zone
     */
    public static Clock system() {
        String id = java.util.TimeZone.getDefault().getID();
        return system(TimeZone.timeZone(id));
    }

    /**
     * Gets an instance of <code>Clock</code> that obtains the current datetime
     * using the system millisecond clock - {@link System#currentTimeMillis()}
     * and the specified <code>timeZone</code>.
     * <p>
     * All objects produced by this implementation have at best millisecond
     * precision, since this is the maximum precision supported by
     * <code>System</code>.
     *
     * @param timeZone a <code>TimeZone</code> instance used to create <code>ZonedDateTime</code> instances, never null
     * @return an instance of <code>Clock</code> that uses the system clock and the specified <code>TimeZone</code>
     * @throws NullPointerException if <code>timeZone</code> is null
     */
    public static Clock system(TimeZone timeZone) {
        if (timeZone == null) {
            throw new NullPointerException("timeZone must not be null");
        }
        return new SystemMillis(timeZone);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>Instant</code> representing the current
     * instant on the time line.
     * <p>
     * The instant precision depends on the underlying implementation.
     *
     * @return a <code>Instant</code> representing the current instant, never null
     */
    public abstract Instant instant();

    /**
     * Gets the <code>TimeZone</code> instance used to produce <code>ZonedDateTime</code> instances.
     *
     * @return the <code>TimeZone</code>
     */
    public abstract TimeZone timeZone();

    //-----------------------------------------------------------------------
    /**
     * Gets the current year as an instance of <code>Year</code>.
     * <p>
     * This returns the year using the time zone returned by {@link #timeZone()}.
     *
     * @return a year object representing the current year, never null
     * @throws CalendricalException if the instant is outside the supported range of years
     */
    public Year currentYear() {
        return today().getYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets today's date as an instance of <code>LocalDate</code>.
     * <p>
     * This returns today's date using the time zone returned by {@link #timeZone()}.
     *
     * @return a date object representing today, never null
     * @throws CalendricalException if the instant is outside the supported range of years
     */
    public LocalDate today() {
        return currentOffsetDateTime().toLocalDate();
    }

    /**
     * Gets yesterday's date as an instance of <code>LocalDate</code>.
     * <p>
     * This returns yesterday's date using the time zone returned by {@link #timeZone()}.
     *
     * @return a date object representing yesterday, never null
     * @throws CalendricalException if the instant is outside the supported range of years
     */
    public LocalDate yesterday() {
        return today().minusDays(1);
    }

    /**
     * Gets tomorrow's date as an instance of <code>LocalDate</code>.
     * <p>
     * This returns tomorrow's date using the time zone returned by {@link #timeZone()}.
     *
     * @return a date object representing tommorrow, never null
     * @throws CalendricalException if the instant is outside the supported range of years
     */
    public LocalDate tomorrow() {
        return today().plusDays(1);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current time as an instance of <code>LocalTime</code>.
     * <p>
     * This returns the current time in the time zone returned by {@link #timeZone()}.
     *
     * @return a time object representing the current time of day, never null
     * @throws CalendricalException if the instant is outside the supported range of years
     */
    public LocalTime currentTime() {
        return currentOffsetDateTime().toLocalTime();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current date-time as an instance of <code>LocalDateTime</code>.
     * <p>
     * This returns the current date-time in the time zone returned by {@link #timeZone()}.
     *
     * @return a date-time object representing the current date and time, never null
     * @throws CalendricalException if the instant is outside the supported range of years
     */
    public LocalDateTime currentDateTime() {
        return currentOffsetDateTime().toLocalDateTime();
    }

    /**
     * Gets the current date-time with an offset as an instance of <code>OffsetDateTime</code>.
     * <p>
     * This method uses the {@link #instant()} and {@link #timeZone()} methods
     * to create an {@link OffsetDateTime}. The offset will be the correct
     * offset for the time zone and instant.
     *
     * @return the current zoned date-time, never null
     * @throws CalendricalException if the instant is outside the supported range of years
     */
    public OffsetDateTime currentOffsetDateTime() {
        Instant instant = instant();
        return OffsetDateTime.dateTime(instant, timeZone().getOffset(instant));
    }

    /**
     * Gets the current date-time with a time zone as an instance of <code>ZonedDateTime</code>.
     * <p>
     * This method uses the {@link #instant()} and {@link #timeZone()} methods
     * to create a {@link ZonedDateTime}.
     *
     * @return the current zoned date-time, never null
     * @throws CalendricalException if the instant is outside the supported range of years
     */
    public ZonedDateTime currentZonedDateTime() {
        return ZonedDateTime.dateTime(instant(), timeZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of Clock that always returns the latest time from
     * {@link System#currentTimeMillis()}.
     */
    private static final class SystemMillis extends Clock implements Serializable {
        /**
         * A serialization identifier for this class.
         */
        private static final long serialVersionUID = 1L;
        /**
         * The <code>TimeZone</code> instance used by this clock.
         */
        private final TimeZone timeZone;

        /**
         * Restricted constructor.
         * @param timeZone TimeZone to use for this clock
         */
        SystemMillis(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        /** {@inheritDoc} */
        @Override
        public Instant instant() {
            return Instant.millisInstant(System.currentTimeMillis());
        }

        /** {@inheritDoc} */
        @Override
        public TimeZone timeZone() {
            return timeZone;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SystemMillis)) {
                return false;
            }
            final SystemMillis other = (SystemMillis) obj;
            if (timeZone != other.timeZone && !timeZone.equals(other.timeZone)) {
                return false;
            }
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + timeZone.hashCode();
            return hash;
        }
    }
}
