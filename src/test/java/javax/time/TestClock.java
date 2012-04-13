/*
 * Copyright (c) 2008-2012 Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * Test Clock.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestClock {

    static class MockInstantClock extends Clock {
        final long millis;
        final ZoneId zone;
        MockInstantClock(long millis, ZoneId zone) {
            this.millis = millis;
            this.zone = zone;
        }
        @Override
        public long millis() {
            return millis;
        }
        @Override
        public ZoneId getZone() {
            return zone;
        }
        @Override
        public Clock withZone(ZoneId timeZone) {
            return new MockInstantClock(millis, timeZone);
        }
    }

    private static final ZoneOffset OFFSET = ZoneOffset.ofHours(2);
    private static final OffsetDateTime DATE_TIME = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500000000, OFFSET);
    private static final ZoneId ZONE = ZoneId.of("Europe/Paris");
    private static final Clock MOCK_INSTANT = new MockInstantClock(DATE_TIME.toInstant().toEpochMilli(), ZONE);

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_mockInstantClock_get() {
        assertEquals(MOCK_INSTANT.instant(), DATE_TIME.toInstant());
        assertEquals(MOCK_INSTANT.millis(), DATE_TIME.toInstant().toEpochMilli());
        assertEquals(MOCK_INSTANT.getZone(), ZONE);
    }

    @Test(groups={"tck"})
    public void test_mockInstantClock_withZone() {
        ZoneId london = ZoneId.of("Europe/London");
        Clock changed = MOCK_INSTANT.withZone(london);
        assertEquals(MOCK_INSTANT.instant(), DATE_TIME.toInstant());
        assertEquals(MOCK_INSTANT.millis(), DATE_TIME.toInstant().toEpochMilli());
        assertEquals(changed.getZone(), london);
    }

}
