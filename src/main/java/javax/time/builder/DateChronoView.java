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

package javax.time.builder;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.Clock;
import javax.time.LocalDate;
import javax.time.MathUtils;

/**
 * Stores a combination of LocalDate and Chronology, providing a view on the combination.
 * <p>
 * DateChronoView is immutable and thread-safe.
 * 
 * @param <T> the type of the calendar system
 * @author Richard Warburton
 */
public final class DateChronoView<T extends Chrono> implements Comparable<DateChronoView<T>>, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The underlying date.
     */
    private final LocalDate date;
    /**
     * The calendar system.
     */
    private final T chronology;

    /**
     * Obtains a view of a date as seen through a specific chronology.
     * 
     * @param date  the underlying date, not null
     * @param chronology  the calendar system to view the date using, not null
     * @return the calendar system date view, not null
     */
    public static <T extends Chrono> DateChronoView<T> of(LocalDate date, T chronology) {
        MathUtils.checkNotNull(date, "LocalDate must not be null");
        MathUtils.checkNotNull(chronology, "Chronology must not be null");
        return new DateChronoView<T>(date, chronology);
    }

    /**
     * Obtains a view of the current date as seen through a specific chronology.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock, not null
     */
    public static <T extends Chrono> DateChronoView<T> now(T chronology) {
        return DateChronoView.of(LocalDate.now(), chronology);
    }

    /**
     * Creates an instance.
     * 
     * @param date  the underlying date, validated not null
     * @param chronology  the calendar system to view the date using, validated not null
     */
    private DateChronoView(LocalDate date, T chronology) {
        super();
        this.date = date;
        this.chronology = chronology;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the underlying date expressed as a {@code LocalDate}.
     * 
     * @return the underlying date, not null
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the chronology.
     * <p>
     * The chronology is typically equivalent to a calendar system.
     * 
     * @return the underlying chronology, not null
     */
    public T getChronology() {
        return chronology;
    }

    //-----------------------------------------------------------------------
    // TODO: are these methods necessary?
    public int getEra() {
        return (int) getValue(StandardDateTimeField.ERA);
    }

    public int getYearOfEra() {
        return (int) getValue(StandardDateTimeField.YEAR_OF_ERA);
    }

    public int getProleptcYear() {
        return (int) getValue(StandardDateTimeField.YEAR);
    }

    public int getMonthOfYear() {
        return (int) getValue(StandardDateTimeField.MONTH_OF_YEAR);
    }

    public int getDayOfMonth() {
        return (int) getValue(StandardDateTimeField.DAY_OF_MONTH);
    }

    public int getDayOfYear() {
        return (int) getValue(StandardDateTimeField.DAY_OF_YEAR);
    }

    public int getDayOfWeek() {
        return (int) getValue(StandardDateTimeField.DAY_OF_WEEK);
    }

    //-----------------------------------------------------------------------
    // TODO: are these methods necessary?
    public DateChronoView<T> withYear(int newValue) {
        return withValue(StandardDateTimeField.YEAR, newValue);
    }

    public DateChronoView<T> withMonthOfYear(int newValue) {
        return withValue(StandardDateTimeField.MONTH_OF_YEAR, newValue);
    }

    public DateChronoView<T> withDayOfWeek(int newValue) {
        return withValue(StandardDateTimeField.DAY_OF_WEEK, newValue);
    }

    public DateChronoView<T> withDayOfMonth(int newValue) {
        return withValue(StandardDateTimeField.DAY_OF_MONTH, newValue);
    }

    public DateChronoView<T> withDayOfYear(int newValue) {
        return withValue(StandardDateTimeField.DAY_OF_YEAR, newValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified field using the chronology.
     * 
     * @param field  the field to query, not null
     * @return the value of the field in the stored chronology
     * @throws CalendricalException if the field is not supported
     */
    public long getValue(DateTimeField field) {
        return chronology.getDateValue(date, field);
    }

    /**
     * Returns a copy of this date with the field set to a new value.
     * 
     * @param field  the field to set, not null
     * @param newValue  the new value of the field
     * @return a date based on this one with the requested field changed, not null
     * @throws CalendricalException if the field is not supported
     */
    public DateChronoView<T> withValue(DateTimeField field, long newValue) {
        LocalDate newDate = chronology.setDate(date, field, newValue);
        return (newDate == date ? this : DateChronoView.of(newDate, chronology));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param amount  the amount to add
     * @param unit  the unit that defines the amount, not null
     * @return a date based on this one with the amount added, not null
     * @throws CalendricalException if the unit is not supported
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public DateChronoView<T> plus(long amount, PeriodUnit unit) {
        LocalDate newDate = chronology.addToDate(date, unit, amount);
        return (newDate == date ? this : DateChronoView.of(newDate, chronology));
    }

    /**
     * Returns a copy of this date with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a date based on this one with the amount added, not null
     * @throws CalendricalException if the unit is not supported
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public DateChronoView<T> plusDays(long days) {
        return plus(days, StandardPeriodUnit.DAYS);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     * 
     * @param amount  the amount to subtract
     * @param unit  the unit that defines the amount, not null
     * @return a date based on this one with the amount subtracted, not null
     * @throws CalendricalException if the unit is not supported
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public DateChronoView<T> minus(long amount, PeriodUnit unit) {
        LocalDate newDate = chronology.addToDate(date, unit, MathUtils.safeNegate(amount));  // TODO: chrono method for minus?
        return (newDate == date ? this : DateChronoView.of(newDate, chronology));
    }

    /**
     * Returns a copy of this date with the specified number of days subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, may be negative
     * @return a date based on this one with the amount subtracted, not null
     * @throws CalendricalException if the unit is not supported
     * @throws CalendricalException if the result exceeds the supported date range
     */
    public DateChronoView<T> minusDays(long days) {
        return minus(days, StandardPeriodUnit.DAYS);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this object with a different date.
     * <p>
     * This returns a new object with the same chronology and a different date.
     * 
     * @param date  the date to set in the returned object, not null
     * @return an object based on this one with the date changed, not null
     */
    public DateChronoView<T> withDate(LocalDate date) {
        MathUtils.checkNotNull(chronology, "LocalDate must not be null");
        return (date.equals(this.date) ? this : DateChronoView.of(date, chronology));
    }

    /**
     * Returns a copy of this date using a different chronology.
     * <p>
     * This returns a new object with the same date and a different chronology.
     * 
     * @param chronology  the chronology to set in the returned object, not null
     * @return an object based on this one with the chronology changed, not null
     */
    @SuppressWarnings("unchecked")
    public <R extends Chrono> DateChronoView<R> withChronology(R chronology) {
        MathUtils.checkNotNull(chronology, "Chronology must not be null");
        return (chronology.equals(this.chronology) ? (DateChronoView<R>) this : DateChronoView.of(date, chronology));
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(DateChronoView<T> o) {
        return getDate().compareTo(o.getDate());  // inconsistent with equals
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DateChronoView) {
            DateChronoView<T> other = (DateChronoView<T>) obj;
            return date.equals(other.date) && chronology.equals(other.chronology);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return date.hashCode() ^ chronology.hashCode();
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getProleptcYear() + "-" + getMonthOfYear() + "-" + getDayOfMonth() + "[" + chronology.getName() + "]";  // TODO: better format?
    }

}
