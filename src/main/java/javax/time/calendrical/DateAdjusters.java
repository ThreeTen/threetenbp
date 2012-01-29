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
package javax.time.calendrical;

import java.io.Serializable;

import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.MonthOfYear;

/**
 * Provides common implementations of {@code DateAdjuster}.
 * <p>
 * This is a thread-safe utility class.
 * All adjusters returned are immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateAdjusters {

    /**
     * Private constructor since this is a utility class.
     */
    private DateAdjusters() {
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the "first day of month" adjuster, which returns a new date set to
     * the first day of the current month.
     * <p>
     * The input 2011-01-15 will return 2011-01-01.<br />
     * The input 2011-02-15 will return 2011-02-01.
     *
     * @return the first day-of-month adjuster, not null
     */
    public static DateAdjuster firstDayOfMonth() {
        return Impl.FIRST_DAY_OF_MONTH;
    }

    /**
     * Returns the "last day of month" adjuster, which returns a new date set to
     * the last day of the current month.
     * <p>
     * The input 2011-01-15 will return 2011-01-31.<br />
     * The input 2011-02-15 will return 2011-02-28.<br />
     * The input 2012-02-15 will return 2012-02-29 (leap year).<br />
     * The input 2011-04-15 will return 2011-04-30.
     *
     * @return the last day-of-month adjuster, not null
     */
    public static DateAdjuster lastDayOfMonth() {
        return Impl.LAST_DAY_OF_MONTH;
    }

    /**
     * Returns the "first day of next month" adjuster, which returns a new date set to
     * the first day of the next month.
     * <p>
     * The input 2011-01-15 will return 2011-02-01.<br />
     * The input 2011-02-15 will return 2011-03-01.
     *
     * @return the first day of next month adjuster, not null
     */
    public static DateAdjuster firstDayOfNextMonth() {
        return Impl.FIRST_DAY_OF_NEXT_MONTH;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the "first day of year" adjuster, which returns a new date set to
     * the first day of the current year - January 1.
     * <p>
     * The input 2011-01-15 will return 2011-01-01.<br />
     * The input 2011-02-15 will return 2011-01-01.<br />
     *
     * @return the first day-of-year adjuster, not null
     */
    public static DateAdjuster firstDayOfYear() {
        return Impl.FIRST_DAY_OF_YEAR;
    }

    /**
     * Returns the "last day of year" adjuster, which returns a new date set to
     * the last day of the current year - December 31.
     * <p>
     * The input 2011-01-15 will return 2011-12-31.<br />
     * The input 2011-02-15 will return 2011-12-31.<br />
     *
     * @return the last day-of-year adjuster, not null
     */
    public static DateAdjuster lastDayOfYear() {
        return Impl.LAST_DAY_OF_YEAR;
    }

    /**
     * Returns the "first day of next year" adjuster, which returns a new date set to
     * the first day of the next year - January 1.
     * <p>
     * The input 2011-01-15 will return 2012-01-01.
     *
     * @return the first day of next month adjuster, not null
     */
    public static DateAdjuster firstDayOfNextYear() {
        return Impl.FIRST_DAY_OF_NEXT_YEAR;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the adjusters.
     */
    private static enum Impl implements DateAdjuster {
        /** First day of month adjuster. */
        FIRST_DAY_OF_MONTH {
            /** {@inheritDoc} */
            public LocalDate adjustDate(LocalDate date) {
                return date.withDayOfMonth(1);
            }
        },
        /** Last day of month adjuster. */
        LAST_DAY_OF_MONTH {
            /** {@inheritDoc} */
            public LocalDate adjustDate(LocalDate date) {
                int dom = date.getMonthOfYear().getLastDayOfMonth(date.isLeapYear());
                return date.withDayOfMonth(dom);
            }
        },
        /** First day of next month adjuster. */
        FIRST_DAY_OF_NEXT_MONTH {
            /** {@inheritDoc} */
            public LocalDate adjustDate(LocalDate date) {
                return date.withDayOfMonth(1).plusMonths(1);
            }
        },
        /** First day of year adjuster. */
        FIRST_DAY_OF_YEAR {
            /** {@inheritDoc} */
            public LocalDate adjustDate(LocalDate date) {
                return LocalDate.of(date.getYear(), MonthOfYear.JANUARY, 1);
            }
        },
        /** Last day of year adjuster. */
        LAST_DAY_OF_YEAR {
            /** {@inheritDoc} */
            public LocalDate adjustDate(LocalDate date) {
                return LocalDate.of(date.getYear(), MonthOfYear.DECEMBER, 31);
            }
        },
        /** First day of next month adjuster. */
        FIRST_DAY_OF_NEXT_YEAR {
            /** {@inheritDoc} */
            public LocalDate adjustDate(LocalDate date) {
                return date.withDayOfYear(1).plusYears(1);
            }
        },
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the first in month adjuster, which returns a new date
     * in the same month with the first matching day-of-week. This is used for
     * expressions like 'first Tuesday in March'.
     * <p>
     * The input 2011-12-15 for (MONDAY) will return 2011-12-03.<br />
     * The input 2011-12-15 for (TUESDAY) will return 2011-12-04.<br />
     *
     * @param dayOfWeek  the day-of-week, not null
     * @return the first in month adjuster, not null
     */
    public static DateAdjuster firstInMonth(DayOfWeek dayOfWeek) {
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
     * The input 2011-12-15 for (1,MONDAY) will return 2011-12-03.<br />
     * The input 2011-12-15 for (2,TUESDAY) will return 2011-12-11.<br />
     * The input 2011-12-15 for (3,TUESDAY) will return 2011-12-18.<br />
     * The input 2011-12-15 for (4,TUESDAY) will return 2011-12-25.<br />
     * The input 2011-12-15 for (5,TUESDAY) will return 2012-01-01.<br />
     * <p>
     * If the ordinal is 5 and there is no 5th of the requested day-of-week,
     * then the first of the next month is returned.
     *
     * @param ordinal  ordinal, from 1 to 5
     * @param dayOfWeek  the day-of-week, not null
     * @return the day-of-week in month adjuster, not null
     * @throws IllegalArgumentException if the ordinal is invalid
     */
    public static DateAdjuster dayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
        if (ordinal < 1 || ordinal > 5) {
            throw new IllegalArgumentException("Illegal value for ordinal, value " + ordinal +
                    " is not in the range 1 to 5");
        }
        if (dayOfWeek == null) {
            throw new NullPointerException("DayOfWeek must not be null");
        }
        return new DayOfWeekInMonth(ordinal, dayOfWeek);
    }

    /**
     * Class implementing day-of-week in month adjuster.
     */
    private static final class DayOfWeekInMonth implements DateAdjuster, Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;

        /** The ordinal, from 1 to 5. */
        private final int ordinal;
        /** The day-of-week. */
        private final DayOfWeek dayOfWeek;

        /**
         * Constructor.
         * @param ordinal  ordinal, from 1 to 5
         * @param dayOfWeek  the day-of-week, not null
         */
        private DayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
            super();
            this.ordinal = ordinal;
            this.dayOfWeek = dayOfWeek;
        }

        /** {@inheritDoc} */
        public LocalDate adjustDate(LocalDate date) {
            LocalDate temp = date.withDayOfMonth(1);
            int curDow = temp.getDayOfWeek().ordinal();
            int newDow = dayOfWeek.ordinal();
            int dowDiff = (newDow - curDow + 7) % 7;
            dowDiff += (ordinal - 1) * 7;
            return temp.plusDays(dowDiff);
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DayOfWeekInMonth) {
                DayOfWeekInMonth other = (DayOfWeekInMonth) obj;
                return ordinal == other.ordinal && dayOfWeek == other.dayOfWeek;
            }
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return ordinal + 8 * dayOfWeek.ordinal();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the next day-of-week adjuster, which adjusts the date to be
     * the next of the specified day-of-week after the specified date.
     * <p>
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-17 (two days later).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-22 (one week later).
     *
     * @param dow  the day-of-week to move the date to, not null
     * @return the next day-of-week adjuster, not null
     */
    public static DateAdjuster next(DayOfWeek dow) {
        if (dow == null) {
            throw new NullPointerException("dow must not be null");
        }
        return new RelativeDayOfWeek(2, dow);
    }

    /**
     * Returns the next or current day-of-week adjuster, which adjusts the
     * date to be be the next of the specified day-of-week, returning the
     * input date if the day-of-week matched.
     * <p>
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-17 (two days later).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-15 (same as input).
     *
     * @param dow  the day-of-week to move the date to, not null
     * @return the next day-of-week adjuster, not null
     */
    public static DateAdjuster nextOrCurrent(DayOfWeek dow) {
        if (dow == null) {
            throw new NullPointerException("dow must not be null");
        }
        return new RelativeDayOfWeek(0, dow);
    }

    /**
     * Returns the previous day-of-week adjuster, which adjusts the date to be
     * the previous of the specified day-of-week after the specified date.
     * <p>
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-10 (five days earlier).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-08 (one week earlier).
     *
     * @param dow  the day-of-week to move the date to, not null
     * @return the next day-of-week adjuster, not null
     */
    public static DateAdjuster previous(DayOfWeek dow) {
        if (dow == null) {
            throw new NullPointerException("dow must not be null");
        }
        return new RelativeDayOfWeek(3, dow);
    }

    /**
     * Returns the previous or current day-of-week adjuster, which adjusts the
     * date to be be the previous of the specified day-of-week, returning the
     * input date if the day-of-week matched.
     * <p>
     * The input 2011-01-15 (a Saturday) for parameter (MONDAY) will return 2011-01-10 (five days earlier).<br />
     * The input 2011-01-15 (a Saturday) for parameter (SATURDAY) will return 2011-01-15 (same as input).
     *
     * @param dow  the day-of-week to move the date to, not null
     * @return the next day-of-week adjuster, not null
     */
    public static DateAdjuster previousOrCurrent(DayOfWeek dow) {
        if (dow == null) {
            throw new NullPointerException("dow must not be null");
        }
        return new RelativeDayOfWeek(1, dow);
    }

    /**
     * Implementation of next, previous or current day-of-week.
     */
    private static final class RelativeDayOfWeek implements DateAdjuster, Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Whether the current date is a valid answer. */
        private final int relative;
        /** The day-of-week to find. */
        private final DayOfWeek dow;

        private RelativeDayOfWeek(int relative, DayOfWeek dow) {
            this.relative = relative;
            this.dow = dow;
        }

        /** {@inheritDoc} */
        public LocalDate adjustDate(LocalDate date) {
            DayOfWeek dow = date.getDayOfWeek();
            if (relative < 2 && dow == this.dow) {
                return date;
            }
            if ((relative & 1) == 0) {
                int daysDiff = dow.ordinal() - this.dow.ordinal();
                return date.plusDays(daysDiff >= 0 ? 7 - daysDiff : -daysDiff);
            } else {
                int daysDiff = this.dow.ordinal() - dow.ordinal();
                return date.minusDays(daysDiff >= 0 ? 7 - daysDiff : -daysDiff);
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RelativeDayOfWeek)) {
                return false;
            }
            final RelativeDayOfWeek other = (RelativeDayOfWeek) obj;
            if (this.relative != other.relative) {
                return false;
            }
            if (this.dow != other.dow) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 13;
            hash = 19 * hash + relative;
            hash = 19 * hash + dow.hashCode();
            return hash;
        }
    }
}
