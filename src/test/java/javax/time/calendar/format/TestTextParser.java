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

import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static org.testng.Assert.assertEquals;

import java.util.Locale;

import javax.time.calendar.DateTimeRule;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test TextPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestTextParser {

    private DateTimeFormatSymbols symbols;

    @BeforeMethod
    public void setUp() {
        symbols = DateTimeFormatSymbols.getInstance(Locale.ENGLISH);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullContext() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL);
        pp.parse((DateTimeParseContext) null, "Monday", 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullText() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setParsedField(DAY_OF_WEEK, 2);
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL);
        pp.parse(context, (String) null, 0);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_print_invalidPositionTooSmall() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setParsedField(DAY_OF_WEEK, 2);
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL);
        pp.parse(context, "Monday", -1);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_print_invalidPositionTooBig() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setParsedField(DAY_OF_WEEK, 2);
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL);
        pp.parse(context, "Monday", 7);
    }

    //-----------------------------------------------------------------------
    public void test_parse_replaceContextValue() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setParsedField(DAY_OF_WEEK, 2);
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL);
        int newPos = pp.parse(context, "Monday", 0);
        assertEquals(newPos, 6);
        assertEquals(context.getParsed(DAY_OF_WEEK), (long) 1);
    }

    public void test_parse_midStr() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL);
        int newPos = pp.parse(context, "XxxMondayXxx", 3);
        assertEquals(newPos, 9);
        assertEquals(context.getParsed(DAY_OF_WEEK), (long) 1);
    }

    public void test_parse_remainderIgnored() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.SHORT);
        int newPos = pp.parse(context, "Wednesday", 0);
        assertEquals(newPos, 3);
        assertEquals(context.getParsed(DAY_OF_WEEK), (long) 3);
    }

    //-----------------------------------------------------------------------
    public void test_parse_noMatch1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL);
        int newPos = pp.parse(context, "Munday", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(DAY_OF_WEEK), false);
    }

    public void test_parse_noMatch2() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL);
        int newPos = pp.parse(context, "Monday", 3);
        assertEquals(newPos, ~3);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(DAY_OF_WEEK), false);
    }

    public void test_parse_noMatch_atEnd() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL);
        int newPos = pp.parse(context, "Monday", 6);
        assertEquals(newPos, ~6);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(DAY_OF_WEEK), false);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="parseText")
    Object[][] provider_text() {
        return new Object[][] {
            {DAY_OF_WEEK, TextStyle.FULL, 1, "Monday"},
            {DAY_OF_WEEK, TextStyle.FULL, 2, "Tuesday"},
            {DAY_OF_WEEK, TextStyle.FULL, 3, "Wednesday"},
            {DAY_OF_WEEK, TextStyle.FULL, 4, "Thursday"},
            {DAY_OF_WEEK, TextStyle.FULL, 5, "Friday"},
            {DAY_OF_WEEK, TextStyle.FULL, 6, "Saturday"},
            {DAY_OF_WEEK, TextStyle.FULL, 7, "Sunday"},
            
            {DAY_OF_WEEK, TextStyle.SHORT, 1, "Mon"},
            {DAY_OF_WEEK, TextStyle.SHORT, 2, "Tue"},
            {DAY_OF_WEEK, TextStyle.SHORT, 3, "Wed"},
            {DAY_OF_WEEK, TextStyle.SHORT, 4, "Thu"},
            {DAY_OF_WEEK, TextStyle.SHORT, 5, "Fri"},
            {DAY_OF_WEEK, TextStyle.SHORT, 6, "Sat"},
            {DAY_OF_WEEK, TextStyle.SHORT, 7, "Sun"},
            
            {MONTH_OF_YEAR, TextStyle.FULL, 1, "January"},
            {MONTH_OF_YEAR, TextStyle.FULL, 12, "December"},
            
            {MONTH_OF_YEAR, TextStyle.SHORT, 1, "Jan"},
            {MONTH_OF_YEAR, TextStyle.SHORT, 12, "Dec"},
       };
    }

    @DataProvider(name="parseNumber")
    Object[][] provider_number() {
        return new Object[][] {
            {DAY_OF_MONTH, TextStyle.FULL, 1, "1"},
            {DAY_OF_MONTH, TextStyle.FULL, 2, "2"},
            {DAY_OF_MONTH, TextStyle.FULL, 30, "30"},
            {DAY_OF_MONTH, TextStyle.FULL, 31, "31"},
            
            {DAY_OF_MONTH, TextStyle.SHORT, 1, "1"},
            {DAY_OF_MONTH, TextStyle.SHORT, 2, "2"},
            {DAY_OF_MONTH, TextStyle.SHORT, 30, "30"},
            {DAY_OF_MONTH, TextStyle.SHORT, 31, "31"},
       };
    }

    @Test(dataProvider="parseText")
    public void test_parseText(DateTimeRule rule, TextStyle style, int dow, String input) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(rule, style);
        int newPos = pp.parse(context, input, 0);
        assertEquals(newPos, input.length());
        assertEquals(context.getParsed(rule), (long) dow);
    }

    @Test(dataProvider="parseNumber")
    public void test_parseNumber(DateTimeRule rule, TextStyle style, int dow, String input) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(rule, style);
        int newPos = pp.parse(context, input, 0);
        assertEquals(newPos, input.length());
        assertEquals(context.getParsed(rule), (long) dow);
    }

    //-----------------------------------------------------------------------
    @Test(dataProvider="parseText")
    public void test_parse_strict_caseSensitive_parseUpper(DateTimeRule rule, TextStyle style, int dow, String input) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setCaseSensitive(true);
        TextPrinterParser pp = new TextPrinterParser(rule, style);
        int newPos = pp.parse(context, input.toUpperCase(), 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(DAY_OF_WEEK), false);
    }

    @Test(dataProvider="parseText")
    public void test_parse_strict_caseInsensitive_parseUpper(DateTimeRule rule, TextStyle style, int dow, String input) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setCaseSensitive(false);
        TextPrinterParser pp = new TextPrinterParser(rule, style);
        int newPos = pp.parse(context, input.toUpperCase(), 0);
        assertEquals(newPos, input.length());
        assertEquals(context.getParsed(rule), (long) dow);
    }

    //-----------------------------------------------------------------------
    @Test(dataProvider="parseText")
    public void test_parse_strict_caseSensitive_parseLower(DateTimeRule rule, TextStyle style, int dow, String input) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setCaseSensitive(true);
        TextPrinterParser pp = new TextPrinterParser(rule, style);
        int newPos = pp.parse(context, input.toLowerCase(), 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(DAY_OF_WEEK), false);
    }

    @Test(dataProvider="parseText")
    public void test_parse_strict_caseInsensitive_parseLower(DateTimeRule rule, TextStyle style, int dow, String input) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setCaseSensitive(false);
        TextPrinterParser pp = new TextPrinterParser(rule, style);
        int newPos = pp.parse(context, input.toLowerCase(), 0);
        assertEquals(newPos, input.length());
        assertEquals(context.getParsed(rule), (long) dow);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_parse_full_strict_full_match() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL);
        int newPos = pp.parse(context, "January", 0);
        assertEquals(newPos, 7);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

    public void test_parse_full_strict_short_noMatch() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL);
        int newPos = pp.parse(context, "Janua", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(MONTH_OF_YEAR), false);
    }

    public void test_parse_full_strict_number_noMatch() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL);
        int newPos = pp.parse(context, "1", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(MONTH_OF_YEAR), false);
    }

    //-----------------------------------------------------------------------
    public void test_parse_short_strict_full_match() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT);
        int newPos = pp.parse(context, "January", 0);
        assertEquals(newPos, 3);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

    public void test_parse_short_strict_short_match() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT);
        int newPos = pp.parse(context, "Janua", 0);
        assertEquals(newPos, 3);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

    public void test_parse_short_strict_number_noMatch() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT);
        int newPos = pp.parse(context, "1", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(MONTH_OF_YEAR), false);
    }

    //-----------------------------------------------------------------------
    public void test_parse_french_short_strict_full_noMatch() throws Exception {
        DateTimeFormatSymbols french = DateTimeFormatSymbols.getInstance(Locale.FRENCH);
        DateTimeParseContext context = new DateTimeParseContext(french);
        context.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT);
        int newPos = pp.parse(context, "janvier", 0);  // correct short form is 'janv.'
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendricalMerger().getInputMap().containsKey(MONTH_OF_YEAR), false);
    }

    public void test_parse_french_short_strict_short_match() throws Exception {
        DateTimeFormatSymbols french = DateTimeFormatSymbols.getInstance(Locale.FRENCH);
        DateTimeParseContext context = new DateTimeParseContext(french);
        context.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT);
        int newPos = pp.parse(context, "janv.", 0);
        assertEquals(newPos, 5);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

    //-----------------------------------------------------------------------
    public void test_parse_full_lenient_full_match() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL);
        int newPos = pp.parse(context, "January", 0);
        assertEquals(newPos, 7);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

    public void test_parse_full_lenient_short_match() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL);
        int newPos = pp.parse(context, "Janua", 0);
        assertEquals(newPos, 3);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

    public void test_parse_full_lenient_number_match() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL);
        int newPos = pp.parse(context, "1", 0);
        assertEquals(newPos, 1);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

    //-----------------------------------------------------------------------
    public void test_parse_short_lenient_full_match() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT);
        int newPos = pp.parse(context, "January", 0);
        assertEquals(newPos, 7);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

    public void test_parse_short_lenient_short_match() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT);
        int newPos = pp.parse(context, "Janua", 0);
        assertEquals(newPos, 3);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

    public void test_parse_short_lenient_number_match() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT);
        int newPos = pp.parse(context, "1", 0);
        assertEquals(newPos, 1);
        assertEquals(context.getParsed(MONTH_OF_YEAR), (long) 1);
    }

}
