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

import static javax.time.calendar.DayOfWeek.MONDAY;
import static javax.time.calendar.ISODateTimeRule.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.EPOCH_MONTH;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.YEAR;
import static javax.time.calendar.MonthOfYear.JANUARY;
import static javax.time.calendar.MonthOfYear.JUNE;
import static javax.time.calendar.MonthOfYear.OCTOBER;
import static org.testng.Assert.assertEquals;

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

    //-----------------------------------------------------------------------
    @DataProvider(name = "merge")
    public Object[][] data_merge() {
        return new Object[][] {
            {cals(LocalDate.of(2011, 6, 30)), LocalDate.rule(), LocalDate.of(2011, 6, 30)},
            {cals(LocalTime.of(11, 30)), LocalDate.rule(), null},
            {cals(LocalDate.of(2011, 6, 30), LocalTime.of(11, 30)), LocalDate.rule(), LocalDate.of(2011, 6, 30)},
            {cals(EPOCH_MONTH.field((2011 - 1970) * 12 + 6 - 1), DAY_OF_MONTH.field(30)), LocalDate.rule(), LocalDate.of(2011, 6, 30)},
            {cals(YEAR.field(2011), MONTH_OF_YEAR.field(6), DAY_OF_MONTH.field(30)), LocalDate.rule(), LocalDate.of(2011, 6, 30)},
            {cals(YEAR.field(2011), MONTH_OF_YEAR.field(6)), LocalDate.rule(), null},
            {cals(YEAR.field(2011), MONTH_OF_YEAR.field(6), QUARTER_OF_YEAR.field(1), DAY_OF_MONTH.field(30)), LocalDate.rule(), CalendricalException.class},
            {cals(YEAR.field(2011), MONTH_OF_YEAR.field(6), QUARTER_OF_YEAR.field(2), DAY_OF_MONTH.field(30)), LocalDate.rule(), LocalDate.of(2011, 6, 30)},
            {cals(YEAR.field(2011), JUNE, ALIGNED_WEEK_OF_MONTH.field(2), MONDAY), LocalDate.rule(), LocalDate.of(2011, 6, 13)},
            {cals(YearMonth.of(2011, 6), DAY_OF_MONTH.field(30)), LocalDate.rule(), LocalDate.of(2011, 6, 30)},
            {cals(Year.of(2011), MonthDay.of(6, 30)), LocalDate.rule(), LocalDate.of(2011, 6, 30)},
            {cals(Year.of(2011), JANUARY, DAY_OF_MONTH.field(23)), LocalDate.rule(), LocalDate.of(2011, 1, 23)},
            
            {cals(LocalDate.of(2011, 6, 30)), Year.rule(), Year.of(2011)},
            {cals(YearMonth.of(2011, 6)), Year.rule(), Year.of(2011)},
            {cals(YEAR.field(2011)), Year.rule(), Year.of(2011)},
            {cals(OCTOBER), Year.rule(), null},
            
            {cals(LocalDate.of(2011, 6, 30)), YearMonth.rule(), YearMonth.of(2011, 6)},
            {cals(Year.of(2011), OCTOBER), YearMonth.rule(), YearMonth.of(2011, 9)},
            {cals(OCTOBER), YearMonth.rule(), null},
            
            {cals(LocalDate.of(2011, 6, 30)), MonthDay.rule(), MonthDay.of(6, 30)},
            {cals(DAY_OF_MONTH.field(23), OCTOBER), MonthDay.rule(), MonthDay.of(9, 23)},
            {cals(OCTOBER), MonthDay.rule(), null},
            
            {cals(LocalDate.of(2011, 6, 30)), MonthOfYear.rule(), JUNE},
            {cals(DAY_OF_MONTH.field(23), OCTOBER), MonthOfYear.rule(), OCTOBER},
            {cals(Year.of(2011)), MonthOfYear.rule(), null},
            
            {cals(LocalDate.of(2011, 6, 30)), LocalTime.rule(), null},
            {cals(LocalTime.of(11, 30)), LocalTime.rule(), LocalTime.of(11, 30)},
            {cals(LocalDate.of(2011, 6, 30), LocalTime.of(11, 30)), LocalTime.rule(), LocalTime.of(11, 30)},
            
            {cals(LocalDate.of(2011, 6, 30)), LocalDateTime.rule(), null},
            {cals(LocalTime.of(11, 30)), LocalDateTime.rule(), null},
            {cals(LocalDate.of(2011, 6, 30), LocalTime.of(11, 30)), LocalDateTime.rule(), LocalDateTime.of(2011, 6, 30, 11, 30)},
        };
    }

    @Test(dataProvider = "merge")
    public void test_merge(List<Calendrical> calendicals, CalendricalRule<?> ruleToDerive, Object expectedVal) {
        Calendrical[] array = calendicals.toArray(new Calendrical[calendicals.size()]);
        if (expectedVal instanceof Class) {
            try {
                CalendricalNormalizer.merge(array);
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
                m.deriveChecked(ruleToDerive);
            } catch (CalendricalException ex) {
                System.out.println(ex);
            }
        }
    }

    private List<Calendrical> cals(Calendrical... cals) {
        return Arrays.asList(cals);
    }

}
