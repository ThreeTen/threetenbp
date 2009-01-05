package javax.time.scale;

import javax.time.Instant;
import javax.time.MathUtils;
import java.io.Serializable;

/** UTC with no accounting for leap seconds at all.
 * The TimeScaleInstant does not report leapSecond. In this implementation the leap second will
 * appear as a second occurrence of 23:59:59. Alternative implementations might make the last 
 * two seconds before midnight run at half speed.
 * @author Mark Thornton
 */
public class UTC_NoLeaps extends AbstractUTC implements Serializable {
    public static final UTC_NoLeaps INSTANCE = new UTC_NoLeaps();

    private UTC_NoLeaps() {}

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
            return TimeScaleInstant.instant(entry.getEndExclusiveSeconds()-1, getTaiNanosOfSecond(t));
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
        if (tsi.getIncludedLeapSeconds() != 0 || tsi.getLeapSecond() != 0)
            throw new IllegalArgumentException("This time scale does not support leap seconds");
        return instant(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
    }

    @Override
    public String getName() {
        return "UTC_NoLeaps";
    }
}
