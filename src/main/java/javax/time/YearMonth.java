/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import static javax.time.calendrical.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendrical.ISODateTimeRule.YEAR;

import java.io.Serializable;

import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.DateAdjuster;
import javax.time.calendrical.DateResolvers;
import javax.time.calendrical.DateTimeFields;
import javax.time.calendrical.ISOChronology;
import javax.time.calendrical.ISODateTimeRule;
import javax.time.calendrical.IllegalCalendarFieldValueException;
import javax.time.calendrical.InvalidCalendarFieldException;
import javax.time.calendrical.PeriodProvider;
import javax.time.format.CalendricalParseException;
import javax.time.format.DateTimeFormatter;
import javax.time.format.DateTimeFormatterBuilder;
import javax.time.format.SignStyle;

/**
 * A year-month in the ISO-8601 calendar system, such as {@code 2007-12}.
 * <p>
 * {@code YearMonth} is an immutable calendrical that represents the combination
 * of a year and month. Any field that can be derived from a year and month, such as
 * quarter-of-year, can be obtained.
 * <p>
 * This class does not store or represent a day, time or time-zone.
 * For example, the value "October 2007" can be stored in a {@code YearMonth}.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which todays's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * Any application that uses historical dates should consider using {@code HistoricDate}.
 * <p>
 * YearMonth is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class YearMonth
        implements Calendrical, DateAdjuster, Comparable<YearMonth>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Parser.
     */
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
        .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
        .appendLiteral('-')
        .appendValue(MONTH_OF_YEAR, 2)
        .toFormatter();

    /**
     * The year.
     */
    private final int year;
    /**
     * The month-of-year, not null.
     */
    private final MonthOfYear month;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code YearMonth}.
     *
     * @return the rule for the year-month, not null
     */
    public static CalendricalRule<YearMonth> rule() {
        return ExtendedCalendricalRule.YEAR_MONTH;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the current year-month from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current year-month.
     * The zone and offset will be set based on the time-zone in the clock.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current year-month using the system clock, not null
     */
    public static YearMonth now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current year-month from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current year-month.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current year-month, not null
     */
    public static YearMonth now(Clock clock) {
        final LocalDate now = LocalDate.now(clock);  // called once
        return YearMonth.of(now.getYear(), now.getMonthOfYear());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearMonth} from a year and month.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @return the year-month, not null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public static YearMonth of(int year, MonthOfYear monthOfYear) {
        YEAR.checkValidValue(year);
        MathUtils.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        return new YearMonth(year, monthOfYear);
    }

    /**
     * Obtains an instance of {@code YearMonth} from a year and month.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return the year-month, not null
     * @throws IllegalCalendarFieldValueException if either field value is invalid
     */
    public static YearMonth of(int year, int monthOfYear) {
        return of(year, MonthOfYear.of(monthOfYear));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearMonth} from a set of calendricals.
     * <p>
     * A calendrical represents some form of date and time information.
     * This method combines the input calendricals into a year-month.
     *
     * @param calendricals  the calendricals to create a year-month from, no nulls, not null
     * @return the year-month, not null
     * @throws CalendricalException if unable to merge to a year-month
     */
    public static YearMonth from(Calendrical... calendricals) {
        return CalendricalEngine.merge(calendricals).deriveChecked(rule());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearMonth} from a text string such as {@code 2007-12}.
     * <p>
     * The string must represent a valid year-month.
     * The format must be {@code yyyy-MM}.
     * Years outside the range 0000 to 9999 must be prefixed by the plus or minus symbol.
     *
     * @param text  the text to parse such as "2007-12", not null
     * @return the parsed year-month, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static YearMonth parse(CharSequence text) {
        return PARSER.parse(text, rule());
    }

    /**
     * Obtains an instance of {@code YearMonth} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a year-month.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed year-month, not null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static YearMonth parse(CharSequence text, DateTimeFormatter formatter) {
        MathUtils.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.parse(text, rule());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     */
    private YearMonth(int year, MonthOfYear monthOfYear) {
        this.year = year;
        this.month = monthOfYear;
    }

    /**
     * Returns a copy of this year-month with the new year and month, checking
     * to see if a new object is in fact required.
     *
     * @param newYear  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param newMonth  the month-of-year to represent, validated not null
     * @return the year-month, not null
     */
    private YearMonth with(int newYear, MonthOfYear newMonth) {
        if (year == newYear && month == newMonth) {
            return this;
        }
        return new YearMonth(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this year-month then
     * {@code null} will be returned.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        if (ruleToDerive == rule()) {
            return (T) this;
        }
        return CalendricalEngine.derive(ruleToDerive, rule(), null, null, null, null, ISOChronology.INSTANCE, toFields());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     * Additional information about the year can be obtained by creating a {@link Year}.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return year;
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
     * @return the month-of-year, not null
     */
    public MonthOfYear getMonthOfYear() {
        return month;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth with the year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned year-month, not null
     * @return a {@code YearMonth} based on this year-month with the requested year, not null
     */
    public YearMonth with(Year year) {
        MathUtils.checkNotNull(year, "Year must not be null");
        return withYear(year.getValue());
    }

    /**
     * Returns a copy of this YearMonth with the month-of-year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned year-month, not null
     * @return a {@code YearMonth} based on this year-month with the requested month, not null
     */
    public YearMonth with(MonthOfYear monthOfYear) {
        MathUtils.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        return with(year, monthOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code YearMonth} with the year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned year-month, from MIN_YEAR to MAX_YEAR
     * @return a {@code YearMonth} based on this year-month with the requested year, not null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public YearMonth withYear(int year) {
        YEAR.checkValidValue(year);
        return with(year, month);
    }

    /**
     * Returns a copy of this {@code YearMonth} with the month-of-year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned year-month, from 1 (January) to 12 (December)
     * @return a {@code YearMonth} based on this year-month with the requested month, not null
     * @throws IllegalCalendarFieldValueException if the month-of-year value is invalid
     */
    public YearMonth withMonthOfYear(int monthOfYear) {
        return with(MonthOfYear.of(monthOfYear));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code YearMonth} with the specified period added.
     * <p>
     * This adds the specified period to this year-month, returning a new year-month.
     * Before addition, the period is converted to a {@code Period} using
     * {@link Period#of(PeriodProvider)}.
     * The calculation only uses the years and months fields.
     * Other fields are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a {@code YearMonth} based on this year-month with the period added, not null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws ArithmeticException if the result exceeds the supported range
     */
    public YearMonth plus(PeriodProvider periodProvider) {
        Period period = Period.of(periodProvider);
        return plusMonths(period.totalMonths());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth with the specified period in years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, positive or negative
     * @return a {@code YearMonth} based on this year-month with the years added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public YearMonth plusYears(long years) {
        if (years == 0) {
            return this;
        }
        int newYear = YEAR.checkValidIntValue(year + years);  // safe overflow
        return with(newYear, month);
    }

    /**
     * Returns a copy of this YearMonth with the specified period in months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a {@code YearMonth} based on this year-month with the months added, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public YearMonth plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long monthCount = year * 12L + (month.getValue() - 1);
        long calcMonths = monthCount + months;  // safe overflow
        int newYear = YEAR.checkValidIntValue(MathUtils.floorDiv(calcMonths, 12));
        MonthOfYear newMonth = MonthOfYear.of(MathUtils.floorMod(calcMonths, 12) + 1);
        return with(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code YearMonth} with the specified period subtracted.
     * <p>
     * This subtracts the specified period from this year-month, returning a new year-month.
     * Before subtraction, the period is converted to a {@code Period} using
     * {@link Period#of(PeriodProvider)}.
     * The calculation only uses the years and months fields.
     * Other fields are ignored.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a {@code YearMonth} based on this year-month with the period subtracted, not null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws ArithmeticException if the result exceeds the supported range
     */
    public YearMonth minus(PeriodProvider periodProvider) {
        Period period = Period.of(periodProvider);
        return minusMonths(period.totalMonths());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this YearMonth with the specified period in years subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, positive or negative
     * @return a {@code YearMonth} based on this year-month with the years subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public YearMonth minusYears(long years) {
        if (years == 0) {
            return this;
        }
        int newYear = YEAR.checkValidIntValue(year - years);  // safe overflow
        return with(newYear, month);
    }

    /**
     * Returns a copy of this YearMonth with the specified period in months subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, positive or negative
     * @return a {@code YearMonth} based on this year-month with the months subtracted, not null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public YearMonth minusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long monthCount = year * 12L + (month.getValue() - 1);
        long calcMonths = monthCount - months;  // safe overflow
        int newYear = YEAR.checkValidIntValue(MathUtils.floorDiv(calcMonths, 12));
        MonthOfYear newMonth = MonthOfYear.of(MathUtils.floorMod(calcMonths, 12) + 1);
        return with(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a date to have the value of this year-month, returning a new date.
     * <p>
     * This method implements the {@link DateAdjuster} interface.
     * It is intended that, instead of calling this method directly, it is used from
     * an instance of {@code LocalDate}:
     * <pre>
     *   date = date.with(yearMonth);
     * </pre>
     * <p>
     * If the day-of-month in the specified date is invalid for this year-month then
     * the day-of-month will be altered to the last valid day in the month.
     * 
     * This implementation handles the case where the day-of-month is invalid for the new
     * month and year using the {@link DateResolvers#previousValid()} resolver.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, not null
     */
    public LocalDate adjustDate(LocalDate date) {
        MathUtils.checkNotNull(date, "LocalDate must not be null");
        if (date.getYear() == year && date.getMonthOfYear() == month) {
            return date;
        }
        return DateResolvers.previousValid().resolveDate(year, month, date.getDayOfMonth());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this month in days.
     * <p>
     * This returns the length in days of the month.
     * The year is used to determine the correct length of February.
     *
     * @return the length of the month in days, from 28 to 31
     */
    public int lengthInDays() {
        return month.lengthInDays(Year.isLeap(year));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the day-of-month is valid for this year-month.
     * <p>
     * This method checks whether this year and month and the input day form
     * a valid date.
     *
     * @param dayOfMonth  the day-of-month to validate, from 1 to 31, invalid value returns false
     * @return true if the day is valid for this year-month
     */
    public boolean isValidDay(int dayOfMonth) {
        return dayOfMonth >= 1 && dayOfMonth <= lengthInDays();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a date formed from this year-month at the specified day-of-month.
     * <p>
     * This method merges {@code this} and the specified day to form an
     * instance of {@code LocalDate}.
     * This method can be used as part of a chain to produce a date:
     * <pre>
     * LocalDate date = year.atMonth(month).atDay(day);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to use, from 1 to 31
     * @return the date formed from this year-month and the specified day, not null
     * @throws InvalidCalendarFieldException when the day is invalid for the year-month
     * @see #isValidDay(int)
     */
    public LocalDate atDay(int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this year-month to an equivalent fields object.
     * <p>
     * The fields will contain {@link ISODateTimeRule#YEAR} and
     * {@link ISODateTimeRule#MONTH_OF_YEAR}.
     *
     * @return the equivalent fields, not null
     */
    public DateTimeFields toFields() {
        return DateTimeFields.of(YEAR, year, MONTH_OF_YEAR, month.getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year-month to another year-month.
     *
     * @param other  the other year-month to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(YearMonth other) {
        int cmp = MathUtils.safeCompare(year, other.year);
        if (cmp == 0) {
            cmp = month.compareTo(other.month);
        }
        return cmp;
    }

    /**
     * Is this year-month after the specified year-month.
     *
     * @param other  the other year-month to compare to, not null
     * @return true if this is after the specified year-month
     */
    public boolean isAfter(YearMonth other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this year-month before the specified year-month.
     *
     * @param other  the other year-month to compare to, not null
     * @return true if this point is before the specified year-month
     */
    public boolean isBefore(YearMonth other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this year-month is equal to another year-month.
     * <p>
     * The comparison is based on the time-line position of the year-months.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other year-month
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof YearMonth) {
            YearMonth other = (YearMonth) obj;
            return year == other.year && month == other.month;
        }
        return false;
    }

    /**
     * A hash code for this year-month.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return year ^ (month.getValue() << 27);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this year-month as a {@code String}, such as {@code 2007-12}.
     * <p>
     * The output will be in the format {@code yyyy-MM}:
     *
     * @return a string representation of this year-month, not null
     */
    @Override
    public String toString() {
        int yearValue = year;
        int monthValue = month.getValue();
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(9);
        if (absYear < 1000) {
            if (yearValue < 0) {
                buf.append(yearValue - 10000).deleteCharAt(1);
            } else {
                buf.append(yearValue + 10000).deleteCharAt(0);
            }
        } else {
            buf.append(yearValue);
        }
        return buf.append(monthValue < 10 ? "-0" : "-")
            .append(monthValue)
            .toString();
    }

    /**
     * Outputs this year-month as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted year-month string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    public String toString(DateTimeFormatter formatter) {
        MathUtils.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.print(this);
    }

}
