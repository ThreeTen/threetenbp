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

import static javax.time.Month.JANUARY;
import static javax.time.Month.JUNE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

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
import javax.time.calendrical.Year;
import javax.time.format.CalendricalFormatter;
import javax.time.zone.ZoneResolver;
import javax.time.zone.ZoneResolvers;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ZonedDateTime.
 */
@Test
public class TestZonedDateTime extends AbstractTest {

    private static final ZoneOffset OFFSET_0100 = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_0200 = ZoneOffset.ofHours(2);
    private static final ZoneOffset OFFSET_0130 = ZoneOffset.of("+01:30");
    private static final ZoneId ZONE_0100 = ZoneId.of(OFFSET_0100);
    private static final ZoneId ZONE_0200 = ZoneId.of(OFFSET_0200);
    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");
    private ZonedDateTime TEST_DATE_TIME;

    @BeforeMethod(groups={"tck","implementation"})
    public void setUp() {
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 11, 30, 59, 500);
        TEST_DATE_TIME = ZonedDateTime.of(dt, ZONE_0100);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(DateTime.class.isAssignableFrom(ZonedDateTime.class));
        assertTrue(Comparable.class.isAssignableFrom(ZonedDateTime.class));
        assertTrue(Serializable.class.isAssignableFrom(ZonedDateTime.class));
    }

    @Test(groups={"tck"})
    public void test_immutable() {
        Class<ZonedDateTime> cls = ZonedDateTime.class;
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
        ZonedDateTime expected = ZonedDateTime.now(Clock.systemDefaultZone());
        ZonedDateTime test = ZonedDateTime.now();
        long diff = Math.abs(test.toLocalTime().toNanoOfDay() - expected.toLocalTime().toNanoOfDay());
        if (diff >= 100000000) {
            // may be date change
            expected = ZonedDateTime.now(Clock.systemDefaultZone());
            test = ZonedDateTime.now();
            diff = Math.abs(test.toLocalTime().toNanoOfDay() - expected.toLocalTime().toNanoOfDay());
        }
        assertTrue(diff < 100000000);  // less than 0.1 secs
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void now_Clock_nullClock() {
        ZonedDateTime.now(null);
    }
    
    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_utc() {
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i).plusNanos(123456789L);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            ZonedDateTime test = ZonedDateTime.now(clock);
            assertEquals(test.getYear(), 1970);
            assertEquals(test.getMonth(), Month.JANUARY);
            assertEquals(test.getDayOfMonth(), (i < 24 * 60 * 60 ? 1 : 2));
            assertEquals(test.getHour(), (i / (60 * 60)) % 24);
            assertEquals(test.getMinute(), (i / 60) % 60);
            assertEquals(test.getSecond(), i % 60);
            assertEquals(test.getNano(), 123456789);
            assertEquals(test.getOffset(), ZoneOffset.UTC);
            assertEquals(test.getZone(), ZoneId.UTC);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_zone() {
        ZoneId zone = ZoneId.of("Europe/London");
        for (int i = 0; i < (2 * 24 * 60 * 60); i++) {
            Instant instant = Instant.ofEpochSecond(i).plusNanos(123456789L);
            ZonedDateTime expected = ZonedDateTime.ofInstant(instant, zone);
            Clock clock = Clock.fixed(expected.toInstant(), zone);
            ZonedDateTime test = ZonedDateTime.now(clock);
            assertEquals(test, expected);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_allSecsInDay_beforeEpoch() {
        LocalTime expected = LocalTime.MIDNIGHT.plusNanos(123456789L);
        for (int i =-1; i >= -(24 * 60 * 60); i--) {
            Instant instant = Instant.ofEpochSecond(i).plusNanos(123456789L);
            Clock clock = Clock.fixed(instant, ZoneId.UTC);
            ZonedDateTime test = ZonedDateTime.now(clock);
            assertEquals(test.getYear(), 1969);
            assertEquals(test.getMonth(), Month.DECEMBER);
            assertEquals(test.getDayOfMonth(), 31);
            expected = expected.minusSeconds(1);
            assertEquals(test.toLocalTime(), expected);
            assertEquals(test.getOffset(), ZoneOffset.UTC);
            assertEquals(test.getZone(), ZoneId.UTC);
        }
    }

    @Test(groups={"tck"})
    public void now_Clock_offsets() {
        ZonedDateTime base = ZonedDateTime.of(OffsetDateTime.of(1970, 1, 1, 12, 0, ZoneOffset.UTC), ZoneId.UTC);
        for (int i = -9; i < 15; i++) {
            ZoneOffset offset = ZoneOffset.ofHours(i);
            Clock clock = Clock.fixed(base.toInstant(), ZoneId.of(offset));
            ZonedDateTime test = ZonedDateTime.now(clock);
            assertEquals(test.getHour(), (12 + i) % 24);
            assertEquals(test.getMinute(), 0);
            assertEquals(test.getSecond(), 0);
            assertEquals(test.getNano(), 0);
            assertEquals(test.getOffset(), offset);
            assertEquals(test.getZone(), ZoneId.of(offset));
        }
    }

    //-----------------------------------------------------------------------
    // dateTime factories
    //-----------------------------------------------------------------------
    void check(ZonedDateTime test, int y, int m, int d, int h, int min, int s, int n, ZoneOffset offset, ZoneId zone) {
        assertEquals(test.getYear(), y);
        assertEquals(test.getMonth().getValue(), m);
        assertEquals(test.getDayOfMonth(), d);
        assertEquals(test.getHour(), h);
        assertEquals(test.getMinute(), min);
        assertEquals(test.getSecond(), s);
        assertEquals(test.getNano(), n);
        assertEquals(test.getOffset(), offset);
        assertEquals(test.getZone(), zone);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intMonthIntHMSN() {
        ZonedDateTime test = ZonedDateTime.of(2008, Month.JUNE, 30, 11, 30, 10, 500, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_intMonthIntHMSN_gap() {
        ZonedDateTime.of(2008, Month.MARCH, 30, 02, 30, 0, 0, ZONE_PARIS);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intMonthIntHMSNResolver() {
        ZonedDateTime test = ZonedDateTime.of(2008, Month.JUNE, 30, 11, 30, 10, 500, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(groups={"tck"})
    public void factory_of_intMonthIntHMSNResolver_gap() {
        ZonedDateTime test = ZonedDateTime.of(2008, Month.MARCH, 30, 2, 30, 0, 0, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 3, 30, 3, 0, 0, 0, OFFSET_0200, ZONE_PARIS);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intsHMSN() {
        ZonedDateTime test = ZonedDateTime.of(2008, 6, 30, 11, 30, 10, 500, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_intsHMSN_gap() {
        ZonedDateTime.of(2008, 3, 30, 02, 30, 0, 0, ZONE_PARIS);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_intsHMSNResolver() {
        ZonedDateTime test = ZonedDateTime.of(2008, 6, 30, 11, 30, 10, 500, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(groups={"tck"})
    public void factory_of_intsHMSNResolver_gap() {
        ZonedDateTime test = ZonedDateTime.of(2008, 3, 30, 2, 30, 0, 0, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 3, 30, 3, 0, 0, 0, OFFSET_0200, ZONE_PARIS);
    }

    //-----------------------------------------------------------------------
    // of(DateProvider, TimeProvider, TimeZone)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_LocalDateLocalTime() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        LocalTime time = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.of(date, time, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTime_nullDate() {
        LocalTime time = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.of((LocalDate) null, time, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTime_nullTime() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        ZonedDateTime.of(date, (LocalTime) null, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTime_nullZone() {
        LocalDate dateProvider = LocalDate.of(2008, 6, 30);
        LocalTime timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.of(dateProvider, timeProvider, null);
    }

    //-----------------------------------------------------------------------
    // of(DateProvider, LocalTime, TimeZone, ZoneResolver)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_LocalDateLocalTimeResolver() {
        LocalDate dateProvider = LocalDate.of(2008, 6, 30);
        LocalTime timeProvider = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.of(dateProvider, timeProvider, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(groups={"tck"})
    public void factory_of_LocalDateLocalTimeResolver_resolverUsed() {
        LocalDate date = LocalDate.of(2008, 3, 30);
        LocalTime time = LocalTime.of(2, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.of(date, time, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 3, 30, 3, 0, 0, 0, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTimeResolver_nullDate() {
        LocalTime time = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.of((LocalDate) null, time, ZONE_PARIS, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTimeResolver_nullTime() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        ZonedDateTime.of(date, (LocalTime) null, ZONE_PARIS, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTimeResolver_nullZone() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        LocalTime time = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.of(date, time, null, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateLocalTimeResolver_nullResolver() {
        LocalDate date = LocalDate.of(2008, 6, 30);
        LocalTime time = LocalTime.of(11, 30, 10, 500);
        ZonedDateTime.of(date, time, ZONE_PARIS, (ZoneResolver) null);
    }

    //-----------------------------------------------------------------------
    // of(DateLocalTime, TimeZone)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_DateLocalTime() {
        LocalDateTime provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.of(provider, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateTime_nullDateTime() {
        ZonedDateTime.of((LocalDateTime) null, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateTime_nullZone() {
        LocalDateTime provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime.of(provider, null);
    }

    //-----------------------------------------------------------------------
    // of(LocalDateTime, TimeZone, ZoneResolver)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_LocalDateTimeResolver() {
        LocalDateTime provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.of(provider, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(groups={"tck"})
    public void factory_of_LocalDateTimeResolver_resolverUsed() {
        LocalDateTime provider = LocalDateTime.of(2008, 3, 30, 2, 30, 10, 500);
        ZonedDateTime test = ZonedDateTime.of(provider, ZONE_PARIS, ZoneResolvers.postTransition());
        check(test, 2008, 3, 30, 3, 0, 0, 0, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateTimeResolver_nullDateTime() {
        ZonedDateTime.of((LocalDateTime) null, ZONE_PARIS, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateTimeResolver_nullZone() {
        LocalDateTime provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime.of(provider, null, ZoneResolvers.strict());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_LocalDateTimeResolver_nullResolver() {
        LocalDateTime provider = LocalDateTime.of(2008, 6, 30, 11, 30, 10, 500);
        ZonedDateTime.of(provider, null, (ZoneResolver) null);
    }

    //-----------------------------------------------------------------------
    // of(OffsetDateTime, TimeZone)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_of_OffsetDateTime() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0200);
        ZonedDateTime test = ZonedDateTime.of(odt, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_OffsetDateTime_inGap() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 3, 30, 2, 30, OFFSET_0100);
        try {
            ZonedDateTime.of(odt, ZONE_PARIS);
        } catch (CalendricalException ex) {
            assertEquals(ex.getMessage().contains("daylight savings gap"), true);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_OffsetDateTime_inOverlap_invalidOfset() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0130);
        try {
            ZonedDateTime.of(odt, ZONE_PARIS);
        } catch (CalendricalException ex) {
            assertEquals(ex.getMessage().contains("invalid for time-zone"), true);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_of_OffsetDateTime_invalidOffset() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0130);
        try {
            ZonedDateTime.of(odt, ZONE_PARIS);
        } catch (CalendricalException ex) {
            assertEquals(ex.getMessage().contains("invalid for time-zone"), true);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_OffsetDateTime_nullDateTime() {
        ZonedDateTime.of((OffsetDateTime) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_of_OffsetDateTime_nullZone() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0100);
        ZonedDateTime.of(odt, null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factoryUTC_ofInstant() {
        Instant instant = Instant.ofEpochSecond(86400 + 5 * 3600 + 10 * 60 + 20);
        ZonedDateTime test = ZonedDateTime.ofInstantUTC(instant);
        check(test, 1970, 1, 2, 5, 10, 20, 0, ZoneOffset.UTC, ZoneId.UTC);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factoryUTC_ofInstant_nullInstant() {
        ZonedDateTime.ofInstantUTC((Instant) null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofInstant_Instant() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0200);
        Instant instant = odt.toInstant();
        ZonedDateTime test = ZonedDateTime.ofInstant(instant, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofInstant_Instant_nullInstant() {
        ZonedDateTime.ofInstant((Instant) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofInstant_Instant_nullZone() {
        Instant instant = Instant.ofEpochSecond(0L);
        ZonedDateTime.ofInstant(instant, null);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofInstant_OffsetDateTime() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0200);
        ZonedDateTime test = ZonedDateTime.ofInstant(odt, ZONE_PARIS);
        check(test, 2008, 6, 30, 11, 30, 10, 500, OFFSET_0200, ZONE_PARIS);
    }

    @Test(groups={"tck"})
    public void factory_ofInstant_OffsetDateTime_inGap() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 3, 30, 2, 30, OFFSET_0100);  // DST gap
        ZonedDateTime test = ZonedDateTime.ofInstant(odt, ZONE_PARIS);
        check(test, 2008, 3, 30, 3, 30, 0, 0, OFFSET_0200, ZONE_PARIS);  // one hour later in summer offset
    }

    @Test(groups={"tck"})
    public void factory_ofInstant_OffsetDateTime_inOverlap_earlier() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0200);  // DST overlap
        ZonedDateTime test = ZonedDateTime.ofInstant(odt, ZONE_PARIS);
        check(test, 2008, 10, 26, 2, 30, 0, 0, OFFSET_0200, ZONE_PARIS);  // same time and offset
    }

    @Test(groups={"tck"})
    public void factory_ofInstant_OffsetDateTime_inOverlap_later() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0100);  // DST overlap
        ZonedDateTime test = ZonedDateTime.ofInstant(odt, ZONE_PARIS);
        check(test, 2008, 10, 26, 2, 30, 0, 0, OFFSET_0100, ZONE_PARIS);  // same time and offset
    }

    @Test(groups={"tck"})
    public void factory_ofInstant_OffsetDateTime_invalidOffset() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0130);
        ZonedDateTime test = ZonedDateTime.ofInstant(odt, ZONE_PARIS);
        check(test, 2008, 6, 30, 12, 0, 10, 500, OFFSET_0200, ZONE_PARIS);  // corrected offset, thus altered time
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofInstant_OffsetDateTime_nullDateTime() {
        ZonedDateTime.ofInstant((OffsetDateTime) null, ZONE_0100);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofInstant_OffsetDateTime_nullZone() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 6, 30, 11, 30, 10, 500, OFFSET_0100);
        ZonedDateTime.ofInstant(odt, null);
    }

    //-----------------------------------------------------------------------
    // ofEpochSecond()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_ofEpochSecond_longOffset_afterEpoch() {
        for (int i = 0; i < 100000; i++) {
            ZonedDateTime test = ZonedDateTime.ofEpochSecond(i, ZONE_0200);
            OffsetDateTime odt = OffsetDateTime.of(1970, 1, 1, 0, 0, ZoneOffset.UTC).withOffsetSameInstant(OFFSET_0200).plusSeconds(i);
            assertEquals(test.toOffsetDateTime(), odt);
            assertEquals(test.getZone(), ZONE_0200);
        }
    }

    @Test(groups={"tck"})
    public void factory_ofEpochSecond_longOffset_beforeEpoch() {
        for (int i = 0; i < 100000; i++) {
            ZonedDateTime test = ZonedDateTime.ofEpochSecond(-i, ZONE_0200);
            OffsetDateTime odt = OffsetDateTime.of(1970, 1, 1, 0, 0, ZoneOffset.UTC).withOffsetSameInstant(OFFSET_0200).minusSeconds(i);
            assertEquals(test.toOffsetDateTime(), odt);
            assertEquals(test.getZone(), ZONE_0200);
        }
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofEpochSecond_longOffset_tooBig() {
        ZonedDateTime.ofEpochSecond(Long.MAX_VALUE, ZONE_PARIS);  // TODO: better test
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void factory_ofEpochSecond_longOffset_tooSmall() {
        ZonedDateTime.ofEpochSecond(Long.MIN_VALUE, ZONE_PARIS);  // TODO: better test
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_ofEpochSecond_longOffset_nullOffset() {
        ZonedDateTime.ofEpochSecond(0L, null);
    }

    //-----------------------------------------------------------------------
    // from()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_factory_CalendricalObject() {
        assertEquals(ZonedDateTime.from(ZonedDateTime.of(2007, 7, 15, 17, 30, 0, 0, ZONE_PARIS)), ZonedDateTime.of(2007, 7, 15, 17, 30, 0, 0, ZONE_PARIS));
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_factory_CalendricalObject_invalid_noDerive() {
        ZonedDateTime.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_CalendricalObject_null() {
        ZonedDateTime.from((DateTime) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleToString", groups={"tck"})
//    public void test_parse(int y, int month, int d, int h, int m, int s, int n, String zoneId, String text) {
//        ZonedDateTime t = ZonedDateTime.parse(text);
//        assertEquals(t.getYear(), y);
//        assertEquals(t.getMonth().getValue(), month);
//        assertEquals(t.getDayOfMonth(), d);
//        assertEquals(t.getHour(), h);
//        assertEquals(t.getMinute(), m);
//        assertEquals(t.getSecond(), s);
//        assertEquals(t.getNano(), n);
//        assertEquals(t.getZone().getID(), zoneId);
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_illegalValue() {
//        ZonedDateTime.parse("2008-06-32T11:15+01:00[Europe/Paris]");
//    }
//
//    @Test(expectedExceptions=CalendricalParseException.class, groups={"tck"})
//    public void factory_parse_invalidValue() {
//        ZonedDateTime.parse("2008-06-31T11:15+01:00[Europe/Paris]");
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void factory_parse_nullText() {
//        ZonedDateTime.parse((String) null);
//    }

    //-----------------------------------------------------------------------
    // parse(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void factory_parse_formatter() {
        final ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2010, 12, 3, 11, 30), ZoneId.of("Europe/London"));
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
        ZonedDateTime test = ZonedDateTime.parse("ANY", f);
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
        ZonedDateTime.parse((String) null, f);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void factory_parse_formatter_nullFormatter() {
        ZonedDateTime.parse("ANY", null);
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleTimes")
    Object[][] provider_sampleTimes() {
        return new Object[][] {
            {2008, 6, 30, 11, 30, 20, 500, ZONE_0100},
            {2008, 6, 30, 11, 0, 0, 0, ZONE_0100},
            {2008, 6, 30, 23, 59, 59, 999999999, ZONE_0100},
            {-1, 1, 1, 0, 0, 0, 0, ZONE_0100},
        };
    }

    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_get(int y, int o, int d, int h, int m, int s, int n, ZoneId zone) {
        LocalDate localDate = LocalDate.of(y, o, d);
        LocalTime localTime = LocalTime.of(h, m, s, n);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneOffset offset = zone.getRules().getOffsetInfo(localDateTime).getEstimatedOffset();
        ZonedDateTime a = ZonedDateTime.of(localDateTime, zone);
        
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
        assertEquals(a.toOffsetDateTime(), OffsetDateTime.of(localDateTime, offset));
        assertEquals(a.toString(), a.toOffsetDateTime().toString() + "[" + zone.toString() + "]");
    }
    
    @Test(dataProvider="sampleTimes", groups={"implementation"})
    public void test_get_same(int y, int o, int d, int h, int m, int s, int n, ZoneId zone) {
        LocalDate localDate = LocalDate.of(y, o, d);
        LocalTime localTime = LocalTime.of(h, m, s, n);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneOffset offset = zone.getRules().getOffsetInfo(localDateTime).getEstimatedOffset();
        ZonedDateTime a = ZonedDateTime.of(localDateTime, zone);

        assertSame(a.getOffset(), offset);
        assertSame(a.getZone(), zone);
        
        assertSame(a.toLocalDate(), localDate);
        assertSame(a.toLocalTime(), localTime);
        assertSame(a.toLocalDateTime(), localDateTime);
    }

    //-----------------------------------------------------------------------
    // get(DateTimeField)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_get_DateTimeField() {
        ZonedDateTime test = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 12, 30, 40, 987654321), ZONE_0100);
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
        ZonedDateTime test = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 12, 30, 40, 987654321), ZONE_0100);
        test.get((DateTimeField) null);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"} )
    public void test_get_DateTimeField_invalidField() {
        TEST_DATE_TIME.get(MockFieldNoValue.INSTANCE);
    }

    //-----------------------------------------------------------------------
    // extract(Class)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_extract_Class() {
        ZonedDateTime test = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 12, 30, 40, 987654321), ZONE_0100);
        assertEquals(test.extract(LocalDate.class), test.toLocalDate());
        assertEquals(test.extract(LocalTime.class), test.toLocalTime());
        assertEquals(test.extract(LocalDateTime.class), test.toLocalDateTime());
        assertEquals(test.extract(OffsetDate.class), test.toOffsetDate());
        assertEquals(test.extract(OffsetTime.class), test.toOffsetTime());
        assertEquals(test.extract(OffsetDateTime.class), test.toOffsetDateTime());
        assertEquals(test.extract(ZonedDateTime.class), test);
        assertEquals(test.extract(ZoneOffset.class), test.getOffset());
        assertEquals(test.extract(ZoneId.class), test.getZone());
        assertEquals(test.extract(Instant.class), test.toInstant());
        assertEquals(test.extract(String.class), null);
        assertEquals(test.extract(BigDecimal.class), null);
        assertEquals(test.extract(null), null);
    }

    //-----------------------------------------------------------------------
    // withDateTime()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDateTime() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 11, 31, 0);
        ZonedDateTime test = base.withDateTime(dt);
        assertEquals(test.toLocalDateTime(), dt);
    }
    
    @Test(groups={"implementation"})
    public void test_withDateTimeSame() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 11, 31, 0);
        ZonedDateTime test = base.withDateTime(dt);
        assertSame(test.getZone(), base.getZone());
    }

    @Test(groups={"implementation"})
    public void test_withDateTime_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 23, 30, 59);
        ZonedDateTime test = base.withDateTime(dt);
        assertSame(test, base);
    }

    @Test(groups={"tck"})
    public void test_withDateTime_retainOffsetResolver1() {
        ZoneId newYork = ZoneId.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30);
        ZonedDateTime base = ZonedDateTime.of(ldt, newYork, ZoneResolvers.preTransition());
        assertEquals(base.getOffset(), ZoneOffset.ofHours(-4));
        ZonedDateTime test = base.withDateTime(LocalDateTime.of(2008, 11, 2, 1, 25));
        assertEquals(test.getOffset(), ZoneOffset.ofHours(-4));
    }

    @Test(groups={"tck"})
    public void test_withDateTime_retainOffsetResolver2() {
        ZoneId newYork = ZoneId.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30);
        ZonedDateTime base = ZonedDateTime.of(ldt, newYork, ZoneResolvers.postTransition());
        assertEquals(base.getOffset(), ZoneOffset.ofHours(-5));
        ZonedDateTime test = base.withDateTime(LocalDateTime.of(2008, 11, 2, 1, 25));
        assertEquals(test.getOffset(), ZoneOffset.ofHours(-5));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withDateTime_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.withDateTime(null);
    }

    //-----------------------------------------------------------------------
    // withDateTime(LocalDateTime,ZoneResolver)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDateTime_resolver() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 11, 31, 0);
        ZonedDateTime test = base.withDateTime(dt, ZoneResolvers.retainOffset());
        assertEquals(test.toLocalDateTime(), dt);
    }

    @Test(groups={"implementation"})
    public void test_withDateTime_resolver_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        LocalDateTime dt = LocalDateTime.of(2008, 6, 30, 23, 30, 59);
        ZonedDateTime test = base.withDateTime(dt, ZoneResolvers.retainOffset());
        assertSame(test, base);
        assertSame(test.getZone(), base.getZone());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withDateTime_resolver_nullDT() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.withDateTime(null, ZoneResolvers.retainOffset());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withDateTime_resolver_nullResolver() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.withDateTime(ldt, null);
    }

    //-----------------------------------------------------------------------
    // withEarlierOffsetAtOverlap()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withEarlierOffsetAtOverlap() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0100);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withEarlierOffsetAtOverlap();
        assertEquals(test.getOffset(), OFFSET_0200);  // offset changed to earlier
    }
    
    @Test(groups={"implementation"})
    public void test_withEarlierOffsetAtOverlapSame() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0100);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withEarlierOffsetAtOverlap();
        assertSame(test.toLocalDateTime(), base.toLocalDateTime());
        assertSame(test.getZone(), base.getZone());
    }

    @Test(groups={"implementation"})
    public void test_withEarlierOffsetAtOverlap_alreadyEarlier() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0200);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withEarlierOffsetAtOverlap();
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withEarlierOffsetAtOverlap_notAtOverlap() {
        LocalDateTime ldt = LocalDateTime.of(2008, 10, 26, 1, 59);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_PARIS);
        ZonedDateTime test = base.withEarlierOffsetAtOverlap();
        assertSame(test, base);
    }
    
    @Test(groups={"implementation"})
    public void test_withEarlierOffsetAtOverlap_fixedZone() {
        LocalDateTime ldt = LocalDateTime.of(2008, 10, 26, 2, 30);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withEarlierOffsetAtOverlap();
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withLaterOffsetAtOverlap()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withLaterOffsetAtOverlap() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0200);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withLaterOffsetAtOverlap();
        assertEquals(test.getOffset(), OFFSET_0100);  // offset changed to later
    }
    
    @Test(groups={"implementation"})
    public void test_withLaterOffsetAtOverlapSame() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0200);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withLaterOffsetAtOverlap();
        assertSame(test.toLocalDateTime(), base.toLocalDateTime());
        assertSame(test.getZone(), base.getZone());
    }

    @Test(groups={"implementation"})
    public void test_withLaterOffsetAtOverlap_alreadyEarlier() {
        OffsetDateTime odt = OffsetDateTime.of(2008, 10, 26, 2, 30, OFFSET_0100);
        ZonedDateTime base = ZonedDateTime.of(odt, ZONE_PARIS);
        ZonedDateTime test = base.withLaterOffsetAtOverlap();
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withLaterOffsetAtOverlap_notAtOverlap() {
        LocalDateTime ldt = LocalDateTime.of(2008, 10, 26, 1, 59);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_PARIS);
        ZonedDateTime test = base.withLaterOffsetAtOverlap();
        assertSame(test, base);
    }

    @Test(groups={"implementation"})
    public void test_withLaterOffsetAtOverlap_fixedZone() {
        LocalDateTime ldt = LocalDateTime.of(2008, 10, 26, 2, 30);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withLaterOffsetAtOverlap();
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withZoneSameLocal()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withZoneSameLocal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0200);
        assertEquals(test.toLocalTime(), base.toLocalTime());
    }
    
    @Test(groups={"implementation"})
    public void test_withZoneSameLocalSame() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0200);
        assertSame(test.getOffset(), OFFSET_0200);
        assertSame(test.getZone(), ZONE_0200);
    }

    @Test(groups={"implementation"})
    public void test_withZoneSameLocal_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0100);
        assertSame(test, base);
    }

    @Test(groups={"tck"})
    public void test_withZoneSameLocal_retainOffsetResolver1() {
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZoneId.of("UTC-04:00") );
        ZonedDateTime test = base.withZoneSameLocal(ZoneId.of("America/New_York"));
        assertEquals(base.getOffset(), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(), ZoneOffset.ofHours(-4));
    }

    @Test(groups={"tck"})
    public void test_withZoneSameLocal_retainOffsetResolver2() {
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZoneId.of("UTC-05:00") );
        ZonedDateTime test = base.withZoneSameLocal(ZoneId.of("America/New_York"));
        assertEquals(base.getOffset(), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(), ZoneOffset.ofHours(-5));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withZoneSameLocal_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.withZoneSameLocal(null);
    }

    //-----------------------------------------------------------------------
    // withZoneSameLocal(TimeZone,ZoneResolver)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withZoneSameLocal_ZoneResolver() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0200, ZoneResolvers.retainOffset());
        assertEquals(test.toLocalTime(), base.toLocalTime());
    }
    
    @Test(groups={"implementation"})
    public void test_withZoneSameLocal_ZoneResolver_same() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0200, ZoneResolvers.retainOffset());
	    assertSame(test.getOffset(), OFFSET_0200);
	    assertSame(test.getZone(), ZONE_0200);
    }

    @Test(groups={"implementation"})
    public void test_withZoneSameLocal_ZoneResolver_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameLocal(ZONE_0100, ZoneResolvers.retainOffset());
        assertSame(test, base);
    }

    @Test(groups={"tck"})
    public void test_withZoneSameLocal_ZoneResolver_retainOffsetResolver1() {
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZoneId.of("UTC-04:00") );
        ZonedDateTime test = base.withZoneSameLocal(ZoneId.of("America/New_York"), ZoneResolvers.retainOffset());
        assertEquals(base.getOffset(), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(), ZoneOffset.ofHours(-4));
    }

    @Test(groups={"tck"})
    public void test_withZoneSameLocal_ZoneResolver_retainOffsetResolver2() {
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 2, 1, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZoneId.of("UTC-05:00") );
        ZonedDateTime test = base.withZoneSameLocal(ZoneId.of("America/New_York"), ZoneResolvers.retainOffset());
        assertEquals(base.getOffset(), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(), ZoneOffset.ofHours(-5));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withZoneSameLocal_ZoneResolver_nullZone() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.withZoneSameLocal(null, ZoneResolvers.retainOffset());
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withZoneSameLocal_ZoneResolver_nullResolver() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.withZoneSameLocal(ZONE_0100, (ZoneResolver) null);
    }

    //-----------------------------------------------------------------------
    // withZoneSameInstant()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withZoneSameInstant() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameInstant(ZONE_0200);
        ZonedDateTime expected = ZonedDateTime.of(ldt.plusHours(1), ZONE_0200);
        assertEquals(test, expected);
    }

    @Test(groups={"implementation"})
    public void test_withZoneSameInstant_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withZoneSameInstant(ZONE_0100);
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_withZoneSameInstant_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.withZoneSameInstant(null);
    }

    //-----------------------------------------------------------------------
    // with(DateTimeAdjuster)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_with_DateAdjuster() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.with(Year.of(2007));
        assertEquals(test, ZonedDateTime.of(ldt.withYear(2007), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_with_DateAdjuster_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.with(new DateTimeAdjuster() {
            @Override
            public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
                return calendrical;
            }
        });
        assertSame(test, base);
    }

    @Test(groups={"tck"})
    public void test_with_DateAdjuster_retainOffsetResolver1() {
        ZoneId newYork = ZoneId.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 1, 1, 30);
        ZonedDateTime base = ZonedDateTime.of(ldt, newYork);
        assertEquals(base.getOffset(), ZoneOffset.ofHours(-4));
        ZonedDateTime test = base.with(LocalDate.of(2008, 11, 2));
        assertEquals(test.getOffset(), ZoneOffset.ofHours(-4));
    }

    @Test(groups={"tck"})
    public void test_with_DateAdjuster_retainOffsetResolver2() {
        ZoneId newYork = ZoneId.of("America/New_York");
        LocalDateTime ldt = LocalDateTime.of(2008, 11, 3, 1, 30);
        ZonedDateTime base = ZonedDateTime.of(ldt, newYork);
        assertEquals(base.getOffset(), ZoneOffset.ofHours(-5));
        ZonedDateTime test = base.with(LocalDate.of(2008, 11, 2));
        assertEquals(test.getOffset(), ZoneOffset.ofHours(-5));
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_with_DateAdjuster_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.with((DateTimeAdjuster) null);
    }

    //-----------------------------------------------------------------------
    // with(DateAdjuster,ZoneResolver)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_with_DateAdjuster_resolver() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.with(Year.of(2007), ZoneResolvers.retainOffset());
        assertEquals(test, ZonedDateTime.of(ldt.withYear(2007), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_with_DateAdjuster_resolver_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.with(new DateTimeAdjuster() {
            @Override
            public AdjustableDateTime doAdjustment(AdjustableDateTime calendrical) {
                return calendrical;
            }
        }, ZoneResolvers.retainOffset());
        assertSame(test, base);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_with_DateAdjuster_resolver_nullAdjuster() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.with((DateTimeAdjuster) null, ZoneResolvers.retainOffset());
    }

//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_with_DateAdjuster_resolver_nullResolver() {
//        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
//        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
//        base.with(ldt.toLocalDate(), null);
//    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withYear_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withYear(2007);
        assertEquals(test, ZonedDateTime.of(ldt.withYear(2007), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_withYear_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withYear(2008);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // with(Month)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withMonth_Month_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.with(JANUARY);
        assertEquals(test, ZonedDateTime.of(ldt.withMonth(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_withMonth_Month_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.with(JUNE);
        assertSame(test, base);
    }

    @Test(expectedExceptions = NullPointerException.class, groups={"tck"})
    public void test_withMonth_Month_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        base.with((Month) null);
    }

    //-----------------------------------------------------------------------
    // withMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withMonth_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withMonth(1);
        assertEquals(test, ZonedDateTime.of(ldt.withMonth(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_withMonth_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withMonth(6);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDayOfMonth_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfMonth(15);
        assertEquals(test, ZonedDateTime.of(ldt.withDayOfMonth(15), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_withDayOfMonth_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfMonth(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDayOfYear()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDayOfYear_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfYear(33);
        assertEquals(test, ZonedDateTime.of(ldt.withDayOfYear(33), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_withDayOfYear_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withDayOfYear(31 + 29 + 31 + 30 + 31 + 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withDate()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withDate() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withDate(2007, 1, 1);
        ZonedDateTime expected = ZonedDateTime.of(ldt.withDate(2007, 1, 1), ZONE_0100);
        assertEquals(test, expected);
    }

    @Test(groups={"implementation"})
    public void test_withDate_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withDate(2008, 6, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withHour()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withHourr_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withHour(15);
        assertEquals(test, ZonedDateTime.of(ldt.withHour(15), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_withHour_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withHour(23);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withMinute()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withMinute_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withMinute(15);
        assertEquals(test, ZonedDateTime.of(ldt.withMinute(15), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_withMinute_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withMinute(30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withSecond()
    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_withSecond_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withSecond(15);
        assertEquals(test, ZonedDateTime.of(ldt.withSecond(15), ZONE_0100));
    }

    @Test(groups={"tck"})
    public void test_withSecond_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withSecond(59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withNano()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withNanoOfSecond_normal() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 1);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withNano(15);
        assertEquals(test, ZonedDateTime.of(ldt.withNano(15), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_withNanoOfSecond_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 1);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withNano(1);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withTime()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withTime_HM() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 0, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(12, 10);
        ZonedDateTime expected = ZonedDateTime.of(ldt.withTime(12, 10), ZONE_0100);
        assertEquals(test, expected);
    }

    @Test(groups={"implementation"})
    public void test_withTime_HM_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 0, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(23, 30);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withTime_HMS() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(12, 10, 9);
        ZonedDateTime expected = ZonedDateTime.of(ldt.withTime(12, 10, 9), ZONE_0100);
        assertEquals(test, expected);
    }

    @Test(groups={"implementation"})
    public void test_withTime_HMS_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(23, 30, 59);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_withTime_HMSN() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(12, 10, 9, 8);
        ZonedDateTime expected = ZonedDateTime.of(ldt.withTime(12, 10, 9, 8), ZONE_0100);
        assertEquals(test, expected);
    }
    
    @Test(groups={"implementation"})
    public void test_withTime_HMSN_noChange() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 500);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.withTime(23, 30, 59, 500);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plus_Period() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2009, 1, 1, 12, 30, 59, 500), ZONE_0100);
        assertEquals(t.plus(period), expected);
    }

    @Test(groups={"implementation"})
    public void test_plus_Period_zero() {
        ZonedDateTime t = TEST_DATE_TIME.plus(Period.ZERO_DAYS);
        assertSame(t, TEST_DATE_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plus_PeriodProvider_null() {
        TEST_DATE_TIME.plus((Period) null);
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusYears() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusYears(1);
        assertEquals(test, ZonedDateTime.of(ldt.plusYears(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_plusYears_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusYears(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusMonths() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMonths(1);
        assertEquals(test, ZonedDateTime.of(ldt.plusMonths(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_plusMonths_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMonths(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusWeeks() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusWeeks(1);
        assertEquals(test, ZonedDateTime.of(ldt.plusWeeks(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_plusWeeks_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusDays() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusDays(1);
        assertEquals(test, ZonedDateTime.of(ldt.plusDays(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_plusDays_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusHours()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusHours() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusHours(13);
        assertEquals(test, ZonedDateTime.of(ldt.plusHours(13), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_plusHours_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusMinutes()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusMinutes() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMinutes(30);
        assertEquals(test, ZonedDateTime.of(ldt.plusMinutes(30), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_plusMinutes_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusSeconds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusSeconds() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusSeconds(1);
        assertEquals(test, ZonedDateTime.of(ldt.plusSeconds(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_plusSeconds_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // plusNanos()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusNanos() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusNanos(1);
        assertEquals(test, ZonedDateTime.of(ldt.plusNanos(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_plusNanos_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.plusNanos(0);
        assertSame(test, base);
    }

//    //-----------------------------------------------------------------------
//    // plusDuration(Period)
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_plusDuration_PeriodProvider() {
//        PeriodProvider duration = Period.ofTimeFields(4, 5, 6);
//        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
//        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 16, 36, 5, 500), ZONE_0100);
//        assertEquals(t.plusDuration(duration), expected);
//    }
//
//    @Test(groups={"implementation"})
//    public void test_plusDuration_PeriodProvider_zero() {
//        ZonedDateTime t = TEST_DATE_TIME.plusDuration(Period.ZERO);
//        assertSame(t, TEST_DATE_TIME);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_plusDuration_PeriodProvider_null() {
//        TEST_DATE_TIME.plusDuration((PeriodProvider) null);
//    }

    //-----------------------------------------------------------------------
    // plusDuration(Duration)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusDuration_Duration() {
        Duration provider = Duration.ofSeconds(4L * 60 * 60 + 5L * 60 + 6L);
        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 16, 36, 5, 500), ZONE_0100);
        assertEquals(t.plusDuration(provider), expected);
    }

    @Test(groups={"implementation"})
    public void test_plusDuration_Duration_zero() {
        ZonedDateTime t = TEST_DATE_TIME.plusDuration(Duration.ZERO);
        assertSame(t, TEST_DATE_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_plusDuration_Duration_null() {
        TEST_DATE_TIME.plusDuration((Duration) null);
    }

    //-----------------------------------------------------------------------
    // plusDuration(int,int,int,long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_plusDuration_intintintlong() {
        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 16, 36, 5, 507), ZONE_0100);
        assertEquals(t.plusDuration(4, 5, 6, 7), expected);
    }

    @Test(groups={"implementation"})
    public void test_plusDuration_intintintlong_zero() {
        ZonedDateTime t = TEST_DATE_TIME.plusDuration(0, 0, 0, 0);
        assertSame(t, TEST_DATE_TIME);
    }

    //-----------------------------------------------------------------------
    // minus(Period)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minus_Period() {
        Period period = Period.of(7, LocalPeriodUnit.MONTHS);
        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2007, 11, 1, 12, 30, 59, 500), ZONE_0100);
        assertEquals(t.minus(period), expected);
    }

    @Test(groups={"implementation"})
    public void test_minus_Period_zero() {
        ZonedDateTime t = TEST_DATE_TIME.minus(Period.ZERO_DAYS);
        assertSame(t, TEST_DATE_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minus_Period_null() {
        TEST_DATE_TIME.minus((Period) null);
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusYears() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusYears(1);
        assertEquals(test, ZonedDateTime.of(ldt.minusYears(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_minusYears_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusYears(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusMonths() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusMonths(1);
        assertEquals(test, ZonedDateTime.of(ldt.minusMonths(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_minusMonths_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusMonths(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusWeeks() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusWeeks(1);
        assertEquals(test, ZonedDateTime.of(ldt.minusWeeks(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_minusWeeks_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusWeeks(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusDays() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusDays(1);
        assertEquals(test, ZonedDateTime.of(ldt.minusDays(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_minusDays_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusDays(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusHours()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusHours() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusHours(13);
        assertEquals(test, ZonedDateTime.of(ldt.minusHours(13), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_minusHours_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusHours(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusMinutes()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusMinutes() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusMinutes(30);
        assertEquals(test, ZonedDateTime.of(ldt.minusMinutes(30), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_minusMinutes_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusMinutes(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusSeconds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusSeconds() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusSeconds(1);
        assertEquals(test, ZonedDateTime.of(ldt.minusSeconds(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_minusSeconds_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusSeconds(0);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // minusNanos()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusNanos() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusNanos(1);
        assertEquals(test, ZonedDateTime.of(ldt.minusNanos(1), ZONE_0100));
    }

    @Test(groups={"implementation"})
    public void test_minusNanos_zero() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime base = ZonedDateTime.of(ldt, ZONE_0100);
        ZonedDateTime test = base.minusNanos(0);
        assertSame(test, base);
    }

//    //-----------------------------------------------------------------------
//    // minusDuration(PeriodProvider)
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_minusDuration_PeriodProvider() {
//        PeriodProvider provider = Period.ofTimeFields(4, 5, 6);
//        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
//        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 8, 25, 53, 500), ZONE_0100);
//        assertEquals(t.minusDuration(provider), expected);
//    }
//
//    @Test(groups={"implementation"})
//    public void test_minusDuration_PeriodProvider_zero() {
//        ZonedDateTime t = TEST_DATE_TIME.minusDuration(Period.ZERO);
//        assertSame(t, TEST_DATE_TIME);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
//    public void test_minusDuration_PeriodProvider_null() {
//        TEST_DATE_TIME.minusDuration((PeriodProvider) null);
//    }

    //-----------------------------------------------------------------------
    // minusDuration(Duration)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusDuration_Duration() {
        Duration duration = Duration.ofSeconds(4L * 60 * 60 + 5L * 60 + 6L);
        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 8, 25, 53, 500), ZONE_0100);
        assertEquals(t.minusDuration(duration), expected);
    }

    @Test(groups={"implementation"})
    public void test_minusDuration_Duration_zero() {
        ZonedDateTime t = TEST_DATE_TIME.minusDuration(Duration.ZERO);
        assertSame(t, TEST_DATE_TIME);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_minusDuration_Duration_null() {
        TEST_DATE_TIME.minusDuration((Duration) null);
    }

    //-----------------------------------------------------------------------
    // minusDuration(int,int,int,long)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_minusDuration_intintintlong() {
        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 12, 30, 59, 500), ZONE_0100);
        ZonedDateTime expected = ZonedDateTime.of(LocalDateTime.of(2008, 6, 1, 8, 25, 53, 493), ZONE_0100);
        assertEquals(t.minusDuration(4, 5, 6, 7), expected);
    }

    @Test(groups={"implementation"})
    public void test_minusDuration_intintintlong_zero() {
        ZonedDateTime t = TEST_DATE_TIME.minusDuration(0, 0, 0, 0);
        assertSame(t, TEST_DATE_TIME);
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
    public void test_compareTo_time1() {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30, 39), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30, 41), ZONE_0100);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_time2() {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30, 40, 4), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30, 40, 5), ZONE_0100);  // a is before b due to time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_offset1() {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30, 41), ZONE_0200);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30, 39), ZONE_0100);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_offset2() {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30, 40, 5), ZoneId.of("UTC+01:01"));
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 30, 40, 4), ZONE_0100);  // a is before b due to offset
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_both() {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 50), ZONE_0200);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 20), ZONE_0100);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_bothNanos() {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 20, 40, 5), ZONE_0200);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 10, 20, 40, 6), ZONE_0100);  // a is before b on instant scale
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(groups={"tck"})
    public void test_compareTo_hourDifference() {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 10, 0), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, 11, 0), ZONE_0200);  // a is before b despite being same time-line time
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
        assertEquals(a.compareTo(a) == 0, true);
        assertEquals(b.compareTo(b) == 0, true);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_compareTo_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.of(ldt, ZONE_0100);
        a.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // isBefore()
    //-----------------------------------------------------------------------
    @DataProvider(name="IsBefore")
    Object[][] data_isBefore() {
        return new Object[][] {
            {11, 30, ZONE_0100, 11, 31, ZONE_0100, true}, // a is before b due to time
            {11, 30, ZONE_0200, 11, 30, ZONE_0100, true}, // a is before b due to offset
            {11, 30, ZONE_0200, 10, 30, ZONE_0100, false}, // a is equal b due to same instant
        };
    }

    @Test(dataProvider="IsBefore", groups={"tck"})
    public void test_isBefore(int hour1, int minute1, ZoneId zone1, int hour2, int minute2, ZoneId zone2, boolean expected) {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, hour1, minute1), zone1);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, hour2, minute2), zone2);
        assertEquals(a.isBefore(b), expected);
        assertEquals(b.isBefore(a), false);
        assertEquals(a.isBefore(a), false);
        assertEquals(b.isBefore(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isBefore_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.of(ldt, ZONE_0100);
        a.isBefore(null);
    }

    //-----------------------------------------------------------------------
    // equalInstant()
    //-----------------------------------------------------------------------
    @DataProvider(name="equalInstant")
    Object[][] data_equalInstant() {
        return new Object[][] {
            {11, 31, ZONE_0100, 11, 30, ZONE_0100, false}, // a is after b due to time
            {11, 30, ZONE_0100, 11, 30, ZONE_0200, false}, // a is after b due to offset
            {11, 30, ZONE_0200, 10, 30, ZONE_0100, true}, // a is equal b due to same instant
        };
    }

    @Test(dataProvider="equalInstant", groups={"tck"})
    public void test_equalInstant(int hour1, int minute1, ZoneId zone1, int hour2, int minute2, ZoneId zone2, boolean expected) {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, hour1, minute1), zone1);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, hour2, minute2), zone2);
        assertEquals(a.equalInstant(b), expected);
        assertEquals(b.equalInstant(a), expected);
        assertEquals(a.equalInstant(a), true);
        assertEquals(b.equalInstant(b), true);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_equalInstant_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.of(ldt, ZONE_0100);
        a.equalInstant(null);
    }

    //-----------------------------------------------------------------------
    // isAfter()
    //-----------------------------------------------------------------------
    @DataProvider(name="IsAfter")
    Object[][] data_isAfter() {
        return new Object[][] {
            {11, 31, ZONE_0100, 11, 30, ZONE_0100, true}, // a is after b due to time
            {11, 30, ZONE_0100, 11, 30, ZONE_0200, true}, // a is after b due to offset
            {11, 30, ZONE_0200, 10, 30, ZONE_0100, false}, // a is equal b due to same instant
        };
    }

    @Test(dataProvider="IsAfter", groups={"tck"})
    public void test_isAfter(int hour1, int minute1, ZoneId zone1, int hour2, int minute2, ZoneId zone2, boolean expected) {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, hour1, minute1), zone1);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(2008, 6, 30, hour2, minute2), zone2);
        assertEquals(a.isAfter(b), expected);
        assertEquals(b.isAfter(a), false);
        assertEquals(a.isAfter(a), false);
        assertEquals(b.isAfter(b), false);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isAfter_null() {
        LocalDateTime ldt = LocalDateTime.of(2008, 6, 30, 23, 30, 59, 0);
        ZonedDateTime a = ZonedDateTime.of(ldt, ZONE_0100);
        a.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_true(int y, int o, int d, int h, int m, int s, int n, ZoneId ignored) {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_year_differs(int y, int o, int d, int h, int m, int s, int n, ZoneId ignored) {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(y + 1, o, d, h, m, s, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_hour_differs(int y, int o, int d, int h, int m, int s, int n, ZoneId ignored) {
        h = (h == 23 ? 22 : h);
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(y, o, d, h + 1, m, s, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_minute_differs(int y, int o, int d, int h, int m, int s, int n, ZoneId ignored) {
        m = (m == 59 ? 58 : m);
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m + 1, s, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_second_differs(int y, int o, int d, int h, int m, int s, int n, ZoneId ignored) {
        s = (s == 59 ? 58 : s);
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s + 1, n), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_nano_differs(int y, int o, int d, int h, int m, int s, int n, ZoneId ignored) {
        n = (n == 999999999 ? 999999998 : n);
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n + 1), ZONE_0100);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleTimes", groups={"tck"})
    public void test_equals_false_offset_differs(int y, int o, int d, int h, int m, int s, int n, ZoneId ignored) {
        ZonedDateTime a = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0100);
        ZonedDateTime b = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZONE_0200);
        assertEquals(a.equals(b), false);
    }

    @Test(groups={"tck"})
    public void test_equals_itself_true() {
        assertEquals(TEST_DATE_TIME.equals(TEST_DATE_TIME), true);
    }

    @Test(groups={"tck"})
    public void test_equals_string_false() {
        assertEquals(TEST_DATE_TIME.equals("2007-07-15"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 6, 30, 11, 30, 59, 0, "UTC", "2008-06-30T11:30:59Z[UTC]"},
            {2008, 6, 30, 11, 30, 59, 0, "UTC+01:00", "2008-06-30T11:30:59+01:00[UTC+01:00]"},
            {2008, 6, 30, 11, 30, 59, 999000000, "UTC", "2008-06-30T11:30:59.999Z[UTC]"},
            {2008, 6, 30, 11, 30, 59, 999000000, "UTC+01:00", "2008-06-30T11:30:59.999+01:00[UTC+01:00]"},
            {2008, 6, 30, 11, 30, 59, 999000, "UTC", "2008-06-30T11:30:59.000999Z[UTC]"},
            {2008, 6, 30, 11, 30, 59, 999000, "UTC+01:00", "2008-06-30T11:30:59.000999+01:00[UTC+01:00]"},
            {2008, 6, 30, 11, 30, 59, 999, "UTC", "2008-06-30T11:30:59.000000999Z[UTC]"},
            {2008, 6, 30, 11, 30, 59, 999, "UTC+01:00", "2008-06-30T11:30:59.000000999+01:00[UTC+01:00]"},
            
            {2008, 6, 30, 11, 30, 59, 999, "Europe/London", "2008-06-30T11:30:59.000000999+01:00[Europe/London]"},
        };
    }

    @Test(dataProvider="sampleToString", groups={"tck"})
    public void test_toString(int y, int o, int d, int h, int m, int s, int n, String zoneId, String expected) {
        ZonedDateTime t = ZonedDateTime.of(LocalDateTime.of(y, o, d, h, m, s, n), ZoneId.of(zoneId));
        String str = t.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(CalendricalFormatter)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString_formatter() {
        final ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2010, 12, 3, 11, 30), ZONE_PARIS);
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
        ZonedDateTime.of(LocalDateTime.of(2010, 12, 3, 11, 30), ZONE_PARIS).toString(null);
    }

}
