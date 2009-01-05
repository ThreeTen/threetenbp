package javax.time.scale;

import javax.time.Instant;
import javax.time.MathUtils;
import java.io.Serializable;

/** UTC where epochSeconds does not include leap seconds.
 * The TimeScaleInstant does report leapSecond.
 * @author Mark Thornton
 */
public class UTC_NoEpochLeaps extends AbstractUTC implements Serializable {
    public static final UTC_NoEpochLeaps INSTANCE = new UTC_NoEpochLeaps();

    private UTC_NoEpochLeaps() {}

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public TimeScaleInstant getTimeScaleInstant(Instant t) {
        if (t.isBefore(getLeapEraInstant()))
            return super.getTimeScaleInstant(t);
        Entry entry = findEntry(t);
        long s = getTaiEpochSeconds(t) - entry.getDeltaSeconds();
        if (s >= entry.getEndExclusiveSeconds() && entry.getNext() != null)
            return TimeScaleInstant.leapInstant(entry.getEndExclusiveSeconds()-1, getTaiNanosOfSecond(t), (int)(s+1-entry.getEndExclusiveSeconds()));
        else
            return TimeScaleInstant.instant(s, getTaiNanosOfSecond(t));
    }

    @Override
    public long getEpochSeconds(Instant t) {
        if (t.isBefore(getLeapEraInstant())) {
            return super.getEpochSeconds(t);
        }
        return getTaiEpochSeconds(t) - findEntry(t).getDeltaSeconds();
    }

    @Override
    public int getNanoOfSecond(Instant t) {
        if (t.isBefore(getLeapEraInstant())) {
            return super.getNanoOfSecond(t);
        }
        return getTaiNanosOfSecond(t);
    }

    @Override
    public Instant instant(long epochSeconds, int nanoOfSecond) {
        if (epochSeconds < leapEraSeconds)
            return super.instant(epochSeconds, nanoOfSecond);
        else
            return checkedTaiInstant(
                    MathUtils.safeAdd(epochSeconds, findEntry(epochSeconds).getDeltaSeconds()),
                    nanoOfSecond);
    }

    public Instant instant(TimeScaleInstant tsi) {
        if (tsi.getIncludedLeapSeconds() != 0)
            throw new IllegalArgumentException("Time scale does not include leap seconds");
        if (tsi.getEpochSeconds() < leapEraSeconds) {
            return super.instant(tsi);
        }
        long s = tsi.getEpochSeconds();
        Entry entry = findEntry(s);
        if (tsi.getLeapSecond() != 0) {
            // Is this a legal point for a leap second?
            if (entry.getNext() == null || s+1 != entry.getEndExclusiveSeconds())
                throw new IllegalArgumentException("There is no leap second at this instant");
            s += tsi.getLeapSecond();
        }
        return checkedTaiInstant(MathUtils.safeAdd(s, entry.getDeltaSeconds()), tsi.getNanoOfSecond());
    }

    @Override
    public String getName() {
        return "UTC_NoEpochLeaps";
    }
}
