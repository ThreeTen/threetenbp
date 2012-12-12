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

import static javax.time.calendrical.ChronoField.DAY_OF_MONTH;
import static javax.time.calendrical.ChronoField.DAY_OF_WEEK;
import static javax.time.calendrical.ChronoField.DAY_OF_YEAR;
import static javax.time.calendrical.ChronoUnit.DAYS;
import static javax.time.calendrical.ChronoUnit.MONTHS;
import static javax.time.calendrical.ChronoUnit.YEARS;

import java.util.Objects;

import javax.time.DayOfWeek;
import javax.time.calendrical.DateTime.WithAdjuster;

/**
 * Common implementations of {@code DateTime.WithAdjuster}.
 * <p>
 * Adjusters are the principal tool for altering date-times.
 * An adjuster should not normally used directly. Instead it should be used as follows:
 * <pre>
 *   date = date.with(adjuster);
 * </pre>
 * <p>
 * The adjusters provided by this class are primarily intended to be pre-packaged
 * and pre-tested pieces of business logic. They are especially useful to document
 * the intent of code and often link well to requirements.
 * For example, these two pieces of code do the same thing, but the former is clearer
 * (assuming that there is a static import of this class):
 * <pre>
 *  // direct manipulation
 *  date.withDayOfMonth(1).plusMonths(1).minusDays(1);
 *  // use of an adjuster from this class
 *  date.with(lastDayOfMonth());
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
    public static WithAdjuster firstDayOfMonth() {
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
    public static WithAdjuster lastDayOfMonth() {
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
    public static WithAdjuster firstDayOfNextMonth() {
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
    public static WithAdjuster firstDayOfYear() {
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
    public static WithAdjuster lastDayOfYear() {
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
    public static WithAdjuster firstDayOfNextYear() {
        return Impl.FIRST_DAY_OF_NEXT_YEAR;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the adjusters.
     */
    private static enum Impl implements WithAdjuster {
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
        public DateTime doWithAdjustment(DateTime dateTime) {
            switch (this) {
                case FIRST_DAY_OF_MONTH: return dateTime.with(DAY_OF_MONTH, 1);
                case LAST_DAY_OF_MONTH: return dateTime.with(DAY_OF_MONTH, dateTime.range(DAY_OF_MONTH).getMaximum());
                case FIRST_DAY_OF_NEXT_MONTH: return dateTime.with(DAY_OF_MONTH, 1).plus(1, MONTHS);
                case FIRST_DAY_OF_YEAR: return dateTime.with(DAY_OF_YEAR, 1);
                case LAST_DAY_OF_YEAR: return dateTime.with(DAY_OF_YEAR, dateTime.range(DAY_OF_YEAR).getMaximum());
                case FIRST_DAY_OF_NEXT_YEAR: return dateTime.with(DAY_OF_YEAR, 1).plus(1, YEARS);
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
     * The input 2011-12-15 for (MONDAY) will return 2011-12-05.<br />
     * The input 2011-12-15 for (FRIDAY) will return 2011-12-02.<br />
     *
     * @param dayOfWeek  the day-of-week, not null
     * @return the first in month adjuster, not null
     */
    public static WithAdjuster firstInMonth(DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        return new DayOfWeekInMonth(1, dayOfWeek);
    }

    /**
     * Returns the last in month adjuster, which returns a new date
     * in the same month with the last matching day-of-week.
     * This is used for expressions like 'last Tuesday in March'.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-12-15 for (MONDAY) will return 2011-12-26.<br />
     * The input 2011-12-15 for (FRIDAY) will return 2011-12-30.<br />
     *
     * @param dayOfWeek  the day-of-week, not null
     * @return the first in month adjuster, not null
     */
    public static WithAdjuster lastInMonth(DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        return new DayOfWeekInMonth(-1, dayOfWeek);
    }

    /**
     * Returns the day-of-week in month adjuster, which returns a new date
     * in the same month with the ordinal day-of-week.
     * This is used for expressions like the 'second Tuesday in March'.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-12-15 for (1,TUESDAY) will return 2011-12-06.<br />
     * The input 2011-12-15 for (2,TUESDAY) will return 2011-12-13.<br />
     * The input 2011-12-15 for (3,TUESDAY) will return 2011-12-20.<br />
     * The input 2011-12-15 for (4,TUESDAY) will return 2011-12-27.<br />
     * The input 2011-12-15 for (5,TUESDAY) will return 2012-01-03.<br />
     * The input 2011-12-15 for (-1,TUESDAY) will return 2011-12-27 (last in month).<br />
     * The input 2011-12-15 for (-4,TUESDAY) will return 2011-12-06 (3 weeks before last in month).<br />
     * The input 2011-12-15 for (-5,TUESDAY) will return 2011-11-29 (4 weeks before last in month).<br />
     * The input 2011-12-15 for (0,TUESDAY) will return 2011-11-29 (last in previous month).<br />
     * <p>
     * For a positive or zero ordinal, the algorithm is equivalent to finding the first
     * day-of-week that matches within the month and then adding a number of weeks to it.
     * For a negative ordinal, the algorithm is equivalent to finding the last
     * day-of-week that matches within the month and then subtracting a number of weeks to it.
     * The ordinal number of weeks is not validated and is interpreted leniently
     * according to this algorithm. This definition means that an ordinal of zero finds
     * the last matching day-of-week in the previous month.
     *
     * @param ordinal  the week within the month, unbound but typically from 1 to 5
     * @param dayOfWeek  the day-of-week, not null
     * @return the day-of-week in month adjuster, not null
     * @throws IllegalArgumentException if the ordinal is invalid
     */
    public static WithAdjuster dayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
        Objects.requireNonNull(dayOfWeek, "dayOfWeek");
        return new DayOfWeekInMonth(ordinal, dayOfWeek);
    }

    /**
     * Class implementing day-of-week in month adjuster.
     */
    private static final class DayOfWeekInMonth implements WithAdjuster {
        /** The ordinal. */
        private final int ordinal;
        /** The day-of-week value, from 1 to 7. */
        private final int dowValue;

        private DayOfWeekInMonth(int ordinal, DayOfWeek dow) {
            super();
            this.ordinal = ordinal;
            this.dowValue = dow.getValue();
        }
        @Override
        public DateTime doWithAdjustment(DateTime dateTime) {
            if (ordinal >= 0) {
                DateTime temp = dateTime.with(DAY_OF_MONTH, 1);
                int curDow = temp.get(DAY_OF_WEEK);
                int dowDiff = (dowValue - curDow + 7) % 7;
                dowDiff += (ordinal - 1L) * 7L;  // safe from overflow
                return temp.plus(dowDiff, DAYS);
            } else {
                DateTime temp = dateTime.with(DAY_OF_MONTH, dateTime.range(DAY_OF_MONTH).getMaximum());
                int curDow = temp.get(DAY_OF_WEEK);
                int daysDiff = dowValue - curDow;
                daysDiff = (daysDiff == 0 ? 0 : (daysDiff > 0 ? daysDiff - 7 : daysDiff));
                daysDiff -= (-ordinal - 1L) * 7L;  // safe from overflow
                return temp.plus(daysDiff, DAYS);
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the next day-of-week adjuster, which adjusts the date to the
     * first occurrence of the specified day-of-week after the date being adjusted.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-17 (two days later).<br />
     * The input 2011-01-15 (a Saturday) for parameter (WEDNESDAY) will return 2011-01-19 (four days later).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-22 (seven days later).
     *
     * @param dayOfWeek  the day-of-week to move the date to, not null
     * @return the next day-of-week adjuster, not null
     */
    public static WithAdjuster next(DayOfWeek dayOfWeek) {
        return new RelativeDayOfWeek(2, dayOfWeek);
    }

    /**
     * Returns the next-or-same day-of-week adjuster, which adjusts the date to the
     * first occurrence of the specified day-of-week after the date being adjusted
     * unless it is already on that day in which case the same object is returned.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-17 (two days later).<br />
     * The input 2011-01-15 (a Saturday) for parameter (WEDNESDAY) will return 2011-01-19 (four days later).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-15 (same as input).
     *
     * @param dayOfWeek  the day-of-week to check for or move the date to, not null
     * @return the next-or-same day-of-week adjuster, not null
     */
    public static WithAdjuster nextOrSame(DayOfWeek dayOfWeek) {
        return new RelativeDayOfWeek(0, dayOfWeek);
    }

    /**
     * Returns the previous day-of-week adjuster, which adjusts the date to the
     * first occurrence of the specified day-of-week before the date being adjusted.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-10 (five days earlier).<br />
     * The input 2011-01-15 (a Saturday) for parameter (WEDNESDAY) will return 2011-01-12 (three days earlier).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-08 (seven days earlier).
     *
     * @param dayOfWeek  the day-of-week to move the date to, not null
     * @return the previous day-of-week adjuster, not null
     */
    public static WithAdjuster previous(DayOfWeek dayOfWeek) {
        return new RelativeDayOfWeek(3, dayOfWeek);
    }

    /**
     * Returns the previous-or-same day-of-week adjuster, which adjusts the date to the
     * first occurrence of the specified day-of-week before the date being adjusted
     * unless it is already on that day in which case the same object is returned.
     * <p>
     * The ISO calendar system behaves as follows:<br />
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-10 (five days earlier).<br />
     * The input 2011-01-15 (a Saturday) for parameter (WEDNESDAY) will return 2011-01-12 (three days earlier).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-15 (same as input).
     *
     * @param dayOfWeek  the day-of-week to check for or move the date to, not null
     * @return the previous-or-same day-of-week adjuster, not null
     */
    public static WithAdjuster previousOrSame(DayOfWeek dayOfWeek) {
        return new RelativeDayOfWeek(1, dayOfWeek);
    }

    /**
     * Implementation of next, previous or current day-of-week.
     */
    private static final class RelativeDayOfWeek implements WithAdjuster {
        /** Whether the current date is a valid answer. */
        private final int relative;
        /** The day-of-week value, from 1 to 7. */
        private final int dowValue;

        private RelativeDayOfWeek(int relative, DayOfWeek dayOfWeek) {
            Objects.requireNonNull(dayOfWeek, "dayOfWeek");
            this.relative = relative;
            this.dowValue = dayOfWeek.getValue();
        }

        @Override
        public DateTime doWithAdjustment(DateTime dateTime) {
            int calDow = dateTime.get(DAY_OF_WEEK);
            if (relative < 2 && calDow == dowValue) {
                return dateTime;
            }
            if ((relative & 1) == 0) {
                int daysDiff = calDow - dowValue;
                return dateTime.plus(daysDiff >= 0 ? 7 - daysDiff : -daysDiff, DAYS);
            } else {
                int daysDiff = dowValue - calDow;
                return dateTime.minus(daysDiff >= 0 ? 7 - daysDiff : -daysDiff, DAYS);
            }
        }
    }

}
