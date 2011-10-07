/*
 * Copyright (c) 2010-2011 Stephen Colebourne & Michael Nascimento Santos
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

import java.io.Serializable;

import javax.time.Duration;
import javax.time.MathUtils;

/**
 * A unit of time for measuring a period, such as 'Days' or 'Minutes'.
 * <p>
 * {@code PeriodUnit} defines a unit of human-scale time. For example, humans typically
 * measure periods of time in units of years, months, days, hours, minutes and seconds.
 * A basic selection of constants is defined in this class.
 * Other calendar systems may define their own constants.
 * <p>
 * Units are either basic or derived. A derived unit can be converted accurately to
 * another smaller unit. A basic unit is fundamental, and has no smaller representation.
 * For example years are a derived unit consisting of 12 months, where a month is a basic unit.
 * The equivalent period in the base unit can be obtained from this unit.
 * <p>
 * This abstract class must be implemented with care to ensure other classes in
 * the framework operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * <p>
 * The subclass is fully responsible for serialization as all fields in this class are
 * transient. The subclass must use {@code readResolve} to replace the deserialized
 * class with a valid one created via a constructor.
 *
 * @author Stephen Colebourne
 */
public abstract class PeriodUnit
        implements Comparable<PeriodUnit>, Serializable {

    private static final int NANOS_ORDINAL = 0;
    private static final int MICROS_ORDINAL = 1;
    private static final int MILLIS_ORDINAL = 2;
    private static final int SECONDS_ORDINAL = 3;
    private static final int MINUTES_ORDINAL = 4;
    private static final int HOURS_ORDINAL = 5;
    private static final int _12_HOURS_ORDINAL = 6;
    private static final int _24_HOURS_ORDINAL = 7;
    private static final int DAYS_ORDINAL = 8;
    private static final int WEEKS_ORDINAL = 9;
    private static final int MONTHS_ORDINAL = 10;
    private static final int QUARTERS_ORDINAL = 11;
    private static final int WEEK_BASED_YEARS_ORDINAL = 12;
    private static final int YEARS_ORDINAL = 13;
    private static final int DECADES_ORDINAL = 14;
    private static final int CENTURIES_ORDINAL = 15;
    private static final int MILLENNIA_ORDINAL = 16;
    private static final int ERAS_ORDINAL = 17;

    /**
     * The period unit for nanoseconds.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is 1 nanosecond.
     * This unit does not depends on any other unit.
     */
    public static final PeriodUnit NANOS = new ISO(NANOS_ORDINAL, "Nanos", 1, null, Duration.ofNanos(1));
    /**
     * The period unit for microseconds.
     * <p>
     * The equivalent period and estimated duration is based on 1000 nanoseconds.
     * This unit depends on the Nanos unit and all units that depends on.
     */
    public static final PeriodUnit MICROS = new ISO(MICROS_ORDINAL, "Micros", 1000L, NANOS, Duration.ofNanos(1000));
    /**
     * The period unit for milliseconds.
     * <p>
     * The equivalent period and estimated duration is based on 1,000,000 nanoseconds.
     * This unit depends on the Micros unit and all units that depends on.
     */
    public static final PeriodUnit MILLIS = new ISO(MILLIS_ORDINAL, "Millis", 1000000L, NANOS, Duration.ofMillis(1));
    /**
     * The period unit for seconds.
     * <p>
     * The equivalent period and estimated duration is based on 1,000,000,000 nanoseconds.
     * This unit depends on the Millis unit and all units that depends on.
     */
    public static final PeriodUnit SECONDS = new ISO(SECONDS_ORDINAL, "Seconds", 1000000000L, NANOS, Duration.ofSeconds(1));
    /**
     * The period unit for minutes.
     * <p>
     * The equivalent period and estimated duration is based on 60 seconds.
     * This unit depends on the Seconds unit and all units that depends on.
     */
    public static final PeriodUnit MINUTES = new ISO(MINUTES_ORDINAL, "Minutes", 60L * 1000000000L, NANOS, Duration.ofSeconds(60));
    /**
     * The period unit for hours.
     * <p>
     * The equivalent period and estimated duration is based on 60 minutes.
     * This unit depends on the Minutes unit and all units that depends on.
     */
    public static final PeriodUnit HOURS = new ISO(HOURS_ORDINAL, "Hours", 60L * 60L * 1000000000L, NANOS, Duration.ofSeconds(60 * 60));
    /**
     * The period unit for twelve hours, as used by AM/PM.
     * <p>
     * The equivalent period and estimated duration is based on 12 hours.
     * This unit depends on the Hours unit and all units that depends on.
     */
    public static final PeriodUnit _12_HOURS = new ISO(_12_HOURS_ORDINAL, "12Hours", 12L * 60L * 60L * 1000000000L, NANOS, Duration.ofSeconds(12 * 60 * 60));
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
    public static final PeriodUnit _24_HOURS = new ISO(_24_HOURS_ORDINAL, "24Hours", 24L * 60L * 60L * 1000000000L, NANOS, Duration.ofSeconds(24 * 60 * 60));

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
    public static final PeriodUnit DAYS = new ISO(DAYS_ORDINAL, "Days", 1, null, Duration.ofSeconds(86400));
    /**
     * The period unit for weeks.
     * <p>
     * The equivalent period and estimated duration is based on 7 days.
     * This unit depends on the Days unit and all units that depends on.
     */
    public static final PeriodUnit WEEKS = new ISO(WEEKS_ORDINAL, "Weeks", 7, DAYS, Duration.ofSeconds(7L * 86400L));
    /**
     * The period unit for months.
     * This is typically 28 to 31 days.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to one-twelfth of a year based on 365.2425 days.
     * This unit does not depend on any other unit.
     */
    public static final PeriodUnit MONTHS = new ISO(MONTHS_ORDINAL, "Months", 1, null, Duration.ofSeconds(31556952L / 12L));
    /**
     * The period unit for quarters of years.
     * <p>
     * The equivalent period and estimated duration is based on 3 months.
     * This unit depends on the Months unit and all units that depends on.
     */
    public static final PeriodUnit QUARTERS = new ISO(QUARTERS_ORDINAL, "Quarters", 3, MONTHS, Duration.ofSeconds(31556952L / 4));
    /**
     * The period unit for week-based-years as defined by ISO-8601.
     * This is typically 52 weeks, and occasionally 53 weeks.
     * <p>
     * This is a basic unit and has no equivalent period.
     * The estimated duration is equal to 364.5 days, which is just over 52 weeks.
     * This unit does not depend on any other unit.
     */
    public static final PeriodUnit WEEK_BASED_YEARS = new ISO(WEEK_BASED_YEARS_ORDINAL, "WeekBasedYears", 1, null, Duration.ofSeconds(364L * 86400L + 43200L));  // 364.5 days
    /**
     * The period unit for years.
     * <p>
     * The equivalent period and estimated duration is based on 12 months based on 365.2425 days.
     * This unit depends on the Quarters unit and all units that depends on.
     */
    public static final PeriodUnit YEARS = new ISO(YEARS_ORDINAL, "Years", 12, MONTHS, Duration.ofSeconds(31556952L));  // 365.2425 days
    /**
     * The period unit for decades.
     * <p>
     * The equivalent period and estimated duration is based on 10 years.
     * This unit depends on the Years unit and all units that depends on.
     */
    public static final PeriodUnit DECADES = new ISO(DECADES_ORDINAL, "Decades", 120, MONTHS, Duration.ofSeconds(10L * 31556952L));
    /**
     * The period unit for centuries.
     * <p>
     * The equivalent period and estimated duration is based on 100 years.
     * This unit depends on the Decades unit and all units that depends on.
     */
    public static final PeriodUnit CENTURIES = new ISO(CENTURIES_ORDINAL, "Centuries", 1200, MONTHS, Duration.ofSeconds(100L * 31556952L));
    /**
     * The period unit for millennia.
     * <p>
     * The equivalent period and estimated duration is based on 1000 years.
     * This unit depends on the Centuries unit and all units that depends on.
     */
    public static final PeriodUnit MILLENNIA = new ISO(MILLENNIA_ORDINAL, "Millennia", 12000, MONTHS, Duration.ofSeconds(1000L * 31556952L));
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
    public static final PeriodUnit ERAS = new ISO(ERAS_ORDINAL, "Eras", 1, null, Duration.ofSeconds(31556952L * 2000000000L));

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the unit, not null.
     */
    private final transient String name;
    /**
     * The estimated duration of the unit, not null.
     */
    private final transient Duration durationEstimate;
    /**
     * The base unit.
     */
    private final transient PeriodUnit baseUnit;
    /**
     * The equivalent period in the base unit.
     */
    private final transient long baseEquivalent;
    /**
     * The cache of the unit hash code.
     */
    private final transient int hashCode;

    /**
     * Constructor to create a base unit that cannot be derived.
     * <p>
     * A base unit cannot be derived from any smaller unit.
     * For example, an ISO month period cannot be derived from any other smaller period.
     * <p>
     * This method is typically only used when writing a {@link Chronology}.
     *
     * @param name  the name of the type, not null
     * @param estimatedDuration  the estimated duration of one unit of this period, not null
     * @throws IllegalArgumentException if the duration is zero or negative
     */
    protected PeriodUnit(String name, Duration estimatedDuration) {
        ISOChronology.checkNotNull(name, "Name must not be null");
        ISOChronology.checkNotNull(estimatedDuration, "Estimated duration must not be null");
        if (estimatedDuration.isNegative() || estimatedDuration.isZero()) {
            throw new IllegalArgumentException("Alternate period must not be negative or zero");
        }
        this.name = name;
        this.durationEstimate = estimatedDuration;
        this.baseUnit = this;
        this.baseEquivalent = 1;
        this.hashCode = calcHashCode();
    }

    /**
     * Constructor to create a unit that is derived from another smaller unit.
     * <p>
     * A derived unit is created as a multiple of a smaller unit.
     * For example, an ISO year period can be derived as 12 ISO month periods.
     * <p>
     * The estimated duration is calculated using {@link PeriodField#toDurationEstimate()}.
     * <p>
     * This method is typically only used when writing a {@link Chronology}.
     *
     * @param name  the name of the type, not null
     * @param baseEquivalentAmount  the equivalent amount that this is derived from, 1 or greater
     * @param baseUnit  the base unit that this is derived from, not null
     * @throws IllegalArgumentException if the period is zero or negative
     * @throws ArithmeticException if the equivalent period calculation overflows
     */
    protected PeriodUnit(String name, long baseEquivalentAmount, PeriodUnit baseUnit) {
        ISOChronology.checkNotNull(name, "Name must not be null");
        ISOChronology.checkNotNull(baseUnit, "Base unit must not be null");
        if (baseUnit != baseUnit.getBaseUnit()) {
            throw new IllegalArgumentException("Unit must be base");
        }
        if (baseEquivalentAmount <= 0) {
            throw new IllegalArgumentException("Amount must not be negative or zero");
        }
        this.name = name;
        this.durationEstimate = baseUnit.getDurationEstimate().multipliedBy(baseEquivalentAmount);
        this.baseUnit = baseUnit;
        this.baseEquivalent = baseEquivalentAmount;
        this.hashCode = calcHashCode();
    }

    /**
     * Constructor used by ISOChronology.
     *
     * @param name  the name of the type, not null
     * @param baseEquivalent  the amount of the equivalent period in the base unit, 1 if this is the base
     * @param baseUnit  the base unit, null if this is the base
     * @param estimatedDuration  the estimated duration of one unit of this period, not null
     * @throws ArithmeticException if the equivalent period calculation overflows
     */
    PeriodUnit(String name, long baseEquivalent, PeriodUnit baseUnit, Duration estimatedDuration) {
        // input known to be valid
        this.name = name;
        this.durationEstimate = estimatedDuration;
        this.baseUnit = (baseUnit != null ? baseUnit : this);
        this.baseEquivalent = baseEquivalent;
        this.hashCode = calcHashCode();
    }

    private int calcHashCode() {
        return name.hashCode() ^ durationEstimate.hashCode() ^ (int) (this.baseEquivalent ^ (this.baseEquivalent >>> 32));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the unit, used as an identifier for the unit.
     * <p>
     * Implementations should use the name that best represents themselves.
     * Most units will have a plural name, such as 'Years' or 'Minutes'.
     * The name is not localized.
     *
     * @return the name of the unit, not null
     */
    public String getName() {
        return name;
    }

    /**
     * Gets an estimate of the duration of the unit.
     * <p>
     * Each unit has a duration which is a reasonable estimate.
     * For those units which can be derived ultimately from nanoseconds, the
     * estimated duration will be accurate. For other units, it will be an estimate.
     * <p>
     * One key use for the estimated duration is to implement {@link Comparable}.
     *
     * @return the estimate of the duration, not null
     */
    public Duration getDurationEstimate() {
        return durationEstimate;
    }

    /**
     * Gets the equivalent period of this unit in terms of the base unit.
     * <p>
     * Obtains the period, in terms of the base unit, equivalent to this unit.
     * For example, the 'Minute' unit has a base equivalent of '60,000,000,000 Nanos'.
     * If this is the base unit, then a period of one of this unit is returned.
     *
     * @return the period, measured in the base unit, equivalent to one of this unit, not null
     */
    public PeriodField getBaseEquivalent() {
        return PeriodField.of(baseEquivalent, baseUnit);
    }

    /**
     * Gets the equivalent period of this unit in terms of the base unit.
     * <p>
     * Obtains the period, in terms of the base unit, equivalent to this unit.
     * For example, the 'Minute' unit has a base equivalent of '60,000,000,000 Nanos',
     * thus this method returns 60,000,000,000.
     * If this is the base unit, then 1 is returned.
     *
     * @return the period, measured in the base unit, equivalent to one of this unit, one or greater
     */
    public long getBaseEquivalentAmount() {
        return baseEquivalent;
    }

    /**
     * Gets the base unit that this unit is derived from.
     * <p>
     * Obtains the underlying base unit.
     * For example, the 'Minute' unit has a base of 'Nanos'.
     * If this is the base unit, then this is returned.
     * Conversion is possible between two units with the same base unit.
     *
     * @return the base unit, not null
     */
    public PeriodUnit getBaseUnit() {
        return baseUnit;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts one unit of this period to the equivalent period in the specified unit.
     * <p>
     * This conversion only succeeds if the two units have the same base unit and if the
     * result is exactly equivalent. The specified unit must be smaller than this unit.
     * For example, if this is the 'Hour' unit and the 'Seconds' unit is requested,
     * then this method would return 3600.
     * <p>
     * This is a low-level operation defined using a primitive {@code long} for performance.
     * Application code should normally use methods on {@link PeriodField}.
     *
     * @param unit  the required unit, not null
     * @return the period, measured in the specified unit, equivalent to one of this unit, negative if unable to convert
     * @throws ArithmeticException if the calculation overflows
     */
    public long toEquivalent(PeriodUnit unit) {
        ISOChronology.checkNotNull(unit, "PeriodUnit must not be null");
        final long thisEquiv = getBaseEquivalentAmount();
        final long otherEquiv = unit.getBaseEquivalentAmount();
        if (getBaseUnit().equals(unit.getBaseUnit()) && thisEquiv % otherEquiv == 0) {
            return thisEquiv / otherEquiv;
        }
        return -1;
    }

    /**
     * Converts the specified period to the equivalent period in this unit.
     * <p>
     * This conversion only succeeds if the two units have the same base unit and if the
     * result is exactly equivalent. The specified unit must be larger than this unit.
     * Thus '2 Hours' can be converted to '120 Minutes', but not vice versa.
     * If the specified unit is this unit, then the specified amount is returned.
     *
     * @param field  the field to convert, not null
     * @return the period, measured in the this unit, equivalent to the specified period, null if unable to convert
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField convertEquivalent(PeriodField field) {
        ISOChronology.checkNotNull(field, "PeriodField must not be null");
        if (field.getUnit() == this) {
            return field;
        }
        return convertEquivalent(field.getAmount(), field.getUnit());
    }

    /**
     * Converts the specified period to the equivalent period in this unit.
     * <p>
     * This conversion only succeeds if the two units have the same base unit and if the
     * result is exactly equivalent. The specified unit must be larger than this unit.
     * Thus '2 Hours' can be converted to '120 Minutes', but not vice versa.
     * If the specified unit is this unit, then the specified amount is returned.
     * <p>
     * This is a low-level operation defined using a primitive {@code long} for performance.
     * Application code should normally use {@link #convertEquivalent(PeriodField)} or
     * methods on {@link PeriodField}.
     *
     * @param amount  the amount in the specified unit
     * @param unit  the unit to convert from, not null
     * @return the period, measured in the this unit, equivalent to the specified period, null if unable to convert
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField convertEquivalent(long amount, PeriodUnit unit) {
        ISOChronology.checkNotNull(unit, "PeriodUnit must not be null");
        if (getBaseUnit().equals(unit.getBaseUnit())) {
            final long thisEquiv = getBaseEquivalentAmount();
            final long otherEquiv = unit.getBaseEquivalentAmount();
            if (otherEquiv % thisEquiv == 0) {
                return field(MathUtils.safeMultiply(amount, otherEquiv / thisEquiv));
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a period field for this unit.
     * <p>
     * This allows the unit to be used as a factory to produce the field.
     * This works well with static imports. For example, these two lines are equivalent:
     * <pre>
     *  DAYS.field(3);
     *  PeriodField.of(3, DAYS);
     * </pre>
     * 
     * @param amount  the amount of the period, measured in terms of this unit, positive or negative
     * @return the created field, not null
     */
    public final PeriodField field(long amount) {
       return PeriodField.of(amount, this); 
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this unit to another.
     * <p>
     * The comparison is based primarily on the {@link #getDurationEstimate() estimated duration}.
     * If that is equal, the name is compared using standard string comparison.
     * Then the base units are compared, followed by the base equivalent period.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     */
    public int compareTo(PeriodUnit other) {
        if (other == this) {
            return 0;
        }
        int cmp = durationEstimate.compareTo(other.durationEstimate);
        if (cmp == 0) {
            cmp = name.compareTo(other.name);
            if (cmp == 0) {
                cmp = MathUtils.safeCompare(baseEquivalent, other.baseEquivalent);
                if (cmp == 0) {
                    cmp = baseUnit.compareTo(other.baseUnit);
                }
            }
        }
        return cmp;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this unit is equal to another unit.
     * <p>
     * The comparison is based on the name, estimated duration, base unit and base equivalent period.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other unit
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PeriodUnit) {
            PeriodUnit other = (PeriodUnit) obj;
            return baseEquivalent == other.baseEquivalent &&
                    name.equals(other.name) &&
                    durationEstimate.equals(other.durationEstimate) &&
                    baseUnit.equals(other.baseUnit);
        }
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * A hash code for this unit.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this unit as a {@code String}, using the name.
     *
     * @return a string representation of this unit, not null
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Constants for the ISO calendar system.
     */
    private static final class ISO extends PeriodUnit implements Serializable {

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
        ISO(int ordinal, String name, long baseEquivalentPeriod, PeriodUnit baseUnit, Duration estimatedDuration) {
            super(name, baseEquivalentPeriod, baseUnit, estimatedDuration);
            this.ordinal = ordinal;
        }

        /**
         * Ensure singletons.
         * @return the singleton, not null
         */
        private Object readResolve() {
            switch (ordinal) {
                case NANOS_ORDINAL: return NANOS;
                case MICROS_ORDINAL: return MICROS;
                case MILLIS_ORDINAL: return MILLIS;
                case SECONDS_ORDINAL: return SECONDS;
                case MINUTES_ORDINAL: return MINUTES;
                case HOURS_ORDINAL: return HOURS;
                case _12_HOURS_ORDINAL: return _12_HOURS;
                case _24_HOURS_ORDINAL: return _24_HOURS;
                case DAYS_ORDINAL: return DAYS;
                case WEEKS_ORDINAL: return WEEKS;
                case MONTHS_ORDINAL: return MONTHS;
                case QUARTERS_ORDINAL: return QUARTERS;
                case WEEK_BASED_YEARS_ORDINAL: return WEEK_BASED_YEARS;
                case YEARS_ORDINAL: return YEARS;
                case DECADES_ORDINAL: return DECADES;
                case CENTURIES_ORDINAL: return CENTURIES;
                case MILLENNIA_ORDINAL: return MILLENNIA;
                case ERAS_ORDINAL: return ERAS;
            }
            throw new IllegalArgumentException("Unknown period unit");
        }

        //-----------------------------------------------------------------------
        @Override
        public int compareTo(PeriodUnit other) {
            if (other instanceof ISO) {
                return ordinal - ((ISO) other).ordinal;
            }
            return super.compareTo(other);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ISO) {
                return ordinal == ((ISO) obj).ordinal;
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return ISO.class.hashCode() + ordinal;
        }
    }

}
