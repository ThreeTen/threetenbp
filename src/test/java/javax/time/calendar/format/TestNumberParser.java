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

import java.util.Locale;

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test NumberPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestNumberParser {

    private static final DateTimeFieldRule RULE_DOW = ISOChronology.dayOfWeekRule();
    private static final DateTimeFieldRule RULE_DOM = ISOChronology.dayOfMonthRule();
    private static final DateTimeFieldRule RULE_MOY = ISOChronology.monthOfYearRule();

    private DateTimeFormatSymbols symbols;

    @BeforeMethod
    public void setUp() {
        symbols = DateTimeFormatSymbols.getInstance(Locale.ENGLISH);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullContext() throws Exception {
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        pp.parse((DateTimeParseContext) null, "12", 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullNumber() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setFieldValue(RULE_DOM, 2);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        pp.parse(context, (String) null, 0);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_print_invalidPositionTooSmall() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setFieldValue(RULE_DOM, 2);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        pp.parse(context, "12", -1);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_print_invalidPositionTooBig() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setFieldValue(RULE_DOM, 2);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        pp.parse(context, "12", 3);
    }

    //-----------------------------------------------------------------------
    public void test_parse_replaceContextValue() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setFieldValue(RULE_DOM, 9);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        int newPos = pp.parse(context, "12", 0);
        assertEquals(newPos, 2);
        assertEquals(context.getFieldValueMapValue(RULE_DOM), 12);
    }

    public void test_parse_midStr1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        int newPos = pp.parse(context, "Xxx12Xxx", 3);
        assertEquals(newPos, 5);
        assertEquals(context.getFieldValueMapValue(RULE_DOM), 12);
    }

    public void test_parse_midStr2() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        int newPos = pp.parse(context, "99912999", 3);
        assertEquals(newPos, 5);
        assertEquals(context.getFieldValueMapValue(RULE_DOM), 12);
    }

    public void test_parse_remainderIgnored_maxWidth() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 2, 4, SignStyle.NEVER);
        int newPos = pp.parse(context, "12345", 0);
        assertEquals(newPos, 4);
        assertEquals(context.getFieldValueMapValue(RULE_DOM), 1234);
    }

    public void test_parse_remainderIgnored_nonDigit1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 2, 4, SignStyle.NEVER);
        int newPos = pp.parse(context, "12-45", 0);
        assertEquals(newPos, 2);
        assertEquals(context.getFieldValueMapValue(RULE_DOM), 12);
    }

    public void test_parse_remainderIgnored_nonDigit2() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 2, 4, SignStyle.NEVER);
        int newPos = pp.parse(context, "123-5", 0);
        assertEquals(newPos, 3);
        assertEquals(context.getFieldValueMapValue(RULE_DOM), 123);
    }

    public void test_parse_fieldRangeIgnored() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        int newPos = pp.parse(context, "32", 0);
        assertEquals(newPos, 2);
        assertEquals(context.getFieldValueMapValue(RULE_DOM), 32);  // parsed dayOfMonth=32
    }

    public void test_parse_textField() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOW, 1, 1, SignStyle.NEVER);
        int newPos = pp.parse(context, "5999", 0);
        assertEquals(newPos, 1);
        assertEquals(context.getFieldValueMapValue(RULE_DOW), 5);
    }

    public void test_parse_maxInteger() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 10, SignStyle.NEVER);
        int newPos = pp.parse(context, "2147483647", 0);
        assertEquals(newPos, 10);
        assertEquals(context.getFieldValueMapValue(RULE_DOM), 2147483647);
    }

    public void test_parse_minInteger() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 10, SignStyle.NORMAL);
        int newPos = pp.parse(context, "-2147483648", 0);
        assertEquals(newPos, 11);
        assertEquals(context.getFieldValueMapValue(RULE_DOM), -2147483648);
    }

    //-----------------------------------------------------------------------
    public void test_parse_noMatch1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        int newPos = pp.parse(context, "A1", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOM), false);
    }

    public void test_parse_noMatch2() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NEVER);
        int newPos = pp.parse(context, "  1", 1);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOM), false);
    }

    public void test_parse_noMatch_notMinWidthLeft1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 2, 2, SignStyle.NEVER);
        int newPos = pp.parse(context, "1", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOM), false);
    }

    public void test_parse_noMatch_notMinWidthLeft2_atEnd() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 2, 2, SignStyle.NEVER);
        int newPos = pp.parse(context, "1", 1);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOM), false);
    }

    public void test_parse_noMatch_notMinWidthLeft_beforeNonDigit() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 2, 4, SignStyle.NEVER);
        int newPos = pp.parse(context, "1-2", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOM), false);
    }

    public void test_parse_tooBig() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 10, SignStyle.NEVER);
        int newPos = pp.parse(context, "2147483648", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOM), false);
    }

    public void test_parse_tooSmall() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, 1, 10, SignStyle.NORMAL);
        int newPos = pp.parse(context, "-2147483649", 0);
        assertEquals(newPos, ~1);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOM), false);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="parse")
    Object[][] provider_dow() {
        return new Object[][] {
            // basics
            {"0", 1, 2, SignStyle.NEVER, 1, 0},
            {"1", 1, 2, SignStyle.NEVER, 1, 1},
            {"2", 1, 2, SignStyle.NEVER, 1, 2},
            {"3", 1, 2, SignStyle.NEVER, 1, 3},
            {"4", 1, 2, SignStyle.NEVER, 1, 4},
            {"5", 1, 2, SignStyle.NEVER, 1, 5},
            {"6", 1, 2, SignStyle.NEVER, 1, 6},
            {"7", 1, 2, SignStyle.NEVER, 1, 7},
            {"8", 1, 2, SignStyle.NEVER, 1, 8},
            {"9", 1, 2, SignStyle.NEVER, 1, 9},
            {"10", 1, 2, SignStyle.NEVER, 2, 10},
            {"100", 1, 2, SignStyle.NEVER, 2, 10},
            {"100", 1, 3, SignStyle.NEVER, 3, 100},
            
            // never
            {"0", 1, 2, SignStyle.NEVER, 1, 0},
            {"5", 1, 2, SignStyle.NEVER, 1, 5},
            {"50", 1, 2, SignStyle.NEVER, 2, 50},
            {"500", 1, 2, SignStyle.NEVER, 2, 50},
            {"-5", 1, 2, SignStyle.NEVER, ~0, null},
            {"-50", 1, 2, SignStyle.NEVER, ~0, null},
            {"-500", 1, 2, SignStyle.NEVER, ~0, null},
            {"-AAA", 1, 2, SignStyle.NEVER, ~0, null},
            {"+5", 1, 2, SignStyle.NEVER, ~0, null},
            {"+50", 1, 2, SignStyle.NEVER, ~0, null},
            {"+500", 1, 2, SignStyle.NEVER, ~0, null},
            {"+AAA", 1, 2, SignStyle.NEVER, ~0, null},
            
            // negative error
            {"0", 1, 2, SignStyle.NOT_NEGATIVE, 1, 0},
            {"5", 1, 2, SignStyle.NOT_NEGATIVE, 1, 5},
            {"50", 1, 2, SignStyle.NOT_NEGATIVE, 2, 50},
            {"500", 1, 2, SignStyle.NOT_NEGATIVE, 2, 50},
            {"-5", 1, 2, SignStyle.NOT_NEGATIVE, ~0, null},
            {"-50", 1, 2, SignStyle.NOT_NEGATIVE, ~0, null},
            {"-500", 1, 2, SignStyle.NOT_NEGATIVE, ~0, null},
            {"-AAA", 1, 2, SignStyle.NOT_NEGATIVE, ~0, null},
            {"+5", 1, 2, SignStyle.NOT_NEGATIVE, ~0, null},
            {"+50", 1, 2, SignStyle.NOT_NEGATIVE, ~0, null},
            {"+500", 1, 2, SignStyle.NOT_NEGATIVE, ~0, null},
            {"+AAA", 1, 2, SignStyle.NOT_NEGATIVE, ~0, null},
            
            // normal
            {"0", 1, 2, SignStyle.NORMAL, 1, 0},
            {"5", 1, 2, SignStyle.NORMAL, 1, 5},
            {"50", 1, 2, SignStyle.NORMAL, 2, 50},
            {"500", 1, 2, SignStyle.NORMAL, 2, 50},
            {"-5", 1, 2, SignStyle.NORMAL, 2, -5},
            {"-50", 1, 2, SignStyle.NORMAL, 3, -50},
            {"-500", 1, 2, SignStyle.NORMAL, 3, -50},
            {"-AAA", 1, 2, SignStyle.NORMAL, ~1, null},
            {"+5", 1, 2, SignStyle.NORMAL, ~0, null},
            {"+50", 1, 2, SignStyle.NORMAL, ~0, null},
            {"+500", 1, 2, SignStyle.NORMAL, ~0, null},
            {"+AAA", 1, 2, SignStyle.NORMAL, ~0, null},
            
            // always
            {"0", 1, 2, SignStyle.ALWAYS, ~0, null},
            {"5", 1, 2, SignStyle.ALWAYS, ~0, null},
            {"50", 1, 2, SignStyle.ALWAYS, ~0, null},
            {"500", 1, 2, SignStyle.ALWAYS, ~0, null},
            {"-5", 1, 2, SignStyle.ALWAYS, 2, -5},
            {"-50", 1, 2, SignStyle.ALWAYS, 3, -50},
            {"-500", 1, 2, SignStyle.ALWAYS, 3, -50},
            {"-AAA", 1, 2, SignStyle.ALWAYS, ~1, null},
            {"+5", 1, 2, SignStyle.ALWAYS, 2, 5},
            {"+50", 1, 2, SignStyle.ALWAYS, 3, 50},
            {"+500", 1, 2, SignStyle.ALWAYS, 3, 50},
            {"+AAA", 1, 2, SignStyle.ALWAYS, ~1, null},
            
            // exceeds pad
            {"0", 1, 2, SignStyle.EXCEEDS_PAD, 1, 0},
            {"5", 1, 2, SignStyle.EXCEEDS_PAD, 1, 5},
            {"50", 1, 2, SignStyle.EXCEEDS_PAD, ~0, null},
            {"500", 1, 2, SignStyle.EXCEEDS_PAD, ~0, null},
            {"-5", 1, 2, SignStyle.EXCEEDS_PAD, 2, -5},
            {"-50", 1, 2, SignStyle.EXCEEDS_PAD, 3, -50},
            {"-500", 1, 2, SignStyle.EXCEEDS_PAD, 3, -50},
            {"-AAA", 1, 2, SignStyle.EXCEEDS_PAD, ~1, null},
            {"+5", 1, 2, SignStyle.EXCEEDS_PAD, ~1, null},
            {"+50", 1, 2, SignStyle.EXCEEDS_PAD, 3, 50},
            {"+500", 1, 2, SignStyle.EXCEEDS_PAD, 3, 50},
            {"+AAA", 1, 2, SignStyle.EXCEEDS_PAD, ~1, null},
       };
    }

    @Test(dataProvider="parse") 
    public void test_parse(String input, int min, int max, SignStyle style, int parseLen, Integer parseVal) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        NumberPrinterParser pp = new NumberPrinterParser(RULE_DOM, min, max, style);
        int newPos = pp.parse(context, input, 0);
        assertEquals(newPos, parseLen);
        if (parseVal == null) {
            assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOM), false);
        } else {
            assertEquals(context.getFieldValueMapValue(RULE_DOM), parseVal.intValue());
        }
    }

}
