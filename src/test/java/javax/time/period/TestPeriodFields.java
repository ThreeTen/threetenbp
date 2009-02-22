/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.period.PeriodUnits.DAYS;
import static javax.time.period.PeriodUnits.HOURS;
import static javax.time.period.PeriodUnits.MINUTES;
import static javax.time.period.PeriodUnits.MONTHS;
import static javax.time.period.PeriodUnits.NANOS;
import static javax.time.period.PeriodUnits.QUARTERS;
import static javax.time.period.PeriodUnits.SECONDS;
import static javax.time.period.PeriodUnits.YEARS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

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
    private PeriodFields fixtureP2Y5D;
    private PeriodFields fixtureZeroYears;

    @BeforeTest
    public void setUp() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 2L);
        map.put(DAYS, 5L);
        fixtureP2Y5D = PeriodFields.periodFields(map);
        fixtureZeroYears = PeriodFields.periodFields(0, YEARS);
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
            {PeriodFields.periodFields(2, YEARS)},
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
        assertSame(PeriodFields.periodFields(MAP_EMPTY), PeriodFields.ZERO);
    }

    //-----------------------------------------------------------------------
    public void factory_singleField() {
        assertPeriodFields(PeriodFields.periodFields(1, YEARS), 1, YEARS);
        assertPeriodFields(fixtureZeroYears, 0, YEARS);
        assertPeriodFields(PeriodFields.periodFields(-1, YEARS), -1, YEARS);
        assertPeriodFields(PeriodFields.periodFields(Long.MAX_VALUE, YEARS), Long.MAX_VALUE, YEARS);
        assertPeriodFields(PeriodFields.periodFields(Long.MIN_VALUE, YEARS), Long.MIN_VALUE, YEARS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_singleField_null() {
        PeriodFields.periodFields(1, (PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    public void factory_map() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 1L);
        map.put(DAYS, -2L);
        PeriodFields test = PeriodFields.periodFields(map);
        assertPeriodFields(test, 1, YEARS, -2, DAYS);
    }

    public void factory_map_cloned() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 1L);
        PeriodFields test = PeriodFields.periodFields(map);
        assertPeriodFields(test, 1, YEARS);
        map.put(DAYS, 2L);
        assertPeriodFields(test, 1, YEARS);
    }

    public void factory_map_empty_singleton() {
        PeriodFields test = PeriodFields.periodFields(MAP_EMPTY);
        assertSame(test, PeriodFields.ZERO);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_map_null() {
        PeriodFields.periodFields(MAP_NULL);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_map_nullKey() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 1L);
        map.put(null, 2L);
        PeriodFields.periodFields(map);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_map_nullValue() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 1L);
        map.put(DAYS, null);
        PeriodFields.periodFields(map);
    }

    //-----------------------------------------------------------------------
    public void factory_period_provider() {
        PeriodProvider provider = Period.period(1, 2, 3, 4, 5, 6, 7);
        PeriodFields test = PeriodFields.periodFields(provider);
        assertEquals(test.size(), 7);
        assertEquals(test.get(YEARS), 1);
        assertEquals(test.get(MONTHS), 2);
        assertEquals(test.get(DAYS), 3);
        assertEquals(test.get(HOURS), 4);
        assertEquals(test.get(MINUTES), 5);
        assertEquals(test.get(SECONDS), 6);
        assertEquals(test.get(NANOS), 7);
    }

    public void factory_period_provider_zeroesRemoved() {
        PeriodProvider provider = Period.period(1, 0, 2, 0, 0, 0, 0);
        assertPeriodFields(PeriodFields.periodFields(provider), 1, YEARS, 2, DAYS);
    }

    public void factory_period_provider_zero() {
        PeriodProvider provider = Period.ZERO;
        assertSame(PeriodFields.periodFields(provider), PeriodFields.ZERO);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_period_provider_null() {
        PeriodProvider provider = null;
        PeriodFields.periodFields(provider);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_period_badProvider() {
        PeriodProvider provider = new MockPeriodProviderReturnsNull();
        PeriodFields.periodFields(provider);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertEquals(fixtureZeroYears.isZero(), true);
        assertEquals(PeriodFields.periodFields(1, YEARS).isZero(), false);
        
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 0L);
        map.put(DAYS, 0L);
        PeriodFields test = PeriodFields.periodFields(map);
        assertEquals(test.isZero(), true);
    }

    //-----------------------------------------------------------------------
    // get(PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_get() {
        assertEquals(fixtureP2Y5D.get(YEARS), 2L);
        assertEquals(fixtureP2Y5D.get(DAYS), 5L);
        assertEquals(fixtureZeroYears.get(YEARS), 0L);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_get_null() {
        fixtureP2Y5D.get((PeriodUnit) null);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_get_notPresent() {
        fixtureP2Y5D.get(MONTHS);
    }

    //-----------------------------------------------------------------------
    // getInt(PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_getInt() {
        assertEquals(fixtureP2Y5D.getInt(YEARS), 2);
        assertEquals(fixtureP2Y5D.getInt(DAYS), 5);
        assertEquals(fixtureZeroYears.getInt(YEARS), 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getInt_null() {
        fixtureP2Y5D.getInt((PeriodUnit) null);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getInt_notPresent() {
        fixtureP2Y5D.getInt(MONTHS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_getInt_tooBig() {
        PeriodFields.periodFields(Integer.MAX_VALUE + 1L, MONTHS).getInt(MONTHS);
    }

    //-----------------------------------------------------------------------
    // get(PeriodUnit,long)
    //-----------------------------------------------------------------------
    public void test_get_long() {
        assertEquals(fixtureP2Y5D.get(YEARS, 0), 2L);
        assertEquals(fixtureP2Y5D.get(DAYS, 0), 5L);
        assertEquals(fixtureZeroYears.get(YEARS, 1), 0L);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_get_long_null() {
        fixtureP2Y5D.get((PeriodUnit) null, 0L);
    }

    public void test_get_long_notPresent() {
        assertEquals(fixtureP2Y5D.get(MONTHS, 0L), 0L);
        assertEquals(fixtureP2Y5D.get(MONTHS, 101L), 101L);
    }

    //-----------------------------------------------------------------------
    // getInt(PeriodUnit, int)
    //-----------------------------------------------------------------------
    public void test_getInt_int() {
        assertEquals(fixtureP2Y5D.getInt(YEARS), 2);
        assertEquals(fixtureP2Y5D.getInt(DAYS), 5);
        assertEquals(fixtureZeroYears.getInt(YEARS), 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getInt_int_null() {
        fixtureP2Y5D.getInt((PeriodUnit) null);
    }

    public void test_getInt_int_notPresent() {
        assertEquals(fixtureP2Y5D.getInt(MONTHS, 0), 0);
        assertEquals(fixtureP2Y5D.getInt(MONTHS, 101), 101);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_getInt_int_tooBig() {
        PeriodFields.periodFields(Integer.MAX_VALUE + 1L, MONTHS).getInt(MONTHS);
    }

    //-----------------------------------------------------------------------
    // getQuiet(PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_getQuiet() {
        assertEquals(fixtureP2Y5D.getQuiet(YEARS), Long.valueOf(2));
        assertEquals(fixtureP2Y5D.getQuiet(DAYS), Long.valueOf(5));
        assertEquals(fixtureZeroYears.getQuiet(YEARS), Long.valueOf(0));
    }

    public void test_getQuiet_null() {
        assertEquals(fixtureP2Y5D.getQuiet((PeriodUnit) null), null);
    }

    public void test_getQuiet_notPresent() {
        assertEquals(fixtureP2Y5D.getQuiet(MONTHS), null);
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
    // size()
    //-----------------------------------------------------------------------
    public void test_size() {
        assertEquals(PeriodFields.ZERO.size(), 0);
        assertEquals(PeriodFields.periodFields(2, YEARS).size(), 1);
        assertEquals(fixtureP2Y5D.size(), 2);
        assertEquals(fixtureZeroYears.size(), 1);
    }

    //-----------------------------------------------------------------------
    // iterator()
    //-----------------------------------------------------------------------
    public void test_iterator() {
        Iterator<PeriodUnit> iterator = fixtureP2Y5D.iterator();
        assertEquals(iterator.hasNext(), true);
        assertEquals(iterator.next(), YEARS);
        assertEquals(iterator.hasNext(), true);
        assertEquals(iterator.next(), DAYS);
        assertEquals(iterator.hasNext(), false);
    }

    //-----------------------------------------------------------------------
    // withZeroesRemoved()
    //-----------------------------------------------------------------------
    public void test_withZeroesRemoved() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 2L);
        map.put(DAYS, 0L);
        map.put(HOURS, -3L);
        map.put(SECONDS, 0L);
        PeriodFields test = PeriodFields.periodFields(map);
        assertPeriodFields(test.withZeroesRemoved(), 2, YEARS, -3, HOURS);
    }

    public void test_withZeroesRemoved_toZero() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 0L);
        map.put(DAYS, 0L);
        map.put(HOURS, 0L);
        map.put(SECONDS, 0L);
        PeriodFields test = PeriodFields.periodFields(map);
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
        PeriodFields test = PeriodFields.periodFields(map);
        assertPeriodFields(test.with(fixtureP2Y5D), 2, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_with_PeriodFields_zeroYearsBase() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(MONTHS, 4L);
        map.put(DAYS, 8L);
        PeriodFields test = PeriodFields.periodFields(map);
        assertPeriodFields(fixtureZeroYears.with(test), 0, YEARS, 4, MONTHS, 8, DAYS);
    }

    public void test_with_PeriodFields_zeroYearsParam() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(MONTHS, 4L);
        map.put(DAYS, 8L);
        PeriodFields test = PeriodFields.periodFields(map);
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
    // with(int,PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_with_intPeriodUnit_add() {
        assertPeriodFields(fixtureP2Y5D.with(8, MONTHS), 2, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_with_intPeriodUnit_replace() {
        assertPeriodFields(fixtureP2Y5D.with(8, YEARS), 8, YEARS, 5, DAYS);
    }

    public void test_with_intPeriodUnit_noChange() {
        assertSame(fixtureP2Y5D.with(2, YEARS), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_intPeriodUnit_null() {
        fixtureP2Y5D.with(1, (PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // withUnitRemoved(PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_withUnitRemoved() {
        assertPeriodFields(fixtureP2Y5D.withUnitRemoved(DAYS), 2, YEARS);
    }

    public void test_withUnitRemoved_toZero() {
        assertSame(fixtureZeroYears.withUnitRemoved(YEARS), PeriodFields.ZERO);
    }

    public void test_withUnitRemoved_notPresent() {
        assertSame(fixtureP2Y5D.withUnitRemoved(MONTHS), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withUnitRemoved_null() {
        fixtureP2Y5D.withUnitRemoved((PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodFields)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodFields() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 4L);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.periodFields(map).plus(fixtureP2Y5D);
        assertPeriodFields(test, 6, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_plus_PeriodFields_zeroBase() {
        assertSame(PeriodFields.ZERO.plus(fixtureP2Y5D), fixtureP2Y5D);
    }

    public void test_plus_PeriodFields_zeroParam() {
        assertSame(fixtureP2Y5D.plus(PeriodFields.ZERO), fixtureP2Y5D);
        assertSame(fixtureP2Y5D.plus(fixtureZeroYears), fixtureP2Y5D);
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
        PeriodFields test = PeriodFields.periodFields(map);
        test.plus(PeriodFields.periodFields(1, YEARS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_PeriodFields_overflowTooSmall() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, Long.MIN_VALUE);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.periodFields(map);
        test.plus(PeriodFields.periodFields(-1, YEARS));
    }

    //-----------------------------------------------------------------------
    // minus(PeriodFields)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodFields() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 4L);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.periodFields(map).minus(fixtureP2Y5D);
        assertPeriodFields(test, 2, YEARS, 8, MONTHS, -5, DAYS);
    }

    public void test_minus_PeriodFields_zeroBase() {
        assertSame(PeriodFields.ZERO.minus(fixtureP2Y5D), fixtureP2Y5D);
    }

    public void test_minus_PeriodFields_zeroParam() {
        assertSame(fixtureP2Y5D.minus(PeriodFields.ZERO), fixtureP2Y5D);
        assertSame(fixtureP2Y5D.minus(fixtureZeroYears), fixtureP2Y5D);
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
        PeriodFields test = PeriodFields.periodFields(map);
        test.minus(PeriodFields.periodFields(-1, YEARS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_PeriodFields_overflowTooSmall() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, Long.MIN_VALUE);
        map.put(MONTHS, 8L);
        PeriodFields test = PeriodFields.periodFields(map);
        test.minus(PeriodFields.periodFields(1, YEARS));
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
        PeriodFields test = PeriodFields.periodFields(Long.MAX_VALUE / 2 + 1, YEARS);
        test.multipliedBy(2);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        PeriodFields test = PeriodFields.periodFields(Long.MIN_VALUE / 2 - 1, YEARS);
        test.multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    // dividedBy()
    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Map<PeriodUnit, Long> map = new HashMap<PeriodUnit, Long>();
        map.put(YEARS, 12L);
        map.put(MONTHS, 15L);
        PeriodFields test = PeriodFields.periodFields(map);
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
        assertPeriodFields(PeriodFields.periodFields(Long.MAX_VALUE, YEARS).negated(), -Long.MAX_VALUE, YEARS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_negated_overflow() {
        PeriodFields.periodFields(Long.MIN_VALUE, YEARS).negated();
    }

    //-----------------------------------------------------------------------
    // toUnitAmountMap()
    //-----------------------------------------------------------------------
    public void test_toUnitAmountMap() {
        SortedMap<PeriodUnit, Long> map = fixtureP2Y5D.toUnitAmountMap();
        assertEquals(map.size(), 2);
        assertEquals(map.get(YEARS), Long.valueOf(2));
        assertEquals(map.get(DAYS), Long.valueOf(5));
        map.clear();
        assertPeriodFields(fixtureP2Y5D, 2, YEARS, 5, DAYS);
    }

    //-----------------------------------------------------------------------
    // toPeriod()
    //-----------------------------------------------------------------------
    public void test_toPeriod1() {
        Period period = fixtureP2Y5D.toPeriod();
        assertEquals(period.getYears(), 2);
        assertEquals(period.getMonths(), 0);
        assertEquals(period.getDays(), 5);
        assertEquals(period.getHours(), 0);
        assertEquals(period.getMinutes(), 0);
        assertEquals(period.getSeconds(), 0);
        assertEquals(period.getNanos(), 0);
    }

    public void test_toPeriod2() {  // different set of fields to complete coverage
        PeriodFields test = PeriodFields.periodFields(1, MONTHS).with(2, HOURS);
        Period period = test.toPeriod();
        assertEquals(period.getYears(), 0);
        assertEquals(period.getMonths(), 1);
        assertEquals(period.getDays(), 0);
        assertEquals(period.getHours(), 2);
        assertEquals(period.getMinutes(), 0);
        assertEquals(period.getSeconds(), 0);
        assertEquals(period.getNanos(), 0);
    }

    public void test_toPeriod_allNonZero() {
        PeriodFields test = PeriodFields.periodFields(1, YEARS).with(2, MONTHS).with(3, DAYS)
            .with(4, HOURS).with(5, MINUTES).with(6, SECONDS).with(7, NANOS);
        Period period = test.toPeriod();
        assertEquals(period.getYears(), 1);
        assertEquals(period.getMonths(), 2);
        assertEquals(period.getDays(), 3);
        assertEquals(period.getHours(), 4);
        assertEquals(period.getMinutes(), 5);
        assertEquals(period.getSeconds(), 6);
        assertEquals(period.getNanos(), 7);
    }

    public void test_toPeriod_zeroYearsBase() {
        Period period = fixtureZeroYears.toPeriod();
        assertSame(period, Period.ZERO);
    }

    public void test_toPeriod_zeroBase() {
        Period period = PeriodFields.ZERO.toPeriod();
        assertSame(period, Period.ZERO);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toPeriod_invalidField() {
        PeriodFields test = fixtureP2Y5D.with(3, QUARTERS);
        test.toPeriod();
    }

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
        PeriodFields a = PeriodFields.periodFields(1, MONTHS);
        PeriodFields b = PeriodFields.periodFields(2, MONTHS);
        assertEquals(a.hashCode() == a.hashCode(), true);
        assertEquals(b.hashCode() == b.hashCode(), true);  // can only test true case
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {PeriodFields.ZERO, "{}"},
            {fixtureZeroYears, "{Years=0}"},
            {fixtureP2Y5D, "{Years=2, Days=5}"},
            {PeriodFields.periodFields(1, MONTHS), "{Months=1}"},
            {PeriodFields.periodFields(-1, DAYS), "{Days=-1}"},
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
        assertEquals(test.get(unit1), amount1);
        assertEquals(test.get(unit1, -100), amount1);
        assertEquals(test.getQuiet(unit1), Long.valueOf(amount1));
        assertEquals(test.isZero(), amount1 == 0);
        SortedMap<PeriodUnit, Long> map = test.toUnitAmountMap();
        assertEquals(map.size(), 1);
        assertEquals(map.get(unit1), Long.valueOf(amount1));
    }

    private void assertPeriodFields(PeriodFields test, long amount1, PeriodUnit unit1, long amount2, PeriodUnit unit2) {
        assertEquals(test.size(), 2);
        assertEquals(test.contains(unit1), true);
        assertEquals(test.contains(unit2), true);
        assertEquals(test.get(unit1), amount1);
        assertEquals(test.get(unit2), amount2);
        assertEquals(test.get(unit1, -100), amount1);
        assertEquals(test.get(unit2, -100), amount2);
        assertEquals(test.getQuiet(unit1), Long.valueOf(amount1));
        assertEquals(test.getQuiet(unit2), Long.valueOf(amount2));
        assertEquals(test.isZero(), amount1 == 0 && amount2 == 0);
        SortedMap<PeriodUnit, Long> map = test.toUnitAmountMap();
        assertEquals(map.size(), 2);
        assertEquals(map.get(unit1), Long.valueOf(amount1));
        assertEquals(map.get(unit2), Long.valueOf(amount2));
    }

    private void assertPeriodFields(PeriodFields test, long amount1, PeriodUnit unit1, long amount2, PeriodUnit unit2, long amount3, PeriodUnit unit3) {
        assertEquals(test.size(), 3);
        assertEquals(test.contains(unit1), true);
        assertEquals(test.contains(unit2), true);
        assertEquals(test.contains(unit3), true);
        assertEquals(test.get(unit1), amount1);
        assertEquals(test.get(unit2), amount2);
        assertEquals(test.get(unit3), amount3);
        assertEquals(test.get(unit1, -100), amount1);
        assertEquals(test.get(unit2, -100), amount2);
        assertEquals(test.get(unit3, -100), amount3);
        assertEquals(test.getQuiet(unit1), Long.valueOf(amount1));
        assertEquals(test.getQuiet(unit2), Long.valueOf(amount2));
        assertEquals(test.getQuiet(unit3), Long.valueOf(amount3));
        assertEquals(test.isZero(), amount1 == 0 && amount2 == 0 && amount3 == 0);
        SortedMap<PeriodUnit, Long> map = test.toUnitAmountMap();
        assertEquals(map.size(), 3);
        assertEquals(map.get(unit1), Long.valueOf(amount1));
        assertEquals(map.get(unit2), Long.valueOf(amount2));
        assertEquals(map.get(unit3), Long.valueOf(amount3));
    }

}
