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
package java.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.time.CalendricalException;
import javax.time.Instant;

/**
 * Test java.util.Date additional methods.
 * <p>
 * To run this, you need the built classes and TestNG in the bootstrap classpath.
 */
//@Test
public class TestDate {
    
    public static void main(String[] args) {
        int success = 0;
        int failure = 0;
        int error = 0;
        try {
            Method[] methods = TestDate.class.getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isStatic(method.getModifiers()) == false && method.getName().startsWith("test")) {
                    try {
                        try {
                            method.invoke(new TestDate());
                        } catch (InvocationTargetException itex) {
                            throw itex.getCause();
                        }
                        System.out.println(method.getName() + " SUCCEEDED");
                        success++;
                    } catch (AssertionError ex) {
                        System.out.println(method.getName() + " FAILED");
                        ex.printStackTrace();
                        failure++;
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        error++;
                    }
                }
            }
            
        } catch (Throwable th) {
            th.printStackTrace();
        }
        System.out.println("Success: " + success);
        System.out.println("Failure: " + failure);
        System.out.println("Error:   " + error);
    }

    //-----------------------------------------------------------------------
    public void test_constructor() {
        for (int i = -1100; i < 1100; i++) {
            Instant instant = Instant.ofEpochMilli(i);
            Date test = new Date(instant);
            assertEquals(test.getTime(), i);
        }
    }

    public void test_constructor_null() {
        try {
            new Date((Instant) null);
            fail();
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void test_constructor_tooBig() {
        try {
            new Date(Instant.ofEpochSecond(Long.MAX_VALUE / 1000 + 1));
            fail();
        } catch (CalendricalException ex) {
            // expected
        }
    }

    public void test_constructor_tooSmall() {
        try {
            new Date(Instant.ofEpochSecond(Long.MIN_VALUE / 1000 - 1));
            fail();
        } catch (CalendricalException ex) {
            // expected
        }
    }

    //-----------------------------------------------------------------------
    public void test_setInstant() {
        for (int i = -1100; i < 1100; i++) {
            Instant instant = Instant.ofEpochMilli(i);
            Date test = new Date();
            test.setInstant(instant);
            assertEquals(test.getTime(), i);
        }
    }

    public void test_setInstant_null() {
        Date test = new Date();
        try {
            test.setInstant((Instant) null);
            fail();
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void test_setInstant_tooBig() {
        Date test = new Date();
        try {
            test.setInstant(Instant.ofEpochSecond(Long.MAX_VALUE / 1000 + 1));
            fail();
        } catch (CalendricalException ex) {
            // expected
        }
    }

    public void test_setInstant_tooSmall() {
        Date test = new Date();
        try {
            test.setInstant(Instant.ofEpochSecond(Long.MIN_VALUE / 1000 - 1));
            fail();
        } catch (CalendricalException ex) {
            // expected
        }
    }

    //-----------------------------------------------------------------------
    public void test_toInstant() {
        for (int i = -1100; i < 1100; i++) {
            Date utilDate = new Date(i);
            Instant test = utilDate.toInstant();
            assertEquals(test, Instant.ofEpochMilli(i));
        }
    }

}
