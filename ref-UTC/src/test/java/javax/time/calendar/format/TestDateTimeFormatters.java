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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Iterator;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDate;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.YearMonth;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.ZonedDateTime;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.WeekOfWeekyear;
import javax.time.calendar.field.Weekyear;
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
    public void test_print_isoDate() {
        CalendricalProvider test = LocalDate.date(2008, 6, 3);
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03");
    }

    public void test_print_isoDate_offset() {
        CalendricalProvider test = OffsetDate.date(2008, 6, 3, ZoneOffset.UTC);
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03Z");
    }

    public void test_print_isoDate_zoned() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-03Z[UTC]");
    }

    public void test_print_isoDate__zoned_largeYear() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(123456, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoDate().print(test), "+123456-06-03Z[UTC]");
    }

    public void test_print_isoDate_fields() {
        Calendrical test = new Calendrical();
        test.getFieldMap().put(Year.rule(), 2008);
        test.getFieldMap().put(MonthOfYear.rule(), 6);
        test.getFieldMap().put(DayOfMonth.rule(), 30);
        assertEquals(DateTimeFormatters.isoDate().print(test), "2008-06-30");
    }

    public void test_print_isoDate_missingField() {
        try {
            CalendricalProvider test = YearMonth.yearMonth(2008, 6).toCalendrical();
            DateTimeFormatters.isoDate().print(test);
            fail();
        } catch (CalendricalFormatFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.dayOfMonthRule());
            assertEquals(ex.getValue(), null);
        }
    }

    //-----------------------------------------------------------------------
    public void test_parse_isoDate() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.INSTANCE.year(), 2008);
        expected.getFieldMap().put(ISOChronology.INSTANCE.monthOfYear(), 6);
        expected.getFieldMap().put(ISOChronology.INSTANCE.dayOfMonth(), 3);
        assertEquals(DateTimeFormatters.isoDate().parse("2008-06-03"), expected);
    }

    public void test_parse_isoDate_largeYear() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.INSTANCE.year(), 123456);
        expected.getFieldMap().put(ISOChronology.INSTANCE.monthOfYear(), 6);
        expected.getFieldMap().put(ISOChronology.INSTANCE.dayOfMonth(), 3);
        assertEquals(DateTimeFormatters.isoDate().parse("+123456-06-03"), expected);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_print_isoTime() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoTime().print(test), "11:05:30");
    }

    public void test_print_isoTime_nanos1() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30, 1), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoTime().print(test), "11:05:30.000000001");
    }

    public void test_print_isoTime_nanos2() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30, 500000000), TimeZone.UTC);
        assertEquals(DateTimeFormatters.isoTime().print(test), "11:05:30.5");
    }

    public void test_print_isoTime_nanos3() {
        CalendricalProvider test = ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 3, 11, 5, 30, 123456000), TimeZone.UTC);
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
    public void test_parse_isoTime_nanos() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.INSTANCE.hourOfDay(), 11);
        expected.getFieldMap().put(ISOChronology.INSTANCE.minuteOfHour(), 5);
        expected.getFieldMap().put(ISOChronology.INSTANCE.secondOfMinute(), 30);
        expected.getFieldMap().put(ISOChronology.INSTANCE.nanoOfSecond(), 123456789);
        assertEquals(DateTimeFormatters.isoTime().parse("11:05:30.123456789"), expected);
    }

    public void test_parse_isoTime_millis() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.INSTANCE.hourOfDay(), 11);
        expected.getFieldMap().put(ISOChronology.INSTANCE.minuteOfHour(), 5);
        expected.getFieldMap().put(ISOChronology.INSTANCE.secondOfMinute(), 30);
        expected.getFieldMap().put(ISOChronology.INSTANCE.nanoOfSecond(), 123000000);
        assertEquals(DateTimeFormatters.isoTime().parse("11:05:30.123"), expected);
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
        test.getFieldMap().put(Weekyear.rule(), 2004);
        test.getFieldMap().put(WeekOfWeekyear.rule(), 5);
        test.getFieldMap().put(DayOfWeek.rule(), 2);
        assertEquals(DateTimeFormatters.isoWeekDate().print(test), "2004-W05-2");
    }

    public void test_print_isoWeekDate_missingField() {
        try {
            CalendricalProvider test = new Calendrical(Weekyear.rule(), 2004, WeekOfWeekyear.rule(), 1);
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
        expected.getFieldMap().put(ISOChronology.weekyearRule(), 2004);
        expected.getFieldMap().put(ISOChronology.weekOfWeekyearRule(), 1);
        expected.getFieldMap().put(ISOChronology.dayOfWeekRule(), 1);
        assertEquals(DateTimeFormatters.isoWeekDate().parse("2004-W01-1"), expected);
    }

    public void test_parse_weekDate_largeYear() {
        Calendrical expected = new Calendrical();
        expected.getFieldMap().put(ISOChronology.weekyearRule(), 123456);
        expected.getFieldMap().put(ISOChronology.weekOfWeekyearRule(), 4);
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

}
