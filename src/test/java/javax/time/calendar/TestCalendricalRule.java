/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.time.calendar.field.Year;
import javax.time.calendar.format.MockSimpleCalendrical;

import org.testng.annotations.Test;

/**
 * Test CalendricalRule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestCalendricalRule {

    static class MockBigYearRule extends CalendricalRule<BigInteger> {
        private static final long serialVersionUID = 1L;
        protected MockBigYearRule() {
            super(BigInteger.class, ISOChronology.INSTANCE, "MockBigYearRule", ISOChronology.periodYears(), null);
        }
        @Override
        protected BigInteger derive(Calendrical calendrical) {
            Integer year = calendrical.get(ISOChronology.yearRule());
            return year != null ? BigInteger.valueOf(year) : null;
        }
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Comparable.class.isAssignableFrom(CalendricalRule.class));
        assertTrue(Comparator.class.isAssignableFrom(CalendricalRule.class));
        assertTrue(Serializable.class.isAssignableFrom(CalendricalRule.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new MockBigYearRule());
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertTrue(ois.readObject() instanceof MockBigYearRule);
    }

    //-----------------------------------------------------------------------
    // getReifiedType()
    //-----------------------------------------------------------------------
    public void test_getReifiedType() {
        assertEquals(new MockBigYearRule().getReifiedType(), BigInteger.class);
    }

    //-----------------------------------------------------------------------
    // reify(Object)
    //-----------------------------------------------------------------------
    public void test_reify() {
        BigInteger test = new MockBigYearRule().reify(BigInteger.ONE);
        assertEquals(test, BigInteger.ONE);
    }

    @Test(expectedExceptions=ClassCastException.class)
    public void test_reify_wrongType() {
        new MockBigYearRule().reify(Integer.valueOf(0));
    }

    //-----------------------------------------------------------------------
    // getName()
    //-----------------------------------------------------------------------
    public void test_getName() {
        assertEquals(new MockBigYearRule().getName(), "MockBigYearRule");
    }

    //-----------------------------------------------------------------------
    // getID()
    //-----------------------------------------------------------------------
    public void test_getID() {
        assertEquals(new MockBigYearRule().getID(), "ISO.MockBigYearRule");
    }

    //-----------------------------------------------------------------------
    // comparator()
    //-----------------------------------------------------------------------
    public void test_comparator() {
        List<Calendrical> list = new ArrayList<Calendrical>();
        LocalDate ld = LocalDate.date(2009, 6, 30);
        list.add(ld);
        LocalDateTime ldt = LocalDateTime.dateTime(2007, 1, 1, 12, 30);
        list.add(ldt);
        OffsetDate od = OffsetDate.date(2008, 6, 30, ZoneOffset.zoneOffset("+01:00"));
        list.add(od);
        
        Collections.sort(list, new MockBigYearRule());
        assertEquals(list.get(0), ldt);
        assertEquals(list.get(1), od);
        assertEquals(list.get(2), ld);
    }

    public void test_comparator_noValueSortedLast() {
        List<Calendrical> list = new ArrayList<Calendrical>();
        LocalTime lt = LocalTime.time(12, 30);
        list.add(lt);
        LocalDateTime ldt = LocalDateTime.dateTime(2007, 1, 1, 12, 30);
        list.add(ldt);
        OffsetDate od = OffsetDate.date(2008, 6, 30, ZoneOffset.zoneOffset("+01:00"));
        list.add(od);
        
        Collections.sort(list, new MockBigYearRule());
        assertEquals(list.get(0), ldt);
        assertEquals(list.get(1), od);
        assertEquals(list.get(2), lt);
    }

    public void test_comparator_combinations() {
        Year year2008 = Year.isoYear(2008);
        Year year2009 = Year.isoYear(2009);
        assertEquals(new MockBigYearRule().compare(year2008, year2008), 0);
        assertEquals(new MockBigYearRule().compare(year2008, year2009), -1);
        assertEquals(new MockBigYearRule().compare(year2009, year2008), 1);
        assertEquals(new MockBigYearRule().compare(year2009, year2009), 0);
    }

    public void test_comparator_combinations_noValue() {
        Year year2008 = Year.isoYear(2008);
        assertEquals(new MockBigYearRule().compare(year2008, new MockSimpleCalendrical()), -1);
        assertEquals(new MockBigYearRule().compare(new MockSimpleCalendrical(), year2008), 1);
        assertEquals(new MockBigYearRule().compare(new MockSimpleCalendrical(), new MockSimpleCalendrical()), 0);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        List<Calendrical> list = new ArrayList<Calendrical>();
        LocalDate ld = LocalDate.date(2009, 6, 30);
        list.add(ld);
        LocalDateTime ldt = LocalDateTime.dateTime(2007, 1, 1, 12, 30);
        list.add(ldt);
        OffsetDate od = OffsetDate.date(2008, 6, 30, ZoneOffset.zoneOffset("+01:00"));
        list.add(od);
        
        Collections.sort(list, new MockBigYearRule());
        assertEquals(list.get(0), ldt);
        assertEquals(list.get(1), od);
        assertEquals(list.get(2), ld);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    public void test_equals() {
        assertEquals(new MockBigYearRule().equals(new MockBigYearRule()), true);
        assertEquals(new MockBigYearRule().equals("OtherType"), false);
        assertEquals(new MockBigYearRule().equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    public void test_hashCode() {
        assertEquals(new MockBigYearRule().hashCode(), "ISO.MockBigYearRule".hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(new MockBigYearRule().toString(), "ISO.MockBigYearRule");
    }

}
