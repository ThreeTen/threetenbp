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

import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.scales.TimeScaleInstant;
import javax.time.scales.TimeScales;

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
 * to the number of seconds that can be held in a {@code long}. This is greater
 * than the current estimated age of the universe.
 * <p>
 * In order to represent the data a 96 bit number is required. To achieve this the
 * data is stored as seconds, measured using a {@code long}, and nanoseconds,
 * measured using an {@code int}. The nanosecond part will always be between
 * 0 and 999,999,999 representing the nanosecond part of the second.
 * <p>
 * The seconds are measured from the standard Java epoch of 1970-01-01T00:00:00Z.
 * Instants on the time-line after the epoch are positive, earlier are negative.
 * <p>
 * This class uses the {@link TimeScales#simplifiedUtc() simplified UTC} time scale.
 * The scale keeps in step with true UTC by simply ignoring leap seconds.
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
     * Obtains an instance of {@code Instant} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The nanosecond field is set to zero.
     *
     * @param seconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @return an {@code Instant}, never null
     */
    public static Instant seconds(long seconds) {
        return create(seconds, 0);
    }

    /**
     * Obtains an instance of {@code Instant} using seconds from the
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
     * @return an {@code Instant}, never null
     * @throws ArithmeticException if the adjustment causes the seconds to exceed the capacity of {@code Instant}
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
     * Obtains an instance of {@code Instant} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The seconds and nanoseconds are extracted from the specified {@code BigDecimal}.
     * If the decimal is larger than {@code Long.MAX_VALUE} or has more than 9 decimal
     * places then an exception is thrown.
     *
     * @param seconds  the number of seconds, up to scale 9
     * @return an {@code Instant}, never null
     * @throws ArithmeticException if the input seconds exceeds the capacity of a {@code Instant}
     */
    public static Instant seconds(BigDecimal seconds) {
        checkNotNull(seconds, "Seconds must not be null");
        return nanos(seconds.movePointRight(9).toBigIntegerExact());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Instant} using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The seconds and nanoseconds are extracted from the specified milliseconds.
     *
     * @param millis  the number of milliseconds
     * @return an {@code Instant}, never null
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
     * Obtains an instance of {@code Instant} using nanoseconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The seconds and nanoseconds are extracted from the specified nanoseconds.
     *
     * @param nanos  the number of nanoseconds
     * @return an {@code Instant}, never null
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
     * Obtains an instance of {@code Instant} using nanoseconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The seconds and nanoseconds are extracted from the specified {@code BigInteger}.
     * If the resulting seconds value is larger than {@code Long.MAX_VALUE} then an
     * exception is thrown.
     *
     * @param nanos  the number of nanoseconds, not null
     * @return an {@code Instant}, never null
     * @throws ArithmeticException if the input nanoseconds exceeds the capacity of {@code Instant}
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
     * Obtains an instance of {@code Instant} from a provider of instants.
     * <p>
     * In addition to calling {@link InstantProvider#toInstant()} this method
     * also checks the validity of the result of the provider.
     *
     * @param instantProvider  a provider of instant information, not null
     * @return an {@code Instant}, never null
     */
    public static Instant from(InstantProvider instantProvider) {
        checkNotNull(instantProvider, "InstantProvider must not be null");
        Instant provided = instantProvider.toInstant();
        checkNotNull(provided, "The implementation of InstantProvider must not return null");
        return provided;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Instant} by parsing a string.
     * <p>
     * This will parse the string produced by {@link #toString()} which is
     * the ISO-8601 format {@code yyyy-MM-ddTHH:mm:ss.SSSSSSSSSZ}.
     * The numbers must be ASCII numerals.
     * The seconds are mandatory, but the fractional seconds are optional.
     * There must be no more than 9 digits after the decimal point.
     * The letters (T and Z) will be accepted in upper or lower case.
     * The decimal point may be either a dot or a comma.
     *
     * @param text  the text to parse, not null
     * @return an {@code Instant}, never null
     * @throws CalendricalParseException if the text cannot be parsed to an {@code Instant}
     */
    public static Instant parse(final String text) {
        Instant.checkNotNull(text, "Text to parse must not be null");
        // TODO: Implement
        throw new UnsupportedOperationException();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Instant} using seconds and nanoseconds.
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
     * Constructs an instance of {@code Instant} using seconds from the epoch of
     * 1970-01-01T00:00:00Z and nanosecond fraction of second.
     *
     * @param epochSeconds  the number of seconds from the epoch
     * @param nanos  the nanoseconds within the second, must be positive
     */
    private Instant(long epochSeconds, int nanos) {
        super();
        this.seconds = epochSeconds;
        this.nanos = nanos;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance, never null
     */
    private Object readResolve() {
        return (seconds | nanos) == 0 ? EPOCH : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * in this instant.
     * <p>
     * The instant is stored using two fields - seconds and nanoseconds.
     * The seconds are relative to the epoch of 1970-01-01T00:00:00Z.
     * The nanoseconds are a value from 0 to 999,999,999 adjusting the epoch
     * seconds to be later along the time-line.
     * The total instant is defined by calling this method and {@link #getNanoOfSecond()}.
     *
     * @return the seconds from the epoch
     */
    public long getEpochSeconds() {
        return seconds;
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start
     * of the second returned by {@link #getEpochSeconds()}.
     * <p>
     * The instant is stored using two fields - seconds and nanoseconds.
     * The seconds are relative to the epoch of 1970-01-01T00:00:00Z.
     * The nanoseconds are a value from 0 to 999,999,999 adjusting the epoch
     * seconds to be later along the time-line.
     * The total instant is defined by calling this method and {@link #getEpochSeconds()}.
     *
     * @return the nanoseconds within the second, always positive, never exceeds 999,999,999
     */
    public int getNanoOfSecond() {
        return nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified {@code Duration} added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, positive or negative, not null
     * @return an {@code Instant} with the duration added, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
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
     * Returns a copy of this instant with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add, positive or negative
     * @return an {@code Instant} with the duration added, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
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
     * Returns a copy of this instant with the specified number of milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToAdd  the milliseconds to add, positive or negative
     * @return an {@code Instant} with the duration added, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
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
     * Returns a copy of this instant with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToAdd  the nanoseconds to add, positive or negative
     * @return an {@code Instant} with the duration added, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
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
     * Returns a copy of this instant with the specified {@code Duration} subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, positive or negative, not null
     * @return an {@code Instant} with the duration subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
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
     * Returns a copy of this instant with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToSubtract  the seconds to subtract, positive or negative
     * @return an {@code Instant} with the duration subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
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
     * Returns a copy of this instant with the specified number of milliseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToSubtract  the milliseconds to subtract, positive or negative
     * @return an {@code Instant} with the duration subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
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
     * Returns a copy of this instant with the specified number of nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToSubtract  the nanoseconds to subtract, positive or negative
     * @return an {@code Instant} with the duration subtracted, never null
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Instant}
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
     * of 1970-01-01T00:00:00Z expressed as a {@code BigDecimal}.
     *
     * @return the number of seconds since the epoch of 1970-01-01T00:00:00Z, scale 9, never null
     */
    public BigDecimal toEpochSeconds() {
        return BigDecimal.valueOf(seconds).add(BigDecimal.valueOf(nanos, 9));
    }

    /**
     * Converts this instant to the number of nanoseconds from the epoch
     * of 1970-01-01T00:00:00Z expressed as a {@code BigInteger}.
     *
     * @return the number of nanoseconds since the epoch of 1970-01-01T00:00:00Z, never null
     */
    public BigInteger toEpochNanos() {
        return BigInteger.valueOf(seconds).multiply(BILLION).add(BigInteger.valueOf(nanos));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to the number of milliseconds from the epoch
     * of 1970-01-01T00:00:00Z.
     * <p>
     * If this instant represents a point on the time-line too far in the future
     * or past to fit in a {@code long} milliseconds, then an exception is thrown.
     * <p>
     * If this instant has greater than millisecond precision, then the conversion
     * will drop any excess precision information as though the amount in nanoseconds
     * was subject to integer division by one million.
     *
     * @return the number of milliseconds since the epoch of 1970-01-01T00:00:00Z
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code long}
     */
    public long toEpochMillisLong() {
        long millis = MathUtils.safeMultiply(seconds, 1000);
        return millis + nanos / 1000000;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to an instant, trivially
     * returning {@code this}.
     *
     * @return {@code this}, never null
     */
    public Instant toInstant() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instant to the specified {@code Instant}.
     * <p>
     * The comparison is based on the time-line position of the instants.
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
     * Checks if this instant is after the specified {@code Instant}.
     * <p>
     * The comparison is based on the time-line position of the instants.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return true if this instant is after the specified instant
     * @throws NullPointerException if otherInstant is null
     */
    public boolean isAfter(Instant otherInstant) {
        return compareTo(otherInstant) > 0;
    }

    /**
     * Checks if this instant is before the specified {@code Instant}.
     * <p>
     * The comparison is based on the time-line position of the instants.
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
     * Checks if this instant is equal to the specified {@code Instant}.
     * <p>
     * The comparison is based on the time-line position of the instants.
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
     * A hash code for this instant.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return ((int) (seconds ^ (seconds >>> 32))) + 51 * nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this instant using ISO-8601 representation.
     * <p>
     * The format of the returned string will be {@code yyyy-MM-ddTHH:mm:ss.SSSSSSSSSZ}.
     *
     * @return an ISO-8601 representation of this instant, never null
     */
    @Override
    public String toString() {
        // TODO: optimize and handle big instants
        // TODO: Consider epoch plus offset format instead
        return OffsetDateTime.fromInstant(this, ZoneOffset.UTC).toLocalDateTime().toString() + 'Z';
    }

}
