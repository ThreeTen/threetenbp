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

import javax.time.calendar.TestCalendrical;
import javax.time.calendar.TestDateAdjustors;
import javax.time.calendar.TestDateMatchers;
import javax.time.calendar.TestDateResolvers;
import javax.time.calendar.TestDateTimeFields;
import javax.time.calendar.TestISOChronology;
import javax.time.calendar.TestLocalDate;
import javax.time.calendar.TestLocalDateTime;
import javax.time.calendar.TestLocalTime;
import javax.time.calendar.TestOffsetDate;
import javax.time.calendar.TestOffsetDateTime;
import javax.time.calendar.TestOffsetDateTime_instants;
import javax.time.calendar.TestOffsetTime;
import javax.time.calendar.TestZoneOffset;
import javax.time.calendar.TestZonedDateTime;
import javax.time.calendar.field.TestDayOfMonth;
import javax.time.calendar.field.TestDayOfMonthRule;
import javax.time.calendar.field.TestDayOfWeek;
import javax.time.calendar.field.TestDayOfYear;
import javax.time.calendar.field.TestMonthOfYear;
import javax.time.calendar.format.TestCharLiteralParser;
import javax.time.calendar.format.TestCharLiteralPrinter;
import javax.time.calendar.format.TestDateTimeFormatters;
import javax.time.calendar.format.TestFractionPrinterParser;
import javax.time.calendar.format.TestNumberPrinter;
import javax.time.calendar.format.TestPadPrinterDecorator;
import javax.time.calendar.format.TestStringLiteralParser;
import javax.time.calendar.format.TestStringLiteralPrinter;
import javax.time.calendar.i18n.TestCopticDate;
import javax.time.period.field.TestDays;
import javax.time.period.field.TestHours;
import javax.time.period.field.TestMinutes;
import javax.time.period.field.TestMonths;
import javax.time.period.field.TestSeconds;
import javax.time.period.field.TestWeeks;
import javax.time.period.field.TestYears;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

/**
 * Test class.
 * 
 * @author Stephen Colebourne
 */
public class AllTest {

    public static void main(String[] args) {
        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] {
            // main classes
            TestClock.class,
            TestDuration.class,
            TestInstant.class,
            TestMathUtils.class,
            // calendar classes
            TestCalendrical.class,
            TestDateAdjustors.class,
            TestDateMatchers.class,
            TestDateResolvers.class,
            TestDateTimeFields.class,
            TestISOChronology.class,
            TestLocalDate.class,
            TestLocalDateTime.class,
            TestLocalTime.class,
            TestOffsetDate.class,
            TestOffsetDateTime.class,
            TestOffsetDateTime_instants.class,
            TestOffsetTime.class,
            TestZonedDateTime.class,
            TestZoneOffset.class,
            // calendar fields
            TestDayOfMonth.class,
            TestDayOfWeek.class,
            TestDayOfYear.class,
            TestMonthOfYear.class,
            // calendar rules
            TestDayOfMonthRule.class,
            // format
            TestDateTimeFormatters.class,
            TestCharLiteralParser.class,
            TestCharLiteralPrinter.class,
            TestDateTimeFormatters.class,
            TestFractionPrinterParser.class,
            TestNumberPrinter.class,
            TestPadPrinterDecorator.class,
            TestStringLiteralPrinter.class,
            TestStringLiteralParser.class,
            // i18n
            TestCopticDate.class,
            // period fields
            TestDays.class,
            TestHours.class,
            TestMinutes.class,
            TestMonths.class,
            TestSeconds.class,
            TestWeeks.class,
            TestYears.class,
        });
        testng.addListener(tla);
        testng.run();
    }

}
