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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.i18n.HistoricChronology;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test Period.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestDatePeriod {

    private static final PeriodUnit YEARS = ISOChronology.periodYears();
    private static final PeriodUnit QUARTERS = ISOChronology.periodQuarters();
    private static final PeriodUnit MONTHS = ISOChronology.periodMonths();
    private static final PeriodUnit DAYS = ISOChronology.periodDays();
    private static final PeriodUnit HISTORIC_MONTHS = HistoricChronology.periodMonths();

    private static final BigInteger MAX_BINT = BigInteger.valueOf(Integer.MAX_VALUE);

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(PeriodProvider.class.isAssignableFrom(DatePeriod.class));
        assertTrue(Serializable.class.isAssignableFrom(DatePeriod.class));
    }

    @DataProvider(name="serialization")
    Object[][] data_serialization() {
        return new Object[][] {
            {DatePeriod.ZERO},
            {DatePeriod.ofDays(0)},
            {DatePeriod.ofDays(1)},
            {DatePeriod.of(1, 2, 3)},
        };
    }

    @Test(dataProvider="serialization")
    public void test_serialization(DatePeriod period) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(period);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        if (period.isZero()) {
            assertSame(ois.readObject(), period);
        } else {
            assertEquals(ois.readObject(), period);
        }
    }

    public void test_immutable() {
        Class<DatePeriod> cls = DatePeriod.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false && !Modifier.isTransient(field.getModifiers())) {
                assertTrue(Modifier.isPrivate(field.getModifiers()));
                assertTrue(Modifier.isFinal(field.getModifiers()));
            }
        }
        Constructor<?>[] cons = cls.getDeclaredConstructors();
        for (Constructor<?> con : cons) {
            assertTrue(Modifier.isPrivate(con.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    public void test_factory_of_intPeriodUnit() {
        assertEquals(DatePeriod.of(1, YEARS), DatePeriod.ofYears(1));
        assertEquals(DatePeriod.of(2, MONTHS), DatePeriod.ofMonths(2));
        assertEquals(DatePeriod.of(3, DAYS), DatePeriod.ofDays(3));
    }

    public void test_factory_of_intPeriodUnit_convert() {
        assertEquals(DatePeriod.of(2, QUARTERS), DatePeriod.ofMonths(6));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_of_intPeriodUnit_noConvert() {
        DatePeriod.of(1, HISTORIC_MONTHS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_of_intPeriodUnit_null() {
        DatePeriod.of(1, null);
    }

    //-----------------------------------------------------------------------
    public void factory_zeroSingleton() {
        assertSame(DatePeriod.ZERO, DatePeriod.ZERO);
        assertSame(DatePeriod.ofYears(0), DatePeriod.ZERO);
        assertSame(DatePeriod.ofMonths(0), DatePeriod.ZERO);
        assertSame(DatePeriod.ofDays(0), DatePeriod.ZERO);
        assertSame(DatePeriod.ofYearsMonths(0, 0), DatePeriod.ZERO);
        assertSame(DatePeriod.of(0, 0, 0), DatePeriod.ZERO);
        assertSame(DatePeriod.of(PeriodFields.ZERO), DatePeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    public void factory_years() {
        assertPeriod(DatePeriod.ofYears(1), 1, 0, 0);
        assertPeriod(DatePeriod.ofYears(0), 0, 0, 0);
        assertPeriod(DatePeriod.ofYears(-1), -1, 0, 0);
        assertPeriod(DatePeriod.ofYears(Integer.MAX_VALUE), Integer.MAX_VALUE, 0, 0);
        assertPeriod(DatePeriod.ofYears(Integer.MIN_VALUE), Integer.MIN_VALUE, 0, 0);
    }

    public void factory_months() {
        assertPeriod(DatePeriod.ofMonths(1), 0, 1, 0);
        assertPeriod(DatePeriod.ofMonths(0), 0, 0, 0);
        assertPeriod(DatePeriod.ofMonths(-1), 0, -1, 0);
        assertPeriod(DatePeriod.ofMonths(Integer.MAX_VALUE), 0, Integer.MAX_VALUE, 0);
        assertPeriod(DatePeriod.ofMonths(Integer.MIN_VALUE), 0, Integer.MIN_VALUE, 0);
    }

    public void factory_days() {
        assertPeriod(DatePeriod.ofDays(1), 0, 0, 1);
        assertPeriod(DatePeriod.ofDays(0), 0, 0, 0);
        assertPeriod(DatePeriod.ofDays(-1), 0, 0, -1);
        assertPeriod(DatePeriod.ofDays(Integer.MAX_VALUE), 0, 0, Integer.MAX_VALUE);
        assertPeriod(DatePeriod.ofDays(Integer.MIN_VALUE), 0, 0, Integer.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    public void factory_yearsMonths() {
        assertPeriod(DatePeriod.ofYearsMonths(1, 2), 1, 2, 0);
        assertPeriod(DatePeriod.ofYearsMonths(0, 2), 0, 2, 0);
        assertPeriod(DatePeriod.ofYearsMonths(1, 0), 1, 0, 0);
        assertPeriod(DatePeriod.ofYearsMonths(0, 0), 0, 0, 0);
        assertPeriod(DatePeriod.ofYearsMonths(-1, -2), -1, -2, 0);
    }

    //-----------------------------------------------------------------------
    public void factory_period_provider() {
        PeriodProvider provider = DatePeriod.of(1, 2, 3);
        assertPeriod(DatePeriod.of(provider), 1, 2, 3);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_period_provider_null() {
        PeriodProvider provider = null;
        DatePeriod.of(provider);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_period_badProvider() {
        PeriodProvider provider = new MockPeriodProviderReturnsNull();
        DatePeriod.of(provider);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test(dataProvider="toStringAndParse")
    public void test_parse(DatePeriod test, String expected) {
        assertEquals(test, DatePeriod.parse(expected));
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void test_parse_tooShort1() {
        DatePeriod.parse("");
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void test_parse_tooShort2() {
        DatePeriod.parse("P");
    }

//    @Test(expectedExceptions=CalendricalParseException.class)
//    public void test_parse_leadingZeros() {
//        DatePeriod.parse("P01D");  // TODO
//    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void test_parse_numberTooBig() {
        DatePeriod.parse("P8123456789D");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_nullText() {
        DatePeriod.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertEquals(DatePeriod.of(1, 2, 3).isZero(), false);
        assertEquals(DatePeriod.of(1, 2, 0).isZero(), false);
        assertEquals(DatePeriod.of(1, 0, 0).isZero(), false);
        assertEquals(DatePeriod.of(0, 2, 0).isZero(), false);
        assertEquals(DatePeriod.of(0, 0, 3).isZero(), false);
        assertEquals(DatePeriod.of(0, 0, 0).isZero(), true);
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    public void test_isPositive() {
        assertEquals(DatePeriod.of(1, 2, 3).isPositive(), true);
        assertEquals(DatePeriod.of(1, 2, 0).isPositive(), true);
        assertEquals(DatePeriod.of(1, 0, 0).isPositive(), true);
        assertEquals(DatePeriod.of(0, 2, 0).isPositive(), true);
        assertEquals(DatePeriod.of(0, 0, 3).isPositive(), true);
        assertEquals(DatePeriod.of(0, 0, 0).isPositive(), false);
        assertEquals(DatePeriod.of(-1, -2, -3).isPositive(), false);
        assertEquals(DatePeriod.of(-1, -2, 0).isPositive(), false);
        assertEquals(DatePeriod.of(-1, 0, 0).isPositive(), false);
        assertEquals(DatePeriod.of(0, -2, 0).isPositive(), false);
        assertEquals(DatePeriod.of(0, 0, -3).isPositive(), false);
        assertEquals(DatePeriod.of(1, 2, -3).isPositive(), false);
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero()
    //-----------------------------------------------------------------------
    public void test_isPositiveOrZero() {
        assertEquals(DatePeriod.of(1, 2, 3).isPositiveOrZero(), true);
        assertEquals(DatePeriod.of(1, 2, 0).isPositiveOrZero(), true);
        assertEquals(DatePeriod.of(1, 0, 0).isPositiveOrZero(), true);
        assertEquals(DatePeriod.of(0, 2, 0).isPositiveOrZero(), true);
        assertEquals(DatePeriod.of(0, 0, 3).isPositiveOrZero(), true);
        assertEquals(DatePeriod.of(0, 0, 0).isPositiveOrZero(), true);
        assertEquals(DatePeriod.of(-1, -2, -3).isPositiveOrZero(), false);
        assertEquals(DatePeriod.of(-1, -2, 0).isPositiveOrZero(), false);
        assertEquals(DatePeriod.of(-1, 0, 0).isPositiveOrZero(), false);
        assertEquals(DatePeriod.of(0, -2, 0).isPositiveOrZero(), false);
        assertEquals(DatePeriod.of(0, 0, -3).isPositiveOrZero(), false);
        assertEquals(DatePeriod.of(1, 2, -3).isPositiveOrZero(), false);
    }

    //-----------------------------------------------------------------------
    // withYears()
    //-----------------------------------------------------------------------
    public void test_withYears() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.withYears(10), 10, 2, 3);
    }

    public void test_withYears_noChange() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.withYears(1), test);
    }

    public void test_withYears_toZero() {
        DatePeriod test = DatePeriod.ofYears(1);
        assertSame(test.withYears(0), DatePeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // withMonths()
    //-----------------------------------------------------------------------
    public void test_withMonths() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.withMonths(10), 1, 10, 3);
    }

    public void test_withMonths_noChange() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.withMonths(2), test);
    }

    public void test_withMonths_toZero() {
        DatePeriod test = DatePeriod.ofMonths(1);
        assertSame(test.withMonths(0), DatePeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // withDays()
    //-----------------------------------------------------------------------
    public void test_withDays() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.withDays(10), 1, 2, 10);
    }

    public void test_withDays_noChange() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.withDays(3), test);
    }

    public void test_withDays_toZero() {
        DatePeriod test = DatePeriod.ofDays(1);
        assertSame(test.withDays(0), DatePeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_provider() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        PeriodProvider provider = DatePeriod.of(3, 3, 3);
        assertPeriod(test.plus(provider), 4, 5, 6);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_provider_null() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        PeriodProvider provider = null;
        test.plus(provider);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_badProvider() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        PeriodProvider provider = new MockPeriodProviderReturnsNull();
        test.plus(provider);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.plusYears(10), 11, 2, 3);
    }

    public void test_plusYears_noChange() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.plusYears(0), test);
    }

    public void test_plusYears_toZero() {
        DatePeriod test = DatePeriod.ofYears(-1);
        assertSame(test.plusYears(1), DatePeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusYears_overflowTooBig() {
        DatePeriod test = DatePeriod.ofYears(Integer.MAX_VALUE);
        test.plusYears(1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusYears_overflowTooSmall() {
        DatePeriod test = DatePeriod.ofYears(Integer.MIN_VALUE);
        test.plusYears(-1);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.plusMonths(10), 1, 12, 3);
    }

    public void test_plusMonths_noChange() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.plusMonths(0), test);
    }

    public void test_plusMonths_toZero() {
        DatePeriod test = DatePeriod.ofMonths(-1);
        assertSame(test.plusMonths(1), DatePeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusMonths_overflowTooBig() {
        DatePeriod test = DatePeriod.ofMonths(Integer.MAX_VALUE);
        test.plusMonths(1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusMonths_overflowTooSmall() {
        DatePeriod test = DatePeriod.ofMonths(Integer.MIN_VALUE);
        test.plusMonths(-1);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    public void test_plusDays() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.plusDays(10), 1, 2, 13);
    }

    public void test_plusDays_noChange() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.plusDays(0), test);
    }

    public void test_plusDays_toZero() {
        DatePeriod test = DatePeriod.ofDays(-1);
        assertSame(test.plusDays(1), DatePeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooBig() {
        DatePeriod test = DatePeriod.ofDays(Integer.MAX_VALUE);
        test.plusDays(1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooSmall() {
        DatePeriod test = DatePeriod.ofDays(Integer.MIN_VALUE);
        test.plusDays(-1);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_provider() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        PeriodProvider provider = DatePeriod.of(3, 3, 3);
        assertPeriod(test.minus(provider), -2, -1, 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_provider_null() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        PeriodProvider provider = null;
        test.minus(provider);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_badProvider() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        PeriodProvider provider = new MockPeriodProviderReturnsNull();
        test.minus(provider);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.minusYears(10), -9, 2, 3);
    }

    public void test_minusYears_noChange() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.minusYears(0), test);
    }

    public void test_minusYears_toZero() {
        DatePeriod test = DatePeriod.ofYears(1);
        assertSame(test.minusYears(1), DatePeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusYears_overflowTooBig() {
        DatePeriod test = DatePeriod.ofYears(Integer.MAX_VALUE);
        test.minusYears(-1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusYears_overflowTooSmall() {
        DatePeriod test = DatePeriod.ofYears(Integer.MIN_VALUE);
        test.minusYears(1);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.minusMonths(10), 1, -8, 3);
    }

    public void test_minusMonths_noChange() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.minusMonths(0), test);
    }

    public void test_minusMonths_toZero() {
        DatePeriod test = DatePeriod.ofMonths(1);
        assertSame(test.minusMonths(1), DatePeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusMonths_overflowTooBig() {
        DatePeriod test = DatePeriod.ofMonths(Integer.MAX_VALUE);
        test.minusMonths(-1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusMonths_overflowTooSmall() {
        DatePeriod test = DatePeriod.ofMonths(Integer.MIN_VALUE);
        test.minusMonths(1);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    public void test_minusDays() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.minusDays(10), 1, 2, -7);
    }

    public void test_minusDays_noChange() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.minusDays(0), test);
    }

    public void test_minusDays_toZero() {
        DatePeriod test = DatePeriod.ofDays(1);
        assertSame(test.minusDays(1), DatePeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooBig() {
        DatePeriod test = DatePeriod.ofDays(Integer.MAX_VALUE);
        test.minusDays(-1);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooSmall() {
        DatePeriod test = DatePeriod.ofDays(Integer.MIN_VALUE);
        test.minusDays(1);
    }

    //-----------------------------------------------------------------------
    // multipliedBy()
    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.multipliedBy(2), 2, 4, 6);
        assertPeriod(test.multipliedBy(-3), -3, -6, -9);
    }

    public void test_multipliedBy_zeroBase() {
        assertSame(DatePeriod.ZERO.multipliedBy(2), DatePeriod.ZERO);
    }

    public void test_multipliedBy_zero() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.multipliedBy(0), DatePeriod.ZERO);
    }

    public void test_multipliedBy_one() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertSame(test.multipliedBy(1), test);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        DatePeriod test = DatePeriod.ofYears(Integer.MAX_VALUE / 2 + 1);
        test.multipliedBy(2);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        DatePeriod test = DatePeriod.ofYears(Integer.MIN_VALUE / 2 - 1);
        test.multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    // dividedBy()
    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        DatePeriod test = DatePeriod.of(12, 12, 11);
        assertSame(DatePeriod.ZERO.dividedBy(2), DatePeriod.ZERO);
        assertSame(test.dividedBy(1), test);
        assertPeriod(test.dividedBy(2), 6, 6, 5);
        assertPeriod(test.dividedBy(-3), -4, -4, -3);
    }

    public void test_dividedBy_zeroBase() {
        assertSame(DatePeriod.ZERO.dividedBy(2), DatePeriod.ZERO);
    }

    public void test_dividedBy_one() {
        DatePeriod test = DatePeriod.of(12, 12, 12);
        assertSame(test.dividedBy(1), test);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        DatePeriod test = DatePeriod.of(12, 12, 12);
        test.dividedBy(0);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_dividedBy_zeroBase_divideByZero() {
        DatePeriod.ZERO.dividedBy(0);
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    public void test_negated() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        assertPeriod(test.negated(), -1, -2, -3);
    }

    public void test_negated_zero() {
        assertSame(DatePeriod.ZERO.negated(), DatePeriod.ZERO);
    }

    public void test_negated_max() {
        assertPeriod(DatePeriod.ofYears(Integer.MAX_VALUE).negated(), -Integer.MAX_VALUE, 0, 0);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_negated_overflow() {
        DatePeriod.ofYears(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    // normalized()
    //-----------------------------------------------------------------------
    public void test_normalized() {
        assertPeriod(DatePeriod.of(1, 2, 3).normalized(), 1, 2, 3);
    }

    public void test_normalized_months() {
        assertPeriod(DatePeriod.of(1, 11, 3).normalized(), 1, 11, 3);
        assertPeriod(DatePeriod.of(1, 12, 3).normalized(), 2, 0, 3);
        assertPeriod(DatePeriod.of(1, 23, 3).normalized(), 2, 11, 3);
        assertPeriod(DatePeriod.of(1, 24, 3).normalized(), 3, 0, 3);
        
        assertPeriod(DatePeriod.of(1, -23, 3).normalized(), -1, 1, 3);
        assertPeriod(DatePeriod.of(1, -24, 3).normalized(), -1, 0, 3);
    }

    public void test_normalized_zero() {
        assertSame(DatePeriod.ZERO.normalized(), DatePeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_normalized_max() {
        DatePeriod base = DatePeriod.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        base.normalized();
    }

    //-----------------------------------------------------------------------
    // totalYears()
    //-----------------------------------------------------------------------
    public void test_totalYears() {
        assertEquals(DatePeriod.ZERO.totalYears(), 0);
        assertEquals(DatePeriod.of(1, 2, 3).totalYears(), 1);
        assertEquals(DatePeriod.of(3, 0, 1000).totalYears(), 3);
        assertEquals(DatePeriod.of(3, 11, 0).totalYears(), 3);
        assertEquals(DatePeriod.of(3, 12, 0).totalYears(), 4);
        assertEquals(DatePeriod.of(3, -11, 0).totalYears(), 3);
        assertEquals(DatePeriod.of(3, -12, 0).totalYears(), 2);
        assertEquals(DatePeriod.of(-3, 11, 0).totalYears(), -3);
        assertEquals(DatePeriod.of(-3, 12, 0).totalYears(), -2);
        assertEquals(DatePeriod.of(-3, -11, 0).totalYears(), -3);
        assertEquals(DatePeriod.of(-3, -12, 0).totalYears(), -4);
    }

    public void test_totalYears_big() {
        BigInteger calc = MAX_BINT.divide(BigInteger.valueOf(12)).add(MAX_BINT);
        long y = new BigDecimal(calc).longValueExact();
        assertEquals(DatePeriod.of(Integer.MAX_VALUE, Integer.MAX_VALUE, 0).totalYears(), y);
    }

    //-----------------------------------------------------------------------
    // totalMonths()
    //-----------------------------------------------------------------------
    public void test_totalMonths() {
        assertEquals(DatePeriod.ZERO.totalMonths(), 0);
        assertEquals(DatePeriod.of(1, 2, 3).totalMonths(), 14);
        assertEquals(DatePeriod.of(3, 0, 1000).totalMonths(), 36);
        assertEquals(DatePeriod.of(3, 11, 0).totalMonths(), 47);
        assertEquals(DatePeriod.of(3, 12, 0).totalMonths(), 48);
        assertEquals(DatePeriod.of(3, -11, 0).totalMonths(), 25);
        assertEquals(DatePeriod.of(3, -12, 0).totalMonths(), 24);
        assertEquals(DatePeriod.of(-3, 11, 0).totalMonths(), -25);
        assertEquals(DatePeriod.of(-3, 12, 0).totalMonths(), -24);
        assertEquals(DatePeriod.of(-3, -11, 0).totalMonths(), -47);
        assertEquals(DatePeriod.of(-3, -12, 0).totalMonths(), -48);
    }

    public void test_totalMonths_big() {
        BigInteger calc = MAX_BINT.multiply(BigInteger.valueOf(12)).add(MAX_BINT);
        long m = new BigDecimal(calc).longValueExact();
        assertEquals(DatePeriod.of(Integer.MAX_VALUE, Integer.MAX_VALUE, 0).totalMonths(), m);
    }

//    //-----------------------------------------------------------------------
//    // toPeriod()
//    //-----------------------------------------------------------------------
//    public void test_toPeriod() {
//        DatePeriod test = DatePeriod.of(1, 2, 3);
//        assertSame(test.toPeriod(), test);
//    }
//
//    public void test_toPeriod_zero() {
//        assertSame(DatePeriod.ZERO.toPeriod(), Period.ZERO);
//    }

    //-----------------------------------------------------------------------
    // toPeriodFields()
    //-----------------------------------------------------------------------
    public void test_toPeriodFields() {
        DatePeriod test = DatePeriod.of(1, 2, 3);
        PeriodFields fields = test.toPeriodFields();
        assertEquals(fields.size(), 3);
        assertEquals(fields.get(YEARS), PeriodField.of(1, YEARS));
        assertEquals(fields.get(MONTHS), PeriodField.of(2, MONTHS));
        assertEquals(fields.get(DAYS), PeriodField.of(3, DAYS));
    }

    public void test_toPeriodFields_zeroRemoved() {
        DatePeriod test = DatePeriod.of(1, 0, 3);
        PeriodFields fields = test.toPeriodFields();
        assertEquals(fields.size(), 2);
        assertEquals(fields.get(YEARS), PeriodField.of(1, YEARS));
        assertEquals(fields.get(DAYS), PeriodField.of(3, DAYS));
        assertEquals(fields.contains(MONTHS), false);
    }

    public void test_toPeriodFields_zero() {
        assertSame(DatePeriod.ZERO.toPeriodFields(), PeriodFields.ZERO);
    }

    //-----------------------------------------------------------------------
    // toEstimatedDuration()
    //-----------------------------------------------------------------------
    public void test_toDuration() {
        assertEquals(DatePeriod.ZERO.toEstimatedDuration(), Duration.ofSeconds(0));
        assertEquals(DatePeriod.of(0, 0, 3).toEstimatedDuration(), Duration.ofStandardDays(3));
        assertEquals(DatePeriod.of(2, 4, 3).toEstimatedDuration(), DatePeriod.of(2, 4, 3).toPeriodFields().toEstimatedDuration());
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        assertEquals(Period.of(1, 0, 0, 0, 0, 0).equals(Period.ofYears(1)), true);
        assertEquals(Period.of(0, 1, 0, 0, 0, 0).equals(Period.ofMonths(1)), true);
        assertEquals(Period.of(0, 0, 1, 0, 0, 0).equals(Period.ofDays(1)), true);
        assertEquals(Period.of(0, 0, 0, 1, 0, 0).equals(Period.ofHours(1)), true);
        assertEquals(Period.of(0, 0, 0, 0, 1, 0).equals(Period.ofMinutes(1)), true);
        assertEquals(Period.of(0, 0, 0, 0, 0, 1).equals(Period.ofSeconds(1)), true);
        assertEquals(Period.of(1, 2, 0, 0, 0, 0).equals(Period.ofYearsMonths(1, 2)), true);
        assertEquals(Period.of(1, 2, 3, 0, 0, 0).equals(Period.ofYearsMonthsDays(1, 2, 3)), true);
        assertEquals(Period.of(0, 0, 0, 1, 2, 3).equals(Period.ofHoursMinutesSeconds(1, 2, 3)), true);
        assertEquals(Period.of(1, 2, 3, 4, 5, 6).equals(Period.of(1, 2, 3, 4, 5, 6)), true);
        
        assertEquals(Period.ofYears(1).equals(Period.ofYears(1)), true);
        assertEquals(Period.ofYears(1).equals(Period.ofYears(2)), false);
        
        assertEquals(Period.ofMonths(1).equals(Period.ofMonths(1)), true);
        assertEquals(Period.ofMonths(1).equals(Period.ofMonths(2)), false);
        
        assertEquals(Period.ofDays(1).equals(Period.ofDays(1)), true);
        assertEquals(Period.ofDays(1).equals(Period.ofDays(2)), false);
        
        assertEquals(Period.ofHours(1).equals(Period.ofHours(1)), true);
        assertEquals(Period.ofHours(1).equals(Period.ofHours(2)), false);
        
        assertEquals(Period.ofMinutes(1).equals(Period.ofMinutes(1)), true);
        assertEquals(Period.ofMinutes(1).equals(Period.ofMinutes(2)), false);
        
        assertEquals(Period.ofSeconds(1).equals(Period.ofSeconds(1)), true);
        assertEquals(Period.ofSeconds(1).equals(Period.ofSeconds(2)), false);
        
        assertEquals(Period.ofYearsMonths(1, 2).equals(Period.ofYearsMonths(1, 2)), true);
        assertEquals(Period.ofYearsMonths(1, 2).equals(Period.ofYearsMonths(3, 2)), false);
        assertEquals(Period.ofYearsMonths(1, 2).equals(Period.ofYearsMonths(1, 3)), false);
        assertEquals(Period.ofYearsMonths(1, 2).equals(Period.ofYearsMonths(3, 3)), false);
        
        assertEquals(Period.ofYearsMonthsDays(1, 2, 3).equals(Period.ofYearsMonthsDays(1, 2, 3)), true);
        assertEquals(Period.ofYearsMonthsDays(1, 2, 3).equals(Period.ofYearsMonthsDays(0, 2, 3)), false);
        assertEquals(Period.ofYearsMonthsDays(1, 2, 3).equals(Period.ofYearsMonthsDays(1, 0, 3)), false);
        assertEquals(Period.ofYearsMonthsDays(1, 2, 3).equals(Period.ofYearsMonthsDays(1, 2, 0)), false);
        
        assertEquals(Period.ofHoursMinutesSeconds(1, 2, 3).equals(Period.ofHoursMinutesSeconds(1, 2, 3)), true);
        assertEquals(Period.ofHoursMinutesSeconds(1, 2, 3).equals(Period.ofHoursMinutesSeconds(0, 2, 3)), false);
        assertEquals(Period.ofHoursMinutesSeconds(1, 2, 3).equals(Period.ofHoursMinutesSeconds(1, 0, 3)), false);
        assertEquals(Period.ofHoursMinutesSeconds(1, 2, 3).equals(Period.ofHoursMinutesSeconds(1, 2, 0)), false);
    }

    public void test_equals_self() {
        Period test = Period.of(1, 2, 3, 4, 5, 6);
        assertEquals(test.equals(test), true);
    }

    public void test_equals_null() {
        Period test = Period.of(1, 2, 3, 4, 5, 6);
        assertEquals(test.equals(null), false);
    }

    public void test_equals_otherClass() {
        Period test = Period.of(1, 2, 3, 4, 5, 6);
        assertEquals(test.equals(""), false);
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Period test5 = Period.ofDays(5);
        Period test6 = Period.ofDays(6);
        assertEquals(test5.hashCode() == test5.hashCode(), true);
        assertEquals(test5.hashCode() == test6.hashCode(), false);
    }

    // test too slow and runs out of memory
//    public void test_hashCode_unique() {
//        // spec requires unique hash codes
//        // years 0-31, months 0-11, days 0-31, hours 0-23, minutes 0-59, seconds 0-59
//        boolean[] pos = new boolean[Integer.MAX_VALUE / 128];  // added 128 to avoid out of memory
//        boolean[] neg = new boolean[Integer.MAX_VALUE / 128];
//        for (int y = 0; y <= 31; y++) {
//            for (int mo = 0; mo <= 11; mo++) {
//                System.out.print(".");
//                for (int d = 0; d <= 31; d++) {
//                    for (int h = 0; h <= 23; h++) {
//                        for (int mn = 0; mn <= 59; mn++) {
//                            for (int s = 0; s <= 50; s++) {
//                                Period test = Period.period(y, mo, d, h, mn, s);
//                                int hashCode = test.hashCode();
//                                if (hashCode >= 0) {
//                                    if (hashCode < pos.length) {
//                                        assertEquals(pos[hashCode], false);
//                                        pos[hashCode] = true;
//                                    }
//                                } else {
//                                    hashCode = -(hashCode + 1);
//                                    if (hashCode < neg.length) {
//                                        assertEquals(neg[hashCode], false);
//                                        neg[hashCode] = true;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    public void test_hashCode_unique_workaroundSlowTest() {
        // spec requires unique hash codes
        // years 0-31, months 0-11, days 0-31, hours 0-23, minutes 0-59, seconds 0-59
        
        // -37 nanos removes the effect of the nanos
        int yearsBits = 0;
        for (int i = 0; i <= 31; i++) {
            Period test = Period.ofYears(i).withNanos(-37);
            yearsBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(yearsBits), 5);
        int monthsBits = 0;
        for (int i = 0; i <= 11; i++) {
            Period test = Period.ofMonths(i).withNanos(-37);
            monthsBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(monthsBits), 4);
        assertEquals(yearsBits & monthsBits, 0);
        int daysBits = 0;
        for (int i = 0; i <= 31; i++) {
            Period test = Period.ofDays(i).withNanos(-37);
            daysBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(daysBits), 5);
        assertEquals(yearsBits & monthsBits & daysBits, 0);
        int hoursBits = 0;
        for (int i = 0; i <= 23; i++) {
            Period test = Period.ofHours(i).withNanos(-37);
            hoursBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(hoursBits), 5);
        assertEquals(yearsBits & monthsBits & daysBits & hoursBits, 0);
        int minutesBits = 0;
        for (int i = 0; i <= 59; i++) {
            Period test = Period.ofMinutes(i).withNanos(-37);
            minutesBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(minutesBits), 6);
        assertEquals(yearsBits & minutesBits & daysBits & hoursBits & minutesBits, 0);
        int secondsBits = 0;
        for (int i = 0; i <= 59; i++) {
            Period test = Period.ofSeconds(i).withNanos(-37);
            secondsBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(secondsBits), 6);
        assertEquals(yearsBits & secondsBits & daysBits & hoursBits & minutesBits & secondsBits, 0);
        
        // make common overflows not same hash code
        assertTrue(Period.ofMonths(16).hashCode() != Period.ofYears(1).hashCode());
        assertTrue(Period.ofDays(32).hashCode() != Period.ofMonths(1).hashCode());
        assertTrue(Period.ofHours(32).hashCode() != Period.ofDays(1).hashCode());
        assertTrue(Period.ofMinutes(64).hashCode() != Period.ofHours(1).hashCode());
        assertTrue(Period.ofSeconds(64).hashCode() != Period.ofMinutes(1).hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toStringAndParse")
    Object[][] data_toString() {
        return new Object[][] {
            {DatePeriod.ZERO, "P0D"},
            {DatePeriod.ofDays(0), "P0D"},
            {DatePeriod.ofYears(1), "P1Y"},
            {DatePeriod.ofMonths(1), "P1M"},
            {DatePeriod.ofDays(1), "P1D"},
            {DatePeriod.of(1, 2, 0), "P1Y2M"},
            {DatePeriod.of(1, 0, 3), "P1Y3D"},
            {DatePeriod.of(0, 2, 3), "P2M3D"},
            {DatePeriod.of(1, 2, 3), "P1Y2M3D"},
            {DatePeriod.of(-1, -2, -3), "P-1Y-2M-3D"},
        };
    }

    @Test(dataProvider="toStringAndParse")
    public void test_toString(DatePeriod test, String expected) {
        assertEquals(test.toString(), expected);
        assertSame(test.toString(), test.toString());  // repeat to check caching
    }

    //-----------------------------------------------------------------------
    private void assertPeriod(DatePeriod test, int y, int m, int d) {
        assertEquals(test.getYears(), y);
        assertEquals(test.getMonths(), m);
        assertEquals(test.getDays(), d);
    }

}
