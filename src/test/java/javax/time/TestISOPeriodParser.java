/*
 * Copyright (c) 2009-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.LocalPeriodUnit.DAYS;
import static javax.time.calendrical.LocalPeriodUnit.HOURS;
import static javax.time.calendrical.LocalPeriodUnit.MINUTES;
import static javax.time.calendrical.LocalPeriodUnit.MONTHS;
import static javax.time.calendrical.LocalPeriodUnit.NANOS;
import static javax.time.calendrical.LocalPeriodUnit.SECONDS;
import static javax.time.calendrical.LocalPeriodUnit.YEARS;
import static org.testng.Assert.assertEquals;

import javax.time.format.DateTimeParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test PeriodParser.
 */
@Test 
public class TestISOPeriodParser {

    //-----------------------------------------------------------------------
    // parse(String)
    //-----------------------------------------------------------------------
    @DataProvider(name="Parse")
    Object[][] provider_factory_parse() {
        return new Object[][] {
            {"Pt0S", ISOPeriod.ZERO},
            {"pT0S", ISOPeriod.ZERO},
            {"PT0S", ISOPeriod.ZERO},
            {"Pt0s", ISOPeriod.ZERO},
            {"pt0s", ISOPeriod.ZERO},
            {"P0Y0M0DT0H0M0.0S", ISOPeriod.ZERO},
            
            {"P1Y", ISOPeriod.of(1, YEARS)},
            {"P100Y", ISOPeriod.of(100, YEARS)},
            {"P-25Y", ISOPeriod.of(-25, YEARS)},
            {"P" + Integer.MAX_VALUE + "Y", ISOPeriod.of(Integer.MAX_VALUE, YEARS)},
            {"P" + Integer.MIN_VALUE + "Y", ISOPeriod.of(Integer.MIN_VALUE, YEARS)},
            
            {"P1M", ISOPeriod.of(1, MONTHS)},
            {"P0M", ISOPeriod.of(0, MONTHS)},
            {"P-1M", ISOPeriod.of(-1, MONTHS)},
            {"P" + Integer.MAX_VALUE + "M", ISOPeriod.of(Integer.MAX_VALUE, MONTHS)},
            {"P" + Integer.MIN_VALUE + "M", ISOPeriod.of(Integer.MIN_VALUE, MONTHS)},
            
            {"P1D", ISOPeriod.of(1, DAYS)},
            {"P0D", ISOPeriod.of(0, DAYS)},
            {"P-1D", ISOPeriod.of(-1, DAYS)},
            {"P" + Integer.MAX_VALUE + "D", ISOPeriod.of(Integer.MAX_VALUE, DAYS)},
            {"P" + Integer.MIN_VALUE + "D", ISOPeriod.of(Integer.MIN_VALUE, DAYS)},
            
            {"P2Y3M25D", ISOPeriod.ofDate(2, 3, 25)},
            
            {"PT1H", ISOPeriod.of(1, HOURS)},
            {"PT-1H", ISOPeriod.of(-1, HOURS)},
            {"PT24H", ISOPeriod.of(24, HOURS)},
            {"PT-24H", ISOPeriod.of(-24, HOURS)},
            {"PT" + Integer.MAX_VALUE + "H", ISOPeriod.of(Integer.MAX_VALUE, HOURS)},
            {"PT" + Integer.MIN_VALUE + "H", ISOPeriod.of(Integer.MIN_VALUE, HOURS)},
            
            {"PT1M", ISOPeriod.of(1, MINUTES)},
            {"PT-1M", ISOPeriod.of(-1, MINUTES)},
            {"PT60M", ISOPeriod.of(60, MINUTES)},
            {"PT-60M", ISOPeriod.of(-60, MINUTES)},
            {"PT" + Integer.MAX_VALUE + "M", ISOPeriod.of(Integer.MAX_VALUE, MINUTES)},
            {"PT" + Integer.MIN_VALUE + "M", ISOPeriod.of(Integer.MIN_VALUE, MINUTES)},
            
            {"PT1S", ISOPeriod.of(1, SECONDS)},
            {"PT-1S", ISOPeriod.of(-1, SECONDS)},
            {"PT60S", ISOPeriod.of(60, SECONDS)},
            {"PT-60S", ISOPeriod.of(-60, SECONDS)},
            {"PT" + Integer.MAX_VALUE + "S", ISOPeriod.of(Integer.MAX_VALUE, SECONDS)},
            {"PT" + Integer.MIN_VALUE + "S", ISOPeriod.of(Integer.MIN_VALUE, SECONDS)},
            
            {"PT0.1S", ISOPeriod.of( 0, 0, 0, 0, 0, 0, 100000000 ) },
            {"PT-0.1S", ISOPeriod.of( 0, 0, 0, 0, 0, 0, -100000000 ) },
            {"PT1.1S", ISOPeriod.of( 0, 0, 0, 0, 0, 1, 100000000 ) },
            {"PT-1.1S", ISOPeriod.of( 0, 0, 0, 0, 0, -1, -100000000 ) },
            {"PT1.0001S", ISOPeriod.of(1, SECONDS).plus( 100000, NANOS ) },
            {"PT1.0000001S", ISOPeriod.of(1, SECONDS).plus( 100, NANOS ) },
            {"PT1.123456789S", ISOPeriod.of( 0, 0, 0, 0, 0, 1, 123456789 ) },
            {"PT1.999999999S", ISOPeriod.of( 0, 0, 0, 0, 0, 1, 999999999 ) },

        };
    }

