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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import javax.time.ZoneOffset;
import javax.time.format.DateTimeFormatterBuilder.ZoneOffsetPrinterParser;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ZoneOffsetPrinterParser.
 */
@Test(groups={"implementation"})
public class TestZoneOffsetParser extends AbstractTestPrinterParser {

    //-----------------------------------------------------------------------
    @DataProvider(name="error")
    Object[][] data_error() {
        return new Object[][] {
            {new ZoneOffsetPrinterParser("Z", "+HH:MM:ss"), "hello", -1, IndexOutOfBoundsException.class},
            {new ZoneOffsetPrinterParser("Z", "+HH:MM:ss"), "hello", 6, IndexOutOfBoundsException.class},
        };
    }

    @Test(dataProvider="error")
    public void test_parse_error(ZoneOffsetPrinterParser pp, String text, int pos, Class<?> expected) {
        try {
            pp.parse(parseContext, text, pos);
        } catch (RuntimeException ex) {
            assertTrue(expected.isInstance(ex));
            assertEquals(parseContext.getParsed().size(), 0);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_exactMatch_UTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", "+HH:MM:ss");
        int result = pp.parse(parseContext, "Z", 0);
        assertEquals(result, 1);
        assertParsed(ZoneOffset.UTC);
    }

    public void test_parse_startStringMatch_UTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", "+HH:MM:ss");
        int result = pp.parse(parseContext, "ZOTHER", 0);
        assertEquals(result, 1);
        assertParsed(ZoneOffset.UTC);
    }

