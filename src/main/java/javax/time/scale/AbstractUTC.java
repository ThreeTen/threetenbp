package javax.time.scale;

import javax.time.TimeScale;
import javax.time.Instant;
import javax.time.MathUtils;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

/** Model UTC before 1972.
 * 
 */
public abstract class AbstractUTC extends TimeScale {
    private static int MJD19700101 = Scale.modifiedJulianDay(1970, 1, 1);
    private static long SECONDS_PER_DAY = 86400L;
    protected static final int leapEraDelta = 10;
    private static final Entry[] entries;
    private static final Instant[] startInstants;
    private static Instant leapEraInstant;
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
        startInstants = new Instant[entries.length];
        Entry leapEraEntry = null;
        for (int i=0; i<entries.length; i++) {
            AbstractUTC.Entry e = entries[i];
            e.computeTerms();
            long delta = e.getDelta(e.getStartInclusiveSeconds(), 0);
            int nano = (int)(delta%NANOS_PER_SECOND);
            long epochSeconds = e.getStartInclusiveSeconds()+(delta/NANOS_PER_SECOND);
            startInstants[i] = taiInstant(epochSeconds, nano);
            if (e.deltaSeconds == 10)
            {
                leapEraEntry = e;
                leapEraInstant = startInstants[i];
            }
        }
        oldEraSeconds = entries[0].startInclusiveSeconds;
        leapEraSeconds = leapEraEntry.startInclusiveSeconds;
    }

    /** Start of leap seconds.
     *
     * @return Instant after which UTC is adjusted by whole seconds.
     */
    public static Instant getLeapEraInstant() {return leapEraInstant;}

    /** find entry containing t.
     *
     * @param t Instant to locate
     * @return null if t is before all defined entries, otherwise latest entry where the start second
     * is equal to or precedes t.
     */
    public static AbstractUTC.Entry findEntry(Instant t) {
        for (int i=startInstants.length; --i >= 0;) {
            if (startInstants[i].compareTo(t) <= 0)
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

    @Override
    public TimeScaleInstant getTimeScaleInstant(Instant t) {
        Entry entry = findEntry(t);
        if (entry == null) {
            // Time is before 1961-01-01
            return TimeScaleInstant.instant(t.getEpochSeconds(), t.getNanoOfSecond());
        }
        // compute exact offset
        long s = getTaiEpochSeconds(t);
        int nanos = getTaiNanosOfSecond(t);
        long delta = entry.getDeltaTAI(s, nanos) - nanos;
        assert delta > 0;   // true for 1961 - 1972
        nanos = (int)(delta%NANOS_PER_SECOND);
        if (nanos < 0)
            nanos += NANOS_PER_SECOND;
        return TimeScaleInstant.instant(s-(delta-nanos)/NANOS_PER_SECOND, nanos);
    }

    @Override
    public long getEpochSeconds(Instant t) {
        Entry entry = findEntry(t);
        if (entry == null) {
            // Time is before 1961-01-01
            return getTaiEpochSeconds(t);
        }
        // compute exact offset
        long s = getTaiEpochSeconds(t);
        int nanos = getTaiNanosOfSecond(t);
        long delta = entry.getDeltaTAI(s, nanos) - nanos;
        assert delta > 0;   // true for 1961 - 1972
        return s-(delta+NANOS_PER_SECOND-1)/NANOS_PER_SECOND;
    }

    @Override
    public int getNanoOfSecond(Instant t) {
        Entry entry = findEntry(t);
        if (entry != null) {
            // compute exact offset
            long s = getTaiEpochSeconds(t);
            int nanos = getTaiNanosOfSecond(t);
            nanos = (int)((entry.getDeltaTAI(s, nanos) - getTaiNanosOfSecond(t))%NANOS_PER_SECOND);
            if (nanos != 0)
                nanos = NANOS_PER_SECOND-nanos;
            return nanos;
        }
        return getTaiNanosOfSecond(t);
    }

    @Override
    public Instant instant(long epochSeconds, int nanoOfSecond) {
        assert epochSeconds < leapEraSeconds;
        if (epochSeconds < oldEraSeconds) {
            return checkedTaiInstant(epochSeconds, nanoOfSecond);
        }
        Entry entry = findEntry(epochSeconds);
        long delta = entry.getDelta(epochSeconds, nanoOfSecond);
        if (nanoOfSecond >= NANOS_PER_SECOND) {
            throw new IllegalArgumentException("Nanosecond fraction must not be more than 999,999,999 but was " + nanoOfSecond);
        }
        if (nanoOfSecond <= -NANOS_PER_SECOND) {
               throw new IllegalArgumentException("Nanosecond fraction must not be less than -999,999,999 but was " + nanoOfSecond);
        }
        delta += nanoOfSecond;
        nanoOfSecond = (int)(delta % NANOS_PER_SECOND);
        return taiInstant(MathUtils.safeAdd(epochSeconds, delta/NANOS_PER_SECOND), nanoOfSecond);
    }

    public Instant instant(TimeScaleInstant tsi) {
        if (tsi.getLeapSecond() != 0)
            throw new IllegalArgumentException("Invalid leap second");
        if (tsi.getIncludedLeapSeconds() != 0)
            throw new IllegalArgumentException("There are no leap seconds in this era");
        return instant(tsi.getEpochSeconds(), tsi.getNanoOfSecond());
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
