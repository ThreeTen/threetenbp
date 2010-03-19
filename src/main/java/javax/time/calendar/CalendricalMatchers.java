/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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

/**
 * Provides common implementations of {@code CalendricalMatcher}.
 * <p>
 * These matchers are useful and common implementations of {@link CalendricalMatcher}.
 * A matcher allows any type of matching to be performed against a calendrical.
 * Examples might be checking of the calendrical represents Friday the Thirteenth,
 * or the last day of the month, or one of the American continent time-zones.
 * <p>
 * CalendricalMatchers is a utility class.
 * All matchers returned are immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class CalendricalMatchers {

    /**
     * Private constructor since this is a utility class
     */
    private CalendricalMatchers() {
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the leap year matcher, which returns true if the date
     * is in a leap year.
     *
     * @return the leap year matcher, never null
     */
    public static CalendricalMatcher leapYear() {
        return Impl.LEAP_YEAR;
    }

    /**
     * Returns the leap day matcher, which returns true if the date
     * is February 29th in a leap year.
     *
     * @return the leap day matcher, never null
     */
    public static CalendricalMatcher leapDay() {
        return Impl.LEAP_DAY;
    }

    /**
     * Returns the last day-of-month matcher, which returns true if the date
     * is the last valid day of the month.
     *
     * @return the last day-of-month matcher, never null
     */
    public static CalendricalMatcher lastDayOfMonth() {
        return Impl.LAST_DAY_OF_MONTH;
    }

    /**
     * Returns the last day-of-year matcher, which returns true if the date is
     * the last valid day of the year.
     *
     * @return the last day-of-year matcher, never null
     */
    public static CalendricalMatcher lastDayOfYear() {
        return Impl.LAST_DAY_OF_YEAR;
    }

    /**
     * Returns the weekend day matcher, which returns true if the date
     * is Saturday or Sunday.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however a DateMatcher
     * can be easily written to do so.
     *
     * @return the non weekend day matcher, never null
     */
    public static CalendricalMatcher weekendDay() {
        return Impl.WEEKEND_DAY;
    }

    /**
     * Returns the non weekend day matcher, which returns true if the date
     * is between Monday and Friday inclusive.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however a DateMatcher
     * can be easily written to do so.
     *
     * @return the non weekend day matcher, never null
     */
    public static CalendricalMatcher nonWeekendDay() {
        return Impl.NON_WEEKEND_DAY;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the adjusters.
     */
    private static enum Impl implements CalendricalMatcher {
        /** Leap year matcher. */
        LEAP_YEAR {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                Integer yearVal = calendrical.get(ISOChronology.yearRule());
                return yearVal != null && ISOChronology.isLeapYear(yearVal);
            }
        },
        /** Leap day matcher. */
        LEAP_DAY {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                MonthOfYear moy = calendrical.get(ISOChronology.monthOfYearRule());
                Integer domVal = calendrical.get(ISOChronology.dayOfMonthRule());
                return domVal != null && domVal == 29 && moy == MonthOfYear.FEBRUARY;
            }
        },
        /** Last day-of-month matcher. */
        LAST_DAY_OF_MONTH {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                Integer yearVal = calendrical.get(ISOChronology.yearRule());
                MonthOfYear moy = calendrical.get(ISOChronology.monthOfYearRule());
                Integer domVal = calendrical.get(ISOChronology.dayOfMonthRule());
                return yearVal != null && moy != null && domVal != null &&
                        domVal == moy.getLastDayOfMonth(ISOChronology.isLeapYear(yearVal));
            }
        },
        /** Last day-of-year matcher. */
        LAST_DAY_OF_YEAR {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                MonthOfYear moy = calendrical.get(ISOChronology.monthOfYearRule());
                Integer domVal = calendrical.get(ISOChronology.dayOfMonthRule());
                return domVal != null && domVal == 31 && moy == MonthOfYear.DECEMBER;
            }
        },
        /** Non weekend matcher. */
        WEEKEND_DAY {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                DayOfWeek dow = calendrical.get(ISOChronology.dayOfWeekRule());
                return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
            }
        },
        /** Non weekend matcher. */
        NON_WEEKEND_DAY {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                DayOfWeek dow = calendrical.get(ISOChronology.dayOfWeekRule());
                return dow != null && dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY;
            }
        },
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the first in month matcher, which returns true if the date
     * is the first occurrence of day-of-week in the month.
     *
     * @param dayOfWeek  the day-of-week, not null
     * @return the first in month matcher, never null
     */
    public static CalendricalMatcher firstInMonth(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            throw new NullPointerException("DayOfWeek must not be null");
        }
        return new DayOfWeekInMonth(1, dayOfWeek);
    }

    /**
     * Returns the day-of-week in month matcher, which returns true if the
     * date is the ordinal occurrence of the day-of-week in the month.
     *
     * @param ordinal  ordinal, from 1 to 5
     * @param dayOfWeek  the day-of-week, not null
     * @return the day-of-week in month matcher, never null
     * @throws IllegalArgumentException if the ordinal is invalid
     */
    public static CalendricalMatcher dayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
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
     * Class implementing day-of-week in month matcher.
     */
    private static final class DayOfWeekInMonth implements CalendricalMatcher, Serializable {
        /** A serialization identifier for this class. */
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
        public boolean matchesCalendrical(Calendrical calendrical) {
            Integer domVal = calendrical.get(ISOChronology.dayOfMonthRule());
            DayOfWeek dow = calendrical.get(ISOChronology.dayOfWeekRule());
            if (dow != dayOfWeek || domVal == null) {
                return false;
            }
            return (domVal - 1) / 7 == ordinal - 1;
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

}
