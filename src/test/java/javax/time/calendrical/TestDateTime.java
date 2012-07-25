/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.OffsetDate;
import javax.time.OffsetDateTime;
import javax.time.OffsetTime;
import javax.time.ZoneOffset;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DateTime interface.
 */
@Test
public class TestDateTime {

    @DataProvider(name="objects")
    Object[][] data_objects() {
        return new Object[][] {
            {LocalDate.of(2012, 7, 27), },
            {LocalTime.of(11, 12, 30, 40), },
            {LocalDateTime.of(2012, 7, 27, 11, 12, 30, 40), },
            {OffsetDate.of(2012, 7, 27, ZoneOffset.ofHours(1)), },
            {OffsetTime.of(11, 12, 30, 40, ZoneOffset.ofHours(1)), },
            {OffsetDateTime.of(2012, 7, 27, 11, 12, 30, 40, ZoneOffset.ofHours(1)), },
            {YearMonth.of(2012, 7), },
            {MonthDay.of(7, 27), },
        };
    }

    @Test(dataProvider="objects", groups={"tck"})
    public void test_extract_preferredFields(DateTime dt) {
        DateTimeField[] fields = dt.extract(DateTimeField[].class);
        assertEquals(fields.length > 0, true);
        for (DateTimeField field : fields) {
            long value = dt.get(field);
            DateTime dt2 = dt.with(field, 1).with(field, value);
            assertEquals(dt2, dt, "Field " + field);
        }
    }

    @Test(dataProvider="objects", groups={"tck"})
    public void test_extract_fields(DateTime dt) {
        LocalDateTimeField[] fields = dt.extract(LocalDateTimeField[].class);
        assertEquals(fields.length > 0, true);
        for (LocalDateTimeField field : fields) {
            long value = dt.get(field);
            DateTime dt2 = dt.with(field, 1).with(field, value);
            assertEquals(dt2, dt, "Field " + field);
        }
    }

    @Test(dataProvider="objects", groups={"tck"})
    public void test_extract_units(DateTime dt) {
        LocalPeriodUnit[] units = dt.extract(LocalPeriodUnit[].class);
        if (dt instanceof AdjustableDateTime) {
            assertEquals(units.length > 0, true);
            for (LocalPeriodUnit unit : units) {
                DateTime dt2 = ((AdjustableDateTime) dt).plus(1, unit).minus(1, unit);
                assertEquals(dt2, dt, "Unit " + unit);
            }
        } else {
            assertEquals(units.length, 0);
        }
    }

}
