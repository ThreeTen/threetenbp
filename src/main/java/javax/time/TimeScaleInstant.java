/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;

/**
 * An instantaneous point on the time-line that is defined relative to a time-scale.
 * <p>
 * Most of the Java Time Framework works on the assumption that the time-line is
 * simple, there are no leap-seconds and there are always 24 * 60 * 60 seconds in a day.
 * Sadly, the real-life time-line is not this simple.
 * <p>
 * This class is an alternative representation of an instant on the time-line for
 * those cases where leap-seconds and similar have to be dealt with. One example is
 * where an external time-source supplies data with leap seconds and this must be
 * correctly handled.
 * <p>
 * The handling is dealt with by the {@link TimeScale} interface, which defines
 * how each time-scale works, including whether it has leap-seconds. This class allows
 * points on the time-line in different time-scales to be compared and worked on.
 * <p>
 * As API designers, we hope that this class will be rarely used, and that the
 * 'ideal world' represented by {@link Instant} will be sufficient.
 * <p>
 * TimeScaleInstant is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class TimeScaleInstant
        implements InstantProvider, Comparable<TimeScaleInstant>, Serializable {
    // TODO: Consider BigDecimal
    // TODO: Check for potential overflows

    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;
    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = -9114640809030911667L;

    /**
     * The time-scale that defines the epoch and counting mechanism.
     * The scale also defines whether and when leap seconds occur.
     */
    private final TimeScale timeScale;
    /**
     * The number of seconds from the time-scale epoch.
     */
    private final long epochSeconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanoOfSecond;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>TimeScaleInstant</code> in the specified
     * time-scale from a provider of instants.
     *
     * @param timeScale  the time-scale to use, not null
     * @param instantProvider  a provider of instant information, not null
     * @return the created instant, never null
     */
    public static TimeScaleInstant from(TimeScale timeScale, InstantProvider instantProvider) {
        Instant.checkNotNull(timeScale, "TimeScale must not be null");
        Instant.checkNotNull(instantProvider, "InstantProvider must not be null");
        return timeScale.toTimeScaleInstant(instantProvider);
    }

    /**
     * Obtains an instance of <code>TimeScaleInstant</code> in the specified
     * time-scale using seconds from the epoch of the scale.
     *
     * @param epochSeconds  the number of seconds from the epoch of the time-scale
     * @return the created instant, never null
     */
    public static TimeScaleInstant seconds(TimeScale timeScale, long epochSeconds) {
        return new TimeScaleInstant(timeScale, epochSeconds, 0);
    }

    /**
     * Obtains an instance of <code>TimeScaleInstant</code> in the specified
     * time-scale using seconds from the epoch of the scale, and nanosecond fraction of second.
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
     * adjustment to the <code>epochSeconds</code> parameter along the time-line.
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfSecond  the nanoseconds within the second, -999,999,999 to 999,999,999
     * @return the created instant, never null
     * @throws IllegalArgumentException if nanoOfSecond is out of range
     */
    public static TimeScaleInstant seconds(TimeScale timeScale, long epochSeconds, int nanoOfSecond) {
        // TODO: does this need to move to TimeScale ?
        
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
        return new TimeScaleInstant(timeScale, epochSeconds, nanoOfSecond);
    }

