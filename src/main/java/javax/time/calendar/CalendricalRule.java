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

import java.util.Comparator;
import java.util.List;

import javax.time.CalendricalException;

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
 * All instantiable subclasses must be final, immutable and thread-safe.
 * Subclasses should implement {@code equals}, {@code hashCode} and {@code Serializable}.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 * 
 * @param <T> the underlying type representing the data, typically a {@code Calendrical},
 *  {@code Number} or {@code Enum}, must be immutable, should be comparable
 */
public abstract class CalendricalRule<T>
        implements Comparator<Calendrical> {

    /** The reified class for the generic type. */
    private final Class<T> type;
    /** The name of the rule, not null. */
    private final String name;

    /**
     * Dummy constructor used in deserialization.
     */
    protected CalendricalRule() {
        // TODO: remove constructor if possible
        type = null;
        name = null;
    }

    /**
     * Constructor used to create a rule.
     *
     * @param type  the reified class, not null
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
     * @throws CalendricalException if the rule cannot be extracted
     */
    public final T getValueChecked(Calendrical calendrical) {
        T value = getValue(calendrical);
        if (value == null) {
            throw new CalendricalRuleException("Unable to obtain " + getName() + " from " + calendrical, this);
        }
        return value;
    }

    //-----------------------------------------------------------------------
    /**
     * Override point to affect the merging process.
     * <p>
     * This method is called during merging on the rule associated with each semi-normalized merger.
     * The implementation has the opportunity to adjust the mergers, potentially handling
     * any conflicts. For example, the {@code OffsetTime} rule could check to see if there
     * is an {@code OffsetDate} with a conflicting offset, and adjust the time accordingly.
     * 
     * @param merger  the merger that was created from an instance of the object associated with this rule, not null
     * @param mergers  all the mergers being processed, unmodifiable, but containing modifiable mergers, not null
     */
    protected void merge(CalendricalNormalizer merger, List<CalendricalNormalizer> mergers) {
        // override to alter the merge process
    }

    /**
     * Override point to derive the value for this rule from the merger.
     * <p>
     * This is part of the merge process, which exists to extract the maximum
     * information possible from a set calendrical data. Before this method is
     * called, the merger will be normalized, which ensures that any fields that can be
     * converted to objects will have been. Thus this method is primarily used
     * to create objects from the normalized form.
     * <p>
     * A typical implementation will check the objects and determine if the value can be
     * derived from them. For example, a {@code LocalDateTime} can be derived from a
     * {@code LocalDate} and a {@code LocalTime}.
     * In general, only the objects should be used for derivation, as derivation from any
     * remaining fields is handled directly by the merger.
     * <p>
     * Implementations should avoid throwing exceptions and use the merger error mechanism instead.
     * It is strongly recommended to treat the data in the merger as immutable.
     * <p>
     * This implementation uses {@link CalendricalNormalizer#getFieldDerived}
     * 
     * @param merger  the merger to process, not null
     * @return the derived field, null if unable to derive
     */
    protected T deriveFrom(CalendricalNormalizer merger) {
        return null;  // override to derive the value from the normalized form
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
     * Outputs this rule as a {@code String}, using the name.
     *
     * @return a string representation of this rule, not null
     */
    @Override
    public String toString() {
        return name;
    }

}
