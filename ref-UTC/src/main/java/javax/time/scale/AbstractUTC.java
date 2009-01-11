package javax.time.scale;

import javax.time.scale.TimeScale;
import javax.time.MathUtils;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

/** Model UTC before 1972.
 * Prior to the start of UTC in 1961-01-01 time reverts to TAI. It might be better to use a smooth transition ending at 
 * parity in 1958-01-01 (when TAI matched UT2).
 * @author Mark Thornton
 */
public abstract class AbstractUTC<T extends AbstractInstant<T>> extends TimeScale<T> {
    private static final int MJD19700101 = Scale.modifiedJulianDay(1970, 1, 1);
    private static final long SECONDS_PER_DAY = 86400L;
    protected static final int leapEraDelta = 10;
    private static final Entry[] entries;
    private static final AbstractInstant[] startInstants;
    private static final AbstractInstant leapEraInstant;
    public static final List<Entry> UTC_ENTRIES;

    protected static final long leapEraSeconds;
    protected static final long oldEraSeconds;

    static
    {
        entries = new UTCHistoryLoader().entries();
        UTC_ENTRIES = Collections.unmodifiableList(Arrays.asList(entries));
        for (int i=1; i<entries.length; i++)
        {
            entries[i-1].next = entries[i];
            entries[i].previous = entries[i-1];
        }
        startInstants = new AbstractInstant[entries.length];
        Entry leapEraEntry = null;
        AbstractInstant startLeapEra = null;
        for (int i=0; i<entries.length; i++) {
            AbstractUTC.Entry e = entries[i];
            e.computeTerms();
            long delta = e.getDelta(e.getStartInclusiveSeconds(), 0);
            int nano = (int)(delta%NANOS_PER_SECOND);
            long epochSeconds = e.getStartInclusiveSeconds()+(delta/NANOS_PER_SECOND);
            startInstants[i] = TAI.SCALE.instant(epochSeconds, nano);
            if (e.deltaSeconds == 10)
            {
                leapEraEntry = e;
                startLeapEra = startInstants[i];
            }
        }
        leapEraInstant = startLeapEra;
        oldEraSeconds = entries[0].startInclusiveSeconds;
        leapEraSeconds = leapEraEntry.startInclusiveSeconds;
    }

    /** Start of leap seconds.
     *
     * @return Instant after which UTC is adjusted by whole seconds.
     */
    public static AbstractInstant getLeapEraInstant() {return leapEraInstant;}

    /** find entry containing t.
     *
     * @param t Instant to locate
     * @return null if t is before all defined entries, otherwise latest entry where the start second
     * is equal to or precedes t.
     */
    public static AbstractUTC.Entry findEntry(AbstractInstant t) {
        for (int i=startInstants.length; --i >= 0;) {
            if (InstantComparator.INSTANCE.compare(startInstants[i], t) <= 0)
                return entries[i];
        }
        return null;
    }

    public static AbstractUTC.Entry findEntry(long epochSeconds) {
        for (int i=entries.length; --i >= 0;) {
            if (entries[i].getStartInclusiveSeconds() <= epochSeconds)
                return entries[i];
        }
        return null;
    }

    protected T fromTAI(TAI.Instant tsiTAI) {
        // if before 1961 simply return input
        Entry entry = findEntry(tsiTAI);
        if (entry == null) {
            return getEpoch().factory(tsiTAI.getEpochSeconds(), tsiTAI.getNanoOfSecond(), 0);
        }
        long s = tsiTAI.getEpochSeconds();
        int nanos = tsiTAI.getNanoOfSecond();
        long delta = entry.getDeltaTAI(s, nanos) - nanos;
        assert delta > 0;   // true for 1961 - 1972
        nanos = (int)(delta%NANOS_PER_SECOND);
        if (nanos < 0)
            nanos += NANOS_PER_SECOND;
        return getEpoch().factory(s-(delta-nanos)/NANOS_PER_SECOND, nanos, 0);
    }

