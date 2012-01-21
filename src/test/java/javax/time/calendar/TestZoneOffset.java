/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.calendrical.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendrical.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.YEAR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.CalendricalException;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.MockOtherChronology;
import javax.time.calendrical.MockRuleNoValue;
import javax.time.calendrical.PeriodField;
import javax.time.calendrical.PeriodFields;
import javax.time.calendrical.PeriodProvider;

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
        ZoneOffset test = ZoneOffset.of("+01:30");
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
            ZoneOffset test = ZoneOffset.of(values[i]);
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
                ZoneOffset.of(values[i]);
                fail("Should have failed:" + values[i]);
            } catch (IllegalArgumentException ex) {
                // expected
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_string_null() {
        ZoneOffset.of((String) null);
    }

    //-----------------------------------------------------------------------
    public void test_factory_string_hours() {
        for (int i = -18; i <= 18; i++) {
            String str = (i < 0 ? "-" : "+") + Integer.toString(Math.abs(i) + 100).substring(1);
            ZoneOffset test = ZoneOffset.of(str);
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
                    ZoneOffset test = ZoneOffset.of(str);
                    doTestOffset(test, i, j, 0);
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.of("-1800");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.of("+1800");
        doTestOffset(test2, 18, 0, 0);
    }

    public void test_factory_string_hours_minutes_colon() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                if ((i < 0 && j <= 0) || (i > 0 && j >= 0) || i == 0) {
                    String str = (i < 0 || j < 0 ? "-" : "+") +
                        Integer.toString(Math.abs(i) + 100).substring(1) + ":" +
                        Integer.toString(Math.abs(j) + 100).substring(1);
                    ZoneOffset test = ZoneOffset.of(str);
                    doTestOffset(test, i, j, 0);
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.of("-18:00");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.of("+18:00");
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
                        ZoneOffset test = ZoneOffset.of(str);
                        doTestOffset(test, i, j, k);
                    }
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.of("-180000");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.of("+180000");
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
                        ZoneOffset test = ZoneOffset.of(str);
                        doTestOffset(test, i, j, k);
                    }
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.of("-18:00:00");
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.of("+18:00:00");
        doTestOffset(test2, 18, 0, 0);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_hours() {
        for (int i = -18; i <= 18; i++) {
            ZoneOffset test = ZoneOffset.ofHours(i);
            doTestOffset(test, i, 0, 0);
        }
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_hours_minutes() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                if ((i < 0 && j <= 0) || (i > 0 && j >= 0) || i == 0) {
                    ZoneOffset test = ZoneOffset.ofHoursMinutes(i, j);
                    doTestOffset(test, i, j, 0);
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.ofHoursMinutes(-18, 0);
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.ofHoursMinutes(18, 0);
        doTestOffset(test2, 18, 0, 0);
    }

    //-----------------------------------------------------------------------
    public void test_factory_int_hours_minutes_seconds() {
        for (int i = -17; i <= 17; i++) {
            for (int j = -59; j <= 59; j++) {
                for (int k = -59; k <= 59; k++) {
                    if ((i < 0 && j <= 0 && k <= 0) || (i > 0 && j >= 0 && k >= 0) ||
                            (i == 0 && ((j < 0 && k <= 0) || (j > 0 && k >= 0) || j == 0))) {
                        ZoneOffset test = ZoneOffset.ofHoursMinutesSeconds(i, j, k);
                        doTestOffset(test, i, j, k);
                    }
                }
            }
        }
        ZoneOffset test1 = ZoneOffset.ofHoursMinutesSeconds(-18, 0, 0);
        doTestOffset(test1, -18, 0, 0);
        ZoneOffset test2 = ZoneOffset.ofHoursMinutesSeconds(18, 0, 0);
        doTestOffset(test2, 18, 0, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_plusHoursMinusMinutes() {
        ZoneOffset.ofHoursMinutesSeconds(1, -1, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_plusHoursMinusSeconds() {
        ZoneOffset.ofHoursMinutesSeconds(1, 0, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_minusHoursPlusMinutes() {
        ZoneOffset.ofHoursMinutesSeconds(-1, 1, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_minusHoursPlusSeconds() {
        ZoneOffset.ofHoursMinutesSeconds(-1, 0, 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_zeroHoursMinusMinutesPlusSeconds() {
        ZoneOffset.ofHoursMinutesSeconds(0, -1, 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_zeroHoursPlusMinutesMinusSeconds() {
        ZoneOffset.ofHoursMinutesSeconds(0, 1, -1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_minutesTooLarge() {
        ZoneOffset.ofHoursMinutesSeconds(0, 60, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_minutesTooSmall() {
        ZoneOffset.ofHoursMinutesSeconds(0, -60, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_secondsTooLarge() {
        ZoneOffset.ofHoursMinutesSeconds(0, 0, 60);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_int_hours_minutes_seconds_secondsTooSmall() {
        ZoneOffset.ofHoursMinutesSeconds(0, 0, 60);
    }

    //-----------------------------------------------------------------------
    public void test_factory_of_PeriodProvider() {
        assertSame(ZoneOffset.of(PeriodFields.ZERO), ZoneOffset.UTC);
        assertEquals(ZoneOffset.of(Period.ofTimeFields(2, 30, 45)), ZoneOffset.ofHoursMinutesSeconds(2, 30, 45));
        
        assertEquals(ZoneOffset.of(Period.of(1, 2, 3, 2, 30, 45, 99)), ZoneOffset.ofHoursMinutesSeconds(2, 30, 45));
        assertEquals(ZoneOffset.of(Period.of(1, 2, 3, 2, -30, 45, 99)), ZoneOffset.ofHoursMinutesSeconds(1, 30, 45));
        assertEquals(ZoneOffset.of(Period.of(1, 2, 3, -2, -30, 45, 99)), ZoneOffset.ofHoursMinutesSeconds(-2, -29, -15));
        assertEquals(ZoneOffset.of(Period.of(1, 2, 3, -2, 30, 45, 99)), ZoneOffset.ofHoursMinutesSeconds(-1, -29, -15));
        
        assertEquals(ZoneOffset.of(Period.of(1, 2, 3, 2, 64, 75, 1000000004)), ZoneOffset.ofHoursMinutesSeconds(3, 5, 16));
        assertEquals(ZoneOffset.of(Period.of(1, 2, 3, 2, -64, 75, 1000000004)), ZoneOffset.ofHoursMinutesSeconds(0, 57, 16));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_of_PeriodProvider_overflow() {
        ZoneOffset.of(Period.ofHours(23));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_of_PeriodProvider_invalidPeriod() {
        ZoneOffset.of(PeriodField.of(2, MockOtherChronology.OTHER_MONTHS));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_of_PeriodProvider_null() {
        ZoneOffset.of((PeriodProvider) null);
    }

    //-----------------------------------------------------------------------
    public void test_factory_ofTotalSeconds() {
        assertSame(ZoneOffset.ofTotalSeconds(0), ZoneOffset.UTC);
        assertEquals(ZoneOffset.ofTotalSeconds(60 * 60 + 1), ZoneOffset.ofHoursMinutesSeconds(1, 0, 1));
        assertEquals(ZoneOffset.ofTotalSeconds(18 * 60 * 60), ZoneOffset.ofHours(18));
        assertEquals(ZoneOffset.ofTotalSeconds(-18 * 60 * 60), ZoneOffset.ofHours(-18));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_ofTotalSeconds_tooLarge() {
        ZoneOffset.ofTotalSeconds(18 * 60 * 60 + 1);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_factory_ofTotalSeconds_tooSmall() {
        ZoneOffset.ofTotalSeconds(-18 * 60 * 60 - 1);
    }

    //-----------------------------------------------------------------------
    // from()
    //-----------------------------------------------------------------------
    public void test_factory_Calendricals() {
        assertEquals(ZoneOffset.from(ZoneOffset.ofHours(6), YearMonth.of(2007, 7), DAY_OF_MONTH.field(15)), ZoneOffset.ofHours(6));
        assertEquals(ZoneOffset.from(ZonedDateTime.of(2007, 7, 15, 17, 30, 0, 0, ZoneId.of("Europe/Paris"))), ZoneOffset.ofHours(2));
        assertEquals(ZoneOffset.from(OffsetDateTime.of(2007, 7, 15, 17, 30, 0, 0, ZoneOffset.ofHours(2))), ZoneOffset.ofHours(2));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_clash() {
        ZoneOffset.from(YearMonth.of(2007, 7), MonthDay.of(9, 15));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_noDerive() {
        ZoneOffset.from(LocalTime.of(12, 30));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_factory_Calendricals_invalid_empty() {
        ZoneOffset.from();
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_nullArray() {
        ZoneOffset.from((Calendrical[]) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_Calendricals_null() {
        ZoneOffset.from((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // getAmountSeconds()
    //-----------------------------------------------------------------------
    public void test_getAmountSeconds() {
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(60 * 60 + 1);
        assertEquals(offset.getAmountSeconds(), 60 * 60 + 1);
    }

    //-----------------------------------------------------------------------
    // getID()
    //-----------------------------------------------------------------------
    public void test_getID() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(1, 0, 0);
        assertEquals(offset.getID(), "+01:00");
        offset = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.getID(), "+01:02:03");
        offset = ZoneOffset.UTC;
        assertEquals(offset.getID(), "Z");
    }

    //-----------------------------------------------------------------------
    // getHoursField()
    //-----------------------------------------------------------------------
    public void test_getHoursField() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.getHoursField(), 1);
    }

    public void test_getHoursField_negative() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3);
        assertEquals(offset.getHoursField(), -1);
    }

    //-----------------------------------------------------------------------
    // getMinutesField()
    //-----------------------------------------------------------------------
    public void test_getMinutesField() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.getMinutesField(), 2);
    }

    public void test_getMinutesField_negative() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3);
        assertEquals(offset.getMinutesField(), -2);
    }

    //-----------------------------------------------------------------------
    // getSecondsField()
    //-----------------------------------------------------------------------
    public void test_getSecondsField() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.getSecondsField(), 3);
    }

    public void test_getSecondsField_negative() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(-1, -2, -3);
        assertEquals(offset.getSecondsField(), -3);
    }

    //-----------------------------------------------------------------------
    // plus(PeriodProvider)
    //-----------------------------------------------------------------------
    public void test_plus_PeriodProvider() {
        ZoneOffset offset = ZoneOffset.of("+01:02:03");
        Period p = Period.ofTimeFields(4, 6, 8);
        assertEquals(offset.plus(p), ZoneOffset.of("+05:08:11"));
    }

    public void test_plus_PeriodProvider_overflowSecs() {
        ZoneOffset offset = ZoneOffset.of("+01:02:03");
        Period p = Period.ofTimeFields(4, 6, 68);
        assertEquals(offset.plus(p), ZoneOffset.of("+05:09:11"));
    }

    public void test_plus_PeriodProvider_overflowMins() {
        ZoneOffset offset = ZoneOffset.of("+01:02:03");
        Period p = Period.ofTimeFields(4, 66, 8);
        assertEquals(offset.plus(p), ZoneOffset.of("+06:08:11"));
    }

    public void test_plus_PeriodProvider_negative() {
        ZoneOffset offset = ZoneOffset.of("-02:04:06");
        Period p = Period.ofTimeFields(1, 2, 3);
        assertEquals(offset.plus(p), ZoneOffset.of("-01:02:03"));
    }

    public void test_plus_PeriodProvider_negativeToPositive() {
        ZoneOffset offset = ZoneOffset.of("-01:02:03");
        Period p = Period.ofTimeFields(4, 6, 8);
        assertEquals(offset.plus(p), ZoneOffset.of("+03:04:05"));
    }

    public void test_plus_PeriodProvider_zero() {
        ZoneOffset offset = ZoneOffset.UTC;
        assertEquals(offset.plus(Period.ZERO), ZoneOffset.UTC);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_plus_PeriodProvider_invalidPeriod() {
        ZoneOffset offset = ZoneOffset.UTC;
        offset.plus(PeriodField.of(2, MockOtherChronology.OTHER_MONTHS));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_plus_PeriodProvider_null() {
        ZoneOffset offset = ZoneOffset.UTC;
        offset.plus((PeriodProvider) null);
    }

    //-----------------------------------------------------------------------
    // toPeriod()
    //-----------------------------------------------------------------------
    public void test_toPeriod() {
        ZoneOffset offset = ZoneOffset.of("+01:02:03");
        assertEquals(offset.toPeriod(), Period.ofTimeFields(1, 2, 3));
    }

    public void test_toPeriod_negative() {
        ZoneOffset offset = ZoneOffset.of("-01:02:03");
        assertEquals(offset.toPeriod(), Period.ofTimeFields(-1, -2, -3));
    }

    public void test_toPeriod_zero() {
        ZoneOffset offset = ZoneOffset.UTC;
        assertEquals(offset.toPeriod(), Period.ZERO);
    }

    //-----------------------------------------------------------------------
    // toZoneId()
    //-----------------------------------------------------------------------
    public void test_toZoneId() {
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.toZoneId(), ZoneId.of(offset));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_compareTo() {
        ZoneOffset offset1 = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        ZoneOffset offset2 = ZoneOffset.ofHoursMinutesSeconds(2, 3, 4);
        assertTrue(offset1.compareTo(offset2) > 0);
        assertTrue(offset2.compareTo(offset1) < 0);
        assertTrue(offset1.compareTo(offset1) == 0);
        assertTrue(offset2.compareTo(offset2) == 0);
    }

    //-----------------------------------------------------------------------
    // equals() / hashCode()
    //-----------------------------------------------------------------------
    public void test_equals() {
        ZoneOffset offset1 = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        ZoneOffset offset2 = ZoneOffset.ofHoursMinutesSeconds(2, 3, 4);
        ZoneOffset offset2b = ZoneOffset.ofHoursMinutesSeconds(2, 3, 4);
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
        ZoneOffset offset = ZoneOffset.ofHoursMinutesSeconds(1, 0, 0);
        assertEquals(offset.toString(), "+01:00");
        offset = ZoneOffset.ofHoursMinutesSeconds(1, 2, 3);
        assertEquals(offset.toString(), "+01:02:03");
        offset = ZoneOffset.UTC;
        assertEquals(offset.toString(), "Z");
    }

    //-----------------------------------------------------------------------
    // get(CalendricalRule)
    //-----------------------------------------------------------------------
    public void test_get_CalendricalRule() {
        ZoneOffset test = ZoneOffset.ofHoursMinutesSeconds(1, 0, 0);
        assertEquals(test.get(Chronology.rule()), null);
        assertEquals(test.get(YEAR), null);
        assertEquals(test.get(HOUR_OF_DAY), null);
        assertEquals(test.get(LocalDate.rule()), null);
        assertEquals(test.get(ZoneOffset.rule()), test);
        assertEquals(test.get(ZoneId.rule()), null);
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_CalendricalRule_null() {
        ZoneOffset test = ZoneOffset.ofHoursMinutesSeconds(1, 0, 0);
        test.get((CalendricalRule<?>) null);
    }

    public void test_get_unsupported() {
        ZoneOffset test = ZoneOffset.ofHoursMinutesSeconds(1, 0, 0);
        assertEquals(test.get(MockRuleNoValue.INSTANCE), null);
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
        assertEquals(offset, ZoneOffset.ofHoursMinutesSeconds(hours, minutes, seconds));
        if (seconds == 0) {
            assertEquals(offset, ZoneOffset.ofHoursMinutes(hours, minutes));
            if (minutes == 0) {
                assertEquals(offset, ZoneOffset.ofHours(hours));
            }
        }
        assertEquals(ZoneOffset.of(id), offset);
        assertEquals(offset.toZoneId(), ZoneId.of(offset));
        assertEquals(offset.toString(), id);
    }

}
