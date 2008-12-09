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
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MockCalendricalProviderReturnsNull;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DateTimeFormatter.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDateTimeFormatter {

    private static final DateTimeFieldRule RULE_DOM = ISOChronology.dayOfMonthRule();
    private List<DateTimePrinter> printers;
    private List<DateTimeParser> parsers;
    private StringLiteralPrinterParser stringPP;
    private NumberPrinterParser numberPP;

    @BeforeMethod
    public void setUp() {
        printers = new ArrayList<DateTimePrinter>();
        parsers = new ArrayList<DateTimeParser>();
        stringPP = new StringLiteralPrinterParser("ONE");
        numberPP = new NumberPrinterParser(RULE_DOM, 1, 2, SignStyle.NOT_NEGATIVE);
        printers.add(stringPP);
        printers.add(numberPP);
        parsers.add(stringPP);
        parsers.add(numberPP);
    }

    //-----------------------------------------------------------------------
    public void test_constructor() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        assertEquals(test.isPrintSupported(), true);
        assertEquals(test.isParseSupported(), true);
        assertEquals(test.getLocale(), Locale.ENGLISH);
    }

    public void test_constructor_empty() throws Exception {
        printers.clear();
        parsers.clear();
        DateTimeFormatter test = new DateTimeFormatter(Locale.FRANCE, printers, parsers);
        assertEquals(test.isPrintSupported(), true);
        assertEquals(test.isParseSupported(), true);
        assertEquals(test.getLocale(), Locale.FRANCE);
    }

    public void test_constructor_cannotPrint() throws Exception {
        printers.set(1, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.GERMANY, printers, parsers);
        assertEquals(test.isPrintSupported(), false);
        assertEquals(test.isParseSupported(), true);
        assertEquals(test.getLocale(), Locale.GERMANY);
    }

    public void test_constructor_cannotParse() throws Exception {
        parsers.set(1, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.US, printers, parsers);
        assertEquals(test.isPrintSupported(), true);
        assertEquals(test.isParseSupported(), false);
        assertEquals(test.getLocale(), Locale.US);
    }

    public void test_constructor_cannotPrintParse() throws Exception {
        printers.set(0, null);
        parsers.set(1, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.UK, printers, parsers);
        assertEquals(test.isPrintSupported(), false);
        assertEquals(test.isParseSupported(), false);
        assertEquals(test.getLocale(), Locale.UK);
    }

    //-----------------------------------------------------------------------
    public void test_withLocale() throws Exception {
        DateTimeFormatter base = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        DateTimeFormatter test = base.withLocale(Locale.GERMAN);
        assertEquals(test.getLocale(), Locale.GERMAN);
    }

    public void test_withLocale_same() throws Exception {
        DateTimeFormatter base = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        DateTimeFormatter test = base.withLocale(Locale.ENGLISH);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withLocale_null() throws Exception {
        DateTimeFormatter base = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        base.withLocale((Locale) null);
    }

    //-----------------------------------------------------------------------
    public void test_print_Calendrical() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        String result = test.print(LocalDate.date(2008, 6, 30));
        assertEquals(result, "ONE30");
    }

    @Test(expectedExceptions=CalendricalFormatException.class)
    public void test_print_Calendrical_noSuchField() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        test.print(LocalTime.time(11, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_Calendrical_null() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        test.print((CalendricalProvider) null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_print_Calendrical_noPrinting() throws Exception {
        printers.set(0, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        test.print(LocalDate.date(2008, 6, 30));
    }

    //-----------------------------------------------------------------------
    public void test_print_CalendricalAppendable() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        StringBuilder buf = new StringBuilder();
        test.print(LocalDate.date(2008, 6, 30), buf);
        assertEquals(buf.toString(), "ONE30");
    }

    @Test(expectedExceptions=CalendricalFormatException.class)
    public void test_print_CalendricalAppendable_noSuchField() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        StringBuilder buf = new StringBuilder();
        test.print(LocalTime.time(11, 30), buf);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_CalendricalAppendable_nullCalendrical() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        StringBuilder buf = new StringBuilder();
        test.print((CalendricalProvider) null, buf);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_CalendricalAppendable_nullAppendable() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        test.print(LocalDate.date(2008, 6, 30), (Appendable) null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_print_CalendricalAppendable_noPrinting() throws Exception {
        printers.set(0, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        test.print(LocalDate.date(2008, 6, 30), new StringBuilder());
    }

    @Test(expectedExceptions=IOException.class)  // IOException
    public void test_print_CalendricalAppendable_ioError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        try {
            test.print(LocalDate.date(2008, 6, 30), new MockIOExceptionAppendable());
        } catch (CalendricalFormatException ex) {
            assertEquals(ex.getCause() instanceof IOException, true);
            ex.rethrowIOException();
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_String() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Calendrical result = test.parse("ONE30");
        assertEquals(result.toDateTimeFields().size(), 1);
        assertEquals(result.getValueInt(RULE_DOM), 30);
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void test_parse_String_parseError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        try {
            test.parse("ONEXXX");
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getMessage().endsWith(": ONEXXX"), true);
            assertEquals(ex.getParsedString(), "ONEXXX");
            assertEquals(ex.getErrorIndex(), 3);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void test_parse_String_parseErrorLongText() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        try {
            test.parse("ONEXXX6789012345678901234567890123456789");
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getMessage().endsWith(": ONEXXX67890123456789012345678901..."), true);
            assertEquals(ex.getParsedString(), "ONEXXX6789012345678901234567890123456789");
            assertEquals(ex.getErrorIndex(), 3);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_String_null() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        test.parse((String) null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_parse_String_noParsing() throws Exception {
        parsers.set(0, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        test.parse("ONE30");
    }

    //-----------------------------------------------------------------------
    public void test_parse_StringParsePosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        ParsePosition pos = new ParsePosition(0);
        Calendrical result = test.parse("ONE30XXX", pos);
        assertEquals(pos.getIndex(), 5);
        assertEquals(pos.getErrorIndex(), -1);
        assertEquals(result.toDateTimeFields().size(), 1);
        assertEquals(result.getValueInt(RULE_DOM), 30);
    }

    public void test_parse_StringParsePosition_parseError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        ParsePosition pos = new ParsePosition(0);
        Calendrical result = test.parse("ONEXXX", pos);
        assertEquals(pos.getIndex(), 0);  // TODO: is this right?
        assertEquals(pos.getErrorIndex(), 3);
        assertEquals(result, null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_StringParsePosition_nullString() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        ParsePosition pos = new ParsePosition(0);
        test.parse((String) null, pos);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_StringParsePosition_nullParsePosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        test.parse("ONE30", (ParsePosition) null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_parse_StringParsePosition_noParsing() throws Exception {
        parsers.set(0, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        test.parse("ONE30", new ParsePosition(0));
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_StringParsePosition_invalidPosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        ParsePosition pos = new ParsePosition(6);
        test.parse("ONE30", pos);
    }

    //-----------------------------------------------------------------------
    public void test_toFormat_format() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        String result = format.format(LocalDate.date(2008, 6, 30));
        assertEquals(result, "ONE30");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toFormat_format_null() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        format.format(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toFormat_format_badProvider() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        format.format(new MockCalendricalProviderReturnsNull());
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_toFormat_format_notCalendrical() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        format.format("Not a CalendricalProvider");
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_toFormat_format_noPrinting() throws Exception {
        printers.set(1, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        format.format(LocalDate.date(2008, 6, 30));
    }

    //-----------------------------------------------------------------------
    public void test_toFormat_parseObject_String() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        Calendrical result = (Calendrical) format.parseObject("ONE30");
        assertEquals(result.toDateTimeFields().size(), 1);
        assertEquals(result.getValueInt(RULE_DOM), 30);
    }

    @Test(expectedExceptions=ParseException.class)
    public void test_toFormat_parseObject_String_parseError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        try {
            format.parseObject("ONEXXX");
        } catch (ParseException ex) {
            assertEquals(ex.getMessage().endsWith(": ONEXXX"), true);
            assertEquals(ex.getErrorOffset(), 3);
            throw ex;
        }
    }

    @Test(expectedExceptions=ParseException.class)
    public void test_toFormat_parseObject_String_parseErrorLongText() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        try {
            format.parseObject("ONEXXX6789012345678901234567890123456789");
        } catch (ParseException ex) {
            assertEquals(ex.getMessage().endsWith(": ONEXXX67890123456789012345678901..."), true);
            assertEquals(ex.getErrorOffset(), 3);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toFormat_parseObject_String_null() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        format.parseObject((String) null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_toFormat_parseObject_String_noParsing() throws Exception {
        parsers.set(0, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        format.parseObject("ONE30");
    }

    //-----------------------------------------------------------------------
    public void test_toFormat_parseObject_StringParsePosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        ParsePosition pos = new ParsePosition(0);
        Calendrical result = (Calendrical) format.parseObject("ONE30XXX", pos);
        assertEquals(pos.getIndex(), 5);
        assertEquals(pos.getErrorIndex(), -1);
        assertEquals(result.toDateTimeFields().size(), 1);
        assertEquals(result.getValueInt(RULE_DOM), 30);
    }

    public void test_toFormat_parseObject_StringParsePosition_parseError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        ParsePosition pos = new ParsePosition(0);
        Calendrical result = (Calendrical) format.parseObject("ONEXXX", pos);
        assertEquals(pos.getIndex(), 0);  // TODO: is this right?
        assertEquals(pos.getErrorIndex(), 3);
        assertEquals(result, null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toFormat_parseObject_StringParsePosition_nullString() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        ParsePosition pos = new ParsePosition(0);
        format.parseObject((String) null, pos);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toFormat_parseObject_StringParsePosition_nullParsePosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        format.parseObject("ONE30", (ParsePosition) null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_toFormat_parseObject_StringParsePosition_noParsing() throws Exception {
        parsers.set(0, null);
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        Format format = test.toFormat();
        format.parseObject("ONE30", new ParsePosition(0));
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_toFormat_parseObject_StringParsePosition_invalidPosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, printers, parsers);
        ParsePosition pos = new ParsePosition(6);
        Format format = test.toFormat();
        format.parseObject("ONE30", pos);
    }

}
