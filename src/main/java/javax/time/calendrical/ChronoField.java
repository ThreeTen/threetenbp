/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.ChronoUnit.DAYS;
import static javax.time.calendrical.ChronoUnit.ERAS;
import static javax.time.calendrical.ChronoUnit.FOREVER;
import static javax.time.calendrical.ChronoUnit.HALF_DAYS;
import static javax.time.calendrical.ChronoUnit.HOURS;
import static javax.time.calendrical.ChronoUnit.MICROS;
import static javax.time.calendrical.ChronoUnit.MILLIS;
import static javax.time.calendrical.ChronoUnit.MINUTES;
import static javax.time.calendrical.ChronoUnit.MONTHS;
import static javax.time.calendrical.ChronoUnit.NANOS;
import static javax.time.calendrical.ChronoUnit.SECONDS;
import static javax.time.calendrical.ChronoUnit.WEEKS;
import static javax.time.calendrical.ChronoUnit.WEEK_BASED_YEARS;
import static javax.time.calendrical.ChronoUnit.YEARS;

import javax.time.DateTimeConstants;
import javax.time.DayOfWeek;
import javax.time.Instant;
import javax.time.ZoneOffset;
import javax.time.chrono.ChronoLocalDate;

/**
 * A standard set of fields.
 * <p>
 * This set of fields provide field-based access to manipulate a date, time or date-time.
 * The standard set of fields can be extended by implementing {@link DateTimeField}.
 * <p>
 * These fields are intended to be applicable in multiple calendar systems.
 * For example, most non-ISO calendar systems define dates as a year, month and day,
 * just with slightly different rules.
 * The documentation of each field explains how it operates.
 *
 * <h4>Implementation notes</h4>
 * This is a final, immutable and thread-safe enum.
 */
public enum ChronoField implements DateTimeField {

