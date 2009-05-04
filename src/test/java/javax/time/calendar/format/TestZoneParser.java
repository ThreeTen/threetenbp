/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Set;

import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;
import javax.time.calendar.zone.ZoneRulesGroup;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ZonePrinterParser.
 */
@Test
public class TestZoneParser {

    private static final String AMERICA_DENVER = "America/Denver";
    private static final TimeZone TIME_ZONE_DENVER = TimeZone.timeZone(AMERICA_DENVER);
    private DateTimeParseContext context;

    @BeforeMethod
    public void setUp() {
        context = new DateTimeParseContext(DateTimeFormatSymbols.getInstance());
    }

    //-----------------------------------------------------------------------
    public void test_parse_nullContext() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        try {
            int result = pp.parse((DateTimeParseContext) null, AMERICA_DENVER, 0);
            assertEquals(result, AMERICA_DENVER.length());
            assertEquals(context.toCalendrical().getFieldMap().size(), 0);
            assertEquals(context.toCalendrical().getOffset(), ZoneOffset.zoneOffset(1));
            // NPE is optional, but parse must still succeed
        } catch (NullPointerException ex) {
            // NPE is optional
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_nullText() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        pp.parse(context, (String) null, 0);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_negativePosition() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        pp.parse(context, "hello", -1);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_parse_offEndPosition() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        pp.parse(context, "hello", 6);
    }

    //-----------------------------------------------------------------------
    public void test_parse_exactMatch_Denver() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, AMERICA_DENVER, 0);
        assertEquals(result, AMERICA_DENVER.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getZone(), TIME_ZONE_DENVER);
    }

    public void test_parse_startStringMatch_Denver() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, AMERICA_DENVER + "OTHER", 0);
        assertEquals(result, AMERICA_DENVER.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getZone(), TIME_ZONE_DENVER);
    }

    public void test_parse_midStringMatch_Denver() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, "OTHER" + AMERICA_DENVER + "OTHER", 5);
        assertEquals(result, 5 + AMERICA_DENVER.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getZone(), TIME_ZONE_DENVER);
    }

    public void test_parse_endStringMatch_Denver() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, "OTHER" + AMERICA_DENVER, 5);
        assertEquals(result, 5+ AMERICA_DENVER.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getZone(), TIME_ZONE_DENVER);
    }

    public void test_parse_partialMatch() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, "OTHERAmerica/Bogusville", 5);
        assertEquals(result, -6);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getZone(), null);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="zones")
    Object[][] populateTestData() {
        Set<String> ids = ZoneRulesGroup.getParsableIDs();
        Object[][] rtnval = new Object[ids.size()][];
        int i = 0;
        for (String id : ids) {
            rtnval[i++] = new Object[] { id, TimeZone.timeZone(id) };
        }
        return rtnval;
    }

    @Test(dataProvider="zones")
    public void test_parse_exactMatch(String parse, TimeZone expected) throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, parse, 0);
        assertEquals(context.toCalendrical().getZone(), expected);
        assertEquals(result, parse.length());
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
    }

    //-----------------------------------------------------------------------
    public void test_parse_endStringMatch_utc() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, "OTHERUTC", 5);
        assertEquals(result, 8);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getZone(), TimeZone.UTC);
    }

    public void test_parse_endStringMatch_utc_plus1() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, "OTHERUTC+01:00", 5);
        assertEquals(result, 14);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getZone(), TimeZone.timeZone("UTC+01:00"));
    }

    //-----------------------------------------------------------------------
    public void test_parse_midStringMatch_utc() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, "OTHERUTCOTHER", 5);
        assertEquals(result, 8);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getZone(), TimeZone.UTC);
    }

    public void test_parse_midStringMatch_utc_plus1() throws Exception {
        ZonePrinterParser pp = new ZonePrinterParser();
        int result = pp.parse(context, "OTHERUTC+01:00OTHER", 5);
        assertEquals(result, 14);
        assertEquals(context.toCalendrical().getFieldMap().size(), 0);
        assertEquals(context.toCalendrical().getZone(), TimeZone.timeZone("UTC+01:00"));
    }

    //-----------------------------------------------------------------------
    public void test_toString_id() {
        ZonePrinterParser pp = new ZonePrinterParser();
        assertEquals(pp.toString(), "ZoneId()");
    }

    public void test_toString_text() {
        ZonePrinterParser pp = new ZonePrinterParser(TextStyle.FULL);
        assertEquals(pp.toString(), "ZoneText(FULL)");
    }

}
