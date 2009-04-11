package javax.time.scale;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import javax.time.Instant;
import javax.time.Duration;
import static javax.time.scale.TestScale.*;

/** Test history of TAI-UTC.
 */
@Test
public class TestUTCHistory {
    private static final int NANOS_PER_SECOND = 1000000000;

    public void testReversible() {
        // number of points to test in each interval
        final int N = 1000000;
        for (UTCHistoryEntry entry: UTCHistory.current().getEntries()) {
            if (entry.isStandardSecond())
                continue;
            Instant start = entry.getStartTAI();
            Instant finish = entry.getNext().getStartTAI(); // assume entries with non standard seconds always have a subsequent entry
            Duration range = finish.durationFrom(start);
            for (int i=0; i<=N; i++) {
                Instant t;
                if (i == 0)
                    t = start;
                else if (i == N)
                    t = finish;
                else {
                    // compute a value approximately i/N into the range (doesn't have to be accurate)
                    double f = i/(double)N;
                    double s = f*range.getSeconds();
                    long epochSeconds = (long)Math.floor(s);
                    int nanoOfSecond = (int)Math.round((s-epochSeconds)*NANOS_PER_SECOND+f*range.getNanoOfSecond());
                    epochSeconds += start.getEpochSeconds();
                    while (nanoOfSecond >= NANOS_PER_SECOND) {
                        epochSeconds++;
                        nanoOfSecond -= NANOS_PER_SECOND;
                    }
                    t = TAI.SCALE.instant(epochSeconds, nanoOfSecond);
                }
                // compute offset given TAI instant
                long tai_utc = entry.getTAIDeltaNanoseconds(t.getEpochSeconds(), t.getNanoOfSecond());
                // calculate UTC instant by subtracting tai_utc nanoseconds from the TAI instant
                long nanos = t.getNanoOfSecond()-tai_utc;
                long epochSeconds = t.getEpochSeconds()+(nanos/NANOS_PER_SECOND);
                int nanoOfSecond = (int)(nanos%NANOS_PER_SECOND);
                if (nanoOfSecond < 0) {
                    nanoOfSecond += NANOS_PER_SECOND;
                    epochSeconds--;
                }
                // Compute offset given UTC instant
                long tai2utc = entry.getUTCDeltaNanoseconds(epochSeconds, nanoOfSecond);
                // should be very close
                if (tai2utc != tai_utc)
                    System.out.println("Conversion inexact for t="+t+", "+tai2utc+" != "+tai_utc);
                assertTrue(Math.abs(tai2utc-tai_utc) <= 1);
            }
        }
    }

    public void testGaps() {
        double[] expected = {0, 0.002402, -0.05, 0, 0.1, 0, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1,
            0, -0.1, 0.107758};
        int index=0;
        for (UTCHistoryEntry entry: UTCHistory.current().getEntries()) {
            if (entry.getPrevious() == null)
                continue;
            Instant t = entry.getStartUTC();
            long tBefore = entry.getPrevious().getUTCDeltaNanoseconds(t.getEpochSeconds(), t.getNanoOfSecond());
            long tAfter = entry.getUTCDeltaNanoseconds(t.getEpochSeconds(), t.getNanoOfSecond());
            long step = tAfter-tBefore;
            System.out.println(t+", step="+step+"ns");
            long expectedGap;
            if (index < expected.length)
                expectedGap = Math.round(NANOS_PER_SECOND*expected[index]);
            else
                expectedGap = NANOS_PER_SECOND;
            assertEquals(step, expectedGap);
            index++;
        }
    }

    private void checkSearch(UTCHistory history, UTCHistoryEntry entry, long tSimple, long tUTC, Instant tai) {
        assertEquals(history.findEntrySimple(tSimple), entry);
        assertEquals(history.findEntryTrue(tUTC), entry);
        assertEquals(history.findEntry(tai), entry);
    }

    public void testSearch() {
        UTCHistory history = UTCHistory.current();
        for (UTCHistoryEntry entry: history.getEntries()) {
            if (entry.getStartUTC() == null) {
                UTCHistoryEntry next = entry.getNext();
                long t = next.getStartUTC().getEpochSeconds()-1;
                checkSearch(history, entry, t, t+next.getLeapSecondCount(),
                        new TAI.Instant(next.getStartTAI().getEpochSeconds()-1, 0));
            }
            else {
                long t = entry.getStartUTC().getEpochSeconds();
                checkSearch(history, entry, t,
                        t+entry.getLeapSecondCount(),
                        entry.getStartTAI());
                checkSearch(history, entry, t+1, t+entry.getLeapSecondCount()+1,
                        new TAI.Instant(entry.getStartTAI().getEpochSeconds()+1, 0));
            }
        }
    }

    public void testRounding() {
        // Test value 1971-12-31T23:59:59.75 is carefully chosen to be worst case (or near to).
        long epochSeconds = date(1971, 12, 31)+time(23, 59, 59);
        UTCHistoryEntry entry = UTCHistory.current().findEntrySimple(epochSeconds);
        long delta0 = entry.getUTCDeltaNanoseconds(epochSeconds, 0);
        // adjust, 1968-02-01, 4.213170, 39126, 0.002592
        long deltaRef = Math.round(NANOS_PER_SECOND*(4.213170+(Scale.modifiedJulianDay(1972, 1, 1)-39126-1.0/86400)*0.002592));
        assertEquals(delta0, deltaRef);
        // compute deltaRef without floating point:
        // note that 0.002592/NANOS_PER_SECOND = 30ns
        deltaRef = 4213170000L + ((Scale.modifiedJulianDay(1972, 1, 1)-39126)*86400L-1)*30;
        assertEquals(delta0, deltaRef);
        int nanoOfSecond = 750000000;   // 0.75
        // The delta changes at a rate of 30ns/s, so 0.75s should result in the exact delta including 0.5ns and
        // thus being rounded up. Any time before this ought to be rounded down.
        long delta = entry.getUTCDeltaNanoseconds(epochSeconds, nanoOfSecond);
        assertEquals(delta, delta0+23); // 23 == Math.round(0.75*30)
        int i;
        for (i=1; entry.getUTCDeltaNanoseconds(epochSeconds, nanoOfSecond-i) == delta; i++) {}
        // if the delta was correctly rounded, i==1. In fact double doesn't have enough bits
        // to avoid a small error.
        // We report here the deviation from correct rounding
        System.out.println("rounding error: "+(i-1)*30e-18+"s, relative error: "+((i-1)*30e-9/delta));
        // assert if result is not as good as obtained using double
        assertTrue(i> 0 && i<=15, "Error too large: "+i);

        // now try reverse conversion
        long taiEpochSeconds = epochSeconds + (delta/NANOS_PER_SECOND);
        int taiNanoOfSecond = nanoOfSecond + (int)(delta%NANOS_PER_SECOND);
        if (taiNanoOfSecond > NANOS_PER_SECOND) {
            taiNanoOfSecond -= NANOS_PER_SECOND;
            taiEpochSeconds++;
        }
        long taiDelta = entry.getTAIDeltaNanoseconds(taiEpochSeconds, taiNanoOfSecond);
        System.out.println("taiDelta-delta="+(taiDelta-delta));
        for (i=1; entry.getTAIDeltaNanoseconds(taiEpochSeconds, taiNanoOfSecond-i) == delta; i++) {}
        assertTrue(i> 0 && i<=16, "Error too large: "+i);
    }
}
