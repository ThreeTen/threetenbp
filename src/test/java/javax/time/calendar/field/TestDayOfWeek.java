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
package javax.time.calendar.field;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.Locale;

import javax.time.calendar.IllegalCalendarFieldValueException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DayOfWeek.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDayOfWeek {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(DayOfWeek.class));
        assertTrue(Serializable.class.isAssignableFrom(DayOfWeek.class));
        assertTrue(Comparable.class.isAssignableFrom(DayOfWeek.class));
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = 1; i <= 7; i++) {
            DayOfWeek test = DayOfWeek.dayOfWeek(i);
            assertEquals(test.getValue(), i);
            assertSame(DayOfWeek.dayOfWeek(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooLow() {
        DayOfWeek.dayOfWeek(0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_valueTooHigh() {
        DayOfWeek.dayOfWeek(8);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="localeFirstDay")
    Object[][] localeFirstDay() {
        return new Object[][] {
            {Locale.FRANCE, DayOfWeek.MONDAY},
            {new Locale("pt", "BR"), DayOfWeek.SUNDAY},
            {Locale.US,     DayOfWeek.SUNDAY},
        };
    }

    @Test(dataProvider="localeFirstDay")
    public void test_firstDayOfWeekFor(Locale locale, DayOfWeek first) {
        assertSame(DayOfWeek.firstDayOfWeekFor(locale), first);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_firstDayOfWeekFor_null() {
        DayOfWeek.firstDayOfWeekFor(null);
    }

    //-----------------------------------------------------------------------
    // getShortText()
    //-----------------------------------------------------------------------
    public void test_getShortText_US() {
        assertEquals(DayOfWeek.MONDAY.getShortText(Locale.US), "Mon");
        assertEquals(DayOfWeek.TUESDAY.getShortText(Locale.US), "Tue");
        assertEquals(DayOfWeek.WEDNESDAY.getShortText(Locale.US), "Wed");
        assertEquals(DayOfWeek.THURSDAY.getShortText(Locale.US), "Thu");
        assertEquals(DayOfWeek.FRIDAY.getShortText(Locale.US), "Fri");
        assertEquals(DayOfWeek.SATURDAY.getShortText(Locale.US), "Sat");
        assertEquals(DayOfWeek.SUNDAY.getShortText(Locale.US), "Sun");
    }

    public void test_getShortText_pt_BR() {
        Locale ptBR = new Locale("pt", "BR");
        assertEquals(DayOfWeek.MONDAY.getShortText(ptBR), "Seg");
        assertEquals(DayOfWeek.TUESDAY.getShortText(ptBR), "Ter");
        assertEquals(DayOfWeek.WEDNESDAY.getShortText(ptBR), "Qua");
        assertEquals(DayOfWeek.THURSDAY.getShortText(ptBR), "Qui");
        assertEquals(DayOfWeek.FRIDAY.getShortText(ptBR), "Sex");
        assertEquals(DayOfWeek.SATURDAY.getShortText(ptBR), "S\u00E1b");
        assertEquals(DayOfWeek.SUNDAY.getShortText(ptBR), "Dom");
    }

//    public void test_getShortText_noText() {
//        assertEquals(DayOfWeek.MONDAY.getShortText(new Locale("", "")), "1");
//        assertEquals(DayOfWeek.TUESDAY.getShortText(new Locale("", "")), "2");
//        assertEquals(DayOfWeek.WEDNESDAY.getShortText(new Locale("", "")), "3");
//        assertEquals(DayOfWeek.THURSDAY.getShortText(new Locale("", "")), "4");
//        assertEquals(DayOfWeek.FRIDAY.getShortText(new Locale("", "")), "5");
//        assertEquals(DayOfWeek.SATURDAY.getShortText(new Locale("", "")), "6");
//        assertEquals(DayOfWeek.SUNDAY.getShortText(new Locale("", "")), "7");
//    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
    public void test_getText_US() {
        assertEquals(DayOfWeek.MONDAY.getText(Locale.US), "Monday");
        assertEquals(DayOfWeek.TUESDAY.getText(Locale.US), "Tuesday");
        assertEquals(DayOfWeek.WEDNESDAY.getText(Locale.US), "Wednesday");
        assertEquals(DayOfWeek.THURSDAY.getText(Locale.US), "Thursday");
        assertEquals(DayOfWeek.FRIDAY.getText(Locale.US), "Friday");
        assertEquals(DayOfWeek.SATURDAY.getText(Locale.US), "Saturday");
        assertEquals(DayOfWeek.SUNDAY.getText(Locale.US), "Sunday");
    }

    public void test_getText_pt_BR() {
        Locale ptBR = new Locale("pt", "BR");
        assertEquals(DayOfWeek.MONDAY.getText(ptBR), "Segunda-feira");
        assertEquals(DayOfWeek.TUESDAY.getText(ptBR), "Ter\u00E7a-feira");
        assertEquals(DayOfWeek.WEDNESDAY.getText(ptBR), "Quarta-feira");
        assertEquals(DayOfWeek.THURSDAY.getText(ptBR), "Quinta-feira");
        assertEquals(DayOfWeek.FRIDAY.getText(ptBR), "Sexta-feira");
        assertEquals(DayOfWeek.SATURDAY.getText(ptBR), "S\u00E1bado");
        assertEquals(DayOfWeek.SUNDAY.getText(ptBR), "Domingo");
    }

//    public void test_getText_noText() {
//        assertEquals(DayOfWeek.MONDAY.getText(new Locale("", "")), "1");
//        assertEquals(DayOfWeek.TUESDAY.getText(new Locale("", "")), "2");
//        assertEquals(DayOfWeek.WEDNESDAY.getText(new Locale("", "")), "3");
//        assertEquals(DayOfWeek.THURSDAY.getText(new Locale("", "")), "4");
//        assertEquals(DayOfWeek.FRIDAY.getText(new Locale("", "")), "5");
//        assertEquals(DayOfWeek.SATURDAY.getText(new Locale("", "")), "6");
//        assertEquals(DayOfWeek.SUNDAY.getText(new Locale("", "")), "7");
//    }

    //-----------------------------------------------------------------------
    // is...()
    //-----------------------------------------------------------------------
    public void test_isMonday() {
        assertEquals(DayOfWeek.MONDAY.isMonday(), true);
        assertEquals(DayOfWeek.TUESDAY.isMonday(), false);
        assertEquals(DayOfWeek.WEDNESDAY.isMonday(), false);
        assertEquals(DayOfWeek.THURSDAY.isMonday(), false);
        assertEquals(DayOfWeek.FRIDAY.isMonday(), false);
        assertEquals(DayOfWeek.SATURDAY.isMonday(), false);
        assertEquals(DayOfWeek.SUNDAY.isMonday(), false);
    }

    public void test_isTuesday() {
        assertEquals(DayOfWeek.MONDAY.isTuesday(), false);
        assertEquals(DayOfWeek.TUESDAY.isTuesday(), true);
        assertEquals(DayOfWeek.WEDNESDAY.isTuesday(), false);
        assertEquals(DayOfWeek.THURSDAY.isTuesday(), false);
        assertEquals(DayOfWeek.FRIDAY.isTuesday(), false);
        assertEquals(DayOfWeek.SATURDAY.isTuesday(), false);
        assertEquals(DayOfWeek.SUNDAY.isTuesday(), false);
    }

    public void test_isWednesday() {
        assertEquals(DayOfWeek.MONDAY.isWednesday(), false);
        assertEquals(DayOfWeek.TUESDAY.isWednesday(), false);
        assertEquals(DayOfWeek.WEDNESDAY.isWednesday(), true);
        assertEquals(DayOfWeek.THURSDAY.isWednesday(), false);
        assertEquals(DayOfWeek.FRIDAY.isWednesday(), false);
        assertEquals(DayOfWeek.SATURDAY.isWednesday(), false);
        assertEquals(DayOfWeek.SUNDAY.isWednesday(), false);
    }

    public void test_isThursday() {
        assertEquals(DayOfWeek.MONDAY.isThursday(), false);
        assertEquals(DayOfWeek.TUESDAY.isThursday(), false);
        assertEquals(DayOfWeek.WEDNESDAY.isThursday(), false);
        assertEquals(DayOfWeek.THURSDAY.isThursday(), true);
        assertEquals(DayOfWeek.FRIDAY.isThursday(), false);
        assertEquals(DayOfWeek.SATURDAY.isThursday(), false);
        assertEquals(DayOfWeek.SUNDAY.isThursday(), false);
    }

    public void test_isFriday() {
        assertEquals(DayOfWeek.MONDAY.isFriday(), false);
        assertEquals(DayOfWeek.TUESDAY.isFriday(), false);
        assertEquals(DayOfWeek.WEDNESDAY.isFriday(), false);
        assertEquals(DayOfWeek.THURSDAY.isFriday(), false);
        assertEquals(DayOfWeek.FRIDAY.isFriday(), true);
        assertEquals(DayOfWeek.SATURDAY.isFriday(), false);
        assertEquals(DayOfWeek.SUNDAY.isFriday(), false);
    }

    public void test_isSaturday() {
        assertEquals(DayOfWeek.MONDAY.isSaturday(), false);
        assertEquals(DayOfWeek.TUESDAY.isSaturday(), false);
        assertEquals(DayOfWeek.WEDNESDAY.isSaturday(), false);
        assertEquals(DayOfWeek.THURSDAY.isSaturday(), false);
        assertEquals(DayOfWeek.FRIDAY.isSaturday(), false);
        assertEquals(DayOfWeek.SATURDAY.isSaturday(), true);
        assertEquals(DayOfWeek.SUNDAY.isSaturday(), false);
    }

    public void test_isSunday() {
        assertEquals(DayOfWeek.MONDAY.isSunday(), false);
        assertEquals(DayOfWeek.TUESDAY.isSunday(), false);
        assertEquals(DayOfWeek.WEDNESDAY.isSunday(), false);
        assertEquals(DayOfWeek.THURSDAY.isSunday(), false);
        assertEquals(DayOfWeek.FRIDAY.isSunday(), false);
        assertEquals(DayOfWeek.SATURDAY.isSunday(), false);
        assertEquals(DayOfWeek.SUNDAY.isSunday(), true);
    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    public void test_next() {
        assertEquals(DayOfWeek.MONDAY.next(), DayOfWeek.TUESDAY);
        assertEquals(DayOfWeek.TUESDAY.next(), DayOfWeek.WEDNESDAY);
        assertEquals(DayOfWeek.WEDNESDAY.next(), DayOfWeek.THURSDAY);
        assertEquals(DayOfWeek.THURSDAY.next(), DayOfWeek.FRIDAY);
        assertEquals(DayOfWeek.FRIDAY.next(), DayOfWeek.SATURDAY);
        assertEquals(DayOfWeek.SATURDAY.next(), DayOfWeek.SUNDAY);
        assertEquals(DayOfWeek.SUNDAY.next(), DayOfWeek.MONDAY);
    }

    //-----------------------------------------------------------------------
    // previous()
    //-----------------------------------------------------------------------
    public void test_previous() {
        assertEquals(DayOfWeek.MONDAY.previous(), DayOfWeek.SUNDAY);
        assertEquals(DayOfWeek.TUESDAY.previous(), DayOfWeek.MONDAY);
        assertEquals(DayOfWeek.WEDNESDAY.previous(), DayOfWeek.TUESDAY);
        assertEquals(DayOfWeek.THURSDAY.previous(), DayOfWeek.WEDNESDAY);
        assertEquals(DayOfWeek.FRIDAY.previous(), DayOfWeek.THURSDAY);
        assertEquals(DayOfWeek.SATURDAY.previous(), DayOfWeek.FRIDAY);
        assertEquals(DayOfWeek.SUNDAY.previous(), DayOfWeek.SATURDAY);
    }

    //-----------------------------------------------------------------------
    // roll(int)
    //-----------------------------------------------------------------------
    public void test_plusDays_monday() {
        assertEquals(DayOfWeek.MONDAY.roll(-7), DayOfWeek.MONDAY);
        assertEquals(DayOfWeek.MONDAY.roll(-6), DayOfWeek.TUESDAY);
        assertEquals(DayOfWeek.MONDAY.roll(-5), DayOfWeek.WEDNESDAY);
        assertEquals(DayOfWeek.MONDAY.roll(-4), DayOfWeek.THURSDAY);
        assertEquals(DayOfWeek.MONDAY.roll(-3), DayOfWeek.FRIDAY);
        assertEquals(DayOfWeek.MONDAY.roll(-2), DayOfWeek.SATURDAY);
        assertEquals(DayOfWeek.MONDAY.roll(-1), DayOfWeek.SUNDAY);
        assertEquals(DayOfWeek.MONDAY.roll(0), DayOfWeek.MONDAY);
        assertEquals(DayOfWeek.MONDAY.roll(1), DayOfWeek.TUESDAY);
        assertEquals(DayOfWeek.MONDAY.roll(2), DayOfWeek.WEDNESDAY);
        assertEquals(DayOfWeek.MONDAY.roll(3), DayOfWeek.THURSDAY);
        assertEquals(DayOfWeek.MONDAY.roll(4), DayOfWeek.FRIDAY);
        assertEquals(DayOfWeek.MONDAY.roll(5), DayOfWeek.SATURDAY);
        assertEquals(DayOfWeek.MONDAY.roll(6), DayOfWeek.SUNDAY);
        assertEquals(DayOfWeek.MONDAY.roll(7), DayOfWeek.MONDAY);
    }

    public void test_roll_thursday() {
        assertEquals(DayOfWeek.THURSDAY.roll(-7), DayOfWeek.THURSDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(-6), DayOfWeek.FRIDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(-5), DayOfWeek.SATURDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(-4), DayOfWeek.SUNDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(-3), DayOfWeek.MONDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(-2), DayOfWeek.TUESDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(-1), DayOfWeek.WEDNESDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(0), DayOfWeek.THURSDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(1), DayOfWeek.FRIDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(2), DayOfWeek.SATURDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(3), DayOfWeek.SUNDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(4), DayOfWeek.MONDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(5), DayOfWeek.TUESDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(6), DayOfWeek.WEDNESDAY);
        assertEquals(DayOfWeek.THURSDAY.roll(7), DayOfWeek.THURSDAY);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(DayOfWeek.MONDAY.toString(), "MONDAY");
        assertEquals(DayOfWeek.TUESDAY.toString(), "TUESDAY");
        assertEquals(DayOfWeek.WEDNESDAY.toString(), "WEDNESDAY");
        assertEquals(DayOfWeek.THURSDAY.toString(), "THURSDAY");
        assertEquals(DayOfWeek.FRIDAY.toString(), "FRIDAY");
        assertEquals(DayOfWeek.SATURDAY.toString(), "SATURDAY");
        assertEquals(DayOfWeek.SUNDAY.toString(), "SUNDAY");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    public void test_enum() {
        assertEquals(DayOfWeek.valueOf("MONDAY"), DayOfWeek.MONDAY);
        assertEquals(DayOfWeek.values()[0], DayOfWeek.MONDAY);
    }

}
