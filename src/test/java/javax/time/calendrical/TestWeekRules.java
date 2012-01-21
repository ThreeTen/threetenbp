/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendrical;

import static javax.time.calendar.DayOfWeek.FRIDAY;
import static javax.time.calendar.DayOfWeek.MONDAY;
import static javax.time.calendar.DayOfWeek.SATURDAY;
import static javax.time.calendar.DayOfWeek.SUNDAY;
import static javax.time.calendar.DayOfWeek.THURSDAY;
import static javax.time.calendar.DayOfWeek.TUESDAY;
import static javax.time.calendar.DayOfWeek.WEDNESDAY;
import static javax.time.calendar.MonthOfYear.JULY;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.time.MathUtils;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.LocalDate;
import javax.time.calendar.YearMonth;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeRule;
import javax.time.calendrical.IllegalCalendarFieldValueException;
import javax.time.calendrical.WeekRules;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test WeekRules.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestWeekRules {

    @DataProvider(name = "allRules")
    Object[][] data_allRules() {
        Object[][] result = new Object[49][2];
        int i = 0;
        for (DayOfWeek dow : DayOfWeek.values()) {
            for (int minimalDays = 1; minimalDays <= 7; minimalDays++) {
                result[i++] = new Object[] {dow, minimalDays};
            }
        }
        return result;
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Comparable.class.isAssignableFrom(WeekRules.class));
        assertTrue(Serializable.class.isAssignableFrom(WeekRules.class));
    }

    //-----------------------------------------------------------------------
    // factory ISO8601
    //-----------------------------------------------------------------------
    public void test_constant_ISO() {
        assertEquals(WeekRules.ISO.getFirstDayOfWeek(), MONDAY);
        assertEquals(WeekRules.ISO.getMinimalDaysInFirstWeek(), 4);
    }

    //-----------------------------------------------------------------------
    // factory of
    //-----------------------------------------------------------------------
    @Test(dataProvider = "allRules")
    public void test_factory_of(DayOfWeek dow, int minimalDays) {
        WeekRules rules = WeekRules.of(dow, minimalDays);
        assertEquals(rules.getFirstDayOfWeek(), dow);
        assertEquals(rules.getMinimalDaysInFirstWeek(), minimalDays);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_of_null() {
        WeekRules.of(null, 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_of_tooSmall() {
        WeekRules.of(MONDAY, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_of_tooLarge() {
        WeekRules.of(MONDAY, 8);
    }

    //-----------------------------------------------------------------------
    // factory locale
    //-----------------------------------------------------------------------
    public void test_factory_ofLocale() {
        assertEquals(WeekRules.of(Locale.US).getFirstDayOfWeek(), SUNDAY);
        assertEquals(1, WeekRules.of(Locale.US).getMinimalDaysInFirstWeek(), 1);
        
        assertEquals(WeekRules.of(Locale.FRANCE).getFirstDayOfWeek(), MONDAY);
        assertEquals(WeekRules.of(Locale.FRANCE).getMinimalDaysInFirstWeek(), 4);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_ofLocale_null() {
        WeekRules.of((Locale) null);
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        WeekRules orginal = WeekRules.of(MONDAY, 3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        WeekRules ser = (WeekRules) in.readObject();
        assertEquals(WeekRules.of(MONDAY, 3), ser);
    }

//    //-----------------------------------------------------------------------
//    // createWeekBasedYearDate()
//    //-----------------------------------------------------------------------
//    public void test_createDate() {
//        for (DayOfWeek dow : DayOfWeek.values()) {
//            for (int minimalDays = 1; minimalDays <= 7; minimalDays++) {
//                WeekRules rules = WeekRules.of(dow, minimalDays);
//                for (int year = 1950; year < 2050; year++) {
//                    LocalDate date = rules.createWeekBasedYearDate(year);
//                    assertEquals(date.getDayOfWeek(), dow);
//                    LocalDate weekEnd = date.plusDays(6);
//                    assertEquals(weekEnd.getYear(), year);
//                    assertEquals(weekEnd.getMonthOfYear(), JANUARY);
//                    assertTrue(weekEnd.getDayOfMonth() >= minimalDays);
//                }
//            }
//        }
//    }
//
//    public void test_createDate_weekDay() {
//        for (DayOfWeek dow : DayOfWeek.values()) {
//            for (int minimalDays = 1; minimalDays <= 7; minimalDays++) {
//                WeekRules rules = WeekRules.of(dow, minimalDays);
//                for (int year = 1950; year < 2050; year++) {
//                    LocalDate start = rules.createWeekBasedYearDate(year);
//                    for (int week = -60; week < 60; week += 20) {
//                        for (int day = -10; day < 10; day += 2) {
//                            LocalDate date = rules.createWeekBasedYearDate(year, week, day);
//                            assertEquals(date, start.plusWeeks(week - 1).plusDays(day - 1));
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public void test_createDate_weekDayStandardized() {
//        for (DayOfWeek dow : DayOfWeek.values()) {
//            for (int minimalDays = 1; minimalDays <= 7; minimalDays++) {
//                WeekRules rules = WeekRules.of(dow, minimalDays);
//                for (int year = 1950; year < 2050; year++) {
//                    LocalDate start = rules.createWeekBasedYearDate(year);
//                    for (int week = -60; week < 60; week += 20) {
//                        for (DayOfWeek day : DayOfWeek.values()) {
//                            LocalDate date = rules.createWeekBasedYearDate(year, week, day);
//                            assertEquals(date, start.plusWeeks(week - 1).with(DateAdjusters.nextOrCurrent(day)));
//                        }
//                    }
//                }
//            }
//        }
//    }

//    //-----------------------------------------------------------------------
//    // createWeekOfMonthDate()
//    //-----------------------------------------------------------------------
//    @Test(dataProvider = "allRules")
//    public void test_createWeekOfMonthDate_weekDay(DayOfWeek dow, int minimalDays) {
//        WeekRules rules = WeekRules.of(dow, minimalDays);
//        for (int year = 1950; year < 2050; year++) {
//            LocalDate date = rules.createWeekOfMonthDate(YearMonth.of(year, MARCH), 1, 7);
//            assertEquals(date.getDayOfWeek(), dow.roll(6));
//            assertEquals(date.getYear(), year);
//            assertEquals(date.getMonthOfYear(), MARCH);
//            assertTrue(date.getDayOfMonth() >= minimalDays);
//            LocalDate date2 = rules.createWeekOfMonthDate(YearMonth.of(year, MARCH), 2, 1);
//            assertEquals(date.plusDays(1), date2);
//            LocalDate date3 = rules.createWeekOfMonthDate(YearMonth.of(year, MARCH), 3, 2);
//            assertEquals(date.plusDays(9), date3);
//        }
//    }
//
//    @Test(dataProvider = "allRules")
//    public void test_createWeekOfMonthDate_weekDayStandardized(DayOfWeek dow, int minimalDays) {
//        WeekRules rules = WeekRules.of(dow, minimalDays);
//        for (int year = 1950; year < 2050; year++) {
//            LocalDate date = rules.createWeekOfMonthDate(YearMonth.of(year, MARCH), 1, dow.previous());
//            assertEquals(date.getDayOfWeek(), dow.roll(6));
//            assertEquals(date.getYear(), year);
//            assertEquals(date.getMonthOfYear(), MARCH);
//            assertTrue(date.getDayOfMonth() >= minimalDays);
//            LocalDate date2 = rules.createWeekOfMonthDate(YearMonth.of(year, MARCH), 2, dow);
//            assertEquals(date.plusDays(1), date2);
//            LocalDate date3 = rules.createWeekOfMonthDate(YearMonth.of(year, MARCH), 3, dow.next());
//            assertEquals(date.plusDays(9), date3);
//        }
//    }
//
//    public void test_createWeekOfMonthDate_examples1() {
//        WeekRules rules = WeekRules.of(MONDAY, 4);
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2008, DECEMBER), 5, 3), LocalDate.of(2008, 12, 31));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 1, 4), LocalDate.of(2009, 1, 1));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 1, 7), LocalDate.of(2009, 1, 4));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 2, 1), LocalDate.of(2009, 1, 5));
//    }
//
//    public void test_createWeekOfMonthDate_examples2() {
//        WeekRules rules = WeekRules.of(MONDAY, 5);
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2008, DECEMBER), 5, 3), LocalDate.of(2008, 12, 31));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 0, 4), LocalDate.of(2009, 1, 1));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 0, 7), LocalDate.of(2009, 1, 4));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 1, 1), LocalDate.of(2009, 1, 5));
//    }
//
//    public void test_createWeekOfMonthDate_examples2_overflow() {
//        WeekRules rules = WeekRules.of(MONDAY, 5);
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 0, 3), LocalDate.of(2008, 12, 31));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 0, 1), LocalDate.of(2008, 12, 29));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 0, 0), LocalDate.of(2008, 12, 28));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 0, 1), LocalDate.of(2008, 12, 29));
//        assertEquals(rules.createWeekOfMonthDate(YearMonth.of(2009, JANUARY), 6, 1), LocalDate.of(2009, 2, 9));
//    }

    //-----------------------------------------------------------------------
    // convertDayOfWeek()
    //-----------------------------------------------------------------------
    public void test_convertDayOfWeekDOW_MondayBased() {
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(MONDAY), 1);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(TUESDAY), 2);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(WEDNESDAY), 3);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(THURSDAY), 4);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(FRIDAY), 5);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(SATURDAY), 6);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(SUNDAY), 7);
    }

    public void test_convertDayOfWeekDOW_SundayBased() {
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(MONDAY), 2);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(TUESDAY), 3);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(WEDNESDAY), 4);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(THURSDAY), 5);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(FRIDAY), 6);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(SATURDAY), 7);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(SUNDAY), 1);
    }

    public void test_convertDayOfWeekDOW_FridayBased() {
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(MONDAY), 4);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(TUESDAY), 5);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(WEDNESDAY), 6);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(THURSDAY), 7);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(FRIDAY), 1);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(SATURDAY), 2);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(SUNDAY), 3);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_convertDayOfWeekDOW_null() {
        WeekRules.of(FRIDAY, 4).convertDayOfWeek(null);
    }

    //-----------------------------------------------------------------------
    // convertDayOfWeek()
    //-----------------------------------------------------------------------
    public void test_convertDayOfWeek_int_MondayBased() {
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(1), MONDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(2), TUESDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(3), WEDNESDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(4), THURSDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(5), FRIDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(6), SATURDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeek(7), SUNDAY);
    }

    public void test_convertDayOfWeek_int_SundayBased() {
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(2), MONDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(3), TUESDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(4), WEDNESDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(5), THURSDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(6), FRIDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(7), SATURDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeek(1), SUNDAY);
    }

    public void test_convertDayOfWeek_int_FridayBased() {
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(4), MONDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(5), TUESDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(6), WEDNESDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(7), THURSDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(1), FRIDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(2), SATURDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeek(3), SUNDAY);
    }

    @Test(expectedExceptions = IllegalCalendarFieldValueException.class)
    public void test_convertDayOfWeek_int_0() {
        WeekRules.of(FRIDAY, 4).convertDayOfWeek(0);
    }

    @Test(expectedExceptions = IllegalCalendarFieldValueException.class)
    public void test_convertDayOfWeek_int_8() {
        WeekRules.of(FRIDAY, 4).convertDayOfWeek(8);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @DataProvider(name="compare")
    public Object[][] data_compare() {
        return new Object[][] {
            {WeekRules.of(MONDAY, 1), WeekRules.of(MONDAY, 2)},
            {WeekRules.of(MONDAY, 1), WeekRules.of(MONDAY, 3)},
            {WeekRules.of(MONDAY, 1), WeekRules.of(MONDAY, 7)},
            {WeekRules.of(MONDAY, 1), WeekRules.of(TUESDAY, 1)},
            {WeekRules.of(MONDAY, 7), WeekRules.of(TUESDAY, 1)},
            {WeekRules.of(TUESDAY, 6), WeekRules.of(TUESDAY, 7)},
            {WeekRules.of(MONDAY, 1), WeekRules.of(SUNDAY, 7)},
        };
    }

    @Test(dataProvider="compare")
    public void test_compareTo(WeekRules a, WeekRules b) {
        assertEquals(a.compareTo(a), 0);
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_compareTo_null() {
        WeekRules.ISO.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="compare")
    public void test_equals(WeekRules a, WeekRules b) {
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    public void test_equals_null() {
        assertEquals(WeekRules.ISO.equals(null), false);
    }

    public void test_equals_otherClass() {
        assertEquals(WeekRules.ISO.equals(""), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="compare")
    public void test_hashCode(WeekRules a, WeekRules b) {
        assertEquals(a.hashCode() == a.hashCode(), true);
        assertEquals(b.hashCode() == b.hashCode(), true);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(WeekRules.of(MONDAY, 4).toString(), "WeekRules[MONDAY,4]");
        assertEquals(WeekRules.of(SUNDAY, 1).toString(), "WeekRules[SUNDAY,1]");
    }

    //-----------------------------------------------------------------------
    // dayOfWeek()
    //-----------------------------------------------------------------------
    public void test_dayOfWeek_sun1() {
        DateTimeRule rule = WeekRules.of(SUNDAY, 1).dayOfWeek();
        assertEquals(rule.toString(), "DayOfWeek-WeekRules[SUNDAY,1]");
        
        assertEquals(rule.getBaseRule(), rule);
        
        assertEquals(rule.convertToPeriod(1), 0);  // Sun
        assertEquals(rule.convertToPeriod(2), 1);  // Mon
        assertEquals(rule.convertToPeriod(3), 2);  // Tue
        assertEquals(rule.convertToPeriod(4), 3);  // Wed
        assertEquals(rule.convertToPeriod(5), 4);  // Thu
        assertEquals(rule.convertToPeriod(6), 5);  // Fri
        assertEquals(rule.convertToPeriod(7), 6);  // Sat
        assertEquals(rule.convertToPeriod(8), 7);
        assertEquals(rule.convertToPeriod(0), -1);
        
        assertEquals(rule.convertFromPeriod(0), 1);  // Sun
        assertEquals(rule.convertFromPeriod(6), 7);  // Sat
        assertEquals(rule.convertFromPeriod(-1), 0);
        assertEquals(rule.convertFromPeriod(7), 8);
    }

    public void test_dayOfWeek_getValue_sun1() {
        // July 2011 starts on a Friday
        DateTimeRule rule = WeekRules.of(SUNDAY, 1).dayOfWeek();
        for (int i = 1; i <= 31; i++) {
            int stdDOW = (i + 4 - 1) % 7 + 1;
            DayOfWeek stdObj = DayOfWeek.of(stdDOW);
            int relDOW = (i + 5 - 1) % 7 + 1;
            DateTimeField relField = rule.field(relDOW);
            assertEquals(rule.getValue(LocalDate.of(2011, 7, i)), relField);
            assertEquals(rule.getValue(stdObj), relField);
//            assertEquals(DayOfWeek.from(relField), stdObj);  // TODO: engine change needed
        }
        
//        assertEquals(rule.field(-7), rule.derive(DAY_OF_WEEK.field(-8)));  // 2prev Mon
//        
//        assertEquals(rule.field(-6), rule.derive(DAY_OF_WEEK.field(0)));  // prev Sun
//        assertEquals(rule.field(-5), rule.derive(DAY_OF_WEEK.field(-6)));  // prev Mon
//        assertEquals(rule.field(-4), rule.derive(DAY_OF_WEEK.field(-5)));  // prev Tue
//        assertEquals(rule.field(-3), rule.derive(DAY_OF_WEEK.field(-4)));  // prev Wed
//        assertEquals(rule.field(-2), rule.derive(DAY_OF_WEEK.field(-3)));  // prev Thu
//        assertEquals(rule.field(-1), rule.derive(DAY_OF_WEEK.field(-2)));  // prev Fri
//        assertEquals(rule.field(0), rule.derive(DAY_OF_WEEK.field(-1)));  // prev Sat
        
        // TODO: these tests used to work (normalizer)
//        assertEquals(rule.field(1), rule.derive(DAY_OF_WEEK.field(7)));  // Sun
//        assertEquals(rule.field(2), rule.derive(DAY_OF_WEEK.field(1)));  // Mon
//        assertEquals(rule.field(3), rule.derive(DAY_OF_WEEK.field(2)));  // Tue
//        assertEquals(rule.field(4), rule.derive(DAY_OF_WEEK.field(3)));  // Wed
//        assertEquals(rule.field(5), rule.derive(DAY_OF_WEEK.field(4)));  // Thu
//        assertEquals(rule.field(6), rule.derive(DAY_OF_WEEK.field(5)));  // Fri
//        assertEquals(rule.field(7), rule.derive(DAY_OF_WEEK.field(6)));  // Sat
//        assertEquals(null, rule.derive(DAY_OF_WEEK.field(0)));
//        assertEquals(null, rule.derive(DAY_OF_WEEK.field(8)));
        
//        assertEquals(rule.field(8), rule.derive(DAY_OF_WEEK.field(14)));  // next Sun
//        assertEquals(rule.field(9), rule.derive(DAY_OF_WEEK.field(8)));  // next Mon
//        assertEquals(rule.field(10), rule.derive(DAY_OF_WEEK.field(9)));  // next Tue
//        assertEquals(rule.field(11), rule.derive(DAY_OF_WEEK.field(10)));  // next Wed
//        assertEquals(rule.field(12), rule.derive(DAY_OF_WEEK.field(11)));  // next Thu
//        assertEquals(rule.field(13), rule.derive(DAY_OF_WEEK.field(12)));  // next Fri
//        assertEquals(rule.field(14), rule.derive(DAY_OF_WEEK.field(13)));  // next Sat
//        
//        assertEquals(rule.field(15), rule.derive(DAY_OF_WEEK.field(21)));  // 2next Sun
    }

    //-----------------------------------------------------------------------
    // weekOfMonth()
    //-----------------------------------------------------------------------
    public void test_weekOfMonth_sun1() {
        DateTimeRule rule = WeekRules.of(SUNDAY, 1).weekOfMonth();
        assertEquals(rule.toString(), "WeekOfMonth-WeekRules[SUNDAY,1]");
        
        assertEquals(rule.getBaseRule(), rule);
        
        assertEquals(rule.convertToPeriod(-1), -1);
        assertEquals(rule.convertToPeriod(0), 0);
        assertEquals(rule.convertToPeriod(1), 1);
        assertEquals(rule.convertToPeriod(2), 2);
        assertEquals(rule.convertToPeriod(5), 5);
        assertEquals(rule.convertToPeriod(6), 6);
        
        assertEquals(rule.convertFromPeriod(-1), -1);
        assertEquals(rule.convertFromPeriod(0), 0);
        assertEquals(rule.convertFromPeriod(1), 1);
        assertEquals(rule.convertFromPeriod(2), 2);
        assertEquals(rule.convertFromPeriod(5), 5);
        assertEquals(rule.convertFromPeriod(6), 6);
    }

    public void test_weekOfMonth_getValue_sun1() {
        // July 2011 starts on a Friday
        // Fri/Sat 1st/2nd are week 1
        DateTimeRule rule = WeekRules.of(SUNDAY, 1).weekOfMonth();
        for (int i = 1; i <= 31; i++) {
            int w = MathUtils.floorDiv(i - 3, 7) + 2;  // 3rd is start of week 2
            assertEquals(rule.getValue(LocalDate.of(2011, 7, i)), rule.field(w));
        }
    }

    public void test_weekOfMonth_buildDate_sun1() {
        // July 2011 starts on a Friday
        // Fri/Sat 1st/2nd are week 0
        WeekRules wr = WeekRules.of(SUNDAY, 1);
        for (int i = 1; i <= 31; i++) {
            int w = MathUtils.floorDiv(i - 3, 7) + 2;  // 3rd is start of week 2
            int d = MathUtils.floorMod(i - 3, 7) + 1;
            CalendricalEngine engine = CalendricalEngine.merge(
                YearMonth.of(2011, JULY), wr.weekOfMonth().field(w), wr.dayOfWeek().field(d));
            assertEquals(engine.derive(LocalDate.rule()), LocalDate.of(2011, JULY, i));
        }
    }

    public void test_weekOfMonth_getValue_sun7() {
        // July 2011 starts on a Friday
        // Fri/Sat 1st/2nd are week 0
        DateTimeRule rule = WeekRules.of(SUNDAY, 7).weekOfMonth();
        for (int i = 1; i <= 31; i++) {
            int w = MathUtils.floorDiv(i - 3, 7) + 1;  // 3rd is start of week 1
            assertEquals(rule.getValue(LocalDate.of(2011, 7, i)), rule.field(w));
        }
    }

    public void test_weekOfMonth_buildDate_sun7() {
        // July 2011 starts on a Friday
        // Fri/Sat 1st/2nd are week 0
        WeekRules wr = WeekRules.of(SUNDAY, 7);
        for (int i = 1; i <= 31; i++) {
            int w = MathUtils.floorDiv(i - 3, 7) + 1;
            int d = MathUtils.floorMod(i - 3, 7) + 1;
            CalendricalEngine engine = CalendricalEngine.merge(
                YearMonth.of(2011, JULY), wr.weekOfMonth().field(w), wr.dayOfWeek().field(d));
            assertEquals(engine.derive(LocalDate.rule()), LocalDate.of(2011, JULY, i));
        }
    }

    @Test(dataProvider = "allRules")
    public void test_weekOfMonth_getValue_crossCheckCalendar(DayOfWeek dow, int minimalDays) {
        DateTimeRule rule = WeekRules.of(dow, minimalDays).weekOfMonth();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setFirstDayOfWeek(dow == SUNDAY ? 1 : dow.getValue() + 1);
        gcal.setMinimalDaysInFirstWeek(minimalDays);
        gcal.set(2011, Calendar.JANUARY, 1);
        LocalDate date = LocalDate.of(2011, 1, 1);
        for (int i = 1; i <= 365; i++) {
            assertEquals(rule.getValue(date).getValue(), gcal.get(Calendar.WEEK_OF_MONTH));
            gcal.add(Calendar.DAY_OF_MONTH, 1);
            date = date.plusDays(1);
        }
    }
}
