/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test ISODateTimeRule.
 */
@Test
public class TestISODateTimeRule {

    public void test_packPemd_em_dom() {
        assertEquals(ISODateTimeRule.packPemd(0, 1), 0);
        assertEquals(ISODateTimeRule.packPemd(0, 2), 1);
        assertEquals(ISODateTimeRule.packPemd(0, 3), 2);
        assertEquals(ISODateTimeRule.packPemd(0, 29), 28);
        assertEquals(ISODateTimeRule.packPemd(0, 30), 29);
        assertEquals(ISODateTimeRule.packPemd(0, 31), 30);
        
        assertEquals(ISODateTimeRule.packPemd(1, 1), 32 + 0);
        assertEquals(ISODateTimeRule.packPemd(1, 2), 32 + 1);
        assertEquals(ISODateTimeRule.packPemd(1, 27), 32 + 26);
        assertEquals(ISODateTimeRule.packPemd(1, 28), 32 + 27);
        
        assertEquals(ISODateTimeRule.packPemd(2, 1), 64 + 0);
        assertEquals(ISODateTimeRule.packPemd(2, 2), 64 + 1);
        assertEquals(ISODateTimeRule.packPemd(2, 30), 64 + 29);
        assertEquals(ISODateTimeRule.packPemd(2, 31), 64 + 30);
        
        assertEquals(ISODateTimeRule.packPemd(-1, 1), -32 + 0);
        assertEquals(ISODateTimeRule.packPemd(-1, 2), -32 + 1);
        assertEquals(ISODateTimeRule.packPemd(-1, 30), -32 + 29);
        assertEquals(ISODateTimeRule.packPemd(-1, 31), -32 + 30);
        
        assertEquals(ISODateTimeRule.packPemd(-2, 1), -64 + 0);
        assertEquals(ISODateTimeRule.packPemd(-2, 2), -64 + 1);
        assertEquals(ISODateTimeRule.packPemd(-2, 29), -64 + 28);
        assertEquals(ISODateTimeRule.packPemd(-2, 30), -64 + 29);
    }

    public void test_packPemd_y_moy_dom() {
        assertEquals(ISODateTimeRule.packPemd(1970, 1, 1), 0);
        assertEquals(ISODateTimeRule.packPemd(1970, 1, 2), 1);
        assertEquals(ISODateTimeRule.packPemd(1970, 1, 3), 2);
        assertEquals(ISODateTimeRule.packPemd(1970, 1, 29), 28);
        assertEquals(ISODateTimeRule.packPemd(1970, 1, 30), 29);
        assertEquals(ISODateTimeRule.packPemd(1970, 1, 31), 30);
        
        assertEquals(ISODateTimeRule.packPemd(1970, 2, 1), 32 + 0);
        assertEquals(ISODateTimeRule.packPemd(1970, 2, 2), 32 + 1);
        assertEquals(ISODateTimeRule.packPemd(1970, 2, 27), 32 + 26);
        assertEquals(ISODateTimeRule.packPemd(1970, 2, 28), 32 + 27);
        
        assertEquals(ISODateTimeRule.packPemd(1970, 3, 1), 64 + 0);
        assertEquals(ISODateTimeRule.packPemd(1970, 3, 2), 64 + 1);
        assertEquals(ISODateTimeRule.packPemd(1970, 3, 30), 64 + 29);
        assertEquals(ISODateTimeRule.packPemd(1970, 3, 31), 64 + 30);
        
        assertEquals(ISODateTimeRule.packPemd(1969, 12, 1), -32 + 0);
        assertEquals(ISODateTimeRule.packPemd(1969, 12, 2), -32 + 1);
        assertEquals(ISODateTimeRule.packPemd(1969, 12, 30), -32 + 29);
        assertEquals(ISODateTimeRule.packPemd(1969, 12, 31), -32 + 30);
        
        assertEquals(ISODateTimeRule.packPemd(1969, 11, 1), -64 + 0);
        assertEquals(ISODateTimeRule.packPemd(1969, 11, 2), -64 + 1);
        assertEquals(ISODateTimeRule.packPemd(1969, 11, 29), -64 + 28);
        assertEquals(ISODateTimeRule.packPemd(1969, 11, 30), -64 + 29);
    }

    @DataProvider(name="packedDateEpochDay")
    public Object[][] data_packedDateEpochDay() {
        return new Object[][] {
            {0, 0},
            {1, 1},
            {2, 2},
            {29, 29},
            {30, 30},  // 1970-01-31
            
            {31, 32 + 0},  // 1970-02-01
            {32, 32 + 1},
            {57, 32 + 26},
            {58, 32 + 27},
            
            {59, 64 + 0},  // 1970-03-01
            {60, 64 + 1},
            
            {-31, -32 + 0},  // 1969-12-01
            {-30, -32 + 1},
            {-2, -32 + 29},
            {-1, -32 + 30},
            
            {-61, -64 + 0},  // 1969-11-01
            {-60, -64 + 1},
            {-33, -64 + 28},
            {-32, -64 + 29},
            
            {58 + 365 + 365, 32 * (12 * 2 + 1) + 27},  // 1972-02-28
            {59 + 365 + 365, 32 * (12 * 2 + 1) + 28},  // 1972-02-29
            {60 + 365 + 365, 32 * (12 * 2 + 2) + 0},   // 1972-03-01
            
            {58 + 365 + 365 + 366 + 365 + 365 + 365, 32 * (12 * 6 + 1) + 27},  // 1976-02-28
            {59 + 365 + 365 + 366 + 365 + 365 + 365, 32 * (12 * 6 + 1) + 28},  // 1976-02-29
            {60 + 365 + 365 + 366 + 365 + 365 + 365, 32 * (12 * 6 + 2) + 0},   // 1976-03-01
        };
    }

    @Test(dataProvider = "packedDateEpochDay")
    public void test_packedDateFromEpochDay(long epochDay, long pemd) {
        assertEquals(ISODateTimeRule.packedDateFromEpochDay(epochDay), pemd);
    }

    @Test(dataProvider = "packedDateEpochDay")
    public void test_epochDayFromPackedDate(long epochDay, long pemd) {
        assertEquals(ISODateTimeRule.epochDayFromPackedDate(pemd), epochDay);
    }

}
