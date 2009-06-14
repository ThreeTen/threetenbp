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

import java.util.Locale;
import java.util.Map;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.TimeZone;
import javax.time.calendar.UnsupportedCalendarFieldException;
import javax.time.calendar.ZoneOffset;

import org.testng.annotations.Test;

/**
 * Test DateTimeParseContext.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDateTimeParseContext {

    private static final DateTimeFieldRule RULE_YEAR = ISOChronology.yearRule();
    private static final DateTimeFieldRule RULE_MOY = ISOChronology.monthOfYearRule();
    private static final DateTimeFieldRule RULE_DOM = ISOChronology.dayOfMonthRule();

//    @BeforeMethod
//    public void setUp() {
//    }

    //-----------------------------------------------------------------------
    public void test_constructor() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        assertEquals(test.getSymbols(), symbols);
        assertEquals(test.getLocale(), Locale.GERMANY);
        assertEquals(test.toCalendrical().getFieldMap().size(), 0);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_constructor_null() throws Exception {
        new DateTimeParseContext(null);
    }

    //-----------------------------------------------------------------------
    public void test_caseSensitive() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        assertEquals(test.isCaseSensitive(), true);
        
        test.setCaseSensitive(false);
        
        assertEquals(test.isCaseSensitive(), false);
    }

    //-----------------------------------------------------------------------
    public void test_strict() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        assertEquals(test.isStrict(), true);
        
        test.setStrict(false);
        
        assertEquals(test.isStrict(), false);
    }

    //-----------------------------------------------------------------------
    public void test_fields_oneField() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        assertEquals(test.toCalendrical().getFieldMap().size(), 0);
        
        test.setFieldValue(RULE_YEAR, 2008);
        
        assertEquals(test.getFieldValue(RULE_YEAR), 2008);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
        Map<DateTimeFieldRule, Integer> map = test.toCalendrical().getFieldMap().toFieldValueMap();
        assertEquals(map.size(), 1);
        assertEquals(map.get(RULE_YEAR), Integer.valueOf(2008));
        //  test cloned and modifiable
        map.clear();
        assertEquals(map.size(), 0);
        assertEquals(test.getFieldValue(RULE_YEAR), 2008);
    }

    public void test_fields_twoFields() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        assertEquals(test.toCalendrical().getFieldMap().size(), 0);
        
        test.setFieldValue(RULE_YEAR, 2008);
        test.setFieldValue(RULE_MOY, 6);
        
        assertEquals(test.toCalendrical().getFieldMap().size(), 2);
        assertEquals(test.getFieldValue(RULE_YEAR), 2008);
        assertEquals(test.getFieldValue(RULE_MOY), 6);
        assertEquals(test.getOffset(), null);
        assertEquals(test.getZone(), null);
        Map<DateTimeFieldRule, Integer> map = test.toCalendrical().getFieldMap().toFieldValueMap();
        assertEquals(map.size(), 2);
        assertEquals(map.get(RULE_YEAR), Integer.valueOf(2008));
        assertEquals(map.get(RULE_MOY), Integer.valueOf(6));
        //  test cloned and modifiable
        map.clear();
        assertEquals(map.size(), 0);
        assertEquals(test.getFieldValue(RULE_YEAR), 2008);
        assertEquals(test.getFieldValue(RULE_MOY), 6);
    }

    public void test_fields_oneField_derive() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        assertEquals(test.toCalendrical().getFieldMap().size(), 0);
        
        test.setFieldValue(RULE_MOY, 6);
        
        assertEquals(test.toCalendrical().getFieldMap().size(), 1);
        assertEquals(test.getFieldValue(RULE_MOY), 6);
        
        assertEquals(test.deriveFieldValue(ISOChronology.monthOfQuarterRule()), 3);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_fields_getNull() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        test.getFieldValue(null);
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_fields_get_notPresent() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        try {
            test.getFieldValue(RULE_DOM);
        } catch (UnsupportedCalendarFieldException ex) {
            assertEquals(ex.getFieldRule(), RULE_DOM);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_fields_setNull() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        test.setFieldValue(null, 2008);
    }

    //-----------------------------------------------------------------------
    public void test_offset() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        assertEquals(test.getOffset(), null);
        
        test.setOffset(ZoneOffset.zoneOffset(18));
        
        assertEquals(test.getOffset(), ZoneOffset.zoneOffset(18));
        assertEquals(test.toCalendrical().getFieldMap().size(), 0);
        assertEquals(test.getZone(), null);
    }

    //-----------------------------------------------------------------------
    public void test_zone() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        assertEquals(test.getZone(), null);
        
        test.setZone(TimeZone.timeZone(ZoneOffset.zoneOffset(18)));
        
        assertEquals(test.getZone(), TimeZone.timeZone(ZoneOffset.zoneOffset(18)));
        assertEquals(test.toCalendrical().getFieldMap().size(), 0);
        assertEquals(test.getOffset(), null);
    }

    //-----------------------------------------------------------------------
    public void test_toCalendrical() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        test.setFieldValue(RULE_YEAR, 2008);
        test.setFieldValue(RULE_MOY, 6);
        test.setOffset(ZoneOffset.zoneOffset(16));
        test.setZone(TimeZone.timeZone(ZoneOffset.zoneOffset(18)));
        
        Calendrical cal = test.toCalendrical();
        
        assertEquals(cal.getFieldMap(), test.toCalendrical().getFieldMap());
        assertEquals(cal.getOffset(), test.getOffset());
        assertEquals(cal.getZone(), test.getZone());
    }

    //-----------------------------------------------------------------------
    public void test_toString() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        test.setFieldValue(RULE_YEAR, 2008);
        test.setFieldValue(RULE_MOY, 6);
        test.setOffset(ZoneOffset.zoneOffset(16));
        test.setZone(TimeZone.timeZone(ZoneOffset.zoneOffset(18)));
        
        assertEquals(test.toString(), "{ISO.Year=2008, ISO.MonthOfYear=6} +16:00 UTC+18:00");
    }

    public void test_toString_empty() throws Exception {
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(Locale.GERMANY);
        DateTimeParseContext test = new DateTimeParseContext(symbols);
        
        assertEquals(test.toString(), "");
    }

}
