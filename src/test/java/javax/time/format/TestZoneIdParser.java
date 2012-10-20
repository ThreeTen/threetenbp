/*
 * Copyright (c) 2009-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.time.ZoneId;
import javax.time.format.DateTimeFormatterBuilder.ZoneIdPrinterParser;
import javax.time.zone.ZoneRulesGroup;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ZonePrinterParser.
 */
@Test(groups={"implementation"})
public class TestZoneIdParser extends AbstractTestPrinterParser {

    private static final String AMERICA_DENVER = "America/Denver";
    private static final ZoneId TIME_ZONE_DENVER = ZoneId.of(AMERICA_DENVER);

    //-----------------------------------------------------------------------
    @DataProvider(name="error")
    Object[][] data_error() {
        return new Object[][] {
            {new ZoneIdPrinterParser(null), "hello", -1, IndexOutOfBoundsException.class},
            {new ZoneIdPrinterParser(null), "hello", 6, IndexOutOfBoundsException.class},
        };
    }

    @Test(dataProvider="error")
    public void test_parse_error(ZoneIdPrinterParser pp, String text, int pos, Class<?> expected) {
        try {
            pp.parse(parseContext, text, pos);
        } catch (RuntimeException ex) {
            assertTrue(expected.isInstance(ex));
            assertEquals(parseContext.getParsed().size(), 0);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_exactMatch_Denver() throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, AMERICA_DENVER, 0);
        assertEquals(result, AMERICA_DENVER.length());
        assertParsed(TIME_ZONE_DENVER);
    }

    public void test_parse_startStringMatch_Denver() throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, AMERICA_DENVER + "OTHER", 0);
        assertEquals(result, AMERICA_DENVER.length());
        assertParsed(TIME_ZONE_DENVER);
    }

    public void test_parse_midStringMatch_Denver() throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, "OTHER" + AMERICA_DENVER + "OTHER", 5);
        assertEquals(result, 5 + AMERICA_DENVER.length());
        assertParsed(TIME_ZONE_DENVER);
    }

    public void test_parse_endStringMatch_Denver() throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, "OTHER" + AMERICA_DENVER, 5);
        assertEquals(result, 5+ AMERICA_DENVER.length());
        assertParsed(TIME_ZONE_DENVER);
    }

    public void test_parse_partialMatch() throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, "OTHERAmerica/Bogusville", 5);
        assertEquals(result, -6);
        assertParsed(null);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="zones")
    Object[][] populateTestData() {
        Set<String> ids = ZoneRulesGroup.getParsableIDs();
        Object[][] rtnval = new Object[ids.size()][];
        int i = 0;
        for (String id : ids) {
            rtnval[i++] = new Object[] { id, ZoneId.of(id) };
        }
        return rtnval;
    }

    @Test(dataProvider="zones")
    public void test_parse_exactMatch(String parse, ZoneId expected) throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, parse, 0);
        assertEquals(result, parse.length());
        assertParsed(expected);
    }

    //-----------------------------------------------------------------------
    public void test_parse_endStringMatch_utc() throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, "OTHERUTC", 5);
        assertEquals(result, 8);
        assertParsed(ZoneId.UTC);
    }

    public void test_parse_endStringMatch_utc_plus1() throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, "OTHERUTC+01:00", 5);
        assertEquals(result, 14);
        assertParsed(ZoneId.of("UTC+01:00"));
    }

    //-----------------------------------------------------------------------
    public void test_parse_midStringMatch_utc() throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, "OTHERUTCOTHER", 5);
        assertEquals(result, 8);
        assertParsed(ZoneId.UTC);
    }

    public void test_parse_midStringMatch_utc_plus1() throws Exception {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        int result = pp.parse(parseContext, "OTHERUTC+01:00OTHER", 5);
        assertEquals(result, 14);
        assertParsed(ZoneId.of("UTC+01:00"));
    }

    //-----------------------------------------------------------------------
    public void test_toString_id() {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(null);
        assertEquals(pp.toString(), "ZoneId()");
    }

    public void test_toString_text() {
        ZoneIdPrinterParser pp = new ZoneIdPrinterParser(TextStyle.FULL);
        assertEquals(pp.toString(), "ZoneText(FULL)");
    }

    private void assertParsed(ZoneId expectedZone) {
        assertEquals(parseContext.getParsed().size(), expectedZone == null ? 0 : 1);
        assertEquals(parseContext.getParsed(ZoneId.class), expectedZone);
    }

}
