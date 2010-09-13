/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.MathUtils;
import javax.time.calendar.format.CalendricalPrintException;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatters;

/**
 * A date with a zone offset from UTC in the ISO-8601 calendar system,
 * such as '2007-12-03+01:00'.
 * <p>
 * OffsetDate is an immutable calendrical that represents a date, often viewed
 * as year-month-day-offset. This object can also access other date fields such as
 * day-of-year, day-of-week and week-of-year.
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
     * Obtains the current date from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * The offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, never null
     */
    public static OffsetDate now(Clock clock) {
        ISOChronology.checkNotNull(clock, "Clock must not be null");
        return ofInstant(clock.instant(), clock.getZone().getRules().getOffset(clock.instant()));
    }

    /**
     * Obtains the current date from the system clock in the default time-zone.
     * <p>
     * This will query the system clock in the default time-zone to obtain the current date - today.
     * The offset will be set based on the time-zone in the system clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock, never null
     */
    public static OffsetDate nowSystemClock() {
        return now(Clock.systemDefaultZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDate} from a year, month and day.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month-of-year, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return the offset date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDate of(int year, MonthOfYear monthOfYear, int dayOfMonth, ZoneOffset offset) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        return new OffsetDate(date, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDate} from a year, month and day.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param offset  the zone offset, not null
     * @return the offset date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static OffsetDate of(int year, int monthOfYear, int dayOfMonth, ZoneOffset offset) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        return new OffsetDate(date, offset);
    }

    /**
     * Obtains an instance of {@code OffsetDate} from a {@code DateProvider}.
     *
     * @param dateProvider  the date provider to use, not null
     * @param offset  the zone offset, not null
     * @return the offset date, never null
     */
    public static OffsetDate of(DateProvider dateProvider, ZoneOffset offset) {
        LocalDate date = LocalDate.of(dateProvider);
        return new OffsetDate(date, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDate} from an {@code InstantProvider}.
     * <p>
     * This conversion drops the time component of the instant.
     *
     * @param instantProvider  the instant to convert, not null
     * @param offset  the zone offset, not null
     * @return the offset date, never null
     * @throws CalendarConversionException if the instant exceeds the supported date range
     */
    public static OffsetDate ofInstant(InstantProvider instantProvider, ZoneOffset offset) {
        Instant instant = Instant.of(instantProvider);
        ISOChronology.checkNotNull(offset, "ZoneOffset must not be null");
        
        long epochSecs = instant.getEpochSeconds() + offset.getAmountSeconds();  // overflow caught later
        long yearZeroDays = MathUtils.floorDiv(epochSecs, ISOChronology.SECONDS_PER_DAY) + ISOChronology.DAYS_0000_TO_1970;
        LocalDate date = LocalDate.ofYearZeroDays(yearZeroDays);
        return new OffsetDate(date, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code OffsetDate} from a text string.
     * <p>
     * The following format is accepted in ASCII:
     * <ul>
     * <li>{@code {Year}-{MonthOfYear}-{DayOfMonth}{OffsetID}}
     * </ul>
     * The year has between 4 and 10 digits with values from MIN_YEAR to MAX_YEAR.
     * If there are more than 4 digits then the year must be prefixed with the plus symbol.
     * Negative years are allowed, but not negative zero.
     * <p>
     * The month-of-year has 2 digits with values from 1 to 12.
     * <p>
     * The day-of-month has 2 digits with values from 1 to 31 appropriate to the month.
     * <p>
     * The offset ID is the normalized form as defined in {@link ZoneOffset}.
     *
     * @param text  the text to parse such as '2007-12-03+01:00', not null
     * @return the parsed offset date, never null
     * @throws CalendricalException if the text cannot be parsed
     */
    public static OffsetDate parse(String text) {
        return DateTimeFormatters.isoOffsetDate().parse(text, rule());
    }

    /**
     * Obtains an instance of {@code OffsetDate} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a date.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed offset date, never null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalException if the text cannot be parsed
     */
    public static OffsetDate parse(String text, DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.parse(text, rule());
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

    /**
     * Returns a new date based on this one, returning {@code this} where possible.
     *
     * @param date  the date to create with, not null
     * @param offset  the zone offset to create with, not null
     */
    private OffsetDate with(LocalDate date, ZoneOffset offset) {
        if (this.date == date && this.offset.equals(offset)) {
            return this;
        }
        return new OffsetDate(date, offset);
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
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, this, this);
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
     * Returns a copy of this OffsetDate with a different zone offset.
     * <p>
     * This method changes the offset stored to a different offset.
     * No calculation is performed. The result simply represents the same
     * local date and the new offset.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetDate, never null
     */
    public OffsetDate withOffset(ZoneOffset offset) {
        ISOChronology.checkNotNull(offset, "ZoneOffset must not be null");
        return with(date, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     * <p>
     * Additional information about the year can be obtained via {@link #toYear}.
     * This returns a {@code Year} object which includes information on whether
     * this is a leap year and its length in days.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return date.getYear();
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
     * @return the month-of-year, never null
     */
    public MonthOfYear getMonthOfYear() {
        return date.getMonthOfYear();
    }

    /**
     * Gets the day-of-month field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 31
     */
    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    /**
     * Gets the day-of-year field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-year.
     *
     * @return the day-of-year, from 1 to 365, or 366 in a leap year
     */
    public int getDayOfYear() {
        return date.getDayOfYear();
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
     * @return the day-of-week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the date altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the date in various ways.
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date to the last day of the month.
     * <p>
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return an {@code OffsetDate} based on this date adjusted as necessary, never null
     * @throws NullPointerException if the adjuster returned null
     */
    public OffsetDate with(DateAdjuster adjuster) {
        return with(date.with(adjuster), offset);
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
     * @return an {@code OffsetDate} based on this date with the requested date, never null
     */
    public OffsetDate withDate(DateProvider dateProvider) {
        LocalDate newDate = LocalDate.of(dateProvider);
        if (newDate.equals(date)) {  // need .equals() for this case
            return this;
        }
        return with(newDate, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDate} with the year altered.
     * If the resulting date is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This method does the same as {@code withYear(year, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date, from MIN_YEAR to MAX_YEAR
     * @return an {@code OffsetDate} based on this date with the requested year, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public OffsetDate withYear(int year) {
        return with(date.withYear(year), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the year altered.
     * If the resulting date is invalid, it will be resolved using {@code dateResolver}.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date, from MIN_YEAR to MAX_YEAR
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return an {@code OffsetDate} based on this date with the requested year, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public OffsetDate withYear(int year, DateResolver dateResolver) {
        return with(date.withYear(year, dateResolver), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the month-of-year altered.
     * If the resulting date is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This method does the same as {@code withMonthOfYear(monthOfYear, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, from 1 (January) to 12 (December)
     * @return an {@code OffsetDate} based on this date with the requested month, never null
     * @throws IllegalCalendarFieldValueException if the month-of-year value is invalid
     */
    public OffsetDate withMonthOfYear(int monthOfYear) {
        return with(date.withMonthOfYear(monthOfYear), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the month-of-year altered.
     * If the resulting date is invalid, it will be resolved using {@code dateResolver}.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, from 1 (January) to 12 (December)
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return an {@code OffsetDate} based on this date with the requested month, never null
     * @throws IllegalCalendarFieldValueException if the month-of-year value is invalid
     */
    public OffsetDate withMonthOfYear(int monthOfYear, DateResolver dateResolver) {
        return with(date.withMonthOfYear(monthOfYear, dateResolver), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the month-of-year altered.
     * If the resulting date is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This method does the same as {@code with(monthOfYear, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, not null
     * @return an {@code OffsetDate} based on this date with the requested month, never null
     */
    public OffsetDate with(MonthOfYear monthOfYear) {
        return with(date.with(monthOfYear), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the month-of-year altered.
     * If the resulting date is invalid, it will be resolved using {@code dateResolver}.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, not null
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return an {@code OffsetDate} based on this date with the requested month, never null
     */
    public OffsetDate with(MonthOfYear monthOfYear, DateResolver dateResolver) {
        return with(date.with(monthOfYear, dateResolver), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the day-of-month altered.
     * If the resulting date is invalid, an exception is thrown.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 28-31
     * @return an {@code OffsetDate} based on this date with the requested day, never null
     * @throws IllegalCalendarFieldValueException if the day-of-month value is invalid
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public OffsetDate withDayOfMonth(int dayOfMonth) {
        return with(date.withDayOfMonth(dayOfMonth), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the day-of-month altered.
     * If the resulting date is invalid, it will be resolved using {@code dateResolver}.
     * The offset does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 31
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return an {@code OffsetDate} based on this date with the requested day, never null
     * @throws IllegalCalendarFieldValueException if the day-of-month value is invalid
     */
    public OffsetDate withDayOfMonth(int dayOfMonth, DateResolver dateResolver) {
        return with(date.withDayOfMonth(dayOfMonth, dateResolver), offset);
    }

    /**
     * Returns a copy of this {@code OffsetDate} with the day-of-year altered.
     * If the resulting date is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return an {@code OffsetDate} based on this date with the requested day, never null
     * @throws IllegalCalendarFieldValueException if the day-of-year value is invalid
     * @throws InvalidCalendarFieldException if the day-of-year is invalid for the year
     */
    public OffsetDate withDayOfYear(int dayOfYear) {
        return with(date.withDayOfYear(dayOfYear), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDate} with the specified date period added.
     * <p>
     * This adds the specified period to this date, returning a new date.
     * Before addition, the period is converted to a date-based {@code Period} using
     * {@link Period#ofDateFields(PeriodProvider)}.
     * That factory ignores any time-based ISO fields, thus adding a time-based
     * period to this date will have no effect. If you want to take time fields into
     * account, call {@link Period#normalizedWith24HourDays()} on the input period.
     * <p>
     * The detailed rules for the addition have some complexity due to variable length months.
     * See {@link LocalDate#plus(PeriodProvider)} for details.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return an {@code OffsetDate} based on this date with the period added, never null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plus(PeriodProvider periodProvider) {
        return with(date.plus(periodProvider), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the specified period in years added.
     * <p>
     * This method add the specified amount to the years field in three steps:
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
     * This method does the same as {@code plusYears(years, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return an {@code OffsetDate} based on this date with the years added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusYears(int, javax.time.calendar.DateResolver)
     */
    public OffsetDate plusYears(int years) {
        return with(date.plusYears(years), offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in years added.
     * <p>
     * This method add the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using {@code dateResolver} if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return an {@code OffsetDate} based on this date with the years added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusYears(int years, DateResolver dateResolver) {
        return with(date.plusYears(years, dateResolver), offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in months added.
     * <p>
     * This method add the specified amount to the months field in three steps:
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
     * This method does the same as {@code plusMonths(months, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return an {@code OffsetDate} based on this date with the months added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusMonths(int, javax.time.calendar.DateResolver)
     */
    public OffsetDate plusMonths(int months) {
        return with(date.plusMonths(months), offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in months added.
     * <p>
     * This method add the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using {@code dateResolver} if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return an {@code OffsetDate} based on this date with the months added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusMonths(int months, DateResolver dateResolver) {
        return with(date.plusMonths(months, dateResolver), offset);
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
     * @return an {@code OffsetDate} based on this date with the weeks added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusWeeks(int weeks) {
        return with(date.plusWeeks(weeks), offset);
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
     * @return an {@code OffsetDate} based on this date with the days added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate plusDays(long days) {
        return with(date.plusDays(days), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code OffsetDate} with the specified date period subtracted.
     * <p>
     * This subtracts the specified period from this date, returning a new date.
     * Before subtraction, the period is converted to a date-based {@code Period} using
     * {@link Period#ofDateFields(PeriodProvider)}.
     * That factory ignores any time-based ISO fields, thus adding a time-based
     * period to this date will have no effect. If you want to take time fields into
     * account, call {@link Period#normalizedWith24HourDays()} on the input period.
     * <p>
     * The detailed rules for the subtraction have some complexity due to variable length months.
     * See {@link LocalDate#minus(PeriodProvider)} for details.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return an {@code OffsetDate} based on this date with the period subtracted, never null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minus(PeriodProvider periodProvider) {
        return with(date.minus(periodProvider), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDate with the specified period in years subtracted.
     * <p>
     * This method subtract the specified amount to the years field in three steps:
     * <ol>
     * <li>Subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2007-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2007-02-28, is selected instead.
     * <p>
     * This method does the same as {@code minusYears(years, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return an {@code OffsetDate} based on this date with the years subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusYears(int, javax.time.calendar.DateResolver)
     */
    public OffsetDate minusYears(int years) {
        return with(date.minusYears(years), offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in years subtracted.
     * <p>
     * This method subtract the specified amount to the years field in three steps:
     * <ol>
     * <li>Subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using {@code dateResolver} if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return an {@code OffsetDate} based on this date with the years subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusYears(int years, DateResolver dateResolver) {
        return with(date.minusYears(years, dateResolver), offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in months subtracted.
     * <p>
     * This method subtract the specified amount to the months field in three steps:
     * <ol>
     * <li>Subtract the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-02-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-02-28, is selected instead.
     * <p>
     * This method does the same as {@code minusMonths(months, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return an {@code OffsetDate} based on this date with the months subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusMonths(int, javax.time.calendar.DateResolver)
     */
    public OffsetDate minusMonths(int months) {
        return with(date.minusMonths(months), offset);
    }

    /**
     * Returns a copy of this OffsetDate with the specified period in months subtracted.
     * <p>
     * This method subtract the specified amount to the months field in three steps:
     * <ol>
     * <li>Subtract the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the date using {@code dateResolver} if necessary</li>
     * </ol>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return an {@code OffsetDate} based on this date with the months subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusMonths(int months, DateResolver dateResolver) {
        return with(date.minusMonths(months, dateResolver), offset);
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
     * @return an {@code OffsetDate} based on this date with the weeks subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusWeeks(int weeks) {
        return with(date.minusWeeks(weeks), offset);
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
     * @return an {@code OffsetDate} based on this date with the days subtracted, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public OffsetDate minusDays(long days) {
        return with(date.minusDays(days), offset);
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
     * This merges the two objects - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to use, not null
     * @return the offset date-time formed from this date and the specified time, never null
     */
    public OffsetDateTime atTime(LocalTime time) {
        return OffsetDateTime.of(this, time, getOffset());
    }

    /**
     * Returns a offset date-time formed from this date at the specified time.
     * <p>
     * This merges the three values - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to use, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to use, from 0 to 59
     * @return the offset date-time formed from this date and the specified time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public OffsetDateTime atTime(int hourOfDay, int minuteOfHour) {
        return atTime(LocalTime.of(hourOfDay, minuteOfHour));
    }

    /**
     * Returns a offset date-time formed from this date at the specified time.
     * <p>
     * This merges the four values - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to use, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to use, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return the offset date-time formed from this date and the specified time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public OffsetDateTime atTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return atTime(LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute));
    }

    /**
     * Returns a offset date-time formed from this date at the specified time.
     * <p>
     * This merges the five values - {@code this} and the specified time -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to use, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to use, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return the offset date-time formed from this date and the specified time, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     */
    public OffsetDateTime atTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        return atTime(LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond));
    }

    /**
     * Returns an offset date-time formed from this date at the time of midnight.
     * <p>
     * This merges the two objects - {@code this} and {@link LocalTime#MIDNIGHT} -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return the offset date-time formed from this date and the time of midnight, never null
     */
    public OffsetDateTime atMidnight() {
        return OffsetDateTime.of(this, LocalTime.MIDNIGHT, getOffset());
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
     * followed by {@link OffsetDateTime#atZoneSimilarLocal(TimeZone)}. Note that the resolver
     * used by {@code atZoneSimilarLocal()} is different to that used here (it chooses
     * the later offset in an overlap, whereas this method chooses the earlier offset).
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
        return ZonedDateTime.of(this, LocalTime.MIDNIGHT, zone, ZoneResolvers.postGapPreOverlap());
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to a {@code LocalDate}.
     *
     * @return a LocalDate with the same date as this instance, never null
     */
    public LocalDate toLocalDate() {
        return date;
    }

    /**
     * Gets the year field as a {@code Year}.
     * <p>
     * This method provides access to an object representing the year field.
     * {@code Year} has methods for querying addition year-based information.
     *
     * @return the year, never null
     */
    public Year toYear() {
        return date.toYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date based on the UTC equivalent dates
     * then local date.
     * <p>
     * This ordering is consistent with {@code equals()}.
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
     * consistent with {@code equals()}.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if {@code other} is null
     */
    public int compareTo(OffsetDate other) {
        if (offset.equals(other.offset)) {
            return date.compareTo(other.date);
        }
        LocalDateTime thisDT = LocalDateTime.ofMidnight(getYear(), getMonthOfYear(), getDayOfMonth());
        LocalDateTime otherDT = LocalDateTime.ofMidnight(other.getYear(), other.getMonthOfYear(), other.getDayOfMonth());
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
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isAfter(OffsetDate other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date before the specified date.
     *
     * @param other  the other date to compare to, not null
     * @return true if this point is before the specified date
     * @throws NullPointerException if {@code other} is null
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
     * Outputs this date as a {@code String}, such as {@code 2007-12-03+01:00}.
     * <p>
     * The output will be in the format {@code yyyy-MM-ddZZZZ}.
     *
     * @return the formatted date, never null
     */
    @Override
    public String toString() {
        return date.toString() + offset.toString();
    }

    /**
     * Outputs this date as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted date string, never null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalPrintException if an error occurs during printing
     */
    public String toString(DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.print(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field rule for {@code OffsetDate}.
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
            super(OffsetDate.class, ISOChronology.INSTANCE, "OffsetDate", ISOChronology.periodDays(), null);
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
