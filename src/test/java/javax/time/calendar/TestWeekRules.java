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

import static javax.time.calendar.DayOfWeek.FRIDAY;
import static javax.time.calendar.DayOfWeek.MONDAY;
import static javax.time.calendar.DayOfWeek.SATURDAY;
import static javax.time.calendar.DayOfWeek.SUNDAY;
import static javax.time.calendar.DayOfWeek.THURSDAY;
import static javax.time.calendar.DayOfWeek.TUESDAY;
import static javax.time.calendar.DayOfWeek.WEDNESDAY;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test WeekRules.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestWeekRules {

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(Comparable.class.isAssignableFrom(WeekRules.class));
        assertTrue(Serializable.class.isAssignableFrom(WeekRules.class));
    }

    //-----------------------------------------------------------------------
    // factory ISO8601
    //-----------------------------------------------------------------------
    public void test_constant_ISO8601() {
        assertEquals(WeekRules.ISO8601.getFirstDayOfWeek(), MONDAY);
        assertEquals(WeekRules.ISO8601.getMinimalDaysInFirstWeek(), 4);
    }

    //-----------------------------------------------------------------------
    // factory of
    //-----------------------------------------------------------------------
    public void test_factory_of() {
        assertEquals(WeekRules.of(MONDAY, 4).getFirstDayOfWeek(), MONDAY);
        assertEquals(WeekRules.of(TUESDAY, 4).getFirstDayOfWeek(), TUESDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).getFirstDayOfWeek(), SUNDAY);
        
        assertEquals(WeekRules.of(MONDAY, 4).getMinimalDaysInFirstWeek(), 4);
        assertEquals(WeekRules.of(MONDAY, 1).getMinimalDaysInFirstWeek(), 1);
        assertEquals(WeekRules.of(MONDAY, 7).getMinimalDaysInFirstWeek(), 7);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_of_null() {
        WeekRules.of(null, 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_of_tooSmall() {
        WeekRules.of(MONDAY, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_of_tooLarge() {
        WeekRules.of(MONDAY, 8);
    }

    //-----------------------------------------------------------------------
    // factory locale
    //-----------------------------------------------------------------------
    public void test_factory_ofLocale() {
        assertEquals(WeekRules.of(Locale.US).getFirstDayOfWeek(), SUNDAY);
        assertEquals(1, WeekRules.of(Locale.US).getMinimalDaysInFirstWeek(), 1);
        
        assertEquals(WeekRules.of(Locale.FRANCE).getFirstDayOfWeek(), MONDAY);
        assertEquals(WeekRules.of(Locale.FRANCE).getMinimalDaysInFirstWeek(), 4);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_ofLocale_null() {
        WeekRules.of((Locale) null);
    }

    //-----------------------------------------------------------------------
    // serialization
    //-----------------------------------------------------------------------
    public void test_serialization() throws Exception {
        WeekRules orginal = WeekRules.of(MONDAY, 3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        WeekRules ser = (WeekRules) in.readObject();
        assertEquals(WeekRules.of(MONDAY, 3), ser);
    }

    //-----------------------------------------------------------------------
    // convertDayOfWeekToValue()
    //-----------------------------------------------------------------------
    public void test_convertDayOfWeekToValue_MondayBased() {
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeekToValue(MONDAY), 1);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeekToValue(TUESDAY), 2);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeekToValue(WEDNESDAY), 3);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeekToValue(THURSDAY), 4);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeekToValue(FRIDAY), 5);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeekToValue(SATURDAY), 6);
        assertEquals(WeekRules.of(MONDAY, 4).convertDayOfWeekToValue(SUNDAY), 7);
    }

    public void test_convertDayOfWeekToValue_SundayBased() {
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeekToValue(MONDAY), 2);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeekToValue(TUESDAY), 3);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeekToValue(WEDNESDAY), 4);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeekToValue(THURSDAY), 5);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeekToValue(FRIDAY), 6);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeekToValue(SATURDAY), 7);
        assertEquals(WeekRules.of(SUNDAY, 4).convertDayOfWeekToValue(SUNDAY), 1);
    }

    public void test_convertDayOfWeekToValue_FridayBased() {
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeekToValue(MONDAY), 4);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeekToValue(TUESDAY), 5);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeekToValue(WEDNESDAY), 6);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeekToValue(THURSDAY), 7);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeekToValue(FRIDAY), 1);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeekToValue(SATURDAY), 2);
        assertEquals(WeekRules.of(FRIDAY, 4).convertDayOfWeekToValue(SUNDAY), 3);
    }

    //-----------------------------------------------------------------------
    // convertValueToDayOfWeek()
    //-----------------------------------------------------------------------
    public void test_convertValueToDayOfWeek_MondayBased() {
        assertEquals(WeekRules.of(MONDAY, 4).convertValueToDayOfWeek(1), MONDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertValueToDayOfWeek(2), TUESDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertValueToDayOfWeek(3), WEDNESDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertValueToDayOfWeek(4), THURSDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertValueToDayOfWeek(5), FRIDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertValueToDayOfWeek(6), SATURDAY);
        assertEquals(WeekRules.of(MONDAY, 4).convertValueToDayOfWeek(7), SUNDAY);
    }

    public void test_convertValueToDayOfWeek_SundayBased() {
        assertEquals(WeekRules.of(SUNDAY, 4).convertValueToDayOfWeek(2), MONDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertValueToDayOfWeek(3), TUESDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertValueToDayOfWeek(4), WEDNESDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertValueToDayOfWeek(5), THURSDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertValueToDayOfWeek(6), FRIDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertValueToDayOfWeek(7), SATURDAY);
        assertEquals(WeekRules.of(SUNDAY, 4).convertValueToDayOfWeek(1), SUNDAY);
    }

    public void test_convertValueToDayOfWeek_FridayBased() {
        assertEquals(WeekRules.of(FRIDAY, 4).convertValueToDayOfWeek(4), MONDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertValueToDayOfWeek(5), TUESDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertValueToDayOfWeek(6), WEDNESDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertValueToDayOfWeek(7), THURSDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertValueToDayOfWeek(1), FRIDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertValueToDayOfWeek(2), SATURDAY);
        assertEquals(WeekRules.of(FRIDAY, 4).convertValueToDayOfWeek(3), SUNDAY);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @DataProvider(name="compare")
    public Object[][] data_compare() {
        return new Object[][] {
            {WeekRules.of(MONDAY, 1), WeekRules.of(MONDAY, 2)},
            {WeekRules.of(MONDAY, 1), WeekRules.of(MONDAY, 3)},
            {WeekRules.of(MONDAY, 1), WeekRules.of(MONDAY, 7)},
            {WeekRules.of(MONDAY, 1), WeekRules.of(TUESDAY, 1)},
            {WeekRules.of(MONDAY, 7), WeekRules.of(TUESDAY, 1)},
            {WeekRules.of(TUESDAY, 6), WeekRules.of(TUESDAY, 7)},
            {WeekRules.of(MONDAY, 1), WeekRules.of(SUNDAY, 7)},
        };
    }

    @Test(dataProvider="compare")
    public void test_compareTo(WeekRules a, WeekRules b) {
        assertEquals(a.compareTo(a), 0);
        assertEquals(a.compareTo(b) < 0, true);
        assertEquals(b.compareTo(a) > 0, true);
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void test_compareTo_null() {
        WeekRules.ISO8601.compareTo(null);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="compare")
    public void test_equals(WeekRules a, WeekRules b) {
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    public void test_equals_null() {
        assertEquals(WeekRules.ISO8601.equals(null), false);
    }

    public void test_equals_otherClass() {
        assertEquals(WeekRules.ISO8601.equals(""), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="compare")
    public void test_hashCode(WeekRules a, WeekRules b) {
        assertEquals(a.hashCode() == a.hashCode(), true);
        assertEquals(b.hashCode() == b.hashCode(), true);
        assertEquals(a.hashCode() == b.hashCode(), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        assertEquals(WeekRules.of(MONDAY, 4).toString(), "WeekRules[MONDAY,4]");
        assertEquals(WeekRules.of(SUNDAY, 1).toString(), "WeekRules[SUNDAY,1]");
    }

}
