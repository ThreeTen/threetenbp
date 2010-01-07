/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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
import static org.testng.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import javax.time.CalendricalException;
import javax.time.calendar.field.AmPmOfDay;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.QuarterOfYear;
import javax.time.period.Period;

import org.testng.annotations.Test;

/**
 * Test CalendricalMerger.
 */
@Test
public class TestCalendricalMerger {

    private static final DateTimeFieldRule<?> NULL_RULE = null;
    private static final DateTimeFieldRule<Integer> YEAR_RULE = ISOChronology.yearRule();
    private static final DateTimeFieldRule<MonthOfYear> MOY_RULE = ISOChronology.monthOfYearRule();
    private static final DateTimeFieldRule<Integer> DOM_RULE = ISOChronology.dayOfMonthRule();
    private static final DateTimeFieldRule<Integer> DOY_RULE = ISOChronology.dayOfYearRule();
    private static final DateTimeFieldRule<DayOfWeek> DOW_RULE = ISOChronology.dayOfWeekRule();
    private static final DateTimeFieldRule<QuarterOfYear> QOY_RULE = ISOChronology.quarterOfYearRule();
    private static final DateTimeFieldRule<Integer> MOQ_RULE = ISOChronology.monthOfQuarterRule();
    private static final DateTimeFieldRule<Integer> HOUR_RULE = ISOChronology.hourOfDayRule();
    private static final DateTimeFieldRule<AmPmOfDay> AMPM_RULE = ISOChronology.amPmOfDayRule();
    private static final DateTimeFieldRule<Integer> HOUR_AMPM_RULE = ISOChronology.hourOfAmPmRule();
    private static final DateTimeFieldRule<Integer> MIN_RULE = ISOChronology.minuteOfHourRule();
    private static final DateTimeFieldRule<Integer> SEC_RULE = ISOChronology.secondOfMinuteRule();
    private static final DateTimeFieldRule<Integer> MILLISEC_RULE = ISOChronology.milliOfSecondRule();
    private static final DateTimeFieldRule<Integer> MILLIDAY_RULE = ISOChronology.milliOfDayRule();

    private static final CalendricalContext STRICT_CONTEXT = new CalendricalContext(true, true);
    private static final CalendricalContext STRICT_DISCARD_UNUSED_CONTEXT = new CalendricalContext(true, false);
    private static final CalendricalContext LENIENT_CONTEXT = new CalendricalContext(false, true);
    private static final CalendricalContext LENIENT_DISCARD_UNUSED_CONTEXT = new CalendricalContext(false, false);

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------

    //-----------------------------------------------------------------------
    // getValue()
    //-----------------------------------------------------------------------
    public void test_getValue() {
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                assertEquals(merger.getValue(YEAR_RULE), (Integer) 2008);
                assertEquals(merger.getValue(this), (Integer) 20);
            }
        };
        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getValue_null() {
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.getValue(NULL_RULE);
            }
        };
        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
    }

    public void test_getValue_fieldNotPresent() {
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                assertEquals(merger.getValue(DOM_RULE), null);
            }
        };
        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_getValue_strictInvalidValue() {
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                fail();
            }
        };
        CalendricalMerger m = createMerger(MOY_RULE, -1, rule, 20, STRICT_CONTEXT);
        try {
            m.merge();
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), MOY_RULE);
            throw ex;
        }
    }

    public void test_getValue_lenientInvalidValue() {
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                assertEquals(merger.getValue(MOY_RULE), -1);
            }
        };
        CalendricalMerger m = createMerger(MOY_RULE, -1, rule, 20, STRICT_CONTEXT);
        m.merge();
    }

