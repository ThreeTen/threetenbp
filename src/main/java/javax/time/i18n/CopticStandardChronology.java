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
import javax.time.calendar.LocalDate;

/**
 * The Coptic calendar system.
 * <p>
 * {@code CopticChronology} defines the rules of the Coptic calendar system.
 * The Coptic calendar has twelve months of 30 days followed by a thirteenth month
 * of 5 days, or 6 days in a leap year. Leap years occur every 4 years without fail.
 * <p>
 * Years are measured in the 'Era of the Martyrs'.
 * 0001-01-01 (Coptic) equals 0284-08-29 (ISO).
 * <p>
 * This class is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class CopticStandardChronology extends StandardChronology {

    /**
     * The singleton instance.
     */
    public static final CopticStandardChronology INSTANCE = new CopticStandardChronology();
    /**
     * The epoch.
     */
    public static final LocalDate EPOCH = LocalDate.of(284, 8, 29);  // TODO check

    /**
     * Restrictive constructor.
     */
    private CopticStandardChronology() {
    }

    /**
     * Retain singleton.
     * 
     * @return the singleton, not null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return "Coptic";
    }

    /**
     * Creates a Coptic date from a standard ISO-8601 date.
     *
     * @return the Coptic date, not null
     */
    @Override
    public ChronologyDate createChronologyDate(LocalDate date) {
        long epochDays = date.toEpochDays();
        int year = (int) (((epochDays * 4) + 1463) / 1461);
        int era = (year < 1 ? 0 : 1);
        int yoe = (year < 1 ? -year + 1 : year);
        int startYearEpochDays = (year - 1) * 365 + (year / 4);
        int doy0 = (int) (epochDays - startYearEpochDays);
        int month = doy0 / 30 + 1;
        int dom = doy0 % 30 + 1;
        return new ChronologyDate(INSTANCE, date, year, era, yoe, month, dom, doy0 + 1);
    }

    /**
     * Creates a Coptic date from a standard ISO-8601 date.
     *
     * @return the Coptic date, not null
     */
    @Override
    public ChronologyDate createChronologyDate(int era, int yearOfEra, int monthOfYear, int dayOfMonth) {
        // validate values
        // calculate ISO date
//        return new ChronologyDate(INSTANCE, date, year, era, yoe, month, dom, doy);
        return null;
    }

    /**
     * Checks if the specified date is in a leap year.
     *
     * @param date  the date to check, not null
     * @return true if the date is in a leap year
     */
    @Override
    protected boolean isLeapYear(ChronologyDate date) {
        if (date.getEra() == 0) {
            return false;
        } else {
            return (date.getYearOfEra() % 4) == 3;
        }
    }

    /**
     * Gets a calendrical rule for the {@code ChronologyDate}.
     * 
     * @return the rule, not null
     */
    @Override
    public CalendricalRule<ChronologyDate> dateRule() {
        return null;
    }

}
