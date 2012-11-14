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
package javax.time.extra.chrono;

import static javax.time.calendrical.ChronoField.EPOCH_DAY;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.time.DateTimeException;
import javax.time.calendrical.ChronoField;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.chrono.Chrono;
import javax.time.chrono.ChronoLocalDate;
import javax.time.chrono.Era;
import javax.time.jdk8.Jdk8Methods;

/**
 * The Coptic calendar system.
 * <p>
 * This chronology defines the rules of the Coptic calendar system.
 * This calendar system is primarily used in Christian Egypt.
 * Dates are aligned such that {@code 0001AM-01-01 (Coptic)} is {@code 0284-08-29 (ISO)}.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, the current 'Era of the Martyrs' (AM) and the previous era (ERA_ERA_BEFORE_AM).
 * <li>year-of-era - The year-of-era for the current era increases uniformly from the epoch at year one.
 *  For the previous era the year increases from one as time goes backwards.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current era. For the previous era, years have zero, then negative values.
 * <li>month-of-year - There are 13 months in a Coptic year, numbered from 1 to 13.
 * <li>day-of-month - There are 30 days in each of the first 12 Coptic months, numbered 1 to 30.
 *  The 13th month has 5 days, or 6 in a leap year, numbered 1 to 5 or 1 to 6.
 * <li>day-of-year - There are 365 days in a standard Coptic year and 366 in a leap year.
 *  The days are numbered from 1 to 365 or 1 to 366.
 * <li>leap-year - Leap years occur every 4 years.
 * </ul>
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class CopticChrono extends Chrono<CopticChrono> implements Serializable {

    /**
     * Singleton instance of the Coptic chronology.
     */
    public static final CopticChrono INSTANCE = new CopticChrono();
    /**
     * The singleton instance for the era BEFORE_AM.
     * This has the numeric value of {@code 0}.
     */
    public static final Era<CopticChrono> ERA_BEFORE_AM = CopticEra.BEFORE_AM;
    /**
     * The singleton instance for the era AM - 'Era of the Martyrs'.
     * This has the numeric value of {@code 1}.
     */
    public static final Era<CopticChrono> ERA_AM = CopticEra.AM;

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 7291205177830286973L;
    /**
     * Range of months.
     */
    static final DateTimeValueRange MOY_RANGE = DateTimeValueRange.of(1, 13);
    /**
     * Range of days.
     */
    static final DateTimeValueRange DOM_RANGE = DateTimeValueRange.of(1, 5, 30);
    /**
     * Range of days.
     */
    static final DateTimeValueRange DOM_RANGE_NONLEAP = DateTimeValueRange.of(1, 5);
    /**
     * Range of days.
     */
    static final DateTimeValueRange DOM_RANGE_LEAP = DateTimeValueRange.of(1, 6);

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
    /**
     * Gets the ID of the chronology - 'Coptic'.
     * <p>
     * The ID uniquely identifies the {@code Chrono}.
     * It can be used to lookup the {@code Chrono} using {@link #of(String)}.
     *
     * @return the chronology ID - 'Coptic'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "Coptic";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'coptic'.
     * <p>
     * The calendar type is an identifier defined by the
     * <em>Unicode Locale Data Markup Language (LDML)</em> specification.
     * It can be used to lookup the {@code Chrono} using {@link #of(String)}.
     * It can also be used as part of a locale, accessible via
     * {@link Locale#getUnicodeLocaleType(String)} with the key 'ca'.
     *
     * @return the calendar system type - 'coptic'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "coptic";
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoLocalDate<CopticChrono> date(int prolepticYear, int month, int dayOfMonth) {
        return new CopticDate(prolepticYear, month, dayOfMonth);
    }

    @Override
    public ChronoLocalDate<CopticChrono> dateFromYearDay(int prolepticYear, int dayOfYear) {
        return new CopticDate(prolepticYear, (dayOfYear - 1) / 30 + 1, (dayOfYear - 1) % 30 + 1);
    }

    @Override
    public ChronoLocalDate<CopticChrono> dateFromYearDay(Era<CopticChrono> era, int yearOfEra, int dayOfYear) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChronoLocalDate<CopticChrono> date(DateTimeAccessor dateTime) {
        if (dateTime instanceof CopticDate) {
            return (CopticDate) dateTime;
        }
        return CopticDate.ofEpochDay(dateTime.getLong(EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
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
        return Jdk8Methods.floorMod(prolepticYear, 4) == 3;
    }

    @Override
    public int prolepticYear(Era<CopticChrono> era, int yearOfEra) {
        if (era instanceof CopticEra == false) {
            throw new DateTimeException("Era must be CopticEra");
        }
        return (era == CopticEra.AM ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public Era<CopticChrono> eraOf(int eraValue) {
        return CopticEra.of(eraValue);
    }

    @Override
    public List<Era<CopticChrono>> eras() {
        return Arrays.<Era<CopticChrono>>asList(CopticEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(ChronoField field) {
        switch (field) {
            case DAY_OF_MONTH: return DateTimeValueRange.of(1, 5, 30);
            case ALIGNED_WEEK_OF_MONTH: return DateTimeValueRange.of(1, 1, 5);
            case MONTH_OF_YEAR: return DateTimeValueRange.of(1, 13);
            case EPOCH_MONTH: return DateTimeValueRange.of(-1000, 1000);  // TODO
            case YEAR_OF_ERA: return DateTimeValueRange.of(1, 999, 1000);  // TODO
            case YEAR: return DateTimeValueRange.of(-1000, 1000);  // TODO
        }
        return field.range();
    }

}
