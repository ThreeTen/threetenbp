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
package javax.time.calendar;

import static org.testng.Assert.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.Instant;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.Year;

import org.testng.annotations.Test;

/**
 * Test OffsetDate creation.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestOffsetDateTime_creation {

    private static final Year YEAR_2007 = Year.isoYear(2007);
    private static final DayOfMonth DAY_15 = DayOfMonth.dayOfMonth(15);
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.zoneOffset(1);
    private static final ZoneOffset OFFSET_MAX = ZoneOffset.zoneOffset(18);
    private static final ZoneOffset OFFSET_MIN = ZoneOffset.zoneOffset(-18);

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(2007, 7, 15, OFFSET_PONE);
        assertTrue(test instanceof Calendrical);
        assertTrue(test instanceof Serializable);
        assertTrue(test instanceof Comparable);
    }

    public void test_immutable() {
        Class<OffsetDateTime> cls = OffsetDateTime.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    public void test_factory_dateMidnight_Year_MonthOfYear_DayOfMonth() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(YEAR_2007, MonthOfYear.JULY, DAY_15, OFFSET_PONE);
        assertEquals(test.getYear(), YEAR_2007);
        assertEquals(test.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(test.getDayOfMonth(), DAY_15);
        assertEquals(test.getOffset(), OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_Year_MonthOfYear_DayOfMonth_dayTooLow() {
        OffsetDateTime.dateMidnight(YEAR_2007, MonthOfYear.JANUARY, (DayOfMonth) null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_Year_MonthOfYear_DayOfMonth_nullMonth() {
        OffsetDateTime.dateMidnight(YEAR_2007, (MonthOfYear) null, DAY_15, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_Year_MonthOfYear_DayOfMonth_yearTooLow() {
        OffsetDateTime.dateMidnight((Year) null, MonthOfYear.JANUARY, DAY_15, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_Year_MonthOfYear_DayOfMonth_nullOffset() {
        OffsetDateTime.dateMidnight(YEAR_2007, MonthOfYear.JANUARY, DAY_15, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    public void test_factory_dateMidnight_int_MonthOfYear_int() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(2007, MonthOfYear.JULY, 15, OFFSET_PONE);
        assertEquals(test.getYear(), YEAR_2007);
        assertEquals(test.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(test.getDayOfMonth(), DAY_15);
        assertEquals(test.getOffset(), OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_int_MonthOfYear_int_dayTooLow() {
        OffsetDateTime.dateMidnight(2007, MonthOfYear.JANUARY, 0, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_int_MonthOfYear_int_dayTooHigh() {
        OffsetDateTime.dateMidnight(2007, MonthOfYear.JANUARY, 32, OFFSET_PONE);
    }


    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_int_MonthOfYear_int_nullMonth() {
        OffsetDateTime.dateMidnight(2007, (MonthOfYear) null, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_int_MonthOfYear_int_yearTooLow() {
        OffsetDateTime.dateMidnight(Integer.MIN_VALUE, MonthOfYear.JANUARY, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_int_MonthOfYear_int_nullOffset() {
        OffsetDateTime.dateMidnight(2007, MonthOfYear.JANUARY, 1, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    public void test_factory_dateMidnight_int_int_int() {
        OffsetDateTime test = OffsetDateTime.dateMidnight(2007, 7, 15, OFFSET_PONE);
        assertEquals(test.getYear(), YEAR_2007);
        assertEquals(test.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(test.getDayOfMonth(), DAY_15);
        assertEquals(test.getOffset(), OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_int_int_int_dayTooLow() {
        OffsetDateTime.dateMidnight(2007, 1, 0, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_int_int_int_dayTooHigh() {
        OffsetDateTime.dateMidnight(2007, 1, 32, OFFSET_PONE);
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_int_int_int_monthTooLow() {
        OffsetDateTime.dateMidnight(2007, 0, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_int_int_int_monthTooHigh() {
        OffsetDateTime.dateMidnight(2007, 13, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_dateMidnight_int_int_int_yearTooLow() {
        OffsetDateTime.dateMidnight(Integer.MIN_VALUE, 1, 1, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_dateMidnight_int_int_int_nullOffset() {
        OffsetDateTime.dateMidnight(2007, 1, 1, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    public void test_factory_dateTime_ReadableInstant_allSecsInDay() {
        for (int i = 0; i < (24 * 60 * 60); i++) {
            Instant instant = Instant.instant(i);
            OffsetDateTime test = OffsetDateTime.dateTime(instant, OFFSET_PONE);
            assertEquals(test.getYear(), Year.isoYear(1970));
            assertEquals(test.getMonthOfYear(), MonthOfYear.JANUARY);
            assertEquals(test.getDayOfMonth(), DayOfMonth.dayOfMonth(1 + (i >= 23 * 60 * 60 ? 1 : 0)));
            assertEquals(test.getHourOfDay(), HourOfDay.hourOfDay(((i / (60 * 60)) + 1) % 24));
            assertEquals(test.getMinuteOfHour(), MinuteOfHour.minuteOfHour((i / 60) % 60));
            assertEquals(test.getSecondOfMinute(), SecondOfMinute.secondOfMinute(i % 60));
        }
    }

    public void test_factory_dateTime_ReadableInstant_allDaysInCycle() {
        // sanity check using different algorithm
        OffsetDateTime expected = OffsetDateTime.dateTime(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        for (long i = 0; i < 146097; i++) {
            Instant instant = Instant.instant(i * 24L * 60L * 60L);
            OffsetDateTime test = OffsetDateTime.dateTime(instant, ZoneOffset.UTC);
            assertEquals(test, expected);
            expected = expected.plusDays(1);
        }
    }

    public void test_factory_dateTime_ReadableInstant_history() {
//        long start = System.currentTimeMillis();
        doTest_factory_dateTime_ReadableInstant_all(-2820, 2820);
//        long end = System.currentTimeMillis();
//        System.err.println(end - start);
    }

    public void test_factory_dateTime_ReadableInstant_minYear() {
        doTest_factory_dateTime_ReadableInstant_all(Year.MIN_YEAR, Year.MIN_YEAR + 420);
    }

    @Test(expectedExceptions= {IllegalCalendarFieldValueException.class})
    public void test_factory_dateTime_ReadableInstant_tooLow() {
        long days_0000_to_1970 = (146097 * 5) - (30 * 365 + 7);
        int year = Year.MIN_YEAR - 1;
        long days = (long) (year * 365L + (year / 4 - year / 100 + year / 400)) - days_0000_to_1970;
        Instant instant = Instant.instant(days * 24L * 60L * 60L);
        OffsetDateTime.dateTime(instant, ZoneOffset.UTC);
    }

    public void test_factory_dateTime_ReadableInstant_maxYear() {
        doTest_factory_dateTime_ReadableInstant_all(Year.MAX_YEAR - 420, Year.MAX_YEAR);
    }

    @Test(expectedExceptions= {IllegalCalendarFieldValueException.class})
    public void test_factory_dateTime_ReadableInstant_tooBig() {
        long days_0000_to_1970 = (146097 * 5) - (30 * 365 + 7);
        int year = Year.MAX_YEAR + 1;
        long days = (long) (year * 365L + (year / 4 - year / 100 + year / 400)) - days_0000_to_1970;
        Instant instant = Instant.instant(days * 24L * 60L * 60L);
        OffsetDateTime.dateTime(instant, ZoneOffset.UTC);
    }

    public void test_factory_dateTime_ReadableInstant_minWithMinOffset() {
        long days_0000_to_1970 = (146097 * 5) - (30 * 365 + 7);
        int year = Year.MIN_YEAR;
        long days = (long) (year * 365L + (year / 4 - year / 100 + year / 400)) - days_0000_to_1970;
        Instant instant = Instant.instant(days * 24L * 60L * 60L - OFFSET_MIN.getAmountSeconds());
        OffsetDateTime test = OffsetDateTime.dateTime(instant, OFFSET_MIN);
        assertEquals(test.getYear().getValue(), Year.MIN_YEAR);
        assertEquals(test.getMonthOfYear().getValue(), 1);
        assertEquals(test.getDayOfMonth().getValue(), 1);
        assertEquals(test.getOffset(), OFFSET_MIN);
        assertEquals(test.getHourOfDay().getValue(), 0);
    }

    public void test_factory_dateTime_ReadableInstant_minWithMaxOffset() {
        long days_0000_to_1970 = (146097 * 5) - (30 * 365 + 7);
        int year = Year.MIN_YEAR;
        long days = (long) (year * 365L + (year / 4 - year / 100 + year / 400)) - days_0000_to_1970;
        Instant instant = Instant.instant(days * 24L * 60L * 60L - OFFSET_MAX.getAmountSeconds());
        OffsetDateTime test = OffsetDateTime.dateTime(instant, OFFSET_MAX);
        assertEquals(test.getYear().getValue(), Year.MIN_YEAR);
        assertEquals(test.getMonthOfYear().getValue(), 1);
        assertEquals(test.getDayOfMonth().getValue(), 1);
        assertEquals(test.getOffset(), OFFSET_MAX);
        assertEquals(test.getHourOfDay().getValue(), 0);
    }

    public void test_factory_dateTime_ReadableInstant_maxWithMinOffset() {
        long days_0000_to_1970 = (146097 * 5) - (30 * 365 + 7);
        int year = Year.MAX_YEAR;
        long days = (long) (year * 365L + (year / 4 - year / 100 + year / 400)) + 365 - days_0000_to_1970;
        Instant instant = Instant.instant(days * 24L * 60L * 60L - OFFSET_MIN.getAmountSeconds());
        OffsetDateTime test = OffsetDateTime.dateTime(instant, OFFSET_MIN);
        assertEquals(test.getYear().getValue(), Year.MAX_YEAR);
        assertEquals(test.getMonthOfYear().getValue(), 12);
        assertEquals(test.getDayOfMonth().getValue(), 31);
        assertEquals(test.getOffset(), OFFSET_MIN);
        assertEquals(test.getHourOfDay().getValue(), 0);
    }

    public void test_factory_dateTime_ReadableInstant_maxWithMaxOffset() {
        long days_0000_to_1970 = (146097 * 5) - (30 * 365 + 7);
        int year = Year.MAX_YEAR;
        long days = (long) (year * 365L + (year / 4 - year / 100 + year / 400)) + 365 - days_0000_to_1970;
        Instant instant = Instant.instant(days * 24L * 60L * 60L - OFFSET_MAX.getAmountSeconds());
        OffsetDateTime test = OffsetDateTime.dateTime(instant, OFFSET_MAX);
        assertEquals(test.getYear().getValue(), Year.MAX_YEAR);
        assertEquals(test.getMonthOfYear().getValue(), 12);
        assertEquals(test.getDayOfMonth().getValue(), 31);
        assertEquals(test.getOffset(), OFFSET_MAX);
        assertEquals(test.getHourOfDay().getValue(), 0);
    }

    private void doTest_factory_dateTime_ReadableInstant_all(int minYear, int maxYear) {
        long days_0000_to_1970 = (146097 * 5) - (30 * 365 + 7);
        int minOffset = (minYear <= 0 ? 0 : 3);
        int maxOffset = (maxYear <= 0 ? 0 : 3);
        long minDays = (long) (minYear * 365L + ((minYear + minOffset) / 4L - (minYear + minOffset) / 100L + (minYear + minOffset) / 400L)) - days_0000_to_1970;
        long maxDays = (long) (maxYear * 365L + ((maxYear + maxOffset) / 4L - (maxYear + maxOffset) / 100L + (maxYear + maxOffset) / 400L)) + 365L - days_0000_to_1970;
        
        OffsetDateTime expected = OffsetDateTime.dateTime(minYear, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        for (long i = minDays; i < maxDays; i++) {
            Instant instant = Instant.instant(i * 24L * 60L * 60L);
            try {
                OffsetDateTime test = OffsetDateTime.dateTime(instant, ZoneOffset.UTC);
                assertEquals(test, expected);
                expected = expected.plusDays(1);
            } catch (RuntimeException ex) {
                System.out.println("Error: " + i + " " + expected);
                throw ex;
            } catch (Error ex) {
                System.out.println("Error: " + i + " " + expected);
                throw ex;
            }
        }
    }

    // for performance testing
//    private void doTest_factory_dateTime_ReadableInstant_all(int minYear, int maxYear) {
//        long days_0000_to_1970 = (146097 * 5) - (30 * 365 + 7);
//        int minOffset = (minYear <= 0 ? 0 : 3);
//        int maxOffset = (maxYear <= 0 ? 0 : 3);
//        long minDays = (long) (minYear * 365L + ((minYear + minOffset) / 4L - (minYear + minOffset) / 100L + (minYear + minOffset) / 400L)) - days_0000_to_1970;
//        long maxDays = (long) (maxYear * 365L + ((maxYear + maxOffset) / 4L - (maxYear + maxOffset) / 100L + (maxYear + maxOffset) / 400L)) + 365L - days_0000_to_1970;
//        
//        OffsetDateTime expected = OffsetDateTime.dateTime(minYear, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
//        Date cutover = new Date(Long.MIN_VALUE);
//        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
//        cal.setGregorianChange(cutover);
//        for (long i = minDays; i < maxDays; i++) {
//            Instant instant = Instant.instant(i * 24L * 60L * 60L);
//            try {
//                cal.setTimeInMillis(instant.getEpochSeconds() * 1000L);
//                assertEquals(cal.get(GregorianCalendar.MONTH), expected.getMonthOfYear().getValue() - 1);
//                assertEquals(cal.get(GregorianCalendar.DAY_OF_MONTH), expected.getDayOfMonth().getValue());
//                expected = expected.plusDays(1);
//            } catch (RuntimeException ex) {
//                System.out.println("Error: " + i + " " + expected);
//                throw ex;
//            } catch (Error ex) {
//                System.out.println("Error: " + i + " " + expected);
//                throw ex;
//            }
//        }
//    }

}
