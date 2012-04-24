/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.TimeZone;

/**
 * A clock providing access to the current instant, date and time using a time-zone.
 * <p>
 * Instances of this class are used to find the current instant, which can be
 * interpreted using the stored time-zone to find the current date and time.
 * As such, a clock can be used instead of {@link System#currentTimeMillis()}
 * and {@link TimeZone#getDefault()}.
 * <p>
 * The primary purpose of this abstraction is to allow alternate clocks to be
 * plugged in as and when required. Applications use an object to obtain the
 * current time rather than a static method. This can simplify testing.
 * <p>
 * Applications should <i>avoid</i> using the static methods on this class.
 * Instead, they should pass a {@code Clock} into any method that requires it.
 * A dependency injection framework is one way to achieve this:
 * <pre>
 * public class MyBean {
 *   private Clock clock;  // dependency inject
 *   ...
 *   public void process(LocalDate eventDate) {
 *     if (eventDate.isBefore(LocalDate.now(clock)) {
 *       ...
 *     }
 *   }
 * }
 * </pre>
 * This approach allows an alternate clock, such as {@link #fixed} to be used during testing.
 * <p>
 * The {@code system} factory method provides clocks based on the best available system clock,
 * such as {@code System.currentTimeMillis}.
 *
 * <h4>Implementation notes</h4>
 * This abstract class must be implemented with care to ensure other operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * <p>
 * The principal methods are defined to allow the throwing of an exception.
 * In normal use, no exceptions will be thrown, however one possible implementation would be to
 * obtain the time from a central time server across the network. Obviously, in this case the
 * lookup could fail, and so the method is permitted to throw an exception.
 * <p>
 * The returned instants from {@code Clock} work on a time-scale that ignores leap seconds.
 * If the implementation wraps a source that provides leap second information, then a mechanism
 * should be used to "smooth" the leap second, such as UTC-SLS.
 * <p>
 * Subclass implementations should implement {@code Serializable} wherever possible.
 * They should also be immutable and thread-safe, implementing {@code equals()},
 * {@code hashCode()} and {@code toString()} based on their state.
 */
public abstract class Clock {

    /**
     * Gets a clock that obtains the current instant using the best available system clock,
     * converting to date and time using the UTC time-zone.
     * <p>
     * This clock, rather than the {@link #systemDefaultZone() default zone clock}, should
     * be used when you need the current instant without the date or time.
     * <p>
     * The clock is based on the best available system clock from the JDK.
     * This may use {@link System#currentTimeMillis()}, or a higher resolution clock if
     * one is available.
     * <p>
     * Conversion from instant to date or time uses the {@link ZoneId#UTC UTC time-zone}.
     * <p>
     * The returned implementation is immutable, thread-safe and {@code Serializable}.
     *
     * @return a clock that uses the best available system clock in the UTC zone, not null
     */
    public static Clock systemUTC() {
        return new SystemClock(ZoneId.UTC);
    }

    /**
     * Gets a clock that obtains the current date and time using best available system clock.
     * <p>
     * The clock is based on the best available system clock from the JDK.
     * This may use {@link System#currentTimeMillis()}, or a higher resolution clock if
     * one is available.
     * <p>
     * Conversion from instant to date or time uses the specified time-zone.
     * <p>
     * The returned implementation is immutable, thread-safe and {@code Serializable}.
     *
     * @param zone  the time-zone to use to convert the instant to date-time, not null
     * @return a clock that uses the best available system clock in the specified zone, not null
     */
    public static Clock system(ZoneId zone) {
        DateTimes.checkNotNull(zone, "ZoneId must not be null");
        return new SystemClock(zone);
    }

    /**
     * Gets a clock using the default time-zone and the best available system clock.
     * <p>
     * The clock is based on the best available system clock from the JDK.
     * This may use {@link System#currentTimeMillis()}, or a higher resolution clock if
     * one is available.
     * <p>
     * Using this method hard codes a dependency to the default time-zone into your application.
     * It is recommended to avoid this and use a specific time-zone whenever possible.
     * The {@link #systemUTC() UTC clock} should be used when you need the current instant
     * without the date or time.
     * <p>
     * The returned implementation is immutable, thread-safe and {@code Serializable}.
     *
     * @return a clock that uses the best available system clock in the default zone, not null
     * @see ZoneId#systemDefault()
     */
    public static Clock systemDefaultZone() {
        return new SystemClock(ZoneId.systemDefault());
    }

