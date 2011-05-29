/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.Duration;
import javax.time.Instant;
import javax.time.MathUtils;
import javax.time.calendar.LocalTime.Overflow;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatters;

/**
 * A date-time without a time-zone in the ISO-8601 calendar system,
 * such as {@code 2007-12-03T10:15:30}.
 * <p>
 * {@code LocalDateTime} is an immutable calendrical that represents a date-time, often
 * viewed as year-month-day-hour-minute-second. This object can also access other
 * fields such as day-of-year, day-of-week and week-of-year.
 * <p>
 * This class stores all date and time fields, to a precision of nanoseconds.
 * It does not store or represent a time-zone. Thus, for example, the value
 * "2nd October 2007 at 13:45.30.123456789" can be stored in an {@code LocalDateTime}.
 * <p>
 * LocalDateTime is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class LocalDateTime
        implements Calendrical, DateTimeProvider, Comparable<LocalDateTime>,
                    CalendricalMatcher, DateAdjuster, TimeAdjuster, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The date part.
     */
    private final LocalDate date;
    /**
     * The time part.
     */
    private final LocalTime time;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date-time.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock, not null
     */
    public static LocalDateTime now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Obtains the current date-time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date-time.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date-time, not null
     */
    public static LocalDateTime now(Clock clock) {
        ISOChronology.checkNotNull(clock, "Clock must not be null");
        // inline OffsetDateTime factory to avoid creating object and InstantProvider checks
        final Instant now = clock.instant();  // called once
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long localSeconds = now.getEpochSecond() + offset.getAmountSeconds();  // overflow caught later
        return create(localSeconds, now.getNanoOfSecond());
    }

    /**
     * Obtains an instance of {@code LocalDateTime} using seconds from the
     * local epoch of 1970-01-01T00:00:00.
     * <p>
     * The nanosecond field is set to zero.
     *
     * @param localSeconds  the number of seconds from the local epoch of 1970-01-01T00:00:00
     * @param nanoOfSecond  the nanosecond within the second, from 0 to 999,999,999
     * @return the local date-time, not null
     * @throws CalendricalException if the instant exceeds the supported date range
     */
    static LocalDateTime create(long localSeconds, int nanoOfSecond) {
        long yearZeroDays = MathUtils.floorDiv(localSeconds, ISOChronology.SECONDS_PER_DAY) + ISOChronology.DAYS_0000_TO_1970;
        int secsOfDay = MathUtils.floorMod(localSeconds, ISOChronology.SECONDS_PER_DAY);
        LocalDate date = LocalDate.ofYearZeroDay(yearZeroDays);
        LocalTime time = LocalTime.ofSecondOfDay(secsOfDay, nanoOfSecond);
        return LocalDateTime.of(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDateTime} from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The day must be valid for the year and month or an exception will be thrown.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the local date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime ofMidnight(int year, MonthOfYear monthOfYear, int dayOfMonth) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        return new LocalDateTime(date, LocalTime.MIDNIGHT);
    }

    /**
     * Obtains an instance of {@code LocalDateTime} from year, month and
     * day with the time set to midnight at the start of day.
     * <p>
     * The day must be valid for the year and month or an exception will be thrown.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the local date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime ofMidnight(int year, int monthOfYear, int dayOfMonth) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        return new LocalDateTime(date, LocalTime.MIDNIGHT);
    }

    /**
     * Obtains an instance of {@code LocalDateTime} from a date with the
     * time set to midnight at the start of day.
     * <p>
     * The day must be valid for the year and month or an exception will be thrown.
     * <p>
     * The time fields will be set to zero by this factory method.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the local date-time, not null
     */
    public static LocalDateTime ofMidnight(DateProvider dateProvider) {
        LocalDate date = LocalDate.of(dateProvider);
        return new LocalDateTime(date, LocalTime.MIDNIGHT);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * The day must be valid for the year and month or an exception will be thrown.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return the local date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime of(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * The day must be valid for the year and month or an exception will be thrown.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return the local date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime of(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour, minute, second and nanosecond.
     * <p>
     * The day must be valid for the year and month or an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return the local date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime of(
            int year, MonthOfYear monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * The day must be valid for the year and month or an exception will be thrown.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return the local date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime of(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * The day must be valid for the year and month or an exception will be thrown.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return the local date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime of(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour, minute, second and nanosecond.
     * <p>
     * The day must be valid for the year and month or an exception will be thrown.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return the local date-time, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static LocalDateTime of(
            int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        LocalTime time = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDateTime} from a date and time.
     *
     * @param dateProvider  the date provider to use, not null
     * @param timeProvider  the time provider to use, not null
     * @return the local date-time, not null
     */
    public static LocalDateTime of(DateProvider dateProvider, TimeProvider timeProvider) {
        LocalDate date = LocalDate.of(dateProvider);
        LocalTime time = LocalTime.of(timeProvider);
        return new LocalDateTime(date, time);
    }

    /**
     * Obtains an instance of {@code LocalTime} from a date-time provider.
     * <p>
     * The purpose of this method is to convert a {@code DateTimeProvider}
     * to a {@code LocalDateTime} in the safest possible way. Specifically,
     * the means checking whether the input parameter is null and
     * whether the result of the provider is null.
     *
     * @param dateTimeProvider  the date-time provider to use, not null
     * @return the local date-time, not null
     */
    public static LocalDateTime of(DateTimeProvider dateTimeProvider) {
        ISOChronology.checkNotNull(dateTimeProvider, "DateTimeProvider must not be null");
        LocalDateTime result = dateTimeProvider.toLocalDateTime();
        ISOChronology.checkNotNull(result, "DateTimeProvider implementation must not return null");
        return result;
    }

//    /**
//     * Obtains an instance of {@code LocalDateTime} from an {@code InstantProvider} using
//     * an offset to define the correct local time.
//     * <p>
//     * In order to calculate the local date-time from an instant the offset is needed.
//     * This is the same as calling {@code OffsetDateTime.of(instant, offset).toLocalDateTime()}
//     * but saves an object creation.
//     *
//     * @param instantProvider  the instant to convert, not null
//     * @param offset  the zone offset, not null
//     * @return the offset date-time, not null
//     * @throws CalendarConversionException if the instant exceeds the supported date range
//     */
//    public static LocalDateTime ofInstant(InstantProvider instantProvider, ZoneOffset offset) {
//        Instant instant = Instant.of(instantProvider);
//        ISOChronology.checkNotNull(offset, "ZoneOffset must not be null");
//        long localSeconds = instant.getEpochSecond() + offset.getAmountSeconds();  // overflow caught later
//        return LocalDateTime.create(localSeconds, instant.getNanoOfSecond());
//    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code LocalDateTime} from a text string such as {@code 2007-12-03T10:15:30}.
     * <p>
     * The following formats are accepted in ASCII:
     * <ul>
     * <li>{@code {Year}-{MonthOfYear}-{DayOfMonth}T{Hour}:{Minute}}
     * <li>{@code {Year}-{MonthOfYear}-{DayOfMonth}T{Hour}:{Minute}:{Second}}
     * <li>{@code {Year}-{MonthOfYear}-{DayOfMonth}T{Hour}:{Minute}:{Second}.{NanosecondFraction}}
     * </ul>
     * <p>
     * The year has between 4 and 10 digits with values from MIN_YEAR to MAX_YEAR.
     * If there are more than 4 digits then the year must be prefixed with the plus symbol.
     * Negative years are allowed, but not negative zero.
     * <p>
     * The month-of-year has 2 digits with values from 1 to 12.
     * <p>
     * The day-of-month has 2 digits with values from 1 to 31 appropriate to the month.
     * <p>
     * The hour has 2 digits with values from 0 to 23.
     * The minute has 2 digits with values from 0 to 59.
     * The second has 2 digits with values from 0 to 59.
     * The nanosecond fraction has from 1 to 9 digits with values from 0 to 999,999,999.
     *
     * @param text  the text to parse such as '2007-12-03T10:15:30', not null
     * @return the parsed local date-time, not null
     * @throws CalendricalException if the text cannot be parsed
     */
    public static LocalDateTime parse(String text) {
        return DateTimeFormatters.isoLocalDateTime().parse(text, rule());
    }

    /**
     * Obtains an instance of {@code LocalDateTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a date-time.
     *
     * @param text  the text to parse, not null
     * @param formatter  the formatter to use, not null
     * @return the parsed local date-time, not null
     * @throws UnsupportedOperationException if the formatter cannot parse
     * @throws CalendricalException if the text cannot be parsed
     */
    public static LocalDateTime parse(String text, DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.parse(text, rule());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param date  the date part of the date-time, not null
     * @param time  the time part of the date-time, not null
     */
    private LocalDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Returns a copy of this date-time with the new date and time, checking
     * to see if a new object is in fact required.
     *
     * @param newDate  the date of the new date-time, not null
     * @param newTime  the time of the new date-time, not null
     * @return the date-time, not null
     */
    private LocalDateTime with(LocalDate newDate, LocalTime newTime) {
        if (date == newDate && time == newTime) {
            return this;
        }
        return new LocalDateTime(newDate, newTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that this date-time uses, which is the ISO calendar system.
     *
     * @return the ISO chronology, not null
     */
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this date-time then
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> rule) {
        if (rule instanceof CalendricalObjectRule<?>) {
            switch (((CalendricalObjectRule<?>) rule).code) {
                case CalendricalObjectRule.LD: return (T) toLocalDate();
                case CalendricalObjectRule.LT: return (T) toLocalTime();
                case CalendricalObjectRule.LDT: return (T) this;
                case CalendricalObjectRule.CHRONO: return (T) ISOChronology.INSTANCE;
            }
            return null;
        }
        return rule.derive(this);
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
     * @return the month-of-year, not null
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
     * @return the day-of-week, not null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    public int getHourOfDay() {
        return time.getHourOfDay();
    }

    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return time.getMinuteOfHour();
    }

    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return time.getSecondOfMinute();
    }

    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return time.getNanoOfSecond();
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
    public boolean isLeapYear() {
        return date.isLeapYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDateTime} with the date altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the date in various ways.
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date to the last day of the month.
     * <p>
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalDateTime} based on this date-time with the date adjusted, not null
     * @throws NullPointerException if the adjuster returned null
     */
    public LocalDateTime with(DateAdjuster adjuster) {
        return with(date.with(adjuster), time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the time altered using the adjuster.
     * <p>
     * Adjusters can be used to alter the time in various ways.
     * A simple adjuster might simply set the one of the fields, such as the hour field.
     * A more complex adjuster might set the time to end of the working day.
     * <p>
     * The date does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalDateTime} based on this date-time with the time adjusted, not null
     * @throws IllegalArgumentException if the adjuster returned null
     */
    public LocalDateTime with(TimeAdjuster adjuster) {
        return with(date, time.with(adjuster));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDateTime} with the year altered.
     * If the resulting {@code LocalDateTime} is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This method does the same as {@code withYear(year, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date, from MIN_YEAR to MAX_YEAR
     * @return a {@code LocalDateTime} based on this date-time with the requested year, not null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public LocalDateTime withYear(int year) {
        return with(date.withYear(year), time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the year altered.
     * If the resulting {@code LocalDateTime} is invalid, it will be resolved using {@code dateResolver}.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to set in the returned date, from MIN_YEAR to MAX_YEAR
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a {@code LocalDateTime} based on this date-time with the requested year, not null
     * @throws IllegalCalendarFieldValueException if the year value is invalid
     */
    public LocalDateTime withYear(int year, DateResolver dateResolver) {
        return with(date.withYear(year, dateResolver), time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the month-of-year altered.
     * If the resulting {@code LocalDateTime} is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This method does the same as {@code withMonthOfYear(monthOfYear, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, from 1 (January) to 12 (December)
     * @return a {@code LocalDateTime} based on this date-time with the requested month, not null
     * @throws IllegalCalendarFieldValueException if the month-of-year value is invalid
     */
    public LocalDateTime withMonthOfYear(int monthOfYear) {
        return with(date.withMonthOfYear(monthOfYear), time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the month-of-year altered.
     * If the resulting {@code LocalDateTime} is invalid, it will be resolved using {@code dateResolver}.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, from 1 (January) to 12 (December)
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a {@code LocalDateTime} based on this date-time with the requested month, not null
     * @throws IllegalCalendarFieldValueException if the month-of-year value is invalid
     */
    public LocalDateTime withMonthOfYear(int monthOfYear, DateResolver dateResolver) {
        return with(date.withMonthOfYear(monthOfYear, dateResolver), time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the month-of-year altered.
     * If the resulting {@code LocalDateTime} is invalid, it will be resolved using {@link DateResolvers#previousValid()}.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This method does the same as {@code with(monthOfYear, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, not null
     * @return a {@code LocalDateTime} based on this date-time with the requested month, not null
     */
    public LocalDateTime with(MonthOfYear monthOfYear) {
        return with(date.with(monthOfYear), time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the month-of-year altered.
     * If the resulting {@code LocalDateTime} is invalid, it will be resolved using {@code dateResolver}.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set in the returned date, not null
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a {@code LocalDateTime} based on this date-time with the requested month, not null
     */
    public LocalDateTime with(MonthOfYear monthOfYear, DateResolver dateResolver) {
        return with(date.with(monthOfYear, dateResolver), time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the day-of-month altered.
     * If the resulting {@code LocalDateTime} is invalid, an exception is thrown.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 28-31
     * @return a {@code LocalDateTime} based on this date-time with the requested day, not null
     * @throws IllegalCalendarFieldValueException if the day-of-month value is invalid
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public LocalDateTime withDayOfMonth(int dayOfMonth) {
        return with(date.withDayOfMonth(dayOfMonth), time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the day-of-month altered.
     * If the resulting {@code LocalDateTime} is invalid, it will be resolved using {@code dateResolver}.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set in the returned date, from 1 to 31
     * @param dateResolver the DateResolver to be used if the resulting date would be invalid
     * @return a {@code LocalDateTime} based on this date-time with the requested day, not null
     * @throws IllegalCalendarFieldValueException if the day-of-month value is invalid
     */
    public LocalDateTime withDayOfMonth(int dayOfMonth, DateResolver dateResolver) {
        return with(date.withDayOfMonth(dayOfMonth, dateResolver), time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the day-of-year altered.
     * If the resulting {@code LocalDateTime} is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set in the returned date, from 1 to 365-366
     * @return a {@code LocalDateTime} based on this date with the requested day, not null
     * @throws IllegalCalendarFieldValueException if the day-of-year value is invalid
     * @throws InvalidCalendarFieldException if the day-of-year is invalid for the year
     */
    public LocalDateTime withDayOfYear(int dayOfYear) {
        return with(date.withDayOfYear(dayOfYear), time);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDateTime} with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return a {@code LocalDateTime} based on this date-time with the requested date, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public LocalDateTime withDate(int year, MonthOfYear monthOfYear, int dayOfMonth) {
        if (year == getYear() &&
                monthOfYear == getMonthOfYear() &&
                dayOfMonth == getDayOfMonth()) {
            return this;
        }
        LocalDate newDate = LocalDate.of(year, monthOfYear, dayOfMonth);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return a {@code LocalDateTime} based on this date-time with the requested date, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public LocalDateTime withDate(int year, int monthOfYear, int dayOfMonth) {
        if (year == getYear() &&
                monthOfYear == getMonthOfYear().getValue() &&
                dayOfMonth == getDayOfMonth()) {
            return this;
        }
        LocalDate newDate = LocalDate.of(year, monthOfYear, dayOfMonth);
        return with(newDate, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDateTime} with the hour-of-day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @return a {@code LocalDateTime} based on this date-time with the requested hour, not null
     * @throws IllegalCalendarFieldValueException if the hour value is invalid
     */
    public LocalDateTime withHourOfDay(int hourOfDay) {
        LocalTime newTime = time.withHourOfDay(hourOfDay);
        return with(date, newTime);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the minute-of-hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return a {@code LocalDateTime} based on this date-time with the requested minute, not null
     * @throws IllegalCalendarFieldValueException if the minute value is invalid
     */
    public LocalDateTime withMinuteOfHour(int minuteOfHour) {
        LocalTime newTime = time.withMinuteOfHour(minuteOfHour);
        return with(date, newTime);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the second-of-minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return a {@code LocalDateTime} based on this date-time with the requested second, not null
     * @throws IllegalCalendarFieldValueException if the second value is invalid
     */
    public LocalDateTime withSecondOfMinute(int secondOfMinute) {
        LocalTime newTime = time.withSecondOfMinute(secondOfMinute);
        return with(date, newTime);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the nano-of-second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code LocalDateTime} based on this date-time with the requested nanosecond, not null
     * @throws IllegalCalendarFieldValueException if the nano value is invalid
     */
    public LocalDateTime withNanoOfSecond(int nanoOfSecond) {
        LocalTime newTime = time.withNanoOfSecond(nanoOfSecond);
        return with(date, newTime);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int,int)} and sets
     * the second and nanosecond fields to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @return a {@code LocalDateTime} based on this date-time with the requested time, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public LocalDateTime withTime(int hourOfDay, int minuteOfHour) {
        return withTime(hourOfDay, minuteOfHour, 0, 0);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int,int)} and sets
     * the nanosecond fields to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @return a {@code LocalDateTime} based on this date-time with the requested time, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public LocalDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        return withTime(hourOfDay, minuteOfHour, secondOfMinute, 0);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour-of-day to represent, from 0 to 23
     * @param minuteOfHour  the minute-of-hour to represent, from 0 to 59
     * @param secondOfMinute  the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano-of-second to represent, from 0 to 999,999,999
     * @return a {@code LocalDateTime} based on this date-time with the requested time, not null
     * @throws IllegalCalendarFieldValueException if any field value is invalid
     */
    public LocalDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond) {
        if (hourOfDay == getHourOfDay() && minuteOfHour == getMinuteOfHour() &&
                secondOfMinute == getSecondOfMinute() && nanoOfSecond == getNanoOfSecond()) {
            return this;
        }
        LocalTime newTime = LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return with(date, newTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period added.
     * <p>
     * This adds the specified period to this date-time, returning a new date-time.
     * Before addition, the period is converted to a {@code Period} using
     * {@link Period#of(PeriodProvider)}.
     * <p>
     * The detailed rules for the addition effectively treat the date and time parts of
     * this date-time completely separately during the calculation.
     * <p>
     * The rules are expressed in four steps:
     * <ol>
     * <li>Add the date part of the period to the date part of this date-time
     * using {@link LocalDate#plus(PeriodProvider)} - which has some complex rules</li>
     * <li>Add the time part of the period to the time part of this date-time using
     * {@link LocalTime#plusWithOverflow(int, int, int, long)}</li>
     * <li>Add the overflow days from the time calculation to the calculated date</li>
     * <li>Combine the new date and time parts to form the result</li>
     * </ol>
     * <p>
     * The effect of this definition is that time periods are always evenly spaced.
     * For example, adding 5 hours will always result in a date-time one hour later
     * than adding 4 hours. However, another effect of the definition is that adding
     * 24 hour periods is not the same as adding 1 day periods. See the rules of
     * {@link LocalDate#plus(PeriodProvider) date addition} to understand why.
     * <p>
     * For example, this table shows what happens when for various inputs and periods:
     * <pre>
     *   2010-01-30T00:00 plus P1M2DT-5H  = 2010-03-01T19:00
     *   2010-01-30T00:00 plus P1M2D      = 2010-03-02T00:00
     *   2010-01-30T00:00 plus P1M2DT4H   = 2010-03-02T04:00
     *   
     *   2010-01-30T00:00 plus P1M1DT-5H  = 2010-02-28T19:00
     *   2010-01-30T00:00 plus P1M1D      = 2010-03-01T00:00
     *   2010-01-30T00:00 plus P1M1DT4H   = 2010-03-01T04:00
     *   
     *   2010-01-30T00:00 plus P1MT-5H    = 2010-02-27T19:00
     *   2010-01-30T00:00 plus P1M        = 2010-02-28T00:00
     *   2010-01-30T00:00 plus P1MT4H     = 2010-02-28T04:00
     *   
     *   2010-01-30T00:00 plus P1M-1DT-5H = 2010-02-27T19:00
     *   2010-01-30T00:00 plus P1M-1D     = 2010-02-28T00:00
     *   2010-01-30T00:00 plus P1M-1DT4H  = 2010-02-28T04:00
     *   
     *   2010-01-30T00:00 plus P1M-2DT-5H = 2010-02-27T19:00
     *   2010-01-30T00:00 plus P1M-2D     = 2010-02-28T00:00
     *   2010-01-30T00:00 plus P1M-2DT4H  = 2010-02-28T04:00
     *   
     *   2010-01-30T00:00 plus P1M-3DT-5H = 2010-02-26T19:00
     *   2010-01-30T00:00 plus P1M-3D     = 2010-02-27T00:00
     *   2010-01-30T00:00 plus P1M-3DT4H  = 2010-02-27T04:00
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a {@code LocalDateTime} based on this date-time with the period added, not null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plus(PeriodProvider periodProvider) {
        Period period = Period.of(periodProvider);
        LocalDate newDate = date.plus(period);
        Overflow overflow = time.plusWithOverflow(
            period.getHours(), period.getMinutes(), period.getSeconds(), period.getNanos());
        LocalDateTime result = overflow.toLocalDateTime(newDate);
        return (result.equals(this) ? this : result);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified duration added.
     * <p>
     * This adds the specified duration to this date-time, returning a new date-time.
     * <p>
     * The calculation is equivalent to using {@link #plusSeconds(long)} and
     * {@link #plusNanos(long)} on the two parts of the duration.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a {@code LocalDateTime} based on this date-time with the duration added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plus(Duration duration) {
        return plusSeconds(duration.getSeconds()).plusNanos(duration.getNanoOfSecond());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in years added.
     * <p>
     * This method adds the specified amount to the years field in three steps:
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
     * @return a {@code LocalDateTime} based on this date-time with the years added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusYears(long, javax.time.calendar.DateResolver)
     */
    public LocalDateTime plusYears(long years) {
        LocalDate newDate = date.plusYears(years);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in years added.
     * <p>
     * This method adds the specified amount to the years field in three steps:
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
     * @return a {@code LocalDateTime} based on this date-time with the years added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusYears(long years, DateResolver dateResolver) {
        LocalDate newDate = date.plusYears(years, dateResolver);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in three steps:
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
     * @return a {@code LocalDateTime} based on this date-time with the months added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #plusMonths(long, javax.time.calendar.DateResolver)
     */
    public LocalDateTime plusMonths(long months) {
        LocalDate newDate = date.plusMonths(months);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in months added.
     * <p>
     * This method adds the specified amount to the months field in three steps:
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
     * @return a {@code LocalDateTime} based on this date-time with the months added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusMonths(long months, DateResolver dateResolver) {
        LocalDate newDate = date.plusMonths(months, dateResolver);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in weeks added.
     * <p>
     * This method adds the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the weeks added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusWeeks(long weeks) {
        LocalDate newDate = date.plusWeeks(weeks);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in days added.
     * <p>
     * This method adds the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the days added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusDays(long days) {
        LocalDate newDate = date.plusDays(days);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the hours added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusHours(long hours) {
        LocalTime.Overflow overflow = time.plusWithOverflow(hours, 0, 0, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return with(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the minutes added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusMinutes(long minutes) {
        LocalTime.Overflow overflow = time.plusWithOverflow(0, minutes, 0, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return with(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the seconds added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusSeconds(long seconds) {
        LocalTime.Overflow overflow = time.plusWithOverflow(0, 0, seconds, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return with(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the nanoseconds added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime plusNanos(long nanos) {
        LocalTime.Overflow overflow = time.plusWithOverflow(0, 0, 0, nanos);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return with(newDate, overflow.getResultTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period subtracted.
     * <p>
     * This subtracts the specified period from this date-time, returning a new date-time.
     * Before subtraction, the period is converted to a {@code Period} using
     * {@link Period#of(PeriodProvider)}.
     * <p>
     * The detailed rules for the subtraction effectively treat the date and time parts of
     * this date-time completely separately during the calculation.
     * <p>
     * The rules are expressed in four steps:
     * <ol>
     * <li>Subtract the date part of the period from the date part of this date-time
     * using {@link LocalDate#minus(PeriodProvider)} - which has some complex rules</li>
     * <li>Subtract the time part of the period from the time part of this date-time using
     * {@link LocalTime#minusWithOverflow(int, int, int, long)}</li>
     * <li>Subtract the overflow days from the time calculation from the calculated date</li>
     * <li>Combine the new date and time parts to form the result</li>
     * </ol>
     * <p>
     * The effect of this definition is that time periods are always evenly spaced.
     * For example, subtracting 5 hours will always result in a date-time one hour earlier
     * than adding 4 hours. However, another effect of the definition is that subtracting
     * 24 hour periods is not the same as subtracting 1 day periods. See the rules of
     * {@link LocalDate#minus(PeriodProvider) date subtraction} to understand why.
     * <p>
     * For example, this table shows what happens when for various inputs and periods:
     * <pre>
     *   2010-03-30T00:00 minus P1M3DT-5H  = 2010-02-27T05:00
     *   2010-03-30T00:00 minus P1M3D      = 2010-02-27T00:00
     *   2010-03-30T00:00 minus P1M3DT4H   = 2010-02-26T20:00
     *   
     *   2010-03-30T00:00 minus P1M2DT-5H  = 2010-02-28T05:00
     *   2010-03-30T00:00 minus P1M2D      = 2010-02-28T00:00
     *   2010-03-30T00:00 minus P1M2DT4H   = 2010-02-27T20:00
     *   
     *   2010-03-30T00:00 minus P1M1DT-5H  = 2010-02-28T05:00
     *   2010-03-30T00:00 minus P1M1D      = 2010-02-28T00:00
     *   2010-03-30T00:00 minus P1M1DT4H   = 2010-02-27T20:00
     *   
     *   2010-03-30T00:00 minus P1MT-5H    = 2010-02-28T05:00
     *   2010-03-30T00:00 minus P1M        = 2010-02-28T00:00
     *   2010-03-30T00:00 minus P1MT4H     = 2010-02-27T20:00
     *   
     *   2010-03-30T00:00 minus P1M-1DT-5H = 2010-03-01T05:00
     *   2010-03-30T00:00 minus P1M-1D     = 2010-03-01T00:00
     *   2010-03-30T00:00 minus P1M-1DT4H  = 2010-02-28T20:00
     *   
     *   2010-03-30T00:00 minus P1M-2DT-5H = 2010-03-02T05:00
     *   2010-03-30T00:00 minus P1M-2D     = 2010-03-02T00:00
     *   2010-03-30T00:00 minus P1M-2DT4H  = 2010-03-01T20:00
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a {@code LocalDateTime} based on this date-time with the period subtracted, not null
     * @throws CalendricalException if the specified period cannot be converted to a {@code Period}
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minus(PeriodProvider periodProvider) {
        Period period = Period.of(periodProvider);
        LocalDate newDate = date.minus(period);
        Overflow overflow = time.minusWithOverflow(
            period.getHours(), period.getMinutes(), period.getSeconds(), period.getNanos());
        LocalDateTime result = overflow.toLocalDateTime(newDate);
        return (result.equals(this) ? this : result);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified duration subtracted.
     * <p>
     * This subtracts the specified duration from this date-time, returning a new date-time.
     * <p>
     * The calculation is equivalent to using {@link #minusSeconds(long)} and
     * {@link #minusNanos(long)} on the two parts of the duration.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a {@code LocalDateTime} based on this date-time with the duration subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minus(Duration duration) {
        return minusSeconds(duration.getSeconds()).minusNanos(duration.getNanoOfSecond());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in years subtracted.
     * <p>
     * This method subtracts the specified amount from the years field in three steps:
     * <ol>
     * <li>Subtract the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This method does the same as {@code minusYears(years, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the years subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusYears(long, javax.time.calendar.DateResolver)
     */
    public LocalDateTime minusYears(long years) {
        LocalDate newDate = date.minusYears(years);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in years subtracted.
     * <p>
     * This method subtracts the specified amount from the years field in three steps:
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
     * @return a {@code LocalDateTime} based on this date-time with the years subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusYears(long years, DateResolver dateResolver) {
        LocalDate newDate = date.minusYears(years, dateResolver);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in months subtracted.
     * <p>
     * This method subtracts the specified amount from the months field in three steps:
     * <ol>
     * <li>Subtract the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This method does the same as {@code minusMonts(months, DateResolvers.previousValid())}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the months subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     * @see #minusMonths(long, javax.time.calendar.DateResolver)
     */
    public LocalDateTime minusMonths(long months) {
        LocalDate newDate = date.minusMonths(months);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in months subtracted.
     * <p>
     * This method subtracts the specified amount from the months field in three steps:
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
     * @return a {@code LocalDateTime} based on this date-time with the months subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusMonths(long months, DateResolver dateResolver) {
        LocalDate newDate = date.minusMonths(months, dateResolver);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks from the days field decrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the weeks subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusWeeks(long weeks) {
        LocalDate newDate = date.minusWeeks(weeks);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in days subtracted.
     * <p>
     * This method subtracts the specified amount from the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 minus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the days subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusDays(long days) {
        LocalDate newDate = date.minusDays(days);
        return with(newDate, time);
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the hours subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusHours(long hours) {
        LocalTime.Overflow overflow = time.minusWithOverflow(hours, 0, 0, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return with(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the minutes subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusMinutes(long minutes) {
        LocalTime.Overflow overflow = time.minusWithOverflow(0, minutes, 0, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return with(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the seconds subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusSeconds(long seconds) {
        LocalTime.Overflow overflow = time.minusWithOverflow(0, 0, seconds, 0);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return with(newDate, overflow.getResultTime());
    }

    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period in nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to subtract, may be negative
     * @return a {@code LocalDateTime} based on this date-time with the nanoseconds subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public LocalDateTime minusNanos(long nanos) {
        LocalTime.Overflow overflow = time.minusWithOverflow(0, 0, 0, nanos);
        LocalDate newDate = date.plusDays(overflow.getOverflowDays());
        return with(newDate, overflow.getResultTime());
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this {@code LocalDateTime} matches the specified matcher.
     * <p>
     * Matchers can be used to query the date-time.
     * A simple matcher might simply compare one of the fields, such as the year field.
     * A more complex matcher might check if the date is the last day of the month.
     *
     * @param matcher  the matcher to use, not null
     * @return true if this date-time matches the matcher, false otherwise
     */
    public boolean matches(CalendricalMatcher matcher) {
        return matcher.matchesCalendrical(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the date-time extracted from the calendrical matches this.
     * <p>
     * This method implements the {@code CalendricalMatcher} interface.
     * It is intended that applications use {@link #matches} rather than this method.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        return this.equals(calendrical.get(rule()));
    }

    /**
     * Adjusts a date to have the value of the date part of this object.
     * <p>
     * This method implements the {@code DateAdjuster} interface.
     * It is intended that applications use {@link #with(DateAdjuster)} rather than this method.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, not null
     */
    public LocalDate adjustDate(LocalDate date) {
        return this.date.adjustDate(date);
    }

    /**
     * Adjusts a time to have the value of the time part of this object.
     * <p>
     * This method implements the {@code TimeAdjuster} interface.
     * It is intended that applications use {@link #with(TimeAdjuster)} rather than this method.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, not null
     */
    public LocalTime adjustTime(LocalTime time) {
        return this.time.adjustTime(time);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an offset date-time formed from this date-time and the specified offset.
     * <p>
     * This merges the two objects - {@code this} and the specified offset -
     * to form an instance of {@code OffsetDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the offset to use, not null
     * @return the offset date-time formed from this date-time and the specified offset, not null
     */
    public OffsetDateTime atOffset(ZoneOffset offset) {
        return OffsetDateTime.of(this, offset);
    }

    /**
     * Returns a zoned date-time formed from this date-time and the specified time-zone.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. When this method converts the date to a date-time it adjusts
     * the time and offset according to the {@link ZoneResolvers#postTransition()} rules.
     * This selects the date-time immediately after a gap and the later offset in overlaps.
     * <p>
     * Finer control over gaps and overlaps is available in two ways.
     * If you simply want to use the earlier offset at overlaps then call
     * {@link ZonedDateTime#withEarlierOffsetAtOverlap()} immediately after this method.
     * Alternately, pass a specific resolver to {@link #atZone(ZoneId, ZoneResolver)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @return the zoned date-time formed from this date-time, not null
     */
    public ZonedDateTime atZone(ZoneId zone) {
        return ZonedDateTime.of(this, zone, ZoneResolvers.postTransition());
    }

    /**
     * Returns a zoned date-time formed from this date-time and the specified time-zone
     * taking control of what occurs in time-line gaps and overlaps.
     * <p>
     * Time-zone rules, such as daylight savings, mean that not every time on the
     * local time-line exists. When this method converts the date to a date-time it adjusts
     * the time and offset according to the specified zone resolver.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param zone  the time-zone to use, not null
     * @param resolver  the zone resolver to use for gaps and overlaps, not null
     * @return the zoned date-time formed from this date-time, not null
     * @throws CalendricalException if the date-time cannot be resolved
     */
    public ZonedDateTime atZone(ZoneId zone, ZoneResolver resolver) {
        return ZonedDateTime.of(this, zone, resolver);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to a {@code LocalDate}.
     *
     * @return a LocalDate representing the date fields of this date-time, not null
     */
    public LocalDate toLocalDate() {
        return date;
    }

    /**
     * Converts this date-time to a {@code LocalTime}.
     *
     * @return a LocalTime representing the time fields of this date-time, not null
     */
    public LocalTime toLocalTime() {
        return time;
    }

    /**
     * Converts this date-time to a {@code LocalDateTime},
     * trivially returning {@code this}.
     *
     * @return {@code this}, not null
     */
    public LocalDateTime toLocalDateTime() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code LocalDateTime} to another date-time.
     * <p>
     * The comparison is based on the time-line position of the date-times.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(LocalDateTime other) {
        int cmp = date.compareTo(other.date);
        if (cmp == 0) {
            cmp = time.compareTo(other.time);
        }
        return cmp;
    }

    /**
     * Checks if this {@code LocalDateTime} is after the specified date-time.
     * <p>
     * The comparison is based on the time-line position of the date-times.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the specified date-time
     */
    public boolean isAfter(LocalDateTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if this {@code LocalDateTime} is before the specified date-time.
     * <p>
     * The comparison is based on the time-line position of the date-times.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is before the specified date-time
     */
    public boolean isBefore(LocalDateTime other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date-time is equal to another date-time.
     * <p>
     * The comparison is based on the time-line position of the date-times.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date-time
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LocalDateTime) {
            LocalDateTime other = (LocalDateTime) obj;
            return date.equals(other.date) && time.equals(other.time);
        }
        return false;
    }

    /**
     * A hash code for this date-time.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return date.hashCode() ^ time.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date-time as a {@code String}, such as {@code 2007-12-03T10:15:30}.
     * <p>
     * The output will be one of the following ISO-8601 formats:
     * <ul>
     * <li>{@code yyyy-MM-dd'T'HH:mm}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnn}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnnnn}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ssfnnnnnnnnn}</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this date-time, not null
     */
    @Override
    public String toString() {
        return date.toString() + 'T' + time.toString();
    }

    /**
     * Outputs this date-time as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted date-time string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws CalendricalException if an error occurs during printing
     */
    public String toString(DateTimeFormatter formatter) {
        ISOChronology.checkNotNull(formatter, "DateTimeFormatter must not be null");
        return formatter.print(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code LocalDateTime}.
     *
     * @return the rule for the date-time, not null
     */
    public static CalendricalRule<LocalDateTime> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class Rule extends CalendricalObjectRule<LocalDateTime> implements Serializable {
        private static final CalendricalRule<LocalDateTime> INSTANCE = new Rule();
        private static final long serialVersionUID = 1L;
        private Rule() {
            super(LocalDateTime.class, LDT);
        }
        private Object readResolve() {
            return INSTANCE;
        }
    }

}
