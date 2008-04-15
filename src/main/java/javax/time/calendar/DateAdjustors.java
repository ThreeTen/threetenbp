/*
 * Copyright (c) 2007, 2008, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.field.DayOfMonth.*;
import static javax.time.calendar.field.MonthOfYear.*;

import java.io.Serializable;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;

/**
 * Provides common implementations of <code>DateAdjustor</code>.
 * <p>
 * DateAdjustors is a utility class.
 * All adjustors returned are immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateAdjustors {
    /**
     * Private constructor since this is a utility class
     */
    private DateAdjustors() {
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the last day of month adjustor, which retuns a new date with
     * the day of month changed to be the last valid day of the month.
     * <p>
     * The input 2007-01-15 will return 2007-01-31.<br />
     * The input 2007-02-15 will return 2007-02-28.<br />
     * The input 2007-03-15 will return 2007-03-31.<br />
     * The input 2007-04-15 will return 2007-04-30.<br />
     * The input 2008-02-15 will return 2008-02-29.
     *
     * @return the last day of month adjustor, never null
     */
    public static DateAdjustor lastDayOfMonth() {
        return LastDayOfMonth.INSTANCE;
    }

    /**
     * Class implementing last day of month adjustor.
     */
    private static final class LastDayOfMonth implements DateAdjustor, Serializable {
        /** The singleton instance. */
        private static final DateAdjustor INSTANCE = new LastDayOfMonth();

        private Object readResolve() {
            return INSTANCE;
        }

        /** {@inheritDoc} */
        public LocalDate adjustDate(LocalDate date) {
            DayOfMonth dom = date.getMonthOfYear().getLastDayOfMonth(date.getYear());
            return dom.adjustDate(date);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the last day of year adjustor, which retuns a new date with
     * the day of year changed to be the last valid day of the year.
     * <p>
     * The input 2007-01-15 will return 2007-12-31.<br />
     * The input 2008-02-15 will return 2008-12-31.<br />
     *
     * @return the last day of year adjustor, never null
     */
    public static DateAdjustor lastDayOfYear() {
        return LastDayOfYear.INSTANCE;
    }

    /**
     * Class implementing last day of year adjustor.
     */
    private static final class LastDayOfYear implements DateAdjustor, Serializable {
        /** The singleton instance. */
        private static final DateAdjustor INSTANCE = new LastDayOfYear();

        private Object readResolve() {
            return INSTANCE;
        }

        /** {@inheritDoc} */
        public LocalDate adjustDate(LocalDate date) {
            return LocalDate.date(date.getYear(), DECEMBER, dayOfMonth(31));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the first in month adjustor, which retuns a new date
     * in the same month with the first matching day of week. This is used for
     * expressions like 'first tuesday in March'.
     * <p>
     * The input 2007-12-15 for (MONDAY) will return 2007-12-03.<br />
     * The input 2007-12-15 for (TUESDAY) will return 2007-12-04.<br />
     *
     * @param dayOfWeek  the day of week, not null
     * @return the first in month adjustor, never null
     */
    public static DateAdjustor firstInMonth(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            throw new NullPointerException("DayOfWeek must not be null");
        }
        return new DayOfWeekInMonth(1, dayOfWeek);
    }

    /**
     * Returns the day of week in month adjustor, which retuns a new date
     * in the same month with the ordinal day of week. This is used for
     * expressions like 'second tuesday in March'.
     * <p>
     * The input 2007-12-15 for (1,MONDAY) will return 2007-12-03.<br />
     * The input 2007-12-15 for (2,TUESDAY) will return 2007-12-11.<br />
     * The input 2007-12-15 for (3,TUESDAY) will return 2007-12-18.<br />
     * The input 2007-12-15 for (4,TUESDAY) will return 2007-12-25.<br />
     * The input 2007-12-15 for (5,TUESDAY) will return 2008-01-01.<br />
     * <p>
     * If the ordinal is 5 and there is no 5th of the requested day of week,
     * then the first of the next month is returned.
     *
     * @param ordinal  ordinal, from 1 to 5
     * @param dayOfWeek  the day of week, not null
     * @return the day of week in month adjustor, never null
     * @throws IllegalArgumentException if the ordinal is invalid
     */
    public static DateAdjustor dayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
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
     * Class implementing day of week in month adjustor.
     */
    private static final class DayOfWeekInMonth implements DateAdjustor, Serializable {
        /** The ordinal, from 1 to 5. */
        private final int ordinal;
        /** The day of week. */
        private final DayOfWeek dayOfWeek;

        /**
         * Constructor.
         * @param ordinal  ordinal, from 1 to 5
         * @param dayOfWeek  the day of week, not null
         */
        private DayOfWeekInMonth(int ordinal, DayOfWeek dayOfWeek) {
            super();
            this.ordinal = ordinal;
            this.dayOfWeek = dayOfWeek;
        }

        /** {@inheritDoc} */
        public LocalDate adjustDate(LocalDate date) {
            // TODO: Better algorithm
            LocalDate temp = date.withDayOfMonth(1);
            int curDow = temp.getDayOfWeek().ordinal();
            int newDow = dayOfWeek.ordinal();
            int dowDiff = (newDow - curDow + 7) % 7;
            dowDiff += (ordinal - 1) * 7;
            return temp.plusDays(dowDiff);
            
//            int curDow = date.getDayOfWeek().ordinal();
//            int newDow = dayOfWeek.ordinal();
////            int dowDiff = newDow - curDow;  // from -6 to 6
//            int curDom = date.getDayOfMonth().getValue() - 1;
////            int curDomMonday = curDom - curDow;
//            int curWeek = curDom / 7;
//            int weekDiff = ordinal - 1 - curWeek;  // from -4 to 4
//            
//            int curAlignedDow = curDom % 7;
//            int dowDiff = (newDow - curAlignedDow + 7) % 7;
//            
//            int days = weekDiff * 7 + dowDiff;
//            
//            System.out.println(curDow + " " + newDow + " " + curDom + " " + curWeek + " " +
//                    weekDiff + " " + curAlignedDow + " " + dowDiff);
//            
//            return date.plusDays(days);
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
     * Returns the next non weekend day adjustor, which adjusts the date one day
     * forward avoiding Saturday and Sunday.
     *
     * @return the next working day adjustor, never null
     */
    public static DateAdjustor nextNonWeekendDay() {
        return NextNonWeekendDay.INSTANCE;
    }

    /**
     * Class implementing next non weekend day adjustor.
     */
    private static class NextNonWeekendDay implements DateAdjustor, Serializable {
        /** The singleton instance. */
        private static final DateAdjustor INSTANCE = new NextNonWeekendDay();

        private Object readResolve() {
           return INSTANCE;
        }

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
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the next Monday adjustor, which adjusts the date to be the
     * next Monday after the specified date.
     *
     * @return the next Monday adjustor, never null
     */
    public static DateAdjustor nextMonday() {
        return null;  // TODO
    }

    /**
     * Returns the next day of week adjustor, which adjusts the date to be
     * the next of the specified day of week after the specified date.
     *
     * @param dow  the dow to move the date to, not null
     * @return the next day of week adjustor, never null
     */
    public static DateAdjustor next(DayOfWeek dow) {
        return null;  // TODO
    }

    /**
     * Returns the next or current day of week adjustor, which adjusts the
     * date to be be the next of the specified day of week, returning the
     * input date if the day of week matched.
     *
     * @param dow  the dow to move the date to, not null
     * @return the next day of week adjustor, never null
     */
    public static DateAdjustor nextOrCurrent(DayOfWeek dow) {
        return null;  // TODO
    }

}
