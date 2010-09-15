/*
 * Copyright (c) 2007-2010, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.Arrays;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.MathUtils;

/**
 * A period of time measured using a single unit, such as '3 Days' or '65 Seconds'.
 * <p>
 * {@code PeriodField} is an immutable period that stores an amount of human-scale
 * time for a single unit. For example, humans typically measure periods of time
 * in units of years, months, days, hours, minutes and seconds. These concepts are
 * defined by instances of {@link PeriodUnit} in the chronology classes. This class
 * allows an amount to be specified for one of the units, such as '3 Days' or '65 Seconds'.
 * <p>
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy(), negated() and abs(), all of which return a new instance.
 * <p>
 * {@code PeriodField} can store rules of any kind which makes it usable with
 * any calendar system.
 * <p>
 * PeriodField is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class PeriodField
        implements PeriodProvider, Comparable<PeriodField>, Serializable {

    /**
     * The serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The amount of the period.
     */
    private final long amount;
    /**
     * The unit the period is measured in.
     */
    private final PeriodUnit unit;

    /**
     * Obtains a {@code PeriodField} from an amount and unit.
     * <p>
     * The parameters represent the two parts of a phrase like '6 Days'.
     *
     * @param amount  the amount of the period, measured in terms of the unit, positive or negative
     * @param unit  the unit that the period is measured in, not null
     * @return the {@code PeriodField} instance, never null
     */
    public static PeriodField of(long amount, PeriodUnit unit) {
        PeriodFields.checkNotNull(unit, "PeriodUnit must not be null");
        return new PeriodField(amount, unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param amount  the amount of the period, measured in terms of the unit, positive or negative
     * @param unit  the unit that the period is measured in, validated not null
     */
    private PeriodField(long amount, PeriodUnit unit) {
        // input pre-validated
        this.amount = amount;
        this.unit = unit;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this period is zero length.
     * <p>
     * A {@code PeriodField} can be positive, zero or negative.
     * This method checks whether the length is zero.
     *
     * @return true if this period is zero length
     */
    public boolean isZero() {
        return amount == 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of this period.
     * <p>
     * For example, in the period '5 Days', the amount is '5'.
     *
     * @return the amount of time of this period, positive or negative
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Gets the amount of this period, converted to an {@code int}.
     * <p>
     * For example, in the period '5 Days', the amount is '5'.
     *
     * @return the amount of time of this period, positive or negative
     * @throws ArithmeticException if the amount exceeds the capacity of an {@code int}
     */
    public int getAmountInt() {
        return MathUtils.safeToInt(amount);
    }

    /**
     * Gets the unit of this period.
     * <p>
     * For example, in the period '5 Days', the unit is 'Days'.
     *
     * @return the period unit, never null
     */
    public PeriodUnit getUnit() {
        return unit;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with a different amount of time.
     * <p>
     * Calling this method returns a new period with the same unit but different amount.
     * For example, it could be used to change '3 Days' to '5 Days'.
     *
     * @param amount  the amount of time to set in the returned period, positive or negative
     * @return a {@code PeriodField} based on this period with the specified amount, never null
     */
    public PeriodField withAmount(long amount) {
        if (amount == this.amount) {
            return this;
        }
        return new PeriodField(amount, unit);
    }

    /**
     * Returns a copy of this period with a different unit.
     * <p>
     * Calling this method returns a new period with the same amount but different unit.
     * For example, it could be used to change '3 Days' to '3 Months'.
     *
     * @param unit  the unit to set in the returned period, positive or negative
     * @return a {@code PeriodField} based on this period with the specified unit, never null
     */
    public PeriodField withUnit(PeriodUnit unit) {
        PeriodFields.checkNotNull(unit, "PeriodUnit must not be null");
        if (unit.equals(this.unit)) {
            return this;
        }
        return new PeriodField(amount, unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, positive or negative
     * @return a {@code PeriodField} based on this period with the specified period added, never null
     * @throws IllegalArgumetException if the specified period has a different unit
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField plus(PeriodField period) {
        PeriodFields.checkNotNull(period, "PeriodField must not be null");
        if (period.getUnit().equals(unit) == false) {
            throw new IllegalArgumentException("Cannot add '" + period + "' to '" + this + "' as the units differ");
        }
        return plus(period.getAmount());
    }

    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the period to add, measured in the unit of the period, positive or negative
     * @return a {@code PeriodField} based on this period with the specified amount added, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField plus(long amount) {
        return withAmount(MathUtils.safeAdd(this.amount, amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, positive or negative
     * @return a {@code PeriodField} based on this period with the specified period subtracted, never null
     * @throws IllegalArgumetException if the specified has a different unit
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField minus(PeriodField period) {
        PeriodFields.checkNotNull(period, "PeriodField must not be null");
        if (period.getUnit().equals(unit) == false) {
            throw new IllegalArgumentException("Cannot subtract '" + period + "' from '" + this + "' as the units differ");
        }
        return minus(period.getAmount());
    }

    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the period to subtract, measured in the unit of the period, positive or negative
     * @return a {@code PeriodField} based on this period with the specified amount subtracted, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField minus(long amount) {
        return withAmount(MathUtils.safeSubtract(this.amount, amount));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the amount multiplied by the specified scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param scalar  the value to multiply by, positive or negative
     * @return a {@code PeriodField} based on this period multiplied by the specified scalar, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField multipliedBy(long scalar) {
        return withAmount(MathUtils.safeMultiply(amount, scalar));
    }

    /**
     * Returns a copy of this period with the amount divided by the specified divisor.
     * <p>
     * This uses the {@code /} operator and integer division to provide the result.
     * For example, the result of '11 Days' divided by 4 is '2 Days'.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the value to divide by, positive or negative
     * @return a {@code PeriodField} based on this period divided by the specified divisor, never null
     * @throws ArithmeticException if the divisor is zero
     */
    public PeriodField dividedBy(long divisor) {
        return withAmount(amount / divisor);
    }

    /**
     * Returns a copy of this period with the amount as the remainder following
     * division by the specified divisor.
     * <p>
     * This uses the {@code %} operator to provide the result, which may be negative.
     * For example, the remainder of '11 Days' divided by 4 is '3 Days'.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the value to divide by, positive or negative
     * @return a {@code PeriodField} based on this period divided by the specified divisor, never null
     * @throws ArithmeticException if the divisor is zero
     */
    public PeriodField remainder(long divisor) {
        return withAmount(amount % divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the amount negated.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code PeriodField} based on this period with the amount negated, never null
     * @throws ArithmeticException if the amount is {@code Long.MIN_VALUE}
     */
    public PeriodField negated() {
        return withAmount(MathUtils.safeNegate(amount));
    }

    /**
     * Returns a copy of this period with a positive amount.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code PeriodField} based on this period with an absolute amount, never null
     * @throws ArithmeticException if the amount is {@code Long.MIN_VALUE}
     */
    public PeriodField abs() {
        return amount < 0 ? negated() : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this period to an equivalent in the specified unit.
     * <p>
     * This converts this period to one measured in the specified unit.
     * This uses {@link PeriodUnit#getEquivalentPeriod(PeriodUnit)} to lookup
     * the equivalent period for the unit.
     * <p>
     * For example, '3 Hours' could be converted to '180 Minutes'.
     * <p>
     * This method is equivalent to {@link #toEquivalent(PeriodUnit...)} with a single parameter.
     *
     * @param requiredUnit  the unit to convert to, not null
     * @return a period equivalent to this period, never null
     * @throws CalendricalException if this period cannot be converted to the specified unit
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField toEquivalent(PeriodUnit requiredUnit) {
        PeriodField equivalent = unit.getEquivalentPeriod(requiredUnit);
        if (equivalent != null) {
            return equivalent.multipliedBy(amount);
        }
        throw new CalendricalException("Unable to convert " + getUnit() + " to " + requiredUnit);
    }

    /**
     * Converts this period to an equivalent in <i>one</i> of the units specified.
     * <p>
     * This converts this period to one measured in one of the specified units.
     * It operates by trying to convert to each unit in turn until one succeeds.
     * As such, it is recommended to specify the units from largest to smallest.
     * <p>
     * For example, '3 Hours' can normally be converted to both minutes and seconds.
     * If the units array contains both 'Minutes' and 'Seconds', then the result will
     * be measured in whichever is first in the array, either '180 Minutes' or '10800 Seconds'.
     *
     * @param requiredUnits  the required unit array, not altered, not null, no nulls
     * @return a period equivalent to this period, never null
     * @throws CalendricalException if this period cannot be converted to any of the units
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField toEquivalent(PeriodUnit... requiredUnits) {
        for (PeriodUnit requiredUnit : requiredUnits) {
            PeriodField equivalent = unit.getEquivalentPeriod(requiredUnit);
            if (equivalent != null) {
                return equivalent.multipliedBy(amount);
            }
        }
        throw new CalendricalException("Unable to convert " + getUnit() + " to any requested unit: " + Arrays.toString(requiredUnits));
    }

    //-----------------------------------------------------------------------
    /**
     * Estimates the duration of this period.
     * <p>
     * The {@link PeriodUnit} contains an estimated duration for that unit.
     * The value allows an estimate to be calculated for this period irrespective
     * of whether the unit is of fixed or variable duration. The estimate will equal the
     * {@link #toDuration accurate} calculation if the unit is based on the second.
     *
     * @return the estimated duration of this period, positive or negative
     * @throws ArithmeticException if the calculation overflows
     */
    public Duration toEstimatedDuration() {
        return unit.getEstimatedDuration().multipliedBy(amount);
    }

    /**
     * Calculates the accurate duration of this period.
     * <p>
     * The conversion is based on the {@code ISOChronology} definition of the seconds and
     * nanoseconds units. If the unit of this period can be converted to either seconds
     * or nanoseconds then the conversion will succeed, subject to calculation overflow.
     * If the unit cannot be converted then an exception is thrown.
     *
     * @return the duration of this period based on {@code ISOChronology} fields, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public Duration toDuration() {
        PeriodField equivalent = unit.getEquivalentPeriod(ISOChronology.periodSeconds());
        if (equivalent != null) {
            return equivalent.multipliedBy(amount).toEstimatedDuration();
        }
        equivalent = unit.getEquivalentPeriod(ISOChronology.periodNanos());
        if (equivalent != null) {
            return equivalent.multipliedBy(amount).toEstimatedDuration();
        }
        throw new CalendricalException("Unable to convert " + getUnit() + " to a Duration");
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this period to a {@code PeriodFields}.
     * <p>
     * The returned {@code PeriodFields} will always contain the unit even
     * if the amount is zero.
     *
     * @return the equivalent period, never null
     */
    public PeriodFields toPeriodFields() {
        return PeriodFields.of(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this period to the specified period.
     * <p>
     * The comparison orders first by the unit, then by the amount.
     *
     * @param otherPeriod  the other period to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(PeriodField otherPeriod) {
        // there are no isGreaterThan/isLessThan methods as they don't make sense
        int cmp = unit.compareTo(otherPeriod.unit);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(amount, otherPeriod.amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this period is equal to the specified period.
     * <p>
     * The comparison is based on the unit and amount.
     *
     * @param obj  the object to check, null returns false
     * @return true if this period is the same as that specified
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
           return true;
        }
        if (obj instanceof PeriodField) {
            PeriodField other = (PeriodField) obj;
            return this.amount == other.amount &&
                    this.unit.equals(other.unit);
        }
        return false;
    }

    /**
     * Returns the hash code for this period.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return unit.hashCode() ^ (int)( amount ^ (amount >>> 32));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of this period, such as '6 Days'.
     * <p>
     * The format consists of the amount, followed by a space, followed by the unit name.
     *
     * @return a descriptive representation of this period, not null
     */
    @Override
    public String toString() {
        return amount + " " + unit.getName();
    }

}
