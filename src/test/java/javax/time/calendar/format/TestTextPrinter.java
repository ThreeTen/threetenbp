/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.DateTimeFields;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.UnsupportedRuleException;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test TextPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestTextPrinter {

    private static final DateTimeFieldRule<DayOfWeek> RULE_DOW = ISOChronology.dayOfWeekRule();
    private static final DateTimeFieldRule<Integer> RULE_DOM = ISOChronology.dayOfMonthRule();
    private static final DateTimeFieldRule<MonthOfYear> RULE_MOY = ISOChronology.monthOfYearRule();

    private StringBuilder buf;
    private Appendable exceptionAppenable;
    private Calendrical emptyCalendrical;
    private DateTimeFormatSymbols symbols;

    @BeforeMethod
    public void setUp() {
        buf = new StringBuilder();
        exceptionAppenable = new MockIOExceptionAppendable();
        emptyCalendrical = DateTimeFields.fields();
        symbols = DateTimeFormatSymbols.getInstance(Locale.ENGLISH);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullAppendable() throws Exception {
        Calendrical calendrical = DateTimeFields.fields(RULE_DOW, 3);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        pp.print(calendrical, (Appendable) null, symbols);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullDateTime() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        pp.print((Calendrical) null, buf, symbols);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullSymbols() throws Exception {
        Calendrical calendrical = DateTimeFields.fields(RULE_DOW, 3);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        pp.print(calendrical, buf, null);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=UnsupportedRuleException.class)
    public void test_print_emptyCalendrical() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        pp.print(emptyCalendrical, buf, symbols);
    }

    public void test_print_append() throws Exception {
        Calendrical calendrical = DateTimeFields.fields(RULE_DOW, 3);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        buf.append("EXISTING");
        pp.print(calendrical, buf, symbols);
        assertEquals(buf.toString(), "EXISTINGWednesday");
    }

    @Test(expectedExceptions=IOException.class)
    public void test_print_appendIO() throws Exception {
        Calendrical calendrical = DateTimeFields.fields(RULE_DOW, 3);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        pp.print(calendrical, exceptionAppenable, symbols);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="print")
    Object[][] provider_dow() {
        return new Object[][] {
            {RULE_DOW, TextStyle.FULL, 1, "Monday"},
            {RULE_DOW, TextStyle.FULL, 2, "Tuesday"},
            {RULE_DOW, TextStyle.FULL, 3, "Wednesday"},
            {RULE_DOW, TextStyle.FULL, 4, "Thursday"},
            {RULE_DOW, TextStyle.FULL, 5, "Friday"},
            {RULE_DOW, TextStyle.FULL, 6, "Saturday"},
            {RULE_DOW, TextStyle.FULL, 7, "Sunday"},
            
            {RULE_DOW, TextStyle.SHORT, 1, "Mon"},
            {RULE_DOW, TextStyle.SHORT, 2, "Tue"},
            {RULE_DOW, TextStyle.SHORT, 3, "Wed"},
            {RULE_DOW, TextStyle.SHORT, 4, "Thu"},
            {RULE_DOW, TextStyle.SHORT, 5, "Fri"},
            {RULE_DOW, TextStyle.SHORT, 6, "Sat"},
            {RULE_DOW, TextStyle.SHORT, 7, "Sun"},
            
            {RULE_DOM, TextStyle.FULL, 1, "1"},
            {RULE_DOM, TextStyle.FULL, 2, "2"},
            {RULE_DOM, TextStyle.FULL, 3, "3"},
            {RULE_DOM, TextStyle.FULL, 28, "28"},
            {RULE_DOM, TextStyle.FULL, 29, "29"},
            {RULE_DOM, TextStyle.FULL, 30, "30"},
            {RULE_DOM, TextStyle.FULL, 31, "31"},
            
            {RULE_DOM, TextStyle.SHORT, 1, "1"},
            {RULE_DOM, TextStyle.SHORT, 2, "2"},
            {RULE_DOM, TextStyle.SHORT, 3, "3"},
            {RULE_DOM, TextStyle.SHORT, 28, "28"},
            {RULE_DOM, TextStyle.SHORT, 29, "29"},
            {RULE_DOM, TextStyle.SHORT, 30, "30"},
            {RULE_DOM, TextStyle.SHORT, 31, "31"},
            
            {RULE_MOY, TextStyle.FULL, 1, "January"},
            {RULE_MOY, TextStyle.FULL, 12, "December"},
            
            {RULE_MOY, TextStyle.SHORT, 1, "Jan"},
            {RULE_MOY, TextStyle.SHORT, 12, "Dec"},
       };
    }

    @Test(dataProvider="print") 
    public void test_print(DateTimeFieldRule<?> rule, TextStyle style, int dow, String expected) throws Exception {
        Calendrical calendrical = DateTimeFields.fields(rule, dow);
        TextPrinterParser pp = new TextPrinterParser(rule, style);
        pp.print(calendrical, buf, symbols);
        assertEquals(buf.toString(), expected);
    }

    //-----------------------------------------------------------------------
    public void test_isPrintDataAvailable_true() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(RULE_MOY, TextStyle.FULL);
        assertEquals(pp.isPrintDataAvailable(DateTimeFields.fields(RULE_MOY, 4)), true);
    }

    public void test_isPrintDataAvailable_false() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(RULE_MOY, TextStyle.FULL);
        assertEquals(pp.isPrintDataAvailable(DateTimeFields.fields()), false);
    }

    //-----------------------------------------------------------------------
    public void test_toString1() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(RULE_MOY, TextStyle.FULL);
        assertEquals(pp.toString(), "Text(ISO.MonthOfYear)");
    }

    public void test_toString2() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(RULE_MOY, TextStyle.SHORT);
        assertEquals(pp.toString(), "Text(ISO.MonthOfYear,SHORT)");
    }

}
