/*
 * Copyright (c) 2009-2010 Stephen Colebourne & Michael Nascimento Santos
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
import java.util.Comparator;

import javax.time.CalendricalException;

/**
 * A rule defining how a single well-defined calendrical element operates.
 * <p>
 * Calendrical rules may define fields like day-of-month, combinations like date-time,
 * or other related types like time-zone.
 * <p>
 * Each rule uses an underlying type to represent the data.
 * This is captured in the generic type of the rule.
 * The underlying type is reified and made available via {@link #getReifiedType()}.
 * It is expected, but not enforced, that the underlying type is {@link Comparable}.
 * <p>
 * CalendricalRule is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe and must
 * ensure serialization works correctly.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 * 
 * @param <T> the underlying type representing the data, typically a {@code Calendrical},
 *  {@code Number} or {@code Enum}, must be immutable, should be comparable
 */
public abstract class CalendricalRule<T>
        implements Comparable<CalendricalRule<T>>, Comparator<Calendrical>, Serializable {

    /** A serialization identifier for this class. */
    private static final long serialVersionUID = 1L;

    /** The reified class for the generic type. */
    private final Class<T> reified;
    /** The chronology of the rule, not null. */
    private final Chronology chronology;
    /** The id of the rule, not null. */
    private final String id;
    /** The name of the rule, not null. */
    private final String name;
    /** The period unit, not null. */
    private final PeriodUnit periodUnit;
    /** The period range, not null. */
    private final PeriodUnit periodRange;

    /**
     * Constructor used to create a rule.
     *
     * @param reifiedClass  the reified class, not null
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param periodUnit  the period unit, may be null
     * @param periodRange  the period range, may be null
     */
    protected CalendricalRule(
            Class<T> reifiedClass,
            Chronology chronology,
            String name,
            PeriodUnit periodUnit,
            PeriodUnit periodRange) {
        // avoid possible circular references by using inline NPE checks
        if (reifiedClass == null) {
            throw new NullPointerException("Reified class must not be null");
        }
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        if (name == null) {
            throw new NullPointerException("Name must not be null");
        }
        if (periodUnit == null && periodRange != null) {
            throw new NullPointerException("Peiod unit must not be null if period range is non-null");
        }
        this.reified = reifiedClass;
        this.chronology = chronology;
        this.id = chronology.getName() + '.' + name;
        this.name = name;
        this.periodUnit = periodUnit;
        this.periodRange = periodRange;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of the rule.
     *
     * @return the chronology of the rule, never null
     */
    public final Chronology getChronology() {
        return chronology;
    }

    /**
     * Gets the ID of the rule.
     * <p>
     * The ID is of the form 'ChronologyName.RuleName'.
     * No two fields should have the same ID.
     *
     * @return the ID of the rule, never null
     */
    public final String getID() {
        return id;
    }

    /**
     * Gets the name of the rule.
     * <p>
     * Implementations should use the name that best represents themselves.
     * If the rule represents a field, then the form 'UnitOfRange' should be used.
     * Otherwise, use the simple class name of the generic type, such as 'ZoneOffset'.
     *
     * @return the name of the rule, never null
     */
    public final String getName() {
        return name;
    }

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
    public PeriodUnit getPeriodUnit() {
        return periodUnit;
    }

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
    public PeriodUnit getPeriodRange() {
        return periodRange;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the reified class representing the underlying type of the rule.
     * <p>
     * Each rule uses an underlying type to represent the data.
     * This is captured in the generic type of the rule.
     * Since the generic implementation is Java is limited to the compiler, the
     * underlying type has been reified and made available through this method.
     * It is expected, but not enforced, that the underlying type is {@link Comparable}.
     *
     * @return the reified type of values of the rule, never null
     */
    public final Class<T> getReifiedType() {
        return reified;
    }

    /**
     * Returns the input value cast to the correct generic type.
     * <p>
     * Each rule uses an underlying type to represent the data.
     * This is captured in the generic type of the rule.
     * Since the generic implementation is Java is limited to the compiler, the
     * underlying type has been reified which allows this method to validate
     * the generic type fully. The implementation simply returns the input value
     * typed as the generic type.
     *
     * @param value  the value to reify, may be null
     * @return the type-cast input value, may be null
     * @throws ClassCastException if the value is not of the reified type
     */
    @SuppressWarnings("unchecked")
    public final T reify(Object value) {
        if (value == null) {
            return null;
        }
        if (reified.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("Value of type " + value.getClass().getName() + " cannot be cast to type " + reified.getName());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of this rule from the specified calendrical returning
     * {@code null} if the value cannot be returned.
     * <p>
     * This method simply queries the calendrical.
     *
     * @param calendrical  the calendrical to get the field value from, not null
     * @return the value of the field, null if unable to extract the field
     */
    public final T getValue(Calendrical calendrical) {
        ISOChronology.checkNotNull(calendrical, "Calendrical must not be null");
        return calendrical.get(this);
    }

    /**
     * Gets the value of the rule from the specified calendrical throwing
     * an exception if the rule cannot be returned.
     * <p>
     * This convenience method uses {@link #getValue(Calendrical)} to find the value
     * and then ensures it isn't {@code null}.
     *
     * @param calendrical  the calendrical to get the field value from, not null
     * @return the value of the field, never null
     * @throws UnsupportedRuleException if the rule cannot be extracted
     */
    public final T getValueChecked(Calendrical calendrical) {
        T value = getValue(calendrical);
        if (value == null) {
            throw new UnsupportedRuleException(this);
        }
        return value;
    }

    /**
     * Derives the value of the specified rule from a calendrical.
     * <p>
     * This method is provided for implementations of {@link Calendrical#get}
     * and is rarely called directly by application code. It is used when the
     * calendrical has its own rule, and this method is called on the rule of the
     * calendrical implementation, not the rule passed into the {@code get} method.
     * <pre>
     *   public <T> T get(CalendricalRule<T> rule) {
     *     return IMPLEMENTATION_RULE.deriveValueFor(rule, this, this);
     *   }
     * </pre>
     * The last parameter in the code snippet above is always {@code this}, however
     * the second parameter may be a different representation, for example in {@link Year#get}.
     * <p>
     * If this rule and the specified rule are the same, then the value is returned.
     * Otherwise, an attempt is made to {@link #derive} the field value.
     *
     * @param rule  the rule to retrieve, not null
     * @param value  the value to return if this rule is the specified rule, not null
     * @param calendrical  the calendrical to get the value from, not null
     * @return the value, null if unable to derive the value
     */
    public final <R> R deriveValueFor(CalendricalRule<R> rule, T value, Calendrical calendrical) {
        ISOChronology.checkNotNull(rule, "CalendricalRule must not be null");
        ISOChronology.checkNotNull(value, "Value must not be null");
        ISOChronology.checkNotNull(calendrical, "Calendrical must not be null");
        if (rule.equals(this)) {
            return rule.reify(value);
        }
        return rule.derive(calendrical);
    }

    /**
     * Derives the value of this rule from a calendrical.
     * <p>
     * This method is provided for implementations of {@link Calendrical#get}
     * and is rarely called directly by application code. It is used when the
     * calendrical has its own values but does not have its own rule.
     * <pre>
     *   public <T> T get(CalendricalRule<T> rule) {
     *     // return data, for example
     *     if (rule.equals(...)) {
     *       return valueForRule;
     *     }
     *     // call this method
     *     return rule.deriveValueFrom(this);
     *   }
     * </pre>
     *
     * @param calendrical  the calendrical to get the value from, not null
     * @return the value, null if unable to derive the value
     */
    public final T deriveValueFrom(Calendrical calendrical) {
        ISOChronology.checkNotNull(calendrical, "Calendrical must not be null");
        return derive(calendrical);
    }

    /**
     * Derives the value of this rule from a calendrical.
     * <p>
     * This method derives the value for this field from other fields in the calendrical
     * without directly querying the calendrical for the value.
     * <p>
     * For example, if this field is quarter-of-year, then the value can be derived
     * from month-of-year.
     * <p>
     * The implementation only needs to derive the value based on its immediate parents.
     * The use of {@link Calendrical#get} will extract any further parents on demand.
     * <p>
     * A typical implementation of this method obtains the parent value and performs a calculation.
     * For example, here is a simple implementation for the quarter-of-year field:
     * <pre>
     * Integer moyVal = calendrical.get(ISOChronology.monthOfYearRule());
     * return (moyVal != null ? ((moyVal - 1) % 4) + 1) : null;
     * </pre>
     * <p>
     * This method is designed to be overridden in subclasses.
     * The subclass implementation must be thread-safe.
     * The subclass implementation must not request the value of this rule from
     * the specified calendrical, otherwise a stack overflow error will occur.
     *
     * @param calendrical  the calendrical to derive from, not null
     * @return the derived value, null if unable to derive
     */
    protected T derive(Calendrical calendrical) {
        return null;  // do nothing - override if this field can derive
    }

    //-----------------------------------------------------------------------
    /**
     * Interprets the specified value converting it into an in range value of the
     * correct type for this rule.
     *
     * @param merger  the merger instance controlling the merge process, not null
     * @param value  the value to interpret, not null
     */
    final T interpretValue(CalendricalMerger merger, Object value) {
        if (reified.isInstance(value)) {
            return reify(value);
        }
        T result = interpret(merger, value);
        if (result != null) {
            return result;
        }
        throw new CalendricalException("Unable to complete merge as input contains an unknown type " +
                " for rule '" + getName() + "': " + value.getClass().getName());
    }

    /**
     * Interprets the specified value converting it into an in range value of the
     * correct type for this rule.
     * <p>
     * Before this method is called, the value will be checked to ensure it is not of
     * the type of this rule.
     *
     * @param merger  the merger instance controlling the merge process, not null
     * @param value  the value to interpret, null if unable to interpret the value
     * @return the interpreted value
     */
    protected T interpret(CalendricalMerger merger, Object value) {
        return null;
    }

    /**
     * Merges this field with other fields to form higher level fields.
     * <p>
     * The aim of this method is to assist in the process of extracting the most
     * date-time information possible from a map of field-value pairs.
     * The merging process is controlled by the mutable merger instance and
     * the input and output of the this merge are held there.
     * <p>
     * Subclasses that override this method may use methods on the merger to
     * obtain the values to merge. The value is guaranteed to be available for
     * this field if this method is called.
     * <p>
     * If the override successfully merged some fields then the following must be performed.
     * The merged field must be stored using {@link CalendricalMerger#storeMerged}.
     * Each field used in the merge must be marked as being used by calling
     * {@link CalendricalMerger#removeProcessed}.
     * <p>
     * An example to merge two fields into one - hour of AM/PM and AM/PM:
     * <pre>
     *  Integer hapVal = merger.getValue(ISOChronology.hourOfAmPmRule());
     *  if (hapVal != null) {
     *    AmPmOfDay amPm = merger.getValue(this);
     *    int hourOfDay = MathUtils.safeAdd(MathUtils.safeMultiply(amPm, 12), hapVal);
     *    merger.storeMerged(ISOChronology.hourOfDayRule(), hourOfDay);
     *    merger.removeProcessed(this);
     *    merger.removeProcessed(ISOChronology.hourOfAmPmRule());
     *  }
     * </pre>
     *
     * @param merger  the merger instance controlling the merge process, not null
     */
    protected void merge(CalendricalMerger merger) {
        // do nothing - override if this rule can merge data to a more significant rule
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two {@code Calendrical} implementations based on the value
     * of this rule extracted from each calendrical.
     * <p>
     * This implements the {@link Comparator} interface and allows any two
     * {@code Calendrical} implementations to be compared using this rule.
     * The comparison is based on the result of calling {@link Calendrical#get}
     * on each calendrical, and comparing those values.
     * <p>
     * For example, to sort a list into year order when the list may contain any
     * mixture of calendricals, such as a {@code LocalDate}, {@code YearMonth}
     * and {@code ZonedDateTime}:
     * <pre>
     *  List<Calendrical> list = ...
     *  Collections.sort(list, ISOChronology.yearRule());
     * </pre>
     * If the value of this rule cannot be obtained from a calendrical, then
     * an exception is thrown.
     * <p>
     * If the underlying type of this rule does not implement {@link Comparable}
     * then an exception will be thrown.
     *
     * @param cal1  the first calendrical to compare, not null
     * @param cal2  the second calendrical to compare, not null
     * @return the comparator result, negative if first is less, positive if first is greater, zero if equal
     * @throws NullPointerException if either input is null
     * @throws ClassCastException if this rule has a type that is not comparable
     * @throws IllegalArgumentException if this rule cannot be extracted from either input parameter
     */
    @SuppressWarnings("unchecked")
    public int compare(Calendrical cal1, Calendrical cal2) {
        Comparable value1 = (Comparable) cal1.get(this);
        Comparable value2 = (Comparable) cal2.get(this);
        if (value1 == null || value2 == null) {
            throw new IllegalArgumentException("Unable to compare as Calendrical does not provide rule: " + getName());
        }
        return value1.compareTo(value2);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this {@code CalendricalRule} to another.
     * <p>
     * The comparison is based on the period unit followed by the period range
     * followed by the rule ID.
     * The period unit is compared first, so MinuteOfHour will be less than
     * HourOfDay, which will be less than DayOfWeek. When the period unit is
     * the same, the period range is compared, so DayOfWeek is less than
     * DayOfMonth, which is less than DayOfYear. Finally, the rule ID is compared.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(CalendricalRule<T> other) {
        if (this.getPeriodUnit() == null) {
            if (other.getPeriodUnit() == null) {
                return getID().compareTo(other.getID());
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
                return getID().compareTo(other.getID());
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
        return getID().compareTo(other.getID());
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two rules based on their ID.
     *
     * @return true if the rules are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return id.equals(((CalendricalRule<?>) obj).id);
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a hash code based on the ID.
     *
     * @return a description of the rule
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the rule.
     *
     * @return a description of the rule, never null
     */
    @Override
    public String toString() {
        return id;
    }

}
