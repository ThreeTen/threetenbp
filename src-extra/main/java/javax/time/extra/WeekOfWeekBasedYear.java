/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.extra;

import static javax.time.calendrical.ISODateTimeRule.WEEK_OF_WEEK_BASED_YEAR;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.time.CalendricalException;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.DateTimeRule;
import javax.time.calendrical.ISOChronology;
import javax.time.calendrical.IllegalCalendarFieldValueException;

/**
 * A representation of a week of week-based-year in the ISO-8601 calendar system.
 * <p>
 * WeekOfWeekBasedYear is an immutable time field that can only store a week of week-based-year.
 * It is a type-safe way of representing a week of week-based-year in an application.
 * <p>
 * The week of week-based-year is a field that should be used in combination with
 * the WeekBasedYear field. Together they represent the ISO-8601 week based date
 * calculation described in {@link WeekBasedYear}.
 * <p>
 * Static factory methods allow you to construct instances.
 * The week of week-based-year may be queried using getValue().
 * <p>
 * WeekOfWeekBasedYear is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class WeekOfWeekBasedYear
        implements Calendrical, Comparable<WeekOfWeekBasedYear>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Cache of singleton instances.
     */
    private static final AtomicReferenceArray<WeekOfWeekBasedYear> CACHE = new AtomicReferenceArray<WeekOfWeekBasedYear>(53);

    /**
     * The week of week-based-year being represented.
     */
    private final int weekOfWeekyear;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the week of week-based-year field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the week of week-based-year rule, never null
     */
    public static DateTimeRule rule() {
        return WEEK_OF_WEEK_BASED_YEAR;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>WeekOfWeekBasedYear</code> from a value.
     * <p>
     * A week of week-based-year object represents one of the 53 weeks of the year,
     * from 1 to 53. These are cached internally and returned as singletons,
     * so they can be compared using ==.
     *
     * @param weekOfWeekyear  the week of week-based-year to represent, from 1 to 53
     * @return the WeekOfWeekBasedYear singleton, never null
     * @throws IllegalCalendarFieldValueException if the weekOfWeekyear is invalid
     */
    public static WeekOfWeekBasedYear weekOfWeekBasedYear(int weekOfWeekyear) {
        try {
            WeekOfWeekBasedYear result = CACHE.get(--weekOfWeekyear);
            if (result == null) {
                WeekOfWeekBasedYear temp = new WeekOfWeekBasedYear(weekOfWeekyear + 1);
                CACHE.compareAndSet(weekOfWeekyear, null, temp);
                result = CACHE.get(weekOfWeekyear);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalCalendarFieldValueException(rule(), ++weekOfWeekyear);
        }
    }

    /**
     * Obtains an instance of <code>WeekOfWeekBasedYear</code> from a calendrical.
     * <p>
     * This can be used extract the week-of-week-based-year value directly from any implementation
     * of <code>Calendrical</code>, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the WeekOfWeekBasedYear instance, never null
     * @throws CalendricalException if the week-of-week-based-year cannot be obtained
     */
    public static WeekOfWeekBasedYear weekOfWeekBasedYear(Calendrical calendrical) {
        return weekOfWeekBasedYear(rule().getValueChecked(calendrical).getValidIntValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified week of week-based-year.
     *
     * @param weekOfWeekyear  the week of week-based-year to represent
     */
    private WeekOfWeekBasedYear(int weekOfWeekyear) {
        this.weekOfWeekyear = weekOfWeekyear;
    }

    /**
     * Resolve the singleton.
     *
     * @return the singleton, never null
     */
    private Object readResolve() {
        return weekOfWeekBasedYear(weekOfWeekyear);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this instance then
     * <code>null</code> will be returned.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        if (ruleToDerive == rule()) {
            return (T) this;
        }
        return CalendricalEngine.derive(ruleToDerive, rule(), ISOChronology.INSTANCE, rule().field(getValue()));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the week of week-based-year value.
     *
     * @return the week of week-based-year, from 1 to 53
     */
    public int getValue() {
        return weekOfWeekyear;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this week of weekyear is valid for the specified week-based-year.
     *
     * @param weekyear  the weekyear to validate against, not null
     * @return true if this week of weekyear is valid for the week-based-year
     */
    public boolean isValid(WeekBasedYear weekyear) {
        if (weekyear == null) {
            throw new NullPointerException("Weekyear cannot be null");
        }
        return (weekOfWeekyear < 53 || weekyear.lengthInWeeks() == 53);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this week of week-based-year instance to another.
     *
     * @param otherWeekOfWeekBasedYear  the other week of week-based-year instance, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherWeekOfWeekBasedYear is null
     */
    public int compareTo(WeekOfWeekBasedYear otherWeekOfWeekBasedYear) {
        int thisValue = this.weekOfWeekyear;
        int otherValue = otherWeekOfWeekBasedYear.weekOfWeekyear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    /**
     * Is this week of week-based-year instance after the specified week of week-based-year.
     *
     * @param otherWeekOfWeekBasedYear  the other week of week-based-year instance, not null
     * @return true if this is after the specified week of week-based-year
     * @throws NullPointerException if otherWeekOfWeekBasedYear is null
     */
    public boolean isAfter(WeekOfWeekBasedYear otherWeekOfWeekBasedYear) {
        return compareTo(otherWeekOfWeekBasedYear) > 0;
    }

    /**
     * Is this week of week-based-year instance before the specified week of week-based-year.
     *
     * @param otherWeekOfWeekBasedYear  the other week of week-based-year instance, not null
     * @return true if this is before the specified week of week-based-year
     * @throws NullPointerException if otherWeekOfWeekBasedYear is null
     */
    public boolean isBefore(WeekOfWeekBasedYear otherWeekOfWeekBasedYear) {
        return compareTo(otherWeekOfWeekBasedYear) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the week of week-based-year.
     *
     * @param otherWeekOfWeekBasedYear  the other week of week-based-year instance, null returns false
     * @return true if the week of week-based-year is the same
     */
    @Override
    public boolean equals(Object otherWeekOfWeekBasedYear) {
        return this == otherWeekOfWeekBasedYear;
    }

    /**
     * A hash code for the week of week-based-year object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return weekOfWeekyear;
    }

    /**
     * A string describing the week of week-based-year object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "WeekOfWeekBasedYear=" + getValue();
    }

}
