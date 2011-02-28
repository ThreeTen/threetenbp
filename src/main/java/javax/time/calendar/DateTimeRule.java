/*
 * Copyright (c) 2007-2011 Stephen Colebourne & Michael Nascimento Santos
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import javax.time.CalendricalException;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * The rule defining how a measurable field of time operates.
 * <p>
 * Rule implementations define how a field like day-of-month operates.
 * This includes the field name and minimum/maximum values.
 * <p>
 * This class is abstract and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe and must
 * ensure serialization works correctly.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class DateTimeRule extends CalendricalRule<DateTimeField> {
    // TODO: broken serialization

    /** A serialization identifier for this class. */
    private static final long serialVersionUID = 1L;

    /** The minimum value for the field. */
    private final long minimumValue;
    /** The maximum value for the field. */
    private final long maximumValue;

    /**
     * Constructor.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param periodUnit  the period unit, not null
     * @param periodRange  the period range, not null
     * @param minimumValue  the minimum value
     * @param maximumValue  the minimum value
     */
    protected DateTimeRule(
            Chronology chronology,
            String name,
            PeriodUnit periodUnit,
            PeriodUnit periodRange,
            long minimumValue,
            long maximumValue) {
        super(DateTimeField.class, chronology, name, periodUnit, periodRange);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the rule defines values that fit in an {@code int}.
     * <p>
     * This checks that all valid values are within the bounds of an {@code int}.
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
     * Checks if the rule defines values that fit in an {@code int}, throwing an exception if not.
     * <p>
     * This checks that all valid values are within the bounds of an {@code int}.
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
            throw new CalendricalRuleException("Rule does not specify an int value: " + getName(), this);
        }
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
     * @param value  the value to check
     * @return true if the value is valid
     */
    public boolean isValidValue(long value) {
        return (value >= getMinimumValue() && value <= getMaximumValue());
    }

    /**
     * Checks if the value is valid for the rule, throwing an exception if invalid.
     * <p>
     * This checks that the value is within the valid range of the rule.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #isValidValue(long)}.
     *
     * @param value  the value to check
     * @return the valid value
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public long checkValidValue(long value) {
        if (isValidValue(value) == false) {
            throw new IllegalCalendarFieldValueException(this, value, getMinimumValue(), getMaximumValue());
        }
        return value;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the value is valid for the rule and that the rule defines
     * values that fit in an {@code int}.
     * <p>
     * This checks that the value is within the valid range of the rule and
     * that all valid values are within the bounds of an {@code int}.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #isIntValue()} and {@link #isValidValue(long)}.
     *
     * @param value  the value to check
     * @return true if the value is valid and fits in an {@code int}
     */
    public boolean isValidIntValue(long value) {
        return isIntValue() && isValidValue(value);
    }

    /**
     * Checks if the value is valid for the rule and that the rule defines
     * values that fit in an {@code int}, throwing an exception if not.
     * <p>
     * This checks that the value is within the valid range of the rule and
     * that all valid values are within the bounds of an {@code int}.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #checkIntValue()} and {@link #checkValidValue(long)}.
     *
     * @param value  the value to check
     * @return the valid value as an {@code int}
     * @throws CalendricalException if the value does not fit in an {@code int}
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public int checkValidIntValue(long value) {
        checkIntValue();
        return (int) checkValidValue(value);
    }

    //-----------------------------------------------------------------------
    /**
     * Is the set of values, from the minimum value to the maximum, a fixed
     * set, or does it vary according to other fields.
     *
     * @return true if the set of values is fixed
     */
    public boolean isFixedValueSet() {
        return getMaximumValue() == getSmallestMaximumValue() &&
                getMinimumValue() == getLargestMinimumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the minimum value that the field can take.
     *
     * @return the minimum value for this field
     */
    public long getMinimumValue() {
        return minimumValue;
    }

    /**
     * Gets the largest possible minimum value that the field can take.
     * <p>
     * The default implementation returns {@link #getMinimumValue()}.
     * Subclasses must override this as necessary.
     *
     * @return the largest possible minimum value for this field
     */
    public long getLargestMinimumValue() {
        return getMinimumValue();
    }

    /**
     * Gets the minimum value that the field can take using the specified
     * calendrical information to refine the accuracy of the response.
     * <p>
     * The result of this method may still be inaccurate, if there is insufficient
     * information in the calendrical.
     * <p>
     * The default implementation returns {@link #getMinimumValue()}.
     * Subclasses must override this as necessary.
     *
     * @param calendrical  context calendrical, not null
     * @return the minimum value of the field given the context
     */
    public long getMinimumValue(Calendrical calendrical) {
        return getMinimumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the maximum value that the field can take.
     *
     * @return the maximum value for this field
     */
    public long getMaximumValue() {
        return maximumValue;
    }

    /**
     * Gets the smallest possible maximum value that the field can take.
     * <p>
     * The default implementation returns {@link #getMaximumValue()}.
     * Subclasses must override this as necessary.
     *
     * @return the smallest possible maximum value for this field
     */
    public long getSmallestMaximumValue() {
        return getMaximumValue();
    }

    /**
     * Gets the minimum value that the field can take using the specified
     * calendrical information to refine the accuracy of the response.
     * <p>
     * The result of this method will still be inaccurate if there is insufficient
     * information in the calendrical.
     * <p>
     * For example, if this field is the ISO day-of-month field, then the number
     * of days in the month varies depending on the month and year. If both the
     * month and year can be derived from the calendrical, then the maximum value
     * returned will be accurate. Otherwise the 'best guess' value from
     * {@link #getMaximumValue()} will be returned.
     * <p>
     * The default implementation returns {@link #getMaximumValue()}.
     * Subclasses must override this as necessary.
     *
     * @param calendrical  context calendrical, not null
     * @return the minimum value of the field given the context
     */
    public long getMaximumValue(Calendrical calendrical) {
        return getMaximumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation of a value in this rule.
     * <p>
     * This returns the textual representation of the field, such as for day-of-week or month-of-year.
     * If no textual mapping is found then the {@link #getValue() numeric value} is returned.
     *
     * @param value  the value to convert to text, must be valid for the rule
     * @param textStyle  the text style, not null
     * @param locale  the locale to use, not null
     * @return the textual representation of the field, not null
     */
    public String getText(long value, TextStyle textStyle, Locale locale) {
        return field(value).getText(textStyle, locale);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts a value for this field to a fraction between 0 and 1.
     * <p>
     * The fractional value is between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if {@link #isFixedValueSet()} returns true.
     * The fraction is obtained by calculation from the field range using 9 decimal
     * places and a rounding mode of {@link RoundingMode#FLOOR FLOOR}.
     * The calculation is inaccurate if the values do not run continuously from smallest to largest.
     * <p>
     * For example, the second-of-minute value of 15 would be returned as 0.25,
     * assuming the standard definition of 60 seconds in a minute.
     *
     * @param value  the value to convert, must be valid for this rule
     * @return the value as a fraction within the range, from 0 to 1, not null
     * @throws CalendricalRuleException if the value cannot be converted to a fraction
     */
    public BigDecimal convertToFraction(long value) {
        if (isFixedValueSet() == false) {
            throw new CalendricalRuleException("The fractional value of " + getName() +
                    " cannot be obtained as the range is not fixed", this);
        }
        checkValidValue(value);
        BigDecimal min = BigDecimal.valueOf(getMinimumValue());
        BigDecimal range = BigDecimal.valueOf(getMaximumValue()).subtract(min).add(BigDecimal.ONE);
        BigDecimal valueBD = BigDecimal.valueOf(value).subtract(min);
        BigDecimal fraction = valueBD.divide(range, 9, RoundingMode.FLOOR);
        // stripTrailingZeros bug
        return fraction.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : fraction.stripTrailingZeros();
    }

    /**
     * Converts a fraction from 0 to 1 for this field to a value.
     * <p>
     * The fractional value must be between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if {@link #isFixedValueSet()} returns true.
     * The value is obtained by calculation from the field range and a rounding
     * mode of {@link RoundingMode#FLOOR FLOOR}.
     * The calculation is inaccurate if the values do not run continuously from smallest to largest.
     * <p>
     * For example, the fractional second-of-minute of 0.25 would be converted to 15,
     * assuming the standard definition of 60 seconds in a minute.
     *
     * @param fraction  the fraction to convert, not null
     * @return the value of the field, valid for this rule
     * @throws UnsupportedRuleException if the value cannot be converted
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public long convertFromFraction(BigDecimal fraction) {
        if (isFixedValueSet() == false) {
            throw new UnsupportedRuleException("The fractional value of " + getName() +
                    " cannot be converted as the range is not fixed", this);
        }
        BigDecimal min = BigDecimal.valueOf(getMinimumValue());
        BigDecimal range = BigDecimal.valueOf(getMaximumValue()).subtract(min).add(BigDecimal.ONE);
        BigDecimal valueBD = fraction.multiply(range).setScale(0, RoundingMode.FLOOR).add(min);
        long value = valueBD.longValueExact();
        checkValidValue(value);
        return value;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a field for this rule.
     * 
     * @param value  the value to create the field for, may be outside the valid range for the rule
     * @return the created field, not null
     */
    public DateTimeField field(long value) {
       return DateTimeField.of(this, value); 
    }

}
