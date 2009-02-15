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
package javax.time;

import java.io.Serializable;

/**
 * A time-source providing access to the current instant.
 * <p>
 * The Java Time Framework abstracts the concept of the 'current time' into two interfaces
 * - <code>TimeSource</code> and {@link javax.time.calendar.Clock Clock}.
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
 * Instead, they should pass a <code>TimeSource</code> into any method that requires it.
 * A dependency injection framework is one way to achieve this:
 * <pre>
 * public class MyBean {
 *   private TimeSource timeSource;  // dependency inject
 *   ...
 *   public void process(Instant eventTime) {
 *     if (eventTime.isBefore(timeSource.instant()) {
 *       ...
 *     }
 *   }
 * }
 * </pre>
 * This approach allows alternate time-source implementations, such as {@link #fixed(Instant)}
 * or {@link #offsetSystemMillis(Duration)} to be used during testing.
 * <p>
 * TimeSource is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class TimeSource {

    /**
     * Gets a time-source that obtains the current instant using
     * the system millisecond clock.
     * <p>
     * The time-source wraps {@link System#currentTimeMillis()}, thus it has
     * at best millisecond resolution.
     *
     * @return a time-source that uses the system millisecond clock, never null
     */
    public static TimeSource system() {
        return SystemTimeSource.INSTANCE;
    }

    /**
     * Gets a time-source that always returns the same instant.
     * <p>
     * The time-source will return the <code>Instant</code> passed in, which may
     * have any resolution an instant supports.
     *
     * @param fixedInstantProvider  the instant to return from each call to the time-source
     * @return a time-source that always returns the same instant, never null
     */
    public static TimeSource fixed(InstantProvider fixedInstantProvider) {
        Instant.checkNotNull(fixedInstantProvider, "InstantProvider must not be null");
        Instant instant = Instant.instant(fixedInstantProvider);
        return new FixedTimeSource(instant);
    }

    /**
     * Gets a time-source that obtains the current instant using the system
     * millisecond clock and adjusts by a fixed offset.
     * <p>
     * The time-source wraps {@link System#currentTimeMillis()}, thus it has
     * at best millisecond resolution.
     * <p>
     * The final instant is adjusted by adding the offset.
     * This is useful for simulating an application running at a later or earlier
     * point in time.
     *
     * @param offset  the duration by which this time-source is offset from the system millisecond clock
     * @return a time-source that is offset from the system millisecond clock, never null
     */
    public static TimeSource offsetSystem(Duration offset) {
        Instant.checkNotNull(offset, "Duration must not be null");
        if (offset.equals(Duration.ZERO)) {
            return SystemTimeSource.INSTANCE;
        }
        return new OffsetSystemTimeSource(offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor accessible by subclasses.
     */
    protected TimeSource() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current instant from the time-source.
     * <p>
     * The instant returned by this method will vary according to the implementation.
     * For example, the time-source returned by {@link #system()} will return
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
    public abstract Instant instant();

    //-----------------------------------------------------------------------
    /**
     * Implementation of a time-source that always returns the latest time from
     * {@link System#currentTimeMillis()}.
     */
    private static final class SystemTimeSource extends TimeSource implements Serializable {
        /** Singleton instance. */
        static final SystemTimeSource INSTANCE = new SystemTimeSource();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;

        /** Restricted constructor. */
        private SystemTimeSource() {
        }
        /** Resolve singletons. */
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public Instant instant() {
            return Instant.millisInstant(System.currentTimeMillis());
        }
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "SystemTimeSource";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a time-source that always returns the same instant.
     * This is typically used for testing.
     */
    private static final class FixedTimeSource extends TimeSource implements Serializable {
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** The fixed instant to return. */
        private final Instant instant;

        /** Restricted constructor. */
        private FixedTimeSource(Instant fixedInstant) {
            this.instant = fixedInstant;
        }
        /** {@inheritDoc} */
        @Override
        public Instant instant() {
            return instant;
        }
        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FixedTimeSource) {
                return instant.equals(((FixedTimeSource) obj).instant);
            }
            return false;
        }
        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return instant.hashCode();
        }
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "FixedTimeSource[" + instant + ']';
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a time-source that returns the latest time from
     * {@link System#currentTimeMillis()} plus an offset.
     */
    private static final class OffsetSystemTimeSource extends TimeSource implements Serializable {
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** The fixed offset to add. */
        private final Duration offset;

        /** Restricted constructor. */
        private OffsetSystemTimeSource(Duration offset) {
            this.offset = offset;
        }
        /** {@inheritDoc} */
        @Override
        public Instant instant() {
            return Instant.millisInstant(System.currentTimeMillis()).plus(offset);
        }
        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof OffsetSystemTimeSource) {
                return offset.equals(((OffsetSystemTimeSource) obj).offset);
            }
            return false;
        }
        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return offset.hashCode();
        }
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "OffsetSystemTimeSource[" + offset + ']';
        }
    }

}
