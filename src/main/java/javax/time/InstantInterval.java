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
 * An interval of instants.
 * <p>
 * InstantInterval is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public abstract class InstantInterval
        implements Serializable {

    /**
     * Singleton instance of the empty interval.
     */
    public static final InstantInterval EMPTY = new Empty();
    /**
     * Singleton instance of the all-time interval.
     */
    public static final InstantInterval ALL = new All();
    /**
     * A serialization identifier for this instance.
     */
    private static final long serialVersionUID = 2751875381768L;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>InstantInterval</code>.
     *
     * @param startInclusive  the start of the interval, inclusive, not null
     * @param endExclusive  the end of the interval, exclusive, not null
     * @return the created interval, never null
     */
    public static InstantInterval intervalBetween(Instant startInclusive, Instant endExclusive) {
        return intervalBetween(startInclusive, true, endExclusive, false);
    }

    /**
     * Obtains an instance of <code>InstantInterval</code>, controlling
     * the inclusive/exclusive status of each end.
     *
     * @param start  the start of the interval, not null
     * @param startInclusive  whether the start is inclusive
     * @param end  the end of the interval, not null
     * @param endInclusive  whether the end is inclusive
     * @return the created interval, never null
     */
    public static InstantInterval intervalBetween(Instant start, boolean startInclusive, Instant end, boolean endInclusive) {
        if (start == null || end == null) {
            throw new NullPointerException("The start and end points must not be null");
        }
        int cmp = start.compareTo(end);
        if (cmp == 0) {
            return EMPTY;
        }
        if (cmp > 0) {
            throw new IllegalArgumentException("The start instant is after the end instant");
        }
        return new Dual(intervalFrom(start, startInclusive), intervalTo(end, endInclusive));
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>InstantInterval</code> where the interval
     * contains all instants from, and including, the specified instant.
     *
     * @param startInclusive  the start of the interval, exclusive, not null
     * @return the created interval, never null
     */
    public static InstantInterval intervalFrom(Instant startInclusive) {
        return intervalTo(startInclusive, true);
    }

    /**
     * Obtains an instance of <code>InstantInterval</code> where the interval
     * contains all instants from the specified instant.
     *
     * @param start  the start of the interval, not null
     * @param startInclusive  whether the start is inclusive
     * @return the created interval, never null
     */
    public static InstantInterval intervalFrom(Instant start, boolean startInclusive) {
        if (startInclusive) {
            return new LessThanExclusive(start);  // TODO
        } else {
            return new LessThanInclusive(start);  // TODO
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>InstantInterval</code> where the interval
     * contains all instants up to, but not including, the specified instant.
     *
     * @param endExclusive  the end of the interval, exclusive, not null
     * @return the created interval, never null
     */
    public static InstantInterval intervalTo(Instant endExclusive) {
        return intervalTo(endExclusive, false);
    }

    /**
     * Obtains an instance of <code>InstantInterval</code> where the interval
     * contains all instants up to the specified instant.
     *
     * @param end  the end of the interval, not null
     * @param endInclusive  whether the end is inclusive
     * @return the created interval, never null
     */
    public static InstantInterval intervalTo(Instant end, boolean endInclusive) {
        if (endInclusive) {
            return new LessThanExclusive(end);
        } else {
            return new LessThanInclusive(end);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     */
    private InstantInterval() {
    }

    //-----------------------------------------------------------------------
    /**
     * Is the interval empty.
     * <p>
     * An empty interval has unusual properties, as it is not located at any
     * point on the time-line, does not contain any other interval and has
     * a zero duration.
     *
     * @return the start of the interval, null if start is unbounded
     */
    public boolean isEmpty() {
        return false;  // subclasses override this
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the start of the interval.
     * <p>
     * If the start of the interval is unbounded, this will return null.
     *
     * @return the start of the interval, null if start is unbounded
     */
    public Instant getStart() {
        return null;  // subclasses override this
    }

    /**
     * Is the start included in the interval.
     * <p>
     * If the start of the interval is unbounded, this will return true.
     *
     * @return true if the start is inclusive, false if exclusive
     */
    public boolean isStartInclusive() {
        return false;  // subclasses override this
    }

    /**
     * Is the start of the interval unbounded.
     * <p>
     * An unbounded start implies that the interval begins at the start of time.
     *
     * @return true if the start is unbounded, false if it is bounded
     */
    public boolean isStartUnbounded() {
        return false;  // subclasses override this
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the end of the interval.
     * <p>
     * If the end of the interval is unbounded, this will return null.
     *
     * @return the end of the interval, null if end is unbounded
     */
    public Instant getEnd() {
        return null;
    }

    /**
     * Is the end included in the interval.
     * <p>
     * If the end of the interval is unbounded, this will return true.
     *
     * @return true if the end is inclusive, false if exclusive
     */
    public boolean isEndInclusive() {
        return false;  // subclasses override this
    }

    /**
     * Is the end of the interval unbounded.
     * <p>
     * An unbounded end implies that the interval ends at the end of time.
     *
     * @return true if the end is unbounded, false if it is bounded
     */
    public boolean isEndUnbounded() {
        return false;  // subclasses override this
    }

    //-----------------------------------------------------------------------
    /**
     * Is this interval before the specified instant.
     *
     * @param instant  the instant to compare to, not null
     * @return true if this interval is completely before the instant
     */
    public boolean isBefore(ReadableInstant instant) {
        return false;   // subclasses override this
    }

    /**
     * Is this interval after the specified instant.
     *
     * @param instant  the instant to compare to, not null
     * @return true if this interval is completely after the instant
     */
    public boolean isAfter(ReadableInstant instant) {
        return false;   // subclasses override this
    }

    /**
     * Does this interval contain the specified instant.
     *
     * @param instant  the instant to compare to, not null
     * @return true if this interval contains the instant
     */
    public boolean contains(ReadableInstant instant) {
        return false;   // subclasses override this
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Is this interval before the specified interval.
//     *
//     * @param interval  the interval to compare to, not null
//     * @return true if this interval is completely before the other
//     */
//    public boolean isBefore(InstantInterval interval) {
//        if (interval.isEmpty()) {
//            return false;
//        }
//        if (interval.isStartUnbounded()) {
//            return false;
//        }
//        if (interval.isStartInclusive()) {
//            return isBefore(interval.getStart());
//        } else {
//            return isBefore(interval.getStart());
//        }
//    }
//
//    /**
//     * Is this interval after the specified interval.
//     *
//     * @param interval  the interval to compare to, not null
//     * @return true if this interval is completely after the other
//     */
//    public abstract boolean isAfter(InstantInterval interval);
//
//    /**
//     * Does this interval contain the specified interval.
//     *
//     * @param interval  the interval to compare to, not null
//     * @return true if this interval completely contains the interval
//     */
//    public abstract boolean contains(InstantInterval interval);
//
//    /**
//     * Does this interval overlap the specified interval.
//     *
//     * @param interval  the interval to compare to, not null
//     * @return true if this interval overlaps the interval
//     */
//    public boolean overlaps(InstantInterval interval) {
//        return isBefore(interval) == false && isAfter(interval) == false;
//    }

    //-----------------------------------------------------------------------
    /**
     * Is this interval equal to the specified interval.
     *
     * @param other  the other interval to compare to, null returns false
     * @return true if this interval is equal to the specified interval
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof InstantInterval) {
            InstantInterval interval = (InstantInterval) other;
            return getStart().equals(interval.getStart()) &&
                    getEnd().equals(interval.getEnd()) &&
                    isStartInclusive() == interval.isStartInclusive() &&
                    isEndInclusive() == interval.isEndInclusive() &&
                    isStartUnbounded() == interval.isStartUnbounded() &&
                    isEndUnbounded() == interval.isEndUnbounded();
        }
        return false;
    }

    /**
     * A hashcode for this interval.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return getStart().hashCode() ^ getEnd().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the string form of the interval.
     *
     * @return the string form of the year
     */
    @Override
    public String toString() {
        return super.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Class implementing the empty interval.
     */
    private static final class Empty extends InstantInterval {
        /** {@inheritDoc} */
        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Class implementing the empty interval.
     */
    private static final class All extends InstantInterval {
        /** {@inheritDoc} */
        @Override
        public boolean isStartInclusive() {
            return true;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isStartUnbounded() {
            return true;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isEndInclusive() {
            return true;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isEndUnbounded() {
            return true;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Class implementing the less than inclusive interval.
     */
    private static final class LessThanInclusive extends InstantInterval {
        /** The maximum value in the interval. */
        private Instant maximum;
        /**
         * Constructor.
         * @param maximum  the maximum instant, inclusive, not null
         */
        private LessThanInclusive(Instant maximum) {
            if (maximum == null) {
                throw new NullPointerException("The maximum point must not be null");
            }
            this.maximum = maximum;
        }
        /** {@inheritDoc} */
        @Override
        public Instant getStart() {
            return null;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isStartUnbounded() {
            return true;
        }
        /** {@inheritDoc} */
        @Override
        public Instant getEnd() {
            return maximum;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isEndInclusive() {
            return true;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isBefore(ReadableInstant instant) {
            return maximum.compareTo(instant.toInstant()) < 0;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isAfter(ReadableInstant instant) {
            return false;
        }
        /** {@inheritDoc} */
        @Override
        public boolean contains(ReadableInstant instant) {
            return instant.toInstant().compareTo(maximum) <= 0;
        }
        /** {@inheritDoc} */
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof LessThanInclusive) {
                return maximum.equals(((LessThanInclusive) other).maximum);
            }
            return false;
        }
        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return getClass().hashCode() ^ maximum.hashCode();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Class implementing the less than exclusive interval.
     */
    private static final class LessThanExclusive extends InstantInterval {
        /** The maximum value in the interval. */
        private Instant maximum;
        /**
         * Constructor.
         * @param maximum  the maximum instant, exclusive, not null
         */
        private LessThanExclusive(Instant maximum) {
            if (maximum == null) {
                throw new NullPointerException("The maximum point must not be null");
            }
            this.maximum = maximum;
        }
        /** {@inheritDoc} */
        @Override
        public Instant getStart() {
            return null;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isStartUnbounded() {
            return true;
        }
        /** {@inheritDoc} */
        @Override
        public Instant getEnd() {
            return maximum;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isBefore(ReadableInstant instant) {
            return maximum.compareTo(instant.toInstant()) <= 0;
        }
        /** {@inheritDoc} */
        @Override
        public boolean isAfter(ReadableInstant instant) {
            return false;
        }
        /** {@inheritDoc} */
        @Override
        public boolean contains(ReadableInstant instant) {
            return instant.toInstant().compareTo(maximum) < 0;
        }
        /** {@inheritDoc} */
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof LessThanExclusive) {
                return maximum.equals(((LessThanExclusive) other).maximum);
            }
            return false;
        }
        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return getClass().hashCode() ^ maximum.hashCode();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Class implementing the less than exclusive interval.
     */
    private static final class Dual extends InstantInterval {
        /** The lower interval. */
        private InstantInterval startAndAfter;
        /** The higher interval. */
        private InstantInterval endAndBefore;
        /**
         * Constructor.
         * @param start  the start interval, not null
         * @param end  the end interval, not null
         */
        private Dual(InstantInterval start, InstantInterval end) {
            this.startAndAfter = start;
            this.endAndBefore = end;
        }
        /** {@inheritDoc} */
        @Override
        public Instant getStart() {
            return startAndAfter.getStart();
        }
        /** {@inheritDoc} */
        @Override
        public boolean isStartInclusive() {
            return startAndAfter.isStartInclusive();
        }
        /** {@inheritDoc} */
        @Override
        public Instant getEnd() {
            return endAndBefore.getEnd();
        }
        /** {@inheritDoc} */
        @Override
        public boolean isEndInclusive() {
            return endAndBefore.isEndInclusive();
        }
        /** {@inheritDoc} */
        @Override
        public boolean isBefore(ReadableInstant instant) {
            return endAndBefore.isBefore(instant);
        }
        /** {@inheritDoc} */
        @Override
        public boolean isAfter(ReadableInstant instant) {
            return startAndAfter.isAfter(instant);
        }
        /** {@inheritDoc} */
        @Override
        public boolean contains(ReadableInstant instant) {
            return startAndAfter.contains(instant) && endAndBefore.contains(instant);
        }
        /** {@inheritDoc} */
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof Dual) {
                Dual dual = (Dual) other;
                return startAndAfter.equals(dual.startAndAfter) &&
                        endAndBefore.equals(dual.endAndBefore);
            }
            return false;
        }
        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return getClass().hashCode() ^ startAndAfter.hashCode() ^ endAndBefore.hashCode();
        }
    }

}
