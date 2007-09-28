/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
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

import javax.time.duration.DurationFieldRule;
import javax.time.duration.Durational;

/**
 * The rule defining how a measurable field of time operates.
 * <p>
 * Time field rule implementations define how a field like 'day of month' operates.
 * This includes the field name and minimum/maximum values.
 * <p>
 * TimeFieldRule is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public abstract class TimeFieldRule {

    /** The name of the rule, not null. */
    private final String name;
    /** The duration unit rule, not null. */
    private final DurationFieldRule durationUnitRule;
    /** The duration range rule, not null. */
    private final DurationFieldRule durationRangeRule;
    /** The minimum value for the field. */
    private final int minimumValue;
    /** The maximum value for the field. */
    private final int maximumValue;

    /**
     * Constructor.
     *
     * @param name  the name of the type, not null
     * @param durationUnitRule  the duration unit rule, not null
     * @param durationRangeRule  the duration range rule, not null
     * @param minimumValue  the minimum value
     * @param maximumValue  the minimum value
     */
    protected TimeFieldRule(
            String name,
            DurationFieldRule durationUnitRule,
            DurationFieldRule durationRangeRule,
            int minimumValue,
            int maximumValue) {
        super();
        // TODO: Validate not null
        this.name = name;
        this.durationUnitRule = durationUnitRule;
        this.durationRangeRule = durationRangeRule;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the time field type.
     * <p>
     * Subclasses should use the form 'UnitOfRange' whenever possible.
     *
     * @return the name of the time field type, never null
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the unit duration rule, which the element which alters within the range.
     * <p>
     * In the phrase 'hour of day', the unit is the hour.
     *
     * @return the rule for the unit duration, never null
     */
    public DurationFieldRule getDurationUnit() {
        return durationUnitRule;
    }

    /**
     * Gets the range duration rule, which the field is bound by.
     * <p>
     * In the phrase 'hour of day', the range is the day.
     *
     * @return the rule for the range duration, never null
     */
    public DurationFieldRule getDurationRange() {
        return durationRangeRule;
    }

//    /**
//     * Creates a new instance of the associated time amount using the
//     * specified value.
//     *
//     * @param value  the value of the field to represent
//     * @return the time amount, never null
//     */
//    public abstract TimeField<U, R> createInstance(int value);

    //-----------------------------------------------------------------------
    /**
     * Gets the value of this field.
     *
     * @param epochDuration  the duration from the epoch, not null
     * @return the value of the field
     * @throws NullPointerException if the duration is null
     * @throws UnsupportedOperationException if the field cannot be calculated
     */
    public int getValue(Durational epochDuration) {
        return epochDuration.getDurationalState().getDerived(getDurationUnit());
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
    public int getMinimumValue() {
        return minimumValue;
    }

    /**
     * Gets the largest possible minimum value that the field can take.
     *
     * @return the largest possible minimum value for this field
     */
    public int getLargestMinimumValue() {
        return getMinimumValue();
    }

    /**
     * Gets the minimum value that the field can take using the specified
     * calendrical information to refine the accuracy of the response.
     *
     * @param calendricalContext  context datetime, null returns getMinimumValue()
     * @return the minimum value of the field given the context
     */
    public int getMinimumValue(Calendrical calendricalContext) {
        return getMinimumValue();
    }

    /**
     * Gets the largest possible minimum value that the field can take using
     * the specified calendrical information to refine the accuracy of the response.
     *
     * @param calendricalContext  context datetime, null returns getLargestMinimumValue()
     * @return the largest possible minimum value of the field given the context
     */
    public int getLargestMinimumValue(Calendrical calendricalContext) {
        if (calendricalContext == null) {
            return getLargestMinimumValue();
        }
        return getMinimumValue(calendricalContext);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the maximum value that the field can take.
     *
     * @return the maximum value for this field
     */
    public int getMaximumValue() {
        return maximumValue;
    }

    /**
     * Gets the smallest possible maximum value that the field can take.
     *
     * @return the smallest possible maximum value for this field
     */
    public int getSmallestMaximumValue() {
        return getMaximumValue();
    }

    /**
     * Gets the maximum value that the field can take using the specified
     * calendrical information to refine the accuracy of the response.
     *
     * @param calendricalContext  context datetime, null returns getMaximumValue()
     * @return the maximum value of the field given the context
     */
    public int getMaximumValue(Calendrical calendricalContext) {
        return getMaximumValue();
    }

    /**
     * Gets the smallest possible maximum value that the field can take using
     * the specified calendrical information to refine the accuracy of the response.
     *
     * @param calendricalContext  context datetime, null returns getSmallestMaximumValue()
     * @return the smallest possible maximum value of the field given the context
     */
    public int getSmallestMaximumValue(Calendrical calendricalContext) {
        if (calendricalContext == null) {
            return getSmallestMaximumValue();
        }
        return getMaximumValue(calendricalContext);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this TimeFieldRule to another based on the duration unit
     * followed by the duration range.
     * <p>
     * The duration unit is compared first, so MinuteOfHour will be less than
     * HourOfDay, which will be less than DayOfWeek. When the duration unit is
     * the same, the duration range is compared, so DayOfWeek is less than
     * DayOfMonth, which is less than DayOfYear.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, postive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(TimeFieldRule other) {
        int cmp = this.getDurationUnit().compareTo(other.getDurationUnit());
        if (cmp != 0) {
            return cmp;
        }
        return this.getDurationRange().compareTo(other.getDurationRange());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the rule.
     *
     * @return a description of the rule
     */
    @Override
    public String toString() {
        return getName();
    }

}