    public void test_parse_midStringMatch_UTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", "+HH:MM:ss");
        int result = pp.parse(parseContext, "OTHERZOTHER", 5);
        assertEquals(result, 6);
        assertParsed(ZoneOffset.UTC);
    }

    public void test_parse_endStringMatch_UTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", "+HH:MM:ss");
        int result = pp.parse(parseContext, "OTHERZ", 5);
        assertEquals(result, 6);
        assertParsed(ZoneOffset.UTC);
    }

    //-----------------------------------------------------------------------
    public void test_parse_exactMatch_UTC_EmptyUTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", "+HH:MM:ss");
        int result = pp.parse(parseContext, "", 0);
        assertEquals(result, 0);
        assertParsed(ZoneOffset.UTC);
    }

    public void test_parse_startStringMatch_UTC_EmptyUTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", "+HH:MM:ss");
        int result = pp.parse(parseContext, "OTHER", 0);
        assertEquals(result, 0);
        assertParsed(ZoneOffset.UTC);
    }

    public void test_parse_midStringMatch_UTC_EmptyUTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", "+HH:MM:ss");
        int result = pp.parse(parseContext, "OTHEROTHER", 5);
        assertEquals(result, 5);
        assertParsed(ZoneOffset.UTC);
    }

    public void test_parse_endStringMatch_UTC_EmptyUTC() throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", "+HH:MM:ss");
        int result = pp.parse(parseContext, "OTHER", 5);
        assertEquals(result, 5);
        assertParsed(ZoneOffset.UTC);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="offsets")
    Object[][] provider_offsets() {
        return new Object[][] {
            {"+HH", "+00", ZoneOffset.UTC},
            {"+HH", "-00", ZoneOffset.UTC},
            {"+HH", "+01", ZoneOffset.ofHours(1)},
            {"+HH", "-01", ZoneOffset.ofHours(-1)},
            
            {"+HHMM", "+0000", ZoneOffset.UTC},
            {"+HHMM", "-0000", ZoneOffset.UTC},
            {"+HHMM", "+0102", ZoneOffset.ofHoursMinutes(1, 2)},
            {"+HHMM", "-0102", ZoneOffset.ofHoursMinutes(-1, -2)},
            
            {"+HH:MM", "+00:00", ZoneOffset.UTC},
            {"+HH:MM", "-00:00", ZoneOffset.UTC},
            {"+HH:MM", "+01:02", ZoneOffset.ofHoursMinutes(1, 2)},
            {"+HH:MM", "-01:02", ZoneOffset.ofHoursMinutes(-1, -2)},
            
            {"+HHMMss", "+0000", ZoneOffset.UTC},
            {"+HHMMss", "-0000", ZoneOffset.UTC},
            {"+HHMMss", "+0100", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)},
            {"+HHMMss", "+0159", ZoneOffset.ofHoursMinutesSeconds(1, 59, 0)},
            {"+HHMMss", "+0200", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)},
            {"+HHMMss", "+1800", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)},
            {"+HHMMss", "+010215", ZoneOffset.ofHoursMinutesSeconds(1, 2, 15)},
            {"+HHMMss", "-0100", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)},
            {"+HHMMss", "-0200", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)},
            {"+HHMMss", "-1800", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)},
            
            {"+HHMMss", "+000000", ZoneOffset.UTC},
            {"+HHMMss", "-000000", ZoneOffset.UTC},
            {"+HHMMss", "+010000", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)},
            {"+HHMMss", "+010203", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)},
            {"+HHMMss", "+015959", ZoneOffset.ofHoursMinutesSeconds(1, 59, 59)},
            {"+HHMMss", "+020000", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)},
            {"+HHMMss", "+180000", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)},
            {"+HHMMss", "-010000", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)},
            {"+HHMMss", "-020000", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)},
            {"+HHMMss", "-180000", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)},
            
            {"+HH:MM:ss", "+00:00", ZoneOffset.UTC},
            {"+HH:MM:ss", "-00:00", ZoneOffset.UTC},
            {"+HH:MM:ss", "+01:00", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)},
            {"+HH:MM:ss", "+01:02", ZoneOffset.ofHoursMinutesSeconds(1, 2, 0)},
            {"+HH:MM:ss", "+01:59", ZoneOffset.ofHoursMinutesSeconds(1, 59, 0)},
            {"+HH:MM:ss", "+02:00", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)},
            {"+HH:MM:ss", "+18:00", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)},
            {"+HH:MM:ss", "+01:02:15", ZoneOffset.ofHoursMinutesSeconds(1, 2, 15)},
            {"+HH:MM:ss", "-01:00", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)},
            {"+HH:MM:ss", "-02:00", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)},
            {"+HH:MM:ss", "-18:00", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)},
            
            {"+HH:MM:ss", "+00:00:00", ZoneOffset.UTC},
            {"+HH:MM:ss", "-00:00:00", ZoneOffset.UTC},
            {"+HH:MM:ss", "+01:00:00", ZoneOffset.ofHoursMinutesSeconds(1, 0, 0)},
            {"+HH:MM:ss", "+01:02:03", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)},
            {"+HH:MM:ss", "+01:59:59", ZoneOffset.ofHoursMinutesSeconds(1, 59, 59)},
            {"+HH:MM:ss", "+02:00:00", ZoneOffset.ofHoursMinutesSeconds(2, 0, 0)},
            {"+HH:MM:ss", "+18:00:00", ZoneOffset.ofHoursMinutesSeconds(18, 0, 0)},
            {"+HH:MM:ss", "-01:00:00", ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0)},
            {"+HH:MM:ss", "-02:00:00", ZoneOffset.ofHoursMinutesSeconds(-2, 0, 0)},
            {"+HH:MM:ss", "-18:00:00", ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0)},
            
            {"+HHMMSS", "+000000", ZoneOffset.UTC},
            {"+HHMMSS", "-000000", ZoneOffset.UTC},
            {"+HHMMSS", "+010203", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)},
            {"+HHMMSS", "-010203", ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3)},
            
            {"+HH:MM:SS", "+00:00:00", ZoneOffset.UTC},
            {"+HH:MM:SS", "-00:00:00", ZoneOffset.UTC},
            {"+HH:MM:SS", "+01:02:03", ZoneOffset.ofHoursMinutesSeconds(1, 2, 3)},
            {"+HH:MM:SS", "-01:02:03", ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3)},
        };
    }

    @Test(dataProvider="offsets")
    public void test_parse_exactMatch(String pattern, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", pattern);
        int result = pp.parse(parseContext, parse, 0);
        assertEquals(result, parse.length());
        assertParsed(expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_startStringMatch(String pattern, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", pattern);
        int result = pp.parse(parseContext, parse + ":OTHER", 0);
        assertEquals(result, parse.length());
        assertParsed(expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_midStringMatch(String pattern, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", pattern);
        int result = pp.parse(parseContext, "OTHER" + parse + ":OTHER", 5);
        assertEquals(result, parse.length() + 5);
        assertParsed(expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_endStringMatch(String pattern, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", pattern);
        int result = pp.parse(parseContext, "OTHER" + parse, 5);
        assertEquals(result, parse.length() + 5);
        assertParsed(expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_exactMatch_EmptyUTC(String pattern, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", pattern);
        int result = pp.parse(parseContext, parse, 0);
        assertEquals(result, parse.length());
        assertParsed(expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_startStringMatch_EmptyUTC(String pattern, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", pattern);
        int result = pp.parse(parseContext, parse + ":OTHER", 0);
        assertEquals(result, parse.length());
        assertParsed(expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_midStringMatch_EmptyUTC(String pattern, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", pattern);
        int result = pp.parse(parseContext, "OTHER" + parse + ":OTHER", 5);
        assertEquals(result, parse.length() + 5);
        assertParsed(expected);
    }

    @Test(dataProvider="offsets")
    public void test_parse_endStringMatch_EmptyUTC(String pattern, String parse, ZoneOffset expected) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("", pattern);
        int result = pp.parse(parseContext, "OTHER" + parse, 5);
        assertEquals(result, parse.length() + 5);
        assertParsed(expected);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="badOffsets")
    Object[][] provider_badOffsets() {
        return new Object[][] {
            {"+HH", "+1", ~0},
            {"+HH", "-1", ~0},
            {"+HH", "01", ~0},
            {"+HH", "01", ~0},
            {"+HH", "+AA", ~0},
            {"+HH", "+19", ~0},
            
            {"+HHMM", "+1", ~0},
            {"+HHMM", "+01", ~0},
            {"+HHMM", "+001", ~0},
            {"+HHMM", "0102", ~0},
            {"+HHMM", "+01:02", ~0},
            {"+HHMM", "+AAAA", ~0},
            {"+HHMM", "+1801", ~0},
            
            {"+HH:MM", "+1", ~0},
            {"+HH:MM", "+01", ~0},
            {"+HH:MM", "+0:01", ~0},
            {"+HH:MM", "+00:1", ~0},
            {"+HH:MM", "+0:1", ~0},
            {"+HH:MM", "+:", ~0},
            {"+HH:MM", "01:02", ~0},
            {"+HH:MM", "+0102", ~0},
            {"+HH:MM", "+AA:AA", ~0},
            {"+HH:MM", "+18:01", ~0},
            
            {"+HHMMss", "+1", ~0},
            {"+HHMMss", "+01", ~0},
            {"+HHMMss", "+001", ~0},
            {"+HHMMss", "0102", ~0},
            {"+HHMMss", "+01:02", ~0},
            {"+HHMMss", "+AAAA", ~0},
            {"+HHMMss", "+1801", ~0},
            {"+HHMMss", "+180103", ~0},
            
            {"+HH:MM:ss", "+1", ~0},
            {"+HH:MM:ss", "+01", ~0},
            {"+HH:MM:ss", "+0:01", ~0},
            {"+HH:MM:ss", "+00:1", ~0},
            {"+HH:MM:ss", "+0:1", ~0},
            {"+HH:MM:ss", "+:", ~0},
            {"+HH:MM:ss", "01:02", ~0},
            {"+HH:MM:ss", "+0102", ~0},
            {"+HH:MM:ss", "+AA:AA", ~0},
            {"+HH:MM:ss", "+18:01", ~0},
            {"+HH:MM:ss", "+18:01:03", ~0},
            
            {"+HHMMSS", "+1", ~0},
            {"+HHMMSS", "+01", ~0},
            {"+HHMMSS", "+001", ~0},
            {"+HHMMSS", "0102", ~0},
            {"+HHMMSS", "+01:02", ~0},
            {"+HHMMSS", "+AAAA", ~0},
            {"+HHMMSS", "+1801", ~0},
            {"+HHMMSS", "+180103", ~0},
            
            {"+HH:MM:SS", "+1", ~0},
            {"+HH:MM:SS", "+01", ~0},
            {"+HH:MM:SS", "+0:01", ~0},
            {"+HH:MM:SS", "+00:1", ~0},
            {"+HH:MM:SS", "+0:1", ~0},
            {"+HH:MM:SS", "+:", ~0},
            {"+HH:MM:SS", "01:02", ~0},
            {"+HH:MM:SS", "+0102", ~0},
            {"+HH:MM:SS", "+AA:AA", ~0},
            {"+HH:MM:SS", "+18:01", ~0},
            {"+HH:MM:SS", "+18:01:03", ~0},
        };
    }

    @Test(dataProvider="badOffsets")
    public void test_parse_invalid(String pattern, String parse, int expectedPosition) throws Exception {
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", pattern);
        int result = pp.parse(parseContext, parse, 0);
        assertEquals(result, expectedPosition);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_parse_caseSensitiveUTC_matchedCase() throws Exception {
        parseContext.setCaseSensitive(true);
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", "+HH:MM:ss");
        int result = pp.parse(parseContext, "Z", 0);
        assertEquals(result, 1);
        assertParsed(ZoneOffset.UTC);
    }

    public void test_parse_caseSensitiveUTC_unmatchedCase() throws Exception {
        parseContext.setCaseSensitive(true);
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", "+HH:MM:ss");
        int result = pp.parse(parseContext, "z", 0);
        assertEquals(result, ~0);
        assertParsed(null);
    }

    public void test_parse_caseInsensitiveUTC_matchedCase() throws Exception {
        parseContext.setCaseSensitive(false);
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", "+HH:MM:ss");
        int result = pp.parse(parseContext, "Z", 0);
        assertEquals(result, 1);
        assertParsed(ZoneOffset.UTC);
    }

    public void test_parse_caseInsensitiveUTC_unmatchedCase() throws Exception {
        parseContext.setCaseSensitive(false);
        ZoneOffsetPrinterParser pp = new ZoneOffsetPrinterParser("Z", "+HH:MM:ss");
        int result = pp.parse(parseContext, "z", 0);
        assertEquals(result, 1);
        assertParsed(ZoneOffset.UTC);
    }

    private void assertParsed(ZoneOffset expectedOffset) {
        assertEquals(parseContext.getParsed().size(), expectedOffset == null ? 0 : 1);
        assertEquals(parseContext.getParsed(ZoneOffset.class), expectedOffset);
    }

}
