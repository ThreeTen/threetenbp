/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.Calendrical;
import javax.time.calendar.DateAdjusters;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.DateProvider;
import javax.time.calendar.LocalDate;

/**
 * A representation of a week-based year in the ISO-8601 calendar system.
 * <p>
 * Weekyear is an immutable time field that can only store a week-based year.
 * It is a type-safe way of representing a week-based year in an application.
 * <p>
 * A week-based year is the year that is applicable when using the
 * ISO-8601 week based date calculation. In this system, the week-based year
 * may begin up to three days early or three days late.
 * <p>
 * For example, 2007-01-01 is Monday, thus the the week-based year of 2007
 * also begins on 2007-01-01. In 2008, the first day of the year is Tuesday,
 * with the Monday being in year 2007. However, the week-based year for both
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
 * The week-based year may be queried using getValue().
 * <p>
 * Weekyear is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class Weekyear 
        implements CalendricalProvider, Comparable<Weekyear>, Serializable, DateMatcher {

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
     * The week-based year being represented.
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
    public static DateTimeFieldRule rule() {
        return ISOChronology.weekyearRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Weekyear</code> from a value.
     *
     * @param weekyear  the week-based year to represent, from MIN_YEAR to MAX_YEAR
     * @return the Weekyear instance, never null
     * @throws IllegalCalendarFieldValueException if the weekyear is invalid
     */
    public static Weekyear weekyear(int weekyear) {
        rule().checkValue(weekyear);
        return new Weekyear(weekyear);
    }

    /**
     * Obtains an instance of <code>Weekyear</code> from a date provider.
     * <p>
     * This can be used extract a weekyear object directly from any implementation
     * of DateProvider, including those in other calendar systems.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the Weekyear instance, never null
     */
    public static Weekyear weekyear(DateProvider dateProvider) {
         LocalDate date = LocalDate.date(dateProvider);
         Year year = date.getYear();
         int dom;

         switch (date.getMonthOfYear()) {
            case JANUARY:
                dom = date.getDayOfMonth().getValue();
                if (dom < 4) {
                    int dow = date.getDayOfWeek().getValue();
                    if (dow > dom + 3) {
                        year = year.previous();
                    }
                }
                break;
            case DECEMBER:
                dom = date.getDayOfMonth().getValue();
                if (dom > 28) {
                    int dow = date.getDayOfWeek().getValue();
                    if (dow <= dom % 7) {
                        year = year.next();
                    }
                }
         }
         return Weekyear.weekyear(year.getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified week-based year.
     *
     * @param weekyear  the week-based year to represent
     */
    private Weekyear(int weekyear) {
        this.weekyear = weekyear;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the week-based year value.
     *
     * @return the week-based year, from MIN_YEAR to MAX_YEAR
     */
    public int getValue() {
        return weekyear;
    }

    /**
     * Checks if the value of this week-based year matches the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date matches, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return Weekyear.weekyear(date).equals(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return Calendrical.calendrical(rule(), getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this week-based year in weeks.
     *
     * @return the length of this weekyear in weeks, either 52 or 53
     */
    public int lengthInWeeks() {
        // TODO: optimize
        // TODO: make it work with MIN_YEAR and MAX_YEAR
        LocalDate start = LocalDate.date(weekyear - 1, MonthOfYear.DECEMBER, 28).with(DateAdjusters.nextMonday());
        LocalDate end = LocalDate.date(weekyear, MonthOfYear.DECEMBER, 28).with(DateAdjusters.nextMonday());

        long weeksAsLong = (end.toModifiedJulianDays() - start.toModifiedJulianDays()) / 7;

        return MathUtils.safeToInt(weeksAsLong);
    }

    /**
     * Gets the last week of the week-based year.
     *
     * @return an object representing the last week of the week-based year
     */
    public WeekOfWeekyear getLastWeekOfWeekyear() {
        return WeekOfWeekyear.weekOfWeekyear(lengthInWeeks());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this week-based year instance to another.
     *
     * @param otherWeekyear  the other week-based year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherWeekyear is null
     */
    public int compareTo(Weekyear otherWeekyear) {
        int thisValue = this.weekyear;
        int otherValue = otherWeekyear.weekyear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this week-based year instance after the specified week-based year.
     *
     * @param otherWeekyear  the other week-based year instance, not null
     * @return true if this is after the specified week-based year
     * @throws NullPointerException if otherWeekyear is null
     */
    public boolean isAfter(Weekyear otherWeekyear) {
        return compareTo(otherWeekyear) > 0;
    }

    /**
     * Is this week-based year instance before the specified week-based year.
     *
     * @param otherWeekyear  the other week-based year instance, not null
     * @return true if this is before the specified week-based year
     * @throws NullPointerException if otherWeekyear is null
     */
    public boolean isBefore(Weekyear otherWeekyear) {
        return compareTo(otherWeekyear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the week-based year.
     *
     * @param otherWeekyear  the other week-based year instance, null returns false
     * @return true if the week-based year is the same
     */
    @Override
    public boolean equals(Object otherWeekyear) {
        if (this == otherWeekyear) {
            return true;
        }
        if (otherWeekyear instanceof Weekyear) {
            return weekyear == ((Weekyear) otherWeekyear).weekyear;
        }
        return false;
    }

    /**
     * A hashcode for the week-based year object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return weekyear;
    }

    /**
     * A string describing the week-based year object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "Weekyear=" + getValue();
    }

}
