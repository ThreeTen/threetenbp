/*
 * Copyright (c) 2008-2011 Stephen Colebourne & Michael Nascimento Santos
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

/**
 * Test TimeSourceClock.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestClock_TimeSourceClock {

    private static final ZoneOffset OFFSET = ZoneOffset.ofHours(2);
    private static final OffsetDateTime DATE_TIME = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET);
    private static final TimeSource TIME_SOURCE = TimeSource.fixed(DATE_TIME);
    private static final ZoneId ZONE = ZoneId.of("Europe/Paris");

    //-----------------------------------------------------------------------
    public void test_isSerializable() throws IOException, ClassNotFoundException {
        Clock test = Clock.clock(TIME_SOURCE, ZONE);
        assertTrue(test instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), test);
    }

    //-----------------------------------------------------------------------
    public void test_get() {
        Clock test = Clock.clock(TIME_SOURCE, ZONE);
        assertEquals(test.getSource(), TIME_SOURCE);
        assertEquals(test.getZone(), ZONE);
    }

    public void test_withSource() {
        Clock test = Clock.clock(TIME_SOURCE, ZONE);
        Clock changed = test.withSource(TimeSource.system());
        assertEquals(test.getSource(), TIME_SOURCE);
        assertEquals(test.getZone(), ZONE);
        assertEquals(changed.getSource(), TimeSource.system());
        assertEquals(changed.getZone(), ZONE);
    }

    public void test_withSource_same() {
        Clock test = Clock.clock(TIME_SOURCE, ZONE);
        Clock changed = test.withSource(TIME_SOURCE);
        assertSame(test, changed);
    }

    public void test_withZone() {
        Clock test = Clock.clock(TIME_SOURCE, ZONE);
        ZoneId london = ZoneId.of("Europe/London");
        Clock changed = test.withZone(london);
        assertEquals(test.getSource(), TIME_SOURCE);
        assertEquals(test.getZone(), ZONE);
        assertEquals(changed.getSource(), TIME_SOURCE);
        assertEquals(changed.getZone(), london);
    }

    public void test_withZone_same() {
        Clock test = Clock.clock(TIME_SOURCE, ZONE);
        Clock changed = test.withZone(ZONE);
        assertSame(test, changed);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Clock test = Clock.clock(TIME_SOURCE, ZONE);
        assertTrue(test.equals(test));
        assertTrue(test.equals(Clock.clock(TIME_SOURCE, ZONE)));
        
        assertFalse(test.equals(null));
        assertFalse(test.equals(new Object()));
        assertFalse(test.equals(new Clock() {
            @Override
            public TimeSource getSource() {
                return TIME_SOURCE;
            }
            @Override
            public ZoneId getZone() {
                return ZONE;
            }
        }));
        assertFalse(test.equals(Clock.system(ZoneId.of(ZoneOffset.ofHours(1)))));
    }

    public void test_hashCode() {
        Clock test = Clock.clock(TIME_SOURCE, ZONE);
        assertEquals(test.hashCode(), test.hashCode());
        assertEquals(test.hashCode(), Clock.clock(TIME_SOURCE, ZONE).hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Clock test = Clock.clock(TIME_SOURCE, ZONE);
        assertEquals(test.toString(), "TimeSourceClock[FixedTimeSource[2008-06-30T09:30:10.000000500Z], Europe/Paris]");
    }

}
