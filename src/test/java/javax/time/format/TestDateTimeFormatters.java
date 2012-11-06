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
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.HOUR_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.MINUTE_OF_HOUR;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_SECOND;
import static javax.time.calendrical.LocalDateTimeField.SECOND_OF_MINUTE;
import static javax.time.calendrical.LocalDateTimeField.YEAR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.time.DateTimeException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.OffsetDateTime;
import javax.time.Year;
import javax.time.YearMonth;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.ZonedDateTime;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DateTimeFormatters.
 */
@Test
public class TestDateTimeFormatters {

    @BeforeMethod
    public void setUp() {
    }

    @Test(groups={"implementation"})
    @SuppressWarnings("rawtypes")
    public void test_constructor() throws Exception {
        for (Constructor constructor : DateTimeFormatters.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    private DateTimeBuilder createDate(Integer year, Integer month, Integer day) {
        DateTimeBuilder test = new DateTimeBuilder();
        if (year != null) {
            test.addFieldValue(YEAR, year);
        }
        if (month != null) {
            test.addFieldValue(MONTH_OF_YEAR, month);
        }
        if (day != null) {
            test.addFieldValue(DAY_OF_MONTH, day);
        }
        return test;
    }

    private DateTimeBuilder createTime(Integer hour, Integer min, Integer sec, Integer nano) {
        DateTimeBuilder test = new DateTimeBuilder();
        if (hour != null) {
            test.addFieldValue(HOUR_OF_DAY, hour);
        }
        if (min != null) {
            test.addFieldValue(MINUTE_OF_HOUR, min);
        }
        if (sec != null) {
            test.addFieldValue(SECOND_OF_MINUTE, sec);
        }
        if (nano != null) {
            test.addFieldValue(NANO_OF_SECOND, nano);
        }
        return test;
    }

    private DateTimeBuilder createDateTime(
            Integer year, Integer month, Integer day,
            Integer hour, Integer min, Integer sec, Integer nano) {
        DateTimeBuilder test = new DateTimeBuilder();
        if (year != null) {
            test.addFieldValue(YEAR, year);
        }
        if (month != null) {
            test.addFieldValue(MONTH_OF_YEAR, month);
        }
        if (day != null) {
            test.addFieldValue(DAY_OF_MONTH, day);
        }
        if (hour != null) {
            test.addFieldValue(HOUR_OF_DAY, hour);
        }
        if (min != null) {
            test.addFieldValue(MINUTE_OF_HOUR, min);
        }
        if (sec != null) {
            test.addFieldValue(SECOND_OF_MINUTE, sec);
        }
        if (nano != null) {
            test.addFieldValue(NANO_OF_SECOND, nano);
        }
        return test;
    }

    private void buildCalendrical(DateTimeBuilder cal, String offsetId, String zoneId) {
        if (offsetId != null) {
            cal.addCalendrical(ZoneOffset.of(offsetId));
        }
        if (zoneId != null) {
            cal.addCalendrical(ZoneId.of(zoneId));
        }
    }

    private void assertParseMatch(DateTimeBuilder parsed, DateTimeBuilder expected) {
        Map<DateTimeField, Long> parsedFVMap = parsed.getFieldValueMap();
        Map<DateTimeField, Long> expectedFVMap = expected.getFieldValueMap();
        assertEquals(parsedFVMap, expectedFVMap);
        
        List<Object> parsedCMap = parsed.getCalendricalList();
        List<Object> expectedCMap = expected.getCalendricalList();
        assertEquals(parsedCMap, expectedCMap);
    }

}
