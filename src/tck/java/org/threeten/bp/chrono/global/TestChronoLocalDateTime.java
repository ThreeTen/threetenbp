/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.chrono.global;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.calendrical.ChronoUnit;
import org.threeten.bp.calendrical.DateTime;
import org.threeten.bp.calendrical.DateTimeAccessor;
import org.threeten.bp.calendrical.DateTimeBuilder;
import org.threeten.bp.calendrical.DateTimeField;
import org.threeten.bp.calendrical.DateTimeValueRange;
import org.threeten.bp.calendrical.PeriodUnit;
import org.threeten.bp.chrono.Chrono;
import org.threeten.bp.chrono.ChronoLocalDateTime;
import org.threeten.bp.chrono.ISOChrono;
import org.threeten.bp.chrono.global.HijrahChrono;
import org.threeten.bp.chrono.global.JapaneseChrono;
import org.threeten.bp.chrono.global.MinguoChrono;
import org.threeten.bp.chrono.global.ThaiBuddhistChrono;

/**
 * Test assertions that must be true for all built-in chronologies.
 */
@Test
public class TestChronoLocalDateTime {
    //-----------------------------------------------------------------------
    // regular data factory for names and descriptions of available calendars
    //-----------------------------------------------------------------------
    @DataProvider(name = "calendars")
    Chrono[][] data_of_calendars() {
        return new Chrono[][]{
                    {HijrahChrono.INSTANCE},
                    {ISOChrono.INSTANCE},
                    {JapaneseChrono.INSTANCE},
                    {MinguoChrono.INSTANCE},
                    {ThaiBuddhistChrono.INSTANCE}};
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badWithAdjusterChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            DateTime.WithAdjuster adjuster = new FixedAdjuster(cdt2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDateTime<?> notreached = cdt.with(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException, "
                            + "required: " + cdt + ", supplied: " + cdt2);
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDateTime<?> result = cdt.with(adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badPlusAdjusterChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            DateTime.PlusAdjuster adjuster = new FixedAdjuster(cdt2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDateTime<?> notreached = cdt.plus(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException, "
                            + "required: " + cdt + ", supplied: " + cdt2);
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDateTime<?> result = cdt.plus(adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date time");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badMinusAdjusterChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            DateTime.MinusAdjuster adjuster = new FixedAdjuster(cdt2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDateTime<?> notreached = cdt.minus(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException, "
                            + "required: " + cdt + ", supplied: " + cdt2);
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDateTime<?> result = cdt.minus(adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badPlusPeriodUnitChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            PeriodUnit adjuster = new FixedPeriodUnit(cdt2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDateTime<?> notreached = cdt.plus(1, adjuster);
                    Assert.fail("PeriodUnit.doAdd plus should have thrown a ClassCastException" + cdt
                            + ", can not be cast to " + cdt2);
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDateTime<?> result = cdt.plus(1, adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badMinusPeriodUnitChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            PeriodUnit adjuster = new FixedPeriodUnit(cdt2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDateTime<?> notreached = cdt.minus(1, adjuster);
                    Assert.fail("PeriodUnit.doAdd minus should have thrown a ClassCastException" + cdt.getClass()
                            + ", can not be cast to " + cdt2.getClass());
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDateTime<?> result = cdt.minus(1, adjuster);
                assertEquals(result, cdt2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badDateTimeFieldChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDateTime cdt = chrono.date(refDate).atTime(LocalTime.NOON);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDateTime<?> cdt2 = chrono2.date(refDate).atTime(LocalTime.NOON);
            DateTimeField adjuster = new FixedDateTimeField(cdt2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDateTime<?> notreached = cdt.with(adjuster, 1);
                    Assert.fail("DateTimeField doSet should have thrown a ClassCastException" + cdt.getClass()
                            + ", can not be cast to " + cdt2.getClass());
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDateTime<?> result = cdt.with(adjuster, 1);
                assertEquals(result, cdt2, "DateTimeField doSet failed to replace date");
            }
        }
    }

    //-----------------------------------------------------------------------
    // isBefore, isAfter, isEqual
    //-----------------------------------------------------------------------
    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_datetime_comparisons(Chrono chrono) {
        List<ChronoLocalDateTime<?>> dates = new ArrayList<>();

        ChronoLocalDateTime<?> date = chrono.date(LocalDate.of(1900, 1, 1)).atTime(LocalTime.MIN_TIME);

        // Insert dates in order, no duplicates
        dates.add(date.minus(100, ChronoUnit.YEARS));
        dates.add(date.minus(1, ChronoUnit.YEARS));
        dates.add(date.minus(1, ChronoUnit.MONTHS));
        dates.add(date.minus(1, ChronoUnit.WEEKS));
        dates.add(date.minus(1, ChronoUnit.DAYS));
        dates.add(date.minus(1, ChronoUnit.HOURS));
        dates.add(date.minus(1, ChronoUnit.MINUTES));
        dates.add(date.minus(1, ChronoUnit.SECONDS));
        dates.add(date.minus(1, ChronoUnit.NANOS));
        dates.add(date);
        dates.add(date.plus(1, ChronoUnit.NANOS));
        dates.add(date.plus(1, ChronoUnit.SECONDS));
        dates.add(date.plus(1, ChronoUnit.MINUTES));
        dates.add(date.plus(1, ChronoUnit.HOURS));
        dates.add(date.plus(1, ChronoUnit.DAYS));
        dates.add(date.plus(1, ChronoUnit.WEEKS));
        dates.add(date.plus(1, ChronoUnit.MONTHS));
        dates.add(date.plus(1, ChronoUnit.YEARS));
        dates.add(date.plus(100, ChronoUnit.YEARS));

        // Check these dates against the corresponding dates for every calendar
        for (Chrono[] clist : data_of_calendars()) {
            List<ChronoLocalDateTime<?>> otherDates = new ArrayList<>();
            Chrono chrono2 = clist[0];
            for (ChronoLocalDateTime<?> d : dates) {
                otherDates.add(chrono2.date(d).atTime(d.getTime()));
            }

            // Now compare  the sequence of original dates with the sequence of converted dates
            for (int i = 0; i < dates.size(); i++) {
                ChronoLocalDateTime<?> a = dates.get(i);
                for (int j = 0; j < otherDates.size(); j++) {
                    ChronoLocalDateTime<?> b = otherDates.get(j);
                    int cmp = ChronoLocalDateTime.DATE_TIME_COMPARATOR.compare(a, b);
                    if (i < j) {
                        assertTrue(cmp < 0, a + " compare " + b);
                        assertEquals(a.isBefore(b), true, a + " isBefore " + b);
                        assertEquals(a.isAfter(b), false, a + " isAfter " + b);
                        assertEquals(a.isEqual(b), false, a + " isEqual " + b);
                    } else if (i > j) {
                        assertTrue(cmp > 0, a + " compare " + b);
                        assertEquals(a.isBefore(b), false, a + " isBefore " + b);
                        assertEquals(a.isAfter(b), true, a + " isAfter " + b);
                        assertEquals(a.isEqual(b), false, a + " isEqual " + b);
                    } else {
                        assertTrue(cmp == 0, a + " compare " + b);
                        assertEquals(a.isBefore(b), false, a + " isBefore " + b);
                        assertEquals(a.isAfter(b), false, a + " isAfter " + b);
                        assertEquals(a.isEqual(b), true, a + " isEqual " + b);
                    }
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // Test Serialization of ISO via chrono API
    //-----------------------------------------------------------------------
    @Test( groups={"tck"}, dataProvider="calendars")
    public <C extends Chrono<C>> void test_ChronoLocalDateTimeSerialization(C chrono) throws Exception {
        LocalDateTime ref = LocalDate.of(2000, 1, 5).atTime(12, 1, 2, 3);
        ChronoLocalDateTime<C> orginal = chrono.date(ref).atTime(ref.getTime());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(orginal);
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        ChronoLocalDateTime<C> ser = (ChronoLocalDateTime<C>) in.readObject();
        assertEquals(ser, orginal, "deserialized date is wrong");
    }


    /**
     * FixedAdjusted returns a fixed DateTime in all adjustments.
     * Construct an adjuster with the DateTime that should be returned from doWithAdjustment.
     */
    static class FixedAdjuster implements DateTime.WithAdjuster, DateTime.PlusAdjuster, DateTime.MinusAdjuster {
        private DateTime datetime;

        FixedAdjuster(DateTime datetime) {
            this.datetime = datetime;
        }

        @Override
        public DateTime doWithAdjustment(DateTime ignore) {
            return datetime;
        }

        @Override
        public DateTime doPlusAdjustment(DateTime ignore) {
            return datetime;
        }

        @Override
        public DateTime doMinusAdjustment(DateTime ignore) {
            return datetime;
        }

    }

    /**
     * FixedPeriodUnit returns a fixed DateTime in all adjustments.
     * Construct an FixedPeriodUnit with the DateTime that should be returned from doAdd.
     */
    static class FixedPeriodUnit implements PeriodUnit {
        private DateTime dateTime;

        FixedPeriodUnit(DateTime dateTime) {
            this.dateTime = dateTime;
        }

        @Override
        public String getName() {
            return "FixedPeriodUnit";
        }

        @Override
        public Duration getDuration() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isDurationEstimated() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isSupported(DateTime dateTime) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends DateTime> R doPlus(R dateTime, long periodToAdd) {
            return (R) this.dateTime;
        }

        @Override
        public <R extends DateTime> PeriodBetween between(R dateTime1, R dateTime2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * FixedDateTimeField returns a fixed DateTime in all adjustments.
     * Construct an FixedDateTimeField with the DateTime that should be returned from doSet.
     */
    static class FixedDateTimeField implements DateTimeField {
        private DateTime dateTime;
        FixedDateTimeField(DateTime dateTime) {
            this.dateTime = dateTime;
        }

        @Override
        public String getName() {
            return "FixedDateTimeField";
        }

        @Override
        public PeriodUnit getBaseUnit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public PeriodUnit getRangeUnit() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int compare(DateTimeAccessor dateTime1, DateTimeAccessor dateTime2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public DateTimeValueRange range() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean doIsSupported(DateTimeAccessor dateTime) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public DateTimeValueRange doRange(DateTimeAccessor dateTime) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long doGet(DateTimeAccessor dateTime) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends DateTime> R doWith(R dateTime, long newValue) {
            return (R) this.dateTime;
        }

        @Override
        public boolean resolve(DateTimeBuilder builder, long value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
