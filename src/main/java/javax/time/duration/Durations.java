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
package javax.time.duration;

import static java.util.Collections.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class providing constants and factories for working with durations.
 * <p>
 * Durations is non-instantiable.
 *
 * @author Stephen Colebourne
 */
public final class Durations {

    /**
     * Duration unit for years.
     */
    public static final DurationUnit YEARS = DurationUnit.createUnit("Years");
    /**
     * Duration unit for quarters.
     */
    public static final DurationUnit QUARTERS = DurationUnit.createUnit("Quarters");
    /**
     * Duration unit for months.
     */
    public static final DurationUnit MONTHS = DurationUnit.createUnit("Months");
    /**
     * Duration unit for weeks.
     */
    public static final DurationUnit WEEKS = DurationUnit.createUnit("Weeks");
    /**
     * Duration unit for days.
     */
    public static final DurationUnit DAYS = DurationUnit.createUnit("Days");
    /**
     * Duration unit for hours.
     */
    public static final DurationUnit HOURS = DurationUnit.createUnit("Hours");
    /**
     * Duration unit for minutes.
     */
    public static final DurationUnit MINUTES = DurationUnit.createUnit("Minutes");
    /**
     * Duration unit for seconds.
     */
    public static final DurationUnit SECONDS = DurationUnit.createUnit("Seconds");

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private Durations() {
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>Duration</code> from years, months and days.
     *
     * @param years  the years to represent
     * @param months  the months to represent
     * @param days  the days to represent
     * @return the created Duration, never null
     */
    public static Duration yearsMonthsDays(int years, int months, int days) {
        Map<DurationUnit, Integer> map = new HashMap<DurationUnit, Integer>();
        map.put(YEARS, years);
        map.put(MONTHS, months);
        map.put(DAYS, days);
        return Duration.durationOf(map);
    }

    /**
     * Obtains an instance of <code>Duration</code> from hours, minutes and seconds.
     *
     * @param hours  the hours to represent
     * @param minutes  the minutes to represent
     * @param seconds  the seconds to represent
     * @return the created Duration, never null
     */
    public static Duration hoursMinutesSeconds(int hours, int minutes, int seconds) {
        Map<DurationUnit, Integer> map = new HashMap<DurationUnit, Integer>();
        map.put(HOURS, hours);
        map.put(MINUTES, minutes);
        map.put(SECONDS, seconds);
        return Duration.durationOf(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a duration of years.
     *
     * @param years  the years to represent
     * @return the created Duration, never null
     */
    public static Duration years(int years) {
        Map<DurationUnit, Integer> map = singletonMap(YEARS, years);
        return Duration.durationOf(map);
    }

    /**
     * Creates a duration of quarters.
     *
     * @param quarters  the quarters to represent
     * @return the created Duration, never null
     */
    public static Duration quarters(int quarters) {
        Map<DurationUnit, Integer> map = singletonMap(QUARTERS, quarters);
        return Duration.durationOf(map);
    }

    /**
     * Creates a duration of months.
     *
     * @param months  the months to represent
     * @return the created Duration, never null
     */
    public static Duration months(int months) {
        Map<DurationUnit, Integer> map = singletonMap(MONTHS, months);
        return Duration.durationOf(map);
    }

    /**
     * Creates a duration of weeks.
     *
     * @param weeks  the weeks to represent
     * @return the created Duration, never null
     */
    public static Duration weeks(int weeks) {
        Map<DurationUnit, Integer> map = singletonMap(WEEKS, weeks);
        return Duration.durationOf(map);
    }

    /**
     * Creates a duration of days.
     *
     * @param days  the days to represent
     * @return the created Duration, never null
     */
    public static Duration days(int days) {
        Map<DurationUnit, Integer> map = singletonMap(DAYS, days);
        return Duration.durationOf(map);
    }

    /**
     * Creates a duration of hours.
     *
     * @param hours  the hours to represent
     * @return the created Duration, never null
     */
    public static Duration hours(int hours) {
        Map<DurationUnit, Integer> map = singletonMap(HOURS, hours);
        return Duration.durationOf(map);
    }

    /**
     * Creates a duration of minutes.
     *
     * @param minutes  the minutes to represent
     * @return the created Duration, never null
     */
    public static Duration minutes(int minutes) {
        Map<DurationUnit, Integer> map = singletonMap(MINUTES, minutes);
        return Duration.durationOf(map);
    }

    /**
     * Creates a duration of seconds.
     *
     * @param seconds  the seconds to represent
     * @return the created Duration, never null
     */
    public static Duration seconds(int seconds) {
        Map<DurationUnit, Integer> map = singletonMap(SECONDS, seconds);
        return Duration.durationOf(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a builder suitable for creating a duration.
     *
     * @return the builder, never null
     */
    public static YearsBuilder durationBuilder() {
        return new YearsBuilder();
    }

    //-----------------------------------------------------------------------
    /**
     * Class that can add seconds to the current state of the builder.
     */
    public static class Builder {
        /** The state of the duration being built. */
        protected Map<DurationUnit, Integer> map = new HashMap<DurationUnit, Integer>();
        /**
         * Builds the duration into a <code>Duration</code>.
         * @return the created Duration, never null
         */
        public Duration build() {
            return Duration.durationOf(map);
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
