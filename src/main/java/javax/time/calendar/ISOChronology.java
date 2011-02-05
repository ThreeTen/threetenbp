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

import static javax.time.calendar.ISOPeriodUnit.DAYS;
import static javax.time.calendar.ISOPeriodUnit.HOURS;
import static javax.time.calendar.ISOPeriodUnit.MILLIS;
import static javax.time.calendar.ISOPeriodUnit.MINUTES;
import static javax.time.calendar.ISOPeriodUnit.MONTHS;
import static javax.time.calendar.ISOPeriodUnit.NANOS;
import static javax.time.calendar.ISOPeriodUnit.QUARTERS;
import static javax.time.calendar.ISOPeriodUnit.SECONDS;
import static javax.time.calendar.ISOPeriodUnit.WEEKS;
import static javax.time.calendar.ISOPeriodUnit.WEEK_BASED_YEARS;
import static javax.time.calendar.ISOPeriodUnit.YEARS;
import static javax.time.calendar.ISOPeriodUnit._12_HOURS;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        return date.getMonthOfYear().getMonthStartDayOfYear(date.isLeapYear()) + date.getDayOfMonth() - 1;
    }

    /**
     * Calculates the date from a year and day-of-year.
     *
     * @param year  the year, valid
     * @param dayOfYear  the day-of-year, valid
     * @return the date, never null
     */
    static LocalDate getDateFromDayOfYear(int year, int dayOfYear) {
        dayOfYearRule().checkValue(dayOfYear);
        boolean leap = ISOChronology.isLeapYear(year);
        if (dayOfYear == 366 && leap == false) {
            throw new InvalidCalendarFieldException("DayOfYear 366 is invalid for year " + year, dayOfYearRule());
        }
        MonthOfYear moy = MonthOfYear.of((dayOfYear - 1) / 31 + 1);
        int monthEnd = moy.getMonthEndDayOfYear(leap);
        if (dayOfYear > monthEnd) {
            moy = moy.next();
        }
        int dom = dayOfYear - moy.getMonthStartDayOfYear(leap) + 1;
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
        Year year = Year.of(date);  // use ISO year object so previous/next are checked
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
    public static DateTimeFieldRule yearRule() {
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
    public static DateTimeFieldRule monthOfYearRule() {
        return MONTH_OF_YEAR;
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
    public static DateTimeFieldRule dayOfMonthRule() {
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
    public static DateTimeFieldRule dayOfYearRule() {
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
    public static DateTimeFieldRule weekBasedYearRule() {
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
    public static DateTimeFieldRule weekOfWeekBasedYearRule() {
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
    public static DateTimeFieldRule dayOfWeekRule() {
        return DAY_OF_WEEK;
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
    public static DateTimeFieldRule weekOfYearRule() {
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
    public static DateTimeFieldRule quarterOfYearRule() {
        return QUARTER_OF_YEAR;
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
    public static DateTimeFieldRule monthOfQuarterRule() {
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
    public static DateTimeFieldRule weekOfMonthRule() {
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
    public static DateTimeFieldRule hourOfDayRule() {
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
    public static DateTimeFieldRule minuteOfHourRule() {
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
    public static DateTimeFieldRule secondOfMinuteRule() {
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
    public static DateTimeFieldRule nanoOfSecondRule() {
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
    public static DateTimeFieldRule secondOfDayRule() {
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
    public static DateTimeFieldRule milliOfDayRule() {
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
    public static DateTimeFieldRule milliOfSecondRule() {
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
    public static DateTimeFieldRule amPmOfDayRule() {
        return AMPM_OF_DAY;
    }

    /**
     * Gets the rule for the hour of AM/PM field from 0 to 11.
     * <p>
     * This field counts hours sequentially from the start of the half-day AM/PM.
     * The values run from 0 to 11.
     *
     * @return the rule for the hour of AM/PM field, never null
     */
    public static DateTimeFieldRule hourOfAmPmRule() {
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
    public static DateTimeFieldRule clockHourOfAmPmRule() {
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
    public static DateTimeFieldRule clockHourOfDayRule() {
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
     * Merges the set of fields known by this chronology.
     *
     * @param merger  the merger to use, not null
     */
    void merge(CalendricalMerger merger) {
        // milli-of-day
        DateTimeField modVal = merger.getValue(ISOChronology.milliOfDayRule());
        if (modVal != null) {
            merger.storeMerged(LocalTime.rule(), LocalTime.ofNanoOfDay(modVal.getValidValue() * 1000000L));
            merger.removeProcessed(ISOChronology.milliOfDayRule());
        }
        
        // second-of-day
        DateTimeField sodVal = merger.getValue(ISOChronology.secondOfDayRule());
        if (modVal != null) {
            DateTimeField nosVal = merger.getValue(ISOChronology.nanoOfSecondRule());
            if (nosVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.ofSecondOfDay(sodVal.getValidValue(), nosVal.getValidValue()));
                merger.removeProcessed(ISOChronology.nanoOfSecondRule());
            } else {
                DateTimeField mosVal = merger.getValue(ISOChronology.milliOfSecondRule());
                if (mosVal != null) {
                    merger.storeMerged(LocalTime.rule(), LocalTime.ofSecondOfDay(sodVal.getValidValue(), mosVal.getValidValue() * 1000000));
                    merger.removeProcessed(ISOChronology.milliOfSecondRule());
                } else {
                    merger.storeMerged(LocalTime.rule(), LocalTime.ofSecondOfDay(sodVal.getValidValue()));
                }
            }
            merger.removeProcessed(ISOChronology.secondOfDayRule());
        }
        
        // am-hour
        DateTimeField amPm = merger.getValue(ISOChronology.amPmOfDayRule());
        if (amPm != null) {
            DateTimeField hapVal = merger.getValue(ISOChronology.hourOfAmPmRule());
            if (hapVal != null) {
                int hourOfDay = amPm.getValidValue() * 12 + hapVal.getValidValue();
                merger.storeMerged(ISOChronology.hourOfDayRule(), hourOfDay);
                merger.removeProcessed(ISOChronology.amPmOfDayRule());
                merger.removeProcessed(ISOChronology.hourOfAmPmRule());
            }
            DateTimeField chapVal = merger.getValue(ISOChronology.clockHourOfAmPmRule());
            if (chapVal != null) {
                int hourOfDay = amPm.getValidValue() * 12 + chapVal.getValidValue();
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
        DateTimeField hourVal = merger.getValue(ISOChronology.hourOfDayRule());
        if (hourVal != null) {
            DateTimeField minuteVal = merger.getValue(ISOChronology.minuteOfHourRule());
            DateTimeField secondVal = merger.getValue(ISOChronology.secondOfMinuteRule());
            DateTimeField mosVal = merger.getValue(ISOChronology.milliOfSecondRule());
            DateTimeField nanoVal = merger.getValue(ISOChronology.nanoOfSecondRule());
            if (minuteVal != null && secondVal != null && nanoVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal.getValidValue(), minuteVal.getValidValue(), secondVal.getValidValue(), nanoVal.getValidValue()));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
                merger.removeProcessed(ISOChronology.secondOfMinuteRule());
                merger.removeProcessed(ISOChronology.nanoOfSecondRule());
            } else if (minuteVal != null && secondVal != null && mosVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal.getValidValue(), minuteVal.getValidValue(), secondVal.getValidValue(), mosVal.getValidValue() * 1000000));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
                merger.removeProcessed(ISOChronology.secondOfMinuteRule());
                merger.removeProcessed(ISOChronology.milliOfSecondRule());
            } else if (minuteVal != null && secondVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal.getValidValue(), minuteVal.getValidValue(), secondVal.getValidValue(), 0));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
                merger.removeProcessed(ISOChronology.secondOfMinuteRule());
            } else if (minuteVal != null) {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal.getValidValue(), minuteVal.getValidValue(), 0, 0));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
                merger.removeProcessed(ISOChronology.minuteOfHourRule());
            } else {
                merger.storeMerged(LocalTime.rule(), LocalTime.of(hourVal.getValidValue(), 0));
                merger.removeProcessed(ISOChronology.hourOfDayRule());
            }
        }
        
        // quarter-of-year and month-of-quarter
        DateTimeField qoy = merger.getValue(ISOChronology.quarterOfYearRule());
        DateTimeField moqVal = merger.getValue(ISOChronology.monthOfQuarterRule());
        if (qoy != null && moqVal != null) {
            int moy = (qoy.getValidValue() - 1) * 3 + moqVal.getValidValue();
            merger.storeMerged(ISOChronology.monthOfYearRule(), moy);
            merger.removeProcessed(ISOChronology.quarterOfYearRule());
            merger.removeProcessed(ISOChronology.monthOfQuarterRule());
        }
        
        // year
        DateTimeField yearVal = merger.getValue(ISOChronology.yearRule());
        if (yearVal != null) {
            // year-month-day
            DateTimeField moy = merger.getValue(ISOChronology.monthOfYearRule());
            DateTimeField domVal = merger.getValue(ISOChronology.dayOfMonthRule());
            if (moy != null && domVal != null) {
                LocalDate date = merger.getContext().resolveDate(yearVal.getValidValue(), moy.getValidValue(), domVal.getValidValue());
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(ISOChronology.yearRule());
                merger.removeProcessed(ISOChronology.monthOfYearRule());
                merger.removeProcessed(ISOChronology.dayOfMonthRule());
            }
            // year-day
            DateTimeField doyVal = merger.getValue(ISOChronology.dayOfYearRule());
            if (doyVal != null) {
                merger.storeMerged(LocalDate.rule(), getDateFromDayOfYear(yearVal.getValidValue(), doyVal.getValidValue()));
                merger.removeProcessed(ISOChronology.yearRule());
                merger.removeProcessed(ISOChronology.dayOfYearRule());
            }
            // year-week-day
            DateTimeField woyVal = merger.getValue(ISOChronology.weekOfYearRule());
            DateTimeField dow = merger.getValue(ISOChronology.dayOfWeekRule());
            if (woyVal != null && dow != null) {
                LocalDate date = LocalDate.of(yearVal.getValidValue(), 1, 1).plusWeeks(woyVal.getValidValue() - 1);
                date = date.with(DateAdjusters.nextOrCurrent(DayOfWeek.of(dow.getValidValue())));
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(ISOChronology.yearRule());
                merger.removeProcessed(ISOChronology.weekOfYearRule());
                merger.removeProcessed(ISOChronology.dayOfWeekRule());
            }
            // year-month-week-day
            DateTimeField womVal = merger.getValue(ISOChronology.weekOfMonthRule());
            if (moy != null && womVal != null && dow != null) {
                LocalDate date = LocalDate.of(yearVal.getValidValue(), moy.getValidValue(), 1).plusWeeks(womVal.getValidValue() - 1);
                date = date.with(DateAdjusters.nextOrCurrent(DayOfWeek.of(dow.getValidValue())));
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(ISOChronology.yearRule());
                merger.removeProcessed(ISOChronology.monthOfYearRule());
                merger.removeProcessed(ISOChronology.weekOfMonthRule());
                merger.removeProcessed(ISOChronology.dayOfWeekRule());
            }
        }
        
        // weekyear-week-day
        DateTimeField wbyVal = merger.getValue(ISOChronology.weekBasedYearRule());
        if (wbyVal != null) {
            DateTimeField woy = merger.getValue(ISOChronology.weekOfWeekBasedYearRule());
            DateTimeField dow = merger.getValue(ISOChronology.dayOfWeekRule());
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
        ZoneId zone = merger.getValue(ZoneId.rule());
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
            if (od != null && ot != null) {
                if (od.getOffset().equals(ot.getOffset()) == false) {
                    if (merger.getContext().isStrict()) {
                        throw new CalendricalRuleException("Unable to merge OffsetDate and OffsetTime as offsets differ", OffsetTime.rule());
                    } else {
                        // TODO test
                        ot = ot.withOffsetSameInstant(od.getOffset());
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
            merger.removeProcessed(ZoneId.rule());
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
     * Single rule subclass, which means fewer classes to load at startup.
     */
    static final class Rule extends DateTimeFieldRule implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int ordinal;
        private final transient int smallestMaximum;
        private Rule(int ordinal, 
                String name,
                PeriodUnit periodUnit,
                PeriodUnit periodRange,
                int minimumValue,
                int maximumValue,
                int smallestMaximum) {
            super(ISOChronology.INSTANCE, name, periodUnit, periodRange, minimumValue, maximumValue,
                    ordinal == AMPM_OF_DAY_ORDINAL || ordinal == DAY_OF_WEEK_ORDINAL || ordinal == MONTH_OF_YEAR_ORDINAL);
            this.ordinal = ordinal;  // allow space for new rules
            this.smallestMaximum = smallestMaximum;
        }
        private Object readResolve() {
            return RULE_CACHE[ordinal / 16];
        }
        @Override
        protected DateTimeField derive(Calendrical calendrical) {
            switch (ordinal) {
                case NANO_OF_SECOND_ORDINAL:
                case MILLI_OF_SECOND_ORDINAL:
                case MILLI_OF_DAY_ORDINAL:
                case SECOND_OF_MINUTE_ORDINAL:
                case SECOND_OF_DAY_ORDINAL:
                case MINUTE_OF_HOUR_ORDINAL:
                case HOUR_OF_DAY_ORDINAL: {
                    LocalTime time = calendrical.get(LocalTime.rule());
                    if (time != null) {
                        switch (ordinal) {
                            case NANO_OF_SECOND_ORDINAL: return field(time.getNanoOfSecond());
                            case MILLI_OF_SECOND_ORDINAL: return field(time.getNanoOfSecond() / 1000000);
                            case MILLI_OF_DAY_ORDINAL: return field((int) (time.toNanoOfDay() / 1000000L));
                            case SECOND_OF_MINUTE_ORDINAL: return field(time.getSecondOfMinute());
                            case SECOND_OF_DAY_ORDINAL: return field(time.toSecondOfDay());
                            case MINUTE_OF_HOUR_ORDINAL: return field(time.getMinuteOfHour());
                            case HOUR_OF_DAY_ORDINAL: return field(time.getHourOfDay());
                        }
                    }
                    break;
                }
                case DAY_OF_WEEK_ORDINAL:
                case DAY_OF_MONTH_ORDINAL:
                case DAY_OF_YEAR_ORDINAL:
                case WEEK_OF_WEEK_BASED_YEAR_ORDINAL:
                case WEEK_BASED_YEAR_ORDINAL:
                case MONTH_OF_YEAR_ORDINAL:
                case YEAR_ORDINAL: {
                    LocalDate date = calendrical.get(LocalDate.rule());
                    if (date != null) {
                        switch (ordinal) {
                            case DAY_OF_WEEK_ORDINAL: return field(getDayOfWeekFromDate(date).getValue());
                            case DAY_OF_MONTH_ORDINAL: return field(date.getDayOfMonth());
                            case DAY_OF_YEAR_ORDINAL: return field(getDayOfYearFromDate(date));
                            case WEEK_OF_WEEK_BASED_YEAR_ORDINAL: return field(getWeekOfWeekBasedYearFromDate(date));
                            case WEEK_BASED_YEAR_ORDINAL: return field(getWeekBasedYearFromDate(date));
                            case MONTH_OF_YEAR_ORDINAL: return field(date.getMonthOfYear().getValue());
                            case YEAR_ORDINAL: return field(date.getYear());
                        }
                    }
                    break;
                }
                case CLOCK_HOUR_OF_AMPM_ORDINAL: {
                    DateTimeField hourVal = calendrical.get(hourOfAmPmRule());
                    return hourVal != null ? field(((hourVal.getValidValue() + 11) % 12) + 1) : null;
                }
                case HOUR_OF_AMPM_ORDINAL: {
                    DateTimeField hourVal = calendrical.get(hourOfDayRule());
                    return hourVal != null ? field(hourVal.getValidValue() % 12) : null;
                }
                case CLOCK_HOUR_OF_DAY_ORDINAL: {
                    DateTimeField hourVal = calendrical.get(hourOfDayRule());
                    return hourVal != null ? field(((hourVal.getValidValue() + 23) % 24) + 1) : null;
                }
                case AMPM_OF_DAY_ORDINAL: {
                    DateTimeField hourVal = calendrical.get(hourOfDayRule());
                    return hourVal != null ? field(hourVal.getValidValue() / 12) : null;
                }
                case MONTH_OF_QUARTER_ORDINAL: {
                    DateTimeField moy = calendrical.get(monthOfYearRule());
                    return moy != null ? field(((moy.getValidValue() - 1) % 3 + 1)) : null;
                }
                case QUARTER_OF_YEAR_ORDINAL: {
                    DateTimeField moy = calendrical.get(monthOfYearRule());
                    return moy != null ? field((moy.getValidValue() - 1) / 3 + 1) : null;
                }
                case WEEK_OF_MONTH_ORDINAL: {
                    DateTimeField domVal = calendrical.get(dayOfMonthRule());
                    return domVal != null ? field((domVal.getValidValue() + 6) / 7) : null;
                }
                case WEEK_OF_YEAR_ORDINAL: {
                    DateTimeField doyVal = calendrical.get(dayOfYearRule());
                    return doyVal != null ? field((doyVal.getValidValue() + 6) / 7) : null;
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
                    DateTimeField moy = calendrical.get(monthOfYearRule());
                    if (moy == null) {
                        return 31;
                    }
                    DateTimeField year = calendrical.get(yearRule());
                    if (year != null) {
                        return MonthOfYear.of(moy.getValidValue()).lengthInDays(isLeapYear(year.getValidValue()));
                    }
                    return MonthOfYear.of(moy.getValidValue()).maxLengthInDays();
                }
                case DAY_OF_YEAR_ORDINAL: {
                    DateTimeField year = calendrical.get(yearRule());
                    return (year != null && isLeapYear(year.getValidValue()) == false ? 365 : 366);
                }
                case WEEK_OF_MONTH_ORDINAL: {
                    DateTimeField year = calendrical.get(yearRule());
                    DateTimeField moy = calendrical.get(monthOfYearRule());
                    if (year != null && moy.getValue() == 2) {
                        return isLeapYear(year.getValidValue()) ? 5 : 4;
                    }
                    return getMaximumValue();
                }
                case WEEK_OF_WEEK_BASED_YEAR_ORDINAL: {
                    // TODO: derive from WeekBasedYear
                    LocalDate date = calendrical.get(LocalDate.rule());
                    if (date == null) {
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
        protected void createTextStores(EnumMap<TextStyle, TextStore> textStores, Locale locale) {
            DateFormatSymbols oldSymbols = new DateFormatSymbols(locale);
            switch (ordinal) {
                case AMPM_OF_DAY_ORDINAL: {
                    String[] array = oldSymbols.getAmPmStrings();
                    Map<Integer, String> map = new HashMap<Integer, String>();
                    map.put(0, array[Calendar.AM]);
                    map.put(1, array[Calendar.PM]);
                    TextStore textStore = new TextStore(locale, map);
                    textStores.put(TextStyle.FULL, textStore);
                    textStores.put(TextStyle.SHORT, textStore);  // re-use, as we don't have different data
                    break;
                }
                case DAY_OF_WEEK_ORDINAL: {
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
                    break;
                }
                case MONTH_OF_YEAR_ORDINAL: {
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
                    break;
                }
            }
        }
        @Override
        public int compareTo(CalendricalRule<?> other) {
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
        @Override
        public int hashCode() {
            return ordinal;
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
    private static final int AMPM_OF_DAY_ORDINAL = 10 * 16;
    private static final int DAY_OF_WEEK_ORDINAL = 11 * 16;
    private static final int DAY_OF_MONTH_ORDINAL = 12 * 16;
    private static final int DAY_OF_YEAR_ORDINAL = 13 * 16;
    private static final int WEEK_OF_MONTH_ORDINAL = 14 * 16;
    private static final int WEEK_OF_WEEK_BASED_YEAR_ORDINAL = 15 * 16;
    private static final int WEEK_OF_YEAR_ORDINAL = 16 * 16;
    private static final int MONTH_OF_QUARTER_ORDINAL = 17 * 16;
    private static final int MONTH_OF_YEAR_ORDINAL = 18 * 16;
    private static final int QUARTER_OF_YEAR_ORDINAL = 19 * 16;
    private static final int WEEK_BASED_YEAR_ORDINAL = 20 * 16;
    private static final int YEAR_ORDINAL = 21 * 16;
    
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
    private static final Rule AMPM_OF_DAY = new Rule(AMPM_OF_DAY_ORDINAL, "AmPmOfDay", _12_HOURS, DAYS, 0, 1, 1);
    private static final Rule DAY_OF_WEEK = new Rule(DAY_OF_WEEK_ORDINAL, "DayOfWeek", DAYS, WEEKS, 1, 7, 7);
    private static final Rule DAY_OF_MONTH = new Rule(DAY_OF_MONTH_ORDINAL, "DayOfMonth", DAYS, MONTHS, 1, 31, 28);
    private static final Rule DAY_OF_YEAR = new Rule(DAY_OF_YEAR_ORDINAL, "DayOfYear", DAYS, YEARS, 1, 366, 365);
    private static final Rule WEEK_OF_MONTH = new Rule(WEEK_OF_MONTH_ORDINAL, "WeekOfMonth", WEEKS, MONTHS, 1, 5, 4);
    private static final Rule WEEK_OF_WEEK_BASED_YEAR = new Rule(WEEK_OF_WEEK_BASED_YEAR_ORDINAL, "WeekOfWeekBasedYear", WEEKS, WEEK_BASED_YEARS, 1, 53, 52);
    private static final Rule WEEK_OF_YEAR = new Rule(WEEK_OF_YEAR_ORDINAL, "WeekOfYear", WEEKS, YEARS, 1, 53, 53);
    private static final Rule MONTH_OF_QUARTER = new Rule(MONTH_OF_QUARTER_ORDINAL, "MonthOfQuarter", MONTHS, QUARTERS, 1, 3, 3);
    private static final Rule MONTH_OF_YEAR = new Rule(MONTH_OF_YEAR_ORDINAL, "MonthOfYear", MONTHS, YEARS, 1, 12, 12);
    private static final Rule QUARTER_OF_YEAR = new Rule(QUARTER_OF_YEAR_ORDINAL, "QuarterOfYear", QUARTERS, YEARS, 1, 4, 4);
    private static final Rule WEEK_BASED_YEAR = new Rule(WEEK_BASED_YEAR_ORDINAL, "WeekBasedYear", WEEK_BASED_YEARS, null, MIN_WEEK_BASED_YEAR, MAX_WEEK_BASED_YEAR, MAX_WEEK_BASED_YEAR);
    private static final Rule YEAR = new Rule(YEAR_ORDINAL, "Year", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR, Year.MAX_YEAR);

    /**
     * Cache of units for deserialization.
     * Indices must match ordinal passed to rule constructor.
     */
    private static final Rule[] RULE_CACHE = new Rule[] {
        NANO_OF_SECOND, MILLI_OF_SECOND, MILLI_OF_DAY,
        SECOND_OF_MINUTE, SECOND_OF_DAY, MINUTE_OF_HOUR,
        CLOCK_HOUR_OF_AMPM, HOUR_OF_AMPM, CLOCK_HOUR_OF_DAY, HOUR_OF_DAY, AMPM_OF_DAY,
        DAY_OF_WEEK, DAY_OF_MONTH, DAY_OF_YEAR,
        WEEK_OF_MONTH, WEEK_OF_WEEK_BASED_YEAR, WEEK_OF_YEAR,
        MONTH_OF_QUARTER, MONTH_OF_YEAR, QUARTER_OF_YEAR,
        WEEK_BASED_YEAR, YEAR,
    };

}
