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

import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.WeekOfMonth;
import javax.time.calendar.field.WeekOfWeekyear;
import javax.time.calendar.field.Weekyear;
import javax.time.calendar.field.Year;
import javax.time.period.Periods;

/**
 * The ISO8601 calendar system, which follows the rules of the current
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
public final class ISOChronology implements Serializable {

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
    public String getName() {
        return "ISO";
    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the calendrical state from year, month, day.
//     *
//     * @param year  the year
//     * @param month  the month of year
//     * @param day  the day of month
//     * @return the state, never null
//     */
//    public CalendricalState stateFromYearMonthDay(int year, int month, int day) {
//        year--;
//        month--;
//        day--;
//        long epochMonths = year;
//        epochMonths *= 13 + month;
//        long epochSeconds = ((long) day) * 24 * 60 * 60;
//        return new State(epochMonths, epochSeconds);
//    }

//    /**
//     * Validates the date from a year, month and day.
//     *
//     * @param year  the year
//     * @param monthOfYear  the month of year
//     * @param dayOfMonth  the day of month
//     * @return true if valid
//     */
//    public boolean validateDate(int year, int monthOfYear, int dayOfMonth) {
//        if (year == Integer.MIN_VALUE) {
//            return false;
//        }
//        if (monthOfYear < 1 || monthOfYear > 12) {
//            return false;
//        }
//        if (dayOfMonth < 1 || dayOfMonth > 28) {
//            int length = MonthOfYear.monthOfYear(monthOfYear).lengthInDays(Year.isoYear(year));
//            if (dayOfMonth < 1 || dayOfMonth > length) {
//                return false;
//            }
//        }
//        return true;
//    }

//    /**
//     * Validates the date from a year, month and day.
//     *
//     * @param year  the year, not null
//     * @param monthOfYear  the month of year, not null
//     * @param dayOfMonth  the day of month, not null
//     * @throws IllegalCalendarFieldValueException if any field is invalid
//     */
//    public void checkValidDate(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
//        if (year == null) {
//            throw new NullPointerException("Year must not be null");
//        }
//        if (monthOfYear == null) {
//            throw new NullPointerException("MonthOfYear must not be null");
//        }
//        if (dayOfMonth == null) {
//            throw new NullPointerException("DayOfMonth must not be null");
//        }
//        if (dayOfMonth.isValid(year, monthOfYear) == false) {
//            throw new IllegalCalendarFieldValueException("DayOfMonth", dayOfMonth.getValue(), 1, monthOfYear.lengthInDays(year));
//        }
//    }

//    /**
//     * Validates the date from a year, month and day.
//     *
//     * @param year  the year
//     * @param monthOfYear  the month of year
//     * @param dayOfMonth  the day of month
//     * @throws IllegalCalendarFieldValueException if any field is invalid
//     */
//    public void checkValidDate(int year, int monthOfYear, int dayOfMonth) {
//        if (year == Integer.MIN_VALUE) {
//            throw new IllegalCalendarFieldValueException("Year", year, Integer.MIN_VALUE + 1, Integer.MAX_VALUE);
//        }
//        if (monthOfYear < 1 || monthOfYear > 12) {
//            throw new IllegalCalendarFieldValueException("MonthOfYear", monthOfYear, 1, 12);
//        }
//        if (dayOfMonth < 1 || dayOfMonth > 28) {
//            int length = MonthOfYear.monthOfYear(monthOfYear).lengthInDays(Year.isoYear(year));
//            if (dayOfMonth < 1 || dayOfMonth > length) {
//                throw new IllegalCalendarFieldValueException("DayOfMonth", dayOfMonth, 1, length);
//            }
//        }
//    }

//    /**
//     * Gets the day of year from a year, month and day.
//     *
//     * @param year  the year, must be valid, must be valid
//     * @param monthOfYear  the month of year, from 1 to 12, must be valid
//     * @return the length of the month in days
//     */
//    int getMonthLength(int year, int monthOfYear) {
//        if (isLeapYear(year)) {
//            return LEAP_MONTH_LENGTHS[monthOfYear - 1];
//        } else {
//            return STANDARD_MONTH_LENGTHS[monthOfYear - 1];
//        }
//    }

