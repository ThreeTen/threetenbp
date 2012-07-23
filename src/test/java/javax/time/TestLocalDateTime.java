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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Iterator;

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.MockFieldNoValue;
import javax.time.calendrical.MockZoneResolverReturnsNull;
import javax.time.calendrical.PeriodUnit;
import javax.time.calendrical.Year;
import javax.time.format.CalendricalFormatter;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalDateTime.
 */
@Test
public class TestLocalDateTime extends AbstractTest {

    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId ZONE_GAZA = ZoneId.of("Asia/Gaza");

    private LocalDateTime TEST_2007_07_15_12_30_40_987654321 = LocalDateTime.of(2007, 7, 15, 12, 30, 40, 987654321);
    private LocalDateTime MAX_DATE_TIME;
    private LocalDateTime MIN_DATE_TIME;
    private Instant MAX_INSTANT;
    private Instant MIN_INSTANT;

    @BeforeMethod(groups={"implementation","tck"})
    public void setUp() {
        MAX_DATE_TIME = LocalDateTime.MAX_DATE_TIME;
        MIN_DATE_TIME = LocalDateTime.MIN_DATE_TIME;
        MAX_INSTANT = MAX_DATE_TIME.atOffset(ZoneOffset.UTC).toInstant();
        MIN_INSTANT = MIN_DATE_TIME.atOffset(ZoneOffset.UTC).toInstant();
    }


