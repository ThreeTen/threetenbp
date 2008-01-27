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
package javax.time.i18n;

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalState;
import javax.time.calendar.field.DayOfWeek;
import javax.time.period.PeriodView;

/**
 * A date in the Coptic calendar system.
 * <p>
 * CopticDate is an immutable class that represents a date in the Coptic calendar
 * system to the precision of a day. Although this class can be created from,
 * and converted to, the standard ISO calendar system, it is not directly linked.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * CopticDate is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class CopticDate implements Calendrical, Comparable<CopticDate>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The calendrical state.
     */
    private final CalendricalState state;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>CopticDate</code>.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     * @return a CopticDate object
     */
    public static CopticDate yearMonthDay(int year, CopticMonthOfYear monthOfYear, int dayOfMonth) {
        return new CopticDate(year, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>CopticDate</code> from a set of moments.
     * <p>
     * This can be used to pass in any combination of moments that fully specify
     * a calendar day. For example, Year + MonthOfYear + DayOfMonth, or
     * Year + DayOfYear.
     *
     * @param moments  a set of moments that fully represent a calendar day
     * @return a CopticDate object
     */
    public static CopticDate copticDate(Calendrical... moments) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified date.
     *
     * @param year  the year to represent
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     */
    private CopticDate(int year, CopticMonthOfYear monthOfYear, int dayOfMonth) {
        this.state = CopticChronology.INSTANCE.stateFromYearMonthDay(year, monthOfYear.getValue(), dayOfMonth);
    }

    /**
     * Constructs an instance with the specified state.
     *
     * @param state  the calendrical state, not null
     */
    private CopticDate(CalendricalState state) {
        this.state = (CopticChronology.State) state;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * instance.
     *
     * @return the calendar state for this instance, never null
     */
    public CalendricalState getCalendricalState() {
        return state;
    }

    /**
     * Gets the chronology that describes the Coptic calendar system.
     *
     * @return the Coptic chronology, never null
     */
    public CopticChronology getChronology() {
        return CopticChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the year value.
     *
     * @return the year
     */
    public int getYear() {
        return CopticChronology.INSTANCE.yearRule().getValue(state);
    }

//    /**
//     * Gets the season of year value.
//     *
//     * @return the season of year, never null
//     */
//    public CopticSeasonOfYear getSeasonOfYear() {
//        return getMonthOfYear().getSeasonOfYear();
//    }
//
//    /**
//     * Gets the month of season value.
//     *
//     * @return the month of season
//     */
//    public int getMonthOfSeason() {
//        return getMonthOfYear().getMonthOfSeason();
//    }
//
    /**
     * Gets the month of year value.
     *
     * @return the month of year, never null
     */
    public CopticMonthOfYear getMonthOfYear() {
        int value = CopticChronology.INSTANCE.monthOfYearRule().getValue(state);
        return CopticMonthOfYear.copticMonthOfYear(value);
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year
     */
    public int getDayOfYear() {
        return CopticChronology.INSTANCE.dayOfYearRule().getValue(state);
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month
     */
    public int getDayOfMonth() {
        return CopticChronology.INSTANCE.dayOfMonthRule().getValue(state);
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week
     */
    public DayOfWeek getDayOfWeek() {
        int value = CopticChronology.INSTANCE.dayOfWeekRule().getValue(state);
        return DayOfWeek.dayOfWeek(value);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CopticDate with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent
     * @return a new updated CopticDate
     */
    public CopticDate withYear(int year) {
        CalendricalState newState = CopticChronology.INSTANCE.yearRule().setValue(state, year);
        return new CopticDate(newState);
    }

    /**
     * Returns a copy of this CopticDate with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent
     * @return a new updated CopticDate
     */
    public CopticDate withMonthOfYear(CopticMonthOfYear monthOfYear) {
        CalendricalState newState = CopticChronology.INSTANCE.monthOfYearRule().setValue(state, monthOfYear.getValue());
        return new CopticDate(newState);
    }

    /**
     * Returns a copy of this CopticDate with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent
     * @return a new updated CopticDate
     */
    public CopticDate withDayOfYear(int dayOfYear) {
        CalendricalState newState = CopticChronology.INSTANCE.dayOfYearRule().setValue(state, dayOfYear);
        return new CopticDate(newState);
    }

    /**
     * Returns a copy of this CopticDate with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CopticDate
     */
    public CopticDate withLastDayOfYear() {
        int lastDOY = CopticChronology.INSTANCE.dayOfYearRule().getMaximumValue(this);
        return withDayOfYear(lastDOY);
    }

    /**
     * Returns a copy of this CopticDate with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent
     * @return a new updated CopticDate
     */
    public CopticDate withDayOfMonth(int dayOfMonth) {
        CalendricalState newState = CopticChronology.INSTANCE.dayOfMonthRule().setValue(state, dayOfMonth);
        return new CopticDate(newState);
    }

    /**
     * Returns a copy of this CopticDate with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated CopticDate
     */
    public CopticDate withLastDayOfMonth() {
        int lastDOM = CopticChronology.INSTANCE.dayOfMonthRule().getMaximumValue(this);
        return withDayOfYear(lastDOM);
    }

    /**
     * Returns a copy of this CopticDate with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent
     * @return a new updated CopticDate
     */
    public CopticDate withDayOfWeek(DayOfWeek dayOfWeek) {
        CalendricalState newState = CopticChronology.INSTANCE.dayOfWeekRule().setValue(state, dayOfWeek.getValue());
        return new CopticDate(newState);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CopticDate with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated CopticDate
     */
    public CopticDate plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this CopticDate with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, not null
     * @return a new updated CopticDate
     */
    public CopticDate plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this CopticDate with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated CopticDate
     */
    public CopticDate plusYears(int years) {
        return null; //new CopticDate(year + years, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this CopticDate with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated CopticDate
     */
    public CopticDate plusMonths(int months) {
        return null; //new CopticDate(year, monthOfYear + months, dayOfMonth);
    }

    /**
     * Returns a copy of this CopticDate with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated CopticDate
     */
    public CopticDate plusWeeks(int weeks) {
        return null; //new CopticDate(year, monthOfYear, dayOfMonth + weeks * 7);
    }

    /**
     * Returns a copy of this CopticDate with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated CopticDate
     */
    public CopticDate plusDays(int days) {
        return null; //new CopticDate(year, monthOfYear, dayOfMonth + days);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instance to another.
     *
     * @param otherDay  the other day instance to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDay is null
     */
    public int compareTo(CopticDate otherDay) {
        int cmp = MathUtils.safeCompare(getYear(), otherDay.getYear());
        if (cmp != 0) {
            return cmp;
        }
        cmp = getMonthOfYear().compareTo(otherDay.getMonthOfYear());
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(getDayOfMonth(), otherDay.getDayOfMonth());
    }

    /**
     * Is this instance after the specified one.
     *
     * @param otherDay  the other day instance to compare to, not null
     * @return true if this day is after the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isAfter(CopticDate otherDay) {
        return compareTo(otherDay) > 0;
    }

    /**
     * Is this instance before the specified one.
     *
     * @param otherDay  the other day instance to compare to, not null
     * @return true if this day is before the specified day
     * @throws NullPointerException if otherDay is null
     */
    public boolean isBefore(CopticDate otherDay) {
        return compareTo(otherDay) < 0;
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
        if (otherDay instanceof CopticDate) {
            CopticDate other = (CopticDate) otherDay;
            return  getDayOfMonth() == other.getDayOfMonth() &&
                    getMonthOfYear() == other.getMonthOfYear() &&
                    getYear() == other.getYear();
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
        return getYear() + 37 * getMonthOfYear().hashCode() + 37 * getDayOfMonth();
    }

}
