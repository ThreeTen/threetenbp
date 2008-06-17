/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.calendar.FlexiDateTime;
import javax.time.calendar.UnsupportedCalendarFieldException;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test SimpleNumberPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestSimpleNumberPrinter {

    private StringBuilder buf;
    private Appendable exceptionAppenable;
    private FlexiDateTime emptyDateTime;
    private Locale locale;

    @BeforeMethod
    public void setUp() {
        buf = new StringBuilder();
        exceptionAppenable = new MockIOExceptionAppendable();
        emptyDateTime = new FlexiDateTime(null, null, null, null, null);
        locale = Locale.ENGLISH;
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullAppendable() throws Exception {
        FlexiDateTime dt = new FlexiDateTime(null, null, null, null, null).withFieldValue(DayOfMonth.rule(), 3);
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), 1, 2, SignStyle.NEVER);
        pp.print((Appendable) null, dt, locale);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullDateTime() throws Exception {
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), 1, 2, SignStyle.NEVER);
        pp.print(buf, (FlexiDateTime) null, locale);
    }

// NPE is not required
//    @Test(expectedExceptions=NullPointerException.class)
//    public void test_print_nullLocale() throws Exception {
//        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser("hello");
//        pp.print(buf, emptyDateTime, (Locale) null);
//        assertEquals(buf, "EXISTINGhello");
//    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_print_emptyDateTime() throws Exception {
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), 1, 2, SignStyle.NEVER);
        pp.print(buf, emptyDateTime, locale);
    }

    public void test_print_append() throws Exception {
        FlexiDateTime dt = new FlexiDateTime(null, null, null, null, null).withFieldValue(DayOfMonth.rule(), 3);
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), 1, 2, SignStyle.NEVER);
        buf.append("EXISTING");
        pp.print(buf, dt, locale);
        assertEquals(buf.toString(), "EXISTING3");
    }

    @Test(expectedExceptions=IOException.class)
    public void test_print_appendIO() throws Exception {
        FlexiDateTime dt = new FlexiDateTime(null, null, null, null, null).withFieldValue(DayOfMonth.rule(), 3);
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), 1, 2, SignStyle.NEVER);
        pp.print(exceptionAppenable, dt, locale);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="Pad")
    Object[][] provider_pad() {
        return new Object[][] {
            {1, 1, -10, null},
            {1, 1, -9, "9"},
            {1, 1, -1, "1"},
            {1, 1, 0, "0"},
            {1, 1, 3, "3"},
            {1, 1, 9, "9"},
            {1, 1, 10, null},
            
            {1, 2, -100, null},
            {1, 2, -99, "99"},
            {1, 2, -10, "10"},
            {1, 2, -9, "9"},
            {1, 2, -1, "1"},
            {1, 2, 0, "0"},
            {1, 2, 3, "3"},
            {1, 2, 9, "9"},
            {1, 2, 10, "10"},
            {1, 2, 99, "99"},
            {1, 2, 100, null},
            
            {2, 2, -100, null},
            {2, 2, -99, "99"},
            {2, 2, -10, "10"},
            {2, 2, -9, "09"},
            {2, 2, -1, "01"},
            {2, 2, 0, "00"},
            {2, 2, 3, "03"},
            {2, 2, 9, "09"},
            {2, 2, 10, "10"},
            {2, 2, 99, "99"},
            {2, 2, 100, null},
            
            {1, 3, -1000, null},
            {1, 3, -999, "999"},
            {1, 3, -100, "100"},
            {1, 3, -99, "99"},
            {1, 3, -10, "10"},
            {1, 3, -9, "9"},
            {1, 3, -1, "1"},
            {1, 3, 0, "0"},
            {1, 3, 3, "3"},
            {1, 3, 9, "9"},
            {1, 3, 10, "10"},
            {1, 3, 99, "99"},
            {1, 3, 100, "100"},
            {1, 3, 999, "999"},
            {1, 3, 1000, null},
            
            {2, 3, -1000, null},
            {2, 3, -999, "999"},
            {2, 3, -100, "100"},
            {2, 3, -99, "99"},
            {2, 3, -10, "10"},
            {2, 3, -9, "09"},
            {2, 3, -1, "01"},
            {2, 3, 0, "00"},
            {2, 3, 3, "03"},
            {2, 3, 9, "09"},
            {2, 3, 10, "10"},
            {2, 3, 99, "99"},
            {2, 3, 100, "100"},
            {2, 3, 999, "999"},
            {2, 3, 1000, null},
            
            {3, 3, -1000, null},
            {3, 3, -999, "999"},
            {3, 3, -100, "100"},
            {3, 3, -99, "099"},
            {3, 3, -10, "010"},
            {3, 3, -9, "009"},
            {3, 3, -1, "001"},
            {3, 3, 0, "000"},
            {3, 3, 3, "003"},
            {3, 3, 9, "009"},
            {3, 3, 10, "010"},
            {3, 3, 99, "099"},
            {3, 3, 100, "100"},
            {3, 3, 999, "999"},
            {3, 3, 1000, null},
            
            {1, 10, Integer.MAX_VALUE - 1, "2147483646"},
            {1, 10, Integer.MAX_VALUE, "2147483647"},
            {1, 10, Integer.MIN_VALUE + 1, "2147483647"},
            {1, 10, Integer.MIN_VALUE, "2147483648"},
       };
    }

    @Test(dataProvider="Pad") 
    public void test_pad_NEGATIVE_ERROR(int minPad, int maxPad, int value, String result) throws Exception {
        FlexiDateTime dt = new FlexiDateTime(null, null, null, null, null).withFieldValue(DayOfMonth.rule(), value);
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), minPad, maxPad, SignStyle.NEGATIVE_ERROR);
        try {
            pp.print(buf, dt, locale);
            if (result == null || value < 0) {
                fail("Expected exception");
            }
            assertEquals(buf.toString(), result);
        } catch (CalendricalFormatFieldException ex) {
            if (result == null || value < 0) {
                assertEquals(ex.getFieldRule(), DayOfMonth.rule());
                assertEquals(ex.getValue(), (Integer) value);
            } else {
                throw ex;
            }
        }
    }

    @Test(dataProvider="Pad") 
    public void test_pad_NEVER(int minPad, int maxPad, int value, String result) throws Exception {
        FlexiDateTime dt = new FlexiDateTime(null, null, null, null, null).withFieldValue(DayOfMonth.rule(), value);
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), minPad, maxPad, SignStyle.NEVER);
        try {
            pp.print(buf, dt, locale);
            if (result == null) {
                fail("Expected exception");
            }
            assertEquals(buf.toString(), result);
        } catch (CalendricalFormatFieldException ex) {
            if (result != null) {
                throw ex;
            }
            assertEquals(ex.getFieldRule(), DayOfMonth.rule());
            assertEquals(ex.getValue(), (Integer) value);
        }
    }

    @Test(dataProvider="Pad") 
    public void test_pad_NORMAL(int minPad, int maxPad, int value, String result) throws Exception {
        FlexiDateTime dt = new FlexiDateTime(null, null, null, null, null).withFieldValue(DayOfMonth.rule(), value);
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), minPad, maxPad, SignStyle.NORMAL);
        try {
            pp.print(buf, dt, locale);
            if (result == null) {
                fail("Expected exception");
            }
            assertEquals(buf.toString(), (value < 0 ? "-" + result : result));
        } catch (CalendricalFormatFieldException ex) {
            if (result != null) {
                throw ex;
            }
            assertEquals(ex.getFieldRule(), DayOfMonth.rule());
            assertEquals(ex.getValue(), (Integer) value);
        }
    }

    @Test(dataProvider="Pad") 
    public void test_pad_ALWAYS(int minPad, int maxPad, int value, String result) throws Exception {
        FlexiDateTime dt = new FlexiDateTime(null, null, null, null, null).withFieldValue(DayOfMonth.rule(), value);
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), minPad, maxPad, SignStyle.ALWAYS);
        try {
            pp.print(buf, dt, locale);
            if (result == null) {
                fail("Expected exception");
            }
            assertEquals(buf.toString(), (value < 0 ? "-" + result : "+" + result));
        } catch (CalendricalFormatFieldException ex) {
            if (result != null) {
                throw ex;
            }
            assertEquals(ex.getFieldRule(), DayOfMonth.rule());
            assertEquals(ex.getValue(), (Integer) value);
        }
    }

    @Test(dataProvider="Pad") 
    public void test_pad_EXCEEDS_PAD(int minPad, int maxPad, int value, String result) throws Exception {
        FlexiDateTime dt = new FlexiDateTime(null, null, null, null, null).withFieldValue(DayOfMonth.rule(), value);
        SimpleNumberPrinterParser pp = new SimpleNumberPrinterParser(DayOfMonth.rule(), minPad, maxPad, SignStyle.EXCEEDS_PAD);
        try {
            pp.print(buf, dt, locale);
            if (result == null) {
                fail("Expected exception");
            }
            if (result.length() > minPad || value < 0) {
                result = (value < 0 ? "-" + result : "+" + result);
            }
            assertEquals(buf.toString(), result);
        } catch (CalendricalFormatFieldException ex) {
            if (result != null) {
                throw ex;
            }
            assertEquals(ex.getFieldRule(), DayOfMonth.rule());
            assertEquals(ex.getValue(), (Integer) value);
        }
    }

}
