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

import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.FlexiDateTime;
import javax.time.period.PeriodView;

/**
 * A date-time with a zone offset from UTC in the ISO-8601 calendar system,
 * such as '2007-12-03T10:15:30+01:00'.
 * <p>
 * OffsetDateTime is an immutable calendrical that represents a date-time, often
 * viewed as year-month-day-hour-minute-second-offset.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * as well as a time zone. Thus, for example, the value
 * "2nd October 2007 at 13:45.30.123456789 +02:00" can be stored in an OffsetDateTime.
 * <p>
 * OffsetDateTime is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class OffsetDateTime
        implements InstantProvider, DateTimeProvider, Calendrical, Comparable<OffsetDateTime>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -456761901L;
    /**
     * The number of seconds per day.
     */
    private static final long SECS_PER_DAY = 24L * 60L * 60L;
    /**
     * The number of days in a 400 year cycle.
     */
    private static final int DAYS_PER_CYCLE = 146097;
    /**
     * The number of days from year zero to year 1970.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     */
    private static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5) - (30 * 365 + 7);

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
     * Obtains an instance of <code>OffsetDateTime</code> from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateMidnight(
            Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateMidnight(year, monthOfYear, dayOfMonth);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateMidnight(
            int year, MonthOfYear monthOfYear, int dayOfMonth, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateMidnight(year, monthOfYear, dayOfMonth);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateMidnight(
            int year, int monthOfYear, int dayOfMonth, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateMidnight(year, monthOfYear, dayOfMonth);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from a date with the
     * time set to midnight at the start of day.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param dateProvider  the date provider to use, not null
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     */
    public static OffsetDateTime dateMidnight(DateProvider dateProvider, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateMidnight(dateProvider);
        return new OffsetDateTime(dt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(
            Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth,
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param secondOfMinute  the second of minute to represent, not null
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(
            Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth,
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour,
            SecondOfMinute secondOfMinute, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month,
     * day, hour, minute, second and nanosecond.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @param hourOfDay  the hour of day to represent, not null
     * @param minuteOfHour  the minute of hour to represent, not null
     * @param secondOfMinute  the second of minute to represent, not null
     * @param nanoOfSecond  the nano of second to represent, not null
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(
            Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth,
            HourOfDay hourOfDay, MinuteOfHour minuteOfHour,
            SecondOfMinute secondOfMinute, NanoOfSecond nanoOfSecond, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new OffsetDateTime(dt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month,
     * day, hour, minute, second and nanosecond.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new OffsetDateTime(dt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from year, month,
     * day, hour, minute, second and nanosecond.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth,
                hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new OffsetDateTime(dt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetDateTime</code> from a date and time.
     *
     * @param dateProvider  the date provider to use, not null
     * @param timeProvider  the time provider to use, not null
     * @param offset  the zone offset, not null
     * @return a OffsetDateTime object, never null
     */
    public static OffsetDateTime dateTime(DateProvider dateProvider, TimeProvider timeProvider, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(dateProvider, timeProvider);
        return new OffsetDateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code>.
     *
     * @param dateTimeProvider  the date-time provider to use, not null
     * @param offset  the zone offset, not null
     * @return an OffsetDateTime object, never null
     */
    public static OffsetDateTime dateTime(DateTimeProvider dateTimeProvider, ZoneOffset offset) {
        LocalDateTime dt = dateTimeProvider.toLocalDateTime();
        return new OffsetDateTime(dt, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetDateTime</code> from an <code>Instant</code>.
     *
     * @param instant  the instant to convert, not null
     * @param offset  the zone offset, not null
     * @return an OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if the instant cannot be represented as a date
     */
    public static OffsetDateTime dateTime(InstantProvider instant, ZoneOffset offset) {
        if (instant == null) {
            throw new NullPointerException("The instant must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("The zone offset must not be null");
        }
        Instant i = instant.toInstant();
        // the following line may cause a wrap, but this will be caught later
        // as not all instants can be represented in an int year
        long epochSecs = i.getEpochSeconds() + offset.getAmountSeconds();
        
        long epochDays = epochSecs / SECS_PER_DAY;
        epochDays += DAYS_0000_TO_1970;
        int secsOfDay = (int) (epochSecs % SECS_PER_DAY);
        if (secsOfDay < 0) {
            secsOfDay += SECS_PER_DAY;
            epochDays--;
        }
        
        // find the march-based year
        epochDays -= 60;  // adjust to 0000-03-01 so leap day is at end of four year cycle
        long adjust = 0;
        if (epochDays < 0) {
            // adjust negative years to positive for calculation
            long adjustCycles = (epochDays + 1) / DAYS_PER_CYCLE - 1;
            adjust = adjustCycles * 400;
            epochDays += -adjustCycles * DAYS_PER_CYCLE;
        }
        long yearEst = (400 * epochDays + 591) / DAYS_PER_CYCLE;
        long doyEst = epochDays - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        if (doyEst < 0) {
            // fix estimate
            yearEst--;
            doyEst = epochDays - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        }
        yearEst += adjust;  // reset any negative year
        int marchDoy0 = (int) doyEst;
        
        // convert march-based values back to january-based
        int marchMonth0 = (marchDoy0 * 5 + 2) / 153;
        int month = (marchMonth0 + 2) % 12 + 1;
        int dom = marchDoy0 - (marchMonth0 * 306 + 5) / 10 + 1;
        yearEst += marchMonth0 / 10;
        
        // check year now we are certain it is correct
        if (yearEst > Year.MAX_YEAR) {
            throw new IllegalCalendarFieldValueException(
                    "Instant cannot be converted to a date-time as the year is greater than the maximum supported year");
        }
        if (yearEst < Year.MIN_YEAR) {
            throw new IllegalCalendarFieldValueException(
                    "Instant cannot be converted to a date-time as the year is less than the minimum supported year");
        }
        int year = (int) yearEst;
        LocalDate date = LocalDate.date(year, month, dom);
        
        // time
        int nano = i.getNanoOfSecond();
        LocalTime time = null;
        if (secsOfDay == 0 && nano == 0) {
            time = LocalTime.MIDNIGHT;
        } else {
            int hour = secsOfDay / (60 * 60);
            int min = (secsOfDay / 60) % 60;
            int sec = secsOfDay % 60;
            time = LocalTime.time(hour, min, sec, nano);
        }
        LocalDateTime dateTime = LocalDateTime.dateTime(date, time);
        return new OffsetDateTime(dateTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param dateTime  the date-time, not null
     * @param offset  the zone offset, not null
     */
    private OffsetDateTime(LocalDateTime dateTime, ZoneOffset offset) {
        if (dateTime == null) {
            throw new NullPointerException("The date-time must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("The zone offset must not be null");
        }
        this.dateTime = dateTime;
        this.offset = offset;
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
     * This method queries whether this <code>OffsetDateTime</code> can
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
     * @throws UnsupportedCalendarFieldException if the field is not supported
     */
    public int get(DateTimeFieldRule field) {
        return field.getValue(toFlexiDateTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>LocalDateTime</code> initialised to the
     * same date-time.
     *
     * @return the date-time object, never null
     */
    public LocalDateTime localDateTime() {
        return dateTime;
    }

    /**
     * Gets an instance of <code>LocalDate</code> initialised to the
     * date of this date-time.
     *
     * @return the date object, never null
     */
    public LocalDate localDate() {
        return dateTime.getDate();
    }

    /**
     * Gets an instance of <code>OffsetDate</code> initialised to the
     * date of this date-time.
     *
     * @return the date object, never null
     */
    public OffsetDate offsetDate() {
        return OffsetDate.date(localDate(), offset);
    }

    /**
     * Gets an instance of <code>LocalTime</code> initialised to the
     * time of this date-time.
     *
     * @return the time object, never null
     */
    public LocalTime localTime() {
        return dateTime.getTime();
    }

    /**
     * Gets an instance of <code>OffsetTime</code> initialised to the
     * time of this date-time.
     *
     * @return the time object, never null
     */
    public OffsetTime offsetTime() {
        return OffsetTime.time(localTime(), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset.
     *
     * @return the zone offset, never null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Returns a copy of this OffsetDateTime with a different zone offset.
     * <p>
     * This method changes the offset stored in this zoned date to a different
     * offset. No calculation is performed. The result simply represents the same
     * date and the new offset.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withOffset(ZoneOffset offset) {
        return offset == this.offset ? this : dateTime(dateTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts the local date-time using the specified offset.
     * <p>
     * This method changes the zoned time from one offset to another.
     * If this time represents 10:30+02:00 and the offset specified is
     * +03:00, then this method will return 11:30+03:00.
     * <p>
     * To change the offset whilst keeping the local time,
     * use {@link #withOffset(ZoneOffset)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime adjustLocalDateTime(ZoneOffset offset) {
        if (offset.equals(this.offset)) {
            return this;
        }
        int difference = offset.getAmountSeconds() - this.offset.getAmountSeconds();
        LocalDateTime adjusted = dateTime.plusSeconds(difference);
        return new OffsetDateTime(adjusted, offset);
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
     * Returns a copy of this OffsetDateTime with the date altered using the adjustor.
     * <p>
     * Adjustors can be used to alter the date in various ways.
     * A simple adjustor might simply set the one of the fields, such as the year field.
     * A more complex adjustor might set the date to the last day of the month.
     * <p>
     * The adjustment has no effect on the time.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjustor  the adjustor to use, not null
     * @return a new updated OffsetDateTime, never null
     * @throws IllegalArgumentException if the adjustor returned null
     */
    public OffsetDateTime with(DateAdjustor adjustor) {
        LocalDateTime newDT = dateTime.with(adjustor);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the time altered using the adjustor.
     * <p>
     * Adjustors can be used to alter the time in various ways.
     * A simple adjustor might simply set the one of the fields, such as the hour field.
     * A more complex adjustor might set the time to end of the working day.
     * <p>
     * The adjustment has no effect on the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjustor  the adjustor to use, not null
     * @return a new updated OffsetDateTime, never null
     * @throws IllegalArgumentException if the adjustor returned null
     */
    public OffsetDateTime with(TimeAdjustor adjustor) {
        LocalDateTime newDT = dateTime.with(adjustor);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDateTime with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withYear(int year) {
        LocalDateTime newDT = dateTime.withYear(year);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withMonthOfYear(int monthOfYear) {
        LocalDateTime newDT = dateTime.withMonthOfYear(monthOfYear);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withDayOfMonth(int dayOfMonth) {
        LocalDateTime newDT = dateTime.withDayOfMonth(dayOfMonth);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated OffsetDateTime
     */
    public OffsetDateTime withDate(int year, int monthOfYear, int dayOfMonth) {
        LocalDateTime newDT = dateTime.withDate(year, monthOfYear, dayOfMonth);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDateTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated OffsetDateTime, never null
     * @throws IllegalCalendarFieldValueException if the value if invalid
     */
    public OffsetDateTime withHourOfDay(int hourOfDay) {
        LocalDateTime newDT = dateTime.withHourOfDay(hourOfDay);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated OffsetDateTime, never null
     * @throws IllegalCalendarFieldValueException if the value if invalid
     */
    public OffsetDateTime withMinuteOfHour(int minuteOfHour) {
        LocalDateTime newDT = dateTime.withMinuteOfHour(minuteOfHour);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated OffsetDateTime, never null
     * @throws IllegalCalendarFieldValueException if the value if invalid
     */
    public OffsetDateTime withSecondOfMinute(int secondOfMinute) {
        LocalDateTime newDT = dateTime.withSecondOfMinute(secondOfMinute);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated OffsetDateTime, never null
     * @throws IllegalCalendarFieldValueException if the value if invalid
     */
    public OffsetDateTime withNanoOfSecond(int nanoOfSecond) {
        LocalDateTime newDT = dateTime.withNanoOfSecond(nanoOfSecond);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the time values altered.
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
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withTime(int hourOfDay, int minuteOfHour) {
        LocalDateTime newDT = dateTime.withTime(hourOfDay, minuteOfHour);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the time values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        LocalDateTime newDT = dateTime.withTime(hourOfDay, minuteOfHour, secondOfMinute);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDateTime with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plus(PeriodView period) {
        LocalDateTime newDT = dateTime.plus(period);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plus(PeriodView... periods) {
        LocalDateTime newDT = dateTime.plus(periods);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDateTime with the specified period in years added.
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
     * @return a new updated OffsetDateTime, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public OffsetDateTime plusYears(int years) {
        LocalDateTime newDT = dateTime.plusYears(years);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in months added.
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
     * @return a new updated OffsetDateTime, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public OffsetDateTime plusMonths(int months) {
        LocalDateTime newDT = dateTime.plusMonths(months);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in weeks added.
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
     * @return a new updated OffsetDateTime, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public OffsetDateTime plusWeeks(int weeks) {
        LocalDateTime newDT = dateTime.plusWeeks(weeks);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in days added.
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
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusDays(int days) {
        LocalDateTime newDT = dateTime.plusDays(days);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in hours added.
     * <p>
     * This method uses field based addition.
     * This method changes the field by the specified number of hours.
     * This may, at daylight savings cutover, result in a duration being added
     * that is more or less than the specified number of hours.
     * <p>
     * For example, consider a zone offset where the spring DST cutover means that
     * the local times 01:00 to 01:59 do not exist. Using this method, adding
     * a duration of 2 hours to 00:30 will result in 02:30, but it is important
     * to note that only a duration of 1 hour was actually added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusHours(int hours) {
        LocalDateTime newDT = dateTime.plusHours(hours);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusMinutes(int minutes) {
        LocalDateTime newDT = dateTime.plusMinutes(minutes);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusSeconds(int seconds) {
        LocalDateTime newDT = dateTime.plusSeconds(seconds);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusNanos(int nanos) {
        LocalDateTime newDT = dateTime.plusNanos(nanos);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the date matches the specified matcher.
     * <p>
     * Matchers can be used to query the date.
     * A simple matcher might simply query one of the fields, such as the year field.
     * A more complex matcher might query if the date is the last day of the month.
     * <p>
     * The time and offset have no effect on the matching.
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
     * The date and offset have no effect on the matching.
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
        return null;  // TODO
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
        return dateTime;
    }

    /**
     * Converts this date to a <code>FlexiDateTime</code>.
     *
     * @return the flexible date-time representation for this instance, never null
     */
    public FlexiDateTime toFlexiDateTime() {
        return new FlexiDateTime(toLocalDate(), toLocalTime(), offset, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date-time to another date-time based on the UTC
     * equivalent date-times.
     * <p>
     * This ordering is inconsistent with <code>equals()</code> as two
     * date-times with the same instant will compare as equal regardless of
     * the actual offsets.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(OffsetDateTime other) {
        if (offset.equals(other.offset)) {
            return dateTime.compareTo(other.dateTime);
        }
        LocalDateTime thisUTC = dateTime.plusSeconds(-offset.getAmountSeconds());
        LocalDateTime otherUTC = other.dateTime.plusSeconds(other.offset.getAmountSeconds());
        return thisUTC.compareTo(otherUTC);
    }

    /**
     * Is this date-time after the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(OffsetDateTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date-time before the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this point is before the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(OffsetDateTime other) {
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
        if (other instanceof OffsetDateTime) {
            OffsetDateTime zonedDateTime = (OffsetDateTime) other;
            return dateTime.equals(zonedDateTime.dateTime) && offset.equals(zonedDateTime.offset);
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
        return dateTime.hashCode() ^ offset.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the date-time as a <code>String</code>, such as
     * '2007-12-03T10:15:30+01:00'.
     * <p>
     * The output will be one of the following formats:
     * <ul>
     * <li>'yyyy-MM-ddThh:mmZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ssZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSSSSZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSSSSSSSZ'</li>
     * </ul>
     * where 'Z' is the id of the zone offset, such as '+02:30' or 'Z'.
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return the formatted date-time string, never null
     */
    @Override
    public String toString() {
        return dateTime.toString() + offset.toString();
    }

}
