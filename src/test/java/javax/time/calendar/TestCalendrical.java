/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test Calendrical.
 */
@Test
public class TestCalendrical {

    private static final ZoneOffset OFFSET_0100 = ZoneOffset.zoneOffset("+01:00");
    private static final ZoneOffset OFFSET_0200 = ZoneOffset.zoneOffset("+02:00");
    private static final TimeZone ZONE_UTC = TimeZone.UTC;
    private static final TimeZone ZONE_0100 = TimeZone.timeZone(OFFSET_0100);
    @SuppressWarnings("unchecked")
    private static final Map<DateTimeFieldRule, Integer> NULL_MAP = (Map) null;
    private static final DateTimeFieldRule NULL_RULE = null;
    private static final DateTimeFieldRule YEAR_RULE = ISOChronology.yearRule();
    private static final DateTimeFieldRule MOY_RULE = ISOChronology.monthOfYearRule();
    private static final DateTimeFieldRule DOM_RULE = ISOChronology.dayOfMonthRule();
    private static final DateTimeFieldRule DOY_RULE = ISOChronology.dayOfYearRule();
    private static final DateTimeFieldRule DOW_RULE = ISOChronology.dayOfWeekRule();
    private static final DateTimeFieldRule QOY_RULE = ISOChronology.quarterOfYearRule();
    private static final DateTimeFieldRule MOQ_RULE = ISOChronology.monthOfQuarterRule();
    private static final DateTimeFieldRule HOUR_RULE = ISOChronology.hourOfDayRule();
    private static final DateTimeFieldRule AMPM_RULE = ISOChronology.amPmOfDayRule();
    private static final DateTimeFieldRule HOUR_AM_PM_RULE = ISOChronology.hourOfAmPmRule();
    private static final DateTimeFieldRule MIN_RULE = ISOChronology.minuteOfHourRule();
    private static final DateTimeFieldRule SEC_RULE = ISOChronology.secondOfMinuteRule();
    private static final DateTimeFieldRule NANO_RULE = ISOChronology.nanoOfSecondRule();
    private static final DateTimeFieldRule MILLI_RULE = ISOChronology.milliOfDayRule();

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalProvider.class.isAssignableFrom(Calendrical.class));
        assertTrue(DateProvider.class.isAssignableFrom(Calendrical.class));
        assertTrue(TimeProvider.class.isAssignableFrom(Calendrical.class));
        assertTrue(DateTimeProvider.class.isAssignableFrom(Calendrical.class));
        assertTrue(Serializable.class.isAssignableFrom(Calendrical.class));
    }

    @DataProvider(name="simple")
    Object[][] data_simple() {
        return new Object[][] {
            {Calendrical.calendrical()},
            {Calendrical.calendrical(YEAR_RULE, 2008)},
            {Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6)},
            {Calendrical.calendrical(LocalDate.date(2008, 6, 30), null, null, null)},
            {Calendrical.calendrical(LocalDate.date(2008, 6, 30), null, OFFSET_0100, null)},
            {Calendrical.calendrical(null, LocalTime.time(11, 30), OFFSET_0100, null)},
            {Calendrical.calendrical(LocalDate.date(2008, 6, 30), LocalTime.time(11, 30), OFFSET_0100, null)},
        };
    }

    @Test(dataProvider="simple")
    public void test_serialization(Calendrical calendrical) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(calendrical);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), calendrical);
    }

    public void test_immutable() {
        Class<Calendrical> cls = Calendrical.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()) ||
                    (Modifier.isVolatile(field.getModifiers()) && Modifier.isTransient(field.getModifiers())) );
        }
        Constructor<?>[] cons = cls.getDeclaredConstructors();
        for (Constructor<?> con : cons) {
            assertTrue(Modifier.isPrivate(con.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    public void factory_calendrical_empty() {
        Calendrical test = Calendrical.calendrical();
        assertEquals(test.toDateTimeFields().size(), 0);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    //-----------------------------------------------------------------------
    public void factory_calendrical_onePair() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008);
        assertFields(test, YEAR_RULE, 2008);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    public void factory_calendrical_onePair_invalidValueOK() {
        Calendrical test = Calendrical.calendrical(MOY_RULE, -1);
        assertFields(test, MOY_RULE, -1);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_calendrical_onePair_null() {
        Calendrical.calendrical(NULL_RULE, 1);
    }

    //-----------------------------------------------------------------------
    public void factory_calendrical_twoPairs() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertFields(test, YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    public void factory_calendrical_twoPairs_orderNotSignificant() {
        Calendrical test = Calendrical.calendrical(MOY_RULE, 6, YEAR_RULE, 2008);
        assertFields(test, YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    public void factory_calendrical_twoPairs_sameFieldOverwrites() {
        Calendrical test = Calendrical.calendrical(MOY_RULE, 6, MOY_RULE, 7);
        assertFields(test, MOY_RULE, 7);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    public void factory_calendrical_twoPairs_invalidValueOK() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, -1);
        assertFields(test, YEAR_RULE, 2008, MOY_RULE, -1);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_calendrical_twoPairs_nullFirst() {
        Calendrical.calendrical(NULL_RULE, 1, MOY_RULE, 6);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_calendrical_twoPairs_nullSecond() {
        Calendrical.calendrical(MOY_RULE, 6, NULL_RULE, 1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_calendrical_twoPairs_nullBoth() {
        Calendrical.calendrical(NULL_RULE, 1, NULL_RULE, 6);
    }

    //-----------------------------------------------------------------------
    public void factory_calendrical_fields_offset_zone() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        ZoneOffset offset = OFFSET_0100;
        TimeZone zone = ZONE_UTC;
        Calendrical test = Calendrical.calendrical(fields, offset, zone);
        assertSame(test.toDateTimeFields(), fields);
        assertSame(test.getOffset(), OFFSET_0100);
        assertSame(test.getZone(), ZONE_UTC);
    }

    public void factory_calendrical_fields_offset_zone_allNull() {
        DateTimeFields fields = null;
        ZoneOffset offset = null;
        TimeZone zone = null;
        Calendrical test = Calendrical.calendrical(fields, offset, zone);
        assertEquals(test.toDateTimeFields().size(), 0);
        assertSame(test.getOffset(), null);
        assertSame(test.getZone(), null);
    }

    //-----------------------------------------------------------------------
    public void factory_calendrical_date_time_offset_zone() {
        LocalDate date = LocalDate.date(2008, 6, 30);
        LocalTime time = LocalTime.time(11, 30);
        ZoneOffset offset = OFFSET_0100;
        TimeZone zone = ZONE_UTC;
        Calendrical test = Calendrical.calendrical(date, time, offset, zone);
        assertEquals(test.toDateTimeFields().size(), 7);
        assertEquals(test.toDateTimeFields().getValue(YEAR_RULE), 2008);
        assertEquals(test.toDateTimeFields().getValue(MOY_RULE), 6);
        assertEquals(test.toDateTimeFields().getValue(DOM_RULE), 30);
        assertEquals(test.toDateTimeFields().getValue(HOUR_RULE), 11);
        assertEquals(test.toDateTimeFields().getValue(MIN_RULE), 30);
        assertEquals(test.toDateTimeFields().getValue(SEC_RULE), 0);
        assertEquals(test.toDateTimeFields().getValue(NANO_RULE), 0);
        assertSame(test.getOffset(), OFFSET_0100);
        assertSame(test.getZone(), ZONE_UTC);
    }

    public void factory_calendrical_date_time_offset_zone_allNull() {
        LocalDate date = null;
        LocalTime time = null;
        ZoneOffset offset = null;
        TimeZone zone = null;
        Calendrical test = Calendrical.calendrical(date, time, offset, zone);
        assertEquals(test.toDateTimeFields().size(), 0);
        assertSame(test.getOffset(), null);
        assertSame(test.getZone(), null);
    }

    //-----------------------------------------------------------------------
    // isSupported()
    //-----------------------------------------------------------------------
    public void test_isSupported() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.isSupported(YEAR_RULE), true);
        assertEquals(test.isSupported(MOY_RULE), true);
    }

    public void test_isSupported_null() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.isSupported(NULL_RULE), false);
    }

    public void test_isSupported_fieldNotPresent() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.isSupported(DOM_RULE), false);
    }

    public void test_isSupported_empty() {
        Calendrical test = Calendrical.calendrical();
        assertEquals(test.isSupported(YEAR_RULE), false);
        assertEquals(test.isSupported(MOY_RULE), false);
        assertEquals(test.isSupported(DOM_RULE), false);
        assertEquals(test.isSupported(DOW_RULE), false);
        assertEquals(test.isSupported(HOUR_RULE), false);
    }

    public void test_isSupported_fromDate() {
        Calendrical test = Calendrical.calendrical(LocalDate.date(2008, 6, 30), null, null, null);
        assertEquals(test.isSupported(YEAR_RULE), true);
        assertEquals(test.isSupported(MOY_RULE), true);
        assertEquals(test.isSupported(DOM_RULE), true);
        assertEquals(test.isSupported(DOW_RULE), true);  // not in fields, supported from date
        assertEquals(test.isSupported(HOUR_RULE), false);  // not in date
    }

    //-----------------------------------------------------------------------
    // getValue()
    //-----------------------------------------------------------------------
    public void test_getValue() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getValue(YEAR_RULE), 2008);
        assertEquals(test.getValue(MOY_RULE), 6);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_getValue_illegalValue() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 0);
        try {
            test.getValue(MOY_RULE);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), MOY_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValue_null() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.getValue(NULL_RULE);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_getValue_fieldNotPresent() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        try {
            test.getValue(DOW_RULE);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), DOW_RULE);
            throw ex;
        }
    }

    public void test_getValue_fromDate() {
        Calendrical test = Calendrical.calendrical(LocalDate.date(2008, 6, 30), null, null, null);
        assertEquals(test.getValue(YEAR_RULE), 2008);
        assertEquals(test.getValue(MOY_RULE), 6);
        assertEquals(test.getValue(DOW_RULE), 1);  // picked up from date
    }

    public void test_getValue_fromTime() {
        Calendrical test = Calendrical.calendrical(null, LocalTime.time(11, 30), null, null);
        assertEquals(test.getValue(HOUR_RULE), 11);
        assertEquals(test.getValue(AMPM_RULE), 0);  // picked up from time
    }

    //-----------------------------------------------------------------------
    // getValue(boolean)
    //-----------------------------------------------------------------------
    public void test_getValue_validate() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getValue(YEAR_RULE, true), 2008);
        assertEquals(test.getValue(MOY_RULE, true), 6);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_getValue_validate_illegalValue() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 0);
        try {
            test.getValue(MOY_RULE, true);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), MOY_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValue_validate_null() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.getValue(NULL_RULE, true);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_getValue_validate_fieldNotPresent() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        try {
            test.getValue(DOM_RULE, true);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), DOM_RULE);
            throw ex;
        }
    }

    public void test_getValue_validate_fromDate() {
        Calendrical test = Calendrical.calendrical(LocalDate.date(2008, 6, 30), null, null, null);
        assertEquals(test.getValue(YEAR_RULE, true), 2008);
        assertEquals(test.getValue(DOW_RULE, true), 1);  // picked up from date
    }

    public void test_getValue_validate_fromTime() {
        Calendrical test = Calendrical.calendrical(null, LocalTime.time(11, 30), null, null);
        assertEquals(test.getValue(HOUR_RULE, true), 11);
        assertEquals(test.getValue(AMPM_RULE, true), 0);  // picked up from time
    }

    //-----------------------------------------------------------------------
    public void test_getValue_noValidate() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getValue(YEAR_RULE, false), 2008);
        assertEquals(test.getValue(MOY_RULE, false), 6);
    }

    public void test_getValue_noValidate_illegalValue() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 0);
        assertEquals(test.getValue(MOY_RULE, false), 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValue_noValidate_null() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.getValue(NULL_RULE, false);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_getValue_noValidate_fieldNotPresent() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        try {
            test.getValue(DOM_RULE, false);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), DOM_RULE);
            throw ex;
        }
    }

    public void test_getValue_noValidate_fromDate() {
        Calendrical test = Calendrical.calendrical(LocalDate.date(2008, 6, 30), null, null, null);
        assertEquals(test.getValue(YEAR_RULE, false), 2008);
        assertEquals(test.getValue(DOW_RULE, false), 1);  // picked up from date
    }

    public void test_getValue_noValidate_fromTime() {
        Calendrical test = Calendrical.calendrical(null, LocalTime.time(11, 30), null, null);
        assertEquals(test.getValue(HOUR_RULE, false), 11);
        assertEquals(test.getValue(AMPM_RULE, false), 0);  // picked up from time
    }

    //-----------------------------------------------------------------------
    // getValueQuiet()
    //-----------------------------------------------------------------------
    public void test_getValueQuiet() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getValueQuiet(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.getValueQuiet(MOY_RULE), Integer.valueOf(6));
    }

    public void test_getValueQuiet_null() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getValueQuiet(NULL_RULE), null);
    }

    public void test_getValueQuiet_fieldNotPresent() {
        Calendrical test = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getValueQuiet(DOM_RULE), null);
    }

    public void test_getValueQuiet_fromDate() {
        Calendrical test = Calendrical.calendrical(LocalDate.date(2008, 6, 30), null, null, null);
        assertEquals(test.getValueQuiet(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.getValueQuiet(DOW_RULE), Integer.valueOf(1));  // picked up from date
    }

    public void test_getValueQuiet_fromTime() {
        Calendrical test = Calendrical.calendrical(null, LocalTime.time(11, 30), null, null);
        assertEquals(test.getValueQuiet(HOUR_RULE), Integer.valueOf(11));
        assertEquals(test.getValueQuiet(AMPM_RULE), Integer.valueOf(0));  // picked up from time
    }

    //-----------------------------------------------------------------------
    // withFields(DateTimeFields)
    //-----------------------------------------------------------------------
    public void test_withFields() {
        Calendrical base = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields fields = DateTimeFields.fields(DOM_RULE, 30);
        Calendrical test = base.withFields(fields);
        assertFields(test, DOM_RULE, 30);
        assertSame(test.toDateTimeFields(), fields);
        // check original immutable
        assertFields(base, YEAR_RULE, 2008, MOY_RULE, 6);
    }

    public void test_withFields_sameFields() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields newFields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, null, null);
        Calendrical test = base.withFields(newFields);
        assertSame(test, base);
    }

    public void test_withFields_invalidValueOK() {
        Calendrical base = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields fields = DateTimeFields.fields(DOM_RULE, -1);
        Calendrical test = base.withFields(fields);
        assertFields(test, DOM_RULE, -1);
        assertSame(test.toDateTimeFields(), fields);
    }

    public void test_withFields_empty() {
        Calendrical base = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields fields = DateTimeFields.fields();
        Calendrical test = base.withFields(fields);
        assertEquals(test.toDateTimeFields().size(), 0);
        assertSame(test.toDateTimeFields(), fields);
    }

    public void test_withFields_null() {
        Calendrical base = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields fields = null;
        Calendrical test = base.withFields(fields);
        assertEquals(test.toDateTimeFields().size(), 0);
    }

    public void test_withFields_empty_null() {
        Calendrical base = Calendrical.calendrical();
        DateTimeFields fields = null;
        Calendrical test = base.withFields(fields);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withOffset(ZoneOffset)
    //-----------------------------------------------------------------------
    public void test_withOffset() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, OFFSET_0100, ZONE_UTC);
        Calendrical test = base.withOffset(OFFSET_0200);
        assertSame(test.toDateTimeFields(), fields);
        assertSame(test.getOffset(), OFFSET_0200);
        assertSame(test.getZone(), ZONE_UTC);
        // check original immutable
        assertSame(base.toDateTimeFields(), fields);
        assertSame(base.getOffset(), OFFSET_0100);
        assertSame(base.getZone(), ZONE_UTC);
    }

    public void test_withOffset_null() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, OFFSET_0100, ZONE_UTC);
        Calendrical test = base.withOffset(null);
        assertSame(test.toDateTimeFields(), fields);
        assertSame(test.getOffset(), null);
        assertSame(test.getZone(), ZONE_UTC);
    }

    public void test_withOffset_nullToNull() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, null, ZONE_UTC);
        Calendrical test = base.withOffset(null);
        assertSame(test, base);
    }

    public void test_withOffset_same() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, ZoneOffset.zoneOffset(1, 2, 3), ZONE_UTC);
        Calendrical test = base.withOffset(ZoneOffset.zoneOffset(1, 2, 3));
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // withZone(TimeZone)
    //-----------------------------------------------------------------------
    public void test_withZone() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, OFFSET_0100, ZONE_UTC);
        Calendrical test = base.withZone(ZONE_0100);
        assertSame(test.toDateTimeFields(), fields);
        assertSame(test.getOffset(), OFFSET_0100);
        assertSame(test.getZone(), ZONE_0100);
        // check original immutable
        assertSame(base.toDateTimeFields(), fields);
        assertSame(base.getOffset(), OFFSET_0100);
        assertSame(base.getZone(), ZONE_UTC);
    }

    public void test_withZone_null() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, OFFSET_0100, ZONE_UTC);
        Calendrical test = base.withZone(null);
        assertSame(test.toDateTimeFields(), fields);
        assertSame(test.getOffset(), OFFSET_0100);
        assertSame(test.getZone(), null);
    }

    public void test_withZone_nullToNull() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, OFFSET_0100, null);
        Calendrical test = base.withZone(null);
        assertSame(test, base);
    }

    public void test_withZone_same() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, OFFSET_0100, ZONE_UTC);
        Calendrical test = base.withZone(ZONE_UTC);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // toDateTimeFields()
    //-----------------------------------------------------------------------
    public void test_toDateTimeFields() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = Calendrical.calendrical(fields, null, null);
        DateTimeFields test = base.toDateTimeFields();
        assertSame(test, fields);
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    public void test_toLocalDate() {
        LocalDate date = LocalDate.date(2008, 6, 30);
        Calendrical base = Calendrical.calendrical(date, null, null, null);
        LocalDate test = base.toLocalDate();
        assertEquals(test, date);
    }

    public void test_toLocalDate_mergeFields() {
        DateTimeFields fields = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30);
        Calendrical base = Calendrical.calendrical(fields, null, null);
        LocalDate test = base.toLocalDate();
        assertEquals(test, LocalDate.date(2008, 6, 30));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDate_invalidMergeFields() {
        DateTimeFields fields = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 31);
        Calendrical base = Calendrical.calendrical(fields, null, null);
        base.toLocalDate();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDate_empty() {
        Calendrical base = Calendrical.calendrical();
        base.toLocalDate();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDate_insufficientInfo() {
        Calendrical base = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        base.toLocalDate();
    }

    //-----------------------------------------------------------------------
    // toLocalTime()
    //-----------------------------------------------------------------------
    public void test_toLocalTime() {
        LocalTime time = LocalTime.time(11, 30);
        Calendrical base = Calendrical.calendrical(null, time, null, null);
        LocalTime test = base.toLocalTime();
        assertEquals(test, time);
    }

    public void test_toLocalTime_mergeFields() {
        DateTimeFields fields = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        Calendrical base = Calendrical.calendrical(fields, null, null);
        LocalTime test = base.toLocalTime();
        assertEquals(test, LocalTime.time(11, 30));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalTime_invalidMergeFields() {
        DateTimeFields fields = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 70);
        Calendrical base = Calendrical.calendrical(fields, null, null);
        base.toLocalTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalTime_empty() {
        Calendrical base = Calendrical.calendrical();
        base.toLocalTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalTime_insufficientInfo() {
        Calendrical base = Calendrical.calendrical(HOUR_AM_PM_RULE, 11);
        base.toLocalTime();
    }

    //-----------------------------------------------------------------------
    // toLocalDateTime()
    //-----------------------------------------------------------------------
    public void test_toLocalDateTime() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = Calendrical.calendrical(dt.toLocalDate(), dt.toLocalTime(), null, null);
        LocalDateTime test = base.toLocalDateTime();
        assertEquals(test, dt);
    }

    public void test_toLocalDateTime_mergeFields() {
        DateTimeFields fields = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        Calendrical base = Calendrical.calendrical(fields, null, null);
        LocalDateTime test = base.toLocalDateTime();
        assertEquals(test, LocalDateTime.dateTime(2008, 6, 30, 11, 30));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDateTime_invalidMergeFields() {
        DateTimeFields fields = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 70);
        Calendrical base = Calendrical.calendrical(fields, null, null);
        base.toLocalDateTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDateTime_empty() {
        Calendrical base = Calendrical.calendrical();
        base.toLocalDateTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDateTime_insufficientInfo() {
        Calendrical base = Calendrical.calendrical(HOUR_AM_PM_RULE, 11);
        base.toLocalDateTime();
    }

    //-----------------------------------------------------------------------
    // toOffsetDate()
    //-----------------------------------------------------------------------
    public void test_toOffsetDate() {
        LocalDate date = LocalDate.date(2008, 6, 30);
        Calendrical base = Calendrical.calendrical(date, null, OFFSET_0100, null);
        OffsetDate test = base.toOffsetDate();
        assertEquals(test.toLocalDate(), date);
        assertSame(test.getOffset(), OFFSET_0100);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toOffsetDate_noOffset() {
        LocalDate date = LocalDate.date(2008, 6, 30);
        Calendrical base = Calendrical.calendrical(date, null, null, null);
        base.toOffsetDate();
    }

    //-----------------------------------------------------------------------
    // toOffsetTime()
    //-----------------------------------------------------------------------
    public void test_toOffsetTime() {
        LocalTime time = LocalTime.time(11, 30);
        Calendrical base = Calendrical.calendrical(null, time, OFFSET_0100, null);
        OffsetTime test = base.toOffsetTime();
        assertEquals(test.toLocalTime(), time);
        assertSame(test.getOffset(), OFFSET_0100);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toOffsetTime_noOffset() {
        LocalTime time = LocalTime.time(11, 30);
        Calendrical base = Calendrical.calendrical(null, time, null, null);
        base.toOffsetTime();
    }

    //-----------------------------------------------------------------------
    // toOffsetDateTime()
    //-----------------------------------------------------------------------
    public void test_toOffsetDateTime() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = Calendrical.calendrical(dt.toLocalDate(), dt.toLocalTime(), OFFSET_0100, null);
        OffsetDateTime test = base.toOffsetDateTime();
        assertEquals(test.toLocalDateTime(), dt);
        assertSame(test.getOffset(), OFFSET_0100);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toOffsetDateTime_noOffset() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = Calendrical.calendrical(dt.toLocalDate(), dt.toLocalTime(), null, null);
        base.toOffsetDateTime();
    }

    //-----------------------------------------------------------------------
    // toZonedDateTime()
    //-----------------------------------------------------------------------
    public void test_toZonedDateTime() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = Calendrical.calendrical(dt.toLocalDate(), dt.toLocalTime(), OFFSET_0100, ZONE_0100);
        ZonedDateTime test = base.toZonedDateTime();
        assertEquals(test.toLocalDateTime(), dt);
        assertSame(test.getOffset(), OFFSET_0100);
        assertSame(test.getZone(), ZONE_0100);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toZonedDateTime_noOffset() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = Calendrical.calendrical(dt.toLocalDate(), dt.toLocalTime(), null, ZONE_0100);
        base.toZonedDateTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toZonedDateTime_noZone() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = Calendrical.calendrical(dt.toLocalDate(), dt.toLocalTime(), OFFSET_0100, null);
        base.toZonedDateTime();
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    public void test_toCalendrical() {
        Calendrical base = Calendrical.calendrical();
        Calendrical test = base.toCalendrical();
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals0() {
        Calendrical a = Calendrical.calendrical();
        Calendrical b = Calendrical.calendrical();
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_equal() {
        Calendrical a = Calendrical.calendrical(YEAR_RULE, 2008);
        Calendrical b = Calendrical.calendrical(YEAR_RULE, 2008);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_notEqualValue() {
        Calendrical a = Calendrical.calendrical(YEAR_RULE, 2008);
        Calendrical b = Calendrical.calendrical(YEAR_RULE, 2007);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_notEqualField() {
        Calendrical a = Calendrical.calendrical(YEAR_RULE, 2008);
        Calendrical b = Calendrical.calendrical(MOY_RULE, 2008);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_equal() {
        Calendrical a = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical b = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualOneValue() {
        Calendrical a = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical b = Calendrical.calendrical(YEAR_RULE, 2007, MOY_RULE, 6);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualTwoValues() {
        Calendrical a = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical b = Calendrical.calendrical(YEAR_RULE, 2007, MOY_RULE, 5);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualField() {
        Calendrical a = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical b = Calendrical.calendrical(YEAR_RULE, 2008, DOM_RULE, 6);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_dateEqual() {
        DateTimeFields fields = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30);
        LocalDate date = LocalDate.date(2008, 6, 30);
        Calendrical a = Calendrical.calendrical(fields, null, null);
        Calendrical b = Calendrical.calendrical(date, null, null, null);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_dateEqual_fields_notEqual() {
        DateTimeFields fields = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(DOY_RULE, 182);
        LocalDate date = LocalDate.date(2008, 6, 30);
        Calendrical a = Calendrical.calendrical(fields, null, null);
        Calendrical b = Calendrical.calendrical(date, null, null, null);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_fieldsIdentical_equal() {
        DateTimeFields fields = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30);
        Calendrical a = Calendrical.calendrical(fields, null, null);
        Calendrical b = Calendrical.calendrical(fields,  null, null);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_offset_equal() {
        Calendrical a = Calendrical.calendrical(null, OFFSET_0100, null);
        Calendrical b = Calendrical.calendrical(null, ZoneOffset.zoneOffset("+01:00"), null);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_offset_notEqual() {
        Calendrical a = Calendrical.calendrical(null, OFFSET_0100, null);
        Calendrical b = Calendrical.calendrical(null, OFFSET_0200, null);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_offset_notEqual_null() {
        Calendrical a = Calendrical.calendrical(null, OFFSET_0100, null);
        Calendrical b = Calendrical.calendrical(null, null, null);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_zone_equal() {
        Calendrical a = Calendrical.calendrical(null, null, TimeZone.timeZone(OFFSET_0100));
        Calendrical b = Calendrical.calendrical(null, null, ZONE_0100);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_zone_notEqual() {
        Calendrical a = Calendrical.calendrical(null, null, ZONE_UTC);
        Calendrical b = Calendrical.calendrical(null, null, ZONE_0100);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_zone_notEqual_null() {
        Calendrical a = Calendrical.calendrical(null, null, ZONE_UTC);
        Calendrical b = Calendrical.calendrical(null, null, null);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_otherType() {
        Calendrical a = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(a.equals("Rubbish"), false);
    }

    public void test_equals_null() {
        Calendrical a = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(a.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString0() {
        Calendrical base = Calendrical.calendrical();
        String test = base.toString();
        assertEquals(test, "{}");
    }

    public void test_toString1() {
        Calendrical base = Calendrical.calendrical(YEAR_RULE, 2008);
        String test = base.toString();
        assertEquals(test, "{Year=2008}");
    }

    public void test_toString1_offset() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008);
        Calendrical base = Calendrical.calendrical(fields, OFFSET_0100, null);
        String test = base.toString();
        assertEquals(test, "{Year=2008} +01:00");
    }

    public void test_toString1_zone() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008);
        Calendrical base = Calendrical.calendrical(fields, null, ZONE_UTC);
        String test = base.toString();
        assertEquals(test, "{Year=2008} UTC");
    }

    public void test_toString1_offset_zone() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008);
        Calendrical base = Calendrical.calendrical(fields, OFFSET_0100, ZONE_UTC);
        String test = base.toString();
        assertEquals(test, "{Year=2008} +01:00 UTC");
    }

    public void test_toString2() {
        Calendrical base = Calendrical.calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        String test = base.toString();
        assertEquals(test, "{Year=2008, MonthOfYear=6}");
    }

    //-----------------------------------------------------------------------
    private void assertFields(
            Calendrical calendrical,
            DateTimeFieldRule rule1, Integer value1) {
        Map<DateTimeFieldRule, Integer> map = calendrical.toDateTimeFields().toFieldValueMap();
        assertEquals(map.size(), 1);
        assertEquals(map.get(rule1), value1);
    }
    private void assertFields(
            Calendrical calendrical,
            DateTimeFieldRule rule1, Integer value1,
            DateTimeFieldRule rule2, Integer value2) {
        Map<DateTimeFieldRule, Integer> map = calendrical.toDateTimeFields().toFieldValueMap();
        assertEquals(map.size(), 2);
        assertEquals(map.get(rule1), value1);
        assertEquals(map.get(rule2), value2);
    }
//    private void assertFields(
//            Calendrical calendrical,
//            DateTimeFieldRule rule1, Integer value1,
//            DateTimeFieldRule rule2, Integer value2,
//            DateTimeFieldRule rule3, Integer value3) {
//        Map<DateTimeFieldRule, Integer> map = calendrical.toDateTimeFields().toFieldValueMap();
//        assertEquals(map.size(), 3);
//        assertEquals(map.get(rule1), value1);
//        assertEquals(map.get(rule2), value2);
//        assertEquals(map.get(rule3), value3);
//    }
//    private void assertFields(
//            Calendrical calendrical,
//            DateTimeFieldRule rule1, Integer value1,
//            DateTimeFieldRule rule2, Integer value2,
//            DateTimeFieldRule rule3, Integer value3,
//            DateTimeFieldRule rule4, Integer value4) {
//        Map<DateTimeFieldRule, Integer> map = calendrical.toDateTimeFields().toFieldValueMap();
//        assertEquals(map.size(), 4);
//        assertEquals(map.get(rule1), value1);
//        assertEquals(map.get(rule2), value2);
//        assertEquals(map.get(rule3), value3);
//        assertEquals(map.get(rule4), value4);
//    }
//    private void assertFields(
//            Calendrical calendrical,
//            DateTimeFieldRule rule1, Integer value1,
//            DateTimeFieldRule rule2, Integer value2,
//            DateTimeFieldRule rule3, Integer value3,
//            DateTimeFieldRule rule4, Integer value4,
//            DateTimeFieldRule rule5, Integer value5) {
//        Map<DateTimeFieldRule, Integer> map = calendrical.toDateTimeFields().toFieldValueMap();
//        assertEquals(map.size(), 5);
//        assertEquals(map.get(rule1), value1);
//        assertEquals(map.get(rule2), value2);
//        assertEquals(map.get(rule3), value3);
//        assertEquals(map.get(rule4), value4);
//        assertEquals(map.get(rule5), value5);
//    }
}
