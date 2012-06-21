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
package javax.time.extended;

import static javax.time.DateTimes.floorDiv;
import static javax.time.DateTimes.floorMod;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.CalendricalParseException;
import javax.time.Clock;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.MonthOfYear;
import javax.time.calendrical.CalendricalAdjuster;
import javax.time.calendrical.CalendricalFormatter;
import javax.time.calendrical.CalendricalObject;
import javax.time.calendrical.DateAdjuster;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeObject;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalDateTimeUnit;
import javax.time.calendrical.PeriodUnit;
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
        implements DateTimeObject, DateAdjuster, Comparable<YearMonth>, Serializable {

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
     * @throws CalendricalException if the year value is invalid
     */
    public static YearMonth of(int year, MonthOfYear monthOfYear) {
        YEAR.checkValidValue(year);
        DateTimes.checkNotNull(monthOfYear, "MonthOfYear must not be null");
        return new YearMonth(year, monthOfYear);
    }

    /**
     * Obtains an instance of {@code YearMonth} from a year and month.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return the year-month, not null
     * @throws CalendricalException if either field value is invalid
     */
    public static YearMonth of(int year, int monthOfYear) {
        return of(year, MonthOfYear.of(monthOfYear));
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
    public static YearMonth from(CalendricalObject calendrical) {
        if (calendrical instanceof YearMonth) {
            return (YearMonth) calendrical;
        }
        return of((int) YEAR.get(calendrical), (int) MONTH_OF_YEAR.get(calendrical));
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
    public static YearMonth parse(String text) {
        return PARSER.parse(text, YearMonth.class);
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
    public static YearMonth parse(String text, CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.parse(text, YearMonth.class);
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
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case MONTH_OF_YEAR: return month.getValue();
                case EPOCH_MONTH: return (year - 1970L) * 12L + month.ordinal();
                case YEAR: return year;
            }
            throw new CalendricalException(field.getName() + " not valid for YearMonth");
        }
        return field.get(this);
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
     *
     * @return the month-of-year, not null
     */
    public MonthOfYear getMonthOfYear() {
        return month;
    }

    //-----------------------------------------------------------------------
    @Override
    public YearMonth with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case MONTH_OF_YEAR: return  withMonthOfYear((int) newValue);
                case EPOCH_MONTH: return with((int) (floorDiv(newValue, 12) + 1970), MonthOfYear.of((int) (floorMod(newValue, 12) + 1)));
                case YEAR: return withYear((int) newValue);
            }
            throw new CalendricalException(field.getName() + " not valid for YearMonth");
        }
        return field.set(this, newValue);
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
     * @param monthOfYear  the month-of-year to set in the returned year-month, from 1 (January) to 12 (December)
     * @return a {@code YearMonth} based on this year-month with the requested month, not null
     * @throws CalendricalException if the month-of-year value is invalid
     */
    public YearMonth withMonthOfYear(int monthOfYear) {
        return with(year, MonthOfYear.of(monthOfYear));
    }

    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this {@code YearMonth} with the specified period added.
//     * <p>
//     * This adds the specified period to this year-month, returning a new year-month.
//     * Before addition, the period is converted to a {@code Period} using
//     * {@link Period#of(PeriodProvider)}.
//     * The calculation only uses the years and months fields.
//     * Other fields are ignored.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param periodProvider  the period to add, not null
//     * @return a {@code YearMonth} based on this year-month with the period added, not null
//     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
//     * @throws ArithmeticException if the result exceeds the supported range
//     */
//    public YearMonth plus(PeriodProvider periodProvider) {
//        Period period = Period.of(periodProvider);
//        return plusMonths(period.totalMonths());
//    }

    //-----------------------------------------------------------------------
    @Override
    public YearMonth plus(long period, PeriodUnit unit) {
        if (unit instanceof LocalDateTimeUnit) {
            switch ((LocalDateTimeUnit) unit) {
                case MONTHS: return plusMonths(period);
                case QUARTER_YEARS: return plusYears(period / 256).plusMonths((period % 256) * 3);  // no overflow (256 is multiple of 4)
                case HALF_YEARS: return plusYears(period / 256).plusMonths((period % 256) * 6);  // no overflow (256 is multiple of 2)
                case YEARS: return plusYears(period);
                case DECADES: return plusYears(DateTimes.safeMultiply(period, 10));
                case CENTURIES: return plusYears(DateTimes.safeMultiply(period, 100));
                case MILLENIA: return plusYears(DateTimes.safeMultiply(period, 1000));
            }
            throw new CalendricalException(unit.getName() + " not valid for YearMonth");
        }
        return unit.add(this, period);
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
        MonthOfYear newMonth = MonthOfYear.of(DateTimes.floorMod(calcMonths, 12) + 1);
        return with(newYear, newMonth);
    }

    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this {@code YearMonth} with the specified period subtracted.
//     * <p>
//     * This subtracts the specified period from this year-month, returning a new year-month.
//     * Before subtraction, the period is converted to a {@code Period} using
//     * {@link Period#of(PeriodProvider)}.
//     * The calculation only uses the years and months fields.
//     * Other fields are ignored.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param periodProvider  the period to subtract, not null
//     * @return a {@code YearMonth} based on this year-month with the period subtracted, not null
//     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
//     * @throws ArithmeticException if the result exceeds the supported range
//     */
//    public YearMonth minus(PeriodProvider periodProvider) {
//        Period period = Period.of(periodProvider);
//        return minusMonths(period.totalMonths());
//    }

    //-----------------------------------------------------------------------
    @Override
    public YearMonth minus(long period, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(period), unit);
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
        MonthOfYear newMonth = MonthOfYear.of(DateTimes.floorMod(calcMonths, 12) + 1);
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
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, not null
     */
    public LocalDate adjustDate(LocalDate date) {
        DateTimes.checkNotNull(date, "LocalDate must not be null");
        if (date.getYear() == year && date.getMonthOfYear() == month) {
            return date;
        }
        return LocalDate.of(year, month, Math.min(date.getDayOfMonth(), lengthInDays()));
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
     * This method exists to fulfill the {@link CalendricalObject} interface.
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

    @Override
    public YearMonth with(CalendricalAdjuster adjuster) {
        if (adjuster instanceof Year) {
            return withYear(((Year) adjuster).getValue());
        } else if (adjuster instanceof MonthOfYear) {
            return withMonthOfYear(((MonthOfYear) adjuster).getValue());
        } else if (adjuster instanceof YearMonth) {
            return ((YearMonth) adjuster);
        }
        DateTimes.checkNotNull(adjuster, "Adjuster must not be null");
        throw new CalendricalException("Unable to adjust YearMonth with " + adjuster.getClass().getSimpleName());
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
