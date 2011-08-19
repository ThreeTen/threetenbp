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
package javax.time.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.time.Duration;

import org.testng.annotations.Test;

/**
 * Test PeriodUnit.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestPeriodUnit {

    private static final int EQUIV1 = 30;
    private static final int EQUIV2 = 20;
    private static final PeriodUnit BASIC = basic("TestBasic", Duration.ofSeconds(1));
    private static final PeriodUnit DERIVED1 = new PeriodUnit("TestDerived", EQUIV1, BASIC) {
        private static final long serialVersionUID = 1L;
    };
    private static final PeriodUnit DERIVED2 = new PeriodUnit("TestDerivedDerived", EQUIV1 * EQUIV2, BASIC) {
        private static final long serialVersionUID = 1L;
    };
    private static final PeriodUnit UNRELATED = basic("Unrelated", Duration.ofSeconds(4));

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Comparable.class.isAssignableFrom(PeriodField.class));
        assertTrue(Serializable.class.isAssignableFrom(PeriodField.class));
    }

    //-----------------------------------------------------------------------
    // basic()
    //-----------------------------------------------------------------------
    public void test_factory_basic() {
        Duration dur = Duration.ofSeconds(EQUIV1);
        PeriodUnit test = basic("TestFactoryBasic1", dur);
        assertSame(test.getBaseUnit(), test);
        assertEquals(test.getBaseEquivalent(), test.field(1));
        assertSame(test.getDurationEstimate(), dur);
        assertEquals(test.getName(), "TestFactoryBasic1");
        assertEquals(test.toString(), "TestFactoryBasic1");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_basic_nullName() {
        basic(null, Duration.ofSeconds(EQUIV1));
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
        basic("TestFactoryBasic3", Duration.ofNanos(-1));
    }

    //-----------------------------------------------------------------------
    // derived()
    //-----------------------------------------------------------------------
    public void test_factory_derived() {
        PeriodField pf = PeriodField.of(EQUIV1, BASIC);
        PeriodUnit test = derived("TestFactoryDerived1", pf);
        assertSame(test.getBaseUnit(), BASIC);
        assertEquals(test.getBaseEquivalent(), pf);
        assertEquals(test.getDurationEstimate(), Duration.ofSeconds(EQUIV1));
        assertEquals(test.getName(), "TestFactoryDerived1");
        assertEquals(test.toString(), "TestFactoryDerived1");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_derived_nullName() {
        derived(null, PeriodField.of(EQUIV1, BASIC));
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
             super("TestSerializationBasic", Duration.ofSeconds(EQUIV1));
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
             super("TestSerializationDerived", 40 * EQUIV1, BASIC);
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
    // getBaseEquivalent()
    //-----------------------------------------------------------------------
    public void test_getBaseEquivalent() {
        assertEquals(BASIC.getBaseEquivalent(), BASIC.field(1));
        assertEquals(DERIVED1.getBaseEquivalent(), BASIC.field(EQUIV1));
        assertEquals(DERIVED2.getBaseEquivalent(), BASIC.field(EQUIV2 * EQUIV1));
    }

    //-----------------------------------------------------------------------
    // getBaseUnit()
    //-----------------------------------------------------------------------
    public void test_getBaseUnit() {
        assertEquals(BASIC.getBaseUnit(), BASIC);
        assertEquals(DERIVED1.getBaseUnit(), BASIC);
        assertEquals(DERIVED2.getBaseUnit(), BASIC);
    }

    //-----------------------------------------------------------------------
    // getDurationEstimate()
    //-----------------------------------------------------------------------
    public void test_getDurationEstimate() {
        assertEquals(BASIC.getDurationEstimate(), Duration.ofSeconds(1));
        assertEquals(DERIVED1.getDurationEstimate(), Duration.ofSeconds(EQUIV1));
    }

    //-----------------------------------------------------------------------
    // toEquivalent(PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_getEquivalentPeriod_unit_basic() {
        assertEquals(BASIC.toEquivalent(BASIC), 1);
        assertEquals(BASIC.toEquivalent(DERIVED1), -1);
        assertEquals(BASIC.toEquivalent(DERIVED2), -1);
    }

    public void test_getEquivalentPeriod_unit_derived() {
        assertEquals(DERIVED2.toEquivalent(BASIC), EQUIV2 * EQUIV1);
        assertEquals(DERIVED2.toEquivalent(DERIVED1), EQUIV2);
        assertEquals(DERIVED2.toEquivalent(DERIVED2), 1);
        assertEquals(DERIVED1.toEquivalent(DERIVED2), -1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getEquivalentPeriod_null() {
    	BASIC.toEquivalent((PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // convertEquivalent(PeriodField)
    //-----------------------------------------------------------------------
    public void test_convertEquivalent_PeriodField_same() {
        PeriodField field = BASIC.field(3);
        assertSame(BASIC.convertEquivalent(field), field);
        field = DERIVED1.field(-5);
        assertSame(DERIVED1.convertEquivalent(field), field);
    }

    public void test_convertEquivalent_PeriodField_convertible() {
        assertEquals(BASIC.convertEquivalent(DERIVED1.field(4)), BASIC.field(4 * EQUIV1));
        assertEquals(BASIC.convertEquivalent(DERIVED2.field(-5)), BASIC.field(-5 * EQUIV2 * EQUIV1));
    }

    public void test_convertEquivalent_PeriodField_notConvertible() {
        assertEquals(DERIVED1.convertEquivalent(BASIC.field(EQUIV1)), null);
        assertEquals(DERIVED2.convertEquivalent(BASIC.field(4)), null);
        assertEquals(UNRELATED.convertEquivalent(BASIC.field(4)), null);
        assertEquals(BASIC.convertEquivalent(UNRELATED.field(4)), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_convertEquivalent_PeriodField_null() {
        BASIC.convertEquivalent((PeriodField) null);
    }

    //-----------------------------------------------------------------------
    // convertEquivalent(long,PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_convertEquivalent_PeriodUnit_same() {
        assertEquals(BASIC.convertEquivalent(3, BASIC), BASIC.field(3));
        assertEquals(DERIVED1.convertEquivalent(-5, DERIVED1), DERIVED1.field(-5));
        assertEquals(DERIVED2.convertEquivalent(12, DERIVED2), DERIVED2.field(12));
    }

    public void test_convertEquivalent_PeriodUnit_convertible() {
        assertEquals(BASIC.convertEquivalent(4, DERIVED1), BASIC.field(4 * EQUIV1));
        assertEquals(BASIC.convertEquivalent(-5, DERIVED2), BASIC.field(-5 * EQUIV2 * EQUIV1));
    }

    public void test_convertEquivalent_PeriodUnit_notConvertible() {
        assertEquals(DERIVED1.convertEquivalent(EQUIV1, BASIC), null);
        assertEquals(DERIVED2.convertEquivalent(4, BASIC), null);
        assertEquals(UNRELATED.convertEquivalent(4, BASIC), null);
        assertEquals(BASIC.convertEquivalent(4, UNRELATED), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_convertEquivalent_PeriodUnit_null() {
        BASIC.convertEquivalent(1, (PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // field(long)
    //-----------------------------------------------------------------------
    public void test_field() {
        assertEquals(BASIC.field(2), PeriodField.of(2, BASIC));
        assertEquals(DERIVED1.field(-3517369), PeriodField.of(-3517369,DERIVED1));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo_basic() {
        PeriodUnit test1 = basic("TestCompareTo1", Duration.ofSeconds(EQUIV1));
        PeriodUnit test2 = basic("TestCompareTo2", Duration.ofSeconds(40));
        assertEquals(test1.compareTo(test1), 0);
        assertEquals(test1.compareTo(test2), -1);
        assertEquals(test2.compareTo(test1), 1);
    }

    public void test_compareTo_durationThenName() {
        PeriodUnit test1 = basic("TestCompareTo1", Duration.ofSeconds(EQUIV1));
        PeriodUnit test2 = basic("TestCompareTo2", Duration.ofSeconds(EQUIV1));
        PeriodUnit test3 = basic("TestCompareTo0", Duration.ofSeconds(40));
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
        PeriodUnit test1 = basic(DERIVED1.getName(), DERIVED1.getDurationEstimate());
        PeriodUnit test2 = DERIVED1;
        assertEquals(test1.compareTo(test1), 0);
        assertTrue(test1.compareTo(test2) < 0);
        assertTrue(test2.compareTo(test1) > 0);
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_compareTo_null() {
        PeriodUnit test5 = basic("TestCompareToNull", Duration.ofSeconds(EQUIV1));
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals_basicDerived() {
        PeriodUnit test1 = BASIC;
        PeriodUnit test2 = DERIVED1;
        PeriodUnit test3 = DERIVED2;
        PeriodUnit test4 = basic(DERIVED1.getName(), DERIVED1.getDurationEstimate());
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
        PeriodUnit test1 = basic("TestEquals1", Duration.ofSeconds(EQUIV1));
        PeriodUnit test2 = basic("TestEquals2", Duration.ofSeconds(EQUIV1));
        PeriodUnit test3 = basic("TestEquals2", Duration.ofSeconds(40));
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
        PeriodUnit test = basic("TestEqualsNull", Duration.ofSeconds(EQUIV1));
        assertEquals(false, test.equals(null));
    }

    public void test_equals_otherClass() {
        PeriodUnit test = basic("TestEqualsOther", Duration.ofSeconds(EQUIV1));
        assertEquals(false, test.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        PeriodUnit test1 = basic("TestHashCode", Duration.ofSeconds(EQUIV1));
        PeriodUnit test2 = basic("TestHashCode", Duration.ofSeconds(40));
        assertEquals(test1.hashCode() == test1.hashCode(), true);
        assertEquals(test1.hashCode() == test2.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        PeriodUnit test = basic("TestToString", Duration.ofSeconds(EQUIV1));
        assertEquals(test.toString(), "TestToString");
    }

    //-----------------------------------------------------------------------
    private static PeriodUnit basic(String name, Duration duration) {
        return new PeriodUnit(name, duration) {
            private static final long serialVersionUID = 1L;
        };
    }

    private static PeriodUnit derived(String name, PeriodField period) {
        return new PeriodUnit(name, period.getAmount(), period.getUnit()) {
            private static final long serialVersionUID = 1L;
        };
    }

}
