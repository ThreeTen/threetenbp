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

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.Year;

/**
 * A representation of a day-of-year in the ISO-8601 calendar system.
 * <p>
 * DayOfYear is an immutable time field that can only store a day-of-year.
 * It is a type-safe way of representing a day-of-year in an application.
 * <p>
 * Static factory methods allow you to construct instances.
 * The day-of-year may be queried using getValue().
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class DayOfYear
        implements Comparable<DayOfYear>, DateTimeAdjuster, Serializable {

    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Cache of singleton instances.
     */
    private static final AtomicReferenceArray<DayOfYear> CACHE = new AtomicReferenceArray<DayOfYear>(366);

    /**
     * The day-of-year being represented.
     */
    private final int dayOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code DayOfYear}.
     *
     * @param dayOfYear  the day-of-year to represent, from 1 to 366
     * @return the day-of-year, not null
     * @throws CalendricalException if the day-of-year is invalid
     */
    public static DayOfYear of(int dayOfYear) {
        try {
            DayOfYear result = CACHE.get(--dayOfYear);
            if (result == null) {
                DayOfYear temp = new DayOfYear(dayOfYear + 1);
                CACHE.compareAndSet(dayOfYear, null, temp);
                result = CACHE.get(dayOfYear);
            }
            return result;
        } catch (IndexOutOfBoundsException ex) {
            throw new CalendricalException("Invalid value for DayOfYear: " + ++dayOfYear);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code DayOfYear} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code DayOfYear}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the day-of-year, not null
     * @throws CalendricalException if unable to convert to a {@code DayOfYear}
     */
    public static DayOfYear from(DateTime calendrical) {
        LocalDate date = LocalDate.from(calendrical);
        return DayOfYear.of(date.getDayOfYear());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified day-of-year.
     *
     * @param dayOfYear  the day-of-year to represent
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
        return of(dayOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field that defines how the day-of-year field operates.
     * <p>
     * The field provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the day-of-year field, never null
     */
    public DateTimeField getField() {
        return LocalDateTimeField.DAY_OF_YEAR;
    }

    /**
     * Gets the day-of-year value.
     *
     * @return the day-of-year, from 1 to 366
     */
    public int getValue() {
        return dayOfYear;
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts a date to have the value of this day-of-year, returning a new date.
     * <p>
     * If the day-of-year is invalid for the year and month then an exception
     * is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to be adjusted, not null
     * @return the adjusted date, never null
     * @throws CalendricalException if the day-of-year is invalid for the input year
     */
    @Override
    public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
        return calendrical.with(DAY_OF_YEAR, dayOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this day-of-year is valid for the specified year.
     *
     * @param year  the year to validate against, not null
     * @return true if this day-of-year is valid for the year
     */
    public boolean isValid(Year year) {
        if (year == null) {
            throw new NullPointerException("Year must not be null");
        }
        return (dayOfYear < 366 || year.isLeap());
    }

    /**
     * Checks if this day-of-year is valid for the specified year.
     *
     * @param year  the year to validate against, from MIN_YEAR to MAX_YEAR
     * @return true if this day-of-year is valid for the year
     * @throws CalendricalException if the year is out of range
     */
    public boolean isValid(int year) {
        YEAR.checkValidValue(year);
        return (dayOfYear < 366 || Year.isLeap(year));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a date formed from this day-of-year at the specified year.
     * <p>
     * This merges the two objects - <code>this</code> and the specified year -
     * to form an instance of <code>LocalDate</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to use, not null
     * @return the local date formed from this day and the specified year, never null
     * @throws CalendricalException if the day does not occur in the year
     */
    public LocalDate atYear(Year year) {
        if (year == null) {
            throw new NullPointerException("Year must not be null");
        }
        return year.atDay(dayOfYear);
    }

    /**
     * Returns a date formed from this day-of-year at the specified year.
     * <p>
     * This merges the two objects - <code>this</code> and the specified year -
     * to form an instance of <code>LocalDate</code>.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to use, from MIN_YEAR to MAX_YEAR
     * @return the local date formed from this day and the specified year, never null
     * @throws CalendricalException if the day does not occur in the year
     */
    public LocalDate atYear(int year) {
        return atYear(Year.of(year));
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this day-of-year instance to another.
     *
     * @param otherDayOfYear  the other day-of-year instance, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherDayOfYear is null
     */
    public int compareTo(DayOfYear otherDayOfYear) {
        return DateTimes.safeCompare(this.dayOfYear, otherDayOfYear.dayOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified, evaluating the day-of-year.
     *
     * @param otherDayOfYear  the other day-of-year instance, null returns false
     * @return true if the day-of-year is the same
     */
    @Override
    public boolean equals(Object otherDayOfYear) {
        return this == otherDayOfYear;
    }

    /**
     * A hash code for the day-of-year object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return dayOfYear;
    }

    /**
     * A string describing the day-of-year object.
     *
     * @return a string describing this object
     */
    @Override
    public String toString() {
        return "DayOfYear=" + getValue();
    }

}
