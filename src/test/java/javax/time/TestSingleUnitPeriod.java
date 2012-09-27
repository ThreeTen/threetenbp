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
package javax.time;

import static javax.time.calendrical.LocalPeriodUnit.MILLIS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;

import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestSingleUnitPeriod {

    private static final PeriodUnit FOREVER = LocalPeriodUnit.FOREVER;
    private static final PeriodUnit MONTHS = LocalPeriodUnit.MONTHS;
    private static final PeriodUnit DAYS = LocalPeriodUnit.DAYS;
    private static final PeriodUnit HOURS = LocalPeriodUnit.HOURS;
    private static final PeriodUnit MINUTES = LocalPeriodUnit.MINUTES;
    private static final PeriodUnit SECONDS = LocalPeriodUnit.SECONDS;

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(Comparable.class.isAssignableFrom(SingleUnitPeriod.class));
        assertTrue(Serializable.class.isAssignableFrom(SingleUnitPeriod.class));
    }

    //-----------------------------------------------------------------------
    // constants
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_constant_zero_days() {
        assertEquals(SingleUnitPeriod.ZERO_DAYS.getAmount(), 0);
        assertEquals(SingleUnitPeriod.ZERO_DAYS.getUnit(), DAYS);
    }

    @Test(groups={"tck"})
    public void test_constant_zero_seconds() {
        assertEquals(SingleUnitPeriod.ZERO_SECONDS.getAmount(), 0);
        assertEquals(SingleUnitPeriod.ZERO_SECONDS.getUnit(), SECONDS);
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_of() {
        assertEquals(SingleUnitPeriod.of(1, DAYS).getAmount(), 1);
        assertEquals(SingleUnitPeriod.of(2, DAYS).getAmount(), 2);
        assertEquals(SingleUnitPeriod.of(Long.MAX_VALUE, DAYS).getAmount(), Long.MAX_VALUE);
        assertEquals(SingleUnitPeriod.of(-1, DAYS).getAmount(), -1);
        assertEquals(SingleUnitPeriod.of(-2, DAYS).getAmount(), -2);
        assertEquals(SingleUnitPeriod.of(Long.MIN_VALUE, DAYS).getAmount(), Long.MIN_VALUE);
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_factory_of_Forever() {
        SingleUnitPeriod.of(1, FOREVER);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_of_null() {
        SingleUnitPeriod.of(1, null);
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_serialization() throws Exception {
        SingleUnitPeriod orginal = SingleUnitPeriod.of(3, DAYS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        SingleUnitPeriod ser = (SingleUnitPeriod) in.readObject();
        assertEquals(SingleUnitPeriod.of(3, DAYS), ser);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isZero() {
        assertEquals(SingleUnitPeriod.of(0, DAYS).isZero(), true);
        assertEquals(SingleUnitPeriod.of(1, DAYS).isZero(), false);
        assertEquals(SingleUnitPeriod.of(-1, DAYS).isZero(), false);
    }

    //-----------------------------------------------------------------------
    // getAmount()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getAmount() {
        assertEquals(SingleUnitPeriod.of(0, DAYS).getAmount(), 0L);
        assertEquals(SingleUnitPeriod.of(1, DAYS).getAmount(), 1L);
        assertEquals(SingleUnitPeriod.of(-1, DAYS).getAmount(), -1L);
        assertEquals(SingleUnitPeriod.of(Long.MAX_VALUE, DAYS).getAmount(), Long.MAX_VALUE);
        assertEquals(SingleUnitPeriod.of(Long.MIN_VALUE, DAYS).getAmount(), Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // getAmountInt()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getAmountInt() {
        assertEquals(SingleUnitPeriod.of(0, DAYS).getAmountInt(), 0);
        assertEquals(SingleUnitPeriod.of(1, DAYS).getAmountInt(), 1);
        assertEquals(SingleUnitPeriod.of(-1, DAYS).getAmountInt(), -1);
        assertEquals(SingleUnitPeriod.of(Integer.MAX_VALUE, DAYS).getAmountInt(), Integer.MAX_VALUE);
        assertEquals(SingleUnitPeriod.of(Integer.MIN_VALUE, DAYS).getAmountInt(), Integer.MIN_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_getAmountInt_tooBig() {
        SingleUnitPeriod.of(Integer.MAX_VALUE + 1L, DAYS).getAmountInt();
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_getAmountInt_tooSmall() {
        SingleUnitPeriod.of(Integer.MIN_VALUE - 1L, DAYS).getAmountInt();
    }

    //-----------------------------------------------------------------------
    // getUnit()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getUnit() {
        assertEquals(SingleUnitPeriod.of(0, DAYS).getUnit(), DAYS);
        assertEquals(SingleUnitPeriod.of(1, DAYS).getUnit(), DAYS);
        assertEquals(SingleUnitPeriod.of(-1, DAYS).getUnit(), DAYS);
    }

    //-----------------------------------------------------------------------
    // withAmount()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withAmount() {
        assertEquals(SingleUnitPeriod.of(0, DAYS).withAmount(23), SingleUnitPeriod.of(23, DAYS));
        assertEquals(SingleUnitPeriod.of(1, DAYS).withAmount(23), SingleUnitPeriod.of(23, DAYS));
        assertEquals(SingleUnitPeriod.of(-1, DAYS).withAmount(23), SingleUnitPeriod.of(23, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_withAmount_same() {
        SingleUnitPeriod base = SingleUnitPeriod.of(1, DAYS);
        assertSame(base.withAmount(1), base);
    }
    
    @Test(groups={"tck"})
    public void test_withAmount_equal() {
        SingleUnitPeriod base = SingleUnitPeriod.of(1, DAYS);
        assertEquals(base.withAmount(1), base);
    }

    //-----------------------------------------------------------------------
    // withUnit()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withUnit() {
        assertEquals(SingleUnitPeriod.of(0, DAYS).withUnit(MONTHS), SingleUnitPeriod.of(0, MONTHS));
        assertEquals(SingleUnitPeriod.of(1, DAYS).withUnit(MONTHS), SingleUnitPeriod.of(1, MONTHS));
        assertEquals(SingleUnitPeriod.of(-1, DAYS).withUnit(MONTHS), SingleUnitPeriod.of(-1, MONTHS));
    }

    @Test(groups={"implementation"})
    public void test_withUnit_same() {
        SingleUnitPeriod base = SingleUnitPeriod.of(1, DAYS);
        assertSame(base.withUnit(DAYS), base);
    }
    
    @Test(groups={"tck"})
    public void test_withUnit_equal() {
        SingleUnitPeriod base = SingleUnitPeriod.of(1, DAYS);
        assertEquals(base.withUnit(DAYS), base);
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_withUnit_forever() {
        SingleUnitPeriod.of(1, DAYS).withUnit(FOREVER);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withUnit_null() {
        SingleUnitPeriod.of(1, DAYS).withUnit(null);
    }

    //-----------------------------------------------------------------------
    // plus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Period() {
        SingleUnitPeriod test5 = SingleUnitPeriod.of(5, DAYS);
        assertEquals(test5.plus(SingleUnitPeriod.of(0, DAYS)), SingleUnitPeriod.of(5, DAYS));
        assertEquals(test5.plus(SingleUnitPeriod.of(2, DAYS)), SingleUnitPeriod.of(7, DAYS));
        assertEquals(test5.plus(SingleUnitPeriod.of(-1, DAYS)), SingleUnitPeriod.of(4, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MAX_VALUE - 1, DAYS).plus(SingleUnitPeriod.of(1, DAYS)), SingleUnitPeriod.of(Long.MAX_VALUE, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MIN_VALUE + 1, DAYS).plus(SingleUnitPeriod.of(-1, DAYS)), SingleUnitPeriod.of(Long.MIN_VALUE, DAYS));
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_plus_Period_wrongRule() {
        SingleUnitPeriod.of(1, DAYS).plus(SingleUnitPeriod.of(-2, MONTHS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plus_Period_overflowTooBig() {
        SingleUnitPeriod.of(Long.MAX_VALUE - 1, DAYS).plus(SingleUnitPeriod.of(2, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plus_Period_overflowTooSmall() {
        SingleUnitPeriod.of(Long.MIN_VALUE + 1, DAYS).plus(SingleUnitPeriod.of(-2, DAYS));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_Period_null() {
        SingleUnitPeriod.of(1, DAYS).plus(null);
    }

    //-----------------------------------------------------------------------
    // plus(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus() {
        SingleUnitPeriod test5 = SingleUnitPeriod.of(5, DAYS);
        assertEquals(test5.plus(0), SingleUnitPeriod.of(5, DAYS));
        assertEquals(test5.plus(2), SingleUnitPeriod.of(7, DAYS));
        assertEquals(test5.plus(-1), SingleUnitPeriod.of(4, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MAX_VALUE - 1, DAYS).plus(1), SingleUnitPeriod.of(Long.MAX_VALUE, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MIN_VALUE + 1, DAYS).plus(-1), SingleUnitPeriod.of(Long.MIN_VALUE, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plus_overflowTooBig() {
        SingleUnitPeriod.of(Long.MAX_VALUE - 1, DAYS).plus(2);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plus_overflowTooSmall() {
        SingleUnitPeriod.of(Long.MIN_VALUE + 1, DAYS).plus(-2);
    }

    //-----------------------------------------------------------------------
    // minus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Period() {
        SingleUnitPeriod test5 = SingleUnitPeriod.of(5, DAYS);
        assertEquals(test5.minus(SingleUnitPeriod.of(0, DAYS)), SingleUnitPeriod.of(5, DAYS));
        assertEquals(test5.minus(SingleUnitPeriod.of(2, DAYS)), SingleUnitPeriod.of(3, DAYS));
        assertEquals(test5.minus(SingleUnitPeriod.of(-1, DAYS)), SingleUnitPeriod.of(6, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MIN_VALUE + 1, DAYS).minus(SingleUnitPeriod.of(1, DAYS)), SingleUnitPeriod.of(Long.MIN_VALUE, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MAX_VALUE - 1, DAYS).minus(SingleUnitPeriod.of(-1, DAYS)), SingleUnitPeriod.of(Long.MAX_VALUE, DAYS));
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_minus_Period_wrongRule() {
        SingleUnitPeriod.of(1, DAYS).minus(SingleUnitPeriod.of(-2, MONTHS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minus_Period_overflowTooBig() {
        SingleUnitPeriod.of(Long.MIN_VALUE + 1, DAYS).minus(SingleUnitPeriod.of(2, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minus_Period_overflowTooSmall() {
        SingleUnitPeriod.of(Long.MAX_VALUE - 1, DAYS).minus(SingleUnitPeriod.of(-2, DAYS));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_Period_null() {
        SingleUnitPeriod.of(1, DAYS).minus(null);
    }

    //-----------------------------------------------------------------------
    // minus(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus() {
        SingleUnitPeriod test5 = SingleUnitPeriod.of(5, DAYS);
        assertEquals(test5.minus(0), SingleUnitPeriod.of(5, DAYS));
        assertEquals(test5.minus(2), SingleUnitPeriod.of(3, DAYS));
        assertEquals(test5.minus(-1), SingleUnitPeriod.of(6, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MIN_VALUE + 1, DAYS).minus(1), SingleUnitPeriod.of(Long.MIN_VALUE, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MAX_VALUE - 1, DAYS).minus(-1), SingleUnitPeriod.of(Long.MAX_VALUE, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minus_overflowTooBig() {
        SingleUnitPeriod.of(Long.MIN_VALUE + 1, DAYS).minus(2);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minus_overflowTooSmall() {
        SingleUnitPeriod.of(Long.MAX_VALUE - 1, DAYS).minus(-2);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_multipliedBy() {
        SingleUnitPeriod test5 = SingleUnitPeriod.of(5, DAYS);
        assertEquals(test5.multipliedBy(0), SingleUnitPeriod.of(0, DAYS));
        assertEquals(test5.multipliedBy(1), SingleUnitPeriod.of(5, DAYS));
        assertEquals(test5.multipliedBy(2), SingleUnitPeriod.of(10, DAYS));
        assertEquals(test5.multipliedBy(3), SingleUnitPeriod.of(15, DAYS));
        assertEquals(test5.multipliedBy(-3), SingleUnitPeriod.of(-15, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_multipliedBy_same() {
        SingleUnitPeriod base = SingleUnitPeriod.of(12, DAYS);
        assertSame(base.multipliedBy(1), base);
    }
    
    @Test(groups={"tck"})
    public void test_multipliedBy_equal() {
        SingleUnitPeriod base = SingleUnitPeriod.of(12, DAYS);
        assertEquals(base.multipliedBy(1), base);
    }

    @Test(expectedExceptions = {ArithmeticException.class}, groups={"tck"})
    public void test_multipliedBy_overflowTooBig() {
        SingleUnitPeriod.of(Long.MAX_VALUE / 2 + 1, DAYS).multipliedBy(2);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_multipliedBy_overflowTooSmall() {
        SingleUnitPeriod.of(Long.MIN_VALUE / 2 - 1, DAYS).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    // dividedBy(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_dividedBy() {
        SingleUnitPeriod test12 = SingleUnitPeriod.of(12, DAYS);
        assertEquals(test12.dividedBy(1), SingleUnitPeriod.of(12, DAYS));
        assertEquals(test12.dividedBy(2), SingleUnitPeriod.of(6, DAYS));
        assertEquals(test12.dividedBy(3), SingleUnitPeriod.of(4, DAYS));
        assertEquals(test12.dividedBy(4), SingleUnitPeriod.of(3, DAYS));
        assertEquals(test12.dividedBy(5), SingleUnitPeriod.of(2, DAYS));
        assertEquals(test12.dividedBy(6), SingleUnitPeriod.of(2, DAYS));
        assertEquals(test12.dividedBy(-3), SingleUnitPeriod.of(-4, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_dividedBy_same() {
        SingleUnitPeriod base = SingleUnitPeriod.of(12, DAYS);
        assertSame(base.dividedBy(1), base);
    }
    
    @Test(groups={"tck"})
    public void test_dividedBy_equal() {
        SingleUnitPeriod base = SingleUnitPeriod.of(12, DAYS);
        assertEquals(base.dividedBy(1), base);
    }

    @Test(groups={"tck"})
    public void test_dividedBy_negate() {
        SingleUnitPeriod test12 = SingleUnitPeriod.of(12, DAYS);
        assertEquals(SingleUnitPeriod.of(-4, DAYS), test12.dividedBy(-3));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_dividedBy_divideByZero() {
        SingleUnitPeriod.of(1, DAYS).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    // remainder(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_remainder() {
        SingleUnitPeriod test12 = SingleUnitPeriod.of(13, DAYS);
        assertEquals(test12.remainder(1), SingleUnitPeriod.of(0, DAYS));
        assertEquals(test12.remainder(2), SingleUnitPeriod.of(1, DAYS));
        assertEquals(test12.remainder(3), SingleUnitPeriod.of(1, DAYS));
        assertEquals(test12.remainder(4), SingleUnitPeriod.of(1, DAYS));
        assertEquals(test12.remainder(5), SingleUnitPeriod.of(3, DAYS));
        assertEquals(test12.remainder(6), SingleUnitPeriod.of(1, DAYS));
        assertEquals(test12.remainder(-3), SingleUnitPeriod.of(1, DAYS));
    }

    @Test(groups={"tck"})
    public void test_remainder_negate() {
        SingleUnitPeriod test12 = SingleUnitPeriod.of(-14, DAYS);
        assertEquals(test12.remainder(-5), SingleUnitPeriod.of(-4, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_remainder_same() {
        SingleUnitPeriod base = SingleUnitPeriod.of(12, DAYS);
        assertSame(base.remainder(15), base);
    }
    
    @Test(groups={"tck"})
    public void test_remainder_equal() {
        SingleUnitPeriod base = SingleUnitPeriod.of(12, DAYS);
        assertEquals(base.remainder(15), base);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_remainder_divideByZero() {
        SingleUnitPeriod.of(1, DAYS).remainder(0);
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_negated() {
        assertEquals(SingleUnitPeriod.of(0, DAYS).negated(), SingleUnitPeriod.of(0, DAYS));
        assertEquals(SingleUnitPeriod.of(12, DAYS).negated(), SingleUnitPeriod.of(-12, DAYS));
        assertEquals(SingleUnitPeriod.of(-12, DAYS).negated(), SingleUnitPeriod.of(12, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MAX_VALUE, DAYS).negated(), SingleUnitPeriod.of(-Long.MAX_VALUE, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_negated_overflow() {
        SingleUnitPeriod.of(Long.MIN_VALUE, DAYS).negated();
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_abs() {
        assertEquals(SingleUnitPeriod.of(0, DAYS).abs(), SingleUnitPeriod.of(0, DAYS));
        assertEquals(SingleUnitPeriod.of(12, DAYS).abs(), SingleUnitPeriod.of(12, DAYS));
        assertEquals(SingleUnitPeriod.of(-12, DAYS).abs(), SingleUnitPeriod.of(12, DAYS));
        assertEquals(SingleUnitPeriod.of(Long.MAX_VALUE, DAYS).abs(), SingleUnitPeriod.of(Long.MAX_VALUE, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_abs_same() {
        SingleUnitPeriod base = SingleUnitPeriod.of(12, DAYS);
        assertSame(base.abs(), base);
    }
    
    @Test(groups={"tck"})
    public void test_abs_equal() {
        SingleUnitPeriod base = SingleUnitPeriod.of(12, DAYS);
        assertEquals(base.abs(), base);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_abs_overflow() {
        SingleUnitPeriod.of(Long.MIN_VALUE, DAYS).abs();
    }

    //-----------------------------------------------------------------------
    // toDuration()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toDuration_hours() {
        Duration test = SingleUnitPeriod.of(5, HOURS).toDuration();
        Duration fiveHours = Duration.ofHours(5);
        assertEquals(test, fiveHours);
    }

    @Test(groups={"tck"})
    public void test_toDuration_millis() {
        Duration test = SingleUnitPeriod.of(5, MILLIS).toDuration();
        Duration fiveMillis = Duration.ofMillis(5);
        assertEquals(test, fiveMillis);
    }

    @Test(groups={"tck"})
    public void test_toDuration_days() {
        Duration test = SingleUnitPeriod.of(5, DAYS).toDuration();
        Duration fiveDays = Duration.ofDays(5);
        assertEquals(test, fiveDays);
    }

    @Test(groups={"tck"})
    public void test_toDuration_months() {
        Duration test = SingleUnitPeriod.of(5, MONTHS).toDuration();
        Duration fiveMonths = MONTHS.getDuration().multipliedBy(5);
        assertEquals(test, fiveMonths);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_toDuration_tooBig() {
        SingleUnitPeriod.of(Long.MAX_VALUE, MINUTES).toDuration();
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_compareTo() {
        SingleUnitPeriod a = SingleUnitPeriod.of(5, DAYS);
        SingleUnitPeriod b = SingleUnitPeriod.of(6, DAYS);
        assertEquals(a.compareTo(a), 0);
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
    }

    @Test(expectedExceptions=IllegalArgumentException.class,groups={"tck"})
    public void test_compareTo_differentUnits() {
        SingleUnitPeriod a = SingleUnitPeriod.of(6 * 60, MINUTES);
        SingleUnitPeriod b = SingleUnitPeriod.of(5, HOURS);
        a.compareTo(b);
    }

    @Test(expectedExceptions = {NullPointerException.class}, groups={"tck"})
    public void test_compareTo_null() {
        SingleUnitPeriod test5 = SingleUnitPeriod.of(5, DAYS);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_equals() {
        SingleUnitPeriod a = SingleUnitPeriod.of(5, DAYS);
        SingleUnitPeriod b = SingleUnitPeriod.of(6, DAYS);
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    @Test(groups={"tck"})
    public void test_equals_null() {
        SingleUnitPeriod test = SingleUnitPeriod.of(5, DAYS);
        assertEquals(test.equals(null), false);
    }

    @Test(groups={"tck"})
    public void test_equals_otherClass() {
        SingleUnitPeriod test = SingleUnitPeriod.of(5, DAYS);
        assertEquals(test.equals(""), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_hashCode() {
        SingleUnitPeriod a = SingleUnitPeriod.of(5, DAYS);
        SingleUnitPeriod b = SingleUnitPeriod.of(6, DAYS);
        SingleUnitPeriod c = SingleUnitPeriod.of(5, HOURS);
        assertEquals(a.hashCode() == a.hashCode(), true);
        assertEquals(a.hashCode() == b.hashCode(), false);
        assertEquals(a.hashCode() == c.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        SingleUnitPeriod test5 = SingleUnitPeriod.of(5, DAYS);
        assertEquals(test5.toString(), "5 Days");
        SingleUnitPeriod testM1 = SingleUnitPeriod.of(-1, MONTHS);
        assertEquals(testM1.toString(), "-1 Months");
    }

}
