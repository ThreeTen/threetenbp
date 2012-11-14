/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendrical;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;

import javax.time.LocalDate;
import javax.time.Month;

import org.testng.annotations.Test;

/**
 * Test DateTimeAdjusters.
 */
@Test
public class TestDateTimeAdjusters {

    @SuppressWarnings("rawtypes")
    @Test(groups={"implementation"})
    public void test_constructor() throws Exception {
        for (Constructor constructor : DateTimeAdjusters.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    @Test(groups={"implementation"})
    public void factory_firstDayOfMonthSame() {
        assertSame(DateTimeAdjusters.firstDayOfMonth(), DateTimeAdjusters.firstDayOfMonth());
    }

    @Test(groups={"implementation"})
    public void factory_lastDayOfMonthSame() {
        assertSame(DateTimeAdjusters.lastDayOfMonth(), DateTimeAdjusters.lastDayOfMonth());
    }

    @Test(groups={"implementation"})
    public void factory_firstDayOfNextMonthSame() {
        assertSame(DateTimeAdjusters.firstDayOfNextMonth(), DateTimeAdjusters.firstDayOfNextMonth());
    }

    @Test(groups={"implementation"})
    public void factory_firstDayOfYearSame() {
        assertSame(DateTimeAdjusters.firstDayOfYear(), DateTimeAdjusters.firstDayOfYear());
    }

    @Test(groups={"implementation"})
    public void factory_lastDayOfYearSame() {
        assertSame(DateTimeAdjusters.lastDayOfYear(), DateTimeAdjusters.lastDayOfYear());
    }

    @Test(groups={"implementation"})
    public void factory_firstDayOfNextYearSame() {
        assertSame(DateTimeAdjusters.firstDayOfNextYear(), DateTimeAdjusters.firstDayOfNextYear());
    }

    private LocalDate date(int year, Month month, int day) {
        return LocalDate.of(year, month, day);
    }

    private LocalDate date(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

}