//    //-----------------------------------------------------------------------
//    // removeProcessed()
//    //-----------------------------------------------------------------------
//    public void test_markFieldAsProcessed() {
//        DateTimeFieldRule rule = new MockFieldRule() {
//            @Override
//            protected void merge(CalendricalMerger merger) {
//                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
//                merger.removeProcessed(YEAR_RULE);
//                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR_RULE)));
//            }
//        };
//        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
//        m.merge();
//    }
//
//    public void test_removeProcessed_set() {
//        DateTimeFieldRule rule = new MockFieldRule() {
//            @Override
//            protected void merge(CalendricalMerger merger) {
//                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>());
//                merger.removeProcessed(YEAR_RULE);
//                merger.removeProcessed(YEAR_RULE);
//                assertEquals(getProcessedFieldSet(merger), new HashSet<DateTimeFieldRule>(Arrays.asList(YEAR_RULE)));
//            }
//        };
//        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
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
//        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
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
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMerged(DOM_RULE, 30);
            }
        };
        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
        assertMerged(m, null, YEAR_RULE, 2008, rule, 20, DOM_RULE, 30);
    }

    public void test_storeMergedField_invalidValueOK() {
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMerged(DOM_RULE, -1);
            }
        };
        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
        assertMerged(m, null, YEAR_RULE, 2008, rule, 20, DOM_RULE, -1);
    }

    public void test_storeMergedField_sameField_sameValue() {
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMerged(YEAR_RULE, 2008);
            }
        };
        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
        m.merge();
        assertMerged(m, null, YEAR_RULE, 2008, rule, 20);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_storeMergedField_sameField_differentValue() {
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMerged(YEAR_RULE, 2007);
            }
        };
        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(YEAR_RULE, ex.getRule());
            assertMerged(m, null, YEAR_RULE, 2008, rule, 20);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_storeMergedField_null() {
        DateTimeFieldRule<Integer> rule = new MockFieldRule() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void merge(CalendricalMerger merger) {
                merger.storeMerged(null, 30);
            }
        };
        CalendricalMerger m = createMerger(YEAR_RULE, 2008, rule, 20, STRICT_CONTEXT);
        try {
            m.merge();
        } catch (NullPointerException ex) {
            assertMerged(m, null, YEAR_RULE, 2008, rule, 20);
            throw ex;
        }
    }

    private CalendricalMerger createMerger(
            CalendricalRule<?> rule1, int value1,
            CalendricalRule<?> rule2, int value2, CalendricalContext context) {
        Map<CalendricalRule<?>, Object> map = new HashMap<CalendricalRule<?>, Object>();
        map.put(rule1, value1);
        map.put(rule2, value2);
        return new CalendricalMerger(context, map);
    }

    //-----------------------------------------------------------------------
    // merge()
    //-----------------------------------------------------------------------
    public void test_merge() {
        CalendricalMerger m = createMerger(AMPM_RULE, 1, HOUR_AMPM_RULE, 9, STRICT_CONTEXT);  // 9pm
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(21, 00));  // merged to 21:00
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_strict() {
        CalendricalMerger m = createMerger(AMPM_RULE, 1, HOUR_AMPM_RULE, 14, STRICT_CONTEXT);  // 14pm
        try {
            m.merge();
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), HOUR_AMPM_RULE);
            throw ex;
        }
    }

    public void test_merge_lenient() {
        CalendricalMerger m = createMerger(AMPM_RULE, 1, HOUR_AMPM_RULE, 14, LENIENT_CONTEXT);  // 14pm
        m.merge();
        assertMerged(m, Period.days(1), LocalTime.rule(), time(2, 00));  // merged to 02:00 + 1day (26:00)
    }

    public void test_merge_empty() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.merge();
        assertEquals(m.get(YEAR_RULE), null);
    }

    public void test_merge_doubleMerge() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(AMPM_RULE, 1);
        m.getInputMap().put(HOUR_AMPM_RULE, 9);  // 9pm -> 21:00
        m.getInputMap().put(QOY_RULE, 2);
        m.getInputMap().put(MOQ_RULE, 3);  // Q2M3 -> June
        m.merge();
        assertMerged(m, null, MOY_RULE, MonthOfYear.JUNE, LocalTime.rule(), time(21, 0));  // merged to 21:00 and June
    }

    public void test_merge_nothingToMerge() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOQ_RULE, 1);
        m.getInputMap().put(DOM_RULE, 30);
        m.merge();
        assertMerged(m, null, YEAR_RULE, 2008, MOQ_RULE, 1, DOM_RULE, 30);
    }

    public void test_merge_otherFieldsUntouched() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(QOY_RULE, 3);
        m.getInputMap().put(MOQ_RULE, 2);
        m.getInputMap().put(MIN_RULE, 30);
        m.merge();
        assertMerged(m, null, MOY_RULE, MonthOfYear.AUGUST, MIN_RULE, 30);  // merged to August
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_inconsistentValue() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(AMPM_RULE, 0);
        m.getInputMap().put(HOUR_AMPM_RULE, 9);  // 9am
        m.getInputMap().put(HOUR_RULE, 10);  // 10am
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), HOUR_RULE);
            assertMerged(m, null, AMPM_RULE, AmPmOfDay.AM, HOUR_AMPM_RULE, 9, HOUR_RULE, 10);
            throw ex;
        }
    }

    public void test_merge_multiLevelMerge_simple() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);
        m.getInputMap().put(MockYearOfDecadeFieldRule.INSTANCE, 2);  // 7 + 2 -> 72
        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        m.merge();
        assertMerged(m, null, YEAR_RULE, 1972);  // merged to 1972
    }

    public void test_merge_multiLevelMerge_fullUpToDate() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);
        m.getInputMap().put(MockYearOfDecadeFieldRule.INSTANCE, 2);  // 7 + 2 -> 72
        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        m.getInputMap().put(QOY_RULE, 4);
        m.getInputMap().put(MOQ_RULE, 3);  // M3 + Q4 -> December
        m.getInputMap().put(DOM_RULE, 3);
        m.merge();
        assertMerged(m, null, LocalDate.rule(), date(1972, 12, 3));  // merged to 1972-12-03
    }

    public void test_merge_multiLevelMerge_crossCheck_valid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);
        m.getInputMap().put(MockYearOfDecadeFieldRule.INSTANCE, 2);  // 7 + 2 -> 72
        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);  // cross check against year of century
        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        m.getInputMap().put(YEAR_RULE, 1972);  // cross check against year
        m.merge();
        assertMerged(m, null, YEAR_RULE, 1972);  // merged to 1972
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_multiLevelMerge_crossCheck_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);
        m.getInputMap().put(MockYearOfDecadeFieldRule.INSTANCE, 2);  // 7 + 2 -> 72
        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 71);  // cross check against year of century
        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        m.getInputMap().put(YEAR_RULE, 1972);  // cross check against year
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), YEAR_RULE);
            assertMerged(m, null, MockDecadeOfCenturyFieldRule.INSTANCE, 7, MockYearOfDecadeFieldRule.INSTANCE, 2,
                    MockYearOfCenturyFieldRule.INSTANCE, 71, MockCenturyFieldRule.INSTANCE, 19, YEAR_RULE, 1972);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_merge_singleLevel_derivableRemoved_valid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);  // derivable from YearOfCentury
        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
        m.merge();
        assertMerged(m, null, MockYearOfCenturyFieldRule.INSTANCE, 72);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_singleLevel_derivableChecked_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from YearOfCentury, value is wrong
        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
            assertMerged(m, null, MockDecadeOfCenturyFieldRule.INSTANCE, 6, MockYearOfCenturyFieldRule.INSTANCE, 72);
            throw ex;
        }
    }

    public void test_merge_singleLevel_derivableDiscardUnused_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from YearOfCentury, value is wrong
        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
        m.merge();
        assertMerged(m, null, MockYearOfCenturyFieldRule.INSTANCE, 72);
    }

    //-----------------------------------------------------------------------
    public void test_merge_multiLevel_simple_derivableRemoved_valid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);  // derivable from Year
        m.getInputMap().put(YEAR_RULE, 1972);
        m.merge();
        assertMerged(m, null, YEAR_RULE, 1972);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_multiLevel_simple_derivableChecked_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from Year, value is wrong
        m.getInputMap().put(YEAR_RULE, 1972);
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
            assertMerged(m,  null, MockDecadeOfCenturyFieldRule.INSTANCE, 6, YEAR_RULE, 1972);
            throw ex;
        }
    }

    public void test_merge_multiLevel_simple_derivableDiscardUnused_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from Year, value is wrong
        m.getInputMap().put(YEAR_RULE, 1972);
        m.merge();
        assertMerged(m,  null, YEAR_RULE, 1972);
    }

    //-----------------------------------------------------------------------
    public void test_merge_multiLevel_complex_derivableRemoved_valid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 7);  // derivable from Year
        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        m.merge();
        assertMerged(m,  null, YEAR_RULE, 1972);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_multiLevel_complex_derivableChecked_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from Year, value is wrong
        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), MockDecadeOfCenturyFieldRule.INSTANCE);
            assertMerged(m, null, MockDecadeOfCenturyFieldRule.INSTANCE, 6, YEAR_RULE, 1972);
            throw ex;
        }
    }

    public void test_merge_multiLevel_complex_derivableDiscardUnused_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
        m.getInputMap().put(MockDecadeOfCenturyFieldRule.INSTANCE, 6);  // derivable from Year, value is wrong
        m.getInputMap().put(MockYearOfCenturyFieldRule.INSTANCE, 72);
        m.getInputMap().put(MockCenturyFieldRule.INSTANCE, 19);  // 19 + 72 -> 1972
        m.merge();
        assertMerged(m, null, YEAR_RULE, 1972);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_merge_toDate_directDMY() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.merge();
        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
    }

    public void test_merge_toDate_directDY() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(DOY_RULE, 182);
        m.merge();
        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
    }

    public void test_merge_toDate_mergeFieldsThenDMY() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(QOY_RULE, 2);
        m.getInputMap().put(MOQ_RULE, 3);
        m.getInputMap().put(DOM_RULE, 30);
        m.merge();
        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDate_direct_strictInvalidWithinBounds() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 31);
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), DOM_RULE);
            assertMerged(m, null, YEAR_RULE, 2008, MOY_RULE, MonthOfYear.JUNE, DOM_RULE, 31);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_toDate_direct_strictInvalidOutsideBounds() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 32);
        try {
            m.merge();
        } catch (IllegalCalendarFieldValueException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), DOM_RULE);
            assertMerged(m, null, YEAR_RULE, 2008, MOY_RULE, MonthOfYear.JUNE, DOM_RULE, 32);
            throw ex;
        }
    }

    public void test_merge_toDate_direct_lenientInvalidWithinBounds() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 31);
        m.merge();
        assertMerged(m, null, LocalDate.rule(), date(2008, 7, 1));
    }

    public void test_merge_toDate_direct_lenientInvalidOutsideBounds() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 32);
        m.merge();
        assertMerged(m, null, LocalDate.rule(), date(2008, 7, 2));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toDate_twoPrimarySetsMatch() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(DOY_RULE, 182);
        m.merge();
        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_merge_toDate_twoPrimarySetsDiffer() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(DOY_RULE, 183);
        try {
            m.merge();
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30), DOY_RULE, 183);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_mergeToDate_strict_crossCheck_valid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(DOW_RULE, 1);   // 2008-06-30 is a Monday, so this is right
        m.getInputMap().put(QOY_RULE, 2);   // 2008-06-30 is Q2, so this is right
        m.getInputMap().put(MIN_RULE, 30);  // ignored
        m.merge();
        assertMerged(m, null, MIN_RULE, 30, LocalDate.rule(), date(2008, 6, 30));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_mergeToDate_strict_crossCheck_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), DOW_RULE);
            assertMerged(m, null, DOW_RULE, DayOfWeek.TUESDAY, LocalDate.rule(), date(2008, 6, 30));
            throw ex;
        }
    }

    public void test_mergeToDate_strict_crossCheck_discardUnused() {
        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(DOW_RULE, 2);  // 2008-06-30 is a Monday, so this is wrong, but discarded
        m.merge();
        assertMerged(m, null, LocalDate.rule(), date(2008, 6, 30));
    }

    //-----------------------------------------------------------------------
    public void test_mergeToDate_lenient_crossCheck_valid() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 32);
        m.getInputMap().put(DOW_RULE, 3);   // 2008-07-02 is a Wednesday, so this is right
        m.getInputMap().put(QOY_RULE, 3);   // 2008-07-02 is Q3, so this is right
        m.getInputMap().put(MIN_RULE, 30);  // ignored
        m.merge();
        assertMerged(m, null, MIN_RULE, 30, LocalDate.rule(), date(2008, 7, 2));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_mergeToDate_lenient_crossCheck_invalid() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 32);
        m.getInputMap().put(DOW_RULE, 2);  // 2008-07-02 is a Wednesday, so this is wrong
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), DOW_RULE);
            assertMerged(m, null, DOW_RULE, DayOfWeek.TUESDAY, LocalDate.rule(), date(2008, 7, 2));
            throw ex;
        }
    }

    public void test_mergeToDate_lenient_crossCheck_discardUnused() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_DISCARD_UNUSED_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 32);
        m.getInputMap().put(DOW_RULE, 2);  // 2008-07-02 is a Wednesday, so this is wrong, but discarded
        m.merge();
        assertMerged(m, null, LocalDate.rule(), date(2008, 7, 2));
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_merge_toTime_directHM() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(11, 30));
    }

    public void test_merge_toTime_fieldsThenHM() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(HOUR_AMPM_RULE, 2);
        m.getInputMap().put(AMPM_RULE, 1);
        m.getInputMap().put(MIN_RULE, 30);
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(14, 30));
    }

    public void test_merge_toTime_fieldsThenHMSN() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 14);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(SEC_RULE, 50);
        m.getInputMap().put(MILLISEC_RULE, 1);  // must be merged to nanos before merge to LocalTime
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(14, 30, 50, 1000000));
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_toTime_strict_outsideBoundsHours() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 24);
        m.getInputMap().put(MIN_RULE, 30);
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), HOUR_RULE);
            assertMerged(m, null, HOUR_RULE, 24, MIN_RULE, 30);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_merge_toTime_strict_outsideBoundsMins() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 70);
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), MIN_RULE);
            assertMerged(m, null, HOUR_RULE, 11, MIN_RULE, 70);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_lenient_outsideBoundsHours() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 24);
        m.getInputMap().put(MIN_RULE, 30);
        m.merge();
        assertMerged(m, Period.days(1), LocalTime.rule(), time(0, 30)); // +P1D
    }

    public void test_merge_toTime_lenient_outsideBoundsMins() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 70);
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(12, 10));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_twoPrimarySetsMatch() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(MILLIDAY_RULE, 41400000);
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(11, 30));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_merge_toTime_twoPrimarySetsDiffer() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(MILLIDAY_RULE, 41400001);
        try {
            m.merge();
        } catch (CalendricalException ex) {
            dumpException(ex);
            assertMerged(m, null, LocalTime.rule(), time(11, 30), MILLIDAY_RULE, 41400001);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_strict_crossCheck_valid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(AMPM_RULE, 0);  // 11:30 is AM, so this is right
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(11, 30));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toTime_strict_crossCheck_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertMerged(m, null, AMPM_RULE, AmPmOfDay.PM, LocalTime.rule(), time(11, 30));
            throw ex;
        }
    }

    public void test_merge_toTime_strict_crossCheck_discardUnused() {
        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong, but discarded
        m.merge();
        assertMerged(m, null, LocalTime.rule(), time(11, 30));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toTime_lenient_crossCheck_valid() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 24);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(AMPM_RULE, 0);  // 00:30 is AM, so this is right
        m.merge();
        assertMerged(m, Period.days(1), LocalTime.rule(), time(0, 30)); // +P1D
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toTime_lenient_crossCheck_invalid() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 24);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(AMPM_RULE, 1);  // 00:30 is AM, so this is wrong
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertMerged(m, Period.days(1), AMPM_RULE, AmPmOfDay.PM, LocalTime.rule(), time(0, 30)); // +P1D
            throw ex;
        }
    }

    public void test_merge_toTime_lenient_crossCheck_discardUnused() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_DISCARD_UNUSED_CONTEXT);
        m.getInputMap().put(HOUR_RULE, 24);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(AMPM_RULE, 1);  // 00:30 is AM, so this is wrong, but ignored
        m.merge();
        assertMerged(m, Period.days(1), LocalTime.rule(), time(0, 30)); // +P1D
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public void test_merge_toDateTime_directYMDHM() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.merge();
        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 6, 30, 11, 30));
    }

    public void test_merge_toDateTime_lenientOverflow() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(HOUR_RULE, 23);
        m.getInputMap().put(MIN_RULE, 70);
        m.merge();
        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 7, 1, 0, 10));  // +P1D added
    }

    //-----------------------------------------------------------------------
    public void test_merge_toDateTime_strict_crossCheck_valid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(AMPM_RULE, 0);  // 11:30 is AM, so this is correct
        m.merge();
        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 6, 30, 11, 30));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDateTime_strict_crossCheck_invalid() {
        CalendricalMerger m = new CalendricalMerger(STRICT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), AMPM_RULE);
            assertMerged(m, null, AMPM_RULE, AmPmOfDay.PM, LocalDateTime.rule(), dateTime(2008, 6, 30, 11, 30));
            throw ex;
        }
    }

    public void test_merge_toDateTime_strict_crossCheck_discardUnused() {
        CalendricalMerger m = new CalendricalMerger(STRICT_DISCARD_UNUSED_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(HOUR_RULE, 11);
        m.getInputMap().put(MIN_RULE, 30);
        m.getInputMap().put(AMPM_RULE, 1);  // 11:30 is AM, so this is wrong, but discarded
        m.merge();
        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 6, 30, 11, 30));
    }

    //-----------------------------------------------------------------------
    public void test_merge_toDateTime_lenient_crossCheck_valid() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(DOW_RULE, 1);  // Monday, correct for 2008-06-30
        m.getInputMap().put(HOUR_RULE, 23);
        m.getInputMap().put(MIN_RULE, 70);
        m.getInputMap().put(AMPM_RULE, 0);  // 00:10 is AM, so this is correct
        m.merge();
        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 7, 1, 0, 10));  // +P1D added
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDateTime_lenient_crossCheck_invalid_daysAdded() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(DOW_RULE, 2);  // Tuesday, correct for end result of 2008-07-01, but check is before days added
        m.getInputMap().put(HOUR_RULE, 23);
        m.getInputMap().put(MIN_RULE, 70);
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), DOW_RULE);
            assertMerged(m, null, DOW_RULE, DayOfWeek.TUESDAY, LocalDateTime.rule(), dateTime(2008, 6, 30, 0, 10));  // +P1D
            throw ex;
        }
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_merge_toDateTime_lenient_crossCheck_invalid() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(HOUR_RULE, 23);
        m.getInputMap().put(MIN_RULE, 70);
        m.getInputMap().put(AMPM_RULE, 1);  // 00:10 is AM, so this is wrong
        try {
            m.merge();
        } catch (InvalidCalendarFieldException ex) {
            dumpException(ex);
            assertEquals(ex.getRule(), AMPM_RULE);
            assertMerged(m, null, AMPM_RULE, AmPmOfDay.PM, LocalDateTime.rule(), dateTime(2008, 6, 30, 0, 10));  // +P1D
            throw ex;
        }
    }

    public void test_merge_toDateTime_lenient_crossCheck_discardUnused() {
        CalendricalMerger m = new CalendricalMerger(LENIENT_DISCARD_UNUSED_CONTEXT);
        m.getInputMap().put(YEAR_RULE, 2008);
        m.getInputMap().put(MOY_RULE, 6);
        m.getInputMap().put(DOM_RULE, 30);
        m.getInputMap().put(HOUR_RULE, 23);
        m.getInputMap().put(MIN_RULE, 70);
        m.getInputMap().put(AMPM_RULE, 1);  // 00:10 is AM, so this is wrong, but discarded
        m.merge();
        assertMerged(m, null, LocalDateTime.rule(), dateTime(2008, 7, 1, 0, 10));  // +P1D added
    }

    //-----------------------------------------------------------------------
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
    private static <T, U, V, W, X> void assertMerged(
            CalendricalMerger m, Period overflow,
            CalendricalRule<T> rule1, T value1,
            CalendricalRule<U> rule2, U value2,
            CalendricalRule<V> rule3, V value3,
            CalendricalRule<W> rule4, W value4,
            CalendricalRule<X> rule5, W value5) {
        if (overflow != null) {
            assertEquals(m.getOverflow(), overflow);
        } else {
            assertEquals(m.getOverflow(), Period.ZERO);
        }
        assertEquals(m.getValue(rule1), value1);
        assertEquals(m.getValue(rule2), value2);
        assertEquals(m.getValue(rule3), value3);
        assertEquals(m.getValue(rule4), value4);
        assertEquals(m.getValue(rule5), value5);
    }
    private static void dumpException(Exception ex) {
        // this is used to allow a human to inspect the error messages to see if they are understandable
        System.out.println(ex.getMessage());
    }

    private LocalDate date(int year, int month, int day) {
        return LocalDate.date(year, month, day);
    }

    private LocalTime time(int hour, int minute) {
        return LocalTime.time(hour, minute);
    }

    private LocalTime time(int hour, int minute, int second, int nano) {
        return LocalTime.time(hour, minute, second, nano);
    }

    private LocalDateTime dateTime(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.dateTime(year, month, day, hour, minute);
    }

}
