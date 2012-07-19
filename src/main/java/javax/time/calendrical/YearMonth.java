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

import static javax.time.calendrical.LocalDateTimeField.EPOCH_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ERA;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.CalendricalParseException;
import javax.time.Clock;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.Month;
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
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class YearMonth
        implements AdjustableDateTime, DateTimeAdjuster, Comparable<YearMonth>, Serializable {

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
    private final Month month;

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
        return YearMonth.of(now.getYear(), now.getMonth());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearMonth} from a year and month.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     * @return the year-month, not null
     * @throws CalendricalException if the year value is invalid
     */
    public static YearMonth of(int year, Month month) {
        YEAR.checkValidValue(year);
        DateTimes.checkNotNull(month, "Month must not be null");
        return new YearMonth(year, month);
    }

    /**
     * Obtains an instance of {@code YearMonth} from a year and month.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return the year-month, not null
     * @throws CalendricalException if either field value is invalid
     */
    public static YearMonth of(int year, int month) {
        return of(year, Month.of(month));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code YearMonth} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code YearMonth}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the year-month, not null
     * @throws CalendricalException if unable to convert to a {@code YearMonth}
     */
    public static YearMonth from(DateTime calendrical) {
        if (calendrical instanceof YearMonth) {
            return (YearMonth) calendrical;
        }
        return of((int) calendrical.get(YEAR), (int) calendrical.get(MONTH_OF_YEAR));
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
        return parse(text, PARSER);
    }

    /**
     * Obtains an instance of {@code YearMonth} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a year-month.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed year-month, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static YearMonth parse(CharSequence text, CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.parse(text, YearMonth.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param year  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, not null
     */
    private YearMonth(int year, Month month) {
        this.year = year;
        this.month = month;
    }

    /**
     * Returns a copy of this year-month with the new year and month, checking
     * to see if a new object is in fact required.
     *
     * @param newYear  the year to represent, validated from MIN_YEAR to MAX_YEAR
     * @param newMonth  the month-of-year to represent, validated not null
     * @return the year-month, not null
     */
    private YearMonth with(int newYear, Month newMonth) {
        if (year == newYear && month == newMonth) {
            return this;
        }
        return new YearMonth(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case MONTH_OF_YEAR: return month.getValue();
                case EPOCH_MONTH: return ((year - 1970) * 12L) + getMonth().ordinal();
                case YEAR_OF_ERA: return (year < 1 ? 1 - year : year);
                case YEAR: return year;
                case ERA: return (year < 1 ? 0 : 1);
            }
            throw new CalendricalException(field.getName() + " not valid for YearMonth");
        }
        return field.doGet(this);
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
     * Gets the month-of-year field using the {@code Month} enum.
     * <p>
     * This method returns the enum {@link Month} for the month.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link Month#getValue() int value}.
     *
     * @return the month-of-year, not null
     */
    public Month getMonth() {
        return month;
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
    public boolean isLeapYear() {
        return DateTimes.isLeapYear(year);
    }

    /**
     * Returns the length of the month, taking account of the year.
     * <p>
     * This returns the length of the month in days.
     * For example, a date in January would return 31.
     *
     * @return the length of the month in days, from 28 to 31
     */
    public int lengthOfMonth() {
        return month.length(isLeapYear());
    }

    /**
     * Returns the length of the year.
     * <p>
     * This returns the length of the year in days, either 365 or 366.
     *
     * @return 366 if the year is leap, 365 otherwise
     */
    public int lengthOfYear() {
        return (isLeapYear() ? 366 : 365);
    }

    //-----------------------------------------------------------------------
    public YearMonth with(DateTimeAdjuster adjuster) {
        return (YearMonth) adjuster.doAdjustment(this);
    }

    @Override
    public YearMonth with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case MONTH_OF_YEAR: return withMonth((int) newValue);
                case EPOCH_MONTH: return plusMonths(newValue - get(EPOCH_MONTH));
                case YEAR_OF_ERA: return withYear((int) (year < 1 ? 1 - newValue : newValue));
                case YEAR: return withYear((int) newValue);
                case ERA: return (get(ERA) == newValue ? this : withYear(1 - year));
            }
            throw new CalendricalException(field.getName() + " not valid for YearMonth");
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code YearMonth} with the year altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned year-month, from MIN_YEAR to MAX_YEAR
     * @return a {@code YearMonth} based on this year-month with the requested year, not null
     * @throws CalendricalException if the year value is invalid
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
     * @param month  the month-of-year to set in the returned year-month, from 1 (January) to 12 (December)
     * @return a {@code YearMonth} based on this year-month with the requested month, not null
     * @throws CalendricalException if the month-of-year value is invalid
     */
    public YearMonth withMonth(int month) {
        return with(year, Month.of(month));
    }

    //-----------------------------------------------------------------------
    @Override
    public YearMonth plus(long periodAmount, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            switch ((LocalPeriodUnit) unit) {
                case MONTHS: return plusMonths(periodAmount);
                case QUARTER_YEARS: return plusYears(periodAmount / 256).plusMonths((periodAmount % 256) * 3);  // no overflow (256 is multiple of 4)
                case HALF_YEARS: return plusYears(periodAmount / 256).plusMonths((periodAmount % 256) * 6);  // no overflow (256 is multiple of 2)
                case YEARS: return plusYears(periodAmount);
                case DECADES: return plusYears(DateTimes.safeMultiply(periodAmount, 10));
                case CENTURIES: return plusYears(DateTimes.safeMultiply(periodAmount, 100));
                case MILLENNIA: return plusYears(DateTimes.safeMultiply(periodAmount, 1000));
            }
            throw new CalendricalException(unit.getName() + " not valid for YearMonth");
        }
        return unit.doAdd(this, periodAmount);
    }

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
        int newYear = YEAR.checkValidIntValue(DateTimes.floorDiv(calcMonths, 12));
        Month newMonth = Month.of(DateTimes.floorMod(calcMonths, 12) + 1);
        return with(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
    @Override
    public YearMonth minus(long periodAmount, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(periodAmount), unit);
    }

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
        int newYear = YEAR.checkValidIntValue(DateTimes.floorDiv(calcMonths, 12));
        Month newMonth = Month.of(DateTimes.floorMod(calcMonths, 12) + 1);
        return with(newYear, newMonth);
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
        return dayOfMonth >= 1 && dayOfMonth <= lengthOfMonth();
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
     * @throws CalendricalException when the day is invalid for the year-month
     * @see #isValidDay(int)
     */
    public LocalDate atDay(int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTime} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>YearMonth
     * <li>DateTimeBuilder, using {@link LocalDateTimeField#YEAR} and {@link LocalDateTimeField#MONTH_OF_YEAR}
     * <li>Class, returning {@code YearMonth}
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
            return (R) new DateTimeBuilder()
                .addFieldValue(YEAR, year)
                .addFieldValue(MONTH_OF_YEAR, month.getValue());
        } else if (type == Class.class) {
            return (R) YearMonth.class;
        } else if (type == YearMonth.class) {
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
     * Adjusts the specified date-time to have the value of this year-month.
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
        return calendrical.with(YEAR, year).with(MONTH_OF_YEAR, month.getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this year-month to another year-month.
     *
     * @param other  the other year-month to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(YearMonth other) {
        int cmp = DateTimes.safeCompare(year, other.year);
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
    public String toString(CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.print(this);
    }

}
