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
import javax.time.part.Month;
import javax.time.part.TimePart;

/**
 * A moment of a month.
 * <p>
 * CalendarMonth is an immutable moment that records time information to the precision
 * of a month. For example, the value "September 2007" can be stored in a CalendarMonth.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarMonth is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarMonth
        implements TimeView<Month, Forever> {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The year being represented.
     */
    private final int year;
    /**
     * The month of year being represented.
     */
    private final int monthOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarMonth</code>.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @return the created CalendarMonth
     */
    public static CalendarMonth yearMonth(int year, int monthOfYear) {
        return new CalendarMonth(year, monthOfYear);
    }

    /**
     * Obtains an instance of <code>CalendarMonth</code>.
     *
     * @param timeView  a time view, not null
     * @return a CalendarMonth object
     */
    public static CalendarMonth calendarMonth(TimeView<Month, Forever> timeView) {
        return new CalendarMonth(0, 0);
    }

    /**
     * Obtains an instance of <code>CalendarMonth</code>.
     *
     * @param <A>  a time part, not null
     * @param timeView1  a time view, not null
     * @param timeView2  a time view, not null
     * @return a CalendarMonth object
     */
    public static <A extends TimePart> CalendarMonth calendarMonth(
            TimeView<A, Forever> timeView1,
            TimeView<Month, A> timeView2) {
        return new CalendarMonth(0, 0);
    }

    /**
     * Obtains an instance of <code>CalendarMonth</code>.
     *
     * @param <A>  a time part, not null
     * @param <B>  a time part, not null
     * @param timeView1  a time view, not null
     * @param timeView2  a time view, not null
     * @param timeView3  a time view, not null
     * @return a CalendarMonth object
     */
    public static <A extends TimePart, B extends TimePart> CalendarMonth calendarMonth(
            TimeView<A, Forever> timeView1,
            TimeView<B, A> timeView2,
            TimeView<Month, B> timeView3) {
        return new CalendarMonth(0, 0);
    }

    /**
     * Obtains an instance of <code>CalendarMonth</code>.
     *
     * @param <A>  a time part, not null
     * @param <B>  a time part, not null
     * @param <C>  a time part, not null
     * @param timeView1  a time view, not null
     * @param timeView2  a time view, not null
     * @param timeView3  a time view, not null
     * @param timeView4  a time view, not null
     * @return a CalendarMonth object
     */
    public static <A extends TimePart, B extends TimePart, C extends TimePart> CalendarMonth calendarMonth(
            TimeView<A, Forever> timeView1,
            TimeView<B, A> timeView2,
            TimeView<C, B> timeView3,
            TimeView<Month, C> timeView4) {
        return new CalendarMonth(0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     */
    private CalendarMonth(int year, int monthOfYear) {
        this.year = year;
        this.monthOfYear = monthOfYear;
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

    /**
     * Gets the month of year value.
     *
     * @return the month of year
     */
    public int getMonthOfYear() {
        return monthOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarMonth with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param timeView  the moment to update to, not null
     * @return a new updated CalendarMonth, never null
     */
    public CalendarMonth with(TimeView<?, ?> timeView) {
        return null;
    }

    /**
     * Returns a copy of this CalendarMonth with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param timeViews  the time views to update to, no nulls
     * @return a new updated CalendarMonth, never null
     */
    public CalendarMonth with(TimeView<?, ?>... timeViews) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarMonth with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CalendarMonth
     */
    public CalendarMonth withYear(int year) {
        return new CalendarMonth(0, 0);
    }

    /**
     * Returns a copy of this CalendarMonth with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent
     * @return a new updated CalendarMonth
     */
    public CalendarMonth withMonthOfYear(int monthOfYear) {
        return new CalendarMonth(0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarMonth with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CalendarMonth
     */
    public CalendarMonth plus(Period period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarMonth with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CalendarMonth
     */
    public CalendarMonth plus(Period... periods) {
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
    public CalendarMonth plusYears(int years) {
        return new CalendarMonth(year + years, monthOfYear);
    }

    /**
     * Returns a copy of this CalendarDay with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated CalendarDay
     */
    public CalendarMonth plusMonths(int months) {
        return new CalendarMonth(year, monthOfYear + months);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param other  the other month instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherMonth is null
     */
    public int compareTo(TimeView<Month, Forever> other) {
        return getCalendarState().compareTo(other.getCalendarState());
    }

    /**
     * Is this instance after the specified one.
     *
     * @param other  the other month instance to compare to, not null
     * @return true if this month is after the specified month
     * @throws NullPointerException if otherMonth is null
     */
    public boolean isAfter(TimeView<Month, Forever> other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param other  the other month instance to compare to, not null
     * @return true if this month is before the specified month
     * @throws NullPointerException if otherMonth is null
     */
    public boolean isBefore(TimeView<Month, Forever> other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherMonth  the other month instance to compare to, null returns false
     * @return true if this month is equal to the specified month
     */
    @Override
    public boolean equals(Object otherMonth) {
        if (this == otherMonth) {
            return true;
        }
        if (otherMonth instanceof CalendarMonth) {
            CalendarMonth other = (CalendarMonth) otherMonth;
            return monthOfYear == other.monthOfYear && year == other.year;
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
        return year + 37 * monthOfYear;
    }

}
