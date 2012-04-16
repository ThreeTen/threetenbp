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
import javax.time.builder.CalendricalObject;

/**
 * The Coptic calendar system.
 * <p>
 * This chronology defines the rules of the Coptic calendar system.
 * This calendar system is primarily used in Christian Egypt.
 * Dates are aligned such that {@code 0001AM-01-01 (Coptic)} is {@code 0284-08-29 (ISO)}.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, the current 'Era of the Martyrs' (AM) and the previous era (BEFORE_AM).
 * <li>year-of-era - The year-of-era is the same as the proleptic-year for the current AM era.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current AM era. For the BEFORE_AM era, years have negative values.
 * <li>month-of-year - There are 13 months in a Coptic year, numbered from 1 to 13.
 * <li>day-of-month - There are 30 days in each of the first 12 Coptic months, numbered 1 to 30.
 *  The 13th month has 5 days, or 6 in a leap year, numbered 1 to 5 or 1 to 6.
 * <li>day-of-year - There are 365 days in a standard Coptic year and 366 in a leap year.
 *  The days are numbered from 1 to 365 or 1 to 366.
 * <li>leap-year - Leap years occur every 4 years.
 * </ul>
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
    public ChronoDate<CopticChrono> date(Era era, int yearOfEra, int monthOfYear, int dayOfMonth) {
        if (era instanceof CopticEra) {
            throw new CalendricalException("Era must be a CopticEra");
        }
        return date(prolepticYear((CopticEra) era, yearOfEra), monthOfYear, dayOfMonth);
    }

    @Override
    public ChronoDate<CopticChrono> date(int prolepticYear, int monthOfYear, int dayOfMonth) {
        return new CopticDate(prolepticYear, monthOfYear, dayOfMonth);
    }

    @Override
    public ChronoDate<CopticChrono> date(CalendricalObject calendrical) {
        if (calendrical instanceof CopticDate) {
            return (CopticDate) calendrical;
        }
        LocalDate date = calendrical.extract(LocalDate.class);
        if (date == null) {
            throw new CalendricalException("Unable to create CopticDate from " + calendrical.getClass());
        }
        return dateFromEpochDay(date.toEpochDay());
    }

    @Override
    public ChronoDate<CopticChrono> dateFromEpochDay(long epochDay) {
        return CopticDate.ofEpochDay(epochDay);
    }

    @Override
    public ChronoDate<CopticChrono> now() {
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

    private static int prolepticYear(CopticEra era, int yearOfEra) {
        return (era == CopticEra.AM ? yearOfEra : 1 - yearOfEra);
    }

    //-------------------------------------------------------------------------
    /**
     * Implementation of a Coptic date.
     */
    private static final class CopticDate extends ChronoDate<CopticChrono> implements Comparable<ChronoDate<CopticChrono>>, Serializable {
        /**
         * Serialization version.
         */
        private static final long serialVersionUID = 1L;
        /**
         * The difference between the ISO and Coptic epoch day count.
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
        private static CopticDate ofEpochDay(long epochDay) {
            // TODO: validate
//            if (epochDay < MIN_EPOCH_DAY || epochDay > MAX_EPOCH_DAY) {
//                throw new CalendricalRuleException("Date exceeds supported range for CopticDate", CopticChronology.YEAR);
//            }
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
        private CopticDate(int prolepticYear, int monthOfYear, int dayOfMonth) {
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
        public int get(ChronoDateField field) {
            DateTimes.checkNotNull(field, "ChronoField must not be null");
            switch (field) {
                case DAY_OF_WEEK: return DateTimes.floorMod(toEpochDay() + 3, 7) + 1;
                case DAY_OF_MONTH: return day;
                case DAY_OF_YEAR: return (month - 1) * 30 + day;
                case MONTH_OF_YEAR: return month;
                case YEAR_OF_ERA: return (prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear);
                case PROLEPTIC_YEAR: return prolepticYear;
                case ERA: return (prolepticYear >= 1 ? 1 : 0);
            }
            throw new CalendricalException("Unknown field");
        }

        @Override
        public CopticDate with(ChronoDateField field, int newValue) {
            DateTimes.checkNotNull(field, "ChronoField must not be null");
            // TODO: validate value
            int curValue = get(field);
            if (curValue == newValue) {
                return this;
            }
            switch (field) {
                case DAY_OF_WEEK: return plusDays(newValue - curValue);
                case DAY_OF_MONTH: return resolvePreviousValid(prolepticYear, month, newValue);
                case DAY_OF_YEAR: return resolvePreviousValid(prolepticYear, ((newValue - 1) / 30) + 1, ((newValue - 1) % 30) + 1);
                case MONTH_OF_YEAR: return resolvePreviousValid(prolepticYear, newValue, day);
                case YEAR_OF_ERA: return resolvePreviousValid(prolepticYear >= 1 ? newValue : 1 - newValue, month, day);
                case PROLEPTIC_YEAR: return resolvePreviousValid(newValue, month, day);
                case ERA: return resolvePreviousValid(1 - prolepticYear, month, day);
            }
            throw new CalendricalException("Unknown field");
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

}
