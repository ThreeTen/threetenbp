/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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

/**
 * An instantaneous point on the time-line.
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
 *
 * @author Stephen Colebourne
 */
public final class Instant implements Comparable<Instant>, Serializable {
    // TODO: Minus methods (as per plus methods)
    // TODO: Duration class integration
    // TODO: Leap seconds (document or implement)
    // TODO: Serialized format
    // TODO: Evaluate hashcode
    // TODO: Optimise to 2 private subclasses (second/nano & millis)
    // TODO: Consider BigDecimal

    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = -9114640809030911667L;

    /**
     * The number of seconds from the epoch of 1970-01-01T00:00:00Z.
     */
    private final long epochSeconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanoOfSecond;

    //-----------------------------------------------------------------------
    /**
     * Factory method to create an instance of Instant using seconds from the
     * epoch of 1970-01-01T00:00:00Z with a zero nanosecond fraction.
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @return the created Instant
     */
    public static Instant instant(long epochSeconds) {
        return new Instant(epochSeconds, 0);
    }

    /**
     * Factory method to create an instance of Instant using seconds from the
     * epoch of 1970-01-01T00:00:00Z and nanosecond fraction of second.
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfSecond  the nanoseconds within the second, must be positive
     * @return the created Instant
     * @throws IllegalArgumentException if nanoOfSecond is not in the range 0 to 999,999,999
     */
    public static Instant instant(long epochSeconds, int nanoOfSecond) {
        if (nanoOfSecond < 0) {
            throw new IllegalArgumentException("NanoOfSecond must be positive but was " + nanoOfSecond);
        }
        if (nanoOfSecond > 999999999) {
            throw new IllegalArgumentException("NanoOfSecond must not be more than 999,999,999 but was " + nanoOfSecond);
        }
        return new Instant(epochSeconds, nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method to create an instance of Instant using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z with no further fraction of a second.
     *
     * @param epochMillis  the number of milliseconds from the epoch of 1970-01-01T00:00:00Z
     * @return the created Instant
     * @throws IllegalArgumentException if nanoOfSecond is not in the range 0 to 999,999,999
     */
    public static Instant millisInstant(long epochMillis) {
        if (epochMillis < 0) {
            epochMillis++;
            long epochSeconds = epochMillis / 1000;
            int millis = ((int) (epochMillis % 1000));  // 0 to -999
            millis = 999 + millis;  // 0 to 999
            return new Instant(epochSeconds - 1, millis * 1000000);
        }
        return new Instant(epochMillis / 1000, ((int) (epochMillis % 1000)) * 1000000);
    }

    /**
     * Factory method to create an instance of Instant using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z and nanosecond fraction of millisecond.
     *
     * @param epochMillis  the number of milliseconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfMillisecond  the nanoseconds within the millisecond, must be positive
     * @return the created Instant
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
        super();
        this.epochSeconds = epochSeconds;
        this.nanoOfSecond = nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds from the epoch of 1970-01-01T00:00:00Z.
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

//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this Instant with the specified duration added.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param duration  the duration to add, not null
//     * @return a new updated Instant
//     */
//    public Instant plus(Duration duration) {
//        // TODO
//        return null;
//    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add
     * @return a new updated Instant
     */
    public Instant plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        return Instant.instant(MathUtils.safeAdd(epochSeconds, secondsToAdd) , nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToAdd  the milliseconds to add
     * @return a new updated Instant
     */
    public Instant plusMillis(long millisToAdd) {
        if (millisToAdd == 0) {
            return this;
        }
        long secondsToAdd = millisToAdd / 1000;
        // add: 0 to 999,000,000, subtract: 0 to -999,000,000
        int nos = ((int) (millisToAdd % 1000)) * 1000000;
        // add: 0 to 0 to 1998,999,999, subtract: -999,000,000 to 999,999,999
        nos += nanoOfSecond;
        if (nos < 0) {
            nos += 1000000000;  // subtract: 1,000,000 to 999,999,999
            secondsToAdd--;
        } else if (nos >= 1000000000) {
            nos -= 1000000000;  // add: 1 to 998,999,999
            secondsToAdd++;
        }
        return Instant.instant(MathUtils.safeAdd(epochSeconds, secondsToAdd) , nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Instant with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToAdd  the nanoseconds to add
     * @return a new updated Instant
     */
    public Instant plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        long secondsToAdd = nanosToAdd / 1000000000;
        // add: 0 to 999,999,999, subtract: 0 to -999,999,999
        int nos = (int) (nanosToAdd % 1000000000);
        // add: 0 to 0 to 1999,999,998, subtract: -999,999,999 to 999,999,999
        nos += nanoOfSecond;
        if (nos < 0) {
            nos += 1000000000;  // subtract: 1 to 999,999,999
            secondsToAdd--;
        } else if (nos >= 1000000000) {
            nos -= 1000000000;  // add: 1 to 999,999,999
            secondsToAdd++;
        }
        return Instant.instant(MathUtils.safeAdd(epochSeconds, secondsToAdd) , nos);
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
        int cmp = MathUtils.safeCompare(epochSeconds, otherInstant.epochSeconds);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(nanoOfSecond, otherInstant.nanoOfSecond);
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
            return this.epochSeconds == other.epochSeconds &&
                   this.nanoOfSecond == other.nanoOfSecond;
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
        return ((int) (epochSeconds ^ (epochSeconds >>> 32))) + 51 * nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this Instant using ISO-8601 representation.
     * <p>
     * The format of the returned string will be <code>yyyy-MM-ddTHH:mm:ss.SSSSSSSSSZ</code>.
     *
     * @return an ISO-8601 represntation of this Instant
     */
    @Override
    public String toString() {
        // TODO
        return "TODO";
    }

}
