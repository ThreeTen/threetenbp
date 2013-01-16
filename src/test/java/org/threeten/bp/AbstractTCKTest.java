/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamConstants;
import java.lang.reflect.Field;

/**
 * Base test class.
 */
public abstract class AbstractTCKTest extends AbstractTest {

    protected static void assertSerializedBySer(Object object, byte[] expectedBytes, byte[]... matches) throws Exception {
        String serClass = object.getClass().getPackage().getName() + ".Ser";
        Class<?> serCls = Class.forName(serClass);
        Field field = serCls.getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        long serVer = (Long) field.get(null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos) ) {
            oos.writeObject(object);
        }
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (DataInputStream dis = new DataInputStream(bais)) {
            assertEquals(dis.readShort(), ObjectStreamConstants.STREAM_MAGIC);
            assertEquals(dis.readShort(), ObjectStreamConstants.STREAM_VERSION);
            assertEquals(dis.readByte(), ObjectStreamConstants.TC_OBJECT);
            assertEquals(dis.readByte(), ObjectStreamConstants.TC_CLASSDESC);
            assertEquals(dis.readUTF(), serClass);
            assertEquals(dis.readLong(), serVer);
            assertEquals(dis.readByte(), ObjectStreamConstants.SC_EXTERNALIZABLE | ObjectStreamConstants.SC_BLOCK_DATA);
            assertEquals(dis.readShort(), 0);  // number of fields
            assertEquals(dis.readByte(), ObjectStreamConstants.TC_ENDBLOCKDATA);  // end of classdesc
            assertEquals(dis.readByte(), ObjectStreamConstants.TC_NULL);  // no superclasses
            if (expectedBytes.length < 256) {
                assertEquals(dis.readByte(), ObjectStreamConstants.TC_BLOCKDATA);
                assertEquals(dis.readUnsignedByte(), expectedBytes.length);
            } else {
                assertEquals(dis.readByte(), ObjectStreamConstants.TC_BLOCKDATALONG);
                assertEquals(dis.readInt(), expectedBytes.length);
            }
            byte[] input = new byte[expectedBytes.length];
            dis.readFully(input);
            assertEquals(input, expectedBytes);
            if (matches.length > 0) {
                for (byte[] match : matches) {
                    boolean matched = false;
                    while (matched == false) {
                        try {
                            dis.mark(1000);
                            byte[] possible = new byte[match.length];
                            dis.readFully(possible);
                            assertEquals(possible, match);
                            matched = true;
                        } catch (AssertionError ex) {
                            dis.reset();
                            dis.readByte();  // ignore
                        }
                    }
                }
            } else {
                assertEquals(dis.readByte(), ObjectStreamConstants.TC_ENDBLOCKDATA);  // end of blockdata
                assertEquals(dis.read(), -1);
            }
        }
    }

}
