package javax.time.scale;

import javax.time.MathUtils;
import javax.time.Instant;
import java.io.Serializable;

/** UTC with no accounting for leap seconds at all.
 * In this implementation the leap second will
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
        if (tsiTAI.compareTo(UTCHistory.TAI_START_LEAP_SECONDS) < 0) {
            return (Instant)super.fromTAI(tsiTAI);
        }
        UTCHistoryEntry entry = UTCHistory.current().findEntry(tsiTAI);
        long s = tsiTAI.getEpochSeconds() - (entry.getLeapSecondCount()+UTCHistory.LEAP_ERA_DELTA);
        if (entry.getNext() != null && s >= entry.getNext().getStartUTC().getEpochSeconds())
            s--;
        return new Instant(s, tsiTAI.getNanoOfSecond());
    }

    @Override
    protected TAI.Instant toTAI(javax.time.Instant tsi) {
        if (tsi.getEpochSeconds() < UTCHistory.UTC_START_LEAP_SECONDS.getEpochSeconds())
            return super.toTAI(tsi);
        long s = tsi.getEpochSeconds();
        UTCHistoryEntry entry = UTCHistory.current().findEntrySimple(s);
        return new TAI.Instant(MathUtils.safeAdd(s, entry.getLeapSecondCount()+UTCHistory.LEAP_ERA_DELTA), tsi.getNanoOfSecond());
    }

    @Override
    public javax.time.Instant instant(javax.time.Instant tsi) {
        if (tsi.getScale().equals(TrueUTC.SCALE)) {
            return new Instant(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
        }
        return super.instant(tsi);
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
