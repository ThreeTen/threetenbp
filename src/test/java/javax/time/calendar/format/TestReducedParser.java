/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Locale;

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ReducedPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestReducedParser {

    private static final DateTimeFieldRule<Integer> RULE_YEAR = ISOChronology.yearRule();
    private static final DateTimeFieldRule<Integer> RULE_DOY = ISOChronology.dayOfYearRule();

    private DateTimeFormatSymbols symbols;
    private DateTimeParseContext context;;

    @BeforeMethod
    public void setUp() {
        symbols = DateTimeFormatSymbols.getInstance(Locale.ENGLISH);
        context = new DateTimeParseContext(symbols);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullContext() throws Exception {
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        pp.parse((DateTimeParseContext) null, "12", 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullNumber() throws Exception {
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        pp.parse(context, (String) null, 0);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_print_invalidPositionTooSmall() throws Exception {
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        pp.parse(context, "12", -1);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_print_invalidPositionTooBig() throws Exception {
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        pp.parse(context, "12", 3);
    }

    public void test_parse_negativeZero() throws Exception {
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 1, 2010);
        int newPos = pp.parse(context, "-0", 0);
        assertEquals(newPos, ~0);
    }

    //-----------------------------------------------------------------------
    public void test_parse_replaceContextValue() throws Exception {
        context.setParsed(RULE_YEAR, 2001);
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        int newPos = pp.parse(context, "12", 0);
        assertEquals(newPos, 2);
        assertEquals(context.getParsed(RULE_YEAR), 2012);
    }

    public void test_parse_midStr() throws Exception {
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        int newPos = pp.parse(context, "Xxx12Xxx", 3);
        assertEquals(newPos, 5);
        assertEquals(context.getParsed(RULE_YEAR), 2012);
    }

    public void test_parse_remainderIgnored_maxWidth() throws Exception {
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        int newPos = pp.parse(context, "12345", 0);
        assertEquals(newPos, 2);
        assertEquals(context.getParsed(RULE_YEAR), 2012);
    }

    public void test_parse_remainderIgnored_nonDigit1() throws Exception {
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        int newPos = pp.parse(context, "12-45", 0);
        assertEquals(newPos, 2);
        assertEquals(context.getParsed(RULE_YEAR), 2012);
    }

    public void test_parse_fieldRangeIgnored() throws Exception {
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_DOY, 3, 10);
        int newPos = pp.parse(context, "456", 0);
        assertEquals(newPos, 3);
        assertEquals(context.getParsed(RULE_DOY), 456);  // parsed dayOfYear=456
    }

    //-----------------------------------------------------------------------
    public void test_parse_noMatch1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        int newPos = pp.parse(context, "A1", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(RULE_YEAR), false);
    }

    public void test_parse_noMatch2() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        int newPos = pp.parse(context, "  1", 1);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(RULE_YEAR), false);
    }

    public void test_parse_noMatch_notMinWidthLeft1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        int newPos = pp.parse(context, "1", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(RULE_YEAR), false);
    }

    public void test_parse_noMatch_notMinWidthLeft2_atEnd() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        int newPos = pp.parse(context, "1", 1);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(RULE_YEAR), false);
    }

    public void test_parse_noMatch_notMinWidthLeft_beforeNonDigit() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, 2, 2010);
        int newPos = pp.parse(context, "1-2", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(RULE_YEAR), false);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="Parse")
    Object[][] provider_parse() {
        return new Object[][] {
            // insufficient digits
            {"0", 2, 2010, ~0, null},
            {"1", 2, 2010, ~0, null},
            {"9", 2, 2010, ~0, null},
            
            // other junk
            {"A0", 2, 2010, ~0, null},
            {"0A", 2, 2010, ~0, null},
            {"-1", 2, 2010, ~0, null},
            {"-10", 2, 2010, ~0, null},
            
            // parse OK 1
            {"0", 1, 2010, 1, 2010},
            {"9", 1, 2010, 1, 2019},
            {"10", 1, 2010, 1, 2011},
            
            {"0", 1, 2005, 1, 2010},
            {"4", 1, 2005, 1, 2014},
            {"5", 1, 2005, 1, 2005},
            {"9", 1, 2005, 1, 2009},
            {"10", 1, 2005, 1, 2011},
            
            // parse OK 2
            {"00", 2, 2010, 2, 2100},
            {"09", 2, 2010, 2, 2109},
            {"10", 2, 2010, 2, 2010},
            {"99", 2, 2010, 2, 2099},
            {"100", 2, 2010, 2, 2010},
            
            // parse OK 2
            {"05", 2, -2005, 2, -2005},
            {"00", 2, -2005, 2, -2000},
            {"99", 2, -2005, 2, -1999},
            {"06", 2, -2005, 2, -1906},
            {"100", 2, -2005, 2, -1910},
       };
    }

    @Test(dataProvider="Parse") 
    public void test_parse(String input, int width, int baseValue, int parseLen, Integer parseVal) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, width, baseValue);
        int newPos = pp.parse(context, input, 0);
        assertEquals(newPos, parseLen);
        assertEquals(context.getParsed(RULE_YEAR), parseVal);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(RULE_YEAR), parseVal != null);
    }

    @Test(dataProvider="Parse") 
    public void test_parseLenient(String input, int width, int baseValue, int parseLen, Integer parseVal) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(false);
        ReducedPrinterParser pp = new ReducedPrinterParser(RULE_YEAR, width, baseValue);
        int newPos = pp.parse(context, input, 0);
        assertEquals(newPos, parseLen);
        assertEquals(context.getParsed(RULE_YEAR), parseVal);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(RULE_YEAR), parseVal != null);
    }

}
