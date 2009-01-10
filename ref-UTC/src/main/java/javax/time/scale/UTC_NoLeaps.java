package javax.time.scale;

import javax.time.MathUtils;
import java.io.Serializable;

/** UTC with no accounting for leap seconds at all.
 * The AbstractInstant does not report leapSecond. In this implementation the leap second will
 * appear as a second occurrence of 23:59:59. Alternative implementations might make the last 
 * two seconds before midnight run at half speed.
 * @author Mark Thornton
 */
public class UTC_NoLeaps extends AbstractUTC implements Serializable {
    public static final UTC_NoLeaps SCALE = new UTC_NoLeaps();
    public static final AbstractInstant EPOCH = new Instant(0, 0);

    private UTC_NoLeaps() {}

    private Object readResolve() {
        return SCALE;
    }

    public AbstractInstant getEpoch() {
        return EPOCH;
    }

    @Override
    protected AbstractInstant fromTAI(AbstractInstant tsiTAI) {
        if (InstantComparator.INSTANCE.compare(tsiTAI, getLeapEraInstant()) < 0) {
            return super.fromTAI(tsiTAI);
        }
        Entry entry = findEntry(tsiTAI);
        long s = tsiTAI.getEpochSeconds() - entry.getDeltaSeconds();
        if (s >= entry.getEndExclusiveSeconds() && entry.getNext() != null)
            return new Instant(entry.getEndExclusiveSeconds()-1, tsiTAI.getNanoOfSecond());
        else
            return new Instant(s, tsiTAI.getNanoOfSecond());
    }

    @Override
    protected AbstractInstant toTAI(AbstractInstant tsi) {
        if (tsi.getEpochSeconds() != tsi.getSimpleEpochSeconds() || tsi.getLeapSecond() != 0)
            throw new IllegalArgumentException("Time scale does not include leap seconds");
        if (tsi.getEpochSeconds() < leapEraSeconds)
            return super.toTAI(tsi);
        long s = tsi.getEpochSeconds();
        Entry entry = findEntry(s);
        return TAI.SCALE.instant(MathUtils.safeAdd(s, entry.getDeltaSeconds()), tsi.getNanoOfSecond());
    }

    @Override
    public String getName() {
        return "UTC_NoLeaps";
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