    protected TAI.Instant toTAI(T tsi) {
        if (tsi.getLeapSecond() != 0)
            throw new IllegalArgumentException("Invalid leap second");
        // if before 1961 simply return input
        assert tsi.getEpochSeconds() < leapEraSeconds;
        if (tsi.getEpochSeconds() < oldEraSeconds) {
            return TAI.SCALE.instant(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
        }
        Entry entry = findEntry(tsi.getEpochSeconds());
        long delta = entry.getDelta(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
        delta += tsi.getNanoOfSecond();
        return TAI.SCALE.instant(MathUtils.safeAdd(tsi.getEpochSeconds(), delta/NANOS_PER_SECOND), (int)(delta % NANOS_PER_SECOND));
    }

    protected Entry findEntryUTC(long utcEpochSeconds) {
        utcEpochSeconds+=leapEraDelta;
        for (int i=UTC_ENTRIES.size(); --i >= 0;) {
            Entry e = UTC_ENTRIES.get(i);
            if (e.getStartInclusiveSeconds()+e.getDeltaSeconds() <= utcEpochSeconds)
                return e;
        }
        return null;
    }

    public static class Entry {
        private Entry previous;
        private Entry next;
        private long startInclusiveSeconds; // in UTC
        private long endExclusiveSeconds;   // in UTC
        private int deltaSeconds;
        private int deltaNanoseconds;
        private int rateNanoseconds; // change in delta per day
        private int originMJD;
        private boolean hasRate;

        private long taiT0;
        private double taiT1;
        private long taiX0;
        private long t0;
        private double t1;
        private long x0;

        private void computeTerms() {
            // first compute terms for UTC seconds
            t0 = deltaSeconds* (long)NANOS_PER_SECOND +deltaNanoseconds;
            x0 = SECONDS_PER_DAY *(originMJD- MJD19700101);
            t1 = rateNanoseconds/(double) SECONDS_PER_DAY;
            // now compute values for TAI seconds
            long da = getDelta(startInclusiveSeconds, 0);
            long db = getDelta(endExclusiveSeconds, 0);
            long r = endExclusiveSeconds-startInclusiveSeconds;
            taiT1 = t1 *(r/(r+1e-9*(db-da)));
            taiX0 = startInclusiveSeconds+r/2;  // any value in or near the range will do
            taiT0 = 0;
            taiT0 = da - getDeltaTAI(startInclusiveSeconds+da/NANOS_PER_SECOND, (int)(da%NANOS_PER_SECOND));
        }

        Entry startInclusive(long seconds) {
            startInclusiveSeconds = seconds;
            return this;
        }

        Entry endExclusive(long seconds) {
            endExclusiveSeconds = seconds;
            return this;
        }

        Entry deltaSeconds(int seconds) {
            deltaSeconds = seconds;
            deltaNanoseconds = 0;
            return this;
        }

        Entry delta(long nanoseconds) {
            deltaNanoseconds = (int)(nanoseconds% NANOS_PER_SECOND);
            deltaSeconds = (int)(nanoseconds/ NANOS_PER_SECOND);
            return this;
        }

        Entry rate(int nanoseconds, int origin) {
            rateNanoseconds = nanoseconds;
            originMJD = origin;
            hasRate = true;
            return this;
        }

        public Entry getNext() {return next;}
        public Entry getPrevious() {return previous;}

        public long getStartInclusiveSeconds() {
            return startInclusiveSeconds;
        }

        public long getEndExclusiveSeconds() {
            return endExclusiveSeconds;
        }

        public int getDeltaSeconds() {
            return deltaSeconds;
        }

        public int getDeltaNanoseconds() {
            return deltaNanoseconds;
        }

        public int getRateNanoseconds() {
            return rateNanoseconds;
        }

        public int getOriginMJD() {
            return originMJD;
        }

        public boolean hasRate() {
            return hasRate;
        }

        public long getDelta(long epochSeconds, int nanoOfSecond) {
            // This requires input resolution of 0.01s to achieve nanosecond accuracy in the output
            if (hasRate)
                return t0+Math.round(t1*(epochSeconds-x0)+1e-9*nanoOfSecond);
            else
                return t0;
        }

        public long getDeltaTAI(long taiEpochSeconds, int nanoOfSecond) {
            if (!hasRate)
                return taiT0;
            else
                return taiT0 + Math.round(taiT1*((taiEpochSeconds-taiX0)+1e-9*nanoOfSecond));
        }
    }
}
