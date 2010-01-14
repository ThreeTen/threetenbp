/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.scales;

import javax.time.TimeScaleInstant;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Mark Thornton
 */
public class TestEarlyUTC_TAI {
    private static final int NANOS_PER_SECOND = 1000000000;
    private long maximumError;

    /** Test gaps between adjacent periods. */
    @Test public void testGaps() {
        // expected gaps in microseconds
        // the first of these gaps was defined by me (there is no official information)
        final long[] expectedGaps = {2402, -50000, 0, 100000, 0,
            100000, 100000, 100000, 100000, 100000, 100000,
            0, -100000, 107758};
        final int gapUnit = 1000;   // convert to nanoseconds
        final int n = EarlyUTC_TAI.list().size();
        assertEquals(n, expectedGaps.length);
        long nextDelta = 10L*NANOS_PER_SECOND;  // 10s
        for (int i=n; --i >= 0;) {
            EarlyUTC_TAI.Entry e = EarlyUTC_TAI.list().get(i);
            long delta = e.getUTCDeltaNanoseconds(e.getEndEpochSeconds(), 0);
            long gap = nextDelta-delta;
            assertEquals(gap, gapUnit*expectedGaps[i]);
            nextDelta = e.getUTCDeltaNanoseconds(e.getStartEpochSeconds(), 0);
        }
        assertEquals(nextDelta, 0);
    }

    @Test public void testTAIDelta() {
        maximumError = 0;
        final int M = 25;
        for (EarlyUTC_TAI.Entry e: EarlyUTC_TAI.list()) {
            // sample points within entry
            long step = (e.getEndEpochSeconds()-e.getStartEpochSeconds())/(M+1);
            for (int i=0; i<M; i++) {
                long seconds = e.getStartEpochSeconds() + step/2 +i*step;
                testTAIDelta(e, seconds);
            }
        }
        System.out.println("TAI delta: maximumError="+maximumError);
    }

    private void testTAIDelta(EarlyUTC_TAI.Entry e, long seconds) {
        // try to pick worst case instances near 'seconds'
        int diff = (int)(e.getUTCDeltaNanoseconds(seconds+1, 0) - e.getUTCDeltaNanoseconds(seconds, 0));
        // diff is the number of nanoseconds by which the delta increases every second.
        // It happens that the forward computation is always exact for whole seconds
        int step = NANOS_PER_SECOND/diff;
        for (int nanos = step/2; nanos < NANOS_PER_SECOND; nanos += NANOS_PER_SECOND) {
            for (int i=-5; i<=5; i++) {
                testTAIDelta(e, seconds, nanos+i);
            }
        }
    }

    private void testTAIDelta(EarlyUTC_TAI.Entry e, long seconds, int nanoOfSecond) {
        long delta = e.getUTCDeltaNanoseconds(seconds, nanoOfSecond);
        long nanos = nanoOfSecond+delta;
        int taiNanos = (int)(nanos%NANOS_PER_SECOND);
        long taiSeconds = seconds + nanos/NANOS_PER_SECOND;
        long taiDelta = e.getTAIDeltaNanoseconds(taiSeconds, taiNanos);
        long error = Math.abs(taiDelta-delta);
        /* Note that some error is unvoidable - the results are rounded to the nearest nanosecond
         * With rounding on both calculations, it is impossible to avoid an occasional error of 1.
         * Using infinite precision internally would still gives the same problem.
         * */
        assertTrue(error <= 1, "error outside bounds: "+error);
        if (error > maximumError) {
            maximumError = error;
        }
    }
    
    private void checkSearch(long epochSeconds, EarlyUTC_TAI.Entry expectedEntry) {
        assertEquals(EarlyUTC_TAI.list().entryFromUTC(epochSeconds), expectedEntry);
    }

    /** test search by utc epoch seconds. */
    @Test public void testSearch() {
        for (EarlyUTC_TAI.Entry e: EarlyUTC_TAI.list()) {
            checkSearch(e.getStartEpochSeconds(), e);
            checkSearch(e.getStartEpochSeconds()+(e.getEndEpochSeconds()-e.getStartEpochSeconds())/2, e);
            checkSearch(e.getEndEpochSeconds()-1, e);
        }
    }
    
    private void checkSearch(TimeScaleInstant t, EarlyUTC_TAI.Entry expectedEntry) {
        assertEquals(EarlyUTC_TAI.list().entryFromTAI(t), expectedEntry);
    }

    /** Test search by TAI instant. */
    @Test public void testSearchTAI() {
        TimeScaleInstant tEnd = TAI.START_LEAPSECONDS;
        int n = EarlyUTC_TAI.list().size();
        for (int i=n; --i >= 0;) {
            EarlyUTC_TAI.Entry e = EarlyUTC_TAI.list().get(i);
            TimeScaleInstant t = e.getStartTAI();
            checkSearch(t, e);
            t = TimeScaleInstant.seconds(TAI.INSTANCE, t.getEpochSeconds()+(tEnd.getEpochSeconds()-t.getEpochSeconds())/2);
            checkSearch(t, e);
            long seconds = tEnd.getEpochSeconds();
            int nanos = tEnd.getNanoOfSecond()-1;
            if (nanos < 0) {
                seconds--;
                nanos += NANOS_PER_SECOND;
            }
            t = TimeScaleInstant.seconds(TAI.INSTANCE, seconds, nanos);
            checkSearch(t, e);
            tEnd = e.getStartTAI();
        }
    }

    @Test public void testRounding() {
        // Test value 1971-12-31T23:59:59.75 is carefully chosen to be worst case (or near to).
        long epochSeconds = ScaleUtil.epochSeconds(1971, 12, 31)+((23*60+59)*60+59);
        EarlyUTC_TAI.Entry entry = EarlyUTC_TAI.list().entryFromUTC(epochSeconds);
        long delta0 = entry.getUTCDeltaNanoseconds(epochSeconds, 0);
        // adjust, 1968-02-01, 4.213170, 39126, 0.002592
        long deltaRef = Math.round(NANOS_PER_SECOND*(4.213170+(ScaleUtil.modifiedJulianDay(1972, 1, 1)-39126-1.0/86400)*0.002592));
        assertEquals(delta0, deltaRef);
        // compute deltaRef without floating point:
        // note that 0.002592/SECONDS_PER_DAY = 30ns
        deltaRef = 4213170000L + ((ScaleUtil.modifiedJulianDay(1972, 1, 1)-39126)*86400L-1)*30;
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
