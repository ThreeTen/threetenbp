/*
 * Copyright (c) 2007, 2008, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.CalendarConversionException;
import javax.time.scale.AbstractInstant;
import javax.time.scale.UTC_NoEpochLeaps;
import javax.time.scale.TimeScale;

/**
 * An instantaneous point on the time-line of the default TimeScale.
 * <p>
 * The Java Time Framework models time as a series of instantaneous events,
 * known as instants, along a single time-line. This class represents one
 * of those instants.
 * <p>
 * Each instant is theoretically an instantaneous event, however for practicality
 * a precision of nanoseconds has been chosen.
 * <p>
 * An instant is always defined with respect to a well-defined fixed point in time,
 * known as the epoch. The Java Time Framework uses the standard Java epoch of
 * 1970-01-01T00:00:00Z.
 * <p>
 * Instant is thread-safe and immutable.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class Instant extends AbstractInstant<Instant>
        implements InstantProvider, Comparable<Instant>, Serializable {
    // TODO: Serialized format
    // TODO: Evaluate hashcode
    // TODO: Optimise to 2 private subclasses (second/nano & millis)
    // TODO: Consider BigDecimal
    // TODO: Check for potential overflows

    /** Coordinated Universal Time without leap seconds.
     * Epoch seconds do not include leap seconds, however the leapSecond field is used
     * to distinguish leapSeconds. */
    public static final TimeScale SCALE = UTC_NoEpochLeaps.SCALE;

    /**
     * Constant for the 1970-01-01T00:00:00Z epoch instant.
     */
    public static final Instant EPOCH = new Instant(0, 0);

    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;
    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = -9114640809030911667L;

    /** Leap second indicator.
     *
     */
    private final int leapSecond;


    protected Instant factory(long epochSeconds, int nanoOfSecond, int leapSecond) {
        if (epochSeconds == 0 && nanoOfSecond == 0 && leapSecond == 0)
            return EPOCH;
        else
            return new Instant(epochSeconds, nanoOfSecond, leapSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Instant</code> from a provider of instants.
     * <p>
     * In addition to calling {@link InstantProvider#toInstant()} this method
     * also checks the validity of the result of the provider.
     *
     * @param instantProvider  a provider of instant information, not null
     * @return the created instant instance, never null
     */
    public static Instant instant(InstantProvider instantProvider) {
        if (instantProvider == null) {
            throw new NullPointerException("Instant provider must not be null");
        }
        Instant provided = instantProvider.toInstant();
        if (provided == null) {
            throw new NullPointerException("The implementation of InstantProvider must not return null");
        }
        return provided;
    }

    public static Instant instant(AbstractInstant tsi) {
        if (tsi instanceof Instant) {
            return (Instant)tsi;
        }
        else if (!SCALE.equals(tsi.getScale())) {
            tsi = SCALE.instant(tsi);
        }
        return new Instant(tsi.getEpochSeconds(), tsi.getNanoOfSecond(), tsi.getLeapSecond());
    }

    /**
     * Factory method to create an instance of Instant using seconds from the
     * epoch of 1970-01-01T00:00:00Z with a zero nanosecond fraction.
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @return the created Instant, never null
     */
    public static Instant instant(long epochSeconds) {
        if (epochSeconds == 0) {
            return EPOCH;
        }

        return new Instant(epochSeconds, 0);
    }

    /**
     * Factory method to create an instance of Instant using seconds from the
     * epoch of 1970-01-01T00:00:00Z and nanosecond fraction of second.
     * <p>
     * Primitive fractions of seconds can be unintuitive.
     * For positive values, they work as expected: <code>instant(0L, 1)</code>
     * represents one nanosecond after the epoch.
     * For negative values, they can be confusing: <code>instant(-1L, 999999999)</code>
     * represents one nanosecond before the epoch.
     * It can be thought of as minus one second plus 999,999,999 nanoseconds.
     * As a result, it can be easier to use a negative fraction:
     * <code>instant(0L, -1)</code> - which does represent one nanosecond before
     * the epoch.
     * Thus, the <code>nanoOfSecond</code> parameter is a positive or negative
     * adjustment to the <code>epochSeconds</code> parameter along the timeline.
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfSecond  the nanoseconds within the second, -999,999,999 to 999,999,999
     * @return the created Instant
     * @throws IllegalArgumentException if nanoOfSecond is out of range
     */
    public static Instant instant(long epochSeconds, int nanoOfSecond) {
        if (nanoOfSecond >= NANOS_PER_SECOND) {
            throw new IllegalArgumentException("Nanosecond fraction must not be more than 999,999,999 but was " + nanoOfSecond);
        }
        if (nanoOfSecond < 0) {
            nanoOfSecond += NANOS_PER_SECOND;
            if (nanoOfSecond <= 0) {
                throw new IllegalArgumentException("Nanosecond fraction must not be less than -999,999,999 but was " + nanoOfSecond);
            }
            epochSeconds = MathUtils.safeDecrement(epochSeconds);
        }
        if (epochSeconds == 0 && nanoOfSecond == 0) {
           return EPOCH;
        }
        return new Instant(epochSeconds, nanoOfSecond);
    }

    /**
     * Factory method to create an instance of Instant using seconds from the
     * epoch of 1970-01-01T00:00:00Z and fraction of second.
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param fractionOfSecond  the fraction of the second, from -1 to 1 exclusive
     * @return the created Instant, never null
     * @throws IllegalArgumentException if fractionOfSecond is out of range
     */
    public static Instant instant(long epochSeconds, double fractionOfSecond) {
        if (fractionOfSecond <= -1 || fractionOfSecond >= 1) {
            throw new IllegalArgumentException("Fraction of second must be between -1 and 1 exclusive but was " + fractionOfSecond);
        }
        if (epochSeconds == 0 && fractionOfSecond == 0d) {
            return EPOCH;
        }
        int nanos = (int) Math.round(fractionOfSecond * NANOS_PER_SECOND);
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            epochSeconds = MathUtils.safeDecrement(epochSeconds);
        } else if (nanos == NANOS_PER_SECOND) {
			nanos = 0;
			epochSeconds = MathUtils.safeIncrement(epochSeconds);
		}
        return new Instant(epochSeconds, nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method to create an instance of Instant using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z with no further fraction of a second.
     *
     * @param epochMillis  the number of milliseconds from the epoch of 1970-01-01T00:00:00Z
     * @return the created Instant, never null
     */
    public static Instant millisInstant(long epochMillis) {
        if (epochMillis < 0) {
            epochMillis++;
            long epochSeconds = epochMillis / 1000;
            int millis = ((int) (epochMillis % 1000));  // 0 to -999
            millis = 999 + millis;  // 0 to 999
            return new Instant(epochSeconds - 1, millis * 1000000);
        }
        if (epochMillis == 0) {
            return EPOCH;
        }
        return new Instant(epochMillis / 1000, ((int) (epochMillis % 1000)) * 1000000);
    }

    /**
     * Factory method to create an instance of Instant using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z and nanosecond fraction of millisecond.
     *
     * @param epochMillis  the number of milliseconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfMillisecond  the nanoseconds within the millisecond, must be positive
     * @return the created Instant, never null
     * @throws IllegalArgumentException if nanoOfMillisecond is not in the range 0 to 999,999
     */
    public static Instant millisInstant(long epochMillis, int nanoOfMillisecond) {
        if (nanoOfMillisecond < 0) {
            throw new IllegalArgumentException("NanoOfMillisecond must be positive but was " + nanoOfMillisecond);
        }
        if (nanoOfMillisecond > 999999) {
            throw new IllegalArgumentException("NanoOfMillisecond must not be more than 999,999 but was " + nanoOfMillisecond);
        }
        if (epochMillis < 0) {
            epochMillis++;
            long epochSeconds = epochMillis / 1000;
            int millis = ((int) (epochMillis % 1000));  // 0 to -999
            millis = 999 + millis;  // 0 to 999
            return new Instant(epochSeconds - 1, millis * 1000000 + nanoOfMillisecond);
        }
        if (epochMillis == 0 && nanoOfMillisecond == 0) {
            return EPOCH;
        }
        return new Instant(epochMillis / 1000, ((int) (epochMillis % 1000)) * 1000000 + nanoOfMillisecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance of Instant using seconds from the epoch of
     * 1970-01-01T00:00:00Z and nanosecond fraction of second.
     *
     * @param epochSeconds  the number of seconds from the epoch
     * @param nanoOfSecond  the nanoseconds within the second, must be positive
     */
    private Instant(long epochSeconds, int nanoOfSecond) {
        super(epochSeconds, nanoOfSecond);
        this.leapSecond = 0;
    }

    Instant(long epochSeconds, int nanoOfSecond, int leapSecond) {
        super(epochSeconds, nanoOfSecond);
        this.leapSecond = leapSecond;
    }

    public TimeScale getScale() {
        return SCALE;
    }

    public int getLeapSecond() {
        return leapSecond;
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
     * value. In this scenario, this constructor will throw an exception.
     *
     * @return the number of milliseconds since the epoch of 1970-01-01T00:00:00Z
     * @throws CalendarConversionException if the instant is too large or too
     *  small to represent as epoch milliseconds
     */
    public long toEpochMillis() {
        try {
            long millis = MathUtils.safeMultiply(getEpochSeconds(), 1000);
            return millis + getNanoOfSecond() / 1000000;
        } catch (ArithmeticException ex) {
            throw new CalendarConversionException("The Instant cannot be represented as epoch milliseconds");
        }
    }

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
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherInstant is null
     */
    public int compareTo(Instant otherInstant) {
        int cmp = MathUtils.safeCompare(getEpochSeconds(), otherInstant.getEpochSeconds());
        if (cmp != 0) {
            return cmp;
        }
        cmp = MathUtils.safeCompare(leapSecond, otherInstant.leapSecond);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(getNanoOfSecond(), otherInstant.getNanoOfSecond());
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
            return this.getEpochSeconds() == other.getEpochSeconds() &&
                   this.getNanoOfSecond() == other.getNanoOfSecond() &&
                    this.leapSecond == other.leapSecond;
        }
        return false;
    }

    /**
     * A hashcode for this Instant.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return ((int) (getEpochSeconds() ^ (getEpochSeconds() >>> 32))) + 51 * getNanoOfSecond();
    }
}
