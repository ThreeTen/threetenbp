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

import java.io.Serializable;

import javax.time.MathUtils;
import javax.time.calendar.LocalDate;

/**
 * The Coptic calendar system.
 * <p>
 * This {@link StandardChronology standard} chronology defines the rules of the Coptic calendar system.
 * The Coptic calendar has twelve months of 30 days followed by an additional
 * period of 5 or 6 days, modeled as the thirteenth month in this implementation.
 * <p>
 * Years are measured in the 'Era of the Martyrs' - AM.
 * 0001-01-01 (Coptic) equals 0284-08-29 (ISO).
 * The supported range is from 1 to 99999999 (inclusive) in both eras.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class CopticStandardChronology extends StandardChronology implements Serializable {

    /**
     * The singleton instance of {@code JulianChronology}.
     */
    public static final CopticStandardChronology INSTANCE = new CopticStandardChronology();
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The number of days to add to MJD to get the Coptic epoch day.
     */
    private static final int MJD_TO_COPTIC = 574971;

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private CopticStandardChronology() {
    }

    /**
     * Resolves singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology, 'Coptic'.
     *
     * @return the name of the chronology, not null
     */
    @Override
    public String getName() {
        return "Coptic";
    }

    @Override
    public ChronologyDate createDate(LocalDate date) {
        ChronologyDate.checkNotNull(date, "LocalDate must not be null");
        long epochDay = date.toEpochDay();
        int prolepticYear = (int) (((epochDay * 4) + 1463) / 1461);
        int startYearEpochDay = (prolepticYear - 1) * 365 + (prolepticYear / 4);
        int doy0 = (int) (epochDay - startYearEpochDay);
        int month = doy0 / 30 + 1;
        int dom = doy0 % 30 + 1;
        return buildDate(date, prolepticYear, month, dom);
    }

    @Override
    public ChronologyDate createDate(int prolepticYear, int monthOfYear, int dayOfMonth) {
        int doy0 = (monthOfYear - 1) * 30 + dayOfMonth - 1;
        long epochDay = ((long) prolepticYear) * 365 + ((long) prolepticYear) / 4 + doy0;
        LocalDate date = LocalDate.ofModifiedJulianDay(epochDay - MJD_TO_COPTIC);
        return buildDate(date, prolepticYear, monthOfYear, dayOfMonth);
    }

    @Override
    public int getDayOfYear(ChronologyDate date) {
        return (date.getMonthOfYear() - 1) * 30 + date.getDayOfMonth();
    }

    @Override
    public boolean isLeapYear(ChronologyDate date) {
        return MathUtils.floorMod(date.getProlepticYear(), 4) == 3;
    }

    //-----------------------------------------------------------------------
    @Override
    public Era createEra(int eraValue) {
        return CopticEra.of(eraValue);
    }

}
