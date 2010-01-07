/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.period.PeriodUnits.DAYS;
import static javax.time.period.PeriodUnits.HOURS;
import static javax.time.period.PeriodUnits.MINUTES;
import static javax.time.period.PeriodUnits.MONTHS;
import static javax.time.period.PeriodUnits.NANOS;
import static javax.time.period.PeriodUnits.SECONDS;
import static javax.time.period.PeriodUnits.YEARS;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

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
 * PeriodFields is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class PeriodFields
        implements PeriodProvider, Iterable<PeriodUnit>, Serializable {

    /**
     * A constant for a period of zero.
     */
    public static final PeriodFields ZERO = new PeriodFields(new TreeMap<PeriodUnit, Long>());
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 986187548716897689L;

    /**
     * The map of period fields.
     */
    private final TreeMap<PeriodUnit, Long> unitAmountMap;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>PeriodFields</code> from a single unit-amount pair.
     *
     * @param amount  the amount of the specified unit
     * @param unit  the period unit, not null
     * @return the created period instance, never null
     * @throws NullPointerException if the period unit is null
     */
    public static PeriodFields periodFields(long amount, PeriodUnit unit) {
        checkNotNull(unit, "PeriodUnit must not be null");
        TreeMap<PeriodUnit, Long> internalMap = createMap();
        internalMap.put(unit, amount);
        return create(internalMap);
    }

    /**
     * Obtains an instance of <code>PeriodFields</code> from a set of unit-amount pairs.
     * <p>
     * The amount to store for each unit is obtained by calling {@link Number#longValue()}.
     * This will lose any decimal places for instances of <code>Double</code> and <code>Float</code>.
     * It may also silently lose precision for instances of <code>BigInteger</code> or <code>BigDecimal</code>.
     *
     * @param unitAmountMap  a map of periods that will be used to create this
     *  period, not updated by this method, not null, contains no nulls
     * @return the created period instance, never null
     * @throws NullPointerException if the map is null or contains nulls
     */
    public static PeriodFields periodFields(Map<PeriodUnit, ? extends Number> unitAmountMap) {
        checkNotNull(unitAmountMap, "Unit-amount map must not be null");
        if (unitAmountMap.isEmpty()) {
            return ZERO;
        }
        // don't use contains() as tree map and others can throw NPE
        TreeMap<PeriodUnit, Long> internalMap = createMap();
        for (Entry<PeriodUnit, ? extends Number> entry : unitAmountMap.entrySet()) {
            PeriodUnit fieldRule = entry.getKey();
            Number value = entry.getValue();
            checkNotNull(fieldRule, "Null keys are not permitted in unit-amount map");
            checkNotNull(value, "Null values are not permitted in unit-amount map");
            internalMap.put(fieldRule, (value instanceof Long ? (Long) value : value.longValue()));
        }
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
        Period period = Period.period(periodProvider);
        if (period.isZero()) {
            return ZERO;
        }
        TreeMap<PeriodUnit, Long> map = createMap();
        if (period.getYears() != 0) {
            map.put(YEARS, Long.valueOf(period.getYears()));
        }
        if (period.getMonths() != 0) {
            map.put(MONTHS, Long.valueOf(period.getMonths()));
        }
        if (period.getDays() != 0) {
            map.put(DAYS, Long.valueOf(period.getDays()));
        }
        if (period.getHours() != 0) {
            map.put(HOURS, Long.valueOf(period.getHours()));
        }
        if (period.getMinutes() != 0) {
            map.put(MINUTES, Long.valueOf(period.getMinutes()));
        }
        if (period.getSeconds() != 0) {
            map.put(SECONDS, Long.valueOf(period.getSeconds()));
        }
        if (period.getNanos() != 0) {
            map.put(NANOS, Long.valueOf(period.getNanos()));
        }
        return create(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a new empty map.
     *
     * @return ordered representation of internal map
     */
    private static TreeMap<PeriodUnit, Long> createMap() {
        return new TreeMap<PeriodUnit, Long>(Collections.reverseOrder());
    }

    /**
     * Internal factory to create an instance using a pre-built map.
     * The map must not be used by the calling code after calling the constructor.
     *
     * @param periodMap  the map of periods to represent, not null, assigned not cloned
     * @return the created period, never null
     */
    private static PeriodFields create(TreeMap<PeriodUnit, Long> periodMap) {
        if (periodMap.isEmpty()) {
            return ZERO;
        }
        return new PeriodFields(periodMap);
    }

    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using a pre-built map.
     * The map must not be used by the calling code after calling the constructor.
     *
     * @param periodMap  the map of periods to represent, not null and safe to assign
     */
    private PeriodFields(TreeMap<PeriodUnit, Long> periodMap) {
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
        for (Long value : unitAmountMap.values()) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
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
     * This method fulfills the {@link Iterable} interface and allows looping
     * around the fields using the for-each loop. The values can be obtained
     * using {@link #get(PeriodUnit)}.
     *
     * @return an iterator over the fields in this object, never null
     */
    public Iterator<PeriodUnit> iterator() {
        return unitAmountMap.keySet().iterator();
    }

    /**
     * Checks whether this period contains an amount for the unit.
     *
     * @param unit  the unit to query, null returns false
     * @return true if the map contains an amount for the unit
     */
    public boolean contains(PeriodUnit unit) {
        return unitAmountMap.containsKey(unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of the period for the specified unit, throwing an
     * exception if this period does have an amount for the unit.
     *
     * @param unit  the unit to query, not null
     * @return the period amount
     * @throws NullPointerException if the period unit is null
     * @throws CalendricalException if there is no amount for the unit
     */
    public long get(PeriodUnit unit) {
        checkNotNull(unit, "PeriodUnit must not be null");
        Long amount = unitAmountMap.get(unit);
        if (amount == null) {
            throw new CalendricalException("No amount for unit: " + unit);
        }
        return amount;
    }

    /**
     * Gets the amount of the period for the specified unit, throwing an
     * exception if this period does have an amount for the unit.
     * <p>
     * The amount is safely converted to an <code>int</code>.
     *
     * @param unit  the unit to query, not null
     * @return the period amount
     * @throws NullPointerException if the period unit is null
     * @throws CalendricalException if there is no amount for the unit
     * @throws ArithmeticException if the amount is too large to be returned in an int
     */
    public int getInt(PeriodUnit unit) {
        return MathUtils.safeToInt(get(unit));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the amount of the period for the specified unit, returning
     * the default value if this period does have an amount for the unit.
     *
     * @param unit  the unit to query, not null
     * @param defaultValue  the default value to return if the unit is not present
     * @return the period amount
     * @throws NullPointerException if the period unit is null
     */
    public long get(PeriodUnit unit, long defaultValue) {
        checkNotNull(unit, "PeriodUnit must not be null");
        Long amount = unitAmountMap.get(unit);
        return amount == null ? defaultValue : amount;
    }

    /**
     * Gets the amount of the period for the specified unit, returning
     * the default value if this period does have an amount for the unit.
     * <p>
     * The amount is safely converted to an <code>int</code>.
     *
     * @param unit  the unit to query, not null
     * @param defaultValue  the default value to return if the unit is not present
     * @return the period amount
     * @throws NullPointerException if the period unit is null
     * @throws ArithmeticException if the amount is too large to be returned in an int
     */
    public int getInt(PeriodUnit unit, int defaultValue) {
        return MathUtils.safeToInt(get(unit, defaultValue));
    }

    /**
     * Gets the amount of the period for the specified unit quietly returning
     * null if this period does have an amount for the unit.
     *
     * @param unit  the unit to query, null returns null
     * @return the period amount, null if unit not present
     */
    public Long getQuiet(PeriodUnit unit) {
        return unit == null ? null : unitAmountMap.get(unit);
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
        TreeMap<PeriodUnit, Long> copy = clonedMap();
        copy.values().removeAll(Collections.singleton(Long.valueOf(0)));
        return create(copy);
    }

    //-----------------------------------------------------------------------
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
    public PeriodFields with(long amount, PeriodUnit unit) {
        Long existing = getQuiet(unit);
        if (existing != null && existing == amount) {
            return this;
        }
        TreeMap<PeriodUnit, Long> copy = clonedMap();
        copy.put(unit, amount);
        return create(copy);
    }

//    /**
//     * Returns a copy of this period with the amounts from the specified map added.
//     * <p>
//     * If this instance already has an amount for any unit then the value is replaced.
//     * Otherwise the value is added.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param unitAmountMap  the new map of fields, not null
//     * @return a new updated period instance, never null
//     * @throws NullPointerException if the map contains null keys or values
//     */
//    public PeriodFields with(Map<PeriodUnit, Long> unitAmountMap) {
//        checkNotNull(unitAmountMap, "The field-value map must not be null");
//        if (unitAmountMap.isEmpty()) {
//            return this;
//        }
//        // don't use contains() as tree map and others can throw NPE
//        TreeMap<PeriodUnit, Long> clonedMap = clonedMap();
//        for (Entry<PeriodUnit, Long> entry : unitAmountMap.entrySet()) {
//            PeriodUnit unit = entry.getKey();
//            Long value = entry.getValue();
//            checkNotNull(unit, "Null keys are not permitted in field-value map");
//            checkNotNull(value, "Null values are not permitted in field-value map");
//            clonedMap.put(unit, value);
//        }
//        return new PeriodFields(clonedMap);
//    }

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
        TreeMap<PeriodUnit, Long> copy = clonedMap();
        copy.putAll(period.unitAmountMap);
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
        checkNotNull(unit, "PeriodUnit must not be null");
        if (unitAmountMap.containsKey(unit) == false) {
            return this;
        }
        TreeMap<PeriodUnit, Long> copy = clonedMap();
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
        checkNotNull(period, "PeriodFields must not be null");
        if (this == ZERO) {
            return period;
        }
        if (period.isZero()) {
            return this;
        }
        TreeMap<PeriodUnit, Long> copy = clonedMap();
        for (PeriodUnit unit : period) {
            long amountToAdd = period.unitAmountMap.get(unit);
            long current = this.get(unit, 0);
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
        checkNotNull(period, "PeriodFields must not be null");
        if (this == ZERO) {
            return period;
        }
        if (period.isZero()) {
            return this;
        }
        TreeMap<PeriodUnit, Long> copy = clonedMap();
        for (PeriodUnit unit : period) {
            long amountToSubtract = period.unitAmountMap.get(unit);
            long current = this.get(unit, 0);
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
        TreeMap<PeriodUnit, Long> copy = clonedMap();
        for (PeriodUnit unit : this) {
            long current = unitAmountMap.get(unit);
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
        TreeMap<PeriodUnit, Long> copy = clonedMap();
        for (PeriodUnit unit : this) {
            long current = unitAmountMap.get(unit);
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
    private TreeMap<PeriodUnit, Long> clonedMap() {
        return (TreeMap) unitAmountMap.clone();
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a map of units to amounts.
     * <p>
     * The returned map will never be null, however it may be empty.
     * It is sorted by the unit, returning the largest first.
     * It is independent of this object - changes will not be reflected back.
     *
     * @return the independent, modifiable map of period amounts, never null, never contains null
     */
    public SortedMap<PeriodUnit, Long> toUnitAmountMap() {
        return clonedMap();
    }

    /**
     * Converts this object to a <code>Period</code>.
     * <p>
     * Conversion is only possible if this period only contains the six units
     * which can be stored in a <code>Period</code>.
     *
     * @return the equivalent period instance, never null
     * @throws CalendricalException if this period cannot be converted
     */
    public Period toPeriod() {
        if (isZero()) {
            return Period.ZERO;
        }
        Map<PeriodUnit, Long> copy = clonedMap();
        Long years = copy.remove(YEARS);
        Long months = copy.remove(MONTHS);
        Long days = copy.remove(DAYS);
        Long hours = copy.remove(HOURS);
        Long minutes = copy.remove(MINUTES);
        Long seconds = copy.remove(SECONDS);
        Long nanos = copy.remove(NANOS);
        if (copy.size() > 0) {
            throw new CalendarConversionException(
                    "Unable to convert to a Period as the following fields are incompatible: " + copy.keySet());
        }
        return Period.period(
                    years == null ? 0 : MathUtils.safeToInt(years),
                    months == null ? 0 : MathUtils.safeToInt(months),
                    days == null ? 0 : MathUtils.safeToInt(days),
                    hours == null ? 0 : MathUtils.safeToInt(hours),
                    minutes == null ? 0 : MathUtils.safeToInt(minutes),
                    seconds == null ? 0 : MathUtils.safeToInt(seconds),
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
