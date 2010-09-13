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

/**
 * A year in the ISO-8601 calendar system, such as '2007'.
 * <p>
 * {@code Year} is an immutable calendrical that represents a year.
 * Any field that can be derived from a year can be obtained.
 * <p>
 * <b>Note that years in the ISO chronology only align with years in the
 * Gregorian-Julian system for modern years. Parts of Russia did not switch to the
 * modern Gregorian/ISO rules until 1920.
 * As such, historical years must be treated with caution.</b>
 * <p>
 * This class does not store or represent a month, day, time or time-zone.
 * Thus, for example, the value "2007" can be stored in a {@code Year}.
 * <p>
 * Years represented by this class follow the ISO-8601 standard and use
 * the proleptic numbering system. Year 1 is preceded by year 0, then by year -1.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which todays's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * Any application that uses historical dates should consider using {@code HistoricDate}.
 * <p>
 * Year is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class Year
        implements Calendrical, Comparable<Year>, Serializable, DateAdjuster, CalendricalMatcher {

    /**
     * Constant for the minimum year on the proleptic ISO calendar system.
     */
    public static final int MIN_YEAR = Integer.MIN_VALUE + 2;
    /**
     * Constant for the maximum year on the proleptic ISO calendar system,
     * which is the same as the maximum for year of era.
     */
    public static final int MAX_YEAR = Integer.MAX_VALUE;
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The year being represented.
     */
    private final int year;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the year field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the year rule, never null
     */
    public static DateTimeFieldRule<Integer> rule() {
        return ISOChronology.yearRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains the current year from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current year.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current year, never null
     */
    public static Year now(Clock clock) {
        return Year.of(LocalDate.now(clock));
    }

    /**
     * Obtains the current year from the system clock in the default time-zone.
     * <p>
     * This will query the system clock in the default time-zone to obtain the current year.
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current year using the system clock, never null
     */
    public static Year nowSystemClock() {
        return now(Clock.systemDefaultZone());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Year}.
     * <p>
     * This method accepts a year value from the proleptic ISO calendar system.
     * <p>
     * The year 2AD/CE is represented by 2.<br />
     * The year 1AD/CE is represented by 1.<br />
     * The year 1BC/BCE is represented by 0.<br />
     * The year 2BC/BCE is represented by -1.<br />
     *
     * @param isoYear  the ISO proleptic year to represent, from MIN_YEAR to MAX_YEAR
     * @return the created Year, never null
     * @throws IllegalCalendarFieldValueException if the field is invalid
     */
    public static Year of(int isoYear) {
        rule().checkValue(isoYear);
        return new Year(isoYear);
    }

    /**
     * Obtains an instance of {@code Year} from a calendrical.
     * <p>
     * This can be used extract the year value directly from any implementation
     * of {@code Calendrical}, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the Year instance, never null
     * @throws UnsupportedRuleException if the year cannot be obtained
     */
    public static Year of(Calendrical calendrical) {
        return Year.of(rule().getInt(calendrical));
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent
     */
    private Year(int year) {
        this.year = year;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year value.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getValue() {
        return year;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this instance then
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, year, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the ISO proleptic
     * calendar system rules.
     * <p>
     * This method applies the current rules for leap years across the whole time-line.
     * In general, a year is a leap year if it is divisible by four without
     * remainder. However, years divisible by 100, are not leap years, with
     * the exception of years divisible by 400 which are.
     * <p>
     * For example, 1904 is a leap year it is divisible by 4.
     * 1900 was not a leap year as it is divisible by 100, however 2000 was a
     * leap year as it is divisible by 400.
     * <p>
     * The calculation is proleptic - applying the same rules into the far future and far past.
     * This is historically inaccurate, but is correct for the ISO8601 standard.
     *
     * @return true if the year is leap, false otherwise
     */
    public boolean isLeap() {
        return ISOChronology.isLeapYear(year);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the next year.
     *
     * @return the next year, never null
     * @throws CalendricalException if the maximum year is reached
     */
    public Year next() {
        if (year == MAX_YEAR) {
            throw new CalendricalException("Year is already at the maximum value");
        }
        return of(year + 1);
    }

    /**
     * Returns the next leap year after the current year.
     * The definition of a leap year is specified in {@link #isLeap()}.
     *
     * @return the next leap year after this year, never null
     * @throws CalendricalException if the maximum year is reached
     */
    public Year nextLeap() {
        Year temp = next();
        while (!temp.isLeap()) {
            temp = temp.next();
        }
        return temp;
    }

    /**
     * Returns the previous year.
     *
     * @return the previous year, never null
     * @throws CalendricalException if the maximum year is reached
     */
    public Year previous() {
        if (year == MIN_YEAR) {
            throw new CalendricalException("Year is already at the minimum value");
        }
        return of(year - 1);
    }

    /**
     * Returns the previous leap year before the current year.
     * The definition of a leap year is specified in {@link #isLeap()}.
     *
     * @return the previous leap year after this year, never null
     * @throws CalendricalException if the minimum year is reached
     */
    public Year previousLeap() {
        Year temp = previous();
        while (!temp.isLeap()) {
            temp = temp.previous();
        }
        return temp;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code Year} with the specified period added.
     * <p>
     * This adds the specified period to this year, returning a new year.
     * Before addition, the period is converted to a {@code Period} using
     * {@link Period#of(PeriodProvider)}.
     * The calculation simply adds the amount of years from the specified period.
     * ISO fields other than years are ignored.
     * <p>
     * Note that no normalization is performed. Thus, adding 24 months has no effect.
     * To take months into account, the period must be normalized by the caller.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a {@code Year} based on this year with the period added, never null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws ArithmeticException if the result exceeds the supported year range
     */
    public Year plus(PeriodProvider periodProvider) {
        Period period = Period.of(periodProvider);
        return plusYears(period.getYears());
    }

    /**
     * Returns a copy of this Year with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a {@code Year} based on this year with the period added, never null
     * @throws CalendricalException if the result exceeds the supported year range
     */
    public Year plusYears(long years) {
        if (years == 0) {
            return this;
        }
        return of(rule().checkValue(year + years));  // overflow safe
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code Year} with the specified period subtracted.
     * <p>
     * This subtracts the specified period from this year, returning a new year.
     * Before subtraction, the period is converted to a {@code Period} using
     * {@link Period#of(PeriodProvider)}.
     * The calculation simply adds the amount of years from the specified period.
     * ISO fields other than years are ignored.
     * <p>
     * Note that no normalization is performed. Thus, subtracting 24 months has no effect.
     * To take months into account, the period must be normalized by the caller.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a {@code Year} based on this year with the period subtracted, never null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws ArithmeticException if the result exceeds the supported year range
     */
    public Year minus(PeriodProvider periodProvider) {
        Period period = Period.of(periodProvider);
        return minusYears(period.getYears());
    }

    /**
     * Returns a copy of this Year with the specified number of years subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract
     * @return a {@code Year} based on this year with the period subtracted, never null
     * @throws CalendricalException if the result exceeds the supported year range
     */
    public Year minusYears(long years) {
        if (years == 0) {
            return this;
        }
        return of(rule().checkValue(year - years));  // overflow safe
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year extracted from the calendrical matches this.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        Integer calValue = calendrical.get(rule());
        return calValue != null && calValue == getValue();
    }

    /**
     * Adjusts a date to have the value of this year, returning a new date.
     * <p>
     * This method implements the {@link DateAdjuster} interface.
     * It is intended that, instead of calling this method directly, it is used from
     * an instance of {@code LocalDate}:
     * <pre>
     *   date = date.with(year);
     * </pre>
     * <p>
     * This implementation handles the case where the date represents February 29 and
     * this is not a leap year using the {@link DateResolvers#previousValid()} resolver.
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
     * Adjusts a date to have the value of this year, using a resolver to
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
        return date.withYear(year, resolver);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this year in days.
     *
     * @return the length of this year in days, 365 or 366
     */
    public int lengthInDays() {
        return isLeap() ? 366 : 365;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the month-day is valid for this year.
     * <p>
     * This method checks whether this year and the input month and day form
     * a valid date.
     *
     * @param monthDay  the month-day to validate, null returns false
     * @return true if the month and day are valid for this year
     */
    public boolean isValidMonthDay(MonthDay monthDay) {
        return monthDay != null && monthDay.isValidYear(year);
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the ISO proleptic year, from MIN_YEAR to MAX_YEAR.
//     * <p>
//     * The year 2AD/CE is represented by 2.<br />
//     * The year 1AD/CE is represented by 1.<br />
//     * The year 1BC/BCE is represented by 0.<br />
//     * The year 2BC/BCE is represented by -1.<br />
//     *
//     * @return the ISO proleptic year, from MIN_YEAR to MAX_YEAR
//     */
//    public int getISOYear() {
//        return year;
//    }
//
//    /**
//     * Returns a new {@code Year} instance with a different year.
//     * <p>
//     * The year 2AD/CE is represented by 2.<br />
//     * The year 1AD/CE is represented by 1.<br />
//     * The year 1BC/BCE is represented by 0.<br />
//     * The year 2BC/BCE is represented by -1.<br />
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param isoYear  the year to represent, from MIN_YEAR to MAX_YEAR
//     * @return a new updated Year, never null
//     */
//    public Year withISOYear(int isoYear) {
//        rule().checkValue(isoYear);
//        return null;
//    }

//    /**
//     * Gets the year of era, from 1 to MAX_YEAR, which is used in combination
//     * with {@link #getEstimatedEra()}.
//     * <p>
//     * The year 2, estimated as 2AD/CE is represented by 2.<br />
//     * The year 1, estimated as 1AD/CE is represented by 1.<br />
//     * The year 0, estimated as 1BC/BCE is represented by 1.<br />
//     * The year -1, estimated as 2BC/BCE is represented by 2.<br />
//     *
//     * @return the year of era, from 1 to MAX_YEAR
//     */
//    public int getYearOfEra() {
//        // TODO: ISO Year doesn't have an era
//        return (year > 0 ? year : -(year - 1));
//    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the century of era, from 0 to MAX_YEAR / 100.
//     * <p>
//     * This method uses a simple definition of century, being the
//     * ISO year divided by 100, which is the same as the printed ISO year
//     * without the last two digits.
//     * <p>
//     * The value 20 will be returned from 2000 to 2099.<br/>
//     * The value 19 will be returned from 1900 to 1999.<br/>
//     * The value 1 will be returned from 100 to 199.<br/>
//     * The value 0 will be returned from -99 to 99 (199 years in the century).<br/>
//     * The value -1 will be returned from -100 to 199.<br/>
//     *
//     * @return the century of era, from 0 to MAX_YEAR / 100
//     */
//    public int getISOCentury() {
//        return year / 100;
//    }
//
//    /**
//     * Gets the year of century, from 0 to 99, which is used in combination
//     * with {@link #getCenturyOfEra()}.
//     * This is the lower two digits of the ISO year.
//     *
//     * @return the year of era, from 0 to 99
//     */
//    public int getYearOfISOCentury() {
//        int yoc = year % 100;
//        return yoc < 0 ? yoc + 100 : yoc;
//    }

    //-----------------------------------------------------------------------
    /**
     * Returns a year-month formed from this year at the specified month.
     * <p>
     * This method merges {@code this} and the specified month to form an
     * instance of {@code YearMonth}.
     * This method can be used as part of a chain to produce a date:
     * <pre>
     * LocalDate date = year.atMonth(month).atDay(day);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to use, not null
     * @return the year-month formed from this year and the specified month, never null
     */
    public YearMonth atMonth(MonthOfYear monthOfYear) {
        return YearMonth.of(year, monthOfYear);
    }

    /**
     * Returns a year-month formed from this year at the specified month.
     * <p>
     * This method merges {@code this} and the specified month to form an
     * instance of {@code YearMonth}.
     * This method can be used as part of a chain to produce a date:
     * <pre>
     * LocalDate date = year.atMonth(month).atDay(day);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to use, from 1 (January) to 12 (December)
     * @return the year-month formed from this year and the specified month, never null
     */
    public YearMonth atMonth(int monthOfYear) {
        return YearMonth.of(year, monthOfYear);
    }

    /**
     * Returns a date formed from this year at the specified month-day.
     * <p>
     * This merges the two objects - {@code this} and the specified day -
     * to form an instance of {@code LocalDate}.
     * <pre>
     * LocalDate date = year.atMonthDay(monthDay);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthDay  the month-day to use, not null
     * @return the local date formed from this year and the specified month-day, never null
     * @throws InvalidCalendarFieldException if the month-day is February 29th and this is not a leap year
     */
    public LocalDate atMonthDay(MonthDay monthDay) {
        return LocalDate.of(year, monthDay.getMonthOfYear(), monthDay.getDayOfMonth());
    }

    /**
     * Returns a date formed from this year at the specified day-of-year.
     * <p>
     * This merges the two objects - {@code this} and the specified day -
     * to form an instance of {@code LocalDate}.
     * <pre>
     * LocalDate date = year.atDay(dayOfYear);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to use, not null
     * @return the local date formed from this year and the specified date of year, never null
     * @throws InvalidCalendarFieldException if the day of year is 366 and this is not a leap year
     */
    public LocalDate atDay(int dayOfYear) {
        ISOChronology.dayOfYearRule().checkValue(dayOfYear);
        if (dayOfYear == 366 && !isLeap()) {
            throw new InvalidCalendarFieldException("Day of year 366 is invalid for year " + year, ISOChronology.dayOfYearRule());
        }
        return LocalDate.of(year, 1, 1).plusDays(dayOfYear - 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year to another year.
     *
     * @param other  the other year to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if {@code other} is null
     */
    public int compareTo(Year other) {
        return MathUtils.safeCompare(year, other.year);
    }

    /**
     * Is this year after the specified year.
     *
     * @param other  the other year to compare to, not null
     * @return true if this is after the specified year
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isAfter(Year other) {
        return year > other.year;
    }

    /**
     * Is this year before the specified year.
     *
     * @param other  the other year to compare to, not null
     * @return true if this point is before the specified year
     * @throws NullPointerException if {@code other} is null
     */
    public boolean isBefore(Year other) {
        return year < other.year;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this year equal to the specified year.
     *
     * @param other  the other year to compare to, null returns false
     * @return true if this point is equal to the specified year
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Year) {
            return year == ((Year) other).year;
        }
        return false;
    }

    /**
     * A hash code for this year.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return year;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the string form of the year.
     *
     * @return the string form of the year
     */
    @Override
    public String toString() {
        return "Year=" + Integer.toString(year);
    }

}
