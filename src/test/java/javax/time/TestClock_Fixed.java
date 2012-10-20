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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

/**
 * Test fixed clock.
 */
@Test
public class TestClock_Fixed {

    private static final ZoneId MOSCOW = ZoneId.of("Europe/Moscow");
    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final Instant INSTANT = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, ZoneOffset.ofHours(2)).toInstant();

    //-----------------------------------------------------------------------
    public void test_fixed_isSerializable() throws IOException, ClassNotFoundException {
        Clock fixed = Clock.fixed(INSTANT, ZoneId.UTC);
        assertEquals(fixed instanceof Serializable, true);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(fixed);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), fixed);
    }

    //-------------------------------------------------------------------------
    public void test_fixedUTC() {
        Clock fixed = Clock.fixedUTC(INSTANT);
        assertEquals(fixed.instant(), INSTANT);
        assertEquals(fixed.getZone(), ZoneId.UTC);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_fixedUTC_nullInstant() {
        Clock.fixedUTC(null);
    }

    public void test_fixed_zoneId() {
        Clock fixed = Clock.fixed(INSTANT, PARIS);
        assertEquals(fixed.instant(), INSTANT);
        assertEquals(fixed.getZone(), PARIS);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_fixed_zoneId_nullInstant() {
        Clock.fixed(null, PARIS);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_fixed_zoneId_nullZoneId() {
        Clock.fixed(INSTANT, null);
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
    public void test_fixed_equals() {
        Clock a = Clock.fixed(INSTANT, ZoneId.UTC);
        Clock b = Clock.fixed(INSTANT, ZoneId.UTC);
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(b.equals(b), true);
        
        Clock c = Clock.fixed(INSTANT, PARIS);
        assertEquals(a.equals(c), false);
        
        Clock d = Clock.fixed(INSTANT.minusNanos(1), ZoneId.UTC);
        assertEquals(a.equals(d), false);
        
        assertEquals(a.equals(null), false);
        assertEquals(a.equals("other type"), false);
        assertEquals(a.equals(Clock.systemUTC()), false);
    }

    public void test_fixed_hashCode() {
        Clock a = Clock.fixed(INSTANT, ZoneId.UTC);
        Clock b = Clock.fixed(INSTANT, ZoneId.UTC);
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        
        Clock c = Clock.fixed(INSTANT, PARIS);
        assertEquals(a.hashCode() == c.hashCode(), false);
        
        Clock d = Clock.fixed(INSTANT.minusNanos(1), ZoneId.UTC);
        assertEquals(a.hashCode() == d.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    public void test_fixed_toString() {
        Clock fixed = Clock.fixed(INSTANT, PARIS);
        assertEquals(fixed.toString(), "FixedClock[2008-06-30T09:30:10.000000500Z,Europe/Paris]");
    }

}
