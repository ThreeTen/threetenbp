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

import javax.time.calendar.field.MonthOfYear;

/**
 * Internal state class providing calendrical information.
 * <p>
 * CalendricalResolvers is a utility class.
 * All resolvers returned are immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public class CalendricalResolvers {

    //-----------------------------------------------------------------------
    /**
     * Returns the strict resolver which does not manipulate the state
     * in any way, resulting in an exception for all invalid values.
     *
     * @return the strict resolver, never null
     */
    public static CalendricalResolver strict() {
        return Strict.INSTANCE;
    }

    /**
     * Class implementing strict resolver.
     */
    private static class Strict extends CalendricalResolver {
        /** The singleton instance. */
        private static final CalendricalResolver INSTANCE = new Strict();

        /** {@inheritDoc} */
        @Override
        int[] doResolveDate(int year, int monthOfYear, int dayOfMonth) {
            ISOChronology.INSTANCE.checkValidDate(year, monthOfYear, dayOfMonth);
            return new int[] {year, monthOfYear, dayOfMonth};
        }
        /** {@inheritDoc} */
        @Override
        protected int[] handleResolveDate(int year, int monthOfYear, int dayOfMonth) {
            return null;  // never called
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the previous valid day resolver, which adjusts the date to be
     * valid by moving to the last valid day of the month.
     *
     * @return the previous valid day resolver, never null
     */
    public static CalendricalResolver previousValid() {
        return PreviousValid.INSTANCE;
    }

    /**
     * Class implementing previousValid resolver.
     */
    private static class PreviousValid extends CalendricalResolver {
        /** The singleton instance. */
        private static final CalendricalResolver INSTANCE = new PreviousValid();

        /** {@inheritDoc} */
        @Override
        int[] doResolveDate(int year, int monthOfYear, int dayOfMonth) {
            ISOChronology.INSTANCE.yearRule().checkValue(year);
            ISOChronology.INSTANCE.monthOfYearRule().checkValue(monthOfYear);
            ISOChronology.INSTANCE.dayOfMonthRule().checkValue(dayOfMonth);
            int len = MonthOfYear.monthOfYear(monthOfYear).lengthInDays(year);
            if (dayOfMonth > len) {
                return new int[] {year, monthOfYear, len};
            }
            return new int[] {year, monthOfYear, dayOfMonth};
        }
        /** {@inheritDoc} */
        @Override
        protected int[] handleResolveDate(int year, int monthOfYear, int dayOfMonth) {
            return null;  // never called
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the next valid day resolver, which adjusts the date to be
     * valid by moving to the first of the next month.
     *
     * @return the next valid day resolver, never null
     */
    public static CalendricalResolver nextValid() {
        return NextValid.INSTANCE;
    }

    /**
     * Class implementing nextValid resolver.
     */
    private static class NextValid extends CalendricalResolver {
        /** The singleton instance. */
        private static final CalendricalResolver INSTANCE = new NextValid();

        /** {@inheritDoc} */
        @Override
        int[] doResolveDate(int year, int monthOfYear, int dayOfMonth) {
            ISOChronology.INSTANCE.yearRule().checkValue(year);
            ISOChronology.INSTANCE.monthOfYearRule().checkValue(monthOfYear);
            ISOChronology.INSTANCE.dayOfMonthRule().checkValue(dayOfMonth);
            int len = MonthOfYear.monthOfYear(monthOfYear).lengthInDays(year);
            if (dayOfMonth > len) {
                if (monthOfYear == 12) {
                    ISOChronology.INSTANCE.yearRule().checkValue(++year);
                    return new int[] {year, 1, 1};
                }
                return new int[] {year, monthOfYear + 1, 1};
            }
            return new int[] {year, monthOfYear, dayOfMonth};
        }
        /** {@inheritDoc} */
        @Override
        protected int[] handleResolveDate(int year, int monthOfYear, int dayOfMonth) {
            return null;  // never called
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the part lenient resolver, which adjusts the date to be
     * valid by moving it to the next month by the number of days that
     * are invalid up to the 31st of the month.
     *
     * @return the part lenient resolver, never null
     */
    public static CalendricalResolver partLenient() {
        return PartLenient.INSTANCE;
    }

    /**
     * Class implementing partLenient resolver.
     */
    private static class PartLenient extends CalendricalResolver {
        /** The singleton instance. */
        private static final CalendricalResolver INSTANCE = new PartLenient();

        /** {@inheritDoc} */
        @Override
        int[] doResolveDate(int year, int monthOfYear, int dayOfMonth) {
            ISOChronology.INSTANCE.yearRule().checkValue(year);
            ISOChronology.INSTANCE.monthOfYearRule().checkValue(monthOfYear);
            ISOChronology.INSTANCE.dayOfMonthRule().checkValue(dayOfMonth);
            int len = MonthOfYear.monthOfYear(monthOfYear).lengthInDays(year);
            if (dayOfMonth > len) {
                if (monthOfYear == 12) {
                    ISOChronology.INSTANCE.yearRule().checkValue(++year);
                    return new int[] {year, 1, dayOfMonth - len};
                }
                return new int[] {year, monthOfYear + 1, dayOfMonth - len};
            }
            return new int[] {year, monthOfYear, dayOfMonth};
        }
        /** {@inheritDoc} */
        @Override
        protected int[] handleResolveDate(int year, int monthOfYear, int dayOfMonth) {
            return null;  // never called
        }
    }

}
