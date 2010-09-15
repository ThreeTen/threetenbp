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
import javax.time.MathUtils;
import javax.time.calendar.format.CalendricalPrintException;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;

/**
 * A year-month in the ISO-8601 calendar system, such as '2007-12'.
 * <p>
 * {@code YearMonth} is an immutable calendrical that represents the combination
 * of a year and month. Any field that can be derived from a year and month, such as
 * quarter-of-year, can be obtained.
 * <p>
 * This class does not store or represent a day, time or time-zone.
 * Thus, for example, the value "October 2007" can be stored in a {@code YearMonth}.
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
        implements Calendrical, CalendricalMatcher, DateAdjuster, Comparable<YearMonth>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Parser.
     */
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
        .appendValue(ISOChronology.yearRule(), 4, 10, SignStyle.EXCEEDS_PAD)
        .appendLiteral('-')
        .appendValue(ISOChronology.monthOfYearRule(), 2)
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
     * Obtains the current year-month from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current year-month.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current year-month, never null
     */
    public static YearMonth now(Clock clock) {
        LocalDate date = LocalDate.now(clock);
        return YearMonth.of(date.getYear(), date.getMonthOfYear());
    }

    /**
     * Obtains the current year-month from the system clock in the default time-zone.
     * <p>
     * This will query the system clock in the default time-zone to obtain the current year-month.
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current year-month using the system clock, never null
     */
    public static YearMonth nowSystemClock() {
        return now(Clock.systemDefaultZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearMonth} from a year and month.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @return the year-month, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public static YearMonth of(int year, MonthOfYear monthOfYear) {
        ISOChronology.yearRule().checkValue(year);
        ISOChronology.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        return new YearMonth(year, monthOfYear);
    }

    /**
     * Obtains an instance of {@code YearMonth} from a year and month.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return the year-month, never null
     * @throws IllegalCalendarFieldValueException if either field value is invalid
     */
    public static YearMonth of(int year, int monthOfYear) {
        return of(year, MonthOfYear.of(monthOfYear));
    }

    /**
     * Obtains an instance of {@code YearMonth} from a Calendrical.
     * <p>
     * This method will create a year-month from the Calendrical by extracting
     * the year and month-of-year fields.
     *
     * @param calendrical  the calendrical to use, not null
     * @return the year-month, never null
     * @throws UnsupportedRuleException if either field cannot be found
     * @throws InvalidCalendarFieldException if the value for either field is invalid
     */
    public static YearMonth of(Calendrical calendrical) {
        Integer year = ISOChronology.yearRule().getValueChecked(calendrical);
        MonthOfYear month = ISOChronology.monthOfYearRule().getValueChecked(calendrical);
        return of(year, month);
    }

    /**
     * Obtains an instance of {@code YearMonth} from a text string.
     * <p>
     * The following formats are accepted in ASCII:
     * <ul>
     * <li>{year}-{monthOfYear}
     * </ul>
     * The year has between 4 and 10 digits with values from MIN_YEAR to MAX_YEAR.
     * If there are more than 4 digits then the year must be prefixed with the plus symbol.
     * Negative years are allowed, but not negative zero.
     * <p>
     * The month-of-year has 2 digits and has values from 1 to 12.
     *
     * @param text  the text to parse such as '2007-12', not null
     * @return the parsed year-month, never null
     * @throws CalendricalException if the text cannot be parsed
     */
    public static YearMonth parse(String text) {
        return PARSER.parse(text, rule());
    }

    /**
     * Obtains an instance of {@code YearMonth} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a year-month.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed year-month, never null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalException if the text cannot be parsed
     */
    public static YearMonth parse(String text, DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
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
     * @return the year-month, never null
     */
    private YearMonth with(int newYear, MonthOfYear newMonth) {
        if (year == newYear && month == newMonth) {
            return this;
        }
        return new YearMonth(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that this year-month uses, which is the ISO calendar system.
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
     * If the value cannot be returned for the rule from this year-month then
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        ISOChronology.checkNotNull(rule, "CalendricalRule must not be null");
        if (rule.equals(ISOChronology.yearRule())) {
            return rule.reify(year);
        }
        if (rule.equals(ISOChronology.monthOfYearRule())) {
            return rule.reify(month);
        }
        return rule().deriveValueFor(rule, this, this);
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
     * @return the month-of-year, never null
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
     * @return a {@code YearMonth} based on this year-month with the requested year, never null
     */
    public YearMonth with(Year year) {
        ISOChronology.checkNotNull(year, "Year must not be null");
        return withYear(year.getValue());
    }

    /**
     * Returns a copy of this YearMonth with the month-of-year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned year-month, not null
     * @return a {@code YearMonth} based on this year-month with the requested month, never null
     */
    public YearMonth with(MonthOfYear monthOfYear) {
        ISOChronology.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        return with(year, monthOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code YearMonth} with the year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned year-month, from MIN_YEAR to MAX_YEAR
     * @return a {@code YearMonth} based on this year-month with the requested year, never null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public YearMonth withYear(int year) {
        ISOChronology.yearRule().checkValue(year);
        return with(year, month);
    }

    /**
     * Returns a copy of this {@code YearMonth} with the month-of-year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned year-month, from 1 (January) to 12 (December)
     * @return a {@code YearMonth} based on this year-month with the requested month, never null
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
     * @return a {@code YearMonth} based on this year-month with the period added, never null
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
     * @return a {@code YearMonth} based on this year-month with the years added, never null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public YearMonth plusYears(long years) {
        if (years == 0) {
            return this;
        }
        int newYear = ISOChronology.yearRule().checkValue(year + years);  // safe overflow
        return with(newYear, month);
    }

    /**
     * Returns a copy of this YearMonth with the specified period in months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a {@code YearMonth} based on this year-month with the months added, never null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public YearMonth plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long monthCount = year * 12L + (month.getValue() - 1);
        long calcMonths = monthCount + months;  // safe overflow
        int newYear = ISOChronology.yearRule().checkValue(MathUtils.floorDiv(calcMonths, 12));
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
     * @return a {@code YearMonth} based on this year-month with the period subtracted, never null
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
     * @return a {@code YearMonth} based on this year-month with the years subtracted, never null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public YearMonth minusYears(long years) {
        if (years == 0) {
            return this;
        }
        int newYear = ISOChronology.yearRule().checkValue(year - years);  // safe overflow
        return with(newYear, month);
    }

    /**
     * Returns a copy of this YearMonth with the specified period in months subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, positive or negative
     * @return a {@code YearMonth} based on this year-month with the months subtracted, never null
     * @throws CalendricalException if the result exceeds the supported range
     */
    public YearMonth minusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long monthCount = year * 12L + (month.getValue() - 1);
        long calcMonths = monthCount - months;  // safe overflow
        int newYear = ISOChronology.yearRule().checkValue(MathUtils.floorDiv(calcMonths, 12));
        MonthOfYear newMonth = MonthOfYear.of(MathUtils.floorMod(calcMonths, 12) + 1);
        return with(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Rolls the month-of-year, adding the specified number of months to a copy
     * of this {@code YearMonth}.
     * <p>
     * This method will add the specified number of months to the month-day,
     * rolling from December back to January if necessary.
     * The year is not altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to roll by, positive or negative
     * @return a {@code YearMonth} based on this year-month with the month rolled, never null
     */
    public YearMonth rollMonthOfYear(int months) {
        return with(month.roll(months));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year-month extracted from the calendrical matches this.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        return this.equals(calendrical.get(rule()));
    }

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
     * This implementation handles the case where the day-of-month is invalid for the new
     * month and year using the {@link DateResolvers#previousValid()} resolver.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     */
    public LocalDate adjustDate(LocalDate date) {
        return adjustDate(date, DateResolvers.previousValid());
    }

    /**
     * Adjusts a date to have the value of this year-month, using a resolver to
     * handle the case when the day-of-month becomes invalid.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @param resolver  the date resolver to use if the day-of-month becomes invalid, not null
     * @return the adjusted date, never null
     * @throws IllegalCalendarFieldValueException if the date cannot be resolved using the resolver
     */
    public LocalDate adjustDate(LocalDate date, DateResolver resolver) {
        ISOChronology.checkNotNull(date, "LocalDate must not be null");
        ISOChronology.checkNotNull(resolver, "DateResolver must not be null");
        if (date.getYear() == year && date.getMonthOfYear() == month) {
            return date;
        }
        LocalDate resolved = resolver.resolveDate(year, month, date.getDayOfMonth());
        ISOChronology.checkNotNull(resolved, "The implementation of DateResolver must not return null");
        return resolved;
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
        return month.lengthInDays(ISOChronology.isLeapYear(year));
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
     * @return the date formed from this year-month and the specified day, never null
     * @throws InvalidCalendarFieldException when the day is invalid for the year-month
     * @see #isValidDay(int)
     */
    public LocalDate atDay(int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year field as a {@code Year}.
     * <p>
     * This method provides access to an object representing the year field.
     * {@code Year} has methods for querying addition year-based information.
     *
     * @return the year, never null
     */
    public Year toYear() {
        return Year.of(year);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year-month to another year-month.
     *
     * @param other  the other year-month to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if {@code other} is null
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
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isAfter(YearMonth other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this year-month before the specified year-month.
     *
     * @param other  the other year-month to compare to, not null
     * @return true if this point is before the specified year-month
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isBefore(YearMonth other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this year-month equal to the specified year-month.
     *
     * @param other  the other year-month to compare to, null returns false
     * @return true if this point is equal to the specified year-month
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof YearMonth) {
            YearMonth otherYM = (YearMonth) other;
            return year == otherYM.year && month == otherYM.month;
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
     * @return the formatted year-month, never null
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
     * @return the formatted year-month string, never null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalPrintException if an error occurs during printing
     */
    public String toString(DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.print(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field rule for the year-month.
     *
     * @return the field rule for the date-time, never null
     */
    public static CalendricalRule<YearMonth> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalRule<YearMonth> implements Serializable {
        private static final CalendricalRule<YearMonth> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(YearMonth.class, ISOChronology.INSTANCE, "YearMonth", ISOChronology.periodMonths(), null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected YearMonth derive(Calendrical calendrical) {
            Integer year = calendrical.get(ISOChronology.yearRule());
            MonthOfYear moy = calendrical.get(ISOChronology.monthOfYearRule());
            return year != null && moy != null ? YearMonth.of(year, moy) : null;
        }
    }

}
