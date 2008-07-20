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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DateTimeFields.
 */
@Test
public class TestDateTimeFields {

    @SuppressWarnings("unchecked")
    private static final Map<DateTimeFieldRule, Integer> NULL_MAP = (Map) null;
    private static final DateTimeFieldRule NULL_RULE = null;
    private static final DateTimeFieldRule YEAR_RULE = ISOChronology.INSTANCE.year();
    private static final DateTimeFieldRule MOY_RULE = ISOChronology.INSTANCE.monthOfYear();
    private static final DateTimeFieldRule DOM_RULE = ISOChronology.INSTANCE.dayOfMonth();
    private static final DateTimeFieldRule DOY_RULE = ISOChronology.INSTANCE.dayOfYear();
    private static final DateTimeFieldRule DOW_RULE = ISOChronology.INSTANCE.dayOfWeek();
    private static final DateTimeFieldRule QOY_RULE = ISOChronology.INSTANCE.quarterOfYear();
    private static final DateTimeFieldRule MOQ_RULE = ISOChronology.INSTANCE.monthOfQuarter();
    private static final DateTimeFieldRule HOUR_RULE = ISOChronology.INSTANCE.hourOfDay();
    private static final DateTimeFieldRule AMPM_RULE = ISOChronology.INSTANCE.amPmOfDay();
    private static final DateTimeFieldRule HOUR_AM_PM_RULE = ISOChronology.INSTANCE.hourOfAmPm();
    private static final DateTimeFieldRule MIN_RULE = ISOChronology.INSTANCE.minuteOfHour();
    private static final DateTimeFieldRule MILLI_RULE = ISOChronology.INSTANCE.milliOfDay();

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalProvider.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(DateProvider.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(TimeProvider.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(DateTimeProvider.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(DateMatcher.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(TimeMatcher.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(Iterable.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(Serializable.class.isAssignableFrom(DateTimeFields.class));
    }

    @DataProvider(name="simple")
    Object[][] data_simple() {
        return new Object[][] {
            {DateTimeFields.fields()},
            {DateTimeFields.fields(YEAR_RULE, 2008)},
            {DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6)},
        };
    }

    @Test(dataProvider="simple")
    public void test_serialization(DateTimeFields fields) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(fields);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        if (fields.getFieldValueMap().isEmpty()) {
            assertSame(ois.readObject(), fields);
        } else {
            assertEquals(ois.readObject(), fields);
        }
    }

    public void test_immutable() {
        Class<DateTimeFields> cls = DateTimeFields.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
        Constructor<?>[] cons = cls.getDeclaredConstructors();
        for (Constructor<?> con : cons) {
            assertTrue(Modifier.isPrivate(con.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    public void factory_fields_empty() {
        DateTimeFields test = DateTimeFields.fields();
        assertEquals(test.getFieldValueMap().size(), 0);
    }

    public void factory_fields_empty_singleton() {
        assertSame(DateTimeFields.fields(), DateTimeFields.fields());
    }

    //-----------------------------------------------------------------------
    public void factory_fields_onePair() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008);
        assertEquals(test.getFieldValueMap().size(), 1);
        assertEquals(test.getFieldValueMap().get(YEAR_RULE), Integer.valueOf(2008));
    }

    public void factory_fields_onePair_invalidValueOK() {
        DateTimeFields test = DateTimeFields.fields(MOY_RULE, -1);
        assertEquals(test.getFieldValueMap().size(), 1);
        assertEquals(test.getFieldValue(MOY_RULE), -1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_onePair_null() {
        DateTimeFields.fields(NULL_RULE, 1);
    }

    //-----------------------------------------------------------------------
    public void factory_fields_twoPairs() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getFieldValueMap().size(), 2);
        assertEquals(test.getFieldValueMap().get(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.getFieldValueMap().get(MOY_RULE), Integer.valueOf(6));
    }

    public void factory_fields_twoPairs_orderNotSignificant() {
        DateTimeFields test = DateTimeFields.fields(MOY_RULE, 6, YEAR_RULE, 2008);
        assertEquals(test.getFieldValueMap().size(), 2);
        assertEquals(test.getFieldValueMap().get(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.getFieldValueMap().get(MOY_RULE), Integer.valueOf(6));
    }

    public void factory_fields_twoPairs_sameFieldOverwrites() {
        DateTimeFields test = DateTimeFields.fields(MOY_RULE, 6, MOY_RULE, 7);
        assertEquals(test.getFieldValueMap().size(), 1);
        assertEquals(test.getFieldValueMap().get(MOY_RULE), Integer.valueOf(7));
    }

    public void factory_fields_twoPairs_invalidValueOK() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, -1);
        assertEquals(test.getFieldValueMap().size(), 2);
        assertEquals(test.getFieldValueMap().get(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.getFieldValueMap().get(MOY_RULE), Integer.valueOf(-1));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_twoPairs_nullFirst() {
        DateTimeFields.fields(NULL_RULE, 1, MOY_RULE, 6);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_twoPairs_nullSecond() {
        DateTimeFields.fields(MOY_RULE, 6, NULL_RULE, 1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_twoPairs_nullBoth() {
        DateTimeFields.fields(NULL_RULE, 1, NULL_RULE, 6);
    }

    //-----------------------------------------------------------------------
    public void factory_fields_map() {
        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
        map.put(YEAR_RULE, 2008);
        map.put(MOY_RULE, 6);
        DateTimeFields test = DateTimeFields.fields(map);
        assertEquals(test.getFieldValueMap().size(), 2);
        assertEquals(test.getFieldValueMap().get(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.getFieldValueMap().get(MOY_RULE), Integer.valueOf(6));
    }

    public void factory_fields_map_cloned() {
        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
        map.put(YEAR_RULE, 2008);
        DateTimeFields test = DateTimeFields.fields(map);
        assertEquals(test.getFieldValueMap().size(), 1);
        assertEquals(test.getFieldValueMap().get(YEAR_RULE), Integer.valueOf(2008));
        map.put(MOY_RULE, 6);
        assertEquals(test.getFieldValueMap().size(), 1);
        assertEquals(test.getFieldValueMap().get(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.getFieldValueMap().get(MOY_RULE), null);
    }

    public void factory_fields_map_empty_singleton() {
        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
        assertSame(DateTimeFields.fields(map), DateTimeFields.fields());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_map_null() {
        DateTimeFields.fields(NULL_MAP);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_map_nullKey() {
        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
        map.put(YEAR_RULE, 2008);
        map.put(null, 6);
        DateTimeFields.fields(map);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_map_nullValue() {
        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
        map.put(YEAR_RULE, 2008);
        map.put(MOY_RULE, null);
        DateTimeFields.fields(map);
    }

    //-----------------------------------------------------------------------
    // getFieldValue()
    //-----------------------------------------------------------------------
    public void test_getFieldValue() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getFieldValue(YEAR_RULE), 2008);
        assertEquals(test.getFieldValue(MOY_RULE), 6);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getFieldValue_null() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        test.getFieldValue(NULL_RULE);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_getFieldValue_fieldNotPresent() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        try {
            test.getFieldValue(DOM_RULE);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), DOM_RULE);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // getFieldValueQuiet()
    //-----------------------------------------------------------------------
    public void test_getFieldValueQuiet() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getFieldValueQuiet(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.getFieldValueQuiet(MOY_RULE), Integer.valueOf(6));
    }

    public void test_getFieldValueQuiet_null() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getFieldValueQuiet(NULL_RULE), null);
    }

    public void test_getFieldValueQuiet_fieldNotPresent() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getFieldValueQuiet(DOM_RULE), null);
    }

    //-----------------------------------------------------------------------
    // size()
    //-----------------------------------------------------------------------
    public void test_size0() {
        DateTimeFields test = DateTimeFields.fields();
        assertEquals(test.size(), 0);
    }

    public void test_size1() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008);
        assertEquals(test.size(), 1);
    }

    public void test_size2() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.size(), 2);
    }

    //-----------------------------------------------------------------------
    // iterator()
    //-----------------------------------------------------------------------
    public void test_iterator() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        Iterator<DateTimeFieldRule> iterator = test.iterator();
        assertEquals(iterator.hasNext(), true);
        assertEquals(iterator.next(), YEAR_RULE);
        assertEquals(iterator.hasNext(), true);
        assertEquals(iterator.next(), MOY_RULE);
        assertEquals(iterator.hasNext(), false);
    }

    //-----------------------------------------------------------------------
    // containsField()
    //-----------------------------------------------------------------------
    public void test_containsField() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.containsField(YEAR_RULE), true);
        assertEquals(test.containsField(MOY_RULE), true);
    }

    public void test_containsField_null() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.containsField(NULL_RULE), false);
    }

    public void test_containsField_fieldNotPresent() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.containsField(DOM_RULE), false);
    }

    //-----------------------------------------------------------------------
    // withFieldValue()
    //-----------------------------------------------------------------------
    public void test_withFieldValue() {
        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields test = base.withFieldValue(DOM_RULE, 30);
        assertEquals(test.getFieldValueMap().size(), 3);
        assertEquals(test.getFieldValue(YEAR_RULE), 2008);
        assertEquals(test.getFieldValue(MOY_RULE), 6);
        assertEquals(test.getFieldValue(DOM_RULE), 30);
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 2);
        assertEquals(base.getFieldValue(YEAR_RULE), 2008);
        assertEquals(base.getFieldValue(MOY_RULE), 6);
    }

    public void test_withFieldValue_invalidValueOK() {
        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields test = base.withFieldValue(DOM_RULE, -1);
        assertEquals(test.getFieldValueMap().size(), 3);
        assertEquals(test.getFieldValue(YEAR_RULE), 2008);
        assertEquals(test.getFieldValue(MOY_RULE), 6);
        assertEquals(test.getFieldValue(DOM_RULE), -1);
    }

    public void test_withFieldValue_sameFieldOverwrites() {
        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields test = base.withFieldValue(MOY_RULE, 1);
        assertEquals(test.getFieldValueMap().size(), 2);
        assertEquals(test.getFieldValue(YEAR_RULE), 2008);
        assertEquals(test.getFieldValue(MOY_RULE), 1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withFieldValue_null() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        test.withFieldValue(NULL_RULE, 30);
    }

    //-----------------------------------------------------------------------
    // withFieldRemoved()
    //-----------------------------------------------------------------------
    public void test_withFieldRemoved() {
        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields test = base.withFieldRemoved(MOY_RULE);
        assertEquals(test.getFieldValueMap().size(), 1);
        assertEquals(test.getFieldValue(YEAR_RULE), 2008);
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 2);
        assertEquals(base.getFieldValue(YEAR_RULE), 2008);
        assertEquals(base.getFieldValue(MOY_RULE), 6);
    }

    public void test_withFieldRemoved_fieldNotPresent() {
        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields test = base.withFieldRemoved(DOM_RULE);
        assertSame(test, base);
    }

    public void test_withFieldRemoved_emptySingleton() {
        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008);
        DateTimeFields test = base.withFieldRemoved(YEAR_RULE);
        assertSame(test, DateTimeFields.fields());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withFieldRemoved_null() {
        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        test.withFieldRemoved(NULL_RULE);
    }

    //-----------------------------------------------------------------------
    // mergeFields()
    //-----------------------------------------------------------------------
    public void test_mergeFields() {
        DateTimeFields base = DateTimeFields.fields(AMPM_RULE, 0, HOUR_AM_PM_RULE, 9);  // 9am
        DateTimeFields test = base.mergeFields();
        assertEquals(test.getFieldValueMap().size(), 1);
        assertEquals(test.getFieldValue(HOUR_RULE), 9);  // merged to 09:00
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 2);
        assertEquals(base.getFieldValue(AMPM_RULE), 0);
        assertEquals(base.getFieldValue(HOUR_AM_PM_RULE), 9);
    }

    public void test_mergeFields_empty() {
        DateTimeFields base = DateTimeFields.fields();
        DateTimeFields test = base.mergeFields();
        assertSame(test, base);
    }

    public void test_mergeFields_doubleMerge() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(AMPM_RULE, 0).withFieldValue(HOUR_AM_PM_RULE, 9)  // 9am -> 09:00
            .withFieldValue(QOY_RULE, 2).withFieldValue(MOQ_RULE, 3);  // Q2M3 -> June
        DateTimeFields test = base.mergeFields();
        assertEquals(test.getFieldValueMap().size(), 2);
        assertEquals(test.getFieldValue(HOUR_RULE), 9);  // merged to 09:00
        assertEquals(test.getFieldValue(MOY_RULE), 6);  // merged to June
    }

    public void test_mergeFields_nothingToMerge() {
        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6).withFieldValue(DOM_RULE, 30);
        DateTimeFields test = base.mergeFields();
        assertEquals(test.getFieldValueMap().size(), 3);
        assertEquals(test.getFieldValue(YEAR_RULE), 2008);
        assertEquals(test.getFieldValue(MOY_RULE), 6);
        assertEquals(test.getFieldValue(DOM_RULE), 30);
    }

    public void test_mergeFields_otherFieldsUntouched() {
        DateTimeFields base = DateTimeFields.fields(AMPM_RULE, 0, HOUR_AM_PM_RULE, 9).withFieldValue(YEAR_RULE, 2008);
        DateTimeFields test = base.mergeFields();
        assertEquals(test.getFieldValueMap().size(), 2);
        assertEquals(test.getFieldValue(HOUR_RULE), 9);  // merged to 09:00
        assertEquals(test.getFieldValue(YEAR_RULE), 2008);
    }

    public void test_mergeFields_resultFieldMeansNoMerge() {
        DateTimeFields base = DateTimeFields.fields(AMPM_RULE, 0, HOUR_AM_PM_RULE, 9).withFieldValue(HOUR_RULE, 10);
        DateTimeFields test = base.mergeFields();
        assertEquals(test.getFieldValueMap().size(), 1);
        assertEquals(test.getFieldValue(HOUR_RULE), 10);  // 9am ignored, but field-value pairs removed
    }

    //-----------------------------------------------------------------------
    // validateFields()
    //-----------------------------------------------------------------------
    public void test_validateFields_valid() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 11)
            .withFieldValue(DOM_RULE, 2);
        DateTimeFields test = base.validateFields();
        assertSame(test, base);
    }

    public void test_validateFields_empty() {
        DateTimeFields base = DateTimeFields.fields();
        DateTimeFields test = base.validateFields();
        assertSame(test, base);
    }

    public void test_validateFields_valid_noCrossValidation() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 2)
            .withFieldValue(DOM_RULE, 31);  // 31st February
        DateTimeFields test = base.validateFields();
        assertSame(test, base);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_validateFields_invalid() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 13)  // invalid
            .withFieldValue(DOM_RULE, 2);
        try {
            test.validateFields();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), MOY_RULE);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // mergeToDate()
    //-----------------------------------------------------------------------
    public void test_mergeToDate() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30);
        LocalDate test = base.mergeToDate();
        assertEquals(test, LocalDate.date(2008, 6, 30));
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 3);
        assertEquals(base.getFieldValue(YEAR_RULE), 2008);
        assertEquals(base.getFieldValue(MOY_RULE), 6);
        assertEquals(base.getFieldValue(DOM_RULE), 30);
    }

    public void test_mergeToDate_lenientInsideBounds() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 31);
        LocalDate test = base.mergeToDate();
        assertEquals(test, LocalDate.date(2008, 7, 1));
    }

    public void test_mergeToDate_lenientOutsideBounds() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 32);
        LocalDate test = base.mergeToDate();
        assertEquals(test, LocalDate.date(2008, 7, 2));
    }

    public void test_mergeToDate_otherFieldMismatch() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong, but ignored
        LocalDate test = base.mergeToDate();
        assertEquals(test, LocalDate.date(2008, 6, 30));
    }

    public void test_mergeToDate_noPrimarySet() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6);
        LocalDate test = base.mergeToDate();
        assertEquals(test, null);
    }

    public void test_mergeToDate_empty() {
        DateTimeFields base = DateTimeFields.fields();
        LocalDate test = base.mergeToDate();
        assertEquals(test, null);
    }

    public void test_mergeToDate_twoPrimarySets() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOY_RULE, 182);
        LocalDate test = base.mergeToDate();
        assertEquals(test, LocalDate.date(2008, 6, 30));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_mergeToDate_twoPrimarySetsDiffer() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOY_RULE, 183);
        base.mergeToDate();
    }

    //-----------------------------------------------------------------------
    // mergeToTime()
    //-----------------------------------------------------------------------
    public void test_mergeToTime() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        LocalTime test = base.mergeToTime();
        assertEquals(test, LocalTime.time(11, 30));
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 2);
        assertEquals(base.getFieldValue(HOUR_RULE), 11);
        assertEquals(base.getFieldValue(MIN_RULE), 30);
    }

    public void test_mergeToTime_lenientOutsideBounds() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 70);
        LocalTime test = base.mergeToTime();
        assertEquals(test, LocalTime.time(12, 10));
    }

    public void test_mergeToTime_otherFieldMismatch() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 11:30 is AM, but this says PM and is ignored
        LocalTime test = base.mergeToTime();
        assertEquals(test, LocalTime.time(11, 30));
    }

    public void test_mergeToTime_noPrimarySet() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_AM_PM_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        LocalTime test = base.mergeToTime();
        assertEquals(test, null);
    }

    public void test_mergeToTime_empty() {
        DateTimeFields base = DateTimeFields.fields();
        LocalTime test = base.mergeToTime();
        assertEquals(test, null);
    }

    public void test_mergeToTime_twoPrimarySets() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400000);
        LocalTime test = base.mergeToTime();
        assertEquals(test, LocalTime.time(11, 30));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_mergeToTime_twoPrimarySetsDiffer() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400001);
        base.mergeToTime();
    }

    //-----------------------------------------------------------------------
    // mergeToDateTime()
    //-----------------------------------------------------------------------
    public void test_mergeToDateTime() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        LocalDateTime test = base.mergeToDateTime();
        assertEquals(test, LocalDateTime.dateTime(2008, 6, 30, 11, 30));
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 5);
        assertEquals(base.getFieldValue(YEAR_RULE), 2008);
        assertEquals(base.getFieldValue(MOY_RULE), 6);
        assertEquals(base.getFieldValue(DOM_RULE), 30);
        assertEquals(base.getFieldValue(HOUR_RULE), 11);
        assertEquals(base.getFieldValue(MIN_RULE), 30);
    }

    public void test_mergeToDateTime_lenientOutsideBounds() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 70);
        LocalDateTime test = base.mergeToDateTime();
        assertEquals(test, LocalDateTime.dateTime(2008, 6, 30, 12, 10));
    }

    public void test_mergeToDateTime_lenientDayOverflow() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 23)
            .withFieldValue(MIN_RULE, 70);
        LocalDateTime test = base.mergeToDateTime();
        assertEquals(test, LocalDateTime.dateTime(2008, 7, 1, 0, 10));
    }

    public void test_mergeToDateTime_otherFieldMismatch() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 11:30 is AM, but this says PM and is ignored
        LocalDateTime test = base.mergeToDateTime();
        assertEquals(test, LocalDateTime.dateTime(2008, 6, 30, 11, 30));
    }

    public void test_mergeToDateTime_noPrimarySetForDate() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        LocalDateTime test = base.mergeToDateTime();
        assertEquals(test, null);
    }

    public void test_mergeToDateTime_noPrimarySetForTime() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_AM_PM_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        LocalDateTime test = base.mergeToDateTime();
        assertEquals(test, null);
    }

    public void test_mergeToDateTime_empty() {
        DateTimeFields base = DateTimeFields.fields();
        LocalDateTime test = base.mergeToDateTime();
        assertEquals(test, null);
    }

    public void test_mergeToDateTime_twoPrimarySets() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400000);
        LocalDateTime test = base.mergeToDateTime();
        assertEquals(test, LocalDateTime.dateTime(2008, 6, 30, 11, 30));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_mergeToDateTime_twoPrimarySetsDiffer() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400001);
        base.mergeToDateTime();
    }

    //-----------------------------------------------------------------------
    // matchesDate()
    //-----------------------------------------------------------------------
    public void test_matchesDate() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30);
        LocalDate date = LocalDate.date(2008, 6, 30);
        assertEquals(test.matchesDate(date), true);
        // check original immutable
        assertEquals(test.getFieldValueMap().size(), 3);
        assertEquals(test.getFieldValue(YEAR_RULE), 2008);
        assertEquals(test.getFieldValue(MOY_RULE), 6);
        assertEquals(test.getFieldValue(DOM_RULE), 30);
    }

    public void test_matchesDate_dowMatches() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOW_RULE, 1);
        LocalDate date = LocalDate.date(2008, 6, 30);
        assertEquals(test.matchesDate(date), true);
    }

    public void test_matchesDate_dowNotMatches() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOW_RULE, 2);  // 2008-06-30 is Monday not Tuesday
        LocalDate date = LocalDate.date(2008, 6, 30);
        assertEquals(test.matchesDate(date), false);
    }

    public void test_matchesDate_partialMatch() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6);
        LocalDate date = LocalDate.date(2008, 6, 30);
        assertEquals(test.matchesDate(date), true);
    }

    public void test_matchesDate_timeIgnored() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 12);
        LocalDate date = LocalDate.date(2008, 6, 30);
        assertEquals(test.matchesDate(date), true);
    }

    public void test_matchesDate_invalidDay() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 31);
        LocalDate date = LocalDate.date(2008, 6, 30);
        assertEquals(test.matchesDate(date), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_null() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30);
        try {
            test.matchesDate((LocalDate) null);
        } catch (NullPointerException ex) {
            // check original immutable
            assertEquals(test.getFieldValueMap().size(), 3);
            assertEquals(test.getFieldValue(YEAR_RULE), 2008);
            assertEquals(test.getFieldValue(MOY_RULE), 6);
            assertEquals(test.getFieldValue(DOM_RULE), 30);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // matchesTime()
    //-----------------------------------------------------------------------
    public void test_matchesTime() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        LocalTime time = LocalTime.time(11, 30);
        assertEquals(test.matchesTime(time), true);
        // check original immutable
        assertEquals(test.getFieldValueMap().size(), 2);
        assertEquals(test.getFieldValue(HOUR_RULE), 11);
        assertEquals(test.getFieldValue(MIN_RULE), 30);
    }

    public void test_matchesTime_amPmMatches() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 0);
        LocalTime time = LocalTime.time(11, 30);
        assertEquals(test.matchesTime(time), true);
    }

    public void test_matchesTime_amPmNotMatches() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // time is 11:30, but this says PM
        LocalTime time = LocalTime.time(11, 30);
        assertEquals(test.matchesTime(time), false);
    }

    public void test_matchesTime_partialMatch() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11);
        LocalTime time = LocalTime.time(11, 30);
        assertEquals(test.matchesTime(time), true);
    }

    public void test_matchesTime_dateIgnored() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(YEAR_RULE, 2008);
        LocalTime time = LocalTime.time(11, 30);
        assertEquals(test.matchesTime(time), true);
    }

    public void test_matchesTime_invalidMinute() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, -1);
        LocalTime time = LocalTime.time(11, 30);
        assertEquals(test.matchesTime(time), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesTime_null() {
        DateTimeFields test = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        try {
            test.matchesTime((LocalTime) null);
        } catch (NullPointerException ex) {
            // check original immutable
            assertEquals(test.getFieldValueMap().size(), 2);
            assertEquals(test.getFieldValue(HOUR_RULE), 11);
            assertEquals(test.getFieldValue(MIN_RULE), 30);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    public void test_toLocalDate() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30);
        LocalDate test = base.toLocalDate();
        assertEquals(test, LocalDate.date(2008, 6, 30));
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 3);
        assertEquals(base.getFieldValue(YEAR_RULE), 2008);
        assertEquals(base.getFieldValue(MOY_RULE), 6);
        assertEquals(base.getFieldValue(DOM_RULE), 30);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_toLocalDate_lenientInsideBounds() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 31);
        try {
            base.toLocalDate();
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), MOY_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_toLocalDate_lenientOutsideBounds() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 32);
        try {
            base.toLocalDate();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), DOM_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_toLocalDate_otherFieldMismatch() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong
        try {
            base.toLocalDate();
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), DOW_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDate_noPrimarySet() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6);
        base.toLocalDate();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDate_empty() {
        DateTimeFields base = DateTimeFields.fields();
        base.toLocalDate();
    }

    public void test_toLocalDate_twoPrimarySets() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOY_RULE, 182);
        LocalDate test = base.toLocalDate();
        assertEquals(test, LocalDate.date(2008, 6, 30));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDate_twoPrimarySetsDiffer() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOY_RULE, 183);
        base.toLocalDate();
    }

    //-----------------------------------------------------------------------
    // toLocalTime()
    //-----------------------------------------------------------------------
    public void test_toLocalTime() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        LocalTime test = base.toLocalTime();
        assertEquals(test, LocalTime.time(11, 30));
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 2);
        assertEquals(base.getFieldValue(HOUR_RULE), 11);
        assertEquals(base.getFieldValue(MIN_RULE), 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_toLocalTime_lenientOutsideBounds() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 70);
        try {
            base.toLocalTime();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), MIN_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_toLocalTime_otherFieldMismatch() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 11:30 is AM, but this says PM
        try {
            base.toLocalTime();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), AMPM_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalTime_noPrimarySet() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_AM_PM_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        base.toLocalTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalTime_empty() {
        DateTimeFields base = DateTimeFields.fields();
        base.toLocalTime();
    }

    public void test_toLocalTime_twoPrimarySets() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400000);
        LocalTime test = base.toLocalTime();
        assertEquals(test, LocalTime.time(11, 30));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalTime_twoPrimarySetsDiffer() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400001);
        base.toLocalTime();
    }

    //-----------------------------------------------------------------------
    // toLocalDateTime()
    //-----------------------------------------------------------------------
    public void test_toLocalDateTime() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        LocalDateTime test = base.toLocalDateTime();
        assertEquals(test, LocalDateTime.dateTime(2008, 6, 30, 11, 30));
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 5);
        assertEquals(base.getFieldValue(YEAR_RULE), 2008);
        assertEquals(base.getFieldValue(MOY_RULE), 6);
        assertEquals(base.getFieldValue(DOM_RULE), 30);
        assertEquals(base.getFieldValue(HOUR_RULE), 11);
        assertEquals(base.getFieldValue(MIN_RULE), 30);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_toLocalDateTime_lenientOutsideBounds() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 70);
        try {
            base.toLocalDateTime();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), MIN_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_toLocalDateTime_lenientDayOverflow() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 23)
            .withFieldValue(MIN_RULE, 70);
        try {
            base.toLocalDateTime();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getFieldRule(), MIN_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_toLocalDateTime_otherFieldMismatch() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 11:30 is AM, but this says PM
        try {
            base.toLocalDateTime();
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), AMPM_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDateTime_noPrimarySetForDate() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        base.toLocalDateTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDateTime_noPrimarySetForTime() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_AM_PM_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        base.toLocalDateTime();
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDateTime_empty() {
        DateTimeFields base = DateTimeFields.fields();
        base.toLocalDateTime();
    }

    public void test_toLocalDateTime_twoPrimarySets() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400000);
        LocalDateTime test = base.toLocalDateTime();
        assertEquals(test, LocalDateTime.dateTime(2008, 6, 30, 11, 30));
    }

    @Test(expectedExceptions=CalendarConversionException.class)
    public void test_toLocalDateTime_twoPrimarySetsDiffer() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400001);
        base.toLocalDateTime();
    }

    //-----------------------------------------------------------------------
    // toCalendrical()
    //-----------------------------------------------------------------------
    public void test_toCalendrical() {
        DateTimeFields base = DateTimeFields.fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30);
        Calendrical test = base.toCalendrical();
        assertEquals(test.getDate(), null);
        assertEquals(test.getTime(), null);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
        assertEquals(test.getFieldValueMap().size(), 3);
        assertEquals(test.getFieldValueMapValue(YEAR_RULE), 2008);
        assertEquals(test.getFieldValueMapValue(MOY_RULE), 6);
        assertEquals(test.getFieldValueMapValue(DOM_RULE), 30);
        // check original immutable
        assertEquals(base.getFieldValueMap().size(), 3);
        assertEquals(base.getFieldValue(YEAR_RULE), 2008);
        assertEquals(base.getFieldValue(MOY_RULE), 6);
        assertEquals(base.getFieldValue(DOM_RULE), 30);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals0() {
        DateTimeFields a = DateTimeFields.fields();
        DateTimeFields b = DateTimeFields.fields();
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_equal() {
        DateTimeFields a = DateTimeFields.fields(YEAR_RULE, 2008);
        DateTimeFields b = DateTimeFields.fields(YEAR_RULE, 2008);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_notEqualValue() {
        DateTimeFields a = DateTimeFields.fields(YEAR_RULE, 2008);
        DateTimeFields b = DateTimeFields.fields(YEAR_RULE, 2007);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_notEqualField() {
        DateTimeFields a = DateTimeFields.fields(YEAR_RULE, 2008);
        DateTimeFields b = DateTimeFields.fields(MOY_RULE, 2008);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_equal() {
        DateTimeFields a = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields b = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualOneValue() {
        DateTimeFields a = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields b = DateTimeFields.fields(YEAR_RULE, 2007, MOY_RULE, 6);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualTwoValues() {
        DateTimeFields a = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields b = DateTimeFields.fields(YEAR_RULE, 2007, MOY_RULE, 5);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualField() {
        DateTimeFields a = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields b = DateTimeFields.fields(YEAR_RULE, 2008, DOM_RULE, 6);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_otherType() {
        DateTimeFields a = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(a.equals("Rubbish"), false);
    }

    public void test_equals_null() {
        DateTimeFields a = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(a.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString0() {
        DateTimeFields base = DateTimeFields.fields();
        String test = base.toString();
        assertEquals(test, "{}");
    }

    public void test_toString1() {
        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008);
        String test = base.toString();
        assertEquals(test, "{Year=2008}");
    }

    public void test_toString2() {
        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
        String test = base.toString();
        assertEquals(test, "{Year=2008, MonthOfYear=6}");
    }

}
