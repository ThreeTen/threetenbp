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
package javax.time.calendar;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.time.CalendricalException;
import javax.time.Duration;

/**
 * A period of time measured using a number of different units,
 * such as '3 Months, 4 Days and 7 Hours'.
 * <p>
 * {@code PeriodFields} is an immutable period that stores an amount of human-scale
 * time for a number of units. For example, humans typically measure periods of time
 * in units of years, months, days, hours, minutes and seconds. These concepts are
 * defined by instances of {@link PeriodUnit} in the chronology classes. This class
 * allows an amount to be specified for a number of the units, such as '3 Days and 65 Seconds'.
 * <p>
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * A value of zero can also be stored for any unit. This means that a
 * period of zero hours is not equal to a period of zero minutes.
 * However, an empty instance constant exists to represent zero irrespective of unit.
 * The {@link #withZeroesRemoved()} method removes zero values.
 * <p>
 * {@code PeriodFields} can store units of any kind which makes it usable with
 * any calendar system.
 * <p>
 * PeriodFields is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class PeriodFields
        implements PeriodProvider, Iterable<PeriodField>, Serializable {

    /**
     * A constant for a period of zero.
     * This constant is independent of any unit.
     */
    public static final PeriodFields ZERO = new PeriodFields(new TreeMap<PeriodUnit, PeriodField>());
    /**
     * The serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The map of periods.
     */
    private final TreeMap<PeriodUnit, PeriodField> unitFieldMap;

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code PeriodFields} from an amount and unit.
     * <p>
     * The parameters represent the two parts of a phrase like '6 Days'.
     *
     * @param amount  the amount of create with, positive or negative
     * @param unit  the period unit, not null
     * @return the {@code PeriodFields} instance, never null
     */
    public static PeriodFields of(long amount, PeriodUnit unit) {
        checkNotNull(unit, "PeriodUnit must not be null");
        TreeMap<PeriodUnit, PeriodField> internalMap = createMap();
        internalMap.put(unit, PeriodField.of(amount, unit));
        return create(internalMap);
    }

    /**
     * Obtains a {@code PeriodFields} from a single-unit period.
     *
     * @param period  the single-unit period, not null
     * @return the {@code PeriodFields} instance, never null
     */
    public static PeriodFields of(PeriodField period) {
        checkNotNull(period, "PeriodField must not be null");
        TreeMap<PeriodUnit, PeriodField> internalMap = createMap();
        internalMap.put(period.getUnit(), period);
        return create(internalMap);
    }

    /**
     * Obtains a {@code PeriodFields} from an array of single-unit periods.
     * <p>
     * The period fields must all have different units.
     *
     * @param periods  the array of single-unit periods, not null
     * @return the {@code PeriodFields} instance, never null
     * @throws IllegalArgumentException if the same period unit occurs twice
     */
    public static PeriodFields of(PeriodField... periods) {
        checkNotNull(periods, "PeriodField array must not be null");
        TreeMap<PeriodUnit, PeriodField> internalMap = createMap();
        for (PeriodField period : periods) {
            checkNotNull(period, "PeriodField array must not contain null");
            if (internalMap.put(period.getUnit(), period) != null) {
                throw new IllegalArgumentException("PeriodField array contains the same unit twice");
            }
        }
        return create(internalMap);
    }

