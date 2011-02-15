/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.ISODateTimeRule.AMPM_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.YEAR;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DateTimeFields.
 */
@Test
public class TestDateTimeFields {

    private static final DateTimeFieldRule NULL_RULE = null;

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Calendrical.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(CalendricalMatcher.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(Iterable.class.isAssignableFrom(DateTimeFields.class));
        assertTrue(Serializable.class.isAssignableFrom(DateTimeFields.class));
    }

    @DataProvider(name="simple")
    Object[][] data_simple() {
        return new Object[][] {
            {DateTimeFields.EMPTY},
            {DateTimeFields.of(YEAR, 2008)},
            {DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6)},
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
        if (fields.size() == 0) {
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
            assertTrue(Modifier.isFinal(field.getModifiers()), "Field:" + field.getName());
            if (Modifier.isStatic(field.getModifiers()) == false) {
                assertTrue(Modifier.isPrivate(field.getModifiers()), "Field:" + field.getName());
            }
        }
        Constructor<?>[] cons = cls.getDeclaredConstructors();
        for (Constructor<?> con : cons) {
            assertTrue(Modifier.isPrivate(con.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    public void singleton_empty() {
        DateTimeFields test = DateTimeFields.EMPTY;
        assertEquals(test.size(), 0);
        assertSame(DateTimeFields.EMPTY, DateTimeFields.EMPTY);
    }

    //-----------------------------------------------------------------------
    // factories
    //-----------------------------------------------------------------------
    public void factory_fields_onePair() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008);
        assertFields(test, YEAR, 2008);
    }

    public void factory_fields_onePair_invalidValue() {
        DateTimeFields test = DateTimeFields.of(MONTH_OF_YEAR, 13);
        assertFields(test, MONTH_OF_YEAR, 13);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_onePair_null() {
        DateTimeFields.of(NULL_RULE, 1);
    }

    //-----------------------------------------------------------------------
    public void factory_fields_twoPairs() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 6);
    }

    public void factory_fields_twoPairs_orderNotSignificant() {
        DateTimeFields test = DateTimeFields.of(MONTH_OF_YEAR, 6, YEAR, 2008);
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 6);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void factory_fields_twoPairs_sameFieldOverwrites() {
        DateTimeFields.of(MONTH_OF_YEAR, 6, MONTH_OF_YEAR, 7);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_twoPairs_nullFirst() {
        DateTimeFields.of(NULL_RULE, 1, MONTH_OF_YEAR, 6);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_twoPairs_nullSecond() {
        DateTimeFields.of(MONTH_OF_YEAR, 6, NULL_RULE, 1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_twoPairs_nullBoth() {
        DateTimeFields.of(NULL_RULE, 1, NULL_RULE, 6);
    }

    //-----------------------------------------------------------------------
    public void factory_fields_iterable() {
        List<DateTimeField> list = new CopyOnWriteArrayList<DateTimeField>();  // CopyOnWriteArrayList objects to nulls
        list.add(DateTimeField.of(YEAR, 2008));
        list.add(DateTimeField.of(MONTH_OF_YEAR, 6));
        DateTimeFields test = DateTimeFields.of(list);
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 6);
    }

    public void factory_fields_iterable_sorted() {
        List<DateTimeField> list = new ArrayList<DateTimeField>();
        list.add(DateTimeField.of(MONTH_OF_YEAR, 6));
        list.add(DateTimeField.of(YEAR, 2008));
        DateTimeFields test = DateTimeFields.of(list);
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 6);
    }

    public void factory_fields_iterable_empty_singleton() {
        List<DateTimeField> list = new ArrayList<DateTimeField>();
        assertSame(DateTimeFields.of(list), DateTimeFields.EMPTY);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void factory_fields_iterable_duplicate() {
        List<DateTimeField> list = new ArrayList<DateTimeField>();
        list.add(DateTimeField.of(YEAR, 2008));
        list.add(DateTimeField.of(MONTH_OF_YEAR, 6));
        list.add(DateTimeField.of(YEAR, 2008));
        DateTimeFields.of(list);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_iterable_null() {
        DateTimeFields.of((Iterable<DateTimeField>) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_iterable_nullContent() {
        List<DateTimeField> list = new ArrayList<DateTimeField>();
        list.add(DateTimeField.of(YEAR, 2008));
        list.add(null);
        DateTimeFields.of(list);
    }

    //-----------------------------------------------------------------------
    public void factory_fields_array() {
        List<DateTimeField> list = new ArrayList<DateTimeField>();
        list.add(DateTimeField.of(YEAR, 2008));
        list.add(DateTimeField.of(MONTH_OF_YEAR, 6));
        DateTimeFields test = DateTimeFields.of(list.toArray(new DateTimeField[0]));
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 6);
    }

    public void factory_fields_array_sorted() {
        List<DateTimeField> list = new ArrayList<DateTimeField>();
        list.add(DateTimeField.of(MONTH_OF_YEAR, 6));
        list.add(DateTimeField.of(YEAR, 2008));
        DateTimeFields test = DateTimeFields.of(list.toArray(new DateTimeField[0]));
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 6);
    }

    public void factory_fields_array_empty_singleton() {
        List<DateTimeField> list = new ArrayList<DateTimeField>();
        assertSame(DateTimeFields.of(list.toArray(new DateTimeField[0])), DateTimeFields.EMPTY);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void factory_fields_array_duplicate() {
        List<DateTimeField> list = new ArrayList<DateTimeField>();
        list.add(DateTimeField.of(YEAR, 2008));
        list.add(DateTimeField.of(MONTH_OF_YEAR, 6));
        list.add(DateTimeField.of(YEAR, 2008));
        DateTimeFields.of(list.toArray(new DateTimeField[0]));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_array_null() {
        DateTimeFields.of((DateTimeField[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_fields_array_nullContent() {
        List<DateTimeField> list = new ArrayList<DateTimeField>();
        list.add(DateTimeField.of(YEAR, 2008));
        list.add(null);
        DateTimeFields.of(list.toArray(new DateTimeField[0]));
    }

    //-----------------------------------------------------------------------
    // size()
    //-----------------------------------------------------------------------
    public void test_size0() {
        DateTimeFields test = DateTimeFields.EMPTY;
        assertEquals(test.size(), 0);
    }

    public void test_size1() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008);
        assertEquals(test.size(), 1);
    }

    public void test_size2() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.size(), 2);
    }

    //-----------------------------------------------------------------------
    // iterator()
    //-----------------------------------------------------------------------
    public void test_iterator0() {
        DateTimeFields test = DateTimeFields.EMPTY;
        Iterator<DateTimeField> iterator = test.iterator();
        assertEquals(iterator.hasNext(), false);
    }

    public void test_iterator2() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        Iterator<DateTimeField> iterator = test.iterator();
        assertEquals(iterator.hasNext(), true);
        assertEquals(iterator.next(), YEAR.field(2008));
        assertEquals(iterator.hasNext(), true);
        assertEquals(iterator.next(), MONTH_OF_YEAR.field(6));
        assertEquals(iterator.hasNext(), false);
    }

    //-----------------------------------------------------------------------
    // contains()
    //-----------------------------------------------------------------------
    public void test_contains() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.contains(YEAR), true);
        assertEquals(test.contains(MONTH_OF_YEAR), true);
    }

    public void test_contains_null() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.contains(NULL_RULE), false);
    }

    public void test_contains_fieldNotPresent() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.contains(DAY_OF_MONTH), false);
    }

