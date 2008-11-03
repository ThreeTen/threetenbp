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
package javax.time;

import java.util.Arrays;

import javax.time.calendar.TestCalendrical;
import javax.time.calendar.TestDateAdjusters;
import javax.time.calendar.TestDateMatchers;
import javax.time.calendar.TestDateResolvers;
import javax.time.calendar.TestDateTimeFields;
import javax.time.calendar.TestISOChronology;
import javax.time.calendar.TestLocalDate;
import javax.time.calendar.TestLocalDateTime;
import javax.time.calendar.TestLocalTime;
import javax.time.calendar.TestMonthDay;
import javax.time.calendar.TestOffsetDate;
import javax.time.calendar.TestOffsetDateTime;
import javax.time.calendar.TestOffsetDateTime_instants;
import javax.time.calendar.TestOffsetTime;
import javax.time.calendar.TestTimeZone;
import javax.time.calendar.TestYearMonth;
import javax.time.calendar.TestZoneOffset;
import javax.time.calendar.TestZoneResolvers;
import javax.time.calendar.TestZonedDateTime;
import javax.time.calendar.field.TestAmPmOfDay;
import javax.time.calendar.field.TestDayOfMonth;
import javax.time.calendar.field.TestDayOfMonthRule;
import javax.time.calendar.field.TestDayOfWeek;
import javax.time.calendar.field.TestDayOfYear;
import javax.time.calendar.field.TestHourOfDay;
import javax.time.calendar.field.TestMinuteOfHour;
import javax.time.calendar.field.TestMonthOfYear;
import javax.time.calendar.field.TestNanoOfSecond;
import javax.time.calendar.field.TestQuarterOfYear;
import javax.time.calendar.field.TestSecondOfMinute;
import javax.time.calendar.field.TestWeekyear;
import javax.time.calendar.field.TestYear;
import javax.time.calendar.format.TestCalendricalFormatException;
import javax.time.calendar.format.TestCharLiteralParser;
import javax.time.calendar.format.TestCharLiteralPrinter;
import javax.time.calendar.format.TestDateTimeFormatter;
import javax.time.calendar.format.TestDateTimeFormatters;
import javax.time.calendar.format.TestDateTimeParseContext;
import javax.time.calendar.format.TestFractionPrinterParser;
import javax.time.calendar.format.TestNumberParser;
import javax.time.calendar.format.TestNumberPrinter;
import javax.time.calendar.format.TestPadParserDecorator;
import javax.time.calendar.format.TestPadPrinterDecorator;
import javax.time.calendar.format.TestStringLiteralParser;
import javax.time.calendar.format.TestStringLiteralPrinter;
import javax.time.calendar.format.TestTextParser;
import javax.time.calendar.format.TestTextPrinter;
import javax.time.calendar.format.TestZoneOffsetPrinter;
import javax.time.calendar.i18n.TestCopticChronology;
import javax.time.calendar.i18n.TestCopticDate;
import javax.time.period.TestPeriod;
import javax.time.period.TestPeriodFields;
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
            TestClock.class,
            TestDuration.class,
            TestInstant.class,
            TestMathUtils.class,
            // calendar classes
            TestCalendrical.class,
            TestDateAdjusters.class,
            TestDateMatchers.class,
            TestDateResolvers.class,
            TestDateTimeFields.class,
            TestISOChronology.class,
            TestLocalDate.class,
            TestLocalDateTime.class,
            TestLocalTime.class,
            TestMonthDay.class,
            TestOffsetDate.class,
            TestOffsetDateTime.class,
            TestOffsetDateTime_instants.class,
            TestOffsetTime.class,
            TestTimeZone.class,
            TestYearMonth.class,
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
            TestWeekyear.class,
            TestYear.class,
            // calendar rules
            TestDayOfMonthRule.class,
            // format
            TestCalendricalFormatException.class,
            TestDateTimeFormatter.class,
            TestDateTimeFormatters.class,
            TestDateTimeParseContext.class,
            TestCharLiteralParser.class,
            TestCharLiteralPrinter.class,
            TestDateTimeFormatters.class,
            TestFractionPrinterParser.class,
            TestNumberParser.class,
            TestNumberPrinter.class,
            TestPadParserDecorator.class,
            TestPadPrinterDecorator.class,
            TestStringLiteralPrinter.class,
            TestStringLiteralParser.class,
            TestTextPrinter.class,
            TestTextParser.class,
            TestZoneOffsetPrinter.class,
            // i18n
            TestCopticChronology.class,
            TestCopticDate.class,
            // periods
            TestPeriod.class,
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
                    System.out.println(Utils.stackTrace(throwable, false)[0]);;
                }
            }
        });
        
        testng.run();
    }

}
