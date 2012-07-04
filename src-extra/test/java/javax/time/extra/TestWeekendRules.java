/*
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.extra;

import static javax.time.DayOfWeek.MONDAY;
import static javax.time.DayOfWeek.SATURDAY;
import static javax.time.DayOfWeek.SUNDAY;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.time.LocalDate;
import javax.time.Month;
import javax.time.calendrical.DateAdjuster;

import org.testng.annotations.Test;

/**
 * Test WeekendRules.
 */
@Test
public class TestWeekendRules {

    //-----------------------------------------------------------------------
    // nextNonWeekendDay()
    //-----------------------------------------------------------------------
    public void test_nextNonWeekendDay_serialization() throws IOException, ClassNotFoundException {
        DateAdjuster nextNonWeekendDay = WeekendRules.nextNonWeekendDay();
        assertTrue(nextNonWeekendDay instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(nextNonWeekendDay);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), nextNonWeekendDay);
    }

    public void factory_nextNonWeekendDay() {
        assertNotNull(WeekendRules.nextNonWeekendDay());
        assertSame(WeekendRules.nextNonWeekendDay(), WeekendRules.nextNonWeekendDay());
    }

    public void test_nextNonWeekendDay() {
        for (Month month : Month.values()) {
            for (int i = 1; i <= month.lengthInDays(false); i++) {
                LocalDate date = LocalDate.of(2007, month, i);
                LocalDate test = WeekendRules.nextNonWeekendDay().adjustDate(date);
                assertTrue(test.isAfter(date));
                assertFalse(test.getDayOfWeek().equals(SATURDAY));
                assertFalse(test.getDayOfWeek().equals(SUNDAY));
                
                switch (date.getDayOfWeek()) {
                    case FRIDAY:
                    case SATURDAY:
                        assertEquals(test.getDayOfWeek(), MONDAY);
                        break;
                    default:
                        assertEquals(date.getDayOfWeek().next(), test.getDayOfWeek());
                }
                
                if (test.getYear() == 2007) {
                    int dayDiff = test.getDayOfYear() - date.getDayOfYear();

                    switch (date.getDayOfWeek()) {
                        case FRIDAY:
                            assertEquals(dayDiff, 3);
                            break;
                        case SATURDAY:
                            assertEquals(dayDiff, 2);
                            break;
                        default:
                            assertEquals(dayDiff, 1);
                    }
                } else {
                    assertEquals(test.getYear(), 2008);
                    assertEquals(test.getMonth(), Month.JANUARY);
                    assertEquals(test.getDayOfMonth(), 1);
                }
            }
        }
    }

    public void test_nextNonWeekendDay_yearChange() {
        LocalDate friday = LocalDate.of(2010, Month.DECEMBER, 31);
        LocalDate monday = WeekendRules.nextNonWeekendDay().adjustDate(friday);
        assertEquals(LocalDate.of(2011, Month.JANUARY, 3), monday);
        
        LocalDate saturday = LocalDate.of(2011, Month.DECEMBER, 31);
        monday = WeekendRules.nextNonWeekendDay().adjustDate(saturday);
        assertEquals(LocalDate.of(2012, Month.JANUARY, 2), monday);
    }

}
