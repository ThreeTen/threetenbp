/*
 * Copyright (c) 2009-2011 Stephen Colebourne & Michael Nascimento Santos
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
import java.util.List;

/**
 * A rule defining how a single well-defined calendrical element operates.
 * <p>
 * Calendrical rules may define fields like day-of-month, combinations like date-time,
 * or other related types like time-zone.
 * <p>
 * Each rule uses an underlying type to represent the data.
 * This is captured in the generic type of the rule.
 * The underlying type is reified and made available via {@link #getType()}.
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
        implements Comparator<Calendrical>, Serializable {

    /** A serialization identifier for this class. */
    private static final long serialVersionUID = 1L;

    /** The reified class for the generic type. */
    private final Class<T> type;
    /** The name of the rule, not null. */
    private final String name;

    /**
     * Constructor used to create a rule.
     *
     * @param reifiedClass  the reified class, not null
     * @param name  the name of the type, not null
     */
    protected CalendricalRule(Class<T> type, String name) {
        // avoid possible circular references by using inline NPE checks
        if (type == null) {
            throw new NullPointerException("Reified class must not be null");
        }
        if (name == null) {
            throw new NullPointerException("Name must not be null");
        }
        this.type = type;
        this.name = name;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the rule.
     * <p>
     * Implementations should use the name that best represents themselves.
     * If the rule represents a field, then the form 'UnitOfRange' should be used.
     * Otherwise, use the simple class name of the generic type, such as 'ZoneOffset'.
     * For debugging reasons, the name should be as unique as possible.
     *
     * @return the name of the rule, not null
     */
    public final String getName() {
        return name;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the type of the rule, which is a reification of the generic type.
     * <p>
     * Each rule uses an underlying type to represent the data.
     * This is captured in the generic type of the rule.
     * Since the generic implementation is Java is limited to the compiler, the
     * underlying type has been reified and made available through this method.
     * It is expected, but not enforced, that the underlying type is {@link Comparable}.
     *
     * @return the reified type of values of the rule, not null
     */
    public final Class<T> getType() {
        return type;
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
        if (type.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("Value of type " + value.getClass().getName() + " cannot be cast to type " + type.getName());
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
     * @return the value of the field, not null
     * @throws UnsupportedRuleException if the rule cannot be extracted
     */
    public final T getValueChecked(Calendrical calendrical) {
        T value = getValue(calendrical);
        if (value == null) {
            throw new UnsupportedRuleException(this);
        }
        return value;
    }

//    /**
//     * Derives the value of this rule from a calendrical.
//     * <p>
//     * This method derives the value for this field from other fields in the calendrical
//     * without directly querying the calendrical for the value.
//     * <p>
//     * For example, if this field is quarter-of-year, then the value can be derived
//     * from month-of-year.
//     * <p>
//     * The implementation only needs to derive the value based on its immediate parents.
//     * The use of {@link Calendrical#get} will extract any further parents on demand.
//     * <p>
//     * A typical implementation of this method obtains the parent value and performs a calculation.
//     * For example, here is a simple implementation for the quarter-of-year field:
//     * <pre>
//     * Integer moyVal = calendrical.get(ISODateTimeRule.MONTH_OF_YEAR);
//     * return (moyVal != null ? ((moyVal - 1) % 4) + 1) : null;
//     * </pre>
//     * <p>
//     * This method is designed to be overridden in subclasses.
//     * The subclass implementation must be thread-safe.
//     * The subclass implementation must not request the value of this rule from
//     * the specified calendrical, otherwise a stack overflow error will occur.
//     *
//     * @param calendrical  the calendrical to derive from, not null
//     * @return the derived value, null if unable to derive
//     */

//    /**
//     * Merges this field with other fields to form higher level fields.
//     * <p>
//     * The aim of this method is to assist in the process of extracting the most
//     * date-time information possible from a map of field-value pairs.
//     * The merging process is controlled by the mutable merger instance and
//     * the input and output of the this merge are held there.
//     * <p>
//     * Subclasses that override this method may use methods on the merger to
//     * obtain the values to merge. The value is guaranteed to be available for
//     * this field if this method is called.
//     * <p>
//     * If the override successfully merged some fields then the following must be performed.
//     * The merged field must be stored using {@link CalendricalMerger#storeMerged}.
//     * Each field used in the merge must be marked as being used by calling
//     * {@link CalendricalMerger#removeProcessed}.
//     * <p>
//     * An example to merge two fields into one - hour of AM/PM and AM/PM:
//     * <pre>
//     *  Integer hapVal = merger.getValue(ISODateTimeRule.HOUR_OF_AMPM);
//     *  if (hapVal != null) {
//     *    AmPmOfDay amPm = merger.getValue(this);
//     *    int hourOfDay = MathUtils.safeAdd(MathUtils.safeMultiply(amPm, 12), hapVal);
//     *    merger.storeMerged(ISODateTimeRule.HOUR_OF_DAY, hourOfDay);
//     *    merger.removeProcessed(this);
//     *    merger.removeProcessed(ISODateTimeRule.HOUR_OF_AMPM);
//     *  }
//     * </pre>
//     *
//     * @param merger  the merger instance controlling the merge process, not null
//     */


    public void merge(CalendricalNormalizer merger, List<CalendricalNormalizer> mergers) {
    }

    protected T deriveFrom(CalendricalNormalizer merger) {
        return null;
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
     *  Collections.sort(list, ISODateTimeRule.YEAR);
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
    @SuppressWarnings({"unchecked", "rawtypes" })
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
        if (obj instanceof CalendricalRule<?>) {
            CalendricalRule<?> other = (CalendricalRule<?>) obj;
            return getClass() == other.getClass() && name.equals(other.getName());
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
        return name.hashCode() + 7;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this rule as a {@code String}, using the name.
     *
     * @return a string representation of this rule, not null
     */
    @Override
    public String toString() {
        return name;
    }

}
