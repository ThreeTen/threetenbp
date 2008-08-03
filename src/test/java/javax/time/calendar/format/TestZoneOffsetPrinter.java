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
import javax.time.calendar.ZoneOffset;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test ZoneOffsetPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestZoneOffsetPrinter {

    private static final ZoneOffset OFFSET_0130 = ZoneOffset.zoneOffset("+01:30");
    private static final ZoneOffset OFFSET_M0245 = ZoneOffset.zoneOffset("-02:45");
    private static final ZoneOffset OFFSET_123456 = ZoneOffset.zoneOffset("+12:34:56");
    private StringBuilder buf;
    private Appendable exceptionAppenable;
    private Calendrical emptyCalendrical;
    private DateTimeFormatSymbols symbols;

    @BeforeMethod
    public void setUp() {
        buf = new StringBuilder("EXISTING");
        exceptionAppenable = new MockIOExceptionAppendable();
        emptyCalendrical = Calendrical.calendrical();
        symbols = DateTimeFormatSymbols.getInstance(Locale.ENGLISH);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullAppendable() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_0130, null);
        pp.print(cal, (Appendable) null, symbols);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullDateTime() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        pp.print((Calendrical) null, buf, symbols);
    }

// NPE is not required
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_print_nullLocale() throws Exception {
//        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
//        pp.print(buf, emptyCalendrical, (DateTimeFormatSymbols) null);
//    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=CalendricalFormatException.class)
    public void test_print_emptyCalendrical() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        pp.print(emptyCalendrical, buf, symbols);
    }

    public void test_print_emptyAppendable() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_0130, null);
        buf.setLength(0);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "+01:30");
    }

    @Test(expectedExceptions=IOException.class)
    public void test_print_appendIO() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_0130, null);
        pp.print(cal, exceptionAppenable, symbols);
    }

    //-----------------------------------------------------------------------
    public void test_print_0130() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_0130, null);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "EXISTING+01:30");
    }

    public void test_print_0130_noColon() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", false, true);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_0130, null);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "EXISTING+0130");
    }

    public void test_print_M0245() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_M0245, null);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "EXISTING-02:45");
    }

    public void test_print_M0245_noColon() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", false, true);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_M0245, null);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "EXISTING-0245");
    }

    public void test_print_UTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("ZZZZ", true, true);
        Calendrical cal = Calendrical.calendrical(null, null, ZoneOffset.UTC, null);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "EXISTINGZZZZ");
    }

    public void test_print_123456() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_123456, null);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "EXISTING+12:34:56");
    }

    public void test_print_123456_noColon() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", false, true);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_123456, null);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "EXISTING+123456");
    }

    public void test_print_123456_noSeconds() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, false);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_123456, null);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "EXISTING+12:34");
    }

    public void test_print_123456_noColon_noSeconds() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", false, false);
        Calendrical cal = Calendrical.calendrical(null, null, OFFSET_123456, null);
        pp.print(cal, buf, symbols);
        assertEquals(buf.toString(), "EXISTING+1234");
    }

}
