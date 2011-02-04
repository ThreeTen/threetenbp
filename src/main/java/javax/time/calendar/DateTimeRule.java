/*
 * Copyright (c) 2011 Stephen Colebourne & Michael Nascimento Santos
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
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.time.CalendricalException;

/**
 * The rule defining how a measurable aspect of time operates.
 * <p>
 * Rule implementations are the low-level framework for manipulating date and time.
 * They provide a common means to access fields like day-of-month.
 * This includes the field name and minimum/maximum values.
 * <p>
 * The name of a rule must be unique within a running system.
 * <p>
 * DateTimeFieldRule is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe and must
 * ensure serialization works correctly.
 * 
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class DateTimeRule implements Comparable<DateTimeRule>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * A Math context for calculating fractions from values.
     */
    private static final MathContext FRACTION_CONTEXT = new MathContext(9, RoundingMode.FLOOR);
    /**
     * A Math context for calculating values from fractions.
     */
    private static final MathContext VALUE_CONTEXT = new MathContext(0, RoundingMode.FLOOR);

    /**
     * Constructor used to create a rule.
     */
    protected DateTimeRule() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the rule.
     * <p>
     * Implementations should use the name that best represents themselves.
     * If the rule represents a field, then the form 'UnitOfRange' should be used (no plurals).
     * If the range is null, then the form 'Unit' should be used (no plural).
     * Otherwise, use the simple class name of the type, such as 'ZoneOffset'.
     *
     * @return the name of the rule, never null
     */
    public abstract String getName();

    //-----------------------------------------------------------------------
    /**
     * Gets the unit that the rule is measured in.
     * <p>
     * Most rules define a field such as 'hour of day' or 'month of year'.
     * The unit is the period that varies within the range.
     * <p>
     * For example, the rule for hour-of-day will return Hours, while the rule for
     * month-of-year will return Months. The rule for a date will return Days
     * as a date could alternately be described as 'days of forever'.
     * <p>
     * The {@code null} value is returned if the rule is not defined by a unit and range.
     *
     * @return the unit defining the rule unit, null if this rule isn't based on a period
     */
    public abstract PeriodUnit getPeriodUnit();

    /**
     * Gets the range that the rule is bound by.
     * <p>
     * Most rules define a field such as 'hour of day' or 'month of year'.
     * The range is the period that the field varies within.
     * <p>
     * For example, the rule for hour-of-day will return Days, while the rule for
     * month-of-year will return Years.
     * <p>
     * When the range is unbounded, such as for a date or the year field, then {@code null}
     * will be returned.
     * The {@code null} value is also returned if the rule is not defined by a unit and range.
     *
     * @return the unit defining the rule range, null if unbounded,
     *  or if this rule isn't based on a period
     */
    public abstract PeriodUnit getPeriodRange();

    //-----------------------------------------------------------------------
    /**
     * Gets the minimum value that the field can take.
     * <p>
     * This obtains the smallest minimum valid value that this rule ever allows.
     * <p>
     * This implementation returns zero.
     * Subclasses must override this as necessary.
     *
     * @return the minimum value for this field
     */
    public long getMinimumValue() {
        return 0;
    }

    /**
     * Gets the largest possible minimum value that the field can take.
     * <p>
     * This obtains the largest minimum valid value that this rule ever allows.
     * <p>
     * This implementation returns {@link #getMinimumValue()}.
     * Subclasses must override this as necessary.
     *
     * @return the largest possible minimum value for this field
     */
    public long getLargestMinimumValue() {
        return getMinimumValue();
    }

    /**
     * Gets the minimum value that the field can take using the specified
     * context to refine the accuracy of the response.
     * <p>
     * This obtains the minimum valid value that this rule allows given the specified context.
     * The result of this method will only be accurate if there is sufficient
     * information in the context to determine the correct value-range.
     * <p>
     * This implementation returns {@link #getMinimumValue()}.
     * Subclasses must override this as necessary.
     *
     * @param context  the context of other fields, not null
     * @return the minimum value of the field given the context
     */
    public long getMinimumValue(DateTimeFields2 context) {
        return getMinimumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the maximum value that the field can take.
     * <p>
     * This obtains the largest maximum valid value that this rule ever allows.
     * <p>
     * For example, 'DayOfMonth' has a size between 28 and 31 depending on the month and year.
     * The maximum is therefore 31.
     * <p>
     * This implementation derives the value from the equivalent period of the unit in the range,
     * adjusted by the {@link #getMinimumValue() minimum value}.
     *
     * @return the maximum value for this field
     */
    public long getMaximumValue() {
        return getPeriodRange().getEquivalentPeriod(getPeriodUnit()).getAmount() - 1 + getMinimumValue();
    }

    /**
     * Gets the smallest possible maximum value that the field can take.
     * <p>
     * This obtains the smallest maximum valid value that this rule ever allows.
     * <p>
     * For example, 'DayOfMonth' has a size between 28 and 31 depending on the month and year.
     * The smallest maximum is therefore 28.
     * <p>
     * This implementation returns {@link #getMaximumValue()}.
     * Subclasses must override this as necessary.
     *
     * @return the smallest possible maximum value for this field
     */
    public long getSmallestMaximumValue() {
        return getMaximumValue();
    }

    /**
     * Gets the maximum value that the field can take using the specified
     * context to refine the accuracy of the response.
     * <p>
     * This obtains the maximum valid value that this rule allows given the specified context.
     * The result of this method will only be accurate if there is sufficient
     * information in the context to determine the correct value-range.
     * <p>
     * For example, 'DayOfMonth' has a size between 28 and 31 depending on the month and year.
     * If both the month and year can be derived from the calendrical, then the maximum value
     * returned will be accurate. Otherwise the best estimate will be returned.
     * <p>
     * This implementation returns {@link #getMaximumValue()}.
     * Subclasses must override this as necessary.
     *
     * @param context  the context of other fields, not null
     * @return the minimum value of the field given the context
     */
    public long getMaximumValue(DateTimeFields2 context) {
        return getMaximumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Is the range of values that the field can take known.
     * <p>
     * This checks if the maximum and minimum values are known (accurately) for all date-times.
     * <p>
     * For example, 'MonthOfYear' always runs from 1 to 12 and is therefore fixed,
     * By comparison, 'DayOfMonth' has a value-range between 28 and 31 depending on the
     * month and year and is therefore not fixed.
     * <p>
     * This implementation uses {@link #getMinimumValue()}, {@link #getLargestMinimumValue()},
     * {@link #getSmallestMaximumValue()} and {@link #getMaximumValue()}.
     *
     * @return true if the set of values is fixed
     */
    public boolean isValueRangeKnown() {
        return getMaximumValue() == getSmallestMaximumValue() &&
                getMinimumValue() == getLargestMinimumValue();
    }

    /**
     * Is the range of values that the field can take known using the specified context.
     * <p>
     * This checks if the maximum and minimum values are known (accurately) for all date-times
     * given the specified context.
     * <p>
     * For example, 'MonthOfYear' always runs from 1 to 12 and is therefore known for all date-times.
     * By comparison, 'DayOfMonth' has a value-range between 28 and 31 depending on the
     * month and year. If both the month and year can be derived from the context,
     * then the value-range is known for all date-times.
     * <p>
     * This implementation uses {@link #getMinimumValue(DateTimeFields2)}, {@link #getLargestMinimumValue()},
     * {@link #getSmallestMaximumValue()} and {@link #getMaximumValue(DateTimeFields2)}.
     *
     * @param context  the context of other fields, not null
     * @return true if the set of values is fixed
     */
    public boolean isValueRangeKnown(DateTimeFields2 context) {
        return getMaximumValue(context) == getSmallestMaximumValue() &&
                getMinimumValue(context) == getLargestMinimumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the range of the values that the field can take.
     * <p>
     * This obtains the value-range, which is the number of valid values in the range.
     * The calculation considers this rule in isolation, returning the maximum possible value-range.
     * <p>
     * For example, 'DayOfMonth' returns 31 which is the maximum value-range for any month.
     * <p>
     * This implementation uses {@link #getMinimumValue()} and
     * {@link #getMaximumValue()}.
     *
     * @return the maximum value for this field
     */
    public long getValueRange() {
        return getMaximumValue() - getMinimumValue() + 1;  // TODO: overflow
    }

    /**
     * Gets the range of the values that the field can take using the specified
     * context to refine the accuracy of the response.
     * <p>
     * This obtains the value-range, which is the number of valid values in the range
     * given the specified context.
     * The result of this method will only be accurate if there is sufficient
     * information in the context to determine the correct value-range.
     * <p>
     * For example, 'DayOfMonth' has a value-range between 28 and 31 depending on the month and year.
     * If both the month and year can be derived from the context, then the value-range
     * returned will be accurate. Otherwise the best estimate will be returned.
     * <p>
     * This implementation uses {@link #getMinimumValue(DateTimeFields2)} and
     * {@link #getMaximumValue(DateTimeFields2)}.
     *
     * @param context  the context of other fields, not null
     * @return the minimum value of the field given the context
     */
    public long getValueRange(DateTimeFields2 context) {
        return getMaximumValue(context) - getMinimumValue(context) + 1;  // TODO: overflow
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the rule defines a value that fits in an {@code int}.
     * <p>
     * This checks that the minimum and maximum values are within the bounds of an {@code int}.
     * <p>
     * For example, the 'MonthOfYear' rule has values from 1 to 12, which fits in an {@code int}.
     * By comparison, 'NanoOfDay' runs from 1 to 86,400,000,000,000 which does not fit in an {@code int}.
     * <p>
     * This implementation uses {@link #getMinimumValue()} and {@link #getMaximumValue()}.
     *
     * @return true if a valid value always fits in an {@code int}
     */
    public boolean isIntValue() {
        return getMinimumValue() >= Integer.MIN_VALUE && getMaximumValue() <= Integer.MAX_VALUE;
    }

    /**
     * Checks if the rule defines a value that fits in an {@code int}, throwing an exception if not.
     * <p>
     * This checks that the minimum and maximum values are within the bounds of an {@code int}.
     * <p>
     * For example, the 'MonthOfYear' rule has values from 1 to 12, which fits in an {@code int}.
     * By comparison, 'NanoOfDay' runs from 1 to 86,400,000,000,000 which does not fit in an {@code int}.
     * <p>
     * This implementation uses {@link #getMinimumValue()} and {@link #getMaximumValue()}.
     *
     * @return true if a valid value always fits in an {@code int}
     * @throws CalendricalException if the value does not fit in an {@code int}
     */
    public void checkIntValue() {
        if (isIntValue() == false) {
            throw new CalendricalException("Rule does not specify an int value: " + getName());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the value is valid for the rule.
     * <p>
     * This checks if the specified value is valid.
     * A valid value is one where the value is greater than or equal to the minimum value
     * and less than or equal to the maximum value.
     * This method considers the rule in isolation, using the outer value-range.
     * <p>
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #getMinimumValue()} and {@link #getMaximumValue()}.
     *
     * @param value  the value to check
     * @return true if the value is valid, false if invalid
     */
    public boolean isValidValue(long value) {
        return (value >= getMinimumValue() && value <= getMaximumValue());
    }

    /**
     * Checks if the value is valid for the rule using the specified
     * context to refine the accuracy of the response.
     * <p>
     * This checks if the specified value is valid.
     * A valid value is one where the value is greater than or equal to the minimum value
     * and less than or equal to the maximum value. This method uses the given context
     * to refine the calculation of the minimum and maximum values.
     * <p>
     * For example, 'DayOfMonth' has a value-range between 28 and 31 depending on the month and year.
     * If both the month and year can be derived from the context, then the value-range
     * used in the check will be accurate, otherwise the best estimate will be used.
     * <p>
     * This implementation uses {@link #getMinimumValue(DateTimeFields2)} and {@link #getMaximumValue(DateTimeFields2)}.
     *
     * @param value  the value to check
     * @param context  the context of other fields, not null
     * @return true if the value is valid, false if invalid
     * 
     */
    public boolean isValidValue(long value, DateTimeFields2 context) {
        return (value >= getMinimumValue(context) && value <= getMaximumValue(context));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the value is valid for the rule, throwing an exception if not.
     * <p>
     * This checks if the specified value is valid.
     * A valid value is one where the value is greater than or equal to the minimum value
     * and less than or equal to the maximum value.
     * This method considers the rule in isolation, using the outer value-range.
     * <p>
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #isValidValue(long)}.
     *
     * @param value  the value to check
     * @throws CalendricalException if the value is invalid
     */
    public void checkValidValue(long value) {
        if (isValidValue(value) == false) {
            throw null; //new IllegalCalendarFieldValueException(this, (int) value, (int) getMinimumValue(), (int) getMaximumValue());  // TODO
        }
    }

    /**
     * Checks if the value is valid for the rule, throwing an exception if not, using
     * the specified context to refine the accuracy of the response.
     * <p>
     * This checks if the specified value is valid.
     * A valid value is one where the value is greater than or equal to the minimum value
     * and less than or equal to the maximum value. This method uses the given context
     * to refine the calculation of the minimum and maximum values.
     * <p>
     * For example, 'DayOfMonth' has a value-range between 28 and 31 depending on the month and year.
     * If both the month and year can be derived from the context, then the value-range
     * used in the check will be accurate, otherwise the best estimate will be used.
     * <p>
     * This implementation uses {@link #isValidValue(long, DateTimeFields2)}.
     *
     * @param value  the value to check
     * @param context  the context of other fields, not null
     * @throws CalendricalException if the value is invalid
     */
    public void checkValidValue(long value, DateTimeFields2 context) {
        if (isValidValue(value, context) == false) {
            throw null; //new IllegalCalendarFieldValueException(this, (int) value, (int) getMinimumValue(), (int) getMaximumValue());  // TODO
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rolls the specified value within the range of this rule.
     * <p>
     * This rolls the value by the specified amount.
     * Rolling is the process of adding or subtracting a value rolling around within the range.
     * For example, 'MonthOfYear' is defined as having 12 months, therefore adding
     * 3 to 11 rolls around to result in 2.
     * The specified value must be valid or an exception will be thrown.
     * <p>
     * This method considers this rule in isolation, using the outer value-range.
     * For example, 'DayOfMonth' will use the outer value-range of 1 to 31 for rolling.
     * <p>
     * This implementation uses {@link #checkValidValue(long)}, {@link #getValueRange()} and {@link #getMinimumValue()}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param value  the initial value to roll, within the valid range
     * @param amountToRollBy  the amount to roll by, positive or negative
     * @return the new rolled value, never null
     * @throws CalendricalException if the value specified is invalid
     */
    public long roll(long value, long amountToRollBy) {
        checkValidValue(value);
        long valueRange = getValueRange();
        long minValue = getMinimumValue();
        return (((value - minValue + (amountToRollBy % valueRange)) + valueRange) % valueRange) + minValue;  // TODO: overflow
    }

    /**
     * Rolls the specified value within the range of this rule.
     * <p>
     * This rolls the value by the specified amount.
     * Rolling is the process of adding or subtracting a value rolling around within the range.
     * For example, 'MonthOfYear' is defined as having 12 months, therefore adding
     * 3 to 11 rolls around to result in 2.
     * The specified value must be valid or an exception will be thrown.
     * <p>
     * This method uses the given context to refine the calculation of the minimum and maximum values.
     * For example, 'DayOfMonth' has a value-range between 28 and 31 depending on the month and year.
     * If both the month and year can be derived from the context, then the value-range
     * used in the roll will be accurate, otherwise the best estimate will be used.
     * <p>
     * This implementation uses {@link #checkValidValue(long, DateTimeFields2)},
     * {@link #getValueRange(DateTimeFields2)} and {@link #getMinimumValue(DateTimeFields2)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param value  the initial value to roll, within the valid range
     * @param amountToRollBy  the amount to roll by, positive or negative
     * @param context  the context of other fields, not null
     * @return the new rolled value, never null
     * @throws CalendricalException if the value specified is invalid
     */
    public long roll(long value, long amountToRollBy, DateTimeFields2 context) {
        checkValidValue(value, context);
        long valueRange = getValueRange(context);
        long minValue = getMinimumValue(context);
        return (((value - minValue + (amountToRollBy % valueRange)) + valueRange) % valueRange) + minValue;  // TODO: overflow
    }

    //-----------------------------------------------------------------------
    /**
     * Converts a value for this field to a fraction between 0 and 1.
     * <p>
     * The resulting fractional value is between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if {@link #isValueRangeFixed()} returns true.
     * The fraction is obtained by calculation from the field range using 9 decimal
     * places and a rounding mode of {@link RoundingMode#FLOOR FLOOR}.
     * <p>
     * For example, the value of 15 for 'SecondOfMinute' would be converted to
     * a fraction of 0.25 as there are 60 seconds in a minute.
     *
     * @param value  the value to convert, valid for this field
     * @return the fractional value of the field, not null
     * @throws CalendricalException if the value is invalid or cannot be converted
     */
    public BigDecimal convertToFraction(long value) {
        if (isValueRangeKnown() == false) {
            throw new CalendricalException("");
//            throw new UnsupportedRuleException("The fractional value of " + getName() +
//                    " cannot be obtained as the range is not fixed", this);  // TODO
        }
        checkValidValue(value);
        BigDecimal value0 = new BigDecimal(value).subtract(new BigDecimal(getMinimumValue()));
        return value0.divide(new BigDecimal(getValueRange()), FRACTION_CONTEXT);
    }

    /**
     * Converts a fraction from 0 to 1 for this field to a value.
     * <p>
     * The fractional value must be between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if {@link #isValueRangeFixed()} returns true.
     * The value is obtained by calculation from the field range and a rounding
     * mode of {@link RoundingMode#FLOOR FLOOR}.
     * <p>
     * For example, the fractional value of 0.25 for 'SecondOfMinute' would be
     * converted to a value of 15 as there are 60 seconds in a minute.
     *
     * @param fraction  the fractional value to convert, not null
     * @return the value of the field, checked for validity
     * @throws CalendricalException if the value is invalid or cannot be converted
     */
    public long convertFromFraction(BigDecimal fraction) {
        if (isValueRangeKnown() == false) {
            throw new CalendricalException("");
//            throw new UnsupportedRuleException("The fractional value of " + getName() +
//                    " cannot be converted as the range is not fixed", this);  // TODO
        }
        BigDecimal value0 = fraction.multiply(new BigDecimal(getValueRange()), VALUE_CONTEXT);
        try {
            long value = value0.add(new BigDecimal(getMinimumValue())).longValueExact();
            checkValidValue(value);
            return value;
        } catch (ArithmeticException ex) {
            throw new CalendricalException("");
//            throw new IllegalCalendarFieldValueException("The fractional value " + fraction + " of " + getName() +
//                    " cannot be converted as it is not in the range 0 (inclusive) to 1 (exclusive)", this);  // TODO
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this rule to another rule.
     * <p>
     * The comparison is based on the period unit followed by the period range
     * followed by the rule name.
     * The period unit is compared first, so MinuteOfHour will be less than
     * HourOfDay, which will be less than DayOfWeek. When the period unit is
     * the same, the period range is compared, so DayOfWeek is less than
     * DayOfMonth, which is less than DayOfYear. Finally, the rule name is compared.
     *
     * @param other  the other rule to compare to, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(DateTimeRule other) {
        if (this.getPeriodUnit() == null) {
            if (other.getPeriodUnit() == null) {
                return getName().compareTo(other.getName());
            } else {
                return 1;
            }
        } else if (other.getPeriodUnit() == null) {
            return -1;
        }
        int cmp = this.getPeriodUnit().compareTo(other.getPeriodUnit());
        if (cmp != 0) {
            return cmp;
        }
        if (this.getPeriodRange() == null) {
            if (other.getPeriodRange() == null) {
                return getName().compareTo(other.getName());
            } else {
                return 1;
            }
        } else if (other.getPeriodRange() == null) {
            return -1;
        }
        cmp = this.getPeriodRange().compareTo(other.getPeriodRange());
        if (cmp != 0) {
            return cmp;
        }
        return getName().compareTo(other.getName());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two rules based on their name.
     *
     * @return true if the rules are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DateTimeRule) {
            return getName().equals(((DateTimeRule) obj).getName());
        }
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * A hash code based on the rule name.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the name of the rule.
     *
     * @return a description of the rule, which is the name, never null
     */
    @Override
    public String toString() {
        return getName();
    }

    //-----------------------------------------------------------------------
    /**
     * A standard date-time rule.
     */
    static final class Standard extends DateTimeRule {

        /**
         * A serialization identifier for this class.
         */
        private static final long serialVersionUID = 1L;

        /** The name of the rule, not null. */
        private final String name;
        /** The period unit, not null. */
        private final PeriodUnit periodUnit;
        /** The period range, not null. */
        private final PeriodUnit periodRange;
        /** The minimum value of the field. */
        private final long minimumValue;
        
        /**
         * Constructor used to create a rule.
         *
         * @param name  the name of the type, not null
         * @param periodUnit  the period unit, may be null
         * @param periodRange  the period range, may be null
         * @param minimumValue  the minimum value
         */
        Standard(String name, PeriodUnit periodUnit, PeriodUnit periodRange, long minimumValue) {
            // avoid possible circular references by using inline NPE checks
            if (name == null) {
                throw new NullPointerException("Name must not be null");
            }
            if (periodUnit == null) {
                throw new NullPointerException("Unit must not be null");
            }
            if (periodRange == null) {
                throw new NullPointerException("Range must not be null");
            }
            if (periodRange.compareTo(periodUnit) <= 0) {
                throw new NullPointerException("Range must be larger than Unit");
            }
            this.name = name;
            this.periodUnit = periodUnit;
            this.periodRange = periodRange;
            this.minimumValue = minimumValue;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public PeriodUnit getPeriodRange() {
            return periodUnit;
        }

        @Override
        public PeriodUnit getPeriodUnit() {
            return periodRange;
        }

        @Override
        public long getMinimumValue() {
            return minimumValue;
        }
    }

}
