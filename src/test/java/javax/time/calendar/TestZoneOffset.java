/*
 * Copyright (c) 2008-2009, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.period.Period;

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
        Object obj = ZoneOffset.UTC;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
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
            "@01:00",
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

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_string_null() {
        ZoneOffset.zoneOffset((String) null);
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

    //-----------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
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

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_plusHoursMinusMinutes() {
        ZoneOffset.zoneOffset(1, -1, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_plusHoursMinusSeconds() {
        ZoneOffset.zoneOffset(1, 0, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_minusHoursPlusMinutes() {
        ZoneOffset.zoneOffset(-1, 1, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_minusHoursPlusSeconds() {
        ZoneOffset.zoneOffset(-1, 0, 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_zeroHoursMinusMinutesPlusSeconds() {
        ZoneOffset.zoneOffset(0, -1, 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_zeroHoursPlusMinutesMinusSeconds() {
        ZoneOffset.zoneOffset(0, 1, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_minutesTooLarge() {
        ZoneOffset.zoneOffset(0, 60, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_minutesTooSmall() {
        ZoneOffset.zoneOffset(0, -60, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_secondsTooLarge() {
        ZoneOffset.zoneOffset(0, 0, 60);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_secondsTooSmall() {
        ZoneOffset.zoneOffset(0, 0, 60);
    }

    //-----------------------------------------------------------------------
    public void test_forTotalSeconds() {
        assertSame(ZoneOffset.forTotalSeconds(0), ZoneOffset.UTC);
        assertEquals(ZoneOffset.forTotalSeconds(60 * 60 + 1), ZoneOffset.zoneOffset(1, 0, 1));
        assertEquals(ZoneOffset.forTotalSeconds(18 * 60 * 60), ZoneOffset.zoneOffset(18));
        assertEquals(ZoneOffset.forTotalSeconds(-18 * 60 * 60), ZoneOffset.zoneOffset(-18));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_forTotalSeconds_tooLarge() {
        ZoneOffset.forTotalSeconds(18 * 60 * 60 + 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_forTotalSeconds_tooSmall() {
        ZoneOffset.forTotalSeconds(-18 * 60 * 60 - 1);
    }

    //-----------------------------------------------------------------------
    // getAmountSeconds()
    //-----------------------------------------------------------------------
    public void test_getAmountSeconds() {
        ZoneOffset offset = ZoneOffset.forTotalSeconds(60 * 60 + 1);
        assertEquals(offset.getAmountSeconds(), 60 * 60 + 1);
    }

    //-----------------------------------------------------------------------
    // getID()
    //-----------------------------------------------------------------------
    public void test_getID() {
        ZoneOffset offset = ZoneOffset.zoneOffset(1, 0, 0);
        assertEquals(offset.getID(), "+01:00");
        offset = ZoneOffset.zoneOffset(1, 2, 3);
        assertEquals(offset.getID(), "+01:02:03");
        offset = ZoneOffset.UTC;
        assertEquals(offset.getID(), "Z");
    }

    //-----------------------------------------------------------------------
    // getHoursField()
    //-----------------------------------------------------------------------
    public void test_getHoursField() {
        ZoneOffset offset = ZoneOffset.zoneOffset(1, 2, 3);
        assertEquals(offset.getHoursField(), 1);
    }

    public void test_getHoursField_negative() {
        ZoneOffset offset = ZoneOffset.zoneOffset(-1, -2, -3);
        assertEquals(offset.getHoursField(), -1);
    }

    //-----------------------------------------------------------------------
    // getMinutesField()
    //-----------------------------------------------------------------------
    public void test_getMinutesField() {
        ZoneOffset offset = ZoneOffset.zoneOffset(1, 2, 3);
        assertEquals(offset.getMinutesField(), 2);
    }

    public void test_getMinutesField_negative() {
        ZoneOffset offset = ZoneOffset.zoneOffset(-1, -2, -3);
        assertEquals(offset.getMinutesField(), -2);
    }

    //-----------------------------------------------------------------------
    // getSecondsField()
    //-----------------------------------------------------------------------
    public void test_getSecondsField() {
        ZoneOffset offset = ZoneOffset.zoneOffset(1, 2, 3);
        assertEquals(offset.getSecondsField(), 3);
    }

    public void test_getSecondsField_negative() {
        ZoneOffset offset = ZoneOffset.zoneOffset(-1, -2, -3);
        assertEquals(offset.getSecondsField(), -3);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        ZoneOffset offset = ZoneOffset.zoneOffset("+01:02:03");
        Period p = Period.hoursMinutesSeconds(4, 6, 8);
        assertEquals(offset.plus(p), ZoneOffset.zoneOffset("+05:08:11"));
    }

    public void test_plus_PeriodProvider_overflowSecs() {
        ZoneOffset offset = ZoneOffset.zoneOffset("+01:02:03");
        Period p = Period.hoursMinutesSeconds(4, 6, 68);
        assertEquals(offset.plus(p), ZoneOffset.zoneOffset("+05:09:11"));
    }

    public void test_plus_PeriodProvider_overflowMins() {
        ZoneOffset offset = ZoneOffset.zoneOffset("+01:02:03");
        Period p = Period.hoursMinutesSeconds(4, 66, 8);
        assertEquals(offset.plus(p), ZoneOffset.zoneOffset("+06:08:11"));
    }

    public void test_plus_PeriodProvider_negative() {
        ZoneOffset offset = ZoneOffset.zoneOffset("-02:04:06");
        Period p = Period.hoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.plus(p), ZoneOffset.zoneOffset("-01:02:03"));
    }

    public void test_plus_PeriodProvider_negativeToPositive() {
        ZoneOffset offset = ZoneOffset.zoneOffset("-01:02:03");
        Period p = Period.hoursMinutesSeconds(4, 6, 8);
        assertEquals(offset.plus(p), ZoneOffset.zoneOffset("+03:04:05"));
    }

    public void test_plus_PeriodProvider_zero() {
        ZoneOffset offset = ZoneOffset.UTC;
        assertEquals(offset.plus(Period.ZERO), ZoneOffset.UTC);
    }

    //-----------------------------------------------------------------------
    // toPeriod()
    //-----------------------------------------------------------------------
    public void test_toPeriod() {
        ZoneOffset offset = ZoneOffset.zoneOffset("+01:02:03");
        assertEquals(offset.toPeriod(), Period.hoursMinutesSeconds(1, 2, 3));
    }

    public void test_toPeriod_negative() {
        ZoneOffset offset = ZoneOffset.zoneOffset("-01:02:03");
        assertEquals(offset.toPeriod(), Period.hoursMinutesSeconds(-1, -2, -3));
    }

    public void test_toPeriod_zero() {
        ZoneOffset offset = ZoneOffset.UTC;
        assertEquals(offset.toPeriod(), Period.ZERO);
    }

    //-----------------------------------------------------------------------
    // toTimeZone()
    //-----------------------------------------------------------------------
    public void test_toTimeZone() {
        ZoneOffset offset = ZoneOffset.zoneOffset(1, 2, 3);
        assertEquals(offset.toTimeZone(), TimeZone.timeZone(offset));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        ZoneOffset offset1 = ZoneOffset.zoneOffset(1, 2, 3);
        ZoneOffset offset2 = ZoneOffset.zoneOffset(2, 3, 4);
        assertTrue(offset1.compareTo(offset2) > 0);
        assertTrue(offset2.compareTo(offset1) < 0);
        assertTrue(offset1.compareTo(offset1) == 0);
        assertTrue(offset2.compareTo(offset2) == 0);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        ZoneOffset offset1 = ZoneOffset.zoneOffset(1, 2, 3);
        ZoneOffset offset2 = ZoneOffset.zoneOffset(2, 3, 4);
        ZoneOffset offset2b = ZoneOffset.zoneOffset(2, 3, 4);
        assertEquals(offset1.equals(offset2), false);
        assertEquals(offset2.equals(offset1), false);
        
        assertEquals(offset1.equals(offset1), true);
        assertEquals(offset2.equals(offset2), true);
        assertEquals(offset2.equals(offset2b), true);
        
        assertEquals(offset1.hashCode() == offset1.hashCode(), true);
        assertEquals(offset2.hashCode() == offset2.hashCode(), true);
        assertEquals(offset2.hashCode() == offset2b.hashCode(), true);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    public void test_toString() {
        ZoneOffset offset = ZoneOffset.zoneOffset(1, 0, 0);
        assertEquals(offset.toString(), "+01:00");
        offset = ZoneOffset.zoneOffset(1, 2, 3);
        assertEquals(offset.toString(), "+01:02:03");
        offset = ZoneOffset.UTC;
        assertEquals(offset.toString(), "Z");
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
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
        assertEquals(ZoneOffset.zoneOffset(id), offset);
        assertEquals(offset.toTimeZone(), TimeZone.timeZone(offset));
        assertEquals(offset.toString(), id);
    }

}
