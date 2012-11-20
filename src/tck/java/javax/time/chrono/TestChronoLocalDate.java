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
package javax.time.chrono;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.time.Duration;
import javax.time.LocalDate;
import javax.time.calendrical.ChronoUnit;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.global.HijrahChrono;
import javax.time.chrono.global.JapaneseChrono;
import javax.time.chrono.global.MinguoChrono;
import javax.time.chrono.global.ThaiBuddhistChrono;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test assertions that must be true for all built-in chronologies.
 */
@Test
public class TestChronoLocalDate {
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
        ChronoLocalDate date = chrono.date(refDate);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDate<?> date2 = chrono2.date(refDate);
            DateTime.WithAdjuster adjuster = new FixedAdjuster(date2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDate<?> notreached = date.with(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException");
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDate<?> result = date.with(adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badPlusAdjusterChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDate date = chrono.date(refDate);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDate<?> date2 = chrono2.date(refDate);
            DateTime.PlusAdjuster adjuster = new FixedAdjuster(date2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDate<?> notreached = date.plus(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException");
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDate<?> result = date.plus(adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badMinusAdjusterChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDate date = chrono.date(refDate);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDate<?> date2 = chrono2.date(refDate);
            DateTime.MinusAdjuster adjuster = new FixedAdjuster(date2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDate<?> notreached = date.minus(adjuster);
                    Assert.fail("WithAdjuster should have thrown a ClassCastException");
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDate<?> result = date.minus(adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badPlusPeriodUnitChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDate date = chrono.date(refDate);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDate<?> date2 = chrono2.date(refDate);
            PeriodUnit adjuster = new FixedPeriodUnit(date2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDate<?> notreached = date.plus(1, adjuster);
                    Assert.fail("PeriodUnit.doAdd plus should have thrown a ClassCastException" + date.getClass()
                            + ", can not be cast to " + date2.getClass());
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDate<?> result = date.plus(1, adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badMinusPeriodUnitChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDate date = chrono.date(refDate);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDate<?> date2 = chrono2.date(refDate);
            PeriodUnit adjuster = new FixedPeriodUnit(date2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDate<?> notreached = date.minus(1, adjuster);
                    Assert.fail("PeriodUnit.doAdd minus should have thrown a ClassCastException" + date.getClass()
                            + ", can not be cast to " + date2.getClass());
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDate<?> result = date.minus(1, adjuster);
                assertEquals(result, date2, "WithAdjuster failed to replace date");
            }
        }
    }

    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_badDateTimeFieldChrono(Chrono chrono) {
        LocalDate refDate = LocalDate.of(1900, 1, 1);
        ChronoLocalDate date = chrono.date(refDate);
        for (Chrono[] clist : data_of_calendars()) {
            Chrono chrono2 = clist[0];
            ChronoLocalDate<?> date2 = chrono2.date(refDate);
            DateTimeField adjuster = new FixedDateTimeField(date2);
            if (chrono != chrono2) {
                try {
                    ChronoLocalDate<?> notreached = date.with(adjuster, 1);
                    Assert.fail("DateTimeField doSet should have thrown a ClassCastException" + date.getClass()
                            + ", can not be cast to " + date2.getClass());
                } catch (ClassCastException cce) {
                    // Expected exception; not an error
                }
            } else {
                // Same chronology,
                ChronoLocalDate<?> result = date.with(adjuster, 1);
                assertEquals(result, date2, "DateTimeField doSet failed to replace date");
            }
        }
    }

    //-----------------------------------------------------------------------
    // isBefore, isAfter, isEqual, DATE_COMPARATOR
    //-----------------------------------------------------------------------
    @Test(groups={"tck"}, dataProvider="calendars")
    public void test_date_comparisons(Chrono chrono) {
        List<ChronoLocalDate<?>> dates = new ArrayList<>();

        ChronoLocalDate<?> date = chrono.date(LocalDate.of(1900, 1, 1));

        // Insert dates in order, no duplicates
        dates.add(date.minus(1000, ChronoUnit.YEARS));
        dates.add(date.minus(100, ChronoUnit.YEARS));
        dates.add(date.minus(10, ChronoUnit.YEARS));
        dates.add(date.minus(1, ChronoUnit.YEARS));
        dates.add(date.minus(1, ChronoUnit.MONTHS));
        dates.add(date.minus(1, ChronoUnit.WEEKS));
        dates.add(date.minus(1, ChronoUnit.DAYS));
        dates.add(date);
        dates.add(date.plus(1, ChronoUnit.DAYS));
        dates.add(date.plus(1, ChronoUnit.WEEKS));
        dates.add(date.plus(1, ChronoUnit.MONTHS));
        dates.add(date.plus(1, ChronoUnit.YEARS));
        dates.add(date.plus(10, ChronoUnit.YEARS));
        dates.add(date.plus(100, ChronoUnit.YEARS));
        dates.add(date.plus(1000, ChronoUnit.YEARS));

        // Check these dates against the corresponding dates for every calendar
        for (Chrono[] clist : data_of_calendars()) {
            List<ChronoLocalDate<?>> otherDates = new ArrayList<>();
            Chrono chrono2 = clist[0];
            for (ChronoLocalDate<?> d : dates) {
                otherDates.add(chrono2.date(d));
            }

            // Now compare  the sequence of original dates with the sequence of converted dates
            for (int i = 0; i < dates.size(); i++) {
                ChronoLocalDate<?> a = dates.get(i);
                for (int j = 0; j < otherDates.size(); j++) {
                    ChronoLocalDate<?> b = otherDates.get(j);
                    int cmp = ChronoLocalDate.DATE_COMPARATOR.compare(a, b);
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

        @Override
        public <R extends DateTime> R doAdd(R dateTime, long periodToAdd) {
            return (R)this.dateTime;
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
        public <R extends DateTime> R doSet(R dateTime, long newValue) {
            return (R) this.dateTime;
        }

        @Override
        public boolean resolve(DateTimeBuilder builder, long value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
