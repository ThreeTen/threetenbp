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
        public int[] resolveYMD(int year, int monthOfYear, int dayOfMonth) {
            ISOChronology.INSTANCE.yearRule().checkValue(year);
            ISOChronology.INSTANCE.monthOfYearRule().checkValue(monthOfYear);
            int len = MonthOfYear.monthOfYear(monthOfYear).lengthInDays(year);
            throw new IllegalCalendarFieldValueException("dayOfMonth", dayOfMonth, 1, len);
        }
        /** {@inheritDoc} */
        @Override
        public CalendricalState set(TimeFieldRule field, CalendricalState state, int value) {
            return null;
        }
        /** {@inheritDoc} */
        @Override
        public CalendricalState plus(TimeFieldRule field, CalendricalState state, int value) {
            return null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the previous valid day resolver, which adjusts the date to be
     * valid by moving to the last valid day of the month.
     *
     * @return the strict resolver, never null
     */
    public static CalendricalResolver previousValidDay() {
        return PreviousValidDay.INSTANCE;
    }

    /**
     * Class implementing previousValidDay resolver.
     */
    private static class PreviousValidDay extends CalendricalResolver {
        /** The singleton instance. */
        private static final CalendricalResolver INSTANCE = new PreviousValidDay();

        /** {@inheritDoc} */
        @Override
        public int[] resolveYMD(int year, int monthOfYear, int dayOfMonth) {
            ISOChronology.INSTANCE.yearRule().checkValue(year);
            ISOChronology.INSTANCE.monthOfYearRule().checkValue(monthOfYear);
            int len = MonthOfYear.monthOfYear(monthOfYear).lengthInDays(year);
            return new int[] {year, monthOfYear, len};
        }
        /** {@inheritDoc} */
        @Override
        public CalendricalState set(TimeFieldRule field, CalendricalState state, int value) {
            return null;
        }
        /** {@inheritDoc} */
        @Override
        public CalendricalState plus(TimeFieldRule field, CalendricalState state, int value) {
            return null;
        }
    }

}
