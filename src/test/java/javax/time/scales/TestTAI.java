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

import javax.time.Instant;
import javax.time.TimeScaleInstant;
import javax.time.TimeScales;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

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
        Instant t = Instant.seconds(ScaleUtil.epochSeconds(year, month, day));
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
}
