package javax.time.scale;

import javax.time.MathUtils;
import javax.time.Duration;
import javax.time.TimeScale;
import javax.time.Instant;
import java.io.Serializable;

/** Coordinated Universal Time including leap seconds.
 * This scale accounts for all seconds including leap seconds. Instant's on
 * this scale exist in two forms:
 * <ul>
 * <li><i>Plain</i> instants record the epoch seconds NOT counting leap seconds, plus a subsequent leap second count.
 * The total number of seconds from the epoch is computed when required.
 * </li>
 * <li><i>Scale</i> instants record the true number of seconds from the instant. The number of leap seconds since the epoch
 * is computed when required.</li>
 * </ul>
 * In most respects the two forms behave the same (apart from performance). However there is a difference for instants
 * in the future beyond the known extent of leap second data. In this case a <i>plain</i> instant represents a particular clock
 * time but an indeterminate number of real seconds in the future, whereas the <i>scale</i> instant determines the exact
 * number of seconds in the future, but the clock time is unknown.
 * <p>Difference calculation and adding or subtracting durations are faster on <i>scale</i> instants. Converting to/from
 * conventional text form is faster with <i>plain</i> instants.
 * @author Mark Thornton
 */
public class TrueUTC extends AbstractUTC implements Serializable {

    public static final TrueUTC SCALE = new TrueUTC();
    public static final UTCInstant EPOCH = new SimpleInstant(0, 0);
    private static final UTCInstant SCALE_EPOCH = new ScaleInstant(0,0);

    private TrueUTC() {}

    protected Object readResolve() {
        return SCALE;
    }

    @Override
    public String getName() {
        return "TrueUTC";
    }

    public UTCInstant getEpoch() {
        return EPOCH;
    }

    protected javax.time.Instant uncheckedInstant(long simpleEpochSeconds, int nanoOfSecond) {
        return uncheckedInstant(simpleEpochSeconds, nanoOfSecond, 0);
    }

    private UTCInstant uncheckedInstant(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
        return leapSecond == 0 ?
                new SimpleInstant(simpleEpochSeconds, nanoOfSecond) :
                new SimpleLeapInstant(simpleEpochSeconds, nanoOfSecond, leapSecond);
    }

    protected UTCInstant newInstant(long epochSeconds, int nanoOfSecond) {
        return new ScaleInstant(epochSeconds, nanoOfSecond);
    }

    @Override
    protected UTCInstant fromTAI(TAI.Instant tsiTAI) {
        if (tsiTAI.compareTo(UTCHistory.TAI_START_LEAP_SECONDS) < 0) {
            return (UTCInstant)super.fromTAI(tsiTAI);
        }
        return new ScaleInstant(MathUtils.safeSubtract(tsiTAI.getEpochSeconds(), UTCHistory.LEAP_ERA_DELTA), tsiTAI.getNanoOfSecond());
    }

    @Override
    protected TAI.Instant toTAI(javax.time.Instant tsi) {
        UTCInstant utc = (UTCInstant)tsi;
        if (utc.getRecordedEpochSeconds() < UTCHistory.UTC_START_LEAP_SECONDS.getEpochSeconds())
            return super.toTAI(utc.getRecordedEpochSeconds(), utc.getNanoOfSecond());
        return new TAI.Instant(MathUtils.safeAdd(utc.getScaleEpochSeconds(), UTCHistory.LEAP_ERA_DELTA), utc.getNanoOfSecond());
    }

    /** Factory for scale instants.
     *
     * @param epochSeconds true number of seconds since the epoch (including leap seconds)
     * @param nanoOfSecond nanoseconds after epochSeconds
     * @return a true UTC instant
     */
    public UTCInstant scaleInstant(long epochSeconds, int nanoOfSecond) {
        // Check and normalize nanoOfSecond
        if (nanoOfSecond >= NANOS_PER_SECOND) {
            throw new IllegalArgumentException("Nanosecond fraction must not be more than 999,999,999 but was " + nanoOfSecond);
        }
        if (nanoOfSecond < 0) {
            nanoOfSecond += NANOS_PER_SECOND;
            if (nanoOfSecond <= 0) {
                throw new IllegalArgumentException("Nanosecond fraction must not be less than -999,999,999 but was " + nanoOfSecond);
            }
            epochSeconds = MathUtils.safeDecrement(epochSeconds);
        }
        if (epochSeconds == 0 && nanoOfSecond == 0)
            return SCALE_EPOCH;
        return new ScaleInstant(epochSeconds, nanoOfSecond);
    }

