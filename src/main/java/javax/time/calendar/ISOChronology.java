/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.period.PeriodUnits.DAYS;
import static javax.time.period.PeriodUnits.HOURS;
import static javax.time.period.PeriodUnits.MILLIS;
import static javax.time.period.PeriodUnits.MINUTES;
import static javax.time.period.PeriodUnits.MONTHS;
import static javax.time.period.PeriodUnits.NANOS;
import static javax.time.period.PeriodUnits.QUARTERS;
import static javax.time.period.PeriodUnits.SECONDS;
import static javax.time.period.PeriodUnits.TWELVE_HOURS;
import static javax.time.period.PeriodUnits.WEEKS;
import static javax.time.period.PeriodUnits.WEEKYEARS;
import static javax.time.period.PeriodUnits.YEARS;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.time.CalendricalException;
import javax.time.MathUtils;
import javax.time.calendar.field.AmPmOfDay;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.WeekBasedYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;
import javax.time.period.Period;

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
     * The singleton instance of <code>ISOChronology</code>.
     */
    public static final ISOChronology INSTANCE = new ISOChronology();
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

    /**
     * Adds a number of years to the specified year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to add to, from MIN_YEAR to MAX_YEAR
     * @param years  the years to add
     * @return the result
     * @throws CalendricalException if the result exceeds the supported year range
     */
    static int addYears(int year, int years) {
        int result = year + years;
        if (((year ^ result) < 0 && (year ^ years) >= 0) || yearRule().isValidValue(result) == false) {
            throw new CalendricalException("Addition exceeds the supported year range: " + year + " + " + years);
        }
        return result;
    }

    /**
     * Subtracts a number of years from the specified year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to add to, from MIN_YEAR to MAX_YEAR
     * @param years  the years to subtract
     * @return the result
     * @throws CalendricalException if the result exceeds the supported year range
     */
    static int subtractYears(int year, int years) {
        int result = year - years;
        if (((year ^ result) < 0 && (year ^ years) < 0) || yearRule().isValidValue(result) == false) {
            throw new CalendricalException("Subtraction exceeds the supported year range: " + year + " - " + years);
        }
        return result;
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
        return DayOfWeek.dayOfWeek(dow0 + 1);
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
     * @param year  the day-of-year, valid
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
        MonthOfYear moy = MonthOfYear.monthOfYear(month);
        int dom = dayOfYear - array[month - 1];
        return LocalDate.date(year, moy, dom);
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
    public static int getWeekOfWeekBasedYearFromDate(LocalDate date) {
        int wby = getWeekBasedYearFromDate(date);
        LocalDate yearStart = LocalDate.date(wby, MonthOfYear.JANUARY, 4);
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

//    //-----------------------------------------------------------------------
//    /**
//     * Checks if the specified year is a leap year.
//     * <p>
//     * ISO chronology leap years are every 4 years. A special rule applies
//     * for years divisible by 100, which are only leap if also divisible by 400.
//     *
//     * @param year  the year to check
//     * @return true if the year is a leap year
//     */
//    private boolean isLeapYear(int year) {
//        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
//    }

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
        return YearRule.INSTANCE;
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
     * Gets the rule for the month of year field in the ISO chronology.
     * <p>
     * This field counts months sequentially from the start of the year.
     * The values follow the ISO-8601 standard and normal human interactions.
     * These define January as value 1 to December as value 12.
     * <p>
     * The enum {@link MonthOfYear} should be used wherever possible in
     * applications when referring to the day of the week to avoid
     * hard-coding the values.
     *
     * @return the rule for the month of year field, never null
     */
    public static DateTimeFieldRule<MonthOfYear> monthOfYearRule() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the ISO chronology.
     * <p>
     * This field counts days sequentially from the start of the month.
     * The first day of the month is 1 and the last is 28, 29, 30 or 31
     * depending on the month and whether it is a leap year.
     *
     * @return the rule for the day of month field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfMonthRule() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the ISO chronology.
     * <p>
     * This field counts days sequentially from the start of the year.
     * The first day of the year is 1 and the last is 365, or 366 in a leap year.
     *
     * @return the rule for the day of year field, never null
     */
    public static DateTimeFieldRule<Integer> dayOfYearRule() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the week-based-year field in the ISO chronology.
     * <p>
     * This field is the year that results from calculating weeks with the ISO-8601 algorithm.
     * See {@link #weekOfWeekBasedYearRule() week of week-based-year} for details.
     * <p>
     * The week year will either be 52 or 53 weeks long, depending on the
     * result of the algorithm for a particular date.
     *
     * @return the rule for the week-based-year field, never null
     */
    public static DateTimeFieldRule<Integer> weekBasedYearRule() {
        return WeekBasedYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the week of week-based-year field in the ISO chronology.
     * <p>
     * This field counts weeks using the ISO-8601 algorithm.
     * The first week of the year is the week which has at least 4 days in the year
     * using a Monday to Sunday week definition. Thus it is possible for the first
     * week to start on any day from the 29th December in the previous year to the
     * 4th January in the new year. The year which is aligned with this field is
     * known as the {@link #weekBasedYearRule() week-based-year}.
     *
     * @return the rule for the week of week-based-year field, never null
     */
    public static DateTimeFieldRule<Integer> weekOfWeekBasedYearRule() {
        return WeekOfWeekBasedYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field.
     * <p>
     * This field uses the ISO-8601 values for the day of week.
     * These define Monday as value 1 to Sunday as value 7.
     * <p>
     * The enum {@link DayOfWeek} should be used wherever possible in
     * applications when referring to the day of the week value to avoid
     * needing to remember the values from 1 to 7.
     *
     * @return the rule for the day of week field, never null
     */
    public static DateTimeFieldRule<DayOfWeek> dayOfWeekRule() {
        return DayOfWeekRule.INSTANCE;
    }

    /**
     * Gets the rule for the week of year field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days starting from the first
     * of January. The 1st to the 7th of January is always week 1 while the
     * 8th to the 14th is always week 2.
     *
     * @return the rule for the week of year field, never null
     */
    public static DateTimeFieldRule<Integer> weekOfYearRule() {
        return WeekOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the quarter of year field in the ISO chronology.
     * <p>
     * This field counts quarters sequentially from the start of the year.
     * The first quarter of the year is 1 and the last is 4. Each quarter
     * lasts exactly three months.
     *
     * @return the rule for the quarter of year field, never null
     */
    public static DateTimeFieldRule<Integer> quarterOfYearRule() {
        return QuarterOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of quarter field in the ISO chronology.
     * <p>
     * This field counts months sequentially from the start of the quarter.
     * The first month of the quarter is 1 and the last is 3. Each quarter
     * lasts exactly three months.
     *
     * @return the rule for the month of quarter field, never null
     */
    public static DateTimeFieldRule<Integer> monthOfQuarterRule() {
        return MonthOfQuarterRule.INSTANCE;
    }

    /**
     * Gets the rule for the week of month field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days starting from the first
     * day of the month. The 1st to the 7th of a month is always week 1 while the
     * 8th to the 14th is always week 2 and so on.
     * <p>
     * This field can be used to create concepts such as 'the second saturday'
     * of a month. To achieve this, setup a {@link DateTimeFields} instance
     * using this rule and the {@link #dayOfWeekRule() day of week} rule.
     *
     * @return the rule for the week of month field, never null
     */
    public static DateTimeFieldRule<Integer> weekOfMonthRule() {
        return WeekOfMonthRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the hour of day field.
     * <p>
     * This field counts hours sequentially from the start of the day.
     * The values run from 0 to 23.
     *
     * @return the rule for the hour of day field, never null
     */
    public static DateTimeFieldRule<Integer> hourOfDayRule() {
        return HourOfDayRule.INSTANCE;
    }

    /**
     * Gets the rule for the minute of hour field.
     * <p>
     * This field counts minutes sequentially from the start of the hour.
     * The values run from 0 to 59.
     *
     * @return the rule for the minute of hour field, never null
     */
    public static DateTimeFieldRule<Integer> minuteOfHourRule() {
        return MinuteOfHourRule.INSTANCE;
    }

    /**
     * Gets the rule for the second of minute field.
     * <p>
     * This field counts seconds sequentially from the start of the minute.
     * The values run from 0 to 59.
     *
     * @return the rule for the second of minute field, never null
     */
    public static DateTimeFieldRule<Integer> secondOfMinuteRule() {
        return SecondOfMinuteRule.INSTANCE;
    }

    /**
     * Gets the rule for the nano of second field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the second.
     * The values run from 0 to 999,999,999.
     *
     * @return the rule for the nano of second field, never null
     */
    public static DateTimeFieldRule<Integer> nanoOfSecondRule() {
        return NanoOfSecondRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the second of day field.
     * <p>
     * This field counts seconds sequentially from the start of the day.
     * The values run from 0 to 86399.
     *
     * @return the rule for the second of day field, never null
     */
    public static DateTimeFieldRule<Integer> secondOfDayRule() {
        return SecondOfDayRule.INSTANCE;
    }

    /**
     * Gets the rule for the milli of day field.
     * <p>
     * This field counts milliseconds sequentially from the start of the day.
     * The values run from 0 to 86399999.
     *
     * @return the rule for the nano of second field, never null
     */
    public static DateTimeFieldRule<Integer> milliOfDayRule() {
        return MilliOfDayRule.INSTANCE;
    }

    /**
     * Gets the rule for the milli of second field.
     * <p>
     * This field counts milliseconds sequentially from the start of the second.
     * The values run from 0 to 999.
     *
     * @return the rule for the nano of second field, never null
     */
    public static DateTimeFieldRule<Integer> milliOfSecondRule() {
        return MilliOfSecondRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the AM/PM of day field.
     * <p>
     * This field defines the half-day AM/PM value. The hour of day from 0 to 11 is
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
        return HourOfAmPmRule.INSTANCE;
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
        return ClockHourOfAmPmRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Merges the set of fields known by this chronology.
     *
     * @param merger  the merger to use, not null
     */
    void merge(CalendricalMerger merger) {
        // milli of day
        Integer modVal = merger.getValue(ISOChronology.milliOfDayRule());
        if (modVal != null) {
            merger.storeMerged(LocalTime.rule(), LocalTime.fromNanoOfDay(modVal * 1000000L));
            merger.removeProcessed(ISOChronology.milliOfDayRule());
        }
        
        // second of day
        Integer sodVal = merger.getValue(ISOChronology.secondOfDayRule());
        if (modVal != null) {
            Integer nosVal = merger.getValue(ISOChronology.nanoOfSecondRule());
            if (nosVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.fromSecondOfDay(sodVal, nosVal));
                merger.removeProcessed(ISOChronology.nanoOfSecondRule());
            } else {
                Integer mosVal = merger.getValue(ISOChronology.milliOfSecondRule());
                if (mosVal != null) {
                    merger.storeMerged(LocalTime.rule(), LocalTime.fromSecondOfDay(sodVal, mosVal * 1000000));
                    merger.removeProcessed(ISOChronology.milliOfSecondRule());
                } else {
                    merger.storeMerged(LocalTime.rule(), LocalTime.fromSecondOfDay(sodVal));
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
                    merger.addToOverflow(Period.days(1));
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
                merger.storeMerged(LocalTime.rule(), LocalTime.time(hourVal, minuteVal, secondVal, nanoVal));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
                merger.removeProcessed(ISOChronology.secondOfMinuteRule());
                merger.removeProcessed(ISOChronology.nanoOfSecondRule());
            } else if (minuteVal != null && secondVal != null && mosVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.time(hourVal, minuteVal, secondVal, mosVal * 1000000));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
                merger.removeProcessed(ISOChronology.secondOfMinuteRule());
                merger.removeProcessed(ISOChronology.milliOfSecondRule());
            } else if (minuteVal != null && secondVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.time(hourVal, minuteVal, secondVal, 0));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
                merger.removeProcessed(ISOChronology.secondOfMinuteRule());
            } else if (minuteVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.time(hourVal, minuteVal, 0, 0));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
            } else {
                merger.storeMerged(LocalTime.rule(), LocalTime.time(hourVal, 0));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
            }
        }
        
        // quarter-of-year and month-of-quarter
        Integer qoyVal = merger.getValue(ISOChronology.quarterOfYearRule());
        Integer moqVal = merger.getValue(ISOChronology.monthOfQuarterRule());
        if (qoyVal != null && moqVal != null) {
            MonthOfYear moy = MonthOfYear.monthOfYear((qoyVal - 1) * 3 + moqVal);
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
                LocalDate date = LocalDate.date(yearVal, 1, 1).plusWeeks(woyVal - 1);
                date = date.with(DateAdjusters.nextOrCurrent(dow));
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(ISOChronology.yearRule());
                merger.removeProcessed(ISOChronology.weekOfYearRule());
                merger.removeProcessed(ISOChronology.dayOfWeekRule());
            }
            // year-month-week-day
            Integer womVal = merger.getValue(ISOChronology.weekOfMonthRule());
            if (moy != null && womVal != null && dow != null) {
                LocalDate date = LocalDate.date(yearVal, moy, 1).plusWeeks(womVal - 1);
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
            merger.storeMerged(LocalDateTime.rule(), LocalDateTime.dateTime(date, time));
            merger.removeProcessed(LocalDate.rule());
            merger.removeProcessed(LocalTime.rule());
        }
        
        // OffsetDate
        if (date != null && offset != null) {
            merger.storeMerged(OffsetDate.rule(), OffsetDate.date(date, offset));
            merger.removeProcessed(LocalDate.rule());
            merger.removeProcessed(ZoneOffset.rule());
        }
        
        // OffsetTime
        if (time != null && offset != null) {
            merger.storeMerged(OffsetTime.rule(), OffsetTime.time(time, offset));
            merger.removeProcessed(LocalTime.rule());
            merger.removeProcessed(ZoneOffset.rule());
        }
        
        // OffsetDateTime
        LocalDateTime ldt = merger.getValue(LocalDateTime.rule());
        if (ldt != null && offset != null) {
            merger.storeMerged(OffsetDateTime.rule(), OffsetDateTime.dateTime(ldt, offset));
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
                merger.storeMerged(OffsetDateTime.rule(), OffsetDateTime.dateTime(od, ot, od.getOffset()));
                merger.removeProcessed(OffsetDate.rule());
                merger.removeProcessed(OffsetTime.rule());
            }
        }
        
        // ZonedDateTime
        OffsetDateTime odt = merger.getValue(OffsetDateTime.rule());
        if (odt != null && zone != null) {
            if (merger.getContext().isStrict()) {
                merger.storeMerged(ZonedDateTime.rule(), ZonedDateTime.dateTime(odt, zone));
            } else {
                merger.storeMerged(ZonedDateTime.rule(), ZonedDateTime.fromInstant(odt, zone));
            }
            merger.removeProcessed(OffsetDateTime.rule());
            merger.removeProcessed(TimeZone.rule());
        }
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class TwoDigitYearRule extends DateTimeFieldRule implements Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule INSTANCE = new TwoDigitYearRule();
//        /** A serialization identifier for this class. */
//        private static final long serialVersionUID = 1L;
//        /** Constructor. */
//        private TwoDigitYearRule() {
//            super(ISOChronology.INSTANCE, "TwoDigitYear", YEARS, CENTURIES, 0, 99);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        public Integer getValueQuiet(LocalDate date, LocalTime time) {
//            if (date == null) {
//                return null;
//            }
//            int year = date.getYear() % 100;
//            return year < 0 ? year + 100 : year;
//        }
//        @Override
//        protected Integer deriveValue(Calendrical.FieldMap fieldMap) {
//            Integer yVal = yearRule().getValueQuiet(fieldMap);
//            if (yVal == null) {
//                return null;
//            }
//            int year = yVal % 100;
//            return year < 0 ? year + 100 : year;
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class YearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new YearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearRule() {
            super(Integer.class, ISOChronology.INSTANCE, "Year", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalDate date = calendrical.get(LocalDate.rule());
            return date != null ? date.getYear() : null;
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
            return MonthOfYear.monthOfYear(value);
        }
        @Override
        protected MonthOfYear interpret(CalendricalMerger merger, Object value) {
            if (value instanceof Integer) {
                int val = (Integer) value;
                if (val < 1 || val > 12) {
                    merger.addToOverflow(Period.months(val - 1));  // TODO: MIN_VALUE overflow
                    val = 1;
                }
                return MonthOfYear.monthOfYear(val);
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
    static final class DayOfMonthRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new DayOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule() {
            super(Integer.class, ISOChronology.INSTANCE, "DayOfMonth", DAYS, MONTHS, 1, 31);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 28;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            MonthOfYear moy = calendrical.get(monthOfYearRule());
            if (moy == null) {
                return 31;
            }
            Integer year = calendrical.get(yearRule());
            return year != null ? moy.lengthInDays(year) : moy.maxLengthInDays();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalDate date = calendrical.get(LocalDate.rule());
            return date != null ? date.getDayOfMonth() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class DayOfYearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new DayOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule() {
            super(Integer.class, ISOChronology.INSTANCE, "DayOfYear", DAYS, YEARS, 1, 366);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 365;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            Integer year = calendrical.get(yearRule());
            return (year != null && isLeapYear(year) == false ? 365 : 366);
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalDate date = calendrical.get(LocalDate.rule());
            return date != null ? getDayOfYearFromDate(date) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class WeekBasedYearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new WeekBasedYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekBasedYearRule() {
            super(Integer.class, ISOChronology.INSTANCE, "WeekBasedYear", WEEKYEARS, null, WeekBasedYear.MIN_YEAR, WeekBasedYear.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalDate date = calendrical.get(LocalDate.rule());
            return date != null ? getWeekBasedYearFromDate(date) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class WeekOfWeekBasedYearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new WeekOfWeekBasedYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekOfWeekBasedYearRule() {
            super(Integer.class, ISOChronology.INSTANCE, "WeekOfWeekBasedYear", WEEKS, WEEKYEARS, 1, 53);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 52;
        }
//        @Override
//        public int getMaximumValue(Calendrical calendrical) {
//            // TODO
//            return getMaximumValue();
//        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalDate date = calendrical.get(LocalDate.rule());
            return date != null ? getWeekOfWeekBasedYearFromDate(date) : null;
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
            return DayOfWeek.dayOfWeek(value);
        }
        @Override
        protected DayOfWeek interpret(CalendricalMerger merger, Object value) {
            if (value instanceof Integer) {
                int val = (Integer) value;
                if (val < 1 || val > 7) {
                    merger.addToOverflow(Period.days(val - 1));  // TODO: MIN_VALUE overflow
                    val = 1;
                }
                return DayOfWeek.dayOfWeek(val);
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
    static final class WeekOfYearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new WeekOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekOfYearRule() {
            super(Integer.class, ISOChronology.INSTANCE, "WeekOfYear", WEEKS, YEARS, 1, 53);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            Integer doyVal = calendrical.get(dayOfYearRule());
            return doyVal != null ? (doyVal + 6) / 7 : null;
       }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class QuarterOfYearRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new QuarterOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private QuarterOfYearRule() {
            super(Integer.class, ISOChronology.INSTANCE, "QuarterOfYear", QUARTERS, YEARS, 1, 4);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            MonthOfYear moy = calendrical.get(monthOfYearRule());
            return moy != null ? moy.getQuarterOfYear().getValue() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class MonthOfQuarterRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new MonthOfQuarterRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfQuarterRule() {
            super(Integer.class, ISOChronology.INSTANCE, "MonthOfQuarter", MONTHS, QUARTERS, 1, 3);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            MonthOfYear moy = calendrical.get(monthOfYearRule());
            return moy != null ? moy.getMonthOfQuarter() : null;
       }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class WeekOfMonthRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new WeekOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekOfMonthRule() {
            super(Integer.class, ISOChronology.INSTANCE, "WeekOfMonth", WEEKS, MONTHS, 1, 5);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 4;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            Integer year = calendrical.get(yearRule());
            if (year != null) {  // TODO: check month
                return Year.isoYear(year).isLeap() ? 5 : 4;
            }
            return getMaximumValue();
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            Integer domVal = calendrical.get(dayOfMonthRule());
            return domVal != null ? (domVal + 6) / 7 : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class HourOfDayRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new HourOfDayRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private HourOfDayRule() {
            super(Integer.class, ISOChronology.INSTANCE, "HourOfDay", HOURS, DAYS, 0, 23);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalTime time = calendrical.get(LocalTime.rule());
            return time != null ? time.getHourOfDay() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class MinuteOfHourRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new MinuteOfHourRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MinuteOfHourRule() {
            super(Integer.class, ISOChronology.INSTANCE, "MinuteOfHour", MINUTES, HOURS, 0, 59);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalTime time = calendrical.get(LocalTime.rule());
            return time != null ? time.getMinuteOfHour() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class SecondOfMinuteRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new SecondOfMinuteRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private SecondOfMinuteRule() {
            super(Integer.class, ISOChronology.INSTANCE, "SecondOfMinute", SECONDS, MINUTES, 0, 59);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalTime time = calendrical.get(LocalTime.rule());
            return time != null ? time.getSecondOfMinute() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class NanoOfSecondRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new NanoOfSecondRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private NanoOfSecondRule() {
            super(Integer.class, ISOChronology.INSTANCE, "NanoOfSecond", NANOS, SECONDS, 0, 999999999);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalTime time = calendrical.get(LocalTime.rule());
            return time != null ? time.getNanoOfSecond() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class SecondOfDayRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new SecondOfDayRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private SecondOfDayRule() {
            super(Integer.class, ISOChronology.INSTANCE, "SecondOfDay", SECONDS, DAYS, 0, 86399);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalTime time = calendrical.get(LocalTime.rule());
            return time != null ? time.toSecondOfDay() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class MilliOfDayRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new MilliOfDayRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MilliOfDayRule() {
            super(Integer.class, ISOChronology.INSTANCE, "MilliOfDay", MILLIS, DAYS, 0, 86399999);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalTime time = calendrical.get(LocalTime.rule());
            return time != null ? (int) (time.toNanoOfDay() / 1000000L) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class MilliOfSecondRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new MilliOfSecondRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MilliOfSecondRule() {
            super(Integer.class, ISOChronology.INSTANCE, "MilliOfSecond", MILLIS, SECONDS, 0, 999);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            LocalTime time = calendrical.get(LocalTime.rule());
            return time != null ? time.getNanoOfSecond() / 1000000 : null;
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
            super(AmPmOfDay.class, ISOChronology.INSTANCE, "AmPmOfDay", TWELVE_HOURS, DAYS, 0, 1, true);
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
            return AmPmOfDay.amPmOfDay((hour % 24) / 12);
        }
        @Override
        public int convertValueToInt(AmPmOfDay value) {
            return value.getValue();
        }
        @Override
        public AmPmOfDay convertIntToValue(int value) {
            return AmPmOfDay.amPmOfDay(value);
        }
        @Override
        protected AmPmOfDay interpret(CalendricalMerger merger, Object value) {
            if (value instanceof Integer) {
                int val = (Integer) value;
                if (val < 0 || val > 1) {  // TODO: check this logic
                    int days = val > 0 ? val / 2 : ((val + 1) / 2) - 1;
                    merger.addToOverflow(Period.days(days));
                    val = (val > 0 ? val % 2 : -(val % 2));
                }
                return AmPmOfDay.amPmOfDay(val);
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
    static final class HourOfAmPmRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new HourOfAmPmRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private HourOfAmPmRule() {
            super(Integer.class, ISOChronology.INSTANCE, "HourOfAmPm", HOURS, TWELVE_HOURS, 0, 11);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            Integer hourVal = calendrical.get(hourOfDayRule());
            if (hourVal == null) {
                return null;
            }
            long hour = hourVal;
            // TODO: Remove overflow handling
            hour = (hour < 0 ? hour + 2147483664L : hour) % 12;  // add multiple of 24 to make positive
            return (int) hour;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    static final class ClockHourOfAmPmRule extends DateTimeFieldRule<Integer> implements Serializable {
        /** Singleton instance. */
        static final DateTimeFieldRule<Integer> INSTANCE = new ClockHourOfAmPmRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private ClockHourOfAmPmRule() {
            super(Integer.class, ISOChronology.INSTANCE, "ClockHourOfAmPm", HOURS, TWELVE_HOURS, 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected Integer derive(Calendrical calendrical) {
            Integer hourVal = calendrical.get(hourOfDayRule());
            if (hourVal == null) {
                return null;
            }
            long hour = hourVal;
            // TODO: Remove overflow handling
            hour = (hour < 0 ? hour + 2147483664L : hour) % 12;  // add multiple of 24 to make positive
            return (int) (hour == 0 ? 12 : hour);
        }
    }

}
