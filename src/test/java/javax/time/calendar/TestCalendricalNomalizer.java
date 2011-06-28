/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendar.DayOfWeek.THURSDAY;
import static javax.time.calendar.ISODateTimeRule.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.YEAR;
import static javax.time.calendar.ISODateTimeRule.ZERO_EPOCH_MONTH;
import static javax.time.calendar.MonthOfYear.FEBRUARY;
import static javax.time.calendar.MonthOfYear.JUNE;
import static javax.time.calendar.MonthOfYear.OCTOBER;
import static javax.time.calendar.QuarterOfYear.Q2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.time.CalendricalException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test merging of ISO fields.
 */
@Test
public class TestCalendricalNomalizer {

    private static final ZoneOffset OFFSET = ZoneOffset.of("+03:00");
    private static final OffsetDateTime OFFSET_DATE_TIME_2011_06_30_11_30 = OffsetDateTime.of(2011, 6, 30, 11, 30, OFFSET);
    private static final LocalDateTime DATE_TIME_2011_06_30_11_30 = LocalDateTime.of(2011, 6, 30, 11, 30);
    private static final LocalDate DATE_2011_06_30 = LocalDate.of(2011, 6, 30);
    private static final Year YEAR_2011 = Year.of(2011);
    private static final YearMonth YEAR_MONTH_2011_06 = YearMonth.of(2011, 6);
    private static final MonthDay MONTH_DAY_06_30 = MonthDay.of(6, 30);
    private static final LocalTime TIME_11_30 = LocalTime.of(11, 30);
    private static final DateTimeField FIELD_YEAR_2011 = YEAR.field(2011);
    private static final DateTimeField FIELD_MOY_06 = MONTH_OF_YEAR.field(6);
    private static final DateTimeField FIELD_DOM_30 = DAY_OF_MONTH.field(30);
    private static final DateTimeField FIELD_QOY_2 = QUARTER_OF_YEAR.field(2);
    private static final DateTimeField FIELD_MOQ_3 = MONTH_OF_QUARTER.field(3);
    private static final DateTimeField FIELD_DOW_4 = DAY_OF_WEEK.field(4);
    private static final DateTimeField FIELD_DOY_181 = DAY_OF_YEAR.field(181);
    private static final DateTimeField FIELD_ZEM_2011_06 = ZERO_EPOCH_MONTH.field(2011 * 12 + 6 - 1);
    private static final DateTimeField FIELD_AWOM_5 = ALIGNED_WEEK_OF_MONTH.field(5);

