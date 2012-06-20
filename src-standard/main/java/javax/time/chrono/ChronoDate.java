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

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.LocalDate;
import javax.time.calendrical.*;

/**
 * A date expressed in terms of a calendar system.
 * <p>
 * This class is intended for applications that need to use a calendar system other than
 * ISO-8601, the <i>de facto</i> world calendar.
 * <p>
 * This class is limited to storing a date, using the generic concepts of year, month and day.
 * Each calendar system, represented by a {@link Chrono}, defines the exact meaning of each field.
 * Note that not all calendar systems are suitable for use with this class.
 * For example, the Mayan calendar uses a system that bears no relation to years, months and days.
 * 
 * <h4>Implementation notes</h4>
 * This abstract class must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * Subclasses should be Serializable wherever possible.
 */
public abstract class ChronoDate<T extends Chrono>
        implements CalendricalObject, Comparable<ChronoDate<T>> {

    /**
     * Obtains an instance of {@code ChronoDate} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ChronoDate}.
     * <p>
     * If the calendrical can provide a calendar system, then that will be used,
     * otherwise, {@link ISOChrono} will be used.
     * This allows a {@link LocalDate} to be converted to a {@code ChronoDate}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the calendar system specific date, not null
     * @throws CalendricalException if unable to convert to a {@code ChronoDate}
     */
    public static ChronoDate<?> from(CalendricalObject calendrical) {
        ChronoDate<?> cd = calendrical.extract(ChronoDate.class);
        if (cd != null) {
            return cd;
        }
        LocalDate ld = calendrical.extract(LocalDate.class);
        if (ld == null) {
            Chrono chrono = calendrical.extract(Chrono.class);
            chrono = (chrono != null ? chrono : ISOChrono.INSTANCE);
            return chrono.date(ld);
        }
        throw new CalendricalException("Unable to convert calendrical to ChronoDate: " + calendrical.getClass() + " " + calendrical);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     */
    protected ChronoDate() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendar system in use for this date.
     * <p>
     * The {@code Chrono} represents the calendar system.
     * The fields of this date are all expressed relative to this.
     * 
     * @return the calendar system, not null
     */
    public abstract Chrono getChronology();

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified date field.
     * <p>
     * This method queries the value of the specified field.
     * The field specified is chronology-neutral.
     * The same set of fields are used to describe all chronologies.
     *
     * @param field  the field to query, not null
     * @return the value of the field
     */
    public abstract long get(DateTimeField field);

    /**
     * Gets the era, as defined by the calendar system.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The era in use at 1970-01-01 (ISO) must have the value 1.
     * Later eras must have sequentially higher values.
     * Earlier eras must have sequentially lower values.
     * Each chronology must refer to an enum or similar singleton to provide the era values.
     * <p>
     * All correctly implemented {@code Era} classes are singletons, thus it
     * is valid code to write {@code date.getEra() == SomeEra.ERA_NAME)}.
     *
     * @return the era, of the correct type for this chronology, not null
     */
    public Era getEra() {
        return getChronology().createEra(DateTimes.safeToInt(get(LocalDateTimeField.ERA)));
    }

    /**
     * Gets the year-of-era, as defined by the calendar system.
     * <p>
     * The year-of-era is a value representing the count of years within the era.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The year-of-era value must be positive.
     *
     * @return the year-of-era, within the valid range for the chronology
     */
    public int getYearOfEra() {
        return DateTimes.safeToInt(get(LocalDateTimeField.YEAR_OF_ERA));
    }

    /**
     * Gets the proleptic-year, as defined by the calendar system.
     * <p>
     * The proleptic-year is a single value representing the year.
     * It combines the era and year-of-era, and increases uniformly as time progresses.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The proleptic-year has a small, or negative, value in the past.
     * Later years have sequentially higher values.
     * Where possible, the proleptic-year will be the same as the year-of-era
     * for the era that is active on 1970-01-01 however this is not guaranteed.
     *
     * @return the proleptic-year, within the valid range for the chronology
     */
    public int getProlepticYear() {
        return DateTimes.safeToInt(get(LocalDateTimeField.PROLEPTIC_YEAR));
    }

    /**
     * Gets the month-of-year, as defined by the calendar system.
     * <p>
     * The month-of-year is a value representing the count of months within the year.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The month-of-year value must be positive.
     *
     * @return the month-of-year, within the valid range for the chronology
     */
    public int getMonthOfYear() {
        return DateTimes.safeToInt(get(LocalDateTimeField.MONTH_OF_YEAR));
    }

    /**
     * Gets the day-of-month, as defined by the calendar system.
     * <p>
     * The day-of-month is a value representing the count of days within the month.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The day-of-month value must be positive.
     *
     * @return the day-of-month, within the valid range for the chronology
     */
    public int getDayOfMonth() {
        return DateTimes.safeToInt(get(LocalDateTimeField.DAY_OF_MONTH));
    }

    /**
     * Gets the day-of-year, as defined by the calendar system.
     * <p>
     * The day-of-year is a value representing the count of days within the year.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The day-of-year value must be positive.
     * The number of days in a year may vary.
     *
     * @return the day-of-year, within the valid range for the chronology
     */
    public int getDayOfYear() {
        return DateTimes.safeToInt(get(LocalDateTimeField.DAY_OF_YEAR));
    }

    /**
     * Gets the day-of-week value for the calendar system.
     * <p>
     * The day-of-week is a value representing the count of days within the week.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The day-of-week value must be positive.
     * The number of days in a week may vary.
     * 
     * @return the day-of-week value
     */
    public int getDayOfWeek() {
        return DateTimes.safeToInt(get(LocalDateTimeField.DAY_OF_WEEK));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year, as defined by the calendar system.
     * <p>
     * A leap-year is a year of a longer length than normal.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * A leap-year must imply a year-length longer than a non leap-year.
     *
     * @return true if this date is in a leap year, false otherwise
     */
    public boolean isLeapYear() {
        return getChronology().isLeapYear(getProlepticYear());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified field altered.
     * <p>
     * This method returns a new date based on this date with a new value for the specified field.
     * This can be used to change any field, for example to set the year, month of day-of-month.
     * The field specified is chronology-neutral.
     * The same set of fields are used to describe all chronologies.
     * <p>
     * In some cases, changing the specified field can cause the resulting date to become invalid.
     * If this occurs, then other fields, typically the day-of-month, will be adjusted to ensure
     * that the result is valid. Typically this will select the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the returned date, not null
     * @param newValue  the new value of the field in the returned date, not null
     * @return a date based on this one with the specified field set, not null
     */
    public abstract ChronoDate<T> with(DateTimeField field, int newValue);

    /**
     * Returns a copy of this date with the specified era.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param era  the era to set, not null
     * @return a date based on this one with the years added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronoDate<T> withEra(Era era) {
        return with(LocalDateTimeField.ERA, era.getValue());
    }

    /**
     * Returns a copy of this date with the specified proleptic-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param prolepticYear  the proleptic-year to set
     * @return a date based on this one with the specified proleptic-year, not null
     */
    public ChronoDate<T> withProlepticYear(int prolepticYear) {
        return with(LocalDateTimeField.PROLEPTIC_YEAR, prolepticYear);
    }

    /**
     * Returns a copy of this date with the specified year-of-era.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearOfEra  the year-of-era to set
     * @return a date based on this one with the specified year-of-era, not null
     */
    public ChronoDate<T> withYearOfEra(int yearOfEra) {
        return with(LocalDateTimeField.YEAR_OF_ERA, yearOfEra);
    }

    /**
     * Returns a copy of this date with the specified month-of-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to set
     * @return a date based on this one with the specified month-of-year, not null
     */
    public ChronoDate<T> withMonthOfYear(int monthOfYear) {
        return with(LocalDateTimeField.MONTH_OF_YEAR, monthOfYear);
    }

    /**
     * Returns a copy of this date with the specified day-of-month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set
     * @return a date based on this one with the specified day-of-month, not null
     */
    public ChronoDate<T> withDayOfMonth(int dayOfMonth) {
        return with(LocalDateTimeField.DAY_OF_MONTH, dayOfMonth);
    }

    /**
     * Returns a copy of this date with the specified day-of-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day-of-year to set
     * @return a date based on this one with the specified day-of-year, not null
     */
    public ChronoDate<T> withDayOfYear(int dayOfYear) {
        return with(LocalDateTimeField.DAY_OF_YEAR, dayOfYear);
    }

    /**
     * Returns a copy of this date with the specified day-of-week.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day-of-week to set
     * @return a date based on this one with the specified day-of-week, not null
     */
    public ChronoDate<T> withDayOfWeek(int dayOfWeek) {
        return with(LocalDateTimeField.DAY_OF_WEEK, dayOfWeek);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period in years added.
     * <p>
     * This adds the specified period in years to the date.
     * In some cases, adding years can cause the resulting date to become invalid.
     * If this occurs, then other fields, typically the day-of-month, will be adjusted to ensure
     * that the result is valid. Typically this will select the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a date based on this one with the years added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public abstract ChronoDate<T> plusYears(long years);

    /**
     * Returns a copy of this date with the specified period in months added.
     * <p>
     * This adds the specified period in months to the date.
     * In some cases, adding months can cause the resulting date to become invalid.
     * If this occurs, then other fields, typically the day-of-month, will be adjusted to ensure
     * that the result is valid. Typically this will select the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a date based on this one with the months added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public abstract ChronoDate<T> plusMonths(long months);

    /**
     * Returns a copy of this date with the specified period in weeks added.
     * <p>
     * This adds the specified period in weeks to the date.
     * In some cases, adding weeks can cause the resulting date to become invalid.
     * If this occurs, then other fields will be adjusted to ensure that the result is valid.
     * <p>
     * The default implementation uses {@link #plusDays(long)} using a 7 day week.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a date based on this one with the weeks added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronoDate<T> plusWeeks(long weeks) {
        return plusDays(DateTimes.safeMultiply(weeks, 7));
    }

    /**
     * Returns a copy of this date with the specified number of days added.
     * <p>
     * This adds the specified period in days to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a date based on this one with the days added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public abstract ChronoDate<T> plusDays(long days);

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period in years subtracted.
     * <p>
     * This subtracts the specified period in years to the date.
     * In some cases, subtracting years can cause the resulting date to become invalid.
     * If this occurs, then other fields, typically the day-of-month, will be adjusted to ensure
     * that the result is valid. Typically this will select the last valid day of the month.
     * <p>
     * The default implementation uses {@link #plusYears(long)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return a date based on this one with the years subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronoDate<T> minusYears(long years) {
        return plusYears(DateTimes.safeNegate(years));
    }

    /**
     * Returns a copy of this date with the specified period in months subtracted.
     * <p>
     * This subtracts the specified period in months to the date.
     * In some cases, subtracting months can cause the resulting date to become invalid.
     * If this occurs, then other fields, typically the day-of-month, will be adjusted to ensure
     * that the result is valid. Typically this will select the last valid day of the month.
     * <p>
     * The default implementation uses {@link #plusMonths(long)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract, may be negative
     * @return a date based on this one with the months subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronoDate<T> minusMonths(long months) {
        return plusMonths(DateTimes.safeNegate(months));
    }

    /**
     * Returns a copy of this date with the specified period in weeks subtracted.
     * <p>
     * This subtracts the specified period in weeks to the date.
     * In some cases, subtracting weeks can cause the resulting date to become invalid.
     * If this occurs, then other fields will be adjusted to ensure that the result is valid.
     * <p>
     * The default implementation uses {@link #plusWeeks(long)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to subtract, may be negative
     * @return a date based on this one with the weeks subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronoDate<T> minusWeeks(long weeks) {
        return plusWeeks(DateTimes.safeNegate(weeks));
    }

    /**
     * Returns a copy of this date with the specified number of days subtracted.
     * <p>
     * This subtracts the specified period in days to the date.
     * <p>
     * The default implementation uses {@link #plusDays(long)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a date based on this one with the days subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronoDate<T> minusDays(long days) {
        return plusDays(DateTimes.safeNegate(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link CalendricalObject} interface.
     * This implementation returns the following types:
     * <ul>
     * <li>LocalDate
     * <li>ChronoDate
     * <li>Chrono
     * <li>DateTimeBuilder
     * <li>Class, returning {@code ChronoDate}
     * </ul>
     * 
     * @param <R> the type to extract
     * @param type  the type to extract, null returns null
     * @return the extracted object, null if unable to extract
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == ChronoDate.class) {
            return (R) this;
        } else if (type == LocalDate.class) {
            return (R) toLocalDate();
        } else if (type == Chrono.class) {
            return (R) getChronology();
        } else if (type == Class.class) {
            return (R) ChronoDate.class;
        } else if (type == DateTimeBuilder.class) {
            return (R) new DateTimeBuilder(this);
        }
        return null;
    }

    @Override
    public ChronoDate<?> with(CalendricalAdjuster adjuster) {
        // TODO: chrono
        if (adjuster instanceof DateAdjuster) {
            return getChronology().date(((DateAdjuster) adjuster).adjustDate(toLocalDate()));
        } else if (adjuster instanceof LocalDate) {
            return getChronology().date((LocalDate) adjuster);
        } else if (adjuster instanceof ChronoDate) {
            return ((ChronoDate<?>) adjuster);
        }
        DateTimes.checkNotNull(adjuster, "Adjuster must not be null");
        throw new CalendricalException("Unable to adjust ChronoDate with " + adjuster.getClass().getSimpleName());
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to the standard epoch-day from 1970-01-01 (ISO).
     * <p>
     * This converts this date to the equivalent standard ISO date.
     * The conversion ensures that the date is accurate at midday.
     * <p>
     * The default implementation uses {@link #toLocalDate()}.
     * Either this method or that method must be overridden.
     * 
     * @return the equivalent date, not null
     */
    public long toEpochDay() {
        return toLocalDate().toEpochDay();
    }

    /**
     * Converts this date to the standard {@code LocalDate}.
     * <p>
     * This converts this date to the equivalent standard ISO date.
     * The conversion ensures that the date is accurate at midday.
     * <p>
     * The default implementation uses {@link #toEpochDay()}.
     * Either this method or that method must be overridden.
     * 
     * @return the equivalent date, not null
     */
    public LocalDate toLocalDate() {
        return LocalDate.ofEpochDay(toEpochDay());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     * Only two dates with the same calendar system can be compared.
     * <p>
     * The default implementation uses {@link #getProlepticYear()}, {@link #getMonthOfYear()}
     * and {@link #getDayOfMonth()}.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(ChronoDate<T> other) {
        int cmp = DateTimes.safeCompare(getProlepticYear(), other.getProlepticYear());
        if (cmp == 0) {
            cmp = DateTimes.safeCompare(getMonthOfYear(), other.getMonthOfYear());
            if (cmp == 0) {
                cmp = DateTimes.safeCompare(getDayOfMonth(), other.getDayOfMonth());
            }
        }
        return cmp;
    }

    /**
     * Checks if this date is equal to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     * Only two dates with the same calendar system will compare equal.
     * <p>
     * The default implementation uses {@link #getChronology()},  #getProlepticYear()},
     * {@link #getMonthOfYear()} and {@link #getDayOfMonth()}.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChronoDate<?>) {
            ChronoDate<?> other = (ChronoDate<?>) obj;
            return getChronology().equals(other.getChronology()) &&
                    getProlepticYear() == other.getProlepticYear() &&
                    getMonthOfYear() == other.getMonthOfYear() &&
                    getDayOfMonth() == other.getDayOfMonth();
        }
        return false;
    }

    /**
     * A hash code for this date.
     * <p>
     * The default implementation uses {@link #getChronology()},  #getProlepticYear()},
     * {@link #getMonthOfYear()} and {@link #getDayOfMonth()}.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return getChronology().hashCode() ^ Integer.rotateLeft(getProlepticYear(), 16) ^ (getMonthOfYear() << 8) ^ getDayOfMonth();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code 1723AD-13-01 (Gregorian)}.
     * <p>
     * The output will be in the format {@code {year}{era}-{month}-{day} ({chrono})}.
     *
     * @return the formatted date, not null
     */
    @Override
    public String toString() {
        int yearValue = getYearOfEra();
        int monthValue = getMonthOfYear();
        int dayValue = getDayOfMonth();
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(12);
        if (absYear < 1000) {
            buf.append(yearValue + 10000).deleteCharAt(0);
        } else {
            buf.append(yearValue);
        }
        return buf.append(getEra())
            .append(monthValue < 10 ? "-0" : "-").append(monthValue)
            .append(dayValue < 10 ? "-0" : "-").append(dayValue)
            .append(" (").append(getChronology().getName()).append(')')
            .toString();
    }

}
