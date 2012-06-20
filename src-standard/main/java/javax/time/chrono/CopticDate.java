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
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;

/**
 * A date in the Coptic calendar system.
 * <p>
 * This date class implements a date for the {@link CopticChrono}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class CopticDate extends ChronoDate<CopticChrono> implements Comparable<ChronoDate<CopticChrono>>, Serializable {
    // this class is package-scoped so that future conversion to public
    // would not change serialization

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The difference between the Coptic and Coptic epoch day count.
     */
    private static final int EPOCH_DAY_DIFFERENCE = 574971;  // TODO: correct value

    /**
     * The proleptic year.
     */
    private final int prolepticYear;
    /**
     * The month.
     */
    private final short month;
    /**
     * The day.
     */
    private final short day;

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the Coptic date, not null
     * @throws CalendricalException if the date is invalid
     */
    static CopticDate ofEpochDay(long epochDay) {
        // TODO: validate
//        if (epochDay < MIN_EPOCH_DAY || epochDay > MAX_EPOCH_DAY) {
//            throw new CalendricalRuleException("Date exceeds supported range for CopticDate", CopticChronology.YEAR);
//        }
        int prolepticYear = (int) (((epochDay * 4) + 1463) / 1461);
        int startYearEpochDay = (prolepticYear - 1) * 365 + (prolepticYear / 4);
        int doy0 = (int) (epochDay - startYearEpochDay);
        int month = doy0 / 30 + 1;
        int dom = doy0 % 30 + 1;
        return new CopticDate(prolepticYear, month, dom);
    }

    private static CopticDate resolvePreviousValid(int prolepticYear, int month, int day) {
        if (month == 13 && day > 5) {
            day = CopticChrono.INSTANCE.isLeapYear(prolepticYear) ? 6 : 5;
        }
        return new CopticDate(prolepticYear, month, day);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     * 
     * @param prolepticYear  the Coptic proleptic-year
     * @param monthOfYear  the Coptic month, from 1 to 13
     * @param dayOfMonth  the Coptic day-of-month, from 1 to 30
     * @throws CalendricalException if the date is invalid
     */
    CopticDate(int prolepticYear, int monthOfYear, int dayOfMonth) {
        this.prolepticYear = prolepticYear;
        this.month = (short) monthOfYear;
        this.day = (short) dayOfMonth;
    }

    /**
     * Validates the object.
     *
     * @return the resolved date, not null
     */
    private Object readResolve() {
        // TODO: validate
        return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public Chrono getChronology() {
        return CopticChrono.INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {

                case DAY_OF_WEEK: return DateTimes.floorMod(toEpochDay() + 3, 7) + 1;
                case DAY_OF_MONTH: return day;
                case DAY_OF_YEAR: return (month - 1) * 30 + day;
                case MONTH_OF_YEAR: return month;
                case YEAR_OF_ERA: return (prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear);
                case PROLEPTIC_YEAR: return prolepticYear;
                case ERA: return (prolepticYear >= 1 ? 1 : 0);
            }
            throw new CalendricalException(field.getName() + " not valid for LocalDate");
        }
        return field.get(this);
    }

    @Override
    public CopticDate with(DateTimeField field, int newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);        // TODO: validate value

            switch (f) {
                case DAY_OF_WEEK: return plusDays(newValue - getDayOfWeek());
                case DAY_OF_MONTH: return resolvePreviousValid(prolepticYear, month, newValue);
                case DAY_OF_YEAR: return resolvePreviousValid(prolepticYear, ((newValue - 1) / 30) + 1, ((newValue - 1) % 30) + 1);
                case MONTH_OF_YEAR: return resolvePreviousValid(prolepticYear, newValue, day);
                case YEAR_OF_ERA: return resolvePreviousValid(prolepticYear >= 1 ? newValue : 1 - newValue, month, day);
                case PROLEPTIC_YEAR: return resolvePreviousValid(newValue, month, day);
                case ERA: return resolvePreviousValid(1 - prolepticYear, month, day);
            }
            throw new CalendricalException(field.getName() + " not valid for LocalDate");
        }
        return field.set(this, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public CopticDate plusYears(long years) {
        return plusMonths(DateTimes.safeMultiply(years, 13));
    }

    @Override
    public CopticDate plusMonths(long months) {
        if (months == 0) {
            return this;
        }
        long curEm = prolepticYear * 13L + (month - 1);
        long calcEm = DateTimes.safeAdd(curEm, months);
        int newYear = DateTimes.safeToInt(DateTimes.floorDiv(calcEm, 13));
        int newMonth = DateTimes.floorMod(calcEm, 13) + 1;
        return resolvePreviousValid(newYear, newMonth, day);
    }

    @Override
    public CopticDate plusDays(long days) {
        if (days == 0) {
            return this;
        }
        return CopticDate.ofEpochDay(DateTimes.safeAdd(toEpochDay(), days));
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        long year = (long) prolepticYear;
        long copticEpochDay = (year * 365) + DateTimes.floorDiv(year, 4) + (getDayOfYear() - 1);
        return copticEpochDay - EPOCH_DAY_DIFFERENCE;
    }

}
