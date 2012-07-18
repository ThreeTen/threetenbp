/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.extra;

import static javax.time.calendrical.LocalPeriodUnit.WEEKS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.time.calendrical.PeriodUnit;

import org.testng.annotations.Test;

/**
 * Test class.
 */
@Test
public class TestWeeks {

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Weeks.class));
    }

    //-----------------------------------------------------------------------
    public void test_factoryZeroSingleton() {
        assertSame(Weeks.ZERO, Weeks.of(0));
        assertSame(Weeks.ZERO, Weeks.of(0));
        assertEquals(0, Weeks.ZERO.getAmount());
    }

    //-----------------------------------------------------------------------
    public void test_factoryGetWeeks() {
        assertEquals(1,  Weeks.of(1).getAmount());
        assertEquals(2,  Weeks.of(2).getAmount());
        assertEquals(Integer.MAX_VALUE,  Weeks.of(Integer.MAX_VALUE).getAmount());
        assertEquals(-1,  Weeks.of(-1).getAmount());
        assertEquals(-2,  Weeks.of(-2).getAmount());
        assertEquals(Integer.MIN_VALUE,  Weeks.of(Integer.MIN_VALUE).getAmount());
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Weeks orginal = Weeks.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Weeks ser = (Weeks) in.readObject();
        assertSame(Weeks.ZERO, ser);
    }

    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_compareTo_null() {
        Weeks test5 = Weeks.of(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_isGreaterThan() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(false, test5.isGreaterThan(test5));
        assertEquals(false, test5.isGreaterThan(test6));
        assertEquals(true, test6.isGreaterThan(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_isGreaterThan_null() {
        Weeks test5 = Weeks.of(5);
        test5.isGreaterThan(null);
    }

    //-----------------------------------------------------------------------
    public void test_isLessThan() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(false, test5.isLessThan(test5));
        assertEquals(true, test5.isLessThan(test6));
        assertEquals(false, test6.isLessThan(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_isLessThan_null() {
        Weeks test5 = Weeks.of(5);
        test5.isLessThan(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Weeks test5 = Weeks.of(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Weeks test5 = Weeks.of(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Weeks test5 = Weeks.of(5);
        Weeks test6 = Weeks.of(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_getUnit() {
        PeriodUnit unit = Weeks.of(5).getUnit();
        assertNotNull(unit);
        assertEquals(unit, WEEKS);
    }

    //-----------------------------------------------------------------------
    public void test_plus() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.plus(0));
        assertEquals(Weeks.of(7), test5.plus(2));
        assertEquals(Weeks.of(3), test5.plus(-2));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).plus(-2);
    }

    //-----------------------------------------------------------------------
    public void test_plus_Weeks() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.plus(Weeks.of(0)));
        assertEquals(Weeks.of(7), test5.plus(Weeks.of(2)));
        assertEquals(Weeks.of(3), test5.plus(Weeks.of(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE),
                Weeks.of(Integer.MAX_VALUE - 1).plus(Weeks.of(1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE),
                Weeks.of(Integer.MIN_VALUE + 1).plus(Weeks.of(-1)));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_Weeks_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).plus(Weeks.of(2));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_Weeks_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).plus(Weeks.of(-2));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_plus_Weeks_null() {
        Weeks.of(Integer.MIN_VALUE + 1).plus(null);
    }

    //-----------------------------------------------------------------------
    public void test_minus() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.minus(0));
        assertEquals(Weeks.of(3), test5.minus(2));
        assertEquals(Weeks.of(7), test5.minus(-2));
        assertEquals(Weeks.of(Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Weeks.of(Integer.MIN_VALUE), Weeks.of(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).minus(2);
    }

    //-----------------------------------------------------------------------
    public void test_minus_Weeks() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(5), test5.minus(Weeks.of(0)));
        assertEquals(Weeks.of(3), test5.minus(Weeks.of(2)));
        assertEquals(Weeks.of(7), test5.minus(Weeks.of(-2)));
        assertEquals(Weeks.of(Integer.MAX_VALUE),
                Weeks.of(Integer.MAX_VALUE - 1).minus(Weeks.of(-1)));
        assertEquals(Weeks.of(Integer.MIN_VALUE),
                Weeks.of(Integer.MIN_VALUE + 1).minus(Weeks.of(1)));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_Weeks_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE - 1).minus(Weeks.of(-2));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_Weeks_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE + 1).minus(Weeks.of(2));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_minus_Weeks_null() {
        Weeks.of(Integer.MIN_VALUE + 1).minus(null);
    }

    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(0), test5.multipliedBy(0));
        assertEquals(Weeks.of(5), test5.multipliedBy(1));
        assertEquals(Weeks.of(10), test5.multipliedBy(2));
        assertEquals(Weeks.of(15), test5.multipliedBy(3));
        assertEquals(Weeks.of(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Weeks test5 = Weeks.of(5);
        assertEquals(Weeks.of(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_multipliedBy_overflowTooBig() {
        Weeks.of(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_multipliedBy_overflowTooSmall() {
        Weeks.of(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Weeks test12 = Weeks.of(12);
        assertEquals(Weeks.of(12), test12.dividedBy(1));
        assertEquals(Weeks.of(6), test12.dividedBy(2));
        assertEquals(Weeks.of(4), test12.dividedBy(3));
        assertEquals(Weeks.of(3), test12.dividedBy(4));
        assertEquals(Weeks.of(2), test12.dividedBy(5));
        assertEquals(Weeks.of(2), test12.dividedBy(6));
        assertEquals(Weeks.of(-4), test12.dividedBy(-3));
    }

    public void test_dividedBy_negate() {
        Weeks test12 = Weeks.of(12);
        assertEquals(Weeks.of(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_dividedBy_divideByZero() {
        Weeks.of(1).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Weeks.of(0), Weeks.of(0).negated());
        assertEquals(Weeks.of(-12), Weeks.of(12).negated());
        assertEquals(Weeks.of(12), Weeks.of(-12).negated());
        assertEquals(Weeks.of(-Integer.MAX_VALUE), Weeks.of(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_negated_overflow() {
        Weeks.of(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Weeks test5 = Weeks.of(5);
        assertEquals("P5W", test5.toString());
        Weeks testM1 = Weeks.of(-1);
        assertEquals("P-1W", testM1.toString());
    }

}
