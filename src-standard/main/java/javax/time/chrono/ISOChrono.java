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
import javax.time.calendrical.CalendricalObject;

/**
 * The ISO calendar system.
 * <p>
 * This chronology defines the rules of the ISO calendar system.
 * This calendar system is based on the ISO-8601 standard, which is the
 * <i>de facto</i> world calendar.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, the 'Current Era' (CE) and 'Before Current Era' (BCE).
 * <li>year-of-era - The year-of-era is the same as the proleptic-year for the current CE era.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current CE era. For the BCE era, years have negative values.
 * <li>month-of-year - There are 12 months in an ISO year, numbered from 1 to 12.
 * <li>day-of-month - There are between 28 and 31 days in each of the ISO month, numbered from 1 to 31.
 *  Months 4, 6, 9 and 11 have 30 days, Months 1, 3, 5, 7, 8, 10 and 12 have 31 days.
 *  Month 2 has 28 days, or 29 in a leap year.
 * <li>day-of-year - There are 365 days in a standard ISO year and 366 in a leap year.
 *  The days are numbered from 1 to 365 or 1 to 366.
 * <li>leap-year - Leap years occur every 4 years, except where the year is divisble by 100 and not divisble by 400.
 * </ul>
 * <p>
 * This class is immutable and thread-safe.
 */
public final class ISOChrono extends Chrono implements Serializable {

    /**
     * Singleton instance.
     */
    public static final ISOChrono INSTANCE = new ISOChrono();

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Restricted constructor.
     */
    private ISOChrono() {
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
        return "ISO";
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoDate<ISOChrono> date(Era era, int yearOfEra, int monthOfYear, int dayOfMonth) {
        if (era instanceof ISOEra) {
            throw new CalendricalException("Era must be a ISOEra");
        }
        return date(prolepticYear((ISOEra) era, yearOfEra), monthOfYear, dayOfMonth);
    }

    @Override
    public ChronoDate<ISOChrono> date(int prolepticYear, int monthOfYear, int dayOfMonth) {
        return new ISODate(LocalDate.of(prolepticYear, monthOfYear, dayOfMonth));
    }

    @Override
    public ChronoDate<ISOChrono> date(CalendricalObject calendrical) {
        if (calendrical instanceof ISODate) {
            return (ISODate) calendrical;
        }
        return new ISODate(LocalDate.from(calendrical));
    }

    @Override
    public ChronoDate<ISOChrono> dateFromEpochDay(long epochDay) {
        return new ISODate(LocalDate.ofEpochDay(epochDay));
    }

    @Override
    public ChronoDate<ISOChrono> now() {
        return (ISODate) super.now();
    }

    /**
     * Checks if the specified year is a leap year.
     * <p>
     * ISO leap years occur exactly in line with ISO leap years.
     * This method does not validate the year passed in, and only has a
     * well-defined result for years in the supported range.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return DateTimes.isLeapYear(prolepticYear);
    }

    @Override
    public ISOEra createEra(int eraValue) {
        return ISOEra.of(eraValue);
    }

    private static int prolepticYear(ISOEra era, int yearOfEra) {
        return (era == ISOEra.CE ? yearOfEra : 1 - yearOfEra);
    }

    //-------------------------------------------------------------------------
    /**
     * Implementation of a ISO date.
     */
    private static final class ISODate extends ChronoDate<ISOChrono> implements Comparable<ChronoDate<ISOChrono>>, Serializable {
        /**
         * Serialization version.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The ISO date.
         */
        private final LocalDate isoDate;

        //-----------------------------------------------------------------------
        /**
         * Creates an instance.
         * 
         * @param date  the equivalent ISO date
         */
        private ISODate(LocalDate isoDate) {
            this.isoDate = isoDate;
        }

        //-----------------------------------------------------------------------
        @Override
        public Chrono getChronology() {
            return ISOChrono.INSTANCE;
        }

        //-----------------------------------------------------------------------
        @Override
        public int get(ChronoDateField field) {
            DateTimes.checkNotNull(field, "ChronoField must not be null");
            switch (field) {
                case DAY_OF_WEEK: return isoDate.getDayOfWeek().getValue();
                case DAY_OF_MONTH: return isoDate.getDayOfMonth();
                case DAY_OF_YEAR: return isoDate.getDayOfYear();
                case MONTH_OF_YEAR: return isoDate.getMonthOfYear().getValue();
                case YEAR_OF_ERA: return (isoDate.getYear() >= 1 ? isoDate.getYear() : 1 - isoDate.getYear());
                case PROLEPTIC_YEAR: return isoDate.getYear();
                case ERA: return (isoDate.getYear() >= 1 ? 1 : 0);
            }
            throw new CalendricalException("Unknown field");
        }

        @Override
        public ISODate with(ChronoDateField field, int newValue) {
            DateTimes.checkNotNull(field, "ChronoField must not be null");
            // TODO: validate value
            int curValue = get(field);
            if (curValue == newValue) {
                return this;
            }
            switch (field) {
                case DAY_OF_WEEK: return plusDays(newValue - curValue);
                case DAY_OF_MONTH: return with(isoDate.withDayOfMonth(newValue));
                case DAY_OF_YEAR: return with(isoDate.withDayOfYear(newValue));
                case MONTH_OF_YEAR: return with(isoDate.withMonthOfYear(newValue));
                case YEAR_OF_ERA: return with(isoDate.withYear(isoDate.getYear() >= 1 ? newValue : (1 - newValue)));
                case PROLEPTIC_YEAR: return with(isoDate.withYear(newValue));
                case ERA: return with(isoDate.withYear(1 - isoDate.getYear()));
            }
            throw new CalendricalException("Unknown field");
        }

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

}
