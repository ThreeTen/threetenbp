/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.CalendricalRule;
import javax.time.calendar.Chronology;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.LocalDate;

/**
 * A standard calendar system formed of eras, years, months and days.
 * <p>
 * Most calendar systems are formed of common elements - eras, years, months and days.
 * This class provides a common way to access this in association with {@link ChronologyDate}.
 * See the individual methods for descriptions of the standard concepts.
 * <p>
 * Note that not all calendar systems are standard.
 * For example, the Mayan calendar has a completely different way of counting.
 * <p>
 * This is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 * Wherever possible subclasses should be singletons with no public constructor.
 * It is recommended that subclasses implement {@code Serializable}
 *
 * @author Stephen Colebourne
 */
public abstract class StandardChronology extends Chronology {

    /**
     * Restrictive constructor.
     */
    protected StandardChronology() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendar system era from an ISO-8601 date.
     * <p>
     * The era is a positive or negative value not counted within any other value.
     * In a standard chronology, the era in use at 1970-01-01 has the value 1.
     * Later eras have sequentially higher values.
     * Earlier eras have sequentially lower values.
     * Each chronology should have constants providing meaning to the era value.
     *
     * @return the calendar system era, not null
     */
    public abstract int getEra(LocalDate date);

    /**
     * Gets the calendar system year-of-era from an ISO-8601 date.
     * <p>
     * The year-of-era is the positive count of years within the era where points later on
     * the time-line have a greater value.
     * An era is not necessarily formed of an integral number of years, but will be
     * formed of an integral number of days.
     *
     * @return the calendar system year-of-era, not null
     */
    public abstract int getYearOfEra(LocalDate date);

    /**
     * Gets the calendar system month-of-year from an ISO-8601 date.
     * <p>
     * The month-of-year is the positive count of months within the year where points later on
     * the time-line have a greater value.
     * In a standard chronology, there will be an integral number of months in a year.
     * The number of months in a year may vary.
     *
     * @return the calendar system month-of-year, not null
     */
    public abstract int getMonthOfYear(LocalDate date);

    /**
     * Gets the calendar system day-of-month from an ISO-8601 date.
     * <p>
     * The day-of-month is the positive count of days within the month where points later on
     * the time-line have a greater value.
     * In a standard chronology, there will be an integral number of days in a month.
     * The number of days in a month may vary.
     *
     * @return the calendar system day-of-month, not null
     */
    public abstract int getDayOfMonth(LocalDate date);

    /**
     * Gets the calendar system day-of-year from an ISO-8601 date.
     * <p>
     * The day-of-year is the positive count of days within the year where points later on
     * the time-line have a greater value.
     * In a standard chronology, there will be an integral number of days in a year.
     * The number of days in a year may vary.
     *
     * @return the calendar system day-of-year, not null
     */
    public abstract int getDayOfYear(LocalDate date);

    /**
     * Gets the day-of-week from an ISO-8601 date.
     * <p>
     * In a standard chronology, the day-of-week is identical to that in ISO-8601.
     *
     * @return the calendar system day-of-year, not null
     */
    public final DayOfWeek getDayOfWeek(LocalDate date) {
        return date.getDayOfWeek();
    }

    /**
     * Gets the calendar system proleptic-year from an ISO-8601 date.
     * <p>
     * This extracts the calendar system proleptic year.
     * The proleptic-year is a single value that represents the era and year-of-era.
     * It should normally be the same as the year-of-era for the era that is active on 1970-01-01.
     *
     * @return the calendar system proleptic-year, not null
     */
    public abstract int getProlepticYear(LocalDate date);

    /**
     * Checks if the specified date is in a leap year.
     *
     * @param date  the date to check, not null
     * @return true if the date is in a leap year
     */
    public abstract boolean isLeapYear(LocalDate date);

    //-----------------------------------------------------------------------
    /**
     * Merges the era, year-of-era, month-of-year and day-of-month fields to form a date.
     * 
     * @param era  the calendar system era
     * @param year  the calendar system year-of-era
     * @param monthOfYear  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return
     */
    public abstract LocalDate merge(int era, int year, int monthOfYear, int dayOfMonth);

    /**
     * Gets a calendrical rule for the {@code ChronologyDate}.
     * 
     * @return the rule, not null
     */
    public abstract CalendricalRule<ChronologyDate> dateRule();

}
