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
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;

/**
 * A date in the Japanese calendar system.
 * <p>
 * This implements {@code ChronoDate} for the {@link JapaneseChronology Japanese calendar}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class JapaneseDate extends ChronoDate implements Comparable<ChronoDate>, Serializable {
    // this class is package-scoped so that future conversion to public
    // would not change serialization

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The underlying date.
     */
    private final LocalDate isoDate;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JapaneseDate} from the proleptic year,
     * month-of-year and day-of-month.
     *
     * @param prolepticYear  the year to represent in the proleptic year
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Japanese date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static JapaneseDate of(int prolepticYear, int month, int dayOfMonth) {
        return new JapaneseDate(LocalDate.of(prolepticYear, month, dayOfMonth));
    }

    /**
     * Obtains an instance of {@code JapaneseDate} from the era, year-of-era,
     * month-of-year and day-of-month.
     *
     * @param era  the era to represent, not null
     * @param yearOfEra  the year-of-era to represent, from 1 to 9999
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Japanese date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static JapaneseDate of(JapaneseEra era, int yearOfEra, int month, int dayOfMonth) {
        DateTimes.checkNotNull(era, "JapaneseEra must not be null");
        yearOfEraCheckValidValue(era, yearOfEra);
        int year = yearOfEra + era.getYearOffset();
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        return new JapaneseDate(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     * 
     * @param date  the time-line date, not null
     */
    JapaneseDate(LocalDate date) {
        DateTimes.checkNotNull(date, "LocalDate must not be null");
        int yearOfEra = getYearOfEra(date);
        if (yearOfEra < 0) {
            yearOfEra = 1 - yearOfEra;
        }
        JapaneseEra era = JapaneseEra.from(date);
        yearOfEraCheckValidValue(era, yearOfEra);
        this.isoDate = date;
    }

    //-----------------------------------------------------------------------
    @Override
    public Chronology getChronology() {
        return JapaneseChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public int lengthOfMonth() {
        return isoDate.lengthOfMonth();
    }

    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case YEAR_OF_ERA: return getYearOfEra(isoDate);
                case ERA: return JapaneseEra.from(isoDate).getValue();
            }
            return isoDate.get(field);
        }
        return field.doGet(this);
    }

    /**
     * Returns year-of-era from a local date object.
     *
     * @param date  the date, validated in range, validated not null
     * @return the year-of-era
     */
    private static int getYearOfEra(LocalDate date) {
        JapaneseEra era = JapaneseEra.from(date);
        return date.getYear() - era.getYearOffset();
    }

    //-----------------------------------------------------------------------
    @Override
    public JapaneseDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            switch (f) {
                case YEAR_OF_ERA:
                case YEAR:
                case ERA: {
                    f.checkValidValue(newValue);
                    int nvalue = (int) newValue;
                    switch (f) {
                        case YEAR_OF_ERA: return this.withYearOfEra(nvalue);
                        case YEAR: return with(isoDate.withYear(nvalue));
                        case ERA: {
                            int yearOfEra = this.getYearOfEra();
                            return this.withYear(JapaneseEra.of(nvalue), yearOfEra);
                        }
                    }
                }
            }
            return with(isoDate.with(field, newValue));
        }
        return field.doSet(this, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the year altered.
     * <p>
     * This method changes the year of the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param era  the era to set in the returned date, not null
     * @param yearOfEra  the year-of-era to set in the returned date, from 1 to 9999
     * @return a {@code JapaneseDate} based on this date with the requested year, never null
     * @throws CalendricalException if the year value is invalid
     */
    private JapaneseDate withYear(JapaneseEra era, int yearOfEra) {
        yearOfEraCheckValidValue(era, yearOfEra);
        int year = yearOfEra + era.getYearOffset();
        return with(isoDate.withYear(year));
    }

    /**
     * Returns a copy of this date with the year-of-era altered.
     * <p>
     * This method changes the year-of-era of the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearOfEra  the year to set in the returned date, from 1 to 9999
     * @return a {@code JapaneseDate} based on this date with the requested year-of-era, never null
     * @throws CalendricalException if the year-of-era value is invalid
     */
    @Override
    public JapaneseDate withYearOfEra(int yearOfEra) {
        return withYear((JapaneseEra) getEra(), yearOfEra);
    }

    /**
     * Check if the yearOfEra is valid for the Era.
     * 
     * @param era the current Era; not null
     * @param yearOfEra the year supposed to be in the Era.
     * @throws CalendricalException if the year-of-era value is invalid
     */
    private static void yearOfEraCheckValidValue(JapaneseEra era, int yearOfEra) {

    }

    //-----------------------------------------------------------------------
    @Override
    public JapaneseDate plusYears(long years) {
        return with(isoDate.plusYears(years));
    }

    @Override
    public JapaneseDate plusMonths(long months) {
        return with(isoDate.plusMonths(months));
    }

    @Override
    public JapaneseDate plusDays(long days) {
        return with(isoDate.plusDays(days));
    }

    private JapaneseDate with(LocalDate newDate) {
        return (newDate.equals(isoDate) ? this : new JapaneseDate(newDate));
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate toLocalDate() {
        return isoDate;
    }

}
