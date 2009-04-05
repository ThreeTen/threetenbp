package javax.time.scale;

import javax.time.TimeScale;
import java.io.Serializable;

/** International Atomic Time.
 *  * @author Mark Thornton
 */
public class TAI extends TimeScale implements Serializable {
    public static final TAI SCALE = new TAI();
    public static final Instant EPOCH = new Instant(0, 0);

    private TAI() {}

    protected Object readResolve() {
        return SCALE;
    }

    public String getName() {
        return "TAI";
    }

    public Instant getEpoch() {
        return EPOCH;
    }

    @Override
    protected Instant fromTAI(Instant tsiTAI) {
        return tsiTAI;
    }

    protected Instant toTAI(javax.time.Instant t) {
        return (Instant)t;
    }

    protected Instant uncheckedInstant(long simpleEpochSeconds, int nanoOfSecond) {
        return new Instant(simpleEpochSeconds, nanoOfSecond);
    }

    public static class Instant extends javax.time.Instant {
        Instant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        public TAI getScale() {
            return SCALE;
        }

    }
}
