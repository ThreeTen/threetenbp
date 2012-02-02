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
package javax.time.calendrical;

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

import javax.time.CalendricalException;

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
        implements Calendrical, Iterable<DateTimeField>, Serializable {

    /**
     * A singleton empty {@code DateTimeFields}, placing no restrictions on the date-time.
     */
    public static final DateTimeFields EMPTY = new DateTimeFields(Collections.<DateTimeField>emptyList());

    /**
     * Serialization version.
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
     * This simply wraps a {@code DateTimeField}.
     *
     * @param field  the field to wrap, not null
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
     * <p>
     * The array must provide fields with no duplicate rules.
     *
     * @param fields  the fields to use, no duplicate fields, not null, no nulls
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
     * <p>
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
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this instance then
     * {@code null} will be returned.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        // no optimization here
        // using the local fields would leave conflicts unresolved
        return CalendricalEngine.derive(ruleToDerive, null, null, null, null, null, null, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number of fields.
     * <p>
     * This method returns the number of stored {@code DateTimeField} instances.
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

    /**
     * Checks if this set of fields contains a field with the specified rule.
     * <p>
     * This method returns true one of the stored {@code DateTimeField} instances has the specified rule.
     *
     * @param rule  the rule to query, null returns false
     * @return true if this contains a field with the specified rule
     */
    public boolean contains(DateTimeRule rule) {
        for (DateTimeField field : fields) {
            if (field.getRule().equals(rule)) {
                return true;
            }
        }
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the stored field for the specified rule.
     * <p>
     * This method queries the stored {@code DateTimeField} instances by rule.
     * If no field is found with the specified rule then null is returned.
     * No attempt is made to derive values.
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
     * This method queries the stored {@code DateTimeField} instances by rule returning the value.
     * If no field is found with the specified unit then an exception is thrown.
     * No attempt is made to derive values.
     * <p>
     * This method does not check that the value is within the valid range of the rule.
     * For example, it is possible to store '[MonthOfYear 13]' or '[DayOfMonth -121]'.
     * Care must therefore be taken when interpreting the values.
     *
     * @param rule  the rule to query, not null
     * @return the value from the field with the specified rule, may be outside the valid range for the rule
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
     * This method queries the stored {@code DateTimeField} instances by rule returning the value.
     * If no field is found with the specified unit then an exception is thrown.
     * No attempt is made to derive values.
     * <p>
     * This method checks that the value is within the valid range of the rule.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @param rule  the rule to query, not null
     * @return the value from the field with the specified rule, checked to ensure it is valid
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
     * This method queries the stored {@code DateTimeField} instances by rule returning the value.
     * If no field is found with the specified unit then an exception is thrown.
     * No attempt is made to derive values.
     * <p>
     * This checks that the value is within the valid range of the rule and
     * that all valid values are within the bounds of an {@code int}.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @return the value from the field with the specified rule, checked to ensure it is valid and fits in an {@code int}
     * @throws CalendricalException if the field is not present, invalid or does not fit in an {@code int}
     */
    public int getValidIntValue(DateTimeRule rule) {
        // TODO: use fields to refine valid range
        long value = getValue(rule);
        return rule.checkValidIntValue(value);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of these fields with the specified rule and value.
     * <p>
     * The result is the set of fields in this instance with the specified field
     * merged as though using {@code Map.put} with the rule and value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param rule  the rule to set in the result, not null
     * @param value  the value to set in the result, may be outside the valid range for the rule
     * @return a {@code DateTimeFields} based on this fields with the specified field set, not null
     */
    public DateTimeFields with(DateTimeRule rule, long value) {
        return with(DateTimeField.of(rule, value));
    }

    /**
     * Returns a copy of these fields with the specified field set.
     * <p>
     * The result is the set of fields in this instance with the specified field
     * merged as though using {@code Map.put} with the rule and value.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the result, not null
     * @return a {@code DateTimeFields} based on this fields with the specified field set, not null
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
     * Returns a copy of these fields with the specified rule removed.
     * <p>
     * The result is the set of fields in this instance with the specified rule removed.
     * No error occurs if the rule is not present.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param rule  the rule to remove from the result, not null
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
     * Matches this field against the specified calendrical.
     * <p>
     * This checks whether the value of each field is the same as the value of
     * the same field extracted from the calendrical.
     * If any field cannot be extracted, false is returned.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical fields match, false otherwise
     */
    public boolean matches(Calendrical calendrical) {
        ISOChronology.checkNotNull(calendrical, "Calendrical must not be null");
        for (DateTimeField field : fields) {
            if (field.matches(calendrical) == false) {
                return false;
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this set of fields is equal to another set of fields.
     * <p>
     * The comparison is based on the complete set of fields.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other set of fields
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DateTimeFields) {
            DateTimeFields other = (DateTimeFields) obj;
            return fields.equals(other.fields);
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
     * Outputs this set of fields as a {@code String}, such as {@code [MonthOfYear 12, DayOfMonth 3]}.
     * <p>
     * The output will include of the complete set of fields.
     *
     * @return a string representation of this set of fields, not null
     */
    @Override
    public String toString() {
        return fields.toString();
    }

}