    /** Factory for scale instants.
     * @param tsi source instant
     * @return Corresponding TrueUTC scale instant.
     */
    public UTCInstant scaleInstant(javax.time.Instant tsi) {
        if (tsi instanceof UTCInstant) {
            if (tsi instanceof ScaleInstant)
                return (ScaleInstant)tsi;
            UTCInstant utc = (UTCInstant)tsi;
            return new ScaleInstant(utc.getScaleEpochSeconds(), utc.getNanoOfSecond());
        }
        if (tsi.getScale().equals(UTC.SCALE)) {
            UTCHistoryEntry entry = UTCHistory.current().findEntrySimple(tsi.getEpochSeconds());
            return new ScaleInstant(tsi.getEpochSeconds()+entry.getLeapSecondCount(), tsi.getNanoOfSecond());
        }
        return (UTCInstant)instant(tsi);
    }

    /** Factory for plain instants.
     * @param tsi source instant
     * @return Corresponding TrueUTC plain instant
     */
    public UTCInstant plainInstant(javax.time.Instant tsi) {
        if (tsi instanceof UTCInstant) {
            if (tsi instanceof SimpleInstant)
                 return (SimpleInstant)tsi;
            UTCInstant utc = (UTCInstant)tsi;
            return SimpleInstant.createSimpleInstant(utc.getScaleEpochSeconds(), utc.getNanoOfSecond());
        }
        if (tsi.getScale().equals(UTC.SCALE)) {
            return new SimpleInstant(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
        }
        // convert to TAI
        tsi = TAI.SCALE.instant(tsi);
        // convert TAI value to UTC
        long s = tsi.getEpochSeconds();
        int nanoOfSecond = tsi.getNanoOfSecond();
        if (tsi.compareTo(UTCHistory.TAI_START_LEAP_SECONDS) >=  0) {
            s -= UTCHistory.LEAP_ERA_DELTA;
            return SimpleInstant.createSimpleInstant(s, nanoOfSecond);
        }
        else {
            UTCHistoryEntry entry = UTCHistory.current().findEntry(tsi);
            long delta = entry.getTAIDeltaNanoseconds(s, nanoOfSecond) - nanoOfSecond;
            nanoOfSecond = (int)(delta%NANOS_PER_SECOND);
            s -= (delta-nanoOfSecond)/NANOS_PER_SECOND;
            if (nanoOfSecond > 0) {
                 nanoOfSecond = NANOS_PER_SECOND - nanoOfSecond;
                s--;
            }
            return new SimpleInstant(s, nanoOfSecond);
        }
    }

    /** Factory for TrueUTC plain instants.
     * @param simpleEpochSeconds non leap seconds since the epoch
     * @param nanoOfSecond nanoseconds after (simpleEpochSeconds+leapSecond)
     * @param leapSecond leap seconds immediately after simpleEpochSeconds
     * @return a plain TrueUTC instant
     */
    @Override
    public UTCInstant instant(long simpleEpochSeconds, int nanoOfSecond, int leapSecond) {
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
        if (leapSecond < 0)
            throw new IllegalArgumentException("leapSecond must be non negative: "+leapSecond);
        if (leapSecond > 0 && simpleEpochSeconds < UTCHistory.current().getMaximumKnownInstant().getEpochSeconds()) {
            // check validity of leap second
            UTCHistoryEntry entry = UTCHistory.current().findEntrySimple(simpleEpochSeconds);
            if (entry.getNext() == null ||
                    !entry.getNext().isLeapEntry() ||
                    simpleEpochSeconds != entry.getNext().getStartUTC().getEpochSeconds()-1) {
                throw new IllegalArgumentException("Invalid leapSecond "+leapSecond);
            }
        }
        return uncheckedInstant(simpleEpochSeconds, nanoOfSecond, leapSecond);
    }

    /** TrueUTC instant matching given Instant.
     * If the supplied instant is a TrueUTC instant (of either kind) it is simply returned. If it is a UTC instant,
     * then the corresponding plain TrueUTC instant is returned. Otherwise, the corresponding scale UTC instant is
     * computed.
     * @param tsi source instant
     * @return corresponding TrueUTC instant
     */
    @Override
    public Instant instant(Instant tsi) {
        if (tsi.getScale().equals(UTC.SCALE)) {
            return new SimpleInstant(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
        }
        return super.instant(tsi);
    }

    public static abstract class UTCInstant extends javax.time.Instant {
        UTCInstant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        public TimeScale getScale() {
            return SCALE;
        }

        /** True number of seconds since epoch.
         * @return seconds since epoch <i>including</i> leap seconds.
         */
        public abstract long getScaleEpochSeconds();

        long getRecordedEpochSeconds()
        {
            return super.getEpochSeconds();
        }

        static int getLeapSecond(UTCHistoryEntry entry, long scaleEpochSeconds) {
            if (entry.getNext() != null && entry.getNext().isLeapEntry()) {
                long leap = scaleEpochSeconds-entry.getLeapSecondCount() - entry.getNext().getStartUTC().getEpochSeconds();
                if (leap >= 0)
                    return (int)(leap+1);
            }
            return 0;
        }

        @Override
        protected Duration difference(javax.time.Instant b) {
            long seconds = MathUtils.safeSubtract(getScaleEpochSeconds(), ((UTCInstant)b).getScaleEpochSeconds());
            int nanos = getNanoOfSecond() - b.getNanoOfSecond();
            if (nanos < 0) {
                nanos += NANOS_PER_SECOND;
                seconds = MathUtils.safeDecrement(seconds);
            }
            return Duration.duration(seconds, nanos);
        }
    }

    private static class SimpleInstant extends UTCInstant {
        SimpleInstant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        protected UTCHistoryEntry getEntry() {
            // Could cache the result in a transient field
            return UTCHistory.current().findEntrySimple(getEpochSeconds());
        }

        public long getScaleEpochSeconds() {
            return getEpochSeconds()+getEntry().getLeapSecondCount()+getLeapSecond();
        }

        private static SimpleInstant createSimpleInstant(long scaleEpochSeconds, int nanoOfSecond) {
            UTCHistoryEntry entry = UTCHistory.current().findEntryTrue(scaleEpochSeconds);
            int leapSecond = getLeapSecond(entry, scaleEpochSeconds);
            long epochSeconds = scaleEpochSeconds - (entry.getLeapSecondCount() + leapSecond);
            return leapSecond == 0 ?
                    new SimpleInstant(epochSeconds, nanoOfSecond) :
                    new SimpleLeapInstant(epochSeconds, nanoOfSecond, leapSecond);
        }

        @Override
        protected javax.time.Instant plus(long addSeconds, int addNanoOfSecond) {
            if (addSeconds == 0 && addNanoOfSecond == 0)
                return this;
            long scaleEpochSeconds = MathUtils.safeAdd(getScaleEpochSeconds(), addSeconds);
            int nanoOfSecond = getNanoOfSecond()+addNanoOfSecond;
            if (nanoOfSecond >= NANOS_PER_SECOND) {
                nanoOfSecond -= NANOS_PER_SECOND;
                scaleEpochSeconds = MathUtils.safeDecrement(scaleEpochSeconds);
            }
            return createSimpleInstant(scaleEpochSeconds, nanoOfSecond);
        }

        @Override
        protected javax.time.Instant minus(long subSeconds, int subNanoOfSecond) {
            if (subSeconds == 0 && subNanoOfSecond == 0)
                return this;
            long scaleEpochSeconds = MathUtils.safeSubtract(getScaleEpochSeconds(), subSeconds);
            int nanoOfSecond = getNanoOfSecond() - subNanoOfSecond;
            if (nanoOfSecond < 0) {
                nanoOfSecond += NANOS_PER_SECOND;
                scaleEpochSeconds = MathUtils.safeIncrement(scaleEpochSeconds);
            }
            return createSimpleInstant(scaleEpochSeconds, nanoOfSecond);
        }
    }

    private static class SimpleLeapInstant extends SimpleInstant {
        private final int leapSecond;

        SimpleLeapInstant(long epochSeconds, int nanoOfSecond, int leapSecond) {
            super(epochSeconds, nanoOfSecond);
            this.leapSecond = leapSecond;
        }

        @Override
        public int getLeapSecond() {
            return leapSecond;
        }
    }

    private static class ScaleInstant extends UTCInstant {
        ScaleInstant(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        protected UTCHistoryEntry getEntry() {
            // Could cache the result in a transient field
            return UTCHistory.current().findEntryTrue(getScaleEpochSeconds());
        }

        @Override
        public long getEpochSeconds() {
            UTCHistoryEntry entry = getEntry();
            return super.getEpochSeconds()-entry.getLeapSecondCount()- getLeapSecond(entry, super.getEpochSeconds());
        }

        public long getScaleEpochSeconds() {
            return super.getEpochSeconds();
        }

        @Override
        public int getLeapSecond() {
            return getLeapSecond(getEntry(), getScaleEpochSeconds());
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
            return new ScaleInstant(seconds, nanos);
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
            return new ScaleInstant(seconds, nanos);
        }
    }
}
