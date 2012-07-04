/*
 * Copyright (c) 2008-2012 Stephen Colebourne & Michael Nascimento Santos
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
package java.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.time.CalendricalException;
import javax.time.Instant;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.OffsetDate;
import javax.time.OffsetDateTime;
import javax.time.OffsetTime;
import javax.time.ZonedDateTime;

/**
 * Test java.util.GregorianCalendar additional methods.
 * <p>
 * To run this, you need the built classes and TestNG in the bootstrap classpath.
 */
//@Test
public class TestGregorianCalendar {
    
    public static void main(String[] args) {
        int success = 0;
        int failure = 0;
        int error = 0;
        try {
            Method[] methods = TestGregorianCalendar.class.getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isStatic(method.getModifiers()) == false && method.getName().startsWith("test")) {
                    try {
                        try {
                            method.invoke(new TestGregorianCalendar());
                        } catch (InvocationTargetException itex) {
                            throw itex.getCause();
                        }
                        System.out.println(method.getName() + " SUCCEEDED");
                        success++;
                    } catch (AssertionError ex) {
                        System.out.println(method.getName() + " FAILED");
                        ex.printStackTrace(System.out);
                        failure++;
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        error++;
                    }
                }
            }
            
        } catch (Throwable th) {
            th.printStackTrace();
        }
        System.out.println("Success: " + success);
        System.out.println("Failure: " + failure);
        System.out.println("Error:   " + error);
    }

    //-----------------------------------------------------------------------
    public void test_setInstant() {
        for (int i = -1100; i < 1100; i++) {
            Instant instant = Instant.ofEpochMilli(i);
            GregorianCalendar test = new GregorianCalendar();
            test.setInstant(instant);
            assertEquals(test.getTimeInMillis(), i);
        }
    }

    public void test_setInstant_null() {
        GregorianCalendar test = new GregorianCalendar();
        try {
            test.setInstant((Instant) null);
            fail();
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void test_setInstant_tooBig() {
        GregorianCalendar test = new GregorianCalendar();
        try {
            test.setInstant(Instant.ofEpochSecond(Long.MAX_VALUE / 1000 + 1));
            fail();
        } catch (CalendricalException ex) {
            // expected
        }
    }

    public void test_setInstant_tooSmall() {
        GregorianCalendar test = new GregorianCalendar();
        try {
            test.setInstant(Instant.ofEpochSecond(Long.MIN_VALUE / 1000 - 1));
            fail();
        } catch (CalendricalException ex) {
            // expected
        }
    }

    //-----------------------------------------------------------------------
    public void test_toInstant() {
        for (int i = -1100; i < 1100; i++) {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(i);
            Instant test = gcal.toInstant();
            assertEquals(test, Instant.ofEpochMilli(i));
        }
    }

    //-----------------------------------------------------------------------
    public void test_toLocalDateTime() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalDateTime test = gcal.toLocalDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalDateTime_endOfDay() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 23, 59, 59);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalDateTime test = gcal.toLocalDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), (24L * 60L * 60L - 1L) * 1000000000L);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalDateTime_fixedOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT+12:00"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalDateTime test = gcal.toLocalDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalDateTime_variableOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalDateTime test = gcal.toLocalDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalDateTime_manualOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
        gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        for (int i = 0; i < 500; i++) {
            LocalDateTime test = gcal.toLocalDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
            gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
            gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        }
    }

    //-----------------------------------------------------------------------
    public void test_toLocalDate() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalDate test = gcal.toLocalDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalDate_endOfDay() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 23, 59, 59);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalDate test = gcal.toLocalDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalDate_fixedOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT+12:00"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalDate test = gcal.toLocalDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalDate_variableOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalDate test = gcal.toLocalDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalDate_manualOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
        gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        for (int i = 0; i < 500; i++) {
            LocalDate test = gcal.toLocalDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            gcal.add(Calendar.DATE, 1);
            gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
            gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        }
    }

    //-----------------------------------------------------------------------
    public void test_toLocalTime() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalTime test = gcal.toLocalTime();
            assertEquals(test.getHourOfDay(), gcal.get(Calendar.HOUR_OF_DAY));
            assertEquals(test.getMinuteOfHour(), gcal.get(Calendar.MINUTE));
            assertEquals(test.getSecondOfMinute(), gcal.get(Calendar.SECOND));
            assertEquals(test.getNanoOfSecond(), gcal.get(Calendar.MILLISECOND) * 1000000);
            assertEquals(test.toNanoOfDay(), 0);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalTime_endOfDay() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 23, 59, 59);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalTime test = gcal.toLocalTime();
            assertEquals(test.toNanoOfDay(), (24L * 60L * 60L - 1L) * 1000000000L);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalTime_fixedOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT+12:00"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalTime test = gcal.toLocalTime();
            assertEquals(test.toNanoOfDay(), 0);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalTime_variableOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            LocalTime test = gcal.toLocalTime();
            assertEquals(test.toNanoOfDay(), 0);
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toLocalTime_manualOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
        gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        for (int i = 0; i < 500; i++) {
            LocalTime test = gcal.toLocalTime();
            assertEquals(test.toNanoOfDay(), 0);
            gcal.add(Calendar.DATE, 1);
            gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
            gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        }
    }

    //-----------------------------------------------------------------------
    public void test_toOffsetDateTime() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetDateTime test = gcal.toOffsetDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toOffsetTime().toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "Z");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetDateTime_endOfDay() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 23, 59, 59);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetDateTime test = gcal.toOffsetDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toOffsetTime().toLocalTime().toNanoOfDay(), (24L * 60L * 60L - 1L) * 1000000000L);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "Z");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetDateTime_fixedOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT+12:00"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetDateTime test = gcal.toOffsetDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toOffsetTime().toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "+12:00");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetDateTime_variableOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetDateTime test = gcal.toOffsetDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toOffsetTime().toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            boolean isDST = TimeZone.getTimeZone("Europe/Paris").inDaylightTime(gcal.getTime());
            assertEquals(test.getOffset().getID(), isDST ? "+02:00" : "+01:00");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetDateTime_manualOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
        gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        for (int i = 0; i < 500; i++) {
            OffsetDateTime test = gcal.toOffsetDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toOffsetTime().toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "+00:45");
            gcal.add(Calendar.DATE, 1);
            gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
            gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        }
    }

    //-----------------------------------------------------------------------
    public void test_toOffsetDate() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetDate test = gcal.toOffsetDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "Z");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetDate_endOfDay() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 23, 59, 59);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetDate test = gcal.toOffsetDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "Z");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetDate_fixedOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT+12:00"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetDate test = gcal.toOffsetDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "+12:00");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetDate_variableOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetDate test = gcal.toOffsetDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            boolean isDST = TimeZone.getTimeZone("Europe/Paris").inDaylightTime(gcal.getTime());
            assertEquals(test.getOffset().getID(), isDST ? "+02:00" : "+01:00");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetDate_manualOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
        gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        for (int i = 0; i < 500; i++) {
            OffsetDate test = gcal.toOffsetDate();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "+00:45");
            gcal.add(Calendar.DATE, 1);
            gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
            gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        }
    }

    //-----------------------------------------------------------------------
    public void test_toOffsetTime() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetTime test = gcal.toOffsetTime();
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getOffset().getID(), "Z");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetTime_endOfDay() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 23, 59, 59);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetTime test = gcal.toOffsetTime();
            assertEquals(test.toLocalTime().toNanoOfDay(), (24L * 60L * 60L - 1L) * 1000000000L);
            assertEquals(test.getOffset().getID(), "Z");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetTime_fixedOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT+12:00"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetTime test = gcal.toOffsetTime();
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getOffset().getID(), "+12:00");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetTime_variableOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            OffsetTime test = gcal.toOffsetTime();
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            boolean isDST = TimeZone.getTimeZone("Europe/Paris").inDaylightTime(gcal.getTime());
            assertEquals(test.getOffset().getID(), isDST ? "+02:00" : "+01:00");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toOffsetTime_manualOffset() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
        gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        for (int i = 0; i < 500; i++) {
            OffsetTime test = gcal.toOffsetTime();
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getOffset().getID(), "+00:45");
            gcal.add(Calendar.DATE, 1);
            gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
            gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        }
    }

    //-----------------------------------------------------------------------
    public void test_toZonedDateTime() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            ZonedDateTime test = gcal.toZonedDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "Z");
            assertEquals(test.getZone().getID(), "UTC");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toZonedDateTime_endOfDay() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gcal.set(2008, 0, 1, 23, 59, 59);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            ZonedDateTime test = gcal.toZonedDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), (24L * 60L * 60L - 1L) * 1000000000L);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "Z");
            assertEquals(test.getZone().getID(), "UTC");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toZonedDateTime_fixedZoned() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("GMT+12:00"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            ZonedDateTime test = gcal.toZonedDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "+12:00");
            assertEquals(test.getZone().getID(), "UTC+12:00");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toZonedDateTime_variableZoned() {
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 500; i++) {
            ZonedDateTime test = gcal.toZonedDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            boolean isDST = TimeZone.getTimeZone("Europe/Paris").inDaylightTime(gcal.getTime());
            assertEquals(test.getOffset().getID(), isDST ? "+02:00" : "+01:00");
            assertEquals(test.getZone().getID(), "Europe/Paris");
            gcal.add(Calendar.DATE, 1);
        }
    }

    public void test_toZonedDateTime_manualZoned() {
        // since 00:45 and Europe/Paris are incompatible, I suppose this should throw an exception
        GregorianCalendar gcal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        gcal.set(2008, 0, 1, 0, 0, 0);
        gcal.set(Calendar.MILLISECOND, 0);
        gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
        gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        for (int i = 0; i < 500; i++) {
            ZonedDateTime test = gcal.toZonedDateTime();
            assertEquals(test.getYear(), gcal.get(Calendar.YEAR));
            assertEquals(test.getMonth().getValue(), gcal.get(Calendar.MONTH) + 1);
            assertEquals(test.getDayOfMonth(), gcal.get(Calendar.DATE));
            assertEquals(test.toLocalTime().toNanoOfDay(), 0);
            assertEquals(test.getYear(), i < 366 ? 2008 : 2009);
            assertEquals(test.getOffset().getID(), "+00:45");
            assertEquals(test.getZone().getID(), "Europe/Paris");
            gcal.add(Calendar.DATE, 1);
            gcal.set(Calendar.ZONE_OFFSET, 30 * 60 * 1000);
            gcal.set(Calendar.DST_OFFSET, 15 * 60 * 1000);
        }
    }

}
