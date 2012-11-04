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

import static javax.time.calendrical.LocalDateTimeField.WEEK_BASED_YEAR;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_WEEK_BASED_YEAR;
import static javax.time.calendrical.LocalDateTimeField.WEEK_OF_YEAR;

import javax.time.DateTimeException;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.LocalTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTime.WithAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.jdk8.DefaultInterfaceChronoLocalDate;
import javax.time.jdk8.Jdk8Methods;

/**
 * A date expressed in terms of a standard year-month-day calendar system.
 * <p>
 * This class is used by applications seeking to handle dates in non-ISO calendar systems.
 * For example, the Japanese, Minguo, Thai Buddhist and others.
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
 * <P>Example: </p>
 * <pre>
 *        System.out.printf("Example()%n");
 *        // Enumerate the list of available calendars and print today for each
 *        Set<String> names = Chronology.getAvailableIds();
 *        for (String name : names) {
 *            Chronology ch = Chronology.of(name);
 *            ChronoLocalDate<?> date = ch.dateNow();
 *            System.out.printf("   %20s: %s%n", ch.getID(), date.toString());
 *        }
 *
 *        // Print the Hijrah date and calendar
 *        ChronoLocalDate<?> date = Chronology.of("Hijrah").dateNow();
 *        int day = date.get(LocalDateTimeField.DAY_OF_MONTH);
 *        int dow = date.get(LocalDateTimeField.DAY_OF_WEEK);
 *        int month = date.get(LocalDateTimeField.MONTH_OF_YEAR);
 *        int year = date.get(LocalDateTimeField.YEAR);
 *        System.out.printf("  Today is %s %s %d-%s-%d%n", date.getChronology().getID(),
 *                dow, day, month, year);

 *        // Print today's date and the last day of the year
 *        ChronoLocalDate<?> now1 = Chronology.of("Hijrah").dateNow();
 *        ChronoLocalDate<?> first = now1.with(LocalDateTimeField.DAY_OF_MONTH, 1)
 *                .with(LocalDateTimeField.MONTH_OF_YEAR, 1);
 *        ChronoLocalDate<?> last = first.plus(1, LocalPeriodUnit.YEARS)
 *                .minus(1, LocalPeriodUnit.DAYS);
 *        System.out.printf("  Today is %s: start: %s; end: %s%n", last.getChronology().getID(),
 *                first, last);
 * </pre>
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
 * {@link Chronology#getID calendar name} and
 * {@link Chronology#getCalendarType() calendar type}. </p>
 *
 * <h4>Implementation notes</h4>
 * This abstract class must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * Subclasses should be Serializable wherever possible.
 * 
 * @param <C> the Chronology of this date
 */
