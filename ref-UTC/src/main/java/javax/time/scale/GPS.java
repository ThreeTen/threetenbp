package javax.time.scale;

import javax.time.scale.TimeScale;
import javax.time.MathUtils;
import java.io.Serializable;

/** TimeScale used by GPS navigation system.
 * @author Mark Thornton
 */
public class GPS extends TimeScale<GPS.Instant> implements Serializable {
    public static final GPS SCALE = new GPS();
    public static final Instant EPOCH = new Instant(0, 0);

    private static final int TAI_GPS = 19;

    private GPS() {}

    private Object readResolve() {
        return SCALE;
    }

    public String getName() {
        return "GPS";
    }

    public Instant getEpoch() {
        return EPOCH;
    }

    protected Instant fromTAI(TAI.Instant tsiTAI) {
        return new Instant(MathUtils.safeSubtract(tsiTAI.getEpochSeconds(), TAI_GPS), tsiTAI.getNanoOfSecond());
    }

    protected TAI.Instant toTAI(Instant t) {
        return TAI.SCALE.instant(MathUtils.safeAdd(t.getEpochSeconds(), TAI_GPS), t.getNanoOfSecond());
    }

    public static class Instant extends AbstractInstant<Instant> {
        Instant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        public GPS getScale() {
            return SCALE;
        }

        protected Instant factory(long epochSeconds, int nanoOfSecond, int leapSecond) {
            return new Instant(epochSeconds, nanoOfSecond);
        }
    }
}
