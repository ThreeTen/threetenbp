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

import static javax.time.calendar.DateTimeFields.*;
import static javax.time.calendar.LocalDate.*;
import static javax.time.calendar.LocalTime.*;
import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.calendar.LocalTime.Overflow;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test CalendricalMerger.
 */
@Test
public class TestCalendricalMerger {

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
    private static final DateTimeFieldRule HOUR_AMPM_RULE = ISOChronology.hourOfAmPmRule();
    private static final DateTimeFieldRule MIN_RULE = ISOChronology.minuteOfHourRule();
    private static final DateTimeFieldRule MILLI_RULE = ISOChronology.milliOfDayRule();

    private static final CalendricalContext STRICT_CONTEXT = new CalendricalContext(true, true);
    private static final CalendricalContext STRICT_DISCARD_UNUSED_CONTEXT = new CalendricalContext(true, false);
    private static final CalendricalContext LENIENT_CONTEXT = new CalendricalContext(false, true);
    private static final CalendricalContext LENIENT_DISCARD_UNUSED_CONTEXT = new CalendricalContext(false, false);

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(CalendricalProvider.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(DateProvider.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(TimeProvider.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(DateTimeProvider.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(DateMatcher.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(TimeMatcher.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(Iterable.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(Serializable.class.isAssignableFrom(CalendricalMerger.class));
    }

//    @DataProvider(name="simple")
//    Object[][] data_simple() {
//        return new Object[][] {
//            {new CalendricalMerger(DateTimeFields.fields(), STRICT_CONTEXT)},
//            {new CalendricalMerger(DateTimeFields.fields(YEAR_RULE, 2008), STRICT_CONTEXT)},
//            {new CalendricalMerger(DateTimeFields.fields(YEAR_RULE, 2008, MOY_RULE, 6), STRICT_CONTEXT)},
//        };
//    }
//
//    @Test(dataProvider="simple")
//    public void test_serialization(CalendricalMerger fieldMap) throws Exception {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//        oos.writeObject(fieldMap);
//        oos.close();
//        
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
//                baos.toByteArray()));
//        assertEquals(ois.readObject(), fieldMap);
//    }

    //-----------------------------------------------------------------------
    // constructors
    //-----------------------------------------------------------------------
    public void constructor_strict() {
        DateTimeFields fields = fields(YEAR_RULE, 2008);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        assertEquals(test.getOriginalFields(), fields);
        assertEquals(test.getContext(), STRICT_CONTEXT);
        assertEquals(test.isStrict(), true);
        assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>());
        assertEquals(test.getMergedFields(), fields);
        assertEquals(test.getMergedDate(), null);
        assertEquals(test.getMergedTime(), null);
    }

    public void constructor_lenient() {
        DateTimeFields fields = fields(YEAR_RULE, 2008);
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        assertEquals(test.getOriginalFields(), fields);
        assertEquals(test.getContext(), LENIENT_CONTEXT);
        assertEquals(test.isStrict(), false);
        assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>());
        assertEquals(test.getMergedFields(), fields);
        assertEquals(test.getMergedDate(), null);
        assertEquals(test.getMergedTime(), null);
    }

    //-----------------------------------------------------------------------
    // getProcessedFieldSet()
    //-----------------------------------------------------------------------
    public void test_getProcessedFieldSet_modifiableIndependent() {
        DateTimeFields fields = fields(YEAR_RULE, 2008);
        CalendricalMerger base = new CalendricalMerger(fields, STRICT_CONTEXT);
        Set<DateTimeFieldRule> test = base.getProcessedFieldSet();
        assertEquals(test, new HashSet<DateTimeFieldRule>());
        test.add(MOQ_RULE);
        assertEquals(base.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>());
    }

    //-----------------------------------------------------------------------
    // get()
    //-----------------------------------------------------------------------
    public void test_get() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, 6), STRICT_CONTEXT);
        assertEquals(test.get(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.get(MOY_RULE), Integer.valueOf(6));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_get_null() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, 6), STRICT_CONTEXT);
        test.get(NULL_RULE);
    }

