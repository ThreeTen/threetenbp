/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.i18n;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.MathUtils;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.Chronology;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.InvalidCalendarFieldException;
import javax.time.calendar.LocalDate;

/**
 * A date based on standard chronology rules.
 * <p>
 * The majority of the Time Framework for Java is based on the ISO-8601 chronology which
 * is the <i>de facto</i> world calendar.
 * This class allows dates in other calendar systems to be stored.
 * <p>
 * Not all calendar systems are suitable for use with this class.
 * For example, the Mayan calendar uses a system that bears no relation to years, months and days.
 * A calendar system may be used if the {@link Chronology} supports the standard set of rules
 * based on era, year, month and day-of-month.
 * <p>
 * Unlike {@link LocalDate}, this class uses {@code int} values for all fields.
 * This is a pragmatic choice to avoid coding one class for each calendar system.
 * Application writers working primarily with another calendar system may choose to write
 * a dedicated class, in the same way as {@code LocalDate} is dedicated to ISO-8601.
 * <p>
 * Instances of this class may be created from other date objects that implement {@code Calendrical}.
 * Notably this includes {@code LocalDate} and all other date classes from other calendar systems.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ChronologyDate
        implements DateProvider, Calendrical, Comparable<ChronologyDate>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The chronology.
     */
    private final StandardChronology chrono;
    /**
     * The underlying local date.
     */
    private final LocalDate date;
    /**
     * The proleptic year.
     */
    private final transient int prolepticYear;
    /**
     * The month.
     */
    private final transient int monthOfYear;
    /**
     * The day.
     */
    private final transient int dayOfMonth;

    //-----------------------------------------------------------------------
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a date for a chronology from the era, year-of-era, month-of-year and day-of-month.
     * <p>
     * This year used here is the {@link #getYearOfEra() year-of-era}.
     * The exact meaning of each field is determined by the chronology.
     *
     * @param chrono  the {@code StandardChronology}, not null
     * @param era  the era to represent, valid for this chronology, not null
     * @param yearOfEra  the year-of-era to represent, within the valid range for the chronology
     * @param monthOfYear  the month-of-year to represent, within the valid range for the chronology
     * @param dayOfMonth  the day-of-month to represent, within the valid range for the chronology
     * @return the calendar system date, not null
     * @throws ClassCastException if the chronology is not a {@code StandardChronology}
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static ChronologyDate of(Chronology chrono, Era era, int yearOfEra, int monthOfYear, int dayOfMonth) {
        // accept Chronology rather than StandardChronology to aid interoperability
        checkNotNull(chrono, "Chronology must not be null");
        if (chrono instanceof StandardChronology == false) {
            throw new ClassCastException("Chronology must implement StandardChronology");
        }
        StandardChronology schrono = (StandardChronology) chrono;
        return schrono.createDate(era, yearOfEra, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains a date for a chronology from the year, month-of-year and day-of-month.
     * <p>
     * This year used here is the {@link #getProlepticYear() proleptic-year}.
     * The exact meaning of each field is determined by the chronology.
     * The proleptic-year is typically the same as the year-of-era for the
     * era that is active on 1970-01-01.
     *
     * @param chrono  the {@code StandardChronology}, not null
     * @param prolepticYear  the proleptic-year to represent, within the valid range for the chronology
     * @param monthOfYear  the month-of-year to represent, within the valid range for the chronology
     * @param dayOfMonth  the day-of-month to represent, within the valid range for the chronology
     * @return the calendar system date, not null
     * @throws ClassCastException if the chronology is not a {@code StandardChronology}
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static ChronologyDate of(Chronology chrono, int prolepticYear, int monthOfYear, int dayOfMonth) {
        // accept Chronology rather than StandardChronology to aid interoperability
        checkNotNull(chrono, "Chronology must not be null");
        if (chrono instanceof StandardChronology == false) {
            throw new ClassCastException("Chronology must implement StandardChronology");
        }
        StandardChronology schrono = (StandardChronology) chrono;
        return schrono.createDate(prolepticYear, monthOfYear, dayOfMonth);
    }

    /**
     * Obtains a date for a chronology from an ISO-8601 date.
     * <p>
     * This will return a date in the specified chronology.
     * The chronology must implement {@link StandardChronology}.
     *
     * @param chrono  the {@code StandardChronology}, not null
     * @param date  the standard ISO-8601 date representation, not null
     * @return the calendar system date, not null
     * @throws ClassCastException if the chronology is not a {@code StandardChronology}
     */
    public static ChronologyDate of(Chronology chrono, LocalDate date) {
        // accept Chronology rather than StandardChronology to aid interoperability
        checkNotNull(chrono, "Chronology must not be null");
        checkNotNull(date, "LocalDate must not be null");
        if (chrono instanceof StandardChronology == false) {
            throw new ClassCastException("Chronology must implement StandardChronology");
        }
        StandardChronology schrono = (StandardChronology) chrono;
        return schrono.createDate(date);
    }

    /**
     * Obtains a date for a chronology from a calendrical.
     * <p>
     * This will return a date in the specified chronology.
     * The chronology must implement {@link StandardChronology}.
     * The underlying {@link LocalDate} is extracted from the calendrical, thus this
     * method can be used with objects such as {@link javax.time.calendar.OffsetDate}
     * or {@link javax.time.calendar.ZonedDateTime}.
     *
     * @param chrono  the {@code StandardChronology}, not null
     * @param calendrical  the calendrical to extract from, not null
     * @return the calendar system date, not null
     * @throws CalendricalException if the date cannot be obtained
     * @throws ClassCastException if the chronology is not a {@code StandardChronology}
     */
    public static ChronologyDate of(Chronology chrono, Calendrical calendrical) {
        // accept Chronology rather than StandardChronology to aid interoperability
        LocalDate date = LocalDate.rule().getValueChecked(calendrical);
        return of(chrono, date);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified chronology and date.
     *
     * @param chrono  the chronology, validated not null
     * @param date  the date, validated not null
     * @param year  the proleptic-year to represent, within the valid range for the chronology
     * @param monthOfYear  the month-of-year to represent, within the valid range for the chronology
     * @param dayOfMonth  the day-of-month to represent, within the valid range for the chronology
     */
    ChronologyDate(StandardChronology chrono, LocalDate date, int year, int monthOfYear, int dayOfMonth) {
        this.chrono = chrono;
        this.date = date;
        this.prolepticYear= year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
    }

    /**
     * Resolve the transient fields.
     * 
     * @return the resolved date, not null
     */
    private Object readResolve() {
        return chrono.createDate(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology that this date uses.
     *
     * @return the chronology, not null
     */
    public Chronology getChronology() {
        return chrono;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this date then
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        if (rule.equals(LocalDate.rule())) {  // NPE check
            return rule.reify(date);
        }
        return chrono.dateRule().deriveValueFor(rule, this, this, chrono);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the era, as defined by the calendar system.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The era in use at 1970-01-01 must have the value 1.
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
        return chrono.getEra(this);
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
        return chrono.getYearOfEra(this);
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
        return prolepticYear;
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
        return monthOfYear;
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
        return dayOfMonth;
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
        return chrono.getDayOfYear(this);
    }

    /**
     * Gets the day-of-week.
     * <p>
     * All calendar systems that can be used with this class have a standard
     * seven day week exactly in line with ISO-8601.
     * As such, the same {@link DayOfWeek} enum is used.
     *
     * @return the day-of-week, not null
     */
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
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
     * @return true if this date is in a leap year
     */
    public boolean isLeapYear() {
        return chrono.isLeapYear(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the chronology altered.
     * <p>
     * This method returns a date object with the same local date in the specified chronology.
     * This allows the view of the underlying date to be changed to a different calendar system.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param chrono  the chronology to set in the result, not null
     * @return a date based on this one with the requested chronology, not null
     */
    public ChronologyDate withChronology(Chronology chrono) {
        if (this.chrono.equals(chrono)) {
            return this;
        }
        return ChronologyDate.of(chrono, date);
    }

    /**
     * Returns a copy of this date with the date altered.
     * <p>
     * This method changes the stored date.
     * This allows the date to be changed while retaining the calendar system.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param date  the date to set in the result, not null
     * @return a date based on this one with the requested chronology, not null
     */
    public ChronologyDate withDate(LocalDate date) {
        if (this.date.equals(date)) {
            return this;
        }
        return ChronologyDate.of(chrono, date);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the year value altered.
     * <p>
     * This changes the era and year-of-era fields.
     * The result of setting the year may leave another field, such as the
     * day-of-month invalid. To avoid this, other fields may also be changed.
     * For example, the day-of-month may be changed to the largest valid value.
     * <p>
     * For example, consider the ISO calendar system.
     * Setting the year on 2012-02-29 to 2010 yields 2010-02-28.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param era  the era to represent, valid for the chronology, not null
     * @param yearOfEra  the year-of-era to represent, within the valid range for the chronology
     * @return a {@code ChronologyDate} based on this date with the specified year-of-era, not null
     * @throws IllegalCalendarFieldValueException if the year-of-era is out of range
     */
    public ChronologyDate withYear(Era era, int yearOfEra) {
        return chrono.createDate(era, yearOfEra, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this date with the year-of-era value altered.
     * <p>
     * The result of setting the year may leave another field, such as the
     * day-of-month invalid. To avoid this, other fields may also be changed.
     * For example, the day-of-month may be changed to the largest valid value.
     * <p>
     * For example, consider the ISO calendar system.
     * Setting the year-of-era on 2012-02-29 to 2010 yields 2010-02-28.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearOfEra  the year-of-era to represent, within the valid range for the chronology
     * @return a {@code ChronologyDate} based on this date with the specified year-of-era, not null
     * @throws IllegalCalendarFieldValueException if the year-of-era is out of range
     */
    public ChronologyDate withYearOfEra(int yearOfEra) {
        return chrono.createDate(getEra(), yearOfEra, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this date with the month-of-year value altered.
     * <p>
     * The result of setting the month-of-year may leave another field, such as the
     * day-of-month invalid. To avoid this, other fields may also be changed.
     * For example, the day-of-month may be changed to the largest valid value.
     * <p>
     * For example, consider the ISO calendar system.
     * Setting the month on 2010-01-31 to 2 yields 2010-02-28.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month-of-year to represent, within the valid range for the chronology
     * @return a {@code ChronologyDate} based on this date with the specified month-of-year, not null
     * @throws IllegalCalendarFieldValueException if the month-of-year is out of range
     */
    public ChronologyDate withMonthOfYear(int monthOfYear) {
        return chrono.createDate(prolepticYear, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this date with the day-of-month value altered.
     * <p>
     * The calendar system may support months with different lengths.
     * If the day-of-month is within the maximum month length, but less than
     * the actual month length for the current month, then the date will
     * be changed to the following month.
     * <p>
     * For example, consider 2010-02-15 in the ISO calendar system.
     * Setting the day-of-month to 28 yields 2010-02-28.
     * Setting the day-of-month to 29 yields 2010-03-01.
     * Setting the day-of-month to 30 yields 2010-03-02.
     * Setting the day-of-month to 31 yields 2010-03-03.
     * Setting the day-of-month to 32 throws an exception.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to represent, within the valid range for the chronology
     * @return a {@code ChronologyDate} based on this date with the specified day-of-month, not null
     * @throws IllegalCalendarFieldValueException if the day-of-month is out of range
     */
    public ChronologyDate withDayOfMonth(int dayOfMonth) {
        return chrono.createDate(prolepticYear, monthOfYear, dayOfMonth);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified number of years added.
     * <p>
     * This method adds the specified amount in years to the date.
     * Other fields will be adjusted as necessary.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a date based on this date with the years added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronologyDate plusYears(long years) {
        if (years == 0) {
            return this;
        }
        int newYears = MathUtils.safeToInt(MathUtils.safeAdd(prolepticYear, years));
        return ChronologyDate.of(chrono, newYears, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this date with the specified number of days added.
     * <p>
     * This method adds the specified amount in days to the date.
     * Other fields will be adjusted as necessary.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a date based on this date with the days added, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronologyDate plusDays(long days) {
        if (days == 0) {
            return this;
        }
        return ChronologyDate.of(chrono, date.plusDays(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified number of years subtracted.
     * <p>
     * This method subtracts the specified amount in years from the date.
     * Other fields will be adjusted as necessary.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract, may be negative
     * @return a date based on this date with the years subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronologyDate minusYears(long years) {
        if (years == 0) {
            return this;
        }
        int newYears = MathUtils.safeToInt(MathUtils.safeSubtract(prolepticYear, years));
        return ChronologyDate.of(chrono, newYears, monthOfYear, dayOfMonth);
    }

    /**
     * Returns a copy of this date with the specified number of days subtracted.
     * <p>
     * This method subtracts the specified amount in days from the date.
     * Other fields will be adjusted as necessary.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a date based on this date with the days subtracted, not null
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public ChronologyDate minusDays(long days) {
        if (days == 0) {
            return this;
        }
        return ChronologyDate.of(chrono, date.minusDays(days));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date to a {@code LocalDate}, which is the default representation
     * of a date, and provides values in the ISO-8601 calendar system.
     *
     * @return the equivalent date in the ISO-8601 calendar system, not null
     */
    public LocalDate toLocalDate() {
        return date;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another based on the ISO equivalent local date
     * and chronology.
     * <p>
     * The chronology is included to ensure that the ordering is consistent
     * with {@code equals()}. To compare just the underlying local date,
     * use the rule as a comparator:
     * <pre>
     *   LocalDate.rule().compare(date1, date2);    // single comparison...
     *   Collections.sort(list, LocalDate.rule());  // or, sort a list
     * </pre>
     * The relative methods like {@link #isAfter} also compare based solely
     * on the ISO equivalent local date.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(ChronologyDate other) {
        int cmp = date.compareTo(other.date);
        return cmp == 0 ? 0 : chrono.getName().compareTo(other.chrono.getName());
    }

    /**
     * Checks if this date is after the specified date.
     * <p>
     * The comparison is based on the underlying time-line position
     * of the ISO equivalent local dates ignoring the chronology.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is after the specified date
     */
    public boolean isAfter(ChronologyDate other) {
        return date.isAfter(other.date);
    }

    /**
     * Checks if this date is before the specified date.
     * <p>
     * The comparison is based on the underlying time-line position
     * of the ISO equivalent local dates ignoring the chronology.
     *
     * @param other  the other date to compare to, not null
     * @return true if this is before the specified date
     */
    public boolean isBefore(ChronologyDate other) {
        return date.isBefore(other.date);
    }

    /**
     * Checks if this date equals the specified date
     * <p>
     * The comparison is based on the underlying time-line position
     * of the ISO equivalent local dates ignoring the chronology. By contrast,
     * {@link #compareTo} and {@link #equals} take the chronology into account.
     *
     * @param other  the other date to compare to, not null
     * @return true if the instant equals the instant of the specified date
     */
    public boolean equalDate(ChronologyDate other) {
        return date.equals(other.date);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to the specified date.
     * <p>
     * This compares the chronology and date.
     *
     * @param other  the other date to compare to, null returns false
     * @return true if this is equal to the specified date
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ChronologyDate) {
            ChronologyDate otherDate = (ChronologyDate) other;
            return chrono.equals(otherDate.chrono) && date.equals(otherDate.date);
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
        return chrono.hashCode() ^ date.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code 1723-13-01 (Coptic)}.
     * <p>
     * The output will be in the format {@code yyyy-MM-dd ({chronoName})}.
     *
     * @return the formatted date, not null
     */
    @Override
    public String toString() {
        // TODO: era
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
        return buf.append(monthValue < 10 ? "-0" : "-")
            .append(monthValue)
            .append(dayValue < 10 ? "-0" : "-")
            .append(dayValue)
            .append(" (" + chrono.getName() + ")")
            .toString();
    }

}
