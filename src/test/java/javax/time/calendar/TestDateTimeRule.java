/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISOPeriodUnit.DAYS;
import static javax.time.calendar.ISOPeriodUnit.HOURS;
import static javax.time.calendar.ISOPeriodUnit.MINUTES;
import static javax.time.calendar.ISOPeriodUnit.WEEKS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Locale;

import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

import org.testng.annotations.Test;

/**
 * Test DateTimeRule.
 */
@Test
public class TestDateTimeRule {

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalRule.class.isAssignableFrom(DateTimeRule.class));
        assertTrue(Serializable.class.isAssignableFrom(DateTimeRule.class));
        assertTrue(Comparable.class.isAssignableFrom(DateTimeRule.class));
        assertTrue(Comparator.class.isAssignableFrom(DateTimeRule.class));
    }

    public void test_serialization() throws Exception {
        DateTimeRule rule = new Mock(HOURS, DAYS, 0, 23);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rule);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), rule);
    }

    //-----------------------------------------------------------------------
    // isIntValue()
    //-----------------------------------------------------------------------
    public void test_isIntValue() {
        assertEquals(new Mock(HOURS, DAYS, 0, 23).isIntValue(), true);
        assertEquals(new Mock(HOURS, DAYS, -1, 23).isIntValue(), true);
        assertEquals(new Mock(HOURS, DAYS, Integer.MIN_VALUE, Integer.MAX_VALUE).isIntValue(), true);
        assertEquals(new Mock(HOURS, DAYS, ((long) Integer.MIN_VALUE) - 1, Integer.MAX_VALUE).isIntValue(), false);
        assertEquals(new Mock(HOURS, DAYS, Integer.MIN_VALUE, ((long) Integer.MAX_VALUE) + 1).isIntValue(), false);
        assertEquals(new Mock(HOURS, DAYS, Long.MIN_VALUE, Long.MAX_VALUE).isIntValue(), false);
    }

    //-----------------------------------------------------------------------
    // checkIntValue()
    //-----------------------------------------------------------------------
    public void test_checkIntValue() {
        new Mock(HOURS, DAYS, 0, 23).checkIntValue();
        new Mock(HOURS, DAYS, -1, 23).checkIntValue();
        new Mock(HOURS, DAYS, Integer.MIN_VALUE, Integer.MAX_VALUE).checkIntValue();
    }

    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_checkIntValue_bad() {
        Mock rule = new Mock(HOURS, DAYS, 0, Long.MAX_VALUE);
        try {
            rule.checkIntValue();
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), rule);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // isValidValue()
    //-----------------------------------------------------------------------
    public void test_isValidValue() {
        assertEquals(new Mock(HOURS, DAYS, 0, 23).isValidValue(0), true);
        assertEquals(new Mock(HOURS, DAYS, 0, 23).isValidValue(1), true);
        assertEquals(new Mock(HOURS, DAYS, 0, 23).isValidValue(23), true);
        assertEquals(new Mock(HOURS, DAYS, 0, 23).isValidValue(-1), false);
        assertEquals(new Mock(HOURS, DAYS, 0, 23).isValidValue(24), false);
        assertEquals(new Mock(HOURS, DAYS, 0, 23).isValidValue(Long.MIN_VALUE), false);
        assertEquals(new Mock(HOURS, DAYS, 0, 23).isValidValue(Long.MAX_VALUE), false);
    }

    //-----------------------------------------------------------------------
    // checkValidValue()
    //-----------------------------------------------------------------------
    public void test_checkValidValue() {
        new Mock(HOURS, DAYS, 0, 23).checkValidValue(0);
        new Mock(HOURS, DAYS, 0, 23).checkValidValue(23);
    }

    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_checkValidValue_bad() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23);
        try {
            rule.checkValidValue(24);
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), rule);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // basics()
    //-----------------------------------------------------------------------
    public void test_basics() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23);
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getSmallestMaximumValue(), 23);
        assertEquals(rule.getMaximumValue(), 23);
        assertEquals(rule.isFixedValueSet(), true);
    }

    public void test_isFixedValueSet() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23, 21);
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getSmallestMaximumValue(), 21);
        assertEquals(rule.getMaximumValue(), 23);
        assertEquals(rule.isFixedValueSet(), false);
    }

    //-----------------------------------------------------------------------
    // getText()
    //-----------------------------------------------------------------------
    public void test_getText() {
        assertEquals(MONTH_OF_YEAR.getText(1, TextStyle.SHORT, Locale.US), "Jan");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getText_nullStyle() {
        MONTH_OF_YEAR.getText(1, null, Locale.US);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_getText_nullLocale() {
        MONTH_OF_YEAR.getText(1, TextStyle.FULL, null);
    }

    //-----------------------------------------------------------------------
    // convertToFraction()
    //-----------------------------------------------------------------------
    public void test_convertToFraction_zeroBased() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23);
        assertEquals(rule.convertToFraction(0), new BigDecimal("0"));
        assertEquals(rule.convertToFraction(6), new BigDecimal("0.25"));
        assertEquals(rule.convertToFraction(12), new BigDecimal("0.5"));
        assertEquals(rule.convertToFraction(18), new BigDecimal("0.75"));
    }

    public void test_convertToFraction_oneBased() {
        Mock rule = new Mock(HOURS, DAYS, 1, 10);
        assertEquals(rule.convertToFraction(1), new BigDecimal("0"));
        assertEquals(rule.convertToFraction(5), new BigDecimal("0.4"));
        assertEquals(rule.convertToFraction(8), new BigDecimal("0.7"));
        assertEquals(rule.convertToFraction(10), new BigDecimal("0.9"));
    }

    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_convertToFraction_zeroBased_notFixedSet() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23, 21);
        try {
            rule.convertToFraction(0);
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), rule);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_convertToFraction_zeroBased_invalidValue() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23);
        try {
            rule.convertToFraction(24);
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), rule);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // convertFromFraction()
    //-----------------------------------------------------------------------
    public void test_convertFromFraction_zeroBased() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23);
        assertEquals(rule.convertFromFraction(new BigDecimal("0")), 0);
        assertEquals(rule.convertFromFraction(new BigDecimal("0.25")), 6);
        assertEquals(rule.convertFromFraction(new BigDecimal("0.5")), 12);
        assertEquals(rule.convertFromFraction(new BigDecimal("0.75")), 18);
    }

    public void test_convertFromFraction_oneBased() {
        Mock rule = new Mock(HOURS, DAYS, 1, 10);
        assertEquals(rule.convertFromFraction(new BigDecimal("0")), 1);
        assertEquals(rule.convertFromFraction(new BigDecimal("0.15")), 2);
        assertEquals(rule.convertFromFraction(new BigDecimal("0.4")), 5);
        assertEquals(rule.convertFromFraction(new BigDecimal("0.7")), 8);
        assertEquals(rule.convertFromFraction(new BigDecimal("0.9")), 10);
    }

    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_convertFromFraction_zeroBased_notFixedSet() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23, 21);
        try {
            rule.convertFromFraction(BigDecimal.ZERO);
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), rule);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_convertFromFraction_zeroBased_invalidValue() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23);
        try {
            rule.convertFromFraction(BigDecimal.ONE);
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), rule);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_convertFromFraction_zeroBased_invalidValueNegative() {
        Mock rule = new Mock(HOURS, DAYS, 0, 23);
        try {
            rule.convertFromFraction(BigDecimal.valueOf(-0.1d));
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), rule);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        DateTimeRule a = new Mock(MINUTES, HOURS, 0, 59);
        DateTimeRule b = new Mock(HOURS, DAYS, 0, 23);
        DateTimeRule c = new Mock(HOURS, WEEKS, 0, 24 * 7 - 1);
        
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(a.compareTo(c) < 0, true);
        
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(b.compareTo(c) < 0, true);
        
        assertEquals(c.compareTo(a) > 0, true);
        assertEquals(c.compareTo(b) > 0, true);
        assertEquals(c.compareTo(c) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_null() {
        DateTimeField.of(MONTH_OF_YEAR, 6).compareTo(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals1() {
        DateTimeRule a = new Mock(MINUTES, HOURS, 0, 59);
        DateTimeRule b = new Mock(MINUTES, HOURS, 0, 59);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2() {
        DateTimeRule a = new Mock(MINUTES, HOURS, 0, 59);
        DateTimeRule b = new Mock(HOURS, DAYS, 0, 23);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_otherType() {
        DateTimeRule a = new Mock(MINUTES, HOURS, 0, 59);
        assertEquals(a.equals("Rubbish"), false);
    }

    public void test_equals_null() {
        DateTimeRule a = new Mock(MINUTES, HOURS, 0, 59);
        assertEquals(a.equals(null), false);
    }

    //-----------------------------------------------------------------------
    static class Mock extends DateTimeRule {
        private static final long serialVersionUID = 1L;
        private final long smallMax;
        
        protected Mock(PeriodUnit unit, PeriodUnit range, long min, long max) {
            this(unit, range, min, max, max);
        }
        protected Mock(PeriodUnit unit, PeriodUnit range, long min, long max, long smallMax) {
            super(ISOChronology.INSTANCE, unit.toString() + "Of" + range.toString(), unit, range, min, max);
            this.smallMax = smallMax;
        }
        @Override
        public long getSmallestMaximumValue() {
            return smallMax;
        }
    }

}
