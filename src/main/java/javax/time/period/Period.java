/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.period;

import java.io.Serializable;

import javax.time.MathUtils;

/**
 * An immutable period consisting of the year, month, day, hour, minute and second fields.
 * <p>
 * As an example, the period "3 months, 4 days and 7 hours" can be stored.
 * <p>
 * Period is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class Period
        implements PeriodProvider, Serializable {

    /**
     * A constant for a period of zero.
     */
    public static final Period ZERO = new Period(0, 0, 0, 0, 0, 0);
    /**
     * A serialization identifier for this class.
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
     * The cached toString value.
     */
    private transient volatile String string;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Period</code> from a provider of periods.
     * <p>
     * In addition to calling {@link PeriodProvider#toPeriod()} this method
     * also checks the validity of the result of the provider.
     *
     * @param periodProvider  a provider of period information, not null
     * @return the created period instance, never null
     */
    public static Period period(PeriodProvider periodProvider) {
        if (periodProvider == null) {
            throw new NullPointerException("Period provider must not be null");
        }
        Period provided = periodProvider.toPeriod();
        if (provided == null) {
            throw new NullPointerException("The implementation of PeriodProvider must not return null");
        }
        return provided;
    }

    /**
     * Obtains an instance of <code>Period</code> from amounts from years to seconds.
     *
     * @param years  the amount of years
     * @param months  the amount of months
     * @param days  the amount of days
     * @param hours  the amount of hours
     * @param minutes  the amount of minutes
     * @param seconds  the amount of seconds
     * @return the created period instance, never null
     */
    public static Period period(int years, int months, int days, int hours, int minutes, int seconds) {
        if ((years | months | days | hours | minutes | seconds) == 0) {
            return ZERO;
        }
        return new Period(years, months, days, hours, minutes, seconds);
    }

    /**
     * Obtains an instance of <code>Period</code> from years, months and days.
     *
     * @param years  the amount of years
     * @param months  the amount of months
     * @return the created period instance, never null
     */
    public static Period yearsMonths(int years, int months) {
        if ((years | months) == 0) {
            return ZERO;
        }
        return new Period(years, months, 0, 0, 0, 0);
    }

    /**
     * Obtains an instance of <code>Period</code> from years, months and days.
     *
     * @param years  the amount of years
     * @param months  the amount of months
     * @param days  the amount of days
     * @return the created period instance, never null
     */
    public static Period yearsMonthsDays(int years, int months, int days) {
        if ((years | months | days) == 0) {
            return ZERO;
        }
        return new Period(years, months, days, 0, 0, 0);
    }

    /**
     * Obtains an instance of <code>Period</code> from hours, minutes and seconds.
     *
     * @param hours  the amount of hours
     * @param minutes  the amount of minutes
     * @param seconds  the amount of seconds
     * @return the created period instance, never null
     */
    public static Period hoursMinutesSeconds(int hours, int minutes, int seconds) {
        if ((hours | minutes | seconds) == 0) {
            return ZERO;
        }
        return new Period(0, 0, 0, hours, minutes, seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a period of years.
     *
     * @param years  the amount of years
     * @return the created period instance, never null
     */
    public static Period years(int years) {
        if (years == 0) {
            return ZERO;
        }
        return new Period(years, 0, 0, 0, 0, 0);
    }

    /**
     * Creates a period of months.
     *
     * @param months  the amount of months
     * @return the created period instance, never null
     */
    public static Period months(int months) {
        if (months == 0) {
            return ZERO;
        }
        return new Period(0, months, 0, 0, 0, 0);
    }

    /**
     * Creates a period of days.
     *
     * @param days  the amount of days
     * @return the created period instance, never null
     */
    public static Period days(int days) {
        if (days == 0) {
            return ZERO;
        }
        return new Period(0, 0, days, 0, 0, 0);
    }

    /**
     * Creates a period of hours.
     *
     * @param hours  the amount of hours
     * @return the created period instance, never null
     */
    public static Period hours(int hours) {
        if (hours == 0) {
            return ZERO;
        }
        return new Period(0, 0, 0, hours, 0, 0);
    }

    /**
     * Creates a period of minutes.
     *
     * @param minutes  the amount of minutes
     * @return the created period instance, never null
     */
    public static Period minutes(int minutes) {
        if (minutes == 0) {
            return ZERO;
        }
        return new Period(0, 0, 0, 0, minutes, 0);
    }

    /**
     * Creates a period of seconds.
     *
     * @param seconds  the amount of seconds
     * @return the created period instance, never null
     */
    public static Period seconds(int seconds) {
        if (seconds == 0) {
            return ZERO;
        }
        return new Period(0, 0, 0, 0, 0, seconds);
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
     */
    private Period(int years, int months, int days, int hours, int minutes, int seconds) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        if ((years | months | days | hours | minutes | seconds) == 0) {
            return ZERO;
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the period is zero-length.
     *
     * @return true if this period is zero-length
     */
    public boolean isZero() {
        return (this == ZERO);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of years of the overall period, if any.
     *
     * @return the amount of years of the overall period
     */
    public int getYears() {
        return years;
    }

    /**
     * Gets the amount of months of the overall period, if any.
     *
     * @return the amount of months of the overall period
     */
    public int getMonths() {
        return months;
    }

    /**
     * Gets the amount of days of the overall period, if any.
     *
     * @return the amount of days of the overall period
     */
    public int getDays() {
        return days;
    }

    /**
     * Gets the amount of hours of the overall period, if any.
     *
     * @return the amount of hours of the overall period
     */
    public int getHours() {
        return hours;
    }

    /**
     * Gets the amount of minutes of the overall period, if any.
     *
     * @return the amount of minutes of the overall period
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Gets the amount of seconds of the overall period, if any.
     *
     * @return the amount of seconds of the overall period
     */
    public int getSeconds() {
        return seconds;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified amount of years.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to represent
     * @return a new updated period instance, never null
     */
    public Period withYears(int years) {
        if (years == this.years) {
            return this;
        }
        return period(years, months, days, hours, minutes, seconds);
    }

    /**
     * Returns a copy of this period with the specified amount of months.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to represent
     * @return a new updated period instance, never null
     */
    public Period withMonths(int months) {
        if (months == this.months) {
            return this;
        }
        return period(years, months, days, hours, minutes, seconds);
    }

    /**
     * Returns a copy of this period with the specified amount of days.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to represent
     * @return a new updated period instance, never null
     */
    public Period withDays(int days) {
        if (days == this.days) {
            return this;
        }
        return period(years, months, days, hours, minutes, seconds);
    }

    /**
     * Returns a copy of this period with the specified amount of hours.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to represent
     * @return a new updated period instance, never null
     */
    public Period withHours(int hours) {
        if (hours == this.hours) {
            return this;
        }
        return period(years, months, days, hours, minutes, seconds);
    }

    /**
     * Returns a copy of this period with the specified amount of minutes.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to represent
     * @return a new updated period instance, never null
     */
    public Period withMinutes(int minutes) {
        if (minutes == this.minutes) {
            return this;
        }
        return period(years, months, days, hours, minutes, seconds);
    }

    /**
     * Returns a copy of this period with the specified amount of seconds.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to represent
     * @return a new updated period instance, never null
     */
    public Period withSeconds(int seconds) {
        if (seconds == this.seconds) {
            return this;
        }
        return period(years, months, days, hours, minutes, seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a new updated period instance, never null
     * @throws NullPointerException if the period to add is null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period plus(PeriodProvider periodProvider) {
        Period other = period(periodProvider);
        return period(
                MathUtils.safeAdd(years, other.years),
                MathUtils.safeAdd(months, other.months),
                MathUtils.safeAdd(days, other.days),
                MathUtils.safeAdd(hours, other.hours),
                MathUtils.safeAdd(minutes, other.minutes),
                MathUtils.safeAdd(seconds, other.seconds));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified number of years added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, positive or negative
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period plusYears(int years) {
        return withYears(MathUtils.safeAdd(this.years, years));
    }

    /**
     * Returns a copy of this period with the specified number of months added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, positive or negative
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period plusMonths(int months) {
        return withMonths(MathUtils.safeAdd(this.months, months));
    }

    /**
     * Returns a copy of this period with the specified number of days added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, positive or negative
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period plusDays(int days) {
        return withDays(MathUtils.safeAdd(this.days, days));
    }

    /**
     * Returns a copy of this period with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, positive or negative
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period plusHours(int hours) {
        return withHours(MathUtils.safeAdd(this.hours, hours));
    }

    /**
     * Returns a copy of this period with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, positive or negative
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period plusMinutes(int minutes) {
        return withMinutes(MathUtils.safeAdd(this.minutes, minutes));
    }

    /**
     * Returns a copy of this period with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, positive or negative
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period plusSeconds(int seconds) {
        return withSeconds(MathUtils.safeAdd(this.seconds, seconds));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a new updated period instance, never null
     * @throws NullPointerException if the period to subtract is null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period minus(PeriodProvider periodProvider) {
        Period other = period(periodProvider);
        return period(
                MathUtils.safeSubtract(years, other.years),
                MathUtils.safeSubtract(months, other.months),
                MathUtils.safeSubtract(days, other.days),
                MathUtils.safeSubtract(hours, other.hours),
                MathUtils.safeSubtract(minutes, other.minutes),
                MathUtils.safeSubtract(seconds, other.seconds));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified number of years subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to subtract
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period minusYears(int years) {
        return withYears(MathUtils.safeSubtract(this.years, years));
    }

    /**
     * Returns a copy of this period with the specified number of months subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to subtract
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period minusMonths(int months) {
        return withMonths(MathUtils.safeSubtract(this.months, months));
    }

    /**
     * Returns a copy of this period with the specified number of days subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period minusDays(int days) {
        return withDays(MathUtils.safeSubtract(this.days, days));
    }

    /**
     * Returns a copy of this period with the specified number of hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period minusHours(int hours) {
        return withHours(MathUtils.safeSubtract(this.hours, hours));
    }

    /**
     * Returns a copy of this period with the specified number of minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period minusMinutes(int minutes) {
        return withMinutes(MathUtils.safeSubtract(this.minutes, minutes));
    }

    /**
     * Returns a copy of this period with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period minusSeconds(int seconds) {
        return withSeconds(MathUtils.safeSubtract(this.seconds, seconds));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with each element in this period multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return the new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period multipliedBy(int scalar) {
        if (this == ZERO || scalar == 1) {
            return this;
        }
        return period(
                MathUtils.safeMultiply(years, scalar),
                MathUtils.safeMultiply(months, scalar),
                MathUtils.safeMultiply(days, scalar),
                MathUtils.safeMultiply(hours, scalar),
                MathUtils.safeMultiply(minutes, scalar),
                MathUtils.safeMultiply(seconds, scalar));
    }

    /**
     * Returns a new instance with each element in this period divided
     * by the specified value.
     *
     * @param divisor  the value to divide by, not null
     * @return the new updated period instance, never null
     * @throws ArithmeticException if dividing by zero
     */
    public Period dividedBy(int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        if (this == ZERO || divisor == 1) {
            return this;
        }
        return period(
                years / divisor, months / divisor, days / divisor,
                hours / divisor, minutes / divisor, seconds / divisor);
    }

    /**
     * Returns a new instance with each amount in this period negated.
     *
     * @return the new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period negated() {
        return multipliedBy(-1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with all amounts normalized to the
     * standard ranges for date-time fields.
     * <p>
     * Two normalizations occur, one for years and months, and one for days,
     * hours, minutes and seconds.
     * For example, a period of P1Y15M will be normalized to 2Y3M.
     * <p>
     * Note that this method normalizes using assumptions:
     * <ul>
     * <li>12 months in a year</li>
     * <li>24 hours in a day</li>
     * <li>60 minutes in an hour</li>
     * <li>60 seconds in a minute</li>
     * </ul>
     * Since some days are not 24 hours long (at daylight savings cutover) and some
     * minutes are not 60 seconds long (due to leap seconds) it is essential that you
     * consider whether these assumptions meet your requirements before using this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public Period normalized() {
        if (this == ZERO) {
            return ZERO;
        }
        int years = this.years;
        int months = this.months;
        if (months >= 12) {
            years = MathUtils.safeAdd(years, months / 12);
            months = months % 12;
        }
        int days = this.days;
        int hours = this.hours;
        int minutes = this.minutes;
        int seconds = this.seconds;
        if (hours >= 24) {
            days = MathUtils.safeAdd(days, hours / 24);
            hours = hours % 24;
        }
        if (minutes >= 60) {
            hours = MathUtils.safeAdd(hours, minutes / 60);
            minutes = minutes % 60;
            if (hours >= 24) {
                days = MathUtils.safeAdd(days, hours / 24);
                hours = hours % 24;
            }
        }
        if (seconds >= 60) {
            minutes = MathUtils.safeAdd(minutes, seconds / 60);
            seconds = seconds % 60;
            if (minutes >= 60) {
                hours = MathUtils.safeAdd(hours, minutes / 60);
                minutes = minutes % 60;
                if (hours >= 24) {
                    days = MathUtils.safeAdd(days, hours / 24);
                    hours = hours % 24;
                }
            }
        }
        return period(years, months, days, hours, minutes, seconds);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a <code>Period</code>, trivially returning <code>this</code>.
     *
     * @return <code>this</code>, never null
     */
    public Period toPeriod() {
        return this;
    }

    /**
     * Converts this object to a <code>PeriodFields</code> instance.
     * <p>
     * The resulting period is always normalized such that it does not contain
     * zero amounts.
     *
     * @return an equivalent period fields instance, never null
     */
    public PeriodFields toPeriodFields() {
        // TODO: Maybe remove?
        return PeriodFields.periodFields(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this period equal to the specified period.
     *
     * @param obj  the other period to compare to, null returns false
     * @return true if this instance is equal to the specified period
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Period) {
            Period other = (Period) obj;
            return years == other.years && months == other.months && days == other.days &
                    hours == other.hours && minutes == other.minutes && seconds == other.seconds;
        }
        return false;
    }

    /**
     * Returns the hash code for this period.
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
                ((days << 6) | (days >>> 26)) ^ seconds;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the amount of time.
     *
     * @return the amount of time in ISO8601 string format
     */
    @Override
    public String toString() {
        String s = string;
        if (s == null) {
            if (this == ZERO) {
                s = "PT0S";
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
                if ((hours | minutes | seconds) != 0) {
                    buf.append('T');
                    if (hours != 0) {
                        buf.append(hours).append('H');
                    }
                    if (minutes != 0) {
                        buf.append(minutes).append('M');
                    }
                    if (seconds != 0) {
                        buf.append(seconds).append('S');
                    }
                }
                s = buf.toString();
            }
            string = s;
        }
        return s;
    }

}
