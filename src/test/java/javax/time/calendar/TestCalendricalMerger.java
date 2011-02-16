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
import static javax.time.calendar.ISODateTimeRule.CLOCK_HOUR_OF_AMPM;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_AMPM;
import static javax.time.calendar.ISODateTimeRule.YEAR;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

/**
 * Test CalendricalMerger.
 */
@Test
public class TestCalendricalMerger {

    private static final DateTimeRule NULL_RULE = null;

    private static final CalendricalContext STRICT_CONTEXT = new CalendricalContext(true, true);
//    private static final CalendricalContext STRICT_DISCARD_UNUSED_CONTEXT = new CalendricalContext(true, false);
//    private static final CalendricalContext LENIENT_CONTEXT = new CalendricalContext(false, true);
//    private static final CalendricalContext LENIENT_DISCARD_UNUSED_CONTEXT = new CalendricalContext(false, false);

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------

    //-----------------------------------------------------------------------
    // getValue()
    //-----------------------------------------------------------------------
    public void test_getValue() {
        DateTimeRule rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                assertEquals(merger.getValue(YEAR), YEAR.field(2008));
                assertEquals(merger.getValue(this), field(20));
            }
        };
        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValue_null() {
        DateTimeRule rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.getValue(NULL_RULE);
            }
        };
        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
    }

    public void test_getValue_fieldNotPresent() {
        DateTimeRule rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                assertEquals(merger.getValue(DAY_OF_MONTH), null);
            }
        };
        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
    }

//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_getValue_strictInvalidValue() {
//        DateTimeFieldRule rule = new MockFieldRule() {
//            private static final long serialVersionUID = 1L;
//            @Override
//            protected void merge(CalendricalMerger merger) {
//                fail();
//            }
//        };
//        CalendricalMerger m = createMerger(MONTH_OF_YEAR, -1, rule, 20, STRICT_CONTEXT);
//        try {
//            m.merge();
//        } catch (IllegalCalendarFieldValueException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), MONTH_OF_YEAR);
//            throw ex;
//        }
//    }
//
//    public void test_getValue_lenientInvalidValue() {
//        DateTimeFieldRule rule = new MockFieldRule() {
//            private static final long serialVersionUID = 1L;
//            @Override
//            protected void merge(CalendricalMerger merger) {
//                assertEquals(merger.getValue(MONTH_OF_YEAR), -1);
//            }
//        };
//        CalendricalMerger m = createMerger(MONTH_OF_YEAR, -1, rule, 20, STRICT_CONTEXT);
//        m.merge();
//    }

