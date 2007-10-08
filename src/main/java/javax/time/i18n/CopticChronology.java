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
package javax.time.i18n;

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.calendar.TimeFieldRule;
import javax.time.duration.Durational;
import javax.time.duration.Durations;

/**
 * The Coptic calendar system.
 * <p>
 * CopticChronology defines the rules of the Coptic calendar system.
 * <p>
 * CopticDate is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CopticChronology implements Serializable {

    /**
     * The singleton instance of <code>CopticChronology</code>.
     */
    public static final CopticChronology INSTANCE = new CopticChronology();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private CopticChronology() {
    }

    /**
     * Resolves singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     *
     * @param year  the year to check, from 1 to MAX_VALUE
     * @return true if the year is a leap year
     */
    public boolean isLeapYear(int year) {
        return ((year % 4) == 3);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, never null
     */
    public String getName() {
        return "Coptic";
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the year field.
     *
     * @return the rule for the year field, never null
     */
    public TimeFieldRule yearRule() {
        return YEAR_RULE;
    }

    /**
     * Year rule class.
     *
     * @author scolebourne
     */
    private static final TimeFieldRule YEAR_RULE = new TimeFieldRule("Year", Durations.YEARS, null, 1, Integer.MAX_VALUE) {
        @Override
        public int getValue(Durational epochDuration) {
            // return extract(epochDuration, Durations.YEARS) + 1;
            Integer months = epochDuration.getDurationalMap().get(Durations.MONTHS);
            return months / 13 + 1;
        }
        public int getValue(long epochSeconds) {
            long epochDays = epochSeconds / (24 * 60 * 60);
            return MathUtils.safeToInt((epochDays * 4 + 1463) / 1461);
        }
    };

    /**
     * Gets the rule for the month of year field.
     *
     * @return the rule for the month of year field, never null
     */
    public TimeFieldRule monthOfYearRule() {
        return null;
    }

    /**
     * Gets the rule for the day of year field.
     *
     * @return the rule for the day of year field, never null
     */
    public TimeFieldRule dayOfYearRule() {
        return null;
    }

    /**
     * Gets the rule for the day of month field.
     *
     * @return the rule for the day of month field, never null
     */
    public TimeFieldRule dayOfMonthRule() {
        return null;
    }

    /**
     * Gets the rule for the day of week field.
     *
     * @return the rule for the day of week field, never null
     */
    public TimeFieldRule dayOfWeekRule() {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * A debugging description of this class.
     *
     * @return a string form for debugging, never null
     */
    @Override
    public String toString() {
        return "CopticChronology";
    }

}
