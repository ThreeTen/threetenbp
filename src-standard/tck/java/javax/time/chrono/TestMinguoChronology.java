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

import javax.time.DateTimeException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.calendrical.DateTimeAdjusters;
import javax.time.calendrical.LocalPeriodUnit;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestMinguoChronology {
    //-----------------------------------------------------------------------
    // Chrono.ofName("Minguo")  Lookup by name
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_chrono_byName() {
        Chronology c = MinguoChronology.INSTANCE;
        Chronology minguo = Chronology.of("Minguo");
        Assert.assertNotNull(minguo, "The Minguo calendar could not be found byName");
        Assert.assertEquals(minguo.getId(), "Minguo", "Name mismatch");
    }


    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name="samples")
    Object[][] data_samples() {
        return new Object[][] {
            {MinguoChronology.INSTANCE.date(1, 1, 1), LocalDate.of(1912, 1, 1)},
            {MinguoChronology.INSTANCE.date(1, 1, 2), LocalDate.of(1912, 1, 2)},
            {MinguoChronology.INSTANCE.date(1, 1, 3), LocalDate.of(1912, 1, 3)},
            
            {MinguoChronology.INSTANCE.date(2, 1, 1), LocalDate.of(1913, 1, 1)},
            {MinguoChronology.INSTANCE.date(3, 1, 1), LocalDate.of(1914, 1, 1)},
            {MinguoChronology.INSTANCE.date(3, 12, 6), LocalDate.of(1914, 12, 6)},
            {MinguoChronology.INSTANCE.date(4, 1, 1), LocalDate.of(1915, 1, 1)},
            {MinguoChronology.INSTANCE.date(4, 7, 3), LocalDate.of(1915, 7, 3)},
            {MinguoChronology.INSTANCE.date(4, 7, 4), LocalDate.of(1915, 7, 4)},
            {MinguoChronology.INSTANCE.date(5, 1, 1), LocalDate.of(1916, 1, 1)},
            {MinguoChronology.INSTANCE.date(100, 3, 3), LocalDate.of(2011, 3, 3)},
            {MinguoChronology.INSTANCE.date(101, 10, 28), LocalDate.of(2012, 10, 28)},
            {MinguoChronology.INSTANCE.date(101, 10, 29), LocalDate.of(2012, 10, 29)},
        };
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_toLocalDate(ChronoLocalDate minguo, LocalDate iso) {
        assertEquals(LocalDate.from(minguo), iso);
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_fromCalendrical(ChronoLocalDate minguo, LocalDate iso) {
        assertEquals(MinguoChronology.INSTANCE.date(iso), minguo);
    }

    @Test(dataProvider="samples", groups={"implementation"})
    public void test_MinguoDate(ChronoLocalDate minguoDate, LocalDate iso) {
        assertEquals(minguoDate instanceof MinguoDate, true, "Minguo didn't create MinguoDate");
        MinguoDate date = MinguoDate.class.cast(minguoDate);
        ChronoLocalDate<MinguoChronology> hd = minguoDate;
        ChronoLocalDateTime hdt = hd.atTime(LocalTime.MIDDAY);
        ZoneOffset zo = ZoneOffset.ofHours(1);
        ChronoOffsetDateTime hodt = hdt.atOffset(zo);
        ChronoZonedDateTime hzdt = hodt.atZoneSameInstant(ZoneId.UTC);
        hdt = hdt.plus(1, LocalPeriodUnit.YEARS);
        hdt = hdt.plus(1, LocalPeriodUnit.MONTHS);
        hdt = hdt.plus(1, LocalPeriodUnit.DAYS);
        hdt = hdt.plus(1, LocalPeriodUnit.HOURS);
        hdt = hdt.plus(1, LocalPeriodUnit.MINUTES);
        hdt = hdt.plus(1, LocalPeriodUnit.SECONDS);
        hdt = hdt.plus(1, LocalPeriodUnit.NANOS);
        ChronoOffsetDateTime<MinguoChronology> a1 = hzdt.getOffsetDateTime();
        ChronoLocalDateTime<MinguoChronology> a2 = a1.getDateTime();
        ChronoLocalDate<MinguoChronology> a3 = a2.getDate();
        ChronoLocalDate<MinguoChronology> a5 = a1.getDate();

        //System.out.printf(" d: %s, dt: %s; odt: %s; zodt: %s; a4: %s%n", date, hdt, hodt, hzdt, a5);
    }
    @Test()
    public void test_MinguoChrono() {
        MinguoDate h1 = MinguoDate.of(MinguoChronology.ERA_ROC, 1, 2, 3);
        ChronoLocalDate<MinguoChronology> h2 = h1;
        ChronoLocalDateTime<MinguoChronology> h3 = h2.atTime(LocalTime.MIDDAY);
        ChronoOffsetDateTime<MinguoChronology> h4 = h3.atOffset(ZoneOffset.UTC);
        ChronoZonedDateTime<MinguoChronology> h5 = h4.atZoneSameInstant(ZoneId.UTC);
    }


    @DataProvider(name="badDates")
    Object[][] data_badDates() {
        return new Object[][] {
            {1912, 0, 0},
            
            {1912, -1, 1},
            {1912, 0, 1},
            {1912, 14, 1},
            {1912, 15, 1},
            
            {1912, 1, -1},
            {1912, 1, 0},
            {1912, 1, 32},
            {1912, 2, 29},
            {1912, 2, 30},
            
            {1912, 12, -1},
            {1912, 12, 0},
            {1912, 12, 32},
            };
    }

    @Test(dataProvider="badDates", groups={"tck"}, expectedExceptions=DateTimeException.class)
    public void test_badDates(int year, int month, int dom) {
        MinguoChronology.INSTANCE.date(year, month, dom);
    }

    //-----------------------------------------------------------------------
    // with(DateTimeAdjuster)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_adjust1() {
        ChronoLocalDate base = MinguoChronology.INSTANCE.date(2012, 10, 29);
        ChronoLocalDate test = base.with(DateTimeAdjusters.lastDayOfMonth());
        assertEquals(test, MinguoChronology.INSTANCE.date(2012, 10, 31));
    }

    @Test(groups={"tck"})
    public void test_adjust2() {
        ChronoLocalDate base = MinguoChronology.INSTANCE.date(1728, 12, 2);
        ChronoLocalDate test = base.with(DateTimeAdjusters.lastDayOfMonth());
        assertEquals(test, MinguoChronology.INSTANCE.date(1728, 12, 31));
    }

    //-----------------------------------------------------------------------
    // MinguoDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_adjust_toLocalDate() {
        ChronoLocalDate minguo = MinguoChronology.INSTANCE.date(99, 1, 4);
        ChronoLocalDate test = minguo.with(LocalDate.of(2012, 7, 6));
        assertEquals(test, MinguoChronology.INSTANCE.date(101, 7, 6));
    }

