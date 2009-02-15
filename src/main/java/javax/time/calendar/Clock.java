/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.Instant;
import javax.time.TimeSource;
import javax.time.calendar.field.Year;

/**
 * A clock providing access to the current date and time.
 * <p>
 * The Java Time Framework abstracts the concept of the 'current time' into two interfaces
 * - {@link TimeSource} and <code>Clock</code>.
 * The former, this class, provides access to the current instant and
 * is independent of local factors such as time-zone.
 * The latter, <code>Clock</code>, provides access to the current date and
 * time but requires a time-zone.
 * <p>
 * The purpose of this abstraction is to allow alternate time-sources
 * to be plugged in as and when required. Applications use an object to obtain
 * the current time rather than a static method. This simplifies testing.
 * <p>
 * Applications should <i>avoid</i> using the static methods on this class.
 * Instead, they should pass a <code>Clock</code> into any method that requires it.
 * A dependency injection framework is one way to achieve this:
 * <pre>
 * public class MyBean {
 *   private Clock clock;  // dependency inject
 *   ...
 *   public void process(LocalDate eventDate) {
 *     if (eventDate.isBefore(clock.today()) {
 *       ...
 *     }
 *   }
 * }
 * </pre>
 * This approach allows alternate time-source implementations, such as
 * {@link TimeSource#fixed(Instant)} to be used during testing.
 * <p>
 * Although <code>Clock</code> is an abstract class, there will rarely be a
 * need to extend it. If you want to provide your own time-source then you
 * should subclass <code>TimeSource</code> instead of <code>Clock</code>.
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
     * Gets a clock that obtains the current date and time using the system millisecond
     * clock and the default time-zone.
     * <p>
     * The time-source wraps {@link System#currentTimeMillis()}, thus it has
     * at best millisecond resolution.
     * <p>
     * Using this method hard codes a dependency to the default time-zone into your application.
     * It is recommended to avoid this and use a specific time zone whenever possible.
     *
     * @return a clock that uses the system millisecond clock in the specified zone, never null
     */
    public static Clock systemDefaultZone() {
        TimeZone timeZone = TimeZone.timeZone(java.util.TimeZone.getDefault().getID());
        return new TimeSourceClock(TimeSource.system(), timeZone);
    }

    /**
     * Gets a clock that obtains the current date and time using the system millisecond
     * clock and the specified time-zone.
     * <p>
     * The time-source wraps {@link System#currentTimeMillis()}, thus it has
     * at best millisecond resolution.
     *
     * @param timeZone  the time-zone to use to convert to date-times, not null
     * @return a clock that uses the system millisecond clock in the specified zone, never null
     */
    public static Clock system(TimeZone timeZone) {
        ISOChronology.checkNotNull(timeZone, "TimeZone must not be null");
        return new TimeSourceClock(TimeSource.system(), timeZone);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a clock that obtains the current date and time using the specified
     * time-source and default time-zone.
     * <p>
     * Using this method hard codes a dependency to the default time-zone into your application.
     * It is recommended to avoid this and use a specific time zone whenever possible.
     *
     * @param timeSource  the time-source to use to obtain the current time, not null
     * @return a clock that uses the system millisecond clock in the specified zone, never null
     */
    public static Clock clockDefaultZone(TimeSource timeSource) {
        ISOChronology.checkNotNull(timeSource, "TimeSource must not be null");
        TimeZone timeZone = TimeZone.timeZone(java.util.TimeZone.getDefault().getID());
        return new TimeSourceClock(timeSource, timeZone);
    }

    /**
     * Gets a clock that obtains the current date and time using the specified
     * time-source and time-zone.
     *
     * @param timeSource  the time-source to use to obtain the current time, not null
     * @param timeZone  the time-zone to use to convert to date-times, not null
     * @return a clock that uses the system millisecond clock in the specified zone, never null
     */
    public static Clock clock(TimeSource timeSource, TimeZone timeZone) {
        ISOChronology.checkNotNull(timeSource, "TimeSource must not be null");
        ISOChronology.checkNotNull(timeZone, "TimeZone must not be null");
        return new TimeSourceClock(timeSource, timeZone);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor accessible by subclasses.
     */
    protected Clock() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time-source being used to create dates and times.
     * <p>
     * The standard implementation of <code>Clock</code> uses a time-source to
     * provide the current instant. This method returns that time-source.
     * <p>
     * Non-standard implementations may choose to use another means to obtain
     * instants, dates and times, thus this method is allowed to throw
     * <code>UnsupportedOperationException</code>.
     *
     * @return the time-source being used to obtain instants, never null
     * @throws UnsupportedOperationException if the implementation does not support accessing the time-source
     */
    public abstract TimeSource getTimeSource();

    /**
     * Returns a copy of this clock with a different time-source.
     * <p>
     * The standard implementation of <code>Clock</code> uses a time-source to
     * provide the current instant. This method allows that time-source to be changed.
     * <p>
     * Non-standard implementations may choose to use another means to obtain
     * instants, dates and times, thus this method is allowed to throw
     * <code>UnsupportedOperationException</code>.
     *
     * @param timeSource  the time-source to change to, not null
     * @return the new clock with the altered time-source, never null
     * @throws UnsupportedOperationException if the implementation does not support changing the time-source
     */
    public abstract Clock withTimeSource(TimeSource timeSource);

    //-----------------------------------------------------------------------
    /**
     * Gets the time-zone being used to create dates and times.
     * <p>
     * The standard implementation of <code>Clock</code> uses a time-zone to
     * interpret the current instant. This method returns that time-zone.
     * <p>
     * Non-standard implementations may choose to use another means to interpret
     * instants, dates and times, thus this method is allowed to throw
     * <code>UnsupportedOperationException</code>.
     *
     * @return the time-zone being used to interpret instants, never null
     * @throws UnsupportedOperationException if the implementation does not support accessing the time-zone
     */
    public abstract TimeZone getTimeZone();

    /**
     * Returns a copy of this clock with a different time-zone.
     * <p>
     * The standard implementation of <code>Clock</code> uses a time-zone to
     * interpret the current instant. This method allows that time-zone to be changed.
     * <p>
     * Non-standard implementations may choose to use another means to interpret
     * instants, dates and times, thus this method is allowed to throw
     * <code>UnsupportedOperationException</code>.
     *
     * @param timeZone  the time-zone to change to, not null
     * @return the new clock with the altered time zone, never null
     * @throws UnsupportedOperationException if the implementation does not support changing the time-zone
     */
    public abstract Clock withTimeZone(TimeZone timeZone);

    //-----------------------------------------------------------------------
    /**
     * Gets the current instant.
     * <p>
     * The instant returned by this method will vary according to the implementation.
     * For example, the time-source returned by {@link #systemMillis()} will return
     * an instant based on {@link System#currentTimeMillis()}.
     * <p>
     * Normally, this method will not throw an exception.
     * However, one possible implementation would be to obtain the time from a
     * central time server across the network. Obviously, in this case the lookup
     * could fail, and so the method is permitted to throw an exception.
     * <p>
     * The default implementation returns the instant from {@link #getTimeSource()}.
     *
     * @return the current instant from the time-source, never null
     * @throws CalendricalException if the instant cannot be obtained, not thrown by most implementations
     */
    public Instant instant() {
        return getTimeSource().instant();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets today's date.
     * <p>
     * This returns today's date from the clock.
     *
     * @return a date object representing today, never null
     * @throws CalendricalException if the date cannot be created
     */
    public LocalDate today() {
        return offsetDateTime().toLocalDate();
    }

    /**
     * Gets yesterday's date.
     * <p>
     * This returns yesterday's date from the clock.
     *
     * @return a date object representing yesterday, never null
     * @throws CalendricalException if the date cannot be created
     */
    public LocalDate yesterday() {
        return today().minusDays(1);
    }

    /**
     * Gets tomorrow's date.
     * <p>
     * This returns tomorrow's date from the clock.
     *
     * @return a date object representing tomorrow, never null
     * @throws CalendricalException if the date cannot be created
     */
    public LocalDate tomorrow() {
        return today().plusDays(1);
    }

    /**
     * Gets the current year-month.
     * <p>
     * This returns the current year-month from the clock.
     *
     * @return a year object representing the current year, never null
     * @throws CalendricalException if the year cannot be created
     */
    public YearMonth yearMonth() {
        OffsetDateTime now = offsetDateTime();
        return YearMonth.yearMonth(now.getYear(), now.getMonthOfYear());
    }

    /**
     * Gets the current year.
     * <p>
     * This returns the current year from the clock.
     *
     * @return a year object representing the current year, never null
     * @throws CalendricalException if the year cannot be created
     */
    public Year year() {
        return Year.isoYear(offsetDateTime().getYear());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current time with maximum resolution of up to nanoseconds.
     * <p>
     * This returns the current time from the clock.
     * The result is not filtered, and so will have whatever resolution the clock has.
     * For example, the {@link #systemMillis system clock} has up to millisecond resolution.
     *
     * @return a time object representing the current time of day, never null
     * @throws CalendricalException if the time cannot be created
     */
    public LocalTime time() {
        return offsetDateTime().toLocalTime();
    }

    /**
     * Gets the current time with a resolution of seconds.
     * <p>
     * This returns the current time from the clock to the rounded to the second.
     * This is achieved by setting the nanosecond part to be zero.
     *
     * @return a time object representing the current time of day, never null
     * @throws CalendricalException if the time cannot be created
     */
    public LocalTime timeToSecond() {
        return offsetDateTime().toLocalTime().withNanoOfSecond(0);
    }

    /**
     * Gets the current time with a resolution of seconds.
     * <p>
     * This returns the current time from the clock to the rounded to the minute.
     * This is achieved by setting the second and nanosecond parts to be zero.
     *
     * @return a time object representing the current time of day, never null
     * @throws CalendricalException if the time cannot be created
     */
    public LocalTime timeToMinute() {
        return offsetDateTime().toLocalTime().withSecondOfMinute(0).withNanoOfSecond(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current date-time.
     * <p>
     * This returns the current date-time from the clock.
     *
     * @return a date-time object representing the current date and time, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public LocalDateTime dateTime() {
        return offsetDateTime().toLocalDateTime();
    }

    /**
     * Gets the current offset date-time.
     * <p>
     * This returns the current date-time from the clock with the correct offset
     * for the zone returned by {@link #timeZone()}.
     *
     * @return the current zoned date-time, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public OffsetDate offsetDate() {
        return offsetDateTime().toOffsetDate();
    }

    /**
     * Gets the current offset date-time.
     * <p>
     * This returns the current date-time from the clock with the correct offset
     * for the zone returned by {@link #timeZone()}.
     *
     * @return the current zoned date-time, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public OffsetTime offsetTime() {
        return offsetDateTime().toOffsetTime();
    }

    /**
     * Gets the current offset date-time.
     * <p>
     * This returns the current date-time from the clock with the correct offset
     * for the zone returned by {@link #timeZone()}.
     *
     * @return the current zoned date-time, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public OffsetDateTime offsetDateTime() {
        Instant instant = instant();
        return OffsetDateTime.fromInstant(instant, getTimeZone().getOffset(instant));
    }

    /**
     * Gets the current zoned date-time.
     * <p>
     * This returns the current date-time from the clock with the zone
     * returned by {@link #timeZone()}.
     *
     * @return the current zoned date-time, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public ZonedDateTime zonedDateTime() {
        return ZonedDateTime.fromInstant(instant(), getTimeZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a clock based on a time-source.
     */
    private static final class TimeSourceClock extends Clock implements Serializable {
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** The time-source being used. */
        private final TimeSource timeSource;
        /** The time-zone being used. */
        private final TimeZone timeZone;

        /** Restricted constructor. */
        private TimeSourceClock(TimeSource timeSource, TimeZone timeZone) {
            this.timeSource = timeSource;
            this.timeZone = timeZone;
        }
        /** {@inheritDoc} */
        @Override
        public TimeSource getTimeSource() {
            return timeSource;
        }
        /** {@inheritDoc} */
        @Override
        public Clock withTimeSource(TimeSource timeSource) {
            ISOChronology.checkNotNull(timeSource, "TimeSource must not be null");
            if (timeSource.equals(this.timeSource)) {
                return this;
            }
            return new TimeSourceClock(timeSource, timeZone);
        }
        /** {@inheritDoc} */
        @Override
        public TimeZone getTimeZone() {
            return timeZone;
        }
        /** {@inheritDoc} */
        @Override
        public Clock withTimeZone(TimeZone timeZone) {
            ISOChronology.checkNotNull(timeZone, "TimeZone must not be null");
            if (timeZone.equals(this.timeZone)) {
                return this;
            }
            return new TimeSourceClock(timeSource, timeZone);
        }
        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof TimeSourceClock) {
                TimeSourceClock other = (TimeSourceClock) obj;
                return timeSource.equals(other.timeSource) && timeZone.equals(other.timeZone);
            }
            return false;
        }
        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + timeSource.hashCode();
            hash = 41 * hash + timeZone.hashCode();
            return hash;
        }
        @Override
        public String toString() {
            return "TimeSourceClock[" + timeSource + ", " + timeZone + ']';
        }
    }
}
