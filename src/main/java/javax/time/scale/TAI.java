package javax.time.scale;

import javax.time.TimeScale;
import javax.time.Instant;
import java.io.Serializable;

/** International Atomic Time.
 *  * @author Mark Thornton
 */
public class TAI extends TimeScale implements Serializable {
    public static final TAI INSTANCE = new TAI();

    private TAI() {}

    private Object readResolve() {
        return INSTANCE;
    }

    public String getName() {
        return "TAI";
    }

    public TimeScaleInstant getTimeScaleInstant(Instant t) {
        return TimeScaleInstant.instant(t.getEpochSeconds(), t.getNanoOfSecond());
    }

    /** Epoch seconds in this time scale for given instant. */
    public long getEpochSeconds(Instant t) {
        return getTaiEpochSeconds(t);
    }
    
    /** Number of nanoseconds after the second for Instant. */
    public int getNanoOfSecond(Instant t) {return getTaiNanosOfSecond(t);}

    /** Create an Instant corresponding to epochSeconds */
    public Instant instant(long epochSeconds, int nanoOfSecond) {
        return checkedTaiInstant(epochSeconds, nanoOfSecond);
    }

    public Instant instant(TimeScaleInstant tsi) {
        if (tsi.getLeapSecond() != 0 || tsi.getIncludedLeapSeconds() != 0)
            throw new IllegalArgumentException("TAI does not include leap seconds");
        return checkedTaiInstant(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
    }
}
