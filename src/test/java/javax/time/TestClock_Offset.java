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

import javax.time.zone.ZoneId;

import org.testng.annotations.Test;

/**
 * Test offset clock.
 */
@Test
public class TestClock_Offset {

    private static final ZoneId MOSCOW = ZoneId.of("Europe/Moscow");
    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final Duration OFFSET = Duration.ofSeconds(2);

    //-----------------------------------------------------------------------
    public void test_offset_isSerializable() throws IOException, ClassNotFoundException {
        Clock offset = Clock.offset(Clock.system(PARIS), OFFSET);
        assertEquals(offset instanceof Serializable, true);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(offset);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), offset);
    }

    //-----------------------------------------------------------------------
    public void test_offset_zero() {
        Clock test = Clock.offset(Clock.systemUTC(), Duration.ZERO);
        assertEquals(test, Clock.systemUTC());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_offset_nullClock() {
        Clock.offset(null, Duration.ZERO);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_offset_nullDuration() {
        Clock.offset(Clock.systemUTC(), null);
    }

    //-----------------------------------------------------------------------
    public void test_offset_utc() {
        Clock offset = Clock.offset(Clock.systemUTC(), OFFSET);
        assertEquals(offset.getZone(), ZoneId.UTC);
        for (int i = 0; i < 10000; i++) {
            // assume can eventually get these within 10 milliseconds
            Instant instant = offset.instant();
            long systemMillis = System.currentTimeMillis();
            if (systemMillis - instant.toEpochMilli() + 2000 < 10) {
                return;  // success
            }
        }
        fail();
    }

    public void test_offset_paris() {
        Clock offset = Clock.offset(Clock.system(PARIS), OFFSET);
        assertEquals(offset.getZone(), PARIS);
        for (int i = 0; i < 10000; i++) {
            // assume can eventually get these within 10 milliseconds
            Instant instant = offset.instant();
            long systemMillis = System.currentTimeMillis();
            if (systemMillis - instant.toEpochMilli() + 2000 < 10) {
                return;  // success
            }
        }
        fail();
    }

    //-------------------------------------------------------------------------
    public void test_withZone() {
        Clock test = Clock.offset(Clock.system(PARIS), OFFSET);
        Clock changed = test.withZone(MOSCOW);
        assertEquals(test.getZone(), PARIS);
        assertEquals(changed.getZone(), MOSCOW);
    }

    public void test_withZone_same() {
        Clock test = Clock.offset(Clock.system(PARIS), OFFSET);
        Clock changed = test.withZone(ZoneId.of("Europe/Paris"));
        assertSame(test, changed);
    }

    //-----------------------------------------------------------------------
    public void test_offset_equals() {
        Clock a = Clock.offset(Clock.system(PARIS), OFFSET);
        Clock b = Clock.offset(Clock.system(PARIS), OFFSET);
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(b.equals(b), true);
        
        Clock c = Clock.offset(Clock.system(MOSCOW), OFFSET);
        assertEquals(a.equals(c), false);
        
        Clock d = Clock.offset(Clock.system(PARIS), OFFSET.minusNanos(1));
        assertEquals(a.equals(d), false);
        
        assertEquals(a.equals(null), false);
        assertEquals(a.equals("other type"), false);
        assertEquals(a.equals(Clock.systemUTC()), false);
    }

    public void test_offset_hashCode() {
        Clock a = Clock.offset(Clock.system(PARIS), OFFSET);
        Clock b = Clock.offset(Clock.system(PARIS), OFFSET);
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        
        Clock c = Clock.offset(Clock.system(MOSCOW), OFFSET);
        assertEquals(a.hashCode() == c.hashCode(), false);
        
        Clock d = Clock.offset(Clock.system(PARIS), OFFSET.minusNanos(1));
        assertEquals(a.hashCode() == d.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    public void test_offset_toString() {
        Clock offset = Clock.offset(Clock.systemUTC(), OFFSET);
        assertEquals(offset.toString(), "OffsetClock[SystemClock[UTC],PT2S]");
    }

}
