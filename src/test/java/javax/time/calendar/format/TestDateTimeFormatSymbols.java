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
package javax.time.calendar.format;

import static org.testng.Assert.assertEquals;

import java.util.Locale;

import javax.time.calendar.DayOfWeek;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DateTimeLocaleInfo.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDateTimeFormatSymbols {

    //-----------------------------------------------------------------------
    @DataProvider(name="localeFirstDay")
    Object[][] localeFirstDay() {
        return new Object[][] {
            {Locale.FRANCE, DayOfWeek.MONDAY},
            // {new Locale("pt", "BR"), DayOfWeek.SUNDAY},  // JDK 6 has Monday
            {Locale.US,     DayOfWeek.SUNDAY},
        };
    }

    @Test(dataProvider="localeFirstDay")
    public void test_getFirstDayOfWeek(Locale locale, DayOfWeek first) {
        assertEquals(DateTimeFormatSymbols.of(locale).getFirstDayOfWeek(), first);
    }

    public void test_withFirstDayOfWeek() {
        DateTimeFormatSymbols base = DateTimeFormatSymbols.DEFAULT;
        assertEquals(base.withFirstDayOfWeek(DayOfWeek.THURSDAY).getFirstDayOfWeek(), DayOfWeek.THURSDAY);
    }

    public void test_zeroDigit() {
        DateTimeFormatSymbols base = DateTimeFormatSymbols.DEFAULT;
        assertEquals(base.withZeroDigit('A').getZeroDigit(), 'A');
    }

    public void test_positiveSign() {
        DateTimeFormatSymbols base = DateTimeFormatSymbols.DEFAULT;
        assertEquals(base.withPositiveSign('A').getPositiveSign(), 'A');
    }

    public void test_negativeSign() {
        DateTimeFormatSymbols base = DateTimeFormatSymbols.DEFAULT;
        assertEquals(base.withNegativeSign('A').getNegativeSign(), 'A');
    }

    public void test_decimalSeparator() {
        DateTimeFormatSymbols base = DateTimeFormatSymbols.DEFAULT;
        assertEquals(base.withDecimalSeparator('A').getDecimalSeparator(), 'A');
    }

}
