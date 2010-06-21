/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import javax.time.calendar.format.CalendricalParseException;

/**
 * A duration between two instants on the time-line.
 * <p>
 * The Time Framework for Java models time as a series of instantaneous events,
 * known as {@link Instant instants}, along a single time-line.
 * This class represents the duration between two of those instants.
 * The model is of a directed duration, meaning that the duration may be negative.
 * <p>
 * A physical instant is an instantaneous event.
 * However, for practicality the API and this class uses a precision of nanoseconds.
 * <p>
 * A physical duration could be of infinite length.
 * However, for practicality the API and this class limits the length to the
 * number of seconds that can be held in a {@code long}.
 * <p>
 * In order to represent the data a 96 bit number is required. To achieve this the
 * data is stored as seconds, measured using a {@code long}, and nanoseconds,
 * measured using an {@code int}. The nanosecond part will always be between
 * 0 and 999,999,999 representing the nanosecond part of the second.
 * For example, the negative duration of {@code PT-0.1S} is represented as
 * -1 second and 900,000,000 nanoseconds.
 * <p>
 * In this API, the unit of "seconds" only has a precise meaning when applied to an instant.
 * This is because it is the instant that defines the time scale used, not the duration.
 * For example, the simplified UTC time scale used by {@code Instant} ignores leap seconds,
 * which alters the effective length of a second. By comparison, the TAI time scale follows
 * the international scientific definition of a second exactly.
 * For most applications, this subtlety will be irrelevant.
 * <p>
 * Duration is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class Duration implements Comparable<Duration>, Serializable {

    /**
     * Constant for a duration of zero.
     */
    public static final Duration ZERO = new Duration(0, 0);
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;
    /**
     * Constant for nanos per microsecond.
     */
    private static final BigInteger BI_NANOS_PER_MICRO = BigInteger.valueOf(1000L);
    /**
     * Constant for nanos per millisecond.
     */
    private static final BigInteger BI_NANOS_PER_MILLI = BigInteger.valueOf(1000000L);
    /**
     * Constant for nanos per second.
     */
    private static final BigInteger BI_NANOS_PER_SECOND = Instant.BILLION;
    /**
     * Constant for nanos per minute.
     */
    private static final BigInteger BI_NANOS_PER_MINUTE = BigInteger.valueOf(60L * 1000000000L);
    /**
     * Constant for nanos per hour.
     */
    private static final BigInteger BI_NANOS_PER_HOUR = BigInteger.valueOf(60L * 60L * 1000000000L);
    /**
     * Constant for nanos per day.
     */
    private static final BigInteger BI_NANOS_PER_DAY = BigInteger.valueOf(24L * 60L * 60L * 1000000000L);
    /**
     * Constant for maximum long.
     */
    private static final BigInteger BI_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    /**
     * Constant for minimum long.
     */
    private static final BigInteger BI_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

    /**
     * The number of seconds in the duration.
     */
    private final long seconds;
    /**
     * The number of nanoseconds in the duration, expressed as a fraction of the
     * number of seconds. This is always positive, and never exceeds 999,999,999.
     */
    private final int nanos;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Duration} from a number of seconds.
     * <p>
     * The nanosecond in second field is set to zero.
     *
     * @param seconds  the number of seconds, positive or negative
     * @return a {@code Duration}, never null
     */
    public static Duration ofSeconds(long seconds) {
        return create(seconds, 0);
    }

    /**
     * Obtains an instance of {@code Duration} from a number of seconds
     * and an adjustment in nanoseconds.
     * <p>
     * This method allows an arbitrary number of nanoseconds to be passed in.
     * The factory will alter the values of the second and nanosecond in order
     * to ensure that the stored nanosecond is in the range 0 to 999,999,999.
     * For example, the following will result in the exactly the same duration:
     * <pre>
     *  Duration.ofSeconds(3, 1);
     *  Duration.ofSeconds(4, -999999999);
     *  Duration.ofSeconds(2, 1000000001);
     * </pre>
     *
     * @param seconds  the number of seconds, positive or negative
     * @param nanoAdjustment  the nanosecond adjustment to the number of seconds, positive or negative
     * @return a {@code Duration}, never null
     * @throws ArithmeticException if the adjustment causes the seconds to exceed the capacity of {@code Duration}
     */
    public static Duration ofSeconds(long seconds, long nanoAdjustment) {
        long secs = MathUtils.safeAdd(seconds, nanoAdjustment / NANOS_PER_SECOND);
        int nos = (int) (nanoAdjustment % NANOS_PER_SECOND);
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        return create(secs, nos);
    }

    /**
     * Obtains an instance of {@code Duration} from a number of seconds.
     * <p>
     * The seconds and nanoseconds are extracted from the specified {@code BigDecimal}.
     * If the decimal is larger than {@code Long.MAX_VALUE} or has more than 9 decimal
     * places then an exception is thrown.
     *
     * @param seconds  the number of seconds, up to scale 9, positive or negative
     * @return a {@code Duration}, never null
     * @throws ArithmeticException if the input seconds exceeds the capacity of a {@code Duration}
     */
    public static Duration ofSeconds(BigDecimal seconds) {
        Instant.checkNotNull(seconds, "Seconds must not be null");
        return ofNanos(seconds.movePointRight(9).toBigIntegerExact());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Duration} from a number of milliseconds.
     * <p>
     * The seconds and nanoseconds are extracted from the specified milliseconds.
     *
     * @param millis  the number of milliseconds, positive or negative
     * @return a {@code Duration}, never null
     */
    public static Duration ofMillis(long millis) {
        long secs = millis / 1000;
        int mos = (int) (millis % 1000);
        if (mos < 0) {
            mos += 1000;
            secs--;
        }
        return create(secs, mos * 1000000);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Duration} from a number of nanoseconds.
     * <p>
     * The seconds and nanoseconds are extracted from the specified nanoseconds.
     *
     * @param nanos  the number of nanoseconds, positive or negative
     * @return a {@code Duration}, never null
     */
    public static Duration ofNanos(long nanos) {
        long secs = nanos / NANOS_PER_SECOND;
        int nos = (int) (nanos % NANOS_PER_SECOND);
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs--;
        }
        return create(secs, nos);
    }

    /**
     * Obtains an instance of {@code Duration} from a number of nanoseconds.
     * <p>
     * The seconds and nanoseconds are extracted from the specified {@code BigInteger}.
     * If the resulting seconds value is larger than {@code Long.MAX_VALUE} then an
     * exception is thrown.
     *
     * @param nanos  the number of nanoseconds, positive or negative, not null
     * @return a {@code Duration}, never null
     * @throws ArithmeticException if the input nanoseconds exceeds the capacity of {@code Duration}
     */
    public static Duration ofNanos(BigInteger nanos) {
        Instant.checkNotNull(nanos, "Nanos must not be null");
        BigInteger[] divRem = nanos.divideAndRemainder(BI_NANOS_PER_SECOND);
        if (divRem[0].bitLength() > 63) {
            throw new ArithmeticException("Exceeds capacity of Duration: " + nanos);
        }
        return ofSeconds(divRem[0].longValue(), divRem[1].intValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Duration} from a number of standard length minutes.
     * <p>
     * The seconds are calculated based on the standard definition of a minute,
     * where each minute is 60 seconds.
     * The nanosecond in second field is set to zero.
     *
     * @param minutes  the number of minutes, positive or negative
     * @return a {@code Duration}, never null
     * @throws ArithmeticException if the input minutes exceeds the capacity of {@code Duration}
     */
    public static Duration ofStandardMinutes(long minutes) {
        return create(MathUtils.safeMultiply(minutes, 60), 0);
    }

    /**
     * Obtains an instance of {@code Duration} from a number of standard length hours.
     * <p>
     * The seconds are calculated based on the standard definition of an hour,
     * where each hour is 3600 seconds.
     * The nanosecond in second field is set to zero.
     *
     * @param hours  the number of hours, positive or negative
     * @return a {@code Duration}, never null
     * @throws ArithmeticException if the input hours exceeds the capacity of {@code Duration}
     */
    public static Duration ofStandardHours(long hours) {
        return create(MathUtils.safeMultiply(hours, 3600), 0);
    }

    /**
     * Obtains an instance of {@code Duration} from a number of standard length days.
     * <p>
     * The seconds are calculated based on the standard definition of a day,
     * where each day is 86400 seconds which implies a 24 hour day.
     * The nanosecond in second field is set to zero.
     *
     * @param days  the number of days, positive or negative
     * @return a {@code Duration}, never null
     * @throws ArithmeticException if the input days exceeds the capacity of {@code Duration}
     */
    public static Duration ofStandardDays(long days) {
        return create(MathUtils.safeMultiply(days, 86400), 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Duration} from a duration in a specified time unit.
     * <p>
     * The duration amount is measured in terms of the specified unit. For example:
     * <pre>
     *  Duration.of(3, TimeUnit.SECONDS);
     *  Duration.of(465, TimeUnit.MICROSECONDS);
     * </pre>
     *
     * @param amount  the amount of the duration, positive or negative
     * @param unit  the unit that the duration is measured in, not null
     * @return a {@code Duration}, never null
     * @throws ArithmeticException if the input amount exceeds the capacity of {@code Duration}
     *  which can only occur for units MINUTES, HOURS and DAYS
     */
    public static Duration of(long amount, TimeUnit unit) {
        Instant.checkNotNull(unit, "TimeUnit must not be null");
        long nanos = unit.toNanos(amount);
        if (unit == TimeUnit.NANOSECONDS ||
                (nanos > Long.MAX_VALUE && nanos < Long.MIN_VALUE)) {
            return ofNanos(nanos);
        }
        BigInteger calc = BigInteger.valueOf(amount);
        switch (unit) {
            case MICROSECONDS:
                return ofNanos(calc.multiply(BI_NANOS_PER_MICRO));
            case MILLISECONDS:
                return ofNanos(calc.multiply(BI_NANOS_PER_MILLI));
            case SECONDS:
                return ofNanos(calc.multiply(BI_NANOS_PER_SECOND));
            case MINUTES:
                return ofNanos(calc.multiply(BI_NANOS_PER_MINUTE));
            case HOURS:
                return ofNanos(calc.multiply(BI_NANOS_PER_HOUR));
            case DAYS:
                return ofNanos(calc.multiply(BI_NANOS_PER_DAY));
            default:
                throw new InternalError("Unreachable");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Duration} representing the duration between two instants.
     * <p>
     * A {@code Duration} represents a directed distance between two points on the time-line.
     * As such, this method will return a negative duration if the end is before the start.
     * To guarantee to obtain a positive duration call {@link #abs()} on the result of this factory.
     *
     * @param startInclusive  the start instant, inclusive, not null
     * @param endExclusive  the end instant, exclusive, not null
     * @return a {@code Duration}, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public static Duration between(InstantProvider startInclusive, InstantProvider endExclusive) {
        Instant start = Instant.of(startInclusive);
        Instant end = Instant.of(endExclusive);
        long secs = MathUtils.safeSubtract(end.getEpochSeconds(), start.getEpochSeconds());
        int nanos = end.getNanoOfSecond() - start.getNanoOfSecond();
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        return create(secs, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Duration} by parsing a string.
     * <p>
     * This will parse the string produced by {@link #toString()} which is
     * the ISO-8601 format {@code PTnS} where {@code n} is
     * the number of seconds with optional decimal part.
     * The number must consist of ASCII numerals.
     * There must only be a negative sign at the start of the number and it can
     * only be present if the value is less than zero.
     * There must be at least one digit before any decimal point.
     * There must be between 1 and 9 inclusive digits after any decimal point.
     * The letters (P, T and S) will be accepted in upper or lower case.
     * The decimal point may be either a dot or a comma.
     *
     * @param text  the text to parse, not null
     * @return a {@code Duration}, never null
     * @throws CalendricalParseException if the text cannot be parsed to a {@code Duration}
     */
    public static Duration parse(final String text) {
        Instant.checkNotNull(text, "Text to parse must not be null");
        int len = text.length();
        if (len < 4 ||
                (text.charAt(0) != 'P' && text.charAt(0) != 'p') ||
                (text.charAt(1) != 'T' && text.charAt(1) != 't') ||
                (text.charAt(len - 1) != 'S' && text.charAt(len - 1) != 's') ||
                (len == 5 && text.charAt(2) == '-' && text.charAt(3) == '0')) {
            throw new CalendricalParseException("Duration could not be parsed: " + text, text, 0);
        }
        String numberText = text.substring(2, len - 1).replace(',', '.');
        int dot = numberText.indexOf('.');
        try {
            if (dot == -1) {
                // no decimal places
                if (numberText.startsWith("-0")) {
                    throw new CalendricalParseException("Duration could not be parsed: " + text, text, 2);
                }
                return create(Long.parseLong(numberText), 0);
            }
            // decimal places
            boolean negative = false;
            if (numberText.charAt(0) == '-') {
                negative = true;
            }
            long secs = Long.parseLong(numberText.substring(0, dot));
            numberText = numberText.substring(dot + 1);
            len = numberText.length();
            if (len == 0 || len > 9 || numberText.charAt(0) == '-') {
                throw new CalendricalParseException("Duration could not be parsed: " + text, text, 2);
            }
            int nanos = Integer.parseInt(numberText);
            switch (len) {
                case 1:
                    nanos *= 100000000;
                    break;
                case 2:
                    nanos *= 10000000;
                    break;
                case 3:
                    nanos *= 1000000;
                    break;
                case 4:
                    nanos *= 100000;
                    break;
                case 5:
                    nanos *= 10000;
                    break;
                case 6:
                    nanos *= 1000;
                    break;
                case 7:
                    nanos *= 100;
                    break;
                case 8:
                    nanos *= 10;
                    break;
            }
            return negative ? ofSeconds(secs, -nanos) : create(secs, nanos);
            
        } catch (ArithmeticException ex) {
            throw new CalendricalParseException("Duration could not be parsed: " + text, text, 2, ex);
        } catch (NumberFormatException ex) {
            throw new CalendricalParseException("Duration could not be parsed: " + text, text, 2, ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Duration} using seconds and nanoseconds.
     *
     * @param seconds  the length of the duration in seconds, positive or negative
     * @param nanoAdjustment  the nanosecond adjustment within the second, from 0 to 999,999,999
     */
    private static Duration create(long seconds, int nanoAdjustment) {
        if ((seconds | nanoAdjustment) == 0) {
            return ZERO;
        }
        return new Duration(seconds, nanoAdjustment);
    }

    /**
     * Constructs an instance of {@code Duration} using seconds and nanoseconds.
     *
     * @param seconds  the length of the duration in seconds, positive or negative
     * @param nanos  the nanoseconds within the second, from 0 to 999,999,999
     */
    private Duration(long seconds, int nanos) {
        super();
        this.seconds = seconds;
        this.nanos = nanos;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance, never null
     */
    private Object readResolve() {
        return (seconds| nanos) == 0 ? ZERO : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this duration is zero length.
     * <p>
     * A {@code Duration} represents a directed distance between two points on
     * the time-line and can therefore be positive, zero or negative.
     * This method checks whether the length is zero.
     *
     * @return true if this duration has a total length equal to zero
     */
    public boolean isZero() {
        return (seconds | nanos) == 0;
    }

    /**
     * Checks if this duration is positive, excluding zero.
     * <p>
     * A {@code Duration} represents a directed distance between two points on
     * the time-line and can therefore be positive, zero or negative.
     * This method checks whether the length is greater than zero.
     *
     * @return true if this duration has a total length greater than zero
     */
    public boolean isPositive() {
        return seconds >= 0 && ((seconds | nanos) != 0);
    }

    /**
     * Checks if this duration is positive or zero.
     * <p>
     * A {@code Duration} represents a directed distance between two points on
     * the time-line and can therefore be positive, zero or negative.
     * This method checks whether the length is greater than or equal to zero.
     *
     * @return true if this duration has a total length greater than or equal zero
     */
    public boolean isPositiveOrZero() {
        return seconds >= 0;
    }

    /**
     * Checks if this duration is negative, excluding zero.
     * <p>
     * A {@code Duration} represents a directed distance between two points on
     * the time-line and can therefore be positive, zero or negative.
     * This method checks whether the length is less than zero.
     *
     * @return true if this duration has a total length less than zero
     */
    public boolean isNegative() {
        return seconds < 0;
    }

    /**
     * Checks if this duration is negative or zero.
     * <p>
     * A {@code Duration} represents a directed distance between two points on
     * the time-line and can therefore be positive, zero or negative.
     * This method checks whether the length is less than or equal to zero.
     *
     * @return true if this duration has a total length less than or equal to zero
     */
    public boolean isNegativeOrZero() {
        return seconds < 0 || ((seconds | nanos) == 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds in this duration.
     * <p>
     * The length of the duration is stored using two fields - seconds and nanoseconds.
     * The nanoseconds part is a value from 0 to 999,999,999 that is an adjustment to
     * the length in seconds.
     * The total duration is defined by calling this method and {@link #getNanoOfSecond()}.
     * <p>
     * A {@code Duration} represents a directed distance between two points on the time-line.
     * A negative duration is expressed by the negative sign of the seconds part.
     * A duration of -1 nanosecond is stored as -1 seconds plus 999,999,999 nanoseconds.
     *
     * @return the whole seconds part of the length of the duration, positive or negative
     */
    public long getSeconds() {
        return seconds;
    }

    /**
     * Gets the number of nanoseconds within the second in this duration.
     * <p>
     * The length of the duration is stored using two fields - seconds and nanoseconds.
     * The nanoseconds part is a value from 0 to 999,999,999 that is an adjustment to
     * the length in seconds.
     * The total duration is defined by calling this method and {@link #getSeconds()}.
     * <p>
     * A {@code Duration} represents a directed distance between two points on the time-line.
     * A negative duration is expressed by the negative sign of the seconds part.
     * A duration of -1 nanosecond is stored as -1 seconds plus 999,999,999 nanoseconds.
     *
     * @return the nanoseconds within the second part of the length of the duration, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the duration in terms of the specified unit.
     * <p>
     * This method returns the duration converted to the unit, truncating
     * excess precision.
     * If the conversion would overflow, the result will saturate to
     * {@code Long.MAX_VALUE} or {@code Long.MIN_VALUE}.
     *
     * @return the duration in the specified unit, saturated at {@code Long.MAX_VALUE}
     * and {@code Long.MIN_VALUE}, positive or negative
     */
    public long get(TimeUnit unit) {
        Instant.checkNotNull(unit, "TimeUnit must not be null");
        BigInteger nanos = toNanos();
        switch (unit) {
            case NANOSECONDS:
                break;
            case MICROSECONDS:
                nanos = nanos.divide(BI_NANOS_PER_MICRO);
                break;
            case MILLISECONDS:
                nanos = nanos.divide(BI_NANOS_PER_MILLI);
                break;
            case SECONDS:
                nanos = nanos.divide(BI_NANOS_PER_SECOND);
                break;
            case MINUTES:
                nanos = nanos.divide(BI_NANOS_PER_MINUTE);
                break;
            case HOURS:
                nanos = nanos.divide(BI_NANOS_PER_HOUR);
                break;
            case DAYS:
                nanos = nanos.divide(BI_NANOS_PER_DAY);
                break;
            default:
                throw new InternalError("Unreachable");
        }
        return nanos.min(BI_MAX_LONG).max(BI_MIN_LONG).longValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration with the specified {@code Duration} added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, positive or negative, not null
     * @return a {@code Duration} based on this duration with the specified duration added, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration plus(Duration duration) {
        long secsToAdd = duration.seconds;
        int nanosToAdd = duration.nanos;
        if (secsToAdd == 0 && nanosToAdd == 0) {
            return this;
        }
        long secs = MathUtils.safeAdd(seconds, secsToAdd);
        int nos = nanos + nanosToAdd;  // safe
        if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;
            secs = MathUtils.safeIncrement(secs);
        }
        return create(secs, nos);
     }

    /**
     * Returns a copy of this duration with the specified duration added.
     * <p>
     * The duration to be added is measured in terms of the specified unit.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the duration to add, positive or negative
     * @param unit  the unit that the duration is measured in, not null
     * @return a {@code Duration} based on this duration with the specified duration added, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration plus(long amount, TimeUnit unit) {
        if (unit == TimeUnit.SECONDS) {
            return plusSeconds(amount);
        } else if (unit == TimeUnit.MILLISECONDS) {
            return plusMillis(amount);
        } else if (unit == TimeUnit.NANOSECONDS) {
            return plusNanos(amount);
        }
        return plus(of(amount, unit));
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified seconds added, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        long secs = MathUtils.safeAdd(seconds, secondsToAdd);
        return create(secs, nanos);
    }

    /**
     * Returns a copy of this duration with the specified number of milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToAdd  the milliseconds to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified milliseconds added, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration plusMillis(long millisToAdd) {
        if (millisToAdd == 0) {
            return this;
        }
        long secondsToAdd = millisToAdd / 1000;
        // add: 0 to 999,000,000, subtract: 0 to -999,000,000
        int nos = ((int) (millisToAdd % 1000)) * 1000000;
        // add: 0 to 0 to 1998,999,999, subtract: -999,000,000 to 999,999,999
        nos += nanos;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;  // subtract: 1,000,000 to 999,999,999
            secondsToAdd--;
        } else if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;  // add: 1 to 998,999,999
            secondsToAdd++;
        }
        return create(MathUtils.safeAdd(seconds, secondsToAdd) , nos);
    }

    /**
     * Returns a copy of this duration with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToAdd  the nanoseconds to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified nanoseconds added, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        long secondsToAdd = nanosToAdd / NANOS_PER_SECOND;
        // add: 0 to 999,999,999, subtract: 0 to -999,999,999
        int nos = (int) (nanosToAdd % NANOS_PER_SECOND);
        // add: 0 to 0 to 1999,999,998, subtract: -999,999,999 to 999,999,999
        nos += nanos;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;  // subtract: 1 to 999,999,999
            secondsToAdd--;
        } else if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;  // add: 1 to 999,999,999
            secondsToAdd++;
        }
        return create(MathUtils.safeAdd(seconds, secondsToAdd) , nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration with the specified {@code Duration} subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, positive or negative, not null
     * @return a {@code Duration} based on this duration with the specified duration subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration minus(Duration duration) {
        long secsToSubtract = duration.seconds;
        int nanosToSubtract = duration.nanos;
        if (secsToSubtract == 0 && nanosToSubtract == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(seconds, secsToSubtract);
        int nos = nanos - nanosToSubtract;  // safe
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        return create(secs, nos);
     }

    /**
     * Returns a copy of this duration with the specified duration subtracted.
     * <p>
     * The duration to be subtracted is measured in terms of the specified unit.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the duration to subtract, positive or negative
     * @param unit  the unit that the duration is measured in, not null
     * @return a {@code Duration} based on this duration with the specified duration subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration minus(long amount, TimeUnit unit) {
        if (unit == TimeUnit.SECONDS) {
            return minusSeconds(amount);
        } else if (unit == TimeUnit.MILLISECONDS) {
            return minusMillis(amount);
        } else if (unit == TimeUnit.NANOSECONDS) {
            return minusNanos(amount);
        }
        return minus(of(amount, unit));
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToSubtract the seconds to subtract, positive or negative
     * @return a {@code Duration} based on this duration with the specified seconds subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(seconds, secondsToSubtract);
        return create(secs, nanos);
    }

    /**
     * Returns a copy of this duration with the specified number of milliseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToSubtract  the milliseconds to subtract, positive or negative
     * @return a {@code Duration} based on this duration with the specified milliseconds subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration minusMillis(long millisToSubtract) {
        if (millisToSubtract == 0) {
            return this;
        }
        long secondsToSubtract = millisToSubtract / 1000;
        int nos = ((int) (millisToSubtract % 1000)) * 1000000;
        nos = nanos - nos;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secondsToSubtract++;
        } else if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;
            secondsToSubtract--;
        }
        return create(MathUtils.safeSubtract(seconds, secondsToSubtract), nos);
    }

    /**
     * Returns a copy of this duration with the specified number of nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToSubtract  the nanoseconds to subtract, positive or negative
     * @return a {@code Duration} based on this duration with the specified nanoseconds subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration minusNanos(long nanosToSubtract) {
        if (nanosToSubtract == 0) {
            return this;
        }
        long secondsToSubtract = nanosToSubtract / NANOS_PER_SECOND;
        int nos = (int) (nanosToSubtract % NANOS_PER_SECOND);
        nos = nanos - nos;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secondsToSubtract++;
        } else if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;
            secondsToSubtract--;
        }
        return create(MathUtils.safeSubtract(seconds, secondsToSubtract), nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration multiplied by the scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param multiplicand  the value to multiply the duration by, positive or negative
     * @return a {@code Duration} based on this duration multiplied by the specified scalar, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration multipliedBy(long multiplicand) {
        if (multiplicand == 0) {
            return ZERO;
        }
        if (multiplicand == 1) {
            return this;
        }
        BigInteger nanos = toNanos();
        nanos = nanos.multiply(BigInteger.valueOf(multiplicand));
        BigInteger[] divRem = nanos.divideAndRemainder(BI_NANOS_PER_SECOND);
        if (divRem[0].bitLength() > 63) {
            throw new ArithmeticException("Multiplication result exceeds capacity of Duration: " + this + " * " + multiplicand);
        }
        return ofSeconds(divRem[0].longValue(), divRem[1].intValue());
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration divided by the specified value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the value to divide the duration by, positive or negative, not zero
     * @return a {@code Duration} based on this duration divided by the specified divisor, never null
     * @throws ArithmeticException if the divisor is zero
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public Duration dividedBy(long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        if (divisor == 1) {
            return this;
        }
        BigInteger nanos = toNanos();
        nanos = nanos.divide(BigInteger.valueOf(divisor));
        BigInteger[] divRem = nanos.divideAndRemainder(BI_NANOS_PER_SECOND);
        return ofSeconds(divRem[0].longValue(), divRem[1].intValue());
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration with the length negated.
     * <p>
     * This method swaps the sign of the total length of this duration.
     * For example, {@code PT1.3S} will be returned as {@code PT-1.3S}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code Duration} based on this period with the amount negated, never null
     * @throws ArithmeticException if the seconds part of the length is {@code Long.MIN_VALUE}
     */
    public Duration negated() {
        return multipliedBy(-1);
    }

    /**
     * Returns a copy of this duration with a positive length.
     * <p>
     * This method returns a positive duration by effectively removing the sign from any negative total length.
     * For example, {@code PT-1.3S} will be returned as {@code PT1.3S}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code Duration} based on this period with an absolute length, never null
     * @throws ArithmeticException if the seconds part of the length is {@code Long.MIN_VALUE}
     */
    public Duration abs() {
        return isNegative() ? negated() : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this duration to the total length in seconds and
     * fractional nanoseconds expressed as a {@code BigDecimal}.
     *
     * @return the total length of the duration in seconds, with a scale of 9, never null
     */
    public BigDecimal toSeconds() {
        return BigDecimal.valueOf(seconds).add(BigDecimal.valueOf(nanos, 9));
    }

    /**
     * Converts this duration to the total length in nanoseconds expressed as a {@code BigInteger}.
     *
     * @return the total length of the duration in nanoseconds, never null
     */
    public BigInteger toNanos() {
        return BigInteger.valueOf(seconds).multiply(BI_NANOS_PER_SECOND).add(BigInteger.valueOf(nanos));
    }

    /**
     * Converts this duration to the total length in nanoseconds expressed as a {@code long}.
     * <p>
     * If this duration is too large to fit in a {@code long} nanoseconds, then an
     * exception is thrown.
     *
     * @return the total length of the duration in nanoseconds
     * @throws ArithmeticException if the length exceeds the capacity of a {@code long}
     */
    public long toNanosLong() {
        long millis = MathUtils.safeMultiply(seconds, 1000000000);
        millis = MathUtils.safeAdd(millis, nanos);
        return millis;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this duration to the total length in milliseconds.
     * <p>
     * If this duration is too large to fit in a {@code long} milliseconds, then an
     * exception is thrown.
     * <p>
     * If this duration has greater than millisecond precision, then the conversion
     * will drop any excess precision information as though the amount in nanoseconds
     * was subject to integer division by one million.
     *
     * @return the total length of the duration in milliseconds
     * @throws ArithmeticException if the length exceeds the capacity of a {@code long}
     */
    public long toMillisLong() {
        long millis = MathUtils.safeMultiply(seconds, 1000);
        millis = MathUtils.safeAdd(millis, nanos / 1000000);
        return millis;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this duration to the specified {@code Duration}.
     * <p>
     * The comparison is based on the total length of the durations.
     *
     * @param otherDuration  the other duration to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(Duration otherDuration) {
        int cmp = MathUtils.safeCompare(seconds, otherDuration.seconds);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(nanos, otherDuration.nanos);
    }

    /**
     * Checks if this duration is greater than the specified {@code Duration}.
     * <p>
     * The comparison is based on the total length of the durations.
     *
     * @param otherDuration  the other duration to compare to, not null
     * @return true if this duration is greater than the specified duration
     */
    public boolean isGreaterThan(Duration otherDuration) {
        return compareTo(otherDuration) > 0;
    }

    /**
     * Checks if this duration is less than the specified {@code Duration}.
     * <p>
     * The comparison is based on the total length of the durations.
     *
     * @param otherDuration  the other duration to compare to, not null
     * @return true if this duration is less than the specified duration
     */
    public boolean isLessThan(Duration otherDuration) {
        return compareTo(otherDuration) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this duration is equal to the specified {@code Duration}.
     * <p>
     * The comparison is based on the total length of the durations.
     *
     * @param otherDuration  the other duration, null returns false
     * @return true if the other duration is equal to this one
     */
    @Override
    public boolean equals(Object otherDuration) {
        if (this == otherDuration) {
            return true;
        }
        if (otherDuration instanceof Duration) {
            Duration other = (Duration) otherDuration;
            return this.seconds == other.seconds &&
                   this.nanos == other.nanos;
        }
        return false;
    }

    /**
     * A hash code for this duration.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return ((int) (seconds ^ (seconds >>> 32))) + (51 * nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this duration using ISO-8601 seconds
     * based representation, such as {@code PT12.345S}.
     * <p>
     * The format of the returned string will be {@code PTnS} where n is
     * the seconds and fractional seconds of the duration.
     *
     * @return an ISO-8601 representation of this duration, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(24);
        buf.append("PT");
        if (seconds < 0 && nanos > 0) {
            if (seconds == -1) {
                buf.append("-0");
            } else {
                buf.append(seconds + 1);
            }
        } else {
            buf.append(seconds);
        }
        if (nanos > 0) {
            int pos = buf.length();
            if (seconds < 0) {
                buf.append(2 * NANOS_PER_SECOND - nanos);
            } else {
                buf.append(nanos + NANOS_PER_SECOND);
            }
            while (buf.charAt(buf.length() - 1) == '0') {
                buf.setLength(buf.length() - 1);
            }
            buf.setCharAt(pos, '.');
        }
        buf.append('S');
        return buf.toString();
    }

}
