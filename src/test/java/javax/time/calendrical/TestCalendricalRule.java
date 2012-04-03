/*
 * Copyright (c) 2009-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.ISODateTimeRule.YEAR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.OffsetDate;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.ZonedDateTime;
import javax.time.extended.Year;

import org.testng.annotations.Test;

/**
 * Test CalendricalRule.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestCalendricalRule {

    static class MockBigYear implements Comparable<MockBigYear> {
        private final long year;
        MockBigYear(long year) {
            this.year = year;
        }
        @Override
        public int compareTo(MockBigYear other) {
            return (int) (year - other.year);
        }
    }

    static class MockBigYearRule extends CalendricalRule<MockBigYear> implements Serializable {
        static final MockBigYearRule INSTANCE = new MockBigYearRule();
        private static final long serialVersionUID = 1L;
        protected MockBigYearRule() {
            super(MockBigYear.class, "MockBigYearRule");
        }
        @Override
        protected MockBigYear deriveFrom(CalendricalEngine engine) {
            DateTimeField year = engine.getFieldDerived(YEAR, true);
            return year != null ? new MockBigYear(year.getValidValue()) : null;
        }
    }

    //-----------------------------------------------------------------------
    // basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Comparator.class.isAssignableFrom(CalendricalRule.class));
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(MockBigYearRule.INSTANCE);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertTrue(ois.readObject() instanceof MockBigYearRule);
    }

    //-----------------------------------------------------------------------
    // getType()
    //-----------------------------------------------------------------------
    public void test_getType() {
        assertEquals(MockBigYearRule.INSTANCE.getType(), MockBigYear.class);
    }

    //-----------------------------------------------------------------------
    // getName()
    //-----------------------------------------------------------------------
    public void test_getName() {
        assertEquals(MockBigYearRule.INSTANCE.getName(), "MockBigYearRule");
    }

    //-----------------------------------------------------------------------
    // comparator()
    //-----------------------------------------------------------------------
    public void test_comparator() {
        List<Calendrical> list = new ArrayList<Calendrical>();
        LocalDate ld = LocalDate.of(2009, 6, 30);
        list.add(ld);
        LocalDateTime ldt = LocalDateTime.of(2007, 1, 1, 12, 30);
        list.add(ldt);
        OffsetDate od = OffsetDate.of(2008, 6, 30, ZoneOffset.of("+01:00"));
        list.add(od);
        
        Collections.sort(list, MockBigYearRule.INSTANCE);
        assertEquals(list.get(0), ldt);
        assertEquals(list.get(1), od);
        assertEquals(list.get(2), ld);
    }

    public void test_comparator_noValueSortedLast() {
        List<Calendrical> list = new ArrayList<Calendrical>();
        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.of(2009, 8, 20, 2, 30), ZoneId.of("Europe/London"));
        list.add(zdt);
        LocalDateTime ldt = LocalDateTime.of(2007, 1, 1, 12, 30);
        list.add(ldt);
        OffsetDate od = OffsetDate.of(2008, 6, 30, ZoneOffset.of("+01:00"));
        list.add(od);
        
        Collections.sort(list, MockBigYearRule.INSTANCE);
        assertEquals(list.get(0), ldt);
        assertEquals(list.get(1), od);
        assertEquals(list.get(2), zdt);
    }

    public void test_comparator_combinations() {
        Year year2008 = Year.of(2008);
        Year year2009 = Year.of(2009);
        assertEquals(MockBigYearRule.INSTANCE.compare(year2008, year2008), 0);
        assertEquals(MockBigYearRule.INSTANCE.compare(year2008, year2009), -1);
        assertEquals(MockBigYearRule.INSTANCE.compare(year2009, year2008), 1);
        assertEquals(MockBigYearRule.INSTANCE.compare(year2009, year2009), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_comparator_combinations_noValue1() {
        Year year2008 = Year.of(2008);
        assertEquals(MockBigYearRule.INSTANCE.compare(year2008, DateTimeFields.EMPTY), -1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_comparator_combinations_noValue2() {
        Year year2008 = Year.of(2008);
        assertEquals(MockBigYearRule.INSTANCE.compare(DateTimeFields.EMPTY, year2008), 1);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        List<Calendrical> list = new ArrayList<Calendrical>();
        LocalDate ld = LocalDate.of(2009, 6, 30);
        list.add(ld);
        LocalDateTime ldt = LocalDateTime.of(2007, 1, 1, 12, 30);
        list.add(ldt);
        OffsetDate od = OffsetDate.of(2008, 6, 30, ZoneOffset.of("+01:00"));
        list.add(od);
        
        Collections.sort(list, MockBigYearRule.INSTANCE);
        assertEquals(list.get(0), ldt);
        assertEquals(list.get(1), od);
        assertEquals(list.get(2), ld);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    public void test_equals() {
        assertEquals(MockBigYearRule.INSTANCE.equals(MockBigYearRule.INSTANCE), true);
        assertEquals(MockBigYearRule.INSTANCE.equals("OtherType"), false);
        assertEquals(MockBigYearRule.INSTANCE.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    public void test_hashCode() {
        assertEquals(MockBigYearRule.INSTANCE.hashCode(), MockBigYearRule.INSTANCE.hashCode());
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(MockBigYearRule.INSTANCE.toString(), "MockBigYearRule");
    }

}
