/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.builder;

import javax.time.LocalDate;
import javax.time.MathUtils;
import javax.time.chronology.ChronologyDate;
import javax.time.chronology.Era;

/**
 * A calendar system.
 * 
 * @author Stephen Colebourne
 */
public abstract class Chrono {

    public abstract String getName();

    public abstract int getField(ChronoField field, ChronoDate<?> chronoDate);

    //-----------------------------------------------------------------------
    /**
     * Creates a date from the era, year-of-era, month-of-year and day-of-month fields.
     * <p>
     * This is the only way to create a {@code ChronoDate}.
     * The values passed to this method must be valid.
     * They are not validated further before constructing the date object.
     * 
     * @param prolepticYear  the proleptic-year to represent, within the valid range for the chronology
     * @param monthOfYear  the month-of-year to represent, within the valid range for the chronology
     * @param dayOfMonth  the day-of-month to represent, within the valid range for the chronology
     * @return the date in this calendar system, not null
     */
    protected ChronoDate buildDate(int prolepticYear, int monthOfYear, int dayOfMonth) {
        return new ChronoDate(this, prolepticYear, monthOfYear, dayOfMonth);
    }

    /**
     * Creates a date from the ISO equivalent local date.
     * 
     * @param date  ISO equivalent local date, not null
     * @return the date in this calendar system, not null
     */
    public abstract ChronologyDate createDate(LocalDate date);

    /**
     * Creates a date from the proleptic-year, month-of-year and day-of-month fields.
     * 
     * @param prolepticYear  the calendar system proleptic-year
     * @param monthOfYear  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date in this calendar system, not null
     */
    public abstract ChronologyDate createDate(int prolepticYear, int monthOfYear, int dayOfMonth);

    /**
     * Creates a date from the era, year-of-era, month-of-year and day-of-month fields.
     * <p>
     * The default implementation uses {@link #getProlepticYear(Era, int)} and
     * {@link #createDate(int, int, int)}.
     * 
     * @param era  the calendar system era, not null
     * @param year  the calendar system year-of-era
     * @param monthOfYear  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date in this calendar system, not null
     */
    public ChronologyDate createDate(Era era, int year, int monthOfYear, int dayOfMonth) {
        return createDate(getProlepticYear(era, year), monthOfYear, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the proleptic-year from an era and year-of-era.
     * <p>
     * The proleptic-year is a single value representing the year.
     * It combines the era and year-of-era, and increases uniformly as time progresses.
     * The exact meaning is calendar system specific according to the following constraints.
     * <p>
     * The proleptic-year has a small, or negative, value in the past.
     * Later years have sequentially higher values.
     * Where possible, the proleptic-year will be the same as the year-of-era
     * for the era that is active on 1970-01-01 however this is not guaranteed.
     * <p>
     * The default implementation assumes two eras.
     * For the era with the value 1, the year-of-era increases sequentially with later dates.
     * For the era with the value 0, the year-of-era increases sequentially with earlier dates.
     * Year 1 of era 0 immediately precedes year 1 of era 1.
     * This definition matches most calendar systems with a single epoch and two eras.
     * If the calendar system has a different era or year-of-era definition then this
     * method must be overridden.
     *
     * @param era  the era to use, not null
     * @param yearOfEra  the year-of-era, may be out of range
     * @return the calendar system proleptic-year
     */
    public int getProlepticYear(Era era, int yearOfEra) {
        MathUtils.checkNotNull(era, "Era must not be null");
        return (era.getValue() == 1 ? yearOfEra : 1 - yearOfEra);
    }

    /**
     * Calculates the era from a date in this calendar system.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The era in use at 1970-01-01 must have the value 1.
     * Later eras must have sequentially higher values.
     * Earlier eras must have sequentially lower values.
     * Each chronology must refer to an enum or similar singleton to provide the era values.
     * <p>
     * The default implementation assumes two eras.
     * For the era with the value 1, the year-of-era increases sequentially with later dates.
     * For the era with the value 0, the year-of-era increases sequentially with earlier dates.
     * Year 1 of era 0 immediately precedes year 1 of era 1.
     * This definition matches most calendar systems with a single epoch and two eras.
     * If the calendar system has a different era or year-of-era definition then this
     * method must be overridden.
     *
     * @param date  the date to check in this calendar system, not null
     * @return the calendar system era, not null
     */
    public Era getEra(ChronoDate date) {
        int year = date.getProlepticYear();
        return (year > 0 ? createEra(1) : createEra(0));
    }

    /**
     * Calculates the year-of-era from a date in this calendar system.
     * <p>
     * The year-of-era is a value representing the count of years within the era.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The year-of-era value must be positive.
     * <p>
     * The default implementation assumes two eras.
     * For the era with the value 1, the year-of-era increases sequentially with later dates.
     * For the era with the value 0, the year-of-era increases sequentially with earlier dates.
     * Year 1 of era 0 immediately precedes year 1 of era 1.
     * This definition matches most calendar systems with a single epoch and two eras.
     * If the calendar system has a different era or year-of-era definition then this
     * method must be overridden.
     *
     * @param date  the date to check in this calendar system, not null
     * @return the calendar system year-of-era
     */
    public int getYearOfEra(ChronologyDate date) {
        int year = date.getProlepticYear();
        return (year > 0 ? year : 1 - year);
    }

    /**
     * Calculates the day-of-year from a date in this calendar system.
     * <p>
     * The day-of-year is a value representing the count of days within the year.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The day-of-year value must be positive.
     * The number of days in a year may vary.
     *
     * @param date  the date to check in this calendar system, not null
     * @return the calendar system day-of-year
     */
    public abstract int getDayOfYear(ChronologyDate date);

    /**
     * Checks if the specified date in this calendar system is in a leap year.
     * <p>
     * A leap-year is a year of a longer length than normal.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * A leap-year must imply a year-length longer than a non leap-year.
     *
     * @param date  the date to check in this calendar system, not null
     * @return true if the date is in a leap year
     */
    public abstract boolean isLeapYear(ChronologyDate date);

    //-----------------------------------------------------------------------
    /**
     * Creates the calendar system era object from the numeric value.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The era in use at 1970-01-01 must have the value 1.
     * Later eras must have sequentially higher values.
     * Earlier eras must have sequentially lower values.
     * Each chronology must refer to an enum or similar singleton to provide the era values.
     * <p>
     * This method returns the singleton era of the correct type for the specified era value.
     *
     * @return the calendar system era, not null
     */
    public abstract Era createEra(int eraValue);

}
