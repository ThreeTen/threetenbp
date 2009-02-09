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

import static javax.time.period.PeriodUnits.*;

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.calendar.field.AmPmOfDay;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.WeekBasedYear;
import javax.time.calendar.field.WeekOfWeekBasedYear;
import javax.time.calendar.field.Year;

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
//    /** Number of months in one year. */
//    private static final int MONTHS_PER_YEAR = 12;
//    /** Number of seconds in one day. */
//    private static final int SECONDS_PER_DAY = 60 * 60 * 24;
//    /** Number of seconds in one hour. */
//    private static final int SECONDS_PER_HOUR = 60 * 60;
//    /** Number of seconds in one minute. */
//    private static final int SECONDS_PER_MINUTE = 60;
//    /** The length of months in a standard year. */
//    private static final int[] STANDARD_MONTH_LENGTHS = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
//    /** The length of months in a leap year. */
//    private static final int[] LEAP_MONTH_LENGTHS = new int[] {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
//    /** The start of months in a standard year. */
//    private static final int[] STANDARD_MONTH_START = new int[] {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
//    /** The start of months in a leap year. */
//    private static final int[] LEAP_MONTH_START = new int[] {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

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
    /**
     * Gets the rule for the year field in the ISO chronology.
     * <p>
     * This field counts years sequentially from the epoch year of 1970.
     * The defintion follows the ISO-8601 rules which mean that there is no
     * historical cutover from the Julian to Gregorian calendar, typically
     * defined as occurring in October 1582.
     * <p>
     * The implication of this is that historical dates will not be accurate.
     * All work requiring accurate historical dates must use the appropriate
     * chronology that defines the Gregorian cutover.
     * <p>
     * A further implication of the ISO-8601 rules is that the year zero
     * exists. This roughly equates to 1 BC/BCE, however the alignment is
     * not exact due to the lack of a Julian/Gregorian cutover.
     *
     * @return the rule for the year field, never null
     */
    @Override
    public DateTimeFieldRule year() {
        return YearRule.INSTANCE;
    }

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
    @Override
    public DateTimeFieldRule monthOfYear() {
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
    @Override
    public DateTimeFieldRule dayOfMonth() {
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
    @Override
    public DateTimeFieldRule dayOfYear() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field.
     * <p>
     * This field uses the ISO-8601 values for the day of week.
     * These define Monday as value 1 to Sunday as value 7.
     * <p>
     * The enum {@link DayOfWeek} should be used wherever possible in
     * applications when referring to the day of the week to avoid
     * hard-coding the values.
     *
     * @return the rule for the day of week field, never null
     */
    @Override
    public DateTimeFieldRule dayOfWeek() {
        return DayOfWeekRule.INSTANCE;
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
    @Override
    public DateTimeFieldRule hourOfDay() {
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
    @Override
    public DateTimeFieldRule minuteOfHour() {
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
    @Override
    public DateTimeFieldRule secondOfMinute() {
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
    @Override
    public DateTimeFieldRule nanoOfSecond() {
        return NanoOfSecondRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the year field in the ISO chronology.
     * <p>
     * This field counts years sequentially from the epoch year of 1970.
     * The defintion follows the ISO-8601 rules which mean that there is no
     * historical cutover from the Julian to Gregorian calendar, typically
     * defined as occurring in October 1582.
     * <p>
     * The implication of this is that historical dates will not be accurate.
     * All work requiring accurate historical dates must use the appropriate
     * chronology that defines the Gregorian cutover.
     * <p>
     * A further implication of the ISO-8601 rules is that the year zero
     * exists. This roughly equates to 1 BC/BCE, however the alignment is
     * not exact due to the lack of a Julian/Gregorian cutover.
     *
     * @return the rule for the year field, never null
     */
    public static DateTimeFieldRule yearRule() {
        return YearRule.INSTANCE;
    }

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
    public static DateTimeFieldRule monthOfYearRule() {
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
    public static DateTimeFieldRule dayOfMonthRule() {
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
    public static DateTimeFieldRule dayOfYearRule() {
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
    public static DateTimeFieldRule weekBasedYearRule() {
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
    public static DateTimeFieldRule weekOfWeekBasedYearRule() {
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
    public static DateTimeFieldRule dayOfWeekRule() {
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
    public static DateTimeFieldRule weekOfYearRule() {
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
    public static DateTimeFieldRule quarterOfYearRule() {
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
    public static DateTimeFieldRule monthOfQuarterRule() {
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
    public static DateTimeFieldRule weekOfMonthRule() {
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
    public static DateTimeFieldRule hourOfDayRule() {
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
    public static DateTimeFieldRule minuteOfHourRule() {
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
    public static DateTimeFieldRule secondOfMinuteRule() {
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
    public static DateTimeFieldRule nanoOfSecondRule() {
        return NanoOfSecondRule.INSTANCE;
    }

    /**
     * Gets the rule for the milli of day field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the second.
     * The values run from 0 to 86399999.
     *
     * @return the rule for the nano of second field, never null
     */
    public static DateTimeFieldRule milliOfDayRule() {
        return MilliOfDayRule.INSTANCE;
    }

    /**
     * Gets the rule for the milli of second field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the second.
     * The values run from 0 to 999.
     *
     * @return the rule for the nano of second field, never null
     */
    public static DateTimeFieldRule milliOfSecondRule() {
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
    public static DateTimeFieldRule amPmOfDayRule() {
        return AmPmOfDayRule.INSTANCE;
    }

    /**
     * Gets the rule for the hour of AM/PM field.
     * <p>
     * This field counts hours sequentially from the start of the half-day AM/PM.
     * The values run from 0 to 11.
     *
     * @return the rule for the hour of AM/PM field, never null
     */
    public static DateTimeFieldRule hourOfAmPmRule() {
        return HourOfAmPmRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class YearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new YearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearRule() {
            super(ISOChronology.INSTANCE, "Year", YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : date.getYear().getValue());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer moyVal = merger.getValue(ISOChronology.monthOfYearRule());
            Integer domVal = merger.getValue(ISOChronology.dayOfMonthRule());
            if (moyVal != null && domVal != null) {
                int year = merger.getValueInt(this);
                LocalDate date = merger.getContext().resolveDate(year, moyVal, domVal);
                merger.storeMergedDate(date);
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.monthOfYearRule());
                merger.markFieldAsProcessed(ISOChronology.dayOfMonthRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super(ISOChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : date.getMonthOfYear().getValue());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfMonthRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule() {
            super(ISOChronology.INSTANCE, "DayOfMonth", DAYS, MONTHS, 1, 31);
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
            Integer year = calendrical.getValue(yearRule());
            Integer moy = calendrical.getValue(monthOfYearRule());
            if (year != null && moy != null) {
                MonthOfYear month = MonthOfYear.monthOfYear(moy);
                return month.lengthInDays(Year.isoYear(year));
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : date.getDayOfMonth().getValue());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule() {
            super(ISOChronology.INSTANCE, "DayOfYear", DAYS, YEARS, 1, 366);
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
            Integer year = calendrical.getValue(yearRule());
            if (year != null) {
                return Year.isoYear(year).lengthInDays();
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : date.getDayOfYear().getValue());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer year = merger.getValue(ISOChronology.yearRule());
            if (year != null) {
                int doy = merger.getValueInt(this);
                if (merger.isStrict() || doy >= 1 && doy <= 365) {  // range is valid for all years
                    merger.storeMergedDate(DayOfYear.dayOfYear(doy).createDate(Year.isoYear(year)));
                } else {
                    merger.storeMergedDate(LocalDate.date(year, 1, 1).plusDays(((long) doy) - 1));  // MIN/MAX handled ok
                }
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.yearRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class WeekBasedYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new WeekBasedYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekBasedYearRule() {
            super(ISOChronology.INSTANCE, "Weekyear", WEEKYEARS, null, WeekBasedYear.MIN_YEAR, WeekBasedYear.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : WeekBasedYear.weekyear(date).getValue());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            // TODO: implement, move to Weekyear?
            Integer woy = merger.getValue(ISOChronology.weekOfWeekBasedYearRule());
            Integer dow = merger.getValue(ISOChronology.dayOfWeekRule());
            if (woy != null && dow != null) {
                int wyear = merger.getValueInt(this);
//                if (merger.isStrict() || woy >= 1 && woy <= 52) {  // range is valid for all years
//                    merger.storeMergedDate(DayOfYear.dayOfYear(doy).createDate(Year.isoYear(year)));
//                } else {
//                    merger.storeMergedDate(LocalDate.date(year, 1, 1).plusDays(((long) doy) - 1));  // MIN/MAX handled ok
//                }
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.weekOfWeekBasedYearRule());
                merger.markFieldAsProcessed(ISOChronology.dayOfWeekRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class WeekOfWeekBasedYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new WeekOfWeekBasedYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekOfWeekBasedYearRule() {
            super(ISOChronology.INSTANCE, "WeekOfWeekyear", WEEKS, WEEKYEARS, 1, 53);
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
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : WeekOfWeekBasedYear.weekOfWeekyear(date).getValue());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super(ISOChronology.INSTANCE, "DayOfWeek", DAYS, WEEKS, 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : date.getDayOfWeek().getValue());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class WeekOfYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new WeekOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekOfYearRule() {
            super(ISOChronology.INSTANCE, "WeekOfYear", WEEKS, YEARS, 1, 53);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : ((date.getDayOfYear().getValue() - 1) % 7) + 1);
        }
        @Override
        protected Integer deriveValue(Calendrical.FieldMap fieldMap) {
            Integer doyVal = monthOfYearRule().getValueQuiet(fieldMap);
            if (doyVal == null) {
                return null;
            }
            int doy = doyVal;
            return (doy >= 1 ? ((doy - 1) / 7) + 1 : 0);  // TODO negatives
       }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer year = merger.getValue(ISOChronology.yearRule());
            Integer dow = merger.getValue(ISOChronology.dayOfWeekRule());
            if (year != null && dow != null) {
                int woy = merger.getValueInt(this);
                LocalDate date = LocalDate.date(year, 1, 1).plusDays((((long) woy) - 1) * 7);
                date = date.with(DateAdjusters.nextOrCurrent(DayOfWeek.dayOfWeek(dow)));
                merger.storeMergedDate(date);
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.yearRule());
                merger.markFieldAsProcessed(ISOChronology.dayOfWeekRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class QuarterOfYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new QuarterOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private QuarterOfYearRule() {
            super(ISOChronology.INSTANCE, "QuarterOfYear", QUARTERS, YEARS, 1, 4);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return date == null ? null : date.getMonthOfYear().getQuarterOfYear().getValue();
        }
        @Override
        protected Integer deriveValue(Calendrical.FieldMap fieldMap) {
            Integer moyVal = monthOfYearRule().getValueQuiet(fieldMap);
            if (moyVal == null) {
                return null;
            }
            int moy = moyVal;
            return (moy >= 1 ? ((moy - 1) / 3) + 1 : 0);  // TODO negatives
        }
        @Override
        protected void mergeFields(Calendrical.Merger merger) {
            Integer moqVal = merger.getValue(ISOChronology.monthOfQuarterRule());
            if (moqVal != null) {
                // TODO negatives
                int qoy = merger.getValueInt(this);
                qoy = MathUtils.safeDecrement(qoy);
                int moq = MathUtils.safeDecrement(moqVal);
                int moy = MathUtils.safeAdd(MathUtils.safeMultiply(qoy, 3), moq);
                moy = MathUtils.safeIncrement(moy);
                merger.storeMergedField(ISOChronology.monthOfYearRule(), moy);
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.monthOfQuarterRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MonthOfQuarterRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new MonthOfQuarterRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfQuarterRule() {
            super(ISOChronology.INSTANCE, "MonthOfQuarter", MONTHS, QUARTERS, 1, 3);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return date == null ? null : date.getMonthOfYear().getMonthOfQuarter();
        }
        @Override
        protected Integer deriveValue(Calendrical.FieldMap fieldMap) {
            Integer moyVal = monthOfYearRule().getValueQuiet(fieldMap);
            if (moyVal == null) {
                return null;
            }
            int moy = moyVal;
            return (moy >= 1 ? ((moy - 1) % 3) + 1 : 3 + (moy % 3));  // TODO negatives
       }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class WeekOfMonthRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new WeekOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekOfMonthRule() {
            super(ISOChronology.INSTANCE, "WeekOfMonth", WEEKS, MONTHS, 1, 5);
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
            Integer year = calendrical.getValue(yearRule());
            if (year != null) {
                return Year.isoYear(year).isLeap() ? 5 : 4;
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : ((date.getDayOfMonth().getValue() - 1) % 7) + 1);
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer year = merger.getValue(ISOChronology.yearRule());
            Integer moy = merger.getValue(ISOChronology.monthOfYearRule());
            Integer dow = merger.getValue(ISOChronology.dayOfWeekRule());
            if (year != null && moy != null && dow != null) {
                int wom = merger.getValueInt(this);
                LocalDate date = LocalDate.date(year, 1, 1).plusMonths(moy).plusDays((((long) wom) - 1) * 7);
                date = date.with(DateAdjusters.nextOrCurrent(DayOfWeek.dayOfWeek(dow)));
                merger.storeMergedDate(date);
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.yearRule());
                merger.markFieldAsProcessed(ISOChronology.monthOfYearRule());
                merger.markFieldAsProcessed(ISOChronology.dayOfWeekRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class HourOfDayRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new HourOfDayRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private HourOfDayRule() {
            super(ISOChronology.INSTANCE, "HourOfDay", HOURS, DAYS, 0, 23);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (time == null ? null : time.getHourOfDay());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            int hour = merger.getValueInt(this);
            Integer minuteObj = merger.getValue(ISOChronology.minuteOfHourRule());
            Integer secondObj = merger.getValue(ISOChronology.secondOfMinuteRule());
            Integer nanoObj = merger.getValue(ISOChronology.nanoOfSecondRule());
            int minute = 0;
            int second = 0;
            int nano = 0;
            if (minuteObj != null && secondObj != null && nanoObj != null) {
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.minuteOfHourRule());
                merger.markFieldAsProcessed(ISOChronology.secondOfMinuteRule());
                merger.markFieldAsProcessed(ISOChronology.nanoOfSecondRule());
                minute = minuteObj;
                second = secondObj;
                nano = nanoObj;
            } else if (minuteObj != null && secondObj != null && nanoObj == null) {
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.minuteOfHourRule());
                merger.markFieldAsProcessed(ISOChronology.secondOfMinuteRule());
                minute = minuteObj;
                second = secondObj;
            } else if (minuteObj != null && secondObj == null && nanoObj == null) {
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.minuteOfHourRule());
                minute = minuteObj;
            } else if (minuteObj == null && secondObj == null && nanoObj == null) {
                merger.markFieldAsProcessed(this);
            } else {
                return;  // no match
            }
            if (merger.isStrict()) {
                merger.storeMergedTime(LocalTime.time(hour, minute, second, nano));
            } else {
                merger.storeMergedTime(LocalTime.MIDNIGHT.plusWithOverflow(hour, minute, second, nano));
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MinuteOfHourRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new MinuteOfHourRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MinuteOfHourRule() {
            super(ISOChronology.INSTANCE, "MinuteOfHour", MINUTES, HOURS, 0, 59);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (time == null ? null : time.getMinuteOfHour());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class SecondOfMinuteRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new SecondOfMinuteRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private SecondOfMinuteRule() {
            super(ISOChronology.INSTANCE, "SecondOfMinute", SECONDS, MINUTES, 0, 59);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (time == null ? null : time.getSecondOfMinute());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class NanoOfSecondRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new NanoOfSecondRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private NanoOfSecondRule() {
            super(ISOChronology.INSTANCE, "NanoOfSecond", NANOS, SECONDS, 0, 999999999);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (time == null ? null : time.getNanoOfSecond());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MilliOfDayRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new MilliOfDayRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MilliOfDayRule() {
            super(ISOChronology.INSTANCE, "MilliOfDay", MILLIS, DAYS, 0, 86399999);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (time == null ? null : (int) (time.toNanoOfDay() / 1000000));
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            long mod = merger.getValueInt(this);
            if (merger.isStrict()) {
                merger.storeMergedTime(LocalTime.fromNanoOfDay(mod * 1000000L));
            } else {
                merger.storeMergedTime(LocalTime.MIDNIGHT.plusNanosWithOverflow(mod * 1000000L));
            }
            merger.markFieldAsProcessed(this);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MilliOfSecondRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new MilliOfSecondRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MilliOfSecondRule() {
            super(ISOChronology.INSTANCE, "MilliOfSecond", MILLIS, SECONDS, 0, 999);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (time == null ? null : time.getNanoOfSecond() / 1000000);
        }
        @Override
        protected void mergeFields(Calendrical.Merger merger) {
            int mod = merger.getValueInt(this);
            int nod = MathUtils.safeMultiply(mod, 1000000);
            merger.storeMergedField(ISOChronology.nanoOfSecondRule(), nod);
            merger.markFieldAsProcessed(this);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class AmPmOfDayRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new AmPmOfDayRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private AmPmOfDayRule() {
            super(ISOChronology.INSTANCE, "AmPmOfDay", TWELVE_HOURS, DAYS, 0, 1);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (time == null ? null : time.toHourOfDay().getAmPm().getValue());
        }
        @Override
        protected Integer deriveValue(Calendrical.FieldMap fieldMap) {
            Integer hourVal = hourOfDayRule().getValueQuiet(fieldMap);
            if (hourVal == null) {
                return null;
            }
            int hour = hourVal;
            hour = (hour < 0 ? 1073741832 + hour + 1073741832 : hour);  // add multiple of 24 to make positive
            return ((hour % 24) / 2);
        }
        @Override
        protected void mergeFields(Calendrical.Merger merger) {
            Integer hapVal = merger.getValue(ISOChronology.hourOfAmPmRule());
            if (hapVal != null) {
                int amPm = merger.getValueInt(this);
                int hourOfDay = MathUtils.safeAdd(MathUtils.safeMultiply(amPm, 12), hapVal);
                merger.storeMergedField(ISOChronology.hourOfDayRule(), hourOfDay);
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ISOChronology.hourOfAmPmRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class HourOfAmPmRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new HourOfAmPmRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private HourOfAmPmRule() {
            super(ISOChronology.INSTANCE, "HourOfAmPm", HOURS, TWELVE_HOURS, 0, 11);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (time == null ? null : time.toHourOfDay().getHourOfAmPm());
        }
        @Override
        protected Integer deriveValue(Calendrical.FieldMap fieldMap) {
            Integer hourVal = hourOfDayRule().getValueQuiet(fieldMap);
            if (hourVal == null) {
                return null;
            }
            int hour = hourVal;
            hour = (hour < 0 ? 1073741832 + hour + 1073741832 : hour);  // add multiple of 24 to make positive
            return (hour % 12);
        }
    }

}
