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
package org.threeten.bp.temporal;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;

import org.testng.annotations.Test;

/**
 * Test DateTimeAdjusters.
 */
@Test(groups={"implementation"})
public class TestDateTimeAdjusters {

    @SuppressWarnings("rawtypes")
    public void test_constructor() throws Exception {
        for (Constructor constructor : DateTimeAdjusters.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expectedExceptions = InvocationTargetException.class, groups={"tck"})
    public void test_forceCoverage() throws Exception {
        Enum en = (Enum) DateTimeAdjusters.lastDayOfYear();
        Class cls = en.getClass();
        Method m = cls.getMethod("valueOf", String.class);
        m.invoke(null, en.name());
        m.invoke(null, "NOTREAL");
    }

    public void factory_firstDayOfMonthSame() {
        assertSame(DateTimeAdjusters.firstDayOfMonth(), DateTimeAdjusters.firstDayOfMonth());
    }

    public void factory_lastDayOfMonthSame() {
        assertSame(DateTimeAdjusters.lastDayOfMonth(), DateTimeAdjusters.lastDayOfMonth());
    }

    public void factory_firstDayOfNextMonthSame() {
        assertSame(DateTimeAdjusters.firstDayOfNextMonth(), DateTimeAdjusters.firstDayOfNextMonth());
    }

    public void factory_firstDayOfYearSame() {
        assertSame(DateTimeAdjusters.firstDayOfYear(), DateTimeAdjusters.firstDayOfYear());
    }

    public void factory_lastDayOfYearSame() {
        assertSame(DateTimeAdjusters.lastDayOfYear(), DateTimeAdjusters.lastDayOfYear());
    }

    public void factory_firstDayOfNextYearSame() {
        assertSame(DateTimeAdjusters.firstDayOfNextYear(), DateTimeAdjusters.firstDayOfNextYear());
    }

}
