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
import javax.time.MathUtils;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.calendar.format.DateTimeFormatters;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

/**
 * A date without a time zone in the ISO-8601 calendar system,
 * such as '2007-12-03'.
 * <p>
 * LocalDate is an immutable calendrical that represents a date, often viewed
 * as year-month-day. This object can also access other date fields such as
 * day of year, day of week and week of year.
 * <p>
 * This class does not store or represent a time or time zone.
 * Thus, for example, the value "2nd October 2007" can be stored in a LocalDate.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which todays's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * <p>
 * However, any application that makes use of historical dates and requires them
 * to be accurate will find the ISO-8601 rules unsuitable. In this case, the
 * application code should use <code>HistoricDate</code> and define an explicit
 * cutover date between the Julian and Gregorian calendar systems.
 * <p>
 * LocalDate is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class LocalDate
        implements Calendrical, DateProvider, DateMatcher, DateAdjuster, Comparable<LocalDate>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 798274969L;

    /**
     * The year.
     */
    private final int year;
    /**
     * The month of year, not null.
     */
    private final MonthOfYear month;
    /**
     * The day of month.
     */
    private final int day;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalDate</code> from a year, month and day.
     *
     * @param year  the year to represent, not null
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, not null
     * @return the local date, never null
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDate date(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
        ISOChronology.checkNotNull(year, "Year must not be null");
        ISOChronology.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        ISOChronology.checkNotNull(dayOfMonth, "DayOfMonth must not be null");
        return create(year.getValue(), monthOfYear, dayOfMonth.getValue());
    }

    /**
     * Obtains an instance of <code>LocalDate</code> from a year, month and day.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return the local date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDate date(int year, MonthOfYear monthOfYear, int dayOfMonth) {
        ISOChronology.yearRule().checkValue(year);
        ISOChronology.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        ISOChronology.dayOfMonthRule().checkValue(dayOfMonth);
        return create(year, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>LocalDate</code> from a year, month and day.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return the local date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDate date(int year, int monthOfYear, int dayOfMonth) {
        ISOChronology.yearRule().checkValue(year);
        ISOChronology.monthOfYearRule().checkValue(monthOfYear);
        ISOChronology.dayOfMonthRule().checkValue(dayOfMonth);
        return create(year, MonthOfYear.monthOfYear(monthOfYear), dayOfMonth);
    }

    /**
     * Obtains an instance of <code>LocalDate</code> from a date provider.
     * <p>
     * The purpose of this method is to convert a <code>DateProvider</code>
     * to a <code>LocalDate</code> in the safest possible way. Specifically,
     * the means checking whether the input parameter is null and
     * whether the result of the provider is null.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the local date, never null
     * @throws NullPointerException if the provider is null or returns null
     */
    public static LocalDate date(DateProvider dateProvider) {
        ISOChronology.checkNotNull(dateProvider, "DateProvider must not be null");
        LocalDate result = dateProvider.toLocalDate();
        ISOChronology.checkNotNull(result, "DateProvider implementation must not return null");
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts an Epoch Day count to a date.
     * <p>
     * The Epoch Day count is a simple incrementing count of days
     * where day 0 is 1970-01-01.
     *
     * @param epochDays  the Epoch Day to convert, based on the epoch 1970-01-01
     * @return the local date, never null
     * @throws IllegalCalendarFieldValueException if the epoch days exceeds the supported date range
     */
    public static LocalDate fromEpochDays(long epochDays) {
        return fromYearZeroDays(epochDays + ISOChronology.DAYS_0000_TO_1970);
    }

    /**
     * Converts a Modified Julian Day (MJD) count to a date.
     * <p>
     * The Modified Julian Day count is a simple incrementing count of days
     * where day 0 is 1858-11-17.
     *
     * @param mjDays  the Modified Julian Day to convert, based on the epoch 1858-11-17
     * @return the local date, never null
     * @throws IllegalCalendarFieldValueException if the modified julian days value is outside the supported range
     */
    public static LocalDate fromModifiedJulianDays(long mjDays) {
        return fromYearZeroDays(mjDays + ISOChronology.DAYS_0000_TO_MJD_EPOCH);
    }

    /**
     * Converts a year zero day count to a date.
     * <p>
     * The year zero day count is a simple incrementing count of days
     * where day 0 is 0000-01-01.
     *
     * @param epochDays  the Epoch Day to convert, based on the epoch 1970-01-01
     * @return the local date, never null
     * @throws IllegalCalendarFieldValueException if the epoch days exceeds the supported date range
     */
    static LocalDate fromYearZeroDays(long epochDays) {
        // find the march-based year
        epochDays -= 60;  // adjust to 0000-03-01 so leap day is at end of four year cycle
        long adjust = 0;
        if (epochDays < 0) {
            // adjust negative years to positive for calculation
            long adjustCycles = (epochDays + 1) / ISOChronology.DAYS_PER_CYCLE - 1;
            adjust = adjustCycles * 400;
            epochDays += -adjustCycles * ISOChronology.DAYS_PER_CYCLE;
        }
        long yearEst = (400 * epochDays + 591) / ISOChronology.DAYS_PER_CYCLE;
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
        int year = ISOChronology.yearRule().checkValue(yearEst);
        return new LocalDate(year, MonthOfYear.monthOfYear(month), dom);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>LocalDate</code> from a text string.
     * <p>
     * The following format is accepted in ASCII:
     * <ul>
     * <li><code>{Year}-{MonthOfYear}-{DayOfMonth}</code>
     * </ul>
     * The year has between 4 and 10 digits with values from MIN_YEAR to MAX_YEAR.
     * If there are more than 4 digits then the year must be prefixed with the plus symbol.
     * Negative years are allowed, but not negative zero.
     * <p>
     * The month of year has 2 digits with values from 1 to 12.
     * <p>
     * The day of month has 2 digits with values from 1 to 31 appropriate to the month.
     *
     * @param text  the text to parse such as '2007-12-03', not null
     * @return the parsed local date, never null
     * @throws CalendricalParseException if the text cannot be parsed
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public static LocalDate parse(String text) {
        return DateTimeFormatters.isoLocalDate().parse(text, rule());
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a local date from the year, month and day fields.
     *
     * @param year  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, validated not null
     * @param dayOfMonth  the day of month to represent, validated from 1 to 31
     * @return the local date, never null
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    private static LocalDate create(int year, MonthOfYear monthOfYear, int dayOfMonth) {
        if (dayOfMonth > monthOfYear.lengthInDays(year)) {
            if (dayOfMonth == 29) {
                throw new InvalidCalendarFieldException("Illegal value for DayOfMonth field, value 29 is not valid as " +
                        year + " is not a leap year", ISOChronology.dayOfMonthRule());
            } else {
                throw new InvalidCalendarFieldException("Illegal value for DayOfMonth field, value " + dayOfMonth +
                        " is not valid for month " + monthOfYear.name(), ISOChronology.dayOfMonthRule());
            }
        }
        return new LocalDate(year, monthOfYear, dayOfMonth);
    }

    /**
     * Constructor, previously validated.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, not null
     * @param dayOfMonth  the day of month to represent, valid for year-month, from 1 to 31
     */
    private LocalDate(int year, MonthOfYear monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
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
     * Gets the year field as a <code>Year</code>.
     * <p>
     * This method provides access to an object representing the year field.
     * This allows operations to be performed on this field in a type-safe manner.
     *
     * @return the year, never null
     */
    public Year toYear() {
        return Year.isoYear(year);
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
        return month;
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
        return DayOfMonth.dayOfMonth(day);
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
        return DayOfYear.dayOfYear(this);
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
        return DayOfWeek.dayOfWeek(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive <code>int</code> value for the year.
     * <p>
     * Additional information about the year can be obtained from via {@link #toYear()}.
     * This returns a <code>Year</code> object which includes information on whether
     * this is a leap year and its length in days. It can also be used as a {@link DateMatcher}
     * and a {@link DateAdjuster}.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return year;
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
        return month;
    }

    /**
     * Gets the day of month field.
     * <p>
     * This method returns the primitive <code>int</code> value for the day of month.
     * <p>
     * Additional information about the day of month can be obtained from via {@link #toDayOfMonth()}.
     * This returns a <code>DayOfMonth</code> object which can be used as a {@link DateMatcher}
     * and a {@link DateAdjuster}.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return day;
    }

    /**
     * Gets the day of year field.
     * <p>
     * This method returns the primitive <code>int</code> value for the day of year.
     * <p>
     * Additional information about the day of year can be obtained from via {@link #toDayOfYear()}.
     * This returns a <code>DayOfYear</code> object which can be used as a {@link DateMatcher}
     * and a {@link DateAdjuster}.
     *
     * @return the day of year, from 1 to 365, or 366 in a leap year
     */
    public int getDayOfYear() {
        return DayOfYear.dayOfYear(this).getValue();  // TODO: inline for performance? move code to chrono?
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
        return DayOfWeek.dayOfWeek(this);
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Checks if the year is a leap year, according to the ISO proleptic
//     * calendar system rules.
//     * <p>
//     * This method applies the current rules for leap years across the whole time-line.
//     * In general, a year is a leap year if it is divisible by four without
//     * remainder. However, years divisible by 100, are not leap years, with
//     * the exception of years divisible by 400 which are.
//     * <p>
//     * For example, 1904 is a leap year it is divisible by 4.
//     * 1900 was not a leap year as it is divisible by 100, however 2000 was a
//     * leap year as it is divisible by 400.
//     * <p>
//     * The calculation is proleptic - applying the same rules into the far future and far past.
//     * This is historically inaccurate, but is correct for the ISO8601 standard.
//     *
//     * @return true if the year is leap, false otherwise
//     */
//    public boolean isLeapYear() {
//        return ISOChronology.isLeapYear(year);
//    }

    //-----------------------------------------------------------------------
    /**
     * Resolves the date, handling incorrectly implemented resolvers.
     *
     * @param dateResolver  the resolver, not null
     * @param year  the year, not null
     * @param month  the month, not null
     * @param day  the day of month, not null
     * @return the resolved date, never null
     * @throws NullPointerException if the resolver returned null
     */
    private LocalDate resolveDate(DateResolver dateResolver, int year, MonthOfYear month, int day) {
        LocalDate date = dateResolver.resolveDate(Year.isoYear(year), month, DayOfMonth.dayOfMonth(day));
        ISOChronology.checkNotNull(date, "DateResolver implementation must not return null");
        return date;
    }

    /**
     * Returns a copy of this LocalDate with the date altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the date in unusual ways. Examples might
     * be an adjuster that set the date avoiding weekends, or one that sets the
     * date to the last day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a new updated LocalDate, never null
     * @throws NullPointerException if the adjuster returned null
     */
    public LocalDate with(DateAdjuster adjuster) {
        ISOChronology.checkNotNull(adjuster, "DateAdjuster must not be null");
        LocalDate date = adjuster.adjustDate(this);
        ISOChronology.checkNotNull(date, "DateAdjuster implementation must not return null");
        return date;
    }

    /**
     * Returns a copy of this LocalDate with the year value altered.
     * If the resulting <code>LocalDate</code> is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * <p>
     * This method does the same as <code>withYear(year, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated LocalDate, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     * @see #withYear(int,DateResolver)
     */
    public LocalDate withYear(int year) {
        return withYear(year, DateResolvers.previousValid());
    }

    /**
     * Returns a copy of this LocalDate with the year value altered.
     * If the resulting <code>LocalDate</code> is invalid, it will be resolved using <code>dateResolver</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated LocalDate, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public LocalDate withYear(int year, DateResolver dateResolver) {
        ISOChronology.checkNotNull(dateResolver, "DateResolver must not be null");
        if (this.year == year) {
            return this;
        }
        return resolveDate(dateResolver, year, month, day);
    }

    /**
     * Returns a copy of this LocalDate with the month of year value altered.
     * If the resulting <code>LocalDate</code> is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * <p>
     * This method does the same as <code>withMonthOfYear(monthOfYear, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated LocalDate, never null
     * @throws IllegalCalendarFieldValueException if the month of year value is invalid
     * @see #withMonthOfYear(int,DateResolver)
     */
    public LocalDate withMonthOfYear(int monthOfYear) {
        return withMonthOfYear(monthOfYear, DateResolvers.previousValid());
    }

    /**
     * Returns a copy of this LocalDate with the month of year value altered.
     * If the resulting <code>LocalDate</code> is invalid, it will be resolved using <code>dateResolver</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated LocalDate, never null
     * @throws IllegalCalendarFieldValueException if the month of year value is invalid
     */
    public LocalDate withMonthOfYear(int monthOfYear, DateResolver dateResolver) {
        ISOChronology.checkNotNull(dateResolver, "DateResolver must not be null");
        if (this.month.getValue() == monthOfYear) {
            return this;
        }
        return resolveDate(dateResolver, year, MonthOfYear.monthOfYear(monthOfYear), day);
    }

    /**
     * Returns a copy of this LocalDate with the day of month value altered.
     * If the resulting <code>LocalDate</code> is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 28-31
     * @return a new updated LocalDate, never null
     * @throws IllegalCalendarFieldValueException if the day of month value is invalid
     * @throws InvalidCalendarFieldException if the day of month is invalid for the month-year
     */
    public LocalDate withDayOfMonth(int dayOfMonth) {
        if (this.day == dayOfMonth) {
            return this;
        }
        return date(year, month, dayOfMonth);
    }

    /**
     * Returns a copy of this LocalDate with the month of year value altered.
     * If the resulting <code>LocalDate</code> is invalid, it will be resolved using <code>dateResolver</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a new updated LocalDate, never null
     * @throws IllegalCalendarFieldValueException if the day of month value is invalid
     */
    public LocalDate withDayOfMonth(int dayOfMonth, DateResolver dateResolver) {
        ISOChronology.checkNotNull(dateResolver, "DateResolver must not be null");
        if (this.day == dayOfMonth) {
            return this;
        }
        return resolveDate(dateResolver, year, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDate with the specified period added.
     * <p>
     * This adds the amount in years, months and days from the specified period to this date.
     * Any time amounts, such as hours, minutes or seconds are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate plus(PeriodProvider periodProvider) {
        Period period = Period.period(periodProvider);
        return plusYears(period.getYears()).plusMonths(period.getMonths()).plusDays(period.getDays());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDate with the specified period in years added.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusYears(int, javax.time.calendar.DateResolver)
     */
    public LocalDate plusYears(int years) {
        return plusYears(years, DateResolvers.previousValid());
    }

    /**
     * Returns a copy of this LocalDate with the specified period in years added.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate plusYears(int years, DateResolver dateResolver) {
        ISOChronology.checkNotNull(dateResolver, "DateResolver must not be null");
        if (years == 0) {
            return this;
        }
        int newYear = ISOChronology.addYears(year, years);
        return resolveDate(dateResolver, newYear, month, day);
    }

    /**
     * Returns a copy of this LocalDate with the specified period in months added.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusMonths(int, javax.time.calendar.DateResolver)
     */
    public LocalDate plusMonths(int months) {
        return plusMonths(months, DateResolvers.previousValid());
    }

    /**
     * Returns a copy of this LocalDate with the specified period in months added.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate plusMonths(int months, DateResolver dateResolver) {
        ISOChronology.checkNotNull(dateResolver, "DateResolver must not be null");
        if (months == 0) {
            return this;
        }
        long newMonth0 = month.getValue() - 1;
        newMonth0 = newMonth0 + months;
        int years = (int) (newMonth0 / 12);
        newMonth0 = newMonth0 % 12;
        if (newMonth0 < 0) {
            newMonth0 += 12;
            years--;
        }
        int newYear = ISOChronology.addYears(year, years);
        MonthOfYear newMonth = MonthOfYear.monthOfYear((int) ++newMonth0);
        return resolveDate(dateResolver, newYear, newMonth, day);
    }

    /**
     * Returns a copy of this LocalDate with the specified period in weeks added.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate plusWeeks(int weeks) {
        return plusDays(7L * weeks);
    }

    /**
     * Returns a copy of this LocalDate with the specified number of days added.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate plusDays(long days) {
        if (days == 0) {
            return this;
        }

        long mjDays = toModifiedJulianDays();

        try {
            mjDays = MathUtils.safeAdd(mjDays, days);
        } catch (ArithmeticException ae) {
            throw new CalendricalException(this + " + " + days + " days exceeds the current capacity");
        }

        return LocalDate.fromModifiedJulianDays(mjDays);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDate with the specified period subtracted.
     * <p>
     * This subtracts the amount in years, months and days from the specified period from this date.
     * Any time amounts, such as hours, minutes or seconds are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate minus(PeriodProvider periodProvider) {
        Period period = Period.period(periodProvider);
        return minusYears(period.getYears()).minusMonths(period.getMonths()).minusDays(period.getDays());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this LocalDate with the specified period in years subtracted.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusYears(int, javax.time.calendar.DateResolver)
     */
    public LocalDate minusYears(int years) {
        return minusYears(years, DateResolvers.previousValid());
    }

    /**
     * Returns a copy of this LocalDate with the specified period in years subtracted.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate minusYears(int years, DateResolver dateResolver) {
        ISOChronology.checkNotNull(dateResolver, "DateResolver must not be null");
        if (years == 0) {
            return this;
        }
        int newYear = ISOChronology.subtractYears(year, years);
        return resolveDate(dateResolver, newYear, month, day);
    }

    /**
     * Returns a copy of this LocalDate with the specified period in months subtracted.
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
     * This method does the same as <code>minusMonts(months, DateResolvers.previousValid())</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusMonths(int, javax.time.calendar.DateResolver)
     */
    public LocalDate minusMonths(int months) {
        return minusMonths(months, DateResolvers.previousValid());
    }

    /**
     * Returns a copy of this LocalDate with the specified period in months subtracted.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate minusMonths(int months, DateResolver dateResolver) {
        ISOChronology.checkNotNull(dateResolver, "DateResolver must not be null");
        if (months == 0) {
            return this;
        }
        long newMonth0 = month.getValue() - 1;
        newMonth0 = newMonth0 - months;
        int years = (int) (newMonth0 / 12);
        newMonth0 = newMonth0 % 12;
        if (newMonth0 < 0) {
            newMonth0 += 12;
            years--;
        }
        int newYear = ISOChronology.subtractYears(year, -years);
        MonthOfYear newMonth = MonthOfYear.monthOfYear((int) ++newMonth0);
        return resolveDate(dateResolver, newYear, newMonth, day);
    }

    /**
     * Returns a copy of this LocalDate with the specified period in weeks subtracted.
     * <p>
     * This method subtract the specified amount in weeks to the days field decrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-07 minus one week would result in the 2008-12-31.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate minusWeeks(int weeks) {
        return minusDays(7L * weeks);
    }

    /**
     * Returns a copy of this LocalDate with the specified number of days subtracted.
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
     * @return a new updated LocalDate, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDate minusDays(long days) {
        if (days == 0) {
            return this;
        }

        long mjDays = toModifiedJulianDays();

        try {
            mjDays = MathUtils.safeSubtract(mjDays, days);
        } catch (ArithmeticException ae) {
            throw new CalendricalException(this + " - " + days + " days exceeds the current capacity");
        }

        return LocalDate.fromModifiedJulianDays(mjDays);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this date matches the specified matcher.
     * <p>
     * Matchers can be used to query the date in unusual ways. Examples might
     * be a matcher that checks if the date is a weekend or holiday, or
     * Friday the Thirteenth.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this date matches the matcher, false otherwise
     */
    public boolean matches(DateMatcher matcher) {
        return matcher.matchesDate(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to the input date
     *
     * @param date  the date to match, not null
     * @return true if the two dates are equal, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return (year == date.year && month == date.month && day == date.day);
    }

    /**
     * Adjusts a date to have the value of this date.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     */
    public LocalDate adjustDate(LocalDate date) {
        return matchesDate(date) ? date : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a local date-time formed from this date at the specified time.
     * <p>
     * This merges the two objects - <code>this</code> and the specified time -
     * to form an instance of <code>LocalDateTime</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to use, not null
     * @return the local date-time formed from this date and the specified time, never null
     */
    public LocalDateTime atTime(LocalTime time) {
        return LocalDateTime.dateTime(this, time);
    }

    /**
     * Returns a local date-time formed from this date at the time of midnight.
     * <p>
     * This merges the two objects - <code>this</code> and {@link LocalTime#MIDNIGHT} -
     * to form an instance of <code>LocalDateTime</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return the local date-time formed from this date and the time of midnight, never null
     */
    public LocalDateTime atMidnight() {
        return LocalDateTime.dateTime(this, LocalTime.MIDNIGHT);
    }

    /**
     * Returns an offset date formed from this time and the specified offset.
     * <p>
     * This merges the two objects - <code>this</code> and the specified offset -
     * to form an instance of <code>OffsetDate</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the offset to use, not null
     * @return the offset date formed from this date and the specified offset, never null
     */
    public OffsetDate atOffset(ZoneOffset offset) {
        return OffsetDate.date(this, offset);
    }

    /**
     * Returns a zoned date-time from this date at the earliest valid time according
     * to the rules in the time-zone.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. When this method converts the date to a date-time it
     * adjusts the time and offset as necessary to ensure that the time is as early
     * as possible on the date, which is typically midnight. Internally this is
     * achieved using a {@link ZoneResolvers#postGapPreOverlap() zone resolver}.
     * <p>
     * To convert to a specific time in a given time-zone call {@link #atTime(LocalTime)}
     * followed by {@link LocalDateTime#atZone(TimeZone)}. Note that the resolver used
     * by <code>atZone()</code> is different to that used here (it chooses the later
     * offset in an overlap, whereas this method chooses the earlier offset).
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
//    /**
//     * Converts this date to a <code>DateTimeFields</code> containing the
//     * year, month of year and day of month fields.
//     *
//     * @return the field set, never null
//     */
//    public DateTimeFields toDateTimeFields() {
//        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
//        map.put(ISOChronology.yearRule(), year);
//        map.put(ISOChronology.monthOfYearRule(), month.getValue());
//        map.put(ISOChronology.dayOfMonthRule(), day);
//        return DateTimeFields.fields(map);
//    }

    /**
     * Converts this date to a <code>LocalDate</code>, trivially
     * returning <code>this</code>.
     *
     * @return <code>this</code>, never null
     */
    public LocalDate toLocalDate() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts the date to Epoch Days.
     * <p>
     * The Epoch Day count is a simple incrementing count of days
     * where day 0 is 1970-01-01.
     *
     * @return the Modified Julian Day equivalent to this date
     */
    public long toEpochDays() {
        return toYearZeroDays() - ISOChronology.DAYS_0000_TO_1970;
    }

    /**
     * Converts the date to Modified Julian Days (MJD).
     * <p>
     * The Modified Julian Day count is a simple incrementing count of days
     * where day 0 is 1858-11-17.
     *
     * @return the Modified Julian Day equivalent to this date
     */
    public long toModifiedJulianDays() {
        return toYearZeroDays() - ISOChronology.DAYS_0000_TO_MJD_EPOCH;
    }

    /**
     * Converts the date to year zero days.
     * <p>
     * The year zero day count is a simple incrementing count of days
     * where day 0 is 0000-01-01.
     *
     * @return the year zero days count equal to this date
     */
    long toYearZeroDays() {
        long y = year;
        long m = month.getValue();
        long total = 0;
        total += 365 * y;
        if (y >= 0) {
            total += (y + 3) / 4 - (y + 99) / 100 + (y + 399) / 400;
        } else {
            total -= y / -4 - y / -100 + y / -400;
        }
        total += ((367 * m - 362) / 12);
        total += day - 1;
        if (m > 2) {
            total--;
            if (ISOChronology.isLeapYear(year) == false) {
                total--;
            }
        }
        return total;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(LocalDate other) {
        int cmp = MathUtils.safeCompare(year, other.year);
        if (cmp == 0) {
            cmp = month.compareTo(other.month);
            if (cmp == 0) {
                cmp = MathUtils.safeCompare(day, other.day);
            }
        }
        return cmp;
    }

    /**
     * Is this date after the specified date.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the specified date
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(LocalDate other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date before the specified date.
     *
     * @param other  the other date to compare to, not null
     * @return true if this point is before the specified date
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(LocalDate other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this date equal to the specified date.
     *
     * @param other  the other date to compare to, null returns false
     * @return true if this point is equal to the specified date
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LocalDate) {
            LocalDate otherDate = (LocalDate) other;
            return matchesDate(otherDate);
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
        int yearValue = year;
        int monthValue = month.getValue();
        int dayValue = day;
        return (yearValue & 0xFFFFF800) ^ ((yearValue << 11) + (monthValue << 6) + (dayValue));
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the date as a <code>String</code>, such as '2007-12-03'.
     * <p>
     * The output will be in the format 'yyyy-MM-dd'.
     *
     * @return the formatted date string, never null
     */
    @Override
    public String toString() {
        int yearValue = year;
        int monthValue = month.getValue();
        int dayValue = day;
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(10);
        if (absYear < 1000) {
            if (yearValue < 0) {
                buf.append(yearValue - 10000).deleteCharAt(1);
            } else {
                buf.append(yearValue + 10000).deleteCharAt(0);
            }
        } else {
            if (yearValue > 9999) {
                buf.append('+');
            }
            buf.append(yearValue);
        }
        return buf.append(monthValue < 10 ? "-0" : "-")
            .append(monthValue)
            .append(dayValue < 10 ? "-0" : "-")
            .append(dayValue)
            .toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field rule for <code>LocalDate</code>.
     *
     * @return the field rule for the date, never null
     */
    public static CalendricalRule<LocalDate> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<LocalDate> implements Serializable {
        private static final CalendricalRule<LocalDate> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(LocalDate.class, ISOChronology.INSTANCE, "LocalDate");
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected LocalDate deriveValue(Calendrical calendrical) {
            LocalDateTime ldt = calendrical.get(LocalDateTime.rule());
            if (ldt != null) {
                return ldt.toLocalDate();
            }
            OffsetDate od = calendrical.get(OffsetDate.rule());
            if (od != null) {
                return od.toLocalDate();
            }
            return null;
        }
    }

}
