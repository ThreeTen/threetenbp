/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.Comparator;

import javax.time.Instant;
import javax.time.InstantProvider;

/**
 * Provides common utilities for working with dates and times.
 * <p>
 * DateTimes is a utility class.
 * All objects returned are immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class DateTimes {

    /**
     * Private constructor since this is a utility class
     */
    private DateTimes() {
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a comparator that sorts by instant.
     * <p>
     * The returned comparator sorts any implementation of {@link InstantProvider}.
     * This includes {@link Instant}, {@link OffsetDateTime} and {@link ZonedDateTime}.
     * The list to be sorted may contain a mixture of these types if desired.
     * <p>
     * Each comparison will call {@link InstantProvider#toInstant()} on both objects.
     *
     * @return the instant provider comparator, never null
     */
    public static Comparator<InstantProvider> instantComparator() {
        return InstantProviderComparator.INSTANCE;
    }

    /**
     * Sort by InstantProvider.
     */
    private static final class InstantProviderComparator implements Comparator<InstantProvider>, Serializable {
        /** Singleton. */
        static final Comparator<InstantProvider> INSTANCE = new InstantProviderComparator();
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private InstantProviderComparator() {
            super();
        }
        /** Preserve singleton. */
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        public int compare(InstantProvider ip1, InstantProvider ip2) {
            return Instant.instant(ip1).compareTo(Instant.instant(ip2));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a comparator that sorts by local date-time.
     * <p>
     * The returned comparator sorts any implementation of {@link DateTimeProvider}.
     * This includes {@link LocalDateTime}, {@link OffsetDateTime} and {@link ZonedDateTime}.
     * The list to be sorted may contain a mixture of these types if desired.
     * <p>
     * Each comparison will call {@link DateTimeProvider#toLocalDateTime()} on both objects.
     *
     * @return the local date-time provider comparator, never null
     */
    public static Comparator<DateTimeProvider> localDateTimeComparator() {
        return DateTimeProviderComparator.INSTANCE;
    }

    /**
     * Sort by DateTimeProvider.
     */
    private static final class DateTimeProviderComparator implements Comparator<DateTimeProvider>, Serializable {
        /** Singleton. */
        static final Comparator<DateTimeProvider> INSTANCE = new DateTimeProviderComparator();
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DateTimeProviderComparator() {
            super();
        }
        /** Preserve singleton. */
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        public int compare(DateTimeProvider dtp1, DateTimeProvider dtp2) {
            return LocalDateTime.dateTime(dtp1).compareTo(LocalDateTime.dateTime(dtp2));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a comparator that sorts by local date.
     * <p>
     * The returned comparator sorts any implementation of {@link DateProvider}.
     * This includes {@link LocalDate}, {@link LocalDateTime}, {@link OffsetDate},
     * {@link OffsetDateTime} and {@link ZonedDateTime}.
     * The list to be sorted may contain a mixture of these types if desired.
     * <p>
     * Each comparison will call {@link DateProvider#toLocalDate()} on both objects.
     *
     * @return the local date provider comparator, never null
     */
    public static Comparator<DateProvider> localDateComparator() {
        return DateProviderComparator.INSTANCE;
    }

    /**
     * Sort by DateProvider.
     */
    private static final class DateProviderComparator implements Comparator<DateProvider>, Serializable {
        /** Singleton. */
        static final Comparator<DateProvider> INSTANCE = new DateProviderComparator();
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DateProviderComparator() {
            super();
        }
        /** Preserve singleton. */
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        public int compare(DateProvider dp1, DateProvider dp2) {
            return LocalDate.date(dp1).compareTo(LocalDate.date(dp2));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a comparator that sorts by local time.
     * <p>
     * The returned comparator sorts any implementation of {@link TimeProvider}.
     * This includes {@link LocalTime}, {@link LocalDateTime}, {@link OffsetTime},
     * {@link OffsetDateTime} and {@link ZonedDateTime}.
     * The list to be sorted may contain a mixture of these types if desired.
     * <p>
     * Each comparison will call {@link TimeProvider#toLocalTime()} on both objects.
     *
     * @return the local time provider comparator, never null
     */
    public static Comparator<TimeProvider> localTimeComparator() {
        return TimeProviderComparator.INSTANCE;
    }

    /**
     * Sort by TimeProvider.
     */
    private static final class TimeProviderComparator implements Comparator<TimeProvider>, Serializable {
        /** Singleton. */
        static final Comparator<TimeProvider> INSTANCE = new TimeProviderComparator();
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private TimeProviderComparator() {
            super();
        }
        /** Preserve singleton. */
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        public int compare(TimeProvider tp1, TimeProvider tp2) {
            return LocalTime.time(tp1).compareTo(LocalTime.time(tp2));
        }
    }

}
