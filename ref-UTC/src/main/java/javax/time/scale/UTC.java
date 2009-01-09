package javax.time.scale;

import javax.time.MathUtils;
import java.io.Serializable;

/** Coordinated Universal Time including leap seconds.
     * The epochSeconds include any leap seconds within the interval. Thus epochSeconds are
     * TAI-10s (10 seconds was the initial offset). The number of subsequent leap seconds is
     * reported by getLeapSeconds.
 * @author Mark Thornton
 */
public class UTC extends AbstractUTC implements Serializable {
    public static final UTC SCALE = new UTC();

    private UTC() {}

    private Object readResolve() {
        return SCALE;
    }

    @Override
    public String getName() {
        return "UTC";
    }

    @Override
    protected AbstractInstant fromTAI(AbstractInstant tsiTAI) {
        if (InstantComparator.INSTANCE.compare(tsiTAI, getLeapEraInstant()) < 0) {
            return super.fromTAI(tsiTAI);
        }
        Entry entry = findEntry(tsiTAI);
        long s = tsiTAI.getEpochSeconds();
        if (s-entry.getDeltaSeconds() >= entry.getEndExclusiveSeconds() && entry.getNext() != null)
            return TimeScaleInstant.leapInstantWithIncludedLeaps(this, s-leapEraDelta, tsiTAI.getNanoOfSecond(),
                    entry.getDeltaSeconds()-leapEraDelta,
                    (int)(s+1-entry.getDeltaSeconds()-entry.getEndExclusiveSeconds()));
        else
            return TimeScaleInstant.instantWithIncludedLeaps(this, s-leapEraDelta, tsiTAI.getNanoOfSecond(),
                    entry.getDeltaSeconds()-leapEraDelta);
    }

    @Override
    protected AbstractInstant toTAI(AbstractInstant tsi) {
        if (tsi.getEpochSeconds() < leapEraSeconds)
            return super.toTAI(tsi);
        long s = tsi.getEpochSeconds();
        if (!(tsi instanceof TimeScaleInstant && ((TimeScaleInstant)tsi).isLeapSecondTotalIncluded())) {
            Entry entry = findEntry(s);
            if (tsi.getLeapSecond() != 0) {
                // Is this a legal point for a leap second?
                if (entry.getNext() == null || s+1 != entry.getEndExclusiveSeconds())
                    throw new IllegalArgumentException("There is no leap second at this instant");
                s += tsi.getLeapSecond();
            }
            s = MathUtils.safeAdd(s, entry.getDeltaSeconds());
        }
        else {
            // TODO: check if tsi.getLeapSecond() is valid
            // TODO: check if tsi.getIncludedLeapSeconds() is valid
            s = MathUtils.safeAdd(s, leapEraDelta);
        }
        return TimeScaleInstant.instant(TAI.SCALE, s, tsi.getNanoOfSecond());
    }

    @Override
    public TimeScaleInstant toScale(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
        if (simpleEpochSeconds < leapEraSeconds) {
            if (leapSecond != 0)
                throw new IllegalArgumentException("There is no leap second at this instant");
            return TimeScaleInstant.instant(this, simpleEpochSeconds, nanoOfSecond);
        }
        Entry entry = findEntry(simpleEpochSeconds);
        int delta = entry.getDeltaSeconds()-leapEraDelta;
        if (leapSecond != 0) {
            if (leapSecond < 0 || entry.getNext() == null || simpleEpochSeconds+1 != entry.getEndExclusiveSeconds() ||
                    leapSecond > (entry.getNext().getDeltaSeconds() - entry.getDeltaSeconds())) {
                throw new IllegalArgumentException("Invalid leapSecond "+leapSecond);
            }
            return TimeScaleInstant.leapInstantWithIncludedLeaps(this, simpleEpochSeconds+delta, nanoOfSecond, delta, leapSecond);
        }
        return TimeScaleInstant.instantWithIncludedLeaps(this, simpleEpochSeconds+delta, nanoOfSecond, delta);
    }

    @Override
    protected long getSimpleEpochSeconds(AbstractInstant t) {
        long s = t.getEpochSeconds();
        if (s < leapEraSeconds)
            return s;
        for (int i=UTC_ENTRIES.size(); --i >= 0;) {
            Entry e = UTC_ENTRIES.get(i);
            if (e.getStartInclusiveSeconds()+e.getDeltaSeconds() <= s) {
                s -= e.getDeltaSeconds();
                if (e.getNext() != null && s+1 == e.getEndExclusiveSeconds())
                    s--;
                return s;
            }
        }
        throw new AssertionError();
    }
}
