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
package javax.time.zone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;

import javax.time.CalendricalException;
import javax.time.LocalDateTime;
import javax.time.OffsetDateTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;

import org.testng.annotations.Test;

/**
 * Test ZoneResolvers.
 */
@Test
public class TestZoneResolvers {

    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneOffset OFFSET_UTC = ZoneOffset.UTC;
    private static final ZoneOffset OFFSET_0100 = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_0200 = ZoneOffset.ofHours(2);
    private static final LocalDateTime DT_PARIS_OVERLAP = dateTime(2008, 10, 26, 2, 30, 0, 0);
    private static final LocalDateTime DT_PARIS_GAP = dateTime(2008, 3, 30, 2, 30, 0, 0);
    private static final LocalDateTime DT_WINTER = dateTime(2008, 1, 1, 2, 30, 0, 0);
    private static final LocalDateTime DT_SUMMER = dateTime(2008, 6, 1, 2, 30, 0, 0);

    @SuppressWarnings("rawtypes")
    public void test_constructor() throws Exception {
        for (Constructor constructor : ZoneResolvers.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expectedExceptions = InvocationTargetException.class)
    public void test_forceCoverage() throws Exception {
        Enum en = (Enum) ZoneResolvers.strict();
        Class cls = en.getClass();
        Method m1 = cls.getMethod("values");
        m1.invoke(null);
        Method m2 = cls.getMethod("valueOf", String.class);
        m2.invoke(null, en.name());
        m2.invoke(null, "NOTREAL");
    }

    //-----------------------------------------------------------------------
    // strict()
    //-----------------------------------------------------------------------
    public void strict_factory() {
        assertNotNull(ZoneResolvers.strict());
        assertSame(ZoneResolvers.strict(), ZoneResolvers.strict());
    }

//    public void strict_serialization() throws IOException, ClassNotFoundException {
//        ZoneResolver strict = ZoneResolvers.strict();
//        assertTrue(strict instanceof Serializable);
//        
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(baos);
//        oos.writeObject(strict);
//        oos.close();
//        
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
//                baos.toByteArray()));
//        assertSame(ois.readObject(), strict);
//    }

    public void strict_winter() {
        OffsetDateTime resolved = resolve(ZoneResolvers.strict(), DT_WINTER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_WINTER);
        assertEquals(resolved.getOffset(), OFFSET_0100);
    }

    public void strict_summer() {
        OffsetDateTime resolved = resolve(ZoneResolvers.strict(), DT_SUMMER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_SUMMER);
        assertEquals(resolved.getOffset(), OFFSET_0200);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void strict_gap() {
        resolve(ZoneResolvers.strict(), DT_PARIS_GAP, ZONE_PARIS);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void strict_overlap() {
        resolve(ZoneResolvers.strict(), DT_PARIS_OVERLAP, ZONE_PARIS);
    }

    //-----------------------------------------------------------------------
    // preTransition()
    //-----------------------------------------------------------------------
    public void preTransition_factory() {
        assertNotNull(ZoneResolvers.preTransition());
        assertSame(ZoneResolvers.preTransition(), ZoneResolvers.preTransition());
    }

    public void preTransition_winter() {
        OffsetDateTime resolved = resolve(ZoneResolvers.preTransition(), DT_WINTER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_WINTER);
        assertEquals(resolved.getOffset(), OFFSET_0100);
    }

    public void preTransition_summer() {
        OffsetDateTime resolved = resolve(ZoneResolvers.preTransition(), DT_SUMMER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_SUMMER);
        assertEquals(resolved.getOffset(), OFFSET_0200);
    }

    public void preTransition_gap() {
        OffsetDateTime resolved = resolve(ZoneResolvers.preTransition(), DT_PARIS_GAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 1, 59, 59, 999999999));
        assertEquals(resolved.getOffset(), OFFSET_0100);  // chooses earlier
    }

    public void preTransition_overlap() {
        OffsetDateTime resolved = resolve(ZoneResolvers.preTransition(), DT_PARIS_OVERLAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_PARIS_OVERLAP);
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses earlier
    }

    //-----------------------------------------------------------------------
    // postTransition()
    //-----------------------------------------------------------------------
    public void postTransition_factory() {
        assertNotNull(ZoneResolvers.postTransition());
        assertSame(ZoneResolvers.postTransition(), ZoneResolvers.postTransition());
    }

    public void postTransition_winter() {
        OffsetDateTime resolved = resolve(ZoneResolvers.postTransition(), DT_WINTER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_WINTER);
        assertEquals(resolved.getOffset(), OFFSET_0100);
    }

    public void postTransition_summer() {
        OffsetDateTime resolved = resolve(ZoneResolvers.postTransition(), DT_SUMMER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_SUMMER);
        assertEquals(resolved.getOffset(), OFFSET_0200);
    }

    public void postTransition_gap() {
        OffsetDateTime resolved = resolve(ZoneResolvers.postTransition(), DT_PARIS_GAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 3, 0, 0, 0));
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses later
    }

    public void postTransition_overlap() {
        OffsetDateTime resolved = resolve(ZoneResolvers.postTransition(), DT_PARIS_OVERLAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_PARIS_OVERLAP);
        assertEquals(resolved.getOffset(), OFFSET_0100);  // chooses later
    }

    //-----------------------------------------------------------------------
    // postGapPreOverlap()
    //-----------------------------------------------------------------------
    public void postGapPreOverlap_factory() {
        assertNotNull(ZoneResolvers.postGapPreOverlap());
        assertSame(ZoneResolvers.postGapPreOverlap(), ZoneResolvers.postGapPreOverlap());
    }

    public void postGapPreOverlap_winter() {
        OffsetDateTime resolved = resolve(ZoneResolvers.postGapPreOverlap(), DT_WINTER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_WINTER);
        assertEquals(resolved.getOffset(), OFFSET_0100);
    }

    public void postGapPreOverlap_summer() {
        OffsetDateTime resolved = resolve(ZoneResolvers.postGapPreOverlap(), DT_SUMMER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_SUMMER);
        assertEquals(resolved.getOffset(), OFFSET_0200);
    }

    public void postGapPreOverlap_gap() {
        OffsetDateTime resolved = resolve(ZoneResolvers.postGapPreOverlap(), DT_PARIS_GAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 3, 0, 0, 0));
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses later
    }

