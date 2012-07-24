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

import javax.time.calendrical.DateTime;
import javax.time.format.CalendricalParseException;


/**
 * An instantaneous point on the time-line.
 * <p>
 * This class models a single instantaneous point on the time-line.
 * This might be used to record event time-stamps in the application.
 * <p>
 * For practicality, the instant is stored with some constraints.
 * The measurable time-line is restricted to the number of seconds that can be held
 * in a {@code long}. This is greater than the current estimated age of the universe.
 * The instant is stored to nanosecond resolution.
 * <p>
 * The range of an instant requires the storage of a number larger than a {@code long}.
 * The standard storage scheme uses epoch-seconds measured from the standard Java epoch
 * of {@code 1970-01-01T00:00:00Z} where instants after the epoch have positive values,
 * and earlier instants have negative values. The standard storage stores the fraction
 * of a second as nanosecond-of-second which will always be between 0 and 999,999,999.
 * 
 * <h4>Time-scale</h4>
 * <p>
 * The length of the solar day is the standard way that humans measure time.
 * This has traditionally been subdivided into 24 hours of 60 minutes of 60 seconds,
 * forming a 86400 second day.
 * <p>
 * Modern timekeeping is based on atomic clocks which precisely define an SI second
 * relative to the transitions of a Caesium atom. The length of an SI second was defined
 * to be very close to the 86400th fraction of a day.
 * <p>
 * Unfortunately, as the Earth rotates the length of the day varies.
 * In addition, over time the average length of the day is getting longer as the Earth slows.
 * As a result, the length of a solar day in 2012 is slightly longer than 86400 SI seconds.
 * The actual length of any given day and the amount by which the Earth is slowing
 * are not predictable and can only be determined by measurement.
 * The UT1 time-scale captures the accurate length of day, but is only available some
 * time after the day has completed.
 * <p>
 * The UTC time-scale is a standard approach to bundle up all the additional fractions
 * of a second from UT1 into whole seconds, known as <i>leap-seconds</i>.
 * A leap-second may be added or removed depending on the Earth's rotational changes.
 * As such, UTC permits a day to have 86399 SI seconds or 86401 SI seconds where
 * necessary in order to keep the day aligned with the Sun.
 * <p>
 * The modern UTC time-scale was introduced in 1972, introducing the concept of whole leap-seconds.
 * Between 1958 and 1972, the definition of UTC was complex, with minor sub-second leaps and
 * alterations to the length of the notional second. As of 2012, discussions are underway
 * to change the definition of UTC again, with the potential to remove leap seconds or
 * introduce other changes.
 * <p>
 * Given the complexity of accurate timekeeping described above, this Java API defines
 * its own time-scale with a simplification. The Java time-scale is defined as follows:
 * <ul>
 * <li>midday will always be exactly as defined by the agreed international civil time</li>
 * <li>other times during the day will be broadly in line with the agreed international civil time</li>
 * <li>the day will be divided into exactly 86400 subdivisions, referred to as "seconds"</li>
 * <li>the Java "second" may differ from an SI second</li>
 * </ul>
 * Agreed international civil time is the base time-scale agreed by international convention,
 * which in 2012 is UTC (with leap-seconds).
 * <p>
 * In 2012, the definition of the Java time-scale is the same as UTC for all days except
 * those where a leap-second occurs. On days where a leap-second does occur, the time-scale
 * effectively eliminates the leap-second, maintaining the fiction of 86400 seconds in the day.
 * <p>
 * The main benefit of always dividing the day into 86400 subdivisions is that it matches the
 * expectations of most users of the API. The alternative is to force every user to understand
 * what a leap second is and to force them to have special logic to handle them.
 * Most applications do not have access to a clock that is accurate enough to record leap-seconds.
 * Most applications also do not have a problem with a second being a very small amount longer or
 * shorter than a real SI second during a leap-second.
 * <p>
 * If an application does have access to an accurate clock that reports leap-seconds, then the
 * recommended technique to implement the Java time-scale is to use the UTC-SLS convention.
 * <a href="http://www.cl.cam.ac.uk/~mgk25/time/utc-sls/">UTC-SLS</a> effectively smoothes the
 * leap-second over the last 1000 seconds of the day, making each of the last 1000 "seconds"
 * 1/1000th longer or shorter than a real SI second.
 * <p>
 * One final problem is the definition of the agreed international civil time before the
 * introduction of modern UTC in 1972. This includes the Java epoch of {@code 1970-01-01}.
 * It is intended that instants before 1972 be interpreted based on the solar day divided
 * into 86400 subdivisions.
 * <p>
 * The Java time-scale is used for all date-time classes supplied by JSR-310.
 * This includes {@code Instant}, {@code LocalDate}, {@code LocalTime}, {@code OffsetDateTime},
 * {@code ZonedDateTime} and {@code Duration}.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class Instant
        implements Comparable<Instant>, Serializable {

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
     * Obtains the current instant from the system clock.
     * <p>
     * This will query the {@link Clock#systemUTC() system UTC clock} to
     * obtain the current instant.
     * <p>
     * Using this method will prevent the ability to use an alternate time-source for
     * testing because the clock is effectively hard-coded.
     *
     * @return the current instant using the system clock, not null
     */
    public static Instant now() {
        return Clock.systemUTC().instant();
    }

    /**
     * Obtains the current instant from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current time.
     * <p>
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current instant, not null
     */
    public static Instant now(Clock clock) {
        DateTimes.checkNotNull(clock, "Clock must not be null");
        return clock.instant();
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
        long secs = DateTimes.safeAdd(epochSecond, DateTimes.floorDiv(nanoAdjustment, NANOS_PER_SECOND));
        int nos = DateTimes.floorMod(nanoAdjustment, NANOS_PER_SECOND);
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
        DateTimes.checkNotNull(epochSecond, "Seconds must not be null");
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
        long secs = DateTimes.floorDiv(epochMilli, 1000);
        int mos = DateTimes.floorMod(epochMilli, 1000);
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
        long secs = DateTimes.floorDiv(epochNano, NANOS_PER_SECOND);
        int nos = DateTimes.floorMod(epochNano, NANOS_PER_SECOND);
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
        DateTimes.checkNotNull(epochNano, "Nanos must not be null");
        BigInteger[] divRem = epochNano.divideAndRemainder(BILLION);
        if (divRem[0].bitLength() > 63) {
            throw new ArithmeticException("Exceeds capacity of Duration: " + epochNano);
        }
        return ofEpochSecond(divRem[0].longValue(), divRem[1].intValue());
    }

    /**
     * Obtains an instance of {@code Instant} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code Instant}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the instant, not null
     * @throws CalendricalException if unable to convert to an {@code Instant}
     */
    public static Instant from(DateTime calendrical) {
        Instant obj = calendrical.extract(Instant.class);
        return DateTimes.ensureNotNull(obj, "Unable to convert calendrical to Instant: ", calendrical.getClass());
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
        DateTimes.checkNotNull(text, "Text to parse must not be null");
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
    public int getNano() {
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
        int nanosToAdd = duration.getNano();
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
        long epochSec = DateTimes.safeAdd(seconds, secondsToAdd);
        epochSec = DateTimes.safeAdd(epochSec, nanosToAdd / NANOS_PER_SECOND);
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
        int nanosToSubtract = duration.getNano();
        if ((secsToSubtract | nanosToSubtract) == 0) {
            return this;
        }
        long secs = DateTimes.safeSubtract(seconds, secsToSubtract);
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
        long millis = DateTimes.safeMultiply(seconds, 1000);
        return millis + nanos / 1000000;
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
        int cmp = DateTimes.safeCompare(seconds, otherInstant.seconds);
        if (cmp != 0) {
            return cmp;
        }
        return DateTimes.safeCompare(nanos, otherInstant.nanos);
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
