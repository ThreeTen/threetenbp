/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.Instant;
import javax.time.calendar.TimeZone.Discontinuity;
import javax.time.calendar.TimeZone.OffsetInfo;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;

/**
 * A calendrical representation of a date-time with a time zone.
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
 * @author Stephen Colebourne
 */
public final class ZonedDateTime
        implements Calendrical, Comparable<ZonedDateTime>, Serializable {

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
     * @throws IllegalCalendarFieldValueException if any field is invalid
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
     * @throws IllegalCalendarFieldValueException if any field is invalid
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
     * @throws IllegalCalendarFieldValueException if any field is invalid
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
     * Obtains an instance of <code>ZonedDateTime</code>.
     *
     * @param dateTime  the date-time, not null
     * @param zone  the time zone, not null
     * @param resolver  the resolver from local date-time to zoned, not null
     * @return a ZonedDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static ZonedDateTime dateTime(LocalDateTime dateTime, TimeZone zone, ZoneResolver resolver) {
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
     * @throws IllegalCalendarFieldValueException if any field is invalid
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
        this.dateTime = dateTime;
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to
     * this date-time.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

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
    public boolean isSupported(TimeFieldRule field) {
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
     * @throws UnsupportedCalendarFieldException if the field is not supported
     */
    public int get(TimeFieldRule field) {
        return dateTime.get(field);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>Year</code> initialised to the
     * year of this date-time.
     *
     * @return the year object, never null
     */
    public Year year() {
        return dateTime.year();
    }

    /**
     * Gets an instance of <code>MonthOfYear</code> initialised to the
     * month of this date-time.
     *
     * @return the month object, never null
     */
    public MonthOfYear monthOfYear() {
        return dateTime.monthOfYear();
    }

    /**
     * Gets an instance of <code>YearMonth</code> initialised to the
     * year and month of this date-time.
     *
     * @return the year-month object, never null
     */
    public YearMonth yearMonth() {
        return dateTime.yearMonth();
    }

    /**
     * Gets an instance of <code>MonthDay</code> initialised to the
     * month and day of month of this date-time.
     *
     * @return the month-day object, never null
     */
    public MonthDay monthDay() {
        return dateTime.monthDay();
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
     */
    public ZonedDateTime withZoneSameInstant(TimeZone zone) {
        return zone == this.zone ? this :
            dateTime(toInstant(), zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ISO proleptic year value.
     * <p>
     * The year 1AD is represented by 1.<br />
     * The year 1BC is represented by 0.<br />
     * The year 2BC is represented by -1.<br />
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return dateTime.getYear();
    }

    /**
     * Gets the month of year value.
     * <p>
     * This method returns the numerical value for the month, from 1 to 12.
     * The enumerated constant is returned by {@link #monthOfYear()}.
     *
     * @return the month of year, from 1 (January) to 12 (December)
     */
    public int getMonthOfYear() {
        return dateTime.getMonthOfYear();
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year, from 1 to 366
     */
    public int getDayOfYear() {
        return dateTime.getDayOfYear();
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return dateTime.getDayOfMonth();
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return dateTime.getDayOfWeek();
    }

    /**
     * Gets the hour of day value.
     *
     * @return the hour of day, from 0 to 23
     */
    public int getHourOfDay() {
        return dateTime.getHourOfDay();
    }

    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return dateTime.getMinuteOfHour();
    }

    /**
     * Gets the second of minute value.
     *
     * @return the second of minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return dateTime.getSecondOfMinute();
    }

    /**
     * Gets the nanosecond fraction of a second expressed as an int.
     *
     * @return the nano of second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return dateTime.getNanoOfSecond();
    }

    /**
     * Gets the nanosecond fraction of a second expressed as a double.
     *
     * @return the nano of second, from 0 to 0.999,999,999
     */
    public double getNanoFraction() {
        return dateTime.getNanoFraction();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated ZonedDateTime, never null
     */
    public ZonedDateTime with(Calendrical calendrical) {
        LocalDateTime newDT = dateTime.localDateTime().with(calendrical);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated ZonedDateTime, never null
     */
    public ZonedDateTime with(Calendrical... calendricals) {
        LocalDateTime newDT = dateTime.localDateTime().with(calendricals);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTime with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated ZonedDateTime, never null
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
     */
    public ZonedDateTime withDayOfMonth(int dayOfMonth) {
        LocalDateTime newDT = dateTime.localDateTime().withDayOfMonth(dayOfMonth);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated ZonedDateTime, never null
     */
    public ZonedDateTime withLastDayOfMonth() {
        LocalDateTime newDT = dateTime.localDateTime().withLastDayOfMonth();
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the day of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent, from 1 to 366
     * @return a new updated ZonedDateTime, never null
     */
    public ZonedDateTime withDayOfYear(int dayOfYear) {
        LocalDateTime newDT = dateTime.localDateTime().withDayOfYear(dayOfYear);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated ZonedDateTime, never null
     */
    public ZonedDateTime withLastDayOfYear() {
        LocalDateTime newDT = dateTime.localDateTime().withLastDayOfYear();
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent, from 1 (Monday) to 7 (Sunday)
     * @return a new updated ZonedDateTime, never null
     */
    public ZonedDateTime withDayOfWeek(int dayOfWeek) {
        LocalDateTime newDT = dateTime.localDateTime().withDayOfWeek(dayOfWeek);
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
     */
    public ZonedDateTime withDate(int year, int monthOfYear, int dayOfMonth) {
        LocalDateTime newDT = dateTime.localDateTime().withDate(year, monthOfYear, dayOfMonth);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
    }

    /**
     * Returns a copy of this ZonedDateTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated ZonedDateTime, never null
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
     */
    public ZonedDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        LocalDateTime newDT = dateTime.localDateTime().withTime(hourOfDay, minuteOfHour, secondOfMinute);
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
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
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
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
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
     * @throws ArithmeticException if the calculation overflows
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
     */
    public ZonedDateTime plusNanos(int nanos) {
        LocalDateTime newDT = dateTime.localDateTime().plusNanos(nanos);
        return (newDT == dateTime.localDateTime() ? this :
            dateTime(newDT, dateTime, zone, ZoneResolvers.retainOffset()));
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
     * Outputs the date-time as a <code>String</code>.
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
