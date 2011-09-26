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
 * {@code PeriodUnit} is an immutable definition of a unit of human-scale time.
 * For example, humans typically measure periods of time in units of years, months,
 * days, hours, minutes and seconds. These concepts are defined by instances of
 * this class defined in the chronology classes.
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
     * @param equivalentPeriod  the period this is derived from, not null
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

}
