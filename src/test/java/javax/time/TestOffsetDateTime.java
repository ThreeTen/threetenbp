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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.CalendricalFormatter;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.MockFieldNoValue;
import javax.time.calendrical.MockZoneResolverReturnsNull;
import javax.time.calendrical.Year;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test OffsetDateTime.
 */
@Test
public class TestOffsetDateTime extends AbstractTest {

    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId ZONE_GAZA = ZoneId.of("Asia/Gaza");
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    private static final ZoneOffset OFFSET_MONE = ZoneOffset.ofHours(-1);
    private static final ZoneOffset OFFSET_MTWO = ZoneOffset.ofHours(-2);
    private OffsetDateTime TEST_2008_6_30_11_30_59_000000500;

    @BeforeMethod(groups={"tck","implementation"})
    public void setUp() {
        TEST_2008_6_30_11_30_59_000000500 = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        Object obj = TEST_2008_6_30_11_30_59_000000500;
        assertTrue(obj instanceof DateTime);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    @Test(groups={"tck"})
    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_2008_6_30_11_30_59_000000500);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_2008_6_30_11_30_59_000000500);
    }

    @Test(groups={"tck"})
    public void test_immutable() {
        Class<OffsetDateTime> cls = OffsetDateTime.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().contains("$") == false) {
                assertTrue(Modifier.isPrivate(field.getModifiers()));
                assertTrue(Modifier.isFinal(field.getModifiers()));
            }
        }
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void now() {
        OffsetDateTime expected = OffsetDateTime.now(Clock.systemDefaultZone());
        OffsetDateTime test = OffsetDateTime.now();
        long diff = Math.abs(test.toLocalTime().toNanoOfDay() - expected.toLocalTime().toNanoOfDay());
        if (diff >= 100000000) {
            // may be date change
            expected = OffsetDateTime.now(Clock.systemDefaultZone());
            test = OffsetDateTime.now();
            diff = Math.abs(test.toLocalTime().toNanoOfDay() - expected.toLocalTime().toNanoOfDay());
        }
        assertTrue(diff < 100000000);  // less than 0.1 secs
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void now_Clock_nullClock() {
        OffsetDateTime.now(null);
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i).plusNanos(123456789L);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            OffsetDateTime test = OffsetDateTime.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonth(), Month.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60 ? 1 : 2));
            assertEquals(test.getHour(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinute(), (i / 60) % 60);
            assertEquals(test.getSecond(), i % 60);
            assertEquals(test.getNano(), 123456789);
            assertEquals(test.getOffset(), ZoneOffset.UTC);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_offset() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i).plusNanos(123456789L);
            Clock clock = Clock.fixed(instant.minusSeconds(OFFSET_PONE.getTotalSeconds()), ZoneId.of(OFFSET_PONE));
            OffsetDateTime test = OffsetDateTime.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonth(), Month.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60) ? 1 : 2);
            assertEquals(test.getHour(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinute(), (i / 60) % 60);
            assertEquals(test.getSecond(), i % 60);
            assertEquals(test.getNano(), 123456789);
            assertEquals(test.getOffset(), OFFSET_PONE);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_beforeEpoch() {
        LocalTime expected = LocalTime.MIDNIGHT.plusNanos(123456789L);
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i).plusNanos(123456789L);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            OffsetDateTime test = OffsetDateTime.now(clock);
            assertEquals(test.getYear(), 1969);
            assertEquals(test.getMonth(), Month.DECEMBER);
            assertEquals(test.getDayOfMonth(), 31);
            expected = expected.minusSeconds(1);
            assertEquals(test.toLocalTime(), expected);
            assertEquals(test.getOffset(), ZoneOffset.UTC);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_offsets() {
        OffsetDateTime base = OffsetDateTime.of(1970, 1, 1, 12, 0, ZoneOffset.UTC);
        for (int i = -9; i < 15; i++) {
            ZoneOffset offset = ZoneOffset.ofHours(i);
            Clock clock = Clock.fixed(base.toInstant(), ZoneId.of(offset));
            OffsetDateTime test = OffsetDateTime.now(clock);
            assertEquals(test.getHour(), (12 + i) % 24);
            assertEquals(test.getMinute(), 0);
            assertEquals(test.getSecond(), 0);
            assertEquals(test.getNano(), 0);
            assertEquals(test.getOffset(), offset);
        }
    }

    //-----------------------------------------------------------------------
    // dateMidnight factories
    //-----------------------------------------------------------------------
    private void check(OffsetDateTime test, int y, int mo, int d, int h, int m, int s, int n, ZoneOffset offset) {
        assertEquals(test.getYear(), y);
        assertEquals(test.getMonth().getValue(), mo);
        assertEquals(test.getDayOfMonth(), d);
        assertEquals(test.getHour(), h);
        assertEquals(test.getMinute(), m);
        assertEquals(test.getSecond(), s);
        assertEquals(test.getNano(), n);
        assertEquals(test.getOffset(), offset);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofMidnight_intMonthInt() {
        OffsetDateTime test = OffsetDateTime.ofMidnight(2008, Month.JUNE, 30, OFFSET_PONE);
        check(test, 2008, 6, 30, 0, 0, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofMidnight_ints() {
        OffsetDateTime test = OffsetDateTime.ofMidnight(2008, 6, 30, OFFSET_PONE);
        check(test, 2008, 6, 30, 0, 0, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofMidnight_LocalDateZoneOffset() {
        LocalDate provider = LocalDate.of(2008, 6, 30);
        OffsetDateTime test = OffsetDateTime.ofMidnight(provider, OFFSET_PONE);
        check(test, 2008, 6, 30, 0, 0, 0, 0, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofMidnight_LocalDateZoneOffset_nullDate() {
        OffsetDateTime.ofMidnight((LocalDate) null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofMidnight_LocalDateZoneOffset_nullOffset() {
        LocalDate provider = LocalDate.of(2008, 6, 30);
        OffsetDateTime.ofMidnight(provider, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // dateTime factories
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intMonthIntHM() {
        OffsetDateTime test = OffsetDateTime.of(2008, Month.JUNE, 30, 11, 30, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intMonthIntHMS() {
        OffsetDateTime test = OffsetDateTime.of(2008, Month.JUNE, 30, 11, 30, 10, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intMonthIntHMSN() {
        OffsetDateTime test = OffsetDateTime.of(2008, Month.JUNE, 30, 11, 30, 10, 500, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intsHM() {
        OffsetDateTime test = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 0, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intsHMS() {
        OffsetDateTime test = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 0, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intsHMSN() {
        OffsetDateTime test = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_LocalDateLocalTimeZoneOffset() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        LocalTime time = LocalTime.of(11, 30, 10, 500);
        OffsetDateTime test = OffsetDateTime.of(date, time, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTimeZoneOffset_nullLocalDate() {
        LocalTime time = LocalTime.of(11, 30, 10, 500);
        OffsetDateTime.of((LocalDate) null, time, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTimeZoneOffset_nullLocalTime() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        OffsetDateTime.of(date, (LocalTime) null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTimeZoneOffset_nullOffset() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        LocalTime time = LocalTime.of(11, 30, 10, 500);
        OffsetDateTime.of(date, time, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_LocalDateTimeZoneOffset() {
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        OffsetDateTime test = OffsetDateTime.of(dt, OFFSET_PONE);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateTimeZoneOffset_nullProvider() {
        OffsetDateTime.of((LocalDateTime) null, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateTimeZoneOffset_nullOffset() {
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        OffsetDateTime.of(dt, (ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_LocalDateOffsetTime() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        OffsetTime time = OffsetTime.of(11, 30, 10, 500, OFFSET_PONE);
        OffsetDateTime test = OffsetDateTime.of(date, time);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_PONE);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateOffsetTime_nullLocalDate() {
        OffsetTime time = OffsetTime.of(11, 30, 10, 500, OFFSET_PONE);
        OffsetDateTime.of((LocalDate) null, time);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateOffsetTime_nullOffsetTime() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        OffsetDateTime.of(date, (OffsetTime) null);
    }

    //-----------------------------------------------------------------------
    // ofEpochSecond()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofEpochSecond_longOffset_afterEpoch() {
        for (int i = 0; i < 100000; i++) {
            OffsetDateTime test = OffsetDateTime.ofEpochSecond(i, OFFSET_PONE);
            assertEquals(test, OffsetDateTime.of(1970, 1, 1, 0, 0, ZoneOffset.UTC).withOffsetSameInstant(OFFSET_PONE).plusSeconds(i));
        }
    }

    @Test(groups={"tck"})
    public void factory_ofEpochSecond_longOffset_beforeEpoch() {
        for (int i = 0; i < 100000; i++) {
            OffsetDateTime test = OffsetDateTime.ofEpochSecond(-i, OFFSET_PONE);
            assertEquals(test, OffsetDateTime.of(1970, 1, 1, 0, 0, ZoneOffset.UTC).withOffsetSameInstant(OFFSET_PONE).minusSeconds(i));
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofEpochSecond_longOffset_tooBig() {
        OffsetDateTime.ofEpochSecond(Long.MAX_VALUE, OFFSET_PONE);  // TODO: better test
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofEpochSecond_longOffset_tooSmall() {
        OffsetDateTime.ofEpochSecond(Long.MIN_VALUE, OFFSET_PONE);  // TODO: better test
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofEpochSecond_longOffset_nullOffset() {
        OffsetDateTime.ofEpochSecond(0L, null);
    }

    //-----------------------------------------------------------------------
    // from()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(OffsetDateTime.from(OffsetDateTime.of(2007, 7, 15, 17, 30, OFFSET_PONE)), OffsetDateTime.of(2007, 7, 15, 17, 30, OFFSET_PONE));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        OffsetDateTime.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_Calendricals_null() {
        OffsetDateTime.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleToString", groups={"tck"})
//    public void test_parse(int y, int month, int d, int h, int m, int s, int n, String offsetId, String text) {
//        OffsetDateTime t = OffsetDateTime.parse(text);
//        assertEquals(t.getYear(), y);
//        assertEquals(t.getMonth().getValue(), month);
//        assertEquals(t.getDayOfMonth(), d);
//        assertEquals(t.getHour(), h);
//        assertEquals(t.getMinute(), m);
//        assertEquals(t.getSecond(), s);
//        assertEquals(t.getNano(), n);
//        assertEquals(t.getOffset().getID(), offsetId);
//    }

//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_illegalValue() {
//        OffsetDateTime.parse("2008-06-32T11:15+01:00");
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_invalidValue() {
//        OffsetDateTime.parse("2008-06-31T11:15+01:00");
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void factory_parse_nullText() {
//        OffsetDateTime.parse((String) null);
//    }

    //-----------------------------------------------------------------------
    // parse(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_parse_formatter() {
        final OffsetDateTime dateTime = OffsetDateTime.of(2010, 12, 3, 11, 30, ZoneOffset.ofHours(1));
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
        OffsetDateTime test = OffsetDateTime.parse("ANY", f);
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
        OffsetDateTime.parse((String) null, f);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_parse_formatter_nullFormatter() {
        OffsetDateTime.parse("ANY", null);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void constructor_nullTime() throws Throwable  {
        Constructor<OffsetDateTime> con = OffsetDateTime.class.getDeclaredConstructor(LocalDateTime.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(null, OFFSET_PONE);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void constructor_nullOffset() throws Throwable  {
        Constructor<OffsetDateTime> con = OffsetDateTime.class.getDeclaredConstructor(LocalDateTime.class, ZoneOffset.class);
        con.setAccessible(true);
        try {
            con.newInstance(LocalDateTime.of(2008, 6, 30, 11, 30), null);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {2008, 6, 30, 11, 30, 20, 500, OFFSET_PONE},
            {2008, 6, 30, 11, 0, 0, 0, OFFSET_PONE},
            {2008, 6, 30, 23, 59, 59, 999999999, OFFSET_PONE},
            {-1, 1, 1, 0, 0, 0, 0, OFFSET_PONE},
        };
    }

    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_get(int y, int o, int d, int h, int m, int s, int n, ZoneOffset offset) {
        LocalDate localDate = LocalDate.of(y, o, d);
        LocalTime localTime = LocalTime.of(h, m, s, n);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        OffsetDateTime a = OffsetDateTime.of(localDateTime, offset);
        
        assertEquals(a.getYear(), localDate.getYear());
        assertEquals(a.getMonth(), localDate.getMonth());
        assertEquals(a.getDayOfMonth(), localDate.getDayOfMonth());
        assertEquals(a.getDayOfYear(), localDate.getDayOfYear());
        assertEquals(a.getDayOfWeek(), localDate.getDayOfWeek());
        
        assertEquals(a.getHour(), localDateTime.getHour());
        assertEquals(a.getMinute(), localDateTime.getMinute());
        assertEquals(a.getSecond(), localDateTime.getSecond());
        assertEquals(a.getNano(), localDateTime.getNano());
        
        assertEquals(a.toOffsetDate(), OffsetDate.of(localDate, offset));
        assertEquals(a.toOffsetTime(), OffsetTime.of(localTime, offset));
        assertEquals(a.toString(), localDateTime.toString() + offset.toString());
    }
    
    @Test(dataProvider="sampleTimes", groups={"implementation"})
    public void test_get_same(int y, int o, int d, int h, int m, int s, int n, ZoneOffset offset) {
        LocalDate localDate = LocalDate.of(y, o, d);
        LocalTime localTime = LocalTime.of(h, m, s, n);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        OffsetDateTime a = OffsetDateTime.of(localDateTime, offset);
    
        assertSame(a.getOffset(), offset);
        assertSame(a.toLocalDate(), localDate);
        assertSame(a.toLocalTime(), localTime);
        assertSame(a.toLocalDateTime(), localDateTime);
    }

    //-----------------------------------------------------------------------
    // get(DateTimeField)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_get_DateTimeField() {
        OffsetDateTime test = OffsetDateTime.of(2008, 6, 30, 12, 30, 40, 987654321, OFFSET_PONE);
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
        OffsetDateTime test = OffsetDateTime.of(2008, 6, 30, 12, 30, 40, 987654321, OFFSET_PONE);
        test.get((DateTimeField) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_get_DateTimeField_invalidField() {
        TEST_2008_6_30_11_30_59_000000500.get(MockFieldNoValue.INSTANCE);
    }

    //-----------------------------------------------------------------------
    // extract(Class)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_extract_Class() {
        OffsetDateTime test = OffsetDateTime.of(2008, 6, 30, 12, 30, 40, 987654321, OFFSET_PONE);
        assertEquals(test.extract(LocalDate.class), test.toLocalDate());
        assertEquals(test.extract(LocalTime.class), test.toLocalTime());
        assertEquals(test.extract(LocalDateTime.class), test.toLocalDateTime());
        assertEquals(test.extract(OffsetDate.class), test.toOffsetDate());
        assertEquals(test.extract(OffsetTime.class), test.toOffsetTime());
        assertEquals(test.extract(OffsetDateTime.class), test);
        assertEquals(test.extract(ZonedDateTime.class), null);
        assertEquals(test.extract(ZoneOffset.class), test.getOffset());
        assertEquals(test.extract(ZoneId.class), null);
        assertEquals(test.extract(Instant.class), test.toInstant());
        assertEquals(test.extract(Class.class), OffsetDateTime.class);
        assertEquals(test.extract(String.class), null);
        assertEquals(test.extract(BigDecimal.class), null);
        assertEquals(test.extract(null), null);
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_with_adjustment() {
        final OffsetDateTime sample = OffsetDateTime.of(2012, 3, 4, 23, 5, OFFSET_PONE);
        DateTimeAdjuster adjuster = new DateTimeAdjuster() {
            @Override
            public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
                return sample;
            }
        };
        assertEquals(TEST_2008_6_30_11_30_59_000000500.with(adjuster), sample);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_with_adjustment_null() {
        TEST_2008_6_30_11_30_59_000000500.with((DateTimeAdjuster) null);
    }

    //-----------------------------------------------------------------------
    // withOffsetSameLocal()
    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_withOffsetSameLocal() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameLocal(OFFSET_PTWO);
        assertSame(test.toLocalDateTime(), base.toLocalDateTime());
        assertSame(test.getOffset(), OFFSET_PTWO);
    }

    @Test(groups={"implementation"})
    public void test_withOffsetSameLocal_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameLocal(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withOffsetSameLocal_null() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.withOffsetSameLocal(null);
    }

    //-----------------------------------------------------------------------
    // withOffsetSameInstant()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withOffsetSameInstant() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameInstant(OFFSET_PTWO);
        OffsetDateTime expected = OffsetDateTime.of(2008, 6, 30, 12, 30, 59, OFFSET_PTWO);
        assertEquals(test, expected);
    }

    @Test(groups={"implementation"})
    public void test_withOffsetSameInstant_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withOffsetSameInstant(OFFSET_PONE);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withOffsetSameInstant_null() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        base.withOffsetSameInstant(null);
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withYear_normal() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withYear(2007);
        assertEquals(test, OffsetDateTime.of(2007, 6, 30, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_withYear_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withYear(2008);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withMonth_normal() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMonth(1);
        assertEquals(test, OffsetDateTime.of(2008, 1, 30, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_withMonth_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMonth(6);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDayOfMonth_normal() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withDayOfMonth(15);
        assertEquals(test, OffsetDateTime.of(2008, 6, 15, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_withDayOfMonth_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withDayOfMonth(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDayOfYear(int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDayOfYear_normal() {
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.withDayOfYear(33);
        assertEquals(t, OffsetDateTime.of(2008, 2, 2, 11, 30, 59, 500, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_withDayOfYear_noChange() {
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.withDayOfYear(31 + 29 + 31 + 30 + 31 + 30);
        assertSame(t, TEST_2008_6_30_11_30_59_000000500);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfYear_illegal() {
        TEST_2008_6_30_11_30_59_000000500.withDayOfYear(367);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfYear_invalid() {
        OffsetDateTime.of(2007, 2, 2, 11, 30, OFFSET_PONE).withDayOfYear(366);
    }

    //-----------------------------------------------------------------------
    // withDate()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDate() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withDate(2007, 1, 1);
        OffsetDateTime expected = OffsetDateTime.of(2007, 1, 1, 11, 30, 59, OFFSET_PONE);
        assertEquals(test, expected);
    }

    @Test(groups={"implementation"})
    public void test_withDate_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PONE);
        OffsetDateTime test = base.withDate(2008, 6, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withHour()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withHour_normal() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withHour(15);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 15, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_withHour_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withHour(11);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMinute()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withMinute_normal() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMinute(15);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 11, 15, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_withMinute_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withMinute(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withSecond()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withSecond_normal() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withSecond(15);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 11, 30, 15, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_withSecond_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withSecond(59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withNano()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withNanoOfSecond_normal() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 1, OFFSET_PONE);
        OffsetDateTime test = base.withNano(15);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 15, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 1, OFFSET_PONE);
        OffsetDateTime test = base.withNano(1);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withTime()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withTime_HM() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.withTime(12, 10);
        OffsetDateTime expected = OffsetDateTime.of(2008, 6, 30, 12, 10, 0, 0, OFFSET_PONE);
        assertEquals(test, expected);
    }

    @Test(groups={"implementation"})
    public void test_withTime_HM_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PONE);
        OffsetDateTime test = base.withTime(11, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withTime_HMS() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.withTime(12, 10, 9);
        OffsetDateTime expected = OffsetDateTime.of(2008, 6, 30, 12, 10, 9, 0, OFFSET_PONE);
        assertEquals(test, expected);
    }

    @Test(groups={"implementation"})
    public void test_withTime_HMS_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withTime(11, 30, 59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withTime_HMSN() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.withTime(12, 10, 9, 8);
        OffsetDateTime expected = OffsetDateTime.of(2008, 6, 30, 12, 10, 9, 8, OFFSET_PONE);
        assertEquals(test, expected);
    }

    @Test(groups={"implementation"})
    public void test_withTime_HMSN_noChange() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 500, OFFSET_PONE);
        OffsetDateTime test = base.withTime(11, 30, 59, 500);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Period() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.plus(period);
        assertEquals(t, OffsetDateTime.of(2009, 1, 30, 11, 30, 59, 500, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plus_Period_zero() {
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.plus(Period.ZERO_DAYS);
        assertSame(t, TEST_2008_6_30_11_30_59_000000500);
    }

    //-----------------------------------------------------------------------
    // plus(Duration)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Duration() {
        Duration dur = Duration.ofSeconds(62, 3);
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.plus(dur);
        assertEquals(t, OffsetDateTime.of(2008, 6, 30, 11, 32, 1, 503, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plus_Duration_zero() {
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.plus(Duration.ZERO);
        assertSame(t, TEST_2008_6_30_11_30_59_000000500);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_Duration_null() {
        TEST_2008_6_30_11_30_59_000000500.plus((Duration) null);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusYears() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusYears(1);
        assertEquals(test, OffsetDateTime.of(2009, 6, 30, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plusYears_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusYears(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusMonths() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMonths(1);
        assertEquals(test, OffsetDateTime.of(2008, 7, 30, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plusMonths_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMonths(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusWeeks() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusWeeks(1);
        assertEquals(test, OffsetDateTime.of(2008, 7, 7, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plusWeeks_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusDays() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusDays(1);
        assertEquals(test, OffsetDateTime.of(2008, 7, 1, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plusDays_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusHours() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusHours(13);
        assertEquals(test, OffsetDateTime.of(2008, 7, 1, 0, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plusHours_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusMinutes() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMinutes(30);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 12, 0, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusSeconds() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusSeconds(1);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 11, 31, 0, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusNanos() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.plusNanos(1);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 1, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.plusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Period() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.minus(period);
        assertEquals(t, OffsetDateTime.of(2007, 11, 30, 11, 30, 59, 500, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minus_Period_zero() {
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.minus(Period.ZERO_DAYS);
        assertSame(t, TEST_2008_6_30_11_30_59_000000500);
    }

    //-----------------------------------------------------------------------
    // minus(Duration)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Duration() {
        Duration dur = Duration.ofSeconds(62, 3);
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.minus(dur);
        assertEquals(t, OffsetDateTime.of(2008, 6, 30, 11, 29, 57, 497, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minus_Duration_zero() {
        OffsetDateTime t = TEST_2008_6_30_11_30_59_000000500.minus(Duration.ZERO);
        assertSame(t, TEST_2008_6_30_11_30_59_000000500);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_Duration_null() {
        TEST_2008_6_30_11_30_59_000000500.minus((Duration) null);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusYears() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusYears(1);
        assertEquals(test, OffsetDateTime.of(2007, 6, 30, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minusYears_zero() {
        OffsetDateTime base = OffsetDateTime.of(2007, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusYears(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusMonths() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMonths(1);
        assertEquals(test, OffsetDateTime.of(2008, 5, 30, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minusMonths_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMonths(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusWeeks() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusWeeks(1);
        assertEquals(test, OffsetDateTime.of(2008, 6, 23, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minusWeeks_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusDays() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusDays(1);
        assertEquals(test, OffsetDateTime.of(2008, 6, 29, 11, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minusDays_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusHours() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusHours(13);
        assertEquals(test, OffsetDateTime.of(2008, 6, 29, 22, 30, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minusHours_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusMinutes() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMinutes(30);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 11, 0, 59, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusSeconds() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusSeconds(1);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 11, 30, 58, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusNanos() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 0, OFFSET_PONE);
        OffsetDateTime test = base.minusNanos(1);
        assertEquals(test, OffsetDateTime.of(2008, 6, 30, 11, 30, 58, 999999999, OFFSET_PONE));
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_zero() {
        OffsetDateTime base = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        OffsetDateTime test = base.minusNanos(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // atZoneSameInstant()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atZone() {
        OffsetDateTime t = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_MTWO);
        assertEquals(t.atZoneSameInstant(ZONE_PARIS),
                ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 15, 30), ZONE_PARIS));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atZone_nullTimeZone() {
        OffsetDateTime t = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PTWO);
        t.atZoneSameInstant((ZoneId) null);
    }

    //-----------------------------------------------------------------------
    // atZoneSimilarLocal()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atZoneSimilarLocal() {
        OffsetDateTime t = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_MTWO);
        assertEquals(t.atZoneSimilarLocal(ZONE_PARIS),
                ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30), ZONE_PARIS));
    }

    @Test(groups={"tck"})
    public void test_atZoneSimilarLocal_dstGap() {
        OffsetDateTime t = OffsetDateTime.of(2007, 4, 1, 0, 0, OFFSET_MTWO);
        assertEquals(t.atZoneSimilarLocal(ZONE_GAZA),
                ZonedDateTime.of(LocalDateTime.of(2007, 4, 1, 1, 0), ZONE_GAZA));
    }

    @Test(groups={"tck"})
    public void test_atZone_dstOverlapSummer() {
        OffsetDateTime t = OffsetDateTime.of(2007, 10, 28, 2, 30, OFFSET_PTWO);
        assertEquals(t.atZoneSimilarLocal(ZONE_PARIS).toOffsetDateTime(), t);
        assertEquals(t.atZoneSimilarLocal(ZONE_PARIS).getZone(), ZONE_PARIS);
    }

    @Test(groups={"tck"})
    public void test_atZone_dstOverlapWinter() {
        OffsetDateTime t = OffsetDateTime.of(2007, 10, 28, 2, 30, OFFSET_PONE);
        assertEquals(t.atZoneSimilarLocal(ZONE_PARIS).toOffsetDateTime(), t);
        assertEquals(t.atZoneSimilarLocal(ZONE_PARIS).getZone(), ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atZoneSimilarLocal_nullTimeZone() {
        OffsetDateTime t = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PTWO);
        t.atZoneSimilarLocal((ZoneId) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atZoneSimilarLocal_resolver() {
        OffsetDateTime t = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_MTWO);
        assertEquals(t.atZoneSimilarLocal(ZONE_PARIS, ZoneResolvers.postTransition()),
                ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30), ZONE_PARIS));
    }

    @Test(groups={"tck"})
    public void test_atZoneSimilarLocal_resolver_dstGap() {
        OffsetDateTime t = OffsetDateTime.of(2007, 4, 1, 0, 0, OFFSET_MTWO);
        assertEquals(t.atZoneSimilarLocal(ZONE_GAZA, ZoneResolvers.postTransition()),
                ZonedDateTime.of(LocalDateTime.of(2007, 4, 1, 1, 0), ZONE_GAZA));
    }

    @Test(groups={"tck"})
    public void test_atZoneSimilarLocal_resolver_dstGap_pre() {
        OffsetDateTime t = OffsetDateTime.of(2007, 4, 1, 0, 0, OFFSET_MTWO);
        assertEquals(t.atZoneSimilarLocal(ZONE_GAZA, ZoneResolvers.preTransition()),
                ZonedDateTime.of(LocalDateTime.of(2007, 3, 31, 23, 59, 59, 999999999), ZONE_GAZA));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atZoneSimilarLocal_resolver_nullTimeZone() {
        OffsetDateTime t = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PTWO);
        t.atZoneSimilarLocal((ZoneId) null, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atZoneSimilarLocal_resolver_nullResolver() {
        OffsetDateTime t = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PTWO);
        t.atZoneSimilarLocal(ZONE_PARIS, (ZoneResolver) null);
    }

    @Test(expectedExceptions=RuntimeException.class, groups={"tck"})
    public void test_atZoneSimilarLocal_resolver_badResolver() {
        OffsetDateTime t = OffsetDateTime.of(2007, 4, 1, 0, 0, OFFSET_PTWO);
        t.atZoneSimilarLocal(ZONE_GAZA, new MockZoneResolverReturnsNull());
    }

    //-----------------------------------------------------------------------
    // toEpochSecond()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toEpochSecond_afterEpoch() {
        for (int i = 0; i < 100000; i++) {
            OffsetDateTime a = OffsetDateTime.of(1970, 1, 1, 0, 0, ZoneOffset.UTC).plusSeconds(i);
            assertEquals(a.toEpochSecond(), i);
        }
    }

    @Test(groups={"tck"})
    public void test_toEpochSecond_beforeEpoch() {
        for (int i = 0; i < 100000; i++) {
            OffsetDateTime a = OffsetDateTime.of(1970, 1, 1, 0, 0, ZoneOffset.UTC).minusSeconds(i);
            assertEquals(a.toEpochSecond(), -i);
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_compareTo_timeMins() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 29, 3, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 30, 2, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_timeSecs() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 29, 2, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 29, 3, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_timeNanos() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 29, 40, 4, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 29, 40, 5, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_offset() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PTWO);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_offsetNanos() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 30, 40, 6, OFFSET_PTWO);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 30, 40, 5, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_both() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 50, OFFSET_PTWO);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 20, OFFSET_PONE);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_bothNanos() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 20, 40, 4, OFFSET_PTWO);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 10, 20, 40, 5, OFFSET_PONE);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) < 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_hourDifference() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 10, 0, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 0, OFFSET_PTWO);  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
        assertEquals(a.toInstant().compareTo(b.toInstant()) == 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_max() {
        OffsetDateTime a = OffsetDateTime.of(Year.MAX_YEAR, 12, 31, 23, 59, OFFSET_MONE);
        OffsetDateTime b = OffsetDateTime.of(Year.MAX_YEAR, 12, 31, 23, 59, OFFSET_MTWO);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_min() {
        OffsetDateTime a = OffsetDateTime.of(Year.MIN_YEAR, 1, 1, 0, 0, OFFSET_PTWO);
        OffsetDateTime b = OffsetDateTime.of(Year.MIN_YEAR, 1, 1, 0, 0, OFFSET_PONE);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_compareTo_null() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.compareTo(null);
    }

    @Test(expectedExceptions=ClassCastException.class, groups={"tck"})
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareToNonOffsetDateTime() {
       Comparable c = TEST_2008_6_30_11_30_59_000000500;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // isAfter() / isBefore() / equalInstant()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isBeforeIsAfterIsEqual1() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 30, 58, 3, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 2, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.equalInstant(b), false);
        assertEquals(a.isAfter(b), false);
        
        assertEquals(b.isBefore(a), false);
        assertEquals(b.equalInstant(a), false);
        assertEquals(b.isAfter(a), true);
        
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
        
        assertEquals(a.equalInstant(a), true);
        assertEquals(b.equalInstant(b), true);
        
        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    @Test(groups={"tck"})
    public void test_isBeforeIsAfterIsEqual2() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 2, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, 3, OFFSET_PONE);  // a is before b due to time
        assertEquals(a.isBefore(b), true);
        assertEquals(a.equalInstant(b), false);
        assertEquals(a.isAfter(b), false);
        
        assertEquals(b.isBefore(a), false);
        assertEquals(b.equalInstant(a), false);
        assertEquals(b.isAfter(a), true);
        
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
        
        assertEquals(a.equalInstant(a), true);
        assertEquals(b.equalInstant(b), true);
        
        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    @Test(groups={"tck"})
    public void test_isBeforeIsAfterIsEqual_instantComparison() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 10, 0, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(2008, 6, 30, 11, 0, OFFSET_PTWO);  // a is same instant as b
        assertEquals(a.isBefore(b), false);
        assertEquals(a.equalInstant(b), true);
        assertEquals(a.isAfter(b), false);
        
        assertEquals(b.isBefore(a), false);
        assertEquals(b.equalInstant(a), true);
        assertEquals(b.isAfter(a), false);
        
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
        
        assertEquals(a.equalInstant(a), true);
        assertEquals(b.equalInstant(b), true);
        
        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isBefore_null() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isEqual_null() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.equalInstant(null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isAfter_null() {
        OffsetDateTime a = OffsetDateTime.of(2008, 6, 30, 11, 30, 59, OFFSET_PONE);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_true(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetDateTime a = OffsetDateTime.of(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(y, o, d, h, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_year_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetDateTime a = OffsetDateTime.of(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(y + 1, o, d, h, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_hour_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        h = (h == 23 ? 22 : h);
        OffsetDateTime a = OffsetDateTime.of(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(y, o, d, h + 1, m, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_minute_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        m = (m == 59 ? 58 : m);
        OffsetDateTime a = OffsetDateTime.of(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(y, o, d, h, m + 1, s, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_second_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        s = (s == 59 ? 58 : s);
        OffsetDateTime a = OffsetDateTime.of(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(y, o, d, h, m, s + 1, n, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_nano_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        n = (n == 999999999 ? 999999998 : n);
        OffsetDateTime a = OffsetDateTime.of(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(y, o, d, h, m, s, n + 1, OFFSET_PONE);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_offset_differs(int y, int o, int d, int h, int m, int s, int n, ZoneOffset ignored) {
        OffsetDateTime a = OffsetDateTime.of(y, o, d, h, m, s, n, OFFSET_PONE);
        OffsetDateTime b = OffsetDateTime.of(y, o, d, h, m, s, n, OFFSET_PTWO);
        assertEquals(a.equals(b), false);
    }

    @Test(groups={"tck"})
    public void test_equals_itself_true() {
        assertEquals(TEST_2008_6_30_11_30_59_000000500.equals(TEST_2008_6_30_11_30_59_000000500), true);
    }

    @Test(groups={"tck"})
    public void test_equals_string_false() {
        assertEquals(TEST_2008_6_30_11_30_59_000000500.equals("2007-07-15"), false);
    }

    @Test(groups={"tck"})
    public void test_equals_null_false() {
        assertEquals(TEST_2008_6_30_11_30_59_000000500.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 6, 30, 11, 30, 59, 0, "Z", "2008-06-30T11:30:59Z"},
            {2008, 6, 30, 11, 30, 59, 0, "+01:00", "2008-06-30T11:30:59+01:00"},
            {2008, 6, 30, 11, 30, 59, 999000000, "Z", "2008-06-30T11:30:59.999Z"},
            {2008, 6, 30, 11, 30, 59, 999000000, "+01:00", "2008-06-30T11:30:59.999+01:00"},
            {2008, 6, 30, 11, 30, 59, 999000, "Z", "2008-06-30T11:30:59.000999Z"},
            {2008, 6, 30, 11, 30, 59, 999000, "+01:00", "2008-06-30T11:30:59.000999+01:00"},
            {2008, 6, 30, 11, 30, 59, 999, "Z", "2008-06-30T11:30:59.000000999Z"},
            {2008, 6, 30, 11, 30, 59, 999, "+01:00", "2008-06-30T11:30:59.000000999+01:00"},
        };
    }

    @Test(dataProvider="sampleToString", groups={"tck"})
    public void test_toString(int y, int o, int d, int h, int m, int s, int n, String offsetId, String expected) {
        OffsetDateTime t = OffsetDateTime.of(y, o, d, h, m, s, n, ZoneOffset.of(offsetId));
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString_formatter() {
        final OffsetDateTime dateTime = OffsetDateTime.of(2010, 12, 3, 11, 30, OFFSET_PONE);
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
        OffsetDateTime.of(2010, 12, 3, 11, 30, OFFSET_PONE).toString(null);
    }

}
