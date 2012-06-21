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
package javax.time.chrono;

import javax.time.LocalDate;
import javax.time.calendrical.CalendricalObject;

/**
 * A calendar system.
 * 
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * Subclasses should be Serializable wherever possible.
 */
public abstract class Chrono {

    /**
     * Gets the name of the calendar system.
     * 
     * @return the name, not null
     */
    public abstract String getName();

    //-----------------------------------------------------------------------
    /**
     * Creates a date in this calendar system from the era, year-of-era, month-of-year and day-of-month fields.
     * 
     * @param era  the calendar system era of the correct type, not null
     * @param yearOfEra  the calendar system year-of-era
     * @param monthOfYear  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date in this calendar system, not null
     */
    public abstract ChronoDate date(Era era, int yearOfEra, int monthOfYear, int dayOfMonth);

    /**
     * Creates a date in this calendar system from the proleptic-year, month-of-year and day-of-month fields.
     * 
     * @param prolepticYear  the calendar system proleptic-year
     * @param monthOfYear  the calendar system month-of-year
     * @param dayOfMonth  the calendar system day-of-month
     * @return the date in this calendar system, not null
     */
    public abstract ChronoDate date(int prolepticYear, int monthOfYear, int dayOfMonth);

    /**
     * Creates a date in this calendar system from another calendrical object.
     * 
     * @param calendrical  the other calendrical, not null
     * @return the date in this calendar system, not null
     */
    public abstract ChronoDate date(CalendricalObject calendrical);

    /**
     * Creates a date in this calendar system from the epoch day from 1970-01-01 (ISO).
     * 
     * @param epochDay  the epoch day measured from 1970-01-01 (ISO), not null
     * @return the date in this calendar system, not null
     */
    public abstract ChronoDate dateFromEpochDay(long epochDay);

    /**
     * Creates the current date in this calendar system.
     * 
     * @return the current date in this calendar system, not null
     */
    public ChronoDate now() {
        return dateFromEpochDay(LocalDate.now().toEpochDay());
    }

    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A leap-year is a year of a longer length than normal.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * A leap-year must imply a year-length longer than a non leap-year.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    public abstract boolean isLeapYear(long prolepticYear);

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
     * @param eraValue  the era value
     * @return the calendar system era, not null
     */
    public abstract Era createEra(int eraValue);

}
