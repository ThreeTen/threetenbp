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
 * A duration between two instants on the time-line.
 * <p>
 * The Java Time Framework models time as a series of instantaneous events,
 * known as instants, along a single time-line. This class represents the
 * duration between two of those instants.
 * <p>
 * Each instant is theoretically an instantaneous event, however for practicality
 * a precision of nanoseconds has been chosen. As a result, the duration class also
 * has a maximum preciion of nanoseconds.
 *
 * @author Stephen Colebourne
 */
public class Duration implements Comparable<Duration>, Serializable {
    // TODO: Minus methods (as per plus methods)
    // TODO: Duration class integration
    // TODO: Leap seconds (document or implement)
    // TODO: Serialized format
    // TODO: Evaluate hashcode
    // TODO: Optimise to 2 private subclasses (second/nano & millis)
    // TODO: Consider BigDecimal

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
    private final long durationSeconds;
    /**
     * The number of nanoseconds in the duration, expressed as a fraction of the
     * number of seconds. This is always positive, and never exceeds 999,999,999.
     */
    private final int nanoOfSecond;

    //-----------------------------------------------------------------------
    /**
     * Factory method to create an instance of Duration using seconds with
     * a zero nanosecond fraction.
     *
     * @param epochSeconds  the number of seconds
     * @return the created Duration
     */
    public static Duration instant(long epochSeconds) {
        return new Duration(epochSeconds, 0);
    }

