/*
 * Copyright (c) 2010-2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.zone;

import static org.testng.Assert.assertEquals;

import javax.time.Instant;
import javax.time.LocalDateTime;
import javax.time.Period;
import javax.time.ZoneId;
import javax.time.ZoneOffset;

import org.testng.annotations.Test;

/**
 * Test ZoneRules.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestZoneRules {

    private static final ZoneOffset OFFSET_1_15 = ZoneOffset.ofHoursMinutes(1, 15);
    private static final Period PERIOD_0 = Period.ZERO;
    private static final LocalDateTime DATE_TIME_2008_01_01 = LocalDateTime.of(2008, 1, 1, 0, 0);

    //-----------------------------------------------------------------------
    // ofFixed()
    //-----------------------------------------------------------------------
    public void test_ofFixed_ZoneOffset() {
        ZoneRules test = ZoneRules.ofFixed(OFFSET_1_15);
        assertEquals(test.isFixedOffset(), true);
        assertEquals(test.getOffset(Instant.EPOCH), OFFSET_1_15);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_01_01.atZone(ZoneId.UTC).toInstant()), new ZoneOffsetInfo(DATE_TIME_2008_01_01.plusHours(1).plusMinutes(15), OFFSET_1_15, null));
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_01_01), new ZoneOffsetInfo(DATE_TIME_2008_01_01, OFFSET_1_15, null));
        assertEquals(test.getStandardOffset(Instant.EPOCH), OFFSET_1_15);
        assertEquals(test.getTransitionRules().size(), 0);
        assertEquals(test.getTransitions().size(), 0);
        assertEquals(test.isDaylightSavings(Instant.EPOCH), false);
        assertEquals(test.getDaylightSavings(Instant.EPOCH), PERIOD_0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toRules_nullID() {
        ZoneRules.ofFixed(null);
    }

}
