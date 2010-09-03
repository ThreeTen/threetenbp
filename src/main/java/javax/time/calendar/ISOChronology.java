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
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.time.Duration;
import javax.time.MathUtils;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * The ISO-8601 calendar system, which follows the rules of the current
 * <i>de facto</i> world calendar.
 * <p>
 * ISOChronology follows the rules of the Gregorian calendar for all time.
 * Thus, dates is the past, and particularly before 1583, may not correspond
 * to historical documents.
 * <p>
 * ISOChronology is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class ISOChronology extends Chronology implements Serializable {

    /**
     * The singleton instance of {@code ISOChronology}.
     */
    public static final ISOChronology INSTANCE = new ISOChronology();
    /**
     * Constant for the minimum week-based-year.
     */
    public static final int MIN_WEEK_BASED_YEAR = Year.MIN_YEAR;
    /**
     * Constant for the maximum week-based-year.
     */
    public static final int MAX_WEEK_BASED_YEAR = Year.MAX_YEAR;
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The number of seconds in one day.
     */
    static final int SECONDS_PER_DAY = 60 * 60 * 24;
    /**
     * The number of days in a 400 year cycle.
     */
    static final int DAYS_PER_CYCLE = 146097;
    /**
     * The number of days from year zero to year 1970.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     */
    static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);
    /**
     * The number of days from year zero to the Modified Julian Day epoch of 1858-11-17.
     */
    static final long DAYS_0000_TO_MJD_EPOCH = 678941;

