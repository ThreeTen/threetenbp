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


import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ERA;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;

import javax.time.DateTimes;
import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;

/**
 * A date in the Thai Buddhist calendar system.
 * <p>
 * {@code ThaiBuddhistDate} is an immutable class that represents a date in the Thai Buddhist calendar system.
 * The rules of the calendar system are described in {@link ThaiBuddhistChronology}.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 *
 * @author Roger Riggs
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
final class ThaiBuddhistDate extends ChronoDate
        implements Comparable<ChronoDate>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -135957664026407129L;

    /**
     * The underlying date.
     */
    private final LocalDate date;

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
     * Constructs an instance with the specified date.
     *
     * @param date  the date, validated in range, validated not null
     */
    private ThaiBuddhistDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns a new date based on this one, returning {@code this} where possible.
     *
     * @param date  the date to create with, not null
     */
    private ThaiBuddhistDate with(LocalDate date) {
        if (this.date == date) {
            return this;
        }
        return new ThaiBuddhistDate(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that this date uses, which is the Thai Buddhist calendar system.
     *
     * @return the Thai Buddhist chronology, never null
     */
    @Override
    public ThaiBuddhistChronology getChronology() {
        return ThaiBuddhistChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            switch ((LocalDateTimeField) field) {
                case DAY_OF_WEEK: 
                case ALIGNED_DAY_OF_WEEK_IN_MONTH: 
                case ALIGNED_DAY_OF_WEEK_IN_YEAR: 
                case DAY_OF_MONTH:
                case DAY_OF_YEAR: 
                case ALIGNED_WEEK_OF_MONTH: 
                case ALIGNED_WEEK_OF_YEAR: 
                case EPOCH_DAY: 
                case MONTH_OF_YEAR: 
                case EPOCH_MONTH:
                case YEAR:
                    // All these are delegated to LocalDate
                    return date.get(field);
                case YEAR_OF_ERA: return getYearOfEra();
                case ERA: return getEra().getValue();
            }
            throw new CalendricalException(field.getName() + " not valid for LocalDate");
        }
        return field.doGet(this);
    }


    @Override
    public ThaiBuddhistDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            f.checkValidValue(newValue);
            switch (f) {
                case DAY_OF_WEEK: return plusDays(newValue - date.getDayOfWeek().getValue());
                case ALIGNED_DAY_OF_WEEK_IN_MONTH: return plusDays(newValue - get(ALIGNED_DAY_OF_WEEK_IN_MONTH));
                case ALIGNED_DAY_OF_WEEK_IN_YEAR: return plusDays(newValue - get(ALIGNED_DAY_OF_WEEK_IN_YEAR));
                case DAY_OF_MONTH: return with(date.withDayOfMonth((int) newValue));
                case DAY_OF_YEAR: return with(date.withDayOfYear((int) newValue));
                case EPOCH_DAY: return with(LocalDate.ofEpochDay(newValue));
                case ALIGNED_WEEK_OF_MONTH: return plusWeeks(newValue - get(ALIGNED_WEEK_OF_MONTH));
                case ALIGNED_WEEK_OF_YEAR: return plusWeeks(newValue - get(ALIGNED_WEEK_OF_YEAR));
                case MONTH_OF_YEAR: return with(date.withMonth((int) newValue));
                case EPOCH_MONTH: return plusMonths(newValue - get(EPOCH_MONTH));
                case YEAR_OF_ERA: return withYearOfEra((int)newValue);
                case YEAR: return with(date.withYear((int) newValue));
                case ERA: return with(date.withYear(1 - date.getYear()));
            }
            throw new CalendricalException(field.getName() + " not valid for LocalDate");
        }
        return field.doSet(this, newValue);
    }


    @Override
    public int lengthOfMonth() {
        return date.lengthOfMonth();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the Thai Buddhist era field.
     *
     * @return the era, never null
     */
    @Override
    public ThaiBuddhistEra getEra() {
        int year = date.getYear() - ThaiBuddhistChronology.YEAR_OFFSET;
        return year < 1 ? ThaiBuddhistEra.BEFORE_BUDDHIST : ThaiBuddhistEra.BUDDHIST;
    }

    /**
     * Gets the Thai Buddhist year-of-era field.
     *
     * @return the year, from 1 to 9999
     */
    @Override
    public int getYearOfEra() {
        int year = date.getYear() - ThaiBuddhistChronology.YEAR_OFFSET;
        return year < 1 ? 1 - year : year;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the Thai Buddhist calendar system rules.
     *
     * @return true if this date is in a leap year
     */
    @Override
    public boolean isLeapYear() {
        return date.isLeapYear();
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
     * @throws IllegalCalendarFieldValueException if the year-of-era value is invalid
     */
    public ThaiBuddhistDate withYear(ThaiBuddhistEra era, int yearOfEra) {
        LocalDateTimeField.YEAR_OF_ERA.checkValidValue(yearOfEra);
        int year = yearOfEra;
        if (era == ThaiBuddhistEra.BEFORE_BUDDHIST) {
            year = 1 - yearOfEra;
        }
        year += ThaiBuddhistChronology.YEAR_OFFSET;
        return with(date.withYear(year));
    }

    @Override
    public ThaiBuddhistDate withYearOfEra(int yearOfEra) {
        return withYear(getEra(), yearOfEra);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified number of years added.
     * <p>
     * This method adds the specified amount in years to the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the years added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    @Override
    public ThaiBuddhistDate plusYears(long years) {
        return with(date.plusYears(years));
    }

    /**
     * Returns a copy of this date with the specified number of months added.
     * <p>
     * This method adds the specified amount in months to the date.
     * If the month-day is invalid for the year, then the previous valid day
     * will be selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the months added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    @Override
    public ThaiBuddhistDate plusMonths(long months) {
        return with(date.plusMonths(months));
    }

    /**
     * Returns a copy of this date with the specified number of weeks added.
     * <p>
     * This method adds the specified amount in weeks to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the weeks added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    @Override
    public ThaiBuddhistDate plusWeeks(long weeks) {
        return with(date.plusWeeks(weeks));
    }

    /**
     * Returns a copy of this date with the specified number of days added.
     * <p>
     * This method adds the specified amount in days to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a {@code ThaiBuddhistDate} based on this date with the days added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    @Override
    public ThaiBuddhistDate plusDays(long days) {
        return with(date.plusDays(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to a {@code LocalDate}, which is the default representation
     * of a date, and provides values in the ISO-8601 calendar system.
     *
     * @return the equivalent date in the ISO-8601 calendar system, never null
     */
    @Override
    public LocalDate toLocalDate() {
        return date;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(ThaiBuddhistDate other) {
        return date.compareTo(other.date);
    }

    /**
     * Checks if this date is after the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the specified date
     */
    public boolean isAfter(ThaiBuddhistDate other) {
        return date.isAfter(other.date);
    }

    /**
     * Checks if this date is before the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is before the specified date
     */
    public boolean isBefore(ThaiBuddhistDate other) {
        return date.isBefore(other.date);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to the specified date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, null returns false
     * @return true if this is equal to the specified date
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ThaiBuddhistDate) {
            ThaiBuddhistDate otherDate = (ThaiBuddhistDate) other;
            return this.date.equals(otherDate.date);
        }
        return false;
    }

    /**
     * A hash code for this date.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return "ThaiBuddhistDate".hashCode() ^ date.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code 2551-12-01 (ThaiBuddhist)}.
     * <p>
     * The output will be in the format {@code yyyy-MM-dd (ThaiBuddhist)}.
     * The year will be negative for the era BEFORE_BUDDHIST.
     * There is no year zero.
     *
     * @return the formatted date, never null
     */
    @Override
    public String toString() {
        boolean currentEra = getEra() == ThaiBuddhistEra.BUDDHIST;
        int yearValue = getYearOfEra();
        yearValue = Math.abs(currentEra ? yearValue : -yearValue);
        int monthValue = getMonth();
        int dayValue = getDayOfMonth();
        StringBuilder buf = new StringBuilder();
        return buf.append(currentEra ? "" : "-")
                .append(yearValue < 10 ? "0" : "").append(yearValue)
                .append(monthValue < 10 ? "-0" : "-").append(monthValue)
                .append(dayValue < 10 ? "-0" : "-").append(dayValue)
                .append(" (ThaiBuddhist)").toString();
    }

}
