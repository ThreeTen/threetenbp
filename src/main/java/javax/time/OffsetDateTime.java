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

import javax.time.builder.CalendricalObject;
import javax.time.builder.DateTimeField;
import javax.time.builder.PeriodUnit;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.DateAdjuster;
import javax.time.calendrical.ISOChronology;
import javax.time.calendrical.IllegalCalendarFieldValueException;
import javax.time.calendrical.InvalidCalendarFieldException;
import javax.time.calendrical.TimeAdjuster;
import javax.time.calendrical.ZoneResolver;
import javax.time.calendrical.ZoneResolvers;
import javax.time.zone.ZoneRules;

/**
 * A date-time with a zone offset from UTC in the ISO-8601 calendar system,
 * such as {@code 2007-12-03T10:15:30+01:00}.
 * <p>
 * {@code OffsetDateTime} is an immutable representation of a date-time with an offset.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * as well as the offset from UTC. For example, the value
 * "2nd October 2007 at 13:45.30.123456789 +02:00" can be stored in an {@code OffsetDateTime}.
 * <p>
 * {@code OffsetDateTime} and {@link Instant} both store an instant on the time-line
 * to nanosecond precision. The main difference is that this class also stores the
 * offset from UTC. {@code Instant} should be used when you only need to compare the
 * object to other instants. {@code OffsetDateTime} should be used when you want to actively
 * query and manipulate the date and time fields, although you should also consider using
 * {@link ZonedDateTime}.
 * <p>
 * OffsetDateTime is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class OffsetDateTime
        implements Calendrical, CalendricalObject, Comparable<OffsetDateTime>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -456761901L;

    /**
     * The local date-time.
     */
    private final LocalDateTime dateTime;
    /**
     * The zone offset.
     */
    private final ZoneOffset offset;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code OffsetDateTime}.
     *
     * @return the rule for the date-time, not null
     */
    public static CalendricalRule<OffsetDateTime> rule() {
        return ISOCalendricalRule.OFFSET_DATE_TIME;
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
    public static OffsetDateTime now() {
        return now(Clock.systemDefaultZone());
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
        DateTimes.checkNotNull(clock, "Clock must not be null");
        final Instant now = clock.instant();  // called once
        return ofInstant(now, clock.getZone().getRules().getOffset(now));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime ofMidnight(
            int year, MonthOfYear monthOfYear, int dayOfMonth, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.ofMidnight(year, monthOfYear, dayOfMonth);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime ofMidnight(
            int year, int monthOfYear, int dayOfMonth, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.ofMidnight(year, monthOfYear, dayOfMonth);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from a date with the
     * time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param date  the local date, not null
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     */
    public static OffsetDateTime ofMidnight(LocalDate date, ZoneOffset offset) {
        return of(date, LocalTime.MIDNIGHT, offset);
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
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour);
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
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month,
     * day, hour, minute, second and nanosecond.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
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
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour);
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
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from year, month,
     * day, hour, minute, second and nanosecond.
     * <p>
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return the offset date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDateTime of(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.of(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
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
        LocalDateTime dt = LocalDateTime.of(date, offsetTime.toLocalTime());
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
     * @throws CalendricalException if the instant exceeds the supported date range
     */
    public static OffsetDateTime ofInstantUTC(Instant instant) {
        return ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from an {@code Instant}.
     * <p>
     * The resulting date-time represents exactly the same instant on the time-line.
     * Calling {@link #toInstant()} will return an instant equal to the one used here.
     *
     * @param instant  the instant to create the date-time from, not null
     * @param offset  the zone offset to use, not null
     * @return the offset date-time, not null
     * @throws CalendricalException if the instant exceeds the supported date range
     */
    public static OffsetDateTime ofInstant(Instant instant, ZoneOffset offset) {
        DateTimes.checkNotNull(instant, "Instant must not be null");
        DateTimes.checkNotNull(offset, "ZoneOffset must not be null");
        long localSeconds = instant.getEpochSecond() + offset.getTotalSeconds();  // overflow caught later
        LocalDateTime ldt = LocalDateTime.create(localSeconds, instant.getNanoOfSecond());
        return new OffsetDateTime(ldt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The nanosecond field is set to zero.
     *
     * @param epochSecond  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @return the offset date-time, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public static OffsetDateTime ofEpochSecond(long epochSecond, ZoneOffset offset) {
        DateTimes.checkNotNull(offset, "ZoneOffset must not be null");
        long localSeconds = epochSecond + offset.getTotalSeconds();  // overflow caught later
        LocalDateTime ldt = LocalDateTime.create(localSeconds, 0);
        return new OffsetDateTime(ldt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} from a set of calendricals.
     * <p>
     * A calendrical represents some form of date and time information.
     * This method combines the input calendricals into a date-time.
     *
     * @param calendricals  the calendricals to create a date-time from, no nulls, not null
     * @return the offset date-time, not null
     * @throws CalendricalException if unable to merge to an offset date-time
     */
    public static OffsetDateTime from(Calendrical... calendricals) {
        return CalendricalEngine.merge(calendricals).deriveChecked(rule());
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from the engine.
     * <p>
     * This internal method is used by the associated rule.
     *
     * @param engine  the engine to derive from, not null
     * @return the offset date-time, null if unable to obtain the date-time
     */
    static OffsetDateTime deriveFrom(CalendricalEngine engine) {
        LocalDateTime dateTime = LocalDateTime.deriveFrom(engine);
        ZoneOffset offset = engine.getOffset(true);
        if (dateTime == null || offset == null) {
            return null;
        }
        return new OffsetDateTime(dateTime, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code OffsetDateTime}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the local date, not null
     * @throws CalendricalException if unable to convert to an {@code OffsetDateTime}
     */
    public static OffsetDateTime from(CalendricalObject calendrical) {
        OffsetDateTime obj = calendrical.extract(OffsetDateTime.class);
        if (obj == null) {
            Instant instant = calendrical.extract(Instant.class);
            ZoneOffset offset = calendrical.extract(ZoneOffset.class);
            if (instant != null && offset != null) {
                return OffsetDateTime.ofInstant(instant, offset);
            }
        }
        return DateTimes.ensureNotNull(obj, "Unable to convert calendrical to OffsetDateTime: ", calendrical.getClass());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDateTime} from a text string such as {@code 2007-12-03T10:15:30+01:00}.
     * <p>
     * The string must represent a valid date-time and is parsed using
     * {@link DateTimeFormatters#isoOffsetDateTime()}.
     * Year, month, day-of-month, hour, minute and offset are required.
     * Seconds and fractional seconds are optional.
     * Years outside the range 0000 to 9999 must be prefixed by the plus or minus symbol.
     *
     * @param text  the text to parse such as "2007-12-03T10:15:30+01:00", not null
     * @return the parsed offset date-time, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
//    public static OffsetDateTime parse(CharSequence text) {
//        return DateTimeFormatters.isoOffsetDateTime().parse(text, rule());
//    }

    /**
     * Obtains an instance of {@code OffsetDateTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a date-time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed offset date-time, not null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalParseException if the text cannot be parsed
     */
//    public static OffsetDateTime parse(CharSequence text, DateTimeFormatter formatter) {
//        MathUtils.checkNotNull(formatter, "DateTimeFormatter must not be null");
//        return formatter.parse(text, rule());
//    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param dateTime  the date-time, not null
     * @param offset  the zone offset, not null
     */
    private OffsetDateTime(LocalDateTime dateTime, ZoneOffset offset) {
        if (dateTime == null) {
            throw new NullPointerException("LocalDateTime must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("ZoneOffset must not be null");
        }
        this.dateTime = dateTime;
        this.offset = offset;
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
    /**
     * Gets the value of the specified date-time field, provided that the field fits in an {@code int}.
     * <p>
     * This checks that the range of valid values for the field fits in an {@code int}
     * throwing an exception if it does not. It then returns the value of the specified field.
     * <p>
     * If the field represents a {@code long} value then you must use
     * {@link DateTimeField#getValueFrom(CalendricalObject)} to obtain the value.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws CalendricalException if the field does not fit in an {@code int}
     */
    public int get(DateTimeField field) {
        if (field.getValueRange().isIntValue() == false) {
            throw new CalendricalException("Unable to query field into an int as valid values require a long: " + field);
        }
        return (int) field.getDateTimeRules().get(toLocalDateTime());
    }

    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this date-time then
     * {@code null} will be returned.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        // optimize, especially for LocalDateTime, OffsetDate and OffsetTime
        if (ruleToDerive instanceof ISOCalendricalRule<?>) {
            switch (((ISOCalendricalRule<?>) ruleToDerive).ordinal) {
                case ISOCalendricalRule.LOCAL_DATE_ORDINAL: return (T) toLocalDate();
                case ISOCalendricalRule.LOCAL_TIME_ORDINAL: return (T) toLocalTime();
                case ISOCalendricalRule.LOCAL_DATE_TIME_ORDINAL: return (T) dateTime;
                case ISOCalendricalRule.OFFSET_DATE_ORDINAL: return (T) toOffsetDate();
                case ISOCalendricalRule.OFFSET_TIME_ORDINAL: return (T) toOffsetTime();
                case ISOCalendricalRule.OFFSET_DATE_TIME_ORDINAL: return (T) this;
                case ISOCalendricalRule.ZONE_OFFSET_ORDINAL: return (T) offset;
            }
            return null;
        }
        return CalendricalEngine.derive(ruleToDerive, rule(), toLocalDate(), toLocalTime(), offset, null, ISOChronology.INSTANCE, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T extract(Class<T> type) {
        if (type == OffsetDateTime.class) {
            return (T) this;
        } else if (type == LocalDateTime.class) {
            return (T) toLocalDateTime();
        } else if (type == LocalDate.class) {
            return (T) toLocalDate();
        } else if (type == LocalTime.class) {
            return (T) toLocalTime();
        } else if (type == OffsetDate.class) {
            return (T) toOffsetDate();
        } else if (type == OffsetTime.class) {
            return (T) toOffsetTime();
        } else if (type == Instant.class) {
            return (T) toInstant();
        } else if (type == ZoneOffset.class) {
            return (T) getOffset();
        }
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset.
     *
     * @return the zone offset, not null
     */
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
     * @throws CalendricalException if the result exceeds the supported date range
     */
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
     * Gets the month-of-year field, which is an enum {@code MonthOfYear}.
     * <p>
     * This method returns the enum {@link MonthOfYear} for the month.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link MonthOfYear#getValue() int value}.
     * <p>
     * Additional information can be obtained from the {@code MonthOfYear}.
     * This includes month lengths, textual names and access to the quarter-of-year
     * and month-of-quarter values.
     *
     * @return the month-of-year, not null
     */
    public MonthOfYear getMonthOfYear() {
        return dateTime.getMonthOfYear();
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
    public int getHourOfDay() {
        return dateTime.getHourOfDay();
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return dateTime.getMinuteOfHour();
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return dateTime.getSecondOfMinute();
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return dateTime.getNanoOfSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the ISO proleptic
     * calendar system rules.
     * <p>
     * This method applies the current rules for leap years across the whole time-line.
     * In general, a year is a leap year if it is divisible by four without
     * remainder. However, years divisible by 100, are not leap years, with
     * the exception of years divisible by 400 which are.
     * <p>
     * For example, 1904 is a leap year it is divisible by 4.
     * 1900 was not a leap year as it is divisible by 100, however 2000 was a
     * leap year as it is divisible by 400.
     * <p>
     * The calculation is proleptic - applying the same rules into the far future and far past.
     * This is historically inaccurate, but is correct for the ISO-8601 standard.
     *
     * @return true if the year is leap, false otherwise
     */
    public boolean isLeapYear() {
        return dateTime.isLeapYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDateTime} with the date-time altered
     * and the offset retained.
     * <p>
     * This method returns an object with the same {@code ZoneOffset} and the
     * specified {@code LocalDateTime}.
     * No calculation is needed or performed.
     *
     * @param dateTime  the local date-time to change to, not null
     * @return an {@code OffsetDateTime} based on this time with the requested date-time, not null
     */
    public OffsetDateTime withDateTime(LocalDateTime dateTime) {
        return this.dateTime.equals(dateTime) ? this : new OffsetDateTime(dateTime, offset);
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the date altered using the adjuster.
     * <p>
     * This adjusts the date according to the rules of the specified adjuster.
     * The time and offset are not part of the calculation and will be unchanged in the result.
     * Note that {@link LocalDate} implements {@code DateAdjuster}, thus this method
     * can be used to change the entire date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return an {@code OffsetDateTime} based on this date-time with the date adjusted, not null
     */
    public OffsetDateTime with(DateAdjuster adjuster) {
        return with(dateTime.with(adjuster), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the time altered using the adjuster.
     * <p>
     * This adjusts the time according to the rules of the specified adjuster.
     * The date and offset are not part of the calculation and will be unchanged in the result.
     * Note that {@link LocalTime} implements {@code TimeAdjuster}, thus this method
     * can be used to change the entire time.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return an {@code OffsetDateTime} based on this date-time with the time adjusted, not null
     */
    public OffsetDateTime with(TimeAdjuster adjuster) {
        return with(dateTime.with(adjuster), offset);
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
     * @param field  the field to set in the returned date-time, not null
     * @param newValue  the new value of the field in the returned date-time, not null
     * @return an {@code OffsetDateTime} based on this date-time with the specified field set, not null
     */
    public OffsetDateTime with(DateTimeField field, long newValue) {
        return with(field.getDateTimeRules().set(toLocalDateTime(), newValue), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDateTime} with the year altered.
     * The offset does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date, from MIN_YEAR to MAX_YEAR
     * @return an {@code OffsetDateTime} based on this date-time with the requested year, not null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
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
     * @param monthOfYear  the month-of-year to set in the returned date, from 1 (January) to 12 (December)
     * @return an {@code OffsetDateTime} based on this date-time with the requested month, not null
     * @throws IllegalCalendarFieldValueException if the month-of-year value is invalid
     */
    public OffsetDateTime withMonthOfYear(int monthOfYear) {
        return with(dateTime.withMonthOfYear(monthOfYear), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the month-of-year altered.
     * The offset does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, not null
     * @return an {@code OffsetDateTime} based on this date-time with the requested month, not null
     */
    public OffsetDateTime with(MonthOfYear monthOfYear) {
        return with(dateTime.with(monthOfYear), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the day-of-month altered.
     * If the resulting {@code OffsetDateTime} is invalid, an exception is thrown.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 28-31
     * @return an {@code OffsetDateTime} based on this date-time with the requested day, not null
     * @throws IllegalCalendarFieldValueException if the day-of-month value is invalid
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
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
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return an {@code OffsetDateTime} based on this date with the requested day, not null
     * @throws IllegalCalendarFieldValueException if the day-of-year value is invalid
     * @throws InvalidCalendarFieldException if the day-of-year is invalid for the year
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
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return an {@code OffsetDateTime} based on this date-time with the requested date, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
      public OffsetDateTime withDate(int year, MonthOfYear monthOfYear, int dayOfMonth) {
          return with(dateTime.withDate(year, monthOfYear, dayOfMonth), offset);
      }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return an {@code OffsetDateTime} based on this date-time with the requested date, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public OffsetDateTime withDate(int year, int monthOfYear, int dayOfMonth) {
        return with(dateTime.withDate(year, monthOfYear, dayOfMonth), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDateTime} with the hour-of-day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @return an {@code OffsetDateTime} based on this date-time with the requested hour, not null
     * @throws IllegalCalendarFieldValueException if the hour value is invalid
     */
    public OffsetDateTime withHourOfDay(int hourOfDay) {
        LocalDateTime newDT = dateTime.withHourOfDay(hourOfDay);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the minute-of-hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return an {@code OffsetDateTime} based on this date-time with the requested minute, not null
     * @throws IllegalCalendarFieldValueException if the minute value is invalid
     */
    public OffsetDateTime withMinuteOfHour(int minuteOfHour) {
        LocalDateTime newDT = dateTime.withMinuteOfHour(minuteOfHour);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the second-of-minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return an {@code OffsetDateTime} based on this date-time with the requested second, not null
     * @throws IllegalCalendarFieldValueException if the second value is invalid
     */
    public OffsetDateTime withSecondOfMinute(int secondOfMinute) {
        LocalDateTime newDT = dateTime.withSecondOfMinute(secondOfMinute);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this {@code OffsetDateTime} with the nano-of-second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return an {@code OffsetDateTime} based on this date-time with the requested nanosecond, not null
     * @throws IllegalCalendarFieldValueException if the nanos value is invalid
     */
    public OffsetDateTime withNanoOfSecond(int nanoOfSecond) {
        LocalDateTime newDT = dateTime.withNanoOfSecond(nanoOfSecond);
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
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return an {@code OffsetDateTime} based on this date-time with the requested time, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public OffsetDateTime withTime(int hourOfDay, int minuteOfHour) {
        LocalDateTime newDT = dateTime.withTime(hourOfDay, minuteOfHour);
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
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return an {@code OffsetDateTime} based on this date-time with the requested time, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public OffsetDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        LocalDateTime newDT = dateTime.withTime(hourOfDay, minuteOfHour, secondOfMinute);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the time values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return an {@code OffsetDateTime} based on this date-time with the requested time, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public OffsetDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        LocalDateTime newDT = dateTime.withTime(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified duration added.
     * <p>
     * This adds the specified duration to this date-time, returning a new date-time.
     * <p>
     * The calculation is equivalent to using {@link #plusSeconds(long)} and
     * {@link #plusNanos(long)} on the two parts of the duration.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return an {@code OffsetDateTime} based on this date-time with the duration added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDateTime plus(Duration duration) {
        LocalDateTime newDT = dateTime.plus(duration);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this date-time with the specified period added.
     * <p>
     * This method returns a new date-time based on this time with the specified period added.
     * The calculation is delegated to the unit within the period.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return an {@code OffsetDateTime} based on this date-time with the period added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDateTime plus(Period period) {
        return plus(period.getAmount(), period.getUnit());
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
     * @param period  the amount of the unit to add to the returned date-time, not null
     * @param unit  the unit of the period to add, not null
     * @return an {@code OffsetDateTime} based on this date-time with the specified period added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDateTime plus(long period, PeriodUnit unit) {
        return with(unit.getRules().addToDateTime(dateTime, period), offset);
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDateTime plusNanos(long nanos) {
        LocalDateTime newDT = dateTime.plusNanos(nanos);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date-time with the specified duration subtracted.
     * <p>
     * This subtracts the specified duration from this date-time, returning a new date-time.
     * <p>
     * The calculation is equivalent to using {@link #minusSeconds(long)} and
     * {@link #minusNanos(long)} on the two parts of the duration.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return an {@code OffsetDateTime} based on this date-time with the duration subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDateTime minus(Duration duration) {
        LocalDateTime newDT = dateTime.minus(duration);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this date-time with the specified period subtracted.
     * <p>
     * This method returns a new date-time based on this time with the specified period subtracted.
     * The calculation is delegated to the unit within the period.
     * The offset is not part of the calculation and will be unchanged in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, not null
     * @return an {@code OffsetDateTime} based on this date-time with the period subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDateTime minus(Period period) {
        return minus(period.getAmount(), period.getUnit());
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
     * @param period  the amount of the unit to subtract from the returned date-time, not null
     * @param unit  the unit of the period to subtract, not null
     * @return an {@code OffsetDateTime} based on this date-time with the specified period subtracted, not null
     */
    public OffsetDateTime minus(long period, PeriodUnit unit) {
        return with(unit.getRules().addToDateTime(dateTime, DateTimes.safeNegate(period)), offset);
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDateTime minusNanos(long nanos) {
        LocalDateTime newDT = dateTime.minusNanos(nanos);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
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
     * @throws CalendricalException if the date-time cannot be resolved
     */
    public ZonedDateTime atZoneSimilarLocal(ZoneId zone, ZoneResolver resolver) {
        ZoneRules rules = zone.getRules();
        OffsetDateTime offsetDT = resolver.resolve(dateTime, rules.getOffsetInfo(dateTime), rules, zone, this);
        return ZonedDateTime.of(offsetDT, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to an {@code Instant}.
     *
     * @return an Instant representing the same instant, not null
     */
    public Instant toInstant() {
        return Instant.ofEpochSecond(toEpochSecond(), getNanoOfSecond());
    }

    /**
     * Converts this date-time to a {@code LocalDate}.
     *
     * @return a LocalDate representing the date fields of this date-time, not null
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Converts this date-time to a {@code LocalTime}.
     *
     * @return a LocalTime representing the time fields of this date-time, not null
     */
    public LocalTime toLocalTime() {
        return dateTime.toLocalTime();
    }

    /**
     * Converts this date-time to a {@code LocalDateTime}.
     *
     * @return a LocalDateTime representing the fields of this date-time, not null
     */
    public LocalDateTime toLocalDateTime() {
        return dateTime;
    }

    /**
     * Converts this date-time to an {@code OffsetDate}.
     *
     * @return an OffsetDate representing the date and offset, not null
     */
    public OffsetDate toOffsetDate() {
        return OffsetDate.of(dateTime.toLocalDate(), offset);
    }

    /**
     * Converts this date-time to an {@code OffsetTime}.
     *
     * @return an OffsetTime representing the time and offset, not null
     */
    public OffsetTime toOffsetTime() {
        return OffsetTime.of(dateTime.toLocalTime(), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to the number of seconds from the epoch
     * of 1970-01-01T00:00:00Z.
     * <p>
     * Instants on the time-line after the epoch are positive, earlier are negative.
     *
     * @return the number of seconds from the epoch of 1970-01-01T00:00:00Z
     */
    public long toEpochSecond() {
        long epochDay = dateTime.toLocalDate().toEpochDay();
        long secs = epochDay * DateTimes.SECONDS_PER_DAY + dateTime.toLocalTime().toSecondOfDay();
        secs -= offset.getTotalSeconds();
        return secs;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code OffsetDateTime} to another date-time.
     * <p>
     * The comparison is based on the instant then local date-time.
     * This ordering is consistent with {@code equals()}.
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
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(OffsetDateTime other) {
        if (offset.equals(other.offset)) {
            return dateTime.compareTo(other.dateTime);
        }
        int compare = DateTimes.safeCompare(toEpochSecond(), other.toEpochSecond());
        if (compare == 0) {
            compare = DateTimes.safeCompare(getNanoOfSecond(), other.getNanoOfSecond());
            if (compare == 0) {
                compare = dateTime.compareTo(other.dateTime);
            }
        }
        return compare;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the instant of this {@code OffsetDateTime} is after that of the specified date-time.
     * <p>
     * This method differs from the comparison in {@link #compareTo} and {@link #equals} in that it
     * only compares the instant of the date-time. This is equivalent to using
     * {@code dateTime1.toInstant().isAfter(dateTime2.toInstant());}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the instant of the specified date-time
     */
    public boolean isAfter(OffsetDateTime other) {
        long thisEpochSec = toEpochSecond();
        long otherEpochSec = other.toEpochSecond();
        return thisEpochSec > otherEpochSec ||
            (thisEpochSec == otherEpochSec && getNanoOfSecond() > other.getNanoOfSecond());
    }

    /**
     * Checks if the instant of this {@code OffsetDateTime} is before that of the specified date-time.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the instant of the date-time. This is equivalent to using
     * {@code dateTime1.toInstant().isBefore(dateTime2.toInstant());}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is before the instant of the specified date-time
     */
    public boolean isBefore(OffsetDateTime other) {
        long thisEpochSec = toEpochSecond();
        long otherEpochSec = other.toEpochSecond();
        return thisEpochSec < otherEpochSec ||
            (thisEpochSec == otherEpochSec && getNanoOfSecond() < other.getNanoOfSecond());
    }

    /**
     * Checks if the instant of this {@code OffsetDateTime} is equal to that of the specified date-time.
     * <p>
     * This method differs from the comparison in {@link #compareTo} and {@link #equals}
     * in that it only compares the instant of the date-time. This is equivalent to using
     * {@code dateTime1.toInstant().equals(dateTime2.toInstant());}.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if the instant equals the instant of the specified date-time
     */
    public boolean equalInstant(OffsetDateTime other) {
        return toEpochSecond() == other.toEpochSecond() &&
            getNanoOfSecond() == other.getNanoOfSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date-time is equal to another date-time.
     * <p>
     * The comparison is based on the local date-time and the offset.
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
     * <ul>
     * <li>{@code yyyy-MM-dd'T'HH:mmXXXXX}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssXXXXX}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnXXXXX}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnnnnXXXXX}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnnnnnnnXXXXX}</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this date-time, not null
     */
    @Override
    public String toString() {
        return dateTime.toString() + offset.toString();
    }

    /**
     * Outputs this date-time as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted date-time string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
//    public String toString(DateTimeFormatter formatter) {
//        MathUtils.checkNotNull(formatter, "DateTimeFormatter must not be null");
//        return formatter.print(this);
//    }

}
