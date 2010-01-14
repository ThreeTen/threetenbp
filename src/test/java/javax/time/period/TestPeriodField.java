/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.Duration;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.PeriodUnit;

import org.testng.annotations.Test;

/**
 * Test class.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestPeriodField {

    private static final PeriodUnit DAYS = ISOChronology.periodDays();
    private static final PeriodUnit MONTHS = ISOChronology.periodMonths();

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Comparable.class.isAssignableFrom(PeriodField.class));
        assertTrue(Serializable.class.isAssignableFrom(PeriodField.class));
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    public void test_factory_of() {
        assertEquals(1,  PeriodField.of(1, DAYS).getAmount());
        assertEquals(2,  PeriodField.of(2, DAYS).getAmount());
        assertEquals(Long.MAX_VALUE,  PeriodField.of(Long.MAX_VALUE, DAYS).getAmount());
        assertEquals(-1,  PeriodField.of(-1, DAYS).getAmount());
        assertEquals(-2,  PeriodField.of(-2, DAYS).getAmount());
        assertEquals(Long.MIN_VALUE,  PeriodField.of(Long.MIN_VALUE, DAYS).getAmount());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_of_null() {
        PeriodField.of(1, null);
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        PeriodField orginal = PeriodField.of(3, DAYS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        PeriodField ser = (PeriodField) in.readObject();
        assertEquals(PeriodField.of(3, DAYS), ser);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertEquals(PeriodField.of(0, DAYS).isZero(), true);
        assertEquals(PeriodField.of(1, DAYS).isZero(), false);
        assertEquals(PeriodField.of(-1, DAYS).isZero(), false);
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    public void test_isPositive() {
        assertEquals(PeriodField.of(0, DAYS).isPositive(), true);
        assertEquals(PeriodField.of(1, DAYS).isPositive(), true);
        assertEquals(PeriodField.of(-1, DAYS).isPositive(), false);
    }

    //-----------------------------------------------------------------------
    // isNegative()
    //-----------------------------------------------------------------------
    public void test_isNegative() {
        assertEquals(PeriodField.of(0, DAYS).isNegative(), false);
        assertEquals(PeriodField.of(1, DAYS).isNegative(), false);
        assertEquals(PeriodField.of(-1, DAYS).isNegative(), true);
    }

    //-----------------------------------------------------------------------
    // getAmount()
    //-----------------------------------------------------------------------
    public void test_getAmount() {
        assertEquals(PeriodField.of(0, DAYS).getAmount(), 0L);
        assertEquals(PeriodField.of(1, DAYS).getAmount(), 1L);
        assertEquals(PeriodField.of(-1, DAYS).getAmount(), -1L);
    }

    //-----------------------------------------------------------------------
    // getAmountInt()
    //-----------------------------------------------------------------------
    public void test_getAmountInt() {
        assertEquals(PeriodField.of(0, DAYS).getAmountInt(), 0L);
        assertEquals(PeriodField.of(1, DAYS).getAmountInt(), 1L);
        assertEquals(PeriodField.of(-1, DAYS).getAmountInt(), -1L);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_getAmountInt_tooBig() {
        PeriodField.of(Integer.MAX_VALUE + 1L, DAYS).getAmountInt();
    }

    //-----------------------------------------------------------------------
    // getUnit()
    //-----------------------------------------------------------------------
    public void test_getUnit() {
        assertEquals(PeriodField.of(0, DAYS).getUnit(), DAYS);
        assertEquals(PeriodField.of(1, DAYS).getUnit(), DAYS);
        assertEquals(PeriodField.of(-1, DAYS).getUnit(), DAYS);
    }

    //-----------------------------------------------------------------------
    // withAmount()
    //-----------------------------------------------------------------------
    public void test_withAmount() {
        assertEquals(PeriodField.of(0, DAYS).withAmount(23), PeriodField.of(23, DAYS));
        assertEquals(PeriodField.of(1, DAYS).withAmount(23), PeriodField.of(23, DAYS));
        assertEquals(PeriodField.of(-1, DAYS).withAmount(23), PeriodField.of(23, DAYS));
    }

    public void test_withAmount_same() {
        PeriodField base = PeriodField.of(1, DAYS);
        assertSame(base.withAmount(1), base);
    }

    //-----------------------------------------------------------------------
    // withRule()
    //-----------------------------------------------------------------------
    public void test_withRule() {
        assertEquals(PeriodField.of(0, DAYS).withRule(MONTHS), PeriodField.of(0, MONTHS));
        assertEquals(PeriodField.of(1, DAYS).withRule(MONTHS), PeriodField.of(1, MONTHS));
        assertEquals(PeriodField.of(-1, DAYS).withRule(MONTHS), PeriodField.of(-1, MONTHS));
    }

    public void test_withRule_same() {
        PeriodField base = PeriodField.of(1, DAYS);
        assertSame(base.withRule(DAYS), base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withAmount_null() {
        PeriodField.of(1, DAYS).withRule(null);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodField)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodField() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        assertEquals(test5.plus(PeriodField.of(0, DAYS)), PeriodField.of(5, DAYS));
        assertEquals(test5.plus(PeriodField.of(2, DAYS)), PeriodField.of(7, DAYS));
        assertEquals(test5.plus(PeriodField.of(-1, DAYS)), PeriodField.of(4, DAYS));
        assertEquals(PeriodField.of(Long.MAX_VALUE - 1, DAYS).plus(PeriodField.of(1, DAYS)), PeriodField.of(Long.MAX_VALUE, DAYS));
        assertEquals(PeriodField.of(Long.MIN_VALUE + 1, DAYS).plus(PeriodField.of(-1, DAYS)), PeriodField.of(Long.MIN_VALUE, DAYS));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_plus_PeriodField_wrongRule() {
        PeriodField.of(1, DAYS).plus(PeriodField.of(-2, MONTHS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_PeriodField_overflowTooBig() {
        PeriodField.of(Long.MAX_VALUE - 1, DAYS).plus(PeriodField.of(2, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_PeriodField_overflowTooSmall() {
        PeriodField.of(Long.MIN_VALUE + 1, DAYS).plus(PeriodField.of(-2, DAYS));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodField_null() {
        PeriodField.of(1, DAYS).plus(null);
    }

    //-----------------------------------------------------------------------
    // plus(long)
    //-----------------------------------------------------------------------
    public void test_plus() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        assertEquals(test5.plus(0), PeriodField.of(5, DAYS));
        assertEquals(test5.plus(2), PeriodField.of(7, DAYS));
        assertEquals(test5.plus(-1), PeriodField.of(4, DAYS));
        assertEquals(PeriodField.of(Long.MAX_VALUE - 1, DAYS).plus(1), PeriodField.of(Long.MAX_VALUE, DAYS));
        assertEquals(PeriodField.of(Long.MIN_VALUE + 1, DAYS).plus(-1), PeriodField.of(Long.MIN_VALUE, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_overflowTooBig() {
        PeriodField.of(Long.MAX_VALUE - 1, DAYS).plus(2);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plus_overflowTooSmall() {
        PeriodField.of(Long.MIN_VALUE + 1, DAYS).plus(-2);
    }

    //-----------------------------------------------------------------------
    // minus(PeriodField)
    //-----------------------------------------------------------------------
    public void test_minus_PeriodField() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        assertEquals(test5.minus(PeriodField.of(0, DAYS)), PeriodField.of(5, DAYS));
        assertEquals(test5.minus(PeriodField.of(2, DAYS)), PeriodField.of(3, DAYS));
        assertEquals(test5.minus(PeriodField.of(-1, DAYS)), PeriodField.of(6, DAYS));
        assertEquals(PeriodField.of(Long.MIN_VALUE + 1, DAYS).minus(PeriodField.of(1, DAYS)), PeriodField.of(Long.MIN_VALUE, DAYS));
        assertEquals(PeriodField.of(Long.MAX_VALUE - 1, DAYS).minus(PeriodField.of(-1, DAYS)), PeriodField.of(Long.MAX_VALUE, DAYS));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_minus_PeriodField_wrongRule() {
        PeriodField.of(1, DAYS).minus(PeriodField.of(-2, MONTHS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_PeriodField_overflowTooBig() {
        PeriodField.of(Long.MIN_VALUE + 1, DAYS).minus(PeriodField.of(2, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_PeriodField_overflowTooSmall() {
        PeriodField.of(Long.MAX_VALUE - 1, DAYS).minus(PeriodField.of(-2, DAYS));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_minus_PeriodField_null() {
        PeriodField.of(1, DAYS).minus(null);
    }

    //-----------------------------------------------------------------------
    // minus(long)
    //-----------------------------------------------------------------------
    public void test_minus() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        assertEquals(test5.minus(0), PeriodField.of(5, DAYS));
        assertEquals(test5.minus(2), PeriodField.of(3, DAYS));
        assertEquals(test5.minus(-1), PeriodField.of(6, DAYS));
        assertEquals(PeriodField.of(Long.MIN_VALUE + 1, DAYS).minus(1), PeriodField.of(Long.MIN_VALUE, DAYS));
        assertEquals(PeriodField.of(Long.MAX_VALUE - 1, DAYS).minus(-1), PeriodField.of(Long.MAX_VALUE, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_overflowTooBig() {
        PeriodField.of(Long.MIN_VALUE + 1, DAYS).minus(2);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minus_overflowTooSmall() {
        PeriodField.of(Long.MAX_VALUE - 1, DAYS).minus(-2);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(long)
    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        assertEquals(test5.multipliedBy(0), PeriodField.of(0, DAYS));
        assertEquals(test5.multipliedBy(1), PeriodField.of(5, DAYS));
        assertEquals(test5.multipliedBy(2), PeriodField.of(10, DAYS));
        assertEquals(test5.multipliedBy(3), PeriodField.of(15, DAYS));
        assertEquals(test5.multipliedBy(-3), PeriodField.of(-15, DAYS));
    }

    @Test(expectedExceptions = {ArithmeticException.class})
    public void test_multipliedBy_overflowTooBig() {
        PeriodField.of(Long.MAX_VALUE / 2 + 1, DAYS).multipliedBy(2);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        PeriodField.of(Long.MIN_VALUE / 2 - 1, DAYS).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    // dividedBy(long)
    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        PeriodField test12 = PeriodField.of(12, DAYS);
        assertEquals(test12.dividedBy(1), PeriodField.of(12, DAYS));
        assertEquals(test12.dividedBy(2), PeriodField.of(6, DAYS));
        assertEquals(test12.dividedBy(3), PeriodField.of(4, DAYS));
        assertEquals(test12.dividedBy(4), PeriodField.of(3, DAYS));
        assertEquals(test12.dividedBy(5), PeriodField.of(2, DAYS));
        assertEquals(test12.dividedBy(6), PeriodField.of(2, DAYS));
        assertEquals(test12.dividedBy(-3), PeriodField.of(-4, DAYS));
    }

    public void test_dividedBy_negate() {
        PeriodField test12 = PeriodField.of(12, DAYS);
        assertEquals(PeriodField.of(-4, DAYS), test12.dividedBy(-3));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        PeriodField.of(1, DAYS).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    public void test_negated() {
        assertEquals(PeriodField.of(0, DAYS).negated(), PeriodField.of(0, DAYS));
        assertEquals(PeriodField.of(12, DAYS).negated(), PeriodField.of(-12, DAYS));
        assertEquals(PeriodField.of(-12, DAYS).negated(), PeriodField.of(12, DAYS));
        assertEquals(PeriodField.of(Long.MAX_VALUE, DAYS).negated(), PeriodField.of(-Long.MAX_VALUE, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_negated_overflow() {
        PeriodField.of(Long.MIN_VALUE, DAYS).negated();
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    public void test_abs() {
        assertEquals(PeriodField.of(0, DAYS).abs(), PeriodField.of(0, DAYS));
        assertEquals(PeriodField.of(12, DAYS).abs(), PeriodField.of(12, DAYS));
        assertEquals(PeriodField.of(-12, DAYS).abs(), PeriodField.of(12, DAYS));
        assertEquals(PeriodField.of(Long.MAX_VALUE, DAYS).abs(), PeriodField.of(Long.MAX_VALUE, DAYS));
    }

    public void test_abs_same() {
        PeriodField base = PeriodField.of(12, DAYS);
        assertSame(base.abs(), base);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_abs_overflow() {
        PeriodField.of(Long.MIN_VALUE, DAYS).abs();
    }

    //-----------------------------------------------------------------------
    // toEstimatedDuration()
    //-----------------------------------------------------------------------
    public void test_toEstimatedDuration() {
        Duration test = PeriodField.of(5, DAYS).toEstimatedDuration();
        Duration fiveDays = ISOChronology.periodDays().getEstimatedDuration().multipliedBy(5);
        assertEquals(test, fiveDays);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        PeriodField test6 = PeriodField.of(6, DAYS);
        assertEquals(0, test5.compareTo(test5));
        assertEquals(-1, test5.compareTo(test6));
        assertEquals(1, test6.compareTo(test5));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_compareTo_null() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    public void test_equals() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        PeriodField test6 = PeriodField.of(6, DAYS);
        assertEquals(true, test5.equals(test5));
        assertEquals(false, test5.equals(test6));
        assertEquals(false, test6.equals(test5));
    }

    public void test_equals_null() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        assertEquals(false, test5.equals(null));
    }

    public void test_equals_otherClass() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        assertEquals(false, test5.equals(""));
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        PeriodField test6 = PeriodField.of(6, DAYS);
        assertEquals(true, test5.hashCode() == test5.hashCode());
        assertEquals(false, test5.hashCode() == test6.hashCode());
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        assertEquals("5 Days", test5.toString());
        PeriodField testM1 = PeriodField.of(-1, MONTHS);
        assertEquals("-1 Months", testM1.toString());
    }

}
