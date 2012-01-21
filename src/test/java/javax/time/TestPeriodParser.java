/*
 * Copyright (c) 2009-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.assertEquals;

import javax.time.Period;
import javax.time.PeriodParser;
import javax.time.format.CalendricalParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test PeriodParser.
 *
 * @author Darryl West
 * @author Stephen Colebourne
 */
@Test 
public class TestPeriodParser {

    //-----------------------------------------------------------------------
    // parse(String)
    //-----------------------------------------------------------------------
    @DataProvider(name="Parse")
    Object[][] provider_factory_parse() {
        return new Object[][] {
            {"Pt0S", Period.ZERO},
            {"pT0S", Period.ZERO},
            {"PT0S", Period.ZERO},
            {"Pt0s", Period.ZERO},
            {"pt0s", Period.ZERO},
            {"P0Y0M0DT0H0M0.0S", Period.ZERO},
            
            {"P1Y", Period.ofYears(1)},
            {"P100Y", Period.ofYears(100)},
            {"P-25Y", Period.ofYears(-25)},
            {"P" + Integer.MAX_VALUE + "Y", Period.ofYears(Integer.MAX_VALUE)},
            {"P" + Integer.MIN_VALUE + "Y", Period.ofYears(Integer.MIN_VALUE)},
            
            {"P1M", Period.ofMonths(1)},
            {"P0M", Period.ofMonths(0)},
            {"P-1M", Period.ofMonths(-1)},
            {"P" + Integer.MAX_VALUE + "M", Period.ofMonths(Integer.MAX_VALUE)},
            {"P" + Integer.MIN_VALUE + "M", Period.ofMonths(Integer.MIN_VALUE)},
            
            {"P1D", Period.ofDays(1)},
            {"P0D", Period.ofDays(0)},
            {"P-1D", Period.ofDays(-1)},
            {"P" + Integer.MAX_VALUE + "D", Period.ofDays(Integer.MAX_VALUE)},
            {"P" + Integer.MIN_VALUE + "D", Period.ofDays(Integer.MIN_VALUE)},
            
            {"P2Y3M25D", Period.ofDateFields(2, 3, 25)},
            
            {"PT1H", Period.ofHours(1)},
            {"PT-1H", Period.ofHours(-1)},
            {"PT24H", Period.ofHours(24)},
            {"PT-24H", Period.ofHours(-24)},
            {"PT" + Integer.MAX_VALUE + "H", Period.ofHours(Integer.MAX_VALUE)},
            {"PT" + Integer.MIN_VALUE + "H", Period.ofHours(Integer.MIN_VALUE)},
            
            {"PT1M", Period.ofMinutes(1)},
            {"PT-1M", Period.ofMinutes(-1)},
            {"PT60M", Period.ofMinutes(60)},
            {"PT-60M", Period.ofMinutes(-60)},
            {"PT" + Integer.MAX_VALUE + "M", Period.ofMinutes(Integer.MAX_VALUE)},
            {"PT" + Integer.MIN_VALUE + "M", Period.ofMinutes(Integer.MIN_VALUE)},
            
            {"PT1S", Period.ofSeconds(1)},
            {"PT-1S", Period.ofSeconds(-1)},
            {"PT60S", Period.ofSeconds(60)},
            {"PT-60S", Period.ofSeconds(-60)},
            {"PT" + Integer.MAX_VALUE + "S", Period.ofSeconds(Integer.MAX_VALUE)},
            {"PT" + Integer.MIN_VALUE + "S", Period.ofSeconds(Integer.MIN_VALUE)},
            
            {"PT0.1S", Period.of( 0, 0, 0, 0, 0, 0, 100000000 ) },
            {"PT-0.1S", Period.of( 0, 0, 0, 0, 0, 0, -100000000 ) },
            {"PT1.1S", Period.of( 0, 0, 0, 0, 0, 1, 100000000 ) },
            {"PT-1.1S", Period.of( 0, 0, 0, 0, 0, -1, -100000000 ) },
            {"PT1.0001S", Period.ofSeconds(1).plusNanos( 100000 ) },
            {"PT1.0000001S", Period.ofSeconds(1).plusNanos( 100 ) },
            {"PT1.123456789S", Period.of( 0, 0, 0, 0, 0, 1, 123456789 ) },
            {"PT1.999999999S", Period.of( 0, 0, 0, 0, 0, 1, 999999999 ) },

        };
    }

    @Test(dataProvider="Parse")
    public void factory_parse(String text, Period expected) {
    	Period p = new PeriodParser(text).parse();
        assertEquals(p, expected);
    }

    @Test(dataProvider="Parse")
    public void factory_parse_comma(String text, Period expected) {
    	if (text.contains(".")) {
    		text = text.replace('.', ',');
    		Period p = new PeriodParser(text).parse();
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

    @Test(dataProvider="ParseFailures", expectedExceptions=CalendricalParseException.class)
    public void factory_parseFailures(String text, int errPos) {
        try {
            new PeriodParser(text).parse();
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getParsedString(), text);
            assertEquals(ex.getErrorIndex(), errPos);
            System.out.println(ex.toString());
            throw ex;
        }
    }

    @Test(dataProvider="ParseFailures", expectedExceptions=CalendricalParseException.class)
    public void factory_parseFailures_comma(String text, int errPos) {
        text = text.replace('.', ',');
        try {
            new PeriodParser(text).parse();
        } catch (CalendricalParseException ex) {
            assertEquals(ex.getParsedString(), text);
            assertEquals(ex.getErrorIndex(), errPos);
            throw ex;
        }
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void factory_parse_tooBig() {
    	String text = "PT" + Long.MAX_VALUE + "1S";
    	new PeriodParser(text).parse();
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void factory_parse_tooBig_decimal() {
    	String text = "PT" + Long.MAX_VALUE + "1.1S";
    	new PeriodParser(text).parse();
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void factory_parse_tooSmall() {
        String text = "PT" + Long.MIN_VALUE + "1S";
        new PeriodParser(text).parse();
    }

    @Test(expectedExceptions=CalendricalParseException.class)
    public void factory_parse_tooSmall_decimal() {
        String text = "PT" + Long.MIN_VALUE + ".1S";
        new PeriodParser(text).parse();
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_null() {
    	new PeriodParser(null).parse();
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

    @Test(dataProvider="ParseSequenceFailures", expectedExceptions=CalendricalParseException.class)
    public void factory_parse_badSequence(String text) {
    	new PeriodParser(text).parse();
    }

}
