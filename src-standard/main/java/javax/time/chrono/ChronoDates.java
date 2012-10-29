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
package javax.time.chrono;

import java.util.Objects;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_SECOND;
import static javax.time.calendrical.LocalDateTimeField.OFFSET_SECONDS;
import static javax.time.calendrical.LocalDateTimeField.INSTANT_SECONDS;
import static javax.time.DateTimes.SECONDS_PER_DAY;

import javax.time.*;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.zone.*;

/**
 * Factories for calendar neutral APIs.
 * Factories to create {@link ChronoDate}, {@link ChronoDateTime},
 * {@link ChronoOffsetDateTime}, and {@link ChronoZonedDateTime}.
 * @author Roger Riggs
 */
public class ChronoDates {
    // Static factories only, no supported constructors
    private ChronoDates() {}

    /**
     * Creates a date in named calendar system from the Era, year-of-era,
     * month-of-year and day-of-month.
     *
     * @param era  the calendar system era, not null
     * @param year  the calendar system year-of-era
     * @param month  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date in this calendar system, not null
     */
    public static <R extends Chronology<R>> ChronoDate<R> 
            newDate(Era<R> era, int year, int month, int dayOfMonth) {
        return era.date(year, month, dayOfMonth);
    }

    /**
     * Obtains an instance of ChronoDate using the chronology from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ChronoDate}.
     * <p>
     * If the calendrical can provide a calendar system, then that will be used,
     * otherwise, the ISO calendar system will be used.
     * This allows a {@link LocalDate} to be converted to a {@code ChronoDate}.
     *
     * @param calendrical  the calendrical to convert, not null
     * @return the calendar system specific date, not null
     * @throws DateTimeException if unable to convert to a {@code ChronoDate}
     */
    public static ChronoDate<?> toDate(DateTimeAccessor calendrical) {
       if (calendrical instanceof ChronoDate) {
            return (ChronoDate<?>) calendrical;
        }
        LocalDate ld = LocalDate.from(calendrical);
        Chronology<?> chronology = Chronology.from(calendrical);
        return chronology.date(ld);
    }


