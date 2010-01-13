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

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.time.CalendricalException;
import javax.time.Duration;
import javax.time.MathUtils;
import javax.time.calendar.PeriodRule;

/**
 * A period of time measured as the combination of a number of separate fields,
 * such as '3 months, 4 days and 7 hours'.
 * <p>
 * <code>PeriodFields</code> is an immutable period that stores an amount of human-scale
 * time for a number of rules. For example, humans typically measure periods of time
 * in years, months, days, hours, minutes and seconds. These concepts are defined by
 * period rules in the chronology classes, and this class allows an amount to be specified
 * for a number of the rules, such as '3 days and 65 seconds'.
 * <p>
 * Basic mathematical operations are provided - plus(), minus(), multipliedBy(),
 * dividedBy() and negated(), all of which return a new instance
 * <p>
 * A value of zero can also be stored for any rule. This means that a
 * period of zero hours is not equal to a period of zero minutes.
 * The {@link #withZeroesRemoved()} method removes zero values.
 * <p>
 * <code>PeriodFields</code> can store rules of any kind which makes it usable with
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
     */
    public static final PeriodFields ZERO = new PeriodFields(new TreeMap<PeriodRule, PeriodField>());
    /**
     * The serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The map of period fields.
     */
    private final TreeMap<PeriodRule, PeriodField> ruleFieldMap;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>PeriodFields</code> from a single rule-amount pair.
     *
     * @param amount  the amount of create with, may be negative
     * @param rule  the period rule, not null
     * @return the <code>PeriodFields</code> instance, never null
     * @throws NullPointerException if the period rule is null
     */
    public static PeriodFields of(long amount, PeriodRule rule) {
        checkNotNull(rule, "PeriodUnit must not be null");
        TreeMap<PeriodRule, PeriodField> internalMap = createMap();
        internalMap.put(rule, PeriodField.of(amount, rule));
        return create(internalMap);
    }

    /**
     * Obtains an instance of <code>PeriodFields</code> from a set of rule-amount pairs.
     * <p>
     * The amount to store for each rule is obtained by calling {@link Number#longValue()}.
     * This will lose any decimal places for instances of <code>Double</code> and <code>Float</code>.
     * It may also silently lose precision for instances of <code>BigInteger</code> or <code>BigDecimal</code>.
     *
     * @param ruleAmountMap  a map of periods that will be used to create this
     *  period, not updated by this method, not null, contains no nulls
     * @return the <code>PeriodFields</code> instance, never null
     * @throws NullPointerException if the map is null or contains nulls
     */
    public static PeriodFields of(Map<PeriodRule, ? extends Number> ruleAmountMap) {
        checkNotNull(ruleAmountMap, "Map must not be null");
        // don't use contains() as tree map and others can throw NPE
        TreeMap<PeriodRule, PeriodField> internalMap = createMap();
        for (Entry<PeriodRule, ? extends Number> entry : ruleAmountMap.entrySet()) {
            PeriodRule rule = entry.getKey();
            Number amount = entry.getValue();
            checkNotNull(rule, "Null keys are not permitted in rule-amount map");
            checkNotNull(amount, "Null amounts are not permitted in rule-amount map");
            internalMap.put(rule, PeriodField.of(amount.longValue(), rule));
        }
        return create(internalMap);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>PeriodFields</code> from <code>PeriodProvider</code>.
     * <p>
     * This factory returns an instance with all the rule-amount pairs from the provider.
     *
     * @param periodProvider  the provider to create from, not null
     * @return the <code>PeriodFields</code> instance, never null
     * @throws NullPointerException if the period provider is null
     */
    public static PeriodFields from(PeriodProvider periodProvider) {
        checkNotNull(periodProvider, "PeriodProvider must not be null");
        if (periodProvider instanceof PeriodFields) {
            return (PeriodFields) periodProvider;
        }
        TreeMap<PeriodRule, PeriodField> map = createMap();
        if (periodProvider instanceof PeriodField) {
            PeriodField providedField = (PeriodField) periodProvider;
            map.put(providedField.getRule(), providedField);
        } else {
            for (PeriodRule rule : periodProvider.periodRules()) {
                long amount = periodProvider.periodAmount(rule);
                map.put(rule, PeriodField.of(amount, rule));
            }
        }
        return create(map);
    }

    /**
     * Obtains an instance of <code>PeriodFields</code> by totalling the amounts in
     * a list of <code>PeriodProvider</code>s.
     * <p>
     * This method returns an instance with all the rule-amount pairs from the providers
     * totalled. Thus a period of '2 months and 5 days' combined with a period of
     * '7 days and 21 hours' will yield a result of '2 months, 12 days and 21 hours'.
     *
     * @param periodProviders  the providers to total, not null
     * @return the <code>PeriodFields</code> instance, never null
     * @throws NullPointerException if the period provider is null
     */
    public static PeriodFields total(PeriodProvider... periodProviders) {
        checkNotNull(periodProviders, "PeriodProvider[] must not be null");
        if (periodProviders.length == 1 && periodProviders[0] instanceof PeriodFields) {
            return (PeriodFields) periodProviders[0];
        }
        TreeMap<PeriodRule, PeriodField> map = createMap();
        for (PeriodProvider periodProvider : periodProviders) {
            if (periodProvider instanceof PeriodField) {
                PeriodField providedField = (PeriodField) periodProvider;
                PeriodField field = map.get(providedField.getRule());
                field = (field != null ? field.plus(providedField.getAmount()) : providedField);
                map.put(providedField.getRule(), field);
            } else {
                for (PeriodRule rule : periodProvider.periodRules()) {
                    long amount = periodProvider.periodAmount(rule);
                    PeriodField field = map.get(rule);
                    field = (field != null ? field.plus(amount) : PeriodField.of(amount, rule));
                    map.put(rule, field);
                }
            }
        }
        return create(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a new empty map.
     *
     * @return ordered representation of internal map
     */
    private static TreeMap<PeriodRule, PeriodField> createMap() {
        return new TreeMap<PeriodRule, PeriodField>(Collections.reverseOrder());
    }

    /**
     * Internal factory to create an instance using a pre-built map.
     * The map must not be used by the calling code after calling the constructor.
     *
     * @param periodMap  the rule-amount map, not null, assigned not cloned
     * @return the created period, never null
     */
    private static PeriodFields create(TreeMap<PeriodRule, PeriodField> periodMap) {
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
    private PeriodFields(TreeMap<PeriodRule, PeriodField> periodMap) {
        this.ruleFieldMap = periodMap;
    }

    /**
     * Resolves singletons.
     *
     * @return the resolved instance
     */
    private Object readResolve() {
        if (ruleFieldMap.size() == 0) {
            return ZERO;
        }
        return this;
    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the complete set of fields.
//     *
//     * @return the period rule as an unmodifiable set, never null
//     */
//    public PeriodField[] periodFields() {
//        return ruleFieldMap.values().toArray(new PeriodField[ruleFieldMap.size()]);
//    }
    /**
     * Gets the complete set of rules which have amounts stored.
     *
     * @return the period rule as an unmodifiable set, never null
     */
    public Set<PeriodRule> periodRules() {
        return Collections.unmodifiableSet(ruleFieldMap.keySet());
    }

    /**
     * Gets the amount of time stored for the specified rule.
     * <p>
     * Zero is returned if no amount is stored for the rule.
     *
     * @param rule  the rule to get, not null
     * @return the amount of time stored in this period for the rule
     */
    public long periodAmount(PeriodRule rule) {
        PeriodFields.checkNotNull(rule, "PeriodRule must not be null");
        PeriodField field = ruleFieldMap.get(rule);
        return (field != null ? field.getAmount() : 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the period is zero-length.
     * <p>
     * This checks whether all the amounts in the period are zero.
     *
     * @return true if this period is zero-length
     */
    public boolean isZero() {
        for (PeriodField field : ruleFieldMap.values()) {
            if (field.isZero() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the period is fully positive, including zero.
     * <p>
     * This checks whether all the amounts in the period are positive,
     * defined as greater than or equal to zero.
     *
     * @return true if this period is fully positive
     */
    public boolean isPositive() {
        for (PeriodField field : ruleFieldMap.values()) {
            if (field.isNegative()) {
                return false;
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the size of the set of rule-amount pairs.
     *
     * @return number of rule-amount pairs
     */
    public int size() {
        return ruleFieldMap.size();
    }

    /**
     * Iterates through all the rules in the period.
     * <p>
     * This method fulfills the {@link Iterable} interface and allows looping
     * around the fields using the for-each loop.
     *
     * @return an iterator over the fields in this object, never null
     */
    public Iterator<PeriodField> iterator() {
        return ruleFieldMap.values().iterator();
    }

    /**
     * Checks whether this period contains an amount for the rule.
     *
     * @param rule  the rule to query, null returns false
     * @return true if the map contains an amount for the rule
     */
    public boolean contains(PeriodRule rule) {
        return ruleFieldMap.containsKey(rule);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the field for the specified rule.
     * <p>
     * This method allows the field to be queried by the rule, like a map.
     * If the rule is not found then <code>null</code> is returned.
     *
     * @param rule  the rule to query, not null
     * @return the period field, null if no field for the rule
     */
    public PeriodField get(PeriodRule rule) {
        checkNotNull(rule, "PeriodRule must not be null");
        return ruleFieldMap.get(rule);
    }

    /**
     * Gets the amount of this period for the specified rule.
     * <p>
     * This method allows the amount to be queried by the rule, like a map.
     * If the rule is not found then an exception is thrown.
     *
     * @param rule  the rule to query, not null
     * @return the period amount
     * @throws CalendricalException if there is no amount for the rule
     */
    public long getAmount(PeriodRule rule) {
        PeriodField field = get(rule);
        if (field == null) {
            throw new CalendricalException("No amount for rule: " + rule);
        }
        return field.getAmount();
    }

    /**
     * Gets the amount of this period for the specified rule converted
     * to an <code>int</code>.
     * <p>
     * This method allows the amount to be queried by the rule, like a map.
     * If the rule is not found then an exception is thrown.
     *
     * @param rule  the rule to query, not null
     * @return the period amount
     * @throws CalendricalException if there is no amount for the rule
     * @throws ArithmeticException if the amount is too large to be returned in an int
     */
    public int getAmountInt(PeriodRule rule) {
        return MathUtils.safeToInt(getAmount(rule));
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the amount of the period for the specified rule, returning
//     * the default value if this period does have an amount for the rule.
//     *
//     * @param rule  the rule to query, not null
//     * @param defaultValue  the default value to return if the rule is not present
//     * @return the period amount
//     * @throws NullPointerException if the period rule is null
//     */
//    public long get(PeriodRule rule, long defaultValue) {
//        checkNotNull(rule, "PeriodRule must not be null");
//        Long amount = ruleAmountMap.get(rule);
//        return amount == null ? defaultValue : amount;
//    }
//
//    /**
//     * Gets the amount of the period for the specified rule, returning
//     * the default value if this period does have an amount for the rule.
//     * <p>
//     * The amount is safely converted to an <code>int</code>.
//     *
//     * @param rule  the rule to query, not null
//     * @param defaultValue  the default value to return if the rule is not present
//     * @return the period amount
//     * @throws NullPointerException if the period rule is null
//     * @throws ArithmeticException if the amount is too large to be returned in an int
//     */
//    public int getInt(PeriodRule rule, int defaultValue) {
//        return MathUtils.safeToInt(get(rule, defaultValue));
//    }
//
//    /**
//     * Gets the amount of the period for the specified rule quietly returning
//     * null if this period does have an amount for the rule.
//     *
//     * @param rule  the rule to query, null returns null
//     * @return the period amount, null if rule not present
//     */
//    public Long getQuiet(PeriodRule rule) {
//        return rule == null ? null : ruleAmountMap.get(rule);
//    }

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
        TreeMap<PeriodRule, PeriodField> copy = clonedMap();
        for (Iterator<PeriodField> it = copy.values().iterator(); it.hasNext(); ) {
            if (it.next().isZero()) {
                it.remove();
            }
        }
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified amount for the rule.
     * <p>
     * If this period already contains an amount for the rule then the amount
     * is replaced. Otherwise, the rule-amount pair is added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to update the new instance with, may be negative
     * @param rule  the rule to update, not null
     * @return a new updated period instance, never null
     */
    public PeriodFields with(long amount, PeriodRule rule) {
        PeriodField existing = get(rule);
        if (existing != null && existing.getAmount() == amount) {
            return this;
        }
        TreeMap<PeriodRule, PeriodField> copy = clonedMap();
        copy.put(rule, PeriodField.of(amount, rule));
        return create(copy);
    }

//    /**
//     * Returns a copy of this period with the amounts from the specified map added.
//     * <p>
//     * If this instance already has an amount for any rule then the value is replaced.
//     * Otherwise the value is added.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param ruleAmountMap  the new map of fields, not null
//     * @return a new updated period instance, never null
//     * @throws NullPointerException if the map contains null keys or values
//     */
//    public PeriodFields with(Map<PeriodRule, Long> ruleAmountMap) {
//        checkNotNull(ruleAmountMap, "The field-value map must not be null");
//        if (ruleAmountMap.isEmpty()) {
//            return this;
//        }
//        // don't use contains() as tree map and others can throw NPE
//        TreeMap<PeriodRule, Long> clonedMap = clonedMap();
//        for (Entry<PeriodRule, Long> entry : ruleAmountMap.entrySet()) {
//            PeriodRule rule = entry.getKey();
//            Long value = entry.getValue();
//            checkNotNull(rule, "Null keys are not permitted in field-value map");
//            checkNotNull(value, "Null values are not permitted in field-value map");
//            clonedMap.put(rule, value);
//        }
//        return new PeriodFields(clonedMap);
//    }

    /**
     * Returns a copy of this period with the specified values altered.
     * <p>
     * This method operates on each rule in the input in turn.
     * If this period already contains an amount for the rule then the amount
     * is replaced. Otherwise, the rule-amount pair is added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period field to update, not null
     * @return a new updated period instance, never null
     */
    public PeriodFields with(PeriodFields period) {
        if (this == ZERO) {
            return period;
        }
        if (period == ZERO) {
            return this;
        }
        TreeMap<PeriodRule, PeriodField> copy = clonedMap();
        copy.putAll(period.ruleFieldMap);
        return create(copy);
    }

    /**
     * Returns a copy of this period with the specified rule removed.
     * <p>
     * If this period already contains an amount for the rule then the amount
     * is removed. Otherwise, no action occurs.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param rule  the rule to remove, not null
     * @return a new updated period instance, never null
     */
    public PeriodFields withRuleRemoved(PeriodRule rule) {
        checkNotNull(rule, "PeriodRule must not be null");
        if (ruleFieldMap.containsKey(rule) == false) {
            return this;
        }
        TreeMap<PeriodRule, PeriodField> copy = clonedMap();
        copy.remove(rule);
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * The returned period will take each rule in the provider and add the value
     * to the amount already stored in this period, returning a new one.
     * If this period does not contain an amount for the rule then the rule and
     * amount are simply returned directly in the result. The result will have
     * the union of the rules in this instance and the rules in the specified instance.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periodProvider  the period to add, not null
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields plus(PeriodProvider periodProvider) {
        checkNotNull(periodProvider, "PeriodProvider must not be null");
        if (this == ZERO && periodProvider instanceof PeriodFields) {
            return (PeriodFields) periodProvider;
        }
        TreeMap<PeriodRule, PeriodField> copy = clonedMap();
        for (PeriodRule rule : periodProvider.periodRules()) {
            long amount = periodProvider.periodAmount(rule);
            PeriodField old = copy.get(rule);
            PeriodField field = (old != null ? old.plus(amount) : PeriodField.of(amount, rule));
            copy.put(rule, field);
        }
        return create(copy);
    }

    /**
     * Returns a copy of this period with the specified period added.
     * <p>
     * The result will contain the rules and amounts from this period plus the
     * specified rule and amount.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to add in the new instance, may be negative
     * @param rule  the rule defining the period, not null
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields plus(long amount, PeriodRule rule) {
        checkNotNull(rule, "PeiodRule must not be null");
        if (amount == 0) {
            return this;
        }
        TreeMap<PeriodRule, PeriodField> copy = clonedMap();
        PeriodField old = copy.get(rule);
        PeriodField field = (old != null ? old.plus(amount) : PeriodField.of(amount, rule));
        copy.put(rule, field);
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * The returned period will take each rule in the provider and subtract the
     * value from the amount already stored in this period, returning a new one.
     * If this period does not contain an amount for the rule then the rule and
     * amount are simply returned directly in the result. The result will have
     * the union of the rules in this instance and the rules in the specified instance.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to subtract, not null
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields minus(PeriodProvider periodProvider) {
        checkNotNull(periodProvider, "PeriodProvider must not be null");
        if (this == ZERO && periodProvider instanceof PeriodFields) {
            return (PeriodFields) periodProvider;
        }
        TreeMap<PeriodRule, PeriodField> copy = clonedMap();
        for (PeriodRule rule : periodProvider.periodRules()) {
            long amount = periodProvider.periodAmount(rule);
            PeriodField old = copy.get(rule);
            PeriodField field = (old != null ? old.minus(amount) : PeriodField.of(amount, rule).negated());
            copy.put(rule, field);
        }
        return create(copy);
    }

    /**
     * Returns a copy of this period with the specified period subtracted.
     * <p>
     * The result will contain the rules and amounts from this period minus the
     * specified rule and amount.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amount  the amount to subtract to create the new instance, may be negative
     * @param rule  the rule defining the period, not null
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields minus(long amount, PeriodRule rule) {
        checkNotNull(rule, "PeiodRule must not be null");
        if (amount == 0) {
            return this;
        }
        TreeMap<PeriodRule, PeriodField> copy = clonedMap();
        PeriodField old = copy.get(rule);
        copy.put(rule, old != null ? old.minus(amount) : PeriodField.of(amount, rule).negated());
        return create(copy);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new instance with each amount in this period multiplied
     * by the specified scalar.
     *
     * @param scalar  the scalar to multiply by, not null
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public PeriodFields multipliedBy(int scalar) {
        if (scalar == 1 || isZero()) {
            return this;
        }
        TreeMap<PeriodRule, PeriodField> copy = createMap();
        for (PeriodField field : this) {
            copy.put(field.getRule(), field.multipliedBy(scalar));
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
        TreeMap<PeriodRule, PeriodField> copy = createMap();
        for (PeriodField field : this) {
            copy.put(field.getRule(), field.dividedBy(divisor));
        }
        return create(copy);
    }

    /**
     * Returns a new instance with each amount in this period negated.
     *
     * @return a new updated period instance, never null
     * @throws ArithmeticException if the calculation overflows
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
    private TreeMap<PeriodRule, PeriodField> clonedMap() {
        return (TreeMap) ruleFieldMap.clone();
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a map of rules to amounts.
     * <p>
     * The returned map will never be null, however it may be empty.
     * It is sorted by the rule, returning the largest first.
     * It is independent of this object - changes will not be reflected back.
     *
     * @return the independent, modifiable map of periods, never null, never contains null
     */
    public SortedMap<PeriodRule, Long> toRuleAmountMap() {
        SortedMap<PeriodRule, Long> map = new TreeMap<PeriodRule, Long>(Collections.reverseOrder());
        for (PeriodField field : this) {
            map.put(field.getRule(), field.getAmount());
        }
        return map;
    }

    /**
     * Converts this period to an estimated duration.
     * <p>
     * Each {@link PeriodRule} contains an estimated duration for that rule.
     * This method uses that estimate to calculate a total estimated duration for
     * this period.
     *
     * @return the estimated duration of this period, may be negative
     * @throws ArithmeticException if the calculation overflows
     */
    public Duration toEstimatedDuration() {
        Duration dur = Duration.ZERO;
        for (PeriodField field : this) {
            dur = dur.plus(field.toEstimatedDuration());
        }
        return dur;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this instance equal to the object specified.
     * <p>
     * Two <code>PeriodFields</code> instances are equal if all the contained
     * <code>PeriodField</code> instances are equal.
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
            return ruleFieldMap.equals(other.ruleFieldMap);
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
        return ruleFieldMap.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the period.
     *
     * @return the rule-amount map, never null
     */
    @Override
    public String toString() {
        if (ruleFieldMap.size() == 0) {
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
