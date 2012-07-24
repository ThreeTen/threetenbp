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

import static javax.time.Month.FEBRUARY;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;

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

/**
 * A month-day in the ISO-8601 calendar system, such as {@code --12-03}.
 * <p>
 * {@code MonthDay} is an immutable calendrical that represents the combination
 * of a year and month. Any field that can be derived from a month and day, such as
 * quarter-of-year, can be obtained.
 * <p>
 * This class does not store or represent a year, time or time-zone.
 * For example, the value "December 3rd" can be stored in a {@code MonthDay}.
 * <p>
 * Since a {@code MonthDay} does not possess a year, the leap day of
 * February 29th is considered valid.
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
public final class MonthDay
        implements DateTime, DateTimeAdjuster, Comparable<MonthDay>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -254395108L;
    /**
     * Parser.
     */
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
        .appendLiteral("--")
        .appendValue(MONTH_OF_YEAR, 2)
        .appendLiteral('-')
        .appendValue(DAY_OF_MONTH, 2)
        .toFormatter();

    /**
     * The month-of-year, not null.
     */
    private final Month month;
    /**
     * The day-of-month.
     */
    private final int day;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current month-day from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current month-day.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current month-day using the system clock, not null
     */
    public static MonthDay now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current month-day from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current month-day.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current month-day, not null
     */
    public static MonthDay now(Clock clock) {
        final LocalDate now = LocalDate.now(clock);  // called once
        return MonthDay.of(now.getMonth(), now.getDayOfMonth());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code MonthDay}.
     * <p>
     * The day-of-month must be valid for the month within a leap year.
     * Hence, for February, day 29 is valid.
     * <p>
     * For example, passing in April and day 31 will throw an exception, as
     * there can never be April 31st in any year. By contrast, passing in
     * February 29th is permitted, as that month-day can sometimes be valid.
     *
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the month-day, not null
     * @throws CalendricalException if the value of any field is out of range
     * @throws CalendricalException if the day-of-month is invalid for the month
     */
    public static MonthDay of(Month month, int dayOfMonth) {
        DateTimes.checkNotNull(month, "Month must not be null");
        DAY_OF_MONTH.checkValidValue(dayOfMonth);
        if (dayOfMonth > month.maxLength()) {
            throw new CalendricalException("Illegal value for DayOfMonth field, value " + dayOfMonth +
                    " is not valid for month " + month.name());
        }
        return new MonthDay(month, dayOfMonth);
    }

    /**
     * Obtains an instance of {@code MonthDay}.
     * <p>
     * The day-of-month must be valid for the month within a leap year.
     * Hence, for month 2 (February), day 29 is valid.
     * <p>
     * For example, passing in month 4 (April) and day 31 will throw an exception, as
     * there can never be April 31st in any year. By contrast, passing in
     * February 29th is permitted, as that month-day can sometimes be valid.
     *
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the month-day, not null
     * @throws CalendricalException if the value of any field is out of range
     * @throws CalendricalException if the day-of-month is invalid for the month
     */
    public static MonthDay of(int month, int dayOfMonth) {
        return of(Month.of(month), dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code MonthDay} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code MonthDay}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the month-day, not null
     * @throws CalendricalException if unable to convert to a {@code MonthDay}
     */
    public static MonthDay from(DateTime calendrical) {
        if (calendrical instanceof MonthDay) {
            return (MonthDay) calendrical;
        }
        return of((int) calendrical.get(MONTH_OF_YEAR), (int) calendrical.get(DAY_OF_MONTH));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code MonthDay} from a text string such as {@code --12-03}.
     * <p>
     * The string must represent a valid month-day.
     * The format is {@code --MM-dd}.
     *
     * @param text  the text to parse such as "--12-03", not null
     * @return the parsed month-day, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static MonthDay parse(CharSequence text) {
        return parse(text, PARSER);
    }

    /**
     * Obtains an instance of {@code MonthDay} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a month-day.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed month-day, not null
     * @throws CalendricalParseException if the text cannot be parsed
     */
    public static MonthDay parse(CharSequence text, CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.parse(text, MonthDay.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, previously validated.
     *
     * @param month  the month-of-year to represent, validated not null
     * @param dayOfMonth  the day-of-month to represent, validated from 1 to 29-31
     */
    private MonthDay(Month month, int dayOfMonth) {
        this.month = month;
        this.day = dayOfMonth;
    }

    /**
     * Returns a copy of this month-day with the new month and day, checking
     * to see if a new object is in fact required.
     *
     * @param newMonth  the month-of-year to represent, validated not null
     * @param newDay  the day-of-month to represent, validated from 1 to 31
     * @return the month-day, not null
     */
    private MonthDay with(Month newMonth, int newDay) {
        if (month == newMonth && day == newDay) {
            return this;
        }
        return new MonthDay(newMonth, newDay);
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case DAY_OF_MONTH: return day;
                case MONTH_OF_YEAR: return month.getValue();
            }
            throw new CalendricalException(field.getName() + " not valid for MonthDay");
        }
        return field.doGet(this);
    }

    //-----------------------------------------------------------------------
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

    /**
     * Gets the day-of-month field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 31
     */
    public int getDayOfMonth() {
        return day;
    }

    //-----------------------------------------------------------------------
    @Override
    public MonthDay with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case DAY_OF_MONTH: return withDayOfMonth((int) newValue);
                case MONTH_OF_YEAR: return  withMonth((int) newValue);
            }
            throw new CalendricalException(field.getName() + " not valid for MonthDay");
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code MonthDay} with the month-of-year altered.
     * <p>
     * If the day-of-month is invalid for the specified month, the day will
     * be adjusted to the last valid day-of-month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to set in the returned month-day, from 1 (January) to 12 (December)
     * @return a {@code MonthDay} based on this month-day with the requested month, not null
     * @throws CalendricalException if the month-of-year value is invalid
     */
    public MonthDay withMonth(int month) {
        return with(Month.of(month));
    }

    /**
    * Returns a copy of this {@code MonthDay} with the month-of-year altered.
    * <p>
    * If the day-of-month is invalid for the specified month, the day will
    * be adjusted to the last valid day-of-month.
    * <p>
    * This instance is immutable and unaffected by this method call.
    *
    * @param month  the month-of-year to set in the returned month-day, not null
    * @return a {@code MonthDay} based on this month-day with the requested month, not null
    */
    public MonthDay with(Month month) {
        DateTimes.checkNotNull(month, "Month must not be null");
        int maxDays = month.maxLength();
        if (day > maxDays) {
            return with(month, maxDays);
        }
        return with(month, day);
    }

    /**
     * Returns a copy of this {@code MonthDay} with the day-of-month altered.
     * <p>
     * If the day-of-month is invalid for the current month, an exception
     * will be thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the return month-day, from 1 to 31
     * @return a {@code MonthDay} based on this month-day with the requested day, not null
     * @throws CalendricalException if the day-of-month value is invalid
     * @throws CalendricalException if the day-of-month is invalid for the month
     */
    public MonthDay withDayOfMonth(int dayOfMonth) {
        DAY_OF_MONTH.checkValidValue(dayOfMonth);
        int maxDays = month.maxLength();
        if (dayOfMonth > maxDays) {
            throw new CalendricalException("Day of month cannot be changed to " + dayOfMonth + " for the month " + month);
        }
        return with(month, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is valid for this month-day.
     * <p>
     * This method checks whether this month and day and the input year form
     * a valid date. This can only return false for February 29th.
     *
     * @param year  the year to validate, an out of range value returns false
     * @return true if the year is valid for this month-day
     * @see Year#isValidMonthDay(MonthDay)
     */
    public boolean isValidYear(int year) {
        return (day == 29 && month == FEBRUARY && Year.isLeap(year) == false) == false;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a date formed from this month-day at the specified year.
     * <p>
     * This method merges {@code this} and the specified year to form an
     * instance of {@code LocalDate}.
     * <pre>
     * LocalDate date = monthDay.atYear(year);
     * </pre>
     * A month-day of February 29th will be adjusted to February 28th in the resulting
     * date if the year is not a leap year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to use, from MIN_YEAR to MAX_YEAR
     * @return the local date formed from this month-day and the specified year, not null
     * @see Year#atMonthDay(MonthDay)
     */
    public LocalDate atYear(int year) {
        return LocalDate.of(year, month, isValidYear(year) ? day : 28);
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTime} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>MonthDay
     * <li>DateTimeBuilder, using {@link LocalDateTimeField#MONTH_OF_YEAR} and {@link LocalDateTimeField#DAY_OF_MONTH}
     * <li>Class, returning {@code MonthDay}
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
                .addFieldValue(MONTH_OF_YEAR, month.getValue())
                .addFieldValue(DAY_OF_MONTH, day);
        } else if (type == Class.class) {
            return (R) MonthDay.class;
        } else if (type == MonthDay.class) {
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
     * Adjusts the specified date-time to have the value of this month-day.
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
        int day = this.day;
        LocalDate date = calendrical.extract(LocalDate.class);
        if (date != null) {
            day = isValidYear(date.getYear()) ? day : 28;
        }
        return calendrical.with(MONTH_OF_YEAR, month.getValue()).with(DAY_OF_MONTH, day);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this month-day to another month-day.
     *
     * @param other  the other month-day to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(MonthDay other) {
        int cmp = month.compareTo(other.month);
        if (cmp == 0) {
            cmp = DateTimes.safeCompare(day, other.day);
        }
        return cmp;
    }

    /**
     * Is this month-day after the specified month-day.
     *
     * @param other  the other month-day to compare to, not null
     * @return true if this is after the specified month-day
     */
    public boolean isAfter(MonthDay other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this month-day before the specified month-day.
     *
     * @param other  the other month-day to compare to, not null
     * @return true if this point is before the specified month-day
     */
    public boolean isBefore(MonthDay other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this month-day is equal to another month-day.
     * <p>
     * The comparison is based on the time-line position of the month-day within a year.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other month-day
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MonthDay) {
            MonthDay other = (MonthDay) obj;
            return month == other.month && day == other.day;
        }
        return false;
    }

    /**
     * A hash code for this month-day.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return (month.getValue() << 6) + day;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this month-day as a {@code String}, such as {@code --12-03}.
     * <p>
     * The output will be in the format {@code --MM-dd}:
     *
     * @return a string representation of this month-day, not null
     */
    @Override
    public String toString() {
        int monthValue = month.getValue();
        int dayValue = day;
        return new StringBuilder(10).append("--")
            .append(monthValue < 10 ? "0" : "").append(monthValue)
            .append(dayValue < 10 ? "-0" : "-").append(dayValue)
            .toString();
    }

    /**
     * Outputs this month-day as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted month-day string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    public String toString(CalendricalFormatter formatter) {
        DateTimes.checkNotNull(formatter, "CalendricalFormatter must not be null");
        return formatter.print(this);
    }

}
