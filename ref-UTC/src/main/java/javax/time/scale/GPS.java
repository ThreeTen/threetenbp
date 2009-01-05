package javax.time.scale;

import javax.time.TimeScale;
import javax.time.Instant;
import javax.time.MathUtils;
import java.io.Serializable;

/** TimeScale used by GPS navigation system.
 * @author Mark Thornton
 */
public class GPS extends TimeScale implements Serializable {
    public static final GPS INSTANCE = new GPS();

    private static final int TAI_GPS = 19;

    private GPS() {}

    private Object readResolve() {
        return INSTANCE;
    }

    public String getName() {
        return "GPS";
    }

    @Override
    public TimeScaleInstant getTimeScaleInstant(Instant t) {
        return TimeScaleInstant.instant(MathUtils.safeSubtract(getTaiEpochSeconds(t), TAI_GPS), getTaiNanosOfSecond(t));    //To change body of overridden methods use File | Settings | File Templates.
    }

    public long getEpochSeconds(Instant t) {
        return MathUtils.safeSubtract(getTaiEpochSeconds(t), TAI_GPS);
    }
    
    /** Number of nanoseconds after the second for Instant. */
    public int getNanoOfSecond(Instant t) {return getTaiNanosOfSecond(t);}

    public Instant instant(long epochSeconds, int nanoseconds) {
        return checkedTaiInstant(MathUtils.safeAdd(epochSeconds, TAI_GPS), nanoseconds);
    }

    public Instant instant(TimeScaleInstant tsi) {
        if (tsi.getLeapSecond() != 0 || tsi.getIncludedLeapSeconds() != 0)
            throw new IllegalArgumentException("GPS does not include leap seconds");
        return checkedTaiInstant(MathUtils.safeAdd(tsi.getEpochSeconds(), TAI_GPS), tsi.getNanoOfSecond());
    }
}
