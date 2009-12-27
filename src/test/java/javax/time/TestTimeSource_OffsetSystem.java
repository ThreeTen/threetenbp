/*
 * Copyright (c) 2008-2009 Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;

/**
 * Test TimeSource.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestTimeSource_OffsetSystem {

    private static final Duration OFFSET = Duration.seconds(2);

    //-----------------------------------------------------------------------
    public void test_offset_isSerializable() throws IOException, ClassNotFoundException {
        TimeSource offset = TimeSource.offsetSystem(OFFSET);
        assertTrue(offset instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(offset);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), offset);
    }

    //-----------------------------------------------------------------------
    public void test_offset_zero() {
        TimeSource test = TimeSource.offsetSystem(Duration.ZERO);
        assertEquals(test, TimeSource.system());
    }

    //-----------------------------------------------------------------------
    public void test_offset_instant() {
        TimeSource offset = TimeSource.offsetSystem(OFFSET);
        for (int i = 0; i < 10000; i++) {
            // assume can eventually get these within 10 milliseconds
            Instant instant = offset.instant();
            long systemMillis = System.currentTimeMillis();
            if (systemMillis - (instant.toEpochMillis() + 2000) < 10) {
                return;  // success
            }
        }
        fail();
    }

    //-----------------------------------------------------------------------
    public void test_offset_equals() {
        TimeSource offset = TimeSource.offsetSystem(OFFSET);
        assertTrue(offset.equals(offset));
        assertTrue(offset.equals(TimeSource.offsetSystem(OFFSET)));
        
        assertFalse(offset.equals(TimeSource.offsetSystem(OFFSET.minusNanos(1))));
        assertFalse(offset.equals(null));
        assertFalse(offset.equals(new Object()));
        assertFalse(offset.equals(new TimeSource() {
            @Override
            public Instant instant() {
                return null;
            }
        }));
    }

    public void test_offset_hashCode() {
        TimeSource offset = TimeSource.offsetSystem(OFFSET);
        assertEquals(offset.hashCode(), offset.hashCode());
        assertEquals(offset.hashCode(), TimeSource.offsetSystem(OFFSET).hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_offset_toString() {
        TimeSource offset = TimeSource.offsetSystem(OFFSET);
        assertEquals(offset.toString(), "OffsetSystemTimeSource[PT2S]");
    }

}
