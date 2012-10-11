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

import static javax.time.calendrical.LocalDateTimeField.YEAR;
import static javax.time.chrono.ThaiBuddhistChronology.YEARS_DIFFERENCE;

import java.io.Serializable;

import javax.time.DateTimeException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
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
     * @param prolepticYear  the year to represent in the Thai Buddhist era, from 1 to MAX_YEAR
     * @param month  the month-of-year to represent, 1 to 12
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Thai Buddhist date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    static ThaiBuddhistDate of(int prolepticYear, int month, int dayOfMonth) {
        return new ThaiBuddhistDate(LocalDate.of(prolepticYear - YEARS_DIFFERENCE, month, dayOfMonth));
    }

    /**
     * Obtains an instance of {@code ThaiBuddhistDate} from the Thai Buddhist proleptic year,
     * month-of-year and day-of-month. This uses the Thai Buddhist era.
     *
     * @param prolepticYear  the year to represent in the Thai Buddhist era, from 1 to MAX_YEAR
     * @param month  the month-of-year to represent, 1 to 12
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Thai Buddhist date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    static ThaiBuddhistDate ofYearDay(int prolepticYear, int dayOfYear) {
        return new ThaiBuddhistDate(LocalDate.ofYearDay(prolepticYear - YEARS_DIFFERENCE, dayOfYear));
    }

    /**
     * Obtains an instance of {@code ThaiBuddhistDate} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ThaiBuddhistDate}.
     *
     * @param calendrical  the calendrical to convert, not null
     * @return the ThaiBuddhistDate, not null
     * @throws DateTimeException if unable to convert to a {@code LocalDate}
     */
    public static ThaiBuddhistDate from(DateTimeAccessor calendrical) {
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
     * @throws DateTimeException if the epoch days exceeds the supported date range
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
    public DateTimeValueRange range(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            switch (f) {
                case DAY_OF_MONTH:
                case DAY_OF_YEAR:
                case ALIGNED_WEEK_OF_MONTH:
                    return isoDate.range(field);
                case YEAR_OF_ERA: {
                    DateTimeValueRange range = YEAR.range();
                    long max = (getProlepticYear() <= 0 ? -(range.getMinimum() + YEARS_DIFFERENCE) + 1 : range.getMaximum() + YEARS_DIFFERENCE);
                    return DateTimeValueRange.of(1, max);
                }
            }
            return getChronology().range(f);
        }
        return field.doRange(this);
    }

    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case YEAR_OF_ERA: return getYearOfEra();
                case YEAR: return getProlepticYear();
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
        return getProlepticYear() < 1 ? ThaiBuddhistEra.BEFORE_BUDDHIST : ThaiBuddhistEra.BUDDHIST;
    }

    private int getProlepticYear() {
        return isoDate.getYear() + YEARS_DIFFERENCE;
    }

    /**
     * Gets the Thai Buddhist year-of-era field.
     *
     * @return the year-of-era
     */
    @Override
    public int getYearOfEra() {
        int year = getProlepticYear();
        return year < 1 ? 1 - year : year;
    }

    //-----------------------------------------------------------------------
    @Override
    public ThaiBuddhistDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (get(f) == newValue) {
                return this;
            }
            switch (f) {
                case YEAR_OF_ERA:
                case YEAR:
                case ERA: {
//                    f.checkValidValue(newValue);  // TODO ranges
                    int nvalue = (int) newValue;
                    switch (f) {
                        case YEAR_OF_ERA:
                            return with(isoDate.withYear((getProlepticYear() >= 1 ? nvalue : 1 - nvalue)  - YEARS_DIFFERENCE));
                        case YEAR:
                            return with(isoDate.withYear(nvalue - YEARS_DIFFERENCE));
                        case ERA:
                            return with(isoDate.withYear((1 - getProlepticYear()) - YEARS_DIFFERENCE));
                    }
                }
            }
            return with(isoDate.with(field, newValue));
        }
        return field.doSet(this, newValue);
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
