/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Rules defining how weeks are counted.
 * <p>
 * A standard week is seven days long, but cultures have different rules for some
 * other aspects of a week.
 * <ul>
 * <li>The first day-of-week.
 * For example, the ISO-8601 standard considers Monday to be the first day-of-week.
 * <li>The minimal number of days in the first week.
 * For example, the ISO08601 standard counts the first week as the one with 4 days.
 * </ul>
 * Together these two values allow the first week of the month or year to be calculated.
 * Within any month or year, the first week is the earliest seven day period, starting
 * on the defined first day-of-week with at least the minimal number of days.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class WeekRules implements Comparable<WeekRules>, Serializable {

    /**
     * The ISO-8601 rules, where a week starts on Monday and the first week
     * has a minimum of 4 days.
     */
    public static final WeekRules ISO8601 = new WeekRules(DayOfWeek.MONDAY, 4);
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The cache of rules by locale.
     */
    private static final ConcurrentMap<Locale, WeekRules> CACHE = new ConcurrentHashMap<Locale, WeekRules>(4, 0.75f, 2);

    /**
     * The first day-of-week.
     */
    private final DayOfWeek firstDayOfWeek;
    /**
     * The minimal number of days in the first week.
     */
    private final int minimalDaysInFirstWeek;

    /**
     * Obtains an instance of {@code WeekRules} from the first day-of-week and minimal days.
     * <p>
     * The first day-of-week defines which day the week starts on.
     * The minimal number of days in the first week defines how many days must be present
     * in a month or year, starting from the first day-of-week, before the week is counted
     * as the first week. A value of 1 will count the first day of the month or year as part
     * of the first week, whereas a value of 7 will require the whole seven days to be in
     * the new month or year.
     *
     * @param firstDayOfWeek  the first day of the week, not null
     * @param minimalDaysInFirstWeek  the minimal number of days in the first week, from 1 to 7
     * @return the week rules, not null
     * @throws IllegalArgumentException if the minimal days value is invalid
     */
    public static WeekRules of(DayOfWeek firstDayOfWeek, int minimalDaysInFirstWeek) {
        if (firstDayOfWeek == DayOfWeek.MONDAY && minimalDaysInFirstWeek == 4) {
            return ISO8601;
        }
        return new WeekRules(firstDayOfWeek, minimalDaysInFirstWeek);
    }

    /**
     * Obtains an instance of {@code WeekRules} appropriate for a locale.
     * <p>
     * This will look up appropriate values based on 
     *
     * @param locale  the locale to use, not null
     * @return the week rules, not null
     */
    public static WeekRules of(Locale locale) {
        ISOChronology.checkNotNull(locale, "Locale must not be null");
        WeekRules rules = CACHE.get(locale);
        if (rules == null) {
            // obtain these from GregorianCalendar
            GregorianCalendar gcal = new GregorianCalendar(locale);
            int calDow = gcal.getFirstDayOfWeek();
            DayOfWeek dow = DayOfWeek.SUNDAY.roll(calDow - 1);
            int minDays = gcal.getMinimalDaysInFirstWeek();
            rules = WeekRules.of(dow, minDays);
            CACHE.putIfAbsent(locale, rules);
            rules = CACHE.get(locale);
        }
        return rules;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance of the rules.
     * 
     * @param firstDayOfWeek  the first day of the week, not null
     * @param minimalDaysInFirstWeek  the minimal number of days in the first week, from 1 to 7
     * @throws IllegalArgumentException if the minimal days value is invalid
     */
    private WeekRules(DayOfWeek firstDayOfWeek, int minimalDaysInFirstWeek) {
        ISOChronology.checkNotNull(firstDayOfWeek, "DayOfWeek must not be null");
        if (minimalDaysInFirstWeek < 1 || minimalDaysInFirstWeek > 7) {
            throw new IllegalArgumentException("Minimal number of days is invalid");
        }
        this.firstDayOfWeek = firstDayOfWeek;
        this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the first day-of-week.
     * <p>
     * The first day-of-week varies by culture.
     * For example, the US uses Sunday, while France and the ISO-8601 standard use Monday.
     *
     * @return the first day-of-week, not null
     */
    public DayOfWeek getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    /**
     * Gets the minimal number of days in the first week.
     * <p>
     * The number of days considered to define the first week of a month or year
     * varies by culture.
     * For example, the ISO-8601 requires 4 days (more than half a week) to
     * be present before counting the first week.
     *
     * @param mjDay  the date as a Modified Julian Day (number of days from the epoch of 1858-11-17)
     * @return the number of seconds added, or removed, from the date, either -1 or 1
     */
    public int getMinimalDaysInFirstWeek() {
        return minimalDaysInFirstWeek;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts the specified day-of-week to an {@code int} value using the
     * stored first day-of-week.
     * <p>
     * The returned value will run from 1 to 7, with 1 being the stored first day-of-week.
     * For example, the first day-of-week in the US is Sunday, so passing Tuesday
     * to this method would return 3.
     *
     * @param dayOfWeek  the day-of-week to convert, not null
     * @return the value for the day-of-week based on the first day-of-week, from 1 to 7
     */
    public int convertDayOfWeekToValue(DayOfWeek dayOfWeek) {
        ISOChronology.checkNotNull(firstDayOfWeek, "DayOfWeek must not be null");
        return ((dayOfWeek.ordinal() - firstDayOfWeek.ordinal() + 7) % 7) + 1;
    }

    /**
     * Converts the specified {@code int} value to a day-of-week using the
     * stored first day-of-week.
     * <p>
     * The value must run from 1 to 7, with 1 being converted to the stored first
     * day-of-week and subsequent values being converted to subsequent days.
     * For example, the value 1 would be converted to Sunday for rules based on the
     * conventions of the US and to Monday for rules based on the conventions of France.
     *
     * @param dayOfWeekValue  the day-of-week value to convert, from 1 to 7
     * @return the day-of-week object based on the first day-of-week, not null
     * @throws IllegalArgumentException if the minimal days value is invalid
     */
    public DayOfWeek convertValueToDayOfWeek(int dayOfWeekValue) {
        if (dayOfWeekValue < 1 || dayOfWeekValue > 7) {
            throw new IllegalArgumentException("Minimal number of days is invalid");
        }
        return firstDayOfWeek.roll(dayOfWeekValue - 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares these rules to another set of rules.
     * <p>
     * The comparison is based on the first day-of-week followed by the minimal days.
     *
     * @param other  the other rules to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(WeekRules other) {
        return hashCode() - other.hashCode();
    }

    /**
     * Checks if these rules are equal to the specified rules.
     * <p>
     * The comparison is based on the entire state of the rules.
     *
     * @param object  the other rules to compare to, null returns false
     * @return true if this is equal to the specified rules
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof WeekRules) {
            return hashCode() == object.hashCode();
        }
        return false;
    }

    /**
     * A hash code for these rules.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return firstDayOfWeek.ordinal() * 7 + minimalDaysInFirstWeek;
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of these rules.
     *
     * @return the string representation, not null
     */
    @Override
    public String toString() {
        return "WeekRules[" + firstDayOfWeek + ',' + minimalDaysInFirstWeek + ']';
    }

}
