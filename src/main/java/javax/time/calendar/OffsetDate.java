/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.calendar.format.DateTimeFormatters;
import javax.time.period.PeriodProvider;

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
 * OffsetDate is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class OffsetDate
        implements Calendrical, DateProvider, CalendricalMatcher, DateAdjuster, Comparable<OffsetDate>, Serializable {

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
     * @return the offset date, never null
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
     * @return the offset date, never null
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
     * @return the offset date, never null
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
     * @return the offset date, never null
     */
    public static OffsetDate date(DateProvider dateProvider, ZoneOffset offset) {
        LocalDate date = LocalDate.date(dateProvider);
        return new OffsetDate(date, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts an instant to an offset date.
     * <p>
     * This conversion drops the time component of the instant.
     *
     * @param instantProvider  the instant to convert, not null
     * @param offset  the zone offset, not null
     * @return the offset date, never null
     * @throws CalendarConversionException if the instant exceeds the supported date range
     */
    public static OffsetDate fromInstant(InstantProvider instantProvider, ZoneOffset offset) {
        Instant instant = Instant.instant(instantProvider);
        ISOChronology.checkNotNull(offset, "ZoneOffset must not be null");
        
        long epochSecs = instant.getEpochSeconds() + offset.getAmountSeconds();  // overflow caught later
        long yearZeroDays = (epochSecs / ISOChronology.SECONDS_PER_DAY) + ISOChronology.DAYS_0000_TO_1970;
        long secsOfDay = epochSecs % ISOChronology.SECONDS_PER_DAY;
        if (secsOfDay < 0) {
            yearZeroDays--;  // overflow caught later
        }
        LocalDate date = LocalDate.fromYearZeroDays(yearZeroDays);
        return new OffsetDate(date, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetDate</code> from a text string.
     * <p>
     * The following format is accepted in ASCII:
     * <ul>
     * <li><code>{Year}-{MonthOfYear}-{DayOfMonth}{OffsetID}</code>
     * </ul>
     * The year has between 4 and 10 digits with values from MIN_YEAR to MAX_YEAR.
     * If there are more than 4 digits then the year must be prefixed with the plus symbol.
     * Negative years are allowed, but not negative zero.
     * <p>
     * The month of year has 2 digits with values from 1 to 12.
     * <p>
     * The day of month has 2 digits with values from 1 to 31 appropriate to the month.
     * <p>
     * The offset ID is the normalized form as defined in {@link ZoneOffset}.
     *
     * @param text  the text to parse such as '2007-12-03+01:00', not null
     * @return the parsed offset date, never null
     * @throws CalendricalParseException if the text cannot be parsed
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static OffsetDate parse(String text) {
        return DateTimeFormatters.isoOffsetDate().parse(text, rule());
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
     * Gets the chronology that this date uses, which is the ISO calendar system.
     *
     * @return the ISO chronology, never null
     */
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this date then
     * <code>null</code> will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, this, this);
    }

    //-----------------------------------------------------------------------
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
        LocalDate localDate = LocalDate.date(dateProvider);
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
     * Gets the year field as a <code>Year</code>.
     * <p>
     * This method provides access to an object representing the year field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the year, never null
     */
    public Year toYear() {
        return date.toYear();
    }

    /**
     * Gets the month of year field as a <code>MonthOfYear</code>.
     * <p>
     * This method provides access to an object representing the month of year field.
     * This allows operations to be performed on this field in a type-safe manner.
     * <p>
     * This method is the same as {@link #getMonthOfYear()}.
     *
     * @return the month of year, never null
     */
    public MonthOfYear toMonthOfYear() {
        return date.toMonthOfYear();
    }

    /**
     * Gets the day of month field as a <code>DayOfMonth</code>.
     * <p>
     * This method provides access to an object representing the day of month field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the day of month, never null
     */
    public DayOfMonth toDayOfMonth() {
        return date.toDayOfMonth();
    }

    /**
     * Gets the day of year field as a <code>DayOfYear</code>.
     * <p>
     * This method provides access to an object representing the day of year field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the day of year, never null
     */
    public DayOfYear toDayOfYear() {
        return date.toDayOfYear();
    }

    /**
     * Gets the day of week field as a <code>DayOfWeek</code>.
     * <p>
     * This method provides access to an object representing the day of week field.
     * This allows operations to be performed on this field in a type-safe manner.
     * <p>
     * This method is the same as {@link #getDayOfWeek()}.
     *
     * @return the day of week, never null
     */
    public DayOfWeek toDayOfWeek() {
        return date.toDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive <code>int</code> value for the year.
     * <p>
     * Additional information about the year can be obtained from via {@link #toYear()}.
     * This returns a <code>Year</code> object which includes information on whether
     * this is a leap year and its length in days. It can also be used as a {@link CalendricalMatcher}
     * and a {@link DateAdjuster}.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return date.getYear();
    }

    /**
     * Gets the month of year field, which is an enum <code>MonthOfYear</code>.
     * <p>
     * This method returns the enum {@link MonthOfYear} for the month.
     * This avoids confusion as to what <code>int</code> values mean.
     * If you need access to the primitive <code>int</code> value then the enum
     * provides the {@link MonthOfYear#getValue() int value}.
     * <p>
     * Additional information can be obtained from the <code>MonthOfYear</code>.
     * This includes month lengths, textual names and access to the quarter of year
     * and month of quarter values.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return date.getMonthOfYear();
    }

    /**
     * Gets the day of month field.
     * <p>
     * This method returns the primitive <code>int</code> value for the day of month.
     * <p>
     * Additional information about the day of month can be obtained from via {@link #toDayOfMonth()}.
     * This returns a <code>DayOfMonth</code> object which can be used as a {@link CalendricalMatcher}
     * and a {@link DateAdjuster}.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    /**
     * Gets the day of year field.
     * <p>
     * This method returns the primitive <code>int</code> value for the day of year.
     * <p>
     * Additional information about the day of year can be obtained from via {@link #toDayOfYear()}.
     * This returns a <code>DayOfYear</code> object which can be used as a {@link CalendricalMatcher}
     * and a {@link DateAdjuster}.
     *
     * @return the day of year, from 1 to 365, or 366 in a leap year
     */
    public int getDayOfYear() {
        return date.getDayOfYear();
    }

    /**
     * Gets the day of week field, which is an enum <code>DayOfWeek</code>.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day of week.
     * This avoids confusion as to what <code>int</code> values mean.
     * If you need access to the primitive <code>int</code> value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the <code>DayOfWeek</code>.
     * This includes textual names of the values.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the date altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the date in unusual ways. Examples might
     * be an adjuster that set the date avoiding weekends, or one that sets the
     * date to the last day of the month.
     * <p>
     * The offset has no effect on and is not affected by the adjustment.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a new updated OffsetDate, never null
     * @throws IllegalArgumentException if the adjuster returned null
     */
    public OffsetDate with(DateAdjuster adjuster) {
        LocalDate newDate = date.with(adjuster);
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
     * This adds the amount in years, months and days from the specified period to this date.
     * Any time amounts, such as hours, minutes or seconds are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plus(PeriodProvider periodProvider) {
        LocalDate newDate = date.plus(periodProvider);
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
     * This subtracts the amount in years, months and days from the specified period from this date.
     * Any time amounts, such as hours, minutes or seconds are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a new updated OffsetDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minus(PeriodProvider periodProvider) {
        LocalDate newDate = date.minus(periodProvider);
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
     * Matchers can be used to query the date.
     * A simple matcher might simply compare one of the fields, such as the year field.
     * A more complex matcher might check if the date is the last day of the month.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this date matches the matcher, false otherwise
     */
    public boolean matches(CalendricalMatcher matcher) {
        return matcher.matchesCalendrical(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the date extracted from the calendrical matches this.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        return this.equals(calendrical.get(rule()));
    }

    /**
     * Adjusts a date to have the value of the date part of this object.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     */
    public LocalDate adjustDate(LocalDate date) {
        return this.date.adjustDate(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an offset date-time formed from this date at the specified time.
     * <p>
     * This merges the two objects - <code>this</code> and the specified time -
     * to form an instance of <code>OffsetDateTime</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to use, not null
     * @return the offset date-time formed from this date and the specified time, never null
     */
    public OffsetDateTime atTime(LocalTime time) {
        return OffsetDateTime.dateTime(this, time, getOffset());
    }

    /**
     * Returns an offset date-time formed from this date at the time of midnight.
     * <p>
     * This merges the two objects - <code>this</code> and {@link LocalTime#MIDNIGHT} -
     * to form an instance of <code>OffsetDateTime</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return the offset date-time formed from this date and the time of midnight, never null
     */
    public OffsetDateTime atMidnight() {
        return OffsetDateTime.dateTime(this, LocalTime.MIDNIGHT, getOffset());
    }

    /**
     * Returns a zoned date-time from this date at the earliest valid time according
     * to the rules in the time-zone ignoring the current offset.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. When this method converts the date to a date-time it
     * adjusts the time and offset as necessary to ensure that the time is as early
     * as possible on the date, which is typically midnight. Internally this is
     * achieved using the {@link ZoneResolvers#postGapPreOverlap() zone resolver}.
     * <p>
     * To convert to a specific time in a given time-zone call {@link #atTime(LocalTime)}
     * followed by {@link OffsetDateTime#atZone(TimeZone)}. Note that the resolver used
     * by <code>atZone()</code> is different to that used here (it chooses the later
     * offset in an overlap, whereas this method chooses the earlier offset).
     * <p>
     * The offset from this date is ignored during the conversion.
     * This ensures that the resultant date-time has the same date as this.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date and the earliest valid time for the zone, never null
     */
    public ZonedDateTime atStartOfDayInZone(TimeZone zone) {
        return ZonedDateTime.dateTime(this, LocalTime.MIDNIGHT, zone, ZoneResolvers.postGapPreOverlap());
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
     * @return the comparator value, negative if less, positive if greater
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
     * A hash code for this date.
     *
     * @return a suitable hash code
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

    //-----------------------------------------------------------------------
    /**
     * Gets the field rule for <code>OffsetDate</code>.
     *
     * @return the field rule for the date, never null
     */
    public static CalendricalRule<OffsetDate> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<OffsetDate> implements Serializable {
        private static final CalendricalRule<OffsetDate> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(OffsetDate.class, ISOChronology.INSTANCE, "OffsetDate");
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected OffsetDate derive(Calendrical calendrical) {
            OffsetDateTime odt = calendrical.get(OffsetDateTime.rule());
            return odt != null ? odt.toOffsetDate() : null;
        }
    }

}
