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

import javax.time.DateTimeException;
import javax.time.LocalTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.PeriodUnit;
import javax.time.format.CalendricalFormatter;

/**
 * A date expressed in terms of a standard year-month-day calendar system.
 * <p>
 * This class is used by applications seeking to handle dates in non-ISO calendar systems.
 * For example, the Japanese, Minguo, Thai Buddhist and others.
 * <a href="package-summary.html">Sample code</a> can be found in the package documentation.
 * <p>
 * {@code ChronoLocalDate} is built on the generic concepts of year, month and day.
 * The calendar system, represented by a {@link Chronology}, expresses the relationship between
 * the fields and this class allows the resulting date to be manipulated.
 * <p>
 * Note that not all calendar systems are suitable for use with this class.
 * For example, the Mayan calendar uses a system that bears no relation to years, months and days.
 * <p>
 * The API design encourages the use of {@code LocalDate} for the majority of the application.
 * This includes code to read and write from a persistent data store, such as a database,
 * and to send dates and times across a network. The {@code ChronoLocalDate} instance is then used
 * at the user interface level to deal with localized input/output.
 *
 * <h4>Adding Calendars</h4>
 * <p> The set of calendars is extensible by defining a subclass of {@link javax.time.chrono.ChronoLocalDate}
 * to represent a date instance and an implementation of {@link javax.time.chrono.Chronology}
 * to be the factory for the ChronoLocalDate subclass.
 * </p>
 * <p> To permit the discovery of the additional calendar types the implementation of 
 * {@link javax.time.chrono.Chronology} must be registered as a Service implementing
 * the {@link javax.time.chrono.Chronology} interface in the {@code META-INF/Services}
 * file as per the specification of {@link java.util.ServiceLoader}.
 * The subclass must function according to the Chronology interface and must provide its
 * {@link Chronology#getId calendar name} and
 * {@link Chronology#getCalendarType() calendar type}. </p>
 *
 * <h4>Implementation notes</h4>
 * This abstract class must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * Subclasses should be Serializable wherever possible.
 * 
 * @param <C> the Chronology of this date
 */
