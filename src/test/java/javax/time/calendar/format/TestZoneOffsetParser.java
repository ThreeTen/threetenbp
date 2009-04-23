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

import javax.time.calendar.ZoneOffset;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ZoneOffsetPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestZoneOffsetParser {

    private DateTimeParseContext context;

    @BeforeMethod
    public void setUp() {
        context = new DateTimeParseContext(DateTimeFormatSymbols.getInstance());
    }

    //-----------------------------------------------------------------------
    public void test_parse_nullContext() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        try {
            int result = pp.parse((DateTimeParseContext) null, "+01:00", 0);
            assertEquals(result, 6);
            assertEquals(context.toCalendrical().getFieldMap().size(), 0);
            assertEquals(context.toCalendrical().getOffset(), ZoneOffset.zoneOffset(1));
            // NPE is optional, but parse must still succeed
        } catch (NullPointerException ex) {
            // NPE is optional
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_nullText() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        pp.parse(context, (String) null, 0);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_negativePosition() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        pp.parse(context, "hello", -1);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_offEndPosition() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        pp.parse(context, "hello", 6);
    }

    //-----------------------------------------------------------------------
    public void test_parse_exactMatch_UTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        int result = pp.parse(context, "Z", 0);
        assertEquals(result, 1);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), ZoneOffset.UTC);
    }

    public void test_parse_startStringMatch_UTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        int result = pp.parse(context, "ZOTHER", 0);
        assertEquals(result, 1);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), ZoneOffset.UTC);
    }

    public void test_parse_midStringMatch_UTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        int result = pp.parse(context, "OTHERZOTHER", 5);
        assertEquals(result, 6);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), ZoneOffset.UTC);
    }

    public void test_parse_endStringMatch_UTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        int result = pp.parse(context, "OTHERZ", 5);
        assertEquals(result, 6);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), ZoneOffset.UTC);
    }

    //-----------------------------------------------------------------------
    public void test_parse_exactMatch_UTC_EmptyUTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", true, true);
        int result = pp.parse(context, "", 0);
        assertEquals(result, 0);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), ZoneOffset.UTC);
    }

    public void test_parse_startStringMatch_UTC_EmptyUTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", true, true);
        int result = pp.parse(context, "OTHER", 0);
        assertEquals(result, 0);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), ZoneOffset.UTC);
    }

    public void test_parse_midStringMatch_UTC_EmptyUTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", true, true);
        int result = pp.parse(context, "OTHEROTHER", 5);
        assertEquals(result, 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), ZoneOffset.UTC);
    }

    public void test_parse_endStringMatch_UTC_EmptyUTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", true, true);
        int result = pp.parse(context, "OTHER", 5);
        assertEquals(result, 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), ZoneOffset.UTC);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="offsets")
    Object[][] provider_offsets() {
        return new Object[][] {
            {true, true, "+00:00", ZoneOffset.UTC},
            {true, true, "-00:00", ZoneOffset.UTC},
            {true, true, "+01:00", ZoneOffset.zoneOffset(1, 0, 0)},
            {true, true, "+01:02", ZoneOffset.zoneOffset(1, 2, 0)},
            {true, true, "+01:59", ZoneOffset.zoneOffset(1, 59, 0)},
            {true, true, "+02:00", ZoneOffset.zoneOffset(2, 0, 0)},
            {true, true, "+18:00", ZoneOffset.zoneOffset(18, 0, 0)},
            {true, true, "-01:00", ZoneOffset.zoneOffset(-1, 0, 0)},
            {true, true, "-02:00", ZoneOffset.zoneOffset(-2, 0, 0)},
            {true, true, "-18:00", ZoneOffset.zoneOffset(-18, 0, 0)},
            
            {true, true, "+00:00:00", ZoneOffset.UTC},
            {true, true, "-00:00:00", ZoneOffset.UTC},
            {true, true, "+01:00:00", ZoneOffset.zoneOffset(1, 0, 0)},
            {true, true, "+01:02:03", ZoneOffset.zoneOffset(1, 2, 3)},
            {true, true, "+01:59:59", ZoneOffset.zoneOffset(1, 59, 59)},
            {true, true, "+02:00:00", ZoneOffset.zoneOffset(2, 0, 0)},
            {true, true, "+18:00:00", ZoneOffset.zoneOffset(18, 0, 0)},
            {true, true, "-01:00:00", ZoneOffset.zoneOffset(-1, 0, 0)},
            {true, true, "-02:00:00", ZoneOffset.zoneOffset(-2, 0, 0)},
            {true, true, "-18:00:00", ZoneOffset.zoneOffset(-18, 0, 0)},
        };
    }

    @Test(dataProvider="offsets")
    public void test_parse_exactMatch(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        int result = pp.parse(context, parse, 0);
        assertEquals(result, parse.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_startStringMatch(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        int result = pp.parse(context, parse + ":OTHER", 0);
        assertEquals(result, parse.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_midStringMatch(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        int result = pp.parse(context, "OTHER" + parse + ":OTHER", 5);
        assertEquals(result, parse.length() + 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_endStringMatch(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", true, true);
        int result = pp.parse(context, "OTHER" + parse, 5);
        assertEquals(result, parse.length() + 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_exactMatch_EmptyUTC(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", true, true);
        int result = pp.parse(context, parse, 0);
        assertEquals(result, parse.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_startStringMatch_EmptyUTC(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", true, true);
        int result = pp.parse(context, parse + ":OTHER", 0);
        assertEquals(result, parse.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_midStringMatch_EmptyUTC(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", true, true);
        int result = pp.parse(context, "OTHER" + parse + ":OTHER", 5);
        assertEquals(result, parse.length() + 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_endStringMatch_EmptyUTC(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", true, true);
        int result = pp.parse(context, "OTHER" + parse, 5);
        assertEquals(result, parse.length() + 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="offsetsNoColon")
    Object[][] provider_offsetsNoColon() {
        return new Object[][] {
            {true, true, "+0000", ZoneOffset.UTC},
            {true, true, "-0000", ZoneOffset.UTC},
            {true, true, "+0100", ZoneOffset.zoneOffset(1, 0, 0)},
            {true, true, "+0102", ZoneOffset.zoneOffset(1, 2, 0)},
            {true, true, "+0159", ZoneOffset.zoneOffset(1, 59, 0)},
            {true, true, "+0200", ZoneOffset.zoneOffset(2, 0, 0)},
            {true, true, "+1800", ZoneOffset.zoneOffset(18, 0, 0)},
            {true, true, "-0100", ZoneOffset.zoneOffset(-1, 0, 0)},
            {true, true, "-0200", ZoneOffset.zoneOffset(-2, 0, 0)},
            {true, true, "-1800", ZoneOffset.zoneOffset(-18, 0, 0)},
            
            {true, true, "+000000", ZoneOffset.UTC},
            {true, true, "-000000", ZoneOffset.UTC},
            {true, true, "+010000", ZoneOffset.zoneOffset(1, 0, 0)},
            {true, true, "+010203", ZoneOffset.zoneOffset(1, 2, 3)},
            {true, true, "+015959", ZoneOffset.zoneOffset(1, 59, 59)},
            {true, true, "+020000", ZoneOffset.zoneOffset(2, 0, 0)},
            {true, true, "+180000", ZoneOffset.zoneOffset(18, 0, 0)},
            {true, true, "-010000", ZoneOffset.zoneOffset(-1, 0, 0)},
            {true, true, "-020000", ZoneOffset.zoneOffset(-2, 0, 0)},
            {true, true, "-180000", ZoneOffset.zoneOffset(-18, 0, 0)},
        };
    }

    @Test(dataProvider="offsetsNoColon")
    public void test_parse_noColon_exactMatch(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", false, true);
        int result = pp.parse(context, parse, 0);
        assertEquals(result, parse.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsetsNoColon")
    public void test_parse_noColon_startStringMatch(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", false, true);
        int result = pp.parse(context, parse + ":OTHER", 0);
        assertEquals(result, parse.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsetsNoColon")
    public void test_parse_noColon_midStringMatch(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", false, true);
        int result = pp.parse(context, "OTHER" + parse + ":OTHER", 5);
        assertEquals(result, parse.length() + 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsetsNoColon")
    public void test_parse_noColon_endStringMatch(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", false, true);
        int result = pp.parse(context, "OTHER" + parse, 5);
        assertEquals(result, parse.length() + 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsetsNoColon")
    public void test_parse_noColon_exactMatch_EmptyUTC(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", false, true);
        int result = pp.parse(context, parse, 0);
        assertEquals(result, parse.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsetsNoColon")
    public void test_parse_noColon_startStringMatch_EmptyUTC(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", false, true);
        int result = pp.parse(context, parse + ":OTHER", 0);
        assertEquals(result, parse.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsetsNoColon")
    public void test_parse_noColon_midStringMatch_EmptyUTC(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", false, true);
        int result = pp.parse(context, "OTHER" + parse + ":OTHER", 5);
        assertEquals(result, parse.length() + 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

    @Test(dataProvider="offsetsNoColon")
    public void test_parse_noColon_endStringMatch_EmptyUTC(boolean colon, boolean seconds, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", false, true);
        int result = pp.parse(context, "OTHER" + parse, 5);
        assertEquals(result, parse.length() + 5);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getOffset(), expected);
    }

}
