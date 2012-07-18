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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.io.IOException;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.time.CalendricalException;
import javax.time.CalendricalParseException;
import javax.time.LocalDate;
import javax.time.LocalTime;
import javax.time.OffsetDate;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.format.DateTimeFormatterBuilder.CompositePrinterParser;
import javax.time.format.DateTimeFormatterBuilder.DateTimePrinterParser;
import javax.time.format.DateTimeFormatterBuilder.NumberPrinterParser;
import javax.time.format.DateTimeFormatterBuilder.StringLiteralPrinterParser;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DateTimeFormatter.
 */
@Test
public class TestDateTimeFormatter {

    private List<DateTimePrinterParser> printerParsers;
    private StringLiteralPrinterParser stringPP;
    private NumberPrinterParser numberPP;
    private CompositePrinterParser compPP;

    @BeforeMethod(groups={"tck"})
    public void setUp() {
        printerParsers = new ArrayList<DateTimePrinterParser>();
        stringPP = new StringLiteralPrinterParser("ONE");
        numberPP = new NumberPrinterParser(DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE);
        printerParsers.add(stringPP);
        printerParsers.add(numberPP);
        compPP = new CompositePrinterParser(printerParsers, false);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withLocale() throws Exception {
        DateTimeFormatter base = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        DateTimeFormatter test = base.withLocale(Locale.GERMAN);
        assertEquals(test.getLocale(), Locale.GERMAN);
    }

    @Test(groups={"implementation"})
    public void test_withLocale_same() throws Exception {
        DateTimeFormatter base = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        DateTimeFormatter test = base.withLocale(Locale.ENGLISH);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withLocale_null() throws Exception {
        DateTimeFormatter base = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        base.withLocale((Locale) null);
    }

    //-----------------------------------------------------------------------
    // print
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_print_Calendrical() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        String result = test.print(LocalDate.of(2008, 6, 30));
        assertEquals(result, "ONE30");
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_print_Calendrical_noSuchField() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.print(LocalTime.of(11, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_print_Calendrical_null() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.print((DateTime) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_print_CalendricalAppendable() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        StringBuilder buf = new StringBuilder();
        test.printTo(LocalDate.of(2008, 6, 30), buf);
        assertEquals(buf.toString(), "ONE30");
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_print_CalendricalAppendable_noSuchField() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        StringBuilder buf = new StringBuilder();
        test.printTo(LocalTime.of(11, 30), buf);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_print_CalendricalAppendable_nullCalendrical() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        StringBuilder buf = new StringBuilder();
        test.printTo((DateTime) null, buf);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_print_CalendricalAppendable_nullAppendable() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.printTo(LocalDate.of(2008, 6, 30), (Appendable) null);
    }

    @Test(expectedExceptions=IOException.class, groups={"tck"})  // IOException
    public void test_print_CalendricalAppendable_ioError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        try {
            test.printTo(LocalDate.of(2008, 6, 30), new MockIOExceptionAppendable());
        } catch (CalendricalPrintException ex) {
            assertEquals(ex.getCause() instanceof IOException, true);
            ex.rethrowIOException();
        }
    }

//    //-----------------------------------------------------------------------
//    // parse
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_parse_Rule_String() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        DateTimeField result = test.parse("ONE30", DAY_OF_MONTH);
//        assertEquals(result, DAY_OF_MONTH.field(30L));
//    }
//
//    @Test(groups={"tck"})
//    public void test_parse_Rule_CharSequence() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        DateTimeField result = test.parse(new StringBuilder("ONE30"), DAY_OF_MONTH);
//        assertEquals(result, DAY_OF_MONTH.field(30L));
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void test_parse_Rule_String_parseError() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        try {
//            test.parse("ONEXXX", DAY_OF_MONTH);
//        } catch (CalendricalParseException ex) {
//            assertEquals(ex.getMessage().contains("could not be parsed"), true);
//            assertEquals(ex.getMessage().contains("ONEXXX"), true);
//            assertEquals(ex.getParsedString(), "ONEXXX");
//            assertEquals(ex.getErrorIndex(), 3);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void test_parse_Rule_String_parseErrorLongText() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        try {
//            test.parse("ONEXXX67890123456789012345678901234567890123456789012345678901234567890123456789", DAY_OF_MONTH);
//        } catch (CalendricalParseException ex) {
//            assertEquals(ex.getMessage().contains("could not be parsed"), true);
//            assertEquals(ex.getMessage().contains("ONEXXX6789012345678901234567890123456789012345678901234567890123..."), true);
//            assertEquals(ex.getParsedString(), "ONEXXX67890123456789012345678901234567890123456789012345678901234567890123456789");
//            assertEquals(ex.getErrorIndex(), 3);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void test_parse_Rule_String_parseIncomplete() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        try {
//            test.parse("ONE30SomethingElse", DAY_OF_MONTH);
//        } catch (CalendricalParseException ex) {
//            assertEquals(ex.getMessage().contains("could not be parsed"), true);
//            assertEquals(ex.getMessage().contains("ONE30SomethingElse"), true);
//            assertEquals(ex.getParsedString(), "ONE30SomethingElse");
//            assertEquals(ex.getErrorIndex(), 5);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_parse_Rule_String_nullText() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        test.parse((String) null, DAY_OF_MONTH);
//    }
//
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_parse_Rule_String_nullRule() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.parse("30", (Class<?>) null);
    }

    //-----------------------------------------------------------------------
    // TODO
//    @Test(groups={"tck"})
//    public void test_parseBest_firstOption() throws Exception {
//        DateTimeFormatter test = DateTimeFormatters.pattern("yyyy-MM-dd[ZZZ]");
//        DateTimeCalendricalObject result = test.parseBest("2011-06-30+03:00", OffsetDate.class, LocalDate.class);
//        assertEquals(result, OffsetDate.of(2011, 6, 30, ZoneOffset.ofHours(3)));
//    }

    @Test(groups={"tck"})
    public void test_parseBest_secondOption() throws Exception {
        DateTimeFormatter test = DateTimeFormatters.pattern("yyyy-MM-dd[ZZZ]");
        DateTime result = test.parseBest("2011-06-30", OffsetDate.class, LocalDate.class);
        assertEquals(result, LocalDate.of(2011, 6, 30));
    }

    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
    public void test_parseBest_String_parseError() throws Exception {
        DateTimeFormatter test = DateTimeFormatters.pattern("yyyy-MM-dd[ZZZ]");
        try {
            test.parseBest("2011-06-XX", OffsetDate.class, LocalDate.class);
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getMessage().contains("could not be parsed"), true);
            assertEquals(ex.getMessage().contains("XX"), true);
            assertEquals(ex.getParsedString(), "2011-06-XX");
            assertEquals(ex.getErrorIndex(), 8);
            throw ex;
        }
    }

//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void test_parseBest_String_parseErrorLongText() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        try {
//            test.parseBest("ONEXXX67890123456789012345678901234567890123456789012345678901234567890123456789", DAY_OF_MONTH, MONTH_OF_YEAR);
//        } catch (CalendricalParseException ex) {
//            assertEquals(ex.getMessage().contains("could not be parsed"), true);
//            assertEquals(ex.getMessage().contains("ONEXXX6789012345678901234567890123456789012345678901234567890123..."), true);
//            assertEquals(ex.getParsedString(), "ONEXXX67890123456789012345678901234567890123456789012345678901234567890123456789");
//            assertEquals(ex.getErrorIndex(), 3);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void test_parseBest_String_parseIncomplete() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        try {
//            test.parseBest("ONE30SomethingElse", DAY_OF_MONTH, MONTH_OF_YEAR);
//        } catch (CalendricalParseException ex) {
//            assertEquals(ex.getMessage().contains("could not be parsed"), true);
//            assertEquals(ex.getMessage().contains("ONE30SomethingElse"), true);
//            assertEquals(ex.getParsedString(), "ONE30SomethingElse");
//            assertEquals(ex.getErrorIndex(), 5);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_parseBest_String_nullText() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        test.parseBest((String) null, DAY_OF_MONTH, MONTH_OF_YEAR);
//    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_parseBest_String_nullRules() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.parseBest("30", (Class<?>[]) null);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_parseBest_String_zeroRules() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.parseBest("30", new Class<?>[0]);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_parseBest_String_oneRule() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.parseBest("30", LocalDate.class);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_parseToBuilder_String() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        DateTimeBuilder result = test.parseToBuilder("ONE30");
        assertEquals(result.getFieldValueMap().size(), 1);
        assertEquals(result.getFieldValue(DAY_OF_MONTH), 30L);
        assertEquals(result.getCalendricalList().size(), 0);
    }

    @Test(groups={"tck"})
    public void test_parseToBuilder_CharSequence() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        DateTimeBuilder result = test.parseToBuilder(new StringBuilder("ONE30"));
        assertEquals(result.getFieldValueMap().size(), 1);
        assertEquals(result.getFieldValue(DAY_OF_MONTH), 30L);
        assertEquals(result.getCalendricalList().size(), 0);
    }

    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
    public void test_parseToBuilder_String_parseError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        try {
            test.parseToBuilder("ONEXXX");
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getMessage().contains("ONEXXX"), true);
            assertEquals(ex.getParsedString(), "ONEXXX");
            assertEquals(ex.getErrorIndex(), 3);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
    public void test_parseToBuilder_String_parseErrorLongText() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        try {
            test.parseToBuilder("ONEXXX67890123456789012345678901234567890123456789012345678901234567890123456789");
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getMessage().contains("ONEXXX6789012345678901234567890123456789012345678901234567890123..."), true);
            assertEquals(ex.getParsedString(), "ONEXXX67890123456789012345678901234567890123456789012345678901234567890123456789");
            assertEquals(ex.getErrorIndex(), 3);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
    public void test_parseToBuilder_String_parseIncomplete() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        try {
            test.parseToBuilder("ONE30SomethingElse");
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getMessage().contains("ONE30SomethingElse"), true);
            assertEquals(ex.getParsedString(), "ONE30SomethingElse");
            assertEquals(ex.getErrorIndex(), 5);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_parseToBuilder_String_null() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.parseToBuilder((String) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_parseToBuilder_StringParsePosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        ParsePosition pos = new ParsePosition(0);
        DateTimeBuilder result = test.parseToBuilder("ONE30XXX", pos);
        assertEquals(pos.getIndex(), 5);
        assertEquals(pos.getErrorIndex(), -1);
        assertEquals(result.getFieldValueMap().size(), 1);
        assertEquals(result.getFieldValueMap().get(DAY_OF_MONTH), Long.valueOf(30));
    }

