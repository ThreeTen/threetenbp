/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

/**
 * Test ZoneOffset.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestZoneOffset {

    //-----------------------------------------------------------------------
    // Basics
    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(ZoneOffset.UTC instanceof Serializable);
        assertTrue(ZoneOffset.UTC instanceof Comparable);
    }

    public void test_immutable() {
        Class<ZoneOffset> cls = ZoneOffset.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
                assertTrue(Modifier.isPrivate(field.getModifiers()));
                assertTrue(Modifier.isFinal(field.getModifiers()));
            }
        }
    }

    public void test_serialization() throws Exception {
        ZoneOffset test = ZoneOffset.zoneOffset("+01:30");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(test);
        baos.close();
        byte[] bytes = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bais);
        ZoneOffset result = (ZoneOffset) in.readObject();
        
        doTestOffset(result, 1, 30, 0);
    }

    //-----------------------------------------------------------------------
    // Creation
    //-----------------------------------------------------------------------
    public void test_constant_UTC() {
        ZoneOffset test = ZoneOffset.UTC;
        doTestOffset(test, 0, 0, 0);
    }

    //-----------------------------------------------------------------------
    public void test_factory_string_UTC() {
        String[] values = new String[] {
            "Z",
            "+00","+0000","+00:00","+000000","+00:00:00",
            "-00","-0000","-00:00","-000000","-00:00:00",
        };
        for (int i = 0; i < values.length; i++) {
            ZoneOffset test = ZoneOffset.zoneOffset(values[i]);
            assertSame(test, ZoneOffset.UTC);
        }
    }

    public void test_factory_string_invalid() {
        String[] values = new String[] {
            "","A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","ZZ",
            "+0","+0:00","+00:0","+0:0",
            "+000","+00000",
            "+0:00:00","+00:0:00","+00:00:0","+0:0:0","+0:0:00","+00:0:0","+0:00:0",
            "+01_00","+01;00","+01@00","+01:AA",
            "+19","+19:00","+18:01","+18:00:01","+1801","+180001",
            "-0","-0:00","-00:0","-0:0",
            "-000","-00000",
            "-0:00:00","-00:0:00","-00:00:0","-0:0:0","-0:0:00","-00:0:0","-0:00:0",
            "-19","-19:00","-18:01","-18:00:01","-1801","-180001",
            "-01_00","-01;00","-01@00","-01:AA",
        };
        for (int i = 0; i < values.length; i++) {
            try {
                ZoneOffset.zoneOffset(values[i]);
                fail("Should have failed:" + values[i]);
            } catch (IllegalArgumentException ex) {
                // expected
            }
        }
    }

    //-----------------------------------------------------------------------
    public void test_factory_string_hours() {
        for (int i = -18; i <= 18; i++) {
            String str = (i < 0 ? "-" : "+") + Integer.toString(Math.abs(i) + 100).substring(1);
            ZoneOffset test = ZoneOffset.zoneOffset(str);
            doTestOffset(test, i, 0, 0);
        }
    }

    public void test_factory_string_hours_minutes_noColon() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                if ((i < 0 && j <= 0) || (i > 0 && j >= 0) || i == 0) {
                    String str = (i < 0 || j < 0 ? "-" : "+") +
                        Integer.toString(Math.abs(i) + 100).substring(1) +
                        Integer.toString(Math.abs(j) + 100).substring(1);
                    ZoneOffset test = ZoneOffset.zoneOffset(str);
                    doTestOffset(test, i, j, 0);
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.zoneOffset("-1800");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.zoneOffset("+1800");
        doTestOffset(test2, 18, 0, 0);
    }

    public void test_factory_string_hours_minutes_colon() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                if ((i < 0 && j <= 0) || (i > 0 && j >= 0) || i == 0) {
                    String str = (i < 0 || j < 0 ? "-" : "+") +
                        Integer.toString(Math.abs(i) + 100).substring(1) + ":" +
                        Integer.toString(Math.abs(j) + 100).substring(1);
                    ZoneOffset test = ZoneOffset.zoneOffset(str);
                    doTestOffset(test, i, j, 0);
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.zoneOffset("-18:00");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.zoneOffset("+18:00");
        doTestOffset(test2, 18, 0, 0);
    }

    public void test_factory_string_hours_minutes_seconds_noColon() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                for (int k = -59; k <= 59; k++) {
                    if ((i < 0 && j <= 0 && k <= 0) || (i > 0 && j >= 0 && k >= 0) ||
                            (i == 0 && ((j < 0 && k <= 0) || (j > 0 && k >= 0) || j == 0))) {
                        String str = (i < 0 || j < 0 || k < 0 ? "-" : "+") +
                            Integer.toString(Math.abs(i) + 100).substring(1) +
                            Integer.toString(Math.abs(j) + 100).substring(1) +
                            Integer.toString(Math.abs(k) + 100).substring(1);
                        ZoneOffset test = ZoneOffset.zoneOffset(str);
                        doTestOffset(test, i, j, k);
                    }
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.zoneOffset("-180000");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.zoneOffset("+180000");
        doTestOffset(test2, 18, 0, 0);
    }

    public void test_factory_string_hours_minutes_seconds_colon() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                for (int k = -59; k <= 59; k++) {
                    if ((i < 0 && j <= 0 && k <= 0) || (i > 0 && j >= 0 && k >= 0) ||
                            (i == 0 && ((j < 0 && k <= 0) || (j > 0 && k >= 0) || j == 0))) {
                        String str = (i < 0 || j < 0 || k < 0 ? "-" : "+") +
                            Integer.toString(Math.abs(i) + 100).substring(1) + ":" +
                            Integer.toString(Math.abs(j) + 100).substring(1) + ":" +
                            Integer.toString(Math.abs(k) + 100).substring(1);
                        ZoneOffset test = ZoneOffset.zoneOffset(str);
                        doTestOffset(test, i, j, k);
                    }
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.zoneOffset("-18:00:00");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.zoneOffset("+18:00:00");
        doTestOffset(test2, 18, 0, 0);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_hours() {
        for (int i = -18; i <= 18; i++) {
            ZoneOffset test = ZoneOffset.zoneOffset(i);
            doTestOffset(test, i, 0, 0);
        }
    }

    public void test_factory_int_hours_minutes() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                if ((i < 0 && j <= 0) || (i > 0 && j >= 0) || i == 0) {
                    ZoneOffset test = ZoneOffset.zoneOffset(i, j);
                    doTestOffset(test, i, j, 0);
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.zoneOffset(-18, 0);
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.zoneOffset(18, 0);
        doTestOffset(test2, 18, 0, 0);
    }

    public void test_factory_int_hours_minutes_seconds() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                for (int k = -59; k <= 59; k++) {
                    if ((i < 0 && j <= 0 && k <= 0) || (i > 0 && j >= 0 && k >= 0) ||
                            (i == 0 && ((j < 0 && k <= 0) || (j > 0 && k >= 0) || j == 0))) {
                        ZoneOffset test = ZoneOffset.zoneOffset(i, j, k);
                        doTestOffset(test, i, j, k);
                    }
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.zoneOffset(-18, 0, 0);
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.zoneOffset(18, 0, 0);
        doTestOffset(test2, 18, 0, 0);
    }

    //-----------------------------------------------------------------------
    private void doTestOffset(ZoneOffset offset, int hours, int minutes, int seconds) {
        assertEquals(offset.getAmountSeconds(), hours * 60 * 60 + minutes * 60 + seconds);
        final String id;
        if (hours == 0 && minutes == 0 && seconds == 0) {
            id = "Z";
        } else {
            String str = (hours < 0 || minutes < 0 || seconds < 0) ? "-" : "+";
            str += Integer.toString(Math.abs(hours) + 100).substring(1);
            str += ":";
            str += Integer.toString(Math.abs(minutes) + 100).substring(1);
            if (seconds != 0) {
                str += ":";
                str += Integer.toString(Math.abs(seconds) + 100).substring(1);
            }
            id = str;
        }
        assertEquals(offset.getID(), id);
        assertEquals(offset.getHoursField(), hours);
        assertEquals(offset.getMinutesField(), minutes);
        assertEquals(offset.getSecondsField(), seconds);
//        assertEquals(offset.toPeriod().getHours(), hours);
//        assertEquals(offset.toPeriod().getMinutes(), minutes);
//        assertEquals(offset.toPeriod().getSeconds(), seconds);
        assertEquals(offset, ZoneOffset.zoneOffset(hours, minutes, seconds));
        if (seconds == 0) {
            assertEquals(offset, ZoneOffset.zoneOffset(hours, minutes));
            if (minutes == 0) {
                assertEquals(offset, ZoneOffset.zoneOffset(hours));
            }
        }
        assertEquals(offset, ZoneOffset.zoneOffset(id));
    }

//    //-----------------------------------------------------------------------
//    // equals()
//    //-----------------------------------------------------------------------
//    @Test(dataProvider="sampleDates")
//    public void test_equals_true(int y, int m, int d) {
//        ZoneOffset a = ZoneOffset.date(y, m, d, OFFSET_PONE);
//        ZoneOffset b = ZoneOffset.date(y, m, d, OFFSET_PONE);
//        assertEquals(a.equals(b), true);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_equals_false_year_differs(int y, int m, int d) {
//        ZoneOffset a = ZoneOffset.date(y, m, d, OFFSET_PONE);
//        ZoneOffset b = ZoneOffset.date(y + 1, m, d, OFFSET_PONE);
//        assertEquals(a.equals(b), false);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_equals_false_month_differs(int y, int m, int d) {
//        ZoneOffset a = ZoneOffset.date(y, m, d, OFFSET_PONE);
//        ZoneOffset b = ZoneOffset.date(y, m + 1, d, OFFSET_PONE);
//        assertEquals(a.equals(b), false);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_equals_false_day_differs(int y, int m, int d) {
//        ZoneOffset a = ZoneOffset.date(y, m, d, OFFSET_PONE);
//        ZoneOffset b = ZoneOffset.date(y, m, d + 1, OFFSET_PONE);
//        assertEquals(a.equals(b), false);
//    }
//    @Test(dataProvider="sampleDates")
//    public void test_equals_false_offset_differs(int y, int m, int d) {
//        ZoneOffset a = ZoneOffset.date(y, m, d, OFFSET_PONE);
//        ZoneOffset b = ZoneOffset.date(y, m, d, OFFSET_PTWO);
//        assertEquals(a.equals(b), false);
//    }
//
//    public void test_equals_itself_true() {
//        assertEquals(TEST_2007_07_15.equals(TEST_2007_07_15), true);
//    }
//
//    public void test_equals_string_false() {
//        assertEquals(TEST_2007_07_15.equals("2007-07-15"), false);
//    }
//
//    //-----------------------------------------------------------------------
//    // toString()
//    //-----------------------------------------------------------------------
//    @DataProvider(name="sampleToString")
//    Object[][] provider_sampleToString() {
//        return new Object[][] {
//            {2008, 7, 5, "Z", "2008-07-05Z"},
//            {2008, 7, 5, "+00", "2008-07-05Z"},
//            {2008, 7, 5, "+0000", "2008-07-05Z"},
//            {2008, 7, 5, "+00:00", "2008-07-05Z"},
//            {2008, 7, 5, "+000000", "2008-07-05Z"},
//            {2008, 7, 5, "+00:00:00", "2008-07-05Z"},
//            {2008, 7, 5, "-00", "2008-07-05Z"},
//            {2008, 7, 5, "-0000", "2008-07-05Z"},
//            {2008, 7, 5, "-00:00", "2008-07-05Z"},
//            {2008, 7, 5, "-000000", "2008-07-05Z"},
//            {2008, 7, 5, "-00:00:00", "2008-07-05Z"},
//            {2008, 7, 5, "+01", "2008-07-05+01:00"},
//            {2008, 7, 5, "+0100", "2008-07-05+01:00"},
//            {2008, 7, 5, "+01:00", "2008-07-05+01:00"},
//            {2008, 7, 5, "+010000", "2008-07-05+01:00"},
//            {2008, 7, 5, "+01:00:00", "2008-07-05+01:00"},
//            {2008, 7, 5, "+0130", "2008-07-05+01:30"},
//            {2008, 7, 5, "+01:30", "2008-07-05+01:30"},
//            {2008, 7, 5, "+013000", "2008-07-05+01:30"},
//            {2008, 7, 5, "+01:30:00", "2008-07-05+01:30"},
//            {2008, 7, 5, "+013040", "2008-07-05+01:30:40"},
//            {2008, 7, 5, "+01:30:40", "2008-07-05+01:30:40"},
//        };
//    }
//
//    @Test(dataProvider="sampleToString")
//    public void test_toString(int y, int m, int d, String offsetId, String expected) {
//        ZoneOffset t = ZoneOffset.date(y, m, d, ZoneOffset.zoneOffset(offsetId));
//        String str = t.toString();
//        assertEquals(str, expected);
//    }

}
