/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test Calendrical.
 */
@Test
public class TestCalendricalFieldMap {

    @SuppressWarnings("unchecked")
    private static final Map<DateTimeFieldRule, Integer> NULL_MAP = (Map) null;
    private static final DateTimeFieldRule NULL_RULE = null;
    private static final DateTimeFieldRule YEAR_RULE = ISOChronology.yearRule();
    private static final DateTimeFieldRule MOY_RULE = ISOChronology.monthOfYearRule();
//    private static final DateTimeFieldRule DOM_RULE = ISOChronology.dayOfMonthRule();
//    private static final DateTimeFieldRule DOY_RULE = ISOChronology.dayOfYearRule();
//    private static final DateTimeFieldRule DOW_RULE = ISOChronology.dayOfWeekRule();
    private static final DateTimeFieldRule QOY_RULE = ISOChronology.quarterOfYearRule();
    private static final DateTimeFieldRule MOQ_RULE = ISOChronology.monthOfQuarterRule();
//    private static final DateTimeFieldRule HOUR_RULE = ISOChronology.hourOfDayRule();
//    private static final DateTimeFieldRule AMPM_RULE = ISOChronology.amPmOfDayRule();
//    private static final DateTimeFieldRule HOUR_AM_PM_RULE = ISOChronology.hourOfAmPmRule();
//    private static final DateTimeFieldRule MIN_RULE = ISOChronology.minuteOfHourRule();
//    private static final DateTimeFieldRule SEC_RULE = ISOChronology.secondOfMinuteRule();
//    private static final DateTimeFieldRule NANO_RULE = ISOChronology.nanoOfSecondRule();
//    private static final DateTimeFieldRule MILLI_RULE = ISOChronology.milliOfDayRule();

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Iterable.class.isAssignableFrom(Calendrical.FieldMap.class));
        assertTrue(Serializable.class.isAssignableFrom(Calendrical.FieldMap.class));
    }

    @DataProvider(name="simple")
    Object[][] data_simple() {
        return new Object[][] {
            {new Calendrical().getFieldMap()},
            {new Calendrical(YEAR_RULE, 2008).getFieldMap()},
            {new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6).getFieldMap()},
        };
    }

    @Test(dataProvider="simple")
    public void test_serialization(Calendrical.FieldMap calendrical) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(calendrical);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(ois.readObject(), calendrical);
    }

    public void test_class() {
        Class<Calendrical.FieldMap> cls = Calendrical.FieldMap.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    public void test_get() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getFieldMap().size(), 2);
        assertEquals(test.getFieldMap().get(YEAR_RULE), 2008);
        assertEquals(test.getFieldMap().get(MOY_RULE), 6);
    }

    public void test_get_invalid() {
        Calendrical test = new Calendrical(MOY_RULE, 13);
        assertEquals(test.getFieldMap().get(MOY_RULE), 13);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_get_null() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.getFieldMap().get(NULL_RULE);
    }

    //-----------------------------------------------------------------------
    // getValidated()
    //-----------------------------------------------------------------------
    public void test_getValidated() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getFieldMap().size(), 2);
        assertEquals(test.getFieldMap().getValidated(YEAR_RULE), 2008);
        assertEquals(test.getFieldMap().getValidated(MOY_RULE), 6);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_getValidated_invalid() {
        Calendrical test = new Calendrical(MOY_RULE, 13);
        test.getFieldMap().getValidated(MOY_RULE);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValidated_null() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        test.getFieldMap().get(NULL_RULE);
    }

    //-----------------------------------------------------------------------
    // getQuiet()
    //-----------------------------------------------------------------------
    public void test_getQuiet() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        assertEquals(test.getFieldMap().size(), 2);
        assertEquals(test.getFieldMap().getQuiet(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.getFieldMap().getQuiet(MOY_RULE), Integer.valueOf(6));
    }

    public void test_getQuiet_notPresent() {
        Calendrical test = new Calendrical();
        assertEquals(test.getFieldMap().getQuiet(YEAR_RULE), null);
    }

    public void test_getQuiet_null() {
        Calendrical test = new Calendrical();
        assertEquals(test.getFieldMap().getQuiet(NULL_RULE), null);
    }

    //-----------------------------------------------------------------------
    // put()
    //-----------------------------------------------------------------------
    public void test_put() {
        Calendrical test = new Calendrical();
        test.getFieldMap().put(MOY_RULE, 12);
        assertEquals(test.getFieldMap().size(), 1);
        assertEquals(test.getFieldMap().get(MOY_RULE), 12);
    }

    
    @Test(expectedExceptions=NullPointerException.class)
    public void test_put_null() {
        Calendrical test = new Calendrical();
        test.getFieldMap().put(NULL_RULE, 12);
    }

    //-----------------------------------------------------------------------
    // putAll(Map)
    //-----------------------------------------------------------------------
    public void test_putAll() {
        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
        map.put(YEAR_RULE, 2008);
        map.put(MOY_RULE, 12);
        
        Calendrical test = new Calendrical();
        test.getFieldMap().putAll(map);
        assertEquals(test.getFieldMap().size(), 2);
        assertEquals(test.getFieldMap().get(YEAR_RULE), 2008);
        assertEquals(test.getFieldMap().get(MOY_RULE), 12);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_putAll_null() {
        Calendrical test = new Calendrical();
        test.getFieldMap().putAll(NULL_MAP);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_putAll_nullKey() {
        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
        map.put(YEAR_RULE, 2008);
        map.put(null, 12);
        
        Calendrical test = new Calendrical();
        test.getFieldMap().putAll(map);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_putAll_nullValue() {
        Map<DateTimeFieldRule, Integer> map = new HashMap<DateTimeFieldRule, Integer>();
        map.put(YEAR_RULE, 2008);
        map.put(MOY_RULE, null);
        
        Calendrical test = new Calendrical();
        test.getFieldMap().putAll(map);
    }

    //-----------------------------------------------------------------------
    // putAll(DateTimeFields)
    //-----------------------------------------------------------------------
    public void test_putAll_DateTimeFields() {
        DateTimeFields dtf = DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 12);
        
        Calendrical test = new Calendrical();
        test.getFieldMap().putAll(dtf);
        assertEquals(test.getFieldMap().size(), 2);
        assertEquals(test.getFieldMap().get(YEAR_RULE), 2008);
        assertEquals(test.getFieldMap().get(MOY_RULE), 12);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_putAll_DateTimeFields_null() {
        Calendrical test = new Calendrical();
        test.getFieldMap().putAll((DateTimeFields) null);
    }

    //-----------------------------------------------------------------------
    // remove()
    //-----------------------------------------------------------------------
    // TODO

    //-----------------------------------------------------------------------
    // removeAll()
    //-----------------------------------------------------------------------
    // TODO

    //-----------------------------------------------------------------------
    // clear()
    //-----------------------------------------------------------------------
    public void test_clear() {
        Calendrical test = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 12);
        test.getFieldMap().clear();
        assertEquals(test.getFieldMap().size(), 0);
    }

    //-----------------------------------------------------------------------
    // isValid()
    //-----------------------------------------------------------------------
    // TODO

    //-----------------------------------------------------------------------
    // validate()
    //-----------------------------------------------------------------------
    // TODO

    //-----------------------------------------------------------------------
    // deriveValueQuiet()
    //-----------------------------------------------------------------------
    public void test_deriveValueQuiet() {
        Calendrical test = new Calendrical(MOY_RULE, 8);
        assertEquals(test.getFieldMap().deriveValueQuiet(QOY_RULE), Integer.valueOf(3));
        assertEquals(test.getFieldMap().deriveValueQuiet(MOQ_RULE), Integer.valueOf(2));
    }

    public void test_deriveValueQuiet_notDerivable() {
        Calendrical test = new Calendrical(MOY_RULE, 8);
        assertEquals(test.getFieldMap().deriveValueQuiet(YEAR_RULE), null);
    }

    public void test_deriveValueQuiet_null() {
        Calendrical test = new Calendrical(MOY_RULE, 8);
        assertEquals(test.getFieldMap().deriveValueQuiet(NULL_RULE), null);
    }

    //-----------------------------------------------------------------------
    // toDateTimeFields()
    //-----------------------------------------------------------------------
    public void test_toDateTimeFields() {
        Calendrical base = new Calendrical(YEAR_RULE, 2008, MOY_RULE, 6);
        DateTimeFields test = base.getFieldMap().toDateTimeFields();
        assertEquals(test.size(), 2);
        assertEquals(test.get(YEAR_RULE), 2008);
        assertEquals(test.get(MOY_RULE), 6);
    }

    // TODO: more tests
}
