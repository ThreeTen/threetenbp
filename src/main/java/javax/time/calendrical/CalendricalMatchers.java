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
package javax.time.calendrical;

import static javax.time.calendrical.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendrical.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendrical.ISODateTimeRule.YEAR;

import javax.time.MonthOfYear;
import javax.time.Year;

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
                return yearVal != null && Year.isLeap(yearVal.getValue());
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
                        domVal.getValue() == MonthOfYear.of(moy.getValidIntValue()).getLastDayOfMonth(Year.isLeap(yearVal.getValue()));
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

}
