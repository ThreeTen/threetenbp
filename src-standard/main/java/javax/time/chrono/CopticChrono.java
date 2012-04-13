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

import javax.time.DateTimes;
import javax.time.builder.CalendricalObject;

/**
 * The Coptic calendar system.
 * <p>
 * This chronology defines the rules of the Coptic calendar system.
 * The Coptic calendar has twelve months of 30 days followed by an additional
 * period of 5 or 6 days, modeled as the thirteenth month in this implementation.
 * <p>
 * Years are measured in the 'Era of the Martyrs' - AM.
 * 0001-01-01 (Coptic) equals 0284-08-29 (ISO).
 * The supported range is from 1 to 99999999 (inclusive) in both eras.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class CopticChrono extends Chrono implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Singleton instance.
     */
    public static final CopticChrono INSTANCE = new CopticChrono();

    /**
     * Restricted constructor.
     */
    private CopticChrono() {
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
        return "Coptic";
    }

    //-----------------------------------------------------------------------
    @Override
    public CopticDate createDate(Era era, int yearOfEra, int monthOfYear, int dayOfMonth) {
        return CopticDate.of((CopticEra) era, yearOfEra, monthOfYear, dayOfMonth);  // TODO check cast
    }

    @Override
    public CopticDate createDate(int prolepticYear, int monthOfYear, int dayOfMonth) {
        return CopticDate.of(prolepticYear, monthOfYear, dayOfMonth);
    }

    @Override
    public CopticDate createDate(CalendricalObject calendrical) {
        return CopticDate.from(calendrical);
    }

    @Override
    public CopticDate now() {
        return (CopticDate) super.now();
    }

    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A Coptic proleptic-year is leap if the remainder after division by four equals three.
     * This method does not validate the year passed in, and only has a
     * well-defined result for years in the supported range.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return DateTimes.floorMod(prolepticYear, 4) == 3;
    }

    @Override
    public CopticEra createEra(int eraValue) {
        return CopticEra.of(eraValue);
    }

}