public interface ChronoLocalDate<C extends Chronology<C>>
        extends DateTime, WithAdjuster, Comparable<ChronoLocalDate<C>> {

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The fields of this date are all expressed relative to this.
     * 
     * @return the calendar system, not null
     */
    public C getChronology();

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field);

    @Override
    public int get(DateTimeField field);

    @Override
    public abstract long getLong(DateTimeField field);

    /**
     * Gets the era, as defined by the calendar system.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the {@code Chronology}.
     * <p>
     * All correctly implemented {@code Era} classes are singletons, thus it
     * is valid code to write {@code date.getEra() == SomeChronology.ERA_NAME)}.
     *
     * @return the chronology specific era constant applicable at this date, not null
     */
    public Era<C> getEra();

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
    public boolean isLeapYear();

    /**
     * Returns the length of the month represented by this date, as defined by the calendar system.
     * <p>
     * This returns the length of the month in days.
     *
     * @return the length of the month in days
     */
    public int lengthOfMonth();

    /**
     * Returns the length of the year represented by this date, as defined by the calendar system.
     * <p>
     * This returns the length of the year in days.
     * <p>
     * The default implementation uses {@link #isLeapYear()} and returns 365 or 366.
     *
     * @return the length of the year in days
     */
    public int lengthOfYear();

    @Override
    public ChronoLocalDate<C> with(WithAdjuster adjuster);

    @Override
    public ChronoLocalDate<C> with(DateTimeField field, long newValue);

    @Override
    public ChronoLocalDate<C> plus(PlusAdjuster adjuster);

    @Override
    public ChronoLocalDate<C> plus(long amountToAdd, PeriodUnit unit);

    @Override
    public ChronoLocalDate<C> minus(MinusAdjuster adjuster);

    @Override
    public ChronoLocalDate<C> minus(long amountToSubtract, PeriodUnit unit);

    @Override
    DateTimeValueRange range(DateTimeField field);

    //-----------------------------------------------------------------------
    /**
     * Returns a ChronoLocalDateTime formed from this date at the specified time.
     * <p>
     * This merges the two objects - {@code this} and the specified time -
     * to form an instance of {@code LocalDateTime}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param localTime  the local time to use, not null
     * @return the local date-time formed from this date and the specified time, not null
     */
    public ChronoLocalDateTime<C> atTime(LocalTime localTime);

    //-----------------------------------------------------------------------

    @Override
    public <R> R extract(Class<R> type);

    @Override
    public DateTime doWithAdjustment(DateTime dateTime);

    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit);

    //-----------------------------------------------------------------------
    /**
     * Compares this date to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     * Only two dates with the same calendar system can be compared.
     * <p>
     * To compare the underlying local date of two {@code DateTimeAccessor} instances,
     * use {@link LocalDateTimeField#EPOCH_DAY} as a comparator.
     * <p>
     * The default implementation uses {@link #getChronology()}, {@link #getEra()},
     * and {@link LocalDateTimeField#YEAR}, {@link LocalDateTimeField#MONTH_OF_YEAR} and
     * {@link LocalDateTimeField#DAY_OF_MONTH}.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws ClassCastException if the dates have different calendar systems
     */
    @Override
    public int compareTo(ChronoLocalDate<C> other);

    //-----------------------------------------------------------------------
    /**
     * Checks if the underlying date of this {@code ChronoLocalDate} is after the specified date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the underlying date and not the chronology.
     * This is equivalent to using {@code date1.getLong(EPOCH_DAY) &gt; date2.getLong(EPOCH_DAY).
     *
     * @param other  the other date to compare to, not null
     * @return true if the underlying date is after the specified date
     */
    public boolean isAfter(ChronoLocalDate<C> other);

    /**
     * Checks if the underlying date of this {@code ChronoLocalDate} is before the specified date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the underlying date and not the chronology.
     * This is equivalent to using {@code date1.getLong(EPOCH_DAY) &lt; date2.getLong(EPOCH_DAY).
     *
     * @param other  the other date to compare to, not null
     * @return true if the underlying date is before the specified date
     */
    public boolean isBefore(ChronoLocalDate<C> other);

    /**
     * Checks if this date is equal to the specified date ignoring the chronology.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the underlying date and not the chronology.
     * This is equivalent to using {@code date1.getLong(EPOCH_DAY) == date2.getLong(EPOCH_DAY).
     *
     * @param other  the other date to compare to, not null
     * @return true if the underlying date is equal to the specified date
     */
    public boolean equalDate(ChronoLocalDate<?> other);

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     * Only objects of type {@code ChronoLocalDate} are compared, other types return false.
     * Only two dates with the same calendar system will compare equal.
     * <p>
     * To check whether the underlying local date of two {@code ChronoLocalDate} instances
     * are equal ignoring the calendar system, use {@link #equalDate(ChronoLocalDate)}.
     * More generally, to compare the underlying local date of two {@code DateTime} instances,
     * use {@link LocalDateTimeField#EPOCH_DAY} as a comparator.
     * <p>
     * The default implementation uses {@link #getChronology()}, {@link #getEra()},
     * and {@link LocalDateTimeField#YEAR}, {@link LocalDateTimeField#MONTH_OF_YEAR} and
     * {@link LocalDateTimeField#DAY_OF_MONTH}.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date
     */
    @Override
    public boolean equals(Object obj);

    /**
     * A hash code for this date.
     * <p>
     * The default implementation uses {@link #getChronology()}, {@link #getEra()},
     * and {@link LocalDateTimeField#YEAR}, {@link LocalDateTimeField#MONTH_OF_YEAR} and
     * {@link LocalDateTimeField#DAY_OF_MONTH}.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode();

    //-----------------------------------------------------------------------
    /**
     * Outputs this date as a {@code String}, such as {@code 1723AD-13-01 (ISO)}.
     * <p>
     * The output will be in the format {@code {year}{era}-{month}-{day} ({chrono})}.
     *
     * @return the formatted date, not null
     */
    @Override
    public String toString();

    /**
     * Outputs this date-time as a {@code String} using the formatter.
     *
     * @param formatter  the formatter to use, not null
     * @return the formatted date-time string, not null
     * @throws UnsupportedOperationException if the formatter cannot print
     * @throws DateTimeException if an error occurs during printing
     */
    public String toString(CalendricalFormatter formatter);
}
