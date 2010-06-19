/*
 * Copyright (c) 2010, Stephen Colebourne & Michael Nascimento Santos
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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import javax.time.calendar.LocalDate;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.ZoneOffset;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test SystemLeapSecondRules.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestSystemLeapSecondRules {

    SystemLeapSecondRules rules;

    @BeforeMethod
    public void setUp() throws Exception {
        Constructor<SystemLeapSecondRules> con = SystemLeapSecondRules.class.getDeclaredConstructor();
        con.setAccessible(true);
        rules = con.newInstance();
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(Duration.class));
    }

    //-----------------------------------------------------------------------
    // serialize
    //-----------------------------------------------------------------------
    public void test_serialize() throws Exception {
        SystemLeapSecondRules test = SystemLeapSecondRules.INSTANCE;  // use real rules, not our hacked copy
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), test);
    }

    //-----------------------------------------------------------------------
    // getName()
    //-----------------------------------------------------------------------
    public void test_getName() {
        assertEquals(rules.getName(), "System");
    }

    //-----------------------------------------------------------------------
    // getLeapSecond()
    //-----------------------------------------------------------------------
    @DataProvider(name="LeapSeconds")
    Object[][] leapSeconds() {
        return new Object[][] {
            {-1, 0, 10, "1858-11-16"},
            {0, 0, 10, "1858-11-17"},
            {1, 0, 10, "1858-11-18"},
            
            {41316, 0, 10, "1971-12-31"},
            {41317, 0, 10, "1972-01-01"},
            {41318, 0, 10, "1972-01-02"},
            
            {41497, 0, 10, "1972-06-29"},
            {41498, 1, 10, "1972-06-30"},
            {41499, 0, 11, "1972-07-01"},
            {41500, 0, 11, "1972-07-02"},
            
            {41681, 0, 11, "1972-12-30"},
            {41682, 1, 11, "1972-12-31"},
            {41683, 0, 12, "1973-01-01"},
            {41684, 0, 12, "1973-01-02"},
            
            {42046, 0, 12, "1973-12-30"},
            {42047, 1, 12, "1973-12-31"},
            {42048, 0, 13, "1974-01-01"},
            {42049, 0, 13, "1974-01-02"},
            
            {42411, 0, 13, "1974-12-30"},
            {42412, 1, 13, "1974-12-31"},
            {42413, 0, 14, "1975-01-01"},
            {42414, 0, 14, "1975-01-02"},
            
            {42776, 0, 14, "1975-12-30"},
            {42777, 1, 14, "1975-12-31"},
            {42778, 0, 15, "1976-01-01"},
            {42779, 0, 15, "1976-01-02"},
            
            {43142, 0, 15, "1976-12-30"},
            {43143, 1, 15, "1976-12-31"},
            {43144, 0, 16, "1977-01-01"},
            {43145, 0, 16, "1977-01-02"},
            
            {43507, 0, 16, "1977-12-30"},
            {43508, 1, 16, "1977-12-31"},
            {43509, 0, 17, "1978-01-01"},
            {43510, 0, 17, "1978-01-02"},
            
            {43872, 0, 17, "1978-12-30"},
            {43873, 1, 17, "1978-12-31"},
            {43874, 0, 18, "1979-01-01"},
            {43875, 0, 18, "1979-01-02"},
            
            {44237, 0, 18, "1979-12-30"},
            {44238, 1, 18, "1979-12-31"},
            {44239, 0, 19, "1980-01-01"},
            {44240, 0, 19, "1980-01-02"},
            
            {44784, 0, 19, "1981-06-29"},
            {44785, 1, 19, "1981-06-30"},
            {44786, 0, 20, "1981-07-01"},
            {44787, 0, 20, "1981-07-02"},
            
            {45149, 0, 20, "1982-06-29"},
            {45150, 1, 20, "1982-06-30"},
            {45151, 0, 21, "1982-07-01"},
            {45152, 0, 21, "1982-07-02"},
            
            {45514, 0, 21, "1983-06-29"},
            {45515, 1, 21, "1983-06-30"},
            {45516, 0, 22, "1983-07-01"},
            {45517, 0, 22, "1983-07-02"},
            
            {46245, 0, 22, "1985-06-29"},
            {46246, 1, 22, "1985-06-30"},
            {46247, 0, 23, "1985-07-01"},
            {46248, 0, 23, "1985-07-02"},
            
            {47159, 0, 23, "1987-12-30"},
            {47160, 1, 23, "1987-12-31"},
            {47161, 0, 24, "1988-01-01"},
            {47162, 0, 24, "1988-01-02"},
            
            {47890, 0, 24, "1989-12-30"},
            {47891, 1, 24, "1989-12-31"},
            {47892, 0, 25, "1990-01-01"},
            {47893, 0, 25, "1990-01-02"},
            
            {48255, 0, 25, "1990-12-30"},
            {48256, 1, 25, "1990-12-31"},
            {48257, 0, 26, "1991-01-01"},
            {48258, 0, 26, "1991-01-02"},
            
            {48802, 0, 26, "1992-06-29"},
            {48803, 1, 26, "1992-06-30"},
            {48804, 0, 27, "1992-07-01"},
            {48805, 0, 27, "1992-07-02"},
            
            {49167, 0, 27, "1993-06-29"},
            {49168, 1, 27, "1993-06-30"},
            {49169, 0, 28, "1993-07-01"},
            {49170, 0, 28, "1993-07-02"},
            
            {49532, 0, 28, "1994-06-29"},
            {49533, 1, 28, "1994-06-30"},
            {49534, 0, 29, "1994-07-01"},
            {49535, 0, 29, "1994-07-02"},
            
            {50081, 0, 29, "1995-12-30"},
            {50082, 1, 29, "1995-12-31"},
            {50083, 0, 30, "1996-01-01"},
            {50084, 0, 30, "1996-01-02"},
            
            {50628, 0, 30, "1997-06-29"},
            {50629, 1, 30, "1997-06-30"},
            {50630, 0, 31, "1997-07-01"},
            {50631, 0, 31, "1997-07-02"},
            
            {51177, 0, 31, "1998-12-30"},
            {51178, 1, 31, "1998-12-31"},
            {51179, 0, 32, "1999-01-01"},
            {51180, 0, 32, "1999-01-02"},
            
            {53734, 0, 32, "2005-12-30"},
            {53735, 1, 32, "2005-12-31"},
            {53736, 0, 33, "2006-01-01"},
            {53737, 0, 33, "2006-01-02"},
            
            {54830, 0, 33, "2008-12-30"},
            {54831, 1, 33, "2008-12-31"},
            {54832, 0, 34, "2009-01-01"},
            {54833, 0, 34, "2009-01-02"},
        };
    }

    @Test(dataProvider="LeapSeconds")
    public void test_leapSeconds(long mjd, int adjust, int offset, String checkDate) {
        assertEquals(mjd, LocalDate.parse(checkDate).toModifiedJulianDays(), "Invalid test");
        
        assertEquals(rules.getLeapSecondAdjustment(mjd), adjust);
        assertEquals(rules.getTAIOffset(mjd), offset);
        if (adjust != 0) {
            long[] leaps = rules.getLeapSecondDates();
            Arrays.sort(leaps);
            assertEquals(Arrays.binarySearch(leaps, mjd) >= 0, true);
        }
    }

    //-----------------------------------------------------------------------
    // convertToUTC(TAIInstant)/convertToTAI(UTCInstant)
    //-----------------------------------------------------------------------
    private static final int CURRENT_TAI_OFFSET = 34;  // change this as leap secs added
    private static final long SECS_PER_DAY = 24 * 60 * 60L;
    private static final long NANOS_PER_SEC = 1000000000L;
    private static final long MJD_1800 = -21504L;
    private static final long MJD_1900 = 15020L;
    private static final long MJD_1958 = 36204L;
    private static final long MJD_1980 = 44239L;
    private static final long MJD_2100 = 88069L;
    private static final long TAI_SECS_UTC1800 = (MJD_1800 - MJD_1958) * SECS_PER_DAY + 10;
    private static final long TAI_SECS_UTC1900 = (MJD_1900 - MJD_1958) * SECS_PER_DAY + 10;
    private static final long TAI_SECS_UTC1958 = 10;
    private static final long TAI_SECS_UTC1980 = (MJD_1980 - MJD_1958) * SECS_PER_DAY + 19;
    private static final long TAI_SECS_UTC2100 = (MJD_2100 - MJD_1958) * SECS_PER_DAY + CURRENT_TAI_OFFSET;
    private static final long TAI_SECS_UTC2100_EXTRA_NEGATIVE_LEAP = (MJD_2100 - MJD_1958) * SECS_PER_DAY + CURRENT_TAI_OFFSET - 1;
//    private static final long TAI_SECS_UTC2100_EXTRA_DOUBLE_LEAP = (MJD_2100 - MJD_1958) * SECS_PER_DAY + CURRENT_TAI_OFFSET + 2;

    public void test_convertToUTC_TAIInstant_startUtcPeriod() {
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC1980, 0);  // 1980-01-01 (19 leap secs added)
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_1980, 0, rules);
        for (int i = -10; i < 10; i++) {
            Duration duration = Duration.ofNanos(i);
            assertEquals(rules.convertToUTC(tai.plus(duration)), expected.plus(duration));
            assertEquals(rules.convertToTAI(expected.plus(duration)), tai.plus(duration)); // check reverse
        }
        for (int i = -10; i < 10; i++) {
            Duration duration = Duration.ofSeconds(i);
            assertEquals(rules.convertToUTC(tai.plus(duration)), expected.plus(duration));
            assertEquals(rules.convertToTAI(expected.plus(duration)), tai.plus(duration)); // check reverse
        }
    }

    public void test_convertToUTC_TAIInstant_furtherAfterLeap() {
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC1980 + 1, 0);  // 1980-01-01 (19 leap secs added)
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_1980, NANOS_PER_SEC, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    public void test_convertToUTC_TAIInstant_justAfterLeap() {
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC1980, 0);  // 1980-01-01 (19 leap secs added)
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_1980, 0, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    public void test_convertToUTC_TAIInstant_inLeap() {
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC1980 - 1, 0);  // 1980-01-01 (1 second before 1980)
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_1980 - 1, SECS_PER_DAY * NANOS_PER_SEC, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    public void test_convertToUTC_TAIInstant_justBeforeLeap() {
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC1980 - 2, 0);  // 1980-01-01 (2 seconds before 1980)
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_1980 - 1, (SECS_PER_DAY - 1) * NANOS_PER_SEC, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    public void test_convertToUTC_TAIInstant_1800() {
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC1800, 0);  // 1800-01-01
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_1800, 0, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    public void test_convertToUTC_TAIInstant_1900() {
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC1900, 0);  // 1900-01-01
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_1900, 0, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    public void test_convertToUTC_TAIInstant_1958() {
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC1958, 0);  // 1958-01-01
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_1958, 0, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    public void test_convertToUTC_TAIInstant_2100() {
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC2100, 0);  // 2100-01-01
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_2100, 0, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_convertToUTC_TAIInstant_null() {
        rules.convertToUTC((TAIInstant) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_convertToTAI_UTCInstant_null() {
        rules.convertToTAI((UTCInstant) null);
    }

    //-------------------------------------------------------------------------
    public void test_negativeLeap_justBeforeLeap() {
        rules.registerLeapSecond(MJD_2100 - 1, -1);
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC2100_EXTRA_NEGATIVE_LEAP - 1, 0);  // 2100-01-01 (1 second before 2100)
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_2100 - 1, (SECS_PER_DAY - 2) * NANOS_PER_SEC, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    public void test_negativeLeap_justAfterLeap() {
        rules.registerLeapSecond(MJD_2100 - 1, -1);
        TAIInstant tai = TAIInstant.ofTAISeconds(TAI_SECS_UTC2100_EXTRA_NEGATIVE_LEAP, 0);  // 2100-01-01
        UTCInstant expected = UTCInstant.ofModifiedJulianDay(MJD_2100, 0, rules);
        assertEquals(rules.convertToUTC(tai), expected);
        assertEquals(rules.convertToTAI(expected), tai); // check reverse
    }

    //-----------------------------------------------------------------------
    // convertToUTC(Instant)/convertToInstant(UTCInstant)
    //-----------------------------------------------------------------------
    public void test_convertToInstant_justBeforeLeap() {
        OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 23, 43, 21, 0, ZoneOffset.UTC);
        Instant instant = odt.toInstant();
        UTCInstant utc = UTCInstant.ofModifiedJulianDay(MJD_1980 - 1, (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC, rules);
        assertEquals(rules.convertToInstant(utc), instant);
        assertEquals(rules.convertToUTC(instant), utc);
    }

    public void test_convertToInstant_sls() {
        for (int i = 1; i < 1000; i++) {
            long utcNanos = (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000000000L;
            long startSls = (86401 - 1000) * NANOS_PER_SEC;
            long slsNanos = (utcNanos - (utcNanos - startSls) / 1000);
            OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 0, 0, 0,
                    (int) (slsNanos % NANOS_PER_SEC), ZoneOffset.UTC)
                    .plusSeconds((int) (slsNanos / NANOS_PER_SEC));
            Instant instant = odt.toInstant();
            UTCInstant utc = UTCInstant.ofModifiedJulianDay(
                    MJD_1980 - 1, (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * NANOS_PER_SEC, rules);
            assertEquals(rules.convertToInstant(utc), instant);
            assertEquals(rules.convertToUTC(instant), utc);
        }
    }

    public void test_convertToInstant_slsMillis() {
        for (int i = 1; i < 1000; i++) {
            long utcNanos = (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000000;
            long startSls = (86401 - 1000) * NANOS_PER_SEC;
            long slsNanos = (utcNanos - (utcNanos - startSls) / 1000);
            OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 23, 43, 21, (int) (slsNanos % NANOS_PER_SEC), ZoneOffset.UTC);
            Instant instant = odt.toInstant();
            UTCInstant utc = UTCInstant.ofModifiedJulianDay(
                    MJD_1980 - 1, (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000000, rules);
            assertEquals(rules.convertToInstant(utc), instant);
            assertEquals(rules.convertToUTC(instant), utc);
        }
    }

    public void test_convertToInstant_slsMicros() {
        for (int i = 1; i < 1000; i++) {
            long utcNanos = (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000;
            long startSls = (86401 - 1000) * NANOS_PER_SEC;
            long slsNanos = (utcNanos - (utcNanos - startSls) / 1000);
            OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 23, 43, 21, (int) (slsNanos % NANOS_PER_SEC), ZoneOffset.UTC);
            Instant instant = odt.toInstant();
            UTCInstant utc = UTCInstant.ofModifiedJulianDay(
                    MJD_1980 - 1, (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i * 1000, rules);
            assertEquals(rules.convertToInstant(utc), instant);
            assertEquals(rules.convertToUTC(instant), utc);
        }
    }

    public void test_convertToInstant_slsNanos() {
        for (int i = 1; i < 5005; i++) {
            long utcNanos = (SECS_PER_DAY + 1 - 1000) * NANOS_PER_SEC + i;
            long startSls = (86401 - 1000) * NANOS_PER_SEC;
            long slsNanos = (utcNanos - (utcNanos - startSls) / 1000);
            OffsetDateTime odt = OffsetDateTime.of(1979, 12, 31, 23, 43, 21, (int) (slsNanos % NANOS_PER_SEC), ZoneOffset.UTC);
            Instant instant = odt.toInstant();
            UTCInstant utc = UTCInstant.ofModifiedJulianDay(MJD_1980 - 1, utcNanos, rules);
            assertEquals(rules.convertToInstant(utc), instant);
            
            System.out.println(instant + " " + rules.convertToUTC(instant) + " "+ utc);
            
            // not all instants can map back to the correct UTC value
            long reverseUtcNanos = startSls + ((slsNanos - startSls) * 1000L) / (1000L - 1);
            assertEquals(rules.convertToUTC(instant), UTCInstant.ofModifiedJulianDay(MJD_1980 - 1, reverseUtcNanos, rules));
        }
    }

    public void test_convertToInstant_justAfterLeap() {
        OffsetDateTime odt = OffsetDateTime.of(1980, 1, 1, 0, 0, 0, ZoneOffset.UTC);
        Instant instant = odt.toInstant();
        UTCInstant utc = UTCInstant.ofModifiedJulianDay(MJD_1980, 0, rules);
        assertEquals(rules.convertToInstant(utc), instant);
        assertEquals(rules.convertToUTC(instant), utc);
    }

    public void test_convertToInstant_furtherAfterLeap() {
        OffsetDateTime odt = OffsetDateTime.of(1980, 1, 1, 0, 0, 1, ZoneOffset.UTC);
        Instant instant = odt.toInstant();
        UTCInstant utc = UTCInstant.ofModifiedJulianDay(MJD_1980, NANOS_PER_SEC, rules);
        assertEquals(rules.convertToInstant(utc), instant);
        assertEquals(rules.convertToUTC(instant), utc);
    }

    //-----------------------------------------------------------------------
    // registerLeapSecond()
    //-----------------------------------------------------------------------
    public void test_registerLeapSecond_justAfterLastDate() {
        long[] dates = rules.getLeapSecondDates();
        long mjd = dates[dates.length - 1] + 1;
        rules.registerLeapSecond(mjd, 1);
        long[] test = rules.getLeapSecondDates();
        assertEquals(test.length, dates.length + 1);
        assertEquals(test[test.length - 1], mjd);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_registerLeapSecond_beforeLastDate() {
        long[] dates = rules.getLeapSecondDates();
        long mjd = dates[dates.length - 1];
        rules.registerLeapSecond(mjd, 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_registerLeapSecond_invalidAdjustment_zero() {
        rules.registerLeapSecond(MJD_2100, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_registerLeapSecond_invalidAdjustment_minusTwo() {
        rules.registerLeapSecond(MJD_2100, -2);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_registerLeapSecond_invalidAdjustment_three() {
        rules.registerLeapSecond(MJD_2100, 3);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(rules.toString(), "LeapSecondRules[System]");
    }

}
