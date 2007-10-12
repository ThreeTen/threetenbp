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
package javax.time.i18n;

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.calendar.TimeFieldRule;
import javax.time.calendar.UnsupportedCalendarFieldException;
import javax.time.duration.DurationUnit;
import javax.time.duration.Durations;

/**
 * The Coptic calendar system.
 * <p>
 * CopticChronology defines the rules of the Coptic calendar system.
 * <p>
 * CopticDate is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CopticChronology implements Serializable {

    /**
     * The singleton instance of <code>CopticChronology</code>.
     */
    public static final CopticChronology INSTANCE = new CopticChronology();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private CopticChronology() {
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
     *
     * @param year  the year to check, from 1 to MAX_VALUE
     * @return true if the year is a leap year
     */
    public boolean isLeapYear(int year) {
        return ((year % 4) == 3);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, never null
     */
    public String getName() {
        return "Coptic";
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state from year, month, day.
     *
     * @param year  the year
     * @param month  the month of year
     * @param day  the day of month
     * @return the state, never null
     */
    public CalendricalState stateFromYearMonthDay(int year, int month, int day) {
        year--;
        month--;
        day--;
        long epochMonths = year;
        epochMonths *= 13 + month;
        long epochSeconds = ((long) day) * 24 * 60 * 60;
        return new State(epochMonths, epochSeconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state from year, month, day.
     *
     * @param year  the year
     * @param month  the month of year
     * @param day  the day of month
     * @return the state, never null
     */
    public long convert(long amount, DurationUnit fromUnit, DurationUnit toUnit) {
        return 0;
    }

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

    //-----------------------------------------------------------------------
    /**
     * A debugging description of this class.
     *
     * @return a string form for debugging, never null
     */
    @Override
    public String toString() {
        return "CopticChronology";
    }

    //-----------------------------------------------------------------------
    /**
     * State implementation for the Coptic calendar system.
     */
    public static class State extends CalendricalState {

        /** The months since the epoch of 0001-01-01 (Coptic). */
        private final long months;
        /** The second of month. */
        private final long seconds;

        /**
         * Constructor.
         *
         * @param months  the months since the epoch of 0001-01-01 (Coptic)
         * @param seconds  the second of month
         */
        State(long months, long seconds) {
            super();
            this.months = months;
            this.seconds = seconds;
        }

        /** {@inheritDoc} */
        @Override
        public DurationUnit getDurationUnit() {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public DurationUnit getDurationRange() {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isSupported(TimeFieldRule fieldRule) {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public int get(TimeFieldRule fieldRule) {
            if (false) {
                throw new UnsupportedCalendarFieldException();
            }
            return 0; //fieldRule.getValue(this);
        }

        /**
         * Gets the number of complete seconds within the month.
         *
         * @return the second of month
         */
        public long getEpochSeconds() {
            return seconds;
        }

        /**
         * Gets the number of complete months since the epoch.
         *
         * @return the number of months since the epoch 0001-01-01 (Coptic)
         */
        public long getEpochMonths() {
            return months;
        }
    }

    //-----------------------------------------------------------------------
    /** Singleton instance of year rule. */
    private static final TimeFieldRule YEAR_RULE = new YearRule();
    /** Class implementing year rule. */
    static final class YearRule extends TimeFieldRule implements Serializable {
        /** Constructor. */
        public YearRule() {
            super("Year", Durations.YEARS, null, 1, Integer.MAX_VALUE);
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
            checkSupported(calState);
            State state = (State) calState;
            long epochMonths0 = state.getEpochMonths();
            return MathUtils.safeToInt((epochMonths0 / 13) + 1);
        }

        /** {@inheritDoc} */
        @Override
        public CalendricalState setValue(CalendricalState calState, int newYear1) {
            // TODO: Validate year
            State state = (State) calState;
            long epochMonths0 = state.getEpochMonths();
            long currentYear0 = epochMonths0 / 13;
            epochMonths0 += ((newYear1 - 1 - currentYear0) * 13);
            State newState = new State(epochMonths0, state.getEpochSeconds());
            // TODO: resolve day of month
            return newState;
        }
    }

    //-----------------------------------------------------------------------
    /** Singleton instance of month of year rule. */
    private static final TimeFieldRule MONTH_OF_YEAR_RULE = new MonthOfYearRule();
    /** Class implementing month of year rule. */
    static final class MonthOfYearRule extends TimeFieldRule implements Serializable {
        /** Constructor. */
        public MonthOfYearRule() {
            super("MonthOfYear", Durations.MONTHS, Durations.YEARS, 1, 13);
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
            return ((int) (epochMonths0 % 13)) + 1;
        }

        /** {@inheritDoc} */
        @Override
        public CalendricalState setValue(CalendricalState calState, int newMonth1) {
            // TODO: Validate month
            State state = (State) calState;
            long epochMonths0 = state.getEpochMonths();
            long currentMonth0 = epochMonths0 % 13;
            epochMonths0 += (newMonth1 - 1 - currentMonth0);
            State newState = new State(epochMonths0, state.getEpochSeconds());
            // TODO: resolve day of month
            return newState;
        }
    }

    //-----------------------------------------------------------------------
    /** Singleton instance of day of year rule. */
    private static final TimeFieldRule DAY_OF_YEAR_RULE = new DayOfYearRule();
    /** Class implementing day of year rule. */
    static final class DayOfYearRule extends TimeFieldRule implements Serializable {
        /** Constructor. */
        public DayOfYearRule() {
            super("DayOfYear", Durations.DAYS, Durations.YEARS, 1, 366);
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
            super("DayOfMonth", Durations.DAYS, Durations.MONTHS, 1, 30);
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
            return ((int) (state.getEpochSeconds() % (24 * 60 * 60))) + 1;
        }

        /** {@inheritDoc} */
        @Override
        public CalendricalState setValue(CalendricalState calState, int newDay1) {
            // TODO: Validate day
            State state = (State) calState;
            long epochSeconds0 = state.getEpochSeconds();
            long currentDay0 = epochSeconds0 % (24 * 60 * 60);
            epochSeconds0 += ((newDay1 - 1 - currentDay0) * 24 * 60 * 60);
            State newState = new State(state.getEpochMonths(), epochSeconds0);
            // TODO: resolve day of month
            return newState;
        }

        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue() {
            return 5;
        }

        /** {@inheritDoc} */
        @Override
        public int getSmallestMaximumValue(Calendrical context) {
            // TODO: Need better algorithm
            if (context.getCalendricalState().isSupported(MONTH_OF_YEAR_RULE)) {
                int month = context.getCalendricalState().get(MONTH_OF_YEAR_RULE);
                if (month < 13) {
                    return 30;
                }
                if (context.getCalendricalState().isSupported(YEAR_RULE)) {
                    int year = context.getCalendricalState().get(YEAR_RULE);
                    return (INSTANCE.isLeapYear(year) ? 6 : 5);
                } else {
                    return 5;
                }
            }
            return 5;
        }

        /** {@inheritDoc} */
        @Override
        public int getMaximumValue(Calendrical context) {
            // TODO: Need better algorithm
            if (context.getCalendricalState().isSupported(MONTH_OF_YEAR_RULE)) {
                int month = context.getCalendricalState().get(MONTH_OF_YEAR_RULE);
                if (month < 13) {
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
