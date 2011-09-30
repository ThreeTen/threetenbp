/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.YEAR;

import java.io.Serializable;

/**
 * Provides common implementations of {@code CalendricalMatcher}.
 * <p>
 * These matchers are useful and common implementations of {@link CalendricalMatcher}.
 * A matcher allows any type of matching to be performed against a calendrical.
 * Examples might be checking of the calendrical represents Friday the Thirteenth,
 * or the last day of the month, or one of the American continent time-zones.
 * All the implemented matchers depend on the ISO calendar system.
 * <p>
 * This is a thread-safe utility class.
 * All matchers returned are immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class CalendricalMatchers {

    /**
     * Private constructor since this is a utility class.
     */
    private CalendricalMatchers() {
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the leap year matcher, which returns true if the date
     * is in a leap year.
     * <p>
     * The input 2011-01-15 will return false.<br />
     * The input 2012-01-15 will return true (leap year).
     *
     * @return the leap year matcher, not null
     */
    public static CalendricalMatcher leapYear() {
        return Impl.LEAP_YEAR;
    }

    /**
     * Returns the leap day matcher, which returns true if the date
     * is February 29th in a leap year.
     * <p>
     * The input 2011-02-27 will return false.<br />
     * The input 2011-02-28 will return false.<br />
     * The input 2012-02-28 will return false (leap year).<br />
     * The input 2012-02-29 will return true (leap year).
     *
     * @return the leap day matcher, not null
     */
    public static CalendricalMatcher leapDay() {
        return Impl.LEAP_DAY;
    }

    /**
     * Returns the last day-of-month matcher, which returns true if the date
     * is the last valid day of the month.
     * <p>
     * The input 2011-02-27 will return false.<br />
     * The input 2011-02-28 will return true.<br />
     * The input 2012-02-28 will return false (leap year).<br />
     * The input 2012-02-29 will return true (leap year).
     *
     * @return the last day-of-month matcher, not null
     */
    public static CalendricalMatcher lastDayOfMonth() {
        return Impl.LAST_DAY_OF_MONTH;
    }

    /**
     * Returns the last day-of-year matcher, which returns true if the date is
     * the last valid day of the year.
     * <p>
     * The input 2011-12-30 will return false.<br />
     * The input 2011-12-31 will return true.
     *
     * @return the last day-of-year matcher, not null
     */
    public static CalendricalMatcher lastDayOfYear() {
        return Impl.LAST_DAY_OF_YEAR;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the matchers.
     */
    private static enum Impl implements CalendricalMatcher {
        /** Leap year matcher. */
        LEAP_YEAR {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                DateTimeField yearVal = calendrical.get(YEAR);
                return yearVal != null && ISOChronology.isLeapYear(yearVal.getValue());
            }
        },
        /** Leap day matcher. */
        LEAP_DAY {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                DateTimeField moyVal = calendrical.get(MONTH_OF_YEAR);
                DateTimeField domVal = calendrical.get(DAY_OF_MONTH);
                return domVal != null && moyVal != null && domVal.getValue() == 29 && moyVal.getValue() == 2;
            }
        },
        /** Last day-of-month matcher. */
        LAST_DAY_OF_MONTH {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                DateTimeField yearVal = calendrical.get(YEAR);
                DateTimeField moy = calendrical.get(MONTH_OF_YEAR);
                DateTimeField domVal = calendrical.get(DAY_OF_MONTH);
                return yearVal != null && moy != null && domVal != null && moy.isValidValue() &&
                        domVal.getValue() == MonthOfYear.of(moy.getValidIntValue()).getLastDayOfMonth(ISOChronology.isLeapYear(yearVal.getValue()));
            }
        },
        /** Last day-of-year matcher. */
        LAST_DAY_OF_YEAR {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                DateTimeField moyVal = calendrical.get(MONTH_OF_YEAR);
                DateTimeField domVal = calendrical.get(DAY_OF_MONTH);
                return domVal != null && moyVal != null && domVal.getValue() == 31 && moyVal.getValue() == 12;
            }
        },
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the first in month matcher, which returns true if the date
     * is the first occurrence of day-of-week in the month.
     *
     * @param dayOfWeek  the day-of-week, not null
     * @return the first in month matcher, not null
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
     * This is used for expressions like the 'second Tuesday in March'.
     *
     * @param ordinal  ordinal, from 1 to 5
     * @param dayOfWeek  the day-of-week, not null
     * @return the day-of-week in month matcher, not null
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
        public boolean matchesCalendrical(Calendrical calendrical) {
            DateTimeField domVal = calendrical.get(DAY_OF_MONTH);
            DateTimeField dowVal = calendrical.get(DAY_OF_WEEK);
            if (dowVal == null || domVal == null || dowVal.getValue() != dayOfWeek.getValue()) {
                return false;
            }
            return (domVal.getValidIntValue() - 1) / 7 == ordinal - 1;
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
