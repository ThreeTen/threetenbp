/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.AMPM_OF_DAY;

import java.util.Calendar;
import java.util.Locale;

import javax.time.CalendricalException;
import javax.time.calendar.format.TextStyle;

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
 * As such, this enum may be used by any calendar system that has the month-of-year
 * concept defined exactly equivalent to the ISO calendar system.
 * <p>
 * This is an immutable and thread-safe enum.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum AmPmOfDay implements Calendrical, CalendricalMatcher {

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
     * Gets the rule for {@code AmPmOfDay}.
     * <p>
     * This rule is a calendrical rule based on {@code AmPmOfDay}.
     * The equivalent date-time rule is {@link ISODateTimeRule#AMPM_OF_DAY}.
     *
     * @return the rule for the am-pm-of-day, not null
     */
    public static CalendricalRule<AmPmOfDay> rule() {
        return ExtendedCalendricalRule.AM_PM_OF_DAY;
    }

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
     * @return the AM/PM, not null
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public static AmPmOfDay of(int amPmOfDay) {
        switch (amPmOfDay) {
            case 0:
                return AM;
            case 1:
                return PM;
            default:
                throw new IllegalCalendarFieldValueException(AMPM_OF_DAY, amPmOfDay);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code AmPmOfDay} from a set of calendricals.
     * <p>
     * A calendrical represents some form of date and time information.
     * This method combines the input calendricals into AM/PM.
     *
     * @param calendricals  the calendricals to create AM/PM from, no nulls, not null
     * @return the AM/PM, not null
     * @throws CalendricalException if unable to merge to AM/PM
     */
    public static AmPmOfDay from(Calendrical... calendricals) {
        return CalendricalEngine.merge(calendricals).deriveChecked(rule());
    }

    /**
     * Obtains an instance of {@code AmPmOfDay} from the engine.
     * <p>
     * This internal method is used by the associated rule.
     *
     * @param engine  the calendrical engine, not null
     * @return the AmPmOfDay singleton, null if unable to obtain
     */
    static AmPmOfDay deriveFrom(CalendricalEngine engine) {
        DateTimeField field = engine.getFieldDerived(AMPM_OF_DAY, true);
        if (field == null) {
            return null;
        }
        return of(field.getValidIntValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This will only return a value for the {@link ISODateTimeRule#AMPM_OF_DAY}
     * rule, or something derivable from it.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        if (ruleToDerive == rule()) {
            return (T) this;
        }
        return CalendricalEngine.derive(ruleToDerive, rule(), null, toField());
    }

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

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation, such as 'AM' or 'PM'.
     * <p>
     * This method is notionally specific to {@link ISOChronology} as it uses
     * the AM/PM rule to obtain the text. However, it is expected that
     * the text will be equivalent for all AM/PM rules, thus this aspect
     * of the implementation should be irrelevant to applications.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the AM/PM, not null
     */
    public String getText(TextStyle style, Locale locale) {
        return AMPM_OF_DAY.getText(getValue(), style, locale);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the AM/PM extracted from the calendrical matches this.
     * <p>
     * This method implements the {@code CalendricalMatcher} interface.
     * It is intended that applications use {@link LocalDate#matches} rather than this method.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    @Override
    public boolean matchesCalendrical(Calendrical calendrical) {
        return this.equals(calendrical.get(rule()));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this AM/PM to an equivalent field.
     * <p>
     * The field is based on {@link ISODateTimeRule#AMPM_OF_DAY}.
     *
     * @return the equivalent AM/PM field, not null
     */
    public DateTimeField toField() {
        return AMPM_OF_DAY.field(getValue());
    }

}
