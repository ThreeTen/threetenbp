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
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.UnsupportedCalendarFieldException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test FractionPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestFractionPrinterParser {

    private static DateTimeFieldRule NANO_RULE = ISOChronology.nanoOfSecondRule();
    private static DateTimeFieldRule SECOND_RULE = ISOChronology.secondOfMinuteRule();

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
    // print
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullAppendable() throws Exception {
        Calendrical calendrical = new Calendrical(NANO_RULE, 3);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 0, 9);
        pp.print(calendrical, (Appendable) null, symbols);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullDateTime() throws Exception {
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 0, 9);
        pp.print((Calendrical) null, buf, symbols);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullSymbols() throws Exception {
        Calendrical calendrical = new Calendrical(NANO_RULE, 3);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 0, 9);
        pp.print(calendrical, buf, (DateTimeFormatSymbols) null);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_print_emptyCalendrical() throws Exception {
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 0, 9);
        pp.print(emptyCalendrical, buf, symbols);
    }

    public void test_print_append() throws Exception {
        Calendrical calendrical = new Calendrical(NANO_RULE, 3);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 0, 9);
        buf.append("EXISTING");
        pp.print(calendrical, buf, symbols);
        assertEquals(buf.toString(), "EXISTING.000000003");
    }

    @Test(expectedExceptions=IOException.class)
    public void test_print_appendIO() throws Exception {
        Calendrical calendrical = new Calendrical(NANO_RULE, 3);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 0, 9);
        pp.print(calendrical, exceptionAppenable, symbols);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="Nanos")
    Object[][] provider_nanos() {
        return new Object[][] {
            {0, 9, 0,           ""},
            {0, 9, 2,           ".000000002"},
            {0, 9, 20,          ".00000002"},
            {0, 9, 200,         ".0000002"},
            {0, 9, 2000,        ".000002"},
            {0, 9, 20000,       ".00002"},
            {0, 9, 200000,      ".0002"},
            {0, 9, 2000000,     ".002"},
            {0, 9, 20000000,    ".02"},
            {0, 9, 200000000,   ".2"},
            {0, 9, 1,           ".000000001"},
            {0, 9, 12,          ".000000012"},
            {0, 9, 123,         ".000000123"},
            {0, 9, 1234,        ".000001234"},
            {0, 9, 12345,       ".000012345"},
            {0, 9, 123456,      ".000123456"},
            {0, 9, 1234567,     ".001234567"},
            {0, 9, 12345678,    ".012345678"},
            {0, 9, 123456789,   ".123456789"},
            
            {1, 9, 0,           ".0"},
            {1, 9, 2,           ".000000002"},
            {1, 9, 20,          ".00000002"},
            {1, 9, 200,         ".0000002"},
            {1, 9, 2000,        ".000002"},
            {1, 9, 20000,       ".00002"},
            {1, 9, 200000,      ".0002"},
            {1, 9, 2000000,     ".002"},
            {1, 9, 20000000,    ".02"},
            {1, 9, 200000000,   ".2"},
            
            {2, 3, 0,           ".00"},
            {2, 3, 2,           ".000"},
            {2, 3, 20,          ".000"},
            {2, 3, 200,         ".000"},
            {2, 3, 2000,        ".000"},
            {2, 3, 20000,       ".000"},
            {2, 3, 200000,      ".000"},
            {2, 3, 2000000,     ".002"},
            {2, 3, 20000000,    ".02"},
            {2, 3, 200000000,   ".20"},
            {2, 3, 1,           ".000"},
            {2, 3, 12,          ".000"},
            {2, 3, 123,         ".000"},
            {2, 3, 1234,        ".000"},
            {2, 3, 12345,       ".000"},
            {2, 3, 123456,      ".000"},
            {2, 3, 1234567,     ".001"},
            {2, 3, 12345678,    ".012"},
            {2, 3, 123456789,   ".123"},
            
            {6, 6, 0,           ".000000"},
            {6, 6, 2,           ".000000"},
            {6, 6, 20,          ".000000"},
            {6, 6, 200,         ".000000"},
            {6, 6, 2000,        ".000002"},
            {6, 6, 20000,       ".000020"},
            {6, 6, 200000,      ".000200"},
            {6, 6, 2000000,     ".002000"},
            {6, 6, 20000000,    ".020000"},
            {6, 6, 200000000,   ".200000"},
            {6, 6, 1,           ".000000"},
            {6, 6, 12,          ".000000"},
            {6, 6, 123,         ".000000"},
            {6, 6, 1234,        ".000001"},
            {6, 6, 12345,       ".000012"},
            {6, 6, 123456,      ".000123"},
            {6, 6, 1234567,     ".001234"},
            {6, 6, 12345678,    ".012345"},
            {6, 6, 123456789,   ".123456"},
       };
    }

    @Test(dataProvider="Nanos")
    public void test_print_nanos(int minWidth, int maxWidth, int value, String result) throws Exception {
        Calendrical calendrical = new Calendrical(NANO_RULE, value);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, minWidth, maxWidth);
        pp.print(calendrical, buf, symbols);
        if (result == null) {
            fail("Expected exception");
        }
        assertEquals(buf.toString(), result);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="Seconds")
    Object[][] provider_seconds() {
        return new Object[][] {
            {0, 9, 0,  ""},
            {0, 9, 3,  ".05"},
            {0, 9, 6,  ".1"},
            {0, 9, 9,  ".15"},
            {0, 9, 12, ".2"},
            {0, 9, 15, ".25"},
            {0, 9, 30, ".5"},
            {0, 9, 45, ".75"},
            
            {2, 2, 0,  ".00"},
            {2, 2, 3,  ".05"},
            {2, 2, 6,  ".10"},
            {2, 2, 9,  ".15"},
            {2, 2, 12, ".20"},
            {2, 2, 15, ".25"},
            {2, 2, 30, ".50"},
            {2, 2, 45, ".75"},
        };
    }

    @Test(dataProvider="Seconds")
    public void test_print_seconds(int minWidth, int maxWidth, int value, String result) throws Exception {
        Calendrical calendrical = new Calendrical(SECOND_RULE, value);
        FractionPrinterParser pp = new FractionPrinterParser(SECOND_RULE, minWidth, maxWidth);
        pp.print(calendrical, buf, symbols);
        if (result == null) {
            fail("Expected exception");
        }
        assertEquals(buf.toString(), result);
    }

    //-----------------------------------------------------------------------
    public void test_isPrintDataAvailable_true() throws Exception {
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        assertEquals(pp.isPrintDataAvailable(new Calendrical(NANO_RULE, 4)), true);
    }

    public void test_isPrintDataAvailable_false() throws Exception {
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        assertEquals(pp.isPrintDataAvailable(new Calendrical()), false);
    }

    //-----------------------------------------------------------------------
    // parse
    //-----------------------------------------------------------------------
    @Test(dataProvider="Nanos")
    public void test_reverseParse(int minWidth, int maxWidth, int value, String result) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, minWidth, maxWidth);
        int newPos = pp.parse(context, result, 0);
        assertEquals(newPos, result.length());
        int expectedValue = fixParsedValue(maxWidth, value);
        if (value == 0 && minWidth == 0) {
            assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
        } else {
            assertEquals(context.getFieldValue(NANO_RULE), expectedValue);
        }
    }

    @Test(dataProvider="Nanos")
    public void test_reverseParse_followedByNonDigit(int minWidth, int maxWidth, int value, String result) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, minWidth, maxWidth);
        int newPos = pp.parse(context, result + " ", 0);
        assertEquals(newPos, result.length());
        int expectedValue = fixParsedValue(maxWidth, value);
        if (value == 0 && minWidth == 0) {
            assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
        } else {
            assertEquals(context.getFieldValue(NANO_RULE), expectedValue);
        }
    }

    @Test(dataProvider="Nanos")
    public void test_reverseParse_preceededByNonDigit(int minWidth, int maxWidth, int value, String result) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, minWidth, maxWidth);
        int newPos = pp.parse(context, " " + result, 1);
        assertEquals(newPos, result.length() + 1);
        int expectedValue = fixParsedValue(maxWidth, value);
        if (value == 0 && minWidth == 0) {
            assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
        } else {
            assertEquals(context.getFieldValue(NANO_RULE), expectedValue);
        }
    }

    private int fixParsedValue(int maxWidth, int value) {
        if (maxWidth < 9) {
            int power = (int) Math.pow(10, (9 - maxWidth));
            value = (value / power) * power;
        }
        return value;
    }

    @Test(dataProvider="Seconds")
    public void test_reverseParse_seconds(int minWidth, int maxWidth, int value, String result) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(SECOND_RULE, minWidth, maxWidth);
        int newPos = pp.parse(context, result, 0);
        assertEquals(newPos, result.length());
        if (value == 0 && minWidth == 0) {
            assertEquals(context.toCalendrical().getFieldMap().contains(SECOND_RULE), false);
        } else {
            assertEquals(context.getFieldValue(SECOND_RULE), value);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_nothing_minWidth() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        int newPos = pp.parse(context, "", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
    }

    public void test_parse_noPoint() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        int newPos = pp.parse(context, "A", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
    }

    public void test_parse_point_noDigits() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        int newPos = pp.parse(context, ".", 0);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
    }

    public void test_parse_point_notMinWidthDigits1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        int newPos = pp.parse(context, ".5", 0);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
    }

    public void test_parse_point_notMinWidthDigits2() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        int newPos = pp.parse(context, ".51", 0);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
    }

    public void test_parse_point_nonDigit1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        int newPos = pp.parse(context, ".A23456", 0);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
    }

    public void test_parse_point_nonDigit2() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        int newPos = pp.parse(context, ".1A3456", 0);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendrical().getFieldMap().contains(NANO_RULE), false);
    }

    //-----------------------------------------------------------------------
    public void test_toString() throws Exception {
        FractionPrinterParser pp = new FractionPrinterParser(NANO_RULE, 3, 6);
        assertEquals(pp.toString(), "Fraction(ISO.NanoOfSecond,3,6)");
    }

}