    @Test(groups={"tck"})
    public void test_parseToBuilder_StringParsePosition_parseError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        ParsePosition pos = new ParsePosition(0);
        DateTimeBuilder result = test.parseToBuilder("ONEXXX", pos);
        assertEquals(pos.getIndex(), 0);  // TODO: is this right?
        assertEquals(pos.getErrorIndex(), 3);
        assertEquals(result, null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_parseToBuilder_StringParsePosition_nullString() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        ParsePosition pos = new ParsePosition(0);
        test.parseToBuilder((String) null, pos);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_parseToBuilder_StringParsePosition_nullParsePosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.parseToBuilder("ONE30", (ParsePosition) null);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class, groups={"tck"})
    public void test_parseToBuilder_StringParsePosition_invalidPosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        ParsePosition pos = new ParsePosition(6);
        test.parseToBuilder("ONE30", pos);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toFormat_format() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        String result = format.format(LocalDate.of(2008, 6, 30));
        assertEquals(result, "ONE30");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_toFormat_format_null() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        format.format(null);
    }

    @Test(expectedExceptions=IllegalArgumentException.class, groups={"tck"})
    public void test_toFormat_format_notCalendrical() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        format.format("Not a Calendrical");
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toFormat_parseObject_String() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        DateTimeBuilder result = (DateTimeBuilder) format.parseObject("ONE30");
        assertEquals(result.getFieldValueMap().size(), 1);
        assertEquals(result.getFieldValue(DAY_OF_MONTH), 30L);
    }

    @Test(expectedExceptions=ParseException.class, groups={"tck"})
    public void test_toFormat_parseObject_String_parseError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        try {
            format.parseObject("ONEXXX");
        } catch (ParseException ex) {
            assertEquals(ex.getMessage().contains("ONEXXX"), true);
            assertEquals(ex.getErrorOffset(), 3);
            throw ex;
        }
    }

    @Test(expectedExceptions=ParseException.class, groups={"tck"})
    public void test_toFormat_parseObject_String_parseErrorLongText() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        try {
            format.parseObject("ONEXXX67890123456789012345678901234567890123456789012345678901234567890123456789");
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getMessage().contains("ONEXXX6789012345678901234567890123456789012345678901234567890123..."), true);
            assertEquals(ex.getParsedString(), "ONEXXX67890123456789012345678901234567890123456789012345678901234567890123456789");
            assertEquals(ex.getErrorIndex(), 3);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_toFormat_parseObject_String_null() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        format.parseObject((String) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toFormat_parseObject_StringParsePosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        ParsePosition pos = new ParsePosition(0);
        DateTimeBuilder result = (DateTimeBuilder) format.parseObject("ONE30XXX", pos);
        assertEquals(pos.getIndex(), 5);
        assertEquals(pos.getErrorIndex(), -1);
        assertEquals(result.getFieldValueMap().size(), 1);
        assertEquals(result.getFieldValue(DAY_OF_MONTH), 30L);
    }

    @Test(groups={"tck"})
    public void test_toFormat_parseObject_StringParsePosition_parseError() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        ParsePosition pos = new ParsePosition(0);
        DateTime result = (DateTime) format.parseObject("ONEXXX", pos);
        assertEquals(pos.getIndex(), 0);  // TODO: is this right?
        assertEquals(pos.getErrorIndex(), 3);
        assertEquals(result, null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_toFormat_parseObject_StringParsePosition_nullString() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        ParsePosition pos = new ParsePosition(0);
        format.parseObject((String) null, pos);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_toFormat_parseObject_StringParsePosition_nullParsePosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        format.parseObject("ONE30", (ParsePosition) null);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class, groups={"tck"})
    public void test_toFormat_parseObject_StringParsePosition_invalidPosition() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        ParsePosition pos = new ParsePosition(6);
        Format format = test.toFormat();
        format.parseObject("ONE30", pos);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toFormat_Rule_format() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        Format format = test.toFormat();
        String result = format.format(LocalDate.of(2008, 6, 30));
        assertEquals(result, "ONE30");
    }

//    @Test(groups={"tck"})
//    public void test_toFormat_Rule_parseObject_String() throws Exception {
//        DateTimeFormatter test = new DateTimeFormatter(Locale.ENGLISH, DateTimeFormatSymbols.STANDARD, compPP);
//        Format format = test.toFormat(DAY_OF_MONTH);
//        DateTimeField result = (DateTimeField) format.parseObject("ONE30");
//        assertEquals(result, DAY_OF_MONTH.field(30L));
//    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_toFormat_Rule() throws Exception {
        DateTimeFormatter test = new DateTimeFormatter(compPP, Locale.ENGLISH, DateTimeFormatSymbols.STANDARD);
        test.toFormat(null);
    }

}
