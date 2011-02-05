/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test CharLiteralPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestCharLiteralParser {

    private DateTimeParseContext context;

    @BeforeMethod
    public void setUp() {
        context = new DateTimeParseContext(DateTimeFormatSymbols.getInstance());
    }

    //-----------------------------------------------------------------------
    public void test_parse_nullContext() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        try {
            int result = pp.parse((DateTimeParseContext) null, "a", 0);
            assertEquals(result, 1);
            assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
            // NPE is optional, but parse must still succeed
        } catch (NullPointerException ex) {
            // NPE is optional
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_nullText() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        pp.parse(context, (String) null, 0);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_negativePosition() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        pp.parse(context, "a", -1);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_offEndPosition() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        pp.parse(context, "a", 2);
    }

    //-----------------------------------------------------------------------
    public void test_parse_exactMatch() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "a", 0);
        assertEquals(result, 1);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_startStringMatch() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "aOTHER", 0);
        assertEquals(result, 1);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_midStringMatch() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "OTHERaOTHER", 5);
        assertEquals(result, 6);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_endStringMatch() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "OTHERa", 5);
        assertEquals(result, 6);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

    //-----------------------------------------------------------------------
    public void test_parse_emptyStringNoMatch() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "", 0);
        assertEquals(result, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_startStringNoMatch() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "b", 0);
        assertEquals(result, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_midStringNoMatch() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "OTHERbOTHER", 5);
        assertEquals(result, ~5);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_endStringNoMatch() throws Exception {
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "a", 1);
        assertEquals(result, ~1);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

    //-----------------------------------------------------------------------
    public void test_parse_caseSensitive() throws Exception {
        context.setCaseSensitive(true);
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "A", 0);
        assertEquals(result, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_caseInsensitive() throws Exception {
        context.setCaseSensitive(false);
        CharLiteralPrinterParser pp = new CharLiteralPrinterParser('a');
        int result = pp.parse(context, "A", 0);
        assertEquals(result, 1);
        assertEquals(context.toCalendricalMerger().getInputMap().size(), 0);
    }

}
