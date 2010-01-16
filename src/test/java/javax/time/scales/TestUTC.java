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
import javax.time.TimeScaleInstant;
import javax.time.TimeScales;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static javax.time.scales.Util.*;

/** Test UTC TimeScale.
 * @author Mark Thornton
 */
public class TestUTC {
    /* The conversion between Instant and TimeScales.utc() is trivial so not tested here.
     * It is the conversion between TAI and UTC which is interesting
     * */

    @Test public void testUtcConversions() {
        // test ambigous region prior to 1965-09-01
        // note 100ms step in UTC gives 200ms step in TAI
        cvtToTAI(date(1965,8,31)+time(23,59,59), millis(900)+1, date(1965,9,1)+time(0,0,3), 955058000);
        cvtToTAI(date(1965,9,1), 0, date(1965,9,1)+4, 155058000);
        cvtFromTAI(date(1965,9,1)+time(0,0,3), 955058000, date(1965,8,31)+time(23,59,59), millis(900)+1);
        cvtFromTAI(date(1965,9,1)+time(0,0,4),   5058000, date(1965,8,31)+time(23,59,59), millis(950)+1);
        cvtFromTAI(date(1965,9,1)+time(0,0,4),  55058000, date(1965,8,31)+time(23,59,59), millis(900)+2);
        cvtFromTAI(date(1965,9,1)+time(0,0,4), 105058000, date(1965,8,31)+time(23,59,59), millis(950)+1);
        cvtFromTAI(date(1965,9,1)+time(0,0,4), 155058000, date(1965,9,1), 0);

        // Test near invalid region; result is clamped (should we throw an exception instead)
        cvtToTAI(date(1968,1,31)+time(23,59,59), millis(910), date(1968,2,1)+6, 185682000);
        cvtToTAI(date(1968,2,1), 0, date(1968,2,1)+6, 185682000);
        // note the 100ms gap in the UTC timeline
        cvtFromTAI(date(1968,2,1)+6, 185681999, date(1968,1,31)+time(23,59,59), millis(900)+2);
        cvtFromTAI(date(1968,2,1)+6, 185682000, date(1968,2,1), 0);

        // Test near leap second at 2008-12-31T23:59:60
        cvtToTAI(date(2008,12,31)+time(23,59,59), millis(100), date(2009,1,1)+time(0,0,32), millis(100));
        cvtToTAI(date(2009,1,1), millis(200), date(2009,1,1)+time(0,0,34), millis(200));

        cvtFromTAI(date(2009,1,1)+time(0,0,31), millis(100), date(2008,12,31)+time(23,59,58), millis(100));
        cvtFromTAI(date(2009,1,1)+time(0,0,32), millis(200), date(2008,12,31)+time(23,59,59), millis(200));
        cvtFromTAI(date(2009,1,1)+time(0,0,33), millis(300), date(2008,12,31)+time(23,59,59), millis(300));
        cvtFromTAI(date(2009,1,1)+time(0,0,34), millis(400), date(2009,1,1)+time(0,0,0), millis(400));
    }

    private void cvtToTAI(long epochSeconds, int nanoOfSecond, long taiEpochSeconds, int taiNanoOfSecond) {
        TimeScaleInstant t = TimeScaleInstant.seconds(TimeScales.utc(), epochSeconds, nanoOfSecond);
        TimeScaleInstant ts = TimeScales.utc().toTAI(t);
        assertEquals(ts.getEpochSeconds(), taiEpochSeconds);
        assertEquals(ts.getNanoOfSecond(), taiNanoOfSecond);
    }

    private void cvtFromTAI(long taiEpochSeconds, int taiNanoOfSecond, long expectedEpochSeconds, int expectedNanoOfSecond) {
        TimeScaleInstant ts = TimeScaleInstant.seconds(TimeScales.tai(), taiEpochSeconds, taiNanoOfSecond);
        TimeScaleInstant t = TimeScales.utc().toTimeScaleInstant(ts);
        assertEquals(t.getEpochSeconds(), expectedEpochSeconds);
        assertEquals(t.getLeapSecond(), 0);
        assertEquals(t.getNanoOfSecond(), expectedNanoOfSecond);
    }

