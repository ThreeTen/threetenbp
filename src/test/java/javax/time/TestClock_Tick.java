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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

/**
 * Test offset clock.
 */
@Test
public class TestClock_Tick {

    private static final ZoneId MOSCOW = ZoneId.of("Europe/Moscow");
    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final OffsetDateTime ODT = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, ZoneOffset.ofHours(2));

    //-----------------------------------------------------------------------
    public void test_tick_isSerializable() throws IOException, ClassNotFoundException {
        Clock test = Clock.tickSeconds(PARIS);
        assertEquals(test instanceof Serializable, true);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    //-----------------------------------------------------------------------
    public void test_tick_clockDuration_zero() {
        Clock test = Clock.tick(Clock.systemUTC(), Duration.ZERO);
        assertEquals(test, Clock.systemUTC());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_tickSeconds_nullZoneId() {
        Clock.tickSeconds(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_tickMinutes_nullZoneId() {
        Clock.tickMinutes(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_tick_clockDuration_nullClock() {
        Clock.tick(null, Duration.ZERO);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_tick_clockDuration_nullDuration() {
        Clock.tick(Clock.systemUTC(), null);
    }

    //-----------------------------------------------------------------------
    public void test_tickSeconds() {
        Clock test = Clock.tickSeconds(PARIS);
        assertEquals(test.getZone(), PARIS);
        for (int i = 0; i < 10000; i++) {
            // assume can eventually get these within 10 milliseconds
            long instant = test.millis();
            assertEquals(instant % 1000, 0);
            long systemMillis = System.currentTimeMillis();
            if ((systemMillis / 1000) * 1000 == instant) {
                return;  // success
            }
        }
        fail();
    }

    public void test_tickMinutes() {
        Clock test = Clock.tickMinutes(PARIS);
        assertEquals(test.getZone(), PARIS);
        for (int i = 0; i < 10000; i++) {
            // assume can eventually get these within 10 milliseconds
            long instant = test.millis();
            assertEquals(instant % 60000, 0);
            long systemMillis = System.currentTimeMillis();
            if ((systemMillis / 60000) * 60000 == instant) {
                return;  // success
            }
        }
        fail();
    }

    public void test_tick_clockDuration() {
        for (int i = 0; i < 1000; i++) {
            Clock test = Clock.tick(Clock.fixed(ODT.withNanoOfSecond(i * 1000000).toInstant(), PARIS), Duration.ofMillis(250));
            assertEquals(test.instant(), ODT.withNanoOfSecond((i / 250) * 250 * 1000000).toInstant());
            assertEquals(test.getZone(), PARIS);
        }
    }

    //-------------------------------------------------------------------------
    public void test_withZone() {
        Clock test = Clock.tick(Clock.system(PARIS), Duration.ofMillis(500));
        Clock changed = test.withZone(MOSCOW);
        assertEquals(test.getZone(), PARIS);
        assertEquals(changed.getZone(), MOSCOW);
    }

    public void test_withZone_same() {
        Clock test = Clock.tick(Clock.system(PARIS), Duration.ofMillis(500));
        Clock changed = test.withZone(ZoneId.of("Europe/Paris"));
        assertSame(test, changed);
    }

    //-----------------------------------------------------------------------
    public void test_tick_equals() {
        Clock a = Clock.tick(Clock.system(PARIS), Duration.ofMillis(500));
        Clock b = Clock.tick(Clock.system(PARIS), Duration.ofMillis(500));
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(b.equals(b), true);
        
        Clock c = Clock.tick(Clock.system(MOSCOW), Duration.ofMillis(500));
        assertEquals(a.equals(c), false);
        
        Clock d = Clock.tick(Clock.system(PARIS), Duration.ofMillis(499));
        assertEquals(a.equals(d), false);
        
        assertEquals(a.equals(null), false);
        assertEquals(a.equals("other type"), false);
        assertEquals(a.equals(Clock.systemUTC()), false);
    }

    public void test_tick_hashCode() {
        Clock a = Clock.tick(Clock.system(PARIS), Duration.ofMillis(500));
        Clock b = Clock.tick(Clock.system(PARIS), Duration.ofMillis(500));
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        
        Clock c = Clock.tick(Clock.system(MOSCOW), Duration.ofMillis(500));
        assertEquals(a.hashCode() == c.hashCode(), false);
        
        Clock d = Clock.tick(Clock.system(PARIS), Duration.ofMillis(499));
        assertEquals(a.hashCode() == d.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    public void test_tick_toString() {
        Clock offset = Clock.tick(Clock.systemUTC(), Duration.ofMillis(500));
        assertEquals(offset.toString(), "TickClock[SystemClock[UTC],PT0.5S]");
    }

}
