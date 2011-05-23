/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;

/**
 * A helper class for rules around weekends.
 *
 * @author Stephen Colebourne
 */
public final class WeekendRules {

    /**
     * Restricted constructor.
     */
    private WeekendRules() {
    }

    /**
     * Returns the next non weekend day adjuster, which adjusts the date one day
     * forward skipping Saturday and Sunday.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however an adjuster
     * can be easily written to do so.
     *
     * @return the next working day adjuster, not null
     */
    public static DateAdjuster nextNonWeekendDay() {
        return Adjuster.NEXT_NON_WEEKEND;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the adjusters.
     */
    private static enum Adjuster implements DateAdjuster {
        /** Next non weekend day adjuster. */
        NEXT_NON_WEEKEND {
            /** {@inheritDoc} */
            public LocalDate adjustDate(LocalDate date) {
                DayOfWeek dow = date.getDayOfWeek();
                switch (dow) {
                    case SATURDAY:
                        return date.plusDays(2);
                    case FRIDAY:
                        return date.plusDays(3);
                    default:
                        return date.plusDays(1);
                }
            }
        },
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the weekend day matcher, which returns true if the date
     * is Saturday or Sunday.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however a matcher
     * can be easily written to do so.
     *
     * @return the weekend day matcher, not null
     */
    public static CalendricalMatcher weekendDay() {
        return Matcher.WEEKEND_DAY;
    }

    /**
     * Returns the non weekend day matcher, which returns true if the date
     * is between Monday and Friday inclusive.
     * <p>
     * Some territories have weekends that do not consist of Saturday and Sunday.
     * No implementation is supplied to support this, however a matcher
     * can be easily written to do so.
     *
     * @return the non weekend day matcher, not null
     */
    public static CalendricalMatcher nonWeekendDay() {
        return Matcher.NON_WEEKEND_DAY;
    }

    //-----------------------------------------------------------------------
    /**
     * Enum implementing the matchers.
     */
    private static enum Matcher implements CalendricalMatcher {
        /** Non weekend matcher. */
        WEEKEND_DAY {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                DateTimeField dow = calendrical.get(DAY_OF_WEEK);
                return dow != null && (dow.getValue() == 6 || dow.getValue() == 7);
            }
        },
        /** Non weekend matcher. */
        NON_WEEKEND_DAY {
            /** {@inheritDoc} */
            public boolean matchesCalendrical(Calendrical calendrical) {
                DateTimeField dow = calendrical.get(DAY_OF_WEEK);
                return dow != null && dow.getValue() >= 1 && dow.getValue() <= 5;
            }
        },
    }

}
