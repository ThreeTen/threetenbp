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
public class TestHijrahChronology {

    //-----------------------------------------------------------------------
    // Chrono.ofName("Hijrah")  Lookup by name
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_chrono_byName() {
        Chrono c = HijrahChronology.INSTANCE;
        Chrono Hijrah = Chrono.ofName("Hijrah");
        Assert.assertNotNull(Hijrah, "The Hijrah calendar could not be found byName");
        Assert.assertEquals(Hijrah.getName(), "Hijrah", "Name mismatch");
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name="samples")
    Object[][] data_samples() {
        return new Object[][] {
            {HijrahChronology.INSTANCE.date(1, 1, 1), LocalDate.of(622, 7, 19)},
            {HijrahChronology.INSTANCE.date(1, 1, 2), LocalDate.of(622, 7, 20)},
            {HijrahChronology.INSTANCE.date(1, 1, 3), LocalDate.of(622, 7, 21)},
            
            {HijrahChronology.INSTANCE.date(2, 1, 1), LocalDate.of(623, 7, 8)},
            {HijrahChronology.INSTANCE.date(3, 1, 1), LocalDate.of(624, 6, 27)},
            {HijrahChronology.INSTANCE.date(3, 12, 6), LocalDate.of(625, 5, 23)},
            {HijrahChronology.INSTANCE.date(4, 1, 1), LocalDate.of(625, 6, 16)},
            {HijrahChronology.INSTANCE.date(4, 7, 3), LocalDate.of(625, 12, 12)},
            {HijrahChronology.INSTANCE.date(4, 7, 4), LocalDate.of(625, 12, 13)},
            {HijrahChronology.INSTANCE.date(5, 1, 1), LocalDate.of(626, 6, 5)},
            {HijrahChronology.INSTANCE.date(1662, 3, 3), LocalDate.of(2234, 4, 3)},
            {HijrahChronology.INSTANCE.date(1728, 10, 28), LocalDate.of(2298, 12, 03)},
            {HijrahChronology.INSTANCE.date(1728, 10, 29), LocalDate.of(2298, 12, 04)},
        };
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_toLocalDate(ChronoDate hijrahDate, LocalDate iso) {
        assertEquals(hijrahDate.toLocalDate(), iso);
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_fromCalendrical(ChronoDate hijrahDate, LocalDate iso) {
        assertEquals(HijrahChronology.INSTANCE.date(iso), hijrahDate);
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
        HijrahChronology.INSTANCE.date(year, month, dom);
    }

    //-----------------------------------------------------------------------
    // with(DateTimeAdjuster)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_adjust1() {
        ChronoDate base = HijrahChronology.INSTANCE.date(1728, 10, 28);
        ChronoDate test = base.with(DateTimeAdjusters.lastDayOfMonth());
        assertEquals(test, HijrahChronology.INSTANCE.date(1728, 10, 29));
    }

    @Test(groups={"tck"})
    public void test_adjust2() {
        ChronoDate base = HijrahChronology.INSTANCE.date(1728, 12, 2);
        ChronoDate test = base.with(DateTimeAdjusters.lastDayOfMonth());
        assertEquals(test, HijrahChronology.INSTANCE.date(1728, 12, 30));
    }

    //-----------------------------------------------------------------------
    // HijrahDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_adjust_toLocalDate() {
        ChronoDate hijrahDate = HijrahChronology.INSTANCE.date(1726, 1, 4);
        ChronoDate test = hijrahDate.with(LocalDate.of(2012, 7, 6));
        assertEquals(test, HijrahChronology.INSTANCE.date(1433, 8, 16));
    }

//    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
//    public void test_adjust_toMonth() {
//        ChronoDate hijrahDate = HijrahChronology.INSTANCE.date(1726, 1, 4);
//        hijrahDate.with(Month.APRIL);
//    }  // TODO: shouldn't really accept ISO Month

    //-----------------------------------------------------------------------
    // LocalDate.with(HijrahDate)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_LocalDate_adjustToHijrahDate() {
        ChronoDate hijrahDate = HijrahChronology.INSTANCE.date(1728, 10, 29);
        LocalDate test = LocalDate.MIN_DATE.with(hijrahDate);
        assertEquals(test, LocalDate.of(2298, 12, 4));
    }

    @Test(groups={"tck"})
    public void test_LocalDateTime_adjustToHijrahDate() {
        ChronoDate hijrahDate = HijrahChronology.INSTANCE.date(1728, 10, 29);
        LocalDateTime test = LocalDateTime.MIN_DATE_TIME.with(hijrahDate);
        assertEquals(test, LocalDateTime.ofMidnight(2298, 12, 4));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {HijrahChronology.INSTANCE.date(1, 1, 1), "01-01-01 (Hijrah)"},
            {HijrahChronology.INSTANCE.date(1728, 10, 28), "1728-10-28 (Hijrah)"},
            {HijrahChronology.INSTANCE.date(1728, 10, 29), "1728-10-29 (Hijrah)"},
            {HijrahChronology.INSTANCE.date(1727, 12, 5), "1727-12-05 (Hijrah)"},
            {HijrahChronology.INSTANCE.date(1727, 12, 6), "1727-12-06 (Hijrah)"},
        };
    }

    @Test(dataProvider="toString", groups={"tck"})
    public void test_toString(ChronoDate hijrahDate, String expected) {
        assertEquals(hijrahDate.toString(), expected);
    }

    
}
