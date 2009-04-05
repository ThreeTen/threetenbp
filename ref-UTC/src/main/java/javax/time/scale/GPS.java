package javax.time.scale;

import javax.time.TimeScale;
import javax.time.MathUtils;
import javax.time.Instant;
import java.io.Serializable;

/** TimeScale used by GPS navigation system.
 * @author Mark Thornton
 */
public class GPS extends TimeScale implements Serializable {
    public static final GPS SCALE = new GPS();
    public static final Instant EPOCH = new Instant(0, 0);

    private static final int TAI_GPS = 19;

    private GPS() {}

    protected Object readResolve() {
        return SCALE;
    }

    public String getName() {
        return "GPS";
    }

    public Instant getEpoch() {
        return EPOCH;
    }

    protected javax.time.Instant uncheckedInstant(long simpleEpochSeconds, int nanoOfSecond) {
        return new Instant(simpleEpochSeconds, nanoOfSecond);
    }

    protected javax.time.Instant fromTAI(TAI.Instant tsiTAI) {
        return new Instant(MathUtils.safeSubtract(tsiTAI.getEpochSeconds(), TAI_GPS), tsiTAI.getNanoOfSecond());
    }

    protected TAI.Instant toTAI(javax.time.Instant t) {
        return TAI.SCALE.uncheckedInstant(MathUtils.safeAdd(t.getEpochSeconds(), TAI_GPS), t.getNanoOfSecond());
    }

    public static class Instant extends javax.time.Instant {
        Instant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        public GPS getScale() {
            return SCALE;
        }
    }
}
