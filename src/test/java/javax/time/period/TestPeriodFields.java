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
import java.util.Iterator;
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

    private static final PeriodUnit DECADES = ISOChronology.periodDecades();
    private static final PeriodUnit YEARS = ISOChronology.periodYears();
    private static final PeriodUnit MONTHS = ISOChronology.periodMonths();
    private static final PeriodUnit DAYS = ISOChronology.periodDays();
    private static final PeriodUnit HOURS24 = ISOChronology.period24Hours();
    private static final PeriodUnit HOURS = ISOChronology.periodHours();
    private static final PeriodUnit MINUTES = ISOChronology.periodMinutes();
    private static final PeriodUnit SECONDS = ISOChronology.periodSeconds();
    private static final PeriodUnit MILLIS = ISOChronology.periodMillis();
    private static final PeriodUnit MICROS = ISOChronology.periodMicros();
    private static final PeriodUnit NANOS = ISOChronology.periodNanos();

    private PeriodFields fixtureP2Y5D;
    private PeriodFields fixtureZeroYears;

    @BeforeTest
    public void setUp() {
        fixtureP2Y5D = PeriodFields.of(PeriodField.of(2, YEARS), PeriodField.of(5, DAYS));
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
        assertSame(PeriodFields.of(), PeriodFields.ZERO);
    }

    //-----------------------------------------------------------------------
    // of(long,PeriodUnit)
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
    // of(PeriodField)
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
    // of(PeriodField...)
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
    // of(PeriodProvider)
    //-----------------------------------------------------------------------
    public void factory_of_provider() {
        PeriodProvider provider = Period.of(1, 2, 3, 4, 5, 6, 7);
        PeriodFields test = PeriodFields.of(provider);
        assertEquals(test.size(), 7);
        assertEquals(test.getAmount(YEARS), 1);
        assertEquals(test.getAmount(MONTHS), 2);
        assertEquals(test.getAmount(DAYS), 3);
        assertEquals(test.getAmount(HOURS), 4);
        assertEquals(test.getAmount(MINUTES), 5);
        assertEquals(test.getAmount(SECONDS), 6);
        assertEquals(test.getAmount(NANOS), 7);
    }

    public void factory_of_provider_zeroesRemoved() {
        PeriodProvider provider = Period.of(1, 0, 2, 0, 0, 0, 0);
        assertPeriodFields(PeriodFields.of(provider), 1, YEARS, 2, DAYS);
    }

    public void factory_of_provider_zero() {
        PeriodProvider provider = Period.ZERO;
        assertSame(PeriodFields.of(provider), PeriodFields.ZERO);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_provider_null() {
        PeriodFields.of((PeriodProvider) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_badProvider() {
        PeriodProvider provider = new MockPeriodProviderReturnsNull();
        PeriodFields.of(provider);
    }

    //-----------------------------------------------------------------------
    // total(PeriodProvider)
    //-----------------------------------------------------------------------
    public void factory_total_providers_empty() {
        PeriodProvider[] array = new PeriodProvider[0];
        PeriodFields test = PeriodFields.ofTotal(array);
        assertSame(test, PeriodFields.ZERO);
    }

    public void factory_total_providers_singleElement() {
        PeriodProvider[] array = new PeriodProvider[] {Period.of(1, 2, 3, 4, 5, 6, 7)};
        PeriodFields test = PeriodFields.ofTotal(array);
        assertEquals(test.size(), 7);
        assertEquals(test.getAmount(YEARS), 1);
        assertEquals(test.getAmount(MONTHS), 2);
        assertEquals(test.getAmount(DAYS), 3);
        assertEquals(test.getAmount(HOURS), 4);
        assertEquals(test.getAmount(MINUTES), 5);
        assertEquals(test.getAmount(SECONDS), 6);
        assertEquals(test.getAmount(NANOS), 7);
    }

    public void factory_total_providers_multipleElements_noAddition() {
        PeriodProvider[] array = new PeriodProvider[] {PeriodField.of(1, YEARS), PeriodField.of(2, MONTHS)};
        PeriodFields test = PeriodFields.ofTotal(array);
        assertEquals(test.size(), 2);
        assertEquals(test.getAmount(YEARS), 1);
        assertEquals(test.getAmount(MONTHS), 2);
    }

    public void factory_total_providers_multipleElements_addition() {
        PeriodProvider[] array = new PeriodProvider[] {PeriodField.of(1, YEARS), Period.ofYearsMonthsDays(2, 3, 4)};
        PeriodFields test = PeriodFields.ofTotal(array);
        assertEquals(test.size(), 3);
        assertEquals(test.getAmount(YEARS), 3);
        assertEquals(test.getAmount(MONTHS), 3);
        assertEquals(test.getAmount(DAYS), 4);
    }

    public void factory_total_providers_singleElement_zeroesRemoved() {
        PeriodProvider[] array = new PeriodProvider[] {Period.of(1, 0, 2, 0, 0, 0, 0)};
        assertPeriodFields(PeriodFields.ofTotal(array), 1, YEARS, 2, DAYS);
    }

    public void factory_total_providers_singleElement_zero() {
        PeriodProvider[] array = new PeriodProvider[] {Period.ZERO};
        assertSame(PeriodFields.ofTotal(array), PeriodFields.ZERO);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_total_providers_nullArray() {
        PeriodProvider[] array = null;
        PeriodFields.ofTotal(array);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_total_providers_nullSingleElement() {
        PeriodProvider[] array = new PeriodProvider[] {null};
        PeriodFields.ofTotal(array);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_total_providers_nullSecondElement() {
        PeriodProvider[] array = new PeriodProvider[] {PeriodField.of(5, DAYS), null};
        PeriodFields.ofTotal(array);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_total_badProviderSingleElement() {
        PeriodProvider[] array = new PeriodProvider[] {new MockPeriodProviderReturnsNull()};
        PeriodFields.ofTotal(array);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_total_badProviderSecondElement() {
        PeriodProvider[] array = new PeriodProvider[] {PeriodField.of(5, DAYS), new MockPeriodProviderReturnsNull()};
        PeriodFields.ofTotal(array);
    }

    //-----------------------------------------------------------------------
    // from(Duration)
    //-----------------------------------------------------------------------
    public void factory_of_Duration() {
        Duration dur = Duration.ofStandardHours(2).plusSeconds(32).plusNanos(345);
        PeriodFields test = PeriodFields.of(dur);
        assertEquals(test.size(), 2);
        assertEquals(test.getAmount(SECONDS), 2 * 3600 + 32);
        assertEquals(test.getAmount(NANOS), 345);
    }

    public void factory_of_Duration_zero() {
        Duration dur = Duration.ZERO;
        PeriodFields test = PeriodFields.of(dur);
        assertEquals(test.size(), 2);
        assertEquals(test.getAmount(SECONDS), 0);
        assertEquals(test.getAmount(NANOS), 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_Duration_null() {
        PeriodFields.of((Duration) null);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertEquals(fixtureZeroYears.isZero(), true);
        assertEquals(PeriodFields.of(1, YEARS).isZero(), false);
        
        PeriodFields test = PeriodFields.of(PeriodField.of(0, YEARS), PeriodField.of(0, DAYS));
        assertEquals(test.isZero(), true);
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    public void test_isPositive() {
        assertEquals(fixtureZeroYears.isPositive(), false);
        assertEquals(PeriodFields.of(1, YEARS).isPositive(), true);
        assertEquals(PeriodFields.of(-1, YEARS).isPositive(), false);
        
        assertEquals(PeriodFields.of(1, YEARS).with(2, DAYS).isPositive(), true);
        assertEquals(PeriodFields.of(1, YEARS).with(-2, DAYS).isPositive(), false);
        assertEquals(PeriodFields.of(-1, YEARS).with(2, DAYS).isPositive(), false);
        assertEquals(PeriodFields.of(-1, YEARS).with(-2, DAYS).isPositive(), false);
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero()
    //-----------------------------------------------------------------------
    public void test_isPositiveOrZero() {
        assertEquals(fixtureZeroYears.isPositiveOrZero(), true);
        assertEquals(PeriodFields.of(1, YEARS).isPositiveOrZero(), true);
        assertEquals(PeriodFields.of(-1, YEARS).isPositiveOrZero(), false);
        
        assertEquals(PeriodFields.of(1, YEARS).with(2, DAYS).isPositiveOrZero(), true);
        assertEquals(PeriodFields.of(1, YEARS).with(-2, DAYS).isPositiveOrZero(), false);
        assertEquals(PeriodFields.of(-1, YEARS).with(2, DAYS).isPositiveOrZero(), false);
        assertEquals(PeriodFields.of(-1, YEARS).with(-2, DAYS).isPositiveOrZero(), false);
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
    // get(PeriodUnit)
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
    // getAmount(PeriodUnit)
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
    // getAmountInt(PeriodUnit)
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

    //-----------------------------------------------------------------------
    // withZeroesRemoved()
    //-----------------------------------------------------------------------
    public void test_withZeroesRemoved() {
        PeriodFields test = PeriodFields.of(
                PeriodField.of(2, YEARS), PeriodField.of(0, DAYS),
                PeriodField.of(-3, HOURS), PeriodField.of(0, SECONDS));
        assertPeriodFields(test.withZeroesRemoved(), 2, YEARS, -3, HOURS);
    }

    public void test_withZeroesRemoved_toZero() {
        PeriodFields test = PeriodFields.of(
                PeriodField.of(0, YEARS), PeriodField.of(0, DAYS),
                PeriodField.of(0, HOURS), PeriodField.of(0, SECONDS));
        assertSame(test.withZeroesRemoved(), PeriodFields.ZERO);
    }

    public void test_withZeroesRemoved_zero() {
        assertSame(PeriodFields.ZERO.withZeroesRemoved(), PeriodFields.ZERO);
    }

    //-----------------------------------------------------------------------
    // with(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_with_PeriodProvider() {
        PeriodFields test = PeriodFields.of(PeriodField.of(4, YEARS), PeriodField.of(8, MONTHS));
        assertPeriodFields(test.with(fixtureP2Y5D), 2, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_with_PeriodProvider_PeriodField_add() {
        PeriodFields test = PeriodFields.of(PeriodField.of(4, YEARS), PeriodField.of(8, MONTHS));
        assertPeriodFields(test.with(PeriodField.of(5, DAYS)), 4, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_with_PeriodProvider_PeriodField_replace() {
        PeriodFields test = PeriodFields.of(PeriodField.of(4, YEARS), PeriodField.of(8, MONTHS));
        assertPeriodFields(test.with(PeriodField.of(5, MONTHS)), 4, YEARS, 5, MONTHS);
    }

    public void test_with_PeriodProvider_zeroYearsBase() {
        PeriodFields test = PeriodFields.of(PeriodField.of(4, MONTHS), PeriodField.of(8, DAYS));
        assertPeriodFields(fixtureZeroYears.with(test), 0, YEARS, 4, MONTHS, 8, DAYS);
    }

    public void test_with_PeriodProvider_zeroYearsParam() {
        PeriodFields test = PeriodFields.of(PeriodField.of(4, MONTHS), PeriodField.of(8, DAYS));
        assertPeriodFields(test.with(fixtureZeroYears), 0, YEARS, 4, MONTHS, 8, DAYS);
    }

    public void test_with_PeriodProvider_zeroBase() {
        assertSame(PeriodFields.ZERO.with(fixtureP2Y5D), fixtureP2Y5D);
    }

    public void test_with_PeriodProvider_zeroParam1() {
        assertSame(fixtureP2Y5D.with(PeriodFields.ZERO), fixtureP2Y5D);
    }

    public void test_with_PeriodProvider_zeroParam2() {
        assertPeriodFields(fixtureP2Y5D.with(PeriodFields.of(0, DAYS)), 2, YEARS, 0, DAYS);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_PeriodProvider_null() {
        fixtureP2Y5D.with((PeriodFields) null);
    }

    //-----------------------------------------------------------------------
    // with(long,PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_with_longPeriodUnit_add() {
        assertPeriodFields(fixtureP2Y5D.with(8L, MONTHS), 2, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_with_longPeriodUnit_replace() {
        assertPeriodFields(fixtureP2Y5D.with(8L, YEARS), 8, YEARS, 5, DAYS);
    }

    public void test_with_longPeriodUnit_addWithZeroAmount() {
        assertPeriodFields(fixtureP2Y5D.with(0L, MONTHS), 2, YEARS, 0, MONTHS, 5, DAYS);
    }

    public void test_with_longPeriodUnit_replaceWithZeroAmount() {
        assertPeriodFields(fixtureP2Y5D.with(0L, YEARS), 0, YEARS, 5, DAYS);
    }

    public void test_with_longPeriodUnit_noChange() {
        assertSame(fixtureP2Y5D.with(2L, YEARS), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_longPeriodUnit_null() {
        fixtureP2Y5D.with(1L, (PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // without(PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_without() {
        assertPeriodFields(fixtureP2Y5D.without(DAYS), 2, YEARS);
    }

    public void test_without_toZero() {
        assertSame(fixtureZeroYears.without(YEARS), PeriodFields.ZERO);
    }

    public void test_without_notPresent() {
        assertSame(fixtureP2Y5D.without(MONTHS), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_without_null() {
        fixtureP2Y5D.without((PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        PeriodFields base = PeriodFields.of(PeriodField.of(4, YEARS), PeriodField.of(8, MONTHS));
        PeriodFields test = base.plus(fixtureP2Y5D);
        assertPeriodFields(test, 6, YEARS, 8, MONTHS, 5, DAYS);
    }

    public void test_plus_PeriodProvider_zeroBase() {
        assertSame(PeriodFields.ZERO.plus(fixtureP2Y5D), fixtureP2Y5D);
    }

    public void test_plus_PeriodProvider_zeroParam() {
        assertEquals(fixtureP2Y5D.plus(PeriodFields.ZERO), fixtureP2Y5D);
        assertEquals(fixtureP2Y5D.plus(fixtureZeroYears), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        fixtureP2Y5D.plus((PeriodFields) null);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_PeriodProvider_overflowTooBig() {
        PeriodFields base = PeriodFields.of(Long.MAX_VALUE, YEARS);
        base.plus(PeriodField.of(1, YEARS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_PeriodProvider_overflowTooSmall() {
        PeriodFields base = PeriodFields.of(Long.MIN_VALUE, YEARS);
        base.plus(PeriodField.of(-1, YEARS));
    }

    //-----------------------------------------------------------------------
    // plus(long,PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_plus_longPeriodUnit_existingUnit() {
        PeriodFields base = PeriodFields.of(4, YEARS);
        PeriodFields test = base.plus(2, YEARS);
        assertPeriodFields(test, 6, YEARS);
    }

    public void test_plus_longPeriodUnit_newUnit() {
        PeriodFields base = PeriodFields.of(4, YEARS);
        PeriodFields test = base.plus(2, DAYS);
        assertPeriodFields(test, 4, YEARS, 2, DAYS);
    }

    public void test_plus_longPeriodUnit_zeroBase() {
        PeriodFields test = PeriodFields.ZERO.plus(2, DAYS);
        assertPeriodFields(test, 2, DAYS);
    }

    public void test_plus_longPeriodUnit_zeroAmountDifferentUnit() {
        PeriodFields test = PeriodFields.of(4, YEARS).plus(0, DAYS);
        assertPeriodFields(test, 4, YEARS, 0, DAYS);
    }

    public void test_plus_longPeriodUnit_same() {
        PeriodFields base = PeriodFields.of(4, YEARS);
        PeriodFields test = base.plus(0, YEARS);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_longPeriodUnit_null() {
        fixtureP2Y5D.plus(2, (PeriodUnit) null);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_longPeriodUnit_overflowTooBig() {
        PeriodFields base = PeriodFields.of(Long.MAX_VALUE, YEARS);
        base.plus(1, YEARS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_longPeriodUnit_overflowTooSmall() {
        PeriodFields base = PeriodFields.of(Long.MIN_VALUE, YEARS);
        base.plus(-1, YEARS);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodProvider() {
        PeriodFields base = PeriodFields.of(PeriodField.of(9, YEARS), PeriodField.of(8, MONTHS));
        PeriodFields test = base.minus(fixtureP2Y5D);
        assertPeriodFields(test, 7, YEARS, 8, MONTHS, -5, DAYS);
    }

    public void test_minus_PeriodProvider_zeroBase() {
        assertSame(PeriodFields.ZERO.minus(fixtureP2Y5D), fixtureP2Y5D);
    }

    public void test_minus_PeriodProvider_zeroParam() {
        assertEquals(fixtureP2Y5D.minus(PeriodFields.ZERO), fixtureP2Y5D);
        assertEquals(fixtureP2Y5D.minus(fixtureZeroYears), fixtureP2Y5D);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodProvider_null() {
        fixtureP2Y5D.minus((PeriodFields) null);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_PeriodProvider_overflowTooBig() {
        PeriodFields base = PeriodFields.of(Long.MAX_VALUE, YEARS);
        base.minus(PeriodField.of(-1, YEARS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_PeriodProvider_overflowTooSmall() {
        PeriodFields base = PeriodFields.of(Long.MIN_VALUE, YEARS);
        base.minus(PeriodField.of(1, YEARS));
    }

    //-----------------------------------------------------------------------
    // minus(long,PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_minus_longPeriodUnit_existingUnit() {
        PeriodFields base = PeriodFields.of(4, YEARS);
        PeriodFields test = base.minus(2, YEARS);
        assertPeriodFields(test, 2, YEARS);
    }

    public void test_minus_longPeriodUnit_newUnit() {
        PeriodFields base = PeriodFields.of(4, YEARS);
        PeriodFields test = base.minus(2, DAYS);
        assertPeriodFields(test, 4, YEARS, -2, DAYS);
    }

    public void test_minus_longPeriodUnit_zeroBase() {
        PeriodFields test = PeriodFields.ZERO.minus(2, DAYS);
        assertPeriodFields(test, -2, DAYS);
    }

    public void test_minus_longPeriodUnit_zeroAmountDifferentUnit() {
        PeriodFields test = PeriodFields.of(4, YEARS).minus(0, DAYS);
        assertPeriodFields(test, 4, YEARS, 0, DAYS);
    }

    public void test_minus_longPeriodUnit_same() {
        PeriodFields base = PeriodFields.of(4, YEARS);
        PeriodFields test = base.minus(0, YEARS);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_longPeriodUnit_null() {
        fixtureP2Y5D.minus(2, (PeriodUnit) null);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_longPeriodUnit_overflowTooBig() {
        PeriodFields base = PeriodFields.of(Long.MAX_VALUE, YEARS);
        base.minus(-1, YEARS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_longPeriodUnit_overflowTooSmall() {
        PeriodFields base = PeriodFields.of(Long.MIN_VALUE, YEARS);
        base.minus(1, YEARS);
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
        PeriodFields test = PeriodFields.of(PeriodField.of(12, YEARS), PeriodField.of(15, MONTHS));
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
    // retain(PeriodUnit...)
    //-----------------------------------------------------------------------
    public void test_retain() {
        assertPeriodFields(fixtureP2Y5D.retain(DAYS), 5, DAYS);
        assertPeriodFields(fixtureP2Y5D.retain(YEARS), 2, YEARS);
        assertPeriodFields(fixtureP2Y5D.retain(DAYS, YEARS), 2, YEARS, 5, DAYS);
        assertPeriodFields(fixtureP2Y5D.retain(DAYS, MONTHS, YEARS), 2, YEARS, 5, DAYS);
    }

    public void test_retain_toZero() {
        assertSame(fixtureZeroYears.retain(DAYS), PeriodFields.ZERO);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_retain_nullArray() {
        fixtureP2Y5D.retain((PeriodUnit[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_retain_nullItem() {
        fixtureP2Y5D.retain(DAYS, null);
    }

    //-----------------------------------------------------------------------
    // retainConvertible(PeriodUnit...)
    //-----------------------------------------------------------------------
    public void test_retainConvertible() {
        PeriodFields test = PeriodFields.of(2, DAYS).with(6, HOURS);
        assertPeriodFields(test.retainConvertible(SECONDS), 6, HOURS);
        assertPeriodFields(test.retainConvertible(MINUTES), 6, HOURS);
        assertPeriodFields(test.retainConvertible(HOURS), 6, HOURS);
        assertPeriodFields(test.retainConvertible(DAYS, SECONDS), 2, DAYS, 6, HOURS);
    }

    public void test_retainConvertible_noConversions() {
        assertPeriodFields(fixtureP2Y5D.retainConvertible(DAYS), 5, DAYS);
        assertPeriodFields(fixtureP2Y5D.retainConvertible(YEARS), 2, YEARS);
        assertPeriodFields(fixtureP2Y5D.retainConvertible(DAYS, YEARS), 2, YEARS, 5, DAYS);
        assertPeriodFields(fixtureP2Y5D.retainConvertible(DAYS, MONTHS, YEARS), 2, YEARS, 5, DAYS);
    }

    public void test_retainConvertible_toZero() {
        assertSame(fixtureZeroYears.retainConvertible(DAYS), PeriodFields.ZERO);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_retainConvertible_nullArray() {
        fixtureP2Y5D.retainConvertible((PeriodUnit[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_retainConvertible_nullItem() {
        fixtureP2Y5D.retainConvertible(DAYS, null);
    }

    //-----------------------------------------------------------------------
    // normalized(PeriodUnit...)
    //-----------------------------------------------------------------------
    public void test_normalized_units() {
        PeriodFields base = PeriodFields.of(5, YEARS).with(28, MONTHS).with(6, DAYS).with(27, HOURS).with(64, MINUTES);
        PeriodFields test = base.normalized(YEARS, MONTHS, DAYS, HOURS, MINUTES);
        assertEquals(test, PeriodFields.of(7, YEARS).with(4, MONTHS).with(6, DAYS).with(28, HOURS).with(4, MINUTES));
    }

    public void test_normalized_units_yearsMonths() {
        PeriodFields base = PeriodFields.of(5, YEARS).with(14, MONTHS);
        PeriodFields test = base.normalized(YEARS, MONTHS);
        assertEquals(test, PeriodFields.of(6, YEARS).with(2, MONTHS));
    }

    public void test_normalized_units_yearsMonths_exactOverflow() {
        PeriodFields base = PeriodFields.of(5, YEARS).with(12, MONTHS);
        PeriodFields test = base.normalized(YEARS, MONTHS);
        assertEquals(test, PeriodFields.of(6, YEARS).with(0, MONTHS));
    }

    public void test_normalized_units_yearsMonths_noYearsInitially() {
        PeriodFields base = PeriodFields.of(14, MONTHS);
        PeriodFields test = base.normalized(YEARS, MONTHS);
        assertEquals(test, PeriodFields.of(1, YEARS).with(2, MONTHS));
    }

    //-----------------------------------------------------------------------
    public void test_normalized_units_decadesYearsMonths_yearsMonths() {
        PeriodFields base = PeriodFields.of(3, DECADES).with(5, YEARS).with(27, MONTHS);
        PeriodFields test = base.normalized(YEARS, MONTHS);
        assertEquals(test, PeriodFields.of(37, YEARS).with(3, MONTHS));
    }

    public void test_normalized_units_decadesYearsMonths_years() {
        PeriodFields base = PeriodFields.of(3, DECADES).with(5, YEARS).with(27, MONTHS);
        PeriodFields test = base.normalized(YEARS);
        assertEquals(test, PeriodFields.of(37, YEARS).with(3, MONTHS));
    }

    public void test_normalized_units_decadesYearsMonths_months() {
        PeriodFields base = PeriodFields.of(2, DECADES).with(5, YEARS).with(27, MONTHS);
        PeriodFields test = base.normalized(MONTHS);
        assertEquals(test, PeriodFields.of(25 * 12 + 27, MONTHS));
    }

    //-----------------------------------------------------------------------
    public void test_normalized_units_hoursMinutesSeconds() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS);
        PeriodFields test = base.normalized(HOURS, MINUTES, SECONDS);
        assertEquals(test, PeriodFields.of(8, HOURS).with(15, MINUTES).with(7, SECONDS));
    }

    public void test_normalized_units_hoursMinutesSeconds_backwardsOrder() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS);
        PeriodFields test = base.normalized(SECONDS, MINUTES, HOURS);
        assertEquals(test, PeriodFields.of(8, HOURS).with(15, MINUTES).with(7, SECONDS));
    }

    public void test_normalized_units_hoursMinutesSeconds_hoursNotSpecified() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS);
        PeriodFields test = base.normalized(MINUTES, SECONDS);
        assertEquals(test, PeriodFields.of(5 * 60 + 74 + 7267 / 60, MINUTES).with(7, SECONDS));
    }

    public void test_normalized_units_hoursMinutesSeconds_minutesNotSpecified() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS);
        PeriodFields test = base.normalized(HOURS, SECONDS);
        assertEquals(test, PeriodFields.of(8, HOURS).with(14 * 60 + 67, SECONDS));
    }

    public void test_normalized_units_hoursMinutesSeconds_secondsNotSpecified() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS);
        PeriodFields test = base.normalized(HOURS, MINUTES);
        assertEquals(test, PeriodFields.of(8, HOURS).with(15, MINUTES).with(7, SECONDS));
    }

    public void test_normalized_units_hoursMinutesSeconds_onlyHours() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS);
        PeriodFields test = base.normalized(HOURS);
        assertEquals(test, PeriodFields.of(8, HOURS).with(14, MINUTES).with(67, SECONDS));
    }

    public void test_normalized_units_hoursMinutesSeconds_onlyMinutes() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS);
        PeriodFields test = base.normalized(MINUTES);
        assertEquals(test, PeriodFields.of(8 * 60 + 15, MINUTES).with(7, SECONDS));
    }

    //-----------------------------------------------------------------------
    public void test_normalized_units_hoursMinutesSeconds_multiOverflow1() {
        PeriodFields base = PeriodFields.of(23, HOURS).with(131, MINUTES).with(3667, SECONDS);
        PeriodFields test = base.normalized(HOURS24, HOURS, MINUTES, SECONDS);
        assertEquals(test, PeriodFields.of(1, HOURS24)
                .with(2, HOURS).with(12, MINUTES).with(7, SECONDS));
    }

    public void test_normalized_units_hoursMinutesSeconds_multiOverflow2() {
        PeriodFields base = PeriodFields.of(23, HOURS).with(133, MINUTES).with(1, SECONDS).with(999, MILLIS).with(2009, MICROS);
        PeriodFields test = base.normalized(HOURS24, HOURS, MINUTES, SECONDS, MILLIS, MICROS);
        assertEquals(test, PeriodFields.of(1, HOURS24)
                .with(1, HOURS).with(13, MINUTES).with(2, SECONDS).with(1, MILLIS).with(9, MICROS));
    }

    public void test_normalized_units_hoursMinutesSeconds_multiOverflow3() {
        PeriodFields base = PeriodFields.of(23, HOURS).with(133, MINUTES).with(59, SECONDS).with(999, MILLIS).with(2009, MICROS);
        PeriodFields test = base.normalized(HOURS24, HOURS, MINUTES, SECONDS, MILLIS, MICROS);
        assertEquals(test, PeriodFields.of(1, HOURS24)
                .with(1, HOURS).with(14, MINUTES).with(0, SECONDS).with(1, MILLIS).with(9, MICROS));
    }

    //-----------------------------------------------------------------------
    public void test_normalized_units_hoursMinutesSeconds_negative() {
        PeriodFields base = PeriodFields.of(-5, HOURS).with(74, MINUTES).with(-7267, SECONDS);
        PeriodFields test = base.normalized(HOURS, MINUTES, SECONDS);
        assertEquals(test, PeriodFields.of(-6, HOURS).with(13, MINUTES).with(-7, SECONDS));
    }

    public void test_normalized_units_hoursMinutesSeconds_bigSeconds() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(Long.MAX_VALUE, MINUTES).with(Long.MAX_VALUE, SECONDS);
        PeriodFields test = base.normalized(HOURS, MINUTES, SECONDS);
        long hours = Long.MAX_VALUE / 3600L + Long.MAX_VALUE / 60L;
        long mins = (Long.MAX_VALUE % 3600L) / 60L + Long.MAX_VALUE % 60L;
        long secs = Long.MAX_VALUE % 60L;
        assertEquals(test, PeriodFields.of(5 + hours, HOURS).with(mins, MINUTES).with(secs, SECONDS));
    }

    public void test_normalized_units_noUnits_noEffect() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS);
        PeriodFields test = base.normalized();
        assertEquals(test, PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS));
    }

    public void test_normalized_units_noOverlappingUnits_noEffect() {
        PeriodFields base = PeriodFields.of(5, HOURS).with(74, MINUTES).with(7267, SECONDS);
        PeriodFields test = base.normalized(YEARS, MONTHS);
        assertEquals(test, PeriodFields.of(0, YEARS).with(0, MONTHS).with(5, HOURS).with(74, MINUTES).with(7267, SECONDS));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_normalized_units_null() {
        PeriodFields.of(5, YEARS).normalized((PeriodUnit[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_normalized_units_arrayContainsNull() {
        PeriodFields.of(5, YEARS).normalized(null, YEARS);
    }

    //-----------------------------------------------------------------------
    // toTotal(PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_toTotal_unit_yearsMonthsToMonths() {
        PeriodField test = PeriodFields.of(5, YEARS).with(2, MONTHS).toTotal(MONTHS);
        assertEquals(test, PeriodField.of(5 * 12 + 2, MONTHS));
    }

    public void test_toTotal_unit_yearsToYears() {
        PeriodField test = PeriodFields.of(5, YEARS).toTotal(YEARS);
        assertEquals(test, PeriodField.of(5, YEARS));
    }

    public void test_toTotal_unit_yearsToMonths() {
        PeriodField test = PeriodFields.of(5, YEARS).toTotal(MONTHS);
        assertEquals(test, PeriodField.of(5 * 12, MONTHS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_toTotal_unit_tooBig() {
        PeriodFields.of(Long.MAX_VALUE / 12 + 12, YEARS).toTotal(MONTHS);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toTotal_unit_noConversion() {
        try {
            PeriodFields.of(5, YEARS).toTotal(DAYS);
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert Years to Days", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toTotal_unit_null() {
        PeriodFields.of(5, YEARS).toTotal(null);
    }

    //-----------------------------------------------------------------------
    // toEquivalent(PeriodUnit...)
    //-----------------------------------------------------------------------
    public void test_toEquivalent_units_yearsMonthsToMonths() {
        PeriodFields test = PeriodFields.of(5, YEARS).with(2, MONTHS).toEquivalent(MONTHS);
        assertEquals(test, PeriodFields.of(5 * 12 + 2, MONTHS));
    }

    public void test_toEquivalent_units_yearsToYears() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalent(new PeriodUnit[] {YEARS});
        assertEquals(test, PeriodFields.of(5, YEARS));
    }

    public void test_toEquivalent_units_yearsToMonths() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalent(MONTHS);
        assertEquals(test, PeriodFields.of(5 * 12, MONTHS));
    }

    public void test_toEquivalent_units_yearsToYearsMonthsOrDays() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalent(YEARS, MONTHS, DAYS);
        assertEquals(test, PeriodFields.of(5, YEARS));
    }

    public void test_toEquivalent_units_yearsToMonthsOrDays() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalent(MONTHS, DAYS);
        assertEquals(test, PeriodFields.of(5 * 12, MONTHS));
    }

    public void test_toEquivalent_units_yearsToDaysOrMonths() {
        PeriodFields test = PeriodFields.of(5, YEARS).toEquivalent(DAYS, MONTHS);
        assertEquals(test, PeriodFields.of(5 * 12, MONTHS));
    }

    public void test_toEquivalent_units_monthsToYearsMonths() {
        PeriodFields test = PeriodFields.of(14, MONTHS).toEquivalent(YEARS, MONTHS);
        assertEquals(test, PeriodFields.of(14, MONTHS));  // no normalization
    }

    public void test_toEquivalent_units_hoursToMinutesOrSeconds() {
        PeriodFields test = PeriodFields.of(5, HOURS).toEquivalent(MINUTES, SECONDS);
        assertEquals(test, PeriodFields.of(5 * 60, MINUTES));
    }

    public void test_toEquivalent_units_hoursToSecondsOrMinutes() {
        PeriodFields test = PeriodFields.of(5, HOURS).toEquivalent(SECONDS, MINUTES);
        assertEquals(test, PeriodFields.of(5 * 60 * 60, SECONDS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_toEquivalent_units_tooBig() {
        PeriodFields.of(Long.MAX_VALUE / 12 + 12, YEARS).toEquivalent(MONTHS);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalent_units_noUnits() {
        try {
            PeriodFields.of(5, YEARS).toEquivalent(new PeriodUnit[0]);
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert Years to any requested unit: []", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalent_units_noConversionOneUnit() {
        try {
            PeriodFields.of(5, YEARS).toEquivalent(new PeriodUnit[] {DAYS});
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert Years to any requested unit: [Days]", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalent_units_noConversionTwoUnits() {
        try {
            PeriodFields.of(5, YEARS).toEquivalent(DAYS, HOURS);
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert Years to any requested unit: [Days, Hours]", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toEquivalent_units_null() {
        PeriodFields.of(5, YEARS).toEquivalent((PeriodUnit[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toEquivalent_units_arrayContainsNull() {
        PeriodFields.of(5, YEARS).toEquivalent(null, YEARS);
    }

    //-----------------------------------------------------------------------
    // toMap()
    //-----------------------------------------------------------------------
    public void test_toMap() {
        SortedMap<PeriodUnit, PeriodField> test = fixtureP2Y5D.toMap();
        assertEquals(test.size(), 2);
        Iterator<PeriodUnit> iterator = test.keySet().iterator();
        assertEquals(iterator.next(), YEARS);
        assertEquals(iterator.next(), DAYS);
        assertEquals(test.get(YEARS), PeriodField.of(2, YEARS));
        assertEquals(test.get(DAYS), PeriodField.of(5, DAYS));
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_toMap_unmodifiable() {
        SortedMap<PeriodUnit, PeriodField> test = fixtureP2Y5D.toMap();
        test.clear();
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

    //-----------------------------------------------------------------------
    // toDuration()
    //-----------------------------------------------------------------------
    public void test_toDuration_hours() {
        Duration test = PeriodFields.of(5, HOURS).toDuration();
        Duration fiveHours = Duration.ofStandardHours(5);
        assertEquals(test, fiveHours);
    }

    public void test_toDuration_millis() {
        Duration test = PeriodFields.of(5, ISOChronology.periodMillis()).toDuration();
        Duration fiveMillis = Duration.ofMillis(5);
        assertEquals(test, fiveMillis);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toDuration_cannotConvert() {
        try {
            PeriodFields.of(5, MONTHS).toDuration();
        } catch (CalendricalException ex) {
            assertEquals(ex.getMessage(), "Unable to convert Months to any requested unit: [Seconds, Nanos]");
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // toPeriodFields()
    //-----------------------------------------------------------------------
    public void test_toPeriodFields() {
        PeriodFields base = PeriodFields.of(5, YEARS);
        assertSame(base.toPeriodFields(), base);
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
        assertEquals(test.isPositive(), amount1 > 0);
        assertEquals(test.isPositiveOrZero(), amount1 >= 0);
        SortedMap<PeriodUnit, PeriodField> map = test.toMap();
        assertEquals(map.size(), 1);
        assertEquals(map.get(unit1), PeriodField.of(amount1, unit1));
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
        assertEquals(test.isPositive(), amount1 > 0 && amount2 > 0);
        assertEquals(test.isPositiveOrZero(), amount1 >= 0 && amount2 >= 0);
        SortedMap<PeriodUnit, PeriodField> map = test.toMap();
        assertEquals(map.size(), 2);
        assertEquals(map.get(unit1), PeriodField.of(amount1, unit1));
        assertEquals(map.get(unit2), PeriodField.of(amount2, unit2));
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
        assertEquals(test.isPositive(), amount1 > 0 && amount2 > 0 && amount3 > 0);
        assertEquals(test.isPositiveOrZero(), amount1 >= 0 && amount2 >= 0 && amount3 >= 0);
        SortedMap<PeriodUnit, PeriodField> map = test.toMap();
        assertEquals(map.size(), 3);
        assertEquals(map.get(unit1), PeriodField.of(amount1, unit1));
        assertEquals(map.get(unit2), PeriodField.of(amount2, unit2));
        assertEquals(map.get(unit3), PeriodField.of(amount3, unit3));
    }

}
