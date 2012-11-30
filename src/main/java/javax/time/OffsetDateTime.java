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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import javax.time.calendrical.ChronoField;
import javax.time.calendrical.ChronoUnit;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeAdjusters;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.ChronoOffsetDateTime;
import javax.time.chrono.ISOChrono;
import javax.time.format.DateTimeFormatter;
import javax.time.format.DateTimeFormatters;
import javax.time.format.DateTimeParseException;
import javax.time.jdk8.DefaultInterfaceChronoOffsetDateTime;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;
import javax.time.zone.ZoneRules;

/**
 * A date-time with an offset from UTC/Greenwich in the ISO-8601 calendar system,
 * such as {@code 2007-12-03T10:15:30+01:00}.
 * <p>
 * {@code OffsetDateTime} is an immutable representation of a date-time with an offset.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * as well as the offset from UTC/Greenwich. For example, the value
 * "2nd October 2007 at 13:45.30.123456789 +02:00" can be stored in an {@code OffsetDateTime}.
 * <p>
 * {@code OffsetDateTime} and {@link Instant} both store an instant on the time-line
 * to nanosecond precision. The main difference is that this class also stores the
 * offset from UTC/Greenwich. {@code Instant} should be used when you only need to compare the
 * object to other instants. {@code OffsetDateTime} should be used when you want to actively
 * query and manipulate the date and time fields, although you should also consider using
 * {@link ZonedDateTime}.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class OffsetDateTime
        extends DefaultInterfaceChronoOffsetDateTime<ISOChrono>
        implements ChronoOffsetDateTime<ISOChrono>, DateTime, WithAdjuster, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 2287754244819255394L;

    /**
     * The local date-time.
     */
    private final LocalDateTime dateTime;
    /**
     * The offset from UTC/Greenwich.
     */
    private final ZoneOffset offset;

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
    public static OffsetDateTime now() {
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
    public static OffsetDateTime now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Obtains the current date-time from the specified clock.
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
    public static OffsetDateTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        final Instant now = clock.instant();  // called once
        return ofInstant(now, clock.getZone().getRules().getOffset(now));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * The second and nanosecond fields will be set to zero.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, Month month, int dayOfMonth,
            int hour, int minute, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * The nanosecond field will be set to zero.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, Month month, int dayOfMonth,
            int hour, int minute, int second, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month,
     * day, hour, minute, second and nanosecond.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, Month month, int dayOfMonth,
            int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
        return new OffsetDateTime(dt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * The second and nanosecond fields will be set to zero.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, int month, int dayOfMonth,
            int hour, int minute, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * The nanosecond field will be set to zero.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, int month, int dayOfMonth,
            int hour, int minute, int second, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month,
     * day, hour, minute, second and nanosecond.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if the value of any field is out of range
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, int month, int dayOfMonth,
            int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond);
        return new OffsetDateTime(dt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} from a date, time and offset.
     *
     * @param date  the local date, not null
     * @param time  the local time, not null
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     */
    public static OffsetDateTime of(LocalDate date, LocalTime time, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(date, time);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from a local date and offset time.
     *
     * @param date  the local date, not null
     * @param offsetTime  the offset time to use, not null
     * @return the offset date-time, not null
     */
    public static OffsetDateTime of(LocalDate date, OffsetTime offsetTime) {
        LocalDateTime dt = LocalDateTime.of(date, offsetTime.getTime());
        return new OffsetDateTime(dt, offsetTime.getOffset());
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from a date-time and offset.
     *
     * @param dateTime  the local date-time, not null
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     */
    public static OffsetDateTime of(LocalDateTime dateTime, ZoneOffset offset) {
        return new OffsetDateTime(dateTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} from an {@code Instant}
     * using the UTC offset.
     * <p>
     * The resulting date-time represents exactly the same instant on the time-line.
     * Calling {@link #toInstant()} will return an instant equal to the one used here.
     *
     * @param instant  the instant to create a date-time from, not null
     * @return the offset date-time in UTC, not null
     * @throws DateTimeException if the instant exceeds the supported date range
     */
    public static OffsetDateTime ofInstantUTC(Instant instant) {
        return ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from an {@code Instant} and offset.
     * <p>
     * The resulting date-time represents exactly the same instant on the time-line.
     * Calling {@link #toInstant()} will return an instant equal to the one used here.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param offset  the zone offset to use, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if the instant exceeds the supported date range
     */
    public static OffsetDateTime ofInstant(Instant instant, ZoneOffset offset) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(offset, "offset");
        return create(instant.getEpochSecond(), instant.getNano(), offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from an {@code Instant} and time-zone.
     * <p>
     * The resulting date-time represents exactly the same instant on the time-line.
     * Calling {@link #toInstant()} will return an instant equal to the one used here.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param zone  the time-zone to use, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if the instant exceeds the supported date range
     */
    public static OffsetDateTime ofInstant(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
        return create(instant.getEpochSecond(), instant.getNano(), zone.getRules().getOffset(instant));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * This allows the {@link ChronoField#INSTANT_SECONDS epoch-seconds} field
     * to be converted to an offset date-time. This is primarily intended for
     * low-level conversions rather than general application usage.
     *
     * @param epochSecond  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfSecond  the nanosecond within the second, from 0 to 999,999,999
     * @return the offset date-time, not null
     * @throws DateTimeException if the result exceeds the supported range
     */
    public static OffsetDateTime ofEpochSecond(long epochSecond, int nanoOfSecond, ZoneOffset offset) {
        Objects.requireNonNull(offset, "offset");
        return create(epochSecond, nanoOfSecond, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     *
     * @param epochSecond  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfSecond  the nanosecond within the second, from 0 to 999,999,999
     * @return the offset date-time, not null
     * @throws DateTimeException if the instant exceeds the supported date range
     */
    static OffsetDateTime create(long epochSecond, int nanoOfSecond, ZoneOffset offset) {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, offset);
        return new OffsetDateTime(ldt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} from a date-time object.
     * <p>
     * A {@code DateTimeAccessor} represents some form of date and time information.
     * This factory converts the arbitrary date-time object to an instance of {@code OffsetDateTime}.
     *
     * @param dateTime  the date-time object to convert, not null
     * @return the offset date-time, not null
     * @throws DateTimeException if unable to convert to an {@code OffsetDateTime}
     */
    public static OffsetDateTime from(DateTimeAccessor dateTime) {
        if (dateTime instanceof OffsetDateTime) {
            return (OffsetDateTime) dateTime;
        }
        ZoneOffset offset = ZoneOffset.from(dateTime);
        try {
            try {
                LocalDateTime ldt = LocalDateTime.from(dateTime);
                return of(ldt, offset);
            } catch (DateTimeException ignore) {
                Instant instant = Instant.from(dateTime);
                return OffsetDateTime.ofInstant(instant, offset);
            }
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to convert date-time to OffsetDateTime: " + dateTime.getClass(), ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} from a text string such as {@code 2007-12-03T10:15:30+01:00}.
     * <p>
     * The string must represent a valid date-time and is parsed using
     * {@link javax.time.format.DateTimeFormatters#isoOffsetDateTime()}.
     *
     * @param text  the text to parse such as "2007-12-03T10:15:30+01:00", not null
     * @return the parsed offset date-time, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static OffsetDateTime parse(CharSequence text) {
        return parse(text, DateTimeFormatters.isoOffsetDateTime());
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a date-time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed offset date-time, not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static OffsetDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.parse(text, OffsetDateTime.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param time  the local date-time, not null
     * @param offset  the zone offset, not null
     */
    private OffsetDateTime(LocalDateTime dateTime, ZoneOffset offset) {
        this.dateTime = Objects.requireNonNull(dateTime, "dateTime");
        this.offset = Objects.requireNonNull(offset, "offset");
    }

    /**
     * Returns a new date-time based on this one, returning {@code this} where possible.
     *
     * @param dateTime  the date-time to create with, not null
     * @param offset  the zone offset to create with, not null
     */
    private OffsetDateTime with(LocalDateTime dateTime, ZoneOffset offset) {
        if (this.dateTime == dateTime && this.offset.equals(offset)) {
            return this;
        }
        return new OffsetDateTime(dateTime, offset);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field) {
        return field instanceof ChronoField || (field != null && field.doIsSupported(this));
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
     * Returns a copy of this {@code OffsetDateTime} with the specified offset ensuring
     * that the result has the same local date-time.
     * <p>
     * This method returns an object with the same {@code LocalDateTime} and the specified {@code ZoneOffset}.
     * No calculation is needed or performed.
     * For example, if this time represents {@code 2007-12-03T10:30+02:00} and the offset specified is
     * {@code +03:00}, then this method will return {@code 2007-12-03T10:30+03:00}.
     * <p>
     * To take into account the difference between the offsets, and adjust the time fields,
     * use {@link #withOffsetSameInstant}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return an {@code OffsetDateTime} based on this date-time with the requested offset, not null
     */
    @Override
    public OffsetDateTime withOffsetSameLocal(ZoneOffset offset) {
        return with(dateTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified offset ensuring
     * that the result is at the same instant.
     * <p>
     * This method returns an object with the specified {@code ZoneOffset} and a {@code LocalDateTime}
     * adjusted by the difference between the two offsets.
     * This will result in the old and new objects representing the same instant.
     * This is useful for finding the local time in a different offset.
     * For example, if this time represents {@code 2007-12-03T10:30+02:00} and the offset specified is
     * {@code +03:00}, then this method will return {@code 2007-12-03T11:30+03:00}.
     * <p>
     * To change the offset without adjusting the local time use {@link #withOffsetSameLocal}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return an {@code OffsetDateTime} based on this date-time with the requested offset, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    @Override
    public OffsetDateTime withOffsetSameInstant(ZoneOffset offset) {
        if (offset.equals(this.offset)) {
            return this;
        }
        int difference = offset.getTotalSeconds() - this.offset.getTotalSeconds();
        LocalDateTime adjusted = dateTime.plusSeconds(difference);
        return new OffsetDateTime(adjusted, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the {@code LocalDateTime} part of this offset date-time.
     * <p>
     * This returns a {@code LocalDateTime} with the same year, month, day and time
     * as this date-time.
     *
     * @return the local date-time part of this date-time, not null
     */
    @Override
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
     * @return an {@code OffsetDateTime} based on this date-time with the adjustment made, not null
     * @throws DateTimeException if the adjustment cannot be made
     */
    @Override
    public OffsetDateTime with(WithAdjuster adjuster) {
        if (adjuster instanceof LocalDate || adjuster instanceof LocalTime || adjuster instanceof LocalDateTime) {
            return with(dateTime.with(adjuster), offset);
        } else if (adjuster instanceof ZoneOffset) {
            return with(dateTime, (ZoneOffset) adjuster);
        } else if (adjuster instanceof OffsetDateTime) {
            return (OffsetDateTime) adjuster;
        }
        return (OffsetDateTime) adjuster.doWithAdjustment(this);
    }

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
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the result, not null
     * @param newValue  the new value of the field in the result
     * @return an {@code OffsetDateTime} based on this date-time with the specified field set, not null
     * @throws DateTimeException if the value is invalid
     */
    @Override
    public OffsetDateTime with(DateTimeField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            switch (f) {
                case INSTANT_SECONDS: return ofEpochSecond(newValue, getNano(), offset);
                case OFFSET_SECONDS: {
                    return with(dateTime, ZoneOffset.ofTotalSeconds(f.checkValidIntValue(newValue)));
                }
            }
            return with(dateTime.with(field, newValue), offset);
        }
        return field.doWith(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDateTime} with the year altered.
     * The offset does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the result, from MIN_YEAR to MAX_YEAR
     * @return an {@code OffsetDateTime} based on this date-time with the requested year, not null
     * @throws DateTimeException if the year value is invalid
     */
    public OffsetDateTime withYear(int year) {
        return with(dateTime.withYear(year), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the month-of-year altered.
     * The offset does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to set in the result, from 1 (January) to 12 (December)
     * @return an {@code OffsetDateTime} based on this date-time with the requested month, not null
     * @throws DateTimeException if the month-of-year value is invalid
     */
    public OffsetDateTime withMonth(int month) {
        return with(dateTime.withMonth(month), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the day-of-month altered.
     * If the resulting {@code OffsetDateTime} is invalid, an exception is thrown.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the result, from 1 to 28-31
     * @return an {@code OffsetDateTime} based on this date-time with the requested day, not null
     * @throws DateTimeException if the day-of-month value is invalid
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public OffsetDateTime withDayOfMonth(int dayOfMonth) {
        return with(dateTime.withDayOfMonth(dayOfMonth), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the day-of-year altered.
     * If the resulting {@code OffsetDateTime} is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the result, from 1 to 365-366
     * @return an {@code OffsetDateTime} based on this date with the requested day, not null
     * @throws DateTimeException if the day-of-year value is invalid
     * @throws DateTimeException if the day-of-year is invalid for the year
     */
    public OffsetDateTime withDayOfYear(int dayOfYear) {
        return with(dateTime.withDayOfYear(dayOfYear), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDateTime} with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the result, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to set in the result, not null
     * @param dayOfMonth  the day-of-month to set in the result, from 1 to 31
     * @return an {@code OffsetDateTime} based on this date-time with the requested date, not null
     * @throws DateTimeException if any field value is invalid
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
      public OffsetDateTime withDate(int year, Month month, int dayOfMonth) {
          return with(dateTime.withDate(year, month, dayOfMonth), offset);
      }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the result, from MIN_VALUE + 1 to MAX_VALUE
     * @param month  the month-of-year to set in the result, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to set in the result, from 1 to 31
     * @return an {@code OffsetDateTime} based on this date-time with the requested date, not null
     * @throws DateTimeException if any field value is invalid
     * @throws DateTimeException if the day-of-month is invalid for the month-year
     */
    public OffsetDateTime withDate(int year, int month, int dayOfMonth) {
        return with(dateTime.withDate(year, month, dayOfMonth), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDateTime} with the hour-of-day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to set in the result, from 0 to 23
     * @return an {@code OffsetDateTime} based on this date-time with the requested hour, not null
     * @throws DateTimeException if the hour value is invalid
     */
    public OffsetDateTime withHour(int hour) {
        LocalDateTime newDT = dateTime.withHour(hour);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the minute-of-hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minute  the minute-of-hour to set in the result, from 0 to 59
     * @return an {@code OffsetDateTime} based on this date-time with the requested minute, not null
     * @throws DateTimeException if the minute value is invalid
     */
    public OffsetDateTime withMinute(int minute) {
        LocalDateTime newDT = dateTime.withMinute(minute);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the second-of-minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param second  the second-of-minute to set in the result, from 0 to 59
     * @return an {@code OffsetDateTime} based on this date-time with the requested second, not null
     * @throws DateTimeException if the second value is invalid
     */
    public OffsetDateTime withSecond(int second) {
        LocalDateTime newDT = dateTime.withSecond(second);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the nano-of-second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to set in the result, from 0 to 999,999,999
     * @return an {@code OffsetDateTime} based on this date-time with the requested nanosecond, not null
     * @throws DateTimeException if the nanos value is invalid
     */
    public OffsetDateTime withNano(int nanoOfSecond) {
        LocalDateTime newDT = dateTime.withNano(nanoOfSecond);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int,int)} and sets
     * the second and nanosecond fields to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to set in the result, from 0 to 23
     * @param minute  the minute-of-hour to set in the result, from 0 to 59
     * @return an {@code OffsetDateTime} based on this date-time with the requested time, not null
     * @throws DateTimeException if any field value is invalid
     */
    public OffsetDateTime withTime(int hour, int minute) {
        LocalDateTime newDT = dateTime.withTime(hour, minute);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int,int)} and sets
     * the nanosecond fields to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to set in the result, from 0 to 23
     * @param minute  the minute-of-hour to set in the result, from 0 to 59
     * @param second  the second-of-minute to set in the result, from 0 to 59
     * @return an {@code OffsetDateTime} based on this date-time with the requested time, not null
     * @throws DateTimeException if any field value is invalid
     */
    public OffsetDateTime withTime(int hour, int minute, int second) {
        LocalDateTime newDT = dateTime.withTime(hour, minute, second);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the time values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour  the hour-of-day to set in the result, from 0 to 23
     * @param minute  the minute-of-hour to set in the result, from 0 to 59
     * @param second  the second-of-minute to set in the result, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to set in the result, from 0 to 999,999,999
     * @return an {@code OffsetDateTime} based on this date-time with the requested time, not null
     * @throws DateTimeException if any field value is invalid
     */
    public OffsetDateTime withTime(int hour, int minute, int second, int nanoOfSecond) {
        LocalDateTime newDT = dateTime.withTime(hour, minute, second, nanoOfSecond);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
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
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return an {@code OffsetDateTime} based on this date-time with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public OffsetDateTime plus(PlusAdjuster adjuster) {
        return (OffsetDateTime) adjuster.doPlusAdjustment(this);
    }

    /**
     * Returns a copy of this date-time with the specified period added.
     * <p>
     * This method returns a new date-time based on this date-time with the specified period added.
     * This can be used to add any period that is defined by a unit, for example to add years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount of the unit to add to the result, may be negative
     * @param unit  the unit of the period to add, not null
     * @return an {@code OffsetDateTime} based on this date-time with the specified period added, not null
     * @throws DateTimeException if the unit cannot be added to this type
     */
    @Override
    public OffsetDateTime plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof ChronoUnit) {
            return with(dateTime.plus(amountToAdd, unit), offset);
        }
        return unit.doPlus(this, amountToAdd);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in years added.
     * <p>
     * This method adds the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) plus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime plusYears(long years) {
        LocalDateTime newDT = dateTime.plusYears(years);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 plus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the months added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime plusMonths(long months) {
        LocalDateTime newDT = dateTime.plusMonths(months);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in weeks added.
     * <p>
     * This method adds the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the weeks added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime plusWeeks(long weeks) {
        LocalDateTime newDT = dateTime.plusWeeks(weeks);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in days added.
     * <p>
     * This method adds the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the days added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime plusDays(long days) {
        LocalDateTime newDT = dateTime.plusDays(days);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the hours added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime plusHours(long hours) {
        LocalDateTime newDT = dateTime.plusHours(hours);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the minutes added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime plusMinutes(long minutes) {
        LocalDateTime newDT = dateTime.plusMinutes(minutes);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the seconds added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime plusSeconds(long seconds) {
        LocalDateTime newDT = dateTime.plusSeconds(seconds);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the nanoseconds added, not null
     * @throws DateTimeException if the unit cannot be added to this type
     */
    public OffsetDateTime plusNanos(long nanos) {
        LocalDateTime newDT = dateTime.plusNanos(nanos);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
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
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return an {@code OffsetDateTime} based on this date-time with the subtraction made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public OffsetDateTime minus(MinusAdjuster adjuster) {
        return (OffsetDateTime) adjuster.doMinusAdjustment(this);
    }

    /**
     * Returns a copy of this date-time with the specified period subtracted.
     * <p>
     * This method returns a new date-time based on this date-time with the specified period subtracted.
     * This can be used to subtract any period that is defined by a unit, for example to subtract years, months or days.
     * The unit is responsible for the details of the calculation, including the resolution
     * of any edge cases in the calculation.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount of the unit to subtract from the result, may be negative
     * @param unit  the unit of the period to subtract, not null
     * @return an {@code OffsetDateTime} based on this date-time with the specified period subtracted, not null
     */
    @Override
    public OffsetDateTime minus(long amountToSubtract, PeriodUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in years subtracted.
     * <p>
     * This method subtracts the specified amount from the years field in three steps:
     * <ol>
     * <li>Subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the years subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime minusYears(long years) {
        LocalDateTime newDT = dateTime.minusYears(years);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in months subtracted.
     * <p>
     * This method subtracts the specified amount from the months field in three steps:
     * <ol>
     * <li>Subtract the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the months subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime minusMonths(long months) {
        LocalDateTime newDT = dateTime.minusMonths(months);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks from the days field decrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the weeks subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime minusWeeks(long weeks) {
        LocalDateTime newDT = dateTime.minusWeeks(weeks);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in days subtracted.
     * <p>
     * This method subtracts the specified amount from the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the days subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime minusDays(long days) {
        LocalDateTime newDT = dateTime.minusDays(days);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the hours subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime minusHours(long hours) {
        LocalDateTime newDT = dateTime.minusHours(hours);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the minutes subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime minusMinutes(long minutes) {
        LocalDateTime newDT = dateTime.minusMinutes(minutes);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the seconds subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime minusSeconds(long seconds) {
        LocalDateTime newDT = dateTime.minusSeconds(seconds);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the specified period in nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return an {@code OffsetDateTime} based on this date-time with the nanoseconds subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public OffsetDateTime minusNanos(long nanos) {
        LocalDateTime newDT = dateTime.minusNanos(nanos);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Resolves this date-time against the specified time-zone updating the offset.
     * <p>
     * The resolution will return an {@code OffsetDateTime} based on this one
     * with the offset resolved to be valid for the time-zone.
     * The offset selected is the offset that is valid at the instant that this
     * date-time represents.
     * <p>
     * This method can be used to manage time-zones without using {@link ZonedDateTime}.
     * Simply create an {@code OffsetDateTime} from an instant and resolve the offset
     * using this method to be accurate. After every calculation, the date-time must
     * be re-resolved to ensure that the offset is always correct for the zone.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use to resolve the offset, not null
     * @return an {@code OffsetDateTime} based on this date-time with the correct offset for the zone, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    public OffsetDateTime resolveOffset(ZoneId zone) {
        ZoneRules rules = zone.getRules();
        if (rules.isValidOffset(dateTime, offset)) {  // avoids toInstant()
            return this;
        }
        return withOffsetSameInstant(rules.getOffset(toInstant()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a zoned date-time formed from the instant represented by this
     * date-time and the specified time-zone.
     * <p>
     * This conversion will ignore the visible local date-time and use the underlying instant instead.
     * This avoids any problems with local time-line gaps or overlaps.
     * The result might have different values for fields such as hour, minute an even day.
     * <p>
     * To attempt to retain the values of the fields, use {@link #atZoneSimilarLocal(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date-time, not null
     */
    @Override
    public ZonedDateTime atZoneSameInstant(ZoneId zone) {
        return ZonedDateTime.ofInstant(this, zone);
    }

    /**
     * Returns a zoned date-time formed from this date-time and the specified time-zone.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. If the local date-time is in a gap or overlap according to
     * the rules then a resolver is used to determine the resultant local time and offset.
     * This method uses the {@link ZoneResolvers#retainOffset() retain-offset} resolver.
     * This selects the date-time immediately after a gap and retains the offset in
     * overlaps where possible, selecting the earlier offset if not possible.
     * <p>
     * Finer control over gaps and overlaps is available in two ways.
     * If you simply want to use the later offset at overlaps then call
     * {@link ZonedDateTime#withLaterOffsetAtOverlap()} immediately after this method.
     * Alternately, pass a specific resolver to {@link #atZoneSimilarLocal(ZoneId, ZoneResolver)}.
     * <p>
     * To create a zoned date-time at the same instant irrespective of the local time-line,
     * use {@link #atZoneSameInstant(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date and the earliest valid time for the zone, not null
     */
    @Override
    public ZonedDateTime atZoneSimilarLocal(ZoneId zone) {
        return atZoneSimilarLocal(zone, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a zoned date-time formed from this date-time and the specified time-zone
     * taking control of what occurs in time-line gaps and overlaps.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. If the local date-time is in a gap or overlap according to
     * the rules then the resolver is used to determine the resultant local time and offset.
     * <p>
     * To create a zoned date-time at the same instant irrespective of the local time-line,
     * use {@link #atZoneSameInstant(ZoneId)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @param resolver  the zone resolver to use for gaps and overlaps, not null
     * @return the zoned date-time formed from this date and the earliest valid time for the zone, not null
     * @throws DateTimeException if the date-time cannot be resolved
     */
    @Override
    public ZonedDateTime atZoneSimilarLocal(ZoneId zone, ZoneResolver resolver) {
        return ZonedDateTime.resolve(dateTime, zone, this, resolver);
    }

    //-----------------------------------------------------------------------
    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof OffsetDateTime == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
        if (unit instanceof ChronoUnit) {
            OffsetDateTime end = (OffsetDateTime) endDateTime;
            end = end.withOffsetSameInstant(offset);
            return dateTime.periodUntil(end, unit);
        }
        return unit.between(this, endDateTime).getAmount();
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to an {@code OffsetDate}.
     *
     * @return an OffsetDate representing the date and offset, not null
     */
    public OffsetDate toOffsetDate() {
        return OffsetDate.of(dateTime.getDate(), offset);
    }

    /**
     * Converts this date-time to an {@code OffsetTime}.
     *
     * @return an OffsetTime representing the time and offset, not null
     */
    public OffsetTime toOffsetTime() {
        return OffsetTime.of(dateTime.getTime(), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code OffsetDateTime} to another date-time.
     * <p>
     * The comparison is based on the instant then on the local date-time.
     * It is "consistent with equals", as defined by {@link Comparable}.
     * <p>
     * For example, the following is the comparator order:
     * <ol>
     * <li>{@code 2008-12-03T10:30+01:00}</li>
     * <li>{@code 2008-12-03T11:00+01:00}</li>
     * <li>{@code 2008-12-03T12:00+02:00}</li>
     * <li>{@code 2008-12-03T11:30+01:00}</li>
     * <li>{@code 2008-12-03T12:00+01:00}</li>
     * <li>{@code 2008-12-03T12:30+01:00}</li>
     * </ol>
     * Values #2 and #3 represent the same instant on the time-line.
     * When two values represent the same instant, the local date-time is compared
     * to distinguish them. This step is needed to make the ordering
     * consistent with {@code equals()}.
     * <p>
     * If all the date-times being compared are instances of {@code OffsetDateTime},
     * then the comparison will be entirely based on the date-time.
     * If some dates being compared are in different chronologies, then the
     * chronology is also considered, see {@link ChronoOffsetDateTime#compareTo}.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override  // override for Javadoc
    public int compareTo(ChronoOffsetDateTime<?> other) {
        return super.compareTo(other);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date-time is equal to another date-time.
     * <p>
     * The comparison is based on the local date-time and the offset.
     * To compare for the same instant on the time-line, use {@link #isEqual}.
     * Only objects of type {@code OffsetDateTime} are compared, other types return false.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date-time
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof OffsetDateTime) {
            OffsetDateTime other = (OffsetDateTime) obj;
            return dateTime.equals(other.dateTime) && offset.equals(other.offset);
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
        return dateTime.hashCode() ^ offset.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date-time as a {@code String}, such as {@code 2007-12-03T10:15:30+01:00}.
     * <p>
     * The output will be one of the following ISO-8601 formats:
     * <p><ul>
     * <li>{@code yyyy-MM-dd'T'HH:mmXXXXX}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssXXXXX}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXXXX}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXXXX}</li>
     * </ul><p>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this date-time, not null
     */
    @Override  // override for Javadoc
    public String toString() {
        return dateTime.toString() + offset.toString();
    }

    //-----------------------------------------------------------------------
    private Object writeReplace() {
        return new Ser(Ser.OFFSET_DATE_TIME_TYPE, this);
    }

    void writeExternal(DataOutput out) throws IOException {
        dateTime.writeExternal(out);
        offset.writeExternal(out);
    }

    static OffsetDateTime readExternal(DataInput in) throws IOException {
        LocalDateTime dateTime = LocalDateTime.readExternal(in);
        ZoneOffset offset = ZoneOffset.readExternal(in);
        return OffsetDateTime.of(dateTime, offset);
    }

}
