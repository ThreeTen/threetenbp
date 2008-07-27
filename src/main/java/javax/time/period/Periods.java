/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.period;

import static java.util.Collections.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class providing constants and factories for working with periods.
 * <p>
 * Periods is non-instantiable.
 *
 * @author Stephen Colebourne
 */
public final class Periods {

    /**
     * Period range of forever.
     */
    public static final PeriodUnit FOREVER = PeriodUnit.createUnit("Forever");
    /**
     * Period unit for years.
     */
    public static final PeriodUnit YEARS = PeriodUnit.createUnit("Years");
    /**
     * Period unit for quarters.
     */
    public static final PeriodUnit QUARTERS = PeriodUnit.createUnit("Quarters");
    /**
     * Period unit for months.
     */
    public static final PeriodUnit MONTHS = PeriodUnit.createUnit("Months");
    /**
     * Period unit for weeks.
     */
    public static final PeriodUnit WEEKS = PeriodUnit.createUnit("Weeks");
    /**
     * Period unit for days.
     */
    public static final PeriodUnit DAYS = PeriodUnit.createUnit("Days");
    /**
     * Period unit for half days.
     */
    public static final PeriodUnit HALF_DAYS = PeriodUnit.createUnit("HalfDays");
    /**
     * Period unit for hours.
     */
    public static final PeriodUnit HOURS = PeriodUnit.createUnit("Hours");
    /**
     * Period unit for minutes.
     */
    public static final PeriodUnit MINUTES = PeriodUnit.createUnit("Minutes");
    /**
     * Period unit for seconds.
     */
    public static final PeriodUnit SECONDS = PeriodUnit.createUnit("Seconds");
    /**
     * Period unit for milliseconds.
     */
    public static final PeriodUnit MILLIS = PeriodUnit.createUnit("Millis");
    /**
     * Period unit for nanoseconds.
     */
    public static final PeriodUnit NANOS = PeriodUnit.createUnit("Nanos");

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private Periods() {
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Period</code> from years, months and days.
     *
     * @param years  the years to represent
     * @param months  the months to represent
     * @param days  the days to represent
     * @return the created Period, never null
     */
    public static PeriodFields yearsMonthsDays(int years, int months, int days) {
        Map<PeriodUnit, Integer> map = new HashMap<PeriodUnit, Integer>();
        map.put(YEARS, years);
        map.put(MONTHS, months);
        map.put(DAYS, days);
        return PeriodFields.period(map);
    }

    /**
     * Obtains an instance of <code>Period</code> from hours, minutes and seconds.
     *
     * @param hours  the hours to represent
     * @param minutes  the minutes to represent
     * @param seconds  the seconds to represent
     * @return the created Period, never null
     */
    public static PeriodFields hoursMinutesSeconds(int hours, int minutes, int seconds) {
        Map<PeriodUnit, Integer> map = new HashMap<PeriodUnit, Integer>();
        map.put(HOURS, hours);
        map.put(MINUTES, minutes);
        map.put(SECONDS, seconds);
        return PeriodFields.period(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a period of years.
     *
     * @param years  the years to represent
     * @return the created Period, never null
     */
    public static PeriodFields years(int years) {
        Map<PeriodUnit, Integer> map = singletonMap(YEARS, years);
        return PeriodFields.period(map);
    }

    /**
     * Creates a period of quarters.
     *
     * @param quarters  the quarters to represent
     * @return the created Period, never null
     */
    public static PeriodFields quarters(int quarters) {
        Map<PeriodUnit, Integer> map = singletonMap(QUARTERS, quarters);
        return PeriodFields.period(map);
    }

    /**
     * Creates a period of months.
     *
     * @param months  the months to represent
     * @return the created Period, never null
     */
    public static PeriodFields months(int months) {
        Map<PeriodUnit, Integer> map = singletonMap(MONTHS, months);
        return PeriodFields.period(map);
    }

    /**
     * Creates a period of weeks.
     *
     * @param weeks  the weeks to represent
     * @return the created Period, never null
     */
    public static PeriodFields weeks(int weeks) {
        Map<PeriodUnit, Integer> map = singletonMap(WEEKS, weeks);
        return PeriodFields.period(map);
    }

    /**
     * Creates a period of days.
     *
     * @param days  the days to represent
     * @return the created Period, never null
     */
    public static PeriodFields days(int days) {
        Map<PeriodUnit, Integer> map = singletonMap(DAYS, days);
        return PeriodFields.period(map);
    }

    /**
     * Creates a period of hours.
     *
     * @param hours  the hours to represent
     * @return the created Period, never null
     */
    public static PeriodFields hours(int hours) {
        Map<PeriodUnit, Integer> map = singletonMap(HOURS, hours);
        return PeriodFields.period(map);
    }

    /**
     * Creates a period of minutes.
     *
     * @param minutes  the minutes to represent
     * @return the created Period, never null
     */
    public static PeriodFields minutes(int minutes) {
        Map<PeriodUnit, Integer> map = singletonMap(MINUTES, minutes);
        return PeriodFields.period(map);
    }

    /**
     * Creates a period of seconds.
     *
     * @param seconds  the seconds to represent
     * @return the created Period, never null
     */
    public static PeriodFields seconds(int seconds) {
        Map<PeriodUnit, Integer> map = singletonMap(SECONDS, seconds);
        return PeriodFields.period(map);
    }

    /**
     * Creates a period of nanoseconds.
     *
     * @param nanos  the nanoseconds to represent
     * @return the created Period, never null
     */
    public static PeriodFields nanos(int nanos) {
        Map<PeriodUnit, Integer> map = singletonMap(NANOS, nanos);
        return PeriodFields.period(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a builder suitable for creating a period.
     * <p>
     * This method provides the entry point to the builder pattern for
     * constructing <code>Period</code> instances. The pattern allows any
     * period to be easily constructed using clear code that is also efficient.
     * Builders are intended to be used via method chaining.
     * <pre>
     * import static javax.time.period.Periods.*;
     * Period p = periodBuilder().years(2).days(5).minutes(30).build();
     * </pre>
     * As can be seen in the example, the builder is created by calling this
     * method. Each required period is then added, with a final <code>build()</code>
     * to convert the builder to a <code>Period</code>. Note that not all the
     * fields from years to seconds have to be specified.
     * <p>
     * The builder classes are not thread-safe.
     * Do not create a builder and make it available to another thread.
     *
     * @return the builder, never null
     */
    public static YearsBuilder periodBuilder() {
        return new YearsBuilder();
    }

    //-----------------------------------------------------------------------
    /**
     * Base period builder class.
     * <p>
     * This method forms part of the builder pattern, see
     * {@link Periods#periodBuilder()}.
     */
    public static class Builder {
        /** The state of the period being built. */
        protected final Map<PeriodUnit, Integer> map = new HashMap<PeriodUnit, Integer>();
        /**
         * Builds the period into a <code>Period</code>.
         * <p>
         * This method is the last stage in the builder pattern.
         * It converts the period information previously added to a
         * <code>Period</code> instance.
         * <p>
         * For full details on the builder pattern, see
         * {@link Periods#periodBuilder()}.
         *
         * @return the created <code>Period</code>, never null
         */
        public PeriodFields build() {
            return PeriodFields.period(map);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Builder class that can add seconds to the current state of the builder.
     * <p>
     * This method forms part of the builder pattern, see
     * {@link Periods#periodBuilder()}.
     */
    public static class SecondsBuilder extends Builder {
        /**
         * Adds a number of seconds to the period that is being built.
         * <p>
         * This method forms part of the builder pattern, see
         * {@link Periods#periodBuilder()}.
         *
         * @param seconds  the number of seconds to store in the period, may be negative
         * @return <code>this</code>, suitable for method chaining to construct the period, never null
         */
        public Builder seconds(int seconds) {
            map.put(SECONDS, seconds);
            return this;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Builder class that can add minutes to the current state of the builder.
     * <p>
     * This method forms part of the builder pattern, see
     * {@link Periods#periodBuilder()}.
     */
    public static class MinutesBuilder extends SecondsBuilder {
        /**
         * Adds a number of minutes to the period that is being built.
         * <p>
         * This method forms part of the builder pattern, see
         * {@link Periods#periodBuilder()}.
         *
         * @param minutes  the number of minutes to store in the period, may be negative
         * @return <code>this</code>, suitable for method chaining to construct the period, never null
         */
        public SecondsBuilder minutes(int minutes) {
            map.put(MINUTES, minutes);
            return this;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Builder class that can add hours to the current state of the builder.
     * <p>
     * This method forms part of the builder pattern, see
     * {@link Periods#periodBuilder()}.
     */
    public static class HoursBuilder extends MinutesBuilder {
        /**
         * Adds a number of hours to the period that is being built.
         * <p>
         * This method forms part of the builder pattern, see
         * {@link Periods#periodBuilder()}.
         *
         * @param hours  the number of hours to store in the period, may be negative
         * @return <code>this</code>, suitable for method chaining to construct the period, never null
         */
        public MinutesBuilder hours(int hours) {
            map.put(HOURS, hours);
            return this;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Builder class that can add days to the current state of the builder.
     * <p>
     * This method forms part of the builder pattern, see
     * {@link Periods#periodBuilder()}.
     */
    public static class DaysBuilder extends HoursBuilder {
        /**
         * Adds a number of days to the period that is being built.
         * <p>
         * This method forms part of the builder pattern, see
         * {@link Periods#periodBuilder()}.
         *
         * @param days  the number of days to store in the period, may be negative
         * @return <code>this</code>, suitable for method chaining to construct the period, never null
         */
        public HoursBuilder days(int days) {
            map.put(DAYS, days);
            return this;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Builder class that can add months to the current state of the builder.
     * <p>
     * This method forms part of the builder pattern, see
     * {@link Periods#periodBuilder()}.
     */
    public static class MonthsBuilder extends DaysBuilder {
        /**
         * Adds a number of months to the period that is being built.
         * <p>
         * This method forms part of the builder pattern, see
         * {@link Periods#periodBuilder()}.
         *
         * @param months  the number of months to store in the period, may be negative
         * @return <code>this</code>, suitable for method chaining to construct the period, never null
         */
        public DaysBuilder months(int months) {
            map.put(MONTHS, months);
            return this;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Builder class that can add years to the current state of the builder.
     * <p>
     * This method forms part of the builder pattern, see
     * {@link Periods#periodBuilder()}.
     */
    public static class YearsBuilder extends MonthsBuilder {
        /**
         * Adds a number of years to the period that is being built.
         * <p>
         * This method forms part of the builder pattern, see
         * {@link Periods#periodBuilder()}.
         *
         * @param years  the number of years to store in the period, may be negative
         * @return <code>this</code>, suitable for method chaining to construct the period, never null
         */
        public MonthsBuilder years(int years) {
            map.put(YEARS, years);
            return this;
        }
    }

}
