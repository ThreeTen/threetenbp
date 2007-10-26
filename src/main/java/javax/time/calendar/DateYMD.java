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
package javax.time.calendar;

import java.io.Serializable;

import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;
import javax.time.period.Periods;

/**
 * A calendrical representation of a date without a time zone.
 * <p>
 * DateYMD is an immutable calendrical that represents a date, often viewed as year-month-day.
 * This class does not store or represent a time or time zone.
 * Thus, for example, the value "2nd October 2007" can be stored in a DateYMD.
 * <p>
 * DateYMD is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class DateYMD
        implements Calendrical, Comparable<DateYMD>, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = -1187006174L;

//    /**
//     * Cache of the year being represented.
//     */
//    private final int year;
//    /**
//     * Cache of the month of year being represented.
//     */
//    private final int monthOfYear;
//    /**
//     * Cache of the day of month being represented.
//     */
//    private final int dayOfMonth;
    /**
     * The calendrical state of this date.
     */
    private final CalendricalState state;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>DateYMD</code>.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a DateYMD object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static DateYMD date(int year, int monthOfYear, int dayOfMonth) {
//        CalendricalState state = CalendricalResolvers.strict().createYMD(State.EMPTY, year, monthOfYear, dayOfMonth);
        return new DateYMD(null);
    }

    /**
     * Obtains an instance of <code>DateYMD</code> from a set of calendricals.
     * <p>
     * This can be used to pass in any combination of calendricals that fully specify
     * a calendar day. For example, Year + MonthOfYear + DayOfMonth, or
     * Year + DayOfYear.
     *
     * @param calendricals  a set of calendricals that fully represent a calendar day
     * @return a DateYMD object, never null
     */
    public static DateYMD date(Calendrical... calendricals) {
        return new DateYMD(null);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param state  the valid date-time state, not null
     */
    private DateYMD(CalendricalState state) {
        this.state = state;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to the
     * calendrical data of this instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return state;
    }

    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>DateYMD</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return field.isSupported(Periods.DAYS, Periods.FOREVER);
    }

    /**
     * Gets the value of the specified calendar field.
     * <p>
     * This method queries the value of the specified calendar field.
     * If the calendar field is not supported then an exception is thrown.
     *
     * @param field  the field to query, not null
     * @return the value for the field
     * @throws UnsupportedCalendarFieldException if the field is not supported
     */
    public int get(TimeFieldRule field) {
        if (!isSupported(field)) {
            throw new UnsupportedCalendarFieldException("DateYMD does not support field " + field.getName());
        }
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ISO proleptic year value.
     * <p>
     * The year 1AD is represented by 1.<br />
     * The year 1BC is represented by 0.<br />
     * The year 2BC is represented by -1.<br />
     *
     * @return the year, from MIN_VALUE + 1 to MAX_VALUE
     */
    public int getYear() {
        return ISOChronology.INSTANCE.yearRule().getValue(state);
    }

    /**
     * Gets the month of year value.
     *
     * @return the month of year, never null
     */
    public MonthOfYear getMonthOfYear() {
        int month = ISOChronology.INSTANCE.monthOfYearRule().getValue(state);
        return MonthOfYear.monthOfYear(month);
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year, from 1 to 366
     */
    public int getDayOfYear() {
        return ISOChronology.INSTANCE.dayOfYearRule().getValue(state);
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return ISOChronology.INSTANCE.dayOfMonthRule().getValue(state);
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        int dow = ISOChronology.INSTANCE.dayOfWeekRule().getValue(state);
        return DayOfWeek.dayOfWeek(dow);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateYMD with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated DateYMD, never null
     */
    public DateYMD with(Calendrical calendrical) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated DateYMD, never null
     */
    public DateYMD with(Calendrical... calendricals) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateYMD with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @return a new updated DateYMD, never null
     */
    public DateYMD withYear(int year) {
        if (year == getYear()) {
            return this;
        }
        CalendricalState newState = CalendricalResolvers.strict().set(ISOChronology.INSTANCE.yearRule(), state, year);
        return new DateYMD(newState);
    }

    /**
     * Returns a copy of this DateYMD with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated DateYMD, never null
     */
    public DateYMD withMonthOfYear(int monthOfYear) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the day of yeare value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent, from 1 to 366
     * @return a new updated DateYMD, never null
     */
    public DateYMD withDayOfYear(int dayOfYear) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated DateYMD, never null
     */
    public DateYMD withLastDayOfYear() {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated DateYMD, never null
     */
    public DateYMD withDayOfMonth(int dayOfMonth) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated DateYMD, never null
     */
    public DateYMD withLastDayOfMonth() {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent, from 1 (Monday) to 7 (Sunday)
     * @return a new updated DateYMD, never null
     */
    public DateYMD withDayOfWeek(int dayOfWeek) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateYMD with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated DateYMD, never null
     */
    public DateYMD plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated DateYMD, never null
     */
    public DateYMD plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this DateYMD with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add
     * @return a new updated DateYMD, never null
     */
    public DateYMD plusYears(int years) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated DateYMD, never null
     */
    public DateYMD plusMonths(int months) {
//        if (months == 0) {
//            return this;
//        }
//        DTState state = ISOChronology.STRATEGY_STRICT.plus(ISOChronology.INSTANCE.months(), state, months);
//        return new DateYMD(state);
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated DateYMD, never null
     */
    public DateYMD plusWeeks(int weeks) {
        return null;
    }

    /**
     * Returns a copy of this DateYMD with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated DateYMD, never null
     */
    public DateYMD plusDays(int days) {
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(DateYMD other) {
        return 0;
    }

    /**
     * Is this date after the specified date.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the specified date
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(DateYMD other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date before the specified date.
     *
     * @param other  the other date to compare to, not null
     * @return true if this point is before the specified date
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(DateYMD other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this date equal to the specified date.
     *
     * @param other  the other date to compare to, null returns false
     * @return true if this point is equal to the specified date
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof DateYMD) {
            DateYMD dateYMD = (DateYMD) other;
            return  true;
        }
        return false;
    }

    /**
     * A hashcode for this date.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Outputs the string form of the date.
     *
     * @return the string form of the date
     */
    @Override
    public String toString() {
        return super.toString();
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Internal state class providing calendrical information.
//     *
//     * @author Stephen Colebourne
//     */
//    private static final class State extends CalendricalState {
//        /** The singleton instance. */
//        private static final State EMPTY = new State();
//        /** The epoch days. */
//        private long epochDays;
//        
//        /** {@inheritDoc} */
//        @Override
//        public long getEpochDays() {
//            return epochDays;
//        }
//        /** {@inheritDoc} */
//        @Override
//        public long getFractionalDays() {
//            throw new UnsupportedCalendarFieldException("DateYMD does not support fractional days");
//        }
//        /** {@inheritDoc} */
//        @Override
//        public PeriodUnit getPeriodRange() {
//            return Periods.FOREVER;
//        }
//        /** {@inheritDoc} */
//        @Override
//        public PeriodUnit getPeriodUnit() {
//            return Periods.DAYS;
//        }
//    }

}
