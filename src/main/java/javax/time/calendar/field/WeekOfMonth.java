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
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.FlexiDateTime;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.DateProvider;

/**
 * A calendrical representation of a week of month.
 * <p>
 * WeekOfMonth is an immutable time field that can only store a week of month.
 * It is a type-safe way of representing a week of month in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The week of month may be queried using getValue().
 * <p>
 * WeekOfMonth is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class WeekOfMonth
        implements Calendrical, Comparable<WeekOfMonth>, Serializable, DateMatcher {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Cache of singleton instances.
     */
    private static final AtomicReferenceArray<WeekOfMonth> cache = new AtomicReferenceArray<WeekOfMonth>(5);

    /**
     * The week of month being represented.
     */
    private final int weekOfMonth;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the week of month field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the week of month rule, never null
     */
    public static DateTimeFieldRule rule() {
        return ISOChronology.INSTANCE.weekOfMonth();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>WeekOfMonth</code>.
     *
     * @param weekOfMonth  the week of month to represent, from 1 to 5
     * @return the created WeekOfMonth
     * @throws IllegalCalendarFieldValueException if the weekOfMonth is invalid
     */
    public static WeekOfMonth weekOfMonth(int weekOfMonth) {
        try {
            WeekOfMonth result = cache.get(--weekOfMonth);
            if (result == null) {
                WeekOfMonth temp = new WeekOfMonth(weekOfMonth + 1);
                cache.compareAndSet(weekOfMonth, null, temp);
                result = cache.get(weekOfMonth);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalCalendarFieldValueException(rule(), weekOfMonth, 1, 5);
        }
    }

    /**
     * Obtains an instance of <code>WeekOfMonth</code> from a date provider.
     * <p>
     * This can be used extract a week of month object directly from any
     * implementation of DateProvider, including those in other calendar systems.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the WeekOfMonth instance, never null
     */
    public static WeekOfMonth weekOfMonth(DateProvider dateProvider) {
        int dom0  = LocalDate.date(dateProvider).getDayOfMonth().getValue() - 1;
        return new WeekOfMonth((dom0 % 7) + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified week of month.
     *
     * @param weekOfMonth  the week of month to represent
     */
    private WeekOfMonth(int weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    /**
     * Resolve the singleton.
     *
     * @return the singleton, never null
     */
    private Object readResolve() {
        return weekOfMonth(weekOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the week of month value.
     *
     * @return the week of month, from 1 to 5
     */
    public int getValue() {
        return weekOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a <code>FlexiDateTime</code>.
     *
     * @return the flexible date-time representation for this instance, never null
     */
    public FlexiDateTime toFlexiDateTime() {
        return new FlexiDateTime(rule(), getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the value of this week of month matches the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date matches, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return WeekOfMonth.weekOfMonth(date) == this;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this week of month instance to another.
     *
     * @param otherWeekOfMonth  the other week of month instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherWeekOfMonth is null
     */
    public int compareTo(WeekOfMonth otherWeekOfMonth) {
        int thisValue = this.weekOfMonth;
        int otherValue = otherWeekOfMonth.weekOfMonth;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the week of month.
     *
     * @param otherWeekOfMonth  the other week of month instance, null returns false
     * @return true if the week of month is the same
     */
    @Override
    public boolean equals(Object otherWeekOfMonth) {
        if (this == otherWeekOfMonth) {
            return true;
        }
        if (otherWeekOfMonth instanceof WeekOfMonth) {
            return weekOfMonth == ((WeekOfMonth) otherWeekOfMonth).weekOfMonth;
        }
        return false;
    }

    /**
     * A hashcode for the week of month object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return weekOfMonth;
    }

    /**
     * A string describing the week of month object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "WeekOfMonth=" + getValue();
    }

}