    @Test public void testUtcValidity() {
        checkValidity(date(2008,12,31)+time(23,59,59), millis(500), TimeScaleInstant.Validity.ambiguous);
        checkValidity(date(2008,12,31)+time(23,59,59), 0, TimeScaleInstant.Validity.ambiguous);
        checkValidity(date(2008,12,31)+time(23,59,58), 0, TimeScaleInstant.Validity.valid);
        checkValidity(date(2009,1,1), 0, TimeScaleInstant.Validity.valid);
        
        // check an invalid interval
        checkValidity(date(1968,1,31)+time(23,59,59), millis(890), TimeScaleInstant.Validity.valid);
        checkValidity(date(1968,1,31)+time(23,59,59), millis(910), TimeScaleInstant.Validity.invalid);
        checkValidity(date(1968,2,1), 0, TimeScaleInstant.Validity.valid);

        // check an ambiguous interval
        checkValidity(date(1965, 8, 31)+time(23,59,59), millis(890), TimeScaleInstant.Validity.valid);
        checkValidity(date(1965, 8, 31)+time(23,59,59), millis(910), TimeScaleInstant.Validity.ambiguous);
        checkValidity(date(1965, 9, 1), 0, TimeScaleInstant.Validity.valid);
    }

    private void checkValidity(long epochSeconds, int nanoOfSecond, TimeScaleInstant.Validity validity) {
        TimeScaleInstant t = TimeScaleInstant.seconds(TimeScales.utc(), epochSeconds, nanoOfSecond);
        assertEquals(t.getValidity(), validity);
    }

    @Test public void testCalculation() {
        assertEquals(instant(date(2008, 12, 31)+time(23,59,59)).plus(Duration.seconds(1)),
           instant(date(2009, 1, 1)));
        assertEquals(instant(date(2009, 1, 1)).minus(Duration.seconds(1)), instant(date(2008, 12, 31)+time(23,59,59)));
        assertEquals(TimeScales.utc().durationBetween(
               instant(date(2008, 12, 31)+time(23,59,59)),
               instant(date(2009, 1, 1))),
           Duration.seconds(1));
        assertEquals(TimeScales.utc().durationBetween(
               instant(date(2008, 12, 31)+time(23,59)),
               instant(date(2009, 1, 1)+60)),
           Duration.seconds(120));
        assertEquals(TimeScales.utc().durationBetween(
               instant(date(2008, 12, 31)+time(23,59)),
               instant(date(2008, 12, 31)+time(23,59,59),0)),
           Duration.seconds(59));

        // check behaviour around gap in time scale
        assertTrue(instant(date(1968,2,1)).minus(Duration.millis(10)).getValidity() == TimeScaleInstant.Validity.valid);
        assertEquals(instant(date(1968,1,31)+time(23,59,59)).plus(Duration.millis(990)), instant(date(1968,2,1)));

        TimeScaleInstant t0 = instant(date(1968,1,1));
        TimeScaleInstant t1= instant(date(1969,1,1));
        Duration dUTC = TimeScales.utc().durationBetween(t0, t1);
        Duration dTAI = TimeScales.tai().durationBetween(t0, t1);
        System.out.println("Duration between "+t0+" and "+t1+" is "+dUTC+"[utc], "+dTAI+"[TAI]");
        System.out.println("Difference is "+dTAI.minus(dUTC));
    }

    private static TimeScaleInstant instant(long epochSeconds) {
        return TimeScaleInstant.seconds(TimeScales.utc(), epochSeconds);
    }

    private static TimeScaleInstant instant(long epochSeconds, int nanoOfSecond) {
        return TimeScaleInstant.seconds(TimeScales.utc(), epochSeconds, nanoOfSecond);
    }
}
