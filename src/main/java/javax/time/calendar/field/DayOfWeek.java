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
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.UnsupportedRuleException;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * A representation of a day of week in the ISO-8601 calendar system.
 * <p>
 * DayOfWeek is an immutable time field that can only store a day of week.
 * It is a type-safe way of representing a day of week in an application.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a DayOfWeek
 * instance. Use getValue() instead.</b>
 * <p>
 * DayOfWeek is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum DayOfWeek implements Calendrical, DateMatcher {

    /**
     * The singleton instance for the day of week of Monday.
     */
    MONDAY(1),
    /**
     * The singleton instance for the day of week of Tuesday.
     */
    TUESDAY(2),
    /**
     * The singleton instance for the day of week of Wednesday.
     */
    WEDNESDAY(3),
    /**
     * The singleton instance for the day of week of Thursday.
     */
    THURSDAY(4),
    /**
     * The singleton instance for the day of week of Friday.
     */
    FRIDAY(5),
    /**
     * The singleton instance for the day of week of Saturday.
     */
    SATURDAY(6),
    /**
     * The singleton instance for the day of week of Sunday.
     */
    SUNDAY(7),
    ;

    /**
     * The day of week being represented.
     */
    private final int dayOfWeek;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the day of week field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the day of week rule, never null
     */
    public static DateTimeFieldRule<DayOfWeek> rule() {
        return ISOChronology.dayOfWeekRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>DayOfWeek</code> from a value.
     * <p>
     * A day of week object represents one of the 7 days of the week. These
     * are numbered following the ISO-8601 standard, from 1 (Monday) to 7 (Sunday).
     * <p>
     * DayOfWeek is an enum, thus each instance is a singleton.
     * As a result, DayOfWeek instances can be compared using ==.
     *
     * @param dayOfWeek  the day of week to represent, from 1 (Monday) to 7 (Sunday)
     * @return the DayOfWeek singleton, never null
     * @throws IllegalCalendarFieldValueException if the day of week is invalid
     */
    public static DayOfWeek dayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return MONDAY;
            case 2:
                return TUESDAY;
            case 3:
                return WEDNESDAY;
            case 4:
                return THURSDAY;
            case 5:
                return FRIDAY;
            case 6:
                return SATURDAY;
            case 7:
                return SUNDAY;
            default:
                throw new IllegalCalendarFieldValueException(rule(), dayOfWeek, 1, 7);
        }
    }

    /**
     * Obtains an instance of <code>DayOfWeek</code> from a date.
     *
     * @param date  the date provider to use, not null
     * @return the DayOfWeek singleton, never null
     */
    public static DayOfWeek dayOfWeek(LocalDate date) {
        // TODO: should this code be elsewhere?
        long mjd = date.toModifiedJulianDays();
        if (mjd < 0) {
            long weeks = mjd / 7;
            mjd += (-weeks + 1) * 7;
        }
        int dow0 = (int) ((mjd + 2) % 7);
        return dayOfWeek(dow0 + 1);
    }

    /**
     * Obtains an instance of <code>DayOfWeek</code> from a calendrical.
     * <p>
     * This can be used extract a day of week object directly from any implementation
     * of Calendrical, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the DayOfWeek singleton, never null
     * @throws UnsupportedRuleException if the day of week cannot be obtained
     */
    public static DayOfWeek dayOfWeek(Calendrical calendrical) {
        return rule().getValueChecked(calendrical);
    }

    /**
     * Returns the <code>DayOfWeek</code> instance that corresponds to the first
     * day of week for a given <code>locale</code>.
     * <p>If there is no information for a locale, <code>MONDAY</code> is
     * returned.
     *
     * @param locale the locale to use, not null
     * @return the DayOfWeek singleton, never null
     */
    public static DayOfWeek firstDayOfWeekFor(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("Locale must not be null");
        }

        //TODO: Read it from resource bundle
        if (locale.equals(Locale.US) || (locale.getLanguage().equals("pt") &&
              locale.getCountry().equals("BR"))) {
            return SUNDAY;
        }

        return MONDAY;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified day of week.
     *
     * @param dayOfWeek  the day of week to represent
     */
    private DayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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
     * Gets the day of week value.
     *
     * @return the day of week, from 1 (Monday) to 7 (Sunday)
     */
    public int getValue() {
        return dayOfWeek;
    }

    /**
     * Gets the day of week value as short text.
     * <p>
     * In English, this will return text of the form 'Mon' or 'Fri'.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the day of week, never null
     */
    public String getShortText(Locale locale) {
        return rule().getText(dayOfWeek, locale, TextStyle.SHORT);
    }

    /**
     * Gets the day of week value as text.
     * <p>
     * In English, this will return text of the form 'Monday' or 'Friday'.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the long text value of the day of week, never null
     */
    public String getText(Locale locale) {
        return rule().getText(dayOfWeek, locale, TextStyle.FULL);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next day of week wrapping so that the next day of week
     * is always returned.
     *
     * @return the next day of week, never null
     */
    public DayOfWeek next() {
        return values()[(ordinal() + 1) % 7];
    }

    /**
     * Gets the previous day of week wrapping so that the previous day of week
     * is always returned.
     *
     * @return the previous day of week, never null
     */
    public DayOfWeek previous() {
        return values()[(ordinal() + 7 - 1) % 7];
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the DayOfWeek which is the specified number of days after
     * this DayOfWeek.
     * <p>
     * The calculation wraps around the end of the week from Sunday to Monday.
     * The days to add may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return the resulting DayOfWeek, never null
     */
    public DayOfWeek plusDays(int days) {
        return values()[(ordinal() + (days % 7)) % 7];
    }

    /**
     * Returns the DayOfWeek which is the specified number of days before
     * this DayOfWeek.
     * <p>
     * The calculation wraps around the start of the week from Monday to Sunday.
     * The days to subtract may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, positive or negative
     * @return the resulting DayOfWeek, never null
     */
    public DayOfWeek minusDays(int days) {
        return values()[(ordinal() + 7 - (days % 7)) % 7];
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the value of this day of week matches the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date matches, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return date.getDayOfWeek() == this;
    }

}