    //-----------------------------------------------------------------------
    // getField()
    //-----------------------------------------------------------------------
    public void test_getField() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.getField(YEAR), YEAR.field(2008));
        assertEquals(test.getField(MONTH_OF_YEAR), MONTH_OF_YEAR.field(6));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getField_null() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        test.getField((DateTimeFieldRule) null);
    }

    public void test_getField_fieldNotDerived() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.getField(QUARTER_OF_YEAR), null);
    }

    public void test_getField_fieldNotPresent() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.getField(DAY_OF_MONTH), null);
    }

    //-----------------------------------------------------------------------
    // getValue()
    //-----------------------------------------------------------------------
    public void test_getValue() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.getValue(YEAR), 2008);
        assertEquals(test.getValue(MONTH_OF_YEAR), 6);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValue_null() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        test.getValue(NULL_RULE);
    }

    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_getValue_fieldNotPresent() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        try {
            test.getValue(DAY_OF_MONTH);
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // getValidValue()
    //-----------------------------------------------------------------------
    public void test_getValidValue() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.getValidValue(YEAR), 2008);
        assertEquals(test.getValidValue(MONTH_OF_YEAR), 6);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValidValue_null() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        test.getValidValue(NULL_RULE);
    }

    @Test(expectedExceptions=CalendricalRuleException.class)
    public void test_getValidValue_fieldNotPresent() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        try {
            test.getValidValue(DAY_OF_MONTH);
        } catch (CalendricalRuleException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_getValidValue_invalid() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 13);  // out of range
        try {
            test.getValidValue(MONTH_OF_YEAR);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MONTH_OF_YEAR);
            assertEquals(ex.getRule(), MONTH_OF_YEAR);
            throw ex;
        }
    }

