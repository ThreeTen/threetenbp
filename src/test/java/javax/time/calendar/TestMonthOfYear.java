/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.Locale;

import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.QuarterOfYear;

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

    private static final int MAX_LENGTH = 12;

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(Serializable.class.isAssignableFrom(MonthOfYear.class));
        assertTrue(Comparable.class.isAssignableFrom(MonthOfYear.class));
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            MonthOfYear test = MonthOfYear.of(i);
            assertEquals(test.getValue(), i);
            assertSame(MonthOfYear.of(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_tooLow() {
        MonthOfYear.of(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_tooHigh() {
        MonthOfYear.of(13);
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(MonthOfYear.JANUARY.get(ISOChronology.monthOfYearRule()), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.AUGUST.get(ISOChronology.monthOfYearRule()), MonthOfYear.AUGUST);
        
        assertEquals(MonthOfYear.JANUARY.get(ISOChronology.quarterOfYearRule()), null);
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
    // is...()
    //-----------------------------------------------------------------------
    public void test_isJanuary() {
        assertEquals(MonthOfYear.JANUARY.isJanuary(), true);
        assertEquals(MonthOfYear.FEBRUARY.isJanuary(), false);
        assertEquals(MonthOfYear.MARCH.isJanuary(), false);
        assertEquals(MonthOfYear.APRIL.isJanuary(), false);
        assertEquals(MonthOfYear.MAY.isJanuary(), false);
        assertEquals(MonthOfYear.JUNE.isJanuary(), false);
        assertEquals(MonthOfYear.JULY.isJanuary(), false);
        assertEquals(MonthOfYear.AUGUST.isJanuary(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isJanuary(), false);
        assertEquals(MonthOfYear.OCTOBER.isJanuary(), false);
        assertEquals(MonthOfYear.NOVEMBER.isJanuary(), false);
        assertEquals(MonthOfYear.DECEMBER.isJanuary(), false);
    }

    public void test_isFebruary() {
        assertEquals(MonthOfYear.JANUARY.isFebruary(), false);
        assertEquals(MonthOfYear.FEBRUARY.isFebruary(), true);
        assertEquals(MonthOfYear.MARCH.isFebruary(), false);
        assertEquals(MonthOfYear.APRIL.isFebruary(), false);
        assertEquals(MonthOfYear.MAY.isFebruary(), false);
        assertEquals(MonthOfYear.JUNE.isFebruary(), false);
        assertEquals(MonthOfYear.JULY.isFebruary(), false);
        assertEquals(MonthOfYear.AUGUST.isFebruary(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isFebruary(), false);
        assertEquals(MonthOfYear.OCTOBER.isFebruary(), false);
        assertEquals(MonthOfYear.NOVEMBER.isFebruary(), false);
        assertEquals(MonthOfYear.DECEMBER.isFebruary(), false);
    }

    public void test_isMarch() {
        assertEquals(MonthOfYear.JANUARY.isMarch(), false);
        assertEquals(MonthOfYear.FEBRUARY.isMarch(), false);
        assertEquals(MonthOfYear.MARCH.isMarch(), true);
        assertEquals(MonthOfYear.APRIL.isMarch(), false);
        assertEquals(MonthOfYear.MAY.isMarch(), false);
        assertEquals(MonthOfYear.JUNE.isMarch(), false);
        assertEquals(MonthOfYear.JULY.isMarch(), false);
        assertEquals(MonthOfYear.AUGUST.isMarch(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isMarch(), false);
        assertEquals(MonthOfYear.OCTOBER.isMarch(), false);
        assertEquals(MonthOfYear.NOVEMBER.isMarch(), false);
        assertEquals(MonthOfYear.DECEMBER.isMarch(), false);
    }

    public void test_isApril() {
        assertEquals(MonthOfYear.JANUARY.isApril(), false);
        assertEquals(MonthOfYear.FEBRUARY.isApril(), false);
        assertEquals(MonthOfYear.MARCH.isApril(), false);
        assertEquals(MonthOfYear.APRIL.isApril(), true);
        assertEquals(MonthOfYear.MAY.isApril(), false);
        assertEquals(MonthOfYear.JUNE.isApril(), false);
        assertEquals(MonthOfYear.JULY.isApril(), false);
        assertEquals(MonthOfYear.AUGUST.isApril(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isApril(), false);
        assertEquals(MonthOfYear.OCTOBER.isApril(), false);
        assertEquals(MonthOfYear.NOVEMBER.isApril(), false);
        assertEquals(MonthOfYear.DECEMBER.isApril(), false);
    }

    public void test_isMay() {
        assertEquals(MonthOfYear.JANUARY.isMay(), false);
        assertEquals(MonthOfYear.FEBRUARY.isMay(), false);
        assertEquals(MonthOfYear.MARCH.isMay(), false);
        assertEquals(MonthOfYear.APRIL.isMay(), false);
        assertEquals(MonthOfYear.MAY.isMay(), true);
        assertEquals(MonthOfYear.JUNE.isMay(), false);
        assertEquals(MonthOfYear.JULY.isMay(), false);
        assertEquals(MonthOfYear.AUGUST.isMay(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isMay(), false);
        assertEquals(MonthOfYear.OCTOBER.isMay(), false);
        assertEquals(MonthOfYear.NOVEMBER.isMay(), false);
        assertEquals(MonthOfYear.DECEMBER.isMay(), false);
    }

    public void test_isJune() {
        assertEquals(MonthOfYear.JANUARY.isJune(), false);
        assertEquals(MonthOfYear.FEBRUARY.isJune(), false);
        assertEquals(MonthOfYear.MARCH.isJune(), false);
        assertEquals(MonthOfYear.APRIL.isJune(), false);
        assertEquals(MonthOfYear.MAY.isJune(), false);
        assertEquals(MonthOfYear.JUNE.isJune(), true);
        assertEquals(MonthOfYear.JULY.isJune(), false);
        assertEquals(MonthOfYear.AUGUST.isJune(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isJune(), false);
        assertEquals(MonthOfYear.OCTOBER.isJune(), false);
        assertEquals(MonthOfYear.NOVEMBER.isJune(), false);
        assertEquals(MonthOfYear.DECEMBER.isJune(), false);
    }

    public void test_isJuly() {
        assertEquals(MonthOfYear.JANUARY.isJuly(), false);
        assertEquals(MonthOfYear.FEBRUARY.isJuly(), false);
        assertEquals(MonthOfYear.MARCH.isJuly(), false);
        assertEquals(MonthOfYear.APRIL.isJuly(), false);
        assertEquals(MonthOfYear.MAY.isJuly(), false);
        assertEquals(MonthOfYear.JUNE.isJuly(), false);
        assertEquals(MonthOfYear.JULY.isJuly(), true);
        assertEquals(MonthOfYear.AUGUST.isJuly(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isJuly(), false);
        assertEquals(MonthOfYear.OCTOBER.isJuly(), false);
        assertEquals(MonthOfYear.NOVEMBER.isJuly(), false);
        assertEquals(MonthOfYear.DECEMBER.isJuly(), false);
    }

    public void test_isAugust() {
        assertEquals(MonthOfYear.JANUARY.isAugust(), false);
        assertEquals(MonthOfYear.FEBRUARY.isAugust(), false);
        assertEquals(MonthOfYear.MARCH.isAugust(), false);
        assertEquals(MonthOfYear.APRIL.isAugust(), false);
        assertEquals(MonthOfYear.MAY.isAugust(), false);
        assertEquals(MonthOfYear.JUNE.isAugust(), false);
        assertEquals(MonthOfYear.JULY.isAugust(), false);
        assertEquals(MonthOfYear.AUGUST.isAugust(), true);
        assertEquals(MonthOfYear.SEPTEMBER.isAugust(), false);
        assertEquals(MonthOfYear.OCTOBER.isAugust(), false);
        assertEquals(MonthOfYear.NOVEMBER.isAugust(), false);
        assertEquals(MonthOfYear.DECEMBER.isAugust(), false);
    }

    public void test_isSeptember() {
        assertEquals(MonthOfYear.JANUARY.isSeptember(), false);
        assertEquals(MonthOfYear.FEBRUARY.isSeptember(), false);
        assertEquals(MonthOfYear.MARCH.isSeptember(), false);
        assertEquals(MonthOfYear.APRIL.isSeptember(), false);
        assertEquals(MonthOfYear.MAY.isSeptember(), false);
        assertEquals(MonthOfYear.JUNE.isSeptember(), false);
        assertEquals(MonthOfYear.JULY.isSeptember(), false);
        assertEquals(MonthOfYear.AUGUST.isSeptember(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isSeptember(), true);
        assertEquals(MonthOfYear.OCTOBER.isSeptember(), false);
        assertEquals(MonthOfYear.NOVEMBER.isSeptember(), false);
        assertEquals(MonthOfYear.DECEMBER.isSeptember(), false);
    }

    public void test_isOctober() {
        assertEquals(MonthOfYear.JANUARY.isOctober(), false);
        assertEquals(MonthOfYear.FEBRUARY.isOctober(), false);
        assertEquals(MonthOfYear.MARCH.isOctober(), false);
        assertEquals(MonthOfYear.APRIL.isOctober(), false);
        assertEquals(MonthOfYear.MAY.isOctober(), false);
        assertEquals(MonthOfYear.JUNE.isOctober(), false);
        assertEquals(MonthOfYear.JULY.isOctober(), false);
        assertEquals(MonthOfYear.AUGUST.isOctober(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isOctober(), false);
        assertEquals(MonthOfYear.OCTOBER.isOctober(), true);
        assertEquals(MonthOfYear.NOVEMBER.isOctober(), false);
        assertEquals(MonthOfYear.DECEMBER.isOctober(), false);
    }

    public void test_isNovember() {
        assertEquals(MonthOfYear.JANUARY.isNovember(), false);
        assertEquals(MonthOfYear.FEBRUARY.isNovember(), false);
        assertEquals(MonthOfYear.MARCH.isNovember(), false);
        assertEquals(MonthOfYear.APRIL.isNovember(), false);
        assertEquals(MonthOfYear.MAY.isNovember(), false);
        assertEquals(MonthOfYear.JUNE.isNovember(), false);
        assertEquals(MonthOfYear.JULY.isNovember(), false);
        assertEquals(MonthOfYear.AUGUST.isNovember(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isNovember(), false);
        assertEquals(MonthOfYear.OCTOBER.isNovember(), false);
        assertEquals(MonthOfYear.NOVEMBER.isNovember(), true);
        assertEquals(MonthOfYear.DECEMBER.isNovember(), false);
    }

    public void test_isDecember() {
        assertEquals(MonthOfYear.JANUARY.isDecember(), false);
        assertEquals(MonthOfYear.FEBRUARY.isDecember(), false);
        assertEquals(MonthOfYear.MARCH.isDecember(), false);
        assertEquals(MonthOfYear.APRIL.isDecember(), false);
        assertEquals(MonthOfYear.MAY.isDecember(), false);
        assertEquals(MonthOfYear.JUNE.isDecember(), false);
        assertEquals(MonthOfYear.JULY.isDecember(), false);
        assertEquals(MonthOfYear.AUGUST.isDecember(), false);
        assertEquals(MonthOfYear.SEPTEMBER.isDecember(), false);
        assertEquals(MonthOfYear.OCTOBER.isDecember(), false);
        assertEquals(MonthOfYear.NOVEMBER.isDecember(), false);
        assertEquals(MonthOfYear.DECEMBER.isDecember(), true);
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
    // roll(int)
    //-----------------------------------------------------------------------
    public void test_roll_january() {
        assertEquals(MonthOfYear.JANUARY.roll(-12), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JANUARY.roll(-11), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JANUARY.roll(-10), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JANUARY.roll(-9), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JANUARY.roll(-8), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JANUARY.roll(-7), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JANUARY.roll(-6), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JANUARY.roll(-5), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JANUARY.roll(-4), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(-3), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JANUARY.roll(-2), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(-1), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(0), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JANUARY.roll(1), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JANUARY.roll(2), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JANUARY.roll(3), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JANUARY.roll(4), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JANUARY.roll(5), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JANUARY.roll(6), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JANUARY.roll(7), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JANUARY.roll(8), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(9), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JANUARY.roll(10), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(11), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JANUARY.roll(12), MonthOfYear.JANUARY);
    }

    public void test_roll_july() {
        assertEquals(MonthOfYear.JULY.roll(-12), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JULY.roll(-11), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JULY.roll(-10), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JULY.roll(-9), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JULY.roll(-8), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JULY.roll(-7), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JULY.roll(-6), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JULY.roll(-5), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JULY.roll(-4), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JULY.roll(-3), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JULY.roll(-2), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JULY.roll(-1), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JULY.roll(0), MonthOfYear.JULY);
        assertEquals(MonthOfYear.JULY.roll(1), MonthOfYear.AUGUST);
        assertEquals(MonthOfYear.JULY.roll(2), MonthOfYear.SEPTEMBER);
        assertEquals(MonthOfYear.JULY.roll(3), MonthOfYear.OCTOBER);
        assertEquals(MonthOfYear.JULY.roll(4), MonthOfYear.NOVEMBER);
        assertEquals(MonthOfYear.JULY.roll(5), MonthOfYear.DECEMBER);
        assertEquals(MonthOfYear.JULY.roll(6), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.JULY.roll(7), MonthOfYear.FEBRUARY);
        assertEquals(MonthOfYear.JULY.roll(8), MonthOfYear.MARCH);
        assertEquals(MonthOfYear.JULY.roll(9), MonthOfYear.APRIL);
        assertEquals(MonthOfYear.JULY.roll(10), MonthOfYear.MAY);
        assertEquals(MonthOfYear.JULY.roll(11), MonthOfYear.JUNE);
        assertEquals(MonthOfYear.JULY.roll(12), MonthOfYear.JULY);
    }

    //-----------------------------------------------------------------------
    // lengthInDays(boolean)
    //-----------------------------------------------------------------------
    public void test_lengthInDays_boolean_notLeapYear() {
        assertEquals(MonthOfYear.JANUARY.lengthInDays(false), 31);
        assertEquals(MonthOfYear.FEBRUARY.lengthInDays(false), 28);
        assertEquals(MonthOfYear.MARCH.lengthInDays(false), 31);
        assertEquals(MonthOfYear.APRIL.lengthInDays(false), 30);
        assertEquals(MonthOfYear.MAY.lengthInDays(false), 31);
        assertEquals(MonthOfYear.JUNE.lengthInDays(false), 30);
        assertEquals(MonthOfYear.JULY.lengthInDays(false), 31);
        assertEquals(MonthOfYear.AUGUST.lengthInDays(false), 31);
        assertEquals(MonthOfYear.SEPTEMBER.lengthInDays(false), 30);
        assertEquals(MonthOfYear.OCTOBER.lengthInDays(false), 31);
        assertEquals(MonthOfYear.NOVEMBER.lengthInDays(false), 30);
        assertEquals(MonthOfYear.DECEMBER.lengthInDays(false), 31);
    }

    public void test_lengthInDays_boolean_leapYear() {
        assertEquals(MonthOfYear.JANUARY.lengthInDays(true), 31);
        assertEquals(MonthOfYear.FEBRUARY.lengthInDays(true), 29);
        assertEquals(MonthOfYear.MARCH.lengthInDays(true), 31);
        assertEquals(MonthOfYear.APRIL.lengthInDays(true), 30);
        assertEquals(MonthOfYear.MAY.lengthInDays(true), 31);
        assertEquals(MonthOfYear.JUNE.lengthInDays(true), 30);
        assertEquals(MonthOfYear.JULY.lengthInDays(true), 31);
        assertEquals(MonthOfYear.AUGUST.lengthInDays(true), 31);
        assertEquals(MonthOfYear.SEPTEMBER.lengthInDays(true), 30);
        assertEquals(MonthOfYear.OCTOBER.lengthInDays(true), 31);
        assertEquals(MonthOfYear.NOVEMBER.lengthInDays(true), 30);
        assertEquals(MonthOfYear.DECEMBER.lengthInDays(true), 31);
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
    // getLastDayOfMonth(boolean)
    //-----------------------------------------------------------------------
    public void test_getLastDayOfMonth_notLeapYear() {
        assertEquals(MonthOfYear.JANUARY.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.FEBRUARY.getLastDayOfMonth(false), 28);
        assertEquals(MonthOfYear.MARCH.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.APRIL.getLastDayOfMonth(false), 30);
        assertEquals(MonthOfYear.MAY.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.JUNE.getLastDayOfMonth(false), 30);
        assertEquals(MonthOfYear.JULY.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.AUGUST.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.SEPTEMBER.getLastDayOfMonth(false), 30);
        assertEquals(MonthOfYear.OCTOBER.getLastDayOfMonth(false), 31);
        assertEquals(MonthOfYear.NOVEMBER.getLastDayOfMonth(false), 30);
        assertEquals(MonthOfYear.DECEMBER.getLastDayOfMonth(false), 31);
    }

    public void test_getLastDayOfMonth_leapYear() {
        assertEquals(MonthOfYear.JANUARY.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.FEBRUARY.getLastDayOfMonth(true), 29);
        assertEquals(MonthOfYear.MARCH.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.APRIL.getLastDayOfMonth(true), 30);
        assertEquals(MonthOfYear.MAY.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.JUNE.getLastDayOfMonth(true), 30);
        assertEquals(MonthOfYear.JULY.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.AUGUST.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.SEPTEMBER.getLastDayOfMonth(true), 30);
        assertEquals(MonthOfYear.OCTOBER.getLastDayOfMonth(true), 31);
        assertEquals(MonthOfYear.NOVEMBER.getLastDayOfMonth(true), 30);
        assertEquals(MonthOfYear.DECEMBER.getLastDayOfMonth(true), 31);
    }

    //-----------------------------------------------------------------------
    // getMonthStartDayOfYear(boolean)
    //-----------------------------------------------------------------------
    public void test_getMonthStartDayOfYear_notLeapYear() {
        assertEquals(MonthOfYear.JANUARY.getMonthStartDayOfYear(false), 1);
        assertEquals(MonthOfYear.FEBRUARY.getMonthStartDayOfYear(false), 1 + 31);
        assertEquals(MonthOfYear.MARCH.getMonthStartDayOfYear(false), 1 + 31 + 28);
        assertEquals(MonthOfYear.APRIL.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31);
        assertEquals(MonthOfYear.MAY.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30);
        assertEquals(MonthOfYear.JUNE.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31);
        assertEquals(MonthOfYear.JULY.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.AUGUST.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(MonthOfYear.OCTOBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(MonthOfYear.NOVEMBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(MonthOfYear.DECEMBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
    }

    public void test_getMonthStartDayOfYear_leapYear() {
        assertEquals(MonthOfYear.JANUARY.getMonthStartDayOfYear(true), 1);
        assertEquals(MonthOfYear.FEBRUARY.getMonthStartDayOfYear(true), 1 + 31);
        assertEquals(MonthOfYear.MARCH.getMonthStartDayOfYear(true), 1 + 31 + 29);
        assertEquals(MonthOfYear.APRIL.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31);
        assertEquals(MonthOfYear.MAY.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30);
        assertEquals(MonthOfYear.JUNE.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31);
        assertEquals(MonthOfYear.JULY.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.AUGUST.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(MonthOfYear.OCTOBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(MonthOfYear.NOVEMBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(MonthOfYear.DECEMBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
    }

    //-----------------------------------------------------------------------
    // getMonthEndDayOfYear(boolean)
    //-----------------------------------------------------------------------
    public void test_getMonthEndDayOfYear_notLeapYear() {
        assertEquals(MonthOfYear.JANUARY.getMonthEndDayOfYear(false), 31);
        assertEquals(MonthOfYear.FEBRUARY.getMonthEndDayOfYear(false), 31 + 28);
        assertEquals(MonthOfYear.MARCH.getMonthEndDayOfYear(false), 31 + 28 + 31);
        assertEquals(MonthOfYear.APRIL.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30);
        assertEquals(MonthOfYear.MAY.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31);
        assertEquals(MonthOfYear.JUNE.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.JULY.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31);
        assertEquals(MonthOfYear.AUGUST.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(MonthOfYear.OCTOBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(MonthOfYear.NOVEMBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.DECEMBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30 + 31);
    }

    public void test_getMonthEndDayOfYear_leapYear() {
        assertEquals(MonthOfYear.JANUARY.getMonthEndDayOfYear(true), 31);
        assertEquals(MonthOfYear.FEBRUARY.getMonthEndDayOfYear(true), 31 + 29);
        assertEquals(MonthOfYear.MARCH.getMonthEndDayOfYear(true), 31 + 29 + 31);
        assertEquals(MonthOfYear.APRIL.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30);
        assertEquals(MonthOfYear.MAY.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31);
        assertEquals(MonthOfYear.JUNE.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.JULY.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31);
        assertEquals(MonthOfYear.AUGUST.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(MonthOfYear.SEPTEMBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(MonthOfYear.OCTOBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(MonthOfYear.NOVEMBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
        assertEquals(MonthOfYear.DECEMBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30 + 31);
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
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(MonthOfYear.JANUARY.toString(), "JANUARY");
        assertEquals(MonthOfYear.FEBRUARY.toString(), "FEBRUARY");
        assertEquals(MonthOfYear.MARCH.toString(), "MARCH");
        assertEquals(MonthOfYear.APRIL.toString(), "APRIL");
        assertEquals(MonthOfYear.MAY.toString(), "MAY");
        assertEquals(MonthOfYear.JUNE.toString(), "JUNE");
        assertEquals(MonthOfYear.JULY.toString(), "JULY");
        assertEquals(MonthOfYear.AUGUST.toString(), "AUGUST");
        assertEquals(MonthOfYear.SEPTEMBER.toString(), "SEPTEMBER");
        assertEquals(MonthOfYear.OCTOBER.toString(), "OCTOBER");
        assertEquals(MonthOfYear.NOVEMBER.toString(), "NOVEMBER");
        assertEquals(MonthOfYear.DECEMBER.toString(), "DECEMBER");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    public void test_enum() {
        assertEquals(MonthOfYear.valueOf("JANUARY"), MonthOfYear.JANUARY);
        assertEquals(MonthOfYear.values()[0], MonthOfYear.JANUARY);
    }

}