    //-----------------------------------------------------------------------
    @DataProvider(name = "merge")
    public Object[][] data_merge() {
        return new Object[][] {
//            // from nothing
//            {cals(), LocalDate.rule(), null},
//            {cals(), LocalTime.rule(), null},
//            {cals(), LocalDateTime.rule(), null},
//            {cals(), OffsetDate.rule(), null},
//            {cals(), OffsetTime.rule(), null},
//            {cals(), OffsetDateTime.rule(), null},
//            {cals(), ZonedDateTime.rule(), null},
//            {cals(), ZoneOffset.rule(), null},
//            {cals(), ZoneId.rule(), null},
//            {cals(), Chronology.rule(), null},
//            
//            // from LocalDateTime
//            {cals(DATE_TIME_2011_06_30_11_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(DATE_TIME_2011_06_30_11_30), LocalTime.rule(), TIME_11_30},
//            {cals(DATE_TIME_2011_06_30_11_30), LocalDateTime.rule(), DATE_TIME_2011_06_30_11_30},
//            {cals(DATE_TIME_2011_06_30_11_30), OffsetDate.rule(), null},
//            {cals(DATE_TIME_2011_06_30_11_30), OffsetTime.rule(), null},
//            {cals(DATE_TIME_2011_06_30_11_30), OffsetDateTime.rule(), null},
//            {cals(DATE_TIME_2011_06_30_11_30), ZonedDateTime.rule(), null},
//            {cals(DATE_TIME_2011_06_30_11_30), ZoneOffset.rule(), null},
//            {cals(DATE_TIME_2011_06_30_11_30), ZoneId.rule(), null},
//            {cals(DATE_TIME_2011_06_30_11_30), Chronology.rule(), ISOChronology.INSTANCE},
//            {cals(DATE_TIME_2011_06_30_11_30), Year.rule(), YEAR_2011},
//            {cals(DATE_TIME_2011_06_30_11_30), YearMonth.rule(), YEAR_MONTH_2011_06},
//            {cals(DATE_TIME_2011_06_30_11_30), MonthDay.rule(), MONTH_DAY_06_30},
//            {cals(DATE_TIME_2011_06_30_11_30), QuarterOfYear.rule(), Q2},
//            {cals(DATE_TIME_2011_06_30_11_30), MonthOfYear.rule(), JUNE},
//            {cals(DATE_TIME_2011_06_30_11_30), DayOfWeek.rule(), THURSDAY},
//            {cals(DATE_TIME_2011_06_30_11_30), YEAR, FIELD_YEAR_2011},
//            {cals(DATE_TIME_2011_06_30_11_30), QUARTER_OF_YEAR, FIELD_QOY_2},
//            {cals(DATE_TIME_2011_06_30_11_30), ZERO_EPOCH_MONTH, FIELD_ZEM_2011_06},
//            {cals(DATE_TIME_2011_06_30_11_30), MONTH_OF_YEAR, FIELD_MOY_06},
//            {cals(DATE_TIME_2011_06_30_11_30), MONTH_OF_QUARTER, FIELD_MOQ_3},
//            {cals(DATE_TIME_2011_06_30_11_30), DAY_OF_YEAR, FIELD_DOY_181},
//            {cals(DATE_TIME_2011_06_30_11_30), DAY_OF_MONTH, FIELD_DOM_30},
//            {cals(DATE_TIME_2011_06_30_11_30), DAY_OF_WEEK, FIELD_DOW_4},
//            {cals(DATE_TIME_2011_06_30_11_30), ALIGNED_WEEK_OF_MONTH, FIELD_AWOM_5},
//            // TODO time
//            
//            // from LocalDate
//            {cals(DATE_2011_06_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(DATE_2011_06_30), LocalTime.rule(), null},
//            {cals(DATE_2011_06_30), LocalDateTime.rule(), null},
//            {cals(DATE_2011_06_30), OffsetDate.rule(), null},
//            {cals(DATE_2011_06_30), OffsetTime.rule(), null},
//            {cals(DATE_2011_06_30), OffsetDateTime.rule(), null},
//            {cals(DATE_2011_06_30), ZonedDateTime.rule(), null},
//            {cals(DATE_2011_06_30), ZoneOffset.rule(), null},
//            {cals(DATE_2011_06_30), ZoneId.rule(), null},
//            {cals(DATE_2011_06_30), Chronology.rule(), ISOChronology.INSTANCE},
//            {cals(DATE_2011_06_30), Year.rule(), YEAR_2011},
//            {cals(DATE_2011_06_30), YearMonth.rule(), YEAR_MONTH_2011_06},
//            {cals(DATE_2011_06_30), MonthDay.rule(), MONTH_DAY_06_30},
//            {cals(DATE_2011_06_30), QuarterOfYear.rule(), Q2},
//            {cals(DATE_2011_06_30), MonthOfYear.rule(), JUNE},
//            {cals(DATE_2011_06_30), DayOfWeek.rule(), THURSDAY},
//            {cals(DATE_2011_06_30), YEAR, FIELD_YEAR_2011},
//            {cals(DATE_2011_06_30), QUARTER_OF_YEAR, FIELD_QOY_2},
//            {cals(DATE_2011_06_30), ZERO_EPOCH_MONTH, FIELD_ZEM_2011_06},
//            {cals(DATE_2011_06_30), MONTH_OF_YEAR, FIELD_MOY_06},
//            {cals(DATE_2011_06_30), MONTH_OF_QUARTER, FIELD_MOQ_3},
//            {cals(DATE_2011_06_30), DAY_OF_YEAR, FIELD_DOY_181},
//            {cals(DATE_2011_06_30), DAY_OF_MONTH, FIELD_DOM_30},
//            {cals(DATE_2011_06_30), DAY_OF_WEEK, FIELD_DOW_4},
//            {cals(DATE_2011_06_30), ALIGNED_WEEK_OF_MONTH, FIELD_AWOM_5},
//            
//            // from Year
//            {cals(YEAR_2011), LocalDate.rule(), null},
//            {cals(YEAR_2011), LocalTime.rule(), null},
//            {cals(YEAR_2011), LocalDateTime.rule(), null},
//            {cals(YEAR_2011), OffsetDate.rule(), null},
//            {cals(YEAR_2011), OffsetTime.rule(), null},
//            {cals(YEAR_2011), OffsetDateTime.rule(), null},
//            {cals(YEAR_2011), ZonedDateTime.rule(), null},
//            {cals(YEAR_2011), ZoneOffset.rule(), null},
//            {cals(YEAR_2011), ZoneId.rule(), null},
//            {cals(YEAR_2011), Chronology.rule(), ISOChronology.INSTANCE},
//            {cals(YEAR_2011), Year.rule(), YEAR_2011},
//            {cals(YEAR_2011), YearMonth.rule(), null},
//            {cals(YEAR_2011), MonthDay.rule(), null},
//            {cals(YEAR_2011), QuarterOfYear.rule(), null},
//            {cals(YEAR_2011), MonthOfYear.rule(), null},
//            {cals(YEAR_2011), DayOfWeek.rule(), null},
//            {cals(YEAR_2011), YEAR, FIELD_YEAR_2011},
//            {cals(YEAR_2011), QUARTER_OF_YEAR, null},
//            {cals(YEAR_2011), ZERO_EPOCH_MONTH, null},
//            {cals(YEAR_2011), MONTH_OF_YEAR, null},
//            {cals(YEAR_2011), MONTH_OF_QUARTER, null},
//            {cals(YEAR_2011), DAY_OF_YEAR, null},
//            {cals(YEAR_2011), DAY_OF_MONTH, null},
//            {cals(YEAR_2011), DAY_OF_WEEK, null},
//            {cals(YEAR_2011), ALIGNED_WEEK_OF_MONTH, null},
//            
//            // from YearMonth
//            {cals(YEAR_MONTH_2011_06), LocalDate.rule(), null},
//            {cals(YEAR_MONTH_2011_06), LocalTime.rule(), null},
//            {cals(YEAR_MONTH_2011_06), LocalDateTime.rule(), null},
//            {cals(YEAR_MONTH_2011_06), OffsetDate.rule(), null},
//            {cals(YEAR_MONTH_2011_06), OffsetTime.rule(), null},
//            {cals(YEAR_MONTH_2011_06), OffsetDateTime.rule(), null},
//            {cals(YEAR_MONTH_2011_06), ZonedDateTime.rule(), null},
//            {cals(YEAR_MONTH_2011_06), ZoneOffset.rule(), null},
//            {cals(YEAR_MONTH_2011_06), ZoneId.rule(), null},
//            {cals(YEAR_MONTH_2011_06), Chronology.rule(), ISOChronology.INSTANCE},
//            {cals(YEAR_MONTH_2011_06), Year.rule(), YEAR_2011},
//            {cals(YEAR_MONTH_2011_06), YearMonth.rule(), YEAR_MONTH_2011_06},
//            {cals(YEAR_MONTH_2011_06), MonthDay.rule(), null},
//            {cals(YEAR_MONTH_2011_06), QuarterOfYear.rule(), Q2},
//            {cals(YEAR_MONTH_2011_06), MonthOfYear.rule(), JUNE},
//            {cals(YEAR_MONTH_2011_06), DayOfWeek.rule(), null},
//            {cals(YEAR_MONTH_2011_06), YEAR, FIELD_YEAR_2011},
//            {cals(YEAR_MONTH_2011_06), QUARTER_OF_YEAR, FIELD_QOY_2},
//            {cals(YEAR_MONTH_2011_06), ZERO_EPOCH_MONTH, FIELD_ZEM_2011_06},
//            {cals(YEAR_MONTH_2011_06), MONTH_OF_YEAR, FIELD_MOY_06},
//            {cals(YEAR_MONTH_2011_06), MONTH_OF_QUARTER, FIELD_MOQ_3},
//            {cals(YEAR_MONTH_2011_06), DAY_OF_YEAR, null},
//            {cals(YEAR_MONTH_2011_06), DAY_OF_MONTH, null},
//            {cals(YEAR_MONTH_2011_06), DAY_OF_WEEK, null},
//            {cals(YEAR_MONTH_2011_06), ALIGNED_WEEK_OF_MONTH, null},
//            
//            // from MonthDay
//            {cals(MONTH_DAY_06_30), LocalDate.rule(), null},
//            {cals(MONTH_DAY_06_30), LocalTime.rule(), null},
//            {cals(MONTH_DAY_06_30), LocalDateTime.rule(), null},
//            {cals(MONTH_DAY_06_30), OffsetDate.rule(), null},
//            {cals(MONTH_DAY_06_30), OffsetTime.rule(), null},
//            {cals(MONTH_DAY_06_30), OffsetDateTime.rule(), null},
//            {cals(MONTH_DAY_06_30), ZonedDateTime.rule(), null},
//            {cals(MONTH_DAY_06_30), ZoneOffset.rule(), null},
//            {cals(MONTH_DAY_06_30), ZoneId.rule(), null},
//            {cals(MONTH_DAY_06_30), Chronology.rule(), ISOChronology.INSTANCE},
//            {cals(MONTH_DAY_06_30), Year.rule(), null},
//            {cals(MONTH_DAY_06_30), YearMonth.rule(), null},
//            {cals(MONTH_DAY_06_30), MonthDay.rule(), MONTH_DAY_06_30},
//            {cals(MONTH_DAY_06_30), QuarterOfYear.rule(), Q2},
//            {cals(MONTH_DAY_06_30), MonthOfYear.rule(), JUNE},
//            {cals(MONTH_DAY_06_30), DayOfWeek.rule(), null},
//            {cals(MONTH_DAY_06_30), YEAR, null},
//            {cals(MONTH_DAY_06_30), QUARTER_OF_YEAR, FIELD_QOY_2},
//            {cals(MONTH_DAY_06_30), ZERO_EPOCH_MONTH, null},
//            {cals(MONTH_DAY_06_30), MONTH_OF_YEAR, FIELD_MOY_06},
//            {cals(MONTH_DAY_06_30), MONTH_OF_QUARTER, FIELD_MOQ_3},
//            {cals(MONTH_DAY_06_30), DAY_OF_YEAR, null},
//            {cals(MONTH_DAY_06_30), DAY_OF_MONTH, FIELD_DOM_30},
//            {cals(MONTH_DAY_06_30), DAY_OF_WEEK, null},
//            {cals(MONTH_DAY_06_30), ALIGNED_WEEK_OF_MONTH, FIELD_AWOM_5},
//            
//            // create date
//            {cals(DATE_2011_06_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(TIME_11_30), LocalDate.rule(), null},
//            {cals(DATE_TIME_2011_06_30_11_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(DATE_2011_06_30, TIME_11_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(FIELD_ZEM_2011_06, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(FIELD_YEAR_2011, FIELD_MOY_06, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(FIELD_YEAR_2011, JUNE, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(FIELD_YEAR_2011, FIELD_MOY_06, FIELD_QOY_2, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(FIELD_YEAR_2011, JUNE, FIELD_QOY_2, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
//            {cals(FIELD_YEAR_2011, JUNE, Q2, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, Q2, FIELD_MOQ_3, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(YEAR_MONTH_2011_06, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(YEAR_2011, MONTH_DAY_06_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, JUNE, FIELD_AWOM_5, THURSDAY), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, FIELD_MOY_06), LocalDate.rule(), null},
            {cals(FIELD_YEAR_2011, FIELD_MOY_06, QUARTER_OF_YEAR.field(1), FIELD_DOM_30), LocalDate.rule(), CalendricalException.class},
            {cals(DATE_2011_06_30, LocalDate.of(2011, 6, 22)), LocalDate.rule(), CalendricalException.class},
            {cals(DATE_2011_06_30, FEBRUARY), LocalDate.rule(), CalendricalException.class},
            
            // create Year
            {cals(TIME_11_30), Year.rule(), null},
            {cals(DATE_TIME_2011_06_30_11_30), Year.rule(), YEAR_2011},
            {cals(FIELD_YEAR_2011), Year.rule(), YEAR_2011},
            {cals(OCTOBER), Year.rule(), null},
            
            // create YearMonth
            {cals(TIME_11_30), YearMonth.rule(), null},
            {cals(DATE_TIME_2011_06_30_11_30), YearMonth.rule(), YEAR_MONTH_2011_06},
            {cals(YEAR_2011, OCTOBER), YearMonth.rule(), YearMonth.of(2011, 10)},
            {cals(OCTOBER), YearMonth.rule(), null},
            
            // create MonthDay
            {cals(TIME_11_30), MonthDay.rule(), null},
            {cals(LocalDateTime.of(2011, 6, 30, 12, 30)), MonthDay.rule(), MONTH_DAY_06_30},
            {cals(DAY_OF_MONTH.field(23), OCTOBER), MonthDay.rule(), MonthDay.of(10, 23)},
            {cals(OCTOBER), MonthDay.rule(), null},
            
            // create MonthOfYear
            {cals(TIME_11_30), MonthOfYear.rule(), null},
            {cals(DATE_TIME_2011_06_30_11_30), MonthOfYear.rule(), JUNE},
            {cals(DAY_OF_MONTH.field(23), OCTOBER), MonthOfYear.rule(), OCTOBER},
            
            // create Field:MonthOfYear
            {cals(TIME_11_30), MONTH_OF_YEAR, null},
            {cals(DATE_TIME_2011_06_30_11_30), MONTH_OF_YEAR, FIELD_MOY_06},
            {cals(DAY_OF_MONTH.field(23), OCTOBER), MONTH_OF_YEAR, MONTH_OF_YEAR.field(10)},
            
            // create LocalTime
            {cals(TIME_11_30), LocalTime.rule(), TIME_11_30},
            {cals(DATE_TIME_2011_06_30_11_30), LocalTime.rule(), TIME_11_30},
            {cals(DATE_2011_06_30, TIME_11_30), LocalTime.rule(), TIME_11_30},
            
            // create LocalDateTime
            {cals(TIME_11_30), LocalDateTime.rule(), null},
            {cals(DATE_2011_06_30, TIME_11_30), LocalDateTime.rule(), DATE_TIME_2011_06_30_11_30},
        };
    }

