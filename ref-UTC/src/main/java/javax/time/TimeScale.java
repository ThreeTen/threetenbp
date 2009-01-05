package javax.time;

import javax.time.scale.TimeScaleInstant;
import javax.time.scale.UTC_NoEpochLeaps;

/**  Describe time scales such as TAI, UTC, GPS.
 * In particular account for the effects of leap seconds.
 * @author Mark Thornton
 */
public abstract class TimeScale {
    protected static final int NANOS_PER_SECOND = 1000000000;

    /** Coordinated Universal Time without leap seconds.
     * Epoch seconds do not include leap seconds. */
    public static final TimeScale DEFAULT = UTC_NoEpochLeaps.INSTANCE;

    protected static Instant taiInstant(long taiEpochSeconds, int taiNanosOfSecond) {
        return Instant.newInstant(taiEpochSeconds, taiNanosOfSecond);
    }

    protected static Instant checkedTaiInstant(long taiEpochSeconds, int taiNanoOfSecond) {
        if (taiNanoOfSecond >= NANOS_PER_SECOND) {
            throw new IllegalArgumentException("Nanosecond fraction must not be more than 999,999,999 but was " + taiNanoOfSecond);
        }
        if (taiNanoOfSecond < 0) {
            taiNanoOfSecond += NANOS_PER_SECOND;
            if (taiNanoOfSecond <= 0) {
                throw new IllegalArgumentException("Nanosecond fraction must not be less than -999,999,999 but was " + (taiNanoOfSecond-NANOS_PER_SECOND));
            }
            taiEpochSeconds = MathUtils.safeDecrement(taiEpochSeconds);
        }
        return Instant.newInstant(taiEpochSeconds, taiNanoOfSecond);
    }

    protected static long getTaiEpochSeconds(Instant t) {
        return t.getRawEpochSeconds();
    }

    protected static int getTaiNanosOfSecond(Instant t) {
        return t.getRawNanoOfSecond();
    }

    /** TimeScale of given name.
     *
     * @param name name of TimeScale required.
     * @return an instance of required time scale (if it exists).
     */
    public static TimeScale forName(String name) {
        throw new UnsupportedOperationException("TODO");
    }

    public abstract String getName();

    public abstract TimeScaleInstant getTimeScaleInstant(Instant t);

    /** Epoch seconds in this time scale for given instant.
     * The default implementation is getTimeScaleInstant(t).getEpochSeconds().
     * This will usually be overridden for greater efficiency */
    public long getEpochSeconds(Instant t) {
        return getTimeScaleInstant(t).getEpochSeconds();
    }

    /** Number of nanoseconds after the second for Instant.
     * The default implementation is getTimeScaleInstant(t).getNanoOfSecond().
     * This will usually be overridden for greater efficiency */
    public int getNanoOfSecond(Instant t) {
        return getTimeScaleInstant(t).getNanoOfSecond();
    }

    /** Create an Instant corresponding to epochSeconds */
    public abstract Instant instant(long epochSeconds, int nanoOfSecond);

    /** Create Instant corresponding to a TimeScaleInstant.
     * If the TimeScaleInstant does not include leap seconds in epochSeconds,
     * then the TimeScale will compute the appropriate value where required.
     * @param tsi
     * @return
     */
    public abstract Instant instant(TimeScaleInstant tsi);
}
