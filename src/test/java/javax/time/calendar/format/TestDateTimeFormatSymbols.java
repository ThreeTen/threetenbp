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
import static org.testng.Assert.assertSame;

import java.util.Arrays;
import java.util.Locale;

import org.testng.annotations.Test;

/**
 * Test DateTimeFormatSymbols.
 */
@Test
public class TestDateTimeFormatSymbols {

    public void test_getAvailableLocales() {
        Locale[] locales = DateTimeFormatSymbols.getAvailableLocales();
        assertEquals(locales.length > 0, true);
        assertEquals(Arrays.asList(locales).contains(Locale.US), true);
    }

    //-----------------------------------------------------------------------
    public void test_of_Locale() {
        DateTimeFormatSymbols loc1 = DateTimeFormatSymbols.of(Locale.CANADA);
        assertEquals(Locale.CANADA, loc1.getLocale());
        assertEquals(loc1.getZeroDigit(), '0');
        assertEquals(loc1.getPositiveSign(), '+');
        assertEquals(loc1.getNegativeSign(), '-');
        assertEquals(loc1.getDecimalSeparator(), '.');
    }

    public void test_of_Locale_cached() {
        DateTimeFormatSymbols loc1 = DateTimeFormatSymbols.of(Locale.CANADA);
        DateTimeFormatSymbols loc2 = DateTimeFormatSymbols.of(Locale.CANADA);
        assertSame(loc1, loc2);
    }

    //-----------------------------------------------------------------------
    public void test_ofDefaultLocale() {
        DateTimeFormatSymbols loc1 = DateTimeFormatSymbols.ofDefaultLocale();
        assertEquals(Locale.getDefault(), loc1.getLocale());
    }

    public void test_ofDefaultLocale_cached() {
        DateTimeFormatSymbols loc1 = DateTimeFormatSymbols.ofDefaultLocale();
        DateTimeFormatSymbols loc2 = DateTimeFormatSymbols.ofDefaultLocale();
        assertSame(loc1, loc2);
    }

    //-----------------------------------------------------------------------
    public void test_DEFAULT() {
        DateTimeFormatSymbols loc1 = DateTimeFormatSymbols.DEFAULT;
        assertEquals(Locale.ROOT, loc1.getLocale());
        assertEquals(loc1.getZeroDigit(), '0');
        assertEquals(loc1.getPositiveSign(), '+');
        assertEquals(loc1.getNegativeSign(), '-');
        assertEquals(loc1.getDecimalSeparator(), '.');
    }

    //-----------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
    public void test_convertToDigit_base() {
        DateTimeFormatSymbols base = DateTimeFormatSymbols.DEFAULT;
        assertEquals(base.convertToDigit('0'), 0);
        assertEquals(base.convertToDigit('1'), 1);
        assertEquals(base.convertToDigit('9'), 9);
        assertEquals(base.convertToDigit(' '), -1);
        assertEquals(base.convertToDigit('A'), -1);
    }

    public void test_convertToDigit_altered() {
        DateTimeFormatSymbols base = DateTimeFormatSymbols.DEFAULT.withZeroDigit('A');
        assertEquals(base.convertToDigit('A'), 0);
        assertEquals(base.convertToDigit('B'), 1);
        assertEquals(base.convertToDigit('J'), 9);
        assertEquals(base.convertToDigit(' '), -1);
        assertEquals(base.convertToDigit('0'), -1);
    }

    //-----------------------------------------------------------------------
    public void test_convertNumberToI18N_base() {
        DateTimeFormatSymbols base = DateTimeFormatSymbols.DEFAULT;
        assertEquals(base.convertNumberToI18N("134"), "134");
    }

    public void test_convertNumberToI18N_altered() {
        DateTimeFormatSymbols base = DateTimeFormatSymbols.DEFAULT.withZeroDigit('A');
        assertEquals(base.convertNumberToI18N("134"), "BDE");
    }

}
