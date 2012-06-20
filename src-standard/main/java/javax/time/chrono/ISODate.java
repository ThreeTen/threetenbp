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
 * PCEUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
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
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;

/**
 * A date in the ISO calendar system.
 * <p>
 * This date class implements a date for the {@link ISOChrono}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class ISODate extends ChronoDate<ISOChrono> implements Comparable<ChronoDate<ISOChrono>>, Serializable {
    // this class is package-scoped so that future conversion to public
    // would not change serialization

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The ISO date.
     */
    private final LocalDate isoDate;

    /**
     * Creates an instance.
     * 
     * @param date  the equivalent ISO date
     */
    ISODate(LocalDate isoDate) {
        this.isoDate = isoDate;
    }

    //-----------------------------------------------------------------------
    @Override
    public Chrono getChronology() {
        return ISOChrono.INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {

                case DAY_OF_WEEK: return isoDate.getDayOfWeek().getValue();
                case DAY_OF_MONTH: return isoDate.getDayOfMonth();
                case DAY_OF_YEAR: return isoDate.getDayOfYear();
                case MONTH_OF_YEAR: return isoDate.getMonthOfYear().getValue();
                case YEAR_OF_ERA: return (isoDate.getYear() >= 1 ? isoDate.getYear() : 1 - isoDate.getYear());
                case PROLEPTIC_YEAR: return isoDate.getYear();
                case ERA: return (isoDate.getYear() >= 1 ? 1 : 0);
            }
            throw new CalendricalException(field.getName() + " not valid for LocalDate");
        }
        return field.get(this);
    }

    @Override
    public ISODate with(DateTimeField field, int newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case DAY_OF_WEEK: return plusDays(newValue - getDayOfWeek());
                case DAY_OF_MONTH: return with(isoDate.withDayOfMonth(newValue));
                case DAY_OF_YEAR: return with(isoDate.withDayOfYear(newValue));
                case MONTH_OF_YEAR: return with(isoDate.withMonthOfYear(newValue));
                case YEAR_OF_ERA: return with(isoDate.withYear(isoDate.getYear() >= 1 ? newValue : (1 - newValue)));
                case PROLEPTIC_YEAR: return with(isoDate.withYear(newValue));
                case ERA: return with(isoDate.withYear(1 - isoDate.getYear()));
            }
            throw new CalendricalException(field.getName() + " not valid for LocalDate");
        }
        return field.set(this, newValue);    }

    //-----------------------------------------------------------------------
    @Override
    public ISODate plusYears(long years) {
        return with(isoDate.plusYears(years));
    }

    @Override
    public ISODate plusMonths(long months) {
        return with(isoDate.plusMonths(months));
    }

    @Override
    public ISODate plusDays(long days) {
        return with(isoDate.plusDays(days));
    }

    private ISODate with(LocalDate newDate) {
        return (newDate == isoDate ? this : new ISODate(newDate));
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate toLocalDate() {
        return isoDate;
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(ChronoDate<ISOChrono> other) {
        return isoDate.compareTo(other.toLocalDate());
    }

}
