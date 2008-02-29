/*
 * Copyright (c) 2008 Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;
import org.testng.annotations.Test;

/**
 * Test Now.
 *
 * @author Michael Nascimento Santos
 */
@Test
public class TestNow {
    //-----------------------------------------------------------------------
    public void test_system_isSerializable() throws IOException, ClassNotFoundException {
        Now system = Now.system();
        assertTrue(system instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(system);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
              baos.toByteArray()));
        assertSame(ois.readObject(), system);
    }

    /**
     * This test assumes an Instant instance can eventually be produced in less than one millisecond
     */
    public void test_system_instantVersusCurrentTimeMillis() {
        Now system = Now.system();

        long currentTimeMillis = System.currentTimeMillis();
        Instant instant = system.instant();

        boolean exit = false;

        do {
            Instant newInstant = system.instant();
            long newCurrentTimeMillis = System.currentTimeMillis();

            if (currentTimeMillis == newCurrentTimeMillis) {
                assertEquals(instant, newInstant);
                exit = true;
            }

            currentTimeMillis = newCurrentTimeMillis;
            instant = newInstant;
        } while (!exit);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_system_setTimeZone_null() {
        Now system = Now.system();
        system.setTimeZone(null);
    }

    public void test_system_timeZone() {
        Now system = Now.system();
        TimeZone timeZone = TimeZone.timeZone(ZoneOffset.zoneOffset(1));
        system.setTimeZone(timeZone);
        assertSame(timeZone, system.timeZone());
        assertSame(timeZone, system.currentZonedDateTime().getZone());
    }

    public void test_system_currentYear() {
        assertNotNull(Now.system().currentYear());
    }

    public void test_system_currentMonth() {
        assertNotNull(Now.system().currentMonth());
    }

    public void test_system_today() {
        assertNotNull(Now.system().today());
    }

    public void test_system_yesterday() {
        assertNotNull(Now.system().yesterday());
    }

    public void test_system_tomorrow() {
        assertNotNull(Now.system().tomorrow());
    }

    public void test_system_currentTime() {
        assertNotNull(Now.system().currentTime());
    }

    public void test_system_currentDateTime() {
        assertNotNull(Now.system().currentDateTime());
    }
}