    /**
     * The nano-of-second.
     * <p>
     * This counts the nanosecond within the second, from 0 to 999,999,999.
     * This field has the same meaning for all calendar systems.
     */
    NANO_OF_SECOND("NanoOfSecond", NANOS, SECONDS, DateTimeValueRange.of(0, 999_999_999)),
    /**
     * The nano-of-day.
     * <p>
     * This counts the nanosecond within the day, from 0 to (24 * 60 * 60 * 1,000,000,000) - 1.
     * This field has the same meaning for all calendar systems.
     */
    NANO_OF_DAY("NanoOfDay", NANOS, DAYS, DateTimeValueRange.of(0, 86400L * 1000_000_000L - 1)),
    /**
     * The micro-of-second.
     * <p>
     * This counts the microsecond within the second, from 0 to 999,999.
     * This field has the same meaning for all calendar systems.
     */
    MICRO_OF_SECOND("MicroOfSecond", MICROS, SECONDS, DateTimeValueRange.of(0, 999_999)),
    /**
     * The micro-of-day.
     * <p>
     * This counts the microsecond within the day, from 0 to (24 * 60 * 60 * 1,000,000) - 1.
     * This field has the same meaning for all calendar systems.
     */
    MICRO_OF_DAY("MicroOfDay", MICROS, DAYS, DateTimeValueRange.of(0, 86400L * 1000_000L - 1)),
    /**
     * The milli-of-second.
     * <p>
     * This counts the millisecond within the second, from 0 to 999.
     * This field has the same meaning for all calendar systems.
     */
    MILLI_OF_SECOND("MilliOfSecond", MILLIS, SECONDS, DateTimeValueRange.of(0, 999)),
    /**
     * The milli-of-day.
     * <p>
     * This counts the millisecond within the day, from 0 to (24 * 60 * 60 * 1,000) - 1.
     * This field has the same meaning for all calendar systems.
     */
    MILLI_OF_DAY("MilliOfDay", MILLIS, DAYS, DateTimeValueRange.of(0, 86400L * 1000L - 1)),
    /**
     * The second-of-minute.
     * <p>
     * This counts the second within the minute, from 0 to 59.
     * This field has the same meaning for all calendar systems.
     */
    SECOND_OF_MINUTE("SecondOfMinute", SECONDS, MINUTES, DateTimeValueRange.of(0, 59)),
    /**
     * The second-of-day.
     * <p>
     * This counts the second within the day, from 0 to (24 * 60 * 60) - 1.
     * This field has the same meaning for all calendar systems.
     */
    SECOND_OF_DAY("SecondOfDay", SECONDS, DAYS, DateTimeValueRange.of(0, 86400L - 1)),
    /**
     * The minute-of-hour.
     * <p>
     * This counts the minute within the hour, from 0 to 59.
     * This field has the same meaning for all calendar systems.
     */
    MINUTE_OF_HOUR("MinuteOfHour", MINUTES, HOURS, DateTimeValueRange.of(0, 59)),
    /**
     * The minute-of-day.
     * <p>
     * This counts the minute within the day, from 0 to (24 * 60) - 1.
     * This field has the same meaning for all calendar systems.
     */
    MINUTE_OF_DAY("MinuteOfDay", MINUTES, DAYS, DateTimeValueRange.of(0, (24 * 60) - 1)),
    /**
     * The hour-of-am-pm.
     * <p>
     * This counts the hour within the AM/PM, from 0 to 11.
     * This is the hour that would be observed on a standard 12-hour digital clock.
     * This field has the same meaning for all calendar systems.
     */
    HOUR_OF_AMPM("HourOfAmPm", HOURS, HALF_DAYS, DateTimeValueRange.of(0, 11)),
    /**
     * The clock-hour-of-am-pm.
     * <p>
     * This counts the hour within the AM/PM, from 1 to 12.
     * This is the hour that would be observed on a standard 12-hour analog wall clock.
     * This field has the same meaning for all calendar systems.
     */
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", HOURS, HALF_DAYS, DateTimeValueRange.of(1, 12)),
    /**
     * The hour-of-day.
     * <p>
     * This counts the hour within the day, from 0 to 23.
     * This is the hour that would be observed on a standard 24-hour digital clock.
     * This field has the same meaning for all calendar systems.
     */
    HOUR_OF_DAY("HourOfDay", HOURS, DAYS, DateTimeValueRange.of(0, 23)),
    /**
     * The clock-hour-of-day.
     * <p>
     * This counts the hour within the AM/PM, from 1 to 24.
     * This is the hour that would be observed on a 24-hour analog wall clock.
     * This field has the same meaning for all calendar systems.
     */
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", HOURS, DAYS, DateTimeValueRange.of(1, 24)),
    /**
     * The am-pm-of-day.
     * <p>
     * This counts the AM/PM within the day, from 0 (AM) to 1 (PM).
     * This field has the same meaning for all calendar systems.
     */
    AMPM_OF_DAY("AmPmOfDay", HALF_DAYS, DAYS, DateTimeValueRange.of(0, 1)),
    /**
     * The day-of-week, such as Tuesday.
     * <p>
     * This represents the standard concept of the day of the week.
     * In the default ISO calendar system, this has values from Monday (1) to Sunday (7).
     * The {@link DayOfWeek} class can be used to interpret the result.
     * <p>
     * Most non-ISO calendar systems also define a seven day week that aligns with ISO.
     * Those calendar systems must also use the same numbering system, from Monday (1) to
     * Sunday (7), which allows {@code DayOfWeek} to be used.
     * <p>
     * Calendar systems that do not have a standard seven day week should implement this field
     * if they have a similar concept of named or numbered days within a period similar
     * to a week. It is recommended that the numbering starts from 1.
     */
    DAY_OF_WEEK("DayOfWeek", DAYS, WEEKS, DateTimeValueRange.of(1, 7)),
    /**
     * The aligned day-of-week within a month.
     * <p>
     * This represents concept of the count of days within the period of a week
     * where the weeks are aligned to the start of the month.
     * This field is typically used with {@link #ALIGNED_WEEK_OF_MONTH}.
     * <p>
     * For example, in a calendar systems with a seven day week, the first aligned-week-of-month
     * starts on day-of-month 1, the second aligned-week starts on day-of-month 8, and so on.
     * Within each of these aligned-weeks, the days are numbered from 1 to 7 and returned
     * as the value of this field.
     * As such, day-of-month 1 to 7 will have aligned-day-of-week values from 1 to 7.
     * And day-of-month 8 to 14 will repeat this with aligned-day-of-week values from 1 to 7.
     * <p>
     * Calendar systems that do not have a seven day week should typically implement this
     * field in the same way, but using the alternate week length.
     */
    ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", DAYS, WEEKS, DateTimeValueRange.of(1, 7)),
    /**
     * The aligned day-of-week within a year.
     * <p>
     * This represents concept of the count of days within the period of a week
     * where the weeks are aligned to the start of the year.
     * This field is typically used with {@link #ALIGNED_WEEK_OF_YEAR}.
     * <p>
     * For example, in a calendar systems with a seven day week, the first aligned-week-of-year
     * starts on day-of-year 1, the second aligned-week starts on day-of-year 8, and so on.
     * Within each of these aligned-weeks, the days are numbered from 1 to 7 and returned
     * as the value of this field.
     * As such, day-of-year 1 to 7 will have aligned-day-of-week values from 1 to 7.
     * And day-of-year 8 to 14 will repeat this with aligned-day-of-week values from 1 to 7.
     * <p>
     * Calendar systems that do not have a seven day week should typically implement this
     * field in the same way, but using the alternate week length.
     */
    ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", DAYS, WEEKS, DateTimeValueRange.of(1, 7)),
    /**
     * The day-of-month.
     * <p>
     * This represents the concept of the day within the month.
     * In the default ISO calendar system, this has values from 1 to 31 in most months.
     * April, June, September, November have days from 1 to 30, while February has days
     * from 1 to 28, or 29 in a leap year.
     * <p>
     * Non-ISO calendar systems should implement this field using the most recognized
     * day-of-month values for users of the calendar system.
     * Normally, this is a count of days from 1 to the length of the month.
     */
    DAY_OF_MONTH("DayOfMonth", DAYS, MONTHS, DateTimeValueRange.of(1, 28, 31)),
    /**
     * The day-of-year.
     * <p>
     * This represents the concept of the day within the year.
     * In the default ISO calendar system, this has values from 1 to 365 in standard
     * years and 1 to 366 in leap years.
     * <p>
     * Non-ISO calendar systems should implement this field using the most recognized
     * day-of-year values for users of the calendar system.
     * Normally, this is a count of days from 1 to the length of the year.
     */
    DAY_OF_YEAR("DayOfYear", DAYS, YEARS, DateTimeValueRange.of(1, 365, 366)),
    /**
     * The epoch-day, based on the Java epoch of 1970-01-01 (ISO).
     * <p>
     * This field is the sequential count of days where 1970-01-01 (ISO) is zero.
     * Note that this uses the <i>local</i> time-line, ignoring offset and time-zone.
     * <p>
     * This field is strictly defined to have the same meaning in all calendar systems.
     * This is necessary to ensure interoperation between calendars.
     */
    EPOCH_DAY("EpochDay", DAYS, FOREVER, DateTimeValueRange.of((long) (DateTimeConstants.MIN_YEAR * 365.25), (long) (DateTimeConstants.MAX_YEAR * 365.25))),
    /**
     * The aligned week within a month.
     * <p>
     * This represents concept of the count of weeks within the period of a month
     * where the weeks are aligned to the start of the month.
     * This field is typically used with {@link #ALIGNED_DAY_OF_WEEK_IN_MONTH}.
     * <p>
     * For example, in a calendar systems with a seven day week, the first aligned-week-of-month
     * starts on day-of-month 1, the second aligned-week starts on day-of-month 8, and so on.
     * Thus, day-of-month values 1 to 7 are in aligned-week 1, while day-of-month values
     * 8 to 14 are in aligned-week 2, and so on.
     * <p>
     * Calendar systems that do not have a seven day week should typically implement this
     * field in the same way, but using the alternate week length.
     */
    ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", WEEKS, MONTHS, DateTimeValueRange.of(1, 4, 5)),
    /**
     * The week within a month.
     * <p>
     * This represents concept of the count of weeks within the month where weeks
     * start on a fixed day-of-week, such as Monday.
     * This field is typically used with {@link #DAY_OF_WEEK}.
     * <p>
     * In the default ISO calendar system, the week starts on Monday and there must be at
     * least 4 days in the first week.
     * Week one is the week starting on a Monday where there are at least 4 days in the month.
     * Thus, week one may start up to three days before the start of the month.
     * If the first week starts after the start of the month then the period before is week zero.
     * <p>
     * For example:<br />
     * - if the 1st day of the month is a Monday, week one starts on the 1st and there is no week zero<br />
     * - if the 2nd day of the month is a Monday, week one starts on the 2nd and the 1st is in week zero<br />
     * - if the 4th day of the month is a Monday, week one starts on the 4th and the 1st to 3rd is in week zero<br />
     * - if the 5th day of the month is a Monday, week two starts on the 5th and the 1st to 4th is in week one<br />
     * <p>
     * Non-ISO calendar systems should implement this field in the same way, taking
     * into account any differences in week or month length.
     */
    WEEK_OF_MONTH("WeekOfMonth", WEEKS, MONTHS, DateTimeValueRange.of(0, 1, 4, 5)),
    /**
     * The week within a week-based-year.
     * <p>
     * This represents the concept of the count of weeks within a week-based-year.
     * This field is defined by ISO-8601 and based on a year, known as the week-based-year,
     * that always starts on Monday.
     * This field is typically used with {@link #DAY_OF_WEEK} and {@link #WEEK_BASED_YEAR}.
     * <p>
     * In the default ISO calendar system, the week starts on Monday and there must be at
     * least 4 days in the first week. With these definitions, the week-based-year can start up
     * to 3 days before or up to 3 days after the start of the standard year.
     * Thus, if the 1st day of the regular year is a Tuesday, then the week-based-year starts
     * on December 31st of the previous regular year. Similarly, if the 1st day of the regular
     * year is a Sunday, then the week-based-year starts on January 2nd.
     * Given this definition, the week of the week-based-year counts the week from one
     * to 52 or 53 within the week-based-year.
     * <p>
     * Non-ISO calendar systems should implement this field in the same way, taking
     * into account any differences in week or year length.
     */
    WEEK_OF_WEEK_BASED_YEAR("WeekOfWeekBasedYear", WEEKS, WEEK_BASED_YEARS, DateTimeValueRange.of(1, 52, 53)),
    /**
     * The aligned week within a year.
     * <p>
     * This represents concept of the count of weeks within the period of a year
     * where the weeks are aligned to the start of the year.
     * This field is typically used with {@link #ALIGNED_DAY_OF_WEEK_IN_YEAR}.
     * <p>
     * For example, in a calendar systems with a seven day week, the first aligned-week-of-year
     * starts on day-of-year 1, the second aligned-week starts on day-of-year 8, and so on.
     * Thus, day-of-year values 1 to 7 are in aligned-week 1, while day-of-year values
     * 8 to 14 are in aligned-week 2, and so on.
     * <p>
     * Calendar systems that do not have a seven day week should typically implement this
     * field in the same way, but using the alternate week length.
     */
    ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", WEEKS, YEARS, DateTimeValueRange.of(1, 53)),
    /**
     * The week within a year.
     * <p>
     * This represents concept of the count of weeks within the year where weeks
     * start on a fixed day-of-week, such as Monday.
     * This field is typically used with {@link #DAY_OF_WEEK}.
     * <p>
     * In the default ISO calendar system, the week starts on Monday and there must be at
     * least 4 days in the first week.
     * Week one is the week starting on a Monday where there are at least 4 days in the year.
     * Thus, week one may start up to three days before the start of the year.
     * If the first week starts after the start of the year then the period before is week zero.
     * <p>
     * For example:<br />
     * - if the 1st day of the year is a Monday, week one starts on the 1st and there is no week zero<br />
     * - if the 2nd day of the year is a Monday, week one starts on the 2nd and the 1st is in week zero<br />
     * - if the 4th day of the year is a Monday, week one starts on the 4th and the 1st to 3rd is in week zero<br />
     * - if the 5th day of the year is a Monday, week two starts on the 5th and the 1st to 4th is in week one<br />
     * <p>
     * Non-ISO calendar systems should implement this field in the same way, taking
     * into account any differences in week or year length.
     */
    WEEK_OF_YEAR("WeekOfYear", WEEKS, YEARS, DateTimeValueRange.of(0, 1, 52, 53)),
    /**
     * The month-of-year, such as March.
     * <p>
     * This represents the concept of the month within the year.
     * In the default ISO calendar system, this has values from January (1) to December (12).
     * <p>
     * Non-ISO calendar systems should implement this field using the most recognized
     * month-of-year values for users of the calendar system.
     * Normally, this is a count of months starting from 1.
     */
    MONTH_OF_YEAR("MonthOfYear", MONTHS, YEARS, DateTimeValueRange.of(1, 12)),
    /**
     * The epoch-month based on the Java epoch of 1970-01-01.
     * <p>
     * This field is the sequential count of months where January 1970 (ISO) is zero.
     * Note that this uses the <i>local</i> time-line, ignoring offset and time-zone.
     * <p>
     * Non-ISO calendar systems should also implement this field to represent a sequential
     * count of months. It is recommended to define zero as the month of 1970-01-01 (ISO).
     */
    EPOCH_MONTH("EpochMonth", MONTHS, FOREVER, DateTimeValueRange.of((DateTimeConstants.MIN_YEAR - 1970L) * 12, (DateTimeConstants.MAX_YEAR - 1970L) * 12L - 1L)),
    /**
     * The proleptic week-based-year.
     * <p>
     * This represents the concept of the week-based-year, counting sequentially using negative
     * numbers and not based on the era. This field is defined by ISO-8601, and numbers years
     * related to the standard ISO year, ensuring that the week-based-year always starts on Monday.
     * This field is typically used with {@link #DAY_OF_WEEK} and {@link #WEEK_OF_WEEK_BASED_YEAR}.
     * <p>
     * In the default ISO calendar system, the week starts on Monday and there must be at
     * least 4 days in the first week. With these definitions, the week-based-year can start up
     * to 3 days before or up to 3 days after the start of the standard year.
     * Similarly, the week-based-year can end before or after the end of the regular year.
     * Thus, if the 1st day of the regular year is a Tuesday, then the week-based-year starts
     * on December 31st of the previous regular year. Similarly, if the 1st day of the regular
     * year is a Sunday, then the week-based-year starts on January 2nd.
     * <p>
     * Non-ISO calendar systems should implement this field in the same way, taking
     * into account any differences in week or year length.
     */
    WEEK_BASED_YEAR("WeekBasedYear", WEEK_BASED_YEARS, FOREVER, DateTimeValueRange.of(DateTimeConstants.MIN_YEAR, DateTimeConstants.MAX_YEAR)),
    /**
     * The year within the era.
     * <p>
     * This represents the concept of the year within the era.
     * This field is typically used with {@link #ERA}.
     * <p>
     * The standard mental model for a date is based on three concepts - year, month and day.
     * These map onto the {@code YEAR}, {@code MONTH_OF_YEAR} and {@code DAY_OF_MONTH} fields.
     * Note that there is no reference to eras.
     * The full model for a date requires four concepts - era, year, month and day. These map onto
     * the {@code ERA}, {@code YEAR_OF_ERA}, {@code MONTH_OF_YEAR} and {@code DAY_OF_MONTH} fields.
     * Whether this field or {@code YEAR} is used depends on which mental model is being used.
     * See {@link ChronoLocalDate} for more discussion on this topic.
     * <p>
     * In the default ISO calendar system, there are two eras defined, 'BCE' and 'CE'.
     * The era 'CE' is the one currently in use and year-of-era runs from 1 to the maximum value.
     * The era 'BCE' is the previous era, and the year-of-era runs backwards.
     * <p>
     * For example, subtracting a year each time yield the following:<br />
     * - year-proleptic 2  = 'CE' year-of-era 2<br />
     * - year-proleptic 1  = 'CE' year-of-era 1<br />
     * - year-proleptic 0  = 'BCE' year-of-era 1<br />
     * - year-proleptic -1 = 'BCE' year-of-era 2<br />
     * <p>
     * Note that the ISO-8601 standard does not actually define eras.
     * Note also that the ISO eras do not align with the well-known AD/BC eras due to the
     * change between the Julian and Gregorian calendar systems.
     * <p>
     * Non-ISO calendar systems should implement this field using the most recognized
     * year-of-era value for users of the calendar system.
     * Since most calendar systems have only two eras, the year-of-era numbering approach
     * will typically be the same as that used by the ISO calendar system.
     * The year-of-era value should typically always be positive, however this is not required.
     */
    YEAR_OF_ERA("YearOfEra", YEARS, FOREVER, DateTimeValueRange.of(1, DateTimeConstants.MAX_YEAR, DateTimeConstants.MAX_YEAR + 1)),
    /**
     * The proleptic year, such as 2012.
     * <p>
     * This represents the concept of the year, counting sequentially and using negative numbers.
     * The proleptic year is not interpreted in terms of the era.
     * See {@link #YEAR_OF_ERA} for an example showing the mapping from proleptic year to year-of-era.
     * <p>
     * The standard mental model for a date is based on three concepts - year, month and day.
     * These map onto the {@code YEAR}, {@code MONTH_OF_YEAR} and {@code DAY_OF_MONTH} fields.
     * Note that there is no reference to eras.
     * The full model for a date requires four concepts - era, year, month and day. These map onto
     * the {@code ERA}, {@code YEAR_OF_ERA}, {@code MONTH_OF_YEAR} and {@code DAY_OF_MONTH} fields.
     * Whether this field or {@code YEAR_OF_ERA} is used depends on which mental model is being used.
     * See {@link ChronoLocalDate} for more discussion on this topic.
     * <p>
     * Non-ISO calendar systems should implement this field as follows.
     * If the calendar system has only two eras, before and after a fixed date, then the
     * proleptic-year value must be the same as the year-of-era value for the later era,
     * and increasingly negative for the earlier era.
     * If the calendar system has more than two eras, then the proleptic-year value may be
     * defined with any appropriate value, although defining it to be the same as ISO may be
     * the best option.
     */
    YEAR("Year", YEARS, FOREVER, DateTimeValueRange.of(DateTimeConstants.MIN_YEAR, DateTimeConstants.MAX_YEAR)),
    /**
     * The era.
     * <p>
     * This represents the concept of the era, which is the largest division of the time-line.
     * This field is typically used with {@link #YEAR_OF_ERA}.
     * <p>
     * In the default ISO calendar system, there are two eras defined, 'BCE' and 'CE'.
     * The era 'CE' is the one currently in use and year-of-era runs from 1 to the maximum value.
     * The era 'BCE' is the previous era, and the year-of-era runs backwards.
     * See {@link #YEAR_OF_ERA} for a full example.
     * <p>
     * Non-ISO calendar systems should implement this field to define eras.
     * The value of the era that was active on 1970-01-01 (ISO) must be assigned the value 1.
     * Earlier eras must have sequentially smaller values.
     * Later eras must have sequentially larger values,
     */
    ERA("Era", ERAS, FOREVER, DateTimeValueRange.of(0, 1)),
    /**
     * The instant epoch-seconds.
     * <p>
     * This represents the concept of the sequential count of seconds where
     * 1970-01-01T00:00Z (ISO) is zero.
     * This field may be used with {@link #NANO_OF_DAY} to represent the fraction of the day.
     * <p>
     * An {@link Instant} represents an instantaneous point on the time-line.
     * On their own they have no elements which allow a local date-time to be obtained.
     * Only when paired with an offset or time-zone can the local date or time be found.
     * This field allows the seconds part of the instant to be queried.
     * <p>
     * This field is strictly defined to have the same meaning in all calendar systems.
     * This is necessary to ensure interoperation between calendars.
     */
    INSTANT_SECONDS("InstantSeconds", SECONDS, FOREVER, DateTimeValueRange.of(Long.MIN_VALUE, Long.MAX_VALUE)),
    /**
     * The offset from UTC/Greenwich.
     * <p>
     * This represents the concept of the offset in seconds of local time from UTC/Greenwich.
     * <p>
     * A {@link ZoneOffset} represents the period of time that local time differs from UTC/Greenwich.
     * This is usually a fixed number of hours and minutes.
     * It is equivalent to the {@link ZoneOffset#getTotalSeconds() total amount} of the offset in seconds.
     * For example, during the winter Paris has an offset of {@code +01:00}, which is 3600 seconds.
     * <p>
     * This field is strictly defined to have the same meaning in all calendar systems.
     * This is necessary to ensure interoperation between calendars.
     */
    OFFSET_SECONDS("OffsetSeconds", SECONDS, FOREVER, DateTimeValueRange.of(-18 * 3600, 18 * 3600));

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;
    private final DateTimeValueRange range;

