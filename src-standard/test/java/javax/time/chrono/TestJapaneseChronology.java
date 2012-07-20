/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chrono;

import static org.testng.Assert.assertEquals;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.calendrical.DateTimeAdjusters;
import org.testng.Assert;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestJapaneseChronology {

    //-----------------------------------------------------------------------
    // Chrono.ofName("Japanese")  Lookup by name
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_chrono_byName() {
        Chronology c = JapaneseChronology.INSTANCE;
        Chronology japanese = Chronology.ofName("Japanese");
        Assert.assertNotNull(japanese, "The Japanese calendar could not be found byName");
        Assert.assertEquals(japanese.getName(), "Japanese", "Name mismatch");
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name="samples")
    Object[][] data_samples() {
        return new Object[][] {
            {JapaneseChronology.INSTANCE.date(1, 1, 1), LocalDate.of(1, 1, 1)},
            {JapaneseChronology.INSTANCE.date(1, 1, 2), LocalDate.of(1, 1, 2)},
            {JapaneseChronology.INSTANCE.date(1, 1, 3), LocalDate.of(1, 1, 3)},
            
            {JapaneseChronology.INSTANCE.date(2, 1, 1), LocalDate.of(2, 1, 1)},
            {JapaneseChronology.INSTANCE.date(3, 1, 1), LocalDate.of(3, 1, 1)},
            {JapaneseChronology.INSTANCE.date(3, 12, 6), LocalDate.of(3, 12, 6)},
            {JapaneseChronology.INSTANCE.date(4, 1, 1), LocalDate.of(4, 1, 1)},
            {JapaneseChronology.INSTANCE.date(4, 7, 3), LocalDate.of(4, 7, 3)},
            {JapaneseChronology.INSTANCE.date(4, 7, 4), LocalDate.of(4, 7, 4)},
            {JapaneseChronology.INSTANCE.date(5, 1, 1), LocalDate.of(5, 1, 1)},
            {JapaneseChronology.INSTANCE.date(1662, 3, 3), LocalDate.of(1662, 3, 3)},
            {JapaneseChronology.INSTANCE.date(1728, 10, 28), LocalDate.of(1728, 10, 28)},
            {JapaneseChronology.INSTANCE.date(1728, 10, 29), LocalDate.of(1728, 10, 29)},
        };
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_toLocalDate(ChronoDate jdate, LocalDate iso) {
        assertEquals(jdate.toLocalDate(), iso);
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_fromCalendrical(ChronoDate jdate, LocalDate iso) {
        assertEquals(JapaneseChronology.INSTANCE.date(iso), jdate);
    }

    @DataProvider(name="badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {1728, 0, 0},
            
            {1728, -1, 1},
            {1728, 0, 1},
            {1728, 14, 1},
            {1728, 15, 1},
            
            {1728, 1, -1},
            {1728, 1, 0},
            {1728, 1, 32},
            
            {1728, 12, -1},
            {1728, 12, 0},
            {1728, 12, 32},
        };
    }

    @Test(dataProvider="badDates", groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_badDates(int year, int month, int dom) {
        JapaneseChronology.INSTANCE.date(year, month, dom);
    }

    //-----------------------------------------------------------------------
    // with(DateTimeAdjuster)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_adjust1() {
        ChronoDate base = JapaneseChronology.INSTANCE.date(1728, 10, 29);
        ChronoDate test = base.with(DateTimeAdjusters.lastDayOfMonth());
        assertEquals(test, JapaneseChronology.INSTANCE.date(1728, 10, 31));
    }

    @Test(groups={"tck"})
    public void test_adjust2() {
        ChronoDate base = JapaneseChronology.INSTANCE.date(1728, 12, 2);
        ChronoDate test = base.with(DateTimeAdjusters.lastDayOfMonth());
        assertEquals(test, JapaneseChronology.INSTANCE.date(1728, 12, 31));
    }

    //-----------------------------------------------------------------------
    // JapaneseDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_adjust_toLocalDate() {
        ChronoDate jdate = JapaneseChronology.INSTANCE.date(1726, 1, 4);
        ChronoDate test = jdate.with(LocalDate.of(2012, 7, 6));
        assertEquals(test, JapaneseChronology.INSTANCE.date(2012, 7, 6));
    }

//    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
//    public void test_adjust_toMonth() {
//        ChronoDate jdate = JapaneseChronology.INSTANCE.date(1726, 1, 4);
//        jdate.with(Month.APRIL);
//    }  // TODO: shouldn't really accept ISO Month

    //-----------------------------------------------------------------------
    // LocalDate.with(JapaneseDate)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_LocalDate_adjustToJapaneseDate() {
        ChronoDate jdate = JapaneseChronology.INSTANCE.date(1728, 10, 29);
        LocalDate test = LocalDate.MIN_DATE.with(jdate);
        assertEquals(test, LocalDate.of(1728, 10, 29));
    }

    @Test(groups={"tck"})
    public void test_LocalDateTime_adjustToJapaneseDate() {
        ChronoDate jdate = JapaneseChronology.INSTANCE.date(1728, 10, 29);
        LocalDateTime test = LocalDateTime.MIN_DATE_TIME.with(jdate);
        assertEquals(test, LocalDateTime.ofMidnight(1728, 10, 29));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {JapaneseChronology.INSTANCE.date(1, 1, 1), "0001UNKNOWN-01-01 (Japanese)"},
            {JapaneseChronology.INSTANCE.date(1728, 10, 28), "1728UNKNOWN-10-28 (Japanese)"},
            {JapaneseChronology.INSTANCE.date(1728, 10, 29), "1728UNKNOWN-10-29 (Japanese)"},
            {JapaneseChronology.INSTANCE.date(1727, 12, 5), "1727UNKNOWN-12-05 (Japanese)"},
            {JapaneseChronology.INSTANCE.date(1727, 12, 6), "1727UNKNOWN-12-06 (Japanese)"},
        };
    }

    @Test(dataProvider="toString", groups={"tck"})
    public void test_toString(ChronoDate jdate, String expected) {
        assertEquals(jdate.toString(), expected);
    }

    
}