abstract class ChronoDateImpl<C extends Chronology<C>>
        extends DefaultInterfaceChronoLocalDate<C>
        implements ChronoLocalDate<C>, DateTime, WithAdjuster, Comparable<ChronoLocalDate<C>> {

    /**
     * Creates an instance.
     */
    ChronoDateImpl() {
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(DateTimeField field) {
        if (field instanceof LocalDateTimeField) {
            return ((LocalDateTimeField) field).isDateField() && field != WEEK_OF_MONTH &&
                    field != WEEK_OF_YEAR && field != WEEK_OF_WEEK_BASED_YEAR && field != WEEK_BASED_YEAR;
        }
        return field != null && field.doIsSupported(this);
    }

    //-----------------------------------------------------------------------
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
    int getYear() {
        return get(LocalDateTimeField.YEAR_OF_ERA);
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
    int getMonthValue() {
        return get(LocalDateTimeField.MONTH_OF_YEAR);
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
    int getDayOfMonth() {
        return get(LocalDateTimeField.DAY_OF_MONTH);
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
    int getDayOfYear() {
        return get(LocalDateTimeField.DAY_OF_YEAR);
    }

    /**
     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day-of-week.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the {@code DayOfWeek}.
     * This includes textual names of the values.
     *
     * @return the day-of-week, not null
     */
    DayOfWeek getDayOfWeek() {
        return DayOfWeek.of(get(LocalDateTimeField.DAY_OF_WEEK));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified era.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param era  the era to set, not null
     * @return a date based on this one with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoLocalDate<C> withEra(Era<C> era) {
        return with(LocalDateTimeField.ERA, era.getValue());
    }

    /**
     * Returns a copy of this date with the specified year-of-era.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year-of-era to set
     * @return a date based on this one with the specified year-of-era, not null
     */
    ChronoLocalDate<C> withYear(int year) {
        return with(LocalDateTimeField.YEAR_OF_ERA, year);
    }

    /**
     * Returns a copy of this date with the specified month-of-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to set
     * @return a date based on this one with the specified month-of-year, not null
     */
    ChronoLocalDate<C> withMonth(int month) {
        return with(LocalDateTimeField.MONTH_OF_YEAR, month);
    }

    /**
     * Returns a copy of this date with the specified day-of-month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day-of-month to set
     * @return a date based on this one with the specified day-of-month, not null
     */
    ChronoLocalDate<C> withDayOfMonth(int dayOfMonth) {
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
    ChronoLocalDate<C> withDayOfYear(int dayOfYear) {
        return with(LocalDateTimeField.DAY_OF_YEAR, dayOfYear);
    }

    //-----------------------------------------------------------------------
    @Override
    public ChronoDateImpl<C> plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            LocalPeriodUnit f = (LocalPeriodUnit) unit;
            switch (f) {
                case DAYS: return plusDays(amountToAdd);
                case WEEKS: return plusDays(Jdk8Methods.safeMultiply(amountToAdd, 7));
                case MONTHS: return plusMonths(amountToAdd);
                case QUARTER_YEARS: return plusYears(amountToAdd / 256).plusMonths((amountToAdd % 256) * 3);  // no overflow (256 is multiple of 4)
                case HALF_YEARS: return plusYears(amountToAdd / 256).plusMonths((amountToAdd % 256) * 6);  // no overflow (256 is multiple of 2)
                case YEARS: return plusYears(amountToAdd);
                case DECADES: return plusYears(Jdk8Methods.safeMultiply(amountToAdd, 10));
                case CENTURIES: return plusYears(Jdk8Methods.safeMultiply(amountToAdd, 100));
                case MILLENNIA: return plusYears(Jdk8Methods.safeMultiply(amountToAdd, 1000));
//                case ERAS: throw new DateTimeException("Unable to add era, standard calendar system only has one era");
//                case FOREVER: return (period == 0 ? this : (period > 0 ? LocalDate.MAX_DATE : LocalDate.MIN_DATE));
            }
            throw new DateTimeException(unit.getName() + " not valid for CopticDate");
        }
        return unit.doAdd(this, amountToAdd);
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
     * @param yearsToAdd  the years to add, may be negative
     * @return a date based on this one with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    abstract ChronoDateImpl<C> plusYears(long yearsToAdd);

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
     * @param monthsToAdd  the months to add, may be negative
     * @return a date based on this one with the months added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    abstract ChronoDateImpl<C> plusMonths(long monthsToAdd);

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
     * @param weeksToAdd  the weeks to add, may be negative
     * @return a date based on this one with the weeks added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateImpl<C> plusWeeks(long weeksToAdd) {
        return plusDays(Jdk8Methods.safeMultiply(weeksToAdd, 7));
    }

    /**
     * Returns a copy of this date with the specified number of days added.
     * <p>
     * This adds the specified period in days to the date.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param daysToAdd  the days to add, may be negative
     * @return a date based on this one with the days added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    abstract ChronoDateImpl<C> plusDays(long daysToAdd);

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
     * @param yearsToSubtract  the years to subtract, may be negative
     * @return a date based on this one with the years subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateImpl<C> minusYears(long yearsToSubtract) {
        return (yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract));
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
     * @param monthsToSubtract  the months to subtract, may be negative
     * @return a date based on this one with the months subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateImpl<C> minusMonths(long monthsToSubtract) {
        return (monthsToSubtract == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-monthsToSubtract));
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
     * @param weeksToSubtract  the weeks to subtract, may be negative
     * @return a date based on this one with the weeks subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateImpl<C> minusWeeks(long weeksToSubtract) {
        return (weeksToSubtract == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeksToSubtract));
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
     * @param daysToSubtract  the days to subtract, may be negative
     * @return a date based on this one with the days subtracted, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    ChronoDateImpl<C> minusDays(long daysToSubtract) {
        return (daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-daysToSubtract));
    }

    @Override
    public final ChronoLocalDateTime<C> atTime(LocalTime localTime) {
        return ChronoDateTimeImpl.of(this, localTime);
    }

    //-----------------------------------------------------------------------
    @Override
    public long periodUntil(DateTime endDateTime, PeriodUnit unit) {
        if (endDateTime instanceof ChronoLocalDate == false) {
            throw new DateTimeException("Unable to calculate period between objects of two different types");
        }
        ChronoLocalDate<?> end = (ChronoLocalDate<?>) endDateTime;
        if (getChronology().equals(end.getChronology()) == false) {
            throw new DateTimeException("Unable to calculate period between two different chronologies");
        }
        if (unit instanceof LocalPeriodUnit) {
            return LocalDate.from(this).periodUntil(end, unit);  // TODO: this is wrong
        }
        return unit.between(this, endDateTime).getAmount();
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(ChronoLocalDate<C> other) {
        ChronoDateImpl<C> cd = (ChronoDateImpl<C>) other;
        if (getChronology().equals(other.getChronology()) == false) {
            throw new ClassCastException("Cannot compare ChronoDate in two different calendar systems, " +
                    "use the EPOCH_DAY field as a Comparator instead");
        }
        int cmp = Integer.compare(getEra().getValue(), cd.getEra().getValue());
        if (cmp == 0) {
            cmp = Integer.compare(getYear(), cd.getYear());
            if (cmp == 0) {
                cmp = Integer.compare(getMonthValue(), cd.getMonthValue());
                if (cmp == 0) {
                    cmp = Integer.compare(getDayOfMonth(), cd.getDayOfMonth());
                }
            }
        }
        return cmp;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChronoDateImpl) {
            ChronoDateImpl<?> other = (ChronoDateImpl<?>) obj;
            return getChronology().equals(other.getChronology()) &&
                    getEra() == other.getEra() &&
                    getYear() == other.getYear() &&
                    getMonthValue() == other.getMonthValue() &&
                    getDayOfMonth() == other.getDayOfMonth();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getChronology().hashCode() ^ Integer.rotateLeft(getYear(), 16) ^
                (getEra().getValue() << 12) ^ (getMonthValue() << 6) ^ getDayOfMonth();
    }

    //-------------------------------------------------------------------------
    @Override // override to use local getters which may be overridden
    public String toString() {
        int yearValue = getYear();
        int monthValue = getMonthValue();
        int dayValue = getDayOfMonth();
        StringBuilder buf = new StringBuilder(30);
        buf.append(getChronology().toString())
                .append(" ")
                .append(getEra().toString())
                .append(yearValue)
                .append(monthValue < 10 ? "-0" : "-").append(monthValue)
                .append(dayValue < 10 ? "-0" : "-").append(dayValue);
        return buf.toString();
    }

}
