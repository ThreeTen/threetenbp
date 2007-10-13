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
    public static Period yearsMonthsDays(int years, int months, int days) {
        Map<PeriodUnit, Integer> map = new HashMap<PeriodUnit, Integer>();
        map.put(YEARS, years);
        map.put(MONTHS, months);
        map.put(DAYS, days);
        return Period.periodOf(map);
    }

    /**
     * Obtains an instance of <code>Period</code> from hours, minutes and seconds.
     *
     * @param hours  the hours to represent
     * @param minutes  the minutes to represent
     * @param seconds  the seconds to represent
     * @return the created Period, never null
     */
    public static Period hoursMinutesSeconds(int hours, int minutes, int seconds) {
        Map<PeriodUnit, Integer> map = new HashMap<PeriodUnit, Integer>();
        map.put(HOURS, hours);
        map.put(MINUTES, minutes);
        map.put(SECONDS, seconds);
        return Period.periodOf(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a period of years.
     *
     * @param years  the years to represent
     * @return the created Period, never null
     */
    public static Period years(int years) {
        Map<PeriodUnit, Integer> map = singletonMap(YEARS, years);
        return Period.periodOf(map);
    }

    /**
     * Creates a period of quarters.
     *
     * @param quarters  the quarters to represent
     * @return the created Period, never null
     */
    public static Period quarters(int quarters) {
        Map<PeriodUnit, Integer> map = singletonMap(QUARTERS, quarters);
        return Period.periodOf(map);
    }

    /**
     * Creates a period of months.
     *
     * @param months  the months to represent
     * @return the created Period, never null
     */
    public static Period months(int months) {
        Map<PeriodUnit, Integer> map = singletonMap(MONTHS, months);
        return Period.periodOf(map);
    }

    /**
     * Creates a period of weeks.
     *
     * @param weeks  the weeks to represent
     * @return the created Period, never null
     */
    public static Period weeks(int weeks) {
        Map<PeriodUnit, Integer> map = singletonMap(WEEKS, weeks);
        return Period.periodOf(map);
    }

    /**
     * Creates a period of days.
     *
     * @param days  the days to represent
     * @return the created Period, never null
     */
    public static Period days(int days) {
        Map<PeriodUnit, Integer> map = singletonMap(DAYS, days);
        return Period.periodOf(map);
    }

    /**
     * Creates a period of hours.
     *
     * @param hours  the hours to represent
     * @return the created Period, never null
     */
    public static Period hours(int hours) {
        Map<PeriodUnit, Integer> map = singletonMap(HOURS, hours);
        return Period.periodOf(map);
    }

    /**
     * Creates a period of minutes.
     *
     * @param minutes  the minutes to represent
     * @return the created Period, never null
     */
    public static Period minutes(int minutes) {
        Map<PeriodUnit, Integer> map = singletonMap(MINUTES, minutes);
        return Period.periodOf(map);
    }

    /**
     * Creates a period of seconds.
     *
     * @param seconds  the seconds to represent
     * @return the created Period, never null
     */
    public static Period seconds(int seconds) {
        Map<PeriodUnit, Integer> map = singletonMap(SECONDS, seconds);
        return Period.periodOf(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a builder suitable for creating a period.
     *
     * @return the builder, never null
     */
    public static YearsBuilder periodBuilder() {
        return new YearsBuilder();
    }

    //-----------------------------------------------------------------------
    /**
     * Class that can add seconds to the current state of the builder.
     */
    public static class Builder {
        /** The state of the period being built. */
        protected Map<PeriodUnit, Integer> map = new HashMap<PeriodUnit, Integer>();
        /**
         * Builds the period into a <code>Period</code>.
         * @return the created Period, never null
         */
        public Period build() {
            return Period.periodOf(map);
        }
    }

    /**
     * Class that can add seconds to the current state of the builder.
     */
    public static class SecondsBuilder extends Builder {
        public Builder seconds(int seconds) {
            map.put(SECONDS, seconds);
            return this;
        }
    }

    /**
     * Class that can add minutes to the current state of the builder.
     */
    public static class MinutesBuilder extends SecondsBuilder {
        public SecondsBuilder seconds(int minutes) {
            map.put(MINUTES, minutes);
            return this;
        }
    }

    /**
     * Class that can add hours to the current state of the builder.
     */
    public static class HoursBuilder extends MinutesBuilder {
        public MinutesBuilder hours(int hours) {
            map.put(HOURS, hours);
            return this;
        }
    }

    /**
     * Class that can add days to the current state of the builder.
     */
    public static class DaysBuilder extends HoursBuilder {
        public MinutesBuilder days(int days) {
            map.put(DAYS, days);
            return this;
        }
    }

    /**
     * Class that can add months to the current state of the builder.
     */
    public static class MonthsBuilder extends DaysBuilder {
        public MinutesBuilder months(int months) {
            map.put(MONTHS, months);
            return this;
        }
    }

    /**
     * Class that can add years to the current state of the builder.
     */
    public static class YearsBuilder extends MonthsBuilder {
        public MinutesBuilder years(int years) {
            map.put(YEARS, years);
            return this;
        }
    }

}