//    //-----------------------------------------------------------------------
//    // removeProcessed()
//    //-----------------------------------------------------------------------
//    public void test_markFieldAsProcessed() {
//        DateTimeFieldRule rule = new MockFieldRule() {
//            @Override
//            protected void merge(CalendricalMerger merger) {
//                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
//                merger.removeProcessed(YEAR);
//                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR)));
//            }
//        };
//        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
//        m.merge();
//    }
//
//    public void test_removeProcessed_set() {
//        DateTimeFieldRule rule = new MockFieldRule() {
//            @Override
//            protected void merge(CalendricalMerger merger) {
//                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
//                merger.removeProcessed(YEAR);
//                merger.removeProcessed(YEAR);
//                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR)));
//            }
//        };
//        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
//        m.merge();
//    }
//
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_removeProcessed_null() {
//        DateTimeFieldRule rule = new MockFieldRule() {
//            @Override
//            protected void merge(CalendricalMerger merger) {
//                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
//                try {
//                    merger.removeProcessed(NULL_RULE);
//                } catch (NullPointerException ex) {
//                    assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
//                    throw ex;
//                }
//            }
//        };
//        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
//        m.merge();
//        
//    }
//
//    @SuppressWarnings("unchecked")
//    private Set<DateTimeFieldRule> getProcessedFieldSet(CalendricalMerger test) {
//        try {
//            Field field = CalendricalMerger.class.getDeclaredField("processedFieldSet");
//            field.setAccessible(true);
//            return (Set<DateTimeFieldRule>) field.get(test);
//        } catch (Exception ex) {
//            fail(ex.toString());
//            return null;
//        }
//    }

    //-----------------------------------------------------------------------
    // storeMergedField()
    //-----------------------------------------------------------------------
    public void test_storeMergedField() {
        DateTimeRule rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMergedField(DAY_OF_MONTH, 30);
            }
        };
        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
        assertMerged(m, null, YEAR, YEAR.field(2008), rule, rule.field(20), DAY_OF_MONTH, DAY_OF_MONTH.field(30));
    }

    public void test_storeMergedField_invalidValueOK() {
        DateTimeRule rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMergedField(DAY_OF_MONTH, -1);
            }
        };
        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
        assertMerged(m, null, YEAR, YEAR.field(2008), rule, rule.field(20), DAY_OF_MONTH, DAY_OF_MONTH.field(-1));
    }

    public void test_storeMergedField_sameField_sameValue() {
        DateTimeRule rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMergedField(YEAR, 2008);
            }
        };
        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
        assertMerged(m, null, YEAR, YEAR.field(2008), rule, rule.field(20));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_storeMergedField_sameField_differentValue() {
        DateTimeRule rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMergedField(YEAR, 2007);
            }
        };
        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(YEAR, ex.getRule());
            assertMerged(m, null, YEAR, YEAR.field(2008), rule, rule.field(20));
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedField_null() {
        DateTimeRule rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMergedField(null, 30);
            }
        };
        CalendricalMerger m = createMerger(YEAR, 2008, rule, 20, STRICT_CONTEXT);
        try {
            m.merge();
        } catch (NullPointerException ex) {
            assertMerged(m, null, YEAR, YEAR.field(2008), rule, rule.field(20));
            throw ex;
        }
    }

    private CalendricalMerger createMerger(
            CalendricalRule<?> rule1, long value1,
            CalendricalRule<?> rule2, long value2, CalendricalContext context) {
        Map<CalendricalRule<?>, Object> map = new HashMap<CalendricalRule<?>, Object>();
        map.put(rule1, value1);
        map.put(rule2, value2);
        return new CalendricalMerger(context, map);
    }

    //-----------------------------------------------------------------------
    // merge()
    //-----------------------------------------------------------------------
    public void test_merge() {
        CalendricalMerger m = createMerger(AMPM_OF_DAY, 1, HOUR_OF_AMPM, 9, STRICT_CONTEXT);  // 9pm
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(21, 00));  // merged to 21:00
    }

    public void test_merge_clockHour() {
        CalendricalMerger m = createMerger(AMPM_OF_DAY, 1, CLOCK_HOUR_OF_AMPM, 9, STRICT_CONTEXT);  // 9pm
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(21, 00));  // merged to 24:00
    }

    public void test_merge_clockHourEndOfDay() {
        CalendricalMerger m = createMerger(AMPM_OF_DAY, 1, CLOCK_HOUR_OF_AMPM, 12, STRICT_CONTEXT);  // 12am end-of-day
        m.merge();
        assertMerged(m, Period.ofDays(1), LocalTime.rule(), time(0, 00));  // merged to 24:00
    }

//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_merge_strict() {
//        CalendricalMerger m = createMerger(AMPM_OF_DAY, 1, HOUR_OF_AMPM, 14, STRICT_CONTEXT);  // 14pm
//        try {
//            m.merge();
//        } catch (IllegalCalendarFieldValueException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), HOUR_OF_AMPM);
//            throw ex;
//        }
//    }

