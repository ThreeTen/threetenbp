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
    public static final Instant EPOCH = new Instant(0, 0);

    private UTC_NoEpochLeaps() {}

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
            return new LeapInstant(entry.getEndExclusiveSeconds()-1, tsiTAI.getNanoOfSecond(), (int)(s+1-entry.getEndExclusiveSeconds()));
        else
            return new Instant(s, tsiTAI.getNanoOfSecond());
    }

    @Override
    protected TAI.Instant toTAI(javax.time.Instant tsi) {
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
        return new TAI.Instant(MathUtils.safeAdd(s, entry.getDeltaSeconds()), tsi.getNanoOfSecond());
    }

    @Override
    public Instant instant(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
        // Check and normalize nanoOfSecond
        if (nanoOfSecond >= NANOS_PER_SECOND) {
            throw new IllegalArgumentException("Nanosecond fraction must not be more than 999,999,999 but was " + nanoOfSecond);
        }
        if (nanoOfSecond < 0) {
            nanoOfSecond += NANOS_PER_SECOND;
            if (nanoOfSecond <= 0) {
                throw new IllegalArgumentException("Nanosecond fraction must not be less than -999,999,999 but was " + nanoOfSecond);
            }
            simpleEpochSeconds = MathUtils.safeDecrement(simpleEpochSeconds);
        }
        // Is at EPOCH?
        if (simpleEpochSeconds == 0 && nanoOfSecond == 0 && leapSecond == 0) {
           return EPOCH;
        }
        if (simpleEpochSeconds < leapEraSeconds) {
            if (leapSecond != 0)
                throw new IllegalArgumentException("There is no leap second at this instant");
            return new Instant(simpleEpochSeconds, nanoOfSecond);
        }
        if (leapSecond != 0) {
            // Verify leap second. Don't check if after latest known date (or perhaps check that it occurs at midnight)
            Entry entry = findEntry(simpleEpochSeconds);
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

    private int getLeapCount(javax.time.Instant t) {
        if (t.getEpochSeconds() < leapEraSeconds)
            return 0;
        Entry e = findEntry(t.getEpochSeconds());
        return e.getDeltaSeconds()-leapEraDelta+t.getLeapSecond();
    }

    private Instant newScaleInstant(long seconds, int nanos) {
        if (seconds < leapEraSeconds)
            return new Instant(seconds, nanos);
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
        if (leapSecond == 0)
            return new Instant(seconds-delta, nanos);
        else
            return new LeapInstant(seconds-delta, nanos, leapSecond);
    }

    private long getUtcEpochSeconds(javax.time.Instant t) {
        long seconds = t.getEpochSeconds();
        if (seconds > leapEraSeconds) {
            Entry e = findEntry(seconds);
            seconds = MathUtils.safeAdd(seconds, e.getDeltaSeconds()-leapEraDelta+t.getLeapSecond());
        }
        return seconds;
    }

    public static class Instant extends javax.time.Instant {
        Instant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        public UTC_NoEpochLeaps getScale() {
            return SCALE;
        }

        @Override
        protected Duration difference(javax.time.Instant b) {
            long seconds = MathUtils.safeSubtract(getEpochSeconds(), b.getEpochSeconds());
            seconds = MathUtils.safeAdd(seconds, SCALE.getLeapCount(this)-SCALE.getLeapCount(b));
            int nanos = getNanoOfSecond() - b.getNanoOfSecond();
            if (nanos < 0) {
                nanos += NANOS_PER_SECOND;
                seconds = MathUtils.safeDecrement(seconds);
            }
            return Duration.duration(seconds, nanos);
        }

        @Override
        protected javax.time.Instant plus(long addSeconds, int addNanoOfSecond) {
            if (addSeconds == 0 && addNanoOfSecond == 0)
                return this;
            long seconds = MathUtils.safeAdd(SCALE.getUtcEpochSeconds(this), addSeconds);
            int nanos = getNanoOfSecond()+addNanoOfSecond;
            if (nanos >= NANOS_PER_SECOND) {
                nanos -= NANOS_PER_SECOND;
                seconds = MathUtils.safeIncrement(seconds);
            }
            return SCALE.newScaleInstant(seconds, nanos);
        }

        @Override
        protected javax.time.Instant minus(long subSeconds, int subNanoOfSecond) {
            if (subSeconds == 0 && subNanoOfSecond == 0)
                return this;
            long seconds = MathUtils.safeSubtract(SCALE.getUtcEpochSeconds(this), subSeconds);
            int nanos = getNanoOfSecond()-subNanoOfSecond;
            if (nanos < 0) {
                nanos += NANOS_PER_SECOND;
                seconds = MathUtils.safeDecrement(seconds);
            }
            return SCALE.newScaleInstant(seconds, nanos);
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
