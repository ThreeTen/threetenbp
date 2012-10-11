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

import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.YEAR;

import javax.time.DateTimeException;
import javax.time.DateTimes;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.Period;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimePlusMinusAdjuster;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;

/**
 * A date expressed in terms of a standard year-month-day calendar system.
 * <p>
 * This class is used by applications seeking to handle dates in non-ISO calendar systems.
 * For example, the Japanese, Minguo, Thai Buddhist and others.
 * <p>
 * {@code ChronoDate} is built on the generic concepts of year, month and day.
 * The calendar system, represented by a {@link Chronology}, expresses the relationship between
 * the fields and this class allows the resulting date to be manipulated.
 * <p>
 * Note that not all calendar systems are suitable for use with this class.
 * For example, the Mayan calendar uses a system that bears no relation to years, months and days.
 * <p>
 * The API design encourages the use of {@code LocalDate} for the majority of the application.
 * This includes code to read and write from a persistent data store, such as a database,
 * and to send dates and times across a network. The {@code ChronoDate} instance is then used
 * at the user interface level to deal with localized input/output.
 * 
 * <h4>Implementation notes</h4>
 * This abstract class must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * Subclasses should be Serializable wherever possible.
 */
public abstract class ChronoDate
        implements DateTime<ChronoDate>, DateTimeAdjuster, Comparable<ChronoDate> {

    /**
     * Obtains an instance of {@code ChronoDate} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ChronoDate}.
     * <p>
     * If the calendrical can provide a calendar system, then that will be used,
     * otherwise, the ISO calendar system will be used.
     * This allows a {@link LocalDate} to be converted to a {@code ChronoDate}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the calendar system specific date, not null
     * @throws DateTimeException if unable to convert to a {@code ChronoDate}
     */
    public static ChronoDate from(DateTimeAccessor calendrical) {
        if (calendrical instanceof ChronoDate) {
            return (ChronoDate) calendrical;
        }
        LocalDate ld = LocalDate.from(calendrical);
        Chronology chronology = Chronology.from(calendrical);
        return chronology.date(ld);
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
    public abstract Chronology getChronology();

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified date-time field for the calendar system represented by this date.
     * <p>
     * This returns the value of the specified field.
     * The result of this method will depend on the {@code Chrono}.
     * <p>
     * Implementations must check and return any fields defined in {@code LocalDateTimeField}
     * before delegating on to the method on the specified field.
     * Invoking this method must not change the observed state of the target.
     *
     * @param field  the field to get, not null
     * @return the value for the field
     * @throws DateTimeException if a value for the field cannot be obtained
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
     * Gets the month-of-year, as defined by the calendar system.
     * <p>
     * The month-of-year is a value representing the count of months within the year.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The month-of-year value must be positive.
     *
     * @return the month-of-year, within the valid range for the chronology
     */
    public int getMonth() {
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
    public DayOfWeek getDayOfWeek() {
        return DayOfWeek.of(DateTimes.safeToInt(get(LocalDateTimeField.DAY_OF_WEEK)));
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
        return getChronology().isLeapYear(get(YEAR));
    }

    /**
     * Returns the length of the month represented by this date, as defined by the calendar system.
     * <p>
     * This returns the length of the month in days.
     *
     * @return the length of the month in days
     */
    public abstract int lengthOfMonth();

    /**
     * Returns the length of the year represented by this date, as defined by the calendar system.
     * <p>
     * This returns the length of the year in days.
     * <p>
     * The default implementation uses {@link #isLeapYear()} and returns 365 or 366.
     *
     * @return the length of the year in days
     */
    public int lengthOfYear() {
        return (isLeapYear() ? 366 : 365);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date that is altered using the adjuster.
     * <p>
     * This adjusts the date according to the rules of the specified adjuster.
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date to the last day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     * @return a date based on this one with the years added, not null
     * @throws DateTimeException if the adjustment cannot be made
     * @throws RuntimeException if the result exceeds the supported range
     */
    public ChronoDate with(DateTimeAdjuster adjuster) {
        return adjuster.doAdjustment(this);
    }

    /**
     * Returns an object of the same type as this object with the specified field altered.
     * <p>
     * This method returns a new object based on this one with the value for the specified field changed.
     * The result of this method will depend on the {@code Chrono}.
     * For example, on a {@code GregorianDate}, this can be used to set the year, month of day-of-month.
     * The returned object will have the same observable type as this object.
     * <p>
     * In some cases, changing a field is not fully defined. For example, if the target object is
     * a date representing the 31st January, then changing the month to February would be unclear.
     * In cases like this, the field is responsible for resolving the result. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * Implementations must check and return any fields defined in {@code LocalDateTimeField} before
     * delegating on to the method on the specified field.
     * If the implementing class is immutable, then this method must return an updated copy of the original.
     * If the class is mutable, then this method must update the original.
     *
     * @param field  the field to set in the returned date, not null
     * @param newValue  the new value of the field in the returned date, not null
     * @return an object of the same type with the specified field set, not null
     * @throws DateTimeException if the specified value is invalid
     * @throws DateTimeException if the field cannot be set on this type
     * @throws RuntimeException if the result exceeds the supported range
     */
    public abstract ChronoDate with(DateTimeField field, long newValue);

    /**
     * Returns a copy of this date with the specified era.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param era  the era to set, not null
     * @return a date based on this one with the years added, not null
     * @throws DateTimeException if the result exceeds the supported date range
     */
    public ChronoDate withEra(Era era) {
        return with(LocalDateTimeField.ERA, era.getValue());
    }

    /**
     * Returns a copy of this date with the specified year-of-era.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param yearOfEra  the year-of-era to set
     * @return a date based on this one with the specified year-of-era, not null
     */
    public ChronoDate withYearOfEra(int yearOfEra) {
        return with(LocalDateTimeField.YEAR_OF_ERA, yearOfEra);
    }

    /**
     * Returns a copy of this date with the specified month-of-year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month  the month-of-year to set
     * @return a date based on this one with the specified month-of-year, not null
     */
    public ChronoDate withMonth(int month) {
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
    public ChronoDate withDayOfMonth(int dayOfMonth) {
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
    public ChronoDate withDayOfYear(int dayOfYear) {
        return with(LocalDateTimeField.DAY_OF_YEAR, dayOfYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period added.
     * <p>
     * This method returns a new date based on this date with the specified period added.
     * The adjuster is typically {@link Period} but may be any other type implementing
     * the {@link DateTimePlusMinusAdjuster} interface.
     * The calculation is delegated to the specified adjuster, which typically calls
     * back to {@link #plus(long, PeriodUnit)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalDate} based on this date with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    public ChronoDate plus(DateTimePlusMinusAdjuster adjuster) {
        return adjuster.doAdd(this);
    }

    /**
     * Returns a copy of this date with the specified period added.
     * <p>
     * This method returns a new date based on this date with the specified period added.
     * The result of this method will depend on the {@code Chrono}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount of the unit to add to the returned date, not null
     * @param unit  the unit of the period to add, not null
     * @return a {@code ChronoDate} based on this date with the specified period added, not null
     */
    @Override
    public ChronoDate plus(long amountToAdd, PeriodUnit unit) {
        if (unit instanceof LocalPeriodUnit) {
            LocalPeriodUnit f = (LocalPeriodUnit) unit;
            switch (f) {
                case DAYS: return plusDays(amountToAdd);
                case WEEKS: return plusDays(DateTimes.safeMultiply(amountToAdd, 7));
                case MONTHS: return plusMonths(amountToAdd);
                case QUARTER_YEARS: return plusYears(amountToAdd / 256).plusMonths((amountToAdd % 256) * 3);  // no overflow (256 is multiple of 4)
                case HALF_YEARS: return plusYears(amountToAdd / 256).plusMonths((amountToAdd % 256) * 6);  // no overflow (256 is multiple of 2)
                case YEARS: return plusYears(amountToAdd);
                case DECADES: return plusYears(DateTimes.safeMultiply(amountToAdd, 10));
                case CENTURIES: return plusYears(DateTimes.safeMultiply(amountToAdd, 100));
                case MILLENNIA: return plusYears(DateTimes.safeMultiply(amountToAdd, 1000));
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
    public abstract ChronoDate plusYears(long yearsToAdd);

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
    public abstract ChronoDate plusMonths(long monthsToAdd);

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
    public ChronoDate plusWeeks(long weeksToAdd) {
        return plusDays(DateTimes.safeMultiply(weeksToAdd, 7));
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
    public abstract ChronoDate plusDays(long daysToAdd);

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this date with the specified period subtracted.
     * <p>
     * This method returns a new date based on this date with the specified period subtracted.
     * The adjuster is typically {@link Period} but may be any other type implementing
     * the {@link DateTimePlusMinusAdjuster} interface.
     * The calculation is delegated to the specified adjuster, which typically calls
     * back to {@link #minus(long, PeriodUnit)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster  the adjuster to use, not null
     * @return a {@code LocalDate} based on this date with the subtraction made, not null
     * @throws DateTimeException if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    public ChronoDate minus(DateTimePlusMinusAdjuster adjuster) {
        return adjuster.doSubtract(this);
    }

    /**
     * Returns a copy of this date with the specified period subtracted.
     * <p>
     * This method returns a new date based on this date with the specified period subtracted.
     * The result of this method will depend on the {@code Chrono}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract  the amount of the unit to subtract from the returned date, not null
     * @param unit  the unit of the period to subtract, not null
     * @return a {@code ChronoDate} based on this date with the specified period subtracted, not null
     * @throws DateTimeException if the unit cannot be added to this type
     */
    public ChronoDate minus(long amountToSubtract, PeriodUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

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
    public ChronoDate minusYears(long yearsToSubtract) {
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
    public ChronoDate minusMonths(long monthsToSubtract) {
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
    public ChronoDate minusWeeks(long weeksToSubtract) {
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
    public ChronoDate minusDays(long daysToSubtract) {
        return (daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-daysToSubtract));
    }

    //-----------------------------------------------------------------------
    /**
     * Extracts date-time information in a generic way.
     * <p>
     * This method exists to fulfill the {@link DateTimeAccessor} interface.
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
        } else if (type == Chronology.class) {
            return (R) getChronology();
        }
        return null;
    }

    @Override
    public <R extends DateTime<R>> R doAdjustment(R dateTime) {
        return dateTime.with(EPOCH_DAY, toEpochDay());
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
     * To compare the underlying local date of two {@code DateTimeAccessor} instances,
     * use {@link LocalDateTimeField#EPOCH_DAY} as a comparator.
     * <p>
     * The default implementation uses {@link #getChronology()}, {@link #getEra()},
     * {@link #getYearOfEra()}, {@link #getMonth()} and {@link #getDayOfMonth()}.
     *
     * @param other  the other date to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws ClassCastException if the dates have different calendar systems
     */
    @Override
    public int compareTo(ChronoDate other) {
        if (getChronology().equals(other.getChronology()) == false) {
            throw new ClassCastException("Cannot compare ChronoDate in two different calendar systems, " +
            		"use the EPOCH_DAY field as a Comparator instead");
        }
        int cmp = DateTimes.safeCompare(getEra().getValue(), other.getEra().getValue());
        if (cmp == 0) {
            cmp = DateTimes.safeCompare(getYearOfEra(), other.getYearOfEra());
            if (cmp == 0) {
                cmp = DateTimes.safeCompare(getMonth(), other.getMonth());
                if (cmp == 0) {
                    cmp = DateTimes.safeCompare(getDayOfMonth(), other.getDayOfMonth());
                }
            }
        }
        return cmp;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the underlying date of this {@code ChronoDate} is after the specified date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the underlying date and not the chronology.
     * This is equivalent to using {@code date1.toLocalDate().isAfter(date2.toLocalDate())}.
     *
     * @param other  the other date to compare to, not null
     * @return true if the underlying date is after the specified date
     */
    public boolean isAfter(ChronoDate other) {
        return toEpochDay() > other.toEpochDay();
    }

    /**
     * Checks if the underlying date of this {@code ChronoDate} is before the specified date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the underlying date and not the chronology.
     * This is equivalent to using {@code date1.toLocalDate().isBefore(date2.toLocalDate())}.
     *
     * @param other  the other date to compare to, not null
     * @return true if the underlying date is before the specified date
     */
    public boolean isBefore(ChronoDate other) {
        return toEpochDay() < other.toEpochDay();
    }

    /**
     * Checks if the underlying date of this {@code ChronoDate} is equal to the specified date.
     * <p>
     * This method differs from the comparison in {@link #compareTo} in that it
     * only compares the underlying date and not the chronology.
     * This is equivalent to using {@code date1.toLocalDate().equals(date2.toLocalDate())}.
     *
     * @param other  the other date to compare to, not null
     * @return true if the underlying date is equal to the specified date
     */
    public boolean equalDate(ChronoDate other) {
        return toEpochDay() == other.toEpochDay();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this date is equal to another date.
     * <p>
     * The comparison is based on the time-line position of the dates.
     * Only objects of type {@code ChronoDate} are compared, other types return false.
     * Only two dates with the same calendar system will compare equal.
     * <p>
     * To check whether the underlying local date of two {@code ChronoDate} instances
     * are equal ignoring the calendar system, use {@link #equalDate(ChronoDate)}.
     * More generally, to compare the underlying local date of two {@code DateTimeAccessor}
     * instances, use {@link LocalDateTimeField#EPOCH_DAY} as a comparator.
     * <p>
     * The default implementation uses {@link #getChronology()}, {@link #getEra()},
     * {@link #getYearOfEra()}, {@link #getMonth()} and {@link #getDayOfMonth()}.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChronoDate) {
            ChronoDate other = (ChronoDate) obj;
            return getChronology().equals(other.getChronology()) &&
                    getEra() == other.getEra() &&
                    getYearOfEra() == other.getYearOfEra() &&
                    getMonth() == other.getMonth() &&
                    getDayOfMonth() == other.getDayOfMonth();
        }
        return false;
    }

    /**
     * A hash code for this date.
     * <p>
     * The default implementation uses {@link #getChronology()}, {@link #getEra()},
     * {@link #getYearOfEra()}, {@link #getMonth()} and {@link #getDayOfMonth()}.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return getChronology().hashCode() ^ Integer.rotateLeft(getYearOfEra(), 16) ^
                (getEra().getValue() << 12) ^ (getMonth() << 6) ^ getDayOfMonth();
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
        int monthValue = getMonth();
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
