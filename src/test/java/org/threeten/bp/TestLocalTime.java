/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;
import org.threeten.bp.LocalTime;

/**
 * Test LocalTime.
 */
@Test
public class TestLocalTime {

    //-----------------------------------------------------------------------
    private void check(LocalTime time, int h, int m, int s, int n) {
        assertEquals(time.getHour(), h);
        assertEquals(time.getMinute(), m);
        assertEquals(time.getSecond(), s);
        assertEquals(time.getNano(), n);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck","implementation"})
    public void constant_MIDNIGHT() {
        check(LocalTime.MIDNIGHT, 0, 0, 0, 0);
    }

    @Test(groups={"implementation"})
    public void constant_MIDNIGHT_same() {
        assertSame(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);
        assertSame(LocalTime.MIDNIGHT, LocalTime.of(0, 0));
    }

    @Test(groups={"tck","implementation"})
    public void constant_MIDDAY() {
        check(LocalTime.NOON, 12, 0, 0, 0);
    }

    @Test(groups={"implementation"})
    public void constant_MIDDAY_same() {
        assertSame(LocalTime.NOON, LocalTime.NOON);
        assertSame(LocalTime.NOON, LocalTime.of(12, 0));
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck","implementation"})
    public void constant_MIN_TIME() {
        check(LocalTime.MIN_TIME, 0, 0, 0, 0);
    }

    @Test(groups={"implementation"})
    public void constant_MIN_TIME_same() {
        assertSame(LocalTime.MIN_TIME, LocalTime.of(0, 0));
    }

    @Test(groups={"tck","implementation"})
    public void constant_MAX_TIME() {
        check(LocalTime.MAX_TIME, 23, 59, 59, 999999999);
    }

    @Test(groups={"implementation"})
    public void constant_MAX_TIME_same() {
        assertSame(LocalTime.NOON, LocalTime.NOON);
        assertSame(LocalTime.NOON, LocalTime.of(12, 0));
    }

    @Test(groups={"implementation"})
    public void factory_time_2ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_time_3ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0, 0);
            LocalTime test2 = LocalTime.of(i, 0, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_time_4ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0, 0, 0);
            LocalTime test2 = LocalTime.of(i, 0, 0, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_ofSecondOfDay_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofSecondOfDay(i * 60L * 60L);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_ofSecondOfDay7_long_int_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofSecondOfDay(i * 60L * 60L, 0);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(groups={"implementation"})
    public void factory_ofNanoOfDay_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofNanoOfDay(i * 1000000000L * 60L * 60L);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

}
