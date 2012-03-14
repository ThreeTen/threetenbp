/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.builder;

import javax.time.Duration;

/**
 * A standard set of time periods units not tied to any specific calendar system.
 * <p>
 * These are the basic set of units common across many calendar systems.
 * Each unit is well-defined only in the presence of a suitable {@link Chrono}.
 * 
 * @author Stephen Colebourne
 */
public enum StandardPeriodUnit implements PeriodUnit {

    /**
     * Unit that represents the concept of a nanosecond.
     * This unit is the smallest supported unit of time.
     * It is usually equal to the 1,000,000,000th part of the second unit, however this
     * definition is chronology specific.
     */
    NANOS("Nanos", Duration.ofNanos(1)),
    /**
     * Unit that represents the concept of a microsecond.
     * It is usually equal to the 1,000,000th part of the second unit, however this
     * definition is chronology specific.
     */
    MICROS("Micros", Duration.ofNanos(1000)),
    /**
     * Unit that represents the concept of a millisecond.
     * It is usually equal to the 1000th part of the second unit, however this
     * definition is chronology specific.
     */
    MILLIS("Millis", Duration.ofMillis(1000000)),
    /**
     * Unit that represents the concept of a second.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to a second in the
     * SI system of units except around a leap-second.
     */
    SECONDS("Seconds", Duration.ofSeconds(1)),
    /**
     * Unit that represents the concept of a minute.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to 60 seconds.
     */
    MINUTES("Minutes", Duration.ofSeconds(60)),
    /**
     * Unit that represents the concept of an hour.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to 60 minutes.
     */
    HOURS("Hours", Duration.ofSeconds(3600)),
    /**
     * Unit that represents the concept of a meridiem, which is half a day.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to 12 hours.
     */
    MERIDIEMS("Meridiems", Duration.ofSeconds(43200)),
    /**
     * Unit that represents the concept of a day.
     * The exact meaning of this unit is chronology specific, however it must correspond
     * to the day defined by the rising and setting of the Sun on Earth.
     * It is not required that days begin at midnight - when converting between calendar
     * systems, the date should be equivalent at midday.
     * All supplied chronologies use a definition, ignoring time-zones, that is equal to 24 hours.
     */
    DAYS("Days", Duration.ofSeconds(86400)),
    /**
     * Unit that represents the concept of a week.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days.
     * A week is typically 7 days, however some calendar systems have other week lengths.
     */
    WEEKS("Weeks", Duration.ofSeconds(7 * 86400L)),
    /**
     * Unit that represents the concept of a month.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days.
     */
    MONTHS("Months", Duration.ofSeconds(31556952L / 12)),
    /**
     * Unit that represents the concept of a quarter-year.
     * The exact meaning of this unit is chronology specific, although it should generally
     * be about one quarter the length of a year. It must be an integral number of days.
     */
    QUARTER_YEARS("QuarterYears", Duration.ofSeconds(31556952L / 4)),
    /**
     * Unit that represents the concept of a half-year.
     * The exact meaning of this unit is chronology specific, although it should generally
     * be about half the length of a year. It must be an integral number of days.
     */
    HALF_YEARS("HalfYears", Duration.ofSeconds(31556952L / 2)),
    /**
     * Unit that represents the concept of a year.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days and should relate to some degree to the passage
     * of the Earth around the Sun.
     */
    YEARS("Years", Duration.ofSeconds(31556952L)),
    /**
     * Unit that represents the concept of a decade.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days and is normally an integral number of years.
     * All supplied chronologies use a definition that is equal to 10 years.
     */
    DECADES("Decades", Duration.ofSeconds(31556952L * 10L)),
    /**
     * Unit that represents the concept of a century.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days and is normally an integral number of years.
     * All supplied chronologies use a definition that is equal to 100 years.
     */
    CENTURIES("Centuries", Duration.ofSeconds(31556952L * 100L)),
    /**
     * Unit that represents the concept of a millenium.
     * The exact meaning of this unit is chronology specific, however it must be
     * an integral number of days and is normally an integral number of years.
     * All supplied chronologies use a definition that is equal to 1000 years.
     */
    MILLENIA("Millenia", Duration.ofSeconds(31556952L * 1000L)),
    /**
     * Unit that represents the concept of an era.
     * The exact meaning of this unit is chronology specific.
     * All supplied chronologies use a definition that is equal to 1000 years.
     */
    ERAS("Eras", Duration.ofSeconds(31556952L * 1000000000L)),
    /**
     * Unit that represents the concept of forever.
     * This is primarily used with {@link DateTimeField} to represent unbounded fields
     * such as the year or era.
     */
    FOREVER("Forever", Duration.ofSeconds(Long.MAX_VALUE, 999999999));

    private final String name;
    private final Duration estimatedDuration;

    private StandardPeriodUnit(String name, Duration estimatedDuration) {
        this.name = name;
        this.estimatedDuration = estimatedDuration;
    }

    @Override
    public String getName() {
        return name;
    }

    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    // TODO: needed? options are:
    // DAYS.of(6)
    // Period.of(6, DAYS)
    // days(6)  ...with static import class
    public Period of(long amount) {
        return Period.of(amount, this);
    }

}
