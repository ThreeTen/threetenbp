/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import static javax.time.Month.JUNE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;

import javax.time.calendrical.DateTime;

import org.testng.annotations.Test;

/**
 * Test Month.
 */
@Test
public class TestMonth {

    private static final int MAX_LENGTH = 12;

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(Enum.class.isAssignableFrom(Month.class));
        assertTrue(Serializable.class.isAssignableFrom(Month.class));
        assertTrue(Comparable.class.isAssignableFrom(Month.class));
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_int_singleton() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            Month test = Month.of(i);
            assertEquals(test.getValue(), i);
        }
    }
    
    @Test(groups={"implementation"})
    public void test_factory_int_singleton_same() {
        for (int i = 1; i <= MAX_LENGTH; i++) {
            Month test = Month.of(i);
            assertSame(Month.of(i), test);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_int_tooLow() {
        Month.of(0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_int_tooHigh() {
        Month.of(13);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(Month.from(LocalDate.of(2011, 6, 6)), JUNE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        Month.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        Month.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_getText() {
//        assertEquals(Month.JANUARY.getText(TextStyle.SHORT, Locale.US), "Jan");
//    }
//
//    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
//    public void test_getText_nullStyle() {
//        Month.JANUARY.getText(null, Locale.US);
//    }
//
//    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
//    public void test_getText_nullLocale() {
//        Month.JANUARY.getText(TextStyle.FULL, null);
//    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_next() {
        assertEquals(Month.JANUARY.next(), Month.FEBRUARY);
        assertEquals(Month.FEBRUARY.next(), Month.MARCH);
        assertEquals(Month.MARCH.next(), Month.APRIL);
        assertEquals(Month.APRIL.next(), Month.MAY);
        assertEquals(Month.MAY.next(), Month.JUNE);
        assertEquals(Month.JUNE.next(), Month.JULY);
        assertEquals(Month.JULY.next(), Month.AUGUST);
        assertEquals(Month.AUGUST.next(), Month.SEPTEMBER);
        assertEquals(Month.SEPTEMBER.next(), Month.OCTOBER);
        assertEquals(Month.OCTOBER.next(), Month.NOVEMBER);
        assertEquals(Month.NOVEMBER.next(), Month.DECEMBER);
        assertEquals(Month.DECEMBER.next(), Month.JANUARY);
    }

    //-----------------------------------------------------------------------
    // previous()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_previous() {
        assertEquals(Month.JANUARY.previous(), Month.DECEMBER);
        assertEquals(Month.FEBRUARY.previous(), Month.JANUARY);
        assertEquals(Month.MARCH.previous(), Month.FEBRUARY);
        assertEquals(Month.APRIL.previous(), Month.MARCH);
        assertEquals(Month.MAY.previous(), Month.APRIL);
        assertEquals(Month.JUNE.previous(), Month.MAY);
        assertEquals(Month.JULY.previous(), Month.JUNE);
        assertEquals(Month.AUGUST.previous(), Month.JULY);
        assertEquals(Month.SEPTEMBER.previous(), Month.AUGUST);
        assertEquals(Month.OCTOBER.previous(), Month.SEPTEMBER);
        assertEquals(Month.NOVEMBER.previous(), Month.OCTOBER);
        assertEquals(Month.DECEMBER.previous(), Month.NOVEMBER);
    }

    //-----------------------------------------------------------------------
    // roll(int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_roll_january() {
        assertEquals(Month.JANUARY.roll(-12), Month.JANUARY);
        assertEquals(Month.JANUARY.roll(-11), Month.FEBRUARY);
        assertEquals(Month.JANUARY.roll(-10), Month.MARCH);
        assertEquals(Month.JANUARY.roll(-9), Month.APRIL);
        assertEquals(Month.JANUARY.roll(-8), Month.MAY);
        assertEquals(Month.JANUARY.roll(-7), Month.JUNE);
        assertEquals(Month.JANUARY.roll(-6), Month.JULY);
        assertEquals(Month.JANUARY.roll(-5), Month.AUGUST);
        assertEquals(Month.JANUARY.roll(-4), Month.SEPTEMBER);
        assertEquals(Month.JANUARY.roll(-3), Month.OCTOBER);
        assertEquals(Month.JANUARY.roll(-2), Month.NOVEMBER);
        assertEquals(Month.JANUARY.roll(-1), Month.DECEMBER);
        assertEquals(Month.JANUARY.roll(0), Month.JANUARY);
        assertEquals(Month.JANUARY.roll(1), Month.FEBRUARY);
        assertEquals(Month.JANUARY.roll(2), Month.MARCH);
        assertEquals(Month.JANUARY.roll(3), Month.APRIL);
        assertEquals(Month.JANUARY.roll(4), Month.MAY);
        assertEquals(Month.JANUARY.roll(5), Month.JUNE);
        assertEquals(Month.JANUARY.roll(6), Month.JULY);
        assertEquals(Month.JANUARY.roll(7), Month.AUGUST);
        assertEquals(Month.JANUARY.roll(8), Month.SEPTEMBER);
        assertEquals(Month.JANUARY.roll(9), Month.OCTOBER);
        assertEquals(Month.JANUARY.roll(10), Month.NOVEMBER);
        assertEquals(Month.JANUARY.roll(11), Month.DECEMBER);
        assertEquals(Month.JANUARY.roll(12), Month.JANUARY);
    }

    @Test(groups={"tck"})
    public void test_roll_july() {
        assertEquals(Month.JULY.roll(-12), Month.JULY);
        assertEquals(Month.JULY.roll(-11), Month.AUGUST);
        assertEquals(Month.JULY.roll(-10), Month.SEPTEMBER);
        assertEquals(Month.JULY.roll(-9), Month.OCTOBER);
        assertEquals(Month.JULY.roll(-8), Month.NOVEMBER);
        assertEquals(Month.JULY.roll(-7), Month.DECEMBER);
        assertEquals(Month.JULY.roll(-6), Month.JANUARY);
        assertEquals(Month.JULY.roll(-5), Month.FEBRUARY);
        assertEquals(Month.JULY.roll(-4), Month.MARCH);
        assertEquals(Month.JULY.roll(-3), Month.APRIL);
        assertEquals(Month.JULY.roll(-2), Month.MAY);
        assertEquals(Month.JULY.roll(-1), Month.JUNE);
        assertEquals(Month.JULY.roll(0), Month.JULY);
        assertEquals(Month.JULY.roll(1), Month.AUGUST);
        assertEquals(Month.JULY.roll(2), Month.SEPTEMBER);
        assertEquals(Month.JULY.roll(3), Month.OCTOBER);
        assertEquals(Month.JULY.roll(4), Month.NOVEMBER);
        assertEquals(Month.JULY.roll(5), Month.DECEMBER);
        assertEquals(Month.JULY.roll(6), Month.JANUARY);
        assertEquals(Month.JULY.roll(7), Month.FEBRUARY);
        assertEquals(Month.JULY.roll(8), Month.MARCH);
        assertEquals(Month.JULY.roll(9), Month.APRIL);
        assertEquals(Month.JULY.roll(10), Month.MAY);
        assertEquals(Month.JULY.roll(11), Month.JUNE);
        assertEquals(Month.JULY.roll(12), Month.JULY);
    }
    
    @Test(groups={"tck"})
    public void test_roll_largerThanTwelveMonths(){
    	 assertEquals(Month.JULY.roll(-13), Month.JUNE);
    	 assertEquals(Month.JULY.roll(13), Month.AUGUST);
    	 int multipleOfMaxLenghthCloserToIntMaxValue = (Integer.MAX_VALUE/MAX_LENGTH)*MAX_LENGTH;
		 assertEquals(Month.JULY.roll(multipleOfMaxLenghthCloserToIntMaxValue), Month.JULY);
    	 assertEquals(Month.JULY.roll(-(multipleOfMaxLenghthCloserToIntMaxValue)), Month.JULY);
    }

    //-----------------------------------------------------------------------
    // length(boolean)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_length_boolean_notLeapYear() {
        assertEquals(Month.JANUARY.length(false), 31);
        assertEquals(Month.FEBRUARY.length(false), 28);
        assertEquals(Month.MARCH.length(false), 31);
        assertEquals(Month.APRIL.length(false), 30);
        assertEquals(Month.MAY.length(false), 31);
        assertEquals(Month.JUNE.length(false), 30);
        assertEquals(Month.JULY.length(false), 31);
        assertEquals(Month.AUGUST.length(false), 31);
        assertEquals(Month.SEPTEMBER.length(false), 30);
        assertEquals(Month.OCTOBER.length(false), 31);
        assertEquals(Month.NOVEMBER.length(false), 30);
        assertEquals(Month.DECEMBER.length(false), 31);
    }

    @Test(groups={"tck"})
    public void test_length_boolean_leapYear() {
        assertEquals(Month.JANUARY.length(true), 31);
        assertEquals(Month.FEBRUARY.length(true), 29);
        assertEquals(Month.MARCH.length(true), 31);
        assertEquals(Month.APRIL.length(true), 30);
        assertEquals(Month.MAY.length(true), 31);
        assertEquals(Month.JUNE.length(true), 30);
        assertEquals(Month.JULY.length(true), 31);
        assertEquals(Month.AUGUST.length(true), 31);
        assertEquals(Month.SEPTEMBER.length(true), 30);
        assertEquals(Month.OCTOBER.length(true), 31);
        assertEquals(Month.NOVEMBER.length(true), 30);
        assertEquals(Month.DECEMBER.length(true), 31);
    }

    //-----------------------------------------------------------------------
    // minLength()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minLength() {
        assertEquals(Month.JANUARY.minLength(), 31);
        assertEquals(Month.FEBRUARY.minLength(), 28);
        assertEquals(Month.MARCH.minLength(), 31);
        assertEquals(Month.APRIL.minLength(), 30);
        assertEquals(Month.MAY.minLength(), 31);
        assertEquals(Month.JUNE.minLength(), 30);
        assertEquals(Month.JULY.minLength(), 31);
        assertEquals(Month.AUGUST.minLength(), 31);
        assertEquals(Month.SEPTEMBER.minLength(), 30);
        assertEquals(Month.OCTOBER.minLength(), 31);
        assertEquals(Month.NOVEMBER.minLength(), 30);
        assertEquals(Month.DECEMBER.minLength(), 31);
    }

    //-----------------------------------------------------------------------
    // maxLength()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_maxLength() {
        assertEquals(Month.JANUARY.maxLength(), 31);
        assertEquals(Month.FEBRUARY.maxLength(), 29);
        assertEquals(Month.MARCH.maxLength(), 31);
        assertEquals(Month.APRIL.maxLength(), 30);
        assertEquals(Month.MAY.maxLength(), 31);
        assertEquals(Month.JUNE.maxLength(), 30);
        assertEquals(Month.JULY.maxLength(), 31);
        assertEquals(Month.AUGUST.maxLength(), 31);
        assertEquals(Month.SEPTEMBER.maxLength(), 30);
        assertEquals(Month.OCTOBER.maxLength(), 31);
        assertEquals(Month.NOVEMBER.maxLength(), 30);
        assertEquals(Month.DECEMBER.maxLength(), 31);
    }

    //-----------------------------------------------------------------------
    // getMonthStartDayOfYear(boolean)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getMonthStartDayOfYear_notLeapYear() {
        assertEquals(Month.JANUARY.getMonthStartDayOfYear(false), 1);
        assertEquals(Month.FEBRUARY.getMonthStartDayOfYear(false), 1 + 31);
        assertEquals(Month.MARCH.getMonthStartDayOfYear(false), 1 + 31 + 28);
        assertEquals(Month.APRIL.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31);
        assertEquals(Month.MAY.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30);
        assertEquals(Month.JUNE.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31);
        assertEquals(Month.JULY.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30);
        assertEquals(Month.AUGUST.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31);
        assertEquals(Month.SEPTEMBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(Month.OCTOBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(Month.NOVEMBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(Month.DECEMBER.getMonthStartDayOfYear(false), 1 + 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
    }

    @Test(groups={"tck"})
    public void test_getMonthStartDayOfYear_leapYear() {
        assertEquals(Month.JANUARY.getMonthStartDayOfYear(true), 1);
        assertEquals(Month.FEBRUARY.getMonthStartDayOfYear(true), 1 + 31);
        assertEquals(Month.MARCH.getMonthStartDayOfYear(true), 1 + 31 + 29);
        assertEquals(Month.APRIL.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31);
        assertEquals(Month.MAY.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30);
        assertEquals(Month.JUNE.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31);
        assertEquals(Month.JULY.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30);
        assertEquals(Month.AUGUST.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31);
        assertEquals(Month.SEPTEMBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(Month.OCTOBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(Month.NOVEMBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(Month.DECEMBER.getMonthStartDayOfYear(true), 1 + 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
    }

    //-----------------------------------------------------------------------
    // getMonthEndDayOfYear(boolean)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getMonthEndDayOfYear_notLeapYear() {
        assertEquals(Month.JANUARY.getMonthEndDayOfYear(false), 31);
        assertEquals(Month.FEBRUARY.getMonthEndDayOfYear(false), 31 + 28);
        assertEquals(Month.MARCH.getMonthEndDayOfYear(false), 31 + 28 + 31);
        assertEquals(Month.APRIL.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30);
        assertEquals(Month.MAY.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31);
        assertEquals(Month.JUNE.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30);
        assertEquals(Month.JULY.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31);
        assertEquals(Month.AUGUST.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(Month.SEPTEMBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(Month.OCTOBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(Month.NOVEMBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
        assertEquals(Month.DECEMBER.getMonthEndDayOfYear(false), 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30 + 31);
    }

    @Test(groups={"tck"})
    public void test_getMonthEndDayOfYear_leapYear() {
        assertEquals(Month.JANUARY.getMonthEndDayOfYear(true), 31);
        assertEquals(Month.FEBRUARY.getMonthEndDayOfYear(true), 31 + 29);
        assertEquals(Month.MARCH.getMonthEndDayOfYear(true), 31 + 29 + 31);
        assertEquals(Month.APRIL.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30);
        assertEquals(Month.MAY.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31);
        assertEquals(Month.JUNE.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30);
        assertEquals(Month.JULY.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31);
        assertEquals(Month.AUGUST.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31);
        assertEquals(Month.SEPTEMBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30);
        assertEquals(Month.OCTOBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31);
        assertEquals(Month.NOVEMBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30);
        assertEquals(Month.DECEMBER.getMonthEndDayOfYear(true), 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30 + 31);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        assertEquals(Month.JANUARY.toString(), "JANUARY");
        assertEquals(Month.FEBRUARY.toString(), "FEBRUARY");
        assertEquals(Month.MARCH.toString(), "MARCH");
        assertEquals(Month.APRIL.toString(), "APRIL");
        assertEquals(Month.MAY.toString(), "MAY");
        assertEquals(Month.JUNE.toString(), "JUNE");
        assertEquals(Month.JULY.toString(), "JULY");
        assertEquals(Month.AUGUST.toString(), "AUGUST");
        assertEquals(Month.SEPTEMBER.toString(), "SEPTEMBER");
        assertEquals(Month.OCTOBER.toString(), "OCTOBER");
        assertEquals(Month.NOVEMBER.toString(), "NOVEMBER");
        assertEquals(Month.DECEMBER.toString(), "DECEMBER");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_enum() {
        assertEquals(Month.valueOf("JANUARY"), Month.JANUARY);
        assertEquals(Month.values()[0], Month.JANUARY);
    }

}
