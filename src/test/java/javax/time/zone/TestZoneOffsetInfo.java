/*
 * Copyright (c) 2010-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.zone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import javax.time.CalendricalException;
import javax.time.OffsetDateTime;
import javax.time.ZoneOffset;

import org.testng.annotations.Test;

/**
 * Test ZoneOffsetInfo.
 */
@Test
public class TestZoneOffsetInfo {

    private static final ZoneOffset OFFSET_0100 = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_0200 = ZoneOffset.ofHours(2);
    private static final ZoneOffset OFFSET_0230 = ZoneOffset.ofHoursMinutes(2, 30);
    private static final ZoneOffset OFFSET_0300 = ZoneOffset.ofHours(3);
    private static final ZoneOffset OFFSET_0400 = ZoneOffset.ofHours(4);

    //-----------------------------------------------------------------------
    // factory
    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_nullZO() {
        ZoneOffsetInfo.ofOffset(null);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_factory_nullZOT() {
        ZoneOffsetInfo.ofTransition(null);
    }

    //-----------------------------------------------------------------------
    // getters
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_normal() throws Exception {
        ZoneOffsetInfo test = ZoneOffsetInfo.ofOffset(OFFSET_0200);
        assertEquals(test.isTransition(), false);
        assertEquals(test.getOffset(), OFFSET_0200);
        assertEquals(test.getEstimatedOffset(), OFFSET_0200);
        assertEquals(test.isValidOffset(OFFSET_0100), false);
        assertEquals(test.isValidOffset(OFFSET_0200), true);
        assertEquals(test.isValidOffset(OFFSET_0230), false);
        assertEquals(test.isValidOffset(OFFSET_0300), false);
        assertEquals(test.isValidOffset(OFFSET_0400), false);
        try {
            test.getTransition();
            fail();
        } catch (CalendricalException ex) {
            // expected
        }
    }

    @Test(groups={"tck"})
    public void test_gap() throws Exception {
        OffsetDateTime odt = OffsetDateTime.of(2010, 3, 31, 1, 0, OFFSET_0200);
        ZoneOffsetTransition zot = ZoneOffsetTransition.of(odt, OFFSET_0300);  // gap from 01:00 to 02:00
        assertEquals(zot.isGap(), true);
        
        ZoneOffsetInfo test = ZoneOffsetInfo.ofTransition(zot);
        assertEquals(test.isTransition(), true);
        assertEquals(test.getTransition(), zot);
        assertEquals(test.getEstimatedOffset(), zot.getOffsetAfter());
        assertEquals(test.isValidOffset(OFFSET_0100), false);
        assertEquals(test.isValidOffset(OFFSET_0200), false);
        assertEquals(test.isValidOffset(OFFSET_0230), false);
        assertEquals(test.isValidOffset(OFFSET_0300), false);
        assertEquals(test.isValidOffset(OFFSET_0400), false);
        try {
            test.getOffset();
            fail();
        } catch (CalendricalException ex) {
            // expected
        }
    }

    @Test(groups={"tck"})
    public void test_overlap() throws Exception {
        OffsetDateTime odt = OffsetDateTime.of(2010, 3, 31, 1, 0, OFFSET_0300);
        ZoneOffsetTransition zot = ZoneOffsetTransition.of(odt, OFFSET_0200);  // overlap from 02:00 to 01:00
        assertEquals(zot.isOverlap(), true);
        
        ZoneOffsetInfo test = ZoneOffsetInfo.ofTransition(zot);
        assertEquals(test.isTransition(), true);
        assertEquals(test.getTransition(), zot);
        assertEquals(test.getEstimatedOffset(), zot.getOffsetAfter());
        assertEquals(test.isValidOffset(OFFSET_0100), false);
        assertEquals(test.isValidOffset(OFFSET_0200), true);
        assertEquals(test.isValidOffset(OFFSET_0230), false);
        assertEquals(test.isValidOffset(OFFSET_0300), true);
        assertEquals(test.isValidOffset(OFFSET_0400), false);
        try {
            test.getOffset();
            fail();
        } catch (CalendricalException ex) {
            // expected
        }
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_equals_ZOT() {
        OffsetDateTime odtA = OffsetDateTime.of(2010, 3, 31, 1, 0, OFFSET_0200);
        ZoneOffsetTransition zota1 = ZoneOffsetTransition.of(odtA, OFFSET_0300);
        ZoneOffsetTransition zota2 = ZoneOffsetTransition.of(odtA, OFFSET_0300);
        ZoneOffsetInfo a1 = ZoneOffsetInfo.ofTransition(zota1);
        ZoneOffsetInfo a2 = ZoneOffsetInfo.ofTransition(zota2);
        OffsetDateTime odtB = OffsetDateTime.of(2010, 10, 31, 1, 0, OFFSET_0300);
        ZoneOffsetTransition zotb = ZoneOffsetTransition.of(odtB, OFFSET_0200);
        ZoneOffsetInfo b = ZoneOffsetInfo.ofTransition(zotb);
        
        assertEquals(a1.equals(a1), true);
        assertEquals(a1.equals(a2), true);
        assertEquals(a1.equals(b), false);
        assertEquals(a2.equals(a1), true);
        assertEquals(a2.equals(a2), true);
        assertEquals(a2.equals(b), false);
        
        assertEquals(b.equals(a1), false);
        assertEquals(b.equals(b), true);
    }

    @Test(groups={"tck"})
    public void test_equals_ZO() {
        ZoneOffsetInfo a1 = ZoneOffsetInfo.ofOffset(OFFSET_0100);
        ZoneOffsetInfo a2 = ZoneOffsetInfo.ofOffset(OFFSET_0100);
        ZoneOffsetInfo b = ZoneOffsetInfo.ofOffset(OFFSET_0200);
        
        assertEquals(a1.equals(a1), true);
        assertEquals(a1.equals(a2), true);
        assertEquals(a1.equals(b), false);
        assertEquals(a2.equals(a1), true);
        assertEquals(a2.equals(a2), true);
        assertEquals(a2.equals(b), false);
        
        assertEquals(b.equals(a1), false);
        assertEquals(b.equals(b), true);
    }

    @Test(groups={"tck"})
    public void test_equals_ZO_to_ZOT() {
        ZoneOffsetTransition zota = ZoneOffsetTransition.of(OffsetDateTime.of(2010, 3, 31, 1, 0, OFFSET_0200), OFFSET_0300);
        ZoneOffsetInfo a = ZoneOffsetInfo.ofTransition(zota);
        ZoneOffsetInfo b = ZoneOffsetInfo.ofOffset(OFFSET_0200);
        
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(b.equals(b), true);
    }

    @Test(groups={"tck"})
    public void test_equals_other() {
        ZoneOffsetInfo test = ZoneOffsetInfo.ofOffset(OFFSET_0100);
        assertEquals(test.equals(""), false);
        assertEquals(test.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_hashCode_ZOT() {
        ZoneOffsetTransition zot = ZoneOffsetTransition.of(OffsetDateTime.of(2010, 3, 31, 1, 0, OFFSET_0200), OFFSET_0300);
        ZoneOffsetInfo test = ZoneOffsetInfo.ofTransition(zot);
        
        assertEquals(test.hashCode(), test.hashCode());
    }

    @Test(groups={"tck"})
    public void test_hashCode_ZO() {
        ZoneOffsetInfo test = ZoneOffsetInfo.ofOffset(OFFSET_0200);
        
        assertEquals(test.hashCode(), test.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_toString_normal() {
        ZoneOffsetInfo test = ZoneOffsetInfo.ofOffset(OFFSET_0200);
        assertEquals(test.toString(), "OffsetInfo[+02:00]");
    }

    @Test(groups={"implementation"})
    public void test_toString_gap() {
        ZoneOffsetTransition zot = ZoneOffsetTransition.of(OffsetDateTime.of(2010, 3, 31, 1, 0, OFFSET_0200), OFFSET_0300);
        ZoneOffsetInfo test = ZoneOffsetInfo.ofTransition(zot);
        assertEquals(test.toString(), "OffsetInfo[Transition[Gap at 2010-03-31T01:00+02:00 to +03:00]]");
    }

    @Test(groups={"implementation"})
    public void test_toString_overlap() {
        ZoneOffsetTransition zot = ZoneOffsetTransition.of(OffsetDateTime.of(2010, 10, 31, 1, 0, OFFSET_0300), OFFSET_0200);
        ZoneOffsetInfo test = ZoneOffsetInfo.ofTransition(zot);
        assertEquals(test.toString(), "OffsetInfo[Transition[Overlap at 2010-10-31T01:00+03:00 to +02:00]]");
    }

    @Test(groups={"tck"})
    public void test_toString_normal_tck() {
        ZoneOffsetInfo test = ZoneOffsetInfo.ofOffset(OFFSET_0200);
        assertNotNull(test.toString());
    }

}
