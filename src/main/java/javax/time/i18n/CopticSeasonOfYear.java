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

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.calendar.TimeFieldRule;

/**
 * A time field representing a Coptic season of year.
 * <p>
 * CopticSeasonOfYear is an immutable time field that can only store a Coptic season of year.
 * It is a type-safe way of representing a Coptic season of year in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a CopticSeasonOfYear
 * instance. Use getCopticSeasonOfYear() instead.</b>
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
                throw new IllegalArgumentException("CopticSeasonOfYear cannot have the value " + copticSeasonOfYear);
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
    public int getCopticSeasonOfYear() {
        return copticSeasonOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * CopticSeasonOfYear instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
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
     * Is this Coptic season of year instance greater than the specified Coptic season of year.
     *
     * @param otherCopticSeasonOfYear  the other Coptic season of year instance, not null
     * @return true if this Coptic season of year is greater
     * @throws NullPointerException if otherCopticSeasonOfYear is null
     */
    public boolean isGreaterThan(CopticSeasonOfYear otherCopticSeasonOfYear) {
        return compareTo(otherCopticSeasonOfYear) > 0;
    }

    /**
     * Is this Coptic season of year instance less than the specified Coptic season of year.
     *
     * @param otherCopticSeasonOfYear  the other Coptic season of year instance, not null
     * @return true if this Coptic season of year is less
     * @throws NullPointerException if otherCopticSeasonOfYear is null
     */
    public boolean isLessThan(CopticSeasonOfYear otherCopticSeasonOfYear) {
        return compareTo(otherCopticSeasonOfYear) < 0;
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