    public void test_get_fieldNotPresent() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, 6), STRICT_CONTEXT);
        assertEquals(test.get(DOM_RULE), null);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_get_strictInvalidValue() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, -1), STRICT_CONTEXT);
        assertEquals(test.get(YEAR_RULE), Integer.valueOf(2008));
        try {
            test.get(MOY_RULE);
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MOY_RULE);
            throw ex;
        }
    }

    public void test_get_lenientInvalidValue() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, -1), LENIENT_CONTEXT);
        assertEquals(test.get(YEAR_RULE), Integer.valueOf(2008));
        assertEquals(test.get(MOY_RULE), Integer.valueOf(-1));
    }

    //-----------------------------------------------------------------------
    // getValue()
    //-----------------------------------------------------------------------
    public void test_getValue() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, 6), STRICT_CONTEXT);
        assertEquals(test.getValue(YEAR_RULE), 2008);
        assertEquals(test.getValue(MOY_RULE), 6);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValue_null() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, 6), STRICT_CONTEXT);
        test.get(NULL_RULE);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_getValue_fieldNotPresent() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, 6), STRICT_CONTEXT);
        try {
            test.getValue(DOM_RULE);
        } catch (UnsupportedCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOM_RULE);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_getValue_strictInvalidValue() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, -1), STRICT_CONTEXT);
        assertEquals(test.getValue(YEAR_RULE), 2008);
        try {
            test.getValue(MOY_RULE);
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MOY_RULE);
            throw ex;
        }
    }

    public void test_getValue_lenientInvalidValue() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008, MOY_RULE, -1), LENIENT_CONTEXT);
        assertEquals(test.getValue(YEAR_RULE), 2008);
        assertEquals(test.getValue(MOY_RULE), -1);
    }

    //-----------------------------------------------------------------------
    // markFieldAsProcessed()
    //-----------------------------------------------------------------------
    public void test_markFieldAsProcessed() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>());
        test.markFieldAsProcessed(YEAR_RULE);
        assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR_RULE)));
    }

    public void test_markFieldAsProcessed_set() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>());
        test.markFieldAsProcessed(YEAR_RULE);
        test.markFieldAsProcessed(YEAR_RULE);
        assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR_RULE)));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_markFieldAsProcessed_null() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        test.markFieldAsProcessed(YEAR_RULE);
        assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR_RULE)));
        
        try {
            test.markFieldAsProcessed(NULL_RULE);
        } catch (NullPointerException ex) {
            assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR_RULE)));
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // storeMergedDate()
    //-----------------------------------------------------------------------
    public void test_storeMergedDate() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        LocalDate date = date(2008, 6, 30);
        assertFields(test, YEAR_RULE, 2008, null, null);
        test.storeMergedDate(date);
        assertFields(test, YEAR_RULE, 2008, date, null);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_storeMergedDate_cannotChangeValue() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        LocalDate date1 = date(2008, 6, 30);
        test.storeMergedDate(date1);
        
        assertFields(test, YEAR_RULE, 2008, date1, null);
        LocalDate date2 = date(2008, 6, 29);
        try {
            test.storeMergedDate(date2);
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertFields(test, YEAR_RULE, 2008, date1, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedDate_nullWhenNull() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        
        assertFields(test, YEAR_RULE, 2008, null, null);
        test.storeMergedDate(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedDate_nullWhenNonNull() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        LocalDate date = date(2008, 6, 30);
        test.storeMergedDate(date);
        
        assertFields(test, YEAR_RULE, 2008, date, null);
        test.storeMergedDate(null);
    }

    //-----------------------------------------------------------------------
    // storeMergedTime()
    //-----------------------------------------------------------------------
    public void test_storeMergedTime() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        LocalTime.Overflow time = time(16, 30).toOverflow(1);
        assertFields(test, YEAR_RULE, 2008, null, null);
        test.storeMergedTime(time);
        assertFields(test, YEAR_RULE, 2008, null, time);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_storeMergedTime_cannotChangeValue() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        Overflow time1 = time(16, 30).toOverflow(1);
        test.storeMergedTime(time1);
        
        assertFields(test, YEAR_RULE, 2008, null, time1);
        Overflow time2 = time(16, 29).toOverflow(1);
        try {
            test.storeMergedTime(time2);
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertFields(test, YEAR_RULE, 2008, null, time1);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedTime_nullWhenNull() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        
        assertFields(test, YEAR_RULE, 2008, null, null);
        test.storeMergedTime(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedTime_nullWhenNonNull() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        Overflow time = time(16, 30).toOverflow(1);
        test.storeMergedTime(time);
        
        assertFields(test, YEAR_RULE, 2008, null, time);
        test.storeMergedTime(null);
    }

    //-----------------------------------------------------------------------
    // storeMergedField()
    //-----------------------------------------------------------------------
    public void test_storeMergedField() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        assertFields(test, YEAR_RULE, 2008, null, null);
        test.storeMergedField(DOM_RULE, 30);
        assertFields(test, YEAR_RULE, 2008, DOM_RULE, 30, null, null);
    }

    public void test_storeMergedField_invalidValueOK() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        assertFields(test, YEAR_RULE, 2008, null, null);
        test.storeMergedField(DOM_RULE, -1);
        assertFields(test, YEAR_RULE, 2008, DOM_RULE, -1, null, null);
    }

    public void test_storeMergedField_sameField_sameValue() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        assertFields(test, YEAR_RULE, 2008, null, null);
        test.storeMergedField(YEAR_RULE, 2008);
        assertFields(test, YEAR_RULE, 2008, null, null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_storeMergedField_sameField_differentValue() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        assertFields(test, YEAR_RULE, 2008, null, null);
        try {
            test.storeMergedField(YEAR_RULE, 2007);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(YEAR_RULE, ex.getFieldRule());
            assertFields(test, YEAR_RULE, 2008, null, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedField_null() {
        CalendricalMerger test = new CalendricalMerger(fields(YEAR_RULE, 2008), STRICT_CONTEXT);
        try {
            test.storeMergedField(NULL_RULE, 30);
        } catch (NullPointerException ex) {
            assertFields(test, YEAR_RULE, 2008, null, null);
            throw ex;
        }
    }

//    //-----------------------------------------------------------------------
//    // deriveValue(field)
//    //-----------------------------------------------------------------------
//    public void test_deriveValue_ymd() {
//        CalendricalMerger test = new CalendricalMerger()
//            .put(YEAR_RULE, 2008)
//            .put(MOY_RULE, 6)
//            .put(DOM_RULE, 30);
//        assertFieldValue(test.deriveValue(YEAR_RULE), 2008);
//        assertFieldValue(test.deriveValue(MOY_RULE), 6);
//        assertFieldValue(test.deriveValue(DOM_RULE), 30);
//    }
//
//    public void test_deriveValue_deriveLarger() {
//        CalendricalMerger test = new CalendricalMerger()
//            .put(QOY_RULE, 3)
//            .put(MOQ_RULE, 2);
//        assertFieldValue(test.deriveValue(QOY_RULE), 3);  // direct get
//        assertFieldValue(test.deriveValue(MOQ_RULE), 2);  // direct get
//        assertFieldValue(test.deriveValue(MOY_RULE), 8);  // derive
//    }
//
//    public void test_deriveValue_derivedSmaller() {
//        CalendricalMerger test = new CalendricalMerger()
//            .put(MOY_RULE, 8);
//        assertFieldValue(test.deriveValue(MOY_RULE), 8);  // direct get
//        assertFieldValue(test.deriveValue(QOY_RULE), 3);  // derive
//        assertFieldValue(test.deriveValue(MOQ_RULE), 2);  // derive
//    }
//
//    public void test_deriveValue_derivedTree() {
//        CalendricalMerger test = new CalendricalMerger()
//            .put(MockYearOfDecadeFieldRule.INSTANCE, 2)
//            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
//            .put(MockCenturyFieldRule.INSTANCE, 19);
//        assertFieldValue(test.deriveValue(MockYearOfDecadeFieldRule.INSTANCE), 2);  // direct get
//        assertFieldValue(test.deriveValue(MockDecadeOfCenturyFieldRule.INSTANCE), 7);  // direct get
//        assertFieldValue(test.deriveValue(MockCenturyFieldRule.INSTANCE), 19);  // direct get
//        assertFieldValue(test.deriveValue(MockYearOfCenturyFieldRule.INSTANCE), 72);  // derive
//        assertFieldValue(test.deriveValue(YEAR_RULE), 1972);  // derive tree
//    }
//
//    public void test_deriveValue_derivedTree_twoLayersProvideSameField_sameValue() {
//        CalendricalMerger test = new CalendricalMerger()
//            .put(MockYearOfDecadeFieldRule.INSTANCE, 2)
//            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
//            .put(MockYearOfCenturyFieldRule.INSTANCE, 72);
//        assertFieldValue(test.deriveValue(MockYearOfDecadeFieldRule.INSTANCE), 2);  // direct get
//        assertFieldValue(test.deriveValue(MockDecadeOfCenturyFieldRule.INSTANCE), 7);  // direct get
//        assertFieldValue(test.deriveValue(MockYearOfCenturyFieldRule.INSTANCE), 72);  // derive, and check
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_deriveValue_derivedTree_twoLayersProvideSameField_differentValue() {
//        CalendricalMerger test = new CalendricalMerger()
//            .put(MockYearOfDecadeFieldRule.INSTANCE, 1)
//            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
//            .put(MockYearOfCenturyFieldRule.INSTANCE, 72);
//        try {
//            test.deriveValue(MockYearOfCenturyFieldRule.INSTANCE); // 72 and derived 71 differ
//        } catch (InvalidCalendarFieldException ex ) {
//            assertEquals(ex.getFieldRule(), MockYearOfCenturyFieldRule.INSTANCE);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_deriveValue_derivedTree_twoLayersProvideSameField_notRequestedLayer_differentValue() {
//        CalendricalMerger test = new CalendricalMerger()
//            .put(MockYearOfDecadeFieldRule.INSTANCE, 1)
//            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
//            .put(MockYearOfCenturyFieldRule.INSTANCE, 72)
//            .put(MockCenturyFieldRule.INSTANCE, 19);
//        try {
//            test.deriveValue(ISOChronology.yearRule()); // 72 and derived 71 differ
//        } catch (InvalidCalendarFieldException ex ) {
//            assertEquals(ex.getFieldRule(), MockYearOfCenturyFieldRule.INSTANCE);
//            throw ex;
//        }
//    }
//
    //-----------------------------------------------------------------------
    // merge()
    //-----------------------------------------------------------------------
    public void test_merge() {
        DateTimeFields fields = fields(AMPM_RULE, 1, HOUR_AMPM_RULE, 9);  // 9pm
        CalendricalMerger base = new CalendricalMerger(fields, STRICT_CONTEXT);
        CalendricalMerger test = base.merge();
        assertSame(test, base);
        assertFields(test, null, time(21, 00).toOverflow(0));  // merged to 21:00
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_strict() {
        DateTimeFields fields = fields(AMPM_RULE, 1, HOUR_AMPM_RULE, 14);  // 14pm
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), HOUR_AMPM_RULE);
            throw ex;
        }
    }

    public void test_merge_lenient() {
        DateTimeFields fields = fields(AMPM_RULE, 1, HOUR_AMPM_RULE, 14);  // 14pm
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        test.merge();
        assertFields(test, null, time(2, 00).toOverflow(1));  // merged to 02:00 + 1day (26:00)
    }

    public void test_merge_empty() {
        CalendricalMerger base = new CalendricalMerger(fields(), STRICT_CONTEXT);
        CalendricalMerger test = base.merge();
        assertSame(test, base);
        assertEquals(test.getMergedFields().size(), 0);
    }

    public void test_merge_doubleMerge() {
        DateTimeFields fields = fields()
            .withFieldValue(AMPM_RULE, 1).withFieldValue(HOUR_AMPM_RULE, 9)  // 9pm -> 21:00
            .withFieldValue(QOY_RULE, 2).withFieldValue(MOQ_RULE, 3);  // Q2M3 -> June
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, MOY_RULE, 6, null, time(21, 0).toOverflow(0));  // merged to 21:00 and June
    }

    public void test_merge_nothingToMerge() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOQ_RULE, 1)
            .withFieldValue(DOM_RULE, 30);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, YEAR_RULE, 2008, MOQ_RULE, 1, DOM_RULE, 30, null, null);
    }

    public void test_merge_otherFieldsUntouched() {
        DateTimeFields fields = fields()
            .withFieldValue(QOY_RULE, 3)
            .withFieldValue(MOQ_RULE, 2)
            .withFieldValue(MIN_RULE, 30);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, MOY_RULE, 8, MIN_RULE, 30, null, null);  // merged to August
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_inconsistentValue() {
        DateTimeFields fields = fields()
            .withFieldValue(AMPM_RULE, 0)
            .withFieldValue(HOUR_AMPM_RULE, 9)  // 9am
            .withFieldValue(HOUR_RULE, 10);  // 10am
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), HOUR_RULE);
            assertFields(test, AMPM_RULE, 0, HOUR_AMPM_RULE, 9, HOUR_RULE, 10, null, null);
            throw ex;
        }
    }

    public void test_merge_multiLevelMerge_simple() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
            .withFieldValue(MockYearOfDecadeFieldRule.INSTANCE, 2)  // 7 + 2 -> 72
            .withFieldValue(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, YEAR_RULE, 1972, null, null);  // merged to 1972
    }

    public void test_merge_multiLevelMerge_fullUpToDate() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
            .withFieldValue(MockYearOfDecadeFieldRule.INSTANCE, 2)  // 7 + 2 -> 72
            .withFieldValue(MockCenturyFieldRule.INSTANCE, 19)  // 19 + 72 -> 1972
            .withFieldValue(QOY_RULE, 4)
            .withFieldValue(MOQ_RULE, 3)  // M3 + Q4 -> December
            .withFieldValue(DOM_RULE, 3);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, date(1972, 12, 3), null);  // merged to 1972-12-03
    }

    public void test_merge_multiLevelMerge_crossCheck_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
            .withFieldValue(MockYearOfDecadeFieldRule.INSTANCE, 2)  // 7 + 2 -> 72
            .withFieldValue(MockYearOfCenturyFieldRule.INSTANCE, 72)  // cross check against year of century
            .withFieldValue(MockCenturyFieldRule.INSTANCE, 19)  // 19 + 72 -> 1972
            .withFieldValue(YEAR_RULE, 1972);  // cross check against year
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, YEAR_RULE, 1972, null, null);  // merged to 1972
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_multiLevelMerge_crossCheck_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
            .withFieldValue(MockYearOfDecadeFieldRule.INSTANCE, 2)  // 7 + 2 -> 72
            .withFieldValue(MockYearOfCenturyFieldRule.INSTANCE, 71)  // cross check against year of century
            .withFieldValue(MockCenturyFieldRule.INSTANCE, 19)  // 19 + 72 -> 1972
            .withFieldValue(YEAR_RULE, 1972);  // cross check against year
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), YEAR_RULE);
            assertFields(test, MockDecadeOfCenturyFieldRule.INSTANCE, 7, MockYearOfDecadeFieldRule.INSTANCE, 2,
                    MockYearOfCenturyFieldRule.INSTANCE, 71, MockCenturyFieldRule.INSTANCE, 19, YEAR_RULE, 1972,
                    null, null);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_merge_singleLevel_derivableRemoved_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 7)  // derivable from YearOfCentury
            .withFieldValue(MockYearOfCenturyFieldRule.INSTANCE, 72);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, MockYearOfCenturyFieldRule.INSTANCE, 72, null, null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_singleLevel_derivableChecked_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from YearOfCentury, value is wrong
            .withFieldValue(MockYearOfCenturyFieldRule.INSTANCE, 72);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
            assertFields(test, MockDecadeOfCenturyFieldRule.INSTANCE, 6, MockYearOfCenturyFieldRule.INSTANCE, 72, null, null);
            throw ex;
        }
    }

    public void test_merge_singleLevel_derivableDiscardUnused_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from YearOfCentury, value is wrong
            .withFieldValue(MockYearOfCenturyFieldRule.INSTANCE, 72);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_DISCARD_UNUSED_CONTEXT);
        test.merge();
        assertFields(test, MockYearOfCenturyFieldRule.INSTANCE, 72, null, null);
    }

    //-----------------------------------------------------------------------
    public void test_merge_multiLevel_simple_derivableRemoved_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 7)  // derivable from Year
            .withFieldValue(YEAR_RULE, 1972);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, YEAR_RULE, 1972, null, null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_multiLevel_simple_derivableChecked_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from Year, value is wrong
            .withFieldValue(YEAR_RULE, 1972);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
            assertFields(test, MockDecadeOfCenturyFieldRule.INSTANCE, 6, YEAR_RULE, 1972, null, null);
            throw ex;
        }
    }

    public void test_merge_multiLevel_simple_derivableDiscardUnused_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from Year, value is wrong
            .withFieldValue(YEAR_RULE, 1972);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_DISCARD_UNUSED_CONTEXT);
        test.merge();
        assertFields(test, YEAR_RULE, 1972, null, null);
    }

    //-----------------------------------------------------------------------
    public void test_merge_multiLevel_complex_derivableRemoved_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 7)  // derivable from Year
            .withFieldValue(MockYearOfCenturyFieldRule.INSTANCE, 72)
            .withFieldValue(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, YEAR_RULE, 1972, null, null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_multiLevel_complex_derivableChecked_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from Year, value is wrong
            .withFieldValue(MockYearOfCenturyFieldRule.INSTANCE, 72)
            .withFieldValue(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
            assertFields(test, MockDecadeOfCenturyFieldRule.INSTANCE, 6, YEAR_RULE, 1972, null, null);
            throw ex;
        }
    }

    public void test_merge_multiLevel_complex_derivableDiscardUnused_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from Year, value is wrong
            .withFieldValue(MockYearOfCenturyFieldRule.INSTANCE, 72)
            .withFieldValue(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_DISCARD_UNUSED_CONTEXT);
        test.merge();
        assertFields(test, YEAR_RULE, 1972, null, null);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_merge_toDate_directDMY() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), null);
    }

    public void test_merge_toDate_directDY() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(DOY_RULE, 182);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), null);
    }

    public void test_merge_toDate_mergeFieldsThenDMY() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(QOY_RULE, 2)
            .withFieldValue(MOQ_RULE, 3)
            .withFieldValue(DOM_RULE, 30);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), null);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDate_direct_strictInvalidWithinBounds() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 31);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOM_RULE);
            assertFields(test, YEAR_RULE, 2008, MOY_RULE, 6, DOM_RULE, 31, null, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_toDate_direct_strictInvalidOutsideBounds() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 32);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOM_RULE);
            assertFields(test, YEAR_RULE, 2008, MOY_RULE, 6, DOM_RULE, 32, null, null);
            throw ex;
        }
    }

    public void test_merge_toDate_direct_lenientInvalidWithinBounds() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 31);
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 7, 1), null);
    }

    public void test_merge_toDate_direct_lenientInvalidOutsideBounds() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 32);
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 7, 2), null);
    }

    //-----------------------------------------------------------------------
    public void test_merge_toDate_twoPrimarySetsMatch() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOY_RULE, 182);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), null);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_merge_toDate_twoPrimarySetsDiffer() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOY_RULE, 183);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertEquals(test.getMergedDate(), date(2008, 6, 30));
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_mergeToDate_strict_crossCheck_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOW_RULE, 1)   // 2008-06-30 is a Monday, so this is right
            .withFieldValue(QOY_RULE, 2)   // 2008-06-30 is Q2, so this is right
            .withFieldValue(MIN_RULE, 30);  // ignored
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, MIN_RULE, 30, date(2008, 6, 30), null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_mergeToDate_strict_crossCheck_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOW_RULE);
            assertFields(test, DOW_RULE, 2, date(2008, 6, 30), null);
            throw ex;
        }
    }

    public void test_mergeToDate_strict_crossCheck_discardUnused() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong, but discarded
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_DISCARD_UNUSED_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), null);
    }

    //-----------------------------------------------------------------------
    public void test_mergeToDate_lenient_crossCheck_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 32)
            .withFieldValue(DOW_RULE, 3)   // 2008-07-02 is a Wednesday, so this is right
            .withFieldValue(QOY_RULE, 3)   // 2008-07-02 is Q3, so this is right
            .withFieldValue(MIN_RULE, 30);  // ignored
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        test.merge();
        assertFields(test, MIN_RULE, 30, date(2008, 7, 2), null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_mergeToDate_lenient_crossCheck_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 32)
            .withFieldValue(DOW_RULE, 2);  // 2008-07-02 is a Wednesday, so this is wrong
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOW_RULE);
            assertFields(test, DOW_RULE, 2, date(2008, 7, 2), null);
            throw ex;
        }
    }

    public void test_mergeToDate_lenient_crossCheck_discardUnused() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 32)
            .withFieldValue(DOW_RULE, 2);  // 2008-07-02 is a Wednesday, so this is wrong, but discarded
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_DISCARD_UNUSED_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 7, 2), null);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_merge_toTime_directHM() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, null, time(11, 30).toOverflow(0));
    }

    public void test_merge_toTime_fieldsThenHM() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_AMPM_RULE, 2)
            .withFieldValue(AMPM_RULE, 1)
            .withFieldValue(MIN_RULE, 30);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, null, time(14, 30).toOverflow(0));
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_toTime_strict_outsideBoundsHours() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 24)
            .withFieldValue(MIN_RULE, 30);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), HOUR_RULE);
            assertFields(test, HOUR_RULE, 24, MIN_RULE, 30, null, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_toTime_strict_outsideBoundsMins() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 70);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MIN_RULE);
            assertFields(test, HOUR_RULE, 11, MIN_RULE, 70, null, null);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_lenient_outsideBoundsHours() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 24)
            .withFieldValue(MIN_RULE, 30);
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        test.merge();
        assertFields(test, null, time(0, 30).toOverflow(1));
    }

    public void test_merge_toTime_lenient_outsideBoundsMins() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 70);
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        test.merge();
        assertFields(test, null, time(12, 10).toOverflow(0));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_twoPrimarySetsMatch() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400000);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, null, time(11, 30).toOverflow(0));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_merge_toTime_twoPrimarySetsDiffer() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(MILLI_RULE, 41400001);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertEquals(test.getMergedTime(), time(11, 30).toOverflow(0));
            //assertFields(test, MILLI_RULE, 41400001, null, time(11, 30).toOverflow(0));
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_strict_crossCheck_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 0);  // 11:30 is AM, so this is right
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, null, time(11, 30).toOverflow(0));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toTime_strict_crossCheck_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertFields(test, AMPM_RULE, 1, null, time(11, 30).toOverflow(0));
            throw ex;
        }
    }

    public void test_merge_toTime_strict_crossCheck_discardUnused() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong, but discarded
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_DISCARD_UNUSED_CONTEXT);
        test.merge();
        assertFields(test, null, time(11, 30).toOverflow(0));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_lenient_crossCheck_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 24)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 0);  // 00:30 is AM, so this is right
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        test.merge();
        assertFields(test, null, time(0, 30).toOverflow(1));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toTime_lenient_crossCheck_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 24)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 00:30 is AM, so this is wrong
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertFields(test, AMPM_RULE, 1, null, time(0, 30).toOverflow(1));
            throw ex;
        }
    }

    public void test_merge_toTime_lenient_crossCheck_discardUnused() {
        DateTimeFields fields = fields()
            .withFieldValue(HOUR_RULE, 24)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 00:30 is AM, so this is wrong, but ignored
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_DISCARD_UNUSED_CONTEXT);
        test.merge();
        assertFields(test, null, time(0, 30).toOverflow(1));
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_merge_toDateTime_directYMDHM() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30);
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), time(11, 30).toOverflow(0));
    }

    public void test_merge_toDateTime_lenientOverflow() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 23)
            .withFieldValue(MIN_RULE, 70);
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), time(0, 10).toOverflow(1));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toDateTime_strict_crossCheck_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 0);  // 11:30 is AM, so this is correct
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), time(11, 30).toOverflow(0));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDateTime_strict_crossCheck_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), AMPM_RULE);
            assertFields(test, AMPM_RULE, 1, date(2008, 6, 30), time(11, 30).toOverflow(0));
            throw ex;
        }
    }

    public void test_merge_toDateTime_strict_crossCheck_discardUnused() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 11)
            .withFieldValue(MIN_RULE, 30)
            .withFieldValue(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong, but discarded
        CalendricalMerger test = new CalendricalMerger(fields, STRICT_DISCARD_UNUSED_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), time(11, 30).toOverflow(0));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toDateTime_lenient_crossCheck_valid() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 23)
            .withFieldValue(MIN_RULE, 70)
            .withFieldValue(AMPM_RULE, 0);  // 00:10 is AM, so this is correct
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), time(0, 10).toOverflow(1));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDateTime_lenient_crossCheck_invalid() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 23)
            .withFieldValue(MIN_RULE, 70)
            .withFieldValue(AMPM_RULE, 1);  // 00:10 is AM, so this is wrong
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_CONTEXT);
        try {
            test.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), AMPM_RULE);
            assertFields(test, AMPM_RULE, 1, date(2008, 6, 30), time(0, 10).toOverflow(1));
            throw ex;
        }
    }

    public void test_merge_toDateTime_lenient_crossCheck_discardUnused() {
        DateTimeFields fields = fields()
            .withFieldValue(YEAR_RULE, 2008)
            .withFieldValue(MOY_RULE, 6)
            .withFieldValue(DOM_RULE, 30)
            .withFieldValue(HOUR_RULE, 23)
            .withFieldValue(MIN_RULE, 70)
            .withFieldValue(AMPM_RULE, 1);  // 00:10 is AM, so this is wrong, but discarded
        CalendricalMerger test = new CalendricalMerger(fields, LENIENT_DISCARD_UNUSED_CONTEXT);
        test.merge();
        assertFields(test, date(2008, 6, 30), time(0, 10).toOverflow(1));
    }

