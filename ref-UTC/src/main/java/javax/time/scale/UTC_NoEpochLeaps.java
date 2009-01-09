package javax.time.scale;

import javax.time.MathUtils;
import java.io.Serializable;

/** UTC where epochSeconds does not include leap seconds.
 * The TimeScaleInstant does report leapSecond.
 * @author Mark Thornton
 */
public class UTC_NoEpochLeaps extends AbstractUTC implements Serializable {
    public static final UTC_NoEpochLeaps SCALE = new UTC_NoEpochLeaps();

    private UTC_NoEpochLeaps() {}

    private Object readResolve() {
        return SCALE;
    }

    @Override
    protected AbstractInstant fromTAI(AbstractInstant tsiTAI) {
        if (InstantComparator.INSTANCE.compare(tsiTAI, getLeapEraInstant()) < 0) {
            return super.fromTAI(tsiTAI);
        }
        Entry entry = findEntry(tsiTAI);
        long s = tsiTAI.getEpochSeconds() - entry.getDeltaSeconds();
        if (s >= entry.getEndExclusiveSeconds() && entry.getNext() != null)
            return TimeScaleInstant.leapInstant(this, entry.getEndExclusiveSeconds()-1, tsiTAI.getNanoOfSecond(), (int)(s+1-entry.getEndExclusiveSeconds()));
        else
            return TimeScaleInstant.instant(this, s, tsiTAI.getNanoOfSecond());
    }

    @Override
    protected AbstractInstant toTAI(AbstractInstant tsi) {
        if (tsi.getEpochSeconds() != tsi.getSimpleEpochSeconds() )
            throw new IllegalArgumentException("Time scale does not include leap seconds");
        if (tsi.getEpochSeconds() < leapEraSeconds)
            return super.toTAI(tsi);
        long s = tsi.getEpochSeconds();
        Entry entry = findEntry(s);
        if (tsi.getLeapSecond() != 0) {
            // Is this a legal point for a leap second?
            if (entry.getNext() == null || s+1 != entry.getEndExclusiveSeconds())
                throw new IllegalArgumentException("There is no leap second at this instant");
            s += tsi.getLeapSecond();
        }
        return TimeScaleInstant.instant(TAI.SCALE, MathUtils.safeAdd(s, entry.getDeltaSeconds()), tsi.getNanoOfSecond());
    }

    @Override
    public TimeScaleInstant toScale(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
        if (simpleEpochSeconds < leapEraSeconds) {
            if (leapSecond != 0)
                throw new IllegalArgumentException("There is no leap second at this instant");
            return TimeScaleInstant.instant(this, simpleEpochSeconds, nanoOfSecond);
        }
        Entry entry = findEntry(simpleEpochSeconds);
        if (leapSecond != 0) {
            if (leapSecond < 0 || entry.getNext() == null || simpleEpochSeconds+1 != entry.getEndExclusiveSeconds() ||
                    leapSecond > (entry.getNext().getDeltaSeconds() -entry.getDeltaSeconds())) {
                throw new IllegalArgumentException("Invalid leapSecond "+leapSecond);
            }
            return TimeScaleInstant.leapInstant(this, simpleEpochSeconds, nanoOfSecond, leapSecond);
        }
        return TimeScaleInstant.instant(this, simpleEpochSeconds, nanoOfSecond);
    }

    @Override
    public String getName() {
        return "UTC_NoEpochLeaps";
    }
}
