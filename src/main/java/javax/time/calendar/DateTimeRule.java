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
public abstract class DateTimeRule extends CalendricalRule<DateTimeField>
        implements Comparable<DateTimeRule> {
    // TODO: broken serialization

    /** A serialization identifier for this class. */
    private static final long serialVersionUID = 1L;

    /** The period unit, not null. */
    private final PeriodUnit periodUnit;
    /** The period range, not null. */
    private final PeriodUnit periodRange;
    /** The outer range of values for the rule. */
    private final DateTimeRuleRange range;
    /** The base rule that this rule relates to. */
    private final DateTimeRule baseRule;
    /** The normalization rule that this rule relates to. */
    private final DateTimeRule normalizationRule;

    /**
     * Creates an instance specifying the minimum and maximum value of the rule.
     *
     * @param name  the name of the type, not null
     * @param periodUnit  the period unit, not null
     * @param periodRange  the period range, not null
     * @param minimumValue  the minimum value
     * @param maximumValue  the minimum value
     */
    protected DateTimeRule(
            String name,
            PeriodUnit periodUnit,
            PeriodUnit periodRange,
            long minimumValue,
            long maximumValue,
            DateTimeRule parentRule) {
        this(name, periodUnit, periodRange, DateTimeRuleRange.of(minimumValue, maximumValue), parentRule);
    }

    /**
     * Creates an instance specifying the outer range of value for the rule
     * and the rule that this is related to.
     * <p>
     * The parent rule is examined and the {@link #getBaseRule() base rule} and
     * {@link #getNormalizationRule() normalization rule} set from it.
     *
     * @param name  the name of the type, not null
     * @param periodUnit  the period unit, not null
     * @param periodRange  the period range, not null
     * @param ruleRange  the range, not null
     * @param parentRule  the parent rule that this rule relates to, null
     *  if this rule does not relate to another rule
     */
    protected DateTimeRule(
            String name,
            PeriodUnit periodUnit,
            PeriodUnit periodRange,
            DateTimeRuleRange ruleRange,
            DateTimeRule parentRule) {
        super(DateTimeField.class, name);
        ISOChronology.checkNotNull(periodUnit, "Period unit must not be null");
        ISOChronology.checkNotNull(ruleRange, "DateTimeRuleRange must not be null");
        this.periodUnit = periodUnit;
        this.periodRange = periodRange;
        this.range = ruleRange;
        DateTimeRule baseRule = this;
        DateTimeRule normalizationRule = this;
        if (parentRule != null) {
            baseRule = parentRule.getBaseRule();
            DateTimeRule parentNormalizationRule = parentRule.getNormalizationRule();
            if (parentNormalizationRule.getPeriodUnit().equals(periodUnit) &&
                    comparePeriodRange(parentNormalizationRule) == 0) {
                normalizationRule = parentNormalizationRule;
            } else {
                DateTimeRuleGroup.of(baseRule).registerRelatedRule(this);
            }
        }
        this.baseRule = baseRule;
        this.normalizationRule = normalizationRule;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unit that the rule is measured in.
     * <p>
     * The unit of the rule is the period that varies within the range.
     * For example, in the rule 'MonthOfYear', the unit is 'Months'.
     * See also {@link #getPeriodRange()}.
     *
     * @return the period unit defining the unit of the rule, not null
     */
    public PeriodUnit getPeriodUnit() {
        return periodUnit;
    }

    /**
     * Gets the range that the rule is bound by.
     * <p>
     * The range of the rule is the period that the field varies within.
     * For example, in the rule 'MonthOfYear', the range is 'Years'.
     * See also {@link #getPeriodUnit()}.
     * <p>
     * A range of null means "forever". For example, the 'Year' rule
     * is shorthand for 'YearOfForever'. It therefore has a unit of 'Years'
     * and a range of "forever" (null).
     *
     * @return the period unit defining the range of the rule, null means an unbound range (forever)
     */
    public PeriodUnit getPeriodRange() {
        return periodRange;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the valid range of values for this rule.
     * <p>
     * For example, the 'DayOfMonth' rule has values from 1 to between 28 and 31.
     *
     * @return the valid range of values, not null
     */
    public DateTimeRuleRange getValueRange() {
        return range;
    }

    /**
     * Gets the valid range of values for this rule using the specified
     * calendrical to refine the accuracy of the response.
     * <p>
     * This uses the calendrical to return a more accurate range of valid values.
     * The result of this method may still be inaccurate, if there is insufficient
     * information in the calendrical.
     * For example, the 'DayOfMonth' rule has values from 1 to between 28 and 31.
     * If the calendrical specifies 'February', then the returned range will be from
     * 1 to between 28 and 29. If the calendrical specifies 'February' in a leap year,
     * then the returned range will be from 1 to 29 exactly.
     * <p>
     * The default implementation returns {@link #getValueRange()}.
     * Subclasses must override this as necessary.
     *
     * @param calendrical  context calendrical, not null
     * @return the valid range of values given the calendrical context, not null
     */
    public DateTimeRuleRange getValueRange(Calendrical calendrical) {
        ISOChronology.checkNotNull(calendrical, "Calendrical must not be null");
        return getValueRange();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the rule defines values that fit in an {@code int}, throwing an exception if not.
     * <p>
     * This checks that all valid values are within the bounds of an {@code int}.
     * <p>
     * For example, the 'MonthOfYear' rule has values from 1 to 12, which fits in an {@code int}.
     * By comparison, 'NanoOfDay' runs from 1 to 86,400,000,000,000 which does not fit in an {@code int}.
     * <p>
     * This implementation uses {@link DateTimeRuleRange#isIntValue()}.
     * Subclasses should not normally override this method.
     *
     * @throws CalendricalException if the value does not fit in an {@code int}
     */
    public void checkIntValue() {
        DateTimeRuleRange range = getValueRange();
        if (range.isIntValue() == false) {
            throw new CalendricalRuleException("Rule does not specify an int value: " + getName(), this);
        }
    }

    /**
     * Checks if the value is valid for the rule, throwing an exception if invalid.
     * <p>
     * This checks that the value is within the valid range of the rule.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link DateTimeRuleRange#isValidValue(long)}.
     * Subclasses should not normally override this method..
     *
     * @param value  the value to check
     * @return the valid value
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public long checkValidValue(long value) {
        DateTimeRuleRange range = getValueRange();
        if (range.isValidValue(value) == false) {
            throw new IllegalCalendarFieldValueException(this, value);
        }
        return value;
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
     * Subclasses should not normally override this method.
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
     * Gets the textual representation of a value in this rule.
     * <p>
     * This returns the textual representation of the field, such as for day-of-week or month-of-year.
     * If no textual mapping is found then the numeric value is returned.
     * <p>
     * This implementation uses {@link #field(long)} and {@link DateTimeField#getText(TextStyle, Locale)}.
     * Subclasses should not normally override this method.
     *
     * @param value  the value to convert to text, must be valid for the rule
     * @param textStyle  the text style, not null
     * @param locale  the locale to use, not null
     * @return the textual representation of the field, not null
     */
    public String getText(long value, TextStyle textStyle, Locale locale) {
        return field(value).getText(textStyle, locale);
    }

    //-------------------------------------------------------------------------
    /**
     * Override point to allow the rule to normalize the fields in the merger.
     * <p>
     * This is part of the merge process, which exists to extract the maximum
     * information possible from a set calendrical data. The merger will automatically
     * normalize fields using the {@link #getNormalizationRule() normalization rule}.
     * It will then merge fields with the same {@link #getBaseRule() base rule}.
     * This method is then called to combine the resulting fields into objects like
     * {@code LocalDate} or {@code LocalTime}.
     * <p>
     * A typical implementation will extract one or more fields, combine them to
     * form an object, and then store the object back into the merger.
     * The fields that were processed should also be removed from the merger.
     * Implementations should avoid throwing exceptions and should add an error to the merger instead.
     * <p>
     * This implementation does nothing.
     * 
     * @param merger  the merger to process, not null
     */
    protected void normalize(CalendricalNormalizer merger) {
        // override to normalize fields to objects
    }

    /**
     * Override point to derive the field for this rule from the merger.
     * <p>
     * This is part of the merge process, which exists to extract the maximum
     * information possible from a set calendrical data. Before this method is
     * called, the merger will be {@link #normalize normalized}.
     * This method is then called to extract information from the objects of the normalized form.
     * <p>
     * A typical implementation will check the objects and determine if the field can be
     * derived from them. For example, the year can be derived from a {@code LocalDate}.
     * In general, only the objects should be used for derivation, as any remaining fields
     * are handled by the merger.
     * <p>
     * Implementations should avoid throwing exceptions and should avoid adding errors to the merger.
     * It is strongly recommended to treat the merger as immutable.
     * <p>
     * This implementation uses {@link CalendricalNormalizer#getFieldDerived}
     * 
     * @param merger  the merger to process, not null
     * @return the derived field, null if unable to derive
     */
    @Override
    protected DateTimeField deriveFrom(CalendricalNormalizer merger) {
        // override to derive field from objects
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the base rule that this rule is related to.
     * <p>
     * Each rule typically has a connection to another rule.
     * For example, the 'SecondOfMinute' and 'MinuteOfHour' rules are related
     * and can be combined. The base rule is the rule that encompasses a group
     * of related rules. For example, 'NanoOfDay' is the rule that encompasses
     * all the major time rules.
     *
     * @return the base rule, {@code this} if this is the base rule, not null
     */
    public DateTimeRule getBaseRule() {
        return baseRule;
    }

    /**
     * Gets a more fundamental rule that this rule is equivalent to.
     * <p>
     * A normalization rule is another, more fundamental, rule that represents
     * the same concept as this rule, meaning that it can be normalized.
     * The rule will always have the same period unit and period range, but not
     * all rules with the same unit and range are necessarily normalizable.
     * To be normalizable, the rules must also share a common definition as to
     * the start of their {@link #convertToPeriod(long) period}.
     * <p>
     * For example, both 'ClockHourOfDay' and 'HourOfDay' represent the hour-of-day
     * concept based on a common definition of period after midnight.
     * 'HourOfDay' is the more fundamental definition. Thus, 'ClockHourOfDay'
     * has a normalization rule of 'HourofDay', and 'HourofDay' has a null
     * normalization rule.
     *
     * @return the base rule, {@code this} if this is the most fundamental version
     *  of this concept, not null
     */
    public DateTimeRule getNormalizationRule() {
        return normalizationRule;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the equivalent period for a value in this rule.
     * <p>
     * The period is the period that the value is after the start of the range.
     * This essentially converts the value to a simple sequential zero-based value.
     * The method will handle out of range values wherever possible.
     * <p>
     * For example, consider a day-of-year running from 1 to 365/366.
     * The equivalent period will run from 0 to 364/365, as day 1 requires adding
     * zero days to the start of the year.
     * <p>
     * This implementation simply returns the value as the period, which is suitable
     * for any sequential zero-based field, such as minute-of-hour.
     * Subclasses must override this as necessary.
     *
     * @param value  the value of this rule, may be outside the value range for the rule
     * @return the period equivalent to the value of this rule in units of this rule, not null
     * @throws CalendricalException if a suitable conversion is not possible
     */
    public long convertToPeriod(long value) {
        return value;
    }

    /**
     * Gets the equivalent value for a period measured in units of this rule.
     * <p>
     * The period is the period that the value is after the start of the range.
     * This essentially converts the value from a simple sequential zero-based
     * amount to the potentially complex value.
     * The method will handle out of range values wherever possible.
     * <p>
     * For example, consider a day-of-year running from 1 to 365/366.
     * The equivalent period will run from 0 to 364/365, as day 1 requires adding
     * zero days to the start of the year.
     * <p>
     * This implementation simply returns the period as the value, which is suitable
     * for any sequential zero-based field, such as minute-of-hour.
     * Subclasses must override this as necessary.
     *
     * @param period  the period measured in units of this rule, positive or negative
     * @return the value of this rule, potentially out of range, not null
     * @throws CalendricalException if a suitable conversion is not possible
     */
    public long convertFromPeriod(long period) {
        return period;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts a value for this field to a fraction between 0 and 1.
     * <p>
     * The fractional value is between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if the {@link #getValueRange() value range} is fixed.
     * The fraction is obtained by calculation from the field range using 9 decimal
     * places and a rounding mode of {@link RoundingMode#FLOOR FLOOR}.
     * The calculation is inaccurate if the values do not run continuously from smallest to largest.
     * <p>
     * For example, the second-of-minute value of 15 would be returned as 0.25,
     * assuming the standard definition of 60 seconds in a minute.
     * <p>
     * Subclasses should not normally override this method.
     *
     * @param value  the value to convert, must be valid for this rule
     * @return the value as a fraction within the range, from 0 to 1, not null
     * @throws CalendricalRuleException if the value cannot be converted to a fraction
     */
    public BigDecimal convertToFraction(long value) {
        DateTimeRuleRange range = getValueRange();
        if (range.isFixed() == false) {
            throw new CalendricalRuleException("The fractional value of " + getName() +
                    " cannot be obtained as the range is not fixed", this);
        }
        checkValidValue(value);
        BigDecimal minBD = BigDecimal.valueOf(range.getMinimum());
        BigDecimal rangeBD = BigDecimal.valueOf(range.getMaximum()).subtract(minBD).add(BigDecimal.ONE);
        BigDecimal valueBD = BigDecimal.valueOf(value).subtract(minBD);
        BigDecimal fraction = valueBD.divide(rangeBD, 9, RoundingMode.FLOOR);
        // stripTrailingZeros bug
        return fraction.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : fraction.stripTrailingZeros();
    }

    /**
     * Converts a fraction from 0 to 1 for this field to a value.
     * <p>
     * The fractional value must be between 0 (inclusive) and 1 (exclusive).
     * It can only be returned if the {@link #getValueRange() value range} is fixed.
     * The value is obtained by calculation from the field range and a rounding
     * mode of {@link RoundingMode#FLOOR FLOOR}.
     * The calculation is inaccurate if the values do not run continuously from smallest to largest.
     * <p>
     * For example, the fractional second-of-minute of 0.25 would be converted to 15,
     * assuming the standard definition of 60 seconds in a minute.
     * <p>
     * Subclasses should not normally override this method.
     *
     * @param fraction  the fraction to convert, not null
     * @return the value of the field, valid for this rule
     * @throws CalendricalException if the value cannot be converted
     */
    public long convertFromFraction(BigDecimal fraction) {
        DateTimeRuleRange range = getValueRange();
        if (range.isFixed() == false) {
            throw new CalendricalRuleException("The fractional value of " + getName() +
                    " cannot be converted as the range is not fixed", this);
        }
        BigDecimal minBD = BigDecimal.valueOf(range.getMinimum());
        BigDecimal rangeBD = BigDecimal.valueOf(range.getMaximum()).subtract(minBD).add(BigDecimal.ONE);
        BigDecimal valueBD = fraction.multiply(rangeBD).setScale(0, RoundingMode.FLOOR).add(minBD);
        long value = valueBD.longValueExact();
        checkValidValue(value);
        return value;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a field for this rule.
     * <p>
     * Subclasses should not normally override this method.
     * 
     * @param value  the value to create the field for, may be outside the valid range for the rule
     * @return the created field, not null
     */
    public DateTimeField field(long value) {
       return DateTimeField.of(this, value);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code CalendricalRule} to another.
     * <p>
     * The comparison is based on the period unit followed by the period range
     * followed by the rule name.
     * The period unit is compared first, so MinuteOfHour will be less than
     * HourOfDay, which will be less than DayOfWeek. When the period unit is
     * the same, the period range is compared, so DayOfWeek is less than
     * DayOfMonth, which is less than DayOfYear. Finally, the rule name is compared.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     */
    public int compareTo(DateTimeRule other) {
        int cmp = comparePeriodUnit(other);
        if (cmp == 0) {
            cmp = comparePeriodRange(other);
            if (cmp == 0) {
                cmp = getName().compareTo(other.getName());
            }
        }
        return cmp;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares the period unit of this rule to another.
     * 
     * @param other the other rule, not null
     * @return the comparator result
     */
    final int comparePeriodUnit(DateTimeRule other) {
        return getPeriodUnit().compareTo(other.getPeriodUnit());
    }

    /**
     * Compares the period range of this rule to another handling null as forever.
     * 
     * @param other the other rule, not null
     * @return the comparator result
     */
    final int comparePeriodRange(DateTimeRule other) {
        return comparePeriodUnits(getPeriodRange(), other.getPeriodRange());
    }

    /**
     * Compares the period range of this rule to another handling null as forever.
     * 
     * @param other the other rule, not null
     * @return the comparator result
     */
    static final int comparePeriodUnits(PeriodUnit unit1, PeriodUnit unit2) {
        if (unit1 == null) {
            return unit2 == null ? 0 : 1;
        }
        if (unit2 == null) {
            return -1;
        }
       return unit1.compareTo(unit2);
    }

}
