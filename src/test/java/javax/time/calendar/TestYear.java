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

import javax.time.CalendricalException;
import javax.time.period.MockPeriodProviderReturnsNull;
import javax.time.period.Period;
import javax.time.period.PeriodProvider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test Year.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestYear {

    private static final DateTimeFieldRule<Integer> RULE = ISOChronology.yearRule();

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Calendrical.class.isAssignableFrom(Year.class));
        assertTrue(Serializable.class.isAssignableFrom(Year.class));
        assertTrue(Comparable.class.isAssignableFrom(Year.class));
        assertTrue(DateAdjuster.class.isAssignableFrom(Year.class));
        assertTrue(CalendricalMatcher.class.isAssignableFrom(Year.class));
    }

    //-----------------------------------------------------------------------
    public void test_rule() {
        assertEquals(Year.rule(), RULE);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_singleton() {
        for (int i = -4; i <= 2104; i++) {
            Year test = Year.of(i);
            assertEquals(test.getValue(), i);
            assertEquals(Year.of(i), test);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_tooLow() {
        if (Year.MIN_YEAR == Integer.MIN_VALUE) {
            return;
        }
        Year.of(Year.MIN_YEAR - 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_int_tooHigh() {
        if (Year.MAX_YEAR == Integer.MAX_VALUE) {
            throw new IllegalCalendarFieldValueException("", RULE);
        }
        Year.of(Year.MAX_YEAR + 1);
    }

    //-----------------------------------------------------------------------
    public void test_factory_Calendrical() {
        for (int i = -4; i <= 2104; i++) {  // Jan
            assertEquals(Year.from(LocalDate.of(i, 1, 1)).getValue(), i);
        }
    }

    @Test(expectedExceptions=UnsupportedRuleException.class)
    public void test_factory_Calendrical_unsupported() {
        Year.from(DateTimeFields.fields());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_nullCalendrical() {
        Year.from((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // get(CalendricalField)
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(Year.of(1999).get(RULE), (Integer) 1999);
        assertEquals(Year.of(1999).get(MockDecadeOfCenturyFieldRule.INSTANCE), (Integer) 9);
    }

    public void test_get_unsupportedField() {
        assertEquals(Year.of(1999).get(ISOChronology.weekBasedYearRule()), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_get_null() {
        Year.of(1999).get((CalendricalRule<?>) null);
    }

    //-----------------------------------------------------------------------
    // isLeap()
    //-----------------------------------------------------------------------
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
    public void test_next() {
        assertEquals(Year.of(2007).next(), Year.of(2008));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_next_max() {
        Year.of(Year.MAX_YEAR).next();
    }

    //-----------------------------------------------------------------------
    // nextLeap()
    //-----------------------------------------------------------------------
    public void test_nextLeap() {
        assertEquals(Year.of(2007).nextLeap(), Year.of(2008));
        assertEquals(Year.of(2008).nextLeap(), Year.of(2012));
        assertEquals(Year.of(2009).nextLeap(), Year.of(2012));
        assertEquals(Year.of(2010).nextLeap(), Year.of(2012));
        assertEquals(Year.of(2011).nextLeap(), Year.of(2012));
        assertEquals(Year.of(2012).nextLeap(), Year.of(2016));
        
        assertEquals(Year.of(2096).nextLeap(), Year.of(2104));
        assertEquals(Year.of(2097).nextLeap(), Year.of(2104));
        assertEquals(Year.of(2098).nextLeap(), Year.of(2104));
        assertEquals(Year.of(2099).nextLeap(), Year.of(2104));
        assertEquals(Year.of(2100).nextLeap(), Year.of(2104));
        assertEquals(Year.of(2101).nextLeap(), Year.of(2104));
        assertEquals(Year.of(2102).nextLeap(), Year.of(2104));
        assertEquals(Year.of(2103).nextLeap(), Year.of(2104));
        assertEquals(Year.of(2104).nextLeap(), Year.of(2108));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_nextLeap_max() {
        Year.of(Year.MAX_YEAR).nextLeap();
    }

    //-----------------------------------------------------------------------
    // previous()
    //-----------------------------------------------------------------------
    public void test_previous() {
        assertEquals(Year.of(2007).previous(), Year.of(2006));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_previous_min() {
        Year.of(Year.MIN_YEAR).previous();
    }

    //-----------------------------------------------------------------------
    // previousLeap()
    //-----------------------------------------------------------------------
    public void test_previousLeap() {
        assertEquals(Year.of(2013).previousLeap(), Year.of(2012));
        assertEquals(Year.of(2012).previousLeap(), Year.of(2008));
        assertEquals(Year.of(2011).previousLeap(), Year.of(2008));
        assertEquals(Year.of(2010).previousLeap(), Year.of(2008));
        assertEquals(Year.of(2009).previousLeap(), Year.of(2008));
        assertEquals(Year.of(2008).previousLeap(), Year.of(2004));
        assertEquals(Year.of(2007).previousLeap(), Year.of(2004));
        
        assertEquals(Year.of(2105).previousLeap(), Year.of(2104));
        assertEquals(Year.of(2104).previousLeap(), Year.of(2096));
        assertEquals(Year.of(2103).previousLeap(), Year.of(2096));
        assertEquals(Year.of(2102).previousLeap(), Year.of(2096));
        assertEquals(Year.of(2101).previousLeap(), Year.of(2096));
        assertEquals(Year.of(2100).previousLeap(), Year.of(2096));
        assertEquals(Year.of(2099).previousLeap(), Year.of(2096));
        assertEquals(Year.of(2098).previousLeap(), Year.of(2096));
        assertEquals(Year.of(2097).previousLeap(), Year.of(2096));
        assertEquals(Year.of(2096).previousLeap(), Year.of(2092));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_previousLeap_min() {
        Year.of(Year.MIN_YEAR).previousLeap();
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertEquals(Year.of(2007).plus(provider), Year.of(2008));
    }

    public void test_plus_PeriodProvider_otherFieldsIgnored() {
        PeriodProvider provider = Period.of(1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 6, 7);
        assertEquals(Year.of(2007).plus(provider), Year.of(2008));
    }

    public void test_plus_PeriodProvider_zero() {
        Year base = Year.of(2007);
        assertSame(base.plus(Period.ZERO), base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        Year.of(2007).plus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_badProvider() {
        Year.of(2007).plus(new MockPeriodProviderReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plus_PeriodProvider_invalidTooLarge() {
        PeriodProvider provider = Period.years(1);
        Year.of(Year.MAX_YEAR).plus(provider);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plus_PeriodProvider_invalidTooSmall() {
        PeriodProvider provider = Period.years(-1);
        Year.of(Year.MIN_YEAR).plus(provider);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
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

    public void test_plusYear_zero() {
        Year base = Year.of(2007);
        assertSame(base.plusYears(0), base);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_max() {
        Year.of(Year.MAX_YEAR).plusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_maxLots() {
        Year.of(Year.MAX_YEAR).plusYears(1000);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_min() {
        Year.of(Year.MIN_YEAR).plusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plusYears_minLots() {
        Year.of(Year.MIN_YEAR).plusYears(-1000);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
        assertEquals(Year.of(2007).minus(provider), Year.of(2006));
    }

    public void test_minus_PeriodProvider_otherFieldsIgnored() {
        PeriodProvider provider = Period.of(1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 6, 7);
        assertEquals(Year.of(2007).minus(provider), Year.of(2006));
    }

    public void test_minus_PeriodProvider_zero() {
        Year base = Year.of(2007);
        assertSame(base.minus(Period.ZERO), base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_null() {
        Year.of(2007).minus((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_badProvider() {
        Year.of(2007).minus(new MockPeriodProviderReturnsNull());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minus_PeriodProvider_invalidTooLarge() {
        PeriodProvider provider = Period.years(-1);
        Year.of(Year.MAX_YEAR).minus(provider);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minus_PeriodProvider_invalidTooSmall() {
        PeriodProvider provider = Period.years(1);
        Year.of(Year.MIN_YEAR).minus(provider);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
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

    public void test_minusYear_zero() {
        Year base = Year.of(2007);
        assertSame(base.minusYears(0), base);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_max() {
        Year.of(Year.MAX_YEAR).minusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_maxLots() {
        Year.of(Year.MAX_YEAR).minusYears(-1000);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_min() {
        Year.of(Year.MIN_YEAR).minusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_minusYears_minLots() {
        Year.of(Year.MIN_YEAR).minusYears(1000);
    }

    //-----------------------------------------------------------------------
    // adjustDate(LocalDate)
    //-----------------------------------------------------------------------
    public void test_adjustDate() {
        LocalDate base = LocalDate.of(2007, 2, 12);
        for (int i = -4; i <= 2104; i++) {
            LocalDate result = Year.of(i).adjustDate(base);
            assertEquals(result, LocalDate.of(i, 2, 12));
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_nullLocalDate() {
        Year test = Year.of(1);
        test.adjustDate((LocalDate) null);
    }

    //-----------------------------------------------------------------------
    // adjustDate(LocalDate,DateResolver)
    //-----------------------------------------------------------------------
    public void test_adjustDate_strictResolver() {
        LocalDate base = LocalDate.of(2007, 2, 12);
        for (int i = -4; i <= 2104; i++) {
            LocalDate result = Year.of(i).adjustDate(base, DateResolvers.strict());
            assertEquals(result, LocalDate.of(i, 2, 12));
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_strictResolver_feb29() {
        LocalDate base = LocalDate.of(2008, 2, 29);
        Year test = Year.of(2007);
        try {
            test.adjustDate(base, DateResolvers.strict());
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_resolver_nullLocalDate() {
        Year test = Year.of(1);
        test.adjustDate((LocalDate) null, DateResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_resolver_nullResolver() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        Year test = Year.of(1);
        test.adjustDate(date, (DateResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_resolver_badResolver() {
        LocalDate date = LocalDate.of(2007, 1, 31);
        Year test = Year.of(2);
        test.adjustDate(date, new MockDateResolverReturnsNull());
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical(Calendrical)
    //-----------------------------------------------------------------------
    public void test_matchesCalendrical_notLeapYear() {
        LocalDate work = LocalDate.of(2007, 3, 2);
        for (int i = -4; i <= 2104; i++) {
            for (int j = -4; j <= 2104; j++) {
                Year test = Year.of(j);
                assertEquals(test.matchesCalendrical(work), work.getYear() == j);
            }
            work = work.plusYears(1);
        }
    }

    public void test_matchesCalendrical_noData() {
        assertEquals(Year.of(2009).matchesCalendrical(LocalTime.of(12, 30)), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesCalendrical_nullLocalDate() {
        Year test = Year.of(1);
        test.matchesCalendrical((LocalDate) null);
    }

    //-----------------------------------------------------------------------
    // lengthInDays()
    //-----------------------------------------------------------------------
    public void test_lengthInDays() {
        assertEquals(Year.of(1999).lengthInDays(), 365);
        assertEquals(Year.of(2000).lengthInDays(), 366);
        assertEquals(Year.of(2001).lengthInDays(), 365);
        
        assertEquals(Year.of(2007).lengthInDays(), 365);
        assertEquals(Year.of(2008).lengthInDays(), 366);
        assertEquals(Year.of(2009).lengthInDays(), 365);
        assertEquals(Year.of(2010).lengthInDays(), 365);
        assertEquals(Year.of(2011).lengthInDays(), 365);
        assertEquals(Year.of(2012).lengthInDays(), 366);
        
        assertEquals(Year.of(2095).lengthInDays(), 365);
        assertEquals(Year.of(2096).lengthInDays(), 366);
        assertEquals(Year.of(2097).lengthInDays(), 365);
        assertEquals(Year.of(2098).lengthInDays(), 365);
        assertEquals(Year.of(2099).lengthInDays(), 365);
        assertEquals(Year.of(2100).lengthInDays(), 365);
        assertEquals(Year.of(2101).lengthInDays(), 365);
        assertEquals(Year.of(2102).lengthInDays(), 365);
        assertEquals(Year.of(2103).lengthInDays(), 365);
        assertEquals(Year.of(2104).lengthInDays(), 366);
        assertEquals(Year.of(2105).lengthInDays(), 365);
        
        assertEquals(Year.of(-500).lengthInDays(), 365);
        assertEquals(Year.of(-400).lengthInDays(), 366);
        assertEquals(Year.of(-300).lengthInDays(), 365);
        assertEquals(Year.of(-200).lengthInDays(), 365);
        assertEquals(Year.of(-100).lengthInDays(), 365);
        assertEquals(Year.of(0).lengthInDays(), 366);
        assertEquals(Year.of(100).lengthInDays(), 365);
        assertEquals(Year.of(200).lengthInDays(), 365);
        assertEquals(Year.of(300).lengthInDays(), 365);
        assertEquals(Year.of(400).lengthInDays(), 366);
        assertEquals(Year.of(500).lengthInDays(), 365);
    }

    //-----------------------------------------------------------------------
    // isValidMonthDay(MonthOfYear)
    //-----------------------------------------------------------------------
    public void test_isValidMonthDay_june() {
        Year test = Year.of(2007);
        MonthDay monthDay = MonthDay.of(6, 30);
        assertEquals(test.isValidMonthDay(monthDay), true);
    }

    public void test_isValidMonthDay_febNonLeap() {
        Year test = Year.of(2007);
        MonthDay monthDay = MonthDay.of(2, 29);
        assertEquals(test.isValidMonthDay(monthDay), false);
    }

    public void test_isValidMonthDay_febLeap() {
        Year test = Year.of(2008);
        MonthDay monthDay = MonthDay.of(2, 29);
        assertEquals(test.isValidMonthDay(monthDay), true);
    }

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
    // atMonth(MonthOfYear)
    //-----------------------------------------------------------------------
    public void test_atMonth() {
        Year test = Year.of(2008);
        assertEquals(test.atMonth(MonthOfYear.JUNE), YearMonth.of(2008, 6));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atMonth_nullMonth() {
        Year test = Year.of(2008);
        test.atMonth((MonthOfYear) null);
    }

    //-----------------------------------------------------------------------
    // atMonth(int)
    //-----------------------------------------------------------------------
    public void test_atMonth_int() {
        Year test = Year.of(2008);
        assertEquals(test.atMonth(6), YearMonth.of(2008, 6));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atMonth_int_invalidMonth() {
        Year test = Year.of(2008);
        try {
            test.atMonth(13);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ISOChronology.monthOfYearRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // atMonthDay(MonthOfYear)
    //-----------------------------------------------------------------------
    public void test_atMonthDay() {
        Year test = Year.of(2008);
        assertEquals(test.atMonthDay(MonthDay.of(6, 30)), LocalDate.of(2008, 6, 30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_atMonthDay_nullMonthDay() {
        Year test = Year.of(2008);
        test.atMonthDay((MonthDay) null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_atMonthDay_invalidMonthDay() {
        Year test = Year.of(2008);
        try {
            test.atMonthDay(MonthDay.of(6, 31));
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfMonthRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // atDay(int)
    //-----------------------------------------------------------------------
    public void test_atDay_notLeapYear() {
        Year test = Year.of(2007);
        LocalDate expected = LocalDate.of(2007, 1, 1);
        for (int i = 1; i <= 365; i++) {
            assertEquals(test.atDay(i), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_atDay_notLeapYear_day366() {
        Year test = Year.of(2007);
        try {
            test.atDay(366);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfYearRule());
            throw ex;
        }
    }

    public void test_atDay_leapYear() {
        Year test = Year.of(2008);
        LocalDate expected = LocalDate.of(2008, 1, 1);
        for (int i = 1; i <= 366; i++) {
            assertEquals(test.atDay(i), expected);
            expected = expected.plusDays(1);
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atDay_day0() {
        Year test = Year.of(2007);
        try {
            test.atDay(0);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfYearRule());
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atDay_day367() {
        Year test = Year.of(2007);
        try {
            test.atDay(367);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), ISOChronology.dayOfYearRule());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
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

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_nullYear() {
        Year doy = null;
        Year test = Year.of(1);
        test.compareTo(doy);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
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

    public void test_equals_nullYear() {
        Year doy = null;
        Year test = Year.of(1);
        assertEquals(test.equals(doy), false);
    }

    public void test_equals_incorrectType() {
        Year test = Year.of(1);
        assertEquals(test.equals("Incorrect type"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        for (int i = -4; i <= 2104; i++) {
            Year a = Year.of(i);
            assertEquals(a.toString(), "Year=" + i);
        }
    }

}
