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
package javax.time.period;

import java.io.Serializable;
import java.util.Arrays;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.MathUtils;
import javax.time.calendar.PeriodUnit;

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
 * dividedBy() and negated(), all of which return a new instance
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
     *  The amount of the period.
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
     * @param amount  the amount of the period, measured in terms of the unit, may be negative
     * @param unit  the unit that the period is measured in, not null
     */
    public static PeriodField of(long amount, PeriodUnit unit) {
        PeriodFields.checkNotNull(unit, "PeriodUnit must not be null");
        return new PeriodField(amount, unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param amount  the amount of the period, measured in terms of the unit, may be negative
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
     *
     * @return true if this period is zero length
     */
    public boolean isZero() {
        return amount == 0;
    }

    /**
     * Checks if this period is positive, including zero.
     * <p>
     * Periods are allowed to be negative, so this method checks if this period is positive.
     *
     * @return true if this period is positive or zero
     */
    public boolean isPositive() {
        // TODO not inc zero
        return amount >= 0;
    }

    /**
     * Checks if this period is negative, excluding zero.
     * <p>
     * Periods are allowed to be negative, so this method checks if this period is negative.
     *
     * @return true if this period is negative
     */
    public boolean isNegative() {
        return amount < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of this period.
     * <p>
     * For example, in the period '5 Days', the amount is '5'.
     *
     * @return the amount of time of this period, may be negative
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Gets the amount of this period, converted to an {@code int}.
     * <p>
     * For example, in the period '5 Days', the amount is '5'.
     *
     * @return the amount of time of this period, may be negative
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
     * @param amount  the amount of time to set in the returned period, may be negative
     * @return a new period with the specified amount, never null
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
     * @param unit  the unit to set in the returned period, may be negative
     * @return a new period with the specified rule, never null
     */
    public PeriodField withRule(PeriodUnit unit) {
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
     * @param period  the period to add, may be negative
     * @return the new period with the specified period added, never null
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
     * @param amount  the period to add, measured in the unit of the period, may be negative
     * @return the new period with the specified amount added, never null
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
     * @param period  the period to subtract, may be negative
     * @return the new period with the specified period subtracted, never null
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
     * @param amount  the period to subtract, measured in the unit of the period, may be negative
     * @return the new period with the specified amount subtracted, never null
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
     * @param scalar  the value to multiply by, may be negative
     * @return the new period multiplied by the specified scalar, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField multipliedBy(long scalar) {
        return withAmount(MathUtils.safeMultiply(amount, scalar));
    }

    /**
     * Returns a copy of this period with the amount divided by the specified divisor.
     * The calculation uses integer division, thus 3 divided by 2 is 1.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param divisor  the value to divide by, may be negative
     * @return the new period divided by the specified divisor, never null
     * @throws ArithmeticException if the divisor is zero
     */
    public PeriodField dividedBy(long divisor) {
        return withAmount(amount / divisor);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the amount negated.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return the new period with the amount negated, never null
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
     * @return the new period with absolute amount, never null
     * @throws ArithmeticException if the amount is {@code Long.MIN_VALUE}
     */
    public PeriodField abs() {
        return isNegative() ? negated() : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this period to the specified unit.
     * <p>
     * This converts this period to one measured in the specified unit.
     * This uses {@link PeriodUnit#getEquivalentPeriod(PeriodUnit)} to lookup
     * the equivalent period for the unit.
     * <p>
     * For example, '3 Hours' could be converted to '180 Minutes' assuming the
     * 'Hours' unit has an equivalent of '60 Minutes'.
     *
     * @return the equivalent period, null if no equivalent period
     * @throws CalendricalException if this period cannot be converted to the specified unit
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField toEquivalentPeriod(PeriodUnit requiredUnit) {
        PeriodField equivalent = unit.getEquivalentPeriod(requiredUnit);
        if (equivalent != null) {
            return equivalent.multipliedBy(amount);
        }
        throw new CalendricalException("Unable to convert '" + this + "' to " + requiredUnit );
    }

    /**
     * Converts this period to one of the units specified.
     * <p>
     * This will attempt to convert this period to each of the specified units
     * in turn. It is recommended to specify the units from largest to smallest.
     * If this period is already one of the specified units, then {@code this}
     * is returned.
     * <p>
     * For example, '3 Hours' can normally be converted to both minutes and seconds.
     * If the units array contains both 'Minutes' and 'Seconds', then the result will
     * be measured in whichever is first in the array.
     *
     * @param requiredUnits  the required unit array, not altered, not null
     * @return the converted period, never null
     * @throws CalendricalException if this period cannot be converted to any of the units
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField toEquivalentPeriod(PeriodUnit... requiredUnits) {
        for (PeriodUnit requiredUnit : requiredUnits) {
            PeriodField equivalent = unit.getEquivalentPeriod(requiredUnit);
            if (equivalent != null) {
                return equivalent.multipliedBy(amount);
            }
        }
        throw new CalendricalException("Unable to convert '" + this + "' to any requested unit: " + Arrays.toString(requiredUnits));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this period to an estimated duration.
     * <p>
     * Each {@link PeriodUnit} contains an estimated duration for that unit.
     * This method uses that estimate to calculate an estimated duration for
     * this period.
     *
     * @return the estimated duration of this period, may be negative
     * @throws ArithmeticException if the calculation overflows
     */
    public Duration toEstimatedDuration() {
        return unit.getEstimatedDuration().multipliedBy(amount);
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
     * Compares this period to another.
     * <p>
     * The comparison orders first by the unit, then by the amount.
     *
     * @param otherPeriod  the other period to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     * @throws NullPointerException if otherPeriod is null
     */
    public int compareTo(PeriodField otherPeriod) {
        int cmp = unit.compareTo(otherPeriod.unit);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(amount, otherPeriod.amount);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instance equal to the object specified.
     * <p>
     * Two {@code PeriodField} instances are equal if the unit and amount are equal.
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
     * Returns a string representation of the period, such as '6 Days'.
     *
     * @return a descriptive representation of the period, not null
     */
    @Override
    public String toString() {
        return amount + " " + unit.getName();
    }

}
