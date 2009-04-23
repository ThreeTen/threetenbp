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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Iterator;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.YearMonth;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.ZonedDateTime;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.WeekBasedYear;
import javax.time.calendar.field.WeekOfWeekBasedYear;
import javax.time.calendar.field.Year;

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
        DateTimeFormatters.isoDate().print((CalendricalProvider) null);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoLocalDate")
    Object[][] provider_sample_isoLocalDate() {
        return new Object[][]{
                {2008, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                {2008, 6, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, 30, null, null, null, CalendricalFormatFieldException.class},
                
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
        Calendrical test = createDate(year, month, day);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createDate(year, month, day);
            assertEquals(DateTimeFormatters.isoLocalDate().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoOffsetDate")
    Object[][] provider_sample_isoOffsetDate() {
        return new Object[][]{
                {2008, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                {2008, 6, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, 30, null, null, null, CalendricalFormatFieldException.class},
                
                {2008, 6, 30, null, null,                   null, CalendricalFormatException.class},
                {2008, 6, 30, "+01:00", null,               "2008-06-30+01:00", null},
                {2008, 6, 30, "+01:00", "Europe/Paris",     "2008-06-30+01:00", null},
                {2008, 6, 30, null, "Europe/Paris",         null, CalendricalFormatException.class},
                
                {123456, 6, 30, "+01:00", null,             "+123456-06-30+01:00", null},
        };
    }

    @Test(dataProvider="sample_isoOffsetDate")
    public void test_print_isoOffsetDate(
            Integer year, Integer month, Integer day, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        Calendrical test = createDate(year, month, day);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createDate(year, month, day);
            if (offsetId != null) {
                expected.setOffset(ZoneOffset.zoneOffset(offsetId));
            }
            assertEquals(DateTimeFormatters.isoOffsetDate().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoDate")
    Object[][] provider_sample_isoDate() {
        return new Object[][]{
                {2008, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                {2008, 6, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, 30, null, null, null, CalendricalFormatFieldException.class},
                
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
        Calendrical test = createDate(year, month, day);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createDate(year, month, day);
            if (offsetId != null) {
                expected.setOffset(ZoneOffset.zoneOffset(offsetId));
                if (zoneId != null) {
                    expected.setZone(TimeZone.timeZone(zoneId));
                }
            }
            assertEquals(DateTimeFormatters.isoDate().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoLocalTime")
    Object[][] provider_sample_isoLocalTime() {
        return new Object[][]{
                {11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, 1, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                
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
        Calendrical test = createTime(hour, min, sec, nano);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createTime(hour, min, sec, nano);
            assertEquals(DateTimeFormatters.isoLocalTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoOffsetTime")
    Object[][] provider_sample_isoOffsetTime() {
        return new Object[][]{
                {11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, 1, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                
                {11, 5, null, null, null, null,     null, CalendricalFormatException.class},
                {11, 5, 30, null, null, null,       null, CalendricalFormatException.class},
                {11, 5, 30, 500000000, null, null,  null, CalendricalFormatException.class},
                {11, 5, 30, 1, null, null,          null, CalendricalFormatException.class},
                
                {11, 5, null, null, "+01:00", null,     "11:05+01:00", null},
                {11, 5, 30, null, "+01:00", null,       "11:05:30+01:00", null},
                {11, 5, 30, 500000000, "+01:00", null,  "11:05:30.5+01:00", null},
                {11, 5, 30, 1, "+01:00", null,          "11:05:30.000000001+01:00", null},
                
                {11, 5, null, null, "+01:00", "Europe/Paris",       "11:05+01:00", null},
                {11, 5, 30, null, "+01:00", "Europe/Paris",         "11:05:30+01:00", null},
                {11, 5, 30, 500000000, "+01:00", "Europe/Paris",    "11:05:30.5+01:00", null},
                {11, 5, 30, 1, "+01:00", "Europe/Paris",            "11:05:30.000000001+01:00", null},
                
                {11, 5, null, null, null, "Europe/Paris",       null, CalendricalFormatException.class},
                {11, 5, 30, null, null, "Europe/Paris",         null, CalendricalFormatException.class},
                {11, 5, 30, 500000000, null, "Europe/Paris",    null, CalendricalFormatException.class},
                {11, 5, 30, 1, null, "Europe/Paris",            null, CalendricalFormatException.class},
        };
    }

    @Test(dataProvider="sample_isoOffsetTime")
    public void test_print_isoOffsetTime(
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        Calendrical test = createTime(hour, min, sec, nano);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createTime(hour, min, sec, nano);
            if (offsetId != null) {
                expected.setOffset(ZoneOffset.zoneOffset(offsetId));
            }
            assertEquals(DateTimeFormatters.isoOffsetTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoTime")
    Object[][] provider_sample_isoTime() {
        return new Object[][]{
                {11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, 1, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                
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
        Calendrical test = createTime(hour, min, sec, nano);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createTime(hour, min, sec, nano);
            if (offsetId != null) {
                expected.setOffset(ZoneOffset.zoneOffset(offsetId));
                if (zoneId != null) {
                    expected.setZone(TimeZone.timeZone(zoneId));
                }
            }
            assertEquals(DateTimeFormatters.isoTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoLocalDateTime")
    Object[][] provider_sample_isoLocalDateTime() {
        return new Object[][]{
                {2008, null, null, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, null, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, 11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                {2008, 6, 30, 11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, 6, 30, null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, 6, null, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, null, 30, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, 30, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                
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
        Calendrical test = createDateTime(year, month, day, hour, min, sec, nano);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createDateTime(year, month, day, hour, min, sec, nano);
            assertEquals(DateTimeFormatters.isoLocalDateTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoOffsetDateTime")
    Object[][] provider_sample_isoOffsetDateTime() {
        return new Object[][]{
                {2008, null, null, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, null, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, 11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                {2008, 6, 30, 11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, 6, 30, null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, 6, null, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, null, 30, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, 30, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                
                {2008, 6, 30, 11, 5, null, null, null, null,                    null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, null, null, null,                      null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, null, null,                 null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 1, null, null,                         null, CalendricalFormatException.class},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", null,                "2008-06-30T11:05+01:00", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", null,                  "2008-06-30T11:05:30+01:00", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", null,             "2008-06-30T11:05:30.5+01:00", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", null,                     "2008-06-30T11:05:30.000000001+01:00", null},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", "Europe/Paris",      "2008-06-30T11:05+01:00", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", "Europe/Paris",        "2008-06-30T11:05:30+01:00", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", "Europe/Paris",   "2008-06-30T11:05:30.5+01:00", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", "Europe/Paris",           "2008-06-30T11:05:30.000000001+01:00", null},
                
                {2008, 6, 30, 11, 5, null, null, null, "Europe/Paris",          null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, null, null, "Europe/Paris",            null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, null, "Europe/Paris",       null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 1, null, "Europe/Paris",               null, CalendricalFormatException.class},
                
                {123456, 6, 30, 11, 5, null, null, "+01:00", null,              "+123456-06-30T11:05+01:00", null},
        };
    }

    @Test(dataProvider="sample_isoOffsetDateTime")
    public void test_print_isoOffsetDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        Calendrical test = createDateTime(year, month, day, hour, min, sec, nano);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createDateTime(year, month, day, hour, min, sec, nano);
            if (offsetId != null) {
                expected.setOffset(ZoneOffset.zoneOffset(offsetId));
            }
            assertEquals(DateTimeFormatters.isoOffsetDateTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoZonedDateTime")
    Object[][] provider_sample_isoZonedDateTime() {
        return new Object[][]{
                {2008, null, null, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, null, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, 11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                {2008, 6, 30, 11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, 6, 30, null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, 6, null, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, null, 30, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, 30, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                
                {2008, 6, 30, 11, 5, null, null, null, null,                    null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, null, null, null,                      null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, null, null,                 null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 1, null, null,                         null, CalendricalFormatException.class},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", null,                null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", null,                  null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", null,             null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", null,                     null, CalendricalFormatException.class},
                
                {2008, 6, 30, 11, 5, null, null, "+01:00", "Europe/Paris",      "2008-06-30T11:05+01:00[Europe/Paris]", null},
                {2008, 6, 30, 11, 5, 30, null, "+01:00", "Europe/Paris",        "2008-06-30T11:05:30+01:00[Europe/Paris]", null},
                {2008, 6, 30, 11, 5, 30, 500000000, "+01:00", "Europe/Paris",   "2008-06-30T11:05:30.5+01:00[Europe/Paris]", null},
                {2008, 6, 30, 11, 5, 30, 1, "+01:00", "Europe/Paris",           "2008-06-30T11:05:30.000000001+01:00[Europe/Paris]", null},
                
                {2008, 6, 30, 11, 5, null, null, null, "Europe/Paris",          null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, null, null, "Europe/Paris",            null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 500000000, null, "Europe/Paris",       null, CalendricalFormatException.class},
                {2008, 6, 30, 11, 5, 30, 1, null, "Europe/Paris",               null, CalendricalFormatException.class},
                
                {123456, 6, 30, 11, 5, null, null, "+01:00", "Europe/Paris",    "+123456-06-30T11:05+01:00[Europe/Paris]", null},
        };
    }

    @Test(dataProvider="sample_isoZonedDateTime")
    public void test_print_isoZonedDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano, String offsetId, String zoneId,
            String expected, Class<?> expectedEx) {
        Calendrical test = createDateTime(year, month, day, hour, min, sec, nano);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createDateTime(year, month, day, hour, min, sec, nano);
            if (offsetId != null) {
                expected.setOffset(ZoneOffset.zoneOffset(offsetId));
                if (zoneId != null) {
                    expected.setZone(TimeZone.timeZone(zoneId));
                }
            }
            assertEquals(DateTimeFormatters.isoZonedDateTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    @DataProvider(name="sample_isoDateTime")
    Object[][] provider_sample_isoDateTime() {
        return new Object[][]{
                {2008, null, null, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, null, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, 30, null, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, 11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, null, null, "+01:00", null, null, CalendricalFormatFieldException.class},
                {null, null, null, null, null, null, null, null, "Europe/Paris", null, CalendricalFormatFieldException.class},
                {2008, 6, 30, 11, null, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, 6, 30, null, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, 6, null, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {2008, null, 30, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                {null, 6, 30, 11, 5, null, null, null, null, null, CalendricalFormatFieldException.class},
                
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
        Calendrical test = createDateTime(year, month, day, hour, min, sec, nano);
        if (offsetId != null) {
            test.setOffset(ZoneOffset.zoneOffset(offsetId));
        }
        if (zoneId != null) {
            test.setZone(TimeZone.timeZone(zoneId));
        }
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
            Calendrical expected = createDateTime(year, month, day, hour, min, sec, nano);
            if (offsetId != null) {
                expected.setOffset(ZoneOffset.zoneOffset(offsetId));
                if (zoneId != null) {
                    expected.setZone(TimeZone.timeZone(zoneId));
                }
            }
            assertEquals(DateTimeFormatters.isoDateTime().parse(input), expected);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_print_isoOrdinalDate() {
        CalendricalProvider test = LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155");
    }

    public void test_print_isoOrdinalDate_offset() {
        CalendricalProvider test = OffsetDateTime.dateTime(2008, 6, 3, 11, 5, 30, ZoneOffset.UTC);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155Z");
    }

    public void test_print_isoOrdinalDate_zoned() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155Z[UTC]");
    }

    public void test_print_isoOrdinalDate_zoned_largeYear() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(123456, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "+123456-155Z[UTC]");
    }

    public void test_print_isoOrdinalDate_fields() {
        Calendrical test = new Calendrical();
        test.getFieldMap().put(Year.rule(), 2008);
        test.getFieldMap().put(DayOfYear.rule(), 231);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-231");
    }

    public void test_print_isoOrdinalDate_missingField() {
        try {
            CalendricalProvider test = Year.isoYear(2008).toCalendrical();
            DateTimeFormatters.isoOrdinalDate().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfYear());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_isoOrdinalDate() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.INSTANCE.year(), 2008);
        expected.getFieldMap().put(ISOChronology.INSTANCE.dayOfYear(), 123);
        assertEquals(DateTimeFormatters.isoOrdinalDate().parse("2008-123"), expected);
    }

    public void test_parse_isoOrdinalDate_largeYear() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.INSTANCE.year(), 123456);
        expected.getFieldMap().put(ISOChronology.INSTANCE.dayOfYear(), 123);
        assertEquals(DateTimeFormatters.isoOrdinalDate().parse("+123456-123"), expected);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_print_basicIsoDate() {
        CalendricalProvider test = LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603");
    }

    public void test_print_basicIsoDate_offset() {
        CalendricalProvider test = OffsetDateTime.dateTime(2008, 6, 3, 11, 5, 30, ZoneOffset.UTC);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603Z");
    }

    public void test_print_basicIsoDate_zoned() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603Z[UTC]");
    }

    @Test(expectedExceptions=CalendricalFormatFieldException.class)
    public void test_print_basicIsoDate_largeYear() {
        try {
            CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(123456, 6, 3, 11, 5, 30), TimeZone.UTC);
            DateTimeFormatters.basicIsoDate().print(test);
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.yearRule());
            throw ex;
        }
    }

    public void test_print_basicIsoDate_fields() {
        Calendrical test = new Calendrical();
        test.getFieldMap().put(Year.rule(), 2008);
        test.getFieldMap().put(MonthOfYear.rule(), 6);
        test.getFieldMap().put(DayOfMonth.rule(), 30);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080630");
    }

    public void test_print_basicIsoDate_missingField() {
        try {
            CalendricalProvider test = YearMonth.yearMonth(2008, 6).toCalendrical();
            DateTimeFormatters.basicIsoDate().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfMonth());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_basicIsoDate() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.INSTANCE.year(), 2008);
        expected.getFieldMap().put(ISOChronology.INSTANCE.monthOfYear(), 6);
        expected.getFieldMap().put(ISOChronology.INSTANCE.dayOfMonth(), 3);
        assertEquals(DateTimeFormatters.basicIsoDate().parse("20080603"), expected);
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void test_parse_basicIsoDate_largeYear() {
        try {
            Calendrical expected = new Calendrical();
            expected.getFieldMap().put(ISOChronology.INSTANCE.year(), 123456);
            expected.getFieldMap().put(ISOChronology.INSTANCE.monthOfYear(), 6);
            expected.getFieldMap().put(ISOChronology.INSTANCE.dayOfMonth(), 3);
            assertEquals(DateTimeFormatters.basicIsoDate().parse("+1234560603"), expected);
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
            private ZonedDateTime date = ZonedDateTime.dateTime(LocalDateTime.dateTime(2003, 12, 29, 11, 5, 30), TimeZone.UTC);
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
    public void test_print_isoWeekDate(CalendricalProvider test, String expected) {
        assertEquals(DateTimeFormatters.isoWeekDate().print(test), expected);
    }

    public void test_print_isoWeekDate_zoned_largeYear() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(123456, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoWeekDate().print(test), "+123456-W23-2Z[UTC]");
    }

    public void test_print_isoWeekDate_fields() {
        Calendrical test = new Calendrical();
        test.getFieldMap().put(WeekBasedYear.rule(), 2004);
        test.getFieldMap().put(WeekOfWeekBasedYear.rule(), 5);
        test.getFieldMap().put(DayOfWeek.rule(), 2);
        assertEquals(DateTimeFormatters.isoWeekDate().print(test), "2004-W05-2");
    }

    public void test_print_isoWeekDate_missingField() {
        try {
            CalendricalProvider test = new Calendrical(WeekBasedYear.rule(), 2004, WeekOfWeekBasedYear.rule(), 1);
            DateTimeFormatters.isoWeekDate().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfWeek());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_weekDate() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.weekBasedYearRule(), 2004);
        expected.getFieldMap().put(ISOChronology.weekOfWeekBasedYearRule(), 1);
        expected.getFieldMap().put(ISOChronology.dayOfWeekRule(), 1);
        assertEquals(DateTimeFormatters.isoWeekDate().parse("2004-W01-1"), expected);
    }

    public void test_parse_weekDate_largeYear() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.weekBasedYearRule(), 123456);
        expected.getFieldMap().put(ISOChronology.weekOfWeekBasedYearRule(), 4);
        expected.getFieldMap().put(ISOChronology.dayOfWeekRule(), 5);
        assertEquals(DateTimeFormatters.isoWeekDate().parse("+123456-W04-5"), expected);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_print_rfc1123() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.rfc1123().print(test), "Tue, 03 Jun 2008 11:05:30 Z");
    }

    public void test_print_rfc1123_missingField() {
        try {
            CalendricalProvider test = YearMonth.yearMonth(2008, 6).toCalendrical();
            DateTimeFormatters.rfc1123().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfWeek());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    private Calendrical createDate(Integer year, Integer month, Integer day) {
        Calendrical test = new Calendrical();
        if (year != null) {
            test.getFieldMap().put(ISOChronology.yearRule(), year);
        }
        if (month != null) {
            test.getFieldMap().put(ISOChronology.monthOfYearRule(), month);
        }
        if (day != null) {
            test.getFieldMap().put(ISOChronology.dayOfMonthRule(), day);
        }
        return test;
    }

    private Calendrical createTime(Integer hour, Integer min, Integer sec, Integer nano) {
        Calendrical test = new Calendrical();
        if (hour != null) {
            test.getFieldMap().put(ISOChronology.hourOfDayRule(), hour);
        }
        if (min != null) {
            test.getFieldMap().put(ISOChronology.minuteOfHourRule(), min);
        }
        if (sec != null) {
            test.getFieldMap().put(ISOChronology.secondOfMinuteRule(), sec);
        }
        if (nano != null) {
            test.getFieldMap().put(ISOChronology.nanoOfSecondRule(), nano);
        }
        return test;
    }

    private Calendrical createDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano) {
        Calendrical test = new Calendrical();
        if (year != null) {
            test.getFieldMap().put(ISOChronology.yearRule(), year);
        }
        if (month != null) {
            test.getFieldMap().put(ISOChronology.monthOfYearRule(), month);
        }
        if (day != null) {
            test.getFieldMap().put(ISOChronology.dayOfMonthRule(), day);
        }
        if (hour != null) {
            test.getFieldMap().put(ISOChronology.hourOfDayRule(), hour);
        }
        if (min != null) {
            test.getFieldMap().put(ISOChronology.minuteOfHourRule(), min);
        }
        if (sec != null) {
            test.getFieldMap().put(ISOChronology.secondOfMinuteRule(), sec);
        }
        if (nano != null) {
            test.getFieldMap().put(ISOChronology.nanoOfSecondRule(), nano);
        }
        return test;
    }

}
