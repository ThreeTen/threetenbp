/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import java.io.Serializable;

import javax.time.calendrical.DateTime;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.ISOChronology;

/**
 * An immutable period consisting of the ISO-8601 year, month, day, hour,
 * minute, second and nanosecond units, such as '3 Months, 4 Days and 7 Hours'.
 * <p>
 * A period is a human-scale description of an amount of time.
 * This class represents the 7 standard definitions from {@link ISOChronology}.
 * The period units used are 'Years', 'Months', 'Days', 'Hours', 'Minutes',
 * 'Seconds' and 'Nanoseconds'.
 * <p>
 * The {@code ISOChronology} defines a relationship between some of the units:
 * <ul>
 * <li>12 months in a year</li>
 * <li>24 hours in a day (ignoring time-zones)</li>
 * <li>60 minutes in an hour</li>
 * <li>60 seconds in a minute</li>
 * <li>1,000,000,000 nanoseconds in a second</li>
 * </ul>
 * <p>
 * This class is immutable and thread-safe.
 */
public final class ISOPeriod
        implements Serializable {

    /**
     * A constant for a period of zero.
     */
    public static final ISOPeriod ZERO = new ISOPeriod(0, 0, 0, 0, 0, 0, 0);
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The number of years.
     */
    private final int years;
    /**
     * The number of months.
     */
    private final int months;
    /**
     * The number of days.
     */
    private final int days;
    /**
     * The number of hours.
     */
    private final int hours;
    /**
     * The number of minutes.
     */
    private final int minutes;
    /**
     * The number of seconds.
     */
    private final int seconds;
    /**
     * The number of nanoseconds.
     */
    private final long nanos;

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code ISOPeriod} from date-based and time-based fields.
     * <p>
     * This creates an instance based on years, months, days, hours, minutes and seconds.
     *
     * @param years  the amount of years, may be negative
     * @param months  the amount of months, may be negative
     * @param days  the amount of days, may be negative
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @return the period, not null
     */
    public static ISOPeriod of(int years, int months, int days, int hours, int minutes, int seconds) {
        return of(years, months, days, hours, minutes, seconds, 0);
    }

    /**
     * Obtains an {@code ISOPeriod} from date-based and time-based fields.
     * <p>
     * This creates an instance based on years, months, days, hours, minutes, seconds and nanoseconds.
     * The resulting period will have normalized seconds and nanoseconds.
     *
     * @param years  the amount of years, may be negative
     * @param months  the amount of months, may be negative
     * @param days  the amount of days, may be negative
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @param nanos  the amount of nanos, may be negative
     * @return the period, not null
     */
    public static ISOPeriod of(int years, int months, int days, int hours, int minutes, int seconds, long nanos) {
        if ((years | months | days | hours | minutes | seconds | nanos) == 0) {
            return ZERO;
        }
        return new ISOPeriod(years, months, days, hours, minutes, seconds, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code ISOPeriod} from date-based fields.
     * <p>
     * This creates an instance based on years, months and days.
     *
     * @param years  the amount of years, may be negative
     * @param months  the amount of months, may be negative
     * @param days  the amount of days, may be negative
     * @return the period, not null
     */
    public static ISOPeriod ofDate(int years, int months, int days) {
        return of(years, months, days, 0, 0, 0, 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code ISOPeriod} from time-based fields.
     * <p>
     * This creates an instance based on hours, minutes and seconds.
     *
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @return the period, not null
     */
    public static ISOPeriod ofTime(int hours, int minutes, int seconds) {
        return of(0, 0, 0, hours, minutes, seconds, 0);
    }

    /**
     * Obtains an {@code ISOPeriod} from time-based fields.
     * <p>
     * This creates an instance based on hours, minutes, seconds and nanoseconds.
     *
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @param nanos  the amount of nanos, may be negative
     * @return the period, not null
     */
    public static ISOPeriod ofTime(int hours, int minutes, int seconds, long nanos) {
        return of(0, 0, 0, hours, minutes, seconds, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code ISOPeriod} from an amount and unit.
     * <p>
     * The parameters represent the two parts of a phrase like '6 Days'.
     * <p>
     * An {@code ISOPeriod} supports 7 units, years, months, days, hours,
     * minutes, seconds and nanoseconds. The unit must be one of these,
     * the units quarter years, half years, are converted to equivalent months,
     * and the units for decades, centuries, and millennia are converted to
     * equivalent years.
     *
     * @param amount  the amount of the period, measured in terms of the unit, positive or negative
     * @param unit  the unit that the period is measured in, not null
     * @return the period, not null
     * @throws DateTimeException if the unit is not supported
     */
    public static ISOPeriod of(long amount, PeriodUnit unit) {
        DateTimes.checkNotNull(unit, "PeriodUnit must not be null");
        if (unit instanceof LocalPeriodUnit) {
            LocalPeriodUnit lpu = (LocalPeriodUnit) unit;
            switch (lpu) {
                case MILLENNIA:
                    return ISOPeriod.ofDate(DateTimes.safeToInt(amount * 1000), 0, 0);
                case CENTURIES:
                    return ISOPeriod.ofDate(DateTimes.safeToInt(amount * 100), 0, 0);
                case DECADES:
                    return ISOPeriod.ofDate(DateTimes.safeToInt(amount * 10), 0, 0);
                case YEARS:
                case WEEK_BASED_YEARS:
                    return ISOPeriod.ofDate(DateTimes.safeToInt(amount), 0, 0);
                case HALF_YEARS:
                    return ISOPeriod.ofDate(0, DateTimes.safeToInt(amount * 6), 0);
                case QUARTER_YEARS:
                    return ISOPeriod.ofDate(0, DateTimes.safeToInt(amount * 3), 0);
                case MONTHS:
                    return ISOPeriod.ofDate(0, DateTimes.safeToInt(amount), 0);
                case WEEKS:
                    return ISOPeriod.ofDate(0, 0, DateTimes.safeToInt(amount * 7));
                case DAYS:
                    return ISOPeriod.ofDate(0, 0, DateTimes.safeToInt(amount));
                case HALF_DAYS:
                    return ISOPeriod.ofTime(DateTimes.safeToInt(amount * 12), 0, 0, 0);
                case HOURS:
                    return ISOPeriod.ofTime(DateTimes.safeToInt(amount), 0, 0, 0);
                case MINUTES:
                    return ISOPeriod.ofTime(0, DateTimes.safeToInt(amount), 0, 0);
                case SECONDS:
                    return ISOPeriod.ofTime(0, 0, DateTimes.safeToInt(amount), 0);
                case MILLIS:
                    return ISOPeriod.ofTime(0, 0, 0, DateTimes.safeToInt(amount * 1000000L));
                case MICROS:
                    return ISOPeriod.ofTime(0, 0, 0, DateTimes.safeToInt(amount * 1000L));
                case NANOS:
                    return ISOPeriod.ofTime(0, 0, 0, amount);
                default:
                    // Fall through to handle throw unsupported PeriodUnit
            }
        }
        throw new DateTimeException("Unsupported unit: " + unit.getName());
    }

    /**
     * Obtains an {@code ISOPeriod} from a Period.
     * <p>
     * An {@code ISOPeriod} supports 7 units, years, months, days, hours,
     * minutes, seconds and nanoseconds. The unit must be one of these,
     * the units quarter years, half years, are converted to equivalent months,
     * and the units for decades, centuries, and millennia are converted to
     * equivalent years.
     *
     * @param amount  the amount of the period, measured in terms of the unit, positive or negative
     * @param unit  the unit that the period is measured in, not null
     * @return the period, not null
     * @throws DateTimeException if the unit is not supported
     */
    public static ISOPeriod of(Period period) {
        DateTimes.checkNotNull(period, "Period must not be null");
        return of(period.getAmount(), period.getUnit());

    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code ISOPeriod} from a {@code Duration}.
     * <p>
     * The created period will have normalized values for the hours, minutes,
     * seconds and nanoseconds fields. The years, months and days fields will be zero.
     * <p>
     * To populate the days field, call {@link #normalizedWith24HourDays()} on the created period.
     *
     * @param duration  the duration to create from, not null
     * @return the {@code PeriodFields} instance, not null
     * @throws ArithmeticException if the result exceeds the supported period range
     */
    public static ISOPeriod of(Duration duration) {
        DateTimes.checkNotNull(duration, "Duration must not be null");
        if (duration.isZero()) {
            return ZERO;
        }
        int hours = DateTimes.safeToInt(duration.getSeconds() / 3600);
        int amount = (int) (duration.getSeconds() % 3600L);
        return new ISOPeriod(0, 0, 0, hours, (amount / 60), (amount % 60), duration.getNano());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns an {@code ISOPeriod} consisting of the number of years, months, days,
     * hours, minutes, seconds, and nanoseconds between two {@code DateTime} instances.
     * <p>
     * The start date is included, but the end date is not. Only whole years count.
     * For example, from {@code 2010-01-15} to {@code 2011-03-18} is one year, two months and three days.
     * <p>
     * The result of this method can be a negative period if the end is before the start.
     * The negative sign will be the same in each of year, month and day.
     * <p>
     * Adding the result of this method to the start date will always yield the end date.
     *
     * @param start  the start date, inclusive, not null
     * @param end  the end date, exclusive, not null
     * @return the period in days, not null
     * @throws DateTimeException if {@code LocalDate} and {@code LocalTime}
     *      cannot be extracted from the {@code start} and {@code end}
     * @throws ArithmeticException if the period exceeds the supported range
     */
    public static ISOPeriod between(DateTime start, DateTime end) {
        ISOPeriod delta = ISOPeriod.ZERO;
        
        LocalDate date1 = start.extract(LocalDate.class);
        LocalTime time1 = start.extract(LocalTime.class);
        LocalDate date2 = end.extract(LocalDate.class);
        LocalTime time2 = end.extract(LocalTime.class);

        if (date1 != null && date2 != null) {
            delta.plus(between(date1, date2));
        } else {
            if (date1 != null || date2 != null) {
                throw new DateTimeException("LocalDate not available for between");
            }
        }
        if (time1 != null && time2 != null) {
            delta.plus(between(time1, time2));
        } else {
            if (time1 != null || time2 != null) {
                throw new DateTimeException("LocalTime not available for between");
            }
        }
        return delta;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code ISOPeriod} consisting of the number of years, months,
     * and days between two dates.
     * <p>
     * The start date is included, but the end date is not. Only whole years count.
     * For example, from {@code 2010-01-15} to {@code 2011-03-18} is one year, two months and three days.
     * <p>
     * The result of this method can be a negative period if the end is before the start.
     * The negative sign will be the same in each of year, month and day.
     * <p>
     * Adding the result of this method to the start date will always yield the end date.
     *
     * @param startDate  the start date, inclusive, not null
     * @param endDate  the end date, exclusive, not null
     * @return the period in days, not null
     * @throws ArithmeticException if the period exceeds the supported range
     */
    public static ISOPeriod between(LocalDate startDate, LocalDate endDate) {
        long startMonth = startDate.getYear() * 12L + startDate.getMonth().ordinal();  // safe
        long endMonth = endDate.getYear() * 12L + endDate.getMonth().ordinal();  // safe
        long totalMonths = endMonth - startMonth;  // safe
        int days = endDate.getDayOfMonth() - startDate.getDayOfMonth();
        if (totalMonths > 0 && days < 0) {
            totalMonths--;
            LocalDate calcDate = startDate.plusMonths(totalMonths);
            days = (int) (endDate.toEpochDay() - calcDate.toEpochDay());  // safe
        } else if (totalMonths < 0 && days > 0) {
            totalMonths++;
            days -= endDate.lengthOfMonth();
        }
        long years = totalMonths / 12;  // safe
        int months = (int) (totalMonths % 12);  // safe
        return ofDate(DateTimes.safeToInt(years), months, days);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code ISOPeriod} consisting of the number of hours, minutes,
     * seconds, and nanoseconds between two times.
     * <p>
     * The start time is included, but the end time is not.
     * For example, from {@code 13:45.30.123456789} to {@code 14:50.00}
     * {@code 01:05.30.123456789}.
     * <p>
     * The result of this method can be a negative period if the end is before the start.
     * The negative sign will be the same in each of year, month and day.
     * <p>
     * Adding the result of this method to the start time will always yield the end time.
     *
     * @param startDate  the start date, inclusive, not null
     * @param endDate  the end date, exclusive, not null
     * @return the period in days, not null
     * @throws ArithmeticException if the period exceeds the supported range
     */
    public static ISOPeriod between(LocalTime startTime, LocalTime endTime) {
        long delta = endTime.toNanoOfDay() - startTime.toNanoOfDay();

        long nanos = DateTimes.floorMod(delta, 1000000000L);
        long total = DateTimes.floorDiv(delta, 1000000000L);  // safe from overflow
        int seconds = DateTimes.floorMod(total, 60);
        total  = DateTimes.floorDiv(total, 60);
        int minutes = DateTimes.floorMod(total, 60);
        total  = DateTimes.floorDiv(total, 60);
        int hours = DateTimes.safeToInt(total);
        return ISOPeriod.ofTime(hours, minutes, seconds, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code ISOPeriod} from a text string such as {@code PnYnMnDTnHnMn.nS}.
     * <p>
     * This will parse the string produced by {@code toString()} which is
     * a subset of the ISO-8601 period format {@code PnYnMnDTnHnMn.nS}.
     * <p>
     * The string consists of a series of numbers with a suffix identifying their meaning.
     * The values, and suffixes, must be in the sequence year, month, day, hour, minute, second.
     * Any of the number/suffix pairs may be omitted providing at least one is present.
     * If the period is zero, the value is normally represented as {@code PT0S}.
     * The numbers must consist of ASCII digits.
     * Any of the numbers may be negative. Negative zero is not accepted.
     * The number of nanoseconds is expressed as an optional fraction of the seconds.
     * There must be at least one digit before any decimal point.
     * There must be between 1 and 9 inclusive digits after any decimal point.
     * The letters will all be accepted in upper or lower case.
     * The decimal point may be either a dot or a comma.
     *
     * @param text  the text to parse, not null
     * @return the parsed period, not null
     * @throws CalendricalParseException if the text cannot be parsed to a period
     */
    public static ISOPeriod parse(final CharSequence text) {
        DateTimes.checkNotNull(text, "Text to parse must not be null");
        return new PeriodParser(text).parse();
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param years  the amount
     * @param months  the amount
     * @param days  the amount
     * @param hours  the amount
     * @param minutes  the amount
     * @param seconds  the amount
     * @param nanos  the amount
     */
    private ISOPeriod(int years, int months, int days, int hours, int minutes, int seconds, long nanos) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.nanos = nanos;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        if ((years | months | days | hours | minutes | seconds | nanos) == 0) {
            return ZERO;
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this period is zero-length.
     *
     * @return true if this period is zero-length
     */
    public boolean isZero() {
        return (this == ZERO);
    }

    /**
     * Checks if this period is fully positive, excluding zero.
     * <p>
     * This checks whether all the amounts in the period are positive,
     * defined as greater than zero.
     *
     * @return true if this period is fully positive excluding zero
     */
    public boolean isPositive() {
        return ((years | months | days | hours | minutes | seconds | nanos) > 0);
    }

    /**
     * Checks if this period is fully positive, including zero.
     * <p>
     * This checks whether all the amounts in the period are positive,
     * defined as greater than or equal to zero.
     *
     * @return true if this period is fully positive including zero
     */
    public boolean isPositiveOrZero() {
        return ((years | months | days | hours | minutes | seconds | nanos) >= 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of years of this period, if any.
     *
     * @return the amount of years of this period
     */
    public int getYears() {
        return years;
    }

    /**
     * Gets the amount of months of this period, if any.
     *
     * @return the amount of months of this period
     */
    public int getMonths() {
        return months;
    }

    /**
     * Gets the amount of days of this period, if any.
     *
     * @return the amount of days of this period
     */
    public int getDays() {
        return days;
    }

    /**
     * Gets the amount of hours of this period, if any.
     *
     * @return the amount of hours of this period
     */
    public int getHours() {
        return hours;
    }

    /**
     * Gets the amount of minutes of this period, if any.
     *
     * @return the amount of minutes of this period
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Gets the amount of seconds of this period, if any.
     *
     * @return the amount of seconds of this period
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Gets the amount of nanoseconds of this period, if any.
     *
     * @return the amount of nanoseconds of this period
     */
    public long getNanos() {
        return nanos;
    }

    /**
     * Gets the amount of nanoseconds of this period safely converted
     * to an {@code int}.
     *
     * @return the amount of nanoseconds of this period
     * @throws ArithmeticException if the number of nanoseconds exceeds the capacity of an {@code int}
     */
    public int getNanosInt() {
        return DateTimes.safeToInt(nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified amount of years.
     * <p>
     * This method will only affect the years field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to represent
     * @return an {@code ISOPeriod} based on this period with the requested years, not null
     */
    public ISOPeriod withYears(int years) {
        if (years == this.years) {
            return this;
        }
        return of(years, months, days, hours, minutes, seconds, nanos);
    }

    /**
     * Returns a copy of this period with the specified amount of months.
     * <p>
     * This method will only affect the months field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to represent
     * @return an {@code ISOPeriod} based on this period with the requested months, not null
     */
    public ISOPeriod withMonths(int months) {
        if (months == this.months) {
            return this;
        }
        return of(years, months, days, hours, minutes, seconds, nanos);
    }

    /**
     * Returns a copy of this period with the specified amount of days.
     * <p>
     * This method will only affect the days field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to represent
     * @return an {@code ISOPeriod} based on this period with the requested days, not null
     */
    public ISOPeriod withDays(int days) {
        if (days == this.days) {
            return this;
        }
        return of(years, months, days, hours, minutes, seconds, nanos);
    }

    /**
     * Returns a copy of this period with the specified amount of hours.
     * <p>
     * This method will only affect the hours field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to represent
     * @return an {@code ISOPeriod} based on this period with the requested hours, not null
     */
    public ISOPeriod withHours(int hours) {
        if (hours == this.hours) {
            return this;
        }
        return of(years, months, days, hours, minutes, seconds, nanos);
    }

    /**
     * Returns a copy of this period with the specified amount of minutes.
     * <p>
     * This method will only affect the minutes field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to represent
     * @return an {@code ISOPeriod} based on this period with the requested minutes, not null
     */
    public ISOPeriod withMinutes(int minutes) {
        if (minutes == this.minutes) {
            return this;
        }
        return of(years, months, days, hours, minutes, seconds, nanos);
    }

    /**
     * Returns a copy of this period with the specified amount of seconds.
     * <p>
     * This method will only affect the seconds field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to represent
     * @return an {@code ISOPeriod} based on this period with the requested seconds, not null
     */
    public ISOPeriod withSeconds(int seconds) {
        if (seconds == this.seconds) {
            return this;
        }
        return of(years, months, days, hours, minutes, seconds, nanos);
    }

    /**
     * Returns a copy of this period with the specified amount of nanoseconds.
     * <p>
     * This method will only affect the nanoseconds field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanoseconds to represent
     * @return an {@code ISOPeriod} based on this period with the requested nanoseconds, not null
     */
    public ISOPeriod withNanos(long nanos) {
        if (nanos == this.nanos) {
            return this;
        }
        return of(years, months, days, hours, minutes, seconds, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period added.
     * The result is not normalized.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param other  the period to add, not null
     * @return an {@code ISOPeriod} based on this period with the requested period added, not null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public ISOPeriod plus(ISOPeriod other) {
        return of(
                DateTimes.safeAdd(years, other.years),
                DateTimes.safeAdd(months, other.months),
                DateTimes.safeAdd(days, other.days),
                DateTimes.safeAdd(hours, other.hours),
                DateTimes.safeAdd(minutes, other.minutes),
                DateTimes.safeAdd(seconds, other.seconds),
                DateTimes.safeAdd(nanos, other.nanos));
    }

    /**
     * Returns a copy of this period with the specified period added.
     * The result is not normalized.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return an {@code ISOPeriod} based on this period with the requested period added, not null
     * @throws DateTimeException if the unit is not supported by {@code ISOPeriod}
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public ISOPeriod plus(Period period) {
        return plus(of(period));
    }

    /**
     * Returns a copy of this period with the specified PeriodUnit value added.
     * The result is not normalized.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the years to add, positive or negative
     * @param unit the PeriodUnit of the amount
     * @return an {@code ISOPeriod} based on this period with the requested years added, not null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public ISOPeriod plus(long amount, PeriodUnit unit) {
        DateTimes.checkNotNull(unit, "PeriodUnit must not be null");
        if (amount == 0) {
            return this;
        }
        if (unit instanceof LocalPeriodUnit) {
            int nvalue = DateTimes.safeToInt(amount);
            switch((LocalPeriodUnit) unit) {
                case NANOS: return of(years, months, days, hours, minutes, seconds, DateTimes.safeAdd(nanos,  nvalue));
                case MICROS: return of(years, months, days, hours, minutes, seconds, DateTimes.safeToInt(nvalue * 1000L +  nanos));
                case MILLIS: return of(years, months, days, hours, minutes, seconds, DateTimes.safeToInt(nvalue * 1000000L + nanos));
                case SECONDS: return of(years, months, days, hours, minutes, DateTimes.safeAdd(seconds, nvalue), nanos);
                case MINUTES: return of(years, months, days, hours, DateTimes.safeAdd(minutes, nvalue), seconds, nanos);
                case HOURS: return of(years, months, days, DateTimes.safeAdd(hours,  nvalue), minutes, seconds, nanos);
                case HALF_DAYS: return of(years, months, days, DateTimes.safeToInt(nvalue * 12L + hours), minutes, seconds, nanos);
                case DAYS: return of(years, months, DateTimes.safeAdd(days, nvalue), hours, minutes, seconds, nanos);
                case WEEKS: return of(years, months, DateTimes.safeToInt(nvalue * 7L + days), hours, minutes, seconds, nanos);
                case MONTHS: return of(years, DateTimes.safeAdd(months, nvalue), days, hours, minutes, seconds, nanos);
                case QUARTER_YEARS: return of(years, DateTimes.safeToInt(nvalue * 3L + months), days, hours, minutes, seconds, nanos);
                case HALF_YEARS: return of(years, DateTimes.safeToInt(nvalue * 6L + months), days, hours, minutes, seconds, nanos);
                case WEEK_BASED_YEARS:
                case YEARS: return of(DateTimes.safeAdd(years, nvalue), months, days, hours, minutes, seconds, nanos);
                case DECADES: return ISOPeriod.ofDate(DateTimes.safeToInt(amount * 10L + years), 0, 0);
                case CENTURIES: return ISOPeriod.ofDate(DateTimes.safeToInt(amount * 100L + years), 0, 0);
                case MILLENNIA: return ISOPeriod.ofDate(DateTimes.safeToInt(amount * 1000L + years), 0, 0);
                default:
                    // Fall through to handle throw unsupported PeriodUnit
            }
        }
        throw new DateTimeException("Unsupported unit: " + unit.getName());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param other  the period to subtract, not null
     * @return an {@code ISOPeriod} based on this period with the requested period subtracted, not null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public ISOPeriod minus(ISOPeriod other) {
        DateTimes.checkNotNull(other, "Period to add must not be null");
        return of(
                DateTimes.safeSubtract(years, other.years),
                DateTimes.safeSubtract(months, other.months),
                DateTimes.safeSubtract(days, other.days),
                DateTimes.safeSubtract(hours, other.hours),
                DateTimes.safeSubtract(minutes, other.minutes),
                DateTimes.safeSubtract(seconds, other.seconds),
                DateTimes.safeSubtract(nanos, other.nanos));
    }

    /**
     * Returns a copy of this period with the specified period subtracted.
     * The result is not normalized.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return an {@code ISOPeriod} based on this period with the requested period added, not null
     * @throws DateTimeException if the unit is not supported by {@code ISOPeriod}
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public ISOPeriod minus(Period period) {
        return minus(of(period));
    }

    /**
     * Returns a copy of this period with the specified PeriodUnit value subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the years to add, positive or negative
     * @param unit the PeriodUnit of the amount
     * @return an {@code ISOPeriod} based on this period with the requested years added, not null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public ISOPeriod minus(long amount, PeriodUnit unit) {
         return plus(DateTimes.safeNegate(amount), unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with each element in this period multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return an {@code ISOPeriod} based on this period with the amounts multiplied by the scalar, not null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public ISOPeriod multipliedBy(int scalar) {
        if (this == ZERO || scalar == 1) {
            return this;
        }
        return of(
                DateTimes.safeMultiply(years, scalar),
                DateTimes.safeMultiply(months, scalar),
                DateTimes.safeMultiply(days, scalar),
                DateTimes.safeMultiply(hours, scalar),
                DateTimes.safeMultiply(minutes, scalar),
                DateTimes.safeMultiply(seconds, scalar),
                DateTimes.safeMultiply(nanos, scalar));
    }

    /**
     * Returns a new instance with each element in this period divided
     * by the specified value.
     * <p>
     * The implementation simply divides each separate field by the divisor
     * using integer division.
     *
     * @param divisor  the value to divide by, not null
     * @return an {@code ISOPeriod} based on this period with the amounts divided by the divisor, not null
     * @throws ArithmeticException if dividing by zero
     */
    public ISOPeriod dividedBy(int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        if (this == ZERO || divisor == 1) {
            return this;
        }
        return of(
                years / divisor, months / divisor, days / divisor,
                hours / divisor, minutes / divisor, seconds / divisor, nanos / divisor);
    }

    /**
     * Returns a new instance with each amount in this period negated.
     *
     * @return an {@code ISOPeriod} based on this period with the amounts negated, not null
     * @throws ArithmeticException if any field has the minimum value
     */
    public ISOPeriod negated() {
        return multipliedBy(-1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with all amounts normalized to the
     * standard ranges for date-time fields.
     * <p>
     * Two normalizations occur, one for years and months, and one for
     * hours, minutes, seconds and nanoseconds.
     * Days are not normalized, as a day may vary in length at daylight savings cutover.
     * For example, a period of {@code P1Y15M1DT28H61M} will be normalized to {@code P2Y3M1DT29H1M}.
     * <p>
     * Note that this method normalizes using ISO-8601:
     * <ul>
     * <li>12 months in a year</li>
     * <li>60 minutes in an hour</li>
     * <li>60 seconds in a minute</li>
     * <li>1,000,000,000 nanoseconds in a second</li>
     * </ul>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return an {@code ISOPeriod} based on this period with the amounts normalized, not null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public ISOPeriod normalized() {
        if (this == ZERO) {
            return ZERO;
        }
        int years = DateTimes.safeAdd(this.years, DateTimes.floorDiv(this.months, 12));
        int months = DateTimes.floorMod(this.months, 12);
        long total = (this.hours * 60L * 60L) + (this.minutes * 60L) + this.seconds;  // safe from overflow
        long nanos = DateTimes.floorMod(this.nanos, 1000000000L);
        total += DateTimes.floorDiv(this.nanos, 1000000000L);  // safe from overflow
        int seconds = DateTimes.floorMod(total, 60);
        total  = DateTimes.floorDiv(total, 60);
        int minutes = DateTimes.floorMod(total, 60);
        total  = DateTimes.floorDiv(total, 60);
        int hours = DateTimes.safeToInt(total);
        return of(years, months, days, hours, minutes, seconds, nanos);
    }

    /**
     * Returns a copy of this period with all amounts normalized to the
     * standard ranges for date-time fields including the assumption that
     * days are 24 hours long.
     * <p>
     * Two normalizations occur, one for years and months, and one for
     * days, hours, minutes, seconds and nanoseconds.
     * For example, a period of {@code P1Y15M1DT28H} will be normalized to {@code P2Y3M2DT4H}.
     * <p>
     * Note that this method normalizes using ISO-8601:
     * <ul>
     * <li>12 months in a year</li>
     * <li>24 hours in a day</li>
     * <li>60 minutes in an hour</li>
     * <li>60 seconds in a minute</li>
     * <li>1,000,000,000 nanoseconds in a second</li>
     * </ul>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return an {@code ISOPeriod} based on this period with the amounts normalized, not null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public ISOPeriod normalizedWith24HourDays() {
        if (this == ZERO) {
            return ZERO;
        }
        int years = DateTimes.safeAdd(this.years, DateTimes.floorDiv(this.months, 12));
        int months = DateTimes.floorMod(this.months, 12);
        long total = (this.hours * 60L * 60L) + (this.minutes * 60L) + this.seconds;  // safe from overflow
        long nanos = DateTimes.floorMod(this.nanos, 1000000000L);
        total += DateTimes.floorDiv(this.nanos, 1000000000L);  // safe from overflow
        int seconds = DateTimes.floorMod(total, 60);
        total  = DateTimes.floorDiv(total, 60);
        int minutes = DateTimes.floorMod(total, 60);
        total  = DateTimes.floorDiv(total, 60);
        int hours = DateTimes.floorMod(total, 24);
        total  = DateTimes.floorDiv(total, 24);
        int days = DateTimes.safeToInt(this.days + total);  // safe from overflow
        return of(years, months, days, hours, minutes, seconds, nanos);
    }

    /**
     * Checks if the duration of this period is an estimate.
     * <p>
     * This method returns true if the duration is an estimate and false if it is
     * accurate. Note that accurate/estimated ignores leap seconds.
     * The duration of this period is estimated only if months and years are non-zero.
     *
     * @return true if the duration is estimated, false if accurate
     */
    boolean isDurationEstimated() {
        return this.years != 0 || this.months != 0;
    }

    /**
     * Calculates the duration of this period.
     * <p>
     * The calculation uses the days, hours, minutes, seconds and nanoseconds fields.
     * If years or months are present an exception is thrown.
     * <p>
     * The duration is calculated using ISO-8601:
     * <ul>
     * <li>24 hours in a day</li>
     * <li>60 minutes in an hour</li>
     * <li>60 seconds in a minute</li>
     * <li>1,000,000,000 nanoseconds in a second</li>
     * </ul>
     *
     * @return a {@code Duration} equivalent to this period, not null
     * @throws DateTimeException if the period cannot be converted as it contains years/months
     */
    public Duration toDuration() {
        if ((years | months) > 0) {
            throw new DateTimeException("Unable to convert period to duration as years/months are present: " + this);
        }
        long secs = ((days * 24L + hours) * 60L + minutes) * 60L + seconds;  // will not overflow
        return Duration.ofSeconds(secs, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this period is equal to another period.
     * <p>
     * The comparison is based on the amounts held in the period.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other period
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ISOPeriod) {
            ISOPeriod other = (ISOPeriod) obj;
            return years == other.years && months == other.months && days == other.days &
                    hours == other.hours && minutes == other.minutes &&
                    seconds == other.seconds && nanos == other.nanos;
        }
        return false;
    }

    /**
     * A hash code for this period.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        // SPEC: Require unique hash code for all periods where fields within these inclusive bounds:
        // years 0-31, months 0-11, days 0-31, hours 0-23, minutes 0-59, seconds 0-59
        // IMPL: Ordered such that overflow from one field doesn't immediately affect the next field
        // years 5 bits, months 4 bits, days 6 bits, hours 5 bits, minutes 6 bits, seconds 6 bits
        return ((years << 27) | (years >>> 5)) ^
                ((hours << 22) | (hours >>> 10)) ^
                ((months << 18) | (months >>> 14)) ^
                ((minutes << 12) | (minutes >>> 20)) ^
                ((days << 6) | (days >>> 26)) ^ seconds ^ (((int) nanos) + 37);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this period as a {@code String}, such as {@code P6Y3M1DT12H}.
     * <p>
     * The output will be in the ISO-8601 period format.
     *
     * @return a string representation of this period, not null
     */
    @Override
    public String toString() {
        // TODO: toString doesn't match state nanos/secs
        if (this == ZERO) {
            return "PT0S";
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append('P');
            if (years != 0) {
                buf.append(years).append('Y');
            }
            if (months != 0) {
                buf.append(months).append('M');
            }
            if (days != 0) {
                buf.append(days).append('D');
            }
            if ((hours | minutes | seconds) != 0 || nanos != 0) {
                buf.append('T');
                if (hours != 0) {
                    buf.append(hours).append('H');
                }
                if (minutes != 0) {
                    buf.append(minutes).append('M');
                }
                if (seconds != 0 || nanos != 0) {
                    if (nanos == 0) {
                        buf.append(seconds).append('S');
                    } else {
                        long s = seconds + (nanos / 1000000000);
                        long n = nanos % 1000000000;
                        if (s < 0 && n > 0) {
                            n -= 1000000000;
                            s++;
                        } else if (s > 0 && n < 0) {
                            n += 1000000000;
                            s--;
                        }
                        if (n < 0) {
                            n = -n;
                            if (s == 0) {
                                buf.append('-');
                            }
                        }
                        buf.append(s);
                        int dotPos = buf.length();
                        n += 1000000000;
                        while (n % 10 == 0) {
                            n /= 10;
                        }
                        buf.append(n);
                        buf.setCharAt(dotPos, '.');
                        buf.append('S');
                    }
                }
            }
            return buf.toString();
        }
    }

}
