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
package javax.time.calendrical;

import static javax.time.calendrical.LocalDateTimeField.ERA;
import static javax.time.calendrical.LocalDateTimeField.YEAR;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.Clock;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.Month;
import javax.time.format.CalendricalFormatter;
import javax.time.format.CalendricalParseException;
import javax.time.format.DateTimeFormatter;
import javax.time.format.DateTimeFormatterBuilder;
import javax.time.format.SignStyle;

/**
 * A year in the ISO-8601 calendar system, such as {@code 2007}.
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
 * For example, the value "2007" can be stored in a {@code Year}.
 * <p>
 * Years represented by this class follow the ISO-8601 standard and use
 * the proleptic numbering system. Year 1 is preceded by year 0, then by year -1.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which todays's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * Any application that uses historical dates should consider using {@code HistoricDate}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class Year
        implements AdjustableDateTime, DateTimeAdjuster, Comparable<Year>, Serializable {

    /**
     * Constant for the minimum year on the proleptic ISO calendar system, -999,999,999.
     */
    public static final int MIN_YEAR = DateTimes.MIN_YEAR;
    /**
     * Constant for the maximum year on the proleptic ISO calendar system, 999,999,999.
     */
    public static final int MAX_YEAR = DateTimes.MAX_YEAR;

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Parser.
     */
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
        .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
        .toFormatter();

    /**
     * The year being represented.
     */
    private final int year;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current year from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current year.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current year using the system clock, not null
     */
    public static Year now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current year from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current year.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current year, not null
     */
    public static Year now(Clock clock) {
        final LocalDate now = LocalDate.now(clock);  // called once
        return Year.of(now.getYear());
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
     * @return the year, not null
     * @throws CalendricalException if the field is invalid
     */
    public static Year of(int isoYear) {
        YEAR.checkValidValue(isoYear);
        return new Year(isoYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Year} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code Year}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the year, not null
     * @throws CalendricalException if unable to convert to a {@code Year}
     */
    public static Year from(DateTime calendrical) {
        if (calendrical instanceof Year) {
            return (Year) calendrical;
        }
        return of((int) calendrical.get(YEAR));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Year} from a text string such as {@code 2007}.
     * <p>
     * The string must represent a valid year.
     * Years outside the range 0000 to 9999 must be prefixed by the plus or minus symbol.
     *
     * @param text  the text to parse such as "2007", not null
     * @return the parsed year, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static Year parse(CharSequence text) {
        return parse(text, PARSER);
    }

    /**
     * Obtains an instance of {@code Year} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a year.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed year, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static Year parse(CharSequence text, CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.parse(text, Year.class);
    }

    //-------------------------------------------------------------------------
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
     * This is historically inaccurate, but is correct for the ISO-8601 standard.
     *
     * @param year  the year to check
     * @return true if the year is leap, false otherwise
     */
    public static boolean isLeap(long year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
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
     * <p>
     * This is a synonm for {@link #getYear()}.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getValue() {
        return year;
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case YEAR_OF_ERA: return (year < 1 ? 1 - year : year);
                case YEAR: return year;
                case ERA: return (year < 1 ? 0 : 1);
            }
            throw new CalendricalException(field.getName() + " not valid for Year");
        }
        return field.doGet(this);
    }

    /**
     * Gets the year value.
     * <p>
     * This is a synonm for {@link #getValue()}.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return year;
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
     * This is historically inaccurate, but is correct for the ISO-8601 standard.
     *
     * @return true if the year is leap, false otherwise
     */
    public boolean isLeap() {
        return Year.isLeap(year);
    }

    /**
     * Gets the length of this year in days.
     *
     * @return the length of this year in days, 365 or 366
     */
    public int length() {
        return isLeap() ? 366 : 365;
    }

    //-----------------------------------------------------------------------
    @Override
    public Year with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case YEAR_OF_ERA: return Year.of((int) (year < 1 ? 1 - newValue : newValue));
                case YEAR: return Year.of((int) newValue);
                case ERA: return (get(ERA) == newValue ? this : Year.of(1 - year));
            }
            throw new CalendricalException(field.getName() + " not valid for Year");
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the next year.
     *
     * @return the next year, not null
     * @throws CalendricalException if the maximum year is reached
     */
    public Year next() {
        if (year == MAX_YEAR) {
            throw new CalendricalException("Year is already at the maximum value");
        }
        return of(year + 1);
    }

    /**
     * Returns the previous year.
     *
     * @return the previous year, not null
     * @throws CalendricalException if the maximum year is reached
     */
    public Year previous() {
        if (year == MIN_YEAR) {
            throw new CalendricalException("Year is already at the minimum value");
        }
        return of(year - 1);
    }

    //-----------------------------------------------------------------------
    @Override
    public Year plus(long periodAmount, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            switch ((LocalPeriodUnit) unit) {
                case YEARS: return plusYears(periodAmount);
                case DECADES: return plusYears(DateTimes.safeMultiply(periodAmount, 10));
                case CENTURIES: return plusYears(DateTimes.safeMultiply(periodAmount, 100));
                case MILLENNIA: return plusYears(DateTimes.safeMultiply(periodAmount, 1000));
            }
            throw new CalendricalException(unit.getName() + " not valid for Year");
        }
        return unit.doAdd(this, periodAmount);
    }

    /**
     * Returns a copy of this Year with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a {@code Year} based on this year with the period added, not null
     * @throws CalendricalException if the result exceeds the supported year range
     */
    public Year plusYears(long years) {
        if (years == 0) {
            return this;
        }
        return of(YEAR.checkValidIntValue(year + years));  // overflow safe
    }

    //-----------------------------------------------------------------------
    @Override
    public Year minus(long periodAmount, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(periodAmount), unit);
    }

    /**
     * Returns a copy of this Year with the specified number of years subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract
     * @return a {@code Year} based on this year with the period subtracted, not null
     * @throws CalendricalException if the result exceeds the supported year range
     */
    public Year minusYears(long years) {
        if (years == 0) {
            return this;
        }
        return of(YEAR.checkValidIntValue(year - years));  // overflow safe
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
     * @param month  the month-of-year to use, not null
     * @return the year-month formed from this year and the specified month, not null
     */
    public YearMonth atMonth(Month month) {
        return YearMonth.of(year, month);
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
     * @param month  the month-of-year to use, from 1 (January) to 12 (December)
     * @return the year-month formed from this year and the specified month, not null
     */
    public YearMonth atMonth(int month) {
        return YearMonth.of(year, month);
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
     * @return the local date formed from this year and the specified month-day, not null
     * @throws CalendricalException if the month-day is February 29th and this is not a leap year
     */
    public LocalDate atMonthDay(MonthDay monthDay) {
        return LocalDate.of(year, monthDay.getMonth(), monthDay.getDayOfMonth());
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
     * @return the local date formed from this year and the specified date of year, not null
     * @throws CalendricalException if the day of year is 366 and this is not a leap year
     */
    public LocalDate atDay(int dayOfYear) {
        return LocalDate.ofYearDay(year, dayOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTime} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>Year
     * <li>DateTimeBuilder, using {@link LocalDateTimeField#YEAR}
     * <li>Class, returning {@code Year}
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == DateTimeBuilder.class) {
            return (R) new DateTimeBuilder(YEAR, year);
        } else if (type == Class.class) {
            return (R) Year.class;
        } else if (type == Year.class) {
            return (R) this;
        }
        return null;
    }

    /**
     * Implementation of the strategy to make an adjustment to the specified date-time object.
     * <p>
     * This method is not intended to be called by application code directly.
     * Applications should use the {@code with(DateTimeAdjuster)} method on the
     * date-time object to make the adjustment passing this as the argument.
     * 
     * <h4>Implementation notes</h4>
     * Adjusts the specified date-time to have the value of this year.
     * Other fields in the target object may be adjusted of necessary to ensure the date is valid.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the target object to be adjusted, not null
     * @return the adjusted object, not null
     */
    @Override
    public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
        // TODO: check calendar system is ISO
        return calendrical.with(YEAR, year);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year to another year.
     *
     * @param other  the other year to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(Year other) {
        return DateTimes.safeCompare(year, other.year);
    }

    /**
     * Is this year after the specified year.
     *
     * @param other  the other year to compare to, not null
     * @return true if this is after the specified year
     */
    public boolean isAfter(Year other) {
        return year > other.year;
    }

    /**
     * Is this year before the specified year.
     *
     * @param other  the other year to compare to, not null
     * @return true if this point is before the specified year
     */
    public boolean isBefore(Year other) {
        return year < other.year;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this year is equal to another year.
     * <p>
     * The comparison is based on the time-line position of the years.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other year
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Year) {
            return year == ((Year) obj).year;
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
     * Outputs this year as a {@code String}.
     *
     * @return a string representation of this year, not null
     */
    @Override
    public String toString() {
        return Integer.toString(year);
    }

    /**
     * Outputs this year as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted year string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    public String toString(CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.print(this);
    }

}
