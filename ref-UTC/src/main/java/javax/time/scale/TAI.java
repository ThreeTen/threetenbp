package javax.time.scale;

import javax.time.scale.TimeScale;
import java.io.Serializable;

/** International Atomic Time.
 *  * @author Mark Thornton
 */
public class TAI extends TimeScale implements Serializable {
    public static final TAI SCALE = new TAI();
    public static final Instant EPOCH = new Instant(0, 0);

    private TAI() {}

    private Object readResolve() {
        return SCALE;
    }

    public String getName() {
        return "TAI";
    }

    public Instant getEpoch() {
        return EPOCH;
    }

    @Override
    protected AbstractInstant fromTAI(AbstractInstant tsiTAI) {
        return tsiTAI;
    }

    protected AbstractInstant toTAI(AbstractInstant t) {
        return t;
    }

    @Override
    public Instant instant(long simpleEpochSeconds, int nanoOfSecond) {
        return new Instant(simpleEpochSeconds, nanoOfSecond);
    }

    public static class Instant extends AbstractInstant<Instant> {
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
