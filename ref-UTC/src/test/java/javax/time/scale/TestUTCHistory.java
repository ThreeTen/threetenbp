package javax.time.scale;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import javax.time.Instant;
import javax.time.Duration;

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
}
