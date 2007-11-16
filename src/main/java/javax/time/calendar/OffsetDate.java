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
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;

/**
 * A calendrical representation of a date with a zone offset from UTC,
 * such as 2007-12-03+02:00.
 * <p>
 * OffsetDate is an immutable calendrical that represents a date, often viewed
 * as year-month-day-offset. This object can also sccess other date fields such as
 * day of year, day of week and week of year.
 * <p>
 * This class does not store or represent a time.
 * Thus, for example, the value "2nd October 2007 +02:00" can be stored
 * in a OffsetDate.
 * <p>
 * OffsetDate is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class OffsetDate
        implements Calendrical, Comparable<OffsetDate>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -3618963189L;

    /**
     * The date.
     */
    private final LocalDate date;
    /**
     * The zone offset.
     */
    private final ZoneOffset offset;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetDate</code>.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return an OffsetDate object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDate date(int year, MonthOfYear monthOfYear, int dayOfMonth, ZoneOffset offset) {
        return date(year, monthOfYear.getMonthOfYear(), dayOfMonth, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDate</code>.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return an OffsetDate object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDate date(int year, int monthOfYear, int dayOfMonth, ZoneOffset offset) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        return date(date, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDate</code>.
     *
     * @param date  the date to represent, not null
     * @param offset  the zone offset, not null
     * @return an OffsetDate object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDate date(LocalDate date, ZoneOffset offset) {
        if (date == null) {
            throw new NullPointerException("The date must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("The zone offset must not be null");
        }
        return new OffsetDate(date, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param date  the date, validated as not null
     * @param offset  the zone offset, validated as not null
     */
    private OffsetDate(LocalDate date, ZoneOffset offset) {
        this.date = date;
        this.offset = offset;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to
     * this date.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    /**
     * Gets the chronology that describes the calendar system rules for
     * this date.
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
     * This method queries whether this <code>OffsetDate</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return date.isSupported(field);
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
        return date.get(field);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>Year</code> initialised to the
     * year of this date.
     *
     * @return the year object, never null
     */
    public Year year() {
        return date.year();
    }

    /**
     * Gets an instance of <code>MonthOfYear</code> initialised to the
     * month of this date.
     *
     * @return the month object, never null
     */
    public MonthOfYear monthOfYear() {
        return date.monthOfYear();
    }

    /**
     * Gets an instance of <code>YearMonth</code> initialised to the
     * year and month of this date.
     *
     * @return the year-month object, never null
     */
    public YearMonth yearMonth() {
        return date.yearMonth();
    }

    /**
     * Gets an instance of <code>MonthDay</code> initialised to the
     * month and day of month of this date.
     *
     * @return the month-day object, never null
     */
    public MonthDay monthDay() {
        return date.monthDay();
    }

    /**
     * Gets an instance of <code>LocalDate</code> which represents the
     * date of this object but without the zone offset.
     *
     * @return the date object, never null
     */
    public LocalDate localDate() {
        return date;
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
     * Returns a copy of this OffsetTime with a different zone offset.
     * <p>
     * This method changes the offset stored in this zoned date to a different
     * offset. No calculation is performed. The result simply represents the same
     * date and the new offset.
     *
     * @param zone  the zone offset to change to, not null
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withOffset(ZoneOffset zone) {
        return zone == this.offset ? this : date(date, zone);
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
        return date.getYear();
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
        return date.getMonthOfYear();
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year, from 1 to 366
     */
    public int getDayOfYear() {
        return date.getDayOfYear();
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate with(Calendrical calendrical) {
        LocalDate newDate = date.with(calendrical);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate with(Calendrical... calendricals) {
        LocalDate newDate = date.with(calendricals);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withYear(int year) {
        LocalDate newDate = date.withYear(year);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withMonthOfYear(int monthOfYear) {
        LocalDate newDate = date.withMonthOfYear(monthOfYear);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withDayOfMonth(int dayOfMonth) {
        LocalDate newDate = date.withDayOfMonth(dayOfMonth);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withLastDayOfMonth() {
        LocalDate newDate = date.withLastDayOfMonth();
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the day of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent, from 1 to 366
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withDayOfYear(int dayOfYear) {
        LocalDate newDate = date.withDayOfYear(dayOfYear);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withLastDayOfYear() {
        LocalDate newDate = date.withLastDayOfYear();
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent, from 1 (Monday) to 7 (Sunday)
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withDayOfWeek(int dayOfWeek) {
        LocalDate newDate = date.withDayOfWeek(dayOfWeek);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate plus(PeriodView period) {
        LocalDate newDate = date.plus(period);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate plus(PeriodView... periods) {
        LocalDate newDate = date.plus(periods);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the specified period in years added.
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
     * @return a new updated OffsetDate, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public OffsetDate plusYears(int years) {
        LocalDate newDate = date.plusYears(years);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in months added.
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
     * @return a new updated OffsetDate, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public OffsetDate plusMonths(int months) {
        LocalDate newDate = date.plusMonths(months);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in weeks added.
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
     * @return a new updated OffsetDate, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public OffsetDate plusWeeks(int weeks) {
        LocalDate newDate = date.plusWeeks(weeks);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in days added.
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
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate plusDays(int days) {
        LocalDate newDate = date.plusDays(days);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     * <p>
     * This compares based on the date, then the offset.
     * This is equivalent to comparing on the start instant.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(OffsetDate other) {
        if (offset.equals(other.offset)) {
            return date.compareTo(other.date);
        }
        LocalDateTime thisDT = LocalDateTime.dateMidnight(getYear(), getMonthOfYear(), getDayOfMonth());
        LocalDateTime otherDT = LocalDateTime.dateMidnight(other.getYear(), other.getMonthOfYear(), other.getDayOfMonth());
        LocalDateTime thisUTC = thisDT.plusSeconds(-offset.getAmountSeconds());
        LocalDateTime otherUTC = otherDT.plusSeconds(other.offset.getAmountSeconds());
        return thisUTC.compareTo(otherUTC);
    }

    /**
     * Is this date after the specified date.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the specified date
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(OffsetDate other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date before the specified date.
     *
     * @param other  the other date to compare to, not null
     * @return true if this point is before the specified date
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(OffsetDate other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this date equal to the specified date.
     * <p>
     * This compares the date and the offset.
     *
     * @param other  the other date to compare to, null returns false
     * @return true if this point is equal to the specified date
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof OffsetDate) {
            OffsetDate zonedDate = (OffsetDate) other;
            return date.equals(zonedDate.date) && offset.equals(zonedDate.offset);
        }
        return false;
    }

    /**
     * A hashcode for this date.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return date.hashCode() ^ offset.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the time as a <code>String</code>.
     * <p>
     * The output will be in the format 'yyyy-MM-ddZ' where 'Z' is the id of
     * the zone offset, such as '+02:30' or 'Z'.
     *
     * @return the formatted time string, never null
     */
    @Override
    public String toString() {
        return date.toString() + offset.toString();
    }

}
