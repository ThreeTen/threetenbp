/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.duration.field;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test class.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestYears {

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Years.class));
    }

    //-----------------------------------------------------------------------
    public void test_factoryZeroSingleton() {
        assertSame(Years.ZERO, Years.years(0));
        assertSame(Years.ZERO, Years.years(0));
        assertEquals(0, Years.ZERO.getAmount());
    }

    //-----------------------------------------------------------------------
    public void test_factoryGetYears() {
        assertEquals(1,  Years.years(1).getAmount());
        assertEquals(2,  Years.years(2).getAmount());
        assertEquals(Integer.MAX_VALUE,  Years.years(Integer.MAX_VALUE).getAmount());
        assertEquals(-1,  Years.years(-1).getAmount());
        assertEquals(-2,  Years.years(-2).getAmount());
        assertEquals(Integer.MIN_VALUE,  Years.years(Integer.MIN_VALUE).getAmount());
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Years orginal = Years.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Years ser = (Years) in.readObject();
        assertSame(Years.ZERO, ser);
    }

    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Years test5 = Years.years(5);
        Years test6 = Years.years(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_compareTo_null() {
        Years test5 = Years.years(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_isGreaterThan() {
        Years test5 = Years.years(5);
        Years test6 = Years.years(6);
        assertEquals(false, test5.isGreaterThan(test5));
        assertEquals(false, test5.isGreaterThan(test6));
        assertEquals(true, test6.isGreaterThan(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_isGreaterThan_null() {
        Years test5 = Years.years(5);
        test5.isGreaterThan(null);
    }

    //-----------------------------------------------------------------------
    public void test_isLessThan() {
        Years test5 = Years.years(5);
        Years test6 = Years.years(6);
        assertEquals(false, test5.isLessThan(test5));
        assertEquals(true, test5.isLessThan(test6));
        assertEquals(false, test6.isLessThan(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_isLessThan_null() {
        Years test5 = Years.years(5);
        test5.isLessThan(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Years test5 = Years.years(5);
        Years test6 = Years.years(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Years test5 = Years.years(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Years test5 = Years.years(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Years test5 = Years.years(5);
        Years test6 = Years.years(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_plus() {
        Years test5 = Years.years(5);
        assertEquals(Years.years(5), test5.plus(0));
        assertEquals(Years.years(7), test5.plus(2));
        assertEquals(Years.years(3), test5.plus(-2));
        assertEquals(Years.years(Integer.MAX_VALUE), Years.years(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Years.years(Integer.MIN_VALUE), Years.years(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_overflowTooBig() {
        Years.years(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_overflowTooSmall() {
        Years.years(Integer.MIN_VALUE + 1).plus(-2);
    }

    //-----------------------------------------------------------------------
    public void test_plus_Years() {
        Years test5 = Years.years(5);
        assertEquals(Years.years(5), test5.plus(Years.years(0)));
        assertEquals(Years.years(7), test5.plus(Years.years(2)));
        assertEquals(Years.years(3), test5.plus(Years.years(-2)));
        assertEquals(Years.years(Integer.MAX_VALUE),
                Years.years(Integer.MAX_VALUE - 1).plus(Years.years(1)));
        assertEquals(Years.years(Integer.MIN_VALUE),
                Years.years(Integer.MIN_VALUE + 1).plus(Years.years(-1)));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_Years_overflowTooBig() {
        Years.years(Integer.MAX_VALUE - 1).plus(Years.years(2));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_Years_overflowTooSmall() {
        Years.years(Integer.MIN_VALUE + 1).plus(Years.years(-2));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_plus_Years_null() {
        Years.years(Integer.MIN_VALUE + 1).plus(null);
    }

    //-----------------------------------------------------------------------
    public void test_minus() {
        Years test5 = Years.years(5);
        assertEquals(Years.years(5), test5.minus(0));
        assertEquals(Years.years(3), test5.minus(2));
        assertEquals(Years.years(7), test5.minus(-2));
        assertEquals(Years.years(Integer.MAX_VALUE), Years.years(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Years.years(Integer.MIN_VALUE), Years.years(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_overflowTooBig() {
        Years.years(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_overflowTooSmall() {
        Years.years(Integer.MIN_VALUE + 1).minus(2);
    }

    //-----------------------------------------------------------------------
    public void test_minus_Years() {
        Years test5 = Years.years(5);
        assertEquals(Years.years(5), test5.minus(Years.years(0)));
        assertEquals(Years.years(3), test5.minus(Years.years(2)));
        assertEquals(Years.years(7), test5.minus(Years.years(-2)));
        assertEquals(Years.years(Integer.MAX_VALUE),
                Years.years(Integer.MAX_VALUE - 1).minus(Years.years(-1)));
        assertEquals(Years.years(Integer.MIN_VALUE),
                Years.years(Integer.MIN_VALUE + 1).minus(Years.years(1)));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_Years_overflowTooBig() {
        Years.years(Integer.MAX_VALUE - 1).minus(Years.years(-2));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_Years_overflowTooSmall() {
        Years.years(Integer.MIN_VALUE + 1).minus(Years.years(2));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_minus_Years_null() {
        Years.years(Integer.MIN_VALUE + 1).minus(null);
    }

    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Years test5 = Years.years(5);
        assertEquals(Years.years(0), test5.multipliedBy(0));
        assertEquals(Years.years(5), test5.multipliedBy(1));
        assertEquals(Years.years(10), test5.multipliedBy(2));
        assertEquals(Years.years(15), test5.multipliedBy(3));
        assertEquals(Years.years(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Years test5 = Years.years(5);
        assertEquals(Years.years(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_multipliedBy_overflowTooBig() {
        Years.years(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_multipliedBy_overflowTooSmall() {
        Years.years(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Years test12 = Years.years(12);
        assertEquals(Years.years(12), test12.dividedBy(1));
        assertEquals(Years.years(6), test12.dividedBy(2));
        assertEquals(Years.years(4), test12.dividedBy(3));
        assertEquals(Years.years(3), test12.dividedBy(4));
        assertEquals(Years.years(2), test12.dividedBy(5));
        assertEquals(Years.years(2), test12.dividedBy(6));
        assertEquals(Years.years(-4), test12.dividedBy(-3));
    }

    public void test_dividedBy_negate() {
        Years test12 = Years.years(12);
        assertEquals(Years.years(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_dividedBy_divideByZero() {
        Years.years(1).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Years.years(0), Years.years(0).negated());
        assertEquals(Years.years(-12), Years.years(12).negated());
        assertEquals(Years.years(12), Years.years(-12).negated());
        assertEquals(Years.years(-Integer.MAX_VALUE), Years.years(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_negated_overflow() {
        Years.years(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Years test5 = Years.years(5);
        assertEquals("P5Y", test5.toString());
        Years testM1 = Years.years(-1);
        assertEquals("P-1Y", testM1.toString());
    }

}