    //-----------------------------------------------------------------------
    private void check(LocalDateTime dateTime, int y, int m, int d, int h, int mi, int s, int n) {
        assertEquals(dateTime.getYear(), y);
        assertEquals(dateTime.getMonth().getValue(), m);
        assertEquals(dateTime.getDayOfMonth(), d);
        assertEquals(dateTime.getHour(), h);
        assertEquals(dateTime.getMinute(), mi);
        assertEquals(dateTime.getSecond(), s);
        assertEquals(dateTime.getNano(), n);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        Object obj = TEST_2007_07_15_12_30_40_987654321;
        assertTrue(obj instanceof DateTime);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    @Test(groups={"tck"})
    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_2007_07_15_12_30_40_987654321);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_immutable() {
        Class<LocalDateTime> cls = LocalDateTime.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().contains("$") == false) {
                if (Modifier.isStatic(field.getModifiers())) {
                    assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
                } else {
                    assertTrue(Modifier.isPrivate(field.getModifiers()), "Field:" + field.getName());
                    assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void constant_MIN_DATE_TIME() {
        check(LocalDateTime.MIN_DATE_TIME, Year.MIN_YEAR, 1, 1, 0, 0, 0, 0);
    }

    @Test(groups={"implementation"})
    public void constant_MAX_DATE_TIME() {
        check(LocalDateTime.MAX_DATE_TIME, Year.MAX_YEAR, 12, 31,  23, 59, 59, 999999999);
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @Test(timeOut=30000, groups={"tck"})  // TODO: remove when time zone loading is faster
    public void now() {
        LocalDateTime expected = LocalDateTime.now(Clock.systemDefaultZone());
        LocalDateTime test = LocalDateTime.now();
        long diff = Math.abs(test.toLocalTime().toNanoOfDay() - expected.toLocalTime().toNanoOfDay());
        if (diff >= 100000000) {
            // may be date change
            expected = LocalDateTime.now(Clock.systemDefaultZone());
            test = LocalDateTime.now();
            diff = Math.abs(test.toLocalTime().toNanoOfDay() - expected.toLocalTime().toNanoOfDay());
        }
        assertTrue(diff < 100000000);  // less than 0.1 secs
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void now_Clock_nullClock() {
        LocalDateTime.now(null);
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i).plusNanos(123456789L);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            LocalDateTime test = LocalDateTime.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonth(), Month.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60 ? 1 : 2));
            assertEquals(test.getHour(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinute(), (i / 60) % 60);
            assertEquals(test.getSecond(), i % 60);
            assertEquals(test.getNano(), 123456789);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_offset() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i).plusNanos(123456789L);
            Clock clock = Clock.fixed(instant.minusSeconds(OFFSET_PONE.getTotalSeconds()), ZoneId.of(OFFSET_PONE));
            LocalDateTime test = LocalDateTime.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonth(), Month.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60) ? 1 : 2);
            assertEquals(test.getHour(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinute(), (i / 60) % 60);
            assertEquals(test.getSecond(), i % 60);
            assertEquals(test.getNano(), 123456789);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_beforeEpoch() {
        LocalTime expected = LocalTime.MIDNIGHT.plusNanos(123456789L);
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i).plusNanos(123456789L);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            LocalDateTime test = LocalDateTime.now(clock);
            assertEquals(test.getYear(), 1969);
            assertEquals(test.getMonth(), Month.DECEMBER);
            assertEquals(test.getDayOfMonth(), 31);
            expected = expected.minusSeconds(1);
            assertEquals(test.toLocalTime(), expected);
        }
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void now_Clock_maxYear() {
        Clock clock = Clock.fixed(MAX_INSTANT, ZoneId.UTC);
        LocalDateTime test = LocalDateTime.now(clock);
        assertEquals(test, MAX_DATE_TIME);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void now_Clock_tooBig() {
        Clock clock = Clock.fixed(MAX_INSTANT.plusSeconds(24 * 60 * 60), ZoneId.UTC);
        LocalDateTime.now(clock);
    }

    @Test(groups={"tck"})
    public void now_Clock_minYear() {
        Clock clock = Clock.fixed(MIN_INSTANT, ZoneId.UTC);
        LocalDateTime test = LocalDateTime.now(clock);
        assertEquals(test, MIN_DATE_TIME);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void now_Clock_tooLow() {
        Clock clock = Clock.fixed(MIN_INSTANT.minusNanos(1), ZoneId.UTC);
        LocalDateTime.now(clock);
    }

    //-----------------------------------------------------------------------
    // of() factories
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofMidnight_intsMonth() {
        LocalDateTime dateTime = LocalDateTime.ofMidnight(2008, Month.FEBRUARY, 29);
        check(dateTime, 2008, 2, 29, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofMidnight_intsMonth_yearTooLow() {
        LocalDateTime.ofMidnight(Integer.MIN_VALUE, Month.FEBRUARY, 29);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofMidnight_intsMonth_nullMonth() {
        LocalDateTime.ofMidnight(2008, null, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofMidnight_intsMonth_dayTooLow() {
        LocalDateTime.ofMidnight(2008, Month.FEBRUARY, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofMidnight_intsMonth_dayTooHigh() {
        LocalDateTime.ofMidnight(2008, Month.MARCH, 32);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofMidnight_ints() {
        LocalDateTime dateTime = LocalDateTime.ofMidnight(2008, 2, 29);
        check(dateTime, 2008, 2, 29, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofMidnight_ints_yearTooLow() {
        LocalDateTime.ofMidnight(Integer.MIN_VALUE, 2, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofMidnight_ints_monthTooLow() {
        LocalDateTime.ofMidnight(2008, 0, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofMidnight_ints_monthTooHigh() {
        LocalDateTime.ofMidnight(2008, 13, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofMidnight_ints_dayTooLow() {
        LocalDateTime.ofMidnight(2008, 2, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofMidnight_ints_dayTooHigh() {
        LocalDateTime.ofMidnight(2008, 3, 32);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofMidnight_LocalDate() {
        LocalDateTime dateTime = LocalDateTime.ofMidnight(LocalDate.of(2008, 2, 29));
        check(dateTime, 2008, 2, 29, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofMidnight_LocalDate_null() {
        LocalDateTime.ofMidnight((LocalDate) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_4intsMonth() {
        LocalDateTime dateTime = LocalDateTime.of(2007, Month.JULY, 15, 12, 30);
        check(dateTime, 2007, 7, 15, 12, 30, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_4intsMonth_yearTooLow() {
        LocalDateTime.of(Integer.MIN_VALUE, Month.JULY, 15, 12, 30);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_4intsMonth_nullMonth() {
        LocalDateTime.of(2007, null, 15, 12, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_4intsMonth_dayTooLow() {
        LocalDateTime.of(2007, Month.JULY, -1, 12, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_4intsMonth_dayTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 32, 12, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_4intsMonth_hourTooLow() {
        LocalDateTime.of(2007, Month.JULY, 15, -1, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_4intsMonth_hourTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 15, 24, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_4intsMonth_minuteTooLow() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_4intsMonth_minuteTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, 60);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_5intsMonth() {
        LocalDateTime dateTime = LocalDateTime.of(2007, Month.JULY, 15, 12, 30, 40);
        check(dateTime, 2007, 7, 15, 12, 30, 40, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5intsMonth_yearTooLow() {
        LocalDateTime.of(Integer.MIN_VALUE, Month.JULY, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_5intsMonth_nullMonth() {
        LocalDateTime.of(2007, null, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5intsMonth_dayTooLow() {
        LocalDateTime.of(2007, Month.JULY, -1, 12, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5intsMonth_dayTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 32, 12, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5intsMonth_hourTooLow() {
        LocalDateTime.of(2007, Month.JULY, 15, -1, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5intsMonth_hourTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 15, 24, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5intsMonth_minuteTooLow() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, -1, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5intsMonth_minuteTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, 60, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5intsMonth_secondTooLow() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, 30, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5intsMonth_secondTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, 30, 60);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_6intsMonth() {
        LocalDateTime dateTime = LocalDateTime.of(2007, Month.JULY, 15, 12, 30, 40, 987654321);
        check(dateTime, 2007, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_yearTooLow() {
        LocalDateTime.of(Integer.MIN_VALUE, Month.JULY, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_6intsMonth_nullMonth() {
        LocalDateTime.of(2007, null, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_dayTooLow() {
        LocalDateTime.of(2007, Month.JULY, -1, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_dayTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 32, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_hourTooLow() {
        LocalDateTime.of(2007, Month.JULY, 15, -1, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_hourTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 15, 24, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_minuteTooLow() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, -1, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_minuteTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, 60, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_secondTooLow() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, 30, -1, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_secondTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, 30, 60, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_nanoTooLow() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, 30, 40, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6intsMonth_nanoTooHigh() {
        LocalDateTime.of(2007, Month.JULY, 15, 12, 30, 40, 1000000000);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_5ints() {
        LocalDateTime dateTime = LocalDateTime.of(2007, 7, 15, 12, 30);
        check(dateTime, 2007, 7, 15, 12, 30, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5ints_yearTooLow() {
        LocalDateTime.of(Integer.MIN_VALUE, 7, 15, 12, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5ints_monthTooLow() {
        LocalDateTime.of(2007, 0, 15, 12, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5ints_monthTooHigh() {
        LocalDateTime.of(2007, 13, 15, 12, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5ints_dayTooLow() {
        LocalDateTime.of(2007, 7, -1, 12, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5ints_dayTooHigh() {
        LocalDateTime.of(2007, 7, 32, 12, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5ints_hourTooLow() {
        LocalDateTime.of(2007, 7, 15, -1, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5ints_hourTooHigh() {
        LocalDateTime.of(2007, 7, 15, 24, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5ints_minuteTooLow() {
        LocalDateTime.of(2007, 7, 15, 12, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_5ints_minuteTooHigh() {
        LocalDateTime.of(2007, 7, 15, 12, 60);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_6ints() {
        LocalDateTime dateTime = LocalDateTime.of(2007, 7, 15, 12, 30, 40);
        check(dateTime, 2007, 7, 15, 12, 30, 40, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_yearTooLow() {
        LocalDateTime.of(Integer.MIN_VALUE, 7, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_monthTooLow() {
        LocalDateTime.of(2007, 0, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_monthTooHigh() {
        LocalDateTime.of(2007, 13, 15, 12, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_dayTooLow() {
        LocalDateTime.of(2007, 7, -1, 12, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_dayTooHigh() {
        LocalDateTime.of(2007, 7, 32, 12, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_hourTooLow() {
        LocalDateTime.of(2007, 7, 15, -1, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_hourTooHigh() {
        LocalDateTime.of(2007, 7, 15, 24, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_minuteTooLow() {
        LocalDateTime.of(2007, 7, 15, 12, -1, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_minuteTooHigh() {
        LocalDateTime.of(2007, 7, 15, 12, 60, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_secondTooLow() {
        LocalDateTime.of(2007, 7, 15, 12, 30, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_6ints_secondTooHigh() {
        LocalDateTime.of(2007, 7, 15, 12, 30, 60);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_7ints() {
        LocalDateTime dateTime = LocalDateTime.of(2007, 7, 15, 12, 30, 40, 987654321);
        check(dateTime, 2007, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_yearTooLow() {
        LocalDateTime.of(Integer.MIN_VALUE, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_monthTooLow() {
        LocalDateTime.of(2007, 0, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_monthTooHigh() {
        LocalDateTime.of(2007, 13, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_dayTooLow() {
        LocalDateTime.of(2007, 7, -1, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_dayTooHigh() {
        LocalDateTime.of(2007, 7, 32, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_hourTooLow() {
        LocalDateTime.of(2007, 7, 15, -1, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_hourTooHigh() {
        LocalDateTime.of(2007, 7, 15, 24, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_minuteTooLow() {
        LocalDateTime.of(2007, 7, 15, 12, -1, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_minuteTooHigh() {
        LocalDateTime.of(2007, 7, 15, 12, 60, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_secondTooLow() {
        LocalDateTime.of(2007, 7, 15, 12, 30, -1, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_secondTooHigh() {
        LocalDateTime.of(2007, 7, 15, 12, 30, 60, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_nanoTooLow() {
        LocalDateTime.of(2007, 7, 15, 12, 30, 40, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_7ints_nanoTooHigh() {
        LocalDateTime.of(2007, 7, 15, 12, 30, 40, 1000000000);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_LocalDate_LocalTime() {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(2007, 7, 15), LocalTime.of(12, 30, 40, 987654321));
        check(dateTime, 2007, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDate_LocalTime_nullLocalDate() {
        LocalDateTime.of(null, LocalTime.of(12, 30, 40, 987654321));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDate_LocalTime_nullLocalTime() {
        LocalDateTime.of(LocalDate.of(2007, 7, 15), null);
    }

    //-----------------------------------------------------------------------
    // from()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(LocalDateTime.from(LocalDateTime.of(2007, 7, 15, 17, 30)), LocalDateTime.of(2007, 7, 15, 17, 30));
        assertEquals(LocalDateTime.from(OffsetDateTime.of(2007, 7, 15, 17, 30, ZoneOffset.ofHours(2))), LocalDateTime.of(2007, 7, 15, 17, 30));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        LocalDateTime.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        LocalDateTime.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleToString", groups={"tck"})
//    public void test_parse(int y, int month, int d, int h, int m, int s, int n, String text) {
//        LocalDateTime t = LocalDateTime.parse(text);
//        assertEquals(t.getYear(), y);
//        assertEquals(t.getMonth().getValue(), month);
//        assertEquals(t.getDayOfMonth(), d);
//        assertEquals(t.getHour(), h);
//        assertEquals(t.getMinute(), m);
//        assertEquals(t.getSecond(), s);
//        assertEquals(t.getNano(), n);
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_illegalValue() {
//        LocalDateTime.parse("2008-06-32T11:15");
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_invalidValue() {
//        LocalDateTime.parse("2008-06-31T11:15");
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void factory_parse_nullText() {
//        LocalDateTime.parse((String) null);
//    }

    //-----------------------------------------------------------------------
    // parse(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_parse_formatter() {
        final LocalDateTime dateTime = LocalDateTime.of(2010, 12, 3, 11, 30, 45);
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                throw new AssertionError();
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object parse(CharSequence text, Class type) {
                return dateTime;
            }
        };
        LocalDateTime test = LocalDateTime.parse("ANY", f);
        assertEquals(test, dateTime);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_parse_formatter_nullText() {
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                throw new AssertionError();
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object parse(CharSequence text, Class type) {
                assertEquals(text, null);
                throw new NullPointerException();
            }
        };
        LocalDateTime.parse((String) null, f);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_parse_formatter_nullFormatter() {
        LocalDateTime.parse("ANY", null);
    }

    //-----------------------------------------------------------------------
    // get(DateTimeField)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_get_DateTimeField() {
        LocalDateTime test = LocalDateTime.of(2008, 6, 30, 12, 30, 40, 987654321);
        assertEquals(test.get(LocalDateTimeField.YEAR), 2008);
        assertEquals(test.get(LocalDateTimeField.MONTH_OF_YEAR), 6);
        assertEquals(test.get(LocalDateTimeField.DAY_OF_MONTH), 30);
        assertEquals(test.get(LocalDateTimeField.DAY_OF_WEEK), 1);
        assertEquals(test.get(LocalDateTimeField.DAY_OF_YEAR), 182);
        
        assertEquals(test.get(LocalDateTimeField.HOUR_OF_DAY), 12);
        assertEquals(test.get(LocalDateTimeField.MINUTE_OF_HOUR), 30);
        assertEquals(test.get(LocalDateTimeField.SECOND_OF_MINUTE), 40);
        assertEquals(test.get(LocalDateTimeField.NANO_OF_SECOND), 987654321);
        assertEquals(test.get(LocalDateTimeField.HOUR_OF_AMPM), 0);
        assertEquals(test.get(LocalDateTimeField.AMPM_OF_DAY), AmPm.PM.getValue());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"} )
    public void test_get_DateTimeField_null() {
        LocalDateTime test = LocalDateTime.of(2008, 6, 30, 12, 30, 40, 987654321);
        test.get((DateTimeField) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_get_DateTimeField_invalidField() {
        TEST_2007_07_15_12_30_40_987654321.get(MockFieldNoValue.INSTANCE);
    }

    //-----------------------------------------------------------------------
    // extract(Class)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_extract_Class() {
        LocalDateTime test = LocalDateTime.of(2008, 6, 30, 12, 30, 40, 987654321);
        assertEquals(test.extract(LocalDate.class), test.toLocalDate());
        assertEquals(test.extract(LocalTime.class), test.toLocalTime());
        assertEquals(test.extract(LocalDateTime.class), test);
        assertEquals(test.extract(OffsetDate.class), null);
        assertEquals(test.extract(OffsetTime.class), null);
        assertEquals(test.extract(OffsetDateTime.class), null);
        assertEquals(test.extract(ZonedDateTime.class), null);
        assertEquals(test.extract(ZoneOffset.class), null);
        assertEquals(test.extract(ZoneId.class), null);
        assertEquals(test.extract(Instant.class), null);
        assertEquals(test.extract(Class.class), LocalDateTime.class);
        assertEquals(test.extract(String.class), null);
        assertEquals(test.extract(BigDecimal.class), null);
        assertEquals(test.extract(null), null);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {2008, 7, 5},
            {2007, 7, 5},
            {2006, 7, 5},
            {2005, 7, 5},
            {2004, 1, 1},
            {-1, 1, 2},
        };
    }

    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {0, 0, 0, 0},
            {0, 0, 0, 1},
            {0, 0, 1, 0},
            {0, 0, 1, 1},
            {0, 1, 0, 0},
            {0, 1, 0, 1},
            {0, 1, 1, 0},
            {0, 1, 1, 1},
            {1, 0, 0, 0},
            {1, 0, 0, 1},
            {1, 0, 1, 0},
            {1, 0, 1, 1},
            {1, 1, 0, 0},
            {1, 1, 0, 1},
            {1, 1, 1, 0},
            {1, 1, 1, 1},
        };
    }

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates", groups={"tck"})
    public void test_get_dates(int y, int m, int d) {
        LocalDateTime a = LocalDateTime.of(y, m, d, 12, 30);
        assertEquals(a.getYear(), y);
        assertEquals(a.getMonth(), Month.of(m));
        assertEquals(a.getDayOfMonth(), d);
    }

    @Test(dataProvider="sampleDates", groups={"tck"})
    public void test_getDOY(int y, int m, int d) {
        LocalDateTime a = LocalDateTime.of(y, m, d, 12 ,30);
        int total = 0;
        for (int i = 1; i < m; i++) {
            total += Month.of(i).length(isIsoLeap(y));
        }
        int doy = total + d;
        assertEquals(a.getDayOfYear(), doy);
    }

    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_get_times(int h, int m, int s, int ns) {
        LocalDateTime a = LocalDateTime.of(TEST_2007_07_15_12_30_40_987654321.toLocalDate(), LocalTime.of(h, m, s, ns));
        assertEquals(a.getHour(), h);
        assertEquals(a.getMinute(), m);
        assertEquals(a.getSecond(), s);
        assertEquals(a.getNano(), ns);
    }

    //-----------------------------------------------------------------------
    // getDayOfWeek()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getDayOfWeek() {
        DayOfWeek dow = DayOfWeek.MONDAY;
        for (Month month : Month.values()) {
            int length = month.length(false);
            for (int i = 1; i <= length; i++) {
                LocalDateTime d = LocalDateTime.of(LocalDate.of(2007, month, i),
                        TEST_2007_07_15_12_30_40_987654321.toLocalTime());
                assertSame(d.getDayOfWeek(), dow);
                dow = dow.plus(1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_with_adjustment() {
        final LocalDateTime sample = LocalDateTime.of(2012, 3, 4, 23, 5);
        DateTimeAdjuster adjuster = new DateTimeAdjuster() {
            @Override
            public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
                return sample;
            }
        };
        assertEquals(TEST_2007_07_15_12_30_40_987654321.with(adjuster), sample);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_with_adjustment_null() {
        TEST_2007_07_15_12_30_40_987654321.with((DateTimeAdjuster) null);
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withYear_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withYear(2008);
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_withYear_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withYear(2007);
        assertSame(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate());
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }
    
    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withYear_int_invalid() {
        TEST_2007_07_15_12_30_40_987654321.withYear(Year.MIN_YEAR - 1);
    }

    @Test(groups={"tck"})
    public void test_withYear_int_adjustDay() {
        LocalDateTime t = LocalDateTime.of(2008, 2, 29, 12, 30).withYear(2007);
        LocalDateTime expected = LocalDateTime.of(2007, 2, 28, 12, 30);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withMonth_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMonth(1);
        check(t, 2007, 1, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_withMonth_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMonth(7);
        assertSame(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate());
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withMonth_int_invalid() {
        TEST_2007_07_15_12_30_40_987654321.withMonth(13);
    }

    @Test(groups={"tck"})
    public void test_withMonth_int_adjustDay() {
        LocalDateTime t = LocalDateTime.of(2007, 12, 31, 12, 30).withMonth(11);
        LocalDateTime expected = LocalDateTime.of(2007, 11, 30, 12, 30);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDayOfMonth_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfMonth(1);
        check(t, 2007, 7, 1, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_withDayOfMonth_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfMonth(15);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfMonth_invalid() {
        LocalDateTime.of(2007, 11, 30, 12, 30).withDayOfMonth(32);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfMonth_invalidCombination() {
        LocalDateTime.of(2007, 11, 30, 12, 30).withDayOfMonth(31);
    }

    //-----------------------------------------------------------------------
    // withDayOfYear(int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDayOfYear_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfYear(33);
        assertEquals(t, LocalDateTime.of(2007, 2, 2, 12, 30, 40, 987654321));
    }

    @Test(groups={"implementation"})
    public void test_withDayOfYear_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDayOfYear(31 + 28 + 31 + 30 + 31 + 30 + 15);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfYear_illegal() {
        TEST_2007_07_15_12_30_40_987654321.withDayOfYear(367);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfYear_invalid() {
        TEST_2007_07_15_12_30_40_987654321.withDayOfYear(366);
    }

    //-----------------------------------------------------------------------
    // withDate(int,Month,int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDate_iMi() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2008, Month.FEBRUARY, 29);
        check(t, 2008, 2, 29, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_withDate_iMi_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2007, Month.JULY, 15);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_withDate_iMi_sameYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2007, Month.JUNE, 14);
        check(t, 2007, 6, 14, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_withDate_iMi_sameMonth() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2006, Month.JULY, 14);
        check(t, 2006, 7, 14, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_withDate_iMi_sameDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2006, Month.JUNE, 15);
        check(t, 2006, 6, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_withDate_iMi_dayChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2007, Month.JULY, 16);
        check(t, 2007, 7, 16, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDate_iMi_yearTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withDate(Integer.MIN_VALUE, Month.FEBRUARY, 29);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withDate_iMi_monthNull() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, null, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDate_iMi_dayTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 2, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDate_iMi_dayTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 3, 32);
    }

    //-----------------------------------------------------------------------
    // withDate()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDate() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2008, 2, 29);
        check(t, 2008, 2, 29, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_withDate_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2007, 7, 15);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_withDate_sameYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2007, 6, 14);
        check(t, 2007, 6, 14, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_withDate_sameMonth() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2006, 7, 14);
        check(t, 2006, 7, 14, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_withDate_sameDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2006, 6, 15);
        check(t, 2006, 6, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_withDate_dayChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withDate(2007, 7, 16);
        check(t, 2007, 7, 16, 12, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDate_yearTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withDate(Integer.MIN_VALUE, 2, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDate_monthTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 0, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDate_monthTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 13, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDate_dayTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 2, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDate_dayTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withDate(2008, 3, 32);
    }

    //-----------------------------------------------------------------------
    // withHour()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withHour_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321;
        for (int i = 0; i < 24; i++) {
            t = t.withHour(i);
            assertEquals(t.getHour(), i);
        }
    }

    @Test(groups={"implementation"})
    public void test_withHour_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withHour(12);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withHour_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(1, 0)).withHour(0);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withHour_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(1, 0)).withHour(12);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withHour_hourTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withHour(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withHour_hourTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withHour(24);
    }

    //-----------------------------------------------------------------------
    // withMinute()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withMinute_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withMinute(i);
            assertEquals(t.getMinute(), i);
        }
    }

    @Test(groups={"implementation"})
    public void test_withMinute_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withMinute(30);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 1)).withMinute(0);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 1)).withMinute(0);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withMinute_minuteTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withMinute(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withMinute_minuteTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withMinute(60);
    }

    //-----------------------------------------------------------------------
    // withSecond()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withSecond_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withSecond(i);
            assertEquals(t.getSecond(), i);
        }
    }

    @Test(groups={"implementation"})
    public void test_withSecond_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withSecond(40);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 1)).withSecond(0);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 1)).withSecond(0);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withSecond_secondTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withSecond(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withSecond_secondTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withSecond(60);
    }

    //-----------------------------------------------------------------------
    // withNano()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withNanoOfSecond_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321;
        t = t.withNano(1);
        assertEquals(t.getNano(), 1);
        t = t.withNano(10);
        assertEquals(t.getNano(), 10);
        t = t.withNano(100);
        assertEquals(t.getNano(), 100);
        t = t.withNano(999999999);
        assertEquals(t.getNano(), 999999999);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withNano(987654321);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(0, 0, 0, 1)).withNano(0);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 0, 0, 1)).withNano(0);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withNanoOfSecond_nanoTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withNano(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withNanoOfSecond_nanoTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withNano(1000000000);
    }

    //-----------------------------------------------------------------------
    // withTime()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withTime_2ints() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(13, 40);
        check(t, 2007, 7, 15, 13, 40, 0, 0);
    }

    @Test(groups={"implementation"})
    public void test_withTime_2ints_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30));
        LocalDateTime wt = t.withTime(12, 30);
        assertSame(t, wt);
    }

    @Test(groups={"tck"})
    public void test_withTime_2ints_sameHour() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 20);
        check(t, 2007, 7, 15, 12, 20, 0, 0);
    }

    @Test(groups={"tck"})
    public void test_withTime_2ints_sameMinute() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 30);
        check(t, 2007, 7, 15, 11, 30, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_2ints_hourTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(-1, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_2ints_hourTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(24, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_2ints_minuteTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_2ints_minuteTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 60);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withTime_3ints() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(13, 40, 50);
        check(t, 2007, 7, 15, 13, 40, 50, 0);
    }

    @Test(groups={"implementation"})
    public void test_withTime_3ints_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30, 40));
        LocalDateTime wt = t.withTime(12, 30, 40);
        assertSame(t, wt);
    }

    @Test(groups={"tck"})
    public void test_withTime_3ints_sameHour() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30, 40));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 20, 30);
        check(t, 2007, 7, 15, 12, 20, 30, 0);
    }

    @Test(groups={"tck"})
    public void test_withTime_3ints_sameMinute() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30, 40));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 30, 30);
        check(t, 2007, 7, 15, 11, 30, 30, 0);
    }

    @Test(groups={"tck"})
    public void test_withTime_3ints_sameSecond() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30, 40));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 20, 40);
        check(t, 2007, 7, 15, 11, 20, 40, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_3ints_hourTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(-1, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_3ints_hourTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(24, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_3ints_minuteTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, -1, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_3ints_minuteTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 60, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_3ints_secondTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_3ints_secondTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, 60);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withTime_4ints() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(13, 40, 50, 987654322);
        check(t, 2007, 7, 15, 13, 40, 50, 987654322);
    }

    @Test(groups={"implementation"})
    public void test_withTime_4ints_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30, 40, 987654321));
        LocalDateTime wt = t.withTime(12, 30, 40, 987654321);
        assertSame(t, wt);
    }

    @Test(groups={"tck"})
    public void test_withTime_4ints_sameHour() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30, 40, 987654321));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 20, 30, 987654320);
        check(t, 2007, 7, 15, 12, 20, 30, 987654320);
    }

    @Test(groups={"tck"})
    public void test_withTime_4ints_sameMinute() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30, 40, 987654321));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 30, 30, 987654320);
        check(t, 2007, 7, 15, 11, 30, 30, 987654320);
    }

    @Test(groups={"tck"})
    public void test_withTime_4ints_sameSecond() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30, 40, 987654321));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 20, 40, 987654320);
        check(t, 2007, 7, 15, 11, 20, 40, 987654320);
    }

    @Test(groups={"tck"})
    public void test_withTime_4ints_sameNano() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.of(12, 30, 40, 987654321));
        t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 20, 30, 987654321);
        check(t, 2007, 7, 15, 11, 20, 30, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_4ints_hourTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(-1, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_4ints_hourTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(24, 30, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_4ints_minuteTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, -1, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_4ints_minuteTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 60, 40, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_4ints_secondTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, -1, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_4ints_secondTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, 60, 987654321);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_4ints_nanoTooLow() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, 40, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withTime_4ints_nanoTooHigh() {
        TEST_2007_07_15_12_30_40_987654321.withTime(12, 30, 40, 1000000000);
    }

    //-----------------------------------------------------------------------
    // plus(Duration)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Duration() {
        Duration dur = Duration.ofSeconds(62, 3);
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(dur);
        assertEquals(t, LocalDateTime.of(2007, 7, 15, 12, 31, 42, 987654324));
    }

    @Test(groups={"implementation"})
    public void test_plus_Duration_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(Duration.ZERO);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_Duration_null() {
        TEST_2007_07_15_12_30_40_987654321.plus((Duration) null);
    }

    //-----------------------------------------------------------------------
    // plus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Period_positiveMonths() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(period);
        assertEquals(t, LocalDateTime.of(2008, 2, 15, 12, 30, 40, 987654321));
    }

    @Test(groups={"tck"})
    public void test_plus_Period_negativeDays() {
        Period period = Period.of(-25, LocalPeriodUnit.DAYS);
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(period);
        assertEquals(t, LocalDateTime.of(2007, 6, 20, 12, 30, 40, 987654321));
    }

    @Test(groups={"implementation"})
    public void test_plus_Period_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(Period.ZERO_DAYS);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_Period_null() {
        TEST_2007_07_15_12_30_40_987654321.plus((Period) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_Period_invalidTooLarge() {
        Period period = Period.of(1, LocalPeriodUnit.YEARS);
        LocalDateTime.of(Year.MAX_YEAR, 1, 1, 0, 0).plus(period);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_Period_invalidTooSmall() {
        Period period = Period.of(-1, LocalPeriodUnit.YEARS);
        LocalDateTime.of(Year.MIN_YEAR, 1, 1, 0, 0).plus(period);
    }

    //-----------------------------------------------------------------------
    // plus(long,PeriodUnit)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_longPeriodUnit_positiveMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(7, LocalPeriodUnit.MONTHS);
        assertEquals(t, LocalDateTime.of(2008, 2, 15, 12, 30, 40, 987654321));
    }
 
    @Test(groups={"tck"})
    public void test_plus_longPeriodUnit_negativeDays() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(-25, LocalPeriodUnit.DAYS);
        assertEquals(t, LocalDateTime.of(2007, 6, 20, 12, 30, 40, 987654321));
    }

    @Test(groups={"implementation"})
    public void test_plus_longPeriodUnit_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plus(0, LocalPeriodUnit.DAYS);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_longPeriodUnit_null() {
        TEST_2007_07_15_12_30_40_987654321.plus(1, (PeriodUnit) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_longPeriodUnit_invalidTooLarge() {
        LocalDateTime.of(Year.MAX_YEAR, 1, 1, 0, 0).plus(1, LocalPeriodUnit.YEARS);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_longPeriodUnit_invalidTooSmall() {
        LocalDateTime.of(Year.MIN_YEAR, 1, 1, 0, 0).plus(-1, LocalPeriodUnit.YEARS);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusYears_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(1);
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusYears_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(0);
        assertSame(TEST_2007_07_15_12_30_40_987654321, t);
    }

    @Test(groups={"tck"})
    public void test_plusYears_int_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1);
        check(t, 2006, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusYears_int_adjustDay() {
        LocalDateTime t = LocalDateTime.ofMidnight(2008, 2, 29).plusYears(1);
        check(t, 2009, 2, 28, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_int_invalidTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 1, 1).plusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_int_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusYears(-1);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusMonths_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(1);
        check(t, 2007, 8, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusMonths_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_plusMonths_int_overYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(25);
        check(t, 2009, 8, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusMonths_int_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-1);
        check(t, 2007, 6, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusMonths_int_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-7);
        check(t, 2006, 12, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusMonths_int_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMonths(-31);
        check(t, 2004, 12, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusMonths_int_adjustDayFromLeapYear() {
        LocalDateTime t = LocalDateTime.ofMidnight(2008, 2, 29).plusMonths(12);
        check(t, 2009, 2, 28, 0, 0, 0, 0);
    }
    
    @Test(groups={"tck"})
    public void test_plusMonths_int_adjustDayFromMonthLength() {
        LocalDateTime t = LocalDateTime.ofMidnight(2007, 3, 31).plusMonths(1);
        check(t, 2007, 4, 30, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusMonths_int_invalidTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 1).plusMonths(1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusMonths_int_invalidTooSmall() {
        LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 1).plusMonths(-1);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusWeeksSymmetry")
    Object[][] provider_samplePlusWeeksSymmetry() {
        return new Object[][] {
            {LocalDateTime.ofMidnight(-1, 1, 1)},
            {LocalDateTime.ofMidnight(-1, 2, 28)},
            {LocalDateTime.ofMidnight(-1, 3, 1)},
            {LocalDateTime.ofMidnight(-1, 12, 31)},
            {LocalDateTime.ofMidnight(0, 1, 1)},
            {LocalDateTime.ofMidnight(0, 2, 28)},
            {LocalDateTime.ofMidnight(0, 2, 29)},
            {LocalDateTime.ofMidnight(0, 3, 1)},
            {LocalDateTime.ofMidnight(0, 12, 31)},
            {LocalDateTime.ofMidnight(2007, 1, 1)},
            {LocalDateTime.ofMidnight(2007, 2, 28)},
            {LocalDateTime.ofMidnight(2007, 3, 1)},
            {LocalDateTime.ofMidnight(2007, 12, 31)},
            {LocalDateTime.ofMidnight(2008, 1, 1)},
            {LocalDateTime.ofMidnight(2008, 2, 28)},
            {LocalDateTime.ofMidnight(2008, 2, 29)},
            {LocalDateTime.ofMidnight(2008, 3, 1)},
            {LocalDateTime.ofMidnight(2008, 12, 31)},
            {LocalDateTime.ofMidnight(2099, 1, 1)},
            {LocalDateTime.ofMidnight(2099, 2, 28)},
            {LocalDateTime.ofMidnight(2099, 3, 1)},
            {LocalDateTime.ofMidnight(2099, 12, 31)},
            {LocalDateTime.ofMidnight(2100, 1, 1)},
            {LocalDateTime.ofMidnight(2100, 2, 28)},
            {LocalDateTime.ofMidnight(2100, 3, 1)},
            {LocalDateTime.ofMidnight(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="samplePlusWeeksSymmetry", groups={"tck"})
    public void test_plusWeeks_symmetry(LocalDateTime reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDateTime t = reference.plusWeeks(weeks).plusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.plusWeeks(-weeks).plusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(1);
        check(t, 2007, 7, 22, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusWeeks_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_overMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(9);
        check(t, 2007, 9, 16, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_overYears() {
        LocalDateTime t = LocalDateTime.of(2006, 7, 16, 12, 30, 40, 987654321).plusWeeks(52);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_overLeapYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1).plusWeeks(104);
        check(t, 2008, 7, 12, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(-1);
        check(t, 2007, 7, 8, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(-28);
        check(t, 2006, 12, 31, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusWeeks(-104);
        check(t, 2005, 7, 17, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_maximum() {
        LocalDateTime t = LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 24).plusWeeks(1);
        check(t, Year.MAX_YEAR, 12, 31, 0, 0, 0, 0);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_minimum() {
        LocalDateTime t = LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 8).plusWeeks(-1);
        check(t, Year.MIN_YEAR, 1, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusWeeks_invalidTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 25).plusWeeks(1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusWeeks_invalidTooSmall() {
        LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 7).plusWeeks(-1);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusDaysSymmetry")
    Object[][] provider_samplePlusDaysSymmetry() {
        return new Object[][] {
            {LocalDateTime.ofMidnight(-1, 1, 1)},
            {LocalDateTime.ofMidnight(-1, 2, 28)},
            {LocalDateTime.ofMidnight(-1, 3, 1)},
            {LocalDateTime.ofMidnight(-1, 12, 31)},
            {LocalDateTime.ofMidnight(0, 1, 1)},
            {LocalDateTime.ofMidnight(0, 2, 28)},
            {LocalDateTime.ofMidnight(0, 2, 29)},
            {LocalDateTime.ofMidnight(0, 3, 1)},
            {LocalDateTime.ofMidnight(0, 12, 31)},
            {LocalDateTime.ofMidnight(2007, 1, 1)},
            {LocalDateTime.ofMidnight(2007, 2, 28)},
            {LocalDateTime.ofMidnight(2007, 3, 1)},
            {LocalDateTime.ofMidnight(2007, 12, 31)},
            {LocalDateTime.ofMidnight(2008, 1, 1)},
            {LocalDateTime.ofMidnight(2008, 2, 28)},
            {LocalDateTime.ofMidnight(2008, 2, 29)},
            {LocalDateTime.ofMidnight(2008, 3, 1)},
            {LocalDateTime.ofMidnight(2008, 12, 31)},
            {LocalDateTime.ofMidnight(2099, 1, 1)},
            {LocalDateTime.ofMidnight(2099, 2, 28)},
            {LocalDateTime.ofMidnight(2099, 3, 1)},
            {LocalDateTime.ofMidnight(2099, 12, 31)},
            {LocalDateTime.ofMidnight(2100, 1, 1)},
            {LocalDateTime.ofMidnight(2100, 2, 28)},
            {LocalDateTime.ofMidnight(2100, 3, 1)},
            {LocalDateTime.ofMidnight(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="samplePlusDaysSymmetry", groups={"tck"})
    public void test_plusDays_symmetry(LocalDateTime reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDateTime t = reference.plusDays(days).plusDays(-days);
            assertEquals(t, reference);

            t = reference.plusDays(-days).plusDays(days);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"tck"})
    public void test_plusDays_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(1);
        check(t, 2007, 7, 16, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusDays_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_plusDays_overMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(62);
        check(t, 2007, 9, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusDays_overYears() {
        LocalDateTime t = LocalDateTime.of(2006, 7, 14, 12, 30, 40, 987654321).plusDays(366);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_plusDays_overLeapYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(-1).plusDays(365 + 366);
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusDays_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(-1);
        check(t, 2007, 7, 14, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusDays_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(-196);
        check(t, 2006, 12, 31, 12, 30, 40, 987654321);
    }
    
    @Test(groups={"tck"})
    public void test_plusDays_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusDays(-730);
        check(t, 2005, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_plusDays_maximum() {
        LocalDateTime t = LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 30).plusDays(1);
        check(t, Year.MAX_YEAR, 12, 31, 0, 0, 0, 0);
    }

    @Test(groups={"tck"})
    public void test_plusDays_minimum() {
        LocalDateTime t = LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 2).plusDays(-1);
        check(t, Year.MIN_YEAR, 1, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusDays_invalidTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 31).plusDays(1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusDays_invalidTooSmall() {
        LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 1).plusDays(-1);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plusDays_overflowTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 31).plusDays(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plusDays_overflowTooSmall() {
        LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 1).plusDays(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusHours_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.toLocalDate();

        for (int i = 0; i < 50; i++) {
            t = t.plusHours(1);

            if ((i + 1) % 24 == 0) {
                d = d.plusDays(1);
            }

            assertEquals(t.toLocalDate(), d);
            assertEquals(t.getHour(), (i + 1) % 24);
        }
    }

    @Test(groups={"tck"})
    public void test_plusHours_fromZero() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = base.toLocalDate().minusDays(3);
        LocalTime t = LocalTime.of(21, 0);
        
        for (int i = -50; i < 50; i++) {
            LocalDateTime dt = base.plusHours(i);
            t = t.plusHours(1);

            if (t.getHour() == 0) {
                d = d.plusDays(1);
            }

            assertEquals(dt.toLocalDate(), d);
            assertEquals(dt.toLocalTime(), t);
        }
    }

    @Test(groups={"tck"})
    public void test_plusHours_fromOne() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.withTime(1, 0);
        LocalDate d = base.toLocalDate().minusDays(3);
        LocalTime t = LocalTime.of(22, 0);

        for (int i = -50; i < 50; i++) {
            LocalDateTime dt = base.plusHours(i);

            t = t.plusHours(1);

            if (t.getHour() == 0) {
                d = d.plusDays(1);
            }

            assertEquals(dt.toLocalDate(), d);
            assertEquals(dt.toLocalTime(), t);
        }
    }

    @Test(groups={"implementation"})
    public void test_plusHours_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusHours(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(23, 0).plusHours(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 0).plusHours(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusMinutes_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.toLocalDate();

        int hour = 0;
        int min = 0;

        for (int i = 0; i < 70; i++) {
            t = t.plusMinutes(1);
            min++;
            if (min == 60) {
                hour++;
                min = 0;
            }

            assertEquals(t.toLocalDate(), d);
            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
        }
    }

    @Test(groups={"tck"})
    public void test_plusMinutes_fromZero() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = base.toLocalDate().minusDays(1);
        LocalTime t = LocalTime.of(22, 49);

        for (int i = -70; i < 70; i++) {
            LocalDateTime dt = base.plusMinutes(i);
            t = t.plusMinutes(1);

            if (t == LocalTime.MIDNIGHT) {
                d = d.plusDays(1);
            }

            assertEquals(dt.toLocalDate(), d, String.valueOf(i));
            assertEquals(dt.toLocalTime(), t, String.valueOf(i));
        }
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMinutes(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_plusMinutes_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMinutes(24 * 60);
        assertEquals(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate().plusDays(1));
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusMinutes(24 * 60);
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(23, 59).plusMinutes(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 59).plusMinutes(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusSeconds_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.toLocalDate();

        int hour = 0;
        int min = 0;
        int sec = 0;

        for (int i = 0; i < 3700; i++) {
            t = t.plusSeconds(1);
            sec++;
            if (sec == 60) {
                min++;
                sec = 0;
            }
            if (min == 60) {
                hour++;
                min = 0;
            }

            assertEquals(t.toLocalDate(), d);
            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
            assertEquals(t.getSecond(), sec);
        }
    }

    @DataProvider(name="plusSeconds_fromZero")
    Iterator<Object[]> plusSeconds_fromZero() {
        return new Iterator<Object[]>() {
            int delta = 30;

            int i = -3660;
            LocalDate date = TEST_2007_07_15_12_30_40_987654321.toLocalDate().minusDays(1);
            int hour = 22;
            int min = 59;
            int sec = 0;

            public boolean hasNext() {
                return i <= 3660;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, date, hour, min, sec};
                i += delta;
                sec += delta;

                if (sec >= 60) {
                    min++;
                    sec -= 60;

                    if (min == 60) {
                        hour++;
                        min = 0;
                        
                        if (hour == 24) {
                            hour = 0;
                        }
                    }
                }
                
                if (i == 0) {
                    date = date.plusDays(1);
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="plusSeconds_fromZero", groups={"tck"})
    public void test_plusSeconds_fromZero(int seconds, LocalDate date, int hour, int min, int sec) {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDateTime t = base.plusSeconds(seconds);

        assertEquals(date, t.toLocalDate());
        assertEquals(hour, t.getHour());
        assertEquals(min, t.getMinute());
        assertEquals(sec, t.getSecond());
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusSeconds(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_plusSeconds_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusSeconds(24 * 60 * 60);
        assertEquals(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate().plusDays(1));
    }
    
    @Test(groups={"implementation"})
    public void test_plusSeconds_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusSeconds(24 * 60 * 60);
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(23, 59, 59).plusSeconds(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 59, 59).plusSeconds(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusNanos_halfABillion() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.toLocalDate();

        int hour = 0;
        int min = 0;
        int sec = 0;
        int nanos = 0;

        for (long i = 0; i < 3700 * 1000000000L; i+= 500000000) {
            t = t.plusNanos(500000000);
            nanos += 500000000;
            if (nanos == 1000000000) {
                sec++;
                nanos = 0;
            }
            if (sec == 60) {
                min++;
                sec = 0;
            }
            if (min == 60) {
                hour++;
                min = 0;
            }

            assertEquals(t.toLocalDate(), d, String.valueOf(i));
            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
            assertEquals(t.getSecond(), sec);
            assertEquals(t.getNano(), nanos);
        }
    }

    @DataProvider(name="plusNanos_fromZero")
    Iterator<Object[]> plusNanos_fromZero() {
        return new Iterator<Object[]>() {
            long delta = 7500000000L;

            long i = -3660 * 1000000000L;
            LocalDate date = TEST_2007_07_15_12_30_40_987654321.toLocalDate().minusDays(1);
            int hour = 22;
            int min = 59;
            int sec = 0;
            long nanos = 0;

            public boolean hasNext() {
                return i <= 3660 * 1000000000L;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, date, hour, min, sec, (int)nanos};
                i += delta;
                nanos += delta;

                if (nanos >= 1000000000L) {
                    sec += nanos / 1000000000L;
                    nanos %= 1000000000L;

                    if (sec >= 60) {
                        min++;
                        sec %= 60;

                        if (min == 60) {
                            hour++;
                            min = 0;

                            if (hour == 24) {
                                hour = 0;
                                date = date.plusDays(1);
                            }
                        }
                    }
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="plusNanos_fromZero", groups={"tck"})
    public void test_plusNanos_fromZero(long nanoseconds, LocalDate date, int hour, int min, int sec, int nanos) {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDateTime t = base.plusNanos(nanoseconds);

        assertEquals(date, t.toLocalDate());
        assertEquals(hour, t.getHour());
        assertEquals(min, t.getMinute());
        assertEquals(sec, t.getSecond());
        assertEquals(nanos, t.getNano());
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusNanos(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusNanos(24 * 60 * 60 * 1000000000L);
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }
    
    @Test(groups={"tck"})
    public void test_plusNanos_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusNanos(24 * 60 * 60 * 1000000000L);
        assertEquals(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate().plusDays(1));
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(23, 59, 59, 999999999).plusNanos(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(11, 59, 59, 999999999).plusNanos(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minus(Duration)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Duration() {
        Duration dur = Duration.ofSeconds(62, 3);
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(dur);
        assertEquals(t, LocalDateTime.of(2007, 7, 15, 12, 29, 38, 987654318));
    }

    @Test(groups={"implementation"})
    public void test_minus_Duration_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(Duration.ZERO);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_Duration_null() {
        TEST_2007_07_15_12_30_40_987654321.minus((Duration) null);
    }

    //-----------------------------------------------------------------------
    // minus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Period_positiveMonths() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(period);
        assertEquals(t, LocalDateTime.of(2006, 12, 15, 12, 30, 40, 987654321));
    }

    @Test(groups={"tck"})
    public void test_minus_Period_negativeDays() {
        Period period = Period.of(-25, LocalPeriodUnit.DAYS);
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(period);
        assertEquals(t, LocalDateTime.of(2007, 8, 9, 12, 30, 40, 987654321));
    }

    @Test(groups={"implementation"})
    public void test_minus_Period_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(Period.ZERO_DAYS);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_Period_null() {
        TEST_2007_07_15_12_30_40_987654321.minus((Period) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_Period_invalidTooLarge() {
        Period period = Period.of(-1, LocalPeriodUnit.YEARS);
        LocalDateTime.of(Year.MAX_YEAR, 1, 1, 0, 0).minus(period);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_Period_invalidTooSmall() {
        Period period = Period.of(1, LocalPeriodUnit.YEARS);
        LocalDateTime.of(Year.MIN_YEAR, 1, 1, 0, 0).minus(period);
    }

    //-----------------------------------------------------------------------
    // minus(long,PeriodUnit)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_longPeriodUnit_positiveMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(7, LocalPeriodUnit.MONTHS);
        assertEquals(t, LocalDateTime.of(2006, 12, 15, 12, 30, 40, 987654321));
    }
 
    @Test(groups={"tck"})
    public void test_minus_longPeriodUnit_negativeDays() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(-25, LocalPeriodUnit.DAYS);
        assertEquals(t, LocalDateTime.of(2007, 8, 9, 12, 30, 40, 987654321));
    }

    @Test(groups={"implementation"})
    public void test_minus_longPeriodUnit_zero() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minus(0, LocalPeriodUnit.DAYS);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_longPeriodUnit_null() {
        TEST_2007_07_15_12_30_40_987654321.minus(1, (PeriodUnit) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_longPeriodUnit_invalidTooLarge() {
        LocalDateTime.of(Year.MAX_YEAR, 1, 1, 0, 0).minus(-1, LocalPeriodUnit.YEARS);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_longPeriodUnit_invalidTooSmall() {
        LocalDateTime.of(Year.MIN_YEAR, 1, 1, 0, 0).minus(1, LocalPeriodUnit.YEARS);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusYears_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(1);
        check(t, 2006, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusYears_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_minusYears_int_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(-1);
        check(t, 2008, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusYears_int_adjustDay() {
        LocalDateTime t = LocalDateTime.ofMidnight(2008, 2, 29).minusYears(1);
        check(t, 2007, 2, 28, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_int_invalidTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 1, 1).minusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_int_invalidTooSmall() {
        LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 1).minusYears(1);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusMonths_int_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(1);
        check(t, 2007, 6, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusMonths_int_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_minusMonths_int_overYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(25);
        check(t, 2005, 6, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusMonths_int_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-1);
        check(t, 2007, 8, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusMonths_int_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-7);
        check(t, 2008, 2, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusMonths_int_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMonths(-31);
        check(t, 2010, 2, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusMonths_int_adjustDayFromLeapYear() {
        LocalDateTime t = LocalDateTime.ofMidnight(2008, 2, 29).minusMonths(12);
        check(t, 2007, 2, 28, 0, 0, 0, 0);
    }

    @Test(groups={"tck"})
    public void test_minusMonths_int_adjustDayFromMonthLength() {
        LocalDateTime t = LocalDateTime.ofMidnight(2007, 3, 31).minusMonths(1);
        check(t, 2007, 2, 28, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusMonths_int_invalidTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 1).minusMonths(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusMonths_int_invalidTooSmall() {
        LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 1).minusMonths(1);
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusWeeksSymmetry")
    Object[][] provider_sampleMinusWeeksSymmetry() {
        return new Object[][] {
            {LocalDateTime.ofMidnight(-1, 1, 1)},
            {LocalDateTime.ofMidnight(-1, 2, 28)},
            {LocalDateTime.ofMidnight(-1, 3, 1)},
            {LocalDateTime.ofMidnight(-1, 12, 31)},
            {LocalDateTime.ofMidnight(0, 1, 1)},
            {LocalDateTime.ofMidnight(0, 2, 28)},
            {LocalDateTime.ofMidnight(0, 2, 29)},
            {LocalDateTime.ofMidnight(0, 3, 1)},
            {LocalDateTime.ofMidnight(0, 12, 31)},
            {LocalDateTime.ofMidnight(2007, 1, 1)},
            {LocalDateTime.ofMidnight(2007, 2, 28)},
            {LocalDateTime.ofMidnight(2007, 3, 1)},
            {LocalDateTime.ofMidnight(2007, 12, 31)},
            {LocalDateTime.ofMidnight(2008, 1, 1)},
            {LocalDateTime.ofMidnight(2008, 2, 28)},
            {LocalDateTime.ofMidnight(2008, 2, 29)},
            {LocalDateTime.ofMidnight(2008, 3, 1)},
            {LocalDateTime.ofMidnight(2008, 12, 31)},
            {LocalDateTime.ofMidnight(2099, 1, 1)},
            {LocalDateTime.ofMidnight(2099, 2, 28)},
            {LocalDateTime.ofMidnight(2099, 3, 1)},
            {LocalDateTime.ofMidnight(2099, 12, 31)},
            {LocalDateTime.ofMidnight(2100, 1, 1)},
            {LocalDateTime.ofMidnight(2100, 2, 28)},
            {LocalDateTime.ofMidnight(2100, 3, 1)},
            {LocalDateTime.ofMidnight(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="sampleMinusWeeksSymmetry", groups={"tck"})
    public void test_minusWeeks_symmetry(LocalDateTime reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDateTime t = reference.minusWeeks(weeks).minusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.minusWeeks(-weeks).minusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(1);
        check(t, 2007, 7, 8, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusWeeks_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_overMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(9);
        check(t, 2007, 5, 13, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_overYears() {
        LocalDateTime t = LocalDateTime.of(2008, 7, 13, 12, 30, 40, 987654321).minusWeeks(52);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_overLeapYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusYears(-1).minusWeeks(104);
        check(t, 2006, 7, 18, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(-1);
        check(t, 2007, 7, 22, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(-28);
        check(t, 2008, 1, 27, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusWeeks(-104);
        check(t, 2009, 7, 12, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_maximum() {
        LocalDateTime t = LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 24).minusWeeks(-1);
        check(t, Year.MAX_YEAR, 12, 31, 0, 0, 0, 0);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_minimum() {
        LocalDateTime t = LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 8).minusWeeks(1);
        check(t, Year.MIN_YEAR, 1, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusWeeks_invalidTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 25).minusWeeks(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusWeeks_invalidTooSmall() {
        LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 7).minusWeeks(1);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusDaysSymmetry")
    Object[][] provider_sampleMinusDaysSymmetry() {
        return new Object[][] {
            {LocalDateTime.ofMidnight(-1, 1, 1)},
            {LocalDateTime.ofMidnight(-1, 2, 28)},
            {LocalDateTime.ofMidnight(-1, 3, 1)},
            {LocalDateTime.ofMidnight(-1, 12, 31)},
            {LocalDateTime.ofMidnight(0, 1, 1)},
            {LocalDateTime.ofMidnight(0, 2, 28)},
            {LocalDateTime.ofMidnight(0, 2, 29)},
            {LocalDateTime.ofMidnight(0, 3, 1)},
            {LocalDateTime.ofMidnight(0, 12, 31)},
            {LocalDateTime.ofMidnight(2007, 1, 1)},
            {LocalDateTime.ofMidnight(2007, 2, 28)},
            {LocalDateTime.ofMidnight(2007, 3, 1)},
            {LocalDateTime.ofMidnight(2007, 12, 31)},
            {LocalDateTime.ofMidnight(2008, 1, 1)},
            {LocalDateTime.ofMidnight(2008, 2, 28)},
            {LocalDateTime.ofMidnight(2008, 2, 29)},
            {LocalDateTime.ofMidnight(2008, 3, 1)},
            {LocalDateTime.ofMidnight(2008, 12, 31)},
            {LocalDateTime.ofMidnight(2099, 1, 1)},
            {LocalDateTime.ofMidnight(2099, 2, 28)},
            {LocalDateTime.ofMidnight(2099, 3, 1)},
            {LocalDateTime.ofMidnight(2099, 12, 31)},
            {LocalDateTime.ofMidnight(2100, 1, 1)},
            {LocalDateTime.ofMidnight(2100, 2, 28)},
            {LocalDateTime.ofMidnight(2100, 3, 1)},
            {LocalDateTime.ofMidnight(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="sampleMinusDaysSymmetry", groups={"tck"})
    public void test_minusDays_symmetry(LocalDateTime reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDateTime t = reference.minusDays(days).minusDays(-days);
            assertEquals(t, reference);

            t = reference.minusDays(-days).minusDays(days);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"tck"})
    public void test_minusDays_normal() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(1);
        check(t, 2007, 7, 14, 12, 30, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusDays_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_minusDays_overMonths() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(62);
        check(t, 2007, 5, 14, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusDays_overYears() {
        LocalDateTime t = LocalDateTime.of(2008, 7, 16, 12, 30, 40, 987654321).minusDays(367);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_minusDays_overLeapYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.plusYears(2).minusDays(365 + 366);
        assertEquals(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_minusDays_negative() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(-1);
        check(t, 2007, 7, 16, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusDays_negativeAcrossYear() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(-169);
        check(t, 2007, 12, 31, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusDays_negativeOverYears() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusDays(-731);
        check(t, 2009, 7, 15, 12, 30, 40, 987654321);
    }

    @Test(groups={"tck"})
    public void test_minusDays_maximum() {
        LocalDateTime t = LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 30).minusDays(-1);
        check(t, Year.MAX_YEAR, 12, 31, 0, 0, 0, 0);
    }

    @Test(groups={"tck"})
    public void test_minusDays_minimum() {
        LocalDateTime t = LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 2).minusDays(1);
        check(t, Year.MIN_YEAR, 1, 1, 0, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusDays_invalidTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 31).minusDays(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusDays_invalidTooSmall() {
        LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 1).minusDays(1);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minusDays_overflowTooLarge() {
        LocalDateTime.ofMidnight(Year.MAX_YEAR, 12, 31).minusDays(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minusDays_overflowTooSmall() {
        LocalDateTime.ofMidnight(Year.MIN_YEAR, 1, 1).minusDays(Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusHours_one() {
        LocalDateTime t =TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.toLocalDate();

        for (int i = 0; i < 50; i++) {
            t = t.minusHours(1);

            if (i % 24 == 0) {
                d = d.minusDays(1);
            }

            assertEquals(t.toLocalDate(), d);
            assertEquals(t.getHour(), (((-i + 23) % 24) + 24) % 24);
        }
    }

    @Test(groups={"tck"})
    public void test_minusHours_fromZero() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = base.toLocalDate().plusDays(2);
        LocalTime t = LocalTime.of(3, 0);

        for (int i = -50; i < 50; i++) {
            LocalDateTime dt = base.minusHours(i);
            t = t.minusHours(1);

            if (t.getHour() == 23) {
                d = d.minusDays(1);
            }

            assertEquals(dt.toLocalDate(), d, String.valueOf(i));
            assertEquals(dt.toLocalTime(), t);
        }
    }

    @Test(groups={"tck"})
    public void test_minusHours_fromOne() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.withTime(1, 0);
        LocalDate d = base.toLocalDate().plusDays(2);
        LocalTime t = LocalTime.of(4, 0);

        for (int i = -50; i < 50; i++) {
            LocalDateTime dt = base.minusHours(i);

            t = t.minusHours(1);

            if (t.getHour() == 23) {
                d = d.minusDays(1);
            }

            assertEquals(dt.toLocalDate(), d, String.valueOf(i));
            assertEquals(dt.toLocalTime(), t);
        }
    }

    @Test(groups={"implementation"})
    public void test_minusHours_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusHours(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(1, 0).minusHours(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(13, 0).minusHours(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusMinutes_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.toLocalDate().minusDays(1);

        int hour = 0;
        int min = 0;

        for (int i = 0; i < 70; i++) {
            t = t.minusMinutes(1);
            min--;
            if (min == -1) {
                hour--;
                min = 59;
                
                if (hour == -1) {
                    hour = 23;
                }
            }
            assertEquals(t.toLocalDate(), d);
            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
        }
    }

    @Test(groups={"tck"})
    public void test_minusMinutes_fromZero() {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = base.toLocalDate().minusDays(1);
        LocalTime t = LocalTime.of(22, 49);

        for (int i = 70; i > -70; i--) {
            LocalDateTime dt = base.minusMinutes(i);
            t = t.plusMinutes(1);
            
            if (t == LocalTime.MIDNIGHT) {
                d = d.plusDays(1);
            }

            assertEquals(dt.toLocalDate(), d);
            assertEquals(dt.toLocalTime(), t);
        }
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMinutes(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_noChange_oneDay_same() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMinutes(24 * 60);
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }
    
    @Test(groups={"tck"})
    public void test_minusMinutes_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusMinutes(24 * 60);
        assertEquals(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate().minusDays(1));
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(0, 1).minusMinutes(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 1).minusMinutes(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusSeconds_one() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.toLocalDate().minusDays(1);

        int hour = 0;
        int min = 0;
        int sec = 0;

        for (int i = 0; i < 3700; i++) {
            t = t.minusSeconds(1);
            sec--;
            if (sec == -1) {
                min--;
                sec = 59;

                if (min == -1) {
                    hour--;
                    min = 59;
                    
                    if (hour == -1) {
                        hour = 23;
                    }
                }
            }

            assertEquals(t.toLocalDate(), d);
            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
            assertEquals(t.getSecond(), sec);
        }
    }

    @DataProvider(name="minusSeconds_fromZero")
    Iterator<Object[]> minusSeconds_fromZero() {
        return new Iterator<Object[]>() {
            int delta = 30;

            int i = 3660;
            LocalDate date = TEST_2007_07_15_12_30_40_987654321.toLocalDate().minusDays(1);
            int hour = 22;
            int min = 59;
            int sec = 0;

            public boolean hasNext() {
                return i >= -3660;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, date, hour, min, sec};
                i -= delta;
                sec += delta;

                if (sec >= 60) {
                    min++;
                    sec -= 60;

                    if (min == 60) {
                        hour++;
                        min = 0;
                        
                        if (hour == 24) {
                            hour = 0;
                        }
                    }
                }

                if (i == 0) {
                    date = date.plusDays(1);
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="minusSeconds_fromZero", groups={"tck"})
    public void test_minusSeconds_fromZero(int seconds, LocalDate date, int hour, int min, int sec) {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDateTime t = base.minusSeconds(seconds);

        assertEquals(date, t.toLocalDate());
        assertEquals(hour, t.getHour());
        assertEquals(min, t.getMinute());
        assertEquals(sec, t.getSecond());
    }

    @Test(groups={"implementation"})    
    public void test_minusSeconds_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusSeconds(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusSeconds(24 * 60 * 60);
        assertEquals(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate().minusDays(1));
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(0, 0, 1).minusSeconds(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 0, 1).minusSeconds(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusNanos_halfABillion() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDate d = t.toLocalDate().minusDays(1);

        int hour = 0;
        int min = 0;
        int sec = 0;
        int nanos = 0;

        for (long i = 0; i < 3700 * 1000000000L; i+= 500000000) {
            t = t.minusNanos(500000000);
            nanos -= 500000000;

            if (nanos < 0) {
                sec--;
                nanos += 1000000000;

                if (sec == -1) {
                    min--;
                    sec += 60;
                    
                    if (min == -1) {
                        hour--;
                        min += 60;

                        if (hour == -1) {
                            hour += 24;
                        }
                    }
                }
            }

            assertEquals(t.toLocalDate(), d);
            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
            assertEquals(t.getSecond(), sec);
            assertEquals(t.getNano(), nanos);
        }
    }

    @DataProvider(name="minusNanos_fromZero")
    Iterator<Object[]> minusNanos_fromZero() {
        return new Iterator<Object[]>() {
            long delta = 7500000000L;

            long i = 3660 * 1000000000L;
            LocalDate date = TEST_2007_07_15_12_30_40_987654321.toLocalDate().minusDays(1);
            int hour = 22;
            int min = 59;
            int sec = 0;
            long nanos = 0;

            public boolean hasNext() {
                return i >= -3660 * 1000000000L;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, date, hour, min, sec, (int)nanos};
                i -= delta;
                nanos += delta;

                if (nanos >= 1000000000L) {
                    sec += nanos / 1000000000L;
                    nanos %= 1000000000L;

                    if (sec >= 60) {
                        min++;
                        sec %= 60;

                        if (min == 60) {
                            hour++;
                            min = 0;

                            if (hour == 24) {
                                hour = 0;
                                date = date.plusDays(1);
                            }
                        }
                    }
                }

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="minusNanos_fromZero", groups={"tck"})
    public void test_minusNanos_fromZero(long nanoseconds, LocalDate date, int hour, int min, int sec, int nanos) {
        LocalDateTime base = TEST_2007_07_15_12_30_40_987654321.with(LocalTime.MIDNIGHT);
        LocalDateTime t = base.minusNanos(nanoseconds);

        assertEquals(date, t.toLocalDate());
        assertEquals(hour, t.getHour());
        assertEquals(min, t.getMinute());
        assertEquals(sec, t.getSecond());
        assertEquals(nanos, t.getNano());
    }

    @Test(groups={"implementation"})    
    public void test_minusNanos_noChange() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusNanos(0);
        assertSame(t, TEST_2007_07_15_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_noChange_oneDay() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.minusNanos(24 * 60 * 60 * 1000000000L);
        assertEquals(t.toLocalDate(), TEST_2007_07_15_12_30_40_987654321.toLocalDate().minusDays(1));
        assertSame(t.toLocalTime(), TEST_2007_07_15_12_30_40_987654321.toLocalTime());
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_toMidnight() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(0, 0, 0, 1).minusNanos(1);
        assertSame(t.toLocalTime(), LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_toMidday() {
        LocalDateTime t = TEST_2007_07_15_12_30_40_987654321.withTime(12, 0, 0, 1).minusNanos(1);
        assertSame(t.toLocalTime(), LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // atOffset()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atOffset() {
        LocalDateTime t = LocalDateTime.of(2008, 6, 30, 11, 30);
        assertEquals(t.atOffset(OFFSET_PTWO), OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PTWO));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atOffset_nullZoneOffset() {
        LocalDateTime t = LocalDateTime.of(2008, 6, 30, 11, 30);
        t.atOffset((ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // atZone()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atZone() {
        LocalDateTime t = LocalDateTime.of(2008, 6, 30, 11, 30);
        assertEquals(t.atZone(ZONE_PARIS),
                ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30), ZONE_PARIS));
    }

    @Test(groups={"tck"})
    public void test_atZone_dstGap() {
        LocalDateTime t = LocalDateTime.of(2007, 4, 1, 0, 0);
        assertEquals(t.atZone(ZONE_GAZA),
                ZonedDateTime.of(LocalDateTime.of(2007, 4, 1, 1, 0), ZONE_GAZA));
    }

    @Test(groups={"tck"})
    public void test_atZone_dstOverlap() {
        LocalDateTime t = LocalDateTime.of(2007, 10, 28, 2, 30);
        assertEquals(t.atZone(ZONE_PARIS),
                ZonedDateTime.of(OffsetDateTime.of(2007, 10, 28, 2, 30, OFFSET_PTWO), ZONE_PARIS));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atZone_nullTimeZone() {
        LocalDateTime t = LocalDateTime.of(2008, 6, 30, 11, 30);
        t.atZone((ZoneId) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atZone_resolver() {
        LocalDateTime t = LocalDateTime.of(2008, 6, 30, 11, 30);
        assertEquals(t.atZone(ZONE_PARIS, ZoneResolvers.postTransition()),
                ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30), ZONE_PARIS));
    }

    @Test(groups={"tck"})
    public void test_atZone_resolver_dstGap() {
        LocalDateTime t = LocalDateTime.of(2007, 4, 1, 0, 0);
        assertEquals(t.atZone(ZONE_GAZA, ZoneResolvers.postTransition()),
                ZonedDateTime.of(LocalDateTime.of(2007, 4, 1, 1, 0), ZONE_GAZA));
    }

    @Test(groups={"tck"})
    public void test_atZone_resolver_dstGap_pre() {
        LocalDateTime t = LocalDateTime.of(2007, 4, 1, 0, 0);
        assertEquals(t.atZone(ZONE_GAZA, ZoneResolvers.preTransition()),
                ZonedDateTime.of(LocalDateTime.of(2007, 3, 31, 23, 59, 59, 999999999), ZONE_GAZA));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atZone_resolver_nullTimeZone() {
        LocalDateTime t = LocalDateTime.of(2008, 6, 30, 11, 30);
        t.atZone((ZoneId) null, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atZone_resolver_nullResolver() {
        LocalDateTime t = LocalDateTime.of(2008, 6, 30, 11, 30);
        t.atZone(ZONE_PARIS, (ZoneResolver) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atZone_resolver_badResolver() {
        LocalDateTime t = LocalDateTime.of(2007, 4, 1, 0, 0);
        t.atZone(ZONE_GAZA, new MockZoneResolverReturnsNull());
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates", groups={"implementation"})
    public void test_toLocalDate(int year, int month, int day) {
        LocalDate d = LocalDate.of(year, month, day);
        LocalDateTime dt = LocalDateTime.ofMidnight(d);
        assertSame(dt.toLocalDate(), d);
    }

    //-----------------------------------------------------------------------
    // toLocalTime()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes", groups={"implementation"})
    public void test_toLocalTime(int h, int m, int s, int ns) {
        LocalTime t = LocalTime.of(h, m, s, ns);
        LocalDateTime dt = LocalDateTime.of(LocalDate.of(2011, 7, 30), t);
        assertSame(dt.toLocalTime(), t);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_comparisons() {
        test_comparisons_LocalDateTime(
            LocalDate.of(Year.MIN_YEAR, 1, 1),
            LocalDate.of(Year.MIN_YEAR, 12, 31),
            LocalDate.of(-1, 1, 1),
            LocalDate.of(-1, 12, 31),
            LocalDate.of(0, 1, 1),
            LocalDate.of(0, 12, 31),
            LocalDate.of(1, 1, 1),
            LocalDate.of(1, 12, 31),
            LocalDate.of(2008, 1, 1),
            LocalDate.of(2008, 2, 29),
            LocalDate.of(2008, 12, 31),
            LocalDate.of(Year.MAX_YEAR, 1, 1),
            LocalDate.of(Year.MAX_YEAR, 12, 31)
        );
    }

    void test_comparisons_LocalDateTime(LocalDate... localDates) {
        test_comparisons_LocalDateTime(
            localDates,
            LocalTime.MIDNIGHT,
            LocalTime.of(0, 0, 0, 999999999),
            LocalTime.of(0, 0, 59, 0),
            LocalTime.of(0, 0, 59, 999999999),
            LocalTime.of(0, 59, 0, 0),
            LocalTime.of(0, 59, 59, 999999999),
            LocalTime.MIDDAY,
            LocalTime.of(12, 0, 0, 999999999),
            LocalTime.of(12, 0, 59, 0),
            LocalTime.of(12, 0, 59, 999999999),
            LocalTime.of(12, 59, 0, 0),
            LocalTime.of(12, 59, 59, 999999999),
            LocalTime.of(23, 0, 0, 0),
            LocalTime.of(23, 0, 0, 999999999),
            LocalTime.of(23, 0, 59, 0),
            LocalTime.of(23, 0, 59, 999999999),
            LocalTime.of(23, 59, 0, 0),
            LocalTime.of(23, 59, 59, 999999999)
        );
    }

    void test_comparisons_LocalDateTime(LocalDate[] localDates, LocalTime... localTimes) {
        LocalDateTime[] localDateTimes = new LocalDateTime[localDates.length * localTimes.length];
        int i = 0;

        for (LocalDate localDate : localDates) {
            for (LocalTime localTime : localTimes) {
                localDateTimes[i++] = LocalDateTime.of(localDate, localTime);
            }
        }

        doTest_comparisons_LocalDateTime(localDateTimes);
    }

    void doTest_comparisons_LocalDateTime(LocalDateTime[] localDateTimes) {
        for (int i = 0; i < localDateTimes.length; i++) {
            LocalDateTime a = localDateTimes[i];
            for (int j = 0; j < localDateTimes.length; j++) {
                LocalDateTime b = localDateTimes[j];
                if (i < j) {
                    assertTrue(a.compareTo(b) < 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertTrue(a.compareTo(b) > 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_compareTo_ObjectNull() {
        TEST_2007_07_15_12_30_40_987654321.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isBefore_ObjectNull() {
        TEST_2007_07_15_12_30_40_987654321.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isAfter_ObjectNull() {
        TEST_2007_07_15_12_30_40_987654321.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class, groups={"tck"})
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareToNonLocalDateTime() {
       Comparable c = TEST_2007_07_15_12_30_40_987654321;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDateTimes") 
    Iterator<Object[]> provider_sampleDateTimes() {
        return new Iterator<Object[]>() {
            Object[][] sampleDates = provider_sampleDates();
            Object[][] sampleTimes = provider_sampleTimes();
            int datesIndex = 0;
            int timesIndex = 0;

            public boolean hasNext() {
                return datesIndex < sampleDates.length;
            }

            public Object[] next() {
                Object[] sampleDate = sampleDates[datesIndex];
                Object[] sampleTime = sampleTimes[timesIndex];

                Object[] ret = new Object[sampleDate.length + sampleTime.length];

                System.arraycopy(sampleDate, 0, ret, 0, sampleDate.length);
                System.arraycopy(sampleTime, 0, ret, sampleDate.length, sampleTime.length);

                if (++timesIndex == sampleTimes.length) {
                    datesIndex++;
                    timesIndex = 0;
                }
                
                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="sampleDateTimes", groups={"tck"})
    public void test_equals_true(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.of(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.of(y, m, d, h, mi, s, n);
        assertTrue(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes", groups={"tck"})
    public void test_equals_false_year_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.of(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.of(y + 1, m, d, h, mi, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes", groups={"tck"})
    public void test_equals_false_month_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.of(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.of(y, m + 1, d, h, mi, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes", groups={"tck"})
    public void test_equals_false_day_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.of(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.of(y, m, d + 1, h, mi, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes", groups={"tck"})
    public void test_equals_false_hour_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.of(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.of(y, m, d, h + 1, mi, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes", groups={"tck"})
    public void test_equals_false_minute_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.of(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.of(y, m, d, h, mi + 1, s, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes", groups={"tck"})
    public void test_equals_false_second_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.of(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.of(y, m, d, h, mi, s + 1, n);
        assertFalse(a.equals(b));
    }

    @Test(dataProvider="sampleDateTimes", groups={"tck"})
    public void test_equals_false_nano_differs(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.of(y, m, d, h, mi, s, n);
        LocalDateTime b = LocalDateTime.of(y, m, d, h, mi, s, n + 1);
        assertFalse(a.equals(b));
    }
    
    @Test(groups={"tck"})
    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15_12_30_40_987654321.equals(TEST_2007_07_15_12_30_40_987654321), true);
    }

    @Test(groups={"tck"})
    public void test_equals_string_false() {
        assertEquals(TEST_2007_07_15_12_30_40_987654321.equals("2007-07-15T12:30:40.987654321"), false);
    }

    @Test(groups={"tck"})
    public void test_equals_null_false() {
        assertEquals(TEST_2007_07_15_12_30_40_987654321.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDateTimes", groups={"tck"})
    public void test_hashCode(int y, int m, int d, int h, int mi, int s, int n) {
        LocalDateTime a = LocalDateTime.of(y, m, d, h, mi, s, n);
        assertEquals(a.hashCode(), a.hashCode());
        LocalDateTime b = LocalDateTime.of(y, m, d, h, mi, s, n);
        assertEquals(a.hashCode(), b.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, 2, 1, 0, 0, "2008-07-05T02:01"},
            {2007, 12, 31, 23, 59, 1, 0, "2007-12-31T23:59:01"},
            {999, 12, 31, 23, 59, 59, 990000000, "0999-12-31T23:59:59.990"},
            {-1, 1, 2, 23, 59, 59, 999990000, "-0001-01-02T23:59:59.999990"},
            {-2008, 1, 2, 23, 59, 59, 999999990, "-2008-01-02T23:59:59.999999990"},
        };
    }

    @Test(dataProvider="sampleToString", groups={"tck"})
    public void test_toString(int y, int m, int d, int h, int mi, int s, int n, String expected) {
        LocalDateTime t = LocalDateTime.of(y, m, d, h, mi, s, n);
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString_formatter() {
        final LocalDateTime dateTime = LocalDateTime.of(2010, 12, 3, 11, 30, 45);
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                assertEquals(calendrical, dateTime);
                return "PRINTED";
            }
            @Override
            public <T> T parse(CharSequence text, Class<T> type) {
                throw new AssertionError();
            }
        };
        String t = dateTime.toString(f);
        assertEquals(t, "PRINTED");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_toString_formatter_null() {
        LocalDateTime.of(2010, 12, 3, 11, 30, 45).toString(null);
    }

}
