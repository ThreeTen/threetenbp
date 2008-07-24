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
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.period.PeriodView;

/**
 * A date with a zone offset from UTC in the ISO-8601 calendar system,
 * such as '2007-12-03+01:00'.
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
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class OffsetDate
        implements CalendricalProvider, DateProvider, DateMatcher, DateAdjustor, Comparable<OffsetDate>, Serializable {

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
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @param offset  the zone offset, not null
     * @return an OffsetDate object, never null
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static OffsetDate date(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth, ZoneOffset offset) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        return new OffsetDate(date, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDate</code>.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return an OffsetDate object, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static OffsetDate date(int year, MonthOfYear monthOfYear, int dayOfMonth, ZoneOffset offset) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        return new OffsetDate(date, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDate</code>.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return an OffsetDate object, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static OffsetDate date(int year, int monthOfYear, int dayOfMonth, ZoneOffset offset) {
        LocalDate date = LocalDate.date(year, monthOfYear, dayOfMonth);
        return new OffsetDate(date, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDate</code>.
     *
     * @param dateProvider  the date provider to use, not null
     * @param offset  the zone offset, not null
     * @return an OffsetDate object, never null
     */
    public static OffsetDate date(DateProvider dateProvider, ZoneOffset offset) {
        LocalDate date = LocalDate.date(dateProvider);
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
        if (date == null) {
            throw new NullPointerException("The date must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("The zone offset must not be null");
        }
        this.date = date;
        this.offset = offset;
    }

    //-----------------------------------------------------------------------
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
     * @param fieldRule  the field to query, null returns false
     * @return true if the field is supported, false otherwise
     */
    public boolean isSupported(DateTimeFieldRule fieldRule) {
        return date.isSupported(fieldRule);
    }

    /**
     * Gets the value of the specified calendar field.
     * <p>
     * This method queries the value of the specified calendar field.
     * If the calendar field is not supported then an exception is thrown.
     *
     * @param fieldRule  the field to query, not null
     * @return the value for the field
     * @throws UnsupportedCalendarFieldException if no value for the field is found
     */
    public int get(DateTimeFieldRule fieldRule) {
        return toCalendrical().getValue(fieldRule);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the local date.
     * <p>
     * This returns the date without the zone offset.
     *
     * @return the local date, never null
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns a copy of this OffsetDate with a different local date.
     * <p>
     * This method changes the date stored to a different date.
     * No calculation is performed. The result simply represents the same
     * offset and the new date.
     *
     * @param dateProvider  the local date to change to, not null
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withDate(DateProvider dateProvider) {
        LocalDate localDate = dateProvider.toLocalDate();
        return localDate.equals(this.date) ? this : new OffsetDate(localDate, offset);
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
     * This method changes the offset stored to a different offset.
     * No calculation is performed. The result simply represents the same
     * local date and the new offset.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withOffset(ZoneOffset offset) {
        return offset != null && offset.equals(this.offset) ? this : new OffsetDate(date, offset);
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
        return date.getYear();
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
        return date.getMonthOfYear();
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
        return date.getDayOfMonth();
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
        return date.getDayOfYear();
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
        return date.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the date altered using the adjustor.
     * <p>
     * Adjustors can be used to alter the date in unusual ways. Examples might
     * be an adjustor that set the date avoiding weekends, or one that sets the
     * date to the last day of the month.
     * <p>
     * The offset has no effect on and is not affected by the adjustment.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjustor  the adjustor to use, not null
     * @return a new updated OffsetDate, never null
     * @throws IllegalArgumentException if the adjustor returned null
     */
    public OffsetDate with(DateAdjustor adjustor) {
        LocalDate newDate = date.with(adjustor);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the year value altered.
     * <p>
     * This method does the same as <code>withYear(year, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated OffsetDate, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     * @see #withYear(int,DateResolver)
     */
    public OffsetDate withYear(int year) {
        LocalDate newDate = date.withYear(year);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the year value altered.
     * If the resulting <code>OffsetDate</code> is invalid, it will be resolved using <code>dateResolver</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated OffsetDate, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public OffsetDate withYear(int year, DateResolver dateResolver) {
        LocalDate newDate = date.withYear(year, dateResolver);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the month of year value altered.
     * <p>
     * This method does the same as <code>withMonthOfYear(monthOfYear, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated OffsetDate, never null
     * @throws IllegalCalendarFieldValueException if the month value is invalid
     * @see #withMonthOfYear(int,DateResolver)
     */
    public OffsetDate withMonthOfYear(int monthOfYear) {
        LocalDate newDate = date.withMonthOfYear(monthOfYear);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the month of year value altered.
     * If the resulting <code>OffsetDate</code> is invalid, it will be resolved using <code>dateResolver</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated OffsetDate, never null
     * @throws IllegalCalendarFieldValueException if the month of year value is invalid
     */
    public OffsetDate withMonthOfYear(int monthOfYear, DateResolver dateResolver) {
        LocalDate newDate = date.withMonthOfYear(monthOfYear, dateResolver);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated OffsetDate, never null
     * @throws IllegalCalendarFieldValueException if the day of month value is invalid
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public OffsetDate withDayOfMonth(int dayOfMonth) {
        LocalDate newDate = date.withDayOfMonth(dayOfMonth);
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * This method does the same as <code>plusYears(years, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusYears(int, javax.time.calendar.DateResolver)
     */
    public OffsetDate plusYears(int years) {
        LocalDate newDate = date.plusYears(years);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in years added.
     * <p>
     * This method add the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using <code>dateResolver</code> if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusYears(int years, DateResolver dateResolver) {
        LocalDate newDate = date.plusYears(years, dateResolver);
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
     * This method does the same as <code>plusMonths(months, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusMonths(int, javax.time.calendar.DateResolver)
     */
    public OffsetDate plusMonths(int months) {
        LocalDate newDate = date.plusMonths(months);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in months added.
     * <p>
     * This method add the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month of year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using <code>dateResolver</code> if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusMonths(int months, DateResolver dateResolver) {
        LocalDate newDate = date.plusMonths(months, dateResolver);
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
     * @throws CalendricalException if the result exceeds the supported date range
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
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusDays(long days) {
        LocalDate newDate = date.plusDays(days);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the specified period subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, not null
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minus(PeriodView period) {
        LocalDate newDate = date.minus(period);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified periods subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to subtract, no nulls
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minus(PeriodView... periods) {
        LocalDate newDate = date.minus(periods);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the specified period in years subtracted.
     * <p>
     * This method subtract the specified amount to the years field in three steps:
     * <ol>
     * <li>Subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2007-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2007-02-28, is selected instead.
     * <p>
     * This method does the same as <code>minusYears(years, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusYears(int, javax.time.calendar.DateResolver)
     */
    public OffsetDate minusYears(int years) {
        LocalDate newDate = date.minusYears(years);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in years subtracted.
     * <p>
     * This method subtract the specified amount to the years field in three steps:
     * <ol>
     * <li>Subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using <code>dateResolver</code> if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusYears(int years, DateResolver dateResolver) {
        LocalDate newDate = date.minusYears(years, dateResolver);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in months subtracted.
     * <p>
     * This method subtract the specified amount to the months field in three steps:
     * <ol>
     * <li>Subtract the input months to the month of year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-02-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-02-28, is selected instead.
     * <p>
     * This method does the same as <code>minusMonths(months, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusMonths(int, javax.time.calendar.DateResolver)
     */
    public OffsetDate minusMonths(int months) {
        LocalDate newDate = date.minusMonths(months);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in months subtracted.
     * <p>
     * This method subtract the specified amount to the months field in three steps:
     * <ol>
     * <li>Subtract the input months to the month of year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using <code>dateResolver</code> if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusMonths(int months, DateResolver dateResolver) {
        LocalDate newDate = date.minusMonths(months, dateResolver);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in weeks subtracted.
     * <p>
     * This method subtract the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-07 minus one week would result in the 2008-12-31.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusWeeks(int weeks) {
        LocalDate newDate = date.minusWeeks(weeks);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified number of days subtracted.
     * <p>
     * This method subtract the specified amount to the days field decrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-01 minus one day would result in the 2008-12-31.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusDays(long days) {
        LocalDate newDate = date.minusDays(days);
        return newDate == date ? this : new OffsetDate(newDate, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this date matches the specified matcher.
     * <p>
     * Matchers can be used to query the date in unusual ways. Examples might
     * be a matcher that checks if the date is a weekend or holiday, or
     * Friday the Thirteenth.
     * <p>
     * The offset has no effect on the matching.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this date matches the matcher, false otherwise
     */
    public boolean matches(DateMatcher matcher) {
        return date.matches(matcher);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the date part of this object is equal to the input date
     *
     * @param date  the date to match, not null
     * @return true if the date part matches the other date, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return this.date.matchesDate(date);
    }

    /**
     * Adjusts a date to have the value of the date part of this object.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     */
    public LocalDate adjustDate(LocalDate date) {
        return matchesDate(date) ? date : this.date;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to a <code>LocalDate</code>.
     *
     * @return a LocalDate with the same date as this instance, never null
     */
    public LocalDate toLocalDate() {
        return date;
    }

    /**
     * Converts this date to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return Calendrical.calendrical(date, null, offset, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date based on the UTC equivalent dates
     * then local date.
     * <p>
     * This ordering is consistent with <code>equals()</code>.
     * For example, the following is the comparator order:
     * <ol>
     * <li>2008-06-29-11:00</li>
     * <li>2008-06-29-12:00</li>
     * <li>2008-06-30+12:00</li>
     * <li>2008-06-29-13:00</li>
     * </ol>
     * Values #2 and #3 represent the same instant on the time-line.
     * When two values represent the same instant, the local date is compared
     * to distinguish them. This step is needed to make the ordering
     * consistent with <code>equals()</code>.
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
        LocalDateTime otherUTC = otherDT.plusSeconds(-other.offset.getAmountSeconds());
        int compare = thisUTC.compareTo(otherUTC);
        if (compare == 0) {
            compare = date.compareTo(other.date);
        }
        return compare;
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
     * Outputs the date as a <code>String</code>, such as '2007-12-03+01:00'.
     * <p>
     * The output will be in the format 'yyyy-MM-ddZ' where 'Z' is the id of
     * the zone offset, such as '+02:30' or 'Z'.
     *
     * @return the formatted date string, never null
     */
    @Override
    public String toString() {
        return date.toString() + offset.toString();
    }

}
