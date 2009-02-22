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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test Calendrical.
 */
@Test
public class TestCalendrical {

    private static final LocalDate DATE_2008_06_30 = LocalDate.date(2008, 6, 30);
    private static final LocalTime TIME_10_15_30 = LocalTime.time(10, 15, 30);
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
            {new Calendrical()},
            {new Calendrical(YEAR_RULE, 2008)},
            {new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6)},
            {new Calendrical(DATE_2008_06_30, null, null, null)},
            {new Calendrical(DATE_2008_06_30, null, OFFSET_0100, null)},
            {new Calendrical(null, TIME_10_15_30, OFFSET_0100, null)},
            {new Calendrical(DATE_2008_06_30, TIME_10_15_30, OFFSET_0100, null)},
            {new Calendrical(DATE_2008_06_30, TIME_10_15_30, OFFSET_0100, ZONE_UTC)},
        };
    }

    @Test(dataProvider="simple")
    public void test_serialization(Calendrical calendrical) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(calendrical);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), calendrical);
    }

    public void test_class() {
        Class<Calendrical> cls = Calendrical.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
    }

    //-----------------------------------------------------------------------
    // constructors
    //-----------------------------------------------------------------------
    public void constructor_calendrical_empty() {
        Calendrical test = new Calendrical();
        assertEquals(test.getFieldMap().size(), 0);
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    //-----------------------------------------------------------------------
    public void constructor_calendrical_onePair() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008);
        assertFields(test, YEAR_RULE, 2008);
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    public void constructor_calendrical_onePair_invalidValueOK() {
        Calendrical test = new Calendrical(MOY_RULE, -1);
        assertFields(test, MOY_RULE, -1);
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_calendrical_onePair_null() {
        new Calendrical(NULL_RULE, 1);
    }

    //-----------------------------------------------------------------------
    public void constructor_calendrical_twoPairs() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertFields(test, YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    public void constructor_calendrical_twoPairs_orderNotSignificant() {
        Calendrical test = new Calendrical(MOY_RULE, 6, YEAR_RULE, 2008);
        assertFields(test, YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    public void constructor_calendrical_twoPairs_sameFieldOverwrites() {
        Calendrical test = new Calendrical(MOY_RULE, 6, MOY_RULE, 7);
        assertFields(test, MOY_RULE, 7);
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    public void constructor_calendrical_twoPairs_invalidValueOK() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, -1);
        assertFields(test, YEAR_RULE, 2008, MOY_RULE, -1);
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_calendrical_twoPairs_nullFirst() {
        new Calendrical(NULL_RULE, 1, MOY_RULE, 6);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_calendrical_twoPairs_nullSecond() {
        new Calendrical(MOY_RULE, 6, NULL_RULE, 1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_calendrical_twoPairs_nullBoth() {
        new Calendrical(NULL_RULE, 1, NULL_RULE, 6);
    }

    //-----------------------------------------------------------------------
    public void constructor_calendrical_dateTimeOffsetZone() {
        Calendrical test = new Calendrical(DATE_2008_06_30, TIME_10_15_30, OFFSET_0100, ZONE_UTC);
        assertEquals(test.getFieldMap().size(), 0);
        assertSame(test.getDate(), DATE_2008_06_30);
        assertSame(test.getTime(), TIME_10_15_30);
        assertSame(test.getOffset(), OFFSET_0100);
        assertSame(test.getZone(), ZONE_UTC);
    }

    public void constructor_calendrical_dateTimeOffsetZone_null() {
        Calendrical test = new Calendrical(null, null, null, null);
        assertEquals(test.getFieldMap().size(), 0);
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    //-----------------------------------------------------------------------
    public void constructor_calendrical_fields() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical test = new Calendrical(fields);
        assertFields(test, YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void constructor_calendrical_fields_null() {
        DateTimeFields fields = null;
        new Calendrical(fields);
    }

//    //-----------------------------------------------------------------------
//    public void constructor_calendrical_fields_offset_zone() {
//        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        ZoneOffset offset = OFFSET_0100;
//        TimeZone zone = ZONE_UTC;
//        Calendrical test = new Calendrical(fields, offset, zone);
//        assertSame(test.toDateTimeFields(), fields);
//        assertSame(test.getOffset(), OFFSET_0100);
//        assertSame(test.getZone(), ZONE_UTC);
//    }
//
//    public void constructor_calendrical_fields_offset_zone_allNull() {
//        DateTimeFields fields = null;
//        ZoneOffset offset = null;
//        TimeZone zone = null;
//        Calendrical test = new Calendrical(fields, offset, zone);
//        assertEquals(test.toDateTimeFields().size(), 0);
//        assertSame(test.getOffset(), null);
//        assertSame(test.getZone(), null);
//    }
//
//    //-----------------------------------------------------------------------
//    public void constructor_calendrical_date_time_offset_zone() {
//        LocalDate date = LocalDate.date(2008, 6, 30);
//        LocalTime time = LocalTime.time(11, 30);
//        ZoneOffset offset = OFFSET_0100;
//        TimeZone zone = ZONE_UTC;
//        Calendrical test = new Calendrical(date, time, offset, zone);
//        assertEquals(test.toDateTimeFields().size(), 7);
//        assertEquals(test.toDateTimeFields().getValue(YEAR_RULE), 2008);
//        assertEquals(test.toDateTimeFields().getValue(MOY_RULE), 6);
//        assertEquals(test.toDateTimeFields().getValue(DOM_RULE), 30);
//        assertEquals(test.toDateTimeFields().getValue(HOUR_RULE), 11);
//        assertEquals(test.toDateTimeFields().getValue(MIN_RULE), 30);
//        assertEquals(test.toDateTimeFields().getValue(SEC_RULE), 0);
//        assertEquals(test.toDateTimeFields().getValue(NANO_RULE), 0);
//        assertSame(test.getOffset(), OFFSET_0100);
//        assertSame(test.getZone(), ZONE_UTC);
//    }
//
//    public void constructor_calendrical_date_time_offset_zone_allNull() {
//        LocalDate date = null;
//        LocalTime time = null;
//        ZoneOffset offset = null;
//        TimeZone zone = null;
//        Calendrical test = new Calendrical(date, time, offset, zone);
//        assertEquals(test.toDateTimeFields().size(), 0);
//        assertSame(test.getOffset(), null);
//        assertSame(test.getZone(), null);
//    }

    //-----------------------------------------------------------------------
    // Accessors
    //-----------------------------------------------------------------------
    public void test_getFieldMap() {
        Calendrical test = new Calendrical();
        assertNotNull(test.getFieldMap());
    }

    //-----------------------------------------------------------------------
    public void test_getSetDate() {
        Calendrical test = new Calendrical();
        assertEquals(test.getDate(), null);
        test.setDate(DATE_2008_06_30);
        assertSame(test.getDate(), DATE_2008_06_30);
    }

    public void test_getSetDate_null() {
        Calendrical test = new Calendrical(DATE_2008_06_30, null, null, null);
        assertEquals(test.getDate(), DATE_2008_06_30);
        test.setDate(null);
        assertSame(test.getDate(), null);
    }

    //-----------------------------------------------------------------------
    public void test_getSetTime() {
        Calendrical test = new Calendrical();
        assertEquals(test.getTime(), null);
        test.setTime(TIME_10_15_30);
        assertSame(test.getTime(), TIME_10_15_30);
    }

    public void test_getSetTime_null() {
        Calendrical test = new Calendrical(null, TIME_10_15_30, null, null);
        assertEquals(test.getTime(), TIME_10_15_30);
        test.setTime(null);
        assertSame(test.getTime(), null);
    }

    //-----------------------------------------------------------------------
    public void test_getSetOffset() {
        Calendrical test = new Calendrical();
        assertEquals(test.getOffset(), null);
        test.setOffset(OFFSET_0100);
        assertSame(test.getOffset(), OFFSET_0100);
    }

    public void test_getSetOffset_null() {
        Calendrical test = new Calendrical(null, null, OFFSET_0100, null);
        assertEquals(test.getOffset(), OFFSET_0100);
        test.setOffset(null);
        assertSame(test.getOffset(), null);
    }

    //-----------------------------------------------------------------------
    public void test_getSetZone() {
        Calendrical test = new Calendrical();
        assertEquals(test.getZone(), null);
        test.setZone(ZONE_0100);
        assertSame(test.getZone(), ZONE_0100);
    }

    public void test_getSetZone_null() {
        Calendrical test = new Calendrical(null, null, null, ZONE_0100);
        assertEquals(test.getZone(), ZONE_0100);
        test.setZone(null);
        assertSame(test.getZone(), null);
    }

    //-----------------------------------------------------------------------
    // isDerivable()
    //-----------------------------------------------------------------------
    public void test_isDerivable() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.isDerivable(YEAR_RULE), true);
        assertEquals(test.isDerivable(MOY_RULE), true);
    }

    public void test_isDerivable_outOfRange() {
        Calendrical test = new Calendrical(MOY_RULE, -3);
        assertEquals(test.isDerivable(MOY_RULE), true);
    }

    public void test_isDerivable_null() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.isDerivable(NULL_RULE), false);
    }

    public void test_isDerivable_fieldNotPresent() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.isDerivable(DOM_RULE), false);
        assertEquals(test.isDerivable(DOW_RULE), false);
    }

    public void test_isDerivable_empty() {
        Calendrical test = new Calendrical();
        assertEquals(test.isDerivable(YEAR_RULE), false);
        assertEquals(test.isDerivable(HOUR_RULE), false);
    }

    public void test_isDerivable_fromDate() {
        Calendrical test = new Calendrical(DATE_2008_06_30, null, null, null);
        assertEquals(test.isDerivable(YEAR_RULE), true);
        assertEquals(test.isDerivable(DOW_RULE), true);
        assertEquals(test.isDerivable(HOUR_RULE), false);
    }

    public void test_isDerivable_fromTime() {
        Calendrical test = new Calendrical(null, TIME_10_15_30, null, null);
        assertEquals(test.isDerivable(HOUR_RULE), true);
        assertEquals(test.isDerivable(MIN_RULE), true);
        assertEquals(test.isDerivable(SEC_RULE), true);
        assertEquals(test.isDerivable(YEAR_RULE), false);
    }

    //-----------------------------------------------------------------------
    // deriveValue()
    //-----------------------------------------------------------------------
    public void test_deriveValue() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.deriveValue(YEAR_RULE), 2008);
        assertEquals(test.deriveValue(MOY_RULE), 6);
    }

    public void test_deriveValue_derived() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.deriveValue(QOY_RULE), 2);
    }

    public void test_deriveValue_outOfRange() {
        Calendrical test = new Calendrical(MOY_RULE, -3);
        assertEquals(test.deriveValue(MOY_RULE), -3);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_deriveValue_null() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.deriveValue(NULL_RULE);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_deriveValue_fieldNotPresent() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.deriveValue(DOM_RULE);
    }

    public void test_deriveValue_fromDate() {
        Calendrical test = new Calendrical(DATE_2008_06_30, null, null, null);
        assertEquals(test.deriveValue(YEAR_RULE), 2008);
        assertEquals(test.deriveValue(MOY_RULE), 6);
        assertEquals(test.deriveValue(DOM_RULE), 30);
        assertEquals(test.deriveValue(DOW_RULE), 1);
    }

    public void test_deriveValue_fromTime() {
        Calendrical test = new Calendrical(null, TIME_10_15_30, null, null);
        assertEquals(test.deriveValue(HOUR_RULE), 10);
        assertEquals(test.deriveValue(MIN_RULE), 15);
        assertEquals(test.deriveValue(SEC_RULE), 30);
    }

    public void test_deriveValue_dateTakesPrecedence() {
        Calendrical test = new Calendrical(YEAR_RULE, 2009);
        test.setDate(DATE_2008_06_30);
        assertEquals(test.deriveValue(YEAR_RULE), 2008);
        assertEquals(test.deriveValue(MOY_RULE), 6);
        assertEquals(test.deriveValue(DOM_RULE), 30);
        assertEquals(test.deriveValue(DOW_RULE), 1);
    }

    public void test_deriveValue_timeTakesPrecedence() {
        Calendrical test = new Calendrical(HOUR_RULE, 14);
        test.setTime(TIME_10_15_30);
        assertEquals(test.deriveValue(HOUR_RULE), 10);
        assertEquals(test.deriveValue(MIN_RULE), 15);
        assertEquals(test.deriveValue(SEC_RULE), 30);
    }

    //-----------------------------------------------------------------------
    // deriveValueQuiet()
    //-----------------------------------------------------------------------
    public void test_deriveValueQuiet() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.deriveValueQuiet(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.deriveValueQuiet(MOY_RULE), Integer.valueOf(6));
    }

    public void test_deriveValueQuiet_derived() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.deriveValueQuiet(QOY_RULE), Integer.valueOf(2));
    }

    public void test_deriveValueQuiet_outOfRange() {
        Calendrical test = new Calendrical(MOY_RULE, -3);
        assertEquals(test.deriveValueQuiet(MOY_RULE), Integer.valueOf(-3));
    }

    public void test_deriveValueQuiet_null() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.deriveValueQuiet(NULL_RULE), null);
    }

    public void test_deriveValueQuiet_fieldNotPresent() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.deriveValueQuiet(DOM_RULE), null);
        assertEquals(test.deriveValueQuiet(DOW_RULE), null);
    }

    public void test_deriveValueQuiet_fromDate() {
        Calendrical test = new Calendrical(DATE_2008_06_30, null, null, null);
        assertEquals(test.deriveValueQuiet(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.deriveValueQuiet(MOY_RULE), Integer.valueOf(6));
        assertEquals(test.deriveValueQuiet(DOM_RULE), Integer.valueOf(30));
        assertEquals(test.deriveValueQuiet(DOW_RULE), Integer.valueOf(1));
    }

    public void test_deriveValueQuiet_fromTime() {
        Calendrical test = new Calendrical(null, TIME_10_15_30, null, null);
        assertEquals(test.deriveValueQuiet(HOUR_RULE), Integer.valueOf(10));
        assertEquals(test.deriveValueQuiet(MIN_RULE), Integer.valueOf(15));
        assertEquals(test.deriveValueQuiet(SEC_RULE), Integer.valueOf(30));
    }

    public void test_deriveValueQuiet_dateTakesPrecedence() {
        Calendrical test = new Calendrical(YEAR_RULE, 2009);
        test.setDate(DATE_2008_06_30);
        assertEquals(test.deriveValueQuiet(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.deriveValueQuiet(MOY_RULE), Integer.valueOf(6));
        assertEquals(test.deriveValueQuiet(DOM_RULE), Integer.valueOf(30));
        assertEquals(test.deriveValueQuiet(DOW_RULE), Integer.valueOf(1));
    }

    public void test_deriveValueQuiet_timeTakesPrecedence() {
        Calendrical test = new Calendrical(HOUR_RULE, 14);
        test.setTime(TIME_10_15_30);
        assertEquals(test.deriveValueQuiet(HOUR_RULE), Integer.valueOf(10));
        assertEquals(test.deriveValueQuiet(MIN_RULE), Integer.valueOf(15));
        assertEquals(test.deriveValueQuiet(SEC_RULE), Integer.valueOf(30));
    }

    //-----------------------------------------------------------------------
    // checkConsistent()
    //-----------------------------------------------------------------------
    public void test_checkConsistent_empty_consistent() {
        Calendrical test = new Calendrical();
        test.checkConsistent();
        assertEquals(test, new Calendrical());
    }

    public void test_checkConsistent_fieldsOnly_consistent() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.checkConsistent();
        assertEquals(test, new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_checkConsistent_fieldsOnly_inconsistent() {
        Calendrical test = new Calendrical(MOY_RULE, 6, QOY_RULE, 1);
        try {
            test.checkConsistent();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), QOY_RULE);
            assertEquals(test, new Calendrical(MOY_RULE, 6, QOY_RULE, 1));
            throw ex;
        }
    }

    public void test_checkConsistent_dateTimeOnly_consistent() {
        Calendrical test = new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null);
        test.checkConsistent();
        assertEquals(test, new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null));
    }

    public void test_checkConsistent_dateFields_consistent() {
        Calendrical test = new Calendrical(DATE_2008_06_30, null, null, null);
        test.getFieldMap().put(QOY_RULE, 2);  // consistent
        test.checkConsistent();
        Calendrical expected = new Calendrical(DATE_2008_06_30, null, null, null);
        expected.getFieldMap().put(QOY_RULE, 2);
        assertEquals(test, expected);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_checkConsistent_dateFields_inconsistent() {
        Calendrical test = new Calendrical(DATE_2008_06_30, null, null, null);
        test.getFieldMap().put(QOY_RULE, 1);  // inconsistent
        try {
            test.checkConsistent();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), QOY_RULE);
            Calendrical expected = new Calendrical(DATE_2008_06_30, null, null, null);
            expected.getFieldMap().put(QOY_RULE, 1);
            assertEquals(test, expected);
            throw ex;
        }
    }

    public void test_checkConsistent_timeFields_consistent() {
        Calendrical test = new Calendrical(null, TIME_10_15_30, null, null);
        test.getFieldMap().put(AMPM_RULE, 0);  // consistent
        test.checkConsistent();
        Calendrical expected = new Calendrical(null, TIME_10_15_30, null, null);
        expected.getFieldMap().put(AMPM_RULE, 0);
        assertEquals(test, expected);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_checkConsistent_timeFields_inconsistent() {
        Calendrical test = new Calendrical(null, TIME_10_15_30, null, null);
        test.getFieldMap().put(AMPM_RULE, 1);  // inconsistent
        try {
            test.checkConsistent();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), AMPM_RULE);
            Calendrical expected = new Calendrical(null, TIME_10_15_30, null, null);
            expected.getFieldMap().put(AMPM_RULE, 1);
            assertEquals(test, expected);
            throw ex;
        }
    }

    public void test_checkConsistent_dateTimeFields_consistent() {
        Calendrical test = new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null);
        test.getFieldMap().put(AMPM_RULE, 0);  // consistent
        test.checkConsistent();
        Calendrical expected = new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null);
        expected.getFieldMap().put(AMPM_RULE, 0);
        assertEquals(test, expected);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_checkConsistent_dateTimeFields_inconsistent() {
        Calendrical test = new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null);
        test.getFieldMap().put(AMPM_RULE, 1);  // inconsistent
        try {
            test.checkConsistent();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), AMPM_RULE);
            Calendrical expected = new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null);
            expected.getFieldMap().put(AMPM_RULE, 1);
            assertEquals(test, expected);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // removeDerivable()
    //-----------------------------------------------------------------------
    public void test_removeDerivable_empty() {
        Calendrical test = new Calendrical();
        test.removeDerivable();
        assertEquals(test, new Calendrical());
    }

    public void test_removeDerivable_nothingRemoved_fieldsOnly() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.removeDerivable();
        assertEquals(test, new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6));
    }

    public void test_removeDerivable_nothingRemoved_dateTimeOnly() {
        Calendrical test = new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null);
        test.removeDerivable();
        assertEquals(test, new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null));
    }

    public void test_removeDerivable_removeDerivedFromDate() {
        Calendrical test = new Calendrical(DATE_2008_06_30, null, null, null);
        test.getFieldMap().put(YEAR_RULE, 2009);  // year doesn't match
        test.removeDerivable();
        assertEquals(test, new Calendrical(DATE_2008_06_30, null, null, null));
    }

    public void test_removeDerivable_removeDerivedFromTime() {
        Calendrical test = new Calendrical(null, TIME_10_15_30, null, null);
        test.getFieldMap().put(HOUR_RULE, 14);  // hour doesn't match
        test.removeDerivable();
        assertEquals(test, new Calendrical(null, TIME_10_15_30, null, null));
    }

    public void test_removeDerivable_removeDerivedFromOtherField() {
        Calendrical test = new Calendrical(MOY_RULE, 6, QOY_RULE, 1);  // quarter doesn't match
        test.removeDerivable();
        assertEquals(test, new Calendrical(MOY_RULE, 6));
    }

    //-----------------------------------------------------------------------
    // mergeStrict()
    //-----------------------------------------------------------------------
    public void test_mergeStrict_empty() {
        Calendrical test = new Calendrical();
        test.mergeStrict();
        assertEquals(test, new Calendrical());
    }

    public void test_mergeStrict_nothingToMerge_fieldsOnly() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.mergeStrict();
        assertEquals(test, new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6));
    }

    public void test_mergeStrict_nothingToMerge_dateTimeOnly() {
        Calendrical test = new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null);
        test.mergeStrict();
        assertEquals(test, new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null));
    }

    //-----------------------------------------------------------------------
    // mergeLenient()
    //-----------------------------------------------------------------------
    public void test_mergeLenient_empty() {
        Calendrical test = new Calendrical();
        test.mergeLenient();
        assertEquals(test, new Calendrical());
    }

    public void test_mergeLenient_nothingToMerge_fieldsOnly() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.mergeLenient();
        assertEquals(test, new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6));
    }

    public void test_mergeLenient_nothingToMerge_dateTimeOnly() {
        Calendrical test = new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null);
        test.mergeLenient();
        assertEquals(test, new Calendrical(DATE_2008_06_30, TIME_10_15_30, null, null));
    }

