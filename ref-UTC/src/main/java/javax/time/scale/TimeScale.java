package javax.time.scale;

import javax.time.scale.AbstractInstant;
import javax.time.scale.TimeScaleInstant;
import javax.time.Duration;
import javax.time.MathUtils;

/**  Describe time scales such as TAI, UTC, GPS.
 * In particular account for the effects of leap seconds.
 * @author Mark Thornton
 */
public abstract class TimeScale {
    protected static final int NANOS_PER_SECOND = 1000000000;


    protected abstract AbstractInstant fromTAI(AbstractInstant tsiTAI);
    protected abstract AbstractInstant toTAI(AbstractInstant t);

    public static Duration durationBetween(AbstractInstant a, AbstractInstant b) {
        if (a.getScale().equals(b.getScale())) {
            return a.getScale().difference(a, b);
        }
        /*
        Note: while it would be possible to convert both instants to a common time scale, the meaning
        of the resulting duration could depend on the selected time scale.
         */
        throw new IllegalArgumentException("Arguments have different TimeScale's");
    }

    /** Compute duration between two instants on this TimeScale.
     *
     * @param a first instant
     * @param b second instant
     * @return difference
     */
    protected Duration difference(AbstractInstant a, AbstractInstant b) {
        long seconds = MathUtils.safeSubtract(a.getEpochSeconds(), b.getEpochSeconds());
        int nanos = MathUtils.safeSubtract(a.getNanoOfSecond(), b.getNanoOfSecond());
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            seconds = MathUtils.safeDecrement(seconds);
        }
        return Duration.duration(seconds, nanos);
    }

    protected <T extends AbstractInstant<T>> T plus(T t, Duration duration) {
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

    protected <T extends AbstractInstant<T>> T minus(T t, Duration duration) {
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

    public TimeScaleInstant toScale(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
        if (leapSecond != 0) {
            throw new IllegalArgumentException(getName()+" does not permit leap seconds");
        }
        return TimeScaleInstant.instant(this, simpleEpochSeconds, nanoOfSecond);
    }

    public AbstractInstant toScale(AbstractInstant tsi) {
        if (tsi.getScale() != this) {
            tsi = fromTAI(tsi.getScale().toTAI(tsi));
        }
        return tsi;
    }

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
