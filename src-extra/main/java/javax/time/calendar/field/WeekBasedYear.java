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

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMatcher;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.UnsupportedRuleException;
import javax.time.calendar.Year;

/**
 * A representation of a week-based-year in the ISO-8601 calendar system.
 * <p>
 * WeekBasedYear is an immutable time field that can only store a week-based-year.
 * It is a type-safe way of representing a week-based-year in an application.
 * <p>
 * A week-based-year is the year that is applicable when using the
 * ISO-8601 week based date calculation. In this system, the week-based-year
 * may begin up to three days early or three days late.
 * <p>
 * For example, 2007-01-01 is Monday, thus the the week-based-year of 2007
 * also begins on 2007-01-01. In 2008, the first day of the year is Tuesday,
 * with the Monday being in year 2007. However, the week-based-year for both
 * Monday and Tuesday is 2008.
 * <pre>
 *   Date     DayOfWeek  Week-based year
 * 2007-12-30  Sunday     2007-W52
 * 2007-12-31  Monday     2008-W01
 * 2007-01-01  Tuesday    2008-W01
 * </pre>
 * <p>
 * The ISO-8601 rules state that the first week of the year is the one that
 * contains the first Thursday of the year.
 * <p>
 * Static factory methods allow you to construct instances.
 * The week-based-year may be queried using getValue().
 * <p>
 * WeekBasedYear is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class WeekBasedYear
        implements Calendrical, Comparable<WeekBasedYear>, CalendricalMatcher, Serializable {

    /**
     * Constant for the minimum week-based-year.
     */
    public static final int MIN_YEAR = Year.MIN_YEAR;
    /**
     * Constant for the maximum week-based-year.
     */
    public static final int MAX_YEAR = Year.MAX_YEAR;
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The week-based-year being represented.
     */
    private final int weekyear;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the week-based-year field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the week-based-year rule, never null
     */
    public static DateTimeFieldRule<Integer> rule() {
        return ISOChronology.weekBasedYearRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>WeekBasedYear</code> from a value.
     *
     * @param weekyear  the week-based-year to represent, from MIN_YEAR to MAX_YEAR
     * @return the WeekBasedYear instance, never null
     * @throws IllegalCalendarFieldValueException if the week-based-year is invalid
     */
    public static WeekBasedYear weekBasedYear(int weekyear) {
        rule().checkValue(weekyear);
        return new WeekBasedYear(weekyear);
    }

    /**
     * Obtains an instance of <code>WeekBasedYear</code> from a calendrical.
     * <p>
     * This can be used extract the week-based-year value directly from any implementation
     * of <code>Calendrical</code>, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the WeekBasedYear instance, never null
     * @throws UnsupportedRuleException if the week-based-year cannot be obtained
     */
    public static WeekBasedYear weekBasedYear(Calendrical calendrical) {
        return weekBasedYear(rule().getInt(calendrical));
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified week-based-year.
     *
     * @param weekyear  the week-based-year to represent
     */
    private WeekBasedYear(int weekyear) {
        this.weekyear = weekyear;
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
        return rule().deriveValueFor(rule, weekyear, this, ISOChronology.INSTANCE);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the week-based-year value.
     *
     * @return the week-based-year, from MIN_YEAR to MAX_YEAR
     */
    public int getValue() {
        return weekyear;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the week-based-year extracted from the calendrical matches this.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical matches, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        Integer calValue = calendrical.get(rule());
        return calValue != null && calValue == getValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this week-based-year in weeks.
     *
     * @return the length of this week-based-year in weeks, either 52 or 53
     */
    public int lengthInWeeks() {
        // TODO: optimize
        LocalDate start = LocalDate.of(weekyear, MonthOfYear.JANUARY, 4);
        LocalDate end = LocalDate.of(weekyear, MonthOfYear.DECEMBER, 28);

        long weeksAsLong = (end.toModifiedJulianDays() + (8 - end.getDayOfWeek().getValue()) -
                start.toModifiedJulianDays() + start.getDayOfWeek().getValue() - 1) / 7;

        return MathUtils.safeToInt(weeksAsLong);
    }

    /**
     * Gets the last week of the week-based-year.
     *
     * @return an object representing the last week of the week-based-year
     */
    public WeekOfWeekBasedYear getLastWeekOfWeekyear() {
        return WeekOfWeekBasedYear.weekOfWeekBasedYear(lengthInWeeks());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this week-based-year instance to another.
     *
     * @param otherWeekyear  the other week-based-year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherWeekyear is null
     */
    public int compareTo(WeekBasedYear otherWeekyear) {
        int thisValue = this.weekyear;
        int otherValue = otherWeekyear.weekyear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this week-based-year instance after the specified week-based-year.
     *
     * @param otherWeekyear  the other week-based-year instance, not null
     * @return true if this is after the specified week-based-year
     * @throws NullPointerException if otherWeekyear is null
     */
    public boolean isAfter(WeekBasedYear otherWeekyear) {
        return compareTo(otherWeekyear) > 0;
    }

    /**
     * Is this week-based-year instance before the specified week-based-year.
     *
     * @param otherWeekyear  the other week-based-year instance, not null
     * @return true if this is before the specified week-based-year
     * @throws NullPointerException if otherWeekyear is null
     */
    public boolean isBefore(WeekBasedYear otherWeekyear) {
        return compareTo(otherWeekyear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the week-based-year.
     *
     * @param otherWeekyear  the other week-based-year instance, null returns false
     * @return true if the week-based-year is the same
     */
    @Override
    public boolean equals(Object otherWeekyear) {
        if (this == otherWeekyear) {
            return true;
        }
        if (otherWeekyear instanceof WeekBasedYear) {
            return weekyear == ((WeekBasedYear) otherWeekyear).weekyear;
        }
        return false;
    }

    /**
     * A hash code for the week-based-year object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return weekyear;
    }

    /**
     * A string describing the week-based-year object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "WeekBasedYear=" + getValue();
    }

}
