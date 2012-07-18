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

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Locale;

import javax.time.calendrical.DateTimeField;
import javax.time.format.DateTimeFormatterBuilder.TextPrinterParser;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test TextPrinterParser.
 */
@Test(groups={"implementation"})
public class TestTextParser extends AbstractTestPrinterParser {

    private static final DateTimeTextProvider PROVIDER = DateTimeFormatters.getTextProvider();

    //-----------------------------------------------------------------------
    @DataProvider(name="error")
    Object[][] data_error() {
        return new Object[][] {
            {new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL, PROVIDER), "Monday", -1, IndexOutOfBoundsException.class},
            {new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL, PROVIDER), "Monday", 7, IndexOutOfBoundsException.class},
        };
    }

    @Test(dataProvider="error")
    public void test_parse_error(TextPrinterParser pp, String text, int pos, Class<?> expected) {
        try {
            pp.parse(parseContext, text, pos);
        } catch (RuntimeException ex) {
            assertTrue(expected.isInstance(ex));
            assertEquals(parseContext.getParsed().size(), 0);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_midStr() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "XxxMondayXxx", 3);
        assertEquals(newPos, 9);
        assertParsed(parseContext, DAY_OF_WEEK, 1L);
    }

    public void test_parse_remainderIgnored() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.SHORT, PROVIDER);
        int newPos = pp.parse(parseContext, "Wednesday", 0);
        assertEquals(newPos, 3);
        assertParsed(parseContext, DAY_OF_WEEK, 3L);
    }

    //-----------------------------------------------------------------------
    public void test_parse_noMatch1() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "Munday", 0);
        assertEquals(newPos, ~0);
        assertEquals(parseContext.getParsed().size(), 0);
    }

    public void test_parse_noMatch2() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "Monday", 3);
        assertEquals(newPos, ~3);
        assertEquals(parseContext.getParsed().size(), 0);
    }

    public void test_parse_noMatch_atEnd() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(DAY_OF_WEEK, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "Monday", 6);
        assertEquals(newPos, ~6);
        assertEquals(parseContext.getParsed().size(), 0);
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
    public void test_parseText(DateTimeField field, TextStyle style, int value, String input) throws Exception {
        TextPrinterParser pp = new TextPrinterParser(field, style, PROVIDER);
        int newPos = pp.parse(parseContext, input, 0);
        assertEquals(newPos, input.length());
        assertParsed(parseContext, field, (long) value);
    }

    @Test(dataProvider="parseNumber")
    public void test_parseNumber(DateTimeField field, TextStyle style, int value, String input) throws Exception {
        TextPrinterParser pp = new TextPrinterParser(field, style, PROVIDER);
        int newPos = pp.parse(parseContext, input, 0);
        assertEquals(newPos, input.length());
        assertParsed(parseContext, field, (long) value);
    }

    //-----------------------------------------------------------------------
    @Test(dataProvider="parseText")
    public void test_parse_strict_caseSensitive_parseUpper(DateTimeField field, TextStyle style, int value, String input) throws Exception {
        parseContext.setCaseSensitive(true);
        TextPrinterParser pp = new TextPrinterParser(field, style, PROVIDER);
        int newPos = pp.parse(parseContext, input.toUpperCase(), 0);
        assertEquals(newPos, ~0);
        assertEquals(parseContext.getParsed().size(), 0);
    }

    @Test(dataProvider="parseText")
    public void test_parse_strict_caseInsensitive_parseUpper(DateTimeField field, TextStyle style, int value, String input) throws Exception {
        parseContext.setCaseSensitive(false);
        TextPrinterParser pp = new TextPrinterParser(field, style, PROVIDER);
        int newPos = pp.parse(parseContext, input.toUpperCase(), 0);
        assertEquals(newPos, input.length());
        assertParsed(parseContext, field, (long) value);
    }

    //-----------------------------------------------------------------------
    @Test(dataProvider="parseText")
    public void test_parse_strict_caseSensitive_parseLower(DateTimeField field, TextStyle style, int value, String input) throws Exception {
        parseContext.setCaseSensitive(true);
        TextPrinterParser pp = new TextPrinterParser(field, style, PROVIDER);
        int newPos = pp.parse(parseContext, input.toLowerCase(), 0);
        assertEquals(newPos, ~0);
        assertEquals(parseContext.getParsed().size(), 0);
    }

    @Test(dataProvider="parseText")
    public void test_parse_strict_caseInsensitive_parseLower(DateTimeField field, TextStyle style, int value, String input) throws Exception {
        parseContext.setCaseSensitive(false);
        TextPrinterParser pp = new TextPrinterParser(field, style, PROVIDER);
        int newPos = pp.parse(parseContext, input.toLowerCase(), 0);
        assertEquals(newPos, input.length());
        assertParsed(parseContext, field, (long) value);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_parse_full_strict_full_match() throws Exception {
        parseContext.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "January", 0);
        assertEquals(newPos, 7);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    public void test_parse_full_strict_short_noMatch() throws Exception {
        parseContext.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "Janua", 0);
        assertEquals(newPos, ~0);
        assertEquals(parseContext.getParsed().size(), 0);
    }

    public void test_parse_full_strict_number_noMatch() throws Exception {
        parseContext.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "1", 0);
        assertEquals(newPos, ~0);
        assertEquals(parseContext.getParsed().size(), 0);
    }

    //-----------------------------------------------------------------------
    public void test_parse_short_strict_full_match() throws Exception {
        parseContext.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT, PROVIDER);
        int newPos = pp.parse(parseContext, "January", 0);
        assertEquals(newPos, 3);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    public void test_parse_short_strict_short_match() throws Exception {
        parseContext.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT, PROVIDER);
        int newPos = pp.parse(parseContext, "Janua", 0);
        assertEquals(newPos, 3);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    public void test_parse_short_strict_number_noMatch() throws Exception {
        parseContext.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT, PROVIDER);
        int newPos = pp.parse(parseContext, "1", 0);
        assertEquals(newPos, ~0);
        assertEquals(parseContext.getParsed().size(), 0);
    }

    //-----------------------------------------------------------------------
    public void test_parse_french_short_strict_full_noMatch() throws Exception {
        parseContext.setLocale(Locale.FRENCH);
        parseContext.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT, PROVIDER);
        int newPos = pp.parse(parseContext, "janvier", 0);  // correct short form is 'janv.'
        assertEquals(newPos, ~0);
        assertEquals(parseContext.getParsed().size(), 0);
    }

    public void test_parse_french_short_strict_short_match() throws Exception {
        parseContext.setLocale(Locale.FRENCH);
        parseContext.setStrict(true);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT, PROVIDER);
        int newPos = pp.parse(parseContext, "janv.", 0);
        assertEquals(newPos, 5);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    //-----------------------------------------------------------------------
    public void test_parse_full_lenient_full_match() throws Exception {
        parseContext.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "January", 0);
        assertEquals(newPos, 7);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    public void test_parse_full_lenient_short_match() throws Exception {
        parseContext.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "Janua", 0);
        assertEquals(newPos, 3);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    public void test_parse_full_lenient_number_match() throws Exception {
        parseContext.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.FULL, PROVIDER);
        int newPos = pp.parse(parseContext, "1", 0);
        assertEquals(newPos, 1);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    //-----------------------------------------------------------------------
    public void test_parse_short_lenient_full_match() throws Exception {
        parseContext.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT, PROVIDER);
        int newPos = pp.parse(parseContext, "January", 0);
        assertEquals(newPos, 7);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    public void test_parse_short_lenient_short_match() throws Exception {
        parseContext.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT, PROVIDER);
        int newPos = pp.parse(parseContext, "Janua", 0);
        assertEquals(newPos, 3);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    public void test_parse_short_lenient_number_match() throws Exception {
        parseContext.setStrict(false);
        TextPrinterParser pp = new TextPrinterParser(MONTH_OF_YEAR, TextStyle.SHORT, PROVIDER);
        int newPos = pp.parse(parseContext, "1", 0);
        assertEquals(newPos, 1);
        assertParsed(parseContext, MONTH_OF_YEAR, 1L);
    }

    private void assertParsed(DateTimeParseContext context, DateTimeField field, Long value) {
        if (value == null) {
            assertEquals(context.getParsed(field), null);
        } else {
            assertEquals(context.getParsed(field), value);
        }
    }

}
