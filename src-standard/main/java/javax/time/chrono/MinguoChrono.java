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

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.calendrical.CalendricalObject;

/**
 * The Minguo calendar system.
 * <p>
 * This chronology defines the rules of the Minguo calendar system.
 * This calendar system is primarily used in the Republic of China, often known as Taiwan.
 * Dates are aligned such that {@code 0001AM-01-01 (Minguo)} is {@code 0284-08-29 (ISO)}.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, the current 'Republic' (ROC) and the previous era (BEFORE_ROC).
 * <li>year-of-era - The year-of-era is the same as the proleptic-year for the current ROC era.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current ROC era and is equal to the ISO year minus 1911.
 * <li>month-of-year - The Minguo month-of-year exactly matches ISO.
 * <li>day-of-month - The Minguo day-of-month exactly matches ISO.
 * <li>day-of-year - The Minguo day-of-year exactly matches ISO.
 * <li>leap-year - The Minguo leap-year pattern exactly matches ISO.
 * </ul>
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class MinguoChrono extends Chrono implements Serializable {

    /**
     * Singleton instance.
     */
    public static final MinguoChrono INSTANCE = new MinguoChrono();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The difference in years between ISO and Minguo.
     */
    static final int YEARS_DIFFERENCE = 1911;

    /**
     * Restricted constructor.
     */
    private MinguoChrono() {
    }

    /**
     * Resolve singleton.
     * 
     * @return the singleton instance, not null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return "Minguo";
    }

    //-----------------------------------------------------------------------
    @Override
    public MinguoDate date(Era era, int yearOfEra, int monthOfYear, int dayOfMonth) {
        if (era instanceof MinguoEra) {
            throw new CalendricalException("Era must be a MinguoEra");
        }
        return date(prolepticYear((MinguoEra) era, yearOfEra), monthOfYear, dayOfMonth);
    }

    @Override
    public MinguoDate date(int prolepticYear, int monthOfYear, int dayOfMonth) {
        return new MinguoDate(LocalDate.of(prolepticYear - YEARS_DIFFERENCE, monthOfYear, dayOfMonth));
    }

    @Override
    public MinguoDate date(CalendricalObject calendrical) {
        if (calendrical instanceof MinguoDate) {
            return (MinguoDate) calendrical;
        }
        return new MinguoDate(LocalDate.from(calendrical));
    }

    @Override
    public MinguoDate dateFromEpochDay(long epochDay) {
        return new MinguoDate(LocalDate.ofEpochDay(epochDay));
    }

    @Override
    public MinguoDate now() {
        return (MinguoDate) super.now();
    }

    /**
     * Checks if the specified year is a leap year.
     * <p>
     * Minguo leap years occur exactly in line with ISO leap years.
     * This method does not validate the year passed in, and only has a
     * well-defined result for years in the supported range.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return DateTimes.isLeapYear(prolepticYear + YEARS_DIFFERENCE);
    }

    @Override
    public MinguoEra createEra(int eraValue) {
        return MinguoEra.of(eraValue);
    }

    private static int prolepticYear(MinguoEra era, int yearOfEra) {
        return (era == MinguoEra.ROC ? yearOfEra : 1 - yearOfEra);
    }

}
