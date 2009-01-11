package javax.time.scale;

import javax.time.MathUtils;
import javax.time.Duration;
import java.io.Serializable;

/** Coordinated Universal Time including leap seconds.
     * The epochSeconds include any leap seconds within the interval. Thus epochSeconds are
     * TAI-10s (10 seconds was the initial offset) for times after 1972-01-01T00:00.
 * @author Mark Thornton
 */
public class UTC extends AbstractUTC<UTC.Instant> implements Serializable {
    public static final UTC SCALE = new UTC();
    public static final Instant EPOCH = new Instant(0, 0, 0);

    private UTC() {}

    private Object readResolve() {
        return SCALE;
    }

    @Override
    public String getName() {
        return "UTC";
    }

    public Instant getEpoch() {
        return EPOCH;
    }

    @Override
    protected Instant fromTAI(TAI.Instant tsiTAI) {
        if (InstantComparator.INSTANCE.compare(tsiTAI, getLeapEraInstant()) < 0) {
            return super.fromTAI(tsiTAI);
        }
        Entry entry = findEntry(tsiTAI);
        long s = tsiTAI.getEpochSeconds();
        if (s-entry.getDeltaSeconds() >= entry.getEndExclusiveSeconds() && entry.getNext() != null)
            return new LeapInstant(s-leapEraDelta, tsiTAI.getNanoOfSecond(),
                    entry.getDeltaSeconds()-leapEraDelta,
                    (int)(s+1-entry.getDeltaSeconds()-entry.getEndExclusiveSeconds()));
        else
            return new Instant(s-leapEraDelta, tsiTAI.getNanoOfSecond(),
                    entry.getDeltaSeconds()-leapEraDelta);
    }

    @Override
    protected TAI.Instant toTAI(Instant tsi) {
        if (tsi.getEpochSeconds() < leapEraSeconds)
            return super.toTAI(tsi);
        return TAI.SCALE.instant(MathUtils.safeAdd(tsi.getEpochSeconds(), leapEraDelta), tsi.getNanoOfSecond());
    }

    @Override
    public Instant instant(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
        if (simpleEpochSeconds < leapEraSeconds) {
            if (leapSecond != 0)
                throw new IllegalArgumentException("There is no leap second at this instant");
            return new Instant(simpleEpochSeconds, nanoOfSecond, 0);
        }
        Entry entry = findEntry(simpleEpochSeconds);
        int delta = entry.getDeltaSeconds()-leapEraDelta;
        if (leapSecond != 0) {
            if (leapSecond < 0 || entry.getNext() == null || simpleEpochSeconds+1 != entry.getEndExclusiveSeconds() ||
                    leapSecond > (entry.getNext().getDeltaSeconds() - entry.getDeltaSeconds())) {
                throw new IllegalArgumentException("Invalid leapSecond "+leapSecond);
            }
            return new LeapInstant(simpleEpochSeconds+delta, nanoOfSecond, delta, leapSecond);
        }
        return new Instant(simpleEpochSeconds+delta, nanoOfSecond, delta);
    }

    @Override
    protected long getSimpleEpochSeconds(AbstractInstant t) {
        long s = t.getEpochSeconds();
        if (s < leapEraSeconds)
            return s;
        Entry e = findEntryUTC(s);
        s -= e.getDeltaSeconds();
        if (e.getNext() != null && s+1 == e.getEndExclusiveSeconds())
            s--;
        return s;
    }

    private <T extends AbstractInstant<T>> T newInstant(T t, long seconds, int nanos) {
        if (seconds < leapEraSeconds)
            return t.factory(seconds, nanos, 0, 0);
        int leapSecond = 0;
        Entry entry = findEntryUTC(seconds);
        int delta = entry.getDeltaSeconds()-leapEraDelta;
        if (entry.getNext() != null) {
            long s = seconds-delta-entry.getEndExclusiveSeconds();
            if (s >= 0)
                leapSecond = 1+(int)s;
        }
        return t.factory(seconds, nanos, leapSecond, delta);
    }

    @Override
    protected <T extends AbstractInstant<T>> T plus(T t, Duration duration) {
        if (duration.equals(Duration.ZERO))
            return t;
        long seconds = MathUtils.safeAdd(t.getEpochSeconds(), duration.getSeconds());
        int nanos = t.getNanoOfSecond() + duration.getNanoOfSecond();
        if (nanos >= NANOS_PER_SECOND) {
            seconds = MathUtils.safeIncrement(seconds);
            nanos -= NANOS_PER_SECOND;
        }
        return newInstant(t, seconds, nanos);
    }

    @Override
    protected <T extends AbstractInstant<T>> T minus(T t, Duration duration) {
        if (duration.equals(Duration.ZERO))
            return t;
        long seconds = MathUtils.safeSubtract(t.getEpochSeconds(), duration.getSeconds());
        int nanos = t.getNanoOfSecond() - duration.getNanoOfSecond();
        if (nanos < 0) {
            seconds = MathUtils.safeDecrement(seconds);
            nanos += NANOS_PER_SECOND;
        }
        return newInstant(t, seconds, nanos);
    }

    public static class Instant extends AbstractInstant<Instant> {
        private final int includedLeapSeconds;

        Instant(long epochSeconds, int nanoOfSecond, int includedLeapSeconds) {
            super(epochSeconds, nanoOfSecond);
            this.includedLeapSeconds = includedLeapSeconds;
        }

        public UTC getScale() {
            return SCALE;
        }

        @Override
        public long getSimpleEpochSeconds() {
            return getEpochSeconds()-includedLeapSeconds-getLeapSecond();
        }

        protected Instant factory(long epochSeconds, int nanoOfSecond, int leapSecond) {
            if (leapSecond != 0)
                return new LeapInstant(epochSeconds, nanoOfSecond, 0, leapSecond);
            else
                return new Instant(epochSeconds, nanoOfSecond, 0);
        }

        @Override
        protected Instant factory(long epochSeconds, int nanoOfSecond, int leapSecond, int includedLeapSeconds) {
            if (leapSecond != 0)
                return new LeapInstant(epochSeconds, nanoOfSecond, includedLeapSeconds, leapSecond);
            else
                return new Instant(epochSeconds, nanoOfSecond, includedLeapSeconds);
        }
    }

    private static class LeapInstant extends Instant {
        private final int leapSecond;

        LeapInstant(long epochSeconds, int nanoOfSecond, int includedLeapSeconds, int leapSecond) {
            super(epochSeconds, nanoOfSecond, includedLeapSeconds);
            this.leapSecond = leapSecond;
        }

        @Override
        public int getLeapSecond() {
            return leapSecond;
        }
    }

}
