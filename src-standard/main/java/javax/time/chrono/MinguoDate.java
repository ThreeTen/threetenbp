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

import static javax.time.chrono.MinguoChronology.YEARS_DIFFERENCE;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;

/**
 * A date in the Minguo calendar system.
 * <p>
 * This implements {@code ChronoDate} for the {@link MinguoChronology Minguo calendar}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class MinguoDate extends ChronoDate implements Comparable<ChronoDate>, Serializable {
    // this class is package-scoped so that future conversion to public
    // would not change serialization

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Minguo date.
     */
    private final LocalDate isoDate;

    /**
     * Creates an instance.
     * 
     * @param date  the equivalent Minguo date
     */
    MinguoDate(LocalDate isoDate) {
        this.isoDate = isoDate;
    }

    //-----------------------------------------------------------------------
    @Override
    public Chrono getChronology() {
        return MinguoChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public int lengthOfMonth() {
        return isoDate.lengthOfMonth();
    }

    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (f.isDateField()) {
                switch (f) {
                    case YEAR_OF_ERA: {
                        int prolepticYear = getProlepticYear();
                        return (prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear);
                    }
                    case YEAR: return isoDate.getYear() - YEARS_DIFFERENCE;
                    case ERA: return (isoDate.getYear() - YEARS_DIFFERENCE >= 1 ? 1 : 0);
                    default: return isoDate.get(field);
                }
            }
            throw new CalendricalException("Unsupported field: " + field.getName());
        }
        return field.doGet(this);
    }

    @Override
    public MinguoDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (f.isDateField()) {
                switch (f) {
                    case YEAR_OF_ERA:
                    case YEAR:
                    case ERA: {
                        f.checkValidValue(newValue);
                        int nvalue = (int) newValue;
                        switch (f) {
                            case YEAR_OF_ERA: return with(isoDate.withYear(
                                    getProlepticYear() >= 1 ? nvalue + YEARS_DIFFERENCE : (1 - nvalue)  + YEARS_DIFFERENCE));
                            case YEAR: return with(isoDate.withYear(nvalue + YEARS_DIFFERENCE));
                            case ERA: return with(isoDate.withYear((1 - getProlepticYear()) + YEARS_DIFFERENCE));
                        }
                    }
                    default: return with(isoDate.with(field, newValue));
                }
            }
            throw new CalendricalException("Unsupported field: " + field.getName());
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public MinguoDate plusYears(long years) {
        return with(isoDate.plusYears(years));
    }

    @Override
    public MinguoDate plusMonths(long months) {
        return with(isoDate.plusMonths(months));
    }

    @Override
    public MinguoDate plusDays(long days) {
        return with(isoDate.plusDays(days));
    }

    private MinguoDate with(LocalDate newDate) {
        return (newDate == isoDate ? this : new MinguoDate(newDate));
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate toLocalDate() {
        return isoDate;
    }

}