//    /**
//     * Factory method to create an instance of Instant using seconds from the
//     * epoch of 1970-01-01T00:00:00Z and fraction of second.
//     *
//     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
//     * @param fractionOfSecond  the fraction of the second, from -1 to 1 exclusive
//     * @return the created Instant, never null
//     * @throws IllegalArgumentException if fractionOfSecond is out of range
//     */
//    public static TimeScaleInstant instant(long epochSeconds, double fractionOfSecond) {
//        if (fractionOfSecond <= -1 || fractionOfSecond >= 1) {
//            throw new IllegalArgumentException("Fraction of second must be between -1 and 1 exclusive but was " + fractionOfSecond);
//        }
//        if (epochSeconds == 0 && fractionOfSecond == 0d) {
//            return EPOCH;
//        }
//        int nanos = (int) Math.round(fractionOfSecond * NANOS_PER_SECOND);
//        if (nanos < 0) {
//            nanos += NANOS_PER_SECOND;
//            epochSeconds = MathUtils.safeDecrement(epochSeconds);
//        }
//        return new TimeScaleInstant(epochSeconds, nanos);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Factory method to create an instance of Instant using milliseconds from the
//     * epoch of 1970-01-01T00:00:00Z with no further fraction of a second.
//     *
//     * @param epochMillis  the number of milliseconds from the epoch of 1970-01-01T00:00:00Z
//     * @return the created Instant, never null
//     */
//    public static TimeScaleInstant millisInstant(long epochMillis) {
//        if (epochMillis < 0) {
//            epochMillis++;
//            long epochSeconds = epochMillis / 1000;
//            int millis = ((int) (epochMillis % 1000));  // 0 to -999
//            millis = 999 + millis;  // 0 to 999
//            return new TimeScaleInstant(epochSeconds - 1, millis * 1000000);
//        }
//        if (epochMillis == 0) {
//            return EPOCH;
//        }
//        return new TimeScaleInstant(epochMillis / 1000, ((int) (epochMillis % 1000)) * 1000000);
//    }
//
//    /**
//     * Factory method to create an instance of Instant using milliseconds from the
//     * epoch of 1970-01-01T00:00:00Z and nanosecond fraction of millisecond.
//     *
//     * @param epochMillis  the number of milliseconds from the epoch of 1970-01-01T00:00:00Z
//     * @param nanoOfMillisecond  the nanoseconds within the millisecond, must be positive
//     * @return the created Instant, never null
//     * @throws IllegalArgumentException if nanoOfMillisecond is not in the range 0 to 999,999
//     */
//    public static TimeScaleInstant millisInstant(long epochMillis, int nanoOfMillisecond) {
//        if (nanoOfMillisecond < 0) {
//            throw new IllegalArgumentException("NanoOfMillisecond must be positive but was " + nanoOfMillisecond);
//        }
//        if (nanoOfMillisecond > 999999) {
//            throw new IllegalArgumentException("NanoOfMillisecond must not be more than 999,999 but was " + nanoOfMillisecond);
//        }
//        if (epochMillis < 0) {
//            epochMillis++;
//            long epochSeconds = epochMillis / 1000;
//            int millis = ((int) (epochMillis % 1000));  // 0 to -999
//            millis = 999 + millis;  // 0 to 999
//            return new TimeScaleInstant(epochSeconds - 1, millis * 1000000 + nanoOfMillisecond);
//        }
//        if (epochMillis == 0 && nanoOfMillisecond == 0) {
//            return EPOCH;
//        }
//        return new TimeScaleInstant(epochMillis / 1000, ((int) (epochMillis % 1000)) * 1000000 + nanoOfMillisecond);
//    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance of TimeScaleInstant.
     *
     * @param timeScale  the time-scale to use, not null
     * @param epochSeconds  the number of seconds from the epoch
     * @param nanoOfSecond  the nanoseconds within the second, must be positive
     */
    private TimeScaleInstant(TimeScale timeScale, long epochSeconds, int nanoOfSecond) {
        super();
        this.timeScale = timeScale;
        this.epochSeconds = epochSeconds;
        this.nanoOfSecond = nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time-scale.
     *
     * @return the time-scale, never null
     */
    public TimeScale getTimeScale() {
        return timeScale;
    }

    /**
     * Gets the number of seconds from the epoch defined by the time-scale.
     * <p>
     * Instants on the time-line after the epoch are positive, earlier are negative.
     *
     * @return the seconds from the epoch
     */
    public long getEpochSeconds() {
        return epochSeconds;
    }

    /**
     * Gets the number of nanoseconds, later along the time-line, from the start
     * of the second returned by {@link #getEpochSeconds()}.
     *
     * @return the nanoseconds within the second, always positive, never exceeds 999,999,999
     */
    public int getNanoOfSecond() {
        return nanoOfSecond;
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
    public TimeScaleInstant plus(Duration duration) {
        // TODO: Move implementation to TimeScale?
        
        long secsToAdd = duration.getSeconds();
        int nanosToAdd = duration.getNanosAdjustment();
        if (secsToAdd == 0 && nanosToAdd == 0) {
            return this;
        }

        long secs = MathUtils.safeAdd(epochSeconds, secsToAdd);
        int nos = nanoOfSecond + nanosToAdd;

        if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;
            secs = MathUtils.safeIncrement(secs);
        }

        return new TimeScaleInstant(timeScale, secs, nos);
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
    public TimeScaleInstant minus(Duration duration) {
//        Instant.checkNotNull(duration, "Duration must not be null");
//        return plus(duration.negated());
        
        // TODO: Move implementation to TimeScale?
        // treat as plus(duration.negated()) ?
        
        long secsToSubtract = duration.getSeconds();
        int nanosToSubtract = duration.getNanosAdjustment();
        if (secsToSubtract == 0 && nanosToSubtract == 0) {
            return this;
        }
        long secs = MathUtils.safeSubtract(epochSeconds, secsToSubtract);
        int nos = nanoOfSecond - nanosToSubtract;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;
            secs = MathUtils.safeDecrement(secs);
        }
        return new TimeScaleInstant(timeScale, secs, nos);
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
            long millis = MathUtils.safeMultiply(epochSeconds, 1000);
            return millis + nanoOfSecond / 1000000;
        } catch (ArithmeticException ex) {
            throw new CalendarConversionException("The Instant cannot be represented as epoch milliseconds");
        }
    }

    /**
     * Converts this instant to an <code>Instant</code>, which may lose
     * leap-second information.
     * <p>
     * An instance of <code>TimeScaleInstant</code> can contain information about
     * leap-seconds which cannot be represented by <code>Instant</code>. As such,
     * the conversion may lose information, and the exact means of conversion is
     * part of the time-scale's definition.
     *
     * @return an instant representing a point on the time-line close to this instant, never null
     */
    public Instant toInstant() {
        return timeScale.toInstant(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this instant to another based on the absolute time-line.
     * <p>
     * This will normalize both instants to the same time-line before comparison.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherInstant is null
     */
    public int compareTo(TimeScaleInstant otherInstant) {
        if (timeScale.equals(otherInstant.timeScale)) {
            int cmp = MathUtils.safeCompare(epochSeconds, otherInstant.epochSeconds);
            if (cmp != 0) {
                return cmp;
            }
            return MathUtils.safeCompare(nanoOfSecond, otherInstant.nanoOfSecond);
        } else {
            // TODO: normalize time and compare. Normalize to TAI scale?
            return 0;
        }
    }

    /**
     * Is this instant after the specified one.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return true if this instant is after the specified instant
     * @throws NullPointerException if otherInstant is null
     */
    public boolean isAfter(TimeScaleInstant otherInstant) {
        return compareTo(otherInstant) > 0;
    }

    /**
     * Is this instant before the specified one.
     *
     * @param otherInstant  the other instant to compare to, not null
     * @return true if this instant is before the specified instant
     * @throws NullPointerException if otherInstant is null
     */
    public boolean isBefore(TimeScaleInstant otherInstant) {
        return compareTo(otherInstant) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instant equal to that specified taking into account the time-scale.
     * <p>
     * Two instants will only be equal if they represent the same time-point
     * and both have the same time-scale.
     *
     * @param otherInstant  the other instant, null returns false
     * @return true if the other instant is equal to this one
     */
    @Override
    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (otherInstant instanceof TimeScaleInstant) {
            TimeScaleInstant other = (TimeScaleInstant) otherInstant;
            return this.epochSeconds == other.epochSeconds &&
                   this.nanoOfSecond == other.nanoOfSecond &&
                   this.timeScale.equals(other.timeScale);
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
        // TODO: Evaluate hash code
        return ((int) (epochSeconds ^ (epochSeconds >>> 32))) + 51 * nanoOfSecond + 7 * timeScale.hashCode();
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
        // TODO: delegate to TimeScale? Or standard (non ISO) representation (preferred)?
        // perhaps "3156351758secs + 3278527ns + 1leapsec TrueUTC"
        // must contain entire state of TimeScaleInstant
        // TODO: optimize and handle big instants
        return OffsetDateTime.fromInstant(this, ZoneOffset.UTC).toLocalDateTime().toString() + 'Z';
    }

}
