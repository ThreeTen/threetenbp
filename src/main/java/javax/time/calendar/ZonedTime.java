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

import javax.time.MathUtils;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;
import javax.time.period.Periods;

/**
 * <p>
 * ZonedTime is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class ZonedTime
        implements Calendrical, Comparable<ZonedTime>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -1751032571L;

    /**
     * The year, from MIN_YEAR to MAX_YEAR.
     */
    private final int year;
    /**
     * The month, from 1 to 12.
     */
    private final int month;
    /**
     * The dayOfMonth, from 1 to 31.
     */
    private final int dayOfMonth;
    /**
     * The hour, from 0 to 23.
     */
    private final int hour;
    /**
     * The minute, from 0 to 59.
     */
    private final int minute;
    /**
     * The second, from 0 to 59.
     */
    private final int second;
    /**
     * The nanosecond, from 0 to 999,999,999.
     */
    private final int nano;
    /**
     * The time zone.
     */
    private final TimeZone zone;

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @param zone  the time zone, not null
     */
    private ZonedTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, TimeZone zone) {
        this.year = year;
        this.month = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.hour = hourOfDay;
        this.minute = minuteOfHour;
        this.second = secondOfMinute;
        this.nano = nanoOfSecond;
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to
     * this time.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    /**
     * Gets the chronology that describes the calendar system rules for
     * this time.
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
     * This method queries whether this <code>ZonedTime</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return field.isSupported(Periods.NANOS, Periods.DAYS);
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
            throw new UnsupportedCalendarFieldException("ZonedTime does not support field " + field.getName());
        }
        if (field == ISOChronology.INSTANCE.yearRule()) {
            return year;
        }
        if (field == ISOChronology.INSTANCE.monthOfYearRule()) {
            return month;
        }
        if (field == ISOChronology.INSTANCE.dayOfMonthRule()) {
            return dayOfMonth;
        }
        if (field == ISOChronology.INSTANCE.hourOfDayRule()) {
            return hour;
        }
        if (field == ISOChronology.INSTANCE.minuteOfHourRule()) {
            return minute;
        }
        if (field == ISOChronology.INSTANCE.secondOfMinuteRule()) {
            return second;
        }
        if (field == ISOChronology.INSTANCE.nanoOfSecondRule()) {
            return nano;
        }
        return field.getValue(getCalendricalState());
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
     * Returns a copy of this ZonedTime with a different time zone, ensuring
     * that the instant remains the same.
     * This method may change the local time.
     *
     * @param zone  the time zone to change to, not null
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withZoneSameInstant(TimeZone zone) {
        return this;
    }

    /**
     * Returns a copy of this ZonedTime with a different time zone, ensuring
     * that the local time, expressed as fields, remains the same.
     * This method may change the instant.
     *
     * @param zone  the time zone to change to, not null
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withZoneSameFields(TimeZone zone) {
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
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return MonthOfYear.monthOfYear(month);
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year, from 1 to 366
     */
    public int getDayOfYear() {
        return ISOChronology.INSTANCE.getDayOfYear(year, month, dayOfMonth);
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return dayOfMonth;
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
        return hour;
    }

    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return minute;
    }

    /**
     * Gets the second of minute value.
     *
     * @return the second of minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return second;
    }

    /**
     * Gets the nanosecond fraction of a second expressed as an int.
     *
     * @return the nano of second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return nano;
    }

    /**
     * Gets the nanosecond fraction of a second expressed as a double.
     *
     * @return the nano of second, from 0 to 0.999,999,999
     */
    public double getNanoFraction() {
        return ((double) nano) / 1000000000d;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime with(Calendrical calendrical) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime with(Calendrical... calendricals) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedTime with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withYear(int year) {
        if (this.year == year) {
            return this;
        }
        int[] resolved = CalendricalResolvers.previousValid().resolveDate(year, month, dayOfMonth);
        return new ZonedTime(resolved[0], resolved[1], resolved[2], hour, minute, second, nano, zone);
    }

    /**
     * Returns a copy of this ZonedTime with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withMonthOfYear(int monthOfYear) {
        if (this.month == monthOfYear) {
            return this;
        }
        int[] resolved = CalendricalResolvers.previousValid().resolveDate(year, monthOfYear, dayOfMonth);
        return new ZonedTime(resolved[0], resolved[1], resolved[2], hour, minute, second, nano, zone);
    }

    /**
     * Returns a copy of this ZonedTime with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withDayOfMonth(int dayOfMonth) {
        if (this.dayOfMonth == dayOfMonth) {
            return this;
        }
        ISOChronology.INSTANCE.checkValidDate(year, month, dayOfMonth);
        return new ZonedTime(year, month, dayOfMonth, hour, minute, second, nano, zone);
    }

    /**
     * Returns a copy of this ZonedTime with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withLastDayOfMonth() {
        int dom = ISOChronology.INSTANCE.getMonthLength(year, month);
        if (this.dayOfMonth == dom) {
            return this;
        }
        return new ZonedTime(year, month, dom, hour, minute, second, nano, zone);
    }

    /**
     * Returns a copy of this ZonedTime with the day of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent, from 1 to 366
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withDayOfYear(int dayOfYear) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withLastDayOfYear() {
        return new ZonedTime(year, 12, 31, hour, minute, second, nano, zone);
    }

    /**
     * Returns a copy of this ZonedTime with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent, from 1 (Monday) to 7 (Sunday)
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withDayOfWeek(int dayOfWeek) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the date values altered.
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
    public ZonedTime withDate(int year, int monthOfYear, int dayOfMonth) {
        if (this.year == year && this.dayOfMonth == dayOfMonth && this.dayOfMonth == dayOfMonth) {
            return this;
        }
        ISOChronology.INSTANCE.checkValidDate(year, monthOfYear, dayOfMonth);
        return new ZonedTime(year, monthOfYear, dayOfMonth, hour, minute, second, nano, zone);
    }

    /**
     * Returns a copy of this ZonedTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withHourOfDay(int hourOfDay) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withMinuteOfHour(int minuteOfHour) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withSecondOfMinute(int secondOfMinute) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withNanoOfSecond(int nanoOfSecond) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the time values altered.
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
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withTime(int hourOfDay, int minuteOfHour) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the time values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedTime with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this ZonedTime with the specified period in years added.
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
     * @return a new updated ZonedTime, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public ZonedTime plusYears(int years) {
        if (years == 0) {
            return this;
        }
        int newYear = MathUtils.safeAdd(year, years);
        return withYear(newYear);
    }

    /**
     * Returns a copy of this ZonedTime with the specified period in months added.
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
     * @return a new updated ZonedTime, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public ZonedTime plusMonths(int months) {
        if (months == 0) {
            return this;
        }
        long newMonth0 = month - 1;
        newMonth0 = newMonth0 + months;
        int years = (int) (newMonth0 / 12);
        newMonth0 = Math.abs(newMonth0) % 12;
        if (years < 0) {
            years--;
        }
        int newYear = MathUtils.safeAdd(year, years);
        int[] resolved = CalendricalResolvers.previousValid().resolveDate(newYear, (int) (newMonth0 + 1), dayOfMonth);
        return new ZonedTime(resolved[0], resolved[1], resolved[2], hour, minute, second, nano, zone);
    }

    /**
     * Returns a copy of this ZonedTime with the specified period in weeks added.
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
     * @return a new updated ZonedTime, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public ZonedTime plusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the specified period in days added.
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
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime plusDays(int days) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the specified period in hours added.
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
     * @param hours  the hours to add, may be negative
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime plusHours(int hours) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the specified duration in hours added.
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
     * @param hours  the hours to add, may be negative
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime plusHoursDuration(int hours) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime plusMinutes(int minutes) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime plusSeconds(int seconds) {
        return null;
    }

    /**
     * Returns a copy of this ZonedTime with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a new updated ZonedTime, never null
     */
    public ZonedTime plusNanos(int nanos) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this time to another time.
     *
     * @param other  the other time to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(ZonedTime other) {
        return 0;
    }

    /**
     * Is this time after the specified time.
     *
     * @param other  the other time to compare to, not null
     * @return true if this is after the specified time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(ZonedTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this time before the specified time.
     *
     * @param other  the other time to compare to, not null
     * @return true if this point is before the specified time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(ZonedTime other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this time equal to the specified time.
     *
     * @param other  the other time to compare to, null returns false
     * @return true if this point is equal to the specified time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ZonedTime) {
            ZonedTime zonedTime = (ZonedTime) other;
            return  true;
        }
        return false;
    }

    /**
     * A hashcode for this time.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Outputs the string form of the time.
     *
     * @return the string form of the time
     */
    @Override
    public String toString() {
        return super.toString();
    }

}
