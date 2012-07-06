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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestCopticChrono {

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name="samples")
    Object[][] data_samples() {
        return new Object[][] {
            {CopticChrono.INSTANCE.date(1, 1, 1), LocalDate.of(284, 8, 29)},
            {CopticChrono.INSTANCE.date(1, 1, 2), LocalDate.of(284, 8, 30)},
            {CopticChrono.INSTANCE.date(1, 1, 3), LocalDate.of(284, 8, 31)},
            
            {CopticChrono.INSTANCE.date(2, 1, 1), LocalDate.of(285, 8, 29)},
            {CopticChrono.INSTANCE.date(3, 1, 1), LocalDate.of(286, 8, 29)},
            {CopticChrono.INSTANCE.date(3, 13, 6), LocalDate.of(287, 8, 29)},
            {CopticChrono.INSTANCE.date(4, 1, 1), LocalDate.of(287, 8, 30)},
            {CopticChrono.INSTANCE.date(4, 7, 3), LocalDate.of(288, 2, 28)},
            {CopticChrono.INSTANCE.date(4, 7, 4), LocalDate.of(288, 2, 29)},
            {CopticChrono.INSTANCE.date(5, 1, 1), LocalDate.of(288, 8, 29)},
            {CopticChrono.INSTANCE.date(1662, 3, 3), LocalDate.of(1945, 11, 12)},
            {CopticChrono.INSTANCE.date(1728, 10, 28), LocalDate.of(2012, 7, 5)},
            {CopticChrono.INSTANCE.date(1728, 10, 29), LocalDate.of(2012, 7, 6)},
        };
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_toLocalDate(ChronoDate coptic, LocalDate iso) {
        assertEquals(coptic.toLocalDate(), iso);
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_fromCalendrical(ChronoDate coptic, LocalDate iso) {
        assertEquals(CopticChrono.INSTANCE.date(iso), coptic);
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
            {1728, 1, 31},
            {1728, 1, 32},
            
            {1728, 12, -1},
            {1728, 12, 0},
            {1728, 12, 31},
            {1728, 12, 32},
            
            {1728, 13, -1},
            {1728, 13, 0},
            {1728, 13, 6},
            {1728, 13, 7},
            
            {1727, 13, -1},
            {1727, 13, 0},
            {1727, 13, 7},
            {1727, 13, 8},
        };
    }

    @Test(dataProvider="badDates", groups={"tck"}, expectedExceptions=CalendricalException.class)
    public void test_badDates(int year, int month, int dom) {
        CopticChrono.INSTANCE.date(year, month, dom);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="toString")
    Object[][] data_toString() {
        return new Object[][] {
            {CopticChrono.INSTANCE.date(1, 1, 1), "0001AM-01-01 (Coptic)"},
            {CopticChrono.INSTANCE.date(1728, 10, 28), "1728AM-10-28 (Coptic)"},
            {CopticChrono.INSTANCE.date(1728, 10, 29), "1728AM-10-29 (Coptic)"},
            {CopticChrono.INSTANCE.date(1727, 13, 5), "1727AM-13-05 (Coptic)"},
            {CopticChrono.INSTANCE.date(1727, 13, 6), "1727AM-13-06 (Coptic)"},
        };
    }

    @Test(dataProvider="toString", groups={"tck"})
    public void test_toString(ChronoDate coptic, String expected) {
        assertEquals(coptic.toString(), expected);
    }

}
