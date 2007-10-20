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

import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;
import javax.time.period.Periods;

/**
 * A calendrical representation of a date-time without a time zone expressing
 * the time component to nanosecond precision.
 * <p>
 * ZonedDateTimeHMS is an immutable calendrical that represents a date-time, often
 * viewed as year-month-day-hour-minute-second.
 * This class stores all date and time fields as well as a time zone.
 * Thus, for example, the value "2nd October 2007 at 13:45:30 in the Europe/Paris time zone"
 * can be stored in a ZonedDateTimeHMS.
 * <p>
 * ZonedDateTimeHMS is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class ZonedDateTimeHMS
        implements Calendrical, Comparable<ZonedDateTimeHMS>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -937327301L;

    /**
     * The year being represented.
     */
    private final int year;
    /**
     * The month of year being represented.
     */
    private final int monthOfYear;
    /**
     * The day of month being represented.
     */
    private final int dayOfMonth;
    /**
     * The second of days being represented.
     */
    private final int secondOfDay;
    /**
     * The time zone.
     */
    private final TimeZone zone;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>ZonedDateTimeHMS</code>.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param zone  the time zone, not null
     * @return a ZonedDateTimeHMS object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static ZonedDateTimeHMS zonedDateTime(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, TimeZone zone) {
        return null;
    }

    /**
     * Obtains an instance of <code>ZonedDateTimeHMS</code> from a set of calendricals.
     * <p>
     * This can be used to pass in any combination of calendricals that fully specify
     * a calendar day. For example, Year + MonthOfYear + DayOfMonth + TimeOfDay.
     *
     * @param calendricals  a set of calendricals that fully represent the date-time, no nulls
     * @return a ZonedDateTimeHMS object, never null
     */
    public static ZonedDateTimeHMS zonedDateTime(Calendrical... calendricals) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified date and time.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @param secondOfDay  the second of day to represent
     * @param zone  the time zone, not null
     */
    private ZonedDateTimeHMS(int year, int monthOfYear, int dayOfMonth, int secondOfDay, TimeZone zone) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.secondOfDay = secondOfDay;
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>ZonedDateTimeHMS</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return field.isSupported(Periods.NANOS, Periods.FOREVER);
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
        if (!isSupported(field)) {
            throw new UnsupportedCalendarFieldException("ZonedDateTimeHMS does not support field " + field.getName());
        }
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time zone.
     *
     * @return the time zone
     */
    public TimeZone getZone() {
        return zone;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with a different time zone, ensuring
     * that the instant remains the same.
     * This method may change the local time.
     *
     * @param zone  the time zone to change to, not null
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withZoneSameInstant(TimeZone zone) {
        return this;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with a different time zone, ensuring
     * that the local time, expressed as fields, remains the same.
     * This method may change the instant.
     *
     * @param zone  the time zone to change to, not null
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withZoneSameFields(TimeZone zone) {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ISO proleptic year value.
     * <p>
     * The year 1AD is represented by 1.<br />
     * The year 1BC is represented by 0.<br />
     * The year 2BC is represented by -1.<br />
     *
     * @return the year, from MIN_VALUE + 1 to MAX_VALUE
     */
    public int getYear() {
        return 0;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return null;
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year, from 1 to 366
     */
    public int getDayOfYear() {
        return 0;
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return 0;
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return null;
    }

    /**
     * Gets the hour of day value.
     *
     * @return the hour of day, from 0 to 23
     */
    public int getHourOfDay() {
        return 0;
    }

    /**
     * Gets the hour of day instance.
     *
     * @return the hour of day, never null
     */
    public HourOfDay hourOfDay() {
        return HourOfDay.hourOfDay(getHourOfDay());
    }

    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return 0;
    }

    /**
     * Gets the second of minute value.
     *
     * @return the second of minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return 0;
    }

    /**
     * Gets the nano of second value.
     *
     * @return the nano of second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS with(Calendrical calendrical) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS with(Calendrical... calendricals) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTimeHMS with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withYear(int year) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withMonthOfYear(int monthOfYear) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent, from 1 to 366
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withDayOfYear(int dayOfYear) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withLastDayOfYear() {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withDayOfMonth(int dayOfMonth) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withLastDayOfMonth() {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent, from 1 (Monday) to 7 (Sunday)
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withDayOfWeek(int dayOfWeek) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the date values altered.
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
    public ZonedDateTimeHMS withDate(int year, int monthOfYear, int dayOfMonth) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withHourOfDay(int hourOfDay) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withMinuteOfHour(int minuteOfHour) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withSecondOfMinute(int secondOfMinute) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withNanoOfSecond(int nanoOfSecond) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the time values altered.
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
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withTime(int hourOfDay, int minuteOfHour) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the time values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plusMonths(int months) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plusDays(int days) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified number of hours added.
     * <p>
     * This method uses field based addition.
     * This method changes the field by the specified number of hours.
     * This may, at daylight savings cutover, result in a duration being added
     * that is more or less than the specified number of hours.
     * <p>
     * For example, consider a time zone where the spring DST cutover means that
     * the local times 01:00 to 01:59 do not exist. Using this method, adding
     * a duration of 2 hours to 00:30 will result in 02:30, but it is important
     * to note that only a duration of 1 hour was actually added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plusHours(int hours) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified duration in hours added.
     * <p>
     * This method uses duration based addition.
     * This method adds the physical duration in hours specified. At the daylight
     * savings cutover, this may result in the hours field not changing by the
     * same number of hours.
     * <p>
     * For example, consider a time zone where the spring DST cutover means that
     * the local times 01:00 to 01:59 do not exist. Using this method, adding
     * a duration of 2 hours to 00:30 will result in 03:30.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plusHoursDuration(int hours) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plusMinutes(int minutes) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plusSeconds(int seconds) {
        return null;
    }

    /**
     * Returns a copy of this ZonedDateTimeHMS with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add
     * @return a new updated ZonedDateTimeHMS, never null
     */
    public ZonedDateTimeHMS plusNanos(int nanos) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date-time to another date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(ZonedDateTimeHMS other) {
        return 0;
    }

    /**
     * Is this date-time after the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(ZonedDateTimeHMS other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date-time before the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this point is before the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(ZonedDateTimeHMS other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this date-time equal to the specified date-time.
     *
     * @param other  the other date-time to compare to, null returns false
     * @return true if this point is equal to the specified date-time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ZonedDateTimeHMS) {
            ZonedDateTimeHMS zonedDateTimeHMS = (ZonedDateTimeHMS) other;
            return  true;
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
        return 0;
    }

    /**
     * Outputs the string form of the date-time.
     *
     * @return the string form of the date-time
     */
    @Override
    public String toString() {
        return super.toString();
    }

}
