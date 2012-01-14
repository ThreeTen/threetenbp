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

import org.testng.annotations.Test;

/**
 * Test ISODateTimeRule.
 */
@Test
public class TestISODateTimeRule {

    public void test_packPemd() {
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
    }

}
