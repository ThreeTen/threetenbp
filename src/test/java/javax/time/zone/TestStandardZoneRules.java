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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import javax.time.DayOfWeek;
import javax.time.Instant;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.Month;
import javax.time.OffsetDateTime;
import javax.time.Period;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.calendrical.Year;
import javax.time.zone.ZoneOffsetTransitionRule.TimeDefinition;

import org.testng.annotations.Test;

/**
 * Test ZoneRules.
 */
@Test(groups="implementation")
public class TestStandardZoneRules {

    private static final ZoneOffset OFFSET_ZERO = ZoneOffset.ofHours(0);
    private static final ZoneOffset OFFSET_PONE = ZoneOffset.ofHours(1);
    private static final ZoneOffset OFFSET_PTWO = ZoneOffset.ofHours(2);
    public static final String LATEST_TZDB = "2009b";

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Serializable.class.isAssignableFrom(StandardZoneRules.class));
    }

    public void test_immutable() {
        Class<StandardZoneRules> cls = StandardZoneRules.class;
        assertFalse(Modifier.isPublic(cls.getModifiers()));
        assertFalse(Modifier.isProtected(cls.getModifiers()));
        assertFalse(Modifier.isPrivate(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
                assertTrue(Modifier.isPrivate(field.getModifiers()));
                assertTrue(Modifier.isFinal(field.getModifiers()) ||
                        (Modifier.isVolatile(field.getModifiers()) && Modifier.isTransient(field.getModifiers())), "" + field);
            }
        }
    }

    public void test_serialization_simple() throws Exception {
        ZoneRulesBuilder b = new ZoneRulesBuilder()
            .addWindow(OFFSET_PONE, LocalDateTime.of(1980, 3, 1, 1, 0), TimeDefinition.STANDARD)
            .setFixedSavingsToWindow(1 * 60 * 60)
            .addWindowForever(OFFSET_PONE)
            .setFixedSavingsToWindow(2 * 60 * 60);
        ZoneRules test = b.toRules("Test");
        assertSerialization(test);
    }

    public void test_serialization_unusual() throws Exception {
        ZoneRulesBuilder b = new ZoneRulesBuilder()
            .addWindow(ZoneOffset.of("-17:49:23"), LocalDateTime.of(1980, 3, 1, 1, 34, 56), TimeDefinition.WALL)
            .setFixedSavingsToWindow(((1 * 60 + 34) * 60) + 23)
            .addWindowForever(ZoneOffset.of("+04:23"))
            .setFixedSavingsToWindow(((13 * 60 + 22) * 60) + 9);
        ZoneRules test = b.toRules("Test");
        assertSerialization(test);
    }

    public void test_serialization_loaded() throws Exception {
        assertSerialization(europeLondon());
        assertSerialization(europeParis());
        assertSerialization(americaNewYork());
    }

    private void assertSerialization(ZoneRules test) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(test);
        baos.close();
        byte[] bytes = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bais);
        StandardZoneRules result = (StandardZoneRules) in.readObject();
        
        assertEquals(result, test);
    }

    //-----------------------------------------------------------------------
    // Europe/London
    //-----------------------------------------------------------------------
    private StandardZoneRules europeLondon() {
        return (StandardZoneRules) ZoneId.of("Europe/London").getRules();
    }

    public void test_London() {
        StandardZoneRules test = europeLondon();
        assertEquals(test.isFixedOffset(), false);
    }

    public void test_London_preTimeZones() {
        StandardZoneRules test = europeLondon();
        OffsetDateTime old = OffsetDateTime.ofMidnight(1800, 1, 1, ZoneOffset.UTC);
        Instant instant = old.toInstant();
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(0, -1, -15);
        assertEquals(test.getOffset(instant), offset);
        checkOffset(test.getOffsetInfo(old.toLocalDateTime()), offset);
        assertEquals(test.getStandardOffset(instant), offset);
        assertEquals(test.getDaylightSavings(instant), Period.ZERO_SECONDS);
        assertEquals(test.isDaylightSavings(instant), false);
    }

    public void test_London_getOffset() {
        StandardZoneRules test = europeLondon();
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 1, 1, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 2, 1, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 1, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 4, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 5, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 6, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 7, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 8, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 9, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 1, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 12, 1, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
    }

    public void test_London_getOffset_toDST() {
        StandardZoneRules test = europeLondon();
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 24, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 25, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 26, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 27, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 28, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 29, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 30, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 31, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        // cutover at 01:00Z
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 3, 30, 0, 59, 59, 999999999, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 3, 30, 1, 0, 0, 0, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
    }

    public void test_London_getOffset_fromDST() {
        StandardZoneRules test = europeLondon();
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 24, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 25, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 26, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 27, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 28, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 29, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 30, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 31, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
        // cutover at 01:00Z
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 10, 26, 0, 59, 59, 999999999, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 10, 26, 1, 0, 0, 0, ZoneOffset.UTC).toInstant()), OFFSET_ZERO);
    }

    public void test_London_getOffsetInfo() {
        StandardZoneRules test = europeLondon();
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 1, 1)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 2, 1)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 1)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 4, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 5, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 6, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 7, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 8, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 9, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 1)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 12, 1)), OFFSET_ZERO);
    }

    public void test_London_getOffsetInfo_toDST() {
        StandardZoneRules test = europeLondon();
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 24)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 25)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 26)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 27)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 28)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 29)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 30)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 31)), OFFSET_PONE);
        // cutover at 01:00Z
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 3, 30, 0, 59, 59, 999999999)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 3, 30, 2, 0, 0, 0)), OFFSET_PONE);
    }

    public void test_London_getOffsetInfo_fromDST() {
        StandardZoneRules test = europeLondon();
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 24)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 25)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 26)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 27)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 28)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 29)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 30)), OFFSET_ZERO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 31)), OFFSET_ZERO);
        // cutover at 01:00Z
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 10, 26, 0, 59, 59, 999999999)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 10, 26, 2, 0, 0, 0)), OFFSET_ZERO);
    }

    public void test_London_getOffsetInfo_gap() {
        StandardZoneRules test = europeLondon();
        final LocalDateTime dateTime = LocalDateTime.of(2008, 3, 30, 1, 0, 0, 0);
        ZoneOffsetInfo info = test.getOffsetInfo(dateTime);
        assertEquals(info.isTransition(), true);
        assertEquals(info.getOffset(), null);
        assertEquals(info.getEstimatedOffset(), OFFSET_PONE);
        ZoneOffsetTransition dis = info.getTransition();
        assertEquals(dis.isGap(), true);
        assertEquals(dis.isOverlap(), false);
        assertEquals(dis.getOffsetBefore(), OFFSET_ZERO);
        assertEquals(dis.getOffsetAfter(), OFFSET_PONE);
        assertEquals(dis.getInstant(), OffsetDateTime.of(2008, 3, 30, 1, 0, ZoneOffset.UTC).toInstant());
        assertEquals(dis.getDateTimeBefore(), OffsetDateTime.of(2008, 3, 30, 1, 0, OFFSET_ZERO));
        assertEquals(dis.getDateTimeAfter(), OffsetDateTime.of(2008, 3, 30, 2, 0, OFFSET_PONE));
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(-1)), false);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(0)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(1)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(2)), false);
        assertEquals(dis.isValidOffset(OFFSET_ZERO), false);
        assertEquals(dis.isValidOffset(OFFSET_PONE), false);
        assertEquals(dis.toString(), "Transition[Gap at 2008-03-30T01:00Z to +01:00]");

        assertFalse(dis.equals(null));
        assertFalse(dis.equals(OFFSET_ZERO));
        assertTrue(dis.equals(dis));

        final ZoneOffsetTransition otherDis = test.getOffsetInfo(dateTime).getTransition();
        assertTrue(dis.equals(otherDis));

        assertEquals(dis.hashCode(), otherDis.hashCode());
    }

    public void test_London_getOffsetInfo_overlap() {
        StandardZoneRules test = europeLondon();
        final LocalDateTime dateTime = LocalDateTime.of(2008, 10, 26, 1, 0, 0, 0);
        ZoneOffsetInfo info = test.getOffsetInfo(dateTime);
        assertEquals(info.isTransition(), true);
        assertEquals(info.getOffset(), null);
        assertEquals(info.getEstimatedOffset(), OFFSET_ZERO);
        ZoneOffsetTransition dis = info.getTransition();
        assertEquals(dis.isGap(), false);
        assertEquals(dis.isOverlap(), true);
        assertEquals(dis.getOffsetBefore(), OFFSET_PONE);
        assertEquals(dis.getOffsetAfter(), OFFSET_ZERO);
        assertEquals(dis.getInstant(), OffsetDateTime.of(2008, 10, 26, 1, 0, ZoneOffset.UTC).toInstant());
        assertEquals(dis.getDateTimeBefore(), OffsetDateTime.of(2008, 10, 26, 2, 0, OFFSET_PONE));
        assertEquals(dis.getDateTimeAfter(), OffsetDateTime.of(2008, 10, 26, 1, 0, OFFSET_ZERO));
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(-1)), false);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(0)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(1)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(2)), false);
        assertEquals(dis.isValidOffset(ZoneOffset.ofHours(-1)), false);
        assertEquals(dis.isValidOffset(OFFSET_ZERO), true);
        assertEquals(dis.isValidOffset(OFFSET_PONE), true);
        assertEquals(dis.isValidOffset(OFFSET_PTWO), false);
        assertEquals(dis.toString(), "Transition[Overlap at 2008-10-26T02:00+01:00 to Z]");

        assertFalse(dis.equals(null));
        assertFalse(dis.equals(OFFSET_PONE));
        assertTrue(dis.equals(dis));

        final ZoneOffsetTransition otherDis = test.getOffsetInfo(dateTime).getTransition();
        assertTrue(dis.equals(otherDis));

        assertEquals(dis.hashCode(), otherDis.hashCode());
    }

    public void test_London_getStandardOffset() {
        StandardZoneRules test = europeLondon();
        OffsetDateTime dateTime = LocalDateTime.ofMidnight(1840, 1, 1).atOffset(ZoneOffset.UTC);
        while (dateTime.getYear() < 2010) {
            Instant instant = dateTime.toInstant();
            if (dateTime.getYear() < 1848) {
                assertEquals(test.getStandardOffset(instant), ZoneOffset.ofHoursMinutesSeconds(0, -1, -15));
            } else if (dateTime.getYear() >= 1969 && dateTime.getYear() < 1972) {
                assertEquals(test.getStandardOffset(instant), OFFSET_PONE);
            } else {
                assertEquals(test.getStandardOffset(instant), OFFSET_ZERO);
            }
            dateTime = dateTime.plusMonths(6);
        }
    }

    public void test_London_getTransitions() {
        StandardZoneRules test = europeLondon();
        List<ZoneOffsetTransition> trans = test.getTransitions();
        
        ZoneOffsetTransition first = trans.get(0);
        assertEquals(first.getLocal(), LocalDateTime.of(1847, 12, 1, 0, 0));
        assertEquals(first.getOffsetBefore(), ZoneOffset.ofHoursMinutesSeconds(0, -1, -15));
        assertEquals(first.getOffsetAfter(), OFFSET_ZERO);
        
        ZoneOffsetTransition spring1916 = trans.get(1);
        assertEquals(spring1916.getLocal(), LocalDateTime.of(1916, 5, 21, 2, 0));
        assertEquals(spring1916.getOffsetBefore(), OFFSET_ZERO);
        assertEquals(spring1916.getOffsetAfter(), OFFSET_PONE);
        
        ZoneOffsetTransition autumn1916 = trans.get(2);
        assertEquals(autumn1916.getLocal(), LocalDateTime.of(1916, 10, 1, 3, 0));
        assertEquals(autumn1916.getOffsetBefore(), OFFSET_PONE);
        assertEquals(autumn1916.getOffsetAfter(), OFFSET_ZERO);
        
        ZoneOffsetTransition zot = null;
        Iterator<ZoneOffsetTransition> it = trans.iterator();
        while (it.hasNext()) {
            zot = it.next();
            if (zot.getLocal().getYear() == 1990) {
                break;
            }
        }
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1990, 3, 25, 1, 0, OFFSET_ZERO));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1990, 10, 28, 2, 0, OFFSET_PONE));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1991, 3, 31, 1, 0, OFFSET_ZERO));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1991, 10, 27, 2, 0, OFFSET_PONE));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1992, 3, 29, 1, 0, OFFSET_ZERO));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1992, 10, 25, 2, 0, OFFSET_PONE));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1993, 3, 28, 1, 0, OFFSET_ZERO));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1993, 10, 24, 2, 0, OFFSET_PONE));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1994, 3, 27, 1, 0, OFFSET_ZERO));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1994, 10, 23, 2, 0, OFFSET_PONE));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1995, 3, 26, 1, 0, OFFSET_ZERO));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1995, 10, 22, 2, 0, OFFSET_PONE));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1996, 3, 31, 1, 0, OFFSET_ZERO));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1996, 10, 27, 2, 0, OFFSET_PONE));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1997, 3, 30, 1, 0, OFFSET_ZERO));
        zot = it.next();
        assertEquals(zot.getDateTimeBefore(), OffsetDateTime.of(1997, 10, 26, 2, 0, OFFSET_PONE));
        assertEquals(it.hasNext(), false);
    }

    public void test_London_getTransitionRules() {
        StandardZoneRules test = europeLondon();
        List<ZoneOffsetTransitionRule> rules = test.getTransitionRules();
        assertEquals(rules.size(), 2);
        
        ZoneOffsetTransitionRule in = rules.get(0);
        assertEquals(in.getMonth(), Month.MARCH);
        assertEquals(in.getDayOfMonthIndicator(), 25);  // optimized from -1
        assertEquals(in.getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(in.getLocalTime(), LocalTime.of(1, 0));
        assertEquals(in.getTimeDefinition(), TimeDefinition.UTC);
        assertEquals(in.getStandardOffset(), OFFSET_ZERO);
        assertEquals(in.getOffsetBefore(), OFFSET_ZERO);
        assertEquals(in.getOffsetAfter(), OFFSET_PONE);
        
        ZoneOffsetTransitionRule out = rules.get(1);
        assertEquals(out.getMonth(), Month.OCTOBER);
        assertEquals(out.getDayOfMonthIndicator(), 25);  // optimized from -1
        assertEquals(out.getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(out.getLocalTime(), LocalTime.of(1, 0));
        assertEquals(out.getTimeDefinition(), TimeDefinition.UTC);
        assertEquals(out.getStandardOffset(), OFFSET_ZERO);
        assertEquals(out.getOffsetBefore(), OFFSET_PONE);
        assertEquals(out.getOffsetAfter(), OFFSET_ZERO);
    }

    //-----------------------------------------------------------------------
    public void test_London_nextTransition_historic() {
        StandardZoneRules test = europeLondon();
        List<ZoneOffsetTransition> trans = test.getTransitions();
        
        ZoneOffsetTransition first = trans.get(0);
        assertEquals(test.nextTransition(first.getInstant().minusNanos(1)), first);
        
        for (int i = 0; i < trans.size() - 1; i++) {
            ZoneOffsetTransition cur = trans.get(i);
            ZoneOffsetTransition next = trans.get(i + 1);
            
            assertEquals(test.nextTransition(cur.getInstant()), next);
            assertEquals(test.nextTransition(next.getInstant().minusNanos(1)), next);
        }
    }

    public void test_London_nextTransition_rulesBased() {
        StandardZoneRules test = europeLondon();
        List<ZoneOffsetTransitionRule> rules = test.getTransitionRules();
        List<ZoneOffsetTransition> trans = test.getTransitions();
        
        ZoneOffsetTransition last = trans.get(trans.size() - 1);
        assertEquals(test.nextTransition(last.getInstant()), rules.get(0).createTransition(1998));
        
        for (int year = 1998; year < 2010; year++) {
            ZoneOffsetTransition a = rules.get(0).createTransition(year);
            ZoneOffsetTransition b = rules.get(1).createTransition(year);
            ZoneOffsetTransition c = rules.get(0).createTransition(year + 1);
            
            assertEquals(test.nextTransition(a.getInstant()), b);
            assertEquals(test.nextTransition(b.getInstant().minusNanos(1)), b);
            
            assertEquals(test.nextTransition(b.getInstant()), c);
            assertEquals(test.nextTransition(c.getInstant().minusNanos(1)), c);
        }
    }

    public void test_London_nextTransition_lastYear() {
        StandardZoneRules test = europeLondon();
        List<ZoneOffsetTransitionRule> rules = test.getTransitionRules();
        ZoneOffsetTransition zot = rules.get(1).createTransition(Year.MAX_YEAR);
        assertEquals(test.nextTransition(zot.getInstant()), null);
    }

    //-----------------------------------------------------------------------
    public void test_London_previousTransition_historic() {
        StandardZoneRules test = europeLondon();
        List<ZoneOffsetTransition> trans = test.getTransitions();
        
        ZoneOffsetTransition first = trans.get(0);
        assertEquals(test.previousTransition(first.getInstant()), null);
        assertEquals(test.previousTransition(first.getInstant().minusNanos(1)), null);
        
        for (int i = 0; i < trans.size() - 1; i++) {
            ZoneOffsetTransition prev = trans.get(i);
            ZoneOffsetTransition cur = trans.get(i + 1);
            
            assertEquals(test.previousTransition(cur.getInstant()), prev);
            assertEquals(test.previousTransition(prev.getInstant().plusSeconds(1)), prev);
            assertEquals(test.previousTransition(prev.getInstant().plusNanos(1)), prev);
        }
    }

    public void test_London_previousTransition_rulesBased() {
        StandardZoneRules test = europeLondon();
        List<ZoneOffsetTransitionRule> rules = test.getTransitionRules();
        List<ZoneOffsetTransition> trans = test.getTransitions();
        
        ZoneOffsetTransition last = trans.get(trans.size() - 1);
        assertEquals(test.previousTransition(last.getInstant().plusSeconds(1)), last);
        assertEquals(test.previousTransition(last.getInstant().plusNanos(1)), last);
        
        for (int year = 1998; year < 2010; year++) {
            ZoneOffsetTransition a = rules.get(0).createTransition(year);
            ZoneOffsetTransition b = rules.get(1).createTransition(year);
            ZoneOffsetTransition c = rules.get(0).createTransition(year + 1);
            
            assertEquals(test.previousTransition(c.getInstant()), b);
            assertEquals(test.previousTransition(b.getInstant().plusSeconds(1)), b);
            assertEquals(test.previousTransition(b.getInstant().plusNanos(1)), b);
            
            assertEquals(test.previousTransition(b.getInstant()), a);
            assertEquals(test.previousTransition(a.getInstant().plusSeconds(1)), a);
            assertEquals(test.previousTransition(a.getInstant().plusNanos(1)), a);
        }
    }

    //-----------------------------------------------------------------------
    // Europe/Paris
    //-----------------------------------------------------------------------
    private StandardZoneRules europeParis() {
        return (StandardZoneRules) ZoneId.of("Europe/Paris").getRules();
    }

    public void test_Paris() {
        StandardZoneRules test = europeParis();
        assertEquals(test.isFixedOffset(), false);
    }

    public void test_Paris_preTimeZones() {
        StandardZoneRules test = europeParis();
        OffsetDateTime old = OffsetDateTime.ofMidnight(1800, 1, 1, ZoneOffset.UTC);
        Instant instant = old.toInstant();
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(0, 9, 21);
        assertEquals(test.getOffset(instant), offset);
        checkOffset(test.getOffsetInfo(old.toLocalDateTime()), offset);
        assertEquals(test.getStandardOffset(instant), offset);
        assertEquals(test.getDaylightSavings(instant), Period.ZERO_SECONDS);
        assertEquals(test.isDaylightSavings(instant), false);
    }

    public void test_Paris_getOffset() {
        StandardZoneRules test = europeParis();
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 1, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 2, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 4, 1, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 5, 1, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 6, 1, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 7, 1, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 8, 1, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 9, 1, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 1, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 12, 1, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
    }

    public void test_Paris_getOffset_toDST() {
        StandardZoneRules test = europeParis();
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 24, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 25, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 26, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 27, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 28, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 29, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 30, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 31, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        // cutover at 01:00Z
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 3, 30, 0, 59, 59, 999999999, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 3, 30, 1, 0, 0, 0, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
    }

    public void test_Paris_getOffset_fromDST() {
        StandardZoneRules test = europeParis();
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 24, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 25, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 26, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 27, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 28, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 29, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 30, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 31, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
        // cutover at 01:00Z
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 10, 26, 0, 59, 59, 999999999, ZoneOffset.UTC).toInstant()), OFFSET_PTWO);
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 10, 26, 1, 0, 0, 0, ZoneOffset.UTC).toInstant()), OFFSET_PONE);
    }

    public void test_Paris_getOffsetInfo() {
        StandardZoneRules test = europeParis();
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 1, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 2, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 4, 1)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 5, 1)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 6, 1)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 7, 1)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 8, 1)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 9, 1)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 1)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 1)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 12, 1)), OFFSET_PONE);
    }

    public void test_Paris_getOffsetInfo_toDST() {
        StandardZoneRules test = europeParis();
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 24)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 25)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 26)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 27)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 28)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 29)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 30)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 31)), OFFSET_PTWO);
        // cutover at 01:00Z which is 02:00+01:00(local Paris time)
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 3, 30, 1, 59, 59, 999999999)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 3, 30, 3, 0, 0, 0)), OFFSET_PTWO);
    }

    public void test_Paris_getOffsetInfo_fromDST() {
        StandardZoneRules test = europeParis();
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 24)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 25)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 26)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 27)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 28)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 29)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 30)), OFFSET_PONE);
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 31)), OFFSET_PONE);
        // cutover at 01:00Z which is 02:00+01:00(local Paris time)
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 10, 26, 1, 59, 59, 999999999)), OFFSET_PTWO);
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 10, 26, 3, 0, 0, 0)), OFFSET_PONE);
    }

    public void test_Paris_getOffsetInfo_gap() {
        StandardZoneRules test = europeParis();
        final LocalDateTime dateTime = LocalDateTime.of(2008, 3, 30, 2, 0, 0, 0);
        ZoneOffsetInfo info = test.getOffsetInfo(dateTime);
        assertEquals(info.isTransition(), true);
        assertEquals(info.getOffset(), null);
        assertEquals(info.getEstimatedOffset(), OFFSET_PTWO);
        ZoneOffsetTransition dis = info.getTransition();
        assertEquals(dis.isGap(), true);
        assertEquals(dis.isOverlap(), false);
        assertEquals(dis.getOffsetBefore(), OFFSET_PONE);
        assertEquals(dis.getOffsetAfter(), OFFSET_PTWO);
        assertEquals(dis.getInstant(), OffsetDateTime.of(2008, 3, 30, 1, 0, ZoneOffset.UTC).toInstant());
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(0)), false);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(1)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(2)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(3)), false);
        assertEquals(dis.isValidOffset(OFFSET_PONE), false);
        assertEquals(dis.isValidOffset(OFFSET_PTWO), false);
        assertEquals(dis.toString(), "Transition[Gap at 2008-03-30T02:00+01:00 to +02:00]");

        assertFalse(dis.equals(null));
        assertFalse(dis.equals(OFFSET_PONE));
        assertTrue(dis.equals(dis));

        final ZoneOffsetTransition otherDis = test.getOffsetInfo(dateTime).getTransition();
        assertTrue(dis.equals(otherDis));

        assertEquals(dis.hashCode(), otherDis.hashCode());
    }

    public void test_Paris_getOffsetInfo_overlap() {
        StandardZoneRules test = europeParis();
        final LocalDateTime dateTime = LocalDateTime.of(2008, 10, 26, 2, 0, 0, 0);
        ZoneOffsetInfo info = test.getOffsetInfo(dateTime);
        assertEquals(info.isTransition(), true);
        assertEquals(info.getOffset(), null);
        assertEquals(info.getEstimatedOffset(), OFFSET_PONE);
        ZoneOffsetTransition dis = info.getTransition();
        assertEquals(dis.isGap(), false);
        assertEquals(dis.isOverlap(), true);
        assertEquals(dis.getOffsetBefore(), OFFSET_PTWO);
        assertEquals(dis.getOffsetAfter(), OFFSET_PONE);
        assertEquals(dis.getInstant(), OffsetDateTime.of(2008, 10, 26, 1, 0, ZoneOffset.UTC).toInstant());
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(0)), false);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(1)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(2)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(3)), false);
        assertEquals(dis.isValidOffset(OFFSET_ZERO), false);
        assertEquals(dis.isValidOffset(OFFSET_PONE), true);
        assertEquals(dis.isValidOffset(OFFSET_PTWO), true);
        assertEquals(dis.isValidOffset(ZoneOffset.ofHours(3)), false);
        assertEquals(dis.toString(), "Transition[Overlap at 2008-10-26T03:00+02:00 to +01:00]");

        assertFalse(dis.equals(null));
        assertFalse(dis.equals(OFFSET_PTWO));
        assertTrue(dis.equals(dis));

        final ZoneOffsetTransition otherDis = test.getOffsetInfo(dateTime).getTransition();
        assertTrue(dis.equals(otherDis));

        assertEquals(dis.hashCode(), otherDis.hashCode());
    }

    public void test_Paris_getStandardOffset() {
        StandardZoneRules test = europeParis();
        OffsetDateTime dateTime = LocalDateTime.ofMidnight(1840, 1, 1).atOffset(ZoneOffset.UTC);
        while (dateTime.getYear() < 2010) {
            Instant instant = dateTime.toInstant();
            if (dateTime.toLocalDate().isBefore(LocalDate.of(1911, 3, 11))) {
                assertEquals(test.getStandardOffset(instant), ZoneOffset.ofHoursMinutesSeconds(0, 9, 21));
            } else if (dateTime.toLocalDate().isBefore(LocalDate.of(1940, 6, 14))) {
                assertEquals(test.getStandardOffset(instant), OFFSET_ZERO);
            } else if (dateTime.toLocalDate().isBefore(LocalDate.of(1944, 8, 25))) {
                assertEquals(test.getStandardOffset(instant), OFFSET_PONE);
            } else if (dateTime.toLocalDate().isBefore(LocalDate.of(1945, 9, 16))) {
                assertEquals(test.getStandardOffset(instant), OFFSET_ZERO);
            } else {
                assertEquals(test.getStandardOffset(instant), OFFSET_PONE);
            }
            dateTime = dateTime.plusMonths(6);
        }
    }

    //-----------------------------------------------------------------------
    // America/New_York
    //-----------------------------------------------------------------------
    private StandardZoneRules americaNewYork() {
        return (StandardZoneRules) ZoneId.of("America/New_York").getRules();
    }

    public void test_NewYork() {
        StandardZoneRules test = americaNewYork();
        assertEquals(test.isFixedOffset(), false);
    }

    public void test_NewYork_preTimeZones() {
        StandardZoneRules test = americaNewYork();
        OffsetDateTime old = OffsetDateTime.ofMidnight(1800, 1, 1, ZoneOffset.UTC);
        Instant instant = old.toInstant();
        ZoneOffset offset = ZoneOffset.of("-04:56:02");
        assertEquals(test.getOffset(instant), offset);
        checkOffset(test.getOffsetInfo(old.toLocalDateTime()), offset);
        assertEquals(test.getStandardOffset(instant), offset);
        assertEquals(test.getDaylightSavings(instant), Period.ZERO_SECONDS);
        assertEquals(test.isDaylightSavings(instant), false);
    }

    public void test_NewYork_getOffset() {
        StandardZoneRules test = americaNewYork();
        ZoneOffset offset = ZoneOffset.ofHours(-5);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 1, 1, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 2, 1, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 1, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 4, 1, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 5, 1, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 6, 1, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 7, 1, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 8, 1, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 9, 1, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 1, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 1, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 12, 1, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 1, 28, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 2, 28, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 28, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 4, 28, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 5, 28, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 6, 28, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 7, 28, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 8, 28, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 9, 28, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 10, 28, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 28, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 12, 28, offset).toInstant()), ZoneOffset.ofHours(-5));
    }

    public void test_NewYork_getOffset_toDST() {
        StandardZoneRules test = americaNewYork();
        ZoneOffset offset = ZoneOffset.ofHours(-5);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 8, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 9, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 10, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 11, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 12, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 13, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 3, 14, offset).toInstant()), ZoneOffset.ofHours(-4));
        // cutover at 02:00 local
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 3, 9, 1, 59, 59, 999999999, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 3, 9, 2, 0, 0, 0, offset).toInstant()), ZoneOffset.ofHours(-4));
    }

    public void test_NewYork_getOffset_fromDST() {
        StandardZoneRules test = americaNewYork();
        ZoneOffset offset = ZoneOffset.ofHours(-4);
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 1, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 2, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 3, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 4, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 5, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 6, offset).toInstant()), ZoneOffset.ofHours(-5));
        assertEquals(test.getOffset(OffsetDateTime.ofMidnight(2008, 11, 7, offset).toInstant()), ZoneOffset.ofHours(-5));
        // cutover at 02:00 local
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 11, 2, 1, 59, 59, 999999999, offset).toInstant()), ZoneOffset.ofHours(-4));
        assertEquals(test.getOffset(OffsetDateTime.of(2008, 11, 2, 2, 0, 0, 0, offset).toInstant()), ZoneOffset.ofHours(-5));
    }

    public void test_NewYork_getOffsetInfo() {
        StandardZoneRules test = americaNewYork();
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 1, 1)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 2, 1)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 1)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 4, 1)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 5, 1)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 6, 1)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 7, 1)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 8, 1)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 9, 1)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 1)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 1)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 12, 1)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 1, 28)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 2, 28)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 28)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 4, 28)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 5, 28)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 6, 28)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 7, 28)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 8, 28)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 9, 28)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 10, 28)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 28)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 12, 28)), ZoneOffset.ofHours(-5));
    }

    public void test_NewYork_getOffsetInfo_toDST() {
        StandardZoneRules test = americaNewYork();
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 8)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 9)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 10)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 11)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 12)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 13)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 3, 14)), ZoneOffset.ofHours(-4));
        // cutover at 02:00 local
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 3, 9, 1, 59, 59, 999999999)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 3, 9, 3, 0, 0, 0)), ZoneOffset.ofHours(-4));
    }

    public void test_NewYork_getOffsetInfo_fromDST() {
        StandardZoneRules test = americaNewYork();
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 1)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 2)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 3)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 4)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 5)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 6)), ZoneOffset.ofHours(-5));
        checkOffset(test.getOffsetInfo(LocalDateTime.ofMidnight(2008, 11, 7)), ZoneOffset.ofHours(-5));
        // cutover at 02:00 local
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 11, 2, 0, 59, 59, 999999999)), ZoneOffset.ofHours(-4));
        checkOffset(test.getOffsetInfo(LocalDateTime.of(2008, 11, 2, 2, 0, 0, 0)), ZoneOffset.ofHours(-5));
    }

    public void test_NewYork_getOffsetInfo_gap() {
        StandardZoneRules test = americaNewYork();
        final LocalDateTime dateTime = LocalDateTime.of(2008, 3, 9, 2, 0, 0, 0);
        ZoneOffsetInfo info = test.getOffsetInfo(dateTime);
        assertEquals(info.isTransition(), true);
        assertEquals(info.getOffset(), null);
        assertEquals(info.getEstimatedOffset(), ZoneOffset.ofHours(-4));
        ZoneOffsetTransition dis = info.getTransition();
        assertEquals(dis.isGap(), true);
        assertEquals(dis.isOverlap(), false);
        assertEquals(dis.getOffsetBefore(), ZoneOffset.ofHours(-5));
        assertEquals(dis.getOffsetAfter(), ZoneOffset.ofHours(-4));
        assertEquals(dis.getInstant(), OffsetDateTime.of(2008, 3, 9, 2, 0, ZoneOffset.ofHours(-5)).toInstant());
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(-1)), false);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(-5)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(-4)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(2)), false);
        assertEquals(dis.isValidOffset(ZoneOffset.ofHours(-5)), false);
        assertEquals(dis.isValidOffset(ZoneOffset.ofHours(-4)), false);
        assertEquals(dis.toString(), "Transition[Gap at 2008-03-09T02:00-05:00 to -04:00]");

        assertFalse(dis.equals(null));
        assertFalse(dis.equals(ZoneOffset.ofHours(-5)));
        assertTrue(dis.equals(dis));

        final ZoneOffsetTransition otherDis = test.getOffsetInfo(dateTime).getTransition();
        assertTrue(dis.equals(otherDis));

        assertEquals(dis.hashCode(), otherDis.hashCode());
    }

    public void test_NewYork_getOffsetInfo_overlap() {
        StandardZoneRules test = americaNewYork();
        final LocalDateTime dateTime = LocalDateTime.of(2008, 11, 2, 1, 0, 0, 0);
        ZoneOffsetInfo info = test.getOffsetInfo(dateTime);
        assertEquals(info.isTransition(), true);
        assertEquals(info.getOffset(), null);
        assertEquals(info.getEstimatedOffset(), ZoneOffset.ofHours(-5));
        ZoneOffsetTransition dis = info.getTransition();
        assertEquals(dis.isGap(), false);
        assertEquals(dis.isOverlap(), true);
        assertEquals(dis.getOffsetBefore(), ZoneOffset.ofHours(-4));
        assertEquals(dis.getOffsetAfter(), ZoneOffset.ofHours(-5));
        assertEquals(dis.getInstant(), OffsetDateTime.of(2008, 11, 2, 2, 0, ZoneOffset.ofHours(-4)).toInstant());
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(-1)), false);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(-5)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(-4)), true);
//        assertEquals(dis.containsOffset(ZoneOffset.zoneOffset(2)), false);
        assertEquals(dis.isValidOffset(ZoneOffset.ofHours(-1)), false);
        assertEquals(dis.isValidOffset(ZoneOffset.ofHours(-5)), true);
        assertEquals(dis.isValidOffset(ZoneOffset.ofHours(-4)), true);
        assertEquals(dis.isValidOffset(OFFSET_PTWO), false);
        assertEquals(dis.toString(), "Transition[Overlap at 2008-11-02T02:00-04:00 to -05:00]");

        assertFalse(dis.equals(null));
        assertFalse(dis.equals(ZoneOffset.ofHours(-4)));
        assertTrue(dis.equals(dis));

        final ZoneOffsetTransition otherDis = test.getOffsetInfo(dateTime).getTransition();
        assertTrue(dis.equals(otherDis));

        assertEquals(dis.hashCode(), otherDis.hashCode());
    }

    public void test_NewYork_getStandardOffset() {
        StandardZoneRules test = americaNewYork();
        OffsetDateTime dateTime = LocalDateTime.ofMidnight(1860, 1, 1).atOffset(ZoneOffset.UTC);
        while (dateTime.getYear() < 2010) {
            Instant instant = dateTime.toInstant();
            if (dateTime.toLocalDate().isBefore(LocalDate.of(1883, 11, 18))) {
                assertEquals(test.getStandardOffset(instant), ZoneOffset.of("-04:56:02"));
            } else {
                assertEquals(test.getStandardOffset(instant), ZoneOffset.ofHours(-5));
            }
            dateTime = dateTime.plusMonths(6);
        }
    }

    //-----------------------------------------------------------------------
    // Kathmandu
    //-----------------------------------------------------------------------
    private StandardZoneRules asiaKathmandu() {
        return (StandardZoneRules) ZoneId.of("Asia/Kathmandu").getRules();
    }

    public void test_Kathmandu_nextTransition_historic() {
        StandardZoneRules test = asiaKathmandu();
        List<ZoneOffsetTransition> trans = test.getTransitions();
        
        ZoneOffsetTransition first = trans.get(0);
        assertEquals(test.nextTransition(first.getInstant().minusNanos(1)), first);
        
        for (int i = 0; i < trans.size() - 1; i++) {
            ZoneOffsetTransition cur = trans.get(i);
            ZoneOffsetTransition next = trans.get(i + 1);
            
            assertEquals(test.nextTransition(cur.getInstant()), next);
            assertEquals(test.nextTransition(next.getInstant().minusNanos(1)), next);
        }
    }

    public void test_Kathmandu_nextTransition_noRules() {
        StandardZoneRules test = asiaKathmandu();
        List<ZoneOffsetTransition> trans = test.getTransitions();
        
        ZoneOffsetTransition last = trans.get(trans.size() - 1);
        assertEquals(test.nextTransition(last.getInstant()), null);
    }

    //-------------------------------------------------------------------------
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_getTransitions_immutable() {
        ZoneRules test = europeParis();
        test.getTransitions().clear();
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_getTransitionRules_immutable() {
        ZoneRules test = europeParis();
        test.getTransitionRules().clear();
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        StandardZoneRules test1 = europeLondon();
        StandardZoneRules test2 = europeParis();
        StandardZoneRules test2b = europeParis();
        assertEquals(test1.equals(test2), false);
        assertEquals(test2.equals(test1), false);
        
        assertEquals(test1.equals(test1), true);
        assertEquals(test2.equals(test2), true);
        assertEquals(test2.equals(test2b), true);
        
        assertEquals(test1.hashCode() == test1.hashCode(), true);
        assertEquals(test2.hashCode() == test2.hashCode(), true);
        assertEquals(test2.hashCode() == test2b.hashCode(), true);
    }

    public void test_equals_null() {
        assertEquals(europeLondon().equals(null), false);
    }

    public void test_equals_notStandardZoneRules() {
        assertEquals(europeLondon().equals("Europe/London"), false);
    }

    public void test_toString() {
        assertEquals(europeLondon().toString().startsWith("StandardZoneRules["), true);
        assertEquals(europeLondon().toString().endsWith("]"), true);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    private void checkOffset(ZoneOffsetInfo info, ZoneOffset zoneOffset) {
        assertEquals(info.isTransition(), false);
        assertEquals(info.getTransition(), null);
        assertEquals(info.getOffset(), zoneOffset);
        assertEquals(info.getEstimatedOffset(), zoneOffset);
//        assertEquals(info.containsOffset(zoneOffset), true);
        assertEquals(info.isValidOffset(zoneOffset), true);
    }

}
