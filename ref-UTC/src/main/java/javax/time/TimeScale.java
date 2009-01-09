package javax.time;

import javax.time.scale.UTC_NoEpochLeaps;
import javax.time.scale.AbstractInstant;
import javax.time.scale.TimeScaleInstant;

/**  Describe time scales such as TAI, UTC, GPS.
 * In particular account for the effects of leap seconds.
 * @author Mark Thornton
 */
public abstract class TimeScale {
    protected static final int NANOS_PER_SECOND = 1000000000;


    protected abstract AbstractInstant fromTAI(AbstractInstant tsiTAI);
    protected abstract AbstractInstant toTAI(AbstractInstant t);

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