//    //-----------------------------------------------------------------------
//    // isFieldValueValid()
//    //-----------------------------------------------------------------------
//    public void test_isFieldValueValid() {
//        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        assertEquals(test.isFieldMapValueValid(YEAR_RULE), true);
//        assertEquals(test.isFieldMapValueValid(MOY_RULE), true);
//    }
//
//    public void test_isFieldValueValid_outOfRange() {
//        Calendrical test = new Calendrical(MOY_RULE, -3);
//        assertEquals(test.isFieldMapValueValid(MOY_RULE), false);
//    }
//
//    public void test_isFieldValueValid_null() {
//        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        assertEquals(test.isFieldMapValueValid(NULL_RULE), false);
//    }
//
//    public void test_isFieldValueValid_fieldNotPresent() {
//        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        assertEquals(test.isFieldMapValueValid(DOM_RULE), false);
//        assertEquals(test.isFieldMapValueValid(HOUR_RULE), false);
//    }
//
//    public void test_isFieldValueValid_empty() {
//        Calendrical test = new Calendrical();
//        assertEquals(test.isFieldMapValueValid(YEAR_RULE), false);
//        assertEquals(test.isFieldMapValueValid(HOUR_RULE), false);
//    }
//
//    public void test_isFieldValueValid_fromDate() {
//        Calendrical test = new Calendrical(DATE_2008_06_30, null, null, null);
//        assertEquals(test.isFieldMapValueValid(YEAR_RULE), false);
//        assertEquals(test.isFieldMapValueValid(HOUR_RULE), false);
//    }
//
//    //-----------------------------------------------------------------------
//    // getFieldValueValidated()
//    //-----------------------------------------------------------------------
//    public void test_getFieldValueValidated() {
//        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        assertEquals(test.getFieldMapValueValidated(YEAR_RULE), 2008);
//        assertEquals(test.getFieldMapValueValidated(MOY_RULE), 6);
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_getFieldValueValidated_illegalValue() {
//        Calendrical test = new Calendrical(MOY_RULE, -3);
//        try {
//            test.getFieldMapValueValidated(MOY_RULE);
//        } catch (UnsupportedCalendarFieldException ex) {
//            assertEquals(ex.getFieldRule(), MOY_RULE);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_getFieldValueValidated_null() {
//        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        test.getFieldMapValueValidated(NULL_RULE);
//    }
//
//    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
//    public void test_getFieldValueValidated_fieldNotPresent() {
//        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        try {
//            test.getFieldMapValueValidated(DOW_RULE);
//        } catch (UnsupportedCalendarFieldException ex) {
//            assertEquals(ex.getFieldRule(), DOW_RULE);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
//    public void test_getFieldValueValidated_fromDate() {
//        Calendrical test = new Calendrical(DATE_2008_06_30, null, null, null);
//        try {
//            test.getFieldMapValueValidated(MOY_RULE);
//        } catch (UnsupportedCalendarFieldException ex) {
//            assertEquals(ex.getFieldRule(), MOY_RULE);
//            throw ex;
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    // withFields(DateTimeFields)
//    //-----------------------------------------------------------------------
//    public void test_withFields() {
//        Calendrical base = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields fields = DateTimeFields.fields(DOM_RULE, 30);
//        Calendrical test = base.withFields(fields);
//        assertFields(test, DOM_RULE, 30);
//        assertSame(test.toDateTimeFields(), fields);
//        // check original immutable
//        assertFields(base, YEAR_RULE, 2008, MOY_RULE, 6);
//    }
//
//    public void test_withFields_sameFields() {
//        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields newFields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        Calendrical base = new Calendrical(fields, null, null);
//        Calendrical test = base.withFields(newFields);
//        assertSame(test, base);
//    }
//
//    public void test_withFields_invalidValueOK() {
//        Calendrical base = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields fields = DateTimeFields.fields(DOM_RULE, -1);
//        Calendrical test = base.withFields(fields);
//        assertFields(test, DOM_RULE, -1);
//        assertSame(test.toDateTimeFields(), fields);
//    }
//
//    public void test_withFields_empty() {
//        Calendrical base = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields fields = DateTimeFields.fields();
//        Calendrical test = base.withFields(fields);
//        assertEquals(test.toDateTimeFields().size(), 0);
//        assertSame(test.toDateTimeFields(), fields);
//    }
//
//    public void test_withFields_null() {
//        Calendrical base = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields fields = null;
//        Calendrical test = base.withFields(fields);
//        assertEquals(test.toDateTimeFields().size(), 0);
//    }
//
//    public void test_withFields_empty_null() {
//        Calendrical base = new Calendrical();
//        DateTimeFields fields = null;
//        Calendrical test = base.withFields(fields);
//        assertSame(test, base);
//    }
//
    //-----------------------------------------------------------------------
    // toDateTimeFields()
    //-----------------------------------------------------------------------
    public void test_toDateTimeFields() {
        DateTimeFields fields = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical base = new Calendrical(fields);
        DateTimeFields test = base.toDateTimeFields();
        assertEquals(test.size(), 2);
        assertEquals(test.get(YEAR_RULE), 2008);
        assertEquals(test.get(MOY_RULE), 6);
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    public void test_toLocalDate() {
        Calendrical base = new Calendrical(DATE_2008_06_30, null, null, null);
        LocalDate test = base.toLocalDate();
        assertEquals(test, DATE_2008_06_30);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDate_empty() {
        Calendrical base = new Calendrical();
        base.toLocalDate();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDate_insufficientInfo() {
        Calendrical base = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        base.toLocalDate();
    }

    //-----------------------------------------------------------------------
    // toLocalTime()
    //-----------------------------------------------------------------------
    public void test_toLocalTime() {
        Calendrical base = new Calendrical(null, TIME_10_15_30, null, null);
        LocalTime test = base.toLocalTime();
        assertEquals(test, TIME_10_15_30);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalTime_empty() {
        Calendrical base = new Calendrical();
        base.toLocalTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalTime_insufficientInfo() {
        Calendrical base = new Calendrical(HOUR_AM_PM_RULE, 11);
        base.toLocalTime();
    }

    //-----------------------------------------------------------------------
    // toLocalDateTime()
    //-----------------------------------------------------------------------
    public void test_toLocalDateTime() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = new Calendrical(dt.toLocalDate(), dt.toLocalTime(), null, null);
        LocalDateTime test = base.toLocalDateTime();
        assertEquals(test, dt);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDateTime_empty() {
        Calendrical base = new Calendrical();
        base.toLocalDateTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDateTime_insufficientInfo() {
        Calendrical base = new Calendrical(HOUR_AM_PM_RULE, 11);
        base.toLocalDateTime();
    }

    //-----------------------------------------------------------------------
    // toOffsetDate()
    //-----------------------------------------------------------------------
    public void test_toOffsetDate() {
        LocalDate date = DATE_2008_06_30;
        Calendrical base = new Calendrical(date, null, OFFSET_0100, null);
        OffsetDate test = base.toOffsetDate();
        assertEquals(test.toLocalDate(), date);
        assertSame(test.getOffset(), OFFSET_0100);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toOffsetDate_noOffset() {
        LocalDate date = DATE_2008_06_30;
        Calendrical base = new Calendrical(date, null, null, null);
        base.toOffsetDate();
    }

    //-----------------------------------------------------------------------
    // toOffsetTime()
    //-----------------------------------------------------------------------
    public void test_toOffsetTime() {
        LocalTime time = LocalTime.time(11, 30);
        Calendrical base = new Calendrical(null, time, OFFSET_0100, null);
        OffsetTime test = base.toOffsetTime();
        assertEquals(test.toLocalTime(), time);
        assertSame(test.getOffset(), OFFSET_0100);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toOffsetTime_noOffset() {
        LocalTime time = LocalTime.time(11, 30);
        Calendrical base = new Calendrical(null, time, null, null);
        base.toOffsetTime();
    }

    //-----------------------------------------------------------------------
    // toOffsetDateTime()
    //-----------------------------------------------------------------------
    public void test_toOffsetDateTime() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = new Calendrical(dt.toLocalDate(), dt.toLocalTime(), OFFSET_0100, null);
        OffsetDateTime test = base.toOffsetDateTime();
        assertEquals(test.toLocalDateTime(), dt);
        assertSame(test.getOffset(), OFFSET_0100);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toOffsetDateTime_noOffset() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = new Calendrical(dt.toLocalDate(), dt.toLocalTime(), null, null);
        base.toOffsetDateTime();
    }

    //-----------------------------------------------------------------------
    // toZonedDateTime()
    //-----------------------------------------------------------------------
    public void test_toZonedDateTime() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = new Calendrical(dt.toLocalDate(), dt.toLocalTime(), OFFSET_0100, ZONE_0100);
        ZonedDateTime test = base.toZonedDateTime();
        assertEquals(test.toLocalDateTime(), dt);
        assertSame(test.getOffset(), OFFSET_0100);
        assertSame(test.getZone(), ZONE_0100);
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toZonedDateTime_noOffset() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = new Calendrical(dt.toLocalDate(), dt.toLocalTime(), null, ZONE_0100);
        base.toZonedDateTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toZonedDateTime_noZone() {
        LocalDateTime dt = LocalDateTime.dateTime(2008, 6, 30, 11, 30);
        Calendrical base = new Calendrical(dt.toLocalDate(), dt.toLocalTime(), OFFSET_0100, null);
        base.toZonedDateTime();
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    public void test_toCalendrical() {
        Calendrical base = new Calendrical();
        Calendrical test = base.toCalendrical();
        assertEquals(test, base);
        assertNotSame(test, base);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals0() {
        Calendrical a = new Calendrical();
        Calendrical b = new Calendrical();
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_equal() {
        Calendrical a = new Calendrical(YEAR_RULE, 2008);
        Calendrical b = new Calendrical(YEAR_RULE, 2008);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_notEqualValue() {
        Calendrical a = new Calendrical(YEAR_RULE, 2008);
        Calendrical b = new Calendrical(YEAR_RULE, 2007);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_notEqualField() {
        Calendrical a = new Calendrical(YEAR_RULE, 2008);
        Calendrical b = new Calendrical(MOY_RULE, 2008);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_equal() {
        Calendrical a = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical b = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualOneValue() {
        Calendrical a = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical b = new Calendrical(YEAR_RULE, 2007, MOY_RULE, 6);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualTwoValues() {
        Calendrical a = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical b = new Calendrical(YEAR_RULE, 2007, MOY_RULE, 5);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualField() {
        Calendrical a = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        Calendrical b = new Calendrical(YEAR_RULE, 2008, DOM_RULE, 6);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_date_notEqual() {
        DateTimeFields fields = DateTimeFields.fields()
            .with(YEAR_RULE, 2008)
            .with(MOY_RULE, 6)
            .with(DOM_RULE, 30);
        LocalDate date = DATE_2008_06_30;
        Calendrical a = new Calendrical(fields);
        Calendrical b = new Calendrical(date, null, null, null);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), true);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_dateEqual_fields_notEqual() {
        DateTimeFields fields = DateTimeFields.fields()
            .with(YEAR_RULE, 2008)
            .with(DOY_RULE, 182);
        LocalDate date = DATE_2008_06_30;
        Calendrical a = new Calendrical(fields);
        Calendrical b = new Calendrical(date, null, null, null);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_fieldsIdentical_equal() {
        DateTimeFields fields = DateTimeFields.fields()
            .with(YEAR_RULE, 2008)
            .with(MOY_RULE, 6)
            .with(DOM_RULE, 30);
        Calendrical a = new Calendrical(fields);
        Calendrical b = new Calendrical(fields);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_offset_equal() {
        Calendrical a = new Calendrical(null, null, OFFSET_0100, null);
        Calendrical b = new Calendrical(null, null, ZoneOffset.zoneOffset("+01:00"), null);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_offset_notEqual() {
        Calendrical a = new Calendrical(null, null, OFFSET_0100, null);
        Calendrical b = new Calendrical(null, null, OFFSET_0200, null);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_offset_notEqual_null() {
        Calendrical a = new Calendrical(null, null, OFFSET_0100, null);
        Calendrical b = new Calendrical(null, null, null, null);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_zone_equal() {
        Calendrical a = new Calendrical(null, null, null, ZONE_0100);
        Calendrical b = new Calendrical(null, null, null, TimeZone.timeZone(OFFSET_0100));
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_zone_notEqual() {
        Calendrical a = new Calendrical(null, null, null, ZONE_0100);
        Calendrical b = new Calendrical(null, null, null, ZONE_UTC);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_fields_zone_notEqual_null() {
        Calendrical a = new Calendrical(null, null, null, ZONE_0100);
        Calendrical b = new Calendrical(null, null, null, null);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_otherType() {
        Calendrical a = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(a.equals("Rubbish"), false);
    }

    public void test_equals_null() {
        Calendrical a = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(a.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString_empty() {
        Calendrical base = new Calendrical();
        String test = base.toString();
        assertEquals(test, "");
    }

    public void test_toString_fields() {
        Calendrical base = new Calendrical(YEAR_RULE, 2008);
        String test = base.toString();
        assertEquals(test, "{ISO.Year=2008}");
    }

    public void test_toString_date() {
        Calendrical base = new Calendrical(DATE_2008_06_30, null, null, null);
        String test = base.toString();
        assertEquals(test, "2008-06-30");
    }

    public void test_toString_time() {
        Calendrical base = new Calendrical(null, TIME_10_15_30, null, null);
        String test = base.toString();
        assertEquals(test, "10:15:30");
    }

    public void test_toString_offset() {
        Calendrical base = new Calendrical(null, null, OFFSET_0100, null);
        String test = base.toString();
        assertEquals(test, "+01:00");
    }

    public void test_toString_zone() {
        Calendrical base = new Calendrical(null, null, null, ZONE_UTC);
        String test = base.toString();
        assertEquals(test, "UTC");
    }

    public void test_toString_all() {
        Calendrical base = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        base.setDate(DATE_2008_06_30);
        base.setTime(TIME_10_15_30);
        base.setOffset(OFFSET_0100);
        base.setZone(ZONE_UTC);
        String test = base.toString();
        assertEquals(test, "{ISO.Year=2008, ISO.MonthOfYear=6} 2008-06-30 10:15:30 +01:00 UTC");
    }

    //-----------------------------------------------------------------------
    private void assertFields(
            Calendrical calendrical,
            DateTimeFieldRule rule1, int value1) {
        assertEquals(calendrical.getFieldMap().size(), 1);
        assertEquals(calendrical.getFieldMap().get(rule1), value1);
    }
    private void assertFields(
            Calendrical calendrical,
            DateTimeFieldRule rule1, int value1,
            DateTimeFieldRule rule2, int value2) {
        assertEquals(calendrical.getFieldMap().size(), 2);
        assertEquals(calendrical.getFieldMap().get(rule1), value1);
        assertEquals(calendrical.getFieldMap().get(rule2), value2);
    }
    private static void dumpException(Exception ex) {
        // this is used to allow a human to inspect the error messages to see if they are understandable
        System.out.println(ex.getMessage());
    }
}
