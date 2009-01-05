package javax.time.scale;

import javax.time.Instant;
import javax.time.MathUtils;
import java.io.Serializable;

/** Coordinated Universal Time including leap seconds.
     * The epochSeconds include any leap seconds within the interval. Thus epochSeconds are
     * TAI-10s (10 seconds was the initial offset). The number of subsequent leap seconds is
     * reported by getLeapSeconds.
 * @author Mark Thornton
 */
public class UTC extends AbstractUTC implements Serializable {
    public static final UTC INSTANCE = new UTC();

    private UTC() {}

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return "UTC";
    }

    @Override
    public TimeScaleInstant getTimeScaleInstant(Instant t) {
        if (t.isBefore(getLeapEraInstant()))
            return super.getTimeScaleInstant(t);
        Entry entry = findEntry(t);
        long s = getTaiEpochSeconds(t);
        if (s-entry.getDeltaSeconds() >= entry.getEndExclusiveSeconds() && entry.getNext() != null)
            return TimeScaleInstant.leapInstantWithIncludedLeaps(s-leapEraDelta, getTaiNanosOfSecond(t),
                    entry.getDeltaSeconds()-leapEraDelta,
                    (int)(s+1-entry.getDeltaSeconds()-entry.getEndExclusiveSeconds()));
        else
            return TimeScaleInstant.instantWithIncludedLeaps(s-leapEraDelta, getTaiNanosOfSecond(t),
                    entry.getDeltaSeconds()-leapEraDelta);
    }

    @Override
    public long getEpochSeconds(Instant t) {
        if (t.isBefore(getLeapEraInstant())) {
            return super.getEpochSeconds(t);
        }
        return getTaiEpochSeconds(t)-leapEraDelta;
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
        if (epochSeconds >= leapEraSeconds) {
            return checkedTaiInstant(MathUtils.safeAdd(epochSeconds, leapEraDelta), nanoOfSecond);
        }
        return super.instant(epochSeconds, nanoOfSecond);
    }

    @Override
    public Instant instant(TimeScaleInstant tsi) {
        long s = tsi.getEpochSeconds();
        if (s < leapEraSeconds)
            return super.instant(tsi);
        if (!tsi.isLeapSecondTotalIncluded()) {
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
        return checkedTaiInstant(s, tsi.getNanoOfSecond());
    }
}