//    //-----------------------------------------------------------------------
//    // getQuiet()
//    //-----------------------------------------------------------------------
//    public void test_getQuiet() {
//        DateTimeFields test = DateTimeFields.of(YEAR_RULE, 2008, MOY_RULE, 6);
//        assertEquals(test.getQuiet(YEAR_RULE), Integer.valueOf(2008));
//        assertEquals(test.getQuiet(MOY_RULE), Integer.valueOf(6));
//    }
//
//    public void test_getQuiet_null() {
//        DateTimeFields test = DateTimeFields.of(YEAR_RULE, 2008, MOY_RULE, 6);
//        assertEquals(test.getQuiet(NULL_RULE), null);
//    }
//
//    public void test_getQuiet_fieldNotPresent() {
//        DateTimeFields test = DateTimeFields.of(YEAR_RULE, 2008, MOY_RULE, 6);
//        assertEquals(test.getQuiet(DOM_RULE), null);
//    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with() {
        DateTimeFields base = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        DateTimeFields test = base.with(DAY_OF_MONTH, 30);
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 6, DAY_OF_MONTH, 30);
        // check original immutable
        assertFields(base, YEAR, 2008, MONTH_OF_YEAR, 6);
    }

    public void test_with_invalidValue() {
        DateTimeFields base = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        DateTimeFields test = base.with(DAY_OF_MONTH, 123);
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 6, DAY_OF_MONTH, 123);
    }

    public void test_with_sameFieldOverwrites() {
        DateTimeFields base = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        DateTimeFields test = base.with(MONTH_OF_YEAR, 1);
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_null() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        test.with(NULL_RULE, 30);
    }

