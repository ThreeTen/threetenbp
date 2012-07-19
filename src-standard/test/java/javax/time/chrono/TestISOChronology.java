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
public class TestISOChronology {

    //-----------------------------------------------------------------------
    // Chrono.ofName("ISO")  Lookup by name
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_chrono_byName() {
        Chrono c = ISOChronology.INSTANCE;
        Chrono ISO = Chrono.ofName("ISO");
        Assert.assertNotNull(ISO, "The ISO calendar could not be found byName");
        Assert.assertEquals(ISO.getName(), "ISO", "Name mismatch");
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name="samples")
    Object[][] data_samples() {
        return new Object[][] {
            {ISOChronology.INSTANCE.date(1, 7, 8), LocalDate.of(1, 7, 8)},
            {ISOChronology.INSTANCE.date(1, 7, 20), LocalDate.of(1, 7, 20)},
            {ISOChronology.INSTANCE.date(1, 7, 21), LocalDate.of(1, 7, 21)},
            
            {ISOChronology.INSTANCE.date(2, 7, 8), LocalDate.of(2, 7, 8)},
            {ISOChronology.INSTANCE.date(3, 6, 27), LocalDate.of(3, 6, 27)},
            {ISOChronology.INSTANCE.date(3, 5, 23), LocalDate.of(3, 5, 23)},
            {ISOChronology.INSTANCE.date(4, 6, 16), LocalDate.of(4, 6, 16)},
            {ISOChronology.INSTANCE.date(4, 7, 3), LocalDate.of(4, 7, 3)},
            {ISOChronology.INSTANCE.date(4, 7, 4), LocalDate.of(4, 7, 4)},
            {ISOChronology.INSTANCE.date(5, 1, 1), LocalDate.of(5, 1, 1)},
            {ISOChronology.INSTANCE.date(1727, 3, 3), LocalDate.of(1727, 3, 3)},
            {ISOChronology.INSTANCE.date(1728, 10, 28), LocalDate.of(1728, 10, 28)},
            {ISOChronology.INSTANCE.date(2012, 10, 29), LocalDate.of(2012, 10, 29)},
        };
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_toLocalDate(ChronoDate ISODate, LocalDate iso) {
        assertEquals(ISODate.toLocalDate(), iso);
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_fromCalendrical(ChronoDate ISODate, LocalDate iso) {
        assertEquals(ISOChronology.INSTANCE.date(iso), ISODate);
    }

    @DataProvider(name="badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {2012, 0, 0},
            
            {2012, -1, 1},
            {2012, 0, 1},
            {2012, 14, 1},
            {2012, 15, 1},
            
            {2012, 1, -1},
            {2012, 1, 0},
            {2012, 1, 32},
            
            {2012, 12, -1},
            {2012, 12, 0},
            {2012, 12, 32},
        };
    }

    @Test(dataProvider="badDates", groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_badDates(int year, int month, int dom) {
        ISOChronology.INSTANCE.date(year, month, dom);
    }

    //-----------------------------------------------------------------------
    // with(DateTimeAdjuster)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_adjust1() {
        ChronoDate base = ISOChronology.INSTANCE.date(1728, 10, 28);
        ChronoDate test = base.with(DateTimeAdjusters.lastDayOfMonth());
        assertEquals(test, ISOChronology.INSTANCE.date(1728, 10, 31));
    }

    @Test(groups={"tck"})
    public void test_adjust2() {
        ChronoDate base = ISOChronology.INSTANCE.date(1728, 12, 2);
        ChronoDate test = base.with(DateTimeAdjusters.lastDayOfMonth());
        assertEquals(test, ISOChronology.INSTANCE.date(1728, 12, 31));
    }

    //-----------------------------------------------------------------------
    // ISODate.with(Local*)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_adjust_toLocalDate() {
        ChronoDate ISODate = ISOChronology.INSTANCE.date(1726, 1, 4);
        ChronoDate test = ISODate.with(LocalDate.of(2012, 7, 6));
        assertEquals(test, ISOChronology.INSTANCE.date(2012, 7, 6));
    }

//    @Test(groups={"tck"}, expectedExceptions=CalendricalException.class)
//    public void test_adjust_toMonth() {
//        ChronoDate ISODate = ISOChronology.INSTANCE.date(1726, 1, 4);
//        ISODate.with(Month.APRIL);
//    }  // TODO: shouldn't really accept ISO Month

    //-----------------------------------------------------------------------
    // LocalDate.with(ISODate)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_LocalDate_adjustToISODate() {
        ChronoDate ISODate = ISOChronology.INSTANCE.date(1728, 10, 29);
        LocalDate test = LocalDate.MIN_DATE.with(ISODate);
        assertEquals(test, LocalDate.of(1728, 10, 29));
    }

    @Test(groups={"tck"})
    public void test_LocalDateTime_adjustToISODate() {
        ChronoDate ISODate = ISOChronology.INSTANCE.date(1728, 10, 29);
        LocalDateTime test = LocalDateTime.MIN_DATE_TIME.with(ISODate);
        assertEquals(test, LocalDateTime.ofMidnight(1728, 10, 29));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {ISOChronology.INSTANCE.date(1, 1, 1), "0001ISO_CE-01-01 (ISO)"},
            {ISOChronology.INSTANCE.date(1728, 10, 28), "1728ISO_CE-10-28 (ISO)"},
            {ISOChronology.INSTANCE.date(1728, 10, 29), "1728ISO_CE-10-29 (ISO)"},
            {ISOChronology.INSTANCE.date(1727, 12, 5), "1727ISO_CE-12-05 (ISO)"},
            {ISOChronology.INSTANCE.date(1727, 12, 6), "1727ISO_CE-12-06 (ISO)"},
        };
    }

    @Test(dataProvider="toString", groups={"tck"})
    public void test_toString(ChronoDate ISODate, String expected) {
        assertEquals(ISODate.toString(), expected);
    }

    
}
