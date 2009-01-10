package javax.time.scale;

import javax.time.MathUtils;
import javax.time.Duration;
import java.io.Serializable;

/** UTC where epochSeconds does not include leap seconds.
 * The AbstractInstant does report leapSecond.
 * @author Mark Thornton
 */
public class UTC_NoEpochLeaps extends AbstractUTC implements Serializable {
    public static final UTC_NoEpochLeaps SCALE = new UTC_NoEpochLeaps();
    public static final AbstractInstant EPOCH = new Instant(0, 0);

    private UTC_NoEpochLeaps() {}

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
            return new LeapInstant(entry.getEndExclusiveSeconds()-1, tsiTAI.getNanoOfSecond(), (int)(s+1-entry.getEndExclusiveSeconds()));
        else
            return new Instant(s, tsiTAI.getNanoOfSecond());
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
        return TAI.SCALE.instant(MathUtils.safeAdd(s, entry.getDeltaSeconds()), tsi.getNanoOfSecond());
    }

    @Override
    public AbstractInstant instant(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
        if (simpleEpochSeconds < leapEraSeconds) {
            if (leapSecond != 0)
                throw new IllegalArgumentException("There is no leap second at this instant");
            return new Instant(simpleEpochSeconds, nanoOfSecond);
        }
        Entry entry = findEntry(simpleEpochSeconds);
        if (leapSecond != 0) {
            if (leapSecond < 0 || entry.getNext() == null || simpleEpochSeconds+1 != entry.getEndExclusiveSeconds() ||
                    leapSecond > (entry.getNext().getDeltaSeconds() -entry.getDeltaSeconds())) {
                throw new IllegalArgumentException("Invalid leapSecond "+leapSecond);
            }
            return new LeapInstant(simpleEpochSeconds, nanoOfSecond, leapSecond);
        }
        return new Instant(simpleEpochSeconds, nanoOfSecond);
    }

    @Override
    public String getName() {
        return "UTC_NoEpochLeaps";
    }

    private int getLeapCount(AbstractInstant t) {
        if (t.getEpochSeconds() < leapEraSeconds)
            return 0;
        Entry e = findEntry(t.getEpochSeconds());
        return e.getDeltaSeconds()-leapEraDelta+t.getLeapSecond();
    }

    @Override
    protected Duration difference(AbstractInstant a, AbstractInstant b) {
        long seconds = MathUtils.safeSubtract(a.getEpochSeconds(), b.getEpochSeconds());
        seconds = MathUtils.safeAdd(seconds, getLeapCount(a)-getLeapCount(b));
        int nanos = a.getNanoOfSecond() - b.getNanoOfSecond();
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            seconds = MathUtils.safeDecrement(seconds);
        }
        return Duration.duration(seconds, nanos);
    }

    private <T extends AbstractInstant<T>> T newInstant(T t, long seconds, int nanos) {
        if (seconds < leapEraSeconds)
            return t.factory(seconds, nanos, 0);
        int leapSecond = 0;
        Entry entry = findEntryUTC(seconds);
        int delta = entry.getDeltaSeconds()-leapEraDelta;
        if (entry.getNext() != null) {
            long s = seconds-delta-entry.getEndExclusiveSeconds();
            if (s >= 0) {
                leapSecond = 1+(int)s;
                seconds -= leapSecond;
            }
        }
        return t.factory(seconds-delta, nanos, leapSecond);
    }

    private long getUtcEpochSeconds(AbstractInstant t) {
        long seconds = t.getEpochSeconds();
        if (seconds > leapEraSeconds) {
            Entry e = findEntry(seconds);
            seconds = MathUtils.safeAdd(seconds, e.getDeltaSeconds()-leapEraDelta+t.getLeapSecond());
        }
        return seconds;
    }

    @Override
    protected <T extends AbstractInstant<T>> T plus(T t, Duration duration) {
        long seconds = MathUtils.safeAdd(getUtcEpochSeconds(t), duration.getSeconds());
        int nanos = t.getNanoOfSecond()+duration.getNanoOfSecond();
        if (nanos >= NANOS_PER_SECOND) {
            nanos -= NANOS_PER_SECOND;
            seconds = MathUtils.safeIncrement(seconds);
        }
        return newInstant(t, seconds, nanos);
    }

    @Override
    protected <T extends AbstractInstant<T>> T minus(T t, Duration duration) {
        long seconds = MathUtils.safeSubtract(getUtcEpochSeconds(t), duration.getSeconds());
        int nanos = t.getNanoOfSecond()-duration.getNanoOfSecond();
        if (nanos < 0) {
            nanos += NANOS_PER_SECOND;
            seconds = MathUtils.safeDecrement(seconds);
        }
        return newInstant(t, seconds, nanos);
    }

    private static class Instant extends AbstractInstant<Instant> {
        Instant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        public TimeScale getScale() {
            return SCALE;
        }

        protected Instant factory(long epochSeconds, int nanoOfSecond, int leapSecond) {
            if (leapSecond == 0)
                return new Instant(epochSeconds, nanoOfSecond);
            else
                return new LeapInstant(epochSeconds, nanoOfSecond, leapSecond);
        }
    }
    
    private static class LeapInstant extends Instant {
        private final int leapSecond;

        LeapInstant(long epochSeconds, int nanoOfSecond, int leapSecond) {
            super(epochSeconds, nanoOfSecond);
            this.leapSecond = leapSecond;
        }

        @Override
        public int getLeapSecond() {
            return leapSecond;
        }
    }
}
