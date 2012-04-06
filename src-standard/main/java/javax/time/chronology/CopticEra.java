/*
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chronology;

import java.util.Calendar;
import java.util.Locale;

import javax.time.calendrical.IllegalCalendarFieldValueException;
import javax.time.format.TextStyle;
import javax.time.i18n.CopticChronology;
import javax.time.i18n.HistoricChronology;

/**
 * An era in the Coptic calendar system.
 * <p>
 * The Coptic calendar system uses the 'Era of the Martyrs'.
 * The Coptic epoch {@code 0001-01-01 (Coptic)} is {@code 0284-08-29 (ISO)}.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code CopticEra}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * This is an immutable and thread-safe enum.
 *
 * @author Stephen Colebourne
 */
public enum CopticEra implements Era {

    /**
     * The singleton instance for the era BAM - 'Before Era of the Martyrs'.
     * This has the numeric value of {@code 0}.
     */
    BAM,
    /**
     * The singleton instance for the era EM - 'Era of the Martyrs'.
     * This has the numeric value of {@code 1}.
     */
    AM;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code HistoricEra} from an {@code int} value.
     * <p>
     * {@code HistoricEra} is an enum representing the historic eras of BCE/CE.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows {@link Calendar}, assigning 0 to BCE and 1 to CE.
     *
     * @param era  the BCE/CE value to represent, from 0 (BCE) to 1 (CE)
     * @return the HistoricEra singleton, not null
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public static CopticEra of(int era) {
        switch (era) {
            case 0:
                return BAM;
            case 1:
                return AM;
            default:
                throw new IllegalCalendarFieldValueException(CopticChronology.DAY_OF_MONTH, era);  // TODO
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the numeric era {@code int} value.
     * <p>
     * The era BAM has the value 0, while the era AM has the value 1.
     *
     * @return the era value, from 0 (BAM) to 1 (AM)
     */
    public int getValue() {
        return ordinal();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation, such as 'BEM' or 'EM'.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the month-of-year, not null
     */
//    public String getText(TextStyle style, Locale locale) {
//        // TODO
//        return HistoricChronology.standardCutover().eraRule().getText(getValue(), style, locale);
//    }

}
