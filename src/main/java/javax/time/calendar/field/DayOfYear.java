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
import javax.time.calendar.DateAdjustor;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.FlexiDateTime;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.DateProvider;

/**
 * A representation of a day of year in the ISO-8601 calendar system.
 * <p>
 * DayOfYear is an immutable time field that can only store a day of year.
 * It is a type-safe way of representing a day of year in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The day of year may be queried using getValue().
 * <p>
 * DayOfYear is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DayOfYear
        implements Calendrical, Comparable<DayOfYear>, Serializable, DateAdjustor, DateMatcher {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Cache of singleton instances.
     */
    private static final AtomicReferenceArray<DayOfYear> cache = new AtomicReferenceArray<DayOfYear>(366);
    /**
     * The start of months in a standard year.
     */
    private static final int[] STANDARD_MONTH_START = new int[] {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    /**
     * The start of months in a leap year.
     */
    private static final int[] LEAP_MONTH_START = new int[] {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    /**
     * The day of year being represented.
     */
    private final int dayOfYear;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the day of year field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the day of year rule, never null
     */
    public static DateTimeFieldRule rule() {
        return ISOChronology.INSTANCE.dayOfYear();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>DayOfYear</code> from a value.
     * <p>
     * A day of year object represents one of the 366 days of the year, from
     * 1 to 366. These are cached internally and returned as singletons, so
     * they can be compared using ==.
     *
     * @param dayOfYear  the day of year to represent, from 1 to 366
     * @return the DayOfYear singleton, never null
     * @throws IllegalCalendarFieldValueException if the dayOfYear is invalid
     */
    public static DayOfYear dayOfYear(int dayOfYear) {
        try {
            DayOfYear result = cache.get(--dayOfYear);
            if (result == null) {
                DayOfYear temp = new DayOfYear(dayOfYear + 1);
                cache.compareAndSet(dayOfYear, null, temp);
                result = cache.get(dayOfYear);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalCalendarFieldValueException("DayOfYear", dayOfYear, 1, 366);
        }
    }

    /**
     * Obtains an instance of <code>DayOfYear</code> from a date provider.
     * <p>
     * This can be used extract a day of year object directly from any implementation
     * of DateProvider, including those in other calendar systems.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the DayOfWeek singleton, never null
     */
    public static DayOfYear dayOfYear(DateProvider dateProvider) {
        LocalDate date = dateProvider.toLocalDate();
        int moy0 = date.getMonthOfYear().ordinal();
        int dom = date.getDayOfMonth().getValue();
        if (date.getYear().isLeap()) {
            return dayOfYear(LEAP_MONTH_START[moy0] + dom);
        } else {
            return dayOfYear(STANDARD_MONTH_START[moy0] + dom);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified day of year.
     *
     * @param dayOfYear  the day of year to represent
     */
    private DayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    /**
     * Resolve the singleton.
     *
     * @return the singleton, never null
     */
    private Object readResolve() {
        return dayOfYear(dayOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the day of year value.
     *
     * @return the day of year, from 1 to 366
     */
    public int getValue() {
        return dayOfYear;
    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the month that this day falls in given a year.
//     *
//     * @param year  the year that the day of year occurs in, not null
//     * @return the month, never null
//     * @throws IllegalCalendarFieldValueException if the day does not occur in the year
//     */
//    public MonthOfYear getMonthOfYear(Year year) {
//        if (isValid(year) == false) {
//            throw new IllegalCalendarFieldValueException("DayOfYear 366 is invalid for year " + year);
//        }
//        int doy0 = dayOfYear - 1;
//        int[] array = (year.isLeap() ? LEAP_MONTH_START : STANDARD_MONTH_START);
//        int month = 1;
//        for ( ; month < 12; month++) {
//            if (doy0 < array[month]) {
//                break;
//            }
//        }
//        return MonthOfYear.monthOfYear(month);
//    }
//
//    /**
//     * Gets the day of month that this day falls in given a year.
//     *
//     * @param year  the year that the day of year occurs in, not null
//     * @return the day of month, never null
//     * @throws IllegalCalendarFieldValueException if the day does not occur in the year
//     */
//    public DayOfMonth getDayOfMonth(Year year) {
//        if (isValid(year) == false) {
//            throw new IllegalCalendarFieldValueException("DayOfYear 366 is invalid for year " + year);
//        }
//        int doy0 = dayOfYear - 1;
//        int[] array = (year.isLeap() ? LEAP_MONTH_START : STANDARD_MONTH_START);
//        int month = 1;
//        for ( ; month < 12; month++) {
//            if (doy0 < array[month]) {
//                break;
//            }
//        }
//        return DayOfMonth.dayOfMonth(dayOfYear - array[month - 1]);
//    }

    //-----------------------------------------------------------------------
    /**
     * Creates a date using this day of year and the given year.
     *
     * @param year  the year that the day of year occurs in, not null
     * @return the date, never null
     * @throws IllegalCalendarFieldValueException if the day does not occur in the year
     */
    public LocalDate createDate(Year year) {
        if (isValid(year) == false) {
            throw new IllegalCalendarFieldValueException("DayOfYear 366 is invalid for year " + year);
        }
        int doy0 = dayOfYear - 1;
        int[] array = (year.isLeap() ? LEAP_MONTH_START : STANDARD_MONTH_START);
        int month = 1;
        for ( ; month < 12; month++) {
            if (doy0 < array[month]) {
                break;
            }
        }
        MonthOfYear moy = MonthOfYear.monthOfYear(month);
        DayOfMonth dom = DayOfMonth.dayOfMonth(dayOfYear - array[month - 1]);
        return LocalDate.date(year, moy, dom);
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
     * Adjusts a date to have the value of this day of year, returning a new date.
     * <p>
     * If the day of year is invalid for the year and month then an exception
     * is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     * @throws IllegalCalendarFieldValueException if the day of year is invalid for the input year
     */
    public LocalDate adjustDate(LocalDate date) {
        return createDate(date.getYear());
    }

    /**
     * Checks if the value of this day of year matches the input date.
     *
     * @param date  the date to match, not null
     * @return true if the date matches, false otherwise
     */
    public boolean matchesDate(LocalDate date) {
        return date.getDayOfYear() == this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this day of year is valid for the specified year.
     *
     * @param year  the year to validate against, not null
     * @return true if this day of year is valid for the year
     */
    public boolean isValid(Year year) {
        if (year == null) {
            throw new NullPointerException("The year must not be null");
        }
        return (dayOfYear < 366 || year.isLeap());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this day of year instance to another.
     *
     * @param otherDayOfYear  the other day of year instance, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDayOfYear is null
     */
    public int compareTo(DayOfYear otherDayOfYear) {
        int thisValue = this.dayOfYear;
        int otherValue = otherDayOfYear.dayOfYear;
        return (thisValue < otherValue ? -1 : (thisValue == otherValue ? 0 : 1));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the day of year.
     *
     * @param otherDayOfYear  the other day of year instance, null returns false
     * @return true if the day of year is the same
     */
    @Override
    public boolean equals(Object otherDayOfYear) {
        if (this == otherDayOfYear) {
            return true;
        }
        if (otherDayOfYear instanceof DayOfYear) {
            return dayOfYear == ((DayOfYear) otherDayOfYear).dayOfYear;
        }
        return false;
    }

    /**
     * A hashcode for the day of year object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return dayOfYear;
    }

    /**
     * A string describing the day of year object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "DayOfYear=" + getValue();
    }

}
