package javax.time.scale;

import javax.time.MathUtils;
import java.io.Serializable;

/** UTC with no accounting for leap seconds at all.
 * The AbstractInstant does not report leapSecond. In this implementation the leap second will
 * appear as a second occurrence of 23:59:59. Alternative implementations might make the last 
 * two seconds before midnight run at half speed.
 * @author Mark Thornton
 */
public class UTC extends AbstractUTC implements Serializable {
    public static final UTC SCALE = new UTC();
    public static final Instant EPOCH = new Instant(0, 0);

    private UTC() {}

    protected Object readResolve() {
        return SCALE;
    }

    public Instant getEpoch() {
        return EPOCH;
    }

    protected javax.time.Instant uncheckedInstant(long simpleEpochSeconds, int nanoOfSecond) {
        return new Instant(simpleEpochSeconds, nanoOfSecond);
    }

    protected Instant newInstant(long epochSeconds, int nanoOfSecond) {
        return new Instant(epochSeconds, nanoOfSecond);
    }

    @Override
    protected Instant fromTAI(TAI.Instant tsiTAI) {
        if (InstantComparator.INSTANCE.compare(tsiTAI, getLeapEraInstant()) < 0) {
            return (Instant)super.fromTAI(tsiTAI);
        }
        Entry entry = findEntry(tsiTAI);
        long s = tsiTAI.getEpochSeconds() - entry.getDeltaSeconds();
        if (s >= entry.getEndExclusiveSeconds() && entry.getNext() != null)
            return new Instant(entry.getEndExclusiveSeconds()-1, tsiTAI.getNanoOfSecond());
        else
            return new Instant(s, tsiTAI.getNanoOfSecond());
    }

    @Override
    protected TAI.Instant toTAI(javax.time.Instant tsi) {
        if (tsi.getEpochSeconds() < leapEraSeconds)
            return super.toTAI(tsi);
        long s = tsi.getEpochSeconds();
        Entry entry = findEntry(s);
        return new TAI.Instant(MathUtils.safeAdd(s, entry.getDeltaSeconds()), tsi.getNanoOfSecond());
    }

    @Override
    public String getName() {
        return "UTC";
    }

    public static class Instant extends javax.time.Instant {
        Instant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        public UTC getScale() {
            return SCALE;
        }

    }
}
