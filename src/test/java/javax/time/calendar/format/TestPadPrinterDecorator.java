/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.calendar.LocalDate;
import javax.time.calendar.field.DayOfMonth;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test PadPrinterDecorator.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestPadPrinterDecorator {

    private StringBuilder buf;
    private Appendable exceptionAppenable;
    private Calendrical emptyCalendrical;
    private DateTimeFormatSymbols symbols;

    @BeforeMethod
    public void setUp() {
        buf = new StringBuilder();
        exceptionAppenable = new MockIOExceptionAppendable();
        emptyCalendrical = new Calendrical();
        symbols = DateTimeFormatSymbols.getInstance(Locale.ENGLISH);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullAppendable() throws Exception {
        Calendrical calendrical = new Calendrical(DayOfMonth.rule(), 3);
        PadPrinterDecorator pp = new PadPrinterDecorator(new CharLiteralPrinterParser('Z'), 3, '-');
        pp.print(calendrical, (Appendable) null, symbols);
    }

// NPE is not required
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_print_nullDateTime() throws Exception {
//        PadPrinterDecorator pp = new PadPrinterDecorator(new CharLiteralPrinterParser('Z'), 3, '-');
//        pp.print(buf, (Calendrical) null, locale);
//    }

// NPE is not required
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_print_nullLocale() throws Exception {
//        SimplePadPrinterDecorator pp = new SimplePadPrinterDecorator("hello");
//        pp.print(buf, emptyCalendrical, (Locale) null);
//        assertEquals(buf, "EXISTINGhello");
//    }

    //-----------------------------------------------------------------------
    public void test_print_emptyCalendrical() throws Exception {
        PadPrinterDecorator pp = new PadPrinterDecorator(new CharLiteralPrinterParser('Z'), 3, '-');
        pp.print(emptyCalendrical, buf, symbols);
        assertEquals(buf.toString(), "--Z");
    }

    public void test_print_fullDateTime() throws Exception {
        Calendrical calendrical = LocalDate.date(2008, 12, 3).toCalendrical();
        PadPrinterDecorator pp = new PadPrinterDecorator(new CharLiteralPrinterParser('Z'), 3, '-');
        pp.print(calendrical, buf, symbols);
        assertEquals(buf.toString(), "--Z");
    }

    public void test_print_append() throws Exception {
        buf.append("EXISTING");
        PadPrinterDecorator pp = new PadPrinterDecorator(new CharLiteralPrinterParser('Z'), 3, '-');
        pp.print(emptyCalendrical, buf, symbols);
        assertEquals(buf.toString(), "EXISTING--Z");
    }

    @Test(expectedExceptions=IOException.class)
    public void test_print_appendIO() throws Exception {
        PadPrinterDecorator pp = new PadPrinterDecorator(new CharLiteralPrinterParser('Z'), 3, '-');
        pp.print(emptyCalendrical, exceptionAppenable, symbols);
    }

    //-----------------------------------------------------------------------
    public void test_print_noPadRequiredSingle() throws Exception {
        PadPrinterDecorator pp = new PadPrinterDecorator(new CharLiteralPrinterParser('Z'), 1, '-');
        pp.print(emptyCalendrical, buf, symbols);
        assertEquals(buf.toString(), "Z");
    }

    public void test_print_padRequiredSingle() throws Exception {
        PadPrinterDecorator pp = new PadPrinterDecorator(new CharLiteralPrinterParser('Z'), 5, '-');
        pp.print(emptyCalendrical, buf, symbols);
        assertEquals(buf.toString(), "----Z");
    }

    public void test_print_noPadRequiredMultiple() throws Exception {
        PadPrinterDecorator pp = new PadPrinterDecorator(new StringLiteralPrinterParser("WXYZ"), 4, '-');
        pp.print(emptyCalendrical, buf, symbols);
        assertEquals(buf.toString(), "WXYZ");
    }

    public void test_print_padRequiredMultiple() throws Exception {
        PadPrinterDecorator pp = new PadPrinterDecorator(new StringLiteralPrinterParser("WXYZ"), 5, '-');
        pp.print(emptyCalendrical, buf, symbols);
        assertEquals(buf.toString(), "-WXYZ");
    }

    @Test(expectedExceptions=CalendricalFormatException.class)
    public void test_print_overPad() throws Exception {
        PadPrinterDecorator pp = new PadPrinterDecorator(new StringLiteralPrinterParser("WXYZ"), 3, '-');
        pp.print(emptyCalendrical, buf, symbols);
    }

}