    @Test(dataProvider = "merge")
    public void test_merge(List<Calendrical> calendicals, CalendricalRule<?> ruleToDerive, Object expectedVal) {
        Calendrical[] array = calendicals.toArray(new Calendrical[calendicals.size()]);
        if (expectedVal instanceof Class) {
            try {
                CalendricalNormalizer.merge(array);
                fail();
            } catch (CalendricalException ex) {
                System.out.println(ex);
            }
        } else {
            CalendricalNormalizer m = CalendricalNormalizer.merge(array);
            Object derived = m.derive(ruleToDerive);
//            if (expectedVal != null && derived == null) {
//                try {
//                    m.deriveChecked(ruleToDerive);
//                } catch (CalendricalException ex) {
//                    System.out.println(ex);
//                }
//            }
            assertEquals(derived, expectedVal);
        }
    }

    @Test(dataProvider = "merge")
    public void test_mergeError(List<Calendrical> calendicals, CalendricalRule<?> ruleToDerive, Object expectedVal) {
        if (expectedVal == null) {
            Calendrical[] array = calendicals.toArray(new Calendrical[calendicals.size()]);
            CalendricalNormalizer m = CalendricalNormalizer.merge(array);
            try {
                Object d = m.deriveChecked(ruleToDerive);
                fail("Failed to throw error: " + calendicals + " -> " + ruleToDerive + " = " + d);
            } catch (CalendricalException ex) {
                System.out.println(ex);
            }
        }
    }

    private List<Calendrical> cals(Calendrical... cals) {
        return Arrays.asList(cals);
    }

}
