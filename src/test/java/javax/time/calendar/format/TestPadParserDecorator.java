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

import javax.time.calendar.ISOChronology;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test PadPrinterParserDecorator.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestPadParserDecorator {

    private DateTimeParseContext context;

    @BeforeMethod
    public void setUp() {
        context = new DateTimeParseContext(DateTimeFormatSymbols.getInstance());
    }

    //-----------------------------------------------------------------------
    public void test_parse_nullContext() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(null, new CharLiteralPrinterParser('Z'), 3, '-');
        try {
            int result = pp.parse((DateTimeParseContext) null, "--Z", 0);
            assertEquals(result, 3);
            assertEquals(context.toCalendrical().getFieldMap().size(), 0);
            // NPE is optional, but parse must still succeed
        } catch (NullPointerException ex) {
            // NPE is optional
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_nullText() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(null, new CharLiteralPrinterParser('Z'), 3, '-');
        pp.parse(context, (String) null, 0);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_negativePosition() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(null, new CharLiteralPrinterParser('Z'), 3, '-');
        pp.parse(context, "--Z", -1);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_offEndPosition() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(null, new CharLiteralPrinterParser('Z'), 3, '-');
        pp.parse(context, "--Z", 4);
    }

    //-----------------------------------------------------------------------
    public void test_parse() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(
                null, new NumberPrinterParser(ISOChronology.monthOfYearRule(), 1, 3, SignStyle.NEVER), 3, '-');
        int result = pp.parse(context, "--2", 0);
        assertEquals(result, 3);
        assertEquals(context.toCalendrical().getFieldMap().size(), 1);
        assertEquals(context.getFieldValue(ISOChronology.monthOfYearRule()), 2);
    }

    public void test_parse_noReadBeyond() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(
                null, new NumberPrinterParser(ISOChronology.monthOfYearRule(), 1, 3, SignStyle.NEVER), 3, '-');
        int result = pp.parse(context, "--22", 0);
        assertEquals(result, 3);
        assertEquals(context.toCalendrical().getFieldMap().size(), 1);
        assertEquals(context.getFieldValue(ISOChronology.monthOfYearRule()), 2);
    }

    public void test_parse_textLessThanPadWidth() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(
                null, new NumberPrinterParser(ISOChronology.monthOfYearRule(), 1, 3, SignStyle.NEVER), 3, '-');
        int result = pp.parse(context, "-1", 0);
        assertEquals(result, ~0);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
    }

    public void test_parse_decoratedErrorPassedBack() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(
                null, new NumberPrinterParser(ISOChronology.monthOfYearRule(), 1, 3, SignStyle.NEVER), 3, '-');
        int result = pp.parse(context, "--A", 0);
        assertEquals(result, ~2);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
    }

    public void test_parse_decoratedDidNotParseToPadWidth() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(
                null, new NumberPrinterParser(ISOChronology.monthOfYearRule(), 1, 3, SignStyle.NEVER), 3, '-');
        int result = pp.parse(context, "-1X", 0);
        assertEquals(result, ~0);
//        assertEquals(context.getFieldValueMap().size(), 0);
    }

    //-----------------------------------------------------------------------
    public void test_parse_decoratedStartsWithPad() throws Exception {
        PadPrinterParserDecorator pp = new PadPrinterParserDecorator(
                null, new StringLiteralPrinterParser("-HELLO-"), 8, '-');
        int result = pp.parse(context, "--HELLO-", 0);
        assertEquals(result, 8);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
    }

}
