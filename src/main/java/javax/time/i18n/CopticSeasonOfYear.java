/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.Calendrical;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.TimeFieldRule;
import javax.time.calendar.format.FlexiDateTime;

/**
 * A calendrical representation of a Coptic season of year.
 * <p>
 * CopticSeasonOfYear is an immutable time field that can only store a Coptic season of year.
 * It is a type-safe way of representing a Coptic season of year in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a CopticSeasonOfYear
 * instance. Use getValue() instead.</b>
 * <p>
 * CopticSeasonOfYear is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum CopticSeasonOfYear implements Calendrical {

    /**
     * The singleton instance for the first season Akhet, the season of innundation (floods of the River Nile).
     */
    AKHET(1),
    /**
     * The singleton instance for the second season Proyet, the season of growth.
     */
    PROYET(2),
    /**
     * The singleton instance for the third season Shomu, the season of harvest.
     */
    SHOMU(3),
    ;
    /**
     * The rule implementation that defines how the Coptic season of year field operates.
     */
    public static final TimeFieldRule RULE = new Rule();

    /**
     * The Coptic season of year being represented.
     */
    private final int copticSeasonOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CopticSeasonOfYear</code>.
     *
     * @param copticSeasonOfYear  the Coptic season of year to represent
     * @return the existing CopticSeasonOfYear
     * @throws IllegalCalendarFieldValueException if the Coptic season of year is invalid
     */
    public static CopticSeasonOfYear copticSeasonOfYear(int copticSeasonOfYear) {
        switch (copticSeasonOfYear) {
            case 1:
                return AKHET;
            case 2:
                return PROYET;
            case 3:
                return SHOMU;
            default:
                throw new IllegalCalendarFieldValueException("CopticSeasonOfYear cannot have the value " + copticSeasonOfYear);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified Coptic season of year.
     *
     * @param copticSeasonOfYear  the Coptic season of year to represent
     */
    private CopticSeasonOfYear(int copticSeasonOfYear) {
        this.copticSeasonOfYear = copticSeasonOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the Coptic season of year value.
     *
     * @return the Coptic season of year
     */
    public int getValue() {
        return copticSeasonOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a <code>FlexiDateTime</code>.
     *
     * @return the flexible date-time representation for this instance, never null
     */
    public FlexiDateTime toFlexiDateTime() {
        return new FlexiDateTime(RULE, getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next Coptic season of year wrapping so that the next Coptic season of year
     * is always returned.
     *
     * @return the next Coptic season of year, never null
     */
    public CopticSeasonOfYear next() {
        return values()[(ordinal() + 1) % 3];
    }

    /**
     * Gets the previous Coptic season of year wrapping so that the previous Coptic season of year
     * is always returned.
     *
     * @return the previous Coptic season of year, never null
     */
    public CopticSeasonOfYear previous() {
        return values()[(ordinal() + 3 - 1) % 3];
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the Coptic season of year field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("CopticSeasonOfYear", null, null, 1, 3);
        }
    }

}
