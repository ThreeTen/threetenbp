/*
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Base test class.
 */
public abstract class AbstractTCKTest {

    private static final String SERIALISATION_DATA_FOLDER = "src/test/resources/";

    protected static boolean isIsoLeap(long year) {
        if (year % 4 != 0) {
            return false;
        }
        if (year % 100 == 0 && year % 400 != 0) {
            return false;
        }
        return true;
    }

    protected static void assertSerializable(Object o) throws IOException, ClassNotFoundException {
        Object deserialisedObject = writeThenRead(o);
        assertEquals(deserialisedObject, o);
    }

    protected static void assertSerializableAndSame(Object o) throws IOException, ClassNotFoundException {
        Object deserialisedObject = writeThenRead(o);
        assertSame(deserialisedObject, o);
    }

    protected static Object writeThenRead(Object o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos) ) {
            oos.writeObject(o);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            return ois.readObject();
        }
    }

    protected static void assertEqualsSerialisedForm(Object objectSerialised) throws IOException, ClassNotFoundException {
        assertEqualsSerialisedForm(objectSerialised, objectSerialised.getClass());
    }

    protected static void assertEqualsSerialisedForm(Object objectSerialised, Class<?> cls) throws IOException, ClassNotFoundException {
        String className = cls.getSimpleName();
//        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SERIALISATION_DATA_FOLDER + className + ".bin"))) {
//            out.writeObject(objectSerialised);
//        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SERIALISATION_DATA_FOLDER + className + ".bin"))) {
            Object objectFromFile = in.readObject();
            assertEquals(objectFromFile, objectSerialised);
        }
    }

}
