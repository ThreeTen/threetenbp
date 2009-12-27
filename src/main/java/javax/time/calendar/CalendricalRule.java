/*
 * Copyright (c) 2009 Stephen Colebourne & Michael Nascimento Santos
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
import javax.time.calendar.field.Year;

/**
 * The rule defining how a single well-defined calendrical operates.
 * <p>
 * Calendrical rules may be fields like day of month, or combinations like date-time.
 * <p>
 * CalendricalRule is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe and must
 * ensure serialization works correctly.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class CalendricalRule<T> implements Comparable<CalendricalRule<T>>, Serializable {

    /** A serialization identifier for this class. */
    private static final long serialVersionUID = 36518675L;

    /** The reified class for the generic type. */
    private final Class<T> reified;
    /** The chronology of the rule, not null. */
    private final Chronology chronology;
    /** The id of the rule, not null. */
    private final String id;
    /** The name of the rule, not null. */
    private final String name;

    /**
     * Constructor used to create a rule.
     *
     * @param reifiedClass  the reified class, not null
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     */
    protected CalendricalRule(
            Class<T> reifiedClass,
            Chronology chronology,
            String name) {
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
        this.reified = reifiedClass;
        this.chronology = chronology;
        this.id = chronology.getName() + '.' + name;
        this.name = name;
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
     * Gets the id of the rule.
     * <p>
     * The id is of the form 'ChronologyName.RuleName'.
     * No two fields should have the same id.
     *
     * @return the id of the rule, never null
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
     * Gets the reified type of values of the rule.
     *
     * @return the reified type of values of the rule, never null
     */
    public final Class<T> getReifiedType() {
        return reified;
    }

    /**
     * Returns the input value cast to the correct generic type.
     * <p>
     * The generics implementation in Java is limited to the compiler.
     * This method is needed to assist the compiler in certain circumstances.
     * The implementation simply returns the input value typed as the generic type.
     *
     * @param value  the value to reify, may be null
     * @return the type-cast input value, may be null
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
     * Gets the value of this rule from the specified calendrical.
     * <p>
     * This method queries the calendrical to determine if it provides the rule
     * value directly. If not, an attempt is made to {@link #deriveValue derive}
     * the value from the value of other fields in the map.
     *
     * @param calendrical  the calendrical to get the field value from, not null
     * @return the value of the field, null if unable to extract the field
     */
    public final T getValueQuiet(Calendrical calendrical) {
        ISOChronology.checkNotNull(calendrical, "Calendrical must not be null");
        return calendrical.get(this);
    }

    /**
     * Gets the value of the rule from the specified calendrical throwing
     * an exception if the rule cannot be returned.
     *
     * @param calendrical  the calendrical to get the field value from, not null
     * @return the value of the field, never null
     * @throws UnsupportedRuleException if the rule cannot be extracted
     */
    public final T getValue(Calendrical calendrical) {
        T value = getValueQuiet(calendrical);
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
     * calendrical implementation, not the rule passed into the <code>get</code> method.
     * <pre>
     *   public <T> T get(CalendricalRule<T> rule) {
     *     return IMPLEMENTATION_RULE.deriveValueFor(rule, this, this);
     *   }
     * </pre>
     * The last parameter in the code snippet above is always <code>this</code>, however
     * the second parameter may be a different representation, for example in {@link Year#get}.
     * <p>
     * If this rule and the specified rule are the same, then the value is returned.
     * Otherwise, an attempt is made to {@link #deriveValue derive} the field value.
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
        return rule.deriveValue(calendrical);
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
        return deriveValue(calendrical);
    }

    /**
     * Derives the value of this rule from a calendrical.
     * <p>
     * This method derives the value for this field from other fields in the calendrical
     * without directly querying the calendrical for the value.
     * <p>
     * For example, if this field is QuarterOfYear, then the value can be derived
     * from MonthOfYear. The implementation must not check to see of the map
     * already contains a value for QuarterOfYear.
     * <p>
     * The derivation can be recursive depending on the hierarchy of fields.
     * This is achieved by using {@link #getValueQuiet} to obtain the parent field rule.
     * <p>
     * A typical implementation of this method obtains the parent value and performs a calculation.
     * For example, here is a simple implementation for the QuarterOfYear field
     * (which doesn't handle negative numbers or leniency):
     * <pre>
     * Integer moyVal = ISOChronology.monthOfYearRule().getValueQuiet(fieldValueMap);
     * return (moyVal == null ? null : ((moyVal - 1) % 4) + 1);
     * </pre>
     * Extracts the value for this field using information in the field map.
     * <p>
     * This method is designed to be overridden in subclasses.
     * The subclass implementation must be thread-safe.
     *
     * @param calendrical  the calendrical to derive from, not null
     * @return the derived value, null if unable to derive
     */
    protected T deriveValue(Calendrical calendrical) {
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
    final T interpret(CalendricalMerger merger, Object value) {
        if (reified.isInstance(value)) {
            return reify(value);
        }
        T result = interpretValue(merger, value);
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
     */
    protected T interpretValue(CalendricalMerger merger, Object value) {
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
     * Compares this DateTimeFieldRule to another based on the period unit
     * followed by the period range followed by the chronology name.
     * <p>
     * The period unit is compared first, so MinuteOfHour will be less than
     * HourOfDay, which will be less than DayOfWeek. When the period unit is
     * the same, the period range is compared, so DayOfWeek is less than
     * DayOfMonth, which is less than DayOfYear. Finally, the chronology name
     * is compared.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, positive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(CalendricalRule<T> other) {
//        int cmp = this.getPeriodUnit().compareTo(other.getPeriodUnit());
//        if (cmp != 0) {
//            return cmp;
//        }
//        if (this.getPeriodRange() == other.getPeriodRange()) {
//            return chronology.getName().compareTo(other.chronology.getName());
//        }
//        if (this.getPeriodRange() == null) {
//            return 1;
//        }
//        if (other.getPeriodRange() == null) {
//            return -1;
//        }
//        cmp = this.getPeriodRange().compareTo(other.getPeriodRange());
//        if (cmp != 0) {
//            return cmp;
//        }
        return chronology.getName().compareTo(other.chronology.getName());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the rule.
     *
     * @return a description of the rule
     */
    @Override
    public String toString() {
        return id;
    }

}
