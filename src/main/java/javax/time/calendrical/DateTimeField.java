/*
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
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

import java.io.Serializable;
import java.util.Locale;

import javax.time.CalendricalException;
import javax.time.MathUtils;
import javax.time.format.DateTimeFormatters;
import javax.time.format.TextStyle;

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
        implements Calendrical, Comparable<DateTimeField>, Serializable {

    /**
     * Serialization version.
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
     * @param value  the value of the rule, may be outside the valid range for the rule
     * @return the date-time field, not null
     */
    public static DateTimeField of(DateTimeRule rule, long value) {
        MathUtils.checkNotNull(rule, "DateTimeRule must not be null");
        return new DateTimeField(rule, value);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param value  the value of the rule, may be outside the valid range for the rule
     * @param rule  the rule defining the field, not null
     */
    private DateTimeField(DateTimeRule rule, long value) {
        // input pre-validated
        this.value = value;
        this.rule = rule;
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
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        if (this.rule.equals(ruleToDerive)) {
            return (T) this;
        }
        return CalendricalEngine.derive(ruleToDerive, this.rule, null, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule defining this field.
     * <p>
     * For example, in the field 'MonthOfYear 12', the rule is 'MonthOfYear'.
     *
     * @return the field rule, not null
     */
    public DateTimeRule getRule() {
        return rule;
    }

    /**
     * Gets the value of this field which may be outside the value range for the rule.
     * <p>
     * For example, in the field 'MonthOfYear 12', the value is 12.
     *
     * @return the value, may be outside the valid range for the rule
     */
    public long getValue() {
        return value;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this {@code DateTimeField} with the rule altered.
     * <p>
     * Calling this method returns a new field with the same value but different rule.
     * For example, it could be used to change 'MonthOfYear 12' to 'HourOfDay 12'.
     * This is rarely a useful operation but is included for completeness.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param rule  the rule to set in the returned field, not null
     * @return a {@code DateTimeField} based on this field with the specified rule, not null
     */
    public DateTimeField withRule(DateTimeRule rule) {
        MathUtils.checkNotNull(rule, "DateTimeRule must not be null");
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
     * @param value  the value to set in the returned field, may be outside the valid range for the rule
     * @return a {@code DateTimeField} based on this field with the specified value, not null
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
     * This checks that the value is within the valid range of the rule.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @return true if the value is valid
     */
    public boolean isValidValue() {
        return rule.getValueRange().isValidValue(value);
    }

    /**
     * Gets the value of this field ensuring it is valid for the rule.
     * <p>
     * This checks that the value is within the valid range of the rule.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @return the valid value
     * @throws CalendricalException if the value is invalid
     */
    public long getValidValue() {
        return rule.checkValidValue(value);
    }

    /**
     * Checks if the value is valid for the rule and that the rule defines
     * values that fit in an {@code int}.
     * <p>
     * This checks that the value is within the valid range of the rule and
     * that all valid values are within the bounds of an {@code int}.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @return true if the value is valid and fits in an {@code int}
     * @throws CalendricalException if the value is invalid
     */
    public boolean isValidIntValue() {
        return rule.getValueRange().isValidIntValue(value);
    }

    /**
     * Gets the value of this field as an {@code int} ensuring it is valid for the rule.
     * <p>
     * This checks that the value is within the valid range of the rule and
     * that all valid values are within the bounds of an {@code int}.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     *
     * @return the valid value
     * @throws CalendricalException if the value is invalid
     */
    public int getValidIntValue() {
        return rule.checkValidIntValue(value);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation of this field.
     * <p>
     * This returns the textual representation of the field, such as for day-of-week or month-of-year.
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param textStyle  the text style, not null
     * @param locale  the locale to use, not null
     * @return the textual representation of the field, not null
     */
    public String getText(TextStyle textStyle, Locale locale) {
        MathUtils.checkNotNull(textStyle, "TextStyle must not be null");
        MathUtils.checkNotNull(locale, "Locale must not be null");
        String text = DateTimeFormatters.getTextProvider().getText(this, textStyle, locale);
        return text == null ? Long.toString(value) : text;
    }

    //-----------------------------------------------------------------------
    /**
     * Matches this field against the specified calendrical.
     * <p>
     * This checks whether the value of this field is the same as the value of
     * the same field extracted from the calendrical.
     * If the field cannot be extracted, false is returned.
     *
     * @param calendrical  the calendrical to match, not null
     * @return true if the calendrical fields match, false otherwise
     */
    public boolean matches(Calendrical calendrical) {
        return this.equals(calendrical.get(rule));
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Returns a copy of this field with the value rolled.
//     * <p>
//     * This rolls the value by the specified amount.
//     * Rolling is the process of adding or subtracting the value of the field rolling
//     * around within the range. For example, 'MonthOfYear' is defined as having 12 months.
//     * Adding 3 to 'MonthOfYear 11' rolls around to result in 'MonthOfYear 2'.
//     * <p>
//     * If the size of the unit is not fixed then the outer value-range is used.
//     * For example, 'DayOfMonth' will use the outer range of 1 to 31 for rolling.
//     * <p>
//     * This instance is immutable and unaffected by this method call.
//     *
//     * @param amountToRollBy  the amount to roll by, positive or negative
//     * @return a {@code DateTimeField} based on this field with the specified amount rolled, not null
//     * @throws CalendricalException if the value is invalid
//     */
//    public DateTimeField roll(long amountToRollBy) {
//        return withValue(rule.roll(value, amountToRollBy));
//    }  TODO

    //-----------------------------------------------------------------------
    /**
     * Derives the value of the specified rule from this field.
     * <p>
     * This method queries the value of the specified rule based on this field.
     * This will only return a result if the requested rule is a subset of the
     * data held in this field and is suitable for derivation. For example,
     * 'MinuteOfHour' is a subset of 'SecondOfDay', but is not a subset of 'HourOfDay'.
     * If the rule cannot be derived, {@code null} is returned.
     * <p>
     * The definition of a subset is controlled by the rule.
     * Each rule defines a {@link DateTimeRule#getBaseRule() base rule},
     * {@link DateTimeRule#getPeriodUnit() period unit} and
     * {@link DateTimeRule#getPeriodRange() period range}.
     * If rule A has the same base rule as rule B, and the period unit to range
     * of A fits within that of B, then the rule is a subset.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the derived value for the rule, null if the value cannot be derived
     */
    DateTimeField derive(DateTimeRule ruleToDerive) {
        MathUtils.checkNotNull(ruleToDerive, "DateTimeRule must not be null");
        // check if this is the desired output already
        if (this.rule.equals(ruleToDerive)) {
            return this;
        }
        // check conversion is feasible and permitted
        if (rule.getBaseRule().equals(ruleToDerive.getBaseRule()) &&
                rule.comparePeriodUnit(ruleToDerive) <= 0 &&
                rule.comparePeriodRange(ruleToDerive) >= 0) {
            return derive(this, ruleToDerive);
        }
        return null;
    }

    private static DateTimeField derive(DateTimeField field, DateTimeRule ruleToDerive) {
        // TODO: doesn't handle DAYS well, as DAYS are not a multiple of NANOS
        DateTimeRule fieldRule = field.getRule();
        long period = fieldRule.convertToPeriod(field.getValue());
        long bottomConversion = ruleToDerive.getPeriodUnit().toEquivalent(fieldRule.getPeriodUnit());
        if (bottomConversion < 0) {
            return null;
        }
        period = MathUtils.floorDiv(period, bottomConversion);
        PeriodUnit rangeToDerive = ruleToDerive.getPeriodRange();
        if (rangeToDerive != null && fieldRule.comparePeriodRange(ruleToDerive) != 0) {
//                if (periodRange.equals(DAYS)) {  // TODO: hack
//                    periodRange = _24_HOURS;
//                }
            long topConversion = rangeToDerive.toEquivalent(ruleToDerive.getPeriodUnit());
            if (topConversion < 0) {
                return null;
            }
            period = MathUtils.floorMod(period, topConversion);
        }
        return ruleToDerive.field(ruleToDerive.convertFromPeriod(period));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this field to a {@code DateTimeFields} instance.
     * <p>
     * The returned fields instance will have {@code this} as a single field.
     *
     * @return the equivalent date-time fields, not null
     */
    public DateTimeFields toDateTimeFields() {
        return DateTimeFields.of(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this field to the specified field.
     * <p>
     * The comparison orders first by the rule, then by the value.
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
     * Checks if this field is equal to another field.
     * <p>
     * The comparison is based on the rule and value.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other field
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
     * A hash code for this field.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return rule.hashCode() ^ (int) (value ^ (value >>> 32));
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this field as a {@code String}, such as {@code MonthOfYear 12}.
     * <p>
     * The output will consist of the rule name, a space and the value.
     *
     * @return a string representation of this field, not null
     */
    @Override
    public String toString() {
        return rule.getName() + " " + value;
    }

}
