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

import static javax.time.calendrical.ChronoField.INSTANT_SECONDS;
import static javax.time.calendrical.ChronoField.NANO_OF_SECOND;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.time.calendrical.ChronoField;
import javax.time.calendrical.ChronoUnit;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeAdjusters;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.ChronoZonedDateTime;
import javax.time.chrono.ISOChrono;
import javax.time.format.DateTimeFormatter;
import javax.time.format.DateTimeFormatters;
import javax.time.format.DateTimeParseException;
import javax.time.jdk8.DefaultInterfaceChronoZonedDateTime;
import javax.time.zone.ZoneOffsetTransition;
import javax.time.zone.ZoneRules;

/**
 * A date-time with a time-zone in the ISO-8601 calendar system,
 * such as {@code 2007-12-03T10:15:30+01:00 Europe/Paris}.
 * <p>
 * {@code ZonedDateTime} is an immutable representation of a date-time with a time-zone.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * a time-zone and zone offset. For example, the value
 * "2nd October 2007 at 13:45.30.123456789 +02:00 in the Europe/Paris time-zone"
 * can be stored in a {@code ZonedDateTime}.
 * <p>
 * This class handles both the local time-line of {@code LocalDateTime} and
 * the instant time-line of {@code Instant}.
 * The difference between the two time-lines is the offset from UTC/Greenwich,
 * represented by a {@code ZoneOffset}.
 * <p>
 * Converting between the two time-lines involves calculating the offset using the
 * {@link ZoneRules rules} accessed from the {@code ZoneId}.
 * Obtaining the offset for an instant is simple, as there is exactly one valid
 * offset for each instant. By contrast, obtaining the offset for a local date-time
 * is not straightforward. There are three cases:
 * <p><ul>
 * <li>Normal, with one valid offset. For the vast majority of the year, the normal
 *  case applies, where there is a single valid offset for the local date-time.</li>
 * <li>Gap, with zero valid offsets. This is when clocks jump forward typically
 *  due to the spring daylight savings change from "winter" to "summer".
 *  In a gap there are local date-time values with no valid offset.</li>
 * <li>Overlap, with two valid offsets. This is when clocks are set back typically
 *  due to the autumn daylight savings change from "summer" to "winter".
 *  In an overlap there are local date-time values with two valid offsets.</li>
 * </ul><p>
 * <p>
 * Any method that converts directly or implicitly from a local date-time to an
 * instant by obtaining the offset has the potential to be complicated.
 * <p>
 * A {@code ZonedDateTime} effectively holds three objects internally,
 * a {@code LocalDateTime}, a {@code ZoneId} and the resolved {@code ZoneOffset}.
 * The offset and local date-time can be combined to define an instant.
 * The zone ID is used to obtain the rules for how and when the offset changes.
 * <p>
 * Two methods, {@link #withEarlierOffsetAtOverlap()} and {@link #withLaterOffsetAtOverlap()},
 * help manage the case of an overlap.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class ZonedDateTime
        extends DefaultInterfaceChronoZonedDateTime<ISOChrono>
        implements ChronoZonedDateTime<ISOChrono>, DateTime, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -6260982410461394882L;

    /**
     * The local date-time.
     */
    private final LocalDateTime dateTime;
    /**
     * The offset from UTC/Greenwich.
     */
    private final ZoneOffset offset;
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
     * Obtains the current date-time from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date-time.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * The offset will be calculated from the specified time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock, not null
     */
    public static ZonedDateTime now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current date-time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date-time.
     * The zone and offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date-time, not null
     */
    public static ZonedDateTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        final Instant now = clock.instant();  // called once
        return ofInstant(now, clock.getZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} from a local date-time.
     * <p>
     * This creates a zoned date-time matching the input local date-time as closely as possible.
     * Time-zone rules, such as daylight savings, mean that not every local date-time
     * is valid for the specified zone, thus the local date-time may be adjusted.
     * <p>
     * The local date-time is resolved to a single instant on the time-line.
     * This is achieved by finding a valid offset from UTC/Greenwich for the local
     * date-time as defined by the {@link ZoneRules rules} of the zone ID.
     *<p>
     * In most cases, there is only one valid offset for a local date-time.
     * In the case of an overlap, when clocks are set back, there are two valid offsets.
     * This method uses the earlier offset typically corresponding to "summer".
     * <p>
     * In the case of a gap, when clocks jump forward, there is no valid offset.
     * Instead, the local date-time is adjusted to be later by the length of the gap.
     * For a typical one hour daylight savings change, the local date-time will be
     * moved one hour later into the offset typically corresponding to "summer".
     *
     * @param localDateTime  the local date-time, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     */
    public static ZonedDateTime of(LocalDateTime localDateTime, ZoneId zone) {
        return ofLocal(localDateTime, zone, null);
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from a local date-time
     * using the preferred offset if possible.
     * <p>
     * The local date-time is resolved to a single instant on the time-line.
     * This is achieved by finding a valid offset from UTC/Greenwich for the local
     * date-time as defined by the {@link ZoneRules rules} of the zone ID.
     *<p>
     * In most cases, there is only one valid offset for a local date-time.
     * In the case of an overlap, where clocks are set back, there are two valid offsets.
     * If the preferred offset is one of the valid offsets then it is used.
     * Otherwise the earlier valid offset is used, typically corresponding to "summer".
     * <p>
     * In the case of a gap, where clocks jump forward, there is no valid offset.
     * Instead, the local date-time is adjusted to be later by the length of the gap.
     * For a typical one hour daylight savings change, the local date-time will be
     * moved one hour later into the offset typically corresponding to "summer".
     *
     * @param localDateTime  the local date-time, not null
     * @param zone  the time-zone, not null
     * @param preferredOffset  the zone offset, null if no preference
     * @return the zoned date-time, not null
     */
    public static ZonedDateTime ofLocal(LocalDateTime localDateTime, ZoneId zone, ZoneOffset preferredOffset) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(zone, "zone");
        if (zone instanceof ZoneOffset) {
            return new ZonedDateTime(localDateTime, (ZoneOffset) zone, zone);
        }
        ZoneRules rules = zone.getRules();
        List<ZoneOffset> validOffsets = rules.getValidOffsets(localDateTime);
        ZoneOffset offset;
        if (validOffsets.size() == 1) {
            offset = validOffsets.get(0);
        } else if (validOffsets.size() == 0) {
            ZoneOffsetTransition trans = rules.getTransition(localDateTime);
            localDateTime = localDateTime.plusSeconds(trans.getDuration().getSeconds());
            offset = trans.getOffsetAfter();
        } else {
            if (preferredOffset != null && validOffsets.contains(preferredOffset)) {
                offset = preferredOffset;
            } else {
                offset = Objects.requireNonNull(validOffsets.get(0), "offset");  // protect against bad ZoneRules
            }
        }
        return new ZonedDateTime(localDateTime, offset, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} from an {@code Instant}.
     * <p>
     * This creates a zoned date-time with the same instant as that specified.
     * Calling {@link #toInstant()} will return an instant equal to the one used here.
     * <p>
     * Converting an instant to a zoned date-time is simple as there is only one valid
     * offset for each instant.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public static ZonedDateTime ofInstant(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
        return create(instant.getEpochSecond(), instant.getNano(), zone);
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} from the instant formed by combining
     * the local date-time and offset.
     * <p>
     * This creates a zoned date-time by {@link LocalDateTime#toInstant(ZoneOffset) combining}
     * the {@code LocalDateTime} and {@code ZoneOffset}.
     * This combination uniquely specifies an instant without ambiguity.
     * <p>
     * Converting an instant to a zoned date-time is simple as there is only one valid
     * offset for each instant. If the valid offset is different to the offset specified,
     * the the date-time and offset of the zoned date-time will differ from those specified.
     * <p>
     * If the {@code ZoneId} to be used is a {@code ZoneOffset}, this method is equivalent
     * to {@link #of(LocalDateTime, ZoneId)}.
     *
     * @param localDateTime  the local date-time, not null
     * @param offset  the zone offset, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     */
    public static ZonedDateTime ofInstant(LocalDateTime localDateTime, ZoneOffset offset, ZoneId zone) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(offset, "offset");
        Objects.requireNonNull(zone, "zone");
        return create(localDateTime.toEpochSecond(offset), localDateTime.getNano(), zone);
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     *
     * @param epochSecond  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfSecond  the nanosecond within the second, from 0 to 999,999,999
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    private static ZonedDateTime create(long epochSecond, int nanoOfSecond, ZoneId zone) {
        ZoneRules rules = zone.getRules();
        Instant instant = Instant.ofEpochSecond(epochSecond, nanoOfSecond);  // TODO: rules should be queryable by epochSeconds
        ZoneOffset offset = rules.getOffset(instant);
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, offset);
        return new ZonedDateTime(ldt, offset, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} strictly validating the
     * combination of local date-time, offset and zone ID.
     * <p>
     * This creates a zoned date-time ensuring that the offset is valid for the
     * local date-time according to the rules of the specified zone.
     * If the offset is invalid, an exception is thrown.
     *
     * @param localDateTime  the local date-time, not null
     * @param offset  the zone offset, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     */
    public static ZonedDateTime ofStrict(LocalDateTime localDateTime, ZoneOffset offset, ZoneId zone) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(offset, "offset");
        Objects.requireNonNull(zone, "zone");
        ZoneRules rules = zone.getRules();
        if (rules.isValidOffset(localDateTime, offset) == false) {
            ZoneOffsetTransition trans = rules.getTransition(localDateTime);
            if (trans != null && trans.isGap()) {
                // error message says daylight savings for simplicity
                // even though there are other kinds of gaps
                throw new DateTimeException("LocalDateTime '" + localDateTime +
                        "' does not exist in zone '" + zone +
                        "' due to a gap in the local time-line, typically caused by daylight savings");
            }
            throw new DateTimeException("ZoneOffset '" + offset + "' is not valid for LocalDateTime '" +
                    localDateTime + "' in zone '" + zone + "'");
        }
        return new ZonedDateTime(localDateTime, offset, zone);
    }

    /**
     * Obtains an instance of {@code ZonedDateTime} leniently, for advanced use cases,
     * allowing any combination of local date-time, offset and zone ID.
     * <p>
     * This creates a zoned date-time with no checks other than no nulls.
     * This means that the resulting zoned date-time may have an offset that is in conflict
     * with the zone ID.
     * <p>
     * This method is intended for advanced use cases.
     * For example, consider the case where a zoned date-time with valid fields is created
     * and then stored in a database or serialization-based store. At some later point,
     * the object is then re-loaded. However, between those points in time, the government
     * that defined the time-zone has changed the rules, such that the originally stored
     * local date-time now does not occur. This method can be used to create the object
     * in an "invalid" state, despite the change in rules.
     *
     * @param localDateTime  the local date-time, not null
     * @param offset  the zone offset, not null
     * @param zone  the time-zone, not null
     * @return the zoned date-time, not null
     */
    private static ZonedDateTime ofLenient(LocalDateTime localDateTime, ZoneOffset offset, ZoneId zone) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(offset, "offset");
        Objects.requireNonNull(zone, "zone");
        if (zone instanceof ZoneOffset && offset.equals(zone) == false) {
            throw new IllegalArgumentException("ZoneId must match ZoneOffset");
        }
        return new ZonedDateTime(localDateTime, offset, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZonedDateTime} from a date-time object.
     * <p>
     * A {@code DateTimeAccessor} represents some form of date and time information.
     * This factory converts the arbitrary date-time object to an instance of {@code ZonedDateTime}.
     * <p>
     * The conversion will try to obtain an instant first, then try to obtain the
     * local date-time.
     *
     * @param dateTime  the date-time object to convert, not null
     * @return the zoned date-time, not null
     * @throws DateTimeException if unable to convert to an {@code ZonedDateTime}
     */
    public static ZonedDateTime from(DateTimeAccessor dateTime) {
        if (dateTime instanceof ZonedDateTime) {
            return (ZonedDateTime) dateTime;
        }
        try {
            ZoneId zone = ZoneId.from(dateTime);
            try {
                long epochSecond = dateTime.getLong(INSTANT_SECONDS);
                int nanoOfSecond = dateTime.get(NANO_OF_SECOND);
                return create(epochSecond, nanoOfSecond, zone);

            } catch (DateTimeException ex1) {
                LocalDateTime ldt = LocalDateTime.from(dateTime);
                return of(ldt, zone);
            }
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to convert DateTimeAccessor to ZonedDateTime: " + dateTime.getClass(), ex);
        }
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
     * @throws DateTimeParseException if the text cannot be parsed
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
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static ZonedDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.parse(text, ZonedDateTime.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param dateTime  the date-time, validated as not null
     * @param offset  the zone offset, validated as not null
     * @param zone  the time-zone, validated as not null
     */
    ZonedDateTime(LocalDateTime dateTime, ZoneOffset offset, ZoneId zone) {
        this.dateTime = dateTime;
        this.offset = offset;
        this.zone = zone;
    }

    /**
     * Resolves the new local date-time using this zone ID, retaining the offset if possible.
     *
     * @param newDateTime  the new local date-time, not null
     * @return the zoned date-time, not null
     */
    private ZonedDateTime resolveLocal(LocalDateTime newDateTime) {
        return ofLocal(newDateTime, zone, offset);
    }

    /**
     * Resolves the new local date-time using the offset to identify the instant.
     *
     * @param newDateTime  the new local date-time, not null
     * @return the zoned date-time, not null
     */
    private ZonedDateTime resolveInstant(LocalDateTime newDateTime) {
        return ofInstant(newDateTime, offset, zone);
    }

    /**
     * Resolves the offset into this zoned date-time.
     * <p>
     * This will use the new offset to find the instant, which is then looked up
     * using the zone ID to find the actual offset to use.
     *
     * @param offset  the offset, not null
     * @return the zoned date-time, not null
     */
    private ZonedDateTime resolveOffset(ZoneOffset offset) {
        long epSec = dateTime.toEpochSecond(offset);
        return create(epSec, dateTime.getNano(), zone);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field) {
        return field instanceof ChronoField || (field != null && field.doIsSupported(this));
    }

    @Override
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof ChronoField) {
            return dateTime.range(field);
        }
        return field.doRange(this);
    }

    @Override
    public int get(DateTimeField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case INSTANT_SECONDS: throw new DateTimeException("Field too large for an int: " + field);
                case OFFSET_SECONDS: return getOffset().getTotalSeconds();
            }
            return dateTime.get(field);
        }
        return super.get(field);
    }

    @Override
    public long getLong(DateTimeField field) {
        if (field instanceof ChronoField) {
            switch ((ChronoField) field) {
                case INSTANT_SECONDS: return toEpochSecond();
                case OFFSET_SECONDS: return getOffset().getTotalSeconds();
            }
            return dateTime.getLong(field);
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset, such as '+01:00'.
     * <p>
     * This is the offset of the local date-time from UTC/Greenwich.
     *
     * @return the zone offset, not null
     */
    @Override
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Returns a copy of this date-time changing the zone offset to the
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
     */
    @Override
    public ZonedDateTime withEarlierOffsetAtOverlap() {
        ZoneOffsetTransition trans = getZone().getRules().getTransition(dateTime);
        if (trans != null && trans.isOverlap()) {
            ZoneOffset earlierOffset = trans.getOffsetBefore();
            if (earlierOffset.equals(offset) == false) {
                return new ZonedDateTime(dateTime, earlierOffset, zone);
            }
        }
        return this;
    }

    /**
     * Returns a copy of this date-time changing the zone offset to the
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
     */
    @Override
    public ZonedDateTime withLaterOffsetAtOverlap() {
        ZoneOffsetTransition trans = getZone().getRules().getTransition(getDateTime());
        if (trans != null) {
            ZoneOffset laterOffset = trans.getOffsetAfter();
            if (laterOffset.equals(offset) == false) {
                return new ZonedDateTime(dateTime, laterOffset, zone);
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time-zone, such as 'Europe/Paris'.
     * <p>
     * This returns the zone ID. This identifies the time-zone {@link ZoneRules rules}
     * that determine when and how the offset from UTC/Greenwich changes.
     * <p>
     * The zone ID may be same as the {@link #getOffset() offset}.
     * If this is true, then any future calculations, such as addition or subtraction,
     * have no complex edge cases due to time-zone rules.
     * See also {@link #withFixedOffsetZone()}.
     *
     * @return the time-zone, not null
     */
    @Override
    public ZoneId getZone() {
        return zone;
    }

    /**
     * Returns a copy of this date-time with a different time-zone,
     * retaining the local date-time if possible.
     * <p>
     * This method changes the time-zone and retains the local date-time.
     * The local date-time is only changed if it is invalid for the new zone,
     * determined using the same approach as
     * {@link #ofLocal(LocalDateTime, ZoneId, ZoneOffset)}.
     * <p>
     * To change the zone and adjust the local date-time,
     * use {@link #withZoneSameInstant(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to change to, not null
     * @return a {@code ZonedDateTime} based on this date-time with the requested zone, not null
     */
    @Override
    public ZonedDateTime withZoneSameLocal(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        return this.zone.equals(zone) ? this : ofLocal(dateTime, zone, offset);
    }

    /**
     * Returns a copy of this date-time with a different time-zone,
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
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    public ZonedDateTime withZoneSameInstant(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        return this.zone.equals(zone) ? this :
            create(dateTime.toEpochSecond(offset), dateTime.getNano(), zone);
    }

    /**
     * Returns a copy of this date-time with the zone ID set to the offset.
     * <p>
     * This returns a zone date-time where the zone ID is the same as {@link #getOffset()}.
     * The local date-time, offset and instant of the result will be the same as in this date-time.
     * <p>
     * Setting the date-time to a fixed single offset means that any future
     * calculations, such as addition or subtraction, have no complex edge cases
     * due to time-zone rules.
     * This might also be useful when sending a zoned date-time across a network,
     * as most protocols, such as ISO-8601, only handle offsets,
     * and not region-based zone IDs.
     * <p>
     * This is equivalent to {@code ZonedDateTime.of(zdt.getDateTime(), zdt.getOffset())}.
     *
     * @return a {@code ZonedDateTime} with the zone ID set to the offset, not null
     */
    public ZonedDateTime withFixedOffsetZone() {
        return this.zone.equals(offset) ? this : new ZonedDateTime(dateTime, offset, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the {@code LocalDateTime} part of this date-time.
     * <p>
     * This returns a {@code LocalDateTime} with the same year, month, day and time
     * as this date-time.
     *
     * @return the local date-time part of this date-time, not null
     */
    @Override  // override for return type
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the {@code LocalDate} part of this date-time.
     * <p>
     * This returns a {@code LocalDate} with the same year, month and day
     * as this date-time.
     *
     * @return the date part of this date-time, not null
     */
    @Override  // override for return type
    public LocalDate getDate() {
        return dateTime.getDate();
    }

    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     * <p>
     * The year returned by this method is proleptic as per {@code get(YEAR)}.
     * To obtain the year-of-era, use {@code get(YEAR_OF_ERA}.
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
     * Gets the {@code LocalTime} part of this date-time.
     * <p>
     * This returns a {@code LocalTime} with the same hour, minute, second and
     * nanosecond as this date-time.
     *
     * @return the time part of this date-time, not null
     */
    @Override  // override for Javadoc and performance
    public LocalTime getTime() {
        return dateTime.getTime();
    }

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
     * Returns an adjusted date-time based on this date-time.
     * <p>
     * This adjusts the date-time according to the rules of the specified adjuster.
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date-time to the last day of the month.
     * A selection of common adjustments is provided in {@link DateTimeAdjusters}.
     * These include finding the "last day of the month" and "next Wednesday".
     * The adjuster is responsible for handling special cases, such as the varying
     * lengths of month and leap years.
     * <p>
     * In addition, all principal classes implement the {@link WithAdjuster} interface,
     * including this one. For example, {@link LocalDate} implements the adjuster interface.
     * As such, this code will compile and run:
     * <pre>
     *  dateTime.with(date);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return a {@code ZonedDateTime} based on this date-time with the adjustment made, not null
     * @throws DateTimeException if the adjustment cannot be made
     */
    @Override
    public ZonedDateTime with(WithAdjuster adjuster) {
        if (adjuster instanceof LocalDate) {
            return resolveLocal(LocalDateTime.of((LocalDate) adjuster, dateTime.getTime()));
        } else if (adjuster instanceof LocalTime) {
            return resolveLocal(LocalDateTime.of(dateTime.getDate(), (LocalTime) adjuster));
        } else if (adjuster instanceof LocalDateTime) {
            return resolveLocal((LocalDateTime) adjuster);
        } else if (adjuster instanceof ZoneOffset) {
            return resolveOffset((ZoneOffset) adjuster);
        }
        return (ZonedDateTime) adjuster.doWithAdjustment(this);
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
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the result, not null
     * @param newValue  the new value of the field in the result
     * @return a {@code ZonedDateTime} based on this date-time with the specified field set, not null
     * @throws DateTimeException if the value is invalid
     */
    @Override
    public ZonedDateTime with(DateTimeField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            switch (f) {
                case INSTANT_SECONDS: return create(newValue, getNano(), zone);
                case OFFSET_SECONDS: {
                    ZoneOffset offset = ZoneOffset.ofTotalSeconds(f.checkValidIntValue(newValue));
                    return resolveOffset(offset);
                }
            }
            return resolveLocal(dateTime.with(field, newValue));
        }
        return field.doWith(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the year value altered.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#withYear(int) changing the year} of the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the result, from MIN_YEAR to MAX_YEAR
     * @return a {@code ZonedDateTime} based on this date-time with the requested year, not null
     * @throws DateTimeException if the year value is invalid
     */
    public ZonedDateTime withYear(int year) {
        return resolveLocal(dateTime.withYear(year));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the month-of-year value altered.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#withMonth(int) changing the month} of the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to set in the result, from 1 (January) to 12 (December)
     * @return a {@code ZonedDateTime} based on this date-time with the requested month, not null
     * @throws DateTimeException if the month-of-year value is invalid
     */
    public ZonedDateTime withMonth(int month) {
        return resolveLocal(dateTime.withMonth(month));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the day-of-month value altered.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#withDayOfMonth(int) changing the day-of-month} of the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the result, from 1 to 28-31
     * @return a {@code ZonedDateTime} based on this date-time with the requested day, not null
     * @throws DateTimeException if the day-of-month value is invalid
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public ZonedDateTime withDayOfMonth(int dayOfMonth) {
        return resolveLocal(dateTime.withDayOfMonth(dayOfMonth));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the day-of-year altered.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#withDayOfYear(int) changing the day-of-year} of the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the result, from 1 to 365-366
     * @return a {@code ZonedDateTime} based on this date with the requested day, not null
     * @throws DateTimeException if the day-of-year value is invalid
     * @throws DateTimeException if the day-of-year is invalid for the year
     */
    public ZonedDateTime withDayOfYear(int dayOfYear) {
        return resolveLocal(dateTime.withDayOfYear(dayOfYear));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the hour-of-day value altered.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#withHour(int) changing the time} of the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to set in the result, from 0 to 23
     * @return a {@code ZonedDateTime} based on this date-time with the requested hour, not null
     * @throws DateTimeException if the hour value is invalid
     */
    public ZonedDateTime withHour(int hour) {
        return resolveLocal(dateTime.withHour(hour));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the minute-of-hour value altered.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#withMinute(int) changing the time} of the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minute  the minute-of-hour to set in the result, from 0 to 59
     * @return a {@code ZonedDateTime} based on this date-time with the requested minute, not null
     * @throws DateTimeException if the minute value is invalid
     */
    public ZonedDateTime withMinute(int minute) {
        return resolveLocal(dateTime.withMinute(minute));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the second-of-minute value altered.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#withSecond(int) changing the time} of the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param second  the second-of-minute to set in the result, from 0 to 59
     * @return a {@code ZonedDateTime} based on this date-time with the requested second, not null
     * @throws DateTimeException if the second value is invalid
     */
    public ZonedDateTime withSecond(int second) {
        return resolveLocal(dateTime.withSecond(second));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the nano-of-second value altered.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#withNano(int) changing the time} of the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to set in the result, from 0 to 999,999,999
     * @return a {@code ZonedDateTime} based on this date-time with the requested nanosecond, not null
     * @throws DateTimeException if the nano value is invalid
     */
    public ZonedDateTime withNano(int nanoOfSecond) {
        return resolveLocal(dateTime.withNano(nanoOfSecond));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the time truncated.
     * <p>
     * Truncation returns a copy of the original date-time with fields
     * smaller than the specified unit set to zero.
     * For example, truncating with the {@link ChronoUnit#MINUTES minutes} unit
     * will set the second-of-minute and nano-of-second field to zero.
     * <p>
     * Not all units are accepted. The {@link ChronoUnit#DAYS days} unit and time
     * units with an exact duration can be used, other units throw an exception.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#truncatedTo(javax.time.calendrical.PeriodUnit) truncating}
     * the underlying local date-time. This is then converted back to a
     * {@code ZoneDateTime}, using the zone ID to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param unit  the unit to truncate to, not null
     * @return a {@code ZonedDateTime} based on this date-time with the time truncated, not null
     * @throws DateTimeException if unable to truncate
     */
    public ZonedDateTime truncatedTo(PeriodUnit unit) {
        return resolveLocal(dateTime.truncatedTo(unit));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified period added.
     * <p>
     * This method returns a new date-time based on this time with the specified period added.
     * The adjuster is typically {@link Period} but may be any other type implementing
     * the {@link javax.time.calendrical.DateTime.PlusAdjuster} interface.
     * The calculation is delegated to the specified adjuster, which typically calls
     * back to {@link #plus(long, PeriodUnit)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code ZonedDateTime} based on this date-time with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public ZonedDateTime plus(PlusAdjuster adjuster) {
        return (ZonedDateTime) adjuster.doPlusAdjustment(this);
    }

    /**
     * Returns a copy of this date-time with the specified period added.
     * <p>
     * This method returns a new date-time based on this date-time with the specified period added.
     * This can be used to add any period that is defined by a unit, for example to add years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * The calculation for date and time units differ.
     * <p>
     * Date units operate on the local time-line.
     * The period is first added to the local date-time, then converted back
     * to a zoned date-time using the zone ID.
     * The conversion uses {@link #ofLocal(LocalDateTime, ZoneId, ZoneOffset)}
     * with the offset before the addition.
     * <p>
     * Time units operate on the instant time-line.
     * The period is first added to the local date-time, then converted back to
     * a zoned date-time using the zone ID.
     * The conversion uses {@link #ofInstant(LocalDateTime, ZoneOffset, ZoneId)}
     * with the offset before the addition.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount of the unit to add to the result, may be negative
     * @param unit  the unit of the period to add, not null
     * @return a {@code ZonedDateTime} based on this date-time with the specified period added, not null
     * @throws DateTimeException if the unit cannot be added to this type
     */
    public ZonedDateTime plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof ChronoUnit) {
            ChronoUnit u = (ChronoUnit) unit;
            if (u.isDateUnit()) {
                return resolveLocal(dateTime.plus(amountToAdd, unit));
            } else {
                return resolveInstant(dateTime.plus(amountToAdd, unit));
            }
        }
        return unit.doPlus(this, amountToAdd);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in years added.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#plusYears(long) adding years} to the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime plusYears(long years) {
        return resolveLocal(dateTime.plusYears(years));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in months added.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#plusMonths(long) adding months} to the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the months added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime plusMonths(long months) {
        return resolveLocal(dateTime.plusMonths(months));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in weeks added.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#plusWeeks(long) adding weeks} to the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the weeks added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime plusWeeks(long weeks) {
        return resolveLocal(dateTime.plusWeeks(weeks));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in days added.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#plusDays(long) adding days} to the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the days added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime plusDays(long days) {
        return resolveLocal(dateTime.plusDays(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in hours added.
     * <p>
     * This operates on the instant time-line, such that adding one hour will
     * always be a duration of one hour later.
     * This may cause the local date-time to change by an amount other than one hour.
     * Note that this is a different approach to that used by days, months and years,
     * thus adding one day is not the same as adding 24 hours.
     * <p>
     * For example, consider a time-zone where the spring DST cutover means that the
     * local times 01:00 to 01:59 occur twice changing from offset +02:00 to +01:00.
     * <p><ul>
     * <li>Adding one hour to 00:30+02:00 will result in 01:30+02:00
     * <li>Adding one hour to 01:30+02:00 will result in 01:30+01:00
     * <li>Adding one hour to 01:30+01:00 will result in 02:30+01:00
     * <li>Adding three hours to 00:30+02:00 will result in 02:30+01:00
     * </ul><p>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the hours added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime plusHours(long hours) {
        return resolveInstant(dateTime.plusHours(hours));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in minutes added.
     * <p>
     * This operates on the instant time-line, such that adding one minute will
     * always be a duration of one minute later.
     * This may cause the local date-time to change by an amount other than one minute.
     * Note that this is a different approach to that used by days, months and years.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the minutes added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime plusMinutes(long minutes) {
        return resolveInstant(dateTime.plusMinutes(minutes));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in seconds added.
     * <p>
     * This operates on the instant time-line, such that adding one second will
     * always be a duration of one second later.
     * This may cause the local date-time to change by an amount other than one second.
     * Note that this is a different approach to that used by days, months and years.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the seconds added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime plusSeconds(long seconds) {
        return resolveInstant(dateTime.plusSeconds(seconds));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in nanoseconds added.
     * <p>
     * This operates on the instant time-line, such that adding one nano will
     * always be a duration of one nano later.
     * This may cause the local date-time to change by an amount other than one nano.
     * Note that this is a different approach to that used by days, months and years.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the nanoseconds added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime plusNanos(long nanos) {
        return resolveInstant(dateTime.plusNanos(nanos));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified period subtracted.
     * <p>
     * This method returns a new date-time based on this time with the specified period subtracted.
     * The adjuster is typically {@link Period} but may be any other type implementing
     * the {@link javax.time.calendrical.DateTime.MinusAdjuster} interface.
     * The calculation is delegated to the specified adjuster, which typically calls
     * back to {@link #minus(long, PeriodUnit)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code ZonedDateTime} based on this date-time with the subtraction made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public ZonedDateTime minus(MinusAdjuster adjuster) {
        return (ZonedDateTime) adjuster.doMinusAdjustment(this);
    }

    /**
     * Returns a copy of this date-time with the specified period subtracted.
     * <p>
     * This method returns a new date-time based on this date-time with the specified period subtracted.
     * This can be used to subtract any period that is defined by a unit, for example to subtract years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * <p>
     * The calculation for date and time units differ.
     * <p>
     * Date units operate on the local time-line.
     * The period is first subtracted from the local date-time, then converted back
     * to a zoned date-time using the zone ID.
     * The conversion uses {@link #ofLocal(LocalDateTime, ZoneId, ZoneOffset)}
     * with the offset before the subtraction.
     * <p>
     * Time units operate on the instant time-line.
     * The period is first subtracted from the local date-time, then converted back to
     * a zoned date-time using the zone ID.
     * The conversion uses {@link #ofInstant(LocalDateTime, ZoneOffset, ZoneId)}
     * with the offset before the subtraction.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount of the unit to subtract from the result, may be negative
     * @param unit  the unit of the period to subtract, not null
     * @return a {@code ZonedDateTime} based on this date-time with the specified period subtracted, not null
     * @throws DateTimeException if the unit cannot be added to this type
     */
    @Override
    public ZonedDateTime minus(long amountToSubtract, PeriodUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in years subtracted.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#minusYears(long) subtracting years} to the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the years subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime minusYears(long years) {
        return (years == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-years));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in months subtracted.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#minusMonths(long) subtracting months} to the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the months subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime minusMonths(long months) {
        return (months == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-months));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in weeks subtracted.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#minusWeeks(long) subtracting weeks} to the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the weeks subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime minusWeeks(long weeks) {
        return (weeks == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeks));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in days subtracted.
     * <p>
     * This operates on the local time-line,
     * {@link LocalDateTime#minusDays(long) subtracting days} to the local date-time.
     * This is then converted back to a {@code ZonedDateTime}, using the zone ID
     * to obtain the offset.
     * <p>
     * When converting back to {@code ZonedDateTime}, if the local date-time is in an overlap,
     * then the offset will be retained if possible, otherwise the earlier offset will be used.
     * If in a gap, the local date-time will be adjusted forward by the length of the gap.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the days subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime minusDays(long days) {
        return (days == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in hours subtracted.
     * <p>
     * This operates on the instant time-line, such that subtracting one hour will
     * always be a duration of one hour earlier.
     * This may cause the local date-time to change by an amount other than one hour.
     * Note that this is a different approach to that used by days, months and years,
     * thus subtracting one day is not the same as adding 24 hours.
     * <p>
     * For example, consider a time-zone where the spring DST cutover means that the
     * local times 01:00 to 01:59 occur twice changing from offset +02:00 to +01:00.
     * <p><ul>
     * <li>Subtracting one hour from 02:30+01:00 will result in 01:30+02:00
     * <li>Subtracting one hour from 01:30+01:00 will result in 01:30+02:00
     * <li>Subtracting one hour from 01:30+02:00 will result in 00:30+01:00
     * <li>Subtracting three hours from 02:30+01:00 will result in 00:30+02:00
     * </ul><p>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the hours subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime minusHours(long hours) {
        return (hours == Long.MIN_VALUE ? plusHours(Long.MAX_VALUE).plusHours(1) : plusHours(-hours));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in minutes subtracted.
     * <p>
     * This operates on the instant time-line, such that subtracting one minute will
     * always be a duration of one minute earlier.
     * This may cause the local date-time to change by an amount other than one minute.
     * Note that this is a different approach to that used by days, months and years.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the minutes subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime minusMinutes(long minutes) {
        return (minutes == Long.MIN_VALUE ? plusMinutes(Long.MAX_VALUE).plusMinutes(1) : plusMinutes(-minutes));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in seconds subtracted.
     * <p>
     * This operates on the instant time-line, such that subtracting one second will
     * always be a duration of one second earlier.
     * This may cause the local date-time to change by an amount other than one second.
     * Note that this is a different approach to that used by days, months and years.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the seconds subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime minusSeconds(long seconds) {
        return (seconds == Long.MIN_VALUE ? plusSeconds(Long.MAX_VALUE).plusSeconds(1) : plusSeconds(-seconds));
    }

    /**
     * Returns a copy of this {@code ZonedDateTime} with the specified period in nanoseconds subtracted.
     * <p>
     * This operates on the instant time-line, such that subtracting one nano will
     * always be a duration of one nano earlier.
     * This may cause the local date-time to change by an amount other than one nano.
     * Note that this is a different approach to that used by days, months and years.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return a {@code ZonedDateTime} based on this date-time with the nanoseconds subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ZonedDateTime minusNanos(long nanos) {
        return (nanos == Long.MIN_VALUE ? plusNanos(Long.MAX_VALUE).plusNanos(1) : plusNanos(-nanos));
    }

    //-----------------------------------------------------------------------
    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof ZonedDateTime == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
        if (unit instanceof ChronoUnit) {
            ZonedDateTime end = (ZonedDateTime) endDateTime;
            end = end.withZoneSameInstant(offset);
            return dateTime.periodUntil(end.dateTime, unit);
        }
        return unit.between(this, endDateTime).getAmount();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date-time is equal to another date-time.
     * <p>
     * The comparison is based on the offset date-time and the zone.
     * Only objects of type {@code ZonedDateTime} are compared, other types return false.
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
                offset.equals(other.offset) &&
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
        return dateTime.hashCode() ^ offset.hashCode() ^ Integer.rotateLeft(zone.hashCode(), 3);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date-time as a {@code String}, such as
     * {@code 2007-12-03T10:15:30+01:00[Europe/Paris]}.
     * <p>
     * The format consists of the {@code LocalDateTime} followed by the {@code ZoneOffset}.
     * If the {@code ZoneId} is not the same as the offset, then the ID is output.
     * The output is compatible with ISO-8601 if the offset and ID are the same.
     *
     * @return a string representation of this date-time, not null
     */
    @Override  // override for Javadoc
    public String toString() {
        String str = dateTime.toString() + offset.toString();
        if (offset != zone) {
            str += '[' + zone.toString() + ']';
        }
        return str;
    }

    //-----------------------------------------------------------------------
    private Object writeReplace() {
        return new Ser(Ser.ZONED_DATE_TIME_TYPE, this);
    }

    void writeExternal(DataOutput out) throws IOException {
        dateTime.writeExternal(out);
        offset.writeExternal(out);
        zone.write(out);
    }

    static ZonedDateTime readExternal(DataInput in) throws IOException {
        LocalDateTime dateTime = LocalDateTime.readExternal(in);
        ZoneOffset offset = ZoneOffset.readExternal(in);
        ZoneId zone = (ZoneId) Ser.read(in);
        return ZonedDateTime.ofLenient(dateTime, offset, zone);
    }

}