//    public void test_merge_lenient() {
//        CalendricalMerger m = createMerger(AMPM_OF_DAY, 1, HOUR_OF_AMPM, 14, LENIENT_CONTEXT);  // 14pm
//        m.merge();
//        assertMerged(m, Period.days(1), LocalTime.rule(), time(2, 00));  // merged to 02:00 + 1day (26:00)
//    }
//
//    public void test_merge_empty() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.merge();
//        assertEquals(m.get(YEAR), null);
//    }
//
//    public void test_merge_doubleMerge() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(AMPM_OF_DAY, 1);
//        m.getInputMap().put(HOUR_OF_AMPM, 9);  // 9pm -> 21:00
//        m.getInputMap().put(QOY_RULE, 2);
//        m.getInputMap().put(MOQ_RULE, 3);  // Q2M3 -> June
//        m.merge();
//        assertMerged(m, null, MONTH_OF_YEAR, MonthOfYear.JUNE, LocalTime.rule(), time(21, 0));  // merged to 21:00 and June
//    }
//
//    public void test_merge_nothingToMerge() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MOQ_RULE, 1);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.merge();
//        assertMerged(m, null, YEAR, 2008, MOQ_RULE, 1, DAY_OF_MONTH, 30);
//    }
//
//    public void test_merge_otherFieldsUntouched() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(QOY_RULE, 3);
//        m.getInputMap().put(MOQ_RULE, 2);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.merge();
//        assertMerged(m, null, MONTH_OF_YEAR, MonthOfYear.AUGUST, MINUTE_OF_HOUR, 30);  // merged to August
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_inconsistentValue() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(AMPM_OF_DAY, 0);
//        m.getInputMap().put(HOUR_OF_AMPM, 9);  // 9am
//        m.getInputMap().put(HOUR_OF_DAY, 10);  // 10am
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), HOUR_OF_DAY);
//            assertMerged(m, null, AMPM_OF_DAY, AmPmOfDay.AM, HOUR_OF_AMPM, 9, HOUR_OF_DAY, 10);
//            throw ex;
//        }
//    }
//
//    public void test_merge_multiLevelMerge_simple() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);
//        m.getInputMap().put(MockYearOfDecadeFieldRule.INSTANCE, 2);  // 7 + 2 -> 72
//        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
//        m.merge();
//        assertMerged(m, null, YEAR, 1972);  // merged to 1972
//    }
//
//    public void test_merge_multiLevelMerge_fullUpToDate() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);
//        m.getInputMap().put(MockYearOfDecadeFieldRule.INSTANCE, 2);  // 7 + 2 -> 72
//        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
//        m.getInputMap().put(QOY_RULE, 4);
//        m.getInputMap().put(MOQ_RULE, 3);  // M3 + Q4 -> December
//        m.getInputMap().put(DAY_OF_MONTH, 3);
//        m.merge();
//        assertMerged(m, null, LocalDate.rule(), date(1972, 12, 3));  // merged to 1972-12-03
//    }
//
//    public void test_merge_multiLevelMerge_crossCheck_valid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);
//        m.getInputMap().put(MockYearOfDecadeFieldRule.INSTANCE, 2);  // 7 + 2 -> 72
//        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);  // cross check against year of century
//        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
//        m.getInputMap().put(YEAR, 1972);  // cross check against year
//        m.merge();
//        assertMerged(m, null, YEAR, 1972);  // merged to 1972
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_multiLevelMerge_crossCheck_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);
//        m.getInputMap().put(MockYearOfDecadeFieldRule.INSTANCE, 2);  // 7 + 2 -> 72
//        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 71);  // cross check against year of century
//        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
//        m.getInputMap().put(YEAR, 1972);  // cross check against year
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            assertEquals(ex.getRule(), YEAR);
//            assertMerged(m, null, MockDecadeOfCenturyFieldRule.INSTANCE, 7, MockYearOfDecadeFieldRule.INSTANCE, 2,
//                    MockYearOfCenturyFieldRule.INSTANCE, 71, MockCenturyFieldRule.INSTANCE, 19, YEAR, 1972);
//            throw ex;
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_singleLevel_derivableRemoved_valid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);  // derivable from YearOfCentury
//        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
//        m.merge();
//        assertMerged(m, null, MockYearOfCenturyFieldRule.INSTANCE, 72);
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_singleLevel_derivableChecked_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from YearOfCentury, value is wrong
//        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
//            assertMerged(m, null, MockDecadeOfCenturyFieldRule.INSTANCE, 6, MockYearOfCenturyFieldRule.INSTANCE, 72);
//            throw ex;
//        }
//    }
//
//    public void test_merge_singleLevel_derivableDiscardUnused_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from YearOfCentury, value is wrong
//        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
//        m.merge();
//        assertMerged(m, null, MockYearOfCenturyFieldRule.INSTANCE, 72);
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_multiLevel_simple_derivableRemoved_valid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);  // derivable from Year
//        m.getInputMap().put(YEAR, 1972);
//        m.merge();
//        assertMerged(m, null, YEAR, 1972);
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_multiLevel_simple_derivableChecked_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from Year, value is wrong
//        m.getInputMap().put(YEAR, 1972);
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
//            assertMerged(m,  null, MockDecadeOfCenturyFieldRule.INSTANCE, 6, YEAR, 1972);
//            throw ex;
//        }
//    }
//
//    public void test_merge_multiLevel_simple_derivableDiscardUnused_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from Year, value is wrong
//        m.getInputMap().put(YEAR, 1972);
//        m.merge();
//        assertMerged(m,  null, YEAR, 1972);
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_multiLevel_complex_derivableRemoved_valid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);  // derivable from Year
//        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
//        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
//        m.merge();
//        assertMerged(m,  null, YEAR, 1972);
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_multiLevel_complex_derivableChecked_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from Year, value is wrong
//        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
//        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
//            assertMerged(m, null, MockDecadeOfCenturyFieldRule.INSTANCE, 6, YEAR, 1972);
//            throw ex;
//        }
//    }
//
//    public void test_merge_multiLevel_complex_derivableDiscardUnused_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
//        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from Year, value is wrong
//        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
//        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
//        m.merge();
//        assertMerged(m, null, YEAR, 1972);
//    }
//
//    //-----------------------------------------------------------------------
//    //-----------------------------------------------------------------------
//    public void test_merge_toDate_directDMY() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.merge();
//        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
//    }
//
//    public void test_merge_toDate_directDY() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(DOY_RULE, 182);
//        m.merge();
//        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
//    }
//
//    public void test_merge_toDate_mergeFieldsThenDMY() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(QOY_RULE, 2);
//        m.getInputMap().put(MOQ_RULE, 3);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.merge();
//        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
//    }
//
//    //-----------------------------------------------------------------------
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_toDate_direct_strictInvalidWithinBounds() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 31);
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), DAY_OF_MONTH);
//            assertMerged(m, null, YEAR, 2008, MONTH_OF_YEAR, MonthOfYear.JUNE, DAY_OF_MONTH, 31);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_merge_toDate_direct_strictInvalidOutsideBounds() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 32);
//        try {
//            m.merge();
//        } catch (IllegalCalendarFieldValueException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), DAY_OF_MONTH);
//            assertMerged(m, null, YEAR, 2008, MONTH_OF_YEAR, MonthOfYear.JUNE, DAY_OF_MONTH, 32);
//            throw ex;
//        }
//    }
//
//    public void test_merge_toDate_direct_lenientInvalidWithinBounds() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 31);
//        m.merge();
//        assertMerged(m, null, LocalDate.rule(), date(2008, 7, 1));
//    }
//
//    public void test_merge_toDate_direct_lenientInvalidOutsideBounds() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 32);
//        m.merge();
//        assertMerged(m, null, LocalDate.rule(), date(2008, 7, 2));
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_toDate_twoPrimarySetsMatch() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(DOY_RULE, 182);
//        m.merge();
//        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
//    }
//
//    @Test(expectedExceptions=CalendricalException.class)
//    public void test_merge_toDate_twoPrimarySetsDiffer() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(DOY_RULE, 183);
//        try {
//            m.merge();
//        } catch (CalendricalException ex) {
//            dumpException(ex);
//            assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30), DOY_RULE, 183);
//            throw ex;
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_mergeToDate_strict_crossCheck_valid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(DOW_RULE, 1);   // 2008-06-30 is a Monday, so this is right
//        m.getInputMap().put(QOY_RULE, 2);   // 2008-06-30 is Q2, so this is right
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);  // ignored
//        m.merge();
//        assertMerged(m, null, MINUTE_OF_HOUR, 30, LocalDate.rule(), date(2008, 6, 30));
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_mergeToDate_strict_crossCheck_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), DOW_RULE);
//            assertMerged(m, null, DOW_RULE, DayOfWeek.TUESDAY, LocalDate.rule(), date(2008, 6, 30));
//            throw ex;
//        }
//    }
//
//    public void test_mergeToDate_strict_crossCheck_discardUnused() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong, but discarded
//        m.merge();
//        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_mergeToDate_lenient_crossCheck_valid() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 32);
//        m.getInputMap().put(DOW_RULE, 3);   // 2008-07-02 is a Wednesday, so this is right
//        m.getInputMap().put(QOY_RULE, 3);   // 2008-07-02 is Q3, so this is right
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);  // ignored
//        m.merge();
//        assertMerged(m, null, MINUTE_OF_HOUR, 30, LocalDate.rule(), date(2008, 7, 2));
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_mergeToDate_lenient_crossCheck_invalid() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 32);
//        m.getInputMap().put(DOW_RULE, 2);  // 2008-07-02 is a Wednesday, so this is wrong
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), DOW_RULE);
//            assertMerged(m, null, DOW_RULE, DayOfWeek.TUESDAY, LocalDate.rule(), date(2008, 7, 2));
//            throw ex;
//        }
//    }
//
//    public void test_mergeToDate_lenient_crossCheck_discardUnused() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_DISCARD_UNUSED_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 32);
//        m.getInputMap().put(DOW_RULE, 2);  // 2008-07-02 is a Wednesday, so this is wrong, but discarded
//        m.merge();
//        assertMerged(m, null, LocalDate.rule(), date(2008, 7, 2));
//    }
//
//    //-----------------------------------------------------------------------
//    //-----------------------------------------------------------------------
//    public void test_merge_toTime_directHM() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.merge();
//        assertMerged(m, null, LocalTime.rule(), time(11, 30));
//    }
//
//    public void test_merge_toTime_fieldsThenHM() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_AMPM, 2);
//        m.getInputMap().put(AMPM_OF_DAY, 1);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.merge();
//        assertMerged(m, null, LocalTime.rule(), time(14, 30));
//    }
//
//    public void test_merge_toTime_fieldsThenHMSN() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 14);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(SEC_RULE, 50);
//        m.getInputMap().put(MILLISEC_RULE, 1);  // must be merged to nanos before merge to LocalTime
//        m.merge();
//        assertMerged(m, null, LocalTime.rule(), time(14, 30, 50, 1000000));
//    }
//
//    //-----------------------------------------------------------------------
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_merge_toTime_strict_outsideBoundsHours() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 24);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), HOUR_OF_DAY);
//            assertMerged(m, null, HOUR_OF_DAY, 24, MINUTE_OF_HOUR, 30);
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void test_merge_toTime_strict_outsideBoundsMins() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 70);
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), MINUTE_OF_HOUR);
//            assertMerged(m, null, HOUR_OF_DAY, 11, MINUTE_OF_HOUR, 70);
//            throw ex;
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_toTime_lenient_outsideBoundsHours() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 24);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.merge();
//        assertMerged(m, Period.days(1), LocalTime.rule(), time(0, 30)); // +P1D
//    }
//
//    public void test_merge_toTime_lenient_outsideBoundsMins() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 70);
//        m.merge();
//        assertMerged(m, null, LocalTime.rule(), time(12, 10));
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_toTime_twoPrimarySetsMatch() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(MILLIDAY_RULE, 41400000);
//        m.merge();
//        assertMerged(m, null, LocalTime.rule(), time(11, 30));
//    }
//
//    @Test(expectedExceptions=CalendricalException.class)
//    public void test_merge_toTime_twoPrimarySetsDiffer() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(MILLIDAY_RULE, 41400001);
//        try {
//            m.merge();
//        } catch (CalendricalException ex) {
//            dumpException(ex);
//            assertMerged(m, null, LocalTime.rule(), time(11, 30), MILLIDAY_RULE, 41400001);
//            throw ex;
//        }
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_toTime_strict_crossCheck_valid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(AMPM_OF_DAY, 0);  // 11:30 is AM, so this is right
//        m.merge();
//        assertMerged(m, null, LocalTime.rule(), time(11, 30));
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_toTime_strict_crossCheck_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(AMPM_OF_DAY, 1);  // 11:30 is AM, so this is wrong
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertMerged(m, null, AMPM_OF_DAY, AmPmOfDay.PM, LocalTime.rule(), time(11, 30));
//            throw ex;
//        }
//    }
//
//    public void test_merge_toTime_strict_crossCheck_discardUnused() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(AMPM_OF_DAY, 1);  // 11:30 is AM, so this is wrong, but discarded
//        m.merge();
//        assertMerged(m, null, LocalTime.rule(), time(11, 30));
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_toTime_lenient_crossCheck_valid() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 24);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(AMPM_OF_DAY, 0);  // 00:30 is AM, so this is right
//        m.merge();
//        assertMerged(m, Period.days(1), LocalTime.rule(), time(0, 30)); // +P1D
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_toTime_lenient_crossCheck_invalid() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 24);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(AMPM_OF_DAY, 1);  // 00:30 is AM, so this is wrong
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertMerged(m, Period.days(1), AMPM_OF_DAY, AmPmOfDay.PM, LocalTime.rule(), time(0, 30)); // +P1D
//            throw ex;
//        }
//    }
//
//    public void test_merge_toTime_lenient_crossCheck_discardUnused() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_DISCARD_UNUSED_CONTEXT);
//        m.getInputMap().put(HOUR_OF_DAY, 24);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(AMPM_OF_DAY, 1);  // 00:30 is AM, so this is wrong, but ignored
//        m.merge();
//        assertMerged(m, Period.days(1), LocalTime.rule(), time(0, 30)); // +P1D
//    }
//
//    //-----------------------------------------------------------------------
//    //-----------------------------------------------------------------------
//    public void test_merge_toDateTime_directYMDHM() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.merge();
//        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 6, 30, 11, 30));
//    }
//
//    public void test_merge_toDateTime_lenientOverflow() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(HOUR_OF_DAY, 23);
//        m.getInputMap().put(MINUTE_OF_HOUR, 70);
//        m.merge();
//        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 7, 1, 0, 10));  // +P1D added
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_toDateTime_strict_crossCheck_valid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(AMPM_OF_DAY, 0);  // 11:30 is AM, so this is correct
//        m.merge();
//        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 6, 30, 11, 30));
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_toDateTime_strict_crossCheck_invalid() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(AMPM_OF_DAY, 1);  // 11:30 is AM, so this is wrong
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), AMPM_OF_DAY);
//            assertMerged(m, null, AMPM_OF_DAY, AmPmOfDay.PM, LocalDateTime.rule(), dateTime(2008, 6, 30, 11, 30));
//            throw ex;
//        }
//    }
//
//    public void test_merge_toDateTime_strict_crossCheck_discardUnused() {
//        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(HOUR_OF_DAY, 11);
//        m.getInputMap().put(MINUTE_OF_HOUR, 30);
//        m.getInputMap().put(AMPM_OF_DAY, 1);  // 11:30 is AM, so this is wrong, but discarded
//        m.merge();
//        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 6, 30, 11, 30));
//    }
//
//    //-----------------------------------------------------------------------
//    public void test_merge_toDateTime_lenient_crossCheck_valid() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(DOW_RULE, 1);  // Monday, correct for 2008-06-30
//        m.getInputMap().put(HOUR_OF_DAY, 23);
//        m.getInputMap().put(MINUTE_OF_HOUR, 70);
//        m.getInputMap().put(AMPM_OF_DAY, 0);  // 00:10 is AM, so this is correct
//        m.merge();
//        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 7, 1, 0, 10));  // +P1D added
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_toDateTime_lenient_crossCheck_invalid_daysAdded() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(DOW_RULE, 2);  // Tuesday, correct for end result of 2008-07-01, but check is before days added
//        m.getInputMap().put(HOUR_OF_DAY, 23);
//        m.getInputMap().put(MINUTE_OF_HOUR, 70);
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), DOW_RULE);
//            assertMerged(m, null, DOW_RULE, DayOfWeek.TUESDAY, LocalDateTime.rule(), dateTime(2008, 6, 30, 0, 10));  // +P1D
//            throw ex;
//        }
//    }
//
//    @Test(expectedExceptions=InvalidCalendarFieldException.class)
//    public void test_merge_toDateTime_lenient_crossCheck_invalid() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(HOUR_OF_DAY, 23);
//        m.getInputMap().put(MINUTE_OF_HOUR, 70);
//        m.getInputMap().put(AMPM_OF_DAY, 1);  // 00:10 is AM, so this is wrong
//        try {
//            m.merge();
//        } catch (InvalidCalendarFieldException ex) {
//            dumpException(ex);
//            assertEquals(ex.getRule(), AMPM_OF_DAY);
//            assertMerged(m, null, AMPM_OF_DAY, AmPmOfDay.PM, LocalDateTime.rule(), dateTime(2008, 6, 30, 0, 10));  // +P1D
//            throw ex;
//        }
//    }
//
//    public void test_merge_toDateTime_lenient_crossCheck_discardUnused() {
//        CalendricalMerger m = new CalendricalMerger(LENIENT_DISCARD_UNUSED_CONTEXT);
//        m.getInputMap().put(YEAR, 2008);
//        m.getInputMap().put(MONTH_OF_YEAR, 6);
//        m.getInputMap().put(DAY_OF_MONTH, 30);
//        m.getInputMap().put(HOUR_OF_DAY, 23);
//        m.getInputMap().put(MINUTE_OF_HOUR, 70);
//        m.getInputMap().put(AMPM_OF_DAY, 1);  // 00:10 is AM, so this is wrong, but discarded
//        m.merge();
//        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 7, 1, 0, 10));  // +P1D added
//    }
//
//    //-----------------------------------------------------------------------
    private static <T> void assertMerged(
            CalendricalMerger m, Period overflow,
            CalendricalRule<T> rule1, T value1) {
        if (overflow != null) {
            assertEquals(m.getOverflow(), overflow);
        } else {
            assertEquals(m.getOverflow(), Period.ZERO);
        }
        assertEquals(m.getValue(rule1), value1);
    }
    private static <T, U> void assertMerged(
            CalendricalMerger m, Period overflow,
            CalendricalRule<T> rule1, T value1,
            CalendricalRule<U> rule2, U value2) {
        if (overflow != null) {
            assertEquals(m.getOverflow(), overflow);
        } else {
            assertEquals(m.getOverflow(), Period.ZERO);
        }
        assertEquals(m.getValue(rule1), value1);
        assertEquals(m.getValue(rule2), value2);
    }
    private static <T, U, V> void assertMerged(
            CalendricalMerger m, Period overflow,
            CalendricalRule<T> rule1, T value1,
            CalendricalRule<U> rule2, U value2,
            CalendricalRule<V> rule3, V value3) {
        if (overflow != null) {
            assertEquals(m.getOverflow(), overflow);
        } else {
            assertEquals(m.getOverflow(), Period.ZERO);
        }
        assertEquals(m.getValue(rule1), value1);
        assertEquals(m.getValue(rule2), value2);
        assertEquals(m.getValue(rule3), value3);
    }
