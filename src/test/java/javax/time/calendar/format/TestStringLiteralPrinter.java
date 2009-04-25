/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.format;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.LocalDateTime;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test StringLiteralPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestStringLiteralPrinter {

    private StringBuilder buf;
    private Appendable exceptionAppenable;
    private Calendrical emptyCalendrical;
    private Calendrical calendrical;
    private DateTimeFormatSymbols symbols;

    @BeforeMethod
    public void setUp() {
        buf = new StringBuilder("EXISTING");
        exceptionAppenable = new MockIOExceptionAppendable();
        emptyCalendrical = new Calendrical();
        calendrical = LocalDateTime.dateTime(2008, 12, 3, 10, 15).toCalendrical();
        symbols = DateTimeFormatSymbols.getInstance(Locale.ENGLISH);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullAppendable() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        pp.print(emptyCalendrical, (Appendable) null, symbols);
    }

// NPE is not required
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_print_nullDateTime() throws Exception {
//        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
//        pp.print(buf, (Calendrical) null, locale);
//        assertEquals(buf, "EXISTINGhello");
//    }

// NPE is not required
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_print_nullLocale() throws Exception {
//        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
//        pp.print(buf, emptyCalendrical, (Locale) null);
//        assertEquals(buf, "EXISTINGhello");
//    }

    //-----------------------------------------------------------------------
    public void test_print_emptyCalendrical() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        pp.print(emptyCalendrical, buf, symbols);
        assertEquals(buf.toString(), "EXISTINGhello");
    }

    public void test_print_dateTime() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        pp.print(calendrical, buf, symbols);
        assertEquals(buf.toString(), "EXISTINGhello");
    }

    public void test_print_emptyAppendable() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        buf.setLength(0);
        pp.print(calendrical, buf, symbols);
        assertEquals(buf.toString(), "hello");
    }

    @Test(expectedExceptions=IOException.class)
    public void test_print_appendIO() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        pp.print(calendrical, exceptionAppenable, symbols);
    }

    //-----------------------------------------------------------------------
    public void test_isPrintDataAvailable() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        assertEquals(pp.isPrintDataAvailable(calendrical), true);
    }

    //-----------------------------------------------------------------------
    public void test_toString() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        assertEquals(pp.toString(), "'hello'");
    }

}
