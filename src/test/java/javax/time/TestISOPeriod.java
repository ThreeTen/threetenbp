/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.PeriodUnit;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestISOPeriod {

    private static final PeriodUnit MILLENNIA = LocalPeriodUnit.MILLENNIA;
    private static final PeriodUnit CENTURIES = LocalPeriodUnit.CENTURIES;
    private static final PeriodUnit DECADES = LocalPeriodUnit.DECADES;
    private static final PeriodUnit YEARS = LocalPeriodUnit.YEARS;
    private static final PeriodUnit WEEK_BASED_YEARS = LocalPeriodUnit.WEEK_BASED_YEARS;
    private static final PeriodUnit QUARTER_YEARS = LocalPeriodUnit.QUARTER_YEARS;
    private static final PeriodUnit HALF_YEARS = LocalPeriodUnit.HALF_YEARS;
    private static final PeriodUnit MONTHS = LocalPeriodUnit.MONTHS;
    private static final PeriodUnit WEEKS = LocalPeriodUnit.WEEKS;
    private static final PeriodUnit DAYS = LocalPeriodUnit.DAYS;
    private static final PeriodUnit HALF_DAYS = LocalPeriodUnit.HALF_DAYS;
    private static final PeriodUnit HOURS = LocalPeriodUnit.HOURS;
    private static final PeriodUnit MINUTES = LocalPeriodUnit.MINUTES;
    private static final PeriodUnit SECONDS = LocalPeriodUnit.SECONDS;
    private static final PeriodUnit MILLIS = LocalPeriodUnit.MILLIS;
    private static final PeriodUnit MICROS = LocalPeriodUnit.MICROS;
    private static final PeriodUnit NANOS = LocalPeriodUnit.NANOS;

    private static final BigInteger MAX_BINT = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger BINT_24 = BigInteger.valueOf(24);
    private static final BigInteger BINT_60 = BigInteger.valueOf(60);
    private static final BigInteger BINT_1BN = BigInteger.valueOf(1000000000L);

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(ISOPeriod.class));
    }

    @DataProvider(name="serialization")
    Object[][] data_serialization() {
        return new Object[][] {
            {ISOPeriod.ZERO},
            {ISOPeriod.of(0, DAYS)},
            {ISOPeriod.of(1, DAYS)},
            {ISOPeriod.of(1, 2, 3, 4, 5, 6)},
        };
    }

    @Test(dataProvider="serialization")
    public void test_serialization(ISOPeriod period) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(period);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        if (period.isZero()) {
            assertSame(ois.readObject(), period);
        } else {
            assertEquals(ois.readObject(), period);
        }
    }

    public void test_immutable() {
        Class<ISOPeriod> cls = ISOPeriod.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false && !Modifier.isTransient(field.getModifiers())) {
                assertTrue(Modifier.isPrivate(field.getModifiers()));
                assertTrue(Modifier.isFinal(field.getModifiers()));
            }
        }
        Constructor<?>[] cons = cls.getDeclaredConstructors();
        for (Constructor<?> con : cons) {
            assertTrue(Modifier.isPrivate(con.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    public void factory_zeroSingleton() {
        assertSame(ISOPeriod.ZERO, ISOPeriod.ZERO);
        assertSame(ISOPeriod.of(0, 0, 0, 0, 0, 0), ISOPeriod.ZERO);
        assertSame(ISOPeriod.of(0, 0, 0, 0, 0, 0, 0), ISOPeriod.ZERO);
        assertSame(ISOPeriod.ofDate(0, 0, 0), ISOPeriod.ZERO);
        assertSame(ISOPeriod.ofTime(0, 0, 0), ISOPeriod.ZERO);
        assertSame(ISOPeriod.ofTime(0, 0, 0, 0), ISOPeriod.ZERO);
        assertSame(ISOPeriod.of(0, YEARS), ISOPeriod.ZERO);
        assertSame(ISOPeriod.of(0, MONTHS), ISOPeriod.ZERO);
        assertSame(ISOPeriod.of(0, DAYS), ISOPeriod.ZERO);
        assertSame(ISOPeriod.of(0, HOURS), ISOPeriod.ZERO);
        assertSame(ISOPeriod.of(0, MINUTES), ISOPeriod.ZERO);
        assertSame(ISOPeriod.of(0, SECONDS), ISOPeriod.ZERO);
        assertSame(ISOPeriod.of(0, NANOS), ISOPeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // of(PeriodProvider)
    //-----------------------------------------------------------------------
    public void factory_of_ints() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 6), 1, 2, 3, 4, 5, 6, 0);
        assertPeriod(ISOPeriod.of(0, 2, 3, 4, 5, 6), 0, 2, 3, 4, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 0, 0, 0, 0, 0), 1, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(0, 0, 0, 0, 0, 0), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(-1, -2, -3, -4, -5, -6), -1, -2, -3, -4, -5, -6, 0);
    }

    //-----------------------------------------------------------------------
    // ofDate
    //-----------------------------------------------------------------------
    public void factory_ofDate_ints() {
        assertPeriod(ISOPeriod.ofDate(1, 2, 3), 1, 2, 3, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.ofDate(0, 2, 3), 0, 2, 3, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.ofDate(1, 0, 0), 1, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.ofDate(0, 0, 0), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.ofDate(-1, -2, -3), -1, -2, -3, 0, 0, 0, 0);
    }

    //-----------------------------------------------------------------------
    // ofTime
    //-----------------------------------------------------------------------
    public void factory_ofTime_3ints() {
        assertPeriod(ISOPeriod.ofTime(1, 2, 3), 0, 0, 0, 1, 2, 3, 0);
        assertPeriod(ISOPeriod.ofTime(0, 2, 3), 0, 0, 0, 0, 2, 3, 0);
        assertPeriod(ISOPeriod.ofTime(1, 0, 0), 0, 0, 0, 1, 0, 0, 0);
        assertPeriod(ISOPeriod.ofTime(0, 0, 0), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.ofTime(-1, -2, -3), 0, 0, 0, -1, -2, -3, 0);
    }

    public void factory_ofTime_4ints() {
        assertPeriod(ISOPeriod.ofTime(1, 2, 3, 4), 0, 0, 0, 1, 2, 3, 4);
        assertPeriod(ISOPeriod.ofTime(0, 2, 3, 4), 0, 0, 0, 0, 2, 3, 4);
        assertPeriod(ISOPeriod.ofTime(1, 0, 0, 0), 0, 0, 0, 1, 0, 0, 0);
        assertPeriod(ISOPeriod.ofTime(0, 0, 0, 0), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.ofTime(-1, -2, -3, -4), 0, 0, 0, -1, -2, -3, -4);
    }

    //-----------------------------------------------------------------------
    // of one field
    //-----------------------------------------------------------------------
    public void test_factory_of_intPeriodUnit() {
        assertEquals(ISOPeriod.of(1, YEARS), ISOPeriod.of(1, YEARS));
        assertEquals(ISOPeriod.of(2, WEEK_BASED_YEARS), ISOPeriod.of(2, YEARS));
        assertEquals(ISOPeriod.of(2, MONTHS), ISOPeriod.of(2, MONTHS));
        assertEquals(ISOPeriod.of(3, DAYS), ISOPeriod.of(3, DAYS));
        assertEquals(ISOPeriod.of(1, HALF_DAYS), ISOPeriod.of(12, HOURS));
        assertEquals(ISOPeriod.of(Integer.MAX_VALUE, HOURS), ISOPeriod.of(Integer.MAX_VALUE, HOURS));
        assertEquals(ISOPeriod.of(-1, MINUTES), ISOPeriod.of(-1, MINUTES));
        assertEquals(ISOPeriod.of(-2, SECONDS), ISOPeriod.of(-2, SECONDS));
        assertEquals(ISOPeriod.of(Integer.MIN_VALUE, NANOS), ISOPeriod.of(Integer.MIN_VALUE, NANOS));
    }

    public void test_factory_of_intPeriodUnit_convert() {
        assertEquals(ISOPeriod.of(2, MILLIS), ISOPeriod.of(2000000, NANOS));
        assertEquals(ISOPeriod.of(2, MICROS), ISOPeriod.of(2000, NANOS));
        assertEquals(ISOPeriod.of(2, WEEKS), ISOPeriod.of(14, DAYS));
        assertEquals(ISOPeriod.of(2, QUARTER_YEARS), ISOPeriod.of(6, MONTHS));
        assertEquals(ISOPeriod.of(1, HALF_YEARS), ISOPeriod.of(6 , MONTHS));
        assertEquals(ISOPeriod.of(2, DECADES), ISOPeriod.of(20, YEARS));
        assertEquals(ISOPeriod.of(2, CENTURIES), ISOPeriod.of(200, YEARS));
        assertEquals(ISOPeriod.of(2, MILLENNIA), ISOPeriod.of(2000, YEARS));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_of_intPeriodUnit_null() {
        ISOPeriod.of(1, null);
    }

    //-----------------------------------------------------------------------
    public void factory_years() {
        assertPeriod(ISOPeriod.of(1, YEARS), 1, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(0, YEARS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(-1, YEARS), -1, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MAX_VALUE, YEARS), Integer.MAX_VALUE, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MIN_VALUE, YEARS), Integer.MIN_VALUE, 0, 0, 0, 0, 0, 0);
    }

    public void factory_months() {
        assertPeriod(ISOPeriod.of(1, MONTHS), 0, 1, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(0, MONTHS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(-1, MONTHS), 0, -1, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MAX_VALUE, MONTHS), 0, Integer.MAX_VALUE, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MIN_VALUE, MONTHS), 0, Integer.MIN_VALUE, 0, 0, 0, 0, 0);
    }

    public void factory_days() {
        assertPeriod(ISOPeriod.of(1, DAYS), 0, 0, 1, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(0, DAYS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(-1, DAYS), 0, 0, -1, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MAX_VALUE, DAYS), 0, 0, Integer.MAX_VALUE, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MIN_VALUE, DAYS), 0, 0, Integer.MIN_VALUE, 0, 0, 0, 0);
    }

    public void factory_hours() {
        assertPeriod(ISOPeriod.of(1, HOURS), 0, 0, 0, 1, 0, 0, 0);
        assertPeriod(ISOPeriod.of(0, HOURS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(-1, HOURS), 0, 0, 0, -1, 0, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MAX_VALUE, HOURS), 0, 0, 0, Integer.MAX_VALUE, 0, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MIN_VALUE, HOURS), 0, 0, 0, Integer.MIN_VALUE, 0, 0, 0);
    }

    public void factory_minutes() {
        assertPeriod(ISOPeriod.of(1, MINUTES), 0, 0, 0, 0, 1, 0, 0);
        assertPeriod(ISOPeriod.of(0, MINUTES), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(-1, MINUTES), 0, 0, 0, 0, -1, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MAX_VALUE, MINUTES), 0, 0, 0, 0, Integer.MAX_VALUE, 0, 0);
        assertPeriod(ISOPeriod.of(Integer.MIN_VALUE, MINUTES), 0, 0, 0, 0, Integer.MIN_VALUE, 0, 0);
    }

    public void factory_seconds() {
        assertPeriod(ISOPeriod.of(1, SECONDS), 0, 0, 0, 0, 0, 1, 0);
        assertPeriod(ISOPeriod.of(0, SECONDS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(-1, SECONDS), 0, 0, 0, 0, 0, -1, 0);
        assertPeriod(ISOPeriod.of(Integer.MAX_VALUE, SECONDS), 0, 0, 0, 0, 0, Integer.MAX_VALUE, 0);
        assertPeriod(ISOPeriod.of(Integer.MIN_VALUE, SECONDS), 0, 0, 0, 0, 0, Integer.MIN_VALUE, 0);
    }

    public void factory_nanos() {
        assertPeriod(ISOPeriod.of(1, NANOS), 0, 0, 0, 0, 0, 0, 1);
        assertPeriod(ISOPeriod.of(0, NANOS), 0, 0, 0, 0, 0, 0, 0);
        assertPeriod(ISOPeriod.of(-1, NANOS), 0, 0, 0, 0, 0, 0, -1);
        assertPeriod(ISOPeriod.of(Long.MAX_VALUE, NANOS), 0, 0, 0, 0, 0, 0, Long.MAX_VALUE);
        assertPeriod(ISOPeriod.of(Long.MIN_VALUE, NANOS), 0, 0, 0, 0, 0, 0, Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // of(Period)
    //-----------------------------------------------------------------------
    @DataProvider(name="PeriodData")
    Object[][] data_ofPeriod() {
        return new Object[][] {
            {SingleUnitPeriod.ZERO_DAYS, ISOPeriod.ZERO},
            {SingleUnitPeriod.ZERO_SECONDS, ISOPeriod.ZERO},
            {SingleUnitPeriod.of(0, DAYS), ISOPeriod.ZERO},
            {SingleUnitPeriod.of(1, MILLENNIA), ISOPeriod.ofDate(1000, 0, 0)},
            {SingleUnitPeriod.of(1, CENTURIES), ISOPeriod.ofDate(100, 0, 0)},
            {SingleUnitPeriod.of(1, DECADES), ISOPeriod.ofDate(10, 0, 0)},
            {SingleUnitPeriod.of(1, WEEK_BASED_YEARS), ISOPeriod.ofDate(1, 0, 0)},
            {SingleUnitPeriod.of(1, YEARS), ISOPeriod.ofDate(1, 0, 0)},
            {SingleUnitPeriod.of(1, HALF_YEARS), ISOPeriod.ofDate(0, 6, 0)},
            {SingleUnitPeriod.of(1, QUARTER_YEARS), ISOPeriod.ofDate(0, 3, 0)},
            {SingleUnitPeriod.of(1, MONTHS), ISOPeriod.ofDate(0, 1, 0)},
            {SingleUnitPeriod.of(1, DAYS), ISOPeriod.ofDate(0, 0, 1)},
            {SingleUnitPeriod.of(1, HOURS), ISOPeriod.ofTime(1, 0, 0)},
            {SingleUnitPeriod.of(1, MINUTES), ISOPeriod.ofTime(0, 1, 0)},
            {SingleUnitPeriod.of(1, SECONDS), ISOPeriod.ofTime(0, 0, 1)},
            {SingleUnitPeriod.of(1, NANOS), ISOPeriod.ofTime(0, 0, 0, 1)},
        };
    }

    @Test(dataProvider="PeriodData")
    public void factory_ofPeriod(SingleUnitPeriod period, ISOPeriod localPeriod) {
        ISOPeriod test = ISOPeriod.of(period);
        assertEquals(localPeriod, test);

    }


    //-----------------------------------------------------------------------
    // of(Duration)
    //-----------------------------------------------------------------------
    public void factory_duration() {
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(2, 3)), 0, 0, 0, 0, 0, 2, 3);
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(59, 3)), 0, 0, 0, 0, 0, 59, 3);
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(60, 3)), 0, 0, 0, 0, 1, 0, 3);
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(61, 3)), 0, 0, 0, 0, 1, 1, 3);
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(3599, 3)), 0, 0, 0, 0, 59, 59, 3);
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(3600, 3)), 0, 0, 0, 1, 0, 0, 3);
    }

    public void factory_duration_negative() {
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(-2, 3)), 0, 0, 0, 0, 0, -2, 3);
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(-59, 3)), 0, 0, 0, 0, 0, -59, 3);
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(-60, 3)), 0, 0, 0, 0, -1, 0, 3);
        
        assertPeriod(ISOPeriod.of(Duration.ofSeconds(2, -3)), 0, 0, 0, 0, 0, 1, 999999997);
    }

    public void factory_duration_big() {
        Duration dur = Duration.ofSeconds(2, Long.MAX_VALUE);
        long secs = Long.MAX_VALUE / 1000000000 + 2;
        long nanos = Long.MAX_VALUE % 1000000000;
        assertPeriod(ISOPeriod.of(dur), 0, 0, 0, (int) (secs / 3600), (int) ((secs % 3600) / 60), (int) (secs % 60), nanos);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_duration_null() {
        ISOPeriod.of((Duration) null);
    }

    //-----------------------------------------------------------------------
    // between
    //-----------------------------------------------------------------------
    @DataProvider(name="Between")
    Object[][] data_between() {
        return new Object[][] {
            {2010, 1, 1, 2010, 1, 1, 0, 0, 0},
            {2010, 1, 1, 2010, 1, 2, 0, 0, 1},
            {2010, 1, 1, 2010, 1, 31, 0, 0, 30},
            {2010, 1, 1, 2010, 2, 1, 0, 1, 0},
            {2010, 1, 1, 2010, 2, 28, 0, 1, 27},
            {2010, 1, 1, 2010, 3, 1, 0, 2, 0},
            {2010, 1, 1, 2010, 12, 31, 0, 11, 30},
            {2010, 1, 1, 2011, 1, 1, 1, 0, 0},
            {2010, 1, 1, 2011, 12, 31, 1, 11, 30},
            {2010, 1, 1, 2012, 1, 1, 2, 0, 0},
            
            {2010, 1, 10, 2010, 1, 1, 0, 0, -9},
            {2010, 1, 10, 2010, 1, 2, 0, 0, -8},
            {2010, 1, 10, 2010, 1, 9, 0, 0, -1},
            {2010, 1, 10, 2010, 1, 10, 0, 0, 0},
            {2010, 1, 10, 2010, 1, 11, 0, 0, 1},
            {2010, 1, 10, 2010, 1, 31, 0, 0, 21},
            {2010, 1, 10, 2010, 2, 1, 0, 0, 22},
            {2010, 1, 10, 2010, 2, 9, 0, 0, 30},
            {2010, 1, 10, 2010, 2, 10, 0, 1, 0},
            {2010, 1, 10, 2010, 2, 28, 0, 1, 18},
            {2010, 1, 10, 2010, 3, 1, 0, 1, 19},
            {2010, 1, 10, 2010, 3, 9, 0, 1, 27},
            {2010, 1, 10, 2010, 3, 10, 0, 2, 0},
            {2010, 1, 10, 2010, 12, 31, 0, 11, 21},
            {2010, 1, 10, 2011, 1, 1, 0, 11, 22},
            {2010, 1, 10, 2011, 1, 9, 0, 11, 30},
            {2010, 1, 10, 2011, 1, 10, 1, 0, 0},
            
            {2010, 3, 30, 2011, 5, 1, 1, 1, 1},
            {2010, 4, 30, 2011, 5, 1, 1, 0, 1},
            
            {2010, 2, 28, 2012, 2, 27, 1, 11, 30},
            {2010, 2, 28, 2012, 2, 28, 2, 0, 0},
            {2010, 2, 28, 2012, 2, 29, 2, 0, 1},
            
            {2012, 2, 28, 2014, 2, 27, 1, 11, 30},
            {2012, 2, 28, 2014, 2, 28, 2, 0, 0},
            {2012, 2, 28, 2014, 3, 1, 2, 0, 1},
            
            {2012, 2, 29, 2014, 2, 28, 1, 11, 30},
            {2012, 2, 29, 2014, 3, 1, 2, 0, 1},
            {2012, 2, 29, 2014, 3, 2, 2, 0, 2},
            
            {2012, 2, 29, 2016, 2, 28, 3, 11, 30},
            {2012, 2, 29, 2016, 2, 29, 4, 0, 0},
            {2012, 2, 29, 2016, 3, 1, 4, 0, 1},
            
            {2010, 1, 1, 2009, 12, 31, 0, 0, -1},
            {2010, 1, 1, 2009, 12, 30, 0, 0, -2},
            {2010, 1, 1, 2009, 12, 2, 0, 0, -30},
            {2010, 1, 1, 2009, 12, 1, 0, -1, 0},
            {2010, 1, 1, 2009, 11, 30, 0, -1, -1},
            {2010, 1, 1, 2009, 11, 2, 0, -1, -29},
            {2010, 1, 1, 2009, 11, 1, 0, -2, 0},
            {2010, 1, 1, 2009, 1, 2, 0, -11, -30},
            {2010, 1, 1, 2009, 1, 1, -1, 0, 0},
            
            {2010, 1, 15, 2010, 1, 15, 0, 0, 0},
            {2010, 1, 15, 2010, 1, 14, 0, 0, -1},
            {2010, 1, 15, 2010, 1, 1, 0, 0, -14},
            {2010, 1, 15, 2009, 12, 31, 0, 0, -15},
            {2010, 1, 15, 2009, 12, 16, 0, 0, -30},
            {2010, 1, 15, 2009, 12, 15, 0, -1, 0},
            {2010, 1, 15, 2009, 12, 14, 0, -1, -1},
            
            {2010, 2, 28, 2009, 3, 1, 0, -11, -27},
            {2010, 2, 28, 2009, 2, 28, -1, 0, 0},
            {2010, 2, 28, 2009, 2, 27, -1, 0, -1},
            
            {2010, 2, 28, 2008, 2, 29, -1, -11, -28},
            {2010, 2, 28, 2008, 2, 28, -2, 0, 0},
            {2010, 2, 28, 2008, 2, 27, -2, 0, -1},
            
            {2012, 2, 29, 2009, 3, 1, -2, -11, -28},
            {2012, 2, 29, 2009, 2, 28, -3, 0, -1},
            {2012, 2, 29, 2009, 2, 27, -3, 0, -2},
            
            {2012, 2, 29, 2008, 3, 1, -3, -11, -28},
            {2012, 2, 29, 2008, 2, 29, -4, 0, 0},
            {2012, 2, 29, 2008, 2, 28, -4, 0, -1},
        };
    }

    @Test(dataProvider="Between")
    public void factory_between(int y1, int m1, int d1, int y2, int m2, int d2, int ye, int me, int de) {
        LocalDate start = LocalDate.of(y1, m1, d1);
        LocalDate end = LocalDate.of(y2, m2, d2);
        ISOPeriod test = ISOPeriod.between(start, end);
        assertPeriod(test, ye, me, de, 0, 0, 0, 0);
        //assertEquals(start.plus(test), end);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_between_nullFirst() {
        ISOPeriod.between((LocalDate) null, LocalDate.of(2010, 1, 1));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_between_nullSecond() {
        ISOPeriod.between(LocalDate.of(2010, 1, 1), (LocalDate) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @Test(dataProvider="toStringAndParse")
    public void test_parse(ISOPeriod test, String expected) {
        if (Math.signum(test.getSeconds()) == Math.signum(test.getNanos())) {
            assertEquals(test, ISOPeriod.parse(expected));
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_parse_nullText() {
        ISOPeriod.parse((String) null);
    }

    //-----------------------------------------------------------------------
    // isZero()
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertEquals(ISOPeriod.of(1, 2, 3, 4, 5, 6, 7).isZero(), false);
        assertEquals(ISOPeriod.of(1, 2, 3, 0, 0, 0, 0).isZero(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 4, 5, 6, 7).isZero(), false);
        assertEquals(ISOPeriod.of(1, 0, 0, 0, 0, 0, 0).isZero(), false);
        assertEquals(ISOPeriod.of(0, 2, 0, 0, 0, 0, 0).isZero(), false);
        assertEquals(ISOPeriod.of(0, 0, 3, 0, 0, 0, 0).isZero(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 4, 0, 0, 0).isZero(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 5, 0, 0).isZero(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 6, 0).isZero(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 0, 7).isZero(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 0).isZero(), true);
    }

    //-----------------------------------------------------------------------
    // isPositive()
    //-----------------------------------------------------------------------
    public void test_isPositive() {
        assertEquals(ISOPeriod.of(1, 2, 3, 4, 5, 6, 7).isPositive(), true);
        assertEquals(ISOPeriod.of(1, 2, 3, 0, 0, 0, 0).isPositive(), true);
        assertEquals(ISOPeriod.of(0, 0, 0, 4, 5, 6, 7).isPositive(), true);
        assertEquals(ISOPeriod.of(1, 0, 0, 0, 0, 0, 0).isPositive(), true);
        assertEquals(ISOPeriod.of(0, 2, 0, 0, 0, 0, 0).isPositive(), true);
        assertEquals(ISOPeriod.of(0, 0, 3, 0, 0, 0, 0).isPositive(), true);
        assertEquals(ISOPeriod.of(0, 0, 0, 4, 0, 0, 0).isPositive(), true);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 5, 0, 0).isPositive(), true);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 6, 0).isPositive(), true);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 0, 7).isPositive(), true);
        assertEquals(ISOPeriod.of(-1, -2, -3, -4, -5, -6, -7).isPositive(), false);
        assertEquals(ISOPeriod.of(-1, -2, 3, 4, -5, -6, -7).isPositive(), false);
        assertEquals(ISOPeriod.of(-1, 0, 0, 0, 0, 0, 0).isPositive(), false);
        assertEquals(ISOPeriod.of(0, -2, 0, 0, 0, 0, 0).isPositive(), false);
        assertEquals(ISOPeriod.of(0, 0, -3, 0, 0, 0, 0).isPositive(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, -4, 0, 0, 0).isPositive(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, -5, 0, 0).isPositive(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, -6, 0).isPositive(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 0, -7).isPositive(), false);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 0).isPositive(), false);
    }

    //-----------------------------------------------------------------------
    // getNanosInt()
    //-----------------------------------------------------------------------
    public void test_getNanosInt() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, NANOS);
        assertEquals(test.getNanosInt(), Integer.MAX_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_getNanosInt_tooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE + 1L, NANOS);
        test.getNanosInt();
    }

    //-----------------------------------------------------------------------
    // withYears()
    //-----------------------------------------------------------------------
    public void test_withYears() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withYears(10), 10, 2, 3, 4, 5, 6, 7);
    }

    public void test_withYears_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withYears(1), test);
    }

    public void test_withYears_toZero() {
        ISOPeriod test = ISOPeriod.of(1, YEARS);
        assertSame(test.withYears(0), ISOPeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // withMonths()
    //-----------------------------------------------------------------------
    public void test_withMonths() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withMonths(10), 1, 10, 3, 4, 5, 6, 7);
    }

    public void test_withMonths_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withMonths(2), test);
    }

    public void test_withMonths_toZero() {
        ISOPeriod test = ISOPeriod.of(1, MONTHS);
        assertSame(test.withMonths(0), ISOPeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // withDays()
    //-----------------------------------------------------------------------
    public void test_withDays() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withDays(10), 1, 2, 10, 4, 5, 6, 7);
    }

    public void test_withDays_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withDays(3), test);
    }

    public void test_withDays_toZero() {
        ISOPeriod test = ISOPeriod.of(1, DAYS);
        assertSame(test.withDays(0), ISOPeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // withHours()
    //-----------------------------------------------------------------------
    public void test_withHours() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withHours(10), 1, 2, 3, 10, 5, 6, 7);
    }

    public void test_withHours_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withHours(4), test);
    }

    public void test_withHours_toZero() {
        ISOPeriod test = ISOPeriod.of(1, HOURS);
        assertSame(test.withHours(0), ISOPeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // withMinutes()
    //-----------------------------------------------------------------------
    public void test_withMinutes() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withMinutes(10), 1, 2, 3, 4, 10, 6, 7);
    }

    public void test_withMinutes_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withMinutes(5), test);
    }

    public void test_withMinutes_toZero() {
        ISOPeriod test = ISOPeriod.of(1, MINUTES);
        assertSame(test.withMinutes(0), ISOPeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // withSeconds()
    //-----------------------------------------------------------------------
    public void test_withSeconds() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withSeconds(10), 1, 2, 3, 4, 5, 10, 7);
    }

    public void test_withSeconds_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withSeconds(6), test);
    }

    public void test_withSeconds_toZero() {
        ISOPeriod test = ISOPeriod.of(1, SECONDS);
        assertSame(test.withSeconds(0), ISOPeriod.ZERO);
    }

    //-----------------------------------------------------------------------
    // withNanos()
    //-----------------------------------------------------------------------
    public void test_withNanos() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.withNanos(10), 1, 2, 3, 4, 5, 6, 10);
    }

    public void test_withNanos_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.withNanos(7), test);
    }

    public void test_withNanos_toZero() {
        ISOPeriod test = ISOPeriod.of(1, NANOS);
        assertSame(test.withNanos(0), ISOPeriod.ZERO);
    }



    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, YEARS), 11, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(ISOPeriod.of(10, YEARS)), 11, 2, 3, 4, 5, 6, 7);
    }

    public void test_plusYears_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, YEARS), test);
        assertPeriod(test.plus(ISOPeriod.of(0, YEARS)), 1, 2, 3, 4, 5, 6, 7);
    }

    public void test_plusYears_toZero() {
        ISOPeriod test = ISOPeriod.of(-1, YEARS);
        assertSame(test.plus(1, YEARS), ISOPeriod.ZERO);
        assertSame(test.plus(ISOPeriod.of(1, YEARS)), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusYears_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, YEARS);
        test.plus(1, YEARS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusYears_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, YEARS);
        test.plus(-1, YEARS);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, MONTHS), 1, 12, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(ISOPeriod.of(10, MONTHS)), 1, 12, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(ISOPeriod.of(SingleUnitPeriod.of(10, MONTHS))), 1, 12, 3, 4, 5, 6, 7);
    }

    public void test_plusMonths_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, MONTHS), test);
        assertEquals(test.plus(ISOPeriod.of(0, MONTHS)), test);
        assertEquals(test.plus(ISOPeriod.of(SingleUnitPeriod.of(0, MONTHS))), test);
    }

    public void test_plusMonths_toZero() {
        ISOPeriod test = ISOPeriod.of(-1, MONTHS);
        assertSame(test.plus(1, MONTHS), ISOPeriod.ZERO);
        assertSame(test.plus(ISOPeriod.of(1, MONTHS)), ISOPeriod.ZERO);
        assertSame(test.plus(ISOPeriod.of(SingleUnitPeriod.of(1, MONTHS))), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusMonths_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, MONTHS);
        test.plus(1, MONTHS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusMonths_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, MONTHS);
        test.plus(-1, MONTHS);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    public void test_plusDays() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, DAYS), 1, 2, 13, 4, 5, 6, 7);
    }

    public void test_plusDays_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, DAYS), test);
    }

    public void test_plusDays_toZero() {
        ISOPeriod test = ISOPeriod.of(-1, DAYS);
        assertSame(test.plus(1, DAYS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, DAYS);
        test.plus(1, DAYS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusDays_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, DAYS);
        test.plus(-1, DAYS);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    public void test_plusHours() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, HOURS), 1, 2, 3, 14, 5, 6, 7);
    }

    public void test_plusHours_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, HOURS), test);
    }

    public void test_plusHours_toZero() {
        ISOPeriod test = ISOPeriod.of(-1, HOURS);
        assertSame(test.plus(1, HOURS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusHours_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, HOURS);
        test.plus(1, HOURS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusHours_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, HOURS);
        test.plus(-1, HOURS);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    public void test_plusMinutes() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, MINUTES), 1, 2, 3, 4, 15, 6, 7);
    }

    public void test_plusMinutes_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, MINUTES), test);
    }

    public void test_plusMinutes_toZero() {
        ISOPeriod test = ISOPeriod.of(-1, MINUTES);
        assertSame(test.plus(1, MINUTES), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusMinutes_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, MINUTES);
        test.plus(1, MINUTES);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusMinutes_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, MINUTES);
        test.plus(-1, MINUTES);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    public void test_plusSeconds() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, SECONDS), 1, 2, 3, 4, 5, 16, 7);
    }

    public void test_plusSeconds_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, SECONDS), test);
    }

    public void test_plusSeconds_toZero() {
        ISOPeriod test = ISOPeriod.of(-1, SECONDS);
        assertSame(test.plus(1, SECONDS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusSeconds_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, SECONDS);
        test.plus(1, SECONDS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusSeconds_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, SECONDS);
        test.plus(-1, SECONDS);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    public void test_plusNanos() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.plus(10, NANOS), 1, 2, 3, 4, 5, 6, 17);
    }

    public void test_plusNanos_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.plus(0, NANOS), test);
    }

    public void test_plusNanos_toZero() {
        ISOPeriod test = ISOPeriod.of(-1, NANOS);
        assertSame(test.plus(1, NANOS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusNanos_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Long.MAX_VALUE, NANOS);
        test.plus(1, NANOS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_plusNanos_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Long.MIN_VALUE, NANOS);
        test.plus(-1, NANOS);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    public void test_minusYears() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, YEARS), -9, 2, 3, 4, 5, 6, 7);
    }

    public void test_minusYears_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, YEARS), test);
    }

    public void test_minusYears_toZero() {
        ISOPeriod test = ISOPeriod.of(1, YEARS);
        assertSame(test.minus(1, YEARS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusYears_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, YEARS);
        test.minus(-1, YEARS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusYears_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, YEARS);
        test.minus(1, YEARS);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    public void test_minusMonths() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, MONTHS), 1, -8, 3, 4, 5, 6, 7);
    }

    public void test_minusMonths_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, MONTHS), test);
    }

    public void test_minusMonths_toZero() {
        ISOPeriod test = ISOPeriod.of(1, MONTHS);
        assertSame(test.minus(1, MONTHS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusMonths_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, MONTHS);
        test.minus(-1, MONTHS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusMonths_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, MONTHS);
        test.minus(1, MONTHS);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    public void test_minusDays() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, DAYS), 1, 2, -7, 4, 5, 6, 7);
    }

    public void test_minusDays_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, DAYS), test);
    }

    public void test_minusDays_toZero() {
        ISOPeriod test = ISOPeriod.of(1, DAYS);
        assertSame(test.minus(1, DAYS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, DAYS);
        test.minus(-1, DAYS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusDays_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, DAYS);
        test.minus(1, DAYS);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    public void test_minusHours() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, HOURS), 1, 2, 3, -6, 5, 6, 7);
    }

    public void test_minusHours_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, HOURS), test);
    }

    public void test_minusHours_toZero() {
        ISOPeriod test = ISOPeriod.of(1, HOURS);
        assertSame(test.minus(1, HOURS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusHours_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, HOURS);
        test.minus(-1, HOURS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusHours_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, HOURS);
        test.minus(1, HOURS);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    public void test_minusMinutes() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, MINUTES), 1, 2, 3, 4, -5, 6, 7);
    }

    public void test_minusMinutes_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, MINUTES), test);
    }

    public void test_minusMinutes_toZero() {
        ISOPeriod test = ISOPeriod.of(1, MINUTES);
        assertSame(test.minus(1, MINUTES), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusMinutes_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, MINUTES);
        test.minus(-1, MINUTES);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusMinutes_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, MINUTES);
        test.minus(1, MINUTES);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    public void test_minusSeconds() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, SECONDS), 1, 2, 3, 4, 5, -4, 7);
    }

    public void test_minusSeconds_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, SECONDS), test);
    }

    public void test_minusSeconds_toZero() {
        ISOPeriod test = ISOPeriod.of(1, SECONDS);
        assertSame(test.minus(1, SECONDS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusSeconds_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE, SECONDS);
        test.minus(-1, SECONDS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusSeconds_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE, SECONDS);
        test.minus(1, SECONDS);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    public void test_minusNanos() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.minus(10, NANOS), 1, 2, 3, 4, 5, 6, -3);
    }

    public void test_minusNanos_noChange() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.minus(0, NANOS), test);
    }

    public void test_minusNanos_toZero() {
        ISOPeriod test = ISOPeriod.of(1, NANOS);
        assertSame(test.minus(1, NANOS), ISOPeriod.ZERO);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusNanos_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Long.MAX_VALUE, NANOS);
        test.minus(-1, NANOS);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_minusNanos_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Long.MIN_VALUE, NANOS);
        test.minus(1, NANOS);
    }

    //-----------------------------------------------------------------------
    // multipliedBy()
    //-----------------------------------------------------------------------
    public void test_multipliedBy() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.multipliedBy(2), 2, 4, 6, 8, 10, 12, 14);
        assertPeriod(test.multipliedBy(-3), -3, -6, -9, -12, -15, -18, -21);
    }

    public void test_multipliedBy_zeroBase() {
        assertSame(ISOPeriod.ZERO.multipliedBy(2), ISOPeriod.ZERO);
    }

    public void test_multipliedBy_zero() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.multipliedBy(0), ISOPeriod.ZERO);
    }

    public void test_multipliedBy_one() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertSame(test.multipliedBy(1), test);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooBig() {
        ISOPeriod test = ISOPeriod.of(Integer.MAX_VALUE / 2 + 1, YEARS);
        test.multipliedBy(2);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_multipliedBy_overflowTooSmall() {
        ISOPeriod test = ISOPeriod.of(Integer.MIN_VALUE / 2 - 1, YEARS);
        test.multipliedBy(2);
    }

    //-----------------------------------------------------------------------
    // dividedBy()
    //-----------------------------------------------------------------------
    public void test_dividedBy() {
        ISOPeriod test = ISOPeriod.of(12, 12, 12, 12, 12, 11, 9);
        assertSame(ISOPeriod.ZERO.dividedBy(2), ISOPeriod.ZERO);
        assertSame(test.dividedBy(1), test);
        assertPeriod(test.dividedBy(2), 6, 6, 6, 6, 6, 5, 4);
        assertPeriod(test.dividedBy(-3), -4, -4, -4, -4, -4, -3, -3);
    }

    public void test_dividedBy_zeroBase() {
        assertSame(ISOPeriod.ZERO.dividedBy(2), ISOPeriod.ZERO);
    }

    public void test_dividedBy_one() {
        ISOPeriod test = ISOPeriod.of(12, 12, 12, 12, 12, 11, 11);
        assertSame(test.dividedBy(1), test);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_dividedBy_divideByZero() {
        ISOPeriod test = ISOPeriod.of(12, 12, 12, 12, 12, 12, 12);
        test.dividedBy(0);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_dividedBy_zeroBase_divideByZero() {
        ISOPeriod.ZERO.dividedBy(0);
    }

    //-----------------------------------------------------------------------
    // negated()
    //-----------------------------------------------------------------------
    public void test_negated() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6, 7);
        assertPeriod(test.negated(), -1, -2, -3, -4, -5, -6, -7);
    }

    public void test_negated_zero() {
        assertSame(ISOPeriod.ZERO.negated(), ISOPeriod.ZERO);
    }

    public void test_negated_max() {
        assertPeriod(ISOPeriod.of(Integer.MAX_VALUE, YEARS).negated(), -Integer.MAX_VALUE, 0, 0, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_negated_overflow() {
        ISOPeriod.of(Integer.MIN_VALUE, YEARS).negated();
    }

    //-----------------------------------------------------------------------
    // normalized()
    //-----------------------------------------------------------------------
    public void test_normalized() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 6, 7).normalized(), 1, 2, 3, 4, 5, 6, 7);
    }

    public void test_normalized_months() {
        assertPeriod(ISOPeriod.of(1, 11, 3, 4, 5, 6).normalized(), 1, 11, 3, 4, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 12, 3, 4, 5, 6).normalized(), 2, 0, 3, 4, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 23, 3, 4, 5, 6).normalized(), 2, 11, 3, 4, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 24, 3, 4, 5, 6).normalized(), 3, 0, 3, 4, 5, 6, 0);
        
        assertPeriod(ISOPeriod.of(3, -2, 3, 4, 5, 6).normalized(), 2, 10, 3, 4, 5, 6, 0);
    }

    public void test_normalized_nanos() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 6, 999999999).normalized(), 1, 2, 3, 4, 5, 6, 999999999);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 6, 1000000000).normalized(), 1, 2, 3, 4, 5, 7, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 59, 999999999).normalized(), 1, 2, 3, 4, 5, 59, 999999999);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 59, 1000000000).normalized(), 1, 2, 3, 4, 6, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 59, 999999999).normalized(), 1, 2, 3, 4, 59, 59, 999999999);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 59, 1000000000).normalized(), 1, 2, 3, 5, 0, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 59, 999999999).normalized(), 1, 2, 3, 23, 59, 59, 999999999);
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 59, 1000000000).normalized(), 1, 2, 3, 24, 0, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 0, 1, 0, -1).normalized(), 1, 2, 3, 0, 0, 59, 999999999);
    }

    public void test_normalized_seconds() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 59).normalized(), 1, 2, 3, 4, 5, 59, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 60).normalized(), 1, 2, 3, 4, 6, 0, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 119).normalized(), 1, 2, 3, 4, 6, 59, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 120).normalized(), 1, 2, 3, 4, 7, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 59).normalized(), 1, 2, 3, 4, 59, 59, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 60).normalized(), 1, 2, 3, 5, 0, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 59).normalized(), 1, 2, 3, 23, 59, 59, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 60).normalized(), 1, 2, 3, 24, 0, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 0, 0, -1).normalized(), 1, 2, 3, -1, 59, 59, 0);
    }

    public void test_normalized_minutes() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 6).normalized(), 1, 2, 3, 4, 59, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 60, 6).normalized(), 1, 2, 3, 5, 0, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 119, 6).normalized(), 1, 2, 3, 5, 59, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 120, 6).normalized(), 1, 2, 3, 6, 0, 6, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 6).normalized(), 1, 2, 3, 23, 59, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 60, 6).normalized(), 1, 2, 3, 24, 0, 6, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, -1, 0).normalized(), 1, 2, 3, 22, 59, 0, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, -1, 60).normalized(), 1, 2, 3, 23, 0, 0, 0);
    }

    public void test_normalized_hours() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 5, 6).normalized(), 1, 2, 3, 23, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 24, 5, 6).normalized(), 1, 2, 3, 24, 5, 6, 0);
    }

    public void test_normalized_zero() {
        assertSame(ISOPeriod.ZERO.normalized(), ISOPeriod.ZERO);
    }

    public void test_normalized_bigNanos() {
        ISOPeriod big = ISOPeriod.of(1, 2, 3, 4, 5, 6, Long.MAX_VALUE);
        long total = Long.MAX_VALUE;
        long nanos = total % 1000000000L;
        total = total / 1000000000L + 6;
        int secs = (int) (total % 60);
        total = total / 60 + 5;
        int mins = (int) (total % 60);
        total = total / 60 + 4;
        if (total > Integer.MAX_VALUE) {
            throw new AssertionError("Bad test");
        }
        int hours = (int) total;
        assertPeriod(big.normalized(), 1, 2, 3, hours, mins, secs, nanos);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_normalized_max() {
        ISOPeriod base = ISOPeriod.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        base.normalized();
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_normalized_maxTime() {
        ISOPeriod base = ISOPeriod.of(0, 0, Integer.MAX_VALUE,
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        base.normalized();
    }

    //-----------------------------------------------------------------------
    // normalizedWith24HourDays()
    //-----------------------------------------------------------------------
    public void test_normalizedWith24HourDays() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 6).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 6, 0);
        
        assertPeriod(ISOPeriod.of(3, -2, 3, 4, 5, 6).normalizedWith24HourDays(), 2, 10, 3, 4, 5, 6, 0);
    }

    public void test_normalizedWith24HourDays_months() {
        assertPeriod(ISOPeriod.of(1, 11, 3, 4, 5, 6).normalizedWith24HourDays(), 1, 11, 3, 4, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 12, 3, 4, 5, 6).normalizedWith24HourDays(), 2, 0, 3, 4, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 23, 3, 4, 5, 6).normalizedWith24HourDays(), 2, 11, 3, 4, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 24, 3, 4, 5, 6).normalizedWith24HourDays(), 3, 0, 3, 4, 5, 6, 0);
    }

    public void test_normalizedWith24HourDays_nanos() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 6, 999999999).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 6, 999999999);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 6, 1000000000).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 7, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 59, 999999999).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 59, 999999999);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 59, 1000000000).normalizedWith24HourDays(), 1, 2, 3, 4, 6, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 59, 999999999).normalizedWith24HourDays(), 1, 2, 3, 4, 59, 59, 999999999);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 59, 1000000000).normalizedWith24HourDays(), 1, 2, 3, 5, 0, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 59, 999999999).normalizedWith24HourDays(), 1, 2, 3, 23, 59, 59, 999999999);
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 59, 1000000000).normalizedWith24HourDays(), 1, 2, 4, 0, 0, 0, 0);
    }

    public void test_normalizedWith24HourDays_seconds() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 59).normalizedWith24HourDays(), 1, 2, 3, 4, 5, 59, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 60).normalizedWith24HourDays(), 1, 2, 3, 4, 6, 0, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 119).normalizedWith24HourDays(), 1, 2, 3, 4, 6, 59, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 5, 120).normalizedWith24HourDays(), 1, 2, 3, 4, 7, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 59).normalizedWith24HourDays(), 1, 2, 3, 4, 59, 59, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 60).normalizedWith24HourDays(), 1, 2, 3, 5, 0, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 59).normalizedWith24HourDays(), 1, 2, 3, 23, 59, 59, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 60).normalizedWith24HourDays(), 1, 2, 4, 0, 0, 0, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 0, 0, -1).normalizedWith24HourDays(), 1, 2, 2, 23, 59, 59, 0);
    }

    public void test_normalizedWith24HourDays_minutes() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 59, 6).normalizedWith24HourDays(), 1, 2, 3, 4, 59, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 60, 6).normalizedWith24HourDays(), 1, 2, 3, 5, 0, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 119, 6).normalizedWith24HourDays(), 1, 2, 3, 5, 59, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 4, 120, 6).normalizedWith24HourDays(), 1, 2, 3, 6, 0, 6, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 59, 6).normalizedWith24HourDays(), 1, 2, 3, 23, 59, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 60, 6).normalizedWith24HourDays(), 1, 2, 4, 0, 0, 6, 0);
        
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, -1, 0).normalizedWith24HourDays(), 1, 2, 3, 22, 59, 0, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, -1, 60).normalizedWith24HourDays(), 1, 2, 3, 23, 0, 0, 0);
    }

    public void test_normalizedWith24HourDays_hours() {
        assertPeriod(ISOPeriod.of(1, 2, 3, 23, 5, 6).normalizedWith24HourDays(), 1, 2, 3, 23, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 24, 5, 6).normalizedWith24HourDays(), 1, 2, 4, 0, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 47, 5, 6).normalizedWith24HourDays(), 1, 2, 4, 23, 5, 6, 0);
        assertPeriod(ISOPeriod.of(1, 2, 3, 48, 5, 6).normalizedWith24HourDays(), 1, 2, 5, 0, 5, 6, 0);
    }

    public void test_normalizedWith24HourDays_zero() {
        assertSame(ISOPeriod.ZERO.normalizedWith24HourDays(), ISOPeriod.ZERO);
    }

    public void test_normalizedWith24HourDays_bigNanos() {
        ISOPeriod big = ISOPeriod.of(1, 2, 3, 4, 5, 6, Long.MAX_VALUE);
        long total = Long.MAX_VALUE;
        long nanos = total % 1000000000L;
        total = total / 1000000000L + 6;
        int secs = (int) (total % 60);
        total = total / 60 + 5;
        int mins = (int) (total % 60);
        total = total / 60 + 4;
        int hours = (int) (total % 24);
        total = total / 24 + 3;
        if (total > Integer.MAX_VALUE) {
            throw new AssertionError("Bad test");
        }
        int days = (int) total;
        assertPeriod(big.normalizedWith24HourDays(), 1, 2, days, hours, mins, secs, nanos);
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_normalizedWith24HourDays_max() {
        ISOPeriod base = ISOPeriod.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        base.normalizedWith24HourDays();
    }

    @Test(expectedExceptions=ArithmeticException.class)
    public void test_normalizedWith24HourDays_maxTime() {
        ISOPeriod base = ISOPeriod.of(0, 0, Integer.MAX_VALUE,
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        base.normalizedWith24HourDays();
    }


    //-----------------------------------------------------------------------
    // toDuration()
    //-----------------------------------------------------------------------
    public void test_toEstimatedDuration() {
        assertEquals(ISOPeriod.ZERO.toDuration(), Duration.of(0, SECONDS));
        assertEquals(ISOPeriod.of(0, 0, 0, 4, 5, 6, 7).toDuration(), Duration.ofSeconds((4 * 60 + 5) * 60L + 6, 7));
        assertEquals(ISOPeriod.of(0, 0, 0, -4, -5, -6, -7).toDuration(), Duration.ofSeconds((-4 * 60 - 5) * 60L - 6, -7));
    }

    public void test_toEstimatedDuration_Days() {
        assertEquals(ISOPeriod.ZERO.toDuration(), Duration.of(0, SECONDS));
        assertEquals(ISOPeriod.of(2, DAYS).toDuration(), LocalPeriodUnit.DAYS.getDuration().multipliedBy(2));
    }

    //-----------------------------------------------------------------------
    // toDuration()
    //-----------------------------------------------------------------------
    public void test_toDuration() {
        assertEquals(ISOPeriod.ZERO.toDuration(), Duration.of(0, SECONDS));
        assertEquals(ISOPeriod.of(0, 0, 0, 4, 5, 6, 7).toDuration(), Duration.ofSeconds((4 * 60 + 5) * 60L + 6, 7));
    }

    public void test_toDuration_calculation() {
        assertEquals(ISOPeriod.of(0, 0, 2, 0, 0, 0, 0).toDuration(), Duration.ofSeconds(2 * 24 * 3600));
        assertEquals(ISOPeriod.of(0, 0, 0, 2, 0, 0, 0).toDuration(), Duration.ofSeconds(2 * 3600));
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 2, 0, 0).toDuration(), Duration.of(120, SECONDS));
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 2, 0).toDuration(), Duration.of(2, SECONDS));
        
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 3, 1000000000L - 1).toDuration(), Duration.ofSeconds(3, 999999999));
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 3, 1000000000L).toDuration(), Duration.ofSeconds(4, 0));
    }

    public void test_toDuration_negatives() {
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 2, 1).toDuration(), Duration.ofSeconds(2, 1));
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 2, -1).toDuration(), Duration.ofSeconds(1, 999999999));
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, -2, 1).toDuration(), Duration.ofSeconds(-2, 1));
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, -2, -1).toDuration(), Duration.ofSeconds(-3, 999999999));
    }

    public void test_toDuration_big() {
        BigInteger calc = BigInteger.valueOf(Long.MAX_VALUE).divide(BINT_1BN)
                            .add(MAX_BINT)
                            .add(MAX_BINT.multiply(BINT_60).add(MAX_BINT).multiply(BINT_60));
        long s = new BigDecimal(calc).longValueExact();
        calc = BigInteger.valueOf(Long.MAX_VALUE).remainder(BINT_1BN);
        int n = new BigDecimal(calc).intValueExact();
        ISOPeriod test = ISOPeriod.of(0, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        assertEquals(test.toDuration(), Duration.ofSeconds(s, n));

        calc = BigInteger.valueOf(Long.MAX_VALUE).divide(BINT_1BN)
                            .add(MAX_BINT)
                            .add(MAX_BINT.multiply(BINT_24).add(MAX_BINT).multiply(BINT_60).add(MAX_BINT).multiply(BINT_60));
        s = new BigDecimal(calc).longValueExact();
        calc = BigInteger.valueOf(Long.MAX_VALUE).remainder(BINT_1BN);
        n = new BigDecimal(calc).intValueExact();
        test = ISOPeriod.of(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE);
        assertEquals(test.toDuration(), Duration.ofSeconds(s, n));
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_toDuration_years() {
        ISOPeriod.of(1, 0, 0, 4, 5, 6, 7).toDuration();
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_toDuration_months() {
        ISOPeriod.of(0, 1, 0, 4, 5, 6, 7).toDuration();
    }

    @Test()
    public void test_toDuration_days() {
        Duration test = ISOPeriod.of(0, 0, 1, 4, 5, 6, 7).toDuration();
        assertEquals(test, Duration.ofSeconds(101106, 7L));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        assertEquals(ISOPeriod.of(1, 0, 0, 0, 0, 0).equals(ISOPeriod.of(1, YEARS)), true);
        assertEquals(ISOPeriod.of(0, 1, 0, 0, 0, 0).equals(ISOPeriod.of(1, MONTHS)), true);
        assertEquals(ISOPeriod.of(0, 0, 1, 0, 0, 0).equals(ISOPeriod.of(1, DAYS)), true);
        assertEquals(ISOPeriod.of(0, 0, 0, 1, 0, 0).equals(ISOPeriod.of(1, HOURS)), true);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 1, 0).equals(ISOPeriod.of(1, MINUTES)), true);
        assertEquals(ISOPeriod.of(0, 0, 0, 0, 0, 1).equals(ISOPeriod.of(1, SECONDS)), true);
        assertEquals(ISOPeriod.of(1, 2, 3, 0, 0, 0).equals(ISOPeriod.ofDate(1, 2, 3)), true);
        assertEquals(ISOPeriod.of(0, 0, 0, 1, 2, 3).equals(ISOPeriod.ofTime(1, 2, 3)), true);
        assertEquals(ISOPeriod.of(1, 2, 3, 4, 5, 6).equals(ISOPeriod.of(1, 2, 3, 4, 5, 6)), true);
        
        assertEquals(ISOPeriod.of(1, YEARS).equals(ISOPeriod.of(1, YEARS)), true);
        assertEquals(ISOPeriod.of(1, YEARS).equals(ISOPeriod.of(2, YEARS)), false);
        
        assertEquals(ISOPeriod.of(1, MONTHS).equals(ISOPeriod.of(1, MONTHS)), true);
        assertEquals(ISOPeriod.of(1, MONTHS).equals(ISOPeriod.of(2, MONTHS)), false);
        
        assertEquals(ISOPeriod.of(1, DAYS).equals(ISOPeriod.of(1, DAYS)), true);
        assertEquals(ISOPeriod.of(1, DAYS).equals(ISOPeriod.of(2, DAYS)), false);
        
        assertEquals(ISOPeriod.of(1, HOURS).equals(ISOPeriod.of(1, HOURS)), true);
        assertEquals(ISOPeriod.of(1, HOURS).equals(ISOPeriod.of(2, HOURS)), false);
        
        assertEquals(ISOPeriod.of(1, MINUTES).equals(ISOPeriod.of(1, MINUTES)), true);
        assertEquals(ISOPeriod.of(1, MINUTES).equals(ISOPeriod.of(2, MINUTES)), false);
        
        assertEquals(ISOPeriod.of(1, SECONDS).equals(ISOPeriod.of(1, SECONDS)), true);
        assertEquals(ISOPeriod.of(1, SECONDS).equals(ISOPeriod.of(2, SECONDS)), false);
        
        assertEquals(ISOPeriod.ofDate(1, 2, 3).equals(ISOPeriod.ofDate(1, 2, 3)), true);
        assertEquals(ISOPeriod.ofDate(1, 2, 3).equals(ISOPeriod.ofDate(0, 2, 3)), false);
        assertEquals(ISOPeriod.ofDate(1, 2, 3).equals(ISOPeriod.ofDate(1, 0, 3)), false);
        assertEquals(ISOPeriod.ofDate(1, 2, 3).equals(ISOPeriod.ofDate(1, 2, 0)), false);
        
        assertEquals(ISOPeriod.ofTime(1, 2, 3).equals(ISOPeriod.ofTime(1, 2, 3)), true);
        assertEquals(ISOPeriod.ofTime(1, 2, 3).equals(ISOPeriod.ofTime(0, 2, 3)), false);
        assertEquals(ISOPeriod.ofTime(1, 2, 3).equals(ISOPeriod.ofTime(1, 0, 3)), false);
        assertEquals(ISOPeriod.ofTime(1, 2, 3).equals(ISOPeriod.ofTime(1, 2, 0)), false);
    }

    public void test_equals_self() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6);
        assertEquals(test.equals(test), true);
    }

    public void test_equals_null() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6);
        assertEquals(test.equals(null), false);
    }

    public void test_equals_otherClass() {
        ISOPeriod test = ISOPeriod.of(1, 2, 3, 4, 5, 6);
        assertEquals(test.equals(""), false);
    }

    //-----------------------------------------------------------------------
    public void test_hashCode() {
        ISOPeriod test5 = ISOPeriod.of(5, DAYS);
        ISOPeriod test6 = ISOPeriod.of(6, DAYS);
        assertEquals(test5.hashCode() == test5.hashCode(), true);
        assertEquals(test5.hashCode() == test6.hashCode(), false);
    }

    public void test_hashCode_unique_workaroundSlowTest() {
        // spec requires unique hash codes
        // years 0-31, months 0-11, days 0-31, hours 0-23, minutes 0-59, seconds 0-59
        
        // -37 nanos removes the effect of the nanos
        int yearsBits = 0;
        for (int i = 0; i <= 31; i++) {
            ISOPeriod test = ISOPeriod.of(i, YEARS).withNanos(-37);
            yearsBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(yearsBits), 5);
        int monthsBits = 0;
        for (int i = 0; i <= 11; i++) {
            ISOPeriod test = ISOPeriod.of(i, MONTHS).withNanos(-37);
            monthsBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(monthsBits), 4);
        assertEquals(yearsBits & monthsBits, 0);
        int daysBits = 0;
        for (int i = 0; i <= 31; i++) {
            ISOPeriod test = ISOPeriod.of(i, DAYS).withNanos(-37);
            daysBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(daysBits), 5);
        assertEquals(yearsBits & monthsBits & daysBits, 0);
        int hoursBits = 0;
        for (int i = 0; i <= 23; i++) {
            ISOPeriod test = ISOPeriod.of(i, HOURS).withNanos(-37);
            hoursBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(hoursBits), 5);
        assertEquals(yearsBits & monthsBits & daysBits & hoursBits, 0);
        int minutesBits = 0;
        for (int i = 0; i <= 59; i++) {
            ISOPeriod test = ISOPeriod.of(i, MINUTES).withNanos(-37);
            minutesBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(minutesBits), 6);
        assertEquals(yearsBits & minutesBits & daysBits & hoursBits & minutesBits, 0);
        int secondsBits = 0;
        for (int i = 0; i <= 59; i++) {
            ISOPeriod test = ISOPeriod.of(i, SECONDS).withNanos(-37);
            secondsBits |= test.hashCode();
        }
        assertEquals(Integer.bitCount(secondsBits), 6);
        assertEquals(yearsBits & secondsBits & daysBits & hoursBits & minutesBits & secondsBits, 0);
        
        // make common overflows not same hash code
        assertTrue(ISOPeriod.of(16, MONTHS).hashCode() != ISOPeriod.of(1, YEARS).hashCode());
        assertTrue(ISOPeriod.of(32, DAYS).hashCode() != ISOPeriod.of(1, MONTHS).hashCode());
        assertTrue(ISOPeriod.of(32, HOURS).hashCode() != ISOPeriod.of(1, DAYS).hashCode());
        assertTrue(ISOPeriod.of(64, MINUTES).hashCode() != ISOPeriod.of(1, HOURS).hashCode());
        assertTrue(ISOPeriod.of(64, SECONDS).hashCode() != ISOPeriod.of(1, MINUTES).hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toStringAndParse")
    Object[][] data_toString() {
        return new Object[][] {
            {ISOPeriod.ZERO, "PT0S"},
            {ISOPeriod.of(0, DAYS), "PT0S"},
            {ISOPeriod.of(1, YEARS), "P1Y"},
            {ISOPeriod.of(1, MONTHS), "P1M"},
            {ISOPeriod.of(1, DAYS), "P1D"},
            {ISOPeriod.of(1, HOURS), "PT1H"},
            {ISOPeriod.of(1, MINUTES), "PT1M"},
            {ISOPeriod.of(1, SECONDS), "PT1S"},
            {ISOPeriod.of(1, 2, 3, 4, 5, 6), "P1Y2M3DT4H5M6S"},
            {ISOPeriod.of(1, 2, 3, 4, 5, 6, 700000000), "P1Y2M3DT4H5M6.7S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, 100000000), "PT0.1S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, -100000000), "PT-0.1S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 1, -900000000), "PT0.1S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, -1, 900000000), "PT-0.1S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 1, 100000000), "PT1.1S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 1, -100000000), "PT0.9S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, -1, 100000000), "PT-0.9S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, -1, -100000000), "PT-1.1S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, 10000000), "PT0.01S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, -10000000), "PT-0.01S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, 1000000), "PT0.001S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, -1000000), "PT-0.001S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, 1000), "PT0.000001S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, -1000), "PT-0.000001S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, 1), "PT0.000000001S"},
            {ISOPeriod.of(0, 0, 0, 0, 0, 0, -1), "PT-0.000000001S"},
        };
    }

    //-----------------------------------------------------------------------
    private void assertPeriod(ISOPeriod test, int y, int mo, int d, int h, int mn, int s, long n) {
        assertEquals(test.getYears(), y, "years");
        assertEquals(test.getMonths(), mo, "months");
        assertEquals(test.getDays(), d, "days");
        assertEquals(test.getHours(), h, "hours");
        assertEquals(test.getMinutes(), mn, "mins");
        assertEquals(test.getSeconds(), s, "secs");
        assertEquals(test.getNanos(), n, "nanos");
    }

}