    //-------------------------------------------------------------------------
    /**
     * Gets a clock that obtains the current date and time ticking in whole seconds.
     * <p>
     * This clock will always have the nano-of-second field set to zero.
     * This ensures that the visible time ticks in whole seconds.
     * The underlying clock is the best available system clock, typically equivalent
     * to {@link #system(ZoneId)}.
     * <p>
     * The returned implementation is immutable, thread-safe and {@code Serializable}.
     *
     * @param zone  the time-zone to use to convert the instant to date-time, not null
     * @return a clock that ticks in whole seconds using the specified zone, not null
     */
    public static Clock tickSeconds(ZoneId zone) {
        return new TickClock(system(zone), 1000);
    }

    /**
     * Gets a clock that obtains the current date and time ticking in whole minutes.
     * <p>
     * This clock will always have the nano-of-second and second-of-minute fields set to zero.
     * This ensures that the visible time ticks in whole minutes.
     * The underlying clock is the best available system clock, typically equivalent
     * to {@link #system(ZoneId)}.
     * <p>
     * The returned implementation is immutable, thread-safe and {@code Serializable}.
     *
     * @param zone  the time-zone to use to convert the instant to date-time, not null
     * @return a clock that ticks in whole minutes using the specified zone, not null
     */
    public static Clock tickMinutes(ZoneId zone) {
        return new TickClock(system(zone), 60 * 1000);
    }

