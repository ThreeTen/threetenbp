/*
 * Copyright (c) 2008-2009 Stephen Colebourne & Michael Nascimento Santos
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.time.TimeSource;
import javax.time.calendar.field.Year;

import org.testng.annotations.Test;

/**
 * Test Clock.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestClock {

    static class MockSimpleClock extends Clock {
    }

    static class MockClock extends Clock {
        final TimeSource timeSource;
        final TimeZone timeZone;
        MockClock(TimeSource ts, TimeZone tz) {
            timeSource = ts;
            timeZone = tz;
        }
        @Override
        public TimeSource getSource() {
            return timeSource;
        }
        @Override
        public Clock withSource(TimeSource timeSource) {
            return new MockClock(timeSource, timeZone);
        }
        @Override
        public TimeZone getZone() {
            return timeZone;
        }
        @Override
        public Clock withZone(TimeZone timeZone) {
            return new MockClock(timeSource, timeZone);
        }
    }

    private static final ZoneOffset OFFSET = ZoneOffset.zoneOffset(2);
    private static final OffsetDateTime DATE_TIME = OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500, OFFSET);
    private static final TimeSource TIME_SOURCE = TimeSource.fixed(DATE_TIME);
    private static final TimeZone ZONE = TimeZone.timeZone("Europe/Paris");
    private static final Clock MOCK = new MockClock(TIME_SOURCE, ZONE);

    //-----------------------------------------------------------------------
    public void test_simpleClock() throws Exception {
        Clock test = new MockSimpleClock();
        Method[] methods = Clock.class.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()) &&
                    Modifier.isStatic(method.getModifiers()) == false &&
                    method.getParameterTypes().length == 0) {
                try {
                    method.invoke(test);
                    fail("Excepted UnsupportedOperationException");
                } catch (InvocationTargetException ex) {
                    if (ex.getCause().getClass() != UnsupportedOperationException.class) {
                        fail("Excepted UnsupportedOperationException, received " + ex.getCause().getClass());
                    }
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    public void test_mockClock_get() {
        assertEquals(MOCK.getSource(), TIME_SOURCE);
        assertEquals(MOCK.getZone(), ZONE);
    }

    public void test_mockClock_withSource() {
        Clock changed = MOCK.withSource(TimeSource.system());
        assertEquals(changed.getSource(), TimeSource.system());
        assertEquals(changed.getZone(), ZONE);
    }

    public void test_mockClock_withZone() {
        TimeZone london = TimeZone.timeZone("Europe/London");
        Clock changed = MOCK.withZone(london);
        assertEquals(changed.getSource(), TIME_SOURCE);
        assertEquals(changed.getZone(), london);
    }

    //-----------------------------------------------------------------------
    public void test_mockClock_dateAndTime() {
        assertEquals(MOCK.today(), LocalDate.date(2008, 6, 30));
        assertEquals(MOCK.yesterday(), LocalDate.date(2008, 6, 29));
        assertEquals(MOCK.tomorrow(), LocalDate.date(2008, 7, 1));
        
        assertEquals(MOCK.year(), Year.isoYear(2008));
        assertEquals(MOCK.yearMonth(), YearMonth.yearMonth(2008, 6));
        
        assertEquals(MOCK.time(), LocalTime.time(11, 30, 10, 500));
        assertEquals(MOCK.timeToSecond(), LocalTime.time(11, 30, 10));
        assertEquals(MOCK.timeToMinute(), LocalTime.time(11, 30));
        
        assertEquals(MOCK.dateTime(), LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500));
        assertEquals(MOCK.dateTimeToSecond(), LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10));
        assertEquals(MOCK.dateTimeToMinute(), LocalDateTime.dateTime(2008, 6, 30, 11, 30));
        
        assertEquals(MOCK.offsetDate(), OffsetDate.date(2008, 6, 30, OFFSET));
        
        assertEquals(MOCK.offsetTime(), OffsetTime.time(11, 30, 10, 500, OFFSET));
        assertEquals(MOCK.offsetTimeToSecond(), OffsetTime.time(11, 30, 10, OFFSET));
        assertEquals(MOCK.offsetTimeToMinute(), OffsetTime.time(11, 30, OFFSET));
        
        assertEquals(MOCK.offsetDateTime(), OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500, OFFSET));
        assertEquals(MOCK.offsetDateTimeToSecond(), OffsetDateTime.dateTime(2008, 6, 30, 11, 30, 10, OFFSET));
        assertEquals(MOCK.offsetDateTimeToMinute(), OffsetDateTime.dateTime(2008, 6, 30, 11, 30, OFFSET));
        
        assertEquals(MOCK.zonedDateTime(), ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10, 500), ZONE));
        assertEquals(MOCK.zonedDateTimeToSecond(), ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 30, 10), ZONE));
        assertEquals(MOCK.zonedDateTimeToMinute(), ZonedDateTime.dateTime(LocalDateTime.dateTime(2008, 6, 30, 11, 30), ZONE));
    }

}
