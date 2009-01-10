package javax.time.scale;

import javax.time.scale.TimeScale;
import javax.time.MathUtils;
import java.io.Serializable;

/** TimeScale used by GPS navigation system.
 * @author Mark Thornton
 */
public class GPS extends TimeScale implements Serializable {
    public static final GPS SCALE = new GPS();

    private static final int TAI_GPS = 19;

    private GPS() {}

    private Object readResolve() {
        return SCALE;
    }

    public String getName() {
        return "GPS";
    }

    protected AbstractInstant fromTAI(AbstractInstant tsiTAI) {
        return TimeScaleInstant.instant(this, MathUtils.safeSubtract(tsiTAI.getEpochSeconds(), TAI_GPS), tsiTAI.getNanoOfSecond());
    }

    protected AbstractInstant toTAI(AbstractInstant t) {
        return TimeScaleInstant.instant(TAI.SCALE, MathUtils.safeAdd(t.getEpochSeconds(), TAI_GPS), t.getNanoOfSecond());
    }
}
