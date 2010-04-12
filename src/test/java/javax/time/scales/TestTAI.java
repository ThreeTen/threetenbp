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

import javax.time.Duration;
import javax.time.Instant;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static javax.time.scales.Util.*;

/** Test TAI time scale
 *
 * @author Mark Thornton
 */
public class TestTAI {
    private static final int NANOS_PER_SECOND = 1000000000;

    @Test public void testFromInstant() {
        testFromInstant(1950, 6, 1, 0);
        testFromInstant(1958, 1, 1, 0);
        testFromInstant(1961, 1, 1, 1422818000);
        testFromInstant(1966, 1, 1, 4313170000L);
        testFromInstant(1970, 1, 1, -1);
        testFromInstant(1971, 12, 31, 10L*NANOS_PER_SECOND-(107758+2592)*1000);
        testFromInstant(1972, 1, 1, 10L*NANOS_PER_SECOND);
        testFromInstant(2010, 1, 1, 34L*NANOS_PER_SECOND);
    }

    private void testFromInstant(int year, int month, int day, long delta) {
        Instant t = Instant.ofSeconds(ScaleUtil.epochSeconds(year, month, day));
        TimeScaleInstant ts = TimeScaleInstant.from(TimeScales.tai(), t);
        long z = (ts.getEpochSeconds()-t.getEpochSeconds())*NANOS_PER_SECOND + (ts.getNanoOfSecond()-t.getNanoOfSecond());
        if (delta == -1) {
            System.out.println("Delta "+z+" for "+ts+", "+t);
        }
        else {
            assertEquals(z, delta);
        }
    }

    @Test public void testToInstant() {
        testToInstant(1950, 6, 1, 0);
        testToInstant(1958, 1, 1, 0);
        testToInstant(1961, 1, 1, 1422818000);
        testToInstant(1966, 1, 1, 4313170000L);
        testToInstant(1971, 12, 31, 10L*NANOS_PER_SECOND-(107758+2592)*1000);
        testToInstant(1972, 1, 1, 10L*NANOS_PER_SECOND);
        testToInstant(2010, 1, 1, 34L*NANOS_PER_SECOND);
    }

    private void testToInstant(int year, int month, int day, long delta) {
        long s = ScaleUtil.epochSeconds(year, month, day);
        TimeScaleInstant ts = TimeScaleInstant.seconds(TimeScales.tai(),
           s+delta/NANOS_PER_SECOND, (int)(delta%NANOS_PER_SECOND));
        Instant t = ts.toInstant();
        assertEquals(t.getEpochSeconds(), s);
        assertEquals(t.getNanoOfSecond(), 0);
    }

    @Test public void testConversions() {
        // test ambigous region prior to 1965-09-01
        // note 100ms step in UTC gives 200ms step in TAI
        cvtFromInstant(date(1965,8,31)+time(23,59,59), millis(900)+1, date(1965,9,1)+time(0,0,3), 955058000);
        cvtFromInstant(date(1965,9,1), 0, date(1965,9,1)+4, 155058000);
        cvtToInstant(date(1965,9,1)+time(0,0,3), 955058000, date(1965,8,31)+time(23,59,59), millis(900)+1);
        cvtToInstant(date(1965,9,1)+time(0,0,4),   5058000, date(1965,8,31)+time(23,59,59), millis(950)+1);
        cvtToInstant(date(1965,9,1)+time(0,0,4),  55058000, date(1965,8,31)+time(23,59,59), millis(900)+2);
        cvtToInstant(date(1965,9,1)+time(0,0,4), 105058000, date(1965,8,31)+time(23,59,59), millis(950)+1);
        cvtToInstant(date(1965,9,1)+time(0,0,4), 155058000, date(1965,9,1), 0);

        // Test near invalid region; result is clamped (should we throw an exception instead)
        cvtFromInstant(date(1968,1,31)+time(23,59,59), millis(910), date(1968,2,1)+6, 185682000);
        cvtFromInstant(date(1968,2,1), 0, date(1968,2,1)+6, 185682000);
        // note the 100ms gap in the UTC timeline
        cvtToInstant(date(1968,2,1)+6, 185681999, date(1968,1,31)+time(23,59,59), millis(900)+2);
        cvtToInstant(date(1968,2,1)+6, 185682000, date(1968,2,1), 0);

        // Test near leap second at 2008-12-31T23:59:60
        cvtFromInstant(date(2008,12,31)+time(23,59,59), millis(100), date(2009,1,1)+time(0,0,32), millis(100));
        cvtFromInstant(date(2009,1,1), millis(200), date(2009,1,1)+time(0,0,34), millis(200));

        cvtToInstant(date(2009,1,1)+time(0,0,31), millis(100), date(2008,12,31)+time(23,59,58), millis(100));
        cvtToInstant(date(2009,1,1)+time(0,0,32), millis(200), date(2008,12,31)+time(23,59,59), millis(200));
        cvtToInstant(date(2009,1,1)+time(0,0,33), millis(300), date(2008,12,31)+time(23,59,59), millis(300));
        cvtToInstant(date(2009,1,1)+time(0,0,34), millis(400), date(2009,1,1)+time(0,0,0), millis(400));
    }

    private void cvtFromInstant(long epochSeconds, int nanoOfSecond, long taiEpochSeconds, int taiNanoOfSecond) {
        Instant t = Instant.ofSeconds(epochSeconds, nanoOfSecond);
        TimeScaleInstant ts = TimeScaleInstant.from(TimeScales.tai(), t);
        assertEquals(ts.getEpochSeconds(), taiEpochSeconds);
        assertEquals(ts.getNanoOfSecond(), taiNanoOfSecond);
    }

    private void cvtToInstant(long taiEpochSeconds, int taiNanoOfSecond, long expectedEpochSeconds, int expectedNanoOfSecond) {
        TimeScaleInstant ts = TimeScaleInstant.seconds(TimeScales.tai(), taiEpochSeconds, taiNanoOfSecond);
        Instant t = ts.toInstant();
        assertEquals(t.getEpochSeconds(), expectedEpochSeconds);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }
    @Test public void testCalculation() {
        assertEquals(instant(date(2008, 12, 31)+time(23,59,59)).plus(Duration.ofSeconds(1)),
           instant(date(2009, 1, 1)));
        assertEquals(instant(date(2009, 1, 1)).minus(Duration.ofSeconds(1)), instant(date(2008, 12, 31)+time(23,59,59)));
        assertEquals(TimeScales.tai().durationBetween(
               instant(date(2008, 12, 31)+time(23,59,59)),
               instant(date(2009, 1, 1))),
           Duration.ofSeconds(1));
        assertEquals(TimeScales.tai().durationBetween(
               instant(date(2008, 12, 31)+time(23,59)),
               instant(date(2009, 1, 1)+60)),
           Duration.ofSeconds(120));
        assertEquals(TimeScales.tai().durationBetween(
               instant(date(2008, 12, 31)+time(23,59)),
               instant(date(2008, 12, 31)+time(23,59,59),0)),
           Duration.ofSeconds(59));
    }

    private static TimeScaleInstant instant(long epochSeconds) {
        return TimeScaleInstant.seconds(TimeScales.tai(), epochSeconds);
    }

    private static TimeScaleInstant instant(long epochSeconds, int nanoOfSecond) {
        return TimeScaleInstant.seconds(TimeScales.tai(), epochSeconds, nanoOfSecond);
    }
}