//    /**
//     * Gets the day of year from a year, month and day.
//     *
//     * @param year  the year, must be valid, must be valid
//     * @param monthOfYear  the month of year, from 1 to 12, must be valid
//     * @param dayOfMonth  the day of month, from 1 to 31, must be valid
//     * @return the day of year, from 1 to 366
//     */
//    int getDayOfYear(Year year, MonthOfYear monthOfYear, DayOfMonth dayOfMonth) {
//        if (year.isLeap()) {
//            return LEAP_MONTH_START[monthOfYear.ordinal()] + dayOfMonth.getValue();
//        } else {
//            return STANDARD_MONTH_START[monthOfYear.ordinal()] + dayOfMonth.getValue();
//        }
//    }

//    /**
//     * Gets the day of week from a year, month and day.
//     *
//     * @param year  the year, must be valid, must be valid
//     * @param monthOfYear  the month of year, from 1 to 12, must be valid
//     * @param dayOfMonth  the day of month, from 1 to 31, must be valid
//     * @return the day of week, from 1 to 7
//     */
//    int getDayOfWeek(int year, int monthOfYear, int dayOfMonth) {
//        return 1;
//    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the calendrical state from year, month, day.
//     *
//     * @param year  the year
//     * @param month  the month of year
//     * @param day  the day of month
//     * @return the state, never null
//     */
//    public long convert(long amount, PeriodUnit fromUnit, PeriodUnit toUnit) {
//        return 0;
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the year field in the ISO chronology.
     *
     * @return the rule for the year field, never null
     */
    public DateTimeFieldRule year() {
        return YearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the ISO chronology.
     *
     * @return the rule for the month of year field, never null
     */
    public DateTimeFieldRule monthOfYear() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the ISO chronology.
     *
     * @return the rule for the day of month field, never null
     */
    public DateTimeFieldRule dayOfMonth() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the ISO chronology.
     *
     * @return the rule for the day of year field, never null
     */
    public DateTimeFieldRule dayOfYear() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the week-based-year field in the ISO chronology.
     *
     * @return the rule for the week-based-year field, never null
     */
    public DateTimeFieldRule weekyear() {
        return WeekyearRule.INSTANCE;
    }

    /**
     * Gets the rule for the week of week-based-year field in the ISO chronology.
     *
     * @return the rule for the week of week-based-year field, never null
     */
    public DateTimeFieldRule weekOfWeekyear() {
        return WeekOfWeekyearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field.
     *
     * @return the rule for the day of week field, never null
     */
    public DateTimeFieldRule dayOfWeek() {
        return DayOfWeekRule.INSTANCE;
    }

    /**
     * Gets the rule for the week of year field in the ISO chronology.
     *
     * @return the rule for the week of year field, never null
     */
    public DateTimeFieldRule weekOfYear() {
        return WeekOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the quarter of year field in the ISO chronology.
     *
     * @return the rule for the quarter of year field, never null
     */
    public DateTimeFieldRule quarterOfYear() {
        return QuarterOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of quarter field in the ISO chronology.
     *
     * @return the rule for the month of quarter field, never null
     */
    public DateTimeFieldRule monthOfQuarter() {
        return MonthOfQuarterRule.INSTANCE;
    }

    /**
     * Gets the rule for the week of month field in the ISO chronology.
     *
     * @return the rule for the week of month field, never null
     */
    public DateTimeFieldRule weekOfMonth() {
        return WeekOfMonthRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the rule for the hour of day field.
//     *
//     * @return the rule for the hour of day field, never null
//     */
//    public TimeFieldRule hourOfDay() {
//        return null;
//    }
//
//    /**
//     * Gets the rule for the minute of hour field.
//     *
//     * @return the rule for the minute of hour field, never null
//     */
//    public TimeFieldRule minuteOfHour() {
//        return null;
//    }
//
//    /**
//     * Gets the rule for the second of minute field.
//     *
//     * @return the rule for the second of minute field, never null
//     */
//    public TimeFieldRule secondOfMinute() {
//        return null;
//    }
//
//    /**
//     * Gets the rule for the nano of second field.
//     *
//     * @return the rule for the nano of second field, never null
//     */
//    public TimeFieldRule nanoOfSecond() {
//        return null;
//    }

    //-----------------------------------------------------------------------
    /**
     * A debugging description of this class.
     *
     * @return a string form for debugging, never null
     */
    @Override
    public String toString() {
        return "ISOChronology";
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
            super("Year", Periods.YEARS, Periods.FOREVER, Year.MIN_YEAR, Year.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? dateTime.getDate().getYear().getValue() : null;
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
            super("MonthOfYear", Periods.MONTHS, Periods.YEARS, 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? dateTime.getDate().getMonthOfYear().getValue() : null;
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
            super("DayOfMonth", Periods.DAYS, Periods.MONTHS, 1, 31);
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
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? dateTime.getDate().getDayOfMonth().getValue() : null;
        }
        /** {@inheritDoc} */
        @Override
        protected FlexiDateTime mergeFields(FlexiDateTime dateTime) {
            int domValue = dateTime.getValue(this);
            if (dateTime.getDate() == null) {
                Map<DateTimeFieldRule, Integer> map = dateTime.getFieldValueMap();
                map.remove(this);
                Integer year = map.remove(Year.rule());
                Integer month = map.remove(MonthOfYear.rule());
                if (year != null && month != null) {
                    LocalDate date = LocalDate.date(year, month, domValue);
                    return dateTime.withFieldValueMap(map).withDate(date);
                }
            }
            return dateTime;
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
            super("DayOfYear", Periods.DAYS, Periods.YEARS, 1, 366);
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
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? dateTime.getDate().getDayOfYear().getValue() : null;
        }
        /** {@inheritDoc} */
        @Override
        protected FlexiDateTime mergeFields(FlexiDateTime dateTime) {
            int doyValue = dateTime.getValue(this);
            if (dateTime.getDate() == null) {
                Map<DateTimeFieldRule, Integer> map = dateTime.getFieldValueMap();
                map.remove(this);
                Integer year = map.remove(Year.rule());
                if (year != null) {
                    DayOfYear doy = DayOfYear.dayOfYear(doyValue);
                    LocalDate date = doy.createDate(Year.isoYear(year));
                    return dateTime.withFieldValueMap(map).withDate(date);
                }
            }
            return dateTime;
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
            super("Weekyear", Periods.YEARS, Periods.FOREVER, Weekyear.MIN_YEAR, Weekyear.MAX_YEAR);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? Weekyear.weekyear(dateTime.getDate()).getValue() : null;
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
            super("WeekOfWeekyear", Periods.WEEKS, Periods.YEARS, 1, 53);
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
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? WeekOfWeekyear.weekOfWeekyear(dateTime.getDate()).getValue() : null;
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
            super("DayOfWeek", Periods.DAYS, Periods.WEEKS, 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? dateTime.getDate().getDayOfWeek().getValue() : null;
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
            super("WeekOfYear", Periods.WEEKS, Periods.YEARS, 1, 53);
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
        protected Integer extractValue(FlexiDateTime dateTime) {
            LocalDate date = dateTime.getDate();
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
            super("QuarterOfYear", Periods.WEEKS, Periods.YEARS, 1, 4);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? dateTime.getDate().getMonthOfYear().getQuarterOfYear().getValue() : null;
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
            super("MonthOfQuarter", Periods.WEEKS, Periods.YEARS, 1, 4);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        /** {@inheritDoc} */
        @Override
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? dateTime.getDate().getMonthOfYear().getMonthOfQuarter() : null;
        }
        /** {@inheritDoc} */
        @Override
        protected FlexiDateTime mergeFields(FlexiDateTime dateTime) {
            int moq = dateTime.getValue(this);
            if (dateTime.getDate() == null) {
                Map<DateTimeFieldRule, Integer> map = dateTime.getFieldValueMap();
                map.remove(this);
                Integer qoy = map.remove(ISOChronology.INSTANCE.quarterOfYear());
                if (qoy != null) {
                    checkValue(qoy, dateTime.getDate());
                    int moy = (qoy - 1) * 3 + moq;
                    Integer existingMoy = map.get(MonthOfYear.rule());
                    if (existingMoy != null && existingMoy != moy) {
                        throw new IllegalCalendarFieldValueException(
                                "Merge of Month of Quarter and Quarter of Year created value " +
                                moy + " that does not match the existing Month of Year value " + existingMoy);
                    }
                    map.put(MonthOfYear.rule(), moy);
                    return dateTime.withFieldValueMap(map);
                }
            }
            return dateTime;
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
            super("WeekOfMonth", Periods.WEEKS, Periods.YEARS, 1, 5);
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
        protected Integer extractValue(FlexiDateTime dateTime) {
            return dateTime.getDate() != null ? WeekOfMonth.weekOfMonth(dateTime.getDate()).getValue() : null;
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
//         * @param dateTime  the date time, not null
//         * @return the value of the field
//         * @throws UnsupportedCalendarFieldException if the value cannot be extracted
//         */
//        public int getValue(FlexiDateTime dateTime) {
//            switch (this) {
//                case YEAR:
//                    return dateTime.getDate().getYear().getValue();
//                default:
//                    throw new UnsupportedCalendarFieldException(this, "FlexiDateTime");
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
