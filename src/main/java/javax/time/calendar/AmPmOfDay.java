/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Calendar;
import java.util.Locale;

import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * A half-day before or after midday, with the values 'AM' and 'PM'.
 * <p>
 * {@code AmPmOfDay} is an enum representing the half-day concepts of AM and PM.
 * AM is defined as from 00:00 to 11:59, while PM is defined from 12:00 to 23:59.
 * <p>
 * The calendrical framework requires date-time fields to have an {@code int} value.
 * The {@code int} value follows {@link Calendar}, assigning 0 to AM and 1 to PM.
 * It is recommended that applications use the enum rather than the {@code int} value
 * to ensure code clarity.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code AmPmOfDay}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * This enum represents a common concept that is found in many calendar systems.
 * As such, this enum may be used by any calendar system that has the AM/PM concept.
 * Note that the implementation of {@link DateTimeFieldRule} may vary by calendar system.
 * <p>
 * AmPmOfDay is an immutable and thread-safe enum.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum AmPmOfDay implements Calendrical {

    /**
     * The singleton instance for the morning, AM - ante meridiem.
     * This has the numeric value of {@code 0}.
     */
    AM,
    /**
     * The singleton instance for the afternoon, PM - post meridiem.
     * This has the numeric value of {@code 1}.
     */
    PM;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code AmPmOfDay} from an {@code int} value.
     * <p>
     * {@code AmPmOfDay} is an enum representing before and after midday.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows {@link Calendar}, assigning 0 to AM and 1 to PM.
     * <p>
     * An exception is thrown if the value is invalid. The exception uses the
     * {@link ISOChronology} AM/PM rule to indicate the failed rule.
     *
     * @param amPmOfDay  the AM/PM value to represent, from 0 (AM) to 1 (PM)
     * @return the AmPmOfDay singleton, never null
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public static AmPmOfDay of(int amPmOfDay) {
        switch (amPmOfDay) {
            case 0:
                return AM;
            case 1:
                return PM;
            default:
                throw new IllegalCalendarFieldValueException(ISOChronology.amPmOfDayRule(), amPmOfDay, 0, 1);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the AM/PM {@code int} value.
     * <p>
     * The values are numbered following {@link Calendar}, assigning 0 to AM and 1 to PM.
     *
     * @return the AM/PM value, from 0 (AM) to 1 (PM)
     */
    public int getValue() {
        return ordinal();
    }

    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This returns the one of the AM/PM values if the type of the rule
     * is {@code AmPmOfDay}. Other rules will return {@code null}.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        if (rule.getReifiedType() != AmPmOfDay.class) {
            return null;
        }
        return rule.reify(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the short textual representation of this AM/PM, such as 'AM' or 'PM'.
     * <p>
     * This method is notionally specific to {@link ISOChronology} as it uses
     * the AM/PM rule to obtain the text. However, it is expected that
     * the text will be equivalent for all AM/PM rules, thus this aspect
     * of the implementation should be irrelevant to applications.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the AM/PM, never null
     */
    public String getShortText(Locale locale) {
        return ISOChronology.amPmOfDayRule().getText(getValue(), locale, TextStyle.SHORT);
    }

    /**
     * Gets the full textual representation of this AM/PM, such as 'AM' or 'PM'.
     * <p>
     * This method is notionally specific to {@link ISOChronology} as it uses
     * the AM/PM rule to obtain the text. However, it is expected that
     * the text will be equivalent for all AM/PM rules, thus this aspect
     * of the implementation should be irrelevant to applications.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the long text value of the AM/PM, never null
     */
    public String getText(Locale locale) {
        return ISOChronology.amPmOfDayRule().getText(getValue(), locale, TextStyle.FULL);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance representing AM (ante-meridiem).
     *
     * @return true if this instance represents AM
     */
    public boolean isAm() {
        return (this == AM);
    }

    /**
     * Is this instance representing PM (post-meridiem).
     *
     * @return true if this instance represents PM
     */
    public boolean isPm() {
        return (this == PM);
    }

}
