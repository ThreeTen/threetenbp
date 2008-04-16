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
 * A calendrical representation of a Coptic month of year.
 * <p>
 * CopticMonthOfYear is an immutable time field that can only store a Coptic month of year.
 * It is a type-safe way of representing a Coptic month of year in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a CopticMonthOfYear
 * instance. Use getValue() instead.</b>
 * <p>
 * CopticMonthOfYear is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum CopticMonthOfYear implements Calendrical {

    /**
     * The singleton instance for the month of Thout.
     */
    THOUT(1),
    /**
     * The singleton instance for the month of Paopi.
     */
    PAOPI(2),
    /**
     * The singleton instance for the month of Hathor.
     */
    HATHOR(3),
    /**
     * The singleton instance for the month of Koiak.
     */
    KOIAK(4),
    /**
     * The singleton instance for the month of Tobi.
     */
    TOBI(5),
    /**
     * The singleton instance for the month of Meshir.
     */
    MESHIR(6),
    /**
     * The singleton instance for the month of Paremhat.
     */
    PAREMHAT(7),
    /**
     * The singleton instance for the month of Paremoude.
     */
    PAREMOUDE(8),
    /**
     * The singleton instance for the month of Pashons.
     */
    PASHONS(9),
    /**
     * The singleton instance for the month of Paoni.
     */
    PAONI(10),
    /**
     * The singleton instance for the month of Epip.
     */
    EPIP(11),
    /**
     * The singleton instance for the month of Mesori.
     */
    MESORI(12),
    /**
     * The singleton instance for the month of Pi Kogi Enavot.
     */
    PI_KOGI_ENAVOT(13),
    ;
    /**
     * The rule implementation that defines how the Coptic month of year field operates.
     */
    public static final TimeFieldRule RULE = new Rule();

    /**
     * The Coptic month of year being represented.
     */
    private final int copticMonthOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CopticMonthOfYear</code>.
     *
     * @param copticMonthOfYear  the Coptic month of year to represent
     * @return the existing CopticMonthOfYear
     * @throws IllegalCalendarFieldValueException if the Coptic month of year is invalid
     */
    public static CopticMonthOfYear copticMonthOfYear(int copticMonthOfYear) {
        switch (copticMonthOfYear) {
            case 1:
                return THOUT;
            case 2:
                return PAOPI;
            case 3:
                return HATHOR;
            case 4:
                return KOIAK;
            case 5:
                return TOBI;
            case 6:
                return MESHIR;
            case 7:
                return PAREMHAT;
            case 8:
                return PAREMOUDE;
            case 9:
                return PASHONS;
            case 10:
                return PAONI;
            case 11:
                return EPIP;
            case 12:
                return MESORI;
            case 13:
                return PI_KOGI_ENAVOT;
            default:
                throw new IllegalCalendarFieldValueException("CopticMonthOfYear cannot have the value " + copticMonthOfYear);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified Coptic month of year.
     *
     * @param copticMonthOfYear  the Coptic month of year to represent
     */
    private CopticMonthOfYear(int copticMonthOfYear) {
        this.copticMonthOfYear = copticMonthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the Coptic month of year value.
     *
     * @return the Coptic month of year
     */
    public int getValue() {
        return copticMonthOfYear;
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
     * Gets the next Coptic month of year wrapping so that the next Coptic month of year
     * is always returned.
     *
     * @return the next Coptic month of year, never null
     */
    public CopticMonthOfYear next() {
        return values()[(ordinal() + 1) % 13];
    }

    /**
     * Gets the previous Coptic month of year wrapping so that the previous Coptic month of year
     * is always returned.
     *
     * @return the previous Coptic month of year, never null
     */
    public CopticMonthOfYear previous() {
        return values()[(ordinal() + 13 - 1) % 13];
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this month in days.
     *
     * @param year  the year to obtain the length for
     * @return the length of this month in days
     */
    public int length(int year) {
        return (this == PI_KOGI_ENAVOT ?
                    (CopticChronology.INSTANCE.isLeapYear(year) ? 6 : 5) : 30);
    }

    /**
     * Gets the maximum length of this month in days.
     *
     * @return the maximum length of this month in days
     */
    public int maxLength() {
        return (this == PI_KOGI_ENAVOT ? 6 : 30);
    }

    /**
     * Gets the minimum length of this month in days.
     *
     * @return the minimum length of this month in days
     */
    public int minLength() {
        return (this == PI_KOGI_ENAVOT ? 5 : 30);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the Coptic season.
     *
     * @return the Coptic season, never null
     */
    public CopticSeasonOfYear getSeasonOfYear() {
        if (ordinal() < 4) {
            return CopticSeasonOfYear.AKHET;
        } else if (ordinal() < 8) {
            return CopticSeasonOfYear.PROYET;
        } else {
            return CopticSeasonOfYear.SHOMU;
        }
    }

    /**
     * Gets the index of the month within the Coptic season.
     * <p>
     * The month of season field is indexed from 1 to 5.
     * Akhet and Proyet have 4 months, while Shomu has 5.
     *
     * @return the month of season, from 1 to 5
     */
    public int getMonthOfSeason() {
        if (this == PI_KOGI_ENAVOT) {
            return 5;
        }
        return (ordinal() % 4) + 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the rules for the Coptic month of year field.
     */
    private static class Rule extends TimeFieldRule {

        /** Constructor. */
        protected Rule() {
            super("CopticMonthOfYear", null, null, 1, 13);
        }
    }

}
