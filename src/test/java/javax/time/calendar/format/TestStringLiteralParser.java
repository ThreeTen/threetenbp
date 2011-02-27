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

import org.testng.annotations.Test;

/**
 * Test StringLiteralPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestStringLiteralParser extends AbstractTestPrinterParser {

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_negativePosition() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        pp.parse(parseContext, "hello", -1);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_offEndPosition() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        pp.parse(parseContext, "hello", 6);
    }

    //-----------------------------------------------------------------------
    public void test_parse_exactMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "hello", 0);
        assertEquals(result, 5);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_startStringMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "helloOTHER", 0);
        assertEquals(result, 5);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_midStringMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "OTHERhelloOTHER", 5);
        assertEquals(result, 10);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_endStringMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "OTHERhello", 5);
        assertEquals(result, 10);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    //-----------------------------------------------------------------------
    public void test_parse_emptyStringNoMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "", 0);
        assertEquals(result, ~0);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_startStringNoMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "hlloo", 0);
        assertEquals(result, ~0);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_midStringNoMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "OTHERhlloOTHER", 5);
        assertEquals(result, ~5);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_endStringNoMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "hello", 5);
        assertEquals(result, ~5);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_startStringTooShortNoMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "h", 0);
        assertEquals(result, ~0);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_midStringTooShortNoMatch() throws Exception {
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "OTHERhel", 5);
        assertEquals(result, ~5);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    //-----------------------------------------------------------------------
    public void test_parse_caseSensitive() throws Exception {
        parseContext.setCaseSensitive(true);
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "HELLO", 0);
        assertEquals(result, ~0);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

    public void test_parse_caseInsensitive() throws Exception {
        parseContext.setCaseSensitive(false);
        StringLiteralPrinterParser pp = new StringLiteralPrinterParser("hello");
        int result = pp.parse(parseContext, "HELLO", 0);
        assertEquals(result, 5);
        assertEquals(parseContext.toCalendricalMerger().getInputMap().size(), 0);
    }

}