    /**
     * Factory method to create an instance of Duration using seconds and
     * nanosecond fraction of second.
     *
     * @param epochSeconds  the number of seconds
     * @param nanoOfSecond  the nanoseconds within the second, must be positive
     * @return the created Duration
     * @throws IllegalArgumentException if nanoOfSecond is not in the range 0 to 999,999,999
     */
    public static Duration instant(long epochSeconds, int nanoOfSecond) {
        if (nanoOfSecond < 0) {
            throw new IllegalArgumentException("NanoOfSecond must be positive but was " + nanoOfSecond);
        }
        if (nanoOfSecond > 999999999) {
            throw new IllegalArgumentException("NanoOfSecond must not be more than 999,999,999 but was " + nanoOfSecond);
        }
        return new Duration(epochSeconds, nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method to create an instance of Duration using milliseconds
     * with no further fraction of a second.
     *
     * @param epochMillis  the number of milliseconds
     * @return the created Duration
     * @throws IllegalArgumentException if nanoOfSecond is not in the range 0 to 999,999,999
     */
    public static Duration millisDuration(long epochMillis) {
        if (epochMillis < 0) {
            epochMillis++;
            long epochSeconds = epochMillis / 1000;
            int millis = ((int) (epochMillis % 1000));  // 0 to -999
            millis = 999 + millis;  // 0 to 999
            return new Duration(epochSeconds - 1, millis * 1000000);
        }
        return new Duration(epochMillis / 1000, ((int) (epochMillis % 1000)) * 1000000);
    }

    /**
     * Factory method to create an instance of Duration using milliseconds
     * and nanosecond fraction of millisecond.
     *
     * @param epochMillis  the number of milliseconds
     * @param nanoOfMillisecond  the nanoseconds within the millisecond, must be positive
     * @return the created Duration
     * @throws IllegalArgumentException if nanoOfMillisecond is not in the range 0 to 999,999
     */
    public static Duration millisDuration(long epochMillis, int nanoOfMillisecond) {
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
            return new Duration(epochSeconds - 1, millis * 1000000 + nanoOfMillisecond);
        }
        return new Duration(epochMillis / 1000, ((int) (epochMillis % 1000)) * 1000000 + nanoOfMillisecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance of Duration using seconds from the epoch of
     * 1970-01-01T00:00:00Z and nanosecond fraction of second.
     *
     * @param epochSeconds  the number of seconds from the epoch
     * @param nanoOfSecond  the nanoseconds within the second, must be positive
     */
    private Duration(long epochSeconds, int nanoOfSecond) {
        super();
        this.durationSeconds = epochSeconds;
        this.nanoOfSecond = nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of seconds in this duration.
     * <p>
     * Durations on the time-line after the epoch are positive, earlier are negative.
     *
     * @return the seconds from the epoch
     */
    public long getEpochSeconds() {
        return durationSeconds;
    }

    /**
     * Gets the number of nanosecond fraction of seconds in this duration.
     *
     * @return the nanoseconds within the second, always positive, never exceeds 999,999,999
     */
    public int getNanoOfSecond() {
        return nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, not null
     * @return a new updated Duration
     */
    public Duration plus(Duration duration) {
        long secsToAdd = duration.durationSeconds;
        int nanosToAdd = duration.nanoOfSecond;
        if (secsToAdd == 0 && nanosToAdd == 0) {
            return this;
        }
        long secs = MathUtils.safeAdd(durationSeconds, secsToAdd);
        if (nanosToAdd == 0) {
            return new Duration(secs, nanoOfSecond);
        }
        int nos = nanoOfSecond + nanosToAdd;
        if (nos > NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;
            MathUtils.safeAdd(secs, 1);
        }
        return new Duration(secs, nos);
     }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add
     * @return a new updated Duration
     */
    public Duration plusSeconds(long secondsToAdd) {
        if (secondsToAdd == 0) {
            return this;
        }
        return new Duration(MathUtils.safeAdd(durationSeconds, secondsToAdd) , nanoOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified number of milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToAdd  the milliseconds to add
     * @return a new updated Duration
     */
    public Duration plusMillis(long millisToAdd) {
        if (millisToAdd == 0) {
            return this;
        }
        long secondsToAdd = millisToAdd / 1000;
        // add: 0 to 999,000,000, subtract: 0 to -999,000,000
        int nos = ((int) (millisToAdd % 1000)) * 1000000;
        // add: 0 to 0 to 1998,999,999, subtract: -999,000,000 to 999,999,999
        nos += nanoOfSecond;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;  // subtract: 1,000,000 to 999,999,999
            secondsToAdd--;
        } else if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;  // add: 1 to 998,999,999
            secondsToAdd++;
        }
        return new Duration(MathUtils.safeAdd(durationSeconds, secondsToAdd) , nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this Duration with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToAdd  the nanoseconds to add
     * @return a new updated Duration
     */
    public Duration plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        long secondsToAdd = nanosToAdd / NANOS_PER_SECOND;
        // add: 0 to 999,999,999, subtract: 0 to -999,999,999
        int nos = (int) (nanosToAdd % NANOS_PER_SECOND);
        // add: 0 to 0 to 1999,999,998, subtract: -999,999,999 to 999,999,999
        nos += nanoOfSecond;
        if (nos < 0) {
            nos += NANOS_PER_SECOND;  // subtract: 1 to 999,999,999
            secondsToAdd--;
        } else if (nos >= NANOS_PER_SECOND) {
            nos -= NANOS_PER_SECOND;  // add: 1 to 999,999,999
            secondsToAdd++;
        }
        return new Duration(MathUtils.safeAdd(durationSeconds, secondsToAdd) , nos);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this Duration to another.
     *
     * @param otherDuration  the other instant to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if otherDuration is null
     */
    public int compareTo(Duration otherDuration) {
        int cmp = MathUtils.safeCompare(durationSeconds, otherDuration.durationSeconds);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(nanoOfSecond, otherDuration.nanoOfSecond);
    }

    /**
     * Is this Duration after the specified one.
     *
     * @param otherDuration  the other instant to compare to, not null
     * @return true if this instant is after the specified instant
     * @throws NullPointerException if otherDuration is null
     */
    public boolean isAfter(Duration otherDuration) {
        return compareTo(otherDuration) > 0;
    }

    /**
     * Is this Duration before the specified one.
     *
     * @param otherDuration  the other instant to compare to, not null
     * @return true if this instant is before the specified instant
     * @throws NullPointerException if otherDuration is null
     */
    public boolean isBefore(Duration otherDuration) {
        return compareTo(otherDuration) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this Duration equal to that specified.
     *
     * @param otherDuration  the other instant, null returns false
     * @return true if the other instant is equal to this one
     */
    @Override
    public boolean equals(Object otherDuration) {
        if (this == otherDuration) {
            return true;
        }
        if (otherDuration instanceof Duration) {
            Duration other = (Duration) otherDuration;
            return this.durationSeconds == other.durationSeconds &&
                   this.nanoOfSecond == other.nanoOfSecond;
        }
        return false;
    }

    /**
     * A hashcode for this Duration.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return ((int) (durationSeconds ^ (durationSeconds >>> 32))) + 51 * nanoOfSecond;
    }

    //-----------------------------------------------------------------------
    /**
     * A string representation of this Duration using ISO-8601 seconds based
     * representation.
     * <p>
     * The format of the returned string will be <code>PnS</code> where n is
     * the seconds and fractional seconds of the duration.
     *
     * @return an ISO-8601 represntation of this Duration
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(24);
        buf.append('P');
        buf.append(durationSeconds);
        if (nanoOfSecond > 0) {
            int pos = buf.length();
            buf.append(nanoOfSecond + NANOS_PER_SECOND);
            while (buf.charAt(buf.length() - 1) == '0') {
                buf.setLength(buf.length() - 1);
            }
            buf.setCharAt(pos, '.');
        }
        buf.append('S');
        return buf.toString();
    }

//    //-----------------------------------------------------------------------
//    private static final class Millis extends Duration {
//        private final long millis;
//        public Millis(long millis) {
//            super(0, 0);
//            this.millis = millis;
//        }
//        @Override
//        public long getEpochSeconds() {
//            return millis / 1000;
//        }
//        @Override
//        public int getNanoOfSecond() {
//            return ((int) millis % 1000) * 1000000;
//        }
//        @Override
//        public Duration plus(Duration duration) {
//            if (duration instanceof Millis) {
//            } else {
//                return super.plus(duration);
//            }
//        }
//        @Override
//        public Duration plusSeconds(long secondsToAdd) {
//            if (secondsToAdd == 0) {
//                return this;
//            }
//            return new Millis(MathUtils.safeAdd(millis, millisToAdd));
//        }
//        @Override
//        public Duration plusMillis(long millisToAdd) {
//            if (millisToAdd == 0) {
//                return this;
//            }
//            long sum = millis + millisToAdd;
//            if ((millis ^ sum) < 0 && (millis ^ millisToAdd) >= 0) {
//                return new Duration(getEpochSeconds(), getNanoOfSecond()).plusMillis(millisToAdd);
//            } else {
//                return new Millis(sum);
//            }
//        }
//        @Override
//        public Duration plusNanos(long nanosToAdd) {
//            if (nanosToAdd == 0) {
//                return this;
//            }
//            long nanos = MathUtils.safeMultiply(millis, millisToAdd);
//            return new Nanos();
//        }
//        @Override
//        public int compareTo(Duration otherDuration) {
//            if (otherDuration instanceof Millis) {
//                return MathUtils.safeCompare(millis, ((Millis) otherDuration).millis);
//            } else {
//                return super.compareTo(otherDuration);
//            }
//        }
//        @Override
//        public boolean equals(Object otherDuration) {
//            if (otherDuration instanceof Millis) {
//                return millis == ((Millis) otherDuration).millis;
//            } else {
//                return super.equals(otherDuration);
//            }
//        }
//        @Override
//        public int hashCode() {
//            return super.hashCode();
//        }
//        @Override
//        public String toString() {
//            return super.toString();
//        }
//    }
//
//    public static class Nano96 {
//        static final long MASK = 0xFFFFFFFFL;
//        int low;
//        long high;
//        Nano96(int low, long high) {
//            this.low = low;
//            this.high = high;
//        }
//        Nano96 add(Nano96 a) {
//            long sum = (low & MASK) + (a.low & MASK);
//            return new Nano96((int) sum, high + a.high + (sum >>> 32));
//        }
//        @Override
//        public String toString() {
//            return high + " " + low;
//        }
//        public static void main(String[] args) {
//            Nano96 a = new Nano96(1, 0);
//            Nano96 b = new Nano96(-2, 0);
//            Nano96 c = a.add(b);
//            System.out.println(c);
//        }
//    }
}
