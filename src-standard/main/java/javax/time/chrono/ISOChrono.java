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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.time.DateTimeException;
import javax.time.LocalDate;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;

/**
 * The ISO calendar system.
 * <p>
 * This chronology defines the rules of the ISO calendar system.
 * This calendar system is based on the ISO-8601 standard, which is the
 * <i>de facto</i> world calendar.
 * <p>
 * The fields are defined as follows:
 * <ul>
 * <li>era - There are two eras, 'Current Era' (CE) and 'Before Current Era' (BCE).
 * <li>year-of-era - The year-of-era is the same as the proleptic-year for the current CE era.
 *  For the BCE era before the ISO epoch the year increases from 1 upwards as time goes backwards.
 * <li>proleptic-year - The proleptic year is the same as the year-of-era for the
 *  current era. For the previous era, years have zero, then negative values.
 * <li>month-of-year - There are 12 months in an ISO year, numbered from 1 to 12.
 * <li>day-of-month - There are between 28 and 31 days in each of the ISO month, numbered from 1 to 31.
 *  Months 4, 6, 9 and 11 have 30 days, Months 1, 3, 5, 7, 8, 10 and 12 have 31 days.
 *  Month 2 has 28 days, or 29 in a leap year.
 * <li>day-of-year - There are 365 days in a standard ISO year and 366 in a leap year.
 *  The days are numbered from 1 to 365 or 1 to 366.
 * <li>leap-year - Leap years occur every 4 years, except where the year is divisble by 100 and not divisble by 400.
 * </ul>
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class ISOChrono extends Chrono<ISOChrono> implements Serializable {

    /**
     * Singleton instance of the ISO chronology.
     */
    public static final ISOChrono INSTANCE = new ISOChrono();
    /**
     * The singleton instance for the era BCE - 'Before Current Era'.
     * The 'ISO' part of the name emphasizes that this differs from the BCE
     * era in the Gregorian calendar system.
     * This has the numeric value of {@code 0}.
     */
    public static final Era<ISOChrono> ERA_BCE = ISOEra.BCE;
    /**
     * The singleton instance for the era CE - 'Current Era'.
     * The 'ISO' part of the name emphasizes that this differs from the CE
     * era in the Gregorian calendar system.
     * This has the numeric value of {@code 1}.
     */
    public static final Era<ISOChrono> ERA_CE = ISOEra.CE;

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -1440403870442975015L;

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
    /**
     * Gets the ID of the chronology - 'ISO'.
     * <p>
     * The ID uniquely identifies the {@code Chrono}.
     * It can be used to lookup the {@code Chrono} using {@link #of(String)}.
     * 
     * @return the chronology ID - 'ISO'
     * @see #getCalendarType()
     */
    @Override
    public String getId() {
        return "ISO";
    }

    /**
     * Gets the calendar type of the underlying calendar system - 'iso8601'.
     * <p>
     * The calendar type is an identifier defined by the
     * <em>Unicode Locale Data Markup Language (LDML)</em> specification.
     * It can be used to lookup the {@code Chrono} using {@link #of(String)}.
     * It can also be used as part of a locale, accessible via
     * {@link Locale#getUnicodeLocaleType(String)} with the key 'ca'.
     * 
     * @return the calendar system type - 'iso8601'
     * @see #getId()
     */
    @Override
    public String getCalendarType() {
        return "iso8601";
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate date(int prolepticYear, int month, int dayOfMonth) {
        return LocalDate.of(prolepticYear, month, dayOfMonth);
    }

    @Override
    public LocalDate dateFromYearDay(int prolepticYear, int dayOfYear) {
        return LocalDate.ofYearDay(prolepticYear, dayOfYear);
    }

    @Override
    public LocalDate date(DateTimeAccessor dateTime) {
        if (dateTime instanceof LocalDate) {
            return (LocalDate) dateTime;
        }
        return LocalDate.ofEpochDay(dateTime.getLong(LocalDateTimeField.EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the ISO proleptic
     * calendar system rules.
     * <p>
     * This method applies the current rules for leap years across the whole time-line.
     * In general, a year is a leap year if it is divisible by four without
     * remainder. However, years divisible by 100, are not leap years, with
     * the exception of years divisible by 400 which are.
     * <p>
     * For example, 1904 is a leap year it is divisible by 4.
     * 1900 was not a leap year as it is divisible by 100, however 2000 was a
     * leap year as it is divisible by 400.
     * <p>
     * The calculation is proleptic - applying the same rules into the far future and far past.
     * This is historically inaccurate, but is correct for the ISO-8601 standard.
     *
     * @param year  the ISO proleptic year to check
     * @return true if the year is leap, false otherwise
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return ((prolepticYear & 3) == 0) && ((prolepticYear % 100) != 0 || (prolepticYear % 400) == 0);
    }

    @Override
    public int prolepticYear(Era<ISOChrono> era, int yearOfEra) {
        if (era instanceof ISOEra == false) {
            throw new DateTimeException("Era must be ISOEra");
        }
        return (era == ISOEra.CE ? yearOfEra : 1 - yearOfEra);
    }

    @Override
    public Era<ISOChrono> eraOf(int eraValue) {
        return ISOEra.of(eraValue);
    }

    @Override
    public List<Era<ISOChrono>> eras() {
        return Arrays.<Era<ISOChrono>>asList(ISOEra.values());
    }

    //-----------------------------------------------------------------------
    @Override
    public DateTimeValueRange range(LocalDateTimeField field) {
        return field.range();
    }

}
