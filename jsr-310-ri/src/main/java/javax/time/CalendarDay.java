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
import javax.time.part.Day;
import javax.time.part.Forever;
import javax.time.part.TimePart;

/**
 * An immutable time point, with day precision, operating without a time zone.
 * <p>
 * CalendarDay is an immutable time point that records time information to the
 * precision of a day.
 * <p>
 * As an example, the value "21st September 2007" can be stored in a CalendarDay.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CalendarDay is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class CalendarDay
        implements TimeView<Day, Forever> {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -3005141082690272399L;

    /**
     * The year being represented.
     */
    private final int year;
    /**
     * The month of year being represented.
     */
    private final int monthOfYear;
    /**
     * The day of month being represented.
     */
    private final int dayOfMonth;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CalendarDay</code>.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @return a CalendarDay object
     */
    public static CalendarDay yearMonthDay(int year, int monthOfYear, int dayOfMonth) {
        return new CalendarDay(year, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>CalendarDay</code>.
     *
     * @param timeView  a time view, not null
     * @return a CalendarDay object
     */
    public static CalendarDay calendarDay(TimeView<Day, Forever> timeView) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Obtains an instance of <code>CalendarDay</code>.
     *
     * @param <A>  a time part, not null
     * @param timeView1  a time view, not null
     * @param timeView2  a time view, not null
     * @return a CalendarDay object
     */
    public static <A extends TimePart> CalendarDay calendarDay(
            TimeView<A, Forever> timeView1,
            TimeView<Day, A> timeView2) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Obtains an instance of <code>CalendarDay</code>.
     *
     * @param <A>  a time part, not null
     * @param <B>  a time part, not null
     * @param timeView1  a time view, not null
     * @param timeView2  a time view, not null
     * @param timeView3  a time view, not null
     * @return a CalendarDay object
     */
    public static <A extends TimePart, B extends TimePart> CalendarDay calendarDay(
            TimeView<A, Forever> timeView1,
            TimeView<B, A> timeView2,
            TimeView<Day, B> timeView3) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Obtains an instance of <code>CalendarDay</code>.
     *
     * @param <A>  a time part, not null
     * @param <B>  a time part, not null
     * @param <C>  a time part, not null
     * @param timeView1  a time view, not null
     * @param timeView2  a time view, not null
     * @param timeView3  a time view, not null
     * @param timeView4  a time view, not null
     * @return a CalendarDay object
     */
    public static <A extends TimePart, B extends TimePart, C extends TimePart> CalendarDay calendarDay(
            TimeView<A, Forever> timeView1,
            TimeView<B, A> timeView2,
            TimeView<C, B> timeView3,
            TimeView<Day, C> timeView4) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Obtains an instance of <code>CalendarDay</code>.
     *
     * @param <A>  a time part, not null
     * @param <B>  a time part, not null
     * @param <C>  a time part, not null
     * @param <D>  a time part, not null
     * @param timeView1  a time view, not null
     * @param timeView2  a time view, not null
     * @param timeView3  a time view, not null
     * @param timeView4  a time view, not null
     * @param timeView5  a time view, not null
     * @return a CalendarDay object
     */
    public static <A extends TimePart, B extends TimePart, C extends TimePart, D extends TimePart> CalendarDay calendarDay(
            TimeView<A, Forever> timeView1,
            TimeView<B, A> timeView2,
            TimeView<C, B> timeView3,
            TimeView<D, C> timeView4,
            TimeView<Day, D> timeView5) {
        return new CalendarDay(0, 0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified month of year.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     */
    private CalendarDay(int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
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

    /**
     * Gets the day of month value.
     *
     * @return the day of month
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year
     */
    public int getDayOfYear() {
        return dayOfMonth;
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week
     */
    public int getDayOfWeek() {
        return dayOfMonth;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param timeView  the moment to update to, not null
     * @return a new updated CalendarDay, never null
     */
    public CalendarDay with(TimeView<?, ?> timeView) {
        return null;
    }

    /**
     * Returns a copy of this CalendarDay with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param timeViews  the time views to update to, no nulls
     * @return a new updated CalendarDay, never null
     */
    public CalendarDay with(TimeView<?, ?>... timeViews) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withYear(int year) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withMonthOfYear(int monthOfYear) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withDayOfMonth(int dayOfMonth) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDay
     */
    public CalendarDay withLastDayOfMonth() {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withDayOfYear(int dayOfYear) {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CalendarDay
     */
    public CalendarDay withLastDayOfYear() {
        return new CalendarDay(0, 0, 0);
    }

    /**
     * Returns a copy of this CalendarDay with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent
     * @return a new updated CalendarDay
     */
    public CalendarDay withDayOfWeek(int dayOfWeek) {
        return new CalendarDay(0, 0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CalendarDay
     */
    public CalendarDay plus(Period period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarDay with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CalendarDay
     */
    public CalendarDay plus(Period... periods) {
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
    public CalendarDay plusYears(int years) {
        return new CalendarDay(year + years, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this CalendarDay with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated CalendarDay
     */
    public CalendarDay plusMonths(int months) {
        return new CalendarDay(year, monthOfYear + months, dayOfMonth);
    }

    /**
     * Returns a copy of this CalendarDay with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated CalendarDay
     */
    public CalendarDay plusWeeks(int weeks) {
        return new CalendarDay(year, monthOfYear, dayOfMonth + weeks * 7);
    }

    /**
     * Returns a copy of this CalendarDay with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated CalendarDay
     */
    public CalendarDay plusDays(int days) {
        return new CalendarDay(year, monthOfYear, dayOfMonth + days);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CalendarDay with the specified field rolled.
     * <p>
     * This operation adds or subtracts from the day, rolling within the
     * specified bounds. Rolling DayOfMonth one day higher will take the day
     * of month to one larger, unless that would cause the month to change.
     * In that case, the day of month would be reset to the first.
     * <pre>
     * CalendarDay endJan = CalendarDay.calendarDay(2007, 1, 31);
     * CalendarDay rolled = endJan.roll(Day.PART, Month.PART, 1);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param unit  the unit to roll, not null
     * @param range  the range of the unit, not null
     * @param amount  the amount to roll, positive or negative
     * @return the rolled CalendarDay, never null
     */
    public CalendarDay roll(TimePart unit, TimePart range, int amount) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CalendarDay with all fields smaller than
     * the specified field rounded to the minimum value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param unit  the unit to round, not null
     * @return the rolled CalendarDay, never null
     */
    public CalendarDay round(TimePart unit) {
        // TODO
        return null;
    }

    /**
     * Queries whether this is a leap year.
     *
     * @return true if this is a leap year
     */
    public boolean isLeapYear() {
        // TODO
        return false;
    }

    /**
     * Queries the number of days in the month.
     *
     * @return the number of days in the month, from 28 to 31
     */
    public int getDaysInMonth() {
        // TODO
        return 31;
    }

    /**
     * Queries the number of days in the year.
     *
     * @return the number of days in the year, from 28 to 31
     */
    public int getDaysInYear() {
        // TODO
        return 365;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param other  the other day instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(TimeView<Day, Forever> other) {
        return getCalendarState().compareTo(other.getCalendarState());
    }

    /**
     * Is this instance after the specified one.
     *
     * @param other  the other day instance to compare to, not null
     * @return true if this day is after the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isAfter(TimeView<Day, Forever> other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param other  the other day instance to compare to, not null
     * @return true if this day is before the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isBefore(TimeView<Day, Forever> other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified.
     *
     * @param otherDay  the other day instance to compare to, null returns false
     * @return true if this day is equal to the specified day
     */
    @Override
    public boolean equals(Object otherDay) {
        if (this == otherDay) {
            return true;
        }
        if (otherDay instanceof CalendarDay) {
            CalendarDay other = (CalendarDay) otherDay;
            return  dayOfMonth == other.dayOfMonth &&
                    monthOfYear == other.monthOfYear &&
                    year == other.year;
        }
        return false;
    }

    /**
     * A suitable hashcode for this object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return year
            + 37 * monthOfYear
            + 37 * dayOfMonth;
    }

}
