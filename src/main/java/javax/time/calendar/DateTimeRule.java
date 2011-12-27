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

import static javax.time.calendar.ISODateTimeRule.EPOCH_DAY;
import static javax.time.calendar.ISODateTimeRule.PACKED_EPOCH_MONTH_DAY;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import javax.time.CalendricalException;
import javax.time.MathUtils;
import javax.time.calendar.format.TextStyle;

/**
 * The rule defining how a measurable field of time operates.
 * <p>
 * Rule implementations define how a field like day-of-month operates.
 * This includes the field name and minimum/maximum values.
 * <p>
 * This class is abstract and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 * Subclasses should implement {@code equals} and {@code hashCode}.
 * The subclass is also fully responsible for serialization as all fields in this class are
 * transient. The subclass must use {@code readResolve} to replace the deserialized
 * class with a valid one created via a constructor.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class DateTimeRule extends CalendricalRule<DateTimeField>
        implements Comparable<DateTimeRule> {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The period unit, not null.
     */
    private final transient PeriodUnit periodUnit;
    /**
     * The period range, not null.
     */
    private final transient PeriodUnit periodRange;
    /**
     * The outer range of values for the rule.
     */
    private final transient DateTimeRuleRange range;
    /**
     * The base rule that this rule relates to.
     */
    private final transient DateTimeRule baseRule;
    /**
     * The normalization rule that this rule relates to.
     */
    private final transient DateTimeRule normalizationRule;

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
     * Checks if the value is valid for the rule, throwing an exception if invalid.
     * <p>
     * This checks that the value is within the valid range of the rule.
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #getValueRange()} and
     * {@link DateTimeRuleRange#isValidValue(long)}.
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
     * For example, the 'MonthOfYear' rule has values from 1 to 12, which
     * fits in an {@code int}. By comparison, 'NanoOfDay' runs from
     * 1 to 86,400,000,000,000 which does not fit in an {@code int}.
     * <p>
     * This method considers the rule in isolation, thus only the
     * outer minimum and maximum range for the field is validated.
     * For example, 'DayOfMonth' has the outer value-range of 1 to 31.
     * <p>
     * This implementation uses {@link #getValueRange()} and
     * {@link DateTimeRuleRange#isValidValue(long)}.
     * Subclasses should not normally override this method.
     *
     * @param value  the value to check
     * @return the valid value as an {@code int}
     * @throws CalendricalException if the value does not fit in an {@code int}
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public int checkValidIntValue(long value) {
        DateTimeRuleRange range = getValueRange();
        if (range.isIntValue() == false) {
            throw new CalendricalRuleException("Rule does not specify an int value: " + getName(), this);
        }
        if (range.isValidValue(value) == false) {
            throw new IllegalCalendarFieldValueException(this, value);
        }
        return (int) value;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation of a value in this rule.
     * <p>
     * This returns the textual representation of the field, such as for day-of-week or month-of-year.
     * If no textual mapping is found then the numeric value is returned.
     * <p>
     * This implementation uses {@link #field} and {@link DateTimeField#getText}.
     * 
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

    //-----------------------------------------------------------------------
    public final DateTimeRuleRange calculateValueRange(long value, DateTimeRule requiredRule) {
        return doValueRangeFromThis(value, requiredRule);
    }

    protected DateTimeRuleRange doValueRangeFromThis(long value, DateTimeRule requiredRule) {
        return requiredRule.doValueRangeFromOther(this, value);
    }

    protected DateTimeRuleRange doValueRangeFromOther(DateTimeRule valueRule, long value) {
        return getValueRange();
    }

    //-----------------------------------------------------------------------
    public final long extractFromTime(long nanoOfDay) {
        return doExtractFromTime(nanoOfDay);
    }

    protected long doExtractFromTime(long nanoOfDay) {
        return Long.MIN_VALUE;
    }

    public final long extractFromPackedDateTime(long packedDate, long nanoOfDay) {
        return doExtractFromPackedDateTime(packedDate, nanoOfDay);
    }

    protected long doExtractFromPackedDateTime(long packedDate, long nanoOfDay) {
        return doExtractFromEpochDayTime(epochDayFromPackedDate(packedDate), nanoOfDay);
    }

    public final long extractFromEpochDayTime(long epochDays, long nanoOfDay) {
        return doExtractFromEpochDayTime(epochDays, nanoOfDay);
    }

    protected long doExtractFromEpochDayTime(long epochDays, long nanoOfDay) {
        return doExtractFromEpochDayTime(packedDateFromEpochDay(epochDays), nanoOfDay);
    }

    //-----------------------------------------------------------------------
    public final long extractFromValue(DateTimeRule valueRule, long value) {
        ISOChronology.checkNotNull(valueRule, "DateTimeRule must not be null");
        // check if this is the desired output already
        if (this.equals(valueRule)) {
            return value;
        }
        if (EPOCH_DAY.equals(valueRule)) {
            return doExtractFromEpochDayTime(value, Long.MIN_VALUE);
        }
        if (PACKED_EPOCH_MONTH_DAY.equals(valueRule)) {
            return doExtractFromPackedDateTime(value, Long.MIN_VALUE);
        }
        return doExtractFromValue(valueRule, value);
    }

    protected long doExtractFromValue(DateTimeRule valueRule, long value) {
        // check conversion is feasible and permitted
        if (getBaseRule().equals(valueRule.getBaseRule()) &&
                valueRule.comparePeriodUnit(this) <= 0 &&
                valueRule.comparePeriodRange(this) >= 0) {
            return defaultExtractFromValue(valueRule, value);
        }
        return Long.MIN_VALUE;
    }

    private long defaultExtractFromValue(DateTimeRule valueRule, long value) {
        // TODO: doesn't handle DAYS well, as DAYS are not a multiple of NANOS
        long bottomConversion = getPeriodUnit().toEquivalent(valueRule.getPeriodUnit());
        if (bottomConversion < 0) {
            return Long.MIN_VALUE;
        }
        long period = valueRule.convertToPeriod(value);
        period = MathUtils.floorDiv(period, bottomConversion);
        if (getPeriodRange() != null && valueRule.comparePeriodRange(this) != 0) {
//                if (periodRange.equals(DAYS)) {  // TODO: hack
//                    periodRange = _24_HOURS;
//                }
            long topConversion = getPeriodRange().toEquivalent(getPeriodUnit());
            if (topConversion < 0) {
                return Long.MIN_VALUE;
            }
            period = MathUtils.floorMod(period, topConversion);
        }
        return convertFromPeriod(period);
    }

    //-----------------------------------------------------------------------
//    public final long setIntoPackedDateTime(long packedDate, long nanoOfDay) {
//        return doSetIntoPackedDateTime(packedDate, nanoOfDay);
//    }
//
//    protected long doSetIntoPackedDateTime(long packedDate, long nanoOfDay) {
//        return doExtractFromEpochDayTime(epochDayFromPackedDate(packedDate), nanoOfDay);
//    }

    public final long[] setIntoEpochDayTime(long newValue, long baseEpochDay, long baseNanoOfDay) {
        return new long[] {doSetIntoEpochDay(newValue, baseEpochDay, baseNanoOfDay),
        				doSetIntoTime(newValue, baseEpochDay, baseNanoOfDay)};
    }

    protected long doSetIntoEpochDay(long newValue, long baseEpochDay, long baseNanoOfDay) {
        return baseEpochDay;
    }

    protected long doSetIntoTime(long newValue, long baseEpochDay, long baseNanoOfDay) {
        return baseNanoOfDay;
    }

    public final long setIntoValue(long newValue, DateTimeRule baseRule, long baseValue) {
        return doSetIntoValue(newValue, baseRule, baseValue);
    }

    protected long doSetIntoValue(long newValue, DateTimeRule baseRule, long baseValue) {
        long curValue = extractFromValue(baseRule, baseValue);
        long bottomConversion = getPeriodUnit().toEquivalent(baseRule.getPeriodUnit());
        long change = convertToPeriod(newValue) - convertToPeriod(curValue);
        return baseValue + change * bottomConversion;
    }

    //-----------------------------------------------------------------------
    protected static long epochDayFromPackedDate(long pemd) {
        long dom = pemd & 31;
        long em = (pemd / 32);
        long year = (em / 12) + 1970;
        long y = year;
        long m = (em % 12) + 1;
        long total = 0;
        total += 365 * y;
        if (y >= 0) {
            total += (y + 3) / 4 - (y + 99) / 100 + (y + 399) / 400;
        } else {
            total -= y / -4 - y / -100 + y / -400;
        }
        total += ((367 * m - 362) / 12);
        total += dom;
        if (m > 2) {
            total--;
            if (ISOChronology.isLeapYear(year) == false) {
                total--;
            }
        }
        return total - ISOChronology.DAYS_0000_TO_1970;
    }

    protected static long packedDateFromEpochDay(long ed) {
        // find the march-based year
        long zeroDay = ed + ISOChronology.DAYS_0000_TO_1970 - 60;  // adjust to 0000-03-01 so leap day is at end of four year cycle
        long adjust = 0;
        if (zeroDay < 0) {
            // adjust negative years to positive for calculation
            long adjustCycles = (zeroDay + 1) / ISOChronology.DAYS_PER_CYCLE - 1;
            adjust = adjustCycles * 400;
            zeroDay += -adjustCycles * ISOChronology.DAYS_PER_CYCLE;
        }
        long yearEst = (400 * zeroDay + 591) / ISOChronology.DAYS_PER_CYCLE;
        long doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        if (doyEst < 0) {
            // fix estimate
            yearEst--;
            doyEst = zeroDay - (365 * yearEst + yearEst / 4 - yearEst / 100 + yearEst / 400);
        }
        yearEst += adjust;  // reset any negative year
        int marchDoy0 = (int) doyEst;
        
        // convert march-based values back to january-based
        int marchMonth0 = (marchDoy0 * 5 + 2) / 153;
        int month = (marchMonth0 + 2) % 12 + 1;
        int dom = marchDoy0 - (marchMonth0 * 306 + 5) / 10 + 1;
        long year = yearEst + marchMonth0 / 10;
        
        // pack
        return packPemd(year, month, dom);
    }

    protected static long packPemd(long year, int month, int dom) {
        return packPemd((year - 1970) * 12 + (month - 1), dom);
    }

    protected static long packPemd(long em, int dom) {
        return (em << 5) + dom;
    }

    //-----------------------------------------------------------------------
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
     * @param engine  the engine to process, not null
     */
    protected void normalize(CalendricalEngine engine) {
        // override to normalize fields to objects
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

    /**
     * Checks if this rule is equal to another rule.
     * <p>
     * The comparison is based on the name and class.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other rule
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            DateTimeRule other = (DateTimeRule) obj;
            return getPeriodUnit().equals(other.getPeriodUnit()) &&
                    getPeriodRange().equals(other.getPeriodRange()) &&
                    getType().equals(other.getType()) &&
                    getName().equals(other.getName());
        }
        return false;
    }

    /**
     * A hash code for this rule.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return getClass().hashCode() ^ getPeriodUnit().hashCode() ^
                (getPeriodRange() == null ? 0 : getPeriodRange().hashCode()) ^
                getType().hashCode() ^ getName().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Compares the period unit of this rule to another.
     * 
     * @param other  the other rule, not null
     * @return the comparator result
     */
    final int comparePeriodUnit(DateTimeRule other) {
        return getPeriodUnit().compareTo(other.getPeriodUnit());
    }

    /**
     * Compares the period range of this rule to another handling null as forever.
     * 
     * @param other  the other rule, not null
     * @return the comparator result
     */
    final int comparePeriodRange(DateTimeRule other) {
        return comparePeriodUnits(getPeriodRange(), other.getPeriodRange());
    }

    /**
     * Compares the period range of this rule to another handling null as forever.
     * 
     * @param unit1  the first unit, null means forever
     * @param unit2  the second unit, null means forever
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
