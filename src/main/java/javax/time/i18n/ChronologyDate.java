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
//    /**
//     * The calculated era.
//     */
//    private final transient int era;
//    /**
//     * The calculated year.
//     */
//    private final transient int year;
//    /**
//     * The calculated month.
//     */
//    private final transient int month;
//    /**
//     * The calculated day.
//     */
//    private final transient int day;

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
     * Obtains a date for a chronology from the year, month-of-year and day-of-month.
     * <p>
     * This will use the era in use at the epoch of 1970-01-01.
     *
     * @param chrono  the chronology, not null
     * @param year  the year to represent, within the valid range for the chronology
     * @param monthOfYear  the month-of-year to represent, within the valid range for the chronology
     * @param dayOfMonth  the day-of-month to represent, within the valid range for the chronology
     * @return the calendar system date, not null
     * @throws IllegalCalendarFieldValueException if the value of any field is out of range
     * @throws InvalidCalendarFieldException if the day-of-month is invalid for the month-year
     */
    public static ChronologyDate of(Chronology chrono, int year, int monthOfYear, int dayOfMonth) {
        checkNotNull(chrono, "Chronology must not be null");
        if (chrono instanceof StandardChronology == false) {
            throw new IllegalArgumentException("Chronology does not implement StandardChronology");
        }
        StandardChronology schrono = (StandardChronology) chrono;
//        chrono.year().checkValidValue(year);
//        chrono.monthOfYear().checkValidValue(monthOfYear);
//        chrono.dayOfMonth().checkValidValue(dayOfMonth);
        LocalDate date = schrono.merge(1, year, monthOfYear, dayOfMonth);
//        DateTimeFields fields = DateTimeFields.of(schrono.year(), year, schrono.monthOfYear(), monthOfYear)
//            .with(schrono.dayOfMonth(), dayOfMonth);
//        LocalDate date = LocalDate.rule().getValueChecked(fields);
        return new ChronologyDate(schrono, date);
    }

    /**
     * Obtains a date for a chronology from an ISO-8601 date.
     *
     * @param chrono  the chronology, not null
     * @param date  the standard ISO-8601 date representation, not null
     * @return the calendar system date, not null
     */
    public static ChronologyDate of(Chronology chrono, LocalDate date) {
        checkNotNull(chrono, "Chronology must not be null");
        checkNotNull(date, "LocalDate must not be null");
        if (chrono instanceof StandardChronology == false) {
            throw new IllegalArgumentException("Chronology does not implement StandardChronology");
        }
        return new ChronologyDate((StandardChronology) chrono, date);
    }

    /**
     * Obtains a date for a chronology from a calendrical.
     * <p>
     * This can be used extract the date directly from any implementation
     * of {@code Calendrical}, including those in other calendar systems.
     *
     * @param chrono  the chronology, not null
     * @param calendrical  the calendrical to extract from, not null
     * @return the calendar system date, not null
     * @throws CalendricalException if the date cannot be obtained
     */
    public static ChronologyDate of(Chronology chrono, Calendrical calendrical) {
        LocalDate date = LocalDate.rule().getValueChecked(calendrical);
        return of(chrono, date);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance with the specified chronology and date.
     *
     * @param chrono  the chronology, validated not null
     * @param date  the date, validated not null
     */
    private ChronologyDate(StandardChronology chrono, LocalDate date) {
        this.chrono = chrono;
        this.date = date;
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
     * Gets the era as defined by the chronology.
     * <p>
     * The meaning of the result of this method is determined by the chronology.
     * <p>
     * The era in use at 1970-01-01 has the value 1.
     * Later eras have sequentially higher values.
     * Earlier eras have sequentially lower values.
     * Each chronology should have constants providing meaning to the era value.
     *
     * @return the era, within the valid range for the chronology
     */
    public int getEra() {
        return chrono.getEra(date);
    }

    /**
     * Gets the year-of-era as defined by the chronology.
     * <p>
     * The meaning of the result of this method is determined by the chronology.
     *
     * @return the year, within the valid range for the chronology
     */
    public int getYearOfEra() {
        return chrono.getYearOfEra(date);
    }

    /**
     * Gets the month-of-year as defined by the chronology.
     * <p>
     * The meaning of the result of this method is determined by the chronology.
     *
     * @return the month-of-year, within the valid range for the chronology
     */
    public int getMonthOfYear() {
        return chrono.getMonthOfYear(date);
    }

    /**
     * Gets the day-of-month as defined by the chronology.
     * <p>
     * The meaning of the result of this method is determined by the chronology.
     *
     * @return the day-of-month, within the valid range for the chronology
     */
    public int getDayOfMonth() {
        return chrono.getDayOfMonth(date);
    }

    /**
     * Gets the day-of-year as defined by the chronology.
     * <p>
     * The meaning of the result of this method is determined by the chronology.
     *
     * @return the day-of-year, within the valid range for the chronology
     */
    public int getDayOfYear() {
        return chrono.getDayOfYear(date);
    }

    /**
     * Gets the day-of-week, which is an enum {@code DayOfWeek}.
     * <p>
     * All chronologies that can be used with this class have a standard seven day week
     * in line with ISO-8601, hence the use of the {@link DayOfWeek} enum.
     *
     * @return the day-of-week, not null
     */
    public DayOfWeek getDayOfWeek() {
        return chrono.getDayOfWeek(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the year is a leap year as defined by the chronology.
     *
     * @return true if this date is in a leap year
     */
    public boolean isLeapYear() {
        return chrono.isLeapYear(date);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the chronology altered.
     * <p>
     * This method changes the stored chronology.
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
     * Compares this date to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(ChronologyDate other) {
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
    public boolean isAfter(ChronologyDate other) {
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
    public boolean isBefore(ChronologyDate other) {
        return date.isBefore(other.date);
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
