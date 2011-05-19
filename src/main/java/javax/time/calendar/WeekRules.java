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

import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.MathUtils;

/**
 * Rules defining how weeks are counted.
 * <p>
 * A standard week is seven days long, but cultures have different rules for some
 * other aspects of a week.
 * <ul>
 * <li>The first day-of-week.
 * For example, the ISO-8601 standard considers Monday to be the first day-of-week.
 * <li>The minimal number of days in the first week.
 * For example, the ISO-08601 standard counts the first week as the one with 4 days.
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
     * Creates a date at the start of the week-based-year based on these rules.
     * <p>
     * These rules define the day-of-week that a week begins on and the minimal
     * number of days in a month or year before the first week is counted.
     * These rules are used to calculate what date a given year starts.
     *
     * @param weekBasedYear  the week-based-year, based on these rules, within the valid range
     * @return the date that the week-based-year starts, not null
     */
    public LocalDate createDate(int weekBasedYear) {
        LocalDate inFirstWeek = LocalDate.of(weekBasedYear, 1, minimalDaysInFirstWeek);
        return inFirstWeek.with(DateAdjusters.previousOrCurrent(firstDayOfWeek));
    }

    /**
     * Creates a date from a week-based-year, week and day, all defined based
     * on these rules.
     * <p>
     * These rules define the day-of-week that a week begins on and the minimal
     * number of days in a month or year before the first week is counted.
     * These rules are used to calculate the date equivalent to the input parameters.
     * <p>
     * The week and day-of-week are interpreted leniently. For example, a week value of
     * -1 is two weeks before week 1, and a day-of-week value of 10 is three days after
     * the day-of-week with the value 7.
     *
     * @param weekBasedYear  the week-based-year, based on these rules, within the valid range
     * @param weekOfWeekbasedYear  the week-of-week-based-year, based on these rules, any value
     * @param ruleRelativeDayOfWeekValue  the day-of-week value, relative to
     *  the first day-of-week of these rules, any value
     * @return the date equivalent to the input parameters, not null
     */
    public LocalDate createDate(int weekBasedYear, int weekOfWeekbasedYear, int ruleRelativeDayOfWeekValue) {
        LocalDate startFirstWeek = createDate(weekBasedYear);
        return startFirstWeek.plusDays((weekOfWeekbasedYear - 1L) * 7L + (ruleRelativeDayOfWeekValue - 1L));
    }

    /**
     * Creates a date from a week-based-year and week based on these rules, combined
     * with the standardized day-of-week.
     * <p>
     * These rules define the day-of-week that a week begins on and the minimal
     * number of days in a month or year before the first week is counted.
     * These rules are used to calculate the date equivalent to the input parameters.
     *
     * @param weekBasedYear  the week-based-year, based on these rules, within the valid range
     * @param weekOfWeekbasedYear  the week-of-week-based-year, based on these rules, any value
     * @param ruleRelativeDayOfWeekValue  the day-of-week value, relative to
     *  the first day-of-week of these rules, any value
     * @return the value for the day-of-week based on the first day-of-week, from 1 to 7
     */
    public LocalDate createDate(int weekBasedYear, int weekOfWeekbasedYear, DayOfWeek dayOfWeek) {
        return createDate(weekBasedYear, weekOfWeekbasedYear, 1).with(DateAdjusters.nextOrCurrent(dayOfWeek));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts the standardized {@code DayOfWeek} to an {@code int} value
     * relative to the first day-of-week.
     * <p>
     * The returned value will run from 1 to 7, with 1 being the stored first day-of-week.
     * For example, the first day-of-week in the US is Sunday, so passing Tuesday
     * to this method would return 3.
     *
     * @param dayOfWeek  the day-of-week to convert, not null
     * @return the value for the day-of-week based on the first day-of-week, from 1 to 7
     */
    public int convertDayOfWeek(DayOfWeek dayOfWeek) {
        ISOChronology.checkNotNull(firstDayOfWeek, "DayOfWeek must not be null");
        return dayOfWeek.roll(-firstDayOfWeek.ordinal()).getValue();
    }

    /**
     * Converts the specified {@code int} value relative to the first
     * day-of-week to a standardized {@code DayOfWeek}.
     * <p>
     * The value must run from 1 to 7, with 1 being converted to the stored first
     * day-of-week and subsequent values being converted to subsequent days.
     * For example, the value 1 would be converted to Sunday for rules based on the
     * conventions of the US and to Monday for rules based on the conventions of France.
     *
     * @param ruleRelativeDayOfWeekValue  the day-of-week value to convert, relative to
     *  the first day-of-week of these rules, from 1 to 7
     * @return the day-of-week object based on the first day-of-week, not null
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public DayOfWeek convertDayOfWeek(int ruleRelativeDayOfWeekValue) {
        dayOfWeek().checkValidValue(ruleRelativeDayOfWeekValue);
        return firstDayOfWeek.roll(ruleRelativeDayOfWeekValue - 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a rule that can be used to print, parse and manipulate the
     * day-of-week value based on these rules.
     * <p>
     * See {@link #convertDayOfWeek(DayOfWeek)} for more information.
     *
     * @return the rule for the date, not null
     */
    public DateTimeRule dayOfWeek() {
        return new DayOfWeekRule();
    }

    /**
     * Gets a rule that can be used to print, parse and manipulate the
     * week-of-week-based-year value.
     * <p>
     * Weeks defined used these rules do not necessarily align with years
     * defined using the standard ISO-8601 calendar. This rule provides
     * the means to access the week number, which is used with the
     * {@link #weekBasedYear() week-based-year}.
     * The week number for the first week of the week-based-year will be 1.
     * <p>
     * Note that the first week may start in the previous calendar year.
     * Note also that the first few days of a calendar year may be in the
     * week-based-year corresponding to the previous calendar year.
     *
     * @return the rule for the date, not null
     */
    public DateTimeRule weekOfWeekBasedYear() {
        return null;  // TODO
    }

    /**
     * Gets a rule that can be used to print, parse and manipulate the
     * week-based-year value.
     * <p>
     * Weeks defined used these rules do not necessarily align with years
     * defined using the standard ISO-8601 calendar. This rule provides
     * the means to access the week-based-year, which is used with the
     * {@link #weekOfWeekBasedYear() week-of-week-based-year}.
     * The week-based-year will be the same as the calendar year except for
     * a few days at the start and end of the year.
     * <p>
     * Note that the first week may start in the previous calendar year.
     * Note also that the first few days of a calendar year may be in the
     * week-based-year corresponding to the previous calendar year.
     *
     * @return the rule for the date, not null
     */
    public DateTimeRule weekBasedYear() {
        return null;  // TODO
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
     * The comparison is based on the entire state of the rules, which is
     * the first day-of-week and minimal days.
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

    //-----------------------------------------------------------------------
    /**
     * Merges the fields for these week rules.
     */
    void merge(CalendricalMerger merger) {
        DateTimeField y = merger.getValue(weekBasedYear());
        DateTimeField woy = merger.getValue(weekOfWeekBasedYear());
        DateTimeField dow = merger.getValue(dayOfWeek());
        if (y != null && woy != null && dow != null) {
            merger.storeMerged(LocalDate.rule(), null);
            merger.removeProcessed(weekBasedYear());
            merger.removeProcessed(weekOfWeekBasedYear());
            merger.removeProcessed(dayOfWeek());
        } else if (dow != null && dow.isValidValue()) {
            merger.storeMergedField(DAY_OF_WEEK, convertDayOfWeek(dow.getValidIntValue()).getValue());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    final class DayOfWeekRule extends DateTimeRule implements Serializable {
        private static final long serialVersionUID = 1L;
        private DayOfWeekRule() {
            super(ISOChronology.INSTANCE, "DayOfWeek-" + WeekRules.this.toString(), ISOPeriodUnit.DAYS, ISOPeriodUnit.WEEKS, 1, 7);
        }
        @Override
        protected DateTimeField derive(Calendrical calendrical) {
            DateTimeField dow = calendrical.get(DAY_OF_WEEK);
            if (dow != null && dow.isValidValue()) {
                return field(((dow.getValue() - 1 - firstDayOfWeek.ordinal() + 7) % 7) + 1);
            }
//                long dow0 = MathUtils.safeDecrement(dow.getValue());
//                long weeks = MathUtils.floorDiv(dow0, 7);
//                dow0 = MathUtils.floorMod(dow0, 7);
//                long adjustedDow = ((dow0 - firstDayOfWeek.ordinal() + 7) % 7) + 1;
//                return field(MathUtils.safeAdd(adjustedDow, MathUtils.safeMultiply(weeks, 7)));
            return null;
        }
        @Override
        protected void merge(CalendricalMerger merger) {
            super.merge(merger);
        }
        @Override
        public long convertToPeriod(long value) {
            return MathUtils.safeDecrement(value);
        }
        @Override
        public long convertFromPeriod(long amount) {
            return MathUtils.safeIncrement(amount);
        }
    }

}
