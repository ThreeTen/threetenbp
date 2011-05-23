/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.CalendricalException;
import javax.time.MathUtils;

/**
 * A set of fields of date-time, such as '[MonthOfYear 12, DayOfMonth 3]'.
 * <p>
 * {@code DateTimeFields} is an immutable class storing  a set of {@link DateTimeField} objects.
 * The representation is effectively a rule-value map.
 * A {@code long} value is used to allow larger fields to be stored, such as 'NanoOfDay'.
 * <p>
 * The set of fields express constraints on dates and times.
 * For example, if an instance stores 'DayOfMonth 13' and 'DayOfWeek 5'
 * then it represents and matches only dates of Friday the Thirteenth.
 * <p>
 * This class permits the value of each field to be invalid.
 * For example, it is possible to store '[MonthOfYear 13]' or '[DayOfMonth -121]'.
 * There is also no cross validation between fields.
 * For example, it is possible to store '[DayOfMonth 31', 'MonthOfYear 2]'
 * Care must therefore be taken when interpreting the values.
 * <p>
 * {@code DateTimeFields} can store rules of any kind which makes it usable with
 * any calendar system.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateTimeFields
        implements Calendrical,
            CalendricalMatcher, Iterable<DateTimeField>, Serializable {

    /**
     * A singleton empty {@code DateTimeFields}, placing no restrictions on the date-time.
     */
    public static final DateTimeFields EMPTY = new DateTimeFields(Collections.<DateTimeField>emptyList());

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The list of fields which never contains the same rule twice (as in a map), not null, may be empty.
     */
    private final List<DateTimeField> fields;

    /**
     * Obtains a {@code DateTimeFields} from a rule and value.
     * <p>
     * The parameters represent the two parts of a phrase like 'MonthOfYear 12'.
     *
     * @param rule  the rule defining the field, not null
     * @param value  the value of the rule, may be outside the valid range for the rule
     * @return the date-time fields, not null
     */
    public static DateTimeFields of(DateTimeRule rule, long value) {
        return of(DateTimeField.of(rule, value));
    }

    /**
     * Obtains a {@code DateTimeFields} from two rule-value pairs.
     * <p>
     * The pair of parameters represent the two parts of a phrase like 'MonthOfYear 12'.
     *
     * @param rule1  the first rule defining the field, not null
     * @param value1  the first value of the rule, may be outside the valid range for the rule
     * @param rule2  the second rule defining the field, not null
     * @param value2  the second value of the rule, may be outside the valid range for the rule
     * @return the date-time fields, not null
     * @throws IllegalArgumentException if any rule is duplicated
     */
    public static DateTimeFields of(DateTimeRule rule1, long value1, DateTimeRule rule2, long value2) {
        DateTimeField field1 = DateTimeField.of(rule1, value1);
        DateTimeField field2 = DateTimeField.of(rule2, value2);
        return of(Arrays.asList(field1, field2));
    }

    /**
     * Obtains a {@code DateTimeFields} from a {@code DateTimeField}.
     * <p>
     * The parameters represent the two parts of a phrase like 'MonthOfYear 12'.
     *
     * @param rule  the rule defining the field, not null
     * @param value  the value of the rule, not necessarily valid for the rule
     * @return the date-time fields, not null
     */
    public static DateTimeFields of(DateTimeField field) {
        ISOChronology.checkNotNull(field, "DateTimeField must not be null");
        return new DateTimeFields(Collections.singletonList(field));
    }

    /**
     * Obtains a {@code DateTimeFields} from an array of fields.
     * <p>
     * Each field represents a phrase like 'MonthOfYear 12'.
     * ,p>
     * The array must provide fields with no duplicate rules.
     *
     * @param fieldsIterable  the iterable providing fields
     * @return the date-time fields, not null
     * @throws IllegalArgumentException if any rule is duplicated
     */
    public static DateTimeFields of(DateTimeField... fields) {
        ISOChronology.checkNotNull(fields, "Array must not be null");
        return of(Arrays.asList(fields));
    }

    /**
     * Obtains a {@code DateTimeFields} from a collection of fields.
     * <p>
     * Each field represents a phrase like 'MonthOfYear 12'.
     * ,p>
     * The iterable must provide fields with no duplicate rules.
     *
     * @param fieldsIterable  the iterable providing fields
     * @return the date-time fields, not null
     * @throws IllegalArgumentException if any rule is duplicated
     */
    public static DateTimeFields of(Iterable<DateTimeField> fieldsIterable) {
        ISOChronology.checkNotNull(fieldsIterable, "Iterable must not be null");
        Set<DateTimeRule> rules = new HashSet<DateTimeRule>();
        List<DateTimeField> created = new ArrayList<DateTimeField>();
        for (DateTimeField field : fieldsIterable) {
            ISOChronology.checkNotNull(field, "DateTimeField must not be null");
            if (rules.add(field.getRule()) == false) {
                throw new IllegalArgumentException("Duplicate rules are not allowed");
            }
            created.add(field);
        }
        if (created.size() == 0) {
            return EMPTY;
        }
        Collections.sort(created, Collections.reverseOrder());
        return new DateTimeFields(created);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param assignedFields  the list of fields, which is assigned, not null
     */
    private DateTimeFields(List<DateTimeField> assignedFields) {
        fields = assignedFields;
    }

    /**
     * Ensure EMPTY singleton.
     *
     * @return the resolved instance
     * @throws ObjectStreamException if an error occurs
     */
    private Object readResolve() throws ObjectStreamException {
        return fields.isEmpty() ? EMPTY : this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of fields.
     * <p>
     * This method returns the number of rule-value pairs stored.
     *
     * @return number of rule-value pairs, zero or greater
     */
    public int size() {
        return fields.size();
    }

    /**
     * Iterates through all the fields.
     * <p>
     * This method fulfills the {@link Iterable} interface and allows looping
     * around the fields using the for-each loop.
     * The fields are returned {@link DateTimeField#compareTo(DateTimeField) sorted} in reverse order.
     *
     * @return an iterator over the fields, not null
     */
    public Iterator<DateTimeField> iterator() {
        return Collections.unmodifiableCollection(fields).iterator();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if one of the stored fields is for the specified rule.
     * <p>
     * This method returns true if a value can be obtained for the specified rule.
     *
     * @param rule  the rule to query, null returns false
     * @return true if these fields contain the rule
     */
    public boolean contains(DateTimeRule rule) {
        for (DateTimeField field : fields) {
            if (field.getRule().equals(rule)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets one of the stored fields given a rule.
     * <p>
     * No attempt is made to derive values.
     * The result will be one of the stored fields or null if the rule is not present.
     *
     * @param rule  the rule to query, not null
     * @return the field with the specified rule, null if not found
     */
    public DateTimeField getField(DateTimeRule rule) {
        ISOChronology.checkNotNull(rule, "DateTimeRule must not be null");
        for (DateTimeField field : fields) {
            if (field.getRule().equals(rule)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Gets the value for the specified rule.
     * <p>
     * This class permits the value of each field to be invalid.
     * For example, it is possible to store '[MonthOfYear 13]' or '[DayOfMonth -121]'.
     * Care must therefore be taken when interpreting the values.
     * <p>
     * No attempt is made to derive values.
     * The result is simply based on the content of the stored field.
     * If there is no field for the rule then an exception is thrown.
     *
     * @return the value of the rule, may be outside the valid range for the rule
     * @throws CalendricalException if the field is not present
     */
    public long getValue(DateTimeRule rule) {
        DateTimeField field = getField(rule);
        if (field == null) {
            throw new CalendricalRuleException("Rule not found: " + rule, rule);
        }
        return field.getValue();
    }

    /**
     * Gets the value for the specified rule ensuring it is valid.
     * <p>
     * This checks that the value is within the valid range of the rule.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * No attempt is made to derive values.
     * The result is simply based on the content of the stored field.
     * If there is no field for the rule then an exception is thrown.
     *
     * @return the value of the rule, checked to ensure it is valid
     * @throws CalendricalException if the field is not present or is invalid
     */
    public long getValidValue(DateTimeRule rule) {
        // TODO: use fields to refine valid range
        long value = getValue(rule);
        return rule.checkValidValue(value);
    }

    /**
     * Gets the value for the specified rule as an {@code int} ensuring it is valid for the rule.
     * <p>
     * This checks that the value is within the valid range of the rule and
     * that all valid values are within the bounds of an {@code int}.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * No attempt is made to derive values.
     * The result is simply based on the content of the stored field.
     * If there is no field for the rule then an exception is thrown.
     *
     * @return the value of the rule, checked to ensure it is valid and fits in an {@code int}
     * @throws CalendricalException if the field is not present, invalid or does not fit in an {@code int}
     */
    public int getValidIntValue(DateTimeRule rule) {
        // TODO: use fields to refine valid range
        long value = getValue(rule);
        return rule.checkValidIntValue(value);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of these fields with the specified field set.
     * <p>
     * This replaces the value of the rule if the rule is present,
     * or adds the field if the rule is not present.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param rule  the rule to alter, not null
     * @param value  the value to use, may be outside the valid range for the rule
     * @return a {@code DateTimeFields} based on this fields with the specified field updated, not null
     */
    public DateTimeFields with(DateTimeRule rule, long value) {
        return with(DateTimeField.of(rule, value));
    }

    /**
     * Returns a copy of these fields with the specified field set.
     * <p>
     * This replaces the value of the rule if the rule is present,
     * or adds the field if the rule is not present.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param fields  the fields to add to the returned object, not null
     * @return a {@code DateTimeFields} based on this fields with the specified field updated, not null
     */
    public DateTimeFields with(DateTimeField field) {
        ISOChronology.checkNotNull(fields, "DateTimeField must not be null");
        List<DateTimeField> newFields = new ArrayList<DateTimeField>(fields);
        for (ListIterator<DateTimeField> it = newFields.listIterator(); it.hasNext(); ) {
            DateTimeField itField = it.next();
            if (itField.getRule().equals(field.getRule())) {
                if (itField.getValue() == field.getValue()) {
                    return this;
                } else {
                    it.set(field);
                    return new DateTimeFields(newFields);
                }
            }
        }
        newFields.add(field);
        Collections.sort(newFields, Collections.reverseOrder());
        return new DateTimeFields(newFields);
    }

    /**
     * Returns a copy of these fields with the specified field removed.
     * <p>
     * This removes the specified rule from those in the returned fields.
     * No error occurs if the rule is not present.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param rule  the field to remove from the returned fields, not null
     * @return a {@code DateTimeFields} based on this fields with the specified rule removed, not null
     */
    public DateTimeFields without(DateTimeRule rule) {
        ISOChronology.checkNotNull(rule, "DateTimeRule must not be null");
        List<DateTimeField> newFields = new ArrayList<DateTimeField>(fields);
        for (Iterator<DateTimeField> it = newFields.iterator(); it.hasNext(); ) {
            if (it.next().getRule().equals(rule)) {
                it.remove();
                if (newFields.size() == 0) {
                    return EMPTY;
                }
                return new DateTimeFields(newFields);
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of these fields with the value of the specified rule rolled.
//     * <p>
//     * This rolls the value of one rule by the specified amount.
//     * Rolling is the process of adding or subtracting the value of the field rolling
//     * around within the range. For example, 'MonthOfYear' is defined as having 12 months.
//     * Adding 3 to 'MonthOfYear 11' rolls around to result in 'MonthOfYear 2'.
//     * <p>
//     * This {@code DateTimeFields} is used as the context for the roll.
//     * For example, 'DayOfMonth' has a value-range between 28 and 31 depending on the month and year.
//     * If both the month and year can be derived from this object, then the value-range
//     * used in the roll will be accurate, otherwise the best estimate will be used.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param rule  the field to roll which must be present, not null
//     * @param amountToRollBy  the amount to roll by, positive or negative
//     * @return a {@code DateTimeFields} based on this fields with the specified amount rolled, not null
//     * @throws CalendricalException if the field is not present or is invalid
//     */
//    public DateTimeFields roll(DateTimeRule rule, long amountToRollBy) {
//        long value = getValidValue(rule);
//        long rolled = rule.roll(value, amountToRollBy, this);
//        return with(rule, rolled);
//    }  // TODO

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of these fields with the values normalized.
     * <p>
     * The calculation examines each set of fields and attempts to combine them.
     * Any fields that are successfully combined are removed.
     * The result will be equivalent to these fields.
     * <p>
     * For example, the fields '[QuarterOfYear 2, MonthOfQuarter 3, DayOfMonth 4]'
     * will be normalized to '[MonthOfYear 6, DayOfMonth 4]'. The quarter-of-year
     * and month-of-quarter can be successfully merged without any loss of accuracy.
     * However, month-of-year and day-of-month cannot be merged without knowing
     * the year due to the different length of February in leap years.
     * <p>
     * During the combination process, some fields may be found to conflict.
     * For example, a second-of-minute value of 12 conflicts with a second-of-day
     * value of 0. An exception is thrown for conflicts.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a {@code DateTimeFields} based on this with the fields normalized, not null
     * @throws CalendricalException if any fields conflict
     * @throws ArithmeticException if the calculation overflows
     */
    public DateTimeFields normalized() {
        if (size() == 0) {
            return EMPTY;
        }
        
        // normalize each individual field in isolation
        // do not restart after this, because the rule must define the correct base
        // rule to ensure that it is completely normalized in isolation in one step
        DateTimeFields result = this;
        for (DateTimeField field : this) {
            DateTimeField normalized = field.normalized();
            if (normalized != field) {
                DateTimeField existing = result.getField(normalized.getRule());
                if (existing != null && existing.equals(normalized) == false) {
                    throw new CalendricalRuleException("Unable to normalize, " + field +
                            " normalized to " + normalized + " which is incompatible with existing field " +
                            existing + " in input " + this, normalized.getRule());
                }
                result = result.without(field.getRule()).with(normalized);
            }
        }
        if (result.size() < 2) {
            return result;
        }
        
        // group according to base rule
        ConcurrentMap<DateTimeRule, List<DateTimeField>> grouped = new ConcurrentHashMap<DateTimeRule, List<DateTimeField>>();
        for (DateTimeField field : result) {
            DateTimeRule rule = field.getRule();
            DateTimeRule baseRule = rule.getBaseRule();
            if (baseRule != rule) {
                grouped.putIfAbsent(baseRule, new ArrayList<DateTimeField>());
                grouped.get(baseRule).add(field);
            }
        }
        
        // normalize groups
        // TODO: loop again (group again) if register on group is public
        for (DateTimeRule rule : grouped.keySet()) {
            result = mergeGroup(result, rule, grouped.get(rule));
        }
        return result;
    }

    private DateTimeFields mergeGroup(DateTimeFields result, DateTimeRule baseRule, List<DateTimeField> fields) {
        if (fields.size() < 2) {
            return result;
        }
        DateTimeRuleGroup ruleGroup = DateTimeRuleGroup.of(baseRule);
        for (int i = 0; i < fields.size() - 1; i++) {
            for (int j = i + 1; j < fields.size(); j++) {
                DateTimeField field1 = fields.get(i);
                DateTimeField field2 = fields.get(j);
                DateTimeField calc = field1.derive(field2.getRule());
                if (calc != null) {
                    if (calc.equals(field2)) {
                        result = result.without(field2.getRule());
                        fields.remove(j--);
                    } else {
                        throw new CalendricalRuleException("Unable to normalize, " + field1 +
                                " is incompatible with " + field2 + " in input " + this, field2.getRule());
                    }
                }
            }
        }
        
        for (int i = 0; i < fields.size() - 1; i++) {
            for (int j = i + 1; j < fields.size(); j++) {
                DateTimeField fieldLarger = fields.get(i);
                DateTimeField fieldSmaller = fields.get(j);
                
                DateTimeRule ruleCombined = ruleGroup.getRelatedRule(fieldLarger.getRule(), fieldSmaller.getRule());
                System.out.println(fields + " " + /*ruleGroup.getRelatedRules() + " " +*/ ruleCombined);
                if (ruleCombined != null) {
                    DateTimeField fieldCombined = merge(fieldLarger, fieldSmaller, ruleCombined);
                    DateTimeField existing = result.getField(ruleCombined);
                    if (existing != null && existing.equals(fieldCombined) == false) {
                        throw new CalendricalRuleException("Unable to normalize, [" + fieldLarger +
                                ", " + fieldSmaller + "] normalized to " + fieldCombined +
                                " which is incompatible with existing field " +
                                existing + " in input " + this, ruleCombined);
                    }
                    result = result.without(fieldLarger.getRule()).without(fieldSmaller.getRule()).with(fieldCombined);
                    fields.set(i--, fieldCombined);
                    fields.remove(j);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Merges the two fields to form an instance of the combined rule.
     * For example, merge hour-of-ampm and ampm-of-day to form hour-of-day.
     * 
     * @param field1  the larger rule to merge - MidOfBig, not null
     * @param field2  the smaller rule to merge - SmallOfMid, not null
     * @param ruleCombined  the rule to merge into - SmallOfBig, not null
     * @return combined field, not null
     */
    private DateTimeField merge(DateTimeField field1, DateTimeField field2, DateTimeRule ruleCombined) {
        long period1 = field1.getRule().convertToPeriod(field1.getValue());
        long period2 = field2.getRule().convertToPeriod(field2.getValue());
        PeriodField conversion = field1.getRule().getPeriodUnit().getEquivalentPeriod(field2.getRule().getPeriodUnit());
        long scaledPeriod1 = MathUtils.safeMultiply(period1, conversion.getAmount());
        long totalPeriod = MathUtils.safeAdd(scaledPeriod1, period2);
        return ruleCombined.field(ruleCombined.convertFromPeriod(totalPeriod));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this instance then
     * an attempt is made to derive the value.
     * If that fails, {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        ISOChronology.checkNotNull(rule, "CalendricalRule must not be null");
        if (rule instanceof DateTimeRule) {
            DateTimeField field = getField((DateTimeRule) rule);
            if (field != null) {
                return rule.reify(field);
            }
        }
        return rule.deriveValueFrom(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the fields in this object match those in the specified calendrical.
     * <p>
     * This implementation checks that all calendrical fields in this object match.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical fields match, false otherwise
     */
    public boolean matchesCalendrical(Calendrical calendrical) {
        ISOChronology.checkNotNull(calendrical, "Calendrical must not be null");
        for (DateTimeField field : fields) {
            DateTimeField calField = field.getRule().getValue(calendrical);
            if (calField != null && calField.equals(field) == false) {
                return false;
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this {@code DateTimeFields} is equal to the specified set of fields.
     * <p>
     * The comparison is based on the complete set of fields.
     *
     * @param other  the other fields to compare to, null returns false
     * @return true if this is equal to the specified fields
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof DateTimeFields) {
            DateTimeFields otherFields = (DateTimeFields) other;
            return fields.equals(otherFields.fields);
        }
        return false;
    }

    /**
     * A hash code for this set of fields.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this fields as a {@code String}, such as {@code [MonthOfYear 12, DayOfMonth 3]}.
     *
     * @return a descriptive representation of this field, not null
     */
    @Override
    public String toString() {
        return fields.toString();
    }

}
