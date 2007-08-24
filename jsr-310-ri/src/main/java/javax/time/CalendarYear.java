/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import javax.time.field.CalendarState;
import javax.time.field.TimeView;
import javax.time.part.Forever;
import javax.time.part.TimePart;
import javax.time.part.Year;

/**
 * A time point of a year.
 * <p>
 * CalendarYear is an immutable time point that records time information to the
 * precision of a year. For example, the value "2007" can be stored in a CalendarYear.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarYear is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarYear
        implements TimeView<Year, Forever> {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -6758311647959144592L;

    /**
     * The year being represented.
     */
    private final int year;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarYear</code>.
     *
     * @param year  the year to represent
     * @return the created CalendarYear
     */
    public static CalendarYear year(int year) {
        return new CalendarYear(year);
    }

    /**
     * Obtains an instance of <code>CalendarYear</code>.
     *
     * @param timeView  a time view, not null
     * @return a CalendarYear object
     */
    public static CalendarYear calendarYear(TimeView<Year, Forever> timeView) {
        return new CalendarYear(0);
    }

    /**
     * Obtains an instance of <code>CalendarYear</code>.
     *
     * @param <A>  a time part, not null
     * @param timeView1  a time view, not null
     * @param timeView2  a time view, not null
     * @return a CalendarYear object
     */
    public static <A extends TimePart> CalendarYear calendarYear(
            TimeView<A, Forever> timeView1,
            TimeView<Year, A> timeView2) {
        return new CalendarYear(0);
    }

    /**
     * Obtains an instance of <code>CalendarYear</code>.
     *
     * @param <A>  a time part, not null
     * @param <B>  a time part, not null
     * @param timeView1  a time view, not null
     * @param timeView2  a time view, not null
     * @param timeView3  a time view, not null
     * @return a CalendarYear object
     */
    public static <A extends TimePart, B extends TimePart> CalendarYear calendarYear(
            TimeView<A, Forever> timeView1,
            TimeView<B, A> timeView2,
            TimeView<Year, B> timeView3) {
        return new CalendarYear(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified year.
     *
     * @param year  the year to represent
     */
    private CalendarYear(int year) {
        this.year = year;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the internal state of the object for interoperation.
     * Most applications will not need to use this method.
     *
     * @return the calendar state, never null
     */
    @Override
    public CalendarState getCalendarState() {
        return new CalendarState();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year value.
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarYear with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param timeView  the moment to update to, not null
     * @return a new updated CalendarYear, never null
     */
    public CalendarYear with(TimeView<?, ?> timeView) {
        return null;
    }

    /**
     * Returns a copy of this CalendarYear with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param timeViews  the time views to update to, no nulls
     * @return a new updated CalendarYear, never null
     */
    public CalendarYear with(TimeView<?, ?>... timeViews) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarYear with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CalendarYear
     */
    public CalendarYear withYear(int year) {
        return new CalendarYear(0);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarYear with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CalendarYear
     */
    public CalendarYear plus(Period period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarYear with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CalendarYear
     */
    public CalendarYear plus(Period... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated CalendarDay
     */
    public CalendarYear plusYears(int years) {
        return new CalendarYear(year + years);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param other  the other year instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherYear is null
     */
    public int compareTo(TimeView<Year, Forever> other) {
        return getCalendarState().compareTo(other.getCalendarState());
    }

    /**
     * Is this instance after the specified one.
     *
     * @param other  the other month instance to compare to, not null
     * @return true if this year is after the specified year
     * @throws NullPointerException if otherYear is null
     */
    public boolean isAfter(TimeView<Year, Forever> other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param other  the other year instance to compare to, not null
     * @return true if this year is before the specified year
     * @throws NullPointerException if otherYear is null
     */
    public boolean isBefore(TimeView<Year, Forever> other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherYear  the other month instance to compare to, null returns false
     * @return true if this month is equal to the specified month
     */
    @Override
    public boolean equals(Object otherYear) {
        if (this == otherYear) {
            return true;
        }
        if (otherYear instanceof CalendarYear) {
            return year == ((CalendarYear) otherYear).year;
        }
        return false;
    }

    /**
     * A hashcode for the month object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return year;
    }

}
