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

import javax.time.calendar.CalendarConversionException;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;

/**
 * An instantaneous point on the time-line.
 * <p>
 * The Java Time Framework models time as a series of instantaneous events,
 * known as instants, along a single time-line. This class represents one
 * of those instants.
 * <p>
 * A physical instant is an instantaneous event.
 * However, for practicality the API and this class uses a precision of nanoseconds.
 * <p>
 * A physical instant could be at any point on an infinite time-line.
 * However, for practicality the API and this class limits the measurable time-line
 * to the number of seconds that can be held in a <code>long</code>. This is greater
 * than the current estimated age of the universe.
 * <p>
 * In order to represent the data a 96 bit number is required. To achieve this the
 * data is stored as seconds, measured using a <code>long</code>, and nanoseconds,
 * measured using an <code>int</code>. The nanosecond part will always be between
 * 0 and 999,999,999 representing the nanosecond part of the second.
 * The seconds are measured from the standard Java epoch of 1970-01-01T00:00:00Z.
 * <p>
 * This class uses the {@link TimeScales#utc() simplified UTC} time scale.
 * The scale keeps in step with true UTC by simply ignoring leap seconds.
 * <p>
 * This scale has been chosen as the default because it is simple to understand
 * and is what most users of the API expect. If the application needs an accurate
 * time scale that is aware of leap seconds then {@link TimeScaleInstant} should
 * be used.
 * <p>
 * Instant is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class Instant
        implements InstantProvider, Comparable<Instant>, Serializable {

    /**
     * Constant for the 1970-01-01T00:00:00Z epoch instant.
     */
    public static final Instant EPOCH = new Instant(0, 0);
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * BigInteger constant for a billion.
     */
    static final BigInteger BILLION = BigInteger.valueOf(1000000000);
    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;

    /**
     * The number of seconds from the epoch of 1970-01-01T00:00:00Z.
     */
    private final long seconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanos;

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
     * Obtains an instance of <code>Instant</code> from a provider of instants.
     * <p>
     * In addition to calling {@link InstantProvider#toInstant()} this method
     * also checks the validity of the result of the provider.
     *
     * @param instantProvider  a provider of instant information, not null
     * @return the created instant, never null
     */
    public static Instant instant(InstantProvider instantProvider) {
        checkNotNull(instantProvider, "InstantProvider must not be null");
        Instant provided = instantProvider.toInstant();
        checkNotNull(provided, "The implementation of InstantProvider must not return null");
        return provided;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Instant</code> using seconds from the
     * epoch of 1970-01-01T00:00:00Z with a zero nanosecond fraction.
     *
     * @param seconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @return the created Instant, never null
     */
    public static Instant seconds(long seconds) {
        return create(seconds, 0);
    }

    /**
     * Obtains an instance of <code>Instant</code> using seconds from the
     * epoch of 1970-01-01T00:00:00Z and nanosecond fraction of second.
     * <p>
     * This methods allows an arbitrary number of nanoseconds to be passed in.
     * The factory will alter the values of the second and nanosecond in order
     * to ensure that the stored nanosecond is in the range 0 to 999,999,999.
     * For example, the following will result in the exactly the same duration:
     * <pre>
     *  Instant.duration(3, 1);
     *  Instant.duration(4, -999999999);
     *  Instant.duration(2, 1000000001);
     * </pre>
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoAdjustment  the nanosecond adjustment to the number of seconds, positive or negative
     * @return the created Instant, never null
     * @throws ArithmeticException if the adjustment causes the seconds to exceed the capacity of Instant
     */
    public static Instant seconds(long epochSeconds, long nanoAdjustment) {
        long secs = MathUtils.safeAdd(epochSeconds, nanoAdjustment / NANOS_PER_SECOND);
        int nos = (int) (nanoAdjustment % NANOS_PER_SECOND);
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        return create(secs, nos);
    }

    /**
     * Obtains an instance of <code>Instant</code> using seconds from the
     * epoch of 1970-01-01T00:00:00Z with up to 9 fractional digits.
     *
     * @param seconds  the number of seconds
     * @return the created Duration, never null
     * @throws ArithmeticException if the input seconds exceeds the capacity of a duration
     */
    public static Instant seconds(BigDecimal seconds) {
        checkNotNull(seconds, "Seconds must not be null");
        return nanos(seconds.movePointRight(9).toBigIntegerExact());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Instant</code> using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z with no further fraction of a second.
     *
     * @param millis  the number of milliseconds
     * @return the created Duration, never null
     */
    public static Instant millis(long millis) {
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
     * Obtains an instance of <code>Instant</code> using nanoseconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * This factory will split the supplied nanosecond amount to ensure that the
     * stored nanosecond is in the range 0 to 999,999,999.
     *
     * @param nanos  the number of nanoseconds
     * @return the created Duration, never null
     */
    public static Instant nanos(long nanos) {
        long secs = nanos / NANOS_PER_SECOND;
        int nos = (int) (nanos % NANOS_PER_SECOND);
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs--;
        }
        return create(secs, nos);
    }

    /**
     * Obtains an instance of <code>Instant</code> using nanoseconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * This factory will split the supplied nanosecond amount to ensure that the
     * stored nanosecond is in the range 0 to 999,999,999.
     *
     * @param nanos  the number of nanoseconds, not null
     * @return the created Duration, never null
     * @throws ArithmeticException if the input nanoseconds exceeds the capacity of Duration
     */
    public static Instant nanos(BigInteger nanos) {
        checkNotNull(nanos, "Nanos must not be null");
        BigInteger[] divRem = nanos.divideAndRemainder(BILLION);
        if (divRem[0].bitLength() > 63) {
            throw new ArithmeticException("Exceeds capacity of Duration: " + nanos);
        }
        return seconds(divRem[0].longValue(), divRem[1].intValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance of Duration using seconds and nanoseconds.
     *
     * @param seconds  the length of the duration in seconds
     * @param nanoAdjustment  the nanosecond adjustment within the second, from 0 to 999,999,999
     */
    private static Instant create(long seconds, int nanoAdjustment) {
        if ((seconds | nanoAdjustment) == 0) {
            return EPOCH;
        }
        return new Instant(seconds, nanoAdjustment);
    }

    /**
     * Constructs an instance of Instant using seconds from the epoch of
     * 1970-01-01T00:00:00Z and nanosecond fraction of second.
     *
     * @param epochSeconds  the number of seconds from the epoch
     * @param nanoOfSecond  the nanoseconds within the second, must be positive
     */
    private Instant(long epochSeconds, int nanoOfSecond) {
        super();
        this.seconds = epochSeconds;
        this.nanos = nanoOfSecond;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        return (seconds | nanos) == 0 ? EPOCH : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds from the epoch of 1970-01-01T00:00:00Z.
     * <p>
     * Instants on the time-line after the epoch are positive, earlier are negative.
     *
     * @return the seconds from the epoch
     * @see #getNanoOfSecond()
     */
    public long getEpochSeconds() {
        return seconds;
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start
     * of the second returned by {@link #getEpochSeconds()}.
     *
     * @return the nanoseconds within the second, always positive, never exceeds 999,999,999
     * @see #getEpochSeconds()
     */
    public int getNanoOfSecond() {
        return nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant plus(Duration duration) {
        long secsToAdd = duration.getSeconds();
        int nanosToAdd = duration.getNanosInSecond();
        if (secsToAdd == 0 && nanosToAdd == 0) {
            return this;
        }

        long secs = MathUtils.safeAdd(seconds, secsToAdd);
        int nos = nanos + nanosToAdd;

        if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;
            secs = MathUtils.safeIncrement(secs);
        }

        return create(secs, nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        long secs = MathUtils.safeAdd(seconds, secondsToAdd);
        return create(secs , nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified number of milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToAdd  the milliseconds to add
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant plusMillis(long millisToAdd) {
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

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToAdd  the nanoseconds to add
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant plusNanos(long nanosToAdd) {
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
     * Returns a copy of this Instant with the specified duration subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, not null
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant minus(Duration duration) {
        long secsToSubtract = duration.getSeconds();
        int nanosToSubtract = duration.getNanosInSecond();
        if (secsToSubtract == 0 && nanosToSubtract == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(seconds, secsToSubtract);
        int nos = nanos - nanosToSubtract;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        return create(secs, nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToSubtract  the seconds to subtract
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(seconds, secondsToSubtract);
        return create(secs , nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified number of milliseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToSubtract  the milliseconds to subtract
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant minusMillis(long millisToSubtract) {
        if (millisToSubtract == 0) {
            return this;
        }

        long secondsToSubtract = millisToSubtract / 1000;
        // add: 0 to 999,000,000, subtract: 0 to -999,000,000
        int nos = ((int) (millisToSubtract % 1000)) * 1000000;
        // add: 0 to 0 to 1998,999,999, subtract: -999,000,000 to 999,999,999
        nos = nanos - nos;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;  // subtract: 1,000,000 to 999,999,999
            secondsToSubtract++;
        } else if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;  // add: 1 to 998,999,999
            secondsToSubtract--;
        }
        return create(MathUtils.safeSubtract(seconds, secondsToSubtract) , nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified number of nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToSubtract  the nanoseconds to subtract
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant minusNanos(long nanosToSubtract) {
        if (nanosToSubtract == 0) {
            return this;
        }

        long secondsToSubtract = nanosToSubtract / NANOS_PER_SECOND;
        // add: 0 to 999,999,999, subtract: 0 to -999,999,999
        int nos = (int) (nanosToSubtract % NANOS_PER_SECOND);
        // add: 0 to 0 to 1999,999,998, subtract: -999,999,999 to 999,999,999
        nos = nanos - nos;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;  // subtract: 1 to 999,999,999
            secondsToSubtract++;
        } else if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;  // add: 1 to 999,999,999
            secondsToSubtract--;
        }
        return create(MathUtils.safeSubtract(seconds, secondsToSubtract) , nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to the number of seconds from the epoch
     * of 1970-01-01T00:00:00Z expressed as a <code>BigDecimal</code>.
     *
     * @return the number of seconds since the epoch of 1970-01-01T00:00:00Z
     */
    public BigDecimal toEpochSeconds() {
        return BigDecimal.valueOf(seconds).add(BigDecimal.valueOf(nanos, 9));
    }

    /**
     * Converts this instant to the number of nanoseconds from the epoch
     * of 1970-01-01T00:00:00Z expressed as a <code>BigInteger</code>.
     *
     * @return the number of nanoseconds since the epoch of 1970-01-01T00:00:00Z
     */
    public BigInteger toEpochNanos() {
        return BigInteger.valueOf(seconds).multiply(BILLION).add(BigInteger.valueOf(nanos));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to the number of milliseconds from the epoch
     * of 1970-01-01T00:00:00Z.
     * <p>
     * <code>Instant</code> uses a precision of nanoseconds.
     * The conversion will drop any excess precision information as though the
     * amount in nanoseconds was subject to integer division by one million.
     * <p>
     * <code>Instant</code> can store points on the time-line further in the
     * future and further in the past than can be represented by a millisecond
     * value. In this scenario, this method will throw an exception.
     *
     * @return the number of milliseconds since the epoch of 1970-01-01T00:00:00Z
     * @throws CalendarConversionException if the instant is too large or too
     *  small to represent as epoch milliseconds
     */
    public long toEpochMillisLong() {
        try {
            long millis = MathUtils.safeMultiply(seconds, 1000);
            return millis + nanos / 1000000;
        } catch (ArithmeticException ex) {
            // TODO: is an exception conversion necessary
            throw new CalendarConversionException("The Instant cannot be represented as epoch milliseconds");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to an <code>Instant</code>, trivially
     * returning <code>this</code>.
     *
     * @return <code>this</code>, never null
     */
    public Instant toInstant() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this Instant to another.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherInstant is null
     */
    public int compareTo(Instant otherInstant) {
        int cmp = MathUtils.safeCompare(seconds, otherInstant.seconds);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(nanos, otherInstant.nanos);
    }

    /**
     * Is this Instant after the specified one.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return true if this instant is after the specified instant
     * @throws NullPointerException if otherInstant is null
     */
    public boolean isAfter(Instant otherInstant) {
        return compareTo(otherInstant) > 0;
    }

    /**
     * Is this Instant before the specified one.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return true if this instant is before the specified instant
     * @throws NullPointerException if otherInstant is null
     */
    public boolean isBefore(Instant otherInstant) {
        return compareTo(otherInstant) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this Instant equal to that specified.
     *
     * @param otherInstant  the other instant, null returns false
     * @return true if the other instant is equal to this one
     */
    @Override
    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (otherInstant instanceof Instant) {
            Instant other = (Instant) otherInstant;
            return this.seconds == other.seconds &&
                   this.nanos == other.nanos;
        }
        return false;
    }

    /**
     * A hash code for this Instant.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return ((int) (seconds ^ (seconds >>> 32))) + 51 * nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this Instant using ISO-8601 representation.
     * <p>
     * The format of the returned string will be <code>yyyy-MM-ddTHH:mm:ss.SSSSSSSSSZ</code>.
     *
     * @return an ISO-8601 representation of this Instant
     */
    @Override
    public String toString() {
        // TODO: optimize and handle big instants
        return OffsetDateTime.fromInstant(this, ZoneOffset.UTC).toLocalDateTime().toString() + 'Z';
    }

}