//    //-----------------------------------------------------------------------
//    // with(Map)
//    //-----------------------------------------------------------------------
//    public void test_with_map() {
//        // using Hashtable checks for incorrect null checking
//        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        Map<DateTimeFieldRule, Integer> map = new Hashtable<DateTimeFieldRule, Integer>();
//        map.put(DOM_RULE, 30);
//        DateTimeFields test = base.with(map);
//        assertFields(test, YEAR_RULE, 2008, MOY_RULE, 6, DOM_RULE, 30);
//        // check original immutable
//        assertFields(base, YEAR_RULE, 2008, MOY_RULE, 6);
//    }
//
//    public void test_with_map_empty() {
//        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
//        DateTimeFields test = base.with(map);
//        assertSame(test, base);
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_with_map_invalidValue() {
//        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
//        map.put(DOM_RULE, -1);
//        try {
//            base.with(map);
//        } catch (IllegalCalendarFieldValueException ex) {
//            assertEquals(ex.getFieldRule(), DOM_RULE);
//            assertFields(base, YEAR_RULE, 2008, MOY_RULE, 6);
//            throw ex;
//        }
//    }
//
//    public void test_with_map_sameFieldOverwrites() {
//        DateTimeFields base = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
//        map.put(MOY_RULE, 1);
//        DateTimeFields test = base.with(map);
//        assertFields(test, YEAR_RULE, 2008, MOY_RULE, 1);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_with_map_null() {
//        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        test.with(NULL_MAP);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_with_map_nullKey() {
//        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
//        map.put(null, 1);
//        test.with(map);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_with_map_nullValue() {
//        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
//        map.put(DOM_RULE, null);
//        test.with(map);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_with_map_nullBoth() {
//        DateTimeFields test = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6);
//        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
//        map.put(null, null);
//        test.with(map);
//    }

//    //-----------------------------------------------------------------------
//    // with(DateTimeFields)
//    //-----------------------------------------------------------------------
//    public void test_with_fields() {
//        DateTimeFields base = DateTimeFields.of(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields fields = DateTimeFields.of(DOM_RULE, 30);
//        DateTimeFields test = base.with(fields);
//        assertFields(test, YEAR_RULE, 2008, MOY_RULE, 6, DOM_RULE, 30);
//        // check original immutable
//        assertFields(base, YEAR_RULE, 2008, MOY_RULE, 6);
//    }
//
//    public void test_with_fields_sameFieldOverwrites() {
//        DateTimeFields base = DateTimeFields.of(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields fields = DateTimeFields.of(MOY_RULE, 1);
//        DateTimeFields test = base.with(fields);
//        assertFields(test, YEAR_RULE, 2008, MOY_RULE, 1);
//    }
//
//    public void test_with_fields_self() {
//        DateTimeFields base = DateTimeFields.of(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields test = base.with(base);
//        assertSame(test, base);
//    }
//
//    public void test_with_fields_emptyAdd() {
//        DateTimeFields base = DateTimeFields.of(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields test = base.with(DateTimeFields.EMPTY);
//        assertSame(test, base);
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_with_fields_null() {
//        DateTimeFields test = DateTimeFields.of(YEAR_RULE, 2008, MOY_RULE, 6);
//        DateTimeFields fields = null;
//        test.with(fields);
//    }

    //-----------------------------------------------------------------------
    // without()
    //-----------------------------------------------------------------------
    public void test_withFieldRemoved() {
        DateTimeFields base = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        DateTimeFields test = base.without(MONTH_OF_YEAR);
        assertFields(test, YEAR, 2008);
        // check original immutable
        assertFields(base, YEAR, 2008, MONTH_OF_YEAR, 6);
    }

    public void test_withFieldRemoved_fieldNotPresent() {
        DateTimeFields base = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        DateTimeFields test = base.without(DAY_OF_MONTH);
        assertSame(test, base);
    }

    public void test_withFieldRemoved_emptySingleton() {
        DateTimeFields base = DateTimeFields.of(YEAR, 2008);
        DateTimeFields test = base.without(YEAR);
        assertSame(test, DateTimeFields.EMPTY);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_withFieldRemoved_null() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        test.without(NULL_RULE);
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    public void test_get() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.get(YEAR), YEAR.field(2008));
        assertEquals(test.get(MONTH_OF_YEAR), MONTH_OF_YEAR.field(6));
    }

    public void test_get_derived() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.get(QUARTER_OF_YEAR), QUARTER_OF_YEAR.field(2));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_get_null() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        test.get((CalendricalRule<?>) null);
    }

    public void test_get_fieldNotPresent() {
        DateTimeFields test = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(test.get(DAY_OF_MONTH), null);
    }

    //-----------------------------------------------------------------------
    // matchesCalendrical()
    //-----------------------------------------------------------------------
    public void test_matchesCalendrical_ymd_date() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(YEAR, 2008)
            .with(MONTH_OF_YEAR, 6)
            .with(DAY_OF_MONTH, 30);
        LocalDate date = LocalDate.of(2008, 6, 30);
        assertEquals(test.matchesCalendrical(date), true);
        // check original immutable
        assertFields(test, YEAR, 2008, MONTH_OF_YEAR, 6, DAY_OF_MONTH, 30);
    }

    public void test_matchesCalendrical_dowMatches() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(YEAR, 2008)
            .with(MONTH_OF_YEAR, 6)
            .with(DAY_OF_MONTH, 30)
            .with(DAY_OF_WEEK, 1);
        LocalDate date = LocalDate.of(2008, 6, 30);
        assertEquals(test.matchesCalendrical(date), true);
    }

    public void test_matchesCalendrical_dowNotMatches() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(YEAR, 2008)
            .with(MONTH_OF_YEAR, 6)
            .with(DAY_OF_MONTH, 30)
            .with(DAY_OF_WEEK, 2);  // 2008-06-30 is Monday not Tuesday
        LocalDate date = LocalDate.of(2008, 6, 30);
        assertEquals(test.matchesCalendrical(date), false);
    }

    public void test_matchesCalendrical_ym_date_partialMatch() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(YEAR, 2008)
            .with(MONTH_OF_YEAR, 6);
        LocalDate date = LocalDate.of(2008, 6, 30);
        assertEquals(test.matchesCalendrical(date), true);
    }

    public void test_matchesCalendrical_timeIgnored() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(YEAR, 2008)
            .with(MONTH_OF_YEAR, 6)
            .with(DAY_OF_MONTH, 30)
            .with(HOUR_OF_DAY, 12);
        LocalDate date = LocalDate.of(2008, 6, 30);
        assertEquals(test.matchesCalendrical(date), true);
    }

    public void test_matchesCalendrical_invalidDay() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(YEAR, 2008)
            .with(MONTH_OF_YEAR, 6)
            .with(DAY_OF_MONTH, 31);
        LocalDate date = LocalDate.of(2008, 6, 30);
        assertEquals(test.matchesCalendrical(date), false);
    }

    public void test_matchesCalendrical_hm_time() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(HOUR_OF_DAY, 11)
            .with(MINUTE_OF_HOUR, 30);
        LocalTime time = LocalTime.of(11, 30);
        assertEquals(test.matchesCalendrical(time), true);
        // check original immutable
        assertFields(test, HOUR_OF_DAY, 11, MINUTE_OF_HOUR, 30);
    }

    public void test_matchesCalendrical_amPmMatches() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(HOUR_OF_DAY, 11)
            .with(MINUTE_OF_HOUR, 30)
            .with(AMPM_OF_DAY, 0);
        LocalTime time = LocalTime.of(11, 30);
        assertEquals(test.matchesCalendrical(time), true);
    }

    public void test_matchesCalendrical_amPmNotMatches() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(HOUR_OF_DAY, 11)
            .with(MINUTE_OF_HOUR, 30)
            .with(AMPM_OF_DAY, 1);  // time is 11:30, but this says PM
        LocalTime time = LocalTime.of(11, 30);
        assertEquals(test.matchesCalendrical(time), false);
    }

    public void test_matchesCalendrical_h_time_partialMatch() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(HOUR_OF_DAY, 11);
        LocalTime time = LocalTime.of(11, 30);
        assertEquals(test.matchesCalendrical(time), true);
    }

    public void test_matchesCalendrical_dateIgnored() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(HOUR_OF_DAY, 11)
            .with(MINUTE_OF_HOUR, 30)
            .with(YEAR, 2008);
        LocalTime time = LocalTime.of(11, 30);
        assertEquals(test.matchesCalendrical(time), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesCalendrical_null() {
        DateTimeFields test = DateTimeFields.EMPTY
            .with(HOUR_OF_DAY, 11)
            .with(MINUTE_OF_HOUR, 30);
        test.matchesCalendrical(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesCalendrical_null_emptyFields() {
        DateTimeFields test = DateTimeFields.EMPTY;
        test.matchesCalendrical((LocalTime) null);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals0() {
        DateTimeFields a = DateTimeFields.EMPTY;
        DateTimeFields b = DateTimeFields.EMPTY;
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_equal() {
        DateTimeFields a = DateTimeFields.of(YEAR, 2008);
        DateTimeFields b = DateTimeFields.of(YEAR, 2008);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_notEqualValue() {
        DateTimeFields a = DateTimeFields.of(YEAR, 2008);
        DateTimeFields b = DateTimeFields.of(YEAR, 2007);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals1_notEqualField() {
        DateTimeFields a = DateTimeFields.of(MONTH_OF_YEAR, 3);
        DateTimeFields b = DateTimeFields.of(DAY_OF_MONTH, 3);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_equal() {
        DateTimeFields a = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        DateTimeFields b = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(a.equals(b), true);
        assertEquals(a.hashCode() == b.hashCode(), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualOneValue() {
        DateTimeFields a = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        DateTimeFields b = DateTimeFields.of(YEAR, 2007, MONTH_OF_YEAR, 6);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualTwoValues() {
        DateTimeFields a = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        DateTimeFields b = DateTimeFields.of(YEAR, 2007, MONTH_OF_YEAR, 5);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals2_notEqualField() {
        DateTimeFields a = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        DateTimeFields b = DateTimeFields.of(YEAR, 2008, DAY_OF_MONTH, 6);
        assertEquals(a.equals(b), false);
        //assertEquals(a.hashCode() == b.hashCode(), false);  // doesn't have to be so
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_otherType() {
        DateTimeFields a = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(a.equals("Rubbish"), false);
    }

    public void test_equals_null() {
        DateTimeFields a = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        assertEquals(a.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString0() {
        DateTimeFields base = DateTimeFields.EMPTY;
        String test = base.toString();
        assertEquals(test, "[]");
    }

    public void test_toString1() {
        DateTimeFields base = DateTimeFields.of(YEAR, 2008);
        String test = base.toString();
        assertEquals(test, "[Year 2008]");
    }

    public void test_toString2() {
        DateTimeFields base = DateTimeFields.of(YEAR, 2008, MONTH_OF_YEAR, 6);
        String test = base.toString();
        assertEquals(test, "[Year 2008, MonthOfYear 6]");
    }

    //-----------------------------------------------------------------------
    private void assertFields(
            DateTimeFields fields,
            DateTimeFieldRule rule1, Integer value1) {
        List<DateTimeField> list = Arrays.asList(DateTimeField.of(rule1, value1));
        assertEquals(fields.size(), 1);
        assertEquals(fields.getField(rule1), list.get(0));
        assertEquals(fields.toString(), list.toString());
    }
    private void assertFields(
            DateTimeFields fields,
            DateTimeFieldRule rule1, Integer value1,
            DateTimeFieldRule rule2, Integer value2) {
        List<DateTimeField> list = Arrays.asList(DateTimeField.of(rule1, value1), DateTimeField.of(rule2, value2));
        assertEquals(fields.size(), 2);
        assertEquals(fields.getField(rule1), list.get(0));
        assertEquals(fields.getField(rule2), list.get(1));
        assertEquals(fields.toString(), list.toString());
    }
    private void assertFields(
            DateTimeFields fields,
            DateTimeFieldRule rule1, Integer value1,
            DateTimeFieldRule rule2, Integer value2,
            DateTimeFieldRule rule3, Integer value3) {
        List<DateTimeField> list = Arrays.asList(DateTimeField.of(rule1, value1), DateTimeField.of(rule2, value2), DateTimeField.of(rule3, value3));
        assertEquals(fields.size(), 3);
        assertEquals(fields.getField(rule1), list.get(0));
        assertEquals(fields.getField(rule2), list.get(1));
        assertEquals(fields.getField(rule3), list.get(2));
        assertEquals(fields.toString(), list.toString());
    }

}
