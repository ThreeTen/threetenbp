/*
 * Copyright (c) 2008-2010 Stephen Colebourne & Michael Nascimento Santos
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
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;

import org.testng.annotations.Test;

/**
 * Test TimeSource.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestTimeSource_Fixed {

    private static final Instant INSTANT = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, ZoneOffset.ofHours(2)).toInstant();

    //-----------------------------------------------------------------------
    public void test_fixed_isSerializable() throws IOException, ClassNotFoundException {
        TimeSource fixed = TimeSource.fixed(INSTANT);
        assertTrue(fixed instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(fixed);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), fixed);
    }

    //-----------------------------------------------------------------------
    public void test_fixed_instant() {
        TimeSource fixed = TimeSource.fixed(INSTANT);
        assertEquals(fixed.instant(), INSTANT);
    }

    //-----------------------------------------------------------------------
    public void test_fixed_utcInstant() {
        TimeSource fixed = TimeSource.fixed(INSTANT);
        assertEquals(fixed.utcInstant(), UTCInstant.of(INSTANT));
    }

    //-----------------------------------------------------------------------
    public void test_fixed_taiInstant() {
        TimeSource fixed = TimeSource.fixed(INSTANT);
        assertEquals(fixed.taiInstant(), TAIInstant.of(INSTANT));
    }

    //-----------------------------------------------------------------------
    public void test_fixed_millis() {
        TimeSource fixed = TimeSource.fixed(INSTANT);
        assertEquals(fixed.millis(), INSTANT.toEpochMilli());
    }

    //-----------------------------------------------------------------------
    public void test_fixed_equals() {
        TimeSource fixed = TimeSource.fixed(INSTANT);
        assertTrue(fixed.equals(fixed));
        assertTrue(fixed.equals(TimeSource.fixed(INSTANT)));
        
        assertFalse(fixed.equals(TimeSource.fixed(INSTANT.minusNanos(1))));
        assertFalse(fixed.equals(null));
        assertFalse(fixed.equals(new Object()));
        assertFalse(fixed.equals(new TimeSource() {
            @Override
            public Instant instant() {
                return null;
            }
        }));
    }

    public void test_fixed_hashCode() {
        TimeSource fixed = TimeSource.fixed(INSTANT);
        assertEquals(fixed.hashCode(), fixed.hashCode());
        assertEquals(fixed.hashCode(), TimeSource.fixed(INSTANT).hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_fixed_toString() {
        TimeSource fixed = TimeSource.fixed(INSTANT);
        assertEquals(fixed.toString(), "FixedTimeSource[2008-06-30T09:30:10.000000500Z]");
    }

}
