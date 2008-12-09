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

import static javax.time.calendar.LocalDate.*;
import static javax.time.calendar.LocalTime.*;
import static org.testng.Assert.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.calendar.Calendrical.Merger;
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
    private static final DateTimeFieldRule SEC_RULE = ISOChronology.secondOfMinuteRule();
    private static final DateTimeFieldRule MILLISEC_RULE = ISOChronology.milliOfSecondRule();
    private static final DateTimeFieldRule MILLIDAY_RULE = ISOChronology.milliOfDayRule();

    private static final CalendricalContext STRICT_CONTEXT = new CalendricalContext(true, true);
    private static final CalendricalContext STRICT_DISCARD_UNUSED_CONTEXT = new CalendricalContext(true, false);
    private static final CalendricalContext LENIENT_CONTEXT = new CalendricalContext(false, true);
    private static final CalendricalContext LENIENT_DISCARD_UNUSED_CONTEXT = new CalendricalContext(false, false);

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
//    public void test_interfaces() {
//        assertTrue(CalendricalProvider.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(DateProvider.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(TimeProvider.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(DateTimeProvider.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(DateMatcher.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(TimeMatcher.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(Iterable.class.isAssignableFrom(CalendricalMerger.class));
//        assertTrue(Serializable.class.isAssignableFrom(CalendricalMerger.class));
//    }

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

