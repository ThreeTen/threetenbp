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

import static javax.time.calendrical.LocalDateTimeField.YEAR;
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

import javax.time.calendrical.AdjustableDateTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAdjuster;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.LocalPeriodUnit;
import javax.time.calendrical.MockFieldNoValue;
import javax.time.calendrical.PeriodUnit;
import javax.time.calendrical.Year;
import javax.time.format.CalendricalFormatter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalDate.
 */
@Test
public class TestLocalDate extends AbstractTest {

//    private static final String MIN_YEAR_STR = Integer.toString(Year.MIN_YEAR);
//    private static final String MAX_YEAR_STR = Integer.toString(Year.MAX_YEAR);
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId ZONE_GAZA = ZoneId.of("Asia/Gaza");
    
    private LocalDate TEST_2007_07_15;
    private long MAX_VALID_EPOCHDAYS;
    private long MIN_VALID_EPOCHDAYS;
    private LocalDate MAX_DATE;
    private LocalDate MIN_DATE;
    private Instant MAX_INSTANT;
    private Instant MIN_INSTANT;

    @BeforeMethod(groups={"tck", "implementation"})
    public void setUp() {
        TEST_2007_07_15 = LocalDate.of(2007, 7, 15);
        
        LocalDate max = LocalDate.MAX_DATE;
        LocalDate min = LocalDate.MIN_DATE;
        MAX_VALID_EPOCHDAYS = max.toEpochDay();
        MIN_VALID_EPOCHDAYS = min.toEpochDay();
        MAX_DATE = max;
        MIN_DATE = min;
        MAX_INSTANT = max.atOffset(ZoneOffset.UTC).atMidnight().toInstant();
        MIN_INSTANT = min.atOffset(ZoneOffset.UTC).atMidnight().toInstant();
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        Object obj = TEST_2007_07_15;
        assertTrue(obj instanceof DateTime);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    @Test(groups={"tck"})
    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_2007_07_15);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_immutable() {
        Class<LocalDate> cls = LocalDate.class;
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
    private void check(LocalDate test_2008_02_29, int y, int m, int d) {
        assertEquals(test_2008_02_29.getYear(), y);
        assertEquals(test_2008_02_29.getMonth().getValue(), m);
        assertEquals(test_2008_02_29.getDayOfMonth(), d);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void constant_MIN_DATE_TIME() {
        check(LocalDate.MIN_DATE, Year.MIN_YEAR, 1, 1);
    }

    @Test(groups={"implementation"})
    public void constant_MAX_DATE_TIME() {
        check(LocalDate.MAX_DATE, Year.MAX_YEAR, 12, 31);
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void now() {
        LocalDate expected = LocalDate.now(Clock.systemDefaultZone());
        LocalDate test = LocalDate.now();
        for (int i = 0; i < 100; i++) {
            if (expected.equals(test)) {
                return;
            }
            expected = LocalDate.now(Clock.systemDefaultZone());
            test = LocalDate.now();
        }
        assertEquals(test, expected);
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void now_Clock_nullClock() {
        LocalDate.now(null);
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            LocalDate test = LocalDate.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonth(), Month.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60 ? 1 : 2));
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_offset() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.fixed(instant.minusSeconds(OFFSET_PONE.getTotalSeconds()), ZoneId.of(OFFSET_PONE));
            LocalDate test = LocalDate.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonth(), Month.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60) ? 1 : 2);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_beforeEpoch() {
        for (int i =-1; i >= -(2 * 24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            LocalDate test = LocalDate.now(clock);
            assertEquals(test.getYear(), 1969);
            assertEquals(test.getMonth(), Month.DECEMBER);
            assertEquals(test.getDayOfMonth(), (i >= -24 * 60 * 60 ? 31 : 30));
        }
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void now_Clock_maxYear() {
        Clock clock = Clock.fixed(MAX_INSTANT, ZoneId.UTC);
        LocalDate test = LocalDate.now(clock);
        assertEquals(test, MAX_DATE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void now_Clock_tooBig() {
        Clock clock = Clock.fixed(MAX_INSTANT.plusSeconds(24 * 60 * 60), ZoneId.UTC);
        LocalDate.now(clock);
    }

    @Test(groups={"tck"})
    public void now_Clock_minYear() {
        Clock clock = Clock.fixed(MIN_INSTANT, ZoneId.UTC);
        LocalDate test = LocalDate.now(clock);
        assertEquals(test, MIN_DATE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void now_Clock_tooLow() {
        Clock clock = Clock.fixed(MIN_INSTANT.minusNanos(1), ZoneId.UTC);
        LocalDate.now(clock);
    }

    //-----------------------------------------------------------------------
    // of() factories
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intsMonth() {
        assertEquals(TEST_2007_07_15, LocalDate.of(2007, Month.JULY, 15));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_intsMonth_29febNonLeap() {
        LocalDate.of(2007, Month.FEBRUARY, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_intsMonth_31apr() {
        LocalDate.of(2007, Month.APRIL, 31);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_intsMonth_dayTooLow() {
        LocalDate.of(2007, Month.JANUARY, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_intsMonth_dayTooHigh() {
        LocalDate.of(2007, Month.JANUARY, 32);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_intsMonth_nullMonth() {
        LocalDate.of(2007, null, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_intsMonth_yearTooLow() {
        LocalDate.of(Integer.MIN_VALUE, Month.JANUARY, 1);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_ints() {
        check(TEST_2007_07_15, 2007, 7, 15);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_ints_29febNonLeap() {
        LocalDate.of(2007, 2, 29);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_ints_31apr() {
        LocalDate.of(2007, 4, 31);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_ints_dayTooLow() {
        LocalDate.of(2007, 1, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_ints_dayTooHigh() {
        LocalDate.of(2007, 1, 32);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_ints_monthTooLow() {
        LocalDate.of(2007, 0, 1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_ints_monthTooHigh() {
        LocalDate.of(2007, 13, 1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_ints_yearTooLow() {
        LocalDate.of(Integer.MIN_VALUE, 1, 1);
    }
    
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofYearDay_ints_nonLeap() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        for (int i = 1; i < 365; i++) {
            assertEquals(LocalDate.ofYearDay(2007, i), date);
            date = next(date);
        }
    }

    @Test(groups={"tck"})
    public void factory_ofYearDay_ints_leap() {
        LocalDate date = LocalDate.of(2008, 1, 1);
        for (int i = 1; i < 366; i++) {
            assertEquals(LocalDate.ofYearDay(2008, i), date);
            date = next(date);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofYearDay_ints_366nonLeap() {
        LocalDate.ofYearDay(2007, 366);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofYearDay_ints_dayTooLow() {
        LocalDate.ofYearDay(2007, 0);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofYearDay_ints_dayTooHigh() {
        LocalDate.ofYearDay(2007, 367);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofYearDay_ints_yearTooLow() {
        LocalDate.ofYearDay(Integer.MIN_VALUE, 1);
    }

    //-----------------------------------------------------------------------
    // Since plusDays/minusDays actually depends on MJDays, it cannot be used for testing
    private LocalDate next(LocalDate date) {
        int newDayOfMonth = date.getDayOfMonth() + 1;
        if (newDayOfMonth <= date.getMonth().length(isIsoLeap(date.getYear()))) {
            return date.withDayOfMonth(newDayOfMonth);
        }
        date = date.withDayOfMonth(1);
        if (date.getMonth() == Month.DECEMBER) {
            date = date.withYear(date.getYear() + 1);
        }
        return date.with(date.getMonth().plus(1));
    }

    private LocalDate previous(LocalDate date) {
        int newDayOfMonth = date.getDayOfMonth() - 1;
        if (newDayOfMonth > 0) {
            return date.withDayOfMonth(newDayOfMonth);
        }
        date = date.with(date.getMonth().minus(1));
        if (date.getMonth() == Month.DECEMBER) {
            date = date.withYear(date.getYear() - 1);
        }
        return date.withDayOfMonth(date.getMonth().length(isIsoLeap(date.getYear())));
    }

    //-----------------------------------------------------------------------
    // ofEpochDay()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofEpochDay() {
        long date_0000_01_01 = -678941 - 40587;
        assertEquals(LocalDate.ofEpochDay(0), LocalDate.of(1970, 1, 1));
        assertEquals(LocalDate.ofEpochDay(date_0000_01_01), LocalDate.of(0, 1, 1));
        assertEquals(LocalDate.ofEpochDay(date_0000_01_01 - 1), LocalDate.of(-1, 12, 31));
        assertEquals(LocalDate.ofEpochDay(MAX_VALID_EPOCHDAYS), LocalDate.of(Year.MAX_YEAR, 12, 31));
        assertEquals(LocalDate.ofEpochDay(MIN_VALID_EPOCHDAYS), LocalDate.of(Year.MIN_YEAR, 1, 1));
        
        LocalDate test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i < 700000; i++) {
            assertEquals(LocalDate.ofEpochDay(i), test);
            test = next(test);
        }
        test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i > -2000000; i--) {
            assertEquals(LocalDate.ofEpochDay(i), test);
            test = previous(test);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofEpochDay_aboveMax() {
        LocalDate.ofEpochDay(MAX_VALID_EPOCHDAYS + 1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofEpochDay_belowMin() {
        LocalDate.ofEpochDay(MIN_VALID_EPOCHDAYS - 1);
    }

    //-----------------------------------------------------------------------
    // from()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(LocalDate.from(LocalDate.of(2007, 7, 15)), LocalDate.of(2007, 7, 15));
        assertEquals(LocalDate.from(LocalDateTime.of(2007, 7, 15, 12, 30)), LocalDate.of(2007, 7, 15));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        LocalDate.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        LocalDate.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleToString", groups={"tck"})
//    public void factory_parse_validText(int y, int m, int d, String parsable) {
//        LocalDate t = LocalDate.parse(parsable);
//        assertNotNull(t, parsable);
//        assertEquals(t.getYear(), y, parsable);
//        assertEquals(t.getMonth().getValue(), m, parsable);
//        assertEquals(t.getDayOfMonth(), d, parsable);
//    }
//
//    @DataProvider(name="sampleBadParse")
//    Object[][] provider_sampleBadParse() {
//        return new Object[][]{
//                {"2008/07/05"},
//                {"10000-01-01"},
//                {"2008-1-1"},
//                {"2008--01"},
//                {"ABCD-02-01"},
//                {"2008-AB-01"},
//                {"2008-02-AB"},
//                {"-0000-02-01"},
//                {"2008-02-01Z"},
//                {"2008-02-01+01:00"},
//                {"2008-02-01+01:00[Europe/Paris]"},
//        };
//    }
//
//    @Test(dataProvider="sampleBadParse", expectedExceptions={CalendricalParseException.class}, groups={"tck"})
//    public void factory_parse_invalidText(String unparsable) {
//        LocalDate.parse(unparsable);
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_illegalValue() {
//        LocalDate.parse("2008-06-32");
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_invalidValue() {
//        LocalDate.parse("2008-06-31");
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void factory_parse_nullText() {
//        LocalDate.parse((String) null);
//    }

    //-----------------------------------------------------------------------
    // parse(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_parse_formatter() {
        final LocalDate date = LocalDate.of(2010, 12, 3);
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                throw new AssertionError();
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object parse(CharSequence text, Class type) {
                return date;
            }
        };
        LocalDate test = LocalDate.parse("ANY", f);
        assertEquals(test, date);
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
        LocalDate.parse((String) null, f);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_parse_formatter_nullFormatter() {
        LocalDate.parse("ANY", null);
    }

    //-----------------------------------------------------------------------
    // get(DateTimeField)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_get_DateTimeField() {
        LocalDate test = LocalDate.of(2008, 6, 30);
        assertEquals(test.get(LocalDateTimeField.YEAR), 2008);
        assertEquals(test.get(LocalDateTimeField.MONTH_OF_YEAR), 6);
        assertEquals(test.get(LocalDateTimeField.DAY_OF_MONTH), 30);
        assertEquals(test.get(LocalDateTimeField.DAY_OF_WEEK), 1);
        assertEquals(test.get(LocalDateTimeField.DAY_OF_YEAR), 182);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"} )
    public void test_get_DateTimeField_null() {
        TEST_2007_07_15.get((DateTimeField) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_get_DateTimeField_invalidField() {
        TEST_2007_07_15.get(MockFieldNoValue.INSTANCE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_get_DateTimeField_timeField() {
        TEST_2007_07_15.get(LocalDateTimeField.AMPM_OF_DAY);
    }

    //-----------------------------------------------------------------------
    // extract(Class)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_extract_Class() {
        LocalDate test = LocalDate.of(2008, 6, 30);
        assertEquals(test.extract(LocalDate.class), test);
        assertEquals(test.extract(LocalTime.class), null);
        assertEquals(test.extract(LocalDateTime.class), null);
        assertEquals(test.extract(OffsetDate.class), null);
        assertEquals(test.extract(OffsetTime.class), null);
        assertEquals(test.extract(OffsetDateTime.class), null);
        assertEquals(test.extract(ZonedDateTime.class), null);
        assertEquals(test.extract(ZoneOffset.class), null);
        assertEquals(test.extract(ZoneId.class), null);
        assertEquals(test.extract(Instant.class), null);
        assertEquals(test.extract(Class.class), LocalDate.class);
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

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates", groups={"tck"})
    public void test_get(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        assertEquals(a.getYear(), y);
        assertEquals(a.getMonth(), Month.of(m));
        assertEquals(a.getDayOfMonth(), d);
    }

    @Test(dataProvider="sampleDates", groups={"tck"})
    public void test_getDOY(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        int total = 0;
        for (int i = 1; i < m; i++) {
            total += Month.of(i).length(isIsoLeap(y));
        }
        int doy = total + d;
        assertEquals(a.getDayOfYear(), doy);
    }

    @Test(groups={"tck"})
    public void test_getDayOfWeek() {
        DayOfWeek dow = DayOfWeek.MONDAY;
        for (Month month : Month.values()) {
            int length = month.length(false);
            for (int i = 1; i <= length; i++) {
                LocalDate d = LocalDate.of(2007, month, i);
                assertSame(d.getDayOfWeek(), dow);
                dow = dow.plus(1);
            }
        }
    }

    //-----------------------------------------------------------------------
    // isLeapYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isLeapYear() {
        assertEquals(LocalDate.of(1999, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(2000, 1, 1).isLeapYear(), true);
        assertEquals(LocalDate.of(2001, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(2002, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(2003, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(2004, 1, 1).isLeapYear(), true);
        assertEquals(LocalDate.of(2005, 1, 1).isLeapYear(), false);
        
        assertEquals(LocalDate.of(1500, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(1600, 1, 1).isLeapYear(), true);
        assertEquals(LocalDate.of(1700, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(1800, 1, 1).isLeapYear(), false);
        assertEquals(LocalDate.of(1900, 1, 1).isLeapYear(), false);
    }

    //-----------------------------------------------------------------------
    // lengthOfMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_lengthOfMonth_notLeapYear() {
        assertEquals(LocalDate.of(2007, 1, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2007, 2, 1).lengthOfMonth(), 28);
        assertEquals(LocalDate.of(2007, 3, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2007, 4, 1).lengthOfMonth(), 30);
        assertEquals(LocalDate.of(2007, 5, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2007, 6, 1).lengthOfMonth(), 30);
        assertEquals(LocalDate.of(2007, 7, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2007, 8, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2007, 9, 1).lengthOfMonth(), 30);
        assertEquals(LocalDate.of(2007, 10, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2007, 11, 1).lengthOfMonth(), 30);
        assertEquals(LocalDate.of(2007, 12, 1).lengthOfMonth(), 31);
    }

    @Test(groups={"tck"})
    public void test_lengthOfMonth_leapYear() {
        assertEquals(LocalDate.of(2008, 1, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2008, 2, 1).lengthOfMonth(), 29);
        assertEquals(LocalDate.of(2008, 3, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2008, 4, 1).lengthOfMonth(), 30);
        assertEquals(LocalDate.of(2008, 5, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2008, 6, 1).lengthOfMonth(), 30);
        assertEquals(LocalDate.of(2008, 7, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2008, 8, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2008, 9, 1).lengthOfMonth(), 30);
        assertEquals(LocalDate.of(2008, 10, 1).lengthOfMonth(), 31);
        assertEquals(LocalDate.of(2008, 11, 1).lengthOfMonth(), 30);
        assertEquals(LocalDate.of(2008, 12, 1).lengthOfMonth(), 31);
    }

    //-----------------------------------------------------------------------
    // lengthOfYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_lengthOfYear() {
        assertEquals(LocalDate.of(2007, 1, 1).lengthOfYear(), 365);
        assertEquals(LocalDate.of(2008, 1, 1).lengthOfYear(), 366);
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_with_adjustment() {
        final LocalDate sample = LocalDate.of(2012, 3, 4);
        DateTimeAdjuster adjuster = new DateTimeAdjuster() {
            @Override
            public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
                return sample;
            }
        };
        assertEquals(TEST_2007_07_15.with(adjuster), sample);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_with_adjustment_null() {
        TEST_2007_07_15.with((DateTimeAdjuster) null);
    }

    //-----------------------------------------------------------------------
    // with(DateTimeField,long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_with_DateTimeField_long_normal() {
        LocalDate t = TEST_2007_07_15.with(YEAR, 2008);
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    @Test(groups={"implementation"})
    public void test_with_DateTimeField_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.with(YEAR, 2007);
        assertSame(t, TEST_2007_07_15);
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"} )
    public void test_with_DateTimeField_long_null() {
        TEST_2007_07_15.with((DateTimeField) null, 1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_with_DateTimeField_long_invalidField() {
        TEST_2007_07_15.with(MockFieldNoValue.INSTANCE, 1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_with_DateTimeField_long_timeField() {
        TEST_2007_07_15.with(LocalDateTimeField.AMPM_OF_DAY, 1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_with_DateTimeField_long_invalidValue() {
        TEST_2007_07_15.with(LocalDateTimeField.DAY_OF_WEEK, -1);
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withYear_int_normal() {
        LocalDate t = TEST_2007_07_15.withYear(2008);
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    @Test(groups={"implementation"})
    public void test_withYear_int_noChange_same() {
        LocalDate t = TEST_2007_07_15.withYear(2007);
        assertSame(t, TEST_2007_07_15);
    }
    
    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withYear_int_invalid() {
        TEST_2007_07_15.withYear(Year.MIN_YEAR - 1);
    }
    
    @Test(groups={"tck"})
    public void test_withYear_int_adjustDay() {
        LocalDate t = LocalDate.of(2008, 2, 29).withYear(2007);
        LocalDate expected = LocalDate.of(2007, 2, 28);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withMonth_int_normal() {
        LocalDate t = TEST_2007_07_15.withMonth(1);
        assertEquals(t, LocalDate.of(2007, 1, 15));
    }

    @Test(groups={"implementation"})
    public void test_withMonth_int_noChange_same() {
        LocalDate t = TEST_2007_07_15.withMonth(7);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withMonth_int_invalid() {
        TEST_2007_07_15.withMonth(13);
    }

    @Test(groups={"tck"})
    public void test_withMonth_int_adjustDay() {
        LocalDate t = LocalDate.of(2007, 12, 31).withMonth(11);
        LocalDate expected = LocalDate.of(2007, 11, 30);
        assertEquals(t, expected);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDayOfMonth_normal() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(1);
        assertEquals(t, LocalDate.of(2007, 7, 1));
    }

    @Test(groups={"implementation"})
    public void test_withDayOfMonth_noChange_same() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(15);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfMonth_illegal() {
        TEST_2007_07_15.withDayOfMonth(32);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfMonth_invalid() {
        LocalDate.of(2007, 11, 30).withDayOfMonth(31);
    }

    //-----------------------------------------------------------------------
    // withDayOfYear(int)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDayOfYear_normal() {
        LocalDate t = TEST_2007_07_15.withDayOfYear(33);
        assertEquals(t, LocalDate.of(2007, 2, 2));
    }

    @Test(groups={"implementation"})
    public void test_withDayOfYear_noChange_same() {
        LocalDate t = TEST_2007_07_15.withDayOfYear(31 + 28 + 31 + 30 + 31 + 30 + 15);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfYear_illegal() {
        TEST_2007_07_15.withDayOfYear(367);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_withDayOfYear_invalid() {
        TEST_2007_07_15.withDayOfYear(366);
    }

    //-----------------------------------------------------------------------
    // plus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Period_positiveMonths() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        LocalDate t = TEST_2007_07_15.plus(period);
        assertEquals(t, LocalDate.of(2008, 2, 15));
    }

    @Test(groups={"tck"})
    public void test_plus_Period_negativeDays() {
        Period period = Period.of(-25, LocalPeriodUnit.DAYS);
        LocalDate t = TEST_2007_07_15.plus(period);
        assertEquals(t, LocalDate.of(2007, 6, 20));
    }

    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_plus_Period_timeNotAllowed() {
        Period period = Period.of(7, LocalPeriodUnit.HOURS);
        TEST_2007_07_15.plus(period);
    }

    @Test(groups={"implementation"})
    public void test_plus_Period_zero() {
        LocalDate t = TEST_2007_07_15.plus(Period.ZERO_DAYS);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_Period_null() {
        TEST_2007_07_15.plus((Period) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_Period_invalidTooLarge() {
        Period period = Period.of(1, LocalPeriodUnit.YEARS);
        LocalDate.of(Year.MAX_YEAR, 1, 1).plus(period);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_Period_invalidTooSmall() {
        Period period = Period.of(-1, LocalPeriodUnit.YEARS);
        LocalDate.of(Year.MIN_YEAR, 1, 1).plus(period);
    }

    //-----------------------------------------------------------------------
    // plus(long,PeriodUnit)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_longPeriodUnit_positiveMonths() {
        LocalDate t = TEST_2007_07_15.plus(7, LocalPeriodUnit.MONTHS);
        assertEquals(t, LocalDate.of(2008, 2, 15));
    }
 
    @Test(groups={"tck"})
    public void test_plus_longPeriodUnit_negativeDays() {
        LocalDate t = TEST_2007_07_15.plus(-25, LocalPeriodUnit.DAYS);
        assertEquals(t, LocalDate.of(2007, 6, 20));
    }

    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_plus_longPeriodUnit_timeNotAllowed() {
        TEST_2007_07_15.plus(7, LocalPeriodUnit.HOURS);
    }

    @Test(groups={"implementation"})
    public void test_plus_longPeriodUnit_zero() {
        LocalDate t = TEST_2007_07_15.plus(0, LocalPeriodUnit.DAYS);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_longPeriodUnit_null() {
        TEST_2007_07_15.plus(1, (PeriodUnit) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_longPeriodUnit_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 1, 1).plus(1, LocalPeriodUnit.YEARS);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plus_longPeriodUnit_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plus(-1, LocalPeriodUnit.YEARS);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusYears_long_normal() {
        LocalDate t = TEST_2007_07_15.plusYears(1);
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    @Test(groups={"implementation"})
    public void test_plusYears_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.plusYears(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_plusYears_long_negative() {
        LocalDate t = TEST_2007_07_15.plusYears(-1);
        assertEquals(t, LocalDate.of(2006, 7, 15));
    }

    @Test(groups={"tck"})
    public void test_plusYears_long_adjustDay() {
        LocalDate t = LocalDate.of(2008, 2, 29).plusYears(1);
        LocalDate expected = LocalDate.of(2009, 2, 28);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_plusYears_long_big() {
        long years = 20L + Year.MAX_YEAR;
        LocalDate test = LocalDate.of(-40, 6, 1).plusYears(years);
        assertEquals(test, LocalDate.of((int) (-40L + years), 6, 1));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_long_invalidTooLarge() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 6, 1);
        test.plusYears(1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_long_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusYears(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_long_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusYears(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_long_invalidTooSmall_validInt() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusYears_long_invalidTooSmall_invalidInt() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusYears(-10);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusMonths_long_normal() {
        LocalDate t = TEST_2007_07_15.plusMonths(1);
        assertEquals(t, LocalDate.of(2007, 8, 15));
    }

    @Test(groups={"implementation"})
    public void test_plusMonths_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.plusMonths(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_plusMonths_long_overYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(25);
        assertEquals(t, LocalDate.of(2009, 8, 15));
    }

    @Test(groups={"tck"})
    public void test_plusMonths_long_negative() {
        LocalDate t = TEST_2007_07_15.plusMonths(-1);
        assertEquals(t, LocalDate.of(2007, 6, 15));
    }

    @Test(groups={"tck"})
    public void test_plusMonths_long_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusMonths(-7);
        assertEquals(t, LocalDate.of(2006, 12, 15));
    }

    @Test(groups={"tck"})
    public void test_plusMonths_long_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusMonths(-31);
        assertEquals(t, LocalDate.of(2004, 12, 15));
    }

    @Test(groups={"tck"})
    public void test_plusMonths_long_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.of(2008, 2, 29).plusMonths(12);
        LocalDate expected = LocalDate.of(2009, 2, 28);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_plusMonths_long_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.of(2007, 3, 31).plusMonths(1);
        LocalDate expected = LocalDate.of(2007, 4, 30);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_plusMonths_long_big() {
        long months = 20L + Integer.MAX_VALUE;
        LocalDate test = LocalDate.of(-40, 6, 1).plusMonths(months);
        assertEquals(test, LocalDate.of((int) (-40L + months / 12), 6 + (int) (months % 12), 1));
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_plusMonths_long_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 1).plusMonths(1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusMonths_long_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusMonths(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_plusMonths_long_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.plusMonths(Long.MIN_VALUE);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_plusMonths_long_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusMonths(-1);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusWeeksSymmetry")
    Object[][] provider_samplePlusWeeksSymmetry() {
        return new Object[][] {
            {LocalDate.of(-1, 1, 1)},
            {LocalDate.of(-1, 2, 28)},
            {LocalDate.of(-1, 3, 1)},
            {LocalDate.of(-1, 12, 31)},
            {LocalDate.of(0, 1, 1)},
            {LocalDate.of(0, 2, 28)},
            {LocalDate.of(0, 2, 29)},
            {LocalDate.of(0, 3, 1)},
            {LocalDate.of(0, 12, 31)},
            {LocalDate.of(2007, 1, 1)},
            {LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2007, 3, 1)},
            {LocalDate.of(2007, 12, 31)},
            {LocalDate.of(2008, 1, 1)},
            {LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 12, 31)},
            {LocalDate.of(2099, 1, 1)},
            {LocalDate.of(2099, 2, 28)},
            {LocalDate.of(2099, 3, 1)},
            {LocalDate.of(2099, 12, 31)},
            {LocalDate.of(2100, 1, 1)},
            {LocalDate.of(2100, 2, 28)},
            {LocalDate.of(2100, 3, 1)},
            {LocalDate.of(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="samplePlusWeeksSymmetry", groups={"implementation"})
    public void test_plusWeeks_symmetry(LocalDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDate t = reference.plusWeeks(weeks).plusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.plusWeeks(-weeks).plusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_normal() {
        LocalDate t = TEST_2007_07_15.plusWeeks(1);
        assertEquals(t, LocalDate.of(2007, 7, 22));
    }

    @Test(groups={"implementation"})
    public void test_plusWeeks_noChange_same() {
        LocalDate t = TEST_2007_07_15.plusWeeks(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_overMonths() {
        LocalDate t = TEST_2007_07_15.plusWeeks(9);
        assertEquals(t, LocalDate.of(2007, 9, 16));
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_overYears() {
        LocalDate t = LocalDate.of(2006, 7, 16).plusWeeks(52);
        assertEquals(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_overLeapYears() {
        LocalDate t = TEST_2007_07_15.plusYears(-1).plusWeeks(104);
        assertEquals(t, LocalDate.of(2008, 7, 12));
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_negative() {
        LocalDate t = TEST_2007_07_15.plusWeeks(-1);
        assertEquals(t, LocalDate.of(2007, 7, 8));
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusWeeks(-28);
        assertEquals(t, LocalDate.of(2006, 12, 31));
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusWeeks(-104);
        assertEquals(t, LocalDate.of(2005, 7, 17));
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_maximum() {
        LocalDate t = LocalDate.of(Year.MAX_YEAR, 12, 24).plusWeeks(1);
        LocalDate expected = LocalDate.of(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_plusWeeks_minimum() {
        LocalDate t = LocalDate.of(Year.MIN_YEAR, 1, 8).plusWeeks(-1);
        LocalDate expected = LocalDate.of(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_plusWeeks_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).plusWeeks(1);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_plusWeeks_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 7).plusWeeks(-1);
    }

    @Test(expectedExceptions={ArithmeticException.class}, groups={"tck"})
    public void test_plusWeeks_invalidMaxMinusMax() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).plusWeeks(Long.MAX_VALUE);
    }

    @Test(expectedExceptions={ArithmeticException.class}, groups={"tck"})
    public void test_plusWeeks_invalidMaxMinusMin() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).plusWeeks(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="samplePlusDaysSymmetry")
    Object[][] provider_samplePlusDaysSymmetry() {
        return new Object[][] {
            {LocalDate.of(-1, 1, 1)},
            {LocalDate.of(-1, 2, 28)},
            {LocalDate.of(-1, 3, 1)},
            {LocalDate.of(-1, 12, 31)},
            {LocalDate.of(0, 1, 1)},
            {LocalDate.of(0, 2, 28)},
            {LocalDate.of(0, 2, 29)},
            {LocalDate.of(0, 3, 1)},
            {LocalDate.of(0, 12, 31)},
            {LocalDate.of(2007, 1, 1)},
            {LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2007, 3, 1)},
            {LocalDate.of(2007, 12, 31)},
            {LocalDate.of(2008, 1, 1)},
            {LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 12, 31)},
            {LocalDate.of(2099, 1, 1)},
            {LocalDate.of(2099, 2, 28)},
            {LocalDate.of(2099, 3, 1)},
            {LocalDate.of(2099, 12, 31)},
            {LocalDate.of(2100, 1, 1)},
            {LocalDate.of(2100, 2, 28)},
            {LocalDate.of(2100, 3, 1)},
            {LocalDate.of(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="samplePlusDaysSymmetry", groups={"implementation"})
    public void test_plusDays_symmetry(LocalDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDate t = reference.plusDays(days).plusDays(-days);
            assertEquals(t, reference);

            t = reference.plusDays(-days).plusDays(days);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"tck"})
    public void test_plusDays_normal() {
        LocalDate t = TEST_2007_07_15.plusDays(1);
        assertEquals(t, LocalDate.of(2007, 7, 16));
    }

    @Test(groups={"implementation"})
    public void test_plusDays_noChange_same() {
        LocalDate t = TEST_2007_07_15.plusDays(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_plusDays_overMonths() {
        LocalDate t = TEST_2007_07_15.plusDays(62);
        assertEquals(t, LocalDate.of(2007, 9, 15));
    }

    @Test(groups={"tck"})
    public void test_plusDays_overYears() {
        LocalDate t = LocalDate.of(2006, 7, 14).plusDays(366);
        assertEquals(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_plusDays_overLeapYears() {
        LocalDate t = TEST_2007_07_15.plusYears(-1).plusDays(365 + 366);
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    @Test(groups={"tck"})
    public void test_plusDays_negative() {
        LocalDate t = TEST_2007_07_15.plusDays(-1);
        assertEquals(t, LocalDate.of(2007, 7, 14));
    }

    @Test(groups={"tck"})
    public void test_plusDays_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusDays(-196);
        assertEquals(t, LocalDate.of(2006, 12, 31));
    }

    @Test(groups={"tck"})
    public void test_plusDays_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.plusDays(-730);
        assertEquals(t, LocalDate.of(2005, 7, 15));
    }

    @Test(groups={"tck"})
    public void test_plusDays_maximum() {
        LocalDate t = LocalDate.of(Year.MAX_YEAR, 12, 30).plusDays(1);
        LocalDate expected = LocalDate.of(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_plusDays_minimum() {
        LocalDate t = LocalDate.of(Year.MIN_YEAR, 1, 2).plusDays(-1);
        LocalDate expected = LocalDate.of(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_plusDays_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 31).plusDays(1);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_plusDays_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusDays(-1);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plusDays_overflowTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 31).plusDays(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_plusDays_overflowTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).plusDays(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Period_positiveMonths() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        LocalDate t = TEST_2007_07_15.minus(period);
        assertEquals(t, LocalDate.of(2006, 12, 15));
    }

    @Test(groups={"tck"})
    public void test_minus_Period_negativeDays() {
        Period period = Period.of(-25, LocalPeriodUnit.DAYS);
        LocalDate t = TEST_2007_07_15.minus(period);
        assertEquals(t, LocalDate.of(2007, 8, 9));
    }

    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_minus_Period_timeNotAllowed() {
        Period period = Period.of(7, LocalPeriodUnit.HOURS);
        TEST_2007_07_15.minus(period);
    }

    @Test(groups={"implementation"})
    public void test_minus_Period_zero() {
        LocalDate t = TEST_2007_07_15.minus(Period.ZERO_DAYS);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_Period_null() {
        TEST_2007_07_15.minus((Period) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_Period_invalidTooLarge() {
        Period period = Period.of(-1, LocalPeriodUnit.YEARS);
        LocalDate.of(Year.MAX_YEAR, 1, 1).minus(period);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_Period_invalidTooSmall() {
        Period period = Period.of(1, LocalPeriodUnit.YEARS);
        LocalDate.of(Year.MIN_YEAR, 1, 1).minus(period);
    }

    //-----------------------------------------------------------------------
    // minus(long,PeriodUnit)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_longPeriodUnit_positiveMonths() {
        LocalDate t = TEST_2007_07_15.minus(7, LocalPeriodUnit.MONTHS);
        assertEquals(t, LocalDate.of(2006, 12, 15));
    }
 
    @Test(groups={"tck"})
    public void test_minus_longPeriodUnit_negativeDays() {
        LocalDate t = TEST_2007_07_15.minus(-25, LocalPeriodUnit.DAYS);
        assertEquals(t, LocalDate.of(2007, 8, 9));
    }

    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_minus_longPeriodUnit_timeNotAllowed() {
        TEST_2007_07_15.minus(7, LocalPeriodUnit.HOURS);
    }

    @Test(groups={"implementation"})
    public void test_minus_longPeriodUnit_zero() {
        LocalDate t = TEST_2007_07_15.minus(0, LocalPeriodUnit.DAYS);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_longPeriodUnit_null() {
        TEST_2007_07_15.minus(1, (PeriodUnit) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_longPeriodUnit_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 1, 1).minus(-1, LocalPeriodUnit.YEARS);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minus_longPeriodUnit_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minus(1, LocalPeriodUnit.YEARS);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusYears_long_normal() {
        LocalDate t = TEST_2007_07_15.minusYears(1);
        assertEquals(t, LocalDate.of(2006, 7, 15));
    }

    @Test(groups={"implementation"})
    public void test_minusYears_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.minusYears(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_minusYears_long_negative() {
        LocalDate t = TEST_2007_07_15.minusYears(-1);
        assertEquals(t, LocalDate.of(2008, 7, 15));
    }

    @Test(groups={"tck"})
    public void test_minusYears_long_adjustDay() {
        LocalDate t = LocalDate.of(2008, 2, 29).minusYears(1);
        LocalDate expected = LocalDate.of(2007, 2, 28);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_minusYears_long_big() {
        long years = 20L + Year.MAX_YEAR;
        LocalDate test = LocalDate.of(40, 6, 1).minusYears(years);
        assertEquals(test, LocalDate.of((int) (40L - years), 6, 1));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_long_invalidTooLarge() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 6, 1);
        test.minusYears(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_long_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusYears(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_long_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusYears(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusYears_long_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusYears(1);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusMonths_long_normal() {
        LocalDate t = TEST_2007_07_15.minusMonths(1);
        assertEquals(t, LocalDate.of(2007, 6, 15));
    }

    @Test(groups={"implementation"})
    public void test_minusMonths_long_noChange_same() {
        LocalDate t = TEST_2007_07_15.minusMonths(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_minusMonths_long_overYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(25);
        assertEquals(t, LocalDate.of(2005, 6, 15));
    }

    @Test(groups={"tck"})
    public void test_minusMonths_long_negative() {
        LocalDate t = TEST_2007_07_15.minusMonths(-1);
        assertEquals(t, LocalDate.of(2007, 8, 15));
    }

    @Test(groups={"tck"})
    public void test_minusMonths_long_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusMonths(-7);
        assertEquals(t, LocalDate.of(2008, 2, 15));
    }

    @Test(groups={"tck"})
    public void test_minusMonths_long_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusMonths(-31);
        assertEquals(t, LocalDate.of(2010, 2, 15));
    }

    @Test(groups={"tck"})
    public void test_minusMonths_long_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.of(2008, 2, 29).minusMonths(12);
        LocalDate expected = LocalDate.of(2007, 2, 28);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_minusMonths_long_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.of(2007, 3, 31).minusMonths(1);
        LocalDate expected = LocalDate.of(2007, 2, 28);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_minusMonths_long_big() {
        long months = 20L + Integer.MAX_VALUE;
        LocalDate test = LocalDate.of(40, 6, 1).minusMonths(months);
        assertEquals(test, LocalDate.of((int) (40L - months / 12), 6 - (int) (months % 12), 1));
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_minusMonths_long_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 1).minusMonths(-1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusMonths_long_invalidTooLargeMaxAddMax() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusMonths(Long.MAX_VALUE);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_minusMonths_long_invalidTooLargeMaxAddMin() {
        LocalDate test = LocalDate.of(Year.MAX_YEAR, 12, 1);
        test.minusMonths(Long.MIN_VALUE);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_minusMonths_long_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusMonths(1);
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusWeeksSymmetry")
    Object[][] provider_sampleMinusWeeksSymmetry() {
        return new Object[][] {
            {LocalDate.of(-1, 1, 1)},
            {LocalDate.of(-1, 2, 28)},
            {LocalDate.of(-1, 3, 1)},
            {LocalDate.of(-1, 12, 31)},
            {LocalDate.of(0, 1, 1)},
            {LocalDate.of(0, 2, 28)},
            {LocalDate.of(0, 2, 29)},
            {LocalDate.of(0, 3, 1)},
            {LocalDate.of(0, 12, 31)},
            {LocalDate.of(2007, 1, 1)},
            {LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2007, 3, 1)},
            {LocalDate.of(2007, 12, 31)},
            {LocalDate.of(2008, 1, 1)},
            {LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 12, 31)},
            {LocalDate.of(2099, 1, 1)},
            {LocalDate.of(2099, 2, 28)},
            {LocalDate.of(2099, 3, 1)},
            {LocalDate.of(2099, 12, 31)},
            {LocalDate.of(2100, 1, 1)},
            {LocalDate.of(2100, 2, 28)},
            {LocalDate.of(2100, 3, 1)},
            {LocalDate.of(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="sampleMinusWeeksSymmetry", groups={"implementation"})
    public void test_minusWeeks_symmetry(LocalDate reference) {
        for (int weeks = 0; weeks < 365 * 8; weeks++) {
            LocalDate t = reference.minusWeeks(weeks).minusWeeks(-weeks);
            assertEquals(t, reference);

            t = reference.minusWeeks(-weeks).minusWeeks(weeks);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_normal() {
        LocalDate t = TEST_2007_07_15.minusWeeks(1);
        assertEquals(t, LocalDate.of(2007, 7, 8));
    }

    @Test(groups={"implementation"})
    public void test_minusWeeks_noChange_same() {
        LocalDate t = TEST_2007_07_15.minusWeeks(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_overMonths() {
        LocalDate t = TEST_2007_07_15.minusWeeks(9);
        assertEquals(t, LocalDate.of(2007, 5, 13));
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_overYears() {
        LocalDate t = LocalDate.of(2008, 7, 13).minusWeeks(52);
        assertEquals(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_overLeapYears() {
        LocalDate t = TEST_2007_07_15.minusYears(-1).minusWeeks(104);
        assertEquals(t, LocalDate.of(2006, 7, 18));
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_negative() {
        LocalDate t = TEST_2007_07_15.minusWeeks(-1);
        assertEquals(t, LocalDate.of(2007, 7, 22));
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusWeeks(-28);
        assertEquals(t, LocalDate.of(2008, 1, 27));
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusWeeks(-104);
        assertEquals(t, LocalDate.of(2009, 7, 12));
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_maximum() {
        LocalDate t = LocalDate.of(Year.MAX_YEAR, 12, 24).minusWeeks(-1);
        LocalDate expected = LocalDate.of(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_minusWeeks_minimum() {
        LocalDate t = LocalDate.of(Year.MIN_YEAR, 1, 8).minusWeeks(1);
        LocalDate expected = LocalDate.of(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_minusWeeks_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).minusWeeks(-1);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_minusWeeks_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 7).minusWeeks(1);
    }

    @Test(expectedExceptions={ArithmeticException.class}, groups={"tck"})
    public void test_minusWeeks_invalidMaxMinusMax() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).minusWeeks(Long.MAX_VALUE);
    }

    @Test(expectedExceptions={ArithmeticException.class}, groups={"tck"})
    public void test_minusWeeks_invalidMaxMinusMin() {
        LocalDate.of(Year.MAX_YEAR, 12, 25).minusWeeks(Long.MIN_VALUE);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleMinusDaysSymmetry")
    Object[][] provider_sampleMinusDaysSymmetry() {
        return new Object[][] {
            {LocalDate.of(-1, 1, 1)},
            {LocalDate.of(-1, 2, 28)},
            {LocalDate.of(-1, 3, 1)},
            {LocalDate.of(-1, 12, 31)},
            {LocalDate.of(0, 1, 1)},
            {LocalDate.of(0, 2, 28)},
            {LocalDate.of(0, 2, 29)},
            {LocalDate.of(0, 3, 1)},
            {LocalDate.of(0, 12, 31)},
            {LocalDate.of(2007, 1, 1)},
            {LocalDate.of(2007, 2, 28)},
            {LocalDate.of(2007, 3, 1)},
            {LocalDate.of(2007, 12, 31)},
            {LocalDate.of(2008, 1, 1)},
            {LocalDate.of(2008, 2, 28)},
            {LocalDate.of(2008, 2, 29)},
            {LocalDate.of(2008, 3, 1)},
            {LocalDate.of(2008, 12, 31)},
            {LocalDate.of(2099, 1, 1)},
            {LocalDate.of(2099, 2, 28)},
            {LocalDate.of(2099, 3, 1)},
            {LocalDate.of(2099, 12, 31)},
            {LocalDate.of(2100, 1, 1)},
            {LocalDate.of(2100, 2, 28)},
            {LocalDate.of(2100, 3, 1)},
            {LocalDate.of(2100, 12, 31)},
        };
    }
    
    @Test(dataProvider="sampleMinusDaysSymmetry", groups={"implementation"})
    public void test_minusDays_symmetry(LocalDate reference) {
        for (int days = 0; days < 365 * 8; days++) {
            LocalDate t = reference.minusDays(days).minusDays(-days);
            assertEquals(t, reference);

            t = reference.minusDays(-days).minusDays(days);
            assertEquals(t, reference);
        }
    }

    @Test(groups={"tck"})
    public void test_minusDays_normal() {
        LocalDate t = TEST_2007_07_15.minusDays(1);
        assertEquals(t, LocalDate.of(2007, 7, 14));
    }

    @Test(groups={"implementation"})
    public void test_minusDays_noChange_same() {
        LocalDate t = TEST_2007_07_15.minusDays(0);
        assertSame(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_minusDays_overMonths() {
        LocalDate t = TEST_2007_07_15.minusDays(62);
        assertEquals(t, LocalDate.of(2007, 5, 14));
    }

    @Test(groups={"tck"})
    public void test_minusDays_overYears() {
        LocalDate t = LocalDate.of(2008, 7, 16).minusDays(367);
        assertEquals(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_minusDays_overLeapYears() {
        LocalDate t = TEST_2007_07_15.plusYears(2).minusDays(365 + 366);
        assertEquals(t, TEST_2007_07_15);
    }

    @Test(groups={"tck"})
    public void test_minusDays_negative() {
        LocalDate t = TEST_2007_07_15.minusDays(-1);
        assertEquals(t, LocalDate.of(2007, 7, 16));
    }

    @Test(groups={"tck"})
    public void test_minusDays_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.minusDays(-169);
        assertEquals(t, LocalDate.of(2007, 12, 31));
    }

    @Test(groups={"tck"})
    public void test_minusDays_negativeOverYears() {
        LocalDate t = TEST_2007_07_15.minusDays(-731);
        assertEquals(t, LocalDate.of(2009, 7, 15));
    }

    @Test(groups={"tck"})
    public void test_minusDays_maximum() {
        LocalDate t = LocalDate.of(Year.MAX_YEAR, 12, 30).minusDays(-1);
        LocalDate expected = LocalDate.of(Year.MAX_YEAR, 12, 31);
        assertEquals(t, expected);
    }

    @Test(groups={"tck"})
    public void test_minusDays_minimum() {
        LocalDate t = LocalDate.of(Year.MIN_YEAR, 1, 2).minusDays(1);
        LocalDate expected = LocalDate.of(Year.MIN_YEAR, 1, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_minusDays_invalidTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 31).minusDays(-1);
    }

    @Test(expectedExceptions={CalendricalException.class}, groups={"tck"})
    public void test_minusDays_invalidTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusDays(1);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minusDays_overflowTooLarge() {
        LocalDate.of(Year.MAX_YEAR, 12, 31).minusDays(Long.MIN_VALUE);
    }

    @Test(expectedExceptions=ArithmeticException.class, groups={"tck"})
    public void test_minusDays_overflowTooSmall() {
        LocalDate.of(Year.MIN_YEAR, 1, 1).minusDays(Long.MAX_VALUE);
    }

    //-----------------------------------------------------------------------
    // atTime()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atTime_OffsetTime() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(OffsetTime.of(11, 30, OFFSET_PONE)), OffsetDateTime.of(2008, 6, 30, 11, 30, OFFSET_PONE));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atTime_OffsetTime_null() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime((OffsetTime) null);
    }

    @Test(groups={"tck"})
    public void test_atTime_LocalTime() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(LocalTime.of(11, 30)), LocalDateTime.of(2008, 6, 30, 11, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atTime_LocalTime_null() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime((LocalTime) null);
    }

    //-------------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atTime_int_int() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(11, 30), LocalDateTime.of(2008, 6, 30, 11, 30));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_hourTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(-1, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_hourTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(24, 30);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_minuteTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_minuteTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 60);
    }

    @Test(groups={"tck"})
    public void test_atTime_int_int_int() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(11, 30, 40), LocalDateTime.of(2008, 6, 30, 11, 30, 40));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_hourTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(-1, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_hourTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(24, 30, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_minuteTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, -1, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_minuteTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 60, 40);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_secondTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_secondTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, 60);
    }

    @Test(groups={"tck"})
    public void test_atTime_int_int_int_int() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atTime(11, 30, 40, 50), LocalDateTime.of(2008, 6, 30, 11, 30, 40, 50));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_int_hourTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(-1, 30, 40, 50);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_int_hourTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(24, 30, 40, 50);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_int_minuteTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, -1, 40, 50);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_int_minuteTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 60, 40, 50);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_int_secondTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, -1, 50);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_int_secondTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, 60, 50);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_int_nanoTooSmall() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, 40, -1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_atTime_int_int_int_int_nanoTooBig() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atTime(11, 30, 40, 1000000000);
    }

    //-----------------------------------------------------------------------
    // atMidnight()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atMidnight() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atMidnight(), LocalDateTime.of(2008, 6, 30, 0, 0));
    }
    
    //-----------------------------------------------------------------------
    // atOffset()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atOffset() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atOffset(OFFSET_PTWO), OffsetDate.of(2008, 6, 30, OFFSET_PTWO));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atOffset_nullZoneOffset() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atOffset((ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // atStartOfDayInZone()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_atStartOfDayInZone() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        assertEquals(t.atStartOfDayInZone(ZONE_PARIS),
                ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 0, 0), ZONE_PARIS));
    }

    @Test(groups={"tck"})
    public void test_atStartOfDayInZone_dstGap() {
        LocalDate t = LocalDate.of(2007, 4, 1);
        assertEquals(t.atStartOfDayInZone(ZONE_GAZA),
                ZonedDateTime.of(LocalDateTime.of(2007, 4, 1, 1, 0), ZONE_GAZA));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_atStartOfDayInZone_nullTimeZone() {
        LocalDate t = LocalDate.of(2008, 6, 30);
        t.atStartOfDayInZone((ZoneId) null);
    }

    //-----------------------------------------------------------------------
    // toEpochDay()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toEpochDay() {
        long date_0000_01_01 = -678941 - 40587;
        
        LocalDate test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i < 700000; i++) {
            assertEquals(test.toEpochDay(), i);
            test = next(test);
        }
        test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i > -2000000; i--) {
            assertEquals(test.toEpochDay(), i);
            test = previous(test);
        }
        
        assertEquals(LocalDate.of(1858, 11, 17).toEpochDay(), -40587);
        assertEquals(LocalDate.of(1, 1, 1).toEpochDay(), -678575 - 40587);
        assertEquals(LocalDate.of(1995, 9, 27).toEpochDay(), 49987 - 40587);
        assertEquals(LocalDate.of(1970, 1, 1).toEpochDay(), 0);
        assertEquals(LocalDate.of(-1, 12, 31).toEpochDay(), -678942 - 40587);
    }

    @Test(groups={"implementation"})
    public void test_toEpochDay_fromMJDays_symmetry() {
        long date_0000_01_01 = -678941 - 40587;
        
        LocalDate test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i < 700000; i++) {
            assertEquals(LocalDate.ofEpochDay(test.toEpochDay()), test);
            test = next(test);
        }
        test = LocalDate.of(0, 1, 1);
        for (long i = date_0000_01_01; i > -2000000; i--) {
            assertEquals(LocalDate.ofEpochDay(test.toEpochDay()), test);
            test = previous(test);
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_comparisons() {
        doTest_comparisons_LocalDate(
            LocalDate.of(Year.MIN_YEAR, 1, 1),
            LocalDate.of(Year.MIN_YEAR, 12, 31),
            LocalDate.of(-1, 1, 1),
            LocalDate.of(-1, 12, 31),
            LocalDate.of(0, 1, 1),
            LocalDate.of(0, 12, 31),
            LocalDate.of(1, 1, 1),
            LocalDate.of(1, 12, 31),
            LocalDate.of(2006, 1, 1),
            LocalDate.of(2006, 12, 31),
            LocalDate.of(2007, 1, 1),
            LocalDate.of(2007, 12, 31),
            LocalDate.of(2008, 1, 1),
            LocalDate.of(2008, 2, 29),
            LocalDate.of(2008, 12, 31),
            LocalDate.of(Year.MAX_YEAR, 1, 1),
            LocalDate.of(Year.MAX_YEAR, 12, 31)
        );
    }

    void doTest_comparisons_LocalDate(LocalDate... localDates) {
        for (int i = 0; i < localDates.length; i++) {
            LocalDate a = localDates[i];
            for (int j = 0; j < localDates.length; j++) {
                LocalDate b = localDates[j];
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
        TEST_2007_07_15.compareTo(null);
    }

    @Test(groups={"tck"})
    public void test_isBefore() {
        assertTrue(TEST_2007_07_15.isBefore(LocalDate.of(2007, 07, 16)));
        assertFalse(TEST_2007_07_15.isBefore(LocalDate.of(2007, 07, 14)));
        assertFalse(TEST_2007_07_15.isBefore(TEST_2007_07_15));
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isBefore_ObjectNull() {
        TEST_2007_07_15.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isAfter_ObjectNull() {
        TEST_2007_07_15.isAfter(null);
    }
    
    @Test(groups={"tck"})
    public void test_isAfter() {
        assertTrue(TEST_2007_07_15.isAfter(LocalDate.of(2007, 07, 14)));
        assertFalse(TEST_2007_07_15.isAfter(LocalDate.of(2007, 07, 16)));
        assertFalse(TEST_2007_07_15.isAfter(TEST_2007_07_15));
    }

    @Test(expectedExceptions=ClassCastException.class, groups={"tck"})
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void compareToNonLocalDate() {
       Comparable c = TEST_2007_07_15;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates" , groups={"tck"})
    public void test_equals_true(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        LocalDate b = LocalDate.of(y, m, d);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleDates", groups={"tck"})
    public void test_equals_false_year_differs(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        LocalDate b = LocalDate.of(y + 1, m, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates", groups={"tck"})
    public void test_equals_false_month_differs(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        LocalDate b = LocalDate.of(y, m + 1, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates", groups={"tck"})
    public void test_equals_false_day_differs(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        LocalDate b = LocalDate.of(y, m, d + 1);
        assertEquals(a.equals(b), false);
    }

    @Test(groups={"tck", "implementation"})
    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15.equals(TEST_2007_07_15), true);
    }

    @Test(groups={"tck"})
    public void test_equals_string_false() {
        assertEquals(TEST_2007_07_15.equals("2007-07-15"), false);
    }

    @Test(groups={"tck"})
    public void test_equals_null_false() {
        assertEquals(TEST_2007_07_15.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates", groups={"tck"})
    public void test_hashCode(int y, int m, int d) {
        LocalDate a = LocalDate.of(y, m, d);
        assertEquals(a.hashCode(), a.hashCode());
        LocalDate b = LocalDate.of(y, m, d);
        assertEquals(a.hashCode(), b.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, "2008-07-05"},
            {2007, 12, 31, "2007-12-31"},
            {999, 12, 31, "0999-12-31"},
            {-1, 1, 2, "-0001-01-02"},
            {9999, 12, 31, "9999-12-31"},
            {-9999, 12, 31, "-9999-12-31"},
            {10000, 1, 1, "+10000-01-01"},
            {-10000, 1, 1, "-10000-01-01"},
            {12345678, 1, 1, "+12345678-01-01"},
            {-12345678, 1, 1, "-12345678-01-01"},
        };
    }

    @Test(dataProvider="sampleToString", groups={"tck"})
    public void test_toString(int y, int m, int d, String expected) {
        LocalDate t = LocalDate.of(y, m, d);
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString_formatter() {
        final LocalDate date = LocalDate.of(2010, 12, 3);
        CalendricalFormatter f = new CalendricalFormatter() {
            @Override
            public String print(DateTime calendrical) {
                assertEquals(calendrical, date);
                return "PRINTED";
            }
            @Override
            public <T> T parse(CharSequence text, Class<T> type) {
                throw new AssertionError();
            }
        };
        String t = date.toString(f);
        assertEquals(t, "PRINTED");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_toString_formatter_null() {
        LocalDate.of(2010, 12, 3).toString(null);
    }

}
