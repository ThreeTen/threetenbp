/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.field;

import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMatcher;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeAdjuster;
import javax.time.calendar.UnsupportedRuleException;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * A representation of the half-day AM/PM value in the ISO-8601 calendar system.
 * <p>
 * AmPmOfDay is an enum that represents the half-day concepts of AM and PM.
 * AM is defined as from 00:00 to 11:59, while PM is defined from 12:00 to 23:59.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a AmPmOfDay instance.
 * Use getValue() instead.</b>
 * <p>
 * AmPmOfDay is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum AmPmOfDay
        implements Calendrical, TimeAdjuster, CalendricalMatcher {

    /**
     * The singleton instance for the morning, AM - ante meridiem.
     */
    AM(0),
    /**
     * The singleton instance for the afternoon, PM - post meridiem.
     */
    PM(1),
    ;

    /**
     * The AM/PM being represented.
     */
    private final int amPmOfDay;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the AM/PM field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the AM/PM rule, never null
     */
    public static DateTimeFieldRule<AmPmOfDay> rule() {
        return ISOChronology.amPmOfDayRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>AmPmOfDay</code>.
     *
     * @param amPmOfDay  the AM/PM value to represent, from 0 (AM) to 1 (PM)
     * @return the AmPmOfDay enum instance, never null
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public static AmPmOfDay amPmOfDay(int amPmOfDay) {
        switch (amPmOfDay) {
            case 0:
                return AM;
            case 1:
                return PM;
            default:
                throw new IllegalCalendarFieldValueException(rule(), amPmOfDay, 0, 1);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>AmPmOfDay</code> from a calendrical.
     * <p>
     * This can be used extract the AM/PM value directly from any implementation
     * of Calendrical, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the AmPmOfDay enum instance, never null
     * @throws UnsupportedRuleException if the AM/PM cannot be obtained
     */
    public static AmPmOfDay amPmOfDay(Calendrical calendrical) {
        return rule().getValueChecked(calendrical);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified AM/PM value.
     *
     * @param amPmOfDay  the AM/PM value to represent
     */
    private AmPmOfDay(int amPmOfDay) {
        this.amPmOfDay = amPmOfDay;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this instance then
     * <code>null</code> will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, this, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the AM/PM numeric value.
     *
     * @return the AM/PM value, from 0 (AM) to 1 (PM)
     */
    public int getValue() {
        return amPmOfDay;
    }

    /**
     * Gets the AM/PM value as short text.
     * <p>
     * In English, this will return text of the form 'AM' or 'PM'.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the long text value of the day of week, never null
     */
    public String getShortText(Locale locale) {
        return rule().getText(amPmOfDay, locale, TextStyle.SHORT);
    }

    /**
     * Gets the AM/PM value as text.
     * <p>
     * In English, this will return text of the form 'AM' or 'PM'.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the long text value of the day of week, never null
     */
    public String getText(Locale locale) {
        return rule().getText(amPmOfDay, locale, TextStyle.FULL);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance representing AM (ante-meridiem).
     *
     * @return true is this instance represents AM
     */
    public boolean isAm() {
        return (this == AM);
    }

    /**
     * Is this instance representing PM (post-meridiem).
     *
     * @return true is this instance represents PM
     */
    public boolean isPm() {
        return (this == PM);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the AM/PM extracted from the calendrical matches this.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        return this.equals(calendrical.get(rule()));
    }

    /**
     * Adjusts a time to have the the AM/PM value represented by this object,
     * returning a new time.
     * <p>
     * Only the AM/PM value is adjusted. The other date and time fields are
     * unaffected. Changing from AM to PM will effectively add 12 hours,
     * while changing from PM to AM will effectively subtract 12 hours.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param time  the time to be adjusted, not null
     * @return the adjusted time, never null
     */
    public LocalTime adjustTime(LocalTime time) {
        int hourOfDay = getValue() * 12 + time.toHourOfDay().getHourOfAmPm();
        return time.withHourOfDay(hourOfDay);
    }

}
