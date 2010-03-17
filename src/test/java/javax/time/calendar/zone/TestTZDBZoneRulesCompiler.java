/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.zone;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.time.calendar.DayOfWeek;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.Year;
import javax.time.calendar.ZonedDateTime;
import javax.time.calendar.format.DateTimeFormatterBuilder;
import javax.time.calendar.zone.TZDBZoneRulesCompiler.TZDBMonthDayTime;
import javax.time.calendar.zone.TZDBZoneRulesCompiler.TZDBRule;
import javax.time.calendar.zone.ZoneRulesBuilder.TimeDefinition;

import org.testng.annotations.Test;

/**
 * Test TZDBZoneRulesCompiler.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestTZDBZoneRulesCompiler {

    //-----------------------------------------------------------------------
    // parseYear()
    //-----------------------------------------------------------------------
    public void test_parseYear_specific() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "2010", 2000), 2010);
    }

    public void test_parseYear_min() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "min", 2000), Year.MIN_YEAR);
    }

    public void test_parseYear_mini() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "mini", 2000), Year.MIN_YEAR);
    }

    public void test_parseYear_minim() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "minim", 2000), Year.MIN_YEAR);
    }

    public void test_parseYear_minimu() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "minimu", 2000), Year.MIN_YEAR);
    }

    public void test_parseYear_minimum() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "minimum", 2000), Year.MIN_YEAR);
    }

    @Test(expectedExceptions=NumberFormatException.class)
    public void test_parseYear_minTooShort() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        parseYear(test, "mi", 2000);
    }

    @Test(expectedExceptions=NumberFormatException.class)
    public void test_parseYear_minTooLong() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        parseYear(test, "minimuma", 2000);
    }

    public void test_parseYear_max() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "max", 2000), Year.MAX_YEAR);
    }

    public void test_parseYear_maxi() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "maxi", 2000), Year.MAX_YEAR);
    }

    public void test_parseYear_maxim() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "maxim", 2000), Year.MAX_YEAR);
    }

    public void test_parseYear_maximu() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "maximu", 2000), Year.MAX_YEAR);
    }

    public void test_parseYear_maximum() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "maximum", 2000), Year.MAX_YEAR);
    }

    @Test(expectedExceptions=NumberFormatException.class)
    public void test_parseYear_maxTooShort() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        parseYear(test, "ma", 2000);
    }

    @Test(expectedExceptions=NumberFormatException.class)
    public void test_parseYear_maxTooLong() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        parseYear(test, "maximuma", 2000);
    }

    public void test_parseYear_only() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "only", 2000), 2000);
    }

    public void test_parseYear_only_uppercase() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseYear(test, "ONLY", 2000), 2000);
    }

    @Test(expectedExceptions=NumberFormatException.class)
    public void test_parseYear_invalidYear() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        parseYear(test, "ABC", 2000);
    }

    static final Method PARSE_YEAR;
    static {
        try {
            PARSE_YEAR = TZDBZoneRulesCompiler.class.getDeclaredMethod("parseYear", String.class, Integer.TYPE);
            PARSE_YEAR.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private int parseYear(TZDBZoneRulesCompiler test, String str, int year) throws Exception {
        try {
            return (Integer) PARSE_YEAR.invoke(test, str, year);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() != null) {
                throw (Exception) ex.getCause();
            }
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // parseMonth()
    //-----------------------------------------------------------------------
    public void test_parseMonth() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseMonth(test, "Jan"), MonthOfYear.JANUARY);
        assertEquals(parseMonth(test, "Feb"), MonthOfYear.FEBRUARY);
        assertEquals(parseMonth(test, "Mar"), MonthOfYear.MARCH);
        assertEquals(parseMonth(test, "Apr"), MonthOfYear.APRIL);
        assertEquals(parseMonth(test, "May"), MonthOfYear.MAY);
        assertEquals(parseMonth(test, "Jun"), MonthOfYear.JUNE);
        assertEquals(parseMonth(test, "Jul"), MonthOfYear.JULY);
        assertEquals(parseMonth(test, "Aug"), MonthOfYear.AUGUST);
        assertEquals(parseMonth(test, "Sep"), MonthOfYear.SEPTEMBER);
        assertEquals(parseMonth(test, "Oct"), MonthOfYear.OCTOBER);
        assertEquals(parseMonth(test, "Nov"), MonthOfYear.NOVEMBER);
        assertEquals(parseMonth(test, "Dec"), MonthOfYear.DECEMBER);
        assertEquals(parseMonth(test, "January"), MonthOfYear.JANUARY);
        assertEquals(parseMonth(test, "February"), MonthOfYear.FEBRUARY);
        assertEquals(parseMonth(test, "March"), MonthOfYear.MARCH);
        assertEquals(parseMonth(test, "April"), MonthOfYear.APRIL);
        assertEquals(parseMonth(test, "May"), MonthOfYear.MAY);
        assertEquals(parseMonth(test, "June"), MonthOfYear.JUNE);
        assertEquals(parseMonth(test, "July"), MonthOfYear.JULY);
        assertEquals(parseMonth(test, "August"), MonthOfYear.AUGUST);
        assertEquals(parseMonth(test, "September"), MonthOfYear.SEPTEMBER);
        assertEquals(parseMonth(test, "October"), MonthOfYear.OCTOBER);
        assertEquals(parseMonth(test, "November"), MonthOfYear.NOVEMBER);
        assertEquals(parseMonth(test, "December"), MonthOfYear.DECEMBER);
        assertEquals(parseMonth(test, "Janu"), MonthOfYear.JANUARY);
        assertEquals(parseMonth(test, "Janua"), MonthOfYear.JANUARY);
        assertEquals(parseMonth(test, "Januar"), MonthOfYear.JANUARY);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_parseMonth_invalidMonth() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        parseMonth(test, "ABC");
    }

    static final Method PARSE_MONTH;
    static {
        try {
            PARSE_MONTH = TZDBZoneRulesCompiler.class.getDeclaredMethod("parseMonth", String.class);
            PARSE_MONTH.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private MonthOfYear parseMonth(TZDBZoneRulesCompiler test, String str) throws Exception {
        try {
            return (MonthOfYear) PARSE_MONTH.invoke(test, str);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() != null) {
                throw (Exception) ex.getCause();
            }
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // parseDayOfWeek()
    //-----------------------------------------------------------------------
    public void test_parseDayOfWeek() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        assertEquals(parseDayOfWeek(test, "Mon"), DayOfWeek.MONDAY);
        assertEquals(parseDayOfWeek(test, "Tue"), DayOfWeek.TUESDAY);
        assertEquals(parseDayOfWeek(test, "Wed"), DayOfWeek.WEDNESDAY);
        assertEquals(parseDayOfWeek(test, "Thu"), DayOfWeek.THURSDAY);
        assertEquals(parseDayOfWeek(test, "Fri"), DayOfWeek.FRIDAY);
        assertEquals(parseDayOfWeek(test, "Sat"), DayOfWeek.SATURDAY);
        assertEquals(parseDayOfWeek(test, "Sun"), DayOfWeek.SUNDAY);
        assertEquals(parseDayOfWeek(test, "Monday"), DayOfWeek.MONDAY);
        assertEquals(parseDayOfWeek(test, "Tuesday"), DayOfWeek.TUESDAY);
        assertEquals(parseDayOfWeek(test, "Wednesday"), DayOfWeek.WEDNESDAY);
        assertEquals(parseDayOfWeek(test, "Thursday"), DayOfWeek.THURSDAY);
        assertEquals(parseDayOfWeek(test, "Friday"), DayOfWeek.FRIDAY);
        assertEquals(parseDayOfWeek(test, "Saturday"), DayOfWeek.SATURDAY);
        assertEquals(parseDayOfWeek(test, "Sunday"), DayOfWeek.SUNDAY);
        assertEquals(parseDayOfWeek(test, "Mond"), DayOfWeek.MONDAY);
        assertEquals(parseDayOfWeek(test, "Monda"), DayOfWeek.MONDAY);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_parseDayOfWeek_invalidMonth() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        parseMonth(test, "ABC");
    }

    static final Method PARSE_DOW;
    static {
        try {
            PARSE_DOW = TZDBZoneRulesCompiler.class.getDeclaredMethod("parseDayOfWeek", String.class);
            PARSE_DOW.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private DayOfWeek parseDayOfWeek(TZDBZoneRulesCompiler test, String str) throws Exception {
        try {
            return (DayOfWeek) PARSE_DOW.invoke(test, str);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() != null) {
                throw (Exception) ex.getCause();
            }
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // parseMonthDayTime()
    //-----------------------------------------------------------------------
    public void test_parseMonthDayTime_marLastSun0220() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        TZDBRule mdt = parseMonthDayTime(test, "Mar lastSun 2:20");
        assertEquals(mdt.month, MonthOfYear.MARCH);
        assertEquals(mdt.dayOfWeek, DayOfWeek.SUNDAY);
        assertEquals(mdt.dayOfMonth, -1);
        assertEquals(mdt.adjustForwards, false);
        assertEquals(mdt.time, LocalTime.of(2, 20));
        assertEquals(mdt.endOfDay, false);
        assertEquals(mdt.timeDefinition, TimeDefinition.WALL);
    }

    public void test_parseMonthDayTime_jun50220s() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        TZDBRule mdt = parseMonthDayTime(test, "Jun 5 2:20s");
        assertEquals(mdt.month, MonthOfYear.JUNE);
        assertEquals(mdt.dayOfWeek, null);
        assertEquals(mdt.dayOfMonth, 5);
        assertEquals(mdt.adjustForwards, true);
        assertEquals(mdt.time, LocalTime.of(2, 20));
        assertEquals(mdt.endOfDay, false);
        assertEquals(mdt.timeDefinition, TimeDefinition.STANDARD);
    }

    public void test_parseMonthDayTime_maySatAfter50220u() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        TZDBRule mdt = parseMonthDayTime(test, "May Sat>=5 2:20u");
        assertEquals(mdt.month, MonthOfYear.MAY);
        assertEquals(mdt.dayOfWeek, DayOfWeek.SATURDAY);
        assertEquals(mdt.dayOfMonth, 5);
        assertEquals(mdt.adjustForwards, true);
        assertEquals(mdt.time, LocalTime.of(2, 20));
        assertEquals(mdt.endOfDay, false);
        assertEquals(mdt.timeDefinition, TimeDefinition.UTC);
    }

    public void test_parseMonthDayTime_maySatBefore50220u() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        TZDBRule mdt = parseMonthDayTime(test, "May Sat<=5 24:00g");
        assertEquals(mdt.month, MonthOfYear.MAY);
        assertEquals(mdt.dayOfWeek, DayOfWeek.SATURDAY);
        assertEquals(mdt.dayOfMonth, 5);
        assertEquals(mdt.adjustForwards, false);
        assertEquals(mdt.time, LocalTime.of(0, 0));
        assertEquals(mdt.endOfDay, true);
        assertEquals(mdt.timeDefinition, TimeDefinition.UTC);
    }

    public void test_parseMonthDayTime_maySatBefore15Dash() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        TZDBRule mdt = parseMonthDayTime(test, "May Sat<=15 -");
        assertEquals(mdt.month, MonthOfYear.MAY);
        assertEquals(mdt.dayOfWeek, DayOfWeek.SATURDAY);
        assertEquals(mdt.dayOfMonth, 15);
        assertEquals(mdt.adjustForwards, false);
        assertEquals(mdt.time, LocalTime.of(0, 0));
        assertEquals(mdt.endOfDay, false);
        assertEquals(mdt.timeDefinition, TimeDefinition.WALL);
    }

    public void test_parseMonthDayTime_maylastSunShortTime() throws Exception {
        TZDBZoneRulesCompiler test = new TZDBZoneRulesCompiler("2010c", new ArrayList<File>(), null, false);
        TZDBRule mdt = parseMonthDayTime(test, "May lastSun 3z");
        assertEquals(mdt.month, MonthOfYear.MAY);
        assertEquals(mdt.dayOfWeek, DayOfWeek.SUNDAY);
        assertEquals(mdt.dayOfMonth, -1);
        assertEquals(mdt.adjustForwards, false);
        assertEquals(mdt.time, LocalTime.of(3, 0));
        assertEquals(mdt.endOfDay, false);
        assertEquals(mdt.timeDefinition, TimeDefinition.UTC);
    }

    static final Method PARSE_MDT;
    static {
        try {
            PARSE_MDT = TZDBZoneRulesCompiler.class.getDeclaredMethod("parseMonthDayTime", StringTokenizer.class, TZDBMonthDayTime.class);
            PARSE_MDT.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private TZDBRule parseMonthDayTime(TZDBZoneRulesCompiler test, String str) throws Exception {
        try {
            TZDBRule mdt = test.new TZDBRule();  // create a bound inner class
            PARSE_MDT.invoke(test, new StringTokenizer(str), mdt);
            return mdt;
        } catch (InvocationTargetException ex) {
            if (ex.getCause() != null) {
                throw (Exception) ex.getCause();
            }
            throw ex;
        }
    }

}
