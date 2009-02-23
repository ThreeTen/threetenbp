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
package javax.time.calendar.field;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateAdjuster;
import javax.time.calendar.DateMatcher;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DateResolver;
import javax.time.calendar.DateResolvers;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.InvalidCalendarFieldException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.MockDateProviderReturnsNull;
import javax.time.calendar.MockDateResolverReturnsNull;
import javax.time.calendar.MonthDay;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test MonthOfYear.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestMonthOfYear {

    private static final DateTimeFieldRule RULE = ISOChronology.monthOfYearRule();
    private static final Year YEAR_STANDARD = Year.isoYear(2007);
    private static final Year YEAR_LEAP = Year.isoYear(2008);
    private static final int STANDARD_YEAR_LENGTH = 365;
    private static final int LEAP_YEAR_LENGTH = 366;
    private static final int MAX_LENGTH = 12;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalProvider.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(Serializable.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(Comparable.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(DateAdjuster.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(DateMatcher.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(Enum.class.isAssignableFrom(MonthOfYear.class));
    }

    //-----------------------------------------------------------------------
    public void test_rule() {
        assertEquals(MonthOfYear.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            MonthOfYear test = MonthOfYear.monthOfYear(i);
            assertEquals(test.getValue(), i);
            assertSame(MonthOfYear.monthOfYear(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_tooLow() {
        MonthOfYear.monthOfYear(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_tooHigh() {
        MonthOfYear.monthOfYear(13);
    }

    //-----------------------------------------------------------------------
    public void test_factory_DateProvider_notLeapYear() {
        LocalDate date = LocalDate.date(2007, 1, 1);
        for (int i = 1; i <= 31; i++) {  // Jan
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 1);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 28; i++) {  // Feb
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 2);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Mar
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 3);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Apr
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 4);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // May
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 5);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Jun
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 6);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Jul
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 7);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Aug
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 8);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Sep
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 9);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Oct
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 10);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 30; i++) {  // Nov
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 11);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Dec
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 12);
            date = date.plusDays(1);
        }
    }

    public void test_factory_DateProvider_leapYear() {
        LocalDate date = LocalDate.date(2008, 1, 1);
        for (int i = 1; i <= 31; i++) {  // Jan
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 1);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 29; i++) {  // Feb
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 2);
            date = date.plusDays(1);
        }
        for (int i = 1; i <= 31; i++) {  // Mar
            assertEquals(MonthOfYear.monthOfYear(date).getValue(), 3);
            date = date.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullDateProvider() {
        MonthOfYear.monthOfYear((DateProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_badDateProvider() {
        MonthOfYear.monthOfYear(new MockDateProviderReturnsNull());
    }

    //-----------------------------------------------------------------------
    // getShortText()
    //-----------------------------------------------------------------------
    public void test_getShortText_US() {
        assertEquals(MonthOfYear.JANUARY.getShortText(Locale.US), "Jan");
        assertEquals(MonthOfYear.FEBRUARY.getShortText(Locale.US), "Feb");
        assertEquals(MonthOfYear.MARCH.getShortText(Locale.US), "Mar");
        assertEquals(MonthOfYear.APRIL.getShortText(Locale.US), "Apr");
        assertEquals(MonthOfYear.MAY.getShortText(Locale.US), "May");
        assertEquals(MonthOfYear.JUNE.getShortText(Locale.US), "Jun");
        assertEquals(MonthOfYear.JULY.getShortText(Locale.US), "Jul");
        assertEquals(MonthOfYear.AUGUST.getShortText(Locale.US), "Aug");
        assertEquals(MonthOfYear.SEPTEMBER.getShortText(Locale.US), "Sep");
        assertEquals(MonthOfYear.OCTOBER.getShortText(Locale.US), "Oct");
        assertEquals(MonthOfYear.NOVEMBER.getShortText(Locale.US), "Nov");
        assertEquals(MonthOfYear.DECEMBER.getShortText(Locale.US), "Dec");
    }

    public void test_getShortText_pt_BR() {
        Locale ptBR = new Locale("pt", "BR");
        assertEquals(MonthOfYear.JANUARY.getShortText(ptBR), "Jan");
        assertEquals(MonthOfYear.FEBRUARY.getShortText(ptBR), "Fev");
        assertEquals(MonthOfYear.MARCH.getShortText(ptBR), "Mar");
        assertEquals(MonthOfYear.APRIL.getShortText(ptBR), "Abr");
        assertEquals(MonthOfYear.MAY.getShortText(ptBR), "Mai");
        assertEquals(MonthOfYear.JUNE.getShortText(ptBR), "Jun");
        assertEquals(MonthOfYear.JULY.getShortText(ptBR), "Jul");
        assertEquals(MonthOfYear.AUGUST.getShortText(ptBR), "Ago");
        assertEquals(MonthOfYear.SEPTEMBER.getShortText(ptBR), "Set");
        assertEquals(MonthOfYear.OCTOBER.getShortText(ptBR), "Out");
        assertEquals(MonthOfYear.NOVEMBER.getShortText(ptBR), "Nov");
        assertEquals(MonthOfYear.DECEMBER.getShortText(ptBR), "Dez");
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
    public void test_getText_US() {
        assertEquals(MonthOfYear.JANUARY.getText(Locale.US), "January");
        assertEquals(MonthOfYear.FEBRUARY.getText(Locale.US), "February");
        assertEquals(MonthOfYear.MARCH.getText(Locale.US), "March");
        assertEquals(MonthOfYear.APRIL.getText(Locale.US), "April");
        assertEquals(MonthOfYear.MAY.getText(Locale.US), "May");
        assertEquals(MonthOfYear.JUNE.getText(Locale.US), "June");
        assertEquals(MonthOfYear.JULY.getText(Locale.US), "July");
        assertEquals(MonthOfYear.AUGUST.getText(Locale.US), "August");
        assertEquals(MonthOfYear.SEPTEMBER.getText(Locale.US), "September");
        assertEquals(MonthOfYear.OCTOBER.getText(Locale.US), "October");
        assertEquals(MonthOfYear.NOVEMBER.getText(Locale.US), "November");
        assertEquals(MonthOfYear.DECEMBER.getText(Locale.US), "December");
    }

    public void test_getText_pt_BR() {
        Locale ptBR = new Locale("pt", "BR");
        assertEquals(MonthOfYear.JANUARY.getText(ptBR), "Janeiro");
        assertEquals(MonthOfYear.FEBRUARY.getText(ptBR), "Fevereiro");
        assertEquals(MonthOfYear.MARCH.getText(ptBR), "Mar\u00E7o");
        assertEquals(MonthOfYear.APRIL.getText(ptBR), "Abril");
        assertEquals(MonthOfYear.MAY.getText(ptBR), "Maio");
        assertEquals(MonthOfYear.JUNE.getText(ptBR), "Junho");
        assertEquals(MonthOfYear.JULY.getText(ptBR), "Julho");
        assertEquals(MonthOfYear.AUGUST.getText(ptBR), "Agosto");
        assertEquals(MonthOfYear.SEPTEMBER.getText(ptBR), "Setembro");
        assertEquals(MonthOfYear.OCTOBER.getText(ptBR), "Outubro");
        assertEquals(MonthOfYear.NOVEMBER.getText(ptBR), "Novembro");
        assertEquals(MonthOfYear.DECEMBER.getText(ptBR), "Dezembro");
    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    public void test_next() {
        assertEquals(MonthOfYear.JANUARY.next(), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.FEBRUARY.next(), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.MARCH.next(), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.APRIL.next(), MonthOfYear.MAY);
        assertEquals(MonthOfYear.MAY.next(), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JUNE.next(), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JULY.next(), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.AUGUST.next(), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.SEPTEMBER.next(), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.OCTOBER.next(), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.NOVEMBER.next(), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.DECEMBER.next(), MonthOfYear.JANUARY);
    }

    //-----------------------------------------------------------------------
    // previous()
    //-----------------------------------------------------------------------
    public void test_previous() {
        assertEquals(MonthOfYear.JANUARY.previous(), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.FEBRUARY.previous(), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.MARCH.previous(), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.APRIL.previous(), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.MAY.previous(), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JUNE.previous(), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JULY.previous(), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.AUGUST.previous(), MonthOfYear.JULY);
        assertEquals(MonthOfYear.SEPTEMBER.previous(), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.OCTOBER.previous(), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.NOVEMBER.previous(), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.DECEMBER.previous(), MonthOfYear.NOVEMBER);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths_january() {
        assertEquals(MonthOfYear.JANUARY.plusMonths(0), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JANUARY.plusMonths(1), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JANUARY.plusMonths(2), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JANUARY.plusMonths(3), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JANUARY.plusMonths(4), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JANUARY.plusMonths(5), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JANUARY.plusMonths(6), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JANUARY.plusMonths(7), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JANUARY.plusMonths(8), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JANUARY.plusMonths(9), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JANUARY.plusMonths(10), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JANUARY.plusMonths(11), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JANUARY.plusMonths(12), MonthOfYear.JANUARY);
    }

    public void test_plusMonths_july() {
        assertEquals(MonthOfYear.JULY.plusMonths(0), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JULY.plusMonths(1), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JULY.plusMonths(2), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JULY.plusMonths(3), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JULY.plusMonths(4), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JULY.plusMonths(5), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JULY.plusMonths(6), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JULY.plusMonths(7), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JULY.plusMonths(8), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JULY.plusMonths(9), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JULY.plusMonths(10), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JULY.plusMonths(11), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JULY.plusMonths(12), MonthOfYear.JULY);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths_january() {
        assertEquals(MonthOfYear.JANUARY.minusMonths(0), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JANUARY.minusMonths(1), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JANUARY.minusMonths(2), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JANUARY.minusMonths(3), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JANUARY.minusMonths(4), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JANUARY.minusMonths(5), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JANUARY.minusMonths(6), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JANUARY.minusMonths(7), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JANUARY.minusMonths(8), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JANUARY.minusMonths(9), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JANUARY.minusMonths(10), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JANUARY.minusMonths(11), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JANUARY.minusMonths(12), MonthOfYear.JANUARY);
    }

    public void test_minusMonths_july() {
        assertEquals(MonthOfYear.JULY.minusMonths(0), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JULY.minusMonths(1), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JULY.minusMonths(2), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JULY.minusMonths(3), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JULY.minusMonths(4), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JULY.minusMonths(5), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JULY.minusMonths(6), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JULY.minusMonths(6), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JULY.minusMonths(7), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JULY.minusMonths(8), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JULY.minusMonths(9), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JULY.minusMonths(10), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JULY.minusMonths(11), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JULY.minusMonths(12), MonthOfYear.JULY);
    }

    //-----------------------------------------------------------------------
    // adjustDate(LocalDate)
    //-----------------------------------------------------------------------
    public void test_adjustDate() {
        LocalDate base = LocalDate.date(2007, 1, 12);
        for (int i = 1; i <= MAX_LENGTH; i++) {
            LocalDate result = MonthOfYear.monthOfYear(i).adjustDate(base);
            assertEquals(result, LocalDate.date(2007, i, 12));
        }
    }

    public void test_adjustDate_adjustDayOfMonthFrom29() {
        LocalDate base = LocalDate.date(2007, 1, 29);
        for (int i = 1; i <= MAX_LENGTH; i++) {
            LocalDate result = MonthOfYear.monthOfYear(i).adjustDate(base);
            int dom = (i == 2 ? 28 : 29);
            assertEquals(result, LocalDate.date(2007, i, dom));
        }
    }

    public void test_adjustDate_adjustDayOfMonthFrom31() {
        LocalDate base = LocalDate.date(2007, 1, 31);
        for (int i = 1; i <= MAX_LENGTH; i++) {
            LocalDate result = MonthOfYear.monthOfYear(i).adjustDate(base);
            int dom = (i == 4 || i == 6 || i == 9 || i == 11) ? 30 : (i == 2 ? 28 : 31);
            assertEquals(result, LocalDate.date(2007, i, dom));
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_nullLocalDate() {
        MonthOfYear test = MonthOfYear.monthOfYear(1);
        test.adjustDate((LocalDate) null);
    }

    //-----------------------------------------------------------------------
    // adjustDate(LocalDate,DateResolver)
    //-----------------------------------------------------------------------
    public void test_adjustDate_strictResolver() {
        LocalDate base = LocalDate.date(2007, 1, 12);
        for (int i = 1; i <= MAX_LENGTH; i++) {
            LocalDate result = MonthOfYear.monthOfYear(i).adjustDate(base, DateResolvers.strict());
            assertEquals(result, LocalDate.date(2007, i, 12));
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_strictResolver_april31() {
        LocalDate base = LocalDate.date(2007, 1, 31);
        MonthOfYear test = MonthOfYear.monthOfYear(4);
        try {
            test.adjustDate(base, DateResolvers.strict());
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_strictResolver_february29_notLeapYear() {
        LocalDate base = LocalDate.date(2007, 1, 29);
        MonthOfYear test = MonthOfYear.monthOfYear(2);
        try {
            test.adjustDate(base, DateResolvers.strict());
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_resolver_nullLocalDate() {
        MonthOfYear test = MonthOfYear.monthOfYear(1);
        test.adjustDate((LocalDate) null, DateResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_resolver_nullResolver() {
        LocalDate date = LocalDate.date(2007, 1, 1);
        MonthOfYear test = MonthOfYear.monthOfYear(1);
        test.adjustDate(date, (DateResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_resolver_badResolver() {
        LocalDate date = LocalDate.date(2007, 1, 31);
        MonthOfYear test = MonthOfYear.monthOfYear(2);
        test.adjustDate(date, new MockDateResolverReturnsNull());
    }

    //-----------------------------------------------------------------------
    // matchesDate(LocalDate)
    //-----------------------------------------------------------------------
    public void test_matchesDate_notLeapYear() {
        LocalDate work = LocalDate.date(2007, 1, 1);
        for (int i = 1; i <= STANDARD_YEAR_LENGTH; i++) {
            for (int j = 1; j <= MAX_LENGTH; j++) {
                MonthOfYear test = MonthOfYear.monthOfYear(j);
                assertEquals(test.matchesDate(work), work.getMonthOfYear().getValue() == j);
            }
            work = work.plusDays(1);
        }
    }

    public void test_matchesDate_leapYear() {
        LocalDate work = LocalDate.date(2008, 1, 1);
        for (int i = 1; i <= LEAP_YEAR_LENGTH; i++) {
            for (int j = 1; j <= MAX_LENGTH; j++) {
                MonthOfYear test = MonthOfYear.monthOfYear(j);
                assertEquals(test.matchesDate(work), work.getMonthOfYear().getValue() == j);
            }
            work = work.plusDays(1);
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_nullLocalDate() {
        MonthOfYear test = MonthOfYear.monthOfYear(1);
        test.matchesDate((LocalDate) null);
    }

    //-----------------------------------------------------------------------
    // lengthInDays(Year)
    //-----------------------------------------------------------------------
    public void test_lengthInDays_notLeapYear() {
        Year year = YEAR_STANDARD;
        assertEquals(MonthOfYear.JANUARY.lengthInDays(year), 31);
        assertEquals(MonthOfYear.FEBRUARY.lengthInDays(year), 28);
        assertEquals(MonthOfYear.MARCH.lengthInDays(year), 31);
        assertEquals(MonthOfYear.APRIL.lengthInDays(year), 30);
        assertEquals(MonthOfYear.MAY.lengthInDays(year), 31);
        assertEquals(MonthOfYear.JUNE.lengthInDays(year), 30);
        assertEquals(MonthOfYear.JULY.lengthInDays(year), 31);
        assertEquals(MonthOfYear.AUGUST.lengthInDays(year), 31);
        assertEquals(MonthOfYear.SEPTEMBER.lengthInDays(year), 30);
        assertEquals(MonthOfYear.OCTOBER.lengthInDays(year), 31);
        assertEquals(MonthOfYear.NOVEMBER.lengthInDays(year), 30);
        assertEquals(MonthOfYear.DECEMBER.lengthInDays(year), 31);
    }

    public void test_lengthInDays_leapYear() {
        Year year = YEAR_LEAP;
        assertEquals(MonthOfYear.JANUARY.lengthInDays(year), 31);
        assertEquals(MonthOfYear.FEBRUARY.lengthInDays(year), 29);
        assertEquals(MonthOfYear.MARCH.lengthInDays(year), 31);
        assertEquals(MonthOfYear.APRIL.lengthInDays(year), 30);
        assertEquals(MonthOfYear.MAY.lengthInDays(year), 31);
        assertEquals(MonthOfYear.JUNE.lengthInDays(year), 30);
        assertEquals(MonthOfYear.JULY.lengthInDays(year), 31);
        assertEquals(MonthOfYear.AUGUST.lengthInDays(year), 31);
        assertEquals(MonthOfYear.SEPTEMBER.lengthInDays(year), 30);
        assertEquals(MonthOfYear.OCTOBER.lengthInDays(year), 31);
        assertEquals(MonthOfYear.NOVEMBER.lengthInDays(year), 30);
        assertEquals(MonthOfYear.DECEMBER.lengthInDays(year), 31);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_lengthInDays_nullYear() {
        MonthOfYear.JANUARY.lengthInDays((Year) null);
    }

    //-----------------------------------------------------------------------
    // lengthInDays(int)
    //-----------------------------------------------------------------------
    public void test_lengthInDays_int_notLeapYear() {
        assertEquals(MonthOfYear.JANUARY.lengthInDays(2007), 31);
        assertEquals(MonthOfYear.FEBRUARY.lengthInDays(2007), 28);
        assertEquals(MonthOfYear.MARCH.lengthInDays(2007), 31);
        assertEquals(MonthOfYear.APRIL.lengthInDays(2007), 30);
        assertEquals(MonthOfYear.MAY.lengthInDays(2007), 31);
        assertEquals(MonthOfYear.JUNE.lengthInDays(2007), 30);
        assertEquals(MonthOfYear.JULY.lengthInDays(2007), 31);
        assertEquals(MonthOfYear.AUGUST.lengthInDays(2007), 31);
        assertEquals(MonthOfYear.SEPTEMBER.lengthInDays(2007), 30);
        assertEquals(MonthOfYear.OCTOBER.lengthInDays(2007), 31);
        assertEquals(MonthOfYear.NOVEMBER.lengthInDays(2007), 30);
        assertEquals(MonthOfYear.DECEMBER.lengthInDays(2007), 31);
    }

    public void test_lengthInDays_int_leapYear() {
        assertEquals(MonthOfYear.JANUARY.lengthInDays(2008), 31);
        assertEquals(MonthOfYear.FEBRUARY.lengthInDays(2008), 29);
        assertEquals(MonthOfYear.MARCH.lengthInDays(2008), 31);
        assertEquals(MonthOfYear.APRIL.lengthInDays(2008), 30);
        assertEquals(MonthOfYear.MAY.lengthInDays(2008), 31);
        assertEquals(MonthOfYear.JUNE.lengthInDays(2008), 30);
        assertEquals(MonthOfYear.JULY.lengthInDays(2008), 31);
        assertEquals(MonthOfYear.AUGUST.lengthInDays(2008), 31);
        assertEquals(MonthOfYear.SEPTEMBER.lengthInDays(2008), 30);
        assertEquals(MonthOfYear.OCTOBER.lengthInDays(2008), 31);
        assertEquals(MonthOfYear.NOVEMBER.lengthInDays(2008), 30);
        assertEquals(MonthOfYear.DECEMBER.lengthInDays(2008), 31);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_lengthInDays_int_invalidYear() {
        MonthOfYear.JANUARY.lengthInDays(Integer.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minLengthInDays()
    //-----------------------------------------------------------------------
    public void test_minLengthInDays() {
        assertEquals(MonthOfYear.JANUARY.minLengthInDays(), 31);
        assertEquals(MonthOfYear.FEBRUARY.minLengthInDays(), 28);
        assertEquals(MonthOfYear.MARCH.minLengthInDays(), 31);
        assertEquals(MonthOfYear.APRIL.minLengthInDays(), 30);
        assertEquals(MonthOfYear.MAY.minLengthInDays(), 31);
        assertEquals(MonthOfYear.JUNE.minLengthInDays(), 30);
        assertEquals(MonthOfYear.JULY.minLengthInDays(), 31);
        assertEquals(MonthOfYear.AUGUST.minLengthInDays(), 31);
        assertEquals(MonthOfYear.SEPTEMBER.minLengthInDays(), 30);
        assertEquals(MonthOfYear.OCTOBER.minLengthInDays(), 31);
        assertEquals(MonthOfYear.NOVEMBER.minLengthInDays(), 30);
        assertEquals(MonthOfYear.DECEMBER.minLengthInDays(), 31);
    }

    //-----------------------------------------------------------------------
    // maxLengthInDays()
    //-----------------------------------------------------------------------
    public void test_maxLengthInDays() {
        assertEquals(MonthOfYear.JANUARY.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.FEBRUARY.maxLengthInDays(), 29);
        assertEquals(MonthOfYear.MARCH.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.APRIL.maxLengthInDays(), 30);
        assertEquals(MonthOfYear.MAY.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.JUNE.maxLengthInDays(), 30);
        assertEquals(MonthOfYear.JULY.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.AUGUST.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.SEPTEMBER.maxLengthInDays(), 30);
        assertEquals(MonthOfYear.OCTOBER.maxLengthInDays(), 31);
        assertEquals(MonthOfYear.NOVEMBER.maxLengthInDays(), 30);
        assertEquals(MonthOfYear.DECEMBER.maxLengthInDays(), 31);
    }

    //-----------------------------------------------------------------------
    // getLastDayOfMonth(Year)
    //-----------------------------------------------------------------------
    public void test_getLastDayOfMonth_notLeapYear() {
        Year year = YEAR_STANDARD;
        assertEquals(MonthOfYear.JANUARY.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.FEBRUARY.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(28));
        assertEquals(MonthOfYear.MARCH.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.APRIL.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(30));
        assertEquals(MonthOfYear.MAY.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.JUNE.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(30));
        assertEquals(MonthOfYear.JULY.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.AUGUST.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.SEPTEMBER.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(30));
        assertEquals(MonthOfYear.OCTOBER.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.NOVEMBER.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(30));
        assertEquals(MonthOfYear.DECEMBER.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
    }

    public void test_getLastDayOfMonth_leapYear() {
        Year year = YEAR_LEAP;
        assertEquals(MonthOfYear.JANUARY.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.FEBRUARY.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(29));
        assertEquals(MonthOfYear.MARCH.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.APRIL.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(30));
        assertEquals(MonthOfYear.MAY.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.JUNE.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(30));
        assertEquals(MonthOfYear.JULY.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.AUGUST.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.SEPTEMBER.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(30));
        assertEquals(MonthOfYear.OCTOBER.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
        assertEquals(MonthOfYear.NOVEMBER.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(30));
        assertEquals(MonthOfYear.DECEMBER.getLastDayOfMonth(year), DayOfMonth.dayOfMonth(31));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getLastDayOfMonth_nullYear() {
        MonthOfYear.JANUARY.getLastDayOfMonth((Year) null);
    }

    //-----------------------------------------------------------------------
    // getQuarterOfYear()
    //-----------------------------------------------------------------------
    public void test_getQuarterOfYear() {
        assertEquals(MonthOfYear.JANUARY.getQuarterOfYear(), QuarterOfYear.Q1);
        assertEquals(MonthOfYear.FEBRUARY.getQuarterOfYear(), QuarterOfYear.Q1);
        assertEquals(MonthOfYear.MARCH.getQuarterOfYear(), QuarterOfYear.Q1);
        assertEquals(MonthOfYear.APRIL.getQuarterOfYear(), QuarterOfYear.Q2);
        assertEquals(MonthOfYear.MAY.getQuarterOfYear(), QuarterOfYear.Q2);
        assertEquals(MonthOfYear.JUNE.getQuarterOfYear(), QuarterOfYear.Q2);
        assertEquals(MonthOfYear.JULY.getQuarterOfYear(), QuarterOfYear.Q3);
        assertEquals(MonthOfYear.AUGUST.getQuarterOfYear(), QuarterOfYear.Q3);
        assertEquals(MonthOfYear.SEPTEMBER.getQuarterOfYear(), QuarterOfYear.Q3);
        assertEquals(MonthOfYear.OCTOBER.getQuarterOfYear(), QuarterOfYear.Q4);
        assertEquals(MonthOfYear.NOVEMBER.getQuarterOfYear(), QuarterOfYear.Q4);
        assertEquals(MonthOfYear.DECEMBER.getQuarterOfYear(), QuarterOfYear.Q4);
    }

    //-----------------------------------------------------------------------
    // getMonthOfQuarter()
    //-----------------------------------------------------------------------
    public void test_getMonthOfQuarter() {
        assertEquals(MonthOfYear.JANUARY.getMonthOfQuarter(), 1);
        assertEquals(MonthOfYear.FEBRUARY.getMonthOfQuarter(), 2);
        assertEquals(MonthOfYear.MARCH.getMonthOfQuarter(), 3);
        assertEquals(MonthOfYear.APRIL.getMonthOfQuarter(), 1);
        assertEquals(MonthOfYear.MAY.getMonthOfQuarter(), 2);
        assertEquals(MonthOfYear.JUNE.getMonthOfQuarter(), 3);
        assertEquals(MonthOfYear.JULY.getMonthOfQuarter(), 1);
        assertEquals(MonthOfYear.AUGUST.getMonthOfQuarter(), 2);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthOfQuarter(), 3);
        assertEquals(MonthOfYear.OCTOBER.getMonthOfQuarter(), 1);
        assertEquals(MonthOfYear.NOVEMBER.getMonthOfQuarter(), 2);
        assertEquals(MonthOfYear.DECEMBER.getMonthOfQuarter(), 3);
    }

    //-----------------------------------------------------------------------
    // atDay(DayOfMonth)
    //-----------------------------------------------------------------------
    public void test_atDay() {
        MonthOfYear test = MonthOfYear.JUNE;
        assertEquals(test.atDay(DayOfMonth.dayOfMonth(30)), MonthDay.monthDay(6, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atDay_nullYear() {
        MonthOfYear test = MonthOfYear.JUNE;
        test.atDay((DayOfMonth) null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_atDay_invalidDay() {
        MonthOfYear test = MonthOfYear.JUNE;
        try {
            test.atDay(DayOfMonth.dayOfMonth(31));
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // atDay(int)
    //-----------------------------------------------------------------------
    public void test_atDay_int() {
        MonthOfYear test = MonthOfYear.JUNE;
        assertEquals(test.atDay(30), MonthDay.monthDay(6, 30));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_atDay_int_invalidDay() {
        MonthOfYear test = MonthOfYear.JUNE;
        try {
            test.atDay(31);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    public void test_toCalendrical() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            MonthOfYear test = MonthOfYear.monthOfYear(i);
            assertEquals(test.toCalendrical(), new Calendrical(RULE, i));
        }
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(MonthOfYear.JANUARY.toString(), "MonthOfYear=JANUARY");
        assertEquals(MonthOfYear.FEBRUARY.toString(), "MonthOfYear=FEBRUARY");
        assertEquals(MonthOfYear.MARCH.toString(), "MonthOfYear=MARCH");
        assertEquals(MonthOfYear.APRIL.toString(), "MonthOfYear=APRIL");
        assertEquals(MonthOfYear.MAY.toString(), "MonthOfYear=MAY");
        assertEquals(MonthOfYear.JUNE.toString(), "MonthOfYear=JUNE");
        assertEquals(MonthOfYear.JULY.toString(), "MonthOfYear=JULY");
        assertEquals(MonthOfYear.AUGUST.toString(), "MonthOfYear=AUGUST");
        assertEquals(MonthOfYear.SEPTEMBER.toString(), "MonthOfYear=SEPTEMBER");
        assertEquals(MonthOfYear.OCTOBER.toString(), "MonthOfYear=OCTOBER");
        assertEquals(MonthOfYear.NOVEMBER.toString(), "MonthOfYear=NOVEMBER");
        assertEquals(MonthOfYear.DECEMBER.toString(), "MonthOfYear=DECEMBER");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    public void test_enum() {
        assertEquals(MonthOfYear.valueOf("JANUARY"), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.values()[0], MonthOfYear.JANUARY);
    }

}
