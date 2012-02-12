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
 * Test system clock.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestClock_System {

    private static final ZoneId MOSCOW = ZoneId.of("Europe/Moscow");
    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");

    //-----------------------------------------------------------------------
    public void test_system_isSerializable() throws IOException, ClassNotFoundException {
        Clock system = Clock.system(PARIS);
        assertEquals(system instanceof Serializable, true);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(system);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), system);
    }

    //-----------------------------------------------------------------------
    public void test_system_instant() {
        Clock system = Clock.systemUTC();
        assertEquals(system.getZone(), ZoneId.UTC);
        for (int i = 0; i < 10000; i++) {
            // assume can eventually get these within 10 milliseconds
            Instant instant = system.instant();
            long systemMillis = System.currentTimeMillis();
            if (systemMillis - instant.toEpochMilli() < 10) {
                return;  // success
            }
        }
        fail();
    }

    public void test_system_millis() {
        Clock system = Clock.systemUTC();
        assertEquals(system.getZone(), ZoneId.UTC);
        for (int i = 0; i < 10000; i++) {
            // assume can eventually get these within 10 milliseconds
            long instant = system.millis();
            long systemMillis = System.currentTimeMillis();
            if (systemMillis - instant < 10) {
                return;  // success
            }
        }
        fail();
    }

    //-------------------------------------------------------------------------
    public void test_systemUTC() {
        Clock system = Clock.system(PARIS);
        assertEquals(system.getZone(), PARIS);
    }

    public void test_system_zoneId() {
        Clock system = Clock.system(PARIS);
        assertEquals(system.getZone(), PARIS);
    }

    public void test_systemDefaultZone() {
        Clock system = Clock.systemDefaultZone();
        assertEquals(system.getZone(), ZoneId.systemDefault());
    }

    //-------------------------------------------------------------------------
    public void test_withZone() {
        Clock test = Clock.system(PARIS);
        Clock changed = test.withZone(MOSCOW);
        assertEquals(test.getZone(), PARIS);
        assertEquals(changed.getZone(), MOSCOW);
    }

    public void test_withZone_same() {
        Clock test = Clock.system(PARIS);
        Clock changed = test.withZone(ZoneId.of("Europe/Paris"));
        assertSame(test, changed);
    }

    //-----------------------------------------------------------------------
    public void test_system_equals() {
        Clock a = Clock.systemUTC();
        Clock b = Clock.systemUTC();
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(b.equals(b), true);
        
        Clock c = Clock.system(PARIS);
        assertEquals(a.equals(c), false);
        
        assertEquals(a.equals(null), false);
        assertEquals(a.equals("other type"), false);
        assertEquals(a.equals(Clock.fixedUTC(Instant.now())), false);
    }

    public void test_system_hashCode() {
        Clock a = Clock.system(ZoneId.UTC);
        Clock b = Clock.system(ZoneId.UTC);
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        
        Clock c = Clock.system(PARIS);
        assertEquals(a.hashCode() == c.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    public void test_system_toString() {
        Clock system = Clock.system(PARIS);
        assertEquals(system.toString(), "SystemClock[Europe/Paris]");
    }

}
