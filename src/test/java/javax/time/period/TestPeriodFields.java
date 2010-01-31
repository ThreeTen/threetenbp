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
package javax.time.period;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.PeriodUnit;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test PeriodFields.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestPeriodFields {

    private static final Map<PeriodUnit, Long> MAP_EMPTY = new HashMap<PeriodUnit, Long>();
    private static final Map<PeriodUnit, Long> MAP_NULL = null;
    private static final PeriodUnit YEARS = ISOChronology.periodYears();
    private static final PeriodUnit MONTHS = ISOChronology.periodMonths();
    private static final PeriodUnit DAYS = ISOChronology.periodDays();
    private static final PeriodUnit HOURS = ISOChronology.periodHours();
    private static final PeriodUnit MINUTES = ISOChronology.periodMinutes();
    private static final PeriodUnit SECONDS = ISOChronology.periodSeconds();
    private static final PeriodUnit NANOS = ISOChronology.periodNanos();

    private PeriodFields fixtureP2Y5D;
    private PeriodFields fixtureZeroYears;

    @BeforeTest
    public void setUp() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 2L);
        map.put(DAYS, 5L);
        fixtureP2Y5D = PeriodFields.of(map);
        fixtureZeroYears = PeriodFields.of(0, YEARS);
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(PeriodProvider.class.isAssignableFrom(PeriodFields.class));
        assertTrue(Iterable.class.isAssignableFrom(PeriodFields.class));
        assertTrue(Serializable.class.isAssignableFrom(PeriodFields.class));
    }

    @DataProvider(name="serialization")
    Object[][] data_serialization() {
        return new Object[][] {
            {PeriodFields.ZERO},
            {PeriodFields.of(2, YEARS)},
        };
    }

    @Test(dataProvider="serialization")
    public void test_serialization(PeriodFields period) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(period);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        if (period.size() == 0) {
            assertSame(ois.readObject(), period);
        } else {
            assertEquals(ois.readObject(), period);
        }
    }

    public void test_immutable() {
        Class<PeriodFields> cls = PeriodFields.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
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
    public void factory_zeroSingleton() {
        assertSame(PeriodFields.ZERO, PeriodFields.ZERO);
        assertSame(PeriodFields.of(MAP_EMPTY), PeriodFields.ZERO);
    }

    //-----------------------------------------------------------------------
    public void factory_singleField_longUnit() {
        assertPeriodFields(PeriodFields.of(1, YEARS), 1, YEARS);
        assertPeriodFields(fixtureZeroYears, 0, YEARS);
        assertPeriodFields(PeriodFields.of(-1, YEARS), -1, YEARS);
        assertPeriodFields(PeriodFields.of(Long.MAX_VALUE, YEARS), Long.MAX_VALUE, YEARS);
        assertPeriodFields(PeriodFields.of(Long.MIN_VALUE, YEARS), Long.MIN_VALUE, YEARS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_singleField_longUnit_null() {
        PeriodFields.of(1, (PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    public void factory_singleField_PeriodField() {
        assertPeriodFields(PeriodFields.of(PeriodField.of(1, YEARS)), 1, YEARS);
        assertPeriodFields(PeriodFields.of(PeriodField.of(-1, YEARS)), -1, YEARS);
        assertPeriodFields(PeriodFields.of(PeriodField.of(Long.MAX_VALUE, YEARS)), Long.MAX_VALUE, YEARS);
        assertPeriodFields(PeriodFields.of(PeriodField.of(Long.MIN_VALUE, YEARS)), Long.MIN_VALUE, YEARS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_singleField_PeriodField_null() {
        PeriodFields.of((PeriodField) null);
    }

    //-----------------------------------------------------------------------
    public void factory_multiField_PeriodField() {
        assertPeriodFields(PeriodFields.of(new PeriodField[] {PeriodField.of(1, YEARS)}), 1, YEARS);
        assertPeriodFields(PeriodFields.of(new PeriodField[] {PeriodField.of(-1, YEARS)}), -1, YEARS);
        assertPeriodFields(PeriodFields.of(new PeriodField[] {PeriodField.of(Long.MAX_VALUE, YEARS)}), Long.MAX_VALUE, YEARS);
        assertPeriodFields(PeriodFields.of(new PeriodField[] {PeriodField.of(Long.MIN_VALUE, YEARS)}), Long.MIN_VALUE, YEARS);
        
        assertPeriodFields(PeriodFields.of(PeriodField.of(1, YEARS), PeriodField.of(2, MONTHS)), 1, YEARS, 2, MONTHS);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void factory_multiField_PeriodField_sameUnit() {
        PeriodFields.of(PeriodField.of(1, YEARS), PeriodField.of(2, MONTHS), PeriodField.of(3, YEARS));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_multiField_PeriodField_null() {
        PeriodFields.of((PeriodField[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_multiField_PeriodField_nullItem() {
        PeriodFields.of(new PeriodField[] {null});
    }

    //-----------------------------------------------------------------------
    public void factory_map() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 1L);
        map.put(DAYS, -2L);
        PeriodFields test = PeriodFields.of(map);
        assertPeriodFields(test, 1, YEARS, -2, DAYS);
    }

    public void factory_map_cloned() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 1L);
        PeriodFields test = PeriodFields.of(map);
        assertPeriodFields(test, 1, YEARS);
        map.put(DAYS, 2L);
        assertPeriodFields(test, 1, YEARS);
    }

    public void factory_map_empty_singleton() {
        PeriodFields test = PeriodFields.of(MAP_EMPTY);
        assertSame(test, PeriodFields.ZERO);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_map_null() {
        PeriodFields.of(MAP_NULL);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_map_nullKey() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 1L);
        map.put(null, 2L);
        PeriodFields.of(map);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_map_nullValue() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 1L);
        map.put(DAYS, null);
        PeriodFields.of(map);
    }

    //-----------------------------------------------------------------------
    public void factory_period_provider() {
        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
        PeriodFields test = PeriodFields.from(provider);
        assertEquals(test.size(), 7);
        assertEquals(test.getAmount(YEARS), 1);
        assertEquals(test.getAmount(MONTHS), 2);
        assertEquals(test.getAmount(DAYS), 3);
        assertEquals(test.getAmount(HOURS), 4);
        assertEquals(test.getAmount(MINUTES), 5);
        assertEquals(test.getAmount(SECONDS), 6);
        assertEquals(test.getAmount(NANOS), 7);
    }

    public void factory_period_provider_zeroesRemoved() {
        PeriodProvider provider = Period.of(1, 0, 2, 0, 0, 0, 0);
        assertPeriodFields(PeriodFields.from(provider), 1, YEARS, 2, DAYS);
    }

    public void factory_period_provider_zero() {
        PeriodProvider provider = Period.ZERO;
        assertSame(PeriodFields.from(provider), PeriodFields.ZERO);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_period_provider_null() {
        PeriodProvider provider = null;
        PeriodFields.from(provider);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_period_badProvider() {
        PeriodProvider provider = new MockPeriodProviderReturnsNull();
        PeriodFields.from(provider);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertEquals(fixtureZeroYears.isZero(), true);
        assertEquals(PeriodFields.of(1, YEARS).isZero(), false);
        
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 0L);
        map.put(DAYS, 0L);
        PeriodFields test = PeriodFields.of(map);
        assertEquals(test.isZero(), true);
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    public void test_isPositive() {
        assertEquals(fixtureZeroYears.isPositive(), true);
        assertEquals(PeriodFields.of(1, YEARS).isPositive(), true);
        assertEquals(PeriodFields.of(-1, YEARS).isPositive(), false);
        
        assertEquals(PeriodFields.of(1, YEARS).with(2, DAYS).isPositive(), true);
        assertEquals(PeriodFields.of(1, YEARS).with(-2, DAYS).isPositive(), false);
        assertEquals(PeriodFields.of(-1, YEARS).with(2, DAYS).isPositive(), false);
        assertEquals(PeriodFields.of(-1, YEARS).with(-2, DAYS).isPositive(), false);
    }

    //-----------------------------------------------------------------------
    // size()
    //-----------------------------------------------------------------------
    public void test_size() {
        assertEquals(PeriodFields.ZERO.size(), 0);
        assertEquals(PeriodFields.of(2, YEARS).size(), 1);
        assertEquals(fixtureP2Y5D.size(), 2);
        assertEquals(fixtureZeroYears.size(), 1);
    }

    //-----------------------------------------------------------------------
    // iterator()
    //-----------------------------------------------------------------------
    public void test_iterator() {
        Iterator<PeriodField> iterator = fixtureP2Y5D.iterator();
        assertEquals(iterator.hasNext(), true);
        assertEquals(iterator.next(), PeriodField.of(2, YEARS));
        assertEquals(iterator.hasNext(), true);
        assertEquals(iterator.next(), PeriodField.of(5, DAYS));
        assertEquals(iterator.hasNext(), false);
    }

    //-----------------------------------------------------------------------
    // contains()
    //-----------------------------------------------------------------------
    public void test_contains() {
        assertEquals(fixtureP2Y5D.contains(YEARS), true);
        assertEquals(fixtureP2Y5D.contains(MONTHS), false);
        assertEquals(fixtureP2Y5D.contains(DAYS), true);
        assertEquals(fixtureP2Y5D.contains(HOURS), false);
        assertEquals(fixtureZeroYears.contains(YEARS), true);
    }

    public void test_contains_zero() {
        assertEquals(PeriodFields.ZERO.contains(YEARS), false);
    }

    //-----------------------------------------------------------------------
    // get(PeriodRule)
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(fixtureP2Y5D.get(YEARS), PeriodField.of(2L, YEARS));
        assertEquals(fixtureP2Y5D.get(DAYS), PeriodField.of(5L, DAYS));
        assertEquals(fixtureZeroYears.get(YEARS), PeriodField.of(0L, YEARS));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_get_null() {
        fixtureP2Y5D.get((PeriodUnit) null);
    }

    public void test_get_notPresent() {
        assertEquals(fixtureP2Y5D.get(MONTHS), null);
    }

    //-----------------------------------------------------------------------
    // getAmount(PeriodRule)
    //-----------------------------------------------------------------------
    public void test_getAmount() {
        assertEquals(fixtureP2Y5D.getAmount(YEARS), 2);
        assertEquals(fixtureP2Y5D.getAmount(DAYS), 5);
        assertEquals(fixtureZeroYears.getAmount(YEARS), 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getAmount_null() {
        fixtureP2Y5D.getAmount((PeriodUnit) null);
    }

    public void test_getAmount_notPresent() {
        assertEquals(fixtureP2Y5D.getAmount(MONTHS), 0);
    }

    //-----------------------------------------------------------------------
    // getAmountInt(PeriodRule)
    //-----------------------------------------------------------------------
    public void test_getAmountInt() {
        assertEquals(fixtureP2Y5D.getAmountInt(YEARS), 2);
        assertEquals(fixtureP2Y5D.getAmountInt(DAYS), 5);
        assertEquals(fixtureZeroYears.getAmountInt(YEARS), 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getAmountInt_null() {
        fixtureP2Y5D.getAmountInt((PeriodUnit) null);
    }

    public void test_getAmountInt_notPresent() {
        assertEquals(fixtureP2Y5D.getAmountInt(MONTHS), 0);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_getAmountInt_tooBig() {
        PeriodFields.of(Integer.MAX_VALUE + 1L, MONTHS).getAmountInt(MONTHS);
    }

//    //-----------------------------------------------------------------------
//    // get(PeriodRule,long)
//    //-----------------------------------------------------------------------
//    public void test_get_long() {
//        assertEquals(fixtureP2Y5D.get(YEARS, 0), 2L);
//        assertEquals(fixtureP2Y5D.get(DAYS, 0), 5L);
//        assertEquals(fixtureZeroYears.get(YEARS, 1), 0L);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_get_long_null() {
//        fixtureP2Y5D.get((PeriodRule) null, 0L);
//    }
//
//    public void test_get_long_notPresent() {
//        assertEquals(fixtureP2Y5D.get(MONTHS, 0L), 0L);
//        assertEquals(fixtureP2Y5D.get(MONTHS, 101L), 101L);
//    }
//
//    //-----------------------------------------------------------------------
//    // getInt(PeriodRule, int)
//    //-----------------------------------------------------------------------
//    public void test_getInt_int() {
//        assertEquals(fixtureP2Y5D.getInt(YEARS), 2);
//        assertEquals(fixtureP2Y5D.getInt(DAYS), 5);
//        assertEquals(fixtureZeroYears.getInt(YEARS), 0);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_getInt_int_null() {
//        fixtureP2Y5D.getInt((PeriodRule) null);
//    }
//
//    public void test_getInt_int_notPresent() {
//        assertEquals(fixtureP2Y5D.getInt(MONTHS, 0), 0);
//        assertEquals(fixtureP2Y5D.getInt(MONTHS, 101), 101);
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class)
//    public void test_getInt_int_tooBig() {
//        PeriodFields.of(Integer.MAX_VALUE + 1L, MONTHS).getInt(MONTHS);
//    }

    //-----------------------------------------------------------------------
    // withZeroesRemoved()
    //-----------------------------------------------------------------------
    public void test_withZeroesRemoved() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 2L);
        map.put(DAYS, 0L);
        map.put(HOURS, -3L);
        map.put(SECONDS, 0L);
        PeriodFields test = PeriodFields.of(map);
        assertPeriodFields(test.withZeroesRemoved(), 2, YEARS, -3, HOURS);
    }

    public void test_withZeroesRemoved_toZero() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 0L);
        map.put(DAYS, 0L);
        map.put(HOURS, 0L);
        map.put(SECONDS, 0L);
        PeriodFields test = PeriodFields.of(map);
        assertSame(test.withZeroesRemoved(), PeriodFields.ZERO);
    }

    public void test_withZeroesRemoved_zero() {
        assertSame(PeriodFields.ZERO.withZeroesRemoved(), PeriodFields.ZERO);
    }

    //-----------------------------------------------------------------------
    // with(PeriodFields)
    //-----------------------------------------------------------------------
    public void test_with_PeriodFields() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 4L);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.of(map);
        assertPeriodFields(test.with(fixtureP2Y5D), 2, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_with_PeriodFields_zeroYearsBase() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(MONTHS, 4L);
        map.put(DAYS, 8L);
        PeriodFields test = PeriodFields.of(map);
        assertPeriodFields(fixtureZeroYears.with(test), 0, YEARS, 4, MONTHS, 8, DAYS);
    }

    public void test_with_PeriodFields_zeroYearsParam() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(MONTHS, 4L);
        map.put(DAYS, 8L);
        PeriodFields test = PeriodFields.of(map);
        assertPeriodFields(test.with(fixtureZeroYears), 0, YEARS, 4, MONTHS, 8, DAYS);
    }

    public void test_with_PeriodFields_zeroBase() {
        assertSame(PeriodFields.ZERO.with(fixtureP2Y5D), fixtureP2Y5D);
    }

    public void test_with_PeriodFields_zeroParam() {
        assertSame(fixtureP2Y5D.with(PeriodFields.ZERO), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_PeriodFields_null() {
        fixtureP2Y5D.with((PeriodFields) null);
    }

    //-----------------------------------------------------------------------
    // with(int,PeriodRule)
    //-----------------------------------------------------------------------
    public void test_with_intPeriodRule_add() {
        assertPeriodFields(fixtureP2Y5D.with(8, MONTHS), 2, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_with_intPeriodRule_replace() {
        assertPeriodFields(fixtureP2Y5D.with(8, YEARS), 8, YEARS, 5, DAYS);
    }

    public void test_with_intPeriodRule_noChange() {
        assertSame(fixtureP2Y5D.with(2, YEARS), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_intPeriodRule_null() {
        fixtureP2Y5D.with(1, (PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // withUnitRemoved(PeriodRule)
    //-----------------------------------------------------------------------
    public void test_withUnitRemoved() {
        assertPeriodFields(fixtureP2Y5D.withRuleRemoved(DAYS), 2, YEARS);
    }

    public void test_withUnitRemoved_toZero() {
        assertSame(fixtureZeroYears.withRuleRemoved(YEARS), PeriodFields.ZERO);
    }

    public void test_withUnitRemoved_notPresent() {
        assertSame(fixtureP2Y5D.withRuleRemoved(MONTHS), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withUnitRemoved_null() {
        fixtureP2Y5D.withRuleRemoved((PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodFields)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodFields() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 4L);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.of(map).plus(fixtureP2Y5D);
        assertPeriodFields(test, 6, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_plus_PeriodFields_zeroBase() {
        assertSame(PeriodFields.ZERO.plus(fixtureP2Y5D), fixtureP2Y5D);
    }

    public void test_plus_PeriodFields_zeroParam() {
        assertEquals(fixtureP2Y5D.plus(PeriodFields.ZERO), fixtureP2Y5D);
        assertEquals(fixtureP2Y5D.plus(fixtureZeroYears), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodFields_null() {
        fixtureP2Y5D.plus((PeriodFields) null);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_PeriodFields_overflowTooBig() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, Long.MAX_VALUE);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.of(map);
        test.plus(PeriodFields.of(1, YEARS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_PeriodFields_overflowTooSmall() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, Long.MIN_VALUE);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.of(map);
        test.plus(PeriodFields.of(-1, YEARS));
    }

    //-----------------------------------------------------------------------
    // minus(PeriodFields)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodFields() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 9L);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.of(map).minus(fixtureP2Y5D);
        assertPeriodFields(test, 7, YEARS, 8, MONTHS, -5, DAYS);
    }

    public void test_minus_PeriodFields_zeroBase() {
        assertSame(PeriodFields.ZERO.minus(fixtureP2Y5D), fixtureP2Y5D);
    }

    public void test_minus_PeriodFields_zeroParam() {
        assertEquals(fixtureP2Y5D.minus(PeriodFields.ZERO), fixtureP2Y5D);
        assertEquals(fixtureP2Y5D.minus(fixtureZeroYears), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodFields_null() {
        fixtureP2Y5D.minus((PeriodFields) null);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_PeriodFields_overflowTooBig() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, Long.MAX_VALUE);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.of(map);
        test.minus(PeriodFields.of(-1, YEARS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_PeriodFields_overflowTooSmall() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, Long.MIN_VALUE);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.of(map);
        test.minus(PeriodFields.of(1, YEARS));
    }

    //-----------------------------------------------------------------------
    // multipliedBy()
    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        assertPeriodFields(fixtureP2Y5D.multipliedBy(2), 4, YEARS, 10, DAYS);
        assertPeriodFields(fixtureP2Y5D.multipliedBy(-3), -6, YEARS, -15, DAYS);
    }

    public void test_multipliedBy_zeroYearsBase() {
        assertSame(fixtureZeroYears.multipliedBy(2), fixtureZeroYears);
    }

    public void test_multipliedBy_zeroBase() {
        assertSame(PeriodFields.ZERO.multipliedBy(2), PeriodFields.ZERO);
    }

    public void test_multipliedBy_zero() {
        assertPeriodFields(fixtureP2Y5D.multipliedBy(0), 0, YEARS, 0, DAYS);
    }

    public void test_multipliedBy_one() {
        assertSame(fixtureP2Y5D.multipliedBy(1), fixtureP2Y5D);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        PeriodFields test = PeriodFields.of(Long.MAX_VALUE / 2 + 1, YEARS);
        test.multipliedBy(2);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        PeriodFields test = PeriodFields.of(Long.MIN_VALUE / 2 - 1, YEARS);
        test.multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    // dividedBy()
    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 12L);
        map.put(MONTHS, 15L);
        PeriodFields test = PeriodFields.of(map);
        assertPeriodFields(test.dividedBy(2), 6, YEARS, 7, MONTHS);
        assertPeriodFields(test.dividedBy(-3), -4, YEARS, -5, MONTHS);
    }

    public void test_dividedBy_zeroYearsBase() {
        assertSame(fixtureZeroYears.dividedBy(3), fixtureZeroYears);
    }

    public void test_dividedBy_zeroBase() {
        assertSame(PeriodFields.ZERO.dividedBy(3), PeriodFields.ZERO);
    }

    public void test_dividedBy_one() {
        assertSame(fixtureP2Y5D.dividedBy(1), fixtureP2Y5D);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        fixtureP2Y5D.dividedBy(0);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_dividedBy_zeroYearsBase_divideByZero() {
        fixtureZeroYears.dividedBy(0);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_dividedBy_zeroBase_divideByZero() {
        PeriodFields.ZERO.dividedBy(0);
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    public void test_negated() {
        assertPeriodFields(fixtureP2Y5D.negated(), -2, YEARS, -5, DAYS);
    }

    public void test_negated_zeroYears() {
        assertSame(fixtureZeroYears.negated(), fixtureZeroYears);
    }

    public void test_negated_zero() {
        assertSame(PeriodFields.ZERO.negated(), PeriodFields.ZERO);
    }

    public void test_negated_max() {
        assertPeriodFields(PeriodFields.of(Long.MAX_VALUE, YEARS).negated(), -Long.MAX_VALUE, YEARS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_negated_overflow() {
        PeriodFields.of(Long.MIN_VALUE, YEARS).negated();
    }

    //-----------------------------------------------------------------------
    // toEquivalentPeriod(PeriodUnit...)
    //-----------------------------------------------------------------------
    public void test_toEquivalentPeriod_units_yearsMonthsToMonths() {
        PeriodFields test = PeriodFields.of(5, YEARS).with(2, MONTHS).toEquivalentPeriod(MONTHS);
        assertEquals(test, PeriodFields.of(5 * 12 + 2, MONTHS));
    }

    public void test_toEquivalentPeriod_units_yearsToYears() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalentPeriod(new PeriodUnit[] {YEARS});
        assertEquals(test, PeriodFields.of(5, YEARS));
    }

    public void test_toEquivalentPeriod_units_yearsToMonths() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalentPeriod(MONTHS);
        assertEquals(test, PeriodFields.of(5 * 12, MONTHS));
    }

    public void test_toEquivalentPeriod_units_yearsToYearsMonthsOrDays() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalentPeriod(YEARS, MONTHS, DAYS);
        assertEquals(test, PeriodFields.of(5, YEARS));
    }

    public void test_toEquivalentPeriod_units_yearsToMonthsOrDays() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalentPeriod(MONTHS, DAYS);
        assertEquals(test, PeriodFields.of(5 * 12, MONTHS));
    }

    public void test_toEquivalentPeriod_units_yearsToDaysOrMonths() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalentPeriod(DAYS, MONTHS);
        assertEquals(test, PeriodFields.of(5 * 12, MONTHS));
    }

    public void test_toEquivalentPeriod_units_hoursToMinutesOrSeconds() {
        PeriodFields test = PeriodFields.of(5, HOURS).toEquivalentPeriod(MINUTES, SECONDS);
        assertEquals(test, PeriodFields.of(5 * 60, MINUTES));
    }

    public void test_toEquivalentPeriod_units_hoursToSecondsOrMinutes() {
        PeriodFields test = PeriodFields.of(5, HOURS).toEquivalentPeriod(SECONDS, MINUTES);
        assertEquals(test, PeriodFields.of(5 * 60 * 60, SECONDS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_toEquivalentPeriod_units_tooBig() {
        PeriodFields.of(Long.MAX_VALUE / 12 + 12, YEARS).toEquivalentPeriod(MONTHS);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalentPeriod_units_noUnits() {
        try {
            PeriodFields.of(5, YEARS).toEquivalentPeriod(new PeriodUnit[0]);
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert '5 Years' to any requested unit: []", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalentPeriod_units_noConversionOneUnit() {
        try {
            PeriodFields.of(5, YEARS).toEquivalentPeriod(new PeriodUnit[] {DAYS});
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert '5 Years' to any requested unit: [Days]", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalentPeriod_units_noConversionTwoUnits() {
        try {
            PeriodFields.of(5, YEARS).toEquivalentPeriod(DAYS, HOURS);
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert '5 Years' to any requested unit: [Days, Hours]", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toEquivalentPeriod_units_null() {
        PeriodFields.of(5, YEARS).toEquivalentPeriod((PeriodUnit[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toEquivalentPeriod_units_arrayContainsNull() {
        PeriodFields.of(5, YEARS).toEquivalentPeriod(null, YEARS);
    }

    //-----------------------------------------------------------------------
    // toPeriodFields()
    //-----------------------------------------------------------------------
    public void test_toPeriodFields() {
        PeriodFields base = PeriodFields.of(5, YEARS);
        assertSame(base.toPeriodFields(), base);
    }

    //-----------------------------------------------------------------------
    // toRuleAmountMap()
    //-----------------------------------------------------------------------
    public void test_toRuleAmountMap() {
        SortedMap<PeriodUnit, Long> test = fixtureP2Y5D.toRuleAmountMap();
        assertEquals(test.size(), 2);
        assertEquals(test.get(YEARS), Long.valueOf(2));
        assertEquals(test.get(DAYS), Long.valueOf(5));
        test.clear();
        assertPeriodFields(fixtureP2Y5D, 2, YEARS, 5, DAYS);
    }

    //-----------------------------------------------------------------------
    // toEstimatedDuration()
    //-----------------------------------------------------------------------
    public void test_toEstimatedDuration() {
        Duration test = fixtureP2Y5D.toEstimatedDuration();
        Duration twoYears = ISOChronology.periodYears().getEstimatedDuration().multipliedBy(2);
        Duration fiveDays = ISOChronology.periodDays().getEstimatedDuration().multipliedBy(5);
        assertEquals(test, twoYears.plus(fiveDays));
    }

//    //-----------------------------------------------------------------------
//    // toPeriod()
//    //-----------------------------------------------------------------------
//    public void test_toPeriod1() {
//        Period period = fixtureP2Y5D.toPeriod();
//        assertEquals(period.getYears(), 2);
//        assertEquals(period.getMonths(), 0);
//        assertEquals(period.getDays(), 5);
//        assertEquals(period.getHours(), 0);
//        assertEquals(period.getMinutes(), 0);
//        assertEquals(period.getSeconds(), 0);
//        assertEquals(period.getNanos(), 0);
//    }
//
//    public void test_toPeriod2() {  // different set of fields to complete coverage
//        PeriodFields test = PeriodFields.of(1, MONTHS).with(2, HOURS);
//        Period period = test.toPeriod();
//        assertEquals(period.getYears(), 0);
//        assertEquals(period.getMonths(), 1);
//        assertEquals(period.getDays(), 0);
//        assertEquals(period.getHours(), 2);
//        assertEquals(period.getMinutes(), 0);
//        assertEquals(period.getSeconds(), 0);
//        assertEquals(period.getNanos(), 0);
//    }
//
//    public void test_toPeriod_allNonZero() {
//        PeriodFields test = PeriodFields.of(1, YEARS).with(2, MONTHS).with(3, DAYS)
//            .with(4, HOURS).with(5, MINUTES).with(6, SECONDS).with(7, NANOS);
//        Period period = test.toPeriod();
//        assertEquals(period.getYears(), 1);
//        assertEquals(period.getMonths(), 2);
//        assertEquals(period.getDays(), 3);
//        assertEquals(period.getHours(), 4);
//        assertEquals(period.getMinutes(), 5);
//        assertEquals(period.getSeconds(), 6);
//        assertEquals(period.getNanos(), 7);
//    }
//
//    public void test_toPeriod_zeroYearsBase() {
//        Period period = fixtureZeroYears.toPeriod();
//        assertSame(period, Period.ZERO);
//    }
//
//    public void test_toPeriod_zeroBase() {
//        Period period = PeriodFields.ZERO.toPeriod();
//        assertSame(period, Period.ZERO);
//    }
//
//    @Test(expectedExceptions=CalendricalException.class)
//    public void test_toPeriod_invalidField() {
//        PeriodFields test = fixtureP2Y5D.with(3, QUARTERS);
//        test.toPeriod();
//    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        PeriodFields a = PeriodFields.ZERO.with(2, YEARS).with(5, DAYS);
        assertEquals(a.equals(fixtureP2Y5D), true);
        
        PeriodFields b = PeriodFields.ZERO.with(3, YEARS).with(5, DAYS);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(b), false);
        
        PeriodFields c = PeriodFields.ZERO.with(2, YEARS).with(6, DAYS);
        assertEquals(c.equals(a), false);
        assertEquals(c.equals(b), false);
        assertEquals(a.equals(c), false);
        assertEquals(b.equals(c), false);
    }

    public void test_equals_zero_false() {
        assertEquals(fixtureZeroYears.equals(PeriodFields.ZERO), false);
        assertEquals(PeriodFields.ZERO.equals(fixtureZeroYears), false);
    }

    public void test_equals_self() {
        assertEquals(fixtureP2Y5D.equals(fixtureP2Y5D), true);
        assertEquals(fixtureZeroYears.equals(fixtureZeroYears), true);
        assertEquals(PeriodFields.ZERO.equals(PeriodFields.ZERO), true);
    }

    public void test_equals_null() {
        assertEquals(fixtureP2Y5D.equals(null), false);
        assertEquals(fixtureZeroYears.equals(null), false);
        assertEquals(PeriodFields.ZERO.equals(null), false);
    }

    public void test_equals_otherClass() {
        assertEquals(fixtureP2Y5D.equals(""), false);
        assertEquals(fixtureZeroYears.equals(""), false);
        assertEquals(PeriodFields.ZERO.equals(""), false);
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        PeriodFields a = PeriodFields.of(1, MONTHS);
        PeriodFields b = PeriodFields.of(2, MONTHS);
        assertEquals(a.hashCode() == a.hashCode(), true);
        assertEquals(b.hashCode() == b.hashCode(), true);  // can only test true case
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {PeriodFields.ZERO, "[]"},
            {fixtureZeroYears, "[0 Years]"},
            {fixtureP2Y5D, "[2 Years, 5 Days]"},
            {PeriodFields.of(1, MONTHS), "[1 Months]"},
            {PeriodFields.of(-1, DAYS), "[-1 Days]"},
        };
    }

    @Test(dataProvider="toString")
    public void test_toString(PeriodFields test, String expected) {
        assertEquals(test.toString(), expected);
    }

    //-----------------------------------------------------------------------
    private void assertPeriodFields(PeriodFields test, long amount1, PeriodUnit unit1) {
        assertEquals(test.size(), 1);
        assertEquals(test.contains(unit1), true);
        assertEquals(test.get(unit1), PeriodField.of(amount1, unit1));
        assertEquals(test.getAmount(unit1), amount1);
        assertEquals(test.isZero(), amount1 == 0);
        assertEquals(test.isPositive(), amount1 >= 0);
        SortedMap<PeriodUnit, Long> map = test.toRuleAmountMap();
        assertEquals(map.size(), 1);
        assertEquals(map.get(unit1), Long.valueOf(amount1));
    }

    private void assertPeriodFields(PeriodFields test, long amount1, PeriodUnit unit1, long amount2, PeriodUnit unit2) {
        assertEquals(test.size(), 2);
        assertEquals(test.contains(unit1), true);
        assertEquals(test.contains(unit2), true);
        assertEquals(test.get(unit1), PeriodField.of(amount1, unit1));
        assertEquals(test.get(unit2), PeriodField.of(amount2, unit2));
        assertEquals(test.getAmount(unit1), amount1);
        assertEquals(test.getAmount(unit2), amount2);
        assertEquals(test.isZero(), amount1 == 0 && amount2 == 0);
        assertEquals(test.isPositive(), amount1 >= 0 && amount2 >= 0);
        SortedMap<PeriodUnit, Long> map = test.toRuleAmountMap();
        assertEquals(map.size(), 2);
        assertEquals(map.get(unit1), Long.valueOf(amount1));
        assertEquals(map.get(unit2), Long.valueOf(amount2));
    }

    private void assertPeriodFields(PeriodFields test, long amount1, PeriodUnit unit1, long amount2, PeriodUnit unit2, long amount3, PeriodUnit unit3) {
        assertEquals(test.size(), 3);
        assertEquals(test.contains(unit1), true);
        assertEquals(test.contains(unit2), true);
        assertEquals(test.contains(unit3), true);
        assertEquals(test.get(unit1), PeriodField.of(amount1, unit1));
        assertEquals(test.get(unit2), PeriodField.of(amount2, unit2));
        assertEquals(test.get(unit3), PeriodField.of(amount3, unit3));
        assertEquals(test.getAmount(unit1), amount1);
        assertEquals(test.getAmount(unit2), amount2);
        assertEquals(test.getAmount(unit3), amount3);
        assertEquals(test.isZero(), amount1 == 0 && amount2 == 0 && amount3 == 0);
        assertEquals(test.isPositive(), amount1 >= 0 && amount2 >= 0 && amount3 >= 0);
        SortedMap<PeriodUnit, Long> map = test.toRuleAmountMap();
        assertEquals(map.size(), 3);
        assertEquals(map.get(unit1), Long.valueOf(amount1));
        assertEquals(map.get(unit2), Long.valueOf(amount2));
        assertEquals(map.get(unit3), Long.valueOf(amount3));
    }

}
