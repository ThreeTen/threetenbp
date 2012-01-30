/*
 * Copyright (c) 2011 Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendrical;

import java.io.Serializable;

import javax.time.Duration;

/**
 * The units of time used by the ISO calendar system, such as 'Days' or 'Minutes'.
 * <p>
 * {@code ISOPeriodUnit} consists of immutable definitions of units of human-scale time.
 * For example, humans typically measure periods of time in units of years, months,
 * days, hours, minutes and seconds. These concepts are defined by instances of
 * this class defined in the chronology classes.
 * <p>
 * Units are either basic or derived. A derived unit can be converted accurately to
 * another smaller unit. A basic unit is fundamental, and has no smaller representation.
 * For example years are a derived unit consisting of 12 months, where a month is a basic unit.
 * <p>
 * Other calendar systems should re-use units from here where appropriate.
 * For example, the definition of a day is usually the same in other calendar systems.
 * To be able to re-use a unit, the definition in the other calendar system must match the
 * definition of a unit and all its dependent units.
 * <p>
 * This class is final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class ISOPeriodUnit extends PeriodUnit implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Ordinal for performance and serialization.
     */
    private final int ordinal;

    /**
     * Restricted constructor.
     */
    private ISOPeriodUnit(int ordinal, String name, long baseEquivalentPeriod, PeriodUnit baseUnit, Duration estimatedDuration) {
        super(name, baseEquivalentPeriod, baseUnit, estimatedDuration);
        this.ordinal = ordinal;
    }

    /**
     * Ensure singletons.
     * @return the singleton, not null
     */
    private Object readResolve() {
        return UNIT_CACHE[ordinal];
    }

    //-----------------------------------------------------------------------
    @Override
    public int compareTo(PeriodUnit other) {
        if (other instanceof ISOPeriodUnit) {
            return ordinal - ((ISOPeriodUnit) other).ordinal;
        }
        return super.compareTo(other);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ISOPeriodUnit) {
            return ordinal == ((ISOPeriodUnit) obj).ordinal;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return ISOPeriodUnit.class.hashCode() + ordinal;
    }

    //-----------------------------------------------------------------------
    /**
     * The period unit for nanoseconds.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is 1 nanosecond.
     * This unit does not depends on any other unit.
     */
    public static final PeriodUnit NANOS = new ISOPeriodUnit(0, "Nanos", 1, null, Duration.ofNanos(1));
    /**
     * The period unit for microseconds.
     * <p>
     * The equivalent period and estimated duration is based on 1000 nanoseconds.
     * This unit depends on the Nanos unit and all units that depends on.
     */
    public static final PeriodUnit MICROS = new ISOPeriodUnit(1, "Micros", 1000L, NANOS, Duration.ofNanos(1000));
    /**
     * The period unit for milliseconds.
     * <p>
     * The equivalent period and estimated duration is based on 1,000,000 nanoseconds.
     * This unit depends on the Micros unit and all units that depends on.
     */
    public static final PeriodUnit MILLIS = new ISOPeriodUnit(2, "Millis", 1000000L, NANOS, Duration.ofMillis(1));
    /**
     * The period unit for seconds.
     * <p>
     * The equivalent period and estimated duration is based on 1,000,000,000 nanoseconds.
     * This unit depends on the Millis unit and all units that depends on.
     */
    public static final PeriodUnit SECONDS = new ISOPeriodUnit(3, "Seconds", 1000000000L, NANOS, Duration.ofSeconds(1));
    /**
     * The period unit for minutes.
     * <p>
     * The equivalent period and estimated duration is based on 60 seconds.
     * This unit depends on the Seconds unit and all units that depends on.
     */
    public static final PeriodUnit MINUTES = new ISOPeriodUnit(4, "Minutes", 60L * 1000000000L, NANOS, Duration.ofSeconds(60));
    /**
     * The period unit for hours.
     * <p>
     * The equivalent period and estimated duration is based on 60 minutes.
     * This unit depends on the Minutes unit and all units that depends on.
     */
    public static final PeriodUnit HOURS = new ISOPeriodUnit(5, "Hours", 60L * 60L * 1000000000L, NANOS, Duration.ofSeconds(60 * 60));
    /**
     * The period unit for twelve hours, as used by AM/PM.
     * <p>
     * The equivalent period and estimated duration is based on 12 hours.
     * This unit depends on the Hours unit and all units that depends on.
     */
    public static final PeriodUnit _12_HOURS = new ISOPeriodUnit(6, "12Hours", 12L * 60L * 60L * 1000000000L, NANOS, Duration.ofSeconds(12 * 60 * 60));
    /**
     * The period unit for twenty-four hours, that is often treated as a day.
     * <p>
     * The period unit defines the concept of a period of exactly 24 hours that
     * is often treated as a day. The unit name of "24Hours" is intended to convey
     * the fact that this is primarily a 24 hour unit that happens to be used as
     * a day unit on occasion. In most scenarios, the standard {@link #DAYS Days}
     * unit is more applicable and accurate.
     * <p>
     * This class defines two units that could represent a day.
     * This unit, {@code 24Hours}, represents a fixed length of exactly 24 hours,
     * allowing it to be converted to seconds, nanoseconds and {@link Duration}.
     * By contrast, the {@code Days} unit varies in length based on time-zone (daylight
     * savings time) changes and cannot be converted to seconds, nanoseconds or {@code Duration}.
     * <p>
     * The equivalent period and estimated duration is based on 24 hours.
     * This unit depends on the 12Hours unit and all units that depends on.
     */
    public static final PeriodUnit _24_HOURS = new ISOPeriodUnit(7, "24Hours", 24L * 60L * 60L * 1000000000L, NANOS, Duration.ofSeconds(24 * 60 * 60));

    /**
     * The period unit for days.
     * <p>
     * The period unit defines the concept of a period of a day.
     * This is typically equal to 24 hours, but may vary due to time-zone changes.
     * <p>
     * This class defines two units that could represent a day.
     * This unit, {@code Days}, represents a day that varies in length based on
     * time-zone (daylight savings time) changes. It is a basic unit that cannot
     * be converted to seconds, nanoseconds or {@link Duration}.
     * By contrast, the {@link #_24_HOURS 24Hours} unit has a fixed length of
     * exactly 24 hours allowing it to be converted to seconds, nanoseconds and {@code Duration}.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to 24 hours.
     * This unit does not depends on any other unit.
     */
    public static final PeriodUnit DAYS = new ISOPeriodUnit(8, "Days", 1, null, Duration.ofSeconds(86400));
    /**
     * The period unit for weeks.
     * <p>
     * The equivalent period and estimated duration is based on 7 days.
     * This unit depends on the Days unit and all units that depends on.
     */
    public static final PeriodUnit WEEKS = new ISOPeriodUnit(9, "Weeks", 7, DAYS, Duration.ofSeconds(7L * 86400L));
    /**
     * The period unit for months.
     * This is typically 28 to 31 days.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to one-twelfth of a year based on 365.2425 days.
     * This unit does not depend on any other unit.
     */
    public static final PeriodUnit MONTHS = new ISOPeriodUnit(10, "Months", 1, null, Duration.ofSeconds(31556952L / 12L));
    /**
     * The period unit for quarters of years.
     * <p>
     * The equivalent period and estimated duration is based on 3 months.
     * This unit depends on the Months unit and all units that depends on.
     */
    public static final PeriodUnit QUARTERS = new ISOPeriodUnit(11, "Quarters", 3, MONTHS, Duration.ofSeconds(31556952L / 4));
    /**
     * The period unit for week-based-years as defined by ISO-8601.
     * This is typically 52 weeks, and occasionally 53 weeks.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to 364.5 days, which is just over 52 weeks.
     * This unit does not depend on any other unit.
     */
    public static final PeriodUnit WEEK_BASED_YEARS = new ISOPeriodUnit(12, "WeekBasedYears", 1, null, Duration.ofSeconds(364L * 86400L + 43200L));  // 364.5 days
    /**
     * The period unit for years.
     * <p>
     * The equivalent period and estimated duration is based on 12 months based on 365.2425 days.
     * This unit depends on the Quarters unit and all units that depends on.
     */
    public static final PeriodUnit YEARS = new ISOPeriodUnit(13, "Years", 12, MONTHS, Duration.ofSeconds(31556952L));  // 365.2425 days
    /**
     * The period unit for decades.
     * <p>
     * The equivalent period and estimated duration is based on 10 years.
     * This unit depends on the Years unit and all units that depends on.
     */
    public static final PeriodUnit DECADES = new ISOPeriodUnit(14, "Decades", 120, MONTHS, Duration.ofSeconds(10L * 31556952L));
    /**
     * The period unit for centuries.
     * <p>
     * The equivalent period and estimated duration is based on 100 years.
     * This unit depends on the Decades unit and all units that depends on.
     */
    public static final PeriodUnit CENTURIES = new ISOPeriodUnit(15, "Centuries", 1200, MONTHS, Duration.ofSeconds(100L * 31556952L));
    /**
     * The period unit for millennia.
     * <p>
     * The equivalent period and estimated duration is based on 1000 years.
     * This unit depends on the Centuries unit and all units that depends on.
     */
    public static final PeriodUnit MILLENNIA = new ISOPeriodUnit(16, "Millennia", 12000, MONTHS, Duration.ofSeconds(1000L * 31556952L));
    /**
     * The period unit for eras.
     * <p>
     * This represents an era based on a simple before/after point on the time-line.
     * Such an era is infinite in length.
     * For this rule, an era has an estimated duration of 2,000,000,000 years.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to 2,000,000,000 years.
     * This unit does not depend on any other unit.
     */
    public static final PeriodUnit ERAS = new ISOPeriodUnit(17, "Eras", 1, null, Duration.ofSeconds(31556952L * 2000000000L));

    /**
     * Cache of units for deserialization.
     * Indices must match ordinal passed to unit constructor.
     */
    private static final PeriodUnit[] UNIT_CACHE = new PeriodUnit[] {
        NANOS, MICROS, MILLIS, SECONDS, MINUTES, HOURS, _12_HOURS, _24_HOURS,
        DAYS, WEEKS, MONTHS, QUARTERS, WEEK_BASED_YEARS, YEARS,
        DECADES, CENTURIES, MILLENNIA, ERAS,
    };

}
