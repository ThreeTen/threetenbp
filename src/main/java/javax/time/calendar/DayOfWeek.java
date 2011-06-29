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

import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;

import java.util.Locale;

import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * A day-of-week, such as 'Tuesday'.
 * <p>
 * {@code DayOfWeek} is an enum representing the 7 days of the week -
 * Monday, Tuesday, Wednesday, Thursday, Friday, Saturday and Sunday.
 * <p>
 * The calendrical framework requires date-time fields to have an {@code int} value.
 * The {@code int} value follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday).
 * It is recommended that applications use the enum rather than the {@code int} value
 * to ensure code clarity.
 * <p>
 * <b>Do not use {@code ordinal()} to obtain the numeric representation of {@code DayOfWeek}.
 * Use {@code getValue()} instead.</b>
 * <p>
 * This enum represents a common concept that is found in many calendar systems.
 * As such, this enum may be used by any calendar system that has the day-of-week
 * concept defined exactly equivalent to the ISO calendar system.
 * <p>
 * This is an immutable and thread-safe enum.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum DayOfWeek implements Calendrical {

    /**
     * The singleton instance for the day-of-week of Monday.
     * This has the numeric value of {@code 1}.
     */
    MONDAY,
    /**
     * The singleton instance for the day-of-week of Tuesday.
     * This has the numeric value of {@code 2}.
     */
    TUESDAY,
    /**
     * The singleton instance for the day-of-week of Wednesday.
     * This has the numeric value of {@code 3}.
     */
    WEDNESDAY,
    /**
     * The singleton instance for the day-of-week of Thursday.
     * This has the numeric value of {@code 4}.
     */
    THURSDAY,
    /**
     * The singleton instance for the day-of-week of Friday.
     * This has the numeric value of {@code 5}.
     */
    FRIDAY,
    /**
     * The singleton instance for the day-of-week of Saturday.
     * This has the numeric value of {@code 6}.
     */
    SATURDAY,
    /**
     * The singleton instance for the day-of-week of Sunday.
     * This has the numeric value of {@code 7}.
     */
    SUNDAY;
    /**
     * Private cache of all the constants.
     */
    private static final DayOfWeek[] ENUMS = DayOfWeek.values();

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code DayOfWeek}.
     * <p>
     * This rule is a calendrical rule base on {@code DayOfWeek}.
     * The equivalent date-time rule is {@link ISODateTimeRule#DAY_OF_WEEK}.
     *
     * @return the rule for the day-of-week, not null
     */
    public static CalendricalRule<DayOfWeek> rule() {
        return ExtendedCalendricalRule.DAY_OF_WEEK;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code DayOfWeek} from an {@code int} value.
     * <p>
     * {@code DayOfWeek} is an enum representing the 7 days of the week.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday).
     * <p>
     * An exception is thrown if the value is invalid.
     *
     * @param dayOfWeek  the day-of-week to represent, from 1 (Monday) to 7 (Sunday)
     * @return the DayOfWeek singleton, not null
     * @throws IllegalCalendarFieldValueException if the day-of-week is invalid
     */
    public static DayOfWeek of(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalCalendarFieldValueException(DAY_OF_WEEK, dayOfWeek);
        }
        return ENUMS[dayOfWeek - 1];
    }

    /**
     * Obtains an instance of {@code DayOfWeek} from a {@code Calendrical}.
     * <p>
     * {@code DayOfWeek} is an enum representing the 7 days of the week.
     * This factory allows the enum to be obtained from a {@code Calendrical},
     * for example {@code LocalDate} or {@code DateTimeField}.
     * <p>
     * An exception is thrown if the day-of-week cannot be obtained.
     *
     * @param calendrical  the calendrical to get the day-of-week from, not null
     * @return the DayOfWeek singleton, not null
     * @throws IllegalCalendarFieldValueException if the day-of-week is invalid
     */
    public static DayOfWeek of(Calendrical calendrical) {
        DateTimeField field = DAY_OF_WEEK.getValueChecked(calendrical);
        return of(field.getValidIntValue());
    }

    /**
     * Obtains an instance of {@code DayOfWeek} from the normalized form.
     * <p>
     * This internal method is used by the associated rule.
     *
     * @param normalized  the normalized calendrical, not null
     * @return the DayOfWeek singleton, null if unable to obtain
     */
    static DayOfWeek deriveFrom(CalendricalNormalizer merger) {
        DateTimeField field = merger.getFieldDerived(DAY_OF_WEEK, true);
        if (field == null) {
            return null;
        }
        return of(field.getValidIntValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This will only return a value for the {@link ISODateTimeRule#DAY_OF_WEEK}
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
        return CalendricalNormalizer.derive(ruleToDerive, rule(), null, toField());
    }

    /**
     * Gets the day-of-week {@code int} value.
     * <p>
     * The values are numbered following the ISO-8601 standard,
     * from 1 (Monday) to 7 (Sunday).
     *
     * @return the day-of-week, from 1 (Monday) to 7 (Sunday)
     */
    public int getValue() {
        return ordinal() + 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation, such as 'Mon' or 'Friday'.
     * <p>
     * This enum uses the {@link ISODateTimeRule#DAY_OF_WEEK} rule to obtain the text.
     * This allows the text to be localized by language, but not by chronology.
     * <p>
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the day-of-week, not null
     */
    public String getText(TextStyle style, Locale locale) {
        return DAY_OF_WEEK.getText(getValue(), style, locale);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next day-of-week.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the week. The next day after Sunday is Monday.
     *
     * @return the next day-of-week, not null
     */
    public DayOfWeek next() {
        return roll(1);
    }

    /**
     * Gets the previous day-of-week.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the week. The previous day before Monday is Sunday.
     *
     * @return the previous day-of-week, not null
     */
    public DayOfWeek previous() {
        return roll(-1);
    }

    /**
     * Rolls the day-of-week, adding the specified number of days.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the week from Sunday to Monday. The days to roll by may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to roll by, positive or negative
     * @return the resulting day-of-week, not null
     */
    public DayOfWeek roll(int days) {
        return values()[(ordinal() + (days % 7 + 7)) % 7];
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this day-of-week to an equivalent field.
     * <p>
     * The field is based on {@link ISODateTimeRule#DAY_OF_WEEK}.
     *
     * @return the equivalent day-of-week field, not null
     */
    public DateTimeField toField() {
        return DAY_OF_WEEK.field(getValue());
    }

}
