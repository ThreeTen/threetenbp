package javax.time.scale;

import javax.time.Duration;
import javax.time.MathUtils;

/**  Describe time scales such as TAI, UTC, GPS.
 * In particular account for the effects of leap seconds.
 * Could parameterise this class by its associated subclass of AbstractInstant.
 * @author Mark Thornton
 */
public abstract class TimeScale<T extends AbstractInstant<T>> {
    protected static final int NANOS_PER_SECOND = 1000000000;

    /** 1970-01-01T00:00 in this time scale. */
    public abstract T getEpoch();

    /** compute instant in this time scale given a TAI instant.
     *
     * @param tsiTAI instant in TAI
     * @return instant on this time scale.
     */
    protected abstract T fromTAI(TAI.Instant tsiTAI);

    /** Compute TAI instant corresponding to an instant in this time scale.
     *
     * @param t instant in this scale
     * @return instant on TAI
     */
    protected abstract TAI.Instant toTAI(T t);

    /** Compute duration between two instants.
     * The difference is evaluated on this scale by converting the instants where necessary
     * @param a first instant
     * @param b second instant
     * @return duration between a and b
     */
    public Duration durationBetween(AbstractInstant a, AbstractInstant b) {
        return difference(instant(a), instant(b));
    }

    /** Compute duration between two instants on this TimeScale.
     * The default implementation assumes there are no leap seconds
     * @param a first instant
     * @param b second instant
     * @return difference
     */
    protected Duration difference(T a, T b) {
        long seconds = MathUtils.safeSubtract(a.getEpochSeconds(), b.getEpochSeconds());
        int nanos = a.getNanoOfSecond() - b.getNanoOfSecond();
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            seconds = MathUtils.safeDecrement(seconds);
        }
        return Duration.duration(seconds, nanos);
    }

    /** add a duration to an instant in this time scale.
     * The default implementation assumes there are no leap seconds.
     * @param t base instant
     * @param duration duration to add
     * @return sum
     */
    protected <U extends AbstractInstant<U>> U plus(U t, Duration duration) {
        if (duration.equals(Duration.ZERO))
            return t;
        long seconds = MathUtils.safeAdd(t.getEpochSeconds(), duration.getSeconds());
        int nanos = t.getNanoOfSecond() + duration.getNanoOfSecond();
        if (nanos >= NANOS_PER_SECOND) {
            seconds = MathUtils.safeIncrement(seconds);
            nanos -= NANOS_PER_SECOND;
        }
        return t.factory(seconds, nanos, 0);
    }

    /** Subtract a duration from an instant in this time scale.
     * The default implementation assumes there are no leap seconds.
     * @param t base instant
     * @param duration duration to subtract
     * @return t-duration
     */
    protected <U extends AbstractInstant<U>> U minus(U t, Duration duration) {
        if (duration.equals(Duration.ZERO))
            return t;
        long seconds = MathUtils.safeSubtract(t.getEpochSeconds(), duration.getSeconds());
        int nanos = t.getNanoOfSecond() - duration.getNanoOfSecond();
        if (nanos < 0) {
            seconds = MathUtils.safeDecrement(seconds);
            nanos += NANOS_PER_SECOND;
        }
        return t.factory(seconds, nanos, 0);
    }

    /** Instant in this scale from epoch seconds.
     *
     * @param simpleEpochSeconds seconds from epoch <i>without</i> leap seconds.
     * @param nanoOfSecond nanoseconds after the second
     * @return instant on this time scale
     */
    public T instant(long simpleEpochSeconds, int nanoOfSecond) {
        return instant(simpleEpochSeconds, nanoOfSecond, 0);
    }

    /** Instant in this scale from epoch seconds.
     * @param simpleEpochSeconds seconds from epoch <i>without</i> leap seconds.
     * @param nanoOfSecond nanoseconds after the second
     * @param leapSecond leap second after the simpleEpoch second. Zero if not a leap second.
     * @return instant on this time scale
     */
    public T instant(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
        if (leapSecond != 0) {
            throw new IllegalArgumentException(getName()+" does not permit leap seconds");
        }
        return getEpoch().factory(simpleEpochSeconds, nanoOfSecond, 0);
    }

    /** Convert instant to this scale.
     *
     * @param tsi Instant in any time scale
     * @return An equivalent instant in this timescale
     */
    public T instant(AbstractInstant tsi) {
        if (!equals(tsi.getScale())) {
            tsi = fromTAI(tsi.getScale().toTAI(tsi));
        }
        return (T)tsi;
    }

    /** Compute simple epoch seconds given an instant in this time scale.
     * The javax.time.Instant class doesn't store simpleEpochSeconds, so it needs to be computed when required.
     * For most time scales this is trivial.
     * @param t
     * @return
     */
    protected long getSimpleEpochSeconds(AbstractInstant t) {
        return t.getEpochSeconds();
    }

    /** TimeScale of given name.
     * TODO: Not yet implemented
     * @param name name of TimeScale required.
     * @return an instance of required time scale (if it exists).
     */
    public static TimeScale forName(String name) {
        throw new UnsupportedOperationException("TODO");
    }

    public abstract String getName();
}
