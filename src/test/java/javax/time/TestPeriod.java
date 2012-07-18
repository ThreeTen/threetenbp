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
public class TestPeriod {

    private static final PeriodUnit FOREVER = LocalPeriodUnit.FOREVER;
    private static final PeriodUnit MONTHS = LocalPeriodUnit.MONTHS;
    private static final PeriodUnit DAYS = LocalPeriodUnit.DAYS;
    private static final PeriodUnit HOURS = LocalPeriodUnit.HOURS;
    private static final PeriodUnit MINUTES = LocalPeriodUnit.MINUTES;
    private static final PeriodUnit SECONDS = LocalPeriodUnit.SECONDS;

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(Comparable.class.isAssignableFrom(Period.class));
        assertTrue(Serializable.class.isAssignableFrom(Period.class));
    }

    //-----------------------------------------------------------------------
    // constants
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_constant_zero_days() {
        assertEquals(Period.ZERO_DAYS.getAmount(), 0);
        assertEquals(Period.ZERO_DAYS.getUnit(), DAYS);
    }

    @Test(groups={"tck"})
    public void test_constant_zero_seconds() {
        assertEquals(Period.ZERO_SECONDS.getAmount(), 0);
        assertEquals(Period.ZERO_SECONDS.getUnit(), SECONDS);
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_of() {
        assertEquals(Period.of(1, DAYS).getAmount(), 1);
        assertEquals(Period.of(2, DAYS).getAmount(), 2);
        assertEquals(Period.of(Long.MAX_VALUE, DAYS).getAmount(), Long.MAX_VALUE);
        assertEquals(Period.of(-1, DAYS).getAmount(), -1);
        assertEquals(Period.of(-2, DAYS).getAmount(), -2);
        assertEquals(Period.of(Long.MIN_VALUE, DAYS).getAmount(), Long.MIN_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_of_Forever() {
        Period.of(1, FOREVER);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_of_null() {
        Period.of(1, null);
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_serialization() throws Exception {
        Period orginal = Period.of(3, DAYS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        Period ser = (Period) in.readObject();
        assertEquals(Period.of(3, DAYS), ser);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isZero() {
        assertEquals(Period.of(0, DAYS).isZero(), true);
        assertEquals(Period.of(1, DAYS).isZero(), false);
        assertEquals(Period.of(-1, DAYS).isZero(), false);
    }

    //-----------------------------------------------------------------------
    // getAmount()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getAmount() {
        assertEquals(Period.of(0, DAYS).getAmount(), 0L);
        assertEquals(Period.of(1, DAYS).getAmount(), 1L);
        assertEquals(Period.of(-1, DAYS).getAmount(), -1L);
        assertEquals(Period.of(Long.MAX_VALUE, DAYS).getAmount(), Long.MAX_VALUE);
        assertEquals(Period.of(Long.MIN_VALUE, DAYS).getAmount(), Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // getAmountInt()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getAmountInt() {
        assertEquals(Period.of(0, DAYS).getAmountInt(), 0);
        assertEquals(Period.of(1, DAYS).getAmountInt(), 1);
        assertEquals(Period.of(-1, DAYS).getAmountInt(), -1);
        assertEquals(Period.of(Integer.MAX_VALUE, DAYS).getAmountInt(), Integer.MAX_VALUE);
        assertEquals(Period.of(Integer.MIN_VALUE, DAYS).getAmountInt(), Integer.MIN_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_getAmountInt_tooBig() {
        Period.of(Integer.MAX_VALUE + 1L, DAYS).getAmountInt();
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_getAmountInt_tooSmall() {
        Period.of(Integer.MIN_VALUE - 1L, DAYS).getAmountInt();
    }

    //-----------------------------------------------------------------------
    // getUnit()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getUnit() {
        assertEquals(Period.of(0, DAYS).getUnit(), DAYS);
        assertEquals(Period.of(1, DAYS).getUnit(), DAYS);
        assertEquals(Period.of(-1, DAYS).getUnit(), DAYS);
    }

    //-----------------------------------------------------------------------
    // withAmount()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withAmount() {
        assertEquals(Period.of(0, DAYS).withAmount(23), Period.of(23, DAYS));
        assertEquals(Period.of(1, DAYS).withAmount(23), Period.of(23, DAYS));
        assertEquals(Period.of(-1, DAYS).withAmount(23), Period.of(23, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_withAmount_same() {
        Period base = Period.of(1, DAYS);
        assertSame(base.withAmount(1), base);
    }
    
    @Test(groups={"tck"})
    public void test_withAmount_equal() {
        Period base = Period.of(1, DAYS);
        assertEquals(base.withAmount(1), base);
    }

    //-----------------------------------------------------------------------
    // withUnit()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withUnit() {
        assertEquals(Period.of(0, DAYS).withUnit(MONTHS), Period.of(0, MONTHS));
        assertEquals(Period.of(1, DAYS).withUnit(MONTHS), Period.of(1, MONTHS));
        assertEquals(Period.of(-1, DAYS).withUnit(MONTHS), Period.of(-1, MONTHS));
    }

    @Test(groups={"implementation"})
    public void test_withUnit_same() {
        Period base = Period.of(1, DAYS);
        assertSame(base.withUnit(DAYS), base);
    }
    
    @Test(groups={"tck"})
    public void test_withUnit_equal() {
        Period base = Period.of(1, DAYS);
        assertEquals(base.withUnit(DAYS), base);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withUnit_forever() {
        Period.of(1, DAYS).withUnit(FOREVER);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withUnit_null() {
        Period.of(1, DAYS).withUnit(null);
    }

    //-----------------------------------------------------------------------
    // plus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Period() {
        Period test5 = Period.of(5, DAYS);
        assertEquals(test5.plus(Period.of(0, DAYS)), Period.of(5, DAYS));
        assertEquals(test5.plus(Period.of(2, DAYS)), Period.of(7, DAYS));
        assertEquals(test5.plus(Period.of(-1, DAYS)), Period.of(4, DAYS));
        assertEquals(Period.of(Long.MAX_VALUE - 1, DAYS).plus(Period.of(1, DAYS)), Period.of(Long.MAX_VALUE, DAYS));
        assertEquals(Period.of(Long.MIN_VALUE + 1, DAYS).plus(Period.of(-1, DAYS)), Period.of(Long.MIN_VALUE, DAYS));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_Period_wrongRule() {
        Period.of(1, DAYS).plus(Period.of(-2, MONTHS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plus_Period_overflowTooBig() {
        Period.of(Long.MAX_VALUE - 1, DAYS).plus(Period.of(2, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plus_Period_overflowTooSmall() {
        Period.of(Long.MIN_VALUE + 1, DAYS).plus(Period.of(-2, DAYS));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_Period_null() {
        Period.of(1, DAYS).plus(null);
    }

    //-----------------------------------------------------------------------
    // plus(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus() {
        Period test5 = Period.of(5, DAYS);
        assertEquals(test5.plus(0), Period.of(5, DAYS));
        assertEquals(test5.plus(2), Period.of(7, DAYS));
        assertEquals(test5.plus(-1), Period.of(4, DAYS));
        assertEquals(Period.of(Long.MAX_VALUE - 1, DAYS).plus(1), Period.of(Long.MAX_VALUE, DAYS));
        assertEquals(Period.of(Long.MIN_VALUE + 1, DAYS).plus(-1), Period.of(Long.MIN_VALUE, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plus_overflowTooBig() {
        Period.of(Long.MAX_VALUE - 1, DAYS).plus(2);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plus_overflowTooSmall() {
        Period.of(Long.MIN_VALUE + 1, DAYS).plus(-2);
    }

    //-----------------------------------------------------------------------
    // minus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Period() {
        Period test5 = Period.of(5, DAYS);
        assertEquals(test5.minus(Period.of(0, DAYS)), Period.of(5, DAYS));
        assertEquals(test5.minus(Period.of(2, DAYS)), Period.of(3, DAYS));
        assertEquals(test5.minus(Period.of(-1, DAYS)), Period.of(6, DAYS));
        assertEquals(Period.of(Long.MIN_VALUE + 1, DAYS).minus(Period.of(1, DAYS)), Period.of(Long.MIN_VALUE, DAYS));
        assertEquals(Period.of(Long.MAX_VALUE - 1, DAYS).minus(Period.of(-1, DAYS)), Period.of(Long.MAX_VALUE, DAYS));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_Period_wrongRule() {
        Period.of(1, DAYS).minus(Period.of(-2, MONTHS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minus_Period_overflowTooBig() {
        Period.of(Long.MIN_VALUE + 1, DAYS).minus(Period.of(2, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minus_Period_overflowTooSmall() {
        Period.of(Long.MAX_VALUE - 1, DAYS).minus(Period.of(-2, DAYS));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_Period_null() {
        Period.of(1, DAYS).minus(null);
    }

    //-----------------------------------------------------------------------
    // minus(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus() {
        Period test5 = Period.of(5, DAYS);
        assertEquals(test5.minus(0), Period.of(5, DAYS));
        assertEquals(test5.minus(2), Period.of(3, DAYS));
        assertEquals(test5.minus(-1), Period.of(6, DAYS));
        assertEquals(Period.of(Long.MIN_VALUE + 1, DAYS).minus(1), Period.of(Long.MIN_VALUE, DAYS));
        assertEquals(Period.of(Long.MAX_VALUE - 1, DAYS).minus(-1), Period.of(Long.MAX_VALUE, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minus_overflowTooBig() {
        Period.of(Long.MIN_VALUE + 1, DAYS).minus(2);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minus_overflowTooSmall() {
        Period.of(Long.MAX_VALUE - 1, DAYS).minus(-2);
    }

    //-----------------------------------------------------------------------
    // multipliedBy(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_multipliedBy() {
        Period test5 = Period.of(5, DAYS);
        assertEquals(test5.multipliedBy(0), Period.of(0, DAYS));
        assertEquals(test5.multipliedBy(1), Period.of(5, DAYS));
        assertEquals(test5.multipliedBy(2), Period.of(10, DAYS));
        assertEquals(test5.multipliedBy(3), Period.of(15, DAYS));
        assertEquals(test5.multipliedBy(-3), Period.of(-15, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_multipliedBy_same() {
        Period base = Period.of(12, DAYS);
        assertSame(base.multipliedBy(1), base);
    }
    
    @Test(groups={"tck"})
    public void test_multipliedBy_equal() {
        Period base = Period.of(12, DAYS);
        assertEquals(base.multipliedBy(1), base);
    }

    @Test(expectedExceptions = {ArithmeticException.class}, groups={"tck"})
    public void test_multipliedBy_overflowTooBig() {
        Period.of(Long.MAX_VALUE / 2 + 1, DAYS).multipliedBy(2);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_multipliedBy_overflowTooSmall() {
        Period.of(Long.MIN_VALUE / 2 - 1, DAYS).multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    // dividedBy(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_dividedBy() {
        Period test12 = Period.of(12, DAYS);
        assertEquals(test12.dividedBy(1), Period.of(12, DAYS));
        assertEquals(test12.dividedBy(2), Period.of(6, DAYS));
        assertEquals(test12.dividedBy(3), Period.of(4, DAYS));
        assertEquals(test12.dividedBy(4), Period.of(3, DAYS));
        assertEquals(test12.dividedBy(5), Period.of(2, DAYS));
        assertEquals(test12.dividedBy(6), Period.of(2, DAYS));
        assertEquals(test12.dividedBy(-3), Period.of(-4, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_dividedBy_same() {
        Period base = Period.of(12, DAYS);
        assertSame(base.dividedBy(1), base);
    }
    
    @Test(groups={"tck"})
    public void test_dividedBy_equal() {
        Period base = Period.of(12, DAYS);
        assertEquals(base.dividedBy(1), base);
    }

    @Test(groups={"tck"})
    public void test_dividedBy_negate() {
        Period test12 = Period.of(12, DAYS);
        assertEquals(Period.of(-4, DAYS), test12.dividedBy(-3));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_dividedBy_divideByZero() {
        Period.of(1, DAYS).dividedBy(0);
    }

    //-----------------------------------------------------------------------
    // remainder(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_remainder() {
        Period test12 = Period.of(13, DAYS);
        assertEquals(test12.remainder(1), Period.of(0, DAYS));
        assertEquals(test12.remainder(2), Period.of(1, DAYS));
        assertEquals(test12.remainder(3), Period.of(1, DAYS));
        assertEquals(test12.remainder(4), Period.of(1, DAYS));
        assertEquals(test12.remainder(5), Period.of(3, DAYS));
        assertEquals(test12.remainder(6), Period.of(1, DAYS));
        assertEquals(test12.remainder(-3), Period.of(1, DAYS));
    }

    @Test(groups={"tck"})
    public void test_remainder_negate() {
        Period test12 = Period.of(-14, DAYS);
        assertEquals(test12.remainder(-5), Period.of(-4, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_remainder_same() {
        Period base = Period.of(12, DAYS);
        assertSame(base.remainder(15), base);
    }
    
    @Test(groups={"tck"})
    public void test_remainder_equal() {
        Period base = Period.of(12, DAYS);
        assertEquals(base.remainder(15), base);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_remainder_divideByZero() {
        Period.of(1, DAYS).remainder(0);
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_negated() {
        assertEquals(Period.of(0, DAYS).negated(), Period.of(0, DAYS));
        assertEquals(Period.of(12, DAYS).negated(), Period.of(-12, DAYS));
        assertEquals(Period.of(-12, DAYS).negated(), Period.of(12, DAYS));
        assertEquals(Period.of(Long.MAX_VALUE, DAYS).negated(), Period.of(-Long.MAX_VALUE, DAYS));
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_negated_overflow() {
        Period.of(Long.MIN_VALUE, DAYS).negated();
    }

    //-----------------------------------------------------------------------
    // abs()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_abs() {
        assertEquals(Period.of(0, DAYS).abs(), Period.of(0, DAYS));
        assertEquals(Period.of(12, DAYS).abs(), Period.of(12, DAYS));
        assertEquals(Period.of(-12, DAYS).abs(), Period.of(12, DAYS));
        assertEquals(Period.of(Long.MAX_VALUE, DAYS).abs(), Period.of(Long.MAX_VALUE, DAYS));
    }

    @Test(groups={"implementation"})
    public void test_abs_same() {
        Period base = Period.of(12, DAYS);
        assertSame(base.abs(), base);
    }
    
    @Test(groups={"tck"})
    public void test_abs_equal() {
        Period base = Period.of(12, DAYS);
        assertEquals(base.abs(), base);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_abs_overflow() {
        Period.of(Long.MIN_VALUE, DAYS).abs();
    }

//    //-----------------------------------------------------------------------
//    // toEquivalent(PeriodUnit)
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_toEquivalent_unit() {
//        Period test = Period.of(5, YEARS).toEquivalent(QUARTERS);
//        assertEquals(test, Period.of(5 * 4, QUARTERS));
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
//    public void test_toEquivalent_unit_tooBig() {
//        Period.of(Long.MAX_VALUE / 12 + 12, YEARS).toEquivalent(MONTHS);
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_toEquivalent_unit_noConversionAllowed() {
//        try {
//            Period.of(4, QUARTERS).toEquivalent(YEARS);
//        } catch (CalendricalException ex) {
//            assertEquals("Unable to convert Quarters to Years", ex.getMessage());
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_toEquivalent_unit_noConversion() {
//        try {
//            Period.of(5, YEARS).toEquivalent(DAYS);
//        } catch (CalendricalException ex) {
//            assertEquals("Unable to convert Years to Days", ex.getMessage());
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_toEquivalent_null() {
//        Period.of(5, YEARS).toEquivalent((PeriodUnit) null);
//    }
//
//    //-----------------------------------------------------------------------
//    // toEquivalent(PeriodUnit...)
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_toEquivalent_units_yearsToYears() {
//        Period test = Period.of(5, YEARS).toEquivalent(new PeriodUnit[] {YEARS});
//        assertEquals(test, Period.of(5, YEARS));
//    }
//
//    @Test(groups={"tck"})
//    public void test_toEquivalent_units_yearsToMonths() {
//        Period test = Period.of(5, YEARS).toEquivalent(new PeriodUnit[] {MONTHS});
//        assertEquals(test, Period.of(5 * 12, MONTHS));
//    }
//
//    @Test(groups={"tck"})
//    public void test_toEquivalent_units_yearsToYearsMonthsOrDays() {
//        Period test = Period.of(5, YEARS).toEquivalent(YEARS, MONTHS, DAYS);
//        assertEquals(test, Period.of(5, YEARS));
//    }
//
//    @Test(groups={"tck"})
//    public void test_toEquivalent_units_yearsToMonthsOrDays() {
//        Period test = Period.of(5, YEARS).toEquivalent(MONTHS, DAYS);
//        assertEquals(test, Period.of(5 * 12, MONTHS));
//    }
//
//    @Test(groups={"tck"})
//    public void test_toEquivalent_units_yearsToDaysOrMonths() {
//        Period test = Period.of(5, YEARS).toEquivalent(DAYS, MONTHS);
//        assertEquals(test, Period.of(5 * 12, MONTHS));
//    }
//
//    @Test(groups={"tck"})
//    public void test_toEquivalent_units_hoursToMinutesOrSeconds() {
//        Period test = Period.of(5, HOURS).toEquivalent(MINUTES, SECONDS);
//        assertEquals(test, Period.of(5 * 60, MINUTES));
//    }
//
//    @Test(groups={"tck"})
//    public void test_toEquivalent_units_hoursToSecondsOrMinutes() {
//        Period test = Period.of(5, HOURS).toEquivalent(SECONDS, MINUTES);
//        assertEquals(test, Period.of(5 * 60 * 60, SECONDS));
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
//    public void test_toEquivalent_units_tooBig() {
//        Period.of(Long.MAX_VALUE / 12 + 12, YEARS).toEquivalent(new PeriodUnit[] {MONTHS});
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_toEquivalent_units_noUnits() {
//        try {
//            Period.of(5, YEARS).toEquivalent(new PeriodUnit[0]);
//        } catch (CalendricalException ex) {
//            assertEquals("Unable to convert Years to any requested unit: []", ex.getMessage());
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_toEquivalent_units_noConversionOneUnit() {
//        try {
//            Period.of(5, YEARS).toEquivalent(new PeriodUnit[] {DAYS});
//        } catch (CalendricalException ex) {
//            assertEquals("Unable to convert Years to any requested unit: [Days]", ex.getMessage());
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_toEquivalent_units_noConversionTwoUnits() {
//        try {
//            Period.of(5, YEARS).toEquivalent(DAYS, HOURS);
//        } catch (CalendricalException ex) {
//            assertEquals("Unable to convert Years to any requested unit: [Days, Hours]", ex.getMessage());
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_toEquivalent_units_null() {
//        Period.of(5, YEARS).toEquivalent((PeriodUnit[]) null);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_toEquivalent_units_arrayContainsNull() {
//        Period.of(5, YEARS).toEquivalent(null, YEARS);
//    }
//
//    //-----------------------------------------------------------------------
//    // toDurationEstimate()
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_toDurationEstimate() {
//        Duration test = Period.of(5, DAYS).toDurationEstimate();
//        Duration fiveDays = ISOPeriodUnit.DAYS.getDurationEstimate().multipliedBy(5);
//        assertEquals(test, fiveDays);
//    }
//
//    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
//    public void test_toDurationEstimate_tooBig() {
//        Period.of(Long.MAX_VALUE, MINUTES).toDurationEstimate();
//    }
//
//    //-----------------------------------------------------------------------
//    // toDuration()
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_toDuration_hours() {
//        Duration test = Period.of(5, HOURS).toDuration();
//        Duration fiveHours = Duration.ofHours(5);
//        assertEquals(test, fiveHours);
//    }
//
//    @Test(groups={"tck"})
//    public void test_toDuration_millis() {
//        Duration test = Period.of(5, ISOPeriodUnit.MILLIS).toDuration();
//        Duration fiveMillis = Duration.ofMillis(5);
//        assertEquals(test, fiveMillis);
//    }
//
//    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
//    public void test_toDuration_cannotConvert() {
//        try {
//            Period.of(5, MONTHS).toDuration();
//        } catch (CalendricalException ex) {
//            assertEquals(ex.getMessage(), "Unable to convert Months to a Duration");
//            throw ex;
//        }
//    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_compareTo() {
        Period a = Period.of(5, DAYS);
        Period b = Period.of(6, DAYS);
        assertEquals(a.compareTo(a), 0);
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
    }

    @Test(expectedExceptions=IllegalArgumentException.class,groups={"tck"})
    public void test_compareTo_differentUnits() {
        Period a = Period.of(6 * 60, MINUTES);
        Period b = Period.of(5, HOURS);
        a.compareTo(b);
    }

    @Test(expectedExceptions = {NullPointerException.class}, groups={"tck"})
    public void test_compareTo_null() {
        Period test5 = Period.of(5, DAYS);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_equals() {
        Period a = Period.of(5, DAYS);
        Period b = Period.of(6, DAYS);
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    @Test(groups={"tck"})
    public void test_equals_null() {
        Period test = Period.of(5, DAYS);
        assertEquals(test.equals(null), false);
    }

    @Test(groups={"tck"})
    public void test_equals_otherClass() {
        Period test = Period.of(5, DAYS);
        assertEquals(test.equals(""), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_hashCode() {
        Period a = Period.of(5, DAYS);
        Period b = Period.of(6, DAYS);
        Period c = Period.of(5, HOURS);
        assertEquals(a.hashCode() == a.hashCode(), true);
        assertEquals(a.hashCode() == b.hashCode(), false);
        assertEquals(a.hashCode() == c.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        Period test5 = Period.of(5, DAYS);
        assertEquals(test5.toString(), "5 Days");
        Period testM1 = Period.of(-1, MONTHS);
        assertEquals(testM1.toString(), "-1 Months");
    }

}
