/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Iterator;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalMerger;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.Year;
import javax.time.calendar.YearMonth;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DateTimeFormatters.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDateTimeFormatters {

    @BeforeMethod
    public void setUp() {
    }

    @SuppressWarnings("unchecked")
    public void test_constructor() throws Exception {
        for (Constructor constructor : DateTimeFormatters.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullCalendrical() {
        DateTimeFormatters.isoDate().print((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoLocalDate")
    Object[][] provider_sample_isoLocalDate() {
        return new Object[][]{
                {2008, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                {2008, 6, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, 30, null, null, null, CalendricalPrintFieldException.class},
                
                {2008, 6, 30, null, null,                   "2008-06-30", null},
                {2008, 6, 30, "+01:00", null,               "2008-06-30", null},
                {2008, 6, 30, "+01:00", "Europe/Paris",     "2008-06-30", null},
                {2008, 6, 30, null, "Europe/Paris",         "2008-06-30", null},
                
                {123456, 6, 30, null, null,                 "+123456-06-30", null},
        };
    }

    @Test(dataProvider="sample_isoLocalDate")
    public void test_print_isoLocalDate(
            Integer year, Integer month, Integer day, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createDate(year, month, day);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoLocalDate().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoLocalDate().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoLocalDate")
    public void test_parse_isoLocalDate(
            Integer year, Integer month, Integer day, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createDate(year, month, day);
            // offset/zone not expected to be parsed
            assertParseMatch(DateTimeFormatters.isoLocalDate().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoOffsetDate")
    Object[][] provider_sample_isoOffsetDate() {
        return new Object[][]{
                {2008, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                {2008, 6, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, 30, null, null, null, CalendricalPrintFieldException.class},
                
                {2008, 6, 30, null, null,                   null, CalendricalPrintException.class},
                {2008, 6, 30, "+01:00", null,               "2008-06-30+01:00", null},
                {2008, 6, 30, "+01:00", "Europe/Paris",     "2008-06-30+01:00", null},
                {2008, 6, 30, null, "Europe/Paris",         null, CalendricalPrintException.class},
                
                {123456, 6, 30, "+01:00", null,             "+123456-06-30+01:00", null},
        };
    }

    @Test(dataProvider="sample_isoOffsetDate")
    public void test_print_isoOffsetDate(
            Integer year, Integer month, Integer day, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createDate(year, month, day);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoOffsetDate().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoOffsetDate().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoOffsetDate")
    public void test_parse_isoOffsetDate(
            Integer year, Integer month, Integer day, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createDate(year, month, day);
            buildCalendrical(expected, offsetId, null);  // zone not expected to be parsed
            assertParseMatch(DateTimeFormatters.isoOffsetDate().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoDate")
    Object[][] provider_sample_isoDate() {
        return new Object[][]{
                {2008, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                {2008, 6, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, 30, null, null, null, CalendricalPrintFieldException.class},
                
                {2008, 6, 30, null, null,                   "2008-06-30", null},
                {2008, 6, 30, "+01:00", null,               "2008-06-30+01:00", null},
                {2008, 6, 30, "+01:00", "Europe/Paris",     "2008-06-30+01:00[Europe/Paris]", null},
                {2008, 6, 30, null, "Europe/Paris",         "2008-06-30", null},
                
                {123456, 6, 30, "+01:00", "Europe/Paris",   "+123456-06-30+01:00[Europe/Paris]", null},
        };
    }

    @Test(dataProvider="sample_isoDate")
    public void test_print_isoDate(
            Integer year, Integer month, Integer day, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createDate(year, month, day);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoDate().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoDate().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoDate")
    public void test_parse_isoDate(
            Integer year, Integer month, Integer day, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createDate(year, month, day);
            if (offsetId != null) {
                expected.put(ZoneOffset.rule(), ZoneOffset.of(offsetId));
                if (zoneId != null) {
                    expected.put(TimeZone.rule(), TimeZone.of(zoneId));
                }
            }
            assertParseMatch(DateTimeFormatters.isoDate().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoLocalTime")
    Object[][] provider_sample_isoLocalTime() {
        return new Object[][]{
                {11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, 1, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                
                {11, 5, null, null, null, null,     "11:05", null},
                {11, 5, 30, null, null, null,       "11:05:30", null},
                {11, 5, 30, 500000000, null, null,  "11:05:30.5", null},
                {11, 5, 30, 1, null, null,          "11:05:30.000000001", null},
                
                {11, 5, null, null, "+01:00", null,     "11:05", null},
                {11, 5, 30, null, "+01:00", null,       "11:05:30", null},
                {11, 5, 30, 500000000, "+01:00", null,  "11:05:30.5", null},
                {11, 5, 30, 1, "+01:00", null,          "11:05:30.000000001", null},
                
                {11, 5, null, null, "+01:00", "Europe/Paris",       "11:05", null},
                {11, 5, 30, null, "+01:00", "Europe/Paris",         "11:05:30", null},
                {11, 5, 30, 500000000, "+01:00", "Europe/Paris",    "11:05:30.5", null},
                {11, 5, 30, 1, "+01:00", "Europe/Paris",            "11:05:30.000000001", null},
                
                {11, 5, null, null, null, "Europe/Paris",       "11:05", null},
                {11, 5, 30, null, null, "Europe/Paris",         "11:05:30", null},
                {11, 5, 30, 500000000, null, "Europe/Paris",    "11:05:30.5", null},
                {11, 5, 30, 1, null, "Europe/Paris",            "11:05:30.000000001", null},
        };
    }

    @Test(dataProvider="sample_isoLocalTime")
    public void test_print_isoLocalTime(
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createTime(hour, min, sec, nano);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoLocalTime().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoLocalTime().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoLocalTime")
    public void test_parse_isoLocalTime(
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createTime(hour, min, sec, nano);
            // offset/zone not expected to be parsed
            assertParseMatch(DateTimeFormatters.isoLocalTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoOffsetTime")
    Object[][] provider_sample_isoOffsetTime() {
        return new Object[][]{
                {11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, 1, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                
                {11, 5, null, null, null, null,     null, CalendricalPrintException.class},
                {11, 5, 30, null, null, null,       null, CalendricalPrintException.class},
                {11, 5, 30, 500000000, null, null,  null, CalendricalPrintException.class},
                {11, 5, 30, 1, null, null,          null, CalendricalPrintException.class},
                
                {11, 5, null, null, "+01:00", null,     "11:05+01:00", null},
                {11, 5, 30, null, "+01:00", null,       "11:05:30+01:00", null},
                {11, 5, 30, 500000000, "+01:00", null,  "11:05:30.5+01:00", null},
                {11, 5, 30, 1, "+01:00", null,          "11:05:30.000000001+01:00", null},
                
                {11, 5, null, null, "+01:00", "Europe/Paris",       "11:05+01:00", null},
                {11, 5, 30, null, "+01:00", "Europe/Paris",         "11:05:30+01:00", null},
                {11, 5, 30, 500000000, "+01:00", "Europe/Paris",    "11:05:30.5+01:00", null},
                {11, 5, 30, 1, "+01:00", "Europe/Paris",            "11:05:30.000000001+01:00", null},
                
                {11, 5, null, null, null, "Europe/Paris",       null, CalendricalPrintException.class},
                {11, 5, 30, null, null, "Europe/Paris",         null, CalendricalPrintException.class},
                {11, 5, 30, 500000000, null, "Europe/Paris",    null, CalendricalPrintException.class},
                {11, 5, 30, 1, null, "Europe/Paris",            null, CalendricalPrintException.class},
        };
    }

    @Test(dataProvider="sample_isoOffsetTime")
    public void test_print_isoOffsetTime(
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createTime(hour, min, sec, nano);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoOffsetTime().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoOffsetTime().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoOffsetTime")
    public void test_parse_isoOffsetTime(
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createTime(hour, min, sec, nano);
            buildCalendrical(expected, offsetId, null);  // zoneId is not expected from parse
            assertParseMatch(DateTimeFormatters.isoOffsetTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoTime")
    Object[][] provider_sample_isoTime() {
        return new Object[][]{
                {11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, 1, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                
                {11, 5, null, null, null, null,     "11:05", null},
                {11, 5, 30, null, null, null,       "11:05:30", null},
                {11, 5, 30, 500000000, null, null,  "11:05:30.5", null},
                {11, 5, 30, 1, null, null,          "11:05:30.000000001", null},
                
                {11, 5, null, null, "+01:00", null,     "11:05+01:00", null},
                {11, 5, 30, null, "+01:00", null,       "11:05:30+01:00", null},
                {11, 5, 30, 500000000, "+01:00", null,  "11:05:30.5+01:00", null},
                {11, 5, 30, 1, "+01:00", null,          "11:05:30.000000001+01:00", null},
                
                {11, 5, null, null, "+01:00", "Europe/Paris",       "11:05+01:00[Europe/Paris]", null},
                {11, 5, 30, null, "+01:00", "Europe/Paris",         "11:05:30+01:00[Europe/Paris]", null},
                {11, 5, 30, 500000000, "+01:00", "Europe/Paris",    "11:05:30.5+01:00[Europe/Paris]", null},
                {11, 5, 30, 1, "+01:00", "Europe/Paris",            "11:05:30.000000001+01:00[Europe/Paris]", null},
                
                {11, 5, null, null, null, "Europe/Paris",       "11:05", null},
                {11, 5, 30, null, null, "Europe/Paris",         "11:05:30", null},
                {11, 5, 30, 500000000, null, "Europe/Paris",    "11:05:30.5", null},
                {11, 5, 30, 1, null, "Europe/Paris",            "11:05:30.000000001", null},
        };
    }

    @Test(dataProvider="sample_isoTime")
    public void test_print_isoTime(
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createTime(hour, min, sec, nano);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoTime().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoTime().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoTime")
    public void test_parse_isoTime(
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createTime(hour, min, sec, nano);
            if (offsetId != null) {
                expected.put(ZoneOffset.rule(), ZoneOffset.of(offsetId));
                if (zoneId != null) {
                    expected.put(TimeZone.rule(), TimeZone.of(zoneId));
                }
            }
            assertParseMatch(DateTimeFormatters.isoTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoLocalDateTime")
    Object[][] provider_sample_isoLocalDateTime() {
        return new Object[][]{
                {2008, null, null, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, null, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, 11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                {2008, 6, 30, 11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, 6, 30, null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, 6, null, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, null, 30, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, 30, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                
                {2008, 6, 30, 11, 5, null, null, null, null,                    "2008-06-30T11:05", null},
                {2008, 6, 30, 11, 5, 30, null, null, null,                      "2008-06-30T11:05:30", null},
                {2008, 6, 30, 11, 5, 30, 500000000, null, null,                 "2008-06-30T11:05:30.5", null},
                {2008, 6, 30, 11, 5, 30, 1, null, null,                         "2008-06-30T11:05:30.000000001", null},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", null,                "2008-06-30T11:05", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", null,                  "2008-06-30T11:05:30", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", null,             "2008-06-30T11:05:30.5", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", null,                     "2008-06-30T11:05:30.000000001", null},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", "Europe/Paris",      "2008-06-30T11:05", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", "Europe/Paris",        "2008-06-30T11:05:30", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", "Europe/Paris",   "2008-06-30T11:05:30.5", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", "Europe/Paris",           "2008-06-30T11:05:30.000000001", null},
                
                {2008, 6, 30, 11, 5, null, null, null, "Europe/Paris",          "2008-06-30T11:05", null},
                {2008, 6, 30, 11, 5, 30, null, null, "Europe/Paris",            "2008-06-30T11:05:30", null},
                {2008, 6, 30, 11, 5, 30, 500000000, null, "Europe/Paris",       "2008-06-30T11:05:30.5", null},
                {2008, 6, 30, 11, 5, 30, 1, null, "Europe/Paris",               "2008-06-30T11:05:30.000000001", null},
                
                {123456, 6, 30, 11, 5, null, null, null, null,                  "+123456-06-30T11:05", null},
        };
    }

    @Test(dataProvider="sample_isoLocalDateTime")
    public void test_print_isoLocalDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createDateTime(year, month, day, hour, min, sec, nano);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoLocalDateTime().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoLocalDateTime().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoLocalDateTime")
    public void test_parse_isoLocalDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createDateTime(year, month, day, hour, min, sec, nano);
            assertParseMatch(DateTimeFormatters.isoLocalDateTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoOffsetDateTime")
    Object[][] provider_sample_isoOffsetDateTime() {
        return new Object[][]{
                {2008, null, null, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, null, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, 11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                {2008, 6, 30, 11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, 6, 30, null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, 6, null, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, null, 30, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, 30, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                
                {2008, 6, 30, 11, 5, null, null, null, null,                    null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, null, null, null,                      null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, null, null,                 null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 1, null, null,                         null, CalendricalPrintException.class},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", null,                "2008-06-30T11:05+01:00", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", null,                  "2008-06-30T11:05:30+01:00", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", null,             "2008-06-30T11:05:30.5+01:00", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", null,                     "2008-06-30T11:05:30.000000001+01:00", null},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", "Europe/Paris",      "2008-06-30T11:05+01:00", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", "Europe/Paris",        "2008-06-30T11:05:30+01:00", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", "Europe/Paris",   "2008-06-30T11:05:30.5+01:00", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", "Europe/Paris",           "2008-06-30T11:05:30.000000001+01:00", null},
                
                {2008, 6, 30, 11, 5, null, null, null, "Europe/Paris",          null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, null, null, "Europe/Paris",            null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, null, "Europe/Paris",       null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 1, null, "Europe/Paris",               null, CalendricalPrintException.class},
                
                {123456, 6, 30, 11, 5, null, null, "+01:00", null,              "+123456-06-30T11:05+01:00", null},
        };
    }

    @Test(dataProvider="sample_isoOffsetDateTime")
    public void test_print_isoOffsetDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createDateTime(year, month, day, hour, min, sec, nano);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoOffsetDateTime().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoOffsetDateTime().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoOffsetDateTime")
    public void test_parse_isoOffsetDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createDateTime(year, month, day, hour, min, sec, nano);
            buildCalendrical(expected, offsetId, null);  // zone not expected to be parsed
            assertParseMatch(DateTimeFormatters.isoOffsetDateTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoZonedDateTime")
    Object[][] provider_sample_isoZonedDateTime() {
        return new Object[][]{
                {2008, null, null, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, null, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, 11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                {2008, 6, 30, 11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, 6, 30, null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, 6, null, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, null, 30, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, 30, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                
                {2008, 6, 30, 11, 5, null, null, null, null,                    null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, null, null, null,                      null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, null, null,                 null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 1, null, null,                         null, CalendricalPrintException.class},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", null,                null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", null,                  null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", null,             null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", null,                     null, CalendricalPrintException.class},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", "Europe/Paris",      "2008-06-30T11:05+01:00[Europe/Paris]", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", "Europe/Paris",        "2008-06-30T11:05:30+01:00[Europe/Paris]", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", "Europe/Paris",   "2008-06-30T11:05:30.5+01:00[Europe/Paris]", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", "Europe/Paris",           "2008-06-30T11:05:30.000000001+01:00[Europe/Paris]", null},
                
                {2008, 6, 30, 11, 5, null, null, null, "Europe/Paris",          null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, null, null, "Europe/Paris",            null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, null, "Europe/Paris",       null, CalendricalPrintException.class},
                {2008, 6, 30, 11, 5, 30, 1, null, "Europe/Paris",               null, CalendricalPrintException.class},
                
                {123456, 6, 30, 11, 5, null, null, "+01:00", "Europe/Paris",    "+123456-06-30T11:05+01:00[Europe/Paris]", null},
        };
    }

    @Test(dataProvider="sample_isoZonedDateTime")
    public void test_print_isoZonedDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createDateTime(year, month, day, hour, min, sec, nano);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoZonedDateTime().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoZonedDateTime().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoZonedDateTime")
    public void test_parse_isoZonedDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createDateTime(year, month, day, hour, min, sec, nano);
            buildCalendrical(expected, offsetId, zoneId);
            assertParseMatch(DateTimeFormatters.isoZonedDateTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoDateTime")
    Object[][] provider_sample_isoDateTime() {
        return new Object[][]{
                {2008, null, null, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, null, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, 30, null, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, 11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, null, null, "+01:00", null, null, CalendricalPrintFieldException.class},
                {null, null, null, null, null, null, null, null, "Europe/Paris", null, CalendricalPrintFieldException.class},
                {2008, 6, 30, 11, null, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, 6, 30, null, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, 6, null, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {2008, null, 30, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                {null, 6, 30, 11, 5, null, null, null, null, null, CalendricalPrintFieldException.class},
                
                {2008, 6, 30, 11, 5, null, null, null, null,                    "2008-06-30T11:05", null},
                {2008, 6, 30, 11, 5, 30, null, null, null,                      "2008-06-30T11:05:30", null},
                {2008, 6, 30, 11, 5, 30, 500000000, null, null,                 "2008-06-30T11:05:30.5", null},
                {2008, 6, 30, 11, 5, 30, 1, null, null,                         "2008-06-30T11:05:30.000000001", null},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", null,                "2008-06-30T11:05+01:00", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", null,                  "2008-06-30T11:05:30+01:00", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", null,             "2008-06-30T11:05:30.5+01:00", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", null,                     "2008-06-30T11:05:30.000000001+01:00", null},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", "Europe/Paris",      "2008-06-30T11:05+01:00[Europe/Paris]", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", "Europe/Paris",        "2008-06-30T11:05:30+01:00[Europe/Paris]", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", "Europe/Paris",   "2008-06-30T11:05:30.5+01:00[Europe/Paris]", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", "Europe/Paris",           "2008-06-30T11:05:30.000000001+01:00[Europe/Paris]", null},
                
                {2008, 6, 30, 11, 5, null, null, null, "Europe/Paris",          "2008-06-30T11:05", null},
                {2008, 6, 30, 11, 5, 30, null, null, "Europe/Paris",            "2008-06-30T11:05:30", null},
                {2008, 6, 30, 11, 5, 30, 500000000, null, "Europe/Paris",       "2008-06-30T11:05:30.5", null},
                {2008, 6, 30, 11, 5, 30, 1, null, "Europe/Paris",               "2008-06-30T11:05:30.000000001", null},
                
                {123456, 6, 30, 11, 5, null, null, null, null,                  "+123456-06-30T11:05", null},
        };
    }

    @Test(dataProvider="sample_isoDateTime")
    public void test_print_isoDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        MockSimpleCalendrical test = createDateTime(year, month, day, hour, min, sec, nano);
        buildCalendrical(test, offsetId, zoneId);
        if (expectedEx == null) {
            assertEquals(DateTimeFormatters.isoDateTime().print(test), expected);
        } else {
            try {
                DateTimeFormatters.isoDateTime().print(test);
                fail();
            } catch (Exception ex) {
                assertEquals(ex.getClass(), expectedEx);
            }
        }
    }

    @Test(dataProvider="sample_isoDateTime")
    public void test_parse_isoDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String input, Class<?> invalid) {
        if (input != null) {
            MockSimpleCalendrical expected = createDateTime(year, month, day, hour, min, sec, nano);
            if (offsetId != null) {
                expected.put(ZoneOffset.rule(), ZoneOffset.of(offsetId));
                if (zoneId != null) {
                    expected.put(TimeZone.rule(), TimeZone.of(zoneId));
                }
            }
            assertParseMatch(DateTimeFormatters.isoDateTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_print_isoOrdinalDate() {
        Calendrical test = LocalDateTime.of(2008, 6, 3, 11, 5, 30);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155");
    }

    public void test_print_isoOrdinalDate_offset() {
        Calendrical test = OffsetDateTime.of(2008, 6, 3, 11, 5, 30, ZoneOffset.UTC);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155Z");
    }

    public void test_print_isoOrdinalDate_zoned() {
        Calendrical test = ZonedDateTime.from(LocalDateTime.of(2008, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155Z[UTC]");
    }

    public void test_print_isoOrdinalDate_zoned_largeYear() {
        Calendrical test = ZonedDateTime.from(LocalDateTime.of(123456, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "+123456-155Z[UTC]");
    }

    public void test_print_isoOrdinalDate_fields() {
        Calendrical test = new MockSimpleCalendrical(ISOChronology.yearRule(), 2008, ISOChronology.dayOfYearRule(), 231);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-231");
    }

    public void test_print_isoOrdinalDate_missingField() {
        try {
            Calendrical test = Year.of(2008);
            DateTimeFormatters.isoOrdinalDate().print(test);
            fail();
        } catch (CalendricalPrintFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfYearRule());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_isoOrdinalDate() {
        MockSimpleCalendrical expected = new MockSimpleCalendrical(ISOChronology.yearRule(), 2008, ISOChronology.dayOfYearRule(), 123);
        assertParseMatch(DateTimeFormatters.isoOrdinalDate().parse("2008-123"), expected);
    }

    public void test_parse_isoOrdinalDate_largeYear() {
        MockSimpleCalendrical expected = new MockSimpleCalendrical(ISOChronology.yearRule(), 123456, ISOChronology.dayOfYearRule(), 123);
        assertParseMatch(DateTimeFormatters.isoOrdinalDate().parse("+123456-123"), expected);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_print_basicIsoDate() {
        Calendrical test = LocalDateTime.of(2008, 6, 3, 11, 5, 30);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603");
    }

    public void test_print_basicIsoDate_offset() {
        Calendrical test = OffsetDateTime.of(2008, 6, 3, 11, 5, 30, ZoneOffset.UTC);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603Z");
    }

    public void test_print_basicIsoDate_zoned() {
        Calendrical test = ZonedDateTime.from(LocalDateTime.of(2008, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603Z[UTC]");
    }

    @Test(expectedExceptions=CalendricalPrintFieldException.class)
    public void test_print_basicIsoDate_largeYear() {
        try {
            Calendrical test = ZonedDateTime.from(LocalDateTime.of(123456, 6, 3, 11, 5, 30), TimeZone.UTC);
            DateTimeFormatters.basicIsoDate().print(test);
        } catch (CalendricalPrintFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.yearRule());
            throw ex;
        }
    }

    public void test_print_basicIsoDate_fields() {
        Calendrical test = LocalDate.of(2008, 6, 30);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080630");
    }

    public void test_print_basicIsoDate_missingField() {
        try {
            Calendrical test = YearMonth.of(2008, 6);
            DateTimeFormatters.basicIsoDate().print(test);
            fail();
        } catch (CalendricalPrintFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfMonthRule());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_basicIsoDate() {
        LocalDate expected = LocalDate.of(2008, 6, 3);
        assertEquals(DateTimeFormatters.basicIsoDate().parse("20080603", LocalDate.rule()), expected);
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void test_parse_basicIsoDate_largeYear() {
        try {
            LocalDate expected = LocalDate.of(123456, 6, 3);
            assertEquals(DateTimeFormatters.basicIsoDate().parse("+1234560603", LocalDate.rule()), expected);
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getErrorIndex(), 0);
            assertEquals(ex.getParsedString(), "+1234560603");
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="weekDate")
    Iterator<Object[]> weekDate() {
        return new Iterator<Object[]>() {
            private ZonedDateTime date = ZonedDateTime.from(LocalDateTime.of(2003, 12, 29, 11, 5, 30), TimeZone.UTC);
            private ZonedDateTime endDate = date.withDate(2005, 1, 2);
            private int week = 1;
            private int day = 1;

            public boolean hasNext() {
                return !date.isAfter(endDate);
            }
            public Object[] next() {
                StringBuilder sb = new StringBuilder("2004-W");
                if (week < 10) {
                    sb.append('0');
                }
                sb.append(week).append('-').append(day).append("Z[UTC]");
                Object[] ret = new Object[] {date, sb.toString()};
                date = date.plusDays(1);
                day += 1;
                if (day == 8) {
                    day = 1;
                    week++;
                }
                return ret;
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Test(dataProvider="weekDate")
    public void test_print_isoWeekDate(Calendrical test, String expected) {
        assertEquals(DateTimeFormatters.isoWeekDate().print(test), expected);
    }

    public void test_print_isoWeekDate_zoned_largeYear() {
        Calendrical test = ZonedDateTime.from(LocalDateTime.of(123456, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoWeekDate().print(test), "+123456-W23-2Z[UTC]");
    }

    public void test_print_isoWeekDate_fields() {
        MockSimpleCalendrical test = new MockSimpleCalendrical();
        test.put(ISOChronology.weekBasedYearRule(), 2004);
        test.put(ISOChronology.weekOfWeekBasedYearRule(), 5);
        test.put(ISOChronology.dayOfWeekRule(), DayOfWeek.TUESDAY);
        assertEquals(DateTimeFormatters.isoWeekDate().print(test), "2004-W05-2");
    }

    public void test_print_isoWeekDate_missingField() {
        try {
            Calendrical test = new MockSimpleCalendrical(ISOChronology.weekBasedYearRule(), 2004, ISOChronology.weekOfWeekBasedYearRule(), 1);
            DateTimeFormatters.isoWeekDate().print(test);
            fail();
        } catch (CalendricalPrintFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfWeekRule());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_weekDate() {
        MockSimpleCalendrical expected = new MockSimpleCalendrical();
        expected.put(ISOChronology.weekBasedYearRule(), 2004);
        expected.put(ISOChronology.weekOfWeekBasedYearRule(), 1);
        expected.put(ISOChronology.dayOfWeekRule(), DayOfWeek.MONDAY);
        assertParseMatch(DateTimeFormatters.isoWeekDate().parse("2004-W01-1"), expected);
    }

    public void test_parse_weekDate_largeYear() {
        MockSimpleCalendrical expected = new MockSimpleCalendrical();
        expected.put(ISOChronology.weekBasedYearRule(), 123456);
        expected.put(ISOChronology.weekOfWeekBasedYearRule(), 4);
        expected.put(ISOChronology.dayOfWeekRule(), DayOfWeek.FRIDAY);
        assertParseMatch(DateTimeFormatters.isoWeekDate().parse("+123456-W04-5"), expected);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_print_rfc1123() {
        Calendrical test = ZonedDateTime.from(LocalDateTime.of(2008, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.rfc1123().print(test), "Tue, 03 Jun 2008 11:05:30 Z");
    }

    public void test_print_rfc1123_missingField() {
        try {
            Calendrical test = YearMonth.of(2008, 6);
            DateTimeFormatters.rfc1123().print(test);
            fail();
        } catch (CalendricalPrintFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfWeekRule());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    private MockSimpleCalendrical createDate(Integer year, Integer month, Integer day) {
        MockSimpleCalendrical test = new MockSimpleCalendrical();
        if (year != null) {
            test.put(ISOChronology.yearRule(), year);
        }
        if (month != null) {
            test.put(ISOChronology.monthOfYearRule(), MonthOfYear.of(month));
        }
        if (day != null) {
            test.put(ISOChronology.dayOfMonthRule(), day);
        }
        return test;
    }

    private MockSimpleCalendrical createTime(Integer hour, Integer min, Integer sec, Integer nano) {
        MockSimpleCalendrical test = new MockSimpleCalendrical();
        if (hour != null) {
            test.put(ISOChronology.hourOfDayRule(), hour);
        }
        if (min != null) {
            test.put(ISOChronology.minuteOfHourRule(), min);
        }
        if (sec != null) {
            test.put(ISOChronology.secondOfMinuteRule(), sec);
        }
        if (nano != null) {
            test.put(ISOChronology.nanoOfSecondRule(), nano);
        }
        return test;
    }

    private MockSimpleCalendrical createDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano) {
        MockSimpleCalendrical test = new MockSimpleCalendrical();
        if (year != null) {
            test.put(ISOChronology.yearRule(), year);
        }
        if (month != null) {
            test.put(ISOChronology.monthOfYearRule(), MonthOfYear.of(month));
        }
        if (day != null) {
            test.put(ISOChronology.dayOfMonthRule(), day);
        }
        if (hour != null) {
            test.put(ISOChronology.hourOfDayRule(), hour);
        }
        if (min != null) {
            test.put(ISOChronology.minuteOfHourRule(), min);
        }
        if (sec != null) {
            test.put(ISOChronology.secondOfMinuteRule(), sec);
        }
        if (nano != null) {
            test.put(ISOChronology.nanoOfSecondRule(), nano);
        }
        return test;
    }

    private void buildCalendrical(MockSimpleCalendrical cal, String offsetId, String zoneId) {
        if (offsetId != null) {
            cal.put(ZoneOffset.rule(), ZoneOffset.of(offsetId));
        }
        if (zoneId != null) {
            cal.put(TimeZone.rule(), TimeZone.of(zoneId));
        }
    }

    private void assertParseMatch(CalendricalMerger merger, MockSimpleCalendrical expected) {
        for (CalendricalRule<?> rule : expected.rules()) {
            if (rule instanceof DateTimeFieldRule<?>) {
                assertEquals(((DateTimeFieldRule<?>) rule).getInt(expected), merger.getInputMap().get(rule), "Failed on rule: " + rule.getName());
            } else {
                assertEquals(merger.getInputMap().get(rule), expected.get(rule), "Failed on rule: " + rule.getName());
            }
        }
    }

}
