/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Calendar;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * An era in the historic calendar system, with the values 'BCE' and 'CE'.
 * <p>
 * {@code HistoricEra} is an enum representing the historic era concepts of BCE and CE.
 * BCE is defined as the time-line before historic year 1, while CE is defined as
 * the time-line from year 1 onwards.
 * <p>
 * The calendrical framework requires date-time fields to have an {@code int} value.
 * The {@code int} value follows {@link Calendar}, assigning 0 to BCE and 1 to CE.
 * It is recommended that applications use the enum rather than the {@code int} value
 * to ensure code clarity.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code HistoricEra}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * HistoricEra is an immutable and thread-safe enum.
 *
 * @author Stephen Colebourne
 */
public enum HistoricEra implements Calendrical {

    /**
     * The singleton instance for the era BCE - Before Common Era.
     * This has the numeric value of {@code 0}.
     */
    BCE,
    /**
     * The singleton instance for the era CE - Common Era.
     * This has the numeric value of {@code 1}.
     */
    CE;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code HistoricEra} from an {@code int} value.
     * <p>
     * {@code HistoricEra} is an enum representing the historic eras of BCE/CE.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows {@link Calendar}, assigning 0 to BCE and 1 to CE.
     * <p>
     * An exception is thrown if the value is invalid. The exception uses the
     * {@link ISOChronology} BCE/CE rule to indicate the failed rule.
     *
     * @param era  the BCE/CE value to represent, from 0 (BCE) to 1 (CE)
     * @return the HistoricEra singleton, never null
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public static HistoricEra of(int era) {
        switch (era) {
            case 0:
                return BCE;
            case 1:
                return CE;
            default:
                throw new IllegalCalendarFieldValueException(HistoricChronology.standardCutover().eraRule(), era, 0, 1);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the BCE/CE {@code int} value.
     * <p>
     * The values are numbered following {@link Calendar}, assigning 0 to BCE and 1 to CE.
     *
     * @return the BCE/CE value, from 0 (BCE) to 1 (CE)
     */
    public int getValue() {
        return ordinal();
    }

    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This returns the one of the era values if the type of the rule
     * is {@code HistoricEra}. Other rules will return {@code null}.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        if (rule.getReifiedType() != HistoricEra.class) {
            return null;
        }
        return rule.reify(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the short textual representation of this BCE/CE, such as 'BCE' or 'CE'.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the BCE/CE, never null
     */
    public String getShortText(Locale locale) {
        return HistoricChronology.standardCutover().eraRule().getText(getValue(), locale, TextStyle.SHORT);
    }

    /**
     * Gets the short textual representation of this day-of-week, such as 'BCE' or 'CE'.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the long text value of the BCE/CE, never null
     */
    public String getText(Locale locale) {
        return HistoricChronology.standardCutover().eraRule().getText(getValue(), locale, TextStyle.FULL);
    }

}
