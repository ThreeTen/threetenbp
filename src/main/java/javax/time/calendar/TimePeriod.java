/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import java.io.Serializable;
import java.util.TreeMap;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.MathUtils;
import javax.time.calendar.format.CalendricalParseException;

/**
 * An immutable period consisting of the ISO-8601 time-based units,
 * such as '2 Hours, 3 Minutes and 4 Seconds'.
 * <p>
 * A period is a human-scale description of an amount of time.
 * This class represents the standard time-based period units from {@link ISOChronology} -
 * '24Hours', 'Hours', 'Minutes', 'Seconds' and 'Nanoseconds'.
 * The nanoseconds are treated as a fraction of the second and are always stored as
 * a value between 0 and 999,999,999.
 * <p>
 * The {@code ISOChronology} defines a relationship between some of the units:
 * <ul>
 * <li>24 hours in a 24-hour-day</li>
 * <li>60 minutes in an hour</li>
 * <li>60 seconds in a minute</li>
 * <li>1,000,000,000 nanoseconds in a second</li>
 * </ul>
 * <p>
 * It is important to note that a 24-hour-day is not the same unit as a day.
 * A day can vary in length due to changes in the time zone.
 * A 24-hour-day never changes in length as it defined as being equal to 24 hours.
 * <p>
 * TimePeriod is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class TimePeriod
        implements PeriodProvider, Serializable {

    /**
     * A constant for a period of zero.
     */
    public static final TimePeriod ZERO = new TimePeriod(0, 0, 0, 0, 0);
    /**
     * The serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The ISO period units, trusted to not be altered.
     */
    private static final PeriodUnit[] UNITS = new PeriodUnit[] {
        ISOChronology.period24Hours(), ISOChronology.periodHours(),
        ISOChronology.periodMinutes(), ISOChronology.periodSeconds(), ISOChronology.periodNanos(),
    };

    /**
     * The number of 24-hour-days.
     */
    private final int days24;
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
     * The nanosecond-of-second.
     */
    private final int nos;
    /**
     * The cached PeriodFields.
     */
    private transient volatile PeriodFields periodFields;
    /**
     * The cached toString value.
     */
    private transient volatile String string;

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code TimePeriod} from an amount of hours, minutes and seconds.
     * <p>
     * The 24-hour-days and nanosecond fields will be zero.
     *
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @return the period, never null
     */
    public static TimePeriod of(int hours, int minutes, int seconds) {
        if ((hours | minutes | seconds) == 0) {
            return ZERO;
        }
        return new TimePeriod(0, hours, minutes, seconds, 0);
    }

    /**
     * Obtains a {@code TimePeriod} from an amount of 24-hour-days, hours, minutes, seconds
     * and nanoseconds.
     * <p>
     * The nanosecond value will be normalized to be in the range 0 to 999,999,999 by
     * altering the values of other fields.
     *
     * @param days24  the amount of 24-hour-days, may be negative
     * @param hours  the amount of hours, may be negative
     * @param minutes  the amount of minutes, may be negative
     * @param seconds  the amount of seconds, may be negative
     * @param nanos  the amount of nanos, may be negative
     * @return the period, never null
     */
    public static TimePeriod of(int days24, int hours, int minutes, int seconds, long nanos) {
        if ((days24 | hours | minutes | seconds | nanos) == 0) {
            return ZERO;
        }
        if (nanos < 0 || nanos >= 1000000000) {
            return new TimePeriod(days24, hours, minutes, seconds, 0).plusNanos(nanos);
        }
        return new TimePeriod(days24, hours, minutes, seconds, (int) nanos);  // safe
    }

    /**
     * Obtains a {@code TimePeriod} from an amount and unit.
     * <p>
     * The parameters represent the two parts of a phrase like '6 Hours'.
     * <p>
     * A {@code TimePeriod} supports 5 units, ISO 24-hour-days, hours,
     * minutes, seconds and nanoseconds. The unit must be one of these, or be
     * able to be converted to one of these.
     *
     * @param amount  the amount of the period, measured in terms of the unit, positive or negative
     * @param unit  the unit that the period is measured in, not null
     * @return the period, never null
     */
    public static TimePeriod of(int amount, PeriodUnit unit) {
        return of(PeriodFields.of(amount, unit));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code TimePeriod} from a number of 24-hour-days.
     *
     * @param days24  the amount of 24-hour-days, may be negative
     * @return the period, never null
     */
    public static TimePeriod ofDays24(int days24) {
        if (days24 == 0) {
            return ZERO;
        }
        return new TimePeriod(days24, 0, 0, 0, 0);
    }

    /**
     * Obtains a {@code TimePeriod} from a number of hours.
     *
     * @param hours  the amount of hours, may be negative
     * @return the period, never null
     */
    public static TimePeriod ofHours(int hours) {
        if (hours == 0) {
            return ZERO;
        }
        return new TimePeriod(0, hours, 0, 0, 0);
    }

    /**
     * Obtains a {@code TimePeriod} from a number of minutes.
     *
     * @param minutes  the amount of minutes, may be negative
     * @return the period, never null
     */
    public static TimePeriod ofMinutes(int minutes) {
        if (minutes == 0) {
            return ZERO;
        }
        return new TimePeriod(0, 0, minutes, 0, 0);
    }

    /**
     * Obtains a {@code TimePeriod} from a number of seconds.
     *
     * @param seconds  the amount of seconds, may be negative
     * @return the period, never null
     */
    public static TimePeriod ofSeconds(int seconds) {
        if (seconds == 0) {
            return ZERO;
        }
        return new TimePeriod(0, 0, 0, seconds, 0);
    }

    /**
     * Obtains a {@code TimePeriod} from a number of nanoseconds.
     * <p>
     * The nanosecond value will be normalized to be in the range 0 to 999,999,999 by
     * altering the values of other fields.
     *
     * @param nanos  the amount of nanos, may be negative
     * @return the period, never null
     */
    public static TimePeriod ofNanos(long nanos) {
        return ZERO.plusNanos(nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code TimePeriod} from a period, throwing an exception
     * if there are any time fields.
     * <p>
     * A {@code TimePeriod} supports 5 units, ISO 24-hour-days, hours, minutes, seconds and nanoseconds.
     * The period specified must only contain these units, or units that can be
     * {@link PeriodFields#toEquivalent converted} to these units.
     * <p>
     * An exception occurs if the period contains other fields, such as years or months.
     * See {@link #ofTimeFields(PeriodProvider)} for a more lenient alternative.
     *
     * @param periodProvider  a provider of period information, not null
     * @return the period, never null
     * @throws CalendricalException if the provided period cannot be converted to the supported units
     * @throws ArithmeticException if any provided amount, exceeds an {@code int}
     */
    public static TimePeriod of(PeriodProvider periodProvider) {
        return of(periodProvider, false);
    }

    /**
     * Obtains a {@code TimePeriod} from a period, ignoring any date fields.
     * <p>
     * A {@code TimePeriod} supports 5 units, ISO 24-hour-days, hours, minutes, seconds and nanoseconds.
     * This method extracts only those units which can be converted to one the required 3 units.
     * <p>
     * No error occurs if the period contains other fields, such as years or months.
     * See {@link #of(PeriodProvider)} for a stricter alternative.
     *
     * @param periodProvider  a provider of period information, not null
     * @return the period, never null
     * @throws ArithmeticException if any provided amount, exceeds an {@code int}
     */
    public static TimePeriod ofTimeFields(PeriodProvider periodProvider) {
        return of(periodProvider, true);
    }

    private static TimePeriod of(PeriodProvider periodProvider, boolean lenient) {
        PeriodFields.checkNotNull(periodProvider, "PeriodProvider must not be null");
        if (periodProvider instanceof TimePeriod) {
            return (TimePeriod) periodProvider;
        }
        PeriodFields periodFields = PeriodFields.of(periodProvider);
        if (lenient) {
            periodFields = periodFields.retainConvertible(UNITS);
        }
        periodFields = periodFields.toEquivalent(UNITS);
        int days24 = periodFields.getAmountInt(ISOChronology.period24Hours());
        int hours = periodFields.getAmountInt(ISOChronology.periodHours());
        int minutes = periodFields.getAmountInt(ISOChronology.periodMinutes());
        int seconds = periodFields.getAmountInt(ISOChronology.periodSeconds());
        long nanos = periodFields.getAmount(ISOChronology.periodNanos());
        return of(days24, hours, minutes, seconds, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code TimePeriod} from a {@code Duration}.
     * <p>
     * The created period will have normalized values for the 24-hour-days,
     * hours, minutes, seconds and nanoseconds fields.
     *
     * @param duration  the duration to create from, not null
     * @return the {@code PeriodFields} instance, never null
     * @throws ArithmeticException if the result exceeds the supported period range
     */
    public static TimePeriod of(Duration duration) {
        PeriodFields.checkNotNull(duration, "Duration must not be null");
        if (duration.isZero()) {
            return ZERO;
        }
        int days = MathUtils.safeToInt(duration.getSeconds() / (3600 * 24));
        int amount = ((int) duration.getSeconds() % (3600 * 24));
        int hours = amount / 3600;
        amount = amount % 3600;
        return new TimePeriod(days, hours, amount / 60, amount % 60, duration.getNanoOfSecond());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code TimePeriod} from a string formatted as {@code PnDTnHnMn.nS}.
     * <p>
     * This will parse the string produced by {@code toString()} which is
     * a subset of the ISO8601 period format {@code PnDTnHnMn.nS}.
     * <p>
     * The string consists of a series of numbers with a suffix identifying their meaning.
     * The values, and suffixes, must be in the sequence day, hour, minute, second.
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
     * @return the parsed period, never null
     * @throws CalendricalParseException if the text cannot be parsed to a Period
     */
    public static TimePeriod parse(final String text) {
        PeriodFields.checkNotNull(text, "Text to parse must not be null");
        Period period = PeriodParser.getInstance().parse(text);
        if (period.getYears() != 0 || period.getMonths() != 0) {
            throw new CalendricalParseException("Period must not contain years or months", text, 1);
        }
        return of(period.getDays(), period.getHours(), period.getMinutes(), period.getSeconds(), period.getNanos());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param days24  the amount
     * @param hours  the amount
     * @param minutes  the amount
     * @param seconds  the amount
     * @param nos  the amount
     */
    private TimePeriod(int days24, int hours, int minutes, int seconds, int nos) {
        this.days24 = days24;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.nos = nos;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        if ((days24 | hours | minutes | seconds | nos) == 0) {
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
        return ((days24 | hours | minutes | seconds | nos) > 0);
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
        return ((days24 | hours | minutes | seconds | nos) >= 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of 24-hour-days of this period, if any.
     *
     * @return the amount of 24-hour-days of this period
     */
    public int getDays24() {
        return days24;
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
     * Gets the fractional nanosecond-of-second part of this period.
     * <p>
     * This value is always from 0 to 999,999,999.
     *
     * @return the nanosecond-of-second of this period, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return nos;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified amount of 24-hour-days.
     * <p>
     * This method will only affect the the 24-hour-days field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days24  the 24-hour-days to represent
     * @return a period based on this period with the requested days, never null
     */
    public TimePeriod withDays24(int days24) {
        if (days24 == this.days24) {
            return this;
        }
        return of(days24, hours, minutes, seconds, nos);
    }

    /**
     * Returns a copy of this period with the specified amount of hours.
     * <p>
     * This method will only affect the the hours field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to represent
     * @return a period based on this period with the requested hours, never null
     */
    public TimePeriod withHours(int hours) {
        if (hours == this.hours) {
            return this;
        }
        return of(days24, hours, minutes, seconds, nos);
    }

    /**
     * Returns a copy of this period with the specified amount of minutes.
     * <p>
     * This method will only affect the the minutes field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to represent
     * @return a period based on this period with the requested minutes, never null
     */
    public TimePeriod withMinutes(int minutes) {
        if (minutes == this.minutes) {
            return this;
        }
        return of(days24, hours, minutes, seconds, nos);
    }

    /**
     * Returns a copy of this period with the specified amount of seconds.
     * <p>
     * This method will only affect the the seconds field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to represent
     * @return a period based on this period with the requested seconds, never null
     */
    public TimePeriod withSeconds(int seconds) {
        if (seconds == this.seconds) {
            return this;
        }
        return of(days24, hours, minutes, seconds, nos);
    }

    /**
     * Returns a copy of this period with the specified nanosecond-of-second.
     * <p>
     * This method will only affect the the nanosecond-of-second field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nanosecond-of-second to represent, from 0 to 999,999,999
     * @return a period based on this period with the requested nanoseconds, never null
     */
    public TimePeriod withNanoOfSecond(long nanoOfSecond) {
        if (nanoOfSecond == this.nos) {
            return this;
        }
        return of(days24, hours, minutes, seconds, nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a period based on this period with the requested period added, never null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public TimePeriod plus(PeriodProvider periodProvider) {
        TimePeriod other = of(periodProvider);
        return of(
                MathUtils.safeAdd(days24, other.days24),
                MathUtils.safeAdd(hours, other.hours),
                MathUtils.safeAdd(minutes, other.minutes),
                MathUtils.safeAdd(seconds, other.seconds),
                nos + other.nos);  // safe
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified number of 24-hour-days added.
     * <p>
     * This method will only affect the the days field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days24  the 24-hour-days to add, positive or negative
     * @return a period based on this period with the requested 24-hour-days added, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public TimePeriod plusDays24(int days24) {
        return withDays24(MathUtils.safeAdd(this.days24, days24));
    }

    /**
     * Returns a copy of this period with the specified number of hours added.
     * <p>
     * This method will only affect the the hours field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, positive or negative
     * @return a period based on this period with the requested hours added, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public TimePeriod plusHours(int hours) {
        return withHours(MathUtils.safeAdd(this.hours, hours));
    }

    /**
     * Returns a copy of this period with the specified number of minutes added.
     * <p>
     * This method will only affect the the minutes field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, positive or negative
     * @return a period based on this period with the requested minutes added, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public TimePeriod plusMinutes(int minutes) {
        return withMinutes(MathUtils.safeAdd(this.minutes, minutes));
    }

    /**
     * Returns a copy of this period with the specified number of seconds added.
     * <p>
     * This method will only affect the the seconds field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, positive or negative
     * @return a period based on this period with the requested seconds added, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public TimePeriod plusSeconds(int seconds) {
        return withSeconds(MathUtils.safeAdd(this.seconds, seconds));
    }

    /**
     * Returns a copy of this period with the specified number of nanoseconds added.
     * <p>
     * The nanosecond-of-second field is normalized between 0 and 999,999,999.
     * This may cause the second field to be altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanoseconds to add, positive or negative
     * @return a period based on this period with the requested nanoseconds added, never null
     * @throws ArithmeticException if the capacity of a {@code long} is exceeded
     */
    public TimePeriod plusNanos(long nanos) {
        long seconds = nanos / 1000000000;
        nanos = nanos % 1000000000;
        nanos += this.nos;
        seconds += MathUtils.floorDiv(nanos, 1000000000);
        int nos = MathUtils.floorMod(nanos, 1000000000);
        return plusSeconds(MathUtils.safeToInt(seconds)).withNanoOfSecond(nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a period based on this period with the requested period subtracted, never null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public TimePeriod minus(PeriodProvider periodProvider) {
        TimePeriod other = of(periodProvider);
        return of(
                MathUtils.safeSubtract(days24, other.days24),
                MathUtils.safeSubtract(hours, other.hours),
                MathUtils.safeSubtract(minutes, other.minutes),
                MathUtils.safeSubtract(seconds, other.seconds),
                nos - other.nos);  // safe
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified number of 24-hour-days subtracted.
     * <p>
     * This method will only affect the the 24-hour-days field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to subtract, positive or negative
     * @return a period based on this period with the requested 24-hour-days subtracted, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public TimePeriod minusDays24(int days) {
        return withDays24(MathUtils.safeSubtract(this.days24, days));
    }

    /**
     * Returns a copy of this period with the specified number of hours subtracted.
     * <p>
     * This method will only affect the the hours field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to subtract, positive or negative
     * @return a period based on this period with the requested hours subtracted, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public TimePeriod minusHours(int hours) {
        return withHours(MathUtils.safeSubtract(this.hours, hours));
    }

    /**
     * Returns a copy of this period with the specified number of minutes subtracted.
     * <p>
     * This method will only affect the the minutes field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to subtract, positive or negative
     * @return a period based on this period with the requested minutes subtracted, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public TimePeriod minusMinutes(int minutes) {
        return withMinutes(MathUtils.safeSubtract(this.minutes, minutes));
    }

    /**
     * Returns a copy of this period with the specified number of seconds subtracted.
     * <p>
     * This method will only affect the the seconds field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract, positive or negative
     * @return a period based on this period with the requested seconds subtracted, never null
     * @throws ArithmeticException if the capacity of an {@code int} is exceeded
     */
    public TimePeriod minusSeconds(int seconds) {
        return withSeconds(MathUtils.safeSubtract(this.seconds, seconds));
    }

    /**
     * Returns a copy of this period with the specified number of nanoseconds subtracted.
     * <p>
     * This method will only affect the the nanoseconds field.
     * All other fields are left untouched.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanoseconds to subtract, positive or negative
     * @return a period based on this period with the requested nanoseconds subtracted, never null
     * @throws ArithmeticException if the capacity of a {@code long} is exceeded
     */
    public TimePeriod minusNanos(long nanos) {
        if (nanos == Long.MIN_VALUE) {
            return plusNanos(1).plusNanos(Long.MAX_VALUE);
        } else {
            return plusNanos(-nanos);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with each element in this period multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return a period based on this period with the amounts multiplied by the scalar, never null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public TimePeriod multipliedBy(int scalar) {
        if (this == ZERO || scalar == 1) {
            return this;
        }
        return of(
                MathUtils.safeMultiply(days24, scalar),
                MathUtils.safeMultiply(hours, scalar),
                MathUtils.safeMultiply(minutes, scalar),
                MathUtils.safeMultiply(seconds, scalar),
                MathUtils.safeMultiply(nos, scalar));
    }

    /**
     * Returns a new instance with each element in this period divided
     * by the specified value.
     * <p>
     * The implementation simply divides each separate field by the divisor
     * using integer division.
     *
     * @param divisor  the value to divide by, not null
     * @return a period based on this period with the amounts divided by the divisor, never null
     * @throws ArithmeticException if dividing by zero
     */
    public TimePeriod dividedBy(int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        if (this == ZERO || divisor == 1) {
            return this;
        }
        return of(
                days24 / divisor,
                hours / divisor, minutes / divisor, seconds / divisor, nos / divisor);
    }

    /**
     * Returns a new instance with each amount in this period negated.
     *
     * @return a period based on this period with the amounts negated, never null
     * @throws ArithmeticException if any field has the minimum value
     */
    public TimePeriod negated() {
        return multipliedBy(-1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with all amounts normalized to the standard ranges.
     * <p>
     * The normalization adjusts each field to be within its standard range, such as
     * from 0 to 59 for minutes.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a period based on this period with the amounts normalized, never null
     * @throws ArithmeticException if the capacity of any field is exceeded
     */
    public TimePeriod normalized() {
        if (this == ZERO) {
            return ZERO;
        }
        long total = ((days24 * 24L + hours) * 3600L) + (minutes * 60L) + seconds;  // will not overflow
        int seconds = (int) (total % 60);
        total /= 60;
        int minutes = (int) (total % 60);
        total /= 60;
        int hours = (int) (total % 24);
        int days24 = MathUtils.safeToInt(total / 24);
        return of(days24, hours, minutes, seconds, nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the total number of hours represented by this period.
     *
     * @return the total number of hours
     */
    public long totalHours() {
        return days24 * 24L + hours + (minutes + seconds / 60L) / 60L;  // will not overflow
    }

    /**
     * Gets the total number of minutes represented by this period.
     *
     * @return the total number of minutes
     */
    public long totalMinutes() {
        return (days24 * 24L + hours) * 60L + minutes + seconds / 60L;  // will not overflow
    }

    /**
     * Gets the total number of seconds represented by this period.
     *
     * @return the total number of seconds
     */
    public long totalSeconds() {
        return ((days24 * 24L + hours) * 60L + minutes) * 60L + seconds;  // will not overflow
    }

    /**
     * Gets the total number of nanoseconds represented by this period .
     *
     * @return the total number of nanoseconds
     * @throws ArithmeticException if the capacity of a {@code long} is exceeded
     */
    public long totalNanos() {
        if (this == ZERO) {
            return 0;
        }
        long nanos = MathUtils.safeMultiply(totalSeconds(), 1000000000L);
        return MathUtils.safeAdd(nanos, nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this period to a {@code PeriodFields}.
     * <p>
     * The returned {@code PeriodFields} will only contain the non-zero amounts.
     *
     * @return a {@code PeriodFields} equivalent to this period, never null
     */
    public PeriodFields toPeriodFields() {
        PeriodFields fields = periodFields;
        if (fields == null) {
            TreeMap<PeriodUnit, PeriodField> map = new TreeMap<PeriodUnit, PeriodField>();
            if (days24 != 0) {
                map.put(ISOChronology.periodDays(), PeriodField.of(days24, ISOChronology.periodDays()));
            }
            if (hours != 0) {
                map.put(ISOChronology.periodHours(), PeriodField.of(hours, ISOChronology.periodHours()));
            }
            if (minutes != 0) {
                map.put(ISOChronology.periodMinutes(), PeriodField.of(minutes, ISOChronology.periodMinutes()));
            }
            if (seconds != 0) {
                map.put(ISOChronology.periodSeconds(), PeriodField.of(seconds, ISOChronology.periodSeconds()));
            }
            if (nos != 0) {
                map.put(ISOChronology.periodNanos(), PeriodField.of(nos, ISOChronology.periodNanos()));
            }
            periodFields = fields = PeriodFields.create(map);
        }
        return fields;
    }

    /**
     * Calculates the accurate duration of this period.
     *
     * @return a {@code Duration} equivalent to this period, never null
     */
    public Duration toDuration() {
        return Duration.ofSeconds(totalSeconds(), nos);
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
        if (obj instanceof TimePeriod) {
            TimePeriod other = (TimePeriod) obj;
            return days24 == other.days24 &
                    hours == other.hours && minutes == other.minutes &&
                    seconds == other.seconds && nos == other.nos;
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
        return ((hours << 24) | (hours >>> 8)) ^
                ((minutes << 14) | (minutes >>> 18)) ^
                ((days24 << 6) | (days24 >>> 26)) ^ seconds ^ (nos + 37);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the amount of time.
     *
     * @return the amount of time in ISO8601 string format
     */
    @Override
    public String toString() {
        String str = string;
        if (str == null) {
            if (this == ZERO) {
                str = "PT0S";
            } else {
                StringBuilder buf = new StringBuilder();
                buf.append('P');
                if (days24 != 0) {
                    buf.append(days24).append('D');
                }
                if ((hours | minutes | seconds) != 0 || nos != 0) {
                    buf.append('T');
                    if (hours != 0) {
                        buf.append(hours).append('H');
                    }
                    if (minutes != 0) {
                        buf.append(minutes).append('M');
                    }
                    if (seconds != 0 || nos != 0) {
                        if (nos == 0) {
                            buf.append(seconds).append('S');
                        } else {
                            long s = seconds + (nos / 1000000000);
                            long n = nos % 1000000000;
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
                str = buf.toString();
            }
            string = str;
        }
        return str;
    }

}
