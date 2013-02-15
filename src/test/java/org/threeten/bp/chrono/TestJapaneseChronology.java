/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.chrono;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.Month;
import org.threeten.bp.chrono.JapaneseChronology;
import org.threeten.bp.temporal.TemporalAdjusters;

/**
 * Test.
 */
@Test
public class TestJapaneseChronology {

    //-----------------------------------------------------------------------
    // Chrono.ofName("Japanese")  Lookup by name
    //-----------------------------------------------------------------------
    @Test
    public void test_chrono_byName() {
        Chronology<JapaneseChronology> c = JapaneseChronology.INSTANCE;
        Chronology<?> test = Chronology.of("Japanese");
        Assert.assertNotNull(test, "The Japanese calendar could not be found byName");
        Assert.assertEquals(test.getId(), "Japanese", "ID mismatch");
        Assert.assertEquals(test.getCalendarType(), "japanese", "Type mismatch");
        Assert.assertEquals(test, c);
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

    @Test(dataProvider="samples")
    public void test_toLocalDate(ChronoLocalDate<JapaneseChronology> jdate, LocalDate iso) {
        assertEquals(LocalDate.from(jdate), iso);
    }

    @Test(dataProvider="samples")
    public void test_fromCalendrical(ChronoLocalDate<JapaneseChronology> jdate, LocalDate iso) {
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

    @Test(dataProvider="badDates", expectedExceptions=DateTimeException.class)
    public void test_badDates(int year, int month, int dom) {
        JapaneseChronology.INSTANCE.date(year, month, dom);
    }

    //-----------------------------------------------------------------------
    // with(WithAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust1() {
        ChronoLocalDate<JapaneseChronology> base = JapaneseChronology.INSTANCE.date(1728, 10, 29);
        ChronoLocalDate<JapaneseChronology> test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, JapaneseChronology.INSTANCE.date(1728, 10, 31));
    }

    @Test
    public void test_adjust2() {
        ChronoLocalDate<JapaneseChronology> base = JapaneseChronology.INSTANCE.date(1728, 12, 2);
        ChronoLocalDate<JapaneseChronology> test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, JapaneseChronology.INSTANCE.date(1728, 12, 31));
    }

    //-----------------------------------------------------------------------
    // JapaneseDate.with(Local*)
    //-----------------------------------------------------------------------
    @Test
    public void test_adjust_toLocalDate() {
        ChronoLocalDate<JapaneseChronology> jdate = JapaneseChronology.INSTANCE.date(1726, 1, 4);
        ChronoLocalDate<JapaneseChronology> test = jdate.with(LocalDate.of(2012, 7, 6));
        assertEquals(test, JapaneseChronology.INSTANCE.date(2012, 7, 6));
    }

    @Test(expectedExceptions=DateTimeException.class)
    public void test_adjust_toMonth() {
        ChronoLocalDate<?> jdate = JapaneseChronology.INSTANCE.date(1726, 1, 4);
        jdate.with(Month.APRIL);
    }

    //-----------------------------------------------------------------------
    // LocalDate.with(JapaneseDate)
    //-----------------------------------------------------------------------
    @Test
    public void test_LocalDate_adjustToJapaneseDate() {
        ChronoLocalDate<JapaneseChronology> jdate = JapaneseChronology.INSTANCE.date(1728, 10, 29);
        LocalDate test = LocalDate.MIN.with(jdate);
        assertEquals(test, LocalDate.of(1728, 10, 29));
    }

    @Test
    public void test_LocalDateTime_adjustToJapaneseDate() {
        ChronoLocalDate<JapaneseChronology> jdate = JapaneseChronology.INSTANCE.date(1728, 10, 29);
        LocalDateTime test = LocalDateTime.MIN.with(jdate);
        assertEquals(test, LocalDateTime.of(1728, 10, 29, 0, 0));
    }

    //-----------------------------------------------------------------------
    // Check Japanese Eras
    //-----------------------------------------------------------------------
    @DataProvider(name="japaneseEras")
    Object[][] data_japanseseEras() {
        return new Object[][] {
            { JapaneseChronology.ERA_SEIREKI, -999, "Seireki"},
            { JapaneseChronology.ERA_MEIJI, -1, "Meiji"},
            { JapaneseChronology.ERA_TAISHO, 0, "Taisho"},
            { JapaneseChronology.ERA_SHOWA, 1, "Showa"},
            { JapaneseChronology.ERA_HEISEI, 2, "Heisei"},
        };
    }

    @Test(dataProvider="japaneseEras")
    public void test_Japanese_Eras(Era<?> era, int eraValue, String name) {
        assertEquals(era.getValue(), eraValue, "EraValue");
        assertEquals(era.toString(), name, "Era Name");
        assertEquals(era, JapaneseChronology.INSTANCE.eraOf(eraValue), "JapaneseChrono.eraOf()");
        List<Era<JapaneseChronology>> eras = JapaneseChronology.INSTANCE.eras();
        assertTrue(eras.contains(era), "Era is not present in JapaneseChrono.INSTANCE.eras()");
    }

    @Test
    public void test_Japanese_badEras() {
        int badEras[] = {-1000, -998, -997, -2, 3, 4, 1000};
        for (int badEra : badEras) {
            try {
                Era<JapaneseChronology> era = JapaneseChronology.INSTANCE.eraOf(badEra);
                fail("JapaneseChrono.eraOf returned " + era + " + for invalid eraValue " + badEra);
            } catch (DateTimeException ex) {
                // ignore expected exception
            }
        }
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {JapaneseChronology.INSTANCE.date(0001,  1,  1), "Japanese 0001-01-01"},
            {JapaneseChronology.INSTANCE.date(1728, 10, 28), "Japanese 1728-10-28"},
            {JapaneseChronology.INSTANCE.date(1728, 10, 29), "Japanese 1728-10-29"},
            {JapaneseChronology.INSTANCE.date(1727, 12,  5), "Japanese 1727-12-05"},
            {JapaneseChronology.INSTANCE.date(1727, 12,  6), "Japanese 1727-12-06"},
            {JapaneseChronology.INSTANCE.date(1868,  9,  8), "Japanese Meiji 1-09-08"},
            {JapaneseChronology.INSTANCE.date(1912,  7, 29), "Japanese Meiji 45-07-29"},
            {JapaneseChronology.INSTANCE.date(1912,  7, 30), "Japanese Taisho 1-07-30"},
            {JapaneseChronology.INSTANCE.date(1926, 12, 24), "Japanese Taisho 15-12-24"},
            {JapaneseChronology.INSTANCE.date(1926, 12, 25), "Japanese Showa 1-12-25"},
            {JapaneseChronology.INSTANCE.date(1989,  1,  7), "Japanese Showa 64-01-07"},
            {JapaneseChronology.INSTANCE.date(1989,  1,  8), "Japanese Heisei 1-01-08"},
            {JapaneseChronology.INSTANCE.date(2012, 12,  6), "Japanese Heisei 24-12-06"},
        };
    }

    @Test(dataProvider="toString")
    public void test_toString(ChronoLocalDate<JapaneseChronology> jdate, String expected) {
        assertEquals(jdate.toString(), expected);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_true() {
        assertTrue(JapaneseChronology.INSTANCE.equals(JapaneseChronology.INSTANCE));
    }

    @Test
    public void test_equals_false() {
        assertFalse(JapaneseChronology.INSTANCE.equals(ISOChronology.INSTANCE));
    }

}