//    //-----------------------------------------------------------------------
//    // constructors
//    //-----------------------------------------------------------------------
//    public void constructor_strict() {
//        DateTimeFields fields = fields(YEAR_RULE, 2008);
//        Calendrical.Merger test = new Calendrical.Merger(fields, STRICT_CONTEXT);
//        assertEquals(test.getOriginalFields(), fields);
//        assertEquals(test.getContext(), STRICT_CONTEXT);
//        assertEquals(test.isStrict(), true);
//        assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>());
//        assertEquals(test.getMergedFields(), fields);
//        assertEquals(test.getMergedDate(), null);
//        assertEquals(test.getMergedTime(), null);
//    }
//
//    public void constructor_lenient() {
//        DateTimeFields fields = fields(YEAR_RULE, 2008);
//        Calendrical.Merger test = new Calendrical.Merger(fields, LENIENT_CONTEXT);
//        assertEquals(test.getOriginalFields(), fields);
//        assertEquals(test.getContext(), LENIENT_CONTEXT);
//        assertEquals(test.isStrict(), false);
//        assertEquals(test.getProcessedFieldSet(), new HashSet<DateTimeFieldRule>());
//        assertEquals(test.getMergedFields(), fields);
//        assertEquals(test.getMergedDate(), null);
//        assertEquals(test.getMergedTime(), null);
//    }
//

    //-----------------------------------------------------------------------
    // getValue()
    //-----------------------------------------------------------------------
    public void test_getValue() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                assertEquals(merger.getValue(YEAR_RULE), Integer.valueOf(2008));
                assertEquals(merger.getValue(this), Integer.valueOf(20));
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        cal.merge(STRICT_CONTEXT);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValue_null() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.getValue(NULL_RULE);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        cal.merge(STRICT_CONTEXT);
    }

    public void test_get_fieldNotPresent() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                assertEquals(merger.getValue(DOM_RULE), null);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        cal.merge(STRICT_CONTEXT);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_getValue_strictInvalidValue() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                fail();
            }
        };
        Calendrical cal = new Calendrical(MOY_RULE, -1, rule, 20);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MOY_RULE);
            throw ex;
        }
    }

    public void test_getValue_lenientInvalidValue() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                assertEquals(merger.getValue(MOY_RULE), Integer.valueOf(-1));
            }
        };
        Calendrical cal = new Calendrical(MOY_RULE, -1, rule, 20);
        cal.merge(LENIENT_CONTEXT);
    }

    //-----------------------------------------------------------------------
    // getValueInt()
    //-----------------------------------------------------------------------
    public void test_getValueInt() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                assertEquals(merger.getValueInt(YEAR_RULE), 2008);
                assertEquals(merger.getValueInt(this), 20);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        cal.merge(STRICT_CONTEXT);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValueInt_null() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.getValueInt(NULL_RULE);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        cal.merge(STRICT_CONTEXT);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_getValueInt_fieldNotPresent() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                try {
                    merger.getValueInt(DOM_RULE);
                } catch (UnsupportedCalendarFieldException ex) {
                    dumpException(ex);
                    assertEquals(ex.getFieldRule(), DOM_RULE);
                    throw ex;
                }
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        cal.merge(STRICT_CONTEXT);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_getValueInt_strictInvalidValue() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                fail();
            }
        };
        Calendrical cal = new Calendrical(MOY_RULE, -1, rule, 20);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MOY_RULE);
            throw ex;
        }
    }

    public void test_getValueInt_lenientInvalidValue() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                assertEquals(merger.getValueInt(MOY_RULE), -1);
            }
        };
        Calendrical cal = new Calendrical(MOY_RULE, -1, rule, 20);
        cal.merge(LENIENT_CONTEXT);
    }

    //-----------------------------------------------------------------------
    // markFieldAsProcessed()
    //-----------------------------------------------------------------------
    public void test_markFieldAsProcessed() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
                merger.markFieldAsProcessed(YEAR_RULE);
                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR_RULE)));
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        cal.merge(STRICT_CONTEXT);
    }

    public void test_markFieldAsProcessed_set() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
                merger.markFieldAsProcessed(YEAR_RULE);
                merger.markFieldAsProcessed(YEAR_RULE);
                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR_RULE)));
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        cal.merge(STRICT_CONTEXT);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_markFieldAsProcessed_null() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
                try {
                    merger.markFieldAsProcessed(NULL_RULE);
                } catch (NullPointerException ex) {
                    assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
                    throw ex;
                }
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        cal.merge(STRICT_CONTEXT);
        
    }

    @SuppressWarnings("unchecked")
    private Set<DateTimeFieldRule> getProcessedFieldSet(Merger test) {
        try {
            Field field = Merger.class.getDeclaredField("processedFieldSet");
            field.setAccessible(true);
            return (Set<DateTimeFieldRule>) field.get(test);
        } catch (Exception ex) {
            fail(ex.toString());
            return null;
        }
    }

    //-----------------------------------------------------------------------
    // storeMergedDate()
    //-----------------------------------------------------------------------
    public void test_storeMergedDate() {
        final LocalDate date = date(2008, 6, 30);
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedDate(date);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, rule, 20, date, null);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_storeMergedDate_cannotChangeValue() {
        final LocalDate date1 = date(2008, 6, 30);
        final LocalDate date2 = date(2008, 6, 29);
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedDate(date1);
                merger.storeMergedDate(date2);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertFields(cal, YEAR_RULE, 2008, rule, 20, date1, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedDate_nullWhenNull() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedDate(null);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (NullPointerException ex) {
            assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedDate_nullWhenNonNull() {
        final LocalDate date = date(2008, 6, 30);
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedDate(date);
                merger.storeMergedDate(null);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (NullPointerException ex) {
            assertFields(cal, YEAR_RULE, 2008, rule, 20, date, null);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // storeMergedTime(LocalTime)
    //-----------------------------------------------------------------------
    public void test_storeMergedTime() {
        final LocalTime time = time(16, 30);
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedTime(time);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, time);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_storeMergedTime_cannotChangeValue() {
        final LocalTime time1 = time(16, 30);
        final LocalTime time2 = time(16, 29);
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedTime(time1);
                merger.storeMergedTime(time2);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertFields(cal, YEAR_RULE, 2008, rule, 20, null, time1);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedTime_nullWhenNull() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedTime((LocalTime) null);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (NullPointerException ex) {
            assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedTime_nullWhenNonNull() {
        final LocalTime time = time(16, 30);
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedTime(time);
                merger.storeMergedTime((LocalTime) null);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (NullPointerException ex) {
            assertFields(cal, YEAR_RULE, 2008, rule, 20, null, time);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // storeMergedTime(LocalTime.Overflow)
    //-----------------------------------------------------------------------
    public void test_storeMergedTimeOverflow() {
        final Overflow time = time(16, 30).toOverflow(1);
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedTime(time);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, time.getResultTime());
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_storeMergedTimeOverflow_cannotChangeValue() {
        final Overflow time1 = time(16, 30).toOverflow(1);
        final Overflow time2 = time(16, 29).toOverflow(1);
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedTime(time1);
                merger.storeMergedTime(time2);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertFields(cal, YEAR_RULE, 2008, rule, 20, null, time1.getResultTime());
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedTimeOverflow_nullWhenNull() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedTime((LocalTime.Overflow) null);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (NullPointerException ex) {
            assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedTimeOverflow_nullWhenNonNull() {
        final Overflow time = time(16, 30).toOverflow(1);
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedTime(time);
                merger.storeMergedTime((LocalTime.Overflow) null);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (NullPointerException ex) {
            assertFields(cal, YEAR_RULE, 2008, rule, 20, null, time.getResultTime());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // storeMergedField()
    //-----------------------------------------------------------------------
    public void test_storeMergedField() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedField(DOM_RULE, 30);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, DOM_RULE, 30, null, null);
    }

    public void test_storeMergedField_invalidValueOK() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedField(DOM_RULE, -1);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, DOM_RULE, -1, null, null);
    }

    public void test_storeMergedField_sameField_sameValue() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedField(YEAR_RULE, 2008);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_storeMergedField_sameField_differentValue() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedField(YEAR_RULE, 2007);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(YEAR_RULE, ex.getFieldRule());
            assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedField_null() {
        DateTimeFieldRule rule = new MockFieldRule() {
            @Override
            protected void merge(Merger merger) {
                merger.storeMergedField(NULL_RULE, 30);
            }
        };
        Calendrical cal = new Calendrical(YEAR_RULE, 2008, rule, 20);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (NullPointerException ex) {
            assertFields(cal, YEAR_RULE, 2008, rule, 20, null, null);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // merge()
    //-----------------------------------------------------------------------
    public void test_merge() {
        Calendrical cal = new Calendrical(AMPM_RULE, 1, HOUR_AMPM_RULE, 9);  // 9pm
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, null, time(21, 00));  // merged to 21:00
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_strict() {
        Calendrical cal = new Calendrical(AMPM_RULE, 1, HOUR_AMPM_RULE, 14);  // 14pm
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), HOUR_AMPM_RULE);
            throw ex;
        }
    }

    public void test_merge_lenient() {
        Calendrical cal = new Calendrical(AMPM_RULE, 1, HOUR_AMPM_RULE, 14);  // 14pm
        cal.merge(LENIENT_CONTEXT);
        assertFields(cal, null, time(2, 00));  // merged to 02:00 + 1day (26:00)
    }

    public void test_merge_empty() {
        Calendrical cal = new Calendrical();
        cal.merge(STRICT_CONTEXT);
        assertEquals(cal.getFieldMap().size(), 0);
    }

    public void test_merge_doubleMerge() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(AMPM_RULE, 1).put(HOUR_AMPM_RULE, 9)  // 9pm -> 21:00
            .put(QOY_RULE, 2).put(MOQ_RULE, 3);  // Q2M3 -> June
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, MOY_RULE, 6, null, time(21, 0));  // merged to 21:00 and June
    }

    public void test_merge_nothingToMerge() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOQ_RULE, 1)
            .put(DOM_RULE, 30);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 2008, MOQ_RULE, 1, DOM_RULE, 30, null, null);
    }

    public void test_merge_otherFieldsUntouched() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(QOY_RULE, 3)
            .put(MOQ_RULE, 2)
            .put(MIN_RULE, 30);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, MOY_RULE, 8, MIN_RULE, 30, null, null);  // merged to August
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_inconsistentValue() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(AMPM_RULE, 0)
            .put(HOUR_AMPM_RULE, 9)  // 9am
            .put(HOUR_RULE, 10);  // 10am
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), HOUR_RULE);
            assertFields(cal, AMPM_RULE, 0, HOUR_AMPM_RULE, 9, HOUR_RULE, 10, null, null);
            throw ex;
        }
    }

    public void test_merge_multiLevelMerge_simple() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
            .put(MockYearOfDecadeFieldRule.INSTANCE, 2)  // 7 + 2 -> 72
            .put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 1972, null, null);  // merged to 1972
    }

    public void test_merge_multiLevelMerge_fullUpToDate() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
            .put(MockYearOfDecadeFieldRule.INSTANCE, 2)  // 7 + 2 -> 72
            .put(MockCenturyFieldRule.INSTANCE, 19)  // 19 + 72 -> 1972
            .put(QOY_RULE, 4)
            .put(MOQ_RULE, 3)  // M3 + Q4 -> December
            .put(DOM_RULE, 3);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, date(1972, 12, 3), null);  // merged to 1972-12-03
    }

    public void test_merge_multiLevelMerge_crossCheck_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
            .put(MockYearOfDecadeFieldRule.INSTANCE, 2)  // 7 + 2 -> 72
            .put(MockYearOfCenturyFieldRule.INSTANCE, 72)  // cross check against year of century
            .put(MockCenturyFieldRule.INSTANCE, 19)  // 19 + 72 -> 1972
            .put(YEAR_RULE, 1972);  // cross check against year
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 1972, null, null);  // merged to 1972
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_multiLevelMerge_crossCheck_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)
            .put(MockYearOfDecadeFieldRule.INSTANCE, 2)  // 7 + 2 -> 72
            .put(MockYearOfCenturyFieldRule.INSTANCE, 71)  // cross check against year of century
            .put(MockCenturyFieldRule.INSTANCE, 19)  // 19 + 72 -> 1972
            .put(YEAR_RULE, 1972);  // cross check against year
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), YEAR_RULE);
            assertFields(cal, MockDecadeOfCenturyFieldRule.INSTANCE, 7, MockYearOfDecadeFieldRule.INSTANCE, 2,
                    MockYearOfCenturyFieldRule.INSTANCE, 71, MockCenturyFieldRule.INSTANCE, 19, YEAR_RULE, 1972,
                    null, null);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_merge_singleLevel_derivableRemoved_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)  // derivable from YearOfCentury
            .put(MockYearOfCenturyFieldRule.INSTANCE, 72);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, MockYearOfCenturyFieldRule.INSTANCE, 72, null, null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_singleLevel_derivableChecked_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from YearOfCentury, value is wrong
            .put(MockYearOfCenturyFieldRule.INSTANCE, 72);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
            assertFields(cal, MockDecadeOfCenturyFieldRule.INSTANCE, 6, MockYearOfCenturyFieldRule.INSTANCE, 72, null, null);
            throw ex;
        }
    }

    public void test_merge_singleLevel_derivableDiscardUnused_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from YearOfCentury, value is wrong
            .put(MockYearOfCenturyFieldRule.INSTANCE, 72);
        cal.merge(STRICT_DISCARD_UNUSED_CONTEXT);
        assertFields(cal, MockYearOfCenturyFieldRule.INSTANCE, 72, null, null);
    }

    //-----------------------------------------------------------------------
    public void test_merge_multiLevel_simple_derivableRemoved_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)  // derivable from Year
            .put(YEAR_RULE, 1972);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 1972, null, null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_multiLevel_simple_derivableChecked_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from Year, value is wrong
            .put(YEAR_RULE, 1972);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
            assertFields(cal, MockDecadeOfCenturyFieldRule.INSTANCE, 6, YEAR_RULE, 1972, null, null);
            throw ex;
        }
    }

    public void test_merge_multiLevel_simple_derivableDiscardUnused_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from Year, value is wrong
            .put(YEAR_RULE, 1972);
        cal.merge(STRICT_DISCARD_UNUSED_CONTEXT);
        assertFields(cal, YEAR_RULE, 1972, null, null);
    }

    //-----------------------------------------------------------------------
    public void test_merge_multiLevel_complex_derivableRemoved_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 7)  // derivable from Year
            .put(MockYearOfCenturyFieldRule.INSTANCE, 72)
            .put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, YEAR_RULE, 1972, null, null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_multiLevel_complex_derivableChecked_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from Year, value is wrong
            .put(MockYearOfCenturyFieldRule.INSTANCE, 72)
            .put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
            assertFields(cal, MockDecadeOfCenturyFieldRule.INSTANCE, 6, YEAR_RULE, 1972, null, null);
            throw ex;
        }
    }

    public void test_merge_multiLevel_complex_derivableDiscardUnused_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(MockDecadeOfCenturyFieldRule.INSTANCE, 6)  // derivable from Year, value is wrong
            .put(MockYearOfCenturyFieldRule.INSTANCE, 72)
            .put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        cal.merge(STRICT_DISCARD_UNUSED_CONTEXT);
        assertFields(cal, YEAR_RULE, 1972, null, null);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_merge_toDate_directDMY() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, date(2008, 6, 30), null);
    }

    public void test_merge_toDate_directDY() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(DOY_RULE, 182);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, date(2008, 6, 30), null);
    }

    public void test_merge_toDate_mergeFieldsThenDMY() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(QOY_RULE, 2)
            .put(MOQ_RULE, 3)
            .put(DOM_RULE, 30);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, date(2008, 6, 30), null);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDate_direct_strictInvalidWithinBounds() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 31);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOM_RULE);
            assertFields(cal, YEAR_RULE, 2008, MOY_RULE, 6, DOM_RULE, 31, null, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_toDate_direct_strictInvalidOutsideBounds() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 32);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOM_RULE);
            assertFields(cal, YEAR_RULE, 2008, MOY_RULE, 6, DOM_RULE, 32, null, null);
            throw ex;
        }
    }

    public void test_merge_toDate_direct_lenientInvalidWithinBounds() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 31);
        cal.merge(LENIENT_CONTEXT);
        assertFields(cal, date(2008, 7, 1), null);
    }

    public void test_merge_toDate_direct_lenientInvalidOutsideBounds() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 32);
        cal.merge(LENIENT_CONTEXT);
        assertFields(cal, date(2008, 7, 2), null);
    }

    //-----------------------------------------------------------------------
    public void test_merge_toDate_twoPrimarySetsMatch() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(DOY_RULE, 182);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, date(2008, 6, 30), null);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_merge_toDate_twoPrimarySetsDiffer() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(DOY_RULE, 183);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertEquals(cal.getDate(), date(2008, 6, 30));
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_mergeToDate_strict_crossCheck_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(DOW_RULE, 1)   // 2008-06-30 is a Monday, so this is right
            .put(QOY_RULE, 2)   // 2008-06-30 is Q2, so this is right
            .put(MIN_RULE, 30);  // ignored
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, MIN_RULE, 30, date(2008, 6, 30), null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_mergeToDate_strict_crossCheck_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOW_RULE);
            assertFields(cal, DOW_RULE, 2, date(2008, 6, 30), null);
            throw ex;
        }
    }

    public void test_mergeToDate_strict_crossCheck_discardUnused() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong, but discarded
        cal.merge(STRICT_DISCARD_UNUSED_CONTEXT);
        assertFields(cal, date(2008, 6, 30), null);
    }

    //-----------------------------------------------------------------------
    public void test_mergeToDate_lenient_crossCheck_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 32)
            .put(DOW_RULE, 3)   // 2008-07-02 is a Wednesday, so this is right
            .put(QOY_RULE, 3)   // 2008-07-02 is Q3, so this is right
            .put(MIN_RULE, 30);  // ignored
        cal.merge(LENIENT_CONTEXT);
        assertFields(cal, MIN_RULE, 30, date(2008, 7, 2), null);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_mergeToDate_lenient_crossCheck_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 32)
            .put(DOW_RULE, 2);  // 2008-07-02 is a Wednesday, so this is wrong
        try {
            cal.merge(LENIENT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOW_RULE);
            assertFields(cal, DOW_RULE, 2, date(2008, 7, 2), null);
            throw ex;
        }
    }

    public void test_mergeToDate_lenient_crossCheck_discardUnused() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 32)
            .put(DOW_RULE, 2);  // 2008-07-02 is a Wednesday, so this is wrong, but discarded
        cal.merge(LENIENT_DISCARD_UNUSED_CONTEXT);
        assertFields(cal, date(2008, 7, 2), null);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_merge_toTime_directHM() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, null, time(11, 30));
    }

    public void test_merge_toTime_fieldsThenHM() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_AMPM_RULE, 2)
            .put(AMPM_RULE, 1)
            .put(MIN_RULE, 30);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, null, time(14, 30));
    }

    public void test_merge_toTime_fieldsThenHMSN() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 14)
            .put(MIN_RULE, 30)
            .put(SEC_RULE, 50)
            .put(MILLISEC_RULE, 1);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, null, time(14, 30, 50, 1000000));
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_toTime_strict_outsideBoundsHours() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 24)
            .put(MIN_RULE, 30);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), HOUR_RULE);
            assertFields(cal, HOUR_RULE, 24, MIN_RULE, 30, null, null);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_toTime_strict_outsideBoundsMins() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 70);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), MIN_RULE);
            assertFields(cal, HOUR_RULE, 11, MIN_RULE, 70, null, null);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_lenient_outsideBoundsHours() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 24)
            .put(MIN_RULE, 30);
        cal.merge(LENIENT_CONTEXT);
        assertFields(cal, null, time(0, 30)); // +P1D
    }

    public void test_merge_toTime_lenient_outsideBoundsMins() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 70);
        cal.merge(LENIENT_CONTEXT);
        assertFields(cal, null, time(12, 10));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_twoPrimarySetsMatch() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30)
            .put(MILLIDAY_RULE, 41400000);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, null, time(11, 30));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_merge_toTime_twoPrimarySetsDiffer() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30)
            .put(MILLIDAY_RULE, 41400001);
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertEquals(cal.getTime(), time(11, 30));
            //assertFields(cal, MILLI_RULE, 41400001, null, time(11, 30));
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_strict_crossCheck_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30)
            .put(AMPM_RULE, 0);  // 11:30 is AM, so this is right
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, null, time(11, 30));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toTime_strict_crossCheck_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30)
            .put(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertFields(cal, AMPM_RULE, 1, null, time(11, 30));
            throw ex;
        }
    }

    public void test_merge_toTime_strict_crossCheck_discardUnused() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30)
            .put(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong, but discarded
        cal.merge(STRICT_DISCARD_UNUSED_CONTEXT);
        assertFields(cal, null, time(11, 30));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_lenient_crossCheck_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 24)
            .put(MIN_RULE, 30)
            .put(AMPM_RULE, 0);  // 00:30 is AM, so this is right
        cal.merge(LENIENT_CONTEXT);
        assertFields(cal, null, time(0, 30)); // +P1D
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toTime_lenient_crossCheck_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 24)
            .put(MIN_RULE, 30)
            .put(AMPM_RULE, 1);  // 00:30 is AM, so this is wrong
        try {
            cal.merge(LENIENT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertFields(cal, AMPM_RULE, 1, null, time(0, 30)); // +P1D
            throw ex;
        }
    }

    public void test_merge_toTime_lenient_crossCheck_discardUnused() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(HOUR_RULE, 24)
            .put(MIN_RULE, 30)
            .put(AMPM_RULE, 1);  // 00:30 is AM, so this is wrong, but ignored
        cal.merge(LENIENT_DISCARD_UNUSED_CONTEXT);
        assertFields(cal, null, time(0, 30)); // +P1D
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_merge_toDateTime_directYMDHM() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30);
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, date(2008, 6, 30), time(11, 30));
    }

    public void test_merge_toDateTime_lenientOverflow() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(HOUR_RULE, 23)
            .put(MIN_RULE, 70);
        cal.merge(LENIENT_CONTEXT);
        assertFields(cal, date(2008, 7, 1), time(0, 10));  // +P1D added
    }

    //-----------------------------------------------------------------------
    public void test_merge_toDateTime_strict_crossCheck_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30)
            .put(AMPM_RULE, 0);  // 11:30 is AM, so this is correct
        cal.merge(STRICT_CONTEXT);
        assertFields(cal, date(2008, 6, 30), time(11, 30));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDateTime_strict_crossCheck_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30)
            .put(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong
        try {
            cal.merge(STRICT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), AMPM_RULE);
            assertFields(cal, AMPM_RULE, 1, date(2008, 6, 30), time(11, 30));
            throw ex;
        }
    }

    public void test_merge_toDateTime_strict_crossCheck_discardUnused() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(HOUR_RULE, 11)
            .put(MIN_RULE, 30)
            .put(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong, but discarded
        cal.merge(STRICT_DISCARD_UNUSED_CONTEXT);
        assertFields(cal, date(2008, 6, 30), time(11, 30));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toDateTime_lenient_crossCheck_valid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(DOW_RULE, 1)  // Monday, correct for 2008-06-30
            .put(HOUR_RULE, 23)
            .put(MIN_RULE, 70)
            .put(AMPM_RULE, 0);  // 00:10 is AM, so this is correct
        cal.merge(LENIENT_CONTEXT);
        assertFields(cal, date(2008, 7, 1), time(0, 10));  // +P1D added
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDateTime_lenient_crossCheck_invalid_daysAdded() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(DOW_RULE, 2)  // Tuesday, correct for end result of 2008-07-01, but check is before days added
            .put(HOUR_RULE, 23)
            .put(MIN_RULE, 70);
        try {
            cal.merge(LENIENT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), DOW_RULE);
            assertFields(cal, DOW_RULE, 2, date(2008, 6, 30), time(0, 10));  // +P1D
            throw ex;
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDateTime_lenient_crossCheck_invalid() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(HOUR_RULE, 23)
            .put(MIN_RULE, 70)
            .put(AMPM_RULE, 1);  // 00:10 is AM, so this is wrong
        try {
            cal.merge(LENIENT_CONTEXT);
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getFieldRule(), AMPM_RULE);
            assertFields(cal, AMPM_RULE, 1, date(2008, 6, 30), time(0, 10));  // +P1D
            throw ex;
        }
    }

    public void test_merge_toDateTime_lenient_crossCheck_discardUnused() {
        Calendrical cal = new Calendrical();
        cal.getFieldMap()
            .put(YEAR_RULE, 2008)
            .put(MOY_RULE, 6)
            .put(DOM_RULE, 30)
            .put(HOUR_RULE, 23)
            .put(MIN_RULE, 70)
            .put(AMPM_RULE, 1);  // 00:10 is AM, so this is wrong, but discarded
        cal.merge(LENIENT_DISCARD_UNUSED_CONTEXT);
        assertFields(cal, date(2008, 7, 1), time(0, 10));  // +P1D added
    }

    //-----------------------------------------------------------------------
    private static void assertFields(
            Calendrical cal,
            LocalDate date, LocalTime time) {
        Map<DateTimeFieldRule, Integer> map = cal.getFieldMap().toFieldValueMap();
        assertEquals(map.size(), 0);
        assertEquals(cal.getDate(), date);
        assertEquals(cal.getTime(), time);
    }
    private static void assertFields(
            Calendrical cal,
            DateTimeFieldRule rule1, int value1,
            LocalDate date, LocalTime time) {
        Map<DateTimeFieldRule, Integer> map = cal.getFieldMap().toFieldValueMap();
        assertEquals(map.size(), 1);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(cal.getDate(), date);
        assertEquals(cal.getTime(), time);
    }
    private static void assertFields(
            Calendrical cal,
            DateTimeFieldRule rule1, int value1,
            DateTimeFieldRule rule2, int value2,
            LocalDate date, LocalTime time) {
        Map<DateTimeFieldRule, Integer> map = cal.getFieldMap().toFieldValueMap();
        assertEquals(map.size(), 2);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(map.get(rule2), Integer.valueOf(value2));
        assertEquals(cal.getDate(), date);
        assertEquals(cal.getTime(), time);
    }
    private static void assertFields(
            Calendrical cal,
            DateTimeFieldRule rule1, int value1,
            DateTimeFieldRule rule2, int value2,
            DateTimeFieldRule rule3, int value3,
            LocalDate date, LocalTime time) {
        Map<DateTimeFieldRule, Integer> map = cal.getFieldMap().toFieldValueMap();
        assertEquals(map.size(), 3);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(map.get(rule2), Integer.valueOf(value2));
        assertEquals(map.get(rule3), Integer.valueOf(value3));
        assertEquals(cal.getDate(), date);
        assertEquals(cal.getTime(), time);
    }
    private static void assertFields(
            Calendrical cal,
            DateTimeFieldRule rule1, int value1,
            DateTimeFieldRule rule2, int value2,
            DateTimeFieldRule rule3, int value3,
            DateTimeFieldRule rule4, int value4,
            LocalDate date, LocalTime time) {
        Map<DateTimeFieldRule, Integer> map = cal.getFieldMap().toFieldValueMap();
        assertEquals(map.size(), 4);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(map.get(rule2), Integer.valueOf(value2));
        assertEquals(map.get(rule3), Integer.valueOf(value3));
        assertEquals(map.get(rule4), Integer.valueOf(value4));
        assertEquals(cal.getDate(), date);
        assertEquals(cal.getTime(), time);
    }
    private static void assertFields(
            Calendrical cal,
            DateTimeFieldRule rule1, int value1,
            DateTimeFieldRule rule2, int value2,
            DateTimeFieldRule rule3, int value3,
            DateTimeFieldRule rule4, int value4,
            DateTimeFieldRule rule5, int value5,
            LocalDate date, LocalTime time) {
        Map<DateTimeFieldRule, Integer> map = cal.getFieldMap().toFieldValueMap();
        assertEquals(map.size(), 5);
        assertEquals(map.get(rule1), Integer.valueOf(value1));
        assertEquals(map.get(rule2), Integer.valueOf(value2));
        assertEquals(map.get(rule3), Integer.valueOf(value3));
        assertEquals(map.get(rule4), Integer.valueOf(value4));
        assertEquals(map.get(rule5), Integer.valueOf(value5));
        assertEquals(cal.getDate(), date);
        assertEquals(cal.getTime(), time);
    }
    private static void assertFieldValue(Integer actual, int expected) {
        Assert.assertEquals(actual, (Integer) expected); 
    }
    private static void dumpException(Exception ex) {
        // this is used to allow a human to inspect the error messages to see if they are understandable
        System.out.println(ex.getMessage());
    }
}
