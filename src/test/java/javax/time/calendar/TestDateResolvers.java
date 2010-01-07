/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;


import org.testng.annotations.Test;

/**
 * Test DateResolvers.
 *
 * @author Michael Nascimento Santos
 */
@Test
public class TestDateResolvers {
    private static final Year YEAR_2007 = Year.of(2007);
    private static final Year YEAR_2008 = Year.of(2008);

    @SuppressWarnings("unchecked")
    public void test_constructor() throws Exception {
        for (Constructor constructor : DateResolvers.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance(Collections.nCopies(constructor.getParameterTypes().length, null).toArray());
        }
    }

    //-----------------------------------------------------------------------
    // strict()
    //-----------------------------------------------------------------------
    public void test_strict_serialization() throws IOException, ClassNotFoundException {
        DateResolver strict = DateResolvers.strict();
        assertTrue(strict instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(strict);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), strict);
    }

    public void factory_strict() {
        assertNotNull(DateResolvers.strict());
        assertSame(DateResolvers.strict(), DateResolvers.strict());
    }

    public void test_strict_nonLeap() {
        test_strict(YEAR_2007);
    }

    public void test_strict_leap() {
        test_strict(YEAR_2008);
    }

    private void test_strict(Year year) {
        for (MonthOfYear month : MonthOfYear.values()) {
            for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
                try {
                    LocalDate test = DateResolvers.strict().resolveDate(year.getValue(), month, dayOfMonth);
                    assertEquals(test.getYear(), year.getValue());
                    assertSame(test.getMonthOfYear(), month);
                    assertEquals(test.getDayOfMonth(), dayOfMonth);
                } catch (InvalidCalendarFieldException icfe) {
                    assertTrue(month.lengthInDays(year.isLeap()) < dayOfMonth, "M" + month + "D" + dayOfMonth);
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // previousValid()
    //-----------------------------------------------------------------------
    public void test_previousValid_serialization() throws IOException, ClassNotFoundException {
        DateResolver previousValid = DateResolvers.previousValid();
        assertTrue(previousValid instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(previousValid);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), previousValid);
    }

    public void factory_previousValid() {
        assertNotNull(DateResolvers.previousValid());
        assertSame(DateResolvers.previousValid(), DateResolvers.previousValid());
    }

    public void test_previousValid_nonLeap() {
        test_previousValid(YEAR_2007);
    }

    public void test_previousValid_leap() {
        test_previousValid(YEAR_2008);
    }

    private void test_previousValid(Year year) {
        for (MonthOfYear month : MonthOfYear.values()) {
            int monthLength = month.lengthInDays(year.isLeap());

            for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
                LocalDate test = DateResolvers.previousValid().resolveDate(year.getValue(), month, dayOfMonth);
                assertEquals(test.getYear(), year.getValue());
                assertSame(test.getMonthOfYear(), month);

                if (dayOfMonth <= monthLength) {
                    assertEquals(test.getDayOfMonth(), dayOfMonth);
                } else {
                    assertEquals(test.getDayOfMonth(), monthLength);
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // nextValid()
    //-----------------------------------------------------------------------
    public void test_nextValid_serialization() throws IOException, ClassNotFoundException {
        DateResolver nextValid = DateResolvers.nextValid();
        assertTrue(nextValid instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(nextValid);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), nextValid);
    }

    public void factory_nextValid() {
        assertNotNull(DateResolvers.nextValid());
        assertSame(DateResolvers.nextValid(), DateResolvers.nextValid());
    }

    public void test_nextValid_nonLeap() {
        test_nextValid(YEAR_2007);
    }

    public void test_nextValid_leap() {
        test_nextValid(YEAR_2008);
    }

    private void test_nextValid(Year year) {
        for (MonthOfYear month : MonthOfYear.values()) {
            int monthLength = month.lengthInDays(year.isLeap());

            for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
                LocalDate test = DateResolvers.nextValid().resolveDate(year.getValue(), month, dayOfMonth);

                assertEquals(test.getYear(), year.getValue());

                if (dayOfMonth <= monthLength) {
                    assertSame(test.getMonthOfYear(), month);
                    assertEquals(test.getDayOfMonth(), dayOfMonth);
                } else {
                    assertEquals(test.getDayOfMonth(), 1);
                    assertSame(test.getMonthOfYear(), month.next());
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // partLenient()
    //-----------------------------------------------------------------------
    public void test_partLenient_serialization() throws IOException, ClassNotFoundException {
        DateResolver partLenient = DateResolvers.partLenient();
        assertTrue(partLenient instanceof Serializable);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(partLenient);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), partLenient);
    }

    public void factory_partLenient() {
        assertNotNull(DateResolvers.partLenient());
        assertSame(DateResolvers.partLenient(), DateResolvers.partLenient());
    }

    public void test_partLenient_nonLeap() {
        test_partLenient(YEAR_2007);
    }

    public void test_partLenient_leap() {
        test_partLenient(YEAR_2008);
    }

    private void test_partLenient(Year year) {
        for (MonthOfYear month : MonthOfYear.values()) {
            int monthLength = month.lengthInDays(year.isLeap());

            for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
                LocalDate test = DateResolvers.partLenient().resolveDate(year.getValue(), month, dayOfMonth);

                assertEquals(test.getYear(), year.getValue());

                if (dayOfMonth <= monthLength) {
                    assertSame(test.getMonthOfYear(), month);
                    assertEquals(test.getDayOfMonth(), dayOfMonth);
                } else {
                    assertEquals(test.getDayOfMonth(), dayOfMonth - monthLength);
                    assertSame(test.getMonthOfYear(), month.next());
                }
            }
        }
    }

}
