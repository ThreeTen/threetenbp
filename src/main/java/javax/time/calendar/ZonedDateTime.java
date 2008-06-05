/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.InstantProvider;
import javax.time.calendar.TimeZone.Discontinuity;
import javax.time.calendar.TimeZone.OffsetInfo;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.Year;
import javax.time.period.PeriodView;

/**
 * A date-time with a time zone in the ISO-8601 calendar system,
 * such as '2007-12-03T10:15:30+01:00 Europe/Paris'.
 * <p>
 * ZonedDateTime is an immutable calendrical that represents a date-time, often
 * viewed as year-month-day-hour-minute-second-zone.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * as well as a time zone and zone offset. Thus, for example, the value
 * "2nd October 2007 at 13:45.30.123456789 +02:00 in the Europe/Paris time zone"
 * can be stored in a ZonedDateTime.
 * <p>
 * ZonedDateTime is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class ZonedDateTime
        implements InstantProvider, DateTimeProvider, Calendrical, Comparable<ZonedDateTime>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -456761901L;

    /**
     * The offset date-time.
     */
    private final OffsetDateTime dateTime;
    /**
     * The time zone.
     */
    private final TimeZone zone;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>ZonedDateTime</code>.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param zone  the time zone, not null
     * @return a ZonedDateTime object, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static ZonedDateTime dateTime(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, TimeZone zone) {
        return dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, 0, 0, zone);
    }

    /**
     * Obtains an instance of <code>ZonedDateTime</code>.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param zone  the time zone, not null
     * @return a ZonedDateTime object, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static ZonedDateTime dateTime(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, TimeZone zone) {
        return dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, 0, zone);
    }

    /**
     * Obtains an instance of <code>ZonedDateTime</code>.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @param zone  the time zone, not null
     * @return a ZonedDateTime object, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     * @throws CalendricalException if the date-time cannot be resolved due to daylight savings
     */
    public static ZonedDateTime dateTime(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, TimeZone zone) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return dateTime(dt, zone, ZoneResolvers.retainOffset());
    }

    /**
     * Obtains an instance of <code>ZonedDateTime</code> from an <code>Instant</code>.
     *
     * @param instant  the instant to convert, not null
     * @param zone  the time zone, not null
     * @return a ZonedDateTime object, never null
     * @throws CalendarConversionException if the result exceeds the supported date range
     */
    public static ZonedDateTime dateTime(Instant instant, TimeZone zone) {
        if (instant == null) {
            throw new NullPointerException("The instant must not be null");
        }
        if (zone == null) {
            throw new NullPointerException("The zone offset must not be null");
        }
        ZoneOffset offset = zone.getOffset(instant);
        OffsetDateTime offsetDateTime = OffsetDateTime.dateTime(instant, offset);
        return new ZonedDateTime(offsetDateTime, zone);
    }

    /**
     * Obtains an instance of <code>ZonedDateTime</code>, providing a resolver
     * to handle an invalid date-time.
     *
     * @param dateTimeProvider  the date-time provider to use, not null
     * @param zone  the time zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return a ZonedDateTime object, never null
     * @throws CalendricalException if the date-time cannot be resolved
     */
    public static ZonedDateTime dateTime(DateTimeProvider dateTimeProvider, TimeZone zone, ZoneResolver resolver) {
        LocalDateTime dt = dateTimeProvider.toLocalDateTime();
        return dateTime(dt, zone, resolver);
    }

    /**
     * Obtains an instance of <code>ZonedDateTime</code>, ensuring that the offset
     * provided is valid for the time zone.
     *
     * @param dateTime  the offset date-time to use, not null
     * @param zone  the time zone, not null
     * @return a ZonedDateTime object, never null
     * @throws CalendricalException if the offset is invalid for the time zone at the date-time
     */
    public static ZonedDateTime dateTime(OffsetDateTime dateTime, TimeZone zone) {
        if (dateTime == null) {
            throw new NullPointerException("The date-time must not be null");
        }
        if (zone == null) {
            throw new NullPointerException("The time zone must not be null");
        }
        ZoneOffset inputOffset = dateTime.getOffset();
        OffsetInfo info = zone.getOffsetInfo(dateTime.toLocalDateTime());
        if (info instanceof ZoneOffset) {
            if (info.equals(inputOffset) == false) {
                throw new CalendricalException("The offset " + inputOffset +
                        " specified in the OffsetDateTime is invalid for the time zone " + zone);
            }
        } else {
            Discontinuity disc = (Discontinuity) info;
            if (disc.isGap()) {
                throw new CalendricalException("The local time " + dateTime +
                        " does not exist in time zone " + zone + " due to a daylight savings gap");
            } else if (disc.containsOffset(inputOffset) == false) {
                throw new CalendricalException("The offset in the date-time " + dateTime +
                        " is invalid for the time zone " + zone);
            }
        }
        return new ZonedDateTime(dateTime, zone);
    }

    /**
     * Obtains an instance of <code>ZonedDateTime</code>, providing a resolver
     * to handle an invalid date-time.
     *
     * @param dateTime  the date-time, not null
     * @param zone  the time zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return a ZonedDateTime object, never null
     * @throws CalendricalException if the date-time cannot be resolved
     */
    private static ZonedDateTime dateTime(LocalDateTime dateTime, TimeZone zone, ZoneResolver resolver) {
        if (dateTime == null) {
            throw new NullPointerException("The date-time must not be null");
        }
        if (zone == null) {
            throw new NullPointerException("The time zone must not be null");
        }
        if (resolver == null) {
            throw new NullPointerException("The resolver must not be null");
        }
        OffsetDateTime offsetDT = resolver.resolve(zone, dateTime, null);
        return new ZonedDateTime(offsetDT, zone);
    }

    /**
     * Obtains an instance of <code>ZonedDateTime</code>.
     *
     * @param dateTime  the date-time, not null
     * @param oldDateTime  the old date-time prior to the calculation, not null
     * @param zone  the time zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return a ZonedDateTime object, never null
     * @throws CalendricalException if the date-time cannot be resolved
     */
    private static ZonedDateTime dateTime(LocalDateTime dateTime, OffsetDateTime oldDateTime, TimeZone zone, ZoneResolver resolver) {
        OffsetDateTime offsetDT = resolver.resolve(zone, dateTime, oldDateTime);
        return new ZonedDateTime(offsetDT, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param dateTime  the date-time, validated as not null
     * @param zone  the time zone, validated as not null
     */
    private ZonedDateTime(OffsetDateTime dateTime, TimeZone zone) {
        if (dateTime == null) {
            throw new NullPointerException("The date-time must not be null");
        }
        if (zone == null) {
            throw new NullPointerException("The time zone must not be null");
        }
        this.dateTime = dateTime;
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that describes the calendar system rules for
     * this date-time.
     *
     * @return the ISO chronology, never null
     */
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>ZonedDateTime</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(DateTimeFieldRule field) {
        return dateTime.isSupported(field);
    }

    /**
     * Gets the value of the specified calendar field.
     * <p>
     * This method queries the value of the specified calendar field.
     * If the calendar field is not supported then an exception is thrown.
     *
     * @param field  the field to query, not null
     * @return the value for the field
     * @throws UnsupportedCalendarFieldException if no value for the field is found
     * @throws InvalidCalendarFieldException if the value for the field is invalid
     */
    public int get(DateTimeFieldRule field) {
        return toFlexiDateTime().getValue(field);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>LocalDateTime</code> initialised to the
     * same date-time.
     *
     * @return the date-time object, never null
     */
    public LocalDateTime localDateTime() {
        return dateTime.localDateTime();
    }

    /**
     * Gets an instance of <code>OffsetDateTime</code> initialised to the
     * same date-time.
     *
     * @return the date-time object, never null
     */
    public OffsetDateTime offsetDateTime() {
        return dateTime;
    }

    /**
     * Gets an instance of <code>LocalDate</code> initialised to the
     * date of this date-time.
     *
     * @return the date object, never null
     */
    public LocalDate localDate() {
        return dateTime.localDate();
    }

    /**
     * Gets an instance of <code>ZonedDate</code> initialised to the
     * date of this date-time.
     *
     * @return the date object, never null
     */
    public OffsetDate offsetDate() {
        return dateTime.offsetDate();
    }

    /**
     * Gets an instance of <code>LocalTime</code> initialised to the
     * time of this date-time.
     *
     * @return the time object, never null
     */
    public LocalTime localTime() {
        return dateTime.localTime();
    }

    /**
     * Gets an instance of <code>ZonedTime</code> initialised to the
     * time of this date-time.
     *
     * @return the time object, never null
     */
    public OffsetTime offsetTime() {
        return dateTime.offsetTime();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset.
     *
     * @return the zone offset, never null
     */
    public ZoneOffset getOffset() {
        return dateTime.getOffset();
    }

    /**
     * Returns a copy of this ZonedDateTime changing the zone offset to the
     * earlier of the two valid offsets at a local time-line overlap.
     * <p>
     * This method is only useful when the local time-line overlaps, such as
     * at an autumn daylight savings cutover. In this scenario, there are two
     * valid offsets for the local date-time. Calling this method will return
     * a zoned date-time with the earlier of the two selected.
     * <p>
     * If this method is called when it is not an overlap, <code>this</code>
     * is returned.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated ZonedDateTime, never null
     */
    public ZonedDateTime withEarlierOffsetAtOverlap() {
        OffsetInfo info = zone.getOffsetInfo(localDateTime());
        if (info instanceof Discontinuity) {
            Discontinuity dis = (Discontinuity) info;
            ZoneOffset offset = dis.getOffsetBefore();
            if (offset.equals(getOffset()) == false) {
                OffsetDateTime newDT = dateTime.withOffset(offset);
                return new ZonedDateTime(newDT, zone);
            }
        }
        return this;
    }

    /**
     * Returns a copy of this ZonedDateTime changing the zone offset to the
     * later of the two valid offsets at a local time-line overlap.
     * <p>
     * This method is only useful when the local time-line overlaps, such as
     * at an autumn daylight savings cutover. In this scenario, there are two
     * valid offsets for the local date-time. Calling this method will return
     * a zoned date-time with the later of the two selected.
     * <p>
     * If this method is called when it is not an overlap, <code>this</code>
     * is returned.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated ZonedDateTime, never null
     */
    public ZonedDateTime withLaterOffsetAtOverlap() {
        OffsetInfo info = zone.getOffsetInfo(localDateTime());
        if (info instanceof Discontinuity) {
            Discontinuity dis = (Discontinuity) info;
            ZoneOffset offset = dis.getOffsetAfter();
            if (offset.equals(getOffset()) == false) {
                OffsetDateTime newDT = dateTime.withOffset(offset);
                return new ZonedDateTime(newDT, zone);
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time zone.
     *
     * @return the time zone, never null
     */
    public TimeZone getZone() {
        return zone;
    }

    /**
     * Returns a copy of this ZonedDateTime with a different time zone,
     * retaining the local date-time if possible.
     * <p>
     * This method changes the time zone and retains the local date-time.
     * The local date-time is only changed if it is invalid for the new
     * time zone. In that case, the standard resolver is used.
     * <p>
     * To change the zone and adjust the local date-time,
     * use {@link #withZoneSameInstant(TimeZone)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time zone to change to, not null
     * @return a new updated ZonedDateTime, never null
     */
    public ZonedDateTime withZoneSameLocal(TimeZone zone) {
        return zone == this.zone ? this :
            dateTime(dateTime.localDateTime(), dateTime, zone, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a copy of this ZonedDateTime with a different time zone,
     * retaining the instant.
     * <p>
     * This method changes the time zone and retains the instant.
     * This normally results in a change to the local date-time.
     * If there is an overlap in the local time-line at the new instant,
     * then the standard resolver is used.
     * <p>
     * To change the offset whilst keeping the local time,
     * use {@link #withZoneSameLocal(TimeZone)}.
     *
     * @param zone  the time zone to change to, not null
     * @return a new updated ZonedDateTime, never null
     * @throws CalendarConversionException if the result exceeds the supported date range
     */
    public ZonedDateTime withZoneSameInstant(TimeZone zone) {
        return zone == this.zone ? this :
            dateTime(toInstant(), zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method provides access to an object representing the year field.
     * This can be used to access the {@link Year#getValue() int value}.
     *
     * @return the year, never null
     */
    public Year getYear() {
        return dateTime.getYear();
    }

    /**
     * Gets the month of year field.
     * <p>
     * This method provides access to an object representing the month field.
     * This can be used to access the {@link MonthOfYear#getValue() int value}.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return dateTime.getMonthOfYear();
    }

    /**
     * Gets the day of month field.
     * <p>
     * This method provides access to an object representing the day of month field.
     * This can be used to access the {@link DayOfMonth#getValue() int value}.
     *
     * @return the day of month, never null
     */
    public DayOfMonth getDayOfMonth() {
        return dateTime.getDayOfMonth();
    }

    /**
     * Gets the day of year field.
     * <p>
     * This method provides access to an object representing the day of year field.
     * This can be used to access the {@link DayOfYear#getValue() int value}.
     *
     * @return the day of year, never null
     */
    public DayOfYear getDayOfYear() {
        return dateTime.getDayOfYear();
    }

    /**
     * Gets the day of week field.
     * <p>
     * This method provides access to an object representing the day of week field.
     * This can be used to access the {@link DayOfWeek#getValue() int value}.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return dateTime.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour of day field.
     * <p>
     * This method provides access to an object representing the hour of day field.
     * This can be used to access the {@link HourOfDay#getValue() int value}.
     *
     * @return the hour of day, never null
     */
    public HourOfDay getHourOfDay() {
        return dateTime.getHourOfDay();
    }

    /**
     * Gets the minute of hour field.
     * <p>
     * This method provides access to an object representing the minute of hour field.
     * This can be used to access the {@link MinuteOfHour#getValue() int value}.
     *
     * @return the minute of hour, never null
     */
    public MinuteOfHour getMinuteOfHour() {
        return dateTime.getMinuteOfHour();
    }

    /**
     * Gets the second of minute field.
     * <p>
     * This method provides access to an object representing the second of minute field.
     * This can be used to access the {@link SecondOfMinute#getValue() int value}.
     *
     * @return the second of minute, never null
     */
    public SecondOfMinute getSecondOfMinute() {
        return dateTime.getSecondOfMinute();
    }

    /**
     * Gets the nano of second field.
     * <p>
     * This method provides access to an object representing the nano of second field.
     * This can be used to access the {@link NanoOfSecond#getValue() int value}.
     *
     * @return the nano of second, never null
     */
    public NanoOfSecond getNanoOfSecond() {
        return dateTime.getNanoOfSecond();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTime with the date altered using the adjustor.
     * <p>
     * Adjustors can be used to alter the date in various ways.
     * A simple adjustor might simply set the one of the fields, such as the year field.
     * A more complex adjustor might set the date to the last day of the month.
     * <p>
     * If the adjusted date results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjustor  the adjustor to use, not null
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalArgumentException if the adjustor returned null
     */
    public ZonedDateTime with(DateAdjustor adjustor) {
        return with(adjustor, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a copy of this ZonedDateTime with the date altered using the
     * adjustor, providing a resolver to handle an invalid date-time.
     * <p>
     * Adjustors can be used to alter the date in various ways.
     * A simple adjustor might simply set the one of the fields, such as the year field.
     * A more complex adjustor might set the date to the last day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjustor  the adjustor to use, not null
     * @param resolver  the resolver to use, not null
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalArgumentException if the adjustor returned null
     * @throws IllegalCalendarFieldValueException if the resolver cannot resolve the date-time
     */
    public ZonedDateTime with(DateAdjustor adjustor, ZoneResolver resolver) {
        LocalDateTime newDT = dateTime.toLocalDateTime().with(adjustor);
        return (newDT == dateTime.toLocalDateTime() ? this : dateTime(newDT, dateTime, zone, resolver));
    }

    /**
     * Returns a copy of this ZonedDateTime with the time altered using the adjustor.
     * <p>
     * Adjustors can be used to alter the time in various ways.
     * A simple adjustor might simply set the one of the fields, such as the hour field.
     * A more complex adjustor might set the time to end of the working day.
     * <p>
     * If the adjusted date results in a date-time that is invalid, then the
     * {@link ZoneResolvers#retainOffset()} resolver is used.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjustor  the adjustor to use, not null
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalArgumentException if the adjustor returned null
     */
    public ZonedDateTime with(TimeAdjustor adjustor) {
        return with(adjustor, ZoneResolvers.retainOffset());
    }

    /**
     * Returns a copy of this ZonedDateTime with the time altered using the
     * adjustor, providing a resolver to handle an invalid date-time.
     * <p>
     * Adjustors can be used to alter the time in various ways.
     * A simple adjustor might simply set the one of the fields, such as the hour field.
     * A more complex adjustor might set the time to end of the working day.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjustor  the adjustor to use, not null
     * @param resolver  the resolver to use, not null
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalArgumentException if the adjustor returned null
     * @throws IllegalCalendarFieldValueException if the resolver cannot resolve the date-time
     */
    public ZonedDateTime with(TimeAdjustor adjustor, ZoneResolver resolver) {
        LocalDateTime newDT = dateTime.toLocalDateTime().with(adjustor);
        return (newDT == dateTime.toLocalDateTime() ? this : dateTime(newDT, dateTime, zone, resolver));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTime with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public ZonedDateTime withYear(int year) {
        LocalDateTime newDT = dateTime.localDateTime().withYear(year);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if the month value is invalid
     */
    public ZonedDateTime withMonthOfYear(int monthOfYear) {
        LocalDateTime newDT = dateTime.localDateTime().withMonthOfYear(monthOfYear);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if the day of month value is invalid
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public ZonedDateTime withDayOfMonth(int dayOfMonth) {
        LocalDateTime newDT = dateTime.localDateTime().withDayOfMonth(dayOfMonth);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated ZonedDateTime
     * @throws IllegalCalendarFieldValueException if the any field value is invalid
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public ZonedDateTime withDate(int year, int monthOfYear, int dayOfMonth) {
        LocalDateTime newDT = dateTime.localDateTime().withDate(year, monthOfYear, dayOfMonth);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if the hour value is invalid
     */
    public ZonedDateTime withHourOfDay(int hourOfDay) {
        LocalDateTime newDT = dateTime.localDateTime().withHourOfDay(hourOfDay);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if the minute value is invalid
     */
    public ZonedDateTime withMinuteOfHour(int minuteOfHour) {
        LocalDateTime newDT = dateTime.localDateTime().withMinuteOfHour(minuteOfHour);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if the second value is invalid
     */
    public ZonedDateTime withSecondOfMinute(int secondOfMinute) {
        LocalDateTime newDT = dateTime.localDateTime().withSecondOfMinute(secondOfMinute);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if the nanos value is invalid
     */
    public ZonedDateTime withNanoOfSecond(int nanoOfSecond) {
        LocalDateTime newDT = dateTime.localDateTime().withNanoOfSecond(nanoOfSecond);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int)} and sets
     * the second field to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public ZonedDateTime withTime(int hourOfDay, int minuteOfHour) {
        LocalDateTime newDT = dateTime.localDateTime().withTime(hourOfDay, minuteOfHour);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the time values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public ZonedDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        LocalDateTime newDT = dateTime.localDateTime().withTime(hourOfDay, minuteOfHour, secondOfMinute);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the time values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated ZonedDateTime, never null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public ZonedDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        LocalDateTime newDT = dateTime.localDateTime().withTime(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTime with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plus(PeriodView period) {
        LocalDateTime newDT = dateTime.localDateTime().plus(period);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plus(PeriodView... periods) {
        LocalDateTime newDT = dateTime.localDateTime().plus(periods);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTime with the specified period in years added.
     * <p>
     * This method add the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) plus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plusYears(int years) {
        LocalDateTime newDT = dateTime.localDateTime().plusYears(years);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the specified period in months added.
     * <p>
     * This method add the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month of year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 plus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plusMonths(int months) {
        LocalDateTime newDT = dateTime.localDateTime().plusMonths(months);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the specified period in weeks added.
     * <p>
     * This method add the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plusWeeks(int weeks) {
        LocalDateTime newDT = dateTime.localDateTime().plusWeeks(weeks);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the specified period in days added.
     * <p>
     * This method add the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plusDays(int days) {
        LocalDateTime newDT = dateTime.localDateTime().plusDays(days);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the specified period in hours added.
     * <p>
     * This method uses field based addition.
     * This method changes the field by the specified number of hours.
     * This may, at daylight savings cutover, result in a duration being added
     * that is more or less than the specified number of hours.
     * <p>
     * For example, consider a time zone where the spring DST cutover means that
     * the local times 01:00 to 01:59 do not exist. Using this method, adding
     * a period of 2 hours to 00:30 will result in 02:30, but it is important
     * to note that the change in duration was only 1 hour.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plusHours(int hours) {
        LocalDateTime newDT = dateTime.localDateTime().plusHours(hours);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plusMinutes(int minutes) {
        LocalDateTime newDT = dateTime.localDateTime().plusMinutes(minutes);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plusSeconds(int seconds) {
        LocalDateTime newDT = dateTime.localDateTime().plusSeconds(seconds);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a new updated ZonedDateTime, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ZonedDateTime plusNanos(int nanos) {
        LocalDateTime newDT = dateTime.localDateTime().plusNanos(nanos);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the date matches the specified matcher.
     * <p>
     * Matchers can be used to query the date.
     * A simple matcher might simply query one of the fields, such as the year field.
     * A more complex matcher might query if the date is the last day of the month.
     * <p>
     * The time and zone have no effect on the matching.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this date matches the matcher, false otherwise
     */
    public boolean matches(DateMatcher matcher) {
        return dateTime.matches(matcher);
    }

    /**
     * Checks whether the time matches the specified matcher.
     * <p>
     * Matchers can be used to query the time.
     * A simple matcher might simply query one of the fields, such as the hour field.
     * A more complex matcher might query if the time is during opening hours.
     * <p>
     * The date and zone have no effect on the matching.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this time matches the matcher, false otherwise
     */
    public boolean matches(TimeMatcher matcher) {
        return dateTime.matches(matcher);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to an <code>Instant</code>.
     *
     * @return an Instant representing the same instant, never null
     */
    public Instant toInstant() {
        return dateTime.toInstant();
    }

    /**
     * Converts this date-time to a <code>LocalDate</code>.
     *
     * @return a LocalDate representing the date fields of this date-time, never null
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Converts this date-time to a <code>LocalTime</code>.
     *
     * @return a LocalTime representing the time fields of this date-time, never null
     */
    public LocalTime toLocalTime() {
        return dateTime.toLocalTime();
    }

    /**
     * Converts this date-time to a <code>LocalDateTime</code>.
     *
     * @return a LocalDateTime representing the fields of this date-time, never null
     */
    public LocalDateTime toLocalDateTime() {
        return dateTime.toLocalDateTime();
    }

    /**
     * Converts this date to a <code>FlexiDateTime</code>.
     *
     * @return the flexible date-time representation for this instance, never null
     */
    public FlexiDateTime toFlexiDateTime() {
        return new FlexiDateTime(toLocalDate(), toLocalTime(), getOffset(), zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date-time to another date-time based on the UTC
     * equivalent date-times.
     * <p>
     * This ordering is inconsistent with <code>equals()</code> as two
     * date-times with the same instant will compare as equal regardless of
     * the actual zones.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(ZonedDateTime other) {
        return dateTime.compareTo(other.dateTime);
    }

    /**
     * Is this date-time after the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(ZonedDateTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date-time before the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this point is before the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(ZonedDateTime other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this date-time equal to the specified date-time.
     * <p>
     * This compares the date-time and the offset.
     *
     * @param other  the other date-time to compare to, null returns false
     * @return true if this point is equal to the specified date-time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTime = (ZonedDateTime) other;
            return dateTime.equals(zonedDateTime.dateTime) &&
                zone.equals(zonedDateTime.zone);
        }
        return false;
    }

    /**
     * A hashcode for this date-time.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return dateTime.hashCode() ^ zone.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the date-time as a <code>String</code>, such as
     * '2007-12-03T10:15:30+01:00 Europe/Paris'.
     * <p>
     * The output will be one of the following formats:
     * <ul>
     * <li>'yyyy-MM-ddThh:mmZ ZZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ssZ ZZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSZ ZZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSSSSZ ZZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSSSSSSSZ ZZ'</li>
     * </ul>
     * where 'Z' is the id of the zone offset, such as '+02:30' or 'Z' and
     * 'ZZ' is the time zone id.
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return the formatted date-time string, never null
     */
    @Override
    public String toString() {
        return dateTime.toString() + " " + zone.toString();
    }

}
