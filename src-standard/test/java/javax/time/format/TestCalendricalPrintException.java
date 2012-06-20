/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.format;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.io.IOException;

import org.testng.annotations.Test;

/**
 * Test CalendricalPrintException.
 */
@Test
public class TestCalendricalPrintException {

	@Test(groups={"tck"})
    public void test_constructor_String() throws Exception {
        CalendricalPrintException ex = new CalendricalPrintException("TEST");
        assertEquals(ex.getMessage(), "TEST");
    }

	@Test(groups={"implementation"})
    public void test_constructor_StringThrowable_notIOException_same() throws Exception {
        IllegalArgumentException iaex = new IllegalArgumentException("INNER");
        CalendricalPrintException ex = new CalendricalPrintException("TEST", iaex);
        assertEquals(ex.getMessage(), "TEST");
        assertSame(ex.getCause(), iaex);
        ex.rethrowIOException();  // no effect
    }
    
	@Test(groups={"tck"})
    public void test_constructor_StringThrowable_notIOException_equal() throws Exception {
        IllegalArgumentException iaex = new IllegalArgumentException("INNER");
        CalendricalPrintException ex = new CalendricalPrintException("TEST", iaex);
        assertEquals(ex.getMessage(), "TEST");
        assertEquals(ex.getCause(), iaex);
        ex.rethrowIOException();  // no effect
    }

    @Test(expectedExceptions=IOException.class, groups={"implementation"})
    public void test_constructor_StringThrowable_IOException_same() throws Exception {
        IOException ioex = new IOException("INNER");
        CalendricalPrintException ex = new CalendricalPrintException("TEST", ioex);
        assertEquals(ex.getMessage(), "TEST");
        assertSame(ex.getCause(), ioex);
        ex.rethrowIOException();  // rethrows
    }
    
    @Test(expectedExceptions=IOException.class, groups={"tck"})
    public void test_constructor_StringThrowable_IOException() throws Exception {
        IOException ioex = new IOException("INNER");
        CalendricalPrintException ex = new CalendricalPrintException("TEST", ioex);
        assertEquals(ex.getMessage(), "TEST");
        assertEquals(ex.getCause(), ioex);
        ex.rethrowIOException();  // rethrows
    }

}
