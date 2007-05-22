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
package javax.time;

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
public class TestMinutes {

    //-----------------------------------------------------------------------
    public void test_isSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(Minutes.class));
    }

    //-----------------------------------------------------------------------
    public void test_factoryZeroSingleton() {
        assertSame(Minutes.ZERO, Minutes.minutes(0));
        assertSame(Minutes.ZERO, Minutes.minutes(0));
        assertEquals(0, Minutes.ZERO.getMinutes());
    }

    //-----------------------------------------------------------------------
    public void test_factoryGetMinutes() {
        assertEquals(1,  Minutes.minutes(1).getMinutes());
        assertEquals(2,  Minutes.minutes(2).getMinutes());
        assertEquals(Integer.MAX_VALUE,  Minutes.minutes(Integer.MAX_VALUE).getMinutes());
        assertEquals(-1,  Minutes.minutes(-1).getMinutes());
        assertEquals(-2,  Minutes.minutes(-2).getMinutes());
        assertEquals(Integer.MIN_VALUE,  Minutes.minutes(Integer.MIN_VALUE).getMinutes());
    }

    //-----------------------------------------------------------------------
    public void test_deserializationSingleton() throws Exception {
        Minutes orginal = Minutes.ZERO;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Minutes ser = (Minutes) in.readObject();
        assertSame(Minutes.ZERO, ser);
    }

    //-----------------------------------------------------------------------
    public void test_compareTo() {
        Minutes test5 = Minutes.minutes(5);
        Minutes test6 = Minutes.minutes(6);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_compareTo_null() {
        Minutes test5 = Minutes.minutes(5);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_isGreaterThan() {
        Minutes test5 = Minutes.minutes(5);
        Minutes test6 = Minutes.minutes(6);
        assertEquals(false, test5.isGreaterThan(test5));
        assertEquals(false, test5.isGreaterThan(test6));
        assertEquals(true, test6.isGreaterThan(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_isGreaterThan_null() {
        Minutes test5 = Minutes.minutes(5);
        test5.isGreaterThan(null);
    }

    //-----------------------------------------------------------------------
    public void test_isLessThan() {
        Minutes test5 = Minutes.minutes(5);
        Minutes test6 = Minutes.minutes(6);
        assertEquals(false, test5.isLessThan(test5));
        assertEquals(true, test5.isLessThan(test6));
        assertEquals(false, test6.isLessThan(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_isLessThan_null() {
        Minutes test5 = Minutes.minutes(5);
        test5.isLessThan(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        Minutes test5 = Minutes.minutes(5);
        Minutes test6 = Minutes.minutes(6);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        Minutes test5 = Minutes.minutes(5);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        Minutes test5 = Minutes.minutes(5);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        Minutes test5 = Minutes.minutes(5);
        Minutes test6 = Minutes.minutes(6);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_plus() {
        Minutes test5 = Minutes.minutes(5);
        assertEquals(Minutes.minutes(5), test5.plus(0));
        assertEquals(Minutes.minutes(7), test5.plus(2));
        assertEquals(Minutes.minutes(3), test5.plus(-2));
        assertEquals(Minutes.minutes(Integer.MAX_VALUE), Minutes.minutes(Integer.MAX_VALUE - 1).plus(1));
        assertEquals(Minutes.minutes(Integer.MIN_VALUE), Minutes.minutes(Integer.MIN_VALUE + 1).plus(-1));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_overflowTooBig() {
        Minutes.minutes(Integer.MAX_VALUE - 1).plus(2);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_overflowTooSmall() {
        Minutes.minutes(Integer.MIN_VALUE + 1).plus(-2);
    }

    //-----------------------------------------------------------------------
    public void test_plus_Minutes() {
        Minutes test5 = Minutes.minutes(5);
        assertEquals(Minutes.minutes(5), test5.plus(Minutes.minutes(0)));
        assertEquals(Minutes.minutes(7), test5.plus(Minutes.minutes(2)));
        assertEquals(Minutes.minutes(3), test5.plus(Minutes.minutes(-2)));
        assertEquals(Minutes.minutes(Integer.MAX_VALUE),
                Minutes.minutes(Integer.MAX_VALUE - 1).plus(Minutes.minutes(1)));
        assertEquals(Minutes.minutes(Integer.MIN_VALUE),
                Minutes.minutes(Integer.MIN_VALUE + 1).plus(Minutes.minutes(-1)));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_Minutes_overflowTooBig() {
        Minutes.minutes(Integer.MAX_VALUE - 1).plus(Minutes.minutes(2));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_plus_Minutes_overflowTooSmall() {
        Minutes.minutes(Integer.MIN_VALUE + 1).plus(Minutes.minutes(-2));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_plus_Minutes_null() {
        Minutes.minutes(Integer.MIN_VALUE + 1).plus(null);
    }

    //-----------------------------------------------------------------------
    public void test_minus() {
        Minutes test5 = Minutes.minutes(5);
        assertEquals(Minutes.minutes(5), test5.minus(0));
        assertEquals(Minutes.minutes(3), test5.minus(2));
        assertEquals(Minutes.minutes(7), test5.minus(-2));
        assertEquals(Minutes.minutes(Integer.MAX_VALUE), Minutes.minutes(Integer.MAX_VALUE - 1).minus(-1));
        assertEquals(Minutes.minutes(Integer.MIN_VALUE), Minutes.minutes(Integer.MIN_VALUE + 1).minus(1));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_overflowTooBig() {
        Minutes.minutes(Integer.MAX_VALUE - 1).minus(-2);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_overflowTooSmall() {
        Minutes.minutes(Integer.MIN_VALUE + 1).minus(2);
    }

    //-----------------------------------------------------------------------
    public void test_minus_Minutes() {
        Minutes test5 = Minutes.minutes(5);
        assertEquals(Minutes.minutes(5), test5.minus(Minutes.minutes(0)));
        assertEquals(Minutes.minutes(3), test5.minus(Minutes.minutes(2)));
        assertEquals(Minutes.minutes(7), test5.minus(Minutes.minutes(-2)));
        assertEquals(Minutes.minutes(Integer.MAX_VALUE),
                Minutes.minutes(Integer.MAX_VALUE - 1).minus(Minutes.minutes(-1)));
        assertEquals(Minutes.minutes(Integer.MIN_VALUE),
                Minutes.minutes(Integer.MIN_VALUE + 1).minus(Minutes.minutes(1)));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_Minutes_overflowTooBig() {
        Minutes.minutes(Integer.MAX_VALUE - 1).minus(Minutes.minutes(-2));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_minus_Minutes_overflowTooSmall() {
        Minutes.minutes(Integer.MIN_VALUE + 1).minus(Minutes.minutes(2));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_minus_Minutes_null() {
        Minutes.minutes(Integer.MIN_VALUE + 1).minus(null);
    }

    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        Minutes test5 = Minutes.minutes(5);
        assertEquals(Minutes.minutes(0), test5.multipliedBy(0));
        assertEquals(Minutes.minutes(5), test5.multipliedBy(1));
        assertEquals(Minutes.minutes(10), test5.multipliedBy(2));
        assertEquals(Minutes.minutes(15), test5.multipliedBy(3));
        assertEquals(Minutes.minutes(-15), test5.multipliedBy(-3));
    }

    public void test_multipliedBy_negate() {
        Minutes test5 = Minutes.minutes(5);
        assertEquals(Minutes.minutes(-15), test5.multipliedBy(-3));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_multipliedBy_overflowTooBig() {
        Minutes.minutes(Integer.MAX_VALUE / 2 + 1).multipliedBy(2);
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_multipliedBy_overflowTooSmall() {
        Minutes.minutes(Integer.MIN_VALUE / 2 - 1).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        Minutes test12 = Minutes.minutes(12);
        assertEquals(Minutes.minutes(12), test12.dividedBy(1));
        assertEquals(Minutes.minutes(6), test12.dividedBy(2));
        assertEquals(Minutes.minutes(4), test12.dividedBy(3));
        assertEquals(Minutes.minutes(3), test12.dividedBy(4));
        assertEquals(Minutes.minutes(2), test12.dividedBy(5));
        assertEquals(Minutes.minutes(2), test12.dividedBy(6));
        assertEquals(Minutes.minutes(-4), test12.dividedBy(-3));
    }

    public void test_dividedBy_negate() {
        Minutes test12 = Minutes.minutes(12);
        assertEquals(Minutes.minutes(-4), test12.dividedBy(-3));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_dividedBy_divideByZero() {
        Minutes.minutes(1).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(Minutes.minutes(0), Minutes.minutes(0).negated());
        assertEquals(Minutes.minutes(-12), Minutes.minutes(12).negated());
        assertEquals(Minutes.minutes(12), Minutes.minutes(-12).negated());
        assertEquals(Minutes.minutes(-Integer.MAX_VALUE), Minutes.minutes(Integer.MAX_VALUE).negated());
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_negated_overflow() {
        Minutes.minutes(Integer.MIN_VALUE).negated();
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        Minutes test5 = Minutes.minutes(5);
        assertEquals("PT5M", test5.toString());
        Minutes testM1 = Minutes.minutes(-1);
        assertEquals("PT-1M", testM1.toString());
    }

}
