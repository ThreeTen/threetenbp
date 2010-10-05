/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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

/**
 * A clock providing access to the current date and time.
 * <p>
 * The Time Framework for Java abstracts the concept of the 'current time' into two interfaces
 * - {@link TimeSource} and {@code Clock}.
 * The former, {@code TimeSource}, provides access to the current instant and
 * is independent of local factors such as time-zone.
 * The latter, this class, provides access to the current date and
 * time but requires a time-zone.
 * <p>
 * The purpose of this abstraction is to allow alternate time-sources
 * to be plugged in as and when required. Applications use an object to obtain
 * the current time rather than a static method. This simplifies testing.
 * <p>
 * Applications should <i>avoid</i> using the static methods on this class.
 * Instead, they should pass a {@code Clock} into any method that requires it.
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
 * {@link TimeSource#fixed} to be used during testing.
 *
 * <h4>Implementation notes</h4>
 * {@code Clock} is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 * <p>
 * The class is designed to be subclassed, however this will rarely be necessary.
 * In most cases, you should subclass {@code TimeSource} instead.
 * <p>
 * A subclass will normally override {@code getSource()} and {@code getZone()}.
 * This will cause all the other methods to work as they are derived from just these two.
 * Subclasses should implement {@code withSource()} and {@code withZone()}
 * if possible to allow the user to change the time-source and time-zone.
 * The default implementation of these four methods throws an {@code UnsupportedOperationException}.
 * <p>
 * One reason to subclass this class would be to provide only a hard coded date for testing
 * and not a time or date-time. In this case, the subclass would only override {@code today()}.
 * Other methods would thus throw {@code UnsupportedOperationException}, which would be fine
 * for testing but not recommended for production usage.
 * <p>
 * Subclass implementations should implement {@code Serializable} wherever possible.
 * They should also implement {@code equals()}, {@code hashCode()} and
 * {@code toString()} based on their state.
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
     * It is recommended to avoid this and use a specific time-zone whenever possible.
     *
     * @return a clock that uses the system millisecond clock in the specified zone, never null
     */
    public static Clock systemDefaultZone() {
        TimeZone zone = TimeZone.of(java.util.TimeZone.getDefault().getID());
        return new TimeSourceClock(TimeSource.system(), zone);
    }

    /**
     * Gets a clock that obtains the current date and time using the system millisecond
     * clock and the specified time-zone.
     * <p>
     * The time-source wraps {@link System#currentTimeMillis()}, thus it has
     * at best millisecond resolution.
     *
     * @param zone  the time-zone to use to convert to date-times, not null
     * @return a clock that uses the system millisecond clock in the specified zone, never null
     */
    public static Clock system(TimeZone zone) {
        ISOChronology.checkNotNull(zone, "TimeZone must not be null");
        return new TimeSourceClock(TimeSource.system(), zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a clock that obtains the current date and time using the specified
     * time-source and default time-zone.
     * <p>
     * Using this method hard codes a dependency to the default time-zone into your application.
     * It is recommended to avoid this and use a specific time-zone whenever possible.
     *
     * @param timeSource  the time-source to use to obtain the current time, not null
     * @return a clock that uses the system millisecond clock in the specified zone, never null
     */
    public static Clock clockDefaultZone(TimeSource timeSource) {
        ISOChronology.checkNotNull(timeSource, "TimeSource must not be null");
        TimeZone zone = TimeZone.of(java.util.TimeZone.getDefault().getID());
        return new TimeSourceClock(timeSource, zone);
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
     * The standard implementation of {@code Clock} uses a time-source to
     * provide the current instant. This method returns that time-source.
     * <p>
     * Non-standard implementations may choose to use another means to obtain
     * instants, dates and times, thus this method is allowed to throw
     * {@code UnsupportedOperationException}.
     *
     * @return the time-source being used to obtain instants, never null
     * @throws UnsupportedOperationException if the implementation does not support accessing the time-source
     */
    public TimeSource getSource() {
        throw new UnsupportedOperationException("Clock.getSource is not supported");
    }

    /**
     * Returns a copy of this clock with a different time-source.
     * <p>
     * The standard implementation of {@code Clock} uses a time-source to
     * provide the current instant. This method allows that time-source to be changed.
     * <p>
     * Non-standard implementations may choose to use another means to obtain
     * instants, dates and times, thus this method is allowed to throw
     * {@code UnsupportedOperationException}.
     *
     * @param timeSource  the time-source to change to, not null
     * @return the new clock with the altered time-source, never null
     * @throws UnsupportedOperationException if the implementation does not support changing the time-source
     */
    public Clock withSource(TimeSource timeSource) {
        throw new UnsupportedOperationException("Clock.withSource is not supported");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time-zone being used to create dates and times.
     * <p>
     * The standard implementation of {@code Clock} uses a time-zone to
     * interpret the current instant. This method returns that time-zone.
     * <p>
     * Non-standard implementations may choose to use another means to interpret
     * instants, dates and times, thus this method is allowed to throw
     * {@code UnsupportedOperationException}.
     *
     * @return the time-zone being used to interpret instants, never null
     * @throws UnsupportedOperationException if the implementation does not support accessing the time-zone
     */
    public TimeZone getZone() {
        throw new UnsupportedOperationException("Clock.getZone is not supported");
    }

    /**
     * Returns a copy of this clock with a different time-zone.
     * <p>
     * The standard implementation of {@code Clock} uses a time-zone to
     * interpret the current instant. This method allows that time-zone to be changed.
     * <p>
     * Non-standard implementations may choose to use another means to interpret
     * instants, dates and times, thus this method is allowed to throw
     * {@code UnsupportedOperationException}.
     *
     * @param zone  the time-zone to change to, not null
     * @return the new clock with the altered time-zone, never null
     * @throws UnsupportedOperationException if the implementation does not support changing the time-zone
     */
    public Clock withZone(TimeZone zone) {
        throw new UnsupportedOperationException("Clock.withZone is not supported");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current instant.
     * <p>
     * The instant returned by this method will vary according to the implementation.
     * For example, the time-source returned by {@link #system(TimeZone)} will return
     * an instant based on {@link System#currentTimeMillis()}.
     * <p>
     * Normally, this method will not throw an exception.
     * However, one possible implementation would be to obtain the time from a
     * central time server across the network. Obviously, in this case the lookup
     * could fail, and so the method is permitted to throw an exception.
     *
     * @return the current instant from the time-source, never null
     * @throws CalendricalException if the instant cannot be obtained, not thrown by most implementations
     */
    public Instant instant() {
        return getSource().instant();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets today's date.
     * <p>
     * This returns today's date from the clock.
     * <p>
     * The local date can only be calculated from an instant if the time-zone is known.
     * As such, the local date is derived by default from {@code offsetDate()}.
     *
     * @return the current date, never null
     * @throws CalendricalException if the date cannot be created
     */
    public LocalDate today() {
        return LocalDate.now(this);
    }

    /**
     * Gets yesterday's date.
     * <p>
     * This returns yesterday's date from the clock.
     * This is calculated relative to {@code today()}.
     *
     * @return the date yesterday, never null
     * @throws CalendricalException if the date cannot be created
     */
    public LocalDate yesterday() {
        return today().minusDays(1);
    }

    /**
     * Gets tomorrow's date.
     * <p>
     * This returns tomorrow's date from the clock.
     * This is calculated relative to {@code today()}.
     *
     * @return the date tomorrow, never null
     * @throws CalendricalException if the date cannot be created
     */
    public LocalDate tomorrow() {
        return today().plusDays(1);
    }

    /**
     * Gets the current year-month.
     * <p>
     * This returns the current year-month from the clock.
     * This is derived from {@code today()}.
     *
     * @return the current year-month, never null
     * @throws CalendricalException if the year cannot be created
     */
    public YearMonth yearMonth() {
        LocalDate today = today();
        return YearMonth.of(today.getYear(), today.getMonthOfYear());
    }

    /**
     * Gets the current year.
     * <p>
     * This returns the current year from the clock.
     * This is derived from {@code today()}.
     *
     * @return the current year, never null
     * @throws CalendricalException if the year cannot be created
     */
    public Year year() {
        return today().toYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current time with maximum resolution of up to nanoseconds.
     * <p>
     * This returns the current time from the clock.
     * The result is not filtered, and so will have whatever resolution the clock has.
     * For example, the {@link #system system clock} has up to millisecond resolution.
     * <p>
     * The local time can only be calculated from an instant if the time-zone is known.
     * As such, the local time is derived by default from {@code offsetTime()}.
     *
     * @return the current time, never null
     * @throws CalendricalException if the time cannot be created
     */
    public LocalTime time() {
        return LocalTime.now(this);
    }

    /**
     * Gets the current time with a resolution of seconds.
     * <p>
     * This returns the current time from the clock rounded to the second.
     * This is achieved by setting the nanosecond part to be zero.
     *
     * @return the current time to the nearest second, never null
     * @throws CalendricalException if the time cannot be created
     */
    public LocalTime timeToSecond() {
        return time().withNanoOfSecond(0);
    }

    /**
     * Gets the current time with a resolution of minutes.
     * <p>
     * This returns the current time from the clock rounded to the minute.
     * This is achieved by setting the second and nanosecond parts to be zero.
     *
     * @return the current time to the nearest minute, never null
     * @throws CalendricalException if the time cannot be created
     */
    public LocalTime timeToMinute() {
        return time().withSecondOfMinute(0).withNanoOfSecond(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current date-time with maximum resolution of up to nanoseconds.
     * <p>
     * This returns the current date-time from the clock.
     * The result is not filtered, and so will have whatever resolution the clock has.
     * For example, the {@link #system system clock} has up to millisecond resolution.
     * <p>
     * The local date-time can only be calculated from an instant if the time-zone is known.
     * As such, the local date-time is derived by default from {@code offsetDateTime()}.
     *
     * @return the current date-time, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public LocalDateTime dateTime() {
        return LocalDateTime.now(this);
    }

    /**
     * Gets the current date-time with a resolution of seconds.
     * <p>
     * This returns the current date-time from the clock rounded to the second.
     * This is achieved by setting the nanosecond part to be zero.
     *
     * @return the current date-time to the nearest second, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public LocalDateTime dateTimeToSecond() {
        return dateTime().withNanoOfSecond(0);
    }

    /**
     * Gets the current date-time with a resolution of minutes.
     * <p>
     * This returns the current date-time from the clock rounded to the minute.
     * This is achieved by setting the second and nanosecond parts to be zero.
     *
     * @return the current date-time to the nearest minute, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public LocalDateTime dateTimeToMinute() {
        return dateTime().withSecondOfMinute(0).withNanoOfSecond(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current offset date.
     * <p>
     * This returns the current offset date from the clock with the correct offset from {@link #getZone()}.
     * <p>
     * The offset date is derived by default from {@code instant()} and {@code getZone()}.
     *
     * @return the current offset date, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public OffsetDate offsetDate() {
        return OffsetDate.now(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current offset time with maximum resolution of up to nanoseconds.
     * <p>
     * This returns the current offset time from the clock with the correct offset from {@link #getZone()}.
     * The result is not filtered, and so will have whatever resolution the clock has.
     * For example, the {@link #system system clock} has up to millisecond resolution.
     * <p>
     * The offset time is derived by default from {@code instant()} and {@code getZone()}.
     *
     * @return the current offset time, never null
     * @throws CalendricalException if the time cannot be created
     */
    public OffsetTime offsetTime() {
        return OffsetTime.now(this);
    }

    /**
     * Gets the current offset time with a resolution of seconds.
     * <p>
     * This returns the current offset time from the clock with the correct offset from {@link #getZone()}.
     * The time is rounded to the second by setting the nanosecond part to be zero.
     *
     * @return the current offset time to the nearest second, never null
     * @throws CalendricalException if the time cannot be created
     */
    public OffsetTime offsetTimeToSecond() {
        return offsetTime().withNanoOfSecond(0);
    }

    /**
     * Gets the current offset time with a resolution of minutes.
     * <p>
     * This returns the current offset time from the clock with the correct offset from {@link #getZone()}.
     * The time is rounded to the second by setting the second and nanosecond parts to be zero.
     *
     * @return the current offset time to the nearest minute, never null
     * @throws CalendricalException if the time cannot be created
     */
    public OffsetTime offsetTimeToMinute() {
        return offsetTime().withSecondOfMinute(0).withNanoOfSecond(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current offset date-time with maximum resolution of up to nanoseconds.
     * <p>
     * This returns the current offset date-time from the clock with the correct offset from {@link #getZone()}.
     * The result is not filtered, and so will have whatever resolution the clock has.
     * For example, the {@link #system system clock} has up to millisecond resolution.
     * <p>
     * The offset date-time is derived by default from {@code instant()} and {@code getZone()}.
     *
     * @return the current offset date-time, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public OffsetDateTime offsetDateTime() {
        return OffsetDateTime.now(this);
    }

    /**
     * Gets the current offset date-time with a resolution of seconds.
     * <p>
     * This returns the current offset date-time from the clock with the correct offset from {@link #getZone()}.
     * The time is rounded to the second by setting the nanosecond part to be zero.
     *
     * @return the current offset date-time to the nearest second, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public OffsetDateTime offsetDateTimeToSecond() {
        return offsetDateTime().withNanoOfSecond(0);
    }

    /**
     * Gets the current offset date-time with a resolution of minutes.
     * <p>
     * This returns the current offset date-time from the clock with the correct offset from {@link #getZone()}.
     * The time is rounded to the second by setting the second and nanosecond parts to be zero.
     *
     * @return the current offset date-time to the nearest minute, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public OffsetDateTime offsetDateTimeToMinute() {
        return offsetDateTime().withSecondOfMinute(0).withNanoOfSecond(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current zoned date-time.
     * <p>
     * This returns the current zoned date-time from the clock with the zone from {@link #getZone()}.
     * The result is not filtered, and so will have whatever resolution the clock has.
     * For example, the {@link #system system clock} has up to millisecond resolution.
     * <p>
     * The zoned date-time is derived by default from {@code instant()} and {@code getZone()}.
     *
     * @return the current zoned date-time, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public ZonedDateTime zonedDateTime() {
        return ZonedDateTime.now(this);
    }

    /**
     * Gets the current zoned date-time with a resolution of seconds.
     * <p>
     * This returns the current zoned date-time from the clock with the zone from {@link #getZone()}.
     * The time is rounded to the second by setting the nanosecond part to be zero.
     *
     * @return the current zoned date-time to the nearest second, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public ZonedDateTime zonedDateTimeToSecond() {
        return zonedDateTime().withNanoOfSecond(0);
    }

    /**
     * Gets the current zoned date-time with a resolution of minutes.
     * <p>
     * This returns the current zoned date-time from the clock with the zone from {@link #getZone()}.
     * The time is rounded to the second by setting the second and nanosecond parts to be zero.
     *
     * @return the current zoned date-time to the nearest minute, never null
     * @throws CalendricalException if the date-time cannot be created
     */
    public ZonedDateTime zonedDateTimeToMinute() {
        return zonedDateTime().withSecondOfMinute(0).withNanoOfSecond(0);
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
        private final TimeZone zone;

        /** Restricted constructor. */
        private TimeSourceClock(TimeSource timeSource, TimeZone zone) {
            this.timeSource = timeSource;
            this.zone = zone;
        }

        /** {@inheritDoc} */
        @Override
        public TimeSource getSource() {
            return timeSource;
        }

        /** {@inheritDoc} */
        @Override
        public Clock withSource(TimeSource timeSource) {
            ISOChronology.checkNotNull(timeSource, "TimeSource must not be null");
            if (timeSource.equals(this.timeSource)) {
                return this;
            }
            return new TimeSourceClock(timeSource, zone);
        }

        /** {@inheritDoc} */
        @Override
        public TimeZone getZone() {
            return zone;
        }

        /** {@inheritDoc} */
        @Override
        public Clock withZone(TimeZone zone) {
            ISOChronology.checkNotNull(zone, "TimeZone must not be null");
            if (zone.equals(this.zone)) {
                return this;
            }
            return new TimeSourceClock(timeSource, zone);
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof TimeSourceClock) {
                TimeSourceClock other = (TimeSourceClock) obj;
                return timeSource.equals(other.timeSource) && zone.equals(other.zone);
            }
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + timeSource.hashCode();
            hash = 41 * hash + zone.hashCode();
            return hash;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "TimeSourceClock[" + timeSource + ", " + zone + ']';
        }
    }
}
