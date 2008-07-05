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
        int i = 1000000000;
        System.out.println(i);
        i = i * 4;
        System.out.println(i);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullCalendrical() {
        DateTimeFormatters.isoDate().print((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    public void test_print_isoDate() {
        Calendrical test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03");
    }

    public void test_print_isoDate_missingField() {
        try {
            Calendrical test = YearMonth.yearMonth(2008, 6).toFlexiDateTime();
            DateTimeFormatters.isoDate().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfMonth());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_print_isoOrdinalDate() {
        Calendrical test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoOrdinalDate().print(test), "2008-155");
    }

    public void test_print_isoOrdinalDate_missingField() {
        try {
            Calendrical test = Year.isoYear(2008).toFlexiDateTime();
            DateTimeFormatters.isoOrdinalDate().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfYear());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_print_basicIsoDate() {
        Calendrical test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.basicIsoDate().print(test), "20080603");
    }

    public void test_print_basicIsoDate_missingField() {
        try {
            Calendrical test = YearMonth.yearMonth(2008, 6).toFlexiDateTime();
            DateTimeFormatters.basicIsoDate().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfMonth());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_print_rfc2822() {
        Calendrical test = ZonedDateTime.dateTime(2008, 6, 3, 11, 5, 30, TimeZone.UTC);
        assertEquals(DateTimeFormatters.rfc2822().print(test), "Tue, 03 Jun 2008 11:05:30 Z");
    }

    public void test_print_rfc2822_missingField() {
        try {
            Calendrical test = YearMonth.yearMonth(2008, 6).toFlexiDateTime();
            DateTimeFormatters.rfc2822().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.INSTANCE.dayOfWeek());
            assertEquals(ex.getValue(), null);
        }
    }

}