    @Test(dataProvider="Parse")
    public void factory_parse(String text, ISOPeriod expected) {
    	ISOPeriod p = ISOPeriod.parse(text);
        assertEquals(p, expected);
    }

    @Test(dataProvider="Parse")
    public void factory_parse_comma(String text, ISOPeriod expected) {
    	if (text.contains(".")) {
    		text = text.replace('.', ',');
    		ISOPeriod p = ISOPeriod.parse(text);
        	assertEquals(p, expected);
    	}
    }

    @DataProvider(name="ParseFailures")
    Object[][] provider_factory_parseFailures() {
        return new Object[][] {
            {"", 0},
            {"PTS", 2},
            {"AT0S", 0},
            {"PA0S", 1},
            {"PT0A", 3},
            
            {"PT+S", 2},
            {"PT-S", 2},
            {"PT.S", 2},
            {"PTAS", 2},
            
            {"PT+0S", 2},
            {"PT-0S", 2},
            {"PT+1S", 2},
            {"PT-.S", 2},
            
            {"PT1ABC2S", 3},
            {"PT1.1ABC2S", 5},
            
            {"PT123456789123456789123456789S", 2},
            {"PT0.1234567891S", 4},
            {"PT1.S", 2},
            {"PT.1S", 2},
            
            {"PT2.-3S", 2},
            {"PT-2.-3S", 2},
            
            {"P1Y1MT1DT1M1S", 7},
            {"P1Y1MT1HT1M1S", 8},
            {"P1YMD", 3},
            {"PT1ST1D", 4},
            {"P1Y2Y", 4},
            {"PT1M+3S", 4},
            
            {"PT1S1", 4},
            {"PT1S.", 4},
            {"PT1SA", 4},
            {"PT1M1", 4},
            {"PT1M.", 4},
            {"PT1MA", 4},
        };
    }

    @Test(dataProvider="ParseFailures", expectedExceptions=DateTimeParseException.class)
    public void factory_parseFailures(String text, int errPos) {
        try {
            ISOPeriod.parse(text);
        } catch (DateTimeParseException ex) {
            assertEquals(ex.getParsedString(), text);
            assertEquals(ex.getErrorIndex(), errPos);
            System.out.println(ex.toString());
            throw ex;
        }
    }

    @Test(dataProvider="ParseFailures", expectedExceptions=DateTimeParseException.class)
    public void factory_parseFailures_comma(String text, int errPos) {
        text = text.replace('.', ',');
        try {
            ISOPeriod.parse(text);
        } catch (DateTimeParseException ex) {
            assertEquals(ex.getParsedString(), text);
            assertEquals(ex.getErrorIndex(), errPos);
            throw ex;
        }
    }

    @Test(expectedExceptions=DateTimeParseException.class)
    public void factory_parse_tooBig() {
    	String text = "PT" + Long.MAX_VALUE + "1S";
    	ISOPeriod.parse(text);
    }

    @Test(expectedExceptions=DateTimeParseException.class)
    public void factory_parse_tooBig_decimal() {
    	String text = "PT" + Long.MAX_VALUE + "1.1S";
    	ISOPeriod.parse(text);
    }

    @Test(expectedExceptions=DateTimeParseException.class)
    public void factory_parse_tooSmall() {
        String text = "PT" + Long.MIN_VALUE + "1S";
        ISOPeriod.parse(text);
    }

    @Test(expectedExceptions=DateTimeParseException.class)
    public void factory_parse_tooSmall_decimal() {
        String text = "PT" + Long.MIN_VALUE + ".1S";
        ISOPeriod.parse(text);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_null() {
    	ISOPeriod.parse(null);
    }

    @DataProvider(name="ParseSequenceFailures")
    Object[][] provider_factory_parseSequenceFailures() {
        return new Object[][] {
        	{"P0M0Y0DT0H0M0.0S"},
        	{"P0M0D0YT0H0M0.0S"},
        	{"P0S0D0YT0S0M0.0H"},
        	{"PT0M0H0.0S"},
        	{"PT0M0H"},
        	{"PT0S0M"},
        	{"PT0.0M2S"},
        };
    }

    @Test(dataProvider="ParseSequenceFailures", expectedExceptions=DateTimeParseException.class)
    public void factory_parse_badSequence(String text) {
    	ISOPeriod.parse(text);
    }

}