    public void postGapPreOverlap_overlap() {
        OffsetDateTime resolved = resolve(ZoneResolvers.postGapPreOverlap(), DT_PARIS_OVERLAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_PARIS_OVERLAP);
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses earlier
    }

    //-----------------------------------------------------------------------
    // retainOffset()
    //-----------------------------------------------------------------------
    public void retainOffset_factory() {
        assertNotNull(ZoneResolvers.retainOffset());
        assertSame(ZoneResolvers.retainOffset(), ZoneResolvers.retainOffset());
    }

    public void retainOffset_winter() {
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_WINTER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_WINTER);
        assertEquals(resolved.getOffset(), OFFSET_0100);
    }

    public void retainOffset_summer() {
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_SUMMER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_SUMMER);
        assertEquals(resolved.getOffset(), OFFSET_0200);
    }

    public void retainOffset_gap_noOld() {
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_PARIS_GAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 3, 0, 0, 0));
        assertEquals(resolved.getOffset(), OFFSET_0200);
    }

    public void retainOffset_gap_oldEarlierOffset() {
        OffsetDateTime old = OffsetDateTime.of(2008, 1, 1, 0, 0, OFFSET_0100);
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_PARIS_GAP, ZONE_PARIS, old);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 3, 0, 0, 0));
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses post transition
    }

    public void retainOffset_gap_oldLaterOffset() {
        OffsetDateTime old = OffsetDateTime.of(2008, 6, 1, 0, 0, OFFSET_0200);
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_PARIS_GAP, ZONE_PARIS, old);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 3, 0, 0, 0));
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses post transition
    }

    public void retainOffset_gap_oldNotValidOffset() {
        OffsetDateTime old = OffsetDateTime.of(DT_PARIS_GAP, OFFSET_UTC);
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_PARIS_GAP, ZONE_PARIS, old);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 3, 0, 0, 0));
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses post gap
    }

    public void retainOffset_overlap_noOld() {
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_PARIS_OVERLAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_PARIS_OVERLAP);
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses pre overlap
    }

    public void retainOffset_overlap_oldEarlierOffset() {
        OffsetDateTime old = OffsetDateTime.of(2008, 6, 1, 0, 0, OFFSET_0200);
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_PARIS_OVERLAP, ZONE_PARIS, old);
        assertEquals(resolved.toLocalDateTime(), DT_PARIS_OVERLAP);
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses same as input
    }

    public void retainOffset_overlap_oldLaterOffset() {
        OffsetDateTime old = OffsetDateTime.of(2008, 11, 1, 0, 0, OFFSET_0100);
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_PARIS_OVERLAP, ZONE_PARIS, old);
        assertEquals(resolved.toLocalDateTime(), DT_PARIS_OVERLAP);
        assertEquals(resolved.getOffset(), OFFSET_0100);  // chooses same as input
    }

    public void retainOffset_overlap_oldNotValidOffset() {
        OffsetDateTime old = OffsetDateTime.of(DT_PARIS_OVERLAP, OFFSET_UTC);
        OffsetDateTime resolved = resolve(ZoneResolvers.retainOffset(), DT_PARIS_OVERLAP, ZONE_PARIS, old);
        assertEquals(resolved.toLocalDateTime(), DT_PARIS_OVERLAP);
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses pre overlap
    }

    //-----------------------------------------------------------------------
    // pushForward()
    //-----------------------------------------------------------------------
    public void pushForward_factory() {
        assertNotNull(ZoneResolvers.pushForward());
        assertSame(ZoneResolvers.pushForward(), ZoneResolvers.pushForward());
    }

    public void pushForward_winter() {
        OffsetDateTime resolved = resolve(ZoneResolvers.pushForward(), DT_WINTER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_WINTER);
        assertEquals(resolved.getOffset(), OFFSET_0100);
    }

    public void pushForward_summer() {
        OffsetDateTime resolved = resolve(ZoneResolvers.pushForward(), DT_SUMMER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_SUMMER);
        assertEquals(resolved.getOffset(), OFFSET_0200);
    }

    public void pushForward_gap() {
        OffsetDateTime resolved = resolve(ZoneResolvers.pushForward(), DT_PARIS_GAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 3, 30, 0, 0));
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses later
    }

    public void pushForward_gap2() {
        OffsetDateTime resolved = resolve(ZoneResolvers.pushForward(), dateTime(2008, 3, 30, 2, 22, 0, 0), ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 3, 22, 0, 0));  // pushed by one hour
        assertEquals(resolved.getOffset(), OFFSET_0200);  // chooses later
    }

    public void pushForward_overlap() {
        OffsetDateTime resolved = resolve(ZoneResolvers.pushForward(), DT_PARIS_OVERLAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_PARIS_OVERLAP);
        assertEquals(resolved.getOffset(), OFFSET_0100);  // chooses later
    }

    //-----------------------------------------------------------------------
    // combination()
    //-----------------------------------------------------------------------
    public void combination_factory() {
        ZoneResolver combo = ZoneResolvers.combination(ZoneResolvers.preTransition(), ZoneResolvers.postTransition());
        assertNotNull(combo);
    }

    public void combination_factory_sameNoCreate() {
        ZoneResolver combo = ZoneResolvers.combination(ZoneResolvers.preTransition(), ZoneResolvers.preTransition());
        assertSame(combo, ZoneResolvers.preTransition());
    }

    public void combination_winter() {
        ZoneResolver combo = ZoneResolvers.combination(ZoneResolvers.preTransition(), ZoneResolvers.postTransition());
        OffsetDateTime resolved = resolve(combo, DT_WINTER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_WINTER);
        assertEquals(resolved.getOffset(), OFFSET_0100);
    }

    public void combination_summer() {
        ZoneResolver combo = ZoneResolvers.combination(ZoneResolvers.preTransition(), ZoneResolvers.postTransition());
        OffsetDateTime resolved = resolve(combo, DT_SUMMER, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_SUMMER);
        assertEquals(resolved.getOffset(), OFFSET_0200);
    }

    public void combination_gap() {
        ZoneResolver combo = ZoneResolvers.combination(ZoneResolvers.preTransition(), ZoneResolvers.postTransition());
        OffsetDateTime resolved = resolve(combo, DT_PARIS_GAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), dateTime(2008, 3, 30, 1, 59, 59, 999999999));
        assertEquals(resolved.getOffset(), OFFSET_0100);  // chooses earlier, from preTransition
    }

    public void combination_overlap() {
        ZoneResolver combo = ZoneResolvers.combination(ZoneResolvers.preTransition(), ZoneResolvers.postTransition());
        OffsetDateTime resolved = resolve(combo, DT_PARIS_OVERLAP, ZONE_PARIS);
        assertEquals(resolved.toLocalDateTime(), DT_PARIS_OVERLAP);
        assertEquals(resolved.getOffset(), OFFSET_0100);  // chooses later, from postTransition
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void combination_factory_nulls() {
        ZoneResolvers.combination(null, null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void combination_factory_nullGap() {
        ZoneResolvers.combination(null, ZoneResolvers.preTransition());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void combination_nullOverlap() {
        ZoneResolvers.combination(ZoneResolvers.preTransition(), null);
    }

    //-----------------------------------------------------------------------
    private OffsetDateTime resolve(ZoneResolver resolver, LocalDateTime desiredLocal, ZoneId zone) {
        ZoneRules rules = zone.getRules();
        return resolver.resolve(desiredLocal, rules.getOffsetInfo(desiredLocal), rules, zone, null);
    }

    private OffsetDateTime resolve(ZoneResolver resolver, LocalDateTime desiredLocal, ZoneId zone, OffsetDateTime old) {
        ZoneRules rules = zone.getRules();
        return resolver.resolve(desiredLocal, rules.getOffsetInfo(desiredLocal), rules, zone, old);
    }

    private static LocalDateTime dateTime(int year, int month, int day, int h, int m, int s, int n) {
        return LocalDateTime.of(year, month, day, h, m, s, n);
    }

}
