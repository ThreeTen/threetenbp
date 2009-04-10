package javax.time;

import javax.time.calendar.CalendarConversionException;
import javax.time.scale.UTC;
import javax.time.scale.InstantFormat;
import java.io.Serializable;

/** An instant in time on a time scale.
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 * @author Mark Thornton
 */
public abstract class Instant implements InstantProvider, Comparable<Instant>, Serializable {

    /**
     * Constant for nanos per second.
     */
    private static final int NANOS_PER_SECOND = 1000000000;

    /**
     * The number of seconds from the epoch of 1970-01-01T00:00:00Z.
     */
    private final long epochSeconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanoOfSecond;

    /** UTC.SCALE.
     * UTC without leap seconds --- the most common variant in computer systems.
     * @return UTC.SCALE
     */
    public static TimeScale getDefaultScale() {
        return UTC.SCALE;
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

    /**
     * Factory method to create an instance of Instant, on the default time scale, using seconds from the
     * epoch of 1970-01-01T00:00:00Z with a zero nanosecond fraction.
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @return the created Instant, never null
     */
    public static Instant instant(long epochSeconds) {
        return getDefaultScale().instant(epochSeconds);
    }

    /**
     * Factory method to create an instance of Instant, on the default time scale, using seconds from the
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
        return getDefaultScale().instant(epochSeconds, nanoOfSecond);
    }

    /**
     * Factory method to create an instance of Instant, on the default time scale, using seconds from the
     * epoch of 1970-01-01T00:00:00Z and fraction of second.
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param fractionOfSecond  the fraction of the second, from -1 to 1 exclusive
     * @return the created Instant, never null
     * @throws IllegalArgumentException if fractionOfSecond is out of range
     */
    public static Instant instant(long epochSeconds, double fractionOfSecond) {
        return getDefaultScale().instant(epochSeconds, fractionOfSecond);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method to create an instance of Instant, on the default time scale, using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z with no further fraction of a second.
     *
     * @param epochMillis  the number of milliseconds from the epoch of 1970-01-01T00:00:00Z
     * @return the created Instant, never null
     */
    public static Instant millisInstant(long epochMillis) {
        return getDefaultScale().millisInstant(epochMillis);
    }

    /**
     * Factory method to create an instance of Instant, on the default time scale, using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z and nanosecond fraction of millisecond.
     *
     * @param epochMillis  the number of milliseconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfMillisecond  the nanoseconds within the millisecond, must be positive
     * @return the created Instant, never null
     * @throws IllegalArgumentException if nanoOfMillisecond is not in the range 0 to 999,999
     */
    public static Instant millisInstant(long epochMillis, int nanoOfMillisecond) {
        return getDefaultScale().millisInstant(epochMillis, nanoOfMillisecond);
    }

    protected Instant(long epochSeconds, int nanoOfSecond) {
        this.epochSeconds = epochSeconds;
        this.nanoOfSecond = nanoOfSecond;
    }

    /** TimeScale for this instant.
     *
     * @return associated time scale.
     */
    public abstract TimeScale getScale();

    /**
     * Gets the number of non leap seconds from the epoch of 1970-01-01T00:00:00Z.
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

    /** leap second after simple epoch instant.
     * This is never negative. It is used to disambiguate seconds which share the same epoch seconds value.
     * @return 1 during a positive leap second.
     */
    public int getLeapSecond() {
        return 0;
    }

    public Instant toInstant() {
        return this;
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
     * @throws javax.time.calendar.CalendarConversionException if the instant is too large or too
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

    public int compareTo(Instant otherInstant) {
        if (getScale().equals(otherInstant.getScale())) {
            int cmp = MathUtils.safeCompare(getEpochSeconds(), otherInstant.getEpochSeconds());
            if (cmp != 0) {
                return cmp;
            }
            cmp = getLeapSecond() - otherInstant.getLeapSecond();
            if (cmp != 0) {
                return cmp;
            }
            // The following can't overflow unless nanoOfSecond is outside its permitted range
            return getNanoOfSecond() - otherInstant.getNanoOfSecond();
        }
        throw new IllegalArgumentException("Can't compare Instants from different time scales");
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
            return this.getScale().equals(other.getScale()) &&
                   this.getEpochSeconds() == other.getEpochSeconds() &&
                   this.getNanoOfSecond() == other.getNanoOfSecond() &&
                   this.getLeapSecond() == other.getLeapSecond();
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

    /** Calculate duration from another instant.
     * To compute durations between instants which may be on different time scales, use the TimeScale.durationBetween
     * method. This is required because the result can depend on the TimeScale used to evaluate the difference. For
     * example the difference between 2008-12-31T23:59:59Z and 2009-01-01T:00:00Z is two seconds in most time scales
     * (and in reality), but only one second on time scales which ignore the leap second.
     * @param other another instant on the same time scale.
     * @return duration from other to this measured on the common time scale.
     */
    public Duration durationFrom(Instant other) {
        if (getScale().equals(other.getScale())) {
            return difference(other);
        }
        throw new IllegalArgumentException("Other duration on different time scale");
    }

    /** Compute duration to another instant on this TimeScale.
     * The default implementation assumes there are no leap seconds
     * @param b second instant on the same TimeScale
     * @return difference
     */
    protected Duration difference(Instant b) {
        assert getScale().equals(b.getScale());
        long seconds = MathUtils.safeSubtract(getEpochSeconds(), b.getEpochSeconds());
        int nanos = getNanoOfSecond() - b.getNanoOfSecond();
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            seconds = MathUtils.safeDecrement(seconds);
        }
        return Duration.duration(seconds, nanos);
    }

    protected Instant plus(long addSeconds, int addNanoOfSecond) {
        if (addSeconds == 0 && addNanoOfSecond == 0)
            return this;
        long seconds = MathUtils.safeAdd(getEpochSeconds(), addSeconds);
        int nanos = getNanoOfSecond() + addNanoOfSecond;
        if (nanos >= NANOS_PER_SECOND) {
            seconds = MathUtils.safeIncrement(seconds);
            nanos -= NANOS_PER_SECOND;
        }
        return getScale().uncheckedInstant(seconds, nanos);
    }

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
        return plus(duration.getSeconds(), duration.getNanoOfSecond());
    }

    /**
     * Returns a copy of this Instant with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant plusSeconds(long seconds) {
        return plus(seconds, 0);
    }

    /**
     * Returns a copy of this Instant with the specified number of milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millis  the milliseconds to add
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant plusMillis(long millis) {
        if (millis == 0)
            return this;
        long seconds = millis/1000;
        int m = (int)(millis%1000);
        if (m < 0) {
            m += 1000;
            seconds--;
        }
        return plus(seconds, m*1000000);
    }

    /**
     * Returns a copy of this Instant with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanoseconds to add
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant plusNanos(long nanos) {
        if (nanos == 0)
            return this;
        long seconds = nanos/NANOS_PER_SECOND;
        int n = (int)(nanos%NANOS_PER_SECOND);
        if (n < 0) {
            n += NANOS_PER_SECOND;
            seconds--;
        }
        return plus(seconds, n);
    }

    protected Instant minus(long subSeconds, int subNanoOfSecond) {
        if (subSeconds == 0 && subNanoOfSecond == 0)
            return this;
        long seconds = MathUtils.safeSubtract(getEpochSeconds(), subSeconds);
        int nanos = getNanoOfSecond() - subNanoOfSecond;
        if (nanos < 0) {
            seconds = MathUtils.safeDecrement(seconds);
            nanos += NANOS_PER_SECOND;
        }
        return getScale().uncheckedInstant(seconds, nanos);
    }

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
        return minus(duration.getSeconds(), duration.getNanoOfSecond());
    }

    /**
     * Returns a copy of this Instant with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to subtract
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant minusSeconds(long seconds) {
        return minus(seconds, 0);
    }

    /**
     * Returns a copy of this Instant with the specified number of milliseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millis  the milliseconds to subtract
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant minusMillis(long millis) {
        if (millis == 0)
            return this;
        long seconds = millis/1000;
        int m = (int)(millis%1000);
        if (m < 0) {
            m += 1000;
            seconds--;
        }
        return minus(seconds, m*1000000);
    }

    /**
     * Returns a copy of this Instant with the specified number of nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanoseconds to subtract
     * @return a new updated Instant, never null
     * @throws ArithmeticException if the result exceeds the storage capacity
     */
    public Instant minusNanos(long nanos) {
        if (nanos == 0)
            return this;
        long seconds = nanos/NANOS_PER_SECOND;
        int n = (int)(nanos%NANOS_PER_SECOND);
        if (n < 0) {
            n += NANOS_PER_SECOND;
            seconds--;
        }
        return minus(seconds, n);
    }

    /**
     * A string representation of this Instant using ISO-8601 representation.
     * <p>
     * The format of the returned string will be <code>yyyy-MM-ddTHH:mm:ss.SSSSSSSSSZ</code>.
     * If the TimeScale is not the default, then <code>[scale]</code> will be appended in place of 'Z'.
     *
     * @return an ISO-8601 representation of this Instant
     */
    public String toString() {
        return InstantFormat.getInstance().format(this);
    }
}
