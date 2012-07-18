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
package javax.time.calendrical;

import static org.testng.Assert.assertEquals;

import javax.time.LocalDate;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test.
 */
@Test
public class TestJulianDayField {

    private static final LocalDate JAN01_1970 = LocalDate.of(1970, 1, 1);
    private static final LocalDate DEC31_1969 = LocalDate.of(1969, 12, 31);
    private static final LocalDate NOV12_1945 = LocalDate.of(1945, 11, 12);
    private static final LocalDate JAN01_0001 = LocalDate.of(1, 1, 1);

    @BeforeMethod
    public void setUp() {
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="samples")
    Object[][] data_samples() {
        return new Object[][] {
            {LocalDateTimeField.EPOCH_DAY, JAN01_1970, 0L},
            {JulianDayField.JULIAN_DAY, JAN01_1970, 2400001L + 40587L},
            {JulianDayField.MODIFIED_JULIAN_DAY, JAN01_1970, 40587L},
            {JulianDayField.RATA_DIE, JAN01_1970, 710347L + (40587L - 31771L)},
            
            {LocalDateTimeField.EPOCH_DAY, DEC31_1969, -1L},
            {JulianDayField.JULIAN_DAY, DEC31_1969, 2400001L + 40586L},
            {JulianDayField.MODIFIED_JULIAN_DAY, DEC31_1969, 40586L},
            {JulianDayField.RATA_DIE, DEC31_1969, 710347L + (40586L - 31771L)},
            
            {LocalDateTimeField.EPOCH_DAY, NOV12_1945, (-24 * 365 - 6) - 31 - 30 + 11},
            {JulianDayField.JULIAN_DAY, NOV12_1945, 2431772L},
            {JulianDayField.MODIFIED_JULIAN_DAY, NOV12_1945, 31771L},
            {JulianDayField.RATA_DIE, NOV12_1945, 710347L},
            
            {LocalDateTimeField.EPOCH_DAY, JAN01_0001, (-24 * 365 - 6) - 31 - 30 + 11 - 710346L},
            {JulianDayField.JULIAN_DAY, JAN01_0001, 2431772L - 710346L},
            {JulianDayField.MODIFIED_JULIAN_DAY, JAN01_0001, 31771L - 710346L},
            {JulianDayField.RATA_DIE, JAN01_0001, 1},
        };
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_samples_get(DateTimeField field, LocalDate date, long expected) {
        assertEquals(date.get(field), expected);
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_samples_set(DateTimeField field, LocalDate date, long value) {
        assertEquals(field.doSet(LocalDate.MAX_DATE, value), date);
        assertEquals(field.doSet(LocalDate.MIN_DATE, value), date);
        assertEquals(field.doSet(JAN01_1970, value), date);
        assertEquals(field.doSet(DEC31_1969, value), date);
        assertEquals(field.doSet(NOV12_1945, value), date);
    }

    @Test(dataProvider="samples", groups={"tck"})
    public void test_samples_date(DateTimeField field, LocalDate date, long value) {
        if (field instanceof JulianDayField) {
            assertEquals(((JulianDayField) field).createDate(value), date);
        }
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        assertEquals(JulianDayField.JULIAN_DAY.toString(), "JulianDay");
        assertEquals(JulianDayField.MODIFIED_JULIAN_DAY.toString(), "ModifiedJulianDay");
        assertEquals(JulianDayField.RATA_DIE.toString(), "RataDie");
    }

    //-----------------------------------------------------------------------
    // generated methods
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_enum() {
        assertEquals(JulianDayField.valueOf("JULIAN_DAY"), JulianDayField.JULIAN_DAY);
        assertEquals(JulianDayField.values()[0], JulianDayField.JULIAN_DAY);
    }

}
