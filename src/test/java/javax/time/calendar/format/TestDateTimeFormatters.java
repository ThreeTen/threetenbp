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

import javax.time.calendar.Calendrical;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.OffsetDate;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.OffsetTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.ZonedDateTime;

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
        DateTimeFormatters.isoDate().print((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    public void test_print_isoDate_LocalDate() {
        LocalDate test = LocalDate.date(2008, 6, 3);
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03");
    }

    public void test_print_isoDate_LocalDateTime() {
        LocalDateTime test = LocalDateTime.dateTime(2008, 6, 3, 12, 30);
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03");
    }

    public void test_print_isoDate_OffsetDate() {
        OffsetDate test = OffsetDate.date(2008, 6, 3, ZoneOffset.zoneOffset(2));
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03");
    }

    public void test_print_isoDate_OffsetDateTime() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 3, 12, 30, ZoneOffset.zoneOffset(2));
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03");
    }

    public void test_print_isoDate_ZonedDateTime() {
        ZonedDateTime test = ZonedDateTime.dateTime(2008, 6, 3, 12, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03");
    }

    @Test(expectedExceptions=CalendricalFormatFieldException.class)
    public void test_print_isoDate_LocalTime() {
        DateTimeFormatters.isoDate().print(LocalTime.time(12, 30));
    }

    @Test(expectedExceptions=CalendricalFormatFieldException.class)
    public void test_print_isoDate_OffsetTime() {
        DateTimeFormatters.isoDate().print(OffsetTime.time(12, 30, ZoneOffset.zoneOffset(2)));
    }

    //-----------------------------------------------------------------------
    public void test_print_isoOrdinalDate_LocalDate() {
        LocalDate test = LocalDate.date(2008, 6, 3);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155");
    }

    public void test_print_isoOrdinalDate_LocalDateTime() {
        LocalDateTime test = LocalDateTime.dateTime(2008, 6, 3, 12, 30);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155");
    }

    public void test_print_isoOrdinalDate_OffsetDate() {
        OffsetDate test = OffsetDate.date(2008, 6, 3, ZoneOffset.zoneOffset(2));
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155");
    }

    public void test_print_isoOrdinalDate_OffsetDateTime() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 3, 12, 30, ZoneOffset.zoneOffset(2));
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155");
    }

    public void test_print_isoOrdinalDate_ZonedDateTime() {
        ZonedDateTime test = ZonedDateTime.dateTime(2008, 6, 3, 12, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155");
    }

    //-----------------------------------------------------------------------
    public void test_print_basicIsoDate_LocalDate() {
        LocalDate test = LocalDate.date(2008, 6, 3);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603");
    }

    public void test_print_basicIsoDate_LocalDateTime() {
        LocalDateTime test = LocalDateTime.dateTime(2008, 6, 3, 12, 30);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603");
    }

    public void test_print_basicIsoDate_OffsetDate() {
        OffsetDate test = OffsetDate.date(2008, 6, 3, ZoneOffset.zoneOffset(2));
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603");
    }

    public void test_print_basicIsoDate_OffsetDateTime() {
        OffsetDateTime test = OffsetDateTime.dateTime(2008, 6, 3, 12, 30, ZoneOffset.zoneOffset(2));
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603");
    }

    public void test_print_basicIsoDate_ZonedDateTime() {
        ZonedDateTime test = ZonedDateTime.dateTime(2008, 6, 3, 12, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603");
    }

}
