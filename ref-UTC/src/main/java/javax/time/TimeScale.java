package javax.time;

import javax.time.scale.TAI;
import javax.time.scale.TimeScaleFactory;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.util.*;

/**  Describe time scales such as TAI, UTC, GPS.
 * In particular account for the effects of leap seconds.
 * @author Mark Thornton
 */
public abstract class TimeScale implements Serializable {
    protected static final int NANOS_PER_SECOND = 1000000000;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // Most TimeScale's don't have any state, so just compare the class.
        // Object identity (==) suffices for classes
        return obj == this ||
                (obj != null && getClass() == obj.getClass());
    }

    /** 1970-01-01T00:00 in this time scale. */
    public abstract Instant getEpoch();

    /** replace by single instance */
    protected abstract Object readResolve() throws ObjectStreamException;

    /** compute instant in this time scale given a TAI instant.
     *
     * @param tsiTAI instant in TAI
     * @return instant on this time scale.
     */
    protected abstract Instant fromTAI(TAI.Instant tsiTAI);

    /** Compute TAI instant corresponding to an instant in this time scale.
     *
     * @param t instant in this scale
     * @return instant on TAI
     */
    protected abstract TAI.Instant toTAI(Instant t);

    /** Compute duration between two instants.
     * The difference is evaluated on this scale by converting the instants where necessary
     * @param a first instant
     * @param b second instant
     * @return duration between a and b
     */
    public Duration durationBetween(Instant a, Instant b) {
        return instant(a).difference(instant(b));
    }

    /** Instant in this scale from epoch seconds.
     * create instant in this time scale without checking the range of parameters. It used internally
     * where the parameters are known to be valid.
     * @param simpleEpochSeconds seconds from epoch <i>without</i> leap seconds. That is seconds calculated assuming
     * all days are exactly 86400 seconds long.
     * @param nanoOfSecond nanoseconds after the second
     * @return instant on this time scale
     */
    protected abstract Instant uncheckedInstant(long simpleEpochSeconds, int nanoOfSecond);

    /**
     * Factory method to create an instance of Instant using seconds from the
     * epoch of 1970-01-01T00:00:00Z with a zero nanosecond fraction.
     *
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @return the created Instant, never null
     */
    public Instant instant(long epochSeconds) {
        if (epochSeconds == 0) {
            return getEpoch();
        }
        return uncheckedInstant(epochSeconds, 0);
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
     * @param epochSeconds  the number of seconds from the epoch of 1970-01-01T00:00:00Z <i>without</i> leap seconds. That is seconds calculated assuming
     * all days are exactly 86400 seconds long.
     * @param nanoOfSecond  the nanoseconds within the second, -999,999,999 to 999,999,999
     * @return the created Instant on this time scale
     * @throws IllegalArgumentException if nanoOfSecond is out of range
     */
    public Instant instant(long epochSeconds, int nanoOfSecond) {
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
           return getEpoch();
        }
        return uncheckedInstant(epochSeconds, nanoOfSecond);
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
    public Instant instant(long epochSeconds, double fractionOfSecond) {
        if (fractionOfSecond <= -1 || fractionOfSecond >= 1) {
            throw new IllegalArgumentException("Fraction of second must be between -1 and 1 exclusive but was " + fractionOfSecond);
        }
        if (epochSeconds == 0 && fractionOfSecond == 0d) {
            return getEpoch();
        }
        int nanos = (int) Math.round(fractionOfSecond * NANOS_PER_SECOND);
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            epochSeconds = MathUtils.safeDecrement(epochSeconds);
        } else if (nanos == NANOS_PER_SECOND) {
			nanos = 0;
			epochSeconds = MathUtils.safeIncrement(epochSeconds);
		}
        return uncheckedInstant(epochSeconds, nanos);
    }

    /**
     * Factory method to create an instance of Instant using milliseconds from the
     * epoch of 1970-01-01T00:00:00Z with no further fraction of a second.
     *
     * @param epochMillis  the number of milliseconds from the epoch of 1970-01-01T00:00:00Z
     * @return the created Instant, never null
     */
    public Instant millisInstant(long epochMillis) {
        if (epochMillis < 0) {
            epochMillis++;
            long epochSeconds = epochMillis / 1000;
            int millis = ((int) (epochMillis % 1000));  // 0 to -999
            millis = 999 + millis;  // 0 to 999
            return uncheckedInstant(epochSeconds - 1, millis * 1000000);
        }
        if (epochMillis == 0) {
            return getEpoch();
        }
        return uncheckedInstant(epochMillis / 1000, ((int) (epochMillis % 1000)) * 1000000);
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
    public Instant millisInstant(long epochMillis, int nanoOfMillisecond) {
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
            return uncheckedInstant(epochSeconds - 1, millis * 1000000 + nanoOfMillisecond);
        }
        if (epochMillis == 0 && nanoOfMillisecond == 0) {
            return getEpoch();
        }
        return uncheckedInstant(epochMillis / 1000, ((int) (epochMillis % 1000)) * 1000000 + nanoOfMillisecond);
    }

    /** Instant in this scale from epoch seconds.
     * Times during a leap second are specified by giving simpleEpochSecond as the last second before the leap second,
     * and leapSecond the number of (positive) leap seconds added after it. To date leap seconds have all been positive
     * and only one at a time, thus the leapSecond parameter should be either zero or one. It is speculated that,
     * at some future time, double leap seconds or even leap hours may be required to keep the UTC time scale aligned
     * with UT1.
     * @param simpleEpochSeconds seconds from epoch <i>without</i> leap seconds. That is seconds calculated assuming
     * all days are exactly 86400 seconds long.
     * @param nanoOfSecond nanoseconds after the second
     * @param leapSecond leap second after the simpleEpoch second. Zero if not a leap second.
     * @return instant on this time scale
     * @throws IllegalArgumentException if a leap second is specified and either the time scale doesn't use leap seconds
     * or there is no leap second following the second specified by <code>simpleEpochSeconds</code>.
     */
    public Instant instant(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
        if (leapSecond != 0) {
            throw new IllegalArgumentException(getName()+" does not permit leap seconds");
        }
        return instant(simpleEpochSeconds, nanoOfSecond);
    }

    /** Convert instant to this scale.
     *
     * @param tsi Instant in any time scale
     * @return An equivalent instant in this timescale
     */
    public Instant instant(Instant tsi) {
        if (!equals(tsi.getScale())) {
            tsi = fromTAI(tsi.getScale().toTAI(tsi));
        }
        return tsi;
    }

    /** TimeScale of given name.
     * @param name name of TimeScale required.
     * @return an instance of required time scale (if it exists).
     */
    public static TimeScale forName(String name) {
        /*
        This implementation isn't very efficient. Needs thought on how to handle situations where the set
        of available TimeScale's changes due to ClassLoader changes.
         */
        ServiceLoader<TimeScaleFactory> loader = ServiceLoader.load(TimeScaleFactory.class);
        for (TimeScaleFactory factory: loader) {
            TimeScale ts = factory.getTimeScale(name);
            if (ts != null)
                return ts;
        }
        throw new IllegalArgumentException("No TimeScale \""+name+"\"");
    }

    public static Set<String> getAvailableNames() {
        Set<String> available = new TreeSet<String>();
        for (TimeScaleFactory factory: ServiceLoader.load(TimeScaleFactory.class)) {
            available.addAll(factory.getNames());
        }
        return available;
    }

    public abstract String getName();
}
