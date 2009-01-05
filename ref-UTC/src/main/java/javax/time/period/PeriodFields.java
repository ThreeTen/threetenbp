/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.period.PeriodUnits.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.time.CalendricalException;
import javax.time.MathUtils;
import javax.time.calendar.CalendarConversionException;

/**
 * An immutable period formed by the storage of a map of unit-amount pairs.
 * <p>
 * As an example, the period "3 months, 4 days and 7 hours" can be stored.
 * <p>
 * A value of zero can also be stored for any unit. This means that a
 * period of zero hours is not equal to a period of zero minutes.
 * The {@link #withZeroesRemoved()} method removes zero values.
 * <p>
 * PeriodFields can store units of any kind which makes it usable with any calendar system.
 * <p>
 * PeriodFields is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class PeriodFields
        implements PeriodProvider, Iterable<PeriodUnit>, Serializable {
    // TODO: Maybe hold Long/BigDecimal/Number internally

    /**
     * A constant for a period of zero.
     */
    public static final PeriodFields ZERO = new PeriodFields(new TreeMap<PeriodUnit, Integer>());
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 986187548716897689L;

    /**
     * The map of period fields.
     */
    private final TreeMap<PeriodUnit, Integer> unitAmountMap;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>PeriodFields</code> from a single unit-amount pair.
     *
     * @param amount  the amount of the specified unit
     * @param unit  the period unit, not null
     * @return the created period instance, never null
     * @throws NullPointerException if the period unit is null
     */
    public static PeriodFields periodFields(int amount, PeriodUnit unit) {
        if (unit == null) {
            throw new NullPointerException("Period unit must not be null");
        }
        TreeMap<PeriodUnit, Integer> internalMap = new TreeMap<PeriodUnit, Integer>(Collections.reverseOrder());
        internalMap.put(unit, amount);
        return create(internalMap);
    }

    /**
     * Obtains an instance of <code>PeriodFields</code> from a set of unit-amount pairs.
     *
     * @param unitAmountMap  a map of periods that will be used to create this
     *  period, not updated by this method, not null, contains no nulls
     * @return the created period instance, never null
     * @throws NullPointerException if the map is null or contains nulls
     */
    public static PeriodFields periodFields(Map<PeriodUnit, Integer> unitAmountMap) {
        if (unitAmountMap == null) {
            throw new NullPointerException("Period map must not be null");
        }
        if (unitAmountMap.isEmpty()) {
            return ZERO;
        }
        if (unitAmountMap.containsKey(null) || unitAmountMap.containsValue(null)) {
            throw new NullPointerException("Period map must not contain null");
        }
        TreeMap<PeriodUnit, Integer> internalMap = new TreeMap<PeriodUnit, Integer>(Collections.reverseOrder());
        internalMap.putAll(unitAmountMap);
        return create(internalMap);
    }

    /**
     * Obtains an instance of <code>PeriodFields</code> from a <code>PeriodProvider</code>.
     * <p>
     * The created instance will only contain the non-zero fields of specified provider.
     *
     * @param periodProvider  the provider to create from, not null
     * @return the created period instance, never null
     * @throws NullPointerException if the period provider is null
     */
    public static PeriodFields periodFields(PeriodProvider periodProvider) {
        //TODO: this probably is not the right thing to do
        Period period = Period.period(periodProvider).normalized();
        if (period.isZero()) {
            return ZERO;
        }
        TreeMap<PeriodUnit, Integer> map = new TreeMap<PeriodUnit, Integer>(Collections.reverseOrder());
        if (period.getYears() != 0) {
            map.put(YEARS, period.getYears());
        }
        if (period.getMonths() != 0) {
            map.put(MONTHS, period.getMonths());
        }
        if (period.getDays() != 0) {
            map.put(DAYS, period.getDays());
        }
        if (period.getHours() != 0) {
            map.put(HOURS, period.getHours());
        }
        if (period.getMinutes() != 0) {
            map.put(MINUTES, period.getMinutes());
        }
        if (period.getSeconds() != 0) {
            map.put(SECONDS, period.getSeconds());
        }
        
        //TODO: this probably is not the right thing to do
        if (period.getNanos() != 0) {
            map.put(NANOS, MathUtils.safeToInt(period.getNanos()));
        }
        return PeriodFields.create(map);
    }
    //-----------------------------------------------------------------------
    /**
     * Internal factory to create an instance using a pre-built map.
     * The map must not be used by the calling code after calling the constructor.
     *
     * @param periodMap  the map of periods to represent, not null, assigned not cloned
     * @return the created period, never null
     */
    static PeriodFields create(TreeMap<PeriodUnit, Integer> periodMap) {
        if (periodMap.isEmpty()) {
            return ZERO;
        }
        return new PeriodFields(periodMap);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a pre-built map.
     * The map must not be used by the calling code after calling the constructor.
     *
     * @param periodMap  the map of periods to represent, not null and safe to assign
     */
    private PeriodFields(TreeMap<PeriodUnit, Integer> periodMap) {
        this.unitAmountMap = periodMap;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        if (unitAmountMap.size() == 0) {
            return ZERO;
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the period is zero-length.
     *
     * @return true if this period is zero-length
     */
    public boolean isZero() {
        if (this == ZERO) {
            return true;
        }
        for (Integer value : unitAmountMap.values()) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of the period for the specified unit, returning
     * null if this period does have an amount for the unit.
     *
     * @param unit  the unit to query, not null
     * @return the period amount
     * @throws NullPointerException if the period unit is null
     * @throws CalendricalException if there is no amount for the unit
     */
    public int getAmount(PeriodUnit unit) {
        if (unit == null) {
            throw new NullPointerException("Period unit must not be null");
        }
        Integer amount = unitAmountMap.get(unit);
        if (amount == null) {
            throw new CalendricalException("No amount for unit: " + unit);
        }
        return amount;
    }

    /**
     * Gets the amount of the period for the specified unit, returning
     * null if this period does have an amount for the unit.
     *
     * @param unit  the unit to query, not null
     * @param defaultValue  the default value to return if the unit is not present
     * @return the period amount
     * @throws NullPointerException if the period unit is null
     */
    public int getAmount(PeriodUnit unit, int defaultValue) {
        if (unit == null) {
            throw new NullPointerException("Period unit must not be null");
        }
        Integer amount = unitAmountMap.get(unit);
        return amount == null ? defaultValue : amount;
    }

    /**
     * Gets the amount of the period for the specified unit, returning
     * zero if this period does have an amount for the unit.
     *
     * @param unit  the unit to query, null returns null
     * @return the period amount, null if unit not present
     */
    public Integer getAmountQuiet(PeriodUnit unit) {
        return unit == null ? null : unitAmountMap.get(unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this period contains an amount for the unit.
     *
     * @param unit  the unit to query, null returns false
     * @return true if the map contains an amount for the unit
     */
    public boolean contains(PeriodUnit unit) {
        return unitAmountMap.containsKey(unit);
    }

    /**
     * Returns the size of the set of unit-amount pairs.
     *
     * @return number of unit-amount pairs
     */
    public int size() {
        return unitAmountMap.size();
    }

    /**
     * Iterates through all the units in the period.
     * <p>
     * This method fulfuls the {@link Iterable} interface and allows looping
     * around the fields using the for-each loop. The values can be obtained
     * using {@link #getAmount(PeriodUnit)}.
     *
     * @return an iterator over the fields in this object, never null
     */
    public Iterator<PeriodUnit> iterator() {
        return unitAmountMap.keySet().iterator();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with all zero amounts removed.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated period instance, never null
     */
    public PeriodFields withZeroesRemoved() {
        if (isZero()) {
            return ZERO;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        copy.values().removeAll(Collections.singleton(Integer.valueOf(0)));
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified values altered.
     * <p>
     * This method operates on each unit in the input in turn.
     * If this period already contains an amount for the unit then the amount
     * is replaced. Otherwise, the unit-amount pair is added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period field to update, not null
     * @return a new updated period instance, never null
     * @throws NullPointerException if the period is null
     */
    public PeriodFields with(PeriodFields period) {
        if (this == ZERO) {
            return period;
        }
        if (period == ZERO) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        copy.putAll(period.unitAmountMap);
        return create(copy);
    }

    /**
     * Returns a copy of this period with the specified amount for the unit.
     * <p>
     * If this period already contains an amount for the unit then the amount
     * is replaced. Otherwise, the unit-amount pair is added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to update the new instance with
     * @param unit  the unit to update, not null
     * @return a new updated period instance, never null
     * @throws NullPointerException if the period unit is null
     */
    public PeriodFields with(int amount, PeriodUnit unit) {
        Integer existing = getAmountQuiet(unit);
        if (existing != null && existing == amount) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        copy.put(unit, amount);
        return create(copy);
    }

    /**
     * Returns a copy of this period with the specified unit removed.
     * <p>
     * If this period already contains an amount for the unit then the amount
     * is removed. Otherwise, no action occurs.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param unit  the unit to remove, not null
     * @return a new updated period instance, never null
     * @throws NullPointerException if the period unit is null
     */
    public PeriodFields withUnitRemoved(PeriodUnit unit) {
        if (unit == null) {
            throw new NullPointerException("Period unit must not be null");
        }
        if (unitAmountMap.containsKey(unit) == false) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        copy.remove(unit);
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * This method operates on each unit in the input in turn.
     * If this period does not contain an amount for the unit then the amount
     * to be added to is treated as zero. The result will have the union of
     * the units in this instance and the units in the specified instance.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated period instance, never null
     * @throws NullPointerException if the period to add is null
     */
    public PeriodFields plus(PeriodFields period) {
        if (period == null) {
            throw new NullPointerException("Period must not be null");
        }
        if (this == ZERO) {
            return period;
        }
        if (period.isZero()) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        for (PeriodUnit unit : period) {
            int amountToAdd = period.unitAmountMap.get(unit);
            int current = this.getAmount(unit, 0);
            copy.put(unit, MathUtils.safeAdd(current, amountToAdd));
        }
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * This method operates on each unit in the input in turn.
     * If this period does not contain an amount for the unit then the amount
     * to be subtracted from is treated as zero. The result will have the union of
     * the units in this instance and the units in the specified instance.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, not null
     * @return a new updated period instance, never null
     * @throws NullPointerException if the period to subtract is null
     */
    public PeriodFields minus(PeriodFields period) {
        if (period == null) {
            throw new NullPointerException("Period must not be null");
        }
        if (this == ZERO) {
            return period;
        }
        if (period.isZero()) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        for (PeriodUnit unit : period) {
            int amountToSubtract = period.unitAmountMap.get(unit);
            int current = this.getAmount(unit, 0);
            copy.put(unit, MathUtils.safeSubtract(current, amountToSubtract));
        }
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with each amount in this period multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation result overflows
     */
    public PeriodFields multipliedBy(int scalar) {
        if (scalar == 1 || isZero()) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        for (PeriodUnit unit : this) {
            int current = unitAmountMap.get(unit);
            copy.put(unit, MathUtils.safeMultiply(current, scalar));
        }
        return create(copy);
    }

    /**
     * Returns a new instance with each amount in this period divided
     * by the specified value.
     *
     * @param divisor  the value to divide by, not null, not zero
     * @return a new updated period instance, never null
     * @throws ArithmeticException if dividing by zero
     */
    public PeriodFields dividedBy(int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        if (divisor == 1 || isZero()) {
            return this;
        }
        TreeMap<PeriodUnit, Integer> copy = cloneMap();
        for (PeriodUnit unit : this) {
            int current = unitAmountMap.get(unit);
            copy.put(unit, current / divisor);
        }
        return create(copy);
    }

    /**
     * Returns a new instance with each amount in this period negated.
     *
     * @return a new updated period instance, never null
     */
    public PeriodFields negated() {
        return multipliedBy(-1);
    }

    //-----------------------------------------------------------------------
    /**
     * Clone the internal data storage map.
     *
     * @return the cloned map, never null
     */
    @SuppressWarnings("unchecked")
    private TreeMap<PeriodUnit, Integer> cloneMap() {
        return (TreeMap) unitAmountMap.clone();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unit-amount map which defines the period.
     * The map is sorted by the unit, returning the largest first.
     *
     * @return the map of period amounts, never null, never contains null or zero
     */
    public SortedMap<PeriodUnit, Integer> toUnitAmountMap() {
        return Collections.unmodifiableSortedMap(unitAmountMap);
    }

    /**
     * Converts this object to a <code>Period</code>.
     * <p>
     * Conversion is only possible if this period only contains the six units
     * which can be stored in a <code>Period</code>.
     *
     * @return the equivalent period instance, never null
     * @throws CalendarConversionException if this period cannot be converted
     */
    public Period toPeriod() {
        if (isZero()) {
            return Period.ZERO;
        }
        Map<PeriodUnit, Integer> copy = cloneMap();
        Integer years = copy.remove(YEARS);
        Integer months = copy.remove(MONTHS);
        Integer days = copy.remove(DAYS);
        Integer hours = copy.remove(HOURS);
        Integer minutes = copy.remove(MINUTES);
        Integer seconds = copy.remove(SECONDS);
        Integer nanos = copy.remove(NANOS);
        if (copy.size() > 0) {
            throw new CalendarConversionException("Unable to convert to a Period as the following fields are incompatible: " + copy);
        }
        return Period.period(
                    years == null ? 0 : years,
                    months == null ? 0 : months,
                    days == null ? 0 : days,
                    hours == null ? 0 : hours,
                    minutes == null ? 0 : minutes,
                    seconds == null ? 0 : seconds,
                    nanos == null ? 0 : nanos);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this period equal to the specified period.
     *
     * @param obj  the other period to compare to, null returns false
     * @return true if this instance is equal to the specified period
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PeriodFields) {
            PeriodFields other = (PeriodFields) obj;
            return unitAmountMap.equals(other.unitAmountMap);
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
        return unitAmountMap.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the period.
     *
     * @return the unit-amount map, never null
     */
    @Override
    public String toString() {
        return unitAmountMap.toString();
    }

}
