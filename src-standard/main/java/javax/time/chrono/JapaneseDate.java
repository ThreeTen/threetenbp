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
import javax.time.LocalDate;
import javax.time.DateTimes;
import javax.time.calendrical.*;

/**
 * A date in the Japanese calendar system.
 * <p>
 * {@code JapaneseDate} is an immutable class that represents a date in the Japanese calendar system.
 * The rules of the calendar system are described in {@link JapaneseChronology}.
 * <p>
 * Instances of this class may be created from other date objects that implement {@code Calendrical}.
 * Notably this includes {@link LocalDate} and all other date classes from other calendar systems.
 * <p>
 * JapaneseDate is immutable and thread-safe.
 *
 * @author Roger Riggs
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
final class JapaneseDate extends ChronoDate
        implements AdjustableDateTime, Comparable<ChronoDate>, Serializable {

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
     * Obtains an instance of {@code JapaneseDate} from the proleptic year,
     * month-of-year and day-of-month. 
     *
     * @param prolepticYear  the year to represent in the proleptic year
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Japanese date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static JapaneseDate of(int prolepticYear, int monthOfYear, int dayOfMonth) {
        return JapaneseDate.of(LocalDate.of(prolepticYear, monthOfYear, dayOfMonth));
    }

    /**
     * Obtains an instance of {@code JapaneseDate} from the era, year-of-era,
     * month-of-year and day-of-month.
     *
     * @param era  the era to represent, not null
     * @param yearOfEra  the year-of-era to represent, from 1 to 9999
     * @param monthOfYear  the month-of-year to represent, not null
     * @param dayOfMonth  the day-of-month to represent, from 1 to 31
     * @return the Japanese date, never null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static JapaneseDate of(JapaneseEra era, int yearOfEra, int monthOfYear, int dayOfMonth) {
        DateTimes.checkNotNull(era, "JapaneseEra must not be null");
        yearOfEraCheckValidValue(era, yearOfEra);
        int year = yearOfEra + era.getYearOffset();
        LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
        return new JapaneseDate(date);
    }

    /**
     * Obtains an instance of {@code JapaneseDate} from a date.
     *
     * @param date  the date to use, not null
     * @return the Japanese date, never null
     * @throws IllegalCalendarFieldValueException if the year is invalid
     */
    static JapaneseDate of(LocalDate date) {
        DateTimes.checkNotNull(date, "LocalDate must not be null");
        int yearOfEra = getYearOfEra(date);
        if (yearOfEra < 0) {
            yearOfEra = 1 - yearOfEra;
        }
        JapaneseEra era = JapaneseEra.from(date);
        yearOfEraCheckValidValue(era, yearOfEra);
        return new JapaneseDate(date);
    }

    /**
     * Obtains an instance of {@code JapaneseDate} from the epoch day count.
     * <p>
     * The Epoch Day count is a simple incrementing count of days
     * where day 0 is 1970-01-01. Negative numbers represent earlier days.
     *
     * @param epochDay  the Epoch Day to convert, based on the epoch 1970-01-01
     * @return the JapaneseDate, not null
     * @throws CalendricalException if the epoch days exceeds the supported date range
     */
    public static ChronoDate ofEpochDay(long epochDay) {
        return new JapaneseDate(LocalDate.ofEpochDay(epochDay));
    }

    @Override
    public long get(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (f.isDateField()) {
                switch (f) {
                    case YEAR_OF_ERA: {
                        JapaneseEra era = JapaneseEra.from(date);
                        return date.getYear() - era.getYearOffset();
                    }
                    case YEAR: return date.getYear();
                    case ERA:
                        JapaneseEra era = JapaneseEra.from(date);
                        return era.getValue();
                    default:
                        return date.get(field);
                }
            }
            throw new CalendricalException("Unsupported field: " + field.getName());
        }
        return field.doGet(this);
    }

    @Override
    public JapaneseDate with(DateTimeField field, long newValue) {
        if (field instanceof LocalDateTimeField) {
            LocalDateTimeField f = (LocalDateTimeField) field;
            if (f.isDateField()) {
                switch (f) {
                    case YEAR_OF_ERA:
                    case YEAR:
                    case ERA: {
                        f.checkValidValue(newValue);
                        int nvalue = (int) newValue;
                        switch (f) {
                            case YEAR_OF_ERA: return this.withYearOfEra(nvalue);
                            case YEAR: return with(date.withYear(nvalue));
                            case ERA: {
                                int yearOfEra = this.getYearOfEra();
                                return this.withYear(JapaneseEra.of(nvalue), yearOfEra);
                            }
                        }
                    }
                    default: return with(date.with(field, newValue));
                }
            }
            throw new CalendricalException("Unsupported field: " + field.getName());
        }
        return field.doSet(this, newValue);
    }

    @Override
    public int lengthOfMonth() {
        return date.lengthOfMonth();
    }


    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified date.
     *
     * @param date  the date, validated in range, validated not null
     */
    private JapaneseDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns a new date based on this one, returning {@code this} where possible.
     *
     * @param date  the date to create with, not null
     */
    private JapaneseDate with(LocalDate date) {
        if (this.date.equals(date)) {
            return this;
        }
        return JapaneseDate.of(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that this date uses, which is the Japanese calendar system.
     *
     * @return the Japanese chronology, never null
     */
    @Override
    public JapaneseChronology getChronology() {
        return JapaneseChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the Japanese era field.
     *
     * @return the era, never null
     */
    @Override
    public JapaneseEra getEra() {
        return JapaneseEra.from(date);
    }

    /**
     * Gets the Japanese year-of-era field.
     *
     * @return the year, from 1 to 9999
     */
    @Override
    public int getYearOfEra() {
        return getYearOfEra(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, according to the Japanese calendar system rules.
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
     * @return a {@code JapaneseDate} based on this date with the requested year, never null
     * @throws IllegalCalendarFieldValueException if the year-of-era value is invalid
     */
    public JapaneseDate withYear(JapaneseEra era, int yearOfEra) {
        yearOfEraCheckValidValue(era, yearOfEra);
        int year = yearOfEra + era.getYearOffset();
        return with(date.withYear(year));
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
     * @throws IllegalCalendarFieldValueException if the year-of-era value is invalid
     */
    @Override
    public JapaneseDate withYearOfEra(int yearOfEra) {
        return withYear(getEra(), yearOfEra);
    }

    /**
     * Check if the yearOfEra is valid for the Era.
     * @param era the current Era; not null
     * @param yearOfEra the year supposed to be in the Era.
     * @throws CalendricalException if the year-of-era value is invalid
     */
    private static void yearOfEraCheckValidValue(JapaneseEra era, int yearOfEra) {

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
     * @return a {@code JapaneseDate} based on this date with the years added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    @Override
    public JapaneseDate plusYears(long years) {
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
     * @return a {@code JapaneseDate} based on this date with the months added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    @Override
    public JapaneseDate plusMonths(long months) {
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
     * @return a {@code JapaneseDate} based on this date with the weeks added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    @Override
    public JapaneseDate plusWeeks(long weeks) {
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
     * @return a {@code JapaneseDate} based on this date with the days added, never null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    @Override
    public JapaneseDate plusDays(long days) {
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
    public int compareTo(JapaneseDate other) {
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
    public boolean isAfter(JapaneseDate other) {
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
    public boolean isBefore(JapaneseDate other) {
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
        if (other instanceof JapaneseDate) {
            JapaneseDate otherDate = (JapaneseDate) other;
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
        return "JapaneseDate".hashCode() ^ date.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code SHOWA 48-12-01 (Japanese)}.
     * <p>
     * The output will be in the format {@code Era yy-MM-dd (Japanese)}.
     *
     * @return the formatted date, never null
     */
    @Override
    public String toString() {
        String era = getEra().name();
        int yearValue = getYearOfEra();

        int monthValue = getMonth();
        int dayValue = getDayOfMonth();
        StringBuilder buf = new StringBuilder();
        return buf//.append(era + " ")
                .append(yearValue < 10 ? "0" : "").append(yearValue)
                .append(monthValue < 10 ? "-0" : "-").append(monthValue)
                .append(dayValue < 10 ? "-0" : "-").append(dayValue)
                .append(" (Japanese)").toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns year-of-era from a local date object.
     *
     * @param date  the date, validated in range, validated not null
     * @return year of era
     */
    private static int getYearOfEra(LocalDate date) {
        JapaneseEra era = JapaneseEra.from(date);
        int yearOffset = era.getYearOffset();
        return date.getYear() - yearOffset;
    }

}
