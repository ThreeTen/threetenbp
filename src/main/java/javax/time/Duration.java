/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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
import java.math.BigInteger;

/**
 * A duration between two instants on the time-line.
 * <p>
 * The Java Time Framework models time as a series of instantaneous events,
 * known as instants, along a single time-line. This class represents the
 * duration between two of those instants.
 * <p>
 * Each instant is theoretically an instantaneous event, however for practicality
 * a precision of nanoseconds has been chosen. As a result, the duration class also
 * has a maximum precision of nanoseconds.
 * <p>
 * Duration is immutable and thread-safe.
 * <p>
 * NOTE: Since the number of nanoseconds is represented by a positive integer,
 * negative durations such as <code>PT-0.1S</code> are represented as -1 second
 * and 900,000,000 nanoseconds.
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
     * BigInteger constant for a billion.
     */
    private static final BigInteger BILLION = BigInteger.valueOf(1000000000);
//    /**
//     * BigInteger constant for a million.
//     */
//    private static final BigInteger MILLION = BigInteger.valueOf(1000000);
    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;
    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = -835275378278L;

    /**
     * The number of seconds in the duration.
     */
    private final long seconds;
    /**
     * The number of nanoseconds in the duration, expressed as a fraction of the
     * number of seconds. This is always positive, and never exceeds 999,999,999.
     */
    private final int nanoAdjustment;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Duration</code> from a number of seconds.
     *
     * @param seconds  the number of seconds
     * @return the created Duration, never null
     */
    public static Duration seconds(long seconds) {
        if (seconds == 0) {
            return ZERO;
        }
        return new Duration(seconds, 0);
    }

    /**
     * Obtains an instance of <code>Duration</code> from a number of seconds
     * and an adjustment in nanoseconds.
     * <p>
     * This methods allows an arbitrary number of nanoseconds to be passed in.
     * If the nanoseconds is not in the range 0 to 999,999,999 then both the
     * seconds and nanoseconds will be adjusted to fit.
     * Thus, the following are equivalent:
     * <pre>
     *  Duration.duration(3, 1);
     *  Duration.duration(4, -999999999);
     *  Duration.duration(2, 1000000001);
     * </pre>
     *
     * @param seconds  the number of seconds
     * @param nanoAdjustment  the nanosecond adjustment to the number of seconds, positive or negative
     * @return the created Duration, never null
     * @throws ArithmeticException if the adjustment causes the seconds to exceed the capacity of Duration
     */
    public static Duration seconds(long seconds, long nanoAdjustment) {
        if (seconds == 0 && nanoAdjustment == 0) {
            return ZERO;
        }
        long secs = MathUtils.safeAdd(seconds, nanoAdjustment / NANOS_PER_SECOND);
        int nos = (int) (nanoAdjustment % NANOS_PER_SECOND);
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        return new Duration(secs, nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Duration</code> from a number of milliseconds.
     *
     * @param millis  the number of milliseconds
     * @return the created Duration, never null
     */
    public static Duration millis(long millis) {
        if (millis == 0) {
            return ZERO;
        }
        long secs = millis / 1000;
        int mos = (int) (millis % 1000);
        if (mos < 0) {
            mos += 1000;
            secs--;
        }
        return new Duration(secs, mos * 1000000);
    }

    /**
     * Obtains an instance of <code>Duration</code> from a number of milliseconds
     * and an adjustment in nanoseconds.
     * <p>
     * This methods allows an arbitrary number of nanoseconds to be passed in.
     * If the nanoseconds is not in the range 0 to 999,999,999 then both the
     * milliseconds and nanoseconds will be adjusted to fit.
     * Thus, the following are equivalent:
     * <pre>
     *  Duration.duration(3, 1);
     *  Duration.duration(4, -999999);
     *  Duration.duration(2, 1000001);
     * </pre>
     *
     * @param millis  the number of milliseconds
     * @param nanoAdjustment  the nanosecond adjustment to the number of milliseconds, positive or negative
     * @return the created Duration, never null
     */
    public static Duration millis(long millis, long nanoAdjustment) {
        if (millis == 0 && nanoAdjustment == 0) {
            return ZERO;
        }
        long secs = (millis / 1000) + (nanoAdjustment / NANOS_PER_SECOND);
        long nanos = ((millis % 1000) * 1000000) + (nanoAdjustment % NANOS_PER_SECOND);
        secs += nanos / NANOS_PER_SECOND;
        int nos = (int) (nanos % NANOS_PER_SECOND);
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs--;
        }
        return new Duration(secs, nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Duration</code> from a number of nanoseconds.
     *
     * @param nanos  the number of nanoseconds
     * @return the created Duration, never null
     */
    public static Duration nanos(long nanos) {
        if (nanos == 0) {
            return ZERO;
        }
        long secs = nanos / NANOS_PER_SECOND;
        int nos = (int) (nanos % NANOS_PER_SECOND);
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs--;
        }
        return new Duration(secs, nos);
    }

    /**
     * Obtains an instance of <code>Duration</code> from a number of nanoseconds.
     *
     * @param nanos  the number of nanoseconds, not null
     * @return the created Duration, never null
     * @throws ArithmeticException if the input nanoseconds exceeds the capacity of Duration
     */
    public static Duration nanos(BigInteger nanos) {
        Instant.checkNotNull(nanos, "Nanos must not be null");
        if (nanos.equals(BigInteger.ZERO)) {
            return ZERO;
        }
        BigInteger[] divRem = nanos.divideAndRemainder(BILLION);
        if (divRem[0].bitLength() > 63) {
            throw new ArithmeticException("Exceeds capacity of Duration: " + nanos);
        }
        return seconds(divRem[0].longValue(), divRem[1].intValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Duration</code> from a number of standard length minutes.
     * <p>
     * This factory uses the standard definition of a minute.
     * Specifically, this means that there are 60 seconds in a minute.
     *
     * @param minutes  the number of minutes
     * @return the created Duration, never null
     * @throws ArithmeticException if the input minutes exceeds the capacity of Duration
     */
    public static Duration standardMinutes(long minutes) {
        if (minutes == 0) {
            return ZERO;
        }
        return new Duration(MathUtils.safeMultiply(minutes, 60), 0);
    }

    /**
     * Obtains an instance of <code>Duration</code> from a number of standard length hours.
     * <p>
     * This factory uses the standard definition of an hour.
     * Specifically, this means that there are 3600 seconds in an hour.
     *
     * @param hours  the number of hours
     * @return the created Duration, never null
     * @throws ArithmeticException if the input hours exceeds the capacity of Duration
     */
    public static Duration standardHours(long hours) {
        if (hours == 0) {
            return ZERO;
        }
        return new Duration(MathUtils.safeMultiply(hours, 3600), 0);
    }

    /**
     * Obtains an instance of <code>Duration</code> from a number of standard length days.
     * <p>
     * This factory uses the standard definition of a day.
     * Specifically, this means that there are 86400 seconds in a day,
     * which implies a 24 hour day.
     *
     * @param days  the number of days
     * @return the created Duration, never null
     * @throws ArithmeticException if the input days exceeds the capacity of Duration
     */
    public static Duration standardDays(long days) {
        if (days == 0) {
            return ZERO;
        }
        return new Duration(MathUtils.safeMultiply(days, 86400), 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Duration</code> representing the duration between two instants.
     * This method will return a negative duration if the end is before the start.
     *
     * @param startInclusive  the start instant, inclusive, not null
     * @param endExclusive  the end instant, exclusive, not null
     * @return the created Duration, never null
     * @throws ArithmeticException if the duration exceeds the capacity of Duration
     */
    public static Duration durationBetween(InstantProvider startInclusive, InstantProvider endExclusive) {
        Instant start = Instant.instant(startInclusive);
        Instant end = Instant.instant(endExclusive);
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
     * Obtains an instance of <code>Duration</code> from a string.
     * <p>
     * This will parse the string produced by <code>toString()</code> which is
     * the ISO8601 format <code>PTsecsS</code> where <code>secs</code> is
     * the number of seconds with optional decimal part.
     * The number must consist of ASCII numerals.
     * There must only be a negative sign at the start of the number and it can
     * only be present if the value is less then zero.
     * There must be at least one digit before any decimal point.
     * There must be between 1 and 9 inclusive digits after any decimal point.
     * The letters (P, T and S) will be accepted in upper or lower case.
     * The decimal point may be either a dot or a comma.
     *
     * @param text  the text to parse, not null
     * @return the created Duration, never null
     * @throws IllegalArgumentException if the text cannot be parsed to a Duration
     */
    public static Duration parse(final String text) {
        Instant.checkNotNull(text, "String must not be null");
        int len = text.length();
        if (len < 4 ||
                (text.charAt(0) != 'P' && text.charAt(0) != 'p') ||
                (text.charAt(1) != 'T' && text.charAt(1) != 't') ||
                (text.charAt(len - 1) != 'S' && text.charAt(len - 1) != 's') ||
                (len == 5 && text.charAt(2) == '-' && text.charAt(3) == '0')) {
            throw new IllegalArgumentException("Text '" + text + "' cannot be parsed as a Duration");
        }
        String numberText = text.substring(2, len - 1).replace(',', '.');
        int dot = numberText.indexOf('.');
        try {
            if (dot == -1) {
                return create(Long.parseLong(numberText), 0);
            }
            boolean negative = false;
            if (numberText.charAt(0) == '-') {
                negative = true;
            }
            long secs = Long.parseLong(numberText.substring(0, dot));
            numberText = numberText.substring(dot + 1);
            len = numberText.length();
            if (len == 0 || len > 9 || numberText.charAt(0) == '-') {
                throw new IllegalArgumentException("Text '" + numberText + "' cannot be parsed as a Duration");
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
            return negative ? seconds(secs, -nanos) : create(secs, nanos);
            
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Text '" + text + "' cannot be parsed as a Duration", ex);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Text '" + text + "' cannot be parsed as a Duration", ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance of Duration using seconds and nanoseconds.
     *
     * @param seconds  the length of the duration in seconds
     * @param nanoAdjustment  the nanosecond adjustment within the second, from 0 to 999,999,999
     */
    private static Duration create(long seconds, int nanoAdjustment) {
        if (seconds == 0 && nanoAdjustment == 0) {
            return ZERO;
        }
        return new Duration(seconds, nanoAdjustment);
    }

    /**
     * Constructs an instance of Duration using seconds and nanoseconds.
     *
     * @param seconds  the length of the duration in seconds
     * @param nanoAdjustment  the nanosecond adjustment within the second, from 0 to 999,999,999
     */
    private Duration(long seconds, int nanoAdjustment) {
        super();
        this.seconds = seconds;
        this.nanoAdjustment = nanoAdjustment;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        if (seconds == 0 && nanoAdjustment == 0) {
            return ZERO;
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds in this duration.
     * <p>
     * The length of the duration is expressed using two fields - seconds and
     * nanoseconds. The nanoseconds is held as a value from 0 to 999,999,999
     * and is an adjustment to the length.
     * <p>
     * A duration can be negative, and this is expressed by the negative sign
     * of the value returned from this method. A duration of -1 nanosecond is
     * stored as -1 seconds plus 999,999,999 nanoseconds.
     *
     * @return the length of the duration in seconds
     */
    public long getSeconds() {
        return seconds;
    }

    /**
     * Gets the number of nanoseconds within the second in this duration.
     * <p>
     * The length of the duration is expressed using two fields - seconds and
     * nanoseconds. The nanoseconds is held as a value from 0 to 999,999,999
     * and is an adjustment to the length.
     * <p>
     * A duration can be negative, and this is expressed by the negative sign
     * of the value returned from {@link #getSeconds()}. A duration of
     * -1 nanosecond is stored as -1 seconds plus 999,999,999 nanoseconds.
     *
     * @return the nanosecond adjustment to the second, from 0 to 999,999,999
     */
    public int getNanosAdjustment() {
        return nanoAdjustment;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration plus(Duration duration) {
        long secsToAdd = duration.seconds;
        int nanosToAdd = duration.nanoAdjustment;
        if (secsToAdd == 0 && nanosToAdd == 0) {
            return this;
        }
        long secs = MathUtils.safeAdd(seconds, secsToAdd);
        int nos = nanoAdjustment + nanosToAdd;  // safe
        if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;
            secs = MathUtils.safeIncrement(secs);
        }
        return create(secs, nos);
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        long secs = MathUtils.safeAdd(seconds, secondsToAdd);
        return create(secs, nanoAdjustment);
    }

    /**
     * Returns a copy of this Duration with the specified number of milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToAdd  the milliseconds to add
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration plusMillis(long millisToAdd) {
        if (millisToAdd == 0) {
            return this;
        }
        long secondsToAdd = millisToAdd / 1000;
        // add: 0 to 999,000,000, subtract: 0 to -999,000,000
        int nos = ((int) (millisToAdd % 1000)) * 1000000;
        // add: 0 to 0 to 1998,999,999, subtract: -999,000,000 to 999,999,999
        nos += nanoAdjustment;
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
     * Returns a copy of this Duration with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToAdd  the nanoseconds to add
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        long secondsToAdd = nanosToAdd / NANOS_PER_SECOND;
        // add: 0 to 999,999,999, subtract: 0 to -999,999,999
        int nos = (int) (nanosToAdd % NANOS_PER_SECOND);
        // add: 0 to 0 to 1999,999,998, subtract: -999,999,999 to 999,999,999
        nos += nanoAdjustment;
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
     * Returns a copy of this Duration with the specified duration subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration minus(Duration duration) {
        long secsToSubtract = duration.seconds;
        int nanosToSubtract = duration.nanoAdjustment;
        if (secsToSubtract == 0 && nanosToSubtract == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(seconds, secsToSubtract);
        int nos = nanoAdjustment - nanosToSubtract;  // safe
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        return create(secs, nos);
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToSubtract the seconds to subtract
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(seconds, secondsToSubtract);
        return create(secs, nanoAdjustment);
    }

    /**
     * Returns a copy of this Duration with the specified number of milliseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToSubtract  the milliseconds to subtract
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration minusMillis(long millisToSubtract) {
        if (millisToSubtract == 0) {
            return this;
        }
        long secondsToSubtract = millisToSubtract / 1000;
        int nos = ((int) (millisToSubtract % 1000)) * 1000000;
        nos = nanoAdjustment - nos;
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
     * Returns a copy of this Duration with the specified number of nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToSubtract  the nanoseconds to subtract
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration minusNanos(long nanosToSubtract) {
        if (nanosToSubtract == 0) {
            return this;
        }
        long secondsToSubtract = nanosToSubtract / NANOS_PER_SECOND;
        int nos = (int) (nanosToSubtract % NANOS_PER_SECOND);
        nos = nanoAdjustment - nos;
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
     * Returns a copy of this Duration multiplied by the scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param multiplicand  the value to multiply the duration by
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration multipliedBy(long multiplicand) {
        if (multiplicand == 0) {
            return ZERO;
        }
        if (multiplicand == 1) {
            return this;
        }
        BigInteger nanos = toNanosBigInteger();
        nanos = nanos.multiply(BigInteger.valueOf(multiplicand));
        BigInteger[] divRem = nanos.divideAndRemainder(BILLION);
        if (divRem[0].bitLength() > 63) {
            throw new ArithmeticException("Multiplication result exceeds capacity of Duration: " + this + " * " + multiplicand);
        }
        return seconds(divRem[0].longValue(), divRem[1].intValue());
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration divided by the specified value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the value to divide the duration by
     * @return a new updated Duration, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Duration dividedBy(long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        if (divisor == 1) {
            return this;
        }
        BigInteger nanos = toNanosBigInteger();
        nanos = nanos.divide(BigInteger.valueOf(divisor));
        BigInteger[] divRem = nanos.divideAndRemainder(BILLION);
        return seconds(divRem[0].longValue(), divRem[1].intValue());
     }

    //-----------------------------------------------------------------------
    /**
     * Compares this Duration to another.
     *
     * @param otherDuration  the other duration to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherDuration is null
     */
    public int compareTo(Duration otherDuration) {
        int cmp = MathUtils.safeCompare(seconds, otherDuration.seconds);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(nanoAdjustment, otherDuration.nanoAdjustment);
    }

    /**
     * Is this Duration longer than the specified one.
     *
     * @param otherDuration  the other duration to compare to, not null
     * @return true if this duration is longer than the specified duration
     * @throws NullPointerException if otherDuration is null
     */
    public boolean isLongerThan(Duration otherDuration) {
        return compareTo(otherDuration) > 0;
    }

    /**
     * Is this Duration shorter than the specified one.
     *
     * @param otherDuration  the other duration to compare to, not null
     * @return true if this duration is shorter than the specified duration
     * @throws NullPointerException if otherDuration is null
     */
    public boolean isShorterThan(Duration otherDuration) {
        return compareTo(otherDuration) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the length of this duration in milliseconds.
     * <p>
     * If the duration is too large to fit in a long milliseconds, then an
     * exception is thrown.
     * If the duration has a length in nanoseconds that is not a whole number
     * of milliseconds, then the remainder is simply dropped.
     *
     * @return the length of the duration in milliseconds
     * @throws ArithmeticException if the length exceeds the capacity of a long
     */
    public long toMillis() {
        long millis = MathUtils.safeMultiply(seconds, 1000);
        millis = MathUtils.safeAdd(millis, nanoAdjustment / 1000000);
        return millis;
    }

//    /**
//     * Returns the length of this duration in milliseconds expressed as a <code>BigInteger</code>.
//     *
//     * @return the length of the duration in milliseconds
//     */
//    public BigInteger toMillisBigInteger() {
//        return toNanosBigInteger().divide(MILLION);
//    }

    /**
     * Returns the length of this duration in nanoseconds.
     * <p>
     * If the duration is too large to fit in a long nanoseconds, then an
     * exception is thrown.
     *
     * @return the length of the duration in nanoseconds
     * @throws ArithmeticException if the length exceeds the capacity of a long
     */
    public long toNanos() {
        long millis = MathUtils.safeMultiply(seconds, 1000000000);
        millis = MathUtils.safeAdd(millis, nanoAdjustment);
        return millis;
    }

    /**
     * Returns the length of this duration in nanoseconds expressed as a <code>BigInteger</code>.
     *
     * @return the length of the duration in nanoseconds
     */
    public BigInteger toNanosBigInteger() {
        return BigInteger.valueOf(seconds).multiply(BILLION).add(BigInteger.valueOf(nanoAdjustment));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this Duration equal to that specified.
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
                   this.nanoAdjustment == other.nanoAdjustment;
        }
        return false;
    }

    /**
     * A hash code for this Duration.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return ((int) (seconds ^ (seconds >>> 32))) + (51 * nanoAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this Duration using ISO-8601 seconds based
     * representation.
     * <p>
     * The format of the returned string will be <code>PTnS</code> where n is
     * the seconds and fractional seconds of the duration.
     *
     * @return an ISO-8601 representation of this Duration
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(24);
        buf.append("PT");
        if (seconds < 0 && nanoAdjustment > 0) {
            if (seconds == -1) {
                buf.append("-0");
            } else {
                buf.append(seconds + 1);
            }
        } else {
            buf.append(seconds);
        }
        if (nanoAdjustment > 0) {
            int pos = buf.length();
            if (seconds < 0) {
                buf.append(2 * NANOS_PER_SECOND - nanoAdjustment);
            } else {
                buf.append(nanoAdjustment + NANOS_PER_SECOND);
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