    /**
     * Gets a clock that obtains the current date and time to the nearest occurrence of the specified duration.
     * <p>
     * This clock will only tick as per the specified duration. Thus, if the duration
     * is half a second, the clock will return instants truncated to the half second.
     * <p>
     * Implementations may use a caching strategy for performance reasons. As such,
     * it is possible that the start of the minute observed via this clock will be
     * later than that observed directly via the underlying clock.
     * <p>
     * The returned implementation is immutable, thread-safe and {@code Serializable}
     * providing that the base clock is.
     *
     * @param baseClock  the base clock to base the ticking clock on, not null
     * @param tickDuration  the duration of each visible tick, not negative, not null
     * @return a clock that ticks in whole units of the duration, not null
     * @throws IllegalArgumentException if the duration is negative
     */
    public static Clock tick(Clock baseClock, Duration tickDuration) {
        DateTimes.checkNotNull(baseClock, "Clock must not be null");
        DateTimes.checkNotNull(tickDuration, "Duration must not be null");
        if (tickDuration.isNegative()) {
            throw new IllegalArgumentException("Duration must not be negative");
        }
        if (tickDuration.isZero()) {
            return baseClock;
        }
        return new TickClock(baseClock, tickDuration.toMillisLong());  // TODO only millis?
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a clock that always returns the same instant in the UTC time-zone.
     * <p>
     * This clock simply returns the specified instant.
     * As such, it is not a clock in the conventional sense.
     * The main use case for this is in testing, where the fixed clock ensures tests
     * are not dependent on the current clock.
     * <p>
     * The returned implementation is immutable, thread-safe and {@code Serializable}.
     *
     * @param fixedInstant  the instant to use as the clock, not null
     * @return a clock that always returns the same instant, not null
     */
    public static Clock fixedUTC(Instant fixedInstant) {
        DateTimes.checkNotNull(fixedInstant, "Instant must not be null");
        return new FixedClock(fixedInstant, ZoneId.UTC);
    }

    /**
     * Gets a clock that always returns the same instant.
     * <p>
     * This clock simply returns the specified instant.
     * As such, it is not a clock in the conventional sense.
     * The main use case for this is in testing, where the fixed clock ensures tests
     * are not dependent on the current clock.
     * <p>
     * The returned implementation is immutable, thread-safe and {@code Serializable}.
     *
     * @param fixedInstant  the instant to use as the clock, not null
     * @param zone  the time-zone to use to convert the instant to date-time, not null
     * @return a clock that always returns the same instant, not null
     */
    public static Clock fixed(Instant fixedInstant, ZoneId zone) {
        DateTimes.checkNotNull(fixedInstant, "Instant must not be null");
        DateTimes.checkNotNull(zone, "ZoneId must not be null");
        return new FixedClock(fixedInstant, zone);
    }

    //-------------------------------------------------------------------------
    /**
     * Gets a clock that is offset from the instant returned by the specified base clock.
     * <p>
     * This clock wraps another clock, returning instants that are offset by the specified duration.
     * The main use case for this is to simulate running in the future or in the past.
     * <p>
     * The returned implementation is immutable, thread-safe and {@code Serializable}
     * providing that the base clock is.
     *
     * @param baseClock  the base clock to add an offset to, not null
     * @param offset  the duration to add as an offset, not null
     * @return a {@code TimeSource} that is offset from the system millisecond clock, not null
     */
    public static Clock offset(Clock baseClock, Duration offset) {
        DateTimes.checkNotNull(baseClock, "Clock must not be null");
        DateTimes.checkNotNull(offset, "Duration must not be null");
        if (offset.equals(Duration.ZERO)) {
            return baseClock;
        }
        return new OffsetClock(baseClock, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor accessible by subclasses.
     */
    protected Clock() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time-zone being used to create dates and times.
     * <p>
     * A clock will typically obtain the current instant and then convert that
     * to a date or time using a time-zone. This method returns the time-zone used.
     *
     * @return the time-zone being used to interpret instants, not null
     */
    public abstract ZoneId getZone();

    /**
     * Returns a copy of this clock with a different time-zone.
     * <p>
     * A clock will typically obtain the current instant and then convert that
     * to a date or time using a time-zone. This method returns a new clock that
     * uses a different time-zone.
     *
     * @param zone  the time-zone to change to, not null
     * @return the new clock with the altered time-zone, not null
    */
    public abstract Clock withZone(ZoneId zone);

    //-------------------------------------------------------------------------
    /**
     * Gets the current millisecond instant of the clock.
     * <p>
     * This returns the millisecond-based instant, measured from 1970-01-01T00:00 UTC.
     * This is equivalent to the definition of {@link System#currentTimeMillis()}.
     * <p>
     * Most applications should avoid this method and use {@link Instant} to represent
     * an instant on the time-line rather than a raw millisecond value.
     * This method is provided to allow the use of the clock in high performance use cases
     * where the creation of an object would be unacceptable.
     *
     * @return the current millisecond instant from this clock, measured from
     *  the Java epoch of 1970-01-01T00:00 UTC, not null
     * @throws CalendricalException if the instant cannot be obtained, not thrown by most implementations
     */
    public abstract long millis();

    //-----------------------------------------------------------------------
    /**
     * Gets the current instant of the clock.
     * <p>
     * This returns an instant representing the current instant as defined by the clock.
     * <p>
     * The default implementation calls {@link #millis}.
     *
     * @return the current instant from this clock, not null
     * @throws CalendricalException if the instant cannot be obtained, not thrown by most implementations
     */
    public Instant instant() {
        return Instant.ofEpochMilli(millis());
    }

//    //-----------------------------------------------------------------------
//    // TODO methods below here offer opportunity for performance gains
//    // need ZoneRules.getOffset(long epSecs)
//    /**
//     * Gets today's date.
//     * <p>
//     * This returns today's date based on the current instant and time-zone.
//     *
//     * @return the current date, not null
//     * @throws CalendricalException if the date cannot be obtained, not thrown by most implementations
//     */
//    public LocalDate today() {
//        return LocalDate.now(this);
////        long epSecs = MathUtils.floorDiv(millis(), 1000);
////        long offsetSecs = getZone().getRules().getOffset(epSecs);
////        long localSecs = MathUtils.safeAdd(epSecs, offsetSecs);
////        long epDay = MathUtils.floorDiv(localSecs, MathUtils.SECONDS_PER_DAY);
////        return LocalDate.ofEpochDay(epDay);
//    }
//
//    /**
//     * Gets yesterday's date.
//     * <p>
//     * This returns yesterday's date from the clock.
//     * This is calculated relative to {@code today()}.
//     *
//     * @return the date yesterday, not null
//     * @throws CalendricalException if the date cannot be created
//     */
//    public LocalDate yesterday() {
//        return today().minusDays(1);
//    }
//
//    /**
//     * Gets tomorrow's date.
//     * <p>
//     * This returns tomorrow's date from the clock.
//     * This is calculated relative to {@code today()}.
//     *
//     * @return the date tomorrow, not null
//     * @throws CalendricalException if the date cannot be created
//     */
//    public LocalDate tomorrow() {
//        return today().plusDays(1);
//    }
//
//    /**
//     * Gets the current time with maximum resolution of up to nanoseconds.
//     * <p>
//     * This returns the current time from the clock.
//     * The result is not filtered, and so will have whatever resolution the clock has.
//     * For example, the {@link #system system clock} has up to millisecond resolution.
//     * <p>
//     * The local time can only be calculated from an instant if the time-zone is known.
//     * As such, the local time is derived by default from {@code offsetTime()}.
//     *
//     * @return the current time, not null
//     * @throws CalendricalException if the time cannot be obtained, not thrown by most implementations
//     */
//    public LocalTime localTime() {
//        return LocalTime.now(this);
//    }
//
//    /**
//     * Gets the current date-time with maximum resolution of up to nanoseconds.
//     * <p>
//     * This returns the current date-time from the clock.
//     * The result is not filtered, and so will have whatever resolution the clock has.
//     * For example, the {@link #system system clock} has up to millisecond resolution.
//     * <p>
//     * The local date-time can only be calculated from an instant if the time-zone is known.
//     * As such, the local date-time is derived by default from {@code offsetDateTime()}.
//     *
//     * @return the current date-time, not null
//     * @throws CalendricalException if the date-time cannot be obtained, not thrown by most implementations
//     */
//    public LocalDateTime localDateTime() {
//        return LocalDateTime.now(this);
//    }
//
//    /**
//     * Gets the current offset date.
//     * <p>
//     * This returns the current offset date from the clock with the correct offset from {@link #getZone()}.
//     * <p>
//     * The offset date is derived by default from {@code instant()} and {@code getZone()}.
//     *
//     * @return the current offset date, not null
//     * @throws CalendricalException if the date-time cannot be created
//     */
//    public OffsetDate offsetDate() {
//        return OffsetDate.now(this);
//    }
//
//    /**
//     * Gets the current offset time with maximum resolution of up to nanoseconds.
//     * <p>
//     * This returns the current offset time from the clock with the correct offset from {@link #getZone()}.
//     * The result is not filtered, and so will have whatever resolution the clock has.
//     * For example, the {@link #system system clock} has up to millisecond resolution.
//     * <p>
//     * The offset time is derived by default from {@code instant()} and {@code getZone()}.
//     *
//     * @return the current offset time, not null
//     * @throws CalendricalException if the time cannot be created
//     */
//    public OffsetTime offsetTime() {
//        return OffsetTime.now(this);
//    }
//
//    /**
//     * Gets the current offset date-time with maximum resolution of up to nanoseconds.
//     * <p>
//     * This returns the current offset date-time from the clock with the correct offset from {@link #getZone()}.
//     * The result is not filtered, and so will have whatever resolution the clock has.
//     * For example, the {@link #system system clock} has up to millisecond resolution.
//     * <p>
//     * The offset date-time is derived by default from {@code instant()} and {@code getZone()}.
//     *
//     * @return the current offset date-time, not null
//     * @throws CalendricalException if the date-time cannot be obtained, not thrown by most implementations
//     */
//    public OffsetDateTime offsetDateTime() {
//        return OffsetDateTime.now(this);
//    }
//
//    /**
//     * Gets the current zoned date-time.
//     * <p>
//     * This returns the current zoned date-time from the clock with the zone from {@link #getZone()}.
//     * The result is not filtered, and so will have whatever resolution the clock has.
//     * For example, the {@link #system system clock} has up to millisecond resolution.
//     * <p>
//     * The zoned date-time is derived by default from {@code instant()} and {@code getZone()}.
//     *
//     * @return the current zoned date-time, not null
//     * @throws CalendricalException if the date-time cannot be obtained, not thrown by most implementations
//     */
//    public ZonedDateTime zonedDateTime() {
//        return ZonedDateTime.now(this);
//    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a clock that always returns the latest time from
     * {@link System#currentTimeMillis()}.
     */
    static final class SystemClock extends Clock implements Serializable {
        private static final long serialVersionUID = 1L;
        private final ZoneId zone;

        SystemClock(ZoneId zone) {
            this.zone = zone;
        }
        @Override
        public ZoneId getZone() {
            return zone;
        }
        @Override
        public Clock withZone(ZoneId zone) {
            if (zone.equals(this.zone)) {  // intentional NPE
                return this;
            }
            return new SystemClock(zone);
        }
        @Override
        public long millis() {
            return System.currentTimeMillis();
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SystemClock) {
                return zone.equals(((SystemClock) obj).zone);
            }
            return false;
        }
        @Override
        public int hashCode() {
            return zone.hashCode() + 1;
        }
        @Override
        public String toString() {
            return "SystemClock[" + zone + "]";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a clock that always returns the same instant.
     * This is typically used for testing.
     */
    static final class FixedClock extends Clock implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Instant instant;
        private final ZoneId zone;

        FixedClock(Instant fixedInstant, ZoneId zone) {
            this.instant = fixedInstant;
            this.zone = zone;
        }
        @Override
        public ZoneId getZone() {
            return zone;
        }
        @Override
        public Clock withZone(ZoneId zone) {
            if (zone.equals(this.zone)) {  // intentional NPE
                return this;
            }
            return new FixedClock(instant, zone);
        }
        @Override
        public long millis() {
            return instant.toEpochMilli();
        }
        @Override
        public Instant instant() {
            return instant;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FixedClock) {
                FixedClock other = (FixedClock) obj;
                return instant.equals(other.instant) && zone.equals(other.zone);
            }
            return false;
        }
        @Override
        public int hashCode() {
            return instant.hashCode() ^ zone.hashCode();
        }
        @Override
        public String toString() {
            return "FixedClock[" + instant + "," + zone + "]";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a clock that adds an offset to an underlying clock.
     */
    static final class OffsetClock extends Clock implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Clock baseClock;
        private final Duration offset;

        OffsetClock(Clock baseClock, Duration offset) {
            this.baseClock = baseClock;
            this.offset = offset;
        }
        @Override
        public ZoneId getZone() {
            return baseClock.getZone();
        }
        @Override
        public Clock withZone(ZoneId zone) {
            if (zone.equals(baseClock.getZone())) {  // intentional NPE
                return this;
            }
            return new OffsetClock(baseClock.withZone(zone), offset);
        }
        @Override
        public long millis() {
            return DateTimes.safeAdd(baseClock.millis(), offset.toMillisLong());
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OffsetClock) {
                OffsetClock other = (OffsetClock) obj;
                return baseClock.equals(other.baseClock) && offset.equals(other.offset);
            }
            return false;
        }
        @Override
        public int hashCode() {
            return baseClock.hashCode() ^ offset.hashCode();
        }
        @Override
        public String toString() {
            return "OffsetClock[" + baseClock + "," + offset + "]";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a clock that adds an offset to an underlying clock.
     */
    static final class TickClock extends Clock implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Clock baseClock;
        private final long tickMillis;
//        private transient AtomicReference<Instant> cachedInstant = new AtomicReference<Instant>(Instant.EPOCH);

        TickClock(Clock baseClock, long tickMillis) {
            this.baseClock = baseClock;
            this.tickMillis = tickMillis;
        }
        @Override
        public ZoneId getZone() {
            return baseClock.getZone();
        }
        @Override
        public Clock withZone(ZoneId zone) {
            if (zone.equals(baseClock.getZone())) {  // intentional NPE
                return this;
            }
            return new TickClock(baseClock.withZone(zone), tickMillis);
        }
        @Override
        public long millis() {
            long millis = baseClock.millis();
            return millis - DateTimes.floorMod(millis, tickMillis);
        }
//        @Override
//        public Instant instant() {
//            Instant instant = super.instant();
//            Instant cached = cachedInstant.get();
//            if (cached.equals(instant)) {
//                return cached;
//            }
//            cachedInstant.compareAndSet(cached, instant);
//            return instant;
//        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TickClock) {
                TickClock other = (TickClock) obj;
                return baseClock.equals(other.baseClock) && tickMillis == other.tickMillis;
            }
            return false;
        }
        @Override
        public int hashCode() {
            return baseClock.hashCode() ^ ((int) (tickMillis ^ (tickMillis >>> 32)));
        }
        @Override
        public String toString() {
            return "OffsetClock[" + baseClock + "," + tickMillis + "]";
        }
    }

}