//    @Test(groups={"tck"}, expectedExceptions=DateTimeException.class)
//    public void test_adjust_toMonth() {
//        ChronoLocalDate minguo = MinguoChronology.INSTANCE.date(1726, 1, 4);
//        minguo.with(Month.APRIL);
//    }  // TODO: shouldn't really accept ISO Month

    //-----------------------------------------------------------------------
    // LocalDate.with(MinguoDate)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_LocalDate_adjustToMinguoDate() {
        ChronoLocalDate minguo = MinguoChronology.INSTANCE.date(101, 10, 29);
        LocalDate test = LocalDate.MIN_DATE.with(minguo);
        assertEquals(test, LocalDate.of(2012, 10, 29));
    }

    @Test(groups={"tck"})
    public void test_LocalDateTime_adjustToMinguoDate() {
        ChronoLocalDate minguo = MinguoChronology.INSTANCE.date(101, 10, 29);
        LocalDateTime test = LocalDateTime.MIN_DATE_TIME.with(minguo);
        assertEquals(test, LocalDateTime.of(2012, 10, 29, 0, 0));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {MinguoChronology.INSTANCE.date(1, 1, 1), "roc R.O.C.1-01-01"},
            {MinguoChronology.INSTANCE.date(1728, 10, 28), "roc R.O.C.1728-10-28"},
            {MinguoChronology.INSTANCE.date(1728, 10, 29), "roc R.O.C.1728-10-29"},
            {MinguoChronology.INSTANCE.date(1727, 12, 5), "roc R.O.C.1727-12-05"},
            {MinguoChronology.INSTANCE.date(1727, 12, 6), "roc R.O.C.1727-12-06"},
        };
    }

    @Test(dataProvider="toString", groups={"tck"})
    public void test_toString(ChronoLocalDate minguo, String expected) {
        assertEquals(minguo.toString(), expected);
    }

    
}
