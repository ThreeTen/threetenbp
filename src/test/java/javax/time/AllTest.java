/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import java.util.Arrays;

import javax.time.calendar.TestClock;
import javax.time.calendar.TestClock_TimeSourceClock;
import javax.time.calendar.TestDateAdjusters;
import javax.time.calendar.TestDateMatchers;
import javax.time.calendar.TestDateResolvers;
import javax.time.calendar.TestISOChronology;
import javax.time.calendar.TestISODayOfMonthRule;
import javax.time.calendar.TestISODayOfWeekRule;
import javax.time.calendar.TestISODayOfYearRule;
import javax.time.calendar.TestISOMonthOfYearRule;
import javax.time.calendar.TestISOYearRule;
import javax.time.calendar.TestLocalDate;
import javax.time.calendar.TestLocalDateTime;
import javax.time.calendar.TestLocalTime;
import javax.time.calendar.TestOffsetDate;
import javax.time.calendar.TestOffsetDateTime;
import javax.time.calendar.TestOffsetDateTime_instants;
import javax.time.calendar.TestOffsetTime;
import javax.time.calendar.TestTimeZone;
import javax.time.calendar.TestZoneOffset;
import javax.time.calendar.TestZoneResolvers;
import javax.time.calendar.TestZonedDateTime;
import javax.time.calendar.field.TestAmPmOfDay;
import javax.time.calendar.field.TestDayOfMonth;
import javax.time.calendar.field.TestDayOfWeek;
import javax.time.calendar.field.TestDayOfYear;
import javax.time.calendar.field.TestHourOfDay;
import javax.time.calendar.field.TestMinuteOfHour;
import javax.time.calendar.field.TestMonthOfYear;
import javax.time.calendar.field.TestNanoOfSecond;
import javax.time.calendar.field.TestQuarterOfYear;
import javax.time.calendar.field.TestSecondOfMinute;
import javax.time.calendar.field.TestWeekBasedYear;
import javax.time.calendar.field.TestWeekOfWeekBasedYear;
import javax.time.calendar.field.TestYear;
import javax.time.calendar.format.TestCalendricalFormatException;
import javax.time.calendar.format.TestCaseSensitivePrinterParser;
import javax.time.calendar.format.TestCharLiteralParser;
import javax.time.calendar.format.TestCharLiteralPrinter;
import javax.time.calendar.format.TestDateTimeFormatter;
import javax.time.calendar.format.TestDateTimeFormatterBuilder;
import javax.time.calendar.format.TestDateTimeFormatters;
import javax.time.calendar.format.TestFractionPrinterParser;
import javax.time.calendar.format.TestNumberParser;
import javax.time.calendar.format.TestNumberPrinter;
import javax.time.calendar.format.TestPadParserDecorator;
import javax.time.calendar.format.TestPadPrinterDecorator;
import javax.time.calendar.format.TestStrictLenientPrinterParser;
import javax.time.calendar.format.TestStringLiteralParser;
import javax.time.calendar.format.TestStringLiteralPrinter;
import javax.time.calendar.format.TestTextParser;
import javax.time.calendar.format.TestTextPrinter;
import javax.time.calendar.format.TestZoneOffsetParser;
import javax.time.calendar.format.TestZoneOffsetPrinter;
import javax.time.calendar.format.TestZoneParser;
import javax.time.calendar.i18n.TestCopticChronology;
import javax.time.calendar.i18n.TestCopticDate;
import javax.time.calendar.zone.TestStandardZoneRules;
import javax.time.calendar.zone.TestZoneRulesBuilder;
import javax.time.period.TestPeriod;
import javax.time.period.TestPeriodFields;
import javax.time.period.TestPeriodParser;
import javax.time.period.TestPeriodUnits;
import javax.time.period.field.TestDays;
import javax.time.period.field.TestHours;
import javax.time.period.field.TestMinutes;
import javax.time.period.field.TestMonths;
import javax.time.period.field.TestSeconds;
import javax.time.period.field.TestWeeks;
import javax.time.period.field.TestYears;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.internal.Utils;

/**
 * Test class.
 * 
 * @author Stephen Colebourne
 */
