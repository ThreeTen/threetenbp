/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamConstants;
import java.lang.reflect.Field;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test that the classes serialized via a Ser delegate reject direct deserialization.
 */
@Test
public class TestSerializationDelegate extends AbstractTest {

    @DataProvider(name="DelegatedClasses")
    Object[][] provider_delegatedClasses() {
        return new Object[][] {
            {"org.threeten.bp.chrono.ChronoLocalDateTimeImpl"},
            {"org.threeten.bp.chrono.MinguoDate"},
            {"org.threeten.bp.chrono.ThaiBuddhistDate"},
            {"org.threeten.bp.zone.StandardZoneRules"},
            {"org.threeten.bp.zone.ZoneOffsetTransition"},
            {"org.threeten.bp.zone.ZoneOffsetTransitionRule"},
        };
    }

    @Test(dataProvider="DelegatedClasses", expectedExceptions=InvalidObjectException.class)
    public void test_directDeserialization(String className) throws Exception {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(directStream(className)));
        try {
            in.readObject();
        } finally {
            in.close();
        }
    }

    /**
     * Builds a stream naming the class itself rather than its Ser delegate.
     * The class descriptor declares no fields, so each field is left at its default.
     */
    private static byte[] directStream(String className) throws Exception {
        Field field = Class.forName(className).getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        out.writeShort(ObjectStreamConstants.STREAM_MAGIC);
        out.writeShort(ObjectStreamConstants.STREAM_VERSION);
        out.writeByte(ObjectStreamConstants.TC_OBJECT);
        out.writeByte(ObjectStreamConstants.TC_CLASSDESC);
        out.writeUTF(className);
        out.writeLong(((Long) field.get(null)).longValue());
        out.writeByte(ObjectStreamConstants.SC_SERIALIZABLE);
        out.writeShort(0);  // number of fields
        out.writeByte(ObjectStreamConstants.TC_ENDBLOCKDATA);  // end of classdesc
        out.writeByte(ObjectStreamConstants.TC_NULL);  // no superclasses
        out.close();
        return baos.toByteArray();
    }

}
