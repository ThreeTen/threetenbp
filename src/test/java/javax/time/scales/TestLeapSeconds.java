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

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * @author Mark Thornton
 */
public class TestLeapSeconds {
    /** check instant before and after a leap second is inserted.
     */
    private void checkDeltaSeconds(int year, int month, int day, int expectedDelta) {
        long epochSeconds = ScaleUtil.epochSeconds(year, month, day);
        LeapSeconds.Entry entry = LeapSeconds.list().entryFromUTC(epochSeconds);
        assertEquals(entry.getDeltaSeconds(), expectedDelta);
        if (epochSeconds > ScaleUtil.START_LEAP_SECONDS) {
            assertEquals(LeapSeconds.list().entryFromUTC(epochSeconds-1).getDeltaSeconds(), expectedDelta-1);
        }
    }

    @Test public void testLeapList() {
        // check a sample of dates
        checkDeltaSeconds(1972, 1, 1, 10);  // beginning of leap second era
        checkDeltaSeconds(1981, 7, 1, 20);
        checkDeltaSeconds(2009, 1, 1, 34);  // most recent leap second at time of writing
        // presence/absence of a leap second is announced about 6 months in advance
        // at the time an absence is announced, the next feasible leap is a bit under a year away
        // So the next possible leap is at most a year from the current date. A tighter bound
        // is possible
        assertTrue(LeapSeconds.getNextPossibleLeap() < System.currentTimeMillis()/1000+(365*86400));
        // If leap second tables are up to date, then have a lower bound
        // the next unannounced point is at least 5 months away
        assertTrue(LeapSeconds.getNextPossibleLeap() > System.currentTimeMillis()/1000+(150*86400),
           "Leap second table is out of date: see most recent Bulletin C");
    }
}
