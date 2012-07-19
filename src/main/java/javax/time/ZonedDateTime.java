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

import static javax.time.DateTimes.SECONDS_PER_HOUR;
import static javax.time.DateTimes.SECONDS_PER_MINUTE;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_DAY;

import java.io.Serializable;

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.CalendricalFormatter;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.DateTimeFormatters;
import javax.time.zone.ZoneOffsetInfo;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;
import javax.time.zone.ZoneRules;

/**
 * A date-time with a time-zone in the ISO-8601 calendar system,
 * such as {@code 2007-12-03T10:15:30+01:00 Europe/Paris}.
 * <p>
 * {@code ZonedDateTime} is an immutable representation of a date-time with a time-zone.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * as well as a time-zone and zone offset. For example, the value
 * "2nd October 2007 at 13:45.30.123456789 +02:00 in the Europe/Paris time-zone"
 * can be stored in a {@code ZonedDateTime}.
 * <p>
 * The purpose of storing the time-zone is to distinguish the ambiguous case where
 * the local time-line overlaps, typically as a result of the end of daylight time.
 * Information about the local-time can be obtained using methods on the time-zone.
 * <p>
 * This class provides control over what happens at these cutover points
 * (typically a gap in spring and an overlap in autumn). The {@link ZoneResolver}
 * interface and implementations in {@link ZoneResolvers} provide strategies for
 * handling these cases. The methods {@link #withEarlierOffsetAtOverlap()} and
 * {@link #withLaterOffsetAtOverlap()} provide further control for overlaps.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class ZonedDateTime
        implements AdjustableDateTime, DateTimeAdjuster, Comparable<ZonedDateTime>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -456761901L;

    /**
     * The offset date-time.
     */
    private final OffsetDateTime dateTime;
    /**
     * The time-zone.
     */
    private final ZoneId zone;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date-time.
     * The zone and offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock, not null
     */
    public static ZonedDateTime now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current date-time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current time.
     * The zone and offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date-time, not null
     */
    public static ZonedDateTime now(Clock clock) {
        DateTimes.checkNotNull(clock, "Clock must not be null");
        final Instant now = clock.instant();  // called once
        return ofInstant(now, clock.getZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} from year, month,
     * day, hour, minute, second, nanosecond and time-zone
     * where the date-time must be valid for the time-zone.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * <p>
     * The local date-time must be valid for the time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the value of any field is out of range
     * @throws CalendricalException if the day-of-month is invalid for the month-year
     * @throws CalendricalException if the local date-time is invalid for the time-zone
     */
    public static ZonedDateTime of(int year, Month month, int dayOfMonth,
            int hour, int minute, int second, int nanoOfSecond, ZoneId zone) {
        return of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zone, ZoneResolvers.strict());
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from year, month,
     * day, hour, minute, second, nanosecond and time-zone
     * providing a resolver to handle an invalid date-time.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * <p>
     * The local date-time must be valid for the time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then the resolver will determine what action to take.
     * See {@link ZoneResolvers} for common resolver implementations.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param zone  the time-zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the value of any field is out of range
     * @throws CalendricalException if the day-of-month is invalid for the month-year
     * @throws CalendricalException if the resolver cannot resolve an invalid local date-time
     */
    public static ZonedDateTime of(int year, Month month, int dayOfMonth,
            int hour, int minute, int second, int nanoOfSecond,
            ZoneId zone, ZoneResolver resolver) {
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
        return resolve(dt, zone, null, resolver);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} from year, month,
     * day, hour, minute, second, nanosecond and time-zone
     * where the date-time must be valid for the time-zone.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * <p>
     * The local date-time must be valid for the time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the value of any field is out of range
     * @throws CalendricalException if the day-of-month is invalid for the month-year
     * @throws CalendricalException if the local date-time is invalid for the time-zone
     */
    public static ZonedDateTime of(int year, int month, int dayOfMonth,
            int hour, int minute, int second, int nanoOfSecond, ZoneId zone) {
        return of(year, month, dayOfMonth,
                hour, minute, second, nanoOfSecond, zone, ZoneResolvers.strict());
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from year, month,
     * day, hour, minute, second, nanosecond and time-zone
     * providing a resolver to handle an invalid date-time.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * <p>
     * The local date-time must be valid for the time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then the resolver will determine what action to take.
     * See {@link ZoneResolvers} for common resolver implementations.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param zone  the time-zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the value of any field is out of range
     * @throws CalendricalException if the day-of-month is invalid for the month-year
     * @throws CalendricalException if the resolver cannot resolve an invalid local date-time
     */
    public static ZonedDateTime of(int year, int month, int dayOfMonth,
            int hour, int minute, int second, int nanoOfSecond,
            ZoneId zone, ZoneResolver resolver) {
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth,
                                    hour, minute, second, nanoOfSecond);
        return resolve(dt, zone, null, resolver);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} from a local date and time
     * where the date-time must be valid for the time-zone.
     * <p>
     * This factory creates a {@code ZonedDateTime} from a date, time and time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then an exception will be thrown.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the local date-time is invalid for the time-zone
     */
    public static ZonedDateTime of(LocalDate date, LocalTime time, ZoneId zone) {
        return of(date, time, zone, ZoneResolvers.strict());
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from a local date and time
     * providing a resolver to handle an invalid date-time.
     * <p>
     * This factory creates a {@code ZonedDateTime} from a date, time and time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then the resolver will determine what action to take.
     * See {@link ZoneResolvers} for common resolver implementations.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @param zone  the time-zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the resolver cannot resolve an invalid local date-time
     */
    public static ZonedDateTime of(LocalDate date, LocalTime time, ZoneId zone, ZoneResolver resolver) {
        return resolve(LocalDateTime.of(date, time), zone, null, resolver);
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from a local date-time
     * where the date-time must be valid for the time-zone.
     * <p>
     * This factory creates a {@code ZonedDateTime} from a date-time and time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then an exception will be thrown.
     *
     * @param dateTime  the local date-time, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the local date-time is invalid for the time-zone
     */
    public static ZonedDateTime of(LocalDateTime dateTime, ZoneId zone) {
        return of(dateTime, zone, ZoneResolvers.strict());
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from a local date-time
     * providing a resolver to handle an invalid date-time.
     * <p>
     * This factory creates a {@code ZonedDateTime} from a date-time and time-zone.
     * If the time is invalid for the zone, due to either being a gap or an overlap,
     * then the resolver will determine what action to take.
     * See {@link ZoneResolvers} for common resolver implementations.
     *
     * @param dateTime  the local date-time, not null
     * @param zone  the time-zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the resolver cannot resolve an invalid local date-time
     */
    public static ZonedDateTime of(LocalDateTime dateTime, ZoneId zone, ZoneResolver resolver) {
        return resolve(dateTime, zone, null, resolver);
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from an {@code OffsetDateTime}
     * ensuring that the offset provided is valid for the time-zone.
     * <p>
     * This factory creates a {@code ZonedDateTime} from an offset date-time and time-zone.
     * If the date-time is invalid for the zone due to a time-line gap then an exception is thrown.
     * Otherwise, the offset is checked against the zone to ensure it is valid.
     * <p>
     * An alternative to this method is {@link #ofInstant}. This method will retain
     * the date and time and throw an exception if the offset is invalid.
     * The {@code ofInstant} method will change the date and time if necessary
     * to retain the same instant.
     *
     * @param dateTime  the offset date-time to use, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if no rules can be found for the zone
     * @throws CalendricalException if the date-time is invalid due to a gap in the local time-line
     * @throws CalendricalException if the offset is invalid for the time-zone at the date-time
     */
    public static ZonedDateTime of(OffsetDateTime dateTime, ZoneId zone) {
        DateTimes.checkNotNull(dateTime, "OffsetDateTime must not be null");
        DateTimes.checkNotNull(zone, "ZoneId must not be null");
        ZoneOffset inputOffset = dateTime.getOffset();
        ZoneRules rules = zone.getRules();  // latest rules version
        ZoneOffsetInfo info = rules.getOffsetInfo(dateTime.toLocalDateTime());
        if (info.isValidOffset(inputOffset) == false) {
            if (info.isTransition() && info.getTransition().isGap()) {
                throw new CalendricalException("The local time " + dateTime.toLocalDateTime() +
                        " does not exist in time-zone " + zone + " due to a daylight savings gap");
            }
            throw new CalendricalException("The offset in the date-time " + dateTime +
                    " is invalid for time-zone " + zone);
        }
        return new ZonedDateTime(dateTime, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} from an {@code Instant}
     * using the UTC zone.
     * <p>
     * The resulting date-time represents exactly the same instant on the time-line.
     * Calling {@link #toInstant()} will return an instant equal to the one used here.
     * <p>
     * If the instant represents a point on the time-line outside the supported year
     * range then an exception will be thrown.
     *
     * @param instant  the instant to create the date-time from, not null
     * @return the zoned date-time in UTC, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public static ZonedDateTime ofInstantUTC(Instant instant) {
        return ofInstant(instant, ZoneId.UTC);
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from an {@code Instant}.
     * <p>
     * The resulting date-time represents exactly the same instant on the time-line.
     * Calling {@link #toInstant()} will return an instant equal to the one used here.
     * <p>
     * If the instant represents a point on the time-line outside the supported year
     * range then an exception will be thrown.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public static ZonedDateTime ofInstant(Instant instant, ZoneId zone) {
        DateTimes.checkNotNull(instant, "Instant must not be null");
        DateTimes.checkNotNull(zone, "ZoneId must not be null");
        ZoneRules rules = zone.getRules();  // latest rules version
        OffsetDateTime offsetDT = OffsetDateTime.ofInstant(instant, rules.getOffset(instant));
        return new ZonedDateTime(offsetDT, zone);
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from an {@code OffsetDateTime}.
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
     * @throws CalendricalException if the result exceeds the supported range
     */
    public static ZonedDateTime ofInstant(OffsetDateTime instantDateTime, ZoneId zone) {
        DateTimes.checkNotNull(instantDateTime, "OffsetDateTime must not be null");
        DateTimes.checkNotNull(zone, "ZoneId must not be null");
        ZoneRules rules = zone.getRules();  // latest rules version
        if (rules.isValidDateTime(instantDateTime) == false) {  // avoids toInstant()
            instantDateTime = instantDateTime.withOffsetSameInstant(rules.getOffset(instantDateTime.toInstant()));
        }
        return new ZonedDateTime(instantDateTime, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The nanosecond field is set to zero.
     *
     * @param epochSecond  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public static ZonedDateTime ofEpochSecond(long epochSecond, ZoneId zone) {
        DateTimes.checkNotNull(zone, "ZoneId must not be null");
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond, 0), zone);
    }

    //-----------------------------------------------------------------------
//    static ZonedDateTime deriveFrom(CalendricalEngine engine) {
//        ZoneOffset offset = engine.getOffset(false);
//        if (offset != null) {
//            OffsetDateTime odt = OffsetDateTime.deriveFrom(engine);
//            if (odt != null) {
//                ZoneId zone = engine.getZone(false);
//                if (zone == null) {
//                    zone = ZoneId.of(offset);  // smart use of offset as zone
//                } else {
//                    ZoneRules rules = zone.getRules();  // latest rules version
//                    if (rules.isValidDateTime(odt) == false) {  // avoids toInstant()
//                        odt = odt.withOffsetSameInstant(rules.getOffset(odt.toInstant()));  // smart use of date-time as instant
//                    }
//                }
//                return new ZonedDateTime(odt, zone);
//            }
//        } else {
//            LocalDateTime ldt = LocalDateTime.deriveFrom(engine);
//            ZoneId zone = engine.getZone(true);
//            if (ldt != null && zone != null) {
//                return resolve(ldt, zone, null, ZoneResolvers.postGapPreOverlap());  // smart use of resolver
//            }
//        }
//        return null;
//    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ZonedDateTime}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if unable to convert to an {@code ZonedDateTime}
     */
    public static ZonedDateTime from(DateTime calendrical) {
        ZonedDateTime obj = calendrical.extract(ZonedDateTime.class);
        if (obj == null) {
            Instant instant = calendrical.extract(Instant.class);
            ZoneId zone = calendrical.extract(ZoneId.class);
            if (instant != null && zone != null) {
                return ZonedDateTime.ofInstant(instant, zone);
            }
            // TODO: more complex conversions
        }
        return DateTimes.ensureNotNull(obj, "Unable to convert calendrical to ZonedDateTime: ", calendrical.getClass());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} from a text string such as
     * {@code 2007-12-03T10:15:30+01:00[Europe/Paris]}.
     * <p>
     * The string must represent a valid date-time and is parsed using
     * {@link javax.time.format.DateTimeFormatters#isoZonedDateTime()}.
     *
     * @param text  the text to parse such as "2007-12-03T10:15:30+01:00[Europe/Paris]", not null
     * @return the parsed zoned date-time, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static ZonedDateTime parse(CharSequence text) {
        return parse(text, DateTimeFormatters.isoZonedDateTime());
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a date-time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed zoned date-time, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static ZonedDateTime parse(CharSequence text, CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.parse(text, ZonedDateTime.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime}.
     *
     * @param desiredLocalDateTime  the date-time, not null
     * @param zone  the time-zone, not null
     * @param oldDateTime  the old date-time prior to the calculation, may be null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return the zoned date-time, not null
     * @throws CalendricalException if the date-time cannot be resolved
     */
    private static ZonedDateTime resolve(LocalDateTime desiredLocalDateTime, ZoneId zone, ZonedDateTime oldDateTime, ZoneResolver resolver) {
        DateTimes.checkNotNull(desiredLocalDateTime, "LocalDateTime must not be null");
        DateTimes.checkNotNull(zone, "ZoneId must not be null");
        DateTimes.checkNotNull(resolver, "ZoneResolver must not be null");
        ZoneRules rules = zone.getRules();
        OffsetDateTime offsetDT = resolver.resolve(desiredLocalDateTime, rules.getOffsetInfo(desiredLocalDateTime), rules, zone,
                oldDateTime != null ? oldDateTime.toOffsetDateTime() : null);
        if (zone.isValidFor(offsetDT) == false) {
            throw new CalendricalException(
                    "ZoneResolver implementation must return a valid date-time and offset for the zone: " + resolver.getClass().getName());
        }
        return new ZonedDateTime(offsetDT, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param dateTime  the date-time, validated as not null
     * @param zone  the time-zone, validated as not null
     */
    private ZonedDateTime(OffsetDateTime dateTime, ZoneId zone) {
        this.dateTime = dateTime;
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            return dateTime.get(field);
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset, such as '+01:00'.
     *
     * @return the zone offset, not null
     */
    public ZoneOffset getOffset() {
        return dateTime.getOffset();
    }

    /**
     * Returns a copy of this ZonedDateTime changing the zone offset to the
     * earlier of the two valid offsets at a local time-line overlap.
     * <p>
     * This method only has any effect when the local time-line overlaps, such as
     * at an autumn daylight savings cutover. In this scenario, there are two
     * valid offsets for the local date-time. Calling this method will return
     * a zoned date-time with the earlier of the two selected.
     * <p>
     * If this method is called when it is not an overlap, {@code this}
     * is returned.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code ZonedDateTime} based on this date-time with the earlier offset, not null
     * @throws CalendricalException if no rules can be found for the zone
     * @throws CalendricalException if no rules are valid for this date-time
     */
    public ZonedDateTime withEarlierOffsetAtOverlap() {
        ZoneOffsetInfo info = getApplicableRules().getOffsetInfo(toLocalDateTime());
        if (info.isTransition()) {
            ZoneOffset offset = info.getTransition().getOffsetBefore();
            if (offset.equals(getOffset()) == false) {
                OffsetDateTime newDT = dateTime.withOffsetSameLocal(offset);
                return new ZonedDateTime(newDT, zone);
            }
        }
        return this;
    }

    /**
     * Returns a copy of this ZonedDateTime changing the zone offset to the
     * later of the two valid offsets at a local time-line overlap.
     * <p>
     * This method only has any effect when the local time-line overlaps, such as
     * at an autumn daylight savings cutover. In this scenario, there are two
     * valid offsets for the local date-time. Calling this method will return
     * a zoned date-time with the later of the two selected.
     * <p>
     * If this method is called when it is not an overlap, {@code this}
     * is returned.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code ZonedDateTime} based on this date-time with the later offset, not null
     * @throws CalendricalException if no rules can be found for the zone
     * @throws CalendricalException if no rules are valid for this date-time
     */
    public ZonedDateTime withLaterOffsetAtOverlap() {
        ZoneOffsetInfo info = getApplicableRules().getOffsetInfo(toLocalDateTime());
        if (info.isTransition()) {
            ZoneOffset offset = info.getTransition().getOffsetAfter();
            if (offset.equals(getOffset()) == false) {
                OffsetDateTime newDT = dateTime.withOffsetSameLocal(offset);
                return new ZonedDateTime(newDT, zone);
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time-zone, such as 'Europe/Paris'.
     * <p>
     * This returns the stored time-zone id used to determine the time-zone rules.
     *
     * @return the time-zone, not null
     */
    public ZoneId getZone() {
        return zone;
    }

    /**
     * Returns a copy of this ZonedDateTime with a different time-zone,
     * retaining the local date-time if possible.
     * <p>
     * This method changes the time-zone and retains the local date-time.
     * The local date-time is only changed if it is invalid for the new zone.
     * In that case, the {@link ZoneResolvers#retainOffset() retain offset} resolver is used.
     * <p>
     * To change the zone and adjust the local date-time,
     * use {@link #withZoneSameInstant(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to change to, not null
     * @return a {@code ZonedDateTime} based on this date-time with the requested zone, not null
     */
    public ZonedDateTime withZoneSameLocal(ZoneId zone) {
        return withZoneSameLocal(zone, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a copy of this ZonedDateTime with a different time-zone,
     * retaining the local date-time if possible.
     * <p>
     * This method changes the time-zone and retains the local date-time.
     * The local date-time is only changed if it is invalid for the new zone.
     * In that case, the specified resolver is used.
     * <p>
     * To change the zone and adjust the local date-time,
     * use {@link #withZoneSameInstant(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to change to, not null
     * @param resolver  the resolver to use, not null
     * @return a {@code ZonedDateTime} based on this date-time with the requested zone, not null
     */
    public ZonedDateTime withZoneSameLocal(ZoneId zone, ZoneResolver resolver) {
        DateTimes.checkNotNull(zone, "ZoneId must not be null");
        DateTimes.checkNotNull(resolver, "ZoneResolver must not be null");
        return zone == this.zone ? this :
            resolve(dateTime.toLocalDateTime(), zone, this, resolver);
    }

    /**
     * Returns a copy of this ZonedDateTime with a different time-zone,
     * retaining the instant.
     * <p>
     * This method changes the time-zone and retains the instant.
     * This normally results in a change to the local date-time.
     * <p>
     * This method is based on retaining the same instant, thus gaps and overlaps
     * in the local time-line have no effect on the result.
     * <p>
     * To change the offset while keeping the local time,
     * use {@link #withZoneSameLocal(ZoneId)}.
     *
     * @param zone  the time-zone to change to, not null
     * @return a {@code ZonedDateTime} based on this date-time with the requested zone, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime withZoneSameInstant(ZoneId zone) {
        return zone == this.zone ? this : ofInstant(dateTime, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the zone rules applicable for this date-time.
     * <p>
     * The rules provide the information on how the zone offset changes over time.
     * This usually includes historical and future information.
     * The rules are determined using {@link ZoneId#getRulesValidFor(OffsetDateTime)}
     * which finds the best matching set of rules for this date-time.
     * If a new version of the time-zone rules is registered then the result
     * of this method may change.
     * <p>
     * If this instance is created on one JVM and passed by serialization to another JVM
     * it is possible for the time-zone id to be invalid.
     * If this happens, this method will throw an exception.
     *
     * @return the time-zone rules, not null
     * @throws CalendricalException if no rules can be found for the zone
     * @throws CalendricalException if no rules are valid for this date-time
     */
    public ZoneRules getApplicableRules() {
        return zone.getRulesValidFor(dateTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return dateTime.getYear();
    }

    /**
     * Gets the month-of-year field from 1 to 12.
     * <p>
     * This method returns the month as an {@code int} from 1 to 12.
     * Application code is frequently clearer if the enum {@link Month}
     * is used by calling {@link #getMonth()}.
     *
     * @return the month-of-year, from 1 to 12
     * @see #getMonth()
     */
    public int getMonthValue() {
        return dateTime.getMonthValue();
    }

    /**
     * Gets the month-of-year field using the {@code Month} enum.
     * <p>
     * This method returns the enum {@link Month} for the month.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link Month#getValue() int value}.
     *
     * @return the month-of-year, not null
     * @see #getMonthValue()
     */
    public Month getMonth() {
        return dateTime.getMonth();
    }

    /**
     * Gets the day-of-month field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 31
     */
    public int getDayOfMonth() {
        return dateTime.getDayOfMonth();
    }

    /**
     * Gets the day-of-year field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-year.
     *
     * @return the day-of-year, from 1 to 365, or 366 in a leap year
     */
    public int getDayOfYear() {
        return dateTime.getDayOfYear();
    }

    /**
     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day-of-week.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the {@code DayOfWeek}.
     * This includes textual names of the values.
     *
     * @return the day-of-week, not null
     */
    public DayOfWeek getDayOfWeek() {
        return dateTime.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    public int getHour() {
        return dateTime.getHour();
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinute() {
        return dateTime.getMinute();
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecond() {
        return dateTime.getSecond();
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    public int getNano() {
        return dateTime.getNano();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the local date-time altered.
     * <p>
     * This method returns an object with the same {@code ZoneId} and the
     * specified {@code LocalDateTime}.
     * <p>
     * If the adjusted date results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     *
     * @param dateTime  the local date-time to change to, not null
     * @return a {@code ZonedDateTime} based on this time with the requested date-time, not null
     */
    public ZonedDateTime withDateTime(LocalDateTime dateTime) {
        return withDateTime(dateTime, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the local date-time altered,
     * providing a resolver for invalid date-times.
     * <p>
     * This method returns an object with the same {@code ZoneId} and the
     * specified {@code LocalDateTime}.
     * <p>
     * If the adjusted date results in a date-time that is invalid, then the
     * specified resolver is used.
     *
     * @param dateTime  the local date-time to change to, not null
     * @param resolver  the resolver to use, not null
     * @return a {@code ZonedDateTime} based on this time with the requested date-time, not null
     */
    public ZonedDateTime withDateTime(LocalDateTime dateTime, ZoneResolver resolver) {
        DateTimes.checkNotNull(dateTime, "LocalDateTime must not be null");
        DateTimes.checkNotNull(resolver, "ZoneResolver must not be null");
        return this.toLocalDateTime().equals(dateTime) ?
                this : ZonedDateTime.resolve(dateTime, zone, this, resolver);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} altered using the adjuster.
     * <p>
     * This adjusts the date-time according to the rules of the specified adjuster.
     * If the adjusted date results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code ZonedDateTime} based on this date-time with the date adjusted, not null
     */
    public ZonedDateTime with(DateTimeAdjuster adjuster) {
        return with(adjuster, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} altered using the adjuster,
     * providing a resolver for invalid date-times.
     * <p>
     * This adjusts the date-time according to the rules of the specified adjuster.
     * If the adjusted date results in a date-time that is invalid, then the
     * specified resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @param resolver  the resolver to use, not null
     * @return a {@code ZonedDateTime} based on this date-time with the date adjusted, not null
     * @throws CalendricalException if the date-time cannot be resolved
     */
    public ZonedDateTime with(DateTimeAdjuster adjuster, ZoneResolver resolver) {
        DateTimes.checkNotNull(adjuster, "DateTimeAdjuster must not be null");
        DateTimes.checkNotNull(resolver, "ZoneResolver must not be null");
        LocalDateTime newDT = dateTime.toLocalDateTime().with(adjuster);  // TODO: should adjust ZDT, not LDT
        return (newDT == dateTime.toLocalDateTime() ? this : resolve(newDT, zone, this, resolver));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified field altered.
     * <p>
     * This method returns a new date-time based on this date-time with a new value for the specified field.
     * This can be used to change any field, for example to set the year, month of day-of-month.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * In some cases, changing the specified field can cause the resulting date-time to become invalid,
     * such as changing the month from January to February would make the day-of-month 31 invalid.
     * In cases like this, the field is responsible for resolving the date. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the returned date-time, not null
     * @param newValue  the new value of the field in the returned date-time, not null
     * @return a {@code ZonedDateTime} based on this date-time with the specified field set, not null
     * @throws CalendricalException if the value is invalid
     */
    public ZonedDateTime with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            return withDateTime(toLocalDateTime().with(field, newValue));
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the year value altered.
     * <p>
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a {@code ZonedDateTime} based on this date-time with the requested year, not null
     * @throws CalendricalException if the year value is invalid
     */
    public ZonedDateTime withYear(int year) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withYear(year);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the month-of-year value altered.
     * <p>
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return a {@code ZonedDateTime} based on this date-time with the requested month, not null
     * @throws CalendricalException if the month value is invalid
     */
    public ZonedDateTime withMonth(int month) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withMonth(month);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the day-of-month value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return a {@code ZonedDateTime} based on this date-time with the requested day, not null
     * @throws CalendricalException if the day-of-month value is invalid
     * @throws CalendricalException if the day-of-month is invalid for the month-year
     */
    public ZonedDateTime withDayOfMonth(int dayOfMonth) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withDayOfMonth(dayOfMonth);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the day-of-year altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return a {@code ZonedDateTime} based on this date with the requested day, not null
     * @throws CalendricalException if the day-of-year value is invalid
     * @throws CalendricalException if the day-of-year is invalid for the year
     */
    public ZonedDateTime withDayOfYear(int dayOfYear) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withDayOfYear(dayOfYear);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the date values altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return a {@code ZonedDateTime} based on this date-time with the requested date, not null
     * @throws CalendricalException if the any field value is invalid
     * @throws CalendricalException if the day-of-month is invalid for the month-year
     */
    public ZonedDateTime withDate(int year, int month, int dayOfMonth) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withDate(year, month, dayOfMonth);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the hour-of-day value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @return a {@code ZonedDateTime} based on this date-time with the requested hour, not null
     * @throws CalendricalException if the hour value is invalid
     */
    public ZonedDateTime withHour(int hour) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withHour(hour);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the minute-of-hour value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return a {@code ZonedDateTime} based on this date-time with the requested minute, not null
     * @throws CalendricalException if the minute value is invalid
     */
    public ZonedDateTime withMinute(int minute) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withMinute(minute);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the second-of-minute value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return a {@code ZonedDateTime} based on this date-time with the requested second, not null
     * @throws CalendricalException if the second value is invalid
     */
    public ZonedDateTime withSecond(int second) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withSecond(second);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the nano-of-second value altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code ZonedDateTime} based on this date-time with the requested nanosecond, not null
     * @throws CalendricalException if the nanos value is invalid
     */
    public ZonedDateTime withNano(int nanoOfSecond) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withNano(nanoOfSecond);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int)} and sets
     * the second field to zero.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return a {@code ZonedDateTime} based on this date-time with the requested time, not null
     * @throws CalendricalException if any field value is invalid
     */
    public ZonedDateTime withTime(int hour, int minute) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withTime(hour, minute);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the time values altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @return a {@code ZonedDateTime} based on this date-time with the requested time, not null
     * @throws CalendricalException if any field value is invalid
     */
    public ZonedDateTime withTime(int hour, int minute, int second) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withTime(hour, minute, second);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the time values altered.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code ZonedDateTime} based on this date-time with the requested time, not null
     * @throws CalendricalException if any field value is invalid
     */
    public ZonedDateTime withTime(int hour, int minute, int second, int nanoOfSecond) {
        LocalDateTime newDT = dateTime.toLocalDateTime().withTime(hour, minute, second, nanoOfSecond);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified period added.
     * <p>
     * This method returns a new date-time based on this time with the specified period added.
     * The calculation is delegated to the unit within the period.
     * <p>
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a {@code ZonedDateTime} based on this date-time with the period added, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public ZonedDateTime plus(Period period) {
        return plus(period.getAmount(), period.getUnit());
    }

    /**
     * Returns a copy of this date-time with the specified period added.
     * <p>
     * This method returns a new date-time based on this date-time with the specified period added.
     * This can be used to add any period that is defined by a unit, for example to add years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to add to the returned date-time, not null
     * @param unit  the unit of the period to add, not null
     * @return a {@code ZonedDateTime} based on this date-time with the specified period added, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public ZonedDateTime plus(long periodAmount, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            return withDateTime(toLocalDateTime().plus(periodAmount, unit));
        }
        return unit.doAdd(this, periodAmount);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in years added.
     * <p>
     * This method add the specified amount to the years field in four steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * <li>Resolve the date-time using {@link ZoneResolvers#retainOffset()}</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) plus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the years added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusYears(long years) {
        LocalDateTime newDT = dateTime.toLocalDateTime().plusYears(years);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in four steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * <li>Resolve the date-time using {@link ZoneResolvers#retainOffset()}</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 plus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the months added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusMonths(long months) {
        LocalDateTime newDT = dateTime.toLocalDateTime().plusMonths(months);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in weeks added.
     * <p>
     * This method adds the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in the 2009-01-07.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the weeks added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusWeeks(long weeks) {
        LocalDateTime newDT = dateTime.toLocalDateTime().plusWeeks(weeks);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in days added.
     * <p>
     * This method adds the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in the 2009-01-01.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the days added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusDays(long days) {
        LocalDateTime newDT = dateTime.toLocalDateTime().plusDays(days);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in hours added.
     * <p>
     * This method uses field based addition.
     * This method changes the field by the specified number of hours.
     * This may, at daylight savings cutover, result in a duration being added
     * that is more or less than the specified number of hours.
     * <p>
     * For example, consider a time-zone where the spring DST cutover means that
     * the local times 01:00 to 01:59 do not exist. Using this method, adding
     * a period of 2 hours to 00:30 will result in 02:30, but it is important
     * to note that the change in duration was only 1 hour.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the hours added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusHours(long hours) {
        LocalDateTime newDT = dateTime.toLocalDateTime().plusHours(hours);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in minutes added.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the minutes added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusMinutes(long minutes) {
        LocalDateTime newDT = dateTime.toLocalDateTime().plusMinutes(minutes);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in seconds added.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the seconds added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusSeconds(long seconds) {
        LocalDateTime newDT = dateTime.toLocalDateTime().plusSeconds(seconds);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in nanoseconds added.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the nanoseconds added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusNanos(long nanos) {
        LocalDateTime newDT = dateTime.toLocalDateTime().plusNanos(nanos);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified duration added.
     * <p>
     * This adds the specified duration to this date-time, returning a new date-time.
     * The calculation is equivalent to addition on the {@link #toInstant() instant} equivalent of this instance.
     * <p>
     * Adding a duration differs from adding a period as gaps and overlaps in
     * the local time-line are taken into account. For example, if there is a
     * gap in the local time-line of one hour from 01:00 to 02:00, then adding a
     * duration of one hour to 00:30 will yield 02:30.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a {@code ZonedDateTime} based on this date-time with the duration added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusDuration(Duration duration) {
        return duration.isZero() ? this : ofInstant(toInstant().plus(duration), zone);
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified duration added.
     * <p>
     * Adding a duration differs from adding a period as gaps and overlaps in
     * the local time-line are taken into account. For example, if there is a
     * gap in the local time-line of one hour from 01:00 to 02:00, then adding a
     * duration of one hour to 00:30 will yield 02:30.
     * <p>
     * The addition of a duration is always absolute and zone-resolvers are not required.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, positive or negative
     * @param minutes  the minutes to add, positive or negative
     * @param seconds  the seconds to add, positive or negative
     * @param nanos  the nanos to add, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the duration added, not null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime plusDuration(int hours, int minutes, int seconds, long nanos) {
        if ((hours | minutes | seconds | nanos) == 0) {
            return this;
        }
        Instant instant = toInstant().plusSeconds(hours * SECONDS_PER_HOUR + minutes * SECONDS_PER_MINUTE + seconds).plusNanos(nanos);
        return ofInstant(instant, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified period subtracted.
     * <p>
     * This method returns a new date-time based on this time with the specified period subtracted.
     * The calculation is delegated to the unit within the period.
     * <p>
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, not null
     * @return a {@code ZonedDateTime} based on this date-time with the period subtracted, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public ZonedDateTime minus(Period period) {
        return minus(period.getAmount(), period.getUnit());
    }

    /**
     * Returns a copy of this date-time with the specified period subtracted.
     * <p>
     * This method returns a new date-time based on this date-time with the specified period subtracted.
     * This can be used to subtract any period that is defined by a unit, for example to subtract years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * If the adjustment results in a date-time that is invalid for the zone,
     * then the {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodAmount  the amount of the unit to subtract from the returned date-time, not null
     * @param unit  the unit of the period to subtract, not null
     * @return a {@code ZonedDateTime} based on this date-time with the specified period subtracted, not null
     * @throws CalendricalException if the unit cannot be added to this type
     */
    public ZonedDateTime minus(long periodAmount, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(periodAmount), unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in years subtracted.
     * <p>
     * This method subtracts the specified amount to the years field in four steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * <li>Resolve the date-time using {@link ZoneResolvers#retainOffset()}</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the years subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusYears(long years) {
        LocalDateTime newDT = dateTime.toLocalDateTime().minusYears(years);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in months subtracted.
     * <p>
     * This method subtracts the specified amount to the months field in four steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * <li>Resolve the date-time using {@link ZoneResolvers#retainOffset()}</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the months subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusMonths(long months) {
        LocalDateTime newDT = dateTime.toLocalDateTime().minusMonths(months);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one week would result in the 2009-01-07.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the weeks subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusWeeks(long weeks) {
        LocalDateTime newDT = dateTime.toLocalDateTime().minusWeeks(weeks);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in days subtracted.
     * <p>
     * This method subtracts the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one day would result in the 2009-01-01.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the days subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusDays(long days) {
        LocalDateTime newDT = dateTime.toLocalDateTime().minusDays(days);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in hours subtracted.
     * <p>
     * This method uses field based subtraction.
     * This method changes the field by the specified number of hours.
     * This may, at daylight savings cutover, result in a duration being subtracted
     * that is more or less than the specified number of hours.
     * <p>
     * For example, consider a time-zone where the spring DST cutover means that
     * the local times 01:00 to 01:59 do not exist. Using this method, subtracting
     * a period of 2 hours from 02:30 will result in 00:30, but it is important
     * to note that the change in duration was only 1 hour.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the hours subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusHours(long hours) {
        LocalDateTime newDT = dateTime.toLocalDateTime().minusHours(hours);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in minutes subtracted.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the minutes subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusMinutes(long minutes) {
        LocalDateTime newDT = dateTime.toLocalDateTime().minusMinutes(minutes);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in seconds subtracted.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the seconds subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusSeconds(long seconds) {
        LocalDateTime newDT = dateTime.toLocalDateTime().minusSeconds(seconds);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in nanoseconds subtracted.
     * <p>
     * If the adjustment results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the nanoseconds subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusNanos(long nanos) {
        LocalDateTime newDT = dateTime.toLocalDateTime().minusNanos(nanos);
        return (newDT == dateTime.toLocalDateTime() ? this :
            resolve(newDT, zone, this, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified duration subtracted.
     * <p>
     * This subtracts the specified duration from this date-time, returning a new date-time.
     * The calculation is equivalent to subtraction on the {@link #toInstant() instant} equivalent of this instance.
     * <p>
     * Subtracting a duration differs from subtracting a period as gaps and overlaps in
     * the local time-line are taken into account. For example, if there is a
     * gap in the local time-line of one hour from 01:00 to 02:00, then subtracting a
     * duration of one hour from 02:30 will yield 00:30.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a {@code ZonedDateTime} based on this date-time with the duration subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusDuration(Duration duration) {
        return duration.isZero() ? this : ofInstant(toInstant().minus(duration), zone);
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified duration subtracted.
     * <p>
     * Subtracting a duration differs from subtracting a period as gaps and overlaps in
     * the local time-line are taken into account. For example, if there is a
     * gap in the local time-line of one hour from 01:00 to 02:00, then subtracting a
     * duration of one hour from 02:30 will yield 00:30.
     * <p>
     * The subtraction of a duration is always absolute and zone-resolvers are not required.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, positive or negative
     * @param minutes  the minutes to subtract, positive or negative
     * @param seconds  the seconds to subtract, positive or negative
     * @param nanos  the nanos to subtract, positive or negative
     * @return a {@code ZonedDateTime} based on this date-time with the duration subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
     * @throws CalendricalException if the result exceeds the supported range
     */
    public ZonedDateTime minusDuration(int hours, int minutes, int seconds, long nanos) {
        if ((hours | minutes | seconds | nanos) == 0) {
            return this;
        }
        Instant instant = toInstant().minusSeconds(hours * SECONDS_PER_HOUR + minutes * SECONDS_PER_MINUTE + seconds).minusNanos(nanos);
        return ofInstant(instant, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTime} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>LocalDate
     * <li>LocalTime
     * <li>LocalDateTime
     * <li>OffsetDate
     * <li>OffsetTime
     * <li>OffsetDateTime
     * <li>ZonedDateTime
     * <li>ZoneOffset
     * <li>ZoneId
     * <li>Instant
     * <li>DateTimeBuilder
     * <li>Class, returning {@code ZonedDateTime}
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == ZonedDateTime.class) {
            return (R) this;
        } else if (type == ZoneId.class) {
            return (R) zone;
        } else if (type == Class.class) {
            return (R) ZonedDateTime.class;
        } else if (type == DateTimeBuilder.class) {
            return (R) new DateTimeBuilder(this);
        }
        return dateTime.extract(type);
    }

    @Override
    public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
        return calendrical.with(EPOCH_DAY, toLocalDate().toEpochDay()).with(NANO_OF_DAY, toLocalTime().toNanoOfDay());
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this {@code ZonedDateTime} to an {@code Instant}.
     *
     * @return an Instant representing the same instant, not null
     */
    public Instant toInstant() {
        return dateTime.toInstant();
    }

    /**
     * Converts this {@code ZonedDateTime} to a {@code LocalDate}.
     *
     * @return a LocalDate representing the date fields of this date-time, not null
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Converts this {@code ZonedDateTime} to a {@code LocalTime}.
     *
     * @return a LocalTime representing the time fields of this date-time, not null
     */
    public LocalTime toLocalTime() {
        return dateTime.toLocalTime();
    }

    /**
     * Converts this {@code ZonedDateTime} to a {@code LocalDateTime}.
     *
     * @return a LocalDateTime representing the fields of this date-time, not null
     */
    public LocalDateTime toLocalDateTime() {
        return dateTime.toLocalDateTime();
    }

    /**
     * Converts this {@code ZonedDateTime} to a {@code OffsetDate}.
     *
     * @return a OffsetDate representing the date fields of this date-time, not null
     */
    public OffsetDate toOffsetDate() {
        return dateTime.toOffsetDate();
    }

    /**
     * Converts this {@code ZonedDateTime} to a {@code OffsetTime}.
     *
     * @return a OffsetTime representing the time fields of this date-time, not null
     */
    public OffsetTime toOffsetTime() {
        return dateTime.toOffsetTime();
    }

    /**
     * Converts this {@code ZonedDateTime} to a {@code OffsetDateTime}.
     *
     * @return a OffsetDateTime representing the fields of this date-time, not null
     */
    public OffsetDateTime toOffsetDateTime() {
        return dateTime;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this {@code ZonedDateTime} to the number of seconds from the epoch
     * of 1970-01-01T00:00:00Z.
     * <p>
     * Instants on the time-line after the epoch are positive, earlier are negative.
     *
     * @return the number of seconds from the epoch of 1970-01-01T00:00:00Z
     */
    public long toEpochSecond() {
        return dateTime.toEpochSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code ZonedDateTime} to another date-time based on the UTC
     * equivalent date-times then time-zone unique key.
     * <p>
     * The ordering is consistent with equals as it takes into account
     * the date-time, offset and zone.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if {@code other} is null
     */
    public int compareTo(ZonedDateTime other) {
        int compare = dateTime.compareTo(other.dateTime);
        if (compare == 0) {
            compare = zone.getID().compareTo(other.zone.getID());
        }
        return compare;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the instant of this date-time is before that of the specified date-time.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the date-time. This is equivalent to using
     * {@code dateTime1.toInstant().isBefore(dateTime2.toInstant());}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this point is before the specified date-time
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isBefore(ZonedDateTime other) {
        return dateTime.isBefore(other.dateTime);
    }

    /**
     * Checks if the instant of this date-time is after that of the specified date-time.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the date-time. This is equivalent to using
     * {@code dateTime1.toInstant().isAfter(dateTime2.toInstant());}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the specified date-time
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isAfter(ZonedDateTime other) {
        return dateTime.isAfter(other.dateTime);
    }

    /**
     * Checks if the instant of this date-time is equal to that of the specified date-time.
     * <p>
     * This method differs from the comparison in {@link #compareTo} and {@link #equals}
     * in that it only compares the instant of the date-time. This is equivalent to using
     * {@code dateTime1.toInstant().equals(dateTime2.toInstant());}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the specified date-time
     * @throws NullPointerException if {@code other} is null
     */
    public boolean equalInstant(ZonedDateTime other) {
        return dateTime.equalInstant(other.dateTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date-time is equal to another date-time.
     * <p>
     * The comparison is based on the offset date-time and the zone.
     * To compare for the same instant on the time-line, use {@link #equalInstant}.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date-time
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ZonedDateTime) {
            ZonedDateTime other = (ZonedDateTime) obj;
            return dateTime.equals(other.dateTime) &&
                zone.equals(other.zone);
        }
        return false;
    }

    /**
     * A hash code for this date-time.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return dateTime.hashCode() ^ zone.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date-time as a {@code String}, such as
     * {@code 2007-12-03T10:15:30+01:00[Europe/Paris]}.
     * <p>
     * The output will be one of the following formats:
     * <ul>
     * <li>{@code yyyy-MM-dd'T'HH:mmXXXXX'['I']'}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssXXXXX'['I']'}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnXXXXX'['I']'}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnnnnXXXXX'['I']'}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnnnnnnnXXXXX'['I']'}</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this date-time, not null
     */
    @Override
    public String toString() {
        return dateTime.toString() + '[' + zone.toString() + ']';
    }

    /**
     * Outputs this date-time as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted date-time string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    public String toString(CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.print(this);
    }

}
