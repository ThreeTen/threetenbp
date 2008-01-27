/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.field.Year;
import javax.time.i18n.CopticChronology.State;
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
    /** Number of months in one year. */
    private static final int MONTHS_PER_YEAR = 12;
    /** Number of seconds in one day. */
    private static final int SECONDS_PER_DAY = 60 * 60 * 24;
    /** Number of seconds in one hour. */
    private static final int SECONDS_PER_HOUR = 60 * 60;
    /** Number of seconds in one minute. */
    private static final int SECONDS_PER_MINUTE = 60;
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

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * ISO chronology leap years are every 4 years. A special rule applies
     * for years divisible by 100, which are only leap if also divisible by 400.
     *
     * @param year  the year to check
     * @return true if the year is a leap year
     */
    private boolean isLeapYear(int year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the name of the chronology.
//     *
//     * @return the name of the chronology, never null
//     */
//    public String getName() {
//        return "ISO";
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
     * Gets the rule for the year field.
     *
     * @return the rule for the year field, never null
     */
    public TimeFieldRule yearRule() {
        return YEAR_RULE;
    }

    /**
     * Gets the rule for the month of year field.
     *
     * @return the rule for the month of year field, never null
     */
    public TimeFieldRule monthOfYearRule() {
        return MONTH_OF_YEAR_RULE;
    }

    /**
     * Gets the rule for the day of year field.
     *
     * @return the rule for the day of year field, never null
     */
    public TimeFieldRule dayOfYearRule() {
        return DAY_OF_YEAR_RULE;
    }

    /**
     * Gets the rule for the day of month field.
     *
     * @return the rule for the day of month field, never null
     */
    public TimeFieldRule dayOfMonthRule() {
        return DAY_OF_MONTH_RULE;
    }

    /**
     * Gets the rule for the day of week field.
     *
     * @return the rule for the day of week field, never null
     */
    public TimeFieldRule dayOfWeekRule() {
        return null;
    }

    /**
     * Gets the rule for the hour of day field.
     *
     * @return the rule for the hour of day field, never null
     */
    public TimeFieldRule hourOfDayRule() {
        return null;
    }

    /**
     * Gets the rule for the minute of hour field.
     *
     * @return the rule for the minute of hour field, never null
     */
    public TimeFieldRule minuteOfHourRule() {
        return null;
    }

    /**
     * Gets the rule for the second of minute field.
     *
     * @return the rule for the second of minute field, never null
     */
    public TimeFieldRule secondOfMinuteRule() {
        return null;
    }

    /**
     * Gets the rule for the nano of second field.
     *
     * @return the rule for the nano of second field, never null
     */
    public TimeFieldRule nanoOfSecondRule() {
        return null;
    }

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

//    //-----------------------------------------------------------------------
//    /**
//     * State implementation for the ISO8601 calendar system.
//     */
//    public static class State extends CalendricalState {
//
//        /** The months since the epoch of 0001-01-01 (ISO8601). */
//        private final long months;
//        /** The second of month. */
//        private final long seconds;
//
//        /**
//         * Constructor.
//         *
//         * @param months  the months since the epoch of 0001-01-01 (ISO8601)
//         * @param seconds  the second of month
//         */
//        State(long months, long seconds) {
//            super();
//            this.months = months;
//            this.seconds = seconds;
//        }
//
//        /**
//         * Constructor.
//         *
//         * @param year  the year, must be valid
//         * @param monthOfYear  the month of year, from 1 to 12, must be valid
//         * @param dayOfMonth  the day of month, from 1 to 31, must be valid
//         */
//        State(int year, int monthOfYear, int dayOfMonth) {
//            super();
//            this.months = ((year - 1) * MONTHS_PER_YEAR) + (monthOfYear - 1);
//            this.seconds = (dayOfMonth - 1) * SECONDS_PER_DAY;
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public PeriodUnit getPeriodUnit() {
//            return null;
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public PeriodUnit getPeriodRange() {
//            return null;
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public boolean isSupported(TimeFieldRule fieldRule) {
//            return false;
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public int get(TimeFieldRule fieldRule) {
//            if (false) {
//                throw new UnsupportedCalendarFieldException();
//            }
//            return 0; //fieldRule.getValue(this);
//        }
//
//        /**
//         * Gets the number of complete seconds within the month.
//         *
//         * @return the second of month
//         */
//        public long getEpochSeconds() {
//            return seconds;
//        }
//
//        /**
//         * Gets the number of complete months since the epoch.
//         *
//         * @return the number of months since the epoch 0001-01-01 (ISO8601)
//         */
//        public long getEpochMonths() {
//            return months;
//        }
//    }

    //-----------------------------------------------------------------------
    /** Singleton instance of year rule. */
    private static final TimeFieldRule YEAR_RULE = new YearRule();
    /** Class implementing year rule. */
    static final class YearRule extends TimeFieldRule implements Serializable {
        /** Constructor. */
        public YearRule() {
            super("Year", Periods.YEARS, null, Year.MIN_YEAR, Year.MAX_YEAR);
        }

        /**
         * Resolves singletons.
         * @return the singleton instance
         */
        private Object readResolve() {
            return YEAR_RULE;
        }

        /** {@inheritDoc} */
        @Override
        public int getValue(CalendricalState calState) {
//            checkSupported(calState);
//            long epochDays0 = calState.getEpochDays();
//            return MathUtils.safeToInt((epochDays0 / MONTHS_PER_YEAR) + 1);
            return 0;
        }

        /** {@inheritDoc} */
        @Override
        public CalendricalState setValue(CalendricalState calState, int newYear1) {
//            // TODO: Validate year
//            State state = (State) calState;
//            long epochMonths0 = state.getEpochMonths();
//            long currentYear0 = epochMonths0 / MONTHS_PER_YEAR;
//            epochMonths0 += ((newYear1 - 1 - currentYear0) * MONTHS_PER_YEAR);
//            State newState = new State(epochMonths0, state.getEpochSeconds());
//            // TODO: resolve day of month
//            return newState;
            return null;
        }
    }

    //-----------------------------------------------------------------------
    /** Singleton instance of month of year rule. */
    private static final TimeFieldRule MONTH_OF_YEAR_RULE = new MonthOfYearRule();
    /** Class implementing month of year rule. */
    static final class MonthOfYearRule extends TimeFieldRule implements Serializable {
        /** Constructor. */
        public MonthOfYearRule() {
            super("MonthOfYear", Periods.MONTHS, Periods.YEARS, 1, MONTHS_PER_YEAR);
        }

        /**
         * Resolves singletons.
         * @return the singleton instance
         */
        private Object readResolve() {
            return MONTH_OF_YEAR_RULE;
        }

        /** {@inheritDoc} */
        @Override
        public int getValue(CalendricalState calState) {
            checkSupported(calState);
            State state = (State) calState;
            long epochMonths0 = state.getEpochMonths();
            return ((int) (epochMonths0 % MONTHS_PER_YEAR)) + 1;
        }

        /** {@inheritDoc} */
        @Override
        public CalendricalState setValue(CalendricalState calState, int newMonth1) {
//            // TODO: Validate month
//            State state = (State) calState;
//            long epochMonths0 = state.getEpochMonths();
//            long currentMonth0 = epochMonths0 % MONTHS_PER_YEAR;
//            epochMonths0 += (newMonth1 - 1 - currentMonth0);
//            State newState = new State(epochMonths0, state.getEpochSeconds());
//            // TODO: resolve day of month
//            return newState;
            return null;
        }
    }

    //-----------------------------------------------------------------------
    /** Singleton instance of day of year rule. */
    private static final TimeFieldRule DAY_OF_YEAR_RULE = new DayOfYearRule();
    /** Class implementing day of year rule. */
    static final class DayOfYearRule extends TimeFieldRule implements Serializable {
        /** Constructor. */
        public DayOfYearRule() {
            super("DayOfYear", Periods.DAYS, Periods.YEARS, 1, 366);
        }

        /**
         * Resolves singletons.
         * @return the singleton instance
         */
        private Object readResolve() {
            return DAY_OF_YEAR_RULE;
        }

        /** {@inheritDoc} */
        @Override
        public int getValue(CalendricalState calState) {
            checkSupported(calState);
            int monthOfYear1 = MONTH_OF_YEAR_RULE.getValue(calState);
            int dayOfMonth1 = DAY_OF_MONTH_RULE.getValue(calState);
            return (monthOfYear1 - 1) * 30 + dayOfMonth1;
        }

        /** {@inheritDoc} */
        @Override
        public CalendricalState setValue(CalendricalState calState, int newDOY1) {
            // TODO: Validate day
            int newDOY0 = newDOY1 - 1;
            int newMonth0 = newDOY0 / 30;
            int newDOM0 = newDOY0 % 30;
            calState = MONTH_OF_YEAR_RULE.setValue(calState, newMonth0);
            calState = DAY_OF_MONTH_RULE.setValue(calState, newDOM0);
            return calState;
        }
    }

    //-----------------------------------------------------------------------
    /** Singleton instance of day of month rule. */
    private static final TimeFieldRule DAY_OF_MONTH_RULE = new DayOfMonthRule();
    /** Class implementing day of month rule. */
    static final class DayOfMonthRule extends TimeFieldRule implements Serializable {
        /** Constructor. */
        public DayOfMonthRule() {
            super("DayOfMonth", Periods.DAYS, Periods.MONTHS, 1, 31);
        }

        /**
         * Resolves singletons.
         * @return the singleton instance
         */
        private Object readResolve() {
            return DAY_OF_MONTH_RULE;
        }

        /** {@inheritDoc} */
        @Override
        public int getValue(CalendricalState calState) {
            checkSupported(calState);
            State state = (State) calState;
            return ((int) (state.getEpochSeconds() % SECONDS_PER_DAY)) + 1;
        }

        /** {@inheritDoc} */
        @Override
        public CalendricalState setValue(CalendricalState calState, int newDay1) {
//            // TODO: Validate day
//            State state = (State) calState;
//            long epochSeconds0 = state.getEpochSeconds();
//            long currentDay0 = epochSeconds0 % SECONDS_PER_DAY;
//            epochSeconds0 += ((newDay1 - 1 - currentDay0) * SECONDS_PER_DAY);
//            State newState = new State(state.getEpochMonths(), epochSeconds0);
//            // TODO: resolve day of month
//            return newState;
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 28;
        }

        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue(Calendrical context) {
            // TODO: Need better algorithm
            if (context.getCalendricalState().isSupported(MONTH_OF_YEAR_RULE)) {
                int month = context.getCalendricalState().get(MONTH_OF_YEAR_RULE);
                if (month != 2) {
                    return 30;
                }
                if (context.getCalendricalState().isSupported(YEAR_RULE)) {
                    int year = context.getCalendricalState().get(YEAR_RULE);
                    return (INSTANCE.isLeapYear(year) ? 29 : 28);
                } else {
                    return 28;
                }
            }
            return 28;
        }

        /** {@inheritDoc} */
        @Override
        public int getMaximumValue(Calendrical context) {
            // TODO: Need better algorithm
            if (context.getCalendricalState().isSupported(MONTH_OF_YEAR_RULE)) {
                int month = context.getCalendricalState().get(MONTH_OF_YEAR_RULE);
                if (month != 2) {
                    return 30;
                }
                if (context.getCalendricalState().isSupported(YEAR_RULE)) {
                    int year = context.getCalendricalState().get(YEAR_RULE);
                    return (INSTANCE.isLeapYear(year) ? 6 : 5);
                } else {
                    return 6;
                }
            }
            return 30;
        }
    }

}
