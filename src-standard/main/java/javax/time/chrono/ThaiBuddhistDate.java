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
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;

/**
 * A date in the Thai Buddhist calendar system.
 * <p>
 * This implements {@code ChronoDate} for the {@link ThaiBuddhistChronology Thai Buddhist calendar}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class ThaiBuddhistDate extends ChronoDate implements Comparable<ChronoDate>, Serializable {
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
     * Obtains an instance of {@code ThaiBuddhistDate} from the Thai Buddhist proleptic year,
     * month-of-year and day-of-month. This uses the Thai Buddhist era.
     *
     * @param yearOfThaiBuddhistEra  the year to represent in the Thai Buddhist era, from 1 to MAX_YEAR
     * @param month  the month-of-year to represent, 1 to 12
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Thai Buddhist date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static ThaiBuddhistDate of(int prolepticYear, int month, int dayOfMonth) {
        return new ThaiBuddhistDate(LocalDate.of(prolepticYear, month, dayOfMonth));
    }

    /**
     * Obtains an instance of {@code ThaiBuddhistDate} from the era, year-of-era,
     * month-of-year and day-of-month.
     *
     * @param era  the era to represent, not null
     * @param yearOfEra  the year-of-era to represent, from 1 to 9999
     * @param month  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Thai Buddhist date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static ThaiBuddhistDate of(ThaiBuddhistEra era, int yearOfEra, int month, int dayOfMonth) {
        DateTimes.checkNotNull(era, "ThaiBuddhistEra must not be null");
        LocalDateTimeField.YEAR_OF_ERA.checkValidValue(yearOfEra);
        int year = yearOfEra;
        if (era == ThaiBuddhistEra.BEFORE_BUDDHIST) {
            year = 1 - yearOfEra;
        }
        year += ThaiBuddhistChronology.YEAR_OFFSET;
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        return new ThaiBuddhistDate(date);
    }
    
    /**
     * Obtains an instance of {@code ThaiBuddhistDate} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ThaiBuddhistDate}.
     *
     * @param calendrical  the calendrical to convert, not null
     * @return the ThaiBuddhistDate, not null
     * @throws CalendricalException if unable to convert to a {@code LocalDate}
     */
    public static ThaiBuddhistDate from(DateTime calendrical) {
        if (calendrical instanceof ThaiBuddhistDate) {
            return (ThaiBuddhistDate) calendrical;
        }
        return new ThaiBuddhistDate(LocalDate.from(calendrical));
    }

    /**
     * Obtains an instance of {@code ThaiBuddhistDate} from the epoch day count.
     * <p>
     * The Epoch Day count is a simple incrementing count of days
     * where day 0 is 1970-01-01. Negative numbers represent earlier days.
     *
     * @param epochDay  the Epoch Day to convert, based on the epoch 1970-01-01
     * @return the ThaiBuddhistDate, not null
     * @throws CalendricalException if the epoch days exceeds the supported date range
     */
    public static ChronoDate ofEpochDay(long epochDay) {
        return new ThaiBuddhistDate(LocalDate.ofEpochDay(epochDay));
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     * 
     * @param date  the time-line date, not null
     */
    ThaiBuddhistDate(LocalDate date) {
        DateTimes.checkNotNull(date, "LocalDate must not be null");
        this.isoDate = date;
    }

    //-----------------------------------------------------------------------
    @Override
    public Chronology getChronology() {
        return ThaiBuddhistChronology.INSTANCE;
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
                case YEAR_OF_ERA: return getYearOfEra();
                case ERA: return getEra().getValue();
            }
            return isoDate.get(field);
        }
        return field.doGet(this);
    }

    /**
     * Gets the Thai Buddhist era field.
     *
     * @return the era, never null
     */
    @Override
    public ThaiBuddhistEra getEra() {
        int year = isoDate.getYear() - ThaiBuddhistChronology.YEAR_OFFSET;
        return year < 1 ? ThaiBuddhistEra.BEFORE_BUDDHIST : ThaiBuddhistEra.BUDDHIST;
    }

    /**
     * Gets the Thai Buddhist year-of-era field.
     *
     * @return the year-of-era
     */
    @Override
    public int getYearOfEra() {
        int year = isoDate.getYear() - ThaiBuddhistChronology.YEAR_OFFSET;
        return year < 1 ? 1 - year : year;
    }

    //-----------------------------------------------------------------------
    @Override
    public ThaiBuddhistDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            switch (f) {
                case YEAR_OF_ERA:
                case YEAR:
                case ERA: {
                    f.checkValidValue(newValue);
                    int nvalue = (int) newValue;
                    switch (f) {
                        case YEAR_OF_ERA: return withYearOfEra(nvalue);
                        case YEAR: return with(isoDate.withYear(nvalue));
                        case ERA: return with(isoDate.withYear(1 - isoDate.getYear()));
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
     * @return a {@code ThaiBuddhistDate} based on this date with the requested year, never null
     * @throws CalendricalException if the year-of-era value is invalid
     */
    private ThaiBuddhistDate withYear(ThaiBuddhistEra era, int yearOfEra) {
        LocalDateTimeField.YEAR_OF_ERA.checkValidValue(yearOfEra);
        int year = yearOfEra;
        if (era == ThaiBuddhistEra.BEFORE_BUDDHIST) {
            year = 1 - yearOfEra;
        }
        year += ThaiBuddhistChronology.YEAR_OFFSET;
        return with(isoDate.withYear(year));
    }

    @Override
    public ThaiBuddhistDate withYearOfEra(int yearOfEra) {
        return withYear(getEra(), yearOfEra);
    }

    //-----------------------------------------------------------------------
    @Override
    public ThaiBuddhistDate plusYears(long years) {
        return with(isoDate.plusYears(years));
    }

    @Override
    public ThaiBuddhistDate plusMonths(long months) {
        return with(isoDate.plusMonths(months));
    }

    @Override
    public ThaiBuddhistDate plusDays(long days) {
        return with(isoDate.plusDays(days));
    }

    private ThaiBuddhistDate with(LocalDate newDate) {
        return (newDate.equals(isoDate) ? this : new ThaiBuddhistDate(newDate));
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate toLocalDate() {
        return isoDate;
    }

}