    private ChronoField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit, DateTimeValueRange range) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
        this.range = range;
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return name;
    }

    @Override
    public PeriodUnit getBaseUnit() {
        return baseUnit;
    }

    @Override
    public PeriodUnit getRangeUnit() {
        return rangeUnit;
    }

    @Override
    public DateTimeValueRange range() {
        return range;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this field represents a component of a date.
     *
     * @return true if it is a component of a date
     */
    public boolean isDateField() {
        return ordinal() >= DAY_OF_WEEK.ordinal() && ordinal() <= ERA.ordinal();
    }

    /**
     * Checks if this field represents a component of a time.
     *
     * @return true if it is a component of a time
     */
    public boolean isTimeField() {
        return ordinal() < DAY_OF_WEEK.ordinal();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks that the specified value is valid for this field.
     * <p>
     * This validates that the value is within the outer range of valid values
     * returned by {@link #range()}.
     *
     * @param value  the value to check
     * @return the value that was passed in
     */
    public long checkValidValue(long value) {  // JAVA8 default method on interface
        return range().checkValidValue(value, this);
    }

    /**
     * Checks that the specified value is valid and fits in an {@code int}.
     * <p>
     * This validates that the value is within the outer range of valid values
     * returned by {@link #range()}.
     * It also checks that all valid values are within the bounds of an {@code int}.
     *
     * @param value  the value to check
     * @return the value that was passed in
     */
    public int checkValidIntValue(long value) {  // JAVA8 default method on interface
        return range().checkValidIntValue(value, this);
    }

    //-------------------------------------------------------------------------
    @Override
    public int compare(DateTimeAccessor dateTime1, DateTimeAccessor dateTime2) {
        return Long.compare(dateTime1.getLong(this), dateTime2.getLong(this));
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean doIsSupported(DateTimeAccessor dateTime) {
        return dateTime.isSupported(this);
    }

    @Override
    public DateTimeValueRange doRange(DateTimeAccessor dateTime) {
        return dateTime.range(this);
    }

    @Override
    public long doGet(DateTimeAccessor dateTime) {
        return dateTime.getLong(this);
    }

    @Override
    public <R extends DateTimeAccessor> R doSet(R dateTime, long newValue) {
        return (R) dateTime.with(this, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean resolve(DateTimeBuilder builder, long value) {
        return false;  // resolve implemented in builder
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getName();
    }

}
