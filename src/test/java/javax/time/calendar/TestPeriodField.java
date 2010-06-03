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
package javax.time.calendar;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.PeriodField;
import javax.time.calendar.PeriodFields;
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

    private static final PeriodUnit YEARS = ISOChronology.periodYears();
    private static final PeriodUnit QUARTERS = ISOChronology.periodQuarters();
    private static final PeriodUnit MONTHS = ISOChronology.periodMonths();
    private static final PeriodUnit DAYS = ISOChronology.periodDays();
    private static final PeriodUnit HOURS = ISOChronology.periodHours();
    private static final PeriodUnit MINUTES = ISOChronology.periodMinutes();
    private static final PeriodUnit SECONDS = ISOChronology.periodSeconds();

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
    // getAmount()
    //-----------------------------------------------------------------------
    public void test_getAmount() {
        assertEquals(PeriodField.of(0, DAYS).getAmount(), 0L);
        assertEquals(PeriodField.of(1, DAYS).getAmount(), 1L);
        assertEquals(PeriodField.of(-1, DAYS).getAmount(), -1L);
        assertEquals(PeriodField.of(Long.MAX_VALUE, DAYS).getAmount(), Long.MAX_VALUE);
        assertEquals(PeriodField.of(Long.MIN_VALUE, DAYS).getAmount(), Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // getAmountInt()
    //-----------------------------------------------------------------------
    public void test_getAmountInt() {
        assertEquals(PeriodField.of(0, DAYS).getAmountInt(), 0);
        assertEquals(PeriodField.of(1, DAYS).getAmountInt(), 1);
        assertEquals(PeriodField.of(-1, DAYS).getAmountInt(), -1);
        assertEquals(PeriodField.of(Integer.MAX_VALUE, DAYS).getAmountInt(), Integer.MAX_VALUE);
        assertEquals(PeriodField.of(Integer.MIN_VALUE, DAYS).getAmountInt(), Integer.MIN_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_getAmountInt_tooBig() {
        PeriodField.of(Integer.MAX_VALUE + 1L, DAYS).getAmountInt();
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_getAmountInt_tooSmall() {
        PeriodField.of(Integer.MIN_VALUE - 1L, DAYS).getAmountInt();
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
    // withUnit()
    //-----------------------------------------------------------------------
    public void test_withUnit() {
        assertEquals(PeriodField.of(0, DAYS).withUnit(MONTHS), PeriodField.of(0, MONTHS));
        assertEquals(PeriodField.of(1, DAYS).withUnit(MONTHS), PeriodField.of(1, MONTHS));
        assertEquals(PeriodField.of(-1, DAYS).withUnit(MONTHS), PeriodField.of(-1, MONTHS));
    }

    public void test_withUnit_same() {
        PeriodField base = PeriodField.of(1, DAYS);
        assertSame(base.withUnit(DAYS), base);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withUnit_null() {
        PeriodField.of(1, DAYS).withUnit(null);
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

    public void test_multipliedBy_same() {
        PeriodField base = PeriodField.of(12, DAYS);
        assertSame(base.multipliedBy(1), base);
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

    public void test_dividedBy_same() {
        PeriodField base = PeriodField.of(12, DAYS);
        assertSame(base.dividedBy(1), base);
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
    // remainder(long)
    //-----------------------------------------------------------------------
    public void test_remainder() {
        PeriodField test12 = PeriodField.of(13, DAYS);
        assertEquals(test12.remainder(1), PeriodField.of(0, DAYS));
        assertEquals(test12.remainder(2), PeriodField.of(1, DAYS));
        assertEquals(test12.remainder(3), PeriodField.of(1, DAYS));
        assertEquals(test12.remainder(4), PeriodField.of(1, DAYS));
        assertEquals(test12.remainder(5), PeriodField.of(3, DAYS));
        assertEquals(test12.remainder(6), PeriodField.of(1, DAYS));
        assertEquals(test12.remainder(-3), PeriodField.of(1, DAYS));
    }

    public void test_remainder_negate() {
        PeriodField test12 = PeriodField.of(-14, DAYS);
        assertEquals(test12.remainder(-5), PeriodField.of(-4, DAYS));
    }

    public void test_remainder_same() {
        PeriodField base = PeriodField.of(12, DAYS);
        assertSame(base.remainder(15), base);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_remainder_divideByZero() {
        PeriodField.of(1, DAYS).remainder(0);
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
    // toEquivalent(PeriodUnit)
    //-----------------------------------------------------------------------
    public void test_toEquivalent_unit() {
        PeriodField test = PeriodField.of(5, YEARS).toEquivalent(QUARTERS);
        assertEquals(test, PeriodField.of(5 * 4, QUARTERS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_toEquivalent_unit_tooBig() {
        PeriodField.of(Long.MAX_VALUE / 12 + 12, YEARS).toEquivalent(MONTHS);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalent_unit_noConversion() {
        try {
            PeriodField.of(5, YEARS).toEquivalent(DAYS);
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert Years to Days", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toEquivalent_null() {
        PeriodField.of(5, YEARS).toEquivalent((PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // toEquivalent(PeriodUnit...)
    //-----------------------------------------------------------------------
    public void test_toEquivalent_units_yearsToYears() {
        PeriodField test = PeriodField.of(5, YEARS).toEquivalent(new PeriodUnit[] {YEARS});
        assertEquals(test, PeriodField.of(5, YEARS));
    }

    public void test_toEquivalent_units_yearsToMonths() {
        PeriodField test = PeriodField.of(5, YEARS).toEquivalent(new PeriodUnit[] {MONTHS});
        assertEquals(test, PeriodField.of(5 * 12, MONTHS));
    }

    public void test_toEquivalent_units_yearsToYearsMonthsOrDays() {
        PeriodField test = PeriodField.of(5, YEARS).toEquivalent(YEARS, MONTHS, DAYS);
        assertEquals(test, PeriodField.of(5, YEARS));
    }

    public void test_toEquivalent_units_yearsToMonthsOrDays() {
        PeriodField test = PeriodField.of(5, YEARS).toEquivalent(MONTHS, DAYS);
        assertEquals(test, PeriodField.of(5 * 12, MONTHS));
    }

    public void test_toEquivalent_units_yearsToDaysOrMonths() {
        PeriodField test = PeriodField.of(5, YEARS).toEquivalent(DAYS, MONTHS);
        assertEquals(test, PeriodField.of(5 * 12, MONTHS));
    }

    public void test_toEquivalent_units_hoursToMinutesOrSeconds() {
        PeriodField test = PeriodField.of(5, HOURS).toEquivalent(MINUTES, SECONDS);
        assertEquals(test, PeriodField.of(5 * 60, MINUTES));
    }

    public void test_toEquivalent_units_hoursToSecondsOrMinutes() {
        PeriodField test = PeriodField.of(5, HOURS).toEquivalent(SECONDS, MINUTES);
        assertEquals(test, PeriodField.of(5 * 60 * 60, SECONDS));
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_toEquivalent_units_tooBig() {
        PeriodField.of(Long.MAX_VALUE / 12 + 12, YEARS).toEquivalent(new PeriodUnit[] {MONTHS});
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalent_units_noUnits() {
        try {
            PeriodField.of(5, YEARS).toEquivalent(new PeriodUnit[0]);
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert Years to any requested unit: []", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalent_units_noConversionOneUnit() {
        try {
            PeriodField.of(5, YEARS).toEquivalent(new PeriodUnit[] {DAYS});
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert Years to any requested unit: [Days]", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toEquivalent_units_noConversionTwoUnits() {
        try {
            PeriodField.of(5, YEARS).toEquivalent(DAYS, HOURS);
        } catch (CalendricalException ex) {
            assertEquals("Unable to convert Years to any requested unit: [Days, Hours]", ex.getMessage());
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toEquivalent_units_null() {
        PeriodField.of(5, YEARS).toEquivalent((PeriodUnit[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toEquivalent_units_arrayContainsNull() {
        PeriodField.of(5, YEARS).toEquivalent(null, YEARS);
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
    // toDuration()
    //-----------------------------------------------------------------------
    public void test_toDuration_hours() {
        Duration test = PeriodField.of(5, HOURS).toDuration();
        Duration fiveHours = Duration.ofStandardHours(5);
        assertEquals(test, fiveHours);
    }

    public void test_toDuration_millis() {
        Duration test = PeriodField.of(5, ISOChronology.periodMillis()).toDuration();
        Duration fiveMillis = Duration.ofMillis(5);
        assertEquals(test, fiveMillis);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_toDuration_cannotConvert() {
        try {
            PeriodField.of(5, MONTHS).toDuration();
        } catch (CalendricalException ex) {
            assertEquals(ex.getMessage(), "Unable to convert Months to a Duration");
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // toPeriodFields()
    //-----------------------------------------------------------------------
    public void test_toPeriodFields() {
        PeriodFields test = PeriodField.of(5, DAYS).toPeriodFields();
        assertEquals(test, PeriodFields.of(5, DAYS));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        PeriodField a = PeriodField.of(5, DAYS);
        PeriodField b = PeriodField.of(6, DAYS);
        assertEquals(0, a.compareTo(a));
        assertEquals(-1, a.compareTo(b));
        assertEquals(1, b.compareTo(a));
    }

    public void test_compareTo_differentUnits() {
        PeriodField a = PeriodField.of(6 * 60, MINUTES);  // longer than 5 hours
        PeriodField b = PeriodField.of(5, HOURS);
        assertEquals(0, a.compareTo(a));
        assertEquals(-1, a.compareTo(b));
        assertEquals(1, b.compareTo(a));
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_compareTo_null() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        test5.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    public void test_equals() {
        PeriodField a = PeriodField.of(5, DAYS);
        PeriodField b = PeriodField.of(6, DAYS);
        assertEquals(true, a.equals(a));
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    public void test_equals_null() {
        PeriodField test = PeriodField.of(5, DAYS);
        assertEquals(false, test.equals(null));
    }

    public void test_equals_otherClass() {
        PeriodField test = PeriodField.of(5, DAYS);
        assertEquals(false, test.equals(""));
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    public void test_hashCode() {
        PeriodField a = PeriodField.of(5, DAYS);
        PeriodField b = PeriodField.of(6, DAYS);
        PeriodField c = PeriodField.of(5, HOURS);
        assertEquals(true, a.hashCode() == a.hashCode());
        assertEquals(false, a.hashCode() == b.hashCode());
        assertEquals(false, a.hashCode() == c.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        PeriodField test5 = PeriodField.of(5, DAYS);
        assertEquals("5 Days", test5.toString());
        PeriodField testM1 = PeriodField.of(-1, MONTHS);
        assertEquals("-1 Months", testM1.toString());
    }

}
