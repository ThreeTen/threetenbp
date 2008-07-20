/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.Map;

import javax.time.MathUtils;
import javax.time.calendar.LocalTime.Overflow;
import javax.time.calendar.field.AmPmOfDay;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.WeekOfMonth;
import javax.time.calendar.field.WeekOfWeekyear;
import javax.time.calendar.field.Weekyear;
import javax.time.calendar.field.Year;
import javax.time.period.Periods;

/**
 * The ISO-8601 calendar system, which follows the rules of the current
 * <i>de facto</i> world calendar.
 * <p>
 * ISOChronology follows the rules of the Gregorian calendar for all time.
 * Thus, dates is the past, and particularly before 1583, may not correspond
 * to historical documents.
 * <p>
 * ISOChronology is thread-safe and immutable.
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
     * Gets the rule for the week-based-year field in the ISO chronology.
     * <p>
     * This field is the year that results from calculating weeks with the
     * ISO-8601 algorithm. See {@link #weekOfWeekyear()} for details.
     * <p>
     * The week year will either be 52 or 53 weeks long, depending on the
     * result of the algorithm for a particular date.
     *
     * @return the rule for the week-based-year field, never null
     */
    public DateTimeFieldRule weekyear() {
        return WeekyearRule.INSTANCE;
    }

    /**
     * Gets the rule for the week of week-based-year field in the ISO chronology.
     * <p>
     * This field counts weeks using the ISO-8601 algorithm. The first week of
     * the year is the week which has at least 4 days in the year using a Monday
     * to Sunday week definition. Thus it is possible for the first week to start
     * on any day from the 29th December in the previous year to the 4th January
     * in the new year. The year which is aligned with this field is known as
     * the {@link #weekyear() weeekyear}.
     *
     * @return the rule for the week of week-based-year field, never null
     */
    public DateTimeFieldRule weekOfWeekyear() {
        return WeekOfWeekyearRule.INSTANCE;
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

    /**
     * Gets the rule for the week of year field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days starting from the first
     * of January. The 1st to the 7th of January is always week 1 while the
     * 8th to the 14th is always week 2.
     *
     * @return the rule for the week of year field, never null
     */
    public DateTimeFieldRule weekOfYear() {
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
    public DateTimeFieldRule quarterOfYear() {
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
    public DateTimeFieldRule monthOfQuarter() {
        return MonthOfQuarterRule.INSTANCE;
    }

    /**
     * Gets the rule for the week of month field in the ISO chronology.
     * <p>
     * This field counts weeks in groups of seven days starting from the first
     * day of the month. The 1st to the 7th of a month is always week 1 while the
     * 8th to the 14th is always week 2 and so on.
     *
     * @return the rule for the week of month field, never null
     */
    public DateTimeFieldRule weekOfMonth() {
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

    /**
     * Gets the rule for the milli of day field.
     * <p>
     * This field counts nanoseconds sequentially from the start of the second.
     * The values run from 0 to 86399999.
     *
     * @return the rule for the nano of second field, never null
     */
    public DateTimeFieldRule milliOfDay() {
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
    public DateTimeFieldRule milliOfSecond() {
        return MilliOfSecondRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the am/pm of day field.
     * <p>
     * This field defines the half-day am/pm value. The hour of day from 0 to 11 is
     * defined as AM, while the hours from 12 to 23 are defined as PM.
     * AM is defined with the value 0, while PM is defined with the value 1.
     * <p>
     * The enum {@link AmPmOfDay} should be used wherever possible in
     * applications when referring to the day of the week to avoid
     * hard-coding the values.
     *
     * @return the rule for the am/pm of day field, never null
     */
    public DateTimeFieldRule amPmOfDay() {
        return AmPmOfDayRule.INSTANCE;
    }

    /**
     * Gets the rule for the hour of am/pm field.
     * <p>
     * This field counts hours sequentially from the start of the half-day am/pm.
     * The values run from 0 to 11.
     *
     * @return the rule for the hour of am/pm field, never null
     */
    public DateTimeFieldRule hourOfAmPm() {
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
            super(ISOChronology.INSTANCE, "Year", Periods.YEARS, Periods.FOREVER, Year.MIN_YEAR, Year.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? calendrical.getDate().getYear().getValue() : null;
        }
        @Override
        protected LocalDate mergeToDate(DateTimeFields fieldValues) {
            int year = fieldValues.getValueQuiet(this);
            Integer month = fieldValues.getValueQuiet(ISOChronology.INSTANCE.monthOfYear());
            Integer dom = fieldValues.getValueQuiet(ISOChronology.INSTANCE.dayOfMonth());
            if (month != null && dom != null) {
                if (ISOChronology.INSTANCE.monthOfYear().isValidValue(month)) {
                    if (dom >= 1 && dom <= 28) {  // range is valid for all months
                        return LocalDate.date(year, month, dom);
                    }
                    return LocalDate.date(year, month, 1).plusDays(dom - 1);
                }
                // handle months==MIN_VALUE
                return LocalDate.date(year, 1, 1).plusMonths(month).plusMonths(-1).plusDays(((long) dom) - 1);
            }
            return null;
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
            super(ISOChronology.INSTANCE, "MonthOfYear", Periods.MONTHS, Periods.YEARS, 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? calendrical.getDate().getMonthOfYear().getValue() : null;
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
            super(ISOChronology.INSTANCE, "DayOfMonth", Periods.DAYS, Periods.MONTHS, 1, 31);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 28;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? calendrical.getDate().getDayOfMonth().getValue() : null;
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
            super(ISOChronology.INSTANCE, "DayOfYear", Periods.DAYS, Periods.YEARS, 1, 366);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 365;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? calendrical.getDate().getDayOfYear().getValue() : null;
        }
        @Override
        protected LocalDate mergeToDate(DateTimeFields fieldValues) {
            int doy = fieldValues.getValueQuiet(this);
            Integer year = fieldValues.getValueQuiet(ISOChronology.INSTANCE.year());
            if (year != null) {
                if (doy >= 1 && doy <= 365) {  // range is valid for all years
                    return DayOfYear.dayOfYear(doy).createDate(Year.isoYear(year));
                }
                return LocalDate.date(year, 1, 1).plusDays(((long) doy) - 1);
            }
            return null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class WeekyearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new WeekyearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekyearRule() {
            super(ISOChronology.INSTANCE, "Weekyear", Periods.YEARS, Periods.FOREVER, Weekyear.MIN_YEAR, Weekyear.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? Weekyear.weekyear(calendrical.getDate()).getValue() : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class WeekOfWeekyearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new WeekOfWeekyearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private WeekOfWeekyearRule() {
            super(ISOChronology.INSTANCE, "WeekOfWeekyear", Periods.WEEKS, Periods.YEARS, 1, 53);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 52;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? WeekOfWeekyear.weekOfWeekyear(calendrical.getDate()).getValue() : null;
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
            super(ISOChronology.INSTANCE, "DayOfWeek", Periods.DAYS, Periods.WEEKS, 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? calendrical.getDate().getDayOfWeek().getValue() : null;
        }
        @Override
        protected LocalDate mergeToDate(DateTimeFields fieldValues) {
            int dow = fieldValues.getValueQuiet(this);
            Integer wyear = fieldValues.getValueQuiet(ISOChronology.INSTANCE.weekyear());
            Integer woy = fieldValues.getValueQuiet(ISOChronology.INSTANCE.weekOfWeekyear());
            if (wyear != null && woy != null) {
                // TODO
                return LocalDate.date(wyear, 1, dow);
            }
            return null;
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
            super(ISOChronology.INSTANCE, "WeekOfYear", Periods.WEEKS, Periods.YEARS, 1, 53);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 52;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            LocalDate date = calendrical.getDate();
            if (date != null) {
                return ((date.getDayOfYear().getValue() - 1) % 7) + 1;
            }
            return null;
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
            super(ISOChronology.INSTANCE, "QuarterOfYear", Periods.WEEKS, Periods.YEARS, 1, 4);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? calendrical.getDate().getMonthOfYear().getQuarterOfYear().getValue() : null;
        }
        /** {@inheritDoc} */
        @Override
        protected void mergeFields(Map<DateTimeFieldRule, Integer> fieldValueMap) {
            if (fieldValueMap.containsKey(ISOChronology.INSTANCE.monthOfQuarter())) {
                int qoy = fieldValueMap.remove(this);
                int moq = fieldValueMap.remove(ISOChronology.INSTANCE.monthOfQuarter());
                if (fieldValueMap.containsKey(ISOChronology.INSTANCE.monthOfYear()) == false) {
                    qoy = MathUtils.safeDecrement(qoy);
                    moq = MathUtils.safeDecrement(moq);
                    int moy = MathUtils.safeAdd(MathUtils.safeMultiply(qoy, 3), moq);
                    moy = MathUtils.safeIncrement(moy);
                    fieldValueMap.put(ISOChronology.INSTANCE.monthOfYear(), moy);
                }
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
            super(ISOChronology.INSTANCE, "MonthOfQuarter", Periods.WEEKS, Periods.YEARS, 1, 4);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? calendrical.getDate().getMonthOfYear().getMonthOfQuarter() : null;
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
            super(ISOChronology.INSTANCE, "WeekOfMonth", Periods.WEEKS, Periods.YEARS, 1, 5);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 4;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getDate() != null ? WeekOfMonth.weekOfMonth(calendrical.getDate()).getValue() : null;
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
            super(ISOChronology.INSTANCE, "HourOfDay", Periods.HOURS, Periods.DAYS, 0, 23);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getTime() != null ? calendrical.getTime().getHourOfDay().getValue() : null;
        }
        @Override
        protected LocalTime.Overflow mergeToTime(DateTimeFields fieldValues) {
            int hour = fieldValues.getValueQuiet(this);
            Integer minuteObj = fieldValues.getValueQuiet(ISOChronology.INSTANCE.minuteOfHour());
            Integer secondObj = fieldValues.getValueQuiet(ISOChronology.INSTANCE.secondOfMinute());
            Integer nanoObj = fieldValues.getValueQuiet(ISOChronology.INSTANCE.nanoOfSecond());
            int minute = minuteObj == null ? 0 : minuteObj;
            int second = secondObj == null ? 0 : secondObj;
            int nano = nanoObj == null ? 0 : nanoObj;
            return LocalTime.MIDNIGHT.plusWithOverflow(hour, minute, second, nano);
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
            super(ISOChronology.INSTANCE, "MinuteOfHour", Periods.MINUTES, Periods.HOURS, 0, 59);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getTime() != null ? calendrical.getTime().getMinuteOfHour().getValue() : null;
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
            super(ISOChronology.INSTANCE, "SecondOfMinute", Periods.SECONDS, Periods.MINUTES, 0, 59);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getTime() != null ? calendrical.getTime().getSecondOfMinute().getValue() : null;
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
            super(ISOChronology.INSTANCE, "NanoOfSecond", Periods.NANOS, Periods.SECONDS, 0, 999999999);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getTime() != null ? calendrical.getTime().getNanoOfSecond().getValue() : null;
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
            super(ISOChronology.INSTANCE, "MilliOfDay", Periods.MILLIS, Periods.DAYS, 0, 86399999);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            if (calendrical.getTime() == null) {
                return null;
            }
            return (int) (calendrical.getTime().toNanoOfDay() / 1000000);
        }
        /** {@inheritDoc} */
        @Override
        protected Overflow mergeToTime(DateTimeFields fieldValues) {
            long mod = fieldValues.getValueQuiet(this);
            return LocalTime.MIDNIGHT.plusNanosWithOverflow(mod * 1000000L);
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
            super(ISOChronology.INSTANCE, "MilliOfSecond", Periods.MILLIS, Periods.SECONDS, 0, 999);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            if (calendrical.getTime() == null) {
                return null;
            }
            return calendrical.getTime().getNanoOfSecond().getValue() / 1000000;
        }
        /** {@inheritDoc} */
        @Override
        protected void mergeFields(Map<DateTimeFieldRule, Integer> fieldValueMap) {
            int mod = fieldValueMap.remove(this);
            if (fieldValueMap.containsKey(ISOChronology.INSTANCE.nanoOfSecond()) == false) {
                int nod = MathUtils.safeMultiply(mod, 1000000);
                fieldValueMap.put(ISOChronology.INSTANCE.nanoOfSecond(), nod);
            }
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
            super(ISOChronology.INSTANCE, "AmPmOfDay", Periods.HALF_DAYS, Periods.DAYS, 0, 1);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getTime() != null ? calendrical.getTime().getHourOfDay().getAmPm().getValue() : null;
        }
        /** {@inheritDoc} */
        @Override
        protected void mergeFields(Map<DateTimeFieldRule, Integer> fieldValueMap) {
            if (fieldValueMap.containsKey(ISOChronology.INSTANCE.hourOfAmPm())) {
                int amPm = fieldValueMap.remove(this);
                int hourOfAmPm = fieldValueMap.remove(ISOChronology.INSTANCE.hourOfAmPm());
                if (fieldValueMap.containsKey(ISOChronology.INSTANCE.hourOfDay()) == false) {
                    int hourOfDay = MathUtils.safeAdd(MathUtils.safeMultiply(amPm, 12), hourOfAmPm);
                    fieldValueMap.put(ISOChronology.INSTANCE.hourOfDay(), hourOfDay);
                }
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
            super(ISOChronology.INSTANCE, "HourOfAmPm", Periods.HOURS, Periods.HALF_DAYS, 0, 11);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(Calendrical calendrical) {
            return calendrical.getTime() != null ? calendrical.getTime().getHourOfDay().getHourOfAmPm() : null;
        }
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static enum Rule implements Serializable {
//        /** Year instance. */
//        YEAR("Year", Periods.YEARS, Periods.FOREVER, Year.MIN_YEAR, Year.MAX_YEAR),
//        /** Year instance. */
//        MONTH_OF_YEAR("MonthOfYear", Periods.MONTHS, Periods.YEARS, 1, 12),
//        /** Year instance. */
//        DAY_OF_MONTH("DayOfMonth", Periods.DAYS, Periods.MONTHS, 1, 31);
//
//        /** The name of the rule, not null. */
//        private final String name;
//        /** The period unit, not null. */
//        private final PeriodUnit periodUnit;
//        /** The period range, not null. */
//        private final PeriodUnit periodRange;
//        /** The minimum value for the field. */
//        private final int minimumValue;
//        /** The maximum value for the field. */
//        private final int maximumValue;
//
//        /**
//         * Constructor.
//         *
//         * @param name  the name of the type, not null
//         * @param periodUnit  the period unit, not null
//         * @param periodRange  the period range, not null
//         * @param minimumValue  the minimum value
//         * @param maximumValue  the minimum value
//         */
//        private Rule(
//                String name,
//                PeriodUnit periodUnit,
//                PeriodUnit periodRange,
//                int minimumValue,
//                int maximumValue) {
//            this.name = name;
//            this.periodUnit = periodUnit;
//            this.periodRange = periodRange;
//            this.minimumValue = minimumValue;
//            this.maximumValue = maximumValue;
//        }
//
//        //-----------------------------------------------------------------------
//        /**
//         * Gets the name of the time field type.
//         * <p>
//         * Subclasses should use the form 'UnitOfRange' whenever possible.
//         *
//         * @return the name of the time field type, never null
//         */
//        public String getName() {
//            return name;
//        }
//
//        /**
//         * Gets the period unit, which the element which alters within the range.
//         * <p>
//         * In the phrase 'hour of day', the unit is the hour.
//         *
//         * @return the rule for the unit period, never null
//         */
//        public PeriodUnit getPeriodUnit() {
//            return periodUnit;
//        }
//
//        /**
//         * Gets the period range, which the field is bound by.
//         * <p>
//         * In the phrase 'hour of day', the range is the day.
//         *
//         * @return the rule for the range period, never null
//         */
//        public PeriodUnit getPeriodRange() {
//            return periodRange;
//        }
//
//        //-----------------------------------------------------------------------
//        /**
//         * Checks if the this field is supported using calendrical data that is
//         * completely specified by the unit and range.
//         * <p>
//         * For example, a date object has a unit of days and a range of forever.
//         * If this field is for hour of day, then that cannot be supported by the
//         * unit and range from a date object.
//         *
//         * @param unit  the unit to check, not null
//         * @param range  the range to check, not null
//         * @return true if the field is supported
//         */
//        public boolean isSupported(PeriodUnit unit, PeriodUnit range) {
//            return (periodUnit.compareTo(unit) >= 0) &&
//                   (periodRange.compareTo(range) < 0);
//        }
//
//        /**
//         * Gets the value of this field.
//         *
//         * @param calendrical  the date time, not null
//         * @return the value of the field
//         * @throws UnsupportedCalendarFieldException if the value cannot be extracted
//         */
//        public int getValue(Calendrical calendrical) {
//            switch (this) {
//                case YEAR:
//                    return calendrical.getDate().getYear().getValue();
//                default:
//                    throw new UnsupportedCalendarFieldException(this, "Calendrical");
//            }
//        }
//
//        //-----------------------------------------------------------------------
//        /**
//         * Checks if the value is invalid and throws an exception if it is.
//         * This method has no context, so only the outer minimum and maximum
//         * values are used.
//         *
//         * @param value  the value to check
//         * @throws IllegalCalendarFieldValueException if the value is invalid
//         */
//        public void checkValue(int value) {
//            if (value < getMinimumValue() || value > getMaximumValue()) {
//                throw new IllegalCalendarFieldValueException(getName(), value, getMinimumValue(), getMaximumValue());
//            }
//        }
//
//        //-----------------------------------------------------------------------
//        /**
//         * Is the set of values, from the minimum value to the maximum, a fixed
//         * set, or does it vary according to other fields.
//         *
//         * @return true if the set of values is fixed
//         */
//        public boolean isFixedValueSet() {
//            return getMaximumValue() == getSmallestMaximumValue() &&
//                    getMinimumValue() == getLargestMinimumValue();
//        }
//
//        //-----------------------------------------------------------------------
//        /**
//         * Gets the minimum value that the field can take.
//         *
//         * @return the minimum value for this field
//         */
//        public int getMinimumValue() {
//            return minimumValue;
//        }
//
//        /**
//         * Gets the largest possible minimum value that the field can take.
//         *
//         * @return the largest possible minimum value for this field
//         */
//        public int getLargestMinimumValue() {
//            return getMinimumValue();
//        }
//
////        /**
////         * Gets the minimum value that the field can take using the specified
////         * calendrical information to refine the accuracy of the response.
////         *
////         * @param calendricalContext  context datetime, null returns getMinimumValue()
////         * @return the minimum value of the field given the context
////         */
////        public int getMinimumValue(Calendrical calendricalContext) {
////            return getMinimumValue();
////        }
//    //
////        /**
////         * Gets the largest possible minimum value that the field can take using
////         * the specified calendrical information to refine the accuracy of the response.
////         *
////         * @param calendricalContext  context datetime, null returns getLargestMinimumValue()
////         * @return the largest possible minimum value of the field given the context
////         */
////        public int getLargestMinimumValue(Calendrical calendricalContext) {
////            if (calendricalContext == null) {
////                return getLargestMinimumValue();
////            }
////            return getMinimumValue(calendricalContext);
////        }
//
//        //-----------------------------------------------------------------------
//        /**
//         * Gets the maximum value that the field can take.
//         *
//         * @return the maximum value for this field
//         */
//        public int getMaximumValue() {
//            return maximumValue;
//        }
//
//        /**
//         * Gets the smallest possible maximum value that the field can take.
//         *
//         * @return the smallest possible maximum value for this field
//         */
//        public int getSmallestMaximumValue() {
//            return getMaximumValue();
//        }
//
////        /**
////         * Gets the maximum value that the field can take using the specified
////         * calendrical information to refine the accuracy of the response.
////         *
////         * @param calendricalContext  context datetime, null returns getMaximumValue()
////         * @return the maximum value of the field given the context
////         */
////        public int getMaximumValue(Calendrical calendricalContext) {
////            return getMaximumValue();
////        }
//    //
////        /**
////         * Gets the smallest possible maximum value that the field can take using
////         * the specified calendrical information to refine the accuracy of the response.
////         *
////         * @param calendricalContext  context datetime, null returns getSmallestMaximumValue()
////         * @return the smallest possible maximum value of the field given the context
////         */
////        public int getSmallestMaximumValue(Calendrical calendricalContext) {
////            if (calendricalContext == null) {
////                return getSmallestMaximumValue();
////            }
////            return getMaximumValue(calendricalContext);
////        }
//    //
////        //-----------------------------------------------------------------------
////        /**
////         * Checks whether a given calendrical is supported or not.
////         *
////         * @param calState  the calendar state to check, not null
////         * @throws UnsupportedCalendarFieldException if the field is unsupported
////         */
////        protected void checkSupported(CalendricalState calState) {
////            if (calState.getPeriodUnit().compareTo(getPeriodUnit()) > 0 ||
////                    calState.getPeriodRange().compareTo(getPeriodRange()) < 0) {
////                throw new UnsupportedCalendarFieldException("Calendar field " + getName() + " cannot be queried");
////            }
////        }
//
//        //-----------------------------------------------------------------------
//        /**
//         * Compares this TimeFieldRule to another based on the period unit
//         * followed by the period range.
//         * <p>
//         * The period unit is compared first, so MinuteOfHour will be less than
//         * HourOfDay, which will be less than DayOfWeek. When the period unit is
//         * the same, the period range is compared, so DayOfWeek is less than
//         * DayOfMonth, which is less than DayOfYear.
//         *
//         * @param other  the other type to compare to, not null
//         * @return the comparator result, negative if less, postive if greater, zero if equal
//         * @throws NullPointerException if other is null
//         */
//        public int compareTo(DateTimeFieldRule other) {
//            int cmp = this.getPeriodUnit().compareTo(other.getPeriodUnit());
//            if (cmp != 0) {
//                return cmp;
//            }
//            return this.getPeriodRange().compareTo(other.getPeriodRange());
//        }
//
//        //-----------------------------------------------------------------------
//        /**
//         * Returns a string representation of the rule.
//         *
//         * @return a description of the rule
//         */
//        @Override
//        public String toString() {
//            return getName();
//        }
//    }

}
