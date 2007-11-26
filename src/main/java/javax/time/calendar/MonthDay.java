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

import javax.time.MathUtils;
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;
import javax.time.period.Periods;

/**
 * A calendrical representation of a month and day without a time zone.
 * <p>
 * MonthDay is an immutable calendrical that represents a month-day combination.
 * This class does not store or represent a year, time or time zone.
 * Thus, for example, the value "2nd October" can be stored in a MonthDay.
 * <p>
 * Static factory methods allow you to constuct instances.
 * <p>
 * MonthDay is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class MonthDay
        implements Calendrical, Comparable<MonthDay>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -254395108L;

    /**
     * The month of year being represented.
     */
    private final int month;
    /**
     * The day of month being represented.
     */
    private final int day;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MonthDay</code>.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a MonthDay object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static MonthDay monthDay(MonthOfYear monthOfYear, int dayOfMonth) {
        return monthDay(monthOfYear.getValue(), dayOfMonth);
    }

    /**
     * Obtains an instance of <code>MonthDay</code>.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a MonthDay object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static MonthDay monthDay(int monthOfYear, int dayOfMonth) {
        ISOChronology.INSTANCE.checkValidDate(2000, monthOfYear, dayOfMonth);
        return new MonthDay(monthOfYear, dayOfMonth);
    }

    /**
     * Obtains an instance of <code>MonthDay</code> from a set of calendricals.
     * <p>
     * This can be used to pass in any combination of calendricals that fully specify
     * a calendar day. For example, MonthOfYear + DayOfMonth.
     *
     * @param calendricals  a set of calendricals that fully represent a calendar day
     * @return a MonthDay object, never null
     */
    public static MonthDay monthDay(Calendrical... calendricals) {
        // TODO
        return new MonthDay(0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param monthOfYear  the month of year to represent
     * @param dayOfMonth  the day of month to represent
     */
    private MonthDay(int monthOfYear, int dayOfMonth) {
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    /**
     * Returns a copy of this month-day with the new month and day, checking
     * to see if a new object is in fact required.
     *
     * @param newMonth  the month of year to represent, from 1 (January) to 12 (December)
     * @param newDay  the day of month to represent, from 1 to 31
     * @return the month-day, never null
     */
    private MonthDay withMonthDay(int newMonth, int newDay) {
        if (month == newMonth && day == newDay) {
            return this;
        }
        return new MonthDay(newMonth, newDay);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to this
     * instance.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>MonthDay</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return field.isSupported(Periods.DAYS, Periods.YEARS);
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
            throw new UnsupportedCalendarFieldException(field, "month-day");
        }
        if (field == ISOChronology.INSTANCE.monthOfYearRule()) {
            return month;
        }
        if (field == ISOChronology.INSTANCE.dayOfMonthRule()) {
            return day;
        }
        return field.getValue(getCalendricalState());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>MonthOfYear</code> initialised to the
     * month of this month-day.
     *
     * @return the month object, never null
     */
    public MonthOfYear monthOfYear() {
        return MonthOfYear.monthOfYear(month);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month of year value.
     * <p>
     * This method returns the numerical value for the month, from 1 to 12.
     * The enumerated constant is returned by {@link #monthOfYear()}.
     *
     * @return the month of year, from 1 (January) to 12 (December)
     */
    public int getMonthOfYear() {
        return month;
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return day;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MonthDay with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated MonthDay, never null
     */
    public MonthDay with(Calendrical calendrical) {
        if (calendrical instanceof MonthOfYear) {
            return withMonthDay(((MonthOfYear) calendrical).getValue(), day);
        }
        if (calendrical instanceof MonthDay) {
            return (MonthDay) calendrical;
        }
        // TODO
        return null;
    }

    /**
     * Returns a copy of this MonthDay with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated MonthDay, never null
     */
    public MonthDay with(Calendrical... calendricals) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MonthDay with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated MonthDay, never null
     */
    public MonthDay withMonthOfYear(int monthOfYear) {
        int[] resolved = CalendricalResolvers.previousValid().resolveDate(2000, monthOfYear, day);
        return withMonthDay(resolved[1], resolved[2]);
    }

    /**
     * Returns a copy of this MonthDay with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated MonthDay, never null
     */
    public MonthDay withDayOfMonth(int dayOfMonth) {
        ISOChronology.INSTANCE.checkValidDate(2000, month, dayOfMonth);
        return withMonthDay(month, dayOfMonth);
    }

    /**
     * Returns a copy of this MonthDay with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated MonthDay, never null
     */
    public MonthDay withLastDayOfMonth() {
        int dom = ISOChronology.INSTANCE.getMonthLength(2000, month);
        return withMonthDay(month, dom);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MonthDay with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated MonthDay, never null
     */
    public MonthDay plus(PeriodView period) {
        // TODO
        return null;
    }

    /**
     * Returns a copy of this MonthDay with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated MonthDay, never null
     */
    public MonthDay plus(PeriodView... periods) {
        // TODO
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this MonthDay with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add
     * @return a new updated MonthDay, never null
     */
    public MonthDay plusMonths(int months) {
        if (months == 0) {
            return this;
        }
        long newMonth0 = month - 1;
        newMonth0 = newMonth0 + months;
        newMonth0 = newMonth0 % 12;
        if (newMonth0 < 0) {
            newMonth0 += 12;
        }
        return withMonthOfYear((int) ++newMonth0);
    }

    /**
     * Returns a copy of this MonthDay with the specified number of weeks added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add
     * @return a new updated MonthDay, never null
     */
    public MonthDay plusWeeks(int weeks) {
        // TODO: What about leap years
        return null;
    }

    /**
     * Returns a copy of this MonthDay with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add
     * @return a new updated MonthDay, never null
     */
    public MonthDay plusDays(int days) {
        // TODO: What about leap years
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this month-day to another month-day.
     *
     * @param other  the other month-day to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(MonthDay other) {
        int cmp = MathUtils.safeCompare(month, other.month);
        if (cmp == 0) {
            cmp = MathUtils.safeCompare(day, other.day);
        }
        return cmp;
    }

    /**
     * Is this month-day after the specified month-day.
     *
     * @param other  the other month-day to compare to, not null
     * @return true if this is after the specified month-day
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(MonthDay other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this month-day before the specified month-day.
     *
     * @param other  the other month-day to compare to, not null
     * @return true if this point is before the specified month-day
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(MonthDay other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this month-day equal to the specified month-day.
     *
     * @param other  the other month-day to compare to, null returns false
     * @return true if this point is equal to the specified month-day
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MonthDay) {
            MonthDay otherMD = (MonthDay) other;
            return month == otherMD.month && day == otherMD.day;
        }
        return false;
    }

    /**
     * A hashcode for this month-day.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return (month << 6) + day;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the month-day as a <code>String</code>.
     * <p>
     * The output will be in the format '"XXXX"-MM-dd':
     *
     * @return the string form of the month-day
     */
    @Override
    public String toString() {
        return new StringBuilder(10).append("XXXX-")
            .append(month < 10 ? "-0" : "-").append(month)
            .append(day < 10 ? "-0" : "-").append(day)
            .toString();
    }

}
