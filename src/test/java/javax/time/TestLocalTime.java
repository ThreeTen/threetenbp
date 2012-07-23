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
import javax.time.calendrical.PeriodUnit;
import javax.time.format.CalendricalFormatter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalTime.
 */
@Test
public class TestLocalTime {

    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);

    private LocalTime TEST_12_30_40_987654321;

    @BeforeMethod(groups={"tck","implementation"})
    public void setUp() {
        TEST_12_30_40_987654321 = LocalTime.of(12, 30, 40, 987654321);
    }

    //-----------------------------------------------------------------------
    private void check(LocalTime time, int h, int m, int s, int n) {
        assertEquals(time.getHour(), h);
        assertEquals(time.getMinute(), m);
        assertEquals(time.getSecond(), s);
        assertEquals(time.getNano(), n);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        Object obj = TEST_12_30_40_987654321;
        assertTrue(obj instanceof DateTime);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    @Test(groups={"tck"})
    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_12_30_40_987654321);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_immutable() {
        Class<LocalTime> cls = LocalTime.class;
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
    @Test(groups={"tck","implementation"})
    public void constant_MIDNIGHT() {
        check(LocalTime.MIDNIGHT, 0, 0, 0, 0);
    }
    
    @Test(groups={"implementation"})
    public void constant_MIDNIGHT_same() {
    	assertSame(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);
        assertSame(LocalTime.MIDNIGHT, LocalTime.of(0, 0));
    }

    @Test(groups={"tck"})
    public void constant_MIDNIGHT_equal() {
    	assertEquals(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);
        assertEquals(LocalTime.MIDNIGHT, LocalTime.of(0, 0));
    }
    
    @Test(groups={"tck","implementation"})
    public void constant_MIDDAY() {
        check(LocalTime.MIDDAY, 12, 0, 0, 0);
    }
    
    @Test(groups={"implementation"})
    public void constant_MIDDAY_same() {
    	assertSame(LocalTime.MIDDAY, LocalTime.MIDDAY);
        assertSame(LocalTime.MIDDAY, LocalTime.of(12, 0));
    }
    
    @Test(groups={"tck"})
    public void constant_MIDDAY_equal() {
    	assertEquals(LocalTime.MIDDAY, LocalTime.MIDDAY);
        assertEquals(LocalTime.MIDDAY, LocalTime.of(12, 0));
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck","implementation"})
    public void constant_MIN_TIME() {
        check(LocalTime.MIN_TIME, 0, 0, 0, 0);
    }
    
    @Test(groups={"implementation"})
    public void constant_MIN_TIME_same() {
    	assertSame(LocalTime.MIN_TIME, LocalTime.of(0, 0));
    }
    
    @Test(groups={"tck"})
    public void constant_MIN_TIME_equal() {
    	assertEquals(LocalTime.MIN_TIME, LocalTime.of(0, 0));
    }

    @Test(groups={"tck","implementation"})
    public void constant_MAX_TIME() {
        check(LocalTime.MAX_TIME, 23, 59, 59, 999999999);     
    }
    
    @Test(groups={"implementation"})
    public void constant_MAX_TIME_same() {
    	assertSame(LocalTime.MIDDAY, LocalTime.MIDDAY);
        assertSame(LocalTime.MIDDAY, LocalTime.of(12, 0));
    }
    
    @Test(groups={"tck"})
    public void constant_MAX_TIME_equal() {
    	assertEquals(LocalTime.MIDDAY, LocalTime.MIDDAY);
        assertEquals(LocalTime.MIDDAY, LocalTime.of(12, 0));
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void now() {
        LocalTime expected = LocalTime.now(Clock.systemDefaultZone());
        LocalTime test = LocalTime.now();
        long diff = Math.abs(test.toNanoOfDay() - expected.toNanoOfDay());
        assertTrue(diff < 100000000);  // less than 0.1 secs
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void now_Clock_nullClock() {
        LocalTime.now(null);
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i, 8);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            LocalTime test = LocalTime.now(clock);
            assertEquals(test.getHour(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinute(), (i / 60) % 60);
            assertEquals(test.getSecond(), i % 60);
            assertEquals(test.getNano(), 8);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_beforeEpoch() {
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i, 8);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            LocalTime test = LocalTime.now(clock);
            assertEquals(test.getHour(), ((i + 24 * 60 * 60) / (60 * 60)) % 24);
            assertEquals(test.getMinute(), ((i + 24 * 60 * 60) / 60) % 60);
            assertEquals(test.getSecond(), (i + 24 * 60 * 60) % 60);
            assertEquals(test.getNano(), 8);
        }
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void now_Clock_maxYear() {
        Clock clock = Clock.fixed(Instant.ofEpochSecond(Long.MAX_VALUE), ZoneId.UTC);
        LocalTime test = LocalTime.now(clock);
        int hour = (int) ((Long.MAX_VALUE / (60 * 60)) % 24);
        int min = (int) ((Long.MAX_VALUE / 60) % 60);
        int sec = (int) (Long.MAX_VALUE % 60);
        assertEquals(test.getHour(), hour);
        assertEquals(test.getMinute(), min);
        assertEquals(test.getSecond(), sec);
        assertEquals(test.getNano(), 0);
    }

    @Test(groups={"tck"})
    public void now_Clock_minYear() {
        long oneDay = 24 * 60 * 60;
        long addition = ((Long.MAX_VALUE / oneDay) + 2) * oneDay;
        
        Clock clock = Clock.fixed(Instant.ofEpochSecond(Long.MIN_VALUE), ZoneId.UTC);
        LocalTime test = LocalTime.now(clock);
        long added = Long.MIN_VALUE + addition;
        int hour = (int) ((added / (60 * 60)) % 24);
        int min = (int) ((added / 60) % 60);
        int sec = (int) (added % 60);
        assertEquals(test.getHour(), hour);
        assertEquals(test.getMinute(), min);
        assertEquals(test.getSecond(), sec);
        assertEquals(test.getNano(), 0);
    }

    //-----------------------------------------------------------------------
    // of() factories
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_time_2ints() {
        LocalTime test = LocalTime.of(12, 30);
        check(test, 12, 30, 0, 0);
    }

    @Test(groups={"implementation"})
    public void factory_time_2ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_2ints_hourTooLow() {
        LocalTime.of(-1, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_2ints_hourTooHigh() {
        LocalTime.of(24, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_2ints_minuteTooLow() {
        LocalTime.of(0, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_2ints_minuteTooHigh() {
        LocalTime.of(0, 60);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_time_3ints() {
        LocalTime test = LocalTime.of(12, 30, 40);
        check(test, 12, 30, 40, 0);
    }

    @Test(groups={"implementation"})
    public void factory_time_3ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0, 0);
            LocalTime test2 = LocalTime.of(i, 0, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_3ints_hourTooLow() {
        LocalTime.of(-1, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_3ints_hourTooHigh() {
        LocalTime.of(24, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_3ints_minuteTooLow() {
        LocalTime.of(0, -1, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_3ints_minuteTooHigh() {
        LocalTime.of(0, 60, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_3ints_secondTooLow() {
        LocalTime.of(0, 0, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_3ints_secondTooHigh() {
        LocalTime.of(0, 0, 60);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_time_4ints() {
        LocalTime test = LocalTime.of(12, 30, 40, 987654321);
        check(test, 12, 30, 40, 987654321);
        test = LocalTime.of(12, 0, 40, 987654321);
        check(test, 12, 0, 40, 987654321);
    }

    @Test(groups={"implementation"})
    public void factory_time_4ints_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.of(i, 0, 0, 0);
            LocalTime test2 = LocalTime.of(i, 0, 0, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_4ints_hourTooLow() {
        LocalTime.of(-1, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_4ints_hourTooHigh() {
        LocalTime.of(24, 0, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_4ints_minuteTooLow() {
        LocalTime.of(0, -1, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_4ints_minuteTooHigh() {
        LocalTime.of(0, 60, 0, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_4ints_secondTooLow() {
        LocalTime.of(0, 0, -1, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_4ints_secondTooHigh() {
        LocalTime.of(0, 0, 60, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_4ints_nanoTooLow() {
        LocalTime.of(0, 0, 0, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_time_4ints_nanoTooHigh() {
        LocalTime.of(0, 0, 0, 1000000000);
    }

    //-----------------------------------------------------------------------
    // ofSecondOfDay(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofSecondOfDay() {
        LocalTime localTime = LocalTime.ofSecondOfDay(2 * 60 * 60 + 17 * 60 + 23);
        check(localTime, 2, 17, 23, 0);
    }

    @Test(groups={"implementation"})
    public void factory_ofSecondOfDay_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofSecondOfDay(i * 60L * 60L);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofSecondOfDay_tooLow() {
        LocalTime.ofSecondOfDay(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofSecondOfDay_tooHigh() {
        LocalTime.ofSecondOfDay(24 * 60 * 60);
    }

    //-----------------------------------------------------------------------
    // ofSecondOfDay(long, int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofSecondOfDay_long_int() {
        LocalTime localTime = LocalTime.ofSecondOfDay(2 * 60 * 60 + 17 * 60 + 23, 987);
        check(localTime, 2, 17, 23, 987);
    }

    @Test(groups={"implementation"})
    public void factory_ofSecondOfDay7_long_int_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofSecondOfDay(i * 60L * 60L, 0);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofSecondOfDay_long_int_tooLowSecs() {
        LocalTime.ofSecondOfDay(-1, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofSecondOfDay_long_int_tooHighSecs() {
        LocalTime.ofSecondOfDay(24 * 60 * 60, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofSecondOfDay_long_int_tooLowNanos() {
        LocalTime.ofSecondOfDay(0, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofSecondOfDay_long_int_tooHighNanos() {
        LocalTime.ofSecondOfDay(0, 1000000000);
    }

    //-----------------------------------------------------------------------
    // ofNanoOfDay(long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofNanoOfDay() {
        LocalTime localTime = LocalTime.ofNanoOfDay(60 * 60 * 1000000000L + 17);
        check(localTime, 1, 0, 0, 17);
    }

    @Test(groups={"implementation"})
    public void factory_ofNanoOfDay_singletons() {
        for (int i = 0; i < 24; i++) {
            LocalTime test1 = LocalTime.ofNanoOfDay(i * 1000000000L * 60L * 60L);
            LocalTime test2 = LocalTime.of(i, 0);
            assertSame(test1, test2);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofNanoOfDay_tooLow() {
        LocalTime.ofNanoOfDay(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofNanoOfDay_tooHigh() {
        LocalTime.ofNanoOfDay(24 * 60 * 60 * 1000000000L);
    }

    //-----------------------------------------------------------------------
    // from()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(LocalTime.from(LocalTime.of(17, 30)), LocalTime.of(17, 30));
        assertEquals(LocalTime.from(LocalDateTime.of(2012, 5, 1, 17, 30)), LocalTime.of(17, 30));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        LocalTime.from(LocalDate.of(2007, 7, 15));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        LocalTime.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
//    @Test(dataProvider = "sampleToString", groups={"tck"})
//    public void factory_parse_validText(int h, int m, int s, int n, String parsable) {
//        LocalTime t = LocalTime.parse(parsable);
//        assertNotNull(t, parsable);
//        assertEquals(t.getHour(), h);
//        assertEquals(t.getMinute(), m);
//        assertEquals(t.getSecond(), s);
//        assertEquals(t.getNano(), n);
//    }

    @DataProvider(name="sampleBadParse")
    Object[][] provider_sampleBadParse() {
        return new Object[][]{
                {"00;00"},
                {"12-00"},
                {"-01:00"},
                {"00:00:00-09"},
                {"00:00:00,09"},
                {"00:00:abs"},
                {"11"},
                {"11:30+01:00"},
                {"11:30+01:00[Europe/Paris]"},
        };
    }

//    @Test(dataProvider = "sampleBadParse", expectedExceptions={CalendricalParseException.class}, groups={"tck"})
//    public void factory_parse_invalidText(String unparsable) {
//        LocalTime.parse(unparsable);
//    }
//
//    //-----------------------------------------------------------------------s
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_illegalHour() {
//        LocalTime.parse("25:00");
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_illegalMinute() {
//        LocalTime.parse("12:60");
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_illegalSecond() {
//        LocalTime.parse("12:12:60");
//    }
//
//    //-----------------------------------------------------------------------s
//    @Test(expectedExceptions = {NullPointerException.class}, groups={"tck"})
//    public void factory_parse_nullTest() {
//        LocalTime.parse((String) null);
//    }

    //-----------------------------------------------------------------------
    // parse(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_parse_formatter() {
        final LocalTime time = LocalTime.of(12, 30, 40);
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                throw new AssertionError();
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object parse(CharSequence text, Class type) {
                return time;
            }
        };
        LocalTime test = LocalTime.parse("ANY", f);
        assertEquals(test, time);
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
        LocalTime.parse((String) null, f);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_parse_formatter_nullFormatter() {
        LocalTime.parse("ANY", null);
    }

    //-----------------------------------------------------------------------
    // get(DateTimeField)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_get_DateTimeField() {
        LocalTime test = TEST_12_30_40_987654321;
        assertEquals(test.get(LocalDateTimeField.HOUR_OF_DAY), 12);
        assertEquals(test.get(LocalDateTimeField.MINUTE_OF_HOUR), 30);
        assertEquals(test.get(LocalDateTimeField.SECOND_OF_MINUTE), 40);
        assertEquals(test.get(LocalDateTimeField.NANO_OF_SECOND), 987654321);
        
        assertEquals(test.get(LocalDateTimeField.SECOND_OF_DAY), 12 * 3600 + 30 * 60 + 40);
        assertEquals(test.get(LocalDateTimeField.MINUTE_OF_DAY), 12 * 60 + 30);
        assertEquals(test.get(LocalDateTimeField.HOUR_OF_AMPM), 0);
        assertEquals(test.get(LocalDateTimeField.CLOCK_HOUR_OF_AMPM), 12);
        assertEquals(test.get(LocalDateTimeField.CLOCK_HOUR_OF_DAY), 12);
        assertEquals(test.get(LocalDateTimeField.AMPM_OF_DAY), AmPm.PM.getValue());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"} )
    public void test_get_DateTimeField_null() {
        TEST_12_30_40_987654321.get((DateTimeField) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_get_DateTimeField_invalidField() {
        TEST_12_30_40_987654321.get(MockFieldNoValue.INSTANCE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_get_DateTimeField_dateField() {
        TEST_12_30_40_987654321.get(LocalDateTimeField.DAY_OF_MONTH);
    }

    //-----------------------------------------------------------------------
    // extract(Class)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_extract_Class() {
        LocalTime test = TEST_12_30_40_987654321;
        assertEquals(test.extract(LocalDate.class), null);
        assertEquals(test.extract(LocalTime.class), test);
        assertEquals(test.extract(LocalDateTime.class), null);
        assertEquals(test.extract(OffsetDate.class), null);
        assertEquals(test.extract(OffsetTime.class), null);
        assertEquals(test.extract(OffsetDateTime.class), null);
        assertEquals(test.extract(ZonedDateTime.class), null);
        assertEquals(test.extract(ZoneOffset.class), null);
        assertEquals(test.extract(ZoneId.class), null);
        assertEquals(test.extract(Instant.class), null);
        assertEquals(test.extract(Class.class), LocalTime.class);
        assertEquals(test.extract(String.class), null);
        assertEquals(test.extract(BigDecimal.class), null);
        assertEquals(test.extract(null), null);
    }

    //-----------------------------------------------------------------------
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
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_get(int h, int m, int s, int ns) {
        LocalTime a = LocalTime.of(h, m, s, ns);
        assertEquals(a.getHour(), h);
        assertEquals(a.getMinute(), m);
        assertEquals(a.getSecond(), s);
        assertEquals(a.getNano(), ns);
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_with_adjustment() {
        final LocalTime sample = LocalTime.of(23, 5);
        DateTimeAdjuster adjuster = new DateTimeAdjuster() {
            @Override
            public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
                return sample;
            }
        };
        assertEquals(TEST_12_30_40_987654321.with(adjuster), sample);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_with_adjustment_null() {
        TEST_12_30_40_987654321.with((DateTimeAdjuster) null);
    }

    //-----------------------------------------------------------------------
    // withHour()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withHour_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        for (int i = 0; i < 24; i++) {
            t = t.withHour(i);
            assertEquals(t.getHour(), i);
        }
    }

    @Test(groups={"implementation"})
    public void test_withHour_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.withHour(12);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_withHour_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.withHour(12);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withHour_toMidnight_same() {
        LocalTime t = LocalTime.of(1, 0).withHour(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_withHour_toMidnight_equal() {
        LocalTime t = LocalTime.of(1, 0).withHour(0);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withHour_toMidday_same() {
        LocalTime t = LocalTime.of(1, 0).withHour(12);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_withHour_toMidday_equal() {
        LocalTime t = LocalTime.of(1, 0).withHour(12);
        assertEquals(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withHour_hourTooLow() {
        TEST_12_30_40_987654321.withHour(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withHour_hourTooHigh() {
        TEST_12_30_40_987654321.withHour(24);
    }

    //-----------------------------------------------------------------------
    // withMinute()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withMinute_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withMinute(i);
            assertEquals(t.getMinute(), i);
        }
    }

    @Test(groups={"implementation"})
    public void test_withMinute_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.withMinute(30);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_withMinute_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.withMinute(30);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 1).withMinute(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_withMinute_toMidnight_equal() {
        LocalTime t = LocalTime.of(0, 1).withMinute(0);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withMinute_toMidday_same() {
        LocalTime t = LocalTime.of(12, 1).withMinute(0);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_withMinute_toMidday_equals() {
        LocalTime t = LocalTime.of(12, 1).withMinute(0);
        assertEquals(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withMinute_minuteTooLow() {
        TEST_12_30_40_987654321.withMinute(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withMinute_minuteTooHigh() {
        TEST_12_30_40_987654321.withMinute(60);
    }

    //-----------------------------------------------------------------------
    // withSecond()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withSecond_normal() {
        LocalTime t = TEST_12_30_40_987654321;
        for (int i = 0; i < 60; i++) {
            t = t.withSecond(i);
            assertEquals(t.getSecond(), i);
        }
    }

    @Test(groups={"implementation"})
    public void test_withSecond_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.withSecond(40);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_withSecond_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.withSecond(40);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 0, 1).withSecond(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_withSecond_toMidnight_equal() {
        LocalTime t = LocalTime.of(0, 0, 1).withSecond(0);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withSecond_toMidday_same() {
        LocalTime t = LocalTime.of(12, 0, 1).withSecond(0);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_withSecond_toMidday_equal() {
        LocalTime t = LocalTime.of(12, 0, 1).withSecond(0);
        assertEquals(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withSecond_secondTooLow() {
        TEST_12_30_40_987654321.withSecond(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withSecond_secondTooHigh() {
        TEST_12_30_40_987654321.withSecond(60);
    }

    //-----------------------------------------------------------------------
    // withNano()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withNanoOfSecond_normal() {
        LocalTime t = TEST_12_30_40_987654321;
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
    public void test_withNanoOfSecond_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.withNano(987654321);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_withNanoOfSecond_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.withNano(987654321);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 0, 0, 1).withNano(0);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_withNanoOfSecond_toMidnight_equal() {
        LocalTime t = LocalTime.of(0, 0, 0, 1).withNano(0);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_toMidday_same() {
        LocalTime t = LocalTime.of(12, 0, 0, 1).withNano(0);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_withNanoOfSecond_toMidday_equal() {
        LocalTime t = LocalTime.of(12, 0, 0, 1).withNano(0);
        assertEquals(t, LocalTime.MIDDAY);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withNanoOfSecond_nanoTooLow() {
        TEST_12_30_40_987654321.withNano(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withNanoOfSecond_nanoTooHigh() {
        TEST_12_30_40_987654321.withNano(1000000000);
    }

    //-----------------------------------------------------------------------
    // plus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Period_positiveHours() {
        Period period = Period.of(7, LocalPeriodUnit.HOURS);
        LocalTime t = TEST_12_30_40_987654321.plus(period);
        assertEquals(t, LocalTime.of(19, 30, 40, 987654321));
    }

    @Test(groups={"tck"})
    public void test_plus_Period_negativeMinutes() {
        Period period = Period.of(-25, LocalPeriodUnit.MINUTES);
        LocalTime t = TEST_12_30_40_987654321.plus(period);
        assertEquals(t, LocalTime.of(12, 5, 40, 987654321));
    }

    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_plus_Period_dateNotAllowed() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        TEST_12_30_40_987654321.plus(period);
    }

    @Test(groups={"implementation"})
    public void test_plus_Period_zero() {
        LocalTime t = TEST_12_30_40_987654321.plus(Period.ZERO_SECONDS);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_Period_null() {
        TEST_12_30_40_987654321.plus((Period) null);
    }

    //-----------------------------------------------------------------------
    // plus(long,PeriodUnit)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_longPeriodUnit_positiveHours() {
        LocalTime t = TEST_12_30_40_987654321.plus(7, LocalPeriodUnit.HOURS);
        assertEquals(t, LocalTime.of(19, 30, 40, 987654321));
    }
 
    @Test(groups={"tck"})
    public void test_plus_longPeriodUnit_negativeMinutes() {
        LocalTime t = TEST_12_30_40_987654321.plus(-25, LocalPeriodUnit.MINUTES);
        assertEquals(t, LocalTime.of(12, 5, 40, 987654321));
    }

    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_plus_longPeriodUnit_dateNotAllowed() {
        TEST_12_30_40_987654321.plus(7, LocalPeriodUnit.MONTHS);
    }

    @Test(groups={"implementation"})
    public void test_plus_longPeriodUnit_zero() {
        LocalTime t = TEST_12_30_40_987654321.plus(0, LocalPeriodUnit.SECONDS);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_longPeriodUnit_null() {
        TEST_12_30_40_987654321.plus(1, (PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // plus(Duration)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Duration() {
        Duration dur = Duration.ofSeconds(62, 3);
        LocalTime t = TEST_12_30_40_987654321.plus(dur);
        assertEquals(t, LocalTime.of(12, 31, 42, 987654324));
    }

    @Test(groups={"tck"})
    public void test_plus_Duration_big1() {
        Duration dur = Duration.ofSeconds(Long.MAX_VALUE, 999999999);
        LocalTime t = TEST_12_30_40_987654321.plus(dur);
        assertEquals(t, TEST_12_30_40_987654321.plusSeconds(Long.MAX_VALUE).plusNanos(999999999));
    }

    @Test(groups={"tck"})
    public void test_plus_Duration_big2() {
        Duration dur = Duration.ofSeconds(999, Long.MAX_VALUE);
        LocalTime t = TEST_12_30_40_987654321.plus(dur);
        assertEquals(t, TEST_12_30_40_987654321.plusSeconds(999).plusNanos(Long.MAX_VALUE));
    }

    @Test(groups={"implementation"})
    public void test_plus_Duration_zero_same() {
        LocalTime t = TEST_12_30_40_987654321.plus(Duration.ZERO);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_plus_Duration_zero_equal() {
        LocalTime t = TEST_12_30_40_987654321.plus(Duration.ZERO);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_plus_Duration_wrap() {
        Duration dur = Duration.ofHours(1);
        LocalTime t = LocalTime.of(23, 30).plus(dur);
        assertEquals(t, LocalTime.of(0, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_Duration_null() {
        TEST_12_30_40_987654321.plus((Duration) null);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusHours_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        for (int i = 0; i < 50; i++) {
            t = t.plusHours(1);
            assertEquals(t.getHour(), (i + 1) % 24);
        }
    }

    @Test(groups={"tck"})
    public void test_plusHours_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.plusHours(i);
            assertEquals(t.getHour(), (i + 72) % 24);
        }
    }

    @Test(groups={"tck"})
    public void test_plusHours_fromOne() {
        LocalTime base = LocalTime.of(1, 0);
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.plusHours(i);
            assertEquals(t.getHour(), (1 + i + 72) % 24);
        }
    }

    @Test(groups={"implementation"})
    public void test_plusHours_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.plusHours(0);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_plusHours_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.plusHours(0);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_toMidnight_same() {
        LocalTime t = LocalTime.of(23, 0).plusHours(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_plusHours_toMidnight_equal() {
        LocalTime t = LocalTime.of(23, 0).plusHours(1);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusHours_toMidday_same() {
        LocalTime t = LocalTime.of(11, 0).plusHours(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"tck"})
    public void test_plusHours_toMidday_equal() {
        LocalTime t = LocalTime.of(11, 0).plusHours(1);
        assertEquals(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_plusHours_big() {
        LocalTime t = LocalTime.of(2, 30).plusHours(Long.MAX_VALUE);
        int hours = (int) (Long.MAX_VALUE % 24L);
        assertEquals(t, LocalTime.of(2, 30).plusHours(hours));
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusMinutes_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        int hour = 0;
        int min = 0;
        for (int i = 0; i < 70; i++) {
            t = t.plusMinutes(1);
            min++;
            if (min == 60) {
                hour++;
                min = 0;
            }
            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
        }
    }

    @Test(groups={"tck"})
    public void test_plusMinutes_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        int hour;
        int min;
        for (int i = -70; i < 70; i++) {
            LocalTime t = base.plusMinutes(i);
            if (i < -60) {
                hour = 22;
                min = i + 120;
            } else if (i < 0) {
                hour = 23;
                min = i + 60;
            } else if (i >= 60) {
                hour = 1;
                min = i - 60;
            } else {
                hour = 0;
                min = i;
            }
            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
        }
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(0);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_plusMinutes_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(0);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(24 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_plusMinutes_noChange_oneDay_equal() {
        LocalTime t = TEST_12_30_40_987654321.plusMinutes(24 * 60);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_toMidnight_same() {
        LocalTime t = LocalTime.of(23, 59).plusMinutes(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_plusMinutes_toMidnight_equal() {
        LocalTime t = LocalTime.of(23, 59).plusMinutes(1);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_toMidday_same() {
        LocalTime t = LocalTime.of(11, 59).plusMinutes(1);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_plusMinutes_toMidday_equal() {
        LocalTime t = LocalTime.of(11, 59).plusMinutes(1);
        assertEquals(t, LocalTime.MIDDAY);
    }

    @Test(groups={"tck"})
    public void test_plusMinutes_big() {
        LocalTime t = LocalTime.of(2, 30).plusMinutes(Long.MAX_VALUE);
        int mins = (int) (Long.MAX_VALUE % (24L * 60L));
        assertEquals(t, LocalTime.of(2, 30).plusMinutes(mins));
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusSeconds_one() {
        LocalTime t = LocalTime.MIDNIGHT;
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
            int hour = 22;
            int min = 59;
            int sec = 0;

            public boolean hasNext() {
                return i <= 3660;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, hour, min, sec};
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

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="plusSeconds_fromZero", groups={"tck"})
    public void test_plusSeconds_fromZero(int seconds, int hour, int min, int sec) {
        LocalTime base = LocalTime.MIDNIGHT;
        LocalTime t = base.plusSeconds(seconds);

        assertEquals(hour, t.getHour());
        assertEquals(min, t.getMinute());
        assertEquals(sec, t.getSecond());
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.plusSeconds(0);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_plusSeconds_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.plusSeconds(0);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.plusSeconds(24 * 60 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_plusSeconds_noChange_oneDay_equal() {
        LocalTime t = TEST_12_30_40_987654321.plusSeconds(24 * 60 * 60);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_toMidnight_same() {
        LocalTime t = LocalTime.of(23, 59, 59).plusSeconds(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_plusSeconds_toMidnight_equal() {
        LocalTime t = LocalTime.of(23, 59, 59).plusSeconds(1);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_toMidday_same() {
        LocalTime t = LocalTime.of(11, 59, 59).plusSeconds(1);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_plusSeconds_toMidday_equal() {
        LocalTime t = LocalTime.of(11, 59, 59).plusSeconds(1);
        assertEquals(t, LocalTime.MIDDAY);
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_big() {
        LocalTime t = LocalTime.of(2, 30).plusSeconds(Long.MAX_VALUE);
        int secs = (int) (Long.MAX_VALUE % (24L * 60L * 60L));
        assertEquals(t, LocalTime.of(2, 30).plusSeconds(secs));
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusNanos_halfABillion() {
        LocalTime t = LocalTime.MIDNIGHT;
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
            int hour = 22;
            int min = 59;
            int sec = 0;
            long nanos = 0;

            public boolean hasNext() {
                return i <= 3660 * 1000000000L;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, hour, min, sec, (int)nanos};
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
    public void test_plusNanos_fromZero(long nanoseconds, int hour, int min, int sec, int nanos) {
        LocalTime base = LocalTime.MIDNIGHT;
        LocalTime t = base.plusNanos(nanoseconds);

        assertEquals(hour, t.getHour());
        assertEquals(min, t.getMinute());
        assertEquals(sec, t.getSecond());
        assertEquals(nanos, t.getNano());
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.plusNanos(0);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_plusNanos_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.plusNanos(0);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.plusNanos(24 * 60 * 60 * 1000000000L);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_plusNanos_noChange_oneDay_equal() {
        LocalTime t = TEST_12_30_40_987654321.plusNanos(24 * 60 * 60 * 1000000000L);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_toMidnight_same() {
        LocalTime t = LocalTime.of(23, 59, 59, 999999999).plusNanos(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_plusNanos_toMidnight_equal() {
        LocalTime t = LocalTime.of(23, 59, 59, 999999999).plusNanos(1);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_toMidday_same() {
        LocalTime t = LocalTime.of(11, 59, 59, 999999999).plusNanos(1);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_plusNanos_toMidday_equal() {
        LocalTime t = LocalTime.of(11, 59, 59, 999999999).plusNanos(1);
        assertEquals(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // minus(Duration)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Duration() {
        Duration dur = Duration.ofSeconds(62, 3);
        LocalTime t = TEST_12_30_40_987654321.minus(dur);
        assertEquals(t, LocalTime.of(12, 29, 38, 987654318));
    }

    @Test(groups={"tck"})
    public void test_minus_Duration_big1() {
        Duration dur = Duration.ofSeconds(Long.MAX_VALUE, 999999999);
        LocalTime t = TEST_12_30_40_987654321.minus(dur);
        assertEquals(t, TEST_12_30_40_987654321.minusSeconds(Long.MAX_VALUE).minusNanos(999999999));
    }

    @Test(groups={"tck"})
    public void test_minus_Duration_big2() {
        Duration dur = Duration.ofSeconds(999, Long.MAX_VALUE);
        LocalTime t = TEST_12_30_40_987654321.minus(dur);
        assertEquals(t, TEST_12_30_40_987654321.minusSeconds(999).minusNanos(Long.MAX_VALUE));
    }

    @Test(groups={"implementation"})
    public void test_minus_Duration_zero_same() {
        LocalTime t = TEST_12_30_40_987654321.minus(Duration.ZERO);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_minus_Duration_zero_equal() {
        LocalTime t = TEST_12_30_40_987654321.minus(Duration.ZERO);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"tck"})
    public void test_minus_Duration_wrap() {
        Duration dur = Duration.ofHours(1);
        LocalTime t = LocalTime.of(0, 30).minus(dur);
        assertEquals(t, LocalTime.of(23, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_Duration_null() {
        TEST_12_30_40_987654321.minus((Duration) null);
    }

    //-----------------------------------------------------------------------
    // minus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Period_positiveHours() {
        Period period = Period.of(7, LocalPeriodUnit.HOURS);
        LocalTime t = TEST_12_30_40_987654321.minus(period);
        assertEquals(t, LocalTime.of(5, 30, 40, 987654321));
    }

    @Test(groups={"tck"})
    public void test_minus_Period_negativeMinutes() {
        Period period = Period.of(-25, LocalPeriodUnit.MINUTES);
        LocalTime t = TEST_12_30_40_987654321.minus(period);
        assertEquals(t, LocalTime.of(12, 55, 40, 987654321));
    }

    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_minus_Period_dateNowAllowed() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        TEST_12_30_40_987654321.minus(period);
    }

    @Test(groups={"implementation"})
    public void test_minus_Period_zero() {
        LocalTime t = TEST_12_30_40_987654321.minus(Period.ZERO_SECONDS);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_Period_null() {
        TEST_12_30_40_987654321.minus((Period) null);
    }

    //-----------------------------------------------------------------------
    // minus(long,PeriodUnit)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_longPeriodUnit_positiveHours() {
        LocalTime t = TEST_12_30_40_987654321.minus(7, LocalPeriodUnit.HOURS);
        assertEquals(t, LocalTime.of(5, 30, 40, 987654321));
    }
 
    @Test(groups={"tck"})
    public void test_minus_longPeriodUnit_negativeMinutes() {
        LocalTime t = TEST_12_30_40_987654321.minus(-25, LocalPeriodUnit.MINUTES);
        assertEquals(t, LocalTime.of(12, 55, 40, 987654321));
    }

    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_minus_longPeriodUnit_dateNotAllowed() {
        TEST_12_30_40_987654321.plus(7, LocalPeriodUnit.DAYS);
    }

    @Test(groups={"implementation"})
    public void test_minus_longPeriodUnit_zero() {
        LocalTime t = TEST_12_30_40_987654321.minus(0, LocalPeriodUnit.SECONDS);
        assertSame(t, TEST_12_30_40_987654321);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_longPeriodUnit_null() {
        TEST_12_30_40_987654321.minus(1, (PeriodUnit) null);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusHours_one() {
        LocalTime t = LocalTime.MIDNIGHT;
        for (int i = 0; i < 50; i++) {
            t = t.minusHours(1);
            assertEquals(t.getHour(), (((-i + 23) % 24) + 24) % 24, String.valueOf(i));
        }
    }

    @Test(groups={"tck"})
    public void test_minusHours_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.minusHours(i);
            assertEquals(t.getHour(), ((-i % 24) + 24) % 24);
        }
    }

    @Test(groups={"tck"})
    public void test_minusHours_fromOne() {
        LocalTime base = LocalTime.of(1, 0);
        for (int i = -50; i < 50; i++) {
            LocalTime t = base.minusHours(i);
            assertEquals(t.getHour(), (1 + (-i % 24) + 24) % 24);
        }
    }

    @Test(groups={"implementation"})
    public void test_minusHours_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.minusHours(0);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_minusHours_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.minusHours(0);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_toMidnight_same() {
        LocalTime t = LocalTime.of(1, 0).minusHours(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_minusHours_toMidnight_equal() {
        LocalTime t = LocalTime.of(1, 0).minusHours(1);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusHours_toMidday_same() {
        LocalTime t = LocalTime.of(13, 0).minusHours(1);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_minusHours_toMidday_equal() {
        LocalTime t = LocalTime.of(13, 0).minusHours(1);
        assertEquals(t, LocalTime.MIDDAY);
    }

    @Test(groups={"tck"})
    public void test_minusHours_big() {
        LocalTime t = LocalTime.of(2, 30).minusHours(Long.MAX_VALUE);
        int hours = (int) (Long.MAX_VALUE % 24L);
        assertEquals(t, LocalTime.of(2, 30).minusHours(hours));
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusMinutes_one() {
        LocalTime t = LocalTime.MIDNIGHT;
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
            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
        }
    }

    @Test(groups={"tck"})
    public void test_minusMinutes_fromZero() {
        LocalTime base = LocalTime.MIDNIGHT;
        int hour = 22;
        int min = 49;
        for (int i = 70; i > -70; i--) {
            LocalTime t = base.minusMinutes(i);
            min++;
            
            if (min == 60) {
                hour++;
                min = 0;
                
                if (hour == 24) {
                    hour = 0;
                }
            }

            assertEquals(t.getHour(), hour);
            assertEquals(t.getMinute(), min);
        }
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.minusMinutes(0);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_minusMinutes_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.minusMinutes(0);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.minusMinutes(24 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_minusMinutes_noChange_oneDay_equal() {
        LocalTime t = TEST_12_30_40_987654321.minusMinutes(24 * 60);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 1).minusMinutes(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_minusMinutes_toMidnight_equal() {
        LocalTime t = LocalTime.of(0, 1).minusMinutes(1);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_toMidday_same() {
        LocalTime t = LocalTime.of(12, 1).minusMinutes(1);
        assertSame(t, LocalTime.MIDDAY);
    }

    @Test(groups={"tck"})
    public void test_minusMinutes_toMidday_equals() {
        LocalTime t = LocalTime.of(12, 1).minusMinutes(1);
        assertEquals(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_minusMinutes_big() {
        LocalTime t = LocalTime.of(2, 30).minusMinutes(Long.MAX_VALUE);
        int mins = (int) (Long.MAX_VALUE % (24L * 60L));
        assertEquals(t, LocalTime.of(2, 30).minusMinutes(mins));
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusSeconds_one() {
        LocalTime t = LocalTime.MIDNIGHT;
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
            int hour = 22;
            int min = 59;
            int sec = 0;

            public boolean hasNext() {
                return i >= -3660;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, hour, min, sec};
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

                return ret;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Test(dataProvider="minusSeconds_fromZero", groups={"tck"})
    public void test_minusSeconds_fromZero(int seconds, int hour, int min, int sec) {
        LocalTime base = LocalTime.MIDNIGHT;
        LocalTime t = base.minusSeconds(seconds);

        assertEquals(t.getHour(), hour);
        assertEquals(t.getMinute(), min);
        assertEquals(t.getSecond(), sec);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.minusSeconds(0);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_minusSeconds_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.minusSeconds(0);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.minusSeconds(24 * 60 * 60);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_minusSeconds_noChange_oneDay_equal() {
        LocalTime t = TEST_12_30_40_987654321.minusSeconds(24 * 60 * 60);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 0, 1).minusSeconds(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_minusSeconds_toMidnight_equal() {
        LocalTime t = LocalTime.of(0, 0, 1).minusSeconds(1);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_toMidday_same() {
        LocalTime t = LocalTime.of(12, 0, 1).minusSeconds(1);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_minusSeconds_toMidday_equal() {
        LocalTime t = LocalTime.of(12, 0, 1).minusSeconds(1);
        assertEquals(t, LocalTime.MIDDAY);
    }

    @Test(groups={"tck"})
    public void test_minusSeconds_big() {
        LocalTime t = LocalTime.of(2, 30).minusSeconds(Long.MAX_VALUE);
        int secs = (int) (Long.MAX_VALUE % (24L * 60L * 60L));
        assertEquals(t, LocalTime.of(2, 30).minusSeconds(secs));
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusNanos_halfABillion() {
        LocalTime t = LocalTime.MIDNIGHT;
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
            int hour = 22;
            int min = 59;
            int sec = 0;
            long nanos = 0;

            public boolean hasNext() {
                return i >= -3660 * 1000000000L;
            }

            public Object[] next() {
                final Object[] ret = new Object[] {i, hour, min, sec, (int)nanos};
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
    public void test_minusNanos_fromZero(long nanoseconds, int hour, int min, int sec, int nanos) {
        LocalTime base = LocalTime.MIDNIGHT;
        LocalTime t = base.minusNanos(nanoseconds);

        assertEquals(hour, t.getHour());
        assertEquals(min, t.getMinute());
        assertEquals(sec, t.getSecond());
        assertEquals(nanos, t.getNano());
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_noChange_same() {
        LocalTime t = TEST_12_30_40_987654321.minusNanos(0);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_minusNanos_noChange_equal() {
        LocalTime t = TEST_12_30_40_987654321.minusNanos(0);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_noChange_oneDay_same() {
        LocalTime t = TEST_12_30_40_987654321.minusNanos(24 * 60 * 60 * 1000000000L);
        assertSame(t, TEST_12_30_40_987654321);
    }
    
    @Test(groups={"tck"})
    public void test_minusNanos_noChange_oneDay_equal() {
        LocalTime t = TEST_12_30_40_987654321.minusNanos(24 * 60 * 60 * 1000000000L);
        assertEquals(t, TEST_12_30_40_987654321);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_toMidnight_same() {
        LocalTime t = LocalTime.of(0, 0, 0, 1).minusNanos(1);
        assertSame(t, LocalTime.MIDNIGHT);
    }
    
    @Test(groups={"tck"})
    public void test_minusNanos_toMidnight_equal() {
        LocalTime t = LocalTime.of(0, 0, 0, 1).minusNanos(1);
        assertEquals(t, LocalTime.MIDNIGHT);
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_toMidday_same() {
        LocalTime t = LocalTime.of(12, 0, 0, 1).minusNanos(1);
        assertSame(t, LocalTime.MIDDAY);
    }
    
    @Test(groups={"tck"})
    public void test_minusNanos_toMidday_equal() {
        LocalTime t = LocalTime.of(12, 0, 0, 1).minusNanos(1);
        assertEquals(t, LocalTime.MIDDAY);
    }

    //-----------------------------------------------------------------------
    // atOffset()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atOffset() {
        LocalTime t = LocalTime.of(11, 30);
        assertEquals(t.atOffset(OFFSET_PTWO), OffsetTime.of(11, 30, OFFSET_PTWO));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atOffset_nullZoneOffset() {
        LocalTime t = LocalTime.of(11, 30);
        t.atOffset((ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // toSecondOfDay()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toSecondOfDay() {
        LocalTime t = LocalTime.of(0, 0);
        for (int i = 0; i < 24 * 60 * 60; i++) {
            assertEquals(t.toSecondOfDay(), i);
            t = t.plusSeconds(1);
        }
    }

    @Test(groups={"tck"})
    public void test_toSecondOfDay_fromNanoOfDay_symmetry() {
        LocalTime t = LocalTime.of(0, 0);
        for (int i = 0; i < 24 * 60 * 60; i++) {
            assertEquals(LocalTime.ofSecondOfDay(t.toSecondOfDay()), t);
            t = t.plusSeconds(1);
        }
    }

    //-----------------------------------------------------------------------
    // toNanoOfDay()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toNanoOfDay() {
        LocalTime t = LocalTime.of(0, 0);
        for (int i = 0; i < 1000000; i++) {
            assertEquals(t.toNanoOfDay(), i);
            t = t.plusNanos(1);
        }
        t = LocalTime.of(0, 0);
        for (int i = 1; i <= 1000000; i++) {
            t = t.minusNanos(1);
            assertEquals(t.toNanoOfDay(), 24 * 60 * 60 * 1000000000L - i);
        }
    }

    @Test(groups={"tck"})
    public void test_toNanoOfDay_fromNanoOfDay_symmetry() {
        LocalTime t = LocalTime.of(0, 0);
        for (int i = 0; i < 1000000; i++) {
            assertEquals(LocalTime.ofNanoOfDay(t.toNanoOfDay()), t);
            t = t.plusNanos(1);
        }
        t = LocalTime.of(0, 0);
        for (int i = 1; i <= 1000000; i++) {
            t = t.minusNanos(1);
            assertEquals(LocalTime.ofNanoOfDay(t.toNanoOfDay()), t);
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_comparisons() {
        doTest_comparisons_LocalTime(
            LocalTime.MIDNIGHT,
            LocalTime.of(0, 0, 0, 999999999),
            LocalTime.of(0, 0, 59, 0),
            LocalTime.of(0, 0, 59, 999999999),
            LocalTime.of(0, 59, 0, 0),
            LocalTime.of(0, 59, 0, 999999999),
            LocalTime.of(0, 59, 59, 0),
            LocalTime.of(0, 59, 59, 999999999),
            LocalTime.MIDDAY,
            LocalTime.of(12, 0, 0, 999999999),
            LocalTime.of(12, 0, 59, 0),
            LocalTime.of(12, 0, 59, 999999999),
            LocalTime.of(12, 59, 0, 0),
            LocalTime.of(12, 59, 0, 999999999),
            LocalTime.of(12, 59, 59, 0),
            LocalTime.of(12, 59, 59, 999999999),
            LocalTime.of(23, 0, 0, 0),
            LocalTime.of(23, 0, 0, 999999999),
            LocalTime.of(23, 0, 59, 0),
            LocalTime.of(23, 0, 59, 999999999),
            LocalTime.of(23, 59, 0, 0),
            LocalTime.of(23, 59, 0, 999999999),
            LocalTime.of(23, 59, 59, 0),
            LocalTime.of(23, 59, 59, 999999999)
        );
    }

    void doTest_comparisons_LocalTime(LocalTime... localTimes) {
        for (int i = 0; i < localTimes.length; i++) {
            LocalTime a = localTimes[i];
            for (int j = 0; j < localTimes.length; j++) {
                LocalTime b = localTimes[j];
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
        TEST_12_30_40_987654321.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isBefore_ObjectNull() {
        TEST_12_30_40_987654321.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isAfter_ObjectNull() {
        TEST_12_30_40_987654321.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class, groups={"tck"})
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareToNonLocalTime() {
       Comparable c = TEST_12_30_40_987654321;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_true(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s, n);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_hour_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h + 1, m, s, n);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_minute_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m + 1, s, n);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_second_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s + 1, n);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_nano_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s, n + 1);
        assertEquals(a.equals(b), false);
    }

    @Test(groups={"tck"})
    public void test_equals_itself_true() {
        assertEquals(TEST_12_30_40_987654321.equals(TEST_12_30_40_987654321), true);
    }

    @Test(groups={"tck"})
    public void test_equals_string_false() {
        assertEquals(TEST_12_30_40_987654321.equals("2007-07-15"), false);
    }

    @Test(groups={"tck"})
    public void test_equals_null_false() {
        assertEquals(TEST_12_30_40_987654321.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_hashCode_same(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s, n);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_hashCode_hour_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h + 1, m, s, n);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_hashCode_minute_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m + 1, s, n);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_hashCode_second_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s + 1, n);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_hashCode_nano_differs(int h, int m, int s, int n) {
        LocalTime a = LocalTime.of(h, m, s, n);
        LocalTime b = LocalTime.of(h, m, s, n + 1);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {0, 0, 0, 0, "00:00"},
            {1, 0, 0, 0, "01:00"},
            {23, 0, 0, 0, "23:00"},
            {0, 1, 0, 0, "00:01"},
            {12, 30, 0, 0, "12:30"},
            {23, 59, 0, 0, "23:59"},
            {0, 0, 1, 0, "00:00:01"},
            {0, 0, 59, 0, "00:00:59"},
            {0, 0, 0, 100000000, "00:00:00.100"},
            {0, 0, 0, 10000000, "00:00:00.010"},
            {0, 0, 0, 1000000, "00:00:00.001"},
            {0, 0, 0, 100000, "00:00:00.000100"},
            {0, 0, 0, 10000, "00:00:00.000010"},
            {0, 0, 0, 1000, "00:00:00.000001"},
            {0, 0, 0, 100, "00:00:00.000000100"},
            {0, 0, 0, 10, "00:00:00.000000010"},
            {0, 0, 0, 1, "00:00:00.000000001"},
            {0, 0, 0, 999999999, "00:00:00.999999999"},
            {0, 0, 0, 99999999, "00:00:00.099999999"},
            {0, 0, 0, 9999999, "00:00:00.009999999"},
            {0, 0, 0, 999999, "00:00:00.000999999"},
            {0, 0, 0, 99999, "00:00:00.000099999"},
            {0, 0, 0, 9999, "00:00:00.000009999"},
            {0, 0, 0, 999, "00:00:00.000000999"},
            {0, 0, 0, 99, "00:00:00.000000099"},
            {0, 0, 0, 9, "00:00:00.000000009"},
        };
    }

    @Test(dataProvider="sampleToString", groups={"tck"})
    public void test_toString(int h, int m, int s, int n, String expected) {
        LocalTime t = LocalTime.of(h, m, s, n);
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString_formatter() {
        final LocalTime time = LocalTime.of(11, 30, 45);
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                assertEquals(calendrical, time);
                return "PRINTED";
            }
            @Override
            public <T> T parse(CharSequence text, Class<T> type) {
                throw new AssertionError();
            }
        };
        String t = time.toString(f);
        assertEquals(t, "PRINTED");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_toString_formatter_null() {
        LocalTime.of(11, 30, 45).toString(null);
    }

}