//    private static <T, U, V, W, X> void assertMerged(
//            CalendricalMerger m, Period overflow,
//            CalendricalRule<T> rule1, T value1,
//            CalendricalRule<U> rule2, U value2,
//            CalendricalRule<V> rule3, V value3,
//            CalendricalRule<W> rule4, W value4,
//            CalendricalRule<X> rule5, W value5) {
//        if (overflow != null) {
//            assertEquals(m.getOverflow(), overflow);
//        } else {
//            assertEquals(m.getOverflow(), Period.ZERO);
//        }
//        assertEquals(m.getValue(rule1), value1);
//        assertEquals(m.getValue(rule2), value2);
//        assertEquals(m.getValue(rule3), value3);
//        assertEquals(m.getValue(rule4), value4);
//        assertEquals(m.getValue(rule5), value5);
//    }
    private static void dumpException(Exception ex) {
        // this is used to allow a human to inspect the error messages to see if they are understandable
        System.out.println(ex.getMessage());
    }

//    private LocalDate date(int year, int month, int day) {
//        return LocalDate.of(year, month, day);
//    }
//
    private LocalTime time(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

//    private LocalTime time(int hour, int minute, int second, int nano) {
//        return LocalTime.of(hour, minute, second, nano);
//    }
//
//    private LocalDateTime dateTime(int year, int month, int day, int hour, int minute) {
//        return LocalDateTime.of(year, month, day, hour, minute);
//    }

}
