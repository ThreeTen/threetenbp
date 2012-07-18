/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.MONTHS;
import static javax.time.calendrical.LocalPeriodUnit.YEARS;

import java.io.Serializable;

import javax.time.DayOfWeek;

/**
 * Provides common implementations of {@code DateTimeAdjuster}.
 * <p>
 * An adjuster should not normally used directly. Instead it should be used as follows:
 * <pre>
 *   date = date.with(adjuster);
 * </pre>
 * 
 * <h4>Implementation notes</h4>
 * This is a thread-safe utility class.
 * All returned adjusters are immutable and thread-safe.
 */
public final class DateTimeAdjusters {

    /**
     * Private constructor since this is a utility class.
     */
    private DateTimeAdjusters() {
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the "first day of month" adjuster, which returns a new date set to
     * the first day of the current month.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 will return 2011-01-01.<br />
     * The input 2011-02-15 will return 2011-02-01.
     *
     * @return the first day-of-month adjuster, not null
     */
    public static DateTimeAdjuster firstDayOfMonth() {
        return Impl.FIRST_DAY_OF_MONTH;
    }

    /**
     * Returns the "last day of month" adjuster, which returns a new date set to
     * the last day of the current month.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 will return 2011-01-31.<br />
     * The input 2011-02-15 will return 2011-02-28.<br />
     * The input 2012-02-15 will return 2012-02-29 (leap year).<br />
     * The input 2011-04-15 will return 2011-04-30.
     *
     * @return the last day-of-month adjuster, not null
     */
    public static DateTimeAdjuster lastDayOfMonth() {
        return Impl.LAST_DAY_OF_MONTH;
    }

    /**
     * Returns the "first day of next month" adjuster, which returns a new date set to
     * the first day of the next month.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 will return 2011-02-01.<br />
     * The input 2011-02-15 will return 2011-03-01.
     *
     * @return the first day of next month adjuster, not null
     */
    public static DateTimeAdjuster firstDayOfNextMonth() {
        return Impl.FIRST_DAY_OF_NEXT_MONTH;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the "first day of year" adjuster, which returns a new date set to
     * the first day of the current year.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 will return 2011-01-01.<br />
     * The input 2011-02-15 will return 2011-01-01.<br />
     *
     * @return the first day-of-year adjuster, not null
     */
    public static DateTimeAdjuster firstDayOfYear() {
        return Impl.FIRST_DAY_OF_YEAR;
    }

    /**
     * Returns the "last day of year" adjuster, which returns a new date set to
     * the last day of the current year.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 will return 2011-12-31.<br />
     * The input 2011-02-15 will return 2011-12-31.<br />
     *
     * @return the last day-of-year adjuster, not null
     */
    public static DateTimeAdjuster lastDayOfYear() {
        return Impl.LAST_DAY_OF_YEAR;
    }

    /**
     * Returns the "first day of next year" adjuster, which returns a new date set to
     * the first day of the next year.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 will return 2012-01-01.
     *
     * @return the first day of next month adjuster, not null
     */
    public static DateTimeAdjuster firstDayOfNextYear() {
        return Impl.FIRST_DAY_OF_NEXT_YEAR;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the adjusters.
     */
    private static enum Impl implements DateTimeAdjuster {
        /** First day of month adjuster. */
        FIRST_DAY_OF_MONTH,
        /** Last day of month adjuster. */
        LAST_DAY_OF_MONTH,
        /** First day of next month adjuster. */
        FIRST_DAY_OF_NEXT_MONTH,
        /** First day of year adjuster. */
        FIRST_DAY_OF_YEAR,
        /** Last day of year adjuster. */
        LAST_DAY_OF_YEAR,
        /** First day of next month adjuster. */
        FIRST_DAY_OF_NEXT_YEAR;
        @Override
        public AdjustableDateTime doAdjustment(AdjustableDateTime cal) {
            switch (this) {
                case FIRST_DAY_OF_MONTH: return cal.with(DAY_OF_MONTH, 1);
                case LAST_DAY_OF_MONTH: return cal.with(DAY_OF_MONTH, 1).plus(1, MONTHS).minus(1, DAYS);
                case FIRST_DAY_OF_NEXT_MONTH: return cal.with(DAY_OF_MONTH, 1).plus(1, MONTHS);
                case FIRST_DAY_OF_YEAR: return cal.with(DAY_OF_YEAR, 1);
                case LAST_DAY_OF_YEAR: return cal.with(DAY_OF_YEAR, 1).plus(1, YEARS).minus(1, DAYS);
                case FIRST_DAY_OF_NEXT_YEAR: return cal.with(DAY_OF_YEAR, 1).plus(1, YEARS);
            }
            throw new IllegalStateException("Unreachable");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the first in month adjuster, which returns a new date
     * in the same month with the first matching day-of-week.
     * This is used for expressions like 'first Tuesday in March'.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-12-15 for (MONDAY) will return 2011-12-03.<br />
     * The input 2011-12-15 for (TUESDAY) will return 2011-12-04.<br />
     *
     * @param dayOfWeek  the day-of-week, not null
     * @return the first in month adjuster, not null
     */
    public static DateTimeAdjuster firstInMonth(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            throw new NullPointerException("DayOfWeek must not be null");
        }
        return new DayOfWeekInMonth(1, dayOfWeek);
    }

    /**
     * Returns the day-of-week in month adjuster, which returns a new date
     * in the same month with the ordinal day-of-week.
     * This is used for expressions like the 'second Tuesday in March'.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-12-15 for (1,MONDAY) will return 2011-12-03.<br />
     * The input 2011-12-15 for (2,TUESDAY) will return 2011-12-11.<br />
     * The input 2011-12-15 for (3,TUESDAY) will return 2011-12-18.<br />
     * The input 2011-12-15 for (4,TUESDAY) will return 2011-12-25.<br />
     * The input 2011-12-15 for (5,TUESDAY) will return 2012-01-01.<br />
     * <p>
     * The algorithm is equivalent to finding the first day-of-week that matches
     * within the month and then adding a number of weeks to it.
     * The ordinal number of weeks is not validated and is interpreted leniently
     * according to this algorithm.
     *
     * @param ordinal  the week within the month, unbound but typically from 1 to 5
     * @param dayOfWeek  the day-of-week, not null
     * @return the day-of-week in month adjuster, not null
     * @throws IllegalArgumentException if the ordinal is invalid
     */
    public static DateTimeAdjuster dayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            throw new NullPointerException("DayOfWeek must not be null");
        }
        return new DayOfWeekInMonth(ordinal, dayOfWeek);
    }

    /**
     * Class implementing day-of-week in month adjuster.
     */
    private static final class DayOfWeekInMonth implements DateTimeAdjuster, Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /** The ordinal. */
        private final int ordinal;
        /** The day-of-week value, from 1 to 7. */
        private final int dowValue;

        /**
         * Constructor.
         * @param ordinal  ordinal, from 1 to 5
         * @param dow  the day-of-week, not null
         */
        private DayOfWeekInMonth(int ordinal, DayOfWeek dow) {
            super();
            this.ordinal = ordinal;
            this.dowValue = dow.getValue();
        }

        @Override
        public AdjustableDateTime doAdjustment(AdjustableDateTime cal) {
            AdjustableDateTime temp = cal.with(DAY_OF_MONTH, 1);
            long curDow0 = temp.get(DAY_OF_WEEK) - 1;
            long newDow0 = dowValue - 1;
            long dowDiff = (newDow0 - curDow0 + 7) % 7;
            dowDiff += (ordinal - 1L) * 7L;  // safe from overflow
            return temp.plus(dowDiff, DAYS);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DayOfWeekInMonth) {
                DayOfWeekInMonth other = (DayOfWeekInMonth) obj;
                return ordinal == other.ordinal && dowValue == other.dowValue;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return ordinal + 8 * dowValue;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the next day-of-week adjuster, which adjusts the date to be
     * the next of the specified day-of-week after the specified date.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-17 (two days later).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-22 (one week later).
     *
     * @param dow  the day-of-week to move the date to, not null
     * @return the next day-of-week adjuster, not null
     */
    public static DateTimeAdjuster next(DayOfWeek dow) {
        if (dow == null) {
            throw new NullPointerException("DayOfWeek must not be null");
        }
        return new RelativeDayOfWeek(2, dow);
    }

    /**
     * Returns the next or current day-of-week adjuster, which adjusts the
     * date to be be the next of the specified day-of-week, returning the
     * input date if the day-of-week matched.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-17 (two days later).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-15 (same as input).
     *
     * @param dow  the day-of-week to move the date to, not null
     * @return the next day-of-week adjuster, not null
     */
    public static DateTimeAdjuster nextOrCurrent(DayOfWeek dow) {
        if (dow == null) {
            throw new NullPointerException("DayOfWeek must not be null");
        }
        return new RelativeDayOfWeek(0, dow);
    }

    /**
     * Returns the previous day-of-week adjuster, which adjusts the date to be
     * the previous of the specified day-of-week after the specified date.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-10 (five days earlier).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-08 (one week earlier).
     *
     * @param dow  the day-of-week to move the date to, not null
     * @return the next day-of-week adjuster, not null
     */
    public static DateTimeAdjuster previous(DayOfWeek dow) {
        if (dow == null) {
            throw new NullPointerException("DayOfWeek must not be null");
        }
        return new RelativeDayOfWeek(3, dow);
    }

    /**
     * Returns the previous or current day-of-week adjuster, which adjusts the
     * date to be be the previous of the specified day-of-week, returning the
     * input date if the day-of-week matched.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-10 (five days earlier).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-15 (same as input).
     *
     * @param dow  the day-of-week to move the date to, not null
     * @return the next day-of-week adjuster, not null
     */
    public static DateTimeAdjuster previousOrCurrent(DayOfWeek dow) {
        if (dow == null) {
            throw new NullPointerException("DayOfWeek must not be null");
        }
        return new RelativeDayOfWeek(1, dow);
    }

    /**
     * Implementation of next, previous or current day-of-week.
     */
    private static final class RelativeDayOfWeek implements DateTimeAdjuster, Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Whether the current date is a valid answer. */
        private final int relative;
        /** The day-of-week value, from 1 to 7. */
        private final int dowValue;

        private RelativeDayOfWeek(int relative, DayOfWeek dow) {
            this.relative = relative;
            this.dowValue = dow.getValue();
        }

        @Override
        public AdjustableDateTime doAdjustment(AdjustableDateTime cal) {
            long calDow = cal.get(DAY_OF_WEEK);
            if (relative < 2 && calDow == dowValue) {
                return cal;
            }
            if ((relative & 1) == 0) {
                long daysDiff = calDow - dowValue;
                return cal.plus(daysDiff >= 0 ? 7 - daysDiff : -daysDiff, DAYS);
            } else {
                long daysDiff = dowValue - calDow;
                return cal.minus(daysDiff >= 0 ? 7 - daysDiff : -daysDiff, DAYS);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RelativeDayOfWeek) {
                RelativeDayOfWeek other = (RelativeDayOfWeek) obj;
                return relative == other.relative && dowValue == other.dowValue;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return dowValue * 256 + relative * 7;
        }
    }

}
