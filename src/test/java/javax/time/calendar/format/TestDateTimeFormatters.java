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

import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.TimeZone;
import javax.time.calendar.YearMonth;
import javax.time.calendar.ZonedDateTime;
import javax.time.calendar.field.Year;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test DateTimeFormatters.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDateTimeFormatters {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullCalendrical() {
        DateTimeFormatters.isoDate().print((CalendricalProvider) null);
    }

    //-----------------------------------------------------------------------
    public void test_print_isoDate() {
        CalendricalProvider test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03");
    }

    public void test_print_isoDate_missingField() {
        try {
            CalendricalProvider test = YearMonth.yearMonth(2008, 6).toCalendrical();
            DateTimeFormatters.isoDate().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfMonth());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_print_isoTime() {
        CalendricalProvider test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoTime().print(test), "11:05:30");
    }

    public void test_print_isoTime_nanos1() {
        CalendricalProvider test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, 1, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoTime().print(test), "11:05:30.000000001");
    }

    public void test_print_isoTime_nanos2() {
        CalendricalProvider test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, 500000000, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoTime().print(test), "11:05:30.5");
    }

    public void test_print_isoTime_nanos3() {
        CalendricalProvider test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, 123456000, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoTime().print(test), "11:05:30.123456");
    }

    public void test_print_isoTime_missingField() {
        try {
            CalendricalProvider test = YearMonth.yearMonth(2008, 6).toCalendrical();
            DateTimeFormatters.isoTime().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.hourOfDay());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_print_isoOrdinalDate() {
        CalendricalProvider test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155");
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
    public void test_print_basicIsoDate() {
        CalendricalProvider test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603");
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
    public void test_print_rfc1123() {
        CalendricalProvider test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, TimeZone.UTC);
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

}