//    //-----------------------------------------------------------------------
//    // toString()
//    //-----------------------------------------------------------------------
//    public void test_toString() {
//        CalendricalMerger base = new CalendricalMerger(fields(), STRICT_CONTEXT);
//        String test = base.toString();
//        assertNotNull(test);
//        System.out.println(test);
//    }

    //-----------------------------------------------------------------------
    private static void assertFields(
            CalendricalMerger merger,
            LocalDate date, Overflow time) {
        Map<DateTimeFieldRule, Integer> map = merger.getMergedFields().toFieldValueMap();
        assertEquals(map.size(), 0);
        assertEquals(merger.getMergedDate(), date);
        assertEquals(merger.getMergedTime(), time);
    }
    private static void assertFields(
            CalendricalMerger merger,
            DateTimeFieldRule rule1, int value1,
            LocalDate date, Overflow time) {
        Map<DateTimeFieldRule, Integer> map = merger.getMergedFields().toFieldValueMap();
        assertEquals(map.size(), 1);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(merger.getMergedDate(), date);
        assertEquals(merger.getMergedTime(), time);
    }
    private static void assertFields(
            CalendricalMerger merger,
            DateTimeFieldRule rule1, int value1,
            DateTimeFieldRule rule2, int value2,
            LocalDate date, Overflow time) {
        Map<DateTimeFieldRule, Integer> map = merger.getMergedFields().toFieldValueMap();
        assertEquals(map.size(), 2);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(map.get(rule2), Integer.valueOf(value2));
        assertEquals(merger.getMergedDate(), date);
        assertEquals(merger.getMergedTime(), time);
    }
    private static void assertFields(
            CalendricalMerger merger,
            DateTimeFieldRule rule1, int value1,
            DateTimeFieldRule rule2, int value2,
            DateTimeFieldRule rule3, int value3,
            LocalDate date, Overflow time) {
        Map<DateTimeFieldRule, Integer> map = merger.getMergedFields().toFieldValueMap();
        assertEquals(map.size(), 3);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(map.get(rule2), Integer.valueOf(value2));
        assertEquals(map.get(rule3), Integer.valueOf(value3));
        assertEquals(merger.getMergedDate(), date);
        assertEquals(merger.getMergedTime(), time);
    }
    private static void assertFields(
            CalendricalMerger merger,
            DateTimeFieldRule rule1, int value1,
            DateTimeFieldRule rule2, int value2,
            DateTimeFieldRule rule3, int value3,
            DateTimeFieldRule rule4, int value4,
            LocalDate date, Overflow time) {
        Map<DateTimeFieldRule, Integer> map = merger.getMergedFields().toFieldValueMap();
        assertEquals(map.size(), 4);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(map.get(rule2), Integer.valueOf(value2));
        assertEquals(map.get(rule3), Integer.valueOf(value3));
        assertEquals(map.get(rule4), Integer.valueOf(value4));
        assertEquals(merger.getMergedDate(), date);
        assertEquals(merger.getMergedTime(), time);
    }
    private static void assertFields(
            CalendricalMerger merger,
            DateTimeFieldRule rule1, int value1,
            DateTimeFieldRule rule2, int value2,
            DateTimeFieldRule rule3, int value3,
            DateTimeFieldRule rule4, int value4,
            DateTimeFieldRule rule5, int value5,
            LocalDate date, Overflow time) {
        Map<DateTimeFieldRule, Integer> map = merger.getMergedFields().toFieldValueMap();
        assertEquals(map.size(), 5);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(map.get(rule2), Integer.valueOf(value2));
        assertEquals(map.get(rule3), Integer.valueOf(value3));
        assertEquals(map.get(rule4), Integer.valueOf(value4));
        assertEquals(map.get(rule5), Integer.valueOf(value5));
        assertEquals(merger.getMergedDate(), date);
        assertEquals(merger.getMergedTime(), time);
    }
    private static void assertFieldValue(Integer actual, int expected) {
        Assert.assertEquals(actual, (Integer) expected); 
    }
    private static void dumpException(Exception ex) {
        // this is used to allow a human to inspect the error messages to see if they are understandable
        System.out.println(ex.getMessage());
    }
}