    //-----------------------------------------------------------------------
    /**
     * Obtains the current date from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static ChronoDate<?> nowDate(String calendar) {
        Chronology chrono = Chronology.of(calendar);
        return chrono.date(Clock.systemDefaultZone().instant());
    }

    /**
     * Obtains the current date from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date-time.
     * Specifying the time-zone avoids dependence on the default time-zone.
     *
     * @return the current date-time using the system clock, not null
     */
    public static ChronoDate<?> nowDate(String calendar, ZoneId zone) {
        Chronology chrono = Chronology.of(calendar);
        return chrono.date(Clock.system(zone).instant());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date-time.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock and default time-zone, not null
     */
    public static ChronoDateTime<?> nowDateTime(String calendar) {
        return nowDateTime(calendar, Clock.systemDefaultZone());
    }

    /**
     * Obtains the current date-time from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date-time.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock, not null
     */
    public static ChronoDateTime<?> nowDateTime(String calendar, ZoneId zone) {
        return nowDateTime(calendar, Clock.system(zone));
    }

    /**
     * Obtains the current date-time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date-time.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date-time, not null
     */
    private static ChronoDateTime nowDateTime(String calendar, Clock clock) {
        Objects.requireNonNull(clock, "Clock must not be null");
        // inline OffsetDateTime factory to avoid creating object and InstantProvider checks
        final Instant now = clock.instant();  // called once
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long localSeconds = now.getEpochSecond() + offset.getTotalSeconds();  // overflow caught later
        return create(Chronology.of(calendar), localSeconds, now.getNano());
    }

    /**
     * Obtains an instance of {@code ChronoDateTime} using seconds from the
     * local epoch of 1970-01-01T00:00:00.
     * <p>
     * The nanosecond field is set to zero.
     *
     * @param chrono the Chronology for which to create the ChronoDate
     * @param localSeconds  the number of seconds from the local epoch of 1970-01-01T00:00:00
     * @param nanoOfSecond  the nanosecond within the second, from 0 to 999,999,999
     * @return the local date-time, not null
     * @throws DateTimeException if the instant exceeds the supported date range
     */
    static ChronoDateTime<?> create(Chronology chrono, long localSeconds, int nanoOfSecond) {
        long epochDays = DateTimes.floorDiv(localSeconds, SECONDS_PER_DAY);
        int secsOfDay = DateTimes.floorMod(localSeconds, SECONDS_PER_DAY);
        ChronoDate date = chrono.dateFromEpochDay(epochDays);
        LocalTime time = LocalTime.ofSecondOfDay(secsOfDay, nanoOfSecond);
        return ChronoDateTime.of(date, time);
    }

    /**
     * Obtains an instance of {@code ChronoDateTime} from a date and time.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @return the local date-time, not null
     */
    public static <R extends Chronology<R>> ChronoDateTime<R> newDateTime(ChronoDate<R> date, LocalTime time) {
        Objects.requireNonNull(date, "ChronoDate must not be null");
        Objects.requireNonNull(time, "LocalTime must not be null");
        return date.atTime(time);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ChronoDateTime} using its chronology
     * from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ChronoDateTime}.
     *
     * @param calendrical  the calendrical to convert, not null
     * @return the local date-time, not null
     * @throws DateTimeException if unable to convert to a {@code ChronoDateTime}
     */
    public static ChronoDateTime<?> toDateTime(DateTimeAccessor calendrical) {
        if (calendrical instanceof ChronoDateTime) {
            return (ChronoDateTime) calendrical;
        }

        Chronology<?> chronology = Chronology.from(calendrical);
        return chronology.date(calendrical).atTime(LocalTime.from(calendrical));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date-time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock, not null
     */
    public static <R extends Chronology<R>> ChronoOffsetDateTime<R> nowOffsetDateTime(String calendar) {
        return nowOffsetDateTime(calendar, Clock.systemDefaultZone());
    }
    
    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date-time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock, not null
     */
    public static <R extends Chronology<R>> ChronoOffsetDateTime<R> nowOffsetDateTime(String calendar, ZoneId zone) {
        return nowOffsetDateTime(calendar, Clock.system(zone));
    }

    /**
     * Obtains the current offset date-time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date-time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date-time, not null
     */
    private static <R extends Chronology<R>> ChronoOffsetDateTime<R> nowOffsetDateTime(String calendar, Clock clock) {
        Objects.requireNonNull(clock, "Clock must not be null");
        Objects.requireNonNull(calendar, "Calendar name must not be null");
        Chronology<R> chrono = (Chronology<R>)Chronology.of(calendar);
        final Instant now = clock.instant();  // called once
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long localSeconds = now.getEpochSecond() + offset.getTotalSeconds();  // overflow caught later

        long epochDays = DateTimes.floorDiv(localSeconds, SECONDS_PER_DAY);
        int secsOfDay = DateTimes.floorMod(localSeconds, SECONDS_PER_DAY);
        ChronoDate<R> date = (ChronoDate<R>)chrono.dateFromEpochDay(epochDays);
        LocalTime time = LocalTime.ofSecondOfDay(secsOfDay, now.getNano());
        return date.atTime(time).atOffset(offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ChronoOffsetDateTime} from a date, time and offset.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     */
    public static <R extends Chronology<R>> ChronoOffsetDateTime<R>
            newOffsetDateTime(ChronoDate<R> date, LocalTime time, ZoneOffset offset) {
        return date.atTime(time).atOffset(offset);
    }

    /**
     * Obtains an instance of {@code ChronoOffsetDateTime} from a date-time and offset.
     *
     * @param dateTime  the local date-time, not null
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     */
    private static <R extends Chronology<R>> ChronoOffsetDateTime<R>
            newOffsetDateTime(ChronoDateTime<R> dateTime, ZoneOffset offset) {
        return dateTime.atOffset(offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ChronoOffsetDateTime} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code OffsetDateTime}.
     *
     * @param calendrical  the calendrical to convert, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if unable to convert to an {@code OffsetDateTime}
     */
    public static ChronoOffsetDateTime<?> toOffsetDateTime(DateTimeAccessor calendrical) {
        if (calendrical instanceof ChronoOffsetDateTime) {
            return (ChronoOffsetDateTime) calendrical;
        }
        try {
            long offset_sec = calendrical.get(OFFSET_SECONDS);
            ZoneOffset offset = ZoneOffset.ofTotalSeconds(DateTimes.safeToInt(offset_sec));
            Chronology<?> chrono = Chronology.from(calendrical);
            long epochSeconds = calendrical.get(INSTANT_SECONDS);
            long nanos = calendrical.get(NANO_OF_SECOND);
            ChronoDateTime<?> cdt = create(chrono, epochSeconds, DateTimes.safeToInt(nanos));
            return newOffsetDateTime(cdt, offset);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to convert calendrical to OffsetDateTime: " + calendrical.getClass(), ex);
        }
    }
    //-----------------------------------------------------------------------
    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date-time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock, not null
     */
    public static <R extends Chronology<R>> ChronoZonedDateTime<R> nowZonedDateTime(String calendar) {
        return nowZonedDateTime(calendar, Clock.systemDefaultZone());
    }
    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date-time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock, not null
     */
    public static <R extends Chronology<R>> ChronoZonedDateTime<R> nowZonedDateTime(String calendar, ZoneId zone) {
        return nowZonedDateTime(calendar, Clock.system(zone));
    }

    /**
     * Obtains the current date-time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date-time.
     * The offset will be calculated from the time-zone in the clock.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced usChronoOffsetDateTimeing {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date-time, not null
     */
    private static <R extends Chronology<R>> ChronoZonedDateTime<R> nowZonedDateTime(String calendar, Clock clock) {
        Objects.requireNonNull(clock, "Clock must not be null");
        Objects.requireNonNull(calendar, "Calendar name must not be null");
        Chronology chrono = Chronology.of(calendar);
        final Instant now = clock.instant();  // called once
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long localSeconds = now.getEpochSecond() + offset.getTotalSeconds();  // overflow caught later

        long epochDays = DateTimes.floorDiv(localSeconds, SECONDS_PER_DAY);
        int secsOfDay = DateTimes.floorMod(localSeconds, SECONDS_PER_DAY);
        ChronoDate date = chrono.dateFromEpochDay(epochDays);
        LocalTime time = LocalTime.ofSecondOfDay(secsOfDay, now.getNano());
        return date.atTime(time).atZone(clock.getZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from a ChronoDate and time
     * where the date-time must be valid for the time-zone.
     * <p>
     * This factory creates a {@code ZoneChronoDateTime} from a date, time and time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then an exception will be thrown.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the local date-time is invalid for the time-zone
     */
    public static <R extends Chronology<R>> ChronoZonedDateTime<R> newZonedDateTime(ChronoDate<R> date, LocalTime time, ZoneId zone) {
        return date.atTime(time).atZone(zone, ZoneResolvers.strict());
    }

    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from a local date and time
     * providing a resolver to handle an invalid date-time.
     * <p>
     * This factory creates a {@code ZoneChronoDateTime} from a date, time and time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then the resolver will determine what action to take.
     * See {@link ZoneResolvers} for common resolver implementations.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @param zone  the time-zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the resolver cannot resolve an invalid local date-time
     */
    private static <R extends Chronology<R>> ChronoZonedDateTime<R>
            newZonedDateTime(ChronoDate<R> date, LocalTime time, ZoneId zone, ZoneResolver resolver) {
        return date.atTime(time).atZone(zone, resolver);
    }

    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from a local date-time
     * where the date-time must be valid for the time-zone.
     * <p>
     * This factory creates a {@code ZoneChronoDateTime} from a date-time and time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then an exception will be thrown.
     *
     * @param dateTime  the local date-time, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the local date-time is invalid for the time-zone
     */
    private static <R extends Chronology<R>> ChronoZonedDateTime<R>
            newZonedDateTime(ChronoDateTime<R> dateTime, ZoneId zone) {
        return  dateTime.atZone(zone, ZoneResolvers.strict());
    }

    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from an {@code ChronoOffsetDateTime}.
     * <p>
     * The resulting date-time represents exactly the same instant on the time-line.
     * As such, the resulting local date-time may be different from the input.
     * <p>
     * If the instant represents a point on the time-line outside the supported year
     * range then an exception will be thrown.
     *
     * @param instantDateTime  the instant to create the date-time from, not null
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    private static <R extends Chronology<R>> ChronoZonedDateTime<R> ofInstant(ChronoOffsetDateTime<R> instantDateTime, ZoneId zone) {
        Objects.requireNonNull(instantDateTime, "ChronoOffsetDateTime must not be null");
        return instantDateTime.atZoneSameInstant(zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneChronoDateTime} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ZoneChronoDateTime}.
     *
     * @param calendrical  the calendrical to convert, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if unable to convert to an {@code ZoneChronoDateTime}
     */
    public static ChronoZonedDateTime<?> toZonedDateTime(DateTimeAccessor calendrical) {
        if (calendrical instanceof ChronoZonedDateTime) {
            return (ChronoZonedDateTime) calendrical;
        }
        try {
            ZoneId zone = ZoneId.from(calendrical);
            try {
                ChronoOffsetDateTime odt = toOffsetDateTime(calendrical);
                return odt.atZoneSameInstant(zone);
            } catch (DateTimeException ex1) {
                ChronoDateTime ldt = toDateTime(calendrical);
                return ldt.atZone(zone, ZoneResolvers.postGapPreOverlap());
            }
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to convert calendrical to ZoneChronoDateTime: " + calendrical.getClass(), ex);
        }
    }

}