public class AllTest {

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] {
            // main classes
            TestDuration.class,
            TestInstant.class,
            TestMathUtils.class,
            TestTimeSource_Fixed.class,
            TestTimeSource_OffsetSystem.class,
            TestTimeSource_System.class,
            // calendar classes
//            TestCalendricalMerger.class,
            TestClock.class,
            TestClock_TimeSourceClock.class,
            TestDateAdjusters.class,
            TestDateMatchers.class,
            TestDateResolvers.class,
//            TestDateTimeFields.class,  // needs Comparable impl
            TestISOChronology.class,
            TestISODayOfMonthRule.class,
            TestISODayOfWeekRule.class,
            TestISODayOfYearRule.class,
            TestISOMonthOfYearRule.class,
            TestISOYearRule.class,
            TestLocalDate.class,
            TestLocalDateTime.class,
            TestLocalTime.class,
//            TestMonthDay.class,  // needs strict to block overflow leftovers
            TestOffsetDate.class,
            TestOffsetDateTime.class,
            TestOffsetDateTime_instants.class,
            TestOffsetTime.class,
            TestTimeZone.class,
//            TestYearMonth.class,  // needs strict to block overflow leftovers
            TestZonedDateTime.class,
            TestZoneOffset.class,
            TestZoneResolvers.class,
            // calendar fields
            TestAmPmOfDay.class,
            TestDayOfMonth.class,
            TestDayOfWeek.class,
            TestDayOfYear.class,
            TestHourOfDay.class,
            TestMinuteOfHour.class,
            TestMonthOfYear.class,
            TestNanoOfSecond.class,
            TestQuarterOfYear.class,
            TestSecondOfMinute.class,
            TestWeekOfWeekBasedYear.class,
            TestWeekBasedYear.class,
            TestYear.class,
            // format
            TestCalendricalFormatException.class,
            TestCaseSensitivePrinterParser.class,
            TestCharLiteralParser.class,
            TestCharLiteralPrinter.class,
            TestDateTimeFormatter.class,
            TestDateTimeFormatters.class,
//            TestDateTimeParseContext.class,  // needs Comparable impl
            TestDateTimeFormatters.class,
            TestDateTimeFormatterBuilder.class,
            TestFractionPrinterParser.class,
            TestNumberParser.class,
            TestNumberPrinter.class,
            TestPadParserDecorator.class,
            TestPadPrinterDecorator.class,
            TestStrictLenientPrinterParser.class,
            TestStringLiteralPrinter.class,
            TestStringLiteralParser.class,
            TestTextPrinter.class,
            TestTextParser.class,
            TestZoneOffsetPrinter.class,
            TestZoneOffsetParser.class,
            TestZoneParser.class,
            // i18n
            TestCopticChronology.class,
            TestCopticDate.class,
            // zone
            TestStandardZoneRules.class,
            TestZoneRulesBuilder.class,
            // periods
            TestPeriod.class,
            TestPeriodParser.class,
            TestPeriodFields.class,
            TestPeriodUnits.class,
            // period fields
            TestDays.class,
            TestHours.class,
            TestMinutes.class,
            TestMonths.class,
            TestSeconds.class,
            TestWeeks.class,
            TestYears.class,
        });
//        testng.addListener(new DotTestListener());
//        testng.addListener(new TextReporter("All", 2));
        testng.addListener(new TestListenerAdapter() {
            private int count = 0;
            private void log() {
                // log dot every 25 tests
                if ((getPassedTests().size() + getFailedTests().size()) % 25 == 0) {
                    System.out.print('.');
                    if (++count == 40) {
                        count = 0;
                        System.out.println();
                    }
                }
            }
            @Override
            public void onTestSuccess(ITestResult tr) {
                super.onTestSuccess(tr);
                log();
            }
            @Override
            public void onTestFailure(ITestResult tr) {
                super.onTestFailure(tr);
                log();
                Throwable throwable = tr.getThrowable();
                String params = "";
                if (tr.getParameters() != null && tr.getParameters().length > 0) {
                    params = " " + Arrays.toString(tr.getParameters());
                    if (tr.getMethod().getMethod().getParameterTypes().length != tr.getParameters().length) {
                        params = " Method has wrong number of arguments for data provider";
                        throwable = null;
                    }
                }
                String desc = tr.getMethod().getDescription() == null ? "" : " " + tr.getMethod().getDescription();
                System.out.println("FAILED: " + tr.getName() + desc + params);
                if (throwable != null) {
                    System.out.println(Utils.stackTrace(throwable, false)[0]);
                }
            }
        });
        
        testng.run();
    }

}
