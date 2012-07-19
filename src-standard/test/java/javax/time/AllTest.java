/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendrical.TestDateTimeAdjusters;
import javax.time.calendrical.TestDateTimeValueRange;
import javax.time.calendrical.TestJulianDayField;
import javax.time.calendrical.TestLocalDateTimeUnit;
import javax.time.calendrical.TestMonthDay;
import javax.time.calendrical.TestQuarterOfYear;
import javax.time.calendrical.TestYear;
import javax.time.calendrical.TestYearMonth;
import javax.time.chrono.*;
import javax.time.format.TestCalendricalPrintException;
import javax.time.format.TestCaseSensitivePrinterParser;
import javax.time.format.TestCharLiteralParser;
import javax.time.format.TestCharLiteralPrinter;
import javax.time.format.TestDateTimeFormatSymbols;
import javax.time.format.TestDateTimeFormatter;
import javax.time.format.TestDateTimeFormatterBuilder;
import javax.time.format.TestDateTimeFormatters;
import javax.time.format.TestDateTimeParseContext;
import javax.time.format.TestDateTimeTextPrinting;
import javax.time.format.TestFractionPrinterParser;
import javax.time.format.TestNumberParser;
import javax.time.format.TestNumberPrinter;
import javax.time.format.TestPadParserDecorator;
import javax.time.format.TestPadPrinterDecorator;
import javax.time.format.TestReducedParser;
import javax.time.format.TestReducedPrinter;
import javax.time.format.TestSimpleDateTimeTextProvider;
import javax.time.format.TestStrictLenientPrinterParser;
import javax.time.format.TestStringLiteralParser;
import javax.time.format.TestStringLiteralPrinter;
import javax.time.format.TestTextParser;
import javax.time.format.TestTextPrinter;
import javax.time.format.TestZoneIdParser;
import javax.time.format.TestZoneOffsetParser;
import javax.time.format.TestZoneOffsetPrinter;
import javax.time.zone.TestFixedZoneRules;
import javax.time.zone.TestStandardZoneRules;
import javax.time.zone.TestTZDBZoneRulesCompiler;
import javax.time.zone.TestZoneOffsetInfo;
import javax.time.zone.TestZoneOffsetTransition;
import javax.time.zone.TestZoneOffsetTransitionRule;
import javax.time.zone.TestZoneResolvers;
import javax.time.zone.TestZoneRulesBuilder;
import javax.time.zone.TestZoneRulesGroup;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.internal.Utils;

/**
 * Test class.
 */
public class AllTest {

    public static void main(String[] args) {
        TestNG testng = getTestSuite();
        testng.run();
    }

	static TestNG getTestSuite() {
		TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] {
            // main classes
            TestAmPm.class,
            TestClock.class,
            TestClock_Fixed.class,
            TestClock_Offset.class,
            TestClock_System.class,
            TestClock_Tick.class,
            TestDateTimes_implementation.class,
            TestDayOfWeek.class,
            TestDuration.class,
            TestInstant.class,
            TestLocalDate.class,
            TestLocalDateTime.class,
            TestLocalTime.class,
            TestMonth.class,
            TestOffsetDate.class,
            TestOffsetDateTime.class,
            TestOffsetDateTime_instants.class,
            TestOffsetTime.class,
            TestPeriod.class,
            TestZonedDateTime.class,
            TestZoneId.class,
            TestZoneOffset.class,
            // additional main classes
            TestMonthDay.class,
            TestYear.class,
            TestYearMonth.class,
            // advanced calendar classes
            TestDateTimeAdjusters.class,
            TestLocalDateTimeUnit.class,
            TestDateTimeValueRange.class,
            // additional calendrical classes
            TestJulianDayField.class,
            TestQuarterOfYear.class,
            // format
            TestCalendricalPrintException.class,
            TestDateTimeFormatSymbols.class,
            TestDateTimeFormatter.class,
            TestDateTimeFormatterBuilder.class,
            TestDateTimeFormatters.class,
            TestDateTimeParseContext.class,
            TestDateTimeTextPrinting.class,
            // format internal
            TestCaseSensitivePrinterParser.class,
            TestCharLiteralParser.class,
            TestCharLiteralPrinter.class,
            TestFractionPrinterParser.class,
            TestNumberParser.class,
            TestNumberPrinter.class,
            TestPadParserDecorator.class,
            TestPadPrinterDecorator.class,
            TestReducedParser.class,
            TestReducedPrinter.class,
            TestSimpleDateTimeTextProvider.class,
            TestStrictLenientPrinterParser.class,
            TestStringLiteralParser.class,
            TestStringLiteralPrinter.class,
            TestTextParser.class,
            TestTextPrinter.class,
            TestZoneIdParser.class,
            TestZoneOffsetParser.class,
            TestZoneOffsetPrinter.class,
            // zone
            TestFixedZoneRules.class,
            TestStandardZoneRules.class,
            TestTZDBZoneRulesCompiler.class,
            TestZoneOffsetInfo.class,
            TestZoneOffsetTransition.class,
            TestZoneOffsetTransitionRule.class,
            TestZoneResolvers.class,
            TestZoneRulesBuilder.class,
            TestZoneRulesGroup.class,
            TestChrono.class,
            TestCopticChronology.class,
            TestHijrahChronology.class,
            TestISOChronology.class,
            TestJapaneseChronology.class,
            TestMinguoChronology.class,
            TestThaiBuddhistChronology.class,
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
		return testng;
	}

}