//    /** Number of months in one year. */
//    private static final int MONTHS_PER_YEAR = 12;
//    /** Number of seconds in one hour. */
//    private static final int SECONDS_PER_HOUR = 60 * 60;
//    /** Number of seconds in one minute. */
//    private static final int SECONDS_PER_MINUTE = 60;
//    /** The length of months in a standard year. */
//    private static final int[] STANDARD_MONTH_LENGTHS = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
//    /** The length of months in a leap year. */
//    private static final int[] LEAP_MONTH_LENGTHS = new int[] {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    /**
     * The start of months in a standard year.
     */
    private static final int[] STANDARD_MONTH_START = new int[] {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    /**
     * The start of months in a leap year.
     */
    private static final int[] LEAP_MONTH_START = new int[] {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year according to the ISO calendar system rules.
     * <p>
     * The ISO calendar system applies the current rules for leap years across the whole time-line.
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
     * @param year  the year to check, not validated for range
     * @return true if the year is a leap year
     */
    public static boolean isLeapYear(int year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the day-of-week from a date.
     *
     * @param date  the date to use, not null
     * @return the day-of-week
     */
    static DayOfWeek getDayOfWeekFromDate(LocalDate date) {
        long mjd = date.toModifiedJulianDays();
        if (mjd < 0) {
            long weeks = mjd / 7;
            mjd += (-weeks + 1) * 7;
        }
        int dow0 = (int) ((mjd + 2) % 7);
        return DayOfWeek.of(dow0 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the day-of-year from a date.
     *
     * @param date  the date to use, not null
     * @return the day-of-year
     */
    static int getDayOfYearFromDate(LocalDate date) {
        int moy0 = date.getMonthOfYear().ordinal();
        int dom = date.getDayOfMonth();
        if (ISOChronology.isLeapYear(date.getYear())) {
            return LEAP_MONTH_START[moy0] + dom;
        } else {
            return STANDARD_MONTH_START[moy0] + dom;
        }
    }

    /**
     * Calculates the date from a year and day-of-year.
     *
     * @param year  the year, valid
     * @param dayOfYear  the day-of-year, valid
     * @return the date, never null
     */
    static LocalDate getDateFromDayOfYear(int year, int dayOfYear) {
        boolean leap = ISOChronology.isLeapYear(year);
        if (dayOfYear == 366 && leap == false) {
            throw new InvalidCalendarFieldException("DayOfYear 366 is invalid for year " + year, dayOfYearRule());
        }
        int doy0 = dayOfYear - 1;
        int[] array = (leap ? LEAP_MONTH_START : STANDARD_MONTH_START);
        int month = 1;
        for ( ; month < 12; month++) {
            if (doy0 < array[month]) {
                break;
            }
        }
        MonthOfYear moy = MonthOfYear.of(month);
        int dom = dayOfYear - array[month - 1];
        return LocalDate.of(year, moy, dom);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the week-based-year.
     *
     * @param date  the date, not null
     * @return the week-based-year
     */
    static int getWeekBasedYearFromDate(LocalDate date) {
        Year year = date.toYear();  // use ISO year object so previous/next are checked
        if (date.getMonthOfYear() == MonthOfYear.JANUARY) {
            int dom = date.getDayOfMonth();
            if (dom < 4) {
                int dow = date.getDayOfWeek().getValue();
                if (dow > dom + 3) {
                    year = year.previous();
                }
            }
        } else if (date.getMonthOfYear() == MonthOfYear.DECEMBER) {
            int dom = date.getDayOfMonth();
            if (dom > 28) {
                int dow = date.getDayOfWeek().getValue();
                if (dow <= dom % 7) {
                    year = year.next();
                }
            }
        }
        return year.getValue();
    }

    /**
     * Calculates the week of week-based-year.
     *
     * @param date  the date to use, not null
     * @return the week
     */
    static int getWeekOfWeekBasedYearFromDate(LocalDate date) {
        int wby = getWeekBasedYearFromDate(date);
        LocalDate yearStart = LocalDate.of(wby, MonthOfYear.JANUARY, 4);
        return MathUtils.safeToInt((date.toModifiedJulianDays() - yearStart.toModifiedJulianDays() +
                yearStart.getDayOfWeek().getValue() - 1) / 7 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private ISOChronology() {
    }

    /**
     * Resolves singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, never null
     */
    @Override
    public String getName() {
        return "ISO";
    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the equivalent rule for the specified field in the ISO chronology.
//     * <p>
//     * This will take the input field and provide the closest matching field
//     * that is based......
//     *
//     * @param rule  the rule to convert, not null
//     * @return the rule in ISO chronology, never null
//     */
//    public DateTimeFieldRule<?> convertRule(DateTimeFieldRule<?> rule) {
//        if (rule.getChronology().equals(this)) {
//            return rule;
//        }
//        return null;
////        return rule.getChronology().convertRule(rule);;
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the year field in the ISO chronology.
     * <p>
     * This field counts years using the modern civil calendar system as defined
     * by ISO-8601. There is no historical cutover (as found in historical dates
     * such as from the Julian to Gregorian calendar).
     * <p>
     * The implication of this is that historical dates will not be accurate.
     * All work requiring accurate historical dates must use the appropriate
     * chronology that defines the Gregorian cutover.
     * <p>
     * A further implication of the ISO-8601 rules is that the year zero
     * exists. This roughly equates to 1 BC/BCE, however the alignment is
     * not exact as explained above.
     *
     * @return the rule for the year field, never null
     */
    public static DateTimeFieldRule<Integer> yearRule() {
        return YEAR;
    }

//    /**
//     * Gets the rule for the two-digit year field in the ISO chronology.
//     * <p>
//     * This field is used to represent the commonly used, and abused, two-digit year.
//     * This is defined as the least significant two digits of the year ignoring negatives.
//     * This implies that the year 2011 will have the two-digit year 11 and that
//     * the year -1423 will have the two-digit year 23.
//     * <p>
//     * Note that this field does not combine with any other field.
//     * As such, it must be manually handled in the calendrical merge process.
//     *
//     * @return the rule for the two digit year field, never null
//     */
//    public static DateTimeFieldRule twoDigitYearRule() {
//        return TwoDigitYearRule.INSTANCE;
//    }

    /**
     * Gets the rule for the month-of-year field in the ISO chronology.
     * <p>
     * This field counts months sequentially from the start of the year.
     * The values follow the ISO-8601 standard and normal human interactions.
     * These define January as value 1 to December as value 12.
     * <p>
     * The enum {@link MonthOfYear} should be used wherever possible in
     * applications when referring to the day of the week to avoid
     * hard-coding the values.
     *
     * @return the rule for the month-of-year field, never null
     */
    public static DateTimeFieldRule<MonthOfYear> monthOfYearRule() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-month field in the ISO chronology.
     * <p>
     * This field counts days sequentially from the start of the month.
     * The first day of the month is 1 and the last is 28, 29, 30 or 31
     * depending on the month and whether it is a leap year.
     *
     * @return the rule for the day-of-month field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfMonthRule() {
        return DAY_OF_MONTH;
    }

    /**
     * Gets the rule for the day-of-year field in the ISO chronology.
     * <p>
     * This field counts days sequentially from the start of the year.
     * The first day of the year is 1 and the last is 365, or 366 in a leap year.
     *
     * @return the rule for the day-of-year field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfYearRule() {
        return DAY_OF_YEAR;
    }

    /**
     * Gets the rule for the week-based-year field in the ISO chronology.
     * <p>
     * This field is the year that results from calculating weeks with the ISO-8601 algorithm.
     * See {@link #weekOfWeekBasedYearRule() week of week-based-year} for details.
     * <p>
     * The week-based-year will either be 52 or 53 weeks long, depending on the
     * result of the algorithm for a particular date.
     *
     * @return the rule for the week-based-year field, never null
     */
    public static DateTimeFieldRule<Integer> weekBasedYearRule() {
        return WEEK_BASED_YEAR;
    }

    /**
     * Gets the rule for the week-of-week-based-year field in the ISO chronology.
     * <p>
     * This field counts weeks using the ISO-8601 algorithm.
     * The first week of the year is the week which has at least 4 days in the year
     * using a Monday to Sunday week definition. Thus it is possible for the first
     * week to start on any day from the 29th December in the previous year to the
     * 4th January in the new year. The year which is aligned with this field is
     * known as the {@link #weekBasedYearRule() week-based-year}.
     *
     * @return the rule for the week-of-week-based-year field, never null
     */
    public static DateTimeFieldRule<Integer> weekOfWeekBasedYearRule() {
        return WEEK_OF_WEEK_BASED_YEAR;
    }

    /**
     * Gets the rule for the day-of-week field.
     * <p>
     * This field uses the ISO-8601 values for the day-of-week.
     * These define Monday as value 1 to Sunday as value 7.
     * <p>
     * The enum {@link DayOfWeek} should be used wherever possible in
     * applications when referring to the day of the week value to avoid
     * needing to remember the values from 1 to 7.
     *
     * @return the rule for the day-of-week field, never null
     */
    public static DateTimeFieldRule<DayOfWeek> dayOfWeekRule() {
        return DayOfWeekRule.INSTANCE;
    }

    /**
     * Gets the rule for the week-of-year field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days starting from the first
     * of January. The 1st to the 7th of January is always week 1 while the
     * 8th to the 14th is always week 2.
     *
     * @return the rule for the week-of-year field, never null
     */
    public static DateTimeFieldRule<Integer> weekOfYearRule() {
        return WEEK_OF_YEAR;
    }

    /**
     * Gets the rule for the quarter-of-year field in the ISO chronology.
     * <p>
     * This field counts quarters sequentially from the start of the year.
     * The first quarter of the year is 1 and the last is 4. Each quarter
     * lasts exactly three months.
     *
     * @return the rule for the quarter-of-year field, never null
     */
    public static DateTimeFieldRule<QuarterOfYear> quarterOfYearRule() {
        return QuarterOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month-of-quarter field in the ISO chronology.
     * <p>
     * This field counts months sequentially from the start of the quarter.
     * The first month of the quarter is 1 and the last is 3. Each quarter
     * lasts exactly three months.
     *
     * @return the rule for the month-of-quarter field, never null
     */
    public static DateTimeFieldRule<Integer> monthOfQuarterRule() {
        return MONTH_OF_QUARTER;
    }

    /**
     * Gets the rule for the week-of-month field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days starting from the first
     * day of the month. The 1st to the 7th of a month is always week 1 while the
     * 8th to the 14th is always week 2 and so on.
     * <p>
     * This field can be used to create concepts such as 'the second Saturday'
     * of a month. To achieve this, setup a {@link DateTimeFields} instance
     * using this rule and the {@link #dayOfWeekRule() day-of-week} rule.
     *
     * @return the rule for the week-of-month field, never null
     */
    public static DateTimeFieldRule<Integer> weekOfMonthRule() {
        return WEEK_OF_MONTH;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the hour-of-day field.
     * <p>
     * This field counts hours sequentially from the start of the day.
     * The values run from 0 to 23.
     *
     * @return the rule for the hour-of-day field, never null
     */
    public static DateTimeFieldRule<Integer> hourOfDayRule() {
        return HOUR_OF_DAY;
    }

    /**
     * Gets the rule for the minute-of-hour field.
     * <p>
     * This field counts minutes sequentially from the start of the hour.
     * The values run from 0 to 59.
     *
     * @return the rule for the minute-of-hour field, never null
     */
    public static DateTimeFieldRule<Integer> minuteOfHourRule() {
        return MINUTE_OF_HOUR;
    }

    /**
     * Gets the rule for the second-of-minute field.
     * <p>
     * This field counts seconds sequentially from the start of the minute.
     * The values run from 0 to 59.
     *
     * @return the rule for the second-of-minute field, never null
     */
    public static DateTimeFieldRule<Integer> secondOfMinuteRule() {
        return SECOND_OF_MINUTE;
    }

    /**
     * Gets the rule for the nano-of-second field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the second.
     * The values run from 0 to 999,999,999.
     *
     * @return the rule for the nano-of-second field, never null
     */
    public static DateTimeFieldRule<Integer> nanoOfSecondRule() {
        return NANO_OF_SECOND;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the second-of-day field.
     * <p>
     * This field counts seconds sequentially from the start of the day.
     * The values run from 0 to 86399.
     *
     * @return the rule for the second-of-day field, never null
     */
    public static DateTimeFieldRule<Integer> secondOfDayRule() {
        return SECOND_OF_DAY;
    }

    /**
     * Gets the rule for the milli-of-day field.
     * <p>
     * This field counts milliseconds sequentially from the start of the day.
     * The values run from 0 to 86,399,999.
     *
     * @return the rule for the milli-of-day field, never null
     */
    public static DateTimeFieldRule<Integer> milliOfDayRule() {
        return MILLI_OF_DAY;
    }

    /**
     * Gets the rule for the milli-of-second field.
     * <p>
     * This field counts milliseconds sequentially from the start of the second.
     * The values run from 0 to 999.
     *
     * @return the rule for the milli-of-second field, never null
     */
    public static DateTimeFieldRule<Integer> milliOfSecondRule() {
        return MILLI_OF_SECOND;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the AM/PM of day field.
     * <p>
     * This field defines the half-day AM/PM value. The hour-of-day from 0 to 11 is
     * defined as AM, while the hours from 12 to 23 are defined as PM.
     * AM is defined with the value 0, while PM is defined with the value 1.
     * <p>
     * The enum {@link AmPmOfDay} should be used wherever possible in
     * applications when referring to the day of the week to avoid
     * hard-coding the values.
     *
     * @return the rule for the am/pm of day field, never null
     */
    public static DateTimeFieldRule<AmPmOfDay> amPmOfDayRule() {
        return AmPmOfDayRule.INSTANCE;
    }

    /**
     * Gets the rule for the hour of AM/PM field from 0 to 11.
     * <p>
     * This field counts hours sequentially from the start of the half-day AM/PM.
     * The values run from 0 to 11.
     *
     * @return the rule for the hour of AM/PM field, never null
     */
    public static DateTimeFieldRule<Integer> hourOfAmPmRule() {
        return HOUR_OF_AMPM;
    }

    /**
     * Gets the rule for the clock hour of AM/PM field from 1 to 12.
     * <p>
     * This field counts hours sequentially within the half-day AM/PM as
     * normally seen on a clock or watch. The values run from 1 to 12.
     *
     * @return the rule for the hour of AM/PM field, never null
     */
    public static DateTimeFieldRule<Integer> clockHourOfAmPmRule() {
        return CLOCK_HOUR_OF_AMPM;
    }

    /**
     * Gets the rule for the clock hour of AM/PM field from 1 to 24.
     * <p>
     * This field counts hours sequentially within the day starting from 1.
     * The values run from 1 to 24.
     *
     * @return the rule for the clock-hour-of-day field, never null
     */
    public static DateTimeFieldRule<Integer> clockHourOfDayRule() {
        return CLOCK_HOUR_OF_DAY;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the epoch-days field.
     * <p>
     * This field counts seconds sequentially from the Java epoch of 1970-01-01.
     *
     * @return the rule for the epoch-days field, never null
     */
    public static CalendricalRule<Long> epochDays() {
        return EpochDaysRule.INSTANCE;
    }

    /**
     * Gets the rule for the nano-of-day field.
     * <p>
     * This field counts seconds sequentially from the start of the day.
     * The values run from 0 to 86,399,999,999,999.
     *
     * @return the rule for the nano-of-day field, never null
     */
    public static CalendricalRule<Long> nanoOfDayRule() {
        return NanoOfDayRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period unit for eras.
     * <p>
     * The period unit defines the concept of a period of a eras.
     * An era, based on a simple before/after point on the time-line, is infinite
     * in length. For this rule, an era has an estimated duration of 2,000,000,000 years.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to 2,000,000,000 years.
     *
     * @return the period unit for eras, never null
     */
    public static PeriodUnit periodEras() {
        return ERAS;
    }

    /**
     * Gets the period unit for millennia of 1000 years.
     * <p>
     * The period unit defines the concept of a period of a century.
     * <p>
     * The equivalent period and estimated duration are equal to 10 centuries.
     *
     * @return the period unit for millennia, never null
     */
    public static PeriodUnit periodMillennia() {
        return MILLENNIA;
    }

    /**
     * Gets the period unit for centuries of 100 years.
     * <p>
     * The period unit defines the concept of a period of a century.
     * <p>
     * The equivalent period and estimated duration are equal to 10 decades.
     *
     * @return the period unit for centuries, never null
     */
    public static PeriodUnit periodCenturies() {
        return CENTURIES;
    }

    /**
     * Gets the period unit for decades of 10 years.
     * <p>
     * The period unit defines the concept of a period of a decade.
     * <p>
     * The equivalent period and estimated duration are equal to 10 years.
     *
     * @return the period unit for decades, never null
     */
    public static PeriodUnit periodDecades() {
        return DECADES;
    }

    /**
     * Gets the period unit for years of 12 months.
     * <p>
     * The period unit defines the concept of a period of a year.
     * <p>
     * The equivalent period and estimated duration are equal to 4 quarters.
     * <p>
     * See {@link #yearRule()} for the main date-time field.
     *
     * @return the period unit for years, never null
     */
    public static PeriodUnit periodYears() {
        return YEARS;
    }

    /**
     * Gets the period unit for week-based-years.
     * <p>
     * The period unit defines the concept of a period of a week-based-year.
     * This is typically 52 weeks, and occasionally 53 weeks.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to 364.5 days, which is just over 5 weeks.
     * <p>
     * See {@link #weekBasedYearRule()} for the main date-time field.
     *
     * @return the period unit for week-based-years, never null
     */
    public static PeriodUnit periodWeekBasedYears() {
        return WEEK_BASED_YEARS;
    }

    /**
     * Gets the period unit for quarters of 3 months.
     * <p>
     * The period unit defines the concept of a period of a quarter.
     * <p>
     * The equivalent period and estimated duration are equal to 3 months.
     * <p>
     * See {@link #quarterOfYearRule()} for the main date-time field.
     *
     * @return the period unit for quarters, never null
     */
    public static PeriodUnit periodQuarters() {
        return QUARTERS;
    }

    /**
     * Gets the period unit for months.
     * <p>
     * The period unit defines the concept of a period of a month.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to one-twelfth of a year based on 365.2425 days.
     * <p>
     * See {@link #monthOfYearRule()} for the main date-time field.
     *
     * @return the period unit for months, never null
     */
    public static PeriodUnit periodMonths() {
        return MONTHS;
    }

    /**
     * Gets the period unit for weeks of 7 days.
     * <p>
     * The period unit defines the concept of a period of a week.
     * <p>
     * The equivalent period and estimated duration are equal to 7 days.
     * <p>
     * See {@link #weekOfWeekBasedYearRule()} and {@link #weekOfYearRule()} for
     * the main date-time fields.
     *
     * @return the period unit for weeks, never null
     */
    public static PeriodUnit periodWeeks() {
        return WEEKS;
    }

    /**
     * Gets the period unit for days.
     * <p>
     * The period unit defines the concept of a period of a day.
     * This is typically equal to 24 hours, but may vary due to time-zone changes.
     * <p>
     * This chronology defines two units that could represent a day.
     * This unit, {@code Days}, represents a day that varies in length based on
     * time-zone (daylight savings time) changes. It is a basic unit that cannot
     * be converted to seconds, nanoseconds or {@link Duration}.
     * By contrast, the {@link #period24Hours() 24Hours} unit has a fixed length of
     * exactly 24 hours allowing it to be converted to seconds, nanoseconds and {@code Duration}.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to 24 hours.
     * <p>
     * See {@link #dayOfMonthRule()} for the main date-time field.
     *
     * @return the period unit for accurate, variable length, days, never null
     */
    public static PeriodUnit periodDays() {
        return DAYS;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period unit for twenty-four hours, that is often treated as a day.
     * <p>
     * The period unit defines the concept of a period of exactly 24 hours that
     * is often treated as a day. The unit name of "24Hours" is intended to convey
     * the fact that this is primarily a 24 hour unit that happens to be used as
     * a day unit on occasion. In most scenarios, the standard {@link #periodDays() Days}
     * unit is more applicable and accurate.
     * <p>
     * This chronology defines two units that could represent a day.
     * This unit, {@code 24Hours}, represents a fixed length of exactly 24 hours,
     * allowing it to be converted to seconds, nanoseconds and {@link Duration}.
     * By contrast, the {@code Days} unit varies in length based on time-zone (daylight
     * savings time) changes and cannot be converted to seconds, nanoseconds or {@code Duration}.
     * <p>
     * The equivalent period and estimated duration are equal to twice the
     * 12 hours unit, making it also equivalent to 24 hours.
     *
     * @return the period unit for fixed, 24 hour, days, never null
     */
    public static PeriodUnit period24Hours() {
        return _24_HOURS;
    }

    /**
     * Gets the period unit for twelve hours, as used by AM/PM.
     * <p>
     * The period unit defines the concept of a period of 12 hours.
     * <p>
     * The equivalent period and estimated duration are equal to 12 hours.
     * <p>
     * See {@link #amPmOfDayRule()} for the main date-time field.
     *
     * @return the period unit for twelve hours, never null
     */
    public static PeriodUnit period12Hours() {
        return _12_HOURS;
    }

    /**
     * Gets the period unit for hours of 60 minutes.
     * <p>
     * The period unit defines the concept of a period of a hour.
     * <p>
     * The equivalent period and estimated duration are equal to 60 minutes.
     * <p>
     * See {@link #hourOfDayRule()} for the main date-time field.
     *
     * @return the period unit for hours, never null
     */
    public static PeriodUnit periodHours() {
        return HOURS;
    }

    /**
     * Gets the period unit for minutes of 60 seconds.
     * <p>
     * The period unit defines the concept of a period of a minute.
     * <p>
     * The equivalent period and estimated duration are equal to 60 seconds.
     * <p>
     * See {@link #minuteOfHourRule()} for the main date-time field.
     *
     * @return the period unit for minutes, never null
     */
    public static PeriodUnit periodMinutes() {
        return MINUTES;
    }

    /**
     * Gets the period unit for seconds.
     * <p>
     * The period unit defines the concept of a period of a second.
     * <p>
     * The equivalent period and estimated duration are equal to 1000 milliseconds.
     * <p>
     * See {@link #secondOfMinuteRule()} for the main date-time field.
     *
     * @return the period unit for seconds, never null
     */
    public static PeriodUnit periodSeconds() {
        return SECONDS;
    }

    /**
     * Gets the period unit for milliseconds.
     * <p>
     * The period unit defines the concept of a period of a millisecond.
     * <p>
     * The equivalent period and estimated duration are equal to 1000 microseconds.
     * <p>
     * See {@link #milliOfSecondRule()} for the main date-time field.
     *
     * @return the period unit for milliseconds, never null
     */
    public static PeriodUnit periodMillis() {
        return MILLIS;
    }

    /**
     * Gets the period unit for microseconds.
     * <p>
     * The period unit defines the concept of a period of a microsecond.
     * <p>
     * The equivalent period and estimated duration are equal to 1000 nanoseconds.
     *
     * @return the period unit for microseconds, never null
     */
    public static PeriodUnit periodMicros() {
        return MICROS;
    }

    /**
     * Gets the period unit for nanoseconds.
     * <p>
     * The period unit defines the concept of a period of a nanosecond.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is 1 nanosecond.
     * <p>
     * See {@link #nanoOfSecondRule()} for the main date-time field.
     *
     * @return the period unit for nanoseconds, never null
     */
    public static PeriodUnit periodNanos() {
        return NANOS;
    }

    //-----------------------------------------------------------------------
    /**
     * Merges the set of fields known by this chronology.
     *
     * @param merger  the merger to use, not null
     */
    void merge(CalendricalMerger merger) {
        // milli-of-day
        Integer modVal = merger.getValue(ISOChronology.milliOfDayRule());
        if (modVal != null) {
            merger.storeMerged(LocalTime.rule(), LocalTime.ofNanoOfDay(modVal * 1000000L));
            merger.removeProcessed(ISOChronology.milliOfDayRule());
        }
        
        // second-of-day
        Integer sodVal = merger.getValue(ISOChronology.secondOfDayRule());
        if (modVal != null) {
            Integer nosVal = merger.getValue(ISOChronology.nanoOfSecondRule());
            if (nosVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.ofSecondOfDay(sodVal, nosVal));
                merger.removeProcessed(ISOChronology.nanoOfSecondRule());
            } else {
                Integer mosVal = merger.getValue(ISOChronology.milliOfSecondRule());
                if (mosVal != null) {
                    merger.storeMerged(LocalTime.rule(), LocalTime.ofSecondOfDay(sodVal, mosVal * 1000000));
                    merger.removeProcessed(ISOChronology.milliOfSecondRule());
                } else {
                    merger.storeMerged(LocalTime.rule(), LocalTime.ofSecondOfDay(sodVal));
                }
            }
            merger.removeProcessed(ISOChronology.secondOfDayRule());
        }
        
        // am-hour
        AmPmOfDay amPm = merger.getValue(ISOChronology.amPmOfDayRule());
        if (amPm != null) {
            Integer hapVal = merger.getValue(ISOChronology.hourOfAmPmRule());
            if (hapVal != null) {
                int hourOfDay = amPm.getValue() * 12 + hapVal;
                merger.storeMerged(ISOChronology.hourOfDayRule(), hourOfDay);
                merger.removeProcessed(ISOChronology.amPmOfDayRule());
                merger.removeProcessed(ISOChronology.hourOfAmPmRule());
            }
            Integer chapVal = merger.getValue(ISOChronology.hourOfAmPmRule());
            if (chapVal != null) {
                int hourOfDay = amPm.getValue() * 12 + chapVal;
                if (hourOfDay == 24) {
                    merger.addToOverflow(Period.ofDays(1));
                    hourOfDay = 0;
                }
                merger.storeMerged(ISOChronology.hourOfDayRule(), hourOfDay);
                merger.removeProcessed(ISOChronology.amPmOfDayRule());
                merger.removeProcessed(ISOChronology.clockHourOfAmPmRule());
            }
        }
        
        // hour-minute-second-nano
        Integer hourVal = merger.getValue(ISOChronology.hourOfDayRule());
        if (hourVal != null) {
            Integer minuteVal = merger.getValue(ISOChronology.minuteOfHourRule());
            Integer secondVal = merger.getValue(ISOChronology.secondOfMinuteRule());
            Integer mosVal = merger.getValue(ISOChronology.milliOfSecondRule());
            Integer nanoVal = merger.getValue(ISOChronology.nanoOfSecondRule());
            if (minuteVal != null && secondVal != null && nanoVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal, minuteVal, secondVal, nanoVal));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
                merger.removeProcessed(ISOChronology.secondOfMinuteRule());
                merger.removeProcessed(ISOChronology.nanoOfSecondRule());
            } else if (minuteVal != null && secondVal != null && mosVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal, minuteVal, secondVal, mosVal * 1000000));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
                merger.removeProcessed(ISOChronology.secondOfMinuteRule());
                merger.removeProcessed(ISOChronology.milliOfSecondRule());
            } else if (minuteVal != null && secondVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal, minuteVal, secondVal, 0));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
                merger.removeProcessed(ISOChronology.secondOfMinuteRule());
            } else if (minuteVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal, minuteVal, 0, 0));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
            } else {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal, 0));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
            }
        }
        
        // quarter-of-year and month-of-quarter
        QuarterOfYear qoy = merger.getValue(ISOChronology.quarterOfYearRule());
        Integer moqVal = merger.getValue(ISOChronology.monthOfQuarterRule());
        if (qoy != null && moqVal != null) {
            MonthOfYear moy = MonthOfYear.of(qoy.getFirstMonthOfQuarter().ordinal() + moqVal);
            merger.storeMerged(ISOChronology.monthOfYearRule(), moy);
            merger.removeProcessed(ISOChronology.quarterOfYearRule());
            merger.removeProcessed(ISOChronology.monthOfQuarterRule());
        }
        
        // year
        Integer yearVal = merger.getValue(ISOChronology.yearRule());
        if (yearVal != null) {
            // year-month-day
            MonthOfYear moy = merger.getValue(ISOChronology.monthOfYearRule());
            Integer domVal = merger.getValue(ISOChronology.dayOfMonthRule());
            if (moy != null && domVal != null) {
                LocalDate date = merger.getContext().resolveDate(yearVal, moy.getValue(), domVal);
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(ISOChronology.yearRule());
                merger.removeProcessed(ISOChronology.monthOfYearRule());
                merger.removeProcessed(ISOChronology.dayOfMonthRule());
            }
            // year-day
            Integer doyVal = merger.getValue(ISOChronology.dayOfYearRule());
            if (doyVal != null) {
                merger.storeMerged(LocalDate.rule(), getDateFromDayOfYear(yearVal, doyVal));
                merger.removeProcessed(ISOChronology.yearRule());
                merger.removeProcessed(ISOChronology.dayOfYearRule());
            }
            // year-week-day
            Integer woyVal = merger.getValue(ISOChronology.weekOfYearRule());
            DayOfWeek dow = merger.getValue(ISOChronology.dayOfWeekRule());
            if (woyVal != null && dow != null) {
                LocalDate date = LocalDate.of(yearVal, 1, 1).plusWeeks(woyVal - 1);
                date = date.with(DateAdjusters.nextOrCurrent(dow));
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(ISOChronology.yearRule());
                merger.removeProcessed(ISOChronology.weekOfYearRule());
                merger.removeProcessed(ISOChronology.dayOfWeekRule());
            }
            // year-month-week-day
            Integer womVal = merger.getValue(ISOChronology.weekOfMonthRule());
            if (moy != null && womVal != null && dow != null) {
                LocalDate date = LocalDate.of(yearVal, moy, 1).plusWeeks(womVal - 1);
                date = date.with(DateAdjusters.nextOrCurrent(dow));
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(ISOChronology.yearRule());
                merger.removeProcessed(ISOChronology.monthOfYearRule());
                merger.removeProcessed(ISOChronology.weekOfMonthRule());
                merger.removeProcessed(ISOChronology.dayOfWeekRule());
            }
        }
        
        // weekyear-week-day
        Integer wbyVal = merger.getValue(ISOChronology.weekBasedYearRule());
        if (wbyVal != null) {
            Integer woy = merger.getValue(ISOChronology.weekOfWeekBasedYearRule());
            DayOfWeek dow = merger.getValue(ISOChronology.dayOfWeekRule());
            if (woy != null && dow != null) {
                // TODO: implement
                merger.removeProcessed(ISOChronology.weekBasedYearRule());
                merger.removeProcessed(ISOChronology.weekOfWeekBasedYearRule());
                merger.removeProcessed(ISOChronology.dayOfWeekRule());
            }
        }
        
        // LocalDateTime
        LocalDate date = merger.getValue(LocalDate.rule());
        LocalTime time = merger.getValue(LocalTime.rule());
        ZoneOffset offset = merger.getValue(ZoneOffset.rule());
        TimeZone zone = merger.getValue(TimeZone.rule());
        if (date != null && time != null) {
            merger.storeMerged(LocalDateTime.rule(), LocalDateTime.of(date, time));
            merger.removeProcessed(LocalDate.rule());
            merger.removeProcessed(LocalTime.rule());
        }
        
        // OffsetDate
        if (date != null && offset != null) {
            merger.storeMerged(OffsetDate.rule(), OffsetDate.of(date, offset));
            merger.removeProcessed(LocalDate.rule());
            merger.removeProcessed(ZoneOffset.rule());
        }
        
        // OffsetTime
        if (time != null && offset != null) {
            merger.storeMerged(OffsetTime.rule(), OffsetTime.of(time, offset));
            merger.removeProcessed(LocalTime.rule());
            merger.removeProcessed(ZoneOffset.rule());
        }
        
        // OffsetDateTime
        LocalDateTime ldt = merger.getValue(LocalDateTime.rule());
        if (ldt != null && offset != null) {
            merger.storeMerged(OffsetDateTime.rule(), OffsetDateTime.of(ldt, offset));
            merger.removeProcessed(LocalDateTime.rule());
            merger.removeProcessed(ZoneOffset.rule());
        } else {
            OffsetDate od = merger.getValue(OffsetDate.rule());
            OffsetTime ot = merger.getValue(OffsetTime.rule());
            if (od != null && ot != null ) {
                if (od.getOffset().equals(ot.getOffset()) == false) {
                    if (merger.getContext().isStrict()) {
                        throw new CalendricalRuleException("Unable to merge OffsetDate and OffsetTime as offsets differ", OffsetTime.rule());
                    } else {
                        // TODO test
                        ot = ot.adjustLocalTime(od.getOffset());
                    }
                }
                merger.storeMerged(OffsetDateTime.rule(), OffsetDateTime.of(od, ot, od.getOffset()));
                merger.removeProcessed(OffsetDate.rule());
                merger.removeProcessed(OffsetTime.rule());
            }
        }
        
        // ZonedDateTime
        OffsetDateTime odt = merger.getValue(OffsetDateTime.rule());
        if (odt != null && zone != null) {
            if (merger.getContext().isStrict()) {
                merger.storeMerged(ZonedDateTime.rule(), ZonedDateTime.of(odt, zone));
            } else {
                merger.storeMerged(ZonedDateTime.rule(), ZonedDateTime.ofInstant(odt, zone));
            }
            merger.removeProcessed(OffsetDateTime.rule());
            merger.removeProcessed(TimeZone.rule());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class MonthOfYearRule extends DateTimeFieldRule<MonthOfYear> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<MonthOfYear> INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super(MonthOfYear.class, ISOChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 12, true);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected MonthOfYear derive(Calendrical calendrical) {
            LocalDate date = calendrical.get(LocalDate.rule());
            return date != null ? date.getMonthOfYear() : null;
        }
        @Override
        public int convertValueToInt(MonthOfYear value) {
            return value.getValue();
        }
        @Override
        public MonthOfYear convertIntToValue(int value) {
            return MonthOfYear.of(value);
        }
        @Override
        protected MonthOfYear interpret(CalendricalMerger merger, Object value) {
            if (value instanceof Integer) {
                int val = (Integer) value;
                if (val < 1 || val > 12) {
                    merger.addToOverflow(Period.ofMonths(val - 1));  // TODO: MIN_VALUE overflow
                    val = 1;
                }
                return MonthOfYear.of(val);
            }
            return null;
        }
        @Override
        protected void createTextStores(EnumMap<TextStyle, TextStore> textStores, Locale locale) {
            DateFormatSymbols oldSymbols = new DateFormatSymbols(locale);
            String[] array = oldSymbols.getMonths();
            Map<Integer, String> map = new HashMap<Integer, String>();
            map.put(1, array[Calendar.JANUARY]);
            map.put(2, array[Calendar.FEBRUARY]);
            map.put(3, array[Calendar.MARCH]);
            map.put(4, array[Calendar.APRIL]);
            map.put(5, array[Calendar.MAY]);
            map.put(6, array[Calendar.JUNE]);
            map.put(7, array[Calendar.JULY]);
            map.put(8, array[Calendar.AUGUST]);
            map.put(9, array[Calendar.SEPTEMBER]);
            map.put(10, array[Calendar.OCTOBER]);
            map.put(11, array[Calendar.NOVEMBER]);
            map.put(12, array[Calendar.DECEMBER]);
            textStores.put(TextStyle.FULL, new TextStore(locale, map));
            array = oldSymbols.getShortMonths();
            map.clear();
            map.put(1, array[Calendar.JANUARY]);
            map.put(2, array[Calendar.FEBRUARY]);
            map.put(3, array[Calendar.MARCH]);
            map.put(4, array[Calendar.APRIL]);
            map.put(5, array[Calendar.MAY]);
            map.put(6, array[Calendar.JUNE]);
            map.put(7, array[Calendar.JULY]);
            map.put(8, array[Calendar.AUGUST]);
            map.put(9, array[Calendar.SEPTEMBER]);
            map.put(10, array[Calendar.OCTOBER]);
            map.put(11, array[Calendar.NOVEMBER]);
            map.put(12, array[Calendar.DECEMBER]);
            textStores.put(TextStyle.SHORT, new TextStore(locale, map));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class DayOfWeekRule extends DateTimeFieldRule<DayOfWeek> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<DayOfWeek> INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super(DayOfWeek.class, ISOChronology.INSTANCE, "DayOfWeek", DAYS, WEEKS, 1, 7, true);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DayOfWeek derive(Calendrical calendrical) {
            LocalDate date = calendrical.get(LocalDate.rule());
            return date != null ? getDayOfWeekFromDate(date) : null;
        }
        @Override
        public int convertValueToInt(DayOfWeek value) {
            return value.getValue();
        }
        @Override
        public DayOfWeek convertIntToValue(int value) {
            return DayOfWeek.of(value);
        }
        @Override
        protected DayOfWeek interpret(CalendricalMerger merger, Object value) {
            if (value instanceof Integer) {
                int val = (Integer) value;
                if (val < 1 || val > 7) {
                    merger.addToOverflow(Period.ofDays(val - 1));  // TODO: MIN_VALUE overflow
                    val = 1;
                }
                return DayOfWeek.of(val);
            }
            return null;
        }
        @Override
        protected void createTextStores(EnumMap<TextStyle, TextStore> textStores, Locale locale) {
            DateFormatSymbols oldSymbols = new DateFormatSymbols(locale);
            String[] array = oldSymbols.getWeekdays();
            Map<Integer, String> map = new HashMap<Integer, String>();
            map.put(1, array[Calendar.MONDAY]);
            map.put(2, array[Calendar.TUESDAY]);
            map.put(3, array[Calendar.WEDNESDAY]);
            map.put(4, array[Calendar.THURSDAY]);
            map.put(5, array[Calendar.FRIDAY]);
            map.put(6, array[Calendar.SATURDAY]);
            map.put(7, array[Calendar.SUNDAY]);
            textStores.put(TextStyle.FULL, new TextStore(locale, map));
            array = oldSymbols.getShortWeekdays();
            map.clear();
            map.put(1, array[Calendar.MONDAY]);
            map.put(2, array[Calendar.TUESDAY]);
            map.put(3, array[Calendar.WEDNESDAY]);
            map.put(4, array[Calendar.THURSDAY]);
            map.put(5, array[Calendar.FRIDAY]);
            map.put(6, array[Calendar.SATURDAY]);
            map.put(7, array[Calendar.SUNDAY]);
            textStores.put(TextStyle.SHORT, new TextStore(locale, map));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class QuarterOfYearRule extends DateTimeFieldRule<QuarterOfYear> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<QuarterOfYear> INSTANCE = new QuarterOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private QuarterOfYearRule() {
            super(QuarterOfYear.class, ISOChronology.INSTANCE, "QuarterOfYear", QUARTERS, YEARS, 1, 4);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected QuarterOfYear derive(Calendrical calendrical) {
            MonthOfYear moy = calendrical.get(monthOfYearRule());
            return moy != null ? QuarterOfYear.of(moy.ordinal() / 3 + 1) : null;
        }
        @Override
        public int convertValueToInt(QuarterOfYear value) {
            return value.getValue();
        }
        @Override
        public QuarterOfYear convertIntToValue(int value) {
            return QuarterOfYear.of(value);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class AmPmOfDayRule extends DateTimeFieldRule<AmPmOfDay> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<AmPmOfDay> INSTANCE = new AmPmOfDayRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private AmPmOfDayRule() {
            super(AmPmOfDay.class, ISOChronology.INSTANCE, "AmPmOfDay", _12_HOURS, DAYS, 0, 1, true);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected AmPmOfDay derive(Calendrical calendrical) {
            Integer hourVal = calendrical.get(hourOfDayRule());
            if (hourVal == null) {
                return null;
            }
            int hour = hourVal;
            hour = (hour < 0 ? 1073741832 + hour + 1073741832 : hour);  // add multiple of 24 to make positive
            return AmPmOfDay.of((hour % 24) / 12);
        }
        @Override
        public int convertValueToInt(AmPmOfDay value) {
            return value.getValue();
        }
        @Override
        public AmPmOfDay convertIntToValue(int value) {
            return AmPmOfDay.of(value);
        }
        @Override
        protected AmPmOfDay interpret(CalendricalMerger merger, Object value) {
            if (value instanceof Integer) {
                int val = (Integer) value;
                if (val < 0 || val > 1) {  // TODO: check this logic
                    int days = val > 0 ? val / 2 : ((val + 1) / 2) - 1;
                    merger.addToOverflow(Period.ofDays(days));
                    val = (val > 0 ? val % 2 : -(val % 2));
                }
                return AmPmOfDay.of(val);
            }
            return null;
        }
        @Override
        protected void createTextStores(EnumMap<TextStyle, TextStore> textStores, Locale locale) {
            DateFormatSymbols oldSymbols = new DateFormatSymbols(locale);
            String[] array = oldSymbols.getAmPmStrings();
            Map<Integer, String> map = new HashMap<Integer, String>();
            map.put(0, array[Calendar.AM]);
            map.put(1, array[Calendar.PM]);
            TextStore textStore = new TextStore(locale, map);
            textStores.put(TextStyle.FULL, textStore);
            textStores.put(TextStyle.SHORT, textStore);  // re-use, as we don't have different data
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class EpochDaysRule extends CalendricalRule<Long> implements Serializable {
        /** Singleton instance. */
        static final CalendricalRule<Long> INSTANCE = new EpochDaysRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EpochDaysRule() {
            super(Long.class, ISOChronology.INSTANCE, "EpochDays", DAYS, null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Long derive(Calendrical calendrical) {
            LocalDate date = calendrical.get(LocalDate.rule());
            return date != null ? date.toEpochDays() : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            long epochDays = merger.getValue(this);
            merger.storeMerged(LocalDate.rule(), LocalDate.ofEpochDays(epochDays));
            merger.removeProcessed(this);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class NanoOfDayRule extends CalendricalRule<Long> implements Serializable {
        /** Singleton instance. */
        static final CalendricalRule<Long> INSTANCE = new NanoOfDayRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private NanoOfDayRule() {
            super(Long.class, ISOChronology.INSTANCE, "NanoOfDay", NANOS, DAYS);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Long derive(Calendrical calendrical) {
            LocalTime time = calendrical.get(LocalTime.rule());
            return time != null ? time.toNanoOfDay() : null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            long nod = merger.getValue(this);
            merger.storeMerged(LocalTime.rule(), LocalTime.ofNanoOfDay(nod));
            merger.removeProcessed(this);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Single unit subclass, which means fewer classes to load at startup.
     */
    static final class Unit extends PeriodUnit {
        private static final long serialVersionUID = 1L;
        private final int ordinal;
        private Unit(int ordinal, String name, PeriodField equivalentPeriod, Duration estimatedDuration) {
            super(name, equivalentPeriod, estimatedDuration);
            this.ordinal = ordinal;  // allow space for new units
        }
        private Object readResolve() {
            return UNIT_CACHE[ordinal / 16];
        }
        @Override
        public int compareTo(PeriodUnit other) {
            if (other instanceof Unit) {
                return ordinal - ((Unit) other).ordinal;
            }
            return super.compareTo(other);
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Unit) {
                return ordinal == ((Unit) obj).ordinal;
            }
            return super.equals(obj);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Period unit for nanoseconds.
     */
    private static final Unit NANOS = new Unit(0 * 16, "Nanos", null, Duration.ofNanos(1));
    /**
     * Period unit for microseconds.
     */
    private static final Unit MICROS = new Unit(1 * 16, "Micros", PeriodField.of(1000, NANOS), Duration.ofNanos(1000));
    /**
     * Period unit for milliseconds.
     */
    private static final Unit MILLIS = new Unit(2 * 16, "Millis", PeriodField.of(1000, MICROS), Duration.ofMillis(1));
    /**
     * Period unit for seconds.
     */
    private static final Unit SECONDS = new Unit(3 * 16, "Seconds", PeriodField.of(1000, MILLIS), Duration.ofSeconds(1));
    /**
     * Period unit for minutes.
     */
    private static final Unit MINUTES = new Unit(4 * 16, "Minutes", PeriodField.of(60, SECONDS), Duration.ofSeconds(60));
    /**
     * Period unit for hours.
     */
    private static final Unit HOURS = new Unit(5 * 16, "Hours", PeriodField.of(60, MINUTES), Duration.ofSeconds(60 * 60));
    /**
     * Period unit for 12 hours half-days, used by AM/PM.
     */
    private static final Unit _12_HOURS = new Unit(6 * 16, "12Hours", PeriodField.of(12, HOURS), Duration.ofSeconds(12 * 60 * 60));
    /**
     * Period unit for 24 hour fixed length days.
     */
    private static final Unit _24_HOURS = new Unit(7 * 16, "24Hours", PeriodField.of(2, _12_HOURS), Duration.ofSeconds(24 * 60 * 60));

    /**
     * Period unit for days.
     */
    private static final Unit DAYS = new Unit(8 * 16, "Days", null, Duration.ofSeconds(86400));
    /**
     * Period unit for weeks.
     */
    private static final Unit WEEKS = new Unit(9 * 16, "Weeks", PeriodField.of(7, DAYS), Duration.ofSeconds(7L * 86400L));
    /**
     * Period unit for months.
     */
    private static final Unit MONTHS = new Unit(10 * 16, "Months", null, Duration.ofSeconds(31556952L / 12L));
    /**
     * Period unit for quarters.
     */
    private static final Unit QUARTERS = new Unit(11 * 16, "Quarters", PeriodField.of(3, MONTHS), Duration.ofSeconds(31556952L / 4));
    /**
     * Period unit for week-based-years.
     */
    private static final Unit WEEK_BASED_YEARS = new Unit(12 * 16, "WeekBasedYears", null, Duration.ofSeconds(364L * 86400L + 43200L));  // 364.5 days
    /**
     * Period unit for years.
     */
    private static final Unit YEARS = new Unit(13 * 16, "Years", PeriodField.of(4, QUARTERS), Duration.ofSeconds(31556952L));  // 365.2425 days
    /**
     * Period unit for decades.
     */
    private static final Unit DECADES = new Unit(14 * 16, "Decades", PeriodField.of(10, YEARS), Duration.ofSeconds(10L * 31556952L));
    /**
     * Period unit for centuries.
     */
    private static final Unit CENTURIES = new Unit(15 * 16, "Centuries", PeriodField.of(10, DECADES), Duration.ofSeconds(100L * 31556952L));
    /**
     * Period unit for millennia.
     */
    private static final Unit MILLENNIA = new Unit(16 * 16, "Millennia", PeriodField.of(10, CENTURIES), Duration.ofSeconds(1000L * 31556952L));
    /**
     * Period unit for eras.
     */
    private static final Unit ERAS = new Unit(17 * 16, "Eras", null, Duration.ofSeconds(31556952L * 2000000000L));

    /**
     * Cache of units for deserialization.
     * Indices must match ordinal passed to unit constructor.
     */
    private static final Unit[] UNIT_CACHE = new Unit[] {
        NANOS, MICROS, MILLIS, SECONDS, MINUTES, HOURS, _12_HOURS, _24_HOURS,
        DAYS, WEEKS, MONTHS, QUARTERS, WEEK_BASED_YEARS, YEARS,
        DECADES, CENTURIES, MILLENNIA, ERAS,
    };

    //-----------------------------------------------------------------------
    /**
     * Single rule subclass, which means fewer classes to load at startup.
     */
    static final class Rule extends DateTimeFieldRule<Integer> implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int ordinal;
        private transient final int smallestMaximum;
        private Rule(int ordinal, 
                String name,
                PeriodUnit periodUnit,
                PeriodUnit periodRange,
                int minimumValue,
                int maximumValue,
                int smallestMaximum) {
            super(Integer.class, ISOChronology.INSTANCE, name, periodUnit, periodRange, minimumValue, maximumValue);
            this.ordinal = ordinal;  // allow space for new rules
            this.smallestMaximum = smallestMaximum;
        }
        private Object readResolve() {
            return RULE_CACHE[ordinal / 16];
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            switch (ordinal) {
                case NANO_OF_SECOND_ORDINAL: {
                    LocalTime time = calendrical.get(LocalTime.rule());
                    return time != null ? time.getNanoOfSecond() : null;
                }
                case MILLI_OF_SECOND_ORDINAL: {
                    LocalTime time = calendrical.get(LocalTime.rule());
                    return time != null ? time.getNanoOfSecond() / 1000000 : null;
                }
                case MILLI_OF_DAY_ORDINAL: {
                    LocalTime time = calendrical.get(LocalTime.rule());
                    return time != null ? (int) (time.toNanoOfDay() / 1000000L) : null;
                }
                case SECOND_OF_MINUTE_ORDINAL: {
                    LocalTime time = calendrical.get(LocalTime.rule());
                    return time != null ? time.getSecondOfMinute() : null;
                }
                case SECOND_OF_DAY_ORDINAL: {
                    LocalTime time = calendrical.get(LocalTime.rule());
                    return time != null ? time.toSecondOfDay() : null;
                }
                case MINUTE_OF_HOUR_ORDINAL: {
                    LocalTime time = calendrical.get(LocalTime.rule());
                    return time != null ? time.getMinuteOfHour() : null;
                }
                case CLOCK_HOUR_OF_AMPM_ORDINAL: {
                    Integer hourVal = calendrical.get(hourOfAmPmRule());
                    return hourVal != null ? (hourVal + 12) % 13 : null;
                }
                case HOUR_OF_AMPM_ORDINAL: {
                    Integer hourVal = calendrical.get(hourOfDayRule());
                    return hourVal != null ? hourVal % 12 : null;
                }
                case CLOCK_HOUR_OF_DAY_ORDINAL: {
                    Integer hourVal = calendrical.get(hourOfDayRule());
                    return hourVal != null ? (hourVal + 24) % 25 : null;
                }
                case HOUR_OF_DAY_ORDINAL: {
                    LocalTime time = calendrical.get(LocalTime.rule());
                    return time != null ? time.getHourOfDay() : null;
                }
                case DAY_OF_MONTH_ORDINAL: {
                    LocalDate date = calendrical.get(LocalDate.rule());
                    return date != null ? date.getDayOfMonth() : null;
                }
                case DAY_OF_YEAR_ORDINAL: {
                    LocalDate date = calendrical.get(LocalDate.rule());
                    return date != null ? getDayOfYearFromDate(date) : null;
                }
                case MONTH_OF_QUARTER_ORDINAL: {
                    MonthOfYear moy = calendrical.get(monthOfYearRule());
                    return moy != null ? (moy.ordinal() % 3 + 1) : null;
                }
                case WEEK_OF_MONTH_ORDINAL: {
                    Integer domVal = calendrical.get(dayOfMonthRule());
                    return domVal != null ? (domVal + 6) / 7 : null;
                }
                case WEEK_OF_WEEK_BASED_YEAR_ORDINAL: {
                    LocalDate date = calendrical.get(LocalDate.rule());
                    return date != null ? getWeekOfWeekBasedYearFromDate(date) : null;
                }
                case WEEK_OF_YEAR_ORDINAL: {
                    Integer doyVal = calendrical.get(dayOfYearRule());
                    return doyVal != null ? (doyVal + 6) / 7 : null;
                }
                case WEEK_BASED_YEAR_ORDINAL: {
                    LocalDate date = calendrical.get(LocalDate.rule());
                    return date != null ? getWeekBasedYearFromDate(date) : null;
                }
                case YEAR_ORDINAL: {
                    LocalDate date = calendrical.get(LocalDate.rule());
                    return date != null ? date.getYear() : null;
                }
            }
            return null;
        }
        @Override
        public int getSmallestMaximumValue() {
            return smallestMaximum;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            switch (ordinal) {
                case DAY_OF_MONTH_ORDINAL: {
                    MonthOfYear moy = calendrical.get(monthOfYearRule());
                    if (moy == null) {
                        return 31;
                    }
                    Integer year = calendrical.get(yearRule());
                    return year != null ? moy.lengthInDays(isLeapYear(year)) : moy.maxLengthInDays();
                }
                case DAY_OF_YEAR_ORDINAL: {
                    Integer year = calendrical.get(yearRule());
                    return (year != null && isLeapYear(year) == false ? 365 : 366);
                }
                case WEEK_OF_MONTH_ORDINAL: {
                    Integer year = calendrical.get(yearRule());
                    MonthOfYear moy = calendrical.get(monthOfYearRule());
                    if (year != null && moy == MonthOfYear.FEBRUARY) {
                        return isLeapYear(year) ? 5 : 4;
                    }
                    return getMaximumValue();
                }
                case WEEK_OF_WEEK_BASED_YEAR_ORDINAL: {
                    // TODO: derive from WeekBasedYear
                    LocalDate date = calendrical.get(LocalDate.rule());
                    if (date == null ) {
                        return 53;
                    }
                    date = date.withDayOfMonth(1).withMonthOfYear(1);
                    if (date.getDayOfWeek() == DayOfWeek.THURSDAY ||
                            (date.getDayOfWeek() == DayOfWeek.WEDNESDAY && isLeapYear(date.getYear()))) {
                        return 53;
                    }
                    return 52;
                }
            }
            return super.getMaximumValue();
        }
        @Override
        public int compareTo(CalendricalRule<Integer> other) {
            if (other instanceof Rule) {
                return ordinal - ((Rule) other).ordinal;
            }
            return super.compareTo(other);
        }
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Rule) {
                return ordinal == ((Rule) obj).ordinal;
            }
            return super.equals(obj);
        }
    }

    //-----------------------------------------------------------------------
    private static final int NANO_OF_SECOND_ORDINAL = 0 * 16;
    private static final int MILLI_OF_SECOND_ORDINAL = 1 * 16;
    private static final int MILLI_OF_DAY_ORDINAL = 2 * 16;
    private static final int SECOND_OF_MINUTE_ORDINAL = 3 * 16;
    private static final int SECOND_OF_DAY_ORDINAL = 4 * 16;
    private static final int MINUTE_OF_HOUR_ORDINAL = 5 * 16;
    private static final int CLOCK_HOUR_OF_AMPM_ORDINAL = 6 * 16;
    private static final int HOUR_OF_AMPM_ORDINAL = 7 * 16;
    private static final int CLOCK_HOUR_OF_DAY_ORDINAL = 8 * 16;
    private static final int HOUR_OF_DAY_ORDINAL = 9 * 16;
    private static final int DAY_OF_MONTH_ORDINAL = 10 * 16;
    private static final int DAY_OF_YEAR_ORDINAL = 11 * 16;
    private static final int WEEK_OF_MONTH_ORDINAL = 12 * 16;
    private static final int WEEK_OF_WEEK_BASED_YEAR_ORDINAL = 13 * 16;
    private static final int WEEK_OF_YEAR_ORDINAL = 14 * 16;
    private static final int MONTH_OF_QUARTER_ORDINAL = 15 * 16;
    private static final int WEEK_BASED_YEAR_ORDINAL = 16 * 16;
    private static final int YEAR_ORDINAL = 17 * 16;
    
    //-----------------------------------------------------------------------
    private static final Rule NANO_OF_SECOND = new Rule(NANO_OF_SECOND_ORDINAL, "NanoOfSecond", NANOS, SECONDS, 0, 999999999, 999999999);
    private static final Rule MILLI_OF_SECOND = new Rule(MILLI_OF_SECOND_ORDINAL, "MilliOfSecond", MILLIS, SECONDS, 0, 999, 999);
    private static final Rule MILLI_OF_DAY = new Rule(MILLI_OF_DAY_ORDINAL, "MilliOfDay", MILLIS, DAYS, 0, 86399999, 86399999);
    private static final Rule SECOND_OF_MINUTE = new Rule(SECOND_OF_MINUTE_ORDINAL, "SecondOfMinute", SECONDS, MINUTES, 0, 59, 59);
    private static final Rule SECOND_OF_DAY = new Rule(SECOND_OF_DAY_ORDINAL, "SecondOfDay", SECONDS, DAYS, 0, 86399, 86399);
    private static final Rule MINUTE_OF_HOUR = new Rule(MINUTE_OF_HOUR_ORDINAL, "MinuteOfHour", MINUTES, HOURS, 0, 59, 59);
    private static final Rule CLOCK_HOUR_OF_AMPM = new Rule(CLOCK_HOUR_OF_AMPM_ORDINAL, "ClockHourOfAmPm", HOURS, _12_HOURS, 1, 12, 12);
    private static final Rule HOUR_OF_AMPM = new Rule(HOUR_OF_AMPM_ORDINAL, "HourOfAmPm", HOURS, _12_HOURS, 0, 11, 11);
    private static final Rule CLOCK_HOUR_OF_DAY = new Rule(CLOCK_HOUR_OF_DAY_ORDINAL, "ClockHourOfDay", HOURS, DAYS, 1, 24, 24);
    private static final Rule HOUR_OF_DAY = new Rule(HOUR_OF_DAY_ORDINAL, "HourOfDay", HOURS, DAYS, 0, 23, 23);
    private static final Rule DAY_OF_MONTH = new Rule(DAY_OF_MONTH_ORDINAL, "DayOfMonth", DAYS, MONTHS, 1, 31, 28);
    private static final Rule DAY_OF_YEAR = new Rule(DAY_OF_YEAR_ORDINAL, "DayOfYear", DAYS, YEARS, 1, 366, 365);
    private static final Rule WEEK_OF_MONTH = new Rule(WEEK_OF_MONTH_ORDINAL, "WeekOfMonth", WEEKS, MONTHS, 1, 5, 4);
    private static final Rule WEEK_OF_WEEK_BASED_YEAR = new Rule(WEEK_OF_WEEK_BASED_YEAR_ORDINAL, "WeekOfWeekBasedYear", WEEKS, WEEK_BASED_YEARS, 1, 53, 52);
    private static final Rule WEEK_OF_YEAR = new Rule(WEEK_OF_YEAR_ORDINAL, "WeekOfYear", WEEKS, YEARS, 1, 53, 53);
    private static final Rule MONTH_OF_QUARTER = new Rule(MONTH_OF_QUARTER_ORDINAL, "MonthOfQuarter", MONTHS, QUARTERS, 1, 3, 3);
    private static final Rule WEEK_BASED_YEAR = new Rule(WEEK_BASED_YEAR_ORDINAL, "WeekBasedYear", WEEK_BASED_YEARS, null, MIN_WEEK_BASED_YEAR, MAX_WEEK_BASED_YEAR, MAX_WEEK_BASED_YEAR);
    private static final Rule YEAR = new Rule(YEAR_ORDINAL, "Year", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR, Year.MAX_YEAR);

    /**
     * Cache of units for deserialization.
     * Indices must match ordinal passed to rule constructor.
     */
    private static final Rule[] RULE_CACHE = new Rule[] {
        NANO_OF_SECOND, MILLI_OF_SECOND, MILLI_OF_DAY,
        SECOND_OF_MINUTE, SECOND_OF_DAY, MINUTE_OF_HOUR,
        CLOCK_HOUR_OF_AMPM, HOUR_OF_AMPM, CLOCK_HOUR_OF_DAY, HOUR_OF_DAY,
        DAY_OF_MONTH, DAY_OF_YEAR,
        WEEK_OF_MONTH, WEEK_OF_WEEK_BASED_YEAR, WEEK_OF_YEAR,
        MONTH_OF_QUARTER,
        WEEK_BASED_YEAR, YEAR,
    };

}
