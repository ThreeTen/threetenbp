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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.time.Instant;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.Month;
import javax.time.OffsetDateTime;
import javax.time.Period;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.zone.ZoneOffsetTransitionRule.TimeDefinition;

import org.testng.annotations.Test;

/**
 * Test ZoneRules for fixed offset time-zones.
 */
@Test
public class TestFixedZoneRules {

    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    private static final LocalDateTime LDT = LocalDateTime.of(2010, 12, 3, 11, 30);
    private static final OffsetDateTime ODT = OffsetDateTime.of(2010, 12, 3, 11, 30, OFFSET_PONE);
    private static final Instant INSTANT = ODT.toInstant();
    
    private ZoneRules make(ZoneOffset offset) {
        return ZoneId.of(offset).getRules();
    }

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    @Test(groups={"implementation","tck"})
    public void test_serialization() throws Exception {
        ZoneRules test = make(OFFSET_PONE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(test);
        baos.close();
        byte[] bytes = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bais);
        ZoneRules result = (ZoneRules) in.readObject();
        
        assertEquals(result, test);
        assertEquals(result.getClass(), test.getClass());
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    @Test(groups={"implementation","tck"})
    public void test_data() {
    	ZoneRules test = make(OFFSET_PONE);
        assertEquals(test.getDaylightSavings(INSTANT), Period.ZERO_SECONDS);
        assertEquals(test.getOffset(INSTANT), OFFSET_PONE);
        assertEquals(test.getOffsetInfo(LDT), ZoneOffsetInfo.ofOffset(OFFSET_PONE));
        assertEquals(test.getStandardOffset(INSTANT), OFFSET_PONE);
        assertEquals(test.getTransitions().size(), 0);
        assertEquals(test.getTransitionRules().size(), 0);
        assertEquals(test.nextTransition(INSTANT), null);
        assertEquals(test.previousTransition(INSTANT), null);
    }

    @Test(groups="implementation")
    public void test_data_nullInput() {
        ZoneRules test = make(OFFSET_PONE);
        assertEquals(test.getDaylightSavings(null), Period.ZERO_SECONDS);
        assertEquals(test.getOffset(null), OFFSET_PONE);
        assertEquals(test.getOffsetInfo(null), ZoneOffsetInfo.ofOffset(OFFSET_PONE));
        assertEquals(test.getStandardOffset(null), OFFSET_PONE);
        assertEquals(test.nextTransition(null), null);
        assertEquals(test.previousTransition(null), null);
    }

    @Test(groups={"implementation","tck"})
    public void test_isValidDateTime_same_offset() {
    	ZoneRules test = make(OFFSET_PONE);
        assertEquals(test.isValidDateTime(ODT), true);
    }

    @Test(groups={"implementation","tck"})
    public void test_isValidDateTime_diff_offset() {
    	ZoneRules test = make(OFFSET_PTWO);
        assertEquals(test.isValidDateTime(ODT), false);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"implementation","tck"})
    public void test_getTransitions_immutable() {
        ZoneRules test = make(OFFSET_PTWO);
        test.getTransitions().add(ZoneOffsetTransition.of(ODT, OFFSET_PTWO));
    }

    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"implementation","tck"})
    public void test_getTransitionRules_immutable() {
        ZoneRules test = make(OFFSET_PTWO);
        test.getTransitionRules().add(ZoneOffsetTransitionRule.of(Month.JULY, 2, null, LocalTime.of(12, 30), false, TimeDefinition.STANDARD, OFFSET_PONE, OFFSET_PTWO, OFFSET_PONE));
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    @Test(groups={"implementation","tck"})
    public void test_equals() {
    	ZoneRules a = make(OFFSET_PONE);
    	ZoneRules b = make(OFFSET_PTWO);
        
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(b.equals(b), true);
        
        assertEquals(a.equals("Rubbish"), false);
        assertEquals(a.equals(null), false);
        
        assertEquals(a.hashCode() == a.hashCode(), true);
        assertEquals(b.hashCode() == b.hashCode(), true);
    }

}
