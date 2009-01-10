package javax.time.scale;

import javax.time.scale.TimeScale;
import javax.time.MathUtils;
import java.io.Serializable;

/** TimeScale used by GPS navigation system.
 * @author Mark Thornton
 */
public class GPS extends TimeScale implements Serializable {
    public static final GPS SCALE = new GPS();
    public static final AbstractInstant EPOCH = new Instant(0, 0);

    private static final int TAI_GPS = 19;

    private GPS() {}

    private Object readResolve() {
        return SCALE;
    }

    public String getName() {
        return "GPS";
    }

    public AbstractInstant getEpoch() {
        return EPOCH;
    }

    protected AbstractInstant fromTAI(AbstractInstant tsiTAI) {
        return new Instant(MathUtils.safeSubtract(tsiTAI.getEpochSeconds(), TAI_GPS), tsiTAI.getNanoOfSecond());
    }

    protected AbstractInstant toTAI(AbstractInstant t) {
        return TAI.SCALE.instant(MathUtils.safeAdd(t.getEpochSeconds(), TAI_GPS), t.getNanoOfSecond());
    }

    private static class Instant extends AbstractInstant<Instant> {
        Instant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        public TimeScale getScale() {
            return SCALE;
        }

        protected Instant factory(long epochSeconds, int nanoOfSecond, int leapSecond) {
            return new Instant(epochSeconds, nanoOfSecond);
        }
    }
}
