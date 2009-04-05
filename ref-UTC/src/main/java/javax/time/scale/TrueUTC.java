package javax.time.scale;

import javax.time.MathUtils;
import javax.time.Duration;
import java.io.Serializable;

/*
TODO: Combine the current UTC and UTC_NoEpochLeap clases into a single timescale. The distinction is what information is
supplied on construction. The timescale is otherwise the same. In both cases cache a (transient) reference to the
total number of leap seconds.
 */

/** Coordinated Universal Time including leap seconds.
     * The epochSeconds include any leap seconds within the interval. Thus epochSeconds are
     * TAI-10s (10 seconds was the initial offset) for times after 1972-01-01T00:00.
 * @author Mark Thornton
 */
public class TrueUTC extends AbstractUTC implements Serializable {
    public static final TrueUTC SCALE = new TrueUTC();
    public static final Instant EPOCH = new Instant(0, 0, 0);

    private TrueUTC() {}

    protected Object readResolve() {
        return SCALE;
    }

    @Override
    public String getName() {
        return "TrueUTC";
    }

    public Instant getEpoch() {
        return EPOCH;
    }

    protected javax.time.Instant uncheckedInstant(long simpleEpochSeconds, int nanoOfSecond) {
        return uncheckedInstant(simpleEpochSeconds, nanoOfSecond, 0);
    }

    private Instant uncheckedInstant(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
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
            return new LeapInstant(simpleEpochSeconds+delta+leapSecond, nanoOfSecond, delta, leapSecond);
        }
        return new Instant(simpleEpochSeconds+delta, nanoOfSecond, delta);
    }

    protected Instant newInstant(long epochSeconds, int nanoOfSecond) {
        return new Instant(epochSeconds, nanoOfSecond, 0);
    }

    @Override
    protected Instant fromTAI(TAI.Instant tsiTAI) {
        if (InstantComparator.INSTANCE.compare(tsiTAI, getLeapEraInstant()) < 0) {
            return (Instant)super.fromTAI(tsiTAI);
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
    protected TAI.Instant toTAI(javax.time.Instant tsi) {
        Instant utc = (Instant)tsi;
        if (utc.getScaleEpochSeconds() < leapEraSeconds)
            return super.toTAI(tsi);
        return new TAI.Instant(MathUtils.safeAdd(utc.getScaleEpochSeconds(), leapEraDelta), utc.getNanoOfSecond());
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
        return uncheckedInstant(simpleEpochSeconds, nanoOfSecond, leapSecond);
    }

    private javax.time.Instant newScaleInstant(long seconds, int nanos) {
        if (seconds < leapEraSeconds)
            return new Instant(seconds, nanos, 0);
        int leapSecond = 0;
        Entry entry = findEntryUTC(seconds);
        int delta = entry.getDeltaSeconds()-leapEraDelta;
        if (entry.getNext() != null) {
            long s = seconds-delta-entry.getEndExclusiveSeconds();
            if (s >= 0)
                leapSecond = 1+(int)s;
        }
        if (leapSecond == 0)
            return new Instant(seconds, nanos, delta);
        else
            return new LeapInstant(seconds, nanos, delta, leapSecond);
    }

    public static class Instant extends javax.time.Instant {
        /*
        TODO: record only epochSeconds and nanoOfSecond
        add a transient reference to the included leapseconds and current leap second (calculated on the basis of the current data)
        then don't need the LeapInstant subclass
         */
        private final int includedLeapSeconds;

        Instant(long epochSeconds, int nanoOfSecond, int includedLeapSeconds) {
            super(epochSeconds, nanoOfSecond);
            this.includedLeapSeconds = includedLeapSeconds;
        }

        @Override
        public long getEpochSeconds() {
            return super.getEpochSeconds()-includedLeapSeconds-getLeapSecond();
        }

        public TrueUTC getScale() {
            return SCALE;
        }

        public long getScaleEpochSeconds() {
            return super.getEpochSeconds();
        }

        @Override
        protected Duration difference(javax.time.Instant b) {
            long seconds = MathUtils.safeSubtract(getScaleEpochSeconds(), ((Instant)b).getScaleEpochSeconds());
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
            long seconds = MathUtils.safeAdd(getScaleEpochSeconds(), addSeconds);
            int nanos = getNanoOfSecond() + addNanoOfSecond;
            if (nanos >= NANOS_PER_SECOND) {
                seconds = MathUtils.safeIncrement(seconds);
                nanos -= NANOS_PER_SECOND;
            }
            return SCALE.newScaleInstant(seconds, nanos);
        }

        @Override
        protected javax.time.Instant minus(long subSeconds, int subNanoOfSecond) {
            if (subSeconds == 0 && subNanoOfSecond == 0)
                return this;
            long seconds = MathUtils.safeSubtract(getScaleEpochSeconds(), subSeconds);
            int nanos = getNanoOfSecond() - subNanoOfSecond;
            if (nanos < 0) {
                seconds = MathUtils.safeDecrement(seconds);
                nanos += NANOS_PER_SECOND;
            }
            return SCALE.newScaleInstant(seconds, nanos);
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
