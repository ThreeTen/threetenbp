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

import javax.time.DateTimeException;

/**
 * Defines the valid eras for the Hijrah calendar system.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a HijrahEra
 * instance. Use getValue() instead.</b>
 * 
 * <h4>Implementation notes</h4>
 * This is an immutable and thread-safe enum.
 */
enum HijrahEra implements Era<HijrahChronology> {

    /**
     * The singleton instance for the era before the current one, 'Before Anno Hegirae',
     * which has the value 0.
     */
    BEFORE_AH,
    /**
     * The singleton instance for the current era, 'Anno Hegirae', which has the value 1.
     */
    AH;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code HijrahEra} from a value.
     * <p>
     * The current era (from ISO date 622-06-19 onwards) has the value 1
     * The previous era has the value 0.
     *
     * @param hijrahEra  the era to represent, from 0 to 1
     * @return the HijrahEra singleton, never null
     * @throws DateTimeException if the era is invalid
     */
    public static HijrahEra of(int hijrahEra) {
        switch (hijrahEra) {
            case 0:
                return BEFORE_AH;
            case 1:
                return AH;
            default:
                throw new DateTimeException("HijrahEra not valid");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the era numeric value.
     * <p>
     * The current era (from ISO date 622-06-19 onwards) has the value 1.
     * The previous era has the value 0.
     *
     * @return the era value, from 0 (BEFORE_AH) to 1 (AH)
     */
    @Override
    public int getValue() {
        return ordinal();
    }

    @Override
    public HijrahDate date(int year, int month, int day) {
        return HijrahDate.of(this, year, month, day);
    }

    @Override
    public ChronoLocalDate<HijrahChronology> dateFromYearDay(int year, int dayOfYear) {
        return HijrahChronology.INSTANCE.dateFromYearDay(this, year, dayOfYear);
    }

    /**
     * Returns the proleptic year from this era and year of era.
     *
     * @param yearOfEra the year of Era
     * @return the computed prolepticYear
     */
    int prolepticYear(int yearOfEra) {
        return (this == HijrahEra.AH ? yearOfEra : 1 - yearOfEra);
    }

}
