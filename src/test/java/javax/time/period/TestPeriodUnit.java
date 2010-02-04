/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.List;

import javax.time.Duration;
import javax.time.calendar.PeriodUnit;

import org.testng.annotations.Test;

/**
 * Test PeriodUnit.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestPeriodUnit {

    private static final PeriodUnit BASIC = basic("TestBasic", Duration.seconds(1));
    private static final PeriodUnit DERIVED1 = new PeriodUnit("TestDerived", PeriodField.of(30, BASIC)) {
        private static final long serialVersionUID = 1L;
    };
    private static final PeriodUnit DERIVED2 = new PeriodUnit("TestDerivedDerived", PeriodField.of(20, DERIVED1)) {
        private static final long serialVersionUID = 1L;
    };

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Comparable.class.isAssignableFrom(PeriodField.class));
        assertTrue(Serializable.class.isAssignableFrom(PeriodField.class));
    }

    //-----------------------------------------------------------------------
    // basic()
    //-----------------------------------------------------------------------
    public void test_factory_basic() {
        Duration dur = Duration.seconds(30);
        PeriodUnit test = basic("TestFactoryBasic1", dur);
        assertEquals(test.getEquivalentPeriods().size(), 0);
        assertSame(test.getEstimatedDuration(), dur);
        assertEquals(test.getName(), "TestFactoryBasic1");
        assertEquals(test.toString(), "TestFactoryBasic1");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_basic_nullName() {
        basic(null, Duration.seconds(30));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_basic_nullDuration() {
        basic("TestFactoryBasic2", null);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_basic_zeroDuration() {
        basic("TestFactoryBasic3", Duration.ZERO);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_basic_negativeDuration() {
        basic("TestFactoryBasic3", Duration.nanos(-1));
    }

    //-----------------------------------------------------------------------
    // derived()
    //-----------------------------------------------------------------------
    public void test_factory_derived() {
        PeriodField pf = PeriodField.of(30, BASIC);
        PeriodUnit test = derived("TestFactoryDerived1", pf);
        assertEquals(test.getEquivalentPeriods().size(), 1);
        assertSame(test.getEquivalentPeriods().get(0), pf);
        assertEquals(test.getEstimatedDuration(), Duration.seconds(30));
        assertEquals(test.getName(), "TestFactoryDerived1");
        assertEquals(test.toString(), "TestFactoryDerived1");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_derived_nullName() {
        derived(null, PeriodField.of(30, BASIC));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_derived_nullPeriod() {
        derived("TestFactoryDerived2", null);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_derived_zeroPeriod() {
        derived("TestFactoryDerived3", PeriodField.of(0, BASIC));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_derived_negativePeriod() {
        derived("TestFactoryDerived3", PeriodField.of(-1, BASIC));
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    static class Basic extends PeriodUnit {
        static final Basic INSTANCE = new Basic();
        private static final long serialVersionUID = 1L;
         Basic() {
             super("TestSerializationBasic", Duration.seconds(30));
         }
         private Object readResolve() {
             return INSTANCE;
         }
    };
    public void test_serialization_basic() throws Exception {
        PeriodUnit original = Basic.INSTANCE;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(original);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        PeriodUnit ser = (PeriodUnit) in.readObject();
        assertSame(ser, original);
    }

    static class Derived extends PeriodUnit {
        static final Derived INSTANCE = new Derived();
        private static final long serialVersionUID = 1L;
        Derived() {
             super("TestSerializationDerived", PeriodField.of(40, DERIVED1));
         }
         private Object readResolve() {
             return INSTANCE;
         }
    };
    public void test_serialization_derived() throws Exception {
        PeriodUnit original = Derived.INSTANCE;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(original);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        PeriodUnit ser = (PeriodUnit) in.readObject();
        assertSame(ser, original);
    }

    //-----------------------------------------------------------------------
    // getEquivalentPeriods()
    //-----------------------------------------------------------------------
    public void test_getEquivalentPeriods_basic() {
        List<PeriodField> test = BASIC.getEquivalentPeriods();
        assertEquals(test.size(), 0);
    }

    public void test_getEquivalentPeriods_derived1() {
        List<PeriodField> test = DERIVED1.getEquivalentPeriods();
        assertEquals(test.size(), 1);
        assertEquals(test.get(0), PeriodField.of(30, BASIC));
    }

    public void test_getEquivalentPeriods_derived2() {
        List<PeriodField> test = DERIVED2.getEquivalentPeriods();
        assertEquals(test.size(), 2);
        assertEquals(test.get(0), PeriodField.of(20, DERIVED1));
        assertEquals(test.get(1), PeriodField.of(20 * 30, BASIC));
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_getEquivalentPeriods_basic_unmodifiable_add() {
        List<PeriodField> test = BASIC.getEquivalentPeriods();
        test.add(null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_getEquivalentPeriods_derived_unmodifiable_add() {
        List<PeriodField> test = DERIVED1.getEquivalentPeriods();
        test.add(null);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_getEquivalentPeriods_derived_unmodifiable_clear() {
        List<PeriodField> test = DERIVED1.getEquivalentPeriods();
        test.clear();
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_getEquivalentPeriods_derived_unmodifiable_set() {
        List<PeriodField> test = DERIVED1.getEquivalentPeriods();
        test.set(0, null);
    }

    //-----------------------------------------------------------------------
    // getEquivalentPeriod(PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_getEquivalentPeriod_unit_basic() {
        assertEquals(BASIC.getEquivalentPeriod(BASIC), PeriodField.of(1, BASIC));
        assertEquals(BASIC.getEquivalentPeriod(DERIVED1), null);
        assertEquals(BASIC.getEquivalentPeriod(DERIVED2), null);
    }

    public void test_getEquivalentPeriod_unit_derived() {
        assertEquals(DERIVED2.getEquivalentPeriod(BASIC), PeriodField.of(20 * 30, BASIC));
        assertEquals(DERIVED2.getEquivalentPeriod(DERIVED1), PeriodField.of(20, DERIVED1));
        assertEquals(DERIVED2.getEquivalentPeriod(DERIVED2), PeriodField.of(1, DERIVED2));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getEquivalentPeriod_null() {
        BASIC.getEquivalentPeriod((PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_basic() {
        PeriodUnit test1 = basic("TestCompareTo1", Duration.seconds(30));
        PeriodUnit test2 = basic("TestCompareTo2", Duration.seconds(40));
        assertEquals(test1.compareTo(test1), 0);
        assertEquals(test1.compareTo(test2), -1);
        assertEquals(test2.compareTo(test1), 1);
    }

    public void test_compareTo_durationThenName() {
        PeriodUnit test1 = basic("TestCompareTo1", Duration.seconds(30));
        PeriodUnit test2 = basic("TestCompareTo2", Duration.seconds(30));
        PeriodUnit test3 = basic("TestCompareTo0", Duration.seconds(40));
        assertEquals(test1.compareTo(test1), 0);
        assertTrue(test1.compareTo(test2) < 0);
        assertTrue(test1.compareTo(test3) < 0);
        assertTrue(test2.compareTo(test1) > 0);
        assertTrue(test2.compareTo(test3) < 0);
        assertTrue(test3.compareTo(test1) > 0);
        assertTrue(test3.compareTo(test2) > 0);
    }

    public void test_compareTo_derived() {
        PeriodUnit test1 = DERIVED1;
        PeriodUnit test2 = DERIVED2;
        assertEquals(test1.compareTo(test1), 0);
        assertTrue(test1.compareTo(test2) < 0);
        assertTrue(test2.compareTo(test1) > 0);
    }

    public void test_compareTo_basicDerived() {
        PeriodUnit test1 = basic(DERIVED1.getName(), DERIVED1.getEstimatedDuration());
        PeriodUnit test2 = DERIVED1;
        assertEquals(test1.compareTo(test1), 0);
        assertTrue(test1.compareTo(test2) < 0);
        assertTrue(test2.compareTo(test1) > 0);
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_compareTo_null() {
        PeriodUnit test5 = basic("TestCompareToNull", Duration.seconds(30));
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals_basicDerived() {
        PeriodUnit test1 = BASIC;
        PeriodUnit test2 = DERIVED1;
        PeriodUnit test3 = DERIVED2;
        PeriodUnit test4 = basic(DERIVED1.getName(), DERIVED1.getEstimatedDuration());
        assertEquals(test1.equals(test1), true);
        assertEquals(test1.equals(test2), false);
        assertEquals(test1.equals(test3), false);
        assertEquals(test1.equals(test4), false);
        assertEquals(test2.equals(test1), false);
        assertEquals(test2.equals(test2), true);
        assertEquals(test2.equals(test3), false);
        assertEquals(test2.equals(test4), false);
        assertEquals(test3.equals(test1), false);
        assertEquals(test3.equals(test2), false);
        assertEquals(test3.equals(test3), true);
        assertEquals(test3.equals(test4), false);
        assertEquals(test4.equals(test1), false);
        assertEquals(test4.equals(test2), false);
        assertEquals(test4.equals(test3), false);
        assertEquals(test4.equals(test4), true);
    }

    public void test_equals_nameDuration() {
        PeriodUnit test1 = basic("TestEquals1", Duration.seconds(30));
        PeriodUnit test2 = basic("TestEquals2", Duration.seconds(30));
        PeriodUnit test3 = basic("TestEquals2", Duration.seconds(40));
        assertEquals(test1.equals(test1), true);
        assertEquals(test1.equals(test2), false);
        assertEquals(test1.equals(test3), false);
        assertEquals(test2.equals(test1), false);
        assertEquals(test2.equals(test2), true);
        assertEquals(test2.equals(test3), false);
        assertEquals(test3.equals(test1), false);
        assertEquals(test3.equals(test2), false);
        assertEquals(test3.equals(test3), true);
    }

    public void test_equals_null() {
        PeriodUnit test = basic("TestEqualsNull", Duration.seconds(30));
        assertEquals(false, test.equals(null));
    }

    public void test_equals_otherClass() {
        PeriodUnit test = basic("TestEqualsOther", Duration.seconds(30));
        assertEquals(false, test.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        PeriodUnit test1 = basic("TestHashCode", Duration.seconds(30));
        PeriodUnit test2 = basic("TestHashCode", Duration.seconds(40));
        assertEquals(test1.hashCode() == test1.hashCode(), true);
        assertEquals(test1.hashCode() == test2.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        PeriodUnit test = basic("TestToString", Duration.seconds(30));
        assertEquals(test.toString(), "TestToString");
    }

    //-----------------------------------------------------------------------
    private static PeriodUnit basic(String name, Duration duration) {
        return new PeriodUnit(name, duration) {
            private static final long serialVersionUID = 1L;
        };
    }

    private static PeriodUnit derived(String name, PeriodField period) {
        return new PeriodUnit(name, period) {
            private static final long serialVersionUID = 1L;
        };
    }

}
