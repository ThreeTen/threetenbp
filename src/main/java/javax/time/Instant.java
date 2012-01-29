/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendrical.TAIInstant;
import javax.time.calendrical.UTCInstant;
import javax.time.calendrical.UTCRules;
import javax.time.format.CalendricalParseException;

/**
 * An instantaneous point on the time-line.
 * <p>
 * The Time Framework for Java models time as a series of instantaneous events,
 * known as instants, along a single time-line.
 * This class represents one of those instants.
 * <p>
 * An instant is in reality an instantaneous event on an infinite time-line.
 * However, for practicality this API uses a precision of nanoseconds.
 * In addition, this API limits the measurable time-line to the number of seconds
 * that can be held in a {@code long}.
 * This is greater than the current estimated age of the universe.
 * <p>
 * In order to represent the data a 96 bit number is required. To achieve this the
 * data is stored as seconds, measured using a {@code long}, and nanoseconds,
 * measured using an {@code int}. The nanosecond part will always be between
 * 0 and 999,999,999 representing the nanosecond part of the second.
 * <p>
 * The seconds are measured from the standard Java epoch of {@code 1970-01-01T00:00:00Z}.
 * Instants on the time-line after the epoch are positive, earlier are negative.
 * 
 * <h4>Time-scale</h4>
 * <p>
 * {@code Instant} uses the <a href="http://www.cl.cam.ac.uk/~mgk25/time/utc-sls/">UTC-SLS</a>
 * time-scale which always has 86400 subdivisions (seconds) in a day.
 * Essentially, UTC-SLS is a consistent mechanism of converting an accurate UTC time
 * (measured in SI seconds with leap seconds) to a day formed of exactly 86400 subdivisions.
 * For the benefit of most users, each subdivision is referred to as a "second", however
 * in reality not all of these are equal to an SI second.
 * <p>
 * The main benefit of UTC-SLS is that it provides a clear definition of how to relate the
 * 86400 subdivision day that application writers want to the accurate time-scales with leap seconds.
 * A second benefit is that in an accurate implementation, the UTC-SLS time never experiences
 * any gaps or overlaps, with the value always increasing.
 * <p>
 * UTC-SLS is defined as spreading any leap second evenly over the last 1000 seconds of the day.
 * This corresponds to times after 23:43:21 on a day with an added leap second, or
 * times after 23:43:19 on a day with a removed leap second.
 * The conversion is implemented in the {@link UTCRules} subclass.
 * <p>
 * The UTC-SLS conversion only matters to users of this class with high precision requirements.
 * To keep full track of an instant using an accurate time-scale use the {@link UTCInstant} or
 * {@link TAIInstant} class.
 * For most applications, the behavior where each day has exactly 86400 subdivisions is the desired one.
 * The UTC-SLS time-scale is also used for all human-scale date-time classes,
 * such as {@code LocalTime}, {@code OffsetDateTime} and {@code ZonedDateTime}.
 * <p>
 * This definition of UTC-SLS is based on the definition of UTC in {@code UTCInstant}.
 * That includes a discussion on instants before 1972, including the epoch of {@code 1970-01-01}.
 * <p>
 * As a result of these definitions, operations to add or subtract durations will ignore leap seconds.
 * Use {@code UTCInstant} or {@code TAIInstant} if accurate duration calculations are required.
 * 
 * <h4>Implementation notes</h4>
 * <p>
 * This class is immutable and thread-safe.
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
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;
    /**
     * BigInteger constant for a billion.
     */
    static final BigInteger BILLION = BigInteger.valueOf(NANOS_PER_SECOND);

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
     * Obtains the current instant from the system time-source in the default time-zone.
     * <p>
     * This will query the {@link TimeSource#system() system time-source} to obtain the current instant.
     * <p>
     * Using this method will prevent the ability to use an alternate time-source for testing
     * because the time-source is hard-coded.
     *
     * @return the current instant using the system clock, not null
     */
    public static Instant now() {
        return now(TimeSource.system());
    }

    /**
     * Obtains the current instant from the specified clock.
     * <p>
     * This will query the specified time-source to obtain the current time.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param timeSource  the time-source to use, not null
     * @return the current instant, not null
     */
    public static Instant now(TimeSource timeSource) {
        checkNotNull(timeSource, "TimeSource must not be null");
        return of(timeSource.instant());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Instant} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The nanosecond field is set to zero.
     *
     * @param epochSecond  the number of seconds from 1970-01-01T00:00:00Z
     * @return an instant, not null
     */
    public static Instant ofEpochSecond(long epochSecond) {
        return create(epochSecond, 0);
    }

    /**
     * Obtains an instance of {@code Instant} using seconds from the
     * epoch of 1970-01-01T00:00:00Z and nanosecond fraction of second.
     * <p>
     * This method allows an arbitrary number of nanoseconds to be passed in.
     * The factory will alter the values of the second and nanosecond in order
     * to ensure that the stored nanosecond is in the range 0 to 999,999,999.
     * For example, the following will result in the exactly the same instant:
     * <pre>
     *  Instant.ofSeconds(3, 1);
     *  Instant.ofSeconds(4, -999999999);
     *  Instant.ofSeconds(2, 1000000001);
     * </pre>
     *
     * @param epochSecond  the number of seconds from 1970-01-01T00:00:00Z
     * @param nanoAdjustment  the nanosecond adjustment to the number of seconds, positive or negative
     * @return an instant, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public static Instant ofEpochSecond(long epochSecond, long nanoAdjustment) {
        long secs = MathUtils.safeAdd(epochSecond, MathUtils.floorDiv(nanoAdjustment, NANOS_PER_SECOND));
        int nos = MathUtils.floorMod(nanoAdjustment, NANOS_PER_SECOND);
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
     * @param epochSecond  the number of seconds from 1970-01-01T00:00:00Z, up to scale 9
     * @return an instant, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public static Instant ofEpochSecond(BigDecimal epochSecond) {
        checkNotNull(epochSecond, "Seconds must not be null");
        return ofEpochNano(epochSecond.movePointRight(9).toBigIntegerExact());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Instant} using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The seconds and nanoseconds are extracted from the specified milliseconds.
     *
     * @param epochMilli  the number of milliseconds from 1970-01-01T00:00:00Z
     * @return an instant, not null
     */
    public static Instant ofEpochMilli(long epochMilli) {
        long secs = MathUtils.floorDiv(epochMilli, 1000);
        int mos = MathUtils.floorMod(epochMilli, 1000);
        return create(secs, mos * 1000000);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Instant} using nanoseconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The seconds and nanoseconds are extracted from the specified nanoseconds.
     *
     * @param epochNano  the number of nanoseconds from 1970-01-01T00:00:00Z
     * @return an instant, not null
     */
    public static Instant ofEpochNano(long epochNano) {
        long secs = MathUtils.floorDiv(epochNano, NANOS_PER_SECOND);
        int nos = MathUtils.floorMod(epochNano, NANOS_PER_SECOND);
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
     * @param epochNano  the number of nanoseconds from 1970-01-01T00:00:00Z, not null
     * @return an instant, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public static Instant ofEpochNano(BigInteger epochNano) {
        checkNotNull(epochNano, "Nanos must not be null");
        BigInteger[] divRem = epochNano.divideAndRemainder(BILLION);
        if (divRem[0].bitLength() > 63) {
            throw new ArithmeticException("Exceeds capacity of Duration: " + epochNano);
        }
        return ofEpochSecond(divRem[0].longValue(), divRem[1].intValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Instant} from a provider of instants.
     * <p>
     * In addition to calling {@link InstantProvider#toInstant()} this method
     * also checks the validity of the result of the provider.
     *
     * @param instantProvider  a provider of instant information, not null
     * @return an instant, not null
     */
    public static Instant of(InstantProvider instantProvider) {
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
     *
     * @param text  the text to parse, not null
     * @return an instant, not null
     * @throws CalendricalParseException if the text cannot be parsed to an {@code Instant}
     */
    //TODO:The decimal point may be either a dot or a comma.
    // TODO: optimize and handle big instants
    public static Instant parse(final CharSequence text) {
        Instant.checkNotNull(text, "Text to parse must not be null");
        int length = text.length();
        if (length < 2) {
            throw new CalendricalParseException("Instant could not be parsed: " + text, text, 0);
        }
        if (text.charAt(length - 1) != 'Z' && text.charAt(length - 1) != 'z') {
            throw new CalendricalParseException("Instant could not be parsed: " + text, text, length - 1);
        }
        return OffsetDateTime.of(LocalDateTime.parse(text.subSequence(0, length - 1)), ZoneOffset.UTC).toInstant();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Instant} using seconds and nanoseconds.
     *
     * @param seconds  the length of the duration in seconds
     * @param nanoOfSecond  the nano-of-second, from 0 to 999,999,999
     */
    private static Instant create(long seconds, int nanoOfSecond) {
        if ((seconds | nanoOfSecond) == 0) {
            return EPOCH;
        }
        return new Instant(seconds, nanoOfSecond);
    }

    /**
     * Constructs an instance of {@code Instant} using seconds from the epoch of
     * 1970-01-01T00:00:00Z and nanosecond fraction of second.
     *
     * @param epochSecond  the number of seconds from 1970-01-01T00:00:00Z
     * @param nanos  the nanoseconds within the second, must be positive
     */
    private Instant(long epochSecond, int nanos) {
        super();
        this.seconds = epochSecond;
        this.nanos = nanos;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance, not null
     */
    private Object readResolve() {
        return (seconds | nanos) == 0 ? EPOCH : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds from the Java epoch of 1970-01-01T00:00:00Z.
     * <p>
     * The epoch second count is a simple incrementing count of seconds where
     * second 0 is 1970-01-01T00:00:00Z.
     * The nanosecond part of the day is returned by {@code getNanosOfSecond}.
     *
     * @return the seconds from the epoch of 1970-01-01T00:00:00Z
     */
    public long getEpochSecond() {
        return seconds;
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start
     * of the second.
     * <p>
     * The nanosecond-of-second value measures the total number of nanoseconds from
     * the second returned by {@code getEpochSecond}.
     *
     * @return the nanoseconds within the second, always positive, never exceeds 999,999,999
     */
    public int getNanoOfSecond() {
        return nanos;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, positive or negative, not null
     * @return an {@code Instant} based on this instant with the specified duration added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant plus(Duration duration) {
        long secsToAdd = duration.getSeconds();
        int nanosToAdd = duration.getNanoOfSecond();
        if ((secsToAdd | nanosToAdd) == 0) {
            return this;
        }
        return plus(secsToAdd, nanosToAdd);
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
     * @return an {@code Instant} based on this duration with the specified duration added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant plus(long amount, TimeUnit unit) {
        if (unit == TimeUnit.SECONDS) {
            return plusSeconds(amount);
        } else if (unit == TimeUnit.MILLISECONDS) {
            return plusMillis(amount);
        } else if (unit == TimeUnit.NANOSECONDS) {
            return plusNanos(amount);
        }
        return plus(Duration.of(amount, unit));
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add, positive or negative
     * @return an {@code Instant} based on this instant with the specified seconds added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        return plus(secondsToAdd, 0);
    }

    /**
     * Returns a copy of this instant with the specified duration in milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToAdd  the milliseconds to add, positive or negative
     * @return an {@code Instant} based on this instant with the specified milliseconds added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant plusMillis(long millisToAdd) {
        return plus(millisToAdd / 1000, (millisToAdd % 1000) * 1000000);
    }

    /**
     * Returns a copy of this instant with the specified duration in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToAdd  the nanoseconds to add, positive or negative
     * @return an {@code Instant} based on this instant with the specified nanoseconds added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant plusNanos(long nanosToAdd) {
        return plus(0, nanosToAdd);
    }

    /**
     * Returns a copy of this instant with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add, positive or negative
     * @param nanosToAdd  the nanos to add, positive or negative
     * @return an {@code Instant} based on this instant with the specified seconds added, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    private Instant plus(long secondsToAdd, long nanosToAdd) {
        if ((secondsToAdd | nanosToAdd) == 0) {
            return this;
        }
        long epochSec = MathUtils.safeAdd(seconds, secondsToAdd);
        epochSec = MathUtils.safeAdd(epochSec, nanosToAdd / NANOS_PER_SECOND);
        nanosToAdd = nanosToAdd % NANOS_PER_SECOND;
        long nanoAdjustment = nanos + nanosToAdd;  // safe int+NANOS_PER_SECOND
        return ofEpochSecond(epochSec, nanoAdjustment);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to subtract, positive or negative, not null
     * @return an {@code Instant} based on this instant with the specified duration subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant minus(Duration duration) {
        long secsToSubtract = duration.getSeconds();
        int nanosToSubtract = duration.getNanoOfSecond();
        if ((secsToSubtract | nanosToSubtract) == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(seconds, secsToSubtract);
        long nanoAdjustment = ((long) nanos) - nanosToSubtract;  // safe int+int
        return ofEpochSecond(secs, nanoAdjustment);
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
     * @return a {@code Duration} based on this duration with the specified duration subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant minus(long amount, TimeUnit unit) {
        if (unit == TimeUnit.SECONDS) {
            return minusSeconds(amount);
        } else if (unit == TimeUnit.MILLISECONDS) {
            return minusMillis(amount);
        } else if (unit == TimeUnit.NANOSECONDS) {
            return minusNanos(amount);
        }
        return minus(Duration.of(amount, unit));
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified duration in seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToSubtract  the seconds to subtract, positive or negative
     * @return an {@code Instant} based on this instant with the specified seconds subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == Long.MIN_VALUE) {
            return plusSeconds(Long.MAX_VALUE).plusSeconds(1);
        }
        return plusSeconds(-secondsToSubtract);
    }

    /**
     * Returns a copy of this instant with the specified duration in milliseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToSubtract  the milliseconds to subtract, positive or negative
     * @return an {@code Instant} based on this instant with the specified milliseconds subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant minusMillis(long millisToSubtract) {
        if (millisToSubtract == Long.MIN_VALUE) {
            return plusMillis(Long.MAX_VALUE).plusMillis(1);
        }
        return plusMillis(-millisToSubtract);
    }

    /**
     * Returns a copy of this instant with the specified duration in nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToSubtract  the nanoseconds to subtract, positive or negative
     * @return an {@code Instant} based on this instant with the specified nanoseconds subtracted, not null
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public Instant minusNanos(long nanosToSubtract) {
        if (nanosToSubtract == Long.MIN_VALUE) {
            return plusNanos(Long.MAX_VALUE).plusNanos(1);
        }
        return plusNanos(-nanosToSubtract);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to the number of seconds from the epoch
     * of 1970-01-01T00:00:00Z expressed as a {@code BigDecimal}.
     *
     * @return the number of seconds since the epoch of 1970-01-01T00:00:00Z, scale 9, not null
     */
    public BigDecimal toEpochSecond() {
        return BigDecimal.valueOf(seconds).add(BigDecimal.valueOf(nanos, 9));
    }

    /**
     * Converts this instant to the number of nanoseconds from the epoch
     * of 1970-01-01T00:00:00Z expressed as a {@code BigInteger}.
     *
     * @return the number of nanoseconds since the epoch of 1970-01-01T00:00:00Z, not null
     */
    public BigInteger toEpochNano() {
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
     * @throws ArithmeticException if the calculation exceeds the supported range
     */
    public long toEpochMilli() {
        long millis = MathUtils.safeMultiply(seconds, 1000);
        return millis + nanos / 1000000;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this instant to an {@code Instant}, trivially returning {@code this}.
     *
     * @return {@code this}, not null
     */
    public Instant toInstant() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instant to the specified instant.
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
     * Checks if this instant is after the specified instant.
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
     * Checks if this instant is before the specified instant.
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
     * Checks if this instant is equal to the specified instant.
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
     * Returns a hash code for this instant.
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
     * @return an ISO-8601 representation of this instant, not null
     */
    @Override
    public String toString() {
        // TODO: optimize and handle big instants
        // TODO: Consider epoch plus offset format instead
        return OffsetDateTime.ofInstantUTC(this).toLocalDateTime().toString() + 'Z';
    }

}
