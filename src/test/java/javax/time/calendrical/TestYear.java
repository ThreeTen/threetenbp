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
package javax.time.calendrical;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.Clock;
import javax.time.Instant;
import javax.time.LocalDate;
import javax.time.LocalTime;
import javax.time.Month;
import javax.time.OffsetDateTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.CalendricalFormatter;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.MonthDay;
import javax.time.calendrical.Year;
import javax.time.calendrical.YearMonth;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test Year.
 */
@Test
public class TestYear {

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(Year.class));
        assertTrue(Comparable.class.isAssignableFrom(Year.class));
        assertTrue(DateTimeAdjuster.class.isAssignableFrom(Year.class));
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void now() {
        Year expected = Year.now(Clock.systemDefaultZone());
        Year test = Year.now();
        for (int i = 0; i < 100; i++) {
            if (expected.equals(test)) {
                return;
            }
            expected = Year.now(Clock.systemDefaultZone());
            test = Year.now();
        }
        assertEquals(test, expected);
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void now_Clock() {
        Instant instant = OffsetDateTime.of(2010, 12, 31, 0, 0, ZoneOffset.UTC).toInstant();
        Clock clock = Clock.fixed(instant, ZoneId.UTC);
        Year test = Year.now(clock);
        assertEquals(test.getValue(), 2010);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void now_Clock_nullClock() {
        Year.now(null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_int_singleton() {
        for (int i = -4; i <= 2104; i++) {
            Year test = Year.of(i);
            assertEquals(test.getValue(), i);
            assertEquals(Year.of(i), test);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_int_tooLow() {
        Year.of(Year.MIN_YEAR - 1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_int_tooHigh() {
        Year.of(Year.MAX_YEAR + 1);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(Year.from(LocalDate.of(2007, 7, 15)), Year.of(2007));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        Year.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        Year.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
//    @DataProvider(name="goodParseData")
//    Object[][] provider_goodParseData() {
//        return new Object[][] {
//                {"0000", Year.of(0)},
//                {"9999", Year.of(9999)},
//                {"2000", Year.of(2000)},
//                
//                {"+12345678", Year.of(12345678)},
//                {"+123456", Year.of(123456)},
//                {"-1234", Year.of(-1234)},
//                {"-12345678", Year.of(-12345678)},
//                
//                {"+" + Year.MAX_YEAR, Year.of(Year.MAX_YEAR)},
//                {"" + Year.MIN_YEAR, Year.of(Year.MIN_YEAR)},
//        };
//    }
//
//    @Test(dataProvider="goodParseData", groups={"tck"})
//    public void factory_parse_success(String text, Year expected) {
//        Year year = Year.parse(text);
//        assertEquals(year, expected);
//    }
//
//    @DataProvider(name="badParseData")
//    Object[][] provider_badParseData() {
//        return new Object[][] {
//                {"", 0},
//                {"-00", 1},
//                {"--01-0", 1},
//                {"A01", 0},
//                {"200", 0},
//                {"2009/12", 4},
//                
//                {"-0000-10", 0},
//                {"-12345678901-10", 11},
//                {"+1-10", 1},
//                {"+12-10", 1},
//                {"+123-10", 1},
//                {"+1234-10", 0},
//                {"12345-10", 0},
//                {"+12345678901-10", 11},
//        };
//    }
//
//    @Test(dataProvider="badParseData", expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_fail(String text, int pos) {
//        try {
//            Year.parse(text);
//            fail(String.format("Parse should have failed for %s at position %d", text, pos));
//        } catch (CalendricalParseException ex) {
//            assertEquals(ex.getParsedString(), text);
//            assertEquals(ex.getErrorIndex(), pos);
//            throw ex;
//        }
//    }
//
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_parse_nullText() {
        Year.parse(null);
    }

    //-----------------------------------------------------------------------
    // parse(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_parse_formatter() {
        final Year date = Year.of(2010);
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                throw new AssertionError();
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object parse(CharSequence text, Class type) {
                return date;
            }
        };
        Year test = Year.parse("ANY", f);
        assertEquals(test, date);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_parse_formatter_nullText() {
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                throw new AssertionError();
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object parse(CharSequence text, Class type) {
                assertEquals(text, null);
                throw new NullPointerException();
            }
        };
        Year.parse((String) null, f);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_parse_formatter_nullFormatter() {
        Year.parse("ANY", null);
    }

    //-----------------------------------------------------------------------
    // isLeap()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isLeap() {
        assertEquals(Year.of(1999).isLeap(), false);
        assertEquals(Year.of(2000).isLeap(), true);
        assertEquals(Year.of(2001).isLeap(), false);
        
        assertEquals(Year.of(2007).isLeap(), false);
        assertEquals(Year.of(2008).isLeap(), true);
        assertEquals(Year.of(2009).isLeap(), false);
        assertEquals(Year.of(2010).isLeap(), false);
        assertEquals(Year.of(2011).isLeap(), false);
        assertEquals(Year.of(2012).isLeap(), true);
        
        assertEquals(Year.of(2095).isLeap(), false);
        assertEquals(Year.of(2096).isLeap(), true);
        assertEquals(Year.of(2097).isLeap(), false);
        assertEquals(Year.of(2098).isLeap(), false);
        assertEquals(Year.of(2099).isLeap(), false);
        assertEquals(Year.of(2100).isLeap(), false);
        assertEquals(Year.of(2101).isLeap(), false);
        assertEquals(Year.of(2102).isLeap(), false);
        assertEquals(Year.of(2103).isLeap(), false);
        assertEquals(Year.of(2104).isLeap(), true);
        assertEquals(Year.of(2105).isLeap(), false);
        
        assertEquals(Year.of(-500).isLeap(), false);
        assertEquals(Year.of(-400).isLeap(), true);
        assertEquals(Year.of(-300).isLeap(), false);
        assertEquals(Year.of(-200).isLeap(), false);
        assertEquals(Year.of(-100).isLeap(), false);
        assertEquals(Year.of(0).isLeap(), true);
        assertEquals(Year.of(100).isLeap(), false);
        assertEquals(Year.of(200).isLeap(), false);
        assertEquals(Year.of(300).isLeap(), false);
        assertEquals(Year.of(400).isLeap(), true);
        assertEquals(Year.of(500).isLeap(), false);
    }

    //-----------------------------------------------------------------------
    // next()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_next() {
        assertEquals(Year.of(2007).next(), Year.of(2008));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_next_max() {
        Year.of(Year.MAX_YEAR).next();
    }

    //-----------------------------------------------------------------------
    // previous()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_previous() {
        assertEquals(Year.of(2007).previous(), Year.of(2006));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_previous_min() {
        Year.of(Year.MIN_YEAR).previous();
    }

//    //-----------------------------------------------------------------------
//    // plus(PeriodProvider)
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_plus_PeriodProvider() {
//        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
//        assertEquals(Year.of(2007).plus(provider), Year.of(2008));
//    }
//
//    @Test(groups={"tck"})
//    public void test_plus_PeriodProvider_normalized() {
//        PeriodProvider provider = PeriodFields.of(5, DECADES).with(3, YEARS).with(25, MONTHS);
//        assertEquals(Year.of(2007).plus(provider), Year.of(2007 + 50 + 3));  // months ignored
//    }
//
//    @Test(groups={"tck"})
//    public void test_plus_PeriodProvider_otherFieldsIgnored() {
//        PeriodProvider provider = Period.of(1, 27, 3, 4, 5, 6, 7);
//        assertEquals(Year.of(2007).plus(provider), Year.of(2008));  // months ignored
//    }
//
//    @Test(groups={"implementation"})
//    public void test_plus_PeriodProvider_zero_same() {
//        Year base = Year.of(2007);
//        assertSame(base.plus(Period.ZERO), base);
//    }
//    
//    @Test(groups={"tck"})
//    public void test_plus_PeriodProvider_zero_equal() {
//        Year base = Year.of(2007);
//        assertEquals(base.plus(Period.ZERO), base);
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_plus_PeriodProvider_invalidPeriod() {
//        PeriodProvider provider = PeriodField.of(20, MockOtherChronology.OTHER_MONTHS);
//        Year.of(2010).plus(provider);
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
//    public void test_plus_PeriodProvider_bigPeriod() {
//        long years = 20L + Integer.MAX_VALUE;
//        PeriodProvider provider = PeriodField.of(years, YEARS);
//        Year.of(-40).plus(provider);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_plus_PeriodProvider_null() {
//        Year.of(2007).plus((PeriodProvider) null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_plus_PeriodProvider_badProvider() {
//        Year.of(2007).plus(new MockPeriodProviderReturnsNull());
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_plus_PeriodProvider_invalidTooLarge() {
//        PeriodProvider provider = Period.ofYears(1);
//        Year.of(Year.MAX_YEAR).plus(provider);
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_plus_PeriodProvider_invalidTooSmall() {
//        PeriodProvider provider = Period.ofYears(-1);
//        Year.of(Year.MIN_YEAR).plus(provider);
//    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusYears() {
        assertEquals(Year.of(2007).plusYears(-1), Year.of(2006));
        assertEquals(Year.of(2007).plusYears(0), Year.of(2007));
        assertEquals(Year.of(2007).plusYears(1), Year.of(2008));
        assertEquals(Year.of(2007).plusYears(2), Year.of(2009));
        
        assertEquals(Year.of(Year.MAX_YEAR - 1).plusYears(1), Year.of(Year.MAX_YEAR));
        assertEquals(Year.of(Year.MAX_YEAR).plusYears(0), Year.of(Year.MAX_YEAR));
        
        assertEquals(Year.of(Year.MIN_YEAR + 1).plusYears(-1), Year.of(Year.MIN_YEAR));
        assertEquals(Year.of(Year.MIN_YEAR).plusYears(0), Year.of(Year.MIN_YEAR));
    }

    @Test(groups={"implementation"})
    public void test_plusYear_zero_same() {
        Year base = Year.of(2007);
        assertSame(base.plusYears(0), base);
    }
    
    @Test(groups={"tck"})
    public void test_plusYear_zero_equals() {
        Year base = Year.of(2007);
        assertEquals(base.plusYears(0), base);
    }

    @Test(groups={"tck"})
    public void test_plusYears_big() {
        long years = 20L + Year.MAX_YEAR;
        assertEquals(Year.of(-40).plusYears(years), Year.of((int) (-40L + years)));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_max() {
        Year.of(Year.MAX_YEAR).plusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_maxLots() {
        Year.of(Year.MAX_YEAR).plusYears(1000);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_min() {
        Year.of(Year.MIN_YEAR).plusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_minLots() {
        Year.of(Year.MIN_YEAR).plusYears(-1000);
    }

//    //-----------------------------------------------------------------------
//    // minus(PeriodProvider)
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_minus_PeriodProvider() {
//        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
//        assertEquals(Year.of(2007).minus(provider), Year.of(2006));
//    }
//
//    @Test(groups={"tck"})
//    public void test_minus_PeriodProvider_normalized() {
//        PeriodProvider provider = PeriodFields.of(5, DECADES).with(3, YEARS).with(25, MONTHS);
//        assertEquals(Year.of(2007).minus(provider), Year.of(2007 - 50 - 3));  // months ignored
//    }
//
//    @Test(groups={"tck"})
//    public void test_minus_PeriodProvider_otherFieldsIgnored() {
//        PeriodProvider provider = Period.of(1, 27, 3, 4, 5, 6, 7);
//        assertEquals(Year.of(2007).minus(provider), Year.of(2006));  // months ignored
//    }
//
//    @Test(groups={"implementation"})
//    public void test_minus_PeriodProvider_zero_same() {
//        Year base = Year.of(2007);
//        assertSame(base.minus(Period.ZERO), base);
//    }
//    
//    @Test(groups={"tck"})
//    public void test_minus_PeriodProvider_zero_equals() {
//        Year base = Year.of(2007);
//        assertEquals(base.minus(Period.ZERO), base);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_minus_PeriodProvider_null() {
//        Year.of(2007).minus((PeriodProvider) null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_minus_PeriodProvider_badProvider() {
//        Year.of(2007).minus(new MockPeriodProviderReturnsNull());
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_minus_PeriodProvider_invalidPeriod() {
//        PeriodProvider provider = PeriodField.of(20, MockOtherChronology.OTHER_MONTHS);
//        Year.of(2010).minus(provider);
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
//    public void test_minus_PeriodProvider_bigPeriod() {
//        long years = 20L + Integer.MAX_VALUE;
//        PeriodProvider provider = PeriodField.of(years, YEARS);
//        Year.of(40).minus(provider);
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_minus_PeriodProvider_invalidTooLarge() {
//        PeriodProvider provider = Period.ofYears(-1);
//        Year.of(Year.MAX_YEAR).minus(provider);
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_minus_PeriodProvider_invalidTooSmall() {
//        PeriodProvider provider = Period.ofYears(1);
//        Year.of(Year.MIN_YEAR).minus(provider);
//    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusYears() {
        assertEquals(Year.of(2007).minusYears(-1), Year.of(2008));
        assertEquals(Year.of(2007).minusYears(0), Year.of(2007));
        assertEquals(Year.of(2007).minusYears(1), Year.of(2006));
        assertEquals(Year.of(2007).minusYears(2), Year.of(2005));
        
        assertEquals(Year.of(Year.MAX_YEAR - 1).minusYears(-1), Year.of(Year.MAX_YEAR));
        assertEquals(Year.of(Year.MAX_YEAR).minusYears(0), Year.of(Year.MAX_YEAR));
        
        assertEquals(Year.of(Year.MIN_YEAR + 1).minusYears(1), Year.of(Year.MIN_YEAR));
        assertEquals(Year.of(Year.MIN_YEAR).minusYears(0), Year.of(Year.MIN_YEAR));
    }

    @Test(groups={"implementation"})
    public void test_minusYear_zero_same() {
        Year base = Year.of(2007);
        assertSame(base.minusYears(0), base);
    }
    
    @Test(groups={"tck"})
    public void test_minusYear_zero_equals() {
        Year base = Year.of(2007);
        assertEquals(base.minusYears(0), base);
    }

    @Test(groups={"tck"})
    public void test_minusYears_big() {
        long years = 20L + Year.MAX_YEAR;
        assertEquals(Year.of(40).minusYears(years), Year.of((int) (40L - years)));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_max() {
        Year.of(Year.MAX_YEAR).minusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_maxLots() {
        Year.of(Year.MAX_YEAR).minusYears(-1000);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_min() {
        Year.of(Year.MIN_YEAR).minusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_minLots() {
        Year.of(Year.MIN_YEAR).minusYears(1000);
    }

    //-----------------------------------------------------------------------
    // doAdjustment()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_adjustDate() {
        LocalDate base = LocalDate.of(2007, 2, 12);
        for (int i = -4; i <= 2104; i++) {
            AdjustableDateTime result = Year.of(i).doAdjustment(base);
            assertEquals(result, LocalDate.of(i, 2, 12));
        }
    }

    @Test(groups={"tck"})
    public void test_adjustDate_resolve() {
        Year test = Year.of(2011);
        assertEquals(test.doAdjustment(LocalDate.of(2012, 2, 29)), LocalDate.of(2011, 2, 28));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_adjustDate_nullLocalDate() {
        Year test = Year.of(1);
        test.doAdjustment((LocalDate) null);
    }

    //-----------------------------------------------------------------------
    // length()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_length() {
        assertEquals(Year.of(1999).length(), 365);
        assertEquals(Year.of(2000).length(), 366);
        assertEquals(Year.of(2001).length(), 365);
        
        assertEquals(Year.of(2007).length(), 365);
        assertEquals(Year.of(2008).length(), 366);
        assertEquals(Year.of(2009).length(), 365);
        assertEquals(Year.of(2010).length(), 365);
        assertEquals(Year.of(2011).length(), 365);
        assertEquals(Year.of(2012).length(), 366);
        
        assertEquals(Year.of(2095).length(), 365);
        assertEquals(Year.of(2096).length(), 366);
        assertEquals(Year.of(2097).length(), 365);
        assertEquals(Year.of(2098).length(), 365);
        assertEquals(Year.of(2099).length(), 365);
        assertEquals(Year.of(2100).length(), 365);
        assertEquals(Year.of(2101).length(), 365);
        assertEquals(Year.of(2102).length(), 365);
        assertEquals(Year.of(2103).length(), 365);
        assertEquals(Year.of(2104).length(), 366);
        assertEquals(Year.of(2105).length(), 365);
        
        assertEquals(Year.of(-500).length(), 365);
        assertEquals(Year.of(-400).length(), 366);
        assertEquals(Year.of(-300).length(), 365);
        assertEquals(Year.of(-200).length(), 365);
        assertEquals(Year.of(-100).length(), 365);
        assertEquals(Year.of(0).length(), 366);
        assertEquals(Year.of(100).length(), 365);
        assertEquals(Year.of(200).length(), 365);
        assertEquals(Year.of(300).length(), 365);
        assertEquals(Year.of(400).length(), 366);
        assertEquals(Year.of(500).length(), 365);
    }

    //-----------------------------------------------------------------------
    // isValidMonthDay(Month)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isValidMonthDay_june() {
        Year test = Year.of(2007);
        MonthDay monthDay = MonthDay.of(6, 30);
        assertEquals(test.isValidMonthDay(monthDay), true);
    }

    @Test(groups={"tck"})
    public void test_isValidMonthDay_febNonLeap() {
        Year test = Year.of(2007);
        MonthDay monthDay = MonthDay.of(2, 29);
        assertEquals(test.isValidMonthDay(monthDay), false);
    }

    @Test(groups={"tck"})
    public void test_isValidMonthDay_febLeap() {
        Year test = Year.of(2008);
        MonthDay monthDay = MonthDay.of(2, 29);
        assertEquals(test.isValidMonthDay(monthDay), true);
    }

    @Test(groups={"tck"})
    public void test_isValidMonthDay_null() {
        Year test = Year.of(2008);
        assertEquals(test.isValidMonthDay(null), false);
    }

//    //-----------------------------------------------------------------------
//    // getEstimatedEra()
//    //-----------------------------------------------------------------------
//    public void test_getEstimatedEra() {
//        assertEquals(Year.isoYear(2).getEstimatedEra(), Era.AD);
//        assertEquals(Year.isoYear(1).getEstimatedEra(), Era.AD);
//        assertEquals(Year.isoYear(0).getEstimatedEra(), Era.BC);
//        assertEquals(Year.isoYear(-1).getEstimatedEra(), Era.BC);
//    }
//
//    //-----------------------------------------------------------------------
//    // getYearOfEstimatedEra()
//    //-----------------------------------------------------------------------
//    public void test_getYearOfEstimatedEra() {
//        assertEquals(Year.isoYear(2).getYearOfEstimatedEra(), 2);
//        assertEquals(Year.isoYear(1).getYearOfEstimatedEra(), 1);
//        assertEquals(Year.isoYear(0).getYearOfEstimatedEra(), 1);
//        assertEquals(Year.isoYear(-1).getYearOfEstimatedEra(), 2);
//    }
//
//    //-----------------------------------------------------------------------
//    // getISOCentury()
//    //-----------------------------------------------------------------------
//    public void test_getISOCentury() {
//        assertEquals(Year.isoYear(2008).getISOCentury(), 20);
//        assertEquals(Year.isoYear(101).getISOCentury(), 1);
//        assertEquals(Year.isoYear(100).getISOCentury(), 1);
//        assertEquals(Year.isoYear(99).getISOCentury(), 0);
//        assertEquals(Year.isoYear(1).getISOCentury(), 0);
//        assertEquals(Year.isoYear(0).getISOCentury(), 0);
//        assertEquals(Year.isoYear(-1).getISOCentury(), 0);
//        assertEquals(Year.isoYear(-99).getISOCentury(), 0);
//        assertEquals(Year.isoYear(-100).getISOCentury(), -1);
//        assertEquals(Year.isoYear(-101).getISOCentury(), -1);
//    }
//
//    //-----------------------------------------------------------------------
//    // getYearOfISOCentury()
//    //-----------------------------------------------------------------------
//    public void test_getYearOfISOCentury() {
//        assertEquals(Year.isoYear(2008).getYearOfISOCentury(), 8);
//        assertEquals(Year.isoYear(101).getYearOfISOCentury(), 1);
//        assertEquals(Year.isoYear(100).getYearOfISOCentury(), 0);
//        assertEquals(Year.isoYear(99).getYearOfISOCentury(), 99);
//        assertEquals(Year.isoYear(1).getYearOfISOCentury(), 1);
//        assertEquals(Year.isoYear(0).getYearOfISOCentury(), 0);
//        assertEquals(Year.isoYear(-1).getYearOfISOCentury(), 1);
//        assertEquals(Year.isoYear(-99).getYearOfISOCentury(), 99);
//        assertEquals(Year.isoYear(-100).getYearOfISOCentury(), 0);
//        assertEquals(Year.isoYear(-101).getYearOfISOCentury(), 1);
//    }

    //-----------------------------------------------------------------------
    // atMonth(Month)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atMonth() {
        Year test = Year.of(2008);
        assertEquals(test.atMonth(Month.JUNE), YearMonth.of(2008, 6));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atMonth_nullMonth() {
        Year test = Year.of(2008);
        test.atMonth((Month) null);
    }

    //-----------------------------------------------------------------------
    // atMonth(int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atMonth_int() {
        Year test = Year.of(2008);
        assertEquals(test.atMonth(6), YearMonth.of(2008, 6));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atMonth_int_invalidMonth() {
        Year test = Year.of(2008);
        test.atMonth(13);
    }

    //-----------------------------------------------------------------------
    // atMonthDay(Month)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atMonthDay() {
        Year test = Year.of(2008);
        assertEquals(test.atMonthDay(MonthDay.of(6, 30)), LocalDate.of(2008, 6, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atMonthDay_nullMonthDay() {
        Year test = Year.of(2008);
        test.atMonthDay((MonthDay) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atMonthDay_invalidMonthDay() {
        Year test = Year.of(2008);
        test.atMonthDay(MonthDay.of(6, 31));
    }

    //-----------------------------------------------------------------------
    // atDay(int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atDay_notLeapYear() {
        Year test = Year.of(2007);
        LocalDate expected = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= 365; i++) {
            assertEquals(test.atDay(i), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atDay_notLeapYear_day366() {
        Year test = Year.of(2007);
        test.atDay(366);
    }

    @Test(groups={"tck"})
    public void test_atDay_leapYear() {
        Year test = Year.of(2008);
        LocalDate expected = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= 366; i++) {
            assertEquals(test.atDay(i), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atDay_day0() {
        Year test = Year.of(2007);
        test.atDay(0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atDay_day367() {
        Year test = Year.of(2007);
        test.atDay(367);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_compareTo() {
        for (int i = -4; i <= 2104; i++) {
            Year a = Year.of(i);
            for (int j = -4; j <= 2104; j++) {
                Year b = Year.of(j);
                if (i < j) {
                    assertEquals(a.compareTo(b), -1);
                    assertEquals(b.compareTo(a), 1);
                    assertEquals(a.isAfter(b), false);
                    assertEquals(a.isBefore(b), true);
                    assertEquals(b.isAfter(a), true);
                    assertEquals(b.isBefore(a), false);
                } else if (i > j) {
                    assertEquals(a.compareTo(b), 1);
                    assertEquals(b.compareTo(a), -1);
                    assertEquals(a.isAfter(b), true);
                    assertEquals(a.isBefore(b), false);
                    assertEquals(b.isAfter(a), false);
                    assertEquals(b.isBefore(a), true);
                } else {
                    assertEquals(a.compareTo(b), 0);
                    assertEquals(b.compareTo(a), 0);
                    assertEquals(a.isAfter(b), false);
                    assertEquals(a.isBefore(b), false);
                    assertEquals(b.isAfter(a), false);
                    assertEquals(b.isBefore(a), false);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_compareTo_nullYear() {
        Year doy = null;
        Year test = Year.of(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_equals() {
        for (int i = -4; i <= 2104; i++) {
            Year a = Year.of(i);
            for (int j = -4; j <= 2104; j++) {
                Year b = Year.of(j);
                assertEquals(a.equals(b), i == j);
                assertEquals(a.hashCode() == b.hashCode(), i == j);
            }
        }
    }

    @Test(groups={"tck"})
    public void test_equals_same() {
        Year test = Year.of(2011);
        assertEquals(test.equals(test), true);
    }

    @Test(groups={"tck"})
    public void test_equals_nullYear() {
        Year doy = null;
        Year test = Year.of(1);
        assertEquals(test.equals(doy), false);
    }

    @Test(groups={"tck"})
    public void test_equals_incorrectType() {
        Year test = Year.of(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        for (int i = -4; i <= 2104; i++) {
            Year a = Year.of(i);
            assertEquals(a.toString(), "" + i);
        }
    }

    //-----------------------------------------------------------------------
    // toString(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString_formatter() {
        final Year date = Year.of(2010);
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                assertEquals(calendrical, date);
                return "PRINTED";
            }
            @Override
            public <T> T parse(CharSequence text, Class<T> type) {
                throw new AssertionError();
            }
        };
        String t = date.toString(f);
        assertEquals(t, "PRINTED");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_toString_formatter_null() {
        Year.of(2010).toString(null);
    }

}
