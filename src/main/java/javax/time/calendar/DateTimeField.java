/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.CalendricalException;
import javax.time.MathUtils;

/**
 * A field of date-time measured using a single rule, such as 'MonthOfYear 12' or 'DayOfMonth 3'.
 * <p>
 * {@code DateTimeField} is an immutable field storing the value for a single date-time rule.
 * A {@code long} value is used to allow larger fields to be stored, such as 'NanoOfDay'.
 * <p>
 * This class permits the value of each field to be invalid.
 * For example, it is possible to store 'MonthOfYear 13' or 'DayOfMonth -121'.
 * Care must therefore be taken when interpreting the values.
 * <p>
 * {@code DateTimeField} can store rules of any kind which makes it usable with any calendar system.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class DateTimeField
        implements Comparable<DateTimeField>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The rule defining the field.
     */
    private final DateTimeRule rule;
    /**
     * The value of the field.
     */
    private final long value;

    /**
     * Obtains a {@code DateTimeField} from a rule and value.
     * <p>
     * The parameters represent the two parts of a phrase like 'MonthOfYear 12'.
     *
     * @param rule  the rule defining the field, not null
     * @param value  the value of the rule
     * @return the date-time field, never null
     */
    public static DateTimeField of(DateTimeRule rule, long value) {
        ISOChronology.checkNotNull(rule, "DateTimeRule must not be null");
        return new DateTimeField(rule, value);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param value  the value of the rule
     * @param rule  the rule defining the field, not null
     */
    private DateTimeField(DateTimeRule rule, long value) {
        // input pre-validated
        this.value = value;
        this.rule = rule;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of this field which may be outside the value range for the rule.
     * <p>
     * For example, in the field 'MonthOfYear 12', the value is 12.
     *
     * @return the value
     */
    public long getValue() {
        return value;
    }

    /**
     * Gets the rule defining this field.
     * <p>
     * For example, in the field 'MonthOfYear 12', the unit is 'MonthOfYear'.
     *
     * @return the field unit, never null
     */
    public DateTimeRule getRule() {
        return rule;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code DateTimeField} with the rule altered.
     * <p>
     * Calling this method returns a new field with the same value but different rule.
     * For example, it could be used to change 'MonthOfYear 12' to 'HourOfDay 12'.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param rule  the rule to set in the returned field, not null
     * @return a {@code DateTimeField} based on this field with the specified rule, never null
     */
    public DateTimeField withRule(DateTimeRule rule) {
        ISOChronology.checkNotNull(rule, "DateTimeRule must not be null");
        if (rule.equals(this.rule)) {
            return this;
        }
        return new DateTimeField(rule, value);
    }

    /**
     * Returns a copy of this {@code DateTimeField} with the value altered.
     * <p>
     * Calling this method returns a new field with the same rule but different value.
     * For example, it could be used to change 'MonthOfYear 12' to 'MonthOfYear 6'.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param value  the value to set in the returned field, not null
     * @return a {@code DateTimeField} based on this field with the specified value, never null
     */
    public DateTimeField withValue(long value) {
        if (value == this.value) {
            return this;
        }
        return new DateTimeField(rule, value);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the value is valid for the rule.
     * <p>
     * A valid value is one where the value is appropriate for the rule.
     * This method considers the rule in isolation, using the outer value-range.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @param value  the value to check
     * @return true if the value is valid, false if invalid
     */
    public boolean isValidValue() {
        return rule.isValidValue(value);
    }

    /**
     * Gets the value of this field ensuring it is valid.
     * <p>
     * A valid value is one where the value is appropriate for the rule.
     * This method considers the rule in isolation, using the outer value-range.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @return the valid value
     * @throws CalendricalException if the value is invalid
     */
    public long getValidValue() {
        rule.checkValidValue(value);
        return value;
    }

    /**
     * Checks if the value is valid for the rule and capable of being converted to an {@code int}.
     * <p>
     * A valid value is one where the value is appropriate for the rule.
     * In addition to being valid, this checks that the minimum and maximum values are
     * {@link DateTimeRule#isIntValue() within} the bounds of an {@code int}.
     * <p>
     * This method considers the rule in isolation, using the outer value-range.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @param value  the value to check
     * @return true if the value is valid, false if invalid
     */
    public boolean isValidIntValue() {
        return rule.isIntValue() && rule.isValidValue(value);
    }

    /**
     * Gets the value of this field ensuring it is valid, converting to an {@code int}.
     * <p>
     * A valid value is one where the value is appropriate for the rule.
     * In addition to being valid, this checks that the minimum and maximum values are
     * {@link DateTimeRule#isIntValue() within} the bounds of an {@code int}.
     * <p>
     * This method considers the rule in isolation, using the outer value-range.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @return the valid {@code int} value
     * @throws CalendricalException if the value is invalid or the rule does not specify an {@code int} value
     */
    public int getValidIntValue() {
        rule.checkValidValue(value);
        rule.checkIntValue();
        return (int) value;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this field with the value rolled.
     * <p>
     * This rolls the value by the specified amount.
     * Rolling is the process of adding or subtracting the value of the field rolling
     * around within the range. For example, 'MonthOfYear' is defined as having 12 months.
     * Adding 3 to 'MonthOfYear 11' rolls around to result in 'MonthOfYear 2'.
     * <p>
     * If the size of the unit is not fixed then the outer value-range is used.
     * For example, 'DayOfMonth' will use the outer range of 1 to 31 for rolling.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToRollBy  the amount to roll by, positive or negative
     * @return a {@code DateTimeField} based on this field with the specified amount rolled, never null
     * @throws CalendricalException if the value is invalid
     */
    public DateTimeField roll(long amountToRollBy) {
        return withValue(rule.roll(value, amountToRollBy));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a {@code DateTimeFields} instance.
     * <p>
     * The returned fields instance will have {@code this} as a single field.
     *
     * @return the equivalent date-time fields, never null
     */
    public DateTimeFields2 toDateTimeFields() {
        return DateTimeFields2.of(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this field to the specified field.
     * <p>
     * The comparison orders first by the unit, then by the amount.
     *
     * @param otherPeriod  the other  to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    public int compareTo(DateTimeField otherPeriod) {
        // there are no isGreaterThan/isLessThan methods as they don't make sense
        int cmp = rule.compareTo(otherPeriod.rule);
        if (cmp != 0) {
            return cmp;
        }
        return MathUtils.safeCompare(value, otherPeriod.value);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this field is equal to the specified field.
     * <p>
     * The comparison is based on the rule and value.
     *
     * @param obj  the object to check, null returns false
     * @return true if this  is the same as that specified
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
           return true;
        }
        if (obj instanceof DateTimeField) {
            DateTimeField other = (DateTimeField) obj;
            return this.value == other.value &&
                    this.rule.equals(other.rule);
        }
        return false;
    }

    /**
     * Returns the hash code for this field.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return rule.hashCode() ^ (int) (value ^ (value >>> 32));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of this field, such as 'MonthOfYear 12'.
     * <p>
     * The format consists of the rule name, followed by a space, followed by the value.
     *
     * @return a descriptive representation of this field, not null
     */
    @Override
    public String toString() {
        return rule.getName() + " " + value;
    }

}