//    /**
//     * Obtains a {@code PeriodFields} from an array of single-unit periods.
//     *
//     * @param periods  the array of single-unit periods, not null
//     * @return the {@code PeriodFields} instance, never null
//     * @throws IllegalArgumentException if the same period unit occurs twice
//     */
//    public static PeriodFields of(Iterable<PeriodField> periods) {
//        checkNotNull(periods, "Iterable must not be null");
//        TreeMap<PeriodUnit, PeriodField> internalMap = createMap();
//        for (PeriodField period : periods) {
//            checkNotNull(period, "Iterable must not contain null");
//            if (internalMap.put(period.getUnit(), period) != null) {
//                throw new IllegalArgumentException("Iterable contains the same unit twice");
//            }
//        }
//        return create(internalMap);
//    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code PeriodFields} from a {@code PeriodProvider}.
     * <p>
     * This method provides null-checking around {@link PeriodProvider#toPeriodFields()}.
     *
     * @param periodProvider  the provider to create from, not null
     * @return the {@code PeriodFields} instance, never null
     * @throws NullPointerException if the period provider is null or returns null
     */
    public static PeriodFields of(PeriodProvider periodProvider) {
        checkNotNull(periodProvider, "PeriodProvider must not be null");
        PeriodFields result = periodProvider.toPeriodFields();
        checkNotNull(result, "PeriodProvider implementation must not return null");
        return result;
    }

    /**
     * Obtains a {@code PeriodFields} by totalling the amounts in a list of
     * {@code PeriodProvider} instances.
     * <p>
     * This method returns a period with all the unit-amount pairs from the providers
     * totalled. Thus a period of '2 Months and 5 Days' combined with a period of
     * '7 Days and 21 Hours' will yield a result of '2 Months, 12 Days and 21 Hours'.
     *
     * @param periodProviders  the providers to total, not null
     * @return the {@code PeriodFields} instance, never null
     * @throws NullPointerException if any period provider is null or returns null
     */
    public static PeriodFields ofTotal(PeriodProvider... periodProviders) {
        checkNotNull(periodProviders, "PeriodProvider[] must not be null");
        if (periodProviders.length == 1) {
            return of(periodProviders[0]);
        }
        TreeMap<PeriodUnit, PeriodField> map = createMap();
        for (PeriodProvider periodProvider : periodProviders) {
            PeriodFields periods = of(periodProvider);
            for (PeriodField period : periods.unitFieldMap.values()) {
                PeriodField old = map.get(period.getUnit());
                period = (old != null ? old.plus(period) : period);
                map.put(period.getUnit(), period);
            }
        }
        return create(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code PeriodFields} from a {@code Duration} based on the standard
     * durations of seconds and nanoseconds.
     * <p>
     * The conversion will create an instance with two units - the {@code ISOChronology}
     * seconds and nanoseconds units. This matches the {@link #toDuration()} method.
     *
     * @param duration  the duration to create from, not null
     * @return the {@code PeriodFields} instance, never null
     */
    public static PeriodFields of(Duration duration) {
        checkNotNull(duration, "Duration must not be null");
        TreeMap<PeriodUnit, PeriodField> internalMap = createMap();
        internalMap.put(ISOChronology.periodSeconds(), PeriodField.of(duration.getSeconds(), ISOChronology.periodSeconds()));
        internalMap.put(ISOChronology.periodNanos(), PeriodField.of(duration.getNanoOfSecond(), ISOChronology.periodNanos()));
        return create(internalMap);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a new empty map.
     *
     * @return ordered representation of internal map
     */
    private static TreeMap<PeriodUnit, PeriodField> createMap() {
        return new TreeMap<PeriodUnit, PeriodField>(Collections.reverseOrder());
    }

    /**
     * Internal factory to create an instance using a pre-built map.
     * The map must not be used by the calling code after calling the constructor.
     *
     * @param periodMap  the unit-amount map, not null, assigned not cloned
     * @return the created period, never null
     */
    static PeriodFields create(TreeMap<PeriodUnit, PeriodField> periodMap) {
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
    private PeriodFields(TreeMap<PeriodUnit, PeriodField> periodMap) {
        this.unitFieldMap = periodMap;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        if (unitFieldMap.size() == 0) {
            return ZERO;
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this period is zero-length.
     * <p>
     * This checks whether all the amounts in this period are zero.
     *
     * @return true if this period is zero-length
     */
    public boolean isZero() {
        for (PeriodField field : unitFieldMap.values()) {
            if (field.isZero() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this period is fully positive, excluding zero.
     * <p>
     * This checks whether all the amounts in this period are positive,
     * defined as greater than zero.
     *
     * @return true if this period is fully positive excluding zero
     */
    public boolean isPositive() {
        for (PeriodField field : unitFieldMap.values()) {
            if (field.getAmount() <= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this period is fully positive, including zero.
     * <p>
     * This checks whether all the amounts in this period are positive,
     * defined as greater than or equal to zero.
     *
     * @return true if this period is fully positive including zero
     */
    public boolean isPositiveOrZero() {
        for (PeriodField field : unitFieldMap.values()) {
            if (field.getAmount() < 0) {
                return false;
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the size of the set of units in this period.
     * <p>
     * This returns the number of different units that are stored.
     *
     * @return number of unit-amount pairs
     */
    public int size() {
        return unitFieldMap.size();
    }

    /**
     * Iterates through all the single-unit periods in this period.
     * <p>
     * This method fulfills the {@link Iterable} interface and allows looping
     * around the contained single-unit periods using the for-each loop.
     *
     * @return an iterator over the single-unit periods in this period, never null
     */
    public Iterator<PeriodField> iterator() {
        return unitFieldMap.values().iterator();
    }

    /**
     * Checks whether this period contains an amount for the unit.
     *
     * @param unit  the unit to query, null returns false
     * @return true if the map contains an amount for the unit
     */
    public boolean contains(PeriodUnit unit) {
        return unitFieldMap.containsKey(unit);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period for the specified unit.
     * <p>
     * This method allows the period to be queried by unit, like a map.
     * If the unit is not found then {@code null} is returned.
     *
     * @param unit  the unit to query, not null
     * @return the period, null if no period stored for the unit
     */
    public PeriodField get(PeriodUnit unit) {
        checkNotNull(unit, "PeriodUnit must not be null");
        return unitFieldMap.get(unit);
    }

    /**
     * Gets the amount of this period for the specified unit.
     * <p>
     * This method allows the amount to be queried by unit, like a map.
     * If the unit is not found then zero is returned.
     *
     * @param unit  the unit to query, not null
     * @return the period amount, 0 if no period stored for the unit
     * @throws CalendricalException if there is no amount for the unit
     */
    public long getAmount(PeriodUnit unit) {
        PeriodField field = get(unit);
        if (field == null) {
            return 0;
        }
        return field.getAmount();
    }

    /**
     * Gets the amount of this period for the specified unit converted
     * to an {@code int}.
     * <p>
     * This method allows the amount to be queried by unit, like a map.
     * If the unit is not found then zero is returned.
     *
     * @param unit  the unit to query, not null
     * @return the period amount, 0 if no period stored for the unit
     * @throws CalendricalException if there is no amount for the unit
     * @throws ArithmeticException if the amount is too large to be returned in an int
     */
    public int getAmountInt(PeriodUnit unit) {
        PeriodField field = get(unit);
        if (field == null) {
            return 0;
        }
        return field.getAmountInt();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with all zero amounts removed.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code PeriodFields} based on this period with zero amounts removed, never null
     */
    public PeriodFields withZeroesRemoved() {
        if (isZero()) {
            return ZERO;
        }
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
        for (Iterator<PeriodField> it = copy.values().iterator(); it.hasNext(); ) {
            if (it.next().isZero()) {
                it.remove();
            }
        }
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
     * @param amount  the amount to store in terms of the unit, positive or negative
     * @param unit  the unit to store not null
     * @return a {@code PeriodFields} based on this period with the specified period overlaid, never null
     */
    public PeriodFields with(long amount, PeriodUnit unit) {
        PeriodField existing = get(unit);
        if (existing != null && existing.getAmount() == amount) {
            return this;
        }
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
        copy.put(unit, PeriodField.of(amount, unit));
        return create(copy);
    }

    /**
     * Returns a copy of this period with the specified values altered.
     * <p>
     * This method operates on each unit in the input in turn.
     * If this period already contains an amount for the unit then the amount
     * is replaced. Otherwise, the unit-amount pair is added.
     * <p>
     * For example, '6 Years, 7 Months' with '2 Months 3 Days' will return
     * '6 Years, 2 Months, 3 Days'.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to merge over this period, not null
     * @return a {@code PeriodFields} based on this period with the specified period overlaid, never null
     */
    public PeriodFields with(PeriodProvider periodProvider) {
        PeriodFields periods = of(periodProvider);
        if (this == ZERO) {
            return periods;
        }
        if (periods == ZERO) {
            return this;
        }
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
        copy.putAll(periods.unitFieldMap);
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
     * @return a {@code PeriodFields} based on this period with the specified unit removed, never null
     */
    public PeriodFields without(PeriodUnit unit) {
        checkNotNull(unit, "PeriodUnit must not be null");
        if (unitFieldMap.containsKey(unit) == false) {
            return this;
        }
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
        copy.remove(unit);
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * The returned period will take each unit in the provider and add the value
     * to the amount already stored in this period, returning a new one.
     * If this period does not contain an amount for the unit then the unit and
     * amount are simply returned directly in the result. The result will have
     * the union of the units in this instance and the units in the specified instance.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a {@code PeriodFields} based on this period with the specified period added, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields plus(PeriodProvider periodProvider) {
        PeriodFields periods = of(periodProvider);
        if (this == ZERO) {
            return periods;
        }
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
        for (PeriodField period : periods.unitFieldMap.values()) {
            PeriodField old = copy.get(period.getUnit());
            period = (old != null ? old.plus(period) : period);
            copy.put(period.getUnit(), period);
        }
        return create(copy);
    }

    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * The result will contain the units and amounts from this period plus the
     * specified unit and amount.
     * The specified unit will always be in the result even if the amount is zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to add, measured in the specified unit, positive or negative
     * @param unit  the unit defining the amount, not null
     * @return a {@code PeriodFields} based on this period with the specified period added, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields plus(long amount, PeriodUnit unit) {
        checkNotNull(unit, "PeiodRule must not be null");
        if (amount == 0 && contains(unit)) {
            return this;
        }
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
        PeriodField old = copy.get(unit);
        PeriodField field = (old != null ? old.plus(amount) : PeriodField.of(amount, unit));
        copy.put(unit, field);
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * The returned period will take each unit in the provider and subtract the
     * value from the amount already stored in this period, returning a new one.
     * If this period does not contain an amount for the unit then the unit and
     * amount are simply returned directly in the result. The result will have
     * the union of the units in this instance and the units in the specified instance.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to subtract, not null
     * @return a {@code PeriodFields} based on this period with the specified period subtracted, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields minus(PeriodProvider periodProvider) {
        PeriodFields periods = of(periodProvider);
        if (this == ZERO) {
            return periods;
        }
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
        for (PeriodField period : periods.unitFieldMap.values()) {
            PeriodField old = copy.get(period.getUnit());
            period = (old != null ? old.minus(period) : period.negated());
            copy.put(period.getUnit(), period);
        }
        return create(copy);
    }

    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * The result will contain the units and amounts from this period minus the
     * specified unit and amount.
     * The specified unit will always be in the result even if the amount is zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to subtract, measured in the specified unit, positive or negative
     * @param unit  the unit defining the amount, not null
     * @return a {@code PeriodFields} based on this period with the specified period subtracted, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields minus(long amount, PeriodUnit unit) {
        checkNotNull(unit, "PeiodRule must not be null");
        if (amount == 0 && contains(unit)) {
            return this;
        }
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
        PeriodField old = copy.get(unit);
        copy.put(unit, old != null ? old.minus(amount) : PeriodField.of(amount, unit).negated());
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with each amount in this period multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return a {@code PeriodFields} based on this period with the amounts multiplied by the scalar, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields multipliedBy(long scalar) {
        if (scalar == 1 || isZero()) {
            return this;
        }
        TreeMap<PeriodUnit, PeriodField> copy = createMap();
        for (PeriodField field : this) {
            copy.put(field.getUnit(), field.multipliedBy(scalar));
        }
        return create(copy);
    }

    /**
     * Returns a copy of this period with each amount in this period divided
     * by the specified value.
     *
     * @param divisor  the value to divide by, not null, not zero
     * @return a {@code PeriodFields} based on this period with the amounts divided by the divisor, never null
     * @throws ArithmeticException if dividing by zero
     */
    public PeriodFields dividedBy(long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        if (divisor == 1 || isZero()) {
            return this;
        }
        TreeMap<PeriodUnit, PeriodField> copy = createMap();
        for (PeriodField field : this) {
            copy.put(field.getUnit(), field.dividedBy(divisor));
        }
        return create(copy);
    }

    /**
     * Returns a copy of this period with each amount in this period negated.
     *
     * @return a {@code PeriodFields} based on this period with the amounts negated, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields negated() {
        return multipliedBy(-1);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified units retained.
     * <p>
     * This method will return a new period that only has the specified units.
     * All units not present in the input will not be present in the result.
     * In most cases, the result will not be equivalent to this period.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param units  the units to retain, not altered, not null, no nulls
     * @return a {@code PeriodFields} based on this period with the specified units retained, never null
     */
    public PeriodFields retain(PeriodUnit... units) {
        checkNotNull(units, "PeriodUnit array must not be null");
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
        List<PeriodUnit> unitList = Arrays.asList(units);
        if (unitList.contains(null)) {
            throw new NullPointerException("PeriodUnit array must not contain null");
        }
        copy.keySet().retainAll(unitList);
        return create(copy);
    }

    /**
     * Returns a copy of this period with only those units that can be converted to
     * the specified units.
     * <p>
     * This method will return a new period where every field can be converted to one
     * of the specified units. In the result, each of the retained periods will have the
     * same amount as they do in this period - no conversion or normalization occurs.
     * <p>
     * For example, if this period is '2 Days, 5 Hours, 7 Minutes' and the specified
     * unit array contains 'Seconds' then the output will be '5 Hours, 7 Minutes'.
     * The 'Days' unit is not retained as it cannot be converted to 'Seconds'.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param units  the units to retain, not altered, not null, no nulls
     * @return a {@code PeriodFields} based on this period with the specified units retained, never null
     */
    public PeriodFields retainConvertible(PeriodUnit... units) {
        checkNotNull(units, "PeriodUnit array must not be null");
        TreeMap<PeriodUnit, PeriodField> copy = clonedMap();
    outer:
        for (Iterator<PeriodUnit> it = copy.keySet().iterator(); it.hasNext(); ) {
            PeriodUnit loopUnit = it.next();
            for (PeriodUnit unit : units) {
                checkNotNull(unit, "PeriodUnit array must not contain null");
                if (loopUnit.isConvertibleTo(unit)) {
                    continue outer;
                }
            }
            it.remove();
        }
        return create(copy);
    }

    /**
     * Returns a copy of this period with the modular division remainder of each field
     * calculated with respect to the specified period.
     * <p>
     * This method will return a new period where every field represents a period less
     * than the specified period. If this period contains a period that cannot be converted
     * to the specified unit then an exception is thrown.
     * <p>
     * For example, if this period is '37 Hours, 7 Minutes' and the specified period is
     * '24 Hours' then the output will be '13 Hours, 7 Minutes'.
     * <p>
     * This method requires this period to be convertible to the specified period.
     * To ensure this is true, call {@link #retainConvertible}, with the base unit of the
     * period passed into this method, before calling this method.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to calculate the remainder against, not null
     * @return a {@code PeriodFields} based on this period with the remainder, never null
     * @throws CalendricalException if any field cannot be converted to the unit of the period
     */
    public PeriodFields remainder(PeriodField period) {
        checkNotNull(period, "PeriodField must not be null");
        TreeMap<PeriodUnit, PeriodField> copy = createMap();
        for (PeriodField loopField : unitFieldMap.values()) {
            if (loopField.getUnit().equals(period.getUnit())) {
                copy.put(loopField.getUnit(), loopField.remainder(period.getAmount()));
            } else {
                for (PeriodField equivalent : period.getUnit().getEquivalentPeriods()) {
                    if (loopField.getUnit().equals(equivalent.getUnit())) {
                        copy.put(loopField.getUnit(), loopField.remainder(equivalent.getAmount()));
                    }
                }
            }
        }
        if (copy.size() < size()) {
            throw new CalendricalException("Unable to calculate remainder as some fields cannot be converted");
        }
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the amounts normalized.
     * <p>
     * The calculation examines each pair of units in this period that have a fixed conversion factor.
     * Each pair is adjusted so that the amount in the smaller unit does not exceed
     * the amount of the fixed conversion factor.
     * <p>
     * For example, a period of '2 Decades, 2 Years, 17 Months' normalized using
     * 'Years' and 'Months' will return '23 Years, 5 Months'.
     * <p>
     * The result will always contain all the units present in this period, even if they are zero.
     * The result will be equivalent to this period.
     *
     * @return a period equivalent to this period with the amounts normalized, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields normalized() {
        return normalizedTo(unitFieldMap.keySet().toArray(new PeriodUnit[unitFieldMap.size()]));
    }

    /**
     * Returns a copy of this period with the amounts normalized to the specified units.
     * <p>
     * This will normalize the period around the specified units.
     * The calculation examines each pair of units that have a fixed conversion factor.
     * Each pair is adjusted so that the amount in the smaller unit does not exceed
     * the amount of the fixed conversion factor.
     * At least one unit must be specified for this method to have any effect.
     * <p>
     * For example, a period of '2 Decades, 2 Years, 17 Months' normalized using
     * 'Years' and 'Months' will return '23 Years, 5 Months'.
     * <p>
     * Any part of this period that cannot be converted to one of the specified units
     * will be unaffected in the result.
     * <p>
     * The result will always contain all the specified units, even if they are zero.
     * The result will be equivalent to this period.
     *
     * @param units  the unit array to normalize to, not altered, not null, no nulls
     * @return a period equivalent to this period with the amounts normalized, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields normalizedTo(PeriodUnit... units) {
        checkNotNull(units, "PeriodUnit array must not be null");
        PeriodFields result = this;
        TreeSet<PeriodUnit> targetUnits = new TreeSet<PeriodUnit>(Collections.reverseOrder());
        targetUnits.addAll(Arrays.asList(units));
        // normalize any fields in this period that have a unit greater than the
        // largest unit in the target set that can be normalized
        // eg. normalize Years-Months when the target set only contains Months
        for (PeriodUnit loopUnit : unitFieldMap.keySet()) {
            for (PeriodUnit targetUnit : targetUnits) {
                if (targetUnits.contains(loopUnit) == false) {
                    PeriodField conversion = loopUnit.getEquivalentPeriod(targetUnit);
                    if (conversion != null) {
                        long amount = result.getAmount(loopUnit);
                        result = result.plus(conversion.multipliedBy(amount)).without(loopUnit);
                        break;
                    }
                }
            }
        }
        // algorithm works by finding pairs to check
        // the first rule is to avoid numeric overflow wherever possible, such as when
        // Seconds and Minutes are both MAX_VALUE -
        // eg. the Hour-Minute and Hour-Second pair must be processed before the Minute-Second pair
        // the second rule is to handle the case where processing two pairs causes a knock on
        // effect on a pair that has already been processed according to the first rule -
        // eg. when the Hour-Minute pair is 59 and the Minute-Second pair is 61
        // this is achieved by restarting the whole algorithm (the process loop)
        for (boolean process = true; process; ) {
            process = false;
            for (PeriodUnit targetUnit : targetUnits) {
                for (PeriodUnit loopUnit : result.unitFieldMap.keySet()) {
                    if (targetUnit.equals(loopUnit) == false) {
                        PeriodField conversion = targetUnit.getEquivalentPeriod(loopUnit);
                        if (conversion != null) {
                            long convertAmount = conversion.getAmount();
                            long amount = result.getAmount(loopUnit);
                            if (amount >= convertAmount || amount <= -convertAmount) {
                                result = result.with(amount % convertAmount, loopUnit).plus(amount /convertAmount, targetUnit);
                                process = (units.length > 2);  // need to re-check from start
                            }
                        }
                    }
                }
                result = result.plus(0, targetUnit);  // ensure unit is in the result
            }
        }
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Clone the internal data storage map.
     *
     * @return the cloned map, never null
     */
    @SuppressWarnings("unchecked")
    private TreeMap<PeriodUnit, PeriodField> clonedMap() {
        return (TreeMap) unitFieldMap.clone();
    }

    //-----------------------------------------------------------------------
    /**
     * Totals this period in terms of a single unit.
     * <p>
     * This will take each of the stored {@code PeriodField} instances and
     * convert them to the specified unit. The result will be the total of these
     * converted periods.
     * <p>
     * For example, '3 Hours, 34 Minutes' can be totalled to minutes resulting
     * in '214 Minutes'.
     *
     * @param unit  the unit to total in, not null
     * @return a period equivalent to the total of this period in a single unit, never null
     * @throws CalendricalException if this period cannot be converted to the unit
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodField toTotal(PeriodUnit unit) {
        checkNotNull(unit, "PeriodUnit must not be null");
        PeriodField result = null;
        for (PeriodField period : unitFieldMap.values()) {
            period = period.toEquivalent(unit);
            result = (result != null ? result.plus(period) : period);
        }
        return result;
    }

    /**
     * Converts this period to one containing only the units specified.
     * <p>
     * This converts this period to one measured in the specified units.
     * It operates by looping through the individual parts of this period,
     * converting each in turn to one of the specified units.
     * These converted periods are then combined to form the result.
     * <p>
     * No normalization is performed on the result.
     * This means that an amount in a smaller unit cannot be converted to an amount in a larger unit.
     * If you need to do this, call {@link #normalized()} before calling this method.
     * <p>
     * This method uses {@link PeriodField#toEquivalent(PeriodUnit...)} and as such,
     * it is recommended to specify the units from largest to smallest.
     * <p>
     * For example, '3 Hours' can normally be converted to both minutes and seconds.
     * If the units array contains both 'Minutes' and 'Seconds', then the result will
     * be measured in whichever is first in the array.
     *
     * @param units  the required unit array, not altered, not null, no nulls
     * @return a period equivalent to this period, never null
     * @throws CalendricalException if this period cannot be converted to any of the units
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields toEquivalent(PeriodUnit... units) {
        checkNotNull(units, "PeriodUnit array must not be null");
        TreeMap<PeriodUnit, PeriodField> map = createMap();
        for (PeriodField period : unitFieldMap.values()) {
            period = period.toEquivalent(units);
            PeriodField old = map.get(period.getUnit());
            period = (old != null ? old.plus(period) : period);
            map.put(period.getUnit(), period);
        }
        return (map.equals(unitFieldMap) ? this : create(map));
    }

    //-----------------------------------------------------------------------
    /**
     * Estimates the duration of this period.
     * <p>
     * Each {@link PeriodUnit} contains an estimated duration for that unit.
     * The per-unit estimate allows an estimate to be calculated for the whole period
     * including fields of variable duration. The estimate will equal the
     * {@link #toDuration accurate} calculation if all the fields are based on seconds.
     *
     * @return the estimated duration of this period, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public Duration toEstimatedDuration() {
        Duration dur = Duration.ZERO;
        for (PeriodField field : this) {
            dur = dur.plus(field.toEstimatedDuration());
        }
        return dur;
    }

    /**
     * Calculates the accurate duration of this period.
     * <p>
     * The conversion is based on the {@code ISOChronology} definition of the seconds and
     * nanoseconds units. If all the fields in this period can be converted to either seconds
     * or nanoseconds then the conversion will succeed, subject to calculation overflow.
     * If any field cannot be converted to these fields above then an exception is thrown.
     *
     * @return the duration of this period based on {@code ISOChronology} fields, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public Duration toDuration() {
        PeriodFields period = toEquivalent(ISOChronology.periodSeconds(), ISOChronology.periodNanos());
        return Duration.ofSeconds(period.getAmount(ISOChronology.periodSeconds()), period.getAmount(ISOChronology.periodNanos()));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a {@code Map} equivalent to this period.
     * <p>
     * The map will connect the unit to the single field period.
     * The sort order is from largest unit to smallest unit.
     *
     * @return the map equivalent to this period, unmodifiable, never null
     */
    public SortedMap<PeriodUnit, PeriodField> toMap() {
        return Collections.unmodifiableSortedMap(unitFieldMap);
    }

    /**
     * Converts this period to a {@code PeriodFields}, trivially
     * returning {@code this}.
     *
     * @return {@code this}, never null
     */
    public PeriodFields toPeriodFields() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instance equal to the specified period.
     * <p>
     * Two {@code PeriodFields} instances are equal if all the contained
     * {@code PeriodField} instances are equal.
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
            return unitFieldMap.equals(other.unitFieldMap);
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
        return unitFieldMap.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the period, such as '[6 Days, 13 Hours]'.
     *
     * @return a descriptive representation of the period, not null
     */
    @Override
    public String toString() {
        if (unitFieldMap.size() == 0) {
            return "[]";
        }
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        for (PeriodField field : this) {
            buf.append(field.toString()).append(',').append(' '); 
        }
        buf.setLength(buf.length() - 2);
        buf.append(']');
        return buf.toString();
    }

}
