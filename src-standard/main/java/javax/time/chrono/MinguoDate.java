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

import static javax.time.chrono.MinguoChrono.YEARS_DIFFERENCE;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalDateTimeUnit;
import javax.time.calendrical.PeriodUnit;

/**
 * A date in the Minguo calendar system.
 * <p>
 * This date class implements a date for the {@link MinguoChrono}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class MinguoDate extends ChronoDate implements Comparable<ChronoDate>, Serializable {
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
        return MinguoChrono.INSTANCE;
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
                case YEAR_OF_ERA: {
                    int prolepticYear = getProlepticYear();
                    return (prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear);
                }
                case PROLEPTIC_YEAR: return isoDate.getYear() - YEARS_DIFFERENCE;
                case ERA: return (isoDate.getYear() - YEARS_DIFFERENCE >= 1 ? 1 : 0);
            }
            throw new CalendricalException(field.getName() + " not valid for LocalDate");
        }
        return field.get(this);
    }

    @Override
    public MinguoDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);
            int nvalue = (int)newValue;
            switch (f) {
                case DAY_OF_WEEK: return plusDays(newValue - getDayOfWeek().getValue());
                case DAY_OF_MONTH: return with(isoDate.withDayOfMonth(nvalue));
                case DAY_OF_YEAR: return with(isoDate.withDayOfYear(nvalue));
                case MONTH_OF_YEAR: return with(isoDate.withMonthOfYear(nvalue));
                case YEAR_OF_ERA: return with(isoDate.withYear(
                        getProlepticYear() >= 1 ? nvalue + YEARS_DIFFERENCE : (1 - nvalue)  + YEARS_DIFFERENCE));
                case PROLEPTIC_YEAR: return with(isoDate.withYear(nvalue + YEARS_DIFFERENCE));
                case ERA: return with(isoDate.withYear((1 - getProlepticYear()) + YEARS_DIFFERENCE));
            }
            throw new CalendricalException(field.getName() + " not valid for LocalDate");
        }
        return field.set(this, newValue);
    }

    @Override
    public MinguoDate minus(long period, PeriodUnit unit) {
        return plus(DateTimes.safeNegate(period), unit);
    }

    @Override
    public MinguoDate plus(long period, PeriodUnit unit) {
        if (unit instanceof LocalDateTimeUnit) {
            LocalDateTimeUnit f = (LocalDateTimeUnit) unit;
            switch (f) {
                case DAYS: return plusDays(period);
                case WEEKS: return plusDays(DateTimes.safeMultiply(period, 7));
                case MONTHS: return plusMonths(period);
                case QUARTER_YEARS: return plusYears(period / 256).plusMonths((period % 256) * 3);  // no overflow (256 is multiple of 4)
                case HALF_YEARS: return plusYears(period / 256).plusMonths((period % 256) * 6);  // no overflow (256 is multiple of 2)
                case YEARS: return plusYears(period);
                case DECADES: return plusYears(DateTimes.safeMultiply(period, 10));
                case CENTURIES: return plusYears(DateTimes.safeMultiply(period, 100));
                case MILLENIA: return plusYears(DateTimes.safeMultiply(period, 1000));
//                case ERAS: throw new CalendricalException("Unable to add era, standard calendar system only has one era");
//                case FOREVER: return (period == 0 ? this : (period > 0 ? LocalDate.MAX_DATE : LocalDate.MIN_DATE));
            }
            throw new CalendricalException(unit.getName() + " not valid for CopticDate");
        }
        return unit.add(this, period);
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
